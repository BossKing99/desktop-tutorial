package com.HttpServer.Portocol;

import com.HttpServer.Manager.GameRoomManager;
import com.HttpServer.publicClass.Console;

import org.json.JSONObject;

import io.netty.channel.Channel;

public class CreateRoomProtocol implements PortocolBasc {
    @Override
    public JSONObject Run(JSONObject jdata, Channel ctx) {
        JSONObject jres = new JSONObject();
        try {
            String[] key = GameRoomManager.Inst.CreateNewGameRoom(jdata);
            jres.put("key", key);
        } catch (Exception e) {
        }
        Console.Log("TestProtocol");
        return jres;
    }
}