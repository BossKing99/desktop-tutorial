package com.HttpServer.Portocol;

import org.json.JSONObject;

public class TestProtocol implements PortocolBasc {
    @Override
    public JSONObject Run(JSONObject jdata) {
        JSONObject jres = new JSONObject();
        try {
            jres.put("ttt", 123);
        } catch (Exception e) {
        }

        return jres;
    }
}
