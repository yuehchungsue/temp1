package com.cybersoft.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import javax.servlet.*;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import java.io.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import com.cybersoft.bean.LogUtils;
import com.cybersoft.bean.UserBean;
import com.fubon.security.filter.SecurityTool;
/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;

public class SessionControlBean implements HttpSessionBindingListener, HttpSessionActivationListener, java.io.Serializable
{
	private static final long serialVersionUID = -6808534395333321861L;
    public SessionControlBean()
    {
    }

    private HttpSession session;
    private ServletContext sc;

    public SessionControlBean(HttpSession aSession,HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException 
    {
        this.session = aSession;
        this.sc = aSession.getServletContext();
        Hashtable sysconfdata=(Hashtable) session.getAttribute("SYSCONFDATA");
    
        if(sysconfdata == null)
        {
            throw new java.lang.UnsupportedOperationException();
        }
    }

    /**
     * called when session will passivate, check if there is AFTask Object in session
     * and remove it from session, keep in TaskExternalSessionRestores
     * @param se HttpSessionEvent
     */
    public void sessionWillPassivate(HttpSessionEvent httpSessionEvent) 
    {
        System.out.println("sessionWillPassivate!!!");
        this.session = null;
    }

    public void sessionDidActivate(HttpSessionEvent httpSessionEvent) 
    {
        System.out.println("sessionDidActivate!!!");
        this.session = httpSessionEvent.getSession();
        this.sc = session.getServletContext();
    }
    
    /** The method implements HttpSessionBindingListener.
     * If the session start then the method will be called.
     *
     * @param event HttpSessionBindingEvent object
     */
    public void valueBound(HttpSessionBindingEvent httpSessionBindingEvent) 
    {
        System.out.println("valueBound!!!");
    }
    
    /** The method implements HttpSessionBindingListener.
     * If the session destory then the method will be called.
     *
     * @param event HttpSessionBindingEvent object
     */
    public void valueUnbound(HttpSessionBindingEvent httpSessionBindingEvent) 
    {
        //移除線上使用者
    }
    
    /**
     * 確認 Session 狀態
     * @param String MerchantID 特店代號
     * @param String UserID     使用者代號
     * @param String SessionID  使用者密碼
     * @param String Today      指定今天日期
     * @return Boolean Flag     執行結果 true:可使用 false:不可使用
     */
    public boolean check_Session(String MerchantID, String UserID, String SessionID,HttpSession session, String Today) 
    {
    	DataBaseBean2 sysBean = new DataBaseBean2();
    	
        boolean Flag = false;
        
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("SELECT MERCHANT_ID,USER_ID,SESSION_ID,To_char(LOGIN_DATE,'yyyy-mm-dd hh:mi:ss') LOGIN_DATE,LOGOUT_DATE,SESSION_STATUS FROM SESSION_CONTROL WHERE MERCHANT_ID = ? AND USER_ID = ? AND  SESSION_STATUS = 'Y' AND SESSION_ID = ? AND TO_CHAR(LOGIN_DATE,'yyyy/mm/dd') = ? ");
		
		sysBean.AddSQLParam(emDataType.STR, MerchantID);
		sysBean.AddSQLParam(emDataType.STR, UserID);
		sysBean.AddSQLParam(emDataType.STR, SessionID);
		sysBean.AddSQLParam(emDataType.STR, Today);
        // System.out.println("Sql="+Sql);
        
        ArrayList arraySys = new ArrayList();
        try 
        {
        	/** 2023/05/23 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-084 */
            arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch(Exception ex) 
        {
            System.out.println(ex.getMessage());
        }
        
        System.out.println("arraySys.size()="+arraySys.size());
        if(arraySys.size()==0) 
        {
            Flag = true;
        }
        else 
        {
            for(int i=0;i<arraySys.size();i++)
            {
                Hashtable hashData = (Hashtable)arraySys.get(i);
                String Session_Status = hashData.get("SESSION_STATUS").toString();
                if(Session_Status.equalsIgnoreCase("N")) 
                { 
                  //  己登出
                    Flag = true;
                }
                else 
                {
                    Flag = false;
                    String srvDate = (String) hashData.get("LOGIN_DATE");
        
                    //        System.out.println("Login Time:"+Integer.parseInt(srvDate.split("-")[0])+"||"+Integer.parseInt(srvDate.split("-")[1])+"||"+Integer.parseInt(srvDate.split("-")[2].split(" ")[0])+"||"+Integer.parseInt(srvDate.split("-")[1].split(" ")[2].split(":")[0])+"||"+Integer.parseInt(srvDate.split("-")[2].split(" ")[1].split(":")[1])+"||"+Integer.parseInt(srvDate.split("-")[2].split(" ")[1].split(":")[2].split(".")[0]));
                    Calendar test=new GregorianCalendar(Integer.parseInt(srvDate.split("-")[0]),Integer.parseInt(srvDate.split("-")[1]),Integer.parseInt(srvDate.split("-")[2].split(" ")[0]),Integer.parseInt(srvDate.split("-")[2].split(" ")[1].split(":")[0]),Integer.parseInt(srvDate.split("-")[2].split(" ")[1].split(":")[1]),Integer.parseInt(srvDate.split("-")[2].split(" ")[1].split(":")[2]));
                    long diff = (new java.util.Date()).getTime()-test.getTime().getTime();
                    long timeout = 15*60*1000;
                    if(diff>=timeout) 
                    { 
                        //Time Out
                        if(close_Session(MerchantID, UserID, SessionID,true)) 
                        {
                            Flag = true;
                        }
                    }
                    else
                    {
                        ServletContext sc=session.getServletContext();
                        String keyvalue=(String)sc.getAttribute(MerchantID+"@"+UserID);
                        Enumeration key=sc.getAttributeNames();
                        while (key.hasMoreElements())
                        {
                            String aa=String.valueOf(key.nextElement());
                            if(aa.indexOf("org")<0&&aa.indexOf("javax")<0)
                            System.out.println(aa+"<"+sc.getAttribute(aa)+">");
                        }
                        
                        if(keyvalue==null)
                        {
                            Flag = true;
                        }
                    }
                }
                
                if(!Flag)
                    break;
            }
        }

        return Flag;
    }

    /* Override check_Session with DataBaseBean parameter */
    public boolean check_Session(DataBaseBean2 sysBean, String MerchantID, String UserID, String SessionID,HttpSession session, String Today) 
    {
        boolean Flag = false;
        // DataBaseBean SysBean = new DataBaseBean();
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append(" SELECT MERCHANT_ID, USER_ID, SESSION_ID, To_char(LOGIN_DATE, 'yyyy-mm-dd hh:mi:ss') LOGIN_DATE, LOGOUT_DATE, SESSION_STATUS FROM SESSION_CONTROL WHERE MERCHANT_ID = ? ");
		sSQLSB.append(" AND USER_ID = ? ");
		sSQLSB.append(" AND SESSION_STATUS = 'Y' ");
		sSQLSB.append(" AND TO_CHAR(LOGIN_DATE,'yyyy/mm/dd') = ? ");
		sysBean.AddSQLParam(emDataType.STR, MerchantID);
		sysBean.AddSQLParam(emDataType.STR, UserID);
		sysBean.AddSQLParam(emDataType.STR, Today);

        // System.out.println("Sql="+Sql);
        
        ArrayList arraySys = new ArrayList();
        try 
        {
        	/** 2023/04/21 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-007 */
            arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString()); /** ir : IR-001 */
        }
        catch(Exception ex) 
        {
            System.out.println(ex.getMessage());
        }
        
        System.out.println("arraySys.size()="+arraySys.size());
        if(arraySys.size()==0) 
        {
            Flag = true;
        }
        else 
        {
            for(int i=0;i<arraySys.size();i++)
            {
                Hashtable hashData = (Hashtable)arraySys.get(i);
                String Session_Status = hashData.get("SESSION_STATUS").toString();
                if(Session_Status.equalsIgnoreCase("N")) 
                { 
                  //  己登出
                    Flag = true;
                }
                else 
                {
                    Flag = false;
                    String srvDate = (String) hashData.get("LOGIN_DATE");
        
                    //        System.out.println("Login Time:"+Integer.parseInt(srvDate.split("-")[0])+"||"+Integer.parseInt(srvDate.split("-")[1])+"||"+Integer.parseInt(srvDate.split("-")[2].split(" ")[0])+"||"+Integer.parseInt(srvDate.split("-")[1].split(" ")[2].split(":")[0])+"||"+Integer.parseInt(srvDate.split("-")[2].split(" ")[1].split(":")[1])+"||"+Integer.parseInt(srvDate.split("-")[2].split(" ")[1].split(":")[2].split(".")[0]));
                    Calendar test=new GregorianCalendar(Integer.parseInt(srvDate.split("-")[0]),Integer.parseInt(srvDate.split("-")[1]),Integer.parseInt(srvDate.split("-")[2].split(" ")[0]),Integer.parseInt(srvDate.split("-")[2].split(" ")[1].split(":")[0]),Integer.parseInt(srvDate.split("-")[2].split(" ")[1].split(":")[1]),Integer.parseInt(srvDate.split("-")[2].split(" ")[1].split(":")[2]));
                    long diff = (new java.util.Date()).getTime()-test.getTime().getTime();
                    long timeout = 15*60*1000;
                    if(diff>=timeout) 
                    { 
                        //Time Out
                        if(close_Session(MerchantID, UserID, SessionID,true)) 
                        {
                            Flag = true;
                        }
                    }
                    else
                    {
                        ServletContext sc=session.getServletContext();
                        String keyvalue=(String)sc.getAttribute(MerchantID+"@"+UserID);
                        Enumeration key=sc.getAttributeNames();
                        while (key.hasMoreElements())
                        {
                            String aa=String.valueOf(key.nextElement());
                            if(aa.indexOf("org")<0&&aa.indexOf("javax")<0)
                            System.out.println(aa+"<"+sc.getAttribute(aa)+">");
                        }
                        
                        if(keyvalue==null)
                        {
                            Flag = true;
                        }
                    }
                }
                
                if(!Flag)
                    break;
            }
        }

        return Flag;
    }
//    /**
//     * 新增 Session
//     * @param String MerchantID 特店代號
//     * @param String UserID     使用者代號
//     * @param String SessionID  使用者密碼
//     * @return Boolean Flag     執行結果 true:成功 false:失敗
//     */
//    public boolean credit_Session(String MerchantID, String UserID, String SessionID) 
//    {
//        boolean Flag = false;
//        DataBaseBean SysBean = new DataBaseBean();
//        
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new java.util.Date());
//        calendar.add(Calendar.DATE, 0);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        String NowTime = sdf.format(calendar.getTime());
//        System.out.println("NowTime="+NowTime);
//        String Sql = "INSERT INTO SESSION_CONTROL (MERCHANT_ID,USER_ID,SESSION_ID,LOGIN_DATE,SESSION_STATUS) VALUES ( '";
//        Sql = Sql +  MerchantID+"', '"+UserID+"', '"+SessionID+"', TO_DATE('"+NowTime+"','YYYY/MM/DD HH24:MI:SS') ,'Y' )";
//        //System.out.println("Sql="+Sql);
//        
//        try 
//        {
//           Flag = ((Boolean) SysBean.executeSQL(Sql, "insert")).booleanValue();
//           System.out.println ("Flag: credit_Session Result: " + Flag);
//        }
//        catch(Exception ex) 
//        {
//          System.out.println ("Flag: credit_Session Exception: " + Flag);
//          System.out.println("credit_Session Exception:" + ex.getMessage());
//        }
//        
//        return Flag;
//    }

    /* Override credit_Session with DataBaseBean parameter */
    public boolean credit_Session(DataBaseBean2 SysBean, String MerchantID, String UserID, String SessionID) 
    {
        boolean flag = false;
        // DataBaseBean SysBean = new DataBaseBean();
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        calendar.add(Calendar.DATE, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String NowTime = sdf.format(calendar.getTime());
        System.out.println("NowTime="+NowTime);
        SysBean.ClearSQLParam();
    	StringBuffer sSQLSB = new StringBuffer();
    	sSQLSB.append("INSERT INTO SESSION_CONTROL (MERCHANT_ID,USER_ID,SESSION_ID,LOGIN_DATE,SESSION_STATUS) VALUES ( ");
    	sSQLSB.append(" ?, ?, ?, TO_DATE(?, 'YYYY/MM/DD HH24:MI:SS') ,'Y' )");
    	SysBean.AddSQLParam(emDataType.STR, MerchantID);
    	SysBean.AddSQLParam(emDataType.STR, UserID);
    	SysBean.AddSQLParam(emDataType.STR, SessionID);
    	SysBean.AddSQLParam(emDataType.STR, NowTime);
        //System.out.println("Sql="+sSQLSB.toString());
        
        try 
        {
        	/** 2023/04/24 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-008 */
        	flag = SysBean.executeSQL(sSQLSB.toString());
           
            /** Fix : java.sql.SQLException: 自動確認設定為開啟時無法確認 (By : YC) */
            SysBean.commit();
        	
        	System.out.println ("Flag: credit_Session Result: " + flag);
        }
        catch(Exception ex) 
        {
          System.out.println ("Flag: credit_Session Exception: " + flag);
          System.out.println("credit_Session Exception:" + ex.getMessage());
        }
        
        return flag;
    }
    
    /**
     * 刪除 Session
     * @param String MerchantID 特店代號
     * @param String UserID     使用者代號
     * @param String SessionID  使用者密碼
     * @param boolean writeflag 是否填寫Log Flag
     * @return Boolean Flag     執行結果 true:成功 false:失敗
     */
    public boolean close_Session(String MerchantID, String UserID, String SessionID, boolean writeflag) 
    {
    	DataBaseBean2 sysBean = new DataBaseBean2();
    	
        boolean flag = false;
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        calendar.add(Calendar.DATE, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String NowTime = sdf.format(calendar.getTime());
        
        if (writeflag) 
        {
            LogUtils log_user = new LogUtils("user");
            String LogUserName = UserID + "(" + UserID + ")";
            String LogFunctionName = "登出";
            String LogStatus = "成功";
            String LogMemo = "登出成功";
            String LogMerchantID = MerchantID;
            UserBean UserBean = new UserBean();
            String LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName,LogStatus, LogMemo);
            log_user.debug(LogData);
        }
        
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("UPDATE SESSION_CONTROL SET LOGOUT_DATE = SYSDATE ,SESSION_STATUS = 'N' WHERE MERCHANT_ID = ? and USER_ID = ? and SESSION_ID = ? ");
		sysBean.AddSQLParam(emDataType.STR, MerchantID);
		sysBean.AddSQLParam(emDataType.STR, UserID);
		sysBean.AddSQLParam(emDataType.STR, SessionID);
		
        // System.out.println("Sql="+Sql);
        ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/05/24 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-085 */
        	arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        System.out.println("arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
            flag = true;
        }
        
        return flag;
    }
  
    /* Override close_Session with DataBaseBean parameter */
    public boolean close_Session(DataBaseBean2 sysBean, String MerchantID, String UserID, String SessionID, boolean writeflag) 
    {
        boolean flag = false;
        // DataBaseBean SysBean = new DataBaseBean();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        calendar.add(Calendar.DATE, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String NowTime = sdf.format(calendar.getTime());
        
        if (writeflag) 
        {
            LogUtils log_user = new LogUtils("user");
            String LogUserName = UserID + "(" + UserID + ")";
            String LogFunctionName = "登出";
            String LogStatus = "成功";
            String LogMemo = "登出成功";
            String LogMerchantID = MerchantID;
            UserBean UserBean = new UserBean();
            String LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName,LogStatus, LogMemo);
            log_user.debug(LogData);
        }
        
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("UPDATE SESSION_CONTROL SET LOGOUT_DATE = SYSDATE ,SESSION_STATUS = 'N' WHERE MERCHANT_ID = ? and USER_ID = ? and SESSION_ID = ? ");
		sysBean.AddSQLParam(emDataType.STR, MerchantID);
		sysBean.AddSQLParam(emDataType.STR, UserID);
		sysBean.AddSQLParam(emDataType.STR, SessionID);
		
        // System.out.println("Sql="+Sql);
        ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/05/24 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-086 */
        	arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        System.out.println("arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
        	flag = true;
        }
        
        return flag;
    }  
//  /**
//   * 刪除 Session
//   * @param String MerchantID 特店代號
//   * @param String UserID     使用者代號
//   * @param boolean writeflag 是否填寫Log Flag
//   * @return Boolean Flag     執行結果 true:成功 false:失敗
//   */
//    public boolean close_Session(String MerchantID, String UserID, boolean writeflag) 
//    {
//        boolean Flag = false;
//        DataBaseBean SysBean = new DataBaseBean();
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new java.util.Date());
//        calendar.add(Calendar.DATE, 0);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        String NowTime = sdf.format(calendar.getTime());
//        
//        if (writeflag) 
//        {
//            LogUtils log_user = new LogUtils("user");
//            String LogUserName = UserID + "(" + UserID + ")";
//            String LogFunctionName = "登出";
//            String LogStatus = "成功";
//            String LogMemo = "登出成功";
//            String LogMerchantID = MerchantID;
//            UserBean UserBean = new UserBean();
//            String LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
//            log_user.debug(LogData);
//        }
//        
//        String Sql = "UPDATE SESSION_CONTROL SET LOGOUT_DATE = SYSDATE ,SESSION_STATUS = 'N' WHERE MERCHANT_ID = '"+
//                      MerchantID+"' and USER_ID = '"+UserID+"' ";
//        // System.out.println("Sql="+Sql);
//        try 
//        {
//            Flag = ((Boolean) SysBean.executeSQL(Sql, "insert")).booleanValue();
//        }
//        catch(Exception ex) 
//        {
//          System.out.println(ex.getMessage());
//        }
//        
//        return Flag;
//    }

    /* Override close_Session with DataBaseBean parameter */
    public boolean close_Session(DataBaseBean2 sysBean, String MerchantID, String UserID, boolean writeflag) 
    {
        boolean flag = false;
        // DataBaseBean SysBean = new DataBaseBean();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        calendar.add(Calendar.DATE, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String NowTime = sdf.format(calendar.getTime());
        
        if (writeflag) 
        {
            LogUtils log_user = new LogUtils("user");
            String LogUserName = UserID + "(" + UserID + ")";
            String LogFunctionName = "登出";
            String LogStatus = "成功";
            String LogMemo = "登出成功";
            String LogMerchantID = MerchantID;
            UserBean UserBean = new UserBean();
            String LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, LogStatus, LogMemo);
            log_user.debug(LogData);
        }
        
        StringBuffer sSQLSB = new StringBuffer();
		sysBean.ClearSQLParam();
		sSQLSB.append("UPDATE SESSION_CONTROL SET LOGOUT_DATE = SYSDATE ,SESSION_STATUS = 'N' WHERE MERCHANT_ID = ? and USER_ID = ? ");
		
		sysBean.AddSQLParam(emDataType.STR, MerchantID);
		sysBean.AddSQLParam(emDataType.STR, UserID);
		
        // System.out.println("Sql="+Sql);
        ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/05/19 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-032 (No Need Test) */
        	arraySys = sysBean.QuerySQLByParam(sSQLSB.toString());
        	
        	/** Fix : java.sql.SQLException: 自動確認設定為開啟時無法確認 (By : YC) */
            sysBean.commit();
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        
        System.out.println("arraySys.size()="+arraySys.size());
        if(arraySys.size() > 0) 
        {
        	flag = true;
        }
        
        return flag;
    }
    
    /**
     * 取得 Session 狀態 
     * @param String MerchantID       特店代號
     * @param String UserID           使用者代號
     * @param String Today            指定今天日期
     * @return Hashtable SessionData  Session 資料
     */
    public Hashtable get_Session(String MerchantID, String UserID,String Today) 
    {
        DataBaseBean2 sysBean = new DataBaseBean2();
        
        Hashtable Sessiondata = new Hashtable();
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("SELECT MERCHANT_ID,USER_ID,SESSION_ID,To_char(LOGIN_DATE,'yyyy-mm-dd hh:mi:ss') LOGIN_DATE,LOGOUT_DATE,SESSION_STATUS FROM SESSION_CONTROL WHERE MERCHANT_ID = ? AND USER_ID = ? AND  SESSION_STATUS = 'Y' AND TO_CHAR(LOGIN_DATE,'yyyy/mm/dd') = ? ORDER BY LOGIN_DATE DESC ");
		sysBean.AddSQLParam(emDataType.STR, MerchantID);
		sysBean.AddSQLParam(emDataType.STR, UserID);
		sysBean.AddSQLParam(emDataType.STR, Today);
		
		// System.out.println("Sql="+Sql);
        ArrayList arraySys = new ArrayList();
        
        try 
        {
        	/** 2023/05/24 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-087 */
            arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch(Exception ex) 
        {
          System.out.println(ex.getMessage());
        }
        
        System.out.println("arraySys.size()="+arraySys.size());
        if(arraySys.size()>0) 
        {
           Sessiondata = (Hashtable)arraySys.get(0);
        }
        
        return Sessiondata;
    }
    
//    /* Override get_Session with DataBaseBean parameter */
//    public Hashtable get_Session(DataBaseBean SysBean, String MerchantID, String UserID,String Today) 
//    {
//        // DataBaseBean SysBean = new DataBaseBean();
//        Hashtable Sessiondata = new Hashtable();
//        String Sql = "SELECT MERCHANT_ID,USER_ID,SESSION_ID,To_char(LOGIN_DATE,'yyyy-mm-dd hh:mi:ss') LOGIN_DATE,LOGOUT_DATE,SESSION_STATUS FROM SESSION_CONTROL WHERE MERCHANT_ID = '"+
//                MerchantID+"' AND USER_ID='"+UserID+"' AND  SESSION_STATUS = 'Y' AND TO_CHAR(LOGIN_DATE,'yyyy/mm/dd') = '"+Today+"' ORDER BY LOGIN_DATE DESC ";
//        // System.out.println("Sql="+Sql);
//        ArrayList arraySys = new ArrayList();
//        
//        try 
//        {
//            arraySys = (ArrayList) SysBean.executeSQL(Sql,"select");
//        }
//        catch(Exception ex) 
//        {
//          System.out.println(ex.getMessage());
//        }
//        
//        System.out.println("arraySys.size()="+arraySys.size());
//        if(arraySys.size()>0) 
//        {
//           Sessiondata = (Hashtable)arraySys.get(0);
//        }
//        
//        return Sessiondata;
//    }
}
