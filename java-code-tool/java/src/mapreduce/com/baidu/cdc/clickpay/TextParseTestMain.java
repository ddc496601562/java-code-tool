package com.baidu.cdc.clickpay;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.StringTokenizer;

public class TextParseTestMain {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		LineNumberReader lineReader=new LineNumberReader(new FileReader("E:/资料/fcFcPay_20120705_part1"));
		String line=null;
		while((line=lineReader.readLine())!=null){
			String[] splits=line.split("	");
			System.out.println(Integer.parseInt(splits[0])+"  "+Integer.parseInt(splits[3])+"  "+Float.parseFloat(splits[4])+"  "+Float.parseFloat(splits[5]));
		}
	}

}
