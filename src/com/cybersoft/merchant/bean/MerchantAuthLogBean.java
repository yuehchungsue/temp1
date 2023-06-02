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
//     * ���o���v�O���d�ߦC����(AuthLog+Billing)
//     * @param String MerchantID     �S���N��
//     * @param String SubMID         �S���N��
//     * @param String StartTransDate �d�߰_��
//     * @param String EndTransDate   �d�ߨ���
//     * @param String StartTransTime �d�߰_��
//     * @param String EndTransTime   �d�ߨ���
//     * @param String TransCode      ������O
//     * @param String TransStatus    ���v���G
//     * @param String OrderType      �d�߭q�����O (Order:�S�����w�渹 System:�t�Ϋ��w�渹 )
//     * @param String OrderID        �q��N��
//     * @param String ApproveCode    ���v�X
//     * @param String TerminalID     �ݥ����N�X
//     * @param String CaptureType    �дڪ��A (ALL:����  NOTCAPTURE:���д�  CAPTURE:�дڧ��� )
//     * @return ArrayList            ���v�O�����
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
//                // �HOrderID
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
//                // �w�д�
//               Sql.append( " AND B.BALANCEAMT < B.TRANSAMT AND B.BALANCEAMT >= 0 AND A.TRANS_STATUS = 'A' ");
//            }
//            
//            if (CaptureType.equalsIgnoreCase("NOTCAPTURE")) 
//            { // ���д�
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
    // get_AuthLog_List method �s�W�޼� TransType by Jimmy Kang 20150721
    // ���v����d�� �s�W �d�߱��� TransType by Jimmy Kang 20150721 -- �ק�}�l --
    public ArrayList get_AuthLog_List(DataBaseBean2 sysBean, String MerchantID, String SubMID, String StartTransDate,
                                      String EndTransDate, String StartTransTime, String EndTransTime, 
                                      String TransCode, String TransType, String TransStatus, String OrderType,
                                      String OrderID, String ApproveCode, String TerminalID, String CaptureType)
    // ���v����d�� �s�W �d�߱��� TransType by Jimmy Kang 20150721 -- �קﵲ�� --
    {
        //DataBaseBean SysBean = new DataBaseBean();

    	StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("SELECT ROWIDTOCHAR(a.rowid) ROWID1,A.MERCHANTID, A.SUBMID, A.TERMINALID, A.ACQUIRERID, A.ORDERID, A.SYS_ORDERID, A.CARD_TYPE, A.PAN, A.EXTENNO, A.EXPIREDATE, A.TRANSCODE, A.REVERSALFLAG, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.TRANS_STATUS, A.APPROVECODE, A.RESPONSECODE, A.RESPONSEMSG, A.ENTRY_MODE, A.CONDITION_CODE, A.BATCHNO, A.USERDEFINE, A.DIRECTION, A.EMAIL, A.MTI, A.RRN, A.SOCIALID, A.TRANSMODE, A.TRANSTYPE, A.ECI, A.CAVV, A.XID, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE, Case  When B.BALANCEAMT < 0 Then 0 When B.BALANCEAMT >=0 Then B.BALANCEAMT End As BALANCEAMT ,B.TRANSCODE BALANCE_TRANSCODE  ");
        sSQLSB.append("FROM  AUTHLOG A LEFT OUTER JOIN  BILLING  B ON A.SYS_ORDERID = B.SYS_ORDERID AND A.MERCHANTID =  B.MERCHANTID AND A.SUBMID = B.SUBMID   ");
     
        // ���v����d�� Trans_Status �s�WP-Pending���A 
        // �d�߱���s�W���FTrans_Status���i�� 'R'�~, �]���i���ť�  by Jimmy Kang 20150721  -- �ק�}�l --
        //sSQLSB.append(" WHERE A.MERCHANTID = '" + MerchantID + "' AND TRANS_STATUS <> 'R' ");   //���ѱ� by Jimmy Kang 20150721
        sSQLSB.append(" WHERE A.MERCHANTID = ? AND (LTRIM(RTRIM(A.TRANS_STATUS)) IS NOT NULL AND A.TRANS_STATUS NOT IN ('R')) ");
        sysBean.AddSQLParam(emDataType.STR, MerchantID);
        
        // �d�߱���s�W���FTrans_Status���i�� 'R'�~, �]���i���ť�  by Jimmy Kang 20150721  -- �קﵲ�� --
        
        //new add �ڥI�_
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
        
        // ���v����d�� �s�W �d�߱��� TransType by Jimmy Kang 20150721  -- �ק�}�l --
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
        // ���v����d�� �s�W �d�߱��� TransType by Jimmy Kang 20150721  -- �קﵲ�� --
        
        if (TransStatus!=null&&TransStatus.length() > 0 && !TransStatus.equalsIgnoreCase("ALL")) 
        {
           sSQLSB.append(" AND A.TRANS_STATUS = ? ");
           sysBean.AddSQLParam(emDataType.STR, TransStatus);
        }
        
        if (OrderID!=null&&OrderID.length() > 0) 
        {
            if (OrderType.equalsIgnoreCase("M")) 
            { 
                // �HOrderID
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
                // �w�д�
               sSQLSB.append(" AND B.BALANCEAMT < B.TRANSAMT AND B.BALANCEAMT >= 0 AND A.TRANS_STATUS = 'A' ");
            }
            
            if (CaptureType.equalsIgnoreCase("NOTCAPTURE")) 
            { // ���д�
               sSQLSB.append(" AND B.BALANCEAMT > 0 AND A.TRANS_STATUS = 'A' ");
            }
        }
        
       sSQLSB.append("  AND  ROWNUM < (SELECT parm_value FROM SYS_PARM_LIST WHERE PARM_ID ='MER_AUTH_QRY_QUANTITY')+2  Order by A.TERMINALID, A.ORDERID, A.TRANSDATE ,A.TRANSTIME ");
        // System.out.println("Sql=" + Sql);
        
        ArrayList arrayData = new ArrayList();
        try 
        {
        	/** 2023/05/22 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-040 */
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
    // get_AuthLog_Count method �s�W�޼� TransType by Jimmy Kang 20150721
    // ���v����d�� �s�W �d�߱��� TransType by Jimmy Kang 20150721 -- �ק�}�l --
    public ArrayList get_AuthLog_Count(DataBaseBean2 sysBean, String MerchantID, String SubMID, String StartTransDate,
                                      String EndTransDate, String StartTransTime, String EndTransTime, 
                                      String TransCode, String TransType, String TransStatus, String OrderType, 
                                      String OrderID, String ApproveCode, String TerminalID, String CaptureType) 
    // ���v����d�� �s�W �d�߱��� TransType by Jimmy Kang 20150721  -- �קﵲ�� --
    {
        //DataBaseBean SysBean = new DataBaseBean();

    	StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("SELECT  COUNT(*) TOTAL    ");
        sSQLSB.append(" FROM  AUTHLOG A LEFT OUTER JOIN  BILLING  B ON A.SYS_ORDERID = B.SYS_ORDERID AND A.MERCHANTID =  B.MERCHANTID AND A.SUBMID = B.SUBMID   ");
        
        // ���v����d�� Trans_Status �s�WP-Pending���A 
        // �d�߱���s�W���FTrans_Status���i�� 'R'�~, �]���i���ť�  by Jimmy Kang 20150721  -- �ק�}�l --
        //sSQLSB.append("WHERE A.MERCHANTID = '" + MerchantID + "' AND TRANS_STATUS <> 'R' ");    //���ѱ� by Jimmy Kang 20150721
        sSQLSB.append("WHERE A.MERCHANTID = '" + MerchantID + "' AND (LTRIM(RTRIM(A.TRANS_STATUS)) IS NOT NULL AND A.TRANS_STATUS NOT IN ('R')) ");
        // �d�߱���s�W���FTrans_Status���i�� 'R'�~, �]���i���ť�  by Jimmy Kang 20150721  -- �קﵲ�� --
        
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
         
         // ���v����d�� �s�W �d�߱��� TransType by Jimmy Kang 20150721  -- �ק�}�l --
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
         // ���v����d�� �s�W �d�߱��� TransType by Jimmy Kang 20150721  -- �קﵲ�� --
         
         if (TransStatus!=null&&TransStatus.length() > 0 && !TransStatus.equalsIgnoreCase("ALL")) 
         {
            sSQLSB.append(" AND A.TRANS_STATUS = ? ");
            
            sysBean.AddSQLParam(emDataType.STR, TransStatus);
         }
         
         if (OrderID!=null&&OrderID.length() > 0) 
         {
             if (OrderType.equalsIgnoreCase("M")) 
             { 
                 // �HOrderID
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
                 // �w�д�
                sSQLSB.append(" AND B.BALANCEAMT < B.TRANSAMT AND B.BALANCEAMT >= 0 AND A.TRANS_STATUS = 'A' ");
             }
             
             if (CaptureType.equalsIgnoreCase("NOTCAPTURE")) 
             { // ���д�
                sSQLSB.append(" AND B.BALANCEAMT > 0 AND A.TRANS_STATUS = 'A' ");
             }
         }
         
       
        // System.out.println("Sql=" + Sql);
        
        ArrayList arrayData = new ArrayList();
        try 
        {
        	/** 2023/05/15 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-041 */
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
//     * ���o���v�O���d�ߦC����(AuthLog+Billing)
//     * @param String MerchantID     �S���N��
//     * @param String SubMID         �S���N��
//     * @param String StartTransDate �d�߰_��
//     * @param String EndTransDate   �d�ߨ���
//     * @param String StartTransTime �d�߰_��
//     * @param String EndTransTime   �d�ߨ���
//     * @param String TransCode      ������O
//     * @param String TransStatus    ���v���G
//     * @param String OrderType      �d�߭q�����O (Order:�S�����w�渹 System:�t�Ϋ��w�渹 )
//     * @param String OrderID        �q��N��
//     * @param String ApproveCode    ���v�X
//     * @param String TerminalID     �ݥ����N�X
//     * @param String CaptureType    �дڪ��A (ALL:����  NOTCAPTURE:���д�  CAPTURE:�дڧ��� )
//     * @return ResultSet            ���v�O�����
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
//                // �HOrderID
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
//                // �w�д�
//               Sql.append( " AND B.BALANCEAMT < B.TRANSAMT AND B.BALANCEAMT >= 0 AND A.TRANS_STATUS = 'A' ");
//            }
//            
//            if (CaptureType.equalsIgnoreCase("NOTCAPTURE")) 
//            { 
//                // ���д�
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
    // get_AuthLog_List method �s�W�޼� TransType by Jimmy Kang 20150721
    // ���v����d�� �s�W �d�߱��� TransType by Jimmy Kang 20150721 -- �ק�}�l --
    public String get_AuthLog_List_rs(DataBaseBean2 sysBean, String MerchantID, String SubMID, String StartTransDate,
                                      String EndTransDate, String StartTransTime, String EndTransTime, 
                                      String TransCode, String TransType, String TransStatus, String OrderType,
                                      String OrderID, String ApproveCode, String TerminalID, String CaptureType) 
    // ���v����d�� �s�W �d�߱��� TransType by Jimmy Kang 20150721 -- �קﵲ�� --
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
	       
	       // ���v����d�� Trans_Status �s�WP-Pending���A 
	       // �d�߱���s�W���FTrans_Status���i�� 'R'�~, �]���i���ť�  by Jimmy Kang 20150721  -- �ק�}�l --
	       //sSQLSB.append("  AND TRANS_STATUS <>  'R' ");    //���ѱ� by Jimmy Kang 20170721
	       sSQLSB.append("  AND (LTRIM(RTRIM(A.TRANS_STATUS)) IS NOT NULL AND A.TRANS_STATUS NOT IN ('R')) ");
	       // �d�߱���s�W���FTrans_Status���i�� 'R'�~, �]���i���ť�  by Jimmy Kang 20150721  -- �קﵲ�� --
	       
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
	        
	        // ���v����d�� �s�W �d�߱��� TransType by Jimmy Kang 20150721  -- �ק�}�l --
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
	        // ���v����d�� �s�W �d�߱��� TransType by Jimmy Kang 20150721  -- �קﵲ�� --
	        
	        if (TransStatus!=null&&TransStatus.length() > 0 && !TransStatus.equalsIgnoreCase("ALL")) 
	        {
	           sSQLSB.append(" AND A.TRANS_STATUS = ? ");
	           
	           sysBean.AddSQLParam(emDataType.STR, TransStatus);
	        }
	        
	        if (OrderID != null && OrderID.length() > 0) 
	        {
	            if (OrderType.equalsIgnoreCase("M")) 
	            { 
	                // �HOrderID
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
	                // �w�д�
	               sSQLSB.append(" AND B.BALANCEAMT < B.TRANSAMT AND B.BALANCEAMT >= 0 AND A.TRANS_STATUS = 'A' ");
	            }
	            
	            if (CaptureType.equalsIgnoreCase("NOTCAPTURE")) 
	            { 
	                // ���д�
	               sSQLSB.append(" AND B.BALANCEAMT > 0 AND A.TRANS_STATUS = 'A' ");
	            }
	        }
	        
	       sSQLSB.append(" AND  ROWNUM < (SELECT parm_value FROM SYS_PARM_LIST WHERE PARM_ID ='MER_AUTH_QRY_QUANTITY')+1  Order by A.TERMINALID, A.ORDERID, A.TRANSDATE ,A.TRANSTIME ");
	        // System.out.println("Sql=" + Sql);
        
//        ResultSet arrayData = null;
//		  ArrayList arrayData = new ArrayList();       
//        try 
//        {
//       	  /** 2023/05/15 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-043 */
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
//     * ���o���v�O���d���˵����(AuthLog+Billing)
//     * @param String MerchantID     �S���N��
//     * @param String SubMID         �S���N��
//     * @param String SysOrderID     �t�Ϋ��w�q��N��
//     * @return Hashtable            ���v�O�����
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
        	/** 2023/05/15 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-042 */
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
