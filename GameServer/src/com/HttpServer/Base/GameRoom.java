package com.HttpServer.Base;

import com.HttpServer.Base.PlayerBase.Player;

import org.json.JSONObject;

public abstract class GameRoom {
    protected RoomStatus _status = RoomStatus.WAIT;
    protected String _key;
    protected String _pass;
    protected long _createTime;
    protected JSONObject RoomInfo; // 房間基本資訊
    protected JSONObject RoomJData = new JSONObject(); // 各規則下必備資料

    public GameRoom(JSONObject jdata, String[] key) {
        _key = key[0];
        _pass = key[1];
        _createTime = System.currentTimeMillis();
        RoomInfo = jdata;
    }

    public abstract void Ready(JSONObject jdata);

    public JSONObject GetStatus() {
        return RoomJData;
    }

    public abstract void Choose(JSONObject jdata);

    public boolean isExpired() {
        return System.currentTimeMillis() - _createTime > 14400000;
    }

    protected abstract void SetStatus(RoomStatus s);
    protected abstract void PlayerAdd(Player p);
}
