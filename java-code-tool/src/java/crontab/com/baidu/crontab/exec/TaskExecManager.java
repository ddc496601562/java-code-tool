package com.baidu.crontab.exec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baidu.crontab.model.TaskModel;
import com.baidu.crontab.util.DateUtil;

/**
 * 对于一批有依赖关系的CT进行调度执行，并发并且支持依赖关系
 * @author dingdongchao
 *
 */
public class TaskExecManager implements Runnable {
	private static final Log LOG = LogFactory.getLog(TaskExecManager.class);

	//0-未调度  1-执行中  2-执行成功   3-执行失败  41-超时杀死  42 -因上游失败而被饿死    43 - 手动cancel而杀死
	public int EXEC_STATUS_PENDING=0;
	public int EXEC_STATUS_RUNNING=1;
	public int EXEC_STATUS_SUCCESS=2;
	public int EXEC_STATUS_FAIL=3;
	public int EXEC_STATUS_KILLED=4;
	public int EXEC_STATUS_KILLED_TIMEOUT=41;
	public int EXEC_STATUS_KILLED_STARVE=42;
	public int EXEC_STATUS_KILLED_CANCEL=43;
	private Map<String,TaskExecResult> execResMap=new HashMap<String,TaskExecResult>();
	private Map<String,TaskModel>  taskMap=new HashMap<String,TaskModel>();
	private String  taskManagerName="";
	private String  logTaskManagerName="";
	private String script_path=null; 
	private String log_path=null;
	/* 一批任务中允许失败的任务数目 
	       当已经失败的task num达到这个值，将杀死所有的待执行和正在执行的task
	       如果一个task失败，则会将所有直接依赖/间接依赖的下游task置为状态42，也算是fail的任务，
	       若fail num达到failuresTaskNum，则fail整个task group
	*/
	private int failuresTaskNum=0 ;
	//该TaskExecManager同时运行的task上限,默认值是10
	private int maxParallelTaskNum=10;
	private final List<TaskModel> taskGroup  ;
	private String  resMessage="not begin !!" ;
	private boolean taskGroupSuccess=false ;
	private boolean cancel=false ;
	//整个taskExecManager的状态，0--未执行  1--正在执行   2--成功  3--失败  
	private int     taskExecManagerStatus=0;

	/**
	 * 
	 * @param taskGroup             待执行的task group  
	 * @param failuresTaskNum       允许fail的数目   
	 * @param maxParallelTaskNium   该TaskExecManager同时运行的task上限
	 * @param taskManagerName       这个执行的名称，多个同时执行时便于区分 
	 */
	public  TaskExecManager(List<TaskModel> taskGroup ,int failuresTaskNum ,int maxParallelTaskNum,String taskManagerName){
		if(taskGroup==null||taskGroup.size()==0)
			throw  new IllegalArgumentException("Illegal task group ,check it !!!");
		script_path=DateUtil.SCRIPT_PATH; 
		log_path=DateUtil.LOG_PATH ;
		this.taskGroup=taskGroup;
		this.failuresTaskNum=Math.max(0, failuresTaskNum);
		this.maxParallelTaskNum=Math.max(1, maxParallelTaskNum);
		for(TaskModel task:taskGroup){
			String scriptId=DateUtil.yyyyMMddHHmmss.format(new Date())+"_"+task.getTaskId()+"_"+(long)(Math.random()*1000);
			execResMap.put(task.getTaskId(), new TaskExecResult(scriptId));
			taskMap.put(task.getTaskId(), task);
		}
		this.taskManagerName=taskManagerName;
		//加上打印日志时候的前缀，避免每次打印均需要使用字符串 + 的操作 。
		this.logTaskManagerName=this.taskManagerName+" -- ";
		//在虚拟机关闭的时候，对于未执行完的task执行进程进行清理 
	    Runtime.getRuntime().addShutdownHook(new Thread() {
	    	  public void run() {
	    		  clear();
	    	  }
	    });
	    LOG.info(this.logTaskManagerName+"init task group Complete ,all task list ");
	    LOG.info(this.logTaskManagerName+"*************************************************************************");
	    for(TaskModel task:this.taskGroup){
	    	LOG.info(this.logTaskManagerName+task.getTaskId()+" "+task.getTaskDesc()+" "+task.getScriptCmd());
	    }
	    LOG.info(this.logTaskManagerName+"*************************************************************************");
	}
	public TaskExecManager(List<TaskModel> taskGroup,String taskManagerName){
		this(taskGroup,0,10,taskManagerName);
	}
	public void run() {
		this.taskExecManagerStatus=1 ;
		this.resMessage="taskManager is running!!!" ;
		while(!cancel){
			//流程1--检查失败的失败task是否超标 
			int failNum= 0 ;
			int overTaskNum=0 ;
			for(TaskExecResult res:this.execResMap.values()){
				if(res.execStatus==EXEC_STATUS_FAIL||res.execStatus/10==EXEC_STATUS_KILLED)
					failNum++ ;
				if(res.execStatus!=EXEC_STATUS_PENDING&&res.execStatus!=EXEC_STATUS_RUNNING)
					overTaskNum++ ;
			}
			if(failNum>this.failuresTaskNum){
				this.clear();
				this.taskExecManagerStatus=3 ;
				this.resMessage="失败task太多,taskManager fail!" ;
				LOG.info(this.logTaskManagerName+"失败task太多，清理task group并退出 ，failuresTaskNum is "+failNum);
				break ;
			}
			if(overTaskNum==this.taskGroup.size()){
				this.taskExecManagerStatus=2;
				this.resMessage="task group 执行完成 " ;
				LOG.info(this.logTaskManagerName+"task group 执行完成!! ,共完成数目 "+overTaskNum);
				break ;
			}
			//流程2--检查是否有运行完毕的process ，并标志运行状态
			for(Map.Entry<String, TaskExecResult> resEntry:execResMap.entrySet()){
				TaskModel task=taskMap.get(resEntry.getKey());
				TaskExecResult res=resEntry.getValue();
				if(res.execStatus==EXEC_STATUS_RUNNING){
					try {
				    	  int exitCode=res.process.exitValue();
				    	  res.exitCode=exitCode ;
				    	  res.end=System.currentTimeMillis();
				    	  if(task.getSuccReCode()==exitCode){
				    		  res.execStatus=EXEC_STATUS_SUCCESS;
				    		  LOG.info(this.logTaskManagerName+task.getTaskId()+" "+task.getTaskDesc()+" success ,exitCode is "+exitCode+" ,succReCode is "+task.getSuccReCode());
				    	  }else{
				    		  res.execStatus=EXEC_STATUS_FAIL;
				    		  LOG.info(this.logTaskManagerName+task.getTaskId()+" "+task.getTaskDesc()+" fail ,exitCode is "+exitCode+" ,succReCode is "+task.getSuccReCode());
				    	  }
				        } catch (IllegalThreadStateException e) {
				        }
				}
			}
			//流程3--检查是否有可运行的task ，运行的条件：无前置依赖 或者  在task-group中的前置依赖都已经运行成功 。
			int parallelTaskNum=0;
			for(TaskExecResult res:this.execResMap.values()){
				if(res.execStatus==EXEC_STATUS_RUNNING)
					parallelTaskNum++ ;
			}
			for(TaskModel task:this.taskGroup){
				TaskExecResult res=this.execResMap.get(task.getTaskId());
				//这个判断其实是应该放在for each外面的，但是为了代码不至于太乱，所以放在这里 
				if(parallelTaskNum>=this.maxParallelTaskNum)
					break ;
				if(this.execResMap.get(task.getTaskId()).execStatus!=EXEC_STATUS_PENDING)
					continue;
				boolean preReady=true ;
				if(CollectionUtils.isEmpty(task.getPreTaskList())){
					preReady=true ;
				}else{
					for(String preTaskId:task.getPreTaskList()){
						TaskExecResult preTaskRes=this.execResMap.get(preTaskId);
						//如果其preTask在task group中且状态不为EXEC_STATUS_SUCCESS，则该任务无法运行
						if(preTaskRes!=null&&preTaskRes.execStatus!=EXEC_STATUS_SUCCESS)
							preReady=false ;
					}
				}
				if(preReady){
					LOG.info(this.logTaskManagerName+task.getTaskId()+" "+task.getTaskDesc()+" pre task ready ,begin process !!!");
					//转换成一个可执行脚本
					String  scriptFile=script_path+res.scriptId+".sh" ;
					try {
						PrintStream out=new PrintStream(scriptFile);
						out.println("#/bin/sh");
						for(Map.Entry<String ,String> entry:  task.getEnvironment().entrySet()){
							out.println("export "+entry.getKey()+"="+entry.getValue()+" "); 
						}
						out.println(task.getScriptCmd());
						out.println("exit $? ");
						out.close();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
					LOG.info("sh "+scriptFile);
					ProcessBuilder builder = new ProcessBuilder(new String[]{"sh",scriptFile});
					if(task.getDir()!=null){
						builder.directory(new File(task.getDir()));
					}
					TaskExecResult taskRes=this.execResMap.get(task.getTaskId());
					taskRes.start=System.currentTimeMillis();
					try {
						taskRes.process=builder.start();
						taskRes.errReader = new BufferedReader(new InputStreamReader(taskRes.process.getErrorStream()));
						taskRes.outReader = new BufferedReader(new InputStreamReader(taskRes.process.getInputStream()));
						taskRes.execStatus=EXEC_STATUS_RUNNING;
						LOG.info(this.logTaskManagerName+res.scriptId+" "+task.getTaskDesc()+" 正常启动，状态转换为 running....");
						parallelTaskNum++ ;
					} catch (IOException e) {
						LOG.info(this.logTaskManagerName+res.scriptId+" "+task.getTaskDesc()+" "+task.getScriptCmd()+" 启动异常！！");
						e.printStackTrace();
						LOG.error(e.getMessage(), e);
						LOG.info(this.logTaskManagerName+e.getStackTrace());
						taskRes.execStatus=EXEC_STATUS_FAIL;
						this.setSubTaskStatus(task.getTaskId(), EXEC_STATUS_KILLED_STARVE);
					}
				}
			}
			//流程4--杀死超时任务，同时将该task的直接/间接子task，状态全部置为fail 
			for(Map.Entry<String, TaskExecResult> resEntry:execResMap.entrySet()){
				TaskModel task=taskMap.get(resEntry.getKey());
				TaskExecResult res=resEntry.getValue();
				if(res.execStatus==EXEC_STATUS_RUNNING
				 &&task.getTimeOutInterval()>0
				 &&(System.currentTimeMillis()-res.start)>task.getTimeOutInterval()){
					res.process.destroy();
					res.execStatus=EXEC_STATUS_KILLED_TIMEOUT;
					this.setSubTaskStatus(task.getTaskId(), EXEC_STATUS_KILLED_STARVE);
					res.end=System.currentTimeMillis();
					LOG.info(this.logTaskManagerName+task.getTaskId()+" "+task.getTaskDesc()+" timeout ,killed!! sub task cancel!!");
				}
			}
			//流程5--清理不需要的日志，减少buffer缓存
			for(Map.Entry<String, TaskExecResult> resEntry:execResMap.entrySet()){
				TaskModel task=taskMap.get(resEntry.getKey());
				TaskExecResult res=resEntry.getValue();
				if(res.process==null||res.errReader==null||res.outReader==null)
					continue;
				String nullLine=null ;
				try {
					if (!task.isNeedErrorLog()) {
						PrintStream err=new PrintStream(log_path+res.scriptId+".err.log");
						while ((nullLine = res.errReader.readLine()) != null) {
							err.println(nullLine);
						}
						err.close();
					}
					nullLine=null ;
					if (!task.isNeedOutLog()) {
						PrintStream out=new PrintStream(log_path+res.scriptId+".out.log");
						while ((nullLine = res.outReader.readLine()) != null) {
							out.println(nullLine);
						}
						out.close();
					}
					
				} catch (IOException ie) {
					LOG.info(this.logTaskManagerName + task.getTaskId()+" "+ie.getStackTrace());
				}
				
			}
			try {
				LOG.info(this.logTaskManagerName + "sleep 5s ,waiting......");
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				LOG.info(this.logTaskManagerName+e.getStackTrace());
			}
		}
		if(cancel){
			this.cancel();
			this.clear();
			this.taskExecManagerStatus=3;
			this.resMessage="clear task group execManager!!!" ;
		}
	}
	public void cancel(){
		this.cancel=true ;
	}
	//设置一个task的所有子task的状态为execStatus
	private void setSubTaskStatus( String taskId ,int execStatus){
		Set<String> subTaskList=new HashSet<String>();
		//通过一遍遍的遍历taskGroup，若是找到一个前置id是taskId或者在subTaskList，则将这个任务加入到subTaskList
		//然后继续遍历，知道一次遍历后subTaskList的大小不变 
		while(true){
			int subTaskNum=subTaskList.size();
			for(TaskModel task:this.taskGroup){
				for(String preTask:task.getPreTaskList()){
					if(preTask.equals(taskId)||subTaskList.contains(preTask))
						subTaskList.add(task.getTaskId());
				}
			}
			if(subTaskNum==subTaskList.size())
				break ;
		}
		for(String subTaskId:subTaskList)
			this.execResMap.get(subTaskId).execStatus=execStatus;
		
	}
	//清理所有未完成的子进程
	private void clear(){
		for(TaskExecResult res:execResMap.values()){
			  Process process=res.process;
			  //如果已经开始且未执行完，则kill掉进程
			  if(process!=null){
			      try {
			    	  process.exitValue();
			        } catch (IllegalThreadStateException e) {
			        	process.destroy();
			        	res.execStatus= EXEC_STATUS_KILLED_CANCEL;
			        }
			  }
		  }
	}
	//正常输出流
	public BufferedReader getOutBufferedReader(String taskId){
		return this.execResMap.get(taskId).outReader;
	}
	//错误输出流
	public BufferedReader getErrBufferedReader(String taskId){
		return this.execResMap.get(taskId).errReader;
	}
	//开始时间
	public long getExecStart(String taskId){
		return this.execResMap.get(taskId).start ;
	}
	//结束时间
	public long getExecEnd(String taskId){
		return this.execResMap.get(taskId).end ;
	}
	//运行时间
	public long getExecTime(String taskId){
		return this.execResMap.get(taskId).end-this.execResMap.get(taskId).start;
	}
	//执行状态
	public int getrrExecStatus(String taskId){
		return this.execResMap.get(taskId).execStatus;
	}
	//执行的返回码
	public int getexitCode(String taskId){
		return this.execResMap.get(taskId).exitCode;
	}
	public String getTaskManagerName() {
		return taskManagerName;
	}
	public List<TaskModel> getTaskGroup() {
		return taskGroup;
	}
	public String getResMessage() {
		return resMessage;
	}
	public boolean isTaskGroupSuccess() {
		return taskGroupSuccess;
	}
	public int getTaskExecManagerStatus() {
		return taskExecManagerStatus;
	}
	
}






class TaskExecResult{
	public TaskExecResult(String scriptId){
		this.scriptId= scriptId ;
	}
	String scriptId=null ;
	int exitCode=Integer.MIN_VALUE;
	//0-未调度  1-执行中  2-执行成功   3-执行失败  41-超时杀死  42 -因上游失败而被饿死  43-因task group失败而被杀死  44 - 手动cancel二杀死
	int execStatus=0 ;
	//在任务被调度起来前，这些对象都是null的 。
	// sub process used to execute the task command
	//开始时间
	long start=Long.MAX_VALUE ;
	//结束时间
	long end=0 ;
	Process process=null ;
	BufferedReader errReader = null ;
	BufferedReader outReader = null ;
}