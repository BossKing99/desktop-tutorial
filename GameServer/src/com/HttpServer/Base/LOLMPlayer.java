package com.HttpServer.Base;

import com.HttpServer.Base.PlayerBase.Player;
import com.HttpServer.Base.PlayerBase.PlayerDecorator;

public class LOLMPlayer extends PlayerDecorator {
    public int team = -1;
    public LOLMPlayer(Player p) {
        super(p);
    }
}
