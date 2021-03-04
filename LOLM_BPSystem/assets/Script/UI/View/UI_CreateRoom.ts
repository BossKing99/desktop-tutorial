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

    public Open() {
        super.Open();
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
    private GetCallBack(data: ECreateRoomData) {
        console.log(data);
    }
}
export class CreateRoomData {
    public pt: number = 2;
    public blueTeamName: String = "";
    public redTeamName: String = "";
    public gameName: String = "";
    public banCount: number = 0;
    public pass: String = "";
}
export class ECreateRoomData {
    public pt: number = 2;
    public blueTeamName: String = "";
    public redTeamName: String = "";
    public gameName: String = "";
    public banCount: number = 0;
    public pass: String = "";
}