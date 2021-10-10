// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html

const { ccclass, property } = cc._decorator;

@ccclass
export default class BanPickIcon extends cc.Component {

    @property(cc.Sprite)
    private Icon: cc.Sprite = null;
    @property(cc.Node)
    private ReadyNode: cc.Node = null;
    private num: number = -1;
    public SetReady() {
        this.ReadyNode.active = true;
    }
    public SetChoose(num: number) {
        if (this.num == num)
            return;
        this.num = num;
        cc.loader.loadRes("HeroIcon/" + num, cc.SpriteFrame, (e: Error, spriteFrame: cc.SpriteFrame) => {
            // 加載 失敗
            if (e) {
                console.log("HeroButton loader Error e = " + e);
                return
            }
            // 加載 成功
            this.Icon.spriteFrame = spriteFrame;
        })
        if (this.ReadyNode !== null)
            this.ReadyNode.active = false;
    }
    public SetPreView(num: number) {
        cc.loader.loadRes("HeroIcon/" + num, cc.SpriteFrame, (e: Error, spriteFrame: cc.SpriteFrame) => {
            // 加載 失敗
            if (e) {
                console.log("HeroButton loader Error e = " + e);
                return
            }
            // 加載 成功
            this.Icon.spriteFrame = spriteFrame;
        })
    }
}
