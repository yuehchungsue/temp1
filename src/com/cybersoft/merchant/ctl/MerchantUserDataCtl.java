/************************************************************
 * <p>#File Name:       MerchantUserDataCtl.java    </p>
 * <p>#Description:                 </p>
 * <p>#Create Date:     2007/09/26      </p>
 * <p>#Company:         cybersoft       </p>
 * <p>#Notice:                      </p>
 * @author      Caspar Chen
 * @since       SPEC version
 * @version 0.1 2007/09/26  Caspar Chen
 * 202208090854-01 20220801 HKP PCI-DSS�󴫱K�X����MD5��SHA256�AMD5�������׬�32�ASHA256�������׬�64�A�H�����קP�_�O�_��MD5�A�ܧ�᪺�K�X�@�ߨϥ�SHA256�PLOG�B��
 ************************************************************/
/**���ʻ���
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
 * <p>����K�X�ܧ�Servlet</p>
 * @version 0.1 2007/09/26  Caspar Chen
 */
public class MerchantUserDataCtl extends HttpServlet
{
    private static final String CONTENT_TYPE = "text/html; charset=Big5";
    private String Forward = ""; // ������}
    private String Message = ""; // ��ܰT��
    private static String userStatus_Y = "���`"; //�ϥΪ̪��A
    private static String userStatus_O = "�ݱҥ�"; //�ϥΪ̪��A
    private static String userStatus_N = "�K�X��w"; //�ϥΪ̪��A
    private static String userStatus_R = "�K�X���m"; //�ϥΪ̪��A
    private static String userStatus_E = "����"; //�ϥΪ̪��A
    private static final String ActionForwardMain = "./Merchant_Main.jsp"; //�w�]�����W��
    private static final String ActionForwardList = "./Merchant_UserDataList.jsp"; //�w�]�����W��
    private static final String ActionForwardAdd = "./Merchant_UserDataAdd.jsp"; //�w�]�����W��
    private static final String ActionForwardUpdate = "./Merchant_UserDataUpdate.jsp"; //�w�]�����W��
    /** private String defPwss = "AAA"; //�w�]�զX��l�K�X�r�� */
    /** private String expireDate = "60"; //�K�X���Ĵ��� */
    /** private String timeOut = "15"; //�t��TIME OUT�ɶ� */
    private String strUserDataRoleAction = "UserDataRoleAction"; //�s�W�B�ק��v���P�_��
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
            Message = ""; // ��ܰT��
            //�ŧi�ܼ�
            Forward = ActionForwardList;
            boolean boolIsEqual = false;
            boolean boolUserAllRole = false;
            boolean boolUserRole = false;
            boolean boolRunUpdate = false;
            String strUserView = "0"; //������ܸ�Ƥ��i�ק� 0=�w�] 1=�s�W
            ArrayList arrayUserData = new ArrayList();
            ArrayList arrayUserAllRoleData = new ArrayList();
            ArrayList arrayUserRoleData = new ArrayList();
            Hashtable hashMerUser = new Hashtable();;
            String strSessionMerchantId = "";
            String strSessionUserId = ""; //�n�J�H���N��
            String strSessionSIGN_BILL ="N";//�O�_���qñ�S��
            String userIdData = ""; //�N��
            String userIdList = ""; //�N��
            String userName = ""; //�W��
            String depName = ""; //����
            String subMid="";//�l�S���N��
            String actionTypeCk = UserBean.trim_Data(request.getParameter("actionTypeCk"));
            String actionType = UserBean.trim_Data(request.getParameter("actionType"));
            String LogUserName = "";
            String LogFunctionName = "�ϥΪ̺޲z";
            String LogStatus = "���\";
            String LogMemo = "";
            String LogData = "";
            String LogMerchantID = "";
            String MenuKey = (String) request.getParameter(MerchantMenuCtl.MENU_FORWARD_KEY);
            request.setAttribute(MerchantMenuCtl.MENU_FORWARD_KEY, MenuKey);
            String expireDate = "60";

            if (MerchantMenuCtl.getUserRole(sysBean, session, MenuKey))
            {
                Hashtable hashConfData = (Hashtable) session.getAttribute("SYSCONFDATA");
                //session���
                if (hashConfData != null && hashConfData.size() > 0)
                {
                     // �ϥΪ̸��
                    hashMerUser = (java.util.Hashtable) hashConfData.get("MERCHANT_USER");

                    // �ϥΪ̸��
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
                //�P�_�i�J�d��
                if (actionType != null && actionType.length() > 0)
                {
                    boolean boolMerchantId = false;
                    boolean boolUserIdData = false;
                    boolean boolUserName = false;

                    //�P�_��J���
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

                    //�P�_�d�����O
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
                                        LogStatus = "����";
                                        LogMemo = "�t�εo�Ϳ��~�A�d�L�ϥΪ̸��";
                                    }
                                    else
                                    {
                                        LogStatus = "���\";
                                        LogMemo = "�d�ߨϥΪ̸�Ʀ��\";
                                    }
                                }
                                else
                                {
                                    arrayUserData = this.select_UserData_DelFlag(strSessionMerchantId, userIdList,subMid, "N");
                                    if(arrayUserData == null)
                                    {
                                        LogStatus = "����";
                                        LogMemo = "�t�εo�Ϳ��~�A�d�L�ϥΪ̸��";
                                    }
                                    else
                                    {
                                        LogStatus = "���\";
                                        LogMemo = "�d�ߨϥΪ̸�Ʀ��\";
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
                                            LogStatus = "����";
                                            Message = "�d�L�ϥΪ̸��";
                                            LogMemo = "�d�L�ϥΪ̸��";
                                        }
                                    }
                                    else
                                    {
                                        LogStatus = "����";
                                        Message = "���i�ק�޲z�̱b�����";
                                        LogMemo = "���i�ק�޲z�̱b�����";
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
                                                //�ϥΪ̪�l�K�X
                                                String strUserPwss = getUserDefPwd(session, hashConfData, userIdData);
                                                //�K�X���Ĵ���
                                                String strExpireDate = getExpireDate(session, hashConfData);
                                                //�s�W
                                                if(subMid == null || subMid.length()==0) subMid = strSessionMerchantId.substring(2);
                                                boolIsEqual = insert_UserData(request, strSessionMerchantId, strSessionUserId, userIdData,
                                                                              userName, depName, strUserPwss, strExpireDate,subMid,strSessionSIGN_BILL);

                                                if(boolIsEqual)
                                                {
                                                    strUserView = "1";
                                                    LogStatus = "���\";
                                                    Message = "�s�W���\";
                                                    LogMemo = "�ϥΪ̷s�W���\";
                                                    sysBean.commit();
                                                }
                                                else
                                                {
                                                    LogStatus = "����";
                                                    Message = "�s�W����";
                                                    LogMemo = "�ϥΪ̷s�W����";
                                                    sysBean.setRollBack();
                                                }
                                            }
                                            else
                                            {
                                                LogStatus = "����";
                                                Message = "�ϥΪ̦W�٥���J";
                                            }
                                        }
                                        else
                                        {
                                            LogStatus = "����";
                                            Message = "���b���P�޲z�̱b������";
                                        }
                                    }
                                    else if ("UPDATEDATA".equals(actionType))
                                    {
                                        if (boolAC)
                                        {
                                            if (boolDelFlag)
                                            {
                                                LogStatus = "����";
                                                Message = "�L�k�ק�ϥΪ̸�ơA�ϥΪ̸�Ƥw�R���C";
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
                                                                LogStatus = "���\";
                                                                Message = "�ק令�\";
                                                                LogMemo = "�ϥΪ̭ק令�\";
                                                                sysBean.commit();
                                                            }
                                                            else
                                                            {
                                                                LogStatus = "����";
                                                                Message = "�ק異��";
                                                                LogMemo = "�ϥΪ̭ק異��";
                                                                boolUserAllRole = true;
                                                                boolUserRole = true;
                                                                sysBean.setRollBack();
                                                            }
                                                        }
                                                        catch (Exception e)
                                                        {
                                                            log_systeminfo.debug("--MerchantUserDataCtl--"+e.toString());
                                                            LogStatus = "����";
                                                            Message = "�ק異��";
                                                            LogMemo = "�ϥΪ̭ק異��";
                                                            e.printStackTrace();
                                                            sysBean.setRollBack();
                                                        }
                                                    }
                                                    else
                                                    {
                                                        LogStatus = "����";
                                                        Message = "�ϥΪ̦W�٥���J";
                                                    }
                                                }
                                                else if ("REUSERSTATUS".equals(actionTypeCk) || "REDEACTIVATE".equals(actionTypeCk))
                                                {
                                                    //���]���A //�Ѱ�����
                                                    try
                                                    {
                                                        String strUserPwss = getUserDefPwd(session, hashConfData, userIdData); //�ϥΪ̪�l�K�X
                                                        boolIsEqual = MUDBean.update_Merchant_User_resetPwd(sysBean, strSessionMerchantId, userIdData,
                                                                                           "R", strUserPwss, strSessionUserId, expireDate);
                                                        if (boolIsEqual)
                                                        {
                                                            strUserView = "1";
                                                            LogStatus = "���\";
                                                            Message = "�Ѱ����Φ��\";
                                                            LogMemo = "�ϥΪ̸Ѱ����Φ��\";
                                                            sysBean.commit();
                                                        }
                                                        else
                                                        {
                                                            LogStatus = "����";
                                                            Message = "�Ѱ����Υ���";
                                                            LogMemo = "�ϥΪ̸Ѱ����Υ���";
                                                            sysBean.setRollBack();
                                                        }
                                                    }
                                                    catch (Exception e)
                                                    {
                                                        log_systeminfo.debug("--MerchantUserDataCtl--"+e.toString());
                                                        LogStatus = "����";
                                                        Message = "�Ѱ����Υ���";
                                                        LogMemo = "�ϥΪ̸Ѱ����Υ���";
                                                        e.printStackTrace();
                                                        sysBean.setRollBack();
                                                    }
                                                }
                                                else if ("DEACTIVATE".equals(actionTypeCk))
                                                {
                                                    //����
                                                    try
                                                    {
                                                        boolIsEqual = MUDBean.update_Merchant_User_Status(sysBean, strSessionMerchantId,
                                                                                  userIdData, "E", strSessionUserId);

                                                        if (boolIsEqual)
                                                        {
                                                            strUserView = "1";
                                                            LogStatus = "���\";
                                                            Message = "���Φ��\";
                                                            LogMemo = "�ϥΪ̰��Φ��\";
                                                            sysBean.commit();
                                                        }
                                                        else
                                                        {
                                                            LogStatus = "����";
                                                            Message = "���Υ���";
                                                            LogMemo = "�ϥΪ̰��Υ���";
                                                            sysBean.setRollBack();
                                                        }
                                                    }
                                                    catch (Exception e)
                                                    {
                                                        log_systeminfo.debug("--MerchantUserDataCtl--"+e.toString());
                                                        LogStatus = "����";
                                                        Message = "���Υ���";
                                                        LogMemo = "�ϥΪ̰��Υ���";
                                                        e.printStackTrace();
                                                        sysBean.setRollBack();
                                                    }
                                                }
                                            }
                                        }
                                        else
                                        {
                                            LogStatus = "����";
                                            Message = "���i�ק�޲z�̱b�����";
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
                                                LogStatus = "���\";
                                                Message = "�R�����\";
                                                LogMemo = "�ϥΪ̧R�����\";
                                                sysBean.commit();
                                            }
                                            else
                                            {
                                                LogStatus = "����";
                                                Message = "�R������";
                                                LogMemo = "�ϥΪ̧R������";
                                                sysBean.setRollBack();
                                            }
                                        }
                                        else
                                        {
                                            LogStatus = "����";
                                            Message = "���i�R���޲z�̱b�����";
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
                                    LogStatus = "����";
                                    Message = "�ϥΪ̥N������J";
                                }
                            }
                        }
                        else
                        {
                            LogStatus = "����";
                            Message = "�L�S���ө��N�����";
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

                //�^�ǭ����T��
                session.setAttribute("Message", Message);

                System.out.println("Forward=" + Forward);
            }
            else
            {
                Message = "�S���ө��L���v���Ь�����B�z";
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
        //20130702 Jason �W�[finally�B�zsysBean.close
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
            Message = "�ϥΪ̪�l�K�X�s�X����";
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
    	String timeOut = "15"; //�t��TIME OUT�ɶ�
    	
        getSysConfData(session, hashConfData);

        return timeOut;
    }

    private boolean insert_UserData(HttpServletRequest request, String strSessionMerchantId,
            String strSessionUserId, String userIdData, String userName,
            String depName, String strUserPwss, String strExpireDate,String strSubMid,String strSessionSIGN_BILL)
    {
        boolean boolIsEqual = false;
        String timeOut = "15"; //�t��TIME OUT�ɶ�

        if (strUserPwss.length() != 0)
        {
            Enumeration k;
            boolean boolD = true; //�O�_�b������(false ���i�s�W)
            boolean boolN = false; //true=> �s�W, false=> (�s�W+�ק�)

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
                            Message = "�ϥΪ̥N������";
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
                    Message = "�s�W���\";
                }
                else
                {
                    Message = "�s�W����";
                    MUDBean.delete_Merchant_User(sysBean, strSessionMerchantId, userIdData, strSessionUserId);
                    k = request.getParameterNames();
                    String uiCheck = "";
                    while (k.hasMoreElements())
                    {
                        //���ȶ�true �S�ȶ�flash
                        uiCheck = String.valueOf(k.nextElement()); //��KEY��
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
     * <p>�s�W�ϥΪ��v��</p>
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
            //���ȶ�true �S�ȶ�flash
            uiCheck = String.valueOf(k.nextElement()); //��KEY��
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
     * <p>�ק�ϥΪ��v��</p>
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

        Hashtable hashAppRoll = new Hashtable(); //�ӤH-�̸s�զW�٤����\��
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
            //���ȶ�true �S�ȶ�flash
            uiCheck = String.valueOf(k.nextElement()); //��KEY��
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
                Hashtable hashData = (Hashtable) hashAppRoll.get(k.nextElement()); //��KEY��)
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
     * <P>�t�ΰѼ�<P>
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
            String timeOut = "15"; //�t��TIME OUT�ɶ�
            String showErrorName = "";
            
            try
            {
                if (hashSys == null || hashSys.size() == 0)
                {
                    ServletContext context = session.getServletContext();
                    showName = context.getInitParameter("MER_DEF_PWD");
                    defPwss = showName; //�w�]�K�X

                    /** showErrorName = "MER_PWD_DAY";
					showName = context.getInitParameter("MER_PWD_DAY");
					expireDate = String.valueOf(Integer.parseInt(showName)); //�K�X���Ĵ��� */
                    showErrorName = "MER_NEWPWD_DAY";
                    showName = context.getInitParameter(showErrorName);
                    expireDate = String.valueOf(Integer.parseInt(showName)); //�s�K�X���Ĵ���

                    showErrorName = "MER_TIMEOUT";
                    showName = context.getInitParameter(showErrorName);
                    timeOut = String.valueOf(Integer.parseInt(showName)); //�t��TIME OUT�ɶ�
                }
                else
                {
                    showName = String.valueOf(hashSys.get("MER_DEF_PWD"));
                    defPwss = showName; //�w�]�K�X

                    /** showErrorName = "MER_PWD_DAY";
					showName = String.valueOf(hashSys.get("MER_PWD_DAY"));
					expireDate = String.valueOf(Integer.parseInt(showName)); //�K�X���Ĵ��� */

                    showErrorName = "MER_NEWPWD_DAY";
                    showName = String.valueOf(hashSys.get(showErrorName));
                    expireDate = String.valueOf(Integer.parseInt(showName)); //�s�K�X���Ĵ���

                    showErrorName = "MER_TIMEOUT";
                    showName = String.valueOf(hashSys.get(showErrorName));
                    timeOut = String.valueOf(Integer.parseInt(showName)); //�t��TIME OUT�ɶ�
                }
            }
            catch (Exception e)
            {
                log_systeminfo.debug("--MerchantUserDataCtl--"+e.toString());
                log_systeminfo.debug("MerchantUserDataCtl");
                log_systeminfo.debug(showErrorName + " ����ƫ��A�����T = [" + showName + "]");
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
