package com.baidu.excel;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class FCShowAllDataCheckFromat {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
		SimpleDateFormat sf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		LineNumberReader hdfsReader=new LineNumberReader(new FileReader("E:/downs/fc_allcmatch_display_abnor_halfhr_20120919_1130_002350"));
		String line =null;
		while ((line=hdfsReader.readLine())!=null){
			String[] sons=line.split("	");
			if(sons.length==32){
				System.out.println(sons[29]+"---"+sons[30]+"---"+sons[31]);
			}
				
		}
		hdfsReader.close();
	}

}
