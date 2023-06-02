package com.cybersoft.bean;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import com.cybersoft.merchant.bean.MerchantAuthParam;
import com.cybersoft4u.prj.tfbpg.bean.ParamBean;
import com.fubon.security.filter.SecurityTool;
/***
 * 202112300619-01 20220210 GARY �дڧ妸�дڳW��W��(Visa Authorization Source Code) AUTH_SRC_CODE
 * ***/
public class UserBean
{
    private String cardType;
    private DataBaseBean2 resultBean = new DataBaseBean2();

    public UserBean()
    {
    }

    /**
     * *�T�{�S���v���Ϊ��A
     * @param Hashtable hashMerchant  �S�����
     * @param String    Column        ���W��
     * @param String    Value         �P�_��
     * @return Boolean  Flag          �T�{���G
     */
    public boolean check_Merchant_Column(Hashtable hashMerchant, String Column, String Value)
    {
        boolean boolFlag = false;

        if (hashMerchant.size() > 0 && Column.length() > 0 && Value.length() > 0)
        {
            String arrayColumn[] = Column.split(",");
            String arrayValue[] = Value.split(",");

            for (int c = 0; c < arrayColumn.length; ++c)
            {
                String strValue = String.valueOf(hashMerchant.get(arrayColumn[c]));
                for (int j = 0; j < arrayValue.length; ++j)
                {
                    // System.out.println(arrayColumn[c] + "=" + arrayValue[j]);
                    if (strValue.equalsIgnoreCase(arrayValue[j]))
                    {
                        // �}���v��
                        boolFlag = true;
                        break;
                    }
                }
            }
        }

        return boolFlag;
    }

    /**
     * �T�{�ݥ����v���Ϊ��A
     * @param String    MerchantID    �S���N��
     * @param String    TerminalID    �ݥ����N��
     * @param ArrayList arrayTerminal �ݥ������
     * @param String    Column        ���W��
     * @param String    Value         �P�_��
     * @return Boolean  Flag          �T�{���G
     */
    public boolean check_Terminal_Column(String MerchantID, String TerminalID, ArrayList arrayTerminal, String Column, String Value)
    {
        boolean boolFlag = false;
        for (int i = 0; i < arrayTerminal.size(); ++i)
        {
            Hashtable hashMerchant = (Hashtable) arrayTerminal.get(i);
            String strMerchantID = hashMerchant.get("MERCHANTID").toString();
            String strTerminalID = hashMerchant.get("TERMINALID").toString();

            if (strMerchantID.equalsIgnoreCase(MerchantID) && strTerminalID.equalsIgnoreCase(TerminalID))
            {
                if (hashMerchant.size() > 0 && Column.length() > 0 && Value.length() > 0)
                {
                    String arrayColumn[] = Column.split(",");
                    String arrayValue[] = Value.split(",");

                    for (int c = 0; c < arrayColumn.length; ++c)
                    {
                        String strValue = String.valueOf(hashMerchant.get(arrayColumn[c]));
                        for (int j = 0; j < arrayValue.length; ++j)
                        {
//                            System.out.println(arrayColumn[c] + "=" + arrayValue[j]);
                            if (strValue.equalsIgnoreCase(arrayValue[j]))
                            {
                                // �}���v��
                                boolFlag = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return boolFlag;
    }

//    /**
//     * ���o�b�������(Billing)
//     * @param String MerchantID     �S���N��
//     * @param String SubMID         �S���N��
//     * @param String OrderType      �d�߭q�����O (Order:�S�����w�渹 System:�t�Ϋ��w�渹 )
//     * @param String OrderID        �q��N��
//     * @param String TransCode      ����N�X
//     * @return ArrayList            �b����
//     */
//    public ArrayList get_Billing(String MerchantID, String SubMID, String OrderType, String OrderID, String TransCode)
//    {
//        ArrayList arrayBillingData = new ArrayList(); // ��ܭq�������
//        DataBaseBean SysBean = new DataBaseBean();
//
//        if (MerchantID.length() > 0 && SubMID.length() > 0 &&  OrderType.length() > 0 && OrderID.length() > 0)
//        {
//            StringBuffer Sql = new StringBuffer( "SELECT *  FROM BILLING WHERE ");
//            Sql.append( " MERCHANTID = '" + MerchantID + "' ");
//            Sql.append( " AND SUBMID = '" + SubMID + "' ");
//
//            if (OrderType.equalsIgnoreCase("Order"))
//            {
//                // �HOrderID
//                Sql.append( " AND ORDERID = '" + OrderID + "' ");
//            }
//            else
//            {
//                Sql.append( " AND SYS_ORDERID = '" + OrderID + "' ");
//            }
//
//            if (TransCode.length() > 0)
//            {
//                Sql.append( " AND TRANSCODE = '" + TransCode + "' ");
//            }
//
//            Sql.append( " ORDER BY TRANSDATE, TRANSTIME ");
//            // System.out.println("Sql=" + Sql);
//
//            try
//            {
//                arrayBillingData = (ArrayList) SysBean.executeSQL(Sql.toString(), "select");
//            }
//            catch (Exception ex)
//            {
//                System.out.println(ex.getMessage());
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

    /* Override get_Billing with DataBaseBean parameter */
    public ArrayList get_Billing(DataBaseBean2 sysBean, String MerchantID, String SubMID, String OrderType, String OrderID, String TransCode)
    {
        ArrayList arrayBillingData = new ArrayList(); // ��ܭq�������
        // DataBaseBean SysBean = new DataBaseBean();

        if (MerchantID.length() > 0 && SubMID.length() > 0 &&  OrderType.length() > 0 && OrderID.length() > 0)
        {
        	StringBuffer sSQLSB = new StringBuffer();
            sysBean.ClearSQLParam();
    		sSQLSB.append(" SELECT *  FROM BILLING WHERE ");
    		sSQLSB.append(" MERCHANTID = ? ");
    		sSQLSB.append(" AND SUBMID = ? ");
    		
    		sysBean.AddSQLParam(emDataType.STR, MerchantID);
    		sysBean.AddSQLParam(emDataType.STR, SubMID);

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

            sSQLSB.append(" ORDER BY TRANSDATE, TRANSTIME ");
            // System.out.println("Sql=" + Sql);

            try
            {
            	/** 2023/05/04 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-027 */
                arrayBillingData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
            }

            if (arrayBillingData == null)
            {
                arrayBillingData = new ArrayList();
            }
        }

        return arrayBillingData;
    }

//    /**
//     * ���o�b������ƾ��{(Billing)
//     * @param String MerchantID     �S���N��
//     * @param String SubMID         �S���N��
//     * @param String OrderType      �d�߭q�����O (Order:�S�����w�渹 System:�t�Ϋ��w�渹 )
//     * @param String OrderID        �q��N��
//     * @param String TransCode      ����N�X
//     * @return ArrayList            �b����
//     */
//    public ArrayList get_BillingHistory(String MerchantID, String SubMID, String OrderType, String OrderID, String TransCode)
//    {
//        ArrayList arrayBillingData = new ArrayList(); // ��ܭq�������
//        DataBaseBean SysBean = new DataBaseBean();
//
//        if (MerchantID.length() > 0 && SubMID.length() > 0 && OrderType.length() > 0 && OrderID.length() > 0)
//        {
//            StringBuffer Sql = new StringBuffer("SELECT A.*, X.ORDERID FROM BILLING A, (SELECT MERCHANTID, SUBMID, ORDERID FROM BILLING WHERE ");
//            Sql.append( " MERCHANTID = '" + MerchantID + "' ");
//            Sql.append( " AND SUBMID = '" + SubMID + "' ");
//
//            if (OrderType.equalsIgnoreCase("Order"))
//            {
//                // �HOrderID
//                Sql.append( " AND ORDERID = '" + OrderID + "' ");
//            }
//            else
//            {
//                Sql.append( " AND SYS_ORDERID = '" + OrderID + "' ");
//            }
//
//            Sql.append( " GROUP BY MERCHANTID, SUBMID, ORDERID ) X WHERE A.ORDERID = X.ORDERID AND  A.MERCHANTID = X.MERCHANTID AND A.SUBMID = X.SUBMID ");
//            if (TransCode.length() > 0)
//            {
//                Sql.append( " AND A.TRANSCODE in (" + TransCode + ") ");
//            }
//
//            Sql.append( " ORDER BY A.TRANSDATE, A.TRANSTIME ");
//            // System.out.println("Sql=" + Sql);
//
//            try
//            {
//                arrayBillingData = (ArrayList) SysBean.executeSQL(Sql.toString(), "select");
//            }
//            catch (Exception ex)
//            {
//                System.out.println(ex.getMessage());
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

    /* Override get_BillingHistory with DataBaseBean parameter */
    public ArrayList get_BillingHistory(DataBaseBean2 sysBean, String MerchantID, String SubMID, String OrderType, String OrderID, String TransCode)
    {
        ArrayList arrayBillingData = new ArrayList(); // ��ܭq�������
        // DataBaseBean SysBean = new DataBaseBean();

        if (MerchantID.length() > 0 && SubMID.length() > 0 && OrderType.length() > 0 && OrderID.length() > 0)
        {
        	StringBuffer sSQLSB = new StringBuffer();
            sysBean.ClearSQLParam();
    		sSQLSB.append("SELECT A.*, X.ORDERID FROM BILLING A, (SELECT MERCHANTID, SUBMID, ORDERID FROM BILLING WHERE ");
            sSQLSB.append(" MERCHANTID = ? ");
            sSQLSB.append(" AND SUBMID = ? ");
            
            sysBean.AddSQLParam(emDataType.STR, MerchantID);
            sysBean.AddSQLParam(emDataType.STR, SubMID);

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

            sSQLSB.append(" GROUP BY MERCHANTID, SUBMID, ORDERID ) X WHERE A.ORDERID = X.ORDERID AND  A.MERCHANTID = X.MERCHANTID AND A.SUBMID = X.SUBMID ");
            if (TransCode.length() > 0)
            {
                sSQLSB.append(" AND A.TRANSCODE in ( ? ) ");
                sysBean.AddSQLParam(emDataType.STR, TransCode);
            }

            sSQLSB.append(" ORDER BY A.TRANSDATE, A.TRANSTIME ");
            // System.out.println("Sql=" + Sql);

            try
            {
            	/** 2023/05/10 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-034 */
                arrayBillingData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
            }

            if (arrayBillingData == null)
            {
                arrayBillingData = new ArrayList();
            }
        }

        return arrayBillingData;
    }


//    /**
//     * ���o�b���ˮָ��(Balance)
//     * @param String MerchantID     �S���N��
//     * @param String SubMID         �A�ȥN��
//     * @param String OrderID        �q��N��
//     * @return Hashtable            �b���ˮָ��
//     */
//    public Hashtable get_Balance(String MerchantID, String SubMID, String OrderID)
//    {
//        ArrayList arrayBillingData = new ArrayList(); // ��ܭq�������
//        Hashtable hashBillingData = new Hashtable();
//        DataBaseBean SysBean = new DataBaseBean();
//
//        if (MerchantID.length() > 0 && SubMID.length() > 0 && OrderID.length() > 0)
//        {
//            StringBuffer Sql =new StringBuffer(  "SELECT MERCHANTID,SUBMID,ORDERID,TERMINALID,ACQUIRERID,AUTHTRANSDATE,AUTHPAN,AUTHAPPROVECODE,CURRENCYCODE,AUTHAMT,REFUNDAMT,TO_CHAR(REFUNDDATE,'YYYY/MM/DD HH24:MI:SS') REFUNDDATE,REFUNDAPPCODE,ESTCAPTUREDATE, TO_CHAR(CAPTUREDATE,'YYYY/MM/DD HH24:MI:SS') CAPTUREDATE,CAPTUREAMT, TO_CHAR(CANCELDATE,'YYYY/MM/DD HH24:MI:SS') CANCELDATE,CANCELAMT,TRANSFLAG,TRANSMODE,TRANSTYPE,BALANCEAMT, REFUNDCAPTUREAMT, TO_CHAR(REFUNDCAPTUREDATE,'YYYY/MM/DD HH24:MI:SS') REFUNDCAPTUREDATE, (AUTHAMT - REFUNDAMT - CANCELAMT) BALANCEAMT1 FROM BALANCE ");
//            Sql.append( " WHERE MERCHANTID = '" + MerchantID + "' ");
//            Sql.append( " AND SUBMID = '" + SubMID + "' ");
//            Sql.append( " AND ORDERID = '" + OrderID + "' ");
//            // System.out.println("Sql=" + Sql);
//            try
//            {
//                arrayBillingData = (ArrayList) SysBean.executeSQL(Sql.toString(), "select");
//            }
//            catch (Exception ex)
//            {
//                System.out.println(ex.getMessage());
//            }
//
//            if (arrayBillingData == null)
//            {
//                arrayBillingData = new ArrayList();
//            }
//
//            if (arrayBillingData.size() > 0)
//            {
//                hashBillingData = (Hashtable) arrayBillingData.get(0);
//            }
//        }
//
//        return hashBillingData;
//    }

    /* Override get_Balance with DatabaseBean parameter */
    public Hashtable get_Balance(DataBaseBean2 sysBean, String MerchantID, String SubMID, String OrderID)
    {
        ArrayList arrayBillingData = new ArrayList(); // ��ܭq�������
        Hashtable hashBillingData = new Hashtable();
        // DataBaseBean SysBean = new DataBaseBean();

        if (MerchantID.length() > 0 && SubMID.length() > 0 && OrderID.length() > 0)
        {
        	StringBuffer sSQLSB = new StringBuffer();
            sysBean.ClearSQLParam();
            sSQLSB.append(" SELECT MERCHANTID,SUBMID,ORDERID,TERMINALID,ACQUIRERID,AUTHTRANSDATE,AUTHPAN,AUTHAPPROVECODE,CURRENCYCODE,AUTHAMT,REFUNDAMT,TO_CHAR(REFUNDDATE,'YYYY/MM/DD HH24:MI:SS') REFUNDDATE,REFUNDAPPCODE,ESTCAPTUREDATE, TO_CHAR(CAPTUREDATE,'YYYY/MM/DD HH24:MI:SS') CAPTUREDATE,CAPTUREAMT, TO_CHAR(CANCELDATE,'YYYY/MM/DD HH24:MI:SS') CANCELDATE,CANCELAMT,TRANSFLAG,TRANSMODE,TRANSTYPE,BALANCEAMT, REFUNDCAPTUREAMT, TO_CHAR(REFUNDCAPTUREDATE,'YYYY/MM/DD HH24:MI:SS') REFUNDCAPTUREDATE, (AUTHAMT - REFUNDAMT - CANCELAMT) BALANCEAMT1 FROM BALANCE ");
            sSQLSB.append(" WHERE MERCHANTID = ? ");
            sSQLSB.append(" AND SUBMID = ? ");
            sSQLSB.append(" AND ORDERID = ? ");
            
            sysBean.AddSQLParam(emDataType.STR, MerchantID);
            sysBean.AddSQLParam(emDataType.STR, SubMID);
            sysBean.AddSQLParam(emDataType.STR, OrderID);
            // System.out.println("Sql=" + Sql);
            try
            {
            	/** 2023/04/27 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-015 */
                arrayBillingData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
            }

            if (arrayBillingData == null)
            {
                arrayBillingData = new ArrayList();
            }

            if (arrayBillingData.size() > 0)
            {
                hashBillingData = (Hashtable) arrayBillingData.get(0);
            }
        }

        return hashBillingData;
    }

//    /**
//     * ���oCUP�M���Ȧs��(CUP115)
//     * Added by Dale Peng 20150513
//     * Merchant Console              �u�W�h�f�@�~�Ҳ�
//     * @param DataBaseBean SysBean  ��Ʈw
//     * @param String MerchantID     �S���N��
//     * @param String SubMID         �A�ȥN��
//     * @param String OrderID        �q��N��
//     * @return Hashtable            CUP�M���Ȧs�ɸ��
//     */
//    public Hashtable get_CUP115(DataBaseBean SysBean, String MerchantID, String SubMID, String OrderID) 
//    {
//    	ArrayList arrayCUP115Data = new ArrayList(); // ���CUP115���
//    	Hashtable hashCUP115Data = new Hashtable();
//    	
//    	if (MerchantID.length() > 0 && SubMID.length() > 0 && OrderID.length() > 0)
//    	{
//    		StringBuffer Sql = new StringBuffer( " SELECT * FROM CUP115 ");
//    		Sql.append( " WHERE MERCHANTID = '" + MerchantID + "' ");
//            Sql.append( " AND SUBMID = '" + SubMID + "' ");
//            Sql.append( " AND ORDERID = '" + OrderID + "' ");
//            
//            try
//            {
//            	arrayCUP115Data = (ArrayList)SysBean.executeSQL(Sql.toString(), "select");
//            }
//            catch (Exception ex)
//            {
//            	 System.out.println(ex.getMessage());
//            }
//            if (arrayCUP115Data == null)
//            {
//            	arrayCUP115Data = new ArrayList();
//            }
//            
//            if (arrayCUP115Data.size()>0)
//            {
//            	hashCUP115Data = (Hashtable)arrayCUP115Data.get(0);
//            }
//    	}
//		return hashCUP115Data;
//    	
//    }
    /**
     * �̫��w����榡���ͤ��Ѥ��
     * @param String Type      ���w�^�Ǥ���榡
     * @return String NowTime  �^�Ǥ��
     */
    public String get_TransDate(String Type)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        calendar.add(Calendar.DATE, 0);
        SimpleDateFormat sdf = new SimpleDateFormat(Type);
        String NowTime = sdf.format(calendar.getTime());

        return NowTime;
    }

    /**
     * ���ͫ��w�ѼƤ��
     * @param String Type      ���w�^�Ǥ���榡
     * @param int AppointDay   ���w�Ѽ�
     * @return String NowTime  ���w�ѼƲ��ͤ��
     */
    public String get_AppointDay_Date(String Type, int AppointDay)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        calendar.add(Calendar.DATE, AppointDay);
        SimpleDateFormat sdf = new SimpleDateFormat(Type);
        String NowTime = sdf.format(calendar.getTime());

        return NowTime;
    }

    /**
     * ��ƫe��h�ť�
     * @return String Data  �^�Ǹ��
     */
    public String trim_Data(String Data)
    {
        if (Data==null) Data = "";
        Data = Data.trim();
        int point = 0;

        for (int i=0; i<Data.length(); ++i)
        {
            if (!Data.substring(i,i+1).equalsIgnoreCase(" "))
                break;
            else
                point++;
        }

        Data = Data.substring(point, Data.length());

        return Data;
    }

    /**
     * �s�W���v������(AuthLog)
     * @param  DataBaseBean SysBean     ��Ʈw
     * @param  String MerchantID    �S���N��
     * @param  String SubMID            �A�ȥN��
     * @param  String TerminalID    �ݥ����N��
     * @param  String AcquirerID    �����N��
     * @param  String OrderID           �S���q��s��
     * @param  String Sys_OrderID   �t�Ϋ��w�渹
     * @param  String Card_Type     �d�O
     * @param  String PAN           �d��
     * @param  String ExtenNo           CVV2/CVC2
     * @param  String ExpireDate    ���Ĵ���
     * @param  String TransCode         ����N�X
     * @param  String ReversalFlag  Auto-Reversal�X��
     * @param  String TransDate         ���v���
     * @param  String TransTime     ���v�ɶ�
     * @param  String CurrencyCode  ���v���O
     * @param  String TransAmt          ���v���B
     * @param  String Trans_Status  ������A
     * @param  String ApproveCode   ���v�X
     * @param  String ResponseCode  �^���X
     * @param  String ResponseMsg   �^���T��
     * @param  String Entry_Mode    Entry Mode
     * @param  String Condition_Code    Condition Code
     * @param  String BatchNo           ���v�妸���X
     * @param  String UserDefine    �ө��ۭq�T��
     * @param  String Direction         �O��
     * @param  String EMail         ���d�H eMail
     * @param  String MTI               ���v MTI
     * @param  String RRN           ���v RRN
     * @param  String SocialID          �����Ҧr��
     * @param  String TransMode         ����Ҧ�
     * @param  String TransType     �������
     * @param  String ECI           ECI
     * @param  String CAVV          3D Secure PARes.cavv
     * @param  String XID           3D Secure PARes.xid
     * @param  String InstallType   ��������O�p���X��
     * @param  String Install           ��������
     * @param  String FirstAmt          �������B
     * @param  String EachAmt           �C�����B
     * @param  String FEE           ����O
     * @param  String RedemType         ���Q���X��
     * @param  String RedemUsed     ���Q����I��
     * @param  String RedemBalance  ���Q�l�B
     * @param  String CreditAmt     �d�H�۲��B
     * @param  String BillMessage   �b��T��
     * @param  String SysTraceNo    gateway�^�Ъ�traceno
     * @return boolean                  �s�W���G
     */
    public boolean insert_AuthLog(DataBaseBean2 sysBean, String MerchantID, String SubMID,
                                  String TerminalID, String AcquirerID, String OrderID, String Sys_OrderID,
                                  String Card_Type, String PAN, String ExtenNo, String ExpireDate, String TransCode,
                                  String ReversalFlag, String TransDate, String TransTime, String CurrencyCode,
                                  String TransAmt, String Trans_Status, String ApproveCode, String ResponseCode,
                                  String ResponseMsg, String Entry_Mode, String Condition_Code, String BatchNo,
                                  String UserDefine, String Direction, String EMail, String MTI, String RRN,
                                  String SocialID, String TransMode, String TransType, String ECI, String CAVV,
                                  String XID, String InstallType, String Install, String FirstAmt,
                                  String EachAmt, String FEE, String RedemType, String RedemUsed, String RedemBalance,
                                  String CreditAmt, String BillMessage, String SysTraceNo,String AUTH_SRC_CODE)
    {
    	//20220210 ADD AUTH_SRC_CODE
        boolean Flag = false;
//        String Sql = " INSERT INTO AUTHLOG (SEQ,MERCHANTID,SUBMID,TERMINALID,ACQUIRERID,ORDERID,SYS_ORDERID,CARD_TYPE,PAN,EXTENNO,EXPIREDATE,TRANSCODE,REVERSALFLAG,TRANSDATE,TRANSTIME,CURRENCYCODE,TRANSAMT,TRANS_STATUS,APPROVECODE,RESPONSECODE,RESPONSEMSG,ENTRY_MODE,CONDITION_CODE,BATCHNO,USERDEFINE,DIRECTION,EMAIL,MTI,RRN,SOCIALID,TRANSMODE,TRANSTYPE,ECI,CAVV,XID,INSTALLTYPE,INSTALL,FIRSTAMT,EACHAMT,FEE,REDEMTYPE,REDEMUSED,REDEMBALANCE,CREDITAMT,BILLMESSAGE,SYSTRACENO ) VALUES (FN_GET_AUTHLOG_SEQ,";
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append(" INSERT INTO AUTHLOG (MERCHANTID,SUBMID,TERMINALID,ACQUIRERID,ORDERID,SYS_ORDERID,CARD_TYPE,PAN,EXTENNO,EXPIREDATE,TRANSCODE,REVERSALFLAG,TRANSDATE,TRANSTIME,CURRENCYCODE,TRANSAMT,TRANS_STATUS,APPROVECODE,RESPONSECODE,RESPONSEMSG,ENTRY_MODE,CONDITION_CODE,BATCHNO,USERDEFINE,DIRECTION,EMAIL,MTI,RRN,SOCIALID,TRANSMODE,TRANSTYPE,ECI,CAVV,XID,INSTALLTYPE,INSTALL,FIRSTAMT,EACHAMT,FEE,REDEMTYPE,REDEMUSED,REDEMBALANCE,CREDITAMT,BILLMESSAGE,SYSTRACENO,AUTH_SRC_CODE ) VALUES (");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, "); //20220210 ADD AUTH_SRC_CODE
        
        sysBean.AddSQLParam(emDataType.STR, MerchantID);
        sysBean.AddSQLParam(emDataType.STR, SubMID);
        sysBean.AddSQLParam(emDataType.STR, TerminalID);
        sysBean.AddSQLParam(emDataType.STR, AcquirerID);
        sysBean.AddSQLParam(emDataType.STR, OrderID);
        sysBean.AddSQLParam(emDataType.STR, Sys_OrderID);
        sysBean.AddSQLParam(emDataType.STR, Card_Type);
        sysBean.AddSQLParam(emDataType.STR, PAN);
        sysBean.AddSQLParam(emDataType.STR, ExtenNo);
        sysBean.AddSQLParam(emDataType.STR, ExpireDate);
        
        sysBean.AddSQLParam(emDataType.STR, TransCode);
        sysBean.AddSQLParam(emDataType.STR, ReversalFlag);
        sysBean.AddSQLParam(emDataType.STR, TransDate);
        sysBean.AddSQLParam(emDataType.STR, TransTime);
        sysBean.AddSQLParam(emDataType.STR, CurrencyCode);
        sysBean.AddSQLParam(emDataType.INT, TransAmt);
        sysBean.AddSQLParam(emDataType.STR, Trans_Status);
        sysBean.AddSQLParam(emDataType.STR, ApproveCode);
        sysBean.AddSQLParam(emDataType.STR, ResponseCode);
        sysBean.AddSQLParam(emDataType.STR, ResponseMsg.trim());
        
        sysBean.AddSQLParam(emDataType.STR, Entry_Mode);
        sysBean.AddSQLParam(emDataType.STR, Condition_Code);
        sysBean.AddSQLParam(emDataType.STR, BatchNo);
        sysBean.AddSQLParam(emDataType.STR, UserDefine);
        sysBean.AddSQLParam(emDataType.STR, Direction);
        sysBean.AddSQLParam(emDataType.STR, EMail);
        sysBean.AddSQLParam(emDataType.STR, MTI);
        sysBean.AddSQLParam(emDataType.STR, RRN);
        sysBean.AddSQLParam(emDataType.STR, SocialID);
        sysBean.AddSQLParam(emDataType.INT, TransMode);
        
        sysBean.AddSQLParam(emDataType.STR, TransType);
        sysBean.AddSQLParam(emDataType.STR, ECI);
        sysBean.AddSQLParam(emDataType.STR, CAVV);
        sysBean.AddSQLParam(emDataType.STR, XID);
        sysBean.AddSQLParam(emDataType.STR, InstallType);
        sysBean.AddSQLParam(emDataType.INT, Install);
        sysBean.AddSQLParam(emDataType.INT, FirstAmt);
        sysBean.AddSQLParam(emDataType.INT, EachAmt);
        sysBean.AddSQLParam(emDataType.INT, FEE);
        sysBean.AddSQLParam(emDataType.STR, RedemType);
        
        sysBean.AddSQLParam(emDataType.INT, RedemUsed);
        sysBean.AddSQLParam(emDataType.INT, RedemBalance);
        sysBean.AddSQLParam(emDataType.INT, CreditAmt);
        sysBean.AddSQLParam(emDataType.STR, BillMessage);
        sysBean.AddSQLParam(emDataType.STR, SysTraceNo);
        sysBean.AddSQLParam(emDataType.STR, AUTH_SRC_CODE);
        
        // System.out.println("Sql=" + Sql);
        ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/05/03 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-028 */
        	arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        
        System.out.println("arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
            Flag = true;
        }

        return Flag;
    }

    /**
     * �s�W���v������FOR CUP (AUTHLOG_CUP)
     * Added by Jimmy Kang 20150511
     * Merchant Console �u�W�����@�~�Ҳ�
     * @param  DataBaseBean SysBean     ��Ʈw
     * @param  String MerchantID        �S���N��
     * @param  String SubMID            �A�ȥN��
     * @param  String Sys_OrderID       �t�Ϋ��w�渹
     * @param  String TransCode         ����N�X
     * @param  String TransDate         ���v���(respTime)
     * @param  String TransTime         ���v�ɶ�(respTime)
     * @param  String Trace_Time        �t�θ��ܮɶ�(traceTime)
     * @param  String Settle_Amount     �M����B(settleAmount)
     * @param  String Settle_Currency   ���v���O(settleCurrency)
     * @param  String Settle_Date       �M����(settleDate)
     * @param  String Exchange_Rate     �M��ײv(exchangeRate)
     * @param  String Exchange_Date     �M��ײv�����(exchangeDate)
     * @param  String Cup_Paymode       ��I�覡(cupReserved��payMode)
     * @param  String Cup_TransCode     ����N�X(transType)
     * @param  String Trans_Status      ������A
     * @param  String ResponseCode      �^���X(respCode)
     * @param  String ResponseMsg       �^���T��(respMsg)
     * @param  String SysTraceNo        �t�θ��ܸ�(traceNumber)
     * @param  String Cup_QID           ����y����(qid)
     * @param  String Cup_Reserved      �t�ΫO�d��(cupReserved)
     * @param  String Direction         �O�� (Record) ���O
     * @param  String Cps_Refresh
     * @param  String Filestatus_Refkey �ɮ׳B�z���A��
     * @param  String Notifyurl         �S�����wurl
     * @param  String Notifyparam       �S�����wurl�᭱���S�w�Ѽ�
     * @param  String Notifytype        �S������
     * @param  String Retry_Cnt         Retry����
     * @param  String PAN               �d��
     * @param  String TransAmt          ���v���B
     * @param  String Cup_Notifypage    CUP���v������x�q������
     * @param  String Cup_Notifyparam   CUP���v������x�q�������Ѽ�
     * @return boolean                  �s�W���G
     */
    public boolean insert_AuthLog_Cup(DataBaseBean2 sysBean, String MerchantID, String SubMID,
    		String Sys_OrderID, String TransCode, String TransDate, String TransTime, String Trace_Time, 
    		String Settle_Amount, String Settle_Currency, String Settle_Date, String Exchange_Rate,
    		String Exchange_Date, String Cup_Paymode, String Cup_TransCode, String Trans_Status,
    		String ResponseCode, String ResponseMsg, String SysTraceNo, String Cup_QID,
    		String Cup_Reserved, String Direction, String Cps_Refresh, String Filestatus_Refkey,
    		String Notifyurl, String Notifyparam, String Notifytype, String Retry_Cnt, String PAN,
    		String TransAmt, String Cup_Notifypage, String Cup_Notifyparam)
    
    {
    	boolean Flag = false;
    	StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append(" INSERT INTO AUTHLOG_CUP ( MERCHANTID, SUBMID, SYS_ORDERID, TRANSCODE, TRANSDATE, TRANSTIME, TRACE_TIME, SETTLE_AMOUNT, SETTLE_CURRENCY, SETTLE_DATE, EXCHANGE_RATE, EXCHANGE_DATE, CUP_PAYMODE, CUP_TRANSCODE, TRANS_STATUS, RESPONSECODE, RESPONSEMSG, SYSTRACENO, CUP_QID, CUP_RESERVED, DIRECTION, CPS_REFRESH, FILESTATUS_REFKEY, NOTIFYURL, NOTIFYPARAM, NOTIFYTYPE, RETRY_CNT, PAN, TRANSAMT, CUP_NOTIFYPAGE, CUP_NOTIFYPARAM ) VALUES (");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?, ");
    	sSQLSB.append(" ?) ");

    	sysBean.AddSQLParam(emDataType.STR, MerchantID);
    	sysBean.AddSQLParam(emDataType.STR, SubMID);
    	sysBean.AddSQLParam(emDataType.STR, Sys_OrderID);
    	sysBean.AddSQLParam(emDataType.STR, TransCode);
    	sysBean.AddSQLParam(emDataType.STR, TransDate);
    	sysBean.AddSQLParam(emDataType.STR, TransTime);
    	sysBean.AddSQLParam(emDataType.STR, Trace_Time);
    	sysBean.AddSQLParam(emDataType.INT, Settle_Amount);
    	sysBean.AddSQLParam(emDataType.STR, Settle_Currency);
    	sysBean.AddSQLParam(emDataType.STR, Settle_Date);
    	
    	sysBean.AddSQLParam(emDataType.STR, Exchange_Rate);
    	sysBean.AddSQLParam(emDataType.STR, Exchange_Date);
    	sysBean.AddSQLParam(emDataType.STR, Cup_Paymode);
    	sysBean.AddSQLParam(emDataType.STR, Cup_TransCode);
    	sysBean.AddSQLParam(emDataType.STR, Trans_Status);
    	sysBean.AddSQLParam(emDataType.STR, ResponseCode);
    	sysBean.AddSQLParam(emDataType.STR, ResponseMsg.trim());
    	sysBean.AddSQLParam(emDataType.STR, SysTraceNo);
    	sysBean.AddSQLParam(emDataType.INT, Cup_QID);
    	sysBean.AddSQLParam(emDataType.STR, Cup_Reserved);
    	
    	sysBean.AddSQLParam(emDataType.STR, Direction);
    	sysBean.AddSQLParam(emDataType.STR, Cps_Refresh);
    	sysBean.AddSQLParam(emDataType.STR, Filestatus_Refkey);
    	sysBean.AddSQLParam(emDataType.STR, Notifyurl);
    	sysBean.AddSQLParam(emDataType.STR, Notifyparam);
    	sysBean.AddSQLParam(emDataType.STR, Notifytype);
    	sysBean.AddSQLParam(emDataType.INT, Retry_Cnt);
    	sysBean.AddSQLParam(emDataType.STR, PAN);
    	sysBean.AddSQLParam(emDataType.INT, TransAmt);
    	sysBean.AddSQLParam(emDataType.STR, Cup_Notifypage);
    	
    	sysBean.AddSQLParam(emDataType.STR, Cup_Notifyparam);
    	
    	// System.out.println("Sql=" + Sql);
    	ArrayList arraySys = new ArrayList();
    	try
    	{
    		/** 2023/05/04 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-029 */
    		arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
    	}
    	catch (Exception ex)
    	{
    		System.out.println(ex.getMessage());
    	}

        System.out.println("arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
            Flag = true;
        }
        
    	return Flag;
    }
    
    /**
     * �s�W�b�������(Billing)
     * @param  DataBaseBean SysBean     ��Ʈw
     * @param  String MerchantID    �S���N��
     * @param  String SubMID            �A�ȥN��
     * @param  String TerminalID    �ݥ����N��
     * @param  String AcquirerID    �����N��
     * @param  String OrderID           �S���q��s��
     * @param  String Sys_OrderID   �t�Ϋ��w�渹
     * @param  String Card_Type     �d�O
     * @param  String PAN           �d��
     * @param  String ExtenNo       CVV2/CVC2
     * @param  String ExpireDate    ���Ĵ���
     * @param  String TransCode         ����N�X
     * @param  String ReversalFlag  Auto-Reversal �X��
     * @param  String TransDate         ���v���
     * @param  String TransTime         ���v�ɶ�
     * @param  String CurrencyCode  ���v���O
     * @param  String TransAmt          �дڪ��B
     * @param  String ApproveCode   ���v�X
     * @param  String ResponseCode  �^���X
     * @param  String ResponseMsg   �^���T��
     * @param  String BatchNo           ���v�妸���X
     * @param  String UserDefine    �ө��ۭq�T��
     * @param  String EMail         ���d�H eMail
     * @param  String MTI           ���v MTI
     * @param  String RRN           ���v RRN
     * @param  String SocialID          �����Ҧr��
     * @param  String Entry_Mode    Entry Mode
     * @param  String Condition_Code    Condition Code
     * @param  String TransMode     ����Ҧ�
     * @param  String TransType     �������
     * @param  String ECI           ECI
     * @param  String CAVV          3D Secure PARes.cavv
     * @param  String XID           3D Secure PARes.xid
     * @param  String InstallType   ��������O�p���X��
     * @param  String Install           ��������
     * @param  String FirstAmt          �������B
     * @param  String EachAmt       �C�����B
     * @param  String FEE           ����O
     * @param  String RedemType         ���Q���X��
     * @param  String RedemUsed         ���Q����I��
     * @param  String RedemBalance  ���Q�l�B
     * @param  String CreditAmt         �d�H�ۥI�B
     * @param  String BillMessage   �b��T��
     * @param  String BalanceAmt    �i�дھl�B
     * @param  String SysTraceNo    gateway�^�Ъ�traceno
     * @return boolean                  �s�W���G
     */
    public boolean insert_Billing(DataBaseBean2 sysBean, String MerchantID, String SubMID,
                                  String TerminalID, String AcquirerID, String OrderID, String Sys_OrderID,
                                  String Card_Type, String PAN, String ExtenNo, String ExpireDate, String TransCode,
                                  String ReversalFlag, String TransDate, String TransTime, String CurrencyCode,
                                  String TransAmt, String ApproveCode, String ResponseCode, String ResponseMsg,
                                  String BatchNo, String UserDefine, String EMail, String MTI, String RRN,
                                  String SocialID, String Entry_Mode, String Condition_Code, String TransMode,
                                  String TransType, String ECI, String CAVV, String XID, String InstallType,
                                  String Install, String FirstAmt, String EachAmt, String FEE, String RedemType,
                                  String RedemUsed, String RedemBalance, String CreditAmt, String BillMessage,
                                  String BalanceAmt, String SysTraceNo,String AUTH_SRC_CODE)
    {
    	//20220210 ADD AUTH_SRC_CODE
        boolean Flag = false;
//        String Sql = " INSERT INTO BILLING (SEQ, MERCHANTID,SUBMID,TERMINALID,ACQUIRERID,ORDERID,SYS_ORDERID,CARD_TYPE,PAN,EXTENNO,EXPIREDATE,TRANSCODE,REVERSALFLAG,TRANSDATE,TRANSTIME,CURRENCYCODE,TRANSAMT,APPROVECODE,RESPONSECODE,RESPONSEMSG,BATCHNO,USERDEFINE,EMAIL,MTI,RRN,SOCIALID,ENTRY_MODE,CONDITION_CODE,TRANSMODE,TRANSTYPE,ECI,CAVV,XID,INSTALLTYPE,INSTALL,FIRSTAMT,EACHAMT,FEE,REDEMTYPE,REDEMUSED,REDEMBALANCE,CREDITAMT,BILLMESSAGE,BALANCEAMT,SYSTRACENO ) VALUES (FN_GET_BILLING_SEQ, ";
//        String Sql = " INSERT INTO BILLING ( MERCHANTID,SUBMID,TERMINALID,ACQUIRERID,ORDERID,SYS_ORDERID,CARD_TYPE,PAN,EXTENNO,EXPIREDATE,TRANSCODE,REVERSALFLAG,TRANSDATE,TRANSTIME,CURRENCYCODE,TRANSAMT,APPROVECODE,RESPONSECODE,RESPONSEMSG,BATCHNO,USERDEFINE,EMAIL,MTI,RRN,SOCIALID,ENTRY_MODE,CONDITION_CODE,TRANSMODE,TRANSTYPE,ECI,CAVV,XID,INSTALLTYPE,INSTALL,FIRSTAMT,EACHAMT,FEE,REDEMTYPE,REDEMUSED,REDEMBALANCE,CREDITAMT,BILLMESSAGE,BALANCEAMT,SYSTRACENO ) VALUES ( ";
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append(" INSERT INTO BILLING (MERCHANTID,SUBMID,TERMINALID,ACQUIRERID,ORDERID,SYS_ORDERID,CARD_TYPE,PAN,EXTENNO,EXPIREDATE,TRANSCODE,REVERSALFLAG,TRANSDATE,TRANSTIME,CURRENCYCODE,TRANSAMT,APPROVECODE,RESPONSECODE,RESPONSEMSG,BATCHNO,USERDEFINE,EMAIL,MTI,RRN,SOCIALID,ENTRY_MODE,CONDITION_CODE,TRANSMODE,TRANSTYPE,ECI,CAVV,XID,INSTALLTYPE,INSTALL,FIRSTAMT,EACHAMT,FEE,REDEMTYPE,REDEMUSED,REDEMBALANCE,CREDITAMT,BILLMESSAGE,BALANCEAMT,SYSTRACENO,AUTH_SRC_CODE ) VALUES ( ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?) "); //20220210 ADD AUTH_SRC_CODE
        
        sysBean.AddSQLParam(emDataType.STR, MerchantID);
        sysBean.AddSQLParam(emDataType.STR, SubMID);
        sysBean.AddSQLParam(emDataType.STR, TerminalID);
        sysBean.AddSQLParam(emDataType.STR, AcquirerID);
        sysBean.AddSQLParam(emDataType.STR, OrderID);
        sysBean.AddSQLParam(emDataType.STR, Sys_OrderID);
        sysBean.AddSQLParam(emDataType.STR, PAN);
        sysBean.AddSQLParam(emDataType.STR, ExtenNo);
        sysBean.AddSQLParam(emDataType.STR, ExpireDate);
        sysBean.AddSQLParam(emDataType.STR, TransCode);
        
        sysBean.AddSQLParam(emDataType.STR, ReversalFlag);
        sysBean.AddSQLParam(emDataType.STR, TransDate);
        sysBean.AddSQLParam(emDataType.STR, CurrencyCode);
        sysBean.AddSQLParam(emDataType.INT, TransAmt);
        sysBean.AddSQLParam(emDataType.STR, ApproveCode);        
        sysBean.AddSQLParam(emDataType.STR, ResponseCode);
        sysBean.AddSQLParam(emDataType.STR, ResponseMsg.trim());
        sysBean.AddSQLParam(emDataType.STR, BatchNo);
        sysBean.AddSQLParam(emDataType.STR, UserDefine);
        sysBean.AddSQLParam(emDataType.STR, EMail);
        
        sysBean.AddSQLParam(emDataType.STR, MTI);
        sysBean.AddSQLParam(emDataType.STR, RRN);
        sysBean.AddSQLParam(emDataType.STR, SocialID);
        sysBean.AddSQLParam(emDataType.STR, Entry_Mode);
        sysBean.AddSQLParam(emDataType.STR, Condition_Code);
        sysBean.AddSQLParam(emDataType.INT, TransMode);
        sysBean.AddSQLParam(emDataType.STR, TransType);
        sysBean.AddSQLParam(emDataType.STR, ECI);
        sysBean.AddSQLParam(emDataType.STR, CAVV);
        sysBean.AddSQLParam(emDataType.STR, XID);
        
        sysBean.AddSQLParam(emDataType.STR, InstallType);
        sysBean.AddSQLParam(emDataType.INT, Install);
        sysBean.AddSQLParam(emDataType.INT, FirstAmt);
        sysBean.AddSQLParam(emDataType.INT, EachAmt);
        sysBean.AddSQLParam(emDataType.INT, FEE);
        sysBean.AddSQLParam(emDataType.STR, RedemType);
        sysBean.AddSQLParam(emDataType.INT, RedemUsed);
        sysBean.AddSQLParam(emDataType.INT, RedemBalance);
        sysBean.AddSQLParam(emDataType.INT, CreditAmt);
        sysBean.AddSQLParam(emDataType.STR, BillMessage);
        
        sysBean.AddSQLParam(emDataType.INT, BalanceAmt);
        sysBean.AddSQLParam(emDataType.STR, SysTraceNo);
        sysBean.AddSQLParam(emDataType.STR, AUTH_SRC_CODE);

        // System.out.println("Sql=" + Sql);
        ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/05/08 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-030 */
        	arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        System.out.println("arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
            Flag = true;
        }
        
        return Flag;
    }

    /**
     * ��s�b���ˮֳ���(Balance)
     * @param  DataBaseBean SysBean     ��Ʈw
     * @param  String MerchantID    �S���N��
     * @param  String SubMID            �A�ȥN��
     * @param  String OrderID           �S���q��s��
     * @param  String RefundAmt         �h�f�`�B
     * @param  String RefundDate    �h�f���
     * @param  String CaptureAmt    �д��`�B
     * @param  String CaptureDate   �дڤ��
     * @param  String CancelAmt         �����`�B
     * @param  String CancelDate    �������
     * @param  String RefundCaptureAmt  �h�f�д��`�B
     * @param  String RefundCaptureDate �h�f�дڤ��
     * @param  String BalanceAmt    �i�дھl�B
     * @return boolean                  ��s���G
     */
    public boolean update_Balance(DataBaseBean2 sysBean, String MerchantID, String SubMID,
                                  String OrderID, String RefundAmt, String RefundDate, String CaptureAmt,
                                  String CaptureDate, String CancelAmt, String CancelDate, String RefundCaptureAmt,
                                  String RefundCaptureDate, String BalanceAmt)
    {
        boolean Flag = false;
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
        sSQLSB.append(" UPDATE BALANCE SET ");
        StringBuffer tmpSql = new StringBuffer();

        if (RefundAmt.length() > 0 && RefundDate.length() > 0)
        {
        	tmpSql.append(" REFUNDAMT = ?, REFUNDDATE = TO_DATE(?, 'YYYY/MM/DD HH24:MI:SS') ");
        	sysBean.AddSQLParam(emDataType.INT, RefundAmt);
        	sysBean.AddSQLParam(emDataType.STR, RefundDate);
        }

        if (CaptureAmt.length() > 0 && CaptureDate.length() > 0)
        {
            if (tmpSql.toString().length() > 0) {
            	tmpSql.append(", ");
            }
            
            tmpSql.append(" CAPTUREAMT = ?, CAPTUREDATE = TO_DATE(?, 'YYYY/MM/DD HH24:MI:SS') ");
            sysBean.AddSQLParam(emDataType.INT, CaptureAmt);
        	sysBean.AddSQLParam(emDataType.STR, CaptureDate);
        }

        if (CancelAmt.length() > 0 && CancelDate.length() > 0)
        {
        	if (tmpSql.toString().length() > 0) {
            	tmpSql.append(", ");
            }

            tmpSql.append(" CANCELAMT = ?, CANCELDATE = TO_DATE(?, 'YYYY/MM/DD HH24:MI:SS') ");
            sysBean.AddSQLParam(emDataType.INT, CancelAmt);
        	sysBean.AddSQLParam(emDataType.STR, CancelDate);
        }

        if (RefundCaptureAmt.length() > 0 && RefundCaptureDate.length() > 0)
        {
        	if (tmpSql.toString().length() > 0) {
            	tmpSql.append(", ");
            }

            tmpSql.append(" REFUNDCAPTUREAMT = ?, REFUNDCAPTUREDATE = TO_DATE(?, 'YYYY/MM/DD HH24:MI:SS') ");
            sysBean.AddSQLParam(emDataType.INT, RefundCaptureAmt);
        	sysBean.AddSQLParam(emDataType.STR, RefundCaptureDate);
        }

        if (BalanceAmt.length() > 0)
        {
        	if (tmpSql.toString().length() > 0) {
            	tmpSql.append(", ");
            }

            tmpSql.append(" BALANCEAMT = ? ");
            sysBean.AddSQLParam(emDataType.INT, BalanceAmt);
        }

        sSQLSB.append( tmpSql.toString());
        sSQLSB.append(" WHERE MERCHANTID = ? AND SUBMID = ? AND ORDERID = ? ");
        sysBean.AddSQLParam(emDataType.STR, MerchantID);
        sysBean.AddSQLParam(emDataType.STR, SubMID);
        sysBean.AddSQLParam(emDataType.STR, OrderID);
        
        // System.out.println("Sql=" + Sql);
        
        ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/04/28 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-016 */
        	arraySys = sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        
        System.out.println("[update_Balance]_arraySys.size()="+arraySys.size());
        if(arraySys.size() > 0) 
        {
            Flag = true;
        }

        return Flag;
    }

    /**
     * �s�W���v������(SAF)
     * @param  DataBaseBean SysBean     ��Ʈw
     * @param  String MerchantID    �S���N��
     * @param  String SubMID            �A�ȥN��
     * @param  String TerminalID    �ݥ����N��
     * @param  String AcquirerID    �����N��
     * @param  String OrderID           �S���q��s��
     * @param  String Sys_OrderID   �t�Ϋ��w�渹
     * @param  String Card_Type     �d�O
     * @param  String PAN           �d��
     * @param  String ExtenNo           CVV2/CVC2
     * @param  String ExpireDate    ���Ĵ���
     * @param  String TransCode         ����N�X
     * @param  String ReversalFlag  Auto-Reversal�X��
     * @param  String TransDate         ���v���
     * @param  String TransTime     ���v�ɶ�
     * @param  String CurrencyCode  ���v���O
     * @param  String TransAmt          ���v���B
     * @param  String ApproveCode   ���v�X
     * @param  String ResponseCode  �^���X
     * @param  String ResponseMsg   �^���T��
     * @param  String Entry_Mode    Entry Mode
     * @param  String Condition_Code    Condition Code
     * @param  String BatchNo           ���v�妸���X
     * @param  String UserDefine    �ө��ۭq�T��
     * @param  String EMail         ���d�H eMail
     * @param  String MTI               ���v MTI
     * @param  String RRN           ���v RRN
     * @param  String SocialID          �����Ҧr��
     * @param  String TransMode         ����Ҧ�
     * @param  String TransType     �������
     * @param  String ECI           ECI
     * @param  String CAVV          3D Secure PARes.cavv
     * @param  String XID           3D Secure PARes.xid
     * @param  String InstallType   ��������O�p���X��
     * @param  String Install           ��������
     * @param  String FirstAmt          �������B
     * @param  String EachAmt           �C�����B
     * @param  String FEE           ����O
     * @param  String RedemType         ���Q���X��
     * @param  String RedemUsed     ���Q����I��
     * @param  String RedemBalance  ���Q�l�B
     * @param  String CreditAmt     �d�H�۲��B
     * @param  String Status        �B�z���A
     * @param  String Resp_Date SAF     ���v�^�Ф��
     * @param  String Resp_Time SAF     ���v�^�Юɶ�
     * @param  String Resp_ResponseCode SAF ���v�^���X
     * @param  String Resp_ApproveCode  SAF ���v���v�X
     * @param  String Resp_ResponseMsg  SAF ���v�^���T��
     * @param  String SysTraceNo    gateway�^�Ъ�traceno
     * @return boolean                  �s�W���G
     */
    public boolean insert_SAF(DataBaseBean2 sysBean, String MerchantID, String SubMID,
                              String TerminalID, String AcquirerID, String OrderID, String Sys_OrderID,
                              String Card_Type, String PAN, String ExtenNo, String ExpireDate, String TransCode,
                              String ReversalFlag, String TransDate, String TransTime, String CurrencyCode,
                              String TransAmt, String ApproveCode, String ResponseCode,
                              String ResponseMsg, String Entry_Mode, String Condition_Code, String BatchNo,
                              String UserDefine, String EMail, String MTI, String RRN,
                              String SocialID, String TransMode, String TransType, String ECI, String CAVV,
                              String XID, String InstallType, String Install, String FirstAmt,
                              String EachAmt, String FEE, String RedemType, String RedemUsed, String RedemBalance,
                              String CreditAmt, String Status, String Resp_Date, String Resp_Time,
                              String Resp_ResponseCode, String Resp_ApproveCode, String Resp_ResponseMsg, String SysTraceNo)
    {
        boolean Flag = false;
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append(" INSERT INTO SAF ( MERCHANTID,SUBMID,TERMINALID,ACQUIRERID,ORDERID,SYS_ORDERID,CARD_TYPE,PAN,EXTENNO,EXPIREDATE,TRANSCODE,REVERSALFLAG,TRANSDATE,TRANSTIME,CURRENCYCODE,TRANSAMT,APPROVECODE,RESPONSECODE,RESPONSEMSG,ENTRY_MODE,CONDITION_CODE,BATCHNO,USERDEFINE,EMAIL,MTI,RRN,SOCIALID,TRANSMODE,TRANSTYPE,ECI,CAVV,XID,INSTALLTYPE,INSTALL,FIRSTAMT,EACHAMT,FEE,REDEMTYPE,REDEMUSED,REDEMBALANCE,CREDITAMT,STATUS,RESP_DATE,RESP_TIME,RESP_RESPONSECODE,RESP_APPROVECODE,RESP_RESPONSEMSG,SYSTRACENO ) VALUES (");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?) ");
        
        sysBean.AddSQLParam(emDataType.STR, MerchantID);
        sysBean.AddSQLParam(emDataType.STR, SubMID);
        sysBean.AddSQLParam(emDataType.STR, TerminalID);
        sysBean.AddSQLParam(emDataType.STR, AcquirerID);
        sysBean.AddSQLParam(emDataType.STR, OrderID);
        sysBean.AddSQLParam(emDataType.STR, Sys_OrderID);
        sysBean.AddSQLParam(emDataType.STR, Card_Type);
        sysBean.AddSQLParam(emDataType.STR, PAN);
        sysBean.AddSQLParam(emDataType.STR, ExtenNo);
        sysBean.AddSQLParam(emDataType.STR, ExpireDate);
        
        sysBean.AddSQLParam(emDataType.STR, TransCode);
        sysBean.AddSQLParam(emDataType.STR, ReversalFlag);
        sysBean.AddSQLParam(emDataType.STR, TransDate);
        sysBean.AddSQLParam(emDataType.STR, TransTime);
        sysBean.AddSQLParam(emDataType.STR, CurrencyCode);
        sysBean.AddSQLParam(emDataType.INT, TransAmt);
        sysBean.AddSQLParam(emDataType.STR, ApproveCode);
        sysBean.AddSQLParam(emDataType.STR, ResponseCode);
        sysBean.AddSQLParam(emDataType.STR, ResponseMsg.trim());
        sysBean.AddSQLParam(emDataType.STR, Entry_Mode);
        
        sysBean.AddSQLParam(emDataType.STR, Condition_Code);
        sysBean.AddSQLParam(emDataType.STR, BatchNo);
        sysBean.AddSQLParam(emDataType.STR, UserDefine);
        sysBean.AddSQLParam(emDataType.STR, EMail);
        sysBean.AddSQLParam(emDataType.STR, MTI);
        sysBean.AddSQLParam(emDataType.STR, RRN);
        sysBean.AddSQLParam(emDataType.STR, SocialID);
        sysBean.AddSQLParam(emDataType.INT, TransMode);
        sysBean.AddSQLParam(emDataType.STR, TransType);
        sysBean.AddSQLParam(emDataType.STR, ECI);
        
        sysBean.AddSQLParam(emDataType.STR, CAVV);
        sysBean.AddSQLParam(emDataType.STR, XID);
        sysBean.AddSQLParam(emDataType.STR, InstallType);
        sysBean.AddSQLParam(emDataType.INT, Install);
        sysBean.AddSQLParam(emDataType.INT, FirstAmt);
        sysBean.AddSQLParam(emDataType.INT, EachAmt);
        sysBean.AddSQLParam(emDataType.INT, FEE);
        sysBean.AddSQLParam(emDataType.STR, RedemType);
        sysBean.AddSQLParam(emDataType.INT, RedemUsed);
        sysBean.AddSQLParam(emDataType.INT, RedemBalance);
        
        sysBean.AddSQLParam(emDataType.INT, CreditAmt);
        sysBean.AddSQLParam(emDataType.STR, Status);
        sysBean.AddSQLParam(emDataType.STR, Resp_Date);
        sysBean.AddSQLParam(emDataType.STR, Resp_Time);
        sysBean.AddSQLParam(emDataType.STR, Resp_ResponseCode);
        sysBean.AddSQLParam(emDataType.STR, Resp_ApproveCode);
        sysBean.AddSQLParam(emDataType.STR, Resp_ResponseMsg.trim());
        sysBean.AddSQLParam(emDataType.STR, SysTraceNo);
        
        // System.out.println("Sql=" + Sql);

        ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/05/09 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-032 */
        	arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        System.out.println("[insert_SAF]_arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
            Flag = true;
        }

        return Flag;
    }

    /**
     * �s�W���v������FOR CUP (SAF_CUP)
     * Added by Jimmy Kang 20150511
     * Merchant Console �u�W�����@�~�Ҳ�
     * @param  DataBaseBean SysBean     ��Ʈw
     * @param  String MerchantID        �S���N��
     * @param  String SubMID            �A�ȥN��
     * @param  String TerminalID        �ݥ����N��
     * @param  String AcquirerID        �����N��
     * @param  String OrderID           �S���q��s��
     * @param  String Sys_OrderID       �t�Ϋ��w�渹
     * @param  String Card_Type         �d�O
     * @param  String PAN               �d��
     * @param  String ExtenNo           CVV2/CVC2
     * @param  String ExpireDate        ���Ĵ���
     * @param  String TransCode         ����N�X
     * @param  String ReversalFlag      Auto-Reversal�X��
     * @param  String TransDate         ���v���
     * @param  String TransTime         ���v�ɶ�
     * @param  String CurrencyCode      ���v���O
     * @param  String TransAmt          ���v���B
     * @param  String ApproveCode       ���v�X
     * @param  String ResponseCode      �^���X
     * @param  String ResponseMsg       �^���T��
     * @param  String Entry_Mode        Entry Mode
     * @param  String Condition_Code    Condition Code
     * @param  String BatchNo           ���v�妸���X
     * @param  String UserDefine        �ө��ۭq�T��
     * @param  String EMail             ���d�H eMail
     * @param  String MTI               ���v MTI
     * @param  String RRN               ���v RRN
     * @param  String SocialID          �����Ҧr��
     * @param  String Status            �B�z���A
     * @param  String Resp_Date         SAF ���v�^�Ф��
     * @param  String Resp_Time         SAF ���v�^�Юɶ�
     * @param  String Resp_ResponseCode SAF ���v�^���X
     * @param  String Resp_ApproveCode  SAF ���v���v�X
     * @param  String Resp_ResponseMsg  SAF ���v�^���T��
     * @param  String SysTraceNo        System Trace No
     * @param  String Retry_Cnt         Retry����
     * @param  String ECI               ECI
     * @param  String TransType         �������
     * @param  String TransMode         ����Ҧ�
     * @return boolean                  �s�W���G
     */
    public boolean insert_SAF_Cup (DataBaseBean2 sysBean, String MerchantID, String SubMID,
                              String TerminalID, String AcquirerID, String OrderID, String Sys_OrderID,
                              String Card_Type, String PAN, String ExtenNo, String ExpireDate, 
                              String TransCode, String ReversalFlag, String TransDate, String TransTime, 
                              String CurrencyCode, String TransAmt, String ApproveCode, String ResponseCode,
                              String ResponseMsg, String Entry_Mode, String Condition_Code, String BatchNo,
                              String UserDefine, String EMail, String MTI, String RRN, String SocialID, 
                              String Status, String Resp_Date, String Resp_Time, String Resp_ResponseCode, 
                              String Resp_ApproveCode, String Resp_ResponseMsg, String SysTraceNo, 
                              String Retry_Cnt, String ECI, String TransType, String TransMode)
    {
        boolean Flag = false;
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
        sSQLSB.append(" INSERT INTO SAF_CUP (  MERCHANTID, SUBMID, TERMINALID, ACQUIRERID, ORDERID, SYS_ORDERID, CARD_TYPE, PAN, EXTENNO, EXPIREDATE, TRANSCODE, REVERSALFLAG, TRANSDATE, TRANSTIME, CURRENCYCODE, TRANSAMT, APPROVECODE, RESPONSECODE, RESPONSEMSG, ENTRY_MODE, CONDITION_CODE, BATCHNO, USERDEFINE, EMAIL, MTI, RRN, SOCIALID, STATUS, RESP_DATE, RESP_TIME, RESP_RESPONSECODE, RESP_APPROVECODE, RESP_RESPONSEMSG, SYSTRACENO, RETRY_CNT, ECI, TRANSTYPE, TRANSMODE ) VALUES (");
		sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?  ) ");
        
        sysBean.AddSQLParam(emDataType.STR, MerchantID);
        sysBean.AddSQLParam(emDataType.STR, SubMID);
        sysBean.AddSQLParam(emDataType.STR, TerminalID);
        sysBean.AddSQLParam(emDataType.STR, AcquirerID);
        sysBean.AddSQLParam(emDataType.STR, OrderID);
        sysBean.AddSQLParam(emDataType.STR, Sys_OrderID);
        sysBean.AddSQLParam(emDataType.STR, Card_Type);
        sysBean.AddSQLParam(emDataType.STR, PAN);
        sysBean.AddSQLParam(emDataType.STR, ExtenNo);
        sysBean.AddSQLParam(emDataType.STR, ExpireDate);
        
        sysBean.AddSQLParam(emDataType.STR, TransCode);
        sysBean.AddSQLParam(emDataType.STR, ReversalFlag);
        sysBean.AddSQLParam(emDataType.STR, TransDate);
        sysBean.AddSQLParam(emDataType.STR, TransTime);
        sysBean.AddSQLParam(emDataType.STR, CurrencyCode);
        sysBean.AddSQLParam(emDataType.INT, TransAmt);
        sysBean.AddSQLParam(emDataType.STR, ApproveCode);
        sysBean.AddSQLParam(emDataType.STR, ResponseCode);
        sysBean.AddSQLParam(emDataType.STR, ResponseMsg.trim());
        sysBean.AddSQLParam(emDataType.STR, Entry_Mode);
        
        sysBean.AddSQLParam(emDataType.STR, Condition_Code);
        sysBean.AddSQLParam(emDataType.STR, BatchNo);
        sysBean.AddSQLParam(emDataType.STR, UserDefine);
        sysBean.AddSQLParam(emDataType.STR, EMail);
        sysBean.AddSQLParam(emDataType.STR, MTI);
        sysBean.AddSQLParam(emDataType.STR, RRN);
        sysBean.AddSQLParam(emDataType.STR, SocialID);
        sysBean.AddSQLParam(emDataType.STR, Status);
        sysBean.AddSQLParam(emDataType.STR, Resp_Date);
        sysBean.AddSQLParam(emDataType.STR, Resp_Time);
        
        sysBean.AddSQLParam(emDataType.STR, Resp_ResponseCode);
        sysBean.AddSQLParam(emDataType.STR, Resp_ApproveCode);
        sysBean.AddSQLParam(emDataType.STR, Resp_ResponseMsg.trim());
        sysBean.AddSQLParam(emDataType.STR, SysTraceNo);
        sysBean.AddSQLParam(emDataType.INT, Retry_Cnt);
        sysBean.AddSQLParam(emDataType.STR, ECI);
        sysBean.AddSQLParam(emDataType.STR, TransType);
        
        //System.out.println("Sql=" + Sql);
        ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/05/08 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-031 */
        	arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        System.out.println("[insert_SAF_Cup]_arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
            Flag = true;
        }

        return Flag;
    }
    
    /**
     * �s�W�дڥ�����(Capture)
     * @param  DataBaseBean SysBean     ��Ʈw
     * @param  String MerchantID    �S���N��
     * @param  String SubMID            �A�ȥN��
     * @param  String TerminalID    �ݥ����N��
     * @param  String AcquirerID    �����N��
     * @param  String OrderID           �S���q��s��
     * @param  String Sys_OrderID   �t�Ϋ��w�渹
     * @param  String Card_Type     �d�O
     * @param  String PAN           �d��
     * @param  String ExpireDate    ���Ĵ���
     * @param  String TransCode         ����N�X
     * @param  String TransDate         ���v���
     * @param  String TransTime     ���v�ɶ�
     * @param  String ApproveCode   ���v�X
     * @param  String ResponseCode  �^���X
     * @param  String ResponseMsg   �^���T��
     * @param  String CurrencyCode  ���v���O
     * @param  String CaptureAmt        �дڪ��B
     * @param  String CaptureDate       �дڤ��
     * @param  String UserDefine    �ө��ۭq�T��
     * @param  String BatchNo           ���v�妸���X
     * @param  String CaptureFlag       �дڱ���X��
     * @param  String ProcessDate       �дڳB�z��
     * @param  String Entry_Mode        Entry_Mode
     * @param  String Condition_Code    Condition_Code
     * @param  String ECI           ECI
     * @param  String CAVV          3D Secure PARes.cavv
     * @param  String TransMode         ����Ҧ�
     * @param  String InstallType   ��������O�p���X��
     * @param  String Install           ��������
     * @param  String FirstAmt          �������B
     * @param  String EachAmt           �C�����B
     * @param  String FEE           ����O
     * @param  String RedemType         ���Q���X��
     * @param  String RedemUsed     ���Q����I��
     * @param  String RedemBalance  ���Q�l�B
     * @param  String CreditAmt     �d�H�۲��B
     * @param  String BillMessage   �b��T��
     * @param  String FeeBackCode       �дڦ^�нX
     * @param  String FeeBackMsg        �дڦ^�аT��
     * @param  String FeeBackDate       �дڦ^�Ф��
     * @param  String DueDate           �дڴ���
     * @param  String TransAmt          ��дڪ��B
     * @param  String SysTraceNo    gateway�^�Ъ�traceno
     * @param  String ExtenNo           ExtenNo
     * @param  String RRN           RRN
     * @param  String MTI           MTI
     * @param  String XID           XID
     * @param  String SocialID          SocialID
     * @param  String AUTH_SRC_CODE     VISA AUTH_SRC_CODE
     * @return boolean                  �s�W���G
     */
    public boolean insert_Capture(DataBaseBean2 sysBean, String MerchantID, String SubMID, String TerminalID,
            String AcquirerID, String OrderID, String Sys_OrderID, String Card_Type,
            String PAN, String ExpireDate, String TransCode, String TransDate,
            String TransTime, String ApproveCode, String ResponseCode, String ResponseMsg,
            String CurrencyCode, String CaptureAmt, String CaptureDate, String UserDefine,
            String BatchNo, String CaptureFlag, String ProcessDate, String Entry_Mode,
            String Condition_Code, String ECI, String CAVV, String TransMode,
            String InstallType, String Install, String FirstAmt, String EachAmt, String FEE,
            String RedemType, String RedemUsed, String RedemBalance, String CreditAmt,
            String BillMessage, String FeeBackCode, String FeeBackMsg, String FeeBackDate,
            String DueDate, String TransAmt, String SysTraceNo, String ExtenNo, String RRN, String MTI, String XID, String SocialID,
            String ReauthFlag, String ExceptFlag,String AUTH_SRC_CODE) 
    {
		//20220210 ADD AUTH_SRC_CODE
		boolean Flag = false;
		StringBuffer sSQLSB = new StringBuffer();
		sysBean.ClearSQLParam();
		sSQLSB.append("INSERT INTO CAPTURE ( MERCHANTID,SUBMID,TERMINALID,ACQUIRERID,ORDERID,SYS_ORDERID,CARD_TYPE,PAN,EXPIREDATE,TRANSCODE,TRANSDATE,TRANSTIME,APPROVECODE,RESPONSECODE,RESPONSEMSG,CURRENCYCODE,CAPTUREAMT,CAPTUREDATE,USERDEFINE,BATCHNO,CAPTUREFLAG,PROCESSDATE,ENTRY_MODE,CONDITION_CODE,ECI,CAVV,TRANSMODE,INSTALLTYPE,INSTALL,FIRSTAMT,EACHAMT,FEE,REDEMTYPE,REDEMUSED,REDEMBALANCE,CREDITAMT,BILLMESSAGE,FEEDBACKCODE,FEEDBACKMSG,FEEDBACKDATE,DUE_DATE,TRANSAMT,SYSTRACENO,EXTENNO,RRN,MTI,XID,SOCIALID, REAUTH_FLAG, EXCEPT_FLAG,AUTH_SRC_CODE ) VALUES ( ");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		
		sysBean.AddSQLParam(emDataType.STR, MerchantID);
		sysBean.AddSQLParam(emDataType.STR, SubMID);
		sysBean.AddSQLParam(emDataType.STR, TerminalID);
		sysBean.AddSQLParam(emDataType.STR, AcquirerID);
		sysBean.AddSQLParam(emDataType.STR, OrderID);
		sysBean.AddSQLParam(emDataType.STR, Sys_OrderID);
		sysBean.AddSQLParam(emDataType.STR, Card_Type);
		sysBean.AddSQLParam(emDataType.STR, PAN);
		sysBean.AddSQLParam(emDataType.STR, ExpireDate);
		sysBean.AddSQLParam(emDataType.STR, TransCode);
		sysBean.AddSQLParam(emDataType.STR, TransDate);
		sysBean.AddSQLParam(emDataType.STR, TransTime);
		sysBean.AddSQLParam(emDataType.STR, ApproveCode);
		sysBean.AddSQLParam(emDataType.STR, ResponseCode);
		sysBean.AddSQLParam(emDataType.STR, ResponseMsg.trim());
		sysBean.AddSQLParam(emDataType.STR, CurrencyCode);
		sysBean.AddSQLParam(emDataType.INT, CaptureAmt);
		
		if (CaptureDate.length() > 0 && !CaptureDate.equalsIgnoreCase("null")) {
			sSQLSB.append(" TO_DATE(?, 'YYYY/MM/DD HH24:MI:SS'),");
			sysBean.AddSQLParam(emDataType.STR, CaptureDate);
			
		} else {
			CaptureDate = "null,";
			sSQLSB.append(CaptureDate);
		}

		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		
		sysBean.AddSQLParam(emDataType.STR, UserDefine);
		sysBean.AddSQLParam(emDataType.STR, BatchNo);
		sysBean.AddSQLParam(emDataType.STR, CaptureFlag);

		if (ProcessDate.length() > 0 && !ProcessDate.equalsIgnoreCase("null")) {
			sSQLSB.append(" TO_DATE( ?, 'YYYY/MM/DD HH24:MI:SS'),");
			sysBean.AddSQLParam(emDataType.STR, ProcessDate);
		} else {
			ProcessDate = "null,";
			sSQLSB.append(ProcessDate);
		}

		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		
		sysBean.AddSQLParam(emDataType.STR, Entry_Mode);
		sysBean.AddSQLParam(emDataType.STR, Condition_Code);
		sysBean.AddSQLParam(emDataType.STR, ECI);
		sysBean.AddSQLParam(emDataType.STR, CAVV);
		sysBean.AddSQLParam(emDataType.INT, TransMode);
		sysBean.AddSQLParam(emDataType.STR, InstallType);
		sysBean.AddSQLParam(emDataType.INT, Install);
		sysBean.AddSQLParam(emDataType.INT, FirstAmt);
		sysBean.AddSQLParam(emDataType.INT, EachAmt);
		sysBean.AddSQLParam(emDataType.INT, FEE);
		sysBean.AddSQLParam(emDataType.STR, RedemType);
		sysBean.AddSQLParam(emDataType.STR, RedemUsed);
		sysBean.AddSQLParam(emDataType.STR, RedemBalance);
		sysBean.AddSQLParam(emDataType.INT, CreditAmt);
		sysBean.AddSQLParam(emDataType.STR, BillMessage);
		sysBean.AddSQLParam(emDataType.STR, FeeBackCode);
		sysBean.AddSQLParam(emDataType.STR, FeeBackMsg);
		
		if (FeeBackDate.length() > 0 && !FeeBackDate.equalsIgnoreCase("null")) {
			sSQLSB.append(" TO_DATE( ?, 'YYYY/MM/DD HH24:MI:SS'),");
			sysBean.AddSQLParam(emDataType.STR, FeeBackDate);
			
		} else {
			FeeBackDate = "null,";
			sSQLSB.append(FeeBackDate);
		}

		if (DueDate.length() > 0 && !DueDate.equalsIgnoreCase("null")) {
			sSQLSB.append("TO_DATE( ?, 'YYYY/MM/DD HH24:MI:SS'), ");
			sysBean.AddSQLParam(emDataType.STR, DueDate);
		} else {
			sSQLSB.append("null,");
		}

		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");
		sSQLSB.append(" ?,");

		sysBean.AddSQLParam(emDataType.INT, TransAmt);
		sysBean.AddSQLParam(emDataType.STR, SysTraceNo);
		sysBean.AddSQLParam(emDataType.STR, ExtenNo);
		sysBean.AddSQLParam(emDataType.STR, RRN);
		sysBean.AddSQLParam(emDataType.STR, MTI);
		sysBean.AddSQLParam(emDataType.STR, XID);
		sysBean.AddSQLParam(emDataType.STR, SocialID);
		
		if (ReauthFlag.length() > 0) {
			sSQLSB.append(" ?,");
			sysBean.AddSQLParam(emDataType.STR, ReauthFlag);
		} else {
			sSQLSB.append("null,");
		}

		if (ExceptFlag.length() > 0) {
			sSQLSB.append(" ?,");
			sysBean.AddSQLParam(emDataType.STR, ExceptFlag);
		} else {
			sSQLSB.append("null,");
		}

		sSQLSB.append(" ?");
		sysBean.AddSQLParam(emDataType.STR, AUTH_SRC_CODE);
		
		sSQLSB.append(" ) ");
		// System.out.println("Sql=" + Sql);
		
		ArrayList arraySys = new ArrayList();
		try {
			/** 2023/04/28 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-018 */
			arraySys = sysBean.QuerySQLByParam(sSQLSB.toString());
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		
        System.out.println("arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
            Flag = true;
        }		

		return Flag;
	}
	
    /**
     * �s�W�b�������(Batch)
     * @param  DataBaseBean SysBean     ��Ʈw
     * @param  String MerchantID    �S���N��
     * @param  String SubMID            �A�ȥN��
     * @param  String TerminalID    �ݥ����N��
     * @param  String AcquirerID    �����N��
     * @param  String OrderID           �S���q��s��
     * @param  String Sys_OrderID   �t�Ϋ��w�渹
     * @param  String Card_Type     �d�O
     * @param  String PAN           �d��
     * @param  String ExtenNo       CVV2/CVC2
     * @param  String ExpireDate    ���Ĵ���
     * @param  String TransCode         ����N�X
     * @param  String ReversalFlag  Auto-Reversal �X��
     * @param  String TransDate         ���v���
     * @param  String TransTime         ���v�ɶ�
     * @param  String CurrencyCode  ���v���O
     * @param  String TransAmt          �дڪ��B
     * @param  String ApproveCode   ���v�X
     * @param  String ResponseCode  �^���X
     * @param  String ResponseMsg   �^���T��
     * @param  String BatchNo           ���v�妸���X
     * @param  String UserDefine    �ө��ۭq�T��
     * @param  String EMail         ���d�H eMail
     * @param  String MTI           ���v MTI
     * @param  String RRN           ���v RRN
     * @param  String SocialID          �����Ҧr��
     * @param  String Entry_Mode    Entry Mode
     * @param  String Condition_Code    Condition Code
     * @param  String TransMode     ����Ҧ�
     * @param  String TransType     �������
     * @param  String ECI           ECI
     * @param  String CAVV          3D Secure PARes.cavv
     * @param  String XID           3D Secure PARes.xid
     * @param  String InstallType   ��������O�p���X��
     * @param  String Install           ��������
     * @param  String FirstAmt          �������B
     * @param  String EachAmt       �C�����B
     * @param  String FEE           ����O
     * @param  String RedemType         ���Q���X��
     * @param  String RedemUsed         ���Q����I��
     * @param  String RedemBalance  ���Q�l�B
     * @param  String CreditAmt         �d�H�ۥI�B
     * @param  String BillMessage   �b��T��
     * @param  String BalanceAmt    �i�дھl�B
     * @param  String CaptureAmt        �дڪ��B
     * @param  String CaptureDate       �дڤ��
     * @param  String FeedbackCode  �дڳB�z�^�аT��
     * @param  String FeedbackMsg       �дڳB�z�^�аT������
     * @param  String FeedbackDate  �дڳB�z���
     * @param  String Due_Date          �дڨ����
     * @param  String BatchPmtid        �妸����Ǹ�
     * @param  String BatchDate         �妸������
     * @param  String BatchHead         �妸������Y
     * @param  String BatchType         �妸������O
     * @param  String BatchTerminalID   �妸����ݥ����N��
     * @param  String BatchSysorderID   �妸����S���q��s��
     * @param  String BatchTxDate       �妸�������
     * @param  String BatchTxTime       �妸�����ɶ�
     * @param  String BatchTxApprovecode�妸������v�X
     * @param  String BatchTransCode    �妸���������O
     * @param  String BatchTxAmt        �妸������B
     * @param  String BatchTxMsg        �妸����B�z�^��
     * @param  String BatchResponse     �妸����B�z�^�л���
     * @param  String SysTraceNo    gateway�^�Ъ�traceno
     * @param  String Capture_RowID ��д�ROWID
     * @param  String AUTH_SRC_CODE     VISA AUTH_SRC_CODE
     * @return boolean                  �s�W���G
     */
    public boolean insert_Batch(DataBaseBean2 sysBean, String MerchantID, String SubMID,
                                String TerminalID, String AcquirerID, String OrderID, String Sys_OrderID,
                                String Card_Type, String PAN, String ExtenNo, String ExpireDate, String TransCode,
                                String ReversalFlag, String TransDate, String TransTime, String CurrencyCode,
                                String TransAmt, String ApproveCode, String ResponseCode, String ResponseMsg,
                                String BatchNo, String UserDefine, String EMail, String MTI, String RRN,
                                String SocialID, String Entry_Mode, String Condition_Code, String TransMode,
                                String TransType, String ECI, String CAVV, String XID, String InstallType,
                                String Install, String FirstAmt, String EachAmt, String FEE, String RedemType,
                                String RedemUsed, String RedemBalance, String CreditAmt, String BillMessage,
                                String BalanceAmt, String CaptureAmt, String CaptureDate, String FeedbackCode,
                                String FeedbackMsg, String FeedbackDate, String Due_Date, String BatchPmtID,
                                String BatchDate, String BatchHead, String BatchType, String BatchTerminalID,
                                String BatchSysorderID, String BatchTxDate, String BatchTxTime, String BatchTxApproveCode,
                                String BatchTransCode, String BatchTxAmt, String BatchTxMsg, String BatchResponse,
                                String SysTraceNo, String Capture_RowID,String AUTH_SRC_CODE)
    {    
        boolean Flag = false;
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append(" INSERT INTO BATCH ( MERCHANTID,SUBMID,TERMINALID,ACQUIRERID,ORDERID,SYS_ORDERID,CARD_TYPE,PAN,EXTENNO,EXPIREDATE,TRANSCODE,REVERSALFLAG,TRANSDATE,TRANSTIME,CURRENCYCODE,TRANSAMT,APPROVECODE,RESPONSECODE,RESPONSEMSG,BATCHNO,USERDEFINE,EMAIL,MTI,RRN,SOCIALID,ENTRY_MODE,CONDITION_CODE,TRANSMODE,TRANSTYPE,ECI,CAVV,XID,INSTALLTYPE,INSTALL,FIRSTAMT,EACHAMT,FEE,REDEMTYPE,REDEMUSED,REDEMBALANCE,CREDITAMT,BILLMESSAGE,BALANCEAMT,CAPTUREAMT,CAPTUREDATE,FEEDBACKCODE,FEEDBACKMSG,FEEDBACKDATE,DUE_DATE,BATCHPMTID,BATCHDATE,BATCHHEAD,BATCHTYPE,BATCHTERMINALID,BATCHSYSORDERID,BATCHTXDATE,BATCHTXTIME,BATCHTXAPPROVECODE,BATCHTRANSCODE,BATCHTXAMT,BATCHTXMSG,BATCHRESPONSE,SYSTRACENO,CAPTURE_ROWID,AUTH_SRC_CODE) VALUES (");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        
        sysBean.AddSQLParam(emDataType.STR, MerchantID.trim());
        sysBean.AddSQLParam(emDataType.STR, SubMID);
        sysBean.AddSQLParam(emDataType.STR, TerminalID);
        sysBean.AddSQLParam(emDataType.STR, AcquirerID);
        sysBean.AddSQLParam(emDataType.STR, OrderID);
        sysBean.AddSQLParam(emDataType.STR, Sys_OrderID);
        sysBean.AddSQLParam(emDataType.STR, Card_Type);
        sysBean.AddSQLParam(emDataType.STR, PAN);
        sysBean.AddSQLParam(emDataType.STR, ExtenNo);
        sysBean.AddSQLParam(emDataType.STR, ExpireDate);
        sysBean.AddSQLParam(emDataType.STR, TransCode);
        sysBean.AddSQLParam(emDataType.STR, ReversalFlag);
        sysBean.AddSQLParam(emDataType.STR, TransDate);
        sysBean.AddSQLParam(emDataType.STR, TransTime);
        sysBean.AddSQLParam(emDataType.STR, CurrencyCode);

        if (TransAmt.length() == 0) {
            TransAmt = "null";
        }
        
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        
        sysBean.AddSQLParam(emDataType.INT, TransAmt);
        sysBean.AddSQLParam(emDataType.STR, ApproveCode);
        sysBean.AddSQLParam(emDataType.STR, ResponseCode);
        sysBean.AddSQLParam(emDataType.STR, ResponseMsg.trim());
        sysBean.AddSQLParam(emDataType.STR, BatchNo);
        sysBean.AddSQLParam(emDataType.STR, UserDefine);
        sysBean.AddSQLParam(emDataType.STR, EMail);
        sysBean.AddSQLParam(emDataType.STR, MTI);
        sysBean.AddSQLParam(emDataType.STR, RRN);
        sysBean.AddSQLParam(emDataType.STR, SocialID);
        sysBean.AddSQLParam(emDataType.STR, Entry_Mode);
        sysBean.AddSQLParam(emDataType.STR, Condition_Code);

        if (TransMode.length() == 0) {
            TransMode = "null";
        }
        
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        
        sysBean.AddSQLParam(emDataType.INT, TransMode);
        sysBean.AddSQLParam(emDataType.STR, TransType);
        sysBean.AddSQLParam(emDataType.STR, ECI);
        sysBean.AddSQLParam(emDataType.STR, CAVV);
        sysBean.AddSQLParam(emDataType.STR, XID);
        sysBean.AddSQLParam(emDataType.STR, InstallType);

        if (Install.length() == 0) {
            Install = "null";
        }
        
        sSQLSB.append( Install + ",");
        if (FirstAmt.length() == 0) {
            FirstAmt = "null";
        }
        
        sSQLSB.append( FirstAmt + ",");
        if (EachAmt.length() == 0) {
            EachAmt = "null";
        }
        
        sSQLSB.append( EachAmt + ",");
        if (FEE.length() == 0) {
            FEE = "null";
        }
        
        sSQLSB.append( FEE + ",");
        sSQLSB.append("'" + RedemType + "',");
        if (RedemUsed.length() == 0) {
            RedemUsed = "null";
        }
        
        sSQLSB.append( RedemUsed + ",");
        if (RedemBalance.length() == 0) {
            RedemBalance = "null";
        }
        
        sSQLSB.append( RedemBalance + ",");
        if (CreditAmt.length() == 0) {
            CreditAmt = "null";
        }
        
        sSQLSB.append( CreditAmt + ",");
        sSQLSB.append("'" + BillMessage + "',");

        if (BalanceAmt.length() == 0) {
            BalanceAmt = "null";
        }
        
        sSQLSB.append( BalanceAmt + ",");
        if (CaptureAmt.length() == 0) {
            CaptureAmt = "null";
        }
        
        sSQLSB.append( CaptureAmt + ",");
        if (CaptureDate.length() > 0 && !CaptureDate.equalsIgnoreCase("null"))
        {
            sSQLSB.append(" TO_DATE( ?, 'YYYY/MM/DD HH24:MI:SS'), ");
            sysBean.AddSQLParam(emDataType.STR, CaptureDate);
        }
        else
        {
            sSQLSB.append("null,");
        }

        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sysBean.AddSQLParam(emDataType.STR, FeedbackCode);
        sysBean.AddSQLParam(emDataType.STR, FeedbackMsg);

        if (FeedbackDate.length() > 0 && !FeedbackDate.equalsIgnoreCase("null"))
        {
            sSQLSB.append(" TO_DATE( ?, 'YYYY/MM/DD HH24:MI:SS'), ");
            sysBean.AddSQLParam(emDataType.STR, TransMode);
        }
        else
        {
            sSQLSB.append("null,");
        }

        if (Due_Date.length()>0 && !Due_Date.equalsIgnoreCase("null"))
        {
            sSQLSB.append("TO_DATE( ?, 'YYYY/MM/DD'), ");
            sysBean.AddSQLParam(emDataType.STR, Due_Date);
        }
        else
        {
            sSQLSB.append("null,");
        }

        sSQLSB.append("'" + BatchPmtID + "',");
        if (BatchDate.length() > 0 && !BatchDate.equalsIgnoreCase("null"))
        {
            sSQLSB.append(" TO_DATE( ?, 'YYYY/MM/DD HH24:MI:SS'), ");
            sysBean.AddSQLParam(emDataType.STR, BatchDate);
        }
        else
        {
            sSQLSB.append("SYSDATE, ");
        }

        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        
        sysBean.AddSQLParam(emDataType.STR, BatchHead);
        sysBean.AddSQLParam(emDataType.STR, BatchType);
        sysBean.AddSQLParam(emDataType.STR, BatchTerminalID);
        sysBean.AddSQLParam(emDataType.STR, BatchSysorderID);
        sysBean.AddSQLParam(emDataType.STR, BatchTxDate);
        sysBean.AddSQLParam(emDataType.STR, BatchTxTime);
        sysBean.AddSQLParam(emDataType.STR, BatchTxApproveCode);
        sysBean.AddSQLParam(emDataType.STR, BatchTransCode);

        if (BatchTxAmt.length() == 0) {
            BatchTxAmt = "null";
        }
        
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?,");
        sSQLSB.append(" ?) ");
        
        sysBean.AddSQLParam(emDataType.INT, BatchTxAmt);
        sysBean.AddSQLParam(emDataType.STR, BatchTxMsg);
        sysBean.AddSQLParam(emDataType.STR, BatchResponse);
        sysBean.AddSQLParam(emDataType.STR, SysTraceNo);
        sysBean.AddSQLParam(emDataType.STR, Capture_RowID);
        sysBean.AddSQLParam(emDataType.STR, AUTH_SRC_CODE);
        
        //System.out.println("Sql=" + Sql);

        ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/04/26 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-020 */
        	arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        System.out.println("arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
            Flag = true;
        }
        
        return Flag;
    }

//    /**
//     * ���o�妸������(Batch)
//     * @param String MerchantID     �S���N��
//     * @param String SubMID         �S���N��
//     * @param String BatchPmtID     �妸�Ǹ�
//     * @param String BatchTxMsg     �妸�B�z���A
//     * @param String OrderBy        �ƧǤ覡
//     * @return ArrayList            �b����
//     */
//    public ArrayList get_Batch(String MerchantID, String SubMID,
//                               String BatchPmtID, String BatchTxMsg,
//                               String OrderBy)
//    {
//        ArrayList arrayBillingData = new ArrayList(); // ��ܭq�������
//        DataBaseBean SysBean = new DataBaseBean();
//
//        if (MerchantID.length() > 0 && SubMID.length() > 0 && BatchPmtID.length() > 0)
//        {
//        	StringBuffer Sql =new StringBuffer( "SELECT ROWIDTOCHAR(A.ROWID) AS RROWID, A.MERCHANTID, A.SUBMID, A.TERMINALID, A.ACQUIRERID, A.ORDERID, A.SYS_ORDERID, A.CARD_TYPE, A.PAN, A.EXTENNO, A.EXPIREDATE, A.TRANSCODE, A.REVERSALFLAG, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.APPROVECODE, A.RESPONSECODE, A.RESPONSEMSG, A.BATCHNO, A.USERDEFINE, A.EMAIL, A.MTI, A.RRN, A.SOCIALID, A.ENTRY_MODE, A.CONDITION_CODE, A.TRANSMODE, A.TRANSTYPE, A.ECI, A.CAVV, A.XID, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE, A.BALANCEAMT, A.CAPTUREAMT, TO_CHAR(A.CAPTUREDATE,'YYYY/MM/DD HH24:MI:SS') CAPTUREDATE, A.FEEDBACKCODE, A.FEEDBACKMSG, TO_CHAR(A.FEEDBACKDATE,'YYYY/MM/DD HH24:MI:SS') FEEDBACKDATE, TO_CHAR(A.DUE_DATE,'YYYY/MM/DD') DUE_DATE, A.BATCHPMTID, TO_CHAR(A.BATCHDATE,'YYYY/MM/DD HH24:MI:SS') BATCHDATE, A.BATCHHEAD, A.BATCHTYPE, A.BATCHTERMINALID, A.BATCHSYSORDERID, A.BATCHTXDATE, A.BATCHTXTIME, A.BATCHTXAPPROVECODE, A.BATCHTRANSCODE, A.BATCHTXAMT, A.BATCHTXMSG, A.BATCHRESPONSE, A.SYSTRACENO, A.CAPTURE_ROWID FROM BATCH A WHERE ");
//            Sql.append( " A.MERCHANTID = '" + MerchantID + "' ");
//            Sql.append( " AND A.SUBMID = '" + SubMID + "' ");
//            Sql.append( " AND A.BATCHPMTID = '" + BatchPmtID + "' ");
//            if (BatchTxMsg.length() > 0)
//            {
//                Sql.append( " AND A.BATCHTXMSG = '" + BatchTxMsg + "' ");
//            }
//
//            if (OrderBy.length() > 0)
//            {
//                String TmpSql = "";
//                String arrayOrderBy[] = OrderBy.split(",");
//
//                for (int i = 0; i < arrayOrderBy.length; i++)
//                {
//                    if (TmpSql.length() == 0)
//                    {
//                        TmpSql = TmpSql + " ORDER BY ";
//                    }
//                    else
//                    {
//                        TmpSql = TmpSql + ",";
//                    }
//
//                    if (arrayOrderBy[i].length() > 0)
//                    {
//                        TmpSql = TmpSql + " A." + arrayOrderBy[i];
//                    }
//                }
//
//                Sql.append( TmpSql);
//            }
//            // System.out.println("Sql=" + Sql);
//            try
//            {
//                arrayBillingData = (ArrayList) SysBean.executeSQL(Sql.toString(), "select");
//            }
//            catch (Exception ex)
//            {
//                System.out.println(ex.getMessage());
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

    /* Override get_Batch with DatabaseBean parameter */
    public ArrayList get_Batch(DataBaseBean2 sysBean, String MerchantID, String SubMID, String BatchPmtID, String BatchTxMsg, String OrderBy)
    {
        ArrayList arrayBillingData = new ArrayList(); // ��ܭq�������
        // DataBaseBean SysBean = new DataBaseBean();

        if (MerchantID.length() > 0 && SubMID.length() > 0 && BatchPmtID.length() > 0)
        {
        	//20220210 ADD AUTH_SRC_CODE
        	StringBuffer sSQLSB = new StringBuffer();
            sysBean.ClearSQLParam();
    		sSQLSB.append(" SELECT ROWIDTOCHAR(A.ROWID) AS RROWID, A.MERCHANTID, A.SUBMID, A.TERMINALID, A.ACQUIRERID, A.ORDERID, A.SYS_ORDERID, A.CARD_TYPE, A.PAN, A.EXTENNO, A.EXPIREDATE, A.TRANSCODE, A.REVERSALFLAG, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.APPROVECODE, A.RESPONSECODE, A.RESPONSEMSG, A.BATCHNO, A.USERDEFINE, A.EMAIL, A.MTI, A.RRN, A.SOCIALID, A.ENTRY_MODE, A.CONDITION_CODE, A.TRANSMODE, A.TRANSTYPE, A.ECI, A.CAVV, A.XID, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE, A.BALANCEAMT, A.CAPTUREAMT, TO_CHAR(A.CAPTUREDATE,'YYYY/MM/DD HH24:MI:SS') CAPTUREDATE, A.FEEDBACKCODE, A.FEEDBACKMSG, TO_CHAR(A.FEEDBACKDATE,'YYYY/MM/DD HH24:MI:SS') FEEDBACKDATE, TO_CHAR(A.DUE_DATE,'YYYY/MM/DD') DUE_DATE, A.BATCHPMTID, TO_CHAR(A.BATCHDATE,'YYYY/MM/DD HH24:MI:SS') BATCHDATE, A.BATCHHEAD, A.BATCHTYPE, A.BATCHTERMINALID, A.BATCHSYSORDERID, A.BATCHTXDATE, A.BATCHTXTIME, A.BATCHTXAPPROVECODE, A.BATCHTRANSCODE, A.BATCHTXAMT, A.BATCHTXMSG, A.BATCHRESPONSE, A.SYSTRACENO, A.CAPTURE_ROWID, A.AUTH_SRC_CODE FROM BATCH A WHERE ");
    		sSQLSB.append(" A.MERCHANTID = ? ");
    		sSQLSB.append(" AND A.SUBMID = ? ");
    		sSQLSB.append(" AND A.BATCHPMTID = ? ");
    		
    		sysBean.AddSQLParam(emDataType.STR, MerchantID);
    		sysBean.AddSQLParam(emDataType.STR, SubMID);
    		sysBean.AddSQLParam(emDataType.STR, BatchPmtID);
    		
            if (BatchTxMsg.length() > 0)
            {
            	sSQLSB.append(" AND A.BATCHTXMSG = ? ");
            	sysBean.AddSQLParam(emDataType.STR, BatchTxMsg);
            }

            if (OrderBy.length() > 0)
            {
                StringBuffer tmpSql = new StringBuffer();
                String arrayOrderBy[] = OrderBy.split(",");

                for (int i = 0; i < arrayOrderBy.length; i++)
                {
                    if (tmpSql.toString().length() == 0)
                    {
                        tmpSql.append(" ORDER BY ");
                    }
                    else
                    {
                        tmpSql.append(",");
                    }

                    if (arrayOrderBy[i].length() > 0)
                    {
                        tmpSql.append(" A." + arrayOrderBy[i]);
                    }
                }

                sSQLSB.append(tmpSql.toString());
            }
            //System.out.println("Sql=" + Sql);
            try
            {
            	/** 2023/05/03 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-026 */
                arrayBillingData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
            }

            if (arrayBillingData == null)
            {
                arrayBillingData = new ArrayList();
            }
        }

        return arrayBillingData;
    }

//    /**
//     * ��s�妸�B�z���G(Batch)
//     * @param  String BatchTxMsg    �妸�B�z���G
//     * @param  String BatchTxResponse   �妸�B�z���G����
//     * @param  String RowID         �妸��sID
//     * @return boolean                  ��s���G
//     */
//    public boolean update_Batch_Status(String BatchTxMsg, String BatchTxResponse, String RowID)
//    {
//        boolean Flag = false;
//        DataBaseBean SysBean = new DataBaseBean();
//
//        String Sql = " UPDATE BATCH SET BATCHTXMSG = '" + BatchTxMsg + "', BATCHRESPONSE='" + BatchTxResponse + "' WHERE ROWID ='" + RowID + "'";
//        // System.out.println("Sql=" + Sql);
//        try
//        {
//            Flag = ((Boolean) SysBean.executeSQL(Sql, "update")).booleanValue();
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//        }
//
//        return Flag;
//    }

    /* Override update_Batch_Status with DatabaseBean as parameter */
    public boolean update_Batch_Status(DataBaseBean2 sysBean, String BatchTxMsg, String BatchTxResponse, String RowID)
    {
        boolean flag = false;
        // DataBaseBean SysBean = new DataBaseBean();

        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append(" UPDATE BATCH SET BATCHTXMSG = ? , BATCHRESPONSE = ? WHERE ROWID = ? ");
		
		sysBean.AddSQLParam(emDataType.STR, BatchTxMsg);
		sysBean.AddSQLParam(emDataType.STR, BatchTxResponse);
		sysBean.AddSQLParam(emDataType.STR, RowID);
		
        // System.out.println("Sql=" + Sql);
        ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/05/17 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-093 (No Need Test) */
        	arraySys = sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        
        System.out.println("arraySys.size()="+arraySys.size());
        if(arraySys.size() > 0) 
        {
        	flag = true;
        }  

        return flag;
    }

//    /**
//     * ���o�妸������(Batch)
//     * @param String MerchantID     �S���N��
//     * @param String SubMID         �S���N��
//     * @param String BatchPmtID     �妸�Ǹ�
//     * @param String BatchTxMsg     �妸�B�z���A
//     * @param String OrderBy        �ƧǤ覡
//     * @param boolean Flag          �O�_��ROWDATA
//     * @return ResultSet            �b����
//     */
//    public ResultSet get_Batch_Result(String MerchantID, String SubMID, String BatchPmtID, String BatchTxMsg, String OrderBy, boolean Flag)
//    {
//        ResultBean = new DataBaseBean();
//        StringBuffer Sql =new StringBuffer( );
//
//        if (Flag)
//        {  // ��rowdata
//        	 Sql.append( "SELECT ROWIDTOCHAR(A.ROWID) AS RROWID, A.MERCHANTID, A.SUBMID, A.TERMINALID, A.ACQUIRERID, A.ORDERID, A.SYS_ORDERID, A.CARD_TYPE, A.PAN, A.EXTENNO, A.EXPIREDATE, A.TRANSCODE, A.REVERSALFLAG, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.APPROVECODE, A.RESPONSECODE, A.RESPONSEMSG, A.BATCHNO, A.USERDEFINE, A.EMAIL, A.MTI, A.RRN, A.SOCIALID, A.ENTRY_MODE, A.CONDITION_CODE, A.TRANSMODE, A.TRANSTYPE, A.ECI, A.CAVV, A.XID, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE, A.BALANCEAMT, A.CAPTUREAMT, TO_CHAR(A.CAPTUREDATE,'YYYYMMDDHH24MISS') CAPTUREDATE, A.FEEDBACKCODE, A.FEEDBACKMSG, TO_CHAR(A.FEEDBACKDATE,'YYYYMMDDHH24MISS') FEEDBACKDATE, TO_CHAR(A.DUE_DATE,'YYYYMMDD') DUE_DATE, A.BATCHPMTID, TO_CHAR(A.BATCHDATE,'YYYYMMDDHH24MISS') BATCHDATE, A.BATCHHEAD, A.BATCHTYPE, A.BATCHTERMINALID, A.BATCHSYSORDERID, A.BATCHTXDATE, A.BATCHTXTIME, A.BATCHTXAPPROVECODE, A.BATCHTRANSCODE, A.BATCHTXAMT, A.BATCHTXMSG, A.BATCHRESPONSE, A.SYSTRACENO, A.CAPTURE_ROWID FROM BATCH A WHERE ");
//        }
//        else
//        {
//        	 Sql.append("SELECT ROWIDTOCHAR(A.ROWID) AS RROWID, A.MERCHANTID, A.SUBMID, A.TERMINALID, A.ACQUIRERID, A.ORDERID, A.SYS_ORDERID, A.CARD_TYPE, A.PAN, A.EXTENNO, A.EXPIREDATE, A.TRANSCODE, A.REVERSALFLAG, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.APPROVECODE, A.RESPONSECODE, A.RESPONSEMSG, A.BATCHNO, A.USERDEFINE, A.EMAIL, A.MTI, A.RRN, A.SOCIALID, A.ENTRY_MODE, A.CONDITION_CODE, A.TRANSMODE, A.TRANSTYPE, A.ECI, A.CAVV, A.XID, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE, A.BALANCEAMT, A.CAPTUREAMT, TO_CHAR(A.CAPTUREDATE,'YYYY/MM/DD HH24:MI:SS') CAPTUREDATE, A.FEEDBACKCODE, A.FEEDBACKMSG, TO_CHAR(A.FEEDBACKDATE,'YYYY/MM/DD HH24:MI:SS') FEEDBACKDATE, TO_CHAR(A.DUE_DATE,'YYYY/MM/DD') DUE_DATE, A.BATCHPMTID, TO_CHAR(A.BATCHDATE,'YYYY/MM/DD HH24:MI:SS') BATCHDATE , A.BATCHHEAD, A.BATCHTYPE, A.BATCHTERMINALID, A.BATCHSYSORDERID, A.BATCHTXDATE, A.BATCHTXTIME, A.BATCHTXAPPROVECODE, A.BATCHTRANSCODE, A.BATCHTXAMT, A.BATCHTXMSG, A.BATCHRESPONSE, A.SYSTRACENO, A.CAPTURE_ROWID FROM BATCH A WHERE ");
//        }
//
//        Sql.append( " A.MERCHANTID = '" + MerchantID + "' ");
//        Sql.append( " AND A.SUBMID = '" + SubMID + "' ");
//        Sql.append( " AND A.BATCHPMTID = '" + BatchPmtID + "' ");
//        if (BatchTxMsg.length() > 0)
//        {
//            Sql.append( " AND A.BATCHTXMSG = '" + BatchTxMsg + "' ");
//        }
//
//        if (OrderBy.length() > 0)
//        {
//            String TmpSql = "";
//            String arrayOrderBy[] = OrderBy.split(",");
//
//            for (int i = 0; i < arrayOrderBy.length; i++)
//            {
//                if (TmpSql.length() == 0)
//                {
//                    TmpSql = TmpSql + " ORDER BY ";
//                }
//                else
//                {
//                    TmpSql = TmpSql + ",";
//                }
//
//                if (arrayOrderBy[i].length() > 0)
//                {
//                    TmpSql = TmpSql + " A." + arrayOrderBy[i];
//                }
//            }
//
//            Sql.append( TmpSql);
//        }
//
//        //System.out.println("Sql=" + Sql);
//        ResultSet ResultData = null;
//        try
//        {
//            ResultData = (ResultSet) ResultBean.executeReportSQL(SecurityTool.output(Sql.toString()), "select");
//            //20130702 Jason Fix Conn release 
//            ResultBean.close();
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//        }
//
//        return ResultData;
//    }

    /* Override get_Batch_Result with DatabaseBean as parameter */
    public String get_Batch_Result(DataBaseBean2 resultBean, String MerchantID, String SubMID, String BatchPmtID, String BatchTxMsg, String OrderBy, boolean Flag)
    {
        // ResultBean = new DataBaseBean();
    	StringBuffer sSQLSB = new StringBuffer();
    	resultBean.ClearSQLParam();

        if (Flag)
        {  // ��rowdata
        	sSQLSB.append("SELECT ROWIDTOCHAR(A.ROWID) AS RROWID, A.MERCHANTID, A.SUBMID, A.TERMINALID, A.ACQUIRERID, A.ORDERID, A.SYS_ORDERID, A.CARD_TYPE, A.PAN, A.EXTENNO, A.EXPIREDATE, A.TRANSCODE, A.REVERSALFLAG, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.APPROVECODE, A.RESPONSECODE, A.RESPONSEMSG, A.BATCHNO, A.USERDEFINE, A.EMAIL, A.MTI, A.RRN, A.SOCIALID, A.ENTRY_MODE, A.CONDITION_CODE, A.TRANSMODE, A.TRANSTYPE, A.ECI, A.CAVV, A.XID, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE, A.BALANCEAMT, A.CAPTUREAMT, TO_CHAR(A.CAPTUREDATE,'YYYYMMDDHH24MISS') CAPTUREDATE      , A.FEEDBACKCODE, A.FEEDBACKMSG, TO_CHAR(A.FEEDBACKDATE,'YYYYMMDDHH24MISS') FEEDBACKDATE       , TO_CHAR(A.DUE_DATE,'YYYYMMDD') DUE_DATE    , A.BATCHPMTID, TO_CHAR(A.BATCHDATE,'YYYYMMDDHH24MISS') BATCHDATE       , A.BATCHHEAD, A.BATCHTYPE, A.BATCHTERMINALID, A.BATCHSYSORDERID, A.BATCHTXDATE, A.BATCHTXTIME, A.BATCHTXAPPROVECODE, A.BATCHTRANSCODE, A.BATCHTXAMT, A.BATCHTXMSG, A.BATCHRESPONSE, A.SYSTRACENO, A.CAPTURE_ROWID FROM BATCH A WHERE ");
        }
        else
        {
        	sSQLSB.append("SELECT ROWIDTOCHAR(A.ROWID) AS RROWID, A.MERCHANTID, A.SUBMID, A.TERMINALID, A.ACQUIRERID, A.ORDERID, A.SYS_ORDERID, A.CARD_TYPE, A.PAN, A.EXTENNO, A.EXPIREDATE, A.TRANSCODE, A.REVERSALFLAG, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.APPROVECODE, A.RESPONSECODE, A.RESPONSEMSG, A.BATCHNO, A.USERDEFINE, A.EMAIL, A.MTI, A.RRN, A.SOCIALID, A.ENTRY_MODE, A.CONDITION_CODE, A.TRANSMODE, A.TRANSTYPE, A.ECI, A.CAVV, A.XID, A.INSTALLTYPE, A.INSTALL, A.FIRSTAMT, A.EACHAMT, A.FEE, A.REDEMTYPE, A.REDEMUSED, A.REDEMBALANCE, A.CREDITAMT, A.BILLMESSAGE, A.BALANCEAMT, A.CAPTUREAMT, TO_CHAR(A.CAPTUREDATE,'YYYY/MM/DD HH24:MI:SS') CAPTUREDATE, A.FEEDBACKCODE, A.FEEDBACKMSG, TO_CHAR(A.FEEDBACKDATE,'YYYY/MM/DD HH24:MI:SS') FEEDBACKDATE, TO_CHAR(A.DUE_DATE,'YYYY/MM/DD') DUE_DATE, A.BATCHPMTID, TO_CHAR(A.BATCHDATE,'YYYY/MM/DD HH24:MI:SS') BATCHDATE , A.BATCHHEAD, A.BATCHTYPE, A.BATCHTERMINALID, A.BATCHSYSORDERID, A.BATCHTXDATE, A.BATCHTXTIME, A.BATCHTXAPPROVECODE, A.BATCHTRANSCODE, A.BATCHTXAMT, A.BATCHTXMSG, A.BATCHRESPONSE, A.SYSTRACENO, A.CAPTURE_ROWID FROM BATCH A WHERE ");
        }

        sSQLSB.append(" A.MERCHANTID = ? ");
        sSQLSB.append(" AND A.SUBMID = ? ");
        sSQLSB.append(" AND A.BATCHPMTID = ? ");        
        resultBean.AddSQLParam(emDataType.STR, MerchantID);
        resultBean.AddSQLParam(emDataType.STR, SubMID);
        resultBean.AddSQLParam(emDataType.STR, BatchPmtID);
        
        if (BatchTxMsg.length() > 0)
        {
        	sSQLSB.append(" AND A.BATCHTXMSG = ? ");
        	resultBean.AddSQLParam(emDataType.STR, BatchTxMsg);
        }

        if (OrderBy.length() > 0)
        {
            StringBuffer tmpSql = new StringBuffer();
            String arrayOrderBy[] = OrderBy.split(",");

            for (int i = 0; i < arrayOrderBy.length; i++)
            {
                if (tmpSql.toString().length() == 0)
                {
                    tmpSql.append(" ORDER BY ");
                }
                else
                {
                    tmpSql.append(",");
                }

                if (arrayOrderBy[i].length() > 0)
                {
                    tmpSql.append(" A." + arrayOrderBy[i]);
                }
            }

            sSQLSB.append(tmpSql.toString());
        }

        //System.out.println("Sql=" + Sql);
//        ResultSet ResultData = null;
//        try
//        {
//            ResultData = (ResultSet) ResultBean.executeReportSQL(Sql, "select");
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//        }
        
        /** 2023/05/02 �� SQL (By : YC) *//** Test Case : IT-TESTCASE-021 */

        return sSQLSB.toString();
    }

//    /**
//     * ���o�򥻰ѼƸ��(Sys_Parm_List) 2007.12.26
//     * @param String Value     �d�߱���
//     * @return ArrayList       �d�߰ѼƸ��
//     */
//    public ArrayList get_Sys_Parm_List(String Value)
//    {
//        ArrayList arraySysData = new ArrayList();
//        DataBaseBean SysBean = new DataBaseBean();
//
//        StringBuffer Sql =new StringBuffer(  "SELECT * FROM SYS_PARM_LIST ");
//        if (Value.length() > 0)
//        {
//           Sql.append( " WHERE PARM_ID LIKE '"+ Value + "%'");
//        }
//        // System.out.println("Sql=" + Sql);
//
//        try
//        {
//            arraySysData = (ArrayList) SysBean.executeSQL(Sql.toString(), "select");
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//        }
//
//        if (arraySysData == null)
//        {
//            arraySysData = new ArrayList();
//        }
//
//        return arraySysData;
//    }

//    /* Override get_Sys_Parm_List with DataBaseBean parameter */
//    public ArrayList get_Sys_Parm_List(DataBaseBean SysBean, String Value)
//    {
//        ArrayList arraySysData = new ArrayList();
//        // DataBaseBean SysBean = new DataBaseBean();
//
//        StringBuffer Sql =new StringBuffer(  "SELECT * FROM SYS_PARM_LIST ");
//        if (Value.length() > 0)
//        {
//           Sql.append( " WHERE PARM_ID LIKE '"+ Value + "%'");
//        }
//        // System.out.println("Sql=" + Sql);
//
//        try
//        {
//            arraySysData = (ArrayList) SysBean.executeSQL(Sql.toString(), "select");
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//        }
//
//        if (arraySysData == null)
//        {
//            arraySysData = new ArrayList();
//        }
//
//        return arraySysData;
//    }

    /**
     * �̫��O��s 2008.01.03 shirley
     * @return boolean flag  �^�ǧ�s���G
     */

    public boolean update_Data(String command)
    {
        DataBaseBean2 sysBean = new DataBaseBean2();
        
        boolean flag = false;

    	ArrayList arraySys = new ArrayList();
    	try
    	{
    		/** 2023/05/24 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-087 */
    		arraySys = (ArrayList) sysBean.QuerySQLByParam(command);
    	}
    	catch (Exception ex)
    	{
    		System.out.println(ex.getMessage());
    	}

        System.out.println("arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
        	flag = true;
        }
        
        return flag;
    }

//    /* Override update_Data with DataBaseBean parameter */
//    public boolean update_Data(DataBaseBean SysBean, String command)
//    {
//        // DataBaseBean SysBean = new DataBaseBean();
//        boolean flag = false;
//
//        try
//        {
//            flag = ((Boolean) SysBean.executeSQL(SecurityTool.output(command), "update")).booleanValue();
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//        }
//
//        return flag;
//    }

    /**
     * ���ͥd�����\���
     * @param  String CardNo      ��l�d��
     * @param  int    StartPoint  *���_�l��m
     * @param  int    EndShowLen  ��ܧ��ƥd������
     * @return String CardNo_New  ���ͥd��
     */

    public String get_CardStar(String CardNo, int StartPoint, int EndShowLen)
    {
        String CardNo_New = "";

        if (CardNo.trim().length() >= (StartPoint+EndShowLen))
        {
            int StarLen = CardNo.trim().length() - StartPoint - EndShowLen + 1;
            CardNo_New = CardNo.trim().substring(0, StartPoint - 1);

            for (int i = 0; i < StarLen; ++i)
            {
                CardNo_New += "*";
            }
            //2014 Jason Fix ���o�d����X�X HotCode��2���D
            CardNo_New += CardNo.trim().substring(CardNo.trim().length() - EndShowLen);
        }
        else
        {
            CardNo_New = CardNo;
        }

        return CardNo_New;
    }

    /**
     * �զX�ϥΪ̰O����� 2008.01.04 Shirley Lin
     * @param  String MerchantID     �S���N��
     * @param  String UserName     �ϥΪ̦W��
     * @param  String FunctionName �\��W��
     * @param  String Status       ���浲�G
     * @param  String Memo         �Ƶ�
     * @return String Status       �զX���
     */

    public String get_LogData(String MerchantID, String UserName, String FunctionName, String Status, String Memo)
    {
        StringBuffer LogData = new StringBuffer();
        LogData.append( " ["+MerchantID+"]");
        LogData.append(  " ["+UserName+"]");
        LogData.append(  " ["+FunctionName+"]");
        LogData.append( " ["+Status+"]");
        LogData.append(  " ["+Memo+"]");

        return LogData.toString();
    }

    /**
     * @author nancywu
     *
     * To change this generated comment edit the template variable "typecomment":
     * Window>Preferences>Java>Templates.
     * To enable and disable the creation of type comments go to
     * Window>Preferences>Java>Code Generation.
     */

    public boolean check(String pan)
    {
        if (pan == null || pan.length() == 0 || pan.length() > 19 || pan.length() < 11)
            return false;

        int panid = Integer.parseInt(pan.substring(0, 1));
        String pan2 = pan.substring(0, 2);

        switch (panid)
        {
            case '4':
                cardType = "V";
                break;
            case '3':
                if (pan2.equals("34") || pan2.equals("37"))
                    cardType = "A";
                else
                    cardType = "J";

                break;
            case '5':
                cardType = "M";
                break;
            default:
                cardType = "U";
                break;
        }

        return true;
    }

    public String getCardType()
    {
        return cardType;
    }

    /**
     * @author nancywu
     *
     * To change this generated comment edit the template variable "typecomment":
     * Window>Preferences>Java>Templates.
     * To enable and disable the creation of type comments go to
     * Window>Preferences>Java>Code Generation.
     */
    //HPP error
    public String pack(MerchantAuthParam hppbean)
    {
        StringBuffer fwardp = new StringBuffer();
        fwardp.append("?MERCHANTID=" + hppbean.getMerchantID());
        fwardp.append("&TERMINALID=" + hppbean.getTerminalID());
        fwardp.append("&TRANSDATE=" + hppbean.getTransDate());
        fwardp.append("&TRANSTIME=" + hppbean.getTransTime());
        fwardp.append("&RESPONSECODE=" + hppbean.getResCode());
        fwardp.append("&RESPONSEMSG=" + hppbean.getResMsg());
        fwardp.append("&APPROVECODE=");
        fwardp.append("&ORDERID=" + hppbean.getOrderID());
        fwardp.append("&SYSORDERID=");
        fwardp.append("&BATCHNO=");
        fwardp.append("&TRANSTYPE=00");
        fwardp.append("&CURRENCYCODE=901");
        fwardp.append("&TRANSAMT=" + hppbean.getTransAmt());
        fwardp.append("&TRANSMODE=" + hppbean.getTransMode());

        return fwardp.toString();
    }

    //API error
    public String pack(ParamBean pbean)
    {
        StringBuffer fwardp = new StringBuffer();
        fwardp.append( "?MERCHANTID=" + pbean.getMerchantID());
        fwardp.append( "&TERMINALID=" + pbean.getTerminalID());
        fwardp.append( "&TRANSDATE=" + pbean.getTransDate());
        fwardp.append( "&TRANSTIME=" + pbean.getTransTime());
        fwardp.append( "&RESPONSECODE=" + pbean.getResponseCode());
        fwardp.append( "&RESPONSEMSG=" + pbean.getResponseMsg());
        fwardp.append( "&APPROVECODE=" + pbean.getApproveCode());
        fwardp.append( "&ORDERID=" + pbean.getOrderID());
        fwardp.append( "&SYSORDERID=");
        fwardp.append( "&BATCHNO=");
        fwardp.append( "&TRANSTYPE=00");
        fwardp.append( "&CURRENCYCODE=" + pbean.getCurrencyCode());
        fwardp.append( "&TRANSAMT=" + pbean.getTransAmt());
        fwardp.append( "&TRANSMODE=" + pbean.getTransMode());

        if (pbean.getTransMode() == 1)
        {
            fwardp.append( "&INSTALL=" + pbean.getInstallCount());
            fwardp.append( "&FIRSTAMT=" + pbean.getFirstAmt());
            fwardp.append( "&EACHAMT=" + pbean.getEachAmt());
            fwardp.append(  "&INSTALLTYPE=" + pbean.getInstallCount());
            fwardp.append( "&FEE=" + pbean.getFee());
        }
        else
        {
            if (pbean.getTransMode() == 2)
            {
            	fwardp.append( "&REDEM_TYPE=" + pbean.getRedemType());
            	fwardp.append( "&REDEM_USED=" + pbean.getRedemUsed());
            	fwardp.append( "&REDEM_BALANCE=" + pbean.getRedemBalance());
            	fwardp.append(  "&CREDITAMT=" + pbean.getCreditAmt());
            }
        }

        return fwardp.toString();
    }

    /**
     * ���Ͱө����A�X����
     * @return String CurrentMess  ���A����
     */
    public String get_CurrentCode(String Code)
    {
        String CurrentMess = "�S�����A���G";

        if (Code.equalsIgnoreCase("A"))
            CurrentMess += "�ݱҥ�";

        if (Code.equalsIgnoreCase("B"))
            CurrentMess += "�ҥ�";

        if (Code.equalsIgnoreCase("C"))
            CurrentMess += "Block�д�";

        if (Code.equalsIgnoreCase("D"))
            CurrentMess += "Block���v";

        if (Code.equalsIgnoreCase("E"))
            CurrentMess += "����";

        if (Code.equalsIgnoreCase("F"))
            CurrentMess += "�Ѭ�";

        return CurrentMess;
    }

//    /**
//     * ���o���եd�����(Test_Card)
//     * @param String Flag_Capture   �дڵ��O
//     * @param String Flag_Activate  �Ұʪ��A
//     * @return ArrayList            ���եd�����
//     */
//    public ArrayList get_Test_Card(String Flag_Capture, String Flag_Activate)
//    {
//        String tmpSql = "";
//        ArrayList arrayData = new ArrayList(); // ��ܴ��եd�����
//        DataBaseBean SysBean = new DataBaseBean();
//        String Sql = "SELECT * FROM TEST_CARD WHERE ";
//
//        if (Flag_Capture.length()>0)
//        {
//          tmpSql = " FLAG_CAPTURE = '"+Flag_Capture+"'";
//        }
//
//        if (tmpSql.length()>0)
//        {
//            tmpSql += " AND ";
//        }
//
//        if (Flag_Capture.length()>0)
//        {
//          tmpSql = tmpSql + " FLAG_ACTIVATE = '"+Flag_Activate+"'";
//        }
//
//        Sql += tmpSql;
//        // System.out.println("Sql=" + Sql);
//        try
//        {
//            arrayData = (ArrayList) SysBean.executeSQL(Sql, "select");
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//        }
//
//        if (arrayData == null)
//        {
//            arrayData = new ArrayList();
//        }
//
//        return arrayData;
//    }

    /* Override get_Test_Card with DataBaseBean parameter */
    public ArrayList get_Test_Card(DataBaseBean2 sysBean, String Flag_Capture, String Flag_Activate)
    {
        String tmpSql = "";
        ArrayList arrayData = new ArrayList(); // ��ܴ��եd�����
        // DataBaseBean SysBean = new DataBaseBean();
        StringBuffer sSQLSB = new StringBuffer();
		sysBean.ClearSQLParam();
		sSQLSB.append("SELECT * FROM TEST_CARD WHERE ");

        if (Flag_Capture.length()>0)
        {
        	sSQLSB.append(" FLAG_CAPTURE = ? ");
        	sysBean.AddSQLParam(emDataType.STR, Flag_Capture);
        }

        if (tmpSql.length()>0)
        {
        	sSQLSB.append(" AND ");
        }

        if (Flag_Capture.length()>0)
        {
        	sSQLSB.append(" FLAG_ACTIVATE = ? ");
        	sysBean.AddSQLParam(emDataType.STR, Flag_Activate);
        }
        
        // System.out.println("Sql=" + Sql);
        try
        {
        	/** 2023/04/26 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-012 */
            arrayData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        if (arrayData == null)
        {
            arrayData = new ArrayList();
        }

        return arrayData;
    }

    /**
     * �T�{�d���O�_�����եd
     * @param ArrayList arrayTestCardData  ���եd�D��
     * @param String    PAN                �d��
     * @return Boolean  Flag               �P�_���G
     */
    public boolean check_Test_Card(ArrayList arrayTestCardData, String PAN)
    {
        boolean boolFlag = true;

        if (arrayTestCardData.size() > 0 && PAN.length() > 0 )
        {
            for (int i=0; i<arrayTestCardData.size(); i++)
            {
                Hashtable hashData = (Hashtable) arrayTestCardData.get(i);

                if (hashData.size()>0)
                {
                    String Pan_Begin = hashData.get("PAN_BEGIN").toString();
                    String Pan_End = hashData.get("PAN_END").toString();

                    if (PAN.compareTo(Pan_Begin) >= 0 && PAN.compareTo(Pan_End) <= 0)
                    {
                        boolFlag = false;
                        break;
                    }
                }
            }
        }

        return boolFlag;
    }

//    /**
//    * ���oMSGCODE���(msgcode)
//    * @param String Function_name  �\��W��
//    * @return ArrayList            �d�ߵ��G���
//    */
//    public ArrayList get_Msgcode(String Function_name)
//    {
//        ArrayList arrayMsgcodeData = new ArrayList(); // ��ܭq�������
//        DataBaseBean SysBean = new DataBaseBean();
//        StringBuffer Sql =new StringBuffer(  "SELECT * FROM MSGCODE ");
//
//        if (Function_name.length() > 0)
//        {
//            Sql.append( " WHERE FUNCTION_NAME = '"+Function_name+"' ");
//        }
//        Sql.append( " ORDER BY ROWID ");
//        // System.out.println("Sql=" + Sql);
//
//        try
//        {
//            arrayMsgcodeData = (ArrayList) SysBean.executeSQL(Sql.toString(), "select");
//        }
//        catch (Exception ex) 
//        {
//            System.out.println(ex.getMessage());
//        }
//        
//        if (arrayMsgcodeData == null) 
//        {
//            arrayMsgcodeData = new ArrayList();
//        }
//        
//        return arrayMsgcodeData;
//    }
    
    /* Override get_Msgcode with DataBaseBean parameter */
    public ArrayList get_Msgcode(DataBaseBean2 sysBean, String Function_name)
    {
        ArrayList arrayMsgcodeData = new ArrayList(); // ��ܭq�������
        // DataBaseBean SysBean = new DataBaseBean();
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("SELECT * FROM MSGCODE ");

        if (Function_name.length() > 0)
        {
            sSQLSB.append(" WHERE FUNCTION_NAME = ? ");
            sysBean.AddSQLParam(emDataType.STR, Function_name);
        }
        sSQLSB.append(" ORDER BY ROWID ");
        // System.out.println("Sql=" + Sql);

        try
        {
        	/** 2023/05/16 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-075 */
            arrayMsgcodeData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        if (arrayMsgcodeData == null)
        {
            arrayMsgcodeData = new ArrayList();
        }

        return arrayMsgcodeData;
    }
    
    public Hashtable get_FtpHostInfo(String EXTERNAL_SYSID)
    {
    	DataBaseBean2 sysBean = new DataBaseBean2();
    	
        ArrayList arraylistFtpHostInfo = new ArrayList(); // ��ܭq�������
        Hashtable hashFtpHostInfo = new Hashtable();
        

        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append(" WHERE EXTERNAL_SYSID = ? "); 
		sysBean.AddSQLParam(emDataType.STR, EXTERNAL_SYSID);
		
            try
            {
            	/** 2023/05/24 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-088 */
            	arraylistFtpHostInfo = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
            	if(arraylistFtpHostInfo.size()>=1){
            	hashFtpHostInfo=(Hashtable) arraylistFtpHostInfo.get(0);
            	}
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
            }

        return hashFtpHostInfo;
    }
}
