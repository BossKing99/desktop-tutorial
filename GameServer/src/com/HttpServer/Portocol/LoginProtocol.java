package com.HttpServer.Portocol;

import com.HttpServer.Manager.PlayerManager;
import com.HttpServer.publicClass.Console;

import org.json.JSONObject;

import io.netty.channel.Channel;

public class LoginProtocol implements PortocolBasc {
    @Override
    public JSONObject Run(JSONObject jdata, Channel ctx) {
        JSONObject jres = new JSONObject();
        try {
            PlayerManager.AddPlayer(ctx);
            jres.put("resCode", 0);
            jres.put("time", System.currentTimeMillis());
        } catch (Exception e) {
            Console.Err("LoginProtocol error e = \n" + e);
        }
        return jres;
    }
}