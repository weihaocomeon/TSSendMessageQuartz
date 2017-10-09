package com.ztgeo.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import com.ztgeo.staticParams.StaticParams;

public class FormateData {

	
	//获取当前的系统时间
	public static String getNowTime(){
		Date date = new Date();
		SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
		String dateS = fm.format(date);
		return dateS;
	}
	
	public static String spitTime(String time){
		return time.substring(0, time.indexOf("."));
	}
	
	public static String getresult(String resultInt){
		String result =null;
		switch (resultInt) {
		case "Delivered":
		case "DeliveryToTerminal":
			result = null;
			break;
		case "DeliveryUncertain":
			result = "发送状态不可查";
			break;
		case "DeliveryImpossible":
			result = "无法发送";
			break;
		case "MessageWaiting":
			result = "排队等待发送中";
			break;	
		case "DeliveryNotificationNotSupported":
			result = "不支持短消息提交通知";
			break;	
		default:
			result = "其他类型错误";
			break;
		}
		return result;
	}
	
	//字符串的截取 
	public static String substr(String str){
		String ywlx = str.substring(str.indexOf("办理")+2,str.indexOf("业务"));
		String slbh = str.substring(str.indexOf("受")+4,str.indexOf("已经")-1);
		String strS = "5272724330006"+"|"+ywlx+"|"+slbh;
		ywlx=null;
		slbh=null;
		return strS;
	}
	

	
}

