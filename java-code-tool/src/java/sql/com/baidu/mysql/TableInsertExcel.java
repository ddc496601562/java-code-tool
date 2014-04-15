package com.baidu.mysql;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;

public class TableInsertExcel {

	/**
	 * @param args
	 * @throws SQLException 
	 * @throws UnsupportedEncodingException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		OutputStream os = new FileOutputStream("E:/jobs/整理建模/table_tmp.xls");
		jxl.write.WritableWorkbook wwb = Workbook.createWorkbook(os);
		jxl.write.WritableSheet cdc_dm = wwb.createSheet("中度汇总层数据表", 1);
		Connection conn=null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/szwg_ecomon_crm_2","root","root");
		}catch(Exception e) {
			System.out.println("链接数据库发生异常!");
			e.printStackTrace();
		}
		PreparedStatement preparedStatement=conn.prepareStatement("SELECT  *  FROM tbls where db_id=51 order by  tbl_name ;");
		ResultSet result=preparedStatement.executeQuery();
		
		int row=0;
		while(result.next()){
			String tbl_name=result.getString("tbl_name");
			Label labelCFC = new Label(0, row, tbl_name);
			cdc_dm.addCell(labelCFC);
			row++;
		}
		preparedStatement=conn.prepareStatement("SELECT  *  FROM tbls where db_id=11 order by  tbl_name;");
		result=preparedStatement.executeQuery();
		jxl.write.WritableSheet cdc_cust_feature = wwb.createSheet("衍生汇总层", 2);
		row=0;
		while(result.next()){
			String tbl_name=result.getString("tbl_name");
			if(!tbl_name.matches("dwa_acc.*_weekly")||tbl_name.matches(".*_matrix_.*"))
				continue ;
			Label labelCFC = new Label(0, row, tbl_name);
			cdc_cust_feature.addCell(labelCFC);
			row++;
		}
		preparedStatement=conn.prepareStatement("SELECT  *  FROM tbls where db_id=11  order by  tbl_name;");
		result=preparedStatement.executeQuery();
		jxl.write.WritableSheet cdc_cust_feature_matrix = wwb.createSheet("衍生汇总层-104列", 2);
		row=0;
		while(result.next()){
			String tbl_name=result.getString("tbl_name");
			if(!tbl_name.matches(".*_matrix_.*"))
				continue ;
			Label labelCFC = new Label(0, row, tbl_name);
			cdc_cust_feature_matrix.addCell(labelCFC);
			row++;
		}
		wwb.write();
		wwb.close();
	}

}
