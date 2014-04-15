package com.baidu.cdc.framagg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
/**
 * 按照用户粒度统计点击消费信息s
 * @author dingdongchao
 *
 */
public class FramConfrecAggReduce implements Reducer<Text, ConfrecByFram, Text, ConfrecByFram> {
	@Override
	public void configure(JobConf job) {
		// TODO Auto-generated method stub
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
	}
	@Override
	public void reduce(Text key, Iterator<ConfrecByFram> values,
			OutputCollector<Text, ConfrecByFram> output, Reporter reporter)
			throws IOException {
		double sumMoney=0d ;
		List<ConfrecByFram> allConfrecList=new ArrayList<ConfrecByFram>();
		while(values.hasNext()){
			ConfrecByFram conf=values.next();
			ConfrecByFram confNew=new ConfrecByFram();
			confNew.reset(conf);
			allConfrecList.add(confNew);
		}
			
		Collections.sort(allConfrecList) ;
		for(ConfrecByFram confrec :allConfrecList){
			sumMoney=sumMoney+confrec.getMoney();
			confrec.resetMoney(sumMoney);
			output.collect(key, confrec);
		}
	}
}
