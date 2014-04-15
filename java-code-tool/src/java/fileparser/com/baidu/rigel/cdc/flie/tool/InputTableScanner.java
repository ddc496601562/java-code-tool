package com.baidu.rigel.cdc.flie.tool;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputTableScanner {
	public static void main(String[] args) throws IOException {
		File file=new File("D:/eclipse-jee-workspace/cdc-cust-feature/etl/ka/feature_warehouse/dwa_contline_revenue_day/t/sql/contline_revenue_day.sql");
		getDependTable(file);
		
	}
	public static List<String>  getDependTable(File sqlFile) throws IOException{
		Map<String,String> dbMapFile=new HashMap<String,String>();
		dbMapFile.put("db_cdc_dm", "cdc_dm");
		dbMapFile.put("db_cdc_cust_feature", "cdc_cust_feature");
		dbMapFile.put("db_cdc_publish", "db_cdc_publish");
		dbMapFile.put("db_pulse", "db_pulse");
		dbMapFile.put("db_ods", "ods");
		dbMapFile.put("db_pub", "pub");
		dbMapFile.put("db_dwd", "dwd");
		dbMapFile.put("db_dwa", "dwa");
		dbMapFile.put("db_dma_pulse", "dma_pulse");
		LineNumberReader lineReader=new LineNumberReader(new FileReader(sqlFile));
		String line=null;
		StringBuffer sqlSb=new StringBuffer();
		while((line=lineReader.readLine())!=null){
			sqlSb.append(line);
			sqlSb.append(" ");
		}
		lineReader.close();
		String sqlString=sqlSb.toString().toLowerCase().replaceAll("\t", " ");
		for(Map.Entry<String, String> entry:dbMapFile.entrySet()){
			sqlString=sqlString.replaceAll("\\$"+entry.getKey(), entry.getValue());
		}
		List<String> insertTableList=new ArrayList<String>();
		//use  dwa ;
		Pattern patternUseDb = Pattern.compile("use\\s+[a-z0-9\\_]+\\s*;");
		List<String> depTableList=new ArrayList<String>();
		//INSERT OVERWRITE TABLE $db_dwa.contline_revenue_day 
		Pattern patternInsertTab = Pattern.compile("insert\\s+overwrite\\s+table\\s+[[a-z0-9\\_]*\\.]*[[a-z0-9\\_]+*]+");
		//FROM $db_dwd.log_contline_revenue_dtl
		Pattern patternFromTable = Pattern.compile("from\\s+[[a-z0-9\\_]*\\.]*[[a-z0-9\\_]+*]+");
		Matcher matcher =patternUseDb.matcher(sqlString);
		String last_db="";
		while(matcher.find()){
			last_db=matcher.group().replaceAll(";", "").trim();
			last_db=last_db.substring(last_db.lastIndexOf(" ")+1);
			System.out.println(last_db);
		}
		Matcher insertTab=patternInsertTab.matcher(sqlString);
		while(insertTab.find()){
			String tblName=insertTab.group().trim();
			tblName=tblName.substring(tblName.lastIndexOf(" ")+1);
			if(tblName.indexOf(".")==-1)
				tblName=last_db+"."+tblName;
			if(!insertTableList.contains(tblName))
				insertTableList.add(tblName) ;
		}
		for(String table:insertTableList)
			System.out.println(table);
		Matcher matcherTable=patternFromTable.matcher(sqlString);
		while(matcherTable.find()){
			String tblName=matcherTable.group().trim();
			tblName=tblName.substring(tblName.lastIndexOf(" ")+1);
			if(tblName.indexOf(".")==-1)
				tblName=last_db+"."+tblName;
			if(!depTableList.contains(tblName))
				depTableList.add(tblName) ;
		}
		for(String table:depTableList)
			System.out.println(table);
		return depTableList ;
	}
}
