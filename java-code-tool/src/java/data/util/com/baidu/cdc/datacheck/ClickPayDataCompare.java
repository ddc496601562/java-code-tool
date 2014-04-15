package com.baidu.cdc.datacheck;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;


public class ClickPayDataCompare {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String devDir="D:/SecureCRT file/down/dev/";
		String productDir="D:/SecureCRT file/down/product/";
		String diffDir="D:/SecureCRT file/down/diffLog/";
		for(File devFile:(new File(devDir)).listFiles()){
			String devFileName=devFile.getName();
			PrintStream diffOut=new PrintStream(diffDir+devFileName.replaceAll("_dev", ""));
			System.out.println("compare file is :"+devFileName);
			if(!devFileName.endsWith("_dev"))
				continue;
			String productFileName=devFileName.replaceAll("_dev", "_product");
			LineNumberReader devReader=new LineNumberReader(new FileReader(devDir+devFileName));
			LineNumberReader productReader=new LineNumberReader(new FileReader(productDir+productFileName));
			String devLine=null;
			String productLine=null;
			int formatDiff=0;
			int valueDiff=0;
			while((devLine=devReader.readLine())!=null){
				productLine=productReader.readLine();
				if(devLine.equals(productLine))
					continue;
				String[] devArray=devLine.split("	");
				String[] productArray=productLine.split("	");
				if(  (!devArray[0].equals(productArray[0]))  ||
				     (!devArray[1].equals(productArray[1]))  ||
				     (!devArray[2].equals(productArray[2]))  ||
				     (!devArray[3].equals(productArray[3]))  ||
				     (Float.parseFloat(devArray[4])!=Float.parseFloat(devArray[4]))  ||
				     (Float.parseFloat(devArray[5])!=Float.parseFloat(devArray[5]))  ||
				     (!devArray[6].equals(productArray[6]))  ){
					valueDiff++;
					diffOut.println(devLine);
					diffOut.println(productLine);
					diffOut.println("*************************************");
					continue;
				}
				formatDiff++;
			}
			productLine=productReader.readLine();
			if(productLine!=null){
				System.out.println("line number is not same!!!");
			}
			System.out.println("formatDiff size is :"+formatDiff+" valueDiff size is :"+valueDiff);
			devReader.close();
			productReader.close();
		}
	}

}
