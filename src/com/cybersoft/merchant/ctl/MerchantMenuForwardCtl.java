/************************************************************
 * <p>#File Name:       MerchantMenuForwardCtl.java </p>
 * <p>#Description:                 </p>
 * <p>#Create Date:     2007/10/15      </p>
 * <p>#Company:         cybersoft       </p>
 * <p>#Notice:                      </p>
 * @author      Caspar Chen
 * @since       SPEC version
 * @version 0.1 2007/10/15  Caspar Chen
 ************************************************************/
/**異動說明
 *                 20221124 Frog Jump Co., YC White Scan
 **/
package com.cybersoft.merchant.ctl;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest; import com.fubon.security.filter.XSSRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cybersoft.bean.LogUtils;
import com.cybersoft.bean.SessionControlBean;
import com.cybersoft.merchant.bean.MerchantMenuBean;
import com.fubon.tp.util.XSSUtils;
/**
 * <p>控制系統選單功能點選後導向頁面的Servlet</p>
 * @version 0.1 2007/10/15  Caspar Chen
 */
public class MerchantMenuForwardCtl extends HttpServlet
{
	private static final String CONTENT_TYPE = "text/html; charset=Big5"; 
	
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");

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

	/**
	 * 20221124 Frog Jump Co., YC White Scan/A01 Broken Access Control/File Disclosure: J2EE
	 * 20221124 Frog Jump Co., YC White Scan/A04 Insecure Design/Race Condition: Singleton Member Field
	 */    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {    
    	/** XSSUtils */
		XSSUtils xssUtils = new XSSUtils(request);
		
		/** XSSRequestWrapper */
		XSSRequestWrapper xssRequestWrapper = new XSSRequestWrapper(request);
		request = (HttpServletRequest) xssRequestWrapper.getRequest();		
        request.setCharacterEncoding("BIG5");

        /** response */
        response.addHeader("Pragma", "No-cache");
        response.addHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType(CONTENT_TYPE);
        
		/** HttpSession */
		HttpSession session = getSession(request);
	
        try
        {
        	@SuppressWarnings("unused")
			SessionControlBean scb =new SessionControlBean();
        	scb = new SessionControlBean(session,request,response);
        }
        catch(UnsupportedOperationException E)
        {
            E.toString();
            
            /** Need to test */
            xssUtils.dispatchPage(request, response, "/Merchant_Bye.jsp");
            
            return ;
        }
        
        String nextPage = ""; // 網頁轉址
        
        try
        {
            /** Need to test */
        	//宣告變數
            nextPage = "Merchant_Main.jsp";
            String act = xssRequestWrapper.getWebField("act");
            if(act != null || act.length() > 0)
            {
                Hashtable hashActionForwardData = (Hashtable)session.getAttribute(MerchantMenuCtl.MENU_FORWARD_NAME);
                if(hashActionForwardData.get(act)!= null)
                {
                    nextPage = (String)hashActionForwardData.get(act) + "?" + MerchantMenuCtl.MENU_FORWARD_KEY + "=" + act;
                }
            }
            log_systeminfo.debug("nextPage=" + nextPage);
    		xssUtils.dispatchPage(request, response, nextPage);
        }
        catch (Exception e)
        {
            /** Need to test */
        	log_systeminfo.debug("--MerchantMenuForwardCtl--"+e.toString());
            request.setAttribute("errMsg",e.toString());
            final String furl = "./Merchant_Error.jsp";
    		xssUtils.dispatchPage(request, response, furl);
        }
    }   
}
