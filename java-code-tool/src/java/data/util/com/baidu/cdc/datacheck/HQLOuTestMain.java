package com.baidu.cdc.datacheck;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HQLOuTestMain {

	/**
	 * @param args
	 * @throws ParseException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws ParseException, FileNotFoundException {
		
		long all_auto=0;
		all_auto=all_auto+876808487416L+377358927325L+11924712122L+77008000625L+84166068548L;
		
		System.out.println(((double)all_auto)/1024/1024/1024/1024);
		System.out.println(219902325555200d/1024/1024/1024/1024);
		
//		if(1>0)  System.exit(1);
		// TODO Auto-generated method stub
		
		SimpleDateFormat sf1=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat sf2=new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sf3=new SimpleDateFormat("HH:mm");
		SimpleDateFormat sf4=new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sf5=new SimpleDateFormat("HHmm");
		Calendar calendar=Calendar.getInstance();
		String[] dates=new String[]{"2013-03-13 00:00","2013-03-14 00:00"};
		PrintStream out=new PrintStream("E:/fc_nor_cmatchall_show_log_30m.sql");
		out.println("use wh_test;");
		for (String date : dates) {
			calendar.setTime(sf1.parse(date));
			System.out.println(sf2.format(calendar.getTime()));
			System.out.println(sf3.format(calendar.getTime()));
			int today = calendar.get(Calendar.DAY_OF_MONTH);

			while (calendar.get(Calendar.DAY_OF_MONTH) == today) {
//				out.println("alter table tb_fc_clk_payment_log  partition (pdatetime='"
//						+ sf2.format(calendar.getTime())
//						+ " "
//						+ sf3.format(calendar.getTime())
//						+ "') location '"
//						+ sf4.format(calendar.getTime())
//						+ "/"
//						+ sf5.format(calendar.getTime()) + "/tc-sf-drd03.tc/' ;");
//				out.println("alter table tb_fc_clk_payment_log  partition (pdatetime='"
//						+ sf2.format(calendar.getTime())
//						+ " "
//						+ sf3.format(calendar.getTime())
//						+ "') set location 'hdfs://hy-ecomoff-hdfs.dmop.baidu.com:54310/app/ecom/rigelci/hive/db_pulse.db/tb_fc_clk_payment_log/"
//						+ sf4.format(calendar.getTime())
//						+ "/"
//						+ sf5.format(calendar.getTime()) + "/tc-sf-drd03.tc/' ;");
				out.println("alter table fc_nor_cmatchall_show_log_30m add partition(dt='"+sf2.format(calendar.getTime())+"',hour='"+sf5.format(calendar.getTime())+
//						"') location '/app/ecom/rigelci/hive/wh_test.db/dwa_fc_nor_all_cmatch_show_30m/pdate="+sf2.format(calendar.getTime())+"/hour="+sf5.format(calendar.getTime())+"' ;");
				        "') location 'hdfs://nj01-yulong-hdfs.dmop.baidu.com:54310/app/ns/lsp/output/ecom_fengchao/fc_allcmatch_display_nor_halfhr/"+sf4.format(calendar.getTime())+"/"+sf5.format(calendar.getTime())+"' ;");
//				out.println("!date;");
//				out.println("alter table tb_fc_clk_payment_log add IF NOT EXISTS partition (pdatetime='2012-09-18 "+sf3.format(calendar.getTime())+"');  ");
//				out.println("INSERT OVERWRITE TABLE cdc_dm.tb_fc_clk_pay_mild_summary PARTITION(dt='2012-09-18',hour='"+sf3.format(calendar.getTime())+"') "+
//                             " select  " +
//                             " userid,planid,unitid,wordid,cmatch,wmatch,cntnid,provid,count(*) as click,sum(price_d) as click_pay,sum(price_d*rrate) as click_money"+
//						     " from db_pulse.tb_fc_clk_payment_log    where pdatetime='2012-09-18 "+sf3.format(calendar.getTime())+"'  "+
//                             " group by userid,planid,unitid,wordid,cmatch,wmatch,cntnid,provid ;");
//				out.println("!date;");
				calendar.add(Calendar.MINUTE, 30);
			}
		}
		out.close();
		
	}

}
