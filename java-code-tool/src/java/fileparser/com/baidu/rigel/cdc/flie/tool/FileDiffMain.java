package com.baidu.rigel.cdc.flie.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FileDiffMain {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length<2)
			System.exit(0);
		File lastDir=new File(args[0]);
		File nowDir=new File(args[1]);
		Map<String,String> diff=new HashMap<String,String>();
		DiffStaticUtil.diffDir(nowDir, lastDir, diff);
		List<Entry<String,String>> diffResult=new ArrayList<Entry<String,String>>(diff.entrySet());
		//按照文件名排序
		Comparator<Entry<String,String>> comparator=new Comparator<Entry<String,String>>(){
			@Override
			public int compare(Entry<String, String> o1,
					Entry<String, String> o2) {
				// TODO Auto-generated method stub
				return o1.getKey().compareTo(o2.getKey());
			}
		};
		Collections.sort(diffResult, comparator);
		//输出diff
		for(Entry<String,String> entry :diffResult){
			System.out.println(entry.getKey()+"   "+entry.getValue());
		}
		System.exit(diffResult.size());
	}

}
