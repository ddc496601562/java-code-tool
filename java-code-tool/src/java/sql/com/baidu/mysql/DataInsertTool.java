package com.baidu.mysql;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class DataInsertTool {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Connection conn=null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/user_clienk_info","root","root");
		}catch(Exception e) {
			System.out.println("链接数据库发生异常!");
			e.printStackTrace();
		}
		PreparedStatement preparedStatement=conn.prepareStatement("insert into account_info(account_id,account_name,company_name,company_add) values(?,?,?,?)");
		LineNumberReader lineReader=new LineNumberReader(new FileReader("E:/资料/account_get.log"));
		String line=null ;
		int i=0 ;
		conn.setAutoCommit(false);
		while((line=lineReader.readLine())!=null){
			String[] splits=line.split("&&&&&");
			int account_id=Integer.parseInt(splits[0].trim());
			String account_name=splits[1].trim().equals("无")?"\n":splits[1].trim();
			String company_name=splits[2].trim().equals("无")?"\n":splits[2].trim();
			String company_add=splits[3].trim().equals("无")?"\n":splits[3].trim();
			preparedStatement.setInt(1, account_id);
			preparedStatement.setString(2, account_name);
			preparedStatement.setString(3, company_name);
			preparedStatement.setString(4, company_add);
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
