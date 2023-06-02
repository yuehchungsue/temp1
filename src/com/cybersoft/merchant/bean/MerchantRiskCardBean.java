package com.cybersoft.merchant.bean;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import java.util.Hashtable;
import java.security.MessageDigest;
import com.cybersoft.bean.UserBean;
import com.cybersoft.bean.emDataType;
import com.fubon.security.filter.SecurityTool;
import com.cybersoft.bean.LogUtils;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MerchantRiskCardBean
{
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    private DataBaseBean2 resultBean = new DataBaseBean2();
    public MerchantRiskCardBean()
    {
    }

//    /**
//     * 取得風險卡資料(Billing+RiskCard)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店服務代號
//     * @param String OrderID        訂單代號
//     * @param String RiskDegree     風險等級
//     * @param String MaxCnt         最多筆數
//     * @return ArrayList            列表資料
//     */
//    public ArrayList get_RiskCard_List(String MerchantID, String SubMID, String RiskDegree, String OrderID, String MaxCnt)
//    {
//        ArrayList arrayData = new ArrayList(); // 顯示訂單交易資料
//        DataBaseBean SysBean = new DataBaseBean();
//
//        if (MerchantID.length() > 0 && RiskDegree.length() > 0)
//        {
//            String Sql = " SELECT X.MERCHANTID, X.ORDERID, X.PAN, X.RISK_DEGREE, X.MER_INS_USER, X.MER_INS_DATE, X.MER_UPD_USER, X.MER_UPD_DATE, X.ACQ_UPD_USER , X.ACQ_UPD_DATE ";
//            Sql = Sql + " FROM ( ";
//            Sql = Sql + " SELECT MERCHANTID, ORDERID, PAN, RISK_DEGREE, MER_INS_USER AS MER_INS_USER, TO_CHAR(MER_INS_DATE,'YYYY/MM/DD HH24:MI:SS') AS MER_INS_DATE, MER_UPD_USER, TO_CHAR(MER_UPD_DATE,'YYYY/MM/DD HH24:MI:SS') AS MER_UPD_DATE, ACQ_UPD_USER , TO_CHAR(ACQ_UPD_DATE,'YYYY/MM/DD HH24:MI:SS') AS ACQ_UPD_DATE FROM RISKCARD WHERE MERCHANTID = '" + MerchantID + "'  AND SUBMID = '" + SubMID + "'  ";
//
//            if (RiskDegree.length() > 0 && !RiskDegree.equalsIgnoreCase("ALL"))
//            {
//                Sql = Sql + " AND RISK_DEGREE = '" + RiskDegree + "' ";
//            }
//
//            if (OrderID.length() > 0)
//            {
//                Sql = Sql + " AND ORDERID LIKE '%" + OrderID + "%' ";
//            }
//
//            Sql = Sql + ") X where rownum <= "+MaxCnt;
//
////            String Sql = " SELECT X.MERCHANTID, X.ORDERID, X.PAN, X.TRANSDATE, X.TRANSTIME, X.RISK_DEGREE, X.MER_INS_USER, X.MER_INS_DATE, X.MER_UPD_USER, X.MER_UPD_DATE, X.ACQ_UPD_USER , X.ACQ_UPD_DATE ";
////            Sql = Sql + " FROM ( ";
////            Sql = Sql + " SELECT Y.* FROM ( ";
////            Sql = Sql + " SELECT A.MERCHANTID, A.ORDERID, A.PAN, A.TRANSDATE, A.TRANSTIME, B.RISK_DEGREE, B.MER_INS_USER AS MER_INS_USER, TO_CHAR(B.MER_INS_DATE,'YYYY/MM/DD HH24:MI:SS') AS MER_INS_DATE, B.MER_UPD_USER AS MER_UPD_USER, TO_CHAR(B.MER_UPD_DATE,'YYYY/MM/DD HH24:MI:SS') AS MER_UPD_DATE, B.ACQ_UPD_USER AS ACQ_UPD_USER , TO_CHAR(B.ACQ_UPD_DATE,'YYYY/MM/DD HH24:MI:SS') AS ACQ_UPD_DATE FROM BILLING A, RISKCARD B WHERE B.MERCHANTID = '" + MerchantID + "'  AND B.SUBMID = '" + SubMID + "' AND A.TRANSCODE = '00' ";
////            if (RiskDegree.length() > 0 && !RiskDegree.equalsIgnoreCase("ALL")) {
////                Sql = Sql + " AND B.RISK_DEGREE = '" + RiskDegree + "' ";
////            }
////            if (OrderID.length() > 0) {
////                Sql = Sql + " AND (A.ORDERID LIKE '%" + OrderID + "' OR A.ORDERID LIKE '" + OrderID + "%') ";
////            }
////            Sql = Sql + " AND A.MERCHANTID =  B.MERCHANTID AND TRIM(A.PAN) = B.PAN AND A.ORDERID = B.ORDERID AND TRIM(A.PAN) IN (SELECT DISTINCT TRIM(PAN) FROM RISKCARD)  ";
////            if (RiskDegree.length() == 0 || RiskDegree.equalsIgnoreCase("ALL")) {
////              Sql = Sql + " UNION ";
////              Sql = Sql + " SELECT A.MERCHANTID, A.ORDERID, A.PAN, A.TRANSDATE, A.TRANSTIME, '' AS RISK_DEGREE, '' AS MER_INS_USER, '' AS MER_INS_DATE, '' AS MER_UPD_USER, '' AS MER_UPD_DATE, '' AS ACQ_UPD_USER , '' AS ACQ_UPD_DATE FROM BILLING A  WHERE A.MERCHANTID = '" + MerchantID + "'  AND A.SUBMID = '" + SubMID + "' AND A.TRANSCODE = '00'  ";
////              if (OrderID.length() > 0) {
////                  Sql = Sql + " AND (A.ORDERID LIKE '%" + OrderID + "' OR A.ORDERID LIKE '" + OrderID + "%') ";
////              }
////              Sql = Sql + " AND TRIM(A.ORDERID) NOT IN(SELECT DISTINCT TRIM(ORDERID) FROM RISKCARD) ";
////            }
////            Sql = Sql + " ) Y ORDER BY  Y.TRANSDATE DESC, Y.TRANSTIME DESC ";
////            Sql = Sql + " ) X where rownum <= "+MaxCnt;
//            // System.out.println("Sql=" + Sql);
//
//            try
//            {
//                arrayData = (ArrayList) SysBean.executeSQL(Sql, "select");
//            }
//            catch (Exception ex)
//            {
//                System.out.println(ex.getMessage());
//                log_systeminfo.debug(ex.toString());
//            }
//
//            if (arrayData == null)
//            {
//                arrayData = new ArrayList();
//            }
//        }
//
//        return arrayData;
//    }

    /* Override get_RiskCard_List with DataBaseBean parameter */
    public ArrayList get_RiskCard_List(DataBaseBean2 sysBean, String MerchantID, String SubMID, String RiskDegree, String OrderID, String MaxCnt)
    {
        ArrayList arrayData = new ArrayList(); // 顯示訂單交易資料
        // DataBaseBean SysBean = new DataBaseBean();

        if (MerchantID.length() > 0 && RiskDegree.length() > 0)
        {
        	StringBuffer sSQLSB = new StringBuffer();
            sysBean.ClearSQLParam();
    		sSQLSB.append(" SELECT X.MERCHANTID, X.ORDERID, X.PAN, X.RISK_DEGREE, X.MER_INS_USER, X.MER_INS_DATE, X.MER_UPD_USER, X.MER_UPD_DATE, X.ACQ_UPD_USER , X.ACQ_UPD_DATE ");
            sSQLSB.append(" FROM ( ");
            sSQLSB.append(" SELECT MERCHANTID, ORDERID, PAN, RISK_DEGREE, MER_INS_USER AS MER_INS_USER, TO_CHAR(MER_INS_DATE,'YYYY/MM/DD HH24:MI:SS') AS MER_INS_DATE, MER_UPD_USER, TO_CHAR(MER_UPD_DATE,'YYYY/MM/DD HH24:MI:SS') AS MER_UPD_DATE, ACQ_UPD_USER , TO_CHAR(ACQ_UPD_DATE,'YYYY/MM/DD HH24:MI:SS') AS ACQ_UPD_DATE FROM RISKCARD WHERE MERCHANTID = ? AND SUBMID = ? ");
            sysBean.AddSQLParam(emDataType.STR, MerchantID);
            sysBean.AddSQLParam(emDataType.STR, SubMID);

            if (RiskDegree.length() > 0 && !RiskDegree.equalsIgnoreCase("ALL"))
            {
                sSQLSB.append(" AND RISK_DEGREE = ? ");
                sysBean.AddSQLParam(emDataType.STR, RiskDegree);
            }

            if (OrderID.length() > 0)
            {
                sSQLSB.append(" AND ORDERID LIKE '%" + OrderID + "%' ");
            }

            sSQLSB.append(") X where rownum <= ? ");
            sysBean.AddSQLParam(emDataType.INT, MaxCnt);

//            String Sql = " SELECT X.MERCHANTID, X.ORDERID, X.PAN, X.TRANSDATE, X.TRANSTIME, X.RISK_DEGREE, X.MER_INS_USER, X.MER_INS_DATE, X.MER_UPD_USER, X.MER_UPD_DATE, X.ACQ_UPD_USER , X.ACQ_UPD_DATE ");
//            sSQLSB.append(" FROM ( ");
//            sSQLSB.append(" SELECT Y.* FROM ( ");
//            sSQLSB.append(" SELECT A.MERCHANTID, A.ORDERID, A.PAN, A.TRANSDATE, A.TRANSTIME, B.RISK_DEGREE, B.MER_INS_USER AS MER_INS_USER, TO_CHAR(B.MER_INS_DATE,'YYYY/MM/DD HH24:MI:SS') AS MER_INS_DATE, B.MER_UPD_USER AS MER_UPD_USER, TO_CHAR(B.MER_UPD_DATE,'YYYY/MM/DD HH24:MI:SS') AS MER_UPD_DATE, B.ACQ_UPD_USER AS ACQ_UPD_USER , TO_CHAR(B.ACQ_UPD_DATE,'YYYY/MM/DD HH24:MI:SS') AS ACQ_UPD_DATE FROM BILLING A, RISKCARD B WHERE B.MERCHANTID = '" + MerchantID + "'  AND B.SUBMID = '" + SubMID + "' AND A.TRANSCODE = '00' ");
//            if (RiskDegree.length() > 0 && !RiskDegree.equalsIgnoreCase("ALL")) {
//                sSQLSB.append(" AND B.RISK_DEGREE = '" + RiskDegree + "' ");
//            }
//            if (OrderID.length() > 0) {
//                sSQLSB.append(" AND (A.ORDERID LIKE '%" + OrderID + "' OR A.ORDERID LIKE '" + OrderID + "%') ");
//            }
//            sSQLSB.append(" AND A.MERCHANTID =  B.MERCHANTID AND TRIM(A.PAN) = B.PAN AND A.ORDERID = B.ORDERID AND TRIM(A.PAN) IN (SELECT DISTINCT TRIM(PAN) FROM RISKCARD)  ");
//            if (RiskDegree.length() == 0 || RiskDegree.equalsIgnoreCase("ALL")) {
//              sSQLSB.append(" UNION ");
//              sSQLSB.append(" SELECT A.MERCHANTID, A.ORDERID, A.PAN, A.TRANSDATE, A.TRANSTIME, '' AS RISK_DEGREE, '' AS MER_INS_USER, '' AS MER_INS_DATE, '' AS MER_UPD_USER, '' AS MER_UPD_DATE, '' AS ACQ_UPD_USER , '' AS ACQ_UPD_DATE FROM BILLING A  WHERE A.MERCHANTID = '" + MerchantID + "'  AND A.SUBMID = '" + SubMID + "' AND A.TRANSCODE = '00'  ");
//              if (OrderID.length() > 0) {
//                  sSQLSB.append(" AND (A.ORDERID LIKE '%" + OrderID + "' OR A.ORDERID LIKE '" + OrderID + "%') ");
//              }
//              sSQLSB.append(" AND TRIM(A.ORDERID) NOT IN(SELECT DISTINCT TRIM(ORDERID) FROM RISKCARD) ");
//            }
//            sSQLSB.append(" ) Y ORDER BY  Y.TRANSDATE DESC, Y.TRANSTIME DESC ");
//            sSQLSB.append(" ) X where rownum <= "+MaxCnt);
            // System.out.println("Sql=" + Sql);

            try
            {
            	/** 2023/05/16 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-068 */
                arrayData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
                log_systeminfo.debug(ex.toString());
            }

            if (arrayData == null)
            {
                arrayData = new ArrayList();
            }
        }

        return arrayData;
    }

//    /**
//     * 取得新增風險卡(Billing)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店服務代號
//     * @param String OrderID        訂單代號
//     * @return Hashtable            列表資料
//     */
//    public Hashtable get_Billing_List(String MerchantID, String SubMID, String OrderID)
//    {
//        ArrayList arrayData = new ArrayList(); // 顯示訂單交易資料
//        Hashtable hashData = new Hashtable();
//        DataBaseBean SysBean = new DataBaseBean();
//
//        if (MerchantID.length() > 0 && OrderID.length() > 0)
//        {
//            String Sql = " SELECT MERCHANTID, ORDERID, PAN FROM BILLING WHERE MERCHANTID = '" + MerchantID + "'  AND SUBMID = '" + SubMID + "' AND TRANSCODE = '00' ";
//            Sql = Sql + " AND ORDERID = '" + OrderID + "' ";
//            // System.out.println("Sql=" + Sql);
//
//            try
//            {
//                arrayData = (ArrayList) SysBean.executeSQL(Sql, "select");
//                if (arrayData ==null) arrayData = new ArrayList();
//                if (arrayData.size()>0)
//                {
//                    hashData = (Hashtable)arrayData.get(0);
//                }
//            }
//            catch (Exception ex)
//            {
//                System.out.println(ex.getMessage());
//                log_systeminfo.debug(ex.toString());
//            }
//        }
//
//        return hashData;
//    }

    /* Override get_Billing_List with DataBaseBean parameter */
    public Hashtable get_Billing_List(DataBaseBean2 sysBean, String MerchantID, String SubMID, String OrderID)
    {
        ArrayList arrayData = new ArrayList(); // 顯示訂單交易資料
        Hashtable hashData = new Hashtable();
        // DataBaseBean SysBean = new DataBaseBean();

        if (MerchantID.length() > 0 && OrderID.length() > 0)
        {
        	StringBuffer sSQLSB = new StringBuffer();
            sysBean.ClearSQLParam();
    		sSQLSB.append(" SELECT MERCHANTID, ORDERID, PAN FROM BILLING WHERE MERCHANTID = ?  AND SUBMID = ? AND TRANSCODE = '00' ");
    		sSQLSB.append(" AND ORDERID = ? ");
    		
    		sysBean.AddSQLParam(emDataType.STR, MerchantID);
    		sysBean.AddSQLParam(emDataType.STR, SubMID);
    		sysBean.AddSQLParam(emDataType.STR, OrderID);
    		
            // System.out.println("Sql=" + Sql);

            try
            {
            	/** 2023/05/16 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-069 */
                arrayData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
                if (arrayData ==null) arrayData = new ArrayList();
                if (arrayData.size()>0)
                {
                    hashData = (Hashtable)arrayData.get(0);
                }
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
                log_systeminfo.debug(ex.toString());
            }
        }

        return hashData;
    }

//    /**
//     * 取得指定風險卡資料筆數(RiskCard)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店服務代號
//     * @param String Pan            信用卡卡號
//     * @return int Count            風險卡筆數
//     */
//    public int get_RiskCard_Count(String MerchantID, String SubMID, String Pan)
//    {
//        int Count = 0;
//        ArrayList arrayList = new ArrayList();
//        DataBaseBean SysBean = new DataBaseBean();
//
//        if (MerchantID.length() > 0 && SubMID.length() > 0 && Pan.length() > 0)
//        {
//            String Sql = " SELECT COUNT(PAN) AS COUNT FROM RISKCARD WHERE MERCHANTID = '" +
//                           MerchantID + "' AND SUBMID = '" + SubMID + "' AND TRIM(PAN) = '" + Pan + "'";
//            // System.out.println("Sql=" + Sql);
//
//            try
//            {
//                arrayList = (ArrayList) SysBean.executeSQL(Sql, "select");
//            }
//            catch (Exception ex)
//            {
//                System.out.println(ex.getMessage());
//                log_systeminfo.debug(ex.toString());
//            }
//
//            if (arrayList == null)
//            {
//                arrayList = new ArrayList();
//            }
//
//            if (arrayList.size() > 0)
//            {
//                Hashtable hashData = (Hashtable) arrayList.get(0);
//                if (hashData != null && hashData.size() > 0)
//                {
//                    String strCount = (String) hashData.get("COUNT");
//                    if (strCount != null && strCount.length() > 0)
//                    {
//                        Count = Integer.parseInt(strCount);
//                    }
//                }
//            }
//        }
//
//        return Count;
//    }

    /* Override get_RiskCard_Count with DataBaseBean parameter */
    public int get_RiskCard_Count(DataBaseBean2 sysBean, String MerchantID, String SubMID, String Pan)
    {
        int Count = 0;
        ArrayList arrayList = new ArrayList();
        // DataBaseBean SysBean = new DataBaseBean();

        if (MerchantID.length() > 0 && SubMID.length() > 0 && Pan.length() > 0)
        {
        	StringBuffer sSQLSB = new StringBuffer();
            sysBean.ClearSQLParam();
    		sSQLSB.append(" SELECT COUNT(PAN) AS COUNT FROM RISKCARD WHERE MERCHANTID = ? AND SUBMID = ? AND TRIM(PAN) = ? ");
    		
    		sysBean.AddSQLParam(emDataType.STR, MerchantID);
    		sysBean.AddSQLParam(emDataType.STR, SubMID);
    		sysBean.AddSQLParam(emDataType.STR, Pan);
    		
            // System.out.println("Sql=" + Sql);

            try
            {
            	/** 2023/05/16 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-073 */
                arrayList = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
                log_systeminfo.debug(ex.toString());
            }

            if (arrayList == null)
            {
                arrayList = new ArrayList();
            }

            if (arrayList.size() > 0)
            {
                Hashtable hashData = (Hashtable) arrayList.get(0);
                if (hashData != null && hashData.size() > 0)
                {
                    String strCount = (String) hashData.get("COUNT");
                    if (strCount != null && strCount.length() > 0)
                    {
                        Count = Integer.parseInt(strCount);
                    }
                }
            }
        }

        return Count;
    }

//    /**
//     * 取得指定風險卡資料(RiskCard)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店服務代號
//     * @param String Pan            信用卡卡號
//     * @return Hashtable            風險卡資料
//     */
//    public Hashtable get_RiskCard_Hash(String MerchantID, String SubMID, String Pan)
//    {
//        ArrayList arrayList = new ArrayList();
//        Hashtable hashData = new Hashtable();
//        DataBaseBean SysBean = new DataBaseBean();
//
//        if (MerchantID.length() > 0 && SubMID.length() > 0 && Pan.length() > 0)
//        {
//            String Sql = " SELECT MERCHANTID, ORDERID, PAN, MER_INS_USER, TO_CHAR(MER_INS_DATE,'YYYY/MM/DD HH24:MI:SS') AS MER_INS_DATE  FROM RISKCARD WHERE MERCHANTID = '" +
//                           MerchantID + "' AND SUBMID = '" + SubMID + "' AND TRIM(PAN) = '" + Pan + "'";
//            // System.out.println("Sql=" + Sql);
//
//            try
//            {
//                arrayList = (ArrayList) SysBean.executeSQL(Sql, "select");
//            }
//            catch (Exception ex)
//            {
//                System.out.println(ex.getMessage());
//                log_systeminfo.debug(ex.toString());
//            }
//
//            if (arrayList == null)
//            {
//                arrayList = new ArrayList();
//            }
//
//            if (arrayList.size() > 0)
//            {
//                hashData = (Hashtable) arrayList.get(0);
//            }
//        }
//
//        return hashData;
//    }

    /* Override get_RiskCard_Hash with DataBaseBean parameter */
    public Hashtable get_RiskCard_Hash(DataBaseBean2 sysBean, String MerchantID, String SubMID, String Pan)
    {
        ArrayList arrayList = new ArrayList();
        Hashtable hashData = new Hashtable();
        // DataBaseBean SysBean = new DataBaseBean();

        if (MerchantID.length() > 0 && SubMID.length() > 0 && Pan.length() > 0)
        {
        	StringBuffer sSQLSB = new StringBuffer();
            sysBean.ClearSQLParam();
    		sSQLSB.append(" SELECT MERCHANTID, ORDERID, PAN, MER_INS_USER, TO_CHAR(MER_INS_DATE,'YYYY/MM/DD HH24:MI:SS') AS MER_INS_DATE  FROM RISKCARD WHERE MERCHANTID = ? AND SUBMID = ? AND TRIM(PAN) = ? ");
    		
    		sysBean.AddSQLParam(emDataType.STR, MerchantID);
    		sysBean.AddSQLParam(emDataType.STR, SubMID);
    		sysBean.AddSQLParam(emDataType.STR, Pan);
    		
            // System.out.println("Sql=" + Sql);

            try
            {
            	/** 2023/05/16 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-070 */
                arrayList = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
                log_systeminfo.debug(ex.toString());
            }

            if (arrayList == null)
            {
                arrayList = new ArrayList();
            }

            if (arrayList.size() > 0)
            {
                hashData = (Hashtable) arrayList.get(0);
            }
        }

        return hashData;
    }

//    /**
//     * 新增風險卡資料 (RiskCard)
//     * @param  String MerchantID    特店代號
//     * @param  String SubMID            特店服務代號
//     * @param  String Pan           信用卡卡號
//     * @param  String OrderID           客戶指定單號
//     * @param  String Risk_Gegree   風險等級
//     * @param  String Mer_Ins_User  新增風險卡人員
//     * @param  String Mer_Ins_Date  新增風險卡日期
//     * @return boolean                  新增結果
//     */
//    public boolean insert_RiskCard(String MerchantID, String SubMID, String Pan, String OrderID,
//                                   String Risk_Gegree, String Mer_Ins_User, String Mer_Ins_Date)
//    {
//        boolean Flag = false;
//        DataBaseBean SysBean = new DataBaseBean();
//
//        String Sql = "INSERT INTO RISKCARD (MERCHANTID, SUBMID, PAN, ORDERID, RISK_DEGREE, MER_INS_USER, MER_INS_DATE) VALUES ('" +
//                     MerchantID + "','" + SubMID + "','" + Pan + "','" +
//                     OrderID + "','" + Risk_Gegree + "','" + Mer_Ins_User +
//                     "',TO_DATE('" + Mer_Ins_Date + "','YYYY/MM/DD HH24:MI:SS') )";
//        // System.out.println("Sql=" + Sql);
//
//        try
//        {
//            Flag = ((Boolean) SysBean.executeSQL(Sql, "update")).booleanValue();
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//
//        return Flag;
//    }

    /* Override insert_RiskCard with DataBaseBean parameter */
    public boolean insert_RiskCard(DataBaseBean2 sysBean, String MerchantID, String SubMID, String Pan, String OrderID,
                                   String Risk_Gegree, String Mer_Ins_User, String Mer_Ins_Date)
    {
        boolean flag = false;
        // DataBaseBean SysBean = new DataBaseBean();

        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
        sSQLSB.append("INSERT INTO RISKCARD (MERCHANTID, SUBMID, PAN, ORDERID, RISK_DEGREE, MER_INS_USER, MER_INS_DATE) VALUES (?, ?, ?, ?, ?, ?, TO_DATE(?, 'YYYY/MM/DD HH24:MI:SS') )");
		
		sysBean.AddSQLParam(emDataType.STR, MerchantID);
		sysBean.AddSQLParam(emDataType.STR, SubMID);
		sysBean.AddSQLParam(emDataType.STR, Pan);
		sysBean.AddSQLParam(emDataType.STR, OrderID);
		sysBean.AddSQLParam(emDataType.STR, Risk_Gegree);
		sysBean.AddSQLParam(emDataType.STR, Mer_Ins_User);
		sysBean.AddSQLParam(emDataType.STR, Mer_Ins_Date);
		
        // System.out.println("Sql=" + Sql);

		ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/05/16 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-071 */
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
//     * 維護風險卡資料 (RiskCard)
//     * @param  String MerchantID    特店代號
//     * @param  String SubMID            特店服務代號
//     * @param  String Pan           信用卡卡號
//     * @param  String OrderID           客戶指定單號
//     * @param  String Risk_Gegree   風險等級
//     * @param  String Mer_Upd_User  維護風險卡人員
//     * @param  String Mer_Upd_Date  維護風險卡日期
//     * @return boolean                  維護結果
//     */
//    public boolean update_RiskCard(String MerchantID, String SubMID, String Pan, String OrderID,
//                                   String Risk_Gegree, String Mer_Upd_User, String Mer_Upd_Date)
//    {
//        boolean Flag = false;
//        DataBaseBean SysBean = new DataBaseBean();
//
//        String Sql = "UPDATE RISKCARD SET RISK_DEGREE = '" + Risk_Gegree +
//                     "', ORDERID = '" + OrderID + "', MER_UPD_USER = '" +
//                     Mer_Upd_User + "', MER_UPD_DATE = TO_DATE('" + Mer_Upd_Date +
//                     "','YYYY/MM/DD HH24:MI:SS')  WHERE MERCHANTID = '" +
//                     MerchantID + "' AND SUBMID = '" + SubMID + "' AND PAN = '" + Pan + "'";
//        // System.out.println("Sql=" + Sql);
//
//        try
//        {
//            Flag = ((Boolean) SysBean.executeSQL(Sql, "update")).booleanValue();
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//
//        return Flag;
//    }

    /* Override update_RiskCard with DataBaseBean parameter */
    public boolean update_RiskCard(DataBaseBean2 sysBean, String MerchantID, String SubMID, String Pan, String OrderID,
                                   String Risk_Gegree, String Mer_Upd_User, String Mer_Upd_Date)
    {
        boolean flag = false;
        // DataBaseBean SysBean = new DataBaseBean();

        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("UPDATE RISKCARD SET RISK_DEGREE = ?, ORDERID = ?, MER_UPD_USER = ?, MER_UPD_DATE = TO_DATE(?, 'YYYY/MM/DD HH24:MI:SS')  WHERE MERCHANTID = ? AND SUBMID = ? AND PAN = ?");
		
		sysBean.AddSQLParam(emDataType.STR, Risk_Gegree);
		sysBean.AddSQLParam(emDataType.STR, OrderID);
		sysBean.AddSQLParam(emDataType.STR, Mer_Upd_Date);
		sysBean.AddSQLParam(emDataType.STR, Mer_Upd_User);
		sysBean.AddSQLParam(emDataType.STR, MerchantID);
		sysBean.AddSQLParam(emDataType.STR, SubMID);
		sysBean.AddSQLParam(emDataType.STR, Pan);
		
        // System.out.println("Sql=" + Sql);

		ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/05/16 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-072 */
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
//     * 取得風險交易資料(Batch)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店代號
//     * @param String OrderID        特店指定單號
//     * @param String Risk_Degree    風簽等級
//     * @param boolean Flag          是否為ROWDATA
//     * @return ResultSet            風險卡資料
//     */
//    public ResultSet get_RiskCard_Result(String MerchantID, String SubMID, String RiskDegree, String OrderID, boolean Flag)
//    {
//        ResultBean = new DataBaseBean();
//        String Sql = "";
//
//        if (MerchantID.length() > 0 && RiskDegree.length() > 0)
//        {
//            Sql = " SELECT MERCHANTID, ORDERID, SUBMID , PAN , ORDERID, RISK_DEGREE , MER_INS_USER, TO_CHAR(MER_INS_DATE,'YYYYMMDDHH24MISS') AS MER_INS_DATE, MER_UPD_USER, TO_CHAR(MER_UPD_DATE,'YYYYMMDDHH24MISS') AS MER_UPD_DATE, ACQ_UPD_USER , TO_CHAR(ACQ_UPD_DATE,'YYYYMMDDHH24MISS') AS ACQ_UPD_DATE FROM  RISKCARD WHERE MERCHANTID = '" + MerchantID + "'  AND SUBMID = '" + SubMID + "' ";
//            if (RiskDegree.length() > 0 && !RiskDegree.equalsIgnoreCase("ALL"))
//            {
//               Sql = Sql + " AND RISK_DEGREE = '" + RiskDegree + "' ";
//            }
//
//            if (OrderID.length() > 0)
//            {
//               Sql = Sql + " AND ORDERID LIKE '%" + OrderID + "%'  ";
//            }
//
////            Sql = " SELECT A.MERCHANTID, A.ORDERID, A.PAN, B.SUBMID , B.PAN ,B.ORDERID, B.RISK_DEGREE , B.MER_INS_USER, TO_CHAR(B.MER_INS_DATE,'YYYYMMDDHH24MISS') AS MER_INS_DATE, B.MER_UPD_USER, TO_CHAR(B.MER_UPD_DATE,'YYYYMMDDHH24MISS') AS MER_UPD_DATE, B.ACQ_UPD_USER , TO_CHAR(B.ACQ_UPD_DATE,'YYYYMMDDHH24MISS') AS ACQ_UPD_DATE FROM BILLING A, RISKCARD B WHERE B.MERCHANTID = '" + MerchantID + "'  AND B.SUBMID = '" + SubMID + "' AND A.TRANSCODE = '00' ";
////            if (RiskDegree.length() > 0 && !RiskDegree.equalsIgnoreCase("ALL")) {
////                Sql = Sql + " AND B.RISK_DEGREE = '" + RiskDegree + "' ";
////            }
////            if (OrderID.length() > 0) {
////                Sql = Sql + " AND (A.ORDERID LIKE '%" + OrderID + "' OR A.ORDERID LIKE '" + OrderID + "%') ";
////            }
////            Sql = Sql + " AND A.MERCHANTID =  B.MERCHANTID AND TRIM(A.PAN) = B.PAN AND A.ORDERID = B.ORDERID AND TRIM(A.PAN) IN (SELECT DISTINCT TRIM(PAN) FROM RISKCARD) ";
////            if (RiskDegree.length() == 0 || RiskDegree.equalsIgnoreCase("ALL")) {
////                Sql = Sql + " UNION ";
////                Sql = Sql + " SELECT A.MERCHANTID, A.ORDERID, A.PAN, ' ' AS SUBMID , ' ' AS PAN ,' ' AS  ORDERID, ' ' AS RISK_DEGREE , ' ' AS MER_INS_USER, ' ' AS MER_INS_DATE, ' ' AS MER_UPD_USER, ' ' AS MER_UPD_DATE, ' ' AS ACQ_UPD_USER , ' ' AS ACQ_UPD_DATE  FROM BILLING A  WHERE A.MERCHANTID = '" +
////                      MerchantID + "' AND A.SUBMID = '" + SubMID +
////                      "' AND A.TRANSCODE = '00' ";
////                if (OrderID.length() > 0) {
////                    Sql = Sql + " AND (A.ORDERID LIKE '%" + OrderID +
////                          "' OR A.ORDERID LIKE '" + OrderID + "%') ";
////                }
////                Sql = Sql +" AND TRIM(A.ORDERID) NOT IN(SELECT DISTINCT TRIM(ORDERID) FROM RISKCARD) ";
////            }
//            // System.out.println("Sql=" + Sql);
//        }
//        ResultSet ResultData = null;
//
//        try
//        {
//            ResultData = (ResultSet) resultBean.executeReportSQL(SecurityTool.output(Sql), "select");
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//        }
//
//        return ResultData;
//    }

    /* Override get_RiskCard_Result with DataBaseBean parameter */
    public String get_RiskCard_Result(DataBaseBean2 sysBean, String MerchantID, String SubMID, String RiskDegree, String OrderID, boolean Flag)
    {
        // ResultBean = new DataBaseBean();
        String Sql = "";

        if (MerchantID.length() > 0 && RiskDegree.length() > 0)
        {
        	StringBuffer sSQLSB = new StringBuffer();
            sysBean.ClearSQLParam();
    		sSQLSB.append(" SELECT MERCHANTID, ORDERID, SUBMID , PAN , ORDERID, RISK_DEGREE , MER_INS_USER, TO_CHAR(MER_INS_DATE,'YYYYMMDDHH24MISS') AS MER_INS_DATE, MER_UPD_USER, TO_CHAR(MER_UPD_DATE,'YYYYMMDDHH24MISS') AS MER_UPD_DATE, ACQ_UPD_USER , TO_CHAR(ACQ_UPD_DATE,'YYYYMMDDHH24MISS') AS ACQ_UPD_DATE FROM  RISKCARD WHERE MERCHANTID = ?  AND SUBMID = ? ");
    		sysBean.AddSQLParam(emDataType.STR, MerchantID);
    		sysBean.AddSQLParam(emDataType.STR, SubMID);
    		
            if (RiskDegree.length() > 0 && !RiskDegree.equalsIgnoreCase("ALL"))
            {
               sSQLSB.append(" AND RISK_DEGREE = ? ");
               sysBean.AddSQLParam(emDataType.STR, RiskDegree);
            }

            if (OrderID.length() > 0)
            {
               sSQLSB.append(" AND ORDERID LIKE '%" + OrderID + "%'  ");
            }

            // System.out.println("Sql=" + Sql);
        }
//        ResultSet arraySys = null;
//
//        try
//        {
//      	  /** 2023/05/16 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-074 */
//            arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//        }

        return Sql;
    }

    public void closeConn()
    {
        try
        {
            resultBean.close();
        }
        catch (SQLException ex)
        {
        }
    }
}
