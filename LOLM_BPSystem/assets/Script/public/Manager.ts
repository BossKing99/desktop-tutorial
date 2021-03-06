import Network from "../Network";
import IUIView from "../UI/View/IUIView";

const { ccclass, property } = cc._decorator;

@ccclass
export default class Manager extends cc.Component {
    public static Inst: Manager = null;
    private _network: Network = null;
    private originURL: string = "";
    @property([cc.Node])
    private UIViewNode: cc.Node[] = [];
    private UIView: IUIView[] = [];
    async onLoad() {
        Manager.Inst = this;
        this._network = new Network();
        for (let i = 0; i < this.UIViewNode.length; i++) {
            this.UIView[i] = this.UIViewNode[i].getComponent<IUIView>(IUIView);
            this.UIView[i].Close();
        }
        let url = new URL(window.location.href);
        this.originURL = url.origin;
        Manager.Inst.GetNetwork().AddCallBack(ProtocolName.LOGIN, this.GetLoginCallBack);
        if (url.searchParams.get("page") === null) {
            let newURL = new URL(this.originURL);
            newURL.searchParams.set("page", "0");
            window.location.href = newURL.toString();
        }
        else {
            this._network.creatWebSocket();
            while (true) {
                if (this.GetNetwork().isConnent()) {
                    Manager.Inst.GetNetwork().Send(new LoginData());
                    break;
                }
                else
                    await this.Wait(100);
            }
        }
    }
    public GetNetwork(): Network {
        return this._network;
    }

    public Wait(ms) {
        return new Promise(r => setTimeout(r, ms));
    }
    public GetOriginURL(): string {
        return this.originURL;
    }
    public GetMyCtrlTeam() {
        let url = new URL(window.location.href);
        if (url.searchParams.has("team"))
            return url.searchParams.get("team");
        else
            return -1;
    }
    private GetLoginCallBack(data: string) {
        let jdata = JSON.parse(data);
        if (jdata.resCode === 0) {
            let url = new URL(window.location.href);
            let page: number = Number.parseInt(url.searchParams.get("page"));
            Manager.Inst.UIView[page].Open();
        }
    }
}
export class LoginData {
    public pt: number = ProtocolName.LOGIN;
}
export class ProtocolName {
    private ProtocolName() { }
    public static LOGIN: number = 1;
    public static CREATE_ROOM: number = 2; // 創房
    public static LINK_ROOM: number = 3; // 連進房
    public static CHOOSE: number = 4; // 選角
    public static SYNC: number = 5; // 同步房間資訊
    public static PREVIEW: number = 6; // 預選角
    public static GET_LOLMDATA: number = 7;
    public static READY: number = 8;
}