package com.HttpServer.Base.PlayerBase;

public abstract class PlayerDecorator implements IPlayer {
    // player的裝飾者模式
    private Player _player;

    public PlayerDecorator(Player p) {
        _player = p;
    }
    
    @Override
    public void Write(String data) {
        _player.Write(data);
    }
}
