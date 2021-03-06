import Manager from "./public/Manager";
import HeroButton from "./UI/HeroButton";

const { ccclass, property } = cc._decorator;
@ccclass
export default class HeroIconManager extends cc.Component {

    @property(cc.Prefab)
    HeroButton: cc.Prefab = null;
    @property(cc.Node)
    HeroButtonPraent: cc.Node = null;
    private AllIcon: HeroButton[];
    public Init(data: HeroData[]) {
        this.AllIcon = [];
        for (let index = 0; index < data.length; index++) {
            let newIcon = cc.instantiate(this.HeroButton)
            newIcon.parent = this.HeroButtonPraent;
            let HeroButtonS = newIcon.getComponent(HeroButton)
            HeroButtonS.Init(data[index])
            this.AllIcon.push(HeroButtonS);
        }
        this.HeroButtonPraent.setContentSize(800, this.AllIcon.length / 10 * 80);
    }
    public OnClick_filter(event, n) {
        for (let i = 0; i < this.AllIcon.length; i++)
            this.AllIcon[i].Filter(n);
    }
}
export class LOLMData {
    public ChampoinTable: HeroData[];
    public Tag: TagData[];
}

export class HeroData {
    public Num: number = 0;
    public NameCN: string = "";
    public NameTW: string = "";
    public Tag: string = "";
}
export class TagData {
    public Num: number = 0;
    public Name: string = "";
}