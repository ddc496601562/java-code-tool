package com.baidu.cdc.client;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperTestMain {

	public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
		// TODO Auto-generated method stub
		ZooKeeper zk = new ZooKeeper("cq01-crm-lin2rd53.vm:2181", 
		        50000, new Watcher() { 
		            public void process(WatchedEvent event) { 
		                System.out.println("已经触发了" + event.getType() + "事件！"); 
		            } 
		        }); 
		zk.create("/testRootPath/testChildPathOne", "testChildDataOne".getBytes(),Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT); 
		System.out.println(new String(zk.getData("/testRootPath",false,null))); 
		System.out.println(zk.getChildren("/testRootPath",true)); 
		zk.create("/testRootPath/testChildPathTwo", "testChildDataTwo".getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT); 
		System.out.println(new String(zk.getData("/testRootPath/testChildPathTwo",true,null))); 
	    // 删除子目录节点
		zk.delete("/testRootPath/testChildPathTwo",-1); 
		zk.delete("/testRootPath/testChildPathOne",-1); 
		// 删除父目录节点
		zk.delete("/testRootPath",-1); 
		// 关闭连接
		zk.close(); 


	}

}
