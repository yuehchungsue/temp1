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
 * 202112300619-01 20220210 GARY 請款批次請款規格增修(Visa Authorization Source Code) AUTH_SRC_CODE
 * ***/
public class UserBean
{
    private String cardType;
    private DataBaseBean2 resultBean = new DataBaseBean2();

    public UserBean()
    {
    }

    /**
     * *確認特店權限及狀態
     * @param Hashtable hashMerchant  特店資料
     * @param String    Column        欄位名稱
     * @param String    Value         判斷值
     * @return Boolean  Flag          確認結果
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
                        // 開放權限
                        boolFlag = true;
                        break;
                    }
                }
            }
        }

        return boolFlag;
    }

    /**
     * 確認端末機權限及狀態
     * @param String    MerchantID    特店代號
     * @param String    TerminalID    端末機代號
     * @param ArrayList arrayTerminal 端末機資料
     * @param String    Column        欄位名稱
     * @param String    Value         判斷值
     * @return Boolean  Flag          確認結果
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
                                // 開放權限
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
//     * 取得帳單交易資料(Billing)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店代號
//     * @param String OrderType      查詢訂單類別 (Order:特店指定單號 System:系統指定單號 )
//     * @param String OrderID        訂單代號
//     * @param String TransCode      交易代碼
//     * @return ArrayList            帳單交易
//     */
//    public ArrayList get_Billing(String MerchantID, String SubMID, String OrderType, String OrderID, String TransCode)
//    {
//        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
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
//                // 以OrderID
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
        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
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
                // 以OrderID
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
            	/** 2023/05/04 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-027 */
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
//     * 取得帳單交易資料歷程(Billing)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店代號
//     * @param String OrderType      查詢訂單類別 (Order:特店指定單號 System:系統指定單號 )
//     * @param String OrderID        訂單代號
//     * @param String TransCode      交易代碼
//     * @return ArrayList            帳單交易
//     */
//    public ArrayList get_BillingHistory(String MerchantID, String SubMID, String OrderType, String OrderID, String TransCode)
//    {
//        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
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
//                // 以OrderID
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
        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
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
                // 以OrderID
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
            	/** 2023/05/10 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-034 */
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
//     * 取得帳單檢核資料(Balance)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         服務代號
//     * @param String OrderID        訂單代號
//     * @return Hashtable            帳單檢核資料
//     */
//    public Hashtable get_Balance(String MerchantID, String SubMID, String OrderID)
//    {
//        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
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
        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
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
            	/** 2023/04/27 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-015 */
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
//     * 取得CUP清分暫存檔(CUP115)
//     * Added by Dale Peng 20150513
//     * Merchant Console              線上退貨作業模組
//     * @param DataBaseBean SysBean  資料庫
//     * @param String MerchantID     特店代號
//     * @param String SubMID         服務代號
//     * @param String OrderID        訂單代號
//     * @return Hashtable            CUP清分暫存檔資料
//     */
//    public Hashtable get_CUP115(DataBaseBean SysBean, String MerchantID, String SubMID, String OrderID) 
//    {
//    	ArrayList arrayCUP115Data = new ArrayList(); // 顯示CUP115資料
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
     * 依指定日期格式產生今天日期
     * @param String Type      指定回傳日期格式
     * @return String NowTime  回傳日期
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
     * 產生指定天數日期
     * @param String Type      指定回傳日期格式
     * @param int AppointDay   指定天數
     * @return String NowTime  指定天數產生日期
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
     * 資料前後去空白
     * @return String Data  回傳資料
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
     * 新增授權交易資料(AuthLog)
     * @param  DataBaseBean SysBean     資料庫
     * @param  String MerchantID    特店代號
     * @param  String SubMID            服務代號
     * @param  String TerminalID    端末機代號
     * @param  String AcquirerID    收單行代號
     * @param  String OrderID           特店訂單編號
     * @param  String Sys_OrderID   系統指定單號
     * @param  String Card_Type     卡別
     * @param  String PAN           卡號
     * @param  String ExtenNo           CVV2/CVC2
     * @param  String ExpireDate    有效期限
     * @param  String TransCode         交易代碼
     * @param  String ReversalFlag  Auto-Reversal旗標
     * @param  String TransDate         授權日期
     * @param  String TransTime     授權時間
     * @param  String CurrencyCode  授權幣別
     * @param  String TransAmt          授權金額
     * @param  String Trans_Status  交易狀態
     * @param  String ApproveCode   授權碼
     * @param  String ResponseCode  回應碼
     * @param  String ResponseMsg   回應訊息
     * @param  String Entry_Mode    Entry Mode
     * @param  String Condition_Code    Condition Code
     * @param  String BatchNo           授權批次號碼
     * @param  String UserDefine    商店自訂訊息
     * @param  String Direction         記錄
     * @param  String EMail         持卡人 eMail
     * @param  String MTI               授權 MTI
     * @param  String RRN           授權 RRN
     * @param  String SocialID          身份證字號
     * @param  String TransMode         交易模式
     * @param  String TransType     交易機制
     * @param  String ECI           ECI
     * @param  String CAVV          3D Secure PARes.cavv
     * @param  String XID           3D Secure PARes.xid
     * @param  String InstallType   分期手續費計價旗標
     * @param  String Install           分期期數
     * @param  String FirstAmt          首期金額
     * @param  String EachAmt           每期金額
     * @param  String FEE           手續費
     * @param  String RedemType         紅利折抵旗標
     * @param  String RedemUsed     紅利折抵點數
     * @param  String RedemBalance  紅利餘額
     * @param  String CreditAmt     卡人自符額
     * @param  String BillMessage   帳單訊息
     * @param  String SysTraceNo    gateway回覆的traceno
     * @return boolean                  新增結果
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
        	/** 2023/05/03 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-028 */
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
     * 新增授權交易資料FOR CUP (AUTHLOG_CUP)
     * Added by Jimmy Kang 20150511
     * Merchant Console 線上取消作業模組
     * @param  DataBaseBean SysBean     資料庫
     * @param  String MerchantID        特店代號
     * @param  String SubMID            服務代號
     * @param  String Sys_OrderID       系統指定單號
     * @param  String TransCode         交易代碼
     * @param  String TransDate         授權日期(respTime)
     * @param  String TransTime         授權時間(respTime)
     * @param  String Trace_Time        系統跟蹤時間(traceTime)
     * @param  String Settle_Amount     清算金額(settleAmount)
     * @param  String Settle_Currency   授權幣別(settleCurrency)
     * @param  String Settle_Date       清算日期(settleDate)
     * @param  String Exchange_Rate     清算匯率(exchangeRate)
     * @param  String Exchange_Date     清算匯率換算日(exchangeDate)
     * @param  String Cup_Paymode       支付方式(cupReserved的payMode)
     * @param  String Cup_TransCode     交易代碼(transType)
     * @param  String Trans_Status      交易狀態
     * @param  String ResponseCode      回應碼(respCode)
     * @param  String ResponseMsg       回應訊息(respMsg)
     * @param  String SysTraceNo        系統跟蹤號(traceNumber)
     * @param  String Cup_QID           交易流水號(qid)
     * @param  String Cup_Reserved      系統保留域(cupReserved)
     * @param  String Direction         記錄 (Record) 類別
     * @param  String Cps_Refresh
     * @param  String Filestatus_Refkey 檔案處理狀態檔
     * @param  String Notifyurl         特店指定url
     * @param  String Notifyparam       特店指定url後面的特定參數
     * @param  String Notifytype        特店類型
     * @param  String Retry_Cnt         Retry次數
     * @param  String PAN               卡號
     * @param  String TransAmt          授權金額
     * @param  String Cup_Notifypage    CUP授權完成後台通知頁面
     * @param  String Cup_Notifyparam   CUP授權完成後台通知頁面參數
     * @return boolean                  新增結果
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
    		/** 2023/05/04 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-029 */
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
     * 新增帳單交易資料(Billing)
     * @param  DataBaseBean SysBean     資料庫
     * @param  String MerchantID    特店代號
     * @param  String SubMID            服務代號
     * @param  String TerminalID    端末機代號
     * @param  String AcquirerID    收單行代號
     * @param  String OrderID           特店訂單編號
     * @param  String Sys_OrderID   系統指定單號
     * @param  String Card_Type     卡別
     * @param  String PAN           卡號
     * @param  String ExtenNo       CVV2/CVC2
     * @param  String ExpireDate    有效期限
     * @param  String TransCode         交易代碼
     * @param  String ReversalFlag  Auto-Reversal 旗標
     * @param  String TransDate         授權日期
     * @param  String TransTime         授權時間
     * @param  String CurrencyCode  授權幣別
     * @param  String TransAmt          請款金額
     * @param  String ApproveCode   授權碼
     * @param  String ResponseCode  回應碼
     * @param  String ResponseMsg   回應訊息
     * @param  String BatchNo           授權批次號碼
     * @param  String UserDefine    商店自訂訊息
     * @param  String EMail         持卡人 eMail
     * @param  String MTI           授權 MTI
     * @param  String RRN           授權 RRN
     * @param  String SocialID          身份證字號
     * @param  String Entry_Mode    Entry Mode
     * @param  String Condition_Code    Condition Code
     * @param  String TransMode     交易模式
     * @param  String TransType     交易機制
     * @param  String ECI           ECI
     * @param  String CAVV          3D Secure PARes.cavv
     * @param  String XID           3D Secure PARes.xid
     * @param  String InstallType   分期手續費計價旗標
     * @param  String Install           分期期數
     * @param  String FirstAmt          首期金額
     * @param  String EachAmt       每期金額
     * @param  String FEE           手續費
     * @param  String RedemType         紅利折抵旗標
     * @param  String RedemUsed         紅利折抵點數
     * @param  String RedemBalance  紅利餘額
     * @param  String CreditAmt         卡人自付額
     * @param  String BillMessage   帳單訊息
     * @param  String BalanceAmt    可請款餘額
     * @param  String SysTraceNo    gateway回覆的traceno
     * @return boolean                  新增結果
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
        	/** 2023/05/08 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-030 */
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
     * 更新帳單檢核單資料(Balance)
     * @param  DataBaseBean SysBean     資料庫
     * @param  String MerchantID    特店代號
     * @param  String SubMID            服務代號
     * @param  String OrderID           特店訂單編號
     * @param  String RefundAmt         退貨總額
     * @param  String RefundDate    退貨日期
     * @param  String CaptureAmt    請款總額
     * @param  String CaptureDate   請款日期
     * @param  String CancelAmt         取消總額
     * @param  String CancelDate    取消日期
     * @param  String RefundCaptureAmt  退貨請款總額
     * @param  String RefundCaptureDate 退貨請款日期
     * @param  String BalanceAmt    可請款餘額
     * @return boolean                  更新結果
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
        	/** 2023/04/28 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-016 */
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
     * 新增授權交易資料(SAF)
     * @param  DataBaseBean SysBean     資料庫
     * @param  String MerchantID    特店代號
     * @param  String SubMID            服務代號
     * @param  String TerminalID    端末機代號
     * @param  String AcquirerID    收單行代號
     * @param  String OrderID           特店訂單編號
     * @param  String Sys_OrderID   系統指定單號
     * @param  String Card_Type     卡別
     * @param  String PAN           卡號
     * @param  String ExtenNo           CVV2/CVC2
     * @param  String ExpireDate    有效期限
     * @param  String TransCode         交易代碼
     * @param  String ReversalFlag  Auto-Reversal旗標
     * @param  String TransDate         授權日期
     * @param  String TransTime     授權時間
     * @param  String CurrencyCode  授權幣別
     * @param  String TransAmt          授權金額
     * @param  String ApproveCode   授權碼
     * @param  String ResponseCode  回應碼
     * @param  String ResponseMsg   回應訊息
     * @param  String Entry_Mode    Entry Mode
     * @param  String Condition_Code    Condition Code
     * @param  String BatchNo           授權批次號碼
     * @param  String UserDefine    商店自訂訊息
     * @param  String EMail         持卡人 eMail
     * @param  String MTI               授權 MTI
     * @param  String RRN           授權 RRN
     * @param  String SocialID          身份證字號
     * @param  String TransMode         交易模式
     * @param  String TransType     交易機制
     * @param  String ECI           ECI
     * @param  String CAVV          3D Secure PARes.cavv
     * @param  String XID           3D Secure PARes.xid
     * @param  String InstallType   分期手續費計價旗標
     * @param  String Install           分期期數
     * @param  String FirstAmt          首期金額
     * @param  String EachAmt           每期金額
     * @param  String FEE           手續費
     * @param  String RedemType         紅利折抵旗標
     * @param  String RedemUsed     紅利折抵點數
     * @param  String RedemBalance  紅利餘額
     * @param  String CreditAmt     卡人自符額
     * @param  String Status        處理狀態
     * @param  String Resp_Date SAF     授權回覆日期
     * @param  String Resp_Time SAF     授權回覆時間
     * @param  String Resp_ResponseCode SAF 授權回應碼
     * @param  String Resp_ApproveCode  SAF 授權授權碼
     * @param  String Resp_ResponseMsg  SAF 授權回應訊息
     * @param  String SysTraceNo    gateway回覆的traceno
     * @return boolean                  新增結果
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
        	/** 2023/05/09 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-032 */
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
     * 新增授權交易資料FOR CUP (SAF_CUP)
     * Added by Jimmy Kang 20150511
     * Merchant Console 線上取消作業模組
     * @param  DataBaseBean SysBean     資料庫
     * @param  String MerchantID        特店代號
     * @param  String SubMID            服務代號
     * @param  String TerminalID        端末機代號
     * @param  String AcquirerID        收單行代號
     * @param  String OrderID           特店訂單編號
     * @param  String Sys_OrderID       系統指定單號
     * @param  String Card_Type         卡別
     * @param  String PAN               卡號
     * @param  String ExtenNo           CVV2/CVC2
     * @param  String ExpireDate        有效期限
     * @param  String TransCode         交易代碼
     * @param  String ReversalFlag      Auto-Reversal旗標
     * @param  String TransDate         授權日期
     * @param  String TransTime         授權時間
     * @param  String CurrencyCode      授權幣別
     * @param  String TransAmt          授權金額
     * @param  String ApproveCode       授權碼
     * @param  String ResponseCode      回應碼
     * @param  String ResponseMsg       回應訊息
     * @param  String Entry_Mode        Entry Mode
     * @param  String Condition_Code    Condition Code
     * @param  String BatchNo           授權批次號碼
     * @param  String UserDefine        商店自訂訊息
     * @param  String EMail             持卡人 eMail
     * @param  String MTI               授權 MTI
     * @param  String RRN               授權 RRN
     * @param  String SocialID          身份證字號
     * @param  String Status            處理狀態
     * @param  String Resp_Date         SAF 授權回覆日期
     * @param  String Resp_Time         SAF 授權回覆時間
     * @param  String Resp_ResponseCode SAF 授權回應碼
     * @param  String Resp_ApproveCode  SAF 授權授權碼
     * @param  String Resp_ResponseMsg  SAF 授權回應訊息
     * @param  String SysTraceNo        System Trace No
     * @param  String Retry_Cnt         Retry次數
     * @param  String ECI               ECI
     * @param  String TransType         交易機制
     * @param  String TransMode         交易模式
     * @return boolean                  新增結果
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
        	/** 2023/05/08 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-031 */
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
     * 新增請款交易資料(Capture)
     * @param  DataBaseBean SysBean     資料庫
     * @param  String MerchantID    特店代號
     * @param  String SubMID            服務代號
     * @param  String TerminalID    端末機代號
     * @param  String AcquirerID    收單行代號
     * @param  String OrderID           特店訂單編號
     * @param  String Sys_OrderID   系統指定單號
     * @param  String Card_Type     卡別
     * @param  String PAN           卡號
     * @param  String ExpireDate    有效期限
     * @param  String TransCode         交易代碼
     * @param  String TransDate         授權日期
     * @param  String TransTime     授權時間
     * @param  String ApproveCode   授權碼
     * @param  String ResponseCode  回應碼
     * @param  String ResponseMsg   回應訊息
     * @param  String CurrencyCode  授權幣別
     * @param  String CaptureAmt        請款金額
     * @param  String CaptureDate       請款日期
     * @param  String UserDefine    商店自訂訊息
     * @param  String BatchNo           授權批次號碼
     * @param  String CaptureFlag       請款控制旗標
     * @param  String ProcessDate       請款處理日
     * @param  String Entry_Mode        Entry_Mode
     * @param  String Condition_Code    Condition_Code
     * @param  String ECI           ECI
     * @param  String CAVV          3D Secure PARes.cavv
     * @param  String TransMode         交易模式
     * @param  String InstallType   分期手續費計價旗標
     * @param  String Install           分期期數
     * @param  String FirstAmt          首期金額
     * @param  String EachAmt           每期金額
     * @param  String FEE           手續費
     * @param  String RedemType         紅利折抵旗標
     * @param  String RedemUsed     紅利折抵點數
     * @param  String RedemBalance  紅利餘額
     * @param  String CreditAmt     卡人自符額
     * @param  String BillMessage   帳單訊息
     * @param  String FeeBackCode       請款回覆碼
     * @param  String FeeBackMsg        請款回覆訊息
     * @param  String FeeBackDate       請款回覆日期
     * @param  String DueDate           請款期限
     * @param  String TransAmt          原請款金額
     * @param  String SysTraceNo    gateway回覆的traceno
     * @param  String ExtenNo           ExtenNo
     * @param  String RRN           RRN
     * @param  String MTI           MTI
     * @param  String XID           XID
     * @param  String SocialID          SocialID
     * @param  String AUTH_SRC_CODE     VISA AUTH_SRC_CODE
     * @return boolean                  新增結果
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
			/** 2023/04/28 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-018 */
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
     * 新增帳單交易資料(Batch)
     * @param  DataBaseBean SysBean     資料庫
     * @param  String MerchantID    特店代號
     * @param  String SubMID            服務代號
     * @param  String TerminalID    端末機代號
     * @param  String AcquirerID    收單行代號
     * @param  String OrderID           特店訂單編號
     * @param  String Sys_OrderID   系統指定單號
     * @param  String Card_Type     卡別
     * @param  String PAN           卡號
     * @param  String ExtenNo       CVV2/CVC2
     * @param  String ExpireDate    有效期限
     * @param  String TransCode         交易代碼
     * @param  String ReversalFlag  Auto-Reversal 旗標
     * @param  String TransDate         授權日期
     * @param  String TransTime         授權時間
     * @param  String CurrencyCode  授權幣別
     * @param  String TransAmt          請款金額
     * @param  String ApproveCode   授權碼
     * @param  String ResponseCode  回應碼
     * @param  String ResponseMsg   回應訊息
     * @param  String BatchNo           授權批次號碼
     * @param  String UserDefine    商店自訂訊息
     * @param  String EMail         持卡人 eMail
     * @param  String MTI           授權 MTI
     * @param  String RRN           授權 RRN
     * @param  String SocialID          身份證字號
     * @param  String Entry_Mode    Entry Mode
     * @param  String Condition_Code    Condition Code
     * @param  String TransMode     交易模式
     * @param  String TransType     交易機制
     * @param  String ECI           ECI
     * @param  String CAVV          3D Secure PARes.cavv
     * @param  String XID           3D Secure PARes.xid
     * @param  String InstallType   分期手續費計價旗標
     * @param  String Install           分期期數
     * @param  String FirstAmt          首期金額
     * @param  String EachAmt       每期金額
     * @param  String FEE           手續費
     * @param  String RedemType         紅利折抵旗標
     * @param  String RedemUsed         紅利折抵點數
     * @param  String RedemBalance  紅利餘額
     * @param  String CreditAmt         卡人自付額
     * @param  String BillMessage   帳單訊息
     * @param  String BalanceAmt    可請款餘額
     * @param  String CaptureAmt        請款金額
     * @param  String CaptureDate       請款日期
     * @param  String FeedbackCode  請款處理回覆訊息
     * @param  String FeedbackMsg       請款處理回覆訊息說明
     * @param  String FeedbackDate  請款處理日期
     * @param  String Due_Date          請款到期日
     * @param  String BatchPmtid        批次交易序號
     * @param  String BatchDate         批次交易日期
     * @param  String BatchHead         批次交易表頭
     * @param  String BatchType         批次交易類別
     * @param  String BatchTerminalID   批次交易端末機代號
     * @param  String BatchSysorderID   批次交易特店訂單編號
     * @param  String BatchTxDate       批次原交易日期
     * @param  String BatchTxTime       批次原交易時間
     * @param  String BatchTxApprovecode批次交易授權碼
     * @param  String BatchTransCode    批次交易交易類別
     * @param  String BatchTxAmt        批次交易金額
     * @param  String BatchTxMsg        批次交易處理回覆
     * @param  String BatchResponse     批次交易處理回覆說明
     * @param  String SysTraceNo    gateway回覆的traceno
     * @param  String Capture_RowID 原請款ROWID
     * @param  String AUTH_SRC_CODE     VISA AUTH_SRC_CODE
     * @return boolean                  新增結果
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
        	/** 2023/04/26 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-020 */
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
//     * 取得批次交易資料(Batch)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店代號
//     * @param String BatchPmtID     批次序號
//     * @param String BatchTxMsg     批次處理狀態
//     * @param String OrderBy        排序方式
//     * @return ArrayList            帳單交易
//     */
//    public ArrayList get_Batch(String MerchantID, String SubMID,
//                               String BatchPmtID, String BatchTxMsg,
//                               String OrderBy)
//    {
//        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
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
        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
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
            	/** 2023/05/03 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-026 */
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
//     * 更新批次處理結果(Batch)
//     * @param  String BatchTxMsg    批次處理結果
//     * @param  String BatchTxResponse   批次處理結果說明
//     * @param  String RowID         批次更新ID
//     * @return boolean                  更新結果
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
        	/** 2023/05/17 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-093 (No Need Test) */
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
//     * 取得批次交易資料(Batch)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店代號
//     * @param String BatchPmtID     批次序號
//     * @param String BatchTxMsg     批次處理狀態
//     * @param String OrderBy        排序方式
//     * @param boolean Flag          是否為ROWDATA
//     * @return ResultSet            帳單交易
//     */
//    public ResultSet get_Batch_Result(String MerchantID, String SubMID, String BatchPmtID, String BatchTxMsg, String OrderBy, boolean Flag)
//    {
//        ResultBean = new DataBaseBean();
//        StringBuffer Sql =new StringBuffer( );
//
//        if (Flag)
//        {  // 為rowdata
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
        {  // 為rowdata
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
        
        /** 2023/05/02 組 SQL (By : YC) *//** Test Case : IT-TESTCASE-021 */

        return sSQLSB.toString();
    }

//    /**
//     * 取得基本參數資料(Sys_Parm_List) 2007.12.26
//     * @param String Value     查詢條件
//     * @return ArrayList       查詢參數資料
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
     * 依指令更新 2008.01.03 shirley
     * @return boolean flag  回傳更新結果
     */

    public boolean update_Data(String command)
    {
        DataBaseBean2 sysBean = new DataBaseBean2();
        
        boolean flag = false;

    	ArrayList arraySys = new ArrayList();
    	try
    	{
    		/** 2023/05/24 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-087 */
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
     * 產生卡號掩蓋資料
     * @param  String CardNo      原始卡號
     * @param  int    StartPoint  *號起始位置
     * @param  int    EndShowLen  顯示尾數卡號長度
     * @return String CardNo_New  產生卡號
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
            //2014 Jason Fix 取得卡號後X碼 HotCode為2問題
            CardNo_New += CardNo.trim().substring(CardNo.trim().length() - EndShowLen);
        }
        else
        {
            CardNo_New = CardNo;
        }

        return CardNo_New;
    }

    /**
     * 組合使用者記錄資料 2008.01.04 Shirley Lin
     * @param  String MerchantID     特店代號
     * @param  String UserName     使用者名稱
     * @param  String FunctionName 功能名稱
     * @param  String Status       執行結果
     * @param  String Memo         備註
     * @return String Status       組合資料
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
     * 產生商店狀態碼說明
     * @return String CurrentMess  狀態說明
     */
    public String get_CurrentCode(String Code)
    {
        String CurrentMess = "特店狀態為：";

        if (Code.equalsIgnoreCase("A"))
            CurrentMess += "待啟用";

        if (Code.equalsIgnoreCase("B"))
            CurrentMess += "啟用";

        if (Code.equalsIgnoreCase("C"))
            CurrentMess += "Block請款";

        if (Code.equalsIgnoreCase("D"))
            CurrentMess += "Block授權";

        if (Code.equalsIgnoreCase("E"))
            CurrentMess += "停用";

        if (Code.equalsIgnoreCase("F"))
            CurrentMess += "解約";

        return CurrentMess;
    }

//    /**
//     * 取得測試卡片資料(Test_Card)
//     * @param String Flag_Capture   請款註記
//     * @param String Flag_Activate  啟動狀態
//     * @return ArrayList            測試卡片資料
//     */
//    public ArrayList get_Test_Card(String Flag_Capture, String Flag_Activate)
//    {
//        String tmpSql = "";
//        ArrayList arrayData = new ArrayList(); // 顯示測試卡片資料
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
        ArrayList arrayData = new ArrayList(); // 顯示測試卡片資料
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
        	/** 2023/04/26 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-012 */
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
     * 確認卡片是否為測試卡
     * @param ArrayList arrayTestCardData  測試卡主檔
     * @param String    PAN                卡號
     * @return Boolean  Flag               判斷結果
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
//    * 取得MSGCODE資料(msgcode)
//    * @param String Function_name  功能名稱
//    * @return ArrayList            查詢結果資料
//    */
//    public ArrayList get_Msgcode(String Function_name)
//    {
//        ArrayList arrayMsgcodeData = new ArrayList(); // 顯示訂單交易資料
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
        ArrayList arrayMsgcodeData = new ArrayList(); // 顯示訂單交易資料
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
        	/** 2023/05/16 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-075 */
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
    	
        ArrayList arraylistFtpHostInfo = new ArrayList(); // 顯示訂單交易資料
        Hashtable hashFtpHostInfo = new Hashtable();
        

        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append(" WHERE EXTERNAL_SYSID = ? "); 
		sysBean.AddSQLParam(emDataType.STR, EXTERNAL_SYSID);
		
            try
            {
            	/** 2023/05/24 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-088 */
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
