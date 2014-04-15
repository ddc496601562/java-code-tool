package com.baidu.cdc.datacheck;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;


public class DataCheck {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String hdfsFile="D:/SecureCRT file/down/hdfs/acctContact_20120823";
		String publishFile="D:/SecureCRT file/down/publish/acctContact_20120823";
		LineNumberReader hdfsReader=new LineNumberReader(new FileReader(hdfsFile));
		LineNumberReader publishReader=new LineNumberReader(new FileReader(publishFile));
		int replaceCounter=0;
		String hdfsLine=null;
		String publishLine=null;
		while((hdfsLine=hdfsReader.readLine())!=null){
			publishLine=publishReader.readLine();
			if(hdfsLine.equals(publishLine))
				continue;
//			System.out.println(StringUtils.difference(hdfsLine, publishLine));
//			System.out.println(hdfsLine +"    " +publishLine);
			int index=-1;
//			System.out.println("the first :"+hdfsLine);
			while((index=hdfsLine.indexOf("${0x5c}"))!=-1){
				hdfsLine=hdfsLine.substring(0, index)+"\\"+hdfsLine.substring(index+7);
//				System.out.println(hdfsLine);
			}
			replaceCounter++;
			if(hdfsLine.equals(publishLine))
				continue;
			System.out.println(hdfsLine +"    " +publishLine);
		}
		publishLine=publishReader.readLine();
		if(publishLine!=null)
			System.out.println("error"+publishReader.getLineNumber());
		System.out.println("replaceCounter is "+replaceCounter);
		hdfsReader.close();
		publishReader.close();
	}

}
