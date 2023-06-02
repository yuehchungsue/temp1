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
public class MerchantReAuthReportBean
{
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    private  DataBaseBean2 sysBean = new DataBaseBean2();
    public MerchantReAuthReportBean()
    {
    }

//    /**
//     * 取得再授權交易統計報表資料(FileStatus)
//     * @param String MerchantID     特店代號
//     * @param String StartTransDate 查詢起日
//     * @param String EndTransDate   查詢迄日
//     * @param String ReauthFlag     再授權結果
//     * @return ResultSet            授權記錄資料
//     */
//    public ResultSet get_FileStatus_Report_rs(String MerchantID, String StartTransDate, String EndTransDate, String ReauthFlag)
//    {
//        SysBean = new DataBaseBean();
//        String Sql = "SELECT MERCHANT_ID, TO_CHAR(TO_DATE(FILE_DATE, 'yyyymmdd'), 'yyyy/mm/dd'), SUM(REAUTH_COUNT) AS SUM_REAUTH_CNT, SUM(REAUTH_AMOUNT) AS SUM_REAUTH_AMT, SUM(NVL(REAUTH_SUCCESS_COUNT,0)) AS SUM_REAUTH_SUCCESS_CNT, SUM(NVL(REAUTH_SUCCESS_AMOUNT,0)) AS SUM_REAUTH_SUCCESS_AMT, SUM(NVL(REAUTH_FAIL_COUNT,0)) AS SUM_REAUTH_FAIL_CNT, SUM(NVL(REAUTH_FAIL_AMOUNT,0)) AS SUM_REAUTH_FAIL_AMT, SUM(NVL(REAUTH_PROCESS_COUNT,0)) AS SUM_REAUTH_PROCESS_CNT, SUM(NVL(REAUTH_PROCESS_AMOUNT,0)) AS SUM_REAUTH_PROCESS_AMT FROM FILESTATUS WHERE ";
//        Sql = Sql + " MERCHANT_ID = '"+MerchantID+"' ";
//        Sql = Sql + " AND FILE_DATE >= TO_CHAR(TO_DATE('"+StartTransDate+"','YYYY/MM/DD') ,'YYYYMMDD') ";
//        Sql = Sql + " AND FILE_DATE <= TO_CHAR(TO_DATE('"+EndTransDate+"','YYYY/MM/DD') ,'YYYYMMDD') ";
//        Sql = Sql + " AND FILE_TAG = 'BA' AND REAUTH_COUNT > 0    ";
//
//        if (ReauthFlag.equalsIgnoreCase("Y"))
//        {
//            Sql = Sql + " AND REAUTH_SUCCESS_COUNT > 0  ";
//        }
//
//        if (ReauthFlag.equalsIgnoreCase("N"))
//        {
//            Sql = Sql + " AND REAUTH_FAIL_COUNT > 0  ";
//        }
//
//        if (ReauthFlag.equalsIgnoreCase("R"))
//        {
//            Sql = Sql + " AND REAUTH_PROCESS_COUNT > 0  ";
//        }
//
//        Sql = Sql + " GROUP BY MERCHANT_ID,FILE_DATE  ";
//        // System.out.println("Sql=" + Sql);
//        ResultSet arrayData = null;
//
//        try
//        {
//            arrayData = (ResultSet) sysBean.executeReportSQL(SecurityTool.output(Sql), "select");
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//
//        return arrayData;
//    }

    /* Override get_FileStatus_Report_rs with DataBaseBean parameter */
    public String get_FileStatus_Report_rs(DataBaseBean2 sysBean, String MerchantID, String StartTransDate, String EndTransDate, String ReauthFlag)
    {
        // SysBean = new DataBaseBean();
    	StringBuffer sSQLSB = new StringBuffer();
		sysBean.ClearSQLParam();
		sSQLSB.append("SELECT MERCHANT_ID, TO_CHAR(TO_DATE(FILE_DATE, 'yyyymmdd'), 'yyyy/mm/dd'), SUM(REAUTH_COUNT) AS SUM_REAUTH_CNT, SUM(REAUTH_AMOUNT) AS SUM_REAUTH_AMT, SUM(NVL(REAUTH_SUCCESS_COUNT,0)) AS SUM_REAUTH_SUCCESS_CNT, SUM(NVL(REAUTH_SUCCESS_AMOUNT,0)) AS SUM_REAUTH_SUCCESS_AMT, SUM(NVL(REAUTH_FAIL_COUNT,0)) AS SUM_REAUTH_FAIL_CNT, SUM(NVL(REAUTH_FAIL_AMOUNT,0)) AS SUM_REAUTH_FAIL_AMT, SUM(NVL(REAUTH_PROCESS_COUNT,0)) AS SUM_REAUTH_PROCESS_CNT, SUM(NVL(REAUTH_PROCESS_AMOUNT,0)) AS SUM_REAUTH_PROCESS_AMT FROM FILESTATUS WHERE ");
        sSQLSB.append(" MERCHANT_ID = ? ");
        sSQLSB.append(" AND FILE_DATE >= TO_CHAR(TO_DATE(?,'YYYY/MM/DD') ,'YYYYMMDD') ");
        sSQLSB.append(" AND FILE_DATE <= TO_CHAR(TO_DATE(?,'YYYY/MM/DD') ,'YYYYMMDD') ");
        sSQLSB.append(" AND FILE_TAG = 'BA' AND REAUTH_COUNT > 0 ");
        
        sysBean.AddSQLParam(emDataType.STR, MerchantID);
        sysBean.AddSQLParam(emDataType.STR, StartTransDate);
        sysBean.AddSQLParam(emDataType.STR, EndTransDate);

        if (ReauthFlag.equalsIgnoreCase("Y"))
        {
            sSQLSB.append(" AND REAUTH_SUCCESS_COUNT > 0  ");
        }

        if (ReauthFlag.equalsIgnoreCase("N"))
        {
            sSQLSB.append(" AND REAUTH_FAIL_COUNT > 0  ");
        }

        if (ReauthFlag.equalsIgnoreCase("R"))
        {
            sSQLSB.append(" AND REAUTH_PROCESS_COUNT > 0  ");
        }

        sSQLSB.append(" GROUP BY MERCHANT_ID,FILE_DATE  ");
        // System.out.println("sSQLSB=" + sSQLSB.toString());
//        ResultSet arrayData = null;
//
//        try
//        {
//      	  /** 2023/05/18 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-031 (No Need Test) */
//            arrayData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
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
