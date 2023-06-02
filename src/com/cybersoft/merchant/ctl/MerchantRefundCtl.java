/************************************************************
 * <p>#File Name:	MerchantRefundQueryCtl.java	</p>
 * <p>#Description:	            			</p>
 * <p>#Create Date:	2007/09/26		        </p>
 * <p>#Company:  	cybersoft	        	</p>
 * <p>#Notice:   					</p>
 * @author		Shirley Lin
 * @since		SPEC version
 * @version	0.1	2007/09/26	Shirley Lin
 * 異動說明
 * 20200306 202002100619-00 saint EC退貨檢核
 * 202112300619-01 20220210 GARY 請款批次請款規格增修(Visa Authorization Source Code) AUTH_SRC_CODE
 ************************************************************/
package com.cybersoft.merchant.ctl;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft.bean.UserBean;
import com.cybersoft.bean.SessionControlBean;
import com.cybersoft.merchant.bean.MerchantRefundBean;
import com.cybersoft.merchant.bean.MerchantLoginBean;
import com.cybersoft.bean.LogUtils;
/**
 * <p>控制退貨的Servlet</p>
 * @version	0.1	2007/09/26	Shiley Lin
 */
public class MerchantRefundCtl extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "text/html; charset=Big5";
    public static final LogUtils log_user = new LogUtils("user");
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    private UserBean UserBean = new UserBean();
    private DataBaseBean2 sysBean = new DataBaseBean2();

    public void init()
    {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        this.doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String Forward = ""; // 網頁轉址
        String Message = ""; // 顯示訊息
        String LogUserName = "--";
        String LogFunctionName = "線上退貨作業";
        String LogStatus = "失敗";
        String LogMemo = "";
        String LogData = "";
        String LogMerchantID = "--";

        response.setContentType(CONTENT_TYPE);
        request.setCharacterEncoding("BIG5");

        Hashtable hashSys = new Hashtable(); // 系統參數
        Hashtable hashMerUser = new Hashtable(); // 特店使用者
        Hashtable hashMerchant = new Hashtable(); // 特店主檔
        ArrayList arrayTerminal = new ArrayList(); // 端末機主檔
        Hashtable hashConfData = new Hashtable();

        HttpSession session = request.getSession(true);
        SessionControlBean scb = new SessionControlBean();
        try
        {
            scb = new SessionControlBean(session, request, response);
            sysBean.setAutoCommit(false);
        }
        catch (UnsupportedOperationException E)
        {
            E.toString();
            request.getRequestDispatcher("/Merchant_Bye.jsp").forward(request, response);
            return;
        }

        try
        {
            String LogOrderID = "";
            hashConfData = (Hashtable) session.getAttribute("SYSCONFDATA");
            if (hashConfData == null)
                hashConfData = new Hashtable();

            boolean Merchant_Current = false; // 特店狀態
            boolean Merchant_Permit = false; // 特店權限
            boolean Terminal_Current = false; // 端末機狀態
            boolean Terminal_Permit = false; // 端末機權限
            String Action = (String) request.getParameter("Action");
            if (Action == null)
                Action = "";

            if (hashConfData.size() > 0)
            {
                hashSys = (Hashtable) hashConfData.get("SYSCONF"); // 系統參數
                if (hashSys == null)
                    hashSys = new Hashtable();

                System.out.println("hashSys="+hashSys);
                hashMerUser = (Hashtable) hashConfData.get("MERCHANT_USER"); // 特店使用者
                if (hashMerUser == null)
                    hashMerUser = new Hashtable();

                System.out.println("hashMerUser="+hashMerUser);
                if (Action.length()==0)
                {
                    // 重新更新資料
                   String MerchantID = hashMerUser.get("MERCHANT_ID").toString();
                   String UserID = hashMerUser.get("USER_ID").toString();
                   MerchantLoginBean LoginBean = new MerchantLoginBean();
//                   Hashtable hashtmpMerchant = LoginBean.get_Merchant(SysBean, MerchantID); //特店主檔
//                   if (hashtmpMerchant !=null && hashtmpMerchant.size() >0 )
//                   {
//                       hashConfData.put("MERCHANT",hashtmpMerchant);
//                   }

                   if (hashMerchant.size()>0)
                   {
                       LogMerchantID = (String)hashMerUser.get("MERCHANT_ID");
                   }

                   ArrayList<Hashtable<String,String>> arraytmpTerminal = LoginBean.get_Terminal(MerchantID); // 端末機主檔
                   if (arraytmpTerminal !=null && arraytmpTerminal.size() >0 )
                   {
                       hashConfData.put("TERMINAL", arraytmpTerminal );
                   }

//                   Hashtable hashtmpMerUser = LoginBean.get_Merchant_User(SysBean, MerchantID, UserID);
//                   if (hashtmpMerUser !=null && hashtmpMerUser.size() >0 )
//                   {
//                       hashConfData.put("MERCHANT_USER", hashtmpMerUser );
//                       hashMerUser = hashtmpMerUser;
//                   }

                   session.setAttribute("SYSCONFDATA", hashConfData);
                }

                hashMerchant = (Hashtable) hashConfData.get("MERCHANT"); // 特店主檔
                if (hashMerchant == null)
                    hashMerchant = new Hashtable();

                arrayTerminal = (ArrayList) hashConfData.get("TERMINAL"); // 端末機主檔
                if (arrayTerminal == null)
                    arrayTerminal = new ArrayList();
            }

            // UserBean UserBean = new UserBean();
            Merchant_Current = UserBean.check_Merchant_Column(hashMerchant, "CURRENTCODE", "B,D"); //  確認特店狀態
            Merchant_Permit = UserBean.check_Merchant_Column(hashMerchant, "PERMIT_REFUND", "Y"); //  確認特店權限

            String MenuKey = (String) request.getParameter(MerchantMenuCtl.MENU_FORWARD_KEY);
            request.setAttribute(MerchantMenuCtl.MENU_FORWARD_KEY, MenuKey);
            boolean User_Permit = MerchantMenuCtl.getUserRole(sysBean, session, MenuKey);

            if (User_Permit)
            {
                // 使用者權限
                if (Merchant_Current)
                {
                    //特店狀態
                    if (Merchant_Permit)
                    {
                        // 特店權限
                        if (Action.length() == 0)
                        {
                            //直接轉到查詢畫頁
                            Forward = "./Merchant_Refund_Query.jsp";
                        }
                        else
                        {
                            MerchantRefundBean RefundBean = new MerchantRefundBean();
                            if (Action.equalsIgnoreCase("Query"))
                            {
                                //查詢退貨列表
                                String OrderType = request.getParameter("OrderType");
                                String OrderID = request.getParameter("OrderID");
                                String MerchantID = hashMerchant.get("MERCHANTID").toString();
                                String SubMID = hashMerUser.get("SUBMID").toString();
                                OrderType = UserBean.trim_Data(OrderType);
                                OrderID = UserBean.trim_Data(OrderID);
                                LogOrderID = OrderID;

                                if (OrderType == null)
                                    OrderType = "";

                                if (OrderID == null)
                                    OrderID = "";

                                if (MerchantID == null)
                                    MerchantID = "";

                                if (SubMID == null)
                                    SubMID = "";

                                ArrayList arrayRefundData = RefundBean.get_Refund_List(MerchantID, SubMID, OrderType, OrderID);
                                if (arrayRefundData == null)
                                    arrayRefundData = new ArrayList();

                                System.out.println("arrayRefundData.size()=" + arrayRefundData.size());
                                String OverRefundLimit = hashMerchant.get( "OVER_REFUND_LIMIT").toString();
                                Hashtable hashRefundData = RefundBean.check_Refund_Status(arrayRefundData, OverRefundLimit);
                                String PartialFlag = hashMerchant.get("PERMIT_PARTIAL_REFUND").toString();
                                hashRefundData.put("DATALIST", arrayRefundData); // 查詢退貨資料
                                hashRefundData.put("PARTIALFLAG", PartialFlag); // 是否可分批退貨
                                hashRefundData.put("OVER_REFUND_LIMIT", OverRefundLimit); // 超額退貨金額
                                System.out.println("hashRefundData="+hashRefundData);
                                Message = hashRefundData.get("MESSAGE").toString();

                                if (session.getAttribute("RefundData") != null)
                                {
                                    session.removeAttribute("RefundData");
                                }

                                session.setAttribute("RefundData", hashRefundData);
                                Forward = "./Merchant_Refund_List.jsp";
                            }

                            if (Action.equalsIgnoreCase("Refund"))
                            {
                                //查詢交易
                                Hashtable hashRefundData = (Hashtable) session.getAttribute("RefundData");
                                ArrayList arrayList = (ArrayList) hashRefundData.get("DATALIST");
                                Hashtable hashList = new Hashtable();

                                for (int i = 0; i < arrayList.size(); i++)
                                {
                                    Hashtable hashTemp = (Hashtable) arrayList.get(i);
                                    String transcode = hashTemp.get("TRANSCODE").toString();
                                    if (transcode.equalsIgnoreCase("00"))
                                    {
                                        hashList = hashTemp;
                                        break;
                                    }
                                }

                                if (hashList.size() > 0)
                                {
                                    System.out.println("hashList=" + hashList);
                                    String MerchantID = hashList.get("MERCHANTID").toString();
                                    String TerminalID = hashList.get("TERMINALID").toString();
                                    String BALANCEAMT1 =  hashList.get("BALANCEAMT1").toString();
                                    System.out.println("MerchantID=" + MerchantID + ",TerminalID=" + TerminalID);
                                    Terminal_Current = UserBean.check_Terminal_Column(MerchantID, TerminalID, arrayTerminal, "CURRENTCODE", "B,D"); //  確認端末機狀態
                                    Terminal_Permit = UserBean.check_Terminal_Column(MerchantID, TerminalID, arrayTerminal, "PERMIT_REFUND", "Y"); //  確認端末機權限
                                    System.out.println("Terminal_Current=" + Terminal_Current + ",Terminal_Permit=" + Terminal_Permit);
                                    
                                    //if(Double.parseDouble(BALANCEAMT1)>0){
	                                    if (Terminal_Current && Terminal_Permit){
	
	                                        String SubMID = hashMerUser.get("SUBMID").toString();
	                                        System.out.println("SubMID=" + SubMID);
	                                        String OrderID = hashList.get("ORDERID").toString();
	                                        System.out.println("OrderID=" + OrderID);
	
	                                        String PartialFlag = hashMerchant.get("PERMIT_PARTIAL_REFUND").toString();
	                                        System.out.println("PartialFlag=" + PartialFlag);
	                                        boolean TerminalPartialFlag = UserBean.check_Terminal_Column(MerchantID, TerminalID, arrayTerminal, "PERMIT_PARTIAL_REFUND", "Y"); //  確認端末機權限
	
	                                        boolean boolPartial = false;
	                                        if (PartialFlag.equalsIgnoreCase("Y") && TerminalPartialFlag)
	                                        {
	                                            boolPartial = true;
	                                        }
	
	                                        String TransMode = hashList.get("TRANSMODE").toString(); //  交易模式  2007.11.08
	                                        if (!TransMode.equalsIgnoreCase("0"))
	                                        {
	                                            // 一般
	                                            boolPartial = false;
	                                        }
	                                        Hashtable hashBalanceData = UserBean.get_Balance(sysBean, MerchantID, SubMID, OrderID);
	                                        /****** 銀聯卡需求修改 DalePeng 20150616 ---Start--- ******/
	                                        String AUTHAMT = hashBalanceData.get("AUTHAMT").toString();
	                                        /****** 銀聯卡需求修改 DalePeng 20150616 ---End--- ******/
	                                        LogOrderID = OrderID;
	                                        String InputRefundAmt = request.getParameter("InputRefundAmt");
	
	                                        if (InputRefundAmt == null)
	                                            InputRefundAmt = "";
	
	                                        System.out.println("InputRefundAmt=" + InputRefundAmt);
	                                        MerchantRefundBean MerchantRefundBean = new MerchantRefundBean();
	
	                                        String OverRefundLimit = hashMerchant.get("OVER_REFUND_LIMIT").toString();
	                                        Hashtable hashRefund = MerchantRefundBean.check_Refund_Amt(hashBalanceData, boolPartial, InputRefundAmt, OverRefundLimit );
	                                        String CheckFlag = hashRefund.get("FLAG").toString();
	
	                                        if (CheckFlag.equalsIgnoreCase("true"))
	                                        {
	                                            // 可進行退貨
	                                            ArrayList arrayBilling = UserBean.get_Billing(sysBean, MerchantID, SubMID, "Order", OrderID, "00");
	                                            if (arrayBilling.size() > 0)
	                                            {
	                                                	Hashtable hashBilling = (Hashtable)arrayBilling.get(0);
	                                                	/****** 銀聯卡需求修改 DalePeng 20150616 ---Start--- ******/
	                                                	// 檢查輸入退貨金與 原始交易金額是否相同,不同則回查詢網頁
	                                                	// 因為銀聯卡不允許需求退貨 else 跳到response.jsp
	                                                	String Card_Type = hashBilling.get("CARD_TYPE").toString();
	                                                	if ((Card_Type.equalsIgnoreCase("C") && InputRefundAmt.equalsIgnoreCase(AUTHAMT)) || (!Card_Type.equalsIgnoreCase("C")))
	                                                	{
	                                                	/****** 銀聯卡需求修改 DalePeng 20150616 ---End--- ******/
	//                                                  DataBaseBean DataBaseBean = new DataBaseBean();
	//                                             	    DataBaseBean.setAutoCommit(false);
	                                                	boolean CommitFlag = true;
	
	                                                	String TransCode = "01";
	                                                	String TmpTransDate = UserBean.get_TransDate("yyyyMMddHHmmss");
	                                                	String TransDate = TmpTransDate.substring(0,8);
	                                                	String TransTime = TmpTransDate.substring(8,14);
	                                                	String TmpSerial = UserBean.get_TransDate("MMddHHmmssSSSS");
	                                                	String Sys_OrderID = OrderID + "_" + TmpSerial;
	                                                	String MTI = "0220";
	                                                	String TransAmt = InputRefundAmt;
	                                                	String BillBalanceAmt = InputRefundAmt;
	                                                	String AcquirerID = hashBilling.get("ACQUIRERID").toString();
	//                                                String Card_Type = hashBilling.get("CARD_TYPE").toString();
	                                                	String PAN = hashBilling.get("PAN").toString();
	                                                	String ExtenNo = hashBilling.get("EXTENNO").toString();
	                                                	String ExpireDate = hashBilling.get("EXPIREDATE").toString();
	                                                	String ReversalFlag = hashBilling.get("REVERSALFLAG").toString();
	                                                	String CurrencyCode = hashBilling.get("CURRENCYCODE").toString();
	                                                	String TransStatus = "R"; // 20150820 Dalepeng
	//                                                	String TransStatus = "A";
	                                                	String ApproveCode = hashBilling.get("APPROVECODE").toString();
	                                                	String ResponseCode = hashBilling.get("RESPONSECODE").toString();
	                                                	String ResponseMsg = hashBilling.get("RESPONSEMSG").toString();
	                                                	String Entry_Mode = hashBilling.get("ENTRY_MODE").toString();
	                                                	String Condition_Code = hashBilling.get("CONDITION_CODE").toString();
	                                                	String BatchNo = hashBilling.get("BATCHNO").toString();
	                                                	String UserDefine = hashBilling.get("USERDEFINE").toString();
	                                                	//String Direction = "R";
	                                                	String Direction = "S";
	                                                	String EMail = hashBilling.get("EMAIL").toString();
	                                                	String RRN = hashBilling.get("RRN").toString();
	                                                	String SocialID = hashBilling.get("SOCIALID").toString();
	                                                	String TransType = hashBilling.get("TRANSTYPE").toString();
	                                                	String ECI = hashBilling.get("ECI").toString();
	                                                	String CAVV = hashBilling.get("CAVV").toString();
	                                                	String XID = hashBilling.get("XID").toString();
	                                                	String InstallType = hashBilling.get("INSTALLTYPE").toString();
	                                                	String Install = hashBilling.get("INSTALL").toString();
	                                                	String FirstAmt = hashBilling.get("FIRSTAMT").toString();
	                                                	String EachAmt = hashBilling.get("EACHAMT").toString();
	                                                	String FEE = hashBilling.get("FEE").toString();
	                                                	String RedemType = hashBilling.get("REDEMTYPE").toString();
	                                                	String RedemUsed = hashBilling.get("REDEMUSED").toString();
	                                                	String RedemBalance = hashBilling.get("REDEMBALANCE").toString();
	                                                	String CreditAmt = hashBilling.get("CREDITAMT").toString();
	                                                	String BillMessage = hashBilling.get("BILLMESSAGE").toString();
	                                                	String SysTraceNo = hashBilling.get("SYSTRACENO").toString();
	                                                	String AUTH_SRC_CODE = hashBilling.get("AUTH_SRC_CODE").toString();//20220210 ADD AUTH_SRC_CODE
	//                                               	 DataBaseBean.setAutoCommit(false);
	                                                	
	                                                	if (Card_Type.equalsIgnoreCase("C"))
	                                                	{
	                                                		PAN = "62XX";
	                                                	}
	                                                	
	                                                	System.out.println("insert_AuthLog");
	                                                	//20220210 ADD AUTH_SRC_CODE
	                                                	if (!UserBean.insert_AuthLog(sysBean, MerchantID, SubMID, TerminalID, AcquirerID,
	                                                            OrderID, Sys_OrderID, Card_Type, PAN, ExtenNo, ExpireDate, TransCode,
	                                                            ReversalFlag, TransDate, TransTime, CurrencyCode, TransAmt, TransStatus,
	                                                            ApproveCode, ResponseCode, ResponseMsg, Entry_Mode, Condition_Code,
	                                                            BatchNo, UserDefine, Direction, EMail, MTI, RRN, SocialID, TransMode,
	                                                            TransType, ECI, CAVV, XID, InstallType, Install, FirstAmt, EachAmt,
	                                                            FEE, RedemType, RedemUsed, RedemBalance, CreditAmt, BillMessage, SysTraceNo,AUTH_SRC_CODE))
	                                                	{
	                                                		CommitFlag = false;
	                                                	}
	                                                	if (Card_Type.equalsIgnoreCase("C"))
	                                                	{
	                                                		System.out.println("卡別 : C - CUP");
	                                                		/****** 銀聯卡修改  DalePeng 20150615 --Start-- ******/
	                                                		String TransDate_CUP     = TransDate;
	                                                		String TransTime_CUP     = TransTime;
	                                                	
	                                                		String Trace_Time        = ""; 
	                                                		String Settle_Amount     = "0";
	                                                		/****** 銀聯卡修改  DalePeng 20150615 --Start-- ******/    	
	                                                		String Settle_Currency   = CurrencyCode;
	                                                		/****** 銀聯卡修改  DalePeng 20150615 --End-- ******/
	                                                		String Settle_Date       = "";
	                                                		String Exchange_Rate     = "";
	                                                		String Exchange_Date     = "";
	                                                		String Cup_Paymode       = "";
	                                                		String Cup_TransCode     = "04";
	                                                		String Trans_Status      = "R";
	                                                		String ResponseCode_CUP  = "";
	                                                		String ResponseMsg_CUP   = "";
	                                                		String Cup_QID           = "0";
	                                                		String Cup_Reserved      = "";
	                                                		String Direction_CUP     = "S";
	                                                		String Cps_Refresh       = "N"; 
	                                                		String Filestatus_Refkey = ""; 
	                                                		String Notifyurl         = ""; 
	                                                		String Notifyparam       = ""; 
	                                                		String Notifytype        = "";  
	                                                		String Retry_Cnt         = "0"; 
	                                                		String Cup_Notifypage    = "";
	                                                		String Cup_Notifyparam   = "";
	                                                		System.out.println("insert_AUTHLOG_CUP");
	                                                		if (!UserBean.insert_AuthLog_Cup(sysBean, MerchantID, SubMID, Sys_OrderID, TransCode,
	                                                				TransDate_CUP, TransTime_CUP, Trace_Time, Settle_Amount, Settle_Currency, Settle_Date,
	                                                		        Exchange_Rate, Exchange_Date, Cup_Paymode, Cup_TransCode, Trans_Status, 
	                                                		        ResponseCode_CUP, ResponseMsg_CUP, SysTraceNo, Cup_QID, Cup_Reserved, Direction_CUP, 
	                                                		        Cps_Refresh, Filestatus_Refkey, Notifyurl, Notifyparam, Notifytype, Retry_Cnt,
	                                                		        PAN, TransAmt, Cup_Notifypage, Cup_Notifyparam))
	                                                		{
	                                                			CommitFlag = false;
	                                                		}
	                                                	}
	                                                	System.out.println("insert_Billing");
	                                                	//20220210 ADD AUTH_SRC_CODE
	                                                	if (!UserBean.insert_Billing(sysBean, MerchantID, SubMID, TerminalID, AcquirerID, OrderID,
	                                                            Sys_OrderID, Card_Type, PAN, ExtenNo, ExpireDate, TransCode, ReversalFlag,
	                                                            TransDate, TransTime, CurrencyCode, TransAmt, ApproveCode, ResponseCode,
	                                                            ResponseMsg, BatchNo, UserDefine, EMail, MTI, RRN, SocialID, Entry_Mode,
	                                                            Condition_Code, TransMode, TransType, ECI, CAVV, XID, InstallType,
	                                                            Install, FirstAmt, EachAmt, FEE, RedemType, RedemUsed, RedemBalance,
	                                                            CreditAmt, BillMessage, BillBalanceAmt, SysTraceNo,AUTH_SRC_CODE))
	                                                	{
	
	                                                		CommitFlag = false;
	                                                	}
	
	                                                	System.out.println("update_Balance");
	                                                	String AuthAmt = hashBalanceData.get("AUTHAMT").toString();
	                                                	String RefundAmt = hashBalanceData.get("REFUNDAMT").toString();
	                                                	System.out.println("AuthAmt=" + AuthAmt + ",RefundAmt=" + RefundAmt + "InputRefundAmt=" + InputRefundAmt);
	                                                	RefundAmt = String.valueOf( Double.parseDouble(RefundAmt) + Double.parseDouble(InputRefundAmt));
	                                                	String RefundDate = TmpTransDate.substring(0, 4) + "/" +TmpTransDate.substring(4, 6) +
	                                                            "/" + TmpTransDate.substring(6, 8) + " " + TmpTransDate.substring(8, 10) + ":" +
	                                                            TmpTransDate.substring(10, 12) + ":" + TmpTransDate.substring(12, 14);
	                                                	String CancelAmt = hashBalanceData.get("CANCELAMT").toString();
	                                                	String BalanceAmt = hashBalanceData.get("BALANCEAMT").toString();
	                                                	//******修改可請款餘額邏輯  20150716     Start **//
	//                                               	 BalanceAmt = String.valueOf(Double.parseDouble(AuthAmt) - Double.parseDouble(RefundAmt) - Double.parseDouble(CancelAmt));
	                                                	BalanceAmt = String.valueOf(Double.parseDouble(BalanceAmt) - Double.parseDouble(RefundAmt) );
	                                                	//******修改可請款餘額邏輯  20150716     End **//
	                                                	System.out.println("AuthAmt=" + AuthAmt + ",RefundAmt=" + RefundAmt + "CancelAmt=" + CancelAmt + "BalanceAmt=" + BalanceAmt);
	                                                	if (!UserBean.update_Balance(sysBean, MerchantID, SubMID, OrderID, RefundAmt, RefundDate, "", "", "", "", "", "", BalanceAmt))
	                                                	{
	                                                    	CommitFlag = false;
	                                                	}
	                                                
	                                                	if (Card_Type.equalsIgnoreCase("C"))
	                                                	{
	                                                		System.out.println("卡別 : C - CUP  insert_SAF_CUP");
	                                                		if (!UserBean.insert_SAF_Cup(sysBean, MerchantID, SubMID, TerminalID, AcquirerID, OrderID, 
	                                                				Sys_OrderID, Card_Type, PAN, ExtenNo, ExpireDate, TransCode, ReversalFlag, 
	                                                				TransDate, TransTime, CurrencyCode, TransAmt, ApproveCode, ResponseCode, 
	                                                				ResponseMsg, Entry_Mode, Condition_Code, BatchNo, UserDefine, EMail, MTI, RRN, 
	                                                				SocialID, "REQ", "", "", "", "", "", SysTraceNo,"0", "C", TransType, TransMode))
	                                                		{
	                                                    		CommitFlag = false;
	                                                		}
	                                                	}
	                                                	else
	                                                	{
	                                                		System.out.println("insert_SAF");
	                                                		if (!UserBean.insert_SAF(sysBean, MerchantID, SubMID, TerminalID, AcquirerID, OrderID,
	                                                            	Sys_OrderID, Card_Type, PAN, ExtenNo, ExpireDate, TransCode, ReversalFlag,
	                                                            	TransDate, TransTime, CurrencyCode, TransAmt, ApproveCode, ResponseCode,
	                                                            	ResponseMsg, Entry_Mode, Condition_Code, BatchNo, UserDefine, EMail, MTI, RRN,
	                                                            	SocialID, TransMode, TransType, ECI, CAVV, XID, InstallType,
	                                                            	Install, FirstAmt, EachAmt, FEE, RedemType, RedemUsed, RedemBalance, CreditAmt,
	                                                            	"REQ", "", "", "", "", "", SysTraceNo))
	                                                		{
	                                                			CommitFlag = false;
	                                                		}
	                                                	}
	                                                
	                                                	if (CommitFlag)
	                                                	{
	                                                		CommitFlag = sysBean.commit();
	                                                	}
	                                                	else
	                                                	{
	                                                		sysBean.setRollBack();
	                                                	}
	//                                                	SysBean.close();
	                                                	if (CommitFlag)
	                                                	{
	                                                    	Message = "線上退貨作業成功";
	                                                    	LogStatus = "成功";
	                                                	}
	                                                	else
	                                                	{
	                                                    	Message = "線上退貨作業失敗";
	                                                	}
	//                                                  Forward = "./Merchant_Response.jsp";
	                                                	if (hashRefundData!=null)
	                                                    	session.removeAttribute("RefundData");
	
	                                                	ArrayList<Hashtable<String,String>> arrayRefundData = RefundBean.get_Refund_List(MerchantID, SubMID, "Order", OrderID);
	                                                	if (arrayRefundData == null)
	                                                		arrayRefundData = new ArrayList<Hashtable<String,String>>();
	
	                                                	Forward = "./Merchant_Refund_Response.jsp";
	                                                	session.setAttribute("RefundDataList",arrayRefundData);
	                                                	session.setAttribute("RefundStatus","Y");
	                                                	session.setAttribute("Message",Message);
	                              
	                                                }
	                                                else
	                                            	{	
	                                                	Message = "注意:銀聯卡交易不可部份退貨";
	                                                	session.setAttribute("RefundStatus","Y");
	                                                    session.setAttribute("Message", Message);
	                                                	Forward = "./Merchant_Refund_Response.jsp";
	                                            	} 
	                                            }   
	                                            else
	                                            {
	                                                Message = "注意：查無交易資料";
	//                                                Forward = "./Merchant_Response.jsp";
	                                                Forward = "./Merchant_Refund_Response.jsp";
	                                                session.setAttribute("RefundStatus","Y");
	                                                session.setAttribute("Message",
	                                                        Message);
	                                            }
	                                        }
	                                        else
	                                        {
	                                            Message = hashRefund.get("MESSAGE").toString();
	                                            Forward = "./Merchant_Refund_Response.jsp";
	                                            session.setAttribute("RefundStatus","Y");
	                                            session.setAttribute("Message", Message);
	                                        }
	                                    }
	                                    else
	                                    {
	                                        Message = "端末機無此權限請洽本行處理";
	                                        Forward = "./Merchant_Response.jsp";
	                                        session.setAttribute("Message", Message);
	                                    }
	                                /*}else{
	                                	Message = "退貨金額超過上限(ctl)";
	                                	Forward = "./Merchant_Response.jsp";
	                                    session.setAttribute("RefundStatus","Y");
	                                	session.setAttribute("Message", Message);
	                                } */    
                                }
                                else
                                {
                                    Message = "注意：查無交易資料";
                                    Forward = "./Merchant_Response.jsp";
                                    session.setAttribute("Message", Message);
                                }
                            }
                        }
                    }
                    else
                    {
                        Message = "特店無此功能權限";
                        Forward = "./Merchant_Response.jsp";
                        session.setAttribute("Message", Message);
                    }
                }
                else
                {
                    String CurrentCode = hashMerchant.get("CURRENTCODE").
                                         toString();
                    Message = UserBean.get_CurrentCode(CurrentCode);
                    Forward = "./Merchant_Response.jsp";
                    session.setAttribute("Message", Message);
                }
            }
            else
            {
                Message = "使用者無此權限請洽系統管理者";
                Forward = "./Merchant_NoUse.jsp";
                session.setAttribute("Message", Message);
            }

            if (Message.length()>0)
            {
                LogMemo = LogOrderID + Message;
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                log_user.debug(LogData);
            }

            System.out.println("Forward=" + Forward);
            request.getRequestDispatcher(Forward).forward(request, response);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log_systeminfo.debug("--MerchantRefundCtl--"+e.toString());
            request.setAttribute("errMsg", e.toString());
            request.getRequestDispatcher("./Merchant_Error.jsp").forward(
                    request, response);
        }
        //20130702 Jason 增加finally處理SysBean.close
        finally{
	        try
	        {
	            sysBean.close();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	            log_systeminfo.debug("--MerchantRefundCtl--"+e.toString());
	            request.setAttribute("errMsg", e.toString());
	            request.getRequestDispatcher("./Merchant_Error.jsp").forward(
	                    request, response);
	        }
        }
    }
}
