# 百家伺服器說明文件
* JAVA版本 1.8.0_77
* 開發環境 VSCode

* 流程 : <br>
    從com.box.Main.GameServer啟動遊戲Server服務<br>
    在BaccaraatMain會自初始化各種資料<br>
    依DB(vbac.bac_roomdata)設定的房間資料，啟動房間<br>
    房間資料每次更新完會設定下一個更新時間點，由Main.Update統一呼叫<br>
    桌資料會依據房內人數自動擴展<br>
    接下來會開始等待Client傳封包過來，並依據封包內容處理<br>
    玩家登入時會幫玩家生成一個PlayerBehavior，裡面會儲存玩家的各種資料<br>


* vscode build jar 方法 : <br>
    * 把bin裡的class檔都丟進BuildData裡面<br>
    * 先進到BuildData資料夾<br>
    * 然後輸入 jar -cvfm ../BaccaratServer.jar MANIFEST.MF ./<br>
    * 如果有新的jar檔要加入的話 直接把jar檔解壓縮將資料丟到自己的資料夾裡(都在BuildData裡<br>

* jar command not found : https://ithelp.ithome.com.tw/articles/10213551

* 資料夾說明:
    * com.Baccarat :
        * BehaviorClass : 遊戲主體的Class 像是房間桌子等
        * DataClass : 一些結構用途的資料
        * DB : 取得資料庫連結
        * Main : 程式啟動點
        * Manager : 一些會大量產生的class的總控
        * Portocol : 處理玩家傳來的封包的class
        * RSA : 放了加密算法的東西
        * TestMode : 測試用功能的資料夾
        * Thread : 放各執行緒的地方

* 開啟server:
    * 需先cd到jar檔放置的資料夾
    * nohup java -XX:+UseG1GC [監控] -jar BaccaratServer.jar &> Logxxxxxxx.txt &
    * [監控] enter記得刪除後再複製
        * -Djava.rmi.server.hostname=18.139.193.212
        * -Dcom.sun.management.jmxremote.port=9999  
        * -Dcom.sun.management.jmxremote.rmi.port=9999 
        * -Dcom.sun.management.jmxremote.ssl=false 
        * -Dcom.sun.management.jmxremote.authenticate=false -jar

* vscode無法執行debug解決方法:<br>
在launch.json中，加入 "console": "internalConsole"<br>
https://gitter.im/Microsoft/vscode-java-debug?at=5b35232bad21887018e6a338

* 維護:
    * 修改DB vbac.bac_game_set.Shutdown = 1 (server有需要關機的時候才需要設定)<br>
發送維護通知API(與上一個步驟，順序調換也可以)<br>
server自動關閉後，即可上傳新版本，並重新啟動server<br>
(vbac.bac_game_set.Shutdown在server關閉後會自動改為0)