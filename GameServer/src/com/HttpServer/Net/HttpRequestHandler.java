package com.HttpServer.Net;

import com.HttpServer.Thread.PackThread;
import com.HttpServer.publicClass.Console;
import com.HttpServer.publicClass.PackData;

import org.json.JSONObject;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.HttpUtil;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        JSONObject reqData = null;
        // 100 Continue
        if (HttpUtil.is100ContinueExpected(req)) {
            ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
        }
        if (req.method().equals(HttpMethod.POST)) {
            HttpPostRequestDecoder decoder;
            try {
                decoder = new HttpPostRequestDecoder(factory, req);
                Attribute data = (Attribute) decoder.next();
                reqData = new JSONObject(data.getValue());
                Console.Err("reqData = " + reqData);
            } catch (Exception e) {
                Console.Err("channelRead0 Err = " + e.toString());
                ctx.close();
            }
        }
        if (reqData == null)
            ctx.close();
        else {
            PackData pack = new PackData(reqData, ctx);
            PackThread.SetPacket(pack);
        }
    }

}