/************************************************************
 * <p>#File Name:   MerchantBatchVoidCtl.java   </p>
 * <p>#Description:                         </p>
 * <p>#Create Date: 2007/11/29              </p>
 * <p>#Company:     cybersoft               </p>
 * <p>#Notice:                      </p>
 * @author      Shirley Lin
 * @since       SPEC version
 * @version 0.1 2007/11/29  Shirley Lin
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
import com.cybersoft.merchant.bean.MerchantVoidBean;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.*;
import org.w3c.util.UUID;
import java.sql.ResultSet;
import com.cybersoft.bean.createReport;
import com.cybersoft.common.Util;
import com.cybersoft4u.util.StringUtil;
import com.fubon.security.filter.SecurityTool;
import com.cybersoft4u.prj.tfbpg.config.Constants;
import com.cybersoft.bean.LogUtils;

/**
 * <p>控制整批取消的Servlet</p>
 * @version 0.1 2007/11/29  Shiley Lin
 * 202112300619-01 20220210 GARY 請款批次請款規格增修(Visa Authorization Source Code) AUTH_SRC_CODE
 */
public class MerchantBatchVoidCtl extends HttpServlet
{
    private static final String CONTENT_TYPE = "text/html; charset=Big5";
    private String Forward = "./Merchant_Response.jsp"; // 網頁轉址
    private String Message = ""; // 顯示訊息
    java.util.Date nowdate;
    public UserBean UserBean = new UserBean();
    public DataBaseBean2 sysBean = new DataBaseBean2();
    public static final LogUtils log = new LogUtils(MerchantBatchVoidCtl.class);

    public void init()
    {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        this.doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        log.debug("---------- MerchantBatchVoidCtl Start ---------");
        response.setContentType(CONTENT_TYPE);
        request.setCharacterEncoding("BIG5");

        HttpSession session = request.getSession(true);
        /* Chech Session */
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

//request.getSession(true);
//        session.invalidate();
        try
        {
            boolean ForwardFlag = true;
            Hashtable hashSys = new Hashtable(); // 系統參數
            Hashtable hashMerUser = new Hashtable(); // 特店使用者
            Hashtable hashMerchant = new Hashtable(); // 特店主檔
            ArrayList arrayTerminal = new ArrayList(); // 端末機主檔
            Hashtable hashConfData = new Hashtable();

            hashConfData = (Hashtable) session.getAttribute("SYSCONFDATA");
            if (hashConfData == null)
            {
                hashConfData = new Hashtable();
            }

            if (hashConfData.size() > 0)
            {
                hashSys = (Hashtable) hashConfData.get("SYSCONF"); // 系統參數
                hashMerUser = (Hashtable) hashConfData.get("MERCHANT_USER"); // 特店使用者
                hashMerchant = (Hashtable) hashConfData.get("MERCHANT"); // 特店主檔
                if (hashSys == null)
                {
                    hashSys = new Hashtable();
                }

                if (hashMerUser == null)
                {
                    hashMerUser = new Hashtable();
                }

                if (hashMerchant == null)
                {
                    hashMerchant = new Hashtable();
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
//            UserBean UserBean = new UserBean();
            boolean Merchant_Current = UserBean.check_Merchant_Column(hashMerchant, "CURRENTCODE", "B,D"); //  確認特店狀態
            boolean Merchant_Permit  = UserBean.check_Merchant_Column(hashMerchant, "PERMIT_SALE_VOID", "Y"); //  確認特店權限
            boolean Merchant_Permit1 = UserBean.check_Merchant_Column(hashMerchant, "PERMIT_REFUND_VOID", "Y"); //  確認特店權限
            boolean Merchant_Permit2 = UserBean.check_Merchant_Column(hashMerchant, "PERMIT_CAPTURE_VOID", "Y"); //  確認特店權限

            if (User_Permit)
            {
                // 使用者權限
                if (Merchant_Current)
                {
                    //特店狀態
                    if (Merchant_Permit || Merchant_Permit1 || Merchant_Permit2)
                    {
                        // 特店權限
                        String Action = (String) request.getParameter("Action");
                        if (Action == null)
                        {
                            Action = "";
                        }

                        log.debug("Action=" + Action);
                        if (Action.length() == 0)
                        {
                            //直接轉到上傳檔案畫頁
                            Forward = "./Merchant_Void_Upload.jsp";
                        }
                        else
                        {
                            String MerchantID = hashMerchant.get("MERCHANTID").toString();
                            String SubMID = hashMerchant.get("SUBMID").toString();

                            if (Action.equalsIgnoreCase("Batch"))
                            {
                                // 整批上傳
                                Hashtable hashData = new Hashtable();
                                int iCount = 0;
                                int SaleCount = 0;
                                int RefundCount = 0;
                                int SaleCaptureCount = 0;
                                int RefundCaptureCount = 0;
                                String dataPath = "";
                                String MerchantPath = hashSys.get("MER_UPLOADTXT_PATH").toString();
                                String saveDirectory = MerchantPath + "/" + MerchantID + "/";
                                if (new File(MerchantPath).isDirectory())
                                {
//                                    System.out.println("dir exist");
                                }
                                else
                                {
//                                    System.out.println("dir not exist");
                                    new File(MerchantPath).mkdir();
                                }

                                if (new File(saveDirectory).isDirectory())
                                {
//                                    System.out.println("dir exist");
                                }
                                else
                                {
//                                    System.out.println("dir not exist");
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
//                                    System.out.println("[" + FileName + "]");

                                    File f = multi.getFile(name);
                                    if (FileName != null)
                                    {
//                                        System.out.println("你上傳的檔案：[" + FileName + "] 長度：[" + f.length() + "]");
                                        if (f.length() == 0)
                                        {
                                            Message = "檔案上傳失敗";
                                            session.setAttribute("Message", Message);
                                            Forward = "./Merchant_Response.jsp";
                                        }
                                        else
                                        {
                                            int[] fieldStart = {0, 16, 41, 49, 55, 63, 65};
                                            int[] fieldLength = {16, 25, 8, 6, 8, 2, 11};
                                            String[] fieldName = {"端末機代號", "特店單號", "交易日期",
                                                                  "交易時間", "授權碼", "交易類別", "請款金額"};

                                            int fieldNo = fieldName.length;
                                            dataPath = saveDirectory + FileName;
//                                            System.out.println(dataPath);
                                            BufferedReader bufferread = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath)));
                                            String HeadMerchantID = "";
                                            String HeadDate = "";
                                            String HeadSerial = "";
                                            String HeadSubMid = "";
                                            String TotalSalePcs = "0";
                                            String TotalSaleAmt = "0";
                                            String TotalRefundPcs = "0";
                                            String TotalRefundAmt = "0";
                                            String TotalSaleCapturePcs = "0";
                                            String TotalSaleCaptureAmt = "0";
                                            String TotalRefundCapturePcs = "0";
                                            String TotalRefundCaptureAmt = "0";
                                            String TotalPcs = "0";
                                            String TotalAmt = "0";
                                            String SumAmt = "0";
                                            String SumSaleAmt = "0";
                                            String SumRefundAmt = "0";
                                            String SumSaleCaptureAmt = "0";
                                            String SumRefundCaptureAmt = "0";
                                            boolean boolCheckLenFlag = true;

                                            while (true)
                                            {
                                                String lineData = bufferread.readLine();
                                                if (lineData == null)
                                                {
                                                    break;
                                                }
//                                                System.out.println("lineData len (" + lineData.length() + ")=" + lineData);
                                                if (lineData.length() == 28 || lineData.length() == 76 || lineData.length() == 105)
                                                {
                                                    if (lineData.length() == 28)
                                                    {
                                                        // Data Head
                                                    	//new MerchantId or SubMid
                                                        HeadMerchantID = lineData.substring(0, 16).trim();
                                                        HeadDate = lineData.substring(16, 24).trim();
                                                        HeadSerial = lineData.substring(24, 28).trim();
                                                        //子特店代號
                                                        HeadSubMid = lineData.substring(28, 40).trim();
                                                    }

                                                    if (lineData.length() == 76)
                                                    {
                                                        // Data
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
                                                                    //最後一個欄位
                                                                    arrayData[0] = lineData.substring(fieldStart[j], (lineData.length())).trim();
                                                                }
                                                                else
                                                                {
                                                                    arrayData[0] = lineData.substring(fieldStart[j], (fieldStart[j] + fieldLength[j])).trim();
                                                                }
                                                            }
                                                            if (fieldName[j].equalsIgnoreCase("特店單號"))
                                                            {
                                                                if (MaxField.equalsIgnoreCase(fieldName[j]) && lineData.length() < (fieldStart[j] + fieldLength[j]))
                                                                {
                                                                    //最後一個欄位
                                                                    arrayData[1] = lineData.substring(fieldStart[j], (lineData.length())).trim();
                                                                }
                                                                else
                                                                {
                                                                    arrayData[1] = lineData.substring(fieldStart[j], (fieldStart[j] + fieldLength[j])).trim();
                                                                }
                                                            }

                                                            if (fieldName[j].equalsIgnoreCase("交易日期"))
                                                            {
                                                                if (MaxField.equalsIgnoreCase(fieldName[j]) && lineData.length() < (fieldStart[j] + fieldLength[j]))
                                                                {
                                                                    //最後一個欄位
                                                                    arrayData[2] = lineData.substring(fieldStart[j], (lineData.length())).trim();                                                                } else {
                                                                    arrayData[2] = lineData.substring(fieldStart[j], (fieldStart[j] + fieldLength[j])).trim();
                                                                }
                                                            }

                                                            if (fieldName[j].equalsIgnoreCase("交易時間"))
                                                            {
                                                                if (MaxField.equalsIgnoreCase(fieldName[j]) && lineData.length() < (fieldStart[j] + fieldLength[j]))
                                                                {
                                                                    //最後一個欄位
                                                                    arrayData[3] = lineData.substring(fieldStart[j], (lineData.length())).trim();
                                                                }
                                                                else
                                                                {
                                                                    arrayData[3] = lineData.substring(fieldStart[j], (fieldStart[j] + fieldLength[j])).trim();
                                                                }
                                                            }

                                                            if (fieldName[j].equalsIgnoreCase("授權碼"))
                                                            {
                                                                if (MaxField.equalsIgnoreCase(fieldName[j]) && lineData.length() < (fieldStart[j] + fieldLength[j]))
                                                                {
                                                                    //最後一個欄位
                                                                    arrayData[4] = lineData.substring(fieldStart[j], (lineData.length())).trim();
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
                                                                    //最後一個欄位
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
                                                                    //最後一個欄位
                                                                    arrayData[6] = lineData.substring(fieldStart[j], (lineData.length())).trim();
                                                                }
                                                                else
                                                                {
                                                                    arrayData[6] = lineData.substring(fieldStart[j], (fieldStart[j] + fieldLength[j])).trim();
                                                                }

                                                                if (arrayData[6].length() > 0)
                                                                {
                                                                    SumAmt = String.valueOf(Double.parseDouble(SumAmt) + Double.parseDouble(arrayData[6]));
                                                                    log.debug("TransCode=" +TransCode);

                                                                    if (TransCode.length() > 0)
                                                                    {
                                                                        if (TransCode.equalsIgnoreCase("10")) { // 購貨取消
                                                                            SaleCount++;
                                                                            SumSaleAmt = String.valueOf(Double.parseDouble(SumSaleAmt) + Double.parseDouble(arrayData[6]));
                                                                        }

                                                                        if (TransCode.equalsIgnoreCase("11")) { // 退貨取消
                                                                            RefundCount++;
                                                                            SumRefundAmt = String.valueOf(Double.parseDouble(SumRefundAmt) + Double.parseDouble(arrayData[6]));
                                                                        }

                                                                        if (TransCode.equalsIgnoreCase("20")) { // 購貨請款取消
                                                                            SaleCaptureCount++;
                                                                            SumSaleCaptureAmt = String.valueOf(Double.parseDouble(SumSaleCaptureAmt) + Double.parseDouble(arrayData[6]));
                                                                        }

                                                                        if (TransCode.equalsIgnoreCase("21")) { // 退貨請款取消
                                                                            RefundCaptureCount++;
                                                                            SumRefundCaptureAmt = String.valueOf(Double.parseDouble(SumRefundCaptureAmt) + Double.parseDouble(arrayData[6]));
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        hashData.put(String.valueOf(hashData.size()),arrayData);
                                                    }

                                                    if (lineData.length() == 105)
                                                    {
                                                        // Data End
                                                        TotalPcs = lineData.substring(0,8).trim();
                                                        TotalSalePcs = lineData.substring(8, 16).trim();
                                                        TotalRefundPcs = lineData.substring(16,24).trim();
                                                        TotalSaleCapturePcs = lineData.substring(24, 32).trim();
                                                        TotalRefundCapturePcs = lineData.substring(32,40).trim();
                                                        TotalAmt = lineData.substring(40, 53).trim();
                                                        TotalSaleAmt = lineData.substring(53,66).trim();
                                                        TotalRefundAmt = lineData.substring(66,79).trim();
                                                        TotalSaleCaptureAmt = lineData.substring(79,92).trim();
                                                        TotalRefundCaptureAmt = lineData.substring(92,105).trim();
                                                    }
                                                }
                                                else
                                                {
                                                    boolCheckLenFlag = false;
                                                    break;
                                                }
                                            }

                                            if (boolCheckLenFlag)
                                            {
                                            	 // 特店代號
                                            	//新增檢核子特店代號是否相符
                                                if (HeadMerchantID.equalsIgnoreCase(MerchantID) && HeadSubMid.equals(SubMID))
                                                {
                                                   
                                                    String Today = UserBean.get_TransDate("yyyyMMdd");
                                                    if (Today.equalsIgnoreCase(HeadDate))
                                                    {
                                                        if (SaleCount == Integer.parseInt(TotalSalePcs) && Double.parseDouble(TotalSaleAmt) == Double.parseDouble(SumSaleAmt))
                                                        {
                                                            if (RefundCount == Integer.parseInt(TotalRefundPcs) && Double.parseDouble(TotalRefundAmt) == Double.parseDouble(SumRefundAmt))
                                                            {
                                                                if (SaleCount == Integer.parseInt(TotalSalePcs) && Double.parseDouble(TotalSaleAmt) == Double.parseDouble(SumSaleAmt))
                                                                {
                                                                    if (RefundCount == Integer.parseInt(TotalRefundPcs) && Double.parseDouble(TotalRefundAmt) == Double.parseDouble(SumRefundAmt))
                                                                    {
                                                                        if (iCount == Integer.parseInt(TotalPcs) && Double.parseDouble(TotalAmt) == Double.parseDouble(SumAmt))
                                                                        {
                                                                            UUID uuid = new UUID();
                                                                            String BatchPmtID = uuid.toString().toUpperCase();
                                                                            log.debug("BatchPmtID=" +BatchPmtID);
                                                                            if (insertToBatch( hashData, BatchPmtID, MerchantID, SubMID, HeadDate, HeadSerial, hashMerchant, arrayTerminal))
                                                                            {
                                                                                ArrayList arrayList = UserBean.get_Batch(sysBean,MerchantID, SubMID, BatchPmtID, "", "BATCHTERMINALID,BATCHSYSORDERID,BATCHTRANSCODE,BATCHTXDATE,BATCHTXTIME");
                                                                                Hashtable hashCaptureData = new Hashtable();
                                                                                hashCaptureData.put("DATALIST",arrayList); // 查詢請款資料
                                                                                hashCaptureData.put("BATCHPMTID", BatchPmtID); // 批次序號
                                                                                if (session.getAttribute("CaptureData") != null)
                                                                                {
                                                                                     session.removeAttribute("CaptureData");
                                                                                }

                                                                                session.setAttribute("CaptureData",hashCaptureData);
                                                                                Forward = "./Merchant_BatchCapture_List.jsp";
                                                                            }
                                                                            else
                                                                            {
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
                                                                        }
                                                                    }
                                                                    else
                                                                    {
                                                                        Message = "退貨請款取消總筆數金額不符";
                                                                        session.setAttribute("Message", Message);
                                                                        Forward ="./Merchant_Response.jsp";

                                                                    }
                                                                }
                                                                else
                                                                {
                                                                    Message = "購貨請款取消總筆數金額不符";
                                                                    session.setAttribute("Message",Message);
                                                                    Forward = "./Merchant_Response.jsp";
                                                                }
                                                            }
                                                            else
                                                            {
                                                                Message ="退貨取消總筆數金額不符";
                                                                session.setAttribute("Message", Message);
                                                                Forward = "./Merchant_Response.jsp";

                                                            }
                                                        }
                                                        else
                                                        {
                                                            Message = "購貨取消總筆數金額不符";
                                                            session.setAttribute("Message", Message);
                                                            Forward = "./Merchant_Response.jsp";
                                                        }
                                                    }
                                                    else
                                                    {
                                                        Message = "批次請款日期須為今日";
                                                        session.setAttribute("Message", Message);
                                                        Forward = "./Merchant_Response.jsp";
                                                    }
                                                }
                                                else
                                                {
                                                    Message = "批次請款無法提供非本特店資料";
                                                    session.setAttribute("Message", Message);
                                                    Forward = "./Merchant_Response.jsp";
                                                }
                                            }
                                            else
                                            {
                                                Message = "批次請款資料長度有誤";
                                                session.setAttribute("Message", Message);
                                                Forward = "./Merchant_Response.jsp";
                                            }

                                            bufferread.close();
                                            new File(dataPath).delete();
                                        }
                                    }
                                }
                            }

                            if (Action.equalsIgnoreCase("Capture"))
                            {
                                // 整批請款
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

                                ArrayList arraySuccess = UserBean.get_Batch(sysBean, MerchantID, SubMID, BatchPmtID, "SUCCESS", "BATCHTERMINALID,BATCHSYSORDERID,BATCHTRANSCODE,BATCHTXDATE,BATCHTXTIME"); // 成功資料
                                ArrayList arrayFail = UserBean.get_Batch(sysBean, MerchantID, SubMID, BatchPmtID, "FAIL", "BATCHTERMINALID,BATCHSYSORDERID,BATCHTRANSCODE,BATCHTXDATE,BATCHTXTIME"); // 失敗資料

                                hashCarpture = new Hashtable();
                                hashCarpture.put("SUCCESS", arraySuccess);
                                hashCarpture.put("FAIL", arrayFail);
                                hashCarpture.put("BATCHPMTID", BatchPmtID); // 批次序號

                                if (session.getAttribute("CaptureUpdateData") != null)
                                {
                                    session.removeAttribute("CaptureUpdateData");
                                }

                                session.setAttribute("CaptureUpdateData", hashCarpture);
                                Forward = "./Merchant_BatchCaptureShow_List.jsp";
                            }

                            if (Action.equalsIgnoreCase("Print"))
                            { // 匯出
                                ForwardFlag = false;
                                String BatchPmtID = (request.getParameter("BatchPmtID") == null) ? "" : UserBean.trim_Data(request.getParameter("BatchPmtID"));
                                String PrintType  = (request.getParameter("PrintType") == null) ? "" : UserBean.trim_Data(request.getParameter("PrintType"));
                                if (BatchPmtID.length() > 0 && PrintType.length() > 0)
                                {
                                    boolean RowdataFlag = true;
                                    if (PrintType.equalsIgnoreCase("PDF")) RowdataFlag = false;
                                    String sql = UserBean.get_Batch_Result(sysBean,MerchantID, SubMID, BatchPmtID, "",
                                                "BATCHTERMINALID,BATCHSYSORDERID,BATCHTRANSCODE,BATCHTXDATE,BATCHTXTIME", RowdataFlag);
                                    createReport cr = new createReport();
                                    Hashtable field = new Hashtable();
                                    field.put("SHOW", "批次");
                                    String RPTName = "MerchantCaptureUpdateListReport.rpt";
                                    cr.createPDF(sysBean,SecurityTool.output(sql), request, response, "/Report/" + RPTName, field, PrintType);
                                    // UserBean.closeConn();
                                }
                            }
                        }
                    }
                    else
                    {
                        Message = "特店無此功能權限";
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
                Message = "使用者無此權限請洽系統管理者";
                Forward = "./Merchant_NoUse.jsp";
                session.setAttribute("Message", Message);
            }

            if (ForwardFlag)
            {
                log.debug("Forward=" + Forward);
                request.getRequestDispatcher(Forward).forward(request, response);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
     * 批次資料寫入資料庫
     * @param Hashtable hashData 欲處理批次資料
     * @param String BatchPmtID  批號
     * @param String MerchantID  特店代碼
     * @param String SubMID      服務代碼
     * @param String HeadDate    批次表頭日期
     * @param String HeadSerial  批次表頭序號
     * @param String CaptureDay  請款期限
     * @param Hashtable hashMerchant  特店資料
     * @param String arrayTermina  終端資料
     */
    private boolean insertToBatch(Hashtable hashData, String BatchPmtID, String MerchantID, String SubMID,
                                  String HeadDate, String HeadSerial, Hashtable hashMerchant, ArrayList arrayTerminal)
    {
        boolean flag = true;
        try
        {
            MerchantVoidBean VoidBean = new MerchantVoidBean();
//            UserBean UserBean = new UserBean();
//            DataBaseBean SysBean = new DataBaseBean();
//            sysBean.setAutoCommit(false);
            boolean boolSaleVoid    = UserBean.check_Merchant_Column(hashMerchant,"PERMIT_SALE_VOID", "Y");
            boolean boolRefundVoid  = UserBean.check_Merchant_Column(hashMerchant,"PERMIT_REFUND_VOID", "Y");
            boolean boolCaptureVoid = UserBean.check_Merchant_Column(hashMerchant,"PERMIT_CAPTURE_VOID", "Y");
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
                String CaptureRowID = "";
                String AUTH_SRC_CODE = "";
                
                ArrayList arrayList = new ArrayList();
                Hashtable hashStatus = new Hashtable();
                Hashtable hashBillingData = new Hashtable();

                if (AddData[5].equalsIgnoreCase("10"))
                {
                    String TmpTransCode = "00";
                    arrayList = VoidBean.get_BillingVoid_List(sysBean, MerchantID, SubMID, BatchSysorderID, TmpTransCode,
                                                              AddData[2], AddData[3], AddData[4], AddData[6]);
                    if (arrayList == null) arrayList = new ArrayList();
                    hashStatus = VoidBean.check_SaleVoid_Status(arrayList);
                }

                if (AddData[5].equalsIgnoreCase("11"))
                {
                    String TmpTransCode = "01";
                    log.debug("Start TransCode = 11");
                    arrayList = VoidBean.get_BillingVoid_List(sysBean, MerchantID, SubMID, BatchSysorderID, TmpTransCode,
                                                          AddData[2], AddData[3], AddData[4], AddData[6]);
                    if (arrayList == null) arrayList = new ArrayList();
                    hashStatus = VoidBean.check_RefundVoid_Status(arrayList);
                }

                if (AddData[5].equalsIgnoreCase("20") || AddData[5].equalsIgnoreCase("21"))
                {
                    String TmpTransCode = "";
                    if (AddData[5].equalsIgnoreCase("20"))
                    {
                        TmpTransCode = "00";
                    }

                    if (AddData[5].equalsIgnoreCase("21"))
                    {
                        TmpTransCode = "01";
                    }

                    arrayList = VoidBean.get_CaptureVoid_List(sysBean, MerchantID, SubMID, AddData[0],AddData[1],
                                                              AddData[2], AddData[3], AddData[4], TmpTransCode, AddData[6]);
                    if (arrayList == null) arrayList = new ArrayList();
                    hashStatus = VoidBean.check_CaptureVoid_Status(arrayList);
                }

                if(arrayList.size() > 0)
                {
                    hashBillingData = (Hashtable)arrayList.get(0);
                }

                if (hashBillingData.size() > 0)
                {
                    TerminalID = hashBillingData.get("TERMINALID").toString().trim();
                    AcquirerID = hashBillingData.get("ACQUIRERID").toString().trim();
                    OrderID = hashBillingData.get("ORDERID").toString().trim();
                    Sys_OrderID = hashBillingData.get("SYS_ORDERID").toString().trim();
                    Card_Type = hashBillingData.get("CARD_TYPE").toString().trim();
                    PAN = hashBillingData.get("PAN").toString().trim();

                    ExpireDate = hashBillingData.get("EXPIREDATE").toString().trim();
                    TransCode = hashBillingData.get("TRANSCODE").toString();
                    TransDate = hashBillingData.get("TRANSDATE").toString().trim();
                    TransTime = hashBillingData.get("TRANSTIME").toString().trim();
                    CurrencyCode = hashBillingData.get("CURRENCYCODE").toString();
                    TransAmt = hashBillingData.get("TRANSAMT").toString().trim();
                    ApproveCode = hashBillingData.get("APPROVECODE").toString().trim();
                    ResponseCode = hashBillingData.get("RESPONSECODE").toString().trim();
                    ResponseMsg = hashBillingData.get("RESPONSEMSG").toString().trim();
                    BatchNo = hashBillingData.get("BATCHNO").toString().trim();
                    UserDefine = hashBillingData.get("USERDEFINE").toString().trim();
                    Entry_Mode = hashBillingData.get("ENTRY_MODE").toString().trim();
                    Condition_Code = hashBillingData.get("CONDITION_CODE").toString().trim();
                    TransMode = hashBillingData.get("TRANSMODE").toString().trim();
                    ECI = hashBillingData.get("ECI").toString().trim();
                    CAVV = hashBillingData.get("CAVV").toString().trim();
                    InstallType = hashBillingData.get("INSTALLTYPE").toString().trim();
                    Install = hashBillingData.get("INSTALL").toString().trim();
                    FirstAmt = hashBillingData.get("FIRSTAMT").toString().trim();
                    EachAmt = hashBillingData.get("EACHAMT").toString().trim();
                    FEE = hashBillingData.get("FEE").toString().trim();
                    RedemType = hashBillingData.get("REDEMTYPE").toString().trim();
                    RedemUsed = hashBillingData.get("REDEMUSED").toString().trim();
                    RedemBalance = hashBillingData.get("REDEMBALANCE").toString().trim();
                    CreditAmt = hashBillingData.get("CREDITAMT").toString().trim();
                    BillMessage = hashBillingData.get("BILLMESSAGE").toString().trim();
                    AUTH_SRC_CODE = hashBillingData.get("AUTH_SRC_CODE").toString(); //20220210

                    if (AddData[5].equalsIgnoreCase("10") || AddData[5].equalsIgnoreCase("11"))
                    {
                        BalanceAmt = hashBillingData.get("BALANCEAMT").toString().trim();
                        XID = hashBillingData.get("XID").toString();
                        SocialID = hashBillingData.get("SOCIALID").toString().trim();
                        RRN = hashBillingData.get("RRN").toString().trim();
                        MTI = hashBillingData.get("MTI").toString().trim();
                        ReversalFlag = hashBillingData.get("REVERSALFLAG").toString().trim();
                        ExtenNo = hashBillingData.get("EXTENNO").toString().trim();
                        EMail = hashBillingData.get("EMAIL").toString().trim();
                        TransType = hashBillingData.get("TRANSTYPE").toString().trim();
                    }

                    SysTraceNo = hashBillingData.get("SYSTRACENO").toString().trim();
                    if (!TerminalID.equalsIgnoreCase(AddData[0]))
                    {
                        BatchTxMsg = "FAIL";
                        BatchResponse = "端末機代號";
                    }

                    if (!TransDate.equalsIgnoreCase(AddData[2]))
                    {
                        BatchTxMsg = "FAIL";
                        if (BatchResponse.length() > 0)
                            BatchResponse = BatchResponse + ".";
                        BatchResponse = BatchResponse + "交易日期";
                    }

                    if (!TransTime.equalsIgnoreCase(AddData[3]))
                    {
                        BatchTxMsg = "FAIL";
                        if (BatchResponse.length() > 0)
                            BatchResponse = BatchResponse + ".";
                        BatchResponse = BatchResponse + "交易時間";
                    }

                    if (!ApproveCode.equalsIgnoreCase(AddData[4]))
                    {
                        BatchTxMsg = "FAIL";
                        if (BatchResponse.length() > 0)
                            BatchResponse = BatchResponse + ".";
                        BatchResponse = BatchResponse + "授權碼";
                    }

                    if (BatchTxMsg.equalsIgnoreCase("FAIL"))
                    {
                        BatchResponse = BatchResponse + "錯誤";
                    }
                    else
                    {
                        if (AddData[6].length() == 0)
                        {
                            BatchTxMsg = "FAIL";
                            BatchResponse = BatchResponse + "金額未輸入";
                        }
                        else
                        {
                            boolean Merchant_Permit =  false;
                            if (AddData[5].equalsIgnoreCase("10"))
                            {
                                Merchant_Permit = boolSaleVoid;
                            }

                            if (AddData[5].equalsIgnoreCase("11"))
                            {
                                Merchant_Permit = boolRefundVoid;
                            }

                            if (AddData[5].equalsIgnoreCase("20") || AddData[5].equalsIgnoreCase("21"))
                            {
                                Merchant_Permit = boolCaptureVoid;
                            }

                            if (Merchant_Permit)
                            {
                                boolean Terminal_Current = UserBean.check_Terminal_Column(MerchantID, TerminalID,arrayTerminal,"CURRENTCODE", "B,D"); //  確認端末機狀態
                                boolean Terminal_Permit = false;
                                Terminal_Permit = UserBean.check_Terminal_Column(MerchantID, TerminalID, arrayTerminal,"PERMIT_SALE_VOID", "Y"); //  確認端末機權限
                                if (Terminal_Current && Terminal_Permit)
                                {
//                                    if (AddData[5].equalsIgnoreCase("10")) {
//                                        if (Double.parseDouble(BalanceAmt) < 0) {
//                                            BatchTxMsg = "FAIL";
//                                            BatchResponse = "購貨資料已取消";
//                                        } else {
//                                        }
//                                    }
//                                    if (AddData[5].equalsIgnoreCase("11")) {
//                                        if (Double.parseDouble(BalanceAmt) < 0) {
//                                            BatchTxMsg = "FAIL";
//                                            BatchResponse = "退貨資料已取消";
//                                        } else {
//                                        }
//                                    }
//                                    if (AddData[5].equalsIgnoreCase("20") || AddData[5].equalsIgnoreCase("21")) {
//                                        if (Double.parseDouble(BalanceAmt) < 0) {
//                                            BatchTxMsg = "FAIL";
//                                            BatchResponse = "購貨資料已取消";
//                                        } else {
//                                        }
//                                    }
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
                                BatchResponse = "特店無此權限";

                            }
                        }
                    }
                }
                else
                {
                    BatchTxMsg = "FAIL";
                    BatchResponse = "查無原交易授權資料";
                }
                //20220210 ADD AUTH_SRC_CODE
                if (!UserBean.insert_Batch(sysBean, MerchantID, SubMID, TerminalID, AcquirerID, OrderID,
                                           Sys_OrderID, Card_Type, PAN, ExtenNo, ExpireDate, TransCode,
                                           ReversalFlag, TransDate, TransTime, CurrencyCode, TransAmt,
                                           ApproveCode, ResponseCode, ResponseMsg, BatchNo, UserDefine,
                                           EMail, MTI, RRN, SocialID, Entry_Mode, Condition_Code,
                                           TransMode, TransType, ECI, CAVV, XID, InstallType, Install,
                                           FirstAmt, EachAmt, FEE, RedemType, RedemUsed, RedemBalance,
                                           CreditAmt, BillMessage, BalanceAmt, CaptureAmt, CaptureDate,
                                           FeedbackCode, FeedbackMsg, FeedbackDate, Due_Date, BatchPmtID,
                                           BatchDate, BatchHead, BatchType, BatchTerminalID, BatchSysorderID,
                                           BatchTxDate, BatchTxTime, BatchTxApproveCode, BatchTransCode,
                                           BatchTxAmt, BatchTxMsg, BatchResponse, SysTraceNo, "",AUTH_SRC_CODE))
                {
                    flag = false;
                    break;
                }
            }

            if (flag)
            {
                sysBean.commit();
            }
            else
            {
                sysBean.setRollBack();
            }

            // sysBean.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
//        System.out.println("flag=" + flag);
        return flag;
    }
}
