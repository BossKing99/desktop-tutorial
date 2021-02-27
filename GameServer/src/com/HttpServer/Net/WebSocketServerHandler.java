package com.HttpServer.Net;

import java.net.InetSocketAddress;

import com.HttpServer.Manager.PlayerManager;
import com.HttpServer.Thread.*;
import com.HttpServer.publicClass.Console;
import com.HttpServer.publicClass.PackData;

import org.json.JSONException;
import org.json.JSONObject;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
	public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	JSONObject jMsg = null;
	boolean isLogin = false;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		Channel oIncoming = ctx.channel();
		String text = "";
		// Console.Log("channelRead0 ");
		if (msg == null) {
			return;
		}
		if (msg instanceof TextWebSocketFrame) {
			// text = VEncoder.keyDecode(((TextWebSocketFrame) msg).text());
			text = ((TextWebSocketFrame) msg).text();
		} else if (msg instanceof BinaryWebSocketFrame) {
			Console.Log("收到二進制消息：" + ((BinaryWebSocketFrame) msg).content().readableBytes());
			return;
		}
		// -------------------------------------------------------------
		// get msg data
		jMsg = null;
		isLogin = false;

		if (text == "") {
			Console.Err("[channelRead0] NO msg /Channel :  " + oIncoming.toString());
			ctx.close();
			return;
		} else {
			// jMsg = new JSONObject(text);
			try {
				jMsg = new JSONObject(text);
			} catch (JSONException e) {
				e.printStackTrace();
				Console.Err("[channelRead0] Error msg : " + text + "/Channel : " + oIncoming.toString());
				// VGameObj vgRtn=new VGameObj(EMResp.AP_ER,"Data Error");
				// WebSocketServerHandler.Broadcast(null, vgRtn, ctx);
				ctx.close();
				return;
			}

		}
		PackData pack = new PackData(jMsg, ctx);
		PackThread.SetPacket(pack);
		// GameControl.getInstance().SetPacket(ctx, oMsg);

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		Console.Err("exceptionCaught : ");
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
		if (incoming != null && !channels.contains(incoming))
			channels.add(incoming);
		Console.Log("[Server] - " + incoming.remoteAddress() + " join");
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
		PlayerManager.Logout(ctx.channel().id().toString());
		if (incoming != null && channels.contains(ctx.channel()))
			channels.remove(ctx.channel());
	}

	public static Channel GetChannel(String m_ChannelID) {
		if (m_ChannelID == "no_connect")
			return null;
		for (Channel channel : WebSocketServerHandler.channels) {
			if (channel.id().toString().equals(m_ChannelID) == true) {
				if (channel.isActive()) {
					return channel;
				}

			}
		}
		return null;
	}

	public static String GetHost(String m_ChannelID) {
		if (m_ChannelID == "no_connect")
			return null;
		for (Channel channel : WebSocketServerHandler.channels) {
			if (channel.id().toString().equals(m_ChannelID) == true) {
				if (channel.isActive()) {
					InetSocketAddress insocket = (InetSocketAddress) channel.remoteAddress();
					return insocket.getAddress().getHostAddress();
				}

			}
		}
		return "NO_FIND";
	}

	public static boolean HasPlayerConnect(String m_ChannelID) {
		if (m_ChannelID == "no_connect")
			return false;
		for (Channel channel : WebSocketServerHandler.channels) {
			if (channel.id().toString().equals(m_ChannelID) == true) {

				if (channel.isActive()) {
					return true;
				}

			}
		}
		return false;
	}

	public static void Write(String data, Channel cha) {
		if (cha == null) {
			Console.Err("Cha is Null");
			return;
		}
		if (cha.isActive() && cha.isWritable()) {
			cha.writeAndFlush(new TextWebSocketFrame(data)).addListener(future -> {
				if (!future.isSuccess()) {
					future.cause().printStackTrace();
					if (cha != null)
						cha.close();
				}
			});
		} else {
			if (cha != null)
				cha.close();
		}
	}

}
