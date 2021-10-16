// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html

import UI_BP from "./View/UI_BP";

const { ccclass, property } = cc._decorator;

@ccclass
export default class BanInfo extends cc.Component {
    @property([cc.Label])
    private InfoLabel: cc.Label[] = [];

    public SetInfo(data: number[]) {
        if (data == undefined)
            return;
        let f = 0;
        for (let i = 0; i < data.length; i++) {
            if (data[i] != -1) {
                if (data[i] != 0)
                    this.InfoLabel[f].string = UI_BP.Inst.AllMgData[data[i]].name;
                f++;
            }
        }
    }
}
