package com.HttpServer.Main;

import com.HttpServer.Net.HttpServer;

public class Server {

    public static void main(String[] args) throws Exception{
        HttpServer server = new HttpServer(8081);// 8081為監聽Port
        server.start();
    }
}