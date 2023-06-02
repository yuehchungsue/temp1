/**異動說明
 * 202208190762-00 20220902 Jeffery Cheng 新增
 */
package com.fubon.tp.model.pojo;
import java.util.Date;
/**
 * The DmChargestation is generated from MyClipse with using Spring Template.
 * 
 * @author Johnson.Chen
 */
//@Entity
//@Table(name = "MerchantLogo", schema = "dbo")
public class MerchantLogo implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
//	@Id
//	@Column(name = "SEQNO", unique = true, nullable = false, length = 4)
	private String SEQNO;
//	@Column(name = "STORENO", nullable = false , length = 15)
	private String STORENO;
//	@Column(name = "LOGO_FILE", nullable = true)
	private String LOGO_FILE;
//	@Column(name = "FILE_NAME", nullable = true, length = 20)
	private String FILE_NAME;
//	@Column(name = "ACTION", nullable = true, length = 1)
	private String ACTION;
//	@Column(name = "VALID_DATE_START", nullable = true)
	private Date VALID_DATE_START;
//	@Column(name = "VALID_DATE_END", nullable = true)
	private Date VALID_DATE_END;
//	@Column(name = "UPD_DATE", nullable = true)
	private Date UPD_DATE;
	public String getSEQNO() {
		return SEQNO;
	}
	public void setSEQNO(String sEQNO) {
		SEQNO = sEQNO;
	}
	public String getSTORENO() {
		return STORENO;
	}
	public void setSTORENO(String sTORENO) {
		STORENO = sTORENO;
	}
	public String getLOGO_FILE() {
		return LOGO_FILE;
	}
	public void setLOGO_FILE(String lOGO_FILE) {
		LOGO_FILE = lOGO_FILE;
	}
	public String getFILE_NAME() {
		return FILE_NAME;
	}
	public void setFILE_NAME(String fILE_NAME) {
		FILE_NAME = fILE_NAME;
	}
	public String getACTION() {
		return ACTION;
	}
	public void setACTION(String aCTION) {
		ACTION = aCTION;
	}
	public Date getVALID_DATE_START() {
		return VALID_DATE_START;
	}
	public void setVALID_DATE_START(Date vALID_DATE_START) {
		VALID_DATE_START = vALID_DATE_START;
	}
	public Date getVALID_DATE_END() {
		return VALID_DATE_END;
	}
	public void setVALID_DATE_END(Date vALID_DATE_END) {
		VALID_DATE_END = vALID_DATE_END;
	}
	public Date getUPD_DATE() {
		return UPD_DATE;
	}
	public void setUPD_DATE(Date uPD_DATE) {
		UPD_DATE = uPD_DATE;
	}

}