/************************************************************
 * <p>#File Name:   MerchantCaptureBean.java         </p>
 * <p>#Description:                         </p>
 * <p>#Create Date: 2007/10/02              </p>
 * <p>#Company:     cybersoft               </p>
 * <p>#Notice:                      </p>
 * @author      Shirley Lin
 * @since       SPEC version
 * @version 0.1 2007/10/02  Shirley Lin
 * 202007070145-00 20200707 HKP 路跑協會需求UI增加整批請款功能
 *    Tag:20200708-01
 *    1.UI:起日、迄日、交易類別(全部、購貨、退貨)
 *    2.顯示總購貨筆數、金額、總退貨筆數、金額，USER確定後進行整批請款，再顯示結果
 * 202112300619-01 20220210 GARY 請款批次請款規格增修(Visa Authorization Source Code AUTH_SRC_CODE)
 ************************************************************/
package com.cybersoft.merchant.bean;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft.bean.DataBaseBean2;

import java.util.Hashtable;
import java.security.MessageDigest;
import com.cybersoft.bean.UserBean;
import com.cybersoft.bean.emDataType;
import com.fubon.security.filter.SecurityTool;
import com.cybersoft.bean.LogUtils;

public class MerchantCaptureBean
{
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    public MerchantCaptureBean()
    {
    }

//    /**
//     * 取得待請款資料(Billing)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店代號
//     * @param String StartTransDate 查詢開始日期
//     * @param String EndTransDate   查詢結束日期
//     * @param String TerminalID     查詢端末機
//     * @param String TransCode      交易類別 (ALL:全部 00:一般交易 01:退貨交易)
//     * @param String OrderType      查詢訂單類別 (Order:特店指定單號 System:系統指定單號 )
//     * @param String OrderID        訂單代號
//     * @param String ReversalFlag   Reversal註記 (default 0, 1:交易回沖)
//     * @param String CaptureDay     請款期限
//     * @return ArrayList            列表資料
//     */
//    public ArrayList get_Capture_List(String MerchantID, String SubMID,
//                                      String StartTransDate,
//                                      String EndTransDate, String TerminalID,
//                                      String TransCode, String OrderType,
//                                      String OrderID, String ReversalFlag,
//                                      String CaptureDay)
//    {
//        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
//        DataBaseBean SysBean = new DataBaseBean();
//
//        if (MerchantID.length() > 0 && SubMID.length() > 0 && StartTransDate.length() > 0 && EndTransDate.length() > 0)
//        {
//            StringBuffer Sql = new StringBuffer("SELECT A.*, TO_CHAR(TO_DATE( TRANSDATE,'YYYYMMDD') + " +
//                    CaptureDay + ", 'YYYY/MM/DD') CAPTUREDDEALLINE FROM (SELECT TRANSCODE,TRANSMODE,BALANCEAMT,SYS_ORDERID,TERMINALID,ORDERID,TRANSDATE,TRANSTIME,RESPONSECODE,BATCHNO,APPROVECODE,TRANSAMT  FROM BILLING WHERE BALANCEAMT > 0 ");
//            Sql.append(" AND TO_DATE(TRANSDATE, 'yyyymmdd') >= TO_DATE('" + StartTransDate +
//                  "', 'yyyy/mm/dd') AND TO_DATE(TRANSDATE, 'yyyymmdd') <= TO_DATE('" + EndTransDate + "', 'yyyy/mm/dd') ");
//            Sql.append(" AND MERCHANTID = '" + MerchantID + "' ");
//            Sql.append(" AND SUBMID = '" + SubMID + "' ");
//            if (TerminalID.length() > 0 && !TerminalID.equalsIgnoreCase("ALL"))
//            {
//                Sql.append(" AND TERMINALID = '" + TerminalID + "'");
//            }
//
//            if (TransCode.length() > 0 && !TransCode.equalsIgnoreCase("ALL"))
//            {
//                Sql.append(" AND TRANSCODE = '" + TransCode + "'");
//            }
//
//            if (OrderType.length() > 0 && OrderID.length() > 0)
//            {
//                if (OrderType.equalsIgnoreCase("Order"))
//                {
//                    // 以OrderID
//                    Sql.append(" AND ORDERID = '" + OrderID + "' ");
//                }
//                else
//                {
//                    Sql.append(" AND SYS_ORDERID = '" + OrderID + "' ");
//                }
//            }
//
//            if (ReversalFlag.length() > 0)
//            {
//                Sql.append(" AND REVERSALFLAG = '" + ReversalFlag + "' ");
//            }
//
//            Sql.append(" ORDER BY TERMINALID, SYS_ORDERID, TRANSCODE, TRANSDATE, TRANSTIME) A ");
//            //System.out.println("Sql=" + Sql);
//            try
//            {
//                arrayBillingData = (ArrayList) SysBean.executeSQL(Sql.toString(), "select");
//            }
//            catch (Exception ex)
//            {
//                System.out.println(ex.getMessage());
//                log_systeminfo.debug(ex.toString());
//            }
//
//            if (arrayBillingData == null)
//            {
//                arrayBillingData = new ArrayList();
//            }
//        }
//
//        return arrayBillingData;
//    }

    /* Override get_Capture_List with DataBaseBean parameter */
    // 在 get_Capture_List method 中加入參數 TransType, 修改  by Jimmy Kang 20150515 Merchant Console 線上請款作業模組
    public ArrayList get_Capture_List(DataBaseBean2 sysBean, String MerchantID, String SubMID, String StartTransDate,
                                      String EndTransDate, String TerminalID, String TransCode, String OrderType,
                                      String OrderID, String ReversalFlag, String CaptureDay, String TransType)
    {
        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
        // DataBaseBean SysBean = new DataBaseBean();
        if (MerchantID.length() > 0 && SubMID.length() > 0 && StartTransDate.length() > 0 && EndTransDate.length() > 0)
        {
            StringBuffer sSQLSB = new StringBuffer();
            sysBean.ClearSQLParam();
            sSQLSB.append("SELECT A.*, TO_CHAR(TO_DATE( TRANSDATE,'YYYYMMDD') + ? , 'YYYY/MM/DD') CAPTUREDDEALLINE FROM (SELECT  TRANSCODE,TRANSMODE,BALANCEAMT,SYS_ORDERID,TERMINALID,ORDERID,TRANSDATE,TRANSTIME,RESPONSECODE,BATCHNO,APPROVECODE,TRANSAMT  FROM BILLING WHERE BALANCEAMT > 0 ");
            sSQLSB.append(" AND TO_DATE(TRANSDATE, 'yyyymmdd') >= TO_DATE(? , 'yyyy/mm/dd') AND TO_DATE(TRANSDATE, 'yyyymmdd') <= TO_DATE(? , 'yyyy/mm/dd') ");
            sSQLSB.append(" AND MERCHANTID = ? ");
            sSQLSB.append(" AND SUBMID = ? ");
            
            sysBean.AddSQLParam(emDataType.INT, CaptureDay);
            sysBean.AddSQLParam(emDataType.STR, StartTransDate);
            sysBean.AddSQLParam(emDataType.STR, MerchantID);
            sysBean.AddSQLParam(emDataType.STR, SubMID);
            
            if (TerminalID.length() > 0 && !TerminalID.equalsIgnoreCase("ALL"))
            {
            	sSQLSB.append(" AND TERMINALID = ? ");
            	sysBean.AddSQLParam(emDataType.STR, TerminalID);
            }

            if (TransCode.length() > 0 && !TransCode.equalsIgnoreCase("ALL"))
            {
            	sSQLSB.append(" AND TRANSCODE = ? ");
            	sysBean.AddSQLParam(emDataType.STR, TransCode);
            }

            // Merchant Console 線上請款作業模組  新增  by Jimmy Kang 20150515 --新增開始--
            if (TransType.length() > 0 && !TransType.equalsIgnoreCase("ALL"))
            {
            	if (TransType.equalsIgnoreCase("VMJ"))
            	{
            		sSQLSB.append(" AND TRANSTYPE IN ( 'V3D', 'M3D', 'J3D')");
            	}
            	else
            	{
            		sSQLSB.append(" AND TRANSTYPE = ? ");
            		sysBean.AddSQLParam(emDataType.STR, TransType);
            	}
            }
            // Merchant Console 線上請款作業模組  新增  by Jimmy Kang 20150515 --新增結束--
            
            if (OrderType.length() > 0 && OrderID.length() > 0)
            {
                if (OrderType.equalsIgnoreCase("Order"))
                {
                    // 以OrderID
                	sSQLSB.append(" AND ORDERID = ? ");
                	sysBean.AddSQLParam(emDataType.STR, OrderID);
                }
                else
                {
                	sSQLSB.append(" AND SYS_ORDERID = ? ");
                	sysBean.AddSQLParam(emDataType.STR, OrderID);
                }
            }

            if (ReversalFlag.length() > 0)
            {
            	sSQLSB.append(" AND REVERSALFLAG = ? ");
            	sysBean.AddSQLParam(emDataType.STR, ReversalFlag);
            }

            sSQLSB.append(" ORDER BY TERMINALID, SYS_ORDERID, TRANSCODE, TRANSDATE, TRANSTIME) A ");
            System.out.println("MerchantCaptureBean.get_Capture_List Sql=" + sSQLSB.toString());
            try
            {
            	/** 2023/04/25 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-010 */
            	arrayBillingData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
                log_systeminfo.debug(ex.toString());
            }

            if (arrayBillingData == null)
            {
                arrayBillingData = new ArrayList();
            }
        }

        return arrayBillingData;
    }

//    /**
//     * 取得待確認請款資料(Billing+Billing)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店代號
//     * @param String SysOrderID     系統指定單號
//     * @param String CaptureAmt     請款金額
//     * @param String CaptureDay     請款期限天數
//     * @return ArrayList            列表資料
//     */
//    public ArrayList get_Capture_Check_List(String MerchantID, String SubMID, String SysOrderID[],
//                                            String CaptureAmt[], String CaptureDay)
//    {
//        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
//        DataBaseBean SysBean = new DataBaseBean();
//
//        if (MerchantID.length() > 0 && SubMID.length() > 0 && SysOrderID.length > 0)
//        {
//            //            StringBuffer Sql = new StringBuffer(" SELECT A.RROWID ,A.MERCHANTID, A.SUBMID, A.ORDERID, A.ACQUIRERID, A.CARD_TYPE, A.PAN, A.EXPIREDATE, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.USERDEFINE, A.ENTRY_MODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG, A.CONDITION_CODE, A.ECI, A.CAVV, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE ,A.EXTENNO, A.SYSTRACENO, A.REVERSALFLAG,A.EMAIL,A.MTI,A.RRN,A.SOCIALID,A.ENTRY_MODE,A.TRANSTYPE,A.XID, A.DUE_DATE, B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT, NVL(B.REFUNDCAPTUREAMT,0) REFUNDCAPTUREAMT , TO_CHAR(TO_DATE( TRANSDATE,'YYYYMMDD') + " +
//            StringBuffer Sql = new StringBuffer(" SELECT A.RROWID ,A.MERCHANTID, A.SUBMID, A.ORDERID, A.ACQUIRERID, A.CARD_TYPE, A.PAN, A.EXPIREDATE, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.USERDEFINE, A.ENTRY_MODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG, A.CONDITION_CODE, A.ECI, A.CAVV, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE ,A.EXTENNO, A.SYSTRACENO,A.EMAIL,A.MTI,A.RRN,A.SOCIALID,A.TRANSTYPE,A.XID, A.DUE_DATE, A.REAUTH_FLAG, B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT, NVL(B.REFUNDCAPTUREAMT,0) REFUNDCAPTUREAMT , TO_CHAR(TO_DATE( TRANSDATE,'YYYYMMDD') + " +  CaptureDay + ", 'YYYY/MM/DD') CAPTUREDDEALLINE");
//            Sql.append(" FROM ( SELECT ROWIDTOCHAR(X.ROWID) AS RROWID, X.*  FROM BILLING X, ( SELECT MERCHANTID, SUBMID, ORDERID FROM BILLING WHERE ");
//            Sql.append("MERCHANTID = '" + MerchantID + "'  AND SUBMID = '" + SubMID + "' ");
//            Sql.append(") Y  WHERE X.MERCHANTID = Y.MERCHANTID AND X.SUBMID = Y.SUBMID AND X.ORDERID = Y.ORDERID AND ( ");
//            String TmpSql = "";
//
//            for (int c = 0; c < SysOrderID.length; ++c)
//            {
//                if (TmpSql.length() > 0)
//                    TmpSql = TmpSql + " OR ";
//
//                TmpSql = TmpSql + " X.SYS_ORDERID = '" + SysOrderID[c] + "' ";
//            }
//
//            Sql.append(TmpSql);
//            Sql.append(" )  ORDER BY X.TERMINALID, X.SYS_ORDERID, X.TRANSCODE, X.TRANSDATE, X.TRANSTIME) A, BALANCE B WHERE A.MERCHANTID = B.MERCHANTID AND A.SUBMID = B.SUBMID AND A.ORDERID = B.ORDERID GROUP BY A.RROWID , A.MERCHANTID, A.SUBMID, A.ORDERID, A.CARD_TYPE, A.PAN, A.EXPIREDATE, A.ENTRY_MODE, A.USERDEFINE, A.ACQUIRERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG, A.CONDITION_CODE, A.ECI, A.CAVV, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.EXTENNO, A.BILLMESSAGE, A.SYSTRACENO, A.REVERSALFLAG,A.EMAIL,A.MTI,A.RRN,A.SOCIALID,A.ENTRY_MODE,A.TRANSTYPE,A.XID, A.DUE_DATE, A.REAUTH_FLAG ,B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT, B.REFUNDCAPTUREAMT  ");
//            // System.out.println("Sql=" + Sql);
//            try
//            {
//                arrayBillingData = (ArrayList) SysBean.executeSQL(Sql.toString(), "select");
//            }
//            catch (Exception ex)
//            {
//                System.out.println(ex.getMessage());
//                log_systeminfo.debug(ex.toString());
//            }
//
//            if (arrayBillingData == null)
//            {
//                arrayBillingData = new ArrayList();
//            }
//
//            ArrayList arrayTemp = new ArrayList();
//            if (arrayBillingData.size() > 0)
//            {
//                for (int i = 0; i < arrayBillingData.size(); i++)
//                {
//                    Hashtable hashtemp = (Hashtable) arrayBillingData.get(i);
//                    String sysorderid = hashtemp.get("SYS_ORDERID").toString();
//
//                    for (int j = 0; j < SysOrderID.length; ++j)
//                    {
//                        if (sysorderid.equalsIgnoreCase(SysOrderID[j]))
//                        {
//                            hashtemp.put("TOCAPTUREAMT", CaptureAmt[j]);
//                            arrayTemp.add(hashtemp);
//                            break;
//                        }
//                    }
//                }
//                arrayBillingData = arrayTemp;
//            }
//        }
//
//        return arrayBillingData;
//    }

    /* Override get_Capture_Check_List with DataBaseBean parameter */
    public ArrayList get_Capture_Check_List(DataBaseBean2 sysBean, String MerchantID, String SubMID, String SysOrderID[],
                                            String CaptureAmt[], String CaptureDay)
    {
        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
        // DataBaseBean SysBean = new DataBaseBean();

        if (MerchantID.length() > 0 && SubMID.length() > 0 && SysOrderID.length > 0)
        {
            //            StringBuffer Sql = new StringBuffer(" SELECT A.RROWID ,A.MERCHANTID, A.SUBMID, A.ORDERID, A.ACQUIRERID, A.CARD_TYPE, A.PAN, A.EXPIREDATE, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.USERDEFINE, A.ENTRY_MODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG, A.CONDITION_CODE, A.ECI, A.CAVV, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE ,A.EXTENNO, A.SYSTRACENO, A.REVERSALFLAG,A.EMAIL,A.MTI,A.RRN,A.SOCIALID,A.ENTRY_MODE,A.TRANSTYPE,A.XID, A.DUE_DATE, B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT, NVL(B.REFUNDCAPTUREAMT,0) REFUNDCAPTUREAMT , TO_CHAR(TO_DATE( TRANSDATE,'YYYYMMDD') + " +
        	//20220210 ADD AUTH_SRC_CODE    		
    		StringBuffer sSQLSB = new StringBuffer();
    		sysBean.ClearSQLParam();
    		sSQLSB.append(" SELECT A.RROWID ,A.MERCHANTID, A.SUBMID, A.ORDERID, A.ACQUIRERID, A.CARD_TYPE, A.PAN, A.EXPIREDATE, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.USERDEFINE, A.ENTRY_MODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG, A.CONDITION_CODE, A.ECI, A.CAVV, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE ,A.EXTENNO, A.SYSTRACENO,A.EMAIL,A.MTI,A.RRN,A.SOCIALID,A.TRANSTYPE,A.XID, A.DUE_DATE, A.REAUTH_FLAG, B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT, NVL(B.REFUNDCAPTUREAMT,0) REFUNDCAPTUREAMT , TO_CHAR(TO_DATE( TRANSDATE,'YYYYMMDD') + ? , 'YYYY/MM/DD') CAPTUREDDEALLINE,A.AUTH_SRC_CODE");
    		sSQLSB.append(" FROM ( SELECT ROWIDTOCHAR(X.ROWID) AS RROWID, X.*  FROM BILLING X, ( SELECT MERCHANTID, SUBMID, ORDERID FROM BILLING WHERE ");
    		sSQLSB.append(" MERCHANTID = ?  AND SUBMID = ? ");
    		sSQLSB.append(" ) Y  WHERE X.MERCHANTID = Y.MERCHANTID AND X.SUBMID = Y.SUBMID AND X.ORDERID = Y.ORDERID AND ( ");
    		
    		sysBean.AddSQLParam(emDataType.INT, CaptureDay);
    		sysBean.AddSQLParam(emDataType.STR, MerchantID);
    		sysBean.AddSQLParam(emDataType.STR, SubMID);
    		
            String TmpSql = "";

            for (int c = 0; c < SysOrderID.length; ++c)
            {
                if (TmpSql.length() > 0)
                    TmpSql = TmpSql + " OR ";

                TmpSql = TmpSql + " X.SYS_ORDERID = ? ";
                sysBean.AddSQLParam(emDataType.STR, SysOrderID[c]);
            }

            sSQLSB.append(TmpSql);
            sSQLSB.append(" )  ORDER BY X.TERMINALID, X.SYS_ORDERID, X.TRANSCODE, X.TRANSDATE, X.TRANSTIME) A, BALANCE B WHERE A.MERCHANTID = B.MERCHANTID AND A.SUBMID = B.SUBMID AND A.ORDERID = B.ORDERID GROUP BY A.RROWID , A.MERCHANTID, A.SUBMID, A.ORDERID, A.CARD_TYPE, A.PAN, A.EXPIREDATE, A.ENTRY_MODE, A.USERDEFINE, A.ACQUIRERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG, A.CONDITION_CODE, A.ECI, A.CAVV, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.EXTENNO, A.BILLMESSAGE, A.SYSTRACENO, A.REVERSALFLAG,A.EMAIL,A.MTI,A.RRN,A.SOCIALID,A.ENTRY_MODE,A.TRANSTYPE,A.XID, A.DUE_DATE, A.REAUTH_FLAG ,B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT, B.REFUNDCAPTUREAMT,A.AUTH_SRC_CODE  ");
            // System.out.println("Sql=" + Sql);
            try
            {
            	/** 2023/04/26 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-011 */
                arrayBillingData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
                log_systeminfo.debug(ex.toString());
            }

            if (arrayBillingData == null)
            {
                arrayBillingData = new ArrayList();
            }

            ArrayList arrayTemp = new ArrayList();

            int Elements = arrayBillingData.size();
            System.out.println ("arrayBillingData.size=" + Elements + " records.");
            if (Elements > 0)
            {
                for (int i = 0; i < Elements; i++)
                {
                    Hashtable hashtemp = (Hashtable) arrayBillingData.get(i);
                    String sysorderid = hashtemp.get("SYS_ORDERID").toString();
                    System.out.println ("arrayBillingData.Index=" + i);

                    for (int j = 0; j < SysOrderID.length; ++j)
                    {
                        if (sysorderid.equalsIgnoreCase(SysOrderID[j]))
                        {
                            hashtemp.put("TOCAPTUREAMT", CaptureAmt[j]);
                            arrayTemp.add(hashtemp);
                            break;
                        }
                    }
                }
                arrayBillingData = arrayTemp;
            }
        }

        return arrayBillingData;
    }

    /**
     * 彙總本次訂單請款金額
     * @param  ArrayList arrayList 請款資料
     * @return Hashtable     彙總資料
     */
    public Hashtable sum_Capture_Amt(ArrayList arrayList)
    {
        Hashtable hashSum = new Hashtable();

        for (int i = 0; i < arrayList.size(); ++i)
        {
            Hashtable hashList = (Hashtable) arrayList.get(i);
//            System.out.println("sum_Capture_Amt= hashList=" + hashList);
            String OrderID = hashList.get("ORDERID").toString();
            Hashtable hashtemp = (Hashtable) hashSum.get(OrderID);

            if (hashtemp == null)
                hashtemp = new Hashtable();

            String OrderCaptureAmt = "0";
            String OrderRefundAmt = "0";

            if (hashtemp.size() == 0)
            {
                String AuthAmt = hashList.get("AUTHAMT").toString();
                String RefundAmt = hashList.get("REFUNDAMT").toString();
                String CaptureAmt = hashList.get("CAPTUREAMT").toString();
                String RefundCaptureAmt = hashList.get("REFUNDCAPTUREAMT").toString();
                hashtemp.put("AuthAmt", AuthAmt);
                hashtemp.put("RefundAmt", RefundAmt);
                hashtemp.put("CaptureAmt", CaptureAmt);
                hashtemp.put("RefundCaptureAmt", RefundCaptureAmt);
            }
            else
            {
                String TmpCaptureAmt = hashtemp.get("OrderCaptureAmt").toString();
                String TmpRefundAmt = hashtemp.get("OrderRefundAmt").toString();

                if (TmpCaptureAmt.length() > 0)
                {
                    OrderCaptureAmt = TmpCaptureAmt;
                }

                if (TmpRefundAmt.length() > 0)
                {
                    OrderRefundAmt = TmpRefundAmt;
                }
            }

            String ToCaptureAmt = hashList.get("TOCAPTUREAMT").toString();
            String TransCode = hashList.get("TRANSCODE").toString();

            if (TransCode.equalsIgnoreCase("00"))
            {
                // 購貨
                OrderCaptureAmt = String.valueOf(Double.parseDouble(OrderCaptureAmt) + Double.parseDouble(ToCaptureAmt));
            }

            if (TransCode.equalsIgnoreCase("01"))
            {
                // 退貨
                OrderRefundAmt = String.valueOf(Double.parseDouble(OrderRefundAmt) + Double.parseDouble(ToCaptureAmt));
            }

            hashtemp.put("OrderCaptureAmt", OrderCaptureAmt);
            hashtemp.put("OrderRefundAmt", OrderRefundAmt);
//            System.out.println("hashtemp=" + hashtemp);
            hashSum.put(OrderID, hashtemp);
        }
//        System.out.println("hashSum=" + hashSum);
        return hashSum;
    }

    /**
     * 檢核請款資料(線上請款第一步check)
     * @param  ArrayList  arrayList       請款資料
     * @param  ArrayList  arrayCardTest   測試卡主檔
     * @return ArrayList  arraySuccess    成功資料
     */
    public ArrayList check_Capture_Amt(ArrayList arrayList, ArrayList arrayCardTest)
    {
        ArrayList arraySuccess = new ArrayList();
        UserBean UserBean = new UserBean();

        for (int i = 0; i < arrayList.size(); ++i)
        {
            boolean flag = false;
            Hashtable hashList = (Hashtable) arrayList.get(i);
            String Pan = hashList.get("PAN").toString();
            boolean Panflag = UserBean.check_Test_Card(arrayCardTest, Pan);
            // System.out.println("Panflag=" + Panflag);

            if (Panflag)
            {
                String DueDate = hashList.get("CAPTUREDDEALLINE").toString().replaceAll("/", "");
                String Today = UserBean.get_TransDate("yyyyMMdd");

                if (Double.parseDouble(DueDate) >= Double.parseDouble(Today))
                {
                    String BalanceAmt = hashList.get("BALANCEAMT").toString(); // Billing.balanceamt
                    String ToCaptureAmt = hashList.get("TOCAPTUREAMT").toString(); // 請款金額

                    if (Double.parseDouble(BalanceAmt) >= Double.parseDouble(ToCaptureAmt) && Double.parseDouble(ToCaptureAmt) > 0)
                    {
                        arraySuccess.add(hashList);
                    }
                }
            }
        }

        return arraySuccess;
    }


    /**
     * 檢核請款與授權交易記錄(線上請款第二步check)
     * @param  ArrayList  arrayList       請款資料
     * @param  Hashtable  hasSumAmt       彙總資料
     * @param  String     PartialFlag     分批請款註記
     * @param  ArrayList  arrayTerminal   端末機代號
     * @param  ArrayList  OverRefundLimit 超額退貨金額
     * @param  ArrayList  arrayCardTest   測試卡主檔
     * @return Hashtable  成功失敗資料
     */
    public Hashtable check_Capture_Amt(ArrayList arrayList, Hashtable hasSumAmt, String PartialFlag,
                                       ArrayList arrayTerminal, String OverRefundLimit, ArrayList arrayCardTest)
    {
        Hashtable hashCheck = new Hashtable();
        Hashtable hashSuccess = new Hashtable();
        Hashtable hashFail = new Hashtable();
        UserBean UserBean = new UserBean();

        for (int i = 0; i < arrayList.size(); ++i)
        {
            boolean flag = false;
            Hashtable hashList = (Hashtable) arrayList.get(i);
            String OrderID = hashList.get("ORDERID").toString();
            Hashtable hashAmt = (Hashtable) hasSumAmt.get(OrderID);

            if (hashAmt == null)
                hashAmt = new Hashtable();

            //System.out.println("hashAmt=" + hashAmt);
            String CheckResponse = "";
            String MerchantID = hashList.get("MERCHANTID").toString();
            String TerminalID = hashList.get("TERMINALID").toString();

            if (UserBean.check_Terminal_Column(MerchantID, TerminalID, arrayTerminal, "CURRENTCODE", "B,D,E,F"))
            {
                //  確認端末機狀態
                if (UserBean.check_Terminal_Column(MerchantID, TerminalID, arrayTerminal, "PERMIT_CAPTURE", "Y"))
                {
                    //  確認端末機權限
                    String Pan = hashList.get("PAN").toString();
                    // Merchant Console 線上請款作業模組  修改  by Dale Peng 20150521 --新增開始--
                	
                    //因為在數字後面的值有可能有空白 所以做trim
                    Pan = Pan.trim();
                    //確認 確認卡不可等於62XX
                    if (!Pan.equalsIgnoreCase("62XX"))
                	{
                		
                	// Merchant Console 線上請款作業模組  修改  by Dale Peng 20150521 --新增結束--
                    boolean Panflag = UserBean.check_Test_Card(arrayCardTest, Pan);

                    // System.out.println("Panflag=" + Panflag);
                    	if (Panflag)
                    	{
//                    	if (hashAmt.size() > 0) {
                    		boolean boolPartialTransFlag = false;
                    		boolean boolPartialFlag = UserBean.check_Terminal_Column(MerchantID, TerminalID, arrayTerminal, "PERMIT_PARTIAL_CAPTURE", "Y");
                    		String ReversalFlag = hashList.get("REVERSALFLAG").toString();

                    		if (ReversalFlag.equalsIgnoreCase("0"))
                    		{
                    			String DueDate = hashList.get("CAPTUREDDEALLINE").toString().replaceAll("/", "");
                    			String Today = UserBean.get_TransDate("yyyyMMdd");

                    			if (Double.parseDouble(DueDate) >= Double.parseDouble(Today))
                    			{
                    				String BalanceAmt = hashList.get("BALANCEAMT").toString(); // Billing.balanceamt
                    				String TransCode = hashList.get("TRANSCODE").toString(); //  交易代號

                    				if (TransCode.equalsIgnoreCase("00"))
                    					boolPartialTransFlag = true;

                    					String TransAmt = hashList.get("TRANSAMT").toString(); //  授權金額
                    					String ToCaptureAmt = hashList.get("TOCAPTUREAMT").toString(); // 請款金額
                    					String TransMode = hashList.get("TRANSMODE").toString(); //  交易模式
                    					boolean PartialModeFlag = false; //可分批交易模式

                    				if (TransMode.equalsIgnoreCase("0"))
                    				{
                                    // 一般
                    					PartialModeFlag = true;
                    				}

//                                	System.out.println("--------  99999999999  BalanceAmt =  " +BalanceAmt + ",ToCaptureAmt=" +ToCaptureAmt);
                    				if (Double.parseDouble(BalanceAmt) >= Double.parseDouble(ToCaptureAmt))
                    				{
                    					if ((!boolPartialFlag || PartialFlag.equalsIgnoreCase("N") || !boolPartialTransFlag || !PartialModeFlag) &&
                                        (Double.parseDouble(TransAmt) != Double.parseDouble(ToCaptureAmt)))
                    					{ // 不可分批請款
                    						CheckResponse = "無法分批請款";
                    					}
                    					else
                    					{
                    						String OrderCaptureAmt = "0";
                    						if (hashAmt.get("OrderCaptureAmt") != null)
                    						{
                    							OrderCaptureAmt = hashAmt.get("OrderCaptureAmt").toString();
                    						}

                    						String OrderRefundAmt = "0";
                    						if (hashAmt.get("OrderRefundAmt") != null)
                    						{
                    							OrderRefundAmt = hashAmt.get("OrderRefundAmt").toString();
                    						}

                    						String CaptureAmt = "0";
                    						if (hashAmt.get("CaptureAmt") != null)
                    						{
                                            CaptureAmt = hashAmt.get("CaptureAmt").toString();
                    						}

                    						String RefundAmt = "0";
                    						if (hashAmt.get("RefundAmt") != null)
                    						{
                    							RefundAmt = hashAmt.get("RefundAmt").toString();
                    						}

                    						String AuthAmt = hashList.get("AUTHAMT").toString();

//                                       	System.out.println("Sys_orderID=" +hashList.get("SYS_ORDERID").toString() +",OrderCaptureAmt=" +OrderCaptureAmt + ",CaptureAmt=" + CaptureAmt +",TransAmt=" + TransAmt + ",Total=" +(Double.parseDouble(OrderCaptureAmt) + Double.parseDouble(CaptureAmt)));
                    						if ((Double.parseDouble(OrderCaptureAmt) + Double.parseDouble(CaptureAmt)) <= Double.parseDouble(AuthAmt))
                    						{
//                                            	System.out.println( "OrderCaptureAmt=" + OrderCaptureAmt + ",CaptureAmt=" + CaptureAmt + ",OrderRefundAmt=" + OrderRefundAmt + ",RefundAmt=" + RefundAmt);
                    							if (OverRefundLimit.length() == 0)
                    							{
                    								OverRefundLimit = "0";
                    							}

                    							String RefundCaptureAmt = "0";
                    							if (hashAmt.get("RefundCaptureAmt") != null)
                    							{
                    								RefundCaptureAmt = hashAmt.get("RefundCaptureAmt").toString();
                    							}

                                            	if ((Double.parseDouble(OrderCaptureAmt) + Double.parseDouble(CaptureAmt)) > 0)
                                            	{
                                                	if ((Double.parseDouble(OrderCaptureAmt) + Double.parseDouble(CaptureAmt) + Double.parseDouble(OverRefundLimit)) >=
                                                		(Double.parseDouble(OrderRefundAmt)  + Double.parseDouble(RefundCaptureAmt)))
                                                	{
//                                                   	 System.out.println("OrderRefundAmt=" + OrderRefundAmt +",RefundAmt=" +RefundAmt);
                                                		if ((Double.parseDouble(OrderRefundAmt) + Double.parseDouble(RefundCaptureAmt)) == Double.parseDouble(RefundAmt))
                                                    	{
                                   
                                                    		flag = true;
                                                    		CheckResponse = "可請款";
                                   
                                                    	}
                                                    	else
                                                    	{
                                                        	CheckResponse = "尚有退貨交易未請款";
                                                    	}
                                                	}
                                                	else
                                                	{
                                                    	CheckResponse = "退貨總金額超過請款總金額";
                                                	}
                                            	}
                                            	else
                                            	{
                                                	CheckResponse = "請款總金額不可為0";
                                            	}
                                        	}
                                        	else
                                        	{
                                            	CheckResponse = "請款總金額超過授權金額";
                                        	}
                                    	}
                                	}
                                	else
                                	{
                                		CheckResponse = "請款金額超過授權金額";
                                	}
                            	}
                            	else
                            	{
                            		CheckResponse = "請款日期超過請款期限";
                            	}
                        	}
                        	else
                        	{
                        		CheckResponse = "交易已回沖";
                        	}
                    	}
                    	else
                    	{
                    		CheckResponse = "卡號為測試卡不可請款";
                    	}
//                    } else {
//                        CheckResponse = "查無交易授權資料";
//                        System.out.println("查無交易授權資料");
//                    }
                	}
                 // Merchant Console 線上請款作業模組  修改  by Dale Peng 20150521 --新增開始--
                    else
                	{
                    	CheckResponse = "尚有作業未完成";
                	}   
                 // Merchant Console 線上請款作業模組  修改  by Dale Peng 20150521 --新增結束--
                    
                }
                else
                {
                    CheckResponse = "端末機無此功能權限";
                }
            }
            else
            {
                CheckResponse = "端末機無提供此服務";
            }

            hashList.put("CHECKRESPONSE", CheckResponse);
            if (flag)
            {
                hashSuccess.put(String.valueOf(hashSuccess.size()), hashList);
            }
            else
            {
                hashFail.put(String.valueOf(hashFail.size()), hashList);
            }
        }

        hashCheck.put("CaptureSuccessData", hashSuccess);
        hashCheck.put("CaptureFailData", hashFail);
        return hashCheck;
    }

    /**
     * 更新可請款金額 (Billing)
     * @param  DataBaseBean SysBean     資料庫
     * @param  String MerchantID    被取消交易的特店代號
     * @param  String SubMID            被取消交易的服務代號
     * @param  String Sys_OrderID   被取消交易的系統指定單號
     * @param  String TransCode         被取消交易的交易代碼  00:購貨 01:退貨
     * @param  String BalanceAmt    可請款金額
     * @return boolean                  更新結果
     */
    public boolean update_BillingeNet(DataBaseBean2 sysBean, String MerchantID, String SubMID,
                                      String Sys_OrderID, String TransCode, String BalanceAmt)
    {
        boolean flag = false;
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
        sSQLSB.append("UPDATE BILLING SET BALANCEAMT = ?  ");
        sSQLSB.append(" WHERE MERCHANTID = ? AND SUBMID = ? AND SYS_ORDERID = ? AND TRANSCODE = ? ");
        //System.out.println("Sql=" + Sql);
        
        sysBean.AddSQLParam(emDataType.INT, BalanceAmt);
        sysBean.AddSQLParam(emDataType.STR, MerchantID);
        sysBean.AddSQLParam(emDataType.STR, SubMID);
        sysBean.AddSQLParam(emDataType.STR, Sys_OrderID);
        sysBean.AddSQLParam(emDataType.STR, TransCode);

        ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/04/27 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-014 */
        	arraySys = sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }
        
        System.out.println("arraySys.size()="+arraySys.size());
        if(arraySys.size() > 0) 
        {
        	flag = true;
        }        

        return flag;
    }

//    /**
//     * 更新可請款金額 (Billing)
//     * @param  String MerchantID    被取消交易的特店代號
//     * @param  String SubMID            被取消交易的服務代號
//     * @param  String Sys_OrderID   被取消交易的系統指定單號
//     * @param  String TransCode         被取消交易的交易代碼  00:購貨 01:退貨
//     * @param  String CaptureMessag 失敗原因
//     * @param  String CaptureAmt    當次請款金額
//     * @param  String DueDate       請款期限
//     * @param  String CaptureDate   請款日期
//     * @return boolean                  更新結果
//     */
//    public boolean update_BillingeFail(String MerchantID, String SubMID, String Sys_OrderID, String TransCode,
//                                       String CaptureMessage, String CaptureAmt, String DueDate, String CaptureDate)
//    {
//        boolean Flag = false;
//        DataBaseBean SysBean = new DataBaseBean();
//        StringBuffer Sql = new StringBuffer("UPDATE BILLING SET CAPTUREMESSAGE = '" + CaptureMessage +
//                     "', CAPTUREAMT = " + CaptureAmt + ", DUE_DATE = '" +
//                     DueDate + "' , CAPTUREDATE = '" + CaptureDate + "' ");
//        Sql.append(" WHERE MERCHANTID='" + MerchantID + "' AND SUBMID = '" +
//              SubMID + "' AND SYS_ORDERID = '" + Sys_OrderID + "' AND TRANSCODE = '" + TransCode + "'");
//
//        // System.out.println("Sql=" + Sql);
//        try
//        {
//            Flag = ((Boolean) SysBean.executeSQL(Sql.toString(), "update")).booleanValue();
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//
//        return Flag;
//    }

    /* Override update_BillingeFail with DatabaseBean parameter */
    public boolean update_BillingeFail(DataBaseBean2 sysBean, String MerchantID, String SubMID, String Sys_OrderID,
                                       String TransCode, String CaptureMessage, String CaptureAmt,
                                       String DueDate, String CaptureDate)
    {
        boolean flag = false;
        // DataBaseBean SysBean = new DataBaseBean();
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append(" UPDATE BILLING SET CAPTUREMESSAGE = ?, CAPTUREAMT = ?, DUE_DATE = ?, CAPTUREDATE = ? ");
		sysBean.AddSQLParam(emDataType.STR, CaptureMessage);
		sysBean.AddSQLParam(emDataType.INT, CaptureAmt);
		sysBean.AddSQLParam(emDataType.STR, DueDate);
		sysBean.AddSQLParam(emDataType.STR, CaptureDate);
		
		sSQLSB.append(" WHERE MERCHANTID = ? AND SUBMID = ? AND SYS_ORDERID = ? AND TRANSCODE = ? ");
		sysBean.AddSQLParam(emDataType.STR, MerchantID);
		sysBean.AddSQLParam(emDataType.STR, SubMID);
		sysBean.AddSQLParam(emDataType.STR, Sys_OrderID);
		sysBean.AddSQLParam(emDataType.STR, TransCode);

        // System.out.println("Sql=" + Sql);
		
		ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/04/26 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-019 */
        	arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }

        System.out.println("arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
        	flag = true;
        }
        
        return flag;
    }

//    /**
//     * 取得請款成功顯示資料 (Capture)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店代號
//     * @param String SysOrderID     系統指定單號
//     * @return ArrayList            列表資料
//     */
//    public ArrayList get_Capture_Show_List(String MerchantID, String SubMID, String SysOrderID)
//    {
//        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
//        DataBaseBean SysBean = new DataBaseBean();
//        if (MerchantID.length() > 0 && SubMID.length() > 0 && SysOrderID.length() > 0)
//        {
//            StringBuffer Sql = new StringBuffer("SELECT MERCHANTID,SUBMID,TERMINALID,ACQUIRERID,ORDERID,SYS_ORDERID,CARD_TYPE,PAN,EXPIREDATE,TRANSCODE,TRANSDATE,TRANSTIME,APPROVECODE,RESPONSECODE,RESPONSEMSG,CURRENCYCODE,CAPTUREAMT,CAPTUREDATE,USERDEFINE,BATCHNO,CAPTUREFLAG,PROCESSDATE,ENTRY_MODE,CONDITION_CODE,ECI,CAVV,TRANSMODE,INSTALLTYPE,INSTALL,FIRSTAMT,EACHAMT,FEE,REDEMTYPE,REDEMUSED,REDEMBALANCE,CREDITAMT,BILLMESSAGE,FEEDBACKCODE,FEEDBACKMSG,FEEDBACKDATE,TRANSAMT,TO_CHAR(DUE_DATE,'YYYY/MM/DD') DUE_DATE  FROM CAPTURE ");
//            Sql.append(" WHERE MERCHANTID = '" + MerchantID +
//                  "'  AND SUBMID = '" + SubMID + "' AND SYS_ORDERID IN ( " + SysOrderID + " ) AND CAPTUREFLAG = '0' ");
//            // System.out.println("Sql=" + Sql);
//            try
//            {
//                arrayBillingData = (ArrayList) SysBean.executeSQL(Sql.toString(), "select");
//            }
//            catch (Exception ex)
//            {
//                System.out.println(ex.getMessage());
//                log_systeminfo.debug(ex.toString());
//            }
//
//            if (arrayBillingData == null)
//            {
//                arrayBillingData = new ArrayList();
//            }
//        }
//
//        return arrayBillingData;
//    }

//    /* Override get_Capture_Show_List with DataBaseBean parameter */
//    public ArrayList get_Capture_Show_List(DataBaseBean SysBean, String MerchantID, String SubMID, String SysOrderID)
//    {
//        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
//        // DataBaseBean SysBean = new DataBaseBean();
//        if (MerchantID.length() > 0 && SubMID.length() > 0 && SysOrderID.length() > 0)
//        {
//            StringBuffer Sql = new StringBuffer("SELECT MERCHANTID,SUBMID,TERMINALID,ACQUIRERID,ORDERID,SYS_ORDERID,CARD_TYPE,PAN,EXPIREDATE,TRANSCODE,TRANSDATE,TRANSTIME,APPROVECODE,RESPONSECODE,RESPONSEMSG,CURRENCYCODE,CAPTUREAMT,CAPTUREDATE,USERDEFINE,BATCHNO,CAPTUREFLAG,PROCESSDATE,ENTRY_MODE,CONDITION_CODE,ECI,CAVV,TRANSMODE,INSTALLTYPE,INSTALL,FIRSTAMT,EACHAMT,FEE,REDEMTYPE,REDEMUSED,REDEMBALANCE,CREDITAMT,BILLMESSAGE,FEEDBACKCODE,FEEDBACKMSG,FEEDBACKDATE,TRANSAMT,TO_CHAR(DUE_DATE,'YYYY/MM/DD') DUE_DATE  FROM CAPTURE ");
//            Sql.append(" WHERE MERCHANTID = '" + MerchantID +
//                  "'  AND SUBMID = '" + SubMID + "' AND SYS_ORDERID IN ( " + SysOrderID + " ) AND CAPTUREFLAG = '0' ");
//            // System.out.println("Sql=" + Sql);
//            try
//            {
//                arrayBillingData = (ArrayList) SysBean.executeSQL(Sql.toString(), "select");
//            }
//            catch (Exception ex)
//            {
//                System.out.println(ex.getMessage());
//                log_systeminfo.debug(ex.toString());
//            }
//
//            if (arrayBillingData == null)
//            {
//                arrayBillingData = new ArrayList();
//            }
//        }
//
//        return arrayBillingData;
//    }

//    /**
//     * 取得請款失敗顯示資料 (Billing)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店代號
//     * @param String SysOrderID     系統指定單號
//     * @return ArrayList            列表資料
//     */
//    public ArrayList get_Billing_Show_List(String MerchantID, String SubMID, String SysOrderID)
//    {
//        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
//        DataBaseBean SysBean = new DataBaseBean();
//
//        if (MerchantID.length() > 0 && SubMID.length() > 0 && SysOrderID.length() > 0)
//        {
//            StringBuffer Sql = new StringBuffer("SELECT MERCHANTID,SUBMID,TERMINALID,ACQUIRERID,ORDERID,SYS_ORDERID,CARD_TYPE,PAN,EXTENNO,EXPIREDATE,TRANSCODE,REVERSALFLAG,TRANSDATE,TRANSTIME,CURRENCYCODE,TRANSAMT,APPROVECODE,RESPONSECODE,RESPONSEMSG,BATCHNO,USERDEFINE,EMAIL,MTI,RRN,SOCIALID,ENTRY_MODE,CONDITION_CODE,TRANSMODE,TRANSTYPE,ECI,CAVV,XID,INSTALLTYPE,INSTALL,FIRSTAMT,EACHAMT,FEE,REDEMTYPE,REDEMUSED,REDEMBALANCE,CREDITAMT,BILLMESSAGE,BALANCEAMT,CAPTUREMESSAGE,CAPTUREAMT,TO_CHAR(DUE_DATE,'YYYY/MM/DD') DUE_DATE,CAPTUREDATE FROM BILLING ");
//            Sql.append(" WHERE MERCHANTID = '" + MerchantID +
//                  "'  AND SUBMID = '" + SubMID + "' AND SYS_ORDERID IN ( " + SysOrderID + " ) ");
//
//            // System.out.println("Sql=" + Sql);
//            try
//            {
//                arrayBillingData = (ArrayList) SysBean.executeSQL(Sql.toString(), "select");
//            }
//            catch (Exception ex)
//            {
//                System.out.println(ex.getMessage());
//                log_systeminfo.debug(ex.toString());
//            }
//
//            if (arrayBillingData == null)
//            {
//                arrayBillingData = new ArrayList();
//            }
//        }
//
//        return arrayBillingData;
//    }

//    /* Override get_Billing_Show_List with DataBaseBean parameter */
//    public ArrayList get_Billing_Show_List(DataBaseBean SysBean, String MerchantID, String SubMID, String SysOrderID)
//    {
//        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
//        // DataBaseBean SysBean = new DataBaseBean();
//
//        if (MerchantID.length() > 0 && SubMID.length() > 0 && SysOrderID.length() > 0)
//        {
//            StringBuffer Sql = new StringBuffer("SELECT MERCHANTID,SUBMID,TERMINALID,ACQUIRERID,ORDERID,SYS_ORDERID,CARD_TYPE,PAN,EXTENNO,EXPIREDATE,TRANSCODE,REVERSALFLAG,TRANSDATE,TRANSTIME,CURRENCYCODE,TRANSAMT,APPROVECODE,RESPONSECODE,RESPONSEMSG,BATCHNO,USERDEFINE,EMAIL,MTI,RRN,SOCIALID,ENTRY_MODE,CONDITION_CODE,TRANSMODE,TRANSTYPE,ECI,CAVV,XID,INSTALLTYPE,INSTALL,FIRSTAMT,EACHAMT,FEE,REDEMTYPE,REDEMUSED,REDEMBALANCE,CREDITAMT,BILLMESSAGE,BALANCEAMT,CAPTUREMESSAGE,CAPTUREAMT,TO_CHAR(DUE_DATE,'YYYY/MM/DD') DUE_DATE,CAPTUREDATE FROM BILLING ");
//            Sql.append(" WHERE MERCHANTID = '" + MerchantID +
//                  "'  AND SUBMID = '" + SubMID + "' AND SYS_ORDERID IN ( " + SysOrderID + " ) ");
//
//            // System.out.println("Sql=" + Sql);
//            try
//            {
//                arrayBillingData = (ArrayList) SysBean.executeSQL(Sql.toString(), "select");
//            }
//            catch (Exception ex)
//            {
//                System.out.println(ex.getMessage());
//                log_systeminfo.debug(ex.toString());
//            }
//
//            if (arrayBillingData == null)
//            {
//                arrayBillingData = new ArrayList();
//            }
//        }
//
//        return arrayBillingData;
//    }

//    /**
//     * 取得待請款資料(Billing)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店代號
//     * @param String OrderID        特店指定單號
//     * @param String TerminalID     查詢端末機
//     * @param String TransCode      交易類別 (ALL:全部 00:一般交易 01:退貨交易)
//     * @param String OrderID        訂單代號
//     * @param String ApproveCode    授權碼
//     * @param String CaptureDay     請款期限
//     * @param String Amt            請款金額
//     * @return Hashtable            列表資料
//     */
//    public Hashtable get_BatchCapture(String MerchantID, String SubMID, String OrderID, String TerminalID,
//                                      String TransCode, String TransDate, String TransTime, String ApproveCode,
//                                      String Amt, String CaptureDay)
//    {
//        Hashtable hashBillingData = new Hashtable();
//        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
//        DataBaseBean SysBean = new DataBaseBean();
//
//        if (MerchantID.length() > 0 && SubMID.length() > 0 && OrderID.length() > 0 && TerminalID.length() > 0 &&
//            TransCode.length() > 0 && TransDate.length() > 0 && TransTime.length() > 0 && ApproveCode.length() > 0)
//        {
//            StringBuffer Sql = new StringBuffer("SELECT A.*, ROWIDTOCHAR(A.ROWID) AS RROID , TO_CHAR(TO_DATE( TRANSDATE,'YYYYMMDD') + " +
//                         CaptureDay + ", 'YYYY/MM/DD') CAPTUREDDEALLINE FROM (SELECT * FROM BILLING WHERE  ");
//            Sql.append(" MERCHANTID = '" + MerchantID + "' ");
//            Sql.append(" AND SUBMID = '" + SubMID + "' ");
//            Sql.append(" AND TERMINALID = '" + TerminalID + "'");
//            Sql.append(" AND TRANSCODE = '" + TransCode + "'");
//            Sql.append(" AND TRANSDATE = '" + TransDate + "'");
//            Sql.append(" AND TRANSTIME = '" + TransTime + "'");
//            Sql.append(" AND ORDERID = '" + OrderID + "' ");
//            Sql.append(" AND TRIM(APPROVECODE) = '" + ApproveCode + "' ");
//
//            if (TransCode.equalsIgnoreCase("01") && Amt.length() > 0)
//            {
//                Sql.append(" AND BALANCEAMT = " + Amt + " ");
//            }
//
//            Sql.append(" ORDER BY TERMINALID, SYS_ORDERID, TRANSCODE, TRANSDATE, TRANSTIME) A ");
//            //System.out.println("Sql=" + Sql);
//
//            try
//            {
//                arrayBillingData = (ArrayList) SysBean.executeSQL(Sql.toString(), "select");
//            }
//            catch (Exception ex)
//            {
//                System.out.println(ex.getMessage());
//                log_systeminfo.debug(ex.toString());
//            }
//
//            if (arrayBillingData == null)
//            {
//                arrayBillingData = new ArrayList();
//            }
//        }
//
//        if (arrayBillingData.size() > 0)
//        {
//            hashBillingData = (Hashtable) arrayBillingData.get(0);
//        }
//
//        return hashBillingData;
//    }

    /* Override get_BatchCapture with DatabaseBean Parameter */
    public Hashtable get_BatchCapture(DataBaseBean2 sysBean, String MerchantID, String SubMID,
                                      String OrderID, String TerminalID, String TransCode, String TransDate,
                                      String TransTime, String ApproveCode, String Amt, String CaptureDay)
    {
        Hashtable hashBillingData = new Hashtable();
        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
        // DataBaseBean SysBean = new DataBaseBean();

        if (MerchantID.length() > 0 && SubMID.length() > 0 && OrderID.length() > 0 && TerminalID.length() > 0 &&
            TransCode.length() > 0 && TransDate.length() > 0 && TransTime.length() > 0 && ApproveCode.length() > 0)
        {
        	StringBuffer sSQLSB = new StringBuffer();
            sysBean.ClearSQLParam();
            sSQLSB.append("SELECT A.*, ROWIDTOCHAR(A.ROWID) AS RROID , TO_CHAR(TO_DATE( TRANSDATE,'YYYYMMDD') + " +
                    CaptureDay + ", 'YYYY/MM/DD') CAPTUREDDEALLINE FROM (SELECT * FROM BILLING WHERE  ");
    		sSQLSB.append(" MERCHANTID = ? ");
    		sSQLSB.append(" AND SUBMID = ? ");
    		sSQLSB.append(" AND TERMINALID = ? ");
    		sSQLSB.append(" AND TRANSCODE = ? ");
    		sSQLSB.append(" AND TRANSDATE = ? ");
    		sSQLSB.append(" AND TRANSTIME = ? ");
    		sSQLSB.append(" AND ORDERID = ? ");
    		sSQLSB.append(" AND TRIM(APPROVECODE) = ? ");
    		
    		sysBean.AddSQLParam(emDataType.STR, MerchantID);
    		sysBean.AddSQLParam(emDataType.STR, SubMID);
    		sysBean.AddSQLParam(emDataType.STR, TerminalID);
    		sysBean.AddSQLParam(emDataType.STR, TransCode);
    		sysBean.AddSQLParam(emDataType.STR, TransDate);
    		sysBean.AddSQLParam(emDataType.STR, TransTime);
    		sysBean.AddSQLParam(emDataType.STR, OrderID);
    		sysBean.AddSQLParam(emDataType.STR, ApproveCode);

            if (TransCode.equalsIgnoreCase("01") && Amt.length() > 0)
            {
            	sSQLSB.append(" AND BALANCEAMT = ? ");
            	sysBean.AddSQLParam(emDataType.INT, Amt);
            }

            sSQLSB.append(" ORDER BY TERMINALID, SYS_ORDERID, TRANSCODE, TRANSDATE, TRANSTIME) A ");
            //System.out.println("Sql=" + Sql);

            try
            {
            	/** 2023/05/17 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-027 (No Need Test) */
                arrayBillingData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
                log_systeminfo.debug(ex.toString());
            }

            if (arrayBillingData == null)
            {
                arrayBillingData = new ArrayList();
            }
        }

        if (arrayBillingData.size() > 0)
        {
            hashBillingData = (Hashtable) arrayBillingData.get(0);
        }

        return hashBillingData;
    }


//    /**
//     * 取得檔案交易資料(Batch)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店代號
//     * @param String BatchPmtID     檔案序號
//     * @param String CaptureDay     請款天數
//     * @param String BatchTxMsg     檔案處理狀態
//     * @param String OrderBy        排序方式
//     * @return ArrayList            帳單交易
//     */
//    public ArrayList get_BatchBalance(String MerchantID, String SubMID, String BatchPmtID, String CaptureDay,
//                                      String BatchTxMsg, String OrderBy)
//    {
//        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
//        DataBaseBean SysBean = new DataBaseBean();
//
//        if (MerchantID.length() > 0 && SubMID.length() > 0 && BatchPmtID.length() > 0 && CaptureDay.length() > 0)
//        {
//            StringBuffer Sql = new StringBuffer("SELECT ROWIDTOCHAR(A.ROWID) AS RROWID, A.BATCHTXAMT AS TOCAPTUREAMT, A.MERCHANTID, A.SUBMID, A.TERMINALID, A.ACQUIRERID, A.ORDERID, A.SYS_ORDERID, A.CARD_TYPE, A.PAN, A.EXTENNO, A.EXPIREDATE, A.TRANSCODE, A.REVERSALFLAG, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.APPROVECODE, A.RESPONSECODE, A.RESPONSEMSG, A.BATCHNO, A.USERDEFINE, A.EMAIL, A.MTI, A.RRN, A.SOCIALID, A.ENTRY_MODE, A.CONDITION_CODE, A.TRANSMODE, A.TRANSTYPE, A.ECI, A.CAVV, A.XID, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE, A.BALANCEAMT, A.CAPTUREDATE, A.FEEDBACKCODE, A.FEEDBACKMSG, A.FEEDBACKDATE, TO_CHAR(A.DUE_DATE,'YYYY/MM/DD') DUE_DATE, A.BATCHPMTID, A.BATCHDATE, A.BATCHHEAD, A.BATCHTYPE, A.BATCHTERMINALID, A.BATCHSYSORDERID, A.BATCHTXDATE, A.BATCHTXTIME, A.BATCHTXAPPROVECODE, A.BATCHTRANSCODE, A.BATCHTXAMT, A.BATCHTXMSG, A.BATCHRESPONSE, A.SYSTRACENO, B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT , NVL(B.REFUNDCAPTUREAMT,0) REFUNDCAPTUREAMT , TO_CHAR(TO_DATE( TRANSDATE,'YYYYMMDD') + " + CaptureDay);
//            Sql.append(" , 'YYYY/MM/DD') CAPTUREDDEALLINE FROM (SELECT * FROM BATCH ");
//            Sql.append(" WHERE  MERCHANTID = '" + MerchantID + "'  ");
//            Sql.append(" AND SUBMID = '" + SubMID + "' ");
//            Sql.append(" AND BATCHPMTID = '" + BatchPmtID + "' ");
//
//            if (BatchTxMsg.length() > 0 && !BatchTxMsg.equalsIgnoreCase("ALL"))
//            {
//                Sql.append(" AND BATCHTXMSG = '" + BatchTxMsg + "' ");
//            }
//
//            Sql.append(" ) A , BALANCE B WHERE A.MERCHANTID = B.MERCHANTID AND A.SUBMID = B.SUBMID AND A.ORDERID = B.ORDERID ");
//            // System.out.println("Sql=" + Sql);
//
//            try
//            {
//                arrayBillingData = (ArrayList) SysBean.executeSQL(Sql.toString(), "select");
//            }
//            catch (Exception ex)
//            {
//                System.out.println(ex.getMessage());
//                log_systeminfo.debug(ex.toString());
//            }
//
//            if (arrayBillingData == null)
//            {
//                arrayBillingData = new ArrayList();
//            }
//        }
//        return arrayBillingData;
//    }

    /* Override get_BatchBalance with DatabaseBean parameter */
    public ArrayList get_BatchBalance(DataBaseBean2 sysBean, String MerchantID, String SubMID,
                                      String BatchPmtID, String CaptureDay, String BatchTxMsg, String OrderBy)
    {
        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
        // DataBaseBean SysBean = new DataBaseBean();
        if (MerchantID.length() > 0 && SubMID.length() > 0 && BatchPmtID.length() > 0 && CaptureDay.length() > 0)
        {
        	StringBuffer sSQLSB = new StringBuffer();
            sysBean.ClearSQLParam();
    		sSQLSB.append("SELECT ROWIDTOCHAR(A.ROWID) AS RROWID, A.BATCHTXAMT AS TOCAPTUREAMT, A.MERCHANTID, A.SUBMID, A.TERMINALID, A.ACQUIRERID, A.ORDERID, A.SYS_ORDERID, A.CARD_TYPE, A.PAN, A.EXTENNO, A.EXPIREDATE, A.TRANSCODE, A.REVERSALFLAG, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.APPROVECODE, A.RESPONSECODE, A.RESPONSEMSG, A.BATCHNO, A.USERDEFINE, A.EMAIL, A.MTI, A.RRN, A.SOCIALID, A.ENTRY_MODE, A.CONDITION_CODE, A.TRANSMODE, A.TRANSTYPE, A.ECI, A.CAVV, A.XID, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE, A.BALANCEAMT, A.CAPTUREDATE, A.FEEDBACKCODE, A.FEEDBACKMSG, A.FEEDBACKDATE, TO_CHAR(A.DUE_DATE,'YYYY/MM/DD') DUE_DATE, A.BATCHPMTID, A.BATCHDATE, A.BATCHHEAD, A.BATCHTYPE, A.BATCHTERMINALID, A.BATCHSYSORDERID, A.BATCHTXDATE, A.BATCHTXTIME, A.BATCHTXAPPROVECODE, A.BATCHTRANSCODE, A.BATCHTXAMT, A.BATCHTXMSG, A.BATCHRESPONSE, A.SYSTRACENO, B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT , NVL(B.REFUNDCAPTUREAMT,0) REFUNDCAPTUREAMT , TO_CHAR(TO_DATE( TRANSDATE,'YYYYMMDD') + " + CaptureDay);
            sSQLSB.append(" , 'YYYY/MM/DD') CAPTUREDDEALLINE FROM (SELECT * FROM BATCH ");
            sSQLSB.append(" WHERE  MERCHANTID = ?  ");
            sSQLSB.append(" AND SUBMID = ? ");
            sSQLSB.append(" AND BATCHPMTID = ? ");
            
            sysBean.AddSQLParam(emDataType.STR, MerchantID);
            sysBean.AddSQLParam(emDataType.STR, SubMID);
            sysBean.AddSQLParam(emDataType.STR, BatchPmtID);

            if (BatchTxMsg.length() > 0 && !BatchTxMsg.equalsIgnoreCase("ALL"))
            {
                sSQLSB.append(" AND BATCHTXMSG = ? ");
                sysBean.AddSQLParam(emDataType.STR, BatchTxMsg);
            }

            sSQLSB.append(" ) A , BALANCE B WHERE A.MERCHANTID = B.MERCHANTID AND A.SUBMID = B.SUBMID AND A.ORDERID = B.ORDERID ");
            // System.out.println("Sql=" + Sql);

            try
            {
            	/** 2023/05/17 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-027 (No Need Test) */
                arrayBillingData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
                log_systeminfo.debug(ex.toString());
            }

            if (arrayBillingData == null)
            {
                arrayBillingData = new ArrayList();
            }
        }
        return arrayBillingData;
    }

//    /**
//     * 取得檔案交易資料(Batch)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店代號
//     * @param String SysOrderID     系統指定單號
//     * @param String TransCode      交易代號
//     * @return String               帳單餘額
//     */
//    public String get_BillingBalance(String MerchantID, String SubMID, String SysOrderID, String TransCode)
//    {
//        String BalanceAmt = "";
//        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
//        DataBaseBean SysBean = new DataBaseBean();
//
//        if (MerchantID.length() > 0 && SubMID.length() > 0 && SysOrderID.length() > 0 && TransCode.length() > 0)
//        {
//            StringBuffer Sql = new StringBuffer("SELECT BALANCEAMT FROM BILLING ");
//            Sql.append(" WHERE  MERCHANTID = '" + MerchantID + "'  ");
//            Sql.append(" AND SUBMID = '" + SubMID + "' ");
//            Sql.append(" AND SYS_ORDERID = '" + SysOrderID + "' ");
//            Sql.append(" AND TRANSCODE = '" + TransCode + "' ");
//            // System.out.println("Sql=" + Sql);
//
//            try
//            {
//                arrayBillingData = (ArrayList) SysBean.executeSQL(Sql.toString(), "select");
//            }
//            catch (Exception ex)
//            {
//                System.out.println(ex.getMessage());
//                log_systeminfo.debug(ex.toString());
//            }
//
//            if (arrayBillingData == null)
//            {
//                arrayBillingData = new ArrayList();
//            }
//        }
//
//        if (arrayBillingData.size() > 0)
//        {
//            Hashtable hashFata = (Hashtable) arrayBillingData.get(0);
//            if (hashFata != null && hashFata.size() > 0)
//            {
//                BalanceAmt = (String) hashFata.get("BALANCEAMT");
//            }
//        }
//
//        return BalanceAmt;
//    }

    /* Override get_BillingBalance with DatabaseBean parameter */
    public String get_BillingBalance(DataBaseBean2 sysBean, String MerchantID, String SubMID, String SysOrderID, String TransCode)
    {
        String BalanceAmt = "";
        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
        // DataBaseBean SysBean = new DataBaseBean();

        if (MerchantID.length() > 0 && SubMID.length() > 0 && SysOrderID.length() > 0 && TransCode.length() > 0)
        {
        	StringBuffer sSQLSB = new StringBuffer();
        	sysBean.ClearSQLParam();
        	sSQLSB.append("SELECT BALANCEAMT FROM BILLING ");
            sSQLSB.append(" WHERE  MERCHANTID = ?  ");
            sSQLSB.append(" AND SUBMID = ? ");
            sSQLSB.append(" AND SYS_ORDERID = ? ");
            sSQLSB.append(" AND TRANSCODE = ? ");
            
            sysBean.AddSQLParam(emDataType.STR, MerchantID);
            sysBean.AddSQLParam(emDataType.STR, SubMID);
            sysBean.AddSQLParam(emDataType.STR, SysOrderID);
            sysBean.AddSQLParam(emDataType.STR, TransCode);
            
            // System.out.println("sSQLSB=" + sSQLSB.toString());

            try
            {
            	/** 2023/05/17 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-027 (No Need Test) */
                arrayBillingData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
                log_systeminfo.debug(ex.toString());
            }

            if (arrayBillingData == null)
            {
                arrayBillingData = new ArrayList();
            }
        }

        if (arrayBillingData.size() > 0)
        {
            Hashtable hashFata = (Hashtable) arrayBillingData.get(0);
            if (hashFata != null && hashFata.size() > 0)
            {
                BalanceAmt = (String) hashFata.get("BALANCEAMT");
            }
        }

        return BalanceAmt;
    }

    /**
     * 收單行-再請款交易 檢核請款資料並進行彙總 (請款第一步check)
     * @param  ArrayList  arrayList       請款資料
     * @param  ArrayList  arrayCardTest   測試卡主檔
     * @return Hashtable  hashCheckData   判斷結果
     */
    public Hashtable check_Capture_First_ACQ(ArrayList arrayList, ArrayList arrayCardTest)
    {
        ArrayList arraySuccess = new ArrayList();
        ArrayList arrayFail = new ArrayList();
        Hashtable hashSum = new Hashtable();
        Hashtable hashCheckData = new Hashtable();
        UserBean UserBean = new UserBean();

        for (int i = 0; i < arrayList.size(); ++i)
        {
            boolean boolCheckFlag = false;
            String CheckResponse = "無法請款";
            Hashtable hashList = (Hashtable) arrayList.get(i);
            String Pan = hashList.get("PAN").toString();
            boolean Panflag = UserBean.check_Test_Card(arrayCardTest, Pan);
            // System.out.println("Panflag=" + Panflag);

            if (Panflag)
            {
                String CurrentCode = hashList.get("CURRENTCODE").toString();
                if (CurrentCode.equalsIgnoreCase("B") || CurrentCode.equalsIgnoreCase("D"))
                {
                    // 特店狀態
                    String PermitCapture = hashList.get("PERMIT_CAPTURE").toString();
                    String CaptureManual = hashList.get("CAPTURE_MANUAL").toString();
                    if (PermitCapture.equalsIgnoreCase("Y") && CaptureManual.equalsIgnoreCase("Y"))
                    {
                        String TCurrentCode = hashList.get("T_CURRENTCODE").toString();
                        if (TCurrentCode.equalsIgnoreCase("B") || TCurrentCode.equalsIgnoreCase("D"))
                        {
                            // 端末機狀態
                            String TPermitCapture = hashList.get("T_PERMIT_CAPTURE").toString();
                            if (TPermitCapture.equalsIgnoreCase("Y"))
                            {
                                String BalanceAmt = hashList.get("BALANCEAMT").toString(); // Billing.balanceamt
                                String ToCaptureAmt = hashList.get("TOCAPTUREAMT").toString(); // 請款金額
                                if (Double.parseDouble(BalanceAmt) >= Double.parseDouble(ToCaptureAmt) && Double.parseDouble(ToCaptureAmt) > 0)
                                {
                                    boolCheckFlag = true;
                                }
                            }
                            else
                            {
                                CheckResponse = "端末機無請款權限";
                            }
                        }
                        else
                        {
                            if (TCurrentCode.equalsIgnoreCase("A"))
                            {
                                CheckResponse = "端末機狀態-待啟用";
                            }

                            if (TCurrentCode.equalsIgnoreCase("C"))
                            {
                                CheckResponse = "端末機狀態-Block請款";
                            }

                            if (TCurrentCode.equalsIgnoreCase("E"))
                            {
                                CheckResponse = "端末機狀態-停用";
                            }

                            if (TCurrentCode.equalsIgnoreCase("F"))
                            {
                                CheckResponse = "端末機狀態-商店解約";
                            }
                        }
                    }
                    else
                    {
                        CheckResponse = "特店無請款權限";
                    }
                }
                else
                {
                    if (CurrentCode.equalsIgnoreCase("A"))
                    {
                        CheckResponse = "特店狀態-待啟用";
                    }

                    if (CurrentCode.equalsIgnoreCase("C"))
                    {
                        CheckResponse = "特店狀態-Block請款";
                    }

                    if (CurrentCode.equalsIgnoreCase("E"))
                    {
                        CheckResponse = "特店狀態-停用";
                    }

                    if (CurrentCode.equalsIgnoreCase("F"))
                    {
                        CheckResponse = "特店狀態-商店解約";
                    }
                }
            }
            else
            {
                CheckResponse = "卡號為測試卡無法請款";
            }

            hashList.put("CHECKRESPONSE", CheckResponse);
            if (boolCheckFlag)
            {
                arraySuccess.add(hashList);
            }
            else
            {
                arrayFail.add(hashList);
            }
        }

        for (int i = 0; i < arraySuccess.size(); ++i)
        {
            Hashtable hashList = (Hashtable) arraySuccess.get(i);
            String OrderID = hashList.get("ORDERID").toString();
            Hashtable hashtemp = (Hashtable) hashSum.get(OrderID);

            if (hashtemp == null)
                hashtemp = new Hashtable();

            String OrderCaptureAmt = "0";
            String OrderRefundAmt = "0";

            if (hashtemp.size() == 0)
            {
                String AuthAmt = hashList.get("AUTHAMT").toString();
                String RefundAmt = hashList.get("REFUNDAMT").toString();
                String CaptureAmt = hashList.get("CAPTUREAMT").toString();
                String RefundCaptureAmt = hashList.get("REFUNDCAPTUREAMT").toString();
                hashtemp.put("AuthAmt", AuthAmt);
                hashtemp.put("RefundAmt", RefundAmt);
                hashtemp.put("CaptureAmt", CaptureAmt);
                hashtemp.put("RefundCaptureAmt", RefundCaptureAmt);
            }
            else
            {
                String TmpCaptureAmt = hashtemp.get("OrderCaptureAmt").toString();
                String TmpRefundAmt = hashtemp.get("OrderRefundAmt").toString();

                if (TmpCaptureAmt.length() > 0)
                {
                    OrderCaptureAmt = TmpCaptureAmt;
                }

                if (TmpRefundAmt.length() > 0)
                {
                    OrderRefundAmt = TmpRefundAmt;
                }
            }

            String ToCaptureAmt = hashList.get("TOCAPTUREAMT").toString();
            String TransCode = hashList.get("TRANSCODE").toString();
            if (TransCode.equalsIgnoreCase("00"))
            {
                // 購貨
                OrderCaptureAmt = String.valueOf(Double.parseDouble(OrderCaptureAmt) + Double.parseDouble(ToCaptureAmt));
            }

            if (TransCode.equalsIgnoreCase("01"))
            {
                // 退貨
                OrderRefundAmt = String.valueOf(Double.parseDouble(OrderRefundAmt) + Double.parseDouble(ToCaptureAmt));
            }

            hashtemp.put("OrderCaptureAmt", OrderCaptureAmt);
            hashtemp.put("OrderRefundAmt", OrderRefundAmt);
            hashSum.put(OrderID, hashtemp);
        }

        hashCheckData.put("SuccessData", arraySuccess);
        hashCheckData.put("FailData", arrayFail);
        hashCheckData.put("SumData", hashSum);

        return hashCheckData;
    }

//    public boolean CheckCapturedFlag(DataBaseBean SysBean, String MerchantID, String SubMID, String OrderID)
//    {
//       boolean capturedFlag = false;
//       ArrayList al = null;
//       StringBuffer Sql = new StringBuffer("SELECT captureamt FROM CAPTURE ");
//       Sql.append(" WHERE MERCHANTID = '" + MerchantID + "' ");
//       Sql.append(" AND SUBMID = '" + SubMID + "' ");
//       Sql.append(" AND SYS_ORDERID = '" + OrderID + "' ");
//       try
//       {
//    	   al =  (ArrayList)SysBean.executeSQL(Sql.toString(), "select");
//    	   if(al.size() > 0){
//    		   capturedFlag =  true;
//    	   }
//       }
//       catch (Exception ex)
//       {
//    	   System.out.println(ex.getMessage());
//    	   log_systeminfo.debug(ex.toString());    	  
//       }
//	   return capturedFlag;
//    }
    
    public ArrayList SumCaptured(DataBaseBean2 sysBean, String MerchantID, String SubMID, String OrderID)
    {       
       ArrayList result = null;
       StringBuffer sSQLSB = new StringBuffer();
       sysBean.ClearSQLParam();
       sSQLSB.append(" select sum(captureamt) TCAMT from capture ");
       sSQLSB.append(" WHERE MERCHANTID = ? ");
       sSQLSB.append(" AND SUBMID = ? ");
       sSQLSB.append(" AND SYS_ORDERID = ? ");
       sSQLSB.append(" AND feedbackcode <> 'CAN' ");
       sysBean.AddSQLParam(emDataType.STR, MerchantID);
       sysBean.AddSQLParam(emDataType.STR, SubMID);
       sysBean.AddSQLParam(emDataType.STR, OrderID);
       
       try
       {
    	   /** 2023/04/28 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-017 */
    	   result =  (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
//    	   Hashtable hashtemp = new Hashtable();    	       		   
//    	   hashtemp = (Hashtable)al.get(0);    		   
       }
       catch (Exception ex)
       {
    	   System.out.println(ex.getMessage());
    	   log_systeminfo.debug(ex.toString());    	  
       }
	   return result;
    }
    /**
     * 收單行-再請款交易 檢核請款與授權交易記錄(請款第二步check)
     * @param  ArrayList  hashCheckData   請款相關資料(SuccessData:可請款資料, FailData:請款失敗資料, SumData:彙總資料)
     * @param  String     PartialFlag     分批請款註記
     * @return Hashtable  成功失敗資料
     */
    public Hashtable check_Capture_Second_ACQ(Hashtable hashCheckData)
    {
        Hashtable hashCheck = new Hashtable();
        ArrayList arraySuccess = new ArrayList();
        ArrayList arrayFail = (ArrayList)hashCheckData.get("FailData");
        ArrayList arrayList = (ArrayList) hashCheckData.get("SuccessData");
        Hashtable hasSumAmt = (Hashtable) hashCheckData.get("SumData");

        for (int i = 0; i < arrayList.size(); ++i)
        {
            boolean flag = false;
            Hashtable hashList = (Hashtable) arrayList.get(i);
            String OrderID = hashList.get("ORDERID").toString();
            Hashtable hashAmt = (Hashtable) hasSumAmt.get(OrderID);

            if (hashAmt == null)
            {
                hashAmt = new Hashtable();
            }
            //System.out.println("hashAmt=" + hashAmt);

            String CheckResponse = "";
            String ReversalFlag = hashList.get("REVERSALFLAG").toString();
            if (ReversalFlag.equalsIgnoreCase("0"))
            {
                String BalanceAmt = hashList.get("BALANCEAMT").toString(); // Billing.balanceamt
                String TransCode = hashList.get("TRANSCODE").toString(); //  交易代號
                String TransMode = hashList.get("TRANSMODE").toString(); //  交易模式
                String OverRefundLimit = (String)hashList.get("OVER_REFUND_LIMIT");  // 特店超額退貨
                String TransAmt = hashList.get("TRANSAMT").toString(); //  授權金額
                String ToCaptureAmt = hashList.get("TOCAPTUREAMT").toString(); // 請款金額
                String PartialFlag = (String)hashList.get("PERMIT_PARTIAL_CAPTURE");  // 特店分批請款狀態
                String TPartialFlag = (String)hashList.get("T_PERMIT_PARTIAL_CAPTURE"); // 端末機分批請款狀態
                boolean boolPartialFlag = false;

                if (TransCode.equalsIgnoreCase("00") && TransMode.equalsIgnoreCase("0") && PartialFlag.equalsIgnoreCase("Y") && TPartialFlag.equalsIgnoreCase("Y"))
                {
                    boolPartialFlag = true;
                }

                if (Double.parseDouble(BalanceAmt) >= Double.parseDouble(ToCaptureAmt))
                {
                    if (!boolPartialFlag && (Double.parseDouble(TransAmt) != Double.parseDouble(ToCaptureAmt)))
                    {
                        // 不可分批請款
                        CheckResponse = "無法分批請款";
                    }
                    else
                    {
                        String OrderCaptureAmt = "0";
                        String OrderRefundAmt = "0";
                        String CaptureAmt = "0";
                        String RefundAmt = "0";
                        String AuthAmt = hashList.get("AUTHAMT").toString();

                        if (hashAmt.get("OrderCaptureAmt") != null)
                        {
                            OrderCaptureAmt = hashAmt.get("OrderCaptureAmt").toString();
                        }

                        if (hashAmt.get("OrderRefundAmt") != null)
                        {
                            OrderRefundAmt = hashAmt.get("OrderRefundAmt").toString();
                        }

                        if (hashAmt.get("CaptureAmt") != null)
                        {
                            CaptureAmt = hashAmt.get("CaptureAmt").toString();
                        }

                        if (hashAmt.get("RefundAmt") != null)
                        {
                            RefundAmt = hashAmt.get("RefundAmt").toString();
                        }

                        if ((Double.parseDouble(OrderCaptureAmt) + Double.parseDouble(CaptureAmt)) <=  Double.parseDouble(AuthAmt))
                        {
                            if (OverRefundLimit.length() == 0)
                            {
                                OverRefundLimit = "0";
                            }

                            String RefundCaptureAmt = "0";
                            if (hashAmt.get("RefundCaptureAmt") != null)
                            {
                                RefundCaptureAmt = hashAmt.get("RefundCaptureAmt").toString();
                            }

                            if ((Double.parseDouble(OrderCaptureAmt) + Double.parseDouble(CaptureAmt)) > 0)
                            {
                                if ((Double.parseDouble(OrderCaptureAmt) + Double.parseDouble(CaptureAmt) + Double.parseDouble(OverRefundLimit)) >=
                                    (Double.parseDouble(OrderRefundAmt) + Double.parseDouble(RefundCaptureAmt)))
                                {
                                    if ((Double.parseDouble(OrderRefundAmt) + Double.parseDouble(RefundCaptureAmt)) == Double.parseDouble(RefundAmt))
                                    {
                                        flag = true;
                                        CheckResponse = "可請款";
                                    }
                                    else
                                    {
                                        CheckResponse = "尚有退貨交易未請款";
                                    }
                                }
                                else
                                {
                                    CheckResponse = "退貨總金額超過請款總金額";
                                }
                            }
                            else
                            {
                                CheckResponse = "請款總金額不可為0";
                            }
                        }
                        else
                        {
                            CheckResponse = "請款總金額超過授權金額";
                        }
                    }
                }
                else
                {
                    CheckResponse = "請款金額超過授權金額";
                }
            }
            else
            {
                CheckResponse = "交易已回沖";
            }

            hashList.put("CHECKRESPONSE", CheckResponse);
            if (flag)
            {
                arraySuccess.add(hashList);
            }
            else
            {
                arrayFail.add(hashList);
            }
        }

        hashCheck.put("SuccessData", arraySuccess);
        hashCheck.put("FailData", arrayFail);

        return hashCheck;
    }
    //Tag:20200708-01
    /***
     * UI畫面整批請款作業，查詢 購貨請款總筆數、總金額，退貨請款總筆數、總金額
     * BILLING資料挑選處理
     * 特店代號,UI選定的TRANSDATE區間
     * BALANCEAMT > 0 AND REVERSALFLAG = '0' AND 卡號不等於'62XX'
     * 本日(請款日) 小於等於 請款期限TRANSDATE+CaptureData
     * 卡號不可為測試卡:NOT IN TEST_CARD WHERE PAN_BEGIN  PAN_END FLAG_CAPTURE = 'N' AND FLAG_ACTIVATE = 'Y'
     * 端末機狀態CURRENTCODE IN "B,D,E,F" AND PERMIT_CAPTURE=="Y"
     * 母店人員可請做子特店，子特店人員只能做自己的
     *  
     * */
    public ArrayList<Hashtable<String,String>> get_BILLING_Batch_SumAmt(String MerchantID,String SUBMID_UI,String StartDate,String EndDate,String TransCode,int CaptureDay)
      throws Exception {
    	String sMessage;
		StringBuffer sSQLSB = new StringBuffer();
		DataBaseBean2 DBBean = new DataBaseBean2();
		ArrayList<Hashtable<String,String>> arrayBillingData = null; // 顯示交易資料;
		DBBean.ClearSQLParam();
		sSQLSB.append("SELECT TRANSCODE,COUNT(1) AS CNT,SUM(BALANCEAMT) AS TOTAL_AMT ");
		sSQLSB.append(" FROM BILLING B WHERE MERCHANTID = ? AND (TRANSDATE BETWEEN ? AND ?)");
		sSQLSB.append(" AND BALANCEAMT > 0 AND REVERSALFLAG = '0' AND TRIM(PAN) <> '62XX'");
        //--本日(請款日) 小於等於 請款期限
		sSQLSB.append(" AND TO_CHAR(SYSDATE,'yyyyMMdd') <= TO_CHAR(TO_DATE( TRANSDATE,'YYYYMMDD') + ?, 'YYYYMMDD')");
		//--卡號不可為測試卡
		sSQLSB.append(" AND (SELECT COUNT(1) FROM TEST_CARD WHERE (PAN BETWEEN PAN_BEGIN AND PAN_END) AND FLAG_CAPTURE = 'N' AND FLAG_ACTIVATE = 'Y') = 0");
		//--端末機狀態
		sSQLSB.append(" AND (SELECT COUNT(1) FROM TERMINAL T WHERE T.MERCHANTID	= B.MERCHANTID AND T.TERMINALID =B.TERMINALID AND CURRENTCODE IN ('B','D','E','F') AND PERMIT_CAPTURE='Y') > 0 ");
		DBBean.AddSQLParam(emDataType.STR, MerchantID);
		DBBean.AddSQLParam(emDataType.STR, StartDate);
		DBBean.AddSQLParam(emDataType.STR, EndDate);
		DBBean.AddSQLParam(emDataType.INT, CaptureDay);
    	//--母店人員可請做子特店，子特店人員只能做自己的
    	if(SUBMID_UI.equals("ALL") == false) {
    		sSQLSB.append(" AND SUBMID = ?");
    		DBBean.AddSQLParam(emDataType.STR, SUBMID_UI);
    	}
    	if(TransCode.equals("ALL")==false) {
    		sSQLSB.append(" AND TRANSCODE = ?");
    		DBBean.AddSQLParam(emDataType.STR, TransCode);
    	}
    	sSQLSB.append(" GROUP BY TRANSCODE ORDER BY TRANSCODE");
    	System.out.println("get_BILLING_Batch_SumAmt sql="+sSQLSB);
    	arrayBillingData = DBBean.QuerySQLByParam(sSQLSB.toString());
    	if(DBBean.Message().length()>0) {
    		sMessage = DBBean.Message();
    		DBBean = null;
    		throw new Exception(sMessage);
    	}
    	DBBean = null;
    	return arrayBillingData;
    }
    public ArrayList<Hashtable<String,String>> get_FILESTATUS_Log(String MerchantID,String sStartDate,String sEndDate){
    	DataBaseBean2 DBBean =new DataBaseBean2(); DBBean.ClearSQLParam();
    	StringBuffer sSQLSB = new StringBuffer();
    	sSQLSB.append("Select * From FILESTATUS WHERE MERCHANT_ID=? AND (FILE_DATE>=? AND FILE_DATE <=?) ORDER BY FILE_DATE DESC,FILE_BATCHNO DESC");
     	DBBean.AddSQLParam(emDataType.STR, MerchantID); //1
     	DBBean.AddSQLParam(emDataType.STR, sStartDate); //2
     	DBBean.AddSQLParam(emDataType.STR, sEndDate); //2
     	ArrayList rsFileStatus = DBBean.QuerySQLByParam(sSQLSB.toString());
     	if(rsFileStatus == null) rsFileStatus = new ArrayList();
     	return rsFileStatus;
    }
    
    
}
