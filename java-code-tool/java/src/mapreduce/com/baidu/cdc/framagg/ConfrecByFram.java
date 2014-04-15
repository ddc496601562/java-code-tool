package com.baidu.cdc.framagg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
/**
 * 单个用户的消费记录
 * @author dingdongchao
 */
public class ConfrecByFram implements Writable , Comparable<ConfrecByFram> {
	private Text   date = new Text();    //用户点击次数
	private DoubleWritable   money = new DoubleWritable();    //用户点击次数
	public void reset(String  date,double money ){
		this.date.set(date);
		this.money.set(money);
	}
	public void reset(ConfrecByFram confrec ){
		this.date.set(confrec.date.toString());
		this.money.set(confrec.money.get());
	}
	public void resetMoney(double money ){
		this.money.set(money);
	}
	@Override
	public  void write(DataOutput out) throws IOException {
		this.date.write(out);
		this.money.write(out);
		
	}
	@Override
	public void readFields(DataInput in) throws IOException {
		this.date.readFields(in);
		this.money.readFields(in);
	}
	public double getMoney(){
		return this.money.get();
	}
	@Override
	public String toString(){
		return this.date+"\t"+this.money ;
	}
	@Override
	public int compareTo(ConfrecByFram o) {
		return this.date.toString().compareTo(o.date.toString());
	}
}
