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
 * 202112300619-01 20220210 GARY �дڧ妸�дڳW��W��(Visa Authorization Source Code) AUTH_SRC_CODE
 */
public class MerchantVoidBean
{
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    public MerchantVoidBean()
    {
    }

    /**
     * ���o�ʳf�ΰh�f�����C����(Billing+Balance)
     * @param String MerchantID     �S���N��
     * @param String SubMID         �S���N��
     * @param String OrderType      �d�߭q�����O (Order:�S�����w�渹 System:�t�Ϋ��w�渹 )
     * @param String OrderID        �q��N��
     * @param String TransCode      ����N�X
     * @return ArrayList            �C����
     */
    public ArrayList get_BillingVoid_List(String MerchantID, String SubMID, String OrderType, String OrderID, String TransCode)
    {
        ArrayList arrayBillingData = new ArrayList(); // ��ܭq�������
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
                // �HOrderID
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
        ArrayList arrayBillingData = new ArrayList(); // ��ܭq�������
        // DataBaseBean SysBean = new DataBaseBean();

        if (MerchantID.length() > 0 && SubMID.length() > 0 && OrderType.length() > 0 && OrderID.length() > 0)
        {
            // SQL���ѱ� by Jimmy Kang 20150511
        	//StringBuffer Sql = new StringBuffer("SELECT A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG , B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT FROM ");
        	// Merchant Console �u�W�����@�~�Ҳ�  �ק�  by Jimmy Kang 20150511  -- �ק�}�l --
        	// SQL�y�k���hselect A.CARDTYPE, �]���n���o�d�O���Ȥ~�వ�P�_�hban�� �d�O��C - CUP ���h�f��� ���������ʧ@
        	StringBuffer sSQLSB = new StringBuffer();
            sysBean.ClearSQLParam();
    		sSQLSB.append("SELECT A.CARD_TYPE, A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG , B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT FROM ");
        	// Merchant Console �u�W�����@�~�Ҳ�  �ק�  by Jimmy Kang 20150511  -- �קﵲ�� --
        	
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
                // �HOrderID
                sSQLSB.append(" AND ORDERID = ? ");
                sysBean.AddSQLParam(emDataType.STR, OrderID);
            }
            else
            {
                sSQLSB.append(" AND SYS_ORDERID = ? ");
                sysBean.AddSQLParam(emDataType.STR, OrderID);
            }

            // System.out.println("Sql=" + Sql);
            
            // SQL���ѱ� by Jimmy Kang 20150511
            //sSQLSB.append(") Y  WHERE X.MERCHANTID = Y.MERCHANTID AND X.SUBMID = Y.SUBMID AND X.ORDERID = Y.ORDERID ORDER BY X.TRANSDATE, X.TRANSTIME) A, BALANCE B WHERE A.MERCHANTID = B.MERCHANTID AND A.SUBMID = B.SUBMID AND A.ORDERID = B.ORDERID GROUP BY A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG , B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT");
            // Merchant Console �u�W�����@�~�Ҳ�  �ק�  by Jimmy Kang 20150511  -- �ק�}�l --
            sSQLSB.append(") Y  WHERE X.MERCHANTID = Y.MERCHANTID AND X.SUBMID = Y.SUBMID AND X.ORDERID = Y.ORDERID ORDER BY X.TRANSDATE, X.TRANSTIME) A, BALANCE B WHERE A.MERCHANTID = B.MERCHANTID AND A.SUBMID = B.SUBMID AND A.ORDERID = B.ORDERID GROUP BY A.CARD_TYPE, A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG , B.AUTHAMT , B.REFUNDAMT ,B.CANCELAMT, B.CAPTUREAMT");
            // Merchant Console �u�W�����@�~�Ҳ�  �ק�  by Jimmy Kang 20150511  -- �קﵲ�� --
            
            try
            {
            	/** 2023/05/10 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-036 */
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
     * ���o�妸�ʳf�ΰh�f�����C����(Billing+Balance)
     * @param String MerchantID     �S���N��
     * @param String SubMID         �S���N��
     * @param String OrderType      �d�߭q�����O (Order:�S�����w�渹 System:�t�Ϋ��w�渹 )
     * @param String OrderID        �q��N��
     * @param String TransCode      ����N�X (00-�ʳf,01-�h�f)
     * @param String TransDate      ���v���
     * @param String TransTime      ���v�ɶ�
     * @param String ApproveCode    ���v�X
     * @param String TransAmt       ������B
     * @return ArrayList            �C����
     */
    public ArrayList get_BillingVoid_List(String MerchantID, String SubMID, String OrderType, String OrderID,
                                          String TransCode, String TransDate, String TransTime, String ApproveCode,
                                          String TransAmt)
    {
        ArrayList arrayBillingData = new ArrayList(); // ��ܭq�������
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
                // �HOrderID
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
            	/** 2023/05/12 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-040 (No Need Test) */
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
        ArrayList arrayBillingData = new ArrayList(); // ��ܭq�������
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
                // �HOrderID
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
            	/** 2023/05/18 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-030 (No Need Test) */
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
//     * ���o�U�O���o�h�f�������(Billing+Balance)
//     * @param String MerchantID     �S���N��
//     * @param String SubMID         �S���N��
//     * @param String OrderType      �d�߭q�����O (Order:�S�����w�渹 System:�t�Ϋ��w�渹 )
//     * @param String OrderID        �q��N��
//     * @param String TransCode      ����N�X
//     * @return ArrayList            �C����
//     */
//    public ArrayList get_BillingVoid_List(String MerchantID, String SubMID, String OrderID, String TransCode)
//    {
//        ArrayList arrayBillingData = new ArrayList(); // ��ܭq�������
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
//        ArrayList arrayBillingData = new ArrayList(); // ��ܭq�������
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
//     * ���o�U�O���o�妸�h�f�������(Billing+Balance)
//     * @param String MerchantID     �S���N��
//     * @param String SubMID         �S���N��
//     * @param String OrderType      �d�߭q�����O (Order:�S�����w�渹 System:�t�Ϋ��w�渹 )
//     * @param String OrderID        �q��N��
//     * @param String TransCode      ����N�X (00-�ʳf,01-�h�f)
//     * @param String TransDate      ���v���
//     * @param String TransTime      ���v�ɶ�
//     * @param String ApproveCode    ���v�X
//     * @param String TransAmt       ������B
//     * @return ArrayList            �C����
//     */
//    public ArrayList get_BillingVoid_List(String MerchantID, String SubMID, String OrderID, String TransCode, String TransDate,
//                                          String TransTime, String ApproveCode, String TransAmt)
//    {
//        ArrayList arrayBillingData = new ArrayList(); // ��ܭq�������
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
        ArrayList arrayBillingData = new ArrayList(); // ��ܭq�������
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
            	/** 2023/05/18 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-030 (No Need Test) */
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
     * �P�_�O�_���\�ʳf����(�C��)
     * @param ArrayList arrayBillingData �����C����
     * @return Hashtable                 �P�_���G FLAG:�h�f�P�_(String)  MESSAGE:��ܵ��G(String)
     */
    public Hashtable check_SaleVoid_Status(ArrayList arrayBillingData)
    {
        Hashtable hashData = new Hashtable();
        boolean boolFlag = false; // �O�_�వ�h�f
        String strMessage = "�d�L������"; // ��ܵ��G

        if (arrayBillingData.size() > 0)
        {
            for (int c = 0; c < arrayBillingData.size(); ++c)
            {
                Hashtable hashTmpData = (Hashtable) arrayBillingData.get(c);
                String TransCode = hashTmpData.get("TRANSCODE").toString();
                String TransAmt = hashTmpData.get("TRANSAMT").toString();
                String BalanceAmt = hashTmpData.get("BALANCEAMT").toString();
                String AuthAmt = hashTmpData.get("AUTHAMT").toString(); // ���v���B
                String RefundAmt = hashTmpData.get("REFUNDAMT").toString(); // �h�f���B
                String CancelAmt = hashTmpData.get("CANCELAMT").toString(); // �ʳf�����f���B
                String CaptureAmt = hashTmpData.get("CAPTUREAMT").toString(); // �дڪ��B
                if (TransCode.length() > 0 && TransAmt.length() > 0 && BalanceAmt.length() > 0 && AuthAmt.length() > 0 &&
                    RefundAmt.length() > 0 && CancelAmt.length() > 0 && CaptureAmt.length() > 0)
                {
                    if (TransCode.equalsIgnoreCase("00"))
                    {
                        // �ʳf���
                        boolFlag = true;
                        if (Double.parseDouble(TransAmt) != Double.parseDouble(AuthAmt))
                        {
                            strMessage = "������B����";
                            boolFlag = false;
                        }

                        if (Double.parseDouble(TransAmt) != Double.parseDouble(BalanceAmt))
                        {
                            strMessage = "����w����/�д�";
                            boolFlag = false;
                        }

                        if (Double.parseDouble(CancelAmt) > 0)
                        {
                            strMessage = "����w����";
                            boolFlag = false;
                        }

                        if (Double.parseDouble(RefundAmt) > 0)
                        {
                            strMessage = "����w�h�f";
                            boolFlag = false;
                        }

                        if (Double.parseDouble(CaptureAmt) > 0)
                        {
                            strMessage = "����w�д�";
                            boolFlag = false;
                        }

                        break;
                    }
                }
                else
                {
                    strMessage = "�d�L������";
                }
            }
        }

        hashData.put("FLAG", String.valueOf(boolFlag));
        hashData.put("MESSAGE", strMessage);

        return hashData;
    }

    /**
     * �P�_�O�_���\�h�f����(�C��)
     * @param ArrayList arrayBillingData �����C����
     * @return Hashtable                 �P�_���G FLAG:�h�f�P�_(String)  MESSAGE:��ܵ��G(String)
     */
    public Hashtable check_RefundVoid_Status(ArrayList arrayBillingData)
    {
        Hashtable hashData = new Hashtable();
        boolean boolFlag = false; // �O�_�వ�h�f
        int RefundCnt = 0;
        String strMessage = "�d�L������"; // ��ܵ��G

        if (arrayBillingData.size() > 0)
        {
            for (int c = 0; c < arrayBillingData.size(); ++c)
            {
                Hashtable hashTmpData = (Hashtable) arrayBillingData.get(c);
                String TransCode = hashTmpData.get("TRANSCODE").toString();
                String TransAmt = hashTmpData.get("TRANSAMT").toString();
                String BalanceAmt = hashTmpData.get("BALANCEAMT").toString();
                String RefundAmt = hashTmpData.get("REFUNDAMT").toString(); // �h�f���B
                
                // Merchant Console �u�W�����@�~�Ҳ�  �ק�  by Jimmy Kang 20150511  -- �ק�}�l --
                // �s�W CardType�ܼƥΥH�P�_�d�O�O�_�� C - CUP
                String CardType = hashTmpData.get("CARD_TYPE").toString(); //�d�O
                // Merchant Console �u�W�����@�~�Ҳ�  �ק�  by Jimmy Kang 20150511  -- �קﵲ�� --

                if (TransCode.length() > 0 && TransAmt.length() > 0 &&
                    BalanceAmt.length() > 0 && RefundAmt.length() > 0)
                {
                    if (TransCode.equalsIgnoreCase("01"))
                    {
                        // �h�f���
                        if (Double.parseDouble(RefundAmt) < 0)
                        {
                            strMessage = "����v����";
                        }
                        else
                        {
                            System.out.println("-----TransAmt ="+TransAmt+" BalanceAmt="+BalanceAmt);
                            if (Double.parseDouble(TransAmt) != Double.parseDouble(BalanceAmt))
                            {
                                strMessage = "������B����";
                            }
                            else
                            {
                            	// ���ѱ� By Jimmy Kang 20150511
                            	//RefundCnt++;
                            	
                            	// Merchant Console �u�W�����@�~�Ҳ�  �ק�  by Jimmy Kang 20150511  -- �ק�}�l --
                            	// �[�J�P�_ CARD_TYPE�O�_��C - CUP ������
                            	if (!CardType.equalsIgnoreCase("C"))
                            	{
                            		RefundCnt++;
                            	}
                            	// Merchant Console �u�W�����@�~�Ҳ�  �ק�  by Jimmy Kang 20150511  -- �קﵲ�� --
                            }
                        }
                    }
                }
                else
                {
                    strMessage = "�d�L�������";
                }
            }

            if (RefundCnt==0)
            {
               boolFlag = false;
               strMessage = "�d�L�i�h�f�������";
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
//     * ���o�дڨ����C����(Capture+Balance)
//     * @param String MerchantID     �S���N��
//     * @param String SubMID         �S���N��
//     * @param String OrderType      �d�߭q�����O (Order:�S�����w�渹 System:�t�Ϋ��w�渹 )
//     * @param String OrderID        �q��N��
//     * @return ArrayList            �C����
//     */
//    public ArrayList get_CaptureVoid_List(String MerchantID, String SubMID, String OrderType, String OrderID)
//    {
//        ArrayList arrayCaptureData = new ArrayList(); // ��ܭq�������
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
//                // �HOrderID
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
        ArrayList arrayCaptureData = new ArrayList(); // ��ܭq�������
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
                // �HOrderID
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
            	/** 2023/05/09 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-033 */
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
//     * ���o�妸�дڨ����C����(Capture+Balance)
//     * @param String MerchantID     �S���N��
//     * @param String SubMID         �S���N��
//     * @param String TerminalID     �ݥ����N��
//     * @param String OrderID        �q��N��
//     * @param String TransDate      ���v���
//     * @param String TransTime      ���v�ɶ�
//     * @param String ApproveCode    ���v�X
//     * @param String TransCode      ������O(00-�ʳf,01-�h�f)
//     * @param String CaptureAmt     �дڪ��B
//     * @return ArrayList            �C����
//     */
//    public ArrayList get_CaptureVoid_List(String MerchantID, String SubMID, String TerminalID, String OrderID,
//                                          String TransDate, String TransTime, String ApproveCode, String TransCode,
//                                          String CaptureAmt ) 
//    {
//        ArrayList arrayCaptureData = new ArrayList(); // ��ܭq�������
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
        ArrayList arrayCaptureData = new ArrayList(); // ��ܭq�������
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
            	/** 2023/05/18 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-030 (No Need Test) */
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
     * �J�`���������q��дڪ��B
     * @param  ArrayList arrayList �дڸ��
     * @return Hashtable     �J�`���
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
                // �ʳf
                OrderCaptureAmt = String.valueOf(Double.parseDouble(OrderCaptureAmt) + Double.parseDouble(CaptureAmt));
            }
            
            if (TransCode.equalsIgnoreCase("01")) 
            { 
                // �h�f
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
         * �P�_�O�_���\�дڨ���(�C��)
         * @param ArrayList arrayCaptureData �����C����
         * @return Hashtable                 �P�_���G FLAG:�h�f�P�_(String)  MESSAGE:��ܵ��G(String)
         */
        public Hashtable check_CaptureVoid_Status(ArrayList arrayCaptureData) 
        {
            Hashtable hashData = new Hashtable();
            boolean boolFlag = false; // �O�_�వ�h�f
            String strMessage = "�d�L������"; // ��ܵ��G
            int count=0;
            System.out.println("arrayCaptureData.size()="+arrayCaptureData.size());
        
            if (arrayCaptureData.size() > 0) 
            {
                for (int c = 0; c < arrayCaptureData.size(); ++c) 
                {
                    Hashtable hashTmpData = (Hashtable) arrayCaptureData.get(c);
                    String CaptureFlag = hashTmpData.get("CAPTUREFLAG").toString(); // �дڪ��A
                    String CaptureAmt = hashTmpData.get("CAPTUREAMT").toString(); // �дڪ��B
                    if (CaptureFlag.length() > 0 && CaptureAmt.length() > 0) 
                    {
                        if (Double.parseDouble(CaptureAmt) < 0) 
                        {
                            strMessage = "������B����";
                            boolFlag = false;
                        }
                        
                        if (!CaptureFlag.equalsIgnoreCase("0")) 
                        {
                            boolFlag = false;
                            if (CaptureFlag.equalsIgnoreCase("1")) 
                            {
                                strMessage = "�дڳB�z��";
                            }
                            
                            if (CaptureFlag.equalsIgnoreCase("2")) 
                            {
                                strMessage = "�дڤv����";
                            }
                            
                            if (CaptureFlag.equalsIgnoreCase("3")) 
                            {
                                strMessage = "�дڤw�B�z";
                            }
                        } 
                        else 
                        {
                            count++;
                        }
                    } 
                    else 
                    {
                        strMessage = "�d�L������";
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
     * �P�_�O�_���\�дڨ���(�C��)
     * @param ArrayList arrayCaptureData �����C����
     * @param Hashtable hashBalanceData  �����C����B�έp���
     * @param String    OverRefundLimit  �W�B�h����B
     * @return Hashtable                 �P�_���G FLAG:�h�f�P�_(String)  MESSAGE:��ܵ��G(String)
     */
    public Hashtable check_CaptureVoid_Action_Status(ArrayList arrayCaptureData, Hashtable hashBalanceData, String OverRefundLimit) 
    {
        Hashtable hashData = new Hashtable();
        Hashtable hashSuccessData = new Hashtable();  // ������\
        Hashtable hashFailData = new Hashtable();     // �������

        String strMessage = "�d�L������"; // ��ܵ��G
        System.out.println("arrayCaptureData.size()="+arrayCaptureData.size());
        if (arrayCaptureData.size() > 0) 
        {
            for (int c = 0; c < arrayCaptureData.size(); ++c) 
            {
                boolean boolFlag = true; // �O�_�వ�h�f
                Hashtable hashTmpData = (Hashtable) arrayCaptureData.get(c);
                String CaptureFlag = hashTmpData.get("CAPTUREFLAG").toString(); // �дڪ��A
                String CaptureAmt = hashTmpData.get("CAPTUREAMT").toString(); // �дڪ��B
                if (CaptureFlag.length() > 0 && CaptureAmt.length() > 0) 
                {
                    if (Double.parseDouble(CaptureAmt) < 0) 
                    {
                        strMessage = "������B����";
                        boolFlag = false;
                    }
                    
                    if (!CaptureFlag.equalsIgnoreCase("0")) 
                    {
                        boolFlag = false;
                        if (CaptureFlag.equalsIgnoreCase("1")) 
                        {
                            strMessage = "�дڤw����";
                        }
                        
                        if (CaptureFlag.equalsIgnoreCase("2")) 
                        {
                            strMessage = "�дڤv����";
                        }
                    } 
                    else 
                    {
                        String OrderID = hashTmpData.get("ORDERID").toString(); // �S���q��s��
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
                                strMessage = "�дڪ��B�����j��0";
                            }
                        }
                        
                        if (boolFlag) 
                        {
                            if ((Double.parseDouble(BalCaptureAmt) - Double.parseDouble(OrderCaptureAmt) + Double.parseDouble(OverRefundLimit)) <
                                (Double.parseDouble(RefundCaptureAmt) - Double.parseDouble(OrderRefundAmt))) 
                            {
                                boolFlag = false;
                                strMessage = "�h�f�дڪ��B���i�j���ʳf�дڪ��B";
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
                    strMessage = "�d�L������";
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
//     * ���o�дڨ����C����(Capture)
//     * @param String ROWID     ROWID
//     * @return ArrayList       �C����
//     */
//     
//    public ArrayList get_CaptureVoid_List(String ROWID) 
//    {
//        ArrayList arrayCaptureData = new ArrayList(); // ��ܭq�������
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
        ArrayList arrayCaptureData = new ArrayList(); // ��ܭq�������
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
            	/** 2023/05/10 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-035 */
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
     * �P�_�O�_���\�ʳf�������
     * @param ArrayList arrayBillingData �����C����
     * @param String    strCancelAmt     �������B
     * @return Hashtable                 �P�_���G FLAG:�h�f�P�_(String)  MESSAGE:��ܵ��G(String)
     */
    public Hashtable check_SaleVoid_Action_Status(ArrayList arrayBillingData, String strCancelAmt) 
    {
        Hashtable hashData = new Hashtable();
        boolean boolFlag = false; // �O�_�వ�h�f
        String strMessage = "�d�L������"; // ��ܵ��G
        
        if (arrayBillingData.size() > 0) 
        {
            for (int c = 0; c < arrayBillingData.size(); ++c) 
            {
                Hashtable hashTmpData = (Hashtable) arrayBillingData.get(c);
                String TransCode = hashTmpData.get("TRANSCODE").toString();
                String TransAmt = hashTmpData.get("TRANSAMT").toString();
                String BalanceAmt = hashTmpData.get("BALANCEAMT").toString();
                String AuthAmt = hashTmpData.get("AUTHAMT").toString(); // ���v���B
                String RefundAmt = hashTmpData.get("REFUNDAMT").toString(); // �h�f���B
                String CancelAmt = hashTmpData.get("CANCELAMT").toString(); // �ʳf�����f���B
                String CaptureAmt = hashTmpData.get("CAPTUREAMT").toString(); // �дڪ��B
                
                if (TransCode.length() > 0 && CancelAmt.length() > 0 && AuthAmt.length() > 0 && RefundAmt.length() > 0 &&
                    CancelAmt.length() > 0 && CaptureAmt.length() > 0 ) 
                {
                    if (TransCode.equalsIgnoreCase("00")) 
                    { 
                        // �ʳf���
                        boolFlag = true;
                        if (strCancelAmt.length() > 0) 
                        {
                            if ((Double.parseDouble(AuthAmt) - Double.parseDouble(RefundAmt) -
                                 Double.parseDouble(CancelAmt) - Double.parseDouble(CaptureAmt)) !=
                                 Double.parseDouble(strCancelAmt)) 
                            {
                                strMessage = "������B����";
                                boolFlag = false;
                            }
                            
                            if (Double.parseDouble(strCancelAmt) != Double.parseDouble(TransAmt)) 
                            {
                                strMessage = "������B����";
                                boolFlag = false;
                            }
                        }
                        
                        if (Double.parseDouble(BalanceAmt) != Double.parseDouble(TransAmt)) 
                        {
                            strMessage = "������B����";
                            boolFlag = false;
                        }
                        break;
                    }
                } 
                else 
                {
                    strMessage = "�d�L������";
                }
            }
        }
        
        hashData.put("FLAG", String.valueOf(boolFlag));
        hashData.put("MESSAGE", strMessage);
        
        return hashData;
    }

    /**
     * �P�_�O�_���\�h�f�������
     * @param ArrayList arrayBillingData �����C����
     * @param String    Sys_OrderID      �����t�Ϋ��w�渹
     * @param String    strCancelAmt     �������B
     * @return Hashtable                 �P�_���G FLAG:�h�f�P�_(String)  MESSAGE:��ܵ��G(String)
     */
    public Hashtable check_RefundVoid_Action_Status(ArrayList arrayBillingData, String strCancelAmt) 
    {
        Hashtable hashData = new Hashtable();
        boolean boolFlag = false; // �O�_�వ�h�f
        String strMessage = "�d�L������"; // ��ܵ��G
        int TotalCount = 0;
    
        if (arrayBillingData.size() > 0) 
        {
            for (int c = 0; c < arrayBillingData.size(); ++c) 
            {
                Hashtable hashTmpData = (Hashtable) arrayBillingData.get(c);
                String TransCode = hashTmpData.get("TRANSCODE").toString();
                String TransAmt = hashTmpData.get("TRANSAMT").toString();
                String BalanceAmt = hashTmpData.get("BALANCEAMT").toString();
                String AuthAmt = hashTmpData.get("AUTHAMT").toString(); // ���v���B
                String RefundAmt = hashTmpData.get("REFUNDAMT").toString(); // �h�f���B
                String CancelAmt = hashTmpData.get("CANCELAMT").toString(); // �ʳf�����f���B
                String CaptureAmt = hashTmpData.get("CAPTUREAMT").toString(); // �дڪ��B
                
                if (TransCode.length() > 0 && CancelAmt.length() > 0 && AuthAmt.length() > 0 && RefundAmt.length() > 0 &&
                    CancelAmt.length() > 0 && CaptureAmt.length() > 0 ) 
                {
                    if (TransCode.equalsIgnoreCase("01") && Double.parseDouble(BalanceAmt) >0) 
                    { 
                        // �h�f���
                        if (Double.parseDouble(RefundAmt) <= 0) 
                        {
                            strMessage = "������B���šA�����h�f���";
                        } 
                        else 
                        {
                            if (strCancelAmt.length() > 0) 
                            {
                                if (Double.parseDouble(strCancelAmt) != Double.parseDouble(TransAmt)) 
                                {
                                    strMessage = "������B����";
                                }
                                
                                if (Double.parseDouble(BalanceAmt) != Double.parseDouble(TransAmt)) 
                                {
                                    strMessage = "������B����";
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
                    strMessage = "�d�L�h�f������";
                }
            }
            
            if (TotalCount==0)
            {
                strMessage = "�d�L�h�f������";
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
     * ��s�ʳf/�h�f�����i�дڪ��B (Billing)
     * @param  DataBaseBean SysBean 	��Ʈw
     * @param  String MerchantID 	�Q����������S���N��
     * @param  String SubMID	        �Q����������A�ȥN��
     * @param  String Sys_OrderID	�Q����������t�Ϋ��w�渹
     * @param  String TransCode	        �Q�������������N�X  00:�ʳf 01:�h�f
     * @return boolean                  ��s���G
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
        	/** 2023/05/11 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-037 */
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
     * ��s�дڨ������ (Capture)
     * @param  DataBaseBean SysBean 	��Ʈw
     * @param  String ROWID             �Q�����дڸ��ROWID
     * @param  String CaptureFlag       �дڱ���X��
     * @param  String FEEBackCode       �дڦ^�нX
     * @param  String FEEBackMsg        �дڦ^�аT��
     * @param  String FEEBackDate       �дڦ^�Ф��
     * @return boolean                  ��s���G
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
        	/** 2023/05/10 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-038 */
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
     * ��s�дڨ����b��i�дڪ��B (Billing)
     * @param  DataBaseBean SysBean 	��Ʈw
     * @param  String MerchantID 	�Q����������S���N��
     * @param  String SubMID	        �Q����������A�ȥN��
     * @param  String Sys_OrderID	�Q����������t�Ϋ��w�渹
     * @param  String CaptureAmt	�^�Ъ��B
     * @return boolean                  ��s���G
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
        	/** 2023/05/11 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-039 */
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
