package com.baidu.excel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.LineNumberReader;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

public class Src_Class_Money {

	public static void main(String[] args) throws Exception {
		String dirPath = "D:/ tmp_data";
		LineNumberReader hdfsReader=new LineNumberReader(new FileReader(dirPath+"table.txt"));
		String  line =null ;
		while((line=hdfsReader.readLine())!=null){
			
		}
	}

}
