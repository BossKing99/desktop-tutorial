import Manager from "../../public/Manager";
import IUIView from "./IUIView";

const { ccclass, property } = cc._decorator;

@ccclass
export default class UI_RoomLink extends IUIView {
    @property([cc.Label])
    private TeamLable: cc.Label[] = [];
    private Urls: string[] = [];
    public Open() {
        super.Open();
        let url = new URL(window.location.href);
        this.TeamLable[0].string = url.searchParams.get("blue");
        this.TeamLable[1].string = url.searchParams.get("red");
        for (let i = 0; i < 2; i++) {
            let TeamUrl = new URL(Manager.Inst.GetOriginURL());
            TeamUrl.searchParams.set("page", "2");
            TeamUrl.searchParams.set("team", i.toString());
            TeamUrl.searchParams.set("key", url.searchParams.get("key"));
            TeamUrl.searchParams.set("pass", url.searchParams.get("pass" + i));
            this.Urls[i] = TeamUrl.toString();
        }
        let AudienceUrl = new URL(Manager.Inst.GetOriginURL());
        AudienceUrl.searchParams.set("page", "2");
        AudienceUrl.searchParams.set("key", url.searchParams.get("key"));
        this.Urls[2] = AudienceUrl.toString();
    }
    public Close() {
        super.Close();
    }
    public OnClick_Goto(event, n) {
        window.open(this.Urls[n]);
    }
    public OnClick_Copy(event, n) {
        const input = document.createElement('input');
        document.body.appendChild(input);
        input.setAttribute('value', this.Urls[n]);
        input.select();
        document.execCommand('copy')
        window.alert("網址已複製");
    }
}
