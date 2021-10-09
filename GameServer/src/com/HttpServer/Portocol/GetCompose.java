package com.HttpServer.Portocol;

import com.HttpServer.Base.PlayerBase.Player;
import com.HttpServer.Manager.PlayerManager;
import com.HttpServer.publicClass.Console;

import org.json.JSONObject;

import io.netty.channel.Channel;

public class GetCompose implements PortocolBasc {
    @Override
    public JSONObject Run(JSONObject jdata, Channel ctx) {
        JSONObject jres = new JSONObject();
        try {
            Player p = PlayerManager.GetPlayer(ctx.id().toString());
            if (p != null && p.GetRoom() != null) {
                String pass = jdata.getString("pass");
                int team = jdata.getInt("team");
                jres = p.GetRoom().GetCompose(pass, team);
            }
        } catch (Exception e) {
            Console.Err("CreateRoomProtocol error e= " + e.toString());
        }

        return jres;
    }
}