package com.baidu.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableHyperlink;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class GetMetaDataLocal {
	public static Map<String,String> global=new HashMap<String,String>();
	static {
		try {
			String filePath="D:/dataget/ts_global_param.dat" ;
			LineNumberReader hdfsReader=new LineNumberReader(new FileReader(filePath));
			String line =null ;
			while((line=hdfsReader.readLine())!=null){
				String key=line.split("\t")[0].trim();
				String value=line.split("\t")[1].trim();
				global.put(key, value);
			}
			hdfsReader.close();
//			for(Entry<String,String> entry:global.entrySet()){
//				System.out.println(entry.getKey()+"$$$$$$$$$$$$$4"+entry.getValue());
//			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws Exception {
		String excelfilePath = "D:/dataget/CDC-CT-local.xls";
		FileOutputStream fileOutputStream = new FileOutputStream(excelfilePath);
		WritableWorkbook writableWorkbook = Workbook.createWorkbook(fileOutputStream);
		WritableSheet firstSheet = writableWorkbook.createSheet("上游数据汇总", 0);
		int sheetIndex=1 ;
		int firstSheetIndex=0;
		FilenameFilter filter2=new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				boolean noAccept=name.toLowerCase().contains("his")
						         ||name.toLowerCase().contains("svn")
						          ||name.toLowerCase().contains(".sh");
				return !noAccept;
			}
		};
		String filePath="D:/eclipse-jee-workspace/cdc-publish/" ;
		String metaPath="D:/dataget/cdc-publish.txt";
		LineNumberReader hdfsReader=new LineNumberReader(new FileReader(metaPath));
		Map<String,List<DirFile>> dirList=new HashMap<String,List<DirFile>>();
		String line =null ;
		while((line=hdfsReader.readLine())!=null){
//			System.out.println(line);
			DirFile dir=new DirFile(line);
			List<DirFile> list=dirList.get(dir.cate);
			if(list==null){
				list=new ArrayList<DirFile>();
				dirList.put(dir.cate, list);
			}
			File metaDir=new File(filePath+dir.cate+"/"+dir.dir+"/e/table_meta/");
			if(!metaDir.exists())
				continue ;
//			System.out.println(metaDir.getAbsolutePath());
			for(File meta:metaDir.listFiles(filter2)){
				Meta cc=new Meta(meta.getAbsolutePath());
				if(cc.type.equals(dir.type)){
//					System.out.println(cc.type);
//					System.out.println(cc.path);
//					System.out.println(cc.cols);
					dir.cols=cc.cols;
					dir.path=cc.path;
					list.add(dir);
					break ;
				}
			}
//			LineNumberReader fileReader=new LineNumberReader(new FileReader(filePath+dir.dir+"/meta/e/"));

		}
		hdfsReader.close();
		Set<String> hashSet=new HashSet<String>();
		for(Entry<String, List<DirFile>> entry:dirList.entrySet()){
			String cate=entry.getKey();
			List<DirFile> list=entry.getValue();
			Label label = new Label(0, firstSheetIndex, cate);
			firstSheet.addCell(label);
			firstSheetIndex++;
			for(DirFile dir :list){
				String sheetName=dir.cate+"-"+dir.name;
				if(hashSet.contains(sheetName))
					System.out.println(sheetName);
				hashSet.add(sheetName);
				if(sheetName.length()>12){
					sheetName=sheetName.substring(0, 12);
				}
				WritableSheet tableSheet = writableWorkbook.createSheet(sheetName, sheetIndex);
				sheetIndex++;
				int thisIndex=0;

				Label labeltype = new Label(0, thisIndex,"路径类型:ftp" );
				
				thisIndex++;
				Label labelpath = new Label(0, thisIndex,dir.path );
				tableSheet.addCell(labeltype);
				tableSheet.addCell(labelpath);
				thisIndex++;
				thisIndex++;
				for(ColNum col :dir.cols){
					Label labelname = new Label(0, thisIndex,col.name );
					Label labelcomment = new Label(1, thisIndex,col.comment );
					tableSheet.addCell(labelname);
					tableSheet.addCell(labelcomment);
					thisIndex++ ;
				}
				tableSheet.addHyperlink(new WritableHyperlink(0,thisIndex,"返回主目录",firstSheet,0,0)); //关键的建立后面sheet连接的一句
//				Label label2 = new Label(1, firstSheetIndex, dir.name);
//				
//				firstSheet.addCell(label2);
				firstSheet.addHyperlink(new WritableHyperlink(1,firstSheetIndex,dir.name,tableSheet,0,0)); //关键的建立后面sheet连接的一句
				firstSheetIndex++;
			}
		}
		writableWorkbook.write();
		writableWorkbook.close();
		fileOutputStream.close();
	}

}
class Meta{
	public Meta(String path){
		List<ColNum> colList=new ArrayList<ColNum>();
		try {
			LineNumberReader fileReader=new LineNumberReader(new FileReader(path));
			String line =null ;
			while((line=fileReader.readLine())!=null){
				if(line.contains("field_list:")){
					line=fileReader.readLine();
					while(line.contains("- field:")){
						String name=line.replace("- field:", "").trim();
						fileReader.readLine();
						String comment=fileReader.readLine().replace("comment:", "").trim();
						colList.add(new ColNum(name,comment));
						line=fileReader.readLine();
//						System.out.println(name+" "+comment);
					}
					String type=line.replace("type:", "").trim();
					String pathFile=fileReader.readLine().replace("file_path:", "").trim();
					int replace1=pathFile.indexOf("${");
					int replace2=pathFile.indexOf("}");
					if(replace1>-1&&replace2>replace1){
						String replace=pathFile.substring(replace1, replace2+1);
						String replaceFrom=pathFile.substring(replace1+2, replace2);
						String replaceTo=GetMetaDataLocal.global.get(replaceFrom);
						if(replaceTo!=null)
							pathFile=pathFile.replace(replace, replaceTo);
//						System.out.println(type+" "+replaceFrom+" "+pathFile);
					}
//					System.out.println(type+" "+pathFile);
					this.cols=colList.toArray(new ColNum[0]);
//					System.out.println(colList.size()+"   "+this.cols.length);
					this.path=pathFile;
					this.type=type;
					break ;
				}
			}
			fileReader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	String type ;
	String path ;
	ColNum[] cols=null ;
}
class ColNum{
	String name ;
	String comment;
	public ColNum(String name ,String comment){
		this.name=name;
		this.comment=comment;
	}
}
class DirFile{
	String   cate ;
	String   dir ;
	String   type ;
	String   name ;
	String   path ;
	ColNum[] cols=null ;
	public DirFile(String line){
		String[] cc=line.trim().split(" ");
		if(cc.length!=4&&cc.length!=3)
			return ;
		this.cate=cc[0];
		this.dir=cc[1];
		this.type=cc[2];
		if(cc.length==3)
			this.name=cc[1];
		else 
			this.name=cc[3];
	}
}
