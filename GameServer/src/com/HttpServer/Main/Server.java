package com.HttpServer.Main;

import com.HttpServer.Manager.ThreadManager;
import com.HttpServer.Net.Client2GameInitializer;
import com.HttpServer.publicClass.CheckPassword;
import com.HttpServer.publicClass.Console;
import com.HttpServer.publicClass.LOLMData;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

// import java.io.File;
// import java.io.FileReader;
// import java.util.Scanner;

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
		Console.Log("GO");
		// try {
		// 	FileWriter fw = new FileWriter("ssh/filename.txt");
		// 	fw.flush();
		// 	fw.close();
		// 	File file2 = new File("./ssh");
		// 	listDir(file2);
		// } catch (Exception e) {

		// 	Console.Log("Error r = " + e);
		// }
		// test();

		CheckPassword.Check("ss");
		LOLMData.LoadData();
		ThreadManager.Init();
		Server.mGame = new Server();
		Server.mGame.bind(8083);
	}

	// private static void test() {
	// 	try {
	// 		FileReader bmifile = new FileReader("ssh/KEY");
	// 		Scanner inf = new Scanner(bmifile);
	// 		System.out.printf(inf.next() + "\n");
	// 		bmifile.close();
	// 	} catch (Exception e) {

	// 		Console.Log("Error r = " + e);
	// 	}

	// }

	// public static void listDir(File file) {
	// 	if (file.isDirectory()) { // 是一個目錄
	// 		// 列出目錄中的全部內容
	// 		File results[] = file.listFiles();
	// 		if (results != null) {
	// 			for (int i = 0; i < results.length; i++) {
	// 				System.out.println(results[i].getName());
	// 			}
	// 		}
	// 	} else {
	// 		System.out.println(file.getName());
	// 	}

	// }
}