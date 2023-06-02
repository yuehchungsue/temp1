/************************************************************
 * <p>#File Name:       MerchantUserDataCtl.java    </p>
 * <p>#Description:                 </p>
 * <p>#Create Date:     2007/09/26      </p>
 * <p>#Company:         cybersoft       </p>
 * <p>#Notice:                      </p>
 * @author      Caspar Chen
 * @since       SPEC version
 * @version 0.1 2007/09/26  Caspar Chen
 * 202208090854-01 20220801 HKP PCI-DSS更換密碼雜錯MD5為SHA256，MD5雜錯長度為32，SHA256雜錯長度為64，以此長度判斷是否為MD5，變更後的密碼一律使用SHA256與LOG遮蔽
 ************************************************************/
/**異動說明
 *                 20221124 Frog Jump Co., YC White Scan
 **/
package com.cybersoft.merchant.ctl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

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
import com.cybersoft.common.Util;
import com.cybersoft.merchant.bean.MerchantUserDataBean;
import com.fubon.security.filter.XSSRequestWrapper;
import com.fubon.tp.util.XSSUtils;

/**
 * <p>控制密碼變更的Servlet</p>
 * @version 0.1 2007/09/26  Caspar Chen
 */
public class MerchantUserDataCtl extends HttpServlet
{
    private static final String CONTENT_TYPE = "text/html; charset=Big5";
    private String Forward = ""; // 網頁轉址
    private String Message = ""; // 顯示訊息
    private static String userStatus_Y = "正常"; //使用者狀態
    private static String userStatus_O = "待啟用"; //使用者狀態
    private static String userStatus_N = "密碼鎖定"; //使用者狀態
    private static String userStatus_R = "密碼重置"; //使用者狀態
    private static String userStatus_E = "停用"; //使用者狀態
    private static final String ActionForwardMain = "./Merchant_Main.jsp"; //預設網頁名稱
    private static final String ActionForwardList = "./Merchant_UserDataList.jsp"; //預設網頁名稱
    private static final String ActionForwardAdd = "./Merchant_UserDataAdd.jsp"; //預設網頁名稱
    private static final String ActionForwardUpdate = "./Merchant_UserDataUpdate.jsp"; //預設網頁名稱
    /** private String defPwss = "AAA"; //預設組合初始密碼字串 */
    /** private String expireDate = "60"; //密碼有效期限 */
    /** private String timeOut = "15"; //系統TIME OUT時間 */
    private String strUserDataRoleAction = "UserDataRoleAction"; //新增、修改權限判斷用
    java.util.Date nowdate;
    MerchantUserDataBean MUDBean = new MerchantUserDataBean();
    MerchantChangePwdCtl MerchantChangePwdCtl = new MerchantChangePwdCtl();
    public UserBean UserBean = new UserBean();
    public DataBaseBean2 sysBean = new DataBaseBean2();

    public static final LogUtils log_user = new LogUtils("user");
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");

    public void init()
    {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        this.doPost(request, response);
    }

    private synchronized HttpSession getSession(HttpServletRequest request) {
    	return request.getSession(true);
    }
    
	/**
	 * 20221124 Frog Jump Co., YC White Scan/A04 Insecure Design/Race Condition: Singleton Member Field
	 */    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	/** XSSUtils */
		XSSUtils xssUtils = new XSSUtils(request);
		
    	/** XSSRequestWrapper */
		XSSRequestWrapper xssRequestWrapper = new XSSRequestWrapper(request);
		request = (HttpServletRequest) xssRequestWrapper.getRequest();		
        request.setCharacterEncoding("BIG5");   
        
        /** response */
        response.setContentType(CONTENT_TYPE);
        
		/** HttpSession */
		HttpSession session = getSession(request);
        
        try
        {
        	@SuppressWarnings("unused")
        	SessionControlBean scb = new SessionControlBean();
            scb = new SessionControlBean(session, request, response);
            sysBean.setAutoCommit(false);

        }
        catch (UnsupportedOperationException E)
        {
            E.toString();
            
            /** Need to test */
            xssRequestWrapper.getRequestDispatcher("/Merchant_Bye.jsp").forward(request, response);
            
            return;
        }
       
        try
        {
            // UserBean UserBean = new UserBean();
            Message = ""; // 顯示訊息
            //宣告變數
            Forward = ActionForwardList;
            boolean boolIsEqual = false;
            boolean boolUserAllRole = false;
            boolean boolUserRole = false;
            boolean boolRunUpdate = false;
            String strUserView = "0"; //頁面顯示資料不可修改 0=預設 1=新增
            ArrayList arrayUserData = new ArrayList();
            ArrayList arrayUserAllRoleData = new ArrayList();
            ArrayList arrayUserRoleData = new ArrayList();
            Hashtable hashMerUser = new Hashtable();;
            String strSessionMerchantId = "";
            String strSessionUserId = ""; //登入人員代號
            String strSessionSIGN_BILL ="N";//是否為電簽特店
            String userIdData = ""; //代號
            String userIdList = ""; //代號
            String userName = ""; //名稱
            String depName = ""; //部門
            String subMid="";//子特店代號
            String actionTypeCk = UserBean.trim_Data(request.getParameter("actionTypeCk"));
            String actionType = UserBean.trim_Data(request.getParameter("actionType"));
            String LogUserName = "";
            String LogFunctionName = "使用者管理";
            String LogStatus = "成功";
            String LogMemo = "";
            String LogData = "";
            String LogMerchantID = "";
            String MenuKey = (String) request.getParameter(MerchantMenuCtl.MENU_FORWARD_KEY);
            request.setAttribute(MerchantMenuCtl.MENU_FORWARD_KEY, MenuKey);
            String expireDate = "60";

            if (MerchantMenuCtl.getUserRole(sysBean, session, MenuKey))
            {
                Hashtable hashConfData = (Hashtable) session.getAttribute("SYSCONFDATA");
                //session資料
                if (hashConfData != null && hashConfData.size() > 0)
                {
                     // 使用者資料
                    hashMerUser = (java.util.Hashtable) hashConfData.get("MERCHANT_USER");

                    // 使用者資料
                    if (hashMerUser != null)
                    {
                        if (hashMerUser.get("MERCHANT_ID") != null)
                        {
                            strSessionMerchantId = hashMerUser.get("MERCHANT_ID").toString();
                            LogMerchantID = strSessionMerchantId;
                        }
                        if (hashMerUser.get("USER_ID") != null)
                        {
                            strSessionUserId = hashMerUser.get("USER_ID").toString();
                        }
                        if (hashMerUser.get("SIGN_BILL") != null)
                        {
                            strSessionSIGN_BILL = Util.objToStrTrim(hashMerUser.get("SIGN_BILL"));
                        }
                    }
                }

                if (request.getParameter("userIdData") != null)
                {
                    userIdData = UserBean.trim_Data(request.getParameter("userIdData"));
                }

                if (request.getParameter("userIdList") != null)
                {
                    userIdList = UserBean.trim_Data(request.getParameter("userIdList"));
                }

                if (request.getParameter("userName") != null)
                {
                    userName = UserBean.trim_Data(request.getParameter("userName"));
                }

                if (request.getParameter("depName") != null)
                {
                    depName = UserBean.trim_Data(request.getParameter("depName"));
                }

                if (request.getParameter("subMid") != null)
                {
                	subMid = UserBean.trim_Data(request.getParameter("subMid"));
                }
                //判斷進入查詢
                if (actionType != null && actionType.length() > 0)
                {
                    boolean boolMerchantId = false;
                    boolean boolUserIdData = false;
                    boolean boolUserName = false;

                    //判斷輸入資料
                    if (strSessionMerchantId.length() > 0)
                    {
                       boolMerchantId = true;
                    }

                    if (userIdData.length() > 0)
                    {
                       boolUserIdData = true;
                    }

                    if (userName.length() > 0)
                    {
                       boolUserName = true;
                    }

                    //判斷查詢類別
                    if (String.valueOf(actionType).equals("LIST"))
                    {
                       Forward = ActionForwardList;
                    }
                    else if (String.valueOf(actionType).equals("CLS"))
                    {
                       Forward = ActionForwardMain;
                    }
                    else if ("ADD".equals(actionType))
                    {
                       Forward = ActionForwardAdd;
                       boolUserAllRole = true;
                    }
                    else
                    {
                        if (boolMerchantId)
                        {
                            if ("SELECT".equals(actionType))
                            {
                                if ("ALL".equals(actionTypeCk))
                                {
                                	if(subMid.equals("all") || subMid.equals("") )
                                		arrayUserData = this.select_UserData_DelFlag(strSessionMerchantId, "N");
                                	else
                                		arrayUserData = this.select_UserData_DelFlag(strSessionMerchantId,"",subMid, "N");
                                	
                                    if(arrayUserData == null)
                                    {
                                        LogStatus = "失敗";
                                        LogMemo = "系統發生錯誤，查無使用者資料";
                                    }
                                    else
                                    {
                                        LogStatus = "成功";
                                        LogMemo = "查詢使用者資料成功";
                                    }
                                }
                                else
                                {
                                    arrayUserData = this.select_UserData_DelFlag(strSessionMerchantId, userIdList,subMid, "N");
                                    if(arrayUserData == null)
                                    {
                                        LogStatus = "失敗";
                                        LogMemo = "系統發生錯誤，查無使用者資料";
                                    }
                                    else
                                    {
                                        LogStatus = "成功";
                                        LogMemo = "查詢使用者資料成功";
                                    }
                                }
                            }
                            else
                            {
                                ArrayList arrayUData = this.select_UserData(strSessionMerchantId, userIdData);
                                Hashtable hd = new Hashtable();
                                boolean boolAC = true;
                                boolean boolDelFlag = false;

                                if (arrayUData != null && arrayUData.size() > 0)
                                {
                                    hd = (Hashtable) arrayUData.get(0);
                                    String strAC = hd.get("AC_ADD") == null ? "" : hd.get("AC_ADD").toString();
                                    if ("Y".equalsIgnoreCase(strAC))
                                    {
                                        boolAC = false;
                                    }

                                    String strDl = hd.get("DEL_FLAG") == null ? "" : hd.get("DEL_FLAG").toString();
                                    if ("Y".equalsIgnoreCase(strDl))
                                    {
                                        boolDelFlag = true;
                                    }
                                }

                                if (boolUserIdData && "UPDATE".equals(actionType))
                                {
                                    if (boolAC)
                                    {
                                        arrayUserData = this.select_UserData_DelFlag(strSessionMerchantId, userIdData, "N");
                                        if (arrayUserData != null && arrayUserData.size() > 0)
                                        {
                                            Forward = ActionForwardUpdate;
                                            boolUserAllRole = true;
                                            boolUserRole = true;
                                            boolRunUpdate = true;
                                        }
                                        else
                                        {
                                            LogStatus = "失敗";
                                            Message = "查無使用者資料";
                                            LogMemo = "查無使用者資料";
                                        }
                                    }
                                    else
                                    {
                                        LogStatus = "失敗";
                                        Message = "不可修改管理者帳號資料";
                                        LogMemo = "不可修改管理者帳號資料";
                                    }
                                }
                                else if (boolUserIdData)
                                {
                                    if ("ADDDATA".equals(actionType))
                                    {
                                        boolIsEqual = false;
                                        Forward = ActionForwardAdd;
                                        boolUserAllRole = true;
                                        if (boolAC)
                                        {
                                            if (boolUserName)
                                            {
                                                //使用者初始密碼
                                                String strUserPwss = getUserDefPwd(session, hashConfData, userIdData);
                                                //密碼有效期限
                                                String strExpireDate = getExpireDate(session, hashConfData);
                                                //新增
                                                if(subMid == null || subMid.length()==0) subMid = strSessionMerchantId.substring(2);
                                                boolIsEqual = insert_UserData(request, strSessionMerchantId, strSessionUserId, userIdData,
                                                                              userName, depName, strUserPwss, strExpireDate,subMid,strSessionSIGN_BILL);

                                                if(boolIsEqual)
                                                {
                                                    strUserView = "1";
                                                    LogStatus = "成功";
                                                    Message = "新增成功";
                                                    LogMemo = "使用者新增成功";
                                                    sysBean.commit();
                                                }
                                                else
                                                {
                                                    LogStatus = "失敗";
                                                    Message = "新增失敗";
                                                    LogMemo = "使用者新增失敗";
                                                    sysBean.setRollBack();
                                                }
                                            }
                                            else
                                            {
                                                LogStatus = "失敗";
                                                Message = "使用者名稱未輸入";
                                            }
                                        }
                                        else
                                        {
                                            LogStatus = "失敗";
                                            Message = "此帳號與管理者帳號重覆";
                                        }
                                    }
                                    else if ("UPDATEDATA".equals(actionType))
                                    {
                                        if (boolAC)
                                        {
                                            if (boolDelFlag)
                                            {
                                                LogStatus = "失敗";
                                                Message = "無法修改使用者資料，使用者資料已刪除。";
                                            }
                                            else
                                            {
                                                boolIsEqual = false;
                                                boolUserAllRole = true;
                                                boolUserRole = true;
                                                Forward = ActionForwardUpdate;

                                                if ("UPDATEDATA".equals(actionTypeCk))
                                                {
                                                    if (boolUserName)
                                                    {
                                                        try
                                                        {
                                                            boolIsEqual = MUDBean.update_Merchant_User(sysBean, strSessionMerchantId, userIdData,
                                                                                                       userName, depName, strSessionUserId);
                                                            if (boolIsEqual)
                                                            {
                                                                Enumeration k = request.getParameterNames();
                                                                boolIsEqual = update_UserRole(k, strSessionMerchantId,
                                                                                              userIdData, strSessionUserId);
                                                            }
                                                            if (boolIsEqual)
                                                            {
                                                                strUserView = "1";
                                                                LogStatus = "成功";
                                                                Message = "修改成功";
                                                                LogMemo = "使用者修改成功";
                                                                sysBean.commit();
                                                            }
                                                            else
                                                            {
                                                                LogStatus = "失敗";
                                                                Message = "修改失敗";
                                                                LogMemo = "使用者修改失敗";
                                                                boolUserAllRole = true;
                                                                boolUserRole = true;
                                                                sysBean.setRollBack();
                                                            }
                                                        }
                                                        catch (Exception e)
                                                        {
                                                            log_systeminfo.debug("--MerchantUserDataCtl--"+e.toString());
                                                            LogStatus = "失敗";
                                                            Message = "修改失敗";
                                                            LogMemo = "使用者修改失敗";
                                                            e.printStackTrace();
                                                            sysBean.setRollBack();
                                                        }
                                                    }
                                                    else
                                                    {
                                                        LogStatus = "失敗";
                                                        Message = "使用者名稱未輸入";
                                                    }
                                                }
                                                else if ("REUSERSTATUS".equals(actionTypeCk) || "REDEACTIVATE".equals(actionTypeCk))
                                                {
                                                    //重設狀態 //解除停用
                                                    try
                                                    {
                                                        String strUserPwss = getUserDefPwd(session, hashConfData, userIdData); //使用者初始密碼
                                                        boolIsEqual = MUDBean.update_Merchant_User_resetPwd(sysBean, strSessionMerchantId, userIdData,
                                                                                           "R", strUserPwss, strSessionUserId, expireDate);
                                                        if (boolIsEqual)
                                                        {
                                                            strUserView = "1";
                                                            LogStatus = "成功";
                                                            Message = "解除停用成功";
                                                            LogMemo = "使用者解除停用成功";
                                                            sysBean.commit();
                                                        }
                                                        else
                                                        {
                                                            LogStatus = "失敗";
                                                            Message = "解除停用失敗";
                                                            LogMemo = "使用者解除停用失敗";
                                                            sysBean.setRollBack();
                                                        }
                                                    }
                                                    catch (Exception e)
                                                    {
                                                        log_systeminfo.debug("--MerchantUserDataCtl--"+e.toString());
                                                        LogStatus = "失敗";
                                                        Message = "解除停用失敗";
                                                        LogMemo = "使用者解除停用失敗";
                                                        e.printStackTrace();
                                                        sysBean.setRollBack();
                                                    }
                                                }
                                                else if ("DEACTIVATE".equals(actionTypeCk))
                                                {
                                                    //停用
                                                    try
                                                    {
                                                        boolIsEqual = MUDBean.update_Merchant_User_Status(sysBean, strSessionMerchantId,
                                                                                  userIdData, "E", strSessionUserId);

                                                        if (boolIsEqual)
                                                        {
                                                            strUserView = "1";
                                                            LogStatus = "成功";
                                                            Message = "停用成功";
                                                            LogMemo = "使用者停用成功";
                                                            sysBean.commit();
                                                        }
                                                        else
                                                        {
                                                            LogStatus = "失敗";
                                                            Message = "停用失敗";
                                                            LogMemo = "使用者停用失敗";
                                                            sysBean.setRollBack();
                                                        }
                                                    }
                                                    catch (Exception e)
                                                    {
                                                        log_systeminfo.debug("--MerchantUserDataCtl--"+e.toString());
                                                        LogStatus = "失敗";
                                                        Message = "停用失敗";
                                                        LogMemo = "使用者停用失敗";
                                                        e.printStackTrace();
                                                        sysBean.setRollBack();
                                                    }
                                                }
                                            }
                                        }
                                        else
                                        {
                                            LogStatus = "失敗";
                                            Message = "不可修改管理者帳號資料";
                                        }
                                    }
                                    else if ("DELETEDATA".equals(actionType))
                                    {
                                        boolIsEqual = false;
                                        Forward = ActionForwardList;
                                        if (boolAC)
                                        {
                                            boolIsEqual = MUDBean.delete_Merchant_User(sysBean, strSessionMerchantId,
                                                                  userIdData, strSessionUserId);
                                            if (boolIsEqual)
                                            {
                                                LogStatus = "成功";
                                                Message = "刪除成功";
                                                LogMemo = "使用者刪除成功";
                                                sysBean.commit();
                                            }
                                            else
                                            {
                                                LogStatus = "失敗";
                                                Message = "刪除失敗";
                                                LogMemo = "使用者刪除失敗";
                                                sysBean.setRollBack();
                                            }
                                        }
                                        else
                                        {
                                            LogStatus = "失敗";
                                            Message = "不可刪除管理者帳號資料";
                                        }
                                    }

                                    if ("DELETEDATA".equals(actionType) || !boolAC)
                                    {
                                        arrayUserData = this.select_UserData_DelFlag(strSessionMerchantId, "N");
                                    }
                                    else
                                    {
                                        arrayUserData = this.select_UserData_DelFlag(strSessionMerchantId, userIdData, "N");
                                    }
                                }
                                else
                                {
                                    LogStatus = "失敗";
                                    Message = "使用者代號未輸入";
                                }
                            }
                        }
                        else
                        {
                            LogStatus = "失敗";
                            Message = "無特約商店代號資料";
                        }

                        if (LogMemo.length() == 0)
                        {
                            LogMemo = Message;
                        }

                        if (!boolRunUpdate)
                        {
                            LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                            LogData = UserBean.get_LogData(LogMerchantID,LogUserName, LogFunctionName, LogStatus, userIdData+LogMemo);
                            log_user.debug(LogData);
                        }
                    }
                }

                if (boolUserAllRole)
                {
                    arrayUserAllRoleData = MUDBean.get_Merchant_User_SetRole(sysBean, strSessionMerchantId);
                }
                if (boolUserRole)
                {
                    arrayUserRoleData = MUDBean.get_Merchant_User_Role(sysBean, strSessionMerchantId, userIdData);
                }

                String strTimeOut = getTimeOut(session, hashConfData);
                Hashtable hashLogData = select_Merchant_User_LoginStatus(sysBean, strSessionMerchantId, strTimeOut);
                session.setAttribute("hashActionUserLogStatusData", hashLogData);
                session.setAttribute("arrayActionUserData", arrayUserData);
                session.setAttribute("arrayActionUserAllRoleData", arrayUserAllRoleData);
                session.setAttribute("arrayActionUserRoleData", arrayUserRoleData);
                session.setAttribute("strActionUserIdList", userIdList);
                session.setAttribute("strUserDataRoleAction", strUserDataRoleAction);
                session.setAttribute("strActionUserView", strUserView);

                //回傳頁面訊息
                session.setAttribute("Message", Message);

                System.out.println("Forward=" + Forward);
            }
            else
            {
                Message = "特約商店無此權限請洽本行處理";
                Forward = "./Merchant_NoUse.jsp";
                session.setAttribute("Message", Message);
            }

            System.out.println("----------------------------Forward="+Forward);
            request.getRequestDispatcher(Forward).forward(request, response);
        }
        catch (Exception e)
        {
            log_systeminfo.debug("--MerchantUserDataCtl--"+e.toString());
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
            log_systeminfo.debug("--MerchantUserDataCtl--"+e.toString());
            request.setAttribute("errMsg", e.toString());
            request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
        }
        }
    }

    private ArrayList select_UserData(String merchantId)
    {
        ArrayList arrayUserData = new ArrayList();
        arrayUserData = MUDBean.get_Merchant_User(sysBean, merchantId);

        return arrayUserData;
    }

    private ArrayList select_UserData(String merchantId, String userId)
    {
        ArrayList arrayUserData = new ArrayList();
        arrayUserData = MUDBean.get_Merchant_User(sysBean, merchantId, userId);

        return arrayUserData;
    }

    private ArrayList select_UserData_DelFlag(String merchantId, String delFlag)
    {
        ArrayList arrayUserData = new ArrayList();
        arrayUserData = MUDBean.get_Merchant_User_delFlag(sysBean, merchantId, delFlag);

        return arrayUserData;
    }

    private ArrayList select_UserData_DelFlag(String merchantId, String userId, String delFlag)
    {
        ArrayList arrayUserData = new ArrayList();
        arrayUserData = MUDBean.get_Merchant_User_delFlag(sysBean, merchantId, userId, delFlag);

        return arrayUserData;
    }
    private ArrayList select_UserData_DelFlag(String merchantId, String userId,String subMid, String delFlag)
    {
        ArrayList arrayUserData = new ArrayList();
        arrayUserData = MUDBean.get_Merchant_User_delFlag(sysBean, merchantId, userId, subMid , delFlag);

        return arrayUserData;
    }
    private String getUserDefPwd(HttpSession session, Hashtable hashConfData, String userIdData)
    {
        getSysConfData(session, hashConfData);
        String defPwss = "AAA";
        String userPwss = "";

        try
        {
            userPwss = defPwss + userIdData;
            //20220801 change MD5 to SHA256
            //userPwss = MerchantChangePwdCtl.getMsgDigestPwd(userPwd);
            userPwss = Util.getPwdfactor(userIdData, userPwss);
            userPwss = Util.SHA256(userPwss);
        }
        catch (Exception e)
        {
            userPwss = "";
            Message = "使用者初始密碼編碼失敗";
            log_systeminfo.debug("--MerchantUserDataCtl--"+e.toString());
            e.printStackTrace();
        }

        return userPwss;
    }

    private String getExpireDate(HttpSession session, Hashtable hashConfData)
    {
    	String expireDate = "60";
    	
        getSysConfData(session, hashConfData);

        return expireDate;
    }

    private String getTimeOut(HttpSession session, Hashtable hashConfData)
    {
    	String timeOut = "15"; //系統TIME OUT時間
    	
        getSysConfData(session, hashConfData);

        return timeOut;
    }

    private boolean insert_UserData(HttpServletRequest request, String strSessionMerchantId,
            String strSessionUserId, String userIdData, String userName,
            String depName, String strUserPwss, String strExpireDate,String strSubMid,String strSessionSIGN_BILL)
    {
        boolean boolIsEqual = false;
        String timeOut = "15"; //系統TIME OUT時間

        if (strUserPwss.length() != 0)
        {
            Enumeration k;
            boolean boolD = true; //是否帳號重覆(false 不可新增)
            boolean boolN = false; //true=> 新增, false=> (新增+修改)

            try
            {
                ArrayList arrayData = select_UserData(strSessionMerchantId, userIdData);

                if (arrayData != null)
                {
                    Hashtable hashN = new Hashtable();
                    if (arrayData.size() == 0)
                    {
                        boolN = true;
                    }
                    else
                    {
                        hashN = (Hashtable) arrayData.get(0);

                        String strN = hashN.get("DEL_FLAG") == null ? "" : hashN.get("DEL_FLAG").toString();
                        if (!"Y".equalsIgnoreCase(strN))
                        {
                            boolD = false;
                            Message = "使用者代號重覆";
                        }
                    }

                    if (boolD)
                    {
                        if (boolN)
                        {
                            boolIsEqual = MUDBean.insert_Merchant_User(sysBean, strSessionMerchantId, userIdData, userName,
                                                  strUserPwss, "O", strExpireDate, depName, strSessionUserId,strSubMid,strSessionSIGN_BILL);
                            if (boolIsEqual)
                            {
                               k = request.getParameterNames();
                               boolIsEqual = insert_UserRole(k, strSessionMerchantId, userIdData, strSessionUserId);
                            }
                        }
                        else
                        {
                            boolIsEqual = MUDBean.update_Merchant_User(sysBean, strSessionMerchantId, userIdData, userName,
                                                        strUserPwss, "O", depName, strSessionUserId, strExpireDate,strSubMid);
                            if (boolIsEqual)
                            {
                               k = request.getParameterNames();
                               boolIsEqual = update_UserRole(k, strSessionMerchantId, userIdData, strSessionUserId);
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
               e.printStackTrace();
               log_systeminfo.debug("--MerchantUserDataCtl--"+e.toString());
            }

            if (boolD)
            {
                if (boolIsEqual)
                {
                    Message = "新增成功";
                }
                else
                {
                    Message = "新增失敗";
                    MUDBean.delete_Merchant_User(sysBean, strSessionMerchantId, userIdData, strSessionUserId);
                    k = request.getParameterNames();
                    String uiCheck = "";
                    while (k.hasMoreElements())
                    {
                        //有值傳true 沒值傳flash
                        uiCheck = String.valueOf(k.nextElement()); //取KEY值
                        if (uiCheck.indexOf(strUserDataRoleAction) >= 0)
                        {
                            String userRol[] = uiCheck.split(",");
                            String strRoleId = "";

                            if (userRol.length >= 2)
                            {
                                strRoleId = userRol[1];
                            }
                            else
                            {
                                continue;
                            }
                            MUDBean.delete_Merchant_User_Role(sysBean, strSessionMerchantId, userIdData, strRoleId);
                            break;
                        }
                    }
                }
                
                sysBean.commit();
            }
        }

        return boolIsEqual;
    }

    /**
     * <p>新增使用者權限</p>
     * @param k Enumeration
     * @param SessionMerchantId String
     * @param UserId String
     * @param SessionUserId String
     * @return boolean
     */
    private boolean insert_UserRole(Enumeration k, String SessionMerchantId, String UserId, String SessionUserId)
    {
        int x = 0;
        int c = 0;
        String uiCheck = "";
        while (k.hasMoreElements())
        {
            boolean boolIsEqual = false;
            //有值傳true 沒值傳flash
            uiCheck = String.valueOf(k.nextElement()); //取KEY值
            if (uiCheck.indexOf(strUserDataRoleAction) >= 0)
            {
                String userRol[] = uiCheck.split(",");
                String strRoleId = "";
                String strMenuId = "";

                if (userRol.length == 3)
                {
                    x++;
                    strRoleId = userRol[1];
                    strMenuId = userRol[2];
                }
                else
                {
                    continue;
                }

                boolIsEqual = MUDBean.insert_Merchant_User_Role(sysBean, SessionMerchantId, UserId, strRoleId,
                                                                strMenuId, SessionUserId);
                if (boolIsEqual)
                {
                    c++;
                }
            }
        }

        if (x == c)
        {
            sysBean.commit();
            return true;
        }

        sysBean.setRollBack();
        return false;
    }

    /**
     * <p>修改使用者權限</p>
     * @param k Enumeration
     * @param SessionMerchantId String
     * @param UserId String
     * @param SessionUserId String
     * @return boolean
     */
    private boolean update_UserRole(Enumeration k, String SessionMerchantId, String UserId, String SessionUserId)
    {
        ArrayList arrayUserRoleData = MUDBean.get_Merchant_User_Role(sysBean, SessionMerchantId, UserId);

        int x = 0;
        int c = 0;
        String uiCheck = "";

        Hashtable hashAppRoll = new Hashtable(); //個人-依群組名稱分類功能
        if (arrayUserRoleData != null && arrayUserRoleData.size() > 0)
        {
            for (int i = 0; i < arrayUserRoleData.size(); i++)
            {
                Hashtable hashData = (Hashtable) arrayUserRoleData.get(i);
                if (hashData.get("ROLEID") != null)
                {
                    if (hashData.get("MENUID") != null)
                    {
                        hashAppRoll.put(hashData.get("ROLEID").toString() + hashData.get("MENUID").toString(), hashData);
                    }
                }
            }
        }

        while (k.hasMoreElements())
        {
            //有值傳true 沒值傳flash
            uiCheck = String.valueOf(k.nextElement()); //取KEY值
            if (uiCheck.indexOf(strUserDataRoleAction) >= 0)
            {
                String userRol[] = uiCheck.split(",");
                String strRoleId = "";
                String strMenuId = "";
                boolean boolIsEqual = false;

                if (userRol.length == 3)
                {
                    strRoleId = userRol[1];
                    strMenuId = userRol[2];
                    if (strRoleId != null && strRoleId.length() > 0 && strMenuId != null && strMenuId.length() > 0)
                    {
                        x++;
                        if (hashAppRoll.get(strRoleId + strMenuId) == null)
                        {
                            boolIsEqual = MUDBean.insert_Merchant_User_Role(sysBean, SessionMerchantId, UserId, strRoleId, strMenuId, SessionUserId);
                        }
                        else if (hashAppRoll.get(strRoleId + strMenuId) != null)
                        {
                            hashAppRoll.remove(strRoleId + strMenuId);
                            boolIsEqual = MUDBean.update_Merchant_User_Role(sysBean, SessionMerchantId, UserId, strRoleId, strMenuId, SessionUserId);
                        }
                        if (boolIsEqual)
                        {
                            c++;
                        }
                    }
                }
                else
                {
                    continue;
                }
            }
        }

        if (hashAppRoll.size() > 0)
        {
            k = hashAppRoll.keys();
            while (k.hasMoreElements())
            {
                Hashtable hashData = (Hashtable) hashAppRoll.get(k.nextElement()); //取KEY值)
                if (hashData != null)
                {
                    x++;
                    boolean boolIsEqual = false;
                    String mid = String.valueOf(hashData.get("MERCHANTID"));
                    String uid = String.valueOf(hashData.get("USERID"));
                    String rid = String.valueOf(hashData.get("ROLEID"));
                    String memuid = String.valueOf(hashData.get("MENUID"));
                    boolIsEqual = MUDBean.delete_Merchant_User_Role(sysBean, mid, uid, rid, memuid);
                    if (boolIsEqual)
                    {
                        c++;
                    }
                }
            }
        }

        if (x == c)
        {
            sysBean.commit();
            return true;
        }

        sysBean.setRollBack();
        return false;
    }

    private Hashtable select_Merchant_User_LoginStatus(DataBaseBean2 sysBean, String SessionMerchantId, String strTimeOut)
    {
        ArrayList arrayData = MUDBean.select_Merchant_User_LoginStatus(sysBean, SessionMerchantId, strTimeOut);
        Hashtable hashLogData = new Hashtable();

        if (arrayData != null && arrayData.size() > 0)
        {
            for (int i = 0; i < arrayData.size(); i++)
            {
                Hashtable hashData = (Hashtable) arrayData.get(i);
                if (hashData != null && hashData.get("USER_ID") != null && hashData.get("USER_ID").toString().length() > 0)
                {
                    hashLogData.put(hashData.get("USER_ID"), hashData.get("USER_ID"));
                }
            }

        }
        return hashLogData;
    }

    /**
     * <P>系統參數<P>
     * @param session HttpSession
     * @param hashConfData Hashtable
     */
    private void getSysConfData(HttpSession session, Hashtable hashConfData)
    {
        if (hashConfData != null && hashConfData.size() > 0)
        {
            @SuppressWarnings("rawtypes")
			Hashtable hashSys = (Hashtable) hashConfData.get("SYSCONF");
            
            String showName = "";
            String defPwss = "AAA";
            String expireDate = "60";
            String timeOut = "15"; //系統TIME OUT時間
            String showErrorName = "";
            
            try
            {
                if (hashSys == null || hashSys.size() == 0)
                {
                    ServletContext context = session.getServletContext();
                    showName = context.getInitParameter("MER_DEF_PWD");
                    defPwss = showName; //預設密碼

                    /** showErrorName = "MER_PWD_DAY";
					showName = context.getInitParameter("MER_PWD_DAY");
					expireDate = String.valueOf(Integer.parseInt(showName)); //密碼有效期限 */
                    showErrorName = "MER_NEWPWD_DAY";
                    showName = context.getInitParameter(showErrorName);
                    expireDate = String.valueOf(Integer.parseInt(showName)); //新密碼有效期限

                    showErrorName = "MER_TIMEOUT";
                    showName = context.getInitParameter(showErrorName);
                    timeOut = String.valueOf(Integer.parseInt(showName)); //系統TIME OUT時間
                }
                else
                {
                    showName = String.valueOf(hashSys.get("MER_DEF_PWD"));
                    defPwss = showName; //預設密碼

                    /** showErrorName = "MER_PWD_DAY";
					showName = String.valueOf(hashSys.get("MER_PWD_DAY"));
					expireDate = String.valueOf(Integer.parseInt(showName)); //密碼有效期限 */

                    showErrorName = "MER_NEWPWD_DAY";
                    showName = String.valueOf(hashSys.get(showErrorName));
                    expireDate = String.valueOf(Integer.parseInt(showName)); //新密碼有效期限

                    showErrorName = "MER_TIMEOUT";
                    showName = String.valueOf(hashSys.get(showErrorName));
                    timeOut = String.valueOf(Integer.parseInt(showName)); //系統TIME OUT時間
                }
            }
            catch (Exception e)
            {
                log_systeminfo.debug("--MerchantUserDataCtl--"+e.toString());
                log_systeminfo.debug("MerchantUserDataCtl");
                log_systeminfo.debug(showErrorName + " 的資料型態不正確 = [" + showName + "]");
                log_systeminfo.debug(e.getMessage());
            }
        }
    }
    	
    public static String getUserStatus(String s)
    {
        if ("Y".equals(s))
        {
            s = userStatus_Y;
        }
        else if ("O".equals(s))
        {
            s = userStatus_O;
        }
        else if ("N".equals(s))
        {
            s = userStatus_N;
        }
        else if ("R".equals(s))
        {
            s = userStatus_R;
        }
        else if ("E".equals(s))
        {
            s = userStatus_E;
        }

        return s;
    }
}
