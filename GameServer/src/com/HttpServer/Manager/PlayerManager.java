package com.HttpServer.Manager;

import java.util.HashMap;
import java.util.Map;

import com.HttpServer.Base.PlayerBase.Player;

import io.netty.channel.Channel;

public class PlayerManager {
    private static Map<String, Player> _allPlayer = new HashMap<>();

    public static void AddPlayer(Channel ctx) {
        synchronized (_allPlayer) {
            _allPlayer.put(ctx.id().toString(), new Player(ctx));
        }
    }

    public static Player GetPlayer(String id) {
        if (_allPlayer.containsKey(id))
            return _allPlayer.get(id);
        else
            return null;
    }

    public static void Logout(String id) {
        synchronized (_allPlayer) {
            if (_allPlayer.get(id).GetRoom() != null)
                _allPlayer.get(id).RmRoom();
            _allPlayer.remove(id);
        }
    }
}
