package com.HttpServer.Manager;

import java.util.HashMap;
import java.util.Map;

import com.HttpServer.Base.Player;

import io.netty.channel.Channel;

public class PlayerManager {
    private static Map<String, Player> _allPlayer = new HashMap<>();

    public static void AddPlayer(Channel ctx) {
        _allPlayer.put(ctx.id().toString(), new Player(ctx));
    }

    public static void Logout(String id) {
        _allPlayer.remove(id);
    }
}
