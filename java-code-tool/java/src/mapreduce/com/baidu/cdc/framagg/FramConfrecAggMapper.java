package com.baidu.cdc.framagg;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.log4j.Logger;

public class FramConfrecAggMapper implements Mapper<LongWritable, Text , Text, ConfrecByFram> {
	Logger logger = Logger.getLogger(FramConfrecAggMapper.class);
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
			OutputCollector<Text, ConfrecByFram> output, Reporter reporter)
			throws IOException {
		String[] splits=value.toString().split("	");
		if(splits.length!=4){
			logger.error("error data ,line is "+key+",data is "+value.toString());
		}
		try{
			Text framNo=new Text(splits[1]);
			ConfrecByFram confrec=new ConfrecByFram();
			confrec.reset(splits[0], Double.parseDouble(splits[2]));
			output.collect(framNo, confrec);
		}catch(NumberFormatException e){
			logger.error("parse data error,line is "+key+",data is "+value.toString());
			e.printStackTrace();
		}
	}
}
