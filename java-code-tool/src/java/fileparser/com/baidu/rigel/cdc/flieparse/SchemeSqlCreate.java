package com.baidu.rigel.cdc.flieparse;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class SchemeSqlCreate {
	public static void main(String[] args) throws IOException {
		System.out.println(Long.MAX_VALUE);
		Set<String> groupColList=new HashSet<String>();
		groupColList.add("winfo_id");
		groupColList.add("word_id");
		groupColList.add("idea_id");
		groupColList.add("unit_id");
		groupColList.add("plan_id");
		groupColList.add("acct_id");
		String blank="                                   ";
		String  db="publish";
		String folder_pre="" ;
		String folder="D:/scheme_folder/";
		String path="D:/eclipse-jee-workspace/java-code-tool/src/java/fileparser/com/baidu/rigel/cdc/flieparse/";
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
		String tableDesc=lineReader.readLine().trim();
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
			if(colnum.colnumName==null||colnum.type==null)
				continue ;
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
				Colnum value=clonumMap.get(colnumName);
				if(value!=null)
					value.comment=comment;
			}
			if(line.contains("ADD CONSTRAINT")&&line.contains("PRIMARY KEY")){
				lineReader.readLine();
				String primary_key="";
				while(!(line=lineReader.readLine()).contains(")")){
					primary_key=primary_key+line.trim().toLowerCase();
				}
				if(!primary_key.equals("linkid"))
					properties.put("primary_key", primary_key);
			}
		}
		lineReader.close();
		for(Colnum c:colnumList){
			c.colnumName=c.colnumName.replace("→", "");
		}
		tableName=tableName.replace("\"", "");
		String tmp=tableName;
		tableName=tableDesc;
		tableDesc=tmp;
		File thisTaskFolder=new File(folder,folder_pre+tableName+"/t");
		thisTaskFolder.mkdirs();
		PrintStream  tableCreadFile=new PrintStream(folder+"/"+tableName+".sql");
		tableCreadFile.println("use  "+db+" ; ");
		tableCreadFile.println("CREATE TABLE "+tableName+" ( ");
		for(int i=0;i<colnumList.size()-1;i++){
			Colnum c=colnumList.get(i);
			System.out.println(c.colnumName);
			tableCreadFile.println(c.colnumName+blank.substring(c.colnumName.length())+c.type+"    COMMENT "+c.comment+" , ");
		}
		Colnum cc=colnumList.get(colnumList.size()-1);
		tableCreadFile.println(cc.colnumName+blank.substring(cc.colnumName.length())+cc.type+"    COMMENT "+cc.comment+"  ");
		tableCreadFile.println(") ");
		tableCreadFile.println("COMMENT '"+tableDesc+"' ");
		tableCreadFile.println("PARTITIONED BY(pdate STRING) ");
		tableCreadFile.println("ROW FORMAT DELIMITED ");
		tableCreadFile.println("FIELDS TERMINATED BY '\\t' ");
		tableCreadFile.println("LINES TERMINATED BY '\\n' ");
		tableCreadFile.println("STORED AS TEXTFILE ");
		if(properties.size()>0){
			tableCreadFile.println("TBLPROPERTIES  ");
			tableCreadFile.println("( ");
			boolean isFirst=true;
			for(Entry<String, String> entry:properties.entrySet()){
				if(isFirst){
					isFirst=false;
				}else{
					tableCreadFile.print(",");
				}
				tableCreadFile.print("'"+entry.getKey()+"'='"+entry.getValue()+"'");
			}
			tableCreadFile.println();
			tableCreadFile.println(");  ");
		}

		tableCreadFile.println();
		tableCreadFile.println();
		tableCreadFile.close();
		//生成meta file
		File metaFileFolder=new File(thisTaskFolder,"table_meta");
		metaFileFolder.mkdir();
		PrintStream  metaFile=new PrintStream(metaFileFolder.getAbsolutePath()+"/"+tableName+"_hive.meta");
		metaFile.println("app_name: "+db);
		metaFile.println("tb_name: "+tableName);
		metaFile.println("field_list:");
		for(Colnum c :colnumList){
			metaFile.println("  - field: "+c.colnumName);
			metaFile.println("    type: "+c.type.toLowerCase());
			metaFile.println("    comment: "+c.comment);
		}
		metaFile.println("type: hive");
		metaFile.println("file_path: $db_"+db);
		metaFile.println("model: hive");
		metaFile.println("duration: -1");
		metaFile.println("encoding: utf8");
		metaFile.close();
		//生成task sql 文件
		File taskSqlFolder=new File(thisTaskFolder,"sql");
		taskSqlFolder.mkdir();
		PrintStream  taskSqlFile=new PrintStream(taskSqlFolder.getAbsolutePath()+"/"+tableName+".sql");
		taskSqlFile.println("SET mapred.job.name='job_"+db+"_"+tableName+"_$YYYY-$mm-$dd';");
		taskSqlFile.println("----"+tableDesc+"----");
		taskSqlFile.println("----添加sqlfile的注释----");
		taskSqlFile.println("INSERT OVERWRITE TABLE $db_"+db+"."+tableName+" PARTITION(pdate='$YYYY-$mm-$dd') ");
		taskSqlFile.println("SELECT ");
		String groubString="";
		boolean isFirst=true;
		String  sqlpp="";
		for(Colnum c :colnumList){
			sqlpp=sqlpp+c.colnumName+", " ;
		}
		System.out.println(sqlpp);
		String allColnum="";
		String bank="";
		String allcASTColnum="";
		for(Colnum c :colnumList){
			allColnum=allColnum+", "+c.colnumName;
			allcASTColnum=allcASTColnum+", "+"CAST(0,BIGINT) AS "+c.colnumName;
			if(!groupColList.contains(c.colnumName)){
				if(c.colnumName.contains("click")||c.colnumName.contains("shw"))
					taskSqlFile.println("SUM("+bank+c.colnumName+") AS "+c.colnumName+",  ");
				else
					taskSqlFile.println("ROUND(SUM("+bank+c.colnumName+")/1000,2) AS "+c.colnumName+",  ");
				//round(tc_cash*1.0/1000,2),
				if(isFirst){
					groubString=groubString+c.colnumName+" ";
					isFirst=false;
				}else
					groubString=groubString+", "+c.colnumName+" ";
					
			}else{
				taskSqlFile.println(bank+""+c.colnumName+" AS "+c.colnumName+",  ");
			}
		}
		taskSqlFile.println(bank+"FROM  $db_"+db+".prod_idea_bd_show_day ");
		taskSqlFile.println(bank+"WHERE  pdate='$YYYY-$mm-$dd'   ");
		taskSqlFile.println(bank+"GROUP BY "+groubString +" ; ");
		taskSqlFile.println(allColnum);
		taskSqlFile.println(allcASTColnum);
		
		taskSqlFile.println();
		taskSqlFile.println();
		taskSqlFile.println();
		taskSqlFile.println();
//		taskSqlFile.println("----从此处开始是测试sql-----");
//		taskSqlFile.println("set hive.cli.print.header=true;");
//		taskSqlFile.println("set hive.cli.print.row.to.vertical=true; ");
//		taskSqlFile.println("set hive.cli.print.row.to.vertical.num=1;");
//		taskSqlFile.println("select ");
//		taskSqlFile.print("count(*) as allRowNum ");
//		for(Colnum c :colnumList){
//			taskSqlFile.println(", ");
//			String n=c.colnumName;
//			String profling="sum(if("+n+" is null ,1 ,0)) as nullNum_"+n+" ,sum("+n+") as sum_"+n+" , count(distinct "+n+") as disNum_"+n+"  ";
//			taskSqlFile.print(profling);
//		}
//		taskSqlFile.println();
//		taskSqlFile.println("FROM $db_"+db+"."+tableName+" WHERE pdate='$YYYY-$mm-$dd' ; ");
		taskSqlFile.close();
		//task yaml 文件
		PrintStream  taskYaml=new  PrintStream(thisTaskFolder.getAbsolutePath()+"/task.yaml");
		taskYaml.println("parent: EtlTask ");
		taskYaml.println("task_name: cdc_etl_ka_"+db+"_"+tableName+"_t ");
		taskYaml.println("transform: ");
		taskYaml.println("  - step: hive.hivesql");
		taskYaml.println("    file: sql/"+tableName+".sql");
		taskYaml.println("    time_offset: -1D");
		taskYaml.println();
		taskYaml.close();
		
	}
}
