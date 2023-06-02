<%@ page contentType="text/html; charset=Big5" %>
<%@ page import="java.util.*"; %>
<%@ page import="com.cybersoft.bean.*" %>
<%@ page import="java.sql.*"; %>
<%System.out.println("welcome.jsp~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
DataBaseBean2 sysBean = new DataBaseBean2();
try{
	StringBuffer sSQLSB = new StringBuffer();
    sysBean.ClearSQLParam();
	sSQLSB.append("select * from tscc_owner.TCSP_FTPHOSTINFO_TB");
	
//	ResultSet rs = stmt.executeQuery("select * from tscc_owner.TCSP_FTPHOSTINFO_TB");
	/** 2023/05/24 §ï¥Î DataBaseBean2.java ªº QuerySQLByParam() (By : YC) *//** Test Case : IT-TESTCASE-089 */
	System.out.println((ArrayList) sysBean.QuerySQLByParam(sSQLSB.toString()));
}catch(Exception ee){
ee.printStackTrace();
}
%>
<html>
<head>
<title>
welcome
</title>
</head>
<body bgcolor="#ffffff">
<h1>
JBuilder Generated JSP
</h1>
<form method="post" action="welcome.jsp">
<br><br>
<input type="submit" name="Submit" value="Submit">
<input type="reset" value="Reset">
</form>
</body>
</html>
