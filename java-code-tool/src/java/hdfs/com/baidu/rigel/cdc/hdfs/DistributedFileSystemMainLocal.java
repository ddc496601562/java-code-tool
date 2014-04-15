package com.baidu.rigel.cdc.hdfs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.server.namenode.NameNode;

public class DistributedFileSystemMainLocal {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	public static void main(String[] args) throws IOException, URISyntaxException {
		// TODO Auto-generated method stub
		Configuration conf=new Configuration();
		conf.set("fs.default.name", "hdfs://localhost:8020/");
		DistributedFileSystem fs = (DistributedFileSystem)FileSystem.get(conf);
		FileStatus[] listFiles=fs.globStatus(new Path("/user/dingdongchao/fc_pay_data/*"));
		for(FileStatus file:listFiles){
			
		}
	}

}
