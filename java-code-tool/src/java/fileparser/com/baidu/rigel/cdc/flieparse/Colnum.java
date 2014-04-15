package com.baidu.rigel.cdc.flieparse;

import java.util.Comparator;

public class  Colnum implements Comparator<Colnum>{
	public int      index ;
	public String   colnumName ;
	public String   type ;
	public String   comment ;
	public Colnum(int   index,String colnumName,String  type,String comment ){
		this.index=index ;
		this.colnumName=colnumName;
		this.type=type;
		this.comment=comment;
	}
	@Override
	public int compare(Colnum o1, Colnum o2) {
		// TODO Auto-generated method stub
		return o1.index-o2.index;
	}
}
