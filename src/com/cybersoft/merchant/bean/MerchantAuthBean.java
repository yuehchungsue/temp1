package com.cybersoft.merchant.bean;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;

/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import java.util.Hashtable;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import com.cybersoft4u.prj.tfbpg.api.SSLServer;
import com.cybersoft4u.prj.tfbpg.bean.ParamBean;
import com.fubon.security.filter.SecurityTool;
import com.cybersoft.bean.LogUtils;

public class MerchantAuthBean
{
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    public MerchantAuthBean()
    {
    }

//    /**
//     * ���o�S���D��
//     * @param String MerchantID �S���N��
//     * @return Hashtable        �S���D�ɸ��
//     */
//    public Hashtable get_Merchant(String MerchantID,String SubMID)
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        Hashtable tableResult=new Hashtable();
//        StringBuffer sb = new StringBuffer();
//        sb.append(" SELECT ");
//        sb.append(" CURRENTCODE,SLOTTYPE, ");
//        sb.append(" SUPPORT_EC_AUTH, ");
//        sb.append(" PERMIT_REDEM_SALE,PERMIT_INSTALL_SALE,PERMIT_SALE, ");
//        sb.append(" SUPPORT_VISA,SUPPORT_MASTER,SUPPORT_JCBI,SUPPORT_UCARD,SUPPORT_AMEX, ");
//        sb.append(" FORCE_CVV2 ");
//        sb.append(" From MERCHANT WHERE MERCHANTID = '" +MerchantID+"' AND SUBMID = '" +SubMID+"' ");
//        //System.out.println("Sql="+sb.toString());
//
//        try
//        {
//            ArrayList listResult = (ArrayList) SysBean.executeSQL(SecurityTool.output(sb.toString()),"select");
//            if(listResult==null || listResult.size()==0)
//                return tableResult;
//            else
//                tableResult = (Hashtable) listResult.get(0);
//        }
//        catch(Exception ex)
//        {
//          System.out.println(ex.getMessage());
//          log_systeminfo.debug(ex.toString());
//        }
//
//        return tableResult;
//    }

//    /* Override get_Merchant with DataBaseBean parameter */
//    public Hashtable get_Merchant(DataBaseBean SysBean, String MerchantID,String SubMID)
//    {
//        // DataBaseBean SysBean = new DataBaseBean();
//        Hashtable tableResult=new Hashtable();
//        StringBuffer sb = new StringBuffer();
//        sb.append(" SELECT ");
//        sb.append(" CURRENTCODE,SLOTTYPE, ");
//        sb.append(" SUPPORT_EC_AUTH, ");
//        sb.append(" PERMIT_REDEM_SALE,PERMIT_INSTALL_SALE,PERMIT_SALE, ");
//        sb.append(" SUPPORT_VISA,SUPPORT_MASTER,SUPPORT_JCBI,SUPPORT_UCARD,SUPPORT_AMEX, ");
//        sb.append(" FORCE_CVV2 ");
//        sb.append(" From MERCHANT WHERE MERCHANTID = '" +MerchantID+"' AND SUBMID = '" +SubMID+"' ");
//        //System.out.println("Sql="+sb.toString());
//
//        try
//        {
//            ArrayList listResult = (ArrayList) SysBean.executeSQL(SecurityTool.output(sb.toString()),"select");
//            if(listResult==null || listResult.size()==0)
//                return tableResult;
//            else
//                tableResult = (Hashtable) listResult.get(0);
//        }
//        catch(Exception ex)
//        {
//          System.out.println(ex.getMessage());
//          log_systeminfo.debug(ex.toString());
//        }
//
//        return tableResult;
//    }

//    /**
//     * ���o�ݥ����D��
//     * @param String MerchantID �S���N��
//     * @param String TerminalID �׺ݾ��N��
//     * @return ArrayList        �S���ݥ������
//     */
//    public Hashtable get_Terminal(String MerchantID,String TerminalID)
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        Hashtable tableResult=new Hashtable();
//        String Sql = "Select * From TERMINAL WHERE MERCHANTID = '" + MerchantID+"' AND TerminalID =  '"+TerminalID+"' ";
//        // System.out.println("Sql="+Sql);
//
//        try
//        {
//            ArrayList listResult = (ArrayList) SysBean.executeSQL(Sql,"select");
//            if(listResult==null || listResult.size()==0)
//                return tableResult;
//            else
//                tableResult = (Hashtable) listResult.get(0);
//        }
//        catch(Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//
//        return tableResult;
//    }

//    /* Override get_Terminal with DataBaseBean parameter */
//    public Hashtable get_Terminal(DataBaseBean SysBean, String MerchantID,String TerminalID)
//    {
//        // DataBaseBean SysBean = new DataBaseBean();
//        Hashtable tableResult=new Hashtable();
//        String Sql = "Select * From TERMINAL WHERE MERCHANTID = '" + MerchantID+"' AND TerminalID =  '"+TerminalID+"' ";
//        // System.out.println("Sql="+Sql);
//
//        try
//        {
//            ArrayList listResult = (ArrayList) SysBean.executeSQL(Sql, "select");
//            if(listResult==null || listResult.size()==0)
//                return tableResult;
//            else
//                tableResult = (Hashtable) listResult.get(0);
//        }
//        catch(Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//
//        return tableResult;
//    }

    /**
     * ���ͥ�����
     * @return String TransDate  ������
     */
     public String get_TransDate(String Type) 
     {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        calendar.add(Calendar.DATE, 0);
        SimpleDateFormat sdf = new SimpleDateFormat(Type);
        String NowTime = sdf.format(calendar.getTime());
     
        return NowTime;
     }
}
