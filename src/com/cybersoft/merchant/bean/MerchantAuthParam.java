package com.cybersoft.merchant.bean;

import java.util.Hashtable;

/**
 * @author nancywu
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 * 2007/11/02 add String socialId ,  public void setgetSocialID(String socialId),  String getSocialID()
 */
public class MerchantAuthParam 
{
    private String merchantId;
    private String subMID;
    private String terminalId;
    private String orderId;
    private int transMode;
    private double transAmt;
    private int install;
    private String notifyURL;
    private String transDate;
    private String transTime;
    private String pan;
    private String cvv2;
    private String expireDate;
    private boolean forcecvv2;
    private Hashtable tableMPermit;
    private String resCode;
    private String resMsg;
    private String socialId;

    public void setResCode(String rcode) 
    {
        resCode = rcode;
    }
    
    public String getResCode() 
    {
        return resCode;
    }
    
    public void setResMsg(String rmsg) 
    {
        resMsg = rmsg;
    }
    
    public String getResMsg() 
    {
        return resMsg;
    }
    
    public void setMPermit(Hashtable tablemp) 
    {
        tableMPermit = tablemp;
    }
    
    public Hashtable getMPermit() 
    {
        return tableMPermit;
    }
    
    public void setForceCVV2(boolean fcvv2) 
    {
        forcecvv2 = fcvv2;
    }
    
    public boolean getForceCVV2() 
    {
        return forcecvv2;
    }
    
    public void setPan(String ipan)
    {
        pan = ipan;
    }
    
    public String getPan() 
    {
        return pan;
    }
    
    public void setCVV2(String icvv2) 
    {
        cvv2 = icvv2;
    }
    
    public String getCVV2() 
    {
        return cvv2;
    }
    
    public void setExpireDate(String iexpDate) 
    {
        expireDate = iexpDate;
    }
    
    public String getExpireDate() 
    {
        return expireDate;
    }
    
    public void setTransDate(String tnsDate) 
    {
        transDate = tnsDate;
    }
    
    public String getTransDate() 
    {
        return transDate;
    }
    
    public void setTransTime(String tnsTime) 
    {
        transTime = tnsTime;
    }
    
    public String getTransTime() 
    {
        return transTime;
    }

    public void setMerchantID(String mid) 
    {
        merchantId = mid;
    }
    
    public String getMerchantID() 
    {
        return merchantId;
    }
    
    public void setSubMID(String smid) 
    {
        subMID = smid;
    }
    
    public String getSubMID() 
    {
        return subMID;
    }
    
    public void setTerminalID(String tid) 
    {
        terminalId = tid;
    }
    
    public String getTerminalID() 
    {
        return terminalId;
    }
    
    public void setOrderID(String oid) 
    {
        orderId = oid;
    }
    
    public String getOrderID() 
    {
        return orderId;
    }
    
    public void setTransMode(int tnsmode) 
    {
        transMode = tnsmode;
    }
    
    public int getTransMode() 
    {
        return transMode;
    }
    
    public void setTransAmt( double tnsamt) 
    {
        transAmt = tnsamt;
    }
    
    public double getTransAmt() 
    {
        return transAmt;
    }
    
    public void setInstall(int ins) 
    {
        install = ins;
    }
    
    public int getInstall() 
    {
        return install;
    }
    
    public void setNotifyURL(String url) 
    {
        notifyURL = url;
    }
    
    public String getNotifyURL() 
    {
        return notifyURL;
    }
    
    public void setSocialID(String Id) 
    {
            socialId = Id;
    }
    
    public String getSocialID() 
    {
            return socialId;
    }
}
