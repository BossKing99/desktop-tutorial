import { HeroData } from "../HeroIconManager";
import Manager from "../public/Manager";
import UI_BP from "./View/UI_BP";

const { ccclass, property } = cc._decorator;

@ccclass
export default class HeroButton extends cc.Component {
    @property(cc.Sprite)
    private Icon: cc.Sprite;
    private Id: number;
    private Tag = [];
    private isChoose = false;
    public Init(data: HeroData) {
        this.Id = data.Num;
        this.Tag = data.Tag.split(",");
        cc.loader.loadRes("HeroIcon/" + this.Id, cc.SpriteFrame, (e: Error, spriteFrame: cc.SpriteFrame) => {
            // 加載 失敗
            if (e) {
                console.log("HeroButton loader Error e = " + e);
                return
            }
            // 加載 成功
            this.Icon.spriteFrame = spriteFrame;
        })
    }
    public OnClick() {
        UI_BP.Inst.SetChoosebox(this.node, this.Id);
    }
    public Choose() {
        if (this.isChoose)
            return;
        this.isChoose = true;
        this.node.active = false;
    }
    public Filter(tag) {
        if (this.isChoose)
            return;
        if (tag == -1)
            this.node.active = true;
        else {
            for (let i = 0; i < this.Tag.length; i++) {
                if (this.Tag[i] == tag) {
                    this.node.active = true;
                    return;
                }
            }
            this.node.active = false;
        }
    }
}
