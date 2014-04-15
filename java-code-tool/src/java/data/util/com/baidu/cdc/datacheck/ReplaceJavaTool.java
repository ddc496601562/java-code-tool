package com.baidu.cdc.datacheck;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;

public class ReplaceJavaTool {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		LineNumberReader fromFile=new LineNumberReader(new FileReader("D:/SecureCRT file/down/hive_crm.sql"));
		PrintStream out=new PrintStream("D:/SecureCRT file/down/hive_crm_replace.sql");
		String hdfsLine=null;
		int i=0 ;
		while((hdfsLine=fromFile.readLine())!=null){
			i++;
			hdfsLine=hdfsLine.replaceAll("TYPE=", "ENGINE=");
			if(i==486){
				System.out.println(hdfsLine);
				hdfsLine=hdfsLine.substring(0, hdfsLine.length()-1)+";";
				System.out.println(hdfsLine);
			}
				
			out.println(hdfsLine);
		}
		System.out.println(i);
		fromFile.close();
		out.close();
	}

}
