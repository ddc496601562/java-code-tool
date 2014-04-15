package com.baidu.rigel.netty;


import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

class HelloWorldServerHandler extends SimpleChannelHandler {  
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)  
            throws Exception {  
        e.getChannel().write("Hello, pppppp");  
    }  
  
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {  
        System.out.println("Unexpected exception from downstream."  
                + e.getCause());  
        e.getChannel().close();  
    }  
}  
  
class HelloWorldClientHandler extends SimpleChannelHandler {  
  
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {  
        String message = (String) e.getMessage();  
        System.out.println(message);  
        e.getChannel().close();  
    }  
  
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {  
        System.out.println("Unexpected exception from downstream."  
                + e.getCause());  
        e.getChannel().close();  
    }  
}  
