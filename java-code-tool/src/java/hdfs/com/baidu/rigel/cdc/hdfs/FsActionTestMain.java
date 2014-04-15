package com.baidu.rigel.cdc.hdfs;

import org.apache.hadoop.fs.permission.FsAction;

public class FsActionTestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FsAction[]  enumSet=FsAction.values();
		for(FsAction fa :enumSet)
			System.out.println(fa.ordinal());
	}

}
