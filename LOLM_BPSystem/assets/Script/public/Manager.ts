import IUIView from "../UI/View/IUIView";

const { ccclass, property } = cc._decorator;

@ccclass
export default class Manager extends cc.Component {
    private originURL: string = "";
    @property([cc.Node])
    private UIViewNode: cc.Node[] = [];
    private UIView: IUIView[] = [];
    onLoad() {
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

        }
    }


}
