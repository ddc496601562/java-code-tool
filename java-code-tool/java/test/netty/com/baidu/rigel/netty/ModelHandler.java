package com.baidu.rigel.netty;

import static org.jboss.netty.buffer.ChannelBuffers.dynamicBuffer;  

  
import org.jboss.netty.buffer.ChannelBuffer;  
import org.jboss.netty.channel.Channel;  
import org.jboss.netty.channel.ChannelFuture;  
import org.jboss.netty.channel.ChannelFutureListener;  
import org.jboss.netty.channel.ChannelHandlerContext;  
import org.jboss.netty.channel.ChannelStateEvent;  
import org.jboss.netty.channel.Channels;  
import org.jboss.netty.channel.MessageEvent;  
import org.jboss.netty.channel.SimpleChannelHandler;  
import org.jboss.netty.handler.codec.frame.FrameDecoder;  
  
/** 
 * 用POJO代替ChannelBuffer 
 */  
  
class TimeServerHandler3 extends SimpleChannelHandler {    
        
    @Override    
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)    
            throws Exception {    
        Persons person = new Persons("周杰伦123",31,10000.44);  
        ChannelFuture future = e.getChannel().write(person);    
        future.addListener(ChannelFutureListener.CLOSE);    
    }    
}    
  
class TimeClientHandler3 extends SimpleChannelHandler{    
        
    @Override    
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)    
            throws Exception {    
        Persons person = (Persons)e.getMessage();    
        System.out.println(person);    
        e.getChannel().close();    
    }    
}  
  
/** 
 * FrameDecoder and ReplayingDecoder allow you to return an object of any type. 
 *  
 */  
class TimeDecoder extends FrameDecoder {    
    private final ChannelBuffer buffer = dynamicBuffer();  
        
    @Override    
    protected Object decode(ChannelHandlerContext ctx, Channel channel,    
            ChannelBuffer channelBuffer) throws Exception {    
        if(channelBuffer.readableBytes()<4) {    
            return null;    
        }    
        if (channelBuffer.readable()) {  
            // 读到,并写入buf  
            channelBuffer.readBytes(buffer, channelBuffer.readableBytes());  
        }  
        int namelength = buffer.readInt();  
        String name = new String(buffer.readBytes(namelength).array(),"GBK");  
        int age = buffer.readInt();  
        double salary = buffer.readDouble();  
        Persons person = new Persons(name,age,salary);  
        return person;    
    }    
    
}    
  
class TimeEncoder extends SimpleChannelHandler {    
    private final ChannelBuffer buffer = dynamicBuffer();  
      
    @Override    
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)    
            throws Exception {    
        Persons person = (Persons)e.getMessage();    
        buffer.writeInt(person.getName().getBytes("GBK").length);  
        buffer.writeBytes(person.getName().getBytes("GBK"));  
        buffer.writeInt(person.getAge());  
        buffer.writeDouble(person.getSalary());  
        Channels.write(ctx, e.getFuture(), buffer);    
    }    
}  
  
class Persons{  
    private String name;  
    private int age;  
    private double salary;  
      
    public Persons(String name,int age,double salary){  
        this.name = name;  
        this.age = age;  
        this.salary = salary;  
    }  
      
    public String getName() {  
        return name;  
    }  
    public void setName(String name) {  
        this.name = name;  
    }  
    public int getAge() {  
        return age;  
    }  
    public void setAge(int age) {  
        this.age = age;  
    }  
    public double getSalary() {  
        return salary;  
    }  
    public void setSalary(double salary) {  
        this.salary = salary;  
    }  
  
    @Override  
    public String toString() {  
        return "Persons [name=" + name + ", age=" + age + ", salary=" + salary  
                + "]";  
    }  
      
      
}  