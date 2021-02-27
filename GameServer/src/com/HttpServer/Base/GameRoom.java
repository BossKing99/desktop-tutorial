package com.HttpServer.Base;

import java.util.Timer;
import java.util.TimerTask;

import com.HttpServer.Manager.GameRoomManager;
import com.HttpServer.publicClass.Console;

import org.json.JSONObject;

public class GameRoom {
    private String _key;
    private long _createTime;
    private JSONObject RoomData = new JSONObject();

    public GameRoom(String blueTeamName, String redTeamName, String gameName, String key) {
        _key = key;
        _createTime = System.currentTimeMillis();
        try {
            RoomData.put("BlueTeamName", blueTeamName);
            RoomData.put("RedTeamName", redTeamName);
            RoomData.put("GameName", gameName);
        } catch (Exception e) {

        }
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                GameRoomManager.Inst.CloseRoom(_key);
            }
        };
        timer.schedule(task, 10000);
    }

}
