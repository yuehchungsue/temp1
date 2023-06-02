/**異動說明
 * 202208190762-00 20220902 Jeffery Cheng 新增
 */
package com.fubon.tp.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft.bean.emDataType;
import com.fubon.tp.model.pojo.MerchantgGroup;
import com.fubon.tp.model.pojo.TErmLog;
import com.fubon.tp.model.pojo.TSignImage;
import com.fubon.tp.secure.TPSecureUtilities;

//@Repository
public class SignDAO /*extends JdbcDaoSupport*/ {
	
	
//	@Autowired
	public SignDAO() {}
	public List<TErmLog> queryTerminalIdByTxDate2(String procDate, String merchantId){
		procDate = TPSecureUtilities.stripSQLInjection(procDate);
		merchantId = TPSecureUtilities.stripSQLInjection(merchantId);
		procDate=procDate.replace("/", "");
		DataBaseBean2 DBBean = new DataBaseBean2();
		List<TErmLog> list = new ArrayList<TErmLog>();
		StringBuilder sSQLSB = new StringBuilder();
		sSQLSB.append("SELECT DISTINCT TERMINAL_ID FROM ERM.T_ERM_LOG ");//DISTINCT TERMINAL_ID
		sSQLSB.append("WHERE MERCHANT_ID =?");
		sSQLSB.append(" AND TX_DATE =?");
		sSQLSB.append(" ORDER BY TERMINAL_ID ASC");
		System.out.println("交易日期查詢>查詢結果"+ sSQLSB.toString());
		DBBean.AddSQLParam(emDataType.STR, merchantId);
		DBBean.AddSQLParam(emDataType.STR, procDate);
		List<Map<String, Object>> objlist = DBBean.QuerySQLByParam2(sSQLSB.toString());
		for(int i = 0; i < objlist.size(); i++){
			Map<String, Object> map = objlist.get(i);
			TErmLog pojo = new TErmLog();
			pojo = tErmLogObj2Pojo(pojo, map);
			list.add(pojo);
		}
		
		return list;
	}
	
	/*20220726 改寫queryTErmLogByTransId()
	 *	參考MerchantSignBillBean.java		=>收單科專案的dao
	 */
	public TErmLog queryTErmLogByTransId2(String trans_id) {
		
		StringBuffer sSQLSB = new StringBuffer();
		DataBaseBean2 DBBean = new DataBaseBean2();
		DBBean.ClearSQLParam();
		sSQLSB.append("SELECT * FROM ERM.T_ERM_LOG WHERE TRANS_ID =?");
		DBBean.AddSQLParam(emDataType.STR, trans_id);
		List<Map<String, Object>> objlist = DBBean.QuerySQLByParam2(sSQLSB.toString());
		Map<String, Object> map=objlist.get(0);
		DBBean = null;
		System.out.println("ERM.T_ERM_LOG search count=" + map.size());
		TErmLog pojo = new TErmLog();
		pojo = tErmLogObj2Pojo(pojo, map);
		
		return pojo;
	}
	
	
	private TErmLog tErmLogObj2Pojo(TErmLog pojo, Map<String, Object> map){
		
		pojo.setTrans_id((String) map.get("TRANS_ID"));
		pojo.setTotal_packet_num((BigDecimal) map.get("TOTAL_PACKET_NUM"));
		pojo.setTotal_packet_len((BigDecimal) map.get("TOTAL_PACKET_LEN"));
		pojo.setCard_no((String) map.get("CARD_NO"));
		pojo.setTx_amount((BigDecimal) map.get("TX_AMOUNT"));
		pojo.setStan((String) map.get("STAN"));
		pojo.setTx_date((String) map.get("TX_DATE"));
		pojo.setTx_time((String) map.get("TX_TIME"));
		pojo.setExpire_date((String) map.get("EXPIRE_DATE"));
		pojo.setRef_no((String) map.get("REF_NO"));
		pojo.setAuth_code((String) map.get("AUTH_CODE"));
		pojo.setTerminal_id((String) map.get("TERMINAL_ID"));
		pojo.setMerchant_id((String) map.get("MERCHANT_ID"));
		pojo.setStore_id(map.get("STORE_ID") == null || " ".equals(map.get("STORE_ID")) ? "" : (String) map.get("STORE_ID"));
		pojo.setTip_amount(map.get("TIP_AMOUNT") == null || " ".equals(map.get("TIP_AMOUNT")) ? BigDecimal.valueOf(0) : (BigDecimal) map.get("TIP_AMOUNT"));
		pojo.setBatch_no((String) map.get("BATCH_NO"));
		pojo.setInvoice_no(map.get("INVOICE_NO") == null || " ".equals(map.get("INVOICE_NO")) ? "" : (String) map.get("INVOICE_NO"));
		pojo.setInst_period(map.get("INST_PERIOD") == null || " ".equals(map.get("INST_PERIOD")) ? "" : (String) map.get("INST_PERIOD"));
		pojo.setInst_down_payment(map.get("INST_DOWN_PAYMENT") == null || " ".equals(map.get("INST_DOWN_PAYMENT")) ? BigDecimal.valueOf(0) : (BigDecimal)map.get("INST_DOWN_PAYMENT"));
		pojo.setInst_payment(map.get("INST_PAYMENT") == null || " ".equals(map.get("INST_PAYMENT")) ? BigDecimal.valueOf(0) : (BigDecimal) map.get("INST_PAYMENT"));
		pojo.setInst_fee(map.get("INST_FEE") == null || " ".equals(map.get("INST_FEE")) ? BigDecimal.valueOf(0) : (BigDecimal) map.get("INST_FEE"));
		pojo.setRedeem_paid_amount(map.get("REDEEM_PAID_AMOUNT") == null || " ".equals(map.get("REDEEM_PAID_AMOUNT")) ? BigDecimal.valueOf(0) : (BigDecimal) map.get("REDEEM_PAID_AMOUNT"));
		pojo.setRedeem_amount(map.get("REDEEM_AMOUNT") == null || " ".equals(map.get("REDEEM_AMOUNT")) ? BigDecimal.valueOf(0) : (BigDecimal) map.get("REDEEM_AMOUNT"));
		pojo.setRedeem_point(map.get("REDEEM_POINT") == null || " ".equals(map.get("REDEEM_POINT")) ? BigDecimal.valueOf(0) : (BigDecimal) map.get("REDEEM_POINT"));
		pojo.setRedeem_balance_point(map.get("REDEEM_BALANCE_POINT") == null || " ".equals(map.get("REDEEM_BALANCE_POINT")) ? BigDecimal.valueOf(0) : (BigDecimal) map.get("REDEEM_BALANCE_POINT"));
		pojo.setCard_type((String) map.get("CARD_TYPE"));
		pojo.setTrans_mode((String) map.get("TRANS_MODE"));
		pojo.setTrans_type((String) map.get("TRANS_TYPE"));
		pojo.setUnsign_flag((String) map.get("UNSIGN_FLAG"));
		pojo.setCheck_no(map.get("CHECK_NO") == null || " ".equals(map.get("CHECK_NO")) ? "" : (String) map.get("CHECK_NO"));
		pojo.setCup_stan(map.get("CUP_STAN") == null || " ".equals(map.get("CUP_STAN")) ? "" : (String) map.get("CUP_STAN"));
		pojo.setEmv_tc(map.get("EMV_TC") == null || " ".equals(map.get("EMV_TC")) ? "" : (String) map.get("EMV_TC"));
		pojo.setCardholder_name(map.get("CARDHOLDER_NAME") == null || " ".equals(map.get("CARDHOLDER_NAME")) ? "" : (String) map.get("CARDHOLDER_NAME"));
		pojo.setChk_err_flag((String) map.get("CHK_ERR_FLAG"));
		pojo.setLog_date((Date) map.get("LOG_DATE"));
		pojo.setData_source((String)map.get("DATA_SOURCE"));
		
		pojo.setSignInfoType(pojo.getSignInfoType());
		pojo.setTx_date_time_show(pojo.getTx_date_time_show());
		pojo.setTrans_type_show_cht(pojo.getTrans_type_show_cht());
		pojo.setTrans_type_show_eng(pojo.getTrans_type_show_eng());
		pojo.setCard_type_show(pojo.getCard_type_show(pojo.getCard_type()));
		pojo.setTip_amount_show(pojo.getTip_amount_show());
		pojo.setTx_amount_show(pojo.getTx_amount_show());
		
		return pojo;
	}
	
	private TSignImage tSignImageObj2Pojo(TSignImage pojo, Map<String, Object> map){
		
//		pojo.setTrans_id((String) map.get("TRANS_ID"));
		pojo.setPacket_num((BigDecimal) map.get("PACKET_NUM"));
//		pojo.setPacket_len((BigDecimal) map.get("PACKET_LEN"));
		pojo.setImage((String) map.get("IMAGE"));
//		pojo.setChk_err_flag((String) map.get("CHK_ERR_FLAG"));
//		pojo.setLog_date((Date) map.get("LOG_DATE"));
		
		return pojo;
	}
	

	public List<TErmLog> queryBySpec2(String merchant_id, String process_date_start, String process_date_end, String card_no, String terminal_id, String batch_no, String invoice_no, String tx_amount, String auth_code) {
		merchant_id = TPSecureUtilities.stripSQLInjection(merchant_id);
		process_date_start = TPSecureUtilities.stripSQLInjection(process_date_start);
		process_date_end = TPSecureUtilities.stripSQLInjection( process_date_end);
		process_date_start = process_date_start.replaceAll("/", "");
		process_date_end = process_date_end.replaceAll("/", "");
		
		card_no = TPSecureUtilities.stripSQLInjection( card_no);
		terminal_id = TPSecureUtilities.stripSQLInjection( terminal_id);
		batch_no = TPSecureUtilities.stripSQLInjection( batch_no);
		invoice_no = TPSecureUtilities.stripSQLInjection( invoice_no);
		tx_amount = TPSecureUtilities.stripSQLInjection( tx_amount);
		auth_code = TPSecureUtilities.stripSQLInjection( auth_code);
		List<TErmLog> list = new ArrayList<TErmLog>();
		StringBuffer sSQLSB = new StringBuffer();
		DataBaseBean2 DBBean = new DataBaseBean2();
		
		sSQLSB.append("Select * From ERM.T_ERM_LOG WHERE MERCHANT_ID=? AND (TX_DATE >=? AND TX_DATE <=?)");
		
		DBBean.AddSQLParam(emDataType.STR, merchant_id);
		DBBean.AddSQLParam(emDataType.STR, process_date_start);
		DBBean.AddSQLParam(emDataType.STR, process_date_end);
		
		if (card_no.length() > 0) {
			sSQLSB.append(" AND CARD_NO=?");
			DBBean.AddSQLParam(emDataType.STR, card_no);
		}
		if (terminal_id.length() > 0) {
			sSQLSB.append(" AND TERMINAL_ID=?");
			DBBean.AddSQLParam(emDataType.STR, terminal_id);
		}
		if (batch_no.length() > 0) {
			sSQLSB.append(" AND BATCH_NO=?");
			DBBean.AddSQLParam(emDataType.STR, batch_no);
		}
		if (invoice_no.length() > 0) {
			sSQLSB.append(" AND INVOICE_NO=?");
			DBBean.AddSQLParam(emDataType.STR, invoice_no);
		}
		if (tx_amount.length() > 0) {
			sSQLSB.append(" AND TX_AMOUNT=?");
			DBBean.AddSQLParam(emDataType.DOUBLE, tx_amount);
		}
		if (auth_code.length() > 0) {
			sSQLSB.append(" AND AUTH_CODE=?");
			DBBean.AddSQLParam(emDataType.STR, auth_code);
		}
		
		//sb.append("ORDER BY TX_DATE ASC");
		
		//20190715 ORDER BY 交易日期>POS代號>批次號碼>調閱編號>卡號>授權碼
		sSQLSB.append("ORDER BY TX_DATE ASC,TERMINAL_ID ASC,BATCH_NO ASC,INVOICE_NO ASC,CARD_NO ASC,AUTH_CODE ASC ");

		System.out.println("特定條件查詢> POS代號明細"+ sSQLSB.toString());
		List<Map<String, Object>> objlist = DBBean.QuerySQLByParam2(sSQLSB.toString());
		for(int i = 0; i < objlist.size(); i++){
			Map<String, Object> map = objlist.get(i);
			TErmLog pojo = new TErmLog();
			pojo = tErmLogObj2Pojo(pojo, map);
			list.add(pojo);
		}
		return list;
	}
	
	public List<TErmLog> queryTErmLogBySpecAndPerPageNumAndSelectedPage(String sMERCHANT_ID, String sTX_DATE_Start,
			String sTX_DATE_End, String sCARD_NO, String sTERMINAL_ID, String sBATCH_NO, String sINVOICE_NO,
			String sTX_AMOUNT, String sAUTH_CODE, String perPageNum, String selectedPage) {
		perPageNum = TPSecureUtilities.stripSQLInjection(perPageNum);
		if("all".equals(perPageNum)) {
			return queryBySpec2(sMERCHANT_ID, sTX_DATE_Start, sTX_DATE_End, sCARD_NO, sTERMINAL_ID, sBATCH_NO, sINVOICE_NO, sTX_AMOUNT, sAUTH_CODE);
		}else {
			sTX_DATE_Start = TPSecureUtilities.stripSQLInjection(sTX_DATE_Start);
			sTX_DATE_End = TPSecureUtilities.stripSQLInjection(sTX_DATE_End);
			sTX_DATE_Start = sTX_DATE_Start.replaceAll("/", "");
			sTX_DATE_End = sTX_DATE_End.replaceAll("/", "");
			sMERCHANT_ID = TPSecureUtilities.stripSQLInjection(sMERCHANT_ID);
			sTERMINAL_ID = TPSecureUtilities.stripSQLInjection(sTERMINAL_ID);
			sCARD_NO = TPSecureUtilities.stripSQLInjection( sCARD_NO);
			sBATCH_NO = TPSecureUtilities.stripSQLInjection( sBATCH_NO);
			sINVOICE_NO = TPSecureUtilities.stripSQLInjection( sINVOICE_NO);
			sTX_AMOUNT = TPSecureUtilities.stripSQLInjection( sTX_AMOUNT);
			sAUTH_CODE = TPSecureUtilities.stripSQLInjection( sAUTH_CODE);
			selectedPage = TPSecureUtilities.stripSQLInjection( selectedPage);
			
			List<TErmLog> list = new ArrayList<TErmLog>();
			StringBuffer sSQLSB = new StringBuffer();
			DataBaseBean2 DBBean = new DataBaseBean2();
			
			sSQLSB.append("SELECT * FROM( ");
			sSQLSB.append("SELECT A.*, ROWNUM MINROWNUM ");
			sSQLSB.append("FROM (SELECT * FROM ERM.T_ERM_LOG WHERE MERCHANT_ID =? AND (TX_DATE >=? AND TX_DATE <=?) ");
			
			DBBean.AddSQLParam(emDataType.STR, sMERCHANT_ID);
			DBBean.AddSQLParam(emDataType.STR, sTX_DATE_Start);
			DBBean.AddSQLParam(emDataType.STR, sTX_DATE_End);
			if (sCARD_NO.length() > 0) {
				sSQLSB.append(" AND CARD_NO=?");
				DBBean.AddSQLParam(emDataType.STR, sCARD_NO);
			}
			if (sTERMINAL_ID.length() > 0) {
				sSQLSB.append(" AND TERMINAL_ID=?");
				DBBean.AddSQLParam(emDataType.STR, sTERMINAL_ID);
			}
			if (sBATCH_NO.length() > 0) {
				sSQLSB.append(" AND BATCH_NO=?");
				DBBean.AddSQLParam(emDataType.STR, sBATCH_NO);
			}
			if (sINVOICE_NO.length() > 0) {
				sSQLSB.append(" AND INVOICE_NO=?");
				DBBean.AddSQLParam(emDataType.STR, sINVOICE_NO);
			}
			if (sTX_AMOUNT.length() > 0) {
				sSQLSB.append(" AND TX_AMOUNT=?");
				DBBean.AddSQLParam(emDataType.DOUBLE, sTX_AMOUNT);
			}
			if (sAUTH_CODE.length() > 0) {
				sSQLSB.append(" AND AUTH_CODE=?");
				DBBean.AddSQLParam(emDataType.STR, sAUTH_CODE);
			}
			
			sSQLSB.append("AND ROWNUM <= ?*?");
			sSQLSB.append("ORDER BY TX_DATE ASC,TERMINAL_ID ASC,BATCH_NO ASC,INVOICE_NO ASC,CARD_NO ASC,AUTH_CODE ASC ) A");
			sSQLSB.append(")WHERE MINROWNUM >=1+?*(?-1) ");
			
			DBBean.AddSQLParam(emDataType.INT, Integer.parseInt(perPageNum));
			DBBean.AddSQLParam(emDataType.INT, Integer.parseInt(selectedPage));
			DBBean.AddSQLParam(emDataType.INT, Integer.parseInt(perPageNum));
			DBBean.AddSQLParam(emDataType.INT, Integer.parseInt(selectedPage));
			
			sSQLSB.append("ORDER BY TX_DATE ASC,TERMINAL_ID ASC,BATCH_NO ASC,INVOICE_NO ASC,CARD_NO ASC,AUTH_CODE ASC ");
			
			System.out.println("特定條件查詢>查詢結果>"+ sSQLSB.toString());
			List<Map<String, Object>> objlist = DBBean.QuerySQLByParam2(sSQLSB.toString());
			for(int i = 0; i < objlist.size(); i++){
				Map<String, Object> map = objlist.get(i);
				TErmLog pojo = new TErmLog();
				pojo = tErmLogObj2Pojo(pojo, map);
				list.add(pojo);
			}
			return list;
		}
	}
	
	public int queryTErmLogTotalPageNumBySpec(String sMERCHANT_ID, String sTX_DATE_Start, String sTX_DATE_End,
			String sCARD_NO, String sTERMINAL_ID, String sBATCH_NO, String sINVOICE_NO, String sTX_AMOUNT,
			String sAUTH_CODE, String perPageNum) {
		if("all".equals(perPageNum)) {
			return 1;
		}else {
			sTX_DATE_Start = TPSecureUtilities.stripSQLInjection(sTX_DATE_Start);
			sTX_DATE_End = TPSecureUtilities.stripSQLInjection(sTX_DATE_End);
			sTX_DATE_Start = sTX_DATE_Start.replaceAll("/", "");
			sTX_DATE_End = sTX_DATE_End.replaceAll("/", "");
			sMERCHANT_ID = TPSecureUtilities.stripSQLInjection(sMERCHANT_ID);
			sTERMINAL_ID = TPSecureUtilities.stripSQLInjection(sTERMINAL_ID);
			sCARD_NO = TPSecureUtilities.stripSQLInjection( sCARD_NO);
			sBATCH_NO = TPSecureUtilities.stripSQLInjection( sBATCH_NO);
			sINVOICE_NO = TPSecureUtilities.stripSQLInjection( sINVOICE_NO);
			sTX_AMOUNT = TPSecureUtilities.stripSQLInjection( sTX_AMOUNT);
			sAUTH_CODE = TPSecureUtilities.stripSQLInjection( sAUTH_CODE);
			
			
			StringBuffer sSQLSB = new StringBuffer();
			DataBaseBean2 DBBean = new DataBaseBean2();
			// 總頁數：總數/每頁筆數，無條件進位
			sSQLSB.append("SELECT CEIL(COUNT(*)/?) AS TOTALPAGENUM FROM( ");
			sSQLSB.append("SELECT * FROM ERM.T_ERM_LOG WHERE MERCHANT_ID=? AND (TX_DATE >=? AND TX_DATE <=?) ");
			
			DBBean.AddSQLParam(emDataType.INT, Integer.parseInt(perPageNum));
			DBBean.AddSQLParam(emDataType.STR, sMERCHANT_ID);
			DBBean.AddSQLParam(emDataType.STR, sTX_DATE_Start);
			DBBean.AddSQLParam(emDataType.STR, sTX_DATE_End);
			
			if (sCARD_NO.length() > 0) {
				sSQLSB.append(" AND CARD_NO=?");
				DBBean.AddSQLParam(emDataType.STR, sCARD_NO);
			}
			if (sTERMINAL_ID.length() > 0) {
				sSQLSB.append(" AND TERMINAL_ID=?");
				DBBean.AddSQLParam(emDataType.STR, sTERMINAL_ID);
			}
			if (sBATCH_NO.length() > 0) {
				sSQLSB.append(" AND BATCH_NO=?");
				DBBean.AddSQLParam(emDataType.STR, sBATCH_NO);
			}
			if (sINVOICE_NO.length() > 0) {
				sSQLSB.append(" AND INVOICE_NO=?");
				DBBean.AddSQLParam(emDataType.STR, sINVOICE_NO);
			}
			if (sTX_AMOUNT.length() > 0) {
				sSQLSB.append(" AND TX_AMOUNT=?");
				DBBean.AddSQLParam(emDataType.DOUBLE, sTX_AMOUNT);
			}
			if (sAUTH_CODE.length() > 0) {
				sSQLSB.append(" AND AUTH_CODE=?");
				DBBean.AddSQLParam(emDataType.STR, sAUTH_CODE);
			}
			
			sSQLSB.append(" )ORDER BY TX_DATE ASC,TERMINAL_ID ASC,BATCH_NO ASC,INVOICE_NO ASC,CARD_NO ASC,AUTH_CODE ASC ");
			System.out.println("總頁數查詢"+ sSQLSB.toString());
			
			List<Map<String, Object>> objlist = DBBean.QuerySQLByParam2(sSQLSB.toString());
			Map<String, Object> map = objlist.get(0);
			int totalPageNum=((BigDecimal)map.get("TOTALPAGENUM")).intValue();
			return totalPageNum;
		}
	}
	
	
	/*
	public List<TSignImage> getSingImage(String transId) {
		
		transId = TPSecureUtilities.stripSQLInjection(transId);
		List<TSignImage> list = new ArrayList<TSignImage>();
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT DISTINCT PACKET_NUM ,RAWTOHEX(IMAGE) AS IMAGE FROM ERM.T_SIGN_IMAGE ");
		sb.append("WHERE TRANS_ID = '" + transId + "' ");
		sb.append("ORDER BY PACKET_NUM ASC");
		System.out.println("個人簽名檔>查詢結果"+ sb.toString());
		List<Map<String, Object>> objlist = this.getJdbcTemplate().queryForList(sb.toString());
		
		
		for(int i = 0; i < objlist.size(); i++){
			Map<String, Object> map = objlist.get(i);
			TSignImage pojo = new TSignImage();
			pojo = tSignImageObj2Pojo(pojo, map);
			list.add(pojo);
		}
		
		System.out.println("objlist size:"+ objlist.size());
		
		return list;
	}
	*/

	/*20220726 改寫getSingImage()
	 *	參考MerchantSignBillBean.java
	 */
	public List<TSignImage> getSingImage2(String transId) {
		transId = TPSecureUtilities.stripSQLInjection(transId);
		List<TSignImage> list = new ArrayList<TSignImage>();
		StringBuffer sSQLSB = new StringBuffer();
		DataBaseBean2 DBBean = new DataBaseBean2();
		DBBean.ClearSQLParam();
		sSQLSB.append("SELECT DISTINCT PACKET_NUM ,RAWTOHEX(IMAGE) AS IMAGE FROM ERM.T_SIGN_IMAGE"
				+ " WHERE TRANS_ID =?"
				+ " ORDER BY PACKET_NUM ASC");
		DBBean.AddSQLParam(emDataType.STR, transId);
		List<Map<String, Object>> objlist = DBBean.QuerySQLByParam2(sSQLSB.toString());
		
		for(int i = 0; i < objlist.size(); i++){
			Map<String, Object> map = objlist.get(i);
			TSignImage pojo = new TSignImage();
			pojo = tSignImageObj2Pojo(pojo, map);
			list.add(pojo);
		}
		
		Map<String, Object> map=objlist.get(0);
		DBBean = null;
		System.out.println("ERM.T_ERM_LOG search count=" + map.size());
		TSignImage pojo = new TSignImage();
		pojo = tSignImageObj2Pojo(pojo, map);
		System.out.println("objlist size:"+ objlist.size());
		
		return list;
		
	}
//	
//	
//	/**
//	 * @param procDate
//	 * @param merchantId
//	 * @return
//	 */
//	public List<TErmLog> queryTerminalIdByTxDateByParent(String procDate, String merchantId){
//		procDate = TPSecureUtilities.stripSQLInjection(procDate);
//		merchantId = TPSecureUtilities.stripSQLInjection(merchantId);
//		List<TErmLog> list = new ArrayList<TErmLog>();
//		StringBuilder sb = new StringBuilder();
//		sb.append("SELECT DISTINCT TERMINAL_ID FROM ERM.T_ERM_LOG ");
//		sb.append("WHERE MERCHANT_ID IN (" + merchantId + ") ");
//		sb.append("AND TX_DATE = '" + procDate + "' ");
//		sb.append("ORDER BY TERMINAL_ID ASC");
//		List<Map<String, Object>> objlist = this.getJdbcTemplate().queryForList(sb.toString());
//		for(int i = 0; i < objlist.size(); i++){
//			Map<String, Object> map = objlist.get(i);
//			TErmLog pojo = new TErmLog();
//			pojo = tErmLogObj2Pojo(pojo, map);
//			list.add(pojo);
//		}
//		return list;
//	}
//	
//	public List<TErmLog> queryTErmLogByTxDateTerminalIdByParent(String procDate, String merchantId, String terminalId ){
//		procDate = TPSecureUtilities.stripSQLInjection(procDate);
//		merchantId = TPSecureUtilities.stripSQLInjection(merchantId);
//		terminalId = TPSecureUtilities.stripSQLInjection(terminalId);
//		List<TErmLog> list = new ArrayList<TErmLog>();
//		StringBuilder sb = new StringBuilder();
//		sb.append("SELECT * FROM ERM.T_ERM_LOG ");
//		sb.append("WHERE MERCHANT_ID IN (" + merchantId + ") ");
//		sb.append("AND TX_DATE = '" + procDate + "' ");
//		sb.append("AND TERMINAL_ID = '" + terminalId + "' ");
////		sb.append("ORDER BY TERMINAL_ID ASC");
//		List<Map<String, Object>> objlist = this.getJdbcTemplate().queryForList(sb.toString());
//		for(int i = 0; i < objlist.size(); i++){
//			Map<String, Object> map = objlist.get(i);
//			TErmLog pojo = new TErmLog();
//			pojo = tErmLogObj2Pojo(pojo, map);
//			list.add(pojo);
//		}
//		return list;
//	}
	
	public List<TErmLog> queryTErmLogByTxDateTerminalId2(String procDate, String merchantId, String terminalId) {
		procDate = TPSecureUtilities.stripSQLInjection(procDate);
		merchantId = TPSecureUtilities.stripSQLInjection(merchantId);
		terminalId = TPSecureUtilities.stripSQLInjection(terminalId);
		procDate = procDate.replaceAll("/", "");
		
		List<TErmLog> list = new ArrayList<TErmLog>();
		StringBuffer sSQLSB = new StringBuffer();
		DataBaseBean2 DBBean = new DataBaseBean2();
		sSQLSB.append("SELECT * FROM ERM.T_ERM_LOG ");
		sSQLSB.append("WHERE MERCHANT_ID =? ");
		sSQLSB.append("AND TX_DATE =? ");
		sSQLSB.append("AND TERMINAL_ID =? ");
//		sb.append("ORDER BY TERMINAL_ID ASC");
		DBBean.AddSQLParam(emDataType.STR, merchantId);
		DBBean.AddSQLParam(emDataType.STR, procDate);
		DBBean.AddSQLParam(emDataType.STR, terminalId);
		
		//20190715 ORDER BY 交易日期>POS代號>批次號碼>調閱編號>卡號>授權碼
		sSQLSB.append("ORDER BY TX_DATE ASC,TERMINAL_ID ASC,BATCH_NO ASC,INVOICE_NO ASC,CARD_NO ASC,AUTH_CODE ASC ");
		
		System.out.println("交易日期查詢>查詢結果>POS代號明細"+ sSQLSB.toString());
		List<Map<String, Object>> objlist = DBBean.QuerySQLByParam2(sSQLSB.toString());
		for(int i = 0; i < objlist.size(); i++){
			Map<String, Object> map = objlist.get(i);
			TErmLog pojo = new TErmLog();
			pojo = tErmLogObj2Pojo(pojo, map);
			list.add(pojo);
		}
		return list;
	}
	
	public List<TErmLog> queryTErmLogByTxDateTerminalIdAndPerPageNumAndSelectedPage(String procDate, String merchantId,
			String terminalId, String perPageNum,String selectedPage) {
		perPageNum = TPSecureUtilities.stripSQLInjection(perPageNum);
		if("all".equals(perPageNum)) {
			return queryTErmLogByTxDateTerminalId2(procDate,merchantId,terminalId);
		}else {
			procDate = TPSecureUtilities.stripSQLInjection(procDate);
			procDate = procDate.replaceAll("/", "");
			merchantId = TPSecureUtilities.stripSQLInjection(merchantId);
			terminalId = TPSecureUtilities.stripSQLInjection(terminalId);
			selectedPage = TPSecureUtilities.stripSQLInjection(selectedPage);
			List<TErmLog> list = new ArrayList<TErmLog>();
			StringBuffer sSQLSB = new StringBuffer();
			DataBaseBean2 DBBean = new DataBaseBean2();
			sSQLSB.append("SELECT * FROM( ");
			sSQLSB.append("SELECT A.*, ROWNUM MINROWNUM ");
			sSQLSB.append("FROM (SELECT * FROM ERM.T_ERM_LOG WHERE MERCHANT_ID =? AND TX_DATE =? AND TERMINAL_ID =? AND ROWNUM <= ?*?");
			sSQLSB.append("ORDER BY TX_DATE ASC,TERMINAL_ID ASC,BATCH_NO ASC,INVOICE_NO ASC,CARD_NO ASC,AUTH_CODE ASC ) A");
			sSQLSB.append(")WHERE MINROWNUM >=1+?*(?-1) ");
//			sb.append("ORDER BY TERMINAL_ID ASC");
			DBBean.AddSQLParam(emDataType.STR, merchantId);
			DBBean.AddSQLParam(emDataType.STR, procDate);
			DBBean.AddSQLParam(emDataType.STR, terminalId);
			DBBean.AddSQLParam(emDataType.INT, Integer.parseInt(perPageNum));
			DBBean.AddSQLParam(emDataType.INT, Integer.parseInt(selectedPage));
			DBBean.AddSQLParam(emDataType.INT, Integer.parseInt(perPageNum));
			DBBean.AddSQLParam(emDataType.INT, Integer.parseInt(selectedPage));
			
			//20190715 ORDER BY 交易日期>POS代號>批次號碼>調閱編號>卡號>授權碼
			sSQLSB.append("ORDER BY TX_DATE ASC,TERMINAL_ID ASC,BATCH_NO ASC,INVOICE_NO ASC,CARD_NO ASC,AUTH_CODE ASC ");
			
			System.out.println("交易日期查詢>查詢結果>POS代號明細"+ sSQLSB.toString());
			List<Map<String, Object>> objlist = DBBean.QuerySQLByParam2(sSQLSB.toString());
			for(int i = 0; i < objlist.size(); i++){
				Map<String, Object> map = objlist.get(i);
				TErmLog pojo = new TErmLog();
				pojo = tErmLogObj2Pojo(pojo, map);
				list.add(pojo);
			}
			return list;
		}
	}
	
	public int queryTErmLogTotalPageNumByTxDateTerminalId(String procDate, String merchantId, String terminalId,
			String perPageNum) {
		perPageNum = TPSecureUtilities.stripSQLInjection(perPageNum);
		if("all".equals(perPageNum)) {
			return 1;
		}else {
			procDate = TPSecureUtilities.stripSQLInjection(procDate);
			procDate = procDate.replaceAll("/", "");
			merchantId = TPSecureUtilities.stripSQLInjection(merchantId);
			terminalId = TPSecureUtilities.stripSQLInjection(terminalId);
			StringBuffer sSQLSB = new StringBuffer();
			DataBaseBean2 DBBean = new DataBaseBean2();
			// 總頁數：總數/每頁筆數，無條件進位
			sSQLSB.append("SELECT CEIL(COUNT(*)/?) AS TOTALPAGENUM FROM( ");
			sSQLSB.append("SELECT * FROM ERM.T_ERM_LOG WHERE MERCHANT_ID =? AND TX_DATE =? AND TERMINAL_ID =?) ");
			sSQLSB.append("ORDER BY TX_DATE ASC,TERMINAL_ID ASC,BATCH_NO ASC,INVOICE_NO ASC,CARD_NO ASC,AUTH_CODE ASC ");
			DBBean.AddSQLParam(emDataType.INT, Integer.parseInt(perPageNum));
			DBBean.AddSQLParam(emDataType.STR, merchantId);
			DBBean.AddSQLParam(emDataType.STR, procDate);
			DBBean.AddSQLParam(emDataType.STR, terminalId);
			
			System.out.println("總頁數查詢"+ sSQLSB.toString());
			List<Map<String, Object>> objlist = DBBean.QuerySQLByParam2(sSQLSB.toString());
			Map<String, Object> map = objlist.get(0);
			int totalPageNum=((BigDecimal)map.get("TOTALPAGENUM")).intValue();
			return totalPageNum;
		}
	}
	
	
//	
//	public List<TErmLog> queryBySpecByParent(String merchant_id, String process_date_start, String process_date_end, String card_no, String terminal_id, String batch_no, String invoice_no, String tx_amount, String auth_code) {
//		merchant_id = TPSecureUtilities.stripSQLInjection(merchant_id);
//		process_date_start = TPSecureUtilities.stripSQLInjection(process_date_start);
//		process_date_end = TPSecureUtilities.stripSQLInjection( process_date_end);
//		card_no = TPSecureUtilities.stripSQLInjection( card_no);
//		terminal_id = TPSecureUtilities.stripSQLInjection( terminal_id);
//		batch_no = TPSecureUtilities.stripSQLInjection( batch_no);
//		invoice_no = TPSecureUtilities.stripSQLInjection( invoice_no);
//		tx_amount = TPSecureUtilities.stripSQLInjection( tx_amount);
//		auth_code = TPSecureUtilities.stripSQLInjection( auth_code);
//		List<TErmLog> list = new ArrayList<TErmLog>();
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append("SELECT * FROM ERM.T_ERM_LOG ");
//		sb.append("WHERE MERCHANT_ID IN (" + merchant_id + ") ");
//		sb.append("AND TX_DATE >= '" + process_date_start + "' ");
//		sb.append("AND TX_DATE <= '" + process_date_end + "' ");
//		
//		if(StringUtils.isNotBlank(card_no)){
//			sb.append("AND CARD_NO = '" + card_no + "' ");
//		}
//		
//		if(StringUtils.isNotBlank(terminal_id)){
//			sb.append("AND TERMINAL_ID = '" + terminal_id + "' ");
//		}
//		
//		if(StringUtils.isNotBlank(batch_no)){
//			sb.append("AND BATCH_NO = '" + batch_no + "' ");
//		}
//		
//		if(StringUtils.isNotBlank(invoice_no)){
//			sb.append("AND INVOICE_NO = '" + invoice_no + "' ");
//		}
//		
//		if(StringUtils.isNotBlank(tx_amount)){
//			sb.append("AND TX_AMOUNT = '" + tx_amount + "' ");
//		}
//		
//		if(StringUtils.isNotBlank(auth_code)){
//			sb.append("AND AUTH_CODE = '" + auth_code + "' ");
//		}
//		
//		sb.append("ORDER BY TX_DATE ASC");
//
//		List<Map<String, Object>> objlist = this.getJdbcTemplate().queryForList(sb.toString());
//		for(int i = 0; i < objlist.size(); i++){
//			Map<String, Object> map = objlist.get(i);
//			TErmLog pojo = new TErmLog();
//			pojo = tErmLogObj2Pojo(pojo, map);
//			list.add(pojo);
//		}
//		return list;
//	}
	
	/*20220803 子特店拉霸功能
	 *	
	 */
	
	public List<MerchantgGroup> getAllMerchantStoreByHqMid(String hqMid) {
		List<MerchantgGroup> list = new ArrayList<MerchantgGroup>();
		StringBuffer sSQLSB = new StringBuffer();
		DataBaseBean2 DBBean = new DataBaseBean2();
		DBBean.ClearSQLParam();
		sSQLSB.append("SELECT * FROM ERM.T_ERM_MERCHANT WHERE MERCHANTID=? AND HQ_MID=?");
		DBBean.AddSQLParam(emDataType.STR, hqMid);
		DBBean.AddSQLParam(emDataType.STR, hqMid);
		List<Map<String, Object>> objlist = DBBean.QuerySQLByParam2(sSQLSB.toString());
		
		for(int i = 0; i < objlist.size(); i++){
			Map<String, Object> map = objlist.get(i);
			MerchantgGroup pojo = new MerchantgGroup();
			pojo.setMid((String)map.get("MERCHANTID"));
			pojo.setCallName((String)map.get("MERCHANTCALLNAME"));
			pojo.setHqMid((String)map.get("HQ_MID"));
			list.add(pojo);
		}
		
		DBBean = null;
		System.out.println("ERM.T_ERM_MERCHANT search count=" + list.size());
		
		return list;
	}






}
