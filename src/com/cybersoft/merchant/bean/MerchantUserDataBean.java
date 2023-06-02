/************************************************************
 * 異動說明
 * 202208090854-01 20220801 HKP PCI-DSS更換密碼雜錯MD5為SHA256，MD5雜錯長度為32，SHA256雜錯長度為64，以此長度判斷是否為MD5，變更後的密碼一律使用SHA256與LOG遮蔽
 ************************************************************/
package com.cybersoft.merchant.bean;

import java.util.ArrayList;
import java.util.Hashtable;
/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft.bean.LogUtils;
import com.cybersoft.bean.emDataType;
public class MerchantUserDataBean
{
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    public MerchantUserDataBean()
    {
        super();
    }

//    /**
//     * 取得特約商店使用者資料
//     * @return ArrayList        使用者資料
//     */
//    public ArrayList get_Merchant_User()
//    {
//        DataBaseBean2 sysBean = new DataBaseBean2();
//        ArrayList arrayData = new ArrayList();
//
//        String Sql = "SELECT *  FROM MERCHANT_USER ORDER BY USER_ID ";
//
//        // System.out.println("Sql=" + Sql);
//        try
//        {
//            arrayData = (ArrayList) SysBean.executeSQL(Sql, "select");
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        return arrayData;
//    }

//    /* Override get_Merchant_User with DataBaseBean parameter */
//    public ArrayList get_Merchant_User(DataBaseBean SysBean)
//    {
//        // DataBaseBean SysBean = new DataBaseBean();
//        ArrayList arrayData = new ArrayList();
//
//        String Sql = "SELECT *  FROM MERCHANT_USER ORDER BY USER_ID ";
//
//        // System.out.println("Sql=" + Sql);
//        try
//        {
//            arrayData = (ArrayList) SysBean.executeSQL(Sql, "select");
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        return arrayData;
//    }

//    /**
//     * 取得特約商店使用者資料
//     * @param String MerchantID 特約商店代號
//     * @return ArrayList        使用者資料
//     */
//    public ArrayList get_Merchant_User(String MerchantID)
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        ArrayList arrayData = new ArrayList();
//
//        String Sql = "SELECT * FROM MERCHANT_USER WHERE MERCHANT_ID = '" + MerchantID + "' ORDER BY USER_ID";
//        // System.out.println("Sql=" + Sql);
//        try
//        {
//            arrayData = (ArrayList) SysBean.executeSQL(Sql, "select");
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        return arrayData;
//    }

    /* Override get_Merchant_User with DataBaseBean parameter */
    public ArrayList get_Merchant_User(DataBaseBean2 sysBean, String MerchantID)
    {
        // DataBaseBean SysBean = new DataBaseBean();
        ArrayList arrayData = new ArrayList();

        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("SELECT * FROM MERCHANT_USER WHERE MERCHANT_ID = ? ORDER BY USER_ID");		
		sysBean.AddSQLParam(emDataType.STR, MerchantID);
		
        // System.out.println("Sql=" + Sql);
        try
        {
        	/** 2023/05/16 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-058 */
            arrayData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }
        return arrayData;
    }

//    /**
//     * 取得特約商店使用者資料
//     * @param String MerchantID 特約商店代號
//     * @param String UserID     使用者代號
//     * @return ArrayList        使用者資料
//     */
//    public ArrayList get_Merchant_User(String MerchantID, String UserID)
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        ArrayList arrayData = new ArrayList();
//
//        String Sql = "SELECT * FROM MERCHANT_USER WHERE MERCHANT_ID = '" + MerchantID + "' AND USER_ID='" + UserID + "' ORDER BY USER_ID";
//
//        // System.out.println("Sql=" + Sql);
//        try
//        {
//            arrayData = (ArrayList) SysBean.executeSQL(Sql, "select");
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        return arrayData;
//    }

    /* Override get_Merchant_User with DataBaseBean parameter */
    public ArrayList get_Merchant_User(DataBaseBean2 sysBean, String MerchantID, String UserID)
    {
        // DataBaseBean SysBean = new DataBaseBean();
        ArrayList arrayData = new ArrayList();

        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("SELECT * FROM MERCHANT_USER WHERE MERCHANT_ID = ? AND USER_ID = ? ORDER BY USER_ID");
		sysBean.AddSQLParam(emDataType.STR, MerchantID);
		sysBean.AddSQLParam(emDataType.STR, UserID);

        // System.out.println("Sql=" + Sql);
        try
        {
        	/** 2023/05/16 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-059 */
            arrayData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }
        return arrayData;
    }

//    /**
//     * 取得特約商店使用者資料
//     * @param String delflag    刪除註記
//     * @return ArrayList        使用者資料
//     */
//    public ArrayList get_Merchant_User_delFlag(String delflag)
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        ArrayList arrayData = new ArrayList();
//
//        String Sql = "SELECT * FROM MERCHANT_USER WHERE DEL_FLAG ='" + delflag + "' ORDER BY USER_ID";
//
//        // System.out.println("Sql=" + Sql);
//        try
//        {
//            arrayData = (ArrayList) SysBean.executeSQL(Sql, "select");
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        return arrayData;
//    }

//    /* Override get_Merchant_User_delFlag with DataBaseBean parameter */
//    public ArrayList get_Merchant_User_delFlag(DataBaseBean SysBean, String delflag)
//    {
//        // DataBaseBean SysBean = new DataBaseBean();
//        ArrayList arrayData = new ArrayList();
//
//        String Sql = "SELECT * FROM MERCHANT_USER WHERE DEL_FLAG ='" + delflag + "' ORDER BY USER_ID";
//
//        // System.out.println("Sql=" + Sql);
//        try
//        {
//            arrayData = (ArrayList) SysBean.executeSQL(Sql, "select");
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        return arrayData;
//    }


//    /**
//     * 取得特約商店使用者資料
//     * @param String MerchantID 特約商店代號
//     * @param String delflag    刪除註記
//     * @return ArrayList        使用者資料
//     */
//    public ArrayList get_Merchant_User_delFlag(String MerchantID, String delflag)
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        ArrayList arrayData = new ArrayList();
//
//        String Sql = "SELECT * FROM MERCHANT_USER WHERE MERCHANT_ID = '" + MerchantID + "'  AND DEL_FLAG ='" + delflag + "' ORDER BY USER_ID";
//
//        // System.out.println("Sql=" + Sql);
//        try
//        {
//            arrayData = (ArrayList) SysBean.executeSQL(Sql, "select");
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        return arrayData;
//    }

    /* Override get_Merchant_User_delFlag with DataBaseBean parameter */
    public ArrayList get_Merchant_User_delFlag(DataBaseBean2 sysBean, String MerchantID, String delflag)
    {
        // DataBaseBean SysBean = new DataBaseBean();
        ArrayList arrayData = new ArrayList();

        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("SELECT A.* ,(SELECT MERCHANTCALLNAME FROM MERCHANT B WHERE A.SUBMID = B.SUBMID AND ROWNUM=1) AS MERCHANTCALLNAME FROM MERCHANT_USER A WHERE MERCHANT_ID = ?  AND DEL_FLAG = ? ORDER BY USER_ID");
		sysBean.AddSQLParam(emDataType.STR, MerchantID);
		sysBean.AddSQLParam(emDataType.STR, delflag);

        // System.out.println("Sql=" + Sql);
        try
        {
        	/** 2023/05/16 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-059-1 */
            arrayData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }
        return arrayData;
    }

//    /**
//     * 取得特約商店使用者資料
//     * @param String MerchantID 特約商店代號
//     * @param String UserID     使用者代號
//     * @param String delflag    刪除註記
//     * @return ArrayList        使用者資料
//     */
//    public ArrayList get_Merchant_User_delFlag(String MerchantID, String UserID, String delflag)
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        ArrayList arrayData = new ArrayList();
//
//        String Sql = "SELECT * FROM MERCHANT_USER  WHERE MERCHANT_ID = '" + MerchantID + "' AND USER_ID='" + UserID + "' AND  DEL_FLAG ='" + delflag + "' ORDER BY USER_ID";
//        // System.out.println("Sql=" + Sql);
//        try
//        {
//            arrayData = (ArrayList) SysBean.executeSQL(Sql, "select");
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        return arrayData;
//    }

    /* Override get_Merchant_User_delFlag with DataBaseBean parameter */
    public ArrayList get_Merchant_User_delFlag(DataBaseBean2 sysBean, String MerchantID, String UserID, String delflag)
    {
        // DataBaseBean SysBean = new DataBaseBean();
        ArrayList arrayData = new ArrayList();

        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("SELECT * FROM MERCHANT_USER  WHERE MERCHANT_ID = ? AND USER_ID = ? AND  DEL_FLAG = ? ORDER BY USER_ID ");
		sysBean.AddSQLParam(emDataType.STR, MerchantID);
		sysBean.AddSQLParam(emDataType.STR, UserID);
		sysBean.AddSQLParam(emDataType.STR, delflag);
		
        // System.out.println("Sql=" + Sql);
        try
        {
        	/** 2023/05/16 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-060 */
            arrayData = (ArrayList) (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }
        return arrayData;
    }
    /* Override get_Merchant_User_delFlag with DataBaseBean parameter */
    public ArrayList get_Merchant_User_delFlag(DataBaseBean2 sysBean, String MerchantID, String UserID,String SubMid, String delflag)
    {
        // DataBaseBean SysBean = new DataBaseBean();
        ArrayList arrayData = new ArrayList();

        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("SELECT  A.* ,(SELECT MERCHANTCALLNAME FROM MERCHANT B WHERE A.SUBMID = B.SUBMID AND ROWNUM=1) AS MERCHANTCALLNAME FROM MERCHANT_USER A WHERE A.MERCHANT_ID = '" + MerchantID + "' ");
		
        if(!UserID.equals("")) {
        	sSQLSB.append("  AND A.USER_ID = ? ");
        	sysBean.AddSQLParam(emDataType.STR, UserID);
        }
        
        if(!SubMid.equals("")&& !SubMid.equalsIgnoreCase("all")) {
        	sSQLSB.append("  AND A.SUBMID = ? ");
        	sysBean.AddSQLParam(emDataType.STR, SubMid);
        }
        	
        sSQLSB.append("   AND  A.DEL_FLAG = ? ORDER BY A.USER_ID");
        sysBean.AddSQLParam(emDataType.STR, delflag);
        
        // System.out.println("Sql=" + Sql);
        try
        {
        	/** 2023/05/16 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-061 */
            arrayData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }
        return arrayData;
    }
//    /**
//     * 新增特約商店使用者資料
//     * @param String MerchantID 特約商店代號
//     * @param String UserID     使用者代號
//     * @param String UserName   使用者名稱
//     * @param String UserPwss    使用者密碼
//     * @param String UserStatus 使用者狀態
//     * @param String ExpireDate 密碼有效期限
//     * @param String DepName    部門名稱
//     * @param String UserUpdId  維護人員
//     * @return boolean
//     */
//    public boolean insert_Merchant_User(String MerchantID, String UserID, String UserName, String UserPwss,
//                                        String UserStatus, String ExpireDate, String DepName, String UserUpdId)
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        boolean boolIsEqual = false;
//
//        String Sql =
//            "insert into "
//            + " MERCHANT_USER "
//            + " ( "
//            + " MERCHANT_ID, USER_ID, "
//            + " USER_NAME, USER_PWD, "
//            + " USER_STATUS, DEP_NAME, "
//            + " USER_UPD_ID, "
//            + " DEL_FLAG, ENF_UPDPWD, USER_PWD_ERRCNT, PWD_CNT, "
//            + " EXPIRE_DATE, USER_INSERT_DATE, USER_UPDATE_DATE, USER_CHANGERPWD_DATE"
//            + " ) "
//            + " values "
//            + " ( "
//            + " '" + MerchantID + "', "
//            + " '" + UserID + "', "
//            + " '" + UserName + "', "
//            + " '" + UserPwss + "', "
//            + " '" + UserStatus + "', "
//            + " '" + DepName + "', "
//            + " '" + UserUpdId + "', "
//            + " 'N', "
//            + " 'Y', "
//            + " 0, "
//            + " 0, "
//            + " SYSDATE + "+ExpireDate+", "
//            + " SYSDATE, "
//            + " SYSDATE, "
//            + " SYSDATE"
//            + " ) ";
//
//        System.out.println("Sql=" + Sql);
//        try
//        {
//            boolIsEqual = ((Boolean) SysBean.executeSQL(Sql, "insert")).booleanValue();
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        return boolIsEqual;
//    }

    /* Override insert_Merchant_User with DataBaseBean parameter */
    public boolean insert_Merchant_User(DataBaseBean2 sysBean, String MerchantID, String UserID, String UserName, String UserPwss,
                                        String UserStatus, String ExpireDate, String DepName, String UserUpdId,String SubMid,String strSessionSIGN_BILL)
    {
        // DataBaseBean SysBean = new DataBaseBean();
        boolean flag = false;

        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("insert into ");
        sSQLSB.append(" MERCHANT_USER ");
        sSQLSB.append(" ( ");
        sSQLSB.append(" MERCHANT_ID, USER_ID, ");
        sSQLSB.append(" USER_NAME, USER_PWD, ");
        sSQLSB.append(" USER_STATUS, DEP_NAME, ");
        sSQLSB.append(" USER_UPD_ID, ");
        sSQLSB.append(" DEL_FLAG, ENF_UPDPWD, USER_PWD_ERRCNT, PWD_CNT, ");
        sSQLSB.append(" EXPIRE_DATE, USER_INSERT_DATE, USER_UPDATE_DATE, USER_CHANGERPWD_DATE,SUBMID,SIGN_BILL");
        sSQLSB.append(" ) ");
        sSQLSB.append(" values ");
        sSQLSB.append(" ( ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" 'N', ");
        sSQLSB.append(" 'Y', ");
        sSQLSB.append(" 0, ");
        sSQLSB.append(" 0, ");
        sSQLSB.append(" SYSDATE + "+ExpireDate+", ");
        sSQLSB.append(" SYSDATE, ");
        sSQLSB.append(" SYSDATE, ");
        sSQLSB.append(" SYSDATE,");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ? ");
        sSQLSB.append(" ) ");
        
        sysBean.AddSQLParam(emDataType.STR, MerchantID);
        sysBean.AddSQLParam(emDataType.STR, UserID);
        sysBean.AddSQLParam(emDataType.STR, UserName);
        sysBean.AddSQLParam(emDataType.STR, UserPwss);
        sysBean.AddSQLParam(emDataType.STR, UserStatus);
        sysBean.AddSQLParam(emDataType.STR, DepName);
        sysBean.AddSQLParam(emDataType.STR, UserUpdId);
        sysBean.AddSQLParam(emDataType.STR, SubMid);
        sysBean.AddSQLParam(emDataType.STR, strSessionSIGN_BILL);

        // System.out.println("Sql=" + Sql);
		ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/05/16 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-061 */
        	arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }

        System.out.println("arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
        	flag = true;
        }
        
        return flag;
    }

//    /**
//     * 修改特約商店使用者資料
//     * @param String MerchantID 特約商店代號
//     * @param String UserID     使用者代號
//     * @param String UserName   使用者名稱
//     * @param String DepName    部門名稱
//     * @param String UserUpdId  維護人員
//     * @return boolean
//     */
//    public boolean update_Merchant_User(String MerchantID, String UserID, String UserName, String DepName, String UserUpdId)
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        boolean boolIsEqual = false;
//
//        String Sql =
//            " update "
//            + " MERCHANT_USER "
//            + " set "
//            + " DEP_NAME = '" + DepName + "', "
//            + " USER_UPD_ID= '" + UserUpdId + "', "
//            + " USER_NAME = '" + UserName + "', "
//            + " USER_UPDATE_DATE = SYSDATE "
//            + " where "
//            + " MERCHANT_ID = '" + MerchantID + "' "
//            + " and "
//            + " USER_ID ='" + UserID + "' ";
//
//        // System.out.println("Sql=" + Sql);
//        try
//        {
//            boolIsEqual = ((Boolean) SysBean.executeSQL(Sql, "update")).booleanValue();
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        return boolIsEqual;
//    }

    /* Override update_Merchant_User with DataBaseBean parameter */
    public boolean update_Merchant_User(DataBaseBean2 sysBean, String MerchantID, String UserID, String UserName, String DepName, String UserUpdId)
    {
        // DataBaseBean SysBean = new DataBaseBean();
        boolean Flag = false;

        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append(" update ");
        sSQLSB.append(" MERCHANT_USER ");
        sSQLSB.append(" set ");
        sSQLSB.append(" DEP_NAME = ?, ");
        sSQLSB.append(" USER_UPD_ID = ?, ");
        sSQLSB.append(" USER_NAME = ?, ");
        sSQLSB.append(" USER_UPDATE_DATE = SYSDATE ");
        sSQLSB.append(" where ");
        sSQLSB.append(" MERCHANT_ID = ? ");
        sSQLSB.append(" and ");
        sSQLSB.append(" USER_ID = ? ");
        
        sysBean.AddSQLParam(emDataType.STR, DepName);
        sysBean.AddSQLParam(emDataType.STR, UserUpdId);
        sysBean.AddSQLParam(emDataType.STR, UserName);
        sysBean.AddSQLParam(emDataType.STR, MerchantID);
        sysBean.AddSQLParam(emDataType.STR, UserID);

        // System.out.println("Sql=" + Sql);
		ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/05/15 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-050 */
        	arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }

        System.out.println("arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
            Flag = true;
        }
        
        return Flag;
    }

//    /**
//     * 修改特約商店使用者資料-舊使用者重新建立
//     * @param String MerchantID 特約商店代號
//     * @param String UserID     使用者代號
//     * @param String UserName   使用者名稱
//     * @param String UserPwss    使用者密碼
//     * @param String UserStatus 使用者狀態
//     * @param String DepName    部門名稱
//     * @param String UserUpdId  維護人員
//     * @param String ExpireDate 新密碼到期日
//     * @return boolean
//     */
//    public boolean update_Merchant_User(String MerchantID, String UserID, String UserName, String UserPwss,
//                                        String UserStatus, String DepName, String UserUpdId, String ExpireDate)
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        boolean boolIsEqual = false;
//
//        String Sql =
//            " update "
//            + " MERCHANT_USER "
//            + " set "
//            + " USER_NAME = '" + UserName + "', "
//            + " USER_PWD = '" + UserPwss + "', "
//            + " USER_STATUS = '" + UserStatus + "', "
//            + " DEP_NAME = '" + DepName + "', "
//            + " USER_UPD_ID= '" + UserUpdId + "', "
//            + " USER_PWD_ERRCNT = 0, "
//            + " USER_INSERT_DATE = SYSDATE, "
//            + " USER_UPDATE_DATE = SYSDATE, "
//            + " USER_LOCK_DATE = null, "
//            + " USER_CHANGERPWD_DATE = SYSDATE, "
//            + " FST_LOGIN_DATE = null, "
//            + " DEL_FLAG = 'N', "
//            + " REENTRY = 'N', "
//            + " ENF_UPDPWD = 'Y', "
//            + " PWD_HIS1 = '', "
//            + " PWD_HIS2 = '', "
//            + " PWD_HIS3 = '', "
//            + " PWD_HIS4 = '', "
//            + " PWD_HIS5 = '', "
//            + " PWD_HIS6 = '', "
//            + " PWD_HIS7 = '', "
//            + " PWD_HIS8 = '', "
//            + " PWD_HIS9 = '', "
//            + " PWD_HIS10 = '', "
//            + " PWD_CNT = 0, "
//            + " EXPIRE_DATE = SYSDATE + "+ ExpireDate
//            + "  where "
//            + " MERCHANT_ID = '" + MerchantID + "' "
//            + " and "
//            + " USER_ID ='" + UserID + "' ";
//
//        // System.out.println("Sql=" + Sql);
//        try
//        {
//            boolIsEqual = ((Boolean) SysBean.executeSQL(Sql, "update")).booleanValue();
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        return boolIsEqual;
//    }

    /* Override update_Merchant_User with DataBaseBean parameter */
    public boolean update_Merchant_User(DataBaseBean2 sysBean, String MerchantID, String UserID, String UserName, String UserPwss,
                                        String UserStatus, String DepName, String UserUpdId, String ExpireDate,String SubMid)
    {
        // DataBaseBean SysBean = new DataBaseBean();
        boolean flag = false;

        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append(" update ");
        sSQLSB.append(" MERCHANT_USER ");
        sSQLSB.append(" set ");
        sSQLSB.append(" USER_NAME = ?, ");
        sSQLSB.append(" USER_PWD = ?, ");
        sSQLSB.append(" USER_STATUS = ?, ");
        sSQLSB.append(" DEP_NAME = ?, ");
        sSQLSB.append(" USER_UPD_ID = ?, ");
        sSQLSB.append(" SUBMID= ?, ");
        sSQLSB.append(" USER_PWD_ERRCNT = 0, ");
        sSQLSB.append(" USER_INSERT_DATE = SYSDATE, ");
        sSQLSB.append(" USER_UPDATE_DATE = SYSDATE, ");
        sSQLSB.append(" USER_LOCK_DATE = null, ");
        sSQLSB.append(" USER_CHANGERPWD_DATE = SYSDATE, ");
        sSQLSB.append(" FST_LOGIN_DATE = null, ");
        sSQLSB.append(" DEL_FLAG = 'N', ");
        sSQLSB.append(" REENTRY = 'N', ");
        sSQLSB.append(" ENF_UPDPWD = 'Y', ");
        sSQLSB.append(" PWD_HIS1 = '', ");
        sSQLSB.append(" PWD_HIS2 = '', ");
        sSQLSB.append(" PWD_HIS3 = '', ");
        sSQLSB.append(" PWD_HIS4 = '', ");
        sSQLSB.append(" PWD_HIS5 = '', ");
        sSQLSB.append(" PWD_HIS6 = '', ");
        sSQLSB.append(" PWD_HIS7 = '', ");
        sSQLSB.append(" PWD_HIS8 = '', ");
        sSQLSB.append(" PWD_HIS9 = '', ");
        sSQLSB.append(" PWD_HIS10 = '', ");
        sSQLSB.append(" PWD_CNT = 0, ");
        sSQLSB.append(" EXPIRE_DATE = SYSDATE + "+ ExpireDate);
        sSQLSB.append("  where ");
        sSQLSB.append(" MERCHANT_ID = ? ");
        sSQLSB.append(" and ");
        sSQLSB.append(" USER_ID = ? ");
        
        sysBean.AddSQLParam(emDataType.STR, UserName);
        sysBean.AddSQLParam(emDataType.STR, UserPwss);
        sysBean.AddSQLParam(emDataType.STR, UserStatus);
        sysBean.AddSQLParam(emDataType.STR, DepName);
        sysBean.AddSQLParam(emDataType.STR, UserUpdId);
        sysBean.AddSQLParam(emDataType.STR, SubMid);
        sysBean.AddSQLParam(emDataType.STR, MerchantID);
        sysBean.AddSQLParam(emDataType.STR, UserID);

        // System.out.println("Sql=" + Sql);
		ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/05/16 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-062 */
        	arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }

        System.out.println("arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
        	flag = true;
        }
        
        return flag;
    }

//    /**
//     * 修改特約商店使用者資料-使用者狀態
//     * @param String MerchantID 特約商店代號
//     * @param String UserID     使用者代號
//     * @param String UserStatus 使用者狀態
//     * @param String UserUpdId  維護人員
//     * @return boolean
//     */
//    public boolean update_Merchant_User_Status(String MerchantID, String UserID, String UserStatus, String UserUpdId)
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        boolean boolIsEqual = false;
//
//        String Sql =
//            " update "
//            + " MERCHANT_USER "
//            + " set "
//            + " USER_UPD_ID= '" + UserUpdId + "', "
//            + " USER_STATUS = '" + UserStatus + "', "
//            + " USER_UPDATE_DATE = SYSDATE "
//            + " where "
//            + " MERCHANT_ID = '" + MerchantID + "' "
//            + " and "
//            + " USER_ID ='" + UserID + "' ";
//
//        // System.out.println("Sql=" + Sql);
//        try
//        {
//            boolIsEqual = ((Boolean) SysBean.executeSQL(Sql, "update")).booleanValue();
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        return boolIsEqual;
//    }

    /* Override update_Merchant_User_Status with DataBaseBean parameter */
    public boolean update_Merchant_User_Status(DataBaseBean2 sysBean, String MerchantID, String UserID, String UserStatus, String UserUpdId)
    {
        // DataBaseBean SysBean = new DataBaseBean();
        boolean Flag = false;

        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append(" update ");
        sSQLSB.append(" MERCHANT_USER ");
        sSQLSB.append(" set ");
        sSQLSB.append(" USER_UPD_ID= ?, ");
        sSQLSB.append(" USER_STATUS = ?, ");
        sSQLSB.append(" USER_UPDATE_DATE = SYSDATE ");
        sSQLSB.append(" where ");
        sSQLSB.append(" MERCHANT_ID = ? ");
        sSQLSB.append(" and ");
        sSQLSB.append(" USER_ID = ? ");
        
        sysBean.AddSQLParam(emDataType.STR, UserUpdId);
        sysBean.AddSQLParam(emDataType.STR, UserStatus);
        sysBean.AddSQLParam(emDataType.STR, MerchantID);
        sysBean.AddSQLParam(emDataType.STR, UserID);

        // System.out.println("Sql=" + Sql);
		ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/05/15 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-052 */
        	arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }

        System.out.println("arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
            Flag = true;
        }
        
        return Flag;
    }

//    /**
//     * 修改特約商店使用者資料-密碼重置 及 解除停用
//     * @param String MerchantID 特約商店代號
//     * @param String UserID     使用者代號
//     * @param String UserStatus 使用者狀態
//     * @param String UserPwss    使用者密碼
//     * @param String UserUpdId  維護人員
//     * @param String ExpireDate 新密碼到期日
//     * @return boolean
//     */
//    public boolean update_Merchant_User_resetPwd(String MerchantID, String UserID, String UserStatus, String UserPwss, String UserUpdId, String ExpireDate )
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        boolean boolIsEqual = false;
//
//        String Sql =
//            " update "
//            + " MERCHANT_USER "
//            + " set "
//            + " USER_UPD_ID= '" + UserUpdId + "', "
//            + " USER_STATUS = '" + UserStatus + "', "
//            + " USER_PWD = '" + UserPwss + "', "
//            + " USER_PWD_ERRCNT = 0 , "
//            + " ENF_UPDPWD = 'Y' , "
//            + " USER_UPDATE_DATE = SYSDATE , "
//            + " USER_CHANGERPWD_DATE = SYSDATE, "
//            + " EXPIRE_DATE = SYSDATE + " + ExpireDate
//            + "  where "
//            + " MERCHANT_ID = '" + MerchantID + "' "
//            + " and "
//            + " USER_ID ='" + UserID + "' ";
//
//        // System.out.println("Sql=" + Sql);
//        try
//        {
//            boolIsEqual = ((Boolean) SysBean.executeSQL(Sql, "update")).booleanValue();
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        return boolIsEqual;
//    }

    /* Override update_Merchant_User_resetPwd with DataBaseBean parameter */
    public boolean update_Merchant_User_resetPwd(DataBaseBean2 sysBean, String MerchantID, String UserID, String UserStatus, String UserPwss, String UserUpdId, String ExpireDate )
    {
        // DataBaseBean SysBean = new DataBaseBean();
        boolean Flag = false;

        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append(" update ");
        sSQLSB.append(" MERCHANT_USER ");
        sSQLSB.append(" set ");
        sSQLSB.append(" USER_UPD_ID= ?, ");
        sSQLSB.append(" USER_STATUS = ?, ");
        sSQLSB.append(" USER_PWD = ?, ");
        sSQLSB.append(" USER_PWD_ERRCNT = 0 , ");
        sSQLSB.append(" ENF_UPDPWD = 'Y' , ");
        sSQLSB.append(" USER_UPDATE_DATE = SYSDATE , ");
        sSQLSB.append(" USER_CHANGERPWD_DATE = SYSDATE, ");
        sSQLSB.append(" EXPIRE_DATE = SYSDATE + " + ExpireDate);
        sSQLSB.append("  where ");
        sSQLSB.append(" MERCHANT_ID = ? ");
        sSQLSB.append(" and ");
        sSQLSB.append(" USER_ID = ? ");
        
        sysBean.AddSQLParam(emDataType.STR, UserUpdId);
        sysBean.AddSQLParam(emDataType.STR, UserStatus);
        sysBean.AddSQLParam(emDataType.STR, UserPwss);
        sysBean.AddSQLParam(emDataType.STR, MerchantID);
        sysBean.AddSQLParam(emDataType.STR, UserID);

        // System.out.println("Sql=" + Sql);
        ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/05/15 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-051 */
        	arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }

        System.out.println("arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
            Flag = true;
        }
        
        return Flag;
    }

//    /**
//     * 刪除特約商店使用者資料
//     * @param String MerchantID 特約商店代號
//     * @param String UserID     使用者代號
//     * @param String UserUpdId  維護人員
//     * @return boolean
//     */
//    public boolean delete_Merchant_User(String MerchantID, String UserID, String UserUpdId)
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        boolean boolIsEqual = false;
//
//        String Sql =
//            " update "
//            + " MERCHANT_USER "
//            + " set "
//            + " USER_UPD_ID= '" + UserUpdId + "', "
//            + " USER_UPDATE_DATE = SYSDATE, "
//            + " DEL_FLAG = 'Y' "
//            + " where "
//            + " MERCHANT_ID = '" + MerchantID + "' "
//            + " and "
//            + " USER_ID ='" + UserID + "' ";
//
//        // System.out.println("Sql=" + Sql);
//        try
//        {
//            boolIsEqual = ((Boolean) SysBean.executeSQL(Sql, "update")).booleanValue();
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        return boolIsEqual;
//    }

    /* Override delete_Merchant_User with DataBaseBean parameter */
    public boolean delete_Merchant_User(DataBaseBean2 sysBean, String MerchantID, String UserID, String UserUpdId)
    {
        // DataBaseBean SysBean = new DataBaseBean();
        boolean flag = false;

        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append(" update ");
        sSQLSB.append(" MERCHANT_USER ");
        sSQLSB.append(" set ");
        sSQLSB.append(" USER_UPD_ID= ?, ");
        sSQLSB.append(" USER_UPDATE_DATE = SYSDATE, ");
        sSQLSB.append(" DEL_FLAG = 'Y' ");
        sSQLSB.append(" where ");
        sSQLSB.append(" MERCHANT_ID = ? ");
        sSQLSB.append(" and ");
        sSQLSB.append(" USER_ID = ? ");
        
        sysBean.AddSQLParam(emDataType.STR, UserUpdId);
        sysBean.AddSQLParam(emDataType.STR, MerchantID);
        sysBean.AddSQLParam(emDataType.STR, UserID);

        // System.out.println("Sql=" + Sql);
		ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/05/15 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-053 */
        	arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }

        System.out.println("arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
        	flag = true;
        }
        
        return flag;
    }

    /**
     * 取得使用者設定權限
     * @return ArrayList
     */
//    public ArrayList get_Merchant_User_SetRole()
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        ArrayList arrayData = new ArrayList();
//
//        String Sql =
//            "select "
//            + " * "
//            + " From "
//            + " APPROLE "
//            + " LEFT JOIN "
//            + " APPROLE_MENUITEM  "
//            + " ON "
//            + " (APPROLE.ROLEID =APPROLE_MENUITEM.ROLEID) "
//            + " LEFT JOIN "
//            + " MENUITEM "
//            + " ON "
//            + " (APPROLE_MENUITEM.MENUID = MENUITEM.MENUID) "
//            + " WHERE LOCATION is not null "
//            + " order by APPROLE.ROLEID desc ";
//
//        System.out.println("Sql=" + Sql);
//        try
//        {
//            arrayData = (ArrayList) SysBean.executeSQL(Sql, "select");
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//        }
//        return arrayData;
//    }


//    /**
//     * 取得使用者設定權限
//     * @param String MerchantID 特約商店代號
//     * @return ArrayList
//     */
//    public ArrayList get_Merchant_User_SetRole(String MerchantID)
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        ArrayList arrayData = new ArrayList();
//
//        String Sql =
//            "Select "
//            + " USER_ID "
//            + " From MERCHANT_USER "
//            + " WHERE "
//            + " MERCHANT_ID = '" + MerchantID + "'"
//            + " AND "
//            + " MERCHANT_USER.AC_ADD = 'Y' ";
//
//        try
//        {
//           String UserID = "";
//           ArrayList ad = (ArrayList) SysBean.executeSQL(Sql, "select");
//           if (ad.size() > 0)
//           {
//              Hashtable hd = (Hashtable) ad.get(0);
//              UserID = (String) hd.get("USER_ID");
//           }
//
//           if (UserID != null && UserID.length() > 0)
//           {
//              Sql =
//                      "select "
//                      + " MERUSER_ROLE.*, MENUITEM.*, APPROLE.* "
//                      + " FROM "
//                      + " MERUSER_ROLE "
//                      + " LEFT JOIN "
//                      + " MENUITEM  "
//                      + " ON "
//                      + " (MERUSER_ROLE.MENUID=MENUITEM.MENUID AND MENUITEM.SYSTYPE = 'M') "
//                      + " LEFT JOIN "
//                      + " APPROLE "
//                      + " ON "
//                      + " (MERUSER_ROLE.ROLEID=APPROLE.ROLEID) "
//                      + " LEFT JOIN "
//                      + " MERCHANT_USER "
//                      + " ON "
//                      + " ( "
//                      + " MERUSER_ROLE.MERCHANTID=MERCHANT_USER.MERCHANT_ID "
//                      + " AND "
//                      + " MERUSER_ROLE.USERID=MERCHANT_USER.USER_ID "
//                      + " ) "
//                      + " WHERE "
//                      + " MERUSER_ROLE.MERCHANTID = '" + MerchantID + "' "
//                      + " and "
//                      + " USER_ID ='" + UserID + "' "
//                      + " order by MENUITEM.SORTIDX ";
////                      + " order by APPROLE.ROLEID desc ";
//
//              // System.out.println("Sql=" + Sql);
//
//              arrayData = (ArrayList) SysBean.executeSQL(Sql, "select");
//           }
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//
//        return arrayData;
//    }

    /* Override get_Merchant_User_SetRole with DataBaseBean parameter */
    public ArrayList get_Merchant_User_SetRole(DataBaseBean2 sysBean, String MerchantID)
    {
        // DataBaseBean SysBean = new DataBaseBean();
        ArrayList arrayData = new ArrayList();

        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("Select ");
        sSQLSB.append(" USER_ID ");
        sSQLSB.append(" From MERCHANT_USER ");
        sSQLSB.append(" WHERE ");
        sSQLSB.append(" MERCHANT_ID = ? ");
        sSQLSB.append(" AND ");
        sSQLSB.append(" MERCHANT_USER.AC_ADD = 'Y' ");
        
        sysBean.AddSQLParam(emDataType.STR, MerchantID);

        try
        {
           String UserID = "";
           
           /** 2023/05/15 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-054 */
           ArrayList ad = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
           if (ad.size() > 0)
           {
              Hashtable hd = (Hashtable) ad.get(0);
              UserID = (String) hd.get("USER_ID");
           }

           if (UserID != null && UserID.length() > 0)
           {
        	   	sSQLSB = new StringBuffer();
        	   	sysBean.ClearSQLParam();
       			sSQLSB.append("select ");
                sSQLSB.append(" MERUSER_ROLE.*, MENUITEM.*, APPROLE.* ");
                sSQLSB.append(" FROM ");
                sSQLSB.append(" MERUSER_ROLE ");
                sSQLSB.append(" LEFT JOIN ");
                sSQLSB.append(" MENUITEM  ");
                sSQLSB.append(" ON ");
                sSQLSB.append(" (MERUSER_ROLE.MENUID=MENUITEM.MENUID AND MENUITEM.SYSTYPE = 'M') ");
                sSQLSB.append(" LEFT JOIN ");
                sSQLSB.append(" APPROLE ");
                sSQLSB.append(" ON ");
                sSQLSB.append(" (MERUSER_ROLE.ROLEID=APPROLE.ROLEID) ");
                sSQLSB.append(" LEFT JOIN ");
                sSQLSB.append(" MERCHANT_USER ");
                sSQLSB.append(" ON ");
                sSQLSB.append(" ( ");
                sSQLSB.append(" MERUSER_ROLE.MERCHANTID=MERCHANT_USER.MERCHANT_ID ");
                sSQLSB.append(" AND ");
                sSQLSB.append(" MERUSER_ROLE.USERID=MERCHANT_USER.USER_ID ");
                sSQLSB.append(" ) ");
                sSQLSB.append(" WHERE ");
                sSQLSB.append(" MERUSER_ROLE.MERCHANTID = ? ");
                sSQLSB.append(" and ");
                sSQLSB.append(" USER_ID = ? ");
                sSQLSB.append(" order by MENUITEM.SORTIDX ");
                
                sysBean.AddSQLParam(emDataType.STR, MerchantID);
                sysBean.AddSQLParam(emDataType.STR, UserID);

                // System.out.println("Sql=" + Sql);
                
                /** 2023/05/15 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-055 */
                arrayData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
           }
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }

        return arrayData;
    }


    /**
     * 取得使用者權限
     * @param String MerchantID 特約商店代號
     * @param String UserID     使用者代號
     * @return ArrayList
     */
//    public ArrayList get_Merchant_User_Role(String MerchantID, String UserID)
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        ArrayList arrayData = new ArrayList();
//
//        String Sql =
//            "select "
//            + " MERUSER_ROLE.* "
//            + " From "
//            + " APPROLE_MENUITEM "
//            + " FULL JOIN "
//            + " MERUSER_ROLE  "
//            + " ON "
//            + " (APPROLE_MENUITEM.ROLEID = MERUSER_ROLE.ROLEID) "
//            + " WHERE MERUSER_ROLE.MERCHANTID = '" + MerchantID + "'"
//            + " AND "
//            + " MERUSER_ROLE.USERID = '" + UserID + "'";
//
//        System.out.println("Sql=" + Sql);
//        try
//        {
//            arrayData = (ArrayList) SysBean.executeSQL(Sql, "select");
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//        }
//        return arrayData;
//    }

//    /**
//     * 取得使用者權限
//     * @param String MerchantID 特約商店代號
//     * @param String UserID     使用者代號
//     * @return ArrayList
//     */
//    public ArrayList get_Merchant_User_Role(String MerchantID, String UserID)
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        ArrayList arrayData = new ArrayList();
//
//        String Sql =
//            "select "
//            + " * "
//            + " From "
//            + " MERUSER_ROLE "
//            + " LEFT JOIN "
//            + " MENUITEM "
//            + " ON "
//            + " (MERUSER_ROLE.MENUID = MENUITEM.MENUID AND MENUITEM.SYSTYPE = 'M') "
//            + " WHERE MERUSER_ROLE.MERCHANTID = '" + MerchantID + "' "
//            + " AND "
//            + " MERUSER_ROLE.USERID = '" + UserID + "' "
//            + " AND "
//            + " MERUSER_ROLE.ROLEID IN "
//            + " ( "
//            + " Select "
//            + " DISTINCT MERUSER_ROLE.ROLEID "
//            + " From MERCHANT_USER "
//            + " LEFT JOIN "
//            + " MERUSER_ROLE "
//            + " ON "
//            + " (MERCHANT_USER.MERCHANT_ID = MERUSER_ROLE.MERCHANTID and MERCHANT_USER.USER_ID = MERUSER_ROLE.USERID) "
//            + " LEFT JOIN "
//            + " MENUITEM "
//            + " ON "
//            + " (MERUSER_ROLE.MENUID = MENUITEM.MENUID) "
//            + " WHERE "
//            + " MERCHANT_ID = '" + MerchantID + "' "
//            + " AND "
//            + " MERCHANT_USER.AC_ADD = 'Y' "
//            + " ) "
//            + " order by MENUITEM.SORTIDX ";
//
//        // System.out.println("Sql=" + Sql);
//        try
//        {
//            arrayData = (ArrayList) SysBean.executeSQL(Sql, "select");
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        return arrayData;
//    }

    /* Override get_Merchant_User_Role with DataBaseBean parameter */
    public ArrayList get_Merchant_User_Role(DataBaseBean2 sysBean, String MerchantID, String UserID)
    {
        // DataBaseBean SysBean = new DataBaseBean();
        ArrayList arrayData = new ArrayList();

        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("select ");
        sSQLSB.append(" * ");
        sSQLSB.append(" From ");
        sSQLSB.append(" MERUSER_ROLE ");
        sSQLSB.append(" LEFT JOIN ");
        sSQLSB.append(" MENUITEM ");
        sSQLSB.append(" ON ");
        sSQLSB.append(" (MERUSER_ROLE.MENUID = MENUITEM.MENUID AND MENUITEM.SYSTYPE = 'M') ");
        sSQLSB.append(" WHERE MERUSER_ROLE.MERCHANTID = ? ");
        sSQLSB.append(" AND ");
        sSQLSB.append(" MERUSER_ROLE.USERID = ? ");
        sSQLSB.append(" AND ");
        sSQLSB.append(" MERUSER_ROLE.ROLEID IN ");
        sSQLSB.append(" ( ");
        sSQLSB.append(" Select ");
        sSQLSB.append(" DISTINCT MERUSER_ROLE.ROLEID ");
        sSQLSB.append(" From MERCHANT_USER ");
        sSQLSB.append(" LEFT JOIN ");
        sSQLSB.append(" MERUSER_ROLE ");
        sSQLSB.append(" ON ");
        sSQLSB.append(" (MERCHANT_USER.MERCHANT_ID = MERUSER_ROLE.MERCHANTID and MERCHANT_USER.USER_ID = MERUSER_ROLE.USERID) ");
        sSQLSB.append(" LEFT JOIN ");
        sSQLSB.append(" MENUITEM ");
        sSQLSB.append(" ON ");
        sSQLSB.append(" (MERUSER_ROLE.MENUID = MENUITEM.MENUID) ");
        sSQLSB.append(" WHERE ");
        sSQLSB.append(" MERCHANT_ID = ? ");
        sSQLSB.append(" AND ");
        sSQLSB.append(" MERCHANT_USER.AC_ADD = 'Y' ");
        sSQLSB.append(" ) ");
        sSQLSB.append(" order by MENUITEM.SORTIDX ");
        
        sysBean.AddSQLParam(emDataType.STR, MerchantID);
        sysBean.AddSQLParam(emDataType.STR, UserID);
        sysBean.AddSQLParam(emDataType.STR, MerchantID);

        // System.out.println("Sql=" + Sql);
        try
        {
        	/** 2023/05/15 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-056 */
            arrayData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }
        return arrayData;
    }

//    /**
//     * 新增使用者設定權限
//     * @param String MERCHANTID   特約商店代號
//     * @param String USERID       使用者代號
//     * @param String ROLEID       群組代碼
//     * @param String MENUID       功能代號
//     * @param String UPD_ID       維護人員
//     * @return boolean
//     */
//
//    public boolean insert_Merchant_User_Role(String MERCHANTID, String USERID, String ROLEID, String MENUID, String UPD_ID)
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        boolean boolIsEqual = false;
//
//        String Sql =
//            "insert into "
//            + " MERUSER_ROLE "
//            + " ( "
//            + " MERCHANTID, USERID, ROLEID, MENUID, UPD_ID, UPD_DATE "
//            + " ) "
//            + " values "
//            + " ( "
//            + " '" + MERCHANTID + "', "
//            + " '" + USERID + "', "
//            + " '" + ROLEID + "', "
//            + " '" + MENUID + "', "
//            + " '" + UPD_ID + "', "
//            + " SYSDATE "
//            + " ) ";
//
//        // System.out.println("Sql=" + Sql);
//        try
//        {
//            boolIsEqual = ((Boolean) SysBean.executeSQL(Sql, "insert")).booleanValue();
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        return boolIsEqual;
//    }

    /* Override insert_Merchant_User_Role with DataBaseBean parameter */
    public boolean insert_Merchant_User_Role(DataBaseBean2 sysBean, String MERCHANTID, String USERID, String ROLEID, String MENUID, String UPD_ID)
    {
        // DataBaseBean SysBean = new DataBaseBean();
        boolean flag = false;

        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("insert into ");
        sSQLSB.append(" MERUSER_ROLE ");
        sSQLSB.append(" ( ");
        sSQLSB.append(" MERCHANTID, USERID, ROLEID, MENUID, UPD_ID, UPD_DATE ");
        sSQLSB.append(" ) ");
        sSQLSB.append(" values ");
        sSQLSB.append(" ( ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" ?, ");
        sSQLSB.append(" SYSDATE ");
        sSQLSB.append(" ) ");
        
        sysBean.AddSQLParam(emDataType.STR, MERCHANTID);
        sysBean.AddSQLParam(emDataType.STR, USERID);
        sysBean.AddSQLParam(emDataType.STR, ROLEID);
        sysBean.AddSQLParam(emDataType.STR, MENUID);
        sysBean.AddSQLParam(emDataType.STR, UPD_ID);

        // System.out.println("Sql=" + Sql);
		ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/05/16 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-064 */
        	arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }

        System.out.println("arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
        	flag = true;
        }
        
        return flag;
    }


//    /**
//     * 修改使用者設定權限
//     * @param String MERCHANTID   特約商店代號
//     * @param String USERID       使用者代號
//     * @param String ROLEID       群組代碼
//     * @param String MENUID       功能代號
//     * @param String UPD_ID       維護人員
//     * @return boolean
//     */
//
//    public boolean update_Merchant_User_Role(String MERCHANTID, String USERID, String ROLEID, String MENUID, String UPD_ID)
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        boolean boolIsEqual = false;
//
//        String Sql =
//            "UPDATE "
//            + " MERUSER_ROLE "
//            + " SET "
//            + " UPD_ID = '" + UPD_ID + "', "
//            + " UPD_DATE = SYSDATE"
//            + " WHERE "
//            + " MERCHANTID = '" + MERCHANTID + "' "
//            + " AND "
//            + " USERID = '" + USERID + "' "
//            + " AND "
//            + " ROLEID = '" + ROLEID + "' "
//            + " AND "
//            + " MENUID = '" + MENUID + "' ";
//
//        // System.out.println("Sql=" + Sql);
//        try
//        {
//            boolIsEqual = ((Boolean) SysBean.executeSQL(Sql, "update")).booleanValue();
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        return boolIsEqual;
//    }

    /* Override update_Merchant_User_Role with DataBaseBean parameter */
    public boolean update_Merchant_User_Role(DataBaseBean2 sysBean, String MERCHANTID, String USERID, String ROLEID, String MENUID, String UPD_ID)
    {
        // DataBaseBean SysBean = new DataBaseBean();
        boolean flag = false;

        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("UPDATE ");
        sSQLSB.append(" MERUSER_ROLE ");
        sSQLSB.append(" SET ");
        sSQLSB.append(" UPD_ID = ?, ");
        sSQLSB.append(" UPD_DATE = SYSDATE");
        sSQLSB.append(" WHERE ");
        sSQLSB.append(" MERCHANTID = ? ");
        sSQLSB.append(" AND ");
        sSQLSB.append(" USERID = ? ");
        sSQLSB.append(" AND ");
        sSQLSB.append(" ROLEID = ? ");
        sSQLSB.append(" AND ");
        sSQLSB.append(" MENUID = ? ");
        
        sysBean.AddSQLParam(emDataType.STR, UPD_ID);
        sysBean.AddSQLParam(emDataType.STR, MERCHANTID);
        sysBean.AddSQLParam(emDataType.STR, USERID);
        sysBean.AddSQLParam(emDataType.STR, ROLEID);
        sysBean.AddSQLParam(emDataType.STR, MENUID);
        
        // System.out.println("Sql=" + Sql);
		ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/05/16 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-065 */
        	arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }

        System.out.println("arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
        	flag = true;
        }
        
        return flag;
    }

//    /**
//     * 刪除使用者設定權限
//     * @param String MERCHANTID   特約商店代號
//     * @param String USERID       使用者代號
//     * @param String ROLEID       群組代碼
//     * @return boolean
//     */
//
//    public boolean delete_Merchant_User_Role(String MERCHANTID, String USERID, String ROLEID)
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        boolean boolIsEqual = false;
//
//        String Sql =
//            "DELETE FROM "
//            + " MERUSER_ROLE "
//            + " WHERE "
//            + " MERCHANTID = '" + MERCHANTID + "' "
//            + " AND "
//            + " USERID = '" + USERID + "' "
//            + " AND "
//            + " ROLEID = '" + ROLEID + "' ";
//
//        // System.out.println("Sql=" + Sql);
//        try
//        {
//            boolIsEqual = ((Boolean) SysBean.executeSQL(Sql, "delete")).booleanValue();
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        return boolIsEqual;
//    }

    /* Override delete_Merchant_User_Role with DataBaseBean parameter */
    public boolean delete_Merchant_User_Role(DataBaseBean2 sysBean, String MERCHANTID, String USERID, String ROLEID)
    {
        // DataBaseBean SysBean = new DataBaseBean();
        boolean flag = false;

        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("DELETE FROM ");
        sSQLSB.append(" MERUSER_ROLE ");
        sSQLSB.append(" WHERE ");
        sSQLSB.append(" MERCHANTID = ? ");
        sSQLSB.append(" AND ");
        sSQLSB.append(" USERID = ? ");
        sSQLSB.append(" AND ");
        sSQLSB.append(" ROLEID = ? ");
        
        sysBean.AddSQLParam(emDataType.STR, MERCHANTID);
        sysBean.AddSQLParam(emDataType.STR, USERID);
        sysBean.AddSQLParam(emDataType.STR, ROLEID);

        // System.out.println("Sql=" + Sql);
		ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/05/16 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-063 */
        	arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }

        System.out.println("arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
        	flag = true;
        }
        
        return flag;
    }

//    /**
//     * 刪除使用者設定權限
//     * @param String MERCHANTID   特約商店代號
//     * @param String USERID       使用者代號
//     * @param String ROLEID       群組代碼
//     * @param String MENUID       功能代號
//     * @return boolean
//     */
//
//    public boolean delete_Merchant_User_Role(String MERCHANTID, String USERID, String ROLEID, String MENUID)
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        boolean boolIsEqual = false;
//
//        String Sql =
//            "DELETE FROM "
//            + " MERUSER_ROLE "
//            + " WHERE "
//            + " MERCHANTID = '" + MERCHANTID + "' "
//            + " AND "
//            + " USERID = '" + USERID + "' "
//            + " AND "
//            + " ROLEID = '" + ROLEID + "' "
//            + " AND "
//            + " MENUID = '" + MENUID + "' ";
//
//        // System.out.println("Sql=" + Sql);
//        try
//        {
//            boolIsEqual = ((Boolean) SysBean.executeSQL(Sql, "delete")).booleanValue();
//        }
//        catch (Exception ex)
//        {
//            System.out.println(ex.getMessage());
//            log_systeminfo.debug(ex.toString());
//        }
//        return boolIsEqual;
//    }

    /* Override delete_Merchant_User_Role with DataBaseBean parameter */
    public boolean delete_Merchant_User_Role(DataBaseBean2 sysBean, String MERCHANTID, String USERID, String ROLEID, String MENUID)
    {
        // DataBaseBean SysBean = new DataBaseBean();
        boolean flag = false;

        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("DELETE FROM ");
        sSQLSB.append(" MERUSER_ROLE ");
        sSQLSB.append(" WHERE ");
        sSQLSB.append(" MERCHANTID = ? ");
        sSQLSB.append(" AND ");
        sSQLSB.append(" USERID = ? ");
        sSQLSB.append(" AND ");
        sSQLSB.append(" ROLEID = ? ");
        sSQLSB.append(" AND ");
        sSQLSB.append(" MENUID = ? ");
        
        sysBean.AddSQLParam(emDataType.STR, MERCHANTID);
        sysBean.AddSQLParam(emDataType.STR, USERID);
        sysBean.AddSQLParam(emDataType.STR, ROLEID);
        sysBean.AddSQLParam(emDataType.STR, MENUID);

        // System.out.println("Sql=" + Sql);
		ArrayList arraySys = new ArrayList();
        try
        {
        	/** 2023/05/16 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-067 */
        	arraySys = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            log_systeminfo.debug(ex.toString());
        }

        System.out.println("arraySys.size()="+arraySys.size());
        int dataSize = arraySys.size();
        if(dataSize > 0) 
        {
        	flag = true;
        }
        
        return flag;
    }

//    /**
//     * 取得特約商店使用者是否登入
//     * @param MERCHANTID String
//     * @param timeDate String
//     * @return ArrayList
//     */
//    public ArrayList select_Merchant_User_LoginStatus(String MERCHANTID, String timeDate)
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        ArrayList arrayData = new ArrayList();
//
//        String Sql =
//                    "select "
//                    + " SESSION_CONTROL.* "
//                    + " from "
//                    + " MERCHANT_USER "
//                    + " left join "
//                    + " SESSION_CONTROL "
//                    + " on( "
//                    + " MERCHANT_USER.merchant_id=SESSION_CONTROL.merchant_id "
//                    + " and "
//                    + " MERCHANT_USER.user_id=SESSION_CONTROL.user_id "
//                    + " ) "
//                    + " where "
//                    + " MERCHANT_USER.merchant_id='" + MERCHANTID + "' "
//                    + " and "
//                    + " SESSION_CONTROL.session_status='Y' "
//                    + " and "
//                    + " login_date +" + timeDate + "/24/60 > sysdate "
//                    + " order by MERCHANT_USER.user_id";
//
//        // System.out.println("Sql=" + Sql);
//        try
//        {
//           arrayData = (ArrayList) SysBean.executeSQL(Sql, "select");
//        }
//        catch (Exception ex)
//        {
//           System.out.println(ex.getMessage());
//           log_systeminfo.debug(ex.toString());
//        }
//        return arrayData;
//    }

    /* Override select_Merchant_User_LoginStatus with DataBaseBean parameter */
    public ArrayList select_Merchant_User_LoginStatus(DataBaseBean2 sysBean, String MERCHANTID, String timeDate)
    {
        // DataBaseBean SysBean = new DataBaseBean();
        ArrayList arrayData = new ArrayList();

     // DataBaseBean SysBean = new DataBaseBean();
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
		sSQLSB.append("select ");
        sSQLSB.append(" SESSION_CONTROL.* ");
        sSQLSB.append(" from ");
        sSQLSB.append(" MERCHANT_USER ");
        sSQLSB.append(" left join ");
        sSQLSB.append(" SESSION_CONTROL ");
        sSQLSB.append(" on( ");
        sSQLSB.append(" MERCHANT_USER.merchant_id=SESSION_CONTROL.merchant_id ");
        sSQLSB.append(" and ");
        sSQLSB.append(" MERCHANT_USER.user_id=SESSION_CONTROL.user_id ");
        sSQLSB.append(" ) ");
        sSQLSB.append(" where ");
        sSQLSB.append(" MERCHANT_USER.merchant_id = ? ");
        sSQLSB.append(" and ");
        sSQLSB.append(" SESSION_CONTROL.session_status='Y' ");
        sSQLSB.append(" and ");
        sSQLSB.append(" login_date +" + timeDate + "/24/60 > sysdate ");
        sSQLSB.append(" order by MERCHANT_USER.user_id");
        
        sysBean.AddSQLParam(emDataType.STR, MERCHANTID);

        // System.out.println("Sql=" + Sql);
        try
        {
        	/** 2023/05/16 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-057 */
        	arrayData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
        }
        catch (Exception ex)
        {
           System.out.println(ex.getMessage());
           log_systeminfo.debug(ex.toString());
        }
        return arrayData;
    }

}
