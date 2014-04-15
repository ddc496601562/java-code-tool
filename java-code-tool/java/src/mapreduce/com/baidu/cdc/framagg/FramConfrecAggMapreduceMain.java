package com.baidu.cdc.framagg;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
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
public class FramConfrecAggMapreduceMain {
	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// TODO Auto-generated method stub
		String Usage="Usage:inputPath  outputh [linespermap reduceNumber] ";
		if(args.length<2){
			System.out.println(Usage);
			System.exit(0);
		}
		JobConf jobconf=new JobConf(new Configuration());
		jobconf.setJarByClass(FramConfrecAggMapreduceMain.class);
		System.out.println(jobconf.get("mapred.jar"));
		jobconf.setJobName("fram agg compute for ddc test");
		FileInputFormat.setInputPaths(jobconf, new Path(args[0]));
		FileOutputFormat.setOutputPath(jobconf, new Path(args[1]));
		if(args.length>=3)
			jobconf.set("mapred.line.input.format.linespermap", args[2]);
		else
			jobconf.set("mapred.line.input.format.linespermap", 100000+"");
		if(args.length==4){
			jobconf.setNumReduceTasks(Integer.parseInt(args[3]));
		}
		jobconf.setMapOutputKeyClass(Text.class);
		jobconf.setMapOutputValueClass(ConfrecByFram.class);
		jobconf.setOutputKeyClass(Text.class);
		jobconf.setOutputValueClass(ConfrecByFram.class);
		jobconf.setInputFormat(NLineInputFormat.class);
		jobconf.setMapperClass(FramConfrecAggMapper.class);
		jobconf.setReducerClass(FramConfrecAggReduce.class);
		jobconf.setOutputFormat(TextOutputFormat.class);
		JobClient jobClient=new JobClient(jobconf);
		RunningJob runjob=jobClient.submitJob(jobconf);
		System.out.println("job id is "+runjob.getID());
		System.out.println("job name is "+runjob.getJobName());
		while(!runjob.isComplete()){
			System.out.println(simpleDateFormat.format(new Date())+" : map is "+runjob.mapProgress() +" , reduce is "+runjob.reduceProgress());
		    Thread.sleep(1000);
		}
		System.out.println("******************  done job  at "+simpleDateFormat.format(new Date()));
	}
}
