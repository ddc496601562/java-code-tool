package com.baidu.mysql;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class DataInsertInto_click_money {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Connection conn=null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://tc-dba-crm-test00.tc.baidu.com:8906/cdc-test-db","crm","123456");
		}catch(Exception e) {
			System.out.println("链接数据库发生异常!");
			e.printStackTrace();
		}
		PreparedStatement preparedStatement=conn.prepareStatement("insert into clickMony_weihu values(?,?)");
		LineNumberReader lineReader=new LineNumberReader(new FileReader("D:/SecureCRT file/down/clickmoney.txt"));
		String line=null ;
		int i=0 ;
		conn.setAutoCommit(false);
		while((line=lineReader.readLine())!=null){
			String[] splits=line.split("	");
			int account_id=Integer.parseInt(splits[0].trim());
			float click_money=Float.parseFloat(splits[1].trim());
			preparedStatement.setInt(1, account_id);
			preparedStatement.setFloat(2, click_money);
			preparedStatement.executeUpdate();
			if(i%1000==0){
				System.out.println("insert success "+i+"  "+(new Date()).toGMTString());
				conn.commit();
			}
			i++;
		}
		preparedStatement.close();
		conn.commit();
		conn.close();
		lineReader.close();
	}

}
