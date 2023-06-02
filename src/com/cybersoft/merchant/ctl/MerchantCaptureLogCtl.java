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
 * <p>����n�J�t�Ϊ�Servlet</p>
 * @version 0.1 2007/09/19  Shiley Lin
 */
public class MerchantCaptureLogCtl extends HttpServlet
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
    String LogFunctionName = "�дڥ���d��";
    String LogStatus = "���\";
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

            boolean Merchant_Current =  false;   // �S�����A
            boolean Merchant_Permit =  false;   // �S���v��
            if (hashConfData.size()>0)
            {
                hashSys = (Hashtable)hashConfData.get("SYSCONF"); // �t�ΰѼ�
                queryMax = Integer.parseInt( hashSys.get("MER_CAPTURE_QRY_QUANTITY").toString());//���v�d�̰߳�����
                hashMerUser = (Hashtable)hashConfData.get("MERCHANT_USER"); // �S���ϥΪ�
                hashMerchant = (Hashtable)hashConfData.get("MERCHANT"); // �S���D��
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

                arrayTerminal = (ArrayList)hashConfData.get("TERMINAL"); // �ݥ����D��
                if (arrayTerminal==null)
                    arrayTerminal = new ArrayList();
            }

            // UserBean UserBean = new UserBean();
            System.out.println("<"+hashMerchant+">");
            Merchant_Current = UserBean.check_Merchant_Column(hashMerchant,"CURRENTCODE","B,C,D");  //  �T�{�S�����A
            Merchant_Permit = UserBean.check_Merchant_Column(hashMerchant,"PERMIT_INQUIRY_TX","Y");  //  �T�{�S���v��
            String reportflag="";

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
                        
                        // �дڥ���d�� �s�W �d�߱��� TransType by Jimmy Kang 20150727  -- �ק�}�l --
                        String transtype = (request.getParameter("TransType") == null) ? "" : UserBean.trim_Data(request.getParameter("TransType"));
                        // �дڥ���d�� �s�W �d�߱��� TransType by Jimmy Kang 20150727  -- �קﵲ�� --
                        
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
                        
                        // �дڥ���d�� �s�W �j�M���� TransType by Jimmy Kang 20150727  -- �ק�}�l --
                        request.setAttribute("TransType", transtype);
                        // �дڥ���d�� �s�W �j�M���� TransType by Jimmy Kang 20150727  -- �קﵲ�� --
                        
                        MerchantCaptureLogBean malb = new MerchantCaptureLogBean();
                        if (startdate != null && !startdate.equals("") && (reportflag == null ||
                            !reportflag.equalsIgnoreCase("ture")))
                        {
                            //���d�߱���
                            Forward = "./MerchantCaptureLogQueryResult.jsp";
                            int tempcon=0;
                            ArrayList capturelog =new ArrayList();
                            if(checkpoint.equals("")){
                            	 // get_CaptureLog_Count method �s�W�޼� TransType by Jimmy Kang 20150727
                          	     // �дڥ���d�� �s�W �d�߱��� TransType by Jimmy Kang 20150727  -- �ק�}�l --
                            	 capturelog = malb.get_CaptureLog_Count(sysBean, merchantid, submid, startdate, enddate, transcode, transtype,
                                        ordertype, orderid, authid, terminalid, capturetype, exceptFlag,type);
                            	 // �дڥ���d�� �s�W �d�߱��� TransType by Jimmy Kang 20150727  -- �קﵲ�� --
                            	 tempcon=capturelog==null ||capturelog.size()==0?tempcon :Integer.parseInt(((Hashtable)capturelog.get(0)).get("COUNT").toString());
                            }
                            if(tempcon <= queryMax || checkpoint.equals("XXX") ){
                            	request.setAttribute("checkpoint", "XXX");
                            	// get_CaptureLog_List method �s�W�޼� TransType by Jimmy Kang 20150727
                         	    // �дڥ���d�� �s�W �d�߱��� TransType by Jimmy Kang 20150727  -- �ק�}�l --
	                            capturelog = malb.get_CaptureLog_List(sysBean, merchantid, submid, startdate, enddate, transcode, transtype,
	                                                                            ordertype, orderid, authid, terminalid, capturetype, exceptFlag,type);
	                            // �дڥ���d�� �s�W �d�߱��� TransType by Jimmy Kang 20150727  -- �קﵲ�� --
                            }
                            session.removeAttribute("Capturelog");
//                            if(capturelog.size()> queryMax){
//                            	errMessage ="�����d�ߵ��G�w�W�L���ƤW��(�̤j����:"+String.valueOf(queryMax)+")";
//                            }
                            session.setAttribute("Capturelog", capturelog);
                            LogMemo = "�d�ߩ��Ӹ��";
                        }
                        else if (sysorderid != null && detail != null && detail.equalsIgnoreCase("true"))
                        {
                            Forward = "./MerchantCaptureLogQueryDetail.jsp";
                            merchantid = UserBean.trim_Data(request.getParameter("MerchantId"));
                            submid = UserBean.trim_Data(request.getParameter("SubMid"));
                            String rowid = UserBean.trim_Data(request.getParameter("RowId"));
                            Hashtable detail_log = malb.get_CaptureLog_View(sysBean, merchantid, submid, sysorderid, rowid);
                            session.setAttribute("DetailLog", detail_log);
                            LogMemo = "�˵�"+sysorderid+"���Ӹ��";
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
                            // get_CaptureLog_List_rs method �s�W�޼� TransType by Jimmy Kang 20150727
                     	    // �дڥ���d�� �s�W �d�߱��� TransType by Jimmy Kang 20150727  -- �ק�}�l --
                            String sql = malb.get_CaptureLog_List_rs(sysBean, merchantid, submid, startdate,
                                                enddate, transcode, transtype, ordertype, orderid, authid, terminalid,
                                                capturetype, RowdataFlag, exceptFlag);
                            // �дڥ���d�� �s�W �d�߱��� TransType by Jimmy Kang 20150727  -- �קﵲ�� --
                            
                            createReport cr = new createReport();
                            Hashtable field = new Hashtable();
                            field.put("startdate", startdate);
                            field.put("enddate", enddate);
                            String terminaltype = "";

                            if (terminalid.equalsIgnoreCase("ALL"))
                                terminaltype="����";
                            else
                                terminaltype = terminalid;

                            field.put("terminaltype", terminaltype);
                            String exceptFlagDesc = "����";
                            System.out.println("-----------exceptFlag1 ="+exceptFlag);
                            if (exceptFlag.equalsIgnoreCase("ALL")) exceptFlagDesc = "����";
                            if (exceptFlag.equalsIgnoreCase("MERCHANT")) exceptFlagDesc = "�S���д�";
                            if (exceptFlag.equalsIgnoreCase("ACQ")) exceptFlagDesc = "�����ɽд�";
                            if (query_submid.equalsIgnoreCase("ALL")){
                            	query_submid = "����";
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
                            
                            // �дڥ���d�� �s�W TransType by Jimmy Kang 20150727  -- �ק�}�l --
                            String transtypeDesc = "";
                            if (transtype.equalsIgnoreCase("SSL"))
                            {
                            	transtypeDesc = "�H�Υd";
                            }
                            else if (transtype.equalsIgnoreCase("ALL"))
                            {
                            	transtypeDesc = "����";
                            }
                            else
                            {
                            	transtypeDesc = transtype;
                            }
                            field.put("transtype", transtypeDesc);
                            // �дڥ���d�� �s�W TransType by Jimmy Kang 20150727  -- �קﵲ�� --

                            System.out.println("rptname="+rptname);
                            cr.createPDF(sysBean,SecurityTool.output(sql), request, response, "/Report/" + rptname, field, printtype);
                            // malb.closeConn();
                            LogMemo = "�H"+printtype+"�榡�ץX";
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

            if (Message.length()>0)
            {
                session.setAttribute("Message", Message);
                LogMemo = Message;
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "����", LogMemo);
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
