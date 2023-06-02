/************************************
 * 2018.01.22 HKP change JCE to jasypt
 * 20200306 202002100619-00 saint EC退貨檢核
 * 202208090854-01 20220801 HKP PCI-DSS更換密碼雜錯MD5為SHA256，MD5雜錯長度為32，SHA256雜錯長度為64，以此長度判斷是否為MD5，變更後的密碼一律使用SHA256與LOG遮蔽
 * */
package com.cybersoft.common;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.jasypt.util.text.*;

public class Util {
	static private String mfactor="myfactorvalue";
	static public String decrypt(String cipherText){
		if(cipherText == null) cipherText = "";
		org.jasypt.util.text.BasicTextEncryptor en = new BasicTextEncryptor();
		en.setPassword(mfactor);
		return en.decrypt(cipherText);
	}
	static public String encrypt(String cipherText){
		if(cipherText == null) cipherText = "";
		org.jasypt.util.text.BasicTextEncryptor en = new BasicTextEncryptor();
		en.setPassword(mfactor);
		return en.encrypt(cipherText);
	}
	static public String objToStr(Object obj ){
		return (obj != null)? obj.toString():"";
	}
	static public String objToStrTrim(Object obj ){
		return (obj != null)? obj.toString().trim():"";
	}
	
	static public int objToInt(Object obj ){
		try{
			if(obj==null)return 0;
			else return Integer.parseInt(obj.toString());
		}catch(Exception ex){
 			System.out.println("objToInt(),data="+obj.toString());
			System.out.println(ex.getMessage());
			return 0;
		}		
	}
	static public double objToDouble(Object obj){
		if(obj==null) return 0;
		return strToDouble(obj.toString());
	}
	static public double strToDouble(String data){
		try{
			if(data==null) return 0;
			return Double.parseDouble(data);
		}catch(Exception ex){
 			System.out.println("getDouble(),data="+data);
			System.out.println(ex.getMessage());
			return 0;
		}
	}
	static public long objToLong(Object obj ){
		try{
			if(obj==null)return 0;
			else return Long.parseLong(obj.toString());
		}catch(Exception ex){
 			System.out.println("objToLong(),data="+obj.toString());
			System.out.println(ex.getMessage());
			return 0;
		}		
	}
	
	static public String lpad(String strp, String chr, int cnt){
		String tmpstr = "";
		if (strp == null)
			strp = "";
	    if (strp.length() < cnt) {
	      for (int i = 0; i < cnt - strp.length(); i++) {
	        tmpstr = tmpstr + chr;
	      }
	      strp = tmpstr + strp;
	      return strp;
	    }
	    return strp;
	  }

	static public String rpad(String strp, String chr, int cnt){
	    if (strp == null)
	      strp = "";
	    String tmpstr = strp;
	    if (strp.length() < cnt) {
	      for (int i = 0; i < cnt - strp.length(); i++) {
	        tmpstr = tmpstr + chr;
	      }
	      return tmpstr;
	    }
	    return tmpstr;
	}
	//20220801
	public final static String getPwdfactor(String sUserId,String sPermitCode) {
		return sUserId+(sUserId.length()*19)+sPermitCode;
	}
	//20220801
    public final static String SHA256(String decript) {
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("SHA-256");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
	static public String maskCardNo(String sCardNo){
		if(sCardNo == null) return "";
		int iLen = sCardNo.length();
		if(iLen>=2 && iLen<16) {
			return rpad(sCardNo.substring(0,iLen/2),"X",iLen); //遮一半
		}else if(iLen>=16) {
			return sCardNo.substring(0,6)+"XXXXXX"+sCardNo.substring(12);
		}else {
			return sCardNo;
		}
	}
	static public String maskCustId(String sCustId){
		if(sCustId == null) return "";
		int iLen = sCustId.length(); 
		if(iLen >=2 && iLen<10) {
			return rpad(sCustId.substring(0,iLen/2),"X",iLen); //遮一半
		}else if(iLen>=10) {
			return sCustId.substring(0,4)+"XXX"+sCustId.substring(7);
		}else {
			return sCustId;
		}
	}

}
