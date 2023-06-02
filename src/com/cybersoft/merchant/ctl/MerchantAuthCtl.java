/************************************************************
 * <p>#File Name:   MerchantAuthCtl.java        </p>
 * <p>#Description:                         </p>
 * <p>#Create Date: 2007/10/11              </p>
 * <p>#Company:     cybersoft               </p>
 * <p>#Notice:                      </p>
 * @author      Nancy
 * @since       SPEC version
 * @version 0.1 2007/10/11  Nancy
 * 20201215 CKJ 202012090486-00 JAVA AUTH元件更新 置換API中method
 * 202208090854-01 20220801 HKP PCI-DSS更換密碼雜錯MD5為SHA256，MD5雜錯長度為32，SHA256雜錯長度為64，以此長度判斷是否為MD5，變更後的密碼一律使用SHA256與LOG遮蔽
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

import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft.bean.LogUtils;
import com.cybersoft.bean.SessionControlBean;
import com.cybersoft.bean.UserBean;
import com.cybersoft.common.Util;
import com.cybersoft.merchant.bean.MerchantAuthBean;
import com.cybersoft.merchant.bean.MerchantAuthParam;
import com.cybersoft4u.prj.tfbpg.api.SSLServer;
import com.cybersoft4u.prj.tfbpg.bean.ParamBean;
import com.cybersoft4u.util.CheckPan;
/**
 * @version     1.0
 * @author
 */
public class MerchantAuthCtl extends HttpServlet
{
    private static final String CONTENT_TYPE = "text/html; charset=Big5";
    private String Forward = ""; // 網頁轉址
    private String Message = ""; // 顯示訊息
    java.util.Date nowdate;
    public static final LogUtils log_user = new LogUtils("user");
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    public UserBean UserBean = new UserBean();
    public DataBaseBean2 SysBean = new DataBaseBean2();

    String LogUserName = "";
    String LogFunctionName = "線上授權作業";
    String LogStatus = "失敗";
    String LogMemo = "";
    String LogData = "";
    String LogMerchantID = "";
    /**
     * @see javax.servlet.http.HttpServlet#void (javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        doPost(req, resp);
    }

    /**
     * @see javax.servlet.http.HttpServlet#void (javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType(CONTENT_TYPE);
        request.setCharacterEncoding("BIG5");
        HttpSession session = request.getSession(true);
        SessionControlBean scb = new SessionControlBean();

        try
        {
            scb = new SessionControlBean(session, request, response);
            SysBean.setAutoCommit (false);
        }
        catch (UnsupportedOperationException E)
        {
        	//20130703 Jason when Exception close DB conn
            try
            {
                SysBean.close();
            }
            catch (Exception e)
            {
            }            
            E.toString();
            request.getRequestDispatcher("/Merchant_Bye.jsp").forward(request, response);
            return;            
        }
        

        try
        {
            Hashtable hashSys = new Hashtable(); // 系統參數
            Hashtable hashMerUser = new Hashtable(); // 特店使用者
            Hashtable hashMerchant = new Hashtable(); // 特店主檔
            ArrayList arrayTerminal = new ArrayList(); // 端末機主檔
            Hashtable hashConfData = (Hashtable) session.getAttribute("SYSCONFDATA");
            if (hashConfData == null)
                hashConfData = new Hashtable();

            boolean Merchant_Current = false; // 特店狀態
            boolean Merchant_Permit = false; // 特店權限

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

                arrayTerminal = (ArrayList) hashConfData.get("TERMINAL"); // 端末機主檔
                if (arrayTerminal == null)
                    arrayTerminal = new ArrayList();
            }

            // UserBean UserBean = new UserBean();
            Merchant_Current = UserBean.check_Merchant_Column(hashMerchant, "CURRENTCODE", "B,C"); //  確認特店狀態
            Merchant_Permit  = UserBean.check_Merchant_Column(hashMerchant, "PERMIT_SALE", "Y"); //  確認特店權限
            String MenuKey = (String) request.getParameter(MerchantMenuCtl.MENU_FORWARD_KEY);
            request.setAttribute(MerchantMenuCtl.MENU_FORWARD_KEY, MenuKey);
            boolean User_Permit = MerchantMenuCtl.getUserRole(SysBean, session, MenuKey);

            if (User_Permit)
            {
                // 使用者權限
                if (Merchant_Current)
                {
                    //特店狀態
                    if (Merchant_Permit)
                    {
                        // 特店權限
                        String Action = (request.getParameter("Action") == null) ? "" : request.getParameter("Action");
                        if (Action.length() == 0)
                        {
                            String Auth_Expire_year = (String) hashSys.get("AUTH_EXPIRE_YEAR");
                            if (Auth_Expire_year.length() == 0 || Auth_Expire_year == null)
                                Auth_Expire_year = "0";

                            int YearCount = Integer.parseInt(Auth_Expire_year);
                            ArrayList arrayExpireYear = new ArrayList();
                            MerchantAuthBean hppbean = new MerchantAuthBean();
                            String ExpireYear = hppbean.get_TransDate("yyyy");

                            for (int i = 0; i < YearCount; ++i)
                            {
                                arrayExpireYear.add(String.valueOf(Integer.parseInt(ExpireYear) + i));
                            }
                            session.setAttribute("AuthExpireYear", arrayExpireYear);
                            Forward = "./Merchant_Auth_Req.jsp";
                        }
                        else
                        {
                            boolean sendAuthAPIFlag = true;
                            String checkMsg = null;
                            ParamBean pbean = new ParamBean();
                            Forward = "./Merchant_Auth_Res.jsp";
                            MerchantAuthBean hppbean = new MerchantAuthBean();
                            MerchantAuthParam hppparam = (MerchantAuthParam) request.getSession().getAttribute("AuthParam");
                            hppparam.setPan(request.getParameter("pan") == null ? "" : UserBean.trim_Data(request.getParameter("pan")));
                            String expm = request.getParameter("expireM") == null ? "" : UserBean.trim_Data(request.getParameter("expireM"));
                            String expy = request.getParameter("expireY") == null ? "" : UserBean.trim_Data(request.getParameter("expireY"));
                            if (expy.length() == 4)
                            {
                                expy = expy.substring(2, 4);
                            }

                            hppparam.setExpireDate(expm + expy);
                            hppparam.setCVV2(request.getParameter("extenNo") == null ? "" : UserBean.trim_Data(request.getParameter("extenNo")));
                            hppparam.setTerminalID(request.getParameter("TerminalID") == null ? "" : UserBean.trim_Data(request.getParameter("TerminalID")));
                            hppparam.setOrderID(request.getParameter("ORDERID") == null ? "" : UserBean.trim_Data(request.getParameter("ORDERID")));
                            hppparam.setTransMode(Integer.parseInt(request.getParameter("TRANSMODE") == null ? "0" : UserBean.trim_Data(request.getParameter("TRANSMODE"))));
                            hppparam.setTransAmt(Double.parseDouble(request.getParameter("TRANSAMT") == null ? "0" : UserBean.trim_Data(request.getParameter("TRANSAMT"))));
                            //取得子特店代號
                            hppparam.setSubMID(hashMerUser.get("SUBMID") == null ? "" : hashMerUser.get("SUBMID").toString());
                            String Install = request.getParameter("INSTALL");

                            if (Install == null)
                            {
                                Install = "0";
                            }

                            Install = UserBean.trim_Data(Install);
                            if (Install.length() == 0)
                            {
                                Install = "0";
                            }

                            hppparam.setInstall(Integer.parseInt(Install));
                            String tmpTransDate = hppbean.get_TransDate("yyyyMMddHHmmss");
                            String transDate = tmpTransDate.substring(0, 8);
                            String transTime = tmpTransDate.substring(8, 14);
                            hppparam.setTransDate(transDate);
                            hppparam.setTransTime(transTime);

                            String SOCIALID = (String) request.getParameter("SOCIALID");
                            if (SOCIALID == null)
                                SOCIALID = "";

                            SOCIALID = UserBean.trim_Data(SOCIALID);
                            if (SOCIALID.length()>0)
                            {
                               hppparam.setSocialID(SOCIALID);
                            }

                            if (hppparam !=null)
                            {
                            	//String  > StringBuffer
                              StringBuffer  log =new StringBuffer( " GET JSP DATA (REQ) ");
                              log.append( "CVV2="+(hppparam.getCVV2() == null ? " " : hppparam.getCVV2()));
                              log.append(  "&" + "ExpireDate="+(hppparam.getExpireDate() == null ? " " : hppparam.getExpireDate()));
                              log.append(  "&" + "MerchantID="+(hppparam.getMerchantID() == null ? " " : hppparam.getMerchantID()));
                              log.append(  "&" + "NotifyURL="+(hppparam.getNotifyURL() == null ? " " : hppparam.getNotifyURL()));
                              log.append(  "&" + "OrderID="+(hppparam.getOrderID() == null ? " " : hppparam.getOrderID()));
                              log.append(  "&" + "Pan="+(hppparam.getPan() == null ? " " : Util.maskCardNo(hppparam.getPan())));
                              log.append(  "&" + "ResCode="+(hppparam.getResCode() == null ? " " : hppparam.getResCode()));
                              log.append(  "&" + "ResMsg="+(hppparam.getResMsg() == null ? " " : hppparam.getResMsg()));
                              log.append(  "&" + "SocialID="+(hppparam.getSocialID() == null ? " " : hppparam.getSocialID()));
                              log.append(  "&" + "SubMID="+(hppparam.getSubMID() == null ? " " : hppparam.getSubMID()));
                              log.append(  "&" + "TerminalID="+(hppparam.getTerminalID() == null ? " " : hppparam.getTerminalID()));
                              log.append(  "&" + "TransDate="+(hppparam.getTransDate() == null ? " " : hppparam.getTransDate()));
                              log.append(  "&" + "TransTime="+(hppparam.getTransTime() == null ? " " : hppparam.getTransTime()));
                              log.append(  "&" + "TransAmt="+String.valueOf(hppparam.getTransAmt()));
                              log.append(  "&" + "TransMode="+String.valueOf(hppparam.getTransMode()));
                              log.append(  "&" + "InstallCnt="+String.valueOf(hppparam.getInstall()));
                              log_systeminfo.debug(log.toString());
                            }

                            CheckPan chkpan = new CheckPan();
                            ParamBean parambean = new ParamBean();
                            parambean.setMerchantID(hppparam.getMerchantID());
                            parambean.setSubMID(hppparam.getSubMID());
                            parambean.setTerminalID(hppparam.getTerminalID());
                            parambean.setAcquirerID("442511");
                            parambean.setOrderID(hppparam.getOrderID());
                            parambean.setPAN(hppparam.getPan());
                            String CVV2 = hppparam.getCVV2();

                            if (CVV2.length() == 0)
                                CVV2 = null;

                            parambean.setCVV2(CVV2);
                            parambean.setExpireDate(hppparam.getExpireDate());
                            parambean.setTransDate(hppparam.getTransDate());
                            parambean.setTransTime(hppparam.getTransTime());
                            parambean.setTransCode("00");
                            parambean.setCurrencyCode("901");
                            parambean.setTransAmt(hppparam.getTransAmt());
                            parambean.setTransMode(hppparam.getTransMode());
                            parambean.setInstallCount(hppparam.getInstall());
                            parambean.setSocialID(hppparam.getSocialID());
                            //new add sub merchant 
                            parambean.setSubMID(hppparam.getSubMID());
                            if (parambean !=null)
                            {
                            	StringBuffer log =new StringBuffer( " Set AuthData To Auth SERVER (REQ) ");
                              log.append( "CVV2="+(parambean.getCVV2() == null ? " " : parambean.getCVV2()));
                              log.append(   "&" + "ExpireDate="+(parambean.getExpireDate() == null ? " " : parambean.getExpireDate()));
                              log.append(  "&" + "MerchantID="+(parambean.getMerchantID() == null ? " " : parambean.getMerchantID()));
                              log.append(  "&" +"OrderID="+(parambean.getOrderID() == null ? " " : parambean.getOrderID()));
                              log.append(  "&" +"Pan="+(parambean.getPAN() == null ? " " : Util.maskCardNo(parambean.getPAN())));
                              log.append(  "&" +"ResCode="+(parambean.getResponseCode() == null ? " " : parambean.getResponseCode()));
                              log.append(  "&" +"ResMsg="+(parambean.getResponseMsg() == null ? " " : parambean.getResponseMsg()));
                              log.append(  "&" +"SocialID="+(parambean.getSocialID() == null ? " " : parambean.getSocialID()));
                              log.append(  "&" +"SubMID="+(parambean.getSubMID() == null ? " " : parambean.getSubMID()));
                              log.append(  "&" +"TerminalID="+(parambean.getTerminalID() == null ? " " : parambean.getTerminalID()));
                              log.append(  "&" +"TransDate="+(parambean.getTransDate() == null ? " " : parambean.getTransDate()));
                              log.append(  "&" +"TransTime="+(parambean.getTransTime() == null ? " " : parambean.getTransTime()));
                              log.append(  "&" +"AcquirerID="+(parambean.getAcquirerID() == null ? " " : parambean.getAcquirerID()));
                              log.append(  "&" +"ExpireDate="+(parambean.getExpireDate() == null ? " " : parambean.getExpireDate()));
                              log.append(  "&" +"TransCode="+(parambean.getTransCode() == null ? " " : parambean.getTransCode()));
                              log.append(  "&" +"CurrencyCode="+(parambean.getCurrencyCode() == null ? " " : parambean.getCurrencyCode()));
                              log.append(  "&" +"TransAmt="+String.valueOf(parambean.getTransAmt()));
                              log.append(  "&" +"TransMode="+String.valueOf(parambean.getTransMode()));
                              log.append(  "&" +"InstallCnt="+String.valueOf(parambean.getInstallCount()));
                              log_systeminfo.debug(log.toString());
                            }

                            if (chkpan.check(hppparam.getPan()) != 0)
                            {
                                sendAuthAPIFlag = false;
                                checkMsg = "卡號錯誤!";
                            }
                            else
                            {
                                // UserBean UserBean = new UserBean();
                                Hashtable tableMP = hppparam.getMPermit();
                                if (!UserBean.check(hppparam.getPan()))
                                {
                                    String cardType = UserBean.getCardType();
                                    if (cardType.equals("V"))
                                    {
                                        if (!tableMP.get("SUPPORT_VISA").equals("Y"))
                                        {
                                            checkMsg = "特店不支援該卡別-" + cardType;
                                            sendAuthAPIFlag = false;
                                        }
                                    }

                                    if (cardType.equals("M"))
                                    {
                                        if (!tableMP.get("SUPPORT_MASTER").equals("Y"))
                                        {
                                            checkMsg = "特店不支援該卡別-" + cardType;
                                            sendAuthAPIFlag = false;
                                        }
                                    }

                                    if (cardType.equals("U"))
                                    {
                                        if (!tableMP.get("SUPPORT_UCARD").equals("Y"))
                                        {
                                            checkMsg = "特店不支援該卡別-" + cardType;
                                            sendAuthAPIFlag = false;
                                        }
                                    }

                                    if (cardType.equals("J"))
                                    {
                                        if (!tableMP.get("SUPPORT_JCBI").equals("Y"))
                                        {
                                            checkMsg = "特店不支援該卡別-" + cardType;
                                            sendAuthAPIFlag = false;
                                        }
                                    }

                                    if (cardType.equals("A"))
                                    {
                                        if (!tableMP.get("SUPPORT_AMEX").equals("Y"))
                                        {
                                            checkMsg = "特店不支援該卡別-" + cardType;
                                            sendAuthAPIFlag = false;
                                        }
                                    }
                                }

                                log_systeminfo.debug("-------------- local Check flag = "+sendAuthAPIFlag+" ---------------------");

                                //Call JAVAAPI
                                if (sendAuthAPIFlag)
                                {
                                    SSLServer sslapi = new SSLServer();
                                    int res = sslapi.STAuth(parambean, 1);
                                    log_systeminfo.debug("---------------sslapi.STAuth res=(" + res+ ")-------------------");
                                    //to Rec API Response
                                    pbean = sslapi.getResponse();
                                    if (pbean == null)
                                    {
                                        checkMsg = "交易失敗";
                                        sendAuthAPIFlag = false;
                                    }
                                    log_systeminfo.debug("-------------- Send To Auth flag = "+sendAuthAPIFlag+" ---------------------");
                                }
                            }

                            if (!sendAuthAPIFlag)
                            {
                                pbean = new ParamBean();
                                pbean.setMerchantID(hppparam.getMerchantID());
                                pbean.setTerminalID(hppparam.getTerminalID());
                                pbean.setPAN(hppparam.getPan());
                                pbean.setExpireDate(hppparam.getExpireDate());
                                pbean.setCVV2(hppparam.getCVV2());
                                pbean.setTerminalID(hppparam.getTerminalID());
                                pbean.setOrderID(hppparam.getOrderID());
                                pbean.setTransMode(hppparam.getTransMode());
                                pbean.setTransAmt(hppparam.getTransAmt());
                                pbean.setInstallCount(hppparam.getInstall());
                                pbean.setTransDate(hppparam.getTransDate());
                                pbean.setTransTime(hppparam.getTransTime());
                                pbean.setSocialID(hppparam.getSocialID());
                                //new add sub merchant 
                                pbean.setSubMID(hppparam.getSubMID());
                            }
                            else
                            {
                                checkMsg = String.valueOf(pbean.getResponseCode()).replaceAll("null","")+String.valueOf(pbean.getResponseMsg()).replaceAll("null","");
                            }

                            if (pbean !=null)
                            {
                            	StringBuffer log =new StringBuffer(  " (RES) ");
                             log.append( "CVV2="+(pbean.getCVV2() == null ? " " : pbean.getCVV2()));
                             log.append( "&" + "ExpireDate="+(pbean.getExpireDate() == null ? " " : pbean.getExpireDate()));
                             log.append( "&" + "MerchantID="+(pbean.getMerchantID() == null ? " " : pbean.getMerchantID()));
                             log.append( "&" + "OrderID="+(pbean.getOrderID() == null ? " " : pbean.getOrderID()));
                             log.append( "&" + "Pan="+(pbean.getPAN() == null ? " " : Util.maskCardNo(pbean.getPAN())));
                             log.append( "&" + "ResCode="+(pbean.getResponseCode() == null ? " " : pbean.getResponseCode()));
                             log.append( "&" + "ResMsg="+(pbean.getResponseMsg() == null ? " " : pbean.getResponseMsg()));
                             log.append( "&" + "SocialID="+(pbean.getSocialID() == null ? " " : pbean.getSocialID()));
                             log.append( "&" + "SubMID="+(pbean.getSubMID() == null ? " " : pbean.getSubMID()));
                             log.append( "&" + "TerminalID="+(pbean.getTerminalID() == null ? " " : pbean.getTerminalID()));
                             log.append( "&" + "TransDate="+(pbean.getTransDate() == null ? " " : pbean.getTransDate()));
                             log.append( "&" + "TransTime="+(pbean.getTransTime() == null ? " " : pbean.getTransTime()));
                             log.append( "&" + "AcquirerID="+(pbean.getAcquirerID() == null ? " " : pbean.getAcquirerID()));
                             log.append( "&" + "ExpireDate="+(pbean.getExpireDate() == null ? " " : pbean.getExpireDate()));
                             log.append( "&" + "TransCode="+(pbean.getTransCode() == null ? " " : pbean.getTransCode()));
                             log.append( "&" + "CurrencyCode="+(pbean.getCurrencyCode() == null ? " " : pbean.getCurrencyCode()));
                             log.append( "&" + "TransAmt="+String.valueOf(pbean.getTransAmt()));
                             log.append( "&" + "TransMode="+String.valueOf(pbean.getTransMode()));
                             log.append( "&" + "InstallCnt="+String.valueOf(pbean.getInstallCount()));
                             log.append( "&" + "ApproveCode="+String.valueOf(pbean.getApproveCode()));
                             log.append( "&" + "BatchNo="+String.valueOf(pbean.getBatchNo()));
                             log.append( "&" + "BillMessage="+String.valueOf(pbean.getBillMessage()));
                             log.append( "&" + "CreditAmt="+String.valueOf(pbean.getCreditAmt()));
                             log.append( "&" + "Direction="+String.valueOf(pbean.getDirection()));
                             log.append( "&" + "EachAmt="+String.valueOf(pbean.getEachAmt()));
                             log.append( "&" + "Email="+String.valueOf(pbean.getEmail()));
                             log.append( "&" + "Fee="+String.valueOf(pbean.getFee()));
                             log.append( "&" + "FirstAmt="+String.valueOf(pbean.getFirstAmt()));
                             log.append( "&" + "InstallType="+String.valueOf(pbean.getInstallType()));
                             log.append( "&" + "MessageType="+String.valueOf(pbean.getMessageType()));
                             log.append( "&" + "RedemBalance="+String.valueOf(pbean.getRedemBalance()));
                             log.append( "&" + "RedemType="+String.valueOf(pbean.getRedemType()));
                             log.append( "&" + "RedemUsed="+String.valueOf(pbean.getRedemUsed()));
                             log.append( "&" + "ResCode="+String.valueOf(pbean.getResponseCode()));
                             log.append( "&" + "ResMsg="+String.valueOf(pbean.getResponseMsg()));
                             log.append( "&" + "ReversalFlag="+String.valueOf(pbean.getReversalFlag()));
                             log.append( "&" + "RRN="+String.valueOf(pbean.getRRN()));
                             log.append( "&" + "SysOrderID="+String.valueOf(pbean.getSysOrderID()));
                             log.append( "&" + "Transtype="+String.valueOf(pbean.getTranstype()));
                              log_systeminfo.debug(log);
                            }

                            request.getSession().setAttribute("ParamBean", pbean);
                            request.setAttribute("checkmsg", checkMsg);
                            String Code = String.valueOf(pbean.getResponseCode()).replaceAll("null","");

                            if (Code.equalsIgnoreCase("000"))
                                LogStatus = "成功";

                            LogMemo = checkMsg;
                            LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                            LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                            log_user.debug(LogData);
                        }
                    }
                    else
                    {
                        Message = "特店無此功能權限";
                        Forward = "./Merchant_Response.jsp";
                        session.setAttribute("Message", Message);
                        LogMemo = Message;
                        LogUserName = hashMerUser.get("USER_NAME").toString();
                        LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "失敗", LogMemo);
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
                    LogUserName = hashMerUser.get("USER_NAME").toString();
                    LogData = UserBean.get_LogData(LogMerchantID,LogUserName, LogFunctionName, "失敗", LogMemo);
                    log_user.debug(LogData);

                }
            }
            else
            {
                Message = "使用者無此權限請洽系統管理者";
                Forward = "./Merchant_NoUse.jsp";
                session.setAttribute("Message", Message);
                LogMemo = Message;
                LogUserName = hashMerUser.get("USER_NAME").toString();
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "失敗", LogMemo);
                log_user.debug(LogData);
            }
            request.getRequestDispatcher(Forward).forward(request, response);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log_systeminfo.debug("--MerchantAuthCtl--"+e.toString());
            request.getRequestDispatcher(Forward).forward(request, response);
        }
        //20130703 Jason finally close SysBean DB connection
        finally{
        	try{
        		SysBean.close();
        	}
        	catch (Exception e){
        	}
        }
    }
}
