/**���ʻ���
 * 202208190762-00 20220902 Jeffery Cheng �s�W
 */
package com.fubon.tp.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.fubon.tp.dao.MerchantgGroupDAO;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;

//import com.fubon.tp.dao.mybatis.mapper.MerchantLogoMapper;
//import com.fubon.tp.dao.mybatis.model.MerchantLogo;

//@Service
public class SignLogoService {
	MerchantgGroupDAO merchantgGroupDAO=new MerchantgGroupDAO();

//	@Autowired
//	private MerchantLogoMapper merchantLogoMapper=new MerchantLogoMapper() {
//		
//		@Override
//		public int updateMerchantLogo(MerchantLogo record) {
//			// TODO Auto-generated method stub
//			return 0;
//		}
//		
//		@Override
//		public MerchantLogo selectByPrimaryKey(Integer seqno) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//		
//		@Override
//		public List<MerchantLogo> selectAllByStoreNoAndValidDate(String storeno) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//		
//		@Override
//		public List<MerchantLogo> selectAllByStoreNo(String storeno) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//		
//		@Override
//		public int insert(MerchantLogo record) {
//			// TODO Auto-generated method stub
//			return 0;
//		}
//	};
	public String getMerchantLogoFile(String sHQ_MID) {
		String sImageFile =merchantgGroupDAO.getMerchantLogoFile(sHQ_MID);
		return sImageFile;
	}
//	/**
//	 * �S���ڢݢբݺ޲z
//	 * Fish�A2018/12/26
//	 * �\�໡���G�S���ڢݢբݺ޲z-�d�߯S���Ҧ��ڢݢբݡ@
//	 */
//	public List<MerchantLogo> selectAllByStoreNo(String storeno) {
//		return this.merchantLogoMapper.selectAllByStoreNo(storeno);
//	}
//	
//	/**
//	 * �S���ڢݢբݺ޲z
//	 * Fish�A2018/12/26
//	 * �\�໡���G�S���ڢݢբݺ޲z-�d�߯S���ϥήɶ������ڢݢբݡ@
//	 */
//	public List<MerchantLogo> selectAllByStoreNoAndValidDate(String storeno) {
//		return this.merchantLogoMapper.selectAllByStoreNoAndValidDate(storeno);
//	}
//	
//	
//	/**
//	 * �S���ڢݢբݺ޲z
//	 * Fish�A2018/12/26
//	 * �\�໡���G�S���ڢݢբݺ޲z-�d�� by PK
//	 */
//	public MerchantLogo getMerchantLogoBySeqno(Integer seqno) {
//		return this.merchantLogoMapper.selectByPrimaryKey(seqno);
//	}
//	
//	/**
//	 * �S���ڢݢբݺ޲z
//	 * Fish�A2018/12/26
//	 * �\�໡���G�S���ڢݢբݺ޲z-�@�R��
//	 */
////	@Transactional(rollbackFor=Exception.class)
//	public void deleteMerchantLogo(MerchantLogo merchantLogo) throws Exception {
//		merchantLogo.setAction("3");
//		merchantLogo.setUpdDate(new Date());
//		int i = this.merchantLogoMapper.updateMerchantLogo(merchantLogo);
//	}
//	
//	/**
//	 * FISH�A2018/12/25
//	 * �\�໡���G�S��LOGO�޲z-�s�W
//	 */
////	@Transactional(rollbackFor=Exception.class)
//	public void insertMerchantLogo(String storeno , String validDateStart , String validDateEnd , String logoFile) throws Exception {
//		MerchantLogo entity = new MerchantLogo();
//		entity.setStoreno(storeno);
//		entity.setAction("1");
//		entity.setLogoFile(logoFile);
//		entity.setValidDateStart(strToDate(validDateStart,"000000"));
//		entity.setValidDateEnd(strToDate(validDateEnd,"235959"));
//		entity.setUpdDate(new Date());
//		
//		this.merchantLogoMapper.insert(entity);
//	}
//	
//	/**
//	 * �s�S���A�ȱM��V1.0
//	 * FISH�A2018/12/25
//	 * �\�໡���G�S��LOGO�޲z-�ק�
//	 */
////	@Transactional(rollbackFor=Exception.class)
//	public void updateMerchantLogo(int seqno ,String storeno , String validDateStart , String validDateEnd , String logoFile) throws Exception {
//		MerchantLogo entity = new MerchantLogo();
//		entity.setSeqno(seqno);
//		entity.setStoreno(storeno);
//		entity.setAction("2");
//		entity.setLogoFile(logoFile);
//		entity.setValidDateStart(strToDate(validDateStart,"000000"));
//		entity.setValidDateEnd(strToDate(validDateEnd,"235959"));
//		entity.setUpdDate(new Date());
//		
//		this.merchantLogoMapper.updateMerchantLogo(entity);
//	}
	
//	/**
//	 * �r������
//	 * @param dateStr ����r��
//	 * @param time �ɤ��� 235959
//	 * @return
//	 * @throws ParseException
//	 */
//	private Date strToDate(String dateStr , String time) throws ParseException{
//		SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMddhhmmss");
//		return formatDate.parse(dateStr+time);
//	}
}
