package com.baidu.cdc.date.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateDimTest {
	
	/**
	 * @param args
	 * @throws ParseException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws ParseException, FileNotFoundException {
		String partDir="/home/dingdongchao/data-dim-data/";
		// TODO Auto-generated method stub
		Calendar calendar=Calendar.getInstance();
		SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sf1=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat sf2=new SimpleDateFormat("HH:mm");
		for(String date :args){
			File dir=new File(partDir+"dt="+date);
			if(!dir.exists())
				dir.mkdir();
			PrintStream out=new PrintStream(new File(dir,"00000"));
			calendar.setTime(sf.parse(date));
			int today=calendar.get(Calendar.DAY_OF_MONTH);
			int next_min_day=calendar.get(Calendar.DAY_OF_MONTH);
			while(today==next_min_day){
				int hour_of_day_js=calendar.get(Calendar.HOUR_OF_DAY);
				int min_of_hour_js=calendar.get(Calendar.MINUTE);
				int month_of_year_js=calendar.get(Calendar.MONTH);
				
				
				
				int min_of_day=hour_of_day_js*60+min_of_hour_js;
				int min_of_hour=min_of_hour_js;
				int min_of_squarter=min_of_hour_js%15;
				int squarter_of_hour=min_of_hour_js/15;
				int squarter_of_day=hour_of_day_js*4+squarter_of_hour;
				int hour_of_day=hour_of_day_js;
				int day_of_week=calendar.get(Calendar.DAY_OF_WEEK);
				int day_of_month=calendar.get(Calendar.DAY_OF_MONTH);
				int day_of_year=calendar.get(Calendar.DAY_OF_YEAR);
				int week_of_month=calendar.get(Calendar.WEEK_OF_MONTH);
				int week_of_year=calendar.get(Calendar.WEEK_OF_YEAR);
				int month_of_quarter=month_of_year_js%4 ;
				int month_of_year=month_of_year_js;
				int quarter_of_year=month_of_year_js/4;
				int year=calendar.get(Calendar.YEAR);
				String date_day=sf.format(calendar.getTime());
				String date_min=sf1.format(calendar.getTime());
				String date_sec=sf2.format(calendar.getTime());
				String stdate=sf.format(calendar.getTime());
				out.println(min_of_day+"	"+
						    min_of_hour+"	"+
						    min_of_squarter+"	"+
						    squarter_of_hour+"	"+
						    squarter_of_day+"	"+
						    hour_of_day+"	"+
						    day_of_week+"	"+
						    day_of_month+"	"+
						    day_of_year+"	"+
						    week_of_month+"	"+
						    week_of_year+"	"+
						    month_of_quarter+"	"+
						    month_of_year+"	"+
						    quarter_of_year+"	"+
						    year+"	"+
						    date_day+"	"+
						    date_min+"	"+
						    date_sec+"	"+
						    stdate);
				calendar.add(Calendar.MINUTE, 1);
				next_min_day=calendar.get(Calendar.DAY_OF_MONTH);
			}
			out.close();
		}
		
	}

}
