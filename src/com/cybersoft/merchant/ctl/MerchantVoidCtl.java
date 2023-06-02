/************************************************************
 * <p>#File Name:	MerchantVoidCtl.java	        </p>
 * <p>#Description:	            			</p>
 * <p>#Create Date:	2007/09/29		        </p>
 * <p>#Company:  	cybersoft	        	</p>
 * <p>#Notice:   					</p>
 * @author		Shirley Lin
 * @since		SPEC version
 * @version	0.1	2007/09/29	Shirley Lin
 * 202112300619-01 20220217 GARY �дڧ妸�дڳW��W��(Visa Authorization Source Code) AUTH_SRC_CODE
 ************************************************************/
package com.cybersoft.merchant.ctl;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft.bean.UserBean;
import com.cybersoft.bean.SessionControlBean;
import com.cybersoft.merchant.bean.MerchantVoidBean;
import com.cybersoft.bean.LogUtils;
/**
 * <p>����h�f��Servlet</p>
 * @version	0.1	2007/09/29	Shiley Lin
 */
public class MerchantVoidCtl extends HttpServlet
{
    private static final String CONTENT_TYPE = "text/html; charset=Big5";
    private String Forward = "./Merchant_Response.jsp"; // ������}
    private String Message = ""; // ��ܰT��

    private Hashtable hashSys = new Hashtable(); // �t�ΰѼ�
    private Hashtable hashMerUser = new Hashtable(); // �S���ϥΪ�
    private Hashtable hashMerchant = new Hashtable(); // �S���D��
    private ArrayList arrayTerminal = new ArrayList(); // �ݥ����D��
    private Hashtable hashConfData = new Hashtable();

    private ArrayList arrayData = new ArrayList();
    private Hashtable hashList = new Hashtable();
    private Hashtable hashData = new Hashtable();
    java.util.Date nowdate;
    public static final LogUtils log_user = new LogUtils("user");
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    String LogUserName = "";
    String LogFunctionName = "�u�W�����@�~";
    String LogStatus = "����";
    String LogMemo = "";
    String LogData = "";
    String LogMerchantID = "";
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
        response.setContentType(CONTENT_TYPE);
        request.setCharacterEncoding("BIG5");
        HttpSession session = request.getSession(true);
        /* Chech Session */
        SessionControlBean scb = new SessionControlBean();

        try
        {
            scb = new SessionControlBean(session, request, response);
            sysBean.setAutoCommit(false);
        }
        catch (UnsupportedOperationException E)
        {
            E.toString();
            request.getRequestDispatcher("/Merchant_Bye.jsp").forward(request, response);
            return;        	
        }

        try
        {
            hashConfData = (Hashtable) session.getAttribute("SYSCONFDATA");
            boolean Merchant_Current = false; // �S�����A
            boolean Merchant_Permit = false; // �S���v��
            boolean Terminal_Current = false; // �ݥ������A
            boolean Terminal_Permit = false; // �ݥ����v��

            if (hashConfData == null)
                hashConfData = new Hashtable();

            if (hashConfData.size() > 0)
            {
                hashSys = (Hashtable) hashConfData.get("SYSCONF"); // �t�ΰѼ�
                hashMerchant = (Hashtable) hashConfData.get("MERCHANT"); // �S���D��
                hashMerUser = (Hashtable) hashConfData.get("MERCHANT_USER"); // �S���ϥΪ�

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
//            UserBean UserBean = new UserBean();
            Merchant_Current = UserBean.check_Merchant_Column(hashMerchant, "CURRENTCODE", "B,C,D"); //  �T�{�S�����A
            String MenuKey = (String) request.getParameter(MerchantMenuCtl.MENU_FORWARD_KEY);

            request.setAttribute(MerchantMenuCtl.MENU_FORWARD_KEY, MenuKey);
            boolean User_Permit = MerchantMenuCtl.getUserRole(sysBean, session, MenuKey);
            String LogOrderID = "";

            if (User_Permit)
            {
                // �ϥΪ��v��
                if (Merchant_Current)
                {
                    //�S�����A
                    if (UserBean.check_Merchant_Column(hashMerchant, "PERMIT_SALE_VOID", "Y") ||
                        UserBean.check_Merchant_Column(hashMerchant, "PERMIT_REFUND_VOID","Y") ||
                        UserBean.check_Merchant_Column(hashMerchant, "PERMIT_CAPTURE_VOID","Y"))
                    {
                        // �S���v��
                        String Action = (String) request.getParameter("Action");
                        if (Action == null)
                            Action = "";

                        if (Action.length() == 0)
                        {
                            //�������d�ߵe��
                            Forward = "./Merchant_Void_Query.jsp";
                        }
                        else
                        {
                            String ServiceType = "";
                            if (Action.equalsIgnoreCase("Query"))
                            {
                                //�d�ߨ����C��
                                ServiceType = request.getParameter("ServiceType");
                                if (ServiceType == null)
                                    ServiceType = "";

                                ServiceType = UserBean.trim_Data(ServiceType);
                            }
                            else
                            {
                                hashData = (Hashtable) session.getAttribute("VoidData");
                                if (hashData == null)
                                    hashData = new Hashtable();

                                if (hashData.size() > 0)
                                {
                                    ServiceType = hashData.get("SERVICETYPE").toString(); // ������O
                                }
                            }

                            if (ServiceType.equalsIgnoreCase("Sale"))
                            {
                                // �ʳf����
                                Merchant_Permit = UserBean.check_Merchant_Column(hashMerchant, "PERMIT_SALE_VOID", "Y"); //  �T�{�S���v��
                            }

                            if (ServiceType.equalsIgnoreCase("Refund"))
                            {
                                // �h�f����
                                Merchant_Permit = UserBean.check_Merchant_Column( hashMerchant, "PERMIT_REFUND_VOID", "Y"); //  �T�{�S���v��
                            }

                            if (ServiceType.equalsIgnoreCase("Capture"))
                            {
                                // �дڨ���
                                Merchant_Permit = UserBean.check_Merchant_Column(hashMerchant, "PERMIT_CAPTURE_VOID", "Y"); //  �T�{�S���v��
                            }

                            if (Merchant_Permit)
                            {
                                // �S���v��
                                MerchantVoidBean VoidBean = new MerchantVoidBean();
                                System.out.println("Action=" + Action);
                                if (Action.equalsIgnoreCase("Query"))
                                {
                                    //�d�ߨ����C��
                                    sysBean.setAutoCommit(false);
                                    String OrderType = request.getParameter("OrderType");
                                    if (OrderType == null)
                                        OrderType = "";

                                    OrderType = UserBean.trim_Data(OrderType);

                                    String OrderID = request.getParameter("OrderID");
                                    if (OrderID == null)
                                        OrderID = "";

                                    OrderID = UserBean.trim_Data(OrderID);
                                    LogOrderID = OrderID;
                                    String MerchantID = hashMerchant.get("MERCHANTID").toString();

                                    if (MerchantID == null)
                                        MerchantID = "";

                                    MerchantID = UserBean.trim_Data(MerchantID);
                                    String SubMID = hashMerUser.get("SUBMID").toString();
                                    if (SubMID == null)
                                        SubMID = "";

                                    String VoidType = "";
                                    if (ServiceType.equalsIgnoreCase("Sale"))
                                    {
                                        // �ʳf����
                                        VoidType = "�ʳf����";
                                        arrayData = VoidBean.get_BillingVoid_List(sysBean, MerchantID, SubMID, OrderType,OrderID, "");

                                        if (arrayData == null)
                                            arrayData = new ArrayList();

                                        hashData = VoidBean.check_SaleVoid_Status(arrayData);
                                        Forward ="./Merchant_SaleVoid_List.jsp";
                                    }

                                    if (ServiceType.equalsIgnoreCase("Refund"))
                                    {
                                        // �h�f����
                                        VoidType = "�h�f����";
                                        arrayData = VoidBean.get_BillingVoid_List(sysBean, MerchantID, SubMID, OrderType,OrderID, "");

                                        if (arrayData == null)
                                            arrayData = new ArrayList();

                                        hashData = VoidBean.check_RefundVoid_Status(arrayData);
                                        Forward ="./Merchant_RefundVoid_List.jsp";
                                    }

                                    if (ServiceType.equalsIgnoreCase("Capture"))
                                    {
                                        // �дڨ���
                                        VoidType = "�дڨ���";
                                        arrayData = VoidBean.get_CaptureVoid_List(sysBean, MerchantID, SubMID, OrderType, OrderID);

                                        if (arrayData == null)
                                            arrayData = new ArrayList();

                                        hashData = VoidBean.check_CaptureVoid_Status(arrayData);
                                        Forward = "./Merchant_CaptureVoid_List.jsp";
                                    }

                                    hashData.put("DATALIST", arrayData); // �d�߸��
                                    hashData.put("SERVICETYPE", ServiceType); // ������O

                                    if (session.getAttribute("VoidData") != null)
                                    {
                                        session.removeAttribute("VoidData");
                                    }
                                    session.setAttribute("VoidData", hashData);
                                    String VoidFlag = hashData.get("FLAG").toString();
                                    Message = hashData.get("MESSAGE").toString();

                                    if (VoidFlag.equalsIgnoreCase("true"))
                                    {
                                        LogStatus = "���\";
                                        Message = "����i����";
                                    }

                                    LogMemo = Message;
                                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                    LogData = UserBean.get_LogData(LogMerchantID,LogUserName, LogFunctionName, LogStatus, LogOrderID+VoidType+LogMemo);
                                    log_user.debug(LogData);
                                }

                                if (Action.equalsIgnoreCase("Void"))
                                {
                                    //������
                                    String VoidType = "";
                                    sysBean.setAutoCommit(false);
                                    if (ServiceType.equalsIgnoreCase("Sale"))
                                    {
                                        // �ʳf����
                                        VoidType = "�ʳf����";
                                        arrayData = (ArrayList) hashData.get("DATALIST");

                                        for (int i = 0; i < arrayData.size(); i++)
                                        {
                                            Hashtable hashTemp = (Hashtable)arrayData.get(i);
                                            String transcode = hashTemp.get("TRANSCODE").toString();

                                            if (transcode.equalsIgnoreCase("00"))
                                            {
                                                hashList = hashTemp;
                                                break;
                                            }
                                        }

                                        if (hashList.size() > 0)
                                        {
                                            String MerchantID = hashList.get("MERCHANTID").toString();
                                            String TerminalID = hashList.get("TERMINALID").toString();
                                            Terminal_Current = UserBean.check_Terminal_Column(MerchantID, TerminalID, arrayTerminal, "CURRENTCODE", "B,C,D"); //  �T�{�ݥ������A
                                            Terminal_Permit = UserBean.check_Terminal_Column(MerchantID, TerminalID,arrayTerminal,"PERMIT_SALE_VOID", "Y"); //  �T�{�ݥ����v��

                                            if (Terminal_Current && Terminal_Permit)
                                            {
                                                Sale_Void(MerchantID);
                                                if (session.getAttribute("VoidDataResponse") != null)
                                                {
                                                    session.removeAttribute("VoidDataResponse");
                                                }

                                                if (session.getAttribute("Message") != null)
                                                {
                                                    session.removeAttribute("Message");
                                                }

                                                ArrayList arrayRefundVoidData = new ArrayList();
                                                String SubMID = hashList.get("SUBMID").toString();
                                                String OrderID = hashList.get("ORDERID").toString();
                                                LogOrderID = OrderID;
                                                arrayRefundVoidData = UserBean.get_BillingHistory(sysBean, MerchantID, SubMID, "Order", OrderID, "");
                                                if (arrayRefundVoidData==null)
                                                    arrayRefundVoidData =  new ArrayList();

                                                session.setAttribute("VoidDataResponse", arrayRefundVoidData);
                                                Forward = "./Merchant_SaleVoid_Response.jsp";
                                                LogStatus = "���\";
                                            }
                                            else
                                            {
                                                Message = "�ݥ����L���v���Ь�����B�z";
                                                Forward = "./Merchant_Response.jsp";
                                            }
                                        }
                                        else
                                        {
                                            Message = "�`�N�G�d�L������";
                                            Forward = "./Merchant_Response.jsp";
                                        }

                                        if (session.getAttribute("VoidData") != null)
                                        {
                                            session.removeAttribute("VoidData");
                                        }

                                        session.setAttribute("Message", Message);
                                        LogMemo = Message;
                                        LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                        LogData = UserBean.get_LogData(LogMerchantID,LogUserName, LogFunctionName,LogStatus, LogOrderID+VoidType+LogMemo);
                                        log_user.debug(LogData);
                                    }

                                    if (ServiceType.equalsIgnoreCase("Refund"))
                                    {
                                        // �h�f����
                                        VoidType = "�h�f�f����";
                                        String InputSys_OrderID = request.getParameter("InputSys_OrderID");

                                        if (InputSys_OrderID == null)
                                            InputSys_OrderID = "";

                                        System.out.println("InputSys_OrderID=" + InputSys_OrderID);
                                        arrayData = (ArrayList) hashData.get("DATALIST");

                                        for (int i = 0; i < arrayData.size(); i++)
                                        {
                                            Hashtable hashTemp = (Hashtable)arrayData.get(i);
                                            String transcode = hashTemp.get("TRANSCODE").toString();
                                            if (transcode.equalsIgnoreCase("01"))
                                            {
                                                hashList = hashTemp;
                                                break;
                                            }
                                        }

                                        String MerchantID = hashList.get("MERCHANTID").toString();
                                        String TerminalID = hashList.get("TERMINALID").toString();
                                        Terminal_Current = UserBean.check_Terminal_Column(MerchantID, TerminalID, arrayTerminal, "CURRENTCODE", "B,C,D"); //  �T�{�ݥ������A
                                        Terminal_Permit = UserBean.check_Terminal_Column(MerchantID, TerminalID, arrayTerminal, "PERMIT_REFUND_VOID", "Y"); //  �T�{�ݥ����v��

                                        if (Terminal_Current && Terminal_Permit)
                                        {
                                            Refund_Void(MerchantID,InputSys_OrderID);
                                            if (session.getAttribute("VoidDataResponse") != null)
                                            {
                                                session.removeAttribute("VoidDataResponse");
                                            }

                                            if (session.getAttribute("Message") != null)
                                            {
                                                session.removeAttribute("Message");
                                            }

                                            ArrayList arrayRefundVoidData = new ArrayList();
                                            LogOrderID = InputSys_OrderID;
                                            String arrayOrderID[] = InputSys_OrderID.split(",");
                                            String SubMID = hashList.get("SUBMID").toString();

                                            for (int Cnt = 0; Cnt < arrayOrderID.length; ++Cnt)
                                            {
                                                ArrayList arrayBilling = UserBean.get_BillingHistory(sysBean, MerchantID, SubMID, "Sys_OrderID", arrayOrderID[Cnt], "");
                                                if (arrayBilling!=null && arrayBilling.size()>0)
                                                {
                                                    arrayRefundVoidData = arrayBilling;
                                                }
                                            }
//                                            session.setAttribute("VoidDataResponse", arrayRefundVoidData);
                                            session.setAttribute("VoidDataResponse", arrayRefundVoidData);
                                            Forward = "./Merchant_RefundVoid_Response.jsp";
                                            LogStatus = "���\";
                                        }
                                        else
                                        {
                                            Message = "�ݥ����L���v���Ь�����B�z";
                                            Forward = "./Merchant_Response.jsp";
                                        }

                                        if (session.getAttribute("VoidData") != null)
                                        {
                                            session.removeAttribute("VoidData");
                                        }

                                        session.setAttribute("Message", Message);
                                        LogMemo = Message;
                                        LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                        LogData = UserBean.get_LogData(LogMerchantID,LogUserName, LogFunctionName, LogStatus, LogOrderID+VoidType+LogMemo);
                                        log_user.debug(LogData);
                                    }

                                    if (ServiceType.equalsIgnoreCase("Capture"))
                                    {
                                        // �дڨ���
                                        String InputROWID = request.getParameter("InputROWID");
                                        if (InputROWID == null)
                                            InputROWID = "";

                                        System.out.println("InputROWID=" + InputROWID);
                                        String InputTERMINALID = request.getParameter("InputTERMINALID");

                                        if (InputTERMINALID == null)
                                            InputTERMINALID = "";

                                        System.out.println("InputTERMINALID=" + InputTERMINALID);
                                        String Terminal[] = InputTERMINALID.split(",");
                                        String MerchantID = hashMerchant.get("MERCHANTID").toString();
                                        Terminal_Current = true;
                                        Terminal_Permit = true;

                                        for (int i = 0; i < Terminal.length; ++i)
                                        {
                                            if (!UserBean.check_Terminal_Column(MerchantID, Terminal[i], arrayTerminal, "CURRENTCODE", "B,C,D"))
                                            {
                                                //  �T�{�ݥ������A
                                                Terminal_Current = false;
                                            }

                                            if (!UserBean.check_Terminal_Column(MerchantID, Terminal[i], arrayTerminal, "PERMIT_CAPTURE_VOID", "Y"))
                                            {
                                                //  �T�{�ݥ����v��
                                                Terminal_Permit = false;
                                            }
                                        }

                                        if (Terminal_Current && Terminal_Permit)
                                        {
                                            MerchantVoidBean MerchantVoidBean = new MerchantVoidBean();
                                            ArrayList arrayCaptureVoidData = MerchantVoidBean.get_CaptureVoid_List(sysBean, InputROWID);
                                            Hashtable hashBalance = (Hashtable)MerchantVoidBean.sum_CaptureVoid_Amt(arrayCaptureVoidData);
                                            String OverRefundLimit = hashMerchant.get("OVER_REFUND_LIMIT").toString();
                                            Hashtable hashCheckData = MerchantVoidBean.check_CaptureVoid_Action_Status(arrayCaptureVoidData, hashBalance, OverRefundLimit);
                                            System.out.println("hashCheckData="+hashCheckData);
                                            Hashtable hashSuccessData = (Hashtable)hashCheckData.get("Success");
                                            Hashtable hashFailData = (Hashtable)hashCheckData.get("Fail");
                                            Hashtable hashtmpData = new Hashtable();

                                            if (hashSuccessData.size()>0)
                                            {
                                                hashtmpData = (Hashtable)hashSuccessData.get("0");
                                            }
                                            else
                                            {
                                                hashtmpData = (Hashtable)hashFailData.get("0");
                                            }

                                            LogOrderID = hashtmpData.get("ORDERID").toString();
                                            if (hashFailData.size() > 0)
                                            {
                                                Hashtable tmp = (Hashtable)hashFailData.get("0");
                                                Message = tmp.get("MESSAGE").toString();
                                            }
                                            else
                                            {
                                                Capture_Void(MerchantID, hashSuccessData);
                                            }

                                            Forward = "./Merchant_CaptureVoid_Response.jsp";
                                            if (hashSuccessData.size()>0)
                                            {
                                                LogStatus = "���\";
                                            }
                                            else
                                            {
                                                LogStatus = "����";
                                            }

                                            if (session.getAttribute("VoidDataResponse") != null)
                                            {
                                                session.removeAttribute("VoidDataResponse");
                                            }

                                            if (session.getAttribute("Message") != null)
                                            {
                                                session.removeAttribute("Message");
                                            }

                                            arrayCaptureVoidData = MerchantVoidBean.get_CaptureVoid_List(sysBean, InputROWID);
                                            session.setAttribute("VoidDataResponse", arrayCaptureVoidData );
                                        }
                                        else
                                        {
                                            Message = "�ݥ����L���v���Ь�����B�z";
                                            Forward = "./Merchant_Response.jsp";
                                        }

                                        if (session.getAttribute("VoidData") != null)
                                        {
                                            session.removeAttribute("VoidData");
                                        }

                                        session.setAttribute("Message", Message);
                                        LogMemo = Message;
                                        LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                        LogData = UserBean.get_LogData(LogMerchantID,LogUserName, LogFunctionName, LogStatus , LogOrderID+VoidType+LogMemo);
                                        log_user.debug(LogData);
                                    }
                                }
                            }
                            else
                            {
                                Message = "�S���L���\���v��";
                                Forward = "./Merchant_Response.jsp";
                                session.setAttribute("Message", Message);
                                LogMemo = Message;
                                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                LogData = UserBean.get_LogData(LogMerchantID,LogUserName, LogFunctionName, LogStatus , LogMemo);
                                log_user.debug(LogData);
                            }
                        }
                    }
                    else
                    {
                        Message = "�S���L���\���v��";
                        Forward = "./Merchant_Response.jsp";
                        session.setAttribute("Message", Message);
                        LogMemo = Message;
                        LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                        LogData = UserBean.get_LogData(LogMerchantID,LogUserName, LogFunctionName, LogStatus , LogMemo);
                        log_user.debug(LogData);
                    }
                }
                else
                {
                    String CurrentCode = hashMerchant.get("CURRENTCODE").toString();
                    Message = UserBean.get_CurrentCode(CurrentCode);
                    Forward = "./Merchant_Response.jsp";
                    session.setAttribute("Message", Message);
                    LogMemo = Message;
                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                    LogData = UserBean.get_LogData(LogMerchantID,LogUserName, LogFunctionName, LogStatus , LogMemo);
                    log_user.debug(LogData);
                }
            }
            else
            {
                Message = "�ϥΪ̵L���v���Ь��t�κ޲z��";
                Forward = "./Merchant_NoUse.jsp";
                session.setAttribute("Message", Message);
                LogMemo = Message;
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID,LogUserName, LogFunctionName, LogStatus , LogMemo);
                log_user.debug(LogData);
            }
            System.out.println("------ Forward=" + Forward);
            request.getRequestDispatcher(Forward).forward(request, response);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log_systeminfo.debug("--MerchantVoidCtl--"+e.toString());
            request.setAttribute("errMsg", e.toString());
            request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
        }
        //2013 Jason finally�B�zSysBean.close()
        finally{
        try
        {
            sysBean.close();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
            log_systeminfo.debug("--MerchantVoidCtl--"+e.toString());
            request.setAttribute("errMsg", e.toString());
            request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
        }
        }
    }

    /**
     * �ʳf���� Sale_Void
     * @param String  MerchantID �S���N��
     */
    private void Sale_Void(String MerchantID)
    {
        Hashtable hashResponse = new Hashtable();
        try
        {
            ArrayList arrayTemp = new ArrayList();
//            UserBean UserBean = new UserBean();
            MerchantVoidBean MerchantVoidBean = new MerchantVoidBean(); // Select
            String SubMID = hashList.get("SUBMID").toString();
            String OrderID = hashList.get("ORDERID").toString();
            String Cancel = hashList.get("TRANSAMT").toString();
            System.out.println("OrderID=" + OrderID);
            arrayTemp = MerchantVoidBean.get_BillingVoid_List(sysBean, MerchantID, SubMID, "Order", OrderID, "00");
            Hashtable hashTemp = MerchantVoidBean.check_SaleVoid_Action_Status(arrayTemp, Cancel);
            String CheckFlag = hashTemp.get("FLAG").toString();

            if (CheckFlag.equalsIgnoreCase("true"))
            {
                // �i�i�����
                ArrayList arrayBilling = UserBean.get_Billing(sysBean, MerchantID, SubMID, "Order", OrderID, "00");
                if (arrayBilling.size() > 0)
                {
                    Hashtable hashBilling = (Hashtable) arrayBilling.get(0);
//                    DataBaseBean DataBaseBean = new DataBaseBean();
//                    DataBaseBean.setAutoCommit(false);
                    boolean CommitFlag = true;
                    String TransCode = "10";
                    String TmpTransDate = UserBean.get_TransDate("yyyyMMddHHmmss");
                    String TransDate = TmpTransDate.substring(0, 8);
                    String TransTime = TmpTransDate.substring(8, 14);
                    String TmpSerial = UserBean.get_TransDate("MMddHHmmssSSSS");
                    String Sys_OrderID = OrderID + "_" + TmpSerial;
                    String MTI = "0220";
                    String OldSys_OrderID = hashBilling.get("SYS_ORDERID").toString();
                    String TerminalID = hashBilling.get("TERMINALID").toString();
                    String TransAmt = hashBilling.get("TRANSAMT").toString();
                    String BillBalanceAmt = String.valueOf(Double.parseDouble(TransAmt) * -1);
                    String AcquirerID = hashBilling.get("ACQUIRERID").toString();
                    String Card_Type = hashBilling.get("CARD_TYPE").toString();
                    String PAN = hashBilling.get("PAN").toString();
                    String ExtenNo = hashBilling.get("EXTENNO").toString();
                    String ExpireDate = hashBilling.get("EXPIREDATE").toString();
                    String ReversalFlag = hashBilling.get("REVERSALFLAG").toString();
                    String CurrencyCode = hashBilling.get("CURRENCYCODE").toString();
                    String TransStatus = "A";
                    String ApproveCode = hashBilling.get("APPROVECODE").toString();
                    String ResponseCode = hashBilling.get("RESPONSECODE").toString();
                    String ResponseMsg = hashBilling.get("RESPONSEMSG").toString();
                    String Entry_Mode = hashBilling.get("ENTRY_MODE").toString();
                    String Condition_Code = hashBilling.get("CONDITION_CODE").toString();
                    String BatchNo = hashBilling.get("BATCHNO").toString();
                    String UserDefine = hashBilling.get("USERDEFINE").toString();
                    String Direction = "R";
                    String EMail = hashBilling.get("EMAIL").toString();
                    String RRN = hashBilling.get("RRN").toString();
                    String SocialID = hashBilling.get("SOCIALID").toString();
                    String TransMode = hashBilling.get("TRANSMODE").toString();
                    String TransType = hashBilling.get("TRANSTYPE").toString();
                    String ECI = hashBilling.get("ECI").toString();
                    String CAVV = hashBilling.get("CAVV").toString();
                    String XID = hashBilling.get("XID").toString();
                    String InstallType = hashBilling.get("INSTALLTYPE").toString();
                    String Install = hashBilling.get("INSTALL").toString();
                    String FirstAmt = hashBilling.get("FIRSTAMT").toString();
                    String EachAmt = hashBilling.get("EACHAMT").toString();
                    String FEE = hashBilling.get("FEE").toString();
                    String RedemType = hashBilling.get("REDEMTYPE").toString();
                    String RedemUsed = hashBilling.get("REDEMUSED").toString();
                    String RedemBalance = hashBilling.get("REDEMBALANCE").toString();
                    String CreditAmt = hashBilling.get("CREDITAMT").toString();
                    String BillMessage = hashBilling.get("BILLMESSAGE").toString();
                    String SysTraceNo = hashBilling.get("SYSTRACENO").toString();
                    String AUTH_SRC_CODE = hashBilling.get("AUTH_SRC_CODE").toString();
                    
                    // �]���d�O��C - CUP�ɶ�bAuthlog��檺������쪺��Ʒ|���P
                    // Direction = S & TransStatus = R & PAN = 62XX
                    // Merchant Console �u�W�����@�~�Ҳ�  �ק�  by Jimmy Kang 20150511  -- �ק�}�l --
                    if (Card_Type.equalsIgnoreCase("C"))
                    {
                    	PAN = "62XX";
                    	Direction = "S";
                    	TransStatus = "R";
                    }
                    // Merchant Console �u�W�����@�~�Ҳ�  �ק�  by Jimmy Kang 20150511  -- �קﵲ�� --
                    
                    System.out.println("insert_AuthLog");
                    //20220210 ADD AUTH_SRC_CODE
                    if (!UserBean.insert_AuthLog(sysBean, MerchantID, SubMID, TerminalID, AcquirerID,
                                                 OrderID, Sys_OrderID, Card_Type, PAN, ExtenNo,
                                                 ExpireDate, TransCode, ReversalFlag, TransDate,
                                                 TransTime, CurrencyCode, TransAmt, TransStatus,
                                                 ApproveCode, ResponseCode, ResponseMsg, Entry_Mode,
                                                 Condition_Code, BatchNo, UserDefine, Direction, EMail,
                                                 MTI, RRN, SocialID, TransMode, TransType, ECI, CAVV, XID,
                                                 InstallType, Install, FirstAmt, EachAmt, FEE, RedemType,
                                                 RedemUsed, RedemBalance, CreditAmt, BillMessage,
                                                 SysTraceNo,AUTH_SRC_CODE))
                    {
                        CommitFlag = false;
                    }
                    
                    System.out.println("insert_Billing");
                    //20220210 ADD AUTH_SRC_CODE
                    if (!UserBean.insert_Billing(sysBean, MerchantID, SubMID, TerminalID, AcquirerID,
                                                 OrderID, Sys_OrderID, Card_Type, PAN, ExtenNo,
                                                 ExpireDate, TransCode, ReversalFlag, TransDate,
                                                 TransTime, CurrencyCode, TransAmt, ApproveCode,
                                                 ResponseCode, ResponseMsg, BatchNo, UserDefine, EMail,
                                                 MTI, RRN, SocialID, Entry_Mode, Condition_Code, TransMode,
                                                 TransType, ECI, CAVV, XID, InstallType, Install, FirstAmt,
                                                 EachAmt, FEE, RedemType, RedemUsed, RedemBalance,
                                                 CreditAmt, BillMessage, BillBalanceAmt, SysTraceNo,AUTH_SRC_CODE))
                    {
                        CommitFlag = false;
                    }

                    System.out.println("update_Billing");

                    if (!MerchantVoidBean.update_BillingeVoid(sysBean, MerchantID, SubMID, OldSys_OrderID, "00")) {
                        CommitFlag = false;
                    }

                    System.out.println("update_Balance");
                    Hashtable hashBalanceData = UserBean.get_Balance(sysBean, MerchantID, SubMID, OrderID);
                    String AuthAmt = hashBalanceData.get("AUTHAMT").toString();
                    String CancelDate = TmpTransDate.substring(0, 4) + "/" + TmpTransDate.substring(4, 6) + "/" +
                                        TmpTransDate.substring(6, 8) + " " + TmpTransDate.substring(8, 10) + ":" +
                                        TmpTransDate.substring(10, 12) + ":" + TmpTransDate.substring(12, 14);
                    String CancelAmt = AuthAmt;
                    String BalanceAmt = String.valueOf(Double.parseDouble(AuthAmt) * -1);
                    System.out.println("AuthAmt=" + AuthAmt + "CancelAmt=" + CancelAmt + "BalanceAmt=" + BalanceAmt);

                    if (!UserBean.update_Balance(sysBean, MerchantID, SubMID, OrderID, "", "", "", "", CancelAmt, CancelDate, "", "", BalanceAmt)) {
                        CommitFlag = false;
                    }

                    
                    // ���ѱ� By Jimmy Kang 20150513 
                    // �N�U�Cif(!UserBean.insert_SAF)�P�_�����ܥd�O���� C - CUP ��else��
                    /*System.out.println("insert_SAF");
                    if (!UserBean.insert_SAF(SysBean, MerchantID, SubMID, TerminalID, AcquirerID, OrderID,
                                             Sys_OrderID, Card_Type, PAN, ExtenNo, ExpireDate, TransCode,
                                             ReversalFlag, TransDate, TransTime, CurrencyCode, TransAmt,
                                             ApproveCode, ResponseCode, ResponseMsg, Entry_Mode,
                                             Condition_Code, BatchNo, UserDefine, EMail, MTI, RRN,
                                             SocialID, TransMode, TransType, ECI, CAVV, XID, InstallType,
                                             Install, FirstAmt, EachAmt, FEE, RedemType, RedemUsed, RedemBalance,
                                             CreditAmt, "REQ", "", "", "", "", "", SysTraceNo))
                    
                    {
                        CommitFlag = false;
                    }*/
                    
                    // Merchant Console �u�W�����@�~�Ҳ�  �ק�  by Jimmy Kang 20150511  -- �ק�}�l --
                    // Direction = S & TransStatus = R & Cup_TransCode = 31 & PAN = 62XX
                    if (Card_Type.equalsIgnoreCase("C"))
                    {
                    	String Cup_TransCode = "31";
                    	String Cps_Refresh = "N"; 
                    	String Retry_Cnt = "0";
                    	
                    	// �]��insert�ɨS����, ���F�קKSQL error, �ҥH��0
                    	String Settle_Amount = "0";
                    	String Cup_QID = "0";
                    	
                    	System.out.println("insert_AuthLog_Cup");
                        if (!UserBean.insert_AuthLog_Cup(sysBean, MerchantID, SubMID, Sys_OrderID, TransCode, TransDate,
                        		TransTime, "", Settle_Amount, "", "", "", "", "", Cup_TransCode, TransStatus, ResponseCode, ResponseMsg,
                        		SysTraceNo, Cup_QID, "", Direction, Cps_Refresh, "", "", "", "", Retry_Cnt, PAN, TransAmt, "", ""))
                        {
                            CommitFlag = false;
                        }
                        
                        System.out.println("insert_SAF_Cup");
                        
                        // �]��insert�ɨS����, ���F�קKSQL error, �ҥH��0
                    	//String Settle_Amount = "0";
                    	//String Cup_QID = "0";
                        
                        if (!UserBean.insert_SAF_Cup(sysBean, MerchantID, SubMID, TerminalID, AcquirerID, OrderID,
                                Sys_OrderID, Card_Type, PAN, ExtenNo, ExpireDate, TransCode,
                                ReversalFlag, TransDate, TransTime, CurrencyCode, TransAmt,
                                ApproveCode, ResponseCode, ResponseMsg, Entry_Mode,
                                Condition_Code, BatchNo, UserDefine, EMail, MTI, RRN,
                                SocialID, "REQ", "", "", "", "", "", SysTraceNo, Retry_Cnt, ECI, TransType, TransMode))
                    	{
                    		CommitFlag = false;
                    	}
                    }                    	
                    else
                    {
                    	System.out.println("insert_SAF");
                    	if (!UserBean.insert_SAF(sysBean, MerchantID, SubMID, TerminalID, AcquirerID, OrderID,
                                Sys_OrderID, Card_Type, PAN, ExtenNo, ExpireDate, TransCode,
                                ReversalFlag, TransDate, TransTime, CurrencyCode, TransAmt,
                                ApproveCode, ResponseCode, ResponseMsg, Entry_Mode,
                                Condition_Code, BatchNo, UserDefine, EMail, MTI, RRN,
                                SocialID, TransMode, TransType, ECI, CAVV, XID, InstallType,
                                Install, FirstAmt, EachAmt, FEE, RedemType, RedemUsed, RedemBalance,
                                CreditAmt, "REQ", "", "", "", "", "", SysTraceNo))
                    	{
                    		CommitFlag = false;
                    	}
                    }
                    // Merchant Console �u�W�����@�~�Ҳ�  �ק�  by Jimmy Kang 20150511  -- �קﵲ�� --
                    
                    

                    if (CommitFlag)
                    {
                        CommitFlag = sysBean.commit();
                    }
                    else
                    {
                    	sysBean.setRollBack();
                    }
                   // SysBean.close();

                    if (CommitFlag)
                    {
                        Message = "�u�W�����@�~���\";
                    }
                    else
                    {
                        Message = "�u�W�����@�~����";
                    }
                    Forward = "./Merchant_Response.jsp";
                }
                else
                {
                    Message = "�`�N�G�d�L������";
                    Forward = "./Merchant_Response.jsp";
                }
            }
            else
            {
                Message = hashTemp.get("MESSAGE").toString();
                Forward = "./Merchant_Response.jsp";
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log_systeminfo.debug("--MerchantVoidCtl--"+e.toString());
        }
    }

    /**
     * �h�f���� Refund_Void
     * @param String  MerchantID �S���N��
     * @param String  InputSys_OrderID ���w�������h�fSys_OrderID
     */
    private void Refund_Void(String MerchantID, String InputSys_OrderID)
    {
        String arrayOrderID[] = InputSys_OrderID.split(",");
        try
        {
            boolean CommitFlag = true;
            Hashtable hashBalanceData = new Hashtable();
//            DataBaseBean DataBaseUpdateBean = new DataBaseBean();
//            DataBaseUpdateBean.setAutoCommit(false);
            ArrayList arrayTemp = new ArrayList();
//            UserBean UserBean = new UserBean();
            String SubMID = hashList.get("SUBMID").toString();
            MerchantVoidBean MerchantVoidBean = new MerchantVoidBean();
            String TmpTransDate = UserBean.get_TransDate("yyyyMMddHHmmss");
            String TotalCancelRefund = "0";
            int CheckCount = 0;

            if (Message.length() > 0)
            {
                Message = Message + "<br>";
            }

            Message = Message + arrayOrderID[0];
            arrayTemp = MerchantVoidBean.get_BillingVoid_List(sysBean, MerchantID, SubMID, "Sys_OrderID", arrayOrderID[0], "01");
            System.out.println("arrayTemp.size()=" + arrayTemp.size());
            String Trans = hashList.get("TRANSAMT").toString();
            Hashtable hashTemp = MerchantVoidBean.check_RefundVoid_Action_Status(arrayTemp,Trans);
            String CheckFlag = hashTemp.get("FLAG").toString();
            System.out.println("CheckFlag=" + CheckFlag);

            if (CheckFlag.equalsIgnoreCase("true"))
            {
                // ����i��s
                CheckCount++;
            }

            System.out.println("CheckCount=" + CheckCount + ",arrayOrderID.length=" + arrayOrderID.length);
            if (CheckFlag.equalsIgnoreCase("true"))
            {
                String OrderID = "";
                for (int Cnt = 0; Cnt < arrayOrderID.length; ++Cnt)
                {
                    ArrayList arrayBilling = UserBean.get_Billing(sysBean, MerchantID, SubMID, "Sys_OrderID", arrayOrderID[Cnt], "01");
                    System.out.println("arrayBilling.size()=" + arrayBilling.size());

                    if (arrayBilling.size() > 0)
                    {
                        Hashtable hashBilling = (Hashtable) arrayBilling.get(0);
                        String TransCode = "11";
                        String TransDate = TmpTransDate.substring(0, 8);
                        String TransTime = TmpTransDate.substring(8, 14);
                        OrderID = hashBilling.get("ORDERID").toString();
                        System.out.println("-------- OrderID= " + OrderID);
                        System.out.println("get_Balance");
                        hashBalanceData = UserBean.get_Balance(sysBean, MerchantID, SubMID, OrderID);
                        System.out.println("MerchantID=" + MerchantID + ",SubMID=" + SubMID + ",OrderID=" + OrderID);
                        System.out.println("hashBalanceData=" + hashBalanceData);
                        String TmpSerial = UserBean.get_TransDate("MMddHHmmssSSSS");
                        String Sys_OrderID = OrderID + "_" + TmpSerial;
                        String MTI = "0220";
                        String OldSys_OrderID = hashBilling.get("SYS_ORDERID").toString();
                        String TerminalID = hashBilling.get("TERMINALID").toString();
                        String TransAmt = hashBilling.get("TRANSAMT").toString();
                        String BillBalanceAmt = String.valueOf(Double.parseDouble(TransAmt) * -1);
                        String AcquirerID = hashBilling.get("ACQUIRERID").toString();
                        String Card_Type = hashBilling.get("CARD_TYPE").toString();
                        String PAN = hashBilling.get("PAN").toString();
                        String ExtenNo = hashBilling.get("EXTENNO").toString();
                        String ExpireDate = hashBilling.get("EXPIREDATE").toString();
                        String ReversalFlag = hashBilling.get("REVERSALFLAG").toString();
                        String CurrencyCode = hashBilling.get("CURRENCYCODE").toString();
                        String TransStatus = "A";
                        String ApproveCode = hashBilling.get("APPROVECODE").toString();
                        String ResponseCode = hashBilling.get("RESPONSECODE").toString();
                        String ResponseMsg = hashBilling.get("RESPONSEMSG").toString();
                        String Entry_Mode = hashBilling.get("ENTRY_MODE").toString();
                        String Condition_Code = hashBilling.get("CONDITION_CODE").toString();
                        String BatchNo = hashBilling.get("BATCHNO").toString();
                        String UserDefine = hashBilling.get("USERDEFINE").toString();
                        String Direction = "R";
                        String EMail = hashBilling.get("EMAIL").toString();
                        String RRN = hashBilling.get("RRN").toString();
                        String SocialID = hashBilling.get("SOCIALID").toString();
                        String TransMode = hashBilling.get("TRANSMODE").toString();
                        String TransType = hashBilling.get("TRANSTYPE").toString();
                        String ECI = hashBilling.get("ECI").toString();
                        String CAVV = hashBilling.get("CAVV").toString();
                        String XID = hashBilling.get("XID").toString();
                        String InstallType = hashBilling.get("INSTALLTYPE").toString();
                        String Install = hashBilling.get("INSTALL").toString();
                        String FirstAmt = hashBilling.get("FIRSTAMT").toString();
                        String EachAmt = hashBilling.get("EACHAMT").toString();
                        String FEE = hashBilling.get("FEE").toString();
                        String RedemType = hashBilling.get("REDEMTYPE").toString();
                        String RedemUsed = hashBilling.get("REDEMUSED").toString();
                        String RedemBalance = hashBilling.get("REDEMBALANCE").toString();
                        String CreditAmt = hashBilling.get("CREDITAMT").toString();
                        String BillMessage = hashBilling.get("BILLMESSAGE").toString();
                        String SysTraceNo = hashBilling.get("SYSTRACENO").toString();
                        String AUTH_SRC_CODE = hashBilling.get("AUTH_SRC_CODE").toString(); //20220210 ADD AUTH_SRC_CODE                        
                        System.out.println("SysTraceNo="+SysTraceNo);
                        TotalCancelRefund = String.valueOf(Double.parseDouble(TransAmt) + Double.parseDouble(TotalCancelRefund));

                        System.out.println("insert_AuthLog");
                        //20220210 ADD AUTH_SRC_CODE
                        if (!UserBean.insert_AuthLog(sysBean, MerchantID,
                                SubMID, TerminalID, AcquirerID, OrderID, Sys_OrderID,
                                Card_Type, PAN, ExtenNo, ExpireDate, TransCode,
                                ReversalFlag, TransDate, TransTime, CurrencyCode,
                                TransAmt, TransStatus, ApproveCode, ResponseCode,
                                ResponseMsg, Entry_Mode, Condition_Code, BatchNo,
                                UserDefine, Direction, EMail, MTI, RRN, SocialID, TransMode,
                                TransType, ECI, CAVV, XID, InstallType, Install, FirstAmt,
                                EachAmt, FEE, RedemType, RedemUsed, RedemBalance,
                                CreditAmt, BillMessage, SysTraceNo,AUTH_SRC_CODE))
                        {
                            CommitFlag = false;
                        }

                        System.out.println("insert_Billing");
                        //20220210 ADD AUTH_SRC_CODE
                        if (!UserBean.insert_Billing(sysBean, MerchantID, SubMID, TerminalID, AcquirerID,
                                OrderID, Sys_OrderID, Card_Type, PAN, ExtenNo,
                                ExpireDate, TransCode, ReversalFlag, TransDate, TransTime, CurrencyCode,
                                TransAmt, ApproveCode, ResponseCode, ResponseMsg,
                                BatchNo, UserDefine, EMail, MTI, RRN, SocialID, Entry_Mode,
                                Condition_Code, TransMode, TransType, ECI, CAVV, XID,
                                InstallType, Install, FirstAmt, EachAmt, FEE, RedemType,
                                RedemUsed, RedemBalance, CreditAmt, BillMessage,
                                BillBalanceAmt, SysTraceNo,AUTH_SRC_CODE))
                        {
                            CommitFlag = false;
                        }

                        System.out.println("update_Billing");
                        if (!MerchantVoidBean.update_BillingeVoid(sysBean, MerchantID, SubMID, OldSys_OrderID, "01"))
                        {
                            CommitFlag = false;
                        }

                        System.out.println("insert_SAF");
                        if (!UserBean.insert_SAF(sysBean, MerchantID, SubMID,
                                                 TerminalID, AcquirerID, OrderID,
                                                 Sys_OrderID, Card_Type, PAN,
                                                 ExtenNo, ExpireDate, TransCode,
                                                 ReversalFlag, TransDate, TransTime,
                                                 CurrencyCode, TransAmt, ApproveCode, ResponseCode,
                                                 ResponseMsg, Entry_Mode, Condition_Code, BatchNo,
                                                 UserDefine, EMail, MTI, RRN, SocialID, TransMode, TransType,
                                                 ECI, CAVV, XID, InstallType, Install, FirstAmt, EachAmt,
                                                 FEE, RedemType, RedemUsed, RedemBalance,
                                                 CreditAmt, "REQ", "", "", "", "", "", SysTraceNo))
                        {
                            CommitFlag = false;
                        }

                        if (CommitFlag)
                        {
                            Message += "�u�W�h�f�����@�~���\";
                        }
                        else
                        {
                            Message += "�u�W�h�f�����@�~����";
                        }
                    }
                    else
                    {
                        Message += "�d�L�h�f������";
                    }
                }

                if (CommitFlag)
                {
                    System.out.println("update_Balance");
                    String RefundAmt = hashBalanceData.get("REFUNDAMT").toString();
                    RefundAmt = String.valueOf(Double.parseDouble(RefundAmt) - Double.parseDouble(TotalCancelRefund));
                    String RefundDate = TmpTransDate.substring(0, 4) + "/" + TmpTransDate.substring(4, 6) + "/" +
                                        TmpTransDate.substring(6, 8) + " " + TmpTransDate.substring(8, 10) + ":" +
                                        TmpTransDate.substring(10, 12) + ":" + TmpTransDate.substring(12, 14);
                    String BalanceAmt = hashBalanceData.get("BALANCEAMT").toString();
                    BalanceAmt = String.valueOf(Double.parseDouble(BalanceAmt) + Double.parseDouble(TotalCancelRefund));
                    System.out.println("RefundAmt=" + RefundAmt + ",BalanceAmt=" + BalanceAmt + ",TotalCancelRefund=" + TotalCancelRefund);

                    if (!UserBean.update_Balance(sysBean, MerchantID, SubMID, OrderID, RefundAmt, RefundDate, "", "", "", "", "", "", BalanceAmt))
                    {
                        CommitFlag = false;
                    }
                    CommitFlag = sysBean.commit();
                }
                else
                {
                	sysBean.setRollBack();
                }
                //SysBean.close();
            }

            if (!CommitFlag)
            {
                Message = "�u�W�h�f�����@�~����";
            }
            else
            {
                Message = "�u�W�h�f�����@�~���\";
            }
           // SysBean.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log_systeminfo.debug("--MerchantVoidCtl--"+e.toString());
        }
    }

    /**
     * �дڨ��� Capture_Void
     * @param String  MerchantID        �S���N��
     * @param Hashtable hashCaptureVoid ���w�������дڸ��
     */
    private void Capture_Void(String MerchantID, Hashtable hashCaptureVoid)
    {
        try
        {
            boolean CommitFlag = true;
            Hashtable hashBalanceData = new Hashtable();
//            DataBaseBean DataBaseUpdateBean = new DataBaseBean();
//            DataBasepdateBean.setAutoCommit(false);
//            UserBean UserBean = new UserBean();
            MerchantVoidBean MerchantVoidBean = new MerchantVoidBean();
            String TmpTransDate = UserBean.get_TransDate("yyyyMMddHHmmss");
            String TmpInsertTransDate = UserBean.get_TransDate("yyyy/MM/dd HH:mm:ss");
            String TotalCancelCapture = "0"; // �ʳf�дڨ����`���B
            String TotalCancelRefCapture = "0"; // �h�f�дڨ����`���B
            int CheckCount = 0;
            String OrderID = "";
            String SubMID = "";

            for (int i = 0; i < hashCaptureVoid.size(); ++i)
            {
                Hashtable hashCapture = (Hashtable) hashCaptureVoid.get(String.valueOf(i));
                String RowID = hashCapture.get("RROWID").toString();
                String TransCode = hashCapture.get("TRANSCODE").toString();

                if (TransCode.equalsIgnoreCase("00"))
                    TransCode = "20";

                if (TransCode.equalsIgnoreCase("01"))
                    TransCode = "21";

                String TransDate = TmpTransDate.substring(0, 8);
                String TransTime = TmpTransDate.substring(8, 14);
                OrderID = hashCapture.get("ORDERID").toString();
                System.out.println("-------- OrderID= " + OrderID);
                System.out.println("MerchantID=" + MerchantID + ",SubMID=" + SubMID + ",OrderID=" + OrderID);
                System.out.println("hashBalanceData=" + hashBalanceData);
                SubMID = hashCapture.get("SUBMID").toString();
                String Sys_OrderID = hashCapture.get("SYS_ORDERID").toString();
                String TerminalID = hashCapture.get("TERMINALID").toString();
                String TransAmt = hashCapture.get("TRANSAMT").toString();
                String CaptureAmt = hashCapture.get("CAPTUREAMT").toString();
                String CaptureDate = hashCapture.get("CAPTUREDATE").toString();
                String AcquirerID = hashCapture.get("ACQUIRERID").toString();
                String Card_Type = hashCapture.get("CARD_TYPE").toString();
                String PAN = hashCapture.get("PAN").toString();
                String ExpireDate = hashCapture.get("EXPIREDATE").toString();
                String CurrencyCode = hashCapture.get("CURRENCYCODE").toString();
                String ApproveCode = hashCapture.get("APPROVECODE").toString();
                String ResponseCode = hashCapture.get("RESPONSECODE").toString();
                String ResponseMsg = hashCapture.get("RESPONSEMSG").toString();
                String CaptureFlag = "2"; // �v����
                String ProcessDate = TmpInsertTransDate;
                String Entry_Mode = hashCapture.get("ENTRY_MODE").toString();
                String Condition_Code = hashCapture.get("CONDITION_CODE").toString();
                String BatchNo = hashCapture.get("BATCHNO").toString();
                String UserDefine = hashCapture.get("USERDEFINE").toString();
                String TransMode = hashCapture.get("TRANSMODE").toString();
                String ECI = hashCapture.get("ECI").toString();
                String CAVV = hashCapture.get("CAVV").toString();
                String InstallType = hashCapture.get("INSTALLTYPE").toString();
                String Install = hashCapture.get("INSTALL").toString();
                String FirstAmt = hashCapture.get("FIRSTAMT").toString();
                String EachAmt = hashCapture.get("EACHAMT").toString();
                String FEE = hashCapture.get("FEE").toString();
                String RedemType = hashCapture.get("REDEMTYPE").toString();
                String RedemUsed = hashCapture.get("REDEMUSED").toString();
                String RedemBalance = hashCapture.get("REDEMBALANCE").toString();
                String CreditAmt = hashCapture.get("CREDITAMT").toString();
                String BillMessage = hashCapture.get("BILLMESSAGE").toString();
                String DueDate = hashCapture.get("DUE_DATE").toString();
                String SysTraceNo = hashCapture.get("SYSTRACENO").toString();
                String FeeBackCode = "CAN";
                String FeeBackMsg = "�w����";
                String FeeBackDate = TmpInsertTransDate;
                String tmpTransCode = hashCapture.get("TRANSCODE").toString();
                String ExtenNo = hashCapture.get("EXTENNO").toString();
                String RRN = hashCapture.get("RRN").toString();
                String MTI = hashCapture.get("MTI").toString();
                String XID = hashCapture.get("XID").toString();
                String SocialID =  hashCapture.get("SOCIALID").toString();
                String ReauthFlag =  hashCapture.get("REAUTH_FLAG").toString();
                String ExceptFlag =  hashCapture.get("EXCEPT_FLAG").toString();
                String AUTH_SRC_CODE =  hashCapture.get("AUTH_SRC_CODE").toString();

                if (tmpTransCode.equalsIgnoreCase("00"))
                {
                    TotalCancelCapture = String.valueOf(Double.parseDouble(TotalCancelCapture) + Double.parseDouble(CaptureAmt));
                }

                if (tmpTransCode.equalsIgnoreCase("01"))
                {
                    TotalCancelRefCapture = String.valueOf(Double.parseDouble(TotalCancelRefCapture) + Double.parseDouble(CaptureAmt));
                }

                System.out.println("update_Capture");
                if (!MerchantVoidBean.update_CaptureVoid(sysBean, RowID, CaptureFlag, FeeBackCode, FeeBackMsg, TmpInsertTransDate))
                {
                    // ��s��дڸ��
                    CommitFlag = false;
                }

                System.out.println("insert_Capture");
                if (!UserBean.insert_Capture(sysBean, MerchantID, SubMID, TerminalID, AcquirerID,
                                             OrderID, Sys_OrderID, Card_Type, PAN, ExpireDate,
                                             TransCode, TransDate, TransTime, ApproveCode,
                                             ResponseCode, ResponseMsg, CurrencyCode, CaptureAmt,
                                             CaptureDate, UserDefine, BatchNo, CaptureFlag,
                                             ProcessDate, Entry_Mode, Condition_Code, ECI, CAVV,
                                             TransMode, InstallType, Install, FirstAmt, EachAmt,
                                             FEE, RedemType, RedemUsed, RedemBalance, CreditAmt,
                                             BillMessage, FeeBackCode, FeeBackMsg, FeeBackDate,
                                             DueDate, TransAmt, SysTraceNo,ExtenNo,RRN,MTI,XID,SocialID,ReauthFlag, ExceptFlag, AUTH_SRC_CODE))//20220210 
                { 
                    //  �s�W�дڸ��
                    CommitFlag = false;
                }
                
                if (!MerchantVoidBean.update_BillingeCaptureVoid(sysBean, MerchantID, SubMID, Sys_OrderID, CaptureAmt)) 
                { 
                    // ��s�^billing��balanceAmt
                    CommitFlag = false;
                }
            }

            if (CommitFlag)
            {
                System.out.println("get_Balance");
                hashBalanceData = UserBean.get_Balance(sysBean,MerchantID, SubMID, OrderID);
                System.out.println("update_Balance");
                String RefundCaptureAmt = hashBalanceData.get("REFUNDCAPTUREAMT").toString();
                RefundCaptureAmt = String.valueOf(Double.parseDouble(RefundCaptureAmt) - Double.parseDouble(TotalCancelRefCapture));
                String CaptureAmt = hashBalanceData.get("CAPTUREAMT").toString();
                CaptureAmt = String.valueOf(Double.parseDouble(CaptureAmt) - Double.parseDouble(TotalCancelCapture));
                String BalanceAmt = hashBalanceData.get("BALANCEAMT").toString();
                BalanceAmt = String.valueOf(Double.parseDouble(BalanceAmt) + Double.parseDouble(TotalCancelCapture));
                System.out.println("CaptureAmt=" + CaptureAmt + ",BalanceAmt=" + BalanceAmt + ",TotalCancelRefund=" + TotalCancelCapture + ",RefundCaptureAmt=" + RefundCaptureAmt);
                if (!UserBean.update_Balance(sysBean, MerchantID, SubMID, OrderID, "", "", CaptureAmt, TmpInsertTransDate, "", "", RefundCaptureAmt, TmpInsertTransDate, BalanceAmt))
                {
                    CommitFlag = false;
                }

                CommitFlag = sysBean.commit();
            }
            else
            {
            	sysBean.setRollBack();
            }

          //  SysBean.close();
            if (!CommitFlag)
            {
                Message = "�u�W�дڨ����@�~����";
            }
            else
            {
                Message = "�u�W�дڨ����@�~���\";
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log_systeminfo.debug("--MerchantVoidCtl--" + e.toString());
        }
    }
}
