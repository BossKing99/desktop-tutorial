// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html

import Manager from "./public/Manager";
import { ECreateRoomData } from "./UI/View/UI_CreateRoom";

const { ccclass, property } = cc._decorator;

@ccclass
export default class Network {
    private _mSocket: WebSocket;
    private _isConnent: boolean = false;
    private _callBack: Function[] = [];
    creatWebSocket(ip: string) {
        //新建Socket，並請求連線
        this._mSocket = new WebSocket('wss://' + ip + ':8083/ws');
        //設定request觸發處理程序(callback)
        //["onopen","onmessage","onerror","onclose"]
        this._mSocket.onopen = async () => {
            console.log("onopen");
            this._isConnent = true;
        }

        this._mSocket.onmessage = (evt) => {
            let jdata = JSON.parse(evt.data);
            console.log(evt.data);
            this._callBack[jdata.pt](jdata.data);
        }

        this._mSocket.onerror = () => {

        }

        this._mSocket.onclose = () => {
            this._isConnent = false;
        }
    }
    public Send(data) {
        console.log(data);
        if (this.isConnent())
            this._mSocket.send(JSON.stringify(data));
        else
            window.alert("與主機無連線");
    }
    public isConnent(): boolean {
        return this._isConnent;
    }
    public AddCallBack(key: number, fun: Function) {
        this._callBack[key] = fun;
    }
}