package com.baidu.rigel.cdc.hdfs;


import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobConfigurable;

public class HiveInputFormatFilter  implements PathFilter ,JobConfigurable{
	public static String REGUL_NAME="hive.partition.file.filter";
	private String regularRule="";
	@Override
	public boolean accept(Path path) {
		// TODO Auto-generated method stub
		String name=path.getName();
		if(regularRule!=null)
			return !name.matches(regularRule);
		return true;
	}
	@Override
	public void configure(JobConf job) {
		regularRule=job.get(REGUL_NAME, null);
		System.out.println("load this class success!!!");
	}
	public static void  main(String[]  args){
		
		System.out.println("000760".matches("@manifest.*"));
	}

}
