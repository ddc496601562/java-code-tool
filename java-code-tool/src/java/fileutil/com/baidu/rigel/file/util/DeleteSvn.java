package com.baidu.rigel.file.util;

import java.io.File;

public class DeleteSvn {

	public static void main(String[] args) {
		File dir=new File("D:/E盘文档/保存的代码/code");
		DeleteSvn.deleteSvn(dir);
	}
	public static void deleteAllFile(File dir){
		if(dir.isFile()){
			dir.delete();
			System.out.println(dir.getAbsolutePath());
			return ;
		}
		for(File svn:dir.listFiles()){
			deleteAllFile(svn);
		}
		
	}
	public static void deleteSvn(File dir){
		if(dir.isFile())
			return ;
		if(dir.getName().endsWith("svn"))
			deleteAllFile(dir);
		for(File svn:dir.listFiles()){
			deleteSvn(svn);
		}
		
	}
}



