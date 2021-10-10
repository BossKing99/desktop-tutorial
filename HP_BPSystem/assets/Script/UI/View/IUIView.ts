
export default class IUIView extends cc.Component {
    public Open() {
        this.node.active = true;
    }
    public Close() {
        this.node.active = false;
    }
}
