package com.baidu.rigelci.norbert;

import java.util.List;
import java.util.Map;

public class DataStaticInfo {
	String db_name ;
	String table_name ;
	String dataTime;
	List<String> tags ;
	List<KeyValue> dataProperty ;
	Map<String ,ColnumStaticInfo> colInfo ;
	
}

class ColnumStaticInfo{
	String colName ;
	List<KeyValue> dataProperty ;
	Map<String ,List<String>> colProperty ;
	Map<String,List<KeyValue>> fenbu  ;
}
class KeyValue{
	String  key ;
	String  strValue ;
}
