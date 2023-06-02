/************************************************************
 * <p>#File Name:	MerchantRefundQueryCtl.java	</p>
 * <p>#Description:	            			</p>
 * <p>#Create Date:	2007/09/26		        </p>
 * <p>#Company:  	cybersoft	        	</p>
 * <p>#Notice:   					</p>
 * @author		Shirley Lin
 * @since		SPEC version
 * @version	0.1	2007/09/26	Shirley Lin
 * 異動說明
 * 20200306 202002100619-00 saint EC退貨檢核
 ************************************************************/
package com.cybersoft.merchant.bean;

import java.util.ArrayList;
import java.util.Hashtable;
import com.cybersoft.bean.*;

public class MerchantRefundBean 
{
    public static final LogUtils log_systeminfo = new LogUtils("systeminfo");
    public MerchantRefundBean() 
    {
    }

    /**
     * 取得退貨列表資料(Billing+Balance)
     * @param String MerchantID     特店代號
     * @param String SubMID         特店代號
     * @param String OrderType      查詢訂單類別 (Order:特店指定單號 System:系統指定單號 )
     * @param String OrderID        訂單代號
     * @return ArrayList            列表資料
     */
    public ArrayList<Hashtable<String,String>> get_Refund_List(String MerchantID, String SubMID, String OrderType, String OrderID) 
    {
		StringBuffer sSQLSB = new StringBuffer();
		DataBaseBean2 DBBean = new DataBaseBean2();
		ArrayList<Hashtable<String,String>> arrayBillingData = null; // 顯示訂單交易資料;
		DBBean.ClearSQLParam();

        if (MerchantID.length() > 0 && SubMID.length() > 0 && OrderType.length() > 0 && OrderID.length() > 0) 
        {
        	sSQLSB.append("SELECT A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.REVERSALFLAG, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG, A.SYSTRACENO , B.AUTHAMT, B.REFUNDAMT, B.CANCELAMT, B.CAPTUREAMT, (B.AUTHAMT - B.REFUNDAMT - B.CANCELAMT)  BALANCEAMT1 FROM ");
        	sSQLSB.append("(SELECT X.*  FROM BILLING X, (SELECT MERCHANTID, SUBMID, ORDERID FROM BILLING ");
        	sSQLSB.append(" WHERE MERCHANTID =? ");// 1
        	sSQLSB.append(" AND SUBMID =? ");// 2
    		DBBean.AddSQLParam(emDataType.STR, MerchantID); // 1
    		DBBean.AddSQLParam(emDataType.STR, SubMID); // 2
            
            if (OrderType.equalsIgnoreCase("Order")) 
            { 
                // 以OrderID
            	sSQLSB.append(" AND ORDERID =? ");
           } 
            else 
            {
            	sSQLSB.append(" AND SYS_ORDERID =? ");
            }
            DBBean.AddSQLParam(emDataType.STR, OrderID); // 3
            
            /******    銀聯卡需求  DalePeng   20150604 ---Start---	******/
            sSQLSB.append(" AND LTRIM(RTRIM(PAN)) != '62XX' ");
            /******    銀聯卡需求  DalePeng   20150604 ---End---	******/
            sSQLSB.append(") Y  WHERE X.MERCHANTID = Y.MERCHANTID AND X.SUBMID = Y.SUBMID AND X.ORDERID = Y.ORDERID ORDER BY X.TRANSDATE, X.TRANSTIME) A, BALANCE B WHERE A.MERCHANTID = B.MERCHANTID AND A.SUBMID = B.SUBMID AND A.ORDERID = B.ORDERID GROUP BY A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.REVERSALFLAG, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG, A.SYSTRACENO , B.AUTHAMT, B.REFUNDAMT, B.CANCELAMT, B.CAPTUREAMT");
            
            try 
            {
                arrayBillingData = DBBean.QuerySQLByParam(sSQLSB.toString());
            } 
            catch (Exception ex) 
            {
                System.out.println(ex.getMessage());
                log_systeminfo.debug(ex.toString());
            }
            DBBean = null;
            if (arrayBillingData == null) 
            {
                arrayBillingData = new ArrayList<Hashtable<String,String>>();
            }
        }
        
        return arrayBillingData;
    }

    /* Override get_Refund_List with DataBaseBean parameter */
    public ArrayList<Hashtable<String,String>> get_Refund_Listxxxx(DataBaseBean2 DBBean, String MerchantID, String SubMID, String OrderType, String OrderID) 
    {
		StringBuffer sSQLSB = new StringBuffer();
		ArrayList<Hashtable<String,String>> arrayBillingData = null; // 顯示訂單交易資料;
		DBBean.ClearSQLParam();

		if (MerchantID.length() > 0 && SubMID.length() > 0 && OrderType.length() > 0 && OrderID.length() > 0) 
        {
			sSQLSB.append("SELECT A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.REVERSALFLAG, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG, A.SYSTRACENO , B.AUTHAMT, B.REFUNDAMT, B.CANCELAMT, B.CAPTUREAMT, (B.AUTHAMT - B.REFUNDAMT - B.CANCELAMT)  BALANCEAMT1 FROM ");
        	sSQLSB.append("(SELECT X.*  FROM BILLING X, (SELECT MERCHANTID, SUBMID, ORDERID FROM BILLING ");
        	sSQLSB.append(" WHERE MERCHANTID =? ");// 1
        	sSQLSB.append(" AND SUBMID =? ");// 2
    		DBBean.AddSQLParam(emDataType.STR, MerchantID); // 1
    		DBBean.AddSQLParam(emDataType.STR, SubMID); // 2
            
            if (OrderType.equalsIgnoreCase("Order")) 
            { 
                // 以OrderID
            	sSQLSB.append(" AND ORDERID =? ");
            } 
            else 
            {
            	sSQLSB.append(" AND SYS_ORDERID =? ");
            }
            /******    銀聯卡需求  DalePeng   20150604 ---Start---	******/
            sSQLSB.append(" AND LTRIM(RTRIM(PAN)) != '62XX' ");
            /******    銀聯卡需求  DalePeng   20150604 ---End---	******/
            sSQLSB.append(") Y  WHERE X.MERCHANTID = Y.MERCHANTID AND X.SUBMID = Y.SUBMID AND X.ORDERID = Y.ORDERID ORDER BY X.TRANSDATE, X.TRANSTIME) A, BALANCE B WHERE A.MERCHANTID = B.MERCHANTID AND A.SUBMID = B.SUBMID AND A.ORDERID = B.ORDERID GROUP BY A.MERCHANTID, A.SUBMID, A.ORDERID, A.SYS_ORDERID, A.TERMINALID, A.TRANSCODE, A.TRANSMODE, A.REVERSALFLAG, A.TRANSDATE, A.TRANSTIME, A.CURRENCYCODE, A.TRANSAMT, A.RESPONSECODE, A.BATCHNO, A.APPROVECODE, A.RESPONSEMSG, A.BALANCEAMT, A.REVERSALFLAG, A.SYSTRACENO , B.AUTHAMT, B.REFUNDAMT, B.CANCELAMT, B.CAPTUREAMT");
            
            try 
            {
                arrayBillingData = DBBean.QuerySQLByParam(sSQLSB.toString());
            } 
            catch (Exception ex) 
            {
                System.out.println(ex.getMessage());
                log_systeminfo.debug(ex.toString());
            }
            
            DBBean = null;
            if (arrayBillingData == null) 
            {
                arrayBillingData = new ArrayList<Hashtable<String,String>>();
            }
        }
        
        return arrayBillingData;
    }
    
    /**
     * 判斷是否允許退貨
     * @param ArrayList arrayBillingData 退貨列表資料
     * @param String    OverRefundLimit  超額退貨金額
     * @return Hashtable                 判斷結果 FLAG:退貨判斷(String)  MESSAGE:顯示結果(String)
     */
    public Hashtable check_Refund_Status(ArrayList arrayBillingData, String OverRefundLimit) 
    {
        Hashtable hashData = new Hashtable();
        boolean boolFlag = false; // 是否能做退貨
        String strMessage = ""; // 顯示結果
        
        if (arrayBillingData.size() == 0) 
        {
            strMessage = "查無交易資料";
        } 
        else 
        {
            try 
            {
                for (int c = 0; c < arrayBillingData.size(); ++c) 
                {
                    Hashtable hashTmpData = (Hashtable) arrayBillingData.get(c);
//                    System.out.println("hashTmpData=" + hashTmpData);
                    String TranCode = hashTmpData.get("TRANSCODE").toString();
//                    System.out.println("TranCode=" + TranCode);

                    if (TranCode.equalsIgnoreCase("00")) 
                    { 
                        // 購貨交易
                        String TransAmt     = hashTmpData.get("TRANSAMT").toString();
                        String BalanceAmt   = hashTmpData.get("BALANCEAMT").toString(); //  Billing 的 BalanceAmt
                        String ReversalFlag = hashTmpData.get("REVERSALFLAG").toString();
                        String BalanceAmt1  = hashTmpData.get("BALANCEAMT1").toString(); // Balance 的 AuthAmt - RefundAmt - CancelAmt
                        String RefundAmt    = hashTmpData.get("REFUNDAMT").toString(); //  Balance 的 RefundAmt
//                        System.out.println("BalanceAmt1=" + BalanceAmt1);
                                 
                        if (TransAmt.length() == 0 || BalanceAmt.length() == 0 || BalanceAmt1.length() == 0) 
                        {
                            strMessage = "查無交易資料";
                        } 
                        else 
                        {
                            boolFlag = true;
                            String CancelAmt = hashTmpData.get("CANCELAMT").toString();
                            String AuthAmt = hashTmpData.get("AUTHAMT").toString();
                            String CardType    = hashTmpData.get("CARD_TYPE").toString(); // Billing 的 CardType
                            String Pan          = hashTmpData.get("PAN").toString();       // Billing 的 Pan
                            
                            if (Double.parseDouble(CancelAmt) == Double.parseDouble(AuthAmt)) 
                            {
                                strMessage = "交易已取消";
                                boolFlag = false;
                            }
                            
                            if (ReversalFlag.equalsIgnoreCase("1")) 
                            {
                                strMessage = "交易已沖正";
                                boolFlag = false;
                            }
                            
                            if (Double.parseDouble(RefundAmt) > 0 && Double.parseDouble(RefundAmt) >= Double.parseDouble(AuthAmt)) 
                            {
                                strMessage = "交易無法退貨";
                                boolFlag = false;
                            } 
                            else 
                            {
                                if ((Double.parseDouble(OverRefundLimit) + Double.parseDouble(BalanceAmt1)) <= 0) 
                                {
                                    strMessage = "交易無法退貨";
                                    boolFlag = false;
                                }
                            }
                            
//                            /******    銀聯卡需求  DalePeng   20150604 ---Start---	******/
//                            if(CardType.equalsIgnoreCase("C"))
//                            {
//                            	if(Pan.equalsIgnoreCase("62XX"))
//                            	{
//                            		 strMessage = "卡號不可為62XX";
//                                     boolFlag = false;
//                            	}
//                            }
//                            /******    銀聯卡需求  DalePeng   20150604 ---End---	******/
                        }
                        
                        break;
                    }
                }
            } 
            catch (Exception ex) 
            {
                System.out.println(ex.getMessage());
                log_systeminfo.debug(ex.toString());
            }
        }

        hashData.put("FLAG", String.valueOf(boolFlag));
        hashData.put("MESSAGE", strMessage);
        
        return hashData;
    }

    /**
     * 判斷退貨金額
     * @param Hashtable hashBalanceData  帳單檢核資料(Balance)
     * @param Boolean boolPartial        是否提供部份退貨 true:可 false:不可 (參考端末機權限Permit_Partial_Refund  若為分期或紅利為false)
     * @param String InputRefundAmt      特店輸入退貨金額
     * @param String OverRefundLimit     超額退貨金額  2007.010.19修改
     * @return Hashtable                 判斷結果 FLAG:退貨判斷(String)  MESSAGE:顯示結果(String)
     */
     
    public Hashtable check_Refund_Amt(Hashtable hashBalanceData, boolean boolPartial, String InputRefundAmt, String OverRefundLimit) 
    {
        boolean boolFlag = false; // 是否能做退貨
        String strMessage = ""; // 顯示結果
        Hashtable hashData = new Hashtable();
        
        if (Double.parseDouble(InputRefundAmt) <= 0)
        {
            strMessage = "退貨金額必須大於0";
            hashData.put("FLAG", String.valueOf(boolFlag));
            hashData.put("MESSAGE", strMessage);
            return hashData;
        }
        if (hashBalanceData.size() == 0) 
        {
            strMessage = "查無交易資料";
            hashData.put("FLAG", String.valueOf(boolFlag));
            hashData.put("MESSAGE", strMessage);
            return hashData;
        } 
        /*開始進行退貨處理
         * BalanceAmt1:剩餘可退貨金額
         * AuthAmt:授權金額
         * BalanceRefundAmt:已退貨金額
         * OverRefundLimit:設定可超額退貨金額，可超額多少錢
         * */
        String BalanceAmt1 = hashBalanceData.get("BALANCEAMT1").toString(); // Balance 的 AuthAmt - RefundAmt - CancelAmt
        String AuthAmt = hashBalanceData.get("AUTHAMT").toString();
        String BalanceRefundAmt = hashBalanceData.get("REFUNDAMT").toString();
        double dBalanceAmt1 = Double.parseDouble(BalanceAmt1);
        double dAuthAmt = Double.parseDouble(AuthAmt);
        double dBalanceRefundAmt = Double.parseDouble(BalanceRefundAmt);
        double dOverRefundLimit = Double.parseDouble(OverRefundLimit);
        double dInputRefundAmt = Double.parseDouble(InputRefundAmt);
        
        if (BalanceAmt1.length() == 0 || AuthAmt.length() == 0) 
        {
            strMessage = "沒有可退貨的交易資料";
            hashData.put("FLAG", String.valueOf(boolFlag));
            hashData.put("MESSAGE", strMessage);
            return hashData;
        }
        //開始進行退貨處理
        if (OverRefundLimit.length() == 0)
            OverRefundLimit = "0";
            
        boolFlag = true;
        if (boolPartial) 
        { 
            // 可分批退貨 BalanceAmt1 = AuthAmt - RundAmt(部份退貨加總金額)
            System.out.println("BalanceAmt1=" + dBalanceAmt1 + ",OverRefundLimit=" + dOverRefundLimit + ",InputRefundAmt=" + dInputRefundAmt + ",BalanceRefundAmt=" + dBalanceRefundAmt + ",AuthAmt=" + dAuthAmt);
            if ((dBalanceAmt1 + dOverRefundLimit) < dInputRefundAmt) 
            {
                boolFlag = false;
                strMessage = "退貨金額超過上限(A)";
            }
        } 
        else 
        {
        	//被設定全額退貨，假如有設定有設定超額退貨金額，則可超額退貨
        	//沒設定超額退貨的話則input退貨金額要等於授權金額
            if (dOverRefundLimit == 0)
            {
            	if(dInputRefundAmt < dAuthAmt)
            	{
            		boolFlag = false;
            		strMessage = "不支援分批退貨";
            	}
            	else if(dInputRefundAmt > dAuthAmt)
            	{
            		boolFlag = false;
            		strMessage = "退貨金額不可大於授權金額";
            	}
            }
            else
            {
            	//有設定超額退貨，尚未退貨
            	 if(dBalanceRefundAmt == 0)
            	 {
                 	if(dInputRefundAmt < dAuthAmt)
                	{
                		boolFlag = false;
                		strMessage = "不支援分批退貨";
                	}
            	 }
            }
            if(boolFlag == false)
            {
            	//沒有檢核通過
                hashData.put("FLAG", String.valueOf(boolFlag));
                hashData.put("MESSAGE", strMessage);
                return hashData;
            }
            
            //(已退貨金額BalanceRefundAmt+此次輸入退貨金額 InputRefundAmt) 不可大於  (授權金額AuthAmt+設定的超額退貨金額OverRefundLimit)
            if((dBalanceRefundAmt + dInputRefundAmt) > (dAuthAmt + dOverRefundLimit))
            {
                boolFlag = false;
                strMessage = "退貨金額超過上限(C)";
            }
        }
        hashData.put("FLAG", String.valueOf(boolFlag));
        hashData.put("MESSAGE", strMessage);
        return hashData;
    }

//    /**
//     * 取得檔案退貨列表資料(Billing+Balance)
//     * @param String MerchantID     特店代號
//     * @param String SubMID         特店代號
//     * @param String OrderID        訂單代號
//     * @param String TransCode      交易代碼 00-購貨
//     * @return Hashtable            列表資料
//     */
//    public Hashtable get_BatchRefund_List(String MerchantID, String SubMID, String OrderID, String TransCode) 
//    {
//        Hashtable hashData = new Hashtable();
//        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
//        
//        DataBaseBean SysBean = new DataBaseBean();
//        if (MerchantID.length() > 0 && SubMID.length() > 0 && OrderID.length() > 0) 
//        {
//            StringBuffer Sql = new StringBuffer("SELECT A.*, B.AUTHAMT, B.REFUNDAMT, B.CANCELAMT, B.CAPTUREAMT, (B.AUTHAMT - B.REFUNDAMT - B.CANCELAMT)  BALANCEAMT1  FROM BILLING A, BALANCE B ");
//            Sql.append(" WHERE A.MERCHANTID = '" + MerchantID + "' ");
//            Sql.append(" AND A.SUBMID = '" + SubMID + "' ");
//            Sql.append(" AND A.ORDERID = '" + OrderID + "' ");
//            
//            if (TransCode.length() > 0) 
//            {
//                Sql.append(" AND A.TRANSCODE = '" + TransCode + "' ");
//            }
//            
//            Sql.append(" AND A.MERCHANTID = B.MERCHANTID AND A.SUBMID = B.SUBMID AND A.ORDERID = B.ORDERID ORDER BY A.TRANSDATE, A.TRANSTIME ");
//            //System.out.println("Sql=" + Sql);
//            
//            try 
//            {
//                arrayBillingData = (ArrayList) SysBean.executeSQL(Sql.toString(), "select");
//            } 
//            catch (Exception ex) 
//            {
//                System.out.println(ex.getMessage());
//                log_systeminfo.debug(ex.toString());
//            }
//            
//            if (arrayBillingData == null) 
//            {
//                arrayBillingData = new ArrayList();
//            }
//        }
//        
//        if (arrayBillingData.size() > 0)
//            hashData = (Hashtable) arrayBillingData.get(0);
//            
//        return hashData;
//    }

    /* Override get_BatchRefund_List with DataBaseBean parameter */
    public Hashtable get_BatchRefund_List(DataBaseBean2 sysBean, String MerchantID, String SubMID, String OrderID, String TransCode) 
    {
        Hashtable hashData = new Hashtable();
        ArrayList arrayBillingData = new ArrayList(); // 顯示訂單交易資料
        
        // DataBaseBean SysBean = new DataBaseBean();
        if (MerchantID.length() > 0 && SubMID.length() > 0 && OrderID.length() > 0) 
        {
        	StringBuffer sSQLSB = new StringBuffer();
    		sysBean.ClearSQLParam();
    		sSQLSB.append("SELECT A.*, B.AUTHAMT, B.REFUNDAMT, B.CANCELAMT, B.CAPTUREAMT, (B.AUTHAMT - B.REFUNDAMT - B.CANCELAMT)  BALANCEAMT1  FROM BILLING A, BALANCE B ");
            sSQLSB.append(" WHERE A.MERCHANTID = ? ");
            sSQLSB.append(" AND A.SUBMID = ? ");
            sSQLSB.append(" AND A.ORDERID = ? ");
            
            sysBean.AddSQLParam(emDataType.STR, MerchantID);
            sysBean.AddSQLParam(emDataType.STR, SubMID);
            sysBean.AddSQLParam(emDataType.STR, OrderID);
            
            if (TransCode.length() > 0) 
            {
                sSQLSB.append(" AND A.TRANSCODE = ? ");
                sysBean.AddSQLParam(emDataType.STR, MerchantID);
            }
            
            sSQLSB.append(" AND A.MERCHANTID = B.MERCHANTID AND A.SUBMID = B.SUBMID AND A.ORDERID = B.ORDERID ORDER BY A.TRANSDATE, A.TRANSTIME ");
            // System.out.println("sSQLSB=" + sSQLSB.toString());
            
            try 
            {
            	/** 2023/05/17 改用 DataBaseBean2.java 的 QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-028 (No Need Test) */
                arrayBillingData = (ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString());
            } 
            catch (Exception ex) 
            {
                System.out.println(ex.getMessage());
                log_systeminfo.debug(ex.toString());
            }
            
            if (arrayBillingData == null) 
            {
                arrayBillingData = new ArrayList();
            }
        }
        
        if (arrayBillingData.size() > 0)
            hashData = (Hashtable) arrayBillingData.get(0);
            
        return hashData;
    }
    /**
     * 判斷是否允許檔案退貨
     * @param ArrayList arrayBillingData 退貨列表資料
     * @param String    RefundAmt        退貨金額
     * @param String    MerchantPartial  特店分批註記
     * @param boolean   TerminalPartial  端末機分批註記
     * @param boolean   TransModePartial 交易模式分批註記(一般:true 分期.紅利:false)
     * @param boolean   OverRefundLimit  超額退貨金額  2007.10.19 修改
     * @return Hashtable                 判斷結果 FLAG:退貨判斷(String)  MESSAGE:顯示結果(String)
     */
    public Hashtable check_BatchRefund_Status(Hashtable hashBillingData, String RefundAmt, String MerchantPartial, boolean TerminalPartial, boolean TransModePartial, String OverRefundLimit)
    {
        Hashtable hashData = new Hashtable();
        String strMessage = ""; // 顯示結果
        boolean boolFlag = false; // 是否能做退貨
        boolean boolPartial = false;
        
        if (MerchantPartial.equalsIgnoreCase("Y") && TerminalPartial && TransModePartial)
            boolPartial = true;
            
        if (hashBillingData.size() == 0) 
        {
            strMessage = "查無交易資料";
        } 
        else 
        {
            try 
            {
                 String TranCode = hashBillingData.get("TRANSCODE").toString();
                 
                 if (TranCode.equalsIgnoreCase("00")) 
                 { 
                    // 購貨交易
                    String TransAmt = hashBillingData.get("TRANSAMT").toString();
                    String BalanceAmt = hashBillingData.get("BALANCEAMT"). toString(); //  Billing 的 BalanceAmt
                    String ReversalFlag = hashBillingData.get("REVERSALFLAG"). toString();
                    String BalanceAmt1 = hashBillingData.get("BALANCEAMT1"). toString(); // Balance 的 AuthAmt - RefundAmt - CancelAmt
                    String AuthAmt = hashBillingData.get("AUTHAMT"). toString(); // Balance 的 AuthAmt
                    
                    if (TransAmt.length() == 0 || BalanceAmt.length() == 0 || BalanceAmt1.length() == 0) 
                    {
                        strMessage = "查無交易資料";
                    } 
                    else 
                    {
                        boolFlag = true;
                        String CancelAmt = hashBillingData.get("CANCELAMT"). toString();
                        if (Double.parseDouble(CancelAmt) == Double.parseDouble(AuthAmt)) 
                        {
                            strMessage = "交易已取消";
                            boolFlag = false;
                        }
                        
                        if (ReversalFlag.equalsIgnoreCase("1")) 
                        {
                            strMessage = "交易已沖正";
                            boolFlag = false;
                        }
                        
                        if (Double.parseDouble(BalanceAmt1) <= 0) 
                        {
                            if (Double.parseDouble(CancelAmt) == Double.parseDouble(TransAmt)) 
                            {
                                strMessage = "交易已取消";
                            } 
                            else 
                            {
                                strMessage = "交易無法退貨";
                            }
                            
                            boolFlag = false;
                        }
                        
                        if (boolFlag) 
                        {
                            if (boolPartial) 
                            { 
                                // 可分批退貨
                                if ((Double.parseDouble(BalanceAmt1) + Double.parseDouble(OverRefundLimit)) < Double.parseDouble(RefundAmt)) 
                                {
                                    boolFlag = false;
                                    strMessage = "退貨金額超過上限(E)";
                                }
                            } 
                            else 
                            {
                                if (((Double.parseDouble(AuthAmt)+ Double.parseDouble(OverRefundLimit)) < Double.parseDouble(RefundAmt))) 
                                {
                                    boolFlag = false;
                                    strMessage = "退貨金額超過上限(F)";
                                }
                                
                                if (Double.parseDouble(AuthAmt) > Double.parseDouble(RefundAmt)) 
                                {
                                    boolFlag = false;
                                    strMessage = "不支援分批退貨";
                                }
                            }
                        }
                    }
                }
            } 
            catch (Exception ex) 
            {
                System.out.println(ex.getMessage());
                log_systeminfo.debug(ex.toString());
            }
        }
        
        if (boolFlag) 
        {
            strMessage = "可進行退貨交易";
        }
        
        hashData.put("FLAG", String.valueOf(boolFlag));
        hashData.put("MESSAGE", strMessage);
        
        return hashData;
    }
}
