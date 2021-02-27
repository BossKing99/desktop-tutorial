package com.HttpServer.publicClass;

import org.json.JSONObject;

import io.netty.channel.ChannelHandlerContext;

public class PackData {
    public JSONObject sendData = null;
    public ChannelHandlerContext ctx = null;

    public PackData(JSONObject data, ChannelHandlerContext c) {
        sendData = data;
        ctx = c;
    }
}