package com.cybersoft.merchant.bean;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import java.util.Hashtable;
import java.security.MessageDigest;
import com.cybersoft.bean.LogUtils;
import com.cybersoft.bean.emDataType;
/**
 * 202112300619-01 20220210 GARY 請款批次請款規格增修(Visa Authorization Source Code) AUTH_SRC_CODE
 */
public class MerchantVoidBean
{
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    public MerchantVoidBean()
    {
    }

    /**
     * 取得購貨及退貨取消列表資料(Billing+Balance)
     * @param String MerchantID     特店代號
     * @param String SubMID         特店代號
     * @param String OrderType      查詢訂單類別 (Order:特店指定單號 System:系統指定單號 )
     * @param String OrderID        訂單代號
     * @param String TransCode      交易代碼
     * @return ArrayList            列表資料
     */
    public ArrayList get_BillingVoid_List(String MerchantID, String SubMID, String OrderType, String OrderID, String TransCode)
    {
        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
        DataBaseBean2 sysBean = new DataBaseBean2();

        if (MerchantID.length() > 0 && SubMID.length() > 0 && OrderType.length() > 0 && OrderID.length() > 0)
        {
        	StringBuffer sSQLSB = new StringBuffer();
            sysBean.ClearSQLParam();
    		sSQLSB.append("SELECT A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG , B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT FROM ");
            sSQLSB.append("(SELECT X.*  FROM BILLING X, (SELECT MERCHANTID, SUBMID, ORDERID FROM BILLING WHERE MERCHANTID = ? ");
            sSQLSB.append(" AND SUBMID = ? ");
            
            sysBean.AddSQLParam(emDataType.STR, MerchantID);
            sysBean.AddSQLParam(emDataType.STR, SubMID);

            if (TransCode.length() > 0)
            {
                sSQLSB.append(" AND TRANSCODE = ? ");
                sysBean.AddSQLParam(emDataType.STR, TransCode);
            }

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

            sSQLSB.append(") Y  WHERE X.MERCHANTID = Y.MERCHANTID AND X.SUBMID = Y.SUBMID AND X.ORDERID = Y.ORDERID ORDER BY X.TRANSDATE, X.TRANSTIME) A, BALANCE B WHERE A.MERCHANTID = B.MERCHANTID AND A.SUBMID = B.SUBMID AND A.ORDERID = B.ORDERID GROUP BY A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG , B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT");

            // System.out.println("Sql=" + Sql);
            try
            {
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

    /* Override get_BillingVoid_List with DataBaseBean parameter */
    public ArrayList get_BillingVoid_List(DataBaseBean2 sysBean, String MerchantID, String SubMID, String OrderType, String OrderID, String TransCode)
    {
        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
        // DataBaseBean SysBean = new DataBaseBean();

        if (MerchantID.length() > 0 && SubMID.length() > 0 && OrderType.length() > 0 && OrderID.length() > 0)
        {
            // SQL註解掉 by Jimmy Kang 20150511
        	//StringBuffer Sql = new StringBuffer("SELECT A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG , B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT FROM ");
        	// Merchant Console 線上取消作業模組  修改  by Jimmy Kang 20150511  -- 修改開始 --
        	// SQL語法中多select A.CARDTYPE, 因為要取得卡別的值才能做判斷去ban掉 卡別為C - CUP 的退貨交易 做取消的動作
        	StringBuffer sSQLSB = new StringBuffer();
            sysBean.ClearSQLParam();
    		sSQLSB.append("SELECT A.CARD_TYPE, A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG , B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT FROM ");
        	// Merchant Console 線上取消作業模組  修改  by Jimmy Kang 20150511  -- 修改結束 --
        	
        	sSQLSB.append("(SELECT X.*  FROM BILLING X, (SELECT MERCHANTID, SUBMID, ORDERID FROM BILLING WHERE MERCHANTID = '"+ MerchantID + "' ");
            sSQLSB.append(" AND SUBMID = ?' ");
            
            sysBean.AddSQLParam(emDataType.STR, SubMID);

            if (TransCode.length() > 0)
            {
                sSQLSB.append(" AND TRANSCODE = ? ");
                sysBean.AddSQLParam(emDataType.STR, TransCode);
            }

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

            // System.out.println("Sql=" + Sql);
            
            // SQL註解掉 by Jimmy Kang 20150511
            //sSQLSB.append(") Y  WHERE X.MERCHANTID = Y.MERCHANTID AND X.SUBMID = Y.SUBMID AND X.ORDERID = Y.ORDERID ORDER BY X.TRANSDATE, X.TRANSTIME) A, BALANCE B WHERE A.MERCHANTID = B.MERCHANTID AND A.SUBMID = B.SUBMID AND A.ORDERID = B.ORDERID GROUP BY A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG , B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT");
            // Merchant Console 線上取消作業模組  修改  by Jimmy Kang 20150511  -- 修改開始 --
            sSQLSB.append(") Y  WHERE X.MERCHANTID = Y.MERCHANTID AND X.SUBMID = Y.SUBMID AND X.ORDERID = Y.ORDERID ORDER BY X.TRANSDATE, X.TRANSTIME) A, BALANCE B WHERE A.MERCHANTID = B.MERCHANTID AND A.SUBMID = B.SUBMID AND A.ORDERID = B.ORDERID GROUP BY A.CARD_TYPE, A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG , B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT");
            // Merchant Console 線上取消作業模組  修改  by Jimmy Kang 20150511  -- 修改結束 --
            
            try
            {
            	/** 2023/05/10 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-036 */
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

    /**
     * 取得批次購貨及退貨取消列表資料(Billing+Balance)
     * @param String MerchantID     特店代號
     * @param String SubMID         特店代號
     * @param String OrderType      查詢訂單類別 (Order:特店指定單號 System:系統指定單號 )
     * @param String OrderID        訂單代號
     * @param String TransCode      交易代碼 (00-購貨,01-退貨)
     * @param String TransDate      授權日期
     * @param String TransTime      授權時間
     * @param String ApproveCode    授權碼
     * @param String TransAmt       交易金額
     * @return ArrayList            列表資料
     */
    public ArrayList get_BillingVoid_List(String MerchantID, String SubMID, String OrderType, String OrderID,
                                          String TransCode, String TransDate, String TransTime, String ApproveCode,
                                          String TransAmt)
    {
        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
        DataBaseBean2 sysBean = new DataBaseBean2();

        if (MerchantID.length() > 0 && SubMID.length() > 0 && OrderType.length() > 0 && OrderID.length() > 0 &&
            TransCode.length() > 0 && TransDate.length() > 0 && TransTime.length() > 0 && ApproveCode.length() > 0 &&
            TransAmt.length() > 0 )
        {
        	StringBuffer sSQLSB = new StringBuffer();
            sysBean.ClearSQLParam();
    		sSQLSB.append("SELECT A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG , B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT FROM ");
            sSQLSB.append("(SELECT X.*  FROM BILLING X, (SELECT MERCHANTID, SUBMID, ORDERID FROM BILLING WHERE MERCHANTID = ? ");
            sSQLSB.append(" AND SUBMID = ? ");
            
            sysBean.AddSQLParam(emDataType.STR, MerchantID);
            sysBean.AddSQLParam(emDataType.STR, SubMID);

            if (TransCode.length() > 0)
            {
                sSQLSB.append(" AND TRANSCODE = ? ");
                sysBean.AddSQLParam(emDataType.STR, TransCode);
            }

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

            if (TransCode.length() > 0)
            {
                sSQLSB.append(" AND TRANSCODE = ? ");
                sysBean.AddSQLParam(emDataType.STR, TransCode);
            }

            if (TransDate.length() > 0)
            {
                sSQLSB.append(" AND TRANSDATE = ? ");
                sysBean.AddSQLParam(emDataType.STR, TransDate);
            }

            if (TransTime.length() > 0)
            {
                sSQLSB.append(" AND TRANSTIME = ? ");
                sysBean.AddSQLParam(emDataType.STR, TransTime);
            }

            if (ApproveCode.length() > 0)
            {
                sSQLSB.append(" AND TRIM(APPROVECODE) = ? ");
                sysBean.AddSQLParam(emDataType.STR, ApproveCode);
            }

            if (TransAmt.length() > 0)
            {
                sSQLSB.append(" AND TRANSAMT = ? ");
                sysBean.AddSQLParam(emDataType.INT, TransAmt);
            }

            sSQLSB.append(") Y  WHERE X.MERCHANTID = Y.MERCHANTID AND X.SUBMID = Y.SUBMID AND X.ORDERID = Y.ORDERID ORDER BY X.TRANSDATE, X.TRANSTIME) A, BALANCE B WHERE A.MERCHANTID = B.MERCHANTID AND A.SUBMID = B.SUBMID AND A.ORDERID = B.ORDERID GROUP BY A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG , B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT");
            // System.out.println("Sql=" + Sql);

            try
            {
            	/** 2023/05/12 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-040 (No Need Test) */
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

    /* Override get_BillingVoid_List with DataBaseBean parameter */
    public ArrayList get_BillingVoid_List(DataBaseBean2 sysBean, String MerchantID, String SubMID, String OrderType, String OrderID,
                                          String TransCode, String TransDate, String TransTime, String ApproveCode, String TransAmt)
    {
        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
        // DataBaseBean SysBean = new DataBaseBean();

        if (MerchantID.length() > 0 && SubMID.length() > 0 && OrderType.length() > 0 && OrderID.length() > 0 &&
            TransCode.length() > 0 && TransDate.length() > 0 && TransTime.length() > 0 && ApproveCode.length() > 0 &&
            TransAmt.length() > 0 )
        {
        	StringBuffer sSQLSB = new StringBuffer();
            sysBean.ClearSQLParam();
    		sSQLSB.append("SELECT A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG , B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT FROM ");
            sSQLSB.append("(SELECT X.*  FROM BILLING X, (SELECT MERCHANTID, SUBMID, ORDERID FROM BILLING WHERE MERCHANTID = ? ");
            sSQLSB.append(" AND SUBMID = ? ");
            
            sysBean.AddSQLParam(emDataType.STR, MerchantID);
            sysBean.AddSQLParam(emDataType.STR, SubMID);

            if (TransCode.length() > 0)
            {
                sSQLSB.append(" AND TRANSCODE = ? ");
                sysBean.AddSQLParam(emDataType.STR, TransCode);
            }

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

            if (TransCode.length() > 0)
            {
                sSQLSB.append(" AND TRANSCODE = ? ");
                sysBean.AddSQLParam(emDataType.STR, TransCode);
            }

            if (TransDate.length() > 0)
            {
                sSQLSB.append(" AND TRANSDATE = ? ");
                sysBean.AddSQLParam(emDataType.STR, TransDate);
            }

            if (TransTime.length() > 0)
            {
                sSQLSB.append(" AND TRANSTIME = ? ");
                sysBean.AddSQLParam(emDataType.STR, TransTime);
            }

            if (ApproveCode.length() > 0)
            {
                sSQLSB.append(" AND TRIM(APPROVECODE) = ? ");
                sysBean.AddSQLParam(emDataType.STR, TransTime);
            }

            if (TransAmt.length() > 0)
            {
                sSQLSB.append(" AND TRANSAMT = ? ");
                sysBean.AddSQLParam(emDataType.INT, TransAmt);
            }

            sSQLSB.append(") Y  WHERE X.MERCHANTID = Y.MERCHANTID AND X.SUBMID = Y.SUBMID AND X.ORDERID = Y.ORDERID ORDER BY X.TRANSDATE, X.TRANSTIME) A, BALANCE B WHERE A.MERCHANTID = B.MERCHANTID AND A.SUBMID = B.SUBMID AND A.ORDERID = B.ORDERID GROUP BY A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG , B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT");
            // System.out.println("sSQLSB=" + sSQLSB.toString());

            try
            {
            	/** 2023/05/18 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-030 (No Need Test) */
                arrayBillingData =  (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
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
//     * 取得各別取得退貨取消資料(Billing+Balance)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店代號
//     * @param String OrderType      查詢訂單類別 (Order:特店指定單號 System:系統指定單號 )
//     * @param String OrderID        訂單代號
//     * @param String TransCode      交易代碼
//     * @return ArrayList            列表資料
//     */
//    public ArrayList get_BillingVoid_List(String MerchantID, String SubMID, String OrderID, String TransCode)
//    {
//        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
//        DataBaseBean SysBean = new DataBaseBean();
//
//        if (MerchantID.length() > 0 && SubMID.length() > 0 && OrderID.length() > 0)
//        {
//            StringBuffer Sql = new StringBuffer("SELECT A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG , B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT FROM BILLING A , BALANCE B WHERE A.MERCHANTID = '"+ MerchantID + "' ");
//            Sql.append(" AND A.SUBMID = '" + SubMID + "' ");
//            if (TransCode.length() > 0)
//            {
//                Sql.append(" AND A.TRANSCODE = '" + TransCode + "' ");
//            }
//
//            Sql.append(" AND A.SYS_ORDERID = '" + OrderID + "' ");
//            Sql.append(" AND A.MERCHANTID = B.MERCHANTID AND A.SUBMID = B.SUBMID AND A.ORDERID = B.ORDERID GROUP BY A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG , B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT");
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
//        return arrayBillingData;
//    }

//    /* Override get_BillingVoid_List with DataBaseBean parameter */
//    public ArrayList get_BillingVoid_List(DataBaseBean SysBean, String MerchantID, String SubMID, String OrderID, String TransCode)
//    {
//        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
//        // DataBaseBean SysBean = new DataBaseBean();
//
//        if (MerchantID.length() > 0 && SubMID.length() > 0 && OrderID.length() > 0)
//        {
//            StringBuffer Sql = new StringBuffer("SELECT A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG , B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT FROM BILLING A , BALANCE B WHERE A.MERCHANTID = '"+ MerchantID + "' ");
//            Sql.append(" AND A.SUBMID = '" + SubMID + "' ");
//            if (TransCode.length() > 0)
//            {
//                Sql.append(" AND A.TRANSCODE = '" + TransCode + "' ");
//            }
//
//            Sql.append(" AND A.SYS_ORDERID = '" + OrderID + "' ");
//            Sql.append(" AND A.MERCHANTID = B.MERCHANTID AND A.SUBMID = B.SUBMID AND A.ORDERID = B.ORDERID GROUP BY A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG , B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT");
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
//        return arrayBillingData;
//    }

//    /**
//     * 取得各別取得批次退貨取消資料(Billing+Balance)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店代號
//     * @param String OrderType      查詢訂單類別 (Order:特店指定單號 System:系統指定單號 )
//     * @param String OrderID        訂單代號
//     * @param String TransCode      交易代碼 (00-購貨,01-退貨)
//     * @param String TransDate      授權日期
//     * @param String TransTime      授權時間
//     * @param String ApproveCode    授權碼
//     * @param String TransAmt       交易金額
//     * @return ArrayList            列表資料
//     */
//    public ArrayList get_BillingVoid_List(String MerchantID, String SubMID, String OrderID, String TransCode, String TransDate,
//                                          String TransTime, String ApproveCode, String TransAmt)
//    {
//        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
//        // System.out.println("---------- Start get_BillingVoid_List -------");
//        // System.out.println("MerchantID="+MerchantID);
//        // System.out.println("SubMID="+SubMID);
//        // System.out.println("OrderID="+OrderID);
//        // System.out.println("TransCode="+TransCode);
//
//        DataBaseBean SysBean = new DataBaseBean();
//        if (MerchantID.length() > 0 && SubMID.length() > 0 && OrderID.length() > 0 && TransCode.length() > 0 &&
//            TransDate.length() > 0 && TransTime.length() > 0 && ApproveCode.length() > 0 && TransAmt.length() > 0 )
//        {
//            StringBuffer Sql = new StringBuffer("SELECT A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.ACQUIRERID, A.CARD_TYPE, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG , A.PAN, A.EXTENNO, A.EXPIREDATE,A.USERDEFINE, A.EMAIL, A.MTI, A.RRN, A.SOCIALID, A.ENTRY_MODE, A.CONDITION_CODE, A.TRANSTYPE, A.ECI, A.CAVV, A.SYSTRACENO, A.XID, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE, A.BALANCEAMT, B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT FROM BILLING A , BALANCE B WHERE A.MERCHANTID = '"+ MerchantID + "' ");
//            Sql.append(" AND A.SUBMID = '" + SubMID + "' ");
//            if (TransCode.length() > 0)
//            {
//                Sql.append(" AND A.TRANSCODE = '" + TransCode + "' ");
//            }
//
//            Sql.append(" AND A.ORDERID = '" + OrderID + "' ");
//            if (TransDate.length() > 0)
//            {
//                Sql.append(" AND A.TRANSDATE = '" + TransDate + "' ");
//            }
//
//            if (TransTime.length() > 0)
//            {
//                Sql.append(" AND A.TRANSTIME = '" + TransTime + "' ");
//            }
//
//            if (ApproveCode.length() > 0)
//            {
//                Sql.append(" AND TRIM(A.APPROVECODE) = '" + ApproveCode + "' ");
//            }
//
//            if (TransAmt.length() > 0)
//            {
//                Sql.append(" AND A.TRANSAMT = " + TransAmt + " ");
//            }
//
//            Sql.append(" AND A.MERCHANTID = B.MERCHANTID AND A.SUBMID = B.SUBMID AND A.ORDERID = B.ORDERID GROUP BY A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG, A.ACQUIRERID, A.CARD_TYPE, A.PAN, A.EXTENNO, A.EXPIREDATE, A.USERDEFINE, A.TRANSTYPE, A.SYSTRACENO, A.EMAIL, A.MTI, A.RRN, A.SOCIALID, A.ENTRY_MODE, A.CONDITION_CODE, A.ECI, A.CAVV, A.XID, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE, A.BALANCEAMT , B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT");
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
//        return arrayBillingData;
//    }

    /* Override get_BillingVoid_List with DataBaseBean parameter */
    public ArrayList get_BillingVoid_List(DataBaseBean2 sysBean, String MerchantID, String SubMID, String OrderID, String TransCode, String TransDate,
                                          String TransTime, String ApproveCode, String TransAmt)
    {
        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
        // System.out.println("---------- Start get_BillingVoid_List -------");
        // System.out.println("MerchantID="+MerchantID);
        // System.out.println("SubMID="+SubMID);
        // System.out.println("OrderID="+OrderID);
        // System.out.println("TransCode="+TransCode);
        // DataBaseBean SysBean = new DataBaseBean();

        if (MerchantID.length() > 0 && SubMID.length() > 0 && OrderID.length() > 0 && TransCode.length() > 0 &&
            TransDate.length() > 0 && TransTime.length() > 0 && ApproveCode.length() > 0 && TransAmt.length() > 0 )
        {
        	//20220210 ADD AUTH_SRC_CODE
        	StringBuffer sSQLSB = new StringBuffer();
            sysBean.ClearSQLParam();
    		sSQLSB.append("SELECT A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.ACQUIRERID, A.CARD_TYPE, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG , A.PAN, A.EXTENNO, A.EXPIREDATE,A.USERDEFINE, A.EMAIL, A.MTI, A.RRN, A.SOCIALID, A.ENTRY_MODE, A.CONDITION_CODE, A.TRANSTYPE, A.ECI, A.CAVV, A.SYSTRACENO, A.XID, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE, A.BALANCEAMT, B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT, A.AUTH_SRC_CODE FROM BILLING A , BALANCE B "
    				+ "WHERE A.MERCHANTID = ? ");
            sSQLSB.append(" AND A.SUBMID = ? ");
            
            sysBean.AddSQLParam(emDataType.STR, MerchantID);
            sysBean.AddSQLParam(emDataType.STR, SubMID);
            
            if (TransCode.length() > 0)
            {
                sSQLSB.append(" AND A.TRANSCODE = ? ");
                sysBean.AddSQLParam(emDataType.STR, TransCode);
            }

            sSQLSB.append(" AND A.ORDERID = ? ");
            sysBean.AddSQLParam(emDataType.STR, OrderID);
            
            if (TransDate.length() > 0)
            {
                sSQLSB.append(" AND A.TRANSDATE = ? ");
                sysBean.AddSQLParam(emDataType.STR, TransDate);
            }

            if (TransTime.length() > 0)
            {
                sSQLSB.append(" AND A.TRANSTIME = ? ");
                sysBean.AddSQLParam(emDataType.STR, TransTime);
            }

            if (ApproveCode.length() > 0)
            {
                sSQLSB.append(" AND TRIM(A.APPROVECODE) = ? ");
                sysBean.AddSQLParam(emDataType.STR, ApproveCode);
            }

            if (TransAmt.length() > 0)
            {
                sSQLSB.append(" AND A.TRANSAMT = ? ");
                sysBean.AddSQLParam(emDataType.STR, TransAmt);
            }

            sSQLSB.append(" AND A.MERCHANTID = B.MERCHANTID AND A.SUBMID = B.SUBMID AND A.ORDERID = B.ORDERID GROUP BY A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG, A.ACQUIRERID, A.CARD_TYPE, A.PAN, A.EXTENNO, A.EXPIREDATE, A.USERDEFINE, A.TRANSTYPE, A.SYSTRACENO, A.EMAIL, A.MTI, A.RRN, A.SOCIALID, A.ENTRY_MODE, A.CONDITION_CODE, A.ECI, A.CAVV, A.XID, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE, A.BALANCEAMT , B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT");
            // System.out.println("sSQLSB=" + sSQLSB.toString());

            try
            {
            	/** 2023/05/18 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-030 (No Need Test) */
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


    /**
     * 判斷是否允許購貨取消(列表)
     * @param ArrayList arrayBillingData 取消列表資料
     * @return Hashtable                 判斷結果 FLAG:退貨判斷(String)  MESSAGE:顯示結果(String)
     */
    public Hashtable check_SaleVoid_Status(ArrayList arrayBillingData)
    {
        Hashtable hashData = new Hashtable();
        boolean boolFlag = false; // 是否能做退貨
        String strMessage = "查無交易資料"; // 顯示結果

        if (arrayBillingData.size() > 0)
        {
            for (int c = 0; c < arrayBillingData.size(); ++c)
            {
                Hashtable hashTmpData = (Hashtable) arrayBillingData.get(c);
                String TransCode = hashTmpData.get("TRANSCODE").toString();
                String TransAmt = hashTmpData.get("TRANSAMT").toString();
                String BalanceAmt = hashTmpData.get("BALANCEAMT").toString();
                String AuthAmt = hashTmpData.get("AUTHAMT").toString(); // 授權金額
                String RefundAmt = hashTmpData.get("REFUNDAMT").toString(); // 退貨金額
                String CancelAmt = hashTmpData.get("CANCELAMT").toString(); // 購貨取消貨金額
                String CaptureAmt = hashTmpData.get("CAPTUREAMT").toString(); // 請款金額
                if (TransCode.length() > 0 && TransAmt.length() > 0 && BalanceAmt.length() > 0 && AuthAmt.length() > 0 &&
                    RefundAmt.length() > 0 && CancelAmt.length() > 0 && CaptureAmt.length() > 0)
                {
                    if (TransCode.equalsIgnoreCase("00"))
                    {
                        // 購貨交易
                        boolFlag = true;
                        if (Double.parseDouble(TransAmt) != Double.parseDouble(AuthAmt))
                        {
                            strMessage = "交易金額不符";
                            boolFlag = false;
                        }

                        if (Double.parseDouble(TransAmt) != Double.parseDouble(BalanceAmt))
                        {
                            strMessage = "交易已取消/請款";
                            boolFlag = false;
                        }

                        if (Double.parseDouble(CancelAmt) > 0)
                        {
                            strMessage = "交易已取消";
                            boolFlag = false;
                        }

                        if (Double.parseDouble(RefundAmt) > 0)
                        {
                            strMessage = "交易已退貨";
                            boolFlag = false;
                        }

                        if (Double.parseDouble(CaptureAmt) > 0)
                        {
                            strMessage = "交易已請款";
                            boolFlag = false;
                        }

                        break;
                    }
                }
                else
                {
                    strMessage = "查無交易資料";
                }
            }
        }

        hashData.put("FLAG", String.valueOf(boolFlag));
        hashData.put("MESSAGE", strMessage);

        return hashData;
    }

    /**
     * 判斷是否允許退貨取消(列表)
     * @param ArrayList arrayBillingData 取消列表資料
     * @return Hashtable                 判斷結果 FLAG:退貨判斷(String)  MESSAGE:顯示結果(String)
     */
    public Hashtable check_RefundVoid_Status(ArrayList arrayBillingData)
    {
        Hashtable hashData = new Hashtable();
        boolean boolFlag = false; // 是否能做退貨
        int RefundCnt = 0;
        String strMessage = "查無交易資料"; // 顯示結果

        if (arrayBillingData.size() > 0)
        {
            for (int c = 0; c < arrayBillingData.size(); ++c)
            {
                Hashtable hashTmpData = (Hashtable) arrayBillingData.get(c);
                String TransCode = hashTmpData.get("TRANSCODE").toString();
                String TransAmt = hashTmpData.get("TRANSAMT").toString();
                String BalanceAmt = hashTmpData.get("BALANCEAMT").toString();
                String RefundAmt = hashTmpData.get("REFUNDAMT").toString(); // 退貨金額
                
                // Merchant Console 線上取消作業模組  修改  by Jimmy Kang 20150511  -- 修改開始 --
                // 新增 CardType變數用以判斷卡別是否為 C - CUP
                String CardType = hashTmpData.get("CARD_TYPE").toString(); //卡別
                // Merchant Console 線上取消作業模組  修改  by Jimmy Kang 20150511  -- 修改結束 --

                if (TransCode.length() > 0 && TransAmt.length() > 0 &&
                    BalanceAmt.length() > 0 && RefundAmt.length() > 0)
                {
                    if (TransCode.equalsIgnoreCase("01"))
                    {
                        // 退貨交易
                        if (Double.parseDouble(RefundAmt) < 0)
                        {
                            strMessage = "交易己取消";
                        }
                        else
                        {
                            System.out.println("-----TransAmt ="+TransAmt+" BalanceAmt="+BalanceAmt);
                            if (Double.parseDouble(TransAmt) != Double.parseDouble(BalanceAmt))
                            {
                                strMessage = "交易金額不符";
                            }
                            else
                            {
                            	// 註解掉 By Jimmy Kang 20150511
                            	//RefundCnt++;
                            	
                            	// Merchant Console 線上取消作業模組  修改  by Jimmy Kang 20150511  -- 修改開始 --
                            	// 加入判斷 CARD_TYPE是否為C - CUP 的條件
                            	if (!CardType.equalsIgnoreCase("C"))
                            	{
                            		RefundCnt++;
                            	}
                            	// Merchant Console 線上取消作業模組  修改  by Jimmy Kang 20150511  -- 修改結束 --
                            }
                        }
                    }
                }
                else
                {
                    strMessage = "查無取消資料";
                }
            }

            if (RefundCnt==0)
            {
               boolFlag = false;
               strMessage = "查無可退貨取消資料";
            }
            else
            {
               boolFlag = true;
            }
        }

        hashData.put("FLAG", String.valueOf(boolFlag));
        hashData.put("MESSAGE", strMessage);

        return hashData;
    }

//    /**
//     * 取得請款取消列表資料(Capture+Balance)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店代號
//     * @param String OrderType      查詢訂單類別 (Order:特店指定單號 System:系統指定單號 )
//     * @param String OrderID        訂單代號
//     * @return ArrayList            列表資料
//     */
//    public ArrayList get_CaptureVoid_List(String MerchantID, String SubMID, String OrderType, String OrderID)
//    {
//        ArrayList arrayCaptureData = new ArrayList(); // 顯示訂單交易資料
//        DataBaseBean SysBean = new DataBaseBean();
//
//        if (MerchantID.length() > 0 && SubMID.length() > 0 && OrderType.length() > 0 && OrderID.length() > 0)
//        {
//            StringBuffer Sql = new StringBuffer("SELECT ROWIDTOCHAR(A.ROWID) AS RROWID, A.MERCHANTID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.CURRENCYCODE, A.CAPTUREAMT, TO_CHAR(A.CAPTUREDATE, 'YYYY/MM/DD HH24:MI:SS') CAPTUREDATE, A.CAPTUREFLAG, A.FEEDBACKCODE, A.EXTENNO, A.RRN, A.MTI, A.XID, A.SOCIALID, A.TRANSAMT , B.AUTHAMT, B.REFUNDAMT, B.CAPTUREAMT AS BALCAPTUREAMT , B.REFUNDCAPTUREAMT  FROM CAPTURE A, BALANCE B WHERE A.MERCHANTID = B.MERCHANTID AND A.SUBMID = B.SUBMID AND A.ORDERID = B.ORDERID ");
//            Sql.append(" AND A.MERCHANTID = '" + MerchantID + "' ");
//            Sql.append(" AND A.SUBMID = '" + SubMID + "' ");
//
//            if (OrderType.equalsIgnoreCase("Order"))
//            {
//                // 以OrderID
//                Sql.append(" AND A.ORDERID = '" + OrderID + "' ");
//            }
//            else
//            {
//                Sql.append(" AND A.SYS_ORDERID = '" + OrderID + "' ");
//            }
//
//            Sql.append(" ORDER BY A.TRANSDATE, A.TRANSTIME ");
//            // System.out.println("Sql=" + Sql);
//
//            try
//            {
//                arrayCaptureData = (ArrayList) SysBean.executeSQL(Sql.toString(), "select");
//            }
//            catch (Exception ex)
//            {
//                System.out.println(ex.getMessage());
//                log_systeminfo.debug(ex.toString());
//            }
//
//            if (arrayCaptureData == null)
//            {
//                arrayCaptureData = new ArrayList();
//            }
//        }
//
//        return arrayCaptureData;
//    }

    /* Override get_CaptureVoid_List with DataBaseBean parameter */
    public ArrayList get_CaptureVoid_List(DataBaseBean2 sysBean, String MerchantID, String SubMID, String OrderType, String OrderID)
    {
        ArrayList arrayCaptureData = new ArrayList(); // 顯示訂單交易資料
        // DataBaseBean SysBean = new DataBaseBean();

        if (MerchantID.length() > 0 && SubMID.length() > 0 && OrderType.length() > 0 && OrderID.length() > 0)
        {
        	StringBuffer sSQLSB = new StringBuffer();
            sysBean.ClearSQLParam();
    		sSQLSB.append("SELECT ROWIDTOCHAR(A.ROWID) AS RROWID, A.MERCHANTID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.CURRENCYCODE, A.CAPTUREAMT, TO_CHAR(A.CAPTUREDATE, 'YYYY/MM/DD HH24:MI:SS') CAPTUREDATE, A.CAPTUREFLAG, A.FEEDBACKCODE, A.EXTENNO, A.RRN, A.MTI, A.XID, A.SOCIALID, A.TRANSAMT , B.AUTHAMT, B.REFUNDAMT, B.CAPTUREAMT AS BALCAPTUREAMT , B.REFUNDCAPTUREAMT  FROM CAPTURE A, BALANCE B WHERE A.MERCHANTID = B.MERCHANTID AND A.SUBMID = B.SUBMID AND A.ORDERID = B.ORDERID ");
    		sSQLSB.append(" AND A.MERCHANTID = ? ");
    		sSQLSB.append(" AND A.SUBMID = ? ");
    		
    		sysBean.AddSQLParam(emDataType.STR, MerchantID);
    		sysBean.AddSQLParam(emDataType.STR, SubMID);

            if (OrderType.equalsIgnoreCase("Order"))
            {
                // 以OrderID
            	sSQLSB.append(" AND A.ORDERID = ? ");
            	sysBean.AddSQLParam(emDataType.STR, OrderID);
            }
            else
            {
            	sSQLSB.append(" AND A.SYS_ORDERID = ? ");
            	sysBean.AddSQLParam(emDataType.STR, OrderID);
            }

            sSQLSB.append(" ORDER BY A.TRANSDATE, A.TRANSTIME ");
            // System.out.println("Sql=" + Sql);

            try
            {
            	/** 2023/05/09 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-033 */
                arrayCaptureData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
                log_systeminfo.debug(ex.toString());
            }
            
            if (arrayCaptureData == null) 
            {
                arrayCaptureData = new ArrayList();
            }
        }
        
        return arrayCaptureData;
    }

//    /**
//     * 取得批次請款取消列表資料(Capture+Balance)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店代號
//     * @param String TerminalID     端末機代號
//     * @param String OrderID        訂單代號
//     * @param String TransDate      授權日期
//     * @param String TransTime      授權時間
//     * @param String ApproveCode    授權碼
//     * @param String TransCode      交易類別(00-購貨,01-退貨)
//     * @param String CaptureAmt     請款金額
//     * @return ArrayList            列表資料
//     */
//    public ArrayList get_CaptureVoid_List(String MerchantID, String SubMID, String TerminalID, String OrderID,
//                                          String TransDate, String TransTime, String ApproveCode, String TransCode,
//                                          String CaptureAmt ) 
//    {
//        ArrayList arrayCaptureData = new ArrayList(); // 顯示訂單交易資料
//        DataBaseBean SysBean = new DataBaseBean();
//        
//        if (MerchantID.length() > 0 && SubMID.length() > 0 && TerminalID.length() > 0 && OrderID.length() > 0 &&
//            TransDate.length()  > 0 && TransTime.length() > 0 && ApproveCode.length() > 0 && TransCode.length() > 0 &&
//            CaptureAmt.length() > 0 ) 
//        {
//            StringBuffer Sql = new StringBuffer("SELECT ROWIDTOCHAR(A.ROWID) AS RROWID, A.MERCHANTID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.CURRENCYCODE, A.CAPTUREAMT, TO_CHAR(A.CAPTUREDATE, 'YYYY/MM/DD HH24:MI:SS') CAPTUREDATE, A.CAPTUREFLAG, A.FEEDBACKCODE, A.TRANSAMT, A.ACQUIRERID, A.CARD_TYPE, A.PAN, A.EXPIREDATE, A.RESPONSECODE, A.USERDEFINE,  A.ENTRY_MODE, A.CONDITION_CODE, A.ECI, A.CAVV, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.EXTENNO, A.RRN, A.MTI, A.XID, A.SOCIALID, A.BILLMESSAGE, A.SYSTRACENO, B.AUTHAMT, B.REFUNDAMT, B.CAPTUREAMT AS BALCAPTUREAMT , B.REFUNDCAPTUREAMT  FROM CAPTURE A, BALANCE B WHERE A.MERCHANTID = B.MERCHANTID AND A.SUBMID = B.SUBMID AND A.ORDERID = B.ORDERID ");
//            Sql.append(" AND A.MERCHANTID = '" + MerchantID + "' ");
//            Sql.append(" AND A.SUBMID = '" + SubMID + "' ");
//            Sql.append(" AND A.TERMINALID = '" + TerminalID + "' ");
//            Sql.append(" AND A.ORDERID = '" + OrderID + "' ");
//            Sql.append(" AND A.TRANSDATE = '" + TransDate + "' ");
//            Sql.append(" AND A.TRANSTIME = '" + TransTime + "' ");
//            Sql.append(" AND TRIM(A.APPROVECODE) = '" + ApproveCode + "' ");
//            Sql.append(" AND TRIM(A.TRANSCODE) = '" + TransCode + "' ");
//            Sql.append(" AND A.CAPTUREAMT = " + CaptureAmt + " ");
//            Sql.append(" AND A.CAPTUREFLAG <> '2' ");
//            Sql.append(" ORDER BY A.TRANSDATE, A.TRANSTIME ");
//            // System.out.println("Sql=" + Sql);
//            
//            try 
//            {
//                arrayCaptureData = (ArrayList) SysBean.executeSQL(Sql.toString(), "select");
//            } 
//            catch (Exception ex) 
//            {
//                System.out.println(ex.getMessage());
//                log_systeminfo.debug(ex.toString());
//            }
//            
//            if (arrayCaptureData == null) 
//            {
//                arrayCaptureData = new ArrayList();
//            }
//        }
//        
//        return arrayCaptureData ;
//    }

    /* Override get_CaptureVoid_List with DataBaseBean parameter */
    public ArrayList get_CaptureVoid_List(DataBaseBean2 sysBean, String MerchantID, String SubMID, String TerminalID, String OrderID,
                                          String TransDate, String TransTime, String ApproveCode, String TransCode,
                                          String CaptureAmt ) 
    {
        ArrayList arrayCaptureData = new ArrayList(); // 顯示訂單交易資料
        // DataBaseBean SysBean = new DataBaseBean();
        
        if (MerchantID.length() > 0 && SubMID.length() > 0 && TerminalID.length() > 0 && OrderID.length() > 0 &&
            TransDate.length()  > 0 && TransTime.length() > 0 && ApproveCode.length() > 0 && TransCode.length() > 0 &&
            CaptureAmt.length() > 0 ) 
        {
        	//20220210 ADD AUTH_SRC_CODE
        	StringBuffer sSQLSB = new StringBuffer();
            sysBean.ClearSQLParam();
    		sSQLSB.append("SELECT ROWIDTOCHAR(A.ROWID) AS RROWID, A.MERCHANTID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.CURRENCYCODE, A.CAPTUREAMT, TO_CHAR(A.CAPTUREDATE, 'YYYY/MM/DD HH24:MI:SS') CAPTUREDATE, A.CAPTUREFLAG, A.FEEDBACKCODE, A.TRANSAMT, A.ACQUIRERID, A.CARD_TYPE, A.PAN, A.EXPIREDATE, A.RESPONSECODE, A.USERDEFINE,  A.ENTRY_MODE, A.CONDITION_CODE, A.ECI, A.CAVV, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.EXTENNO, A.RRN, A.MTI, A.XID, A.SOCIALID, A.BILLMESSAGE, A.SYSTRACENO, B.AUTHAMT, B.REFUNDAMT, B.CAPTUREAMT AS BALCAPTUREAMT , B.REFUNDCAPTUREAMT, A.AUTH_SRC_CODE  FROM CAPTURE A, BALANCE B WHERE A.MERCHANTID = B.MERCHANTID AND A.SUBMID = B.SUBMID AND A.ORDERID = B.ORDERID ");
            sSQLSB.append(" AND A.MERCHANTID = ? ");
            sSQLSB.append(" AND A.SUBMID = ? ");
            sSQLSB.append(" AND A.TERMINALID = ? ");
            sSQLSB.append(" AND A.ORDERID = ? ");
            sSQLSB.append(" AND A.TRANSDATE = ? ");
            sSQLSB.append(" AND A.TRANSTIME = ? ");
            sSQLSB.append(" AND TRIM(A.APPROVECODE) = ? ");
            sSQLSB.append(" AND TRIM(A.TRANSCODE) = ? ");
            sSQLSB.append(" AND A.CAPTUREAMT = ? ");
            sSQLSB.append(" AND A.CAPTUREFLAG <> '2' ");
            sSQLSB.append(" ORDER BY A.TRANSDATE, A.TRANSTIME ");
            // System.out.println("sSQLSB=" + sSQLSB.toString());
            
            sysBean.AddSQLParam(emDataType.STR, MerchantID);
            sysBean.AddSQLParam(emDataType.STR, SubMID);
            sysBean.AddSQLParam(emDataType.STR, TerminalID);
            sysBean.AddSQLParam(emDataType.STR, OrderID);
            sysBean.AddSQLParam(emDataType.STR, TransDate);
            sysBean.AddSQLParam(emDataType.STR, TransTime);
            sysBean.AddSQLParam(emDataType.STR, ApproveCode);
            sysBean.AddSQLParam(emDataType.STR, TransCode);
            sysBean.AddSQLParam(emDataType.INT, CaptureAmt);
            
            try 
            {
            	/** 2023/05/18 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-030 (No Need Test) */
                arrayCaptureData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
            } 
            catch (Exception ex) 
            {
                System.out.println(ex.getMessage());
                log_systeminfo.debug(ex.toString());
            }
            
            if (arrayCaptureData == null) 
            {
                arrayCaptureData = new ArrayList();
            }
        }
        
        return arrayCaptureData ;
    }
    
    /**
     * 彙總本次取消訂單請款金額
     * @param  ArrayList arrayList 請款資料
     * @return Hashtable     彙總資料
     */
    public Hashtable sum_CaptureVoid_Amt(ArrayList arrayList) 
    {
        Hashtable hashSum = new Hashtable();
        
        for (int i = 0; i < arrayList.size(); ++i) 
        {
            Hashtable hashList = (Hashtable) arrayList.get(i);
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
                String BalCaptureAmt = hashList.get("BALCAPTUREAMT").toString();
                String RefundCaptureAmt = hashList.get("REFUNDCAPTUREAMT").toString();
                hashtemp.put("AuthAmt", AuthAmt);
                hashtemp.put("RefundAmt", RefundAmt);
                hashtemp.put("CaptureAmt", BalCaptureAmt);
                hashtemp.put("RefundCaptureAmt", RefundCaptureAmt);
                hashtemp.put("OrderID", OrderID);
            } 
            else 
            {
                String TmpCaptureAmt = hashtemp.get("OrderCaptureAmt").toString();
                if (TmpCaptureAmt.length() > 0) 
                {
                    OrderCaptureAmt = TmpCaptureAmt;
                }
                
                String TmpRefundAmt = hashtemp.get("OrderRefundAmt").toString();
                if (TmpRefundAmt.length() > 0) 
                {
                    OrderRefundAmt = TmpRefundAmt;
                }
            }
            
            String CaptureAmt = hashList.get("CAPTUREAMT").toString();
            String TransCode = hashList.get("TRANSCODE").toString();
            if (TransCode.equalsIgnoreCase("00")) 
            { 
                // 購貨
                OrderCaptureAmt = String.valueOf(Double.parseDouble(OrderCaptureAmt) + Double.parseDouble(CaptureAmt));
            }
            
            if (TransCode.equalsIgnoreCase("01")) 
            { 
                // 退貨
                OrderRefundAmt = String.valueOf(Double.parseDouble(OrderRefundAmt) + Double.parseDouble(CaptureAmt));
            }
            
            hashtemp.put("OrderCaptureAmt", OrderCaptureAmt);
            hashtemp.put("OrderRefundAmt", OrderRefundAmt);

            System.out.println("hashtemp=" + hashtemp);
            hashSum.put(OrderID, hashtemp);
        }
        
        System.out.println("hashSum=" + hashSum);
        return hashSum;
    }

    /**
         * 判斷是否允許請款取消(列表)
         * @param ArrayList arrayCaptureData 取消列表資料
         * @return Hashtable                 判斷結果 FLAG:退貨判斷(String)  MESSAGE:顯示結果(String)
         */
        public Hashtable check_CaptureVoid_Status(ArrayList arrayCaptureData) 
        {
            Hashtable hashData = new Hashtable();
            boolean boolFlag = false; // 是否能做退貨
            String strMessage = "查無交易資料"; // 顯示結果
            int count=0;
            System.out.println("arrayCaptureData.size()="+arrayCaptureData.size());
        
            if (arrayCaptureData.size() > 0) 
            {
                for (int c = 0; c < arrayCaptureData.size(); ++c) 
                {
                    Hashtable hashTmpData = (Hashtable) arrayCaptureData.get(c);
                    String CaptureFlag = hashTmpData.get("CAPTUREFLAG").toString(); // 請款狀態
                    String CaptureAmt = hashTmpData.get("CAPTUREAMT").toString(); // 請款金額
                    if (CaptureFlag.length() > 0 && CaptureAmt.length() > 0) 
                    {
                        if (Double.parseDouble(CaptureAmt) < 0) 
                        {
                            strMessage = "交易金額不符";
                            boolFlag = false;
                        }
                        
                        if (!CaptureFlag.equalsIgnoreCase("0")) 
                        {
                            boolFlag = false;
                            if (CaptureFlag.equalsIgnoreCase("1")) 
                            {
                                strMessage = "請款處理中";
                            }
                            
                            if (CaptureFlag.equalsIgnoreCase("2")) 
                            {
                                strMessage = "請款己取消";
                            }
                            
                            if (CaptureFlag.equalsIgnoreCase("3")) 
                            {
                                strMessage = "請款已處理";
                            }
                        } 
                        else 
                        {
                            count++;
                        }
                    } 
                    else 
                    {
                        strMessage = "查無交易資料";
                    }
                }
            }
            
            if (count>0) 
            {
                boolFlag = true;
            }
            
            hashData.put("FLAG", String.valueOf(boolFlag));
            hashData.put("MESSAGE", strMessage);
            
            return hashData;
    }

    /**
     * 判斷是否允許請款取消(列表)
     * @param ArrayList arrayCaptureData 取消列表資料
     * @param Hashtable hashBalanceData  取消列表金額統計資料
     * @param String    OverRefundLimit  超額退資金額
     * @return Hashtable                 判斷結果 FLAG:退貨判斷(String)  MESSAGE:顯示結果(String)
     */
    public Hashtable check_CaptureVoid_Action_Status(ArrayList arrayCaptureData, Hashtable hashBalanceData, String OverRefundLimit) 
    {
        Hashtable hashData = new Hashtable();
        Hashtable hashSuccessData = new Hashtable();  // 交易成功
        Hashtable hashFailData = new Hashtable();     // 交易失敗

        String strMessage = "查無交易資料"; // 顯示結果
        System.out.println("arrayCaptureData.size()="+arrayCaptureData.size());
        if (arrayCaptureData.size() > 0) 
        {
            for (int c = 0; c < arrayCaptureData.size(); ++c) 
            {
                boolean boolFlag = true; // 是否能做退貨
                Hashtable hashTmpData = (Hashtable) arrayCaptureData.get(c);
                String CaptureFlag = hashTmpData.get("CAPTUREFLAG").toString(); // 請款狀態
                String CaptureAmt = hashTmpData.get("CAPTUREAMT").toString(); // 請款金額
                if (CaptureFlag.length() > 0 && CaptureAmt.length() > 0) 
                {
                    if (Double.parseDouble(CaptureAmt) < 0) 
                    {
                        strMessage = "交易金額不符";
                        boolFlag = false;
                    }
                    
                    if (!CaptureFlag.equalsIgnoreCase("0")) 
                    {
                        boolFlag = false;
                        if (CaptureFlag.equalsIgnoreCase("1")) 
                        {
                            strMessage = "請款已完成";
                        }
                        
                        if (CaptureFlag.equalsIgnoreCase("2")) 
                        {
                            strMessage = "請款己取消";
                        }
                    } 
                    else 
                    {
                        String OrderID = hashTmpData.get("ORDERID").toString(); // 特店訂單編號
                        Hashtable hashTemp = (Hashtable)hashBalanceData.get(OrderID);
                        String RefundCaptureAmt = (String)hashTemp.get("RefundCaptureAmt");
                        String OrderCaptureAmt = (String)hashTemp.get("OrderCaptureAmt");
                        String OrderRefundAmt = (String)hashTemp.get("OrderRefundAmt");
                        String BalCaptureAmt = (String)hashTemp.get("CaptureAmt");   //BalanceAmt.CaptureAmt
                        System.out.println("CaptureAmt="+BalCaptureAmt+",RefundCaptureAmt="+RefundCaptureAmt+",OrderCaptureAmt="+OrderCaptureAmt+",OrderRefundAmt="+OrderRefundAmt);

                        System.out.println("CAPTURE="+(Double.parseDouble(BalCaptureAmt) - Double.parseDouble(OrderCaptureAmt) + Double.parseDouble(OverRefundLimit) ));
                        System.out.println("REFUND="+(Double.parseDouble(RefundCaptureAmt) - Double.parseDouble(OrderRefundAmt)));
                    
                        if ((Double.parseDouble(BalCaptureAmt) - Double.parseDouble(OrderCaptureAmt)) == 0) 
                        {
                            if ((Double.parseDouble(RefundCaptureAmt) - Double.parseDouble(OrderRefundAmt) > 0 )) 
                            {
                                boolFlag = false;
                                strMessage = "請款金額必須大於0";
                            }
                        }
                        
                        if (boolFlag) 
                        {
                            if ((Double.parseDouble(BalCaptureAmt) - Double.parseDouble(OrderCaptureAmt) + Double.parseDouble(OverRefundLimit)) <
                                (Double.parseDouble(RefundCaptureAmt) - Double.parseDouble(OrderRefundAmt))) 
                            {
                                boolFlag = false;
                                strMessage = "退貨請款金額不可大於購貨請款金額";
                            } 
                            else 
                            {
                                boolFlag = true;
                                strMessage = "";
                            }
                        }
                    }
                } 
                else 
                {
                    strMessage = "查無交易資料";
                    boolFlag = false;
                }
                
                hashTmpData.put("MESSAGE", strMessage);
                if (boolFlag) 
                {
                    hashSuccessData.put(String.valueOf(hashSuccessData.size()), hashTmpData);
                } 
                else 
                {
                    hashFailData.put(String.valueOf(hashFailData.size()), hashTmpData);
                }
            }
        }
        
        hashData.put("Success",hashSuccessData);
        hashData.put("Fail",hashFailData);
        
        return hashData;
    }

//    /**
//     * 取得請款取消列表資料(Capture)
//     * @param String ROWID     ROWID
//     * @return ArrayList       列表資料
//     */
//     
//    public ArrayList get_CaptureVoid_List(String ROWID) 
//    {
//        ArrayList arrayCaptureData = new ArrayList(); // 顯示訂單交易資料
//        DataBaseBean SysBean = new DataBaseBean();
//        String arrayROWID[] = ROWID.split(",");
//    
//        if (ROWID.length() > 0 ) 
//        {
//            StringBuffer Sql = new StringBuffer("SELECT ROWIDTOCHAR(A.ROWID) AS RROWID, A.MERCHANTID, A.SUBMID, A.TERMINALID, A.ACQUIRERID, A.ORDERID, A.SYS_ORDERID, A.CARD_TYPE, A.PAN, A.EXPIREDATE, A.TRANSCODE, A.TRANSDATE, A.TRANSTIME, A.APPROVECODE, A.RESPONSECODE, A.RESPONSEMSG, A.CURRENCYCODE, A.CAPTUREAMT, TO_CHAR(A.CAPTUREDATE, 'YYYY/MM/DD HH24:MI:SS') AS CAPTUREDATE, A.USERDEFINE, A.BATCHNO, A.CAPTUREFLAG, TO_CHAR(A.PROCESSDATE,'YYYY/MM/DD HH24:MI:SS') AS PROCESSDATE, A.ENTRY_MODE, A.CONDITION_CODE, A.ECI, A.CAVV, A.TRANSMODE, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.EXTENNO, A.RRN, A.MTI, A.XID, A.SOCIALID, A.BILLMESSAGE, A.FEEDBACKCODE, A.FEEDBACKMSG, TO_CHAR(A.FEEDBACKDATE,  'YYYY/MM/DD HH24:MI:SS') AS FEEDBACKDATE, TO_CHAR(A.DUE_DATE, 'YYYY/MM/DD HH24:MI:SS') AS DUE_DATE, A.TRANSAMT, A.SYSTRACENO , B.AUTHAMT, B.REFUNDAMT, B.CAPTUREAMT AS BALCAPTUREAMT , B.REFUNDCAPTUREAMT, A.REAUTH_FLAG, A.EXCEPT_FLAG   FROM CAPTURE A, BALANCE B WHERE A.MERCHANTID = B.MERCHANTID AND A.SUBMID = B.SUBMID AND A.ORDERID = B.ORDERID ");
//            Sql.append(" AND A.ROWID IN (" );
//            for (int i=0; i<arrayROWID.length; ++i) 
//            {
//                if (i > 0) 
//                {
//                    Sql.append(", ");
//                }
//                
//                Sql.append("'" + arrayROWID[i] + "' ");
//            }
//            
//            Sql.append(") ");
//            Sql.append(" ORDER BY A.TRANSDATE, A.TRANSTIME ");
//            // System.out.println("Sql=" + Sql);
//            
//            try 
//            {
//                arrayCaptureData = (ArrayList) SysBean.executeSQL(Sql.toString(), "select");
//            } 
//            catch (Exception ex) 
//            {
//                System.out.println(ex.getMessage());
//                log_systeminfo.debug(ex.toString());
//            }
//            
//            if (arrayCaptureData == null) 
//            {
//                arrayCaptureData = new ArrayList();
//            }
//        }
//        
//        return arrayCaptureData;
//    }

    /* Override get_CaptureVoid_List with DataBaseBean parameter */
    public ArrayList get_CaptureVoid_List(DataBaseBean2 sysBean, String ROWID) 
    {
        ArrayList arrayCaptureData = new ArrayList(); // 顯示訂單交易資料
        // DataBaseBean SysBean = new DataBaseBean();
        String arrayROWID[] = ROWID.split(",");
    
        if (ROWID.length() > 0 ) 
        {
        	//20220210 ADD AUTH_SRC_CODE
        	StringBuffer sSQLSB = new StringBuffer();
            sysBean.ClearSQLParam();
    		sSQLSB.append("SELECT ROWIDTOCHAR(A.ROWID) AS RROWID, A.MERCHANTID, A.SUBMID, A.TERMINALID, A.ACQUIRERID, A.ORDERID, A.SYS_ORDERID, A.CARD_TYPE, A.PAN, A.EXPIREDATE, A.TRANSCODE, A.TRANSDATE, A.TRANSTIME, A.APPROVECODE, A.RESPONSECODE, A.RESPONSEMSG, A.CURRENCYCODE, A.CAPTUREAMT, TO_CHAR(A.CAPTUREDATE, 'YYYY/MM/DD HH24:MI:SS') AS CAPTUREDATE, A.USERDEFINE, A.BATCHNO, A.CAPTUREFLAG, TO_CHAR(A.PROCESSDATE,'YYYY/MM/DD HH24:MI:SS') AS PROCESSDATE, A.ENTRY_MODE, A.CONDITION_CODE, A.ECI, A.CAVV, A.TRANSMODE, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.EXTENNO, A.RRN, A.MTI, A.XID, A.SOCIALID, A.BILLMESSAGE, A.FEEDBACKCODE, A.FEEDBACKMSG, TO_CHAR(A.FEEDBACKDATE,  'YYYY/MM/DD HH24:MI:SS') AS FEEDBACKDATE, TO_CHAR(A.DUE_DATE, 'YYYY/MM/DD HH24:MI:SS') AS DUE_DATE, A.TRANSAMT, A.SYSTRACENO , B.AUTHAMT, B.REFUNDAMT, B.CAPTUREAMT AS BALCAPTUREAMT , B.REFUNDCAPTUREAMT, A.REAUTH_FLAG, A.EXCEPT_FLAG, A.AUTH_SRC_CODE FROM CAPTURE A, BALANCE B WHERE A.MERCHANTID = B.MERCHANTID AND A.SUBMID = B.SUBMID AND A.ORDERID = B.ORDERID ");
            sSQLSB.append(" AND A.ROWID IN (" );
            for (int i=0; i<arrayROWID.length; ++i) 
            {
                if (i > 0) 
                {
                    sSQLSB.append(", ");
                }
                
                sSQLSB.append(" ? ");
                sysBean.AddSQLParam(emDataType.STR, arrayROWID[i]);
            }
            
            sSQLSB.append(") ");
            sSQLSB.append(" ORDER BY A.TRANSDATE, A.TRANSTIME ");
            // System.out.println("Sql=" + Sql);
            
            try 
            {
            	/** 2023/05/10 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-035 */
                arrayCaptureData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
            } 
            catch (Exception ex) 
            {
                System.out.println(ex.getMessage());
                log_systeminfo.debug(ex.toString());
            }
            
            if (arrayCaptureData == null) 
            {
                arrayCaptureData = new ArrayList();
            }
        }
        
        return arrayCaptureData;
    }
    
    /**
     * 判斷是否允許購貨執行取消
     * @param ArrayList arrayBillingData 取消列表資料
     * @param String    strCancelAmt     取消金額
     * @return Hashtable                 判斷結果 FLAG:退貨判斷(String)  MESSAGE:顯示結果(String)
     */
    public Hashtable check_SaleVoid_Action_Status(ArrayList arrayBillingData, String strCancelAmt) 
    {
        Hashtable hashData = new Hashtable();
        boolean boolFlag = false; // 是否能做退貨
        String strMessage = "查無交易資料"; // 顯示結果
        
        if (arrayBillingData.size() > 0) 
        {
            for (int c = 0; c < arrayBillingData.size(); ++c) 
            {
                Hashtable hashTmpData = (Hashtable) arrayBillingData.get(c);
                String TransCode = hashTmpData.get("TRANSCODE").toString();
                String TransAmt = hashTmpData.get("TRANSAMT").toString();
                String BalanceAmt = hashTmpData.get("BALANCEAMT").toString();
                String AuthAmt = hashTmpData.get("AUTHAMT").toString(); // 授權金額
                String RefundAmt = hashTmpData.get("REFUNDAMT").toString(); // 退貨金額
                String CancelAmt = hashTmpData.get("CANCELAMT").toString(); // 購貨取消貨金額
                String CaptureAmt = hashTmpData.get("CAPTUREAMT").toString(); // 請款金額
                
                if (TransCode.length() > 0 && CancelAmt.length() > 0 && AuthAmt.length() > 0 && RefundAmt.length() > 0 &&
                    CancelAmt.length() > 0 && CaptureAmt.length() > 0 ) 
                {
                    if (TransCode.equalsIgnoreCase("00")) 
                    { 
                        // 購貨交易
                        boolFlag = true;
                        if (strCancelAmt.length() > 0) 
                        {
                            if ((Double.parseDouble(AuthAmt) - Double.parseDouble(RefundAmt) -
                                 Double.parseDouble(CancelAmt) - Double.parseDouble(CaptureAmt)) !=
                                 Double.parseDouble(strCancelAmt)) 
                            {
                                strMessage = "交易金額不符";
                                boolFlag = false;
                            }
                            
                            if (Double.parseDouble(strCancelAmt) != Double.parseDouble(TransAmt)) 
                            {
                                strMessage = "交易金額不符";
                                boolFlag = false;
                            }
                        }
                        
                        if (Double.parseDouble(BalanceAmt) != Double.parseDouble(TransAmt)) 
                        {
                            strMessage = "交易金額不符";
                            boolFlag = false;
                        }
                        break;
                    }
                } 
                else 
                {
                    strMessage = "查無交易資料";
                }
            }
        }
        
        hashData.put("FLAG", String.valueOf(boolFlag));
        hashData.put("MESSAGE", strMessage);
        
        return hashData;
    }

    /**
     * 判斷是否允許退貨執行取消
     * @param ArrayList arrayBillingData 取消列表資料
     * @param String    Sys_OrderID      取消系統指定單號
     * @param String    strCancelAmt     取消金額
     * @return Hashtable                 判斷結果 FLAG:退貨判斷(String)  MESSAGE:顯示結果(String)
     */
    public Hashtable check_RefundVoid_Action_Status(ArrayList arrayBillingData, String strCancelAmt) 
    {
        Hashtable hashData = new Hashtable();
        boolean boolFlag = false; // 是否能做退貨
        String strMessage = "查無交易資料"; // 顯示結果
        int TotalCount = 0;
    
        if (arrayBillingData.size() > 0) 
        {
            for (int c = 0; c < arrayBillingData.size(); ++c) 
            {
                Hashtable hashTmpData = (Hashtable) arrayBillingData.get(c);
                String TransCode = hashTmpData.get("TRANSCODE").toString();
                String TransAmt = hashTmpData.get("TRANSAMT").toString();
                String BalanceAmt = hashTmpData.get("BALANCEAMT").toString();
                String AuthAmt = hashTmpData.get("AUTHAMT").toString(); // 授權金額
                String RefundAmt = hashTmpData.get("REFUNDAMT").toString(); // 退貨金額
                String CancelAmt = hashTmpData.get("CANCELAMT").toString(); // 購貨取消貨金額
                String CaptureAmt = hashTmpData.get("CAPTUREAMT").toString(); // 請款金額
                
                if (TransCode.length() > 0 && CancelAmt.length() > 0 && AuthAmt.length() > 0 && RefundAmt.length() > 0 &&
                    CancelAmt.length() > 0 && CaptureAmt.length() > 0 ) 
                {
                    if (TransCode.equalsIgnoreCase("01") && Double.parseDouble(BalanceAmt) >0) 
                    { 
                        // 退貨交易
                        if (Double.parseDouble(RefundAmt) <= 0) 
                        {
                            strMessage = "交易金額不符，未做退貨交易";
                        } 
                        else 
                        {
                            if (strCancelAmt.length() > 0) 
                            {
                                if (Double.parseDouble(strCancelAmt) != Double.parseDouble(TransAmt)) 
                                {
                                    strMessage = "交易金額不符";
                                }
                                
                                if (Double.parseDouble(BalanceAmt) != Double.parseDouble(TransAmt)) 
                                {
                                    strMessage = "交易金額不符";
                                } 
                                else 
                                {
                                    TotalCount++;
                                }
                            }
                        }
                    }
                } 
                else 
                {
                    strMessage = "查無退貨交易資料";
                }
            }
            
            if (TotalCount==0)
            {
                strMessage = "查無退貨交易資料";
            } 
            else 
            {
                boolFlag = true;
                strMessage = "";
            }
        }

        hashData.put("FLAG", String.valueOf(boolFlag));
        hashData.put("MESSAGE", strMessage);
        return hashData;
    }

    /**
     * 更新購貨/退貨取消可請款金額 (Billing)
     * @param  DataBaseBean SysBean 	資料庫
     * @param  String MerchantID 	被取消交易的特店代號
     * @param  String SubMID	        被取消交易的服務代號
     * @param  String Sys_OrderID	被取消交易的系統指定單號
     * @param  String TransCode	        被取消交易的交易代碼  00:購貨 01:退貨
     * @return boolean                  更新結果
     */
    public boolean update_BillingeVoid(DataBaseBean2 sysBean, String MerchantID, String SubMID, String Sys_OrderID, String TransCode) 
    {
        boolean Flag = false;
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("UPDATE BILLING SET BALANCEAMT = (SELECT TRANSAMT * (-1) FROM BILLING WHERE MERCHANTID = ? AND SUBMID = ? AND SYS_ORDERID = ? AND TRANSCODE = ? ) ");
		
		sysBean.AddSQLParam(emDataType.STR, MerchantID);
		sysBean.AddSQLParam(emDataType.STR, SubMID);
		sysBean.AddSQLParam(emDataType.STR, Sys_OrderID);
		sysBean.AddSQLParam(emDataType.STR, TransCode);
		
		sSQLSB.append(" WHERE MERCHANTID = ? AND SUBMID = ? AND SYS_ORDERID = ? AND TRANSCODE = ? ");
		
		sysBean.AddSQLParam(emDataType.STR, MerchantID);
		sysBean.AddSQLParam(emDataType.STR, SubMID);
		sysBean.AddSQLParam(emDataType.STR, Sys_OrderID);
		sysBean.AddSQLParam(emDataType.STR, TransCode);
		
        // System.out.println("Sql=" + Sql);
        
		ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/05/11 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-037 */
        	arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }

        System.out.println("[update_BillingeVoid]_arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
            Flag = true;
        }        
        
        return Flag;
    }

    /**
     * 更新請款取消資料 (Capture)
     * @param  DataBaseBean SysBean 	資料庫
     * @param  String ROWID             被取消請款資料ROWID
     * @param  String CaptureFlag       請款控制旗標
     * @param  String FEEBackCode       請款回覆碼
     * @param  String FEEBackMsg        請款回覆訊息
     * @param  String FEEBackDate       請款回覆日期
     * @return boolean                  更新結果
     */
    public boolean update_CaptureVoid(DataBaseBean2 sysBean, String ROWID, String CaptureFlag , String FEEBackCode, String FEEBackMsg, String FEEBackDate) 
    {
        boolean Flag = false;
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("UPDATE CAPTURE SET CAPTUREFLAG = ? , FEEDBACKCODE = ? , FEEDBACKMSG = ? ,FEEDBACKDATE = TO_DATE( ?, 'YYYY/MM/DD HH24:MI:SS') ");
		sSQLSB.append(" WHERE ROWID = ? ");
		
		sysBean.AddSQLParam(emDataType.STR, CaptureFlag);
		sysBean.AddSQLParam(emDataType.STR, FEEBackCode);
		sysBean.AddSQLParam(emDataType.STR, FEEBackMsg);
		sysBean.AddSQLParam(emDataType.STR, FEEBackDate);
		sysBean.AddSQLParam(emDataType.STR, ROWID);
		
        //System.out.println("Sql=" + Sql);
        
		ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/05/10 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-038 */
        	arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }

        System.out.println("[update_CaptureVoid]_arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
            Flag = true;
        }
        
        return Flag;
    }

    /**
     * 更新請款取消帳單可請款金額 (Billing)
     * @param  DataBaseBean SysBean 	資料庫
     * @param  String MerchantID 	被取消交易的特店代號
     * @param  String SubMID	        被取消交易的服務代號
     * @param  String Sys_OrderID	被取消交易的系統指定單號
     * @param  String CaptureAmt	回覆金額
     * @return boolean                  更新結果
     */
    public boolean update_BillingeCaptureVoid(DataBaseBean2 sysBean, String MerchantID, String SubMID, String Sys_OrderID, String CaptureAmt) 
    {
        boolean Flag = false;
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("UPDATE BILLING SET BALANCEAMT = (SELECT BALANCEAMT + ? FROM BILLING WHERE MERCHANTID = ? AND SUBMID = ? AND SYS_ORDERID = ? ) ");
		sSQLSB.append(" WHERE MERCHANTID = ? AND SUBMID = ? AND SYS_ORDERID = ? ");
		
		sysBean.AddSQLParam(emDataType.INT, CaptureAmt);
		sysBean.AddSQLParam(emDataType.STR, MerchantID);
		sysBean.AddSQLParam(emDataType.STR, SubMID);
		sysBean.AddSQLParam(emDataType.STR, Sys_OrderID);
		sysBean.AddSQLParam(emDataType.STR, MerchantID);
		sysBean.AddSQLParam(emDataType.STR, SubMID);
		sysBean.AddSQLParam(emDataType.STR, Sys_OrderID);
		
        // System.out.println("Sql=" + Sql);
        
		ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/05/11 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-039 */
        	arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }

        System.out.println("[update_BillingeCaptureVoid]_arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
            Flag = true;
        }
        
        return Flag;
    }
}
