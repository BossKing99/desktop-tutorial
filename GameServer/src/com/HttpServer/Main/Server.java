package com.HttpServer.Main;

import com.HttpServer.Base.GameRoom;
import com.HttpServer.Manager.GameRoomManager;
import com.HttpServer.Manager.ThreadManager;
import com.HttpServer.Net.Client2GameInitializer;
import com.HttpServer.Net.HttpServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class Server {
	public static Server mGame = null;
	public void bind(int port) throws Exception { // 使用netty啟動server
		// WebSocketServerHandler.Init();
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO)) // server 線程設定
					.childHandler(new Client2GameInitializer()) // Client Socket線程設定
					.option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
			Channel ch = b.bind(port).sync().channel();
			ch.closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		ThreadManager.Init();
		// HttpServer server = new HttpServer(8081);// 8081為監聽Port
		// server.start();
		Server.mGame = new Server();
		Server.mGame.bind(8083);
	}
}