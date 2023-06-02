/************************************************************
 * <p>#File Name:       MerchantMenuCtl.java    </p>
 * <p>#Description:                 </p>
 * <p>#Create Date:     2007/10/11      </p>
 * <p>#Company:         cybersoft       </p>
 * <p>#Notice:                      </p>
 * @author      Caspar Chen
 * @since       SPEC version
 * @version 0.1 2007/10/11  Caspar Chen
 ************************************************************/
/**異動說明
 *                 20221124 Frog Jump Co., YC White Scan/A04 Insecure Design/Race Condition: Singleton Member Field
 **/
package com.cybersoft.merchant.ctl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft.bean.LogUtils;
import com.cybersoft.merchant.bean.MerchantMenuBean;
import com.fubon.security.filter.XSSRequestWrapper;
import com.fubon.tp.util.XSSUtils;
/**
 * <p>控制系統選單功能顯示的Servlet</p>
 * @version 0.1 2007/10/11  Caspar Chen
 */
public class MerchantMenuCtl extends HttpServlet
{
    private static final String CONTENT_TYPE = "text/html; charset=Big5";
    private String Forward = ""; // 網頁轉址
    public static final String MENU_SCRIPT_TEXT = "strScriptTextAction"; //系統選單-全部
    public static final String MENU_FORWARD_NAME = "hashMerchantMenuCtlActionForwardData"; //系統選單連結
    public static final String MENU_FORWARD_USER_ROLE = "hashMerchantMenuCtlUserRoleData"; //使用者系統選單確認
    public static final String MENU_FORWARD_KEY = "menuForwardKey"; //系統選單連結-參數名稱
    public static final LogUtils log_MerchantMenuCtl = new LogUtils("systeminfo");
    public DataBaseBean2 sysBean = new DataBaseBean2();   
	
    public void init()
    {
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        this.doPost(request, response);
    }
    
    private synchronized HttpSession getSession(HttpServletRequest request) {
    	return request.getSession(true);
    }    

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
    {
    	/** XSSUtils */
		XSSUtils xssUtils = new XSSUtils(request);
		
    	/** XSSRequestWrapper */
		XSSRequestWrapper xssRequestWrapper = new XSSRequestWrapper(request);
		request = (HttpServletRequest) xssRequestWrapper.getRequest();	
		
        response.addHeader("Pragma", "No-cache");
        response.addHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType(CONTENT_TYPE);
        try {
			request.setCharacterEncoding("BIG5");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
        
        /** HttpSession */
        HttpSession session = getSession(request);
        
        try
        {
            sysBean.setAutoCommit (false);
        }
        catch(UnsupportedOperationException E)
        {
            E.toString();
            try {
				request.getRequestDispatcher("/Merchant_Bye.jsp").forward(request, response);
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}
            return ;
        }

        try
        {
            //使用者系統選單
            getUserMenu(sysBean, session, xssRequestWrapper);            
            
            /** Need to test*/
            String nextPage = "Merchant_Menu.jsp";
    		xssUtils.dispatchPage(request, response, nextPage);
            
            /** Need to test -- (1). marked (By : YC)*/
            /**Forward = "Merchant_Menu.jsp";
            System.out.println("Forward=" + Forward);
            response.sendRedirect(response.encodeRedirectURL(Forward));*/
    		return;
        }
        catch (Exception e)
        {
            log_MerchantMenuCtl.debug("--MerchantMenuCtl--"+e.toString());
            xssRequestWrapper.setAttribute(session, "errMsg",e.toString());
            try {
				xssRequestWrapper.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
			} catch (ServletException | IOException e1) {
				e1.printStackTrace();
			}
        }
        //20130703 Jason Finally Close sysBean DB Connection
		finally {
			try {
				sysBean.close();
			} catch (Exception e) {
			}
		}
    }

    public static boolean getUserRole(DataBaseBean2 sysBean, HttpSession session, String key)
    {
        //宣告變數
        String mid = "";
        String uid = "";
        try
        {
            if (session != null)
            {
                java.util.Hashtable hashConfData = (java.util.Hashtable) session.getAttribute("SYSCONFDATA");

                if (hashConfData != null && hashConfData.size() > 0)
                {
                    java.util.Hashtable hashMerUser = (java.util.Hashtable) hashConfData.get("MERCHANT_USER"); // 使用者資料
                    // 使用者資料
                    if (hashMerUser != null)
                    {
                        if (hashMerUser.get("MERCHANT_ID") != null)
                        {
                            mid = hashMerUser.get("MERCHANT_ID").toString();
                        }
                        if (hashMerUser.get("USER_ID") != null)
                        {
                            uid = hashMerUser.get("USER_ID").toString();
                        }
                    }
                }

                Hashtable hashUserRoledData = (Hashtable) session.getAttribute(MENU_FORWARD_USER_ROLE);
                String ur = "";
                if (key != null)
                {
                    ur = String.valueOf(hashUserRoledData.get(key));
                    String ursp[] = ur.split(",");
                    String roleid = "";
                    String menuid = "";
                    ArrayList arrayData = new ArrayList();
                    if (ursp.length == 2)
                    {
                        roleid = ursp[0];
                        menuid = ursp[1];
                        MerchantMenuBean MUDBean = new MerchantMenuBean();
                        
                        /** 2023/04/25 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-009 */
                        arrayData = MUDBean.get_Merchant_User_Role(sysBean, mid, uid, roleid, menuid);
                    }

                    if (arrayData.size() > 0)
                    {
                        return true;
                    }
                }
            }
        }
        catch(Exception e)
        {
            log_MerchantMenuCtl.debug("--MerchantMenuCtl--"+e.toString());
            log_MerchantMenuCtl.debug("MerchantMenuCtl.getUserRole() Exception");
            e.printStackTrace();
        }

        return false;
    }

    private static void getUserMenu(DataBaseBean2 sysBean, HttpSession session, XSSRequestWrapper xssRequestWrapper)
    {
        //宣告變數
        String mid = "";
        String uid = "";

        java.util.Hashtable hashConfData = (java.util.Hashtable) session.getAttribute("SYSCONFDATA");

        if (hashConfData != null && hashConfData.size() > 0)
        {
            java.util.Hashtable hashMerUser = (java.util.Hashtable) hashConfData.get("MERCHANT_USER"); // 使用者資料
            // 使用者資料
            if (hashMerUser != null)
            {
                if (hashMerUser.get("MERCHANT_ID") != null)
                {
                    mid = hashMerUser.get("MERCHANT_ID").toString();
                }
                if (hashMerUser.get("USER_ID") != null)
                {
                    uid = hashMerUser.get("USER_ID").toString();
                }
            }
        }

        MerchantMenuBean MUDBean = new MerchantMenuBean();
        ArrayList arrayData = MUDBean.get_Merchant_User_Role(sysBean, mid, uid);
        Hashtable hashActionForwardData = new Hashtable();
        Hashtable hashUserRoledData = new Hashtable();
        String scriptText = "";
        String st1 = "├";
        String st2 = "└";
        if (arrayData != null && arrayData.size() > 0)
        {
            for (int i = 0; i < arrayData.size(); i++)
            {
                Hashtable hashData = (Hashtable) arrayData.get(i);
                String sd = getRandom(6, i + "AA", false);
                String sv = st1;
                while (hashActionForwardData.get(sd) != null)
                {
                    sd = getRandom(6, i + "AA", false);
                }

                if(i + 1 >= arrayData.size())
                {
                   sv = st2;
                }

                scriptText +="" + sv + hashData.get("MENUNAME") + ",item," + "./MerchantMenuForwardCtl?act=" + sd + ",main\\n";
                hashActionForwardData.put(sd, hashData.get("LOCATION"));
                String ur = hashData.get("ROLEID") +","+hashData.get("MENUID");
                hashUserRoledData.put(sd, ur);
            }
        }

        xssRequestWrapper.setAttribute(session, MENU_SCRIPT_TEXT, scriptText);
        xssRequestWrapper.setAttribute(session, MENU_FORWARD_NAME, hashActionForwardData);
        xssRequestWrapper.setAttribute(session, MENU_FORWARD_USER_ROLE, hashUserRoledData);
    }

    /**
     *
     * @param i int       回傳長度
     * @param r String    預設值
     * @param b boolean   是否要小數點
     * @return String
     */
    public static String getRandom(int i, String r, boolean b)
    {
        boolean boolReturn = false;
        String strR = "";
        if(b)
        {
            strR = String.valueOf(Math.random());
        }
        else
        {
            strR = String.valueOf(Math.random()*(Math.pow(10,i)));
        }

        if (strR.length() > i && i > 1)
        {
            boolReturn = true;
            strR = strR.substring(0, (i-1));
        }

        if (!boolReturn)
        {
            strR = r;
        }
        return strR;
    }     
}
