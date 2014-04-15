package com.baidu.rigel.cdc.flie.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DiffStaticUtil {

	 /**
	   * 获取单个文件的MD5值！
	   * @param file
	   * @return
	   */
	public static String getFileMD5(File file) {
		if (!file.isFile()){
			return null;
		}
		MessageDigest digest = null;
	    FileInputStream in=null;
	    byte buffer[] = new byte[1024];
	    int len;
	    try {
	    	digest = MessageDigest.getInstance("MD5");
	    	in = new FileInputStream(file);
	    	while ((len = in.read(buffer, 0, 1024)) != -1) {
	    		digest.update(buffer, 0, len);
	    	}
	    	in.close();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return null;
	    }
	    BigInteger bigInt = new BigInteger(1, digest.digest());
	    return bigInt.toString(16);
	  }
	/**
	 * 递归比较新版本的文件夹和旧版本的文件夹的差异
	 * @param nowDir     新版本的工程
	 * @param lastDir    旧版本的工程
	 * @param diff       差异  <文件路径,差异(f_add,f_delete,d_add,d_delete,f_modify)>
	 */
	public static void diffDir(File nowDir ,File lastDir,Map<String,String> diff){
		FilenameFilter svnFileFilter=new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				if(name.startsWith(".svn")||
				   name.endsWith(".log") ||
				   name.endsWith("TempStatsStore")||
				   name.endsWith(".tar.gz"))
					return false;
				return true ;
			}
		};
		File[] lastDirFiles=lastDir.listFiles(svnFileFilter);
		File[] nowDirFiles=nowDir.listFiles(svnFileFilter);
		Map<String,File> lastName2File=new HashMap<String,File>();
		for(File file :lastDirFiles){
			if(file.isFile())
				lastName2File.put("f_"+file.getName(), file);
			else
				lastName2File.put("d_"+file.getName(), file);
				
		}
		for(File now :nowDirFiles){
			String name_str=now.isFile()?("f_"+now.getName()):("d_"+now.getName());
			File lastSameFile=lastName2File.remove(name_str);
			if(lastSameFile==null){
				diff.put(now.getName(), now.isFile()?("f_add"):("d_add")) ;
				continue ;
			}
			if(now.isFile()){
				String nowMd5=DiffStaticUtil.getFileMD5(now);
				String lastMd5=DiffStaticUtil.getFileMD5(lastSameFile);
				if(!nowMd5.equals(lastMd5))
					diff.put(now.getName(), "f_modify") ;
			}else
			{
				Map<String,String> diffThis=new HashMap<String,String>();
				DiffStaticUtil.diffDir(now, lastSameFile, diffThis);
				for(Entry<String,String> entry :diffThis.entrySet()){
					diff.put(now.getName()+"/"+entry.getKey(), entry.getValue());
				}
			}
		}
		for(Entry<String,File> entry :lastName2File.entrySet()){
			File fileValue=entry.getValue();
			diff.put(fileValue.getName(), fileValue.isFile()?"f_delete":"d_delete");
		}
	}

}
