package com.baidu.mysql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class DataInsertInto_all_cmatch {

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
		PreparedStatement preparedStatement=conn.prepareStatement("insert into fc_allcmatch_display_abnor_halfhr values" +
				"(?,?,?,?,?,?,?,?," +
				 "?,?,?,?,?,?,?,?," +
				 "?,?,?,?,?,?,?,?," +
				 "?,?,?,?,?,?,?,?)");
		int i=0 ;
		conn.setAutoCommit(false);
		for(File dataFile:new File("D:/SecureCRT file/down/allcmatch/").listFiles()){
			LineNumberReader lineReader=new LineNumberReader(new InputStreamReader(new FileInputStream(dataFile),"GBK"));
			String line=null ;
			while((line=lineReader.readLine())!=null){
				String[] splits=line.split("	");
				if(splits.length<33){
					System.out.println("continue");
					continue ;
				}
				preparedStatement.setString(1, splits[1].trim());
				preparedStatement.setString(2, splits[2].trim());
				preparedStatement.setString(3, splits[3].trim());
				preparedStatement.setString(4, splits[4].trim());
				preparedStatement.setString(5, splits[5].trim());
				
				
				preparedStatement.setString(6, splits[6].trim());
				preparedStatement.setString(7, splits[7].trim());
				preparedStatement.setString(8, splits[8].trim());
				preparedStatement.setString(9, splits[9].trim());
				preparedStatement.setString(10, splits[10].trim());
				preparedStatement.setString(11, splits[11].trim());
				preparedStatement.setString(12, splits[12].trim());
				preparedStatement.setString(13, splits[13].trim());
				preparedStatement.setString(14, splits[14].trim());
				preparedStatement.setString(15, splits[15].trim());
				preparedStatement.setString(16, splits[16].trim());
				preparedStatement.setString(17, splits[17].trim());
				preparedStatement.setString(18, splits[18].trim());
				preparedStatement.setString(19, splits[19].trim());
				
				preparedStatement.setString(20, splits[20].trim());
				preparedStatement.setString(21, splits[21].trim());
				preparedStatement.setString(22, splits[22].trim());
				preparedStatement.setString(23, splits[23].trim());
				preparedStatement.setString(24, splits[24].trim());
				preparedStatement.setString(25, splits[25].trim());
				preparedStatement.setString(26, splits[26].trim());
				preparedStatement.setString(27, splits[27].trim());
				
				
				preparedStatement.setString(28, splits[28].trim());
				
				
				preparedStatement.setString(29, splits[29].trim());
				preparedStatement.setString(30, splits[30].trim());
				preparedStatement.setString(31, splits[31].trim());
				preparedStatement.setString(32, splits[32].trim());
				
				preparedStatement.executeUpdate();
				if(i%1000==0){
					System.out.println("insert success "+i+"  "+(new Date()).toGMTString());
					conn.commit();
				}
				i++;
			}
			lineReader.close();
		}
		preparedStatement.close();
		conn.commit();
		conn.close();
	}

}
