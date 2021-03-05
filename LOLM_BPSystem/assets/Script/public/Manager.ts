import Network from "../Network";
import IUIView from "../UI/View/IUIView";

const { ccclass, property } = cc._decorator;

@ccclass
export default class Manager extends cc.Component {
    public static Init: Manager = null;
    private _network: Network = null;
    private originURL: string = "";
    @property([cc.Node])
    private UIViewNode: cc.Node[] = [];
    private UIView: IUIView[] = [];
    onLoad() {
        Manager.Init = this;
        this._network = new Network();
        for (let i = 0; i < this.UIViewNode.length; i++) {
            this.UIView[i] = this.UIViewNode[i].getComponent<IUIView>(IUIView);
            this.UIView[i].Close();
        }
        let url = new URL(window.location.href);
        this.originURL = url.origin;

        if (url.searchParams.get("page") === null) {
            let newURL = new URL(this.originURL);
            newURL.searchParams.set("page", "0");
            window.location.href = newURL.toString();
        }
        else {
            this._network.creatWebSocket();
            let page: number = Number.parseInt(url.searchParams.get("page"));
            this.UIView[page].Open();
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
}
