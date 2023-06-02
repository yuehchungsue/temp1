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
    private String Message = ""; // ��ܰT��

    //�t�ΰѼ�
    private int Sys_Pram_Mer_Order_Count = 3;
    private int Sys_Pram_Mer_Equals_Count = 3;
    private int Sys_Pram_Mer_Pwd_Minlen = 6;
    private int Sys_Pram_Mer_Pwd_Maxlen = 16;
    public int Sys_Order_Count = getMER_PWD_ORDERCNT();
    public int Sys_quals_Count = getMER_PWD_EQUALSCNT();
    public int Sys_Pwd_Minlen = getMER_PWD_MINLEN();
    public int Sys_Pwd_Maxlen = getMER_PWD_MAXLEN();

    //�ϥΪ̸��
    private String Data_Merchant_Id = "";
    private String Data_User_Id = "";
    private String Data_User_Pwss = "";

    //session���
    private Hashtable hashSys = new Hashtable(); // �t�ΰѼ�
    private Hashtable hashMerUser = new Hashtable(); // �S���ϥΪ�
    private Hashtable hashMerchant = new Hashtable(); // �S���D��
    private ArrayList arrayTerminal = new ArrayList(); // �ݥ����D��

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
            Message = "�L�k���o�n�J���";
            new Exception();
        }
        else
        {
            hashSys = (Hashtable)hashConfData.get("SYSCONF");
            hashMerUser = (Hashtable)hashConfData.get("MERCHANT_USER");
            hashMerchant = (Hashtable)hashConfData.get("MERCHANT");
            arrayTerminal = (ArrayList)hashConfData.get("TERMINAL");

            // �t�ΰѼƸ��
            if(hashSys == null || hashSys.size() == 0)
            {
                Message = "�L�k���o�n�J��ƪ��t�ΰѼƸ��";
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

            // �S���ϥΪ̸��
            if (hashMerUser == null || hashMerUser.size() == 0)
            {
                Message = "�L�k���o�n�J��ƪ��S���ϥΪ̸��";
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
            System.out.println(showErrorName + " ����ƫ��A�����T = [" + showName +"]");
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
            System.out.println(showErrorName + " ����ƫ��A�����T = [" + showName +"]");
            System.out.println(e.getMessage());
        }
    }

    /**
     * <p>�ˮ� �K�X���L���</p>
     * @version 0.1 2007/09/21  Caspar Chen
     * @param Pwd   �K�X
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
     * <p>�ˮ� �ϥΪ��±K�X�P�K�X�O�_�@�P</p>
     * @version 0.1 2007/09/21  Caspar Chen
     * @param PW   �K�X
     * @return boolean
     */
    public boolean CheckOldPwd(String PW) throws Exception
    {
        String strOldMsgPW = Data_User_Pwss; //�±K�X(DB)
        String strOldMsgPWInput = this.getMsgDigestPwd(PW); //�K�X

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
     * <p>�ˮ� �K�X���o���ۦP�Ʀr�έ^��r(�j�p�g�������P�r��)</p>
     * @version 0.1 2007/09/21  Caspar Chen
     * @param strNewPW     �s�K�X
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

                //�D���T�r��
                if (intNumber == -1 || intNumberNew == -1)
                {
                    //
                }
                else
                {
                    //�Ʀr�B�j�p�g�^��r
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

                    //�P�_ �K�X���o���ۦP�Ʀr�έ^��r
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

    public String getMsgDigestPwd(String msgPsw) throws Exception
    {
        MessageDigest md = MessageDigest.getInstance("MD5");

        md.update(msgPsw.getBytes());//�r��s��

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
}
