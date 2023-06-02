/**異動說明
 * 2020.03.06 HKP Add DB連線參數化免除SQL INJECTION
 * */
package com.cybersoft.bean;
import java.util.ArrayList;
/*
 * 進行整批作業SQL暫存物件
 * */
public class DBTransationSQLList {
	private ArrayList<String> mSQLList = new ArrayList<String>();
	private ArrayList<ArrayList<SQLParam>> mSQLParamList = new ArrayList<ArrayList<SQLParam>>();
	public void AddTrnSQLAndParam(String sSQL,ArrayList<SQLParam> SqlParam){
		mSQLList.add(sSQL);
		mSQLParamList.add(SqlParam);
	}
	public ArrayList<String> getSQLList(){
		return mSQLList;
	}
	public ArrayList<SQLParam> getSQLParam(int index){
		if(index <= mSQLParamList.size()){
			return mSQLParamList.get(index);
		}else{
			return new ArrayList<SQLParam>();
		}
	}
}
