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
            int team = jdata.getInt("team");
            String pass = "";
            if (team != -1)
                pass = jdata.getString("pass");
            GameRoom room = GameRoomManager.Inst.GetRoom(key);

            if (room != null) {
                if (team == -1 || pass.equals(room.GetRoomPass()[team])) {
                    Player p = PlayerManager.GetPlayer(ctx.id().toString());
                    if (p == null) {
                        Console.Err("LinkRoomProtocol Player is null ");
                        return jres;
                    }
                    room.PlayerAdd(p, team);
                    jres.put("resCode", 0);
                    Console.Log(room.GetRoomInfo().toString());
                    jres.put("info", room.GetRoomInfo());
                } else
                    jres.put("resCode", 1);
            } else
                jres.put("resCode", 1);
        } catch (Exception e) {
            Console.Err("Error LinkRoomProtocol e =" + e);
        }
        return jres;
    }
}