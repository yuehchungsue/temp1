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
 * <p>����n�J�t�Ϊ�Servlet</p>
 * @version 0.1 2007/09/19  Shiley Lin
 */
public class MerchantAuthLogCtl extends HttpServlet
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
    String LogFunctionName = "���v����d��";
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
            Hashtable hashSys = new Hashtable(); // �t�ΰѼ�
            Hashtable hashMerUser = new Hashtable(); // �S���ϥΪ�
            Hashtable hashMerchant = new Hashtable(); // �S���D��
            ArrayList arrayTerminal = new ArrayList(); // �ݥ����D��
            //�l�S���M��
            ArrayList subMidList = new ArrayList();
          //�O�_����@�S��
            boolean isSignMer = false;
            Hashtable hashConfData = (Hashtable) session.getAttribute("SYSCONFDATA");

            if (hashConfData == null)
                hashConfData = new Hashtable();

            boolean Merchant_Current = false; // �S�����A
            boolean Merchant_Permit = false; // �S���v��
            if (hashConfData.size() > 0)
            {
                hashSys = (Hashtable) hashConfData.get("SYSCONF"); // �t�ΰѼ�
                hashMerUser = (Hashtable) hashConfData.get("MERCHANT_USER"); // �S���ϥΪ�
                hashMerchant = (Hashtable) hashConfData.get("MERCHANT"); // �S���D��
                queryMax = Integer.parseInt( hashSys.get("MER_AUTH_QRY_QUANTITY").toString());//���v�d�̰߳�����
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

                arrayTerminal = (ArrayList) hashConfData.get("TERMINAL"); // �ݥ����D��
                if (arrayTerminal == null)
                    arrayTerminal = new ArrayList();
            }

            // UserBean UserBean = new UserBean();
            Merchant_Current = UserBean.check_Merchant_Column(hashMerchant, "CURRENTCODE", "B,C,D"); //  �T�{�S�����A
            Merchant_Permit  = UserBean.check_Merchant_Column(hashMerchant, "PERMIT_INQUIRY_TX", "Y"); //  �T�{�S���v��
            String reportflag = "";
            String MenuKey = (String) request.getParameter(MerchantMenuCtl.MENU_FORWARD_KEY);
            request.setAttribute(MerchantMenuCtl.MENU_FORWARD_KEY, MenuKey);
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
                        
                        // ���v����d�� �s�W �j�M���� TransType by Jimmy Kang 20150721  -- �ק�}�l --
                        String transtype = (request.getParameter("TransType") == null) ? "" : UserBean.trim_Data(request.getParameter("TransType"));
                        // ���v����d�� �s�W �j�M���� TransType by Jimmy Kang 20150721  -- �קﵲ�� --
                        
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
                        
                        // ���v����d�� �s�W �j�M���� TransType by Jimmy Kang 20150721  -- �ק�}�l --
                        request.setAttribute("TransType", transtype);
                        // ���v����d�� �s�W �j�M���� TransType by Jimmy Kang 20150721  -- �קﵲ�� --
                        
                        MerchantAuthLogBean malb = new MerchantAuthLogBean();

                        if (startdate != null && !startdate.equals("") && (reportflag == null || !reportflag.equalsIgnoreCase("ture")))
                        {
                            //���d�߱���
                            Forward = "./MerchantAuthLogQueryResult.jsp";
                            int tempcon=0;
                            ArrayList authlog =new ArrayList();
                            if(checkpoint.equals("")){
                            	
                            	  // get_AuthLog_Count method �s�W�޼� TransType by Jimmy Kang 20150721
                            	  // ���v����d�� �s�W �d�߱��� TransType by Jimmy Kang 20150721  -- �ק�}�l --
                            	  authlog = malb.get_AuthLog_Count(sysBean, merchantid, submid, startdate,
                                          enddate, starttime, endtime, transcode, transtype, transstatus, ordertype, orderid,
                                          authid, terminalid, capturetype);
                            	  // ���v����d�� �s�W �d�߱��� TransType by Jimmy Kang 20150721  -- �קﵲ�� --
                            	  
                            	  tempcon=Integer.parseInt(((Hashtable)authlog.get(0)).get("TOTAL").toString());
                            }
                            if(tempcon <= queryMax || checkpoint.equals("XXX") ){
                            	request.setAttribute("checkpoint", "XXX");
                            
                             // get_AuthLog_List method �s�W�޼� TransType by Jimmy Kang 20150721
                             // ���v����d�� �s�W �d�߱��� TransType by Jimmy Kang 20150721  -- �ק�}�l --
                             authlog = malb.get_AuthLog_List(sysBean, merchantid, submid, startdate,
                            		    enddate, starttime, endtime, transcode, transtype, transstatus, ordertype, orderid,
                                        authid, terminalid, capturetype);
                             // ���v����d�� �s�W �d�߱��� TransType by Jimmy Kang 20150721  -- �קﵲ�� --
                             
//                            if(authlog.size()> queryMax){
//                            	errMessage ="�����d�ߵ��G�w�W�L���ƤW��(�̤j����:"+String.valueOf(queryMax)+")";
//                            }
                            }
                            session.removeAttribute("AuthLog");
                            session.setAttribute("AuthLog", authlog);
                            LogMemo = "�d�ߩ��Ӹ��";
                        }
                        else if (sysorderid != null && detail != null && detail.equalsIgnoreCase("true"))
                        {
                            Forward = "./MerchantAuthLogQueryDetail.jsp";
                            merchantid = request.getParameter("MerchantId");
                            submid = request.getParameter("SubMid");
//                            rowid= URLDecoder.decode(rowid);
                            Hashtable detail_log = malb.get_AuthLog_View(sysBean, merchantid, submid, sysorderid, rowid);
                            session.setAttribute("DetailLog", detail_log);
                            LogMemo = "�˵�"+sysorderid+"���Ӹ��";
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
                                rpttranscode = "����";

                            if (transcode.equalsIgnoreCase("00"))
                                rpttranscode = "00-�ʳf���";

                            if (transcode.equalsIgnoreCase("01"))
                                 rpttranscode = "01-�h�f���";

                            if (transcode.equalsIgnoreCase("10"))
                                rpttranscode = "10-�ʳf�������";

                            if (transcode.equalsIgnoreCase("11"))
                                rpttranscode = "11-�h�f�������";

                            System.out.println("rpttranscode="+rpttranscode);
                            String rpttransstatus = "";
                            if (transstatus.equalsIgnoreCase("ALL"))
                                rpttransstatus = "����";

                            if (transstatus.equalsIgnoreCase("A"))
                                rpttransstatus = "A-Approved";

                            if (transstatus.equalsIgnoreCase("D"))
                                rpttransstatus = "D-Declined";

                            if (transstatus.equalsIgnoreCase("C"))
                                rpttransstatus = "C-Call Bank";
                            
                            // ���v����d�� �s�W ������A "P-Pending" by Jimmy Kang 20150721  -- �ק�}�l --
                            if (transstatus.equalsIgnoreCase("P"))
                                rpttransstatus = "P-Pending";
                            // ���v����d�� �s�W ������A "P-Pending" by Jimmy Kang 20150721  -- �קﵲ�� --

                            System.out.println("rpttransstatus="+rpttransstatus);
                            
                            // ���v����d�� �s�W TransType by Jimmy Kang 20150721  -- �ק�}�l --
                            String rpttranstype = "";
                            if (transtype.equalsIgnoreCase("ALL"))
                            {
                            	rpttranstype = "����";
                            }
                            else if (transtype.equalsIgnoreCase("SSL"))
                            {
                            	rpttranstype = "�H�Υd";
                            }
                            else
                            {
                            	rpttranstype = transtype;
                            }
                            System.out.println("rpttranstype="+rpttranstype);
                            // ���v����d�� �s�W TransType by Jimmy Kang 20150721  -- �קﵲ�� --

                            String rptordertype = "";
                            if (ordertype.equalsIgnoreCase("M"))
                                rptordertype = "�S�����w�渹";

                            if (ordertype.equalsIgnoreCase("S"))
                                rptordertype = "�t�Ϋ��w�渹";

                            System.out.println("rptordertype="+rptordertype);
                            System.out.println("orderid="+orderid);
                            System.out.println("authid="+authid);

                            String rptterminalid = "";
                            if (terminalid.equalsIgnoreCase("ALL"))
                            {
                                rptterminalid = "����";
                            }
                            else
                            {
                                rptterminalid = terminalid;
                            }

                            System.out.println("rptterminalid="+rptterminalid);

                            String rptcapturetype = "";
                            if (capturetype.equalsIgnoreCase("ALL"))
                                rptcapturetype = "����";

                            if (capturetype.equalsIgnoreCase("Capture"))
                                rptcapturetype = "�w�д�";

                            if (capturetype.equalsIgnoreCase("NotCapture"))
                                rptcapturetype = "���д�";
                            System.out.println("query_submid="+query_submid);
                            if (query_submid.equalsIgnoreCase("ALL")){
                            	query_submid = "����";
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
                            
                            // ���v����d�� �s�W TransType by Jimmy Kang 20150721  -- �ק�}�l --
                            field.put("transtype", rpttranstype);
                            // ���v����d�� �s�W TransType by Jimmy Kang 20150721  -- �קﵲ�� --
                            
                            cr.createPDF(sysBean,SecurityTool.output(sql), request, response, "/Report/" + rptname, field, printtype);
                            // malb.closeConn();
                            LogMemo = "�H"+printtype+"�榡�ץX";
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
                        Message = "�S���L���\���v��";
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
                Message = "�ϥΪ̵L���v���Ь��t�κ޲z��";
                Forward = "./Merchant_NoUse.jsp";
            }

            if (Message.length() > 0)
            {
                session.setAttribute("Message", Message);
                LogMemo = Message;
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID,LogUserName, LogFunctionName, "����", LogMemo);
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
