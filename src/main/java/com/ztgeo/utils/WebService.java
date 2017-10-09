package com.ztgeo.utils;

import java.rmi.RemoteException;

import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.csapi.www.schema.common.v2_0.PolicyException;
import org.csapi.www.schema.common.v2_0.ServiceException;
import org.csapi.www.schema.mms.SendMessageRequest;
import org.csapi.www.schema.sms.DeliveryInformation;
import org.csapi.www.schema.sms.GetSmsDeliveryStatusRequest;
import org.csapi.www.schema.sms.MessageFormat;
import org.csapi.www.schema.sms.SendMethodType;
import org.csapi.www.schema.sms.SendSmsRequest;
import org.csapi.www.schema.sms.SendSmsResponse;
import org.csapi.www.service.Cmcc_mas_wbsProxy;
import org.csapi.www.service.Cmcc_mas_wbs_PortType;
import org.csapi.www.service.Cmcc_mas_wbs_Service;
import org.csapi.www.service.Cmcc_mas_wbs_ServiceLocator;
import org.junit.Test;

import com.ztgeo.entity.TelS;
import com.ztgeo.staticParams.StaticParams;

public class WebService {
	//发送短信的方法
	public static String sentSMS(TelS tel){
	SendSmsResponse resp;
	String result =null;
	Cmcc_mas_wbs_PortType proxy = new Cmcc_mas_wbsProxy();
	URI destinationAddresses[]=new URI[1];
	try {
		destinationAddresses[0] = new URI("tel:"+tel.getTel());
	} catch (MalformedURIException e1) {
		System.out.println("转换为地址时报错!");
		ErrorLog.log.error("转换为地址时报错!",e1);
		e1.printStackTrace();
	}
	try {
	    resp =	proxy.sendSms(new SendSmsRequest(StaticParams.APPLICATIONID, destinationAddresses, "", tel.getContent(),
		MessageFormat.fromString(MessageFormat._GB2312), SendMethodType.fromString(SendMethodType._Normal), true));
		result =resp.getRequestIdentifier();
		System.out.println("--请求接口成功:"+result);
		InfoLog.log.info("--请求接口成功:"+result);
	} catch (ServiceException e) {
		System.out.println("服务端故障");
		ErrorLog.log.error("服务端故障",e);
		e.printStackTrace();
	} catch (PolicyException e) {
		System.out.println("服务端故障");
		ErrorLog.log.error("服务端故障",e);
		e.printStackTrace();
	} catch (RemoteException e) {
		System.out.println("服务端故障");
		ErrorLog.log.error("服务端故障",e);
		e.printStackTrace();
	}finally {
		proxy= null;
		resp=null;
	}
	return result;
	}
	
	public static DeliveryInformation[] getResult(String identifier){
		Cmcc_mas_wbs_PortType proxy = new Cmcc_mas_wbsProxy();
		DeliveryInformation[] aas = null;
		try {
			 aas =proxy.getSmsDeliveryStatus(new GetSmsDeliveryStatusRequest(StaticParams.APPLICATIONID, identifier));
			
		} catch (RemoteException e) {
			System.out.println("服务端故障");
			ErrorLog.log.error("服务端故障",e);
			e.printStackTrace();
		}
		proxy=null;
		return aas;
	}
	
	@Test
	public void  getResult(){
		String identifier="67b9243d-86e7-4073-8faa-c82e2e67c84b";
		Cmcc_mas_wbs_PortType proxy = new Cmcc_mas_wbsProxy();
		DeliveryInformation[] aas = null;
		try {
			 aas =proxy.getSmsDeliveryStatus(new GetSmsDeliveryStatusRequest("P000000000000005", identifier));
			if(aas.length>0){
			 for (DeliveryInformation aa : aas) {
					System.out.println((aa.getDeliveryStatus().getValue()));
				}
			}else{
				System.out.println("解析不到数据!!");
			}
		} catch (RemoteException e) {
			System.out.println("服务端故障");
			ErrorLog.log.error("服务端故障",e);
			e.printStackTrace();
		}
		
	}
	
}
