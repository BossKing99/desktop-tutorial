package com.HttpServer.Portocol;

import com.HttpServer.publicClass.Console;

import org.json.JSONObject;

public class TestProtocol implements PortocolBasc {
    @Override
    public JSONObject Run(JSONObject jdata) {
        JSONObject jres = new JSONObject();
        try {
            jres.put("ttt", 123);
        } catch (Exception e) {
        }
        Console.Log("TestProtocol");
        return jres;
    }
}
