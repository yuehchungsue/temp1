/**異動說明
 * 202208190762-00 20220902 Jeffery Cheng 新增
 */
package com.fubon.tp.model.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class TSignImage implements Serializable{
	
	private String trans_id;
	private BigDecimal packet_num;
	private BigDecimal packet_len;
	private String image;
	private String chk_err_flag;
	private Date log_date;
	
	public String getTrans_id() {
		return trans_id;
	}
	public void setTrans_id(String trans_id) {
		this.trans_id = trans_id;
	}
	public BigDecimal getPacket_num() {
		return packet_num;
	}
	public void setPacket_num(BigDecimal packet_num) {
		this.packet_num = packet_num;
	}
	public BigDecimal getPacket_len() {
		return packet_len;
	}
	public void setPacket_len(BigDecimal packet_len) {
		this.packet_len = packet_len;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getChk_err_flag() {
		return chk_err_flag;
	}
	public void setChk_err_flag(String chk_err_flag) {
		this.chk_err_flag = chk_err_flag;
	}
	public Date getLog_date() {
		return log_date;
	}
	public void setLog_date(Date log_date) {
		this.log_date = log_date;
	}
}
