import HeroButton from "./UI/HeroButton";

const { ccclass, property } = cc._decorator;
@ccclass
export default class NewClass extends cc.Component {

    @property(cc.Prefab)
    HeroButton: cc.Prefab = null;
    @property(cc.Node)
    HeroButtonPraent: cc.Node = null;
    private AllIcon: HeroButton[];
    start() {
        this.AllIcon = [];
        for (let index = 0; index < 50; index++) {
            let newIcon = cc.instantiate(this.HeroButton)
            newIcon.parent = this.HeroButtonPraent;
            let HeroButtonS = newIcon.getComponent(HeroButton)
            HeroButtonS.Init(index)
            this.AllIcon.push(HeroButtonS);
        }
        this.HeroButtonPraent.setContentSize(800, this.AllIcon.length / 10 * 80);
    }


}
