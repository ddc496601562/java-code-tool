package com.baidu.rigel.cdc.hdfs;

import java.io.File;

public class PermissTestMain {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File f=new File("E:/tmp/hadoop-dingdongchao/mapred/local/ttprivate");
		boolean isSuccess=f.setReadable(false, false);
		System.out.println(isSuccess);
	}

}
