package com.baidu.rigel.cdc.serverlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
public class FileTailServlet extends HttpServlet {
	private static final long serialVersionUID = -5274807172809748937L;
	  @Override
	  public void doGet(HttpServletRequest request, 
	                    HttpServletResponse response
	                    ) throws ServletException, IOException {
		String fileName = null;
		long start = 0;
		long end =1024*1024*3;
		fileName=request.getParameter("fileName");
		if(fileName==null||"".equals(fileName))
			fileName=File.separator;
		String sLogOff = request.getParameter("start");
		if (sLogOff != null) {
			start = Long.valueOf(sLogOff).longValue();
		}
		String sLogEnd = request.getParameter("end");
		if (sLogEnd != null) {
			end = Long.valueOf(sLogEnd).longValue();
		}
		OutputStream byteOut= response.getOutputStream();
		PrintWriter StringOut = new PrintWriter(byteOut);
		
	    if(fileName==null||!new File(fileName).exists()){
	    	StringOut.write("file (name:"+fileName+") not exists !");
	    }else if(new File(fileName).isDirectory()){
	    	byteOut.write(("<html>\n" +
	                 "<title>tail file "+fileName+"</title>\n" +
	                 "<body>\n" +
	                 "<h1>"+fileName+"</h1><br>\n").getBytes()); 
	    	File dir=new File(fileName);
	    	StringOut.write("list file/dir is :<br>");
	    	for(File file:dir.listFiles()){
	    		StringOut.write("<a href=\""+"../filetail?fileName="+file.getAbsolutePath()+"\"><B>"+file.getName()+"</B></a>");
	    		StringOut.write("<BR>");
	    	}
	    	 byteOut.write("</body></html>\n".getBytes());
	    }else{
	    	File f=new File(fileName);
	    	if(start<=0)
	    		start=f.length()-1024*1024*3;
	    	if(end==0)
	    		end=f.length();
	    	start=Math.max(0, start);
	    	end=Math.min(end, f.length() );
	    	FileInputStream fis = new FileInputStream(f);
	    	fis.skip(start);
	        byte[] b = new byte[65536];
	        int result;
			while (true) {
				result = fis.read(b);
				if (result > 0) {
					byteOut.write(b, 0, result);
				} else {
					break;
				}
			}
			fis.close();
	    }
	   
	    StringOut.close();
	    byteOut.close();	
	    
	  }
}
