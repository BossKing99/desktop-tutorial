package com.HttpServer.Base;

import org.json.JSONObject;

public abstract class GameRoom {
    protected String _key;
    protected long _createTime;
    protected JSONObject RoomData;

    public GameRoom(JSONObject jdata, String key) {
        _key = key;
        _createTime = System.currentTimeMillis();
        RoomData = jdata;
    }
    public abstract void Ready();
    public abstract JSONObject GetStatus();
    public abstract void Choose(JSONObject jdata);
    public boolean isExpired() {
        return System.currentTimeMillis() - _createTime > 14400000;
    }
}
