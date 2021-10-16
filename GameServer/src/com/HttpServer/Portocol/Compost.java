package com.HttpServer.Portocol;

import com.HttpServer.Base.PlayerBase.Player;
import com.HttpServer.Manager.PlayerManager;

import org.json.JSONObject;

import io.netty.channel.Channel;

public class Compost implements PortocolBasc {
    @Override
    public JSONObject Run(JSONObject jdata, Channel ctx) {
        JSONObject jres = new JSONObject();
        try {
            Player p = PlayerManager.GetPlayer(ctx.id().toString());
            if (p != null && p.GetRoom() != null) {
                p.GetRoom().Compose(jdata);
            }
        } catch (Exception e) {
        }
        return jres;
    }
}