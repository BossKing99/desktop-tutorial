package com.HttpServer.Base;

import org.json.JSONObject;

public class LOLM3BanRoom extends GameRoom {
    public LOLM3BanRoom(JSONObject jdata, String key) {
        super(jdata, key);
    }

    @Override
    public void Ready() {
    }

    @Override
    public JSONObject GetStatus() {

        return new JSONObject();
    }

    @Override
    public void Choose(JSONObject jdata) {
    }
}
