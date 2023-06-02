/**���ʻ���
 * 2020.03.06 HKP Add DB�s�u�ѼƤƧK��SQL INJECTION
 * 202007070145-00 20200707 HKP ���]��|�ݨDUI�W�[���дڥ\��
 * 202208090854-01 20220801 HKP PCI-DSS�󴫱K�X����MD5��SHA256�AMD5�������׬�32�ASHA256�������׬�64�A�H�����קP�_�O�_��MD5�A�ܧ�᪺�K�X�@�ߨϥ�SHA256�PLOG�B��
 *                 20221124 Frog Jump Co., YC White Scan
 **/
package com.cybersoft.bean;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.jconfig.Configuration;

import com.cybersoft.common.Util;
import com.fubon.security.filter.SecurityTool;
import com.fubon.tp.util.XSSUtils;

public class DataBaseBean2 {
	private Connection mConn = null;
	/** private ResultSet rs = null; */
	private ArrayList<SQLParam> mSQLParam = new ArrayList<>();
	private String debugMessage = "--**********����DB���s�s�u********--";
	public static final LogUtils log_DataBaseBean2 = new LogUtils("DBBean");
	
    private emDB mDB= emDB.EC;
    /** private String sqlPrefix = "select"; */
	public DataBaseBean2() {
		mDB=emDB.EC;
		mSQLParam.clear();
	}	
	public DataBaseBean2(emDB useDB) {
		mDB=useDB;
		mSQLParam.clear();
	}	
	private String mMessage = "";
	public String Message(){
		return mMessage;
	}
	public void ClearSQLParam(){
		mSQLParam.clear();
	}
	//*******
	public void AddSQLParam(String dataType,int value){
		SQLParam param = new SQLParam(dataType,value);
		mSQLParam.add(param);
	}
	public void AddSQLParam(String dataType,String value){
		SQLParam param = new SQLParam(dataType,value);
		mSQLParam.add(param);
	}
	//�������
	public void AddSQLParam(String dataType,String value,int iDataLength){
    	if(value == null) value="";
    	if(value.length()>iDataLength) value = value.substring(0,iDataLength);

		SQLParam param = new SQLParam(dataType,value);
		mSQLParam.add(param);
	}
	public void AddSQLParam(String dataType,Date value){
		SQLParam param = new SQLParam(dataType,value);
		mSQLParam.add(param);
	}
	public void AddSQLParam(String dataType,double value){
		SQLParam param = new SQLParam(dataType,value);
		mSQLParam.add(param);
	}
	public void AddSQLParam(String dataType,long value){
		SQLParam param = new SQLParam(dataType,value);
		mSQLParam.add(param);
	}
	//*******
	private void WriteLog(String sSQL){
		WriteLog(sSQL,mSQLParam);
	}
	
	/**
	 * Write Log
	 * @param sSQL
	 * @param oSQLParam
	 */
	private void WriteLog(String sSQL, ArrayList<SQLParam> oSQLParam){
    	SQLParam param;
    	StringBuilder sb = new StringBuilder();
    	sb.append("SQL=").append(sSQL).append(",Param=");
    	String sValue="";
    	for(int i = 0 ;i < oSQLParam.size();i++){
			param = oSQLParam.get(i);
			sb.append("[").append(i).append(",");
			if(param.getType().equals(emDataType.STR)){
				sValue = param.getValueString();
				if(sValue == null) sValue = "";
				if(sValue.trim().length() == 16 || sValue.trim().length() == 19) {
					sValue = Util.maskCardNo(sValue); //20220804 LOG�ɥu�n�O16�B19�X�@�߶i��B��
				}else {
					int iStartPos;
					int iEndPos;
					String sData;
					String sMasked;
					//PAN
					iStartPos = sValue.indexOf("<PAN>");
					if(iStartPos >=0) {
						iStartPos+=5;
						iEndPos =  sValue.indexOf("</PAN>");
						if(iStartPos < iEndPos) {
							sData = sValue.substring(iStartPos,iEndPos);
							sMasked = Util.maskCardNo(sData);
							sValue= sValue.substring(0,iStartPos)+sMasked+sValue.substring(iEndPos);
						}
					}
				}
				
				sb.append(sValue);
			}else if(param.getType().equals(emDataType.INT)){
				sb.append(param.getValueInt());
			}else if(param.getType().equals(emDataType.LONG)){
				sb.append(param.getValueLong());
			}else if(param.getType().equals(emDataType.DOUBLE)){
				sb.append(param.getValueDouble());
			}else if(param.getType().equals(emDataType.DATE)){
				sb.append(param.getValueDate());
			}
			sb.append("],");
    	}
    	log_DataBaseBean2.debug(sb);
	}
	/* 
	 *  �d����ۤv�|Connect close.
	 * */
	/**���ʻ���
	 *                 20221124 Frog Jump Co., YC White Scan/A03 Injection
	 *                 20221124 Frog Jump Co., YC White Scan/A03 Injection/Dynamic Code Evaluation: JNDI Reference Injection
	 **/	 
    public ArrayList<Hashtable<String,String>> QuerySQLByParam(String sSQL){
    	mMessage = "";
    	ResultSet rs = null;
    	ArrayList<Hashtable<String,String>> result=null;
    	SQLParam param;
    	this.WriteLog(sSQL);
    	if(sSQL == null || sSQL.equals("")){
    		mMessage = "SQL���i���Ŧr��";
    		return result; 
    	}
    	try{
        	/** ���pSQL����s�u���ѫh���s�@����A����@�� */
        	for(int j=0 ;j<=1;j++){
        		
        		if(!this.makeConn()) {
        			continue;
        		}
        		
            	try(PreparedStatement prestmt =mConn.prepareStatement(SecurityTool.output(sSQL))){
                	if(prestmt == null) return result;
        			//�m��where������
        			
        			for(int i = 0 ;i < mSQLParam.size();i++){
        				param = mSQLParam.get(i);
        				if(param.getType().equals(emDataType.STR)){
        					prestmt.setString(i+1, param.getValueString());
        				}else if(param.getType().equals(emDataType.INT)){
        					int k = param.getValueInt();
        					prestmt.setInt(i+1,k);
        				}else if(param.getType().equals(emDataType.LONG)){
        					long k = param.getValueLong();
        					prestmt.setLong(i+1,k);
        				}else if(param.getType().equals(emDataType.DOUBLE)){
        					double k = param.getValueDouble();
        					prestmt.setDouble(i+1,k );
        				}else if(param.getType().equals(emDataType.DATE)){
        					prestmt.setDate(i+1,param.getValueDate() );
        				}
        			}
        			//�m��where������end
        			rs = prestmt.executeQuery();
            		
        			/** marked (By : YC) */
            		/**break;*/
        			
                	/** result*/
        			result= transferFormat(rs);
        			
        			/** use (By : YC) */
            		break;        			
            	}catch(Exception ex){
            		if(ex.getMessage().indexOf("connection abort")>=0){
    					log_DataBaseBean2.error(debugMessage+ex.getMessage());
    					mConn = null;
            		}
            		
            		result= new ArrayList<Hashtable<String,String>>();
            	}finally{
            		try{
            			if (mConn != null){mConn.close();mConn = null;}
            			
            		}catch(Exception e){
            			e.printStackTrace();
            			log_DataBaseBean2.error(e.getMessage());
            			
            			result= new ArrayList<Hashtable<String,String>>();
            		}
            	}
        	}
        	

    	}catch(Exception e){
    		log_DataBaseBean2.error("DataBaseBean�G"+e.getMessage());
    		mMessage = e.getMessage(); 
    	}finally{
    		try{
    			if (mConn != null){mConn.close();mConn = null;}
    			
    		}catch(Exception e){
    			e.printStackTrace();
    			log_DataBaseBean2.error(e.getMessage());
    		}
    	}
    	return result;
    }
	public List<Map<String, Object>> QuerySQLByParam2(String sSQL) {
		mMessage = "";
		ResultSet rs = null;
		List<Map<String, Object>> result=null;
    	SQLParam param;
    	this.WriteLog(sSQL);
    	
    	if(sSQL == null || sSQL.equals("")){
    		mMessage = "SQL���i���Ŧr��";
    		return result;
    	}
    	try{
    		//���pSQL����s�u���ѫh���s�@����A����@��
        	for(int j=0 ;j<=1;j++){
        		
        		if(this.makeConn() == false) {
        			continue;
        		}
        		
            	try(PreparedStatement prestmt = mConn.prepareStatement(SecurityTool.output(sSQL))){
                	if(prestmt == null) {
                		return result;
                	}
                	//�m��where������
        			for(int i = 0 ;i < mSQLParam.size();i++){
        				param = mSQLParam.get(i);
        				if(param.getType().equals(emDataType.STR)){
        					prestmt.setString(i+1, param.getValueString());
        				}else if(param.getType().equals(emDataType.INT)){
        					int k = param.getValueInt();
        					prestmt.setInt(i+1,k);
        				}else if(param.getType().equals(emDataType.LONG)){
        					long k = param.getValueLong();
        					prestmt.setLong(i+1,k);
        				}else if(param.getType().equals(emDataType.DOUBLE)){
        					double k = param.getValueDouble();
        					prestmt.setDouble(i+1,k );
        				}else if(param.getType().equals(emDataType.DATE)){
        					prestmt.setDate(i+1,param.getValueDate() );
        				}else if(param.getType().equals(emDataType.BigDecimal)){
        					prestmt.setBigDecimal(i+1, param.getValueBigDecimal());
        				}
        			}
        			//�m��where������end 
        			rs = prestmt.executeQuery();            		
            		break;
            	}catch(Exception ex){
            		if(ex.getMessage().indexOf("connection abort")>=0){
    					log_DataBaseBean2.debug(debugMessage);
    					mConn = null;
            		}
            	}
        	}
        	
        	/** result*/
			result= transferFormat2(rs);
    	}catch(Exception e){
    		log_DataBaseBean2.error("DataBaseBean�G"+e.getMessage());
    		mMessage = e.getMessage(); 
    	}finally{
    		try{
    			if (mConn != null){mConn.close();mConn = null;}
    			
    		}catch(Exception e){
    			e.printStackTrace();
    			log_DataBaseBean2.error(e.getMessage());
    		}
    	}
    	return result;
	}
    
    /*
     * ����浧insert,update,delete
     * */
    public boolean executeSQL(String sSQL){
    	ResultSet rs = null;
    	if(sSQL==null || sSQL.trim().length()==0){
    		mMessage="SQL string is null"; 
    		return false;
    	}
    	
    	mMessage = "";
    	this.WriteLog(sSQL);
    	
    	/** ���pSQL����s�u���ѫh���s�@����A����@�� */
    	for(int j=0 ;j<=1;j++){
    		
    		if(!this.makeConn()) {
    			continue;
    		}
    		
        	try(PreparedStatement prestmt =mConn.prepareStatement(SecurityTool.output(sSQL))){
        		
            	if(prestmt == null) {
            		return false;
            	}
            	
    			/** �m�������� */
    			SQLParam param;
    			
    			for(int i = 0 ;i < mSQLParam.size();i++){
    				param = mSQLParam.get(i);
    				if(param.getType().equals(emDataType.STR)){
    					prestmt.setString(i+1, param.getValueString());
    				}else if(param.getType().equals(emDataType.INT)){
    					int k = param.getValueInt();
    					prestmt.setInt(i+1,k);
    				}else if(param.getType().equals(emDataType.LONG)){
    					long k = param.getValueLong();
    					prestmt.setLong(i+1,k);
    				}else if(param.getType().equals(emDataType.DOUBLE)){
    					double k = param.getValueDouble();
    					prestmt.setDouble(i+1,k );
    				}else if(param.getType().equals(emDataType.DATE)){
    					prestmt.setDate(i+1,param.getValueDate() );
    				}
    			}
    			
    			/** �m��������end */
    			rs = prestmt.executeQuery();
    			return true;
    		} catch(Exception ex){
    			if(ex.getMessage().indexOf("connection abort")>=0){
					log_DataBaseBean2.debug(debugMessage);
					mConn = null;
        		}
        	} finally {
    			try{
    				if (mConn != null) {
    	            	mConn.close();
    	            	mConn = null;
    	            }
    			}catch(Exception ex){
    				ex.printStackTrace();
    				log_DataBaseBean2.error(ex.getMessage());
    			}
    		}
    	}
        return true;
    }
    public Connection getConn() { //Tag:20200708-01
    	if(mConn == null) makeConn();
    	return mConn;
    }
	
	/**���ʻ���
	 *                 20221124 Frog Jump Co., YC White Scan/A03 Injection
	 *                 20221124 Frog Jump Co., YC White Scan/A03 Injection/Dynamic Code Evaluation: JNDI Reference Injection
	 **/	
	private boolean makeConn(){
		Configuration configuration = org.jconfig.ConfigurationManager.getConfiguration();
		
		try{
			String sJNDIName="";
			if(mDB.equals(emDB.EC)){
				sJNDIName = "JNDINAME";
			}else if(mDB.equals(emDB.MAPS)){
				sJNDIName = "JNDINAME_MAPS";
			}else if(mDB.equals(emDB.CPS)){
				sJNDIName = "JNDINAME_CPS";
			}else if(mDB.equals(emDB.ERM)){
				sJNDIName = "JNDINAME_ERM";
			}else{
				sJNDIName = "JNDINAME";
			}

	        String sMODE = configuration.getProperty("MODE", "--", "JDBC");
	        String sJNDINAME = configuration.getProperty(sJNDIName, "--", "JDBC");
	        
    		
			/**  �P�_�s���覡 */
			/** log_DataBaseBean2.info(sMODE); */
			if("JNDI".equals(sMODE)){
				// JNDI�覡�s��				
				Context initContext = new InitialContext();
				
				/** DataSource ds = (DataSource) initContext.lookup("java:comp/env/"+configuration.getProperty("JNDINAME", null, "JDBC")); */ 
				log_DataBaseBean2.debug("JNDI:"+sJNDINAME);
				
				DataSource ds = null;
		        boolean checkIfhasXSSContent = SecurityTool.hasXSSContent(sJNDINAME);
	    		if(!checkIfhasXSSContent) {
	    			ds = (DataSource) initContext.lookup(sJNDINAME);
	    			mConn = ds.getConnection();
	    		}else {
	    			log_DataBaseBean2.error("--checkIfhasXSSContent:true!--");
	    		}
				
			}else{
		        String sDRIVER = configuration.getProperty("DRIVER", "--","JDBC");
		        String sURL    ;
		        String sUSER   ;
		        String sPWD    ;
				Class.forName(sDRIVER);
				// JDBC�覡�s��
				// �r�Ŧ�s���A�q�t�m��󤤨��ourl�Buser�Mpwd
				if(mDB.equals(emDB.EC)){
					sURL = configuration.getProperty("URL", "--", "JDBC");
					sUSER   = configuration.getProperty("USER","--", "JDBC");
					sPWD    = configuration.getProperty("PWD", "--","JDBC");
				}else if(mDB.equals(emDB.MAPS)){
					sURL = configuration.getProperty("URL_MAPS", "--", "JDBC");
					sUSER   = configuration.getProperty("USER_MAPS","--", "JDBC");
					sPWD    = configuration.getProperty("PWD_MAPS", "--","JDBC");
				}else if(mDB.equals(emDB.CPS)){
					sURL = configuration.getProperty("URL_CPS", "--", "JDBC");
					sUSER   = configuration.getProperty("USER_CPS","--", "JDBC");
					sPWD    = configuration.getProperty("PWD_CPS", "--","JDBC");
				}else{
					sURL = configuration.getProperty("URL", "--", "JDBC");
					sUSER   = configuration.getProperty("USER","--", "JDBC");
					sPWD    = configuration.getProperty("PWD", "--","JDBC");
				}
				log_DataBaseBean2.debug("JDBC:["+sUSER+"]["+sURL+"]");
				mConn = DriverManager.getConnection(sURL, sUSER,sPWD);
			}
			return true;
		}catch(Exception e){
			log_DataBaseBean2.error("makeConn():"+e.getMessage());
			mMessage = e.getMessage(); 
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * executeTransationSQL</br>
	 * ����SQL Transation</br>
	 * @param DBTransationSQLList trnSQLlList
	 * @return boolean
	 */
	public boolean executeTransationSQL(DBTransationSQLList trnSQLlList){
		ResultSet rs = null;
		if(!makeConn()) return false;
		try{
			String sSQL;
			mConn.setAutoCommit(false);
			ArrayList<String> sqlList = trnSQLlList.getSQLList();
			for(int io = 0 ; io < sqlList.size() ; io++){
				//���XSQL
				sSQL = sqlList.get(io);
				//���XSQLParam
				ArrayList<SQLParam> oSQLParam = trnSQLlList.getSQLParam(io);
				/** ����Insert,Update,Delete SQL */
				try(PreparedStatement prestmt =mConn.prepareStatement(sSQL)){
					/** �m�������� */
					SQLParam param;
					for(int i = 0 ;i < oSQLParam.size();i++){
						param = oSQLParam.get(i);
						if(param.getType().equals(emDataType.STR)){
							prestmt.setString(i+1, param.getValueString());
						}else if(param.getType().equals(emDataType.INT)){
							int k = param.getValueInt();
							prestmt.setInt(i+1,k);
						}else if(param.getType().equals(emDataType.LONG)){
							long k = param.getValueLong();
							prestmt.setLong(i+1,k);
						}else if(param.getType().equals(emDataType.DOUBLE)){
							double k = param.getValueDouble();
							prestmt.setDouble(i+1,k );
						}else if(param.getType().equals(emDataType.DATE)){
							prestmt.setDate(i+1,param.getValueDate() );
						}
					}
					/** �m��������end */
					rs = prestmt.executeQuery();
				}
			}
			mConn.commit();
			return true;
		}catch(Exception e){
			if(mConn != null){
				try {
					mConn.rollback();
				} catch (SQLException e1) {
					log_DataBaseBean2.error(e1.getMessage());
					e1.printStackTrace();
				}
			} 
			log_DataBaseBean2.error("executeTranSQL:"+e.getMessage());
			mMessage = e.getMessage(); 
			return false;
		}finally{
    		try{
    			if (mConn != null){mConn.close();mConn = null;}
    		}catch(Exception e){
    			e.printStackTrace();
    			log_DataBaseBean2.error(e.getMessage());
    		}
		}
	}
    /**
     * setCommit
     * @param boolean
     */
	public boolean setAutoCommit(boolean flag) {
        try {
        	if(makeConn()){
            	mConn.setAutoCommit(flag);
                return true;
        	}else{
        		return false;
        	}
        }
        catch (Exception ex) {
            ex.printStackTrace();
            log_DataBaseBean2.error("Set Auto Commit Error,"+ex.getMessage());
            return false;
        }
    }
	
    /**
     * transferFormat  ��Ʈw����ഫ
     *
     * @param resultset ResultSet ��Ʈwselect�X�����
     * @return Object (�ഫ����)
    */
	/** 2023/05/03 �N private transferFormat() �אּ public transferFormat() (By : YC) *//** Test Case : IT-TESTCASE-025 */
	public ArrayList<Hashtable<String,String>> transferFormat(ResultSet resultset) {
		try{
			ArrayList<Hashtable<String,String>> showContent = new ArrayList<>();
			ResultSetMetaData metadata = resultset.getMetaData();
			int colCount = metadata.getColumnCount();
			String[] colName = new String[colCount+1];
			for(int i = 1; i<=colCount; i++) {
				colName[i] = metadata.getColumnName(i);
			}
			while(resultset.next()) {
				Hashtable<String,String> content = new Hashtable<> ();
				for(int i = 1; i<=colCount; i++) {
					content.put(colName[i],resultset.getObject(colName[i])==null?" ":resultset.getObject(colName[i]).toString());
				}
				showContent.add(content);
			}
			return showContent;
		}catch(Exception e){
			e.printStackTrace();
			log_DataBaseBean2.error(e.getMessage());
			return new ArrayList<>();
		}
	}
	
	/**
	 * transferFormat2
	 * @param resultset
	 * @return List<Map<String, Object>>
	 */
	private List<Map<String, Object>> transferFormat2(ResultSet resultset) {
		try{
			List<Map<String, Object>> showContent = new ArrayList<>();
			ResultSetMetaData metadata = resultset.getMetaData();
			int colCount = metadata.getColumnCount();
			String[] colName = new String[colCount+1];
			for(int i = 1; i<=colCount; i++) {
				colName[i] = metadata.getColumnName(i);
			}
			while(resultset.next()) {
				Map<String, Object> content = new HashMap<> ();
				for(int i = 1; i<=colCount; i++) {
					content.put(colName[i],resultset.getObject(colName[i])==null?" ":(Object)resultset.getObject(colName[i]));
				}
				showContent.add(content);
			}
			return showContent;
		}catch(Exception e){
			e.printStackTrace();
			log_DataBaseBean2.error(e.getMessage());
			return Collections.emptyList();
		}
	}
	
	/**
	 * close
	 * @throws SQLException
	 */
	public void close() throws SQLException {
		/** if(this.rs != null)    {this.rs.close(); this.rs = null;} */
		if(this.mConn != null)  {this.mConn.close(); this.mConn = null;}
	}
	
    /**
     * getSEQ
     * @param sSEQName
     * @return
     */
    public long getSEQ(String sSEQName) {
    	long lSEQ=-1;
        StringBuilder sSQLSB = new StringBuilder();
        sSQLSB.append("select ").append(sSEQName).append(".nextval as SNO from dual ");
        this.ClearSQLParam();
        ArrayList<Hashtable<String,String>> listResult = this.QuerySQLByParam(sSQLSB.toString());
        if(listResult != null && !listResult.isEmpty()){
        	lSEQ = Util.objToLong((listResult.get(0)).get("SNO"));
        }
        /** listResult.clear();listResult = null; */

    	return lSEQ;
    }
	
	/**
	 * setCommit
	 *
	 * @param flag String
	 */
	public boolean commit() {
		try {
			if (mConn == null || mConn.isClosed()) {
				mConn = getConn();
			} else {
				mConn.commit();
			}

			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			log_DataBaseBean2.debug("-- Commit Error");
			return false;
		}
	}
	
	/**
	 * setRollBack
	 *
	 * @param flag boolean
	 */
	public boolean setRollBack() {
		try {
			mConn.rollback();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			log_DataBaseBean2.debug("--RollBack Error");
			return false;
		}
	}
	
	/* 
	 *  �d����ۤv�|Connect close.
	 * */
	/**���ʻ���
	 *                 20221124 Frog Jump Co., YC White Scan/A03 Injection
	 *                 20221124 Frog Jump Co., YC White Scan/A03 Injection/Dynamic Code Evaluation: JNDI Reference Injection
	 **/
	/** 2023/05/03 �b DataBaseBean2.java �s�W public ResultSet QuerySQLByParam3(String sSQL) (By : YC) *//** Test Case : IT-TESTCASE-022 */
    public ResultSet QuerySQLByParam3(String sSQL){
    	mMessage = "";
    	ResultSet rs = null;
    	SQLParam param;
    	this.WriteLog(sSQL);
    	if(sSQL == null || sSQL.equals("")){
    		mMessage = "SQL���i���Ŧr��";
    		return rs; 
    	}
    	try{
        	/** ���pSQL����s�u���ѫh���s�@����A����@�� */
        	for(int j=0 ;j<=1;j++){
        		
        		if(!this.makeConn()) {
        			continue;
        		}
        		
            	try(PreparedStatement prestmt =mConn.prepareStatement(SecurityTool.output(sSQL))){
                	if(prestmt == null) return rs;
        			//�m��where������
        			
        			for(int i = 0 ;i < mSQLParam.size();i++){
        				param = mSQLParam.get(i);
        				if(param.getType().equals(emDataType.STR)){
        					prestmt.setString(i+1, param.getValueString());
        				}else if(param.getType().equals(emDataType.INT)){
        					int k = param.getValueInt();
        					prestmt.setInt(i+1,k);
        				}else if(param.getType().equals(emDataType.LONG)){
        					long k = param.getValueLong();
        					prestmt.setLong(i+1,k);
        				}else if(param.getType().equals(emDataType.DOUBLE)){
        					double k = param.getValueDouble();
        					prestmt.setDouble(i+1,k );
        				}else if(param.getType().equals(emDataType.DATE)){
        					prestmt.setDate(i+1,param.getValueDate() );
        				}
        			}
        			//�m��where������end
        			rs = prestmt.executeQuery();
        			
        			/** use (By : YC) */
            		break;        			
            	}catch(Exception ex){
            		if(ex.getMessage().indexOf("connection abort")>=0){
    					log_DataBaseBean2.error(debugMessage+ex.getMessage());
    					mConn = null;
            		}
            		
            		/** rs = new ResultSet(); */
            	}finally{
            		try{
            			if (mConn != null){mConn.close();mConn = null;}
            			
            		}catch(Exception e){
            			e.printStackTrace();
            			log_DataBaseBean2.error(e.getMessage());
            			
            			/** rs = new ResultSet(); */
            		}
            	}
        	}        	

    	}catch(Exception e){
    		log_DataBaseBean2.error("DataBaseBean�G"+e.getMessage());
    		mMessage = e.getMessage(); 
    	}finally{
    		try{
    			if (mConn != null){mConn.close();mConn = null;}
    			
    		}catch(Exception e){
    			e.printStackTrace();
    			log_DataBaseBean2.error(e.getMessage());
    		}
    	}
    	return rs;
    }
}