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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
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
import com.cybersoft.merchant.bean.MerchantLoginBean;

/**
 * <p>�j��n�J�t�Ϊ�Servlet</p>
 * @version 0.1 2008/01/31  Shiley Lin
 */
public class MerchantReLoginCtl extends HttpServlet
{
    private static final String CONTENT_TYPE = "text/html; charset=Big5";
    private String initPassword;
    java.util.Date nowdate;
    public UserBean UserBean = new UserBean();
    public DataBaseBean2 sysBean = new DataBaseBean2();

    public void init()
    {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        this.doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        LogUtils log_user = new LogUtils("user");
        LogUtils log_systeminfo = new LogUtils("systeminfo");
        String LogUserName = "";
        String LogFunctionName = "�n�J";
        String LogStatus = "����";
        String LogMemo = "";
        String LogData = "";
        String LogMerchantID = "";
        response.setContentType(CONTENT_TYPE);
        request.setCharacterEncoding("BIG5");

        String Forward = ""; // ������}
        String Message = ""; // ��ܰT��

        int intTimeMin = 0;
        int intMaxCheckPwd = 0;
        int intPasswordMinLength = 0;
        int intPasswordMaxLength = 0;
        int intPwdChangDay = 0;
        int intPwdAlertDay = 0;
        int intTimeOut = 0;
        Hashtable hashConfData = new Hashtable();
        Hashtable hashSys = new Hashtable(); // �t�ΰѼ�
        Hashtable hashMerUser = new Hashtable(); // �S���ϥΪ�
        Hashtable hashMerchant = new Hashtable(); // �S���D��
        ArrayList arrayTerminal = new ArrayList(); // �ݥ����D��

        RequestDispatcher myRequestDispatcher = null;
        HttpSession session = request.getSession(true);
        SessionControlBean scb = new SessionControlBean();

        try
        {
            System.out.println("-------------------- Start MerchantLoginCtl ----------------");
            MerchantLoginBean mlb = new MerchantLoginBean();
            Hashtable rtn = new Hashtable();
            sysBean.setAutoCommit(false);
            rtn = init_Sys_Parm_List(sysBean); // �t�ΰѼ�

            hashSys = (Hashtable) rtn.get("hashData");
            intPwdChangDay = Integer.parseInt(String.valueOf(rtn.get("intPwdChangDay")));
            intPwdAlertDay = Integer.parseInt(String.valueOf(rtn.get("intPwdAlertDay")));
            intMaxCheckPwd = Integer.parseInt(String.valueOf(rtn.get("intMaxCheckPwd")));
            intTimeOut = Integer.parseInt(String.valueOf(rtn.get("intTimeOut")));

            // UserBean UserBean = new UserBean();

            String Flag = request.getParameter("Flag");
            if (Flag == null)
            {
                Flag = "Cancel";
            }

            if (Flag.equalsIgnoreCase("Cancel"))
            {
                if (session.getAttribute("SESSIONDATA") != null)
                {
                    session.removeAttribute("SESSIONDATA");
                }
            }
            else
            {
                Hashtable hashSession = (Hashtable) session.getAttribute("SESSIONDATA");
                if (session.getAttribute("SESSIONDATA") != null)
                {
                    session.removeAttribute("SESSIONDATA");
                }

                session.invalidate();
                System.out.println("------------ invalidate()-------");
                session = request.getSession(true);
                System.out.println("------------ SessionID = " + session.getId());
                String MerchantID = (String) hashSession.get("MERCHANT_ID");
                String UserID = (String) hashSession.get("USER_ID");

                ServletContext sc = session.getServletContext();
                if (sc.getAttribute(MerchantID + "@" + UserID)!=null)
                {
                    sc.removeAttribute(MerchantID + "@" + UserID);
                }

                scb.close_Session(sysBean, MerchantID, UserID, true); //  Close Session
                hashMerUser = mlb.get_Merchant_User(sysBean, MerchantID, UserID, "<>", "D", String.valueOf(intPwdChangDay));
                session.setMaxInactiveInterval(intTimeOut * 60);
                LogMerchantID = MerchantID;
                LogUserName = UserID + "(" + UserID + ")";
                hashMerchant = mlb.get_Merchant(sysBean, MerchantID); //�S���D��
                arrayTerminal = mlb.get_Terminal(sysBean, MerchantID); // �ݥ����D��
                if (hashMerchant.size() > 0)
                {
                    if (arrayTerminal.size() > 0)
                    {
                        String SessionID = session.getId();
                        System.out.println("------------ SessionID = " + SessionID);
                        String Today = UserBean.get_TransDate("yyyy/MM/dd");
                        if (scb.credit_Session(sysBean, MerchantID, UserID, SessionID))
                        {
                            // session �إߦ��\
                            Forward = "./Merchant_Index.jsp";
                            Message = "�t�αj��ñ�J";
                            LogStatus = "���\";
                            if (hashMerchant.size() > 0)
                            {
                                LogMerchantID = (String) hashMerUser.get("MERCHANT_ID");
                            }

                            hashConfData.put("MERCHANT", hashMerchant);
                            hashConfData.put("TERMINAL", arrayTerminal);
                            hashConfData.put("MERCHANT_USER", hashMerUser);
                            hashConfData.put("SYSCONF", hashSys);
                            session.setAttribute("SYSCONFDATA", hashConfData);
                        }
                        else
                        {
                            Message = "�t�μȰ��A�Ƚеy��A��";
                            Forward = "./Merchant_Login.jsp";
                        }
                    }
                    else
                    {
                        Message = "�ݥ�����ƥ��إߡA�Ь�����H��";
                        Forward = "./Merchant_Login.jsp";
                    }
                }
                else
                {
                    Message = "�S���ө��|���إߡA�Ь�����H��";
                    Forward = "./Merchant_Login.jsp";
                }

                if (hashMerUser.size() > 0)
                {
                    LogUserName = hashMerUser.get("USER_ID").toString() + "(" + hashMerUser.get("USER_NAME").toString() + ")";
                }

                LogMemo = Message;
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                System.out.println("------------- MerchantLoginCtl.java InsertLog --------------");
                log_user.debug(LogData);
            }

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
        //20130702 Jason �W�[finally�B�zsysBean.close
        finally{
        try
        {
            sysBean.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            request.setAttribute("errMsg", e.toString());
            log_systeminfo.debug(e.toString());
            request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
        }
        }
    }

    /**
     * ���o�t�ΰѼ��� SYS_PARM_LIST
     * @return Hashtable �t�ΰѼƸ��
     */
    private Hashtable init_Sys_Parm_List(DataBaseBean2 sysBean)
    {
        Hashtable result = new Hashtable();
        /** DataBaseBean SysBean = new DataBaseBean(); */        
        ArrayList arraySYS = new ArrayList();
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
        sSQLSB.append("Select * From SYS_PARM_LIST ");        
        try
        {
        	/** 2023/05/19 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-032 (No Need Test) */
            arraySYS = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        if (arraySYS.size() == 0)
        {
            // ��Ʈw�d�L�t�ΰѼ�
            ServletContext context = this.getServletContext();
            String GetContext[] = {"MER_PWD_MAXLEN", "MER_PWD_MINLEN", "MER_PWD_MAXCNT",
                                   "MER_PWD_DAY", "MER_PWD_ALERTDAY", "MER_TIMEOUT"};

            for (int i = 0; i < GetContext.length; ++i)
            {
                Hashtable hashSYS = new Hashtable();
                String ContextValue = context.getInitParameter(GetContext[i]); // �t�ΰѼ� web.xml
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
        }

        result.put("hashData", hashData);
        return result;
    }
}
