/************************************************************
 * <p>#File Name:   MerchantCaptureCtl.java         </p>
 * <p>#Description:                         </p>
 * <p>#Create Date: 2007/10/02              </p>
 * <p>#Company:     cybersoft               </p>
 * <p>#Notice:                      </p>
 * @author      Shirley Lin
 * @since       SPEC version
 * @version 0.1 2007/10/02  Shirley Lin
 * 202007070145-00 20200707 HKP ���]��|�ݨDUI�W�[���дڥ\��
 *    Tag:20200708-01
 *    1.UI:�_��B����B������O(�����B�ʳf�B�h�f)
 *    2.����`�ʳf���ơB���B�B�`�h�f���ơB���B�AUSER�T�w��i����дڡA�A��ܵ��G
 * 202112300619-01 20220210 GARY �дڧ妸�дڳW��W��(Visa Authorization Source Code)
 ************************************************************/
package com.cybersoft.merchant.ctl;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.w3c.util.UUID;

/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft.bean.LogUtils;
import com.cybersoft.bean.SessionControlBean;
import com.cybersoft.bean.UserBean;
import com.cybersoft.bean.createReport;
import com.cybersoft.bean.emDataType;
import com.cybersoft.common.Util;
import com.cybersoft.merchant.bean.MerchantCaptureBean;
import com.fubon.security.filter.SecurityTool;
/**
 * <p>����дڪ�Servlet</p>
 * @version 0.1 2007/10/02  Shiley Lin
 */
public class MerchantCaptureCtl extends HttpServlet
{
	private static final long serialVersionUID = 1L;
    private static final String CONTENT_TYPE = "text/html; charset=Big5";
    public static final LogUtils log_user = new LogUtils("user");
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");

    public void init()
    {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        this.doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String LogFunctionName = "�u�W�дڧ@�~";
        String LogStatus = "����";
    	//20150910  �{���ϥζǻ���ƥΪ��󤣨ϥΥ����ܼ� Start
    	System.out.println("Online Capture Ver:20160216");
        ArrayList arrayData = new ArrayList();        
        Hashtable hashData = new Hashtable();
        Hashtable hashMerUser = new Hashtable(); // �S���ϥΪ�
        Hashtable hashMerchant = new Hashtable(); // �S���D��        
        ArrayList arrayTerminal = new ArrayList(); // �ݥ����D��
        Hashtable hashSys = new Hashtable(); // 20150910 �t�ΰѼƤ@�P�ҥH�������J        
        Hashtable hashConfData = new Hashtable();
        DataBaseBean2 sysBean = new DataBaseBean2();
        UserBean UserBean = new UserBean();
        String LogUserName = "";
        String LogMemo = "";
        String LogData = "";
        String LogMerchantID = "";
        String BatchPmtID = "";
        //20150910 �{���ϥζǻ���ƥΪ��󤣨ϥΥ����ܼ� END
        String MerchantID="";
        String SubMID = "";
        String SubMID_UI = "";
        String UserID="";
        
        //20160215 Jason ����dopost �ŧi
        String Forward = "./Merchant_Response.jsp"; // ������}
        String Message = ""; // ��ܰT��
        response.setContentType(CONTENT_TYPE);
        request.setCharacterEncoding("BIG5");

        HttpSession session = request.getSession(true);
        /* Chech Session */
        SessionControlBean scb = new SessionControlBean();       
        boolean  isSubMid =false;
        try
        {
        	//session����
            scb = new SessionControlBean(session, request, response);
            sysBean.setAutoCommit(false);
            //�S���n�J�ɯS����T
            hashConfData = (session.getAttribute("SYSCONFDATA")!=null)
            		?(Hashtable)session.getAttribute("SYSCONFDATA"): null;
            if(hashConfData == null || hashConfData.size()==0) {
                log_systeminfo.debug("--MerchantCaptureCtl:�S����T���ĽЭ��s�n�J,SYSCONFDATA is null");
                request.setAttribute("errMsg", "�S����T���ĽЭ��s�n�J(SYSCONFDATA)");
                request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
                return;
            }
            //���^��T
            /**********/
            if (hashConfData.get("SYSCONF") == null || ((Hashtable)hashConfData.get("SYSCONF")).size()==0) {
                log_systeminfo.debug("--MerchantCaptureCtl:�S����T���ĽЭ��s�n�J,SYSCONF is null");
                request.setAttribute("errMsg", "�S����T���ĽЭ��s�n�J(SYSCONF)");
                request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
                return;
            }
            hashSys = (Hashtable) hashConfData.get("SYSCONF"); // �t�ΰѼ�
            /****MERCHANT_USER******/
            if (hashConfData.get("MERCHANT_USER") == null ||((Hashtable)hashConfData.get("MERCHANT_USER")).size()==0) {
                log_systeminfo.debug("--MerchantCaptureCtl:�S����T���ĽЭ��s�n�J,MERCHANT_USER is null");
                request.setAttribute("errMsg", "�S����T���ĽЭ��s�n�J(MERCHANT_USER)");
                request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
                return;
            }
            hashMerUser = (Hashtable) hashConfData.get("MERCHANT_USER"); // �S���ϥΪ�
            /****MERCHANT******/
            if (hashConfData.get("MERCHANT") == null || ((Hashtable)hashConfData.get("MERCHANT")).size() ==0) {
                log_systeminfo.debug("--MerchantCaptureCtl:�S����T���ĽЭ��s�n�J,MERCHANT is null");
                request.setAttribute("errMsg", "�S����T���ĽЭ��s�n�J(MERCHANT)");
                request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
                return;
            }
            hashMerchant = (Hashtable) hashConfData.get("MERCHANT"); // �S���D��
            /****get infor******/
            isSubMid =((String) hashMerUser.get("ISSUBMERCHANT")).equals("Y") ?  true : false;
            LogMerchantID = (String)hashMerUser.get("MERCHANT_ID");
            MerchantID = hashMerchant.get("MERCHANTID").toString();
            UserID = hashMerUser.get("USER_ID").toString();
            SubMID =(hashMerUser.get("SUBMID") != null) ? hashMerUser.get("SUBMID").toString():"";
        }
        catch (UnsupportedOperationException E)
        {
            E.toString();
            request.getRequestDispatcher("/Merchant_Bye.jsp").forward(request, response);
            return;
        }

        try
        {
            boolean ForwardFlag = true;
            String MenuKey = (String) request.getParameter(MerchantMenuCtl.MENU_FORWARD_KEY);
            request.setAttribute(MerchantMenuCtl.MENU_FORWARD_KEY, MenuKey);
            String Action = Util.objToStrTrim(request.getParameter("Action"));
            if (Action.length() == 0)
            {
                //�������d�ߵe��
                if (session.getAttribute("CaptureQuery") != null)
                {
                    session.removeAttribute("CaptureQuery");
                }
                Forward = "./Merchant_Capture_Query.jsp";
                System.out.println("Forward=" + Forward);
                request.getRequestDispatcher(Forward).forward(request, response);
                return;
            }
            arrayTerminal = (ArrayList) hashConfData.get("TERMINAL"); // �ݥ����D��
            if (arrayTerminal == null)
                arrayTerminal = new ArrayList();

            boolean Merchant_Current = UserBean.check_Merchant_Column(hashMerchant, "CURRENTCODE", "B,D,E,F"); //  �T�{�ݥ������A B,D�i�ʳf�д� B,D,E,F�i�h�f�д�
            boolean Merchant_Permit = UserBean.check_Merchant_Column(hashMerchant, "PERMIT_CAPTURE", "Y"); //  �T�{�S���v��
            boolean Merchant_Permit1 = UserBean.check_Merchant_Column(hashMerchant, "CAPTURE_MANUAL", "Y"); //  �T�{�S���v��
            boolean User_Permit = MerchantMenuCtl.getUserRole(sysBean, session, MenuKey);
            Date date = new Date();
            String today= new SimpleDateFormat("yyyy/MM/dd").format(date);
            boolean isLogOutFlag = scb.check_Session(hashMerUser.get("MERCHANT_ID").toString(), hashMerUser.get("USER_ID").toString(), session.getId(), session, today);
            if(isLogOutFlag == true) {
                Message = "�S���n�J���ĽЭ��s�n�J";
                Forward = "./Merchant_NoUse.jsp";
                session.setAttribute("Message", Message);
                LogMemo = Message;
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                log_user.debug(LogData);
                System.out.println("Forward=" + Forward);
                request.getRequestDispatcher(Forward).forward(request, response);
                return;
            }
            if(User_Permit == false) {
                Message = "�S���ө�/�ϥΪ̵L���v���Ь�����B�z";
                Forward = "./Merchant_NoUse.jsp";
                session.setAttribute("Message", Message);
                LogMemo = Message;
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                log_user.debug(LogData);
                System.out.println("Forward=" + Forward);
                request.getRequestDispatcher(Forward).forward(request, response);
                return;
            }
            // �ϥΪ��v��
            if(Merchant_Current == false) {
                String CurrentCode = hashMerchant.get("CURRENTCODE").toString();
                Message = UserBean.get_CurrentCode(CurrentCode);
                Forward = "./Merchant_Response.jsp";
                session.setAttribute("Message", Message);
                LogMemo = Message;
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                log_user.debug(LogData);
                System.out.println("Forward=" + Forward);
                request.getRequestDispatcher(Forward).forward(request, response);
                return;
            }
          //�S�����A
            if(Merchant_Permit ==false || Merchant_Permit1==false) {
                Message = "�S���L���\���v��";
                Forward = "./Merchant_Response.jsp";
                session.setAttribute("Message", Message);
                LogMemo = Message;
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                log_user.debug(LogData);
                System.out.println("Forward=" + Forward);
                request.getRequestDispatcher(Forward).forward(request, response);
                return;
            }

            MerchantCaptureBean CaptureBean = new MerchantCaptureBean();
            System.out.println("Action=" + Action);
            if (Action.equalsIgnoreCase("Query") || Action.equalsIgnoreCase("FirstQuery"))
            {
            	//�d�߽дڦC��
                String StartTransDate = "";
                String EndTransDate = "";
                String TerminalID = "";
                String TransCode = "";
                String CheckFlag = "";
                String OrderType = "";
                String OrderID = "";
                
                // Merchant Console �u�W�дڧ@�~�Ҳ�  �s�WTransType�ܼ�  by Jimmy Kang 20150515 --�s�W�}�l--
                String TransType = "";
                // Merchant Console �u�W�дڧ@�~�Ҳ�  �s�WTransType�ܼ�  by Jimmy Kang 20150515 --�s�W����--
               
                String page_no = UserBean.trim_Data(request.getParameter("page_no"));
                if (page_no == null)
                    page_no = "0";

                Hashtable hashQuery = new Hashtable();
                System.out.println("session.getAttribute('CaptureData')="+session.getAttribute("CaptureData") +",");
                if (session.getAttribute("CaptureData") != null && Action.equalsIgnoreCase("Query"))
                {
                    Hashtable hashAll = (Hashtable) session.getAttribute("CaptureData");
                    hashQuery = (Hashtable) hashAll.get("QUERY");
                    StartTransDate = (String) hashQuery.get("StartTransDate");
                    EndTransDate = (String) hashQuery.get("EndTransDate");
                    TerminalID = (String) hashQuery.get("TerminalID");
                    TransCode = (String) hashQuery.get("TransCode");

                    //Merchant Console �u�W�дڧ@�~�Ҳ�  �s�W  by Dale Peng 20150521 --�s�W�}�l--
                    TransType = (String) hashQuery.get("TransType");
                    //Merchant Console �u�W�дڧ@�~�Ҳ�  �s�W  by Dale Peng 20150521 --�s�W����--

                    CheckFlag = (String) hashQuery.get("CheckFlag");
                    OrderType = (String) hashQuery.get("OrderType");
                    OrderID = (String) hashQuery.get("OrderID");
                }
                else
                {
                	StartTransDate = Util.objToStrTrim(request.getParameter("StartTransDate"));
                    hashQuery.put("StartTransDate", StartTransDate);
                    EndTransDate = Util.objToStrTrim(request.getParameter("EndTransDate"));
                    hashQuery.put("EndTransDate", EndTransDate);
                    TerminalID = Util.objToStrTrim(request.getParameter("TerminalID"));
                    if (TerminalID == "") TerminalID = "ALL";
                    hashQuery.put("TerminalID", TerminalID);
                    TransCode = Util.objToStrTrim(request.getParameter("TransCode"));
                    if (TransCode == "") TransCode = "ALL";
                    hashQuery.put("TransCode", TransCode);
                    
                    // Merchant Console �u�W�дڧ@�~�Ҳ�  �s�W  by Jimmy Kang 20150515 --�s�W�}�l--
                    // �NTransType��J�x�s�d�߱���hashQuery�� 
                    TransType = Util.objToStrTrim(request.getParameter("TransType"));
                    hashQuery.put("TransType", TransType);
                    // Merchant Console �u�W�дڧ@�~�Ҳ�  �s�W  by Jimmy Kang 20150515 --�s�W����--
                    
                    CheckFlag = Util.objToStrTrim(request.getParameter("CheckFlag"));
                    if (CheckFlag == "") CheckFlag = "Y";
                    hashQuery.put("CheckFlag", CheckFlag);
                    OrderType = Util.objToStrTrim(request.getParameter("OrderType"));
                    hashQuery.put("OrderType", OrderType);
                    OrderID = Util.objToStrTrim(request.getParameter("OrderID"));
                    hashQuery.put("OrderID", OrderID);
                }

                String CaptureDay = Util.objToStrTrim(hashSys.get("MER_CAPTURE_DAY"));
                System.out.println("CaptureDay="+CaptureDay);
                // Merchant Console �u�W�дڧ@�~�Ҳ�  �ק�  by Jimmy Kang 20150515 --�s�W�}�l--
                // �b  get_Capture_List method �s�W�Ѽ� TransType
                arrayData = CaptureBean.get_Capture_List(sysBean,  MerchantID, SubMID, StartTransDate,
                                EndTransDate, TerminalID, TransCode, OrderType, OrderID, "0", CaptureDay, TransType);
                // Merchant Console �u�W�дڧ@�~�Ҳ�  �ק�  by Jimmy Kang 20150515 --�s�W����--
                
                if (arrayData == null)
                    arrayData = new ArrayList();

                String PartialFlag = hashMerchant.get("PERMIT_PARTIAL_CAPTURE").toString();
                hashData.put("DATALIST", arrayData); // �d�߽дڸ��
                hashData.put("PARTIALFLAG", PartialFlag); // �O�_�i����д�
                hashData.put("CHECKFLAG", CheckFlag); // ���覡
                hashData.put("QUERY", hashQuery); // �d�߱���
                hashData.put("NOWPAGE", page_no); // �d�߭���
                /****** ���p�d�ݨD�ק� Dalepeng 20150618 ---Start ******/ 
                hashData.put("TRANSTYPE", TransType); // �d�O  DALE
                /****** ���p�d�ݨD�ק� Dalepeng 20150618 ---END ******/ 
                if (session.getAttribute("CaptureData") != null)
                {
                    session.removeAttribute("CaptureData");
                }

                session.setAttribute("CaptureData", hashData);
                LogMemo = "�дڸ�ƦC��@"+String.valueOf(arrayData.size())+"��";
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "���\", LogMemo);
                log_user.debug(LogData);
                Forward = "./Merchant_Capture_List.jsp";
            }//END if (Action.equalsIgnoreCase("Query") || Action.equalsIgnoreCase("FirstQuery"))
            if (Action.equalsIgnoreCase("Check"))
            {
                //�d�߽дڿ�����
                String InputSysOrderID = Util.objToStrTrim(request.getParameter("InputSysOrderID"));
                String InputCaptureAmt = Util.objToStrTrim(request.getParameter("InputCaptureAmt"));
                String SysOrderID[] = InputSysOrderID.split(",");
                String CaptureAmt[] = InputCaptureAmt.split(",");
                String CaptureDay = Util.objToStrTrim(hashSys.get("MER_CAPTURE_DAY"));
                arrayData = CaptureBean.get_Capture_Check_List(sysBean, MerchantID, SubMID,
                                            SysOrderID, CaptureAmt, CaptureDay);
                ArrayList arrayCardTest = UserBean.get_Test_Card(sysBean, "N","Y");   //  ���X���եd
                ArrayList arraySuccess = CaptureBean.check_Capture_Amt(arrayData, arrayCardTest);
                Hashtable hasSumAmt = CaptureBean.sum_Capture_Amt(arraySuccess);
                String PartialFlag = "";
                /****** ���p�d�ݨD�ק� Dalepeng 20150618 ---Start ******/ 
                String TransType = "";
                /****** ���p�d�ݨD�ק� Dalepeng 20150618 ---END ******/ 
                if (session.getAttribute("CaptureData") != null)
                {
                    Hashtable hashCaptureData = (Hashtable) session.getAttribute("CaptureData");
                    PartialFlag = hashCaptureData.get( "PARTIALFLAG").toString(); // �O�_�i����д�
                    /****** ���p�d�ݨD�ק� Dalepeng 20150618 ---Start ******/ 
                    TransType = hashCaptureData.get("TRANSTYPE").toString();
                    System.out.println("TransType" + TransType);
                    /****** ���p�d�ݨD�ק� Dalepeng 20150618 ---END ******/ 
                    session.removeAttribute("CaptureData");
                }

                String OverRefundLimit = hashMerchant.get("OVER_REFUND_LIMIT").toString();
                Hashtable hashCaptureData = CaptureBean.check_Capture_Amt(arrayData, hasSumAmt,
                                    PartialFlag, arrayTerminal, OverRefundLimit, arrayCardTest);
                /****** ���p�d�ݨD�ק� Dalepeng 20150618 ---Start ******/ 
                hashCaptureData.put("TRANSTYPE", TransType); //
                /****** ���p�d�ݨD�ק� Dalepeng 20150618 ---END ******/ 
                if (session.getAttribute("CaptureCheckData") != null)
                {
                    session.removeAttribute("CaptureCheckData");
                }

                session.setAttribute("CaptureCheckData", hashCaptureData);
                LogMemo = "�дڸ�ƤĿ�@"+String.valueOf(arrayData.size())+"��";
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "���\", LogMemo);
                log_user.debug(LogData);
                Forward = "./Merchant_CaptureCheck_List.jsp";
            } //END if (Action.equalsIgnoreCase("Check"))

            if (Action.equalsIgnoreCase("Capture"))
            {
                //����дڧ@�~
                Hashtable hashCarpture = new Hashtable();
                /****** ���p�d�ݨD�ק� Dalepeng 20150618 ---Start ******/ 
                String TransType = "";
                /****** ���p�d�ݨD�ק� Dalepeng 20150618 ---END ******/ 
                if (session.getAttribute("CaptureCheckData") != null)
                {
                    hashCarpture = (Hashtable) session.getAttribute("CaptureCheckData");
                    /****** ���p�d�ݨD�ק� Dalepeng 20150618 ---Start ******/ 
                    TransType = hashCarpture.get("TRANSTYPE").toString();
                    /****** ���p�d�ݨD�ק� Dalepeng 20150618 ---END ******/ 
                    session.removeAttribute("CaptureCheckData");
                }
                //20150911 Jason �令�ǤJ BatchPmtID
                System.out.println("--CALL CAPTURE METHOD START--");
                BatchPmtID = Capture(sysBean, hashCarpture ,hashMerchant);
                System.out.println("--CALL CAPTURE METHOD END--");
                ArrayList arraySuccess = UserBean.get_Batch(sysBean, MerchantID, SubMID, BatchPmtID,
                            "SUCCESS", "BATCHTERMINALID,BATCHSYSORDERID,BATCHTRANSCODE,BATCHTXDATE,BATCHTXTIME"); // ���\���
                ArrayList arrayFail    = UserBean.get_Batch(sysBean, MerchantID, SubMID, BatchPmtID,
                            "FAIL"   , "BATCHTERMINALID,BATCHSYSORDERID,BATCHTRANSCODE,BATCHTXDATE,BATCHTXTIME"); // ���Ѹ��

                hashCarpture = new Hashtable();
                hashCarpture.put("SUCCESS", arraySuccess);
                hashCarpture.put("FAIL", arrayFail);
                hashCarpture.put("BATCHPMTID", BatchPmtID); // �妸�Ǹ�
                /****** ���p�d�ݨD�ק� Dalepeng 20150618 ---Start ******/ 
                hashCarpture.put("TRANSTYPE", TransType);
                /****** ���p�d�ݨD�ק� Dalepeng 20150618 ---END ******/ 
                if (session.getAttribute("CaptureUpdateData") != null)
                {
                    session.removeAttribute("CaptureUpdateData");
                }

                session.setAttribute("CaptureUpdateData", hashCarpture);
                Forward = "./Merchant_CaptureUpdate_List.jsp";
                LogMemo = "�дڸ�Ʀ��\"+String.valueOf(arraySuccess.size())+"���A����"+String.valueOf(arrayFail.size())+"��";
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "���\", LogMemo);
                log_user.debug(LogData);
            } // END if (Action.equalsIgnoreCase("Capture"))

            if (Action.equalsIgnoreCase("Print"))
            {
            	/****** ���p�d�ݨD�ק� Dalepeng 20150618 ---Start ******/ 
            	 Hashtable hashCarpture = new Hashtable();
                 String TransType = "";
                 if (session.getAttribute("CaptureUpdateData") != null)
                 {
                     hashCarpture = (Hashtable) session.getAttribute("CaptureUpdateData");
                     TransType = hashCarpture.get("TRANSTYPE").toString();
                     System.out.println("TransType_Print" + TransType);
                     if (TransType.equalsIgnoreCase("ALL"))
                     {
                    	 TransType = "����";
                     }
                     else if (TransType.equalsIgnoreCase("SSL"))
                     {
                    	 TransType = "�H�Υd";
                     }
                     else if (TransType.equalsIgnoreCase("VMJ"))
                     {
                    	 TransType = "VMJ";
                     }
                     else if (TransType.equalsIgnoreCase("CUP"))
                     {
                    	 TransType = "CUP";
                     }
                     System.out.println("TransType_Print_2" + TransType);
                 } 
                 /****** ���p�d�ݨD�ק� Dalepeng 20150618 ---END ******/ 
            	
            	// �ץX
                ForwardFlag = false;
                //20150911 �ާ@BatchPmtID��Ѱϰ��ܼ� ����String�ŧi �]���w�g�ŧi�L�F
                BatchPmtID = (request.getParameter("BatchPmtID") == null) ? "" : UserBean.trim_Data(request.getParameter("BatchPmtID"));
                System.out.println("BatchPmtID" +BatchPmtID);
                String PrintType = (request.getParameter("PrintType") == null) ? "" : UserBean.trim_Data(request.getParameter("PrintType"));

                if (BatchPmtID.length() > 0 && PrintType.length() > 0)
                {
                    boolean RowdataFlag = true;
                    if (PrintType.equalsIgnoreCase("PDF")) RowdataFlag = false;
                    
                    /** 2023/05/02 �� SQL (By : YC) *//** Test Case : IT-TESTCASE-021 */
                    String sql = UserBean.get_Batch_Result(sysBean, MerchantID, SubMID, BatchPmtID, "",
                                "BATCHTERMINALID,BATCHSYSORDERID,BATCHTRANSCODE,BATCHTXDATE,BATCHTXTIME",RowdataFlag);
                    createReport cr = new createReport();
                    Hashtable field = new Hashtable();
                    field.put("SHOW", "�u�W");
                    /****** ���p�d�ݨD�ק� Dalepeng 20150618 ---Start ******/ 
                    field.put("TRANSTYPESHOW", TransType);
                    /****** ���p�d�ݨD�ק� Dalepeng 20150618 ---END ******/ 
                    
                    String RPTName = "MerchantCaptureUpdateListReport.rpt";
                    if(isSubMid && !PrintType.equals("PDF") ){
                    	 RPTName = "MerchantCaptureUpdateListReportSubMID.rpt";
                    }
                    cr.createPDF(sysBean,SecurityTool.output(sql), request, response, "/Report/" + RPTName, field, PrintType);
                    // UserBean.closeConn();
                    LogMemo = "�H"+PrintType+"�榡�ץX�дڸ��";
                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "���\", LogMemo);
                    log_user.debug(LogData);
                }
            } // END if (Action.equalsIgnoreCase("Print"))
            //Tag:20200708-01
            /***
             ** ���дڡAUI��J��쬰����_�W����P����OTransCode�P�l�S���N���AAction==BATCH
             */
            if (Action.equalsIgnoreCase("BATCH"))
            {
         		hashData.put("Message", "");
            	UserBean util = new UserBean();
            	String sToday = util.get_AppointDay_Date("yyyyMMdd", 0);
            	String StartTransDate = Util.objToStrTrim(request.getParameter("StartTransDate")).replace("/", "");
            	String EndTransDate = Util.objToStrTrim(request.getParameter("EndTransDate")).replace("/", "");
            	String TransCode = Util.objToStrTrim(request.getParameter("TransCode"));
            	if(TransCode.length()==0) TransCode="ALL";
            	//�i�дڤ��
            	int iCaptureDayLimit = Util.objToInt(hashSys.get("MER_CAPTURE_DAY"));
            	hashData.clear();
            	try {
            		arrayData = CaptureBean.get_BILLING_Batch_SumAmt(MerchantID, "ALL"/*SubMID*/, StartTransDate, EndTransDate, TransCode, iCaptureDayLimit);
            		hashData.put("Message", "");
            	}catch(Exception ex) {
            		hashData.put("Message", ex.getMessage());
            	}
            	
            	//=======================================
            	DataBaseBean2 DBBean =new DataBaseBean2();
            	DBBean.ClearSQLParam();
            	StringBuffer sSQLSB = new StringBuffer();
            	//�d�ߦ��S��UI�妸�O�_���b���� FILE_ATTRIBUTE IN CF,SF
            	sSQLSB.append("Select * From FILESTATUS WHERE MERCHANT_ID=? AND FILE_DATE=? AND FILE_ATTRIBUTE IN('CF','SF') ORDER BY FILE_DATE DESC,FILE_BATCHNO DESC");
             	DBBean.AddSQLParam(emDataType.STR, MerchantID); //1
             	DBBean.AddSQLParam(emDataType.STR, sToday); //1
             	ArrayList rsFileStatus = DBBean.QuerySQLByParam(sSQLSB.toString());
             	if(rsFileStatus != null && rsFileStatus.size() > 0) {
             		hashData.put("Message", "�妸�дڰ��椤�A�еy��.....");
             		hashData.put("SUBMITBATCH_DISABLE", "Y"); //UI�e������
             	}else {
             		hashData.put("Message", "");
             		hashData.put("SUBMITBATCH_DISABLE", "N");//UI�e������
             	}
            	//1�P�������妸�дڬ���
            	String sStartDate = util.get_AppointDay_Date("yyyyMMdd", -7);
            	String sEndDate = util.get_AppointDay_Date("yyyyMMdd", 0);
         		rsFileStatus = CaptureBean.get_FILESTATUS_Log(MerchantID,sStartDate,sEndDate);
            	//=======================================
            	//�N��Ʃ�Jsession��
            	Hashtable hashQuery = new Hashtable();
            	hashQuery.put("StartTransDate", StartTransDate);
            	hashQuery.put("EndTransDate", EndTransDate);
            	hashQuery.put("TransCode", TransCode);
            	hashQuery.put("BatchContent", "LIST");//�妸�дڥ�LIST:��ܸ��;RESULT:��ܵ��G
            	hashData.put("QUERY", hashQuery); // �d�߽дڸ��
            	hashData.put("DATALIST", arrayData); // �дڸ��
            	hashData.put("FILESTATUS", rsFileStatus); // 1�P�������妸�дڬ���
            	if (session.getAttribute("CaptureBatchData") != null) session.removeAttribute("CaptureBatchData");
            	session.setAttribute("CaptureBatchData", hashData);
                LogMemo = "UI�妸�дڬd�ߦ@"+String.valueOf(arrayData.size())+"��";
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "���\", LogMemo);
                log_user.debug(LogData);
                Forward = "./Merchant_Capture_List_Batch.jsp";
            }
            /***
             * * ���дڶ}�l����AUI��J��쬰����_�W����P����OTransCode�P�l�S���N���AAction==BATCHCAPTURE
             */
            if (Action.equalsIgnoreCase("BATCHCAPTURE"))
            {
            	String StartTransDate = Util.objToStrTrim(request.getParameter("StartTransDate")).replace("/", "");
            	String EndTransDate = Util.objToStrTrim(request.getParameter("EndTransDate")).replace("/", "");
            	String TransCode = Util.objToStrTrim(request.getParameter("TransCode"));
            	if(TransCode.length()==0) TransCode="ALL";
            	//�i�дڤ��
            	int iCaptureDayLimit = Util.objToInt(hashSys.get("MER_CAPTURE_DAY"));

            	UserBean util = new UserBean();
            	//=======================================
            	DataBaseBean2 DBBean =new DataBaseBean2(); DBBean.ClearSQLParam();
            	StringBuffer sSQLSB = new StringBuffer();
            	//�d�ߦ��S��UI�妸�O�_���b���� FILE_ATTRIBUTE IN CF,SF
            	sSQLSB.append("Select * From FILESTATUS WHERE MERCHANT_ID=? AND FILE_ATTRIBUTE IN('CF','SF') ORDER BY FILE_DATE DESC,FILE_BATCHNO DESC");
             	DBBean.AddSQLParam(emDataType.STR, MerchantID); //1
             	ArrayList rsFileStatus = DBBean.QuerySQLByParam(sSQLSB.toString());
             	if(rsFileStatus != null && rsFileStatus.size() > 0) {
             		hashData.put("Message", "�妸�дڰ��椤�A�еy��.....");
             		hashData.put("SUBMITBATCH_DISABLE", "Y");
                	//1�P�������妸�дڬ���
                	String sStartDate = util.get_AppointDay_Date("yyyyMMdd", -7);
                	String sEndDate = util.get_AppointDay_Date("yyyyMMdd", 0);
             		rsFileStatus = CaptureBean.get_FILESTATUS_Log(MerchantID,sStartDate,sEndDate);
                 	//****��Ƹm���Session���Aforward��UI
             		Hashtable hashQuery = new Hashtable();
                	hashQuery.put("BatchContent", "LIST");//�妸�дڥ�LIST:��ܸ��;RESULT:��ܵ��G
                	//�N��Ʃ�Jsession��
                	hashData.put("DATALIST", arrayData); // �дڸ��
                	hashData.put("QUERY", hashQuery); // �d�߽дڸ��
                	hashData.put("FILESTATUS", rsFileStatus); // �d�ߧ妸�дڰ��檬�A���
                	if (session.getAttribute("CaptureBatchData") != null) session.removeAttribute("CaptureBatchData");
                	session.setAttribute("CaptureBatchData", hashData);
                    LogMemo = "UI�妸�дڰ��椤";
                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "���\", LogMemo);
                    log_user.debug(LogData);
                    Forward = "./Merchant_Capture_List_Batch.jsp";
                    request.getRequestDispatcher(Forward).forward(request, response);
                    return;
             	}
             	ArrayList rs;
            	//=======================================
             	// 1. ���o���帹
             	String sToday = util.get_AppointDay_Date("yyyyMMdd", 0);
             	DBBean =new DataBaseBean2();DBBean.ClearSQLParam();
             	sSQLSB = new StringBuffer("SELECT LPAD(TO_NUMBER(NVL(MAX(FILE_BATCHNO),-1))+1,4,'0') AS FILE_BATCHNO FROM FILESTATUS WHERE  MERCHANT_ID=? AND FILE_DATE=? ");
             	DBBean.AddSQLParam(emDataType.STR, MerchantID); //1
             	DBBean.AddSQLParam(emDataType.STR, sToday); //1
             	rs = DBBean.QuerySQLByParam(sSQLSB.toString());
             	String sBatchNo = (rs != null) ?Util.objToStrTrim(((Hashtable)rs.get(0)).get("FILE_BATCHNO")):"0000";  
             	// 2.Create FILESTATUS
             	String sFILE_NAME = "UI"+MerchantID+"_"+sToday+"_"+sBatchNo;
             	DBBean =new DataBaseBean2();DBBean.ClearSQLParam();
            	sSQLSB = new StringBuffer();
            	sSQLSB.append("INSERT INTO FileStatus(FILE_TAG,FILE_BATCHNO,MERCHANT_ID,FILE_DATE,FILE_NAME,FILE_ATTRIBUTE,PROCESS_DATE,FILESTATUS_REFKEY)");
            	sSQLSB.append("VALUES('UC',?,?,?,?,'CF',SYSTIMESTAMP,to_char(SYSTIMESTAMP,'YYYYMMDDHH24MISSFF'))");
             	DBBean.AddSQLParam(emDataType.STR, sBatchNo); //FILE_BATCHNO
             	DBBean.AddSQLParam(emDataType.STR, MerchantID); //MERCHANT_ID
             	DBBean.AddSQLParam(emDataType.STR, sToday); //FILE_DATE
             	DBBean.AddSQLParam(emDataType.STR, sFILE_NAME); //FILE_NAME
             	if(DBBean.executeSQL(sSQLSB.toString()) == false) {
             		hashData.put("Message", "���ͧ妸�дڸ��(FILESTATUS)���~.....");
             		hashData.put("SUBMITBATCH_DISABLE", "Y");
                	//1�P�������妸�дڬ���
                	String sStartDate = util.get_AppointDay_Date("yyyyMMdd", -7);
                	String sEndDate = util.get_AppointDay_Date("yyyyMMdd", 0);
             		rsFileStatus = CaptureBean.get_FILESTATUS_Log(MerchantID,sStartDate,sEndDate);
                 	//****��Ƹm���Session���Aforward��UI
             		Hashtable hashQuery = new Hashtable();
                	hashQuery.put("BatchContent", "LIST");//�妸�дڥ�LIST:��ܸ��;RESULT:��ܵ��G
                	//�N��Ʃ�Jsession��
                	hashData.put("DATALIST", arrayData); // �дڸ��
                	hashData.put("QUERY", hashQuery); // �d�߽дڸ��
                	hashData.put("FILESTATUS", rsFileStatus); // �d�ߧ妸�дڰ��檬�A���
                	if (session.getAttribute("CaptureBatchData") != null) session.removeAttribute("CaptureBatchData");
                	session.setAttribute("CaptureBatchData", hashData);
                    LogMemo = "UI�妸�д�Inser int FILESTATUS Fail";
                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "����", LogMemo);
                    log_user.debug(LogData);
                    Forward = "./Merchant_Capture_List_Batch.jsp";
                    request.getRequestDispatcher(Forward).forward(request, response);
                    return;
             	}
             	//2.1 GET FileStatus_RowID
             	DBBean =new DataBaseBean2();DBBean.ClearSQLParam();
            	sSQLSB = new StringBuffer();
            	sSQLSB.append("SELECT ROWIDTOCHAR(ROWID) ROWIDCHAR ,FILE_NAME FROM FILESTATUS WHERE");
            	sSQLSB.append(" FILE_NAME=? AND FILE_ATTRIBUTE='CF'");
            	sSQLSB.append(" AND MERCHANT_ID=? AND FILE_DATE=? AND ROWNUM=1");
             	DBBean.AddSQLParam(emDataType.STR, sFILE_NAME); //FILE_NAME
             	DBBean.AddSQLParam(emDataType.STR, MerchantID); //MERCHANT_ID
             	DBBean.AddSQLParam(emDataType.STR, sToday); //FILE_DATE
             	rs = DBBean.QuerySQLByParam(sSQLSB.toString());
             	String sFILESTATUS_REFKEY = "";
             	if(rs != null) {
             		sFILESTATUS_REFKEY =Util.objToStrTrim(((Hashtable)rs.get(0)).get("ROWIDCHAR"));
             	}
             	
             	if(sFILESTATUS_REFKEY.length()==0) {
             		hashData.put("Message", "��s FILESTATUS.FILESTATUS_REFKEY����.....");
             		hashData.put("SUBMITBATCH_DISABLE", "Y");
                	//1�P�������妸�дڬ���
                	String sStartDate = util.get_AppointDay_Date("yyyyMMdd", -7);
                	String sEndDate = util.get_AppointDay_Date("yyyyMMdd", 0);
             		rsFileStatus = CaptureBean.get_FILESTATUS_Log(MerchantID,sStartDate,sEndDate);
                 	//****��Ƹm���Session���Aforward��UI
             		Hashtable hashQuery = new Hashtable();
                	hashQuery.put("BatchContent", "LIST");//�妸�дڥ�LIST:��ܸ��;RESULT:��ܵ��G
                	//�N��Ʃ�Jsession��
                	hashData.put("DATALIST", arrayData); // �дڸ��
                	hashData.put("QUERY", hashQuery); // �d�߽дڸ��
                	hashData.put("FILESTATUS", rsFileStatus); // �d�ߧ妸�дڰ��檬�A���
                	if (session.getAttribute("CaptureBatchData") != null) session.removeAttribute("CaptureBatchData");
                	session.setAttribute("CaptureBatchData", hashData);
                    LogMemo = "UI�妸�д�,��s FILESTATUS.FILESTATUS_REFKEY����";
                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "����", LogMemo);
                    log_user.debug(LogData);
                    Forward = "./Merchant_Capture_List_Batch.jsp";
                    request.getRequestDispatcher(Forward).forward(request, response);
                    return;
             	}
             	
            	//3.UPDATE FILESTATUS
             	DBBean =new DataBaseBean2();DBBean.ClearSQLParam();
            	sSQLSB = new StringBuffer();
            	sSQLSB.append("UPDATE FILESTATUS SET FILESTATUS_REFKEY=?");
            	sSQLSB.append(" WHERE FILE_NAME=? AND FILE_ATTRIBUTE='CF' AND MERCHANT_ID=? AND FILE_DATE=?");
            	DBBean.AddSQLParam(emDataType.STR, sFILESTATUS_REFKEY); //FILESTATUS_REFKEY
             	DBBean.AddSQLParam(emDataType.STR, sFILE_NAME); //FILE_NAME
             	DBBean.AddSQLParam(emDataType.STR, MerchantID); //MERCHANT_ID
             	DBBean.AddSQLParam(emDataType.STR, sToday); //FILE_DATE
             	if(DBBean.executeSQL(sSQLSB.toString()) == false) {
             		hashData.put("Message", "��s FILESTATUS.FILESTATUS_REFKEY����.....");
             		hashData.put("SUBMITBATCH_DISABLE", "Y");
                	//1�P�������妸�дڬ���
                	String sStartDate = util.get_AppointDay_Date("yyyyMMdd", -7);
                	String sEndDate = util.get_AppointDay_Date("yyyyMMdd", 0);
             		rsFileStatus = CaptureBean.get_FILESTATUS_Log(MerchantID,sStartDate,sEndDate);
                 	//****��Ƹm���Session���Aforward��UI
             		Hashtable hashQuery = new Hashtable();
                	hashQuery.put("BatchContent", "LIST");//�妸�дڥ�LIST:��ܸ��;RESULT:��ܵ��G
                	//�N��Ʃ�Jsession��
                	hashData.put("DATALIST", arrayData); // �дڸ��
                	hashData.put("QUERY", hashQuery); // �d�߽дڸ��
                	hashData.put("FILESTATUS", rsFileStatus); // �d�ߧ妸�дڰ��檬�A���
                	if (session.getAttribute("CaptureBatchData") != null) session.removeAttribute("CaptureBatchData");
                	session.setAttribute("CaptureBatchData", hashData);
                    LogMemo = "UI�妸�д�,��s FILESTATUS.FILESTATUS_REFKEY����";
                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "����", LogMemo);
                    log_user.debug(LogData);
                    Forward = "./Merchant_Capture_List_Batch.jsp";
                    request.getRequestDispatcher(Forward).forward(request, response);
                    return;
             	}
             	
             	//4.UPDATE BILLING TABLE �妸�e��Ʒǳ�
             	DBBean =new DataBaseBean2();DBBean.ClearSQLParam();
            	sSQLSB = new StringBuffer();
            	sSQLSB.append("UPDATE BILLING B SET CAPTUREBATCHNO = ?,/*UIBATCHNO*/");
            	sSQLSB.append("DUE_DATE=TO_CHAR(TO_DATE(TRANSDATE,'YYYYMMDD')+?,'YYYYMMDD'),CAPTUREDATE = ?");
            	sSQLSB.append(" WHERE MERCHANTID = ? AND (TRANSDATE BETWEEN ? AND ?)");
            	sSQLSB.append(" AND BALANCEAMT > 0 AND REVERSALFLAG = '0' AND TRIM(PAN) <> '62XX'");
            	sSQLSB.append(" AND TO_CHAR(SYSDATE,'yyyyMMdd') <=TO_CHAR(TO_DATE( TRANSDATE,'YYYYMMDD') + ?, 'YYYYMMDD')");
            	sSQLSB.append(" AND (SELECT COUNT(1) FROM TEST_CARD WHERE (PAN BETWEEN PAN_BEGIN AND PAN_END) AND FLAG_CAPTURE = 'N' AND FLAG_ACTIVATE = 'Y') = 0");
            	sSQLSB.append(" AND (SELECT COUNT(1) FROM TERMINAL T WHERE T.MERCHANTID	= B.MERCHANTID AND T.TERMINALID =B.TERMINALID AND CURRENTCODE IN ('B','D','E','F') AND PERMIT_CAPTURE='Y') > 0");
             	DBBean.AddSQLParam(emDataType.STR, sBatchNo); //FILE_BATCHNO
             	DBBean.AddSQLParam(emDataType.INT, iCaptureDayLimit); //�i�дڤ��
             	DBBean.AddSQLParam(emDataType.STR, sToday); //CAPTUREDATE
             	DBBean.AddSQLParam(emDataType.STR, MerchantID); //MERCHANT_ID
             	DBBean.AddSQLParam(emDataType.STR, StartTransDate); //
             	DBBean.AddSQLParam(emDataType.STR, EndTransDate); //
             	DBBean.AddSQLParam(emDataType.INT, iCaptureDayLimit); //�i�дڤ��
             	if(DBBean.executeSQL(sSQLSB.toString()) == false) {
             		hashData.put("Message", "��s BILLING TABLE �妸�e��Ʒǳ�..����.....");
             		hashData.put("SUBMITBATCH_DISABLE", "Y");
                	//1�P�������妸�дڬ���
                	String sStartDate = util.get_AppointDay_Date("yyyyMMdd", -7);
                	String sEndDate = util.get_AppointDay_Date("yyyyMMdd", 0);
             		rsFileStatus = CaptureBean.get_FILESTATUS_Log(MerchantID,sStartDate,sEndDate);
                 	//****��Ƹm���Session���Aforward��UI
             		Hashtable hashQuery = new Hashtable();
                	hashQuery.put("BatchContent", "LIST");//�妸�дڥ�LIST:��ܸ��;RESULT:��ܵ��G
                	//�N��Ʃ�Jsession��
                	hashData.put("DATALIST", arrayData); // �дڸ��
                	hashData.put("QUERY", hashQuery); // �d�߽дڸ��
                	hashData.put("FILESTATUS", rsFileStatus); // �d�ߧ妸�дڰ��檬�A���
                	if (session.getAttribute("CaptureBatchData") != null) session.removeAttribute("CaptureBatchData");
                	session.setAttribute("CaptureBatchData", hashData);
                    LogMemo = "UI�妸�д�,BILLING TABLE �妸�e��Ʒǳ�..����";
                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "����", LogMemo);
                    log_user.debug(LogData);
                    Forward = "./Merchant_Capture_List_Batch.jsp";
                    request.getRequestDispatcher(Forward).forward(request, response);
                    return;
             	}
             	// UPDATE FILESTATUS FILE_TOTAL_COUNT....FILE_REFUND_AMOUNT
             	DBBean =new DataBaseBean2();DBBean.ClearSQLParam();
            	sSQLSB = new StringBuffer();
            	sSQLSB.append("SELECT COUNT(1) AS CNT,SUM(BALANCEAMT) SUMAMT,");
            	sSQLSB.append(" SUM(DECODE(TRANSCODE,'00',1,0)) AS CNT00,SUM(DECODE(TRANSCODE,'00',BALANCEAMT,0)) AS SUM00,");
            	sSQLSB.append(" SUM(DECODE(TRANSCODE,'01',1,0)) AS CNT01,SUM(DECODE(TRANSCODE,'01',BALANCEAMT,0)) AS SUM01");
            	sSQLSB.append(" FROM BILLING WHERE MERCHANTID=? AND CAPTUREDATE=? AND CAPTUREBATCHNO =? ");
             	DBBean.AddSQLParam(emDataType.STR, MerchantID); //MERCHANT_ID
             	DBBean.AddSQLParam(emDataType.STR, sToday); //FILE_DATE
             	DBBean.AddSQLParam(emDataType.STR, sBatchNo); //FILE_BATCHNO
             	rs = DBBean.QuerySQLByParam(sSQLSB.toString());
             	if(rs == null) {
             		hashData.put("Message", "��s FILESTATUS FILE_TOTAL_COUNT��Ʒǳ�..����.....");
             		hashData.put("SUBMITBATCH_DISABLE", "Y");
                	//1�P�������妸�дڬ���
                	String sStartDate = util.get_AppointDay_Date("yyyyMMdd", -7);
                	String sEndDate = util.get_AppointDay_Date("yyyyMMdd", 0);
             		rsFileStatus = CaptureBean.get_FILESTATUS_Log(MerchantID,sStartDate,sEndDate);
                 	//****��Ƹm���Session���Aforward��UI
             		Hashtable hashQuery = new Hashtable();
                	hashQuery.put("BatchContent", "LIST");//�妸�дڥ�LIST:��ܸ��;RESULT:��ܵ��G
                	//�N��Ʃ�Jsession��
                	hashData.put("DATALIST", arrayData); // �дڸ��
                	hashData.put("QUERY", hashQuery); // �d�߽дڸ��
                	hashData.put("FILESTATUS", rsFileStatus); // �d�ߧ妸�дڰ��檬�A���
                	if (session.getAttribute("CaptureBatchData") != null) session.removeAttribute("CaptureBatchData");
                	session.setAttribute("CaptureBatchData", hashData);
                    LogMemo = "UI�妸�д�,��s FILESTATUS FILE_TOTAL_COUNT��Ʒǳ�..����";
                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "����", LogMemo);
                    log_user.debug(LogData);
                    Forward = "./Merchant_Capture_List_Batch.jsp";
                    request.getRequestDispatcher(Forward).forward(request, response);
                    return;
             	}
             	int iFILE_TOTAL_COUNT =Util.objToInt(((Hashtable)rs.get(0)).get("CNT"));
             	double dFILE_TOTAL_AMOUNT =Util.objToDouble(((Hashtable)rs.get(0)).get("SUMAMT"));
             	int iFILE_PURCHASE_COUNT =Util.objToInt(((Hashtable)rs.get(0)).get("CNT00"));
             	double dFILE_PURCHASE_AMOUNT =Util.objToDouble(((Hashtable)rs.get(0)).get("SUM00"));
             	int iFILE_REFUND_COUNT =Util.objToInt(((Hashtable)rs.get(0)).get("CNT01"));
             	double dFILE_REFUND_AMOUNT =Util.objToDouble(((Hashtable)rs.get(0)).get("SUM01"));
            	//�}�l�B�z�妸�д�
             	// 1.UPDATE FILESTATUS �� SF /*�B�z��*/
             	DBBean =new DataBaseBean2();DBBean.ClearSQLParam();
            	sSQLSB = new StringBuffer();
            	sSQLSB.append("UPDATE FILESTATUS SET FILE_ATTRIBUTE='SF',");
            	sSQLSB.append(" FILE_TOTAL_COUNT=?,FILE_TOTAL_AMOUNT=?,");
            	sSQLSB.append(" FILE_PURCHASE_COUNT=?,FILE_PURCHASE_AMOUNT=?,");
            	sSQLSB.append(" FILE_REFUND_COUNT=?,FILE_REFUND_AMOUNT=?,");
            	sSQLSB.append(" PROCESS_DATE =SYSDATE ");
            	sSQLSB.append(" WHERE FILE_NAME=? AND FILE_ATTRIBUTE='CF'");
            	sSQLSB.append(" AND MERCHANT_ID=? AND FILE_DATE=?");
            	DBBean.AddSQLParam(emDataType.INT, iFILE_TOTAL_COUNT); //
            	DBBean.AddSQLParam(emDataType.DOUBLE, dFILE_TOTAL_AMOUNT); //
            	DBBean.AddSQLParam(emDataType.INT, iFILE_PURCHASE_COUNT); //
            	DBBean.AddSQLParam(emDataType.DOUBLE, dFILE_PURCHASE_AMOUNT); //
            	DBBean.AddSQLParam(emDataType.INT, iFILE_REFUND_COUNT); //
            	DBBean.AddSQLParam(emDataType.DOUBLE, dFILE_REFUND_AMOUNT); //
             	DBBean.AddSQLParam(emDataType.STR, sFILE_NAME); //FILE_NAME
             	DBBean.AddSQLParam(emDataType.STR, MerchantID); //MERCHANT_ID
             	DBBean.AddSQLParam(emDataType.STR, sToday); //FILE_DATE
             	if(DBBean.executeSQL(sSQLSB.toString()) == false) {
             		hashData.put("Message", "��s FILESTATUS��SF�妸�B�z��..����.....");
             		hashData.put("SUBMITBATCH_DISABLE", "Y");
                	//1�P�������妸�дڬ���
                	String sStartDate = util.get_AppointDay_Date("yyyyMMdd", -7);
                	String sEndDate = util.get_AppointDay_Date("yyyyMMdd", 0);
             		rsFileStatus = CaptureBean.get_FILESTATUS_Log(MerchantID,sStartDate,sEndDate);
                 	//****��Ƹm���Session���Aforward��UI
             		Hashtable hashQuery = new Hashtable();
                	hashQuery.put("BatchContent", "LIST");//�妸�дڥ�LIST:��ܸ��;RESULT:��ܵ��G
                	//�N��Ʃ�Jsession��
                	hashData.put("DATALIST", arrayData); // �дڸ��
                	hashData.put("QUERY", hashQuery); // �d�߽дڸ��
                	hashData.put("FILESTATUS", rsFileStatus); // �d�ߧ妸�дڰ��檬�A���
                	if (session.getAttribute("CaptureBatchData") != null) session.removeAttribute("CaptureBatchData");
                	session.setAttribute("CaptureBatchData", hashData);
                    LogMemo = "UI�妸�д�,��s FILESTATUS��SF�妸�B�z��..����";
                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "����", LogMemo);
                    log_user.debug(LogData);
                    Forward = "./Merchant_Capture_List_Batch.jsp";
                    request.getRequestDispatcher(Forward).forward(request, response);
                    return;
                }
             	// INSERT INTO BATCH_CAPTURE FROM BILLING WHERE MERCHANTID AND CAPTUREBATCHNO =(FILE_BATCHNO)
             	//20220210 ADD AUTH_SRC_CODE
             	DBBean =new DataBaseBean2();DBBean.ClearSQLParam();
            	sSQLSB = new StringBuffer();
            	sSQLSB.append("INSERT INTO Batch_CAPTURE (MERCHANTID,SUBMID,TERMINALID,ACQUIRERID,ORDERID,SYS_ORDERID,CARD_TYPE,PAN,EXTENNO,EXPIREDATE,TRANSCODE,REVERSALFLAG,TRANSDATE,TRANSTIME,CURRENCYCODE,TRANSAMT,APPROVECODE,RESPONSECODE,RESPONSEMSG,BATCHNO,USERDEFINE,EMAIL,MTI,RRN,SOCIALID,ENTRY_MODE,CONDITION_CODE,TRANSMODE,TRANSTYPE,ECI,CAVV,XID,INSTALLTYPE,INSTALL,FIRSTAMT,EACHAMT,FEE,REDEMTYPE,REDEMUSED,REDEMBALANCE,CREDITAMT,BILLMESSAGE,BALANCEAMT,CAPTUREAMT,CAPTUREDATE,FEEDBACKCODE,FEEDBACKDATE,SYSTRACENO,FileStatus_RowID,FILE_DATE,DUE_DATE,REAUTH_FLAG,MOBILEPAYFLAG,MOBILEPAYTX,MOBILEPAYINFO,MOBILEPAYCRYPTO,MOBILEPAYWALLET,MOBILEPAYECI,MPI_VERSION,DS_TRANSID,MERCHANTPAN,QR_FLAG,AUTH_SRC_CODE)");
             	sSQLSB.append(" SELECT MERCHANTID,SUBMID,TERMINALID,ACQUIRERID,ORDERID,SYS_ORDERID,CARD_TYPE,PAN,EXTENNO,EXPIREDATE,TRANSCODE,REVERSALFLAG,TRANSDATE,TRANSTIME,CURRENCYCODE,TRANSAMT,APPROVECODE,RESPONSECODE,RESPONSEMSG,BATCHNO,USERDEFINE,EMAIL,MTI,RRN,SOCIALID,ENTRY_MODE,CONDITION_CODE,TRANSMODE,TRANSTYPE,ECI,CAVV,XID,INSTALLTYPE,INSTALL,FIRSTAMT,EACHAMT,FEE,REDEMTYPE,REDEMUSED,REDEMBALANCE,CREDITAMT,BILLMESSAGE,");
             	sSQLSB.append(" BALANCEAMT /*BALANCEAMT*/,BALANCEAMT /*CAPTUREAMT*/,");
             	sSQLSB.append(" SYSDATE/* CAPTUREDATE*/,'000' /*FEEDBACKCODE*/,SYSDATE /*FEEDBACKDATE*/,SYSTRACENO,");
             	sSQLSB.append(" ?/*FileStatus_RowID*/,?/*FILE_DATE*/,");
             	sSQLSB.append(" TO_DATE(DUE_DATE,'yyyyMMdd'),");
             	sSQLSB.append(" REAUTH_FLAG,MOBILEPAYFLAG,MOBILEPAYTX,MOBILEPAYINFO,MOBILEPAYCRYPTO,MOBILEPAYWALLET,MOBILEPAYECI,MPI_VERSION,DS_TRANSID,MERCHANTPAN,QR_FLAG,AUTH_SRC_CODE");
             	sSQLSB.append(" FROM BILLING ");
             	//------------------------
             	sSQLSB.append(" WHERE MERCHANTID= ? AND CAPTUREDATE = ?/*FILE_DATE*/ AND  CAPTUREBATCHNO = ?/*UIBATCHNO*/");
             	sSQLSB.append("  AND BALANCEAMT > 0");
             	
             	DBBean.AddSQLParam(emDataType.STR, sFILESTATUS_REFKEY); //FileStatus_RowID
             	DBBean.AddSQLParam(emDataType.STR, sToday); //FILE_DATE
             	//------------------------
             	DBBean.AddSQLParam(emDataType.STR, MerchantID); //MERCHANT_ID
             	DBBean.AddSQLParam(emDataType.STR, sToday); //FILE_DATE
             	DBBean.AddSQLParam(emDataType.STR, sBatchNo); //FILE_BATCHNO
             	if(DBBean.executeSQL(sSQLSB.toString()) == false) {
             		hashData.put("Message", "INSERT INTO Batch_CAPTURE..����.....");
             		hashData.put("SUBMITBATCH_DISABLE", "Y");
                	//1�P�������妸�дڬ���
                	String sStartDate = util.get_AppointDay_Date("yyyyMMdd", -7);
                	String sEndDate = util.get_AppointDay_Date("yyyyMMdd", 0);
             		rsFileStatus = CaptureBean.get_FILESTATUS_Log(MerchantID,sStartDate,sEndDate);
                 	//****��Ƹm���Session���Aforward��UI
             		Hashtable hashQuery = new Hashtable();
                	hashQuery.put("BatchContent", "LIST");//�妸�дڥ�LIST:��ܸ��;RESULT:��ܵ��G
                	//�N��Ʃ�Jsession��
                	hashData.put("DATALIST", arrayData); // �дڸ��
                	hashData.put("QUERY", hashQuery); // �d�߽дڸ��
                	hashData.put("FILESTATUS", rsFileStatus); // �d�ߧ妸�дڰ��檬�A���
                	if (session.getAttribute("CaptureBatchData") != null) session.removeAttribute("CaptureBatchData");
                	session.setAttribute("CaptureBatchData", hashData);
                    LogMemo = "INSERT INTO Batch_CAPTURE..����";
                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "����", LogMemo);
                    log_user.debug(LogData);
                    Forward = "./Merchant_Capture_List_Batch.jsp";
                    request.getRequestDispatcher(Forward).forward(request, response);
                    return;
             	}
             	//
            	/***************
            	 ** CALL Store PROCEDURE �}�l������дڧ@�~
            	 * 1.UPDATE BILLING
            	 * 2.UPDATE BALANCE
            	 * 3.INSERT CAPTURE
            	 * * */
             	Connection conn=null;
             	DBBean =new DataBaseBean2();DBBean.ClearSQLParam();
            	sSQLSB = new StringBuffer();
            	sSQLSB.append("{call SP_BATCH_CAPTURE (?, ?, ?)}");/*FILE_NAME,MERCHANTID,FILE_BATCHNO*/
            	CallableStatement stmt = null;
           	    try{
           	    	conn = DBBean.getConn();
           	    	System.out.println("Creating statement...");
           	    	stmt = conn.prepareCall(sSQLSB.toString());
           	    	stmt.setString(1, sFILE_NAME);
           	    	stmt.setString(2, MerchantID);
           	    	stmt.setString(3, sBatchNo);
           	        // Because parameter is OUT so register it
           	        //stmt.registerOutParameter(2, java.sql.Types.VARCHAR);
           	        //Use execute method to run stored procedure.
           	        System.out.println("Executing stored procedure..." );
           	        stmt.execute();
                    //Retrieve employee name with getXXX method
           	        //String empName = stmt.getString(2);
           	        stmt.close();
           	        conn.close();
           	    }catch(SQLException se) {
           	        //Handle errors for JDBC
           	        se.printStackTrace();
           	    }catch(Exception ex) {
           	        //Handle errors for Class.forName
           	        ex.printStackTrace();
           	    }finally{
           	      //finally block used to close resources
           	      try{
           	         if(stmt!=null)
           	            stmt.close();
           	      }catch(SQLException se2){
           	      }// nothing we can do
           	      try{
           	         if(conn!=null)
           	            conn.close();
           	      }catch(SQLException se){
           	         se.printStackTrace();
           	      }//end finally try
           	   }//end try
            	hashData.clear();
            	
            	//���o���浲�GFrom FILESTATUS TABLE
             	DBBean =new DataBaseBean2();DBBean.ClearSQLParam();
            	sSQLSB = new StringBuffer();
            	sSQLSB.append("SELECT * FROM FILESTATUS WHERE");
            	sSQLSB.append(" FILE_NAME=? AND FILE_ATTRIBUTE='RF'");
            	sSQLSB.append(" AND MERCHANT_ID=? AND FILE_DATE=? AND FILE_BATCHNO=? AND ROWNUM=1");
             	DBBean.AddSQLParam(emDataType.STR, sFILE_NAME); //FILE_NAME
             	DBBean.AddSQLParam(emDataType.STR, MerchantID); //MERCHANT_ID
             	DBBean.AddSQLParam(emDataType.STR, sToday); //FILE_DATE
             	DBBean.AddSQLParam(emDataType.STR, sBatchNo); //FILE_BATCHNO
             	rs = DBBean.QuerySQLByParam(sSQLSB.toString());
            	int iCountOK00=0,iCountOK01=0,iCountFail00=0,iCountFail01=0;
            	double dSumOK00=0,dSumOK01=0,dSumFail00=0,dSumFail01=0;
             	if(rs != null) {
                 	Hashtable dr = (Hashtable)rs.get(0);
                	iCountOK00 = Util.objToInt(dr.get("PURCHASE_SUCCESS_COUNT"));
                	dSumOK00 = Util.objToDouble(dr.get("PURCHASE_SUCCESS_AMOUNT"));
                	iCountOK01 = Util.objToInt(dr.get("REFUND_SUCCESS_COUNT"));
                	dSumOK01 = Util.objToDouble(dr.get("REFUND_SUCCESS_AMOUNT"));
                	iCountFail00 = Util.objToInt(dr.get("PURCHASE_REJECT_COUNT"));
                	dSumFail00 = Util.objToDouble(dr.get("PURCHASE_REJECT_AMOUNT"));
                	iCountFail01 = Util.objToInt(dr.get("REFUND_REJECT_COUNT"));
                	dSumFail01 = Util.objToDouble(dr.get("REFUND_REJECT_AMOUNT"));
                	hashData.put("Message", "");
             	}
             	else {
             		hashData.put("Message", "���o���浲�G���~�A�䤣���������AFILE_NAME:"+sFILE_NAME+",BatchNo:"+sBatchNo);
             	}
            	
            	ArrayList<Hashtable<String,String>> show_content = new ArrayList<Hashtable<String,String>>();
            	Hashtable<String,String> content = new Hashtable<String,String> ();
            	content.put("TRANSCODE","00");
            	content.put("CNT",iCountOK00 +"");
            	content.put("TOTAL_AMT",dSumOK00 +"");
            	show_content.add(content);
            	content = new Hashtable<String,String> ();
            	content.put("TRANSCODE","01");
            	content.put("CNT",iCountOK01 +"");
            	content.put("TOTAL_AMT",dSumOK01 +"");
            	show_content.add(content);
            	content = new Hashtable<String,String> ();
            	content.put("TRANSCODE","00FAIL");
            	content.put("CNT",iCountFail00 +"");
            	content.put("TOTAL_AMT",dSumFail00 +"");
            	show_content.add(content);
            	content = new Hashtable<String,String> ();
            	content.put("TRANSCODE","01FAIL");
            	content.put("CNT",iCountFail01 +"");
            	content.put("TOTAL_AMT",dSumFail01 +"");
            	show_content.add(content);

            	//1�P�������妸�дڬ���
            	String sStartDate = util.get_AppointDay_Date("yyyyMMdd", -7);
            	String sEndDate = util.get_AppointDay_Date("yyyyMMdd", 0);
         		rsFileStatus = CaptureBean.get_FILESTATUS_Log(MerchantID,sStartDate,sEndDate);
             	//���p�����~�A�h�N���~�����`�T����ܩ�e���W
             	ArrayList rsBatchFailGroup = new ArrayList();
             	if(iCountFail00 > 0 || iCountFail01 > 0) {
                	DBBean =new DataBaseBean2(); DBBean.ClearSQLParam();
                	sSQLSB = new StringBuffer();
                	sSQLSB.append("SELECT TRANSDATE,FEEDBACKMSG,COUNT(1) AS CNT,BALANCEAMT FROM BATCH_CAPTURE ");
                	sSQLSB.append(" WHERE FILESTATUS_ROWID=? AND MERCHANTID=? AND FEEDBACKCODE<>'000' ");
                	sSQLSB.append(" GROUP BY TRANSDATE,BALANCEAMT,FEEDBACKCODE,FEEDBACKMSG");
                 	DBBean.AddSQLParam(emDataType.STR, sFILESTATUS_REFKEY); //1
                 	DBBean.AddSQLParam(emDataType.STR, MerchantID); //2
                 	rsBatchFailGroup = DBBean.QuerySQLByParam(sSQLSB.toString());
                 	if(rsBatchFailGroup == null) rsBatchFailGroup = new ArrayList();
             	}

            	Hashtable hashQuery = new Hashtable();
         		hashData.put("SUBMITBATCH_DISABLE", "Y");
            	hashQuery.put("BatchContent", "RESULT");//�妸�дڥ�LIST:��ܸ��;RESULT:��ܵ��G
            	hashData.put("FILESTATUS", rsFileStatus); // �d�߽дڸ��
            	hashData.put("BatchFailGroup", rsBatchFailGroup); // ���p�����~�A�h�N���~�����`�T����ܩ�e���W
            	//�N��Ʃ�Jsession��
            	hashData.put("DATALIST", show_content); // �дڸ��
            	hashData.put("QUERY", hashQuery); // �d�߽дڸ��
            	if (session.getAttribute("CaptureBatchData") != null) session.removeAttribute("CaptureBatchData");
            	session.setAttribute("CaptureBatchData", hashData);
                LogMemo = "UI�妸�дڬd�ߦ@"+String.valueOf(arrayData.size())+"��";
            	arrayData.clear();arrayData=null;
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "���\", LogMemo);
                log_user.debug(LogData);
                Forward = "./Merchant_Capture_List_Batch.jsp";
            }
            

            if (Message.length()>0)
            {
                LogMemo = Message;
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                log_user.debug(LogData);
            }

            if (ForwardFlag)
            {
                System.out.println("Forward=" + Forward);
                request.getRequestDispatcher(Forward).forward(request, response);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log_systeminfo.debug("--MerchantCaptureCtl--"+e.toString());
            request.setAttribute("errMsg", e.toString());
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
     ** �ʳf�дڥ�� Capture
     * @param Hashtable hashCarpture ���B�z�дڸ��
     */
    //private void Capture(DataBaseBean SysBean, Hashtable hashCarpture) // �쥻���^�ǭȬ�void
    //20150910 Jason BatchPmtID �אּ�ϰ��ܼ� �]BatchPmtID������k���ͩҥH�W�[�^��BatchPmtID
    //TESTCASE-013
    private String Capture(DataBaseBean2 sysBean, Hashtable hashCarpture,Hashtable hashMerchant)
    {
    //20150910 Jason�ϰ�UserBean
    	UserBean UserBean = new UserBean();
        boolean CommitFlag = true;
    //20150910 Jason�W�[BatchPmtID�ܼ�        
        String BatchPmtID = "";
        try
        {
        	//��Universally Unique Identifier
            UUID uuid = new UUID();
            BatchPmtID = uuid.toString().toUpperCase();
//            UserBean UserBean = new UserBean();
            MerchantCaptureBean CaptureBean = new MerchantCaptureBean();
            //���ˮ֦��\�� HashTable
            Hashtable hashSuccessCapture = (Hashtable) hashCarpture.get("CaptureSuccessData");            
            if (hashSuccessCapture == null)
            {
                hashSuccessCapture = new Hashtable();
            }
            //���ˮ֥��Ѫ�HashTable
            Hashtable hashFailCapture = (Hashtable) hashCarpture.get("CaptureFailData");
            if (hashFailCapture == null)
            {
                hashFailCapture = new Hashtable();
            }
            //�B�z�ˮ֦��\�������@���@����
            for (int i = 0; i < hashSuccessCapture.size(); i++)
            {
//                DataBaseBean DataBaseUpdateBean = new DataBaseBean();
//                DataBaseUpdateBean.setAutoCommit(false);
            //���^�U��쥲�n��
                String SumCaptureAmt = "0"; // �ʳf�дڪ��B�έp
                String SumRefundCaptureAmt = "0"; // �h�f�дڪ��B�έp
                String TmpTransDate = UserBean.get_TransDate("yyyy/MM/dd HH:mm:ss");
                Hashtable hashData = (Hashtable) hashSuccessCapture.get(String.valueOf(i));
                String MerchantID = hashData.get("MERCHANTID").toString();
                String SubMID = hashData.get("SUBMID").toString();
                String TerminalID = hashData.get("TERMINALID").toString();
                String AcquirerID = hashData.get("ACQUIRERID").toString();
                String OrderID = hashData.get("ORDERID").toString();
                String Sys_OrderID = hashData.get("SYS_ORDERID").toString();
                String Card_Type = hashData.get("CARD_TYPE").toString();
                String PAN = hashData.get("PAN").toString();
                String ExpireDate = hashData.get("EXPIREDATE").toString();
                String TransCode = hashData.get("TRANSCODE").toString();
                String TransDate = hashData.get("TRANSDATE").toString();
                String TransTime = hashData.get("TRANSTIME").toString();
                String ApproveCode = hashData.get("APPROVECODE").toString();
                String ResponseCode = hashData.get("RESPONSECODE").toString();
                String ResponseMsg = hashData.get("RESPONSEMSG").toString();
                String CurrencyCode = hashData.get("CURRENCYCODE").toString();
                String CaptureAmt = hashData.get("TOCAPTUREAMT").toString();
                //00�ʳf�p��дڪ��B
                if (TransCode.equalsIgnoreCase("00"))
                {
                    SumCaptureAmt = String.valueOf(Double.parseDouble(SumCaptureAmt) + Double.parseDouble(CaptureAmt));
                }
                //01�h�f�p��h�f���B
                if (TransCode.equalsIgnoreCase("01"))
                {
                    SumRefundCaptureAmt = String.valueOf(Double.parseDouble( SumRefundCaptureAmt) +
                                                         Double.parseDouble(CaptureAmt));
                }
                //�~�������
                String CaptureDate = TmpTransDate;
                String UserDefine = hashData.get("USERDEFINE").toString();
                String BatchNo = hashData.get("BATCHNO").toString();
                String CaptureFlag = "0";
                String ProcessDate = TmpTransDate;
                String Enrty_Mode = hashData.get("ENTRY_MODE").toString();
                String Condition_Code = hashData.get("CONDITION_CODE").toString();
                String ECI = hashData.get("ECI").toString();
                String CAVV = hashData.get("CAVV").toString();
                String TransMode = hashData.get("TRANSMODE").toString();
                String InstallType = hashData.get("INSTALLTYPE").toString();
                String Install = hashData.get("INSTALL").toString();
                String FirstAmt = hashData.get("FIRSTAMT").toString();
                String EachAmt = hashData.get("EACHAMT").toString();
                String FEE = hashData.get("FEE").toString();
                String RedemType = hashData.get("REDEMTYPE").toString();
                String RedemUsed = hashData.get("REDEMUSED").toString();
                String RedemBalance = hashData.get("REDEMBALANCE").toString();
                String CreditAmt = hashData.get("CREDITAMT").toString();
                String BillMessage = hashData.get("BILLMESSAGE").toString();
                String SysTraceNo = hashData.get("SYSTRACENO").toString();
                String FeeBackCode = " ";
                String FeeBackMsg = " ";
                String FeeBackDate = "";
                String DueDate = hashData.get("CAPTUREDDEALLINE").toString();
                String TransAmt = hashData.get("TRANSAMT").toString();
                String BalanceAmt = hashData.get("BALANCEAMT").toString();
                String ExtenNo = hashData.get("EXTENNO").toString();
                String RRN = hashData.get("RRN").toString();
                String MTI = hashData.get("MTI").toString();
                String XID = hashData.get("XID").toString();
                String SocialID =  hashData.get("SOCIALID").toString();
                String ReauthFlag =  hashData.get("REAUTH_FLAG").toString();
                String ExceptFlag =  "";
                String AUTH_SRC_CODE = hashData.get("AUTH_SRC_CODE").toString(); //20220210
                String BatchTxMsg = "";
                String BatchResponse = "";
//                20160112 Jason Capture�ˮ֤�insert�h��̫᭱�h��
//                String permit_partial_capture = (null==hashMerchant.get("PERMIT_PARTIAL_CAPTURE")?"":hashMerchant.get("PERMIT_PARTIAL_CAPTURE").toString());
//                if("N".equals(permit_partial_capture)){
//                checkCapturedFlag = CaptureBean.CheckCapturedFlag(SysBean, MerchantID, SubMID, Sys_OrderID);
//                }
//                
//                if (checkCapturedFlag||!UserBean.insert_Capture(SysBean, MerchantID, SubMID, TerminalID, AcquirerID, OrderID,
//                                             Sys_OrderID, Card_Type, PAN, ExpireDate, TransCode, TransDate,
//                                             TransTime, ApproveCode, ResponseCode, ResponseMsg, CurrencyCode,
//                                             CaptureAmt, CaptureDate, UserDefine, BatchNo, CaptureFlag,
//                                             ProcessDate, Enrty_Mode, Condition_Code, ECI, CAVV, TransMode,
//                                             InstallType, Install, FirstAmt, EachAmt, FEE, RedemType,
//                                             RedemUsed, RedemBalance, CreditAmt, BillMessage, FeeBackCode,
//                                             FeeBackMsg, FeeBackDate, DueDate, TransAmt, SysTraceNo,ExtenNo,
//                                             RRN,MTI,XID,SocialID,ReauthFlag,ExceptFlag))
//                {
//                //�p�G�g�J���� CommitFlag�]�� false
//                    CommitFlag = false;
//                }
                //�p��BalanceAmt = ��lBalanceAmt - �����дڪ��B
                BalanceAmt = String.valueOf(Double.parseDouble(BalanceAmt) - Double.parseDouble(CaptureAmt));
                //20150910 Jason��sbilling�p�G�g�Jcapture���ѴN�����F
                System.out.println("Capture UpdBilling Start MercahntID:"+MerchantID+" SysoOrderid:"+Sys_OrderID+" BatchPmtID:"+BatchPmtID);
                if (CommitFlag && !CaptureBean.update_BillingeNet(sysBean, MerchantID, SubMID, Sys_OrderID, TransCode, BalanceAmt))
                {
                    CommitFlag = false;
                }                               
                System.out.println("Capture GetBalance Start MercahntID:"+MerchantID+" SysoOrderid:"+Sys_OrderID+" BatchPmtID:"+BatchPmtID);
                Hashtable hashBalanceData = UserBean.get_Balance(sysBean, MerchantID, SubMID, OrderID);                 
                String BalCaptureAmt = hashBalanceData.get("CAPTUREAMT").toString();
                String BalRefundCaptureAmt = hashBalanceData.get("REFUNDCAPTUREAMT").toString();

                if (TransCode.equalsIgnoreCase("00"))
                {
                    // �ʳf
                    BalCaptureAmt = String.valueOf(Double.parseDouble(BalCaptureAmt) + Double.parseDouble(SumCaptureAmt)); // �дڪ��B
                }

                if (TransCode.equalsIgnoreCase("01"))
                {
                    // �h�f
                    BalRefundCaptureAmt = String.valueOf(Double.parseDouble(BalRefundCaptureAmt) + Double.parseDouble(SumRefundCaptureAmt)); // �h�ڪ��B
                }

                String BalBalanceAmt = hashBalanceData.get("BALANCEAMT").toString();
                BalBalanceAmt = String.valueOf(Double.parseDouble(BalBalanceAmt) - Double.parseDouble(SumCaptureAmt));

                String TmpCaptureDate = TmpTransDate;
                
                // �쥻���P�_�� �S��CommitFlag
                //if (!UserBean.update_Balance(SysBean, MerchantID, SubMID, OrderID, "", "", BalCaptureAmt,
                //        TmpCaptureDate, "", "", BalRefundCaptureAmt, TmpCaptureDate, BalBalanceAmt)) {
                
                //20150910 Jason �p�G commitFlag = false �N���n���F �]���nrollback
                System.out.println("Capture update_Balance Start MercahntID:"+MerchantID+" SysoOrderid:"+Sys_OrderID+" BatchPmtID:"+BatchPmtID);
                if (CommitFlag && !UserBean.update_Balance(sysBean, MerchantID, SubMID, OrderID, "", "", BalCaptureAmt,
                                             TmpCaptureDate, "", "", BalRefundCaptureAmt, TmpCaptureDate, BalBalanceAmt)) {
                    CommitFlag = false;
                }                
               //20160112 Jason Insert Capture�ˮ� �p�G�e�z�B�J�Y�w���ѴN����insert capture�F
                boolean checkCapturedFlag = false;
                System.out.println("Capture insert_Capture Start MercahntID:"+MerchantID+" SysoOrderid:"+Sys_OrderID+" BatchPmtID:"+BatchPmtID);
                if(CommitFlag){
                    //String permit_partial_capture = (null==hashData.get("PERMIT_PARTIAL_CAPTURE")?"":hashMerchant.get("PERMIT_PARTIAL_CAPTURE").toString());                
                    //if("N".equals(permit_partial_capture)){
                    //checkCapturedFlag = CaptureBean.CheckCapturedFlag(SysBean, MerchantID, SubMID, Sys_OrderID);
                    String transamt = hashData.get("TRANSAMT").toString();
                    System.out.println("Capture insert_Capture Start MercahntID:"+MerchantID+" SysoOrderid:"+Sys_OrderID+" BatchPmtID:"+BatchPmtID);
                    ArrayList sumcaptureamt = CaptureBean.SumCaptured(sysBean, MerchantID, SubMID, Sys_OrderID);
                    System.out.println("Get Capture AMT MercahntID:"+MerchantID+" SysoOrderid:"+Sys_OrderID+" BatchPmtID:"+BatchPmtID);
                    if(null != sumcaptureamt){
                    Hashtable hashtemp = (Hashtable)sumcaptureamt.get(0);
                    int trans_amt = Integer.parseInt(transamt);
                    int captured_amt = Integer.parseInt("".equals(hashtemp.get("TCAMT").toString().trim())?"0":hashtemp.get("TCAMT").toString());
                    if(captured_amt >= trans_amt){
                    	System.out.println("Capture captured_amt >= trans_amt rollback"+" MercahntID:"+MerchantID+" SysoOrderid:"+Sys_OrderID+" BatchPmtID:"+BatchPmtID);
                    	checkCapturedFlag = true;
                    	}
                    }
                    //20220210 ADD AUTH_SRC_CODE
                    if (checkCapturedFlag||!UserBean.insert_Capture(sysBean, MerchantID, SubMID, TerminalID, AcquirerID, OrderID,
                                                 Sys_OrderID, Card_Type, PAN, ExpireDate, TransCode, TransDate,
                                                 TransTime, ApproveCode, ResponseCode, ResponseMsg, CurrencyCode,
                                                 CaptureAmt, CaptureDate, UserDefine, BatchNo, CaptureFlag,
                                                 ProcessDate, Enrty_Mode, Condition_Code, ECI, CAVV, TransMode,
                                                 InstallType, Install, FirstAmt, EachAmt, FEE, RedemType,
                                                 RedemUsed, RedemBalance, CreditAmt, BillMessage, FeeBackCode,
                                                 FeeBackMsg, FeeBackDate, DueDate, TransAmt, SysTraceNo,ExtenNo,
                                                 RRN,MTI,XID,SocialID,ReauthFlag,ExceptFlag,AUTH_SRC_CODE))
                    	{
                    		CommitFlag = false;
                    	}
                	}
                if (CommitFlag)
                {
                    // ��s���\
                    CommitFlag = sysBean.commit();

                    if (CommitFlag)
                    {
                        BatchTxMsg = "SUCCESS";
                        BatchResponse = "�ݳB�z";
                    }
                    else
                    {
                        BatchTxMsg = "FAIL";
                        BatchResponse = "�дڧ�s����";
                    }
                }
                else
                {
                    BatchTxMsg = "FAIL";
                    BatchResponse = "�дڧ�s����";
                    sysBean.setRollBack();
                }
               // SysBean.close();
                if (!CommitFlag)
                {
                    // ����
                    BatchTxMsg = "FAIL";
                    BatchResponse = "�дڧ�s����";
                    CaptureBean.update_BillingeFail(sysBean, MerchantID, SubMID, Sys_OrderID, TransCode,
                                          "�дڧ�s����", CaptureAmt, DueDate, CaptureDate.substring(0, 10));
                }

                String ReversalFlag = hashData.get("REVERSALFLAG").toString();
                String EMail = hashData.get("EMAIL").toString();
                String Entry_Mode = hashData.get("ENTRY_MODE").toString();
                String TransType = hashData.get("TRANSTYPE").toString();
                String FeedbackCode = "";
                String FeedbackMsg = "";
                String FeedbackDate = "";
                String BatchDate = "";
                String BatchHead = "";
                String BatchType = "Capture";
                String BatchTerminalID = TerminalID;
                String BatchSysorderID = OrderID;
                String BatchTxDate = TransDate;
                String BatchTxTime = TransTime;
                String BatchTxApproveCode = ApproveCode.trim();
                String BatchTransCode = TransCode;
                String BatchTxAmt = CaptureAmt;
//                DataBaseBean DataBaseBean = new DataBaseBean();
                //20220210 ADD AUTH_SRC_CODE
                if(UserBean.insert_Batch(sysBean, MerchantID, SubMID, TerminalID, AcquirerID, OrderID,
                                      Sys_OrderID, Card_Type, PAN, ExtenNo, ExpireDate, TransCode,
                                      ReversalFlag, TransDate, TransTime, CurrencyCode, TransAmt,
                                      ApproveCode, ResponseCode, ResponseMsg, BatchNo, UserDefine,
                                      EMail, MTI, RRN, SocialID, Entry_Mode, Condition_Code,
                                      TransMode, TransType, ECI, CAVV, XID, InstallType, Install,
                                      FirstAmt, EachAmt, FEE, RedemType, RedemUsed, RedemBalance,
                                      CreditAmt, BillMessage, BalanceAmt, CaptureAmt, CaptureDate,
                                      FeedbackCode, FeedbackMsg, FeedbackDate, DueDate, BatchPmtID,
                                      BatchDate, BatchHead, BatchType, BatchTerminalID, BatchSysorderID,
                                      BatchTxDate, BatchTxTime, BatchTxApproveCode, BatchTransCode,
                                      BatchTxAmt, BatchTxMsg, BatchResponse, SysTraceNo, "",AUTH_SRC_CODE))
                {
                CommitFlag = true;
	            }
	            else
	            {
	            CommitFlag = false;
	            }
        //        DataBaseBean.close();
                
                if(CommitFlag)
                {
                	sysBean.commit();
                }
                else
                {
                	sysBean.setRollBack();
                }
            }

            for (int i = 0; i < hashFailCapture.size(); i++)
            {
                Hashtable hashData = (Hashtable) hashFailCapture.get(String.valueOf(i));
                String MerchantID = hashData.get("MERCHANTID").toString();
                String SubMID = hashData.get("SUBMID").toString();
                String TransCode = hashData.get("TRANSCODE").toString();
                String Sys_OrderID = hashData.get("SYS_ORDERID").toString();
                String CaptureAmt = hashData.get("TOCAPTUREAMT").toString();
                String CaptureMessage = hashData.get("CHECKRESPONSE").toString();
                String CaptureDate = UserBean.get_TransDate("yyyy/MM/dd HH:mm:ss");
                String Due_Date = hashData.get("CAPTUREDDEALLINE").toString();
                
                //20130726 Jason Capture Fail ��Ƨ�s update�T�{ update_billing updatebatch���\�~commit;
                if(CaptureBean.update_BillingeFail(sysBean, MerchantID, SubMID, Sys_OrderID, TransCode, CaptureMessage,
                                                CaptureAmt, Due_Date, CaptureDate.substring(0, 10)))
                {
                	CommitFlag = true;
                }
                else
                {
                	CommitFlag = false;
                }
                
                String TerminalID = hashData.get("TERMINALID").toString();
                String AcquirerID = hashData.get("ACQUIRERID").toString();
                String OrderID = hashData.get("ORDERID").toString();
                String Card_Type = hashData.get("CARD_TYPE").toString();
                String PAN = hashData.get("PAN").toString();
                String ExpireDate = hashData.get("EXPIREDATE").toString();
                String TransDate = hashData.get("TRANSDATE").toString();
                String TransTime = hashData.get("TRANSTIME").toString();
                String ApproveCode = hashData.get("APPROVECODE").toString();
                String ResponseCode = hashData.get("RESPONSECODE").toString();
                String ResponseMsg = hashData.get("RESPONSEMSG").toString();
                String CurrencyCode = hashData.get("CURRENCYCODE").toString();
                String UserDefine = hashData.get("USERDEFINE").toString();
                String BatchNo = hashData.get("BATCHNO").toString();
                String Enrty_Mode = hashData.get("ENTRY_MODE").toString();
                String Condition_Code = hashData.get("CONDITION_CODE").toString();
                String ECI = hashData.get("ECI").toString();
                String CAVV = hashData.get("CAVV").toString();
                String TransMode = hashData.get("TRANSMODE").toString();
                String InstallType = hashData.get("INSTALLTYPE").toString();
                String Install = hashData.get("INSTALL").toString();
                String FirstAmt = hashData.get("FIRSTAMT").toString();
                String EachAmt = hashData.get("EACHAMT").toString();
                String FEE = hashData.get("FEE").toString();
                String RedemType = hashData.get("REDEMTYPE").toString();
                String RedemUsed = hashData.get("REDEMUSED").toString();
                String RedemBalance = hashData.get("REDEMBALANCE").toString();
                String CreditAmt = hashData.get("CREDITAMT").toString();
                String BillMessage = hashData.get("BILLMESSAGE").toString();
                String SysTraceNo = hashData.get("SYSTRACENO").toString();
                String TransAmt = hashData.get("TRANSAMT").toString();
                String BalanceAmt = hashData.get("BALANCEAMT").toString();
                String BatchTxMsg = "FAIL";
                String BatchResponse = CaptureMessage;
                String ExtenNo = hashData.get("EXTENNO").toString();
                String ReversalFlag = hashData.get("REVERSALFLAG").toString();
                String EMail = hashData.get("EMAIL").toString();
                String MTI = hashData.get("MTI").toString();
                String RRN = hashData.get("RRN").toString();
                String SocialID = hashData.get("SOCIALID").toString();
                String Entry_Mode = hashData.get("ENTRY_MODE").toString();
                String TransType = hashData.get("TRANSTYPE").toString();
                String XID = hashData.get("XID").toString();
                String AUTH_SRC_CODE = hashData.get("AUTH_SRC_CODE").toString(); //20220210
                String FeedbackCode = "";
                String FeedbackMsg = "";
                String FeedbackDate = "";
                String BatchDate = "";
                String BatchHead = "";
                String BatchType = "";
                String BatchTerminalID = TerminalID;
                String BatchSysorderID = OrderID;
                String BatchTxDate = TransDate;
                String BatchTxTime = TransTime;
                String BatchTxApproveCode = ApproveCode.trim();
                String BatchTransCode = TransCode;
                String BatchTxAmt = CaptureAmt;

                // DataBaseBean DataBaseBean = new DataBaseBean();
                //20200210 ADD AUTH_SRC_CODE
                if(UserBean.insert_Batch(sysBean, MerchantID, SubMID, TerminalID, AcquirerID, OrderID,
                                      Sys_OrderID, Card_Type, PAN, ExtenNo, ExpireDate, TransCode,
                                      ReversalFlag, TransDate, TransTime, CurrencyCode, TransAmt,
                                      ApproveCode, ResponseCode, ResponseMsg, BatchNo, UserDefine,
                                      EMail, MTI, RRN, SocialID, Entry_Mode, Condition_Code,
                                      TransMode, TransType, ECI, CAVV, XID, InstallType, Install,
                                      FirstAmt, EachAmt, FEE, RedemType, RedemUsed, RedemBalance,
                                      CreditAmt, BillMessage, BalanceAmt, CaptureAmt, CaptureDate,
                                      FeedbackCode, FeedbackMsg, FeedbackDate, Due_Date, BatchPmtID,
                                      BatchDate, BatchHead, BatchType, BatchTerminalID,  BatchSysorderID,
                                      BatchTxDate, BatchTxTime, BatchTxApproveCode, BatchTransCode,
                                      BatchTxAmt, BatchTxMsg, BatchResponse, SysTraceNo, "",AUTH_SRC_CODE))
                {
                	CommitFlag = true;
                }
                else
                {
                	CommitFlag = false;
                }
                // DataBaseBean.close();                           
                if(CommitFlag)
                {
                	sysBean.commit();
                }
                else
                {
                	sysBean.setRollBack();
                }
              //20130726 Jason Capture Fail ��Ƨ�s update�T�{ update_billing updatebatch���\�~commit;
            }
        //20150910 Jason �^�ǧ妸�N���ѥ~���ϥ�
        return BatchPmtID;
        }
        catch (Exception e)
        {
        	//20130726 Jason Exception RollBack
        	sysBean.setRollBack();
            e.printStackTrace();
            log_systeminfo.debug(e.toString());
        }
        //20130726 Jason 
        finally
        {        	
        	try
        	{
        	//20130726 Jason ���� Exception �ɷ|�Q����~ 
            //SysBean.commit();
            sysBean.close();
        	}
        	catch (Exception e)
        	{
        	
        	}
        }
        //20150916 exception �^�ǧ妸�N��
		return BatchPmtID;
    }
}
