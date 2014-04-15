package com.baidu.cdc.clickpay;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;
/**
 * 单个用户的消费记录
 * @author dingdongchao
 */
public class ClickInfo implements Writable {
	private IntWritable     clickCounter = new IntWritable();    //用户点击次数
	private FloatWritable   preConsumption =new FloatWritable();   //用户消费金额
	private FloatWritable   tureConsumptionMoney=new FloatWritable();//用户实际消费金额
	public void reset(int clickCounter,float preConsumption,float tureConsumptionMoney){
		this.clickCounter.set(clickCounter);
		this.preConsumption.set(preConsumption);
		this.tureConsumptionMoney.set(tureConsumptionMoney);
	}
	@Override
	public  void write(DataOutput out) throws IOException {
		this.clickCounter.write(out);
		this.preConsumption.write(out);
		this.tureConsumptionMoney.write(out);
		
	}
	@Override
	public void readFields(DataInput in) throws IOException {
		this.clickCounter.readFields(in);
		this.preConsumption.readFields(in);
		this.tureConsumptionMoney.readFields(in);
	}
	@Override
	public String toString(){
		return this.clickCounter+"\t"+this.preConsumption+"\t"+this.tureConsumptionMoney;
	}
	public int getClientCounter() {
		return this.clickCounter.get();
	}
	public float getPreConsumption() {
		return this.preConsumption.get();
	}
	public float getTureConsumptionMoney() {
		return this.tureConsumptionMoney.get();
	}
	
}
