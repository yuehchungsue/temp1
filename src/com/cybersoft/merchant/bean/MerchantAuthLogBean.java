package com.cybersoft.merchant.bean;


import java.util.ArrayList;
/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import java.util.Hashtable;
import java.util.*;
import java.sql.*;
import com.cybersoft.bean.LogUtils;
import com.cybersoft.bean.emDataType;
import com.fubon.security.filter.SecurityTool;
public class MerchantAuthLogBean
{
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    private  DataBaseBean2 sysBean = new DataBaseBean2();
    public MerchantAuthLogBean()
    {
    }

//    /**
//     * 取得授權記錄查詢列表資料(AuthLog+Billing)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店代號
//     * @param String StartTransDate 查詢起日
//     * @param String EndTransDate   查詢迄日
//     * @param String StartTransTime 查詢起時
//     * @param String EndTransTime   查詢迄時
//     * @param String TransCode      交易類別
//     * @param String TransStatus    授權結果
//     * @param String OrderType      查詢訂單類別 (Order:特店指定單號 System:系統指定單號 )
//     * @param String OrderID        訂單代號
//     * @param String ApproveCode    授權碼
//     * @param String TerminalID     端末機代碼
//     * @param String CaptureType    請款狀態 (ALL:全部  NOTCAPTURE:未請款  CAPTURE:請款完畢 )
//     * @return ArrayList            授權記錄資料
//     */
//    public ArrayList get_AuthLog_List(String MerchantID, String SubMID, String StartTransDate,
//                                      String EndTransDate, String StartTransTime, String EndTransTime, 
//                                      String TransCode, String TransStatus, String OrderType, 
//                                      String OrderID, String ApproveCode, String TerminalID, String CaptureType) 
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//
//        StringBuffer Sql = new StringBuffer("SELECT ROWIDTOCHAR(a.rowid) ROWID1,A.MERCHANTID, A.SUBMID, A.TERMINALID, A.ACQUIRERID, A.ORDERID, A.SYS_ORDERID, A.CARD_TYPE, A.PAN, A.EXTENNO, A.EXPIREDATE, A.TRANSCODE, A.REVERSALFLAG, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.TRANS_STATUS, A.APPROVECODE, A.RESPONSECODE, A.RESPONSEMSG, A.ENTRY_MODE, A.CONDITION_CODE, A.BATCHNO, A.USERDEFINE, A.DIRECTION, A.EMAIL, A.MTI, A.RRN, A.SOCIALID, A.TRANSMODE, A.TRANSTYPE, A.ECI, A.CAVV, A.XID, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE, Case  When B.BALANCEAMT < 0 Then 0 When B.BALANCEAMT >=0 Then B.BALANCEAMT End As BALANCEAMT ,B.TRANSCODE BALANCE_TRANSCODE  FROM  AUTHLOG A LEFT OUTER JOIN  BILLING  B ON A.SYS_ORDERID = B.SYS_ORDERID AND A.MERCHANTID =  B.MERCHANTID AND A.SUBMID = B.SUBMID WHERE ");
//       Sql.append( "A.MERCHANTID = '" + MerchantID + "' AND A.SUBMID = '" + SubMID + "' AND TRANS_STATUS <> 'R' ");
//        if (StartTransDate != null && EndTransDate != null && StartTransDate.length() > 0 && EndTransDate.length() > 0) 
//        {
//            StartTransDate = StartTransDate.replaceAll("/", "");
//            StartTransDate = StartTransDate.replaceAll("-", "");
//            EndTransDate = EndTransDate.replaceAll("/", "");
//            EndTransDate = EndTransDate.replaceAll("-", "");
//           Sql.append( " AND A.TRANSDATE BETWEEN '" + StartTransDate + "' AND '" + EndTransDate + "'");
//        }
//        
//        if (StartTransTime.length() > 0 && EndTransTime.length() > 0) 
//        {
//           Sql.append( " AND SUBSTR(A.TRANSTIME ,1,2) BETWEEN '" + StartTransTime + "' AND '" + EndTransTime + "'");
//        }
//        
//        if (TransCode!=null&&TransCode.length() > 0 && !TransCode.equalsIgnoreCase("ALL")) 
//        {
//           Sql.append( " AND A.TRANSCODE = '" + TransCode + "' ");
//        }
//        
//        if (TransStatus!=null&&TransStatus.length() > 0 && !TransStatus.equalsIgnoreCase("ALL")) 
//        {
//           Sql.append( " AND A.TRANS_STATUS = '" + TransStatus + "' ");
//        }
//        
//        if (OrderID!=null&&OrderID.length() > 0) 
//        {
//            if (OrderType.equalsIgnoreCase("M")) 
//            { 
//                // 以OrderID
//               Sql.append( " AND A.ORDERID = '" + OrderID + "' ");
//            } 
//            else 
//            {
//               Sql.append( " AND A.SYS_ORDERID = '" + OrderID + "' ");
//            }
//        }
//        
//        if (ApproveCode!=null&&ApproveCode.length() > 0) 
//        {
//           Sql.append( " AND TRIM(A.APPROVECODE) = '" + ApproveCode + "' ");
//        }
//        
//        if (TerminalID!=null&&TerminalID.length() > 0&& !TerminalID.equalsIgnoreCase("ALL")) 
//        {
//           Sql.append( " AND A.TERMINALID = '" + TerminalID + "' ");
//        }
//        
//        if (CaptureType.length() > 0 && !CaptureType.equalsIgnoreCase("ALL")) 
//        {
//            if (CaptureType.equalsIgnoreCase("CAPTURE")) 
//            { 
//                // 已請款
//               Sql.append( " AND B.BALANCEAMT < B.TRANSAMT AND B.BALANCEAMT >= 0 AND A.TRANS_STATUS = 'A' ");
//            }
//            
//            if (CaptureType.equalsIgnoreCase("NOTCAPTURE")) 
//            { // 未請款
//               Sql.append( " AND B.BALANCEAMT > 0 AND A.TRANS_STATUS = 'A' ");
//            }
//        }
//        
//       Sql.append( " Order by A.TERMINALID, A.ORDERID, A.TRANSDATE ,A.TRANSTIME ");
//        // System.out.println("Sql=" + Sql);
//        
//        ArrayList arrayData = new ArrayList();
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
    
    /* Override get_AuthLog_List with DataBaseBean parameter */
    // get_AuthLog_List method 新增引數 TransType by Jimmy Kang 20150721
    // 授權交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150721 -- 修改開始 --
    public ArrayList get_AuthLog_List(DataBaseBean2 sysBean, String MerchantID, String SubMID, String StartTransDate,
                                      String EndTransDate, String StartTransTime, String EndTransTime, 
                                      String TransCode, String TransType, String TransStatus, String OrderType,
                                      String OrderID, String ApproveCode, String TerminalID, String CaptureType)
    // 授權交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150721 -- 修改結束 --
    {
        //DataBaseBean SysBean = new DataBaseBean();

    	StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("SELECT ROWIDTOCHAR(a.rowid) ROWID1,A.MERCHANTID, A.SUBMID, A.TERMINALID, A.ACQUIRERID, A.ORDERID, A.SYS_ORDERID, A.CARD_TYPE, A.PAN, A.EXTENNO, A.EXPIREDATE, A.TRANSCODE, A.REVERSALFLAG, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.TRANS_STATUS, A.APPROVECODE, A.RESPONSECODE, A.RESPONSEMSG, A.ENTRY_MODE, A.CONDITION_CODE, A.BATCHNO, A.USERDEFINE, A.DIRECTION, A.EMAIL, A.MTI, A.RRN, A.SOCIALID, A.TRANSMODE, A.TRANSTYPE, A.ECI, A.CAVV, A.XID, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE, Case  When B.BALANCEAMT < 0 Then 0 When B.BALANCEAMT >=0 Then B.BALANCEAMT End As BALANCEAMT ,B.TRANSCODE BALANCE_TRANSCODE  ");
        sSQLSB.append("FROM  AUTHLOG A LEFT OUTER JOIN  BILLING  B ON A.SYS_ORDERID = B.SYS_ORDERID AND A.MERCHANTID =  B.MERCHANTID AND A.SUBMID = B.SUBMID   ");
     
        // 授權交易查詢 Trans_Status 新增P-Pending狀態 
        // 查詢條件新增除了Trans_Status不可為 'R'外, 也不可為空白  by Jimmy Kang 20150721  -- 修改開始 --
        //sSQLSB.append(" WHERE A.MERCHANTID = '" + MerchantID + "' AND TRANS_STATUS <> 'R' ");   //註解掉 by Jimmy Kang 20150721
        sSQLSB.append(" WHERE A.MERCHANTID = ? AND (LTRIM(RTRIM(A.TRANS_STATUS)) IS NOT NULL AND A.TRANS_STATUS NOT IN ('R')) ");
        sysBean.AddSQLParam(emDataType.STR, MerchantID);
        
        // 查詢條件新增除了Trans_Status不可為 'R'外, 也不可為空白  by Jimmy Kang 20150721  -- 修改結束 --
        
        //new add 歐付寶
        if(SubMID != null  && !SubMID.equals("all")){
    	    sSQLSB.append(" AND A.SUBMID = ?  ");
    	    sysBean.AddSQLParam(emDataType.STR, SubMID);
        }
       
        if (StartTransDate != null && EndTransDate != null && StartTransDate.length() > 0 && EndTransDate.length() > 0) 
        {
            StartTransDate = StartTransDate.replaceAll("/", "");
            StartTransDate = StartTransDate.replaceAll("-", "");
            EndTransDate = EndTransDate.replaceAll("/", "");
            EndTransDate = EndTransDate.replaceAll("-", "");
           sSQLSB.append(" AND A.TRANSDATE BETWEEN ? AND ? ");
           sysBean.AddSQLParam(emDataType.STR, StartTransDate);
           sysBean.AddSQLParam(emDataType.STR, EndTransDate);
        }
        
        if (StartTransTime.length() > 0 && EndTransTime.length() > 0) 
        {
           sSQLSB.append(" AND SUBSTR(A.TRANSTIME ,1,2) BETWEEN ? AND ? ");
           sysBean.AddSQLParam(emDataType.STR, StartTransTime);
           sysBean.AddSQLParam(emDataType.STR, EndTransTime);
        }
        
        if (TransCode!=null&&TransCode.length() > 0 && !TransCode.equalsIgnoreCase("ALL")) 
        {
           sSQLSB.append(" AND A.TRANSCODE = ? ");
           sysBean.AddSQLParam(emDataType.STR, TransCode);
        }
        
        // 授權交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150721  -- 修改開始 --
        if (TransType!=null&&TransType.length() > 0 && !TransType.equalsIgnoreCase("ALL")) 
        {
        	if (TransType.equalsIgnoreCase("VMJ"))
        	{
        		sSQLSB.append(" AND A.TRANSTYPE IN ('V3D', 'M3D', 'J3D') ");
        	}
        	else
        	{
        		sSQLSB.append(" AND A.TRANSTYPE = ? ");
        		sysBean.AddSQLParam(emDataType.STR, TransType);
        	}
        }
        // 授權交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150721  -- 修改結束 --
        
        if (TransStatus!=null&&TransStatus.length() > 0 && !TransStatus.equalsIgnoreCase("ALL")) 
        {
           sSQLSB.append(" AND A.TRANS_STATUS = ? ");
           sysBean.AddSQLParam(emDataType.STR, TransStatus);
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
            { 
                // 已請款
               sSQLSB.append(" AND B.BALANCEAMT < B.TRANSAMT AND B.BALANCEAMT >= 0 AND A.TRANS_STATUS = 'A' ");
            }
            
            if (CaptureType.equalsIgnoreCase("NOTCAPTURE")) 
            { // 未請款
               sSQLSB.append(" AND B.BALANCEAMT > 0 AND A.TRANS_STATUS = 'A' ");
            }
        }
        
       sSQLSB.append("  AND  ROWNUM < (SELECT parm_value FROM SYS_PARM_LIST WHERE PARM_ID ='MER_AUTH_QRY_QUANTITY')+2  Order by A.TERMINALID, A.ORDERID, A.TRANSDATE ,A.TRANSTIME ");
        // System.out.println("Sql=" + Sql);
        
        ArrayList arrayData = new ArrayList();
        try 
        {
        	/** 2023/05/22 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-040 */
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
    /* Override get_AuthLog_List with DataBaseBean parameter */
    // get_AuthLog_Count method 新增引數 TransType by Jimmy Kang 20150721
    // 授權交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150721 -- 修改開始 --
    public ArrayList get_AuthLog_Count(DataBaseBean2 sysBean, String MerchantID, String SubMID, String StartTransDate,
                                      String EndTransDate, String StartTransTime, String EndTransTime, 
                                      String TransCode, String TransType, String TransStatus, String OrderType, 
                                      String OrderID, String ApproveCode, String TerminalID, String CaptureType) 
    // 授權交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150721  -- 修改結束 --
    {
        //DataBaseBean SysBean = new DataBaseBean();

    	StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("SELECT  COUNT(*) TOTAL    ");
        sSQLSB.append(" FROM  AUTHLOG A LEFT OUTER JOIN  BILLING  B ON A.SYS_ORDERID = B.SYS_ORDERID AND A.MERCHANTID =  B.MERCHANTID AND A.SUBMID = B.SUBMID   ");
        
        // 授權交易查詢 Trans_Status 新增P-Pending狀態 
        // 查詢條件新增除了Trans_Status不可為 'R'外, 也不可為空白  by Jimmy Kang 20150721  -- 修改開始 --
        //sSQLSB.append("WHERE A.MERCHANTID = '" + MerchantID + "' AND TRANS_STATUS <> 'R' ");    //註解掉 by Jimmy Kang 20150721
        sSQLSB.append("WHERE A.MERCHANTID = '" + MerchantID + "' AND (LTRIM(RTRIM(A.TRANS_STATUS)) IS NOT NULL AND A.TRANS_STATUS NOT IN ('R')) ");
        // 查詢條件新增除了Trans_Status不可為 'R'外, 也不可為空白  by Jimmy Kang 20150721  -- 修改結束 --
        
        if(SubMID != null  && !SubMID.equals("all")){
     	   sSQLSB.append(" AND A.SUBMID = ? ");
     	   
     	  sysBean.AddSQLParam(emDataType.STR, SubMID);
        }
        
         if (StartTransDate != null && EndTransDate != null && StartTransDate.length() > 0 && EndTransDate.length() > 0) 
         {
             StartTransDate = StartTransDate.replaceAll("/", "");
             StartTransDate = StartTransDate.replaceAll("-", "");
             EndTransDate = EndTransDate.replaceAll("/", "");
             EndTransDate = EndTransDate.replaceAll("-", "");
             
            sSQLSB.append(" AND A.TRANSDATE BETWEEN ? AND ? ");
            
            sysBean.AddSQLParam(emDataType.STR, StartTransDate);
            sysBean.AddSQLParam(emDataType.STR, EndTransDate);
         }
         
         if (StartTransTime.length() > 0 && EndTransTime.length() > 0) 
         {
            sSQLSB.append(" AND SUBSTR(A.TRANSTIME ,1,2) BETWEEN ? AND ? ");
            
            sysBean.AddSQLParam(emDataType.STR, StartTransTime);
            sysBean.AddSQLParam(emDataType.STR, EndTransTime);
         }
         
         if (TransCode!=null&&TransCode.length() > 0 && !TransCode.equalsIgnoreCase("ALL")) 
         {
            sSQLSB.append(" AND A.TRANSCODE = ? ");
            
            sysBean.AddSQLParam(emDataType.STR, TransCode);
         }
         
         // 授權交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150721  -- 修改開始 --
         if (TransType!=null&&TransType.length() > 0 && !TransType.equalsIgnoreCase("ALL")) 
         {
         	if (TransType.equalsIgnoreCase("VMJ"))
         	{
         		sSQLSB.append(" AND A.TRANSTYPE IN ('V3D', 'M3D', 'J3D') ");
         	}
         	else
         	{
         		sSQLSB.append(" AND A.TRANSTYPE = ? ");
         		
         		sysBean.AddSQLParam(emDataType.STR, TransType);
         	}
         }
         // 授權交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150721  -- 修改結束 --
         
         if (TransStatus!=null&&TransStatus.length() > 0 && !TransStatus.equalsIgnoreCase("ALL")) 
         {
            sSQLSB.append(" AND A.TRANS_STATUS = ? ");
            
            sysBean.AddSQLParam(emDataType.STR, TransStatus);
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
             { 
                 // 已請款
                sSQLSB.append(" AND B.BALANCEAMT < B.TRANSAMT AND B.BALANCEAMT >= 0 AND A.TRANS_STATUS = 'A' ");
             }
             
             if (CaptureType.equalsIgnoreCase("NOTCAPTURE")) 
             { // 未請款
                sSQLSB.append(" AND B.BALANCEAMT > 0 AND A.TRANS_STATUS = 'A' ");
             }
         }
         
       
        // System.out.println("Sql=" + Sql);
        
        ArrayList arrayData = new ArrayList();
        try 
        {
        	/** 2023/05/15 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-041 */
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
//     * 取得授權記錄查詢列表資料(AuthLog+Billing)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店代號
//     * @param String StartTransDate 查詢起日
//     * @param String EndTransDate   查詢迄日
//     * @param String StartTransTime 查詢起時
//     * @param String EndTransTime   查詢迄時
//     * @param String TransCode      交易類別
//     * @param String TransStatus    授權結果
//     * @param String OrderType      查詢訂單類別 (Order:特店指定單號 System:系統指定單號 )
//     * @param String OrderID        訂單代號
//     * @param String ApproveCode    授權碼
//     * @param String TerminalID     端末機代碼
//     * @param String CaptureType    請款狀態 (ALL:全部  NOTCAPTURE:未請款  CAPTURE:請款完畢 )
//     * @return ResultSet            授權記錄資料
//     */
//    public ResultSet get_AuthLog_List_rs(String MerchantID, String SubMID, String StartTransDate,
//                                      String EndTransDate, String StartTransTime, String EndTransTime, 
//                                      String TransCode, String TransStatus, String OrderType,
//                                      String OrderID, String ApproveCode, String TerminalID, String CaptureType) 
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        
//        StringBuffer Sql = new StringBuffer("SELECT ROWIDTOCHAR(a.rowid) ROWID1,A.MERCHANTID, A.SUBMID, A.TERMINALID, A.ACQUIRERID, A.ORDERID, A.SYS_ORDERID, A.CARD_TYPE, A.PAN, A.EXTENNO, A.EXPIREDATE, A.TRANSCODE, A.REVERSALFLAG, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.TRANS_STATUS, A.APPROVECODE, A.RESPONSECODE, A.RESPONSEMSG, A.ENTRY_MODE, A.CONDITION_CODE, A.BATCHNO, A.USERDEFINE, A.DIRECTION, A.EMAIL, A.MTI, A.RRN, A.SOCIALID, A.TRANSMODE, A.TRANSTYPE, A.ECI, A.CAVV, A.XID, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE,  Case  When B.BALANCEAMT < 0 Then 0 When B.BALANCEAMT >=0 Then B.BALANCEAMT End As BALANCEAMT ,B.TRANSCODE BALANCE_TRANSCODE  FROM  AUTHLOG A LEFT OUTER JOIN  BILLING  B ON A.SYS_ORDERID = B.SYS_ORDERID AND A.MERCHANTID =  B.MERCHANTID AND A.SUBMID = B.SUBMID WHERE ");
//       Sql.append( "A.MERCHANTID = '" + MerchantID + "' AND A.SUBMID = '" + SubMID + "' AND TRANS_STATUS <>  'R' ");
//        if (StartTransDate != null && EndTransDate != null && StartTransDate.length() > 0 && EndTransDate.length() > 0) 
//        {
//            StartTransDate = StartTransDate.replaceAll("/", "");
//            StartTransDate = StartTransDate.replaceAll("-", "");
//            EndTransDate = EndTransDate.replaceAll("/", "");
//            EndTransDate = EndTransDate.replaceAll("-", "");
//           Sql.append( " AND A.TRANSDATE BETWEEN '" + StartTransDate + "' AND '" + EndTransDate + "'");
//        }
//        
//        if (StartTransTime.length() > 0 && EndTransTime.length() > 0) 
//        {
//           Sql.append( " AND SUBSTR(A.TRANSTIME ,1,2) BETWEEN '" + StartTransTime + "' AND '" + EndTransTime + "'");
//        }
//        
//        if (TransCode!=null&&TransCode.length() > 0 && !TransCode.equalsIgnoreCase("ALL")) 
//        {
//           Sql.append( " AND A.TRANSCODE = '" + TransCode + "' ");
//        }
//        
//        if (TransStatus!=null&&TransStatus.length() > 0 && !TransStatus.equalsIgnoreCase("ALL")) 
//        {
//           Sql.append( " AND A.TRANS_STATUS = '" + TransStatus + "' ");
//        }
//        
//        if (OrderID != null && OrderID.length() > 0) 
//        {
//            if (OrderType.equalsIgnoreCase("M")) 
//            { 
//                // 以OrderID
//               Sql.append( " AND A.ORDERID = '" + OrderID + "' ");
//            } 
//            else 
//            {
//               Sql.append( " AND A.SYS_ORDERID = '" + OrderID + "' ");
//            }
//        }
//        
//        if (ApproveCode!=null&&ApproveCode.length() > 0) 
//        {
//           Sql.append( " AND TRIM(A.APPROVECODE) = '" + ApproveCode + "' ");
//        }
//        
//        if (TerminalID != null && TerminalID.length() > 0 && !TerminalID.equalsIgnoreCase("ALL")) 
//        {
//           Sql.append( " AND A.TERMINALID = '" + TerminalID + "' ");
//        }
//        
//        if (CaptureType.length() > 0 && !CaptureType.equalsIgnoreCase("ALL")) 
//        {
//            if (CaptureType.equalsIgnoreCase("CAPTURE")) 
//            { 
//                // 已請款
//               Sql.append( " AND B.BALANCEAMT < B.TRANSAMT AND B.BALANCEAMT >= 0 AND A.TRANS_STATUS = 'A' ");
//            }
//            
//            if (CaptureType.equalsIgnoreCase("NOTCAPTURE")) 
//            { 
//                // 未請款
//               Sql.append( " AND B.BALANCEAMT > 0 AND A.TRANS_STATUS = 'A' ");
//            }
//        }
//        
//       Sql.append( " Order by A.TERMINALID, A.ORDERID, A.TRANSDATE ,A.TRANSTIME ");
//        // System.out.println("Sql=" + Sql);
//        
//        ResultSet arrayData = null;
//        try 
//        {
//            arrayData = (ResultSet) sysBean.executeReportSQL(SecurityTool.output(Sql.toString()), "select");
//        } 
//        catch (Exception ex) 
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//
//        return arrayData;
//    }
    
    /* Override get_AuthLog_List_rs with DataBaseBean parameter */
    // get_AuthLog_List method 新增引數 TransType by Jimmy Kang 20150721
    // 授權交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150721 -- 修改開始 --
    public String get_AuthLog_List_rs(DataBaseBean2 sysBean, String MerchantID, String SubMID, String StartTransDate,
                                      String EndTransDate, String StartTransTime, String EndTransTime, 
                                      String TransCode, String TransType, String TransStatus, String OrderType,
                                      String OrderID, String ApproveCode, String TerminalID, String CaptureType) 
    // 授權交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150721 -- 修改結束 --
    {
        // DataBaseBean SysBean = new DataBaseBean();
        
    	StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("SELECT ROWIDTOCHAR(a.rowid) ROWID1,A.MERCHANTID, A.SUBMID, A.TERMINALID, A.ACQUIRERID, A.ORDERID, A.SYS_ORDERID, A.CARD_TYPE, A.PAN, A.EXTENNO, A.EXPIREDATE, A.TRANSCODE, A.REVERSALFLAG, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.TRANS_STATUS, A.APPROVECODE, A.RESPONSECODE, A.RESPONSEMSG, A.ENTRY_MODE, A.CONDITION_CODE, A.BATCHNO, A.USERDEFINE, A.DIRECTION, A.EMAIL, A.MTI, A.RRN, A.SOCIALID, A.TRANSMODE, A.TRANSTYPE, A.ECI, A.CAVV, A.XID, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE,  Case  When B.BALANCEAMT < 0 Then 0 When B.BALANCEAMT >=0 Then B.BALANCEAMT End As BALANCEAMT ,B.TRANSCODE BALANCE_TRANSCODE  FROM  AUTHLOG A LEFT OUTER JOIN  BILLING  B ON A.SYS_ORDERID = B.SYS_ORDERID AND A.MERCHANTID =  B.MERCHANTID AND A.SUBMID = B.SUBMID WHERE ");
	       sSQLSB.append("A.MERCHANTID = '" + MerchantID + "' ");
	       if(!SubMID.equalsIgnoreCase("ALL")){
	    	   sSQLSB.append("AND  A.SUBMID = ?  ");
	    	   sysBean.AddSQLParam(emDataType.STR, SubMID);
	       }
	       
	       // 授權交易查詢 Trans_Status 新增P-Pending狀態 
	       // 查詢條件新增除了Trans_Status不可為 'R'外, 也不可為空白  by Jimmy Kang 20150721  -- 修改開始 --
	       //sSQLSB.append("  AND TRANS_STATUS <>  'R' ");    //註解掉 by Jimmy Kang 20170721
	       sSQLSB.append("  AND (LTRIM(RTRIM(A.TRANS_STATUS)) IS NOT NULL AND A.TRANS_STATUS NOT IN ('R')) ");
	       // 查詢條件新增除了Trans_Status不可為 'R'外, 也不可為空白  by Jimmy Kang 20150721  -- 修改結束 --
	       
	       if (StartTransDate != null && EndTransDate != null && StartTransDate.length() > 0 && EndTransDate.length() > 0) 
	        {
	            StartTransDate = StartTransDate.replaceAll("/", "");
	            StartTransDate = StartTransDate.replaceAll("-", "");
	            EndTransDate = EndTransDate.replaceAll("/", "");
	            EndTransDate = EndTransDate.replaceAll("-", "");
	            
	           sSQLSB.append(" AND A.TRANSDATE BETWEEN ? AND ? ");
	           
	           sysBean.AddSQLParam(emDataType.STR, StartTransDate);
	           sysBean.AddSQLParam(emDataType.STR, EndTransDate);
	        }
	        
	        if (StartTransTime.length() > 0 && EndTransTime.length() > 0) 
	        {
	           sSQLSB.append(" AND SUBSTR(A.TRANSTIME ,1,2) BETWEEN ? AND ? ");
	           
	           sysBean.AddSQLParam(emDataType.STR, StartTransTime);
	           sysBean.AddSQLParam(emDataType.STR, EndTransTime);
	        }
	        
	        if (TransCode!=null&&TransCode.length() > 0 && !TransCode.equalsIgnoreCase("ALL")) 
	        {
	           sSQLSB.append(" AND A.TRANSCODE = ? ");
	           
	           sysBean.AddSQLParam(emDataType.STR, TransCode);
	        }
	        
	        // 授權交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150721  -- 修改開始 --
	        if (TransType!=null&&TransType.length() > 0 && !TransType.equalsIgnoreCase("ALL")) 
	        {
	        	if (TransType.equalsIgnoreCase("VMJ"))
	        	{
	        		sSQLSB.append(" AND A.TRANSTYPE IN ('V3D', 'M3D', 'J3D') ");
	        	}
	        	else
	        	{
	        		sSQLSB.append(" AND A.TRANSTYPE = ? ");
	        		
	        		sysBean.AddSQLParam(emDataType.STR, TransType);
	        	}
	        }
	        // 授權交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150721  -- 修改結束 --
	        
	        if (TransStatus!=null&&TransStatus.length() > 0 && !TransStatus.equalsIgnoreCase("ALL")) 
	        {
	           sSQLSB.append(" AND A.TRANS_STATUS = ? ");
	           
	           sysBean.AddSQLParam(emDataType.STR, TransStatus);
	        }
	        
	        if (OrderID != null && OrderID.length() > 0) 
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
	                // 已請款
	               sSQLSB.append(" AND B.BALANCEAMT < B.TRANSAMT AND B.BALANCEAMT >= 0 AND A.TRANS_STATUS = 'A' ");
	            }
	            
	            if (CaptureType.equalsIgnoreCase("NOTCAPTURE")) 
	            { 
	                // 未請款
	               sSQLSB.append(" AND B.BALANCEAMT > 0 AND A.TRANS_STATUS = 'A' ");
	            }
	        }
	        
	       sSQLSB.append(" AND  ROWNUM < (SELECT parm_value FROM SYS_PARM_LIST WHERE PARM_ID ='MER_AUTH_QRY_QUANTITY')+1  Order by A.TERMINALID, A.ORDERID, A.TRANSDATE ,A.TRANSTIME ");
	        // System.out.println("Sql=" + Sql);
        
//        ResultSet arrayData = null;
//		  ArrayList arrayData = new ArrayList();       
//        try 
//        {
//       	  /** 2023/05/15 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-043 */
//            arrayData = (ResultSet) sysBean.executeReportSQL(Sql, "select");
//        } 
//        catch (Exception ex) 
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }

        return sSQLSB.toString();
    }
//    /**
//     * 取得授權記錄查詢檢視資料(AuthLog+Billing)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店代號
//     * @param String SysOrderID     系統指定訂單代號
//     * @return Hashtable            授權記錄資料
//     */
//    public Hashtable get_AuthLog_View(String MerchantID, String SubMID, String SysOrderID,String rowid) 
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        Hashtable hashData = new Hashtable();
//        
//        StringBuffer Sql = new StringBuffer("SELECT A.MERCHANTID, A.SUBMID, A.TERMINALID, A.ACQUIRERID, A.ORDERID, A.SYS_ORDERID, A.CARD_TYPE, A.PAN, A.EXTENNO, A.EXPIREDATE, A.TRANSCODE, A.REVERSALFLAG, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.TRANS_STATUS, A.APPROVECODE, A.RESPONSECODE, A.RESPONSEMSG, A.ENTRY_MODE, A.CONDITION_CODE, A.BATCHNO, A.USERDEFINE, A.DIRECTION, A.EMAIL, A.MTI, A.RRN, A.SOCIALID, A.TRANSMODE, A.TRANSTYPE, A.ECI, A.CAVV, A.XID, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE, B.BALANCEAMT  FROM  AUTHLOG A LEFT OUTER JOIN  BILLING  B ON A.SYS_ORDERID = B.SYS_ORDERID AND A.MERCHANTID =  B.MERCHANTID AND A.SUBMID = B.SUBMID WHERE ");
//       Sql.append( " A.MERCHANTID = '" + MerchantID + "' AND A.SUBMID = '" +
//              SubMID + "' AND A.SYS_ORDERID = '" + SysOrderID + "' and  ROWIDTOCHAR(a.rowid)='"+rowid+"' " );
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
//        if (arrayData.size() > 0) 
//        {
//            hashData = (Hashtable) arrayData.get(0);
//        }
//        
//        return hashData;
//    }

    /* Override get_AuthLog_View with DataBaseBean parameter */
    public Hashtable get_AuthLog_View(DataBaseBean2 sysBean, String MerchantID, String SubMID, String SysOrderID,String rowid) 
    {
        // DataBaseBean SysBean = new DataBaseBean();
        Hashtable hashData = new Hashtable();
        
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("SELECT A.MERCHANTID, A.SUBMID, A.TERMINALID, A.ACQUIRERID, A.ORDERID, A.SYS_ORDERID, A.CARD_TYPE, A.PAN, A.EXTENNO, A.EXPIREDATE, A.TRANSCODE, A.REVERSALFLAG, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.TRANS_STATUS, A.APPROVECODE, A.RESPONSECODE, A.RESPONSEMSG, A.ENTRY_MODE, A.CONDITION_CODE, A.BATCHNO, A.USERDEFINE, A.DIRECTION, A.EMAIL, A.MTI, A.RRN, A.SOCIALID, A.TRANSMODE, A.TRANSTYPE, A.ECI, A.CAVV, A.XID, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE, B.BALANCEAMT  FROM  AUTHLOG A LEFT OUTER JOIN  BILLING  B ON A.SYS_ORDERID = B.SYS_ORDERID AND A.MERCHANTID =  B.MERCHANTID AND A.SUBMID = B.SUBMID WHERE ");
		sSQLSB.append(" A.MERCHANTID = ? AND A.SUBMID = '" +
              SubMID + "' AND A.SYS_ORDERID = ? and  ROWIDTOCHAR(a.rowid) = ? " );
		
		sysBean.AddSQLParam(emDataType.STR, MerchantID);
		sysBean.AddSQLParam(emDataType.STR, SysOrderID);
		sysBean.AddSQLParam(emDataType.STR, rowid);
		
        // System.out.println("Sql=" + Sql);
        ArrayList arrayData = new ArrayList();
        
        try 
        {
        	/** 2023/05/15 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-042 */
            arrayData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        } 
        catch (Exception ex) 
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
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
