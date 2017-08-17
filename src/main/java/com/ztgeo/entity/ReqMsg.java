package com.ztgeo.entity;

public class ReqMsg {
	private String ecName ;//集团客户名称
	private String apId;//用户名
	private String secretKey;//密码
	private String mobiles;//手机号
	private String content;//短信内容
	private String sign;//网签编码
	private String addSerial;//扩展码
	private String mac;//api签名结果 md5加密
	
	public ReqMsg() {
		// TODO Auto-generated constructor stub
	}

	public ReqMsg(String ecName, String apId, String secretKey, String mobiles, String content, String sign,
			String addSerial, String mac) {
		super();
		this.ecName = ecName;
		this.apId = apId;
		this.secretKey = secretKey;
		this.mobiles = mobiles;
		this.content = content;
		this.sign = sign;
		this.addSerial = addSerial;
		this.mac = mac;
	}

	public String getEcName() {
		return ecName;
	}

	public void setEcName(String ecName) {
		this.ecName = ecName;
	}

	public String getApId() {
		return apId;
	}

	public void setApId(String apId) {
		this.apId = apId;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getMobiles() {
		return mobiles;
	}

	public void setMobiles(String mobiles) {
		this.mobiles = mobiles;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getAddSerial() {
		return addSerial;
	}

	public void setAddSerial(String addSerial) {
		this.addSerial = addSerial;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	@Override
	public String toString() {
		return "ReqMsg [ecName=" + ecName + ", apId=" + apId + ", secretKey=" + secretKey + ", mobiles=" + mobiles
				+ ", content=" + content + ", sign=" + sign + ", addSerial=" + addSerial + ", mac=" + mac + "]";
	}
	
	
}
