/************************************************************
 * <p>#File Name:       Merchant_ChangePwdCtl.java  </p>
 * <p>#Description:                 </p>
 * <p>#Create Date:     2007/09/26      </p>
 * <p>#Company:         cybersoft       </p>
 * <p>#Notice:                      </p>
 * @author      Caspar Chen
 * @since       SPEC version
 * @version 0.1 2007/09/26  Caspar Chen
 * 202208090854-01 20220801 HKP PCI-DSS�󴫱K�X����MD5��SHA256�AMD5�������׬�32�ASHA256�������׬�64�A�H�����קP�_�O�_��MD5�A�ܧ�᪺�K�X�@�ߨϥ�SHA256�PLOG�B��
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
 * <p>����K�X�ܧ�Servlet</p>
 * @version 0.1 2007/09/26  Caspar Chen
 */
public class MerchantChangePwdCtl extends HttpServlet
{
    private static final String CONTENT_TYPE = "text/html; charset=Big5";
    private String Forward = ""; // ������}
    private String Message = ""; // ��ܰT��

    //�t�ΰѼ�
    private int Sys_Pram_Mer_His_Count = 10;   //�K�X��s���г̤j��
    private int Sys_Pram_Mer_Order_Count = 4;  //�K�X�i�s���
    private int Sys_Pram_Mer_Equals_Count = 4; //�K�X�i�ۦP��
    private int Sys_Pram_Mer_Pwd_Minlen = 6;   //�K�X�̤p����
    private int Sys_Pram_Mer_Pwd_Maxlen = 16;  //�K�X�̤j����
    private int Sys_Pram_Mer_Pwd_Common_Cnt = 0;  //�K�X�`�Φr��w�إ߼�
    private Hashtable Sys_Pram_Mer_Pwd_Common = new Hashtable();  //�K�X�`�Φr��
    private int Sys_Pram_Mer_Expire_Date = 60; //�K�X���Ĵ���

    //�Ѽ�
    private final String pwssHisName = "PWD_HIS"; //���v�K�X���W��
    private final String pwssCommonName = "MER_PWD_COMMON_"; //�K�X�`�Φr��W��

    //�ϥΪ̸��
    private String Data_Merchant_Id = "";
    private String Data_User_Id = "";

    //session���
    private Hashtable hashSys = new Hashtable(); // �t�ΰѼ�
    private Hashtable hashMerUser = new Hashtable(); // �S���ө��ϥΪ�

    MerchantChangePwdBean MCPBean;
    public UserBean UserBean = new UserBean();
    public DataBaseBean2 sysBean = new DataBaseBean2();
    
    java.util.Date nowdate;
    public static final LogUtils log_user = new LogUtils("user");
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    String LogUserName = "";
    String LogFunctionName = "�K�X�ܧ�";
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
            Message = ""; // ��ܰT��
            //������J���
            boolean boolsendRedirect = false;  // ������}
            boolean boolForwardAction = false;
            boolean boolAction = false; //�O�_�i�J�ק�K�X
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
                       Message = "�L�k���o�n�J���";
                       boolIsEqual = false;
                   }
                   else
                   {
                       hashSys = (Hashtable) hashConfData.get("SYSCONF");
                       hashMerUser = (Hashtable) hashConfData.get("MERCHANT_USER");

                       // �t�ΰѼƸ��
                       if (hashSys == null || hashSys.size() == 0)
                       {
                          Message = "�L�k���o�n�J���-�t�ΰѼƸ��";
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

                       // �S���ө��ϥΪ̸��
                       if (hashMerUser == null || hashMerUser.size() == 0)
                       {
                          Message = "�L�k���o�n�J���-�S���ө��ϥΪ̸��";
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
                           Message = "�±K�X���L���";
                       }

                       if (boolInputDataEqual)
                       {
                          boolInputDataEqual = this.CheckPwdNoData(newPwss);
                          if (!boolInputDataEqual)
                          {
                             Message = "�s�K�X���L���";
                          }
                       }

                       if (boolInputDataEqual)
                       {
                          boolInputDataEqual = this.CheckPwdNoData(confirm);
                          if (!boolInputDataEqual)
                          {
                             Message = "�T�{�s�K�X���L���";
                          }
                       }

                       //�K�X�ܧ�
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
                                   //�ˮ� �ϥΪ̱K�X�O�_�@�P
                                   if (boolIsEqual)
                                   {
                                       try
                                       {
                                    	   //20220801 change CheckOldPwd function,MD5 and SHA256 check
                                           boolIsEqual = this.CheckOldPwd(arrayData, oldPwss.trim());
                                           if (!boolIsEqual)
                                           {
                                               Message = "�ϥΪ��±K�X���@�P";
                                           }

                                           if (boolIsEqual)
                                           {
                                               boolIsEqual = !this.CheckOldPwd(arrayData, newPwss.trim());
                                               if (!boolIsEqual)
                                               {
                                                  Message = "�s�K�X���i�P�±K�X�@��";
                                               }
                                           }
                                       }
                                       catch (Exception e)
                                       {
                                           log_systeminfo.debug("--MerchantChangePwdCtl--"+e.toString());
                                           boolIsEqual = false;
                                           Message = "�K�X�s�X���ѵL�k�ˮ�";
                                       }
                                   }

                                   //�ˮ� �s�K�X������T�{�s�K�X
                                   if (boolIsEqual)
                                   {
                                       boolIsEqual = newPwss.equals(confirm);
                                       if (!boolIsEqual)
                                       {
                                           Message = "�s�K�X������T�{�s�K�X";
                                       }
                                   }

                                   //�ˮ� �s�K�X���פ�����T�{�s�K�X����
                                   if (boolIsEqual)
                                   {
                                       if (newPwss.length() == confirm.length())
                                       {
                                           boolIsEqual = true;
                                       }
                                       else
                                       {
                                           boolIsEqual = false;
                                           Message = "�s�K�X���פ�����T�{�s�K�X����";
                                       }
                                   }

                                   //�ˮ� �i��J����
                                   if (boolIsEqual)
                                   {
                                       boolIsEqual = this.CheckInputLength(newPwss.length());
                                       if (!boolIsEqual)
                                       {
                                           Message = "�K�X���׻ݬ� " + this.Sys_Pram_Mer_Pwd_Minlen +
                                                    " �� " + this.Sys_Pram_Mer_Pwd_Maxlen + " ��";
                                       }
                                   }

                                   //�P�_ �K�X�ݬ��Ʀr�έ^��r
                                   if (boolIsEqual)
                                   {
                                       boolIsEqual = this.CheckLetterOfDigit(newPwss);
                                       if (!boolIsEqual)
                                       {
                                          Message = "�K�X�ݬ��Ʀr�έ^��r";
                                       }
                                   }

                                   //�P�_ �K�X���o���s��Ʀr�έ^��r(�j�p�g�������P�r��)
                                   if (boolIsEqual)
                                   {
                                       boolIsEqual = this.CheckLetter12345(newPwss);
                                       if (!boolIsEqual)
                                       {
                                          Message = "�K�X���o���s��Ʀr�έ^��r";
                                       }
                                   }

                                   //�P�_ �K�X���o���ۦP�Ʀr�έ^��r(�j�p�g�������P�r��)
                                   if (boolIsEqual)
                                   {
                                       boolIsEqual = this.CheckLetterOfDigitEqual(newPwss);
                                       if (!boolIsEqual)
                                       {
                                          Message = "�K�X���o���ۦP�Ʀr�έ^��r";
                                       }
                                   }

                                   //�P�_ �K�X���o���u�ϥΪ̥N�X�v���l�r��
                                   if (boolIsEqual)
                                   {
                                       boolIsEqual = this.CheckUserIdEqual(newPwss);
                                       if (!boolIsEqual)
                                       {
                                          Message = "�K�X���o���u�ϥΪ̥N�X�v���l�r��";
                                       }
                                   }

                                   //�P�_ �K�X���o���u�S���ө��N���v���l�r��
                                   if (boolIsEqual)
                                   {
                                       boolIsEqual = this.CheckMerchantIdEqual(newPwss);
                                       if (!boolIsEqual)
                                       {
                                          Message = "�K�X���o���u�S���ө��N���v���l�r��";
                                       }
                                   }

                                   //�K�X���o�P�eN���P
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
                                               Message = "�K�X���o�P�e " + Sys_Pram_Mer_His_Count + "���P";
                                               break;
                                           }
                                       }
                                   }

                                   //�K�X���o���`�Φr��
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
                                                   Message = "�K�X���o���`�Φr��";
                                               }
                                           }
                                       }
                                   }

                                   //�ק�K�X DB
                                   if (boolIsEqual)
                                   {
                               	       //20220801 change MD5 to SHA256
                                       //newPwss = this.getMsgDigestPwd(newPwss);
                               	       newPwss = Util.getPwdfactor(Data_User_Id,newPwss);
                                       newPwss = Util.SHA256(newPwss);

                                        String cnt = getUserPwdCnt(arrayData); //�K�X��s����
                                        String strName = pwssHisName + cnt;

                                        boolean boolChkPwss = false; //�ϥΪ̤��i�R���ΰ���...��
                                        String strData = hashData.get("DEL_FLAG") == null ? "" : hashData.get("DEL_FLAG").toString();
                                        if (strData.length() == 0 || "Y".equals(strData))
                                        {
                                            Message = "�ϥΪ̤w�R���Ь��t�κ޲z�̡C";
                                        }
                                        else
                                        {
                                            strData = hashData.get("USER_STATUS") == null ? "" : hashData.get("USER_STATUS").toString();
                                            if ("E".equalsIgnoreCase(strData))
                                            {
                                                Message = "�ϥΪ̤w���νЬ��t�κ޲z�̡C";
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
                                                Message = "�K�X�ܧ󦨥\";
                                                LogStatus = "���\";

                                            }
                                            else
                                            {
                                                Message = "�K�X�ܧ󥢱�";
                                            }
                                        }
                                   }
                               }
                           }
                           else
                           {
                               Message = "�L�k���o�ϥΪ̸�ơA�еy��A�թά��t�κ޲z�̡C";
                           }
                       }

                       LogMemo = Message;
                       LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                       LogData = UserBean.get_LogData(LogMerchantID,LogUserName, LogFunctionName, LogStatus, LogMemo);
                       log_user.debug(LogData);
                   }

                   //�������|
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
                   Message = "�S���ө��L���v���Ь�����B�z";
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
            Sys_Pram_Mer_Expire_Date = Integer.parseInt(showName); //�K�X���Ĵ���

        }
        catch(Exception e)
        {
            System.out.println("MerchantCheckPwdCtl.setDefaultDbData()");
            System.out.println(showErrorName + " ����ƫ��A�����T = [" + showName +"]");
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
            Sys_Pram_Mer_Expire_Date = Integer.parseInt(showName); //�K�X���Ĵ���
        }
        catch(Exception e)
        {
            System.out.println("MerchantCheckPwdCtl.setDefaultContextData()");
            System.out.println(showErrorName + " ����ƫ��A�����T = [" + showName +"]");
            System.out.println(e.getMessage());
            log_systeminfo.debug("--MerchantChangePwdCtl--"+e.toString());
        }
    }

    /**
     * <p>�ˮ� �K�X���L���</p>
     * @version 0.1 2007/09/21  Caspar Chen
     * @param Pwd   �K�X
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
     * <p>�ˮ� �ϥΪ��±K�X�P�K�X�O�_�@�P</p>
     * @version 0.1 2007/09/21  Caspar Chen
     * @param  arrayData  �ϥΪ̸��
     * @param  PW   �K�X
     * @return boolean
     */
    public boolean CheckOldPwd(ArrayList arrayData, String PW) throws Exception
    {
    	//20220801 HKP MD5�������׬�32�ASHA256�������׬�64�A�H�����קP�_�O�_��MD5�A�ܧ�᪺�K�X�@�ߨϥ�SHA256
    	String strUserId = "";
        String strOldMsgPW = ""; //�±K�X(DB)
        boolean blSHA256 = true;

        Hashtable hashData = (Hashtable)arrayData.get(0);
        strUserId = Util.objToStrTrim(hashData.get("USER_ID"));
        if (hashData.get("USER_PWD") != null)
        {
            strOldMsgPW = Util.objToStrTrim(hashData.get("USER_PWD"));
            if(strOldMsgPW.length()==32) blSHA256 = false; //20220801
        }

        String strOldMsgPWInput = ""; //�K�X
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
     * <p>�ˮ� �i��J����</p>
     * @version 0.1 2007/09/21  Caspar Chen
     * @param intNewPW     �s�K�X����
     * @param intConfirm   �s�K�X�T�{����
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
     * <p>�ˮ� �K�X�ݬ��Ʀr�έ^��r</p>
     * @version 0.1 2007/09/21  Caspar Chen
     * @param strNewPW     �s�K�X
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
     * <p>�ˮ� �K�X���o���s��Ʀr�έ^��r(�j�p�g�������P�r��)</p>
     * @version 0.1 2007/09/21  Caspar Chen
     * @param strNewPW     �s�K�X
     * @return boolean
     */
    public boolean CheckLetter12345(String strNewPW)
    {
        String strNumberCase = "0"; //0=�w�], 1=���V, 2=�f�V
        int intNumber = -1;
        int intNumberNew = -1;
        int intNumberCountNew = 1; //�w�]��l��
        int intNumberCount = intNumberCountNew; //�s��r��ƶq
        int intNumberCountCk = Sys_Pram_Mer_Order_Count; //�Ѽ�-�̤j�s��r��ƶq

        for (int i = 0; i < strNewPW.length(); i++)
        {
            char charNewPW = strNewPW.charAt(i);
            intNumber = Character.getNumericValue(charNewPW);
            if ((i + 1) < strNewPW.length())
            {
                char charNewPWCase = strNewPW.charAt(i + 1);
                intNumberNew = Character.getNumericValue(charNewPWCase);

                //�D���T�r��
                if (intNumber == -1 || intNumberNew == -1)
                {
                    strNumberCase = "0";
                }
                else
                {
                    boolean boolIsDigitCase = true;
                    //�Ʀr�B�j�p�g�^��r
                    if (!((Character.isDigit(charNewPW)     && Character.isDigit(charNewPWCase)) ||
                          (Character.isLowerCase(charNewPW) && Character.isLowerCase(charNewPWCase)) ||
                          (Character.isUpperCase(charNewPW) && Character.isUpperCase(charNewPWCase))))
                    {
                        boolIsDigitCase = false;
                    }

                    //�P�_ �s��
                    if (boolIsDigitCase)
                    {
                        //�O�_�s��
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
                            //���V�s��
                            if (!(intNumber + 1 == intNumberNew))
                            {
                                strNumberCase = "0";
                            }
                        }
                        else if (strNumberCase.equals("2"))
                        {
                            //�f�V�s��
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

                    //�P�_ �K�X���o���s��Ʀr�έ^��r
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
     * <p>�ˮ� �K�X���o���ۦP�Ʀr�έ^��r(�j�p�g�������P�r��)</p>
     * @version 0.1 2007/09/21  Caspar Chen
     * @param strNewPW     �s�K�X
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

                //�D���T�r��
                if (intNumber == -1 || intNumberNew == -1)
                {
                    //
                }
                else
                {
                    if (intNumber == intNumberNew)
                    {
                        //�Ʀr�B�j�p�g�^��r
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

                   //�P�_ �K�X���o���ۦP�Ʀr�έ^��r
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
     * <p>�ˮ� �K�X���o���u�ϥΪ̥N�X�v���l�r��</p>
     * @version 0.1 2007/09/21  Caspar Chen
     * @param strNewPW     �s�K�X
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
     * <p>�ˮ� �K�X���o���u�S���ө��N���v���l�r��</p>
     * @version 0.1 2007/09/21  Caspar Chen
     * @param strNewPW     �s�K�X
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
     * <p>���o�s�X�r��</p>
     * @version 0.1 2007/09/21  Caspar Chen
     * @param msgPsw   �r��
     * @return String
     */

    public static String getMsgDigestPwd(String msgPsw) throws Exception
    {
        MessageDigest md = MessageDigest.getInstance("MD5");

        md.update(msgPsw.getBytes());//�r��s��

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
     * <p>�ˮֽs�X�r��ۦP</p>
     * @version 0.1 2007/09/21  Caspar Chen
     * @param oldMsgPsw   �s���r��
     * @param newMsgPsw   �s���r��
     * @return boolean
     */
    public boolean getMsgDigestIsEqual(String oldMsgPsw, String newMsgPsw) throws Exception
    {
        byte[] byteOldMsgPsw = oldMsgPsw.getBytes();
        byte[] byteNewMsgPsw = newMsgPsw.getBytes();

        return MessageDigest.isEqual(byteOldMsgPsw, byteNewMsgPsw);
    }

    /**
     * <p>���o�K�X��s����</p>
     * @version 0.1 2007/10/12  Caspar Chen
     * @param  arrayData  �ϥΪ̸��
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
