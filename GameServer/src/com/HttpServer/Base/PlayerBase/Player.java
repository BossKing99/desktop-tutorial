package com.HttpServer.Base.PlayerBase;

import com.HttpServer.Net.WebSocketServerHandler;

import io.netty.channel.Channel;

public class Player {
    private Channel _ctx = null;

    public Player(Channel ctx) {
        _ctx = ctx;
    }

    public void Write(String data) {
        WebSocketServerHandler.Write(data, _ctx);
    }

}
