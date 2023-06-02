package com.cybersoft.merchant.bean;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import java.util.Hashtable;
import java.security.MessageDigest;
import com.cybersoft.bean.UserBean;
import com.cybersoft.bean.emDataType;
import com.cybersoft.bean.LogUtils;
import java.text.DecimalFormat;
import java.text.ParseException;

public class MerchantDownloadBean
{
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    public MerchantDownloadBean()
    {
    }

//    /**
//     * 取得下載文件資料
//     * @param String MerchantID      特店代號
//     * @return ArrayList             列表資料
//     */
//    public ArrayList get_DocDownload_List(String MerchantID)
//    {
//        ArrayList arrayData = new ArrayList(); // 顯示訂單交易資料
//        DataBaseBean SysBean = new DataBaseBean();
//
//        if (MerchantID.length() > 0 )
//        {
//            String Sql = " SELECT * FROM FILEDOWNLOAD WHERE MERCHANT_ID = '"+MerchantID+"' ORDER BY SEQ ";
//            // System.out.println("Sql=" + Sql);
//            try
//            {
//                arrayData = (ArrayList) SysBean.executeSQL(Sql, "select");
//            }
//            catch (Exception ex)
//            {
//                System.out.println(ex.getMessage());
//                log_systeminfo.debug(ex.toString());
//            }
//
//            if (arrayData == null)
//            {
//                arrayData = new ArrayList();
//            }
//        }
//
//        return arrayData;
//    }

    /* Override get_DocDownload_List with DataBaseBean parameter */
    public ArrayList get_DocDownload_List(DataBaseBean2 sysBean, String MerchantID)
    {
        ArrayList arrayData = new ArrayList(); // 顯示訂單交易資料
        // DataBaseBean SysBean = new DataBaseBean();

        if (MerchantID.length() > 0 )
        {
        	StringBuffer sSQLSB = new StringBuffer();
            sysBean.ClearSQLParam();
    		sSQLSB.append(" SELECT * FROM FILEDOWNLOAD WHERE MERCHANT_ID = ? ORDER BY SEQ ");
    		sysBean.AddSQLParam(emDataType.STR, MerchantID);
    		
            // System.out.println("Sql=" + Sql);
            try
            {
            	/** 2023/05/17 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-078 */
                arrayData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
            }
            catch (Exception ex)
            {
                System.out.println(ex.getMessage());
                log_systeminfo.debug(ex.toString());
            }

            if (arrayData == null)
            {
                arrayData = new ArrayList();
            }
        }

        return arrayData;
    }
}
