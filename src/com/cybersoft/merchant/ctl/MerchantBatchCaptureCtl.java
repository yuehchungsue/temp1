/************************************************************
 * <p>#File Name:	MerchantBatchCaptureCtl.java	</p>
 * <p>#Description:	            			</p>
 * <p>#Create Date:	2007/10/15		        </p>
 * <p>#Company:  	cybersoft	        	</p>
 * <p>#Notice:   					</p>
 * @author		Shirley Lin
 * @since		SPEC version
 * @version	0.1	2007/10/15	Shirley Lin
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
import com.cybersoft.merchant.bean.MerchantCaptureBean;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.*;
import org.w3c.util.UUID;
import java.sql.ResultSet;
import com.cybersoft.bean.createReport;
import com.cybersoft.common.Util;
import com.cybersoft.merchant.bean.MerchantLoginBean;
import com.fubon.security.filter.SecurityTool;
import com.cybersoft.bean.LogUtils;
/**
 * <p>������дڪ�Servlet</p>
 * @version	0.1	2007/10/15	Shiley Lin
 * 202112300619-01 20220210 GARY �дڧ妸�дڳW��W��(Visa Authorization Source Code) AUTH_SRC_CODE
 */
public class MerchantBatchCaptureCtl extends HttpServlet
{
    private static final String CONTENT_TYPE = "text/html; charset=Big5";
    private String Forward = "./Merchant_Response.jsp"; // ������}
    private String Message = ""; // ��ܰT��
    java.util.Date nowdate;
    public static final LogUtils log_user = new LogUtils("user");
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    String LogUserName = "";
    String LogFunctionName = "�ɮ׽дڧ@�~";
    String LogStatus = "����";
    String LogMemo = "";
    String LogData = "";
    String LogMerchantID = "";
    public UserBean UserBean = new UserBean();
    public DataBaseBean2 sysBean = new DataBaseBean2();

    public void init()
    {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        this.doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	Date start =new Date();
        response.setContentType(CONTENT_TYPE);
        request.setCharacterEncoding("BIG5");

        HttpSession session = request.getSession(true);
        /* Check Session */
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
            Hashtable hashSys = new Hashtable(); // �t�ΰѼ�
            Hashtable hashMerUser = new Hashtable(); // �S���ϥΪ�
            Hashtable hashMerchant = new Hashtable(); // �S���D��
            ArrayList arrayTerminal = new ArrayList(); // �ݥ����D��
            Hashtable hashConfData = new Hashtable();
            String Action = (String) request.getParameter("Action");

            if (Action == null)
            {
                Action = "";
            }

            System.out.println("Action=" + Action);
            hashConfData = (Hashtable) session.getAttribute("SYSCONFDATA");
            if (hashConfData == null)
            {
                hashConfData = new Hashtable();
            }

            if (hashConfData.size() > 0)
            {
                hashSys = (Hashtable) hashConfData.get("SYSCONF"); // �t�ΰѼ�
                if (hashSys == null)
                {
                    hashSys = new Hashtable();
                }

                hashMerUser = (Hashtable) hashConfData.get("MERCHANT_USER"); // �S���ϥΪ�
                if (hashMerUser == null)
                {
                    hashMerUser = new Hashtable();
                }

                if (Action.length()==0)
                {   // ���s��s���
                    String MerchantID = hashMerUser.get("MERCHANT_ID").toString();
                    String UserID = hashMerUser.get("USER_ID").toString();
                    MerchantLoginBean LoginBean = new MerchantLoginBean();
//                    Hashtable hashtmpMerchant = LoginBean.get_Merchant(SysBean, MerchantID); //�S���D��
//
//                    if (hashtmpMerchant !=null && hashtmpMerchant.size() >0 )
//                    {
//                        hashConfData.put("MERCHANT",hashtmpMerchant);
//                    }

                    ArrayList arraytmpTerminal = LoginBean.get_Terminal(MerchantID); // �ݥ����D��
                    if (arraytmpTerminal !=null && arraytmpTerminal.size() >0 )
                    {
                        hashConfData.put("TERMINAL", arraytmpTerminal );
                    }

//                    Hashtable hashtmpMerUser = LoginBean.get_Merchant_User(SysBean, MerchantID, UserID);
//                    if (hashtmpMerUser !=null && hashtmpMerUser.size() >0 )
//                    {
//                        hashConfData.put("MERCHANT_USER", hashtmpMerUser );
//                        hashMerUser = hashtmpMerUser;
//                    }

                    session.setAttribute("SYSCONFDATA", hashConfData);
                }

                hashMerchant = (Hashtable) hashConfData.get("MERCHANT"); // �S���D��
                if (hashMerchant == null)
                {
                    hashMerchant = new Hashtable();
                }

                if (hashMerchant.size()>0)
                {
                    LogMerchantID = (String)hashMerUser.get("MERCHANT_ID");
                }

                arrayTerminal = (ArrayList) hashConfData.get("TERMINAL"); // �ݥ����D��
                if (arrayTerminal == null)
                {
                    arrayTerminal = new ArrayList();
                }
            }

            String MenuKey = (String) request.getParameter(MerchantMenuCtl.MENU_FORWARD_KEY);
            request.setAttribute(MerchantMenuCtl.MENU_FORWARD_KEY, MenuKey);
            boolean User_Permit = MerchantMenuCtl.getUserRole(sysBean, session, MenuKey);
            /* UserBean has been declared at function begining */
            // UserBean UserBean = new UserBean();
            boolean Merchant_Current = UserBean.check_Merchant_Column(hashMerchant, "CURRENTCODE", "B,D,E,F"); //  �T�{�S�����A  B,D�i�ʳf�д� B,D,E,F�i�h�f�д�
            boolean Merchant_Permit  = UserBean.check_Merchant_Column(hashMerchant, "PERMIT_CAPTURE", "Y"); //  �T�{�S���v��
            boolean Merchant_Permit1 = UserBean.check_Merchant_Column(hashMerchant, "CAPTURE_MANUAL", "Y"); //  �T�{�S���v��
//            System.out.println("Merchant_Permit=" + Merchant_Permit + ",Merchant_Permit1=" + Merchant_Permit1);

            if (User_Permit)
            {
                // �ϥΪ��v��
                if (Merchant_Current)
                {
                    //�S�����A
                    if (Merchant_Permit && Merchant_Permit1)
                    {
                        // �S���v��
                        if (Action.length() == 0)
                        {
                            //�������W���ɮ׵e��
                            Forward = "./Merchant_Capture_Upload.jsp";
                        }
                        else
                        {
                            String MerchantID = hashMerchant.get("MERCHANTID").toString();
                            String SubMID = hashMerUser.get("SUBMID").toString();
                            
                            if (Action.equalsIgnoreCase("Batch"))
                            {   // ���W��
                                Hashtable hashData = new Hashtable();
                                int iCount = 0;
                                int SaleCount = 0;
                                int RefundCount = 0;
                                String dataPath = "";
                                String MerchantPath = hashSys.get("MER_UPLOADTXT_PATH").toString();
                                String saveDirectory = MerchantPath + "/" + MerchantID + "/";

                                sysBean.setAutoCommit(false);
                                if (new File(MerchantPath).isDirectory())
                                {
                                }
                                else
                                {
                                    new File(MerchantPath).mkdir();
                                }

                                if (new File(saveDirectory).isDirectory())
                                {
                                }
                                else
                                {
                                    new File(saveDirectory).mkdir();
                                }

                                int maxPostSize = 5 * 1024 * 1024;
                                String FileName = null;

                                MultipartRequest multi = new MultipartRequest(request, saveDirectory, maxPostSize, "BIG5");
                                Enumeration filesname = multi.getFileNames();

                                while (filesname.hasMoreElements())
                                {
                                    String name = (String) filesname.nextElement();
                                    FileName = multi.getFilesystemName(name);
                                    File f = multi.getFile(name);

                                    if (FileName != null)
                                    {
                                        LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                        if (f.length() == 0)
                                        {
                                            Message = "�ɮפW�ǥ���";
                                            LogMemo = Message;
                                            LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                            log_user.debug(LogData);
                                            session.setAttribute("Message", Message);
                                            Forward = "./Merchant_Response.jsp";
                                        }
                                        else
                                        {
                                            LogMemo = "�ɮפW�Ǧ��\";
                                            int[] fieldStart = {0, 16, 41, 49, 55, 63, 65};
                                            int[] fieldLength = {16, 25, 8, 6, 8, 2, 11};
                                            String[] fieldName = {"�ݥ����N��", "�q��s��", "������", "����ɶ�", "���v�X", "������O", "�дڪ��B"};
                                            int fieldNo = fieldName.length;

                                            dataPath = saveDirectory + FileName;
//                                            System.out.println(dataPath);
                                            BufferedReader bufferread = new  BufferedReader(new InputStreamReader(new FileInputStream(dataPath)));

                                            String HeadMerchantID = "";
                                            String HeadDate = "";
                                            String HeadSerial = "";
                                            //new add �l�S���N��
                                            String HeadSubMid="";
                                            String TotalSalePcs = "0";
                                            String TotalSaleAmt = "0";
                                            String TotalRefundPcs = "0";
                                            String TotalRefundAmt = "0";
                                            String TotalPcs = "0";
                                            String TotalAmt = "0";
                                            String SumAmt = "0";
                                            String SumSaleAmt = "0";
                                            String SumRefundAmt = "0";
                                            boolean boolCheckLenFlag = true;
                                            while (true)
                                            {
                                                String lineData = bufferread.readLine();
                                                if (lineData == null)
                                                {
                                                    break;
                                                }
//                                                System.out.println("lineData len (" + lineData.length() + ")=" + lineData);
                                                if (lineData.length() == 28 || lineData.length() == 41 || lineData.length() == 76 || lineData.length() == 63)
                                                {
                                                	//�д��ɼW�[layOut ����28�����S��  �۰ʨ��l�S���N��
                                                	if (lineData.length() == 28)
                                                    { // Data Head
                                                         HeadMerchantID = lineData.substring(0,  16).trim();
                                                         HeadDate       = lineData.substring(16, 24).trim();
                                                         HeadSerial     = lineData.substring(24, 28).trim();
                                                         //new Add 20130913  submid = verchar(13)
                                                         HeadSubMid = SubMID;
                                                    }
                                                    if (lineData.length() == 41)
                                                    { // Data Head
                                                        HeadMerchantID = lineData.substring(0,  16).trim();
                                                        HeadDate       = lineData.substring(16, 24).trim();
                                                        HeadSerial     = lineData.substring(24, 28).trim();
                                                        //new Add 20130913  submid = verchar(13)
                                                        HeadSubMid = lineData.substring(28, 41).trim();
                                                    }

                                                    if (lineData.length() == 76)
                                                    { // Data
                                                        String arrayData[] = new String[7];
                                                        iCount++;
                                                        String MaxField = "";
                                                        String TransCode = "";

                                                        for (int j = 0; j < fieldNo; j++)
                                                        {
                                                            if (fieldName[j].equalsIgnoreCase("�ݥ����N��"))
                                                            {
                                                                if (MaxField.equalsIgnoreCase(fieldName[j]) && lineData.length() < (fieldStart[j] + fieldLength[j]))
                                                                {
                                                                    //��1�����
                                                                    arrayData[0] = lineData.substring(fieldStart[j], (lineData.length())).trim();
                                                                }
                                                                else
                                                                {
                                                                    arrayData[0] = lineData.substring(fieldStart[j], (fieldStart[j] + fieldLength[j])).trim();
                                                                }
                                                            }

                                                            if (fieldName[j].equalsIgnoreCase("�q��s��"))
                                                            {
                                                                if (MaxField.equalsIgnoreCase(fieldName[j]) && lineData.length() < (fieldStart[j] + fieldLength[j]))
                                                                {
                                                                    //��2�����
                                                                    arrayData[1] = lineData.substring(fieldStart[j],(lineData.length())).trim();
                                                                }
                                                                else
                                                                {
                                                                    arrayData[1] = lineData.substring(fieldStart[j],(fieldStart[j] + fieldLength[j])).trim();
                                                                }
                                                            }

                                                            if (fieldName[j].equalsIgnoreCase("������"))
                                                            {
                                                                if (MaxField.equalsIgnoreCase(fieldName[j]) && lineData.length() < (fieldStart[j] + fieldLength[j]))
                                                                {
                                                                    //��3�����
                                                                    arrayData[2] = lineData.substring(fieldStart[j], (lineData.length())).trim();
                                                                }
                                                                else
                                                                {
                                                                    arrayData[2] = lineData.substring(fieldStart[j], (fieldStart[j] + fieldLength[j])).trim();
                                                                }
                                                            }

                                                            if (fieldName[j].equalsIgnoreCase("����ɶ�"))
                                                            {
                                                                if (MaxField.equalsIgnoreCase(fieldName[j]) && lineData.length() < (fieldStart[j] + fieldLength[j]))
                                                                {
                                                                    //��4�����
                                                                    arrayData[3] = lineData.substring(fieldStart[j], (lineData.length())).trim();
                                                                }
                                                                else
                                                                {
                                                                    arrayData[3] = lineData.substring(fieldStart[j],(fieldStart[j] +fieldLength[j])).trim();
                                                                }
                                                            }

                                                            if (fieldName[j].equalsIgnoreCase("���v�X"))
                                                            {
                                                                if (MaxField.equalsIgnoreCase(fieldName[j]) && lineData.length() < (fieldStart[j] + fieldLength[j]))
                                                                {
                                                                    //��5�����
                                                                    arrayData[4] = lineData.substring(fieldStart[j],(lineData.length())).trim();
                                                                }
                                                                else
                                                                {
                                                                    arrayData[4] = lineData.substring(fieldStart[j], (fieldStart[j] + fieldLength[j])).trim();
                                                                }
                                                            }

                                                            if (fieldName[j].equalsIgnoreCase("������O"))
                                                            {
                                                                if (MaxField.equalsIgnoreCase(fieldName[j]) && lineData.length() < (fieldStart[j] + fieldLength[j]))
                                                                {
                                                                    //��6�����
                                                                    arrayData[5] = lineData.substring(fieldStart[j], (lineData.length())).trim();
                                                                }
                                                                else
                                                                {
                                                                    arrayData[5] = lineData.substring(fieldStart[j], (fieldStart[j] + fieldLength[j])).trim();
                                                                }

                                                                TransCode = arrayData[5];
                                                            }

                                                            if (fieldName[j].equalsIgnoreCase("�дڪ��B"))
                                                            {
                                                                if (MaxField.equalsIgnoreCase(fieldName[j]) && lineData.length() < (fieldStart[j] + fieldLength[j]))
                                                                {
                                                                    //��7�����
                                                                    arrayData[6] = lineData.substring(fieldStart[j], (lineData.length())).trim();
                                                                }
                                                                else
                                                                {
                                                                    arrayData[6] = lineData.substring(fieldStart[j], (fieldStart[j] + fieldLength[j])).trim();
                                                                }

                                                                if (arrayData[6].length() > 0)
                                                                {
                                                                    SumAmt = String.valueOf(Double.parseDouble(SumAmt) + Double.parseDouble(arrayData[6]));
                                                                    // System.out.println("TransCode=" + TransCode);

                                                                    if (TransCode.length() > 0)
                                                                    {
                                                                        if (TransCode.equalsIgnoreCase("00"))
                                                                        {
                                                                            // �ʳf
                                                                            SaleCount++;
                                                                            SumSaleAmt = String.valueOf(Double.parseDouble(SumSaleAmt) + Double.parseDouble(arrayData[6]));
                                                                        }

                                                                        if (TransCode.equalsIgnoreCase("01"))
                                                                        {
                                                                            // �h�f
                                                                            RefundCount++;
                                                                            SumRefundAmt = String.valueOf(Double.parseDouble(SumRefundAmt) + Double.parseDouble(arrayData[6]));
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        hashData.put(String.valueOf(hashData.size()),arrayData);
                                                    }

                                                    if (lineData.length() == 63)
                                                    {
                                                        // Data End
                                                        TotalPcs       = lineData.substring(0,   8).trim();
                                                        TotalSalePcs   = lineData.substring(8,  16).trim();
                                                        TotalRefundPcs = lineData.substring(16, 24).trim();
                                                        TotalAmt       = lineData.substring(24, 37).trim();
                                                        TotalSaleAmt   = lineData.substring(37, 50).trim();
                                                        TotalRefundAmt = lineData.substring(50, 63).trim();
                                                    }
                                                }
                                                else
                                                {
                                                    boolCheckLenFlag = false;
                                                    break;
                                                }
                                            }
                                            Date checkstart =new Date();
                                            if (boolCheckLenFlag)
                                            {
//                                                System.out.println("HeadMerchantID=" +HeadMerchantID + ",MerchantID=" + MerchantID);
                                            	//�s�W�ˮ֤l�S���N���O�_�۲�
                                                if (HeadMerchantID.equalsIgnoreCase(MerchantID) && HeadSubMid.equals(SubMID))
                                                {
                                                    // �S���N��
                                                    String Today = UserBean.get_TransDate("yyyyMMdd");
//                                                    System.out.println("Today=" +  Today + ",HeadDate=" + HeadDate);

                                                    if (Today.equalsIgnoreCase(HeadDate))
                                                    {
//                                                        System.out.println( "SaleCount=" + SaleCount + ",TotalSalePcs=" + TotalSalePcs + ",TotalSaleAmt=" + TotalSaleAmt +",SumSaleAmt=" + SumSaleAmt);
                                                        if (SaleCount == Integer.parseInt(TotalSalePcs) && Double.parseDouble(TotalSaleAmt) == Double.parseDouble(SumSaleAmt))
                                                        {
//                                                            System.out.println( "RefundCount=" + RefundCount + ",TotalRefundPcs=" + TotalRefundPcs +",TotalRefundAmt=" + TotalRefundAmt + ",SumRefundAmt=" + SumRefundAmt);
                                                            if (RefundCount == Integer.parseInt(TotalRefundPcs) && Double.parseDouble(TotalRefundAmt) == Double.parseDouble(SumRefundAmt))
                                                            {
//                                                                System.out.println( "iCount=" + iCount + ",TotalPcs=" +TotalPcs +",TotalAmt=" +TotalAmt +",SumAmt=" +SumAmt);
                                                                if (iCount == Integer.parseInt(TotalPcs) && Double.parseDouble(TotalAmt) == Double.parseDouble(SumAmt))
                                                                {
                                                                    UUID uuid = new UUID();
                                                                    String BatchPmtID = uuid.toString().toUpperCase();
                                                                    System.out.println("BatchPmtID=" + BatchPmtID);
                                                                    String CaptureDay =hashSys.get("MER_CAPTURE_DAY").toString();
                                                                    ArrayList arrayCardTest = UserBean.get_Test_Card(sysBean, "N","Y");   //  ���X���եd
                                                                    Date insertToBatchstart =new Date();
                                                                    if ( insertToBatch(hashData, BatchPmtID, MerchantID, SubMID, HeadDate, HeadSerial, CaptureDay, hashMerchant, arrayTerminal, arrayCardTest))
                                                                    {
                                                                    	 Date insertToBatchend =new Date();
                                                                         System.out.println("!!!!!!!!!!!    insertToBatchend  End[ "+(insertToBatchend.getTime()-insertToBatchstart.getTime())/1000+"]!!!!!!!!!!!!!!!");
                                                                        
                                                                        // �T�{�`���B
                                                                        CheckCapture(MerchantID, SubMID, BatchPmtID, CaptureDay, hashMerchant, arrayTerminal);
                                                                        System.out.println("!!!!!!!!!!!    CheckCapture  End[ "+(new Date().getTime()-insertToBatchend.getTime())/1000+"]!!!!!!!!!!!!!!!");
//                                                            MerchantCaptureBean CaptureBean = new  MerchantCaptureBean();
                                                                        ArrayList arrayList = UserBean.get_Batch(sysBean, MerchantID, SubMID, BatchPmtID, "", "BATCHTERMINALID,BATCHSYSORDERID,BATCHTRANSCODE,BATCHTXDATE,BATCHTXTIME");                                                                        
                                                                        System.out.println("!!!!!!!!!!!    UserBean.get_Batch  End[ "+(new Date().getTime()-insertToBatchend.getTime())/1000+"]!!!!!!!!!!!!!!!");
                                                                        Hashtable   hashCaptureData = new  Hashtable();

                                                                        // �d�߽дڸ��
                                                                        hashCaptureData.put("DATALIST", arrayList);
                                                                        // �ɮקǸ�
                                                                        hashCaptureData.put("BATCHPMTID", BatchPmtID);
                                                                        if (session.getAttribute("CaptureData") != null)
                                                                        {
                                                                            session.removeAttribute("CaptureData");
                                                                        }

                                                                        LogMemo = "��ƤW�Ǧ��\";
                                                                        LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                                                        LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "���\", LogMemo);
                                                                        log_user.debug(LogData);
                                                                        session.setAttribute("CaptureData", hashCaptureData);
                                                                        Forward = "./Merchant_BatchCapture_List.jsp";
                                                                    }
                                                                    else
                                                                    {
                                                                        LogMemo = "�t�μȰ��дڥ��-��Ʈw���`";
                                                                        LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                                                        LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                                                        log_user.debug(LogData);
                                                                        Message = "�t�μȰ��дڥ���еy��A��";
                                                                        session.setAttribute("Message", Message);
                                                                        Forward = "./Merchant_Response.jsp";
                                                                    }
                                                                }
                                                                else
                                                                {
                                                                    Message = "�д��`���ƪ��B����";
                                                                    session.setAttribute("Message", Message);
                                                                    Forward = "./Merchant_Response.jsp";
                                                                    LogMemo = "�д��`���ƪ��B����";
                                                                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                                                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                                                    log_user.debug(LogData);
                                                                }
                                                            }
                                                            else
                                                            {
                                                                Message = "�h�f�д��`���ƪ��B����";
                                                                session.setAttribute("Message", Message);
                                                                Forward = "./Merchant_Response.jsp";
                                                                LogMemo = "�h�f�д��`���ƪ��B����";
                                                                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                                                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                                                log_user.debug(LogData);
                                                            }
                                                        }
                                                        else
                                                        {
                                                            Message = "�ʳf�д��`���ƪ��B����";
                                                            session.setAttribute("Message", Message);
                                                            Forward = "./Merchant_Response.jsp";
                                                            LogMemo = "�ʳf�д��`���ƪ��B����";
                                                            LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                                            LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                                            log_user.debug(LogData);
                                                        }
                                                    }
                                                    else
                                                    {
                                                        LogMemo = "�ɮ׽дڤ����������";
                                                        LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                                        LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                                        log_user.debug(LogData);

                                                        Message = "�ɮ׽дڤ����������";
                                                        session.setAttribute("Message", Message);
                                                        Forward = "./Merchant_Response.jsp";
                                                    }
                                                }
                                                else
                                                {
                                                    LogMemo = "�ɮ׽дڵL�k���ѫD���S�����";
                                                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                                    log_user.debug(LogData);

                                                    Message = "�ɮ׽дڵL�k���ѫD���S�����";
                                                    session.setAttribute("Message", Message);
                                                    Forward = "./Merchant_Response.jsp";
                                                }
                                            }
                                            else
                                            {
                                                LogMemo = "�ɮ׽дڸ�ƪ��צ��~";
                                                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                                log_user.debug(LogData);

                                                Message = "�ɮ׽дڸ�ƪ��צ��~";
                                                session.setAttribute("Message", Message);
                                                Forward = "./Merchant_Response.jsp";
                                            }

                                            bufferread.close();
                                            new File(dataPath).delete();
                                            Date checkend =new Date();
                                            System.out.println("!!!!!!!!!!!    File check data End[ "+(checkend.getTime()-checkstart.getTime())/1000+"]!!!!!!!!!!!!!!!");

                                        }
                                    }
                                }
                            }

                            if (Action.equalsIgnoreCase("Capture"))
                            {
                                // ���д�
                                sysBean.setAutoCommit(false);

                                response.addHeader("Pragma", "No-cache");
                                response.addHeader("Cache-Control", "no-cache");
                                response.setDateHeader("Expires", 0);
                                String BatchPmtID = "";
                                ArrayList arrayCapture = new ArrayList();
                                Hashtable hashCarpture = new Hashtable();
                                if (session.getAttribute("CaptureData") != null)
                                {
                                    hashCarpture = (Hashtable) session.getAttribute("CaptureData");
                                    arrayCapture = (ArrayList) hashCarpture.get("DATALIST");
                                    BatchPmtID = (String) hashCarpture.get("BATCHPMTID");
                                    session.removeAttribute("CaptureData");
                                }

                                Capture(arrayCapture);

                                ArrayList arraySuccess = UserBean.get_Batch(sysBean, MerchantID, SubMID, BatchPmtID, "SUCCESS", "BATCHTERMINALID,BATCHSYSORDERID,BATCHTRANSCODE,BATCHTXDATE,BATCHTXTIME"); // ���\���
                                ArrayList arrayFail = UserBean.get_Batch(sysBean, MerchantID, SubMID, BatchPmtID, "FAIL", "BATCHTERMINALID,BATCHSYSORDERID,BATCHTRANSCODE,BATCHTXDATE,BATCHTXTIME"); // ���Ѹ��

                                hashCarpture = new Hashtable();
                                hashCarpture.put("SUCCESS", arraySuccess);
                                hashCarpture.put("FAIL", arrayFail);
                                hashCarpture.put("BATCHPMTID", BatchPmtID); // �ɮקǸ�

                                if (session.getAttribute("CaptureUpdateData") != null)
                                {
                                    session.removeAttribute("CaptureUpdateData");
                                }

                                session.setAttribute("CaptureUpdateData", hashCarpture);
                                LogMemo = "�дڸ�Ʀ��\"+String.valueOf(arraySuccess.size())+"���A����"+String.valueOf(arrayFail.size())+"��";
                                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "���\", LogMemo);
                                log_user.debug(LogData);
                                Forward = "./Merchant_BatchCaptureShow_List.jsp";
                            }

                            if (Action.equalsIgnoreCase("Print"))
                            {
                                // �ץX
                                sysBean.setAutoCommit(false);

                                ForwardFlag = false;
                                String BatchPmtID = (request.getParameter("BatchPmtID") == null) ? "" : UserBean.trim_Data(request.getParameter("BatchPmtID"));
                                String PrintType = (request.getParameter("PrintType") == null) ? "" : UserBean.trim_Data(request.getParameter("PrintType"));

                                if (BatchPmtID.length() > 0 && PrintType.length() > 0)
                                {
                                    boolean RowdataFlag = true;
                                    if (PrintType.equalsIgnoreCase("PDF")) RowdataFlag = false;
                                    String sql = UserBean.get_Batch_Result(sysBean, MerchantID, SubMID, BatchPmtID, "", "BATCHTERMINALID,BATCHSYSORDERID,BATCHTRANSCODE,BATCHTXDATE,BATCHTXTIME", RowdataFlag);
                                    createReport cr = new createReport();
                                    Hashtable field = new Hashtable();

                                    field.put("SHOW", "�ɮ�");
                                    
                                    // �]���P "�u�W�дڥ\��" �ϥΦP�@��rpt��  �]���W�[TRANSTYPE���� �s�W Jimmy Kang 20150717 --- Start --- 
                                    field.put("TRANSTYPESHOW", "����");
                                    // �]���P "�u�W�дڥ\��" �ϥΦP�@��rpt��  �]���W�[TRANSTYPE���� �s�W Jimmy Kang 20150717 --- Start --- 
                                    
                                    String RPTName = "MerchantCaptureUpdateListReport.rpt";
                                    cr.createPDF(sysBean,SecurityTool.output(sql), request, response, "/Report/" + RPTName, field, PrintType);
                                    /* DatabaseBean used by UserBean has been pass by parameter.  Don't Close DB Here */
                                    // UserBean.close();
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
                        LogMemo = "�S���L���\���v��";
                        LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                        LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "����", LogMemo);
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
                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "����", LogMemo);
                    log_user.debug(LogData);
                }
            }
            else
            {
                Message = "�ϥΪ̵L���v���Ь��t�κ޲z��";
                Forward = "./Merchant_NoUse.jsp";
                session.setAttribute("Message", Message);
                LogMemo = Message;
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "����", LogMemo);
                log_user.debug(LogData);
            }
            Date end =new Date();
            System.out.println("!!!!!!!!!!!     End[ "+(end.getTime()-start.getTime())/1000+"]!!!!!!!!!!!!!!!");
            if (ForwardFlag)
            {
                System.out.println("Forward=" + Forward);
                request.getRequestDispatcher(Forward).forward(request, response);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log_systeminfo.debug("--MerchantBatchCaptureCtl--"+e.toString());
            request.setAttribute("errMsg", e.toString());
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

    /**
     * �ɮ׸�Ƽg�J��Ʈw
     * @param Hashtable hashData ���B�z�ɮ׸��
     * @param String BatchPmtID  �帹
     * @param String MerchantID  �S���N�X
     * @param String SubMID      �A�ȥN�X
     * @param String HeadDate    �ɮת��Y���
     * @param String HeadSerial  �ɮת��Y�Ǹ�
     * @param String CaptureDay  �дڴ���
     * @param Hashtable hashMerchant  �S�����
     * @param String arrayTermina  �׺ݸ��
     */


    private boolean insertToBatch(Hashtable hashData, String BatchPmtID,
                                  String MerchantID, String SubMID,
                                  String HeadDate, String HeadSerial,
                                  String CaptureDay, Hashtable hashMerchant,
                                  ArrayList arrayTerminal,ArrayList arrayCardTest)
    {
        boolean flag = true;
        try
        {
            MerchantCaptureBean CaptureBean = new MerchantCaptureBean();

//            UserBean UserBean = new UserBean();
//            DataBaseBean SysBean = new DataBaseBean();
//            sysBean.setAutoCommit(false);

            for (int i = 0; i < hashData.size(); ++i)
            {
                String AddData[] = (String[]) hashData.get(String.valueOf(i));
                String TerminalID = "";
                String AcquirerID = "";
                String OrderID = "";
                String Sys_OrderID = "";
                String Card_Type = "";
                String PAN = "";
                String ExtenNo = "";
                String ExpireDate = "";
                String TransCode = "";
                String ReversalFlag = "";
                String TransDate = "";
                String TransTime = "";
                String CurrencyCode = "";
                String TransAmt = "";
                String ApproveCode = "";
                String ResponseCode = "";
                String ResponseMsg = "";
                String BatchNo = "";
                String UserDefine = "";
                String EMail = "";
                String MTI = "";
                String RRN = "";
                String SocialID = "";
                String Entry_Mode = "";
                String Condition_Code = "";
                String TransMode = "";
                String TransType = "";
                String ECI = "";
                String CAVV = "";
                String XID = "";
                String InstallType = "";
                String Install = "";
                String FirstAmt = "";
                String EachAmt = "";
                String FEE = "";
                String RedemType = "";
                String RedemUsed = "";
                String RedemBalance = "";
                String CreditAmt = "";
                String BillMessage = "";
                String BalanceAmt = "";
                String CaptureDate = UserBean.get_TransDate("yyyy/MM/dd HH:mm:ss");
                String FeedbackCode = "";
                String FeedbackMsg = "";
                String FeedbackDate = "";
                String Due_Date = "";
                String BatchDate = "";
                String BatchHead = HeadDate + HeadSerial;
                String BatchType = "";
                String BatchTerminalID = AddData[0];
                String BatchSysorderID = AddData[1];
                String BatchTxDate = AddData[2];
                String BatchTxTime = AddData[3];
                String BatchTxApproveCode = AddData[4];
                String BatchTransCode = AddData[5];
                String BatchTxAmt = String.valueOf(Double.parseDouble(AddData[6]));
                String CaptureAmt = BatchTxAmt;
                String BatchTxMsg = "SUCCESS";
                String BatchResponse = "";
                String SysTraceNo = "";
                String Reauthflag = "";  // �A���vflag
                String AUTH_SRC_CODE =""; //20220210
                Date insertToBatchstart =new Date();
                Hashtable hashBillingData = CaptureBean.get_BatchCapture(sysBean, MerchantID,
                        SubMID, AddData[1], AddData[0], AddData[5], AddData[2],
                        AddData[3], AddData[4], AddData[6], CaptureDay);
//                System.out.println("XXXXXXXXX    hashBillingData  DB end  End[ "+(new Date().getTime()-insertToBatchstart.getTime())+"]!!!!!!!!!!!!!!!");
                if (hashBillingData.size() > 0)
                {
                    TerminalID = hashBillingData.get("TERMINALID").toString();
                    AcquirerID = hashBillingData.get("ACQUIRERID").toString();
                    OrderID = hashBillingData.get("ORDERID").toString();
                    Sys_OrderID = hashBillingData.get("SYS_ORDERID").toString();
                    Card_Type = hashBillingData.get("CARD_TYPE").toString();
                    PAN = hashBillingData.get("PAN").toString();
                    ExtenNo = hashBillingData.get("EXTENNO").toString();
                    ExpireDate = hashBillingData.get("EXPIREDATE").toString();
                    TransCode = hashBillingData.get("TRANSCODE").toString();
                    ReversalFlag = hashBillingData.get("REVERSALFLAG").toString();
                    TransDate = hashBillingData.get("TRANSDATE").toString();
                    TransTime = hashBillingData.get("TRANSTIME").toString();
                    CurrencyCode = hashBillingData.get("CURRENCYCODE").toString();
                    TransAmt = hashBillingData.get("TRANSAMT").toString();
                    ApproveCode = hashBillingData.get("APPROVECODE").toString();
                    ResponseCode = hashBillingData.get("RESPONSECODE").toString();
                    ResponseMsg = hashBillingData.get("RESPONSEMSG").toString();
                    BatchNo = hashBillingData.get("BATCHNO").toString();
                    UserDefine = hashBillingData.get("USERDEFINE").toString();
                    EMail = hashBillingData.get("EMAIL").toString();
                    MTI = hashBillingData.get("MTI").toString();
                    RRN = hashBillingData.get("RRN").toString();
                    SocialID = hashBillingData.get("SOCIALID").toString();
                    Entry_Mode = hashBillingData.get("ENTRY_MODE").toString();
                    Condition_Code = hashBillingData.get("CONDITION_CODE").toString();
                    TransMode = hashBillingData.get("TRANSMODE").toString();
                    TransType = hashBillingData.get("TRANSTYPE").toString();
                    ECI = hashBillingData.get("ECI").toString();
                    CAVV = hashBillingData.get("CAVV").toString();
                    XID = hashBillingData.get("XID").toString();
                    InstallType = hashBillingData.get("INSTALLTYPE").toString();
                    Install = hashBillingData.get("INSTALL").toString();
                    FirstAmt = hashBillingData.get("FIRSTAMT").toString();
                    EachAmt = hashBillingData.get("EACHAMT").toString();
                    FEE = hashBillingData.get("FEE").toString();
                    RedemType = hashBillingData.get("REDEMTYPE").toString();
                    RedemUsed = hashBillingData.get("REDEMUSED").toString();
                    RedemBalance = hashBillingData.get("REDEMBALANCE").toString();
                    CreditAmt = hashBillingData.get("CREDITAMT").toString();
                    BillMessage = hashBillingData.get("BILLMESSAGE").toString();
                    Reauthflag  = hashBillingData.get("REAUTH_FLAG").toString();
                    //20220210 AUTH_SRC_CODE
                    AUTH_SRC_CODE = hashBillingData.get("AUTH_SRC_CODE").toString();

                    if (Due_Date.length() == 0)
                    {
                        Due_Date = hashBillingData.get("CAPTUREDDEALLINE").toString();
                    }

                    BalanceAmt = hashBillingData.get("BALANCEAMT").toString();
                    SysTraceNo = hashBillingData.get("SYSTRACENO").toString();

                    if (!TerminalID.equalsIgnoreCase(AddData[0]))
                    {
                        BatchTxMsg = "FAIL";
                        BatchResponse = "�ݥ����N��";
                    }

//                    System.out.println("TransDate=" + TransDate +",AddData[2]=" +AddData[2] + ",");
                    if (!TransDate.equalsIgnoreCase(AddData[2]))
                    {
                        BatchTxMsg = "FAIL";
                        if (BatchResponse.length() > 0)
                            BatchResponse = BatchResponse + ".";
                        BatchResponse = BatchResponse + "������";
                    }

//                    System.out.println("TransTime=" + TransTime +",AddData[3]=" +AddData[3] + ",");
                    if (!TransTime.equalsIgnoreCase(AddData[3]))
                    {
                        BatchTxMsg = "FAIL";
                        if (BatchResponse.length() > 0)
                            BatchResponse = BatchResponse + ".";
                        BatchResponse = BatchResponse + "����ɶ�";
                    }

//                    System.out.println("ApproveCode=" + ApproveCode);
                    String StrApproveCode = ApproveCode.trim();
                    if (!StrApproveCode.equalsIgnoreCase(AddData[4]))
                    {
                        BatchTxMsg = "FAIL";
                        if (BatchResponse.length() > 0)
                            BatchResponse = BatchResponse + ".";

                        BatchResponse = BatchResponse + "���v�X";
                    }
//                    System.out.println("----------------- BatchTxMsg =" + BatchTxMsg);
                    if (BatchTxMsg.equalsIgnoreCase("FAIL"))
                    {
                        BatchResponse = BatchResponse + "���~";
                    }
                    else
                    {
                        if (AddData[6].length() == 0)
                        {
                            BatchTxMsg = "FAIL";
                            BatchResponse = BatchResponse + "�дڪ��B����J";
                        }
                        else
                        {
                        	// Merchant Console �ɮ׽дڧ@�~�Ҳ�  �ק�  by Jimmy Kang 20150717  -- �ק�}�l --
                        	if (PAN.equalsIgnoreCase("62XX"))
                        	{
                        		BatchTxMsg = "FAIL";
                                BatchResponse = "���p������^��";
                        	}
                        	else
                        	{
                        	// Merchant Console �ɮ׽дڧ@�~�Ҳ�  �ק�  by Jimmy Kang 20150717  -- �קﵲ�� --
                        		
                        		if (UserBean.check_Test_Card(arrayCardTest,PAN))
                                {
                                    boolean Terminal_Current = UserBean.check_Terminal_Column(MerchantID, TerminalID, arrayTerminal,
                                                                    "CURRENTCODE", "B,D,E,F"); //  �T�{�ݥ������A B,D�i�ʳf�д� B,D,E,F�i�h�f�д�
                                    boolean Terminal_Permit = UserBean.check_Terminal_Column(MerchantID, TerminalID, arrayTerminal,
                                                                    "PERMIT_CAPTURE", "Y"); //  �T�{�ݥ����v��

                                    if (Terminal_Current && Terminal_Permit)
                                    {
                                        if (Double.parseDouble(BalanceAmt) < 0)
                                        {
                                            BatchTxMsg = "FAIL";
                                            BatchResponse = "����v��Ƥw����";
                                        }
                                        else
                                        {
                                            if (Double.parseDouble(BalanceAmt) == 0)
                                            {
                                                BatchTxMsg = "FAIL";
                                                BatchResponse = "����v��Ƥw�����д�";
                                            }
                                            else
                                            {
                                                boolean MerchantPartial = UserBean.check_Merchant_Column(hashMerchant,
                                                                        "PERMIT_PARTIAL_CAPTURE", "Y"); //  �T�{�S������дڪ��A
                                                boolean TerminalPartial = UserBean.check_Terminal_Column(MerchantID, TerminalID, arrayTerminal,
                                                                        "PERMIT_PARTIAL_CAPTURE", "Y"); //  �T�{�ݥ����v��
                                                BatchTxMsg = "SUCCESS";
                                                BatchResponse = "�i�д�";

                                                if (TransCode.equalsIgnoreCase("00"))
                                                {
                                                    // �ʳf�д�
//                                                    if (!UserBean.check_Merchant_Column(hashMerchant,"CURRENTCODE", "B,D")  ){ //  �T�{�S�����A E,F�ȥi�h�f�д�
//                                                        if (!UserBean.check_Terminal_Column(MerchantID, TerminalID,arrayTerminal,"CURRENTCODE", "E,F")) { //  �T�{�S�����A E,F�ȥi�h�f�д�
                                                            boolean TransModePartial = false;
                                                            if (TransMode.equalsIgnoreCase("0"))
                                                                TransModePartial = true;
                                                            if (MerchantPartial && TerminalPartial && TransModePartial)
                                                            {
                                                                // �i����
                                                                if (Double.parseDouble(AddData[6]) <= Double.parseDouble(TransAmt))
                                                                {
                                                                    BatchTxMsg = "SUCCESS";
                                                                    BatchResponse = "�i�д�";
                                                                }
                                                                else
                                                                {
                                                                    BatchTxMsg = "FAIL";
                                                                    BatchResponse = "�дڪ��B�W�L������B";
                                                                }
                                                            }
                                                            else
                                                            {
                                                                if (Double.parseDouble(AddData[6]) == Double.parseDouble(TransAmt))
                                                                {
                                                                    BatchTxMsg = "SUCCESS";
                                                                    BatchResponse = "�i�д�";
                                                                }
                                                                else
                                                                {
                                                                    BatchTxMsg = "FAIL";
                                                                    BatchResponse = "���i����д�";
                                                                }
                                                            }
//                                                        } else {
//                                                            BatchTxMsg = "FAIL";
//                                                            BatchResponse = "�ݥ����L�k�����ʳf�д�";
//                                                        }
//                                                    } else  {
//                                                        BatchTxMsg = "FAIL";
//                                                        BatchResponse = "�S���L�k�����ʳf�д�";
//                                                    }
                                                }

                                                if (TransCode.equalsIgnoreCase("01"))
                                                {
                                                    // �h�f�д�
                                                   if (Double.parseDouble(AddData[6]) == Double.parseDouble(TransAmt))
                                                   {
                                                      BatchTxMsg = "SUCCESS";
                                                      BatchResponse = "�i�д�";
                                                   }
                                                   else
                                                   {
                                                      BatchTxMsg = "FAIL";
                                                      BatchResponse = "���i����д�";
                                                   }
                                                }
                                            }
                                        }
                                    }
                                    else
                                    {
                                        BatchTxMsg = "FAIL";
                                        BatchResponse = "�ݥ����L���v��";
                                    }
                                }
                                else
                                {
                                    BatchTxMsg = "FAIL";
                                    BatchResponse = "�d�������եd���i�д�";
                                }
                        	// Merchant Console �ɮ׽дڧ@�~�Ҳ�  �ק�  by Jimmy Kang 20150717  -- �ק�}�l --
                        	}
                        	// Merchant Console �ɮ׽дڧ@�~�Ҳ�  �ק�  by Jimmy Kang 20150717  -- �קﵲ�� --
                        }
                    }
                }
                else
                {
                    BatchTxMsg = "FAIL";
                    BatchResponse = "�d�L�������v���";
                }

//                System.out.println("UserBean.insert_Batch");
                if (!UserBean.insert_Batch(sysBean,      MerchantID,  SubMID,       TerminalID,
                                           AcquirerID,   OrderID,     Sys_OrderID,  Card_Type,
                                           PAN,          ExtenNo,     ExpireDate,   TransCode,
                                           ReversalFlag, TransDate,   TransTime,    CurrencyCode,
                                           TransAmt,     ApproveCode, ResponseCode, ResponseMsg,
                                           BatchNo,      UserDefine,  EMail,        MTI,
                                           RRN,          SocialID,    Entry_Mode,   Condition_Code,
                                           TransMode,    TransType,   ECI,          CAVV,
                                           XID,          InstallType, Install,      FirstAmt,
                                           EachAmt,      FEE,         RedemType,    RedemUsed,
                                           RedemBalance, CreditAmt,   BillMessage,  BalanceAmt,
                                           CaptureAmt,   CaptureDate, FeedbackCode, FeedbackMsg,
                                           FeedbackDate, Due_Date,    BatchPmtID,   BatchDate,
                                           BatchHead,    BatchType,   BatchTerminalID, BatchSysorderID,
                                           BatchTxDate,  BatchTxTime, BatchTxApproveCode,
                                           BatchTransCode, BatchTxAmt,BatchTxMsg,
                                           BatchResponse, SysTraceNo, Reauthflag, AUTH_SRC_CODE))
                {
                    flag = false;
                    break;
                }
//                Date insertToBatchend =new Date();
//                System.out.println("XXXXXXXXX    insertToBatch  DB end  End[ "+(insertToBatchend.getTime()-insertToBatchstart.getTime())+"]!!!!!!!!!!!!!!!");
               
            }

            if (flag)
            {
                sysBean.commit();
            }
            else
            {
                sysBean.setRollBack();
            }

            /* SysBean is activate at class level.  Don't close connection at function */
            // sysBean.close();
        }
        catch (Exception e)
        {
            log_systeminfo.debug("--MerchantBatchCaptureCtl--"+e.toString());
            e.printStackTrace();
        }
//        System.out.println("flag=" + flag);
        return flag;
    }

    private void CheckCapture(String MerchantID, String SubMID,
                              String BatchPmtID, String CaptureDay,
                              Hashtable hashMerchant, ArrayList arrayTerminal)
    {
        /* UserBean has been delcared at class level */
        // UserBean UserBean = new UserBean();
        MerchantCaptureBean CaptureBean = new MerchantCaptureBean();
        ArrayList arraySuccess = CaptureBean.get_BatchBalance(sysBean, MerchantID, SubMID, BatchPmtID, CaptureDay, "SUCCESS", "");
        Hashtable hasSumAmt = CaptureBean.sum_Capture_Amt(arraySuccess);
        String PartialFlag = hashMerchant.get("PERMIT_PARTIAL_CAPTURE").toString();
        String OverRefundLimit = hashMerchant.get("OVER_REFUND_LIMIT").toString();
        ArrayList arrayCardTest = new ArrayList();   //  ���X���եd
        Hashtable hashCaptureCheck = CaptureBean.check_Capture_Amt(arraySuccess, hasSumAmt, PartialFlag, arrayTerminal, OverRefundLimit, arrayCardTest);
        Hashtable hashFailCapture = (Hashtable) hashCaptureCheck.get("CaptureFailData");

        if (hashFailCapture == null)
            hashFailCapture = new Hashtable();

        for (int i = 0; i < hashFailCapture.size(); ++i)
        {
            Hashtable hashResponse = (Hashtable) hashFailCapture.get(String.valueOf(i));
            String BatchTxResponse = hashResponse.get("CHECKRESPONSE").toString();
            String RowID = hashResponse.get("RROWID").toString();
            UserBean.update_Batch_Status(sysBean, "FAIL", BatchTxResponse, RowID);
        }
    }


    /**
     * �ʳf�дڥ�� Capture
     * @param ArrayList hashCarpture ���B�z�дڸ��
     */
    private void Capture(ArrayList arrayCarpture)
    {
        boolean CommitFlag = true;

        try
        {
            /* UserBean and SysBean has been declared as class member */
            /* Change DataBaseUpdateBean to SysBean */
            // UserBean UserBean = new UserBean();
            // DataBaseBean DataBaseUpdateBean = new DataBaseBean();
            // DataBaseUpdateBean.setAutoCommit(false);

            MerchantCaptureBean CaptureBean = new MerchantCaptureBean();
            for (int i = 0; i < arrayCarpture.size(); ++i)
            {
                Hashtable hashData = (Hashtable) arrayCarpture.get(i);
                //System.out.println("hashData=" + hashData);
                String BatchTxMsg = hashData.get("BATCHTXMSG").toString();

                if (BatchTxMsg.equalsIgnoreCase("SUCCESS"))
                {
                    String SumRefundCaptureAmt = "0"; // �h�ڽдڪ��B�έp
                    String SumCaptureAmt = "0"; // �ʳf�дڪ��B�έp
                    String TmpTransDate = UserBean.get_TransDate("yyyy/MM/dd HH:mm:ss");
//                    System.out.println("arrayCarpture=" + arrayCarpture);
                    String BatchPmtID = hashData.get("BATCHPMTID").toString();
//                    System.out.println("hashData=" + hashData);
                    String MerchantID = hashData.get("MERCHANTID").toString();
                    String SubMID = hashData.get("SUBMID").toString();
                    String TerminalID = hashData.get("TERMINALID").toString();
                    String AcquirerID = hashData.get("ACQUIRERID").toString();
                    String OrderID = hashData.get("ORDERID").toString();
                    String Sys_OrderID = hashData.get("SYS_ORDERID").toString();
                    String Card_Type = hashData.get("CARD_TYPE").toString();
                    String PAN = hashData.get("PAN").toString();
                    String ExpireDate = hashData.get("EXPIREDATE").toString();
                    String TransCode = hashData.get("TRANSCODE").toString();
                    String TransDate = hashData.get("TRANSDATE").toString();
                    String TransTime = hashData.get("TRANSTIME").toString();
                    String ApproveCode = hashData.get("APPROVECODE").toString();
                    String ResponseCode = hashData.get("RESPONSECODE").toString();
                    String ResponseMsg = hashData.get("RESPONSEMSG").toString();
                    String CurrencyCode = hashData.get("CURRENCYCODE").toString();
                    String CaptureAmt = hashData.get("CAPTUREAMT").toString().trim();

                    if (TransCode.equalsIgnoreCase("00"))
                    {
                        SumCaptureAmt = String.valueOf(Double.parseDouble(SumCaptureAmt) + Double.parseDouble(CaptureAmt));
                    }

                    if (TransCode.equalsIgnoreCase("01"))
                    {
                        SumRefundCaptureAmt = String.valueOf(Double.parseDouble(SumRefundCaptureAmt) + Double.parseDouble(CaptureAmt));
                    }

                    String CaptureDate = TmpTransDate;
                    String UserDefine = hashData.get("USERDEFINE").toString();
                    String BatchNo = hashData.get("BATCHNO").toString();
                    String CaptureFlag = "0";
                    String ProcessDate = TmpTransDate;
                    String Enrty_Mode = hashData.get("ENTRY_MODE").toString();
                    String Condition_Code = hashData.get("CONDITION_CODE").toString();
                    String ECI = hashData.get("ECI").toString();
                    String CAVV = hashData.get("CAVV").toString();
                    String TransMode = hashData.get("TRANSMODE").toString();
                    String InstallType = hashData.get("INSTALLTYPE").toString();
                    String Install = hashData.get("INSTALL").toString();
                    String FirstAmt = hashData.get("FIRSTAMT").toString();
                    String EachAmt = hashData.get("EACHAMT").toString();
                    String FEE = hashData.get("FEE").toString();
                    String RedemType = hashData.get("REDEMTYPE").toString();
                    String RedemUsed = hashData.get("REDEMUSED").toString();
                    String RedemBalance = hashData.get("REDEMBALANCE").toString();
                    String CreditAmt = hashData.get("CREDITAMT").toString();
                    String BillMessage = hashData.get("BILLMESSAGE").toString();
                    String SysTraceNo = hashData.get("SYSTRACENO").toString();
                    String FeeBackCode = " ";
                    String FeeBackMsg = " ";
                    String FeeBackDate = "";
                    String DueDate = hashData.get("DUE_DATE").toString();
                    String TransAmt = hashData.get("TRANSAMT").toString();
                    String BalanceAmt = hashData.get("BALANCEAMT").toString();
                    String ExtenNo = hashData.get("EXTENNO").toString();
                    String RRN = hashData.get("RRN").toString();
                    String MTI = hashData.get("MTI").toString();
                    String XID = hashData.get("XID").toString();
                    String SocialID =  hashData.get("SOCIALID").toString();
                    String ReauthFlag =  hashData.get("CAPTURE_ROWID").toString();
                    //20220210 AUTH_SRC_CODE
                    String AUTH_SRC_CODE = hashData.get("AUTH_SRC_CODE").toString();
                    System.out.println("-----------ReauthFlag="+ReauthFlag);
                    String ExceptFlag =  "";
                    String BatchResponse = "";

                    if (!UserBean.insert_Capture(sysBean,
                                                 MerchantID,    SubMID,       TerminalID,     AcquirerID,
                                                 OrderID,       Sys_OrderID,  Card_Type,      PAN,
                                                 ExpireDate,    TransCode,    TransDate,      TransTime,
                                                 ApproveCode,   ResponseCode, ResponseMsg,    CurrencyCode,
                                                 CaptureAmt,    CaptureDate,  UserDefine,     BatchNo,
                                                 CaptureFlag,   ProcessDate,  Enrty_Mode,     Condition_Code,
                                                 ECI,           CAVV,         TransMode,      InstallType,
                                                 Install,       FirstAmt,     EachAmt,        FEE,
                                                 RedemType,     RedemUsed,    RedemBalance,   CreditAmt,
                                                 BillMessage,   FeeBackCode,  FeeBackMsg,     FeeBackDate,
                                                 DueDate,       TransAmt,     SysTraceNo,     ExtenNo,
                                                 RRN,           MTI,          XID,            SocialID,
                                                 ReauthFlag,    ExceptFlag,AUTH_SRC_CODE))
                    {
                        CommitFlag = false;
                    }

//                    System.out.println("-----------insert_Capture = "+CommitFlag);
//                    System.out.println("----------------------BalanceAmt=" + BalanceAmt + ",CaptureAmt=" + CaptureAmt);
                    String tmpBalanceAmt = CaptureBean.get_BillingBalance(sysBean, MerchantID, SubMID, Sys_OrderID, TransCode);

                    if (tmpBalanceAmt.length() > 0)
                    {
                        BalanceAmt = tmpBalanceAmt;
                        BalanceAmt = String.valueOf(Double.parseDouble(BalanceAmt) - Double.parseDouble(CaptureAmt));

                        if (!CaptureBean.update_BillingeNet(sysBean, MerchantID, SubMID, Sys_OrderID, TransCode, BalanceAmt))
                        {
                            CommitFlag = false;
                        }

//                        System.out.println("-----------insert_Capture ="+CommitFlag);
                    }
                    else
                    {
                        CommitFlag = false;
                    }
//                    System.out.println("update_Balance");

                    Hashtable hashBalanceData = UserBean.get_Balance(sysBean, MerchantID, SubMID, OrderID);
                    String BalCaptureAmt = hashBalanceData.get("CAPTUREAMT").toString();
                    String BalRefundCaptureAmt = hashBalanceData.get("REFUNDCAPTUREAMT").toString();
                    String BalBalanceAmt = hashBalanceData.get("BALANCEAMT").toString();
                    String TmpCaptureDate = TmpTransDate;

                    BalCaptureAmt       = String.valueOf(Double.parseDouble(BalCaptureAmt)       + Double.parseDouble(SumCaptureAmt)); // �дڪ��B
                    BalRefundCaptureAmt = String.valueOf(Double.parseDouble(BalRefundCaptureAmt) + Double.parseDouble(SumRefundCaptureAmt)); // �дڪ��B
                    BalBalanceAmt       = String.valueOf(Double.parseDouble(BalBalanceAmt)       - Double.parseDouble(SumCaptureAmt));

                    if (!UserBean.update_Balance(sysBean,
                                                 MerchantID,    SubMID,        OrderID,    "",
                                                 "",            BalCaptureAmt, TmpCaptureDate,
                                                 "",            "",            BalRefundCaptureAmt,
                                                 TmpCaptureDate, BalBalanceAmt))
                    {
                        CommitFlag = false;
                    }

                    System.out.println("-----------update_Balance ="+CommitFlag);
                    if (CommitFlag)
                    {
                        // ��s���\
                        CommitFlag = sysBean.commit();
                        if (CommitFlag)
                        {
                            System.out.println("-----------sysBean.commit ="+CommitFlag);
                            BatchTxMsg = "SUCCESS";
                            BatchResponse = "�ݳB�z";
                        }
                        else
                        {
                            BatchTxMsg = "FAIL";
                            BatchResponse = "�дڧ�s����";
                        }
                    }
                    else
                    {
                        BatchTxMsg = "FAIL";
                        BatchResponse = "�дڧ�s����";
                        sysBean.setRollBack();
                    }

                    if (!CommitFlag)
                    {
                        // ����
                        BatchTxMsg = "FAIL";
                        BatchResponse = "�дڧ�s����";
                        CaptureBean.update_BillingeFail(sysBean, MerchantID, SubMID, Sys_OrderID, TransCode, "�дڧ�s����", CaptureAmt, DueDate, CaptureDate.substring(0, 10));
                    }

                    String RowID = hashData.get("RROWID").toString();
                    UserBean.update_Batch_Status(sysBean, BatchTxMsg, BatchResponse,RowID);
                }
            }

            /* SysBean has been declared at class level */
            // sysBean.close();
        } catch (Exception e) {
            log_systeminfo.debug("--MerchantBatchCaptureCtl--"+e.toString());
            e.printStackTrace();
        }
    }
}
