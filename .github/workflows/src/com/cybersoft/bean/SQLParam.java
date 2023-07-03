/**���ʻ���
 * 2020.03.06 HKP Add DB�s�u�ѼƤƧK��SQL INJECTION
 * 202208090854-01 20220801 HKP PCI-DSS�󴫱K�X����MD5��SHA256�AMD5�������׬�32�ASHA256�������׬�64�A�H�����קP�_�O�_��MD5�A�ܧ�᪺�K�X�@�ߨϥ�SHA256�PLOG�B��
 * */
package com.cybersoft.bean;
import java.math.BigDecimal;
import java.sql.Date;
import com.cybersoft.bean.emDataType;

public class SQLParam {
	//private emDataType mDataType=emDataType.STR;
	private String mDataType=emDataType.STR;
	private int mValue_i;
	private String mValue_s;
	private Date mValue_dat;
	private double mValue_d;
	private long mValue_l;
	private BigDecimal mValue_bd;
	/* DataType=INT,STR,DATE,DECIMAL,LONG
	 * */
	public SQLParam(String DataType,int oValue){
		mDataType = DataType;
		mValue_i = oValue;
	}
	public SQLParam(String DataType,String oValue){
		mDataType = DataType;
		mValue_s = oValue;
	}
	//�������
	public SQLParam(String DataType,String oValue,int iDataLength){
    	if(oValue == null) oValue="";
    	if(oValue.length()>iDataLength) oValue = oValue.substring(0,iDataLength);
		mDataType = DataType;
		mValue_s = oValue;
	}
	public SQLParam(String DataType,Date oValue){
		mDataType = DataType;
		mValue_dat = oValue;
	}
	public SQLParam(String DataType,double oValue){
		mDataType = DataType;
		mValue_d = oValue;
	}
	public SQLParam(String DataType,long oValue){
		mDataType = DataType;
		mValue_l = oValue;
	}
	public SQLParam(String DataType,BigDecimal oValue){
		mDataType = DataType;
		mValue_bd = oValue;
	}
	public String getType(){
		return mDataType;
	}
	public int getValueInt(){
		return mValue_i;
	}
	public String getValueString(){
		return mValue_s;
	}
	public Date getValueDate(){
		return mValue_dat;
	}
	public double getValueDouble(){
		return mValue_d;
	}
	public long getValueLong(){
		return mValue_l;
	}
	public BigDecimal getValueBigDecimal() {
		return mValue_bd;
	}	
}
