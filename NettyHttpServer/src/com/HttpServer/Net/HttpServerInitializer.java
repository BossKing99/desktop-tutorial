package com.HttpServer.Net;

//import java.nio.charset.Charset;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel>{

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new HttpServerCodec());// http 編解碼
        pipeline.addLast("httpAggregator",new HttpObjectAggregator(512*1024)); // 512*1024為接收的最大contentlength
        pipeline.addLast(new HttpRequestHandler());// 請求處理器

    }
}   