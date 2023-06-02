package com.cybersoft.merchant.bean;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;

/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import java.util.Hashtable;
import java.text.SimpleDateFormat;
import com.cybersoft.bean.LogUtils;
import com.fubon.security.filter.SecurityTool;

public class MerchantBatchAuthBean
{
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    public MerchantBatchAuthBean()
    {
    }

//    /**
//     * 取得端末機主檔
//     * @return ArrayList        特店端末機資料
//     */
//    public Hashtable get_Terminal()
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        ArrayList tableResult=new ArrayList();
//        Hashtable result=new Hashtable();
//        String Sql = "Select * From TERMINAL ";
//        // System.out.println("Sql="+Sql);
//        try
//        {
//           tableResult = (ArrayList) SysBean.executeSQL(Sql,"select");
//           for(int i=0; tableResult != null&& i<tableResult.size(); i++)
//           {
//               Hashtable content=(Hashtable) tableResult.get(i);
//               String index=(String) content.get("MERCHANTID");
//               index+=content.get("TERMINALID");
//               result.put(index,content);
//           }
//
//        }
//        catch(Exception ex)
//        {
//          System.out.println(ex.getMessage());
//          log_systeminfo.debug(ex.toString());
//        }
//
//        return result;
//      }

    /* Override get_Terminal with DataBaseBean parameter */
    public Hashtable get_Terminal(DataBaseBean2 sysBean)
    {
        // DataBaseBean SysBean = new DataBaseBean();
        ArrayList tableResult=new ArrayList();
        Hashtable result=new Hashtable();
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("Select * From TERMINAL ");
		
        // System.out.println("Sql="+Sql);
        try
        {
        	/** 2023/05/17 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-090 (No Need Test) */
        	tableResult = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        	for(int i=0; tableResult != null&& i<tableResult.size(); i++)
        	{
               Hashtable content=(Hashtable) tableResult.get(i);
               String index=(String) content.get("MERCHANTID");
               index+=content.get("TERMINALID");
               result.put(index,content);
        	}

        }
        catch(Exception ex)
        {
          System.out.println(ex.getMessage());
          log_systeminfo.debug(ex.toString());
        }

        return result;
      }

    /**
     * 產生交易日期
     * @return String TransDate  交易日期
     */
     public String get_TransDate(String Type)
     {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        calendar.add(Calendar.DATE, 0);
        SimpleDateFormat sdf = new SimpleDateFormat(Type);
        String NowTime = sdf.format(calendar.getTime());

        return NowTime;
     }
}
