package com.baidu.rigel.cdc.string.code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 提供和sf-enc模块中非标准base64的url编码，解码功能函数
 * @since 1.0.0
 * @author ying
 *
 */
public class StringBase64 {
	 
	// Base64编码字符表
	static char BASE64CHAR[]= "0KajD7AZcF2QnPr5fwiHRNygmupUTIXx69BWb-hMCGJo_V8Eskz1YdvL34letqSO".toCharArray();
	static byte LOW[] = { 0x0, 0x1, 0x3, 0x7, 0xF, 0x1F, 0x3F };
	static char BASE64VAL[] = {
		(char)-1,(char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1,
		(char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1,
		(char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, 37, (char) -1, (char) -1,
		0, 51, 10, 56, 57, 15, 32, 5, 46, 33, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1,
		(char) -1, 6, 34, 40, 4, 47, 9, 41, 19, 29, 42, 1, 55, 39, 21, 63,
		13, 11, 20, 62, 28, 27, 45, 35, 30, 52, 7, (char) -1, (char) -1, (char) -1, (char) -1, 44,
		(char) -1, 2, 36, 8, 53, 59, 16, 23, 38, 18, 3, 49, 58, 24, 12, 43,
		26, 61, 14, 48, 60, 25, 54, 17, 31, 22, 50, (char) -1, (char) -1, (char) -1, (char) -1, (char) -1
	};

	/**
	 * 编码函数
	 * @author 	ying
	 * @since	1.0.0
	 * @param 	in 		待编码字符串
	 * @return 	String	编码后字符串
	 */
	public static String bc_base64_enc(String in){
		
		if(in==null||in.length()<1)
		{
			return null;
		}
		byte[] inByte = in.getBytes();
		int inlen = in.getBytes().length;
		int outlen = 0;
		if(inlen%3>0){
			outlen = inlen * 8 / 6 +1;
		}else{
			outlen = inlen * 8 / 6 +0;
		}
		char[] out = new char[outlen];
		
		int l = outlen;
		byte b = 0;
		int n=0;
		int p=0;
		
		while (l> 0)
		{
			b = 0;
			if (n > 0)
			{
				if(inByte[p]<0)
				{
					b |= ((inByte[p]+256)& LOW[n]) << (6 - n);
					p++;
				}else{
					b |= (inByte[p] & LOW[n]) << (6 - n);
					p++;
				}
			}
			n = 6 - n;
			if (n > 0 && inlen > 0 )
			{
				if (p<inlen) 
				{
					if(inByte[p]<0)
					{
						b |= (inByte[p]+256) >> (8 - n);
						n = 8 - n;
					}else{
						b |= inByte[p] >> (8 - n);
						n = 8 - n;
					}
				}else if (p==inlen)
				{
					b |= 0 >> (8 - n);
					n = 8 - n;
				}
			}
			out[outlen-l] = BASE64CHAR[b];
			l--;			
		}
		
		return new String(out);
	}
	
	/**
	 * 解码函数
	 * @author 	ying
	 * @since	1.0.0
	 * @param 	in	 	待解码字符串
	 * @return	String	解码后字符串
	 */
	public static String bc_base64_dec(String in)
	{

		int inlen = in.getBytes().length;
		int outlen = 0;
		
		if(in==null || inlen<1 || (inlen * 6) % 8 >= 6)
		{
			return null;
		}
		
		byte[] inByte = in.getBytes();
		for (int i = 0; i < inlen; i++)
		{
			if (inByte[i] > 127 || BASE64VAL[inByte[i]] == (char)-1)
			{
				return null;
			}
		}
		
		outlen = inlen * 6 / 8;
		char[] out = new char[outlen];
		byte[] outbyte = new byte[outlen];
		
		int l = outlen;
		int n=0;
		int p=0;
		
		while (l > 0)
		{
			if (n > 0)
			{
				out[outlen-l] |= (BASE64VAL[in.charAt(p)] & LOW[n]) << (8 - n);
				p++;
			}
			n = 8 - n;
			if (n >= 6)
			{
				out[outlen-l] |= BASE64VAL[in.charAt(p)] << (n - 6);
				n -= 6;
				p++;
			}
			if (n > 0)
			{
				out[outlen-l] |= BASE64VAL[in.charAt(p)] >> (6 - n);
				n = 6 - n;
			}
			l--;
		}
		for(int i=0;i<outlen;i++)
		{
			if(out[i]>127)
				outbyte[i]=(byte) (out[i]-256);
			else
				outbyte[i]=(byte) out[i];
		}
		return new String(outbyte);
		
	}
	
	 public static void main(String[] args) throws IOException { 
		 
		
		    String a = "dddd123";

		    String encode = bc_base64_enc(a);
			System.out.println("afer encode is:"+encode);
			encode="IgF_5y9YIZ0lQzqLILT8IvN8UybknHf8mvqVQLI-UhdGQvu9UMI-UBqCugwEUhI3pyN4pitznj04QH0LQHDLQ1csnjbsP1DLnHfLPjfzQh9YUys0IAYqnH61PHm30Zwd5gRzP1msn1m0IvsqnsKWUMw85HRkmh7GIv78gvPsT6KYI1Ys0A7bmvk9TLnqn0KWpjYs0Zw9TWYz0APzm1Y1nHbdn0K";
			//encode = "9TDK000aZ79Sz4vrTk2GnLLCNh4chvqi7a-mq0GHik9OMm2bPtYFfqLNP72_Nwu1kNUAzl0Rt9QSsINyIhDzNFlCguoLI8XsFgXQxf46eZ_5KGcQDjijNC2eUG-a.Db_ig4x27nNKhFPKDakn5dG1T_rrumuCyPLWkoo0.IgF_5y9YIZ0lQzq8ugI1Qh-huy4MQhPEUiqVmy-8UA78uaq1TANWpy7_QvkGIgGCpyGdUBqWUv4Yuy4YQHcEuANYmy-_g1csnHFxnjnEnHnEnHnkPjf4P1Kxna41pZwVU0KYUHYknjf4rjR0IZRqn0KLUjYv0AP8IA3qpyu-UhIxmLKz0ZwL5Hc0mywWUA71T1Ys0APC5H00IA7z5Hc0mLFW5HD3rHfY0j";
			String decode = bc_base64_dec(encode);
			System.out.println(decode);
			encode="IgF_5y9YIZ0lQzqLILT8mhqETvC8mvqVQsKYUHYdnjT4r0KYIHYs0ZI_5Hc0mv4YUWdYIZbsrj64gLwsgvPsT6KYI1Ys0A7bmvk9TLnqn6KWpjYs0Zw9TWYz0APzm1Y3njn40Z";
			decode = bc_base64_dec(encode);
			System.out.println(decode);
		
	 }
}
