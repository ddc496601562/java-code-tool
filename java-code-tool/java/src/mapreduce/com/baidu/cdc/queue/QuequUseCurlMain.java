package com.baidu.cdc.queue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QuequUseCurlMain {
	public static SimpleDateFormat yyyyMMddHHmm=new SimpleDateFormat("yyyyMMdd HH:mm"); 
	public static void main(String[] args)  {
		while(true){
			try {
				getUsed();
				Thread.sleep(60*1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public static void getUsed() throws Exception{
		ProcessBuilder builder = new ProcessBuilder(new String[]{"curl","http://nmg01-khan-abaci.dmop.baidu.com:8030/metamaster.jsp"});
		Process process=builder.start();
		BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		BufferedReader outReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String date=yyyyMMddHHmm.format(new Date());
		Thread.sleep(2000);
		float cpu=0 ;
		float mem=0;
		float disk=0 ;
		String line=null ;
		String queue=null ;
		while((line=outReader.readLine())!=null){
			if(line.startsWith("<td><a href=\"jobqueue.jsp?queueName=")){
				String queueName=line.replace("<td><a href=\"jobqueue.jsp?queueName=", "").replace("</a></td>", "");
				queueName=queueName.substring(queueName.indexOf("\">")+2);
				queue=outReader.readLine();
//				System.out.println(line);
//				System.out.println(queue);
				queue=queue.replace("<td>CPU used ", "");
				int index=queue.indexOf("%,") ;
				cpu=Float.parseFloat(queue.substring(0,index));
				queue=queue.substring(index+2);
				queue=queue.replace(" MEMORY used ","") ;
				index=queue.indexOf("%,") ;
				mem=Float.parseFloat(queue.substring(0,index));
				queue=queue.substring(index+2);
				queue=queue.replace(" DISK used ","") ;
				index=queue.indexOf("%<") ;
				disk=Float.parseFloat(queue.substring(0,index));
				System.out.println(date+"\t"+queueName+"\t"+cpu+"\t"+mem+"\t"+disk);
//				System.out.println("cpu "+cpu+" ,mem "+mem+" ,disk "+disk);
//				System.out.println("cpu "+cpu+" ,mem "+mem+" ,disk "+disk);
			}
		}
		errReader.close();
		outReader.close();
	}
}
