package com.cybersoft.merchant.bean;

import java.util.ArrayList;
/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import java.util.Hashtable;
import java.sql.*;
import com.cybersoft.bean.LogUtils;
import com.cybersoft.bean.emDataType;
import com.fubon.security.filter.SecurityTool;
public class MerchantCaptureLogBean
{
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    private DataBaseBean2 sysBean;
    public MerchantCaptureLogBean()
    {
    }

//    /**
//     * 取得請款記錄查詢列表資料(CaptureLog)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         服務代號
//     * @param String StartTransDate 查詢起日
//     * @param String EndTransDate   查詢迄日
//     * @param String TransCode      交易類別
//     * @param String OrderType      查詢訂單類別 (Order:特店指定單號 System:系統指定單號 )
//     * @param String OrderID        訂單代號
//     * @param String ApproveCode    授權碼
//     * @param String TerminalID     端末機代碼
//     * @param String CaptureType    請款狀態 (ALL:全部  NOTCAPTURE:未請款  CAPTURE:請款完畢 )
//     * @param String ExceptFlag     請款方式 (ALL:全部  MERCHANT:特店請款  ACQ:收單行補請款 )
//     * @return ArrayList            請款記錄資料
//     */
//    public ArrayList get_CaptureLog_List(String MerchantID, String SubMID, String StartTransDate,
//                                      String EndTransDate, String TransCode, String OrderType,
//                                      String OrderID, String ApproveCode, String TerminalID,
//                                      String CaptureType, String ExceptFlag,String type)
//    {
//        SysBean = new DataBaseBean();
//    	StringBuffer Sql = new StringBuffer("");
//    	if(type.equals("TotalNet")){
//    		Sql.append( "SELECT count(*) COUNT,MERCHANTID,TRANSCODE , TRANSDATE, TRANSTIME, sum(CAPTUREAMT ) CAPTUREAMT, TRANSMODE ,FEEDBACKCODE, CAPTUREFLAG        FROM  CAPTURE   WHERE ");
//       
//    	}else{
//    		Sql.append( "SELECT ROWIDTOCHAR(rowid) ROWID1,MERCHANTID, SUBMID, TRANSDATE, TRANSTIME, TERMINALID, ORDERID , SYS_ORDERID , TRANSCODE, CURRENCYCODE , CAPTUREAMT ,APPROVECODE,RESPONSECODE,RESPONSEMSG,BATCHNO,"+
//                     " TRANSMODE ,TO_CHAR(CAPTUREDATE,'YYYY/MM/DD HH24:MI:SS') CAPTUREDATE,ECI,FEEDBACKCODE,TO_CHAR(FEEDBACKDATE,'YYYY/MM/DD HH24:MI:SS') FEEDBACKDATE, FEEDBACKMSG, CAPTUREFLAG, EXCEPT_FLAG, RECAPTURE_USERID, RECAPTURE_STATUS, REAUTH_FLAG, AUTOCAPTURE_FLAG        FROM  CAPTURE   WHERE ");
//    	}
//       Sql.append( "MERCHANTID = '" + MerchantID + "' AND SUBMID = '" + SubMID + "'");
//        if (StartTransDate != null && EndTransDate != null && StartTransDate.length() > 0 && EndTransDate.length() > 0)
//        {
//            StartTransDate = StartTransDate.replaceAll("/", "");
//            StartTransDate = StartTransDate.replaceAll("-", "");
//            EndTransDate = EndTransDate.replaceAll("/", "");
//            EndTransDate = EndTransDate.replaceAll("-", "");
//           Sql.append( " AND TO_CHAR(CAPTUREDATE,'YYYYMMDD')  BETWEEN '" + StartTransDate + "' AND '" + EndTransDate + "'");
//        }
//
//        if (TransCode != null && TransCode.length() > 0 && !TransCode.equalsIgnoreCase("ALL") && !TransCode.equalsIgnoreCase("CANCEL") )
//        {
//           Sql.append( " AND TRANSCODE in ('" + TransCode + "') ");
//        }
//        else
//        {
//            if(TransCode.equalsIgnoreCase("ALL"))
//            {
//               Sql.append( " AND TRANSCODE in ('00','01') ");
//
//            }
//            else
//            {
//                if(TransCode.equalsIgnoreCase("CANCEL"))
//                {
//                   Sql.append( " AND TRANSCODE in ('20','21') ");
//                }
//            }
//        }
//
//
//        if (OrderID!=null&&OrderID.length() > 0)
//        {
//            if (OrderType.equalsIgnoreCase("M"))
//            {
//                // 以OrderID
//               Sql.append( " AND ORDERID = '" + OrderID + "' ");
//            }
//            else
//            {
//               Sql.append( " AND SYS_ORDERID = '" + OrderID + "' ");
//            }
//        }
//
//        if (ApproveCode!=null&&ApproveCode.length() > 0)
//        {
//           Sql.append( " AND TRIM(APPROVECODE) = '" + ApproveCode + "' ");
//        }
//
//        if (TerminalID!=null&&TerminalID.length() > 0&& !TerminalID.equalsIgnoreCase("ALL"))
//        {
//           Sql.append( " AND TERMINALID = '" + TerminalID + "' ");
//        }
//
//        if (CaptureType.length() > 0 && !CaptureType.equalsIgnoreCase("ALL"))
//        {
//            if (CaptureType.equalsIgnoreCase("CAPTURE"))
//            { // 完成請款
//               Sql.append( " AND CAPTUREFLAG = '3' AND FEEDBACKCODE = '000' ");
//            }
//
//            if (CaptureType.equalsIgnoreCase("NOTCAPTURE"))
//            { // 未請款
//               Sql.append( " AND CAPTUREFLAG = '3' AND FEEDBACKCODE <> '000' AND A.FEEDBACKCODE <> 'CAN' ");
//            }
//
//            if (CaptureType.equalsIgnoreCase("PROCESS"))
//            { // 處理中
//               Sql.append( " AND CAPTUREFLAG = '1' ");
//            }
//        }
//
//        if (ExceptFlag.length()>0 && !ExceptFlag.equalsIgnoreCase("ALL"))
//        {
//            if (ExceptFlag.equalsIgnoreCase("ACQ"))
//            {
//               Sql.append( " AND EXCEPT_FLAG = 'Y' ");
//            }
//
//            if (ExceptFlag.equalsIgnoreCase("MERCHANT"))
//            {
//               Sql.append( " AND EXCEPT_FLAG IS NULL ");
//            }
//        }
//        if(type.equals("TotalNet")){
//    		Sql.append( "GROUP BY MERCHANTID,TRANSCODE,TRANSDATE,TRANSTIME, CAPTUREAMT,TRANSMODE,TO_CHAR(CAPTUREDATE,'YYYY/MM/DD'),CAPTUREFLAG,FEEDBACKCODE   ");
//    	}
//       Sql.append( " Order by TRANSDATE ,TRANSTIME ");
//        // System.out.println("Sql=" + Sql);
//        ArrayList arrayData = new ArrayList();
//
//        try
//        {
//            arrayData = (ArrayList) sysBean.executeSQL(Sql.toString(), "select");
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//
//        if (arrayData == null)
//            arrayData = new ArrayList();
//
//        return arrayData;
//    }

    /* Override get_CaptureLog_List with DataBaseBean parameter */
    // get_CaptureLog_List method 新增引數 TransType by Jimmy Kang 20150727
    // 請款交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150727 -- 修改開始 --
    public ArrayList get_CaptureLog_List(DataBaseBean2 sysBean, String MerchantID, String SubMID, String StartTransDate,
                                      String EndTransDate, String TransCode, String TransType, String OrderType,
                                      String OrderID, String ApproveCode, String TerminalID,
                                      String CaptureType, String ExceptFlag,String type)
    // 請款交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150727 -- 修改結束 --
    {
        // SysBean = new DataBaseBean();
	//20130319 Jason 增加回傳 PAN
    	StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
    	
    	// 請款交易查詢 修改SQL & 搜尋條件新增TransType 
    	// 但是因為Capture表格裡面沒有 TransType欄位, 因此必須JOIN Billing表格 
    	// 請款交易查詢 by Jimmy Kang 20150727  -- 修改開始 --
    	// 註解掉 by Jimmy Kang 20150727
    	/*if(type.equals("TotalNet")){
    		sSQLSB.append("SELECT count(*) COUNT,MERCHANTID,TRANSCODE , TRANSDATE, TRANSTIME, sum(CAPTUREAMT ) CAPTUREAMT, TRANSMODE ,FEEDBACKCODE, CAPTUREFLAG        FROM  CAPTURE   WHERE ");
    	}else{
    		sSQLSB.append("SELECT ROWIDTOCHAR(rowid) ROWID1,MERCHANTID, SUBMID, PAN, TRANSDATE, TRANSTIME, TERMINALID, ORDERID , SYS_ORDERID , TRANSCODE, CURRENCYCODE , CAPTUREAMT ,APPROVECODE,RESPONSECODE,RESPONSEMSG,BATCHNO,"+
                    " TRANSMODE ,TO_CHAR(CAPTUREDATE,'YYYY/MM/DD HH24:MI:SS') CAPTUREDATE,ECI,FEEDBACKCODE,TO_CHAR(FEEDBACKDATE,'YYYY/MM/DD HH24:MI:SS') FEEDBACKDATE, FEEDBACKMSG, CAPTUREFLAG, EXCEPT_FLAG, RECAPTURE_USERID, RECAPTURE_STATUS, REAUTH_FLAG, AUTOCAPTURE_FLAG        FROM  CAPTURE   WHERE     ROWNUM < (SELECT parm_value FROM SYS_PARM_LIST WHERE PARM_ID ='MER_CAPTURE_QRY_QUANTITY')+2  AND ");
    	}
    	sSQLSB.append("MERCHANTID = '" + MerchantID + "'  ");
       
       if(!SubMID.equalsIgnoreCase("ALL")){
     	   sSQLSB.append("AND  SUBMID = '" + SubMID + "'  ");
        }
        if (StartTransDate != null && EndTransDate != null && StartTransDate.length() > 0 && EndTransDate.length() > 0)
        {
            StartTransDate = StartTransDate.replaceAll("/", "");
            StartTransDate = StartTransDate.replaceAll("-", "");
            EndTransDate = EndTransDate.replaceAll("/", "");
            EndTransDate = EndTransDate.replaceAll("-", "");
           sSQLSB.append(" AND TO_CHAR(CAPTUREDATE,'YYYYMMDD')  BETWEEN '" + StartTransDate + "' AND '" + EndTransDate + "'");
        }

        if (TransCode != null && TransCode.length() > 0 && !TransCode.equalsIgnoreCase("ALL") && !TransCode.equalsIgnoreCase("CANCEL") )
        {
           sSQLSB.append(" AND TRANSCODE in ('" + TransCode + "') ");
        }
        else
        {
            if(TransCode.equalsIgnoreCase("ALL"))
            {
               sSQLSB.append(" AND TRANSCODE in ('00','01') ");

            }
            else
            {
                if(TransCode.equalsIgnoreCase("CANCEL"))
                {
                   sSQLSB.append(" AND TRANSCODE in ('20','21') ");
                }
            }
        }

        if (OrderID!=null&&OrderID.length() > 0)
        {
            if (OrderType.equalsIgnoreCase("M"))
            {
                // 以OrderID
               sSQLSB.append(" AND ORDERID = '" + OrderID + "' ");
            }
            else
            {
               sSQLSB.append(" AND SYS_ORDERID = '" + OrderID + "' ");
            }
        }

        if (ApproveCode!=null&&ApproveCode.length() > 0)
        {
           sSQLSB.append(" AND TRIM(APPROVECODE) = '" + ApproveCode + "' ");
        }

        if (TerminalID!=null&&TerminalID.length() > 0&& !TerminalID.equalsIgnoreCase("ALL"))
        {
           sSQLSB.append(" AND TERMINALID = '" + TerminalID + "' ");
        }

        if (CaptureType.length() > 0 && !CaptureType.equalsIgnoreCase("ALL"))
        {
            if (CaptureType.equalsIgnoreCase("CAPTURE"))
            { // 完成請款
               sSQLSB.append(" AND CAPTUREFLAG = '3' AND FEEDBACKCODE = '000' ");
            }

            if (CaptureType.equalsIgnoreCase("NOTCAPTURE"))
            { // 未請款
               sSQLSB.append(" AND CAPTUREFLAG = '3' AND FEEDBACKCODE <> '000' AND A.FEEDBACKCODE <> 'CAN' ");
            }

            if (CaptureType.equalsIgnoreCase("PROCESS"))
            { // 處理中
               sSQLSB.append(" AND CAPTUREFLAG = '1' ");
            }
        }

        if (ExceptFlag.length()>0 && !ExceptFlag.equalsIgnoreCase("ALL"))
        {
            if (ExceptFlag.equalsIgnoreCase("ACQ"))
            {
               sSQLSB.append(" AND EXCEPT_FLAG = 'Y' ");
            }

            if (ExceptFlag.equalsIgnoreCase("MERCHANT"))
            {
               sSQLSB.append(" AND EXCEPT_FLAG IS NULL ");
            }
        }
        if(type.equals("TotalNet")){
    		sSQLSB.append("GROUP BY MERCHANTID,CAPTUREAMT,CAPTUREFLAG,FEEDBACKCODE,TRANSCODE,TRANSMODE,TRANSTIME,TRANSDATE ");
    	}
        sSQLSB.append(" Order by TRANSDATE ,TRANSTIME ");*/
    	
    	if(type.equals("TotalNet")){
    		sSQLSB.append(" SELECT count(*) COUNT, A.MERCHANTID, A.TRANSCODE , A.TRANSDATE, A.TRANSTIME, sum(A.CAPTUREAMT) CAPTUREAMT, A.TRANSMODE, A.FEEDBACKCODE, A.CAPTUREFLAG ");
    		sSQLSB.append(" FROM  CAPTURE A, BILLING B  WHERE A.MERCHANTID = B.MERCHANTID AND A.SYS_ORDERID = B.SYS_ORDERID AND CASE A.TRANSCODE WHEN '20' THEN '00' WHEN '21' THEN '01' ELSE A.TRANSCODE END = B.TRANSCODE AND A.TRANSAMT = B.TRANSAMT AND ");
    	}else{
    		sSQLSB.append(" SELECT ROWIDTOCHAR(A.rowid) ROWID1, A.MERCHANTID, A.SUBMID, A.PAN, A.TRANSDATE, A.TRANSTIME, A.TERMINALID, A.ORDERID , A.SYS_ORDERID , A.TRANSCODE, A.CURRENCYCODE, A.CAPTUREAMT, A.APPROVECODE, A.RESPONSECODE, A.RESPONSEMSG, A.BATCHNO," +
                    " A.TRANSMODE, TO_CHAR(A.CAPTUREDATE,'YYYY/MM/DD HH24:MI:SS') CAPTUREDATE, A.ECI, A.FEEDBACKCODE, TO_CHAR(A.FEEDBACKDATE,'YYYY/MM/DD HH24:MI:SS') FEEDBACKDATE, A.FEEDBACKMSG, A.CAPTUREFLAG, A.EXCEPT_FLAG, A.RECAPTURE_USERID, A.RECAPTURE_STATUS, A.REAUTH_FLAG, A.AUTOCAPTURE_FLAG " +
                    " FROM CAPTURE A, BILLING B   WHERE A.MERCHANTID = B.MERCHANTID AND A.SYS_ORDERID = B.SYS_ORDERID AND CASE A.TRANSCODE WHEN '20' THEN '00' WHEN '21' THEN '01' ELSE A.TRANSCODE END = B.TRANSCODE AND A.TRANSAMT = B.TRANSAMT AND ROWNUM < (SELECT parm_value FROM SYS_PARM_LIST WHERE PARM_ID ='MER_CAPTURE_QRY_QUANTITY')+1  AND ");
    	}
    	sSQLSB.append("A.MERCHANTID = ?  ");
    	sysBean.AddSQLParam(emDataType.STR, MerchantID);
       
       if(!SubMID.equalsIgnoreCase("ALL")){
     	   sSQLSB.append("AND  A.SUBMID = ?  ");
        }
        if (StartTransDate != null && EndTransDate != null && StartTransDate.length() > 0 && EndTransDate.length() > 0)
        {
            StartTransDate = StartTransDate.replaceAll("/", "");
            StartTransDate = StartTransDate.replaceAll("-", "");
            EndTransDate = EndTransDate.replaceAll("/", "");
            EndTransDate = EndTransDate.replaceAll("-", "");
           sSQLSB.append(" AND TO_CHAR(A.CAPTUREDATE,'YYYYMMDD')  BETWEEN ? AND ? ");
           sysBean.AddSQLParam(emDataType.STR, StartTransDate);
           sysBean.AddSQLParam(emDataType.STR, EndTransDate);
        }

        if (TransCode != null && TransCode.length() > 0 && !TransCode.equalsIgnoreCase("ALL") && !TransCode.equalsIgnoreCase("CANCEL") )
        {
           sSQLSB.append(" AND A.TRANSCODE in ( ? ) ");
           sysBean.AddSQLParam(emDataType.STR, TransCode);
        }
        else
        {
            if(TransCode.equalsIgnoreCase("ALL"))
            {
               sSQLSB.append(" AND A.TRANSCODE in ('00','01') ");

            }
            else
            {
                if(TransCode.equalsIgnoreCase("CANCEL"))
                {
                   sSQLSB.append(" AND A.TRANSCODE in ('20','21') ");
                }
            }
        }
        
        // 請款交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150727
        if (TransType!=null&&TransType.length() > 0 && !TransType.equalsIgnoreCase("ALL")) 
        {
        	if (TransType.equalsIgnoreCase("VMJ"))
        	{
        		sSQLSB.append(" AND B.TRANSTYPE IN ('V3D', 'M3D', 'J3D') ");
        	}
        	else
        	{
        		sSQLSB.append(" AND B.TRANSTYPE = ? ");
        		sysBean.AddSQLParam(emDataType.STR, TransType);
        	}
        }

        if (OrderID!=null&&OrderID.length() > 0)
        {
            if (OrderType.equalsIgnoreCase("M"))
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
        }

        if (ApproveCode!=null&&ApproveCode.length() > 0)
        {
           sSQLSB.append(" AND TRIM(A.APPROVECODE) = ? ");
           sysBean.AddSQLParam(emDataType.STR, ApproveCode);
        }

        if (TerminalID!=null&&TerminalID.length() > 0&& !TerminalID.equalsIgnoreCase("ALL"))
        {
           sSQLSB.append(" AND A.TERMINALID = ? ");
           sysBean.AddSQLParam(emDataType.STR, TerminalID);
        }

        if (CaptureType.length() > 0 && !CaptureType.equalsIgnoreCase("ALL"))
        {
            if (CaptureType.equalsIgnoreCase("CAPTURE"))
            { // 完成請款
               sSQLSB.append(" AND A.CAPTUREFLAG = '3' AND A.FEEDBACKCODE = '000' ");
            }

            if (CaptureType.equalsIgnoreCase("NOTCAPTURE"))
            { // 未請款
               sSQLSB.append(" AND A.CAPTUREFLAG = '3' AND A.FEEDBACKCODE <> '000' AND A.FEEDBACKCODE <> 'CAN' ");
            }

            if (CaptureType.equalsIgnoreCase("PROCESS"))
            { // 處理中
               sSQLSB.append(" AND A.CAPTUREFLAG = '1' ");
            }
        }

        if (ExceptFlag.length()>0 && !ExceptFlag.equalsIgnoreCase("ALL"))
        {
            if (ExceptFlag.equalsIgnoreCase("ACQ"))
            {
               sSQLSB.append(" AND A.EXCEPT_FLAG = 'Y' ");
            }

            if (ExceptFlag.equalsIgnoreCase("MERCHANT"))
            {
               sSQLSB.append(" AND A.EXCEPT_FLAG IS NULL ");
            }
        }
        if(type.equals("TotalNet")){
    		sSQLSB.append("GROUP BY A.MERCHANTID, A.CAPTUREAMT, A.CAPTUREFLAG, A.FEEDBACKCODE, A.TRANSCODE, A.TRANSMODE, A.TRANSTIME, A.TRANSDATE ");
    	}
        sSQLSB.append(" Order by A.TRANSDATE, A.TRANSTIME ");
        // 請款交易查詢 by Jimmy Kang 20150727  -- 修改結束 --
        
        // System.out.println("Sql=" + Sql);
        ArrayList arrayData = new ArrayList();

        try
        {
        	/** 2023/05/15 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-044 */
            arrayData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }

        if (arrayData == null)
            arrayData = new ArrayList();

        return arrayData;
    }

    /* Override get_CaptureLog_List with DataBaseBean parameter */
    // get_CaptureLog_Count method 新增引數 TransType by Jimmy Kang 20150727
    // 請款交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150727 -- 修改開始 --
    public ArrayList get_CaptureLog_Count(DataBaseBean2 sysBean, String MerchantID, String SubMID, String StartTransDate,
                                      String EndTransDate, String TransCode, String TransType, String OrderType,
                                      String OrderID, String ApproveCode, String TerminalID,
                                      String CaptureType, String ExceptFlag,String type)
    // 請款交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150727 -- 修改結束 --
    {
        // SysBean = new DataBaseBean();
	//20130319 Jason 增加回傳 PAN
    	
    	StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
    	
    	// 請款交易查詢 修改SQL & 搜尋條件新增TransType 
    	// 但是因為Capture表格裡面沒有 TransType欄位, 因此必須JOIN Billing表格 
    	// 請款交易查詢 by Jimmy Kang 20150727  -- 修改開始 --
    	// 註解掉 by Jimmy Kang 20150727
    	/*if(type.equals("TotalNet")){
    		sSQLSB.append("SELECT count(*) COUNT,MERCHANTID,TRANSCODE , TRANSDATE, TRANSTIME, sum(CAPTUREAMT ) CAPTUREAMT, TRANSMODE ,FEEDBACKCODE, CAPTUREFLAG        FROM  CAPTURE   WHERE ");
    	}else{
    		sSQLSB.append("SELECT COUNT(*)    COUNT    FROM  CAPTURE   WHERE      ");
    	}
    	sSQLSB.append("MERCHANTID = '" + MerchantID + "'  ");
    	
        if(!SubMID.equalsIgnoreCase("ALL")){
     	   sSQLSB.append("AND  SUBMID = '" + SubMID + "'  ");
        }
        if (StartTransDate != null && EndTransDate != null && StartTransDate.length() > 0 && EndTransDate.length() > 0)
        {
            StartTransDate = StartTransDate.replaceAll("/", "");
            StartTransDate = StartTransDate.replaceAll("-", "");
            EndTransDate = EndTransDate.replaceAll("/", "");
            EndTransDate = EndTransDate.replaceAll("-", "");
            sSQLSB.append(" AND TO_CHAR(CAPTUREDATE,'YYYYMMDD')  BETWEEN '" + StartTransDate + "' AND '" + EndTransDate + "'");
        }

        if (TransCode != null && TransCode.length() > 0 && !TransCode.equalsIgnoreCase("ALL") && !TransCode.equalsIgnoreCase("CANCEL") )
        {
           sSQLSB.append(" AND TRANSCODE in ('" + TransCode + "') ");
        }
        else
        {
            if(TransCode.equalsIgnoreCase("ALL"))
            {
               sSQLSB.append(" AND TRANSCODE in ('00','01') ");
            }
            else
            {
                if(TransCode.equalsIgnoreCase("CANCEL"))
                {
                   sSQLSB.append(" AND TRANSCODE in ('20','21') ");
                }
            }
        }
        
        if (OrderID!=null&&OrderID.length() > 0)
        {
            if (OrderType.equalsIgnoreCase("M"))
            {
                // 以OrderID
               sSQLSB.append(" AND ORDERID = '" + OrderID + "' ");
            }
            else
            {
               sSQLSB.append(" AND SYS_ORDERID = '" + OrderID + "' ");
            }
        }

        if (ApproveCode!=null&&ApproveCode.length() > 0)
        {
           sSQLSB.append(" AND TRIM(APPROVECODE) = '" + ApproveCode + "' ");
        }

        if (TerminalID!=null&&TerminalID.length() > 0&& !TerminalID.equalsIgnoreCase("ALL"))
        {
           sSQLSB.append(" AND TERMINALID = '" + TerminalID + "' ");
        }

        if (CaptureType.length() > 0 && !CaptureType.equalsIgnoreCase("ALL"))
        {
            if (CaptureType.equalsIgnoreCase("CAPTURE"))
            { // 完成請款
               sSQLSB.append(" AND CAPTUREFLAG = '3' AND FEEDBACKCODE = '000' ");
            }

            if (CaptureType.equalsIgnoreCase("NOTCAPTURE"))
            { // 未請款
               sSQLSB.append(" AND CAPTUREFLAG = '3' AND FEEDBACKCODE <> '000' AND A.FEEDBACKCODE <> 'CAN' ");
            }

            if (CaptureType.equalsIgnoreCase("PROCESS"))
            { // 處理中
               sSQLSB.append(" AND CAPTUREFLAG = '1' ");
            }
        }

        if (ExceptFlag.length()>0 && !ExceptFlag.equalsIgnoreCase("ALL"))
        {
            if (ExceptFlag.equalsIgnoreCase("ACQ"))
            {
               sSQLSB.append(" AND EXCEPT_FLAG = 'Y' ");
            }

            if (ExceptFlag.equalsIgnoreCase("MERCHANT"))
            {
               sSQLSB.append(" AND EXCEPT_FLAG IS NULL ");
            }
        }
        if(type.equals("TotalNet")){
    		sSQLSB.append("GROUP BY MERCHANTID,CAPTUREAMT,CAPTUREFLAG,FEEDBACKCODE,TRANSCODE,TRANSMODE,TRANSTIME,TRANSDATE ");
    	}*/
    	
    	if(type.equals("TotalNet")){
    		sSQLSB.append(" SELECT count(*) COUNT, A.MERCHANTID, A.TRANSCODE, A.TRANSDATE, A.TRANSTIME, sum(A.CAPTUREAMT) CAPTUREAMT, A.TRANSMODE, A.FEEDBACKCODE, A.CAPTUREFLAG " );
    		sSQLSB.append(" FROM CAPTURE A, bILLING B   WHERE A.MERCHANTID = B.MERCHANTID AND A.SYS_ORDERID = B.SYS_ORDERID AND CASE A.TRANSCODE WHEN '20' THEN '00' WHEN '21' THEN '01' ELSE A.TRANSCODE END = B.TRANSCODE AND A.TRANSAMT = B.TRANSAMT AND " );
    	}else{
    		sSQLSB.append(" SELECT COUNT(*) COUNT   FROM CAPTURE A, bILLING B " );
    		sSQLSB.append(" WHERE A.MERCHANTID = B.MERCHANTID AND A.SYS_ORDERID = B.SYS_ORDERID AND CASE A.TRANSCODE WHEN '20' THEN '00' WHEN '21' THEN '01' ELSE A.TRANSCODE END = B.TRANSCODE AND A.TRANSAMT = B.TRANSAMT AND " );
    	}
    	
    	sSQLSB.append(" A.MERCHANTID = ? ");
        
    	if(!SubMID.equalsIgnoreCase("ALL")){
           sSQLSB.append(" AND A.SUBMID = ? ");
        }
    	
        if (StartTransDate != null && EndTransDate != null && StartTransDate.length() > 0 && EndTransDate.length() > 0)
        {
            StartTransDate = StartTransDate.replaceAll("/", "");
            StartTransDate = StartTransDate.replaceAll("-", "");
            EndTransDate = EndTransDate.replaceAll("/", "");
            EndTransDate = EndTransDate.replaceAll("-", "");
            
            sSQLSB.append(" AND TO_CHAR(A.CAPTUREDATE,'YYYYMMDD')  BETWEEN ? AND ? ");
            
            sysBean.AddSQLParam(emDataType.STR, StartTransDate);
            sysBean.AddSQLParam(emDataType.STR, EndTransDate);
        }

        if (TransCode != null && TransCode.length() > 0 && !TransCode.equalsIgnoreCase("ALL") && !TransCode.equalsIgnoreCase("CANCEL") )
        {
           sSQLSB.append(" AND A.TRANSCODE in ( ? ) ");
           
           sysBean.AddSQLParam(emDataType.STR, TransCode);
        }
        else
        {
            if(TransCode.equalsIgnoreCase("ALL"))
            {
               sSQLSB.append(" AND A.TRANSCODE in ('00','01') ");
            }
            else
            {
                if(TransCode.equalsIgnoreCase("CANCEL"))
                {
                   sSQLSB.append(" AND A.TRANSCODE in ('20','21') ");
                }
            }
        }
        
        // 請款交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150727
        if (TransType!=null&&TransType.length() > 0 && !TransType.equalsIgnoreCase("ALL")) 
        {
        	if (TransType.equalsIgnoreCase("VMJ"))
        	{
        		sSQLSB.append(" AND B.TRANSTYPE IN ('V3D', 'M3D', 'J3D') ");
        	}
        	else
        	{
        		sSQLSB.append(" AND B.TRANSTYPE = ? ");
        		
        		sysBean.AddSQLParam(emDataType.STR, TransType);
        	}
        }

        if (OrderID!=null&&OrderID.length() > 0)
        {
            if (OrderType.equalsIgnoreCase("M"))
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
        }

        if (ApproveCode!=null&&ApproveCode.length() > 0)
        {
           sSQLSB.append(" AND TRIM(A.APPROVECODE) = ? ");
           
           sysBean.AddSQLParam(emDataType.STR, ApproveCode);
        }

        if (TerminalID!=null&&TerminalID.length() > 0&& !TerminalID.equalsIgnoreCase("ALL"))
        {
           sSQLSB.append(" AND A.TERMINALID = ? ");
           
           sysBean.AddSQLParam(emDataType.STR, TerminalID);
        }

        if (CaptureType.length() > 0 && !CaptureType.equalsIgnoreCase("ALL"))
        {
            if (CaptureType.equalsIgnoreCase("CAPTURE"))
            { // 完成請款
               sSQLSB.append(" AND A.CAPTUREFLAG = '3' AND A.FEEDBACKCODE = '000' ");
            }

            if (CaptureType.equalsIgnoreCase("NOTCAPTURE"))
            { // 未請款
               sSQLSB.append(" AND A.CAPTUREFLAG = '3' AND A.FEEDBACKCODE <> '000' AND A.FEEDBACKCODE <> 'CAN' ");
            }

            if (CaptureType.equalsIgnoreCase("PROCESS"))
            { // 處理中
               sSQLSB.append(" AND A.CAPTUREFLAG = '1' ");
            }
        }

        if (ExceptFlag.length()>0 && !ExceptFlag.equalsIgnoreCase("ALL"))
        {
            if (ExceptFlag.equalsIgnoreCase("ACQ"))
            {
               sSQLSB.append(" AND A.EXCEPT_FLAG = 'Y' ");
            }

            if (ExceptFlag.equalsIgnoreCase("MERCHANT"))
            {
               sSQLSB.append(" AND A.EXCEPT_FLAG IS NULL ");
            }
        }
        if(type.equals("TotalNet")){
    		sSQLSB.append("GROUP BY A.MERCHANTID, A.CAPTUREAMT, A.CAPTUREFLAG, A.FEEDBACKCODE, A.TRANSCODE, A.TRANSMODE, A.TRANSTIME, A.TRANSDATE ");
    	}
        // 請款交易查詢 by Jimmy Kang 20150727  -- 修改結束 --
        
//       sSQLSB.append(" Order by TRANSDATE ,TRANSTIME ");
        // System.out.println("Sql=" + Sql);
        ArrayList arrayData = new ArrayList();

        try
        {
        	/** 2023/05/15 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-045 */
            arrayData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }

        if (arrayData == null)
            arrayData = new ArrayList();

        return arrayData;
    }
    
//    /**
//     * 取得請款記錄查詢列表資料(CaptureLog)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         服務代號
//     * @param String StartTransDate 查詢起日
//     * @param String EndTransDate   查詢迄日
//     * @param String TransCode      交易類別
//     * @param String OrderType      查詢訂單類別 (Order:特店指定單號 System:系統指定單號 )
//     * @param String OrderID        訂單代號
//     * @param String ApproveCode    授權碼
//     * @param String TerminalID     端末機代碼
//     * @param String CaptureType    請款狀態 (ALL:全部  NOTCAPTURE:未請款  CAPTURE:請款完畢 )
//     * @param String ExceptFlag     請款方式 (ALL:全部  MERCHANT:特店請款  ACQ:收單行補請款 )
//     * @param boolean Flag          是否為ROWDATA
//     * @return ResultSet            請款記錄資料
//     */
//    public ResultSet get_CaptureLog_List_rs(String MerchantID, String SubMID, String StartTransDate,
//                                      String EndTransDate, String TransCode, String OrderType,
//                                      String OrderID, String ApproveCode, String TerminalID,
//                                      String CaptureType, boolean Flag, String ExceptFlag)
//    {
//        SysBean = new DataBaseBean();
//        StringBuffer Sql = new StringBuffer("");
//
//        if (Flag)
//        {  // 為rowdata
//        	Sql.append("SELECT CONCAT(MERCHANTID, DECODE(EXCEPT_FLAG, 'Y' , '*', ' ')) AS MERCHANTID, SUBMID, TRANSDATE, TRANSTIME, TERMINALID, ORDERID , SYS_ORDERID , PAN, TRANSCODE, CURRENCYCODE , CAPTUREAMT ,APPROVECODE,RESPONSECODE,RESPONSEMSG,BATCHNO,TRANSMODE ,TO_CHAR(CAPTUREDATE,'YYYYMMDDHH24MISS') CAPTUREDATE,ECI,FEEDBACKCODE,TO_CHAR(FEEDBACKDATE,'YYYYMMDD HH24MISS') FEEDBACKDATE,FEEDBACKMSG,CAPTUREFLAG,ACQUIRERID,CARD_TYPE,EXPIREDATE,USERDEFINE,CONDITION_CODE,CAVV,INSTALLTYPE,INSTALL,FIRSTAMT,EACHAMT,FEE,REDEMTYPE,REDEMUSED,REDEMBALANCE,CREDITAMT,BILLMESSAGE,SYSTRACENO,DUE_DATE,TRANSAMT, EXCEPT_FLAG FROM  CAPTURE   WHERE ");
//        }
//        else
//        {
////        	Sql.append("SELECT MERCHANTID, SUBMID, TRANSDATE, TRANSTIME, TERMINALID, ORDERID , SYS_ORDERID , PAN, TRANSCODE, CURRENCYCODE , CAPTUREAMT ,APPROVECODE,RESPONSECODE,RESPONSEMSG,BATCHNO,TRANSMODE ,TO_CHAR(CAPTUREDATE,'YYYY/MM/DD HH24:MI:SS') CAPTUREDATE,DECODE(EXCEPT_FLAG, 'Y' , '收單行補請款', '特店請款') AS ECI,FEEDBACKCODE,TO_CHAR(FEEDBACKDATE,'YYYY/MM/DD HH24:MI:SS') FEEDBACKDATE,FEEDBACKMSG,CAPTUREFLAG,ACQUIRERID,CARD_TYPE,EXPIREDATE,USERDEFINE,CONDITION_CODE,CAVV,INSTALLTYPE,INSTALL,FIRSTAMT,EACHAMT,FEE,REDEMTYPE,REDEMUSED,REDEMBALANCE,CREDITAMT,BILLMESSAGE,SYSTRACENO,DUE_DATE,TRANSAMT, EXCEPT_FLAG, DECODE(EXCEPT_FLAG, 'Y' , '收單行補請款', '特店請款') AS EXCEPT_DESC FROM  CAPTURE   WHERE ");
//        	Sql.append("SELECT  count(*) COUNT,MERCHANTID,TRANSCODE , TRANSDATE, TRANSTIME, sum(CAPTUREAMT ) CAPTUREAMT, TRANSMODE ,FEEDBACKCODE, CAPTUREFLAG        FROM  CAPTURE   WHERE ");
//        }
//
//       Sql.append( "MERCHANTID = '" + MerchantID + "' AND SUBMID = '" + SubMID + "'");
//        if (StartTransDate != null && EndTransDate != null && StartTransDate.length() > 0 && EndTransDate.length() > 0)
//        {
//            StartTransDate = StartTransDate.replaceAll("/", "");
//            StartTransDate = StartTransDate.replaceAll("-", "");
//            EndTransDate = EndTransDate.replaceAll("/", "");
//            EndTransDate = EndTransDate.replaceAll("-", "");
//           Sql.append( " AND TO_CHAR(CAPTUREDATE,'YYYYMMDD')  BETWEEN '" + StartTransDate + "' AND '" + EndTransDate + "'");
//        }
//
//        if (TransCode!=null&&TransCode.length() > 0 && !TransCode.equalsIgnoreCase("ALL"))
//        {
//           Sql.append( " AND TRANSCODE in ('" + TransCode + "') ");
//
//        }
//        else
//        {
//            if(TransCode.equalsIgnoreCase("ALL"))
//            {
//               Sql.append( " AND TRANSCODE in ('00','01') ");
//            }
//        }
//
//        if (OrderID!=null&&OrderID.length() > 0)
//        {
//            if (OrderType.equalsIgnoreCase("M"))
//            {
//                // 以OrderID
//               Sql.append( " AND ORDERID = '" + OrderID + "' ");
//            }
//            else
//            {
//               Sql.append( " AND SYS_ORDERID = '" + OrderID + "' ");
//            }
//        }
//
//        if (ApproveCode!=null&&ApproveCode.length() > 0)
//        {
//           Sql.append( " AND TRIM(APPROVECODE) = '" + ApproveCode + "' ");
//        }
//
//        if (TerminalID != null && TerminalID.length() > 0 && !TerminalID.equalsIgnoreCase("ALL"))
//        {
//           Sql.append( " AND TERMINALID = '" + TerminalID + "' ");
//        }
//
//        if (CaptureType.length() > 0 && !CaptureType.equalsIgnoreCase("ALL"))
//        {
//            if (CaptureType.equalsIgnoreCase("CAPTURE"))
//            {
//                // 完成請款
//               Sql.append( " AND CAPTUREFLAG = '3' AND FEEDBACKCODE = '000' ");
//            }
//
//            if (CaptureType.equalsIgnoreCase("NOTCAPTURE"))
//            {
//                // 未請款
//               Sql.append( " AND CAPTUREFLAG = '3' AND FEEDBACKCODE <> '000' AND A.FEEDBACKCODE <> 'CAN' ");
//            }
//
//            if (CaptureType.equalsIgnoreCase("PROCESS"))
//            {
//                // 處理中
//               Sql.append( " AND CAPTUREFLAG = '1' ");
//            }
//        }
//
//        if (ExceptFlag.length()>0 && !ExceptFlag.equalsIgnoreCase("ALL"))
//        {
//            if (ExceptFlag.equalsIgnoreCase("ACQ"))
//            {
//               Sql.append( " AND EXCEPT_FLAG = 'Y' ");
//            }
//
//            if (ExceptFlag.equalsIgnoreCase("MERCHANT"))
//            {
//               Sql.append( " AND EXCEPT_FLAG IS NULL ");
//            }
//        }
//        if (!Flag)
//        {  
//        	Sql.append( "GROUP BY MERCHANTID,CAPTUREAMT,CAPTUREFLAG,FEEDBACKCODE,TRANSCODE,TRANSMODE,TRANSTIME,TRANSDATE ");
//        }
//
//       Sql.append( " Order by TRANSDATE ,TRANSTIME ");
//        // System.out.println("Sql=" + Sql);
//        ResultSet arrayData=null ;
//
//        try
//        {
//            arrayData = (ResultSet) sysBean.executeReportSQL(SecurityTool.output(Sql.toString()), "select");
//        }
//        catch (Exception ex)
//        {
//            log_systeminfo.debug(ex.toString());
//            System.out.println(ex.getMessage());
//        }
//
//        return arrayData;
//    }

    /* Override get_CaptureLog_List_rs with DataBaseBean parameter */
    // get_CaptureLog_List_rs method 新增引數 TransType by Jimmy Kang 20150727
    // 請款交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150727 -- 修改開始 --
    public String get_CaptureLog_List_rs(DataBaseBean2 sysBean, String MerchantID, String SubMID, String StartTransDate,
                                      String EndTransDate, String TransCode, String TransType, String OrderType,
                                      String OrderID, String ApproveCode, String TerminalID,
                                      String CaptureType, boolean Flag, String ExceptFlag)
    // 請款交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150727 -- 修改結束 --
    {
        // SysBean = new DataBaseBean();
    	StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
        
        // 請款交易查詢 修改SQL & 搜尋條件新增TransType 
    	// 但是因為Capture表格裡面沒有 TransType欄位, 因此必須JOIN Billing表格 
    	// 請款交易查詢 by Jimmy Kang 20150727  -- 修改開始 --
    	// 註解掉 by Jimmy Kang 20150727
        /*if (Flag)
        {  // 為rowdata
        	sSQLSB.append("SELECT CONCAT(MERCHANTID, DECODE(EXCEPT_FLAG, 'Y' , '*', ' ')) AS MERCHANTID, SUBMID, TRANSDATE, TRANSTIME, TERMINALID, ORDERID , SYS_ORDERID , PAN, TRANSCODE, CURRENCYCODE , CAPTUREAMT ,APPROVECODE,RESPONSECODE,RESPONSEMSG,BATCHNO,TRANSMODE ,TO_CHAR(CAPTUREDATE,'YYYYMMDDHH24MISS') CAPTUREDATE,ECI,FEEDBACKCODE,TO_CHAR(FEEDBACKDATE,'YYYYMMDD HH24MISS') FEEDBACKDATE,FEEDBACKMSG,CAPTUREFLAG,ACQUIRERID,CARD_TYPE,EXPIREDATE,USERDEFINE,CONDITION_CODE,CAVV,INSTALLTYPE,INSTALL,FIRSTAMT,EACHAMT,FEE,REDEMTYPE,REDEMUSED,REDEMBALANCE,CREDITAMT,BILLMESSAGE,SYSTRACENO,DUE_DATE,TRANSAMT, EXCEPT_FLAG FROM  CAPTURE   WHERE ");
        }
        else
        {
//        	sSQLSB.append("SELECT MERCHANTID, SUBMID, TRANSDATE, TRANSTIME, TERMINALID, ORDERID , SYS_ORDERID , PAN, TRANSCODE, CURRENCYCODE , CAPTUREAMT ,APPROVECODE,RESPONSECODE,RESPONSEMSG,BATCHNO,TRANSMODE ,TO_CHAR(CAPTUREDATE,'YYYY/MM/DD HH24:MI:SS') CAPTUREDATE,DECODE(EXCEPT_FLAG, 'Y' , '收單行補請款', '特店請款') AS ECI,FEEDBACKCODE,TO_CHAR(FEEDBACKDATE,'YYYY/MM/DD HH24:MI:SS') FEEDBACKDATE,FEEDBACKMSG,CAPTUREFLAG,ACQUIRERID,CARD_TYPE,EXPIREDATE,USERDEFINE,CONDITION_CODE,CAVV,INSTALLTYPE,INSTALL,FIRSTAMT,EACHAMT,FEE,REDEMTYPE,REDEMUSED,REDEMBALANCE,CREDITAMT,BILLMESSAGE,SYSTRACENO,DUE_DATE,TRANSAMT, EXCEPT_FLAG, DECODE(EXCEPT_FLAG, 'Y' , '收單行補請款', '特店請款') AS EXCEPT_DESC FROM  CAPTURE   WHERE ");
        	sSQLSB.append("SELECT  count(*) COUNT,MERCHANTID,TRANSCODE , TRANSDATE, TRANSTIME, sum(CAPTUREAMT ) CAPTUREAMT, TRANSMODE ,FEEDBACKCODE, CAPTUREFLAG        FROM  CAPTURE   WHERE ");
        }

       sSQLSB.append("MERCHANTID = '" + MerchantID + "' ");
        if(!SubMID.equalsIgnoreCase("ALL")){
     	   sSQLSB.append(" AND  SUBMID = '" + SubMID + "'  ");
        }
        if (StartTransDate != null && EndTransDate != null && StartTransDate.length() > 0 && EndTransDate.length() > 0)
        {
            StartTransDate = StartTransDate.replaceAll("/", "");
            StartTransDate = StartTransDate.replaceAll("-", "");
            EndTransDate = EndTransDate.replaceAll("/", "");
            EndTransDate = EndTransDate.replaceAll("-", "");
           sSQLSB.append(" AND TO_CHAR(CAPTUREDATE,'YYYYMMDD')  BETWEEN '" + StartTransDate + "' AND '" + EndTransDate + "'");
        }

        if (TransCode!=null&&TransCode.length() > 0 && !TransCode.equalsIgnoreCase("ALL"))
        {
           sSQLSB.append(" AND TRANSCODE in ('" + TransCode + "') ");

        }
        else
        {
            if(TransCode.equalsIgnoreCase("ALL"))
            {
               sSQLSB.append(" AND TRANSCODE in ('00','01') ");
            }
        }

        if (OrderID!=null&&OrderID.length() > 0)
        {
            if (OrderType.equalsIgnoreCase("M"))
            {
                // 以OrderID
               sSQLSB.append(" AND ORDERID = '" + OrderID + "' ");
            }
            else
            {
               sSQLSB.append(" AND SYS_ORDERID = '" + OrderID + "' ");
            }
        }

        if (ApproveCode!=null&&ApproveCode.length() > 0)
        {
           sSQLSB.append(" AND TRIM(APPROVECODE) = '" + ApproveCode + "' ");
        }

        if (TerminalID != null && TerminalID.length() > 0 && !TerminalID.equalsIgnoreCase("ALL"))
        {
           sSQLSB.append(" AND TERMINALID = '" + TerminalID + "' ");
        }

        if (CaptureType.length() > 0 && !CaptureType.equalsIgnoreCase("ALL"))
        {
            if (CaptureType.equalsIgnoreCase("CAPTURE"))
            {
                // 完成請款
               sSQLSB.append(" AND CAPTUREFLAG = '3' AND FEEDBACKCODE = '000' ");
            }

            if (CaptureType.equalsIgnoreCase("NOTCAPTURE"))
            {
                // 未請款
               sSQLSB.append(" AND CAPTUREFLAG = '3' AND FEEDBACKCODE <> '000' AND A.FEEDBACKCODE <> 'CAN' ");
            }

            if (CaptureType.equalsIgnoreCase("PROCESS"))
            {
                // 處理中
               sSQLSB.append(" AND CAPTUREFLAG = '1' ");
            }
        }

        if (ExceptFlag.length()>0 && !ExceptFlag.equalsIgnoreCase("ALL"))
        {
            if (ExceptFlag.equalsIgnoreCase("ACQ"))
            {
               sSQLSB.append(" AND EXCEPT_FLAG = 'Y' ");
            }

            if (ExceptFlag.equalsIgnoreCase("MERCHANT"))
            {
               sSQLSB.append(" AND EXCEPT_FLAG IS NULL ");
            }
        }
        if (!Flag)
        {  
        	sSQLSB.append("GROUP BY MERCHANTID,CAPTUREAMT,CAPTUREFLAG,FEEDBACKCODE,TRANSCODE,TRANSMODE,TRANSTIME,TRANSDATE ");
        }
        sSQLSB.append(" Order by TRANSDATE ,TRANSTIME ");*/
        
        if (Flag)
        {  // 為rowdata
        	sSQLSB.append(" SELECT CONCAT(A.MERCHANTID, DECODE(A.EXCEPT_FLAG, 'Y' , '*', ' ')) AS MERCHANTID, A.SUBMID, A.TRANSDATE, A.TRANSTIME, A.TERMINALID, A.ORDERID, A.SYS_ORDERID, A.PAN, A.TRANSCODE, A.CURRENCYCODE, A.CAPTUREAMT, A.APPROVECODE, A.RESPONSECODE, A.RESPONSEMSG, A.BATCHNO, A.TRANSMODE, TO_CHAR(A.CAPTUREDATE, 'YYYYMMDDHH24MISS') CAPTUREDATE, A.ECI, " +
        			" A.FEEDBACKCODE, TO_CHAR(A.FEEDBACKDATE, 'YYYYMMDD HH24MISS') FEEDBACKDATE, A.FEEDBACKMSG, A.CAPTUREFLAG, A.ACQUIRERID, A.CARD_TYPE, A.EXPIREDATE, A.USERDEFINE, A.CONDITION_CODE, A.CAVV, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE, A.SYSTRACENO, A.DUE_DATE, A.TRANSAMT, A.EXCEPT_FLAG " +
        			" FROM  CAPTURE A, BILLING B   " +
        			" WHERE A.MERCHANTID = B.MERCHANTID AND A.SYS_ORDERID = B.SYS_ORDERID AND CASE A.TRANSCODE WHEN '20' THEN '00' WHEN '21' THEN '01' ELSE A.TRANSCODE END = B.TRANSCODE AND A.TRANSAMT = B.TRANSAMT AND ");
        }
        else
        {
//        	sSQLSB.append("SELECT MERCHANTID, SUBMID, TRANSDATE, TRANSTIME, TERMINALID, ORDERID , SYS_ORDERID , PAN, TRANSCODE, CURRENCYCODE , CAPTUREAMT ,APPROVECODE,RESPONSECODE,RESPONSEMSG,BATCHNO,TRANSMODE ,TO_CHAR(CAPTUREDATE,'YYYY/MM/DD HH24:MI:SS') CAPTUREDATE,DECODE(EXCEPT_FLAG, 'Y' , '收單行補請款', '特店請款') AS ECI,FEEDBACKCODE,TO_CHAR(FEEDBACKDATE,'YYYY/MM/DD HH24:MI:SS') FEEDBACKDATE,FEEDBACKMSG,CAPTUREFLAG,ACQUIRERID,CARD_TYPE,EXPIREDATE,USERDEFINE,CONDITION_CODE,CAVV,INSTALLTYPE,INSTALL,FIRSTAMT,EACHAMT,FEE,REDEMTYPE,REDEMUSED,REDEMBALANCE,CREDITAMT,BILLMESSAGE,SYSTRACENO,DUE_DATE,TRANSAMT, EXCEPT_FLAG, DECODE(EXCEPT_FLAG, 'Y' , '收單行補請款', '特店請款') AS EXCEPT_DESC FROM  CAPTURE   WHERE ");
        	sSQLSB.append(" SELECT count(*) COUNT, A.MERCHANTID, A.TRANSCODE, A.TRANSDATE, A.TRANSTIME, sum(A.CAPTUREAMT) CAPTUREAMT, A.TRANSMODE, A.FEEDBACKCODE, A.CAPTUREFLAG " +
        			" FROM  CAPTURE A, BILLING B " +
        			" WHERE A.MERCHANTID = B.MERCHANTID AND A.SYS_ORDERID = B.SYS_ORDERID AND CASE A.TRANSCODE WHEN '20' THEN '00' WHEN '21' THEN '01' ELSE A.TRANSCODE END = B.TRANSCODE AND A.TRANSAMT = B.TRANSAMT AND ");
        }

       sSQLSB.append(" A.MERCHANTID = ? ");
       sysBean.AddSQLParam(emDataType.STR, MerchantID);
       
        if(!SubMID.equalsIgnoreCase("ALL")){
     	   sSQLSB.append(" AND A.SUBMID = ? ");
     	  sysBean.AddSQLParam(emDataType.STR, SubMID);
        }
        if (StartTransDate != null && EndTransDate != null && StartTransDate.length() > 0 && EndTransDate.length() > 0)
        {
            StartTransDate = StartTransDate.replaceAll("/", "");
            StartTransDate = StartTransDate.replaceAll("-", "");
            EndTransDate = EndTransDate.replaceAll("/", "");
            EndTransDate = EndTransDate.replaceAll("-", "");
           sSQLSB.append(" AND TO_CHAR(A.CAPTUREDATE,'YYYYMMDD')  BETWEEN ? AND ? ");
           sysBean.AddSQLParam(emDataType.STR, StartTransDate);
           sysBean.AddSQLParam(emDataType.STR, EndTransDate);
        }

        if (TransCode != null && TransCode.length() > 0 && !TransCode.equalsIgnoreCase("ALL") && !TransCode.equalsIgnoreCase("CANCEL") )
        {
           sSQLSB.append(" AND A.TRANSCODE in ( ? ) ");
           sysBean.AddSQLParam(emDataType.STR, TransCode);
        }
        else
        {
            if(TransCode.equalsIgnoreCase("ALL"))
            {
               sSQLSB.append(" AND A.TRANSCODE in ('00','01') ");
            }
            else
            {
                if(TransCode.equalsIgnoreCase("CANCEL"))
                {
                   sSQLSB.append(" AND A.TRANSCODE in ('20','21') ");
                }
            }
        }
        
        // 請款交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150727
        if (TransType!=null&&TransType.length() > 0 && !TransType.equalsIgnoreCase("ALL")) 
        {
        	if (TransType.equalsIgnoreCase("VMJ"))
        	{
        		sSQLSB.append(" AND B.TRANSTYPE IN ('V3D', 'M3D', 'J3D') ");
        	}
        	else
        	{
        		sSQLSB.append(" AND B.TRANSTYPE = ? ");
        		sysBean.AddSQLParam(emDataType.STR, TransType);
        	}
        }

        if (OrderID!=null&&OrderID.length() > 0)
        {
            if (OrderType.equalsIgnoreCase("M"))
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
        }

        if (ApproveCode!=null&&ApproveCode.length() > 0)
        {
           sSQLSB.append(" AND TRIM(A.APPROVECODE) = ? ");
           sysBean.AddSQLParam(emDataType.STR, ApproveCode);
        }

        if (TerminalID != null && TerminalID.length() > 0 && !TerminalID.equalsIgnoreCase("ALL"))
        {
           sSQLSB.append(" AND A.TERMINALID = ? ");
           sysBean.AddSQLParam(emDataType.STR, TerminalID);
        }

        if (CaptureType.length() > 0 && !CaptureType.equalsIgnoreCase("ALL"))
        {
            if (CaptureType.equalsIgnoreCase("CAPTURE"))
            {
                // 完成請款
               sSQLSB.append(" AND A.CAPTUREFLAG = '3' AND A.FEEDBACKCODE = '000' ");
            }

            if (CaptureType.equalsIgnoreCase("NOTCAPTURE"))
            {
                // 未請款
               sSQLSB.append(" AND A.CAPTUREFLAG = '3' AND A.FEEDBACKCODE <> '000' AND A.FEEDBACKCODE <> 'CAN' ");
            }

            if (CaptureType.equalsIgnoreCase("PROCESS"))
            {
                // 處理中
               sSQLSB.append(" AND A.CAPTUREFLAG = '1' ");
            }
        }

        if (ExceptFlag.length()>0 && !ExceptFlag.equalsIgnoreCase("ALL"))
        {
            if (ExceptFlag.equalsIgnoreCase("ACQ"))
            {
               sSQLSB.append(" AND A.EXCEPT_FLAG = 'Y' ");
            }

            if (ExceptFlag.equalsIgnoreCase("MERCHANT"))
            {
               sSQLSB.append(" AND A.EXCEPT_FLAG IS NULL ");
            }
        }
        if (!Flag)
        {  
        	sSQLSB.append(" GROUP BY A.MERCHANTID, A.CAPTUREAMT, A.CAPTUREFLAG, A.FEEDBACKCODE, A.TRANSCODE, A.TRANSMODE, A.TRANSTIME, A.TRANSDATE ");
        }
        sSQLSB.append(" Order by A.TRANSDATE, A.TRANSTIME ");
        // 請款交易查詢 by Jimmy Kang 20150727  -- 修改結束 --
        
        // System.out.println("Sql=" + Sql);
//        ResultSet arrayData=null ;
//
//        try
//        {
//            /** 2023/05/15 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-047 */
//            arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
//        }
//        catch (Exception ex)
//        {
//            log_systeminfo.debug(ex.toString());
//            System.out.println(ex.getMessage());
//        }

        return sSQLSB.toString();
    }
    


//    /**
//     * 取得請款記錄查詢檢視資料(CaptureLog)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店代號
//     * @param String SysOrderID     系統指定訂單代號
//     * @return Hashtable            授權記錄資料
//     */
//    public Hashtable get_CaptureLog_View(String MerchantID, String SubMID, String SysOrderID,String rowid)
//    {
//        SysBean = new DataBaseBean();
//        Hashtable hashData = new Hashtable();
//        StringBuffer Sql = new StringBuffer( "SELECT MERCHANTID, SUBMID, TRANSDATE, TRANSTIME, TERMINALID, ORDERID , CAPTUREAMT , SYS_ORDERID , TRANSCODE, CURRENCYCODE , TRANSAMT ,APPROVECODE,RESPONSECODE,RESPONSEMSG,BATCHNO,"+
//                      " TRANSMODE ,ECI,FEEDBACKCODE,TO_CHAR(CAPTUREDATE,'YYYY/MM/DD HH24:MI:SS') CAPTUREDATE,FEEDBACKMSG,CAPTUREFLAG,PAN FROM  CAPTURE   WHERE ");
//       Sql.append( " MERCHANTID = '" + MerchantID + "' AND SUBMID = '" + SubMID + "' AND SYS_ORDERID = '" + SysOrderID + "' and ROWIDTOCHAR(rowid)='"+rowid+"' ");
//        // System.out.println("Sql=" + Sql);
//        ArrayList arrayData = new ArrayList();
//
//        try
//        {
//            arrayData = (ArrayList) sysBean.executeSQL(Sql.toString(), "select");
//        }
//        catch (Exception ex)
//        {
//            log_systeminfo.debug(ex.toString());
//            System.out.println(ex.getMessage());
//        }
//
//        if (arrayData == null)
//            arrayData = new ArrayList();
//
//        if (arrayData.size() > 0)
//        {
//            hashData = (Hashtable) arrayData.get(0);
//        }
//
//        return hashData;
//    }

    /* Override get_CaptureLog_View with DataBaseBean parameter */
    public Hashtable get_CaptureLog_View(DataBaseBean2 sysBean, String MerchantID, String SubMID, String SysOrderID,String rowid)
    {
        // SysBean = new DataBaseBean();
    	Hashtable hashData = new Hashtable();
    	StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("SELECT MERCHANTID, SUBMID, TRANSDATE, TRANSTIME, TERMINALID, ORDERID , CAPTUREAMT , SYS_ORDERID , TRANSCODE, CURRENCYCODE , TRANSAMT ,APPROVECODE,RESPONSECODE,RESPONSEMSG,BATCHNO,"+
                      " TRANSMODE ,ECI,FEEDBACKCODE,TO_CHAR(CAPTUREDATE,'YYYY/MM/DD HH24:MI:SS') CAPTUREDATE,FEEDBACKMSG,CAPTUREFLAG,PAN FROM  CAPTURE   WHERE ");
		sSQLSB.append(" MERCHANTID = ? AND SUBMID = ? AND SYS_ORDERID = ? and ROWIDTOCHAR(rowid) = ? ");
		sysBean.AddSQLParam(emDataType.STR, MerchantID);
		sysBean.AddSQLParam(emDataType.STR, SubMID);
		sysBean.AddSQLParam(emDataType.STR, SysOrderID);
		sysBean.AddSQLParam(emDataType.STR, rowid);
		
        // System.out.println("Sql=" + Sql);
        ArrayList arrayData = new ArrayList();

        try
        {
        	/** 2023/05/15 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-046 */
            arrayData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            log_systeminfo.debug(ex.toString());
            System.out.println(ex.getMessage());
        }
        
        if (arrayData == null)
            arrayData = new ArrayList();
            
        if (arrayData.size() > 0) 
        {
            hashData = (Hashtable) arrayData.get(0);
        }
        
        return hashData;
    }
    
    public void closeConn()
    {
        try 
        {
            sysBean.close();
        }
        catch (SQLException ex) 
        {
        }
    }
}
