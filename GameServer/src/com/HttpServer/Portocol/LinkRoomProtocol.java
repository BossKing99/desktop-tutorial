package com.HttpServer.Portocol;

import com.HttpServer.Base.GameRoom;
import com.HttpServer.Base.PlayerBase.Player;
import com.HttpServer.Manager.GameRoomManager;
import com.HttpServer.Manager.PlayerManager;
import com.HttpServer.publicClass.Console;

import org.json.JSONObject;

import io.netty.channel.Channel;

public class LinkRoomProtocol implements PortocolBasc {
    @Override
    public JSONObject Run(JSONObject jdata, Channel ctx) {
        JSONObject jres = new JSONObject();
        try {
            String key = jdata.getString("key");
            GameRoom room = GameRoomManager.Inst.GetRoom(key);
            if (room != null) {
                Player p = PlayerManager.GetPlayer(ctx.id().toString());
                room.PlayerAdd(p);
            }
        } catch (Exception e) {
        }
        Console.Log("TestProtocol");
        return jres;
    }
}