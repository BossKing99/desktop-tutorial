import { MgData } from "../MagicCardManager";
import UI_BP from "./View/UI_BP";

const { ccclass, property } = cc._decorator;

@ccclass
export default class CardButton extends cc.Component {
    @property(cc.Sprite)
    private Icon: cc.Sprite;
    @property(cc.Label)
    private NameLabel: cc.Label;
    @property(Boolean)
    private isView: boolean = false;
    @property(Number)
    private CardNumber: number = 0;
    @property(Number)
    private CardType: number = 0;
    private Id: number;
    private Type: number;
    private isChoose = false;
    
    public Init(data: MgData) {
        this.Id = data.no;
        this.Type = data.type;
        cc.loader.loadRes("HeroIcon/" + this.Id, cc.SpriteFrame, (e: Error, spriteFrame: cc.SpriteFrame) => {
            // 加載 失敗
            if (e) {
                console.log("CardButton loader Error e = " + e);
                return
            }
            // 加載 成功
            this.Icon.spriteFrame = spriteFrame;
            this.NameLabel.string = data.name;
        })
    }
    public OnClick() {
        if (this.isView) {
            UI_BP.Inst.ShowChooseBox(this.CardNumber, this.CardType);
        } else {
            UI_BP.Inst.SetChooseCardData(this.Id)
        }
    }
    public Choose() {
        if (this.isChoose || this.Id == 0)
            return;
        this.isChoose = true;
        this.node.active = false;
    }
    public Filter(t): boolean {
        if (this.isChoose)
            return false;
        if (t == -1)
            this.node.active = true;
        else {
            if (this.Type == t) {
                this.node.active = true;
                return true;
            }
            this.node.active = false;
        }
        return false;
    }
}
