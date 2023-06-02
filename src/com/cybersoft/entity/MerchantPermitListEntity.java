/************************************************************
 * <p>#File Name:   MerchantPermitListEntity.java        </p>
 * <p>#Description:  table(MERCHANT_PERMIT_LIST)對應的類別          </p>
 * <p>#Create Date: 2020/4/6              </p>
 * <p>#Company:     cybersoft               </p>
 * <p>#Notice:                      </p>
 * @author      dan
 * @since       SPEC version
 * @version 0.1 2020/4/6  dan
 ************************************************************/
package com.cybersoft.entity;

import java.sql.Timestamp;

/**
 * for MERCHANT_PERMIT_LIST table用之entity
 * @author dan
 *
 */
public class MerchantPermitListEntity {

	private String MERCHANTID;
	private String PERMIT_CODE;
	private String EXPIRE_DATE;
	private String ENABLE_FLAG;
	private Timestamp MODIFY_DATE;
	private String UPDATE_USER;
	
	public String getMERCHANTID() {
		return MERCHANTID;
	}
	public void setMERCHANTID(String mERCHANTID) {
		MERCHANTID = mERCHANTID;
	}
	public String getPERMIT_CODE() {
		return PERMIT_CODE;
	}
	public void setPERMIT_CODE(String pERMIT_CODE) {
		PERMIT_CODE = pERMIT_CODE;
	}
	public String getEXPIRE_DATE() {
		return EXPIRE_DATE;
	}
	public void setEXPIRE_DATE(String eXPIRE_DATE) {
		EXPIRE_DATE = eXPIRE_DATE;
	}
	public String getENABLE_FLAG() {
		return ENABLE_FLAG;
	}
	public void setENABLE_FLAG(String eNABLE_FLAG) {
		ENABLE_FLAG = eNABLE_FLAG;
	}
	public Timestamp getMODIFY_DATE() {
		return MODIFY_DATE;
	}
	public void setMODIFY_DATE(Timestamp mODIFY_DATE) {
		MODIFY_DATE = mODIFY_DATE;
	}
	public String getUPDATE_USER() {
		return UPDATE_USER;
	}
	public void setUPDATE_USER(String uPDATE_USER) {
		UPDATE_USER = uPDATE_USER;
	}
	
	
}
