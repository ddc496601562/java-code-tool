package com.baidu.cdc.clickpay;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.log4j.Logger;

public class ClickStaticMapper implements Mapper<LongWritable, Text , IntWritable, ClickInfo> {
	Logger logger = Logger.getLogger(ClickStaticMapper.class);
	@Override
	public void configure(JobConf job) {
		// TODO Auto-generated methockjd stub
	}
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
	}
	@Override
	public void map(LongWritable key, Text value,
			OutputCollector<IntWritable, ClickInfo> output, Reporter reporter)
			throws IOException {
		//每行的数据such as ：699202	201	0	1	9.95	9.92	2012-07-05
		String[] splits=value.toString().split("	");
		if(splits.length!=7){
			logger.error("error data ,line is "+key+",data is "+value.toString());
		}
		try{
			IntWritable useId=new IntWritable(Integer.parseInt(splits[0]));
			ClickInfo clientInfo=new ClickInfo();
			clientInfo.reset(Integer.parseInt(splits[3]), Float.parseFloat(splits[4]),Float.parseFloat(splits[5]));
			output.collect(useId, clientInfo);
		}catch(NumberFormatException e){
			logger.error("parse data error,line is "+key+",data is "+value.toString());
		}
	}
}
