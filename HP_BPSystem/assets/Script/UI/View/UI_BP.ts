
import MagicCardManager, { HPData, MgData } from "../../MagicCardManager";
import GetTime from "../../public/GetTime";
import Manager, { ProtocolName } from "../../public/Manager";
import BanPickIcon from "../BanPickIcon";
import CardButton from "../CardButton";
import IUIView from "./IUIView";

const { ccclass, property } = cc._decorator;

@ccclass
export default class UI_BP extends IUIView {
    @property(cc.Label)
    private blueTeamLabel: cc.Label = null;
    @property(cc.Label)
    private redTeamLabel: cc.Label = null;
    @property(cc.Label)
    private ReadyLabel: cc.Label = null;
    @property(cc.Button)
    private ReadyButtonNode: cc.Button = null;
    @property(MagicCardManager)
    private CardManager: MagicCardManager = null;
    @property([cc.SpriteFrame])
    private TeamBoxSf: cc.SpriteFrame[] = [];
    @property([cc.Sprite])
    private TeamBox: cc.Sprite[] = [];
    @property([cc.Label])
    private TimeCount: cc.Label[] = [];
    @property(cc.Label)
    private RoomInfoLable: cc.Label;
    @property(cc.Node)
    private ObBox: cc.Node;
    @property(cc.Label)
    private OBText: cc.Label;
    @property(cc.Node)
    private ChooseBox: cc.Node;
    //------------------------------------------
    public static Inst: UI_BP = null;
    private myTeam: number = -1;
    private key: string
    private pass: string
    private SyncData: ESyncData;
    private AllBanIcon: BanPickIcon[] = [];
    private isInit: boolean = false;
    private AllChooseCard: CardButton[] = [];
    private NowChooseCard: number = 0;
    private AllMgData: MgData[] = []

    public Open() {
        super.Open();
        UI_BP.Inst = this;
        Manager.Inst.GetNetwork().AddCallBack(ProtocolName.LINK_ROOM, this.GetLinkCallBack); //OK
        Manager.Inst.GetNetwork().AddCallBack(ProtocolName.SYNC, this.SyncCallBack); //OK
        Manager.Inst.GetNetwork().AddCallBack(ProtocolName.PREVIEW, this.PreviewCallBack);
        Manager.Inst.GetNetwork().AddCallBack(ProtocolName.GET_DATA, this.GetLOLMDataCallBack); //OK
        Manager.Inst.GetNetwork().Send(new GetLOLMData());

        console.log(UI_BP.Inst.ChooseBox.childrenCount)
        let box = UI_BP.Inst.ChooseBox.children[0];
        for (let j = 0; j < box.childrenCount; j++) {
            UI_BP.Inst.AllChooseCard[j] = box.children[j].getComponent(CardButton)
            console.log(UI_BP.Inst.AllChooseCard)
        }

        let url = new URL(window.location.href);
        if (url.searchParams.has("team")) {
            this.myTeam = Number.parseInt(url.searchParams.get("team"));
            this.pass = url.searchParams.get("pass");
        }
        this.key = url.searchParams.get("key");
        this.ReadyButtonNode.node.active = url.searchParams.has("team");
    }
    public Close() {
        super.Close();
    }

    public CloseChoosebox() {
        if (this.SyncData.nowCtrl != 0 && this.SyncData.nowCtrl != 1)
            return
    }
    private GetLinkCallBack(data: string) {
        let jdata: ELinkRoomData = JSON.parse(data);
        if (jdata.resCode === 0) {
            UI_BP.Inst.blueTeamLabel.string = jdata.info.blue;
            UI_BP.Inst.redTeamLabel.string = jdata.info.red;
            UI_BP.Inst.Countdown();
            UI_BP.Inst.isInit = true;
        }
        else if (jdata.resCode === 1) {
            window.alert("找不到房間");
            let url = new URL(Manager.Inst.GetOriginURL());
            url.searchParams.set("page", "0");
            window.location.href = url.toString();
        }
    }
    private async SyncCallBack(data: string) {
        while (!UI_BP.Inst.isInit)
            await Manager.Inst.Wait(100);

        let jdata: ESyncData = JSON.parse(data);
        if (jdata.Status === "END") {
            let url = new URL(Manager.Inst.GetOriginURL());
            url.searchParams.set("page", "3");
            url.searchParams.set("key", UI_BP.Inst.key);
            window.location.href = url.toString();
        }
        else {
            UI_BP.Inst.SyncData = jdata
            switch (UI_BP.Inst.SyncData.Status) {
                case "WAIT":
                    UI_BP.Inst.RoomInfoLable.string = "準備階段";
                    // UI_BP.Inst.ChooseBox.active = false
                    if (UI_BP.Inst.myTeam != -1) {
                        UI_BP.Inst.ReadyLabel.string = jdata.Ready[UI_BP.Inst.myTeam] ? "準備完成" : "準備";
                        UI_BP.Inst.ReadyButtonNode.interactable = !jdata.Ready[UI_BP.Inst.myTeam];
                        UI_BP.Inst.ObBox.active = false;
                    } else {
                        UI_BP.Inst.ObBox.active = true;
                        UI_BP.Inst.OBText.string = "等待選手就位"
                    }
                    for (let i = 0; i < 2; i++)
                        UI_BP.Inst.TeamBox[i].spriteFrame = UI_BP.Inst.TeamBoxSf[i];
                    break;
                case "BAN":
                    for (let i = 0; i < UI_BP.Inst.SyncData.BanList.length; i++) {
                        if (UI_BP.Inst.SyncData.BanList[i] != -1) {
                            UI_BP.Inst.AllBanIcon[i].SetChoose(UI_BP.Inst.SyncData.BanList[i]);
                        }
                    }
                    if (UI_BP.Inst.SyncData.banFlage < UI_BP.Inst.AllBanIcon.length)
                        UI_BP.Inst.AllBanIcon[UI_BP.Inst.SyncData.banFlage].SetReady();
                    break;
                case "COMPOSE":
                    if (UI_BP.Inst.myTeam != -1) {
                        UI_BP.Inst.OBText.string = "等待選手組牌"
                    } else {
                        UI_BP.Inst.ChooseBox.active = true
                    }
                    break;
            }
            if (UI_BP.Inst.SyncData.Status == "BAN") {
                UI_BP.Inst.RoomInfoLable.string = "";
                UI_BP.Inst.ReadyLabel.string = UI_BP.Inst.SyncData.nowCtrl == UI_BP.Inst.myTeam ? "確認選擇" : "等待對手";
                UI_BP.Inst.ReadyButtonNode.interactable = UI_BP.Inst.SyncData.nowCtrl == UI_BP.Inst.myTeam;
                for (let i = 0; i < 2; i++) {
                    if (UI_BP.Inst.SyncData.nowCtrl == i) {
                        UI_BP.Inst.TeamBox[i].spriteFrame = UI_BP.Inst.TeamBoxSf[i];
                        UI_BP.Inst.NextTime[i] = UI_BP.Inst.SyncData.NextTime;
                    }
                    else {
                        UI_BP.Inst.TeamBox[i].spriteFrame = UI_BP.Inst.TeamBoxSf[2];
                        UI_BP.Inst.NextTime[i] = 0;
                    }
                }
            }
        }
        UI_BP.Inst.CloseChoosebox();
    }
    private NextTime: number[] = [0, 0];
    private async Countdown() {
        let t: number;
        while (true) {
            t = GetTime.GetTime();
            for (let i = 0; i < 2; i++) {
                if (t > UI_BP.Inst.NextTime[i])
                    UI_BP.Inst.TimeCount[i].string = "";
                else {
                    UI_BP.Inst.TimeCount[i].string = parseInt((UI_BP.Inst.NextTime[i] - t) / 1000 + "").toString();
                }
            }
            await Manager.Inst.Wait(100);
        }
    }
    private async PreviewCallBack(data: string) {
        while (!UI_BP.Inst.isInit)
            await Manager.Inst.Wait(100);

        let jdata: EPreviewRoomData = JSON.parse(data);
        switch (UI_BP.Inst.SyncData.Status) {
            case "BAN":
                UI_BP.Inst.AllBanIcon[UI_BP.Inst.SyncData.banFlage].SetPreView(jdata.preview);
                break;
        }

    }

    private GetLOLMDataCallBack(data: string) {
        let jdata: HPData = JSON.parse(data);
        UI_BP.Inst.AllMgData = UI_BP.Inst.CardManager.Init(jdata.ChampoinTable);
        let linkData: LinkRoomData = new LinkRoomData();
        linkData.key = UI_BP.Inst.key;
        linkData.team = UI_BP.Inst.myTeam;
        linkData.pass = UI_BP.Inst.pass;
        Manager.Inst.GetNetwork().Send(linkData);
    }

    public OnCheckButton() {
        switch (UI_BP.Inst.SyncData.Status) {
            case "WAIT":
                if (this.myTeam != -1 && !this.SyncData.Ready[this.myTeam]) {
                    let data: ReadyData = new ReadyData();
                    data.team = this.myTeam;
                    Manager.Inst.GetNetwork().Send(data);
                }
                break;
            case "BAN":
                break;
        }
    }

    public ShowChooseBox(cardNum: number, cardType: number) {
        UI_BP.Inst.NowChooseCard = cardNum
        UI_BP.Inst.CardManager.OpenChessBox(cardType)
    }

    public SetChooseCardData(cardNum: number) {
        UI_BP.Inst.AllChooseCard[UI_BP.Inst.NowChooseCard].Init(UI_BP.Inst.AllMgData[cardNum])
        UI_BP.Inst.CardManager.isChoose()
    }
}
export class LinkRoomData {
    public pt: number = ProtocolName.LINK_ROOM;
    public key: string = "";
    public team: number = -1;
    public pass: string = "";
}
export class ReadyData {
    public pt: number = ProtocolName.READY;
    public team: number;
}
export class GetLOLMData {
    public pt: number = ProtocolName.GET_DATA;
    public gameType: number = 2;
}
export class ChooseData {
    public pt: number = ProtocolName.CHOOSE;
    public choose: number;
}
export class ELinkRoomData {
    public resCode: number = -1;
    public info: RoomInfo;
}
export class PreviewData {
    public pt: number = ProtocolName.PREVIEW;
    public num: number;
}
export class EPreviewRoomData {
    public preview: number;
}
export class RoomInfo {
    public blue: string;
    public red: string;
    public game: string;
    public banCount: number;
}
export class ESyncData {
    public Status: string;
    public BanList: number[];
    public PickList: number[];
    public Ready: boolean[];
    public nowCtrl: number;
    public NextTime: number;
    public banFlage: number;
    public pickFlage: number;
}