

import MagicCardManager, { HPData, MgData } from "../../MagicCardManager";
import Network from "../../Network";
import GetTime from "../../public/GetTime";
import Manager, { ProtocolName } from "../../public/Manager";
import BanInfo from "../BanInfo";
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
    @property([BanInfo])
    private BanInfos: BanInfo[] = [];
    //------------------------------------------
    public static Inst: UI_BP = null;
    private myTeam: number = -1;
    private key: string
    private pass: string
    public SyncData: ESyncData;
    private AllBanIcon: BanPickIcon[] = [];
    private isInit: boolean = false;
    private AllChooseCard: CardButton[] = [];
    private NowChooseCard: number = 0;
    public AllMgData: MgData[] = []
    private ComposeGroup: number[][] = [];
    private nowGroup: number = 0
    private rinfo: RoomInfo;

    public Open() {
        super.Open();
        UI_BP.Inst = this;
        Manager.Inst.GetNetwork().AddCallBack(ProtocolName.LINK_ROOM, this.GetLinkCallBack); //OK
        Manager.Inst.GetNetwork().AddCallBack(ProtocolName.SYNC, this.SyncCallBack); //OK
        Manager.Inst.GetNetwork().AddCallBack(ProtocolName.PREVIEW, this.PreviewCallBack);
        Manager.Inst.GetNetwork().AddCallBack(ProtocolName.GET_DATA, this.GetLOLMDataCallBack); //OK
        Manager.Inst.GetNetwork().AddCallBack(ProtocolName.COMPOSE, this.rCompose); //OK
        Manager.Inst.GetNetwork().AddCallBack(ProtocolName.GET_COMPOST, this.rGetCompose); //OK
        Manager.Inst.GetNetwork().Send(new GetLOLMData());

        let box = UI_BP.Inst.ChooseBox.children[0];
        for (let j = 0; j < box.childrenCount; j++) {
            UI_BP.Inst.AllChooseCard[j] = box.children[j].getComponent(CardButton)
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
        if (this.SyncData.NowCtrl != 0 && this.SyncData.NowCtrl != 1)
            return
    }
    private GetLinkCallBack(data: string) {
        let jdata: ELinkRoomData = JSON.parse(data);
        if (jdata.resCode === 0) {
            UI_BP.Inst.Countdown();
            UI_BP.Inst.isInit = true;
            UI_BP.Inst.rinfo = jdata.info;
            for (let i = 0; i < 2; i++) {
                UI_BP.Inst.BanInfos[i].node.active = false
                UI_BP.Inst.TeamBox[i].spriteFrame = UI_BP.Inst.TeamBoxSf[UI_BP.Inst.myTeam == -1 ? i : 3];
            }

            if (UI_BP.Inst.myTeam == -1) {
                UI_BP.Inst.blueTeamLabel.string = UI_BP.Inst.rinfo.blue;
                UI_BP.Inst.redTeamLabel.string = UI_BP.Inst.rinfo.red;
            } else {
                UI_BP.Inst.blueTeamLabel.string = UI_BP.Inst.myTeam == 0 ? UI_BP.Inst.rinfo.blue : UI_BP.Inst.rinfo.red;
            }

            for (let i = 0; i < jdata.info.composeCount; i++) {
                UI_BP.Inst.ComposeGroup[i] = new Array(12)
                for (let j = 0; j < 12; j++) {
                    UI_BP.Inst.ComposeGroup[i][j] = 0
                }
            }
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
                    UI_BP.Inst.RoomInfoLable.string = UI_BP.Inst.rinfo.game;
                    UI_BP.Inst.ChooseBox.active = false
                    if (UI_BP.Inst.myTeam != -1) {
                        UI_BP.Inst.ReadyLabel.string = jdata.Ready[UI_BP.Inst.myTeam] ? "準備完成" : "準備";
                        UI_BP.Inst.ReadyButtonNode.interactable = !jdata.Ready[UI_BP.Inst.myTeam];
                        UI_BP.Inst.ObBox.active = false;
                        UI_BP.Inst.redTeamLabel.string = "準備階段";
                    } else {
                        UI_BP.Inst.ObBox.active = true;
                        UI_BP.Inst.OBText.string = "等待選手就位"
                    }

                    break;

                case "BAN":
                    for (let i = 0; i < 2; i++) {
                        UI_BP.Inst.BanInfos[i].node.active = true
                        UI_BP.Inst.TeamBox[i].spriteFrame = UI_BP.Inst.TeamBoxSf[i];
                        UI_BP.Inst.ReadyButtonNode.node.active = true;
                    }
                    UI_BP.Inst.ObBox.active = false;
                    UI_BP.Inst.CardManager.isChoose()
                    UI_BP.Inst.ChooseBox.active = true
                    UI_BP.Inst.RoomInfoLable.string = "";
                    UI_BP.Inst.ReadyLabel.string = UI_BP.Inst.SyncData.NowCtrl == UI_BP.Inst.myTeam ? "BAN" : "等待對手";
                    UI_BP.Inst.ReadyButtonNode.interactable = UI_BP.Inst.SyncData.NowCtrl == UI_BP.Inst.myTeam;
                    for (let i = 0; i < 2; i++) {
                        if (UI_BP.Inst.SyncData.NowCtrl == i) {
                            UI_BP.Inst.TeamBox[i].spriteFrame = UI_BP.Inst.TeamBoxSf[i];
                            UI_BP.Inst.NextTime[i] = UI_BP.Inst.SyncData.NextTime;
                        }
                        else {
                            UI_BP.Inst.TeamBox[i].spriteFrame = UI_BP.Inst.TeamBoxSf[2];
                            UI_BP.Inst.NextTime[i] = 0;
                        }
                    }

                    UI_BP.Inst.ComposeGroup = jdata.NowCtrl == 0 ? jdata.HideCompose.buleCompose : jdata.HideCompose.redCompose
                    UI_BP.Inst.nowGroup = 0;
                    UI_BP.Inst.SetGroup();

                    if (jdata.BanList) {
                        for (let i = 0; i < jdata.BanList.length; i++) {
                            UI_BP.Inst.BanInfos[i].SetInfo(i == 0 ? jdata.HideCompose.buleCompose[jdata.BanList[i]] : jdata.HideCompose.redCompose[jdata.BanList[i]])
                        }
                    }

                    break;

                case "COMPOSE":
                    UI_BP.Inst.NextTime[0] = UI_BP.Inst.SyncData.NextTime;
                    UI_BP.Inst.ReadyButtonNode.node.active = false;
                    if (UI_BP.Inst.myTeam == -1) {
                        UI_BP.Inst.OBText.string = "等待選手組牌"
                        UI_BP.Inst.ChooseBox.active = false
                    } else {
                        UI_BP.Inst.ChooseBox.active = true
                        UI_BP.Inst.redTeamLabel.string = "選卡階段";
                        let getCompose = new GetCompose;
                        getCompose.team = UI_BP.Inst.myTeam;
                        Manager.Inst.GetNetwork().Send(getCompose);
                    }
                    break;
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

    private SetGroup() {
        for (let i = 0; i < 12; i++) {
            this.AllChooseCard[i].Init(UI_BP.Inst.AllMgData[UI_BP.Inst.ComposeGroup[UI_BP.Inst.nowGroup][i]])
        }
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
                if (UI_BP.Inst.myTeam == UI_BP.Inst.SyncData.NowCtrl) {
                    let data: ChooseData = new ChooseData();
                    data.choose = UI_BP.Inst.nowGroup;
                    Manager.Inst.GetNetwork().Send(data);
                }
                break;
        }
    }

    public ShowChooseBox(cardNum: number, cardType: number) {
        UI_BP.Inst.NowChooseCard = cardNum
        UI_BP.Inst.CardManager.OpenChoosBox(cardType)
    }

    public SetChooseCardData(cardNum: number) {
        let compose: Compose = new Compose;
        compose.choose = cardNum;
        compose.group = UI_BP.Inst.nowGroup;
        compose.no = UI_BP.Inst.NowChooseCard;
        compose.team = UI_BP.Inst.myTeam;
        Manager.Inst.GetNetwork().Send(compose);
        UI_BP.Inst.CardManager.isChoose()
    }

    public SwitchGroup(event, next) {
        UI_BP.Inst.nowGroup += +next
        if (UI_BP.Inst.nowGroup < 0)
            UI_BP.Inst.nowGroup = UI_BP.Inst.rinfo.composeCount
        else if (UI_BP.Inst.nowGroup >= UI_BP.Inst.rinfo.composeCount)
            UI_BP.Inst.nowGroup = 0
        UI_BP.Inst.SetGroup()
    }

    public IsChoose(num: number): boolean {

        if (num == UI_BP.Inst.AllChooseCard[UI_BP.Inst.NowChooseCard].Id)
            return false;

        for (let i = 0; i < 12; i++) {
            if (UI_BP.Inst.ComposeGroup[UI_BP.Inst.nowGroup][i] == num) {
                return true;
            }
        }
        return false
    }

    private rCompose(data: string) {
        let jdata: Compose = JSON.parse(data);
        if (jdata.team == UI_BP.Inst.myTeam) {
            UI_BP.Inst.ComposeGroup[jdata.group][jdata.no] = jdata.choose;
        }
        UI_BP.Inst.SetGroup();
    }

    private rGetCompose(data: string) {
        let jdata: EGetCompose = JSON.parse(data);
        UI_BP.Inst.ComposeGroup = jdata.pick
        UI_BP.Inst.SetGroup();
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
    public composeCount: number;
}
export class ESyncData {
    public Status: string;
    public BanList: number[];
    public PickList: number[];
    public Ready: boolean[];
    public NowCtrl: number;
    public NextTime: number;
    public banFlage: number;
    public pickFlage: number;
    public HideCompose: ComposeData;
    public Compose: ComposeData;
}

export class Compose {
    public pt: number = ProtocolName.COMPOSE;
    public choose: number;
    public no: number;
    public group: number;
    public team: number;
}

export class GetCompose {
    public pt: number = ProtocolName.GET_COMPOST;
    public team: number;
}
export class EGetCompose {
    public pick: number[][]
}
export class ComposeData {
    public buleCompose: number[][]
    public redCompose: number[][]
}