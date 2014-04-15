package com.baidu.excel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StringArrayCreateTest {

	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		SimpleDateFormat sf1=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat sf5=new SimpleDateFormat("HHmm");
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(sf1.parse("2012-09-18 00:00"));
		System.out.print("time_slice_array=$time_slice_array'");
		for(int i=1;i<=96;i++){
			System.out.print(""+sf5.format(calendar.getTime())+"");
			if(i%12==0){
				System.out.println(" '");
				System.out.print("time_slice_array=$time_slice_array'");
			}
			else 
				System.out.print(" ");
			calendar.add(Calendar.MINUTE, 15);
		}

	}

}
