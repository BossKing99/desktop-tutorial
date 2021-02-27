package com.HttpServer.Manager;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.HttpServer.Base.GameRoom;
import com.HttpServer.publicClass.Console;

public class GameRoomManager {
    public static GameRoomManager Inst = new GameRoomManager();

    private GameRoomManager() {
    }

    private Map<String, GameRoom> AllGame = new HashMap<>();

    public String CreateNewGameRoom(String blueTeamName, String redTeamName, String GameName) {
        String Key = GetKey(blueTeamName + redTeamName + GameName + System.currentTimeMillis());
        GameRoom newGameRoom = new GameRoom(blueTeamName, redTeamName, GameName, Key);
        synchronized (AllGame) {
            AllGame.put(Key, newGameRoom);
        }
        return Key;
    }

    public void CloseRoom(String Key) {
        Console.Log("CloseRoom");
        synchronized (AllGame) {
            AllGame.remove(Key);
        }
        Console.Log(""+AllGame.size());
    }

    private String GetKey(String str) {
        char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(str.getBytes("UTF-8"));
            // Hash計算, 產生128位的長整數
            byte[] bytes = messageDigest.digest();
            StringBuffer sb = new StringBuffer(bytes.length * 2);
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
