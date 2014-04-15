package com.baidu.rigel.cdc.hdfs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class RigelFileListTest {
	/**
	 * @param args
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException, URISyntaxException {
		// TODO Auto-generated method stub
		String rootPath=args[0];
		if(rootPath==null||"".equals(rootPath))
			System.exit(0);
		FileSystem fs =FileSystem.get(new URI("hdfs://nj01-yulong-namespace.dmop.baidu.com:54310"), new Configuration());
		int num=RigelFileListTest.getFileNum(fs, new Path(rootPath));
		System.out.println("all file num is "+num);
	}
	public static int getFileNum(FileSystem fs ,Path path ) throws IOException{
		int num= 0;
		FileStatus[] files=fs.listStatus(path);
		for(FileStatus file:files){
			if(file.isDir()){
				num=num+RigelFileListTest.getFileNum(fs, file.getPath());
			}else{
				num=num+1;
			}
		}
		return num;
	}
	

}
