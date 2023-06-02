/************************************************************
 * <p>#File Name:       Merchant_ChangePwdCtl.java  </p>
 * <p>#Description:                 </p>
 * <p>#Create Date:     2007/09/26      </p>
 * <p>#Company:         cybersoft       </p>
 * <p>#Notice:                      </p>
 * @author      Caspar Chen
 * @since       SPEC version
 * @version 0.1 2007/09/26  Caspar Chen
 * 202208090854-01 20220801 HKP PCI-DSS更換密碼雜錯MD5為SHA256，MD5雜錯長度為32，SHA256雜錯長度為64，以此長度判斷是否為MD5，變更後的密碼一律使用SHA256與LOG遮蔽
 ************************************************************/
package com.cybersoft.merchant.ctl;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.cybersoft.merchant.bean.MerchantChangePwdBean;
import com.cybersoft.bean.SessionControlBean;
import java.security.MessageDigest;
import com.cybersoft.bean.UserBean;
/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft.bean.LogUtils;
import com.cybersoft.common.*;
/**
 * <p>控制密碼變更的Servlet</p>
 * @version 0.1 2007/09/26  Caspar Chen
 */
public class MerchantChangePwdCtl extends HttpServlet
{
    private static final String CONTENT_TYPE = "text/html; charset=Big5";
    private String Forward = ""; // 網頁轉址
    private String Message = ""; // 顯示訊息

    //系統參數
    private int Sys_Pram_Mer_His_Count = 10;   //密碼更新指標最大值
    private int Sys_Pram_Mer_Order_Count = 4;  //密碼可連續數
    private int Sys_Pram_Mer_Equals_Count = 4; //密碼可相同數
    private int Sys_Pram_Mer_Pwd_Minlen = 6;   //密碼最小長度
    private int Sys_Pram_Mer_Pwd_Maxlen = 16;  //密碼最大長度
    private int Sys_Pram_Mer_Pwd_Common_Cnt = 0;  //密碼常用字串已建立數
    private Hashtable Sys_Pram_Mer_Pwd_Common = new Hashtable();  //密碼常用字串
    private int Sys_Pram_Mer_Expire_Date = 60; //密碼有效期限

    //參數
    private final String pwssHisName = "PWD_HIS"; //歷史密碼欄位名稱
    private final String pwssCommonName = "MER_PWD_COMMON_"; //密碼常用字串名稱

    //使用者資料
    private String Data_Merchant_Id = "";
    private String Data_User_Id = "";

    //session資料
    private Hashtable hashSys = new Hashtable(); // 系統參數
    private Hashtable hashMerUser = new Hashtable(); // 特約商店使用者

    MerchantChangePwdBean MCPBean;
    public UserBean UserBean = new UserBean();
    public DataBaseBean2 sysBean = new DataBaseBean2();
    
    java.util.Date nowdate;
    public static final LogUtils log_user = new LogUtils("user");
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    String LogUserName = "";
    String LogFunctionName = "密碼變更";
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
        SessionControlBean scb =new SessionControlBean();

        try
        {
            scb = new SessionControlBean(session,request,response);
            sysBean.setAutoCommit (false);
        }
        catch(UnsupportedOperationException E)
        {
            E.toString();
            request.getRequestDispatcher("/Merchant_Bye.jsp").forward(request, response);
            return ;
        }

        try
        {
            // UserBean UserBean = new UserBean();
            Message = ""; // 顯示訊息
            //頁面輸入資料
            boolean boolsendRedirect = false;  // 直接轉址
            boolean boolForwardAction = false;
            boolean boolAction = false; //是否進入修改密碼
            boolean boolInputDataEqual = false;
            boolean boolIsEqual = true;
            String changePwssFlag = request.getParameter("changePwdFlag");
            String oldPwss = (String) request.getParameter("oldPwd");
            String newPwss = (String) request.getParameter("newPwd");
            String confirm = (String) request.getParameter("confirm");

            String MenuKey = (String) request.getParameter(MerchantMenuCtl.MENU_FORWARD_KEY);
            String strlen = (String)session.getAttribute(com.cybersoft.merchant.ctl.MerchantLoginCtl.LOGIN_OLD_JSP);

            if(strlen != null && MenuKey == null )
            {
               Forward = strlen;
            }
            else
            {
               request.setAttribute(MerchantMenuCtl.MENU_FORWARD_KEY, MenuKey);

               boolean boolPwOld = false;
               String ur = "";
               if (session.getAttribute(MenuKey) != null)
               {
                  ur = String.valueOf(session.getAttribute(MenuKey));
                  if (ur.equals("OK"))
                  {
                     boolPwOld = true;
                  }
               }

               if (boolPwOld || MerchantMenuCtl.getUserRole(sysBean, session, MenuKey))
               {
                   Hashtable hashConfData = (Hashtable) session.getAttribute("SYSCONFDATA");
                   if (hashConfData == null || hashConfData.size() == 0)
                   {
                       Message = "無法取得登入資料";
                       boolIsEqual = false;
                   }
                   else
                   {
                       hashSys = (Hashtable) hashConfData.get("SYSCONF");
                       hashMerUser = (Hashtable) hashConfData.get("MERCHANT_USER");

                       // 系統參數資料
                       if (hashSys == null || hashSys.size() == 0)
                       {
                          Message = "無法取得登入資料-系統參數資料";
                          boolIsEqual = false;
                          setDefaultContextData(session);
                       }
                       else
                       {
                          if (!setDefaultDbData())
                          {
                             setDefaultContextData(session);
                          }
                       }

                       // 特約商店使用者資料
                       if (hashMerUser == null || hashMerUser.size() == 0)
                       {
                          Message = "無法取得登入資料-特約商店使用者資料";
                          boolIsEqual = false;
                       }
                       else
                       {
                           if (hashMerUser.get("MERCHANT_ID") != null)
                           {
                              Data_Merchant_Id = Util.objToStrTrim(hashMerUser.get("MERCHANT_ID"));
                              LogMerchantID = Data_Merchant_Id;
                           }

                           if (hashMerUser.get("USER_ID") != null)
                           {
                        	   Data_User_Id = Util.objToStrTrim(hashMerUser.get("USER_ID"));
                           }
                       }
                   }

                   MCPBean = new MerchantChangePwdBean();

                   if (oldPwss != null && newPwss != null && confirm != null && boolIsEqual)
                   {

                       boolInputDataEqual = this.CheckPwdNoData(oldPwss);
                       if (!boolInputDataEqual)
                       {
                           Message = "舊密碼欄位無資料";
                       }

                       if (boolInputDataEqual)
                       {
                          boolInputDataEqual = this.CheckPwdNoData(newPwss);
                          if (!boolInputDataEqual)
                          {
                             Message = "新密碼欄位無資料";
                          }
                       }

                       if (boolInputDataEqual)
                       {
                          boolInputDataEqual = this.CheckPwdNoData(confirm);
                          if (!boolInputDataEqual)
                          {
                             Message = "確認新密碼欄位無資料";
                          }
                       }

                       //密碼變更
                       if (boolInputDataEqual)
                       {
                           ArrayList arrayData = MCPBean.get_Merchant_User_PWD(Data_Merchant_Id, Data_User_Id);
                           if (arrayData != null && arrayData.size() > 0)
                           {
                               Hashtable hashData = (Hashtable) arrayData.get(0);
                               if (hashData == null)
                               {
                                   hashData = new Hashtable();
                               }
                               if (this.getMessage().length() == 0)
                               {
                                   //檢核 使用者密碼是否一致
                                   if (boolIsEqual)
                                   {
                                       try
                                       {
                                    	   //20220801 change CheckOldPwd function,MD5 and SHA256 check
                                           boolIsEqual = this.CheckOldPwd(arrayData, oldPwss.trim());
                                           if (!boolIsEqual)
                                           {
                                               Message = "使用者舊密碼不一致";
                                           }

                                           if (boolIsEqual)
                                           {
                                               boolIsEqual = !this.CheckOldPwd(arrayData, newPwss.trim());
                                               if (!boolIsEqual)
                                               {
                                                  Message = "新密碼不可與舊密碼一樣";
                                               }
                                           }
                                       }
                                       catch (Exception e)
                                       {
                                           log_systeminfo.debug("--MerchantChangePwdCtl--"+e.toString());
                                           boolIsEqual = false;
                                           Message = "密碼編碼失敗無法檢核";
                                       }
                                   }

                                   //檢核 新密碼不等於確認新密碼
                                   if (boolIsEqual)
                                   {
                                       boolIsEqual = newPwss.equals(confirm);
                                       if (!boolIsEqual)
                                       {
                                           Message = "新密碼不等於確認新密碼";
                                       }
                                   }

                                   //檢核 新密碼長度不等於確認新密碼長度
                                   if (boolIsEqual)
                                   {
                                       if (newPwss.length() == confirm.length())
                                       {
                                           boolIsEqual = true;
                                       }
                                       else
                                       {
                                           boolIsEqual = false;
                                           Message = "新密碼長度不等於確認新密碼長度";
                                       }
                                   }

                                   //檢核 可輸入長度
                                   if (boolIsEqual)
                                   {
                                       boolIsEqual = this.CheckInputLength(newPwss.length());
                                       if (!boolIsEqual)
                                       {
                                           Message = "密碼長度需為 " + this.Sys_Pram_Mer_Pwd_Minlen +
                                                    " 至 " + this.Sys_Pram_Mer_Pwd_Maxlen + " 位";
                                       }
                                   }

                                   //判斷 密碼需為數字及英文字
                                   if (boolIsEqual)
                                   {
                                       boolIsEqual = this.CheckLetterOfDigit(newPwss);
                                       if (!boolIsEqual)
                                       {
                                          Message = "密碼需為數字及英文字";
                                       }
                                   }

                                   //判斷 密碼不得為連續數字或英文字(大小寫視為不同字元)
                                   if (boolIsEqual)
                                   {
                                       boolIsEqual = this.CheckLetter12345(newPwss);
                                       if (!boolIsEqual)
                                       {
                                          Message = "密碼不得為連續數字或英文字";
                                       }
                                   }

                                   //判斷 密碼不得為相同數字或英文字(大小寫視為不同字元)
                                   if (boolIsEqual)
                                   {
                                       boolIsEqual = this.CheckLetterOfDigitEqual(newPwss);
                                       if (!boolIsEqual)
                                       {
                                          Message = "密碼不得為相同數字或英文字";
                                       }
                                   }

                                   //判斷 密碼不得為「使用者代碼」的子字串
                                   if (boolIsEqual)
                                   {
                                       boolIsEqual = this.CheckUserIdEqual(newPwss);
                                       if (!boolIsEqual)
                                       {
                                          Message = "密碼不得為「使用者代碼」的子字串";
                                       }
                                   }

                                   //判斷 密碼不得為「特約商店代號」的子字串
                                   if (boolIsEqual)
                                   {
                                       boolIsEqual = this.CheckMerchantIdEqual(newPwss);
                                       if (!boolIsEqual)
                                       {
                                          Message = "密碼不得為「特約商店代號」的子字串";
                                       }
                                   }

                                   //密碼不得與前N次同
                                   if (boolIsEqual)
                                   {
                                	   //20220801 change MD5 to SHA256
                                       String strPwssChk = this.getMsgDigestPwd(newPwss);
                                       String strPwssChkSHA256 = Util.SHA256(Util.getPwdfactor(Data_User_Id,newPwss));
                                       boolean blTheSame = false;
                                       for (int i = 1; i <= Sys_Pram_Mer_His_Count; i++)
                                       {
                                    	   blTheSame = false;
                                           String strName = pwssHisName + i;
                                           String strPwssHis = String.valueOf(hashData.get(strName));
                                           if(strPwssHis.length()==32) {
                                        	   if (getMsgDigestIsEqual(strPwssChk, strPwssHis)) blTheSame = true;
                                           }else {
                                        	   //SHA256
                                        	   if (getMsgDigestIsEqual(strPwssChkSHA256, strPwssHis)) blTheSame = true;
                                           }
                                           
                                           if (blTheSame==true)
                                           {
                                               boolIsEqual = false;
                                               Message = "密碼不得與前 " + Sys_Pram_Mer_His_Count + "次同";
                                               break;
                                           }
                                       }
                                   }

                                   //密碼不得為常用字串
                                   if (boolIsEqual)
                                   {
                                       if (Sys_Pram_Mer_Pwd_Common != null)
                                       {
                                           Enumeration K = Sys_Pram_Mer_Pwd_Common.keys();
                                           while (K.hasMoreElements())
                                           {
                                               if (String.valueOf(newPwss).equalsIgnoreCase(String.valueOf(
                                                       Sys_Pram_Mer_Pwd_Common.get(K.nextElement()))))
                                               {
                                                   boolIsEqual = false;
                                                   Message = "密碼不得為常用字串";
                                               }
                                           }
                                       }
                                   }

                                   //修改密碼 DB
                                   if (boolIsEqual)
                                   {
                               	       //20220801 change MD5 to SHA256
                                       //newPwss = this.getMsgDigestPwd(newPwss);
                               	       newPwss = Util.getPwdfactor(Data_User_Id,newPwss);
                                       newPwss = Util.SHA256(newPwss);

                                        String cnt = getUserPwdCnt(arrayData); //密碼更新指標
                                        String strName = pwssHisName + cnt;

                                        boolean boolChkPwss = false; //使用者不可刪除或停用...等
                                        String strData = hashData.get("DEL_FLAG") == null ? "" : hashData.get("DEL_FLAG").toString();
                                        if (strData.length() == 0 || "Y".equals(strData))
                                        {
                                            Message = "使用者已刪除請洽系統管理者。";
                                        }
                                        else
                                        {
                                            strData = hashData.get("USER_STATUS") == null ? "" : hashData.get("USER_STATUS").toString();
                                            if ("E".equalsIgnoreCase(strData))
                                            {
                                                Message = "使用者已停用請洽系統管理者。";
                                            }
                                            else
                                            {
                                                boolChkPwss = true;
                                            }
                                        }

                                        if (boolChkPwss)
                                        {
                                            boolAction = true;
                                            boolForwardAction = MCPBean.updateDb_UserPwd(Data_Merchant_Id, Data_User_Id,
                                                    newPwss, cnt, strName, String.valueOf(Sys_Pram_Mer_Expire_Date));

                                            if (boolForwardAction)
                                            {
                                                Message = "密碼變更成功";
                                                LogStatus = "成功";

                                            }
                                            else
                                            {
                                                Message = "密碼變更失敗";
                                            }
                                        }
                                   }
                               }
                           }
                           else
                           {
                               Message = "無法取得使用者資料，請稍後再試或洽系統管理者。";
                           }
                       }

                       LogMemo = Message;
                       LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                       LogData = UserBean.get_LogData(LogMerchantID,LogUserName, LogFunctionName, LogStatus, LogMemo);
                       log_user.debug(LogData);
                   }

                   //頁面路徑
                   if (changePwssFlag != null && "Y".equals(changePwssFlag))
                   {
                       if (boolForwardAction)
                       {
                           boolsendRedirect = true;
                           Forward = "./Merchant_Index.jsp";
                       }
                       else
                       {
                           Forward = "./Merchant_ChangePwdOld.jsp";
                       }
                   }
                   else
                   {
                       Forward = "./Merchant_ChangePwd.jsp";
                       if(boolAction)
                       {
                          if (boolForwardAction)
                          {
                              Forward = "./Merchant_Response.jsp";
                          }
                       }
                   }
               }
               else
               {
                   Message = "特約商店無此權限請洽本行處理";
                   Forward = "./Merchant_NoUse.jsp";
                   LogMemo = Message;
                   LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                   LogData = UserBean.get_LogData(LogMerchantID,LogUserName, LogFunctionName, LogStatus, LogMemo);
                   log_user.debug(LogData);
               }
            }

            session.setAttribute("Message", Message);
            System.out.println("Forward=" + Forward);
            if (boolsendRedirect)
            {
                 response.sendRedirect(Forward);
            }
            else
            {
                 request.getRequestDispatcher(Forward).forward(request, response);
            }
        }
        catch (Exception e)
        {
            log_systeminfo.debug("--MerchantChangePwdCtl--"+e.toString());
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

    public String getMessage()
    {
       return Message;
    }

    public int getMER_PWD_HISCNT()
    {
       return Sys_Pram_Mer_His_Count;
    }

    public int getMER_PWD_ORDERCNT()
    {
       return Sys_Pram_Mer_Order_Count;
    }

    public int getMER_PWD_EQUALSCNT()
    {
       return Sys_Pram_Mer_Equals_Count;
    }

    public int getMER_PWD_MINLEN()
    {
       return Sys_Pram_Mer_Pwd_Minlen;
    }

    public int getMER_PWD_MAXLEN()
    {
       return Sys_Pram_Mer_Pwd_Maxlen;
    }

    public int getMER_PWD_COMMON_CNT()
    {
       return Sys_Pram_Mer_Pwd_Common_Cnt;
    }

    public Hashtable getMER_PWD_COMMON()
    {
       return Sys_Pram_Mer_Pwd_Common;
    }

    public int getMER_PWD_EXPIRE_DATE()
    {
       return Sys_Pram_Mer_Expire_Date;
    }

    public boolean setDefaultDbData()
    {
        String showErrorName = "";
        String showName = "";
        try{
            showErrorName = "MER_PWD_HISCNT";
            showName = String.valueOf(hashSys.get("MER_PWD_HISCNT"));
            Sys_Pram_Mer_His_Count = Integer.parseInt(showName);

            showErrorName = "MER_PWD_ORDERCNT";
            showName = String.valueOf(hashSys.get("MER_PWD_ORDERCNT"));
            Sys_Pram_Mer_Order_Count = Integer.parseInt(showName);

            showErrorName = "MER_PWD_EQUALSCNT";
            showName = String.valueOf(hashSys.get("MER_PWD_EQUALSCNT"));
            Sys_Pram_Mer_Equals_Count = Integer.parseInt(showName);

            showErrorName = "MER_PWD_MINLEN";
            showName = String.valueOf(hashSys.get("MER_PWD_MINLEN"));
            Sys_Pram_Mer_Pwd_Minlen = Integer.parseInt(showName);

            showErrorName = "MER_PWD_MAXLEN";
            showName = String.valueOf(hashSys.get("MER_PWD_MAXLEN"));
            Sys_Pram_Mer_Pwd_Maxlen = Integer.parseInt(showName);

            showErrorName = "MER_PWD_COMMON_CNT";
            showName = String.valueOf(hashSys.get("MER_PWD_COMMON_CNT"));
            Sys_Pram_Mer_Pwd_Common_Cnt = Integer.parseInt(showName);

            Hashtable hashCommon = new Hashtable();
            for(int i=0;i<Sys_Pram_Mer_Pwd_Common_Cnt;i++)
            {
                showErrorName = pwssCommonName+i;  //MER_PWD_COMMON_
                showName = String.valueOf(hashSys.get(pwssCommonName+i));
                if(showName != null && showName.length() > 0)
                {
                   hashCommon.put(showName,showName);
                }
            }

            Sys_Pram_Mer_Pwd_Common = hashCommon;

            showErrorName = "MER_PWD_DAY";
            showName = String.valueOf(hashSys.get("MER_PWD_DAY"));
            Sys_Pram_Mer_Expire_Date = Integer.parseInt(showName); //密碼有效期限

        }
        catch(Exception e)
        {
            System.out.println("MerchantCheckPwdCtl.setDefaultDbData()");
            System.out.println(showErrorName + " 的資料型態不正確 = [" + showName +"]");
            System.out.println(e.getMessage());
            log_systeminfo.debug("--MerchantChangePwdCtl--"+e.toString());
            return false;
        }

        return true;
    }

    public void setDefaultContextData(HttpSession session)
    {
        String showErrorName = "";
        String showName = "";
        try
        {
            ServletContext context = session.getServletContext();
            showErrorName = "MER_PWD_ORDERCNT";
            showName = context.getInitParameter("MER_PWD_ORDERCNT");
            Sys_Pram_Mer_Order_Count = Integer.parseInt(showName);

            showErrorName = "MER_PWD_EQUALSCNT";
            showName = context.getInitParameter("MER_PWD_EQUALSCNT");
            Sys_Pram_Mer_Equals_Count = Integer.parseInt(showName);

            showErrorName = "MER_PWD_MINLEN";
            showName = context.getInitParameter("MER_PWD_MINLEN");
            Sys_Pram_Mer_Pwd_Minlen = Integer.parseInt(showName);

            showErrorName = "MER_PWD_MAXLEN";
            showName = context.getInitParameter("MER_PWD_MAXLEN");
            Sys_Pram_Mer_Pwd_Maxlen = Integer.parseInt(showName);

            showErrorName = "MER_PWD_COMMON_CNT";
            showName = String.valueOf(hashSys.get("MER_PWD_COMMON_CNT"));
            Sys_Pram_Mer_Pwd_Common_Cnt = Integer.parseInt(showName);

            Hashtable hashCommon = new Hashtable();
            for (int i=0;i<Sys_Pram_Mer_Pwd_Common_Cnt;i++)
            {
                showErrorName = pwssCommonName+i;  //MER_PWD_COMMON_
                showName = String.valueOf(hashSys.get(pwssCommonName+i));
                if(showName != null && showName.length() > 0)
                {
                   hashCommon.put(showName,showName);
                }
            }
            Sys_Pram_Mer_Pwd_Common = hashCommon;

            showErrorName = "MER_PWD_DAY";
            showName = context.getInitParameter("MER_PWD_DAY");
            Sys_Pram_Mer_Expire_Date = Integer.parseInt(showName); //密碼有效期限
        }
        catch(Exception e)
        {
            System.out.println("MerchantCheckPwdCtl.setDefaultContextData()");
            System.out.println(showErrorName + " 的資料型態不正確 = [" + showName +"]");
            System.out.println(e.getMessage());
            log_systeminfo.debug("--MerchantChangePwdCtl--"+e.toString());
        }
    }

    /**
     * <p>檢核 密碼欄位無資料</p>
     * @version 0.1 2007/09/21  Caspar Chen
     * @param Pwd   密碼
     * @return boolean
     */
    public static boolean CheckPwdNoData(String Pwss)
    {
        if (Pwss == null || Pwss.trim().length() == 0)
        {
            return  false;
        }

        return true;
    }

    /**
     * <p>檢核 使用者舊密碼與密碼是否一致</p>
     * @version 0.1 2007/09/21  Caspar Chen
     * @param  arrayData  使用者資料
     * @param  PW   密碼
     * @return boolean
     */
    public boolean CheckOldPwd(ArrayList arrayData, String PW) throws Exception
    {
    	//20220801 HKP MD5雜錯長度為32，SHA256雜錯長度為64，以此長度判斷是否為MD5，變更後的密碼一律使用SHA256
    	String strUserId = "";
        String strOldMsgPW = ""; //舊密碼(DB)
        boolean blSHA256 = true;

        Hashtable hashData = (Hashtable)arrayData.get(0);
        strUserId = Util.objToStrTrim(hashData.get("USER_ID"));
        if (hashData.get("USER_PWD") != null)
        {
            strOldMsgPW = Util.objToStrTrim(hashData.get("USER_PWD"));
            if(strOldMsgPW.length()==32) blSHA256 = false; //20220801
        }

        String strOldMsgPWInput = ""; //密碼
        if(blSHA256) {
        	PW = Util.getPwdfactor(strUserId,PW);
        	strOldMsgPWInput = Util.SHA256(PW);
        }
        else {
        	strOldMsgPWInput = this.getMsgDigestPwd(PW);
        }

        return getMsgDigestIsEqual(strOldMsgPW, strOldMsgPWInput);
    }

    /**
     * <p>檢核 可輸入長度</p>
     * @version 0.1 2007/09/21  Caspar Chen
     * @param intNewPW     新密碼長度
     * @param intConfirm   新密碼確認長度
     * @return boolean
     */
    public boolean CheckInputLength(int intNewPW)
    {
        if (intNewPW < Sys_Pram_Mer_Pwd_Minlen || intNewPW > Sys_Pram_Mer_Pwd_Maxlen)
        {
            return false;
        }

        return true;
    }

    /**
     * <p>檢核 密碼需為數字及英文字</p>
     * @version 0.1 2007/09/21  Caspar Chen
     * @param strNewPW     新密碼
     * @return boolean
     */
    public boolean CheckLetterOfDigit(String strNewPW)
    {
        boolean boolDigit = false;
        boolean boolLowerCase = false;
        boolean boolUpperCase = false;
        for (int i = 0; i < strNewPW.length(); i++)
        {
            char charNewPW = strNewPW.charAt(i);
            if (Character.isDigit(charNewPW))
            {
                boolDigit = true;
            }

            if (Character.isLowerCase(charNewPW))
            {
                boolLowerCase = true;
            }

            if (Character.isUpperCase(charNewPW))
            {
                boolUpperCase = true;
            }
        }

        if (!boolDigit || ( !boolLowerCase && !boolUpperCase))
        {
            return false;
        }

        return true;
    }

    /**
     * <p>檢核 密碼不得為連續數字或英文字(大小寫視為不同字元)</p>
     * @version 0.1 2007/09/21  Caspar Chen
     * @param strNewPW     新密碼
     * @return boolean
     */
    public boolean CheckLetter12345(String strNewPW)
    {
        String strNumberCase = "0"; //0=預設, 1=順向, 2=逆向
        int intNumber = -1;
        int intNumberNew = -1;
        int intNumberCountNew = 1; //預設初始值
        int intNumberCount = intNumberCountNew; //連續字串數量
        int intNumberCountCk = Sys_Pram_Mer_Order_Count; //參數-最大連續字串數量

        for (int i = 0; i < strNewPW.length(); i++)
        {
            char charNewPW = strNewPW.charAt(i);
            intNumber = Character.getNumericValue(charNewPW);
            if ((i + 1) < strNewPW.length())
            {
                char charNewPWCase = strNewPW.charAt(i + 1);
                intNumberNew = Character.getNumericValue(charNewPWCase);

                //非正確字元
                if (intNumber == -1 || intNumberNew == -1)
                {
                    strNumberCase = "0";
                }
                else
                {
                    boolean boolIsDigitCase = true;
                    //數字、大小寫英文字
                    if (!((Character.isDigit(charNewPW)     && Character.isDigit(charNewPWCase)) ||
                          (Character.isLowerCase(charNewPW) && Character.isLowerCase(charNewPWCase)) ||
                          (Character.isUpperCase(charNewPW) && Character.isUpperCase(charNewPWCase))))
                    {
                        boolIsDigitCase = false;
                    }

                    //判斷 連續
                    if (boolIsDigitCase)
                    {
                        //是否連續
                        if (strNumberCase.equals("0"))
                        {
                            if (intNumber + 1 == intNumberNew)
                            {
                                strNumberCase = "1";
                            }
                            else if (intNumber - 1 == intNumberNew)
                            {
                                strNumberCase = "2";
                            }
                            else
                            {
                                strNumberCase = "0";
                            }
                        }
                        else if (strNumberCase.equals("1"))
                        {
                            //順向連續
                            if (!(intNumber + 1 == intNumberNew))
                            {
                                strNumberCase = "0";
                            }
                        }
                        else if (strNumberCase.equals("2"))
                        {
                            //逆向連續
                            if (!(intNumber - 1 == intNumberNew))
                            {
                                strNumberCase = "0";
                            }
                        }
                    }
                    else
                    {
                        strNumberCase = "0";
                    }

                    //判斷 密碼不得為連續數字或英文字
                    if (strNumberCase.equals("0"))
                    {
                        intNumberCount = intNumberCountNew;
                    }
                    else
                    {
                        intNumberCount++;
                        if (intNumberCount > intNumberCountCk)
                        {
                            return false;
                        }
                    }
                }
            }
            else
            {
                break;
            }
        }

        return true;
    }

    /**
     * <p>檢核 密碼不得為相同數字或英文字(大小寫視為不同字元)</p>
     * @version 0.1 2007/09/21  Caspar Chen
     * @param strNewPW     新密碼
     * @return boolean
     */
    public boolean CheckLetterOfDigitEqual(String strNewPW)
    {
        int intNumber = -1;
        int intNumberNew = -1;
        StringBuffer strDigit = new StringBuffer(strNewPW.length());
        StringBuffer strLowerCase = new StringBuffer(strNewPW.length());
        StringBuffer strUpperCase = new StringBuffer(strNewPW.length());
        char charNewPW = " ".charAt(0);

        for (int i = 0; i < strNewPW.length(); i++)
        {
            if (i == 0)
            {
                charNewPW = strNewPW.charAt(i);
                strDigit.append(charNewPW);
                strLowerCase.append(charNewPW);
                strUpperCase.append(charNewPW);
            }
            else
            {
                intNumber = Character.getNumericValue(charNewPW);
                char charNewPWCase = strNewPW.charAt(i);
                intNumberNew = Character.getNumericValue(charNewPWCase);

                //非正確字元
                if (intNumber == -1 || intNumberNew == -1)
                {
                    //
                }
                else
                {
                    if (intNumber == intNumberNew)
                    {
                        //數字、大小寫英文字
                        if (Character.isDigit(charNewPW) &&
                            Character.isDigit(charNewPWCase))
                        {
                           strDigit.append(charNewPW);

                           strLowerCase = new StringBuffer(strNewPW.length());
                           strUpperCase = new StringBuffer(strNewPW.length());

                           strLowerCase.append(charNewPWCase);
                           strUpperCase.append(charNewPWCase);
                        }
                        else if (Character.isLowerCase(charNewPW) && Character.isLowerCase(charNewPWCase))
                        {
                           strLowerCase.append(charNewPW);

                           strDigit = new StringBuffer(strNewPW.length());
                           strUpperCase = new StringBuffer(strNewPW.length());

                           strDigit.append(charNewPWCase);
                           strUpperCase.append(charNewPWCase);

                        }
                        else if (Character.isUpperCase(charNewPW) && Character.isUpperCase(charNewPWCase))
                        {
                           strUpperCase.append(charNewPW);

                           strDigit = new StringBuffer(strNewPW.length());
                           strLowerCase = new StringBuffer(strNewPW.length());

                           strDigit.append(charNewPWCase);
                           strLowerCase.append(charNewPWCase);
                        }
                    }
                    else
                    {
                        strDigit = new StringBuffer(strNewPW.length());
                        strLowerCase = new StringBuffer(strNewPW.length());
                        strUpperCase = new StringBuffer(strNewPW.length());
                        strDigit.append(charNewPWCase);
                        strLowerCase.append(charNewPWCase);
                        strUpperCase.append(charNewPWCase);
                    }

                    charNewPW = charNewPWCase;

                   //判斷 密碼不得為相同數字或英文字
                   if (strDigit.length() > Sys_Pram_Mer_Equals_Count ||
                       strLowerCase.length() > Sys_Pram_Mer_Equals_Count ||
                       strUpperCase.length() > Sys_Pram_Mer_Equals_Count)
                   {
                      return false;
                   }
                }
            }
        }
        return true;
    }

    /**
     * <p>檢核 密碼不得為「使用者代碼」的子字串</p>
     * @version 0.1 2007/09/21  Caspar Chen
     * @param strNewPW     新密碼
     * @return boolean
     */
    public boolean CheckUserIdEqual(String strNewPW)
    {
        if (strNewPW.equals(Data_User_Id) || strNewPW.indexOf(Data_User_Id) >= 0)
        {
            return false;
        }

        return true;
    }

    /**
     * <p>檢核 密碼不得為「特約商店代號」的子字串</p>
     * @version 0.1 2007/09/21  Caspar Chen
     * @param strNewPW     新密碼
     * @return boolean
     */
    public boolean CheckMerchantIdEqual(String strNewPW)
    {
        if (strNewPW.equals(Data_Merchant_Id) || strNewPW.indexOf(Data_Merchant_Id) >= 0)
        {
            return false;
        }

        return true;
    }

    /**
     * <p>取得編碼字串</p>
     * @version 0.1 2007/09/21  Caspar Chen
     * @param msgPsw   字串
     * @return String
     */

    public static String getMsgDigestPwd(String msgPsw) throws Exception
    {
        MessageDigest md = MessageDigest.getInstance("MD5");

        md.update(msgPsw.getBytes());//字串編瑪

        byte[] digest = md.digest();
        final StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < digest.length; ++i)
        {
            final byte b = digest[i];
            final int value = (b & 0x7F) + (b < 0 ? 128 : 0);
            buffer.append(value < 16 ? "0" : "");
            buffer.append(Integer.toHexString(value));
        }

        return buffer.toString();
    }

    /**
     * <p>檢核編碼字串相同</p>
     * @version 0.1 2007/09/21  Caspar Chen
     * @param oldMsgPsw   編瑪字串
     * @param newMsgPsw   編瑪字串
     * @return boolean
     */
    public boolean getMsgDigestIsEqual(String oldMsgPsw, String newMsgPsw) throws Exception
    {
        byte[] byteOldMsgPsw = oldMsgPsw.getBytes();
        byte[] byteNewMsgPsw = newMsgPsw.getBytes();

        return MessageDigest.isEqual(byteOldMsgPsw, byteNewMsgPsw);
    }

    /**
     * <p>取得密碼更新指標</p>
     * @version 0.1 2007/10/12  Caspar Chen
     * @param  arrayData  使用者資料
     * @return String
     */
    private String getUserPwdCnt(ArrayList arrayData)
    {
        String cnt = "0";
        if (arrayData != null && arrayData.size() > 0)
        {
            Hashtable hashData = (Hashtable) arrayData.get(0);
            String strPCnt = (String) hashData.get("PWD_CNT");
            if(strPCnt == null)
            {
                cnt = "0";
            }
            else
            {
                cnt = strPCnt;
            }
        }
        try
        {
            cnt = String.valueOf(Integer.parseInt(cnt) + 1);
            if (Integer.parseInt(cnt) > Sys_Pram_Mer_His_Count)
            {
                cnt = "1";
            }
        }
        catch (Exception e)
        {
            cnt = "1";
        }

        return cnt;
    }
}
