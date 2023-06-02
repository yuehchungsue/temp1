/**���ʻ���
 * 202208190762-00 20220902 Jeffery Cheng �s�W
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
	 * Fish,2018/12/10 �����d��
	 * 
	 * @param procDate
	 *            ������
	 * @param merchantId
	 *            �S���N��
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
	 *            ������
	 * @param merchantId
	 *            �S���N��
	 * @param trerminalId
	 *            POS�N��
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
	 *            ������
	 * @param merchantId
	 *            �S���N��
	 * @param trerminalId
	 *            POS�N��
	 * @param perPageNum
	 *     	 �������� �A�w�]10��
	 * @param selectedPage
	 *  	 ��ܭ��ơA�w�]��1��
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
	 *            ������
	 * @param merchantId
	 *            �S���N��
	 * @param trerminalId
	 *            POS�N��
	 * @param perPageNum
	 *     	 �������� �A�w�]10��
	 *           
	 * @return �����`��
	 */
	public int queryTErmLogTotalPageNumByTxDateTerminalId(String procDate, String merchantId, String terminalId,
			String perPageNum) {
		int totalPageNum=signDAO.queryTErmLogTotalPageNumByTxDateTerminalId(procDate,merchantId,terminalId,perPageNum);
		return totalPageNum;
	}

	/**
	 * Fish,2018/12/10 �d�߳浧ñ�檺���Ӹ��
	 * 
	 * @param trans_id
	 *            ñ��ߤ@�Ǹ�
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
	 * Fish,2018/12/10 �S�w����d��
	 * 
	 * @param merchant_id
	 *            �S���N��
	 * @param process_date_start
	 *            �������}�l�ɶ�
	 * @param process_date_end
	 *            �����������ɶ�
	 * @param card_no
	 *            �d��
	 * @param terminal_id
	 *            �ݥ����N��
	 * @param batch_no
	 *            �妸���X
	 * @param invoice_no
	 *            �վ\�s��
	 * @param tx_amount
	 *            ������B
	 * @param auth_code
	 *            ���v�X
	 * @return
	 */
	 
	public List<TErmLog> queryBySpec(String merchant_id, String process_date_start, String process_date_end, String card_no, String terminal_id, String batch_no, String invoice_no, String tx_amount, String auth_code) {
		List<TErmLog> list = signDAO.queryBySpec2(merchant_id, process_date_start, process_date_end,
				card_no, terminal_id, batch_no, invoice_no, tx_amount, auth_code);
		return list;
	}
	
	
	/* Jeffery,2022/08/30
	 * �i���d��
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
	 * Fish,2018/12/10 �S������
	 * 
	 * @param storeno
	 *            �S���N��
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
	 * �����[�ѱK��KEY(���KEY) key 1 = �d���e 4 �X + �d�����Ĵ� 4 �X key 2 = �ݥ����N�� 8 �X
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
	 * �����ӤHñ�W��(���ѱK)
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
	 * �����ӤHñ�W��(�ѱK)
	 * 
	 * @param file
	 *            ���ѱKñ�W��
	 * @param secretKey
	 *            �[�ѱK��KEY
	 * @return
	 * @throws Exception
	 */
	
	/*�սc���i
	 * �϶��s�X�����@�~�Ҧ��O�@�غt��k�A���y�z�p��@�A�M�αK�X����@�϶��@�~�Ӧw���a�ഫ�W�L�@�Ӱ϶�����ƶq�C���ǧ@�~�Ҧ��]�t�F�q�l�{���X�y (ECB)�B�[�K�϶��쵲 (CBC)�B�[�K�^�X (CFB) �M�p�ƾ� (CTR)�C
	 * �b����W�AECB �Ҧ����w���ʸ��C�A�]�����|�������ۦP���¤�r�϶����ͤ@�˪��[�K��r�CCBC �Ҧ��e���D�� Padding oracle �����CCTR �Ҧ��h�L�o�Ǯz�I�A�]�����u����Ϊ��Ҧ��C
	 * 
	 * ��[�K����ƶW�L�@�Ӱ϶��ɡA���קK�ϥ� ECB �M CBC �@�~�Ҧ��C�b�P SSL �f�t�ϥήɡACBC �Ҧ����į�|���ǭ��C�ӾɭP�Y�����I �C
	 * �]���A�Ч�� CCM (Counter with CBC-MAC) �Ҧ��A�Ϊ̡A�p�G�į���u���Ҷq�A�h�ϥ� GCM (Galois/Counter Mode) �Ҧ� (�p�G�i��)�C
	 */
	public String getDecodeSignImage(byte[] file, String secretKey) throws Exception {

		String base64Str = "";
		GZIPInputStream gis = null;
		ByteArrayOutputStream out = null;

		byte[] data = null;
		byte[] data_remaining = null;

		// �B�z������8bytes�����
		int remaining = file.length % 8;
		if (remaining > 0) {
			System.out.println("����8bytes�����סG" + remaining);
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
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance(TRANSFORMATION1);//�սc���i����ĳ�ϥιL��DES�t��A���˨ϥ�AES
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
			cipher = Cipher.getInstance(lines.get(0));//�սc���˨ϥΡA�Ԧp�H�W���ѳ��i
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
	 * ��x�ϥ�
	 * Fish,2018/12/10 �����d��
	 * 
	 * @param procDate
	 *            ������
	 * @param merchantId
	 *            �S���N��
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
	 * ��x�ϥ�
	 * Fish,2018/12/10
	 * 
	 * @param procDate
	 *            ������
	 * @param merchantId
	 *            �S���N��
	 * @param trerminalId
	 *            POS�N��
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
	 * ��x�ϥ�
	 * Fish,2018/12/10 �S�w����d��
	 * 
	 * @param merchant_id
	 *            �S���N��
	 * @param process_date_start
	 *            �������}�l�ɶ�
	 * @param process_date_end
	 *            �����������ɶ�
	 * @param card_no
	 *            �d��
	 * @param terminal_id
	 *            �ݥ����N��
	 * @param batch_no
	 *            �妸���X
	 * @param invoice_no
	 *            �վ\�s��
	 * @param tx_amount
	 *            ������B
	 * @param auth_code
	 *            ���v�X
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
			byte b2 = (byte) Integer.parseInt(buString, 16);// �N��r����16�i��
			returnBytes[j] = b2;
		}
		return returnBytes;
	}







}
