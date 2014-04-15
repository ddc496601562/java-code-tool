package com.baidu.mysql;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class DataInserttd_col {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Connection conn=null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/nj-yulong-test","root","root");
		}catch(Exception e) {
			System.out.println("链接数据库发生异常!");
			e.printStackTrace();
		}
		PreparedStatement preparedStatement=conn.prepareStatement("insert into td_col values(?,?,?,?,?,?,?,?)");
		LineNumberReader lineReader=new LineNumberReader(new FileReader("E:/db/td_col.data"));
		String line=null ;
		int i=0 ;
		conn.setAutoCommit(false);
		while((line=lineReader.readLine())!=null){
			String[] splits=line.split("	");
			int id=Integer.parseInt(splits[0].trim());
			int table_id=Integer.parseInt(splits[1].trim());
			String col_name=splits[2].trim();
			String col_type=splits[3].trim();
			int max_col_length=splits[4].trim().equals("NULL")?1000:Integer.parseInt(splits[4].trim());
			String col_format=splits[5].trim();
			String comment=splits[6].trim();
			int col_pos= Integer.parseInt(splits[7].trim());
			preparedStatement.setInt(1, id);
			preparedStatement.setInt(2, table_id);
			preparedStatement.setString(3, col_name);
			preparedStatement.setString(4, col_type);
			preparedStatement.setInt(5, max_col_length);
			preparedStatement.setString(6, col_format);
			preparedStatement.setString(7, comment);
			preparedStatement.setInt(8, col_pos);
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
