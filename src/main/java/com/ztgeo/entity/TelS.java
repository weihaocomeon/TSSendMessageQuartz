package com.ztgeo.entity;

public class TelS {
	private String tel;//电话号码
	private String id;//主键id
	private String content;//短信内容
	
	public TelS() {
		// TODO Auto-generated constructor stub
	}

	public TelS(String tel, String id, String content) {
		super();
		this.tel = tel;
		this.id = id;
		this.content = content;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "TelS [tel=" + tel + ", id=" + id + ", content=" + content + "]";
	}
	
	
	
}
