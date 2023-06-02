/**異動說明
 * 202208190762-00 20220902 Jeffery Cheng 新增
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
//	 * 特店ＬＯＧＯ管理
//	 * Fish，2018/12/26
//	 * 功能說明：特店ＬＯＧＯ管理-查詢特店所有ＬＯＧＯ　
//	 */
//	public List<MerchantLogo> selectAllByStoreNo(String storeno) {
//		return this.merchantLogoMapper.selectAllByStoreNo(storeno);
//	}
//	
//	/**
//	 * 特店ＬＯＧＯ管理
//	 * Fish，2018/12/26
//	 * 功能說明：特店ＬＯＧＯ管理-查詢特店使用時間內的ＬＯＧＯ　
//	 */
//	public List<MerchantLogo> selectAllByStoreNoAndValidDate(String storeno) {
//		return this.merchantLogoMapper.selectAllByStoreNoAndValidDate(storeno);
//	}
//	
//	
//	/**
//	 * 特店ＬＯＧＯ管理
//	 * Fish，2018/12/26
//	 * 功能說明：特店ＬＯＧＯ管理-查詢 by PK
//	 */
//	public MerchantLogo getMerchantLogoBySeqno(Integer seqno) {
//		return this.merchantLogoMapper.selectByPrimaryKey(seqno);
//	}
//	
//	/**
//	 * 特店ＬＯＧＯ管理
//	 * Fish，2018/12/26
//	 * 功能說明：特店ＬＯＧＯ管理-　刪除
//	 */
////	@Transactional(rollbackFor=Exception.class)
//	public void deleteMerchantLogo(MerchantLogo merchantLogo) throws Exception {
//		merchantLogo.setAction("3");
//		merchantLogo.setUpdDate(new Date());
//		int i = this.merchantLogoMapper.updateMerchantLogo(merchantLogo);
//	}
//	
//	/**
//	 * FISH，2018/12/25
//	 * 功能說明：特店LOGO管理-新增
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
//	 * 新特店服務專區V1.0
//	 * FISH，2018/12/25
//	 * 功能說明：特店LOGO管理-修改
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
//	 * 字串轉日期
//	 * @param dateStr 日期字串
//	 * @param time 時分秒 235959
//	 * @return
//	 * @throws ParseException
//	 */
//	private Date strToDate(String dateStr , String time) throws ParseException{
//		SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMddhhmmss");
//		return formatDate.parse(dateStr+time);
//	}
}
