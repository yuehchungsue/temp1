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
 * <p>控制整批請款的Servlet</p>
 * @version	0.1	2007/10/15	Shiley Lin
 * 202112300619-01 20220210 GARY 請款批次請款規格增修(Visa Authorization Source Code) AUTH_SRC_CODE
 */
public class MerchantBatchCaptureCtl extends HttpServlet
{
    private static final String CONTENT_TYPE = "text/html; charset=Big5";
    private String Forward = "./Merchant_Response.jsp"; // 網頁轉址
    private String Message = ""; // 顯示訊息
    java.util.Date nowdate;
    public static final LogUtils log_user = new LogUtils("user");
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    String LogUserName = "";
    String LogFunctionName = "檔案請款作業";
    String LogStatus = "失敗";
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
            Hashtable hashSys = new Hashtable(); // 系統參數
            Hashtable hashMerUser = new Hashtable(); // 特店使用者
            Hashtable hashMerchant = new Hashtable(); // 特店主檔
            ArrayList arrayTerminal = new ArrayList(); // 端末機主檔
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
                hashSys = (Hashtable) hashConfData.get("SYSCONF"); // 系統參數
                if (hashSys == null)
                {
                    hashSys = new Hashtable();
                }

                hashMerUser = (Hashtable) hashConfData.get("MERCHANT_USER"); // 特店使用者
                if (hashMerUser == null)
                {
                    hashMerUser = new Hashtable();
                }

                if (Action.length()==0)
                {   // 重新更新資料
                    String MerchantID = hashMerUser.get("MERCHANT_ID").toString();
                    String UserID = hashMerUser.get("USER_ID").toString();
                    MerchantLoginBean LoginBean = new MerchantLoginBean();
//                    Hashtable hashtmpMerchant = LoginBean.get_Merchant(SysBean, MerchantID); //特店主檔
//
//                    if (hashtmpMerchant !=null && hashtmpMerchant.size() >0 )
//                    {
//                        hashConfData.put("MERCHANT",hashtmpMerchant);
//                    }

                    ArrayList arraytmpTerminal = LoginBean.get_Terminal(MerchantID); // 端末機主檔
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

                hashMerchant = (Hashtable) hashConfData.get("MERCHANT"); // 特店主檔
                if (hashMerchant == null)
                {
                    hashMerchant = new Hashtable();
                }

                if (hashMerchant.size()>0)
                {
                    LogMerchantID = (String)hashMerUser.get("MERCHANT_ID");
                }

                arrayTerminal = (ArrayList) hashConfData.get("TERMINAL"); // 端末機主檔
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
            boolean Merchant_Current = UserBean.check_Merchant_Column(hashMerchant, "CURRENTCODE", "B,D,E,F"); //  確認特店狀態  B,D可購貨請款 B,D,E,F可退貨請款
            boolean Merchant_Permit  = UserBean.check_Merchant_Column(hashMerchant, "PERMIT_CAPTURE", "Y"); //  確認特店權限
            boolean Merchant_Permit1 = UserBean.check_Merchant_Column(hashMerchant, "CAPTURE_MANUAL", "Y"); //  確認特店權限
//            System.out.println("Merchant_Permit=" + Merchant_Permit + ",Merchant_Permit1=" + Merchant_Permit1);

            if (User_Permit)
            {
                // 使用者權限
                if (Merchant_Current)
                {
                    //特店狀態
                    if (Merchant_Permit && Merchant_Permit1)
                    {
                        // 特店權限
                        if (Action.length() == 0)
                        {
                            //直接轉到上傳檔案畫頁
                            Forward = "./Merchant_Capture_Upload.jsp";
                        }
                        else
                        {
                            String MerchantID = hashMerchant.get("MERCHANTID").toString();
                            String SubMID = hashMerUser.get("SUBMID").toString();
                            
                            if (Action.equalsIgnoreCase("Batch"))
                            {   // 整批上傳
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
                                            Message = "檔案上傳失敗";
                                            LogMemo = Message;
                                            LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                            log_user.debug(LogData);
                                            session.setAttribute("Message", Message);
                                            Forward = "./Merchant_Response.jsp";
                                        }
                                        else
                                        {
                                            LogMemo = "檔案上傳成功";
                                            int[] fieldStart = {0, 16, 41, 49, 55, 63, 65};
                                            int[] fieldLength = {16, 25, 8, 6, 8, 2, 11};
                                            String[] fieldName = {"端末機代號", "訂單編號", "交易日期", "交易時間", "授權碼", "交易類別", "請款金額"};
                                            int fieldNo = fieldName.length;

                                            dataPath = saveDirectory + FileName;
//                                            System.out.println(dataPath);
                                            BufferedReader bufferread = new  BufferedReader(new InputStreamReader(new FileInputStream(dataPath)));

                                            String HeadMerchantID = "";
                                            String HeadDate = "";
                                            String HeadSerial = "";
                                            //new add 子特店代號
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
                                                	//請款檔增加layOut 長度28為母特店  自動取子特店代號
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
                                                            if (fieldName[j].equalsIgnoreCase("端末機代號"))
                                                            {
                                                                if (MaxField.equalsIgnoreCase(fieldName[j]) && lineData.length() < (fieldStart[j] + fieldLength[j]))
                                                                {
                                                                    //第1個欄位
                                                                    arrayData[0] = lineData.substring(fieldStart[j], (lineData.length())).trim();
                                                                }
                                                                else
                                                                {
                                                                    arrayData[0] = lineData.substring(fieldStart[j], (fieldStart[j] + fieldLength[j])).trim();
                                                                }
                                                            }

                                                            if (fieldName[j].equalsIgnoreCase("訂單編號"))
                                                            {
                                                                if (MaxField.equalsIgnoreCase(fieldName[j]) && lineData.length() < (fieldStart[j] + fieldLength[j]))
                                                                {
                                                                    //第2個欄位
                                                                    arrayData[1] = lineData.substring(fieldStart[j],(lineData.length())).trim();
                                                                }
                                                                else
                                                                {
                                                                    arrayData[1] = lineData.substring(fieldStart[j],(fieldStart[j] + fieldLength[j])).trim();
                                                                }
                                                            }

                                                            if (fieldName[j].equalsIgnoreCase("交易日期"))
                                                            {
                                                                if (MaxField.equalsIgnoreCase(fieldName[j]) && lineData.length() < (fieldStart[j] + fieldLength[j]))
                                                                {
                                                                    //第3個欄位
                                                                    arrayData[2] = lineData.substring(fieldStart[j], (lineData.length())).trim();
                                                                }
                                                                else
                                                                {
                                                                    arrayData[2] = lineData.substring(fieldStart[j], (fieldStart[j] + fieldLength[j])).trim();
                                                                }
                                                            }

                                                            if (fieldName[j].equalsIgnoreCase("交易時間"))
                                                            {
                                                                if (MaxField.equalsIgnoreCase(fieldName[j]) && lineData.length() < (fieldStart[j] + fieldLength[j]))
                                                                {
                                                                    //第4個欄位
                                                                    arrayData[3] = lineData.substring(fieldStart[j], (lineData.length())).trim();
                                                                }
                                                                else
                                                                {
                                                                    arrayData[3] = lineData.substring(fieldStart[j],(fieldStart[j] +fieldLength[j])).trim();
                                                                }
                                                            }

                                                            if (fieldName[j].equalsIgnoreCase("授權碼"))
                                                            {
                                                                if (MaxField.equalsIgnoreCase(fieldName[j]) && lineData.length() < (fieldStart[j] + fieldLength[j]))
                                                                {
                                                                    //第5個欄位
                                                                    arrayData[4] = lineData.substring(fieldStart[j],(lineData.length())).trim();
                                                                }
                                                                else
                                                                {
                                                                    arrayData[4] = lineData.substring(fieldStart[j], (fieldStart[j] + fieldLength[j])).trim();
                                                                }
                                                            }

                                                            if (fieldName[j].equalsIgnoreCase("交易類別"))
                                                            {
                                                                if (MaxField.equalsIgnoreCase(fieldName[j]) && lineData.length() < (fieldStart[j] + fieldLength[j]))
                                                                {
                                                                    //第6個欄位
                                                                    arrayData[5] = lineData.substring(fieldStart[j], (lineData.length())).trim();
                                                                }
                                                                else
                                                                {
                                                                    arrayData[5] = lineData.substring(fieldStart[j], (fieldStart[j] + fieldLength[j])).trim();
                                                                }

                                                                TransCode = arrayData[5];
                                                            }

                                                            if (fieldName[j].equalsIgnoreCase("請款金額"))
                                                            {
                                                                if (MaxField.equalsIgnoreCase(fieldName[j]) && lineData.length() < (fieldStart[j] + fieldLength[j]))
                                                                {
                                                                    //第7個欄位
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
                                                                            // 購貨
                                                                            SaleCount++;
                                                                            SumSaleAmt = String.valueOf(Double.parseDouble(SumSaleAmt) + Double.parseDouble(arrayData[6]));
                                                                        }

                                                                        if (TransCode.equalsIgnoreCase("01"))
                                                                        {
                                                                            // 退貨
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
                                            	//新增檢核子特店代號是否相符
                                                if (HeadMerchantID.equalsIgnoreCase(MerchantID) && HeadSubMid.equals(SubMID))
                                                {
                                                    // 特店代號
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
                                                                    ArrayList arrayCardTest = UserBean.get_Test_Card(sysBean, "N","Y");   //  捉出測試卡
                                                                    Date insertToBatchstart =new Date();
                                                                    if ( insertToBatch(hashData, BatchPmtID, MerchantID, SubMID, HeadDate, HeadSerial, CaptureDay, hashMerchant, arrayTerminal, arrayCardTest))
                                                                    {
                                                                    	 Date insertToBatchend =new Date();
                                                                         System.out.println("!!!!!!!!!!!    insertToBatchend  End[ "+(insertToBatchend.getTime()-insertToBatchstart.getTime())/1000+"]!!!!!!!!!!!!!!!");
                                                                        
                                                                        // 確認總金額
                                                                        CheckCapture(MerchantID, SubMID, BatchPmtID, CaptureDay, hashMerchant, arrayTerminal);
                                                                        System.out.println("!!!!!!!!!!!    CheckCapture  End[ "+(new Date().getTime()-insertToBatchend.getTime())/1000+"]!!!!!!!!!!!!!!!");
//                                                            MerchantCaptureBean CaptureBean = new  MerchantCaptureBean();
                                                                        ArrayList arrayList = UserBean.get_Batch(sysBean, MerchantID, SubMID, BatchPmtID, "", "BATCHTERMINALID,BATCHSYSORDERID,BATCHTRANSCODE,BATCHTXDATE,BATCHTXTIME");                                                                        
                                                                        System.out.println("!!!!!!!!!!!    UserBean.get_Batch  End[ "+(new Date().getTime()-insertToBatchend.getTime())/1000+"]!!!!!!!!!!!!!!!");
                                                                        Hashtable   hashCaptureData = new  Hashtable();

                                                                        // 查詢請款資料
                                                                        hashCaptureData.put("DATALIST", arrayList);
                                                                        // 檔案序號
                                                                        hashCaptureData.put("BATCHPMTID", BatchPmtID);
                                                                        if (session.getAttribute("CaptureData") != null)
                                                                        {
                                                                            session.removeAttribute("CaptureData");
                                                                        }

                                                                        LogMemo = "資料上傳成功";
                                                                        LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                                                        LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "成功", LogMemo);
                                                                        log_user.debug(LogData);
                                                                        session.setAttribute("CaptureData", hashCaptureData);
                                                                        Forward = "./Merchant_BatchCapture_List.jsp";
                                                                    }
                                                                    else
                                                                    {
                                                                        LogMemo = "系統暫停請款交易-資料庫異常";
                                                                        LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                                                        LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                                                        log_user.debug(LogData);
                                                                        Message = "系統暫停請款交易請稍後再試";
                                                                        session.setAttribute("Message", Message);
                                                                        Forward = "./Merchant_Response.jsp";
                                                                    }
                                                                }
                                                                else
                                                                {
                                                                    Message = "請款總筆數金額不符";
                                                                    session.setAttribute("Message", Message);
                                                                    Forward = "./Merchant_Response.jsp";
                                                                    LogMemo = "請款總筆數金額不符";
                                                                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                                                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                                                    log_user.debug(LogData);
                                                                }
                                                            }
                                                            else
                                                            {
                                                                Message = "退貨請款總筆數金額不符";
                                                                session.setAttribute("Message", Message);
                                                                Forward = "./Merchant_Response.jsp";
                                                                LogMemo = "退貨請款總筆數金額不符";
                                                                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                                                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                                                log_user.debug(LogData);
                                                            }
                                                        }
                                                        else
                                                        {
                                                            Message = "購貨請款總筆數金額不符";
                                                            session.setAttribute("Message", Message);
                                                            Forward = "./Merchant_Response.jsp";
                                                            LogMemo = "購貨請款總筆數金額不符";
                                                            LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                                            LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                                            log_user.debug(LogData);
                                                        }
                                                    }
                                                    else
                                                    {
                                                        LogMemo = "檔案請款日期須為今日";
                                                        LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                                        LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                                        log_user.debug(LogData);

                                                        Message = "檔案請款日期須為今日";
                                                        session.setAttribute("Message", Message);
                                                        Forward = "./Merchant_Response.jsp";
                                                    }
                                                }
                                                else
                                                {
                                                    LogMemo = "檔案請款無法提供非本特店資料";
                                                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                                    log_user.debug(LogData);

                                                    Message = "檔案請款無法提供非本特店資料";
                                                    session.setAttribute("Message", Message);
                                                    Forward = "./Merchant_Response.jsp";
                                                }
                                            }
                                            else
                                            {
                                                LogMemo = "檔案請款資料長度有誤";
                                                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                                log_user.debug(LogData);

                                                Message = "檔案請款資料長度有誤";
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
                                // 整批請款
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

                                ArrayList arraySuccess = UserBean.get_Batch(sysBean, MerchantID, SubMID, BatchPmtID, "SUCCESS", "BATCHTERMINALID,BATCHSYSORDERID,BATCHTRANSCODE,BATCHTXDATE,BATCHTXTIME"); // 成功資料
                                ArrayList arrayFail = UserBean.get_Batch(sysBean, MerchantID, SubMID, BatchPmtID, "FAIL", "BATCHTERMINALID,BATCHSYSORDERID,BATCHTRANSCODE,BATCHTXDATE,BATCHTXTIME"); // 失敗資料

                                hashCarpture = new Hashtable();
                                hashCarpture.put("SUCCESS", arraySuccess);
                                hashCarpture.put("FAIL", arrayFail);
                                hashCarpture.put("BATCHPMTID", BatchPmtID); // 檔案序號

                                if (session.getAttribute("CaptureUpdateData") != null)
                                {
                                    session.removeAttribute("CaptureUpdateData");
                                }

                                session.setAttribute("CaptureUpdateData", hashCarpture);
                                LogMemo = "請款資料成功"+String.valueOf(arraySuccess.size())+"筆，失敗"+String.valueOf(arrayFail.size())+"筆";
                                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "成功", LogMemo);
                                log_user.debug(LogData);
                                Forward = "./Merchant_BatchCaptureShow_List.jsp";
                            }

                            if (Action.equalsIgnoreCase("Print"))
                            {
                                // 匯出
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

                                    field.put("SHOW", "檔案");
                                    
                                    // 因為與 "線上請款功能" 使用同一個rpt檔  因此增加TRANSTYPE的值 新增 Jimmy Kang 20150717 --- Start --- 
                                    field.put("TRANSTYPESHOW", "全部");
                                    // 因為與 "線上請款功能" 使用同一個rpt檔  因此增加TRANSTYPE的值 新增 Jimmy Kang 20150717 --- Start --- 
                                    
                                    String RPTName = "MerchantCaptureUpdateListReport.rpt";
                                    cr.createPDF(sysBean,SecurityTool.output(sql), request, response, "/Report/" + RPTName, field, PrintType);
                                    /* DatabaseBean used by UserBean has been pass by parameter.  Don't Close DB Here */
                                    // UserBean.close();
                                    LogMemo = "以"+PrintType+"格式匯出請款資料";
                                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "成功", LogMemo);
                                    log_user.debug(LogData);
                                }
                            }
                        }
                    }
                    else
                    {
                        Message = "特店無此功能權限";
                        Forward = "./Merchant_Response.jsp";
                        session.setAttribute("Message", Message);
                        LogMemo = "特店無此功能權限";
                        LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
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
                    LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "失敗", LogMemo);
                    log_user.debug(LogData);
                }
            }
            else
            {
                Message = "使用者無此權限請洽系統管理者";
                Forward = "./Merchant_NoUse.jsp";
                session.setAttribute("Message", Message);
                LogMemo = Message;
                LogUserName = hashMerUser.get("USER_ID").toString()+"("+hashMerUser.get("USER_NAME").toString()+")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "失敗", LogMemo);
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
     * 檔案資料寫入資料庫
     * @param Hashtable hashData 欲處理檔案資料
     * @param String BatchPmtID  批號
     * @param String MerchantID  特店代碼
     * @param String SubMID      服務代碼
     * @param String HeadDate    檔案表頭日期
     * @param String HeadSerial  檔案表頭序號
     * @param String CaptureDay  請款期限
     * @param Hashtable hashMerchant  特店資料
     * @param String arrayTermina  終端資料
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
                String Reauthflag = "";  // 再授權flag
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
                        BatchResponse = "端末機代號";
                    }

//                    System.out.println("TransDate=" + TransDate +",AddData[2]=" +AddData[2] + ",");
                    if (!TransDate.equalsIgnoreCase(AddData[2]))
                    {
                        BatchTxMsg = "FAIL";
                        if (BatchResponse.length() > 0)
                            BatchResponse = BatchResponse + ".";
                        BatchResponse = BatchResponse + "交易日期";
                    }

//                    System.out.println("TransTime=" + TransTime +",AddData[3]=" +AddData[3] + ",");
                    if (!TransTime.equalsIgnoreCase(AddData[3]))
                    {
                        BatchTxMsg = "FAIL";
                        if (BatchResponse.length() > 0)
                            BatchResponse = BatchResponse + ".";
                        BatchResponse = BatchResponse + "交易時間";
                    }

//                    System.out.println("ApproveCode=" + ApproveCode);
                    String StrApproveCode = ApproveCode.trim();
                    if (!StrApproveCode.equalsIgnoreCase(AddData[4]))
                    {
                        BatchTxMsg = "FAIL";
                        if (BatchResponse.length() > 0)
                            BatchResponse = BatchResponse + ".";

                        BatchResponse = BatchResponse + "授權碼";
                    }
//                    System.out.println("----------------- BatchTxMsg =" + BatchTxMsg);
                    if (BatchTxMsg.equalsIgnoreCase("FAIL"))
                    {
                        BatchResponse = BatchResponse + "錯誤";
                    }
                    else
                    {
                        if (AddData[6].length() == 0)
                        {
                            BatchTxMsg = "FAIL";
                            BatchResponse = BatchResponse + "請款金額未輸入";
                        }
                        else
                        {
                        	// Merchant Console 檔案請款作業模組  修改  by Jimmy Kang 20150717  -- 修改開始 --
                        	if (PAN.equalsIgnoreCase("62XX"))
                        	{
                        		BatchTxMsg = "FAIL";
                                BatchResponse = "銀聯交易未回覆";
                        	}
                        	else
                        	{
                        	// Merchant Console 檔案請款作業模組  修改  by Jimmy Kang 20150717  -- 修改結束 --
                        		
                        		if (UserBean.check_Test_Card(arrayCardTest,PAN))
                                {
                                    boolean Terminal_Current = UserBean.check_Terminal_Column(MerchantID, TerminalID, arrayTerminal,
                                                                    "CURRENTCODE", "B,D,E,F"); //  確認端末機狀態 B,D可購貨請款 B,D,E,F可退貨請款
                                    boolean Terminal_Permit = UserBean.check_Terminal_Column(MerchantID, TerminalID, arrayTerminal,
                                                                    "PERMIT_CAPTURE", "Y"); //  確認端末機權限

                                    if (Terminal_Current && Terminal_Permit)
                                    {
                                        if (Double.parseDouble(BalanceAmt) < 0)
                                        {
                                            BatchTxMsg = "FAIL";
                                            BatchResponse = "原授權資料已取消";
                                        }
                                        else
                                        {
                                            if (Double.parseDouble(BalanceAmt) == 0)
                                            {
                                                BatchTxMsg = "FAIL";
                                                BatchResponse = "原授權資料已完全請款";
                                            }
                                            else
                                            {
                                                boolean MerchantPartial = UserBean.check_Merchant_Column(hashMerchant,
                                                                        "PERMIT_PARTIAL_CAPTURE", "Y"); //  確認特店分批請款狀態
                                                boolean TerminalPartial = UserBean.check_Terminal_Column(MerchantID, TerminalID, arrayTerminal,
                                                                        "PERMIT_PARTIAL_CAPTURE", "Y"); //  確認端末機權限
                                                BatchTxMsg = "SUCCESS";
                                                BatchResponse = "可請款";

                                                if (TransCode.equalsIgnoreCase("00"))
                                                {
                                                    // 購貨請款
//                                                    if (!UserBean.check_Merchant_Column(hashMerchant,"CURRENTCODE", "B,D")  ){ //  確認特店狀態 E,F僅可退貨請款
//                                                        if (!UserBean.check_Terminal_Column(MerchantID, TerminalID,arrayTerminal,"CURRENTCODE", "E,F")) { //  確認特店狀態 E,F僅可退貨請款
                                                            boolean TransModePartial = false;
                                                            if (TransMode.equalsIgnoreCase("0"))
                                                                TransModePartial = true;
                                                            if (MerchantPartial && TerminalPartial && TransModePartial)
                                                            {
                                                                // 可分批
                                                                if (Double.parseDouble(AddData[6]) <= Double.parseDouble(TransAmt))
                                                                {
                                                                    BatchTxMsg = "SUCCESS";
                                                                    BatchResponse = "可請款";
                                                                }
                                                                else
                                                                {
                                                                    BatchTxMsg = "FAIL";
                                                                    BatchResponse = "請款金額超過交易金額";
                                                                }
                                                            }
                                                            else
                                                            {
                                                                if (Double.parseDouble(AddData[6]) == Double.parseDouble(TransAmt))
                                                                {
                                                                    BatchTxMsg = "SUCCESS";
                                                                    BatchResponse = "可請款";
                                                                }
                                                                else
                                                                {
                                                                    BatchTxMsg = "FAIL";
                                                                    BatchResponse = "不可分批請款";
                                                                }
                                                            }
//                                                        } else {
//                                                            BatchTxMsg = "FAIL";
//                                                            BatchResponse = "端末機無法提供購貨請款";
//                                                        }
//                                                    } else  {
//                                                        BatchTxMsg = "FAIL";
//                                                        BatchResponse = "特店無法提供購貨請款";
//                                                    }
                                                }

                                                if (TransCode.equalsIgnoreCase("01"))
                                                {
                                                    // 退貨請款
                                                   if (Double.parseDouble(AddData[6]) == Double.parseDouble(TransAmt))
                                                   {
                                                      BatchTxMsg = "SUCCESS";
                                                      BatchResponse = "可請款";
                                                   }
                                                   else
                                                   {
                                                      BatchTxMsg = "FAIL";
                                                      BatchResponse = "不可分批請款";
                                                   }
                                                }
                                            }
                                        }
                                    }
                                    else
                                    {
                                        BatchTxMsg = "FAIL";
                                        BatchResponse = "端末機無此權限";
                                    }
                                }
                                else
                                {
                                    BatchTxMsg = "FAIL";
                                    BatchResponse = "卡號為測試卡不可請款";
                                }
                        	// Merchant Console 檔案請款作業模組  修改  by Jimmy Kang 20150717  -- 修改開始 --
                        	}
                        	// Merchant Console 檔案請款作業模組  修改  by Jimmy Kang 20150717  -- 修改結束 --
                        }
                    }
                }
                else
                {
                    BatchTxMsg = "FAIL";
                    BatchResponse = "查無原交易授權資料";
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
        ArrayList arrayCardTest = new ArrayList();   //  捉出測試卡
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
     * 購貨請款交易 Capture
     * @param ArrayList hashCarpture 欲處理請款資料
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
                    String SumRefundCaptureAmt = "0"; // 退款請款金額統計
                    String SumCaptureAmt = "0"; // 購貨請款金額統計
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

                    BalCaptureAmt       = String.valueOf(Double.parseDouble(BalCaptureAmt)       + Double.parseDouble(SumCaptureAmt)); // 請款金額
                    BalRefundCaptureAmt = String.valueOf(Double.parseDouble(BalRefundCaptureAmt) + Double.parseDouble(SumRefundCaptureAmt)); // 請款金額
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
                        // 更新成功
                        CommitFlag = sysBean.commit();
                        if (CommitFlag)
                        {
                            System.out.println("-----------sysBean.commit ="+CommitFlag);
                            BatchTxMsg = "SUCCESS";
                            BatchResponse = "待處理";
                        }
                        else
                        {
                            BatchTxMsg = "FAIL";
                            BatchResponse = "請款更新失敗";
                        }
                    }
                    else
                    {
                        BatchTxMsg = "FAIL";
                        BatchResponse = "請款更新失敗";
                        sysBean.setRollBack();
                    }

                    if (!CommitFlag)
                    {
                        // 失敗
                        BatchTxMsg = "FAIL";
                        BatchResponse = "請款更新失敗";
                        CaptureBean.update_BillingeFail(sysBean, MerchantID, SubMID, Sys_OrderID, TransCode, "請款更新失敗", CaptureAmt, DueDate, CaptureDate.substring(0, 10));
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
