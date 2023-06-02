/************************************************************
 * <p>#File Name:   MerchantPermitCodeCtl.java        </p>
 * <p>#Description:   �]�wpermitCode               </p>
 * <p>#Create Date: 2020/4/6              </p>
 * <p>#Company:     cybersoft               </p>
 * <p>#Notice:                      </p>
 * @author      dan
 * @since       SPEC version
 * @version 0.1 2020/4/6  dan
 * 2020/06/17 �ۥ� 202006110363-00 �S���޲z�����s�WPermitCode�]�w
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
//		// get sysdate �֥h15��
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
	private String Forward = ""; // ������}
	private String Message = ""; // ��ܰT��
	java.util.Date nowdate;
	public static final LogUtils log_user = new LogUtils("user");
	public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
	public UserBean UserBean = new UserBean();
	public DataBaseBean2 sysBean = new DataBaseBean2();
	public MerchantPermitCodeBean merchantPermitCodeBean = new MerchantPermitCodeBean();

	// DB���o�����container
	ArrayList<Hashtable<String, String>> permitCodeData = new ArrayList<>();

	// �e�ݨӪ����
	String UPD_OLDPERMITCODE = new String();
	String UPD_PERMITCODE = new String();
	String UPD_PERMITCODETWO = new String();
	String INS_PERMITCODE = new String();
	String INS_PERMITCODETWO = new String();

	String LogUserName = "";
	String LogFunctionName = "�]�wPermitCode";
	String LogStatus = "����";
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
			Hashtable hashSys = new Hashtable(); // �t�ΰѼ�
			Hashtable hashMerUser = new Hashtable(); // �S���ϥΪ�
			Hashtable hashMerchant = new Hashtable(); // �S���D��
			ArrayList arrayTerminal = new ArrayList(); // �ݥ����D��
			Hashtable hashConfData = (Hashtable) session.getAttribute("SYSCONFDATA");
			if (hashConfData == null)
				hashConfData = new Hashtable();

			boolean Merchant_Current = false; // �S�����A
			boolean Merchant_Permit = false; // �S���v��

			if (hashConfData.size() > 0) {
				hashSys = (Hashtable) hashConfData.get("SYSCONF"); // �t�ΰѼ�
				hashMerUser = (Hashtable) hashConfData.get("MERCHANT_USER"); // �S���ϥΪ�
				hashMerchant = (Hashtable) hashConfData.get("MERCHANT"); // �S���D��
				if (hashSys == null)
					hashSys = new Hashtable();

				if (hashMerUser == null)
					hashMerUser = new Hashtable();

				if (hashMerchant == null)
					hashMerchant = new Hashtable();

				if (hashMerchant.size() > 0) {
					LogMerchantID = (String) hashMerUser.get("MERCHANT_ID");
				}

				arrayTerminal = (ArrayList) hashConfData.get("TERMINAL"); // �ݥ����D��
				if (arrayTerminal == null)
					arrayTerminal = new ArrayList();
			}

			// UserBean UserBean = new UserBean();
			Merchant_Current = UserBean.check_Merchant_Column(hashMerchant, "CURRENTCODE", "B,C"); // �T�{�S�����A
			Merchant_Permit = UserBean.check_Merchant_Column(hashMerchant, "PERMIT_SALE", "Y"); // �T�{�S���v��
			String MenuKey = (String) request.getParameter(MerchantMenuCtl.MENU_FORWARD_KEY);
			request.setAttribute(MerchantMenuCtl.MENU_FORWARD_KEY, MenuKey);
			boolean User_Permit = MerchantMenuCtl.getUserRole(sysBean, session, MenuKey);

			if (User_Permit) {
				// �ϥΪ��v��
				if (Merchant_Current) {
					// �S�����A
					if (Merchant_Permit) {
						// �S���v��
						String Action = (request.getParameter("Action") == null) ? "" : request.getParameter("Action");
						if (Action.length() == 0) {
							// ���oMERCHANT_PERMIT_LIST���
							permitCodeData = merchantPermitCodeBean.getEnablePermitCodeList(LogMerchantID);
							if (permitCodeData != null) {
								List<MerchantPermitListEntity> entityList = new ArrayList<>();
								MerchantPermitListEntity entity;
								for (int index = 0; index < permitCodeData.size(); index++) {
									// �ݮ���Ҧ��æC�b�e��
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
							} else { // table �̭��S���ӵ����
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
							// update���
							if (!request.getParameter("UPD_PERMITCODETWO").isEmpty()) {
								System.out.println("UPD start");

							} else if (!request.getParameter("INS_PERMITCODETWO").isEmpty()) {
								System.out.println("INS start");
							}
						}
					} else {
						Message = "�S���L���\���v��";
						Forward = "./Merchant_Response.jsp";
						session.setAttribute("Message", Message);
						LogMemo = Message;
						LogUserName = hashMerUser.get("USER_NAME").toString();
						LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "����", LogMemo);
						log_user.debug(LogData);
					}
				} else {
					String CurrentCode = hashMerchant.get("CURRENTCODE").toString();
					Message = UserBean.get_CurrentCode(CurrentCode);
					Forward = "./Merchant_Response.jsp";
					session.setAttribute("Message", Message);
					LogMemo = Message;
					LogUserName = hashMerUser.get("USER_NAME").toString();
					LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "����", LogMemo);
					log_user.debug(LogData);

				}
			} else if (request.getParameter("UPD_PERMITCODETWO").length() != 0) { // ����s
				try {
					String merchantID = request.getParameter("merchantId");
					String newPermitCode = request.getParameter("UPD_PERMITCODETWO");
					String newUserid = request.getParameter("userName");
					// �쥻��permitCode�A�{�b�n���ק�

					// �T�w�̭��s�b��permitCode
					// ���i�P���e�]�p������
					ArrayList<Hashtable<String, String>> permitCodeList = new ArrayList<>();
					// �����ˮ�
//					permitCodeList = merchantPermitCodeBean.getEnablePermitCodeList(merchantID);
					// �ˮ֦��Ĵ��̤j��
					permitCodeList = merchantPermitCodeBean.getMaxExpiredPermitCodeList(merchantID);

					MerchantPermitListEntity merchantPermitListEntity;
					List<MerchantPermitListEntity> entityList = new ArrayList<>();
					entityList.clear();

					for (int index = 0; index < permitCodeList.size(); index++) {
						Hashtable<String, String> data = permitCodeList.get(index);

						// �˴����S����s��permitCode�@�ˡA�@�ˮɸ��X�åX�{���~�T���C
						if (newPermitCode.equals(data.get("PERMIT_CODE"))) {
							permitCodeList.clear();
							break;
						}
					}

					if (permitCodeList.size() <= 0) { // �p�GList�S����ƥN�� "�s�W��permitcode����"
						// �Q��k�b�e�ݸ��X���~�T��
						throw new Exception("permitCode���i���Ƴ]�w");
					} else {
						merchantPermitCodeBean.insertNewPermitCode(merchantID, newPermitCode, newUserid);
						// ���sselect�Ҧ����
						// ���oMERCHANT_PERMIT_LIST���
						permitCodeData = merchantPermitCodeBean.getEnablePermitCodeList(merchantID);
						if (permitCodeData != null) {
							List<MerchantPermitListEntity> newEntityList = new ArrayList<>();
							MerchantPermitListEntity entity2;
							for (int index = 0; index < permitCodeData.size(); index++) {
								// �ݮ���Ҧ��æC�b�e��
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

				} catch (Exception e) { // ���i�]�w���ƪ����
					Message = "PermitCode���i���Ƴ]�w";
					Forward = "./MerchantPermitCode.jsp";
					System.out.println("PermitCode���i���Ƴ]�w");
					session.setAttribute("Message", Message);
				}
			} else if (request.getParameter("INS_PERMITCODETWO").length() != 0) {
				try {
					String merchantID = request.getParameter("merchantId");
					String newPermitCode = request.getParameter("INS_PERMITCODETWO");
					String newUserid = request.getParameter("userName");
					
					// ���ݬO�_���s�b���
					permitCodeData = merchantPermitCodeBean.getEnablePermitCodeList(merchantID);
					if(permitCodeData.size() == 0) { // 1. MERCHANT_PERMIT_LIST �̭��L���(insert)
						merchantPermitCodeBean.insertNoExistsMerchantId(merchantID, newPermitCode, newUserid);
						// ���sselect�Ҧ����
						// ���oMERCHANT_PERMIT_LIST���
						permitCodeData = merchantPermitCodeBean.getEnablePermitCodeList(merchantID);
						if (permitCodeData != null) {
							List<MerchantPermitListEntity> newEntityList = new ArrayList<>();
							MerchantPermitListEntity entity2;
							for (int index = 0; index < permitCodeData.size(); index++) {
								// �ݮ���Ҧ��æC�b�e��
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
					else if(permitCodeData.size() > 0) {// 2. MERCHANT_PERMIT_LIST �̭�����ơA���OpermitCode�̭��S���]�w(update)
						merchantPermitCodeBean.updateExistsMerchantId(merchantID, newPermitCode, newUserid);
						// ���sselect�Ҧ����
						// ���oMERCHANT_PERMIT_LIST���
						permitCodeData = merchantPermitCodeBean.getEnablePermitCodeList(merchantID);
						if (permitCodeData != null) {
							List<MerchantPermitListEntity> newEntityList = new ArrayList<>();
							MerchantPermitListEntity entity2;
							for (int index = 0; index < permitCodeData.size(); index++) {
								// �ݮ���Ҧ��æC�b�e��
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
				Message = "�ϥΪ̵L���v���Ь��t�κ޲z��";
				Forward = "./Merchant_NoUse.jsp";
				session.setAttribute("Message", Message);
				LogMemo = Message;
				LogUserName = hashMerUser.get("USER_NAME").toString();
				LogData = UserBean.get_LogData(LogMerchantID, LogUserName, LogFunctionName, "����", LogMemo);
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
