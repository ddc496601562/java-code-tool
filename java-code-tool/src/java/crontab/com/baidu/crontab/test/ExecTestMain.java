package com.baidu.crontab.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.baidu.crontab.exec.TaskExecManager;
import com.baidu.crontab.model.TaskModel;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

public class ExecTestMain {

	public static void main(String[] args) throws BiffException, IOException {
		String path=args[0];
		int index=Integer.parseInt(args[1]);
		InputStream fs = new FileInputStream(path);
		WorkbookSettings workbookSettings=new WorkbookSettings();
        workbookSettings.setEncoding("GBK"); //关键代码，解决中文乱码
        Workbook workBook = Workbook.getWorkbook(fs,workbookSettings);
		Sheet sheet = workBook.getSheet(index);// 这里只取得第一个sheet的值，默认从0开始
		int rowNum=sheet.getRows();
		List<TaskModel> taskGroup=new ArrayList<TaskModel>();
		for(int row=1;row< rowNum;row++){
			TaskModel task=new TaskModel();
			task.setTaskId(sheet.getCell(0, row).getContents().trim())
			    .setTaskDesc(sheet.getCell(1, row).getContents().trim())
			    .setScriptCmd(sheet.getCell(4, row).getContents().trim());
		    String preStr=sheet.getCell(2, row).getContents().trim();
		    if(StringUtils.isNotBlank(preStr)){
		    	task.setPreTaskList(Arrays.asList(preStr.split(",")));
		    }else{
		    	task.setPreTaskList(new ArrayList<String>());
		    }
			String[] env=sheet.getCell(3, row).getContents().trim().split("=");
			Map<String,String> map=new HashMap<String,String>();
			map.put(env[0], env[1]);
			task.setEnvironment(map);
			task.setNeedOutLog(false);
			task.setNeedErrorLog(false);
			taskGroup.add(task);
			
			System.out.println(ReflectionToStringBuilder.toString(task, ToStringStyle.MULTI_LINE_STYLE));
		}
		workBook.close();
		TaskExecManager tm=new TaskExecManager(taskGroup,0,10,"测试任务1");
		tm.run();
		System.out.println(tm.getResMessage());
		System.out.println(tm.getTaskExecManagerStatus());
		for(TaskModel task :taskGroup){
			System.out.println(tm.getExecTime(task.getTaskId()));
		}
		
	}

}
