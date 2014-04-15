package com.baidu.rigel.cdc.flie.tool;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateAddParSqlForHour {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		Calendar calendar=Calendar.getInstance();
		calendar.set(2013, 07, 01);
		SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
		PrintStream out=new PrintStream("D:/dorado_dcharge_bd.sql");
		sf.format(calendar.getTime()) ;
		out.println("use ods ;");
		while(sf.format(calendar.getTime()).compareTo("2013-11-18")<0){
			String sql="alter table prod_sf_acct_info drop partition (pdate='"+sf.format(calendar.getTime())+"') ;";
			sql="ALTER TABLE dorado_dcharge_bd partition(pdate='"+sf.format(calendar.getTime())+"') SET location 'hdfs://nmg01-khan-hdfs.dmop.baidu.com:54310/app/ecom/rigelci/hive/warehouse/dwd.db/prod_sf_acct_info/pdate=2013-11-18' ;";
			out.println(sql);
			calendar.add(calendar.DATE, 1);
		}
	}

}
