package com.baidu.mysql;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class DataInsertToolFromHdfs {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/user_clienk_info", "root",
					"root");
		} catch (Exception e) {
			System.out.println("链接数据库发生异常!");
			e.printStackTrace();
		}
		PreparedStatement preparedStatement=conn.prepareStatement("insert into clickinfo(account_id,click_counter,pre_consumption,ture_consumption_money) values(?,?,?,?)");
		LineNumberReader lineReader = new LineNumberReader(new FileReader("E:/资料/clickInfoStatic.log"));
		String line = null;
		int i = 0;
		conn.setAutoCommit(false);
		while ((line = lineReader.readLine()) != null) {
			String[] splits = line.split("	");
			int account_id = Integer.parseInt(splits[0].trim());
			int click_counter=Integer.parseInt(splits[1].trim());
			float pre_consumption =Float.parseFloat(splits[2].trim());
			float ture_consumption_money = Float.parseFloat(splits[3].trim());
			System.out.println(account_id+" "+click_counter+" "+pre_consumption+" "+ture_consumption_money);
			preparedStatement.setInt(1, account_id);
			preparedStatement.setInt(2, click_counter);
			preparedStatement.setFloat(3, pre_consumption);
			preparedStatement.setFloat(4, ture_consumption_money);
			preparedStatement.executeUpdate();
			if (i % 1000 == 0) {
				System.out.println("insert success " + i + "  "
						+ (new Date()).toGMTString());
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
