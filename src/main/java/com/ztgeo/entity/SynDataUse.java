package com.ztgeo.entity;

public class SynDataUse {
	private String mysqlid;//状态码
	private int syncount;//同步次数  
	
	public SynDataUse() {
		// TODO 自动生成的构造函数存根
	}

	public SynDataUse(String mysqlid, int syncount) {
		super();
		this.mysqlid = mysqlid;
		this.syncount = syncount;
	}

	/**
	 * @return mysqlid
	 */
	public String getMysqlid() {
		return mysqlid;
	}

	/**
	 * @param mysqlid 要设置的 mysqlid
	 */
	public void setMysqlid(String mysqlid) {
		this.mysqlid = mysqlid;
	}

	/**
	 * @return syncount
	 */
	public int getSyncount() {
		return syncount;
	}

	/**
	 * @param syncount 要设置的 syncount
	 */
	public void setSyncount(int syncount) {
		this.syncount = syncount;
	}

	/* （非 Javadoc）
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SynDataUse [mysqlid=" + mysqlid + ", syncount=" + syncount + "]";
	}
	
}
