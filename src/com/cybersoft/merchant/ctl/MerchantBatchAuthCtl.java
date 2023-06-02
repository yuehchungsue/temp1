/************************************************************
 * <p>#File Name:   MerchantBatchAuthCtl.java   </p>
 * <p>#Description:                         </p>
 * <p>#Create Date: 2008/06/02              </p>
 * <p>#Company:     cybersoft               </p>
 * <p>#Notice:                      </p>
 * @author
 * @since       SPEC version
 * @version 0.1 2008/06/02  Shirley Lin
 ************************************************************/
package com.cybersoft.merchant.ctl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft.bean.FtpBean;
import com.cybersoft.bean.LogUtils;
import com.cybersoft.bean.SessionControlBean;
import com.cybersoft.bean.UserBean;
import com.cybersoft.common.SFTPutil;
import com.cybersoft.common.Util;
import com.cybersoft.merchant.bean.MerchantBatchAuthBean;
import com.cybersoft.merchant.bean.MerchantLoginBean;
import com.cybersoft4u.util.CheckPan;
import com.oreilly.servlet.Base64Decoder;
import com.oreilly.servlet.MultipartRequest;

/**
 * <p>控制整批退貨的Servlet</p>
 * @version 0.1 2007/10/09  Shiley Lin
 * 2018.01.22 HKP change JCE to jasypt
 */
public class MerchantBatchAuthCtl extends HttpServlet
{
    private static final String CONTENT_TYPE = "text/html; charset=Big5";
    
    java.util.Date nowdate;
    public static final LogUtils log_user = new LogUtils("user");
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    public UserBean UserBean = new UserBean();
    public DataBaseBean2 sysBean = new DataBaseBean2();
    //20160216 Jason 移動至dopost
    //String LogUserName = "";
    //private String Message = ""; // 顯示訊息
    //String LogData = "";
    //String LogMemo = "";
    //String LogMerchantID = "";
    //private String Forward = "./Merchant_Response.jsp"; // 網頁轉址
    
    String LogFunctionName = "檔案授權作業";
    String LogStatus = "失敗";

    public void init()
    {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        this.doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	System.out.println("BATCH AUTH VER:20160216");
    	//20160216 Jason 移動至dopost
        String LogUserName = "";
        String Message = ""; // 顯示訊息
        String LogData = "";
        String LogMemo = "";
        String LogMerchantID = "";
        String Forward = "./Merchant_Response.jsp"; // 網頁轉址
        String KEY = "";
        String IV = "";
        response.setContentType(CONTENT_TYPE);
        request.setCharacterEncoding("BIG5");
        /**2018.01.22 change S002 to S003**/
        Hashtable ftphostInfo = UserBean.get_FtpHostInfo("S003");
        
        HttpSession session = request.getSession(true);
        /* Chech Session */
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

        Hashtable hashSys = new Hashtable(); // 系統參數
        Hashtable hashMerUser = new Hashtable(); // 特店使用者
        Hashtable hashMerchant = new Hashtable(); // 特店主檔
        ArrayList arrayTerminal = new ArrayList(); // 端末機主檔        
        Hashtable hashConfData = new Hashtable();
        ArrayList ftpInfo = new ArrayList();//FTP Info

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

            boolean Merchant_Current = false; // 特店狀態
            boolean Merchant_Permit = false; // 特店權限
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

                if (Action.length() == 0)
                {
                    // 重新更新資料
                    String MerchantID = hashMerUser.get("MERCHANT_ID").toString();
                    String UserID = hashMerUser.get("USER_ID").toString();
                    MerchantLoginBean LoginBean = new MerchantLoginBean();
                    Hashtable hashtmpMerchant = LoginBean.get_Merchant(sysBean, MerchantID); //特店主檔
                    if (hashtmpMerchant != null && hashtmpMerchant.size() > 0)
                    {
                        hashConfData.put("MERCHANT", hashtmpMerchant);
                    }

                    ArrayList arraytmpTerminal = LoginBean.get_Terminal(sysBean, MerchantID); // 端末機主檔
                    if (arraytmpTerminal != null && arraytmpTerminal.size() > 0)
                    {
                        hashConfData.put("TERMINAL", arraytmpTerminal);
                    }

                    //Hashtable hashtmpMerUser = LoginBean.get_Merchant_User(SysBean, MerchantID, UserID);
                    Hashtable hashtmpMerUser = LoginBean.get_Merchant_User(sysBean,MerchantID,UserID,"" ,"" ,String.valueOf(hashSys.get("MER_PWD_DAY")));
                    if (hashtmpMerUser != null && hashtmpMerUser.size() > 0)
                    {
                        hashConfData.put("MERCHANT_USER", hashtmpMerUser);
                        hashMerUser = hashtmpMerUser;
                    }

                    session.setAttribute("SYSCONFDATA", hashConfData);
                }

                hashMerchant = (Hashtable) hashConfData.get("MERCHANT"); // 特店主檔
                if (hashMerchant == null)
                {
                    hashMerchant = new Hashtable();
                }

                if (hashMerchant.size() > 0)
                {
                    LogMerchantID = (String) hashMerUser.get("MERCHANT_ID");
                }

                arrayTerminal = (ArrayList) hashConfData.get("TERMINAL"); // 端末機主檔
                if (arrayTerminal == null)
                {
                    arrayTerminal = new ArrayList();
                }
            }

            // UserBean UserBean = new UserBean();
            Merchant_Current = UserBean.check_Merchant_Column(hashMerchant, "CURRENTCODE", "B,C"); //  確認特店狀態
            Merchant_Permit = UserBean.check_Merchant_Column(hashMerchant, "SUPPORT_BATCH_AUTH", "Y"); //  確認特店權限

            if (User_Permit)
            {
                // 使用者權限
                if (Merchant_Current)
                {
                    //特店狀態
                    if (Merchant_Permit)
                    {
                        // 特店權限
                        if (Action.length() == 0)
                        {
                            //直接轉到上傳檔案畫頁
                            Forward = "./Merchant_BatchAuth.jsp";
                        }
                        else
                        {
                            if (Action.equalsIgnoreCase("Batch"))
                            {
                                // 整批上傳
                                response.addHeader("Pragma", "No-cache");
                                response.addHeader("Cache-Control", "no-cache");
                                response.setDateHeader("Expires", 0);

                                String dataPath = "";
                                String MerchantPath = hashSys.get("MER_UPLOADTXT_PATH").toString();
                                String MerchantID = hashMerchant.get("MERCHANTID").toString();
                                String saveDirectory = MerchantPath + "/" + MerchantID + "/";
                                System.out.println("MerchantBatchAuthCtl saveDirector:"+saveDirectory);
                                if (new File(MerchantPath).isDirectory())
                                {
                                	System.out.println("MerchantBatchAuthCtl new File(MerchantPath).isDirectory()");
                                }
                                else
                                {
                                	System.out.println("MerchantBatchAuthCtl File(MerchantPath).mkdir()-S-");
                                    new File(MerchantPath).mkdir();
                                    System.out.println("MerchantBatchAuthCtl File(MerchantPath).mkdir()-E-");
                                }

                                if (new File(saveDirectory).isDirectory())
                                {
                                	System.out.println("MerchantBatchAuthCtl new File(saveDirectory).isDirectory()");
                                }
                                else
                                {
                                	System.out.println("MerchantBatchAuthCtl new File(saveDirectory).mkdir()-S-");
                                    new File(saveDirectory).mkdir();
                                    System.out.println("MerchantBatchAuthCtl new File(saveDirectory).mkdir()-E-");
                                }

                                try
                                {
                                    saveDirectory = "/temp_file/";
                                    int maxPostSize = 1 * 1024 * 1024;
                                    String FileName = null;
                                    MultipartRequest multi;

                                    multi = new MultipartRequest(request, saveDirectory, maxPostSize, "BIG5");
                                    System.out.println("---------------OK");
                                    Enumeration filesname = multi.getFileNames();
                                    String content = "";

                                    while (filesname.hasMoreElements())
                                    {
                                        String name = (String) filesname.nextElement();
                                        FileName = multi.getFilesystemName(name);
                                        File f = multi.getFile(name);

                                        if (FileName != null)
                                        {
                                            if (f.length() == 0) {
                                                Message = "檔案上傳失敗";
                                                session.setAttribute("Message", Message);
                                                Forward =
                                                        "./Merchant_Response.jsp";
                                                LogMemo = Message;
                                                LogUserName = hashMerUser.get("USER_ID").toString() + "(" + hashMerUser.get("USER_NAME").toString() + ")";
                                                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                                log_user.debug(LogData);
                                            }
                                            else
                                            {
                                                String CheckMsg = "";
                                                LogMemo = "檔案上傳成功";
                                                dataPath = saveDirectory + FileName;
                                                BufferedReader bufferread = new BufferedReader(new InputStreamReader(new FileInputStream(dataPath)));
                                                System.out.println(System.getProperties());

                                                boolean boolCheckLenFlag = true;
                                                MerchantBatchAuthBean mba = new MerchantBatchAuthBean();
                                                ArrayList input_data = new ArrayList();
                                                String warn_message = "";
                                                int row_count = 0;
                                                String filename = FileName.substring(FileName.length() - 3);

                                                if (filename.equalsIgnoreCase("csv"))
                                                {
                                                    while (true)
                                                    {
                                                        row_count++;
                                                        System.out.println("-------row_count="+row_count);
                                                        String lineData = bufferread.readLine();
                                                        System.out.println("-------lineData="+lineData);
                                                        if (lineData != null && !lineData.equals(""))
                                                            content += lineData + "\r\n";

                                                        if (lineData == null || lineData.length() == 0 )
                                                        {
                                                            warn_message = "無資料!!";
                                                            break;
                                                        }

                                                        lineData = lineData.replaceAll("\n","").replaceAll("\r","").replaceAll("\r\n","").replaceAll(" ","");
                                                        System.out.println("lineData len (" + lineData.length() + ")=" + lineData);
                                                        lineData = lineData.replaceAll(","," , ");
                                                        System.out.println( " -----------lineData len (" +  lineData.length() + ")=(" + lineData+")");
                                                        String data[] = lineData.split(",");
                                                        Hashtable condition = new Hashtable();
                                                        boolean check_point = true;

                                                        if (data.length == 10)
                                                        {
                                                            //check file data length!!
                                                            boolean chk_tran_mode = false;
                                                            condition.put("Terminal", UserBean.trim_Data(data[0]));
                                                            if (data[0] != null && UserBean.trim_Data(data[0]).length() > 0)
                                                            {
                                                                Hashtable condition_t = mba.get_Terminal(sysBean); //取得條件
                                                                try
                                                                {
                                                                    Hashtable condition_d = (Hashtable) condition_t.get(LogMerchantID + UserBean.trim_Data(data[0]));
                                                                    String check_CURRENTCODE = String.valueOf(condition_d.get("CURRENTCODE"));
                                                                    String chechk_TRANS_TYPE = String.valueOf(condition_d.get("TRANS_TYPE"));
                                                                    if (!chechk_TRANS_TYPE.equalsIgnoreCase("A"))
                                                                    {
                                                                        CheckMsg = "(M04)";
                                                                        warn_message += "第" + row_count + "筆端末機代號,";
                                                                        check_point = false;
                                                                    }
                                                                    else
                                                                    {
                                                                        if ((!check_CURRENTCODE.equalsIgnoreCase("B") && !check_CURRENTCODE.equalsIgnoreCase("C")))
                                                                        {
                                                                            CheckMsg = "(M05)";
                                                                            warn_message += "第" + row_count + "筆端末機代號,";
                                                                            check_point = false;
                                                                        }
                                                                    }
                                                                }
                                                                catch (Exception e)
                                                                {
                                                                    warn_message += "第" + row_count + "筆端末機代號,";
                                                                    check_point = false;
                                                                    CheckMsg = "(M03)";
                                                                }
                                                            }
                                                            else
                                                            {
                                                                warn_message += "第" + row_count + "筆端末機代號,";
                                                                check_point = false;
                                                                CheckMsg = "(M03)";
                                                            }
                                                            System.out.println("=============== Check Terminal ");
                                                            condition.put("Orderid", UserBean.trim_Data(data[1]));

                                                            if (check_point)
                                                            {
                                                                if (data[1] != null && UserBean.trim_Data(data[1]).length() > 0)
                                                                {
                                                                    if (UserBean.trim_Data(data[1]).length() > 25)
                                                                    {
                                                                        warn_message += "第" + row_count + "筆特店指定單號,";
                                                                        check_point = false;
                                                                        CheckMsg = "(M14)";
                                                                    }
                                                                }
                                                                else
                                                                {
                                                                    warn_message += "第" + row_count + "筆特店指定單號,";
                                                                    check_point = false;
                                                                    CheckMsg = "(M13)";
                                                                }
                                                            }

                                                            System.out.println("=============== Check Orderid ");
                                                            condition.put("Tranmode", UserBean.trim_Data(data[2]));

                                                            if (check_point)
                                                            {
                                                                if (data[2] != null && UserBean.trim_Data(data[2]).length() > 0)
                                                                {
                                                                    try {
                                                                        if (Integer.parseInt(UserBean.trim_Data(data[2])) >= 0 &&
                                                                            Integer.parseInt(UserBean.trim_Data(data[2])) < 3)
                                                                        {
                                                                            if (Integer.parseInt(UserBean.trim_Data(data[2])) == 1)
                                                                            {
                                                                                chk_tran_mode = true;
                                                                            }
                                                                        }
                                                                        else
                                                                        {
                                                                            warn_message += "第" + row_count + "筆交易模式(" + UserBean.trim_Data(data[2]) + "),";
                                                                            check_point = false;
                                                                            CheckMsg = "(M10)";
                                                                        }
                                                                    }
                                                                    catch (Exception chk)
                                                                    {
                                                                        System.out.println(chk.getMessage());
                                                                        warn_message += "第" + row_count + "筆交易模式(" + UserBean.trim_Data(data[2]) + "),";
                                                                        check_point = false;
                                                                        CheckMsg = "(M10)";
                                                                    }
                                                                }
                                                                else
                                                                {
                                                                    warn_message += "第" + row_count + "筆交易模式,";
                                                                    check_point = false;
                                                                    CheckMsg = "(M10)";
                                                                }
                                                            }

                                                            System.out.println("=============== Check Transmode ");
                                                            condition.put("Tranamt", UserBean.trim_Data(data[3]));
                                                            if (check_point)
                                                            {
                                                                if (data[3] != null && UserBean.trim_Data(data[3]).length() > 0)
                                                                {
                                                                    try
                                                                    {
                                                                        if (Integer.parseInt(UserBean.trim_Data(data[3])) < 0)
                                                                        {
                                                                            warn_message += "第" + row_count + "筆交易金額,";
                                                                            check_point = false;
                                                                            CheckMsg = "(M06)";
                                                                        }
                                                                    }
                                                                    catch (Exception chk)
                                                                    {
                                                                        System.out.println(chk.getMessage());
                                                                        warn_message += "第" + row_count + "筆交易金額(" + UserBean.trim_Data(data[3]) + "),";
                                                                        check_point = false;
                                                                        CheckMsg = "(M06)";
                                                                    }
                                                                }
                                                                else
                                                                {
                                                                    warn_message += "第" + row_count + "筆交易金額,";
                                                                    check_point = false;
                                                                    CheckMsg = "(M06)";
                                                                }
                                                             }

                                                             System.out.println("=============== Check Amt ");
                                                             condition.put( "Pan", UserBean.trim_Data(data[4]));
                                                             if (check_point)
                                                             {
                                                                if (data[4] != null && UserBean.trim_Data(data[4]).length() > 0)
                                                                {
                                                                    CheckPan chkpan = new CheckPan();
                                                                    
                                                                    // 檔案授權作業 加入擋掉銀聯卡進行檔案授權的判斷  修改  by Jimmy Kang 20150806 -- 修改開始 --
                                                                    //System.out.println(UserBean.trim_Data(data[4]).substring(0, 2));
                                                                    if (!UserBean.trim_Data(data[4]).substring(0, 2).equalsIgnoreCase("62"))   // 擋掉卡號前兩碼為62的資料
                                                                    {
                                                                    // 檔案授權作業 加入擋掉銀聯卡進行檔案授權的判斷  修改  by Jimmy Kang 20150806 -- 修改結束 --
                                                                    	try
                                                                        {
                                                                            int checkflag = chkpan.check(UserBean.trim_Data(data[4]));
                                                                            if (checkflag < 0)
                                                                            {
                                                                                warn_message += "第" + row_count + "筆卡號,";
                                                                                check_point = false;
                                                                                CheckMsg = "(M07)";
                                                                            }
                                                                        }
                                                                        catch (Exception e)
                                                                        {
                                                                            warn_message += "第" + row_count + "筆卡號,";
                                                                            check_point = false;
                                                                            CheckMsg = "(M07)";
                                                                        }
                                                                    // 檔案授權作業 加入擋掉銀聯卡進行檔案授權的判斷  修改  by Jimmy Kang 20150806 -- 修改開始 --
                                                                    }
                                                                    else
                                                                    {
                                                                    	warn_message += "第" + row_count + "筆卡號,";
                                                                        check_point = false;
                                                                        CheckMsg = "(M15)";
                                                                    }
                                                                    // 檔案授權作業 加入擋掉銀聯卡進行檔案授權的判斷  修改  by Jimmy Kang 20150806 -- 修改結束 --
                                                                    
                                                                }
                                                                else
                                                                {
                                                                    warn_message += "第" + row_count + "筆卡號,";
                                                                    check_point = false;
                                                                    CheckMsg = "(M07)";
                                                                }
                                                            }

                                                            System.out.println("=============== Check Pan ");
                                                            condition.put("Effectdate",UserBean.trim_Data(data[5]));

                                                            if (check_point)
                                                            {
                                                                if (data[5] != null && UserBean.trim_Data(data[5]).length() == 4)
                                                                {
                                                                    try
                                                                    {
                                                                        Integer.parseInt(UserBean.trim_Data(data[5]));
                                                                        Date nowdate = new Date();
                                                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                                                                        String now_year = sdf.format(nowdate);
                                                                        sdf = new SimpleDateFormat("MM");
                                                                        String now_month = sdf.format(nowdate);
                                                                        String temp_year = "20" +UserBean.trim_Data(data[5]).substring(2);
                                                                        String temp_month = UserBean.trim_Data(data[5]).substring(0, 2);

                                                                        if (Integer.parseInt(temp_month) > 0 && Integer.parseInt(temp_month) < 13)
                                                                        {
                                                                            if (Integer.parseInt(temp_year) >= Integer.parseInt(now_year))
                                                                            {
                                                                                if (Integer.parseInt(temp_year) == Integer.parseInt(now_year))
                                                                                {
                                                                                    if (Integer.parseInt(temp_month) < Integer.parseInt(now_month))
                                                                                    {
                                                                                        warn_message += "第" + row_count + "筆信用卡有效月年(" + UserBean.trim_Data(data[5]) + "),";
                                                                                        check_point = false;
                                                                                        CheckMsg = "(M08)";
                                                                                    }
                                                                                }
                                                                            }
                                                                            else
                                                                            {
                                                                                warn_message += "第" + row_count + "筆信用卡有效月年(" + UserBean.trim_Data(data[5]) + "),";
                                                                                check_point = false;
                                                                                CheckMsg = "(M08)";
                                                                            }
                                                                        }
                                                                        else
                                                                        {
                                                                            warn_message += "第" + row_count + "筆信用卡有效月年(" + UserBean.trim_Data(data[5]) + "),";
                                                                            check_point = false;
                                                                            CheckMsg = "(M08)";
                                                                        }
                                                                    }
                                                                    catch (Exception chk)
                                                                    {
                                                                        System.out.println(chk.getMessage());
                                                                        warn_message += "第" + row_count + "筆信用卡有效月年(" + data[5] + "),";
                                                                        check_point = false;
                                                                        CheckMsg = "(M08)";
                                                                    }
                                                                }
                                                                else
                                                                {
                                                                    warn_message += "第" + row_count + "筆信用卡有效月年,";
                                                                    check_point = false;
                                                                    CheckMsg = "(M08)";
                                                                }
                                                            }
                                                            System.out.println("=============== Check Date1 ");
                                                            condition.put("CVV2/CVC2",data[6] != null ? UserBean.trim_Data(data[6]) :"");
                                                            if (check_point) {
                                                                String Cvv2 = data[6] != null ? UserBean.trim_Data(data[6]) :"";
                                                                String  FORCE_CVV2 = hashMerchant.get("FORCE_CVV2").toString();
                                                                if (FORCE_CVV2 == null)
                                                                    FORCE_CVV2 = "N";

                                                                if (FORCE_CVV2.equalsIgnoreCase("Y"))
                                                                {
                                                                    // 強制輸入CVV2
                                                                    if (data[6] == null)
                                                                        data[6] = "";

                                                                    if (Cvv2.length() ==0)
                                                                    {
                                                                        warn_message += "第" + row_count + "筆CVV2/CVC2,";
                                                                        check_point = false;
                                                                        CheckMsg = "(M11)";
                                                                    }
                                                                }

                                                                if (Cvv2.length()>0)
                                                                {
                                                                    if (Cvv2.length()!=3)
                                                                    {
                                                                        warn_message += "第" + row_count + "筆CVV2/CVC2,";
                                                                        check_point = false;
                                                                        CheckMsg = "(M11)";
                                                                    }
                                                                }
                                                            }

                                                            System.out.println("=============== Check CVV2 ");
                                                            condition.put("Time", data[7] != null ? (data[7]) : "");
                                                            if (check_point)
                                                            {
                                                                if (chk_tran_mode)
                                                                {
                                                                    // 分期交易
                                                                    if (data[7] != null && UserBean.trim_Data(data[7]).length() > 0)
                                                                    {
                                                                        //非必要
                                                                        try
                                                                        {
                                                                            if (Integer.parseInt(UserBean.trim_Data(data[7])) <= 0)
                                                                            {
                                                                                warn_message += "第" + row_count + "筆分期期數,";
                                                                                check_point = false;
                                                                                CheckMsg = "(M09)";
                                                                            }
                                                                        }
                                                                        catch (Exception chk)
                                                                        {
                                                                            System.out.println(chk.getMessage());
                                                                            warn_message += "第" + row_count + "筆分期期數(" + UserBean.trim_Data(data[7]) + "),";
                                                                            check_point = false;
                                                                            CheckMsg = "(M09)";
                                                                        }
                                                                    }
                                                                    else
                                                                    {
                                                                        warn_message += "第" + row_count + "筆分期期數,";
                                                                        check_point = false;
                                                                        CheckMsg = "(M09)";
                                                                    }
                                                                }
                                                            }

                                                            System.out.println("=============== Check Date2 ");
                                                            condition.put("Email", data[8] != null ? UserBean.trim_Data(data[8]) : "");
                                                            if (check_point)
                                                            {
                                                                if (data[8] != null && UserBean.trim_Data(data[8]).length() > 0)
                                                                {
                                                                    //非必要
                                                                    if (UserBean.trim_Data(data[8]).indexOf("@") < 0)
                                                                    {
                                                                        warn_message += "第" + row_count + "筆持卡人email,";
                                                                        check_point = false;
                                                                        CheckMsg = "(M12)";
                                                                    }

                                                                    warn_message += "第" + row_count + "筆持卡人email,";
                                                                }
                                                            }

                                                            System.out.println("=============== Check Email ");
                                                            condition.put("Id", data[9] != null ? UserBean.trim_Data(data[9]) : ""); // 筆身份證字號
                                                            condition.put("Check", String.valueOf(check_point));
                                                            condition.put("CheckMsg",CheckMsg); // 檢核說明 2008.07.23
                                                            input_data.add(condition);
                                                        }
                                                        else
                                                        {
                                                            warn_message += "第" + row_count + "筆為空值,";
                                                            boolCheckLenFlag = false;
                                                            break;
                                                        }

                                                        System.out.println("=============== Check Terminal ");
                                                        System.out.println("---------Check condition="+condition);
                                                    }

                                                    System.out.println("Warn=" + warn_message);
                                                }
                                                else
                                                {
                                                    System.out.println("Filenam=" + FileName);
                                                }

                                                System.out.println("---------Check boolCheckLenFlag="+boolCheckLenFlag);
                                                if (boolCheckLenFlag)
                                                {
                                                    if (warn_message.length() > 0)
                                                        warn_message = warn_message.substring(0,warn_message.length() - 1) + "欄位檢核失敗!!";

                                                    Message = "檔案上傳成功";
                                                    Forward = "./Merchant_BatchAuthShow_List.jsp";
                                                    session.setAttribute("Message", warn_message);
                                                    session.setAttribute("Content", input_data); //文字檔內容
                                                    session.setAttribute("Source", content);
                                                }
                                                else
                                                {
                                                    Message = "檔案上傳資料長度有誤";
                                                    session.setAttribute("Message", Message);
                                                    Forward = "./Merchant_Response.jsp";
                                                    LogMemo = Message;
                                                    LogUserName = hashMerUser.get("USER_ID").toString() + "(" + hashMerUser.get("USER_NAME").toString() + ")";
                                                    LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
                                                    log_user.debug(LogData);
                                                }

                                                String tmpsn = this.read(hashSys.get("MER_BATCHAUTH_SN_PATH") != null ?
                                                        hashSys.get("MER_BATCHAUTH_SN_PATH").toString() : "/UIBATCH/SN/", "sn.txt", request) == null ?
                                                        "0" : this.read(hashSys.get("MER_BATCHAUTH_SN_PATH") != null ?
                                                        hashSys.get("MER_BATCHAUTH_SN_PATH").toString() : "/UIBATCH/SN/", "sn.txt", request).trim();

                                                System.out.println("---------Check tmpsn="+tmpsn);
                                                if (tmpsn.length() == 0)
                                                    tmpsn = "0";

                                                System.out.println("------tmpsn="+tmpsn);
                                                String sn = this.checkLength(String.valueOf(Integer.parseInt(tmpsn)), "3", "Number");
                                                System.out.println("------sn="+sn);
                                                // 更新批次序號 2008.07.24 shirley
                                                String sn_path = hashSys.get("MER_BATCHAUTH_SN_PATH") != null ?
                                                        hashSys.get("MER_BATCHAUTH_SN_PATH").toString() : "/UIBATCH/SN/";
                                                int Sn_Number = (Integer.parseInt(sn) + 1) % 1000;   //  shirley 2008.07.29
                                                this.save(String.valueOf(Sn_Number), "sn.txt", sn_path, true); // 批次序號
                                                if (session.getAttribute("Sn") != null)
                                                {
                                                    session.removeAttribute("Sn");
                                                }

                                                session.setAttribute("Sn", sn);
                                                if (session.getAttribute("Filename") != null)
                                                {
                                                    session.removeAttribute("Filename");
                                                }

                                                session.setAttribute("Filename",FileName);
                                                String file_content = this.getHeader(LogMerchantID, "8" + sn);
                                                int tran_count = 0;
                                                int tran_amt = 0;
                                                int auth_count = 0;
                                                int auth_amt = 0;
                                                int refund_count = 0;
                                                int refund_amt = 0;
                                                //20151021 Jason 檔案表尾增加授權退貨金額筆數
                                                for (int i = 0; input_data != null && i < input_data.size(); i++)
                                                {
                                                    Hashtable data = (Hashtable) input_data.get(i);
                                                    String check_point = String.valueOf(data.get("Check"));
                                                    if (check_point.equalsIgnoreCase("true"))
                                                    {
                                                        tran_count++;
                                                        tran_amt += Integer.parseInt(data.get("Tranamt").toString());
                                                        file_content += this.getRowData(data);
                                                        if("00".equals(null == data.get("Tranmode")?"": data.get("Tranmode").toString())){
                                                        	auth_count++;
                                                        	auth_amt += Integer.parseInt(data.get("Tranamt").toString());
                                                        }
                                                        if("01".equals(null == data.get("Tranmode")?"": data.get("Tranmode").toString())){
                                                        	refund_count++;
                                                        	refund_amt += Integer.parseInt(data.get("Tranamt").toString());
                                                        }                                                        	
                                                    }
                                                }

                                                file_content += this.getTrailer(String.valueOf(tran_count), String.valueOf(tran_amt), String.valueOf(auth_count), String.valueOf(auth_amt), String.valueOf(refund_count),  String.valueOf(refund_amt));
                                                String dat_swap_path = hashSys.get("MER_BATCHAUTHTEMP_DAT_PATH") != null ?
                                                            hashSys.get("MER_BATCHAUTHTEMP_DAT_PATH").toString() : "/UIBATCH/Swap";
                                                Date nowdate = new Date();
                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                                                String today = sdf.format(nowdate);
                                                filename = "BA" + MerchantID + "_" + today + "8" + sn;
                                                this.save(file_content, filename + ".dat", dat_swap_path);
                                                bufferread.close();
                                            }
                                        }
                                    }
                                }
                                catch (Exception ex)
                                {
                                    ex.printStackTrace();
                                    this.save("", "test.txt", saveDirectory);
                                    Forward = "./Merchant_Response.jsp";
                                    session.setAttribute("Message", "檔案大小上限為1MB");
                                    request.getRequestDispatcher(Forward).forward(request, response);
                                }
                            }

                            if (Action.equalsIgnoreCase("Confirm"))
                            {
                                String path = hashSys.get(
                                        "MER_BATCHAUTH_CSV_PATH") != null ? hashSys.get("MER_BATCHAUTH_CSV_PATH").toString() : "/UIBATCH/BatchFileBK/";
                                String content = String.valueOf(session.getAttribute("Source"));
                                String sn = String.valueOf(session.getAttribute("Sn"));
                                String filename = String.valueOf(session.getAttribute("Filename"));

                                if (this.save(content, filename, path))
                                {
//                                    String sn_path = hashSys.get("MER_BATCHAUTH_SN_PATH") != null ? hashSys.get("MER_BATCHAUTH_SN_PATH").toString() : "/UIBATCH/SN/";
//                                    this.save(String.valueOf(Integer.parseInt(sn) + 1), "sn.txt", sn_path, true);  // 批次序號
                                    String d_path = hashSys.get("MER_BATCHAUTH_DAT_PATH") != null ? hashSys.get("MER_BATCHAUTH_DAT_PATH").toString() : "/BATCH/BatchFiles/BA";
                                    String s_path = hashSys.get("MER_BATCHAUTHTEMP_DAT_PATH") != null ? hashSys.get("MER_BATCHAUTHTEMP_DAT_PATH").toString() : "/UIBATCH/Swap";
                                    Date nowdate = new Date();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                                    String today = sdf.format(nowdate);
                                    String MerchantID = hashMerUser.get("MERCHANT_ID").toString();
                                    filename = "BA" + MerchantID + "_" + today +"8" + sn;
                                    String zip_path = hashSys.get("MER_BATCHAUTH_DAT_PATH") != null ? hashSys.get("MER_BATCHAUTH_DAT_PATH").toString() : "/BATCH/BatchFiles/BA";
                                    String datapath = s_path + "/" + filename + ".dat";
                                    String zippath = zip_path + "/" + filename + ".zip";
                                    System.out.println("-------datapath="+datapath);
                                    System.out.println("-------zippath="+zippath);
                                    File srcFile = new File(datapath);
                                    File targetZip = new File(zippath);
                                    System.out.println("srcFile is exist?!"+srcFile.exists());
                                    System.out.println("Make batchAuth zip file--START--");
                                    this.makeZip(srcFile, targetZip);
                                    System.out.println("Make batchAuth zip file--END--");
                                    System.out.println("START PUT FTP FILE --START--");
                                    /**2018.01.22 change JCE to jasypt**/
                                    //String encodeAESstr = null == hashSys.get("FTP_AES_STR")?"":hashSys.get("FTP_AES_STR").toString();
                                    //System.out.println("AES STR="+encodeAESstr);
                                    //String decodeAESstr = Base64Decoder.decode(encodeAESstr);                                                                      
                                    SFTPutil sFTPutil = new SFTPutil();
                                    //sFTPutil.setAESStr(decodeAESstr);
                                    //System.out.println("AESStr:"+sFTPutil.getAESStr());
                                    //KEY = sFTPutil.getKEY();
                                    //System.out.println("KEY:"+KEY);
                                    //IV = sFTPutil.getIV();
                                    //System.out.println("IV:"+IV);
                                    String encodeftpPassWord = (null == ftphostInfo.get("LOGIN_PWD"))?"":(String)ftphostInfo.get("LOGIN_PWD");
                                   // System.out.println("ENCODE FTP PASSWORD:"+encodeftpPassWord);
                                    String decodeftpPassWord = Util.decrypt(encodeftpPassWord);
                                    System.out.println("DECODE FTP PASSWORD FINISH"+Util.lpad(decodeftpPassWord, "*", decodeftpPassWord.length()));
                                    FtpBean ftpBean = new FtpBean();
                                    ftpBean.setLocalFile(filename+".zip");
                                    ftpBean.setLocalPath(zip_path+("/".equals(zip_path.substring(zip_path.length()-1))?"":"/"));
                                    ftpBean.setsFTPIp((String)ftphostInfo.get("EXTERNAL_SERVERIP"));
                                    ftpBean.setsFTPPort((String)ftphostInfo.get("EXTERNAL_PORT"));
                                    ftpBean.setsFTPUserName((String)ftphostInfo.get("LOGIN_ID"));
                                    ftpBean.setsFTPUserPw(decodeftpPassWord.trim());
                                    ftpBean.setsFTPRemoteFile(filename+".zip");
                                    ftpBean.setsFTPRemotePath(zip_path);
                                    ftpBean = sFTPutil.putSFTPfile(ftpBean);
                                    if("9999".equals(ftpBean.getRespcode())){
                                     System.out.println("FTP:"+ftpBean.getRespmsg());
                                     Message = "檔案上傳成功";
                                    }
                                    else{
                                     System.out.println("FTP:"+ftpBean.getRespmsg());
                                     Message = "檔案上傳失敗";
                                    }                                     
//                                    boolean result = this.moveFile(filename +".dat", s_path, d_path);
//                                    if (result) {                                    
//                                    } else {
//                                        Message = "檔案上傳處理失敗";
//                                    }
                                }
                                else
                                {
                                    Message = "檔名已存在!!";
                                    Forward = "./Merchant_Response.jsp";
                                    session.setAttribute("Message", Message);
                                }

                                Forward = "./Merchant_Response.jsp";
                                session.setAttribute("Message", Message);
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

            System.out.println("---Message=" + Message);
            if (Message.length() > 0)
            {
                LogMemo = Message;
//                LogUserName = hashMerUser.get("USER_NAME").toString();
                LogUserName = hashMerUser.get("USER_ID").toString() + "(" + hashMerUser.get("USER_NAME").toString() + ")";
                LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "失敗", LogMemo);
                log_user.debug(LogData);
            }

            System.out.println("---ForwardFlag=" + ForwardFlag);
            if (ForwardFlag)
            {
                System.out.println("Forward=" + Forward);
                request.getRequestDispatcher(Forward).forward(request, response);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log_systeminfo.debug("--MerchantAuthBatchRefundCtl--" + e.toString());
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

    public boolean save(String content, String filename, String path)
    {
        return this.save(content, filename, path, false);
    }

    public boolean save(String content, String filename, String path, boolean overwrite)
    {
        //write transaction file
        String msg_file_name = filename;
        File dir = new File(path);
        boolean result = false;
        System.out.println("Directory is save = " + dir.isDirectory());
        String filelist[] = dir.list();
        boolean checkfile = true;
        for (int a = 0; filelist != null && a < filelist.length; a++)
        {
            if (filelist[a].equalsIgnoreCase(filename))
            {
                checkfile = false;
            }
        }

        System.out.println("after  for (int a = 0; filelist != null && a < filelist.length; a++) { ");
        if (checkfile || overwrite)
        {
        	System.out.println(!dir.isDirectory());
            if (!dir.isDirectory())
            {
            System.out.println("dir.mkdirs-S");
                dir.mkdirs();
            System.out.println("dir.mkdirs-E");
            }
            System.out.println("dir:"+dir+" msg_file_name:"+msg_file_name);
            File file = new File(dir, msg_file_name);
            try
            {
            	System.out.println(" file.createNewFile()");
                file.createNewFile();
                System.out.println("FileOutputStream out = new FileOutputStream(file)");
                FileOutputStream out = new FileOutputStream(file);
                System.out.println("String newContent = content.replaceFirst(Big5, UTF-8)");                
                String newContent = content.replaceFirst("Big5", "UTF-8"); //content.replaceFirst("Big5", "UTF-8");
                System.out.println("out.write(newContent.getBytes(UTF-8))");
                out.write(newContent.getBytes("UTF-8"));
                out.flush();
                out.close();

                System.out.println("Save message to " + path + " " + file.getName());
                result = true;
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                return false;
            }
        }

        System.out.println("result = " + result);
        return result;
    }

    /**
     * execute
     *
     * @param content String
     * @return String
     */
    public String execute(String content, ArrayList template)
    {
        content = content.replaceAll("\r\n", "\r");
        String temp[] = content.split("\r");
        String result = "";

        for (int i = 0; i < temp.length; i++)
        {
            if (temp[i].trim().length() > 0)
            {
                String temp1[] = temp[i].split(",");
                for (int a = 0; a < temp1.length; a++)
                {
//                    for(int b=1;template!=null&& b<template.size();b++){
                    Hashtable template_detail = (Hashtable) template.get(a + 1);
                    result = result +this.checkLength(temp1[a], String.valueOf(template_detail.get("Length")),
                                              String.valueOf(template_detail.get("Type")));
//                    }

                }
                if (i != temp.length - 1)
                {
                    result = result + System.getProperty("line.separator");
                }

            }
        }

        return result;
    }

    public ArrayList readTemp(String parent, String filename, HttpServletRequest request)
    {
        //read transaction file
//        System.out.println("read Start time{"+new Date()+"}");
        String data = "";
        ArrayList result = new ArrayList();
        FileInputStream bis;
        File file1 = new File(parent, "/" + filename);
//        System.out.println(file1.getAbsolutePath());

        try
        {
            if (file1.exists())
            {
                bis = new FileInputStream(file1);
                byte inbt[] = new byte[(int) file1.length()];
                bis.read(inbt);
                bis.close();
                data = new String(inbt, "big5");
//                System.out.println("File read out ====>" + data +"<========end");

            }
            else
            {
//                System.out.println("File can't read ====>" + parent+"/"+ parent+"/"+filename +"<========end");
                file1 = new File(request.getSession().getServletContext().getRealPath("") + filename);
                if (file1.exists())
                {
//                     System.out.println("File read ====>"+filename +"<========end");
                    bis = new FileInputStream(file1);
                    byte inbt[] = new byte[(int) file1.length()];
                    bis.read(inbt);
                    bis.close();
                    data = new String(inbt, "big5");
//                    System.out.println("File read out ====>" + data +"<========end");

                }
                else
                {
                    System.out.println("File can't read ====>" + file1.getAbsolutePath() + "<========end");
                }
            }
//            return data;
        }
        catch (Throwable e)
        {
            data = null;
            e.printStackTrace();
        }

        String config_str = data;
        config_str = config_str.replaceAll("\r", ""); //將設定內的換行符號替代掉
        config_str = config_str.replaceAll("\n", "");
        String[] config_tag = config_str.split(";");
        for (int i = 0; i < config_tag.length; i++)
        {
            String[] config_keyvalue = config_tag[i].split("=", 2);
            Hashtable content = new Hashtable();
            int a = 0;
            if (config_keyvalue[0].indexOf("#") != 0 && config_keyvalue[0].trim().length() > 0)
            {
//                System.out.println(config_keyvalue[0]);
                if (config_keyvalue.length == 2)
                {
                    if (config_keyvalue[0].indexOf("cul") == 0)
                    {
                        if (config_keyvalue[1].split(",").length == 3)
                        {
                            content.put("Type", config_keyvalue[1].split(",")[0]);
                            content.put("Length", config_keyvalue[1].split(",")[1]);
                        } else if (config_keyvalue[1].split(",").length == 4)
                        {
                            content.put("Type", config_keyvalue[1].split(",")[0]);
                            content.put("Length",
                                        config_keyvalue[1].split(",")[1]);
                            content.put("Desc", config_keyvalue[1].split(",")[2]);
                        }
                        else
                        {
                            System.out.println("Error Template!!");
                        }

                        a = Integer.parseInt(config_keyvalue[0].replaceAll(" ", "").replaceAll("cul", ""));
                    }
                    else if (config_keyvalue[0].indexOf("top") == 0 || config_keyvalue[0].indexOf("end") == 0)
                    {
                        if (result.get(0) == null)
                        {
                            content.put(config_keyvalue[0].replaceAll(" ", ""), config_keyvalue[1]);

                        }
                        else
                        {
                            content = (Hashtable) result.get(0);
                            content.put(config_keyvalue[0].replaceAll(" ", ""), config_keyvalue[1]);
                        }

                        a = 0;
                    }

                }
                if (result == null || result.size() == 0)
                {
                    result.add(0, "");
                    result.add(a, content);
                }
                else
                {
                    result.add(a, content);
                }

            }
        }

//        System.out.println("Config <"+result+">");

        return result;
    }

    public String read(String parent, String filename, HttpServletRequest request)
    { //read transaction file
//        System.out.println("read Start time{"+new Date()+"}");
        String data = "";
        FileInputStream bis;
        File file1 = new File(parent + "/" + filename);
        System.out.println("file path=" + parent + "/" + filename);

        try
        {
            if (file1.exists())
            {
                bis = new FileInputStream(file1);
                byte inbt[] = new byte[(int) file1.length()];
                bis.read(inbt);
                bis.close();
                data = new String(inbt, "big5");
//                System.out.println("File read out ====>" + data +"<========end");

            }
            else
            {
//                System.out.println("File can't read ====>" + parent+"/"+ parent+"/"+filename +"<========end");
                file1 = new File(request.getSession().getServletContext().getRealPath(parent) + "/" + filename);
                if (file1.exists())
                {
//                     System.out.println("File read ====>"+filename +"<========end");
                    bis = new FileInputStream(file1);
                    byte inbt[] = new byte[(int) file1.length()];
                    bis.read(inbt);
                    bis.close();
                    data = new String(inbt, "big5");
//                    System.out.println("File read out ====>" + data +"<========end");

                }
                else
                {
                    System.out.println("File can't read ====>" + file1.getAbsolutePath() + "<========end");
                }

            }
//            return data;
        }
        catch (Throwable e)
        {
            data = null;
            e.printStackTrace();
        }

        return data;
    }

    /**
     * checkLength
     *
     * @param indata String
     * @param len String
     * @param type String
     * @return String
     */
    private String checkLength(String indata, String len, String type)
    {
        String retstr = indata.trim();
        int    TrailerLength;
        try
        {
            int leng = len.indexOf(".") >= 0 ? Integer.parseInt(len.substring(0, len.indexOf("."))) : Integer.parseInt(len);

            if (type.equalsIgnoreCase("String") || type.equalsIgnoreCase("Number") ||
                type.equalsIgnoreCase("Amount") || type.equalsIgnoreCase("CardId"))
            {
                if (retstr.length() != leng)
                {
                    if (retstr.length() > leng)
                    {
                        retstr = retstr.substring(0, leng);
                    }
                    else
                    {
                        if (type.equals("String"))
                        {
                            TrailerLength = leng - retstr.length();
                            for (int i = 0; i < TrailerLength; i++)
                            {
                                retstr = retstr + " ";
                            }
                        }
                        else if (type.equals("Number"))
                        {
                            TrailerLength = leng - retstr.length();
                            for (int i = 0; i < TrailerLength; i++)
                            {
                                retstr = "0" + retstr;
                            }
                        }
                        else if (type.equalsIgnoreCase("CardId"))
                        {
                            //UserBean UserBean = new UserBean();
                            retstr = UserBean.get_CardStar(retstr.trim(), 9, 2);
                            TrailerLength = leng - retstr.length();
                            for (int i = 0; i < TrailerLength; i++)
                            {
                                retstr = retstr + " ";
                            }
                        }
                        else if (type.equals("Amount"))
                        {
                            int MaxLength = Integer.parseInt(len.substring(0, len.indexOf(".")));
                            int DecimalLength = Integer.parseInt(len.substring(len.indexOf(".") + 1));
                            String ImportData = retstr;

                            if (ImportData.trim().length() > 0)
                            {
                                if (Double.parseDouble(ImportData) < 0)
                                {
                                    MaxLength = MaxLength - 1;
                                }

                                NumberFormat nf = NumberFormat.getInstance();
                                nf.setMaximumFractionDigits(DecimalLength);
                                nf.setMinimumFractionDigits(DecimalLength);

                                DecimalFormat df = new DecimalFormat("0");
                                df.setMaximumIntegerDigits(MaxLength - DecimalLength);
                                df.setMinimumIntegerDigits(MaxLength - DecimalLength);

                                ImportData = nf.format(Double.parseDouble(ImportData));
//                                  System.out.println("ImportData=" + ImportData);
                                ImportData = ImportData.replaceAll(",", "");
                                int DecimalPoint = ImportData.indexOf(".");
                                String Data = ImportData.substring(0, DecimalPoint);
//                                  System.out.println("Data=" + Data);
                                String DecimalData = ImportData.substring(DecimalPoint + 1, ImportData.length());
//                                  System.out.println("DecimalData=" +DecimalData);
                                ImportData = df.format(Double.parseDouble(Data));
                                String ExportData = ImportData + "." + DecimalData;
                                ExportData = ExportData.substring(1);
//                                  System.out.println("ExportData=" + ExportData);
                                retstr = ExportData;
                            }
                            else
                            {
                                retstr = checkLength(ImportData, String.valueOf(MaxLength), "String");
                            }
                        }
                    }
                }

            }
        }
        catch (Exception e)
        {
            e.toString();
        }
        return retstr;
    }

    /**
     * getHeader
     *
     * @param merchantid String
     * @param sn String
     * @return String
     */
    public String getHeader(String merchantid, String sn)
    {
        String header = "";
        String date = "";
        String time = "";
        Date nowdate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        date = sdf.format(nowdate);
        sdf = new SimpleDateFormat("HHmmss");
        time = sdf.format(nowdate);
        header += "H";
        header += "BA";
        header += this.checkLength(merchantid, "16", "String");
        header += date;
        header += time;
        header += this.checkLength(sn, "4", "String"); //sn
        header += this.checkLength("", "263", "String"); //FILLER
        header += "\r\n";

        return header;
    }

    /**
     * getRowData
     *
     * @param content Hashtable
     * @return String
     */
    public String getRowData(Hashtable content)
    {
        String rowdata = "";

        rowdata += "D";
        rowdata += this.checkLength(content.get("Terminal").toString(), "16", "String"); //端末機代號16
        rowdata += this.checkLength(content.get("Orderid").toString(), "25", "String"); //＊特店指定單號25
        rowdata += "00";
        rowdata += content.get("Tranmode").toString(); //交易模式
        rowdata += this.checkLength(content.get("Tranamt").toString(), "11", "Number"); //＊交易金額11
        rowdata += this.checkLength(content.get("Pan").toString(), "19", "String"); //＊卡號19
        rowdata += this.checkLength(content.get("Effectdate").toString(), "4", "String"); //＊信用卡有效月年4
        rowdata += this.checkLength(content.get("CVV2/CVC2").toString(), "3", "String"); //CVV2/CVC2 3
        rowdata += this.checkLength(content.get("Time").toString(), "2", "Number"); //分期期數2
        rowdata += this.checkLength(content.get("Email").toString(), "40", "String"); //持卡人email40
        rowdata += this.checkLength(content.get("Id").toString(), "10", "String"); //身份證字號10
        rowdata += this.checkLength("", "166", "String"); //
        rowdata += "\r\n";

        return rowdata;
    }

    /**
     * getTrailer
     *
     * @param count String
     * @param amt String
     * @return String
     */
    public String getTrailer(String count, String amt, String authcnt, String authamt, String refundcnt, String refundamt)
    {
        String trailer = "";

        trailer += "T";
        trailer += this.checkLength(count, "8", "Number");
        trailer += this.checkLength(authcnt, "8", "Number");
        trailer += this.checkLength(refundcnt, "8", "Number");
        trailer += this.checkLength(amt, "13", "Number");
        trailer += this.checkLength(authamt, "13", "Number");
        trailer += this.checkLength(refundamt, "13", "Number");
        trailer += this.checkLength("0", "8", "Number");
        trailer += this.checkLength("0", "8", "Number");
        trailer += this.checkLength("0", "8", "Number");
        trailer += this.checkLength("0", "8", "Number");
        trailer += this.checkLength("0", "13", "Number");
        trailer += this.checkLength("0", "13", "Number");
        trailer += this.checkLength("0", "13", "Number");
        trailer += this.checkLength("0", "13", "Number");
        trailer += this.checkLength("0", "8", "Number");
        trailer += this.checkLength("0", "13", "Number");
        trailer += this.checkLength("", "131", "String");
        trailer += "\r\n";

        return trailer;
    }

    /**
     * moveFile
     *
     * @param sourcepath String
     * @param destinationpath String
     * @return boolean
     */
    public boolean moveFile(String filename, String sourcepath, String destinationpath)
    {
        boolean result = false;
        String data = "";
        FileInputStream bis;
        File file1 = new File(sourcepath, "/" + filename);
//        System.out.println(file1.getAbsolutePath());

        try
        {
            if (file1.exists())
            {
                bis = new FileInputStream(file1);
                byte inbt[] = new byte[(int) file1.length()];
                bis.read(inbt);
                bis.close();
                data = new String(inbt, "big5");
                System.out.println("File read out ====>" + data + "<========end");
                file1.delete();
            }
            else
            {
//                System.out.println("File can't read ====>" + parent+"/"+ parent+"/"+filename +"<========end");
                file1 = new File(destinationpath + filename);
                if (file1.exists())
                {
//                     System.out.println("File read ====>"+filename +"<========end");
                    bis = new FileInputStream(file1);
                    byte inbt[] = new byte[(int) file1.length()];
                    bis.read(inbt);
                    bis.close();
                    data = new String(inbt, "big5");
//                    System.out.println("File read out ====>" + data +"<========end");
                }
                else
                {
                    System.out.println("File can't read ====>" + file1.getAbsolutePath() + "<========end");
                }

            }

            result = this.save(data, filename, destinationpath);

//            return data;
        }
        catch (Throwable e)
        {
            result = false;
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 建立 zip 檔
     * @param srcFile 想要壓縮的資料夾
     * @param targetZip 壓縮zip檔
     * @throws IOException
     * @throws FileNotFoundException
     */
    public void makeZip(File srcFile, File targetZip) throws IOException, FileNotFoundException
    {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(targetZip));
        String dir = "";
        recurseFiles(srcFile, zos, dir);

        zos.close();
    }

    /**
     * 壓縮 主程式
     * @param file
     * @param zos
     * @throws IOException
     * @throws FileNotFoundException
     */
    private void recurseFiles(File file, ZipOutputStream zos, String dir) throws IOException, FileNotFoundException
    {
        //目錄
        if (file.isDirectory())
        {
            System.out.println("找到資料夾:" + file.getName());
            dir += file.getName() + File.separator;
            String[] fileNames = file.list();
            if (fileNames != null)
            {
                for (int i = 0; i < fileNames.length; i++)
                {
                    recurseFiles(new File(file, fileNames[i]), zos, dir);
                }
            }
        }
        else
        {
            //Otherwise, a file so add it as an entry to the Zip file.
            System.out.println("壓縮檔案:" + file.getName());

            byte[] buf = new byte[1024];
            int len;

            //Create a new Zip entry with the file's name.
            dir = dir.substring(dir.indexOf(File.separator) + 1);
            ZipEntry zipEntry = new ZipEntry(dir + file.getName());
            //Create a buffered input stream out of the file
            //we're trying to add into the Zip archive.
            FileInputStream fin = new FileInputStream(file);
            BufferedInputStream in = new BufferedInputStream(fin);
            zos.putNextEntry(zipEntry);
            //Read bytes from the file and write into the Zip archive.
            while ((len = in.read(buf)) >= 0)
            {
                zos.write(buf, 0, len);
            }

            //Close the input stream.
            in.close();
            //Close this entry in the Zip stream.
            zos.closeEntry();
        }
    }
}
