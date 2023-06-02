/************************************************************
 * <p>#File Name:	MerchantLoginBean.java	</p>
 * <p>#Description:	            			</p>
 * <p>#Create Date:	2007/09/26		        </p>
 * <p>#Company:  	cybersoft	        	</p>
 * <p>#Notice:   					</p>
 * @author		Shirley Lin
 * @since		SPEC version
 * @version	0.1	2007/09/26	Shirley Lin
 * 異動說明
 * 20200306 202002100619-00 saint EC退貨檢核,因設定都是以母特店，get_Merchant() 以抓取母特店資料為主
 * 202208090854-01 20220801 HKP PCI-DSS更換密碼雜錯MD5為SHA256，MD5雜錯長度為32，SHA256雜錯長度為64，以此長度判斷是否為MD5，變更後的密碼一律使用SHA256與LOG遮蔽
 ************************************************************/

package com.cybersoft.merchant.bean;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft.bean.UserBean;
import com.cybersoft.bean.emDataType;
import com.cybersoft.common.Util;
import com.cybersoft.merchant.ctl.MerchantChangePwdCtl;

import java.util.Hashtable;
import java.security.MessageDigest;
import com.cybersoft.bean.LogUtils;

public class MerchantLoginBean 
{
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    
    public MerchantLoginBean() 
    {
    }

//    /**
//     * 取得特店使用者
//     * @param String MerchantID  特店代號
//     * @param String UserID      使用者代號
//     * @param String Sign        使用者條件 = 或 <>
//     * @param String Value       使用者狀態值 N:密碼鎖定 Y:正常 D:刪除 O:待啟用 R:重置密碼
//     * @param String PwssChangDay 密碼有效天數
//     * @return Hashtable         特店使用者資料
//     */
//    public Hashtable get_Merchant_User(String MerchantID, String UserID, String Sign, String Value, String PwssChangDay) 
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        Hashtable hashData = new Hashtable();
////      String Sql = "Select MERCHANT_ID,USER_ID,USER_NAME,USER_PWD,USER_STATUS,USER_PWD_ERRCNT,TO_CHAR(USER_INSERT_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_INSERT_DATE ,TO_CHAR(USER_UPDATE_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_UPDATE_DATE ,TO_CHAR(USER_LOCK_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_LOCK_DATE ,TO_CHAR(USER_CHANGERPWD_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_CHANGEPWD_DATE , (SYSDATE - USER_INSERT_DATE) AS CHANGEPWD_DAY From MERCHANT_USER WHERE MERCHANT_ID = '"+ MerchantID+"' AND USER_ID='"+UserID+"' ";
//        String Sql = "Select MERCHANT_ID,USER_ID,USER_NAME,USER_PWD,USER_STATUS,USER_PWD_ERRCNT,TO_CHAR(USER_INSERT_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_INSERT_DATE ,TO_CHAR(USER_UPDATE_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_UPDATE_DATE ,TO_CHAR(USER_LOCK_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_LOCK_DATE ,TO_CHAR(USER_CHANGERPWD_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_CHANGEPWD_DATE , To_Date(to_Char(SYSDATE,'yyyy/mm/dd'),'yyyy/mm/dd') - To_Date(to_char(USER_CHANGERPWD_DATE,'yyyy/mm/dd'),'yyyy/mm/dd') AS CHANGEPWD_DAY, ENF_UPDPWD, DEL_FLAG, TO_CHAR(USER_CHANGERPWD_DATE+"+PwssChangDay+",'yyyy/mm/dd') AS PWDDUE_DATE, TO_CHAR(EXPIRE_DATE, 'yyyymmdd') AS EXPIRE_DATE From MERCHANT_USER WHERE MERCHANT_ID = '" +
//                     MerchantID + "' AND USER_ID='" + UserID + "' ";
//
//        if (Sign.length() > 0 && Value.length() > 0) 
//        {
//            Sql = Sql + " AND USER_STATUS " + Sign + "'" + Value + "' ";
//        }
//        
//        // System.out.println("Sql=" + Sql);
//        ArrayList arrayData = new ArrayList();
//        try 
//        {
//            arrayData = (ArrayList) SysBean.executeSQL(Sql, "select");
//        } 
//        catch (Exception ex) 
//        {
//            //System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        
//        if (arrayData == null)
//            arrayData = new ArrayList();
//            
//        if (arrayData.size() > 0) 
//        {
//            hashData = (Hashtable) arrayData.get(0);
//            if (hashData == null)
//                hashData = new Hashtable();
//                
//        }
//        
//        return hashData;
//    }

    /* Override get_Merchant_User with DataBaseBean parameter */
    public Hashtable get_Merchant_User(DataBaseBean2 SysBean, String MerchantID, String UserID, String Sign, String Value, String PwssChangDay) 
    {
    	//假如SIGN_BILL='Y' OR 'B':實體電子簽名特店,MERCHANTCALLNAME 則到ERM.T_ERM_MERCHANT抓取
        Hashtable hashData = new Hashtable();
//      String Sql = "Select MERCHANT_ID,USER_ID,USER_NAME,USER_PWD,USER_STATUS,USER_PWD_ERRCNT,TO_CHAR(USER_INSERT_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_INSERT_DATE ,TO_CHAR(USER_UPDATE_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_UPDATE_DATE ,TO_CHAR(USER_LOCK_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_LOCK_DATE ,TO_CHAR(USER_CHANGERPWD_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_CHANGEPWD_DATE , (SYSDATE - USER_INSERT_DATE) AS CHANGEPWD_DAY From MERCHANT_USER WHERE MERCHANT_ID = '"+ MerchantID+"' AND USER_ID='"+UserID+"' ";
        StringBuffer sSQLSB = new StringBuffer();
        SysBean.ClearSQLParam();
        /** String Sql = "Select MERCHANT_ID,USER_ID,USER_NAME,USER_PWD,USER_STATUS,USER_PWD_ERRCNT,TO_CHAR(USER_INSERT_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_INSERT_DATE ,TO_CHAR(USER_UPDATE_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_UPDATE_DATE ,TO_CHAR(USER_LOCK_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_LOCK_DATE ,TO_CHAR(USER_CHANGERPWD_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_CHANGEPWD_DATE , To_Date(to_Char(SYSDATE,'yyyy/mm/dd'),'yyyy/mm/dd') - To_Date(to_char(USER_CHANGERPWD_DATE,'yyyy/mm/dd'),'yyyy/mm/dd') AS CHANGEPWD_DAY, ENF_UPDPWD, DEL_FLAG, TO_CHAR(USER_CHANGERPWD_DATE+"+PwssChangDay+",'yyyy/mm/dd') AS PWDDUE_DATE, TO_CHAR(EXPIRE_DATE, 'yyyymmdd') AS EXPIRE_DATE,A.SUBMID "
        		+",Case When SIGN_BILL='Y' OR SIGN_BILL='B' THEN (Select MERCHANTCALLNAME FROM ERM.T_ERM_MERCHANT WHERE MERCHANTID=A.MERCHANT_ID AND ROWNUM=1) ELSE MERCHANTCALLNAME END AS MERCHANTCALLNAME"
        		//+",NVL(MERCHANTCALLNAME,MERCHANT_ID) AS MERCHANTCALLNAME"
        		+",Case When substr(MERCHANT_ID,length(merchantId)-12) <> B.SUBMID Then 'Y' Else 'N' End ISSUBMERCHANT,SIGN_BILL "
        		+" From MERCHANT_USER A LEFT OUTER JOIN MERCHANT B ON A.SUBMID = B.SUBMID "
        		+" WHERE MERCHANT_ID = ? AND USER_ID = ? "; */
        sSQLSB.append("Select MERCHANT_ID,USER_ID,USER_NAME,USER_PWD,USER_STATUS,USER_PWD_ERRCNT,TO_CHAR(USER_INSERT_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_INSERT_DATE ,TO_CHAR(USER_UPDATE_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_UPDATE_DATE ,TO_CHAR(USER_LOCK_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_LOCK_DATE ,TO_CHAR(USER_CHANGERPWD_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_CHANGEPWD_DATE , To_Date(to_Char(SYSDATE,'yyyy/mm/dd'),'yyyy/mm/dd') - To_Date(to_char(USER_CHANGERPWD_DATE,'yyyy/mm/dd'),'yyyy/mm/dd') AS CHANGEPWD_DAY, ENF_UPDPWD, DEL_FLAG, TO_CHAR(USER_CHANGERPWD_DATE+"+PwssChangDay+",'yyyy/mm/dd') AS PWDDUE_DATE, TO_CHAR(EXPIRE_DATE, 'yyyymmdd') AS EXPIRE_DATE,A.SUBMID ");
        sSQLSB.append(",Case When SIGN_BILL='Y' OR SIGN_BILL='B' THEN (Select MERCHANTCALLNAME FROM ERM.T_ERM_MERCHANT WHERE MERCHANTID=A.MERCHANT_ID AND ROWNUM=1) ELSE MERCHANTCALLNAME END AS MERCHANTCALLNAME");
        sSQLSB.append(",Case When substr(MERCHANT_ID,length(merchantId)-12) <> B.SUBMID Then 'Y' Else 'N' End ISSUBMERCHANT,SIGN_BILL ");
        sSQLSB.append(" From MERCHANT_USER A LEFT OUTER JOIN MERCHANT B ON A.SUBMID = B.SUBMID ");
        sSQLSB.append(" WHERE MERCHANT_ID = ? AND USER_ID = ? ");
        
        SysBean.AddSQLParam(emDataType.STR, MerchantID);
        SysBean.AddSQLParam(emDataType.STR, UserID);
        
        if (Sign.length() > 0 && Value.length() > 0) 
        {
            /** Sql = Sql + " AND USER_STATUS " + Sign + "'" + Value + "' "; */
        	sSQLSB.append(" AND USER_STATUS " + Sign + " ? ");

        	SysBean.AddSQLParam(emDataType.STR, Value);
        }
        // System.out.println("Sql=" + Sql);
        ArrayList arrayData = new ArrayList();
        try 
        {
        	/** 2023/04/19 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** IT-TESTCASE-010-020-002-001, TESTCASE-002*/
            arrayData = (ArrayList) SysBean.QuerySQLByParam(sSQLSB.toString());
        } 
        catch (Exception ex) 
        {
            //System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }
        
        if (arrayData == null)
            arrayData = new ArrayList();
            
        if (arrayData.size() > 0) 
        {
            hashData = (Hashtable) arrayData.get(0);
            if (hashData == null)
                hashData = new Hashtable();
                
        }
        
        return hashData;
    }
    
//    /**
//     * 取得特店使用者
//     * @param String MerchantID  特店代號
//     * @param String UserID      使用者代號
//     * @return Hashtable         特店使用者資料
//     */
//    public Hashtable get_Merchant_User(String MerchantID, String UserID) 
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        Hashtable hashData = new Hashtable();
//        String Sql = "Select MERCHANT_ID,USER_ID,USER_NAME,USER_PWD,USER_STATUS,USER_PWD_ERRCNT,TO_CHAR(USER_INSERT_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_INSERT_DATE ,TO_CHAR(USER_UPDATE_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_UPDATE_DATE ,TO_CHAR(USER_LOCK_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_LOCK_DATE ,TO_CHAR(USER_CHANGERPWD_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_CHANGEPWD_DATE , To_Date(to_Char(SYSDATE,'yyyy/mm/dd'),'yyyy/mm/dd') - To_Date(to_char(USER_CHANGERPWD_DATE,'yyyy/mm/dd'),'yyyy/mm/dd') AS CHANGEPWD_DAY, ENF_UPDPWD, DEL_FLAG, TO_CHAR(EXPIRE_DATE, 'yyyymmdd') AS EXPIRE_DATE,SUBMID From MERCHANT_USER WHERE MERCHANT_ID = '" +
//                     MerchantID + "' AND USER_ID='" + UserID + "' ";
//                     
//        // System.out.println("Sql=" + Sql);
//        ArrayList arrayData = new ArrayList();
//        
//        try 
//        {
//            arrayData = (ArrayList) SysBean.executeSQL(Sql, "select");
//        } 
//        catch (Exception ex) 
//        {
//            //System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        
//        if (arrayData == null)
//            arrayData = new ArrayList();
//            
//        if (arrayData.size() > 0) 
//        {
//            hashData = (Hashtable) arrayData.get(0);
//            if (hashData == null)
//                hashData = new Hashtable();
//        }
//        
//        return hashData;
//    }

//    /* Override get_Merchant_User with DatabaseBean parameter */
//    public Hashtable get_Merchant_User(DataBaseBean SysBean, String MerchantID, String UserID) 
//    {
//        // DataBaseBean SysBean = new DataBaseBean();
//        Hashtable hashData = new Hashtable();
//        String Sql = "Select MERCHANT_ID,USER_ID,USER_NAME,USER_PWD,USER_STATUS,USER_PWD_ERRCNT,TO_CHAR(USER_INSERT_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_INSERT_DATE ,TO_CHAR(USER_UPDATE_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_UPDATE_DATE ,TO_CHAR(USER_LOCK_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_LOCK_DATE ,TO_CHAR(USER_CHANGERPWD_DATE, 'yyyy/mm/dd hh:mi:ss') AS USER_CHANGEPWD_DATE , To_Date(to_Char(SYSDATE,'yyyy/mm/dd'),'yyyy/mm/dd') - To_Date(to_char(USER_CHANGERPWD_DATE,'yyyy/mm/dd'),'yyyy/mm/dd') AS CHANGEPWD_DAY, ENF_UPDPWD, DEL_FLAG, TO_CHAR(EXPIRE_DATE, 'yyyymmdd') AS EXPIRE_DATE From MERCHANT_USER WHERE MERCHANT_ID = '" +
//                     MerchantID + "' AND USER_ID='" + UserID + "' ";
//        //System.out.println("Sql=" + Sql);
//        ArrayList arrayData = new ArrayList();
//        try {
//            arrayData = (ArrayList) SysBean.executeSQL(Sql, "select");
//        } catch (Exception ex) {
//            //System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        if (arrayData == null)
//            arrayData = new ArrayList();
//        if (arrayData.size() > 0) {
//            hashData = (Hashtable) arrayData.get(0);
//            if (hashData == null)
//                hashData = new Hashtable();
//        }
//        return hashData;
//    }
    
//    /**
//     * 取得特店主檔
//     * @param String MerchantID 特店代號
//     * @return Hashtable        特店主檔資料
//     */
//     
//    public Hashtable get_Merchant(String MerchantID) 
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        Hashtable hashData = new Hashtable();
//        //20200312 設定都是以母特店為主，抓特店資料時以抓母特店為主
//        String Sql = "Select *  From MERCHANT WHERE MERCHANTID = '" + MerchantID + "' AND SUBMID = SUBSTR(MerchantID,3)";
////      String Sql = "Select MERCHANTID,SUBMID,MERCHANTNAME,MERCHANTCHTNAME,ACQUIRERID,DIRECTORNAME,PHONE,FAX,POSTAL_CODE,MCCCODE,INVOICENO,INVOICEADDR,RATE,MANAGEPASSWD,CITY,MERCHANTURL,SERVICEMAIL,REFRESHDATE,STARTDATE,ACTIVATE_DATE,DEACTIVATE_DATE,END_DATE,MAINTAIN_DATE,ACTIVATE_USERID,MAINTAIN_USERID,DEACTIVATE_USERID,DEACTIVATE_FLAG,STATUS,CURRENTCODE,FTPHOST,FTPID,FTPPASSWD,ACQBIN,V3DPASSWORD,V3DFLAG,V3DMERCHANTID,J3DFLAG,J3DACQBIN,J3DMERCHANTID,J3DPASSWORD,M3DFLAG,M3DACQBIN,M3DMERCHANTID,M3DPASSWORD,CERTIFICATE,KEYFILE,CAFILE,VERESFAIL,PARESFAIL,SUPPORT_VISA,SUPPORT_MASTER,SUPPORT_JCBI,SUPPORT_UCARD,SUPPORT_AMEX,SUPPORT_TRAVEL,SUPPORT_BATCH_AUTH,SUPPORT_RECURRING,SUPPORT_ONLINE_AUTH,SUPPORT_EC_AUTH,PERMIT_SALE,PERMIT_REFUND,PERMIT_PARTIAL_REFUND,PERMIT_SALE_VOID,PERMIT_REFUND_VOID,PERMIT_CAPTURE_VOID,PERMIT_REDEM_SALE,PERMIT_INSTALL_SALE,PERMIT_CAPTURE,PERMIT_PARTIAL_CAPTURE,PERMIT_REAUTH,PERMIT_SHOW_PAN,PERMIT_INQUIRY_TX,PERMIT_HPP_UPLOAD,PERMIT_PRESALE,PERMIT_RISK_CARD,OVER_REFUND_LIMIT,CAPTURE_AUTO,CAPTURE_MANUAL,CAPTURE_UPLOAD,SLOTTYPE,HPP_URL,FORCE_CVV2 From MERCHANT WHERE MERCHANTID = '" +MerchantID+"' ";
//        // System.out.println("Sql=" + Sql);
//        ArrayList arrayData = new ArrayList();
//        
//        try 
//        {
//            arrayData = (ArrayList) SysBean.executeSQL(Sql, "select");
//        } 
//        catch (Exception ex) 
//        {
//            //System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        
//        if (arrayData == null)
//            arrayData = new ArrayList();
//            
//        if (arrayData.size() > 0) 
//        {
//            hashData = (Hashtable) arrayData.get(0);
//            if (hashData == null)
//                hashData = new Hashtable();
//        }
//        
//        return hashData;
//    }

    /* Override get_Merchant with DataBaseBean parameter */
    public Hashtable get_Merchant(DataBaseBean2 sysBean, String MerchantID) 
    {
        // DataBaseBean SysBean = new DataBaseBean();
        Hashtable hashData = new Hashtable();
        //20200312 設定都是以母特店為主，抓特店資料時以抓母特店為主
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("Select *  From MERCHANT WHERE MERCHANTID = ? AND SUBMID = SUBSTR(MerchantID,3)");
		sysBean.AddSQLParam(emDataType.STR, MerchantID);
		
//      String Sql = "Select MERCHANTID,SUBMID,MERCHANTNAME,MERCHANTCHTNAME,ACQUIRERID,DIRECTORNAME,PHONE,FAX,POSTAL_CODE,MCCCODE,INVOICENO,INVOICEADDR,RATE,MANAGEPASSWD,CITY,MERCHANTURL,SERVICEMAIL,REFRESHDATE,STARTDATE,ACTIVATE_DATE,DEACTIVATE_DATE,END_DATE,MAINTAIN_DATE,ACTIVATE_USERID,MAINTAIN_USERID,DEACTIVATE_USERID,DEACTIVATE_FLAG,STATUS,CURRENTCODE,FTPHOST,FTPID,FTPPASSWD,ACQBIN,V3DPASSWORD,V3DFLAG,V3DMERCHANTID,J3DFLAG,J3DACQBIN,J3DMERCHANTID,J3DPASSWORD,M3DFLAG,M3DACQBIN,M3DMERCHANTID,M3DPASSWORD,CERTIFICATE,KEYFILE,CAFILE,VERESFAIL,PARESFAIL,SUPPORT_VISA,SUPPORT_MASTER,SUPPORT_JCBI,SUPPORT_UCARD,SUPPORT_AMEX,SUPPORT_TRAVEL,SUPPORT_BATCH_AUTH,SUPPORT_RECURRING,SUPPORT_ONLINE_AUTH,SUPPORT_EC_AUTH,PERMIT_SALE,PERMIT_REFUND,PERMIT_PARTIAL_REFUND,PERMIT_SALE_VOID,PERMIT_REFUND_VOID,PERMIT_CAPTURE_VOID,PERMIT_REDEM_SALE,PERMIT_INSTALL_SALE,PERMIT_CAPTURE,PERMIT_PARTIAL_CAPTURE,PERMIT_REAUTH,PERMIT_SHOW_PAN,PERMIT_INQUIRY_TX,PERMIT_HPP_UPLOAD,PERMIT_PRESALE,PERMIT_RISK_CARD,OVER_REFUND_LIMIT,CAPTURE_AUTO,CAPTURE_MANUAL,CAPTURE_UPLOAD,SLOTTYPE,HPP_URL,FORCE_CVV2 From MERCHANT WHERE MERCHANTID = '" +MerchantID+"' ";
        // System.out.println("Sql=" + Sql);
        ArrayList arrayData = new ArrayList();
        try 
        {
        	/** 2023/05/17 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-091 (No Need Test) */
            arrayData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        } 
        catch (Exception ex) 
        {
            //System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }
        
        if (arrayData == null)
            arrayData = new ArrayList();
            
        if (arrayData.size() > 0) 
        {
            hashData = (Hashtable) arrayData.get(0);
            if (hashData == null)
                hashData = new Hashtable();
        }
        
        return hashData;
    }
    /*******************
     * EC MERCHANT OR ERM MERCHANT
     *******************/
    public Hashtable get_Merchant(DataBaseBean2 SysBean, String MerchantID,String SignBill) 
    {
    	if(SignBill == null) SignBill = "N";
        Hashtable hashData = new Hashtable();
        /** String Sql=""; */
        StringBuffer sSQLSB = new StringBuffer();
        SysBean.ClearSQLParam();
        if("N".equals(SignBill)) {
        	//20200312 設定都是以母特店為主，抓特店資料時以抓母特店為主
        	/** Sql = "Select * From MERCHANT WHERE MERCHANTID = '" + MerchantID + "' AND SUBMID = SUBSTR(MerchantID,3)"; */
        	sSQLSB.append("Select * From MERCHANT WHERE MERCHANTID = ? AND SUBMID = SUBSTR(MerchantID,3)");
        	
        	SysBean.AddSQLParam(emDataType.STR, MerchantID);
        }else {
        	/** Sql = "Select MERCHANTID ,MERCHANTCALLNAME,CURRENTCODE,PERMIT_INQUIRY_TX From ERM.T_ERM_MERCHANT WHERE MERCHANTID = '" + MerchantID + "'"; */
        	sSQLSB.append("Select MERCHANTID ,MERCHANTCALLNAME,CURRENTCODE,PERMIT_INQUIRY_TX From ERM.T_ERM_MERCHANT WHERE MERCHANTID = ? ");
        	
        	SysBean.AddSQLParam(emDataType.STR, MerchantID);        	
        }
        ArrayList arrayData = new ArrayList();
        try 
        {
        	/** 2023/04/19 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** IT-TESTCASE-010-020-002-001, TESTCASE-003*/
            arrayData = (ArrayList) SysBean.QuerySQLByParam(sSQLSB.toString());
        } 
        catch (Exception ex) 
        {
            //System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }
        
        if (arrayData == null)
            arrayData = new ArrayList();
            
        if (arrayData.size() > 0) 
        {
            hashData = (Hashtable) arrayData.get(0);
            if (hashData == null)
                hashData = new Hashtable();
        }
        
        return hashData;
    }

    /**
     * 取得特店主檔
     * @param String MerchantID 特店代號
     * @param String UserID     使用者代號
     * @return boolean
     */
    public boolean setUserPwdFlag(String MerchantID, String UserID) 
    {
    	DataBaseBean2 sysBean = new DataBaseBean2();
    	
        //寫入使用者登入時間
    	StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append(" UPDATE MERCHANT_USER SET ENF_UPDPWD = 'Y' ");
        sSQLSB.append(" WHERE MERCHANT_ID = ? ");
        sSQLSB.append(" AND USER_ID = ? ");
        // System.out.println("UpdateSql=" + UpdateSql);
        
        sysBean.AddSQLParam(emDataType.STR, MerchantID);
        sysBean.AddSQLParam(emDataType.STR, UserID);
        
        boolean flag = false;
        
        ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/05/23 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-081 */
        	arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        
        System.out.println("arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
        	flag = true;
        }
        
        return flag;
    }

//    /* Override setUserPwdFlag with DataBaseBean parameter */
//    public boolean setUserPwdFlag(DataBaseBean SysBean, String MerchantID, String UserID) 
//    {
//        //寫入使用者登入時間
//        String UpdateSql = " UPDATE MERCHANT_USER SET ENF_UPDPWD = 'Y' ";
//        UpdateSql += " WHERE MERCHANT_ID = '" + MerchantID + "'";
//        UpdateSql += " AND USER_ID='" + UserID + "' ";
//        boolean flag = false;
//        // System.out.println("UpdateSql=" + UpdateSql);
//        // DataBaseBean SysBean = new DataBaseBean();
//        
//        try
//        {
//            flag = ((Boolean) SysBean.executeSQL(UpdateSql, "update")). booleanValue();
//        } 
//        catch (Exception ex) 
//        {
//            //System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        
//        return flag;
//    }
    
    /**
     * 取得端末機主檔
     * @param String MerchantID 特店代號
     * @return ArrayList        特店端末機資料
     */
    public ArrayList get_Terminal(String MerchantID) 
    {
    	DataBaseBean2 sysBean = new DataBaseBean2();
    	
    	StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("Select MERCHANTID,TERMINALID,ACQUIRERID,OCCUPIED,STATUS,END_DATE,CURRENTCODE,PERMIT_SALE,PERMIT_REFUND,PERMIT_PARTIAL_REFUND,PERMIT_SALE_VOID,PERMIT_REFUND_VOID,PERMIT_CAPTURE_VOID,PERMIT_REDEM_SALE,PERMIT_INSTALL_SALE,PERMIT_CAPTURE,PERMIT_PARTIAL_CAPTURE,TRANS_TYPE,CAPTURE_TYPE,TRANS_KEY From TERMINAL WHERE MERCHANTID = ? ORDER BY TERMINALID ");
		sysBean.AddSQLParam(emDataType.STR, MerchantID);
//        System.out.println("Sql=" + Sql);
        ArrayList arraySYS = new ArrayList();
  
        try 
        {
        	/** 2023/05/23 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-082 */
            arraySYS = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex) 
        {
            //System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }
        
        return arraySYS;
    }

    /* Override get_Terminal with DataBaseBean parameter */
    public ArrayList get_Terminal(DataBaseBean2 sysBean, String MerchantID) 
    {
        // DataBaseBean SysBean = new DataBaseBean();
    	StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("Select MERCHANTID,TERMINALID,ACQUIRERID,OCCUPIED,STATUS,END_DATE,CURRENTCODE,PERMIT_SALE,PERMIT_REFUND,PERMIT_PARTIAL_REFUND,PERMIT_SALE_VOID,PERMIT_REFUND_VOID,PERMIT_CAPTURE_VOID,PERMIT_REDEM_SALE,PERMIT_INSTALL_SALE,PERMIT_CAPTURE,PERMIT_PARTIAL_CAPTURE,TRANS_TYPE,CAPTURE_TYPE,TRANS_KEY From TERMINAL WHERE MERCHANTID = ? ORDER BY TERMINALID ");
		sysBean.AddSQLParam(emDataType.STR, MerchantID);
		
        // System.out.println("Sql=" + Sql);
        ArrayList arraySYS = new ArrayList();
        
        try 
        {
        	/** 2023/05/17 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-092 (No Need Test) */
            arraySYS = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        } 
        catch (Exception ex) 
        {
            //System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }
        
        return arraySYS;
    }
    /**************************
     * EC特店Terminal 或 ERM特店Terminal
     **************************/
    public ArrayList get_Terminal(DataBaseBean2 sysBean, String MerchantID,String SignBill) 
    {
    	if(SignBill == null) SignBill ="N";
    	/** String Sql=""; */
    	StringBuffer sSQLSB = new StringBuffer();
    	sysBean.ClearSQLParam();
    	if("N".equals(SignBill)) {
    		/** Sql = "Select MERCHANTID,TERMINALID,ACQUIRERID,OCCUPIED,STATUS,END_DATE,CURRENTCODE,PERMIT_SALE,PERMIT_REFUND,PERMIT_PARTIAL_REFUND,PERMIT_SALE_VOID,PERMIT_REFUND_VOID,PERMIT_CAPTURE_VOID,PERMIT_REDEM_SALE,PERMIT_INSTALL_SALE,PERMIT_CAPTURE,PERMIT_PARTIAL_CAPTURE,TRANS_TYPE,CAPTURE_TYPE,TRANS_KEY From TERMINAL WHERE MERCHANTID = '" +
                    MerchantID + "' ORDER BY TERMINALID "; */
    		
    		sSQLSB.append("Select MERCHANTID,TERMINALID,ACQUIRERID,OCCUPIED,STATUS,END_DATE,CURRENTCODE,PERMIT_SALE,PERMIT_REFUND,PERMIT_PARTIAL_REFUND,PERMIT_SALE_VOID,PERMIT_REFUND_VOID,PERMIT_CAPTURE_VOID,PERMIT_REDEM_SALE,PERMIT_INSTALL_SALE,PERMIT_CAPTURE,PERMIT_PARTIAL_CAPTURE,TRANS_TYPE,CAPTURE_TYPE,TRANS_KEY From TERMINAL WHERE MERCHANTID = ? ORDER BY TERMINALID ");
    		
    		sysBean.AddSQLParam(emDataType.STR, MerchantID);
    	}else {
    		//ERM特店Terminal 沒使用到給dual data
    		/** Sql = "Select '"+MerchantID+"' AS MERCHANTID,'99999999' AS TERMINALID FROM DUAL"; */
    		
    		sSQLSB.append("Select '"+MerchantID+"' AS MERCHANTID,'99999999' AS TERMINALID FROM DUAL");
    		
    		/** SysBean.AddSQLParam(emDataType.STR, MerchantID); */
    	}
        ArrayList arraySYS = new ArrayList();
        try 
        {
        	/** 2023/04/19 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-004 */
            arraySYS = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        } 
        catch (Exception ex) 
        {
            //System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }
        
        return arraySYS;
    }
    
    /**
     * 確認特店使用者
     * @param Hashtable         特店資料
     * @param String MerchantID 特店代號
     * @param String UserID     使用者代號
     * @param String EnUserPwss    使用者密碼
     * @return String Flag      執行結果 nouser:無此使用者 Pwsserror:密碼錯誤 changPwss:需密碼變更 changNewPwss:首次登入需密碼變更 changResetPwss:密碼重置需密碼變更 changTimePwss:密碼已到期需密碼變更 Pwsslock:密碼鎖定 changealert:正常但需提示變更密碼 ok:正常 userlock:使用者停用
     */
    public String check_Merchant_User(Hashtable hashUser, String MerchantID, String UserID, String UserPwss,
                                      int intPwssChangDay, int intPwssAlertDay, int intMaxCheckPwss) throws NoSuchAlgorithmException, UnsupportedEncodingException ,Exception
    {
        UserBean UserBean = new UserBean();
        String userDel = "";
        String Pwss_Status = "";
        String Flag = "nouser"; // 無使用者
        if (hashUser.size() > 0) 
        { 
            // 資料庫查特店使用者資料
            userDel = String.valueOf(hashUser.get("DEL_FLAG"));
            if ("Y".equalsIgnoreCase(userDel)) 
            {
                // 使用者刪除
                Flag = "nouser"; // 無使用者
            } 
            else 
            {
                String enfUpdPwss = hashUser.get("ENF_UPDPWD") != null ? hashUser.get("ENF_UPDPWD").toString() : "";
                //System.out.println("enfUpdPwss=" + enfUpdPwss);
                Pwss_Status = hashUser.get("USER_STATUS").toString();
                if (Pwss_Status.equalsIgnoreCase("Y") || Pwss_Status.equalsIgnoreCase("O") || Pwss_Status.equalsIgnoreCase("R")) 
                { 
                    // 正常狀態及待啟用
                    String Pwss = String.valueOf(hashUser.get("USER_PWD"));//資料表內的雜錯密碼
                    //20220801 HKP MD5雜錯長度為32，SHA256雜錯長度為64，以此長度判斷是否為MD5，一律變更後的密碼一律使用SHA256 
                    //System.out.println("EnUserPwss=" + EnUserPwss + ",Pwss=" + Pwss);
                    String EnUserPwss ;
                    if(Pwss.length() == 32) EnUserPwss = MerchantChangePwdCtl.getMsgDigestPwd(UserPwss);
                    else EnUserPwss = Util.SHA256(Util.getPwdfactor(UserID, UserPwss));
                    int UserErrorCnt = 0;
                    if (MessageDigest.isEqual(EnUserPwss.getBytes(), Pwss.getBytes())) 
                    { 
                        // 密碼正確
                        //System.out.println("密碼正確");
                        //20220801 Pwss為MD5強制變更密碼為SHA256
                        if(Pwss.length() == 32) {
                        	DataBaseBean2 DBBean =new DataBaseBean2();DBBean.ClearSQLParam();
                        	StringBuffer sSQLSB = new StringBuffer();
                        	sSQLSB.append("UPDATE MERCHANT_USER SET USER_PWD=? WHERE MERCHANT_ID=? AND USER_ID=?");
                        	DBBean.AddSQLParam(emDataType.STR, Util.SHA256(Util.getPwdfactor(UserID, UserPwss))); 
                        	DBBean.AddSQLParam(emDataType.STR, MerchantID); 
                        	DBBean.AddSQLParam(emDataType.STR, UserID); 
                        	if(DBBean.executeSQL(sSQLSB.toString()) == true) {
                        		log_systeminfo.debug("MERCHANT_ID:"+MerchantID+",USER_ID:"+UserID+",PWD auto change to SHA256 success!!");
                        	}else {
                        		log_systeminfo.debug("MERCHANT_ID:"+MerchantID+",USER_ID:"+UserID+",PWD auto change to SHA256 fail !!");
                        	}
                        	DBBean.close();DBBean = null;
                        }
                        //20220801 pwd為MD5強制變更密碼為SHA256 end
                        if (Pwss_Status.equalsIgnoreCase("Y")) 
                        { 
                            // 正常狀態
                            String ChangePwss_Day = hashUser.get("CHANGEPWD_DAY").toString();
                            if (ChangePwss_Day.length() == 0) 
                            {
                               ChangePwss_Day = "0";
                            }
                            
                            //System.out.println("ChangePwss_Day=" + ChangePwss_Day);
                            int intDay = Integer.parseInt(ChangePwss_Day);
                            if (intPwssChangDay <= intDay) 
                            { 
                                // 密碼變更超過x個月
                                Flag = "changTimepwd"; // 變更密碼
                                setUserPwdFlag(MerchantID, UserID);
                            } 
                            else 
                            {
                                //System.out.println("(intPwdChangDay-intDay)=" + (intPwdChangDay - intDay) + ",intPwdAlertDay=" + intPwdAlertDay);
                                if ((intPwssChangDay - intDay) <= intPwssAlertDay) 
                                { 
                                    // 通知使用者變更密碼
                                    Flag = "changealert"; // 密碼正確需提醒密碼變更
                                } 
                                else 
                                {
                                    Flag = "ok"; // 密碼正確
                                }
                            }
                        } 
                        else if (Pwss_Status.equalsIgnoreCase("O")) 
                        {
                            //待啟用
                            String ExpireDate = hashUser.get("EXPIRE_DATE").toString();
                            String Today = UserBean.get_TransDate("yyyyMMdd");
                            //System.out.println("Today="+Today+",ExpireDate="+ExpireDate);
                            if (Double.parseDouble(Today) >  Double.parseDouble(ExpireDate)) 
                            {  
                                // 新密碼超過期限 2008.01.03
                                Flag = "pwdexpire"; // 新密碼超過期限
                                Pwss_Status = "N"; // 己鎖定
                                enfUpdPwss = "N";
                            } 
                            else 
                            {
                                Flag = "changNewpwd"; // 變更密碼
                            }
                        }
                        else if (Pwss_Status.equalsIgnoreCase("R")) 
                        { 
                            //密碼重置
                            String ExpireDate = hashUser.get("EXPIRE_DATE").toString();
                            String Today = UserBean.get_TransDate("yyyyMMdd");
                            //System.out.println("Today="+Today+",ExpireDate="+ExpireDate);
                            
                            if (Double.parseDouble(Today) >  Double.parseDouble(ExpireDate)) 
                            {  
                                // 新密碼超過期限 2008.01.03
                                Flag = "pwdexpire"; // 新密碼超過期限
                                Pwss_Status = "N"; // 己鎖定
                                enfUpdPwss = "N";
                            } 
                            else 
                            {
                                Flag = "changResetpwd"; // 變更密碼
                            }
                        }
                        else if (!"N".equalsIgnoreCase(enfUpdPwss))
                        {
                            //強迫修改密碼
                            Flag = "changpwd"; // 變更密碼
                        }

                        //寫入使用者登入時間
                        String UpdateSql = " UPDATE MERCHANT_USER SET FST_LOGIN_DATE = SYSDATE ";
                        UpdateSql += " WHERE MERCHANT_ID = '" + MerchantID + "'";
                        UpdateSql += " AND USER_ID='" + UserID + "' ";
                        // System.out.println("UpdateSql=" + UpdateSql);
                        boolean flag = UserBean.update_Data(UpdateSql);
                    } 
                    else 
                    {
                        String ErrCnt = hashUser.get("USER_PWD_ERRCNT").toString();
                        if (ErrCnt.length() == 0)
                            ErrCnt = "0";
                            
                        UserErrorCnt = Integer.parseInt(ErrCnt) + 1;
                        //System.out.println("UserErrorCnt=" + UserErrorCnt + ",intMaxCheckPwd=" + intMaxCheckPwd);
                        if (UserErrorCnt >= intMaxCheckPwss) 
                        { 
                            // 密碼超過限制次數
                            Pwss_Status = "N"; // 己鎖定
                            Flag = "pwdlock"; // 密碼己鎖定
                        } 
                        else 
                        {
                            Flag = "pwderror"; // 密碼錯誤
                        }
                    }
                    
                    String UpdateSql = "UPDATE MERCHANT_USER SET USER_STATUS = '" + Pwss_Status +
                                       "', USER_PWD_ERRCNT = '" + String.valueOf(UserErrorCnt) + "' ";

                    if (Flag.equalsIgnoreCase("pwdlock")) 
                    { 
                        // 密碼己鎖定
                        UpdateSql += " , USER_LOCK_DATE = SYSDATE";
                    }
                    
                    UpdateSql = UpdateSql + " WHERE MERCHANT_ID = '" + MerchantID + "' AND USER_ID='" + UserID + "' ";
                    // System.out.println("UpdateSql=" + UpdateSql);
                    boolean flag = UserBean.update_Data(UpdateSql);
                }
                else if (!"N".equalsIgnoreCase(enfUpdPwss))
                {
                    //強迫修改密碼
                    Flag = "changpwd"; // 變更密碼
                }
                
                if (!Flag.equalsIgnoreCase("pwdexpire")) 
                {
                    if (Pwss_Status.equalsIgnoreCase("N")) 
                    { 
                        // 密碼鎖定
                        Flag = "pwdlock"; // 密碼錯誤
                    }
                    
                    if (Pwss_Status.equalsIgnoreCase("E")) 
                    { // 使用者停用
                        Flag = "userlock"; // 使用者停用
                    }
                }
            }
        }
        
        return Flag;
    }

//    /**
//     * 新增公告資料(MsgBoard)
//     * @param  String SysType    	訊息類別(M:特店)
//     * @param  String MsgNo      	訊息代號
//     * @param  String Msg_Content 	訊息內容
//     * @param  String Msg_Show   	訊息狀態(Y:顯示)
//     * @param  String UpdUser   	維護人員
//     * @return boolean                  新增結果
//     */
//
//    public boolean insert_MsgBoard(String SysType, String MsgNo, String Msg_Content, String Msg_Show, String UpdUser) 
//    {
//        boolean Flag = false;
//        String Sql = " INSERT INTO MSGBOARD (SYSTYPE , MSG_NO, MSG_CONTENT, MSG_SHOW, UPDDATE, UPDUSER) VALUES ( ";
//        Sql = Sql + "'" + SysType + "', ";
//        Sql = Sql + " " + MsgNo + ", ";
//        Sql = Sql + "'" + Msg_Content + "', ";
//        Sql = Sql + "'" + Msg_Show + "', ";
//        Sql = Sql + "SYSDATE, ";
//        Sql = Sql + "'" + UpdUser + "' )";
//        // System.out.println("Sql=" + Sql);
//        
//        try 
//        {
//            DataBaseBean SysBean = new DataBaseBean();
//            Flag = ((Boolean) SysBean.executeTranSQL(Sql, "insert")).booleanValue();
//        } 
//        catch (Exception ex) 
//        {
//            //System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        
//        return Flag;
//    }
    
//    /* Override insert_MsgBoard with DataBaseBean parameter */
//    public boolean insert_MsgBoard(DataBaseBean SysBean, String SysType, String MsgNo, String Msg_Content, String Msg_Show, String UpdUser) 
//    {
//        boolean Flag = false;
//        String Sql = " INSERT INTO MSGBOARD (SYSTYPE , MSG_NO, MSG_CONTENT, MSG_SHOW, UPDDATE, UPDUSER) VALUES ( ";
//        Sql = Sql + "'" + SysType + "', ";
//        Sql = Sql + " " + MsgNo + ", ";
//        Sql = Sql + "'" + Msg_Content + "', ";
//        Sql = Sql + "'" + Msg_Show + "', ";
//        Sql = Sql + "SYSDATE, ";
//        Sql = Sql + "'" + UpdUser + "' )";
//        // System.out.println("Sql=" + Sql);
//        
//        try 
//        {
//            // DataBaseBean SysBean = new DataBaseBean();
//            Flag = ((Boolean) SysBean.executeTranSQL(Sql, "insert")).booleanValue();
//        } 
//        catch (Exception ex) 
//        {
//            //System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        
//        return Flag;
//    }

    /**
     * 取得公告資料
     * @param  String SysType  	訊息類別(M:特店)
     * @return ArrayList        公告資料
     */
    public ArrayList get_MsgBoard(String SysType) 
    {
        DataBaseBean2 sysBean = new DataBaseBean2();
        
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("Select * FROM MSGBOARD WHERE SYSTYPE = ? ORDER BY MSG_NO ");
        // System.out.println("Sql=" + Sql);
		
		sysBean.AddSQLParam(emDataType.STR, SysType);
		
        ArrayList arraySYS = new ArrayList();
        
        try 
        {
        	/** 2023/05/23 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-083 */
            arraySYS = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        } 
        catch (Exception ex) 
        {
            //System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }
        //20130603 Jason Message Board exit release Session
        finally{        	
        try{
        	sysBean.close();
        }
        catch(Exception ex){
            //System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }
        }
        
        return arraySYS;
    }

//    /* Override get_MsgBoard with DataBaseBean parameter */
//    public ArrayList get_MsgBoard(DataBaseBean SysBean, String SysType) 
//    {
//        // DataBaseBean SysBean = new DataBaseBean();
//        String Sql = "Select * FROM MSGBOARD WHERE SYSTYPE = '" + SysType + "' ORDER BY MSG_NO ";
//        // System.out.println("Sql=" + Sql);
//        ArrayList arraySYS = new ArrayList();
//        
//        try 
//        {
//            arraySYS = (ArrayList) SysBean.executeSQL(Sql, "select");
//        } 
//        catch (Exception ex) 
//        {
//            //System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        
//        return arraySYS;
//    }
    
//    /**
//     * 取得使用者權限
//     * @param String MerchantID 特約商店代號
//     * @param String UserID     使用者代號
//     * @param String RoleID     群組代號 (非必要欄位)
//     * @param String MenuID     功能代號 (非必要欄位)
//     * @return ArrayList
//     */
//    public ArrayList get_Merchant_User_Role(String MerchantID, String UserID, String MenuID)
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        ArrayList arrayData = new ArrayList();
//
//        String Sql = "select  MERUSER_ROLE.*  From  MERUSER_ROLE WHERE MERUSER_ROLE.MERCHANTID = '" + MerchantID + "'" +
//                     " AND  MERUSER_ROLE.USERID = '" + UserID + "'";
//        if (MenuID.length()>0) 
//        {
//            Sql = Sql + " AND MERUSER_ROLE.MENUID = '" + MenuID + "'";
//        }
//        
//        // System.out.println("Sql=" + Sql);
//        
//        try
//        {
//            arrayData = (ArrayList) SysBean.executeSQL(Sql, "select");
//        }
//        catch (Exception ex)
//        {
//            //System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        
//        return arrayData;
//    }

    /* Override get_Merchant_User_Role with DataBaseBean parameter */
    public ArrayList get_Merchant_User_Role(DataBaseBean2 sysBean, String MerchantID, String UserID, String MenuID)
    {
        // DataBaseBean SysBean = new DataBaseBean();
        ArrayList arrayData = new ArrayList();
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
        sSQLSB.append("select  MERUSER_ROLE.*  From  MERUSER_ROLE WHERE MERUSER_ROLE.MERCHANTID = ? " +
                " AND  MERUSER_ROLE.USERID = ? ");
        sysBean.AddSQLParam(emDataType.STR, MerchantID);
        sysBean.AddSQLParam(emDataType.STR, UserID);
        if (MenuID.length()>0) 
        {
        	sSQLSB.append(" AND MERUSER_ROLE.MENUID = ? ");
        	sysBean.AddSQLParam(emDataType.STR, MenuID);
        }
        
        // System.out.println("Sql=" + Sql);
        
        try
        {
        	/** 2023/04/21 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-006 */
            arrayData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            //System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }
        
        return arrayData;
    }
    //取得子特店清單
	public ArrayList get_SubMid(DataBaseBean2 sysBean, String merchantID,String sSignBill) {
        // 201907 201906180256 特店管理介面子特店下拉選單排序 SHERRY ANN
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("Select SUBMID,MERCHANTCALLNAME From MERCHANT Where MERCHANTID = ? order by SUBMID "); //AND substr(MERCHANTID,length(MERCHANTID)-12) <>SUBMID ";
		sysBean.AddSQLParam(emDataType.STR, merchantID);
        ArrayList arraySYS = new ArrayList();
        
        try 
        {
        	/** 2023/04/21 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-005 */
            arraySYS = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        } 
        catch (Exception ex) 
        {
            //System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }
        
        return arraySYS;
	}
}
