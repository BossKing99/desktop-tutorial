import Manager from "./public/Manager";
import CardButton from "./UI/CardButton";

const { ccclass, property } = cc._decorator;
@ccclass
export default class MagicCardManager extends cc.Component {

    @property(cc.Prefab)
    HeroButton: cc.Prefab = null;
    @property(cc.Node)
    HeroButtonPraent: cc.Node = null;
    @property(cc.Node)
    ChooseBox: cc.Node = null;

    private AllIcon: CardButton[];


    public Init(data: MgData[]): MgData[] {
        this.AllIcon = [];
        let allMgData: MgData[] = [];
        for (let index = 0; index < data.length; index++) {
            let newIcon = cc.instantiate(this.HeroButton)
            newIcon.parent = this.HeroButtonPraent;
            let CardButtonS = newIcon.getComponent(CardButton)
            CardButtonS.Init(data[index])
            this.AllIcon.push(CardButtonS);
            allMgData[data[index].no] = data[index];
        }
        this.HeroButtonPraent.setContentSize(1050, ((this.AllIcon.length / 5) + 1) * 226 + 40);
        this.ChooseBox.active = false

        return allMgData
    }
    public OpenChoosBox(cardType: number) {
        this.OnClick_filter(null, cardType)
        this.ChooseBox.active = true
    }
    public isChoose() {
        this.ChooseBox.active = false
    }
    public OnClick_filter(event, n) {
        let num = 0;
        for (let i = 0; i < this.AllIcon.length; i++)
            num += this.AllIcon[i].Filter(n) ? 1 : 0;
        this.HeroButtonPraent.setPosition(0, 240)
        this.HeroButtonPraent.setContentSize(1050, ((num / 5) + 1) * 226 + 40);
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