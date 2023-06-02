/************************************************************
 * <p>#File Name:       MerchantCheckPwdCtl.java    </p>
 * <p>#Description:                 </p>
 * <p>#Create Date:     2007/09/19      </p>
 * <p>#Company:         cybersoft       </p>
 * <p>#Notice:                      </p>
 * @author      Caspar Chen
 * @since       SPEC version
 * @version 0.1 2007/09/19  Caspar Chen
 ************************************************************/
package com.cybersoft.merchant.ctl;

import javax.servlet.http.HttpSession;
import java.util.Hashtable;
import java.util.ArrayList;
import java.security.MessageDigest;
import javax.servlet.ServletContext;

public class MerchantCheckPwdCtl
{
    private String Message = ""; // 顯示訊息

    //系統參數
    private int Sys_Pram_Mer_Order_Count = 3;
    private int Sys_Pram_Mer_Equals_Count = 3;
    private int Sys_Pram_Mer_Pwd_Minlen = 6;
    private int Sys_Pram_Mer_Pwd_Maxlen = 16;
    public int Sys_Order_Count = getMER_PWD_ORDERCNT();
    public int Sys_quals_Count = getMER_PWD_EQUALSCNT();
    public int Sys_Pwd_Minlen = getMER_PWD_MINLEN();
    public int Sys_Pwd_Maxlen = getMER_PWD_MAXLEN();

    //使用者資料
    private String Data_Merchant_Id = "";
    private String Data_User_Id = "";
    private String Data_User_Pwss = "";

    //session資料
    private Hashtable hashSys = new Hashtable(); // 系統參數
    private Hashtable hashMerUser = new Hashtable(); // 特店使用者
    private Hashtable hashMerchant = new Hashtable(); // 特店主檔
    private ArrayList arrayTerminal = new ArrayList(); // 端末機主檔

    public MerchantCheckPwdCtl()
    {
    }

    public void MerchantCheckPwdCtl()
    {
    }

    public MerchantCheckPwdCtl(HttpSession session) throws Exception
    {
        Message = "";
        Hashtable hashConfData = (Hashtable)session.getAttribute("SYSCONFDATA");

        if(hashConfData == null || hashConfData.size() == 0)
        {
            Message = "無法取得登入資料";
            new Exception();
        }
        else
        {
            hashSys = (Hashtable)hashConfData.get("SYSCONF");
            hashMerUser = (Hashtable)hashConfData.get("MERCHANT_USER");
            hashMerchant = (Hashtable)hashConfData.get("MERCHANT");
            arrayTerminal = (ArrayList)hashConfData.get("TERMINAL");

            // 系統參數資料
            if(hashSys == null || hashSys.size() == 0)
            {
                Message = "無法取得登入資料的系統參數資料";
                setDefaultContextData(session);
                new Exception();
            }
            else
            {
                if(setDefaultDbData())
                {
                    setDefaultContextData(session);
                }
            }

            // 特店使用者資料
            if (hashMerUser == null || hashMerUser.size() == 0)
            {
                Message = "無法取得登入資料的特店使用者資料";
                new Exception();
            }
            else
            {
                if(hashMerUser.get("MERCHANT_ID") != null)
                {
                    Data_Merchant_Id = hashMerUser.get("MERCHANT_ID").toString();
                }

                if(hashMerUser.get("USER_ID") != null)
                {
                    Data_User_Id = hashMerUser.get("USER_ID").toString();
                }

                if(hashMerUser.get("USER_PWD") != null){
                    Data_User_Pwss = hashMerUser.get("USER_PWD").toString();
                }
            }
        }
    }

    public String getMessage()
    {
        return Message;
    }

    private int getMER_PWD_ORDERCNT()
    {
        return Sys_Pram_Mer_Order_Count;
    }

    private int getMER_PWD_EQUALSCNT()
    {
        return Sys_Pram_Mer_Equals_Count;
    }

    private int getMER_PWD_MINLEN()
    {
        return Sys_Pram_Mer_Pwd_Minlen;
    }

    private int getMER_PWD_MAXLEN()
    {
        return Sys_Pram_Mer_Pwd_Maxlen;
    }

    private boolean setDefaultDbData()
    {
        String showErrorName = "";
        String showName = "";
        try
        {
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
        }
        catch(Exception e)
        {
            System.out.println("MerchantCheckPwdCtl.setDefaultDbData()");
            System.out.println(showErrorName + " 的資料型態不正確 = [" + showName +"]");
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    private void setDefaultContextData(HttpSession session)
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

        }
        catch(Exception e)
        {
            System.out.println("MerchantCheckPwdCtl.setDefaultContextData()");
            System.out.println(showErrorName + " 的資料型態不正確 = [" + showName +"]");
            System.out.println(e.getMessage());
        }
    }

    /**
     * <p>檢核 密碼欄位無資料</p>
     * @version 0.1 2007/09/21  Caspar Chen
     * @param Pwd   密碼
     * @return boolean
     */
    public static boolean CheckPwdNoData(String Pwd)
    {
        if (Pwd == null || Pwd.trim().length() == 0)
        {
            return  false;
        }

        return true;
    }

    /**
     * <p>檢核 使用者舊密碼與密碼是否一致</p>
     * @version 0.1 2007/09/21  Caspar Chen
     * @param PW   密碼
     * @return boolean
     */
    public boolean CheckOldPwd(String PW) throws Exception
    {
        String strOldMsgPW = Data_User_Pwss; //舊密碼(DB)
        String strOldMsgPWInput = this.getMsgDigestPwd(PW); //密碼

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
                        if (intNumberCount >= intNumberCountCk)
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
        String strDigit = "";
        String strLowerCase = "";
        String strUpperCase = "";

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
                    //
                }
                else
                {
                    //數字、大小寫英文字
                    if (Character.isDigit(charNewPW) && Character.isDigit(charNewPWCase))
                    {
                        if (intNumber == intNumberNew)
                        {
                            strDigit += String.valueOf(charNewPW);
                        }
                    }
                    else if (Character.isLowerCase(charNewPW) && Character.isLowerCase(charNewPWCase))
                    {
                        if (intNumber == intNumberNew)
                        {
                            strLowerCase += String.valueOf(charNewPW);
                        }
                    }
                    else if (Character.isUpperCase(charNewPW) && Character.isUpperCase(charNewPWCase))
                    {
                        if (intNumber == intNumberNew)
                        {
                            strUpperCase += String.valueOf(charNewPW);
                        }
                    }
                    else
                    {
                        strDigit = "";
                        strLowerCase = "";
                        strUpperCase = "";
                    }

                    //判斷 密碼不得為相同數字或英文字
                    if (strDigit.length() > Sys_Pram_Mer_Equals_Count ||
                        strLowerCase.length() > Sys_Pram_Mer_Equals_Count ||
                        strUpperCase.length() > Sys_Pram_Mer_Equals_Count)
                    {
                        return false;
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

    public String getMsgDigestPwd(String msgPsw) throws Exception
    {
        MessageDigest md = MessageDigest.getInstance("MD5");

        md.update(msgPsw.getBytes());//字串編瑪

        byte[] digest = md.digest();
//        return new String(digest);
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
}
