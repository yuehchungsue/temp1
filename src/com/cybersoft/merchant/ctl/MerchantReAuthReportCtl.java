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
 * <p>��������Servlet</p>
 * @version 0.1 2008/05/04  Shiley Lin
 */
public class MerchantReAuthReportCtl extends HttpServlet
{
    private static final String CONTENT_TYPE = "text/html; charset=Big5";
    private String Forward = ""; // ������}
    private String Message = ""; // ��ܰT��
    java.util.Date nowdate;
    public static final LogUtils log_user = new LogUtils("user");
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    public UserBean UserBean = new UserBean();
    public DataBaseBean2 sysBean = new DataBaseBean2();
    String LogUserName = "";
    String LogFunctionName = "�A���v����έp����";
    String LogStatus = "���\";
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
            Hashtable hashSys = new Hashtable(); // �t�ΰѼ�
            Hashtable hashMerUser = new Hashtable(); // �S���ϥΪ�
            Hashtable hashMerchant = new Hashtable(); // �S���D��
            ArrayList arrayTerminal = new ArrayList(); // �ݥ����D��
            Hashtable hashConfData = (Hashtable) session.getAttribute("SYSCONFDATA");
            if (hashConfData == null) hashConfData = new Hashtable();
            boolean Merchant_Current =  false;   // �S�����A
            boolean Merchant_Permit =  false;   // �S���v��
            if (hashConfData.size()>0)
            {
                hashSys = (Hashtable)hashConfData.get("SYSCONF"); // �t�ΰѼ�
                hashMerUser = (Hashtable)hashConfData.get("MERCHANT_USER"); // �S���ϥΪ�
                hashMerchant = (Hashtable)hashConfData.get("MERCHANT"); // �S���D��

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

                arrayTerminal = (ArrayList)hashConfData.get("TERMINAL"); // �ݥ����D��
                if (arrayTerminal==null)
                    arrayTerminal = new ArrayList();
            }

            // UserBean UserBean = new UserBean();
            Merchant_Current = UserBean.check_Merchant_Column(hashMerchant,"CURRENTCODE","B,C,D");  //  �T�{�S�����A
            Merchant_Permit = UserBean.check_Merchant_Column(hashMerchant,"PERMIT_INQUIRY_TX","Y");  //  �T�{�S���v��
            boolean boolForwardFlag = false;
            String MenuKey = (String)request.getParameter(MerchantMenuCtl.MENU_FORWARD_KEY);
            request.setAttribute(MerchantMenuCtl.MENU_FORWARD_KEY,MenuKey);
            boolean User_Permit = MerchantMenuCtl.getUserRole(sysBean, session, MenuKey);

            if (User_Permit)
            {
                // �ϥΪ��v��
                if (Merchant_Current)
                {
                    //�S�����A
                    if (Merchant_Permit)
                    {
                        // �S���v��
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
                                String ReauthFlagDesc = "����";

                                if (ReauthFlag.equalsIgnoreCase("Y"))
                                {
                                    ReauthFlagDesc = "�֭�";
                                }

                                if (ReauthFlag.equalsIgnoreCase("N"))
                                {
                                    ReauthFlagDesc = "�ڵ�";
                                }

                                if (ReauthFlag.equalsIgnoreCase("R"))
                                {
                                    ReauthFlagDesc = "�A���v";
                                }

                                field.put("pReauthFlag", ReauthFlagDesc);
                                String RPTName = "ReAuthReport.rpt";
                                System.out.println("PTName=" + RPTName);
                                System.out.println("PrintType=" + PrintType);
                                cr.createPDF(sysBean,SecurityTool.output(sql), request, response, "/Report/" + RPTName, field, PrintType);
                                // ReAuthReportBean.closeConn();

                                LogMemo = "�d��"+StartDate+"~"+EndDate+"�A�A���v���G="+ReauthFlag+"�A���v����έp����";
                                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                log_user.debug(LogData);
                            }
                        }
                    }
                    else
                    {
                        Message = "�S���L���\���v��";
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
                Message = "�ϥΪ̵L���v���Ь��t�κ޲z��";
                Forward = "./Merchant_NoUse.jsp";
                session.setAttribute("Message", Message);
                boolForwardFlag = true;
            }

            if (Message.length()>0)
            {
                LogMemo = Message;
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "����", LogMemo);
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
        //20130702 Jason �W�[finally�B�zsysBean.close
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
