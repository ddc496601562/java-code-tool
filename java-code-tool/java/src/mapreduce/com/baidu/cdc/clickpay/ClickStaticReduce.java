package com.baidu.cdc.clickpay;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
/**
 * 按照用户粒度统计点击消费信息s
 * @author dingdongchao
 *
 */
public class ClickStaticReduce implements Reducer<IntWritable, ClickInfo, IntWritable, ClickInfo> {
	@Override
	public void configure(JobConf job) {
		// TODO Auto-generated method stub
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
	}
	@Override
	public void reduce(IntWritable key, Iterator<ClickInfo> values,
			OutputCollector<IntWritable, ClickInfo> output, Reporter reporter)
			throws IOException {
		int clickCounter=0;
		float preConsumption=0.0f;
		float tureConsumptionMoney=0.0f;
		/***
		 * 累加单个用户的点击消费
		 */
		while(values.hasNext()){
			ClickInfo curret=values.next();
			clickCounter=clickCounter+curret.getClientCounter();
			preConsumption=preConsumption+curret.getPreConsumption();
			tureConsumptionMoney=tureConsumptionMoney+curret.getTureConsumptionMoney();
		}
		ClickInfo clientStatic=new ClickInfo();
		clientStatic.reset(clickCounter, preConsumption, tureConsumptionMoney);
		output.collect(key, clientStatic);
	}
}
