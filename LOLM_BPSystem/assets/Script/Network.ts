// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html

const { ccclass, property } = cc._decorator;

@ccclass
export default class Network extends cc.Component {
    private _mSocket: WebSocket;
    onEnable() {
        this.creatWebSocket();
    }
    creatWebSocket() {
        //新建Socket，並請求連線
        this._mSocket = new WebSocket('ws://127.0.0.1:8083/ws');
        //設定request觸發處理程序(callback)
        //["onopen","onmessage","onerror","onclose"]
        this._mSocket.onopen = () => {
            console.log("onopen");
            let data: TestData = new TestData();
            data.acc = "132";
            data.pass = "321";
            this._mSocket.send(JSON.stringify(data));
        }

        this._mSocket.onmessage = (evt) => {
            console.log(evt);
        }

        this._mSocket.onerror = () => {

        }

        this._mSocket.onclose = () => {

        }
    }
    public Test()
    {
        let data: TestData = new TestData();
        data.acc = "132";
        data.pass = "321";
        this._mSocket.send(JSON.stringify(data));
    }
}
export class TestData {
    public pt: number = 0;
    public acc: String = "";
    public pass: String = "";
}