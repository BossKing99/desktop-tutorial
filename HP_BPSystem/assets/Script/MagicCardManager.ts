import Manager from "./public/Manager";
import CardButton from "./UI/CardButton";

const { ccclass, property } = cc._decorator;
@ccclass
export default class MagicCardManager extends cc.Component {

    @property(cc.Prefab)
    HeroButton: cc.Prefab = null;
    @property(cc.Node)
    HeroButtonPraent: cc.Node = null;
    private AllIcon: CardButton[];
    public Init(data: MgData[]) {
        this.AllIcon = [];
        for (let index = 0; index < data.length; index++) {
            let newIcon = cc.instantiate(this.HeroButton)
            newIcon.parent = this.HeroButtonPraent;
            let CardButtonS = newIcon.getComponent(CardButton)
            CardButtonS.Init(data[index])
            this.AllIcon.push(CardButtonS);
        }
        this.HeroButtonPraent.setContentSize(1050, ((this.AllIcon.length / 4) + 1) * 122);
    }
    public isChoose(n) {
        console.log(n);
        this.AllIcon[n].Choose();
    }
    public OnClick_filter(event, n) {
        for (let i = 0; i < this.AllIcon.length; i++)
            this.AllIcon[i].Filter(n);
    }
}
export class HPData {
    public ChampoinTable: MgData[];
}

export class MgData {
    public no: number = 0;
    public type: number = 0;
    public name: string = "";
}