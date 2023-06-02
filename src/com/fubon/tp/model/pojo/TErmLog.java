/**異動說明
 * 202208190762-00 20220902 Jeffery Cheng 新增
 */
package com.fubon.tp.model.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class TErmLog implements Serializable{

	private String trans_id;
	private BigDecimal total_packet_num;
	private BigDecimal total_packet_len;
	private String card_no;
	private BigDecimal tx_amount;
	private String stan;
	private String tx_date;
	private String tx_time;
	private String expire_date;
	private String ref_no;
	private String auth_code;
	private String terminal_id;
	private String merchant_id;
	private String store_id;
	private BigDecimal tip_amount;
	private String batch_no;
	private String invoice_no;
	private String inst_period;
	private BigDecimal inst_down_payment;
	private BigDecimal inst_payment;
	private BigDecimal inst_fee;
	private BigDecimal redeem_paid_amount;
	private BigDecimal redeem_amount;
	private BigDecimal redeem_point;
	private BigDecimal redeem_balance_point;
	private String card_type;
	private String trans_mode;
	private String trans_type;
	private String unsign_flag;
	private String check_no;
	private String cup_stan;
	private String emv_tc;
	private String cardholder_name;
	private String chk_err_flag;
	private Date log_date;
	private String data_source;


	private String signInfoType;
	private String trans_type_show_cht;
	private String trans_type_show_eng;
	private String card_type_show;
	private String tx_date_time_show;
	
	private transient DecimalFormat fmt1 = new DecimalFormat("###,###,###,###.##");
	private String tx_amount_show;
	private String tip_amount_show;
	
	
	private transient DecimalFormat fmt2 = new DecimalFormat("##,###,###");
	private String inst_down_payment_show;
	private String inst_payment_show;
	private String inst_fee_show;
	private String redeem_paid_amount_show;
	private String redeem_amount_show;
	private String redeem_point_show;
	private String redeem_balance_point_show;
	
	



	public String getTrans_id() {
		return trans_id;
	}

	public void setTrans_id(String trans_id) {
		this.trans_id = trans_id;
	}

	public BigDecimal getTotal_packet_num() {
		return total_packet_num;
	}

	public void setTotal_packet_num(BigDecimal total_packet_num) {
		this.total_packet_num = total_packet_num;
	}

	public BigDecimal getTotal_packet_len() {
		return total_packet_len;
	}

	public void setTotal_packet_len(BigDecimal total_packet_len) {
		this.total_packet_len = total_packet_len;
	}

	public String getCard_no() {
		return card_no;
	}

	public void setCard_no(String card_no) {
		this.card_no = card_no;
	}

	public BigDecimal getTx_amount() {
		return tx_amount;
	}

	public void setTx_amount(BigDecimal tx_amount) {
		this.tx_amount = tx_amount;
	}

	public String getStan() {
		return stan;
	}

	public void setStan(String stan) {
		this.stan = stan;
	}

	public String getTx_date() {
		return tx_date;
	}

	public void setTx_date(String tx_date) {
		this.tx_date = tx_date;
	}

	public String getTx_time() {
		return tx_time;
	}

	public void setTx_time(String tx_time) {
		this.tx_time = tx_time;
	}

	public String getExpire_date() {
		return expire_date;
	}

	public void setExpire_date(String expire_date) {
		this.expire_date = expire_date;
	}

	public String getRef_no() {
		return ref_no;
	}

	public void setRef_no(String ref_no) {
		this.ref_no = ref_no;
	}

	public String getAuth_code() {
		return auth_code;
	}

	public void setAuth_code(String auth_code) {
		this.auth_code = auth_code;
	}

	public String getTerminal_id() {
		return terminal_id;
	}

	public void setTerminal_id(String terminal_id) {
		this.terminal_id = terminal_id;
	}

	public String getMerchant_id() {
		return merchant_id;
	}

	public void setMerchant_id(String merchant_id) {
		this.merchant_id = merchant_id;
	}

	public String getStore_id() {
		return store_id;
	}

	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}

	public BigDecimal getTip_amount() {
		return tip_amount;
	}

	public void setTip_amount(BigDecimal tip_amount) {
		this.tip_amount = tip_amount;
	}

	public String getBatch_no() {
		return batch_no;
	}

	public void setBatch_no(String batch_no) {
		this.batch_no = batch_no;
	}

	public String getInvoice_no() {
		return invoice_no;
	}

	public void setInvoice_no(String invoice_no) {
		this.invoice_no = invoice_no;
	}

	public String getInst_period() {
		return inst_period;
	}

	public void setInst_period(String inst_period) {
		this.inst_period = inst_period;
	}

	public BigDecimal getInst_down_payment() {
		return inst_down_payment;
	}

	public void setInst_down_payment(BigDecimal inst_down_payment) {
		this.inst_down_payment = inst_down_payment;
	}

	public BigDecimal getInst_payment() {
		return inst_payment;
	}

	public void setInst_payment(BigDecimal inst_payment) {
		this.inst_payment = inst_payment;
	}

	public BigDecimal getInst_fee() {
		return inst_fee;
	}

	public void setInst_fee(BigDecimal inst_fee) {
		this.inst_fee = inst_fee;
	}

	public BigDecimal getRedeem_paid_amount() {
		return redeem_paid_amount;
	}

	public void setRedeem_paid_amount(BigDecimal redeem_paid_amount) {
		this.redeem_paid_amount = redeem_paid_amount;
	}

	public BigDecimal getRedeem_amount() {
		return redeem_amount;
	}

	public void setRedeem_amount(BigDecimal redeem_amount) {
		this.redeem_amount = redeem_amount;
	}

	public BigDecimal getRedeem_point() {
		return redeem_point;
	}

	public void setRedeem_point(BigDecimal redeem_point) {
		this.redeem_point = redeem_point;
	}

	public BigDecimal getRedeem_balance_point() {
		return redeem_balance_point;
	}

	public void setRedeem_balance_point(BigDecimal redeem_balance_point) {
		this.redeem_balance_point = redeem_balance_point;
	}

	public String getCard_type() {
		return card_type;
	}

	public void setCard_type(String card_type) {
		this.card_type = card_type;
	}

	public String getTrans_mode() {
		return trans_mode;
	}

	public void setTrans_mode(String trans_mode) {
		this.trans_mode = trans_mode;
	}

	public String getTrans_type() {
		return trans_type;
	}

	public void setTrans_type(String trans_type) {
		this.trans_type = trans_type;
	}

	public String getUnsign_flag() {
		return unsign_flag;
	}

	public void setUnsign_flag(String unsign_flag) {
		this.unsign_flag = unsign_flag;
	}

	public String getCheck_no() {
		return check_no;
	}

	public void setCheck_no(String check_no) {
		this.check_no = check_no;
	}

	public String getCup_stan() {
		return cup_stan;
	}

	public void setCup_stan(String cup_stan) {
		this.cup_stan = cup_stan;
	}

	public String getEmv_tc() {
		return emv_tc;
	}

	public void setEmv_tc(String emv_tc) {
		this.emv_tc = emv_tc;
	}

	public String getCardholder_name() {
		return cardholder_name;
	}

	public void setCardholder_name(String cardholder_name) {
		this.cardholder_name = cardholder_name;
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
	
	

	public String getTx_amount_show() {
		if(fmt1!=null&&tx_amount!=null)
			return fmt1.format(Double.parseDouble(tx_amount.toString()));
		return null;
	}

	public String getTip_amount_show() {
		if(fmt1!=null&&tx_amount!=null)
			return fmt1.format(Double.parseDouble(tip_amount.toString()));
		return null;
	}

	public String getInst_down_payment_show() {
		if(fmt2!=null&&inst_down_payment!=null)
			return fmt2.format(Double.parseDouble(inst_down_payment.toString()));
		return null;
	}

	public String getInst_payment_show() {
		if(fmt2!=null&&inst_payment!=null)
			return fmt2.format(Double.parseDouble(inst_payment.toString()));
		return null;
	}

	public String getInst_fee_show() {
		if(fmt2!=null&&inst_fee!=null)
			return fmt2.format(Double.parseDouble(inst_fee.toString()));
		return null;
	}

	public String getRedeem_paid_amount_show() {
		if(fmt2!=null&&redeem_paid_amount!=null)
			return fmt2.format(Double.parseDouble(redeem_paid_amount.toString()));
		return null;
	}

	public String getRedeem_amount_show() {
		if(fmt2!=null&&redeem_amount!=null)
			return fmt2.format(Double.parseDouble(redeem_amount.toString()));
		return null;
	}

	public String getRedeem_point_show() {
		if(fmt2!=null&&redeem_point!=null)
			return fmt2.format(Double.parseDouble(redeem_point.toString()));
		return null;
	}

	public String getRedeem_balance_point_show() {
		if(fmt2!=null&&redeem_balance_point!=null)
			return fmt2.format(Double.parseDouble(redeem_balance_point.toString()));
		return null;
	}
	
	public String getSignInfoType() {
		String type ="" ;
		if(! StringUtils.equals(card_type, "04")){
			if(StringUtils.equals(trans_type, "01")){
				type = "1";
			}else if(StringUtils.equals(trans_type, "41")){
				type = "2";
			}else if(StringUtils.equals(trans_type, "02")){
				type = "3";
			}else if(StringUtils.equals(trans_type, "42")){
				type = "4";
			}else if(StringUtils.equals(trans_type, "21")){
				type = "5";
			}else if(StringUtils.equals(trans_type, "61")){
				type = "6";
			}else if(StringUtils.equals(trans_type, "22")){
				type = "7";
			}else if(StringUtils.equals(trans_type, "62")){
				type = "8";
			}else if(StringUtils.equals(trans_type, "31")){
				type = "9";
			}else if(StringUtils.equals(trans_type, "71")){
				type = "10";
			}else if(StringUtils.equals(trans_type, "32")){
				type = "11";
			}else if(StringUtils.equals(trans_type, "72")){
				type = "12";
			}else if(StringUtils.equals(trans_type, "04")){
				type = "13";
			}else if(StringUtils.equals(trans_type, "11")){
				type = "14";
			}else if(StringUtils.equals(trans_type, "03")){
				type = "15";
			}else if(StringUtils.equals(trans_type, "43")){
				type = "16";
			}else if(StringUtils.equals(trans_type, "23")){
				type = "17";
			}else if(StringUtils.equals(trans_type, "63")){
				type = "18";
			}else if(StringUtils.equals(trans_type, "33")){
				type = "19";
			}else if(StringUtils.equals(trans_type, "73")){
				type = "20";
			}
		}else if(StringUtils.equals(card_type, "04")){
			if(StringUtils.equals(trans_type, "01")){
				type = "21";
			}else if(StringUtils.equals(trans_type, "41")){
				type = "22";
			}else if(StringUtils.equals(trans_type, "02")){
				type = "23";
			}else if(StringUtils.equals(trans_type, "11")){
				type = "24";
			}else if(StringUtils.equals(trans_type, "51")){
				type = "25";
			}else if(StringUtils.equals(trans_type, "12")){
				type = "26";
			}else if(StringUtils.equals(trans_type, "52")){
				type = "27";
			}
		}
		return type;
	}
	

	public String getTrans_type_show_cht() {
		String str ="" ;
		if(! StringUtils.equals(card_type, "04")){
			if(StringUtils.equals(trans_type, "01")){
				str ="銷售";
			}else if(StringUtils.equals(trans_type, "41")){
				str ="取消";
			}else if(StringUtils.equals(trans_type, "02")){
				str ="退貨";
			}else if(StringUtils.equals(trans_type, "42")){
				str ="取消-退貨";
			}else if(StringUtils.equals(trans_type, "21")){
				str ="分期付款";
			}else if(StringUtils.equals(trans_type, "61")){
				str ="分期付款取消";
			}else if(StringUtils.equals(trans_type, "22")){
				str ="分期退貨";
			}else if(StringUtils.equals(trans_type, "62")){
				str ="取消-退貨-分期付款";
			}else if(StringUtils.equals(trans_type, "31")){
				str ="紅利折抵 ";
			}else if(StringUtils.equals(trans_type, "71")){
				str ="紅利折抵取消";
			}else if(StringUtils.equals(trans_type, "32")){
				str ="紅利折抵退貨";
			}else if(StringUtils.equals(trans_type, "72")){
				str ="取消-退貨-紅利抵扣";
			}else if(StringUtils.equals(trans_type, "04")){
				str ="小費交易";
			}else if(StringUtils.equals(trans_type, "11")){
				str ="預先授權";
			}else if(StringUtils.equals(trans_type, "03")){
				str ="交易補登";
			}else if(StringUtils.equals(trans_type, "43")){
				str ="取消-交易補登";
			}else if(StringUtils.equals(trans_type, "23")){
				str ="分期付款調帳";
			}else if(StringUtils.equals(trans_type, "63")){
				str ="取消-後台調帳-分期";
			}else if(StringUtils.equals(trans_type, "33")){
				str ="紅利抵扣調帳";
			}else if(StringUtils.equals(trans_type, "73")){
				str ="取消-後台調帳-紅利";
			}
		}else if(StringUtils.equals(card_type, "04")){
			if(StringUtils.equals(trans_type, "01")){
				str ="銷售";
			}else if(StringUtils.equals(trans_type, "41")){
				str ="取消";
			}else if(StringUtils.equals(trans_type, "02")){
				str ="退貨";
			}else if(StringUtils.equals(trans_type, "11")){
				str ="預先授權";
			}else if(StringUtils.equals(trans_type, "51")){
				str ="預先授權取消";
			}else if(StringUtils.equals(trans_type, "12")){
				str ="預先授權完成";
			}else if(StringUtils.equals(trans_type, "52")){
				str ="預先授權完成取消";
			}
		}
		return str;
	}
	
	public String getTrans_type_show_eng() {
		String str ="" ;
		if(! StringUtils.equals(card_type, "04")){
			if(StringUtils.equals(trans_type, "01")){
				str ="SALE";
			}else if(StringUtils.equals(trans_type, "41")){
				str ="VOID";
			}else if(StringUtils.equals(trans_type, "02")){
				str ="REFUND";
			}else if(StringUtils.equals(trans_type, "42")){
				str ="VOID REFUND";
			}else if(StringUtils.equals(trans_type, "21")){
				str ="SALE INSTALLMENT";
			}else if(StringUtils.equals(trans_type, "61")){
				str ="VOID INSTALLMENT";
			}else if(StringUtils.equals(trans_type, "22")){
				str ="REFUND INSTALLMENT";
			}else if(StringUtils.equals(trans_type, "62")){
				str ="VOID REFUND INST.";
			}else if(StringUtils.equals(trans_type, "31")){
				str ="SALE REDEMPTION";
			}else if(StringUtils.equals(trans_type, "71")){
				str ="VOID REDEMPTION";
			}else if(StringUtils.equals(trans_type, "32")){
				str ="REFUND REDEMPTION";
			}else if(StringUtils.equals(trans_type, "72")){
				str ="VOID REFUND REDEEM";
			}else if(StringUtils.equals(trans_type, "04")){
				str ="TIPS";
			}else if(StringUtils.equals(trans_type, "11")){
				str ="PREAUTH";
			}else if(StringUtils.equals(trans_type, "03")){
				str ="OFFLINE";
			}else if(StringUtils.equals(trans_type, "43")){
				str =" VOID OFFLINE";
			}else if(StringUtils.equals(trans_type, "23")){
				str ="ADJUST INSTALLMENT";
			}else if(StringUtils.equals(trans_type, "63")){
				str ="VOID ADJUST INST.";
			}else if(StringUtils.equals(trans_type, "33")){
				str ="ADJUST REDEMPTION";
			}else if(StringUtils.equals(trans_type, "73")){
				str ="VOID ADJUST REDEEM";
			}
		}else if(StringUtils.equals(card_type, "04")){
			if(StringUtils.equals(trans_type, "01")){
				str ="SALE";
			}else if(StringUtils.equals(trans_type, "41")){
				str ="VOID";
			}else if(StringUtils.equals(trans_type, "02")){
				str ="REFUND";
			}else if(StringUtils.equals(trans_type, "11")){
				str ="PREAUTH";
			}else if(StringUtils.equals(trans_type, "51")){
				str ="PREAUTH VOID";
			}else if(StringUtils.equals(trans_type, "12")){
				str ="PREAUTH COMPLETE";
			}else if(StringUtils.equals(trans_type, "52")){
				str ="PREAUTH COMPLETE VOID";
			}
		}
		return str;
	}

	public String getCard_type_show(String card_type) {
		String card_type_show;
		if(StringUtils.equals(card_type, "01")){
			return card_type_show = "VISA";
		}else if(StringUtils.equals(card_type, "02")){
			return card_type_show  = "MasterCard";
		}else if(StringUtils.equals(card_type, "03")){
			return card_type_show= "JCB";
		}else{
			return card_type_show= "CUP";
		}
	}
	
	public String getTx_date_time_show() {
		if(tx_date!=null&&tx_time!=null) {
			String dateTimeStr =tx_date.substring(0,4)+"/"+tx_date.substring(4, 6)+"/"+tx_date.substring(6, 8)+" "
					+tx_time.substring(0,2)+":"+tx_time.substring(2,4)+":"+tx_time.substring(4,6);
			return dateTimeStr;
		}
		return null;
	}
	
	
	public String getData_source() {
		return data_source;
	}

	public void setData_source(String data_source) {
		this.data_source = data_source;
	}
	
	public String getMaskCardNo(String card_no) {
		if (card_no.length() >= 10 && card_no != ""){
			int middle = card_no.length()-10;
			String middleStr = "";
			for(int i=0 ; i<middle ;i++){
				middleStr += "*" ;
			}
			return card_no.substring(0, 6)+middleStr+card_no.substring(card_no.length()-4, card_no.length());
	
		}
		return card_no;
	}
	
	
	public void setSignInfoType(String signInfoType) {
		this.signInfoType = signInfoType;
	}	

	public void setTrans_type_show_cht(String trans_type_show_cht) {
		this.trans_type_show_cht = trans_type_show_cht;
	}

	public void setTrans_type_show_eng(String trans_type_show_eng) {
		this.trans_type_show_eng = trans_type_show_eng;
	}

	public void setTx_date_time_show(String tx_date_time_show) {
		this.tx_date_time_show = tx_date_time_show;
	}

	public void setTx_amount_show(String tx_amount_show) {
		this.tx_amount_show = tx_amount_show;
	}
	public void setTip_amount_show(String tip_amount_show) {
		this.tip_amount_show = tip_amount_show;
	}
	
	public void setCard_type_show(String card_type_show) {
		this.card_type_show=card_type_show;
	}

	@Override
	public String toString() {
		return "TErmLog [trans_id=" + trans_id + ", total_packet_num=" + total_packet_num + ", total_packet_len="
				+ total_packet_len + ", card_no=" + card_no + ", tx_amount=" + tx_amount + ", stan=" + stan
				+ ", tx_date=" + tx_date + ", tx_time=" + tx_time + ", expire_date=" + expire_date + ", ref_no="
				+ ref_no + ", auth_code=" + auth_code + ", terminal_id=" + terminal_id + ", merchant_id=" + merchant_id
				+ ", store_id=" + store_id + ", tip_amount=" + tip_amount + ", batch_no=" + batch_no + ", invoice_no="
				+ invoice_no + ", inst_period=" + inst_period + ", inst_down_payment=" + inst_down_payment
				+ ", inst_payment=" + inst_payment + ", inst_fee=" + inst_fee + ", redeem_paid_amount="
				+ redeem_paid_amount + ", redeem_amount=" + redeem_amount + ", redeem_point=" + redeem_point
				+ ", redeem_balance_point=" + redeem_balance_point + ", card_type=" + card_type + ", trans_mode="
				+ trans_mode + ", trans_type=" + trans_type + ", unsign_flag=" + unsign_flag + ", check_no=" + check_no
				+ ", cup_stan=" + cup_stan + ", emv_tc=" + emv_tc + ", cardholder_name=" + cardholder_name
				+ ", chk_err_flag=" + chk_err_flag + ", log_date=" + log_date + ", data_source=" + data_source
				+ ", signInfoType=" + signInfoType + ", card_type_show=" + card_type_show + ", tx_date_time_show="
				+ tx_date_time_show + ", fmt1=" + fmt1 + ", tx_amount_show=" + tx_amount_show + ", tip_amount_show="
				+ tip_amount_show + ", fmt2=" + fmt2 + ", inst_down_payment_show=" + inst_down_payment_show
				+ ", inst_payment_show=" + inst_payment_show + ", inst_fee_show=" + inst_fee_show
				+ ", redeem_paid_amount_show=" + redeem_paid_amount_show + ", redeem_amount_show=" + redeem_amount_show
				+ ", redeem_point_show=" + redeem_point_show + ", redeem_balance_point_show="
				+ redeem_balance_point_show + "]";
	}
	
	
	

	

}
