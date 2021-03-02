const { ccclass, property } = cc._decorator;

@ccclass
export default class HeroButton extends cc.Component {
    private Id: number;
    @property(cc.Sprite)
    private Icon: cc.Sprite;
    public Init(id: number) {
        this.Id = id;
        cc.loader.loadRes("HeroIcon/"+this.Id, cc.SpriteFrame, (e: Error, spriteFrame: cc.SpriteFrame) => {
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

    }
    start() {

    }

    // update (dt) {}
}
