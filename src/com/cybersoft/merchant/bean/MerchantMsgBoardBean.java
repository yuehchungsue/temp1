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
import com.cybersoft.bean.LogUtils;
import java.text.DecimalFormat;
import java.text.ParseException;

public class MerchantMsgBoardBean
{
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    public MerchantMsgBoardBean()
    {
    }

//    /**
//     * 取得公告訊息-請款狀態資料(Capture)
//     * @param String MerchantID      特店代號
//     * @param String SubMID          特店服務代號
//     * @param String Today           查詢指定日期-今日(yyyy/MM/DD)
//     * @param String Mer_Board_Day   公告期限
//     * @return ArrayList             列表資料
//     */
//    public ArrayList get_MsgBoard_Caputre_List(String MerchantID, String SubMID, String Today, String Mer_Board_Day)
//    {
//        ArrayList arrayData = new ArrayList(); // 顯示訂單交易資料
//        DataBaseBean SysBean = new DataBaseBean();
//        if (MerchantID.length() > 0 && SubMID.length() > 0 && Today.length() > 0 && Mer_Board_Day.length() > 0 )
//        {
//            String Sql = " SELECT Y.CAPTUREDATE , SUM(Y.CAPTURECOUNT) AS CAPTURECOUNT, SUM(Y.CAPTUREAMT) AS CAPTUREAMT, SUM(Y.CAPTUREPROCESSCOUNT) AS CAPTUREPROCESSCOUNT, SUM(Y.CAPTUREPROCESSAMT) AS CAPTUREPROCESSAMT, SUM(Y.CAPTUREAPPROVECOUNT) AS CAPTUREAPPROVECOUNT, SUM(Y.CAPTUREAPPROVEAMT) AS CAPTUREAPPROVEAMT, SUM(Y.CAPTUREREJECTCOUNT) AS CAPTUREREJECTCOUNT, SUM(Y.CAPTUREREJECTAMT) AS CAPTUREREJECTAMT  FROM ( ";
//            Sql = Sql + " SELECT TO_CHAR(X.CAPTUREDATE,'YYYY/MM/DD') AS CAPTUREDATE , SUM(X.CAPTURECOUNT) AS CAPTURECOUNT, SUM(X.CAPTUREAMT) AS CAPTUREAMT, SUM(X.CAPTUREPROCESSCOUNT) AS CAPTUREPROCESSCOUNT, SUM(X.CAPTUREPROCESSAMT) AS CAPTUREPROCESSAMT, SUM(X.CAPTUREAPPROVECOUNT) AS CAPTUREAPPROVECOUNT, SUM(X.CAPTUREAPPROVEAMT) AS CAPTUREAPPROVEAMT, SUM(X.CAPTUREREJECTCOUNT) AS CAPTUREREJECTCOUNT, SUM(X.CAPTUREREJECTAMT) AS CAPTUREREJECTAMT  FROM ( ";
//            Sql = Sql + " SELECT CAPTUREDATE , COUNT(CAPTUREAMT) AS CAPTURECOUNT, SUM(CAPTUREAMT) AS CAPTUREAMT, 0 AS CAPTUREPROCESSCOUNT, 0 AS CAPTUREPROCESSAMT, 0 AS CAPTUREAPPROVECOUNT, 0 AS CAPTUREAPPROVEAMT, 0 AS CAPTUREREJECTCOUNT, 0 AS CAPTUREREJECTAMT FROM CAPTURE WHERE MERCHANTID = '"+MerchantID+"' AND SUBMID= '"+SubMID+"' AND CAPTUREFLAG = '0' AND CAPTUREDATE >= TO_DATE('"+Today+"', 'YYYYMMDD') - "+Mer_Board_Day+"  AND TO_DATE(TO_CHAR(CAPTUREDATE, 'YYYYMMDD'),'YYYYMMDD' ) <= TO_DATE('"+Today+"', 'YYYYMMDD') AND TRANSCODE = '00'  GROUP BY CAPTUREDATE ";
//            Sql = Sql + " UNION ";
//            Sql = Sql + " SELECT CAPTUREDATE , 0 AS CAPTURECOUNT, 0 AS CAPTUREAMT, COUNT(CAPTUREAMT) AS CAPTUREPROCESSCOUNT, SUM(CAPTUREAMT) AS CAPTUREPROCESSAMT, 0 AS CAPTUREAPPROVECOUNT, 0 AS CAPTUREAPPROVEAMT, 0 AS CAPTUREREJECTCOUNT, 0 AS CAPTUREREJECTAMT FROM CAPTURE WHERE MERCHANTID = '"+MerchantID+"' AND SUBMID= '"+SubMID+"' AND CAPTUREFLAG = '1' AND CAPTUREDATE >= TO_DATE('"+Today+"', 'YYYYMMDD') - "+Mer_Board_Day+"  AND TO_DATE(TO_CHAR(CAPTUREDATE, 'YYYYMMDD'),'YYYYMMDD' ) <= TO_DATE('"+Today+"', 'YYYYMMDD') AND TRANSCODE = '00'   GROUP BY CAPTUREDATE ";
//            Sql = Sql + " UNION ";
//            Sql = Sql + " SELECT CAPTUREDATE , 0 AS CAPTURECOUNT, 0 AS CAPTUREAMT, 0 AS CAPTUREPROCESSCOUNT, 0 AS CAPTUREPROCESSAMT, COUNT(CAPTUREAMT) AS CAPTUREAPPROVECOUNT, SUM(CAPTUREAMT) AS CAPTUREAPPROVEAMT, 0 AS CAPTUREREJECTCOUNT, 0 AS CAPTUREREJECTAMT FROM CAPTURE WHERE MERCHANTID = '"+MerchantID+"' AND SUBMID= '"+SubMID+"' AND CAPTUREFLAG = '3' AND FEEDBACKCODE = '000' AND CAPTUREDATE >= TO_DATE('"+Today+"', 'YYYYMMDD') - "+Mer_Board_Day+"  AND TO_DATE(TO_CHAR(CAPTUREDATE, 'YYYYMMDD'),'YYYYMMDD' ) <= TO_DATE('"+Today+"', 'YYYYMMDD') AND TRANSCODE = '00'   GROUP BY CAPTUREDATE ";
//            Sql = Sql + " UNION ";
//            Sql = Sql + " SELECT CAPTUREDATE , 0 AS CAPTURECOUNT, 0 AS CAPTUREAMT, 0 AS CAPTUREPROCESSCOUNT, 0 AS CAPTUREPROCESSAMT, 0 AS CAPTUREAPPROVECOUNT, 0 AS CAPTUREAPPROVEAMT, COUNT(CAPTUREAMT) AS CAPTUREREJECTCOUNT, SUM(CAPTUREAMT) AS CAPTUREREJECTAMT FROM CAPTURE WHERE MERCHANTID = '"+MerchantID+"' AND SUBMID= '"+SubMID+"' AND CAPTUREFLAG = '3' AND FEEDBACKCODE <> '000' AND CAPTUREDATE >= TO_DATE('"+Today+"', 'YYYYMMDD') - "+Mer_Board_Day+"  AND TO_DATE(TO_CHAR(CAPTUREDATE, 'YYYYMMDD'),'YYYYMMDD' ) <= TO_DATE('"+Today+"', 'YYYYMMDD') AND TRANSCODE = '00'   GROUP BY CAPTUREDATE ";
//            Sql = Sql + " UNION ";
//            Sql = Sql + " SELECT CAPTUREDATE , COUNT(CAPTUREAMT) AS CAPTURECOUNT, SUM(CAPTUREAMT) * -1 AS CAPTUREAMT, 0 AS CAPTUREPROCESSCOUNT, 0 AS CAPTUREPROCESSAMT, 0 AS CAPTUREAPPROVECOUNT, 0 AS CAPTUREAPPROVEAMT, 0 AS CAPTUREREJECTCOUNT, 0 AS CAPTUREREJECTAMT FROM CAPTURE WHERE MERCHANTID = '"+MerchantID+"' AND SUBMID= '"+SubMID+"' AND CAPTUREFLAG = '0' AND CAPTUREDATE >= TO_DATE('"+Today+"', 'YYYYMMDD') - "+Mer_Board_Day+"  AND TO_DATE(TO_CHAR(CAPTUREDATE, 'YYYYMMDD'),'YYYYMMDD' ) <= TO_DATE('"+Today+"', 'YYYYMMDD') AND TRANSCODE = '01'   GROUP BY CAPTUREDATE ";
//            Sql = Sql + " UNION ";
//            Sql = Sql + " SELECT CAPTUREDATE , 0 AS CAPTURECOUNT, 0 AS CAPTUREAMT, COUNT(CAPTUREAMT) AS CAPTUREPROCESSCOUNT, SUM(CAPTUREAMT) * -1 AS CAPTUREPROCESSAMT, 0 AS CAPTUREAPPROVECOUNT, 0 AS CAPTUREAPPROVEAMT, 0 AS CAPTUREREJECTCOUNT, 0 AS CAPTUREREJECTAMT FROM CAPTURE WHERE MERCHANTID = '"+MerchantID+"' AND SUBMID= '"+SubMID+"' AND CAPTUREFLAG = '1' AND CAPTUREDATE >= TO_DATE('"+Today+"', 'YYYYMMDD') - "+Mer_Board_Day+"  AND TO_DATE(TO_CHAR(CAPTUREDATE, 'YYYYMMDD'),'YYYYMMDD' ) <= TO_DATE('"+Today+"', 'YYYYMMDD') AND TRANSCODE = '01'  GROUP BY CAPTUREDATE ";
//            Sql = Sql + " UNION ";
//            Sql = Sql + " SELECT CAPTUREDATE , 0 AS CAPTURECOUNT, 0 AS CAPTUREAMT, 0 AS CAPTUREPROCESSCOUNT, 0 AS CAPTUREPROCESSAMT, COUNT(CAPTUREAMT) AS CAPTUREAPPROVECOUNT, SUM(CAPTUREAMT) * -1 AS CAPTUREAPPROVEAMT, 0 AS CAPTUREREJECTCOUNT, 0 AS CAPTUREREJECTAMT FROM CAPTURE WHERE MERCHANTID = '"+MerchantID+"' AND SUBMID= '"+SubMID+"' AND CAPTUREFLAG = '3' AND FEEDBACKCODE = '000' AND CAPTUREDATE >= TO_DATE('"+Today+"', 'YYYYMMDD') - "+Mer_Board_Day+"  AND TO_DATE(TO_CHAR(CAPTUREDATE, 'YYYYMMDD'),'YYYYMMDD' ) <= TO_DATE('"+Today+"', 'YYYYMMDD') AND TRANSCODE = '01'  GROUP BY CAPTUREDATE ";
//            Sql = Sql + " UNION ";
//            Sql = Sql + " SELECT CAPTUREDATE , 0 AS CAPTURECOUNT, 0 AS CAPTUREAMT, 0 AS CAPTUREPROCESSCOUNT, 0 AS CAPTUREPROCESSAMT, 0 AS CAPTUREAPPROVECOUNT, 0 AS CAPTUREAPPROVEAMT, COUNT(CAPTUREAMT) AS CAPTUREREJECTCOUNT, SUM(CAPTUREAMT) * -1 AS CAPTUREREJECTAMT FROM CAPTURE WHERE MERCHANTID = '"+MerchantID+"' AND SUBMID= '"+SubMID+"' AND CAPTUREFLAG = '3' AND FEEDBACKCODE <> '000' AND CAPTUREDATE >= TO_DATE('"+Today+"', 'YYYYMMDD') - "+Mer_Board_Day+"  AND TO_DATE(TO_CHAR(CAPTUREDATE, 'YYYYMMDD'),'YYYYMMDD' ) <= TO_DATE('"+Today+"', 'YYYYMMDD') AND TRANSCODE = '01'  GROUP BY CAPTUREDATE ";
//            Sql = Sql + " ) X  GROUP BY  X.CAPTUREDATE ";
//            Sql = Sql + " ) Y  GROUP BY  Y.CAPTUREDATE ORDER BY  Y.CAPTUREDATE ";
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

    /* Override get_MsgBoard_Caputre_List with DataBaseBean parameter */
    public ArrayList get_MsgBoard_Caputre_List(DataBaseBean2 sysBean, String MerchantID, String SubMID, String Today, String Mer_Board_Day)
    {
        ArrayList arrayData = new ArrayList(); // 顯示訂單交易資料
        // DataBaseBean SysBean = new DataBaseBean();
        if (MerchantID.length() > 0 && SubMID.length() > 0 && Today.length() > 0 && Mer_Board_Day.length() > 0 )
        {
        	StringBuffer sSQLSB = new StringBuffer();
            sysBean.ClearSQLParam();
    		sSQLSB.append(" SELECT Y.CAPTUREDATE , SUM(Y.CAPTURECOUNT) AS CAPTURECOUNT, SUM(Y.CAPTUREAMT) AS CAPTUREAMT, SUM(Y.CAPTUREPROCESSCOUNT) AS CAPTUREPROCESSCOUNT, SUM(Y.CAPTUREPROCESSAMT) AS CAPTUREPROCESSAMT, SUM(Y.CAPTUREAPPROVECOUNT) AS CAPTUREAPPROVECOUNT, SUM(Y.CAPTUREAPPROVEAMT) AS CAPTUREAPPROVEAMT, SUM(Y.CAPTUREREJECTCOUNT) AS CAPTUREREJECTCOUNT, SUM(Y.CAPTUREREJECTAMT) AS CAPTUREREJECTAMT  FROM ( ");
            sSQLSB.append(" SELECT TO_CHAR(X.CAPTUREDATE,'YYYY/MM/DD') AS CAPTUREDATE , SUM(X.CAPTURECOUNT) AS CAPTURECOUNT, SUM(X.CAPTUREAMT) AS CAPTUREAMT, SUM(X.CAPTUREPROCESSCOUNT) AS CAPTUREPROCESSCOUNT, SUM(X.CAPTUREPROCESSAMT) AS CAPTUREPROCESSAMT, SUM(X.CAPTUREAPPROVECOUNT) AS CAPTUREAPPROVECOUNT, SUM(X.CAPTUREAPPROVEAMT) AS CAPTUREAPPROVEAMT, SUM(X.CAPTUREREJECTCOUNT) AS CAPTUREREJECTCOUNT, SUM(X.CAPTUREREJECTAMT) AS CAPTUREREJECTAMT  FROM ( ");
            sSQLSB.append(" SELECT CAPTUREDATE , COUNT(CAPTUREAMT) AS CAPTURECOUNT, SUM(CAPTUREAMT) AS CAPTUREAMT, 0 AS CAPTUREPROCESSCOUNT, 0 AS CAPTUREPROCESSAMT, 0 AS CAPTUREAPPROVECOUNT, 0 AS CAPTUREAPPROVEAMT, 0 AS CAPTUREREJECTCOUNT, 0 AS CAPTUREREJECTAMT FROM CAPTURE "
            		+ "WHERE MERCHANTID = ? AND SUBMID= ? AND CAPTUREFLAG = '0' AND CAPTUREDATE >= TO_DATE(?, 'YYYYMMDD') - "+Mer_Board_Day+"  "
            		+ "AND TO_DATE(TO_CHAR(CAPTUREDATE, 'YYYYMMDD'),'YYYYMMDD' ) <= TO_DATE(?, 'YYYYMMDD') AND TRANSCODE = '00'  GROUP BY CAPTUREDATE ");
            sysBean.AddSQLParam(emDataType.STR, MerchantID);
            sysBean.AddSQLParam(emDataType.STR, SubMID);
            sysBean.AddSQLParam(emDataType.STR, Today);
            sysBean.AddSQLParam(emDataType.STR, Today);
            sSQLSB.append(" UNION ");
            sSQLSB.append(" SELECT CAPTUREDATE , 0 AS CAPTURECOUNT, 0 AS CAPTUREAMT, COUNT(CAPTUREAMT) AS CAPTUREPROCESSCOUNT, SUM(CAPTUREAMT) AS CAPTUREPROCESSAMT, 0 AS CAPTUREAPPROVECOUNT, 0 AS CAPTUREAPPROVEAMT, 0 AS CAPTUREREJECTCOUNT, 0 AS CAPTUREREJECTAMT FROM CAPTURE "
            		+ "WHERE MERCHANTID = ? AND SUBMID = ? AND CAPTUREFLAG = '1' AND CAPTUREDATE >= TO_DATE(?, 'YYYYMMDD') - "+Mer_Board_Day+"  "
            		+ "AND TO_DATE(TO_CHAR(CAPTUREDATE, 'YYYYMMDD'),'YYYYMMDD' ) <= TO_DATE(?, 'YYYYMMDD') AND TRANSCODE = '00'   GROUP BY CAPTUREDATE ");
            sysBean.AddSQLParam(emDataType.STR, MerchantID);
            sysBean.AddSQLParam(emDataType.STR, SubMID);
            sysBean.AddSQLParam(emDataType.STR, Today);
            sSQLSB.append(" UNION ");
            sSQLSB.append(" SELECT CAPTUREDATE , 0 AS CAPTURECOUNT, 0 AS CAPTUREAMT, 0 AS CAPTUREPROCESSCOUNT, 0 AS CAPTUREPROCESSAMT, COUNT(CAPTUREAMT) AS CAPTUREAPPROVECOUNT, SUM(CAPTUREAMT) AS CAPTUREAPPROVEAMT, 0 AS CAPTUREREJECTCOUNT, 0 AS CAPTUREREJECTAMT FROM CAPTURE "
            		+ "WHERE MERCHANTID = ? AND SUBMID = ? AND CAPTUREFLAG = '3' AND FEEDBACKCODE = '000' AND CAPTUREDATE >= TO_DATE(?', 'YYYYMMDD') - "+Mer_Board_Day+"  "
            		+ "AND TO_DATE(TO_CHAR(CAPTUREDATE, 'YYYYMMDD'),'YYYYMMDD' ) <= TO_DATE(?, 'YYYYMMDD') AND TRANSCODE = '00'   GROUP BY CAPTUREDATE ");
            sysBean.AddSQLParam(emDataType.STR, MerchantID);
            sysBean.AddSQLParam(emDataType.STR, SubMID);
            sysBean.AddSQLParam(emDataType.STR, Today);
            sysBean.AddSQLParam(emDataType.STR, Today);
            sSQLSB.append(" UNION ");
            sSQLSB.append(" SELECT CAPTUREDATE , 0 AS CAPTURECOUNT, 0 AS CAPTUREAMT, 0 AS CAPTUREPROCESSCOUNT, 0 AS CAPTUREPROCESSAMT, 0 AS CAPTUREAPPROVECOUNT, 0 AS CAPTUREAPPROVEAMT, COUNT(CAPTUREAMT) AS CAPTUREREJECTCOUNT, SUM(CAPTUREAMT) AS CAPTUREREJECTAMT FROM CAPTURE "
            		+ "WHERE MERCHANTID = ? AND SUBMID = ? AND CAPTUREFLAG = '3' AND FEEDBACKCODE <> '000' AND CAPTUREDATE >= TO_DATE(?, 'YYYYMMDD') - "+Mer_Board_Day+"  "
            		+ "AND TO_DATE(TO_CHAR(CAPTUREDATE, 'YYYYMMDD'),'YYYYMMDD' ) <= TO_DATE(?, 'YYYYMMDD') AND TRANSCODE = '00'   GROUP BY CAPTUREDATE ");
            sysBean.AddSQLParam(emDataType.STR, MerchantID);
            sysBean.AddSQLParam(emDataType.STR, SubMID);
            sysBean.AddSQLParam(emDataType.STR, Today);
            sysBean.AddSQLParam(emDataType.STR, Today);
            sSQLSB.append(" UNION ");
            sSQLSB.append(" SELECT CAPTUREDATE , COUNT(CAPTUREAMT) AS CAPTURECOUNT, SUM(CAPTUREAMT) * -1 AS CAPTUREAMT, 0 AS CAPTUREPROCESSCOUNT, 0 AS CAPTUREPROCESSAMT, 0 AS CAPTUREAPPROVECOUNT, 0 AS CAPTUREAPPROVEAMT, 0 AS CAPTUREREJECTCOUNT, 0 AS CAPTUREREJECTAMT FROM CAPTURE "
            		+ "WHERE MERCHANTID = ? AND SUBMID= ? AND CAPTUREFLAG = '0' AND CAPTUREDATE >= TO_DATE(?, 'YYYYMMDD') - "+Mer_Board_Day+"  "
            		+ "AND TO_DATE(TO_CHAR(CAPTUREDATE, 'YYYYMMDD'),'YYYYMMDD' ) <= TO_DATE(?, 'YYYYMMDD') AND TRANSCODE = '01'   GROUP BY CAPTUREDATE ");
            sysBean.AddSQLParam(emDataType.STR, MerchantID);
            sysBean.AddSQLParam(emDataType.STR, SubMID);
            sysBean.AddSQLParam(emDataType.STR, Today);
            sysBean.AddSQLParam(emDataType.STR, Today);
            sSQLSB.append(" UNION ");
            sSQLSB.append(" SELECT CAPTUREDATE , 0 AS CAPTURECOUNT, 0 AS CAPTUREAMT, COUNT(CAPTUREAMT) AS CAPTUREPROCESSCOUNT, SUM(CAPTUREAMT) * -1 AS CAPTUREPROCESSAMT, 0 AS CAPTUREAPPROVECOUNT, 0 AS CAPTUREAPPROVEAMT, 0 AS CAPTUREREJECTCOUNT, 0 AS CAPTUREREJECTAMT FROM CAPTURE "
            		+ "WHERE MERCHANTID = ? AND SUBMID = ? AND CAPTUREFLAG = '1' AND CAPTUREDATE >= TO_DATE(?, 'YYYYMMDD') - "+Mer_Board_Day+"  "
            		+ "AND TO_DATE(TO_CHAR(CAPTUREDATE, 'YYYYMMDD'),'YYYYMMDD' ) <= TO_DATE(?, 'YYYYMMDD') AND TRANSCODE = '01'  GROUP BY CAPTUREDATE ");
            sysBean.AddSQLParam(emDataType.STR, MerchantID);
            sysBean.AddSQLParam(emDataType.STR, SubMID);
            sysBean.AddSQLParam(emDataType.STR, Today);
            sysBean.AddSQLParam(emDataType.STR, Today);
            sSQLSB.append(" UNION ");
            sSQLSB.append(" SELECT CAPTUREDATE , 0 AS CAPTURECOUNT, 0 AS CAPTUREAMT, 0 AS CAPTUREPROCESSCOUNT, 0 AS CAPTUREPROCESSAMT, COUNT(CAPTUREAMT) AS CAPTUREAPPROVECOUNT, SUM(CAPTUREAMT) * -1 AS CAPTUREAPPROVEAMT, 0 AS CAPTUREREJECTCOUNT, 0 AS CAPTUREREJECTAMT FROM CAPTURE "
            		+ "WHERE MERCHANTID = ? AND SUBMID = ? AND CAPTUREFLAG = '3' AND FEEDBACKCODE = '000' AND CAPTUREDATE >= TO_DATE(?, 'YYYYMMDD') - "+Mer_Board_Day+"  "
            		+ "AND TO_DATE(TO_CHAR(CAPTUREDATE, 'YYYYMMDD'),'YYYYMMDD' ) <= TO_DATE(?, 'YYYYMMDD') AND TRANSCODE = '01'  GROUP BY CAPTUREDATE ");
            sysBean.AddSQLParam(emDataType.STR, MerchantID);
            sysBean.AddSQLParam(emDataType.STR, SubMID);
            sysBean.AddSQLParam(emDataType.STR, Today);
            sysBean.AddSQLParam(emDataType.STR, Today);
            sSQLSB.append(" UNION ");
            sSQLSB.append(" SELECT CAPTUREDATE , 0 AS CAPTURECOUNT, 0 AS CAPTUREAMT, 0 AS CAPTUREPROCESSCOUNT, 0 AS CAPTUREPROCESSAMT, 0 AS CAPTUREAPPROVECOUNT, 0 AS CAPTUREAPPROVEAMT, COUNT(CAPTUREAMT) AS CAPTUREREJECTCOUNT, SUM(CAPTUREAMT) * -1 AS CAPTUREREJECTAMT FROM CAPTURE "
            		+ "WHERE MERCHANTID = ? AND SUBMID= ? AND CAPTUREFLAG = '3' AND FEEDBACKCODE <> '000' AND CAPTUREDATE >= TO_DATE(?, 'YYYYMMDD') - "+Mer_Board_Day+"  "
            		+ "AND TO_DATE(TO_CHAR(CAPTUREDATE, 'YYYYMMDD'),'YYYYMMDD' ) <= TO_DATE(?, 'YYYYMMDD') AND TRANSCODE = '01'  GROUP BY CAPTUREDATE ");
            sysBean.AddSQLParam(emDataType.STR, MerchantID);
            sysBean.AddSQLParam(emDataType.STR, SubMID);
            sysBean.AddSQLParam(emDataType.STR, Today);
            sysBean.AddSQLParam(emDataType.STR, Today);
            sSQLSB.append(" ) X  GROUP BY  X.CAPTUREDATE ");
            sSQLSB.append(" ) Y  GROUP BY  Y.CAPTUREDATE ORDER BY  Y.CAPTUREDATE ");

            // System.out.println("Sql=" + Sql);

            try
            {
            	/** 2023/05/16 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-076 */
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
//     * 取得公告訊息-將逾期資料(Billing)
//     * @param String MerchantID       特店代號
//     * @param String SubMID           特店服務代號
//     * @param String Today            查詢指定日期-今日(yyyy/MM/DD)
//     * @param String Mer_Capture_Day  請款期限
//     * @param String Mer_Board_Dueday 公告期限
//     * @return ArrayList              列表資料
//     */
//    public ArrayList get_MsgBoard_Billing_List(String MerchantID, String SubMID, String Today, String Mer_Capture_Day, String Mer_Board_Dueday)
//    {
//        ArrayList arrayData = new ArrayList(); // 顯示訂單交易資料
//        DataBaseBean SysBean = new DataBaseBean();
//
//        if (MerchantID.length() > 0 && SubMID.length() > 0 && Today.length() > 0 && Mer_Capture_Day.length() > 0 && Mer_Board_Dueday.length() > 0)
//        {
//        	//20130725 Jason 改用StringBuffer
//        	StringBuffer Sql = new StringBuffer();
//        	/*
//            String Sql = " SELECT T.TRANSDATE, T.CAPTUREDEALDATE, SUM(T.SALECAPTUREBEHINDCOUNT) AS SALECAPTUREBEHINDCOUNT, SUM(T.SALECAPTUREBEHINDAMT) AS SALECAPTUREBEHINDAMT, SUM(T.REFUNDCAPTUREBEHINDCOUNT) AS REFUNDCAPTUREBEHINDCOUNT , SUM (T.REFUNDCAPTUREBEHINDAMT) AS REFUNDCAPTUREBEHINDAMT FROM ( ";
//            Sql = Sql + " SELECT TO_CHAR(TO_DATE(TRANSDATE,'YYYYMMDD') ,'YYYY/MM/DD')  TRANSDATE, TO_CHAR(TO_DATE(TRANSDATE,'YYYYMMDD') + "+Mer_Capture_Day+",'YYYY/MM/DD') CAPTUREDEALDATE , COUNT(BALANCEAMT) AS SALECAPTUREBEHINDCOUNT, SUM(BALANCEAMT) AS SALECAPTUREBEHINDAMT , 0 AS REFUNDCAPTUREBEHINDCOUNT, 0 AS REFUNDCAPTUREBEHINDAMT FROM BILLING WHERE MERCHANTID = '"+MerchantID+"' AND SUBMID= '"+SubMID+"' AND BALANCEAMT > 0 AND TO_DATE(TRANSDATE, 'YYYYMMDD') + "+Mer_Capture_Day+" > TO_DATE('"+Today+"', 'YYYYMMDD') AND TO_DATE(TRANSDATE, 'YYYYMMDD') + "+Mer_Capture_Day+" <= TO_DATE('"+Today+"', 'YYYYMMDD') + "+Mer_Board_Dueday+" AND TRANSCODE = '00' GROUP BY TRANSDATE  ";
//            Sql = Sql + " UNION ";
//            Sql = Sql + " SELECT TO_CHAR(TO_DATE(TRANSDATE,'YYYYMMDD') ,'YYYY/MM/DD')  TRANSDATE, TO_CHAR(TO_DATE(TRANSDATE,'YYYYMMDD') + "+Mer_Capture_Day+",'YYYY/MM/DD') CAPTUREDEALDATE , 0 AS SALECAPTUREBEHINDCOUNT, 0 AS SALECAPTUREBEHINDAMT , COUNT(BALANCEAMT) AS REFUNDCAPTUREBEHINDCOUNT, SUM(BALANCEAMT) AS REFUNDCAPTUREBEHINDAMT FROM BILLING WHERE MERCHANTID = '"+MerchantID+"' AND SUBMID= '"+SubMID+"' AND BALANCEAMT > 0 AND TO_DATE(TRANSDATE, 'YYYYMMDD') + "+Mer_Capture_Day+" > TO_DATE('"+Today+"', 'YYYYMMDD') AND TO_DATE(TRANSDATE, 'YYYYMMDD') + "+Mer_Capture_Day+" <= TO_DATE('"+Today+"', 'YYYYMMDD') + "+Mer_Board_Dueday+" AND TRANSCODE = '01' GROUP BY TRANSDATE  ";
//            Sql = Sql + " ) T  GROUP BY T.TRANSDATE, T.CAPTUREDEALDATE";
//			*/
//            Sql.append(" SELECT T.TRANSDATE, T.CAPTUREDEALDATE, SUM(T.SALECAPTUREBEHINDCOUNT) AS SALECAPTUREBEHINDCOUNT, SUM(T.SALECAPTUREBEHINDAMT) AS SALECAPTUREBEHINDAMT, SUM(T.REFUNDCAPTUREBEHINDCOUNT) AS REFUNDCAPTUREBEHINDCOUNT , SUM (T.REFUNDCAPTUREBEHINDAMT) AS REFUNDCAPTUREBEHINDAMT FROM ( ");
//            Sql.append(" SELECT TO_CHAR(TO_DATE(TRANSDATE,'YYYYMMDD') ,'YYYY/MM/DD')  TRANSDATE, TO_CHAR(TO_DATE(TRANSDATE,'YYYYMMDD') + "+Mer_Capture_Day+",'YYYY/MM/DD') CAPTUREDEALDATE , COUNT(BALANCEAMT) AS SALECAPTUREBEHINDCOUNT, SUM(BALANCEAMT) AS SALECAPTUREBEHINDAMT , 0 AS REFUNDCAPTUREBEHINDCOUNT, 0 AS REFUNDCAPTUREBEHINDAMT FROM BILLING WHERE MERCHANTID = '"+MerchantID+"' AND SUBMID= '"+SubMID+"' AND BALANCEAMT > 0 AND TO_DATE(TRANSDATE, 'YYYYMMDD') + "+Mer_Capture_Day+" > TO_DATE('"+Today+"', 'YYYYMMDD') AND TO_DATE(TRANSDATE, 'YYYYMMDD') + "+Mer_Capture_Day+" <= TO_DATE('"+Today+"', 'YYYYMMDD') + "+Mer_Board_Dueday+" AND TRANSCODE = '00' GROUP BY TRANSDATE  ");
//            Sql.append(" UNION ");
//            Sql.append(" SELECT TO_CHAR(TO_DATE(TRANSDATE,'YYYYMMDD') ,'YYYY/MM/DD')  TRANSDATE, TO_CHAR(TO_DATE(TRANSDATE,'YYYYMMDD') + "+Mer_Capture_Day+",'YYYY/MM/DD') CAPTUREDEALDATE , 0 AS SALECAPTUREBEHINDCOUNT, 0 AS SALECAPTUREBEHINDAMT , COUNT(BALANCEAMT) AS REFUNDCAPTUREBEHINDCOUNT, SUM(BALANCEAMT) AS REFUNDCAPTUREBEHINDAMT FROM BILLING WHERE MERCHANTID = '"+MerchantID+"' AND SUBMID= '"+SubMID+"' AND BALANCEAMT > 0 AND TO_DATE(TRANSDATE, 'YYYYMMDD') + "+Mer_Capture_Day+" > TO_DATE('"+Today+"', 'YYYYMMDD') AND TO_DATE(TRANSDATE, 'YYYYMMDD') + "+Mer_Capture_Day+" <= TO_DATE('"+Today+"', 'YYYYMMDD') + "+Mer_Board_Dueday+" AND TRANSCODE = '01' GROUP BY TRANSDATE  ");
//            Sql.append(" ) T  GROUP BY T.TRANSDATE, T.CAPTUREDEALDATE");
//            
//            try
//            {
//                arrayData = (ArrayList) SysBean.executeSQL(Sql.toString(), "select");
//            }
//            catch (Exception ex)
//            {
//                System.out.println(ex.getMessage());
//                log_systeminfo.debug(ex.toString());
//            }
//            if (arrayData == null) {
//                arrayData = new ArrayList();
//            }
//        }
//
//        return arrayData;
//    }

    /* Override get_MsgBoard_Billing_List with DataBaseBean parameter */
    public ArrayList get_MsgBoard_Billing_List(DataBaseBean2 sysBean, String MerchantID, String SubMID, String Today, String Mer_Capture_Day, String Mer_Board_Dueday)
    {
        ArrayList arrayData = new ArrayList(); // 顯示訂單交易資料
        // DataBaseBean SysBean = new DataBaseBean();

        if (MerchantID.length() > 0 && SubMID.length() > 0 && Today.length() > 0 && Mer_Capture_Day.length() > 0 && Mer_Board_Dueday.length() > 0)
        {
        	//20130725 Jason 改用StringBuffer
        	StringBuffer sSQLSB = new StringBuffer();
        	sysBean.ClearSQLParam();
        	sSQLSB.append(" SELECT T.TRANSDATE, T.CAPTUREDEALDATE, SUM(T.SALECAPTUREBEHINDCOUNT) AS SALECAPTUREBEHINDCOUNT, SUM(T.SALECAPTUREBEHINDAMT) AS SALECAPTUREBEHINDAMT, SUM(T.REFUNDCAPTUREBEHINDCOUNT) AS REFUNDCAPTUREBEHINDCOUNT , SUM (T.REFUNDCAPTUREBEHINDAMT) AS REFUNDCAPTUREBEHINDAMT FROM ( ");
        	sSQLSB.append(" SELECT TO_CHAR(TO_DATE(TRANSDATE,'YYYYMMDD') ,'YYYY/MM/DD')  TRANSDATE, TO_CHAR(TO_DATE(TRANSDATE,'YYYYMMDD') + "+Mer_Capture_Day+",'YYYY/MM/DD') CAPTUREDEALDATE , COUNT(BALANCEAMT) AS SALECAPTUREBEHINDCOUNT, SUM(BALANCEAMT) AS SALECAPTUREBEHINDAMT , 0 AS REFUNDCAPTUREBEHINDCOUNT, 0 AS REFUNDCAPTUREBEHINDAMT FROM BILLING "
        			+ "WHERE MERCHANTID = ? AND SUBMID= ? AND BALANCEAMT > 0 AND TO_DATE(TRANSDATE, 'YYYYMMDD') + "+Mer_Capture_Day+" > TO_DATE(?, 'YYYYMMDD') "
        			+ "AND TO_DATE(TRANSDATE, 'YYYYMMDD') + "+Mer_Capture_Day+" <= TO_DATE(?, 'YYYYMMDD') + "+Mer_Board_Dueday+" AND TRANSCODE = '00' GROUP BY TRANSDATE  ");
        	sysBean.AddSQLParam(emDataType.STR, MerchantID);
        	sysBean.AddSQLParam(emDataType.STR, SubMID);
        	sysBean.AddSQLParam(emDataType.STR, Today);
        	sysBean.AddSQLParam(emDataType.STR, Today);
        	sSQLSB.append(" UNION ");
        	sSQLSB.append(" SELECT TO_CHAR(TO_DATE(TRANSDATE,'YYYYMMDD') ,'YYYY/MM/DD')  TRANSDATE, TO_CHAR(TO_DATE(TRANSDATE,'YYYYMMDD') + "+Mer_Capture_Day+",'YYYY/MM/DD') CAPTUREDEALDATE , 0 AS SALECAPTUREBEHINDCOUNT, 0 AS SALECAPTUREBEHINDAMT , COUNT(BALANCEAMT) AS REFUNDCAPTUREBEHINDCOUNT, SUM(BALANCEAMT) AS REFUNDCAPTUREBEHINDAMT FROM BILLING "
        			+ "WHERE MERCHANTID = ? AND SUBMID= ? AND BALANCEAMT > 0 AND TO_DATE(TRANSDATE, 'YYYYMMDD') + "+Mer_Capture_Day+" > TO_DATE(?, 'YYYYMMDD') "
        			+ "AND TO_DATE(TRANSDATE, 'YYYYMMDD') + "+Mer_Capture_Day+" <= TO_DATE(?, 'YYYYMMDD') + "+Mer_Board_Dueday+" AND TRANSCODE = '01' GROUP BY TRANSDATE  ");
        	sSQLSB.append(" ) T  GROUP BY T.TRANSDATE, T.CAPTUREDEALDATE");
        	sysBean.AddSQLParam(emDataType.STR, MerchantID);
        	sysBean.AddSQLParam(emDataType.STR, SubMID);
        	sysBean.AddSQLParam(emDataType.STR, Today);
        	sysBean.AddSQLParam(emDataType.STR, Today);
        	/*
            String Sql = " SELECT T.TRANSDATE, T.CAPTUREDEALDATE, SUM(T.SALECAPTUREBEHINDCOUNT) AS SALECAPTUREBEHINDCOUNT, SUM(T.SALECAPTUREBEHINDAMT) AS SALECAPTUREBEHINDAMT, SUM(T.REFUNDCAPTUREBEHINDCOUNT) AS REFUNDCAPTUREBEHINDCOUNT , SUM (T.REFUNDCAPTUREBEHINDAMT) AS REFUNDCAPTUREBEHINDAMT FROM ( ";
            Sql = Sql + " SELECT TO_CHAR(TO_DATE(TRANSDATE,'YYYYMMDD') ,'YYYY/MM/DD')  TRANSDATE, TO_CHAR(TO_DATE(TRANSDATE,'YYYYMMDD') + "+Mer_Capture_Day+",'YYYY/MM/DD') CAPTUREDEALDATE , COUNT(BALANCEAMT) AS SALECAPTUREBEHINDCOUNT, SUM(BALANCEAMT) AS SALECAPTUREBEHINDAMT , 0 AS REFUNDCAPTUREBEHINDCOUNT, 0 AS REFUNDCAPTUREBEHINDAMT FROM BILLING WHERE MERCHANTID = '"+MerchantID+"' AND SUBMID= '"+SubMID+"' AND BALANCEAMT > 0 AND TO_DATE(TRANSDATE, 'YYYYMMDD') + "+Mer_Capture_Day+" > TO_DATE('"+Today+"', 'YYYYMMDD') AND TO_DATE(TRANSDATE, 'YYYYMMDD') + "+Mer_Capture_Day+" <= TO_DATE('"+Today+"', 'YYYYMMDD') + "+Mer_Board_Dueday+" AND TRANSCODE = '00' GROUP BY TRANSDATE  ";
            Sql = Sql + " UNION ";
            Sql = Sql + " SELECT TO_CHAR(TO_DATE(TRANSDATE,'YYYYMMDD') ,'YYYY/MM/DD')  TRANSDATE, TO_CHAR(TO_DATE(TRANSDATE,'YYYYMMDD') + "+Mer_Capture_Day+",'YYYY/MM/DD') CAPTUREDEALDATE , 0 AS SALECAPTUREBEHINDCOUNT, 0 AS SALECAPTUREBEHINDAMT , COUNT(BALANCEAMT) AS REFUNDCAPTUREBEHINDCOUNT, SUM(BALANCEAMT) AS REFUNDCAPTUREBEHINDAMT FROM BILLING WHERE MERCHANTID = '"+MerchantID+"' AND SUBMID= '"+SubMID+"' AND BALANCEAMT > 0 AND TO_DATE(TRANSDATE, 'YYYYMMDD') + "+Mer_Capture_Day+" > TO_DATE('"+Today+"', 'YYYYMMDD') AND TO_DATE(TRANSDATE, 'YYYYMMDD') + "+Mer_Capture_Day+" <= TO_DATE('"+Today+"', 'YYYYMMDD') + "+Mer_Board_Dueday+" AND TRANSCODE = '01' GROUP BY TRANSDATE  ";
            Sql = Sql + " ) T  GROUP BY T.TRANSDATE, T.CAPTUREDEALDATE";
			*/
			
            try
            {
            	/** 2023/05/17 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-077 */
                arrayData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
                log_systeminfo.debug(ex.toString());
            }
            if (arrayData == null) {
                arrayData = new ArrayList();
            }
        }

        return arrayData;
    }
}
