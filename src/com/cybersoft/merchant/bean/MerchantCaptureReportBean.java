package com.cybersoft.merchant.bean;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import java.util.Hashtable;
import java.security.MessageDigest;
import java.util.*;
import java.sql.*;
import com.cybersoft.bean.LogUtils;
import com.cybersoft.bean.emDataType;
import com.fubon.security.filter.SecurityTool;

public class MerchantCaptureReportBean
{
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    private  DataBaseBean2 sysBean = new DataBaseBean2();
    public MerchantCaptureReportBean()
    {
    }

//    /**
//     * 取得請款查詢列表資料(Capture+Merchant)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店代號
//     * @param String StartTransDate 查詢起日
//     * @param String EndTransDate   查詢迄日
//     * @param String TransCode      交易類別
//     * @param String FeedBackCode    請款結果
//     * @return ResultSet            請款資料
//     */
//    public ResultSet get_Capture_Report_rs(String MerchantID, String SubMID, String StartTransDate, String EndTransDate, String TransCode, String FeedBackCode)
//    {
//        SysBean = new DataBaseBean();
//
//        StringBuffer Sql = new StringBuffer("SELECT A.MERCHANTID, A.SUBMID, A.TERMINALID, A.ACQUIRERID, A.ORDERID, A.SYS_ORDERID, A.CARD_TYPE, A.PAN, A.EXPIREDATE, A.TRANSCODE, A.TRANSDATE, A.TRANSTIME, A.APPROVECODE, A.RESPONSECODE, A.RESPONSEMSG, A.CURRENCYCODE, A.CAPTUREAMT, TO_CHAR(A.CAPTUREDATE,'yyyy/mm/dd') CAPTUREDATE, A.USERDEFINE, A.BATCHNO, A.CAPTUREFLAG, A.PROCESSDATE, A.ENTRY_MODE, A.CONDITION_CODE, A.ECI, A.CAVV, A.TRANSMODE, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE, A.FEEDBACKCODE, A.FEEDBACKMSG, A.FEEDBACKDATE, A.DUE_DATE, A.TRANSAMT, A.SYSTRACENO , B.MERCHANTCALLNAME  FROM  CAPTURE A , MERCHANT B WHERE");
//        Sql.append( " A.MERCHANTID = '" + MerchantID + "' AND  A.SUBMID = B.SUBMID  ");
//        //new add 歐付寶
//        if(SubMID != null  && !SubMID.equals("all")){
//     	   Sql.append( " AND A.SUBMID = '" + SubMID + "'  ");
//        }
//        if (StartTransDate != null && EndTransDate != null && StartTransDate.length() > 0 && EndTransDate.length() > 0)
//        {
//           Sql.append(" AND TO_CHAR(A.CAPTUREDATE, 'YYYYMMDD') BETWEEN '" + StartTransDate + "' AND '" + EndTransDate + "'");
//        }
//
//        if (TransCode!=null&&TransCode.length() > 0 && TransCode.equalsIgnoreCase("CANCEL"))
//        {
//           Sql.append(" AND A.TRANSCODE IN ('20','21') ");
//        }
//        else
//        {
//           Sql.append(" AND A.TRANSCODE IN ('00','01') AND CAPTUREFLAG <> '2' ");
//        }
//
//        if (FeedBackCode!=null&&FeedBackCode.length() > 0 && !FeedBackCode.equalsIgnoreCase("ALL"))
//        {
//           if (FeedBackCode.equalsIgnoreCase("APPROVE"))
//           {
//              Sql.append(" AND CAPTUREFLAG = '3' AND A.FEEDBACKCODE = '000' ");
//           }
//
//           if (FeedBackCode.equalsIgnoreCase("REJECT"))
//           {
//              Sql.append(" AND CAPTUREFLAG = '3' AND A.FEEDBACKCODE <> '000' AND A.FEEDBACKCODE <> 'CAN' ");
//           }
//
//           if (FeedBackCode.equalsIgnoreCase("PROCESS"))
//           {
//            // 處理中
//              Sql.append(" AND CAPTUREFLAG = '1' ");
//           }
//        }
//
//       Sql.append(" AND A.MERCHANTID = B.MERCHANTID  Order by A.TRANSDATE ,A.TRANSTIME ");
//        // System.out.println("Sql=" + Sql);
//        ResultSet arrayData = null;
//
//        try
//        {
//            arrayData = (ResultSet) SysBean.executeReportSQL(SecurityTool.output(Sql.toString()), "select");
//        }
//        catch (Exception ex)
//        {
//            log_systeminfo.debug(ex.toString());
//            System.out.println(ex.getMessage());
//        }
//
//        return arrayData;
//    }

    /* Override get_Capture_Report_rs with DataBaseBean parameter */
    // get_Capture_Report_rs 新增引數 TransType by Jimmy Kang 20150803
    // 請款交易統計報表 新增 查詢條件 TransType by Jimmy Kang 20150803 -- 修改開始 --
    public String get_Capture_Report_rs(DataBaseBean2 sysBean, String MerchantID, String SubMID, String StartTransDate, String EndTransDate, String TransCode, String FeedBackCode, String TransType)
    // 請款交易統計報表 新增 查詢條件 TransType by Jimmy Kang 20150803 -- 修改結束 --
    {
        // SysBean = new DataBaseBean();

//        StringBuffer Sql = new StringBuffer("SELECT A.MERCHANTID, A.SUBMID, A.TERMINALID, A.ACQUIRERID, A.ORDERID, A.SYS_ORDERID, A.CARD_TYPE, A.PAN, A.EXPIREDATE, A.TRANSCODE, A.TRANSDATE, A.TRANSTIME, A.APPROVECODE, A.RESPONSECODE, A.RESPONSEMSG, A.CURRENCYCODE, A.CAPTUREAMT, TO_CHAR(A.CAPTUREDATE,'yyyy/mm/dd') CAPTUREDATE, A.USERDEFINE, A.BATCHNO, A.CAPTUREFLAG, A.PROCESSDATE, A.ENTRY_MODE, A.CONDITION_CODE, A.ECI, A.CAVV, A.TRANSMODE, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE, A.FEEDBACKCODE, A.FEEDBACKMSG, A.FEEDBACKDATE, A.DUE_DATE, A.TRANSAMT, A.SYSTRACENO , B.MERCHANTCALLNAME  FROM  CAPTURE A , MERCHANT B WHERE");
    	
    	// 請款交易統計報表 修改SQL & 搜尋條件新增TransType 
    	// 但是因為Capture表格裡面沒有 TransType欄位, 因此必須JOIN Billing表格
    	// 請款交易統計報表 新增 查詢條件 TransType by Jimmy Kang 20150803 -- 修改開始 --
    	//StringBuffer Sql = new StringBuffer("SELECT A.MERCHANTID, A.SUBMID, A.TERMINALID,  A.TRANSCODE,SUM(A.CAPTUREAMT) CAPTUREAMT, TO_CHAR(A.CAPTUREDATE,'yyyy/mm/dd') CAPTUREDATE, A.FEEDBACKCODE, B.MERCHANTCALLNAME  ,COUNT(*) CNT  FROM  CAPTURE A , MERCHANT B WHERE");  // 註解掉 by Jimmy Kang 20150727
    	StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append(" SELECT A.MERCHANTID, A.SUBMID, A.TERMINALID,  A.TRANSCODE,SUM(A.CAPTUREAMT) CAPTUREAMT, TO_CHAR(A.CAPTUREDATE,'yyyy/mm/dd') CAPTUREDATE, A.FEEDBACKCODE, B.MERCHANTCALLNAME  ,COUNT(*) CNT ");
		sSQLSB.append(" FROM  CAPTURE A, MERCHANT B, BILLING C ");
		sSQLSB.append(" WHERE A.MERCHANTID = C.MERCHANTID AND A.SYS_ORDERID = C.SYS_ORDERID AND CASE A.TRANSCODE WHEN '20' THEN '00' WHEN '21' THEN '01' ELSE A.TRANSCODE END = C.TRANSCODE AND A.TRANSAMT = C.TRANSAMT AND ");
    	// 請款交易統計報表 新增 查詢條件 TransType by Jimmy Kang 20150803 -- 修改結束 --
    	
        sSQLSB.append(" A.MERCHANTID = ? AND  A.SUBMID = B.SUBMID  ");
        sysBean.AddSQLParam(emDataType.STR, MerchantID);
        
        //new add 歐付寶
        if(SubMID != null  && !SubMID.equals("all")){
     	   sSQLSB.append(" AND A.SUBMID = ?  ");
     	  sysBean.AddSQLParam(emDataType.STR, SubMID);
        }
        if (StartTransDate != null && EndTransDate != null && StartTransDate.length() > 0 && EndTransDate.length() > 0)
        {
           sSQLSB.append(" AND TO_CHAR(A.CAPTUREDATE, 'YYYYMMDD') BETWEEN ? AND ? ");
           sysBean.AddSQLParam(emDataType.STR, StartTransDate);
           sysBean.AddSQLParam(emDataType.STR, EndTransDate);
        }

        if (TransCode!=null&&TransCode.length() > 0 && TransCode.equalsIgnoreCase("CANCEL"))
        {
           sSQLSB.append(" AND A.TRANSCODE IN ('20','21') ");
        }
        else
        {
           sSQLSB.append(" AND A.TRANSCODE IN ('00','01') AND CAPTUREFLAG <> '2' ");
        }
        
        // 請款交易統計報表 新增 查詢條件 TransType by Jimmy Kang 20150803 -- 修改開始 --
        if (TransType!=null&&TransType.length() > 0 && !TransType.equalsIgnoreCase("ALL")) 
        {
        	if (TransType.equalsIgnoreCase("VMJ"))
        	{
        		sSQLSB.append(" AND C.TRANSTYPE IN ('V3D', 'M3D', 'J3D') ");
        	}
        	else
        	{
        		sSQLSB.append(" AND C.TRANSTYPE = ? ");
        		sysBean.AddSQLParam(emDataType.STR, TransType);
        	}
        }
        // 請款交易統計報表 新增 查詢條件 TransType by Jimmy Kang 20150803 -- 修改結束 --

        if (FeedBackCode!=null&&FeedBackCode.length() > 0 && !FeedBackCode.equalsIgnoreCase("ALL"))
        {
           if (FeedBackCode.equalsIgnoreCase("APPROVE"))
           {
              sSQLSB.append(" AND CAPTUREFLAG = '3' AND A.FEEDBACKCODE = '000' ");
           }

           if (FeedBackCode.equalsIgnoreCase("REJECT"))
           {
              sSQLSB.append(" AND CAPTUREFLAG = '3' AND A.FEEDBACKCODE <> '000' AND A.FEEDBACKCODE <> 'CAN' ");
           }

           if (FeedBackCode.equalsIgnoreCase("PROCESS"))
           {
            // 處理中
              sSQLSB.append(" AND CAPTUREFLAG = '1' ");
           }
        }

       sSQLSB.append(" AND A.MERCHANTID = B.MERCHANTID  GROUP BY A.MERCHANTID, A.SUBMID,  A.TERMINALID,    A.TRANSCODE,  TO_CHAR(A.CAPTUREDATE,'yyyy/mm/dd'),   A.FEEDBACKCODE,    B.MERCHANTCALLNAME   Order by CAPTUREDATE ");
        // System.out.println("Sql=" + Sql);
//        ResultSet arrayData = null;
//
//        try
//        {
//            /** 2023/05/15 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-049 */
//            arrayData = (ResultSet) SysBean.executeReportSQL(Sql, "select");
//        }
//        catch (Exception ex)
//        {
//            log_systeminfo.debug(ex.toString());
//            System.out.println(ex.getMessage());
//        }

        return sSQLSB.toString();
    }

    public void closeConn(){
        try {
            sysBean.close();
        }
        catch (SQLException ex) {
        }
    }
}
