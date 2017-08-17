package com.ztgeo.entity;

public class RespMsg {
	private boolean isOk ;//返回状态值是否正常
	private String rspcod;//返回的具体情况
	private String msgGroup;//返回的消息批次号
	private String id;//原消息的定位
	public RespMsg() {
	}
	
	
	
	public RespMsg(boolean isOk, String rspcod, String msgGroup, String id) {
		super();
		this.isOk = isOk;
		this.rspcod = rspcod;
		this.msgGroup = msgGroup;
		this.id = id;
	}



	public String getMsgGroup() {
		return msgGroup;
	}



	public void setMsgGroup(String msgGroup) {
		this.msgGroup = msgGroup;
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public boolean isOk() {
		return isOk;
	}
	public void setOk(boolean isOk) {
		this.isOk = isOk;
	}
	public String getRspcod() {
		return rspcod;
	}
	public void setRspcod(String rspcod) {
		this.rspcod = rspcod;
	}
}
