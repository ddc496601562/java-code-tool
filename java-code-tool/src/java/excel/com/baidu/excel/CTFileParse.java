package com.baidu.excel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

public class CTFileParse {

	/**
	 * @param args
	 * @throws IOException
	 * @throws BiffException
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		HashMap<Set<String> ,List<CTInfo>> xueyuanMap=new HashMap<Set<String> ,List<CTInfo>>();
		List<CTInfo> ctList=new ArrayList<CTInfo>();
		Map<String,CTInfo> ctMap=new HashMap<String,CTInfo>();
		final Set<String>  ctIds=new HashSet<String>();
 		String filePath = "E:/jobs/集群升级/CDC-CT.xls";
		InputStream fs = null;
		Workbook workBook = null;
		// 加载excel文件
		fs = new FileInputStream(filePath);
		// 得到 workbook
		WorkbookSettings workbookSettings=new WorkbookSettings();
        workbookSettings.setEncoding("GBK"); //关键代码，解决中文乱码
		workBook = Workbook.getWorkbook(fs,workbookSettings);
		Sheet sheet = workBook.getSheet(0);// 这里只取得第一个sheet的值，默认从0开始
		int rowNum=sheet.getRows();
		int pulseCount=0;
		for(int row=1;row< rowNum;row++){
			String id =Long.parseLong(sheet.getCell(0, row).getContents().trim())+"";
			String desc=sheet.getCell(1, row).getContents().trim();
			String host=sheet.getCell(2, row).getContents().trim();
			String crontab=sheet.getCell(3, row).getContents().trim();
			String cmd=sheet.getCell(4, row).getContents().trim();
			String[] lines=sheet.getCell(5, row).getContents().split("\n");
//			if(lines.length>1){
//				System.out.println(sheet.getCell(5, row).getContents());
//				System.out.println("**************");
//			}
			List<String> pre=new ArrayList<String>();
			for(String hang:lines){
				String preId=hang.split(" ")[0].trim();
				if(!"".equals(preId)){
					Long.parseLong(preId);
					pre.add(preId);
					
				}
			}
//			if(!crontab.equals("事件驱动")&&crontab.contains(","))
//				System.out.println(id+"  "+crontab+"  "+desc+"  "+pre);
			if(desc.contains("Pulse")&&(host.equals("db-crm-etl05.db01")||host.equals("db-crm-etl04.db01"))){
				pulseCount++;
				continue;
			}
//			if(pre.equals(""))
//				System.out.println(id+"  "+crontab+"  "+desc+"  "+pre);
			CTInfo ctInfo=new CTInfo(id, desc, host, crontab, cmd, pre);
			ctList.add(ctInfo);
			ctIds.add(id);
			ctMap.put(id, ctInfo);
		}
		System.out.println(pulseCount+"  "+ctList.size()+"  "+rowNum);
		workBook.close();
		while(ctList.size()>0){
			CTInfo firstCt=ctList.remove(0);
			Set<String> ids=new HashSet<String>();
			ids.add(firstCt.id);
			ids.addAll(firstCt.pre);
			List<CTInfo> ctListForOne=new ArrayList<CTInfo>();
			ctListForOne.add(firstCt);
			int firstSize=ctListForOne.size();
			while(true){
				firstSize=ctListForOne.size();
				 Iterator<CTInfo> iterator=ctList.iterator();
				 while(iterator.hasNext()){
					 CTInfo info=iterator.next();
					 boolean isHave=false;
					 if(ids.contains(info.id))
						 isHave=true ;
					 for(String pre :info.pre){
							if(ids.contains(pre)){
								isHave=true ;
							}
					 }
					 if(isHave==true){
						 iterator.remove();
						 ids.add(info.id);
						 ids.addAll(info.pre);
						 ctListForOne.add(info);
					 }
				 }
				if(firstSize==ctListForOne.size()){
					xueyuanMap.put(ids, ctListForOne);
					break ;
				}
					
			}
			
		}
//		for(CTInfo info :ctList){
//			boolean isHave=false;
//			for(Map.Entry<Set<String> ,List<CTInfo>> entry: xueyuanMap.entrySet()){
//				if(entry.getKey().contains(info.id)){
//					isHave=true ;
//				}
//				for(String pre :info.pre){
//					if(entry.getKey().contains(pre)){
//						isHave=true ;
//					}
//				}
//				if(isHave==true){
//					entry.getKey().add(info.id);
//					entry.getKey().addAll(info.pre);
//					entry.getValue().add(info);
//					break;
//				}else{
//					continue ;
//				}
//			}
//			if(isHave==false){
//				Set<String> ids=new HashSet<String>();
//				ids.add(info.id);
//				ids.addAll(info.pre);
//				List<CTInfo> ctListForOne=new ArrayList<CTInfo>();
//				ctListForOne.add(info);
//				xueyuanMap.put(ids, ctListForOne);
//			}
//		}
//		
		Comparator<CTInfo> ctComparator=new Comparator<CTInfo>(){

			@Override
			public int compare(CTInfo o1, CTInfo o2) {
				// TODO Auto-generated method stub
				if(o2.pre.contains(o1.id))
					return -1;
				boolean o1HasPre=false;
				if(o1.pre==null||o1.pre.size()==0)
					o1HasPre=false;
				else{
					for(String pre :o1.pre){
						if(ctIds.contains(pre))
							o1HasPre=true;	
					}
				}
				if(o1HasPre==false)
					return -1;
				return 1;
			}
			
		};
		System.setOut(new PrintStream("e:/ct.log"));
		int i=0;
		for(Map.Entry<Set<String> ,List<CTInfo>> entry: xueyuanMap.entrySet()){
			Collections.sort(entry.getValue(), ctComparator);
			i++;
//			System.out.println(i);
			for(CTInfo ct :entry.getValue()){
				System.out.println(ct.id +"  "+ct.desc+"  "+ct.pre+"  "+ct.crontab+""+"          &&&&&&&  "+ct.cmd);
//				System.out.println(ct.id +"  "+ct.desc+"  "+ct.pre+"  "+ct.crontab+"  "+ct.cmd);
			}
			System.out.println("****************************************************************************************");
			System.out.println("****************************************************************************************");
			System.out.println("****************************************************************************************");
		}
		System.out.println(xueyuanMap.size());
	}
}

class CTInfo{
	String id=null;
	String desc=null;
	String host=null;
	String crontab=null;
	String cmd=null;
	List<String> pre=null;
	public CTInfo(String id,String desc,String host,String crontab,String cmd,List<String> pre){
		this.id=id;
		this.desc=desc;
		this.host=host;
		this.crontab=crontab;
		this.cmd=cmd;
		this.pre=pre;
	}
}
