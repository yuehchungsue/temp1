/************************************************************
 * <p>#File Name:		MerchantcheckPwd.java	</p>
 * <p>#Description:					</p>
 * <p>#Create Date:		2007/09/19		</p>
 * <p>#Company:  		cybersoft		</p>
 * <p>#Notice:   					</p>
 * @author		Caspar Chen
 * @since		SPEC version
 * @version	0.1	2007/09/19	Caspar Chen
 ************************************************************/
package com.cybersoft.merchant.bean;

/** import com.cybersoft.bean.DataBaseBean; */
import com.cybersoft.bean.DataBaseBean2;

public class MerchantcheckPwdBean
{
    public MerchantcheckPwdBean()
    {
    }

//    /**
//     * ���o�S���ϥΪ�
//     * @param String MerchantID �S���N��
//     * @param String UserID     �ϥΪ̥N��
//     * @param String Pwd        �K�X
//     * @param String Pwd_date   �K�X�ק�ɶ�
//     * @return boolean
//     */
//    public boolean updateDb_UserPwd(String MerchantID, String UserID, String Pwd, String Pwd_date) 
//    {
//      DataBaseBean SysBean = new DataBaseBean();
//      boolean boolIsEqual = false;
//
//      String Sql = " UPDATE MERCHANT_USER "+
//                   " SET USER_PWD ='" + Pwd + "', USER_CHANGEPWD_DATE ='" + Pwd_date + "' " +
//                   " WHERE MERCHANT_ID = '" + MerchantID + "' AND USER_ID='" + UserID + "' ";
//      // System.out.println("Sql="+Sql);
////      try{
////          boolIsEqual = SysBean.
////      }
////      catch(Exception ex) {
////        System.out.println(ex.getMessage());
////      }
//
//      return boolIsEqual;
//    }
    
//    /* Override updateDb_UserPwd with DataBaseBean parameter */
//    public boolean updateDb_UserPwd(DataBaseBean SysBean, String MerchantID, String UserID, String Pwd, String Pwd_date) 
//    {
//        //DataBaseBean SysBean = new DataBaseBean();
//        boolean boolIsEqual = false;
//        
//        String Sql = " UPDATE MERCHANT_USER "+
//                     " SET USER_PWD ='" + Pwd + "', USER_CHANGEPWD_DATE ='" + Pwd_date + "' " +
//                     " WHERE MERCHANT_ID = '" + MerchantID + "' AND USER_ID='" + UserID + "' ";
//        // System.out.println("Sql="+Sql);
////        try{
////            boolIsEqual = SysBean.
////        }
////        catch(Exception ex) {
////          System.out.println(ex.getMessage());
////        }
//        
//        return boolIsEqual;
//    }
}
