package com.baidu.rigel.cdc;


import javax.servlet.http.HttpServlet;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.QueuedThreadPool;

import com.baidu.rigel.cdc.serverlet.StackServlet;

public class JettyHttpServer {
	  private final Server webServer;
	  private final Connector listener;
	  private final WebAppContext webAppContext;
	  private final String name ;
	  private final String bindAddress ;
	  private final int port ;
	  private final String warDir ;
	  public JettyHttpServer(String name, String bindAddress, int port ,String warDir){
		  if(bindAddress==null||"".equals(bindAddress))
			  bindAddress="0.0.0.0";
		  if(warDir==null||"".equals(warDir))
			  warDir="/webapps" ;
		  this.name=name;
		  this.bindAddress=bindAddress;
		  this.port=port ;
		  this.warDir=warDir;
		  webServer = new Server();
		  
		  SelectChannelConnector ret = new SelectChannelConnector();
		  ret.setLowResourceMaxIdleTime(10000);
		  ret.setAcceptQueueSize(128);
		  ret.setResolveNames(false);
		  ret.setUseDirectBuffers(false);
		  listener=ret;
		  listener.setHost(this.bindAddress);
		  listener.setPort(this.port);
		  webServer.addConnector(listener);
		  QueuedThreadPool queue=new QueuedThreadPool();
		  queue.setMaxThreads(2);
		  queue.setMinThreads(1);
		  webServer.setThreadPool(queue);
		  
		  ContextHandlerCollection contexts = new ContextHandlerCollection();
		  webServer.setHandler(contexts);
		  
		  webAppContext = new WebAppContext();
		  webAppContext.setDisplayName("WepAppsContext");
		  webAppContext.setContextPath("/");
		  webAppContext.setWar(this.warDir);
		  webServer.addHandler(webAppContext);
		  
		  this.addServlet("stacks", "/stacks", StackServlet.class);
	  }
	  public void addServlet(String name, String pathSpec,  Class<? extends HttpServlet> clazz){
		  ServletHolder holder = new ServletHolder(clazz);
		  if (name != null) {
			  holder.setName(name);
		  }
		  webAppContext.addServlet(holder, pathSpec);
	  }
	  public void setAttribute(String name, Object value) {
		  webAppContext.setAttribute(name, value);
	  }
	  public void start() throws Exception  {
		  webServer.start();
	  }
	  public void stop() throws Exception {
		  listener.close();
		  webServer.stop();
	  }
	  public void setThreads(int min, int max) {
		  QueuedThreadPool pool = (QueuedThreadPool) webServer.getThreadPool();
		  pool.setMinThreads(min);
		  pool.setMaxThreads(max);
	  }
	  public String getName() {
		return name;
	  }
	public static Connector createDefaultChannelConnector() {
		  SelectChannelConnector ret = new SelectChannelConnector();
		  ret.setLowResourceMaxIdleTime(10000);
		  ret.setAcceptQueueSize(128);
		  ret.setResolveNames(false);
		  ret.setUseDirectBuffers(false);
		  return ret;
	  }
}
