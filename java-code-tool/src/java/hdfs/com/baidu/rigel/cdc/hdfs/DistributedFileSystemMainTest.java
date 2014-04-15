package com.baidu.rigel.cdc.hdfs;

import java.io.IOException;
import java.io.LineNumberReader;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;

public class DistributedFileSystemMainTest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		URI NAME = URI.create("file:///");
		System.out.println(NAME.getScheme());
		System.out.println(NAME.getAuthority());
		// TODO Auto-generated method stub
		String uri = "hdfs://10.0.0.21:9000";
		DistributedFileSystem fs = (DistributedFileSystem)FileSystem.get(URI.create(uri), new Configuration());
		Path pathPattern=new Path("/user/hadoop/archive/news/20110808/20110808235830/content/part-00000/data");
		FSDataInputStream fsInputStream=fs.open(pathPattern, 1024*64);
	}

}
