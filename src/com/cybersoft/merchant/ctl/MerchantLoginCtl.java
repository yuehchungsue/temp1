/************************************************************
 * <p>#File Name:       MerchantLoginCtl.java   </p>
 * <p>#Description:                 </p>
 * <p>#Create Date:     2007/09/13      </p>
 * <p>#Company:         cybersoft       </p>
 * <p>#Notice:                      </p>
 * @author      Shirley Lin
 * @since       SPEC version
 * @version 0.1 2007/09/13  Shirley Lin
 * 201803200651 20180320 修補滲透測試弱點,修改EC UI特店登入畫面 SherryAnn，增加圖型驗證
 * 202208090854-01 20220801 HKP PCI-DSS更換密碼雜錯MD5為SHA256，MD5雜錯長度為32，SHA256雜錯長度為64，以此長度判斷是否為MD5，變更後的密碼一律使用SHA256與LOG遮蔽
 ************************************************************/
package com.cybersoft.merchant.ctl;

import java.io.*;

import sun.misc.*;

import java.sql.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import java.text.*;
import java.security.*;

/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft.bean.SessionControlBean;
import com.cybersoft.merchant.bean.MerchantLoginBean;
import com.cybersoft.merchant.ctl.MerchantChangePwdCtl;
import com.cybersoft.bean.UserBean;
import com.cybersoft.bean.emDB;
import com.cybersoft.bean.LogUtils;
import com.cybersoft.common.Util;
/**
 * <p>控制登入系統的Servlet</p>
 * @version 0.1 2007/09/13  Shiley Lin
 */
public class MerchantLoginCtl extends HttpServlet
{
    private static final String CONTENT_TYPE = "text/html; charset=Big5";
    private String initPassword;
    public static final String LOGIN_OLD_JSP = "Login_Old_Jsp"; //登入修改密碼-參數
    public static final String LOGIN_OLD_CHANGE = "Login_Old_Change"; //登入修改密碼-參數
    public static final String LOGIN_OLD_USER_STATUS = "Login_Old_User_Status"; //登入修改密碼-參數
    public UserBean UserBean = new UserBean();
    public DataBaseBean2 sysBean = new DataBaseBean2();
    public static final LogUtils log_user = new LogUtils("user");
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    java.util.Date nowdate;

    public void init()
    {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        this.doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        //LogUtils log_user = new LogUtils("user");
        //LogUtils log_user = new LogUtils("user");
        //LogUtils log_systeminfo = new LogUtils("systeminfo");
        String LogUserName = "";
        String LogFunctionName = "登入";
        String LogStatus = "失敗";
        String LogMemo = "";
        String LogData = "";
        String LogMerchantID = "";
        response.setContentType(CONTENT_TYPE);
        request.setCharacterEncoding("BIG5");

        String Forward = ""; // 網頁轉址
        String Message = ""; // 顯示訊息

        int intTimeMin = 0;
        int intMaxCheckPwd = 0;
        int intPasswordMinLength = 0;
        int intPasswordMaxLength = 0;
        int intPwdChangDay = 0;
        int intPwdAlertDay = 0;
        int intTimeOut = 0;
        int intQueryMonth = 0;
        Hashtable hashConfData = new Hashtable();
        Hashtable hashSys = new Hashtable(); // 系統參數
        Hashtable hashMerUser = new Hashtable(); // 特店使用者
        Hashtable hashMerchant = new Hashtable(); // 特店主檔
        ArrayList arrayTerminal = new ArrayList(); // 端末機主檔
        //撈取子特店名單
        ArrayList arraySubMid = new ArrayList();// 所屬子特店列表
        
        RequestDispatcher myRequestDispatcher = null;
        HttpSession session = request.getSession(true);
        SessionControlBean scb = new SessionControlBean();
        try
        {
            System.out.println("-------------------- Start MerchantLoginCtl ----------------");
            //20180320檢查圖型驗證碼
            String szNO_UI= Util.objToStrTrim(request.getParameter("zNO"));
            String szNO_Session= Util.objToStrTrim(session.getAttribute("zNO"));
            System.out.println("zNO_UI:"+szNO_UI+",NO_Session:"+szNO_Session);
            if(!szNO_UI.equals(szNO_Session)){
                Message = "圖型驗證碼錯誤，請重新輸入";
                Forward = "./Merchant_Login.jsp";
                if (session.getAttribute("Message") != null){
                    session.removeAttribute("Message");
                }
                session.setAttribute("Message", Message);
                response.sendRedirect(Forward);
                return;
            }
            //清除圖型驗證碼
            session.removeAttribute("zNO");
            
            sysBean.setAutoCommit (false);
            MerchantLoginBean mlb = new MerchantLoginBean();
            Hashtable rtn = new Hashtable();
            rtn = init_Sys_Parm_List(sysBean); // 系統參數

            hashSys = (Hashtable) rtn.get("hashData");
            intPwdChangDay = Integer.parseInt(String.valueOf(rtn.get("intPwdChangDay")));
            intPwdAlertDay = Integer.parseInt(String.valueOf(rtn.get("intPwdAlertDay")));
            intMaxCheckPwd = Integer.parseInt(String.valueOf(rtn.get("intMaxCheckPwd")));
            intTimeOut = Integer.parseInt(String.valueOf(rtn.get("intTimeOut")));
            //查詢日期區間(月),特店
            intQueryMonth = Integer.parseInt(String.valueOf(rtn.get("intQuryMonth")));
            //UserBean UserBean = new UserBean();

            String MerchantID = request.getParameter("MerchantID");
            if (MerchantID == null)
                MerchantID = "";

            MerchantID = UserBean.trim_Data(MerchantID);
            String UserID = request.getParameter("UserID");
            if (UserID == null)
                UserID = "";

            UserID = UserBean.trim_Data(UserID);
            String UserPwd = request.getParameter("UserPwss");
            if (UserPwd == null)
                UserPwd = "";

            UserPwd = UserBean.trim_Data(UserPwd);
            
            /** 2023/04/19 測試 : get_Merchant_User() (By : YC) *//** IT-TESTCASE-010-020-002-001*/
            hashMerUser = mlb.get_Merchant_User(sysBean, MerchantID, UserID, "<>", "D", String.valueOf(intPwdChangDay));
            session.setMaxInactiveInterval(intTimeOut * 60);

            //20220801
            //UserPwd = MerchantChangePwdCtl.getMsgDigestPwd(UserPwd); //取得密碼編碼字串

            LogMerchantID = MerchantID;
            LogUserName =  UserID+"("+UserID+")";
            String UserFlag = mlb.check_Merchant_User(hashMerUser, MerchantID, UserID, UserPwd, intPwdChangDay, intPwdAlertDay, intMaxCheckPwd);
            System.out.println("UserFlag=" + UserFlag);
            // nouser:無此使用者 pwderror:密碼錯誤 changpwd:需密碼變更 changNewpwd:首次登入需密碼變更 changResetpwd:密碼重置需密碼變更 changTimepwd:密碼已到期需密碼變更 pwdlock:密碼鎖定 changealert:正常但需提示變更密碼 ok:正常 userlock:使用者停用

            if (UserFlag.equalsIgnoreCase("ok")            || UserFlag.equalsIgnoreCase("changealert") ||
                UserFlag.equalsIgnoreCase("changpwd")      || UserFlag.equalsIgnoreCase("changNewpwd") ||
                UserFlag.equalsIgnoreCase("changResetpwd") || UserFlag.equalsIgnoreCase("changTimepwd"))
            {
                // 使用者正確 或 提示變更密碼 或 指定轉至密碼變更
            	String sSignBill = Util.objToStrTrim(hashMerUser.get("SIGN_BILL"));
                hashMerchant = mlb.get_Merchant(sysBean, MerchantID,sSignBill); //特店主檔
                arrayTerminal = mlb.get_Terminal(sysBean, MerchantID,sSignBill); // 端末機主檔
                arraySubMid = mlb.get_SubMid(sysBean, MerchantID,sSignBill); //取得子特店清單
                if (hashMerchant.size() > 0)
                {
                    if (arrayTerminal.size() > 0)
                    {
                        String SessionID = session.getId();
                        System.out.println("------------ SessionID = "+SessionID);
                        String Today = UserBean.get_TransDate("yyyy/MM/dd");

                        // 確認使用者是否登入
                        boolean Session_Flag = scb.check_Session(sysBean, MerchantID, UserID, SessionID, session, Today);
                        System.out.println("----------- Session_Flag = "+Session_Flag);
                        if (Session_Flag)
                        {
                            if (scb.credit_Session(sysBean, MerchantID, UserID, SessionID))
                            {
                                // session 建立成功
                                Forward = "./Merchant_Index.jsp";
                                LogStatus = "成功";

                                ArrayList arrayMerRole = mlb.get_Merchant_User_Role(sysBean, MerchantID,UserID, "MerchantMsgBoard" );  //查詢公告訊息是否存在
                                if (arrayMerRole.size()>0)
                                {
                                    session.setAttribute("MsgBoardFlag", "Y");
                                }

                                if (UserFlag.equalsIgnoreCase("changealert"))
                                {
                                    String PwdDueDate = hashMerUser.get("PWDDUE_DATE").toString();
                                    Message = "密碼即將於 " + PwdDueDate + " 到期，請記得進行密碼變更";
                                    session.setAttribute("Message", Message);
                                }

                                if (UserFlag.equalsIgnoreCase("changpwd")      || UserFlag.equalsIgnoreCase("changNewpwd") ||
                                    UserFlag.equalsIgnoreCase("changResetpwd") ||UserFlag.equalsIgnoreCase("changTimepwd"))
                                {
                                    String userstatus = "";
                                    if (UserFlag.equalsIgnoreCase("changNewpwd"))
                                    {
                                        userstatus = "O";
                                    }
                                    else if (UserFlag.equalsIgnoreCase("changResetpwd"))
                                    {
                                        userstatus = "R";
                                    }
                                    else if (UserFlag.equalsIgnoreCase("changTimepwd"))
                                    {
                                        userstatus = "T";
                                    }

                                    String sd = MerchantMenuCtl.getRandom(6, "0AA", false);

                                    session.setAttribute(this.LOGIN_OLD_JSP, "./Merchant_ChangePwdOld.jsp");
                                    session.setAttribute(this.LOGIN_OLD_CHANGE, "login" + sd);
                                    session.setAttribute(this.LOGIN_OLD_USER_STATUS, userstatus);
                                    session.setAttribute("login" + sd, "OK");
                                    Message = "登錄需作密碼變更";
                                    Forward = "./MerchantChangePwdCtl";
                                }

                                if (hashMerchant.size()>0)
                                {
                                    LogMerchantID = (String)hashMerUser.get("MERCHANT_ID");
                                }

                                hashConfData.put("MERCHANT", hashMerchant);
                                hashConfData.put("TERMINAL", arrayTerminal);
                                hashConfData.put("MERCHANT_USER", hashMerUser);
                                hashConfData.put("SYSCONF", hashSys);
                                //將子特店清單放入系統參數中
                                hashConfData.put("SUBMID",arraySubMid);
                                session.setAttribute("SYSCONFDATA", hashConfData);
                            }
                            else
                            {
                                Message = "系統暫停服務請稍後再試";
                                Forward = "./Merchant_Login.jsp";
                            }
                        }
                        else
                        {
                            Hashtable hashSessionData = scb.get_Session(MerchantID,UserID, Today);
                            session.setAttribute("SESSIONDATA", hashSessionData);
                            Forward = "./Merchant_ReLogin.jsp";
                        }
                    }
                    else
                    {
                        Message = "端末機資料未建立，請洽本行人員";
                        Forward = "./Merchant_Login.jsp";
                    }
                }
                else
                {
                    Message = "特約商店尚未建立，請洽本行人員";
                    Forward = "./Merchant_Login.jsp";
                }
            }
            else
            {
                if (UserFlag.equalsIgnoreCase("pwdlock"))
                {
                    Message = "使用者已鎖定";
                }
                else
                {
                    if (UserFlag.equalsIgnoreCase("userlock"))
                    {
                        Message = "使用者停用";
                    }
                    else
                    {
                        if (UserFlag.equalsIgnoreCase("pwdexpire"))
                        {
                            Message = "密碼逾期請洽系統管理人員";
                        }
                        else
                        {
                            Message = "使用者帳號密碼錯誤";
                        }
                    }
                }

                Forward = "./Merchant_Login.jsp";
            }

            if (session.getAttribute("Message") != null)
            {
                session.removeAttribute("Message");
            }

            if (Message.length() > 0)
            {
                session.setAttribute("Message", Message);
            }

            if (hashMerUser.size()>0)
            {
               LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
            }

            LogMemo = Message;
            LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
            System.out.println("------------- MerchantLoginCtl.java InsertLog --------------");
            log_user.debug(LogData);

            System.out.println("Forward=" + Forward);
            response.sendRedirect(Forward);
            // request.getRequestDispatcher(Forward).forward(request, response);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            request.setAttribute("errMsg", e.toString());
            log_systeminfo.debug(e.toString());
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
     * 取得系統參數檔 SYS_PARM_LIST
     * @return Hashtable 系統參數資料
     */
    private Hashtable init_Sys_Parm_List(DataBaseBean2 SysBean)
    {
        Hashtable result = new Hashtable();
        /** DataBaseBean SysBean = new DataBaseBean(); */
        /** String sql = "Select * From SYS_PARM_LIST "; */
        StringBuilder sql = new StringBuilder();
        sql.append("Select * From SYS_PARM_LIST ");
        ArrayList arraySYS = new ArrayList();

        try
        {
        	/** 2023/04/19 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** IT-TESTCASE-010-020-002-001, TESTCASE-001*/
        	arraySYS = (ArrayList) sysBean.QuerySQLByParam(sql.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        if (arraySYS.size() == 0)
        {
            // 資料庫查無系統參數
            ServletContext context = this.getServletContext();
            String GetContext[] = {"MER_PWD_MAXLEN", "MER_PWD_MINLEN", "MER_PWD_MAXCNT",
                                   "MER_PWD_DAY", "MER_PWD_ALERTDAY", "MER_TIMEOUT"};

            for (int i = 0; i < GetContext.length; ++i)
            {
                Hashtable hashSYS = new Hashtable();
                String ContextValue = context.getInitParameter(GetContext[i]); // 系統參數 web.xml
                hashSYS.put("PARM_ID", GetContext[i]);
                hashSYS.put("PARM_DESC", GetContext[i]);
                hashSYS.put("PARM_VALUE", ContextValue);
                arraySYS.add(hashSYS);
            }
        }

        Hashtable hashData = new Hashtable();
        for (int c = 0; c < arraySYS.size(); ++c)
        {
            Hashtable hashTmp = (Hashtable) arraySYS.get(c);
            String ParmID = hashTmp.get("PARM_ID").toString();
            String ParmValue = hashTmp.get("PARM_VALUE").toString();
            hashData.put(ParmID, ParmValue);
            if (ParmID.equalsIgnoreCase("MER_PWD_MAXLEN"))
            {
                result.put("intPasswordMaxLength", ParmValue);
            }

            if (ParmID.equalsIgnoreCase("MER_PWD_MINLEN"))
            {
                result.put("intPasswordMinLength", ParmValue);
            }

            if (ParmID.equalsIgnoreCase("MER_PWD_MAXCNT"))
            {
                result.put("intMaxCheckPwd", ParmValue);
            }

            if (ParmID.equalsIgnoreCase("MER_PWD_DAY"))
            {
                result.put("intPwdChangDay", ParmValue);
            }

            if (ParmID.equalsIgnoreCase("MER_PWD_ALERTDAY"))
            {
                result.put("intPwdAlertDay", ParmValue);
            }

            if (ParmID.equalsIgnoreCase("MER_TIMEOUT"))
            {
                result.put("intTimeOut", ParmValue);
            }
            if (ParmID.equalsIgnoreCase("MER_LIMIT_QRY_MONTH"))
            {
                result.put("intQuryMonth", ParmValue);
            }
        }

        result.put("hashData", hashData);
        return result;
    }
}
