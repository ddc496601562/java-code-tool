package com.baidu.rigel.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

public class ModelClient {

	public static void main(String[] args) {
		//创建客户端channel的辅助类,发起connection请求   
        ClientBootstrap bootstrap = new ClientBootstrap(  
                new NioClientSocketChannelFactory(  
                        Executors.newCachedThreadPool(),  
                        Executors.newCachedThreadPool()));  
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {  
            public ChannelPipeline getPipeline() {  
                ChannelPipeline pipeline =  Channels.pipeline();  
                pipeline.addLast("decoder", new TimeDecoder());  
                pipeline.addLast("encoder", new TimeEncoder());  
                pipeline.addLast("handler", new TimeClientHandler3());  
                return pipeline;  
            }  
        });  
        //创建无连接传输channel的辅助类(UDP),包括client和server  
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(  
                "localhost", 9999));  
        future.getChannel().getCloseFuture().awaitUninterruptibly();  
        bootstrap.releaseExternalResources();  

	}

}
