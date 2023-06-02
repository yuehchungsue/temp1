/**異動說明
 * 202208190762-00 20220902 Jeffery Cheng 新增
 */
package com.fubon.tp.model.pojo;

import java.io.Serializable;

public class TMerchants implements Serializable{
	
	private String merchantId="";
	private String callName="";
	private String hqMid="";

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getCallName() {
		return callName;
	}

	public void setCallName(String callName) {
		this.callName = callName;
	}

	public void setHQ_MID(String hqMid) {
		this.hqMid = hqMid;
	}
	public String getHQ_MID() {
		return hqMid;
	}

	@Override
	public String toString() {
		return "TMerchants [merchantId=" + merchantId + ", callName=" + callName + "]";
	}
	
	

}
