/************************************************************
 * <p>#File Name:   MerchantPermitCodeBean.java        </p>
 * <p>#Description:                  </p>
 * <p>#Create Date: 2020/4/6              </p>
 * <p>#Company:     cybersoft               </p>
 * <p>#Notice:                      </p>
 * @author      dan
 * @since       SPEC version
 * @version 0.1 2020/4/6  dan
 * 2020/06/17 彥仲 202006110363-00 特店管理介面新增PermitCode設定
 ************************************************************/
package com.cybersoft.merchant.bean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.cybersoft.bean.DBTransationSQLList;
import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft.bean.LogUtils;
import com.cybersoft.bean.SQLParam;
import com.cybersoft.bean.emDataType;
import com.cybersoft.entity.MerchantPermitListEntity;

public class MerchantPermitCodeBean {
	public static final LogUtils log_systeminfo = new LogUtils("systeminfo");

	public MerchantPermitCodeBean() {
	}

	/**
	 * 有該筆資料但是permitCode沒有資料
	 * @param MerchantId
	 * @param PermitCodeNew
	 * @param UserId
	 * @return
	 */
	public boolean updateExistsMerchantId(String MerchantId, String PermitCodeNew, String UserId) {
		boolean Result = false;

		DBTransationSQLList trnSQLList = new DBTransationSQLList();
		ArrayList<SQLParam> sqlList;
		StringBuffer sqlUpdate;

		// update 原先的永久有效
		sqlList = new ArrayList<>();
		sqlUpdate = new StringBuffer();
		sqlUpdate.append("UPDATE MERCHANT_PERMIT_LIST ");
		sqlUpdate.append(" SET EXPIRE_DATE=?, PERMIT_CODE=? WHERE MERCHANTID=?");

		sqlList.add(new SQLParam(emDataType.STR, "20991231"));
		sqlList.add(new SQLParam(emDataType.STR, PermitCodeNew));
		sqlList.add(new SQLParam(emDataType.STR, MerchantId));

		trnSQLList.AddTrnSQLAndParam(sqlUpdate.toString(), sqlList);

		DataBaseBean2 DBBean = new DataBaseBean2();
		if (DBBean.executeTransationSQL(trnSQLList) == true) {
			log_systeminfo.debug("MerchantPermitCodeBean.insertNewPermitCode() commit OK");
			Result = true;
		} else {
			log_systeminfo.error("MerchantPermitCodeBean.insertNewPermitCode() commit Fall and RollBack");
		}

		return Result;
	}

	/**
	 * 原本不存在的merchantID
	 * 
	 * @param MerchantId
	 *            merchantId
	 * @param PermitCodeNew
	 *            新的permitCode
	 * @param UserId
	 *            使用者id
	 * @return
	 */
	public boolean insertNoExistsMerchantId(String MerchantId, String PermitCodeNew, String UserId) {
		boolean Result = false;

		DBTransationSQLList trnSQLList = new DBTransationSQLList();
		ArrayList<SQLParam> sqlList;
		StringBuffer sqlInsert;

		// 加入新的PermitCode
		sqlList = new ArrayList<>();
		sqlInsert = new StringBuffer();
		sqlInsert.append("INSERT INTO MERCHANT_PERMIT_LIST ");
		sqlInsert.append(" (MERCHANTID, PERMIT_CODE, EXPIRE_DATE, ENABLE_FLAG, MODIFY_DATE, UPDATE_USER) ");
		sqlInsert.append(" values ");
		sqlInsert.append(" (?, ?, ?, ?, sysdate, ?) ");

		sqlList.add(new SQLParam(emDataType.STR, MerchantId));
		sqlList.add(new SQLParam(emDataType.STR, PermitCodeNew));
		sqlList.add(new SQLParam(emDataType.STR, "20991231"));
		sqlList.add(new SQLParam(emDataType.STR, "Y"));
		sqlList.add(new SQLParam(emDataType.STR, UserId));

		trnSQLList.AddTrnSQLAndParam(sqlInsert.toString(), sqlList);

		DataBaseBean2 DBBean = new DataBaseBean2();
		if (DBBean.executeTransationSQL(trnSQLList) == true) {
			log_systeminfo.debug("MerchantPermitCodeBean.insertNewPermitCode() commit OK");
			Result = true;
		} else {
			log_systeminfo.error("MerchantPermitCodeBean.insertNewPermitCode() commit Fall and RollBack");
		}

		return Result;
	}

	/**
	 * 取得特定特店之permitcode
	 * 
	 * @version 1.0 2020/03/31
	 * @param merchantId
	 *            該特店之ID
	 * @return Hashtable 該特店設定之permitCode資料
	 */
	public ArrayList<Hashtable<String, String>> getEnablePermitCodeList(String merchantId) {
		
		StringBuffer sql = new StringBuffer();
		ArrayList<Hashtable<String, String>> arrayPermitData = null; // 特店permitCode

		sql.append("SELECT * FROM MERCHANT_PERMIT_LIST WHERE MERCHANTID=? ");
		sql.append(" AND EXPIRE_DATE >= ? AND ENABLE_FLAG='Y' ");
		sql.append(" ORDER BY EXPIRE_DATE ");
		DataBaseBean2 DBBean = new DataBaseBean2();
		DBBean.ClearSQLParam();

		DBBean.AddSQLParam(emDataType.STR, merchantId);
		DBBean.AddSQLParam(emDataType.STR, calculateDate(0)); // 有效期大於今日才顯示
		try {
			arrayPermitData = DBBean.QuerySQLByParam(sql.toString());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			log_systeminfo.error(e.toString());
		}
		DBBean = null;
		return arrayPermitData;

	}
	
	/**
	 * 取得最新的PermitCode(有效期最大的)
	 * @param merchantId
	 * @return
	 */
	public ArrayList<Hashtable<String, String>> getMaxExpiredPermitCodeList(String merchantId) {
		
		StringBuffer sql = new StringBuffer();
		ArrayList<Hashtable<String, String>> arrayPermitData = null; // 特店permitCode

		sql.append("SELECT * FROM MERCHANT_PERMIT_LIST WHERE MERCHANTID=? ");
		sql.append(" AND EXPIRE_DATE = ? AND ENABLE_FLAG='Y' ");
		sql.append(" ORDER BY EXPIRE_DATE ");
		DataBaseBean2 DBBean = new DataBaseBean2();
		DBBean.ClearSQLParam();

		DBBean.AddSQLParam(emDataType.STR, merchantId);
		DBBean.AddSQLParam(emDataType.STR, "20991231");
		try {
			arrayPermitData = DBBean.QuerySQLByParam(sql.toString());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			log_systeminfo.error(e.toString());
		}
		DBBean = null;
		return arrayPermitData;
	}

	/**
	 * 原本存在permitCode 並作新增
	 * 
	 * @param MerchantId
	 *            特店ID
	 * @param PermitCodeNew
	 *            新的permitCode
	 * @param UserId
	 *            更新的人
	 * @return
	 */
	public boolean insertNewPermitCode(String MerchantId, String PermitCodeNew, String UserId) {

		boolean Result = false;
		// 3.1 刪除所有此特店PermitCode record
		// 3.2 Insert into PermitCodeNew 到期日為20991231
		// 3.3 Insert into PermitCode(原本的)到期日為 當日(交易類check PermitCode時會加15天進行有無效的判斷)
		/**** commit & rollback ****/
		DBTransationSQLList trnSQLList = new DBTransationSQLList();
		ArrayList<SQLParam> sqlList;
		// StringBuffer sqlDelete;
		StringBuffer sqlUpdate;
		StringBuffer sqlInsert;

		// update 原先的永久有效
		sqlList = new ArrayList<>();
		sqlUpdate = new StringBuffer();
		sqlUpdate.append("UPDATE MERCHANT_PERMIT_LIST ");
		sqlUpdate.append(" SET EXPIRE_DATE=? WHERE MERCHANTID=? AND EXPIRE_DATE=?");

		sqlList.add(new SQLParam(emDataType.STR, calculateDate(14))); // 舊的延期14天
		sqlList.add(new SQLParam(emDataType.STR, MerchantId));
		sqlList.add(new SQLParam(emDataType.STR, "20991231"));

		trnSQLList.AddTrnSQLAndParam(sqlUpdate.toString(), sqlList);

		// 加入新的PermitCode
		sqlList = new ArrayList<>();
		sqlInsert = new StringBuffer();
		sqlInsert.append("INSERT INTO MERCHANT_PERMIT_LIST ");
		sqlInsert.append(" (MERCHANTID, PERMIT_CODE, EXPIRE_DATE, ENABLE_FLAG, MODIFY_DATE, UPDATE_USER) ");
		sqlInsert.append(" values ");
		sqlInsert.append(" (?, ?, ?, ?, sysdate, ?) ");

		sqlList.add(new SQLParam(emDataType.STR, MerchantId));
		sqlList.add(new SQLParam(emDataType.STR, PermitCodeNew));
		sqlList.add(new SQLParam(emDataType.STR, "20991231"));
		sqlList.add(new SQLParam(emDataType.STR, "Y"));
		sqlList.add(new SQLParam(emDataType.STR, UserId));

		trnSQLList.AddTrnSQLAndParam(sqlInsert.toString(), sqlList);
		
		// 設定過期PermitCode的ENABLE_FLAG
		sqlList = new ArrayList<>();
		sqlUpdate = new StringBuffer();
		sqlUpdate.append("UPDATE MERCHANT_PERMIT_LIST ");
		sqlUpdate.append(" SET ENABLE_FLAG=? WHERE MERCHANTID=? AND EXPIRE_DATE < ?");
		
		sqlList.add(new SQLParam(emDataType.STR, "E")); // 失效的
		sqlList.add(new SQLParam(emDataType.STR, MerchantId));
		sqlList.add(new SQLParam(emDataType.STR, calculateDate(0))); // 當天
		
		trnSQLList.AddTrnSQLAndParam(sqlUpdate.toString(), sqlList);

		DataBaseBean2 DBBean = new DataBaseBean2();
		if (DBBean.executeTransationSQL(trnSQLList) == true) {
			log_systeminfo.debug("MerchantPermitCodeBean.insertNewPermitCode() commit OK");
			Result = true;
		} else {
			log_systeminfo.error("MerchantPermitCodeBean.insertNewPermitCode() commit Fall and RollBack");
		}

		return Result;
	}
	
	/**
	 * 獲得過去天數的日期或未來天數的日期
	 * @param parsent 等於0表示當天，小於0表示過去天數，大於0表示未來天數
	 * @return
	 */
	private String calculateDate(int parsent) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + parsent);
		Date parsentDay = calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String result = format.format(parsentDay);
	   
		return result;
	}
}
