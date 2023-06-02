/************************************************************
 * <p>#File Name:       MerchantChangePwdBean.java  </p>
 * <p>#Description:                 </p>
 * <p>#Create Date:     2007/09/26      </p>
 * <p>#Company:         cybersoft       </p>
 * <p>#Notice:                      </p>
 * @author      Caspar Chen
 * @since       SPEC version
 * @version 0.1 2007/09/26  Caspar Chen
 ************************************************************/
package com.cybersoft.merchant.bean;

/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import java.util.ArrayList;
import com.cybersoft.bean.LogUtils;
import com.cybersoft.bean.emDataType;

public class MerchantChangePwdBean
{
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    public MerchantChangePwdBean()
    {
    }

    /**
     * 取得特約商店使用者資料
     * @param String MerchantID 特約商店代號
     * @param String UserID     使用者代號
     * @return ArrayList        使用者密碼
     */
    public ArrayList get_Merchant_User_PWD(String MerchantID, String UserID)
    {
        DataBaseBean2 sysBean = new DataBaseBean2();
        ArrayList arrayData = new ArrayList();

        StringBuffer sSQLSB = new StringBuffer();
		sysBean.ClearSQLParam();
		sSQLSB.append("Select * From MERCHANT_USER WHERE MERCHANT_ID = ? AND USER_ID = ? ");
        // System.out.println("Sql=" + Sql);
		
		sysBean.AddSQLParam(emDataType.STR, MerchantID);
		sysBean.AddSQLParam(emDataType.STR, UserID);

        try
        {
        	/** 2023/05/23 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-079 */
            arrayData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }

        return arrayData;
    }

//    /* Override get_Merchant_User_PWD with DataBaseBean parameter */
//    public ArrayList get_Merchant_User_PWD(DataBaseBean SysBean, String MerchantID, String UserID)
//    {
//        // DataBaseBean SysBean = new DataBaseBean();
//        ArrayList arrayData = new ArrayList();
//
//        String Sql = "Select * From MERCHANT_USER WHERE MERCHANT_ID = '"+ MerchantID + "' AND USER_ID='"+UserID+"' ";
//        // System.out.println("Sql=" + Sql);
//
//        try
//        {
//            arrayData = (ArrayList) SysBean.executeSQL(Sql, "select");
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//
//        return arrayData;
//    }

    /**
     * 修改特約商店使用者密碼
     * @param String MerchantID 特約商店代號
     * @param String UserID     使用者代號
     * @param String Pwd        密碼
     * @param String PwdCnt     密碼更新指標
     * @param String PwdHisName 密碼歷史資料欄位名稱
     * @param String ExpireDate 密碼有效期限
     * @return boolean
     */
    public boolean updateDb_UserPwd(String MerchantID, String UserID, String Pwd, String PwdCnt, String PwdHisName, String ExpireDate)
    {
       DataBaseBean2 sysBean = new DataBaseBean2();
       boolean flag = false;

		StringBuffer sSQLSB = new StringBuffer();
		sysBean.ClearSQLParam();
		sSQLSB.append(" UPDATE MERCHANT_USER ");
		sSQLSB.append(" SET ");
		sSQLSB.append(" USER_PWD = ?, ");
		sSQLSB.append(" PWD_CNT = ?, ");
		sSQLSB.append(" " + PwdHisName + " = ?, ");
		sSQLSB.append(" EXPIRE_DATE = SYSDATE + " + ExpireDate +", ");
		sSQLSB.append(" USER_CHANGERPWD_DATE = SYSDATE, ");
		sSQLSB.append(" USER_STATUS = 'Y', ");
		sSQLSB.append(" ENF_UPDPWD = 'N' ");
		sSQLSB.append(" WHERE MERCHANT_ID = ? AND USER_ID = ? ");
		
		sysBean.AddSQLParam(emDataType.STR, Pwd);
		sysBean.AddSQLParam(emDataType.STR, PwdCnt);
		//sysBean.AddSQLParam(emDataType.STR, PwdHisName);
		sysBean.AddSQLParam(emDataType.STR, Pwd);
		//sysBean.AddSQLParam(emDataType.STR, ExpireDate);
		sysBean.AddSQLParam(emDataType.STR, MerchantID);
		sysBean.AddSQLParam(emDataType.STR, UserID);

       // System.out.println("Sql=" + Sql);
        ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/05/23 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-080 */
        	arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        
        System.out.println("arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
            flag = true;
        }
        
       return flag;
    }

//    /* Override updateDb_UserPwd with DataBaseBean parameter */
//    public boolean updateDb_UserPwd(DataBaseBean SysBean, String MerchantID, String UserID, String Pwd, String PwdCnt, String PwdHisName, String ExpireDate)
//    {
//       // DataBaseBean SysBean = new DataBaseBean();
//       boolean boolIsEqual = false;
//
//       String Sql =
//               " UPDATE MERCHANT_USER "
//               + " SET "
//               + " USER_PWD ='" + Pwd + "', "
//               + " PWD_CNT ='" + PwdCnt + "', "
//               + " " + PwdHisName + " ='" + Pwd + "', "
//               + " EXPIRE_DATE = SYSDATE + " + ExpireDate +", "
//               + " USER_CHANGERPWD_DATE = SYSDATE, "
//               + " USER_STATUS = 'Y', "
//               + " ENF_UPDPWD = 'N' "
//               + " WHERE MERCHANT_ID = '" + MerchantID + "' AND USER_ID='" + UserID + "' ";
//
//       // System.out.println("Sql=" + Sql);
//       try
//       {
//          boolIsEqual = ((Boolean) SysBean.executeSQL(Sql, "update")).booleanValue();
//       }
//       catch (Exception ex)
//       {
//          System.out.println(ex.getMessage());
//          log_systeminfo.debug(ex.toString());
//       }
//       return boolIsEqual;
//    }
}
