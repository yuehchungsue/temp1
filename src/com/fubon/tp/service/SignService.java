/**異動說明
 * 202208190762-00 20220902 Jeffery Cheng 新增
 */
package com.fubon.tp.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Key;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.codec.binary.Base64;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;

import com.fubon.tp.dao.MerchantgGroupDAO;
//import com.fubon.tp.dao.MerchantLogoDAO;
import com.fubon.tp.dao.SignDAO;
import com.fubon.tp.dao.TMerchantDAO;
//import com.fubon.tp.model.pojo.MerchantLogo;
import com.fubon.tp.model.pojo.MerchantgGroup;
import com.fubon.tp.model.pojo.TErmLog;
import com.fubon.tp.model.pojo.TMerchants;
import com.fubon.tp.model.pojo.TSignImage;
import com.fubon.tp.util.FileReaderUtils;

//@Service
//@Transactional
public class SignService { 

//	@Autowired
	private SignDAO signDAO=new SignDAO();

//	@Autowired
//	private MerchantLogoDAO merchantLogoDAO=new MerchantLogoDAO();
	
//	@Autowired
	private TMerchantDAO tMerchantDAO=new TMerchantDAO();
	
	private MerchantgGroupDAO merchantgGroupDAO=new MerchantgGroupDAO();
	
	private static final String TEXT_FILENAME = "src/TRANSFORMATION.txt";
	private static final String TRANSFORMATION1 = "AES";
	

	/**
	 * Fish,2018/12/10 交易日查詢
	 * 
	 * @param procDate
	 *            交易日期
	 * @param merchantId
	 *            特店代號
	 * @return
	 */
	public List<TErmLog> queryTerminalIdByTxDate(String procDate, String merchantId) {
		List<TErmLog> list = signDAO.queryTerminalIdByTxDate2(procDate, merchantId);
		return list;
	}
	
	/**
	 * Fish,2018/12/10
	 *  
	 * @param procDate
	 *            交易日期
	 * @param merchantId
	 *            特店代號
	 * @param trerminalId
	 *            POS代號
	 * @return
	 */
	public List<TErmLog> queryTErmLogByTxDateTerminalId(String procDate, String merchantId, String trerminalId) {
		List<TErmLog> list = signDAO.queryTErmLogByTxDateTerminalId2(procDate, merchantId, trerminalId);
		return list;
	}
	
	
	
	/**
	 * Jeffery,2022/08/26
	 * 
	 * @param procDate
	 *            交易日期
	 * @param merchantId
	 *            特店代號
	 * @param trerminalId
	 *            POS代號
	 * @param perPageNum
	 *     	 分頁筆數 ，預設10筆
	 * @param selectedPage
	 *  	 選擇頁數，預設第1頁
	 *           
	 * @return
	 */
	public List<TErmLog> queryTErmLogByTxDateTerminalIdAndPerPageNumAndSelectedPage(String procDate, String merchantId,
			String terminalId, String perPageNum,String selectedPage) {
		List<TErmLog> list = signDAO.queryTErmLogByTxDateTerminalIdAndPerPageNumAndSelectedPage(procDate, merchantId, terminalId,perPageNum,selectedPage);
		return list;
	}
	
	
	/**
	 * Jeffery,2022/08/29
	 * 
	 * @param procDate
	 *            交易日期
	 * @param merchantId
	 *            特店代號
	 * @param trerminalId
	 *            POS代號
	 * @param perPageNum
	 *     	 分頁筆數 ，預設10筆
	 *           
	 * @return 分頁總數
	 */
	public int queryTErmLogTotalPageNumByTxDateTerminalId(String procDate, String merchantId, String terminalId,
			String perPageNum) {
		int totalPageNum=signDAO.queryTErmLogTotalPageNumByTxDateTerminalId(procDate,merchantId,terminalId,perPageNum);
		return totalPageNum;
	}

	/**
	 * Fish,2018/12/10 查詢單筆簽單的明細資料
	 * 
	 * @param trans_id
	 *            簽單唯一序號
	 * @return
	 */
	public TErmLog queryTErmLogByTransId(String trans_id) {
		TErmLog t = signDAO.queryTErmLogByTransId2(trans_id);
		return t;
	}

	
	public TMerchants queryByMerchantsId(String merchantId) {
		TMerchants t = tMerchantDAO.queryByMerchantId2(merchantId);
		return t;
	}
	
	public List<MerchantgGroup> getAllMerchantStoreByHqMid(String hqMid) {
		List<MerchantgGroup> t=merchantgGroupDAO.getAllMerchantStoreByHqMid(hqMid);
		return t;
	}
	
	/**
	 * Fish,2018/12/10 特定條件查詢
	 * 
	 * @param merchant_id
	 *            特店代號
	 * @param process_date_start
	 *            交易日期開始時間
	 * @param process_date_end
	 *            交易日期結束時間
	 * @param card_no
	 *            卡號
	 * @param terminal_id
	 *            端末機代號
	 * @param batch_no
	 *            批次號碼
	 * @param invoice_no
	 *            調閱編號
	 * @param tx_amount
	 *            交易金額
	 * @param auth_code
	 *            授權碼
	 * @return
	 */
	 
	public List<TErmLog> queryBySpec(String merchant_id, String process_date_start, String process_date_end, String card_no, String terminal_id, String batch_no, String invoice_no, String tx_amount, String auth_code) {
		List<TErmLog> list = signDAO.queryBySpec2(merchant_id, process_date_start, process_date_end,
				card_no, terminal_id, batch_no, invoice_no, tx_amount, auth_code);
		return list;
	}
	
	
	/* Jeffery,2022/08/30
	 * 進階查詢
	 * 
	 */
	public List<TErmLog> queryTErmLogBySpecAndPerPageNumAndSelectedPage(String sMERCHANT_ID, String sTX_DATE_Start,
			String sTX_DATE_End, String sCARD_NO, String sTERMINAL_ID, String sBATCH_NO, String sINVOICE_NO,
			String sTX_AMOUNT, String sAUTH_CODE, String perPageNum, String choosenPage) {
		List<TErmLog> list=signDAO.queryTErmLogBySpecAndPerPageNumAndSelectedPage(
				 sMERCHANT_ID,  sTX_DATE_Start,
				 sTX_DATE_End,  sCARD_NO,  sTERMINAL_ID,  sBATCH_NO,  sINVOICE_NO,
				 sTX_AMOUNT,  sAUTH_CODE, perPageNum ,  choosenPage);
		return list;
	}

	public int queryTErmLogTotalPageNumBySpec(String sMERCHANT_ID, String sTX_DATE_Start, String sTX_DATE_End,
			String sCARD_NO, String sTERMINAL_ID, String sBATCH_NO, String sINVOICE_NO, String sTX_AMOUNT,
			String sAUTH_CODE, String perPageNum) {
		int totalPageNum=signDAO.queryTErmLogTotalPageNumBySpec(
				 sMERCHANT_ID,  sTX_DATE_Start,
				 sTX_DATE_End,  sCARD_NO,  sTERMINAL_ID,  sBATCH_NO,  sINVOICE_NO,
				 sTX_AMOUNT,  sAUTH_CODE, perPageNum);
		return totalPageNum;
	}

	/**
	 * Fish,2018/12/10 特店圖檔
	 * 
	 * @param storeno
	 *            特店代號
	 * @return
	 */
//	public String getMerchantLogo(String storeno) {
//		String img = "";
//		List<MerchantLogo> list = merchantLogoDAO.findMerchantLogoByStoreno(storeno);
//		if (list.size() > 0) {
//			img = list.get(0).getLOGO_FILE();
//		}
//		return img;
//	}

	/**
	 * 取的加解密的KEY(兩組KEY) key 1 = 卡號前 4 碼 + 卡片有效期 4 碼 key 2 = 端末機代號 8 碼
	 * 
	 * @param pojo
	 * @return
	 */
	public String getDecodeSecretKey(TErmLog pojo) {
		String key1 = pojo.getCard_no().substring(0, 4) + pojo.getExpire_date();
		String key2 = pojo.getTerminal_id();
		return key1 + key2 + key1;
	}

	/**
	 * 取的個人簽名檔(未解密)
	 * 
	 * @param transId
	 * @return
	 */
	public byte[] getSingImage(String transId) {
		String imgStr = "";
		List<TSignImage> list = signDAO.getSingImage2(transId);

		for (TSignImage obj : list) {
			imgStr += obj.getImage();
		}
		
		return twoCharToByte(imgStr);
	}

	/**
	 * 取的個人簽名檔(解密)
	 * 
	 * @param file
	 *            未解密簽名檔
	 * @param secretKey
	 *            加解密的KEY
	 * @return
	 * @throws Exception
	 */
	
	/*白箱報告
	 * 區塊編碼器的作業模式是一種演算法，它描述如何一再套用密碼的單一區塊作業來安全地轉換超過一個區塊的資料量。有些作業模式包含了電子程式碼籍 (ECB)、加密區塊鏈結 (CBC)、加密回饋 (CFB) 和計數器 (CTR)。
	 * 在本質上，ECB 模式的安全性較低，因為它會為完全相同的純文字區塊產生一樣的加密文字。CBC 模式容易遭受 Padding oracle 攻擊。CTR 模式則無這些弱點，因此為優先選用的模式。
	 * 
	 * 當加密的資料超過一個區塊時，請避免使用 ECB 和 CBC 作業模式。在與 SSL 搭配使用時，CBC 模式的效能會有些降低而導致嚴重風險 。
	 * 因此，請改用 CCM (Counter with CBC-MAC) 模式，或者，如果效能需優先考量，則使用 GCM (Galois/Counter Mode) 模式 (如果可用)。
	 */
	public String getDecodeSignImage(byte[] file, String secretKey) throws Exception {

		String base64Str = "";
		GZIPInputStream gis = null;
		ByteArrayOutputStream out = null;

		byte[] data = null;
		byte[] data_remaining = null;

		// 處理有不足8bytes的資料
		int remaining = file.length % 8;
		if (remaining > 0) {
			System.out.println("不足8bytes的長度：" + remaining);
			data = new byte[file.length - remaining];
			System.arraycopy(file, 0, data, 0, file.length - remaining);
			data_remaining = new byte[remaining];
			System.arraycopy(file, file.length - remaining, data_remaining, 0, remaining);
		} else {
			data = file;
		}

		Key deskey = null;
		DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
//		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(TRANSFORMATION1);//白箱報告不建議使用過時DES演算，推薦使用AES
		deskey = keyfactory.generateSecret(spec);		
//		Cipher cipher = Cipher.getInstance("desede/ECB/NoPadding");
		List<String> lines = null;
		try {
			FileReaderUtils obj = FileReaderUtils.getInstance();
			lines = obj.generateArrayListFromFile(TEXT_FILENAME);
		} catch (IOException e) {
			e.printStackTrace();
		} 		
		Cipher cipher = null;
		if(lines!=null) {
			cipher = Cipher.getInstance(lines.get(0));//白箱推薦使用，詳如以上註解報告
		}
		byte[] decryptData = null;
		if(cipher!=null) {
			cipher.init(Cipher.DECRYPT_MODE, deskey);
			decryptData = cipher.doFinal(data);
		}
		
		if(decryptData!=null) {
			if (data_remaining != null) {
				byte[] newResult = new byte[decryptData.length + data_remaining.length];
				System.arraycopy(decryptData, 0, newResult, 0, decryptData.length);
				System.arraycopy(data_remaining, 0, newResult, decryptData.length, data_remaining.length);
				gis = new GZIPInputStream(new ByteArrayInputStream(newResult));
			} else {
				gis = new GZIPInputStream(new ByteArrayInputStream(decryptData));
			}			
		}


		byte[] buffer = new byte[1024];
		out = new ByteArrayOutputStream();

		int len;
		while ((len = gis.read(buffer)) > 0) {
			out.write(buffer, 0, len);
		}
		base64Str = new String(Base64.encodeBase64String(out.toByteArray()));

		out.close();
		gis.close();

		return base64Str.trim();
	}
	
	
	
	/**
	 * 後台使用
	 * Fish,2018/12/10 交易日查詢
	 * 
	 * @param procDate
	 *            交易日期
	 * @param merchantId
	 *            特店代號
	 * @return
	 */
//	public List<TErmLog> queryTerminalIdByTxDateByParent(String procDate, List<String> merchantId) {
//		String subIdList = "";
//		for(String obj:merchantId){
//			subIdList = subIdList + "'" + obj + "',";
//		}
//		subIdList = subIdList.substring(0, subIdList.lastIndexOf(","));
//		List<TErmLog> list = signDAO.queryTerminalIdByTxDateByParent(procDate, subIdList);
//		return list;
//	}
	
	/**
	 * 後台使用
	 * Fish,2018/12/10
	 * 
	 * @param procDate
	 *            交易日期
	 * @param merchantId
	 *            特店代號
	 * @param trerminalId
	 *            POS代號
	 * @return
	 */
//	public List<TErmLog> queryTErmLogByTxDateTerminalIdByParent(String procDate, List<String> merchantId, String trerminalId) {
//		String subIdList = "";
//		for(String obj:merchantId){
//			subIdList = subIdList + "'" + obj + "',";
//		}
//		subIdList = subIdList.substring(0, subIdList.lastIndexOf(","));
//		
//		List<TErmLog> list = signDAO.queryTErmLogByTxDateTerminalIdByParent(procDate, subIdList, trerminalId);
//		return list;
//	}
	
	
	/**
	 * 後台使用
	 * Fish,2018/12/10 特定條件查詢
	 * 
	 * @param merchant_id
	 *            特店代號
	 * @param process_date_start
	 *            交易日期開始時間
	 * @param process_date_end
	 *            交易日期結束時間
	 * @param card_no
	 *            卡號
	 * @param terminal_id
	 *            端末機代號
	 * @param batch_no
	 *            批次號碼
	 * @param invoice_no
	 *            調閱編號
	 * @param tx_amount
	 *            交易金額
	 * @param auth_code
	 *            授權碼
	 * @return
	 */
//	public List<TErmLog> queryBySpecByParent(List<String> merchant_id, String process_date_start, String process_date_end, String card_no, String terminal_id, String batch_no, String invoice_no, String tx_amount, String auth_code) {
//		String subIdList = "";
//		for(String obj:merchant_id){
//			subIdList = subIdList + "'" + obj + "',";
//		}
//		subIdList = subIdList.substring(0, subIdList.lastIndexOf(","));
//		
//		List<TErmLog> list = signDAO.queryBySpecByParent(subIdList, process_date_start, process_date_end,
//				card_no, terminal_id, batch_no, invoice_no, tx_amount, auth_code);
//		return list;
//	}
	private static byte[] twoCharToByte(String data) {
		String buString = "";
		int j = 0;
		byte[] returnBytes = new byte[data.length() / 2];
		for (int i = 0; i < data.length(); i += 2, j++) {
			char fs = data.charAt(i);
			char bs = data.charAt(i + 1);
			buString = String.valueOf(fs) + String.valueOf(bs);
			byte b2 = (byte) Integer.parseInt(buString, 16);// 將文字視為16進位
			returnBytes[j] = b2;
		}
		return returnBytes;
	}







}
