package com.ztgeo.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


import com.ztgeo.staticParams.StaticParams;

public class FormateData {

	
	//获取当前的系统时间
	public static String getNowTime(){
		Date date = new Date();
		SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
		String dateS = fm.format(date);
		return dateS;
	}
	
/*	//加密数组转16进制输出
	public static String converByteToHashString(byte[] bytes){
		String result ="";
		for (int i = 0; i < bytes.length; i++) {
			int temp = bytes[i] & 0xff;
			String tempHex = Integer.toHexString(temp);
			if(tempHex.length()<2){
				result +="0" +tempHex;
			}else{
				result += tempHex;
			}
		}
		return result;
	}
	
	public static String getMd5(String str){
		//使用md5加密
		 MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
			  //BASE64Encoder base64en = new BASE64Encoder();
			 //加密后的字符串
			 str = converByteToHashString(md5.digest(str.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
		
		return str;
	}*/
	
	// MD5转换


}

