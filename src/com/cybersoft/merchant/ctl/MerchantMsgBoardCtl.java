/************************************************************
 * <p>#File Name:   MerchantMsgBoardCtl.java        </p>
 * <p>#Description:                         </p>
 * <p>#Create Date: 2008/03/05              </p>
 * <p>#Company:     cybersoft               </p>
 * <p>#Notice:                      </p>
 * @author      Shirley Lin
 * @since       SPEC version
 * @version 0.1 2008/03/05  Shirley Lin
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
import org.w3c.util.*;
import java.sql.ResultSet;
import com.cybersoft.bean.createReport;
import com.cybersoft.merchant.bean.MerchantMsgBoardBean;
import com.cybersoft.bean.LogUtils;

/**
 * <p>公告訊息的Servlet</p>
 * @version 0.1 2008/03/05  Shiley Lin
 */
public class MerchantMsgBoardCtl extends HttpServlet
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
    String LogFunctionName = "公告訊息";
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
     //20130703 Jason 增加 Exception Handle Start
   try{
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

        hashConfData = (Hashtable) session.getAttribute("SYSCONFDATA");
        if (hashConfData == null)
            hashConfData = new Hashtable();

        if (hashConfData.size() > 0)
        {
            hashSys = (Hashtable) hashConfData.get("SYSCONF"); // 系統參數
            hashMerchant = (Hashtable) hashConfData.get("MERCHANT"); // 特店主檔
            hashMerUser = (Hashtable) hashConfData.get("MERCHANT_USER"); // 特店使用者
            if (hashSys == null)
                hashSys = new Hashtable();

            if (hashMerchant == null)
                hashMerchant = new Hashtable();

            if (hashMerUser == null)
                hashMerUser = new Hashtable();
        }

        // UserBean UserBean = new UserBean();
        boolean Merchant_Current = UserBean.check_Merchant_Column(hashMerchant, "CURRENTCODE", "B,C,D"); //  確認特店狀態
        String MenuKey = (String) request.getParameter(MerchantMenuCtl.MENU_FORWARD_KEY);
        request.setAttribute(MerchantMenuCtl.MENU_FORWARD_KEY, MenuKey);
        boolean User_Permit = MerchantMenuCtl.getUserRole(sysBean, session, MenuKey);
        String MsgBoardFlag = "N";

        if (session.getAttribute("MsgBoardFlag") !=null)
        {
            MsgBoardFlag = (String)session.getAttribute("MsgBoardFlag");
        }

        if (User_Permit || MsgBoardFlag.equalsIgnoreCase("Y"))
        {
            // 使用者權限
            if (Merchant_Current)
            {
                //特店狀態
                String MerchantID = hashMerchant.get("MERCHANTID").toString();
                if (MerchantID == null)
                    MerchantID = "";

                String SubMID = hashMerUser.get("SUBMID").toString();
                if (SubMID == null)
                    SubMID = "";

                if (session.getAttribute("MsgBoardData") != null)
                {
                    session.removeAttribute("MsgBoardData");
                }

                MerchantMsgBoardBean MsgBoardBean = new MerchantMsgBoardBean();
                if (hashSys.size() > 0)
                {
                    String Mer_Board_Day = hashSys.get("MER_BOARD_DAY").toString();
                    String Today = UserBean.get_AppointDay_Date("yyyyMMdd", 0);
                    ArrayList arrayCaputreData = MsgBoardBean.get_MsgBoard_Caputre_List(sysBean, MerchantID,SubMID, Today, Mer_Board_Day);  // 請款狀態資料
                    if (arrayCaputreData == null)
                        arrayCaputreData = new ArrayList();

                    String Mer_Capture_Day = hashSys.get("MER_CAPTURE_DAY").toString();
                    String Mer_Board_Dueday = hashSys.get("MER_BOARD_DUEDAY").toString();

                    ArrayList arrayBillingData = MsgBoardBean.get_MsgBoard_Billing_List(sysBean, MerchantID,SubMID, Today, Mer_Capture_Day, Mer_Board_Dueday);
                    if (arrayBillingData == null)
                        arrayBillingData =  new ArrayList();

                    hashData.put("CaputreData",arrayCaputreData);
                    hashData.put("BillingData",arrayBillingData);
                    session.setAttribute("MsgBoardData", hashData);
                    LogMemo = "查詢公告訊息";
                    LogUserName = hashMerUser.get("USER_ID").toString() + "(" + hashMerUser.get("USER_NAME").toString() + ")";
                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "成功", LogMemo);
                    log_user.debug(LogData);
                    Forward = "./Merchant_MsgBoard_List.jsp";
                }
                else
                {
                    Message = "目前暫停公告訊息功能";
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

        if (Message.length() > 0)
        {
            LogMemo = Message;
            LogUserName = hashMerUser.get("USER_ID").toString() + "(" + hashMerUser.get("USER_NAME").toString() + ")";
            LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
            log_user.debug(LogData);
        }

        System.out.println("Forward=" + Forward);
        request.getRequestDispatcher(Forward).forward(request, response);
     }
     catch(Exception e){
         log_systeminfo.debug("--MerchantMsgBoardCtl--"+e.toString());
         request.setAttribute("errMsg",e.toString());
         request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
     }
   //20130703 Jason 增加 Exception Handle End
   //20130703 Jason 增加 Finally Close DB connection
     finally
     {
        try
        {
            sysBean.close();
        }
        catch (Exception e)
        {
            log_systeminfo.debug("--MerchantMsgBoardCtl--"+e.toString());
            request.setAttribute("errMsg",e.toString());
            request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
        }
     }
    }
}
