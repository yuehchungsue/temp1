/************************************************************
 * <p>#File Name:       MerchantAuthLogCtl.java </p>
 * <p>#Description:                 </p>
 * <p>#Create Date:     2007/09/19      </p>
 * <p>#Company:         cybersoft       </p>
 * <p>#Notice:                      </p>
 * @author      Shirley Lin
 * @since       SPEC version
 * @version 0.1 2007/09/19  Shirley Lin
 * @modify              2007/09/27      SidChai
 ************************************************************/
package com.cybersoft.merchant.ctl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft.bean.LogUtils;
import com.cybersoft.bean.SessionControlBean;
import com.cybersoft.bean.UserBean;
import com.cybersoft.bean.createReport;
import com.cybersoft.merchant.bean.MerchantAuthLogBean;
import com.fubon.security.filter.SecurityTool;

/**
 * <p>控制登入系統的Servlet</p>
 * @version 0.1 2007/09/19  Shiley Lin
 */
public class MerchantAuthLogCtl extends HttpServlet
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
    String LogFunctionName = "授權交易查詢";
    String LogStatus = "成功";
    String LogMemo = "";
    String LogData = "";
    String LogMerchantID = "";

    public void init()
    {
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
        String errMessage="";
        int queryMax = 3000;
        SessionControlBean scb = new SessionControlBean();
        try
        {
            scb = new SessionControlBean(session, request, response);
            sysBean.setAutoCommit (false);
        }
        catch (UnsupportedOperationException E)
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
            return;
        }

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

            boolean Merchant_Current = false; // 特店狀態
            boolean Merchant_Permit = false; // 特店權限
            if (hashConfData.size() > 0)
            {
                hashSys = (Hashtable) hashConfData.get("SYSCONF"); // 系統參數
                hashMerUser = (Hashtable) hashConfData.get("MERCHANT_USER"); // 特店使用者
                hashMerchant = (Hashtable) hashConfData.get("MERCHANT"); // 特店主檔
                queryMax = Integer.parseInt( hashSys.get("MER_AUTH_QRY_QUANTITY").toString());//授權查詢最高筆數
	              subMidList = (ArrayList) hashConfData.get("SUBMID");
	              isSignMer = subMidList != null && subMidList.size() > 1 ? false :  true;
	              
                if (hashSys == null)
                    hashSys = new Hashtable();

                if (hashMerUser == null)
                    hashMerUser = new Hashtable();

                if (hashMerchant == null)
                    hashMerchant = new Hashtable();

                if (hashMerchant.size()>0)
                {
                    LogMerchantID = (String)hashMerUser.get("MERCHANT_ID");
                }

                arrayTerminal = (ArrayList) hashConfData.get("TERMINAL"); // 端末機主檔
                if (arrayTerminal == null)
                    arrayTerminal = new ArrayList();
            }

            // UserBean UserBean = new UserBean();
            Merchant_Current = UserBean.check_Merchant_Column(hashMerchant, "CURRENTCODE", "B,C,D"); //  確認特店狀態
            Merchant_Permit  = UserBean.check_Merchant_Column(hashMerchant, "PERMIT_INQUIRY_TX", "Y"); //  確認特店權限
            String reportflag = "";
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
                        Forward = "./MerchantAuthLogQuery.jsp";
                        String submid = String.valueOf(hashMerchant.get("SUBMID"));
                        //
                        submid = (request.getParameter("subMid") == null) ? submid : UserBean.trim_Data(request.getParameter("subMid"));
                        String merchantid = String.valueOf(hashMerchant.get("MERCHANTID"));
                        String startdate = (request.getParameter("Start_TransDate") == null) ? "" : UserBean.trim_Data(request.getParameter("Start_TransDate"));
                        String starttime = (request.getParameter("Start_TransHour") == null) ? "" : UserBean.trim_Data(request.getParameter("Start_TransHour"));
                        String enddate = (request.getParameter("End_TransDate") == null) ? "" : UserBean.trim_Data(request.getParameter("End_TransDate"));
                        String endtime = (request.getParameter("End_TransHour") == null) ? "" : UserBean.trim_Data(request.getParameter("End_TransHour"));
                        String transcode = (request.getParameter("TransCode") == null) ? "" : UserBean.trim_Data(request.getParameter("TransCode"));
                        String transstatus = (request.getParameter("TransStatus") == null) ? "" : UserBean.trim_Data(request.getParameter("TransStatus"));
                        String ordertype = (request.getParameter("OrderType") == null) ? "" : UserBean.trim_Data(request.getParameter("OrderType"));
                        String orderid = (request.getParameter("OrderID") == null) ? "" : UserBean.trim_Data(request.getParameter("OrderID"));
                        String authid = (request.getParameter("AuthID") == null) ? "" : UserBean.trim_Data(request.getParameter("AuthID"));
                        String terminalid = (request.getParameter("TerminalID") == null) ? "" : UserBean.trim_Data(request.getParameter("TerminalID"));
                        String capturetype = (request.getParameter("CaptrueType") == null) ? "" : UserBean.trim_Data(request.getParameter("CaptrueType"));
                        String rowid = (request.getParameter("RowId") == null) ? "" : UserBean.trim_Data(request.getParameter("RowId"));
                        String checkpoint = (request.getParameter("checkpoint") == null) ? "" : UserBean.trim_Data(request.getParameter("checkpoint"));
                        
                        // 授權交易查詢 新增 搜尋條件 TransType by Jimmy Kang 20150721  -- 修改開始 --
                        String transtype = (request.getParameter("TransType") == null) ? "" : UserBean.trim_Data(request.getParameter("TransType"));
                        // 授權交易查詢 新增 搜尋條件 TransType by Jimmy Kang 20150721  -- 修改結束 --
                        
                        String sysorderid = UserBean.trim_Data(request.getParameter("SysOrderId"));
                        String page = UserBean.trim_Data(request.getParameter("page_no"));
                        String detail = UserBean.trim_Data(request.getParameter("Detail"));
                        String query_submid =  (request.getParameter("subMid") == null) ? "" : UserBean.trim_Data(request.getParameter("subMid"));
                        reportflag = UserBean.trim_Data(request.getParameter("ReportFlag"));
                        String rptname = UserBean.trim_Data(request.getParameter("RptName"));
                        String printtype = UserBean.trim_Data(request.getParameter("PrintType"));
                        request.setAttribute("page_no", page);
                        request.setAttribute("Start_TransDate", startdate);
                        request.setAttribute("Start_TransHour", starttime);
                        request.setAttribute("End_TransDate", enddate);
                        request.setAttribute("End_TransHour", endtime);
                        request.setAttribute("TransCode", transcode);
                        request.setAttribute("TransStatus", transstatus);
                        request.setAttribute("OrderType", ordertype);
                        request.setAttribute("OrderId", orderid);
                        request.setAttribute("AuthID", authid);
                        request.setAttribute("TerminalID", terminalid);
                        request.setAttribute("CaptrueType", capturetype);
                        request.setAttribute("subMid", query_submid);
                        request.setAttribute("checkpoint", checkpoint);
                        
                        // 授權交易查詢 新增 搜尋條件 TransType by Jimmy Kang 20150721  -- 修改開始 --
                        request.setAttribute("TransType", transtype);
                        // 授權交易查詢 新增 搜尋條件 TransType by Jimmy Kang 20150721  -- 修改結束 --
                        
                        MerchantAuthLogBean malb = new MerchantAuthLogBean();

                        if (startdate != null && !startdate.equals("") && (reportflag == null || !reportflag.equalsIgnoreCase("ture")))
                        {
                            //有查詢條件
                            Forward = "./MerchantAuthLogQueryResult.jsp";
                            int tempcon=0;
                            ArrayList authlog =new ArrayList();
                            if(checkpoint.equals("")){
                            	
                            	  // get_AuthLog_Count method 新增引數 TransType by Jimmy Kang 20150721
                            	  // 授權交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150721  -- 修改開始 --
                            	  authlog = malb.get_AuthLog_Count(sysBean, merchantid, submid, startdate,
                                          enddate, starttime, endtime, transcode, transtype, transstatus, ordertype, orderid,
                                          authid, terminalid, capturetype);
                            	  // 授權交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150721  -- 修改結束 --
                            	  
                            	  tempcon=Integer.parseInt(((Hashtable)authlog.get(0)).get("TOTAL").toString());
                            }
                            if(tempcon <= queryMax || checkpoint.equals("XXX") ){
                            	request.setAttribute("checkpoint", "XXX");
                            
                             // get_AuthLog_List method 新增引數 TransType by Jimmy Kang 20150721
                             // 授權交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150721  -- 修改開始 --
                             authlog = malb.get_AuthLog_List(sysBean, merchantid, submid, startdate,
                            		    enddate, starttime, endtime, transcode, transtype, transstatus, ordertype, orderid,
                                        authid, terminalid, capturetype);
                             // 授權交易查詢 新增 查詢條件 TransType by Jimmy Kang 20150721  -- 修改結束 --
                             
//                            if(authlog.size()> queryMax){
//                            	errMessage ="本次查詢結果已超過筆數上限(最大筆數:"+String.valueOf(queryMax)+")";
//                            }
                            }
                            session.removeAttribute("AuthLog");
                            session.setAttribute("AuthLog", authlog);
                            LogMemo = "查詢明細資料";
                        }
                        else if (sysorderid != null && detail != null && detail.equalsIgnoreCase("true"))
                        {
                            Forward = "./MerchantAuthLogQueryDetail.jsp";
                            merchantid = request.getParameter("MerchantId");
                            submid = request.getParameter("SubMid");
//                            rowid= URLDecoder.decode(rowid);
                            Hashtable detail_log = malb.get_AuthLog_View(sysBean, merchantid, submid, sysorderid, rowid);
                            session.setAttribute("DetailLog", detail_log);
                            LogMemo = "檢視"+sysorderid+"明細資料";
                        }
                        else if (reportflag != null && reportflag.equalsIgnoreCase("ture"))
                        {
                            String sql = malb.get_AuthLog_List_rs(sysBean, merchantid, submid, startdate,
                                    enddate, starttime, endtime, transcode, transtype, transstatus, ordertype, orderid,
                                    authid, terminalid, capturetype);
                            createReport cr = new createReport();
                            Hashtable field = new Hashtable();
                            System.out.println("starttime="+starttime+",endtime="+endtime);
                            String rpttranscode = "";
                            if (transcode.equalsIgnoreCase("ALL"))
                                rpttranscode = "全部";

                            if (transcode.equalsIgnoreCase("00"))
                                rpttranscode = "00-購貨交易";

                            if (transcode.equalsIgnoreCase("01"))
                                 rpttranscode = "01-退貨交易";

                            if (transcode.equalsIgnoreCase("10"))
                                rpttranscode = "10-購貨取消交易";

                            if (transcode.equalsIgnoreCase("11"))
                                rpttranscode = "11-退貨取消交易";

                            System.out.println("rpttranscode="+rpttranscode);
                            String rpttransstatus = "";
                            if (transstatus.equalsIgnoreCase("ALL"))
                                rpttransstatus = "全部";

                            if (transstatus.equalsIgnoreCase("A"))
                                rpttransstatus = "A-Approved";

                            if (transstatus.equalsIgnoreCase("D"))
                                rpttransstatus = "D-Declined";

                            if (transstatus.equalsIgnoreCase("C"))
                                rpttransstatus = "C-Call Bank";
                            
                            // 授權交易查詢 新增 交易狀態 "P-Pending" by Jimmy Kang 20150721  -- 修改開始 --
                            if (transstatus.equalsIgnoreCase("P"))
                                rpttransstatus = "P-Pending";
                            // 授權交易查詢 新增 交易狀態 "P-Pending" by Jimmy Kang 20150721  -- 修改結束 --

                            System.out.println("rpttransstatus="+rpttransstatus);
                            
                            // 授權交易查詢 新增 TransType by Jimmy Kang 20150721  -- 修改開始 --
                            String rpttranstype = "";
                            if (transtype.equalsIgnoreCase("ALL"))
                            {
                            	rpttranstype = "全部";
                            }
                            else if (transtype.equalsIgnoreCase("SSL"))
                            {
                            	rpttranstype = "信用卡";
                            }
                            else
                            {
                            	rpttranstype = transtype;
                            }
                            System.out.println("rpttranstype="+rpttranstype);
                            // 授權交易查詢 新增 TransType by Jimmy Kang 20150721  -- 修改結束 --

                            String rptordertype = "";
                            if (ordertype.equalsIgnoreCase("M"))
                                rptordertype = "特店指定單號";

                            if (ordertype.equalsIgnoreCase("S"))
                                rptordertype = "系統指定單號";

                            System.out.println("rptordertype="+rptordertype);
                            System.out.println("orderid="+orderid);
                            System.out.println("authid="+authid);

                            String rptterminalid = "";
                            if (terminalid.equalsIgnoreCase("ALL"))
                            {
                                rptterminalid = "全部";
                            }
                            else
                            {
                                rptterminalid = terminalid;
                            }

                            System.out.println("rptterminalid="+rptterminalid);

                            String rptcapturetype = "";
                            if (capturetype.equalsIgnoreCase("ALL"))
                                rptcapturetype = "全部";

                            if (capturetype.equalsIgnoreCase("Capture"))
                                rptcapturetype = "已請款";

                            if (capturetype.equalsIgnoreCase("NotCapture"))
                                rptcapturetype = "未請款";
                            System.out.println("query_submid="+query_submid);
                            if (query_submid.equalsIgnoreCase("ALL")){
                            	query_submid = "全部";
                            }
                            field.put("startdate", startdate);
                            field.put("enddate", enddate);
                            field.put("starttime", starttime);
                            field.put("endtime", endtime);
                            field.put("transcode", rpttranscode);
                            field.put("transstatus", rpttransstatus);
                            field.put("ordertype", rptordertype);
                            field.put("orderid", orderid);
                            field.put("authid", authid);
                            field.put("terminalid", rptterminalid);
                            field.put("capturetype", rptcapturetype);
                            //new add submid
                            field.put("submid", query_submid);
                            field.put("isSubMid", isSignMer ? "N" : "Y");
                            
                            // 授權交易查詢 新增 TransType by Jimmy Kang 20150721  -- 修改開始 --
                            field.put("transtype", rpttranstype);
                            // 授權交易查詢 新增 TransType by Jimmy Kang 20150721  -- 修改結束 --
                            
                            cr.createPDF(sysBean,SecurityTool.output(sql), request, response, "/Report/" + rptname, field, printtype);
                            // malb.closeConn();
                            LogMemo = "以"+printtype+"格式匯出";
                        }

                        if (LogMemo.length()>0)
                        {
                            LogUserName = hashMerUser.get("USER_ID").toString() + "(" + hashMerUser.get("USER_NAME").toString() + ")";
                            LogData = UserBean.get_LogData(LogMerchantID,LogUserName, LogFunctionName, LogStatus, LogMemo);
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

            if (Message.length() > 0)
            {
                session.setAttribute("Message", Message);
                LogMemo = Message;
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID,LogUserName, LogFunctionName, "失敗", LogMemo);
                log_user.debug(LogData);
            }

            System.out.println("Forward=" + Forward);
            if (reportflag == null || !reportflag.equalsIgnoreCase("ture"))
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
        catch (Exception e)
        {
            e.printStackTrace();
            request.setAttribute("errMsg", e.toString());
            log_systeminfo.debug("--MerchantAuthLogCtl--"+e.toString());
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
