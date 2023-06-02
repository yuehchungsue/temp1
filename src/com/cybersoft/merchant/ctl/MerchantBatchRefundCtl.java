/************************************************************
 * <p>#File Name:	MerchantBatchRefundCtl.java	</p>
 * <p>#Description:	            			</p>
 * <p>#Create Date:	2007/10/09		        </p>
 * <p>#Company:  	cybersoft	        	</p>
 * <p>#Notice:   					</p>
 * @author		Shirley Lin
 * @since		SPEC version
 * @version	0.1	2007/10/09	Shirley Lin
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
import com.cybersoft.merchant.bean.MerchantRefundBean;
import com.fubon.security.filter.SecurityTool;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.*;

import org.w3c.util.*;
import org.w3c.util.UUID;

import java.sql.ResultSet;

import com.cybersoft.bean.createReport;
import com.cybersoft.merchant.bean.MerchantLoginBean;
import com.cybersoft.bean.LogUtils;
/**
 * <p>������h�f��Servlet</p>
 * @version	0.1	2007/10/09	Shiley Lin
 * 202112300619-01 20220210 GARY �дڧ妸�дڳW��W��(Visa Authorization Source Code) AUTH_SRC_CODE
*/
public class MerchantBatchRefundCtl extends HttpServlet
{
    private static final String CONTENT_TYPE = "text/html; charset=Big5";
    private String Forward = "./Merchant_Response.jsp"; // ������}
    private String Message = ""; // ��ܰT��
    java.util.Date nowdate;
    public static final LogUtils log_user = new LogUtils("user");
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    String LogUserName = "";
    String LogFunctionName = "�ɮװh�f�@�~";
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
        response.setContentType(CONTENT_TYPE);
        request.setCharacterEncoding("BIG5");

        HttpSession session = request.getSession(true);
        /* Check Session */
        SessionControlBean scb = new SessionControlBean();

        try
        {
            scb = new SessionControlBean(session, request, response);
            sysBean.setAutoCommit (false);
        }
        catch (UnsupportedOperationException E)
        {
            E.toString();
            request.getRequestDispatcher("/Merchant_Bye.jsp").forward(request, response);
            return;
        }

        Hashtable hashSys       = new Hashtable(); // �t�ΰѼ�
        Hashtable hashMerUser   = new Hashtable(); // �S���ϥΪ�
        Hashtable hashMerchant  = new Hashtable(); // �S���D��
        ArrayList arrayTerminal = new ArrayList(); // �ݥ����D��
        Hashtable hashConfData  = new Hashtable();

        try
        {
            boolean ForwardFlag = true;
            String Action = (String) request.getParameter("Action");
            if (Action == null)
            {
                Action = "";
            }

            String MenuKey = (String) request.getParameter(MerchantMenuCtl.MENU_FORWARD_KEY);
            request.setAttribute(MerchantMenuCtl.MENU_FORWARD_KEY, MenuKey);
            System.out.println("Action=" + Action + ",MenuKey=" + MenuKey);
            boolean User_Permit = MerchantMenuCtl.getUserRole(sysBean, session, MenuKey);
            hashConfData = (Hashtable) session.getAttribute("SYSCONFDATA");

            if (hashConfData == null)
            {
                hashConfData = new Hashtable();
            }

            boolean Merchant_Current = false; // �S�����A
            boolean Merchant_Permit  = false; // �S���v��
            boolean Terminal_Current = false; // �ݥ������A
            boolean Terminal_Permit  = false; // �ݥ����v��

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
                {
                    // ���s��s���
                    String MerchantID = hashMerUser.get("MERCHANT_ID").toString();
                    String UserID = hashMerUser.get("USER_ID").toString();
                    MerchantLoginBean LoginBean = new MerchantLoginBean();

//                    Hashtable hashtmpMerchant = LoginBean.get_Merchant(SysBean, MerchantID); //�S���D��
//                    if (hashtmpMerchant !=null && hashtmpMerchant.size() >0 )
//                    {
//                        hashConfData.put("MERCHANT",hashtmpMerchant);
//                    }

                    ArrayList arraytmpTerminal = LoginBean.get_Terminal(sysBean, MerchantID); // �ݥ����D��
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

            Merchant_Current = UserBean.check_Merchant_Column(hashMerchant, "CURRENTCODE", "B,D"); //  �T�{�S�����A
            Merchant_Permit  = UserBean.check_Merchant_Column(hashMerchant, "PERMIT_REFUND", "Y"); //  �T�{�S���v��

            if (User_Permit)
            {
                // �ϥΪ��v��
                if (Merchant_Current)
                {
                    //�S�����A
                    if (Merchant_Permit)
                    {
                        // �S���v��
                        if (Action.length() == 0)
                        {
                            //�������W���ɮ׵e��
                            Forward = "./Merchant_Refund_Upload.jsp";
                        }
                        else
                        {
                            if (Action.equalsIgnoreCase("Batch"))
                            {
                                // ���W��
                                sysBean.setAutoCommit (false);
                                response.addHeader("Pragma", "No-cache");
                                response.addHeader("Cache-Control", "no-cache");
                                response.setDateHeader("Expires", 0);

                                Hashtable hashData = new Hashtable();
                                int iCount = 0;
                                String dataPath = "";
                                String MerchantPath = hashSys.get("MER_UPLOADTXT_PATH").toString();
                                String MerchantID = hashMerchant.get("MERCHANTID").toString();
                                String SubMID = hashMerUser.get("SUBMID").toString();
                                String saveDirectory = MerchantPath + "/" + MerchantID + "/";

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
                                        if (f.length() == 0)
                                        {
                                            Message = "�ɮפW�ǥ���";
                                            session.setAttribute("Message", Message);
                                            Forward = "./Merchant_Response.jsp";
                                            LogMemo = Message;
//                                            LogUserName = hashMerUser.get("USER_NAME").toString();
                                            LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                            LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                            log_user.debug(LogData);
                                        }
                                        else
                                        {
                                            LogMemo = "�ɮפW�Ǧ��\";
                                            int[] fieldStart = {0, 16, 41, 49, 55, 63};
                                            int[] fieldLength = {16, 25, 8, 6, 8, 11};
                                            // 20150803 DalePeng
//                                            String[] fieldName = {"�ݥ����N��", "�S���N��", "������","����ɶ�", "���v�X", "�h�f���B"};
                                            String[] fieldName = {"�ݥ����N��", "�t�Ϋ��w�渹", "������","����ɶ�", "���v�X", "�h�f���B"};
                                            int fieldNo = fieldName.length;

                                            dataPath = saveDirectory + FileName;
//                                            System.out.println(dataPath);
                                            BufferedReader bufferread = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath)));
                                            String HeadMerchantID = "";
                                            String HeadDate = "";
                                            String HeadSerial = "";
                                            String HeadSubMid = "";
                                            String TotalPcs = "0";
                                            String TotalAmt = "0";
                                            String SumAmt = "0";

                                            boolean boolCheckLenFlag = true;
                                            while (true)
                                            {
                                                String lineData = bufferread.readLine();
                                                if (lineData == null)
                                                {
                                                    break;
                                                }
//                                                System.out.println("lineData len (" + lineData.length() +")=" +lineData);

                                                if (lineData.length() == 28 ||lineData.length() == 41 || lineData.length() == 74 || lineData.length() == 21)
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
                                                    {
                                                        // Data Head
                                                        HeadMerchantID = lineData.substring(0, 16).trim();
                                                        HeadDate = lineData.substring(16, 24).trim();
                                                        HeadSerial = lineData.substring(24, 28).trim();
                                                        //�l�S���N��
                                                        HeadSubMid = lineData.substring(28, 41).trim();
                                                    }

                                                    if (lineData.length() == 74)
                                                    {
                                                        // Data
                                                        String arrayData[] = new String[6];
                                                        String MaxField = "";

                                                        iCount++;
                                                        for (int j = 0; j < fieldNo; j++)
                                                        {
                                                            if (fieldName[j].equalsIgnoreCase("�ݥ����N��"))
                                                            {
                                                                if (MaxField.equalsIgnoreCase(fieldName[j]) && lineData.length() < (fieldStart[j] + fieldLength[j]))
                                                                {
                                                                    //�̫�@�����
                                                                    arrayData[0] = lineData.substring(fieldStart[j], (lineData.length())).trim();
                                                                }
                                                                else
                                                                {
                                                                    arrayData[0] = lineData.substring(fieldStart[j], (fieldStart[j] + fieldLength[j])).trim();
                                                                }
                                                            }
                                                            //20150803 DalePeng
//                                                            if (fieldName[j].equalsIgnoreCase("�S���N��"))
                                                            if (fieldName[j].equalsIgnoreCase("�t�Ϋ��w�渹"))
                                                            {
                                                                if (MaxField.equalsIgnoreCase(fieldName[j]) && lineData.length() < (fieldStart[j] + fieldLength[j]))
                                                                {
                                                                    //�̫�@�����
                                                                    arrayData[1] = lineData.substring(fieldStart[j], (lineData.length())).trim();
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
                                                                    //�̫�@�����
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
                                                                    //�̫�@�����
                                                                    arrayData[3] = lineData.substring(fieldStart[j], (lineData.length())).trim();
                                                                }
                                                                else
                                                                {
                                                                    arrayData[3] = lineData.substring(fieldStart[j], (fieldStart[j] + fieldLength[j])).trim();
                                                                }
                                                            }

                                                            if (fieldName[j].equalsIgnoreCase("���v�X"))
                                                            {
                                                                if (MaxField.equalsIgnoreCase(fieldName[j]) && lineData.length() < (fieldStart[j] + fieldLength[j]))
                                                                {
                                                                    //�̫�@�����
                                                                    arrayData[4] = lineData.substring(fieldStart[j], (lineData.length())).trim();
                                                                }
                                                                else
                                                                {
                                                                    arrayData[4] = lineData.substring(fieldStart[j], (fieldStart[j] + fieldLength[j])).trim();
                                                                }
                                                            }

                                                            if (fieldName[j].equalsIgnoreCase("�h�f���B"))
                                                            {
                                                                if (MaxField.equalsIgnoreCase(fieldName[j]) && lineData.length() < (fieldStart[j] + fieldLength[j]))
                                                                {
                                                                    //�̫�@�����
                                                                    arrayData[5] = lineData.substring(fieldStart[j], (lineData.length())).trim();
                                                                }
                                                                else
                                                                {
                                                                    arrayData[5] = lineData.substring(fieldStart[j], (fieldStart[j] + fieldLength[j])).trim();
                                                                }

                                                                if (arrayData[5].length() > 0)
                                                                {
                                                                    SumAmt = String.valueOf(Double.parseDouble(SumAmt) + Double.parseDouble(arrayData[5]));
                                                                }
                                                            }
                                                        }

                                                        hashData.put(String.valueOf(hashData.size()), arrayData);
                                                    }

                                                    if (lineData.length() == 21)
                                                    {
                                                        // Data End
                                                        TotalPcs = lineData.substring(0,8).trim();
                                                        TotalAmt = lineData.substring(8,21).trim();
                                                    }
                                                }
                                                else
                                                {
                                                    boolCheckLenFlag = false;
                                                    break;
                                                }
                                            }

                                            if (boolCheckLenFlag)
                                            {//�h���l�S���N��(�ڥI�_)
                                            	System.out.println("HeadMerchantID["+HeadMerchantID+"]MerchantID["+MerchantID+"]HeadSubMid["+HeadSubMid+"]SubMID["+SubMID+"]");
                                                if (HeadMerchantID.equalsIgnoreCase(MerchantID) && HeadSubMid.equals(SubMID))
                                                {
                                                    // �S���N��
                                                    String Today = UserBean.get_TransDate("yyyyMMdd");
                                                    if (Today.equalsIgnoreCase(HeadDate))
                                                    {
                                                        if (iCount == Integer.parseInt(TotalPcs) && Double.parseDouble(TotalAmt) == Double.parseDouble(SumAmt))
                                                        {
                                                            UUID uuid = new UUID();
                                                            String BatchPmtID = uuid.toString().toUpperCase();
                                                            if (insertToBatch(hashData, BatchPmtID, MerchantID, SubMID, HeadDate, HeadSerial, hashMerchant, arrayTerminal))
                                                            {
                                                                ArrayList arrayList = UserBean.get_Batch(sysBean, MerchantID, SubMID, BatchPmtID, "", "BATCHTERMINALID,BATCHSYSORDERID,BATCHTRANSCODE,BATCHTXDATE,BATCHTXTIME");
                                                                Hashtable hashRefundData = new Hashtable();
                                                                hashRefundData.put("DATALIST",arrayList); // �d�߰h�f���
                                                                hashRefundData.put("BATCHPMTID",BatchPmtID); // �ɮקǸ�

                                                                if (session.getAttribute("RefundData") != null)
                                                                {
                                                                    session.removeAttribute("RefundData");
                                                                }

                                                                session.setAttribute("RefundData", hashRefundData);
                                                                Forward = "./Merchant_BatchRefund_List.jsp";
                                                                LogMemo = "�ɮפW�Ǧ��\";
                                                                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                                                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "���\", LogMemo);
                                                                log_user.debug(LogData);

                                                            }
                                                            else
                                                            {
                                                                Message = "�t�μȰ��h�f����еy��A��";
                                                                session.setAttribute("Message", Message);
                                                                Forward = "./Merchant_Response.jsp";
                                                                LogMemo = Message;
//                                                                LogUserName = hashMerUser.get("USER_NAME").toString();
                                                                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                                                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                                                log_user.debug(LogData);
                                                            }
                                                        }
                                                        else
                                                        {
                                                            Message = "�h�f�`���ƪ��B����";
                                                            session.setAttribute("Message", Message);
                                                            Forward = "./Merchant_Response.jsp";
                                                            LogMemo = Message;
//                                                            LogUserName = hashMerUser.get("USER_NAME").toString();
                                                            LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                                            LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                                            log_user.debug(LogData);
                                                        }
                                                    }
                                                    else
                                                    {
                                                        Message = "�ɮװh�f�����������";
                                                        session.setAttribute("Message", Message);
                                                        Forward = "./Merchant_Response.jsp";
                                                        LogMemo = Message;
//                                                        LogUserName = hashMerUser.get("USER_NAME").toString();
                                                        LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                                        LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                                        log_user.debug(LogData);
                                                    }
                                                }
                                                else
                                                {
                                                    Message = "�ɮװh�f�L�k���ѫD���S�����";
                                                    session.setAttribute("Message", Message);
                                                    Forward = "./Merchant_Response.jsp";
                                                    LogMemo = Message;
//                                                    LogUserName = hashMerUser.get("USER_NAME").toString();
                                                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                                    log_user.debug(LogData);
                                                }
                                            }
                                            else
                                            {
                                                Message = "�ɮװh�f��ƪ��צ��~";
                                                session.setAttribute("Message", Message);
                                                Forward = "./Merchant_Response.jsp";
                                                LogMemo = Message;
//                                                LogUserName = hashMerUser.get("USER_NAME").toString();
                                                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                                log_user.debug(LogData);
                                            }

                                            bufferread.close();
                                            new File(dataPath).delete();
                                        }
                                    }
                                }
                            }

                            if (Action.equalsIgnoreCase("Refund"))
                            {
                                // ���h�f
                                Hashtable hashRefundData = new Hashtable();
                                ArrayList arrayRefundData = new ArrayList();
                                String BatchPmtID = "";

                                sysBean.setAutoCommit (false);
                                if (session.getAttribute("RefundData") != null)
                                {
                                    hashRefundData = (Hashtable) session.getAttribute("RefundData");
                                    BatchPmtID = hashRefundData.get("BATCHPMTID").toString();

                                    if (BatchPmtID == null)
                                        BatchPmtID = "";
                                }

                                if (hashRefundData.size() > 0)
                                {
                                    arrayRefundData = (ArrayList) hashRefundData.get("DATALIST");

                                    if (arrayRefundData == null)
                                        arrayRefundData = new ArrayList();
                                }

                                if (arrayRefundData.size() > 0)
                                {
                                    // �h�f�B�z
                                    updateRefund(arrayRefundData, hashMerchant, arrayTerminal);
                                    ArrayList arraySuccessRefundData = new ArrayList();
                                    ArrayList arrayFailRefundData = new ArrayList();
                                    String MerchantID = hashMerchant.get("MERCHANTID").toString();
                                    String SubMID = hashMerUser.get("SUBMID").toString();

                                    arraySuccessRefundData = UserBean.get_Batch(sysBean, MerchantID, SubMID, BatchPmtID,
                                            "SUCCESS", "BATCHTERMINALID,BATCHSYSORDERID,BATCHTRANSCODE,BATCHTXDATE,BATCHTXTIME"); // ���\���
                                    arrayFailRefundData = UserBean.get_Batch(sysBean, MerchantID,
                                            SubMID, BatchPmtID, "FAIL", "BATCHTERMINALID,BATCHSYSORDERID,BATCHTRANSCODE,BATCHTXDATE,BATCHTXTIME"); // ���Ѹ��
                                    hashRefundData.put("SUCCESSLIST", arraySuccessRefundData); // �d�߰h�f���\���
                                    hashRefundData.put("FAILLIST",    arrayFailRefundData); // �d�߰h�f���Ѹ��
                                    hashRefundData.put("BATCHPMTID",  BatchPmtID); // �ɮקǸ�

                                    if (session.getAttribute("RefundData") != null)
                                    {
                                        session.removeAttribute("RefundData");
                                    }

                                    session.setAttribute("RefundData",hashRefundData);
                                    LogMemo = "�h�f��Ʀ��\"+String.valueOf(arraySuccessRefundData.size())+"���A����"+String.valueOf(arrayFailRefundData.size())+"��";
//                                    LogUserName = hashMerUser.get("USER_NAME").toString();
                                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "���\", LogMemo);
                                    log_user.debug(LogData);
                                    Forward = "./Merchant_BatchRefundShow_List.jsp";
                                }
                                else
                                {
                                    Message = "�ɮװh�f����";
                                    session.setAttribute("Message", Message);
                                    Forward = "./Merchant_Response.jsp";
                                    LogMemo = Message;
//                                    LogUserName = hashMerUser.get("USER_NAME").toString();
                                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                    log_user.debug(LogData);
                                }
                            }

                            if (Action.equalsIgnoreCase("Print"))
                            {
                                // �ץX
                                ForwardFlag = false;
                                String SubMID = String.valueOf(hashMerUser.get("SUBMID"));
                                String MerchantID = String.valueOf(hashMerchant.get("MERCHANTID"));
                                String BatchPmtID = (request.getParameter("BatchPmtID") == null) ? "" : UserBean.trim_Data(request.getParameter("BatchPmtID"));
                                String PrintType  = (request.getParameter("PrintType") == null) ? "" : UserBean.trim_Data(request.getParameter("PrintType"));

                                sysBean.setAutoCommit (false);
                                if (BatchPmtID.length() > 0 && PrintType.length() > 0)
                                {
                                    boolean RowdataFlag = true;
                                    if (PrintType.equalsIgnoreCase("PDF")) RowdataFlag = false;
                                    String sql = UserBean.get_Batch_Result(sysBean, MerchantID, SubMID, BatchPmtID,
                                                            "", "BATCHTERMINALID,BATCHSYSORDERID,BATCHTRANSCODE,BATCHTXDATE,BATCHTXTIME",RowdataFlag);
                                    createReport cr = new createReport();
                                    Hashtable field = new Hashtable();
                                    field.put("SHOW", "�ɮ�");
                                    String RPTName = "MerchantRefundUpdateListReport.rpt";
                                    cr.createPDF(sysBean,SecurityTool.output(sql), request, response, "/Report/" + RPTName, field, PrintType);

                                    //UserBean.closeConn();

                                    LogMemo = "�H"+PrintType+"�榡�ץX�дڸ��";
//                                    LogUserName = hashMerUser.get("USER_NAME").toString();
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
                Message = "�ϥΪ̵L���v���Ь��t�κ޲z��";
                Forward = "./Merchant_NoUse.jsp";
                session.setAttribute("Message", Message);
            }

            if (Message.length()>0)
            {
                LogMemo = Message;
//                LogUserName = hashMerUser.get("USER_NAME").toString();
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "����", LogMemo);
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
            log_systeminfo.debug("--MerchantAuthBatchRefundCtl--"+e.toString());
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
     * @param Hashtable hashMerchant  �S�����
     * @param String arrayTermina  �׺ݸ��
     */

    private boolean insertToBatch(Hashtable hashData, String BatchPmtID,String MerchantID, String SubMID,
                                  String HeadDate, String HeadSerial, Hashtable hashMerchant, ArrayList arrayTerminal )
    {
        MerchantRefundBean RefundBean = new MerchantRefundBean();
        boolean flag = false;

        try
        {
            int insertcnt = 0;
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
                String CaptureAmt = "";
                String CaptureDate = "";
                String FeedbackCode = "";
                String FeedbackMsg = "";
                String FeedbackDate = "";
                String Due_Date = "";
                String BatchDate = HeadDate;
                String BatchHead = HeadSerial;
                String BatchType = "";
                String BatchTerminalID = AddData[0];
                String BatchSysorderID = AddData[1];
                String BatchTxDate = AddData[2];
                String BatchTxTime = AddData[3];
                String BatchTxApproveCode = AddData[4];
                String BatchTransCode = "01";
                String BatchTxAmt = String.valueOf(Double.parseDouble(AddData[5]));
                String BatchTxMsg = "SUCCESS";
                String BatchResponse = "";
                String SysTraceNo = "";
                String AUTH_SRC_CODE = ""; //20220210

                // MerchantRefundBean RefundBean = new MerchantRefundBean();
                // UserBean UserBean = new UserBean();

                Hashtable hashBillingData = RefundBean.get_BatchRefund_List(sysBean, MerchantID, SubMID, AddData[1], "00");
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
                    BatchNo = hashBillingData.get("BATCHNO").toString().trim();
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
                    BalanceAmt = hashBillingData.get("BALANCEAMT").toString();
                    SysTraceNo = hashBillingData.get("SYSTRACENO").toString();
                    AUTH_SRC_CODE = hashBillingData.get("AUTH_SRC_CODE").toString(); //20220210

                    if (!TerminalID.equalsIgnoreCase(AddData[0]))
                    {
                        BatchTxMsg = "FAIL";
                        BatchResponse = "�ݥ����N��";
                    }

                    if (!TransDate.equalsIgnoreCase(AddData[2]))
                    {
                        BatchTxMsg = "FAIL";
                        if (BatchResponse.length() > 0)
                            BatchResponse = BatchResponse + ".";

                        BatchResponse = BatchResponse + "������";
                    }

                    if (!TransTime.equalsIgnoreCase(AddData[3]))
                    {
                        BatchTxMsg = "FAIL";
                        if (BatchResponse.length() > 0)
                            BatchResponse = BatchResponse + ".";
                        BatchResponse = BatchResponse + "����ɶ�";
                    }

                    if (!ApproveCode.trim().equalsIgnoreCase(AddData[4]))
                    {
                        BatchTxMsg = "FAIL";
                        if (BatchResponse.length() > 0)
                            BatchResponse = BatchResponse + ".";
                        BatchResponse = BatchResponse + "���v�X";
                    }

                    if (BatchTxMsg.equalsIgnoreCase("FAIL"))
                    {
                        BatchResponse = BatchResponse + "���~";
                    }
                    else
                    {   
                    	/****** ���p�d�ݨD�ק� Dalepeng 20150709 ---Start ******/
//                    	String PAN_2 = "";
//                    	if (PAN!=null) 
//                    		PAN_2 = PAN.substring(0, 2);
//                    	
//                    	if (PAN_2.equalsIgnoreCase("62"))
//                    	{
//                    		BatchTxMsg = "FAIL";
//                            BatchResponse = BatchResponse + "���p����L�k�ɮװh�f";
//                    	}
                    	// 20150813 DalePeng UCM �ݨD�ܧ�
                    	if (PAN.equalsIgnoreCase("62XX"))
                    	{
                    		BatchTxMsg = "FAIL";
                    		BatchResponse = BatchResponse + "���p������^�еL�k�ɮװh�f";
                    	}
                    	/****** ���p�d�ݨD�ק� Dalepeng 20150709 ---End ******/
                    	else
                    	{	
                    		/** �B�zTransAmt ��11 ��r�� DalePeng **/
                    		StringBuffer sb = new StringBuffer();
                    		while(sb.length() < 11 - TransAmt.length())
                    		{
                    			sb.append("0");
                    		}
                    		String TransAmt_E = sb.append(TransAmt).toString();
                    		/** �B�zTransAmt ��11 ��r�� DalePeng **/
                    		if (AddData[5].length() == 0)
                    		{
                    			BatchTxMsg = "FAIL";
                    			BatchResponse = BatchResponse + "�h�f���B����J";
                    		}
                    		// 20150813 DalePeng ���p�d���i�����h�f  --start
                    		else if (Card_Type.equalsIgnoreCase("C") && !AddData[5].equalsIgnoreCase(TransAmt_E))
                    		{
 
                    			BatchTxMsg = "FAIL";
                    			BatchResponse = BatchResponse + "���p�d����L�k�����h�f";       			
                    		}
                    		// 20150813 DalePeng ���p�d���i�����h�f  --End
                    		else
                    		{
                    			boolean Terminal_Current = UserBean.check_Terminal_Column (MerchantID, TerminalID, arrayTerminal, "CURRENTCODE", "B,D"); //  �T�{�ݥ������A
                    			boolean Terminal_Permit = UserBean.check_Terminal_Column(MerchantID, TerminalID, arrayTerminal, "PERMIT_REFUND", "Y"); //  �T�{�ݥ����v��

                    			if (Terminal_Current && Terminal_Permit)
                    			{
                    				String MerchantPartial = hashMerchant.get("PERMIT_PARTIAL_REFUND").toString();
                    				boolean TerminalPartial = UserBean.check_Terminal_Column(MerchantID, TerminalID, arrayTerminal, "PERMIT_PARTIAL_REFUND", "Y"); //  �T�{�ݥ����v��
                    				String OverRefundLimit = hashMerchant.get("OVER_REFUND_LIMIT").toString();
                    				boolean TransModePartial = false;

                    				if (TransMode.equalsIgnoreCase("0"))
                    				{
                    					TransModePartial = true;
                    				}

                    				Hashtable hashRefundData = RefundBean.check_BatchRefund_Status(hashBillingData, AddData[5], MerchantPartial, TerminalPartial,TransModePartial, OverRefundLimit);
                    				String CheckFlag = hashRefundData.get("FLAG").toString();

                    				if (CheckFlag.equalsIgnoreCase("True"))
                    				{
                    					BatchTxMsg = "SUCCESS";
                    				}
                    				else
                    				{
                    					BatchTxMsg = "FAIL";
                    				}

                    				BatchResponse = hashRefundData.get("MESSAGE").toString();
                    			}
                    			else
                    			{
                    				BatchTxMsg = "FAIL";
                    				BatchResponse = "�ݥ����L���v��";
                    			}
                    		}
                        }
                    }
                }
                else
                {
                    BatchTxMsg = "FAIL";
                    BatchResponse = "�d�L���ʳf������v���";
                }
//                System.out.println("UserBean.insert_Batch");
                //DataBaseBean SysBean = new DataBaseBean();
                //sysBean.setAutoCommit(false);
                //20220210 ADD AUTH_SRC_CODE
                if (!UserBean.insert_Batch(sysBean, MerchantID, SubMID, TerminalID,
                                           AcquirerID, OrderID, Sys_OrderID,
                                           Card_Type, PAN, ExtenNo, ExpireDate,
                                           TransCode, ReversalFlag, TransDate,
                                           TransTime, CurrencyCode, TransAmt,
                                           ApproveCode, ResponseCode, ResponseMsg,
                                           BatchNo, UserDefine, EMail, MTI, RRN,
                                           SocialID, Entry_Mode, Condition_Code,
                                           TransMode, TransType, ECI, CAVV, XID,
                                           InstallType, Install, FirstAmt, EachAmt,
                                           FEE, RedemType, RedemUsed, RedemBalance,
                                           CreditAmt, BillMessage, BalanceAmt,
                                           CaptureAmt, CaptureDate, FeedbackCode,
                                           FeedbackMsg, FeedbackDate, Due_Date,
                                           BatchPmtID, BatchDate, BatchHead,
                                           BatchType, BatchTerminalID,
                                           BatchSysorderID, BatchTxDate,
                                           BatchTxTime, BatchTxApproveCode,
                                           BatchTransCode, BatchTxAmt,BatchTxMsg,
                                           BatchResponse, SysTraceNo, "",AUTH_SRC_CODE))
                {
                    flag = false;
                    sysBean.setRollBack();
                    break;
                }
                else
                {
                    insertcnt++;
                    sysBean.commit();
                }

                // sysBean.close();
            }

            if (insertcnt == hashData.size())
            {
                flag = true;
            }
        }
        catch (Exception e)
        {
            log_systeminfo.debug("--MerchantAuthBatchRefundCtl--"+e.toString());
            e.printStackTrace();
        }

        return flag;
    }

    /**
     * �ɮװh�f��s��Ʈw
     * @param ArrayList arrayRefundData ���B�z�ɮ׸��
     * @param Hashtable hashMerchant  �S�����
     * @param String arrayTermina  �׺ݸ��
     */
    private boolean updateRefund(ArrayList arrayRefundData, Hashtable hashMerchant, ArrayList arrayTerminal)
    {
        boolean flag = true;
        MerchantRefundBean MerchantRefundBean = new MerchantRefundBean();

        try
        {
            String MerchantPartial = hashMerchant.get("PERMIT_PARTIAL_REFUND").toString();
            String OverRefundLimit = hashMerchant.get("OVER_REFUND_LIMIT").toString();
            // UserBean UserBean = new UserBean();

            for (int i = 0; i < arrayRefundData.size(); ++i)
            {
                Hashtable hashtmp = (Hashtable) arrayRefundData.get(i);
                String BatchTxMsg = hashtmp.get("BATCHTXMSG").toString();

                if (BatchTxMsg.equalsIgnoreCase("SUCCESS"))
                {
                    // �i�h�f���
                    String RowID = hashtmp.get("RROWID").toString();
                    String TerminalID = hashtmp.get("BATCHTERMINALID").toString();
                    String MerchantID = hashtmp.get("MERCHANTID").toString();

                    boolean boolPartial = UserBean.check_Terminal_Column(MerchantID, TerminalID, arrayTerminal, "PERMIT_PARTIAL_REFUND", "Y"); //  ����д�

                    if (MerchantPartial.equalsIgnoreCase("N"))
                    {
                        boolPartial = false;
                    }

                    String TransMode = hashtmp.get("TRANSMODE").toString(); //  ����Ҧ�  2007.11.08
                    if (!TransMode.equalsIgnoreCase("0"))
                    {
                        // �@��
                        boolPartial = false;
                    }

                    String SubMID = hashtmp.get("SUBMID").toString();
                    String OrderID = hashtmp.get("ORDERID").toString();
                    String InputRefundAmt = hashtmp.get("BATCHTXAMT").toString();
                    Hashtable hashBalanceData = UserBean.get_Balance(sysBean, MerchantID, SubMID, OrderID);
                    Hashtable hashRefund = MerchantRefundBean.check_Refund_Amt(hashBalanceData, boolPartial, InputRefundAmt, OverRefundLimit);
                    String CheckFlag = hashRefund.get("FLAG").toString();
                    String BatchTxResponse = "";

                    if (CheckFlag.equalsIgnoreCase("true"))
                    {
                        // �i�h�f
                        String TransCode = "01";
                        String TmpTransDate = UserBean.get_TransDate("yyyyMMddHHmmss");
                        String TransDate = TmpTransDate.substring(0, 8);
                        String TransTime = TmpTransDate.substring(8, 14);
                        String TmpSerial = UserBean.get_TransDate("MMddHHmmssSSSS");
                        String Sys_OrderID = OrderID + "_" + TmpSerial;
                        String MTI = "0220";
                        String TransAmt = InputRefundAmt;
                        String BillBalanceAmt = InputRefundAmt;

                        String AcquirerID = hashtmp.get("ACQUIRERID").toString();
                        String Card_Type = hashtmp.get("CARD_TYPE").toString();
                        String PAN = hashtmp.get("PAN").toString();
                        // System.out.println("---------------- 1 PAN ="+PAN);

                        String ExtenNo = hashtmp.get("EXTENNO").toString();
                        String ExpireDate = hashtmp.get("EXPIREDATE").toString();
                        String ReversalFlag = hashtmp.get("REVERSALFLAG").toString();
                        String CurrencyCode = hashtmp.get("CURRENCYCODE").toString();
                        String TransStatus = "R";
                        String ApproveCode = hashtmp.get("APPROVECODE").toString();
                        String ResponseCode = hashtmp.get("RESPONSECODE").toString();
                        String ResponseMsg = hashtmp.get("RESPONSEMSG").toString();
                        String Entry_Mode = hashtmp.get("ENTRY_MODE").toString();
                        String Condition_Code = hashtmp.get("CONDITION_CODE").toString();
                        String BatchNo = hashtmp.get("BATCHNO").toString();
                        String UserDefine = hashtmp.get("USERDEFINE").toString();
                        String Direction = "S";
                        String EMail = hashtmp.get("EMAIL").toString();
                        String RRN = hashtmp.get("RRN").toString();
                        String SocialID = hashtmp.get("SOCIALID").toString();
                        String TransType = hashtmp.get("TRANSTYPE").toString();
                        String ECI = hashtmp.get("ECI").toString();
                        String CAVV = hashtmp.get("CAVV").toString();
                        String XID = hashtmp.get("XID").toString();
                        String InstallType = hashtmp.get("INSTALLTYPE").toString();
                        String Install = hashtmp.get("INSTALL").toString();
                        String FirstAmt = hashtmp.get("FIRSTAMT").toString();
                        String EachAmt = hashtmp.get("EACHAMT").toString();
                        String FEE = hashtmp.get("FEE").toString();
                        String RedemType = hashtmp.get("REDEMTYPE").toString();
                        String RedemUsed = hashtmp.get("REDEMUSED").toString();
                        String RedemBalance = hashtmp.get("REDEMBALANCE").toString();
                        String CreditAmt = hashtmp.get("CREDITAMT").toString();
                        String BillMessage = hashtmp.get("BILLMESSAGE").toString();
                        String SysTraceNo = hashtmp.get("SYSTRACENO").toString();
                        String AUTH_SRC_CODE = hashtmp.get("AUTH_SRC_CODE").toString(); //20220210
                        System.out.println("insert_AuthLog");

                        // DataBaseBean DataBaseBean = new DataBaseBean();
                        // DataBaseBean.setAutoCommit(false);
                        boolean CommitFlag = true;
                        if (Card_Type.equalsIgnoreCase("C"))
                        {
                            PAN = "62XX";
                        }

                        //20220210 ADD CODE
                        if (!UserBean.insert_AuthLog(sysBean, MerchantID, SubMID, TerminalID,
                                AcquirerID, OrderID, Sys_OrderID,
                                Card_Type, PAN, ExtenNo, ExpireDate, TransCode,
                                ReversalFlag, TransDate, TransTime, CurrencyCode,
                                TransAmt, TransStatus, ApproveCode, ResponseCode,
                                ResponseMsg, Entry_Mode, Condition_Code, BatchNo,
                                UserDefine, Direction, EMail, MTI, RRN,
                                SocialID, TransMode, TransType, ECI, CAVV,
                                XID, InstallType, Install, FirstAmt,
                                EachAmt, FEE, RedemType, RedemUsed, RedemBalance,
                                CreditAmt, BillMessage, SysTraceNo,AUTH_SRC_CODE))
                        {
                            CommitFlag = false;
                        }
                        /****** ���p�d�ק�  DalePeng 20150813 --Start-- ******/
                        if (Card_Type.equalsIgnoreCase("C"))
                        {
                        	System.out.println("�d�O : C - CUP");
                        	String TransDate_CUP     = TransDate;
                        	String TransTime_CUP     = TransTime;                        	
                        	String Trace_Time        = ""; 
                        	String Settle_Amount     = "0";  	
                        	String Settle_Currency   = CurrencyCode;
                        	String Settle_Date       = "";
                        	String Exchange_Rate     = "";
                        	String Exchange_Date     = "";
                        	String Cup_Paymode       = "";
                        	String Cup_TransCode     = "04";
                        	String Trans_Status      = "R";
                        	String ResponseCode_CUP  = "";
                        	String ResponseMsg_CUP   = "";
                        	String Cup_QID           = "0";
                        	String Cup_Reserved      = "";
                        	String Direction_CUP     = "S";
                        	String Cps_Refresh       = "N"; 
                        	String Filestatus_Refkey = ""; 
                        	String Notifyurl         = ""; 
                        	String Notifyparam       = ""; 
                        	String Notifytype        = "";  
                        	String Retry_Cnt         = "0"; 
                        	String Cup_Notifypage    = "";
                        	String Cup_Notifyparam   = "";
                        	System.out.println("insert_AUTHLOG_CUP");
                        	if (!UserBean.insert_AuthLog_Cup(sysBean, MerchantID, SubMID, Sys_OrderID, TransCode,
                        				TransDate_CUP, TransTime_CUP, Trace_Time, Settle_Amount, Settle_Currency, Settle_Date,
                        		        Exchange_Rate, Exchange_Date, Cup_Paymode, Cup_TransCode, Trans_Status, 
                        		        ResponseCode_CUP, ResponseMsg_CUP, SysTraceNo, Cup_QID, Cup_Reserved, Direction_CUP, 
                        		        Cps_Refresh, Filestatus_Refkey, Notifyurl, Notifyparam, Notifytype, Retry_Cnt,
                        		        PAN, TransAmt, Cup_Notifypage, Cup_Notifyparam))
                        	{
                        		CommitFlag = false;
                        	}
                        }
                        /****** ���p�d�ק�  DalePeng 20150813 --Start-- ******/

                        System.out.println("insert_Billing");
                      //20220210 ADD CODE
                        if (!UserBean.insert_Billing(sysBean, MerchantID, SubMID, TerminalID,
                                AcquirerID, OrderID, Sys_OrderID, Card_Type, PAN, ExtenNo,
                                ExpireDate, TransCode, ReversalFlag,
                                TransDate, TransTime, CurrencyCode,
                                TransAmt, ApproveCode, ResponseCode,
                                ResponseMsg, BatchNo, UserDefine,
                                EMail, MTI, RRN, SocialID,
                                Entry_Mode, Condition_Code,
                                TransMode, TransType, ECI, CAVV,
                                XID, InstallType, Install, FirstAmt,
                                EachAmt, FEE, RedemType, RedemUsed,
                                RedemBalance, CreditAmt,
                                BillMessage, BillBalanceAmt, SysTraceNo,AUTH_SRC_CODE))
                        {
                            CommitFlag = false;
                        }

                        System.out.println("update_Balance");
                        String AuthAmt = hashBalanceData.get("AUTHAMT").toString();
                        String RefundAmt = hashBalanceData.get("REFUNDAMT").toString();

//                        System.out.println("AuthAmt=" + AuthAmt +",RefundAmt=" + RefundAmt + "InputRefundAmt=" +InputRefundAmt);
                        RefundAmt = String.valueOf(Double.parseDouble(RefundAmt) + Double.parseDouble(InputRefundAmt));
                        String RefundDate = TmpTransDate.substring(0, 4)   + "/" +
                                            TmpTransDate.substring(4, 6)   + "/" +
                                            TmpTransDate.substring(6, 8)   + " " +
                                            TmpTransDate.substring(8, 10)  + ":" +
                                            TmpTransDate.substring(10, 12) + ":" +
                                            TmpTransDate.substring(12, 14);
                        String CancelAmt = hashBalanceData.get("CANCELAMT").toString();
                        String BalanceAmt = hashBalanceData.get("BALANCEAMT").toString();
                      //******�ק�i�дھl�B�޿�  20150716     Start **//
//                      BalanceAmt = String.valueOf(Double.parseDouble(AuthAmt) - Double.parseDouble(RefundAmt) - Double.parseDouble(CancelAmt));
                      BalanceAmt = String.valueOf(Double.parseDouble(BalanceAmt) - Double.parseDouble(RefundAmt) );
                      //******�ק�i�дھl�B�޿�  20150716     End **//
//                        System.out.println("AuthAmt=" + AuthAmt +",RefundAmt=" + RefundAmt +"CancelAmt=" + CancelAmt +"BalanceAmt=" + BalanceAmt);

                        if (!UserBean.update_Balance(sysBean, MerchantID, SubMID, OrderID, RefundAmt, RefundDate, "", "", "", "", "", "", BalanceAmt))
                        {
                            CommitFlag = false;
                        }
                        
                        /****** ���p�d�ק�  DalePeng 20150813 --Start-- ******/

                        if (Card_Type.equalsIgnoreCase("C"))
                        {
                        	System.out.println("�d�O : C - CUP  insert_SAF_CUP");
                        	if (!UserBean.insert_SAF_Cup(sysBean, MerchantID, SubMID, TerminalID, AcquirerID, OrderID, 
                        				Sys_OrderID, Card_Type, PAN, ExtenNo, ExpireDate, TransCode, ReversalFlag, 
                        				TransDate, TransTime, CurrencyCode, TransAmt, ApproveCode, ResponseCode, 
                        				ResponseMsg, Entry_Mode, Condition_Code, BatchNo, UserDefine, EMail, MTI, RRN, 
                        				SocialID, "REQ", "", "", "", "", "", SysTraceNo,"0", "C", TransType, TransMode))
                        	{
                            	CommitFlag = false;
                        	}
                        }
                        else
                        {
                        	 /****** ���p�d�ק�  DalePeng 20150813 --End-- ******/
                        	System.out.println("insert_SAF");
                        	if (!UserBean.insert_SAF(sysBean,
                                                 MerchantID, SubMID, TerminalID,
                                                 AcquirerID, OrderID,Sys_OrderID,
                                                 Card_Type, PAN, ExtenNo, ExpireDate,
                                                 TransCode, ReversalFlag, TransDate,
                                                 TransTime, CurrencyCode, TransAmt,
                                                 ApproveCode, ResponseCode, ResponseMsg,
                                                 Entry_Mode, Condition_Code, BatchNo,
                                                 UserDefine, EMail, MTI, RRN, SocialID,
                                                 TransMode, TransType, ECI, CAVV,
                                                 XID, InstallType, Install, FirstAmt,
                                                 EachAmt, FEE, RedemType, RedemUsed,
                                                 RedemBalance, CreditAmt, "REQ", "",
                                                 "", "", "", "", SysTraceNo))
                        	{
                        		CommitFlag = false;
                        	}
                        } //
                        
                        if (CommitFlag)
                        {
                            CommitFlag = sysBean.commit();
                            BatchTxResponse = "�h�f���\";
                        }
                        else
                        {
                            sysBean.setRollBack();
                            BatchTxResponse = "�h�f����";
                            BatchTxMsg = "FAIL";
                        }
                        // DataBaseBean.close();
                    }
                    else
                    {
                        BatchTxResponse = hashRefund.get("MESSAGE").toString();
                        BatchTxMsg = "FAIL";
                    }

                    UserBean.update_Batch_Status(sysBean, BatchTxMsg, BatchTxResponse, RowID);
                }
            }

        }
        catch (Exception e)
        {
            log_systeminfo.debug("--MerchantAuthBatchRefundCtl--"+e.toString());
            e.printStackTrace();
            flag = false;
        }

        return flag;
    }
}
