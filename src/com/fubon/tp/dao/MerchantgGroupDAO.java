/**異動說明
 * 202208190762-00 20220902 Jeffery Cheng 新增
 */
package com.fubon.tp.dao;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft.bean.emDB;
import com.cybersoft.bean.emDataType;
import com.fubon.tp.model.pojo.MerchantgGroup;

public class MerchantgGroupDAO {
	
	public List<MerchantgGroup> getAllMerchantStoreByHqMid(String hqMid) {
		String mid=hqMid;
		MerchantgGroup mg=getMerchantgGroupBeanByMid(mid);
		if(mg.getHqMid()!=null && mg.getHqMid()!="" && mg.getHqMid()!=" ") {
			hqMid=mg.getHqMid();
		}
		
		List<MerchantgGroup> list = new ArrayList<MerchantgGroup>();
		StringBuffer sSQLSB = new StringBuffer();
		DataBaseBean2 DBBean = new DataBaseBean2();
		DBBean.ClearSQLParam();
		sSQLSB.append("SELECT * FROM ERM.T_ERM_MERCHANT WHERE MERCHANTID=? OR HQ_MID=?");
		DBBean.AddSQLParam(emDataType.STR, hqMid);
		DBBean.AddSQLParam(emDataType.STR, hqMid);
		List<Map<String, Object>> objlist = DBBean.QuerySQLByParam2(sSQLSB.toString());
		
		for(int i = 0; i < objlist.size(); i++){
			Map<String, Object> map = objlist.get(i);
			MerchantgGroup pojo = new MerchantgGroup();
			pojo.setMid((String)map.get("MERCHANTID"));
			pojo.setCallName((String)map.get("MERCHANTCALLNAME"));
			pojo.setHqMid((String)map.get("HQ_MID"));
			list.add(pojo);
		}
		
		DBBean = null;
		System.out.println("ERM.T_ERM_MERCHANT search count=" + list.size());
		
		return list;
	}
	private MerchantgGroup getMerchantgGroupBeanByMid(String mid) {
		StringBuffer sSQLSB = new StringBuffer();
		DataBaseBean2 DBBean = new DataBaseBean2();
		DBBean.ClearSQLParam();
		sSQLSB.append("SELECT * FROM ERM.T_ERM_MERCHANT WHERE MERCHANTID=?");
		DBBean.AddSQLParam(emDataType.STR, mid);
		List<Map<String, Object>> objlist = DBBean.QuerySQLByParam2(sSQLSB.toString());
		Map<String, Object> map = objlist.get(0);
		MerchantgGroup pojo = new MerchantgGroup();
		pojo.setMid((String)map.get("MERCHANTID"));
		pojo.setCallName((String)map.get("MERCHANTCALLNAME"));
		pojo.setHqMid((String)map.get("HQ_MID"));
		return pojo;
	}
	public String getMerchantLogoFile(String sHQ_MID) {
		ArrayList<Hashtable<String, String>> listResult = null;
		String imageFile = "";
		StringBuffer sSQLSB = new StringBuffer();
		DataBaseBean2 DBBean = new DataBaseBean2();
		DBBean.ClearSQLParam();
		sSQLSB.append("SELECT IMAGEFILE FROM ERM.T_ERM_MERCHANTLOGO WHERE HQ_MID=?");
		DBBean.AddSQLParam(emDataType.STR, sHQ_MID);
		listResult = DBBean.QuerySQLByParam(sSQLSB.toString());
		DBBean = null;
		if(listResult != null) {
			imageFile = objToStrTrim(listResult.get(0).get("IMAGEFILE"));
		}
		return imageFile;
	}
	private String objToStrTrim(String obj) {
		if(obj == null) return "";
		return obj.toString().trim();
	}
}
