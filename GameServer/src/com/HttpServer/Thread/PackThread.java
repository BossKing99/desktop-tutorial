package com.HttpServer.Thread;

import java.util.LinkedList;
import java.util.Queue;

import com.HttpServer.Net.WebSocketServerHandler;
import com.HttpServer.Portocol.NetJson;
import com.HttpServer.Portocol.PortocolBasc;
import com.HttpServer.Portocol.TestProtocol;
import com.HttpServer.publicClass.Console;
import com.HttpServer.publicClass.PackData;
import com.HttpServer.publicClass.ProtocolName;
import com.HttpServer.publicClass.StackTraceUtil;


public class PackThread extends Thread {
    private static Queue<PackData> QuePacket = new LinkedList<PackData>();

    public static void SetPacket(PackData Netdata) {
        synchronized (QuePacket) {
            QuePacket.offer(Netdata);
        }

    }

    public void run() {
        try {
            while (true) {
                try {
                    PackData newPack = null;
                    if (QuePacket.size() != 0) {
                        synchronized (QuePacket) {
                            if (QuePacket.size() != 0)
                                newPack = QuePacket.poll();
                        }
                        if (newPack == null) {
                            Thread.sleep(10);
                        } else
                            DoPacket(newPack);
                        newPack = null;
                    } else
                        Thread.sleep(1000);
                } catch (Exception e) {
                    Console.Err("PackManager error : " + StackTraceUtil.getStackTrace(e));
                }

            }
        } catch (Exception e) {
            Console.Err("強制關閉");
        }
    }

    private void DoPacket(PackData Netdata) {
        try {
            if (Netdata == null || Netdata.ctx == null)
                return;
            int pt = Netdata.sendData.getInt("pt");
            PortocolBasc oCtrl = GetControl(pt);
            if (oCtrl == null)
                return;
            // -------------------------------------------------------------
            String msg = oCtrl.Run(Netdata.sendData).toString();
            // FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
            // HttpResponseStatus.OK,
            // Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
            // response.headers().set(HttpHeaderNames.CONTENT_TYPE,
            // "application/x-www-form-urlencoded");
            // response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            // Netdata.ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            WebSocketServerHandler.Write(NetJson.CreatePack(msg, pt),Netdata.ctx.channel());
        } catch (Exception e) {
            Console.Err("DoPacket Error e = " + StackTraceUtil.getStackTrace(e) + " protocol = " + Netdata.sendData);
        }
    }

    private PortocolBasc GetControl(int pt) {
        switch (pt) {
            case ProtocolName.TEST:
                return new TestProtocol();
            case ProtocolName.CREATE_ROOM:
                return new TestProtocol();
            default:
                break;
        }
        return null;
    }

}