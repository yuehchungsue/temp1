/************************************************************
 * <p>#File Name:       MerchantSignBillCtl.java </p>
 * <p>#Description:                 </p>
 * <p>#Create Date:     2022/07/20      </p>
 * <p>#Company:         fubon       </p>
 * <p>#Notice:                      </p>
 * @author      Jeffery
 * @since       SPEC version
 * @version 0.1 2022/07/20 Jeffery
 * @modify      2022/07/20 Jeffery
 ************************************************************/
package com.cybersoft.merchant.ctl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft.bean.LogUtils;
import com.cybersoft.bean.SessionControlBean;
import com.cybersoft.bean.UserBean;
import com.cybersoft.common.EnRSA;
import com.cybersoft.common.Util;
import com.fubon.tp.model.pojo.MerchantgGroup;
import com.fubon.tp.model.pojo.TErmLog;
import com.fubon.tp.model.pojo.TMerchants;
import com.fubon.tp.service.SignLogoService;
import com.fubon.tp.service.SignService;
import com.google.gson.Gson;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * <p>控制登入系統的Servlet</p>
 * @version 0.1 2022/07/20  Jeffery
 */
public class MerchantSignBillCtl extends HttpServlet {
	
    private static final String CONTENT_TYPE = "text/html; charset=Big5";
    private String Forward = ""; // 網頁轉址
    private String Message = ""; // 顯示訊息
    java.util.Date nowdate;
    public static final LogUtils log_user = new LogUtils("user");
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    public UserBean UserBean = new UserBean();
    public DataBaseBean2 sysBean = new DataBaseBean2();

    String LogUserName = "";
    String LogFunctionName = "電子簽單查詢";
    String LogStatus = "成功";
    String LogMemo = "";
    String LogData = "";
    String LogMerchantID = "";

	public void init() {
//    	System.out.println("callDoPostTimes : "+callDoPostTimes);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//        response.setContentType(CONTENT_TYPE);
//        request.setCharacterEncoding("BIG5");
        response.setContentType("text/html; charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        
        HttpSession session = request.getSession(true);
        String errMessage="";
        int queryMax = 3000;
        
        
        	SessionControlBean scb = new SessionControlBean();
		try {
			scb = new SessionControlBean(session, request, response);
			sysBean.setAutoCommit(false);
		} catch (UnsupportedOperationException E) {
			E.toString();
			String Action = (request.getParameter("Action") == null) ? "" : request.getParameter("Action");
			System.out.println("Action=" + Action);
			if (Action.equalsIgnoreCase("OPEN")) {
				request.getRequestDispatcher("/Merchant_Close.jsp").forward(request, response);
			} else {
				request.getRequestDispatcher("/Merchant_Bye.jsp").forward(request, response);
			}
			return;
		}
        try{
            Hashtable hashSys = new Hashtable(); // 系統參數
            Hashtable hashMerUser = new Hashtable(); // 特店使用者
            Hashtable hashMerchant = new Hashtable(); // 特店主檔
            ArrayList arrayTerminal = new ArrayList(); // 端末機主檔
            //子特店清單
            ArrayList subMidList = new ArrayList();
          //是否為單一特店
            boolean isSignMer = false;
            Hashtable hashConfData = (Hashtable) session.getAttribute("SYSCONFDATA");

            if (hashConfData == null)
                hashConfData = new Hashtable();

            boolean Merchant_Current = false; // 特店狀態
            boolean Merchant_Permit = false; // 特店權限
			if (hashConfData.size() > 0) {
				hashSys = (Hashtable) hashConfData.get("SYSCONF"); // 系統參數
				hashMerUser = (Hashtable) hashConfData.get("MERCHANT_USER"); // 特店使用者
				hashMerchant = (Hashtable) hashConfData.get("MERCHANT"); // 特店主檔
				queryMax = Integer.parseInt(hashSys.get("MER_AUTH_QRY_QUANTITY").toString());// 授權查詢最高筆數
				subMidList = (ArrayList) hashConfData.get("SUBMID");
				isSignMer = subMidList != null && subMidList.size() > 1 ? false : true;

				if (hashSys == null)
					hashSys = new Hashtable();

				if (hashMerUser == null)
					hashMerUser = new Hashtable();

				if (hashMerchant == null)
					hashMerchant = new Hashtable();

				if (hashMerchant.size() > 0) {
					LogMerchantID = (String) hashMerUser.get("MERCHANT_ID");
				}

				arrayTerminal = (ArrayList) hashConfData.get("TERMINAL"); // 端末機主檔
				if (arrayTerminal == null)
					arrayTerminal = new ArrayList();
			}

            // UserBean UserBean = new UserBean();
            String MenuKey = (String) request.getParameter(MerchantMenuCtl.MENU_FORWARD_KEY);
            
            boolean User_Permit = MerchantMenuCtl.getUserRole(sysBean, session, MenuKey);
            Merchant_Current = UserBean.check_Merchant_Column(hashMerchant, "CURRENTCODE", "B,C,D"); //  確認特店狀態
            Merchant_Permit  = UserBean.check_Merchant_Column(hashMerchant, "PERMIT_INQUIRY_TX", "Y"); //  確認特店權限
            String reportflag = "";

			if (User_Permit) {
				// 使用者權限
				if (Merchant_Current) {
					// 特店狀態
					if (Merchant_Permit) {
						/***
				         * ajax
				         */
			    	        if(request.getParameter("ajax")!=null) {
			    	        	String ajax=request.getParameter("ajax");
			    	        	System.out.println("deal ajax req start !");
			    	        	
			    	        	if("ajaxReq_pdf".equals(ajax)) {
			    	        		System.out.println("ajaxReq_pdf req start !");
			    	        		Map<String, Object> ajaxReturn= signInfo(request);
			    	        		String json = new Gson().toJson(ajaxReturn);
			    	        		//System.out.println(json);
			    	        	    response.setContentType("application/json");
			    	        	    response.setCharacterEncoding("UTF-8");
			    	        	    request.setCharacterEncoding("UTF-8");
			    	        		PrintWriter out = response.getWriter();
			    	        	    out.print(json);
			    	        	    out.flush();
			    	        	    System.out.println("ajaxReq_pdf end !");
			    	        	    return;
			    	        	}
			    	        }
//			        	}//ajax_end
						
						//查詢畫面變數賦值開始
						
			            request.setAttribute(MerchantMenuCtl.MENU_FORWARD_KEY, MenuKey);
			            
			            //for signInfo.jsp ./MerchantSignBillCtl?menuForwardKey=78171
			            String forwardStr="./MerchantSignBillCtl?"+MerchantMenuCtl.MENU_FORWARD_KEY+"="+MenuKey;
			            request.setAttribute("forwardStr",forwardStr);
			            //行員查詢顯示輸入框，特店端查詢顯示子特店下拉選單，行員的SIGN_BILL ='B'，電簽特店SIGN_BILL='Y'
			            String SIGN_BILL_Str= Util.objToStrTrim(hashMerUser.get("SIGN_BILL"));
			            if("B".equals(SIGN_BILL_Str)) {
			            	String clientIp= request.getRemoteAddr();
			            	//System.out.println(clientIp);
			            	//限制SIGN_BILL ='B'只能從內網查詢
			            	if("172.".equals(clientIp.substring(0, 4))==false) {
			    				LogUserName = Util.objToStrTrim(hashMerUser.get("USER_ID")) + "(" + Util.objToStrTrim(hashMerUser.get("USER_NAME"))+ ")";
			            		Message = "WARNING!!! illegal IP："+clientIp+"，禁止外網使用，Merchant:"+LogMerchantID+",user:"+LogUserName;
			            		//WRITE LOG
			    				LogMemo = Message;
			    				LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "WARNING", LogMemo);
			    				log_user.debug(LogData);
			    				Forward = "./Merchant_Response.jsp";
			    				session.setAttribute("Message", Message);
			    				request.getRequestDispatcher(Forward).forward(request, response);
			    				return;
			            	}
			            	request.setAttribute("SIGN_BILL_Flag", SIGN_BILL_Str); //jsp切換畫面使用
			            	request.setAttribute("merchantId_type_B",Util.objToStrTrim(request.getParameter("merchantId"))); //行員ui畫面上keyin的merchantid
			            }else if(!"B".equals(SIGN_BILL_Str)) {
			            	String merchantId=Util.objToStrTrim(hashMerUser.get("MERCHANT_ID"));
			            	//子特店下拉集合
				            List<MerchantgGroup> allMerchantStore = signService.getAllMerchantStoreByHqMid(merchantId);	
				        	request.setAttribute("allMerchantStore", allMerchantStore);
				        	request.setAttribute("MERCHANT_ID_choosen", merchantId);
			            }
			        	
			            UserBean mlb=new UserBean();
			          	String Today = mlb.get_TransDate("yyyy/MM/dd");
			          	//UI進階查詢的日期
			        	String sTX_DATE_Start = Util.objToStrTrim(request.getAttribute("Start_TransDate")); 
			        	String sTX_DATE_End   = Util.objToStrTrim(request.getAttribute("End_TransDate"));
			          	if(sTX_DATE_Start.length()==0) sTX_DATE_Start=Today;
			          	if(sTX_DATE_End.length()==0) sTX_DATE_End =Today;
			          	//UI進階查詢的日期 END
			          	//UI一般的日期 input box init date
			          	if(Util.objToStrTrim(request.getAttribute("TransDate")).length()==0) {
			          		request.setAttribute("TransDate", Today);
			          	}
			          	//UI一般的日期 END
			          		
			          	Hashtable userinfo=(Hashtable)session.getAttribute("SYSCONFDATA");
			          	Hashtable conf = (Hashtable)userinfo.get("SYSCONF");
			          	int queryMonth = Integer.parseInt(conf.get("MER_LIMIT_QRY_MONTH").toString());
			          	
			          	request.setAttribute("Start_TransDate", sTX_DATE_Start);
			        	request.setAttribute("End_TransDate", sTX_DATE_End);
			        	request.setAttribute("queryMonth", queryMonth);
			          	
			          	Forward = "./MerchantSignBillQuery.jsp";
			            String sAction = Util.objToStrTrim(request.getParameter("Action"));
			            request.setAttribute("Action", sAction);
			            /***
			             *	Action	
			             *	空白			直接forward 查詢畫面
			             * 	Query		進行細部查詢
			             * 	QryByDate	以日期及單一交易日期查詢不重複posID
			             * 	POS_ID		依QryByDate查詢結果，以posID、merchantID、單一交易日期查詢
			             * 
			             * ***/
			            if(sAction.length()==0) {

			            	//malb.get_AuthLog_View(SysBean, merchantid, submid, sysorderid, rowid);
//			            	ArrayList<Hashtable<String, String>> detail_log = new ArrayList<Hashtable<String, String>> ();
//			            	session.setAttribute("DetailLog", detail_log);
			            	
			            	request.getRequestDispatcher(Forward).forward(request, response);
			            	return;
			            }else if("QUERY".equals(sAction)) {
			            	/*取出查詢的條件資料
			            	 *特店代號	MERCHANT_ID
			            	 *交易日期	TX_DATE
			            	 *卡號		CARD_NO
			            	 *POS代號		TERMINAL_ID
			            	 *批次序號	BATCH_NO
			            	 *調閱編號	INVOICE_NO
			            	 *交易日期	TX_DATE
			            	 *交易金額	TX_AMOUNT
			            	 *授權碼		AUTH_CODE
			            	 */
//			            	String sMERCHANT_ID = Util.objToStrTrim(hashMerUser.get("MERCHANT_ID"));
			            	String sMERCHANT_ID =request.getParameter("merchantId");
			            			sTX_DATE_Start=request.getParameter("Start_TransDate");
			            			sTX_DATE_End=request.getParameter("End_TransDate");
			            	String sCARD_NO = request.getParameter("CARD_NO");
			            	String sMaskCardNo=getMaskCardNo(sCARD_NO);
			            	
			            	String sTERMINAL_ID = request.getParameter("TERMINAL_ID");
			            	String sBATCH_NO = request.getParameter("BATCH_NO");
			            	String sINVOICE_NO = request.getParameter("INVOICE_NO");
			            	//String sTX_DATE = Util.objToStrTrim(request.getParameter("TX_DATE"));
			            	String sTX_AMOUNT = request.getParameter("TX_AMOUNT");
			            	String sAUTH_CODE = request.getParameter("AUTH_CODE");
			            	
//			            	List<TErmLog> tErmlogGroup = signService.queryBySpec(sMERCHANT_ID, sTX_DATE_Start, sTX_DATE_End, sCARD_NO, sTERMINAL_ID, sBATCH_NO, sINVOICE_NO, sTX_AMOUNT, sAUTH_CODE);
			            	
			            	//分頁查詢：筆數預設10筆，頁數預設第1頁
			            	List<TErmLog> tErmlogGroup = signService.queryTErmLogBySpecAndPerPageNumAndSelectedPage(
			            			sMERCHANT_ID, sTX_DATE_Start, sTX_DATE_End, sCARD_NO, sTERMINAL_ID, sBATCH_NO, sINVOICE_NO, sTX_AMOUNT, sAUTH_CODE,"10","1");
			            	
			            	//查詢結果遮蔽卡號及加密電簽id
			            	tErmlogGroup=getMaskCardNoAndEncryptTransId(tErmlogGroup);
			            	
			            	//總頁數，預設每頁10筆
			            	int totalPageNum=signService.queryTErmLogTotalPageNumBySpec(
			            			sMERCHANT_ID, sTX_DATE_Start, sTX_DATE_End, sCARD_NO, sTERMINAL_ID, sBATCH_NO, sINVOICE_NO, sTX_AMOUNT, sAUTH_CODE,"10");
			            	
			            	
			            	request.setAttribute("Start_TransDate", sTX_DATE_Start);
			            	request.setAttribute("End_TransDate", sTX_DATE_End);
			            	request.setAttribute("CARD_NO", sCARD_NO);
			            	request.setAttribute("maskCardNo", sMaskCardNo);
			            	request.setAttribute("TERMINAL_ID", sTERMINAL_ID);
			            	request.setAttribute("BATCH_NO", sBATCH_NO);
			            	request.setAttribute("INVOICE_NO", sINVOICE_NO);
			            	request.setAttribute("TX_AMOUNT", sTX_AMOUNT);
			            	request.setAttribute("AUTH_CODE", sAUTH_CODE);
			            	
			            	//request.setAttribute("Action", sAction);
			            	request.setAttribute("MERCHANT_ID_choosen", sMERCHANT_ID);
			            	request.setAttribute("tErmlogGroup", tErmlogGroup);
			            	request.setAttribute("page_choosen", 1);//選擇的頁數，預設第一頁
			            	request.setAttribute("totalPageNum", totalPageNum);//總頁數
			                LogMemo = "查詢"+sMERCHANT_ID+"電子簽單資料";
			            	request.getRequestDispatcher(Forward).forward(request, response);
			            	return;
			            }else if("QryByDate".equals(sAction)) {
			            	//僅透過單一交易日期及特店代號查詢 查詢結果為不重複之posID
			            	System.out.println("QryByDate start!");
			            	String MERCHANT_ID =request.getParameter("merchantId");
			            	String TransDate = request.getParameter("TransDate");
			            	
			            	List<TErmLog> tErmlogGroup_DistinctPos=signService.queryTerminalIdByTxDate(TransDate, MERCHANT_ID);
			            	
			            	request.setAttribute("MERCHANT_ID_choosen", MERCHANT_ID);
			            	request.setAttribute("TransDate", TransDate);
			            	request.setAttribute("tErmlogGroup_DistinctPos",tErmlogGroup_DistinctPos);	
			            	//request.setAttribute("Action", sAction);
			            	request.getRequestDispatcher(Forward).forward(request, response);
			            	return;
			            }else if("POS_ID".equals(sAction)) {
			            	//透過不重複之posID、特店代號、單一交易日期查詢
			            	System.out.println("Qry by POS_ID & merchantId & TransDate !!!");
			            	String merchantId = request.getParameter("merchant_id");
			            	String procDate = request.getParameter("process_date");
			            	String terminalId = request.getParameter("terminal_id");
			            	
			            	List<TErmLog> tErmlogGroup_DistinctPos=signService.queryTerminalIdByTxDate(procDate, merchantId);
//			            	List<TErmLog> tErmlogGroup = signService.queryTErmLogByTxDateTerminalId(procDate, merchantId, terminalId);
			            	

			        	 	//分頁查詢：筆數預設10筆，頁數預設第1頁
			            	List<TErmLog> tErmlogGroup = signService.queryTErmLogByTxDateTerminalIdAndPerPageNumAndSelectedPage(procDate, merchantId, terminalId,"10","1");
			            	//查詢結果遮蔽卡號及加密電簽id
			            	tErmlogGroup=getMaskCardNoAndEncryptTransId(tErmlogGroup);
			            	
			            	//總頁數，預設每頁10筆
			            	int totalPageNum=signService.queryTErmLogTotalPageNumByTxDateTerminalId(procDate, merchantId, terminalId,"10");
			            	
			            	request.setAttribute("tErmlogGroup_DistinctPos",tErmlogGroup_DistinctPos);
			            	request.setAttribute("tErmlogGroup",tErmlogGroup);
			            	request.setAttribute("TransDate", procDate);
			            	request.setAttribute("terminalId", terminalId);
			            	request.setAttribute("merchantId_type_B", merchantId); //for ui input box
			            	request.setAttribute("MERCHANT_ID_choosen", merchantId); // for ui dropdown list
			            	request.setAttribute("page_choosen", 1);//選擇的頁數，預設第一頁
			            	request.setAttribute("totalPageNum", totalPageNum);//總頁數
			            	//request.setAttribute("Action", sAction);
			            	request.getRequestDispatcher(Forward).forward(request, response);
			            	return;
			            }else if("PerPageNum".equals(sAction)) {//切換查詢筆數
			            	System.out.println("change NumPerPage !!!");
			            	String perPageNum = request.getParameter("perPage__num");
			            	
			            	//判斷是posid查詢的表or進階查詢的表
			            	if(request.getParameter("process__date")!="" && request.getParameter("process__date")!=null) {
			            		String merchantId = request.getParameter("merchant__id");
				            	String procDate = request.getParameter("process__date");
				            	String terminalId = request.getParameter("terminal__id");
				            	List<TErmLog> tErmlogGroup_DistinctPos=signService.queryTerminalIdByTxDate(procDate, merchantId);
				            	
				            	//分頁查詢，切換筆數後預設show第1頁
				            	List<TErmLog> tErmlogGroup = signService.queryTErmLogByTxDateTerminalIdAndPerPageNumAndSelectedPage(procDate, merchantId, terminalId,perPageNum,"1");
				            	//查詢結果遮蔽卡號及加密電簽id
				            	tErmlogGroup=getMaskCardNoAndEncryptTransId(tErmlogGroup);
				            	
				            	//總頁數
				            	int totalPageNum=signService.queryTErmLogTotalPageNumByTxDateTerminalId(procDate, merchantId, terminalId,perPageNum);
				            	
				            	request.setAttribute("tErmlogGroup_DistinctPos", tErmlogGroup_DistinctPos);
				            	request.setAttribute("tErmlogGroup", tErmlogGroup);
				            	request.setAttribute("TransDate", procDate);
				            	request.setAttribute("terminalId", terminalId);
				            	request.setAttribute("merchantId_type_B", merchantId); //for ui input box
				            	request.setAttribute("MERCHANT_ID_choosen", merchantId); // for ui dropdown list
				            	request.setAttribute("perPageNum_choosen", perPageNum);//選擇的每頁筆數
				            	request.setAttribute("page_choosen", 1);//選擇的頁數，預設第一頁
				            	request.setAttribute("totalPageNum", totalPageNum);//總頁數
				            	//request.setAttribute("Action", sAction);
				            	request.getRequestDispatcher(Forward).forward(request, response);
				            	return;
			            	}else if(request.getParameter("process__date")=="" || request.getParameter("process__date")==null) {
			            		String sMERCHANT_ID =Util.objToStrTrim(request.getParameter("merchant__id"));
		            			sTX_DATE_Start=Util.objToStrTrim(request.getParameter("start__date"));
		            			sTX_DATE_End=Util.objToStrTrim(request.getParameter("end__date"));
				            	String sCARD_NO = Util.objToStrTrim(request.getParameter("card__no"));
				            	String sMaskCardNo=getMaskCardNo(sCARD_NO);
				            	
				            	String sTERMINAL_ID = Util.objToStrTrim(request.getParameter("terminal__id"));
				            	String sBATCH_NO = Util.objToStrTrim(request.getParameter("batch__no"));
				            	String sINVOICE_NO = Util.objToStrTrim(request.getParameter("ref__no"));
				            	String sTX_AMOUNT = Util.objToStrTrim(request.getParameter("tx__amount"));
				            	String sAUTH_CODE = Util.objToStrTrim(request.getParameter("auth__code"));
				            	
				            	
				            	//分頁查詢：頁數預設第1頁
				            	List<TErmLog> tErmlogGroup = signService.queryTErmLogBySpecAndPerPageNumAndSelectedPage(
				            			sMERCHANT_ID, sTX_DATE_Start, sTX_DATE_End, sCARD_NO, sTERMINAL_ID, sBATCH_NO, sINVOICE_NO, sTX_AMOUNT, sAUTH_CODE,perPageNum,"1");
				            	
				            	//查詢結果遮蔽卡號及加密電簽id
				            	tErmlogGroup=getMaskCardNoAndEncryptTransId(tErmlogGroup);
				            	
				            	//總頁數
				            	int totalPageNum=signService.queryTErmLogTotalPageNumBySpec(
				            			sMERCHANT_ID, sTX_DATE_Start, sTX_DATE_End, sCARD_NO, sTERMINAL_ID, sBATCH_NO, sINVOICE_NO, sTX_AMOUNT, sAUTH_CODE,perPageNum);
				            	
				            	
				            	request.setAttribute("Start_TransDate", sTX_DATE_Start);
				            	request.setAttribute("End_TransDate", sTX_DATE_End);
				            	request.setAttribute("CARD_NO", sCARD_NO);
				            	request.setAttribute("maskCardNo", sMaskCardNo);
				            	request.setAttribute("TERMINAL_ID", sTERMINAL_ID);
				            	request.setAttribute("BATCH_NO", sBATCH_NO);
				            	request.setAttribute("INVOICE_NO", sINVOICE_NO);
				            	request.setAttribute("TX_AMOUNT", sTX_AMOUNT);
				            	request.setAttribute("AUTH_CODE", sAUTH_CODE);
				            	
				            	request.setAttribute("tErmlogGroup", tErmlogGroup);
				            	request.setAttribute("merchantId_type_B", sMERCHANT_ID); //for ui input box
				            	request.setAttribute("MERCHANT_ID_choosen", sMERCHANT_ID); // for ui dropdown list
				            	request.setAttribute("perPageNum_choosen", perPageNum);//選擇的每頁筆數
				            	request.setAttribute("page_choosen", 1);//選擇的頁數，預設第一頁
				            	request.setAttribute("totalPageNum", totalPageNum);//總頁數
				            	//request.setAttribute("Action", sAction);
				            	request.getRequestDispatcher(Forward).forward(request, response);
				            	return;
			            	}
			            	System.out.println("change NumPerPage end !!!");
			            }else if("ChoosenPage".equals(sAction)) {//選擇查詢頁
			            	System.out.println("choosenPage !!!");
			            	String perPageNum = request.getParameter("perPage___num");
			            	String choosenPage = request.getParameter("choosenPage");
			            	String totalPageNum = request.getParameter("totalPageNum");
			            	
			            	//判斷是posid查詢的表or進階查詢的表
			            	if(request.getParameter("process___date")!="" && request.getParameter("process___date")!=null) {
			            		String merchantId = request.getParameter("merchant___id");
				            	String procDate = request.getParameter("process___date");
				            	String terminalId = request.getParameter("terminal___id");
				            	List<TErmLog> tErmlogGroup_DistinctPos=signService.queryTerminalIdByTxDate(procDate, merchantId);
				            	
				            	//分頁查詢
				            	List<TErmLog> tErmlogGroup = signService.queryTErmLogByTxDateTerminalIdAndPerPageNumAndSelectedPage(procDate, merchantId, terminalId,perPageNum,choosenPage);
				            	//查詢結果遮蔽卡號及加密電簽id
				            	tErmlogGroup=getMaskCardNoAndEncryptTransId(tErmlogGroup);
				            	
				            	request.setAttribute("tErmlogGroup_DistinctPos", tErmlogGroup_DistinctPos);
				            	request.setAttribute("tErmlogGroup", tErmlogGroup);
				            	request.setAttribute("TransDate", procDate);
				            	request.setAttribute("terminalId", terminalId);
				            	request.setAttribute("merchantId_type_B", merchantId); //for ui input box
				            	request.setAttribute("MERCHANT_ID_choosen", merchantId); // for ui dropdown list
				            	request.setAttribute("perPageNum_choosen", perPageNum);//選擇的每頁筆數
				            	request.setAttribute("page_choosen", Integer.parseInt(choosenPage));//選擇的頁數
				            	request.setAttribute("totalPageNum", totalPageNum);//總頁數
				            	//request.setAttribute("Action", sAction);
				            	request.getRequestDispatcher(Forward).forward(request, response);
				            	return;
			            	}else if(request.getParameter("process___date")=="" || request.getParameter("process___date")==null) {
			            		String sMERCHANT_ID =Util.objToStrTrim(request.getParameter("merchant___id"));
		            			sTX_DATE_Start=Util.objToStrTrim(request.getParameter("start___date"));
		            			sTX_DATE_End=Util.objToStrTrim(request.getParameter("end___date"));
				            	String sCARD_NO = Util.objToStrTrim(request.getParameter("card___no"));
				            	String sMaskCardNo=getMaskCardNo(sCARD_NO);
				            	
				            	String sTERMINAL_ID = Util.objToStrTrim(request.getParameter("terminal___id"));
				            	String sBATCH_NO = Util.objToStrTrim(request.getParameter("batch___no"));
				            	String sINVOICE_NO = Util.objToStrTrim(request.getParameter("ref___no"));
				            	String sTX_AMOUNT = Util.objToStrTrim(request.getParameter("tx___amount"));
				            	String sAUTH_CODE = Util.objToStrTrim(request.getParameter("auth___code"));
				            	
				            	//分頁查詢
				            	List<TErmLog> tErmlogGroup = signService.queryTErmLogBySpecAndPerPageNumAndSelectedPage(
				            			sMERCHANT_ID, sTX_DATE_Start, sTX_DATE_End, sCARD_NO, sTERMINAL_ID, sBATCH_NO, sINVOICE_NO, sTX_AMOUNT, sAUTH_CODE,perPageNum,choosenPage);
				            	//查詢結果遮蔽卡號及加密電簽id
				            	tErmlogGroup=getMaskCardNoAndEncryptTransId(tErmlogGroup);
				            	
				            	request.setAttribute("Start_TransDate", sTX_DATE_Start);
				            	request.setAttribute("End_TransDate", sTX_DATE_End);
				            	request.setAttribute("CARD_NO", sCARD_NO);
				            	request.setAttribute("maskCardNo", sMaskCardNo);
				            	request.setAttribute("TERMINAL_ID", sTERMINAL_ID);
				            	request.setAttribute("BATCH_NO", sBATCH_NO);
				            	request.setAttribute("INVOICE_NO", sINVOICE_NO);
				            	request.setAttribute("TX_AMOUNT", sTX_AMOUNT);
				            	request.setAttribute("AUTH_CODE", sAUTH_CODE);
				            	
				            	request.setAttribute("tErmlogGroup", tErmlogGroup);
				            	request.setAttribute("merchantId_type_B", sMERCHANT_ID); //for ui input box
				            	request.setAttribute("MERCHANT_ID_choosen", sMERCHANT_ID); // for ui dropdown list
				            	request.setAttribute("perPageNum_choosen", perPageNum);//選擇的每頁筆數
				            	request.setAttribute("page_choosen", choosenPage);//選擇的頁數
				            	request.setAttribute("totalPageNum", totalPageNum);//總頁數
				            	//request.setAttribute("Action", sAction);
				            	request.getRequestDispatcher(Forward).forward(request, response);
				            	return;
			            	}
			            }//查詢畫面變數賦值結束

					} else {
						Message = "特店無此功能權限";
						Forward = "./Merchant_Response.jsp";
						session.setAttribute("Message", Message);
					}
				} else {
					String CurrentCode = hashMerchant.get("CURRENTCODE").toString();
					Message = UserBean.get_CurrentCode(CurrentCode);
					Forward = "./Merchant_Response.jsp";
					session.setAttribute("Message", Message);
				}
			} else {
				Message = "使用者無此權限請洽系統管理者";
				Forward = "./Merchant_NoUse.jsp";
			}
			

			
			if (Message.length() > 0) {
				session.setAttribute("Message", Message);
				LogMemo = Message;
				LogUserName = hashMerUser.get("USER_ID").toString() + "(" + hashMerUser.get("USER_NAME").toString()
						+ ")";
				LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "失敗", LogMemo);
				log_user.debug(LogData);
			}

            //System.out.println("Forward=" + Forward);
			if (reportflag == null || !reportflag.equalsIgnoreCase("ture")) {
				if (errMessage.length() == 0)
					request.getRequestDispatcher(Forward).forward(request, response);
				else {
					Message = errMessage;
					Forward = "./Merchant_Response.jsp";
					session.setAttribute("Message", Message);
					request.getRequestDispatcher(Forward).forward(request, response);
				}
			}
        }catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("errMsg", e.toString());
			log_systeminfo.debug("--MerchantSignBillCtl--" + e.toString());
			request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
		}finally {// 20130703 Jason finally close SysBean DB connection
			try {
				sysBean.close();
			} catch (Exception e) {
			}
		}
    }

    private List<TErmLog> getMaskCardNoAndEncryptTransId(List<TErmLog> tErmlogGroup) {
    	for(int i=0; i<tErmlogGroup.size();++i){
    		TErmLog log = tErmlogGroup.get(i);
    		log.setCard_no(getMaskCardNo(log.getCard_no()));
    		log.setTrans_id(EnRSA.encrypt(log.getTrans_id()));
    		tErmlogGroup.set(i, log);
    	}
    	return tErmlogGroup;
	}

    /* ajax
     * 
     * SignController.java
     */
	/**
	 * 	雲端部SignController.java方法
	 * Fish，2018/12/06 
	 * 	功能說明： 電子簽單資訊
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception 
	 */
    
    SignService signService=new SignService();
    SignLogoService signLogoService=new SignLogoService();
    
    private Map<String,Object> signInfo(HttpServletRequest request) {
    	
		System.out.println("電子簽單資訊");
		Map<String,Object> mapR = new HashMap<String,Object>();
		String imageFile ="";
		String signImageFile ="";
		String callName="";
		TErmLog pojo = null;
		try{
			//tracklog(ActionItemEnum.SignSearch, EventCodeEnum.F040);
			String trans_id = EnRSA.decrypt(request.getParameter("trans_id"));
			pojo = signService.queryTErmLogByTransId(trans_id);
//			List<MerchantLogo> merchantLogolist = signLogoService.selectAllByStoreNoAndValidDate(pojo.getMerchant_id());
			TMerchants merchantPojo =null;
			
			merchantPojo =signService.queryByMerchantsId(pojo.getMerchant_id());
			callName =merchantPojo.getCallName();

			System.out.println("特店資料: "+callName);			
			//logger.info("特店資料: ",callName);
			mapR.put("merchantName",callName);
			
			imageFile = signLogoService.getMerchantLogoFile(merchantPojo.getHQ_MID());
			
			/*if(merchantLogolist!=null) {
				MerchantLogo logo =merchantLogolist.get(0);
				imageFile=logo.getLogoFile();
			}*/
			
			if(pojo != null){
			//	imageFile = signService.getMerchantLogo(pojo.getMerchant_id());
				String secretKey = signService.getDecodeSecretKey(pojo);
				byte[] imgBytes = signService.getSingImage(trans_id);
				
				int txAmount =pojo.getTx_amount().intValue();
				
				if(StringUtils.equals(pojo.getData_source(), "F")&& txAmount>=3000){
					signImageFile = signService.getDecodeSignImage(imgBytes, secretKey);
				}
				
				
				if(StringUtils.equals(pojo.getUnsign_flag(), "0")){
					signImageFile = signService.getDecodeSignImage(imgBytes, secretKey);
				}
				
//				//20190514 mask card_no
				String maskCardNo = pojo.getMaskCardNo(pojo.getCard_no());
				String maskCardNo2 = getMaskCardNo2(pojo.getCard_no());
				
				mapR.put("maskNo", maskCardNo2);				
				mapR.put("cardNo", pojo.getCard_no());
				pojo.setCard_no(maskCardNo);
				
			}
			
			mapR.put("data", pojo);
			mapR.put("imageFile",imageFile);
			mapR.put("signImageFile",signImageFile);
			
//			System.out.println("signImageFile: "+signImageFile);
//			System.out.println("imageFile: "+imageFile);
			
			return mapR;
		} 
		catch (Exception e) {
			//logger.error(e.getMessage(), e);
			e.printStackTrace();
			ExceptionUtils.getFullStackTrace(e);
			
			if(pojo != null){
				//20190514 mask card_no
				String maskCardNo = pojo.getMaskCardNo(pojo.getCard_no());
				String maskCardNo2 = getMaskCardNo2(pojo.getCard_no());
				
				mapR.put("maskNo", maskCardNo2);				
				mapR.put("cardNo", pojo.getCard_no());
				pojo.setCard_no(maskCardNo);
			}
			
			mapR.put("data", pojo);
			mapR.put("imageFile",imageFile);
			mapR.put("signImageFile",signImageFile);
			
			System.out.println("signImageFile: "+signImageFile);
			System.out.println("imageFile: "+imageFile);
			
			//logger.info("imageFile :{}  signImageFile:{}",imageFile,signImageFile);
			
			return mapR;
		}
	}

	/**
	 * 加密
	 * SignController.java
	 * @param card_no
	 * @return
	 * 
	 * ajax
	 */
	private String getMaskCardNo2(String card_no) {
		if (card_no.length() >= 10 && card_no != ""){
			return card_no.substring(6, 12);
		}
		return card_no;
	}
	
	private String getMaskCardNo(String card_no) {
		if (card_no != null && card_no.length() >= 10 && card_no != ""){
			int middle = card_no.length()-10;
			String middleStr = "";
			for(int i=0 ; i<middle ;i++){
				middleStr += "*" ;
			}
			return card_no.substring(0, 6)+middleStr+card_no.substring(card_no.length()-4, card_no.length());
	
		}
		return card_no;
	}
    
}
