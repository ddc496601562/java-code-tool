package com.baidu.rigel.cdc.hdfs;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.UTF8;

public class FSImageFileTestMain {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		UTF8 U_STR = new UTF8();
		short replication ;
		DataInputStream in=new DataInputStream(new FileInputStream("/fsimage"));
		System.out.println("image vision="+in.readInt());
		System.out.println("namespaceId="+in.readInt());
		long numFile=in.readLong();
		System.out.println("numFile="+numFile);
		System.out.println("genstamp is :"+in.readLong());
		for(int i=0;i<numFile;i++){
			long modificationTime = 0;
	        long atime = 0;
	        long blockSize = 0;
	        String path =U_STR.readString(in).toString();
	        System.out.println(path);
	        replication = in.readShort();
	        modificationTime = in.readLong();
	        atime = in.readLong();
	        blockSize = in.readLong();
	        int numBlocks = in.readInt();
		}
		in.close();
	}

}
