// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html

import CardButton from "./CardButton";
import UI_Output from "./View/UI_Output";

const { ccclass, property } = cc._decorator;

@ccclass
export default class OutputCardInfo extends cc.Component {

    @property([cc.Node])
    info: cc.Node[] = [];
    @property(cc.Label)
    InfoName: cc.Label;

    private BuleCard: CardButton[] = []
    private RedCard: CardButton[] = []
    onLoad() {
        for (let j = 0; j < this.info[0].childrenCount; j++) {
            this.BuleCard[j] = this.info[0].children[j].getComponent(CardButton)
        }

        for (let j = 0; j < this.info[1].childrenCount; j++) {
            this.RedCard[j] = this.info[1].children[j].getComponent(CardButton)
        }

    }
    public SetName(no: number) {
        this.InfoName.string = "牌組 " + (no + 1)
    }
    public SetBuleInfo(data: number[]) {
        console.log(data)
        for (let i = 0; i < data.length; i++) {
            if (data[i] != 0)
                this.BuleCard[i].Init(UI_Output.Inst.AllMgData[data[i]])
        }
    }
    public SetRedInfo(data: number[]) {
        for (let i = 0; i < data.length; i++) {
            if (data[i] != 0)
                this.RedCard[i].Init(UI_Output.Inst.AllMgData[data[i]])
        }
    }
}
