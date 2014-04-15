package com.baidu.rigel.netty;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class ClientHandler extends SimpleChannelUpstreamHandler  {
    @Override    
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)    
            throws Exception {   
        ChannelFuture future = e.getChannel().write("test dingdongchao!!!!"); 
        System.out.println("test dingdongchao!!!!");
        future.addListener(ChannelFutureListener.CLOSE);    
    } 
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		ChannelBuffer buffer = (ChannelBuffer) e.getMessage();
		System.out.println("client received message is :  "+ buffer.toString(Charset.defaultCharset()));
		e.getChannel().close();
	}
}
