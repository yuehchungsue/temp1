package com.cybersoft.merchant.bean;

import java.util.ArrayList;

/*** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft.bean.DataBaseBean2;
import com.cybersoft.bean.LogUtils;
import com.cybersoft.bean.emDataType;
public class MerchantMenuBean
{
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    public MerchantMenuBean()
    {
        super();
    }

//    /**
//     * 取得權限功能
//     * @return ArrayList
//     */
//    public ArrayList get_Merchant_User_Role()
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        ArrayList arrayData = new ArrayList();
//
//        String Sql =
//                "Select "
//                + " * "
//                + " From TFBPG.MENUITEM "
//                + " WHERE "
//                + " LOCATION is not null "
//                + " AND "
//                + " SYSTYPE = 'M' "
//                + " ORDER BY SORTIDX ";
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
//
//        return arrayData;
//    }

//    /* Override get_Merchant_User_Role with DataBaseBean parameter */
//    public ArrayList get_Merchant_User_Role(DataBaseBean SysBean)
//    {
//        // DataBaseBean SysBean = new DataBaseBean();
//        ArrayList arrayData = new ArrayList();
//
//        String Sql =
//                "Select "
//                + " * "
//                + " From TFBPG.MENUITEM "
//                + " WHERE "
//                + " LOCATION is not null "
//                + " AND "
//                + " SYSTYPE = 'M' "
//                + " ORDER BY SORTIDX ";
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
//
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
//            + " DISTINCT "
//            + " MERUSER_ROLE.*, MENUITEM.* "
//            + " From "
//            + " APPROLE_MENUITEM "
//            + " FULL JOIN "
//            + " MERUSER_ROLE "
//            + " ON "
//            + " (APPROLE_MENUITEM.ROLEID = MERUSER_ROLE.ROLEID) "
//            + " LEFT JOIN "
//            + " MENUITEM "
//            + " ON "
//            + " (MERUSER_ROLE.MENUID = MENUITEM.MENUID) "
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
//        ArrayList arrayData = new ArrayList();
//
//        MerchantUserDataBean MerchantUserDataBean = new MerchantUserDataBean();
//        arrayData = MerchantUserDataBean.get_Merchant_User_Role(MerchantID, UserID);
//        return arrayData;
//    }

    /* Override get_Merchant_User_Role with DataBaseBean parameter */
    public ArrayList get_Merchant_User_Role(DataBaseBean2 sysBean, String MerchantID, String UserID)
    {
        ArrayList arrayData = new ArrayList();

        MerchantUserDataBean MerchantUserDataBean = new MerchantUserDataBean();
        arrayData = MerchantUserDataBean.get_Merchant_User_Role(sysBean, MerchantID, UserID);
        return arrayData;
    }

    /**
     * 取得使用者權限
     * @param String MerchantID 特約商店代號
     * @param String UserID     使用者代號
     * @param String RoleID     群組代號
     * @param String MenuID     功能代號
     * @return ArrayList
     */
//    public ArrayList get_Merchant_User_Role(String MerchantID, String UserID,
//                                            String RoleID, String MenuID)
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        ArrayList arrayData = new ArrayList();
//
//        String Sql =
//            "select "
//            + " DISTINCT "
//            + " MERUSER_ROLE.*, MENUITEM.* "
//            + " From "
//            + " APPROLE_MENUITEM "
//            + " FULL JOIN "
//            + " MERUSER_ROLE "
//            + " ON "
//            + " (APPROLE_MENUITEM.ROLEID = MERUSER_ROLE.ROLEID) "
//            + " LEFT JOIN "
//            + " MENUITEM "
//            + " ON "
//            + " (MERUSER_ROLE.MENUID = MENUITEM.MENUID) "
//            + " WHERE MERUSER_ROLE.MERCHANTID = '" + MerchantID + "'"
//            + " AND "
//            + " MERUSER_ROLE.USERID = '" + UserID + "'"
//            + " AND "
//            + " MERUSER_ROLE.ROLEID = '" + RoleID + "'"
//            + " AND "
//            + " MERUSER_ROLE.MENUID = '" + MenuID + "'";
//
//
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
//     * @param String RoleID     群組代號
//     * @param String MenuID     功能代號
//     * @return ArrayList
//     */
//    public ArrayList get_Merchant_User_Role(String MerchantID, String UserID, String RoleID, String MenuID)
//    {
//        DataBaseBean SysBean = new DataBaseBean();
//        ArrayList arrayData = new ArrayList();
//
//        String Sql =
//            "select "
//            + " MERUSER_ROLE.* "
//            + " From "
//            + " MERUSER_ROLE "
//            + " WHERE MERUSER_ROLE.MERCHANTID = '" + MerchantID + "'"
//            + " AND "
//            + " MERUSER_ROLE.USERID = '" + UserID + "'"
//            + " AND "
//            + " MERUSER_ROLE.ROLEID = '" + RoleID + "'"
//            + " AND "
//            + " MERUSER_ROLE.MENUID = '" + MenuID + "'";
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
//
//        return arrayData;
//    }

    /* Override get_Merchant_User_Role with DataBaseBean parameter */
    public ArrayList get_Merchant_User_Role(DataBaseBean2 sysBean, String MerchantID, String UserID, String RoleID, String MenuID)
    {
        // DataBaseBean SysBean = new DataBaseBean();
        ArrayList arrayData = new ArrayList();
        StringBuffer sSQLSB = new StringBuffer();
        sysBean.ClearSQLParam();
        sSQLSB.append("select ");
        sSQLSB.append(" MERUSER_ROLE.* ");
        sSQLSB.append(" From ");
        sSQLSB.append(" MERUSER_ROLE ");
        sSQLSB.append(" WHERE MERUSER_ROLE.MERCHANTID = ? ");
        sSQLSB.append(" AND ");
        sSQLSB.append(" MERUSER_ROLE.USERID = ? ");
        sSQLSB.append(" AND ");
        sSQLSB.append(" MERUSER_ROLE.ROLEID = ? ");
        sSQLSB.append(" AND ");
        sSQLSB.append(" MERUSER_ROLE.MENUID = ? ");
        
        sysBean.AddSQLParam(emDataType.STR, MerchantID);
        sysBean.AddSQLParam(emDataType.STR, UserID);
        sysBean.AddSQLParam(emDataType.STR, RoleID);
        sysBean.AddSQLParam(emDataType.STR, MenuID);

        // System.out.println("Sql=" + Sql);
        try
        {
        	/** 2023/04/25 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-009 */
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
