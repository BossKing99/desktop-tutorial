import { HPData, MgData } from "../../MagicCardManager";
import Manager, { ProtocolName } from "../../public/Manager";
import BanPickIcon from "../BanPickIcon";
import OutputCardInfo from "../OutputCardInfo";
import IUIView from "./IUIView";
import { ELinkRoomData, ESyncData, GetLOLMData, LinkRoomData } from "./UI_BP";

const { ccclass, property } = cc._decorator;

@ccclass
export default class UI_Output extends IUIView {
    @property(cc.Label)
    private blueTeamLabel: cc.Label = null;
    @property(cc.Label)
    private redTeamLabel: cc.Label = null;
    @property(cc.Label)
    private ReadyLabel: cc.Label = null;
    @property(cc.Prefab)
    private infoPrefab: cc.Prefab = null;
    @property(cc.Node)
    private InfoPraent: cc.Node = null;

    public static Inst: UI_Output = null;
    private isInit: boolean = false;
    public AllMgData: MgData[] = []
    public AllInfo: OutputCardInfo[] = []
    private key: string

    public Open() {
        super.Open();
        UI_Output.Inst = this;
        Manager.Inst.GetNetwork().AddCallBack(ProtocolName.LINK_ROOM, this.GetLinkCallBack);
        Manager.Inst.GetNetwork().AddCallBack(ProtocolName.SYNC, this.SyncCallBack);
        Manager.Inst.GetNetwork().AddCallBack(ProtocolName.GET_DATA, this.GetLOLMDataCallBack); //OK
        Manager.Inst.GetNetwork().Send(new GetLOLMData());
        let url = new URL(window.location.href);
        UI_Output.Inst.key = url.searchParams.get("key");
    }
    public Close() {
        super.Close();
    }
    private GetLinkCallBack(data: string) {
        let jdata: ELinkRoomData = JSON.parse(data);
        if (jdata.resCode === 0) {
            UI_Output.Inst.blueTeamLabel.string = jdata.info.blue;
            UI_Output.Inst.redTeamLabel.string = jdata.info.red;
            UI_Output.Inst.ReadyLabel.string = jdata.info.game;

            for (let index = 0; index < jdata.info.composeCount - 1; index++) {
                let newInfo = cc.instantiate(UI_Output.Inst.infoPrefab)
                newInfo.parent = UI_Output.Inst.InfoPraent;
                UI_Output.Inst.AllInfo[index] =  newInfo.getComponent(OutputCardInfo)
                UI_Output.Inst.AllInfo[index].SetName(index)
            }
            UI_Output.Inst.InfoPraent.setContentSize(1250, (jdata.info.composeCount - 1) * 300);
            UI_Output.Inst.isInit = true;
        }
        else if (jdata.resCode === 1) {
            window.alert("找不到房間");
            let url = new URL(Manager.Inst.GetOriginURL());
            url.searchParams.set("page", "0");
            window.location.href = url.toString();
        }
    }
    private async SyncCallBack(data: string) {
        while (!UI_Output.Inst.isInit)
            await Manager.Inst.Wait(100);

        let jdata: ESyncData = JSON.parse(data);
        if (jdata.Status === "END") {
            let bf = 0;
            let rf = 0;
            for (let index = 0; index < jdata.Compose.buleCompose.length; index++) {

                if (bf != jdata.BanList[1]) {
                    UI_Output.Inst.AllInfo[bf].SetBuleInfo(jdata.Compose.buleCompose[bf])
                    bf++;
                }
                if (rf != jdata.BanList[0]) {
                    UI_Output.Inst.AllInfo[rf].SetRedInfo(jdata.Compose.redCompose[rf])
                    rf++;
                }
            }
        }
    }
    private GetLOLMDataCallBack(data: string) {
        let jdata: HPData = JSON.parse(data);
        let allMgData: MgData[] = [];
        for (let index = 0; index < jdata.ChampoinTable.length; index++) {
            allMgData[jdata.ChampoinTable[index].no] = jdata.ChampoinTable[index];
        }
        UI_Output.Inst.AllMgData = allMgData;
        let linkData: LinkRoomData = new LinkRoomData();
        linkData.key = UI_Output.Inst.key;
        linkData.team = -1;
        Manager.Inst.GetNetwork().Send(linkData);
    }

}
