package com.HttpServer.Portocol;

import com.HttpServer.Manager.GameRoomManager;
import com.HttpServer.publicClass.CheckPassword;
import com.HttpServer.publicClass.Console;

import org.json.JSONObject;

import io.netty.channel.Channel;

public class CreateRoomProtocol implements PortocolBasc {
    @Override
    public JSONObject Run(JSONObject jdata, Channel ctx) {
        JSONObject jres = new JSONObject();
        try {
            if (CheckPassword.Check(jdata.getString("pass"))) {
                String[] key = GameRoomManager.Inst.CreateNewGameRoom(jdata);
                jres.put("resCode", 0);
                jres.put("key", key);
            } else {
                jres.put("resCode", 1);
            }
        } catch (Exception e) {
            Console.Err("CreateRoomProtocol");
        }

        return jres;
    }
}