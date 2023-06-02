/************************************************************
 * <p>#File Name:   MerchantPermitCodeCtl.java        </p>
 * <p>#Description:   設定permitCode               </p>
 * <p>#Create Date: 2020/4/6              </p>
 * <p>#Company:     cybersoft               </p>
 * <p>#Notice:                      </p>
 * @author      dan
 * @since       SPEC version
 * @version 0.1 2020/4/6  dan
 * 2020/06/17 彥仲 202006110363-00 特店管理介面新增PermitCode設定
 ************************************************************/
package com.cybersoft.merchant.ctl;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cybersoft4u.prj.tfbpg.api.SSLServer;
import com.cybersoft.merchant.bean.MerchantAuthParam;
import com.cybersoft.merchant.bean.MerchantPermitCodeBean;
import com.cybersoft.merchant.bean.MerchantAuthBean;
import com.cybersoft4u.prj.tfbpg.bean.ParamBean;
import com.cybersoft.bean.UserBean;
import com.cybersoft.entity.MerchantPermitListEntity;
/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft4u.util.CheckPan;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpSession;
import com.cybersoft.bean.SessionControlBean;
import com.cybersoft.bean.LogUtils;

public class MerchantPermitCodeCtl extends HttpServlet {
	// ============for Test============
//	public static void main(String[] args) {
//		// get sysdate 少去15天
//		Date dateNow = new Date();
//		DateFormat df = new SimpleDateFormat("yyyyMMdd");
//		Calendar minus15Date = Calendar.getInstance();
//		minus15Date.setTime(dateNow);
//		minus15Date.add(Calendar.DATE, -15);
//		Date dateMinus15Date = minus15Date.getTime();
//		System.out.println(df.format(dateMinus15Date));
//	}
	// =================================

	private static final String CONTENT_TYPE = "text/html; charset=Big5";
	private String Forward = ""; // 網頁轉址
	private String Message = ""; // 顯示訊息
	java.util.Date nowdate;
	public static final LogUtils log_user = new LogUtils("user");
	public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
	public UserBean UserBean = new UserBean();
	public DataBaseBean2 sysBean = new DataBaseBean2();
	public MerchantPermitCodeBean merchantPermitCodeBean = new MerchantPermitCodeBean();

	// DB取得之資料container
	ArrayList<Hashtable<String, String>> permitCodeData = new ArrayList<>();

	// 前端來的資料
	String UPD_OLDPERMITCODE = new String();
	String UPD_PERMITCODE = new String();
	String UPD_PERMITCODETWO = new String();
	String INS_PERMITCODE = new String();
	String INS_PERMITCODETWO = new String();

	String LogUserName = "";
	String LogFunctionName = "設定PermitCode";
	String LogStatus = "失敗";
	String LogMemo = "";
	String LogData = "";
	String LogMerchantID = "";

	/**
	 * @see javax.servlet.http.HttpServlet#void
	 *      (javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	/**
	 * @see javax.servlet.http.HttpServlet#void
	 *      (javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType(CONTENT_TYPE);
		request.setCharacterEncoding("BIG5");
		HttpSession session = request.getSession(true);
		SessionControlBean scb = new SessionControlBean();

		try {
			scb = new SessionControlBean(session, request, response);
			sysBean.setAutoCommit(false);
		} catch (UnsupportedOperationException E) {
			// 20130703 Jason when Exception close DB conn
			try {
				sysBean.close();
			} catch (Exception e) {
			}
			E.toString();
			request.getRequestDispatcher("/Merchant_Bye.jsp").forward(request, response);
			return;
		}

		try {
			Hashtable hashSys = new Hashtable(); // 系統參數
			Hashtable hashMerUser = new Hashtable(); // 特店使用者
			Hashtable hashMerchant = new Hashtable(); // 特店主檔
			ArrayList arrayTerminal = new ArrayList(); // 端末機主檔
			Hashtable hashConfData = (Hashtable) session.getAttribute("SYSCONFDATA");
			if (hashConfData == null)
				hashConfData = new Hashtable();

			boolean Merchant_Current = false; // 特店狀態
			boolean Merchant_Permit = false; // 特店權限

			if (hashConfData.size() > 0) {
				hashSys = (Hashtable) hashConfData.get("SYSCONF"); // 系統參數
				hashMerUser = (Hashtable) hashConfData.get("MERCHANT_USER"); // 特店使用者
				hashMerchant = (Hashtable) hashConfData.get("MERCHANT"); // 特店主檔
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
			Merchant_Current = UserBean.check_Merchant_Column(hashMerchant, "CURRENTCODE", "B,C"); // 確認特店狀態
			Merchant_Permit = UserBean.check_Merchant_Column(hashMerchant, "PERMIT_SALE", "Y"); // 確認特店權限
			String MenuKey = (String) request.getParameter(MerchantMenuCtl.MENU_FORWARD_KEY);
			request.setAttribute(MerchantMenuCtl.MENU_FORWARD_KEY, MenuKey);
			boolean User_Permit = MerchantMenuCtl.getUserRole(sysBean, session, MenuKey);

			if (User_Permit) {
				// 使用者權限
				if (Merchant_Current) {
					// 特店狀態
					if (Merchant_Permit) {
						// 特店權限
						String Action = (request.getParameter("Action") == null) ? "" : request.getParameter("Action");
						if (Action.length() == 0) {
							// 取得MERCHANT_PERMIT_LIST資料
							permitCodeData = merchantPermitCodeBean.getEnablePermitCodeList(LogMerchantID);
							if (permitCodeData != null) {
								List<MerchantPermitListEntity> entityList = new ArrayList<>();
								MerchantPermitListEntity entity;
								for (int index = 0; index < permitCodeData.size(); index++) {
									// 需拿到所有並列在前端
									Hashtable<String, String> tableValue = permitCodeData.get(index);
									entity = new MerchantPermitListEntity();
									entity.setMERCHANTID(String.valueOf(tableValue.get("MERCHANTID")));
									entity.setPERMIT_CODE(String.valueOf(tableValue.get("PERMIT_CODE")));
									entity.setEXPIRE_DATE(String.valueOf(tableValue.get("EXPIRE_DATE")));
									entity.setENABLE_FLAG(String.valueOf(tableValue.get("ENABLE_FLAG")));
									entity.setMODIFY_DATE(Timestamp.valueOf(tableValue.get("MODIFY_DATE")));
									entity.setUPDATE_USER(String.valueOf(tableValue.get("UPDATE_USER")));
									entityList.add(entity);
								}
								session.setAttribute("GETPERMITDATA", entityList);
							} else { // table 裡面沒有該筆資料
								session.setAttribute("GETPERMITDATA", "");
							}

							String Auth_Expire_year = (String) hashSys.get("AUTH_EXPIRE_YEAR");
							if (Auth_Expire_year.length() == 0 || Auth_Expire_year == null)
								Auth_Expire_year = "0";

							int YearCount = Integer.parseInt(Auth_Expire_year);
							ArrayList arrayExpireYear = new ArrayList();
							MerchantAuthBean hppbean = new MerchantAuthBean();
							String ExpireYear = hppbean.get_TransDate("yyyy");
							Forward = "./MerchantPermitCode.jsp";
						} else {
							// update資料
							if (!request.getParameter("UPD_PERMITCODETWO").isEmpty()) {
								System.out.println("UPD start");

							} else if (!request.getParameter("INS_PERMITCODETWO").isEmpty()) {
								System.out.println("INS start");
							}
						}
					} else {
						Message = "特店無此功能權限";
						Forward = "./Merchant_Response.jsp";
						session.setAttribute("Message", Message);
						LogMemo = Message;
						LogUserName = hashMerUser.get("USER_NAME").toString();
						LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "失敗", LogMemo);
						log_user.debug(LogData);
					}
				} else {
					String CurrentCode = hashMerchant.get("CURRENTCODE").toString();
					Message = UserBean.get_CurrentCode(CurrentCode);
					Forward = "./Merchant_Response.jsp";
					session.setAttribute("Message", Message);
					LogMemo = Message;
					LogUserName = hashMerUser.get("USER_NAME").toString();
					LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "失敗", LogMemo);
					log_user.debug(LogData);

				}
			} else if (request.getParameter("UPD_PERMITCODETWO").length() != 0) { // 做更新
				try {
					String merchantID = request.getParameter("merchantId");
					String newPermitCode = request.getParameter("UPD_PERMITCODETWO");
					String newUserid = request.getParameter("userName");
					// 原本有permitCode，現在要做修改

					// 確定裡面存在的permitCode
					// 不可與之前設計的重複
					ArrayList<Hashtable<String, String>> permitCodeList = new ArrayList<>();
					// 全部檢核
//					permitCodeList = merchantPermitCodeBean.getEnablePermitCodeList(merchantID);
					// 檢核有效期最大的
					permitCodeList = merchantPermitCodeBean.getMaxExpiredPermitCodeList(merchantID);

					MerchantPermitListEntity merchantPermitListEntity;
					List<MerchantPermitListEntity> entityList = new ArrayList<>();
					entityList.clear();

					for (int index = 0; index < permitCodeList.size(); index++) {
						Hashtable<String, String> data = permitCodeList.get(index);

						// 檢測有沒有跟新的permitCode一樣，一樣時跳出並出現錯誤訊息。
						if (newPermitCode.equals(data.get("PERMIT_CODE"))) {
							permitCodeList.clear();
							break;
						}
					}

					if (permitCodeList.size() <= 0) { // 如果List沒有資料代表 "新增的permitcode重複"
						// 想辦法在前端跳出錯誤訊息
						throw new Exception("permitCode不可重複設定");
					} else {
						merchantPermitCodeBean.insertNewPermitCode(merchantID, newPermitCode, newUserid);
						// 重新select所有資料
						// 取得MERCHANT_PERMIT_LIST資料
						permitCodeData = merchantPermitCodeBean.getEnablePermitCodeList(merchantID);
						if (permitCodeData != null) {
							List<MerchantPermitListEntity> newEntityList = new ArrayList<>();
							MerchantPermitListEntity entity2;
							for (int index = 0; index < permitCodeData.size(); index++) {
								// 需拿到所有並列在前端
								Hashtable<String, String> tableValue = permitCodeData.get(index);
								entity2 = new MerchantPermitListEntity();
								entity2.setMERCHANTID(String.valueOf(tableValue.get("MERCHANTID")));
								entity2.setPERMIT_CODE(String.valueOf(tableValue.get("PERMIT_CODE")));
								entity2.setEXPIRE_DATE(String.valueOf(tableValue.get("EXPIRE_DATE")));
								entity2.setENABLE_FLAG(String.valueOf(tableValue.get("ENABLE_FLAG")));
								entity2.setMODIFY_DATE(Timestamp.valueOf(tableValue.get("MODIFY_DATE")));
								entity2.setUPDATE_USER(String.valueOf(tableValue.get("UPDATE_USER")));
								newEntityList.add(entity2);
							}
							session.setAttribute("GETPERMITDATA", newEntityList);
						}
					}

				} catch (Exception e) { // 不可設定重複的資料
					Message = "PermitCode不可重複設定";
					Forward = "./MerchantPermitCode.jsp";
					System.out.println("PermitCode不可重複設定");
					session.setAttribute("Message", Message);
				}
			} else if (request.getParameter("INS_PERMITCODETWO").length() != 0) {
				try {
					String merchantID = request.getParameter("merchantId");
					String newPermitCode = request.getParameter("INS_PERMITCODETWO");
					String newUserid = request.getParameter("userName");
					
					// 先看是否有存在資料
					permitCodeData = merchantPermitCodeBean.getEnablePermitCodeList(merchantID);
					if(permitCodeData.size() == 0) { // 1. MERCHANT_PERMIT_LIST 裡面無資料(insert)
						merchantPermitCodeBean.insertNoExistsMerchantId(merchantID, newPermitCode, newUserid);
						// 重新select所有資料
						// 取得MERCHANT_PERMIT_LIST資料
						permitCodeData = merchantPermitCodeBean.getEnablePermitCodeList(merchantID);
						if (permitCodeData != null) {
							List<MerchantPermitListEntity> newEntityList = new ArrayList<>();
							MerchantPermitListEntity entity2;
							for (int index = 0; index < permitCodeData.size(); index++) {
								// 需拿到所有並列在前端
								Hashtable<String, String> tableValue = permitCodeData.get(index);
								entity2 = new MerchantPermitListEntity();
								entity2.setMERCHANTID(String.valueOf(tableValue.get("MERCHANTID")));
								entity2.setPERMIT_CODE(String.valueOf(tableValue.get("PERMIT_CODE")));
								entity2.setEXPIRE_DATE(String.valueOf(tableValue.get("EXPIRE_DATE")));
								entity2.setENABLE_FLAG(String.valueOf(tableValue.get("ENABLE_FLAG")));
								entity2.setMODIFY_DATE(Timestamp.valueOf(tableValue.get("MODIFY_DATE")));
								entity2.setUPDATE_USER(String.valueOf(tableValue.get("UPDATE_USER")));
								newEntityList.add(entity2);
							}
							session.setAttribute("GETPERMITDATA", newEntityList);
						}
					}
					else if(permitCodeData.size() > 0) {// 2. MERCHANT_PERMIT_LIST 裡面有資料，但是permitCode裡面沒有設定(update)
						merchantPermitCodeBean.updateExistsMerchantId(merchantID, newPermitCode, newUserid);
						// 重新select所有資料
						// 取得MERCHANT_PERMIT_LIST資料
						permitCodeData = merchantPermitCodeBean.getEnablePermitCodeList(merchantID);
						if (permitCodeData != null) {
							List<MerchantPermitListEntity> newEntityList = new ArrayList<>();
							MerchantPermitListEntity entity2;
							for (int index = 0; index < permitCodeData.size(); index++) {
								// 需拿到所有並列在前端
								Hashtable<String, String> tableValue = permitCodeData.get(index);
								entity2 = new MerchantPermitListEntity();
								entity2.setMERCHANTID(String.valueOf(tableValue.get("MERCHANTID")));
								entity2.setPERMIT_CODE(String.valueOf(tableValue.get("PERMIT_CODE")));
								entity2.setEXPIRE_DATE(String.valueOf(tableValue.get("EXPIRE_DATE")));
								entity2.setENABLE_FLAG(String.valueOf(tableValue.get("ENABLE_FLAG")));
								entity2.setMODIFY_DATE(Timestamp.valueOf(tableValue.get("MODIFY_DATE")));
								entity2.setUPDATE_USER(String.valueOf(tableValue.get("UPDATE_USER")));
								newEntityList.add(entity2);
							}
							session.setAttribute("GETPERMITDATA", newEntityList);
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			} else {
				Message = "使用者無此權限請洽系統管理者";
				Forward = "./Merchant_NoUse.jsp";
				session.setAttribute("Message", Message);
				LogMemo = Message;
				LogUserName = hashMerUser.get("USER_NAME").toString();
				LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "失敗", LogMemo);
				log_user.debug(LogData);
			}
			System.out.println("Forward=" + Forward);
			request.getRequestDispatcher(Forward).forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
			log_systeminfo.debug("--MerchantPermitCodeCtl--" + e.toString());
			request.getRequestDispatcher(Forward).forward(request, response);
		}
		// 20130703 Jason finally close SysBean DB connection
		finally {
			try {
				sysBean.close();
			} catch (Exception e) {
			}
		}
	}
}
