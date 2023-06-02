/************************************************************
 * <p>#File Name:   MerchantRiskCardCtl.java        </p>
 * <p>#Description:                         </p>
 * <p>#Create Date: 2008/02/27              </p>
 * <p>#Company:     cybersoft               </p>
 * <p>#Notice:                      </p>
 * @author      Shirley Lin
 * @since       SPEC version
 * @version 0.1 2008/02/27  Shirley Lin
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
import com.cybersoft.merchant.bean.MerchantRiskCardBean;
import com.fubon.security.filter.SecurityTool;

import org.w3c.util.*;
import java.sql.ResultSet;
import com.cybersoft.bean.createReport;
import com.cybersoft.bean.LogUtils;
/**
 * <p>����I�d��Servlet</p>
 * @version 0.1 2008/02/27  Shiley Lin
 */
public class MerchantRiskCardCtl extends HttpServlet
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
    private String Success_SysOrderID = "";
    private String Fail_SysOrderID = "";
    private String BatchPmtID = "";
    java.util.Date nowdate;
    public static final LogUtils log_user = new LogUtils("user");
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    public UserBean UserBean = new UserBean();
    public DataBaseBean2 sysBean = new DataBaseBean2();

    String LogUserName = "";
    String LogFunctionName = "���I�d���@�@�~";
    String LogStatus = "����";
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
            boolean ForwardFlag = true;
            hashConfData = (Hashtable) session.getAttribute("SYSCONFDATA");
            if (hashConfData == null)
                hashConfData = new Hashtable();

            String Action = (String) request.getParameter("Action");
            if (Action == null)
                Action = "";

            if (hashConfData.size() > 0)
            {
                hashSys = (Hashtable) hashConfData.get("SYSCONF"); // �t�ΰѼ�
                hashMerUser = (Hashtable) hashConfData.get("MERCHANT_USER"); // �S���ϥΪ�
                hashMerchant = (Hashtable) hashConfData.get("MERCHANT"); // �S���D��
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
            }

            // UserBean UserBean = new UserBean();
            boolean Merchant_Current = UserBean.check_Merchant_Column(hashMerchant, "CURRENTCODE", "B,C,D"); //  �T�{�S�����A
            boolean Merchant_Permit = UserBean.check_Merchant_Column(hashMerchant, "PERMIT_RISK_CARD", "Y"); //  �T�{�S���v��
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
                        if (Action.length() == 0)
                        {
                            //�������d�ߵe��
                            if (session.getAttribute("RiskCardData") != null)
                            {
                                session.removeAttribute("RiskCardData");
                            }

                            ArrayList arrayMsgcode = UserBean.get_Msgcode(sysBean, "RISKCARD");
                            request.setAttribute("RiskCardMsgCode", arrayMsgcode);
                            if (session.getAttribute("RiskCardMsgCode") == null)
                            {
                                session.setAttribute("RiskCardMsgCode", arrayMsgcode);
                            }

                            if (session.getAttribute("Message")  != null)
                            {
                                session.removeAttribute("Message");
                            }

                            Forward = "./Merchant_RiskCard_Query.jsp";
                        }
                        else
                        {
                            String MerchantID = hashMerchant.get("MERCHANTID").toString();
                            String SubMID = hashMerchant.get("SUBMID").toString();
                            if (MerchantID == null)
                                MerchantID = "";

                            if (SubMID == null)
                                SubMID = "";

                            System.out.println("Action=" + Action);
                            if (Action.equalsIgnoreCase("Query") || Action.equalsIgnoreCase("FirstQuery"))
                            {
                                //�d�߽дڦC��
                                String OrderID = "";
                                String Risk_Degree = "";
                                String page_no = UserBean.trim_Data(request.getParameter("page_no"));
                                if (page_no == null)
                                    page_no = "0";

                                Hashtable hashQuery = new Hashtable();
                                if (session.getAttribute("RiskCardData") != null && Action.equalsIgnoreCase("Query"))
                                {
                                    Hashtable hashAll = (Hashtable) session.getAttribute("RiskCardData");
                                    hashQuery = (Hashtable) hashAll.get("QUERY");
                                    OrderID = (String) hashQuery.get("OrderID");
                                    Risk_Degree = (String) hashQuery.get("Risk_Degree");
                                }
                                else
                                {
                                    OrderID = request.getParameter("OrderID");
                                    if (OrderID == null)
                                        OrderID = "";

                                    OrderID = UserBean.trim_Data(OrderID);
                                    hashQuery.put("OrderID", OrderID);
                                    Risk_Degree = request.getParameter("Risk_Degree");
                                    if (Risk_Degree == null)
                                        Risk_Degree = "";

                                    Risk_Degree = UserBean.trim_Data(Risk_Degree);
                                    hashQuery.put("Risk_Degree", Risk_Degree);
                                }

                                String MaxCnt = "500";
                                if (hashSys.get("MER_RISKCARD_CNT") !=null)
                                {
                                    MaxCnt =hashSys.get("MER_RISKCARD_CNT").toString();
                                }

                                MerchantRiskCardBean RiskCardBean = new MerchantRiskCardBean();
                                arrayData = RiskCardBean.get_RiskCard_List(sysBean, MerchantID, SubMID, Risk_Degree, OrderID, MaxCnt);
                                if (arrayData == null)
                                    arrayData = new ArrayList();

                                hashData.put("DATALIST", arrayData); // �d�߽дڸ��
                                hashData.put("QUERY", hashQuery); // �d�߱���
                                hashData.put("NOWPAGE", page_no); // �d�߭���
                                if (session.getAttribute("RiskCardData") != null)
                                {
                                    session.removeAttribute("RiskCardData");
                                }

                                session.setAttribute("RiskCardData", hashData);
                                LogMemo = "���I�d��ƦC��@"+String.valueOf(arrayData.size())+"��";
                                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "���\", LogMemo);
                                log_user.debug(LogData);
                                Forward = "./Merchant_RiskCard_List.jsp";
                            }

                            if (Action.equalsIgnoreCase("Add"))
                            {
                                //�s�W�e��
                                String OrderID = (String)request.getParameter("OrderID");
                                if ( OrderID == null )
                                    OrderID = "";

                                String Risk_Degree = (String)request.getParameter("Risk_Degree");
                                if ( Risk_Degree == null ) Risk_Degree = "";
                                MerchantRiskCardBean RiskCardBean = new MerchantRiskCardBean();
                                Hashtable hashData = RiskCardBean.get_Billing_List(sysBean, MerchantID, SubMID, OrderID);
                                if (session.getAttribute("AddRiskData")==null)
                                {
                                    session.removeAttribute("AddRiskData");
                                }

                                if (hashData.size()>0)
                                {
                                    hashData.put("Risk_Degree", Risk_Degree);
                                    session.setAttribute("AddRiskData",hashData);
                                    Forward = "./Merchant_RiskCard_Add.jsp";
                                }
                                else
                                {
                                    session.setAttribute("Message","�d�L������");
                                    Forward = "./Merchant_RiskCard_Query.jsp";
                                }
                            }
                            if (Action.equalsIgnoreCase("toAdd"))
                            {
                                //�s�W����e��
                                MerchantRiskCardBean RiskCardBean = new MerchantRiskCardBean();
                                Hashtable hashData = new Hashtable();
                                ArrayList arrayUpdateList = new ArrayList();
                                if (session.getAttribute("AddRiskData") != null)
                                {
                                   hashData = (Hashtable) session.getAttribute("AddRiskData");
                                }

                                if (hashData.size()> 0)
                                {
                                    boolean UpdateFlag = false; // �s�W.���@���G
                                    boolean InsertFlag = true; // ��ƬO�_�s�W
                                    String Pan = hashData.get("PAN").toString().trim();
                                    String OrderID = hashData.get("ORDERID").toString();
                                    String Risk_Degree = hashData.get("Risk_Degree").toString();
                                    Hashtable hashRisk = RiskCardBean.get_RiskCard_Hash(sysBean, MerchantID,SubMID, Pan);   // �쭷�I�d
                                    if ( hashRisk.size() > 0)
                                    {
                                         // �d�ߥd���O�_�w�s���I�d��
                                        InsertFlag = false;
                                    }

                                    String MerInsUser = "";
                                    String MerInsDate = "";
                                    String MerUpdUser = "";
                                    String MerUpdDate = "";
                                    String TmpTransDate = UserBean.get_TransDate("yyyy/MM/dd HH:mm:ss");
                                    String Status = "";
                                    String Userid = hashMerUser.get("USER_ID").toString();

                                    if (InsertFlag)
                                    {
                                        // �s�W
                                        UpdateFlag = RiskCardBean.insert_RiskCard(sysBean, MerchantID, SubMID,
                                                            Pan, OrderID, Risk_Degree, Userid, TmpTransDate);
                                        MerInsUser = Userid;
                                        MerInsDate = TmpTransDate;
                                    }
                                    else
                                    {
                                        // ���@
                                        UpdateFlag = RiskCardBean.update_RiskCard(sysBean, MerchantID, SubMID, Pan,
                                                        OrderID, Risk_Degree, Userid, TmpTransDate);
                                        MerInsUser = hashRisk.get("MER_INS_USER").toString();
                                        MerInsDate = hashRisk.get("MER_INS_DATE").toString();
                                        MerUpdUser = Userid;
                                        MerUpdDate = TmpTransDate;
                                    }

                                    if (UpdateFlag)
                                    {
                                        //�s�W���@���\
                                        Status = "���\";
                                    }
                                    else
                                    {
                                        //�s�W���@����
                                        Status = "����";
                                    }

                                    Hashtable hashUpdateData = new Hashtable();
                                    hashUpdateData.put("ORDERID", OrderID);
                                    hashUpdateData.put("PAN", Pan);
                                    hashUpdateData.put("RISK_DEGREE",Risk_Degree);
                                    hashUpdateData.put("MER_INS_USER",MerInsUser);
                                    hashUpdateData.put("MER_INS_DATE",MerInsDate);
                                    hashUpdateData.put("MER_UPD_USER",MerUpdUser);
                                    hashUpdateData.put("MER_UPD_DATE",MerUpdDate);
                                    hashUpdateData.put("STATUS", Status);
                                    arrayUpdateList.add(hashUpdateData);
                                    LogMemo = "���I�d�s�W���"+Status;
                                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName,Status , LogMemo);
                                    log_user.debug(LogData);
                                }

                                session.setAttribute("RiskCardUpdCheckData",arrayUpdateList);
                                Forward = "./Merchant_RiskCard_Update_List.jsp";
                            }

                            if (Action.equalsIgnoreCase("Check"))
                            {
                                //�d�߽дڿ�����
                                String InputRisk_Degree = (String) request.getParameter("InputRisk_Degree"); // ���I����
                                String []arrayRisk_Degree = InputRisk_Degree.split(",");
                                String InputOrderID = (String) request.getParameter("InputOrderID"); // �S�����w�渹
                                String InputPan = (String) request.getParameter("InputPan");  // �d��
                                String InputMerInsUser = (String) request.getParameter("InputMerInsUser");  // �S���s�W�H��
                                String InputMerUpdUser = (String) request.getParameter("InputMerUpdUser");  // �S�����@�H��
                                String InputMerInsDate = (String) request.getParameter("InputMerInsDate");  // �S���s�W���
                                String InputMerUpdDate = (String) request.getParameter("InputMerUpdDate");  // �S�����@���

                                if (InputRisk_Degree == null)
                                    InputRisk_Degree = "";

                                if (InputOrderID == null)
                                    InputOrderID = "";

                                if (InputPan == null)
                                    InputPan = "";

                                if (InputMerInsUser == null)
                                    InputMerInsUser = "";

                                if (InputMerUpdUser == null)
                                    InputMerUpdUser = "";

                                if (InputMerInsDate == null)
                                    InputMerInsDate = "";

                                if (InputMerUpdDate == null)
                                    InputMerUpdDate = "";

                                if (session.getAttribute("RiskCardPreCheckData") != null)
                                {
                                    session.removeAttribute("RiskCardPreCheckData");
                                }

                                Hashtable hashData = new Hashtable();
                                hashData.put("InputRisk_Degree",InputRisk_Degree);
                                hashData.put("InputOrderID",InputOrderID);
                                hashData.put("InputPan",InputPan);
                                hashData.put("InputMerInsUser",InputMerInsUser);
                                hashData.put("InputMerUpdUser",InputMerUpdUser);
                                hashData.put("InputMerInsDate",InputMerInsDate);
                                hashData.put("InputMerUpdDate",InputMerUpdDate);
                                session.setAttribute("RiskCardPreCheckData",hashData);
                                LogMemo = "���I�d��ƤĿ�@"+String.valueOf(arrayRisk_Degree.length)+"��";
                                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "���\", LogMemo);
                                log_user.debug(LogData);
                                Forward = "./Merchant_RiskCard_Check_List.jsp";
                            }

                            if (Action.equalsIgnoreCase("Risk"))
                            {
                                //����дڧ@�~
                                Hashtable hashRiskCard = new Hashtable();
                                if (session.getAttribute("RiskCardPreCheckData") != null)
                                {
                                    hashRiskCard = (Hashtable) session.getAttribute("RiskCardPreCheckData");
                                    session.removeAttribute("RiskCardPreCheckData");
                                }

                                if (session.getAttribute("RiskCardUpdCheckData") != null)
                                {
                                    session.removeAttribute("RiskCardUpdCheckData");
                                }

                                int SuccessCnt = 0;
                                int FailCnt = 0;
                                String InputRisk_Degree = ""; // ���I����
                                String InputOrderID = ""; // �S�����w�渹
                                String InputPan = "";  // �d��
                                String InputMerInsUser = "";  // �S���s�W�H��
                                String InputMerUpdUser = "";  // �S�����@�H��
                                String InputMerInsDate = "";  // �S���s�W���
                                String InputMerUpdDate = "";  // �S�����@���
                                if (hashRiskCard.size()>0)
                                {
                                    InputRisk_Degree = hashRiskCard.get("InputRisk_Degree").toString(); // ���I����
                                    if (InputRisk_Degree == null)
                                        InputRisk_Degree = "";

                                    String []arrayRisk_Degree = InputRisk_Degree.split(",");
                                    InputOrderID = hashRiskCard.get("InputOrderID").toString(); // �S�����w�渹
                                    if (InputOrderID == null)
                                        InputOrderID = "";

                                    String []arrayInputOrderID = InputOrderID.split(",");
                                    InputPan = hashRiskCard.get("InputPan").toString();  // �d��
                                    if (InputPan == null)
                                        InputPan = "";

                                    String []arrayInputPan = InputPan.split(",");
                                    InputMerInsUser = hashRiskCard.get("InputMerInsUser").toString();  // �S���s�W�H��
                                    if (InputMerInsUser == null)
                                        InputMerInsUser = "";

                                    String []arrayInputMerInsUser = InputMerInsUser.split(",");
                                    InputMerUpdUser = hashRiskCard.get("InputMerUpdUser").toString();  // �S�����@�H��
                                    if (InputMerUpdUser == null)
                                        InputMerUpdUser = "";

                                    String []arrayInputMerUpdUser = InputMerUpdUser.split(",");
                                    InputMerInsDate = hashRiskCard.get("InputMerInsDate").toString();  // �S���s�W���
                                    if (InputMerInsDate == null)
                                        InputMerInsDate = "";

                                    String []arrayInputMerInsDate = InputMerInsDate.split(",");
                                    InputMerUpdDate = hashRiskCard.get("InputMerUpdDate").toString();  // �S�����@���
                                    if (InputMerUpdDate == null)
                                        InputMerUpdDate = "";

                                    String []arrayInputMerUpdDate = InputMerUpdDate.split(",");
                                    String Userid = hashMerUser.get("USER_ID").toString();
                                    ArrayList arrayUpdateList = new ArrayList();
                                    MerchantRiskCardBean RiskCardBean = new MerchantRiskCardBean();

                                    for (int c=0; c<arrayInputOrderID.length;++c)
                                    {
                                        boolean UpdateFlag = false; // �s�W.���@���G
                                        boolean InsertFlag = true;  // ��ƬO�_�s�W
                                        if (RiskCardBean.get_RiskCard_Count(sysBean, MerchantID, SubMID,arrayInputPan[c])>0)
                                        { // �d�ߥd���O�_�w�s���I�d��
                                            InsertFlag = false;
                                        }

                                        String MerInsUser = "";
                                        String MerInsDate = "";
                                        String MerUpdUser = "";
                                        String MerUpdDate = "";
                                        String TmpTransDate = UserBean.get_TransDate("yyyy/MM/dd HH:mm:ss");
                                        String Status = "";
                                        if (InsertFlag)
                                        {
                                            // �s�W
                                            UpdateFlag = RiskCardBean.insert_RiskCard(sysBean, MerchantID, SubMID, arrayInputPan[c], arrayInputOrderID[c], arrayRisk_Degree[c], Userid, TmpTransDate);
                                            MerInsUser = Userid;
                                            MerInsDate = TmpTransDate;
                                        }
                                        else
                                        {
                                            // ���@
                                            UpdateFlag = RiskCardBean.update_RiskCard(sysBean, MerchantID, SubMID, arrayInputPan[c], arrayInputOrderID[c], arrayRisk_Degree[c], Userid, TmpTransDate);
                                            MerInsUser = arrayInputMerInsUser[c];
                                            MerInsDate = arrayInputMerInsDate[c];
                                            MerUpdUser = Userid;
                                            MerUpdDate = TmpTransDate;
                                        }

                                        if (UpdateFlag)
                                        {
                                            //�s�W���@���\
                                            SuccessCnt++;
                                            Status = "���\";
                                        }
                                        else
                                        {
                                            //�s�W���@����
                                            if (!InsertFlag)
                                            {   // �s�W
                                                MerUpdUser = arrayInputMerUpdUser[c];
                                                MerUpdDate = arrayInputMerUpdDate[c];
                                            }
                                            Status = "����";
                                            FailCnt++;
                                        }

                                        Hashtable hashUpdateData = new Hashtable();
                                        hashUpdateData.put("ORDERID",arrayInputOrderID[c]);
                                        hashUpdateData.put("PAN",arrayInputPan[c]);
                                        hashUpdateData.put("RISK_DEGREE",arrayRisk_Degree[c]);
                                        hashUpdateData.put("MER_INS_USER",MerInsUser);
                                        hashUpdateData.put("MER_INS_DATE",MerInsDate);
                                        hashUpdateData.put("MER_UPD_USER",MerUpdUser);
                                        hashUpdateData.put("MER_UPD_DATE",MerUpdDate);
                                        hashUpdateData.put("STATUS",Status);
                                        arrayUpdateList.add(hashUpdateData);
                                    }

                                    session.setAttribute("RiskCardUpdCheckData",arrayUpdateList);
                                }

                                Forward = "./Merchant_RiskCard_Update_List.jsp";
                                LogMemo = "���I�d���@��Ʀ��\"+String.valueOf(SuccessCnt)+"���A����"+String.valueOf(FailCnt)+"��";
                                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "���\", LogMemo);
                                log_user.debug(LogData);
                            }

                            if (Action.equalsIgnoreCase("Print"))
                            {
                                // �ץXTXT
                                ForwardFlag = false;
                                String OrderID = request.getParameter("OrderID");
                                if (OrderID == null)
                                    OrderID = "";

                                OrderID = UserBean.trim_Data(OrderID);
                                String Risk_Degree = request.getParameter("Risk_Degree");
                                if (Risk_Degree == null)
                                    Risk_Degree = "";

                                Risk_Degree = UserBean.trim_Data(Risk_Degree);
                                String PrintType = (request.getParameter("PrintType") == null) ? "" : UserBean.trim_Data(request.getParameter("PrintType"));

                                if (PrintType.length() > 0)
                                {
                                    boolean RowdataFlag = false ;
                                    MerchantRiskCardBean RiskCardBean = new MerchantRiskCardBean();
                                    String sql = RiskCardBean.get_RiskCard_Result(sysBean, MerchantID, SubMID, Risk_Degree, OrderID, RowdataFlag);
                                    createReport cr = new createReport();
                                    Hashtable field = new Hashtable();
                                    String RPTName = "MerchantRiskCardListReport.rpt";
                                    cr.createPDF(sysBean,SecurityTool.output(sql), request, response,"/Report/" + RPTName, field, PrintType);
                                    RiskCardBean.closeConn();
                                    LogMemo = "�H"+PrintType+"�榡�ץX�дڸ��";
                                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "���\", LogMemo);
                                    log_user.debug(LogData);
                                }
                            }

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
                Message = "�S���ө�/�ϥΪ̵L���v���Ь�����B�z";
                Forward = "./Merchant_NoUse.jsp";
                session.setAttribute("Message", Message);
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
        //20130702 Jason �W�[finally�B�zsysBean.close
        finally{
        try
        {
            sysBean.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log_systeminfo.debug("--MerchantCaptureCtl--"+e.toString());
            request.setAttribute("errMsg", e.toString());
            request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
        }
        }
    }
}
