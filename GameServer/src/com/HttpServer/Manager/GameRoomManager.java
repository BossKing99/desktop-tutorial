package com.HttpServer.Manager;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.HttpServer.Base.GameRoom;
import com.HttpServer.Base.LOLM3BanRoom;
import com.HttpServer.publicClass.Console;

import org.json.JSONObject;

public class GameRoomManager {
    public static GameRoomManager Inst = new GameRoomManager();

    private GameRoomManager() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                checkRoom();
            }
        };
        timer.schedule(task, 14400000);
    }

    private Map<String, GameRoom> AllGame = new HashMap<>();

    public String[] CreateNewGameRoom(JSONObject jdata) {
        String[] Key = new String[2];
        try {
            Key[0] = getKey(jdata.getString("blueTeamName") + jdata.getString("redTeamName")
                    + jdata.getString("gameName") + System.currentTimeMillis());
            Key[1] = getKey(System.currentTimeMillis() + "").substring(5, 12);
        } catch (Exception e) {
        }
        if (!Key[0].equals("")) {
            // 這邊可以換成可擴充結構
            GameRoom newGameRoom = new LOLM3BanRoom(jdata, Key);
            synchronized (AllGame) {
                AllGame.put(Key[0], newGameRoom);
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

    private String getKey(String str) {
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
