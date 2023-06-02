/************************************************************
 * <p>#File Name:       MerchantCaptureLogCtl.java  </p>
 * <p>#Description:                 </p>
 * <p>#Create Date:     2007/10/03      </p>
 * <p>#Company:         cybersoft       </p>
 * <p>#Notice:                      </p>
 * @author      Sid Chai
 * @since       SPEC version
 * @version 0.1 2007/10/03  SidChai
 * @modify              2007/10/03      SidChai
 ************************************************************/
package com.cybersoft.merchant.ctl;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.cybersoft.bean.UserBean;
/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft.merchant.bean.MerchantCaptureLogBean;
import com.fubon.security.filter.SecurityTool;
import com.cybersoft.bean.SessionControlBean;
import com.cybersoft.bean.createReport;
import com.cybersoft.bean.LogUtils;
/**
 * <p>控制登入系統的Servlet</p>
 * @version 0.1 2007/09/19  Shiley Lin
 */
public class MerchantCaptureLogCtl extends HttpServlet
{
    private static final String CONTENT_TYPE = "text/html; charset=Big5";
    private String Forward = ""; // 網頁轉址
    private String Message = ""; // 顯示訊息
    java.util.Date nowdate;
    public static final LogUtils log_user = new LogUtils("user");
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    public UserBean UserBean = new UserBean();
    public DataBaseBean2 sysBean = new DataBaseBean2();

    String LogUserName = "";
    String LogFunctionName = "請款交易查詢";
    String LogStatus = "成功";
    String LogMemo = "";
    String LogData = "";
    String LogMerchantID = "";

    public void init()
    {
    }
    
    public String panenCode(String strp)
    {
    	String temppan = strp.substring(0,8);
    	temppan = rpad(temppan,"*", strp.length() - 10);
    	temppan = temppan + strp.substring(temppan.length(),strp.length());
    	
    	return temppan;
    }
    
    public String rpad(String strp,String chr,int cnt) {
		if(strp==null)
			strp = "";
		String tmpstr = strp;
		
			for(int i=0;i<cnt;i++) {
				tmpstr += chr;
			}
			return tmpstr;
			
	} 
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        this.doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType(CONTENT_TYPE);
        request.setCharacterEncoding("BIG5");
        HttpSession session = request.getSession(true);
        SessionControlBean scb =new SessionControlBean();
        String errMessage="";
        int queryMax = 3000;
        try
        {
            scb = new SessionControlBean(session,request,response);
        }
        catch(UnsupportedOperationException E)
        {
            E.toString();
            String Action = (request.getParameter("Action") == null) ? "" : request.getParameter("Action");
            System.out.println("Action="+Action);
            if (Action.equalsIgnoreCase("OPEN"))
            {
                request.getRequestDispatcher("/Merchant_Close.jsp").forward(request, response);
            }
            else
            {
                request.getRequestDispatcher("/Merchant_Bye.jsp").forward(request, response);
            }

            return ;
        }
//        SessionContrl      ###########################################
        try
        {
            Hashtable hashSys = new Hashtable(); // 系統參數
            Hashtable hashMerUser = new Hashtable(); // 特店使用者
            Hashtable hashMerchant = new Hashtable(); // 特店主檔
            ArrayList arrayTerminal = new ArrayList(); // 端末機主檔
            //子特店清單
            ArrayList subMidList = new ArrayList();
          //是否為單一特店
            boolean isSignMer = false;
            Hashtable hashConfData = (Hashtable) session.getAttribute("SYSCONFDATA");
            if (hashConfData == null)
                hashConfData = new Hashtable();

            boolean Merchant_Current =  false;   // 特店狀態
            boolean Merchant_Permit =  false;   // 特店權限
            if (hashConfData.size()>0)
            {
                hashSys = (Hashtable)hashConfData.get("SYSCONF"); // 系統參數
                queryMax = Integer.parseInt( hashSys.get("MER_CAPTURE_QRY_QUANTITY").toString());//授權查詢最高筆數
                hashMerUser = (Hashtable)hashConfData.get("MERCHANT_USER"); // 特店使用者
                hashMerchant = (Hashtable)hashConfData.get("MERCHANT"); // 特店主檔
	              subMidList = (ArrayList) hashConfData.get("SUBMID");
	              isSignMer = subMidList != null && subMidList.size() > 1 ? false :  true;
                if (hashSys==null)
                    hashSys = new Hashtable();

                if (hashMerUser==null)
                    hashMerUser = new Hashtable();

                if (hashMerchant==null)
                    hashMerchant = new Hashtable();

                if (hashMerchant.size()>0)
                {
                    LogMerchantID = (String)hashMerUser.get("MERCHANT_ID");
                }

                arrayTerminal = (ArrayList)hashConfData.get("TERMINAL"); // 端末機主檔
                if (arrayTerminal==null)
                    arrayTerminal = new ArrayList();
            }

            // UserBean UserBean = new UserBean();
            System.out.println("<"+hashMerchant+">");
            Merchant_Current = UserBean.check_Merchant_Column(hashMerchant,"CURRENTCODE","B,C,D");  //  確認特店狀態
            Merchant_Permit = UserBean.check_Merchant_Column(hashMerchant,"PERMIT_INQUIRY_TX","Y");  //  確認特店權限
            String reportflag="";

            String MenuKey = (String)request.getParameter(MerchantMenuCtl.MENU_FORWARD_KEY);
            request.setAttribute(MerchantMenuCtl.MENU_FORWARD_KEY,MenuKey);
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
                        Forward = "./MerchantCaptureLogQuery.jsp";
                        String submid = String.valueOf(hashMerchant.get("SUBMID"));
                        submid = (request.getParameter("subMid") == null) ? submid : UserBean.trim_Data(request.getParameter("subMid"));
                        String merchantid = String.valueOf(hashMerchant.get("MERCHANTID"));
                        String startdate = (request.getParameter("Start_TransDate") == null) ? "" : UserBean.trim_Data(request.getParameter("Start_TransDate"));
                        String enddate = (request.getParameter("End_TransDate") == null) ? "" :  UserBean.trim_Data(request.getParameter("End_TransDate"));
                        String transcode = (request.getParameter("TransCode") == null) ? "" :  UserBean.trim_Data(request.getParameter("TransCode"));
                        String ordertype = (request.getParameter("OrderType") == null) ? "" :  UserBean.trim_Data(request.getParameter("OrderType"));
                        String orderid = (request.getParameter("OrderID") == null) ? "" :  UserBean.trim_Data(request.getParameter("OrderID"));
                        String authid = (request.getParameter("AuthID") == null) ? "" : UserBean.trim_Data(request.getParameter("AuthID"));
                        String terminalid = (request.getParameter("TerminalID") == null) ? "" :  UserBean.trim_Data(request.getParameter("TerminalID"));
                        String capturetype = (request.getParameter("CaptrueType") == null) ? "" : UserBean.trim_Data(request.getParameter("CaptrueType"));
                        String exceptFlag = (request.getParameter("ExceptFlag") == null) ? "ALL" : UserBean.trim_Data(request.getParameter("ExceptFlag"));
                        String checkpoint = (request.getParameter("checkpoint") == null) ? "" : UserBean.trim_Data(request.getParameter("checkpoint"));
                        String type = (request.getParameter("Type") == null) ? "" : UserBean.trim_Data(request.getParameter("Type"));
                        
                        // 請款交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150727  -- 修改開始 --
                        String transtype = (request.getParameter("TransType") == null) ? "" : UserBean.trim_Data(request.getParameter("TransType"));
                        // 請款交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150727  -- 修改結束 --
                        
                        String sysorderid = UserBean.trim_Data(request.getParameter("SysOrderId"));
                        String page = UserBean.trim_Data(request.getParameter("page_no"));
                        reportflag = UserBean.trim_Data(request.getParameter("ReportFlag"));
                        String detail = UserBean.trim_Data(request.getParameter("Detail"));
                        String rptname = UserBean.trim_Data(request.getParameter("RptName"));
                        String printtype = UserBean.trim_Data(request.getParameter("PrintType"));
                        String query_submid =  (request.getParameter("subMid") == null) ? "" : UserBean.trim_Data(request.getParameter("subMid"));
                        
                        request.setAttribute("page_no", page);
                        request.setAttribute("Start_TransDate", startdate);
                        request.setAttribute("End_TransDate", enddate);
                        request.setAttribute("TransCode", transcode);
                        request.setAttribute("OrderType", ordertype);
                        request.setAttribute("OrderId", orderid);
                        request.setAttribute("AuthID", authid);
                        request.setAttribute("TerminalID", terminalid);
                        request.setAttribute("CaptrueType", capturetype);
                        request.setAttribute("ExceptFlag", exceptFlag);
                        request.setAttribute("Type", type);
                        request.setAttribute("subMid", query_submid);
                        request.setAttribute("checkpoint", checkpoint);
                        
                        // 請款交易查詢 新增 搜尋條件 TransType by Jimmy Kang 20150727  -- 修改開始 --
                        request.setAttribute("TransType", transtype);
                        // 請款交易查詢 新增 搜尋條件 TransType by Jimmy Kang 20150727  -- 修改結束 --
                        
                        MerchantCaptureLogBean malb = new MerchantCaptureLogBean();
                        if (startdate != null && !startdate.equals("") && (reportflag == null ||
                            !reportflag.equalsIgnoreCase("ture")))
                        {
                            //有查詢條件
                            Forward = "./MerchantCaptureLogQueryResult.jsp";
                            int tempcon=0;
                            ArrayList capturelog =new ArrayList();
                            if(checkpoint.equals("")){
                            	 // get_CaptureLog_Count method 新增引數 TransType by Jimmy Kang 20150727
                          	     // 請款交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150727  -- 修改開始 --
                            	 capturelog = malb.get_CaptureLog_Count(sysBean, merchantid, submid, startdate, enddate, transcode, transtype,
                                        ordertype, orderid, authid, terminalid, capturetype, exceptFlag,type);
                            	 // 請款交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150727  -- 修改結束 --
                            	 tempcon=capturelog==null ||capturelog.size()==0?tempcon :Integer.parseInt(((Hashtable)capturelog.get(0)).get("COUNT").toString());
                            }
                            if(tempcon <= queryMax || checkpoint.equals("XXX") ){
                            	request.setAttribute("checkpoint", "XXX");
                            	// get_CaptureLog_List method 新增引數 TransType by Jimmy Kang 20150727
                         	    // 請款交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150727  -- 修改開始 --
	                            capturelog = malb.get_CaptureLog_List(sysBean, merchantid, submid, startdate, enddate, transcode, transtype,
	                                                                            ordertype, orderid, authid, terminalid, capturetype, exceptFlag,type);
	                            // 請款交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150727  -- 修改結束 --
                            }
                            session.removeAttribute("Capturelog");
//                            if(capturelog.size()> queryMax){
//                            	errMessage ="本次查詢結果已超過筆數上限(最大筆數:"+String.valueOf(queryMax)+")";
//                            }
                            session.setAttribute("Capturelog", capturelog);
                            LogMemo = "查詢明細資料";
                        }
                        else if (sysorderid != null && detail != null && detail.equalsIgnoreCase("true"))
                        {
                            Forward = "./MerchantCaptureLogQueryDetail.jsp";
                            merchantid = UserBean.trim_Data(request.getParameter("MerchantId"));
                            submid = UserBean.trim_Data(request.getParameter("SubMid"));
                            String rowid = UserBean.trim_Data(request.getParameter("RowId"));
                            Hashtable detail_log = malb.get_CaptureLog_View(sysBean, merchantid, submid, sysorderid, rowid);
                            session.setAttribute("DetailLog", detail_log);
                            LogMemo = "檢視"+sysorderid+"明細資料";
                        }
                        else if (reportflag != null && reportflag.equalsIgnoreCase("ture"))
                        {
                            boolean RowdataFlag = true;
                            if (printtype.equalsIgnoreCase("PDF")) {
                            	if (type.equalsIgnoreCase("TotalNet"))
                            		RowdataFlag = false;
                            	else
                            		 RowdataFlag = true;
                            }
                            // get_CaptureLog_List_rs method 新增引數 TransType by Jimmy Kang 20150727
                     	    // 請款交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150727  -- 修改開始 --
                            String sql = malb.get_CaptureLog_List_rs(sysBean, merchantid, submid, startdate,
                                                enddate, transcode, transtype, ordertype, orderid, authid, terminalid,
                                                capturetype, RowdataFlag, exceptFlag);
                            // 請款交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150727  -- 修改結束 --
                            
                            createReport cr = new createReport();
                            Hashtable field = new Hashtable();
                            field.put("startdate", startdate);
                            field.put("enddate", enddate);
                            String terminaltype = "";

                            if (terminalid.equalsIgnoreCase("ALL"))
                                terminaltype="全部";
                            else
                                terminaltype = terminalid;

                            field.put("terminaltype", terminaltype);
                            String exceptFlagDesc = "全部";
                            System.out.println("-----------exceptFlag1 ="+exceptFlag);
                            if (exceptFlag.equalsIgnoreCase("ALL")) exceptFlagDesc = "全部";
                            if (exceptFlag.equalsIgnoreCase("MERCHANT")) exceptFlagDesc = "特店請款";
                            if (exceptFlag.equalsIgnoreCase("ACQ")) exceptFlagDesc = "收單行補請款";
                            if (query_submid.equalsIgnoreCase("ALL")){
                            	query_submid = "全部";
                            }
                            
                            field.put("exceptflag", exceptFlagDesc);
                            //new add submid
                            field.put("subMid", query_submid);
                            field.put("isSubMid", isSignMer ? "N" : "Y");
                            if (type.equalsIgnoreCase("TotalNet"))
                            {
                                rptname = "MerchantCaptureLogReportNet.rpt";
                            }
                            else
                            {
                                rptname = "MerchantCaptureLogReport.rpt";
                            }
                            
                            // 請款交易查詢 新增 TransType by Jimmy Kang 20150727  -- 修改開始 --
                            String transtypeDesc = "";
                            if (transtype.equalsIgnoreCase("SSL"))
                            {
                            	transtypeDesc = "信用卡";
                            }
                            else if (transtype.equalsIgnoreCase("ALL"))
                            {
                            	transtypeDesc = "全部";
                            }
                            else
                            {
                            	transtypeDesc = transtype;
                            }
                            field.put("transtype", transtypeDesc);
                            // 請款交易查詢 新增 TransType by Jimmy Kang 20150727  -- 修改結束 --

                            System.out.println("rptname="+rptname);
                            cr.createPDF(sysBean,SecurityTool.output(sql), request, response, "/Report/" + rptname, field, printtype);
                            // malb.closeConn();
                            LogMemo = "以"+printtype+"格式匯出";
                        }
                        if (LogMemo.length()>0)
                        {
                            LogUserName = hashMerUser.get("USER_ID").toString() + "(" +
                                          hashMerUser.get("USER_NAME").toString() + ")";
                            LogData = UserBean.get_LogData(LogMerchantID,
                            LogUserName, LogFunctionName, LogStatus, LogMemo);
                            log_user.debug(LogData);
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
                    String CurrentCode = hashMerchant.get("CURRENTCODE").toString();
                    Message = UserBean.get_CurrentCode(CurrentCode);
                    Forward = "./Merchant_Response.jsp";
                    session.setAttribute("Message", Message);
                }
            }
            else
            {
                Message = "使用者無此權限請洽系統管理者";
                Forward = "./Merchant_NoUse.jsp";
            }

            if (Message.length()>0)
            {
                session.setAttribute("Message", Message);
                LogMemo = Message;
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "失敗", LogMemo);
                log_user.debug(LogData);
            }

            System.out.println("Forward="+Forward);
            if(reportflag==null||!reportflag.equalsIgnoreCase("ture"))
            {
            	if(errMessage.length()==0)
                    request.getRequestDispatcher(Forward).forward(request, response);
                else
                	{
                        Message = errMessage;
                        Forward = "./Merchant_Response.jsp";
                        session.setAttribute("Message", Message);
                		request.getRequestDispatcher(Forward).forward(request, response);
                	}
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            log_systeminfo.debug("--MerchantCaptureLogCtl--"+e.toString());
            request.setAttribute("errMsg",e.toString());
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
}
