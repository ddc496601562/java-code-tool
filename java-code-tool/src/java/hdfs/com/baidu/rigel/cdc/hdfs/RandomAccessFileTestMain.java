package com.baidu.rigel.cdc.hdfs;

import java.io.RandomAccessFile;

public class RandomAccessFileTestMain {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		RandomAccessFile randomAccessFile = new RandomAccessFile("E:/randomAccessFile.txt", "rw");
		randomAccessFile.writeUTF("a");
		randomAccessFile.seek(2000);
		for(int i=0;i<1000000;i++)
			randomAccessFile.writeUTF("哈哈哈哈哈哈哈\r\n");// 这个长度写在当前文件指针的前两个字节处，可用readShort()读取
		randomAccessFile.writeUTF("又是一个UTF字符串");
		randomAccessFile.writeChar('a');// 占2个字节
		randomAccessFile.close();
	}

}
