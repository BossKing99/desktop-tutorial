package com.HttpServer.Portocol;

import org.json.JSONObject;

import io.netty.channel.Channel;

public interface PortocolBasc {

    abstract public JSONObject Run(JSONObject jdata, Channel ctx);

}