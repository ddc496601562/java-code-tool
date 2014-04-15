package com.baidu.crontab.model;

import java.util.List;
import java.util.Map;


public class TaskModel {
	//任务id
	private String taskId=null ;
	//任务的描述
	private String taskDesc=null;
	//执行命令
	private String scriptCmd="echo bank cmd ,bank exec .......happy life ";
	//哪些返回值表示执行成功 ，默认0 
	private Integer succReCode=0;
	//前置任务id
	private List<String> preTaskList=null ;
	//cmd命令执行环境 
	private Map<String, String> environment; // env for the command execution
	//cmd命令执行时候的当前文件夹
	private String dir;
	//超时时间,若是为0或者-1则没有超时限制
	private long  timeOutInterval=-1 ;
	//是否需要日志，不需要的话，执行框架会将日志print向/dev/null 
	private boolean isNeedOutLog=false;
	private boolean isNeedErrorLog=false;
	public TaskModel setTaskId(String taskId) {
		this.taskId = taskId;
		return this ;
	}
	public TaskModel setTaskDesc(String taskDesc) {
		this.taskDesc = taskDesc;
		return this ;
	}
	public TaskModel setScriptCmd(String scriptCmd) {
		this.scriptCmd = scriptCmd;
		return this ;
	}
	public TaskModel setSuccReCode(Integer succReCode) {
		this.succReCode = succReCode;
		return this ;
	}
	public TaskModel setPreTaskList(List<String> preTaskList) {
		this.preTaskList = preTaskList;
		return this ;
	}
	public TaskModel setEnvironment(Map<String, String> environment) {
		this.environment = environment;
		return this ;
	}
	public Integer getSuccReCode() {
		return succReCode;
	}
	public TaskModel setDir(String dir) {
		this.dir = dir;
		return this ;
	}
	public TaskModel setTimeOutInterval(long timeOutInterval) {
		this.timeOutInterval = timeOutInterval;
		return this ;
	}
	public TaskModel setNeedOutLog(boolean isNeedOutLog) {
		this.isNeedOutLog = isNeedOutLog;
		return this ;
	}
	public TaskModel setNeedErrorLog(boolean isNeedErrorLog) {
		this.isNeedErrorLog = isNeedErrorLog;
		return this ;
	}
	public String getTaskId() {
		return taskId;
	}
	public String getTaskDesc() {
		return taskDesc;
	}
	public String getScriptCmd() {
		return scriptCmd;
	}
	public List<String> getPreTaskList() {
		return preTaskList;
	}
	public Map<String, String> getEnvironment() {
		return environment;
	}
	public String getDir() {
		return dir;
	}
	public long getTimeOutInterval() {
		return timeOutInterval;
	}
	public boolean isNeedOutLog() {
		return isNeedOutLog;
	}
	public boolean isNeedErrorLog() {
		return isNeedErrorLog;
	}
}