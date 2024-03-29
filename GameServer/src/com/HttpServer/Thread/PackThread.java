package com.HttpServer.Thread;

import java.util.LinkedList;
import java.util.Queue;

import com.HttpServer.Net.WebSocketServerHandler;
import com.HttpServer.Portocol.*;
import com.HttpServer.publicClass.Console;
import com.HttpServer.publicClass.PackData;
import com.HttpServer.publicClass.ProtocolName;
import com.HttpServer.publicClass.StackTraceUtil;

import org.json.JSONObject;

public class PackThread extends Thread {
    private static Queue<PackData> QuePacket = new LinkedList<PackData>();

    public static void SetPacket(PackData Netdata) {
        synchronized (QuePacket) {
            QuePacket.offer(Netdata);
        }

    }

    @Override
    public void run() {
        while (true) {
            try {
                PackData newPack = null;
                if (QuePacket.size() != 0) {
                    synchronized (QuePacket) {
                        if (QuePacket.size() != 0)
                            newPack = QuePacket.poll();
                    }
                    if (newPack == null)
                        Thread.sleep(10);
                    else
                        doPacket(newPack);
                } else
                    Thread.sleep(50);
            } catch (Exception e) {
                Console.Err("PackManager error : " + StackTraceUtil.getStackTrace(e));
            }

        }
    }

    private void doPacket(PackData netdata) {
        try {
            if (netdata == null || netdata.ctx == null)
                return;
            int pt = netdata.sendData.getInt("pt");
            PortocolBasc oCtrl = getControl(pt);
            if (oCtrl == null)
                return;
            // -------------------------------------------------------------
            JSONObject jmsg = oCtrl.Run(netdata.sendData, netdata.ctx.channel());

            if (jmsg.length() > 0) {
                String msg = jmsg.toString();
                WebSocketServerHandler.Write(NetJson.CreatePack(msg, pt), netdata.ctx.channel());
            }
        } catch (Exception e) {
            Console.Err("DoPacket Error e = " + StackTraceUtil.getStackTrace(e) + " protocol = " + netdata.sendData);
        }
    }

    private PortocolBasc getControl(int pt) {
        switch (pt) {
            case ProtocolName.LOGIN:
                return new LoginProtocol();
            case ProtocolName.CREATE_ROOM:
                return new CreateRoomProtocol();
            case ProtocolName.LINK_ROOM:
                return new LinkRoomProtocol();
            case ProtocolName.CHOOSE:
                return new ChooseProtocol();
            case ProtocolName.PREVIEW:
                return new PreviewProtocol();
            case ProtocolName.GET_DATA:
                return new GetDataProtocol();
            case ProtocolName.READY:
                return new ReadyProtocol();
            case ProtocolName.GetCompose:
                return new GetCompose();
            case ProtocolName.Compose:
                return new Compost();
            default:
                break;
        }
        return null;
    }

}