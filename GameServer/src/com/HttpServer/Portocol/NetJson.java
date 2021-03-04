package com.HttpServer.Portocol;

import com.HttpServer.publicClass.Console;

import org.json.JSONObject;


public class NetJson {
    public static JSONObject ErrorJson(String errorcode) {
        JSONObject rdata = new JSONObject();
        try {
            rdata.put("ErrorCode", errorcode);
        } catch (Exception e) {
        }
        return rdata;
    }
    public static String CreatePack(String jdata, int protocol) {
        try {
            JSONObject rdata = new JSONObject();
            rdata.put("pt", protocol);
            rdata.put("data", jdata);
            return rdata.toString();
        } catch (Exception e) {
            Console.Err("jdata = "+ jdata);
            Console.Err("protocol = "+ protocol);
        }
       return null;
    }
}