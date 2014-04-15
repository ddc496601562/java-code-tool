package com.baidu.rigel.cdc.hdfs;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReadTestMain {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File file=new File("E:/fstime");
		DataInputStream in=new DataInputStream(new FileInputStream(file));
		long timeLong=in.readLong();
		SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd HH:mm");
		System.out.println("2012-05-23 17:30  "+df.format(new Date(timeLong)));
	}

}
