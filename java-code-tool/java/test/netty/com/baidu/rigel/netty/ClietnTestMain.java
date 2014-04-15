package com.baidu.rigel.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

public class ClietnTestMain {

	public static void main(String[] args) {
		// 创建客户端channel的辅助类,发起connection请求
		ClientBootstrap bootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));
		// It means one same HelloWorldClientHandler instance is going to handle
		// multiple Channels and consequently the data will be corrupted.
		// 基于上面这个描述，必须用到ChannelPipelineFactory每次创建一个pipeline
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("decoder", new StringDecoder());
				pipeline.addLast("encoder", new StringEncoder());
				pipeline.addLast("handler", new HelloWorldClientHandler());
				return pipeline;
			}
		});
		// 创建无连接传输channel的辅助类(UDP),包括client和server
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(
				"localhost", 8080));
		future.getChannel().getCloseFuture().awaitUninterruptibly();
		bootstrap.getFactory().releaseExternalResources();
		bootstrap.releaseExternalResources();

	}
}
