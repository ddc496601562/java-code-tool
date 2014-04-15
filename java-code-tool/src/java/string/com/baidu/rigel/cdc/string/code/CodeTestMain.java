package com.baidu.rigel.cdc.string.code;

public class CodeTestMain {
	static int i=0 ;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String dec=StringBase64.bc_base64_enc("a=b?get!!!put哈啊哈哈哈");
	    System.out.println(dec);
	    System.out.println(StringBase64.bc_base64_dec(dec));
	    String columnValue[] = new String[2];
	    String columnValue2[] = new String[args.length];
	    System.out.println(inc());
	    System.out.println(inc());
	    System.out.println(inc());
	}
    public static int inc(){
    	return i++;
    }
}
