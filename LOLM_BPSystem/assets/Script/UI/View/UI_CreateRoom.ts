import Manager, { ProtocolName } from "../../public/Manager";
import IUIView from "./IUIView";

const { ccclass, property } = cc._decorator;

@ccclass
export default class UI_CreateRoom extends IUIView {
    @property(cc.EditBox)
    gameName_box: cc.EditBox = null;
    @property(cc.EditBox)
    Team1Name_box: cc.EditBox = null;
    @property(cc.EditBox)
    Team2Name_box: cc.EditBox = null;
    @property(cc.EditBox)
    pass_box: cc.EditBox = null;
    @property(cc.EditBox)
    banCount_box: cc.EditBox = null;
    @property(cc.Toggle)
    isRandomTeam: cc.Toggle = null;
    private isClick: boolean = false;
    public static Inst: UI_CreateRoom = null;
    public Open() {
        super.Open();
        UI_CreateRoom.Inst = this;
        let url = new URL(window.location.href);
        if (url.searchParams.get("team1") != null)
            this.Team1Name_box.string = url.searchParams.get("team1");
        if (url.searchParams.get("team2") != null)
            this.Team2Name_box.string = url.searchParams.get("team2");
        Manager.Inst.GetNetwork().AddCallBack(ProtocolName.CREATE_ROOM, this.GetCallBack);
    }
    public Close() {
        super.Close();
    }
    public OnClick() {
        if (!this.isClick) {
            let data: CreateRoomData = new CreateRoomData();
            if (this.isRandomTeam.isChecked && Math.random() * 2 < 1) {
                data.blueTeamName = this.Team2Name_box.string;
                data.redTeamName = this.Team1Name_box.string;
            }
            else {
                data.blueTeamName = this.Team1Name_box.string;
                data.redTeamName = this.Team2Name_box.string;
            }
            data.gameName = this.gameName_box.string;
            data.banCount = Number.parseInt(this.banCount_box.string);
            data.pass = this.pass_box.string;
            if (data.blueTeamName === "" || data.redTeamName === "" || data.gameName === "" || data.pass === "")
                window.alert("輸入框不可為空");
            else if (data.banCount < 1 || data.banCount > 5)
                window.alert("Ban數需介於1~5之間");
            else if (data.redTeamName === data.blueTeamName)
                window.alert("隊名不可相同");
            else {
                this.isClick = true;
                Manager.Inst.GetNetwork().Send(data);
            }
        }
    }
    private GetCallBack(data: string) {
        UI_CreateRoom.Inst.isClick = false;
        let jdata: ECreateRoomData = JSON.parse(data);
        if (jdata.resCode === 0) {
            let url = new URL(Manager.Inst.GetOriginURL());
            url.searchParams.set("page", "1");
            url.searchParams.set("key", jdata.key);
            url.searchParams.set("pass0", jdata.roomPass[0]);
            url.searchParams.set("pass1", jdata.roomPass[1]);
            url.searchParams.set("blue", jdata.blueTeam);
            url.searchParams.set("red", jdata.redTeam);
            window.location.href = url.toString();
        }
        else if (jdata.resCode === 1)
            window.alert("密碼錯誤");
    }
}
export class CreateRoomData {
    public pt: number = ProtocolName.CREATE_ROOM;
    public blueTeamName: string = "";
    public redTeamName: string = "";
    public gameName: string = "";
    public banCount: number = 1;
    public pass: string = "";
    public gameType: number = 2
    public composeCount: number = 1;
}
export class ECreateRoomData {
    public resCode: number = -1;
    public key: string;
    public roomPass: string[] = []
    public blueTeam: string;
    public redTeam: string;
    public gameName: string;
}