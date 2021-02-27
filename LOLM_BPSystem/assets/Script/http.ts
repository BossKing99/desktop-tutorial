// Learn TypeScript:
//  - https://docs.cocos.com/creator/manual/en/scripting/typescript.html
// Learn Attribute:
//  - https://docs.cocos.com/creator/manual/en/scripting/reference/attributes.html
// Learn life-cycle callbacks:
//  - https://docs.cocos.com/creator/manual/en/scripting/life-cycle-callbacks.html

const { ccclass, property } = cc._decorator;

@ccclass
export default class http extends cc.Component {
    public static ins: http = new http();

    public sendHttpPOST(data, callback) {
        //發送請求前，須先取得新的 XMLHttpRequest Pipeline
        var request = cc.loader.getXMLHttpRequest();
        var url = "http://127.0.0.1:8081";
        //開啟通道(設定請求方法、網址、異步請求)
        request.open("POST", url, true);
        //有需要時，設定請求的封包header，以下檔頭為node.js使用的檔頭
        request.setRequestHeader
            ("Content-Type", "application/x-www-form-urlencoded");
        //設定request觸發處理程序(callback)
        //["onloadstart","onabort","onerror","onload","onloadend","ontimeout"]
        request["onloadend"] = function () {
            //readyState
            //0:沒有發出請求 1:建立(open)但未發出(send)
            //2:已發出請求，請求處理中 3:已處理，等待完整回應 4:接收完成
            //status : HTTP狀態碼
            if (request.readyState == 4 &&
                (request.status >= 200 && request.status < 300)) {
                //取出回傳字串responseText
                var response = request.responseText;
                console.log(response);
                callback(JSON.parse(response));
            }
        }

        //送出請求(送出後，觸發callback，才會進入onreadystatechange)
        request.send("req=" + JSON.stringify(data)); //[資料] = "req=" + JSON.stringify(Data)
    }


}
