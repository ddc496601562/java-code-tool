package com.baidu.excel;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;


public class DataCheck2 {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String hdfsFile="D:/SecureCRT file/down/account_20120826.txt";
		LineNumberReader hdfsReader=new LineNumberReader(new FileReader(hdfsFile));
	    String line = null;
	    int i=0 ;
	    while((line=hdfsReader.readLine())!=null){
	    	String[] splits=line.split("	");
	    	System.out.println(i++ +"  "+splits[20]+"   "+splits[21]);
	    	if(splits.length!=22)
	    		System.out.println(i++ +"  "+splits.length+"   "+line);
	    }
	}

}
