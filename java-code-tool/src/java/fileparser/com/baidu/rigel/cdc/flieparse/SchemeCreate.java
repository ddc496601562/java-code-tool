package com.baidu.rigel.cdc.flieparse;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SchemeCreate {
	public static void main(String[] args) throws IOException {
		System.out.println(Long.MAX_VALUE);
		String blank="                         ";
		String  db="ods";
		String path="D:/eclipse-jee-workspace/java-hadoop-test/src/com/baidu/cdc/data/util/";
		Map<String,String> dataType=new HashMap<String,String>();
		Map<String,String> properties=new HashMap<String,String>();
		dataType.put("INTEGER", "INT   ");
		dataType.put("VARCHAR2", "STRING");
		dataType.put("VARCHAR", "STRING");
		dataType.put("BIGINT", "BIGINT");
		dataType.put("NUMBER", "BIGINT");
		Map<String,Colnum> clonumMap=new HashMap<String,Colnum>();
		LineNumberReader lineReader=new LineNumberReader(new FileReader(path+"sql.sql"));
		String line=null;
		String tableName=null;
		while((line=lineReader.readLine())!=null){
			if(line.contains("CREATE")&&!line.contains("TABLE")){
				line=lineReader.readLine();
				if(line.contains("TABLE")){
					tableName=line.trim().split(" ")[1];
				}
				break;
			}
			if(line.contains("CREATE TABLE ")){
				tableName=line.split(" ")[2];
				break;
			}
		}
		line=lineReader.readLine();
		int colnumIndex=0;
		ArrayList<Colnum> colnumList=new ArrayList<Colnum>();
		while((line=lineReader.readLine())!=null){
			if(line.contains("    --  "))
				continue ;
			if(line.contains("  )"))
				break;
//			System.out.println(line);
			String[] lineSplit=line.trim().split("\\s{1,}");
//			System.out.println(line+"  : "+lineSplit[0]+" "+lineSplit[1]);
			Colnum colnum=new Colnum(colnumIndex,lineSplit[0],dataType.get(lineSplit[1]),"");
			clonumMap.put(lineSplit[0].toLowerCase(), colnum) ;
			colnumList.add(colnum);
			colnumIndex++ ;
		}
		while((line=lineReader.readLine())!=null){
			if(line.contains("    --  "))
				continue ;
			if(line.contains("COMMENT ON COLUMN ")){
//				System.out.println(line.trim().split(" ")[3]);
				String colnumName=line.trim().split(" ")[3].split("\\.")[1].toLowerCase();
				lineReader.readLine();
				String comment=lineReader.readLine().trim().replace(";", "").trim();;
//				System.out.println(colnumName);
				clonumMap.get(colnumName).comment=comment;
			}
			if(line.contains("ADD CONSTRAINT")&&line.contains("PRIMARY KEY")){
				lineReader.readLine();
				String primary_key="";
				while(!(line=lineReader.readLine()).contains(")")){
					primary_key=primary_key+line.trim().toLowerCase();
				}
				properties.put("primary_key", primary_key);
			}
		}
		lineReader.close();
		System.out.println("use  "+db+" ; ");
		System.out.println("CREATE TABLE "+tableName+" ( ");
		for(int i=0;i<colnumList.size()-1;i++){
			Colnum c=colnumList.get(i);
			System.out.println(c.colnumName+blank.substring(c.colnumName.length())+c.type+"    COMMENT "+c.comment+" , ");
		}
		Colnum cc=colnumList.get(colnumList.size()-1);
		System.out.println(cc.colnumName+blank.substring(cc.colnumName.length())+cc.type+"    COMMENT "+cc.comment+"  ");
		System.out.println(") ");
		System.out.println("COMMENT '请补充comment' ");
		System.out.println("PARTITIONED BY(pdate STRING) ");
		System.out.println("ROW FORMAT DELIMITED ");
		System.out.println("FIELDS TERMINATED BY '\\t' ");
		System.out.println("LINES TERMINATED BY '\\n' ");
		System.out.println("STORED AS TEXTFILE ");
		if(properties.size()>0){
			System.out.println("TBLPROPERTIES  ");
			System.out.println("( ");
			boolean isFirst=true;
			for(Entry<String, String> entry:properties.entrySet()){
				if(isFirst){
					isFirst=false;
				}else{
					System.out.print(",");
				}
				System.out.print("'"+entry.getKey()+"'='"+entry.getValue()+"'");
			}
			System.out.println();
			System.out.println(");  ");
		}

		System.out.println();
		System.out.println();
		String sql="" ;
		System.out.println("app_name: "+db);
		System.out.println("tb_name: "+tableName);
		System.out.println("field_list:");
		for(Colnum c :colnumList){
			System.out.println("  - field: "+c.colnumName);
			System.out.println("    type: "+c.type.toLowerCase());
			System.out.println("    comment: "+c.comment);
			sql=sql+", "+c.colnumName ;
		}
		System.out.println("type: hive");
		System.out.println("file_path: $db_"+db);
		System.out.println("model: hive");
		System.out.println("duration: -1");
		System.out.println("encoding: utf8");
		
		System.out.println(sql);
	}

}

