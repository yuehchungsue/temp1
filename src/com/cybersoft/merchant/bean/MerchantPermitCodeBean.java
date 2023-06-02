/************************************************************
 * <p>#File Name:   MerchantPermitCodeBean.java        </p>
 * <p>#Description:                  </p>
 * <p>#Create Date: 2020/4/6              </p>
 * <p>#Company:     cybersoft               </p>
 * <p>#Notice:                      </p>
 * @author      dan
 * @since       SPEC version
 * @version 0.1 2020/4/6  dan
 * 2020/06/17 �ۥ� 202006110363-00 �S���޲z�����s�WPermitCode�]�w
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
	 * ���ӵ���Ʀ��OpermitCode�S�����
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

		// update ������ä[����
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
	 * �쥻���s�b��merchantID
	 * 
	 * @param MerchantId
	 *            merchantId
	 * @param PermitCodeNew
	 *            �s��permitCode
	 * @param UserId
	 *            �ϥΪ�id
	 * @return
	 */
	public boolean insertNoExistsMerchantId(String MerchantId, String PermitCodeNew, String UserId) {
		boolean Result = false;

		DBTransationSQLList trnSQLList = new DBTransationSQLList();
		ArrayList<SQLParam> sqlList;
		StringBuffer sqlInsert;

		// �[�J�s��PermitCode
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
	 * ���o�S�w�S����permitcode
	 * 
	 * @version 1.0 2020/03/31
	 * @param merchantId
	 *            �ӯS����ID
	 * @return Hashtable �ӯS���]�w��permitCode���
	 */
	public ArrayList<Hashtable<String, String>> getEnablePermitCodeList(String merchantId) {
		
		StringBuffer sql = new StringBuffer();
		ArrayList<Hashtable<String, String>> arrayPermitData = null; // �S��permitCode

		sql.append("SELECT * FROM MERCHANT_PERMIT_LIST WHERE MERCHANTID=? ");
		sql.append(" AND EXPIRE_DATE >= ? AND ENABLE_FLAG='Y' ");
		sql.append(" ORDER BY EXPIRE_DATE ");
		DataBaseBean2 DBBean = new DataBaseBean2();
		DBBean.ClearSQLParam();

		DBBean.AddSQLParam(emDataType.STR, merchantId);
		DBBean.AddSQLParam(emDataType.STR, calculateDate(0)); // ���Ĵ��j�󤵤�~���
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
	 * ���o�̷s��PermitCode(���Ĵ��̤j��)
	 * @param merchantId
	 * @return
	 */
	public ArrayList<Hashtable<String, String>> getMaxExpiredPermitCodeList(String merchantId) {
		
		StringBuffer sql = new StringBuffer();
		ArrayList<Hashtable<String, String>> arrayPermitData = null; // �S��permitCode

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
	 * �쥻�s�bpermitCode �ç@�s�W
	 * 
	 * @param MerchantId
	 *            �S��ID
	 * @param PermitCodeNew
	 *            �s��permitCode
	 * @param UserId
	 *            ��s���H
	 * @return
	 */
	public boolean insertNewPermitCode(String MerchantId, String PermitCodeNew, String UserId) {

		boolean Result = false;
		// 3.1 �R���Ҧ����S��PermitCode record
		// 3.2 Insert into PermitCodeNew ����鬰20991231
		// 3.3 Insert into PermitCode(�쥻��)����鬰 ���(�����check PermitCode�ɷ|�[15�Ѷi�榳�L�Ī��P�_)
		/**** commit & rollback ****/
		DBTransationSQLList trnSQLList = new DBTransationSQLList();
		ArrayList<SQLParam> sqlList;
		// StringBuffer sqlDelete;
		StringBuffer sqlUpdate;
		StringBuffer sqlInsert;

		// update ������ä[����
		sqlList = new ArrayList<>();
		sqlUpdate = new StringBuffer();
		sqlUpdate.append("UPDATE MERCHANT_PERMIT_LIST ");
		sqlUpdate.append(" SET EXPIRE_DATE=? WHERE MERCHANTID=? AND EXPIRE_DATE=?");

		sqlList.add(new SQLParam(emDataType.STR, calculateDate(14))); // �ª�����14��
		sqlList.add(new SQLParam(emDataType.STR, MerchantId));
		sqlList.add(new SQLParam(emDataType.STR, "20991231"));

		trnSQLList.AddTrnSQLAndParam(sqlUpdate.toString(), sqlList);

		// �[�J�s��PermitCode
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
		
		// �]�w�L��PermitCode��ENABLE_FLAG
		sqlList = new ArrayList<>();
		sqlUpdate = new StringBuffer();
		sqlUpdate.append("UPDATE MERCHANT_PERMIT_LIST ");
		sqlUpdate.append(" SET ENABLE_FLAG=? WHERE MERCHANTID=? AND EXPIRE_DATE < ?");
		
		sqlList.add(new SQLParam(emDataType.STR, "E")); // ���Ī�
		sqlList.add(new SQLParam(emDataType.STR, MerchantId));
		sqlList.add(new SQLParam(emDataType.STR, calculateDate(0))); // ���
		
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
	 * ��o�L�h�Ѽƪ�����Υ��ӤѼƪ����
	 * @param parsent ����0��ܷ�ѡA�p��0��ܹL�h�ѼơA�j��0��ܥ��ӤѼ�
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
