package com.baidu.rigel.cdc.hdfs;


public class ThreadTestMain {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		Thread sleepThread=new SleepThread();
		Thread waitThread=new WaitThread();
		sleepThread.start();
		waitThread.start();
		Thread.sleep(1000);
		sleepThread.interrupt();
		waitThread.interrupt();
	}

}


class SleepThread  extends Thread {
	public void run(){
		while(true){
			try {
				Thread.sleep(10000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
		System.out.println("sleep 线程结束");
	}
}


class WaitThread  extends Thread {
	public void run(){
		while(true){
			try {
				synchronized(this){
					this.wait(10000L);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
		System.out.println("wait 线程结束");
	}
}