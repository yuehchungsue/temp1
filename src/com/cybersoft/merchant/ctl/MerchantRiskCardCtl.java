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
 * <p>控制風險卡的Servlet</p>
 * @version 0.1 2008/02/27  Shiley Lin
 */
public class MerchantRiskCardCtl extends HttpServlet
{
    private static final String CONTENT_TYPE = "text/html; charset=Big5";
    private String Forward = "./Merchant_Response.jsp"; // 網頁轉址
    private String Message = ""; // 顯示訊息
    private Hashtable hashSys = new Hashtable(); // 系統參數
    private Hashtable hashMerUser = new Hashtable(); // 特店使用者
    private Hashtable hashMerchant = new Hashtable(); // 特店主檔
    private ArrayList arrayTerminal = new ArrayList(); // 端末機主檔
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
    String LogFunctionName = "風險卡維護作業";
    String LogStatus = "失敗";
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
                hashSys = (Hashtable) hashConfData.get("SYSCONF"); // 系統參數
                hashMerUser = (Hashtable) hashConfData.get("MERCHANT_USER"); // 特店使用者
                hashMerchant = (Hashtable) hashConfData.get("MERCHANT"); // 特店主檔
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
            boolean Merchant_Current = UserBean.check_Merchant_Column(hashMerchant, "CURRENTCODE", "B,C,D"); //  確認特店狀態
            boolean Merchant_Permit = UserBean.check_Merchant_Column(hashMerchant, "PERMIT_RISK_CARD", "Y"); //  確認特店權限
            String MenuKey = (String) request.getParameter(MerchantMenuCtl.MENU_FORWARD_KEY);
            request.setAttribute(MerchantMenuCtl.MENU_FORWARD_KEY, MenuKey);
            boolean User_Permit = MerchantMenuCtl.getUserRole(sysBean, session, MenuKey);

            if (User_Permit)
            {
                // 使用者權限
                if (Merchant_Current)
                {
                    //特店狀態
                    if (Merchant_Permit)
                    {
                        // 特店權限
                        if (Action.length() == 0)
                        {
                            //直接轉到查詢畫頁
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
                                //查詢請款列表
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

                                hashData.put("DATALIST", arrayData); // 查詢請款資料
                                hashData.put("QUERY", hashQuery); // 查詢條件
                                hashData.put("NOWPAGE", page_no); // 查詢頁次
                                if (session.getAttribute("RiskCardData") != null)
                                {
                                    session.removeAttribute("RiskCardData");
                                }

                                session.setAttribute("RiskCardData", hashData);
                                LogMemo = "風險卡資料列表共"+String.valueOf(arrayData.size())+"筆";
                                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "成功", LogMemo);
                                log_user.debug(LogData);
                                Forward = "./Merchant_RiskCard_List.jsp";
                            }

                            if (Action.equalsIgnoreCase("Add"))
                            {
                                //新增畫頁
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
                                    session.setAttribute("Message","查無交易資料");
                                    Forward = "./Merchant_RiskCard_Query.jsp";
                                }
                            }
                            if (Action.equalsIgnoreCase("toAdd"))
                            {
                                //新增執行畫頁
                                MerchantRiskCardBean RiskCardBean = new MerchantRiskCardBean();
                                Hashtable hashData = new Hashtable();
                                ArrayList arrayUpdateList = new ArrayList();
                                if (session.getAttribute("AddRiskData") != null)
                                {
                                   hashData = (Hashtable) session.getAttribute("AddRiskData");
                                }

                                if (hashData.size()> 0)
                                {
                                    boolean UpdateFlag = false; // 新增.維護結果
                                    boolean InsertFlag = true; // 資料是否新增
                                    String Pan = hashData.get("PAN").toString().trim();
                                    String OrderID = hashData.get("ORDERID").toString();
                                    String Risk_Degree = hashData.get("Risk_Degree").toString();
                                    Hashtable hashRisk = RiskCardBean.get_RiskCard_Hash(sysBean, MerchantID,SubMID, Pan);   // 原風險卡
                                    if ( hashRisk.size() > 0)
                                    {
                                         // 查詢卡號是否已存於風險卡檔
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
                                        // 新增
                                        UpdateFlag = RiskCardBean.insert_RiskCard(sysBean, MerchantID, SubMID,
                                                            Pan, OrderID, Risk_Degree, Userid, TmpTransDate);
                                        MerInsUser = Userid;
                                        MerInsDate = TmpTransDate;
                                    }
                                    else
                                    {
                                        // 維護
                                        UpdateFlag = RiskCardBean.update_RiskCard(sysBean, MerchantID, SubMID, Pan,
                                                        OrderID, Risk_Degree, Userid, TmpTransDate);
                                        MerInsUser = hashRisk.get("MER_INS_USER").toString();
                                        MerInsDate = hashRisk.get("MER_INS_DATE").toString();
                                        MerUpdUser = Userid;
                                        MerUpdDate = TmpTransDate;
                                    }

                                    if (UpdateFlag)
                                    {
                                        //新增維護成功
                                        Status = "成功";
                                    }
                                    else
                                    {
                                        //新增維護失敗
                                        Status = "失敗";
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
                                    LogMemo = "風險卡新增資料"+Status;
                                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName,Status , LogMemo);
                                    log_user.debug(LogData);
                                }

                                session.setAttribute("RiskCardUpdCheckData",arrayUpdateList);
                                Forward = "./Merchant_RiskCard_Update_List.jsp";
                            }

                            if (Action.equalsIgnoreCase("Check"))
                            {
                                //查詢請款選取資料
                                String InputRisk_Degree = (String) request.getParameter("InputRisk_Degree"); // 風險等級
                                String []arrayRisk_Degree = InputRisk_Degree.split(",");
                                String InputOrderID = (String) request.getParameter("InputOrderID"); // 特店指定單號
                                String InputPan = (String) request.getParameter("InputPan");  // 卡號
                                String InputMerInsUser = (String) request.getParameter("InputMerInsUser");  // 特店新增人員
                                String InputMerUpdUser = (String) request.getParameter("InputMerUpdUser");  // 特店維護人員
                                String InputMerInsDate = (String) request.getParameter("InputMerInsDate");  // 特店新增日期
                                String InputMerUpdDate = (String) request.getParameter("InputMerUpdDate");  // 特店維護日期

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
                                LogMemo = "風險卡資料勾選共"+String.valueOf(arrayRisk_Degree.length)+"筆";
                                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "成功", LogMemo);
                                log_user.debug(LogData);
                                Forward = "./Merchant_RiskCard_Check_List.jsp";
                            }

                            if (Action.equalsIgnoreCase("Risk"))
                            {
                                //執行請款作業
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
                                String InputRisk_Degree = ""; // 風險等級
                                String InputOrderID = ""; // 特店指定單號
                                String InputPan = "";  // 卡號
                                String InputMerInsUser = "";  // 特店新增人員
                                String InputMerUpdUser = "";  // 特店維護人員
                                String InputMerInsDate = "";  // 特店新增日期
                                String InputMerUpdDate = "";  // 特店維護日期
                                if (hashRiskCard.size()>0)
                                {
                                    InputRisk_Degree = hashRiskCard.get("InputRisk_Degree").toString(); // 風險等級
                                    if (InputRisk_Degree == null)
                                        InputRisk_Degree = "";

                                    String []arrayRisk_Degree = InputRisk_Degree.split(",");
                                    InputOrderID = hashRiskCard.get("InputOrderID").toString(); // 特店指定單號
                                    if (InputOrderID == null)
                                        InputOrderID = "";

                                    String []arrayInputOrderID = InputOrderID.split(",");
                                    InputPan = hashRiskCard.get("InputPan").toString();  // 卡號
                                    if (InputPan == null)
                                        InputPan = "";

                                    String []arrayInputPan = InputPan.split(",");
                                    InputMerInsUser = hashRiskCard.get("InputMerInsUser").toString();  // 特店新增人員
                                    if (InputMerInsUser == null)
                                        InputMerInsUser = "";

                                    String []arrayInputMerInsUser = InputMerInsUser.split(",");
                                    InputMerUpdUser = hashRiskCard.get("InputMerUpdUser").toString();  // 特店維護人員
                                    if (InputMerUpdUser == null)
                                        InputMerUpdUser = "";

                                    String []arrayInputMerUpdUser = InputMerUpdUser.split(",");
                                    InputMerInsDate = hashRiskCard.get("InputMerInsDate").toString();  // 特店新增日期
                                    if (InputMerInsDate == null)
                                        InputMerInsDate = "";

                                    String []arrayInputMerInsDate = InputMerInsDate.split(",");
                                    InputMerUpdDate = hashRiskCard.get("InputMerUpdDate").toString();  // 特店維護日期
                                    if (InputMerUpdDate == null)
                                        InputMerUpdDate = "";

                                    String []arrayInputMerUpdDate = InputMerUpdDate.split(",");
                                    String Userid = hashMerUser.get("USER_ID").toString();
                                    ArrayList arrayUpdateList = new ArrayList();
                                    MerchantRiskCardBean RiskCardBean = new MerchantRiskCardBean();

                                    for (int c=0; c<arrayInputOrderID.length;++c)
                                    {
                                        boolean UpdateFlag = false; // 新增.維護結果
                                        boolean InsertFlag = true;  // 資料是否新增
                                        if (RiskCardBean.get_RiskCard_Count(sysBean, MerchantID, SubMID,arrayInputPan[c])>0)
                                        { // 查詢卡號是否已存於風險卡檔
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
                                            // 新增
                                            UpdateFlag = RiskCardBean.insert_RiskCard(sysBean, MerchantID, SubMID, arrayInputPan[c], arrayInputOrderID[c], arrayRisk_Degree[c], Userid, TmpTransDate);
                                            MerInsUser = Userid;
                                            MerInsDate = TmpTransDate;
                                        }
                                        else
                                        {
                                            // 維護
                                            UpdateFlag = RiskCardBean.update_RiskCard(sysBean, MerchantID, SubMID, arrayInputPan[c], arrayInputOrderID[c], arrayRisk_Degree[c], Userid, TmpTransDate);
                                            MerInsUser = arrayInputMerInsUser[c];
                                            MerInsDate = arrayInputMerInsDate[c];
                                            MerUpdUser = Userid;
                                            MerUpdDate = TmpTransDate;
                                        }

                                        if (UpdateFlag)
                                        {
                                            //新增維護成功
                                            SuccessCnt++;
                                            Status = "成功";
                                        }
                                        else
                                        {
                                            //新增維護失敗
                                            if (!InsertFlag)
                                            {   // 新增
                                                MerUpdUser = arrayInputMerUpdUser[c];
                                                MerUpdDate = arrayInputMerUpdDate[c];
                                            }
                                            Status = "失敗";
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
                                LogMemo = "風險卡維護資料成功"+String.valueOf(SuccessCnt)+"筆，失敗"+String.valueOf(FailCnt)+"筆";
                                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "成功", LogMemo);
                                log_user.debug(LogData);
                            }

                            if (Action.equalsIgnoreCase("Print"))
                            {
                                // 匯出TXT
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
                                    LogMemo = "以"+PrintType+"格式匯出請款資料";
                                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "成功", LogMemo);
                                    log_user.debug(LogData);
                                }
                            }

                        }
                    }
                    else
                    {
                        Message = "特店無此功能權限";
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
                Message = "特約商店/使用者無此權限請洽本行處理";
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
        //20130702 Jason 增加finally處理sysBean.close
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
