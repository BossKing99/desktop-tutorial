package com.HttpServer.Portocol;

import com.HttpServer.publicClass.Console;

import org.json.JSONObject;

import io.netty.channel.Channel;

public class TestProtocol implements PortocolBasc {
    @Override
    public JSONObject Run(JSONObject jdata, Channel ctx) {
        JSONObject jres = new JSONObject();
        try {
            jres.put("ttt", 123);
        } catch (Exception e) {
        }
        Console.Log("TestProtocol");
        return jres;
    }
}
