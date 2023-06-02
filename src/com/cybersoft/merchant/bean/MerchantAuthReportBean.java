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

public class MerchantAuthReportBean
{
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    private  DataBaseBean2 sysBean = new DataBaseBean2();
    public MerchantAuthReportBean()
    {
    }

//    /**
//     * ���o���v�O���d�ߦC����(AuthLog+Merchant)
//     * @param String MerchantID     �S���N��
//     * @param String SubMID         �S���N��
//     * @param String StartTransDate �d�߰_��
//     * @param String EndTransDate   �d�ߨ���
//     * @param String StartTransTime �d�߰_��
//     * @param String EndTransTime   �d�ߨ���
//     * @param String TransCode      ������O
//     * @return ResultSet            ���v�O�����
//     */
//    public ResultSet get_AuthLog_Report_rs(String MerchantID, String SubMID, String StartTransDate,
//                                      String EndTransDate, String StartTransTime, String EndTransTime, String TransCode)
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        StringBuffer Sql = new StringBuffer("SELECT A.MERCHANTID, A.SUBMID, A.TERMINALID, A.ACQUIRERID, A.ORDERID, A.SYS_ORDERID, A.CARD_TYPE, A.PAN, A.EXTENNO, A.EXPIREDATE, A.TRANSCODE, A.REVERSALFLAG, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.TRANS_STATUS, A.APPROVECODE, A.RESPONSECODE, A.RESPONSEMSG, A.ENTRY_MODE, A.CONDITION_CODE, A.BATCHNO, A.USERDEFINE, A.DIRECTION, A.EMAIL, A.MTI, A.RRN, A.SOCIALID, A.TRANSMODE, A.TRANSTYPE, A.ECI, A.CAVV, A.XID, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE, A.SYSTRACENO, B.MERCHANTCALLNAME  FROM  AUTHLOG A , MERCHANT B WHERE");
//       Sql.append( " A.MERCHANTID = '" + MerchantID + "' AND  A.SUBMID = B.SUBMID  ");
//       //new add �ڥI�_
//       if(SubMID != null  && !SubMID.equals("all")){
//    	   Sql.append( " AND A.SUBMID = '" + SubMID + "'  ");
//       }
//        if (StartTransDate != null && EndTransDate != null && StartTransDate.length() > 0 && EndTransDate.length() > 0)
//        {
//           Sql.append( " AND A.TRANSDATE BETWEEN '" + StartTransDate + "' AND '" + EndTransDate + "'");
//        }
//
//        if (StartTransTime.length() > 0 && EndTransTime.length() > 0)
//        {
//           Sql.append( " AND SUBSTR(A.TRANSTIME,1,2) BETWEEN '" + StartTransTime + "' AND '" + EndTransTime + "'");
//        }
//
//        if (TransCode!=null&&TransCode.length() > 0 && !TransCode.equalsIgnoreCase("ALL"))
//        {
//           Sql.append( " AND A.TRANSCODE IN ('10','11') ");
//        }
//
//       Sql.append( " AND A.MERCHANTID = B.MERCHANTID  Order by A.TRANSDATE ,A.TRANSTIME ");
//        // System.out.println("Sql=" + Sql);
//        ResultSet arrayData = null;
//
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

    /* Override get_AuthLog_Report_rs with DataBaseBean parameter */
    // get_AuthLog_Report_rs method �s�W�޼� TransType by Jimmy Kang 20150730
    // ���v����έp���� �s�W �d�߱��� TransType by Jimmy Kang 20150730 -- �ק�}�l --
    public String get_AuthLog_Report_rs(DataBaseBean2 sysBean, String MerchantID, String SubMID, String StartTransDate,
                                      String EndTransDate, String StartTransTime, String EndTransTime, String TransCode, String TransType)
    // ���v����έp���� �s�W �d�߱��� TransType by Jimmy Kang 20150730  -- �קﵲ�� --
    {
        // DataBaseBean SysBean = new DataBaseBean();
//        StringBuffer Sql = new StringBuffer("SELECT A.MERCHANTID, A.SUBMID, A.TERMINALID, A.ACQUIRERID, A.ORDERID, A.SYS_ORDERID, A.CARD_TYPE, A.PAN, A.EXTENNO, A.EXPIREDATE, A.TRANSCODE, A.REVERSALFLAG, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.TRANS_STATUS, A.APPROVECODE, A.RESPONSECODE, A.RESPONSEMSG, A.ENTRY_MODE, A.CONDITION_CODE, A.BATCHNO, A.USERDEFINE, A.DIRECTION, A.EMAIL, A.MTI, A.RRN, A.SOCIALID, A.TRANSMODE, A.TRANSTYPE, A.ECI, A.CAVV, A.XID, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE, A.SYSTRACENO, B.MERCHANTCALLNAME  FROM  AUTHLOG A , MERCHANT B WHERE");
    	StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("SELECT  A.MERCHANTID, A.SUBMID, A.TERMINALID, A.TRANSCODE, A.TRANSDATE, A.TRANS_STATUS, sum(A.TRANSAMT) TRANSAMT, B.MERCHANTCALLNAME  ,COUNT(*) CNT    FROM  AUTHLOG A , MERCHANT B ");
        
        // ���v����έp���� Trans_Status �s�WP-Pending���A 
        // �d�߱���s�WTrans_Status���i�� 'R', 'P' �M�ť�  by Jimmy Kang 20150721  -- �ק�}�l --
        sSQLSB.append(" WHERE (LTRIM(RTRIM(A.TRANS_STATUS)) IS NOT NULL AND A.TRANS_STATUS NOT IN ('R', 'P')) AND ");
        // �d�߱���s�WTrans_Status���i�� 'R', 'P' �M�ť�  by Jimmy Kang 20150721  -- �ק�}�l --
        		
        sSQLSB.append(" A.MERCHANTID = ? AND A.SUBMID = B.SUBMID ");
        sysBean.AddSQLParam(emDataType.STR, MerchantID);
        
       //new add �ڥI�_
       if(SubMID != null  && !SubMID.equals("all")){
    	   sSQLSB.append(" AND A.SUBMID = ?  ");
    	   sysBean.AddSQLParam(emDataType.STR, SubMID);
       }
        
        if (StartTransDate != null && EndTransDate != null && StartTransDate.length() > 0 && EndTransDate.length() > 0)
        {
           sSQLSB.append(" AND A.TRANSDATE BETWEEN ? AND ? ");
           sysBean.AddSQLParam(emDataType.STR, StartTransDate);
           sysBean.AddSQLParam(emDataType.STR, EndTransDate);
        }

        if (StartTransTime.length() > 0 && EndTransTime.length() > 0)
        {
           sSQLSB.append(" AND SUBSTR(A.TRANSTIME,1,2) BETWEEN ? AND ? ");
           sysBean.AddSQLParam(emDataType.STR, StartTransTime);
           sysBean.AddSQLParam(emDataType.STR, EndTransTime);
        }

        if (TransCode!=null&&TransCode.length() > 0 && !TransCode.equalsIgnoreCase("ALL"))
        {
           sSQLSB.append(" AND A.TRANSCODE IN ('10','11') ");
        }
        
        // ���v����έp���� �s�W �d�߱��� TransType by Jimmy Kang 20150730  -- �ק�}�l --
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
        // ���v����έp���� �s�W �d�߱��� TransType by Jimmy Kang 20150730  -- �קﵲ�� --


       sSQLSB.append(" AND A.MERCHANTID = B.MERCHANTID GROUP  BY A.MERCHANTID, A.SUBMID, A.TERMINALID, A.TRANSCODE, A.TRANSDATE, A.TRANS_STATUS, B.MERCHANTCALLNAME   Order by A.TRANSDATE  ");
        // System.out.println("Sql=" + Sql);
//        ResultSet arrayData = null;

//        try
//        {
//            /** 2023/05/15 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-048 */
//            arrayData = (ResultSet) sysBean.executeReportSQL(Sql, "select");
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }

        return sSQLSB.toString();
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
