package com.baidu.rigel.cdc.flie.tool;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateAddParSql {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		Calendar calendar=Calendar.getInstance();
		calendar.set(2013, 11, 30);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sfHour=new SimpleDateFormat("HHmm");
		PrintStream out=new PrintStream("D:/dorado_dcharge_bd.sql");
		sf.format(calendar.getTime()) ;
		System.out.println(sf.format(calendar.getTime()));
		out.println("use ods ;");
		
		while(sf.format(calendar.getTime()).compareTo("2014-01-01")<0){
			String sql="alter table prod_sf_acct_info drop partition (pdate='"+sf.format(calendar.getTime())+"') ;";
			sql="ALTER TABLE dorado_dcharge_bd ADD partition(pdate='"+sf.format(calendar.getTime())+"',hour='"+sfHour.format(calendar.getTime())+"')  location '/app/ecom/rigelci/hive/ods.db/dorado_dcharge_bd/pdate="+sf.format(calendar.getTime())+"/hour="+sfHour.format(calendar.getTime())+"' ;";
			out.println(sql);
			calendar.add(Calendar.MINUTE, 15);
		}
		out.close();
	}

}
