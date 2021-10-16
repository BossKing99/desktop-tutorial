package com.HttpServer.Portocol;

import com.HttpServer.Base.HPBanRoom;
import com.HttpServer.publicClass.Console;
import com.HttpServer.publicClass.HPData;
import com.HttpServer.publicClass.LOLMData;

import org.json.JSONObject;

import io.netty.channel.Channel;

public class GetDataProtocol implements PortocolBasc {
    @Override
    public JSONObject Run(JSONObject jdata, Channel ctx) {
        try {
            Console.Log(jdata.getInt("gameType")+"");
            switch (jdata.getInt("gameType")) {
                case 1:
                    return LOLMData.GetJData();
                case 2:
                    return HPData.GetJData();
                default:
                    return null;
            }
        } catch (Exception e) {
            Console.Log(e.toString());
            return null;
        }

    }
}