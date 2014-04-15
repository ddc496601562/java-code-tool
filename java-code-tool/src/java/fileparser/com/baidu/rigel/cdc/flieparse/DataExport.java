package com.baidu.rigel.cdc.flieparse;

import java.io.File;
import java.io.FilenameFilter;

public class DataExport {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FilenameFilter filter=new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				String names="audit fc holmes  ka pss sf  weihu beidou ccd finance itac kafc mcc qiushi uc";
				return names.contains(name);
			}
		};
		FilenameFilter filter2=new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				boolean noAccept=name.toLowerCase().contains("his")
						         ||name.toLowerCase().contains("svn")
						          ||name.toLowerCase().contains(".sh");
				return !noAccept;
			}
		};
		String allDirStr="D:/eclipse-jee-workspace/cdc-publish/";
		File dirAll=new File(allDirStr);
		for(File dir :dirAll.listFiles(filter)){
			for(File file:dir.listFiles(filter2)){
				System.out.println(dir.getName()+" "+file.getName()+" "+"local");
			}
		}
	}

}
