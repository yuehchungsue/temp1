/************************************************************
 * <p>#File Name:   MerchantCaptureCtl.java         </p>
 * <p>#Description:                         </p>
 * <p>#Create Date: 2007/10/02              </p>
 * <p>#Company:     cybersoft               </p>
 * <p>#Notice:                      </p>
 * @author      Shirley Lin
 * @since       SPEC version
 * @version 0.1 2007/10/02  Shirley Lin
 * 202007070145-00 20200707 HKP 路跑協會需求UI增加整批請款功能
 *    Tag:20200708-01
 *    1.UI:起日、迄日、交易類別(全部、購貨、退貨)
 *    2.顯示總購貨筆數、金額、總退貨筆數、金額，USER確定後進行整批請款，再顯示結果
 * 202112300619-01 20220210 GARY 請款批次請款規格增修(Visa Authorization Source Code)
 ************************************************************/
package com.cybersoft.merchant.ctl;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.w3c.util.UUID;

/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft.bean.LogUtils;
import com.cybersoft.bean.SessionControlBean;
import com.cybersoft.bean.UserBean;
import com.cybersoft.bean.createReport;
import com.cybersoft.bean.emDataType;
import com.cybersoft.common.Util;
import com.cybersoft.merchant.bean.MerchantCaptureBean;
import com.fubon.security.filter.SecurityTool;
/**
 * <p>控制請款的Servlet</p>
 * @version 0.1 2007/10/02  Shiley Lin
 */
public class MerchantCaptureCtl extends HttpServlet
{
	private static final long serialVersionUID = 1L;
    private static final String CONTENT_TYPE = "text/html; charset=Big5";
    public static final LogUtils log_user = new LogUtils("user");
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");

    public void init()
    {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        this.doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String LogFunctionName = "線上請款作業";
        String LogStatus = "失敗";
    	//20150910  程式使用傳遞資料用物件不使用全域變數 Start
    	System.out.println("Online Capture Ver:20160216");
        ArrayList arrayData = new ArrayList();        
        Hashtable hashData = new Hashtable();
        Hashtable hashMerUser = new Hashtable(); // 特店使用者
        Hashtable hashMerchant = new Hashtable(); // 特店主檔        
        ArrayList arrayTerminal = new ArrayList(); // 端末機主檔
        Hashtable hashSys = new Hashtable(); // 20150910 系統參數一致所以不須移入        
        Hashtable hashConfData = new Hashtable();
        DataBaseBean2 sysBean = new DataBaseBean2();
        UserBean UserBean = new UserBean();
        String LogUserName = "";
        String LogMemo = "";
        String LogData = "";
        String LogMerchantID = "";
        String BatchPmtID = "";
        //20150910 程式使用傳遞資料用物件不使用全域變數 END
        String MerchantID="";
        String SubMID = "";
        String SubMID_UI = "";
        String UserID="";
        
        //20160215 Jason 移至dopost 宣告
        String Forward = "./Merchant_Response.jsp"; // 網頁轉址
        String Message = ""; // 顯示訊息
        response.setContentType(CONTENT_TYPE);
        request.setCharacterEncoding("BIG5");

        HttpSession session = request.getSession(true);
        /* Chech Session */
        SessionControlBean scb = new SessionControlBean();       
        boolean  isSubMid =false;
        try
        {
        	//session物件
            scb = new SessionControlBean(session, request, response);
            sysBean.setAutoCommit(false);
            //特店登入時特店資訊
            hashConfData = (session.getAttribute("SYSCONFDATA")!=null)
            		?(Hashtable)session.getAttribute("SYSCONFDATA"): null;
            if(hashConfData == null || hashConfData.size()==0) {
                log_systeminfo.debug("--MerchantCaptureCtl:特店資訊失效請重新登入,SYSCONFDATA is null");
                request.setAttribute("errMsg", "特店資訊失效請重新登入(SYSCONFDATA)");
                request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
                return;
            }
            //取回資訊
            /**********/
            if (hashConfData.get("SYSCONF") == null || ((Hashtable)hashConfData.get("SYSCONF")).size()==0) {
                log_systeminfo.debug("--MerchantCaptureCtl:特店資訊失效請重新登入,SYSCONF is null");
                request.setAttribute("errMsg", "特店資訊失效請重新登入(SYSCONF)");
                request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
                return;
            }
            hashSys = (Hashtable) hashConfData.get("SYSCONF"); // 系統參數
            /****MERCHANT_USER******/
            if (hashConfData.get("MERCHANT_USER") == null ||((Hashtable)hashConfData.get("MERCHANT_USER")).size()==0) {
                log_systeminfo.debug("--MerchantCaptureCtl:特店資訊失效請重新登入,MERCHANT_USER is null");
                request.setAttribute("errMsg", "特店資訊失效請重新登入(MERCHANT_USER)");
                request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
                return;
            }
            hashMerUser = (Hashtable) hashConfData.get("MERCHANT_USER"); // 特店使用者
            /****MERCHANT******/
            if (hashConfData.get("MERCHANT") == null || ((Hashtable)hashConfData.get("MERCHANT")).size() ==0) {
                log_systeminfo.debug("--MerchantCaptureCtl:特店資訊失效請重新登入,MERCHANT is null");
                request.setAttribute("errMsg", "特店資訊失效請重新登入(MERCHANT)");
                request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
                return;
            }
            hashMerchant = (Hashtable) hashConfData.get("MERCHANT"); // 特店主檔
            /****get infor******/
            isSubMid =((String) hashMerUser.get("ISSUBMERCHANT")).equals("Y") ?  true : false;
            LogMerchantID = (String)hashMerUser.get("MERCHANT_ID");
            MerchantID = hashMerchant.get("MERCHANTID").toString();
            UserID = hashMerUser.get("USER_ID").toString();
            SubMID =(hashMerUser.get("SUBMID") != null) ? hashMerUser.get("SUBMID").toString():"";
        }
        catch (UnsupportedOperationException E)
        {
            E.toString();
            request.getRequestDispatcher("/Merchant_Bye.jsp").forward(request, response);
            return;
        }

        try
        {
            boolean ForwardFlag = true;
            String MenuKey = (String) request.getParameter(MerchantMenuCtl.MENU_FORWARD_KEY);
            request.setAttribute(MerchantMenuCtl.MENU_FORWARD_KEY, MenuKey);
            String Action = Util.objToStrTrim(request.getParameter("Action"));
            if (Action.length() == 0)
            {
                //直接轉到查詢畫頁
                if (session.getAttribute("CaptureQuery") != null)
                {
                    session.removeAttribute("CaptureQuery");
                }
                Forward = "./Merchant_Capture_Query.jsp";
                System.out.println("Forward=" + Forward);
                request.getRequestDispatcher(Forward).forward(request, response);
                return;
            }
            arrayTerminal = (ArrayList) hashConfData.get("TERMINAL"); // 端末機主檔
            if (arrayTerminal == null)
                arrayTerminal = new ArrayList();

            boolean Merchant_Current = UserBean.check_Merchant_Column(hashMerchant, "CURRENTCODE", "B,D,E,F"); //  確認端末機狀態 B,D可購貨請款 B,D,E,F可退貨請款
            boolean Merchant_Permit = UserBean.check_Merchant_Column(hashMerchant, "PERMIT_CAPTURE", "Y"); //  確認特店權限
            boolean Merchant_Permit1 = UserBean.check_Merchant_Column(hashMerchant, "CAPTURE_MANUAL", "Y"); //  確認特店權限
            boolean User_Permit = MerchantMenuCtl.getUserRole(sysBean, session, MenuKey);
            Date date = new Date();
            String today= new SimpleDateFormat("yyyy/MM/dd").format(date);
            boolean isLogOutFlag = scb.check_Session(hashMerUser.get("MERCHANT_ID").toString(), hashMerUser.get("USER_ID").toString(), session.getId(), session, today);
            if(isLogOutFlag == true) {
                Message = "特店登入失效請重新登入";
                Forward = "./Merchant_NoUse.jsp";
                session.setAttribute("Message", Message);
                LogMemo = Message;
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                log_user.debug(LogData);
                System.out.println("Forward=" + Forward);
                request.getRequestDispatcher(Forward).forward(request, response);
                return;
            }
            if(User_Permit == false) {
                Message = "特約商店/使用者無此權限請洽本行處理";
                Forward = "./Merchant_NoUse.jsp";
                session.setAttribute("Message", Message);
                LogMemo = Message;
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                log_user.debug(LogData);
                System.out.println("Forward=" + Forward);
                request.getRequestDispatcher(Forward).forward(request, response);
                return;
            }
            // 使用者權限
            if(Merchant_Current == false) {
                String CurrentCode = hashMerchant.get("CURRENTCODE").toString();
                Message = UserBean.get_CurrentCode(CurrentCode);
                Forward = "./Merchant_Response.jsp";
                session.setAttribute("Message", Message);
                LogMemo = Message;
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                log_user.debug(LogData);
                System.out.println("Forward=" + Forward);
                request.getRequestDispatcher(Forward).forward(request, response);
                return;
            }
          //特店狀態
            if(Merchant_Permit ==false || Merchant_Permit1==false) {
                Message = "特店無此功能權限";
                Forward = "./Merchant_Response.jsp";
                session.setAttribute("Message", Message);
                LogMemo = Message;
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                log_user.debug(LogData);
                System.out.println("Forward=" + Forward);
                request.getRequestDispatcher(Forward).forward(request, response);
                return;
            }

            MerchantCaptureBean CaptureBean = new MerchantCaptureBean();
            System.out.println("Action=" + Action);
            if (Action.equalsIgnoreCase("Query") || Action.equalsIgnoreCase("FirstQuery"))
            {
            	//查詢請款列表
                String StartTransDate = "";
                String EndTransDate = "";
                String TerminalID = "";
                String TransCode = "";
                String CheckFlag = "";
                String OrderType = "";
                String OrderID = "";
                
                // Merchant Console 線上請款作業模組  新增TransType變數  by Jimmy Kang 20150515 --新增開始--
                String TransType = "";
                // Merchant Console 線上請款作業模組  新增TransType變數  by Jimmy Kang 20150515 --新增結束--
               
                String page_no = UserBean.trim_Data(request.getParameter("page_no"));
                if (page_no == null)
                    page_no = "0";

                Hashtable hashQuery = new Hashtable();
                System.out.println("session.getAttribute('CaptureData')="+session.getAttribute("CaptureData") +",");
                if (session.getAttribute("CaptureData") != null && Action.equalsIgnoreCase("Query"))
                {
                    Hashtable hashAll = (Hashtable) session.getAttribute("CaptureData");
                    hashQuery = (Hashtable) hashAll.get("QUERY");
                    StartTransDate = (String) hashQuery.get("StartTransDate");
                    EndTransDate = (String) hashQuery.get("EndTransDate");
                    TerminalID = (String) hashQuery.get("TerminalID");
                    TransCode = (String) hashQuery.get("TransCode");

                    //Merchant Console 線上請款作業模組  新增  by Dale Peng 20150521 --新增開始--
                    TransType = (String) hashQuery.get("TransType");
                    //Merchant Console 線上請款作業模組  新增  by Dale Peng 20150521 --新增結束--

                    CheckFlag = (String) hashQuery.get("CheckFlag");
                    OrderType = (String) hashQuery.get("OrderType");
                    OrderID = (String) hashQuery.get("OrderID");
                }
                else
                {
                	StartTransDate = Util.objToStrTrim(request.getParameter("StartTransDate"));
                    hashQuery.put("StartTransDate", StartTransDate);
                    EndTransDate = Util.objToStrTrim(request.getParameter("EndTransDate"));
                    hashQuery.put("EndTransDate", EndTransDate);
                    TerminalID = Util.objToStrTrim(request.getParameter("TerminalID"));
                    if (TerminalID == "") TerminalID = "ALL";
                    hashQuery.put("TerminalID", TerminalID);
                    TransCode = Util.objToStrTrim(request.getParameter("TransCode"));
                    if (TransCode == "") TransCode = "ALL";
                    hashQuery.put("TransCode", TransCode);
                    
                    // Merchant Console 線上請款作業模組  新增  by Jimmy Kang 20150515 --新增開始--
                    // 將TransType放入儲存查詢條件的hashQuery中 
                    TransType = Util.objToStrTrim(request.getParameter("TransType"));
                    hashQuery.put("TransType", TransType);
                    // Merchant Console 線上請款作業模組  新增  by Jimmy Kang 20150515 --新增結束--
                    
                    CheckFlag = Util.objToStrTrim(request.getParameter("CheckFlag"));
                    if (CheckFlag == "") CheckFlag = "Y";
                    hashQuery.put("CheckFlag", CheckFlag);
                    OrderType = Util.objToStrTrim(request.getParameter("OrderType"));
                    hashQuery.put("OrderType", OrderType);
                    OrderID = Util.objToStrTrim(request.getParameter("OrderID"));
                    hashQuery.put("OrderID", OrderID);
                }

                String CaptureDay = Util.objToStrTrim(hashSys.get("MER_CAPTURE_DAY"));
                System.out.println("CaptureDay="+CaptureDay);
                // Merchant Console 線上請款作業模組  修改  by Jimmy Kang 20150515 --新增開始--
                // 在  get_Capture_List method 新增參數 TransType
                arrayData = CaptureBean.get_Capture_List(sysBean,  MerchantID, SubMID, StartTransDate,
                                EndTransDate, TerminalID, TransCode, OrderType, OrderID, "0", CaptureDay, TransType);
                // Merchant Console 線上請款作業模組  修改  by Jimmy Kang 20150515 --新增結束--
                
                if (arrayData == null)
                    arrayData = new ArrayList();

                String PartialFlag = hashMerchant.get("PERMIT_PARTIAL_CAPTURE").toString();
                hashData.put("DATALIST", arrayData); // 查詢請款資料
                hashData.put("PARTIALFLAG", PartialFlag); // 是否可分批請款
                hashData.put("CHECKFLAG", CheckFlag); // 圈選方式
                hashData.put("QUERY", hashQuery); // 查詢條件
                hashData.put("NOWPAGE", page_no); // 查詢頁次
                /****** 銀聯卡需求修改 Dalepeng 20150618 ---Start ******/ 
                hashData.put("TRANSTYPE", TransType); // 卡別  DALE
                /****** 銀聯卡需求修改 Dalepeng 20150618 ---END ******/ 
                if (session.getAttribute("CaptureData") != null)
                {
                    session.removeAttribute("CaptureData");
                }

                session.setAttribute("CaptureData", hashData);
                LogMemo = "請款資料列表共"+String.valueOf(arrayData.size())+"筆";
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "成功", LogMemo);
                log_user.debug(LogData);
                Forward = "./Merchant_Capture_List.jsp";
            }//END if (Action.equalsIgnoreCase("Query") || Action.equalsIgnoreCase("FirstQuery"))
            if (Action.equalsIgnoreCase("Check"))
            {
                //查詢請款選取資料
                String InputSysOrderID = Util.objToStrTrim(request.getParameter("InputSysOrderID"));
                String InputCaptureAmt = Util.objToStrTrim(request.getParameter("InputCaptureAmt"));
                String SysOrderID[] = InputSysOrderID.split(",");
                String CaptureAmt[] = InputCaptureAmt.split(",");
                String CaptureDay = Util.objToStrTrim(hashSys.get("MER_CAPTURE_DAY"));
                arrayData = CaptureBean.get_Capture_Check_List(sysBean, MerchantID, SubMID,
                                            SysOrderID, CaptureAmt, CaptureDay);
                ArrayList arrayCardTest = UserBean.get_Test_Card(sysBean, "N","Y");   //  捉出測試卡
                ArrayList arraySuccess = CaptureBean.check_Capture_Amt(arrayData, arrayCardTest);
                Hashtable hasSumAmt = CaptureBean.sum_Capture_Amt(arraySuccess);
                String PartialFlag = "";
                /****** 銀聯卡需求修改 Dalepeng 20150618 ---Start ******/ 
                String TransType = "";
                /****** 銀聯卡需求修改 Dalepeng 20150618 ---END ******/ 
                if (session.getAttribute("CaptureData") != null)
                {
                    Hashtable hashCaptureData = (Hashtable) session.getAttribute("CaptureData");
                    PartialFlag = hashCaptureData.get( "PARTIALFLAG").toString(); // 是否可分批請款
                    /****** 銀聯卡需求修改 Dalepeng 20150618 ---Start ******/ 
                    TransType = hashCaptureData.get("TRANSTYPE").toString();
                    System.out.println("TransType" + TransType);
                    /****** 銀聯卡需求修改 Dalepeng 20150618 ---END ******/ 
                    session.removeAttribute("CaptureData");
                }

                String OverRefundLimit = hashMerchant.get("OVER_REFUND_LIMIT").toString();
                Hashtable hashCaptureData = CaptureBean.check_Capture_Amt(arrayData, hasSumAmt,
                                    PartialFlag, arrayTerminal, OverRefundLimit, arrayCardTest);
                /****** 銀聯卡需求修改 Dalepeng 20150618 ---Start ******/ 
                hashCaptureData.put("TRANSTYPE", TransType); //
                /****** 銀聯卡需求修改 Dalepeng 20150618 ---END ******/ 
                if (session.getAttribute("CaptureCheckData") != null)
                {
                    session.removeAttribute("CaptureCheckData");
                }

                session.setAttribute("CaptureCheckData", hashCaptureData);
                LogMemo = "請款資料勾選共"+String.valueOf(arrayData.size())+"筆";
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "成功", LogMemo);
                log_user.debug(LogData);
                Forward = "./Merchant_CaptureCheck_List.jsp";
            } //END if (Action.equalsIgnoreCase("Check"))

            if (Action.equalsIgnoreCase("Capture"))
            {
                //執行請款作業
                Hashtable hashCarpture = new Hashtable();
                /****** 銀聯卡需求修改 Dalepeng 20150618 ---Start ******/ 
                String TransType = "";
                /****** 銀聯卡需求修改 Dalepeng 20150618 ---END ******/ 
                if (session.getAttribute("CaptureCheckData") != null)
                {
                    hashCarpture = (Hashtable) session.getAttribute("CaptureCheckData");
                    /****** 銀聯卡需求修改 Dalepeng 20150618 ---Start ******/ 
                    TransType = hashCarpture.get("TRANSTYPE").toString();
                    /****** 銀聯卡需求修改 Dalepeng 20150618 ---END ******/ 
                    session.removeAttribute("CaptureCheckData");
                }
                //20150911 Jason 改成傳入 BatchPmtID
                System.out.println("--CALL CAPTURE METHOD START--");
                BatchPmtID = Capture(sysBean, hashCarpture ,hashMerchant);
                System.out.println("--CALL CAPTURE METHOD END--");
                ArrayList arraySuccess = UserBean.get_Batch(sysBean, MerchantID, SubMID, BatchPmtID,
                            "SUCCESS", "BATCHTERMINALID,BATCHSYSORDERID,BATCHTRANSCODE,BATCHTXDATE,BATCHTXTIME"); // 成功資料
                ArrayList arrayFail    = UserBean.get_Batch(sysBean, MerchantID, SubMID, BatchPmtID,
                            "FAIL"   , "BATCHTERMINALID,BATCHSYSORDERID,BATCHTRANSCODE,BATCHTXDATE,BATCHTXTIME"); // 失敗資料

                hashCarpture = new Hashtable();
                hashCarpture.put("SUCCESS", arraySuccess);
                hashCarpture.put("FAIL", arrayFail);
                hashCarpture.put("BATCHPMTID", BatchPmtID); // 批次序號
                /****** 銀聯卡需求修改 Dalepeng 20150618 ---Start ******/ 
                hashCarpture.put("TRANSTYPE", TransType);
                /****** 銀聯卡需求修改 Dalepeng 20150618 ---END ******/ 
                if (session.getAttribute("CaptureUpdateData") != null)
                {
                    session.removeAttribute("CaptureUpdateData");
                }

                session.setAttribute("CaptureUpdateData", hashCarpture);
                Forward = "./Merchant_CaptureUpdate_List.jsp";
                LogMemo = "請款資料成功"+String.valueOf(arraySuccess.size())+"筆，失敗"+String.valueOf(arrayFail.size())+"筆";
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "成功", LogMemo);
                log_user.debug(LogData);
            } // END if (Action.equalsIgnoreCase("Capture"))

            if (Action.equalsIgnoreCase("Print"))
            {
            	/****** 銀聯卡需求修改 Dalepeng 20150618 ---Start ******/ 
            	 Hashtable hashCarpture = new Hashtable();
                 String TransType = "";
                 if (session.getAttribute("CaptureUpdateData") != null)
                 {
                     hashCarpture = (Hashtable) session.getAttribute("CaptureUpdateData");
                     TransType = hashCarpture.get("TRANSTYPE").toString();
                     System.out.println("TransType_Print" + TransType);
                     if (TransType.equalsIgnoreCase("ALL"))
                     {
                    	 TransType = "全部";
                     }
                     else if (TransType.equalsIgnoreCase("SSL"))
                     {
                    	 TransType = "信用卡";
                     }
                     else if (TransType.equalsIgnoreCase("VMJ"))
                     {
                    	 TransType = "VMJ";
                     }
                     else if (TransType.equalsIgnoreCase("CUP"))
                     {
                    	 TransType = "CUP";
                     }
                     System.out.println("TransType_Print_2" + TransType);
                 } 
                 /****** 銀聯卡需求修改 Dalepeng 20150618 ---END ******/ 
            	
            	// 匯出
                ForwardFlag = false;
                //20150911 操作BatchPmtID改由區域變數 拿掉String宣告 因為已經宣告過了
                BatchPmtID = (request.getParameter("BatchPmtID") == null) ? "" : UserBean.trim_Data(request.getParameter("BatchPmtID"));
                System.out.println("BatchPmtID" +BatchPmtID);
                String PrintType = (request.getParameter("PrintType") == null) ? "" : UserBean.trim_Data(request.getParameter("PrintType"));

                if (BatchPmtID.length() > 0 && PrintType.length() > 0)
                {
                    boolean RowdataFlag = true;
                    if (PrintType.equalsIgnoreCase("PDF")) RowdataFlag = false;
                    
                    /** 2023/05/02 組 SQL (By : YC) *//** Test Case : IT-TESTCASE-021 */
                    String sql = UserBean.get_Batch_Result(sysBean, MerchantID, SubMID, BatchPmtID, "",
                                "BATCHTERMINALID,BATCHSYSORDERID,BATCHTRANSCODE,BATCHTXDATE,BATCHTXTIME",RowdataFlag);
                    createReport cr = new createReport();
                    Hashtable field = new Hashtable();
                    field.put("SHOW", "線上");
                    /****** 銀聯卡需求修改 Dalepeng 20150618 ---Start ******/ 
                    field.put("TRANSTYPESHOW", TransType);
                    /****** 銀聯卡需求修改 Dalepeng 20150618 ---END ******/ 
                    
                    String RPTName = "MerchantCaptureUpdateListReport.rpt";
                    if(isSubMid && !PrintType.equals("PDF") ){
                    	 RPTName = "MerchantCaptureUpdateListReportSubMID.rpt";
                    }
                    cr.createPDF(sysBean,SecurityTool.output(sql), request, response, "/Report/" + RPTName, field, PrintType);
                    // UserBean.closeConn();
                    LogMemo = "以"+PrintType+"格式匯出請款資料";
                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "成功", LogMemo);
                    log_user.debug(LogData);
                }
            } // END if (Action.equalsIgnoreCase("Print"))
            //Tag:20200708-01
            /***
             ** 整批請款，UI輸入欄位為交易起訖日期與交易別TransCode與子特店代號，Action==BATCH
             */
            if (Action.equalsIgnoreCase("BATCH"))
            {
         		hashData.put("Message", "");
            	UserBean util = new UserBean();
            	String sToday = util.get_AppointDay_Date("yyyyMMdd", 0);
            	String StartTransDate = Util.objToStrTrim(request.getParameter("StartTransDate")).replace("/", "");
            	String EndTransDate = Util.objToStrTrim(request.getParameter("EndTransDate")).replace("/", "");
            	String TransCode = Util.objToStrTrim(request.getParameter("TransCode"));
            	if(TransCode.length()==0) TransCode="ALL";
            	//可請款日數
            	int iCaptureDayLimit = Util.objToInt(hashSys.get("MER_CAPTURE_DAY"));
            	hashData.clear();
            	try {
            		arrayData = CaptureBean.get_BILLING_Batch_SumAmt(MerchantID, "ALL"/*SubMID*/, StartTransDate, EndTransDate, TransCode, iCaptureDayLimit);
            		hashData.put("Message", "");
            	}catch(Exception ex) {
            		hashData.put("Message", ex.getMessage());
            	}
            	
            	//=======================================
            	DataBaseBean2 DBBean =new DataBaseBean2();
            	DBBean.ClearSQLParam();
            	StringBuffer sSQLSB = new StringBuffer();
            	//查詢此特店UI批次是否有在執行 FILE_ATTRIBUTE IN CF,SF
            	sSQLSB.append("Select * From FILESTATUS WHERE MERCHANT_ID=? AND FILE_DATE=? AND FILE_ATTRIBUTE IN('CF','SF') ORDER BY FILE_DATE DESC,FILE_BATCHNO DESC");
             	DBBean.AddSQLParam(emDataType.STR, MerchantID); //1
             	DBBean.AddSQLParam(emDataType.STR, sToday); //1
             	ArrayList rsFileStatus = DBBean.QuerySQLByParam(sSQLSB.toString());
             	if(rsFileStatus != null && rsFileStatus.size() > 0) {
             		hashData.put("Message", "批次請款執行中，請稍後.....");
             		hashData.put("SUBMITBATCH_DISABLE", "Y"); //UI畫面控管
             	}else {
             		hashData.put("Message", "");
             		hashData.put("SUBMITBATCH_DISABLE", "N");//UI畫面控管
             	}
            	//1星期內的批次請款紀錄
            	String sStartDate = util.get_AppointDay_Date("yyyyMMdd", -7);
            	String sEndDate = util.get_AppointDay_Date("yyyyMMdd", 0);
         		rsFileStatus = CaptureBean.get_FILESTATUS_Log(MerchantID,sStartDate,sEndDate);
            	//=======================================
            	//將資料放入session內
            	Hashtable hashQuery = new Hashtable();
            	hashQuery.put("StartTransDate", StartTransDate);
            	hashQuery.put("EndTransDate", EndTransDate);
            	hashQuery.put("TransCode", TransCode);
            	hashQuery.put("BatchContent", "LIST");//批次請款用LIST:顯示資料;RESULT:顯示結果
            	hashData.put("QUERY", hashQuery); // 查詢請款資料
            	hashData.put("DATALIST", arrayData); // 請款資料
            	hashData.put("FILESTATUS", rsFileStatus); // 1星期內的批次請款紀錄
            	if (session.getAttribute("CaptureBatchData") != null) session.removeAttribute("CaptureBatchData");
            	session.setAttribute("CaptureBatchData", hashData);
                LogMemo = "UI批次請款查詢共"+String.valueOf(arrayData.size())+"筆";
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "成功", LogMemo);
                log_user.debug(LogData);
                Forward = "./Merchant_Capture_List_Batch.jsp";
            }
            /***
             * * 整批請款開始執行，UI輸入欄位為交易起訖日期與交易別TransCode與子特店代號，Action==BATCHCAPTURE
             */
            if (Action.equalsIgnoreCase("BATCHCAPTURE"))
            {
            	String StartTransDate = Util.objToStrTrim(request.getParameter("StartTransDate")).replace("/", "");
            	String EndTransDate = Util.objToStrTrim(request.getParameter("EndTransDate")).replace("/", "");
            	String TransCode = Util.objToStrTrim(request.getParameter("TransCode"));
            	if(TransCode.length()==0) TransCode="ALL";
            	//可請款日數
            	int iCaptureDayLimit = Util.objToInt(hashSys.get("MER_CAPTURE_DAY"));

            	UserBean util = new UserBean();
            	//=======================================
            	DataBaseBean2 DBBean =new DataBaseBean2(); DBBean.ClearSQLParam();
            	StringBuffer sSQLSB = new StringBuffer();
            	//查詢此特店UI批次是否有在執行 FILE_ATTRIBUTE IN CF,SF
            	sSQLSB.append("Select * From FILESTATUS WHERE MERCHANT_ID=? AND FILE_ATTRIBUTE IN('CF','SF') ORDER BY FILE_DATE DESC,FILE_BATCHNO DESC");
             	DBBean.AddSQLParam(emDataType.STR, MerchantID); //1
             	ArrayList rsFileStatus = DBBean.QuerySQLByParam(sSQLSB.toString());
             	if(rsFileStatus != null && rsFileStatus.size() > 0) {
             		hashData.put("Message", "批次請款執行中，請稍後.....");
             		hashData.put("SUBMITBATCH_DISABLE", "Y");
                	//1星期內的批次請款紀錄
                	String sStartDate = util.get_AppointDay_Date("yyyyMMdd", -7);
                	String sEndDate = util.get_AppointDay_Date("yyyyMMdd", 0);
             		rsFileStatus = CaptureBean.get_FILESTATUS_Log(MerchantID,sStartDate,sEndDate);
                 	//****資料置放於Session內再forward到UI
             		Hashtable hashQuery = new Hashtable();
                	hashQuery.put("BatchContent", "LIST");//批次請款用LIST:顯示資料;RESULT:顯示結果
                	//將資料放入session內
                	hashData.put("DATALIST", arrayData); // 請款資料
                	hashData.put("QUERY", hashQuery); // 查詢請款資料
                	hashData.put("FILESTATUS", rsFileStatus); // 查詢批次請款執行狀態資料
                	if (session.getAttribute("CaptureBatchData") != null) session.removeAttribute("CaptureBatchData");
                	session.setAttribute("CaptureBatchData", hashData);
                    LogMemo = "UI批次請款執行中";
                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "成功", LogMemo);
                    log_user.debug(LogData);
                    Forward = "./Merchant_Capture_List_Batch.jsp";
                    request.getRequestDispatcher(Forward).forward(request, response);
                    return;
             	}
             	ArrayList rs;
            	//=======================================
             	// 1. 取得當日批號
             	String sToday = util.get_AppointDay_Date("yyyyMMdd", 0);
             	DBBean =new DataBaseBean2();DBBean.ClearSQLParam();
             	sSQLSB = new StringBuffer("SELECT LPAD(TO_NUMBER(NVL(MAX(FILE_BATCHNO),-1))+1,4,'0') AS FILE_BATCHNO FROM FILESTATUS WHERE  MERCHANT_ID=? AND FILE_DATE=? ");
             	DBBean.AddSQLParam(emDataType.STR, MerchantID); //1
             	DBBean.AddSQLParam(emDataType.STR, sToday); //1
             	rs = DBBean.QuerySQLByParam(sSQLSB.toString());
             	String sBatchNo = (rs != null) ?Util.objToStrTrim(((Hashtable)rs.get(0)).get("FILE_BATCHNO")):"0000";  
             	// 2.Create FILESTATUS
             	String sFILE_NAME = "UI"+MerchantID+"_"+sToday+"_"+sBatchNo;
             	DBBean =new DataBaseBean2();DBBean.ClearSQLParam();
            	sSQLSB = new StringBuffer();
            	sSQLSB.append("INSERT INTO FileStatus(FILE_TAG,FILE_BATCHNO,MERCHANT_ID,FILE_DATE,FILE_NAME,FILE_ATTRIBUTE,PROCESS_DATE,FILESTATUS_REFKEY)");
            	sSQLSB.append("VALUES('UC',?,?,?,?,'CF',SYSTIMESTAMP,to_char(SYSTIMESTAMP,'YYYYMMDDHH24MISSFF'))");
             	DBBean.AddSQLParam(emDataType.STR, sBatchNo); //FILE_BATCHNO
             	DBBean.AddSQLParam(emDataType.STR, MerchantID); //MERCHANT_ID
             	DBBean.AddSQLParam(emDataType.STR, sToday); //FILE_DATE
             	DBBean.AddSQLParam(emDataType.STR, sFILE_NAME); //FILE_NAME
             	if(DBBean.executeSQL(sSQLSB.toString()) == false) {
             		hashData.put("Message", "產生批次請款資料(FILESTATUS)錯誤.....");
             		hashData.put("SUBMITBATCH_DISABLE", "Y");
                	//1星期內的批次請款紀錄
                	String sStartDate = util.get_AppointDay_Date("yyyyMMdd", -7);
                	String sEndDate = util.get_AppointDay_Date("yyyyMMdd", 0);
             		rsFileStatus = CaptureBean.get_FILESTATUS_Log(MerchantID,sStartDate,sEndDate);
                 	//****資料置放於Session內再forward到UI
             		Hashtable hashQuery = new Hashtable();
                	hashQuery.put("BatchContent", "LIST");//批次請款用LIST:顯示資料;RESULT:顯示結果
                	//將資料放入session內
                	hashData.put("DATALIST", arrayData); // 請款資料
                	hashData.put("QUERY", hashQuery); // 查詢請款資料
                	hashData.put("FILESTATUS", rsFileStatus); // 查詢批次請款執行狀態資料
                	if (session.getAttribute("CaptureBatchData") != null) session.removeAttribute("CaptureBatchData");
                	session.setAttribute("CaptureBatchData", hashData);
                    LogMemo = "UI批次請款Inser int FILESTATUS Fail";
                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "失敗", LogMemo);
                    log_user.debug(LogData);
                    Forward = "./Merchant_Capture_List_Batch.jsp";
                    request.getRequestDispatcher(Forward).forward(request, response);
                    return;
             	}
             	//2.1 GET FileStatus_RowID
             	DBBean =new DataBaseBean2();DBBean.ClearSQLParam();
            	sSQLSB = new StringBuffer();
            	sSQLSB.append("SELECT ROWIDTOCHAR(ROWID) ROWIDCHAR ,FILE_NAME FROM FILESTATUS WHERE");
            	sSQLSB.append(" FILE_NAME=? AND FILE_ATTRIBUTE='CF'");
            	sSQLSB.append(" AND MERCHANT_ID=? AND FILE_DATE=? AND ROWNUM=1");
             	DBBean.AddSQLParam(emDataType.STR, sFILE_NAME); //FILE_NAME
             	DBBean.AddSQLParam(emDataType.STR, MerchantID); //MERCHANT_ID
             	DBBean.AddSQLParam(emDataType.STR, sToday); //FILE_DATE
             	rs = DBBean.QuerySQLByParam(sSQLSB.toString());
             	String sFILESTATUS_REFKEY = "";
             	if(rs != null) {
             		sFILESTATUS_REFKEY =Util.objToStrTrim(((Hashtable)rs.get(0)).get("ROWIDCHAR"));
             	}
             	
             	if(sFILESTATUS_REFKEY.length()==0) {
             		hashData.put("Message", "更新 FILESTATUS.FILESTATUS_REFKEY失敗.....");
             		hashData.put("SUBMITBATCH_DISABLE", "Y");
                	//1星期內的批次請款紀錄
                	String sStartDate = util.get_AppointDay_Date("yyyyMMdd", -7);
                	String sEndDate = util.get_AppointDay_Date("yyyyMMdd", 0);
             		rsFileStatus = CaptureBean.get_FILESTATUS_Log(MerchantID,sStartDate,sEndDate);
                 	//****資料置放於Session內再forward到UI
             		Hashtable hashQuery = new Hashtable();
                	hashQuery.put("BatchContent", "LIST");//批次請款用LIST:顯示資料;RESULT:顯示結果
                	//將資料放入session內
                	hashData.put("DATALIST", arrayData); // 請款資料
                	hashData.put("QUERY", hashQuery); // 查詢請款資料
                	hashData.put("FILESTATUS", rsFileStatus); // 查詢批次請款執行狀態資料
                	if (session.getAttribute("CaptureBatchData") != null) session.removeAttribute("CaptureBatchData");
                	session.setAttribute("CaptureBatchData", hashData);
                    LogMemo = "UI批次請款,更新 FILESTATUS.FILESTATUS_REFKEY失敗";
                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "失敗", LogMemo);
                    log_user.debug(LogData);
                    Forward = "./Merchant_Capture_List_Batch.jsp";
                    request.getRequestDispatcher(Forward).forward(request, response);
                    return;
             	}
             	
            	//3.UPDATE FILESTATUS
             	DBBean =new DataBaseBean2();DBBean.ClearSQLParam();
            	sSQLSB = new StringBuffer();
            	sSQLSB.append("UPDATE FILESTATUS SET FILESTATUS_REFKEY=?");
            	sSQLSB.append(" WHERE FILE_NAME=? AND FILE_ATTRIBUTE='CF' AND MERCHANT_ID=? AND FILE_DATE=?");
            	DBBean.AddSQLParam(emDataType.STR, sFILESTATUS_REFKEY); //FILESTATUS_REFKEY
             	DBBean.AddSQLParam(emDataType.STR, sFILE_NAME); //FILE_NAME
             	DBBean.AddSQLParam(emDataType.STR, MerchantID); //MERCHANT_ID
             	DBBean.AddSQLParam(emDataType.STR, sToday); //FILE_DATE
             	if(DBBean.executeSQL(sSQLSB.toString()) == false) {
             		hashData.put("Message", "更新 FILESTATUS.FILESTATUS_REFKEY失敗.....");
             		hashData.put("SUBMITBATCH_DISABLE", "Y");
                	//1星期內的批次請款紀錄
                	String sStartDate = util.get_AppointDay_Date("yyyyMMdd", -7);
                	String sEndDate = util.get_AppointDay_Date("yyyyMMdd", 0);
             		rsFileStatus = CaptureBean.get_FILESTATUS_Log(MerchantID,sStartDate,sEndDate);
                 	//****資料置放於Session內再forward到UI
             		Hashtable hashQuery = new Hashtable();
                	hashQuery.put("BatchContent", "LIST");//批次請款用LIST:顯示資料;RESULT:顯示結果
                	//將資料放入session內
                	hashData.put("DATALIST", arrayData); // 請款資料
                	hashData.put("QUERY", hashQuery); // 查詢請款資料
                	hashData.put("FILESTATUS", rsFileStatus); // 查詢批次請款執行狀態資料
                	if (session.getAttribute("CaptureBatchData") != null) session.removeAttribute("CaptureBatchData");
                	session.setAttribute("CaptureBatchData", hashData);
                    LogMemo = "UI批次請款,更新 FILESTATUS.FILESTATUS_REFKEY失敗";
                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "失敗", LogMemo);
                    log_user.debug(LogData);
                    Forward = "./Merchant_Capture_List_Batch.jsp";
                    request.getRequestDispatcher(Forward).forward(request, response);
                    return;
             	}
             	
             	//4.UPDATE BILLING TABLE 批次前資料準備
             	DBBean =new DataBaseBean2();DBBean.ClearSQLParam();
            	sSQLSB = new StringBuffer();
            	sSQLSB.append("UPDATE BILLING B SET CAPTUREBATCHNO = ?,/*UIBATCHNO*/");
            	sSQLSB.append("DUE_DATE=TO_CHAR(TO_DATE(TRANSDATE,'YYYYMMDD')+?,'YYYYMMDD'),CAPTUREDATE = ?");
            	sSQLSB.append(" WHERE MERCHANTID = ? AND (TRANSDATE BETWEEN ? AND ?)");
            	sSQLSB.append(" AND BALANCEAMT > 0 AND REVERSALFLAG = '0' AND TRIM(PAN) <> '62XX'");
            	sSQLSB.append(" AND TO_CHAR(SYSDATE,'yyyyMMdd') <=TO_CHAR(TO_DATE( TRANSDATE,'YYYYMMDD') + ?, 'YYYYMMDD')");
            	sSQLSB.append(" AND (SELECT COUNT(1) FROM TEST_CARD WHERE (PAN BETWEEN PAN_BEGIN AND PAN_END) AND FLAG_CAPTURE = 'N' AND FLAG_ACTIVATE = 'Y') = 0");
            	sSQLSB.append(" AND (SELECT COUNT(1) FROM TERMINAL T WHERE T.MERCHANTID	= B.MERCHANTID AND T.TERMINALID =B.TERMINALID AND CURRENTCODE IN ('B','D','E','F') AND PERMIT_CAPTURE='Y') > 0");
             	DBBean.AddSQLParam(emDataType.STR, sBatchNo); //FILE_BATCHNO
             	DBBean.AddSQLParam(emDataType.INT, iCaptureDayLimit); //可請款日數
             	DBBean.AddSQLParam(emDataType.STR, sToday); //CAPTUREDATE
             	DBBean.AddSQLParam(emDataType.STR, MerchantID); //MERCHANT_ID
             	DBBean.AddSQLParam(emDataType.STR, StartTransDate); //
             	DBBean.AddSQLParam(emDataType.STR, EndTransDate); //
             	DBBean.AddSQLParam(emDataType.INT, iCaptureDayLimit); //可請款日數
             	if(DBBean.executeSQL(sSQLSB.toString()) == false) {
             		hashData.put("Message", "更新 BILLING TABLE 批次前資料準備..失敗.....");
             		hashData.put("SUBMITBATCH_DISABLE", "Y");
                	//1星期內的批次請款紀錄
                	String sStartDate = util.get_AppointDay_Date("yyyyMMdd", -7);
                	String sEndDate = util.get_AppointDay_Date("yyyyMMdd", 0);
             		rsFileStatus = CaptureBean.get_FILESTATUS_Log(MerchantID,sStartDate,sEndDate);
                 	//****資料置放於Session內再forward到UI
             		Hashtable hashQuery = new Hashtable();
                	hashQuery.put("BatchContent", "LIST");//批次請款用LIST:顯示資料;RESULT:顯示結果
                	//將資料放入session內
                	hashData.put("DATALIST", arrayData); // 請款資料
                	hashData.put("QUERY", hashQuery); // 查詢請款資料
                	hashData.put("FILESTATUS", rsFileStatus); // 查詢批次請款執行狀態資料
                	if (session.getAttribute("CaptureBatchData") != null) session.removeAttribute("CaptureBatchData");
                	session.setAttribute("CaptureBatchData", hashData);
                    LogMemo = "UI批次請款,BILLING TABLE 批次前資料準備..失敗";
                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "失敗", LogMemo);
                    log_user.debug(LogData);
                    Forward = "./Merchant_Capture_List_Batch.jsp";
                    request.getRequestDispatcher(Forward).forward(request, response);
                    return;
             	}
             	// UPDATE FILESTATUS FILE_TOTAL_COUNT....FILE_REFUND_AMOUNT
             	DBBean =new DataBaseBean2();DBBean.ClearSQLParam();
            	sSQLSB = new StringBuffer();
            	sSQLSB.append("SELECT COUNT(1) AS CNT,SUM(BALANCEAMT) SUMAMT,");
            	sSQLSB.append(" SUM(DECODE(TRANSCODE,'00',1,0)) AS CNT00,SUM(DECODE(TRANSCODE,'00',BALANCEAMT,0)) AS SUM00,");
            	sSQLSB.append(" SUM(DECODE(TRANSCODE,'01',1,0)) AS CNT01,SUM(DECODE(TRANSCODE,'01',BALANCEAMT,0)) AS SUM01");
            	sSQLSB.append(" FROM BILLING WHERE MERCHANTID=? AND CAPTUREDATE=? AND CAPTUREBATCHNO =? ");
             	DBBean.AddSQLParam(emDataType.STR, MerchantID); //MERCHANT_ID
             	DBBean.AddSQLParam(emDataType.STR, sToday); //FILE_DATE
             	DBBean.AddSQLParam(emDataType.STR, sBatchNo); //FILE_BATCHNO
             	rs = DBBean.QuerySQLByParam(sSQLSB.toString());
             	if(rs == null) {
             		hashData.put("Message", "更新 FILESTATUS FILE_TOTAL_COUNT資料準備..失敗.....");
             		hashData.put("SUBMITBATCH_DISABLE", "Y");
                	//1星期內的批次請款紀錄
                	String sStartDate = util.get_AppointDay_Date("yyyyMMdd", -7);
                	String sEndDate = util.get_AppointDay_Date("yyyyMMdd", 0);
             		rsFileStatus = CaptureBean.get_FILESTATUS_Log(MerchantID,sStartDate,sEndDate);
                 	//****資料置放於Session內再forward到UI
             		Hashtable hashQuery = new Hashtable();
                	hashQuery.put("BatchContent", "LIST");//批次請款用LIST:顯示資料;RESULT:顯示結果
                	//將資料放入session內
                	hashData.put("DATALIST", arrayData); // 請款資料
                	hashData.put("QUERY", hashQuery); // 查詢請款資料
                	hashData.put("FILESTATUS", rsFileStatus); // 查詢批次請款執行狀態資料
                	if (session.getAttribute("CaptureBatchData") != null) session.removeAttribute("CaptureBatchData");
                	session.setAttribute("CaptureBatchData", hashData);
                    LogMemo = "UI批次請款,更新 FILESTATUS FILE_TOTAL_COUNT資料準備..失敗";
                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "失敗", LogMemo);
                    log_user.debug(LogData);
                    Forward = "./Merchant_Capture_List_Batch.jsp";
                    request.getRequestDispatcher(Forward).forward(request, response);
                    return;
             	}
             	int iFILE_TOTAL_COUNT =Util.objToInt(((Hashtable)rs.get(0)).get("CNT"));
             	double dFILE_TOTAL_AMOUNT =Util.objToDouble(((Hashtable)rs.get(0)).get("SUMAMT"));
             	int iFILE_PURCHASE_COUNT =Util.objToInt(((Hashtable)rs.get(0)).get("CNT00"));
             	double dFILE_PURCHASE_AMOUNT =Util.objToDouble(((Hashtable)rs.get(0)).get("SUM00"));
             	int iFILE_REFUND_COUNT =Util.objToInt(((Hashtable)rs.get(0)).get("CNT01"));
             	double dFILE_REFUND_AMOUNT =Util.objToDouble(((Hashtable)rs.get(0)).get("SUM01"));
            	//開始處理批次請款
             	// 1.UPDATE FILESTATUS 為 SF /*處理中*/
             	DBBean =new DataBaseBean2();DBBean.ClearSQLParam();
            	sSQLSB = new StringBuffer();
            	sSQLSB.append("UPDATE FILESTATUS SET FILE_ATTRIBUTE='SF',");
            	sSQLSB.append(" FILE_TOTAL_COUNT=?,FILE_TOTAL_AMOUNT=?,");
            	sSQLSB.append(" FILE_PURCHASE_COUNT=?,FILE_PURCHASE_AMOUNT=?,");
            	sSQLSB.append(" FILE_REFUND_COUNT=?,FILE_REFUND_AMOUNT=?,");
            	sSQLSB.append(" PROCESS_DATE =SYSDATE ");
            	sSQLSB.append(" WHERE FILE_NAME=? AND FILE_ATTRIBUTE='CF'");
            	sSQLSB.append(" AND MERCHANT_ID=? AND FILE_DATE=?");
            	DBBean.AddSQLParam(emDataType.INT, iFILE_TOTAL_COUNT); //
            	DBBean.AddSQLParam(emDataType.DOUBLE, dFILE_TOTAL_AMOUNT); //
            	DBBean.AddSQLParam(emDataType.INT, iFILE_PURCHASE_COUNT); //
            	DBBean.AddSQLParam(emDataType.DOUBLE, dFILE_PURCHASE_AMOUNT); //
            	DBBean.AddSQLParam(emDataType.INT, iFILE_REFUND_COUNT); //
            	DBBean.AddSQLParam(emDataType.DOUBLE, dFILE_REFUND_AMOUNT); //
             	DBBean.AddSQLParam(emDataType.STR, sFILE_NAME); //FILE_NAME
             	DBBean.AddSQLParam(emDataType.STR, MerchantID); //MERCHANT_ID
             	DBBean.AddSQLParam(emDataType.STR, sToday); //FILE_DATE
             	if(DBBean.executeSQL(sSQLSB.toString()) == false) {
             		hashData.put("Message", "更新 FILESTATUS為SF批次處理中..失敗.....");
             		hashData.put("SUBMITBATCH_DISABLE", "Y");
                	//1星期內的批次請款紀錄
                	String sStartDate = util.get_AppointDay_Date("yyyyMMdd", -7);
                	String sEndDate = util.get_AppointDay_Date("yyyyMMdd", 0);
             		rsFileStatus = CaptureBean.get_FILESTATUS_Log(MerchantID,sStartDate,sEndDate);
                 	//****資料置放於Session內再forward到UI
             		Hashtable hashQuery = new Hashtable();
                	hashQuery.put("BatchContent", "LIST");//批次請款用LIST:顯示資料;RESULT:顯示結果
                	//將資料放入session內
                	hashData.put("DATALIST", arrayData); // 請款資料
                	hashData.put("QUERY", hashQuery); // 查詢請款資料
                	hashData.put("FILESTATUS", rsFileStatus); // 查詢批次請款執行狀態資料
                	if (session.getAttribute("CaptureBatchData") != null) session.removeAttribute("CaptureBatchData");
                	session.setAttribute("CaptureBatchData", hashData);
                    LogMemo = "UI批次請款,更新 FILESTATUS為SF批次處理中..失敗";
                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "失敗", LogMemo);
                    log_user.debug(LogData);
                    Forward = "./Merchant_Capture_List_Batch.jsp";
                    request.getRequestDispatcher(Forward).forward(request, response);
                    return;
                }
             	// INSERT INTO BATCH_CAPTURE FROM BILLING WHERE MERCHANTID AND CAPTUREBATCHNO =(FILE_BATCHNO)
             	//20220210 ADD AUTH_SRC_CODE
             	DBBean =new DataBaseBean2();DBBean.ClearSQLParam();
            	sSQLSB = new StringBuffer();
            	sSQLSB.append("INSERT INTO Batch_CAPTURE (MERCHANTID,SUBMID,TERMINALID,ACQUIRERID,ORDERID,SYS_ORDERID,CARD_TYPE,PAN,EXTENNO,EXPIREDATE,TRANSCODE,REVERSALFLAG,TRANSDATE,TRANSTIME,CURRENCYCODE,TRANSAMT,APPROVECODE,RESPONSECODE,RESPONSEMSG,BATCHNO,USERDEFINE,EMAIL,MTI,RRN,SOCIALID,ENTRY_MODE,CONDITION_CODE,TRANSMODE,TRANSTYPE,ECI,CAVV,XID,INSTALLTYPE,INSTALL,FIRSTAMT,EACHAMT,FEE,REDEMTYPE,REDEMUSED,REDEMBALANCE,CREDITAMT,BILLMESSAGE,BALANCEAMT,CAPTUREAMT,CAPTUREDATE,FEEDBACKCODE,FEEDBACKDATE,SYSTRACENO,FileStatus_RowID,FILE_DATE,DUE_DATE,REAUTH_FLAG,MOBILEPAYFLAG,MOBILEPAYTX,MOBILEPAYINFO,MOBILEPAYCRYPTO,MOBILEPAYWALLET,MOBILEPAYECI,MPI_VERSION,DS_TRANSID,MERCHANTPAN,QR_FLAG,AUTH_SRC_CODE)");
             	sSQLSB.append(" SELECT MERCHANTID,SUBMID,TERMINALID,ACQUIRERID,ORDERID,SYS_ORDERID,CARD_TYPE,PAN,EXTENNO,EXPIREDATE,TRANSCODE,REVERSALFLAG,TRANSDATE,TRANSTIME,CURRENCYCODE,TRANSAMT,APPROVECODE,RESPONSECODE,RESPONSEMSG,BATCHNO,USERDEFINE,EMAIL,MTI,RRN,SOCIALID,ENTRY_MODE,CONDITION_CODE,TRANSMODE,TRANSTYPE,ECI,CAVV,XID,INSTALLTYPE,INSTALL,FIRSTAMT,EACHAMT,FEE,REDEMTYPE,REDEMUSED,REDEMBALANCE,CREDITAMT,BILLMESSAGE,");
             	sSQLSB.append(" BALANCEAMT /*BALANCEAMT*/,BALANCEAMT /*CAPTUREAMT*/,");
             	sSQLSB.append(" SYSDATE/* CAPTUREDATE*/,'000' /*FEEDBACKCODE*/,SYSDATE /*FEEDBACKDATE*/,SYSTRACENO,");
             	sSQLSB.append(" ?/*FileStatus_RowID*/,?/*FILE_DATE*/,");
             	sSQLSB.append(" TO_DATE(DUE_DATE,'yyyyMMdd'),");
             	sSQLSB.append(" REAUTH_FLAG,MOBILEPAYFLAG,MOBILEPAYTX,MOBILEPAYINFO,MOBILEPAYCRYPTO,MOBILEPAYWALLET,MOBILEPAYECI,MPI_VERSION,DS_TRANSID,MERCHANTPAN,QR_FLAG,AUTH_SRC_CODE");
             	sSQLSB.append(" FROM BILLING ");
             	//------------------------
             	sSQLSB.append(" WHERE MERCHANTID= ? AND CAPTUREDATE = ?/*FILE_DATE*/ AND  CAPTUREBATCHNO = ?/*UIBATCHNO*/");
             	sSQLSB.append("  AND BALANCEAMT > 0");
             	
             	DBBean.AddSQLParam(emDataType.STR, sFILESTATUS_REFKEY); //FileStatus_RowID
             	DBBean.AddSQLParam(emDataType.STR, sToday); //FILE_DATE
             	//------------------------
             	DBBean.AddSQLParam(emDataType.STR, MerchantID); //MERCHANT_ID
             	DBBean.AddSQLParam(emDataType.STR, sToday); //FILE_DATE
             	DBBean.AddSQLParam(emDataType.STR, sBatchNo); //FILE_BATCHNO
             	if(DBBean.executeSQL(sSQLSB.toString()) == false) {
             		hashData.put("Message", "INSERT INTO Batch_CAPTURE..失敗.....");
             		hashData.put("SUBMITBATCH_DISABLE", "Y");
                	//1星期內的批次請款紀錄
                	String sStartDate = util.get_AppointDay_Date("yyyyMMdd", -7);
                	String sEndDate = util.get_AppointDay_Date("yyyyMMdd", 0);
             		rsFileStatus = CaptureBean.get_FILESTATUS_Log(MerchantID,sStartDate,sEndDate);
                 	//****資料置放於Session內再forward到UI
             		Hashtable hashQuery = new Hashtable();
                	hashQuery.put("BatchContent", "LIST");//批次請款用LIST:顯示資料;RESULT:顯示結果
                	//將資料放入session內
                	hashData.put("DATALIST", arrayData); // 請款資料
                	hashData.put("QUERY", hashQuery); // 查詢請款資料
                	hashData.put("FILESTATUS", rsFileStatus); // 查詢批次請款執行狀態資料
                	if (session.getAttribute("CaptureBatchData") != null) session.removeAttribute("CaptureBatchData");
                	session.setAttribute("CaptureBatchData", hashData);
                    LogMemo = "INSERT INTO Batch_CAPTURE..失敗";
                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "失敗", LogMemo);
                    log_user.debug(LogData);
                    Forward = "./Merchant_Capture_List_Batch.jsp";
                    request.getRequestDispatcher(Forward).forward(request, response);
                    return;
             	}
             	//
            	/***************
            	 ** CALL Store PROCEDURE 開始執行整批請款作業
            	 * 1.UPDATE BILLING
            	 * 2.UPDATE BALANCE
            	 * 3.INSERT CAPTURE
            	 * * */
             	Connection conn=null;
             	DBBean =new DataBaseBean2();DBBean.ClearSQLParam();
            	sSQLSB = new StringBuffer();
            	sSQLSB.append("{call SP_BATCH_CAPTURE (?, ?, ?)}");/*FILE_NAME,MERCHANTID,FILE_BATCHNO*/
            	CallableStatement stmt = null;
           	    try{
           	    	conn = DBBean.getConn();
           	    	System.out.println("Creating statement...");
           	    	stmt = conn.prepareCall(sSQLSB.toString());
           	    	stmt.setString(1, sFILE_NAME);
           	    	stmt.setString(2, MerchantID);
           	    	stmt.setString(3, sBatchNo);
           	        // Because parameter is OUT so register it
           	        //stmt.registerOutParameter(2, java.sql.Types.VARCHAR);
           	        //Use execute method to run stored procedure.
           	        System.out.println("Executing stored procedure..." );
           	        stmt.execute();
                    //Retrieve employee name with getXXX method
           	        //String empName = stmt.getString(2);
           	        stmt.close();
           	        conn.close();
           	    }catch(SQLException se) {
           	        //Handle errors for JDBC
           	        se.printStackTrace();
           	    }catch(Exception ex) {
           	        //Handle errors for Class.forName
           	        ex.printStackTrace();
           	    }finally{
           	      //finally block used to close resources
           	      try{
           	         if(stmt!=null)
           	            stmt.close();
           	      }catch(SQLException se2){
           	      }// nothing we can do
           	      try{
           	         if(conn!=null)
           	            conn.close();
           	      }catch(SQLException se){
           	         se.printStackTrace();
           	      }//end finally try
           	   }//end try
            	hashData.clear();
            	
            	//取得執行結果From FILESTATUS TABLE
             	DBBean =new DataBaseBean2();DBBean.ClearSQLParam();
            	sSQLSB = new StringBuffer();
            	sSQLSB.append("SELECT * FROM FILESTATUS WHERE");
            	sSQLSB.append(" FILE_NAME=? AND FILE_ATTRIBUTE='RF'");
            	sSQLSB.append(" AND MERCHANT_ID=? AND FILE_DATE=? AND FILE_BATCHNO=? AND ROWNUM=1");
             	DBBean.AddSQLParam(emDataType.STR, sFILE_NAME); //FILE_NAME
             	DBBean.AddSQLParam(emDataType.STR, MerchantID); //MERCHANT_ID
             	DBBean.AddSQLParam(emDataType.STR, sToday); //FILE_DATE
             	DBBean.AddSQLParam(emDataType.STR, sBatchNo); //FILE_BATCHNO
             	rs = DBBean.QuerySQLByParam(sSQLSB.toString());
            	int iCountOK00=0,iCountOK01=0,iCountFail00=0,iCountFail01=0;
            	double dSumOK00=0,dSumOK01=0,dSumFail00=0,dSumFail01=0;
             	if(rs != null) {
                 	Hashtable dr = (Hashtable)rs.get(0);
                	iCountOK00 = Util.objToInt(dr.get("PURCHASE_SUCCESS_COUNT"));
                	dSumOK00 = Util.objToDouble(dr.get("PURCHASE_SUCCESS_AMOUNT"));
                	iCountOK01 = Util.objToInt(dr.get("REFUND_SUCCESS_COUNT"));
                	dSumOK01 = Util.objToDouble(dr.get("REFUND_SUCCESS_AMOUNT"));
                	iCountFail00 = Util.objToInt(dr.get("PURCHASE_REJECT_COUNT"));
                	dSumFail00 = Util.objToDouble(dr.get("PURCHASE_REJECT_AMOUNT"));
                	iCountFail01 = Util.objToInt(dr.get("REFUND_REJECT_COUNT"));
                	dSumFail01 = Util.objToDouble(dr.get("REFUND_REJECT_AMOUNT"));
                	hashData.put("Message", "");
             	}
             	else {
             		hashData.put("Message", "取得執行結果有誤，找不到執行紀錄，FILE_NAME:"+sFILE_NAME+",BatchNo:"+sBatchNo);
             	}
            	
            	ArrayList<Hashtable<String,String>> show_content = new ArrayList<Hashtable<String,String>>();
            	Hashtable<String,String> content = new Hashtable<String,String> ();
            	content.put("TRANSCODE","00");
            	content.put("CNT",iCountOK00 +"");
            	content.put("TOTAL_AMT",dSumOK00 +"");
            	show_content.add(content);
            	content = new Hashtable<String,String> ();
            	content.put("TRANSCODE","01");
            	content.put("CNT",iCountOK01 +"");
            	content.put("TOTAL_AMT",dSumOK01 +"");
            	show_content.add(content);
            	content = new Hashtable<String,String> ();
            	content.put("TRANSCODE","00FAIL");
            	content.put("CNT",iCountFail00 +"");
            	content.put("TOTAL_AMT",dSumFail00 +"");
            	show_content.add(content);
            	content = new Hashtable<String,String> ();
            	content.put("TRANSCODE","01FAIL");
            	content.put("CNT",iCountFail01 +"");
            	content.put("TOTAL_AMT",dSumFail01 +"");
            	show_content.add(content);

            	//1星期內的批次請款紀錄
            	String sStartDate = util.get_AppointDay_Date("yyyyMMdd", -7);
            	String sEndDate = util.get_AppointDay_Date("yyyyMMdd", 0);
         		rsFileStatus = CaptureBean.get_FILESTATUS_Log(MerchantID,sStartDate,sEndDate);
             	//假如有錯誤，則將錯誤的匯總訊息顯示於畫面上
             	ArrayList rsBatchFailGroup = new ArrayList();
             	if(iCountFail00 > 0 || iCountFail01 > 0) {
                	DBBean =new DataBaseBean2(); DBBean.ClearSQLParam();
                	sSQLSB = new StringBuffer();
                	sSQLSB.append("SELECT TRANSDATE,FEEDBACKMSG,COUNT(1) AS CNT,BALANCEAMT FROM BATCH_CAPTURE ");
                	sSQLSB.append(" WHERE FILESTATUS_ROWID=? AND MERCHANTID=? AND FEEDBACKCODE<>'000' ");
                	sSQLSB.append(" GROUP BY TRANSDATE,BALANCEAMT,FEEDBACKCODE,FEEDBACKMSG");
                 	DBBean.AddSQLParam(emDataType.STR, sFILESTATUS_REFKEY); //1
                 	DBBean.AddSQLParam(emDataType.STR, MerchantID); //2
                 	rsBatchFailGroup = DBBean.QuerySQLByParam(sSQLSB.toString());
                 	if(rsBatchFailGroup == null) rsBatchFailGroup = new ArrayList();
             	}

            	Hashtable hashQuery = new Hashtable();
         		hashData.put("SUBMITBATCH_DISABLE", "Y");
            	hashQuery.put("BatchContent", "RESULT");//批次請款用LIST:顯示資料;RESULT:顯示結果
            	hashData.put("FILESTATUS", rsFileStatus); // 查詢請款資料
            	hashData.put("BatchFailGroup", rsBatchFailGroup); // 假如有錯誤，則將錯誤的匯總訊息顯示於畫面上
            	//將資料放入session內
            	hashData.put("DATALIST", show_content); // 請款資料
            	hashData.put("QUERY", hashQuery); // 查詢請款資料
            	if (session.getAttribute("CaptureBatchData") != null) session.removeAttribute("CaptureBatchData");
            	session.setAttribute("CaptureBatchData", hashData);
                LogMemo = "UI批次請款查詢共"+String.valueOf(arrayData.size())+"筆";
            	arrayData.clear();arrayData=null;
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "成功", LogMemo);
                log_user.debug(LogData);
                Forward = "./Merchant_Capture_List_Batch.jsp";
            }
            

            if (Message.length()>0)
            {
                LogMemo = Message;
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                log_user.debug(LogData);
            }

            if (ForwardFlag)
            {
                System.out.println("Forward=" + Forward);
                request.getRequestDispatcher(Forward).forward(request, response);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log_systeminfo.debug("--MerchantCaptureCtl--"+e.toString());
            request.setAttribute("errMsg", e.toString());
            request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
        }
        //20130703 Jason finally close SysBean DB connection
        finally{
        	try{
        		sysBean.close();
        	}
        	catch (Exception e){
        	}
        }
    }

    /**
     ** 購貨請款交易 Capture
     * @param Hashtable hashCarpture 欲處理請款資料
     */
    //private void Capture(DataBaseBean SysBean, Hashtable hashCarpture) // 原本的回傳值為void
    //20150910 Jason BatchPmtID 改為區域變數 因BatchPmtID為此方法產生所以增加回傳BatchPmtID
    //TESTCASE-013
    private String Capture(DataBaseBean2 sysBean, Hashtable hashCarpture,Hashtable hashMerchant)
    {
    //20150910 Jason區域UserBean
    	UserBean UserBean = new UserBean();
        boolean CommitFlag = true;
    //20150910 Jason增加BatchPmtID變數        
        String BatchPmtID = "";
        try
        {
        	//取Universally Unique Identifier
            UUID uuid = new UUID();
            BatchPmtID = uuid.toString().toUpperCase();
//            UserBean UserBean = new UserBean();
            MerchantCaptureBean CaptureBean = new MerchantCaptureBean();
            //取檢核成功的 HashTable
            Hashtable hashSuccessCapture = (Hashtable) hashCarpture.get("CaptureSuccessData");            
            if (hashSuccessCapture == null)
            {
                hashSuccessCapture = new Hashtable();
            }
            //取檢核失敗的HashTable
            Hashtable hashFailCapture = (Hashtable) hashCarpture.get("CaptureFailData");
            if (hashFailCapture == null)
            {
                hashFailCapture = new Hashtable();
            }
            //處理檢核成功的部份一筆一筆做
            for (int i = 0; i < hashSuccessCapture.size(); i++)
            {
//                DataBaseBean DataBaseUpdateBean = new DataBaseBean();
//                DataBaseUpdateBean.setAutoCommit(false);
            //取回各欄位必要值
                String SumCaptureAmt = "0"; // 購貨請款金額統計
                String SumRefundCaptureAmt = "0"; // 退貨請款金額統計
                String TmpTransDate = UserBean.get_TransDate("yyyy/MM/dd HH:mm:ss");
                Hashtable hashData = (Hashtable) hashSuccessCapture.get(String.valueOf(i));
                String MerchantID = hashData.get("MERCHANTID").toString();
                String SubMID = hashData.get("SUBMID").toString();
                String TerminalID = hashData.get("TERMINALID").toString();
                String AcquirerID = hashData.get("ACQUIRERID").toString();
                String OrderID = hashData.get("ORDERID").toString();
                String Sys_OrderID = hashData.get("SYS_ORDERID").toString();
                String Card_Type = hashData.get("CARD_TYPE").toString();
                String PAN = hashData.get("PAN").toString();
                String ExpireDate = hashData.get("EXPIREDATE").toString();
                String TransCode = hashData.get("TRANSCODE").toString();
                String TransDate = hashData.get("TRANSDATE").toString();
                String TransTime = hashData.get("TRANSTIME").toString();
                String ApproveCode = hashData.get("APPROVECODE").toString();
                String ResponseCode = hashData.get("RESPONSECODE").toString();
                String ResponseMsg = hashData.get("RESPONSEMSG").toString();
                String CurrencyCode = hashData.get("CURRENCYCODE").toString();
                String CaptureAmt = hashData.get("TOCAPTUREAMT").toString();
                //00購貨計算請款金額
                if (TransCode.equalsIgnoreCase("00"))
                {
                    SumCaptureAmt = String.valueOf(Double.parseDouble(SumCaptureAmt) + Double.parseDouble(CaptureAmt));
                }
                //01退貨計算退貨金額
                if (TransCode.equalsIgnoreCase("01"))
                {
                    SumRefundCaptureAmt = String.valueOf(Double.parseDouble( SumRefundCaptureAmt) +
                                                         Double.parseDouble(CaptureAmt));
                }
                //繼續取欄位值
                String CaptureDate = TmpTransDate;
                String UserDefine = hashData.get("USERDEFINE").toString();
                String BatchNo = hashData.get("BATCHNO").toString();
                String CaptureFlag = "0";
                String ProcessDate = TmpTransDate;
                String Enrty_Mode = hashData.get("ENTRY_MODE").toString();
                String Condition_Code = hashData.get("CONDITION_CODE").toString();
                String ECI = hashData.get("ECI").toString();
                String CAVV = hashData.get("CAVV").toString();
                String TransMode = hashData.get("TRANSMODE").toString();
                String InstallType = hashData.get("INSTALLTYPE").toString();
                String Install = hashData.get("INSTALL").toString();
                String FirstAmt = hashData.get("FIRSTAMT").toString();
                String EachAmt = hashData.get("EACHAMT").toString();
                String FEE = hashData.get("FEE").toString();
                String RedemType = hashData.get("REDEMTYPE").toString();
                String RedemUsed = hashData.get("REDEMUSED").toString();
                String RedemBalance = hashData.get("REDEMBALANCE").toString();
                String CreditAmt = hashData.get("CREDITAMT").toString();
                String BillMessage = hashData.get("BILLMESSAGE").toString();
                String SysTraceNo = hashData.get("SYSTRACENO").toString();
                String FeeBackCode = " ";
                String FeeBackMsg = " ";
                String FeeBackDate = "";
                String DueDate = hashData.get("CAPTUREDDEALLINE").toString();
                String TransAmt = hashData.get("TRANSAMT").toString();
                String BalanceAmt = hashData.get("BALANCEAMT").toString();
                String ExtenNo = hashData.get("EXTENNO").toString();
                String RRN = hashData.get("RRN").toString();
                String MTI = hashData.get("MTI").toString();
                String XID = hashData.get("XID").toString();
                String SocialID =  hashData.get("SOCIALID").toString();
                String ReauthFlag =  hashData.get("REAUTH_FLAG").toString();
                String ExceptFlag =  "";
                String AUTH_SRC_CODE = hashData.get("AUTH_SRC_CODE").toString(); //20220210
                String BatchTxMsg = "";
                String BatchResponse = "";
//                20160112 Jason Capture檢核及insert搬到最後面去做
//                String permit_partial_capture = (null==hashMerchant.get("PERMIT_PARTIAL_CAPTURE")?"":hashMerchant.get("PERMIT_PARTIAL_CAPTURE").toString());
//                if("N".equals(permit_partial_capture)){
//                checkCapturedFlag = CaptureBean.CheckCapturedFlag(SysBean, MerchantID, SubMID, Sys_OrderID);
//                }
//                
//                if (checkCapturedFlag||!UserBean.insert_Capture(SysBean, MerchantID, SubMID, TerminalID, AcquirerID, OrderID,
//                                             Sys_OrderID, Card_Type, PAN, ExpireDate, TransCode, TransDate,
//                                             TransTime, ApproveCode, ResponseCode, ResponseMsg, CurrencyCode,
//                                             CaptureAmt, CaptureDate, UserDefine, BatchNo, CaptureFlag,
//                                             ProcessDate, Enrty_Mode, Condition_Code, ECI, CAVV, TransMode,
//                                             InstallType, Install, FirstAmt, EachAmt, FEE, RedemType,
//                                             RedemUsed, RedemBalance, CreditAmt, BillMessage, FeeBackCode,
//                                             FeeBackMsg, FeeBackDate, DueDate, TransAmt, SysTraceNo,ExtenNo,
//                                             RRN,MTI,XID,SocialID,ReauthFlag,ExceptFlag))
//                {
//                //如果寫入失敗 CommitFlag設為 false
//                    CommitFlag = false;
//                }
                //計算BalanceAmt = 原始BalanceAmt - 本次請款金額
                BalanceAmt = String.valueOf(Double.parseDouble(BalanceAmt) - Double.parseDouble(CaptureAmt));
                //20150910 Jason更新billing如果寫入capture失敗就不做了
                System.out.println("Capture UpdBilling Start MercahntID:"+MerchantID+" SysoOrderid:"+Sys_OrderID+" BatchPmtID:"+BatchPmtID);
                if (CommitFlag && !CaptureBean.update_BillingeNet(sysBean, MerchantID, SubMID, Sys_OrderID, TransCode, BalanceAmt))
                {
                    CommitFlag = false;
                }                               
                System.out.println("Capture GetBalance Start MercahntID:"+MerchantID+" SysoOrderid:"+Sys_OrderID+" BatchPmtID:"+BatchPmtID);
                Hashtable hashBalanceData = UserBean.get_Balance(sysBean, MerchantID, SubMID, OrderID);                 
                String BalCaptureAmt = hashBalanceData.get("CAPTUREAMT").toString();
                String BalRefundCaptureAmt = hashBalanceData.get("REFUNDCAPTUREAMT").toString();

                if (TransCode.equalsIgnoreCase("00"))
                {
                    // 購貨
                    BalCaptureAmt = String.valueOf(Double.parseDouble(BalCaptureAmt) + Double.parseDouble(SumCaptureAmt)); // 請款金額
                }

                if (TransCode.equalsIgnoreCase("01"))
                {
                    // 退貨
                    BalRefundCaptureAmt = String.valueOf(Double.parseDouble(BalRefundCaptureAmt) + Double.parseDouble(SumRefundCaptureAmt)); // 退款金額
                }

                String BalBalanceAmt = hashBalanceData.get("BALANCEAMT").toString();
                BalBalanceAmt = String.valueOf(Double.parseDouble(BalBalanceAmt) - Double.parseDouble(SumCaptureAmt));

                String TmpCaptureDate = TmpTransDate;
                
                // 原本的判斷式 沒有CommitFlag
                //if (!UserBean.update_Balance(SysBean, MerchantID, SubMID, OrderID, "", "", BalCaptureAmt,
                //        TmpCaptureDate, "", "", BalRefundCaptureAmt, TmpCaptureDate, BalBalanceAmt)) {
                
                //20150910 Jason 如果 commitFlag = false 就不要做了 因為要rollback
                System.out.println("Capture update_Balance Start MercahntID:"+MerchantID+" SysoOrderid:"+Sys_OrderID+" BatchPmtID:"+BatchPmtID);
                if (CommitFlag && !UserBean.update_Balance(sysBean, MerchantID, SubMID, OrderID, "", "", BalCaptureAmt,
                                             TmpCaptureDate, "", "", BalRefundCaptureAmt, TmpCaptureDate, BalBalanceAmt)) {
                    CommitFlag = false;
                }                
               //20160112 Jason Insert Capture檢核 如果前述步驟即已失敗就不做insert capture了
                boolean checkCapturedFlag = false;
                System.out.println("Capture insert_Capture Start MercahntID:"+MerchantID+" SysoOrderid:"+Sys_OrderID+" BatchPmtID:"+BatchPmtID);
                if(CommitFlag){
                    //String permit_partial_capture = (null==hashData.get("PERMIT_PARTIAL_CAPTURE")?"":hashMerchant.get("PERMIT_PARTIAL_CAPTURE").toString());                
                    //if("N".equals(permit_partial_capture)){
                    //checkCapturedFlag = CaptureBean.CheckCapturedFlag(SysBean, MerchantID, SubMID, Sys_OrderID);
                    String transamt = hashData.get("TRANSAMT").toString();
                    System.out.println("Capture insert_Capture Start MercahntID:"+MerchantID+" SysoOrderid:"+Sys_OrderID+" BatchPmtID:"+BatchPmtID);
                    ArrayList sumcaptureamt = CaptureBean.SumCaptured(sysBean, MerchantID, SubMID, Sys_OrderID);
                    System.out.println("Get Capture AMT MercahntID:"+MerchantID+" SysoOrderid:"+Sys_OrderID+" BatchPmtID:"+BatchPmtID);
                    if(null != sumcaptureamt){
                    Hashtable hashtemp = (Hashtable)sumcaptureamt.get(0);
                    int trans_amt = Integer.parseInt(transamt);
                    int captured_amt = Integer.parseInt("".equals(hashtemp.get("TCAMT").toString().trim())?"0":hashtemp.get("TCAMT").toString());
                    if(captured_amt >= trans_amt){
                    	System.out.println("Capture captured_amt >= trans_amt rollback"+" MercahntID:"+MerchantID+" SysoOrderid:"+Sys_OrderID+" BatchPmtID:"+BatchPmtID);
                    	checkCapturedFlag = true;
                    	}
                    }
                    //20220210 ADD AUTH_SRC_CODE
                    if (checkCapturedFlag||!UserBean.insert_Capture(sysBean, MerchantID, SubMID, TerminalID, AcquirerID, OrderID,
                                                 Sys_OrderID, Card_Type, PAN, ExpireDate, TransCode, TransDate,
                                                 TransTime, ApproveCode, ResponseCode, ResponseMsg, CurrencyCode,
                                                 CaptureAmt, CaptureDate, UserDefine, BatchNo, CaptureFlag,
                                                 ProcessDate, Enrty_Mode, Condition_Code, ECI, CAVV, TransMode,
                                                 InstallType, Install, FirstAmt, EachAmt, FEE, RedemType,
                                                 RedemUsed, RedemBalance, CreditAmt, BillMessage, FeeBackCode,
                                                 FeeBackMsg, FeeBackDate, DueDate, TransAmt, SysTraceNo,ExtenNo,
                                                 RRN,MTI,XID,SocialID,ReauthFlag,ExceptFlag,AUTH_SRC_CODE))
                    	{
                    		CommitFlag = false;
                    	}
                	}
                if (CommitFlag)
                {
                    // 更新成功
                    CommitFlag = sysBean.commit();

                    if (CommitFlag)
                    {
                        BatchTxMsg = "SUCCESS";
                        BatchResponse = "待處理";
                    }
                    else
                    {
                        BatchTxMsg = "FAIL";
                        BatchResponse = "請款更新失敗";
                    }
                }
                else
                {
                    BatchTxMsg = "FAIL";
                    BatchResponse = "請款更新失敗";
                    sysBean.setRollBack();
                }
               // SysBean.close();
                if (!CommitFlag)
                {
                    // 失敗
                    BatchTxMsg = "FAIL";
                    BatchResponse = "請款更新失敗";
                    CaptureBean.update_BillingeFail(sysBean, MerchantID, SubMID, Sys_OrderID, TransCode,
                                          "請款更新失敗", CaptureAmt, DueDate, CaptureDate.substring(0, 10));
                }

                String ReversalFlag = hashData.get("REVERSALFLAG").toString();
                String EMail = hashData.get("EMAIL").toString();
                String Entry_Mode = hashData.get("ENTRY_MODE").toString();
                String TransType = hashData.get("TRANSTYPE").toString();
                String FeedbackCode = "";
                String FeedbackMsg = "";
                String FeedbackDate = "";
                String BatchDate = "";
                String BatchHead = "";
                String BatchType = "Capture";
                String BatchTerminalID = TerminalID;
                String BatchSysorderID = OrderID;
                String BatchTxDate = TransDate;
                String BatchTxTime = TransTime;
                String BatchTxApproveCode = ApproveCode.trim();
                String BatchTransCode = TransCode;
                String BatchTxAmt = CaptureAmt;
//                DataBaseBean DataBaseBean = new DataBaseBean();
                //20220210 ADD AUTH_SRC_CODE
                if(UserBean.insert_Batch(sysBean, MerchantID, SubMID, TerminalID, AcquirerID, OrderID,
                                      Sys_OrderID, Card_Type, PAN, ExtenNo, ExpireDate, TransCode,
                                      ReversalFlag, TransDate, TransTime, CurrencyCode, TransAmt,
                                      ApproveCode, ResponseCode, ResponseMsg, BatchNo, UserDefine,
                                      EMail, MTI, RRN, SocialID, Entry_Mode, Condition_Code,
                                      TransMode, TransType, ECI, CAVV, XID, InstallType, Install,
                                      FirstAmt, EachAmt, FEE, RedemType, RedemUsed, RedemBalance,
                                      CreditAmt, BillMessage, BalanceAmt, CaptureAmt, CaptureDate,
                                      FeedbackCode, FeedbackMsg, FeedbackDate, DueDate, BatchPmtID,
                                      BatchDate, BatchHead, BatchType, BatchTerminalID, BatchSysorderID,
                                      BatchTxDate, BatchTxTime, BatchTxApproveCode, BatchTransCode,
                                      BatchTxAmt, BatchTxMsg, BatchResponse, SysTraceNo, "",AUTH_SRC_CODE))
                {
                CommitFlag = true;
	            }
	            else
	            {
	            CommitFlag = false;
	            }
        //        DataBaseBean.close();
                
                if(CommitFlag)
                {
                	sysBean.commit();
                }
                else
                {
                	sysBean.setRollBack();
                }
            }

            for (int i = 0; i < hashFailCapture.size(); i++)
            {
                Hashtable hashData = (Hashtable) hashFailCapture.get(String.valueOf(i));
                String MerchantID = hashData.get("MERCHANTID").toString();
                String SubMID = hashData.get("SUBMID").toString();
                String TransCode = hashData.get("TRANSCODE").toString();
                String Sys_OrderID = hashData.get("SYS_ORDERID").toString();
                String CaptureAmt = hashData.get("TOCAPTUREAMT").toString();
                String CaptureMessage = hashData.get("CHECKRESPONSE").toString();
                String CaptureDate = UserBean.get_TransDate("yyyy/MM/dd HH:mm:ss");
                String Due_Date = hashData.get("CAPTUREDDEALLINE").toString();
                
                //20130726 Jason Capture Fail 資料更新 update確認 update_billing updatebatch成功才commit;
                if(CaptureBean.update_BillingeFail(sysBean, MerchantID, SubMID, Sys_OrderID, TransCode, CaptureMessage,
                                                CaptureAmt, Due_Date, CaptureDate.substring(0, 10)))
                {
                	CommitFlag = true;
                }
                else
                {
                	CommitFlag = false;
                }
                
                String TerminalID = hashData.get("TERMINALID").toString();
                String AcquirerID = hashData.get("ACQUIRERID").toString();
                String OrderID = hashData.get("ORDERID").toString();
                String Card_Type = hashData.get("CARD_TYPE").toString();
                String PAN = hashData.get("PAN").toString();
                String ExpireDate = hashData.get("EXPIREDATE").toString();
                String TransDate = hashData.get("TRANSDATE").toString();
                String TransTime = hashData.get("TRANSTIME").toString();
                String ApproveCode = hashData.get("APPROVECODE").toString();
                String ResponseCode = hashData.get("RESPONSECODE").toString();
                String ResponseMsg = hashData.get("RESPONSEMSG").toString();
                String CurrencyCode = hashData.get("CURRENCYCODE").toString();
                String UserDefine = hashData.get("USERDEFINE").toString();
                String BatchNo = hashData.get("BATCHNO").toString();
                String Enrty_Mode = hashData.get("ENTRY_MODE").toString();
                String Condition_Code = hashData.get("CONDITION_CODE").toString();
                String ECI = hashData.get("ECI").toString();
                String CAVV = hashData.get("CAVV").toString();
                String TransMode = hashData.get("TRANSMODE").toString();
                String InstallType = hashData.get("INSTALLTYPE").toString();
                String Install = hashData.get("INSTALL").toString();
                String FirstAmt = hashData.get("FIRSTAMT").toString();
                String EachAmt = hashData.get("EACHAMT").toString();
                String FEE = hashData.get("FEE").toString();
                String RedemType = hashData.get("REDEMTYPE").toString();
                String RedemUsed = hashData.get("REDEMUSED").toString();
                String RedemBalance = hashData.get("REDEMBALANCE").toString();
                String CreditAmt = hashData.get("CREDITAMT").toString();
                String BillMessage = hashData.get("BILLMESSAGE").toString();
                String SysTraceNo = hashData.get("SYSTRACENO").toString();
                String TransAmt = hashData.get("TRANSAMT").toString();
                String BalanceAmt = hashData.get("BALANCEAMT").toString();
                String BatchTxMsg = "FAIL";
                String BatchResponse = CaptureMessage;
                String ExtenNo = hashData.get("EXTENNO").toString();
                String ReversalFlag = hashData.get("REVERSALFLAG").toString();
                String EMail = hashData.get("EMAIL").toString();
                String MTI = hashData.get("MTI").toString();
                String RRN = hashData.get("RRN").toString();
                String SocialID = hashData.get("SOCIALID").toString();
                String Entry_Mode = hashData.get("ENTRY_MODE").toString();
                String TransType = hashData.get("TRANSTYPE").toString();
                String XID = hashData.get("XID").toString();
                String AUTH_SRC_CODE = hashData.get("AUTH_SRC_CODE").toString(); //20220210
                String FeedbackCode = "";
                String FeedbackMsg = "";
                String FeedbackDate = "";
                String BatchDate = "";
                String BatchHead = "";
                String BatchType = "";
                String BatchTerminalID = TerminalID;
                String BatchSysorderID = OrderID;
                String BatchTxDate = TransDate;
                String BatchTxTime = TransTime;
                String BatchTxApproveCode = ApproveCode.trim();
                String BatchTransCode = TransCode;
                String BatchTxAmt = CaptureAmt;

                // DataBaseBean DataBaseBean = new DataBaseBean();
                //20200210 ADD AUTH_SRC_CODE
                if(UserBean.insert_Batch(sysBean, MerchantID, SubMID, TerminalID, AcquirerID, OrderID,
                                      Sys_OrderID, Card_Type, PAN, ExtenNo, ExpireDate, TransCode,
                                      ReversalFlag, TransDate, TransTime, CurrencyCode, TransAmt,
                                      ApproveCode, ResponseCode, ResponseMsg, BatchNo, UserDefine,
                                      EMail, MTI, RRN, SocialID, Entry_Mode, Condition_Code,
                                      TransMode, TransType, ECI, CAVV, XID, InstallType, Install,
                                      FirstAmt, EachAmt, FEE, RedemType, RedemUsed, RedemBalance,
                                      CreditAmt, BillMessage, BalanceAmt, CaptureAmt, CaptureDate,
                                      FeedbackCode, FeedbackMsg, FeedbackDate, Due_Date, BatchPmtID,
                                      BatchDate, BatchHead, BatchType, BatchTerminalID,  BatchSysorderID,
                                      BatchTxDate, BatchTxTime, BatchTxApproveCode, BatchTransCode,
                                      BatchTxAmt, BatchTxMsg, BatchResponse, SysTraceNo, "",AUTH_SRC_CODE))
                {
                	CommitFlag = true;
                }
                else
                {
                	CommitFlag = false;
                }
                // DataBaseBean.close();                           
                if(CommitFlag)
                {
                	sysBean.commit();
                }
                else
                {
                	sysBean.setRollBack();
                }
              //20130726 Jason Capture Fail 資料更新 update確認 update_billing updatebatch成功才commit;
            }
        //20150910 Jason 回傳批次代號供外部使用
        return BatchPmtID;
        }
        catch (Exception e)
        {
        	//20130726 Jason Exception RollBack
        	sysBean.setRollBack();
            e.printStackTrace();
            log_systeminfo.debug(e.toString());
        }
        //20130726 Jason 
        finally
        {        	
        	try
        	{
        	//20130726 Jason 此行 Exception 時會被執行~ 
            //SysBean.commit();
            sysBean.close();
        	}
        	catch (Exception e)
        	{
        	
        	}
        }
        //20150916 exception 回傳批次代號
		return BatchPmtID;
    }
}
