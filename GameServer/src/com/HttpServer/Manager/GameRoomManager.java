package com.HttpServer.Manager;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.HttpServer.Base.GameRoom;
import com.HttpServer.Base.HPBanRoom;
import com.HttpServer.Base.LOLM3BanRoom;
import com.HttpServer.publicClass.Console;

import org.json.JSONObject;

public class GameRoomManager {
    public static GameRoomManager Inst = new GameRoomManager();

    private GameRoomManager() {
        Console.Log("GameRoomManager");
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                checkRoom();
            }
        };
        timer.schedule(task, 10000, 10000);
    }

    private Map<String, GameRoom> AllGame = new HashMap<>();

    public String CreateNewGameRoom(JSONObject jdata) {
        String Key = "";
        int gameTpye = 0;
        try {
            gameTpye = jdata.getInt("gameType");
            Key = GetKey(jdata.getString("blueTeamName") + jdata.getString("redTeamName")
                    + jdata.getString("gameName") + System.currentTimeMillis());
        } catch (Exception e) {
        }
        if (!Key.equals("")) {
            // 這邊可以換成可擴充結構
            GameRoom newGameRoom = null;
            switch (gameTpye) {
                case 1:
                    newGameRoom = new LOLM3BanRoom(jdata, Key);
                    break;
                case 2:
                    newGameRoom = new HPBanRoom(jdata, Key);
                    break;
            }

            synchronized (AllGame) {
                AllGame.put(Key, newGameRoom);
            }
        }
        return Key;
    }

    public void CloseRoom(String Key) {
        Console.Log("CloseRoom");
        synchronized (AllGame) {
            AllGame.remove(Key);
        }
        Console.Log("" + AllGame.size());
    }

    public GameRoom GetRoom(String key) {
        if (AllGame.containsKey(key))
            return AllGame.get(key);
        else
            return null;
    }

    // 檢查房間是否超時
    private void checkRoom() {
        Iterator<Map.Entry<String, GameRoom>> entries = AllGame.entrySet().iterator();
        synchronized (AllGame) {
            while (entries.hasNext()) {
                Map.Entry<String, GameRoom> entry = entries.next();
                if (entry.getValue().isExpired())
                    AllGame.remove(entry.getKey());
            }
        }
    }

    public static String GetKey(String str) {
        char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
            // Hash計算, 產生128位的長整數
            byte[] bytes = messageDigest.digest();
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (Byte b : bytes) {
                // 右移四位, 取字節中前四位轉換
                sb.append(hexDigits[(b >> 4) & 0x0f]);
                // 取字節中後四位轉換
                sb.append(hexDigits[b & 0x0f]);
            }
            // 輸出 602965cf9dd0e80ca28269257a6aba87
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
