/**異動說明
 * 2020.03.06 HKP Add DB連線參數化免除SQL INJECTION
 * 202208090854-01 20220801 HKP PCI-DSS更換密碼雜錯MD5為SHA256，MD5雜錯長度為32，SHA256雜錯長度為64，以此長度判斷是否為MD5，變更後的密碼一律使用SHA256與LOG遮蔽
 * */
package com.cybersoft.bean;
public enum emDB {
	EC,
	CPS,
	MAPS,
	ERM
}
