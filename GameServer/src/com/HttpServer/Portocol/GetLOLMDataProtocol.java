package com.HttpServer.Portocol;

import com.HttpServer.publicClass.LOLMData;

import org.json.JSONObject;

import io.netty.channel.Channel;

public class GetLOLMDataProtocol implements PortocolBasc {
    @Override
    public JSONObject Run(JSONObject jdata, Channel ctx) {
        return LOLMData.GetJData();
    }
}