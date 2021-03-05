package com.HttpServer.Base.PlayerBase;

import com.HttpServer.Base.GameRoom;
import com.HttpServer.Net.WebSocketServerHandler;

import io.netty.channel.Channel;

public class Player implements IPlayer {
    private Channel _ctx = null;
    private GameRoom _room = null;

    public Player(Channel ctx) {
        _ctx = ctx;
    }

    public GameRoom GetRoom() {
        return _room;
    }
    public void RmRoom() {
         _room.RmPlayer(this);
         _room = null;
    }
    @Override
    public void Write(String data) {
        WebSocketServerHandler.Write(data, _ctx);
    }

    @Override
    public String GetCtxId() {
        return _ctx.id().toString();
    }
}
