package com.baidu.rigel.cdc.log4j.test;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;



public class HelloLog4j {
	  
    /** 
     * @param args 
     */  
    public static void main(String[] args) {  
        // System.out.println("This is println message.");   mbn 
    	PropertyConfigurator.configure("D:/eclipse-jee-workspace/java-code-tool/src/java/log4j/com/baidu/rigel/cdc/log4j/test/log4j.properties");
    	Logger logger = Logger.getLogger(HelloLog4j.class);  
    	logger.setLevel(Level.DEBUG);
        // 记录debug级别的信息  
        logger.debug("This is debug message.");  
        // 记录info级别的信息  
        logger.info("This is info message.");  
        // 记录error级别的信息  
        logger.error("This is error message.");  
    }  
}
