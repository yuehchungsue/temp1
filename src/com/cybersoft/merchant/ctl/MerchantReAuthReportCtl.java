/************************************************************
 * <p>#File Name:   MerchantReAuthReportCtl.java    </p>
 * <p>#Description:                 </p>
 * <p>#Create Date: 2008/05/04              </p>
 * <p>#Company:     cybersoft               </p>
 * <p>#Notice:                      </p>
 * @author      Shirley Lin
 * @since       SPEC version
 * @version 0.1 2008/05/04  Shirley Lin
 * @modify              2008/05/04      Shirley Lin
 ************************************************************/
package com.cybersoft.merchant.ctl;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.cybersoft.bean.UserBean;
/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft.merchant.bean.MerchantReAuthReportBean;
import com.fubon.security.filter.SecurityTool;
import com.cybersoft.bean.SessionControlBean;
import java.sql.ResultSet;
import com.cybersoft.bean.createReport;
import com.cybersoft.bean.LogUtils;
/**
 * <p>控制交易的Servlet</p>
 * @version 0.1 2008/05/04  Shiley Lin
 */
public class MerchantReAuthReportCtl extends HttpServlet
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
    String LogFunctionName = "再授權交易統計報表";
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
        SessionControlBean scb =new SessionControlBean();
        try
        {
            scb = new SessionControlBean(session,request,response);
            sysBean.setAutoCommit(false);
        }
        catch(UnsupportedOperationException E)
        {
            E.toString();
            request.getRequestDispatcher("/Merchant_Bye.jsp").forward(request, response);
            return ;
        }

        try
        {
            Hashtable hashSys = new Hashtable(); // 系統參數
            Hashtable hashMerUser = new Hashtable(); // 特店使用者
            Hashtable hashMerchant = new Hashtable(); // 特店主檔
            ArrayList arrayTerminal = new ArrayList(); // 端末機主檔
            Hashtable hashConfData = (Hashtable) session.getAttribute("SYSCONFDATA");
            if (hashConfData == null) hashConfData = new Hashtable();
            boolean Merchant_Current =  false;   // 特店狀態
            boolean Merchant_Permit =  false;   // 特店權限
            if (hashConfData.size()>0)
            {
                hashSys = (Hashtable)hashConfData.get("SYSCONF"); // 系統參數
                hashMerUser = (Hashtable)hashConfData.get("MERCHANT_USER"); // 特店使用者
                hashMerchant = (Hashtable)hashConfData.get("MERCHANT"); // 特店主檔

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
            Merchant_Current = UserBean.check_Merchant_Column(hashMerchant,"CURRENTCODE","B,C,D");  //  確認特店狀態
            Merchant_Permit = UserBean.check_Merchant_Column(hashMerchant,"PERMIT_INQUIRY_TX","Y");  //  確認特店權限
            boolean boolForwardFlag = false;
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
                        String Action = (request.getParameter("Action") == null) ? "" : request.getParameter("Action");
                        if (Action.length() == 0)
                        {
                            Forward = "./MerchantReAuthReportQuery.jsp";
                            request.getRequestDispatcher(Forward).forward(request, response);
                        }
                        else
                        {
                            String MerchantID = String.valueOf(hashMerchant.get("MERCHANTID"));
                            String StartDate = (request.getParameter("Start_TransDate") == null) ? "" : UserBean.trim_Data(request.getParameter("Start_TransDate"));
                            String EndDate = (request.getParameter("End_TransDate") == null) ? "" : UserBean.trim_Data(request.getParameter("End_TransDate"));
                            String ReauthFlag = (request.getParameter("ReauthFlag") == null) ? "" : UserBean.trim_Data(request.getParameter("ReauthFlag"));
                            String PrintType = (request.getParameter("PrintType") == null) ? "" : UserBean.trim_Data(request.getParameter("PrintType"));

                            if (StartDate.length() > 0 && EndDate.length() > 0 && PrintType.length() > 0 && ReauthFlag.length() > 0)
                            {
                                MerchantReAuthReportBean ReAuthReportBean = new MerchantReAuthReportBean();
                                String sql = ReAuthReportBean.get_FileStatus_Report_rs(sysBean, MerchantID, StartDate, EndDate, ReauthFlag);
                                createReport cr = new createReport();
                                Hashtable field = new Hashtable();
                                StartDate = StartDate;
                                EndDate = EndDate;
                                field.put("pStart_Trans_DateTime", StartDate);
                                field.put("pEnd_Trans_DateTime", EndDate);
                                String ReauthFlagDesc = "全部";

                                if (ReauthFlag.equalsIgnoreCase("Y"))
                                {
                                    ReauthFlagDesc = "核准";
                                }

                                if (ReauthFlag.equalsIgnoreCase("N"))
                                {
                                    ReauthFlagDesc = "拒絕";
                                }

                                if (ReauthFlag.equalsIgnoreCase("R"))
                                {
                                    ReauthFlagDesc = "再授權";
                                }

                                field.put("pReauthFlag", ReauthFlagDesc);
                                String RPTName = "ReAuthReport.rpt";
                                System.out.println("PTName=" + RPTName);
                                System.out.println("PrintType=" + PrintType);
                                cr.createPDF(sysBean,SecurityTool.output(sql), request, response, "/Report/" + RPTName, field, PrintType);
                                // ReAuthReportBean.closeConn();

                                LogMemo = "查詢"+StartDate+"~"+EndDate+"，再授權結果="+ReauthFlag+"再授權交易統計報表";
                                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                log_user.debug(LogData);
                            }
                        }
                    }
                    else
                    {
                        Message = "特店無此功能權限";
                        Forward = "./Merchant_Response.jsp";
                        session.setAttribute("Message", Message);
                        boolForwardFlag = true;
                    }
                }
                else
                {
                    String CurrentCode = hashMerchant.get("CURRENTCODE").toString();
                    Message = UserBean.get_CurrentCode(CurrentCode);
                    Forward = "./Merchant_Response.jsp";
                    session.setAttribute("Message", Message);
                    boolForwardFlag = true;
                }
            }
            else
            {
                Message = "使用者無此權限請洽系統管理者";
                Forward = "./Merchant_NoUse.jsp";
                session.setAttribute("Message", Message);
                boolForwardFlag = true;
            }

            if (Message.length()>0)
            {
                LogMemo = Message;
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "失敗", LogMemo);
                log_user.debug(LogData);
            }

            if ( boolForwardFlag )
            {
                request.getRequestDispatcher(Forward).forward(request, response);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            request.setAttribute("errMsg",e.toString());
            log_systeminfo.debug("--MerchantReAuthReportCtl--"+e.toString());
            request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
        }
        //20130702 Jason 增加finally處理sysBean.close
        finally{
        try
        {
            sysBean.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            request.setAttribute("errMsg",e.toString());
            log_systeminfo.debug("--MerchantReAuthReportCtl--"+e.toString());
            request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
        }
        }
    }
}
