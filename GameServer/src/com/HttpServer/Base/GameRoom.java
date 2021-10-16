package com.HttpServer.Base;

import com.HttpServer.Base.PlayerBase.Player;

import org.json.JSONObject;

public abstract class GameRoom {
    protected RoomStatus _status = RoomStatus.WAIT;
    protected String _key;
    protected String[] _pass;
    protected long _createTime;
    protected JSONObject RoomInfo; // 房間基本資訊
    protected JSONObject RoomJData = new JSONObject(); // 各規則下必備資料

    public GameRoom(JSONObject jdata, String key) {
        _key = key;
        _createTime = System.currentTimeMillis();
    }

    public abstract void Ready(JSONObject jdata);

    public JSONObject GetStatus() {
        return RoomJData;
    }

    public JSONObject GetRoomInfo() {
        return RoomInfo;
    }

    public abstract void Choose(JSONObject jdata, String ctxId);

    public boolean isExpired() {
        return System.currentTimeMillis() - _createTime > 14400000;
    }

    public String[] GetRoomPass() {
        return _pass;
    }

    public void SetRoomPass(String[] pass) {
        _pass = pass;
    }

    protected abstract void SetStatus(RoomStatus s);

    public abstract void PlayerAdd(Player p, int team);

    public abstract void RmPlayer(Player p);

    public abstract void Preview(JSONObject jdata, String ctxId);

    public abstract void Compose(JSONObject jdata);

    public abstract JSONObject GetCompose(int team);
}
