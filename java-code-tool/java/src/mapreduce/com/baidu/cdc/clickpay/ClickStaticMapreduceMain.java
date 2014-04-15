package com.baidu.cdc.clickpay;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.lib.NLineInputFormat;
import org.apache.hadoop.mapred.FileInputFormat;
/**
 * 按照用户粒度统计点击消费信息的mapreduce入口main程序
 * @author dingdongchao
 *
 */
public class ClickStaticMapreduceMain {
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// TODO Auto-generated method stub
		String Usage="Usage:inputPath outputh linespermap [mapCounter reduceCounter]";
		if(!checkArgsSuccess(args)){
			System.out.println(Usage);
			System.exit(0);
		}
		JobConf jobconf=new JobConf(new Configuration());
		jobconf.setJarByClass(ClickStaticMapreduceMain.class);
		System.out.println(jobconf.get("mapred.jar"));
		jobconf.setJobName("click static for ddc test");
		FileInputFormat.setInputPaths(jobconf, new Path(args[0]));
		FileOutputFormat.setOutputPath(jobconf, new Path(args[1]));
		if(args.length==4){
			jobconf.setNumMapTasks(Integer.parseInt(args[3]));
		}
		if(args.length==5){
			jobconf.setNumReduceTasks(Integer.parseInt(args[4]));
		}
		//设置10000行一个map
		jobconf.set("mapred.line.input.format.linespermap", args[2]);
		jobconf.setMapOutputKeyClass(IntWritable.class);
		jobconf.setMapOutputValueClass(ClickInfo.class);
		jobconf.setOutputKeyClass(IntWritable.class);
		jobconf.setOutputValueClass(ClickInfo.class);
		jobconf.setInputFormat(NLineInputFormat.class);
		jobconf.setMapperClass(ClickStaticMapper.class);
		jobconf.setReducerClass(ClickStaticReduce.class);
		jobconf.setCombinerClass(ClickStaticReduce.class);
		jobconf.setOutputFormat(TextOutputFormat.class);
		jobconf.setNumMapTasks(1);
		JobClient jobClient=new JobClient(jobconf);
		RunningJob runjob=jobClient.submitJob(jobconf);
		System.out.println("job id is "+runjob.getID());
		System.out.println("job name is "+runjob.getJobName());
		while(!runjob.isComplete()){
			System.out.println("******************  wait job  at "+simpleDateFormat.format(new Date()));
		    Thread.sleep(2000);
		}
		System.out.println("******************  done job  at "+simpleDateFormat.format(new Date()));
	}
	//运行参数的判定，输入输出路径必须有，第三第四个参数为map、reduce数量，可以没有，有的话必须为整数
	public static boolean checkArgsSuccess(String[] args){
		if(args==null||args.length<2)
			return false;
		if(args.length>=3){
			try{
				Integer.parseInt(args[2]);
			}catch(NumberFormatException e){
				e.printStackTrace();
				return false;
			}
		}
		if(args.length>=4){
			try{
				Integer.parseInt(args[3]);
			}catch(NumberFormatException e){
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}
