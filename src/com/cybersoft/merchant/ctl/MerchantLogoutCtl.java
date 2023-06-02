/************************************************************
 * <p>#File Name:       MerchantLoginCtl.java   </p>
 * <p>#Description:                 </p>
 * <p>#Create Date:     2007/09/13      </p>
 * <p>#Company:         cybersoft       </p>
 * <p>#Notice:                      </p>
 * @author      Shirley Lin
 * @since       SPEC version
 * @version 0.1 2007/09/13  Shirley Lin
 ************************************************************/
package com.cybersoft.merchant.ctl;

import java.io.*;
import sun.misc.*;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.text.*;
import java.text.SimpleDateFormat;
import java.security.*;
/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft.bean.SessionControlBean;
import com.cybersoft.merchant.bean.MerchantLoginBean;
import com.cybersoft.bean.UserBean;
import com.cybersoft.bean.LogUtils;
/**
 * <p>控制登出系統的Servlet</p>
 * @version 0.1 2007/09/27  Shiley Lin
 */
public class MerchantLogoutCtl extends HttpServlet
{
    private static final String CONTENT_TYPE = "text/html; charset=Big5";
    private Hashtable hashSys = new Hashtable(); // 系統參數
    private Hashtable hashMerUser = new Hashtable(); // 特店使用者
    private Hashtable hashMerchant = new Hashtable(); // 特店主檔
    private ArrayList arrayTerminal = new ArrayList(); // 端末機主檔
    private String Forward = ""; // 網頁轉址
    java.util.Date nowdate;
    public static final LogUtils log_user = new LogUtils("user");
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    public UserBean UserBean = new UserBean();
    public DataBaseBean2 sysBean = new DataBaseBean2();

    String LogUserName = "";
    String LogFunctionName = "登出";
    String LogStatus = "成功";
    String LogMemo = "";
    String LogData = "";
    String LogMerchantID = "";
    private Hashtable hashConfData = new Hashtable();

    public void init()
    {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
      this.doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String Message = ""; // 顯示訊息
        response.addHeader("Pragma", "No-cache");
        response.addHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType(CONTENT_TYPE);
        request.setCharacterEncoding("BIG5");
        HttpSession session = request.getSession(true);
        SessionControlBean scb =new SessionControlBean();

        try
        {
            scb = new SessionControlBean(session,request,response);
            sysBean.setAutoCommit (false);
        }catch(UnsupportedOperationException E)
        {
            E.toString();
            request.getRequestDispatcher("/Merchant_Bye.jsp").forward(request, response);
            return ;
        }

        try
        {
            String LogoutFlag = request.getParameter("LogoutFlag");
            if(LogoutFlag==null) LogoutFlag = "";
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

            if(hashMerUser!=null)
            {
                String MerchantID = hashMerUser.get("MERCHANT_ID").toString();
                String UserID = hashMerUser.get("USER_ID").toString();
                String SessionID = session.getId();
                scb.close_Session(sysBean, MerchantID, UserID, SessionID, false); //  Close Session
                session.getServletContext().removeAttribute(MerchantID+"@"+UserID);
                session.getServletContext().removeAttribute(SessionID);
                session.removeAttribute("SYSCONFDATA");
                Forward = "./Merchant_NoUse.jsp";

                if (LogoutFlag.equalsIgnoreCase("Login") || LogoutFlag.equalsIgnoreCase("Close") || LogoutFlag.equalsIgnoreCase("End") )
                {
                    session.setAttribute("Message", "您已成功登出本系統!");
                    Message = "您已成功登出本系統!";
                    session.setAttribute("LogoutFlag", LogoutFlag);

                    if (LogoutFlag.equalsIgnoreCase("Login"))
                    {
                        session.setAttribute("Forward", "./Merchant_Login.jsp");
                    }

                    Forward = "./Merchant_Bye.jsp";
                }
                System.out.println("Forward=" + Forward);
            }
            else
            {
                Forward = "./Merchant_Login.jsp";
                session.setAttribute("Message", "閒置時間過長或非從登入畫頁進入!");
                Message = "閒置時間過長或非從登入畫頁進入!";
            }

            LogMemo = Message;
            LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
            // UserBean UserBean = new UserBean();
            LogData = UserBean.get_LogData(LogMerchantID,LogUserName, LogFunctionName, LogStatus, LogMemo);
            log_user.debug(LogData);
            response.sendRedirect(response.encodeRedirectURL(Forward));
        }
        catch(Exception e)
        {
            e.printStackTrace();
            session.setAttribute("Message", "閒置時間過長或非從登入畫頁進入!");
            response.sendRedirect(response.encodeRedirectURL("./Merchant_Login.jsp"));
        }
        //20130703 Jason Fix Finally Fix SessionTime logout DB session unrelease
        finally
        {
        try
        {
            sysBean.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            session.setAttribute("Message", "閒置時間過長或非從登入畫頁進入!");
            response.sendRedirect(response.encodeRedirectURL("./Merchant_Login.jsp"));
        }
        }
    }
}
