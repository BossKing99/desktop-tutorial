import Manager from "../../public/Manager";
import IUIView from "./IUIView";

const { ccclass, property } = cc._decorator;

@ccclass
export default class UI_BP extends IUIView {
    private myTeam: number = -1;
    private key: string
    private pass: string
    public Open() {
        super.Open();
        let url = new URL(window.location.href);
        if (url.searchParams.has("team")) {
            this.myTeam = Number.parseInt(url.searchParams.get("team"));
            this.pass = url.searchParams.get("pass");
        }
        this.key = url.searchParams.get("key");
        let linkData: LinkRoomData = new LinkRoomData();
        linkData.key = this.key;
        Manager.Init.GetNetwork().Send(linkData);
    }
    public Close() {
        super.Close();
    }
}
export class LinkRoomData {
    public pt: number = 3;
    public key: string = "";
}
export class ECreateRoomData {
    public resCode: number = -1;
    public key: string[] = [];
    public blueTeam: string;
    public redTeam: string;
    public gameName: string;
}