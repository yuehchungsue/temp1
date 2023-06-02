/************************************************************
 * <p>#File Name:       MerchantLoginCtl.java   </p>
 * <p>#Description:                 </p>
 * <p>#Create Date:     2007/09/13      </p>
 * <p>#Company:         cybersoft       </p>
 * <p>#Notice:                      </p>
 * @author      Shirley Lin
 * @since       SPEC version
 * @version 0.1 2007/09/13  Shirley Lin
 * 201803200651 20180320 �׸ɺ��z���ծz�I,�ק�EC UI�S���n�J�e�� SherryAnn�A�W�[�ϫ�����
 * 202208090854-01 20220801 HKP PCI-DSS�󴫱K�X����MD5��SHA256�AMD5�������׬�32�ASHA256�������׬�64�A�H�����קP�_�O�_��MD5�A�ܧ�᪺�K�X�@�ߨϥ�SHA256�PLOG�B��
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
 * <p>����n�J�t�Ϊ�Servlet</p>
 * @version 0.1 2007/09/13  Shiley Lin
 */
public class MerchantLoginCtl extends HttpServlet
{
    private static final String CONTENT_TYPE = "text/html; charset=Big5";
    private String initPassword;
    public static final String LOGIN_OLD_JSP = "Login_Old_Jsp"; //�n�J�ק�K�X-�Ѽ�
    public static final String LOGIN_OLD_CHANGE = "Login_Old_Change"; //�n�J�ק�K�X-�Ѽ�
    public static final String LOGIN_OLD_USER_STATUS = "Login_Old_User_Status"; //�n�J�ק�K�X-�Ѽ�
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
        int intQueryMonth = 0;
        Hashtable hashConfData = new Hashtable();
        Hashtable hashSys = new Hashtable(); // �t�ΰѼ�
        Hashtable hashMerUser = new Hashtable(); // �S���ϥΪ�
        Hashtable hashMerchant = new Hashtable(); // �S���D��
        ArrayList arrayTerminal = new ArrayList(); // �ݥ����D��
        //�����l�S���W��
        ArrayList arraySubMid = new ArrayList();// ���ݤl�S���C��
        
        RequestDispatcher myRequestDispatcher = null;
        HttpSession session = request.getSession(true);
        SessionControlBean scb = new SessionControlBean();
        try
        {
            System.out.println("-------------------- Start MerchantLoginCtl ----------------");
            //20180320�ˬd�ϫ����ҽX
            String szNO_UI= Util.objToStrTrim(request.getParameter("zNO"));
            String szNO_Session= Util.objToStrTrim(session.getAttribute("zNO"));
            System.out.println("zNO_UI:"+szNO_UI+",NO_Session:"+szNO_Session);
            if(!szNO_UI.equals(szNO_Session)){
                Message = "�ϫ����ҽX���~�A�Э��s��J";
                Forward = "./Merchant_Login.jsp";
                if (session.getAttribute("Message") != null){
                    session.removeAttribute("Message");
                }
                session.setAttribute("Message", Message);
                response.sendRedirect(Forward);
                return;
            }
            //�M���ϫ����ҽX
            session.removeAttribute("zNO");
            
            sysBean.setAutoCommit (false);
            MerchantLoginBean mlb = new MerchantLoginBean();
            Hashtable rtn = new Hashtable();
            rtn = init_Sys_Parm_List(sysBean); // �t�ΰѼ�

            hashSys = (Hashtable) rtn.get("hashData");
            intPwdChangDay = Integer.parseInt(String.valueOf(rtn.get("intPwdChangDay")));
            intPwdAlertDay = Integer.parseInt(String.valueOf(rtn.get("intPwdAlertDay")));
            intMaxCheckPwd = Integer.parseInt(String.valueOf(rtn.get("intMaxCheckPwd")));
            intTimeOut = Integer.parseInt(String.valueOf(rtn.get("intTimeOut")));
            //�d�ߤ���϶�(��),�S��
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
            
            /** 2023/04/19 ���� : get_Merchant_User() (By : YC) *//** IT-TESTCASE-010-020-002-001*/
            hashMerUser = mlb.get_Merchant_User(sysBean, MerchantID, UserID, "<>", "D", String.valueOf(intPwdChangDay));
            session.setMaxInactiveInterval(intTimeOut * 60);

            //20220801
            //UserPwd = MerchantChangePwdCtl.getMsgDigestPwd(UserPwd); //���o�K�X�s�X�r��

            LogMerchantID = MerchantID;
            LogUserName =  UserID+"("+UserID+")";
            String UserFlag = mlb.check_Merchant_User(hashMerUser, MerchantID, UserID, UserPwd, intPwdChangDay, intPwdAlertDay, intMaxCheckPwd);
            System.out.println("UserFlag=" + UserFlag);
            // nouser:�L���ϥΪ� pwderror:�K�X���~ changpwd:�ݱK�X�ܧ� changNewpwd:�����n�J�ݱK�X�ܧ� changResetpwd:�K�X���m�ݱK�X�ܧ� changTimepwd:�K�X�w����ݱK�X�ܧ� pwdlock:�K�X��w changealert:���`���ݴ����ܧ�K�X ok:���` userlock:�ϥΪ̰���

            if (UserFlag.equalsIgnoreCase("ok")            || UserFlag.equalsIgnoreCase("changealert") ||
                UserFlag.equalsIgnoreCase("changpwd")      || UserFlag.equalsIgnoreCase("changNewpwd") ||
                UserFlag.equalsIgnoreCase("changResetpwd") || UserFlag.equalsIgnoreCase("changTimepwd"))
            {
                // �ϥΪ̥��T �� �����ܧ�K�X �� ���w��ܱK�X�ܧ�
            	String sSignBill = Util.objToStrTrim(hashMerUser.get("SIGN_BILL"));
                hashMerchant = mlb.get_Merchant(sysBean, MerchantID,sSignBill); //�S���D��
                arrayTerminal = mlb.get_Terminal(sysBean, MerchantID,sSignBill); // �ݥ����D��
                arraySubMid = mlb.get_SubMid(sysBean, MerchantID,sSignBill); //���o�l�S���M��
                if (hashMerchant.size() > 0)
                {
                    if (arrayTerminal.size() > 0)
                    {
                        String SessionID = session.getId();
                        System.out.println("------------ SessionID = "+SessionID);
                        String Today = UserBean.get_TransDate("yyyy/MM/dd");

                        // �T�{�ϥΪ̬O�_�n�J
                        boolean Session_Flag = scb.check_Session(sysBean, MerchantID, UserID, SessionID, session, Today);
                        System.out.println("----------- Session_Flag = "+Session_Flag);
                        if (Session_Flag)
                        {
                            if (scb.credit_Session(sysBean, MerchantID, UserID, SessionID))
                            {
                                // session �إߦ��\
                                Forward = "./Merchant_Index.jsp";
                                LogStatus = "���\";

                                ArrayList arrayMerRole = mlb.get_Merchant_User_Role(sysBean, MerchantID,UserID, "MerchantMsgBoard" );  //�d�ߤ��i�T���O�_�s�b
                                if (arrayMerRole.size()>0)
                                {
                                    session.setAttribute("MsgBoardFlag", "Y");
                                }

                                if (UserFlag.equalsIgnoreCase("changealert"))
                                {
                                    String PwdDueDate = hashMerUser.get("PWDDUE_DATE").toString();
                                    Message = "�K�X�Y�N�� " + PwdDueDate + " ����A�аO�o�i��K�X�ܧ�";
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
                                    Message = "�n���ݧ@�K�X�ܧ�";
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
                                //�N�l�S���M���J�t�ΰѼƤ�
                                hashConfData.put("SUBMID",arraySubMid);
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
                            Hashtable hashSessionData = scb.get_Session(MerchantID,UserID, Today);
                            session.setAttribute("SESSIONDATA", hashSessionData);
                            Forward = "./Merchant_ReLogin.jsp";
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
            }
            else
            {
                if (UserFlag.equalsIgnoreCase("pwdlock"))
                {
                    Message = "�ϥΪ̤w��w";
                }
                else
                {
                    if (UserFlag.equalsIgnoreCase("userlock"))
                    {
                        Message = "�ϥΪ̰���";
                    }
                    else
                    {
                        if (UserFlag.equalsIgnoreCase("pwdexpire"))
                        {
                            Message = "�K�X�O���Ь��t�κ޲z�H��";
                        }
                        else
                        {
                            Message = "�ϥΪ̱b���K�X���~";
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
     * ���o�t�ΰѼ��� SYS_PARM_LIST
     * @return Hashtable �t�ΰѼƸ��
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
        	/** 2023/04/19 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** IT-TESTCASE-010-020-002-001, TESTCASE-001*/
        	arraySYS = (ArrayList) sysBean.QuerySQLByParam(sql.toString());
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
            if (ParmID.equalsIgnoreCase("MER_LIMIT_QRY_MONTH"))
            {
                result.put("intQuryMonth", ParmValue);
            }
        }

        result.put("hashData", hashData);
        return result;
    }
}
