package com.baidu.rigel.cdc.string.code;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RemoveTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<String> chars=new ArrayList<String>();
		chars.add("a");
		chars.add("b");
		chars.add("c");
		chars.add("d");
		chars.add("e");
		Iterator<String> iterator=chars.iterator();
		while(iterator.hasNext()){
			String a=iterator.next();
			if(a.equals("a"))
				iterator.remove();
		}
		System.out.println(chars);
		iterator=chars.iterator();
		while(iterator.hasNext()){
			String a=iterator.next();
			if(a.equals("c"))
				iterator.remove();
		}
		System.out.println(chars);
		for(String cc:chars){
			if(cc.equals("e"))
				chars.remove(0);
		}
	}

}
