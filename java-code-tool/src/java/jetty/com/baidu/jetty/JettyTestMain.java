package com.baidu.jetty;

import com.baidu.jetty.serverlet.FileTailServlet;

public class JettyTestMain {
	public static void main(String[] args) throws Exception {
		String add="0.0.0.0";
		int port=80;
		String webAppDir=System.getProperties().getProperty("user.home");
		if(args!=null&&args.length>0)
			add=args[0];
		if(args!=null&&args.length>1)
			port=Integer.parseInt(args[1]);
		if(args!=null&&args.length>2)
			webAppDir=args[2];
		JettyHttpServer server =new JettyHttpServer("local file tail tool http",add,port,webAppDir);
		server.addServlet("filetail","/filetail", FileTailServlet.class);
		server.setThreads(2, 4);
		server.start();
	}
}
