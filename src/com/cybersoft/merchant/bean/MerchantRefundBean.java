/************************************************************
 * <p>#File Name:	MerchantRefundQueryCtl.java	</p>
 * <p>#Description:	            			</p>
 * <p>#Create Date:	2007/09/26		        </p>
 * <p>#Company:  	cybersoft	        	</p>
 * <p>#Notice:   					</p>
 * @author		Shirley Lin
 * @since		SPEC version
 * @version	0.1	2007/09/26	Shirley Lin
 * ���ʻ���
 * 20200306 202002100619-00 saint EC�h�f�ˮ�
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
     * ���o�h�f�C����(Billing+Balance)
     * @param String MerchantID     �S���N��
     * @param String SubMID         �S���N��
     * @param String OrderType      �d�߭q�����O (Order:�S�����w�渹 System:�t�Ϋ��w�渹 )
     * @param String OrderID        �q��N��
     * @return ArrayList            �C����
     */
    public ArrayList<Hashtable<String,String>> get_Refund_List(String MerchantID, String SubMID, String OrderType, String OrderID) 
    {
		StringBuffer sSQLSB = new StringBuffer();
		DataBaseBean2 DBBean = new DataBaseBean2();
		ArrayList<Hashtable<String,String>> arrayBillingData = null; // ��ܭq�������;
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
                // �HOrderID
            	sSQLSB.append(" AND ORDERID =? ");
           } 
            else 
            {
            	sSQLSB.append(" AND SYS_ORDERID =? ");
            }
            DBBean.AddSQLParam(emDataType.STR, OrderID); // 3
            
            /******    ���p�d�ݨD  DalePeng   20150604 ---Start---	******/
            sSQLSB.append(" AND LTRIM(RTRIM(PAN)) != '62XX' ");
            /******    ���p�d�ݨD  DalePeng   20150604 ---End---	******/
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
		ArrayList<Hashtable<String,String>> arrayBillingData = null; // ��ܭq�������;
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
                // �HOrderID
            	sSQLSB.append(" AND ORDERID =? ");
            } 
            else 
            {
            	sSQLSB.append(" AND SYS_ORDERID =? ");
            }
            /******    ���p�d�ݨD  DalePeng   20150604 ---Start---	******/
            sSQLSB.append(" AND LTRIM(RTRIM(PAN)) != '62XX' ");
            /******    ���p�d�ݨD  DalePeng   20150604 ---End---	******/
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
     * �P�_�O�_���\�h�f
     * @param ArrayList arrayBillingData �h�f�C����
     * @param String    OverRefundLimit  �W�B�h�f���B
     * @return Hashtable                 �P�_���G FLAG:�h�f�P�_(String)  MESSAGE:��ܵ��G(String)
     */
    public Hashtable check_Refund_Status(ArrayList arrayBillingData, String OverRefundLimit) 
    {
        Hashtable hashData = new Hashtable();
        boolean boolFlag = false; // �O�_�వ�h�f
        String strMessage = ""; // ��ܵ��G
        
        if (arrayBillingData.size() == 0) 
        {
            strMessage = "�d�L������";
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
                        // �ʳf���
                        String TransAmt     = hashTmpData.get("TRANSAMT").toString();
                        String BalanceAmt   = hashTmpData.get("BALANCEAMT").toString(); //  Billing �� BalanceAmt
                        String ReversalFlag = hashTmpData.get("REVERSALFLAG").toString();
                        String BalanceAmt1  = hashTmpData.get("BALANCEAMT1").toString(); // Balance �� AuthAmt - RefundAmt - CancelAmt
                        String RefundAmt    = hashTmpData.get("REFUNDAMT").toString(); //  Balance �� RefundAmt
//                        System.out.println("BalanceAmt1=" + BalanceAmt1);
                                 
                        if (TransAmt.length() == 0 || BalanceAmt.length() == 0 || BalanceAmt1.length() == 0) 
                        {
                            strMessage = "�d�L������";
                        } 
                        else 
                        {
                            boolFlag = true;
                            String CancelAmt = hashTmpData.get("CANCELAMT").toString();
                            String AuthAmt = hashTmpData.get("AUTHAMT").toString();
                            String CardType    = hashTmpData.get("CARD_TYPE").toString(); // Billing �� CardType
                            String Pan          = hashTmpData.get("PAN").toString();       // Billing �� Pan
                            
                            if (Double.parseDouble(CancelAmt) == Double.parseDouble(AuthAmt)) 
                            {
                                strMessage = "����w����";
                                boolFlag = false;
                            }
                            
                            if (ReversalFlag.equalsIgnoreCase("1")) 
                            {
                                strMessage = "����w�R��";
                                boolFlag = false;
                            }
                            
                            if (Double.parseDouble(RefundAmt) > 0 && Double.parseDouble(RefundAmt) >= Double.parseDouble(AuthAmt)) 
                            {
                                strMessage = "����L�k�h�f";
                                boolFlag = false;
                            } 
                            else 
                            {
                                if ((Double.parseDouble(OverRefundLimit) + Double.parseDouble(BalanceAmt1)) <= 0) 
                                {
                                    strMessage = "����L�k�h�f";
                                    boolFlag = false;
                                }
                            }
                            
//                            /******    ���p�d�ݨD  DalePeng   20150604 ---Start---	******/
//                            if(CardType.equalsIgnoreCase("C"))
//                            {
//                            	if(Pan.equalsIgnoreCase("62XX"))
//                            	{
//                            		 strMessage = "�d�����i��62XX";
//                                     boolFlag = false;
//                            	}
//                            }
//                            /******    ���p�d�ݨD  DalePeng   20150604 ---End---	******/
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
     * �P�_�h�f���B
     * @param Hashtable hashBalanceData  �b���ˮָ��(Balance)
     * @param Boolean boolPartial        �O�_���ѳ����h�f true:�i false:���i (�ѦҺݥ����v��Permit_Partial_Refund  �Y�������ά��Q��false)
     * @param String InputRefundAmt      �S����J�h�f���B
     * @param String OverRefundLimit     �W�B�h�f���B  2007.010.19�ק�
     * @return Hashtable                 �P�_���G FLAG:�h�f�P�_(String)  MESSAGE:��ܵ��G(String)
     */
     
    public Hashtable check_Refund_Amt(Hashtable hashBalanceData, boolean boolPartial, String InputRefundAmt, String OverRefundLimit) 
    {
        boolean boolFlag = false; // �O�_�వ�h�f
        String strMessage = ""; // ��ܵ��G
        Hashtable hashData = new Hashtable();
        
        if (Double.parseDouble(InputRefundAmt) <= 0)
        {
            strMessage = "�h�f���B�����j��0";
            hashData.put("FLAG", String.valueOf(boolFlag));
            hashData.put("MESSAGE", strMessage);
            return hashData;
        }
        if (hashBalanceData.size() == 0) 
        {
            strMessage = "�d�L������";
            hashData.put("FLAG", String.valueOf(boolFlag));
            hashData.put("MESSAGE", strMessage);
            return hashData;
        } 
        /*�}�l�i��h�f�B�z
         * BalanceAmt1:�Ѿl�i�h�f���B
         * AuthAmt:���v���B
         * BalanceRefundAmt:�w�h�f���B
         * OverRefundLimit:�]�w�i�W�B�h�f���B�A�i�W�B�h�ֿ�
         * */
        String BalanceAmt1 = hashBalanceData.get("BALANCEAMT1").toString(); // Balance �� AuthAmt - RefundAmt - CancelAmt
        String AuthAmt = hashBalanceData.get("AUTHAMT").toString();
        String BalanceRefundAmt = hashBalanceData.get("REFUNDAMT").toString();
        double dBalanceAmt1 = Double.parseDouble(BalanceAmt1);
        double dAuthAmt = Double.parseDouble(AuthAmt);
        double dBalanceRefundAmt = Double.parseDouble(BalanceRefundAmt);
        double dOverRefundLimit = Double.parseDouble(OverRefundLimit);
        double dInputRefundAmt = Double.parseDouble(InputRefundAmt);
        
        if (BalanceAmt1.length() == 0 || AuthAmt.length() == 0) 
        {
            strMessage = "�S���i�h�f��������";
            hashData.put("FLAG", String.valueOf(boolFlag));
            hashData.put("MESSAGE", strMessage);
            return hashData;
        }
        //�}�l�i��h�f�B�z
        if (OverRefundLimit.length() == 0)
            OverRefundLimit = "0";
            
        boolFlag = true;
        if (boolPartial) 
        { 
            // �i����h�f BalanceAmt1 = AuthAmt - RundAmt(�����h�f�[�`���B)
            System.out.println("BalanceAmt1=" + dBalanceAmt1 + ",OverRefundLimit=" + dOverRefundLimit + ",InputRefundAmt=" + dInputRefundAmt + ",BalanceRefundAmt=" + dBalanceRefundAmt + ",AuthAmt=" + dAuthAmt);
            if ((dBalanceAmt1 + dOverRefundLimit) < dInputRefundAmt) 
            {
                boolFlag = false;
                strMessage = "�h�f���B�W�L�W��(A)";
            }
        } 
        else 
        {
        	//�Q�]�w���B�h�f�A���p���]�w���]�w�W�B�h�f���B�A�h�i�W�B�h�f
        	//�S�]�w�W�B�h�f���ܫhinput�h�f���B�n������v���B
            if (dOverRefundLimit == 0)
            {
            	if(dInputRefundAmt < dAuthAmt)
            	{
            		boolFlag = false;
            		strMessage = "���䴩����h�f";
            	}
            	else if(dInputRefundAmt > dAuthAmt)
            	{
            		boolFlag = false;
            		strMessage = "�h�f���B���i�j����v���B";
            	}
            }
            else
            {
            	//���]�w�W�B�h�f�A�|���h�f
            	 if(dBalanceRefundAmt == 0)
            	 {
                 	if(dInputRefundAmt < dAuthAmt)
                	{
                		boolFlag = false;
                		strMessage = "���䴩����h�f";
                	}
            	 }
            }
            if(boolFlag == false)
            {
            	//�S���ˮֳq�L
                hashData.put("FLAG", String.valueOf(boolFlag));
                hashData.put("MESSAGE", strMessage);
                return hashData;
            }
            
            //(�w�h�f���BBalanceRefundAmt+������J�h�f���B InputRefundAmt) ���i�j��  (���v���BAuthAmt+�]�w���W�B�h�f���BOverRefundLimit)
            if((dBalanceRefundAmt + dInputRefundAmt) > (dAuthAmt + dOverRefundLimit))
            {
                boolFlag = false;
                strMessage = "�h�f���B�W�L�W��(C)";
            }
        }
        hashData.put("FLAG", String.valueOf(boolFlag));
        hashData.put("MESSAGE", strMessage);
        return hashData;
    }

//    /**
//     * ���o�ɮװh�f�C����(Billing+Balance)
//     * @param String MerchantID     �S���N��
//     * @param String SubMID         �S���N��
//     * @param String OrderID        �q��N��
//     * @param String TransCode      ����N�X 00-�ʳf
//     * @return Hashtable            �C����
//     */
//    public Hashtable get_BatchRefund_List(String MerchantID, String SubMID, String OrderID, String TransCode) 
//    {
//        Hashtable hashData = new Hashtable();
//        ArrayList arrayBillingData = new ArrayList(); // ��ܭq�������
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
        ArrayList arrayBillingData = new ArrayList(); // ��ܭq�������
        
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
            	/** 2023/05/17 ��� DataBaseBean2.java �� QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-028 (No Need Test) */
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
     * �P�_�O�_���\�ɮװh�f
     * @param ArrayList arrayBillingData �h�f�C����
     * @param String    RefundAmt        �h�f���B
     * @param String    MerchantPartial  �S��������O
     * @param boolean   TerminalPartial  �ݥ���������O
     * @param boolean   TransModePartial ����Ҧ�������O(�@��:true ����.���Q:false)
     * @param boolean   OverRefundLimit  �W�B�h�f���B  2007.10.19 �ק�
     * @return Hashtable                 �P�_���G FLAG:�h�f�P�_(String)  MESSAGE:��ܵ��G(String)
     */
    public Hashtable check_BatchRefund_Status(Hashtable hashBillingData, String RefundAmt, String MerchantPartial, boolean TerminalPartial, boolean TransModePartial, String OverRefundLimit)
    {
        Hashtable hashData = new Hashtable();
        String strMessage = ""; // ��ܵ��G
        boolean boolFlag = false; // �O�_�వ�h�f
        boolean boolPartial = false;
        
        if (MerchantPartial.equalsIgnoreCase("Y") && TerminalPartial && TransModePartial)
            boolPartial = true;
            
        if (hashBillingData.size() == 0) 
        {
            strMessage = "�d�L������";
        } 
        else 
        {
            try 
            {
                 String TranCode = hashBillingData.get("TRANSCODE").toString();
                 
                 if (TranCode.equalsIgnoreCase("00")) 
                 { 
                    // �ʳf���
                    String TransAmt = hashBillingData.get("TRANSAMT").toString();
                    String BalanceAmt = hashBillingData.get("BALANCEAMT"). toString(); //  Billing �� BalanceAmt
                    String ReversalFlag = hashBillingData.get("REVERSALFLAG"). toString();
                    String BalanceAmt1 = hashBillingData.get("BALANCEAMT1"). toString(); // Balance �� AuthAmt - RefundAmt - CancelAmt
                    String AuthAmt = hashBillingData.get("AUTHAMT"). toString(); // Balance �� AuthAmt
                    
                    if (TransAmt.length() == 0 || BalanceAmt.length() == 0 || BalanceAmt1.length() == 0) 
                    {
                        strMessage = "�d�L������";
                    } 
                    else 
                    {
                        boolFlag = true;
                        String CancelAmt = hashBillingData.get("CANCELAMT"). toString();
                        if (Double.parseDouble(CancelAmt) == Double.parseDouble(AuthAmt)) 
                        {
                            strMessage = "����w����";
                            boolFlag = false;
                        }
                        
                        if (ReversalFlag.equalsIgnoreCase("1")) 
                        {
                            strMessage = "����w�R��";
                            boolFlag = false;
                        }
                        
                        if (Double.parseDouble(BalanceAmt1) <= 0) 
                        {
                            if (Double.parseDouble(CancelAmt) == Double.parseDouble(TransAmt)) 
                            {
                                strMessage = "����w����";
                            } 
                            else 
                            {
                                strMessage = "����L�k�h�f";
                            }
                            
                            boolFlag = false;
                        }
                        
                        if (boolFlag) 
                        {
                            if (boolPartial) 
                            { 
                                // �i����h�f
                                if ((Double.parseDouble(BalanceAmt1) + Double.parseDouble(OverRefundLimit)) < Double.parseDouble(RefundAmt)) 
                                {
                                    boolFlag = false;
                                    strMessage = "�h�f���B�W�L�W��(E)";
                                }
                            } 
                            else 
                            {
                                if (((Double.parseDouble(AuthAmt)+ Double.parseDouble(OverRefundLimit)) < Double.parseDouble(RefundAmt))) 
                                {
                                    boolFlag = false;
                                    strMessage = "�h�f���B�W�L�W��(F)";
                                }
                                
                                if (Double.parseDouble(AuthAmt) > Double.parseDouble(RefundAmt)) 
                                {
                                    boolFlag = false;
                                    strMessage = "���䴩����h�f";
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
            strMessage = "�i�i��h�f���";
        }
        
        hashData.put("FLAG", String.valueOf(boolFlag));
        hashData.put("MESSAGE", strMessage);
        
        return hashData;
    }
}
