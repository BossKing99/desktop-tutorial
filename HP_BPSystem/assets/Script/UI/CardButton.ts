import { MgData } from "../MagicCardManager";
import UI_BP from "./View/UI_BP";

const { ccclass, property } = cc._decorator;

@ccclass
export default class HeroButton extends cc.Component {
    @property(cc.Sprite)
    private Icon: cc.Sprite;
    @property(cc.Label)
    private NameLabel: cc.Label;
    private Id: number;
    private Type: number;
    private isChoose = false;
    public Init(data: MgData) {
        this.Id = data.no;
        this.Type = data.type;
        cc.loader.loadRes("HeroIcon/" + this.Id, cc.SpriteFrame, (e: Error, spriteFrame: cc.SpriteFrame) => {
            // 加載 失敗
            if (e) {
                console.log("HeroButton loader Error e = " + e);
                return
            }
            // 加載 成功
            this.Icon.spriteFrame = spriteFrame;
            this.NameLabel.string = data.name;
        })
    }
    public OnClick() {
        UI_BP.Inst.SetChoosebox(this.node, this.Id);
    }
    public Choose() {
        if (this.isChoose || this.Id == 0)
            return;
        this.isChoose = true;
        this.node.active = false;
    }
    public Filter(t) {
        if (this.isChoose)
            return;
        if (t == -1)
            this.node.active = true;
        else {
            if (this.Type == t) {
                this.node.active = true;
                return;
            }
            this.node.active = false;
        }
    }
}
