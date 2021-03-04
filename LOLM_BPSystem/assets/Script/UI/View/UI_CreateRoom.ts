import Manager from "../../public/Manager";
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
    private blueTeam: string = "";
    private redTeam: string = "";
    public Open() {
        super.Open();
        let url = new URL(window.location.href);
        if (url.searchParams.get("team1") != null)
            this.Team1Name_box.string = url.searchParams.get("team1");
        if (url.searchParams.get("team2") != null)
            this.Team2Name_box.string = url.searchParams.get("team2");
        Manager.Init.GetNetwork().AddCallBack(2, this.GetCallBack);
    }
    public Close() {
        super.Close();
    }
    public OnClick() {
        let data: CreateRoomData = new CreateRoomData();
        if (this.isRandomTeam.isChecked && Math.random() * 2 < 1) {
            data.blueTeamName = this.Team2Name_box.string;
            data.redTeamName = this.Team1Name_box.string;
        }
        else {
            data.blueTeamName = this.Team1Name_box.string;
            data.redTeamName = this.Team2Name_box.string;
        }
        this.blueTeam = data.blueTeamName;
        this.redTeam = data.redTeamName;
        data.gameName = this.gameName_box.string;
        data.banCount = Number.parseInt(this.banCount_box.string);
        data.pass = this.pass_box.string;
        if (data.blueTeamName === "" || data.redTeamName === "" || data.gameName === "" || data.pass === "")
            window.alert("輸入框不可為空");
        else if (data.banCount < 0 || data.banCount > 5)
            window.alert("Ban數需介於0~5之間");
        else if (data.redTeamName === data.blueTeamName)
            window.alert("隊名不可相同");
        else
            Manager.Init.GetNetwork().Send(data);
    }
    private GetCallBack(data: string) {
        let jdata = JSON.parse(data);
        if (jdata.resCode === 0) {
            let url = new URL(Manager.Init.GetOriginURL());
            url.searchParams.set("page", "1");
            url.searchParams.set("key", jdata.key[0]);
            url.searchParams.set("pass", jdata.key[1]);
            url.searchParams.set("blue", jdata.blueTeam);
            url.searchParams.set("red", jdata.redTeam);
            window.location.href = url.toString();
        }
        else if (jdata.resCode === 1)
            window.alert("密碼錯誤");
    }
}
export class CreateRoomData {
    public pt: number = 2;
    public blueTeamName: string = "";
    public redTeamName: string = "";
    public gameName: string = "";
    public banCount: number = 0;
    public pass: string = "";
}
export class ECreateRoomData {
    public resCode: number = -1;
    public key: string[] = [];
    public blueTeam: string;
    public redTeam: string;
    public gameName: string;
}