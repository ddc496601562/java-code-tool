package com.baidu.rigel.cdc.hdfs;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HftpTestMain {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Configuration conf=new Configuration();
		URI uri=new URI("hftp://nj01-yulong-hdfs.dmop.baidu.com:8070/app/ecom/rigelci/hive/warehouse/cdc_dm.db/dm_agent_customer/dt=2013-05-20");
		FileSystem fs = FileSystem.get(uri, conf);
		FileStatus[]  dirs=fs.listStatus(new Path("/app/ecom/rigelci/hive/warehouse/cdc_dm.db/dm_agent_customer/dt=2013-05-20/"));
		for(FileStatus file:dirs){
			System.out.println(file.getPath().getName());
		}
		FSDataInputStream in=fs.open(new Path("/app/ecom/rigelci/hive/warehouse/cdc_dm.db/dm_agent_customer/dt=2013-05-20/000000_0"));
		LineNumberReader lineReader=new LineNumberReader(new InputStreamReader(in));
		String line=null ;
		while((line=lineReader.readLine())!=null){
//			System.out.println(line);
		}
		lineReader.close();
		
	}

}
