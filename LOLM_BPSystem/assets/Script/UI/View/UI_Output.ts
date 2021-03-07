import Manager, { ProtocolName } from "../../public/Manager";
import BanPickIcon from "../BanPickIcon";
import IUIView from "./IUIView";
import { ELinkRoomData, ESyncData, LinkRoomData } from "./UI_BP";

const { ccclass, property } = cc._decorator;

@ccclass
export default class UI_Output extends IUIView {
    public static Inst: UI_Output = null;
    @property([cc.Prefab])
    private BanIcon: cc.Prefab[] = [];
    @property([cc.Layout])
    private BanParent: cc.Layout[] = [];
    @property([BanPickIcon])
    private AllPickIcon: BanPickIcon[] = [];
    private AllBanIcon: BanPickIcon[] = [];

    private isInit: boolean = false;
    private sizeL: number[] = [0, 170, 85, 12, 5, 5];
    private sizeX: number[] = [0, 0, 100, 80, 35, 10];
    public Open() {
        super.Open();
        UI_Output.Inst = this;
        Manager.Inst.GetNetwork().AddCallBack(ProtocolName.LINK_ROOM, this.GetLinkCallBack);
        Manager.Inst.GetNetwork().AddCallBack(ProtocolName.SYNC, this.SyncCallBack);
        let url = new URL(window.location.href);
        let linkData: LinkRoomData = new LinkRoomData();
        linkData.key = url.searchParams.get("key");
        linkData.team = -1;
        Manager.Inst.GetNetwork().Send(linkData);
    }
    public Close() {
        super.Close();
    }
    private GetLinkCallBack(data: string) {
        let jdata: ELinkRoomData = JSON.parse(data);
        if (jdata.resCode === 0) {
            UI_Output.Inst.BanParent[0].spacingX = UI_Output.Inst.sizeX[jdata.info.banCount];
            UI_Output.Inst.BanParent[0].paddingLeft = UI_Output.Inst.sizeL[jdata.info.banCount];
            UI_Output.Inst.BanParent[1].spacingX = UI_Output.Inst.sizeX[jdata.info.banCount];
            UI_Output.Inst.BanParent[1].paddingRight = UI_Output.Inst.sizeL[jdata.info.banCount];
            for (let i = 0; i < jdata.info.banCount; i++) {
                for (let j = 0; j < 2; j++) {
                    let newIcon = cc.instantiate(UI_Output.Inst.BanIcon[j]);
                    newIcon.parent = UI_Output.Inst.BanParent[j].node;
                    UI_Output.Inst.AllBanIcon.push(newIcon.getComponent(BanPickIcon));
                }
            }
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
            for (let i = 0; i < jdata.BanList.length; i++) {
                UI_Output.Inst.AllBanIcon[i].SetChoose(jdata.BanList[i]);
            }
            for (let i = 0; i < jdata.PickList.length; i++) {
                if (jdata.PickList[i] != -1) {
                    UI_Output.Inst.AllPickIcon[i].SetChoose(jdata.PickList[i]);
                }
            }
        }
    }
}
