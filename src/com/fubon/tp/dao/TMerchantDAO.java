/**異動說明
 * 202208190762-00 20220902 Jeffery Cheng 新增
 */
package com.fubon.tp.dao;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft.bean.emDB;
import com.cybersoft.bean.emDataType;
import com.fubon.tp.model.pojo.TErmLog;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.jdbc.core.support.JdbcDaoSupport;
//import org.springframework.stereotype.Repository;

import com.fubon.tp.model.pojo.TMerchants;
import com.fubon.tp.secure.TPSecureUtilities;

//@Repository
public class TMerchantDAO /*extends JdbcDaoSupport*/ {
	
//	@Autowired
	public TMerchantDAO() {
	}
	
	public TMerchants queryByMerchantId2(String merchantId) {
		merchantId =TPSecureUtilities.stripSQLInjection(merchantId);
		StringBuffer sSQLSB = new StringBuffer();
		DataBaseBean2 DBBean = new DataBaseBean2();
		DBBean.ClearSQLParam();
		//假如單一特店,HQ_MID是NULL，則將HQ_MID 設定為MERCHANTID
		sSQLSB.append("SELECT MERCHANTID,MERCHANTCALLNAME,NVL(HQ_MID,MERCHANTID) AS HQ_MID,CURRENTCODE,PERMIT_INQUIRY_TX FROM ERM.T_ERM_MERCHANT WHERE MERCHANTID=?");
		DBBean.AddSQLParam(emDataType.STR, merchantId);
		List<Map<String, Object>> objlist = DBBean.QuerySQLByParam2(sSQLSB.toString());
		Map<String, Object> map=objlist.get(0);
		DBBean = null;
		System.out.println("ERM.T_ERM_MERCHANT search count=" + map.size());
		TMerchants pojo = new TMerchants();
		pojo = tMerchantsObj2Pojo(pojo, map);
		
		return pojo;
	}
	
	private TMerchants tMerchantsObj2Pojo(TMerchants pojo, Map<String, Object> map){
		pojo.setMerchantId((String)map.get("MERCHANTID"));
		pojo.setCallName((String)map.get("MERCHANTCALLNAME"));
		//假如單一特店,HQ_MID是NULL，則將HQ_MID 設定為MERCHANTID
		String sHQ_MID =(map.get("HQ_MID") != null)?(String)map.get("HQ_MID"):(String)map.get("MERCHANTID");
		pojo.setHQ_MID(sHQ_MID);
		return pojo;
	}
	
	
}
