<%@page contentType="text/html;charset=BIG5"%>
<%
  response.addHeader("Pragma", "No-cache");
  response.addHeader("Cache-Control", "no-cache");
  response.setDateHeader("Expires", 0);
%>
<html>
<head>
 <meta http-equiv="Content-Type" content="text/html" charset=big5">
 <link href="css/style.css" type="text/css" rel="stylesheet">
</head>

<!--
<body topmargin="0" leftmargin="0" bgcolor="#FFF7D0">
-->

<body topmargin="0" leftmargin="0" background="images/menu_bg.jpg">

 <table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
   <td height="24" bgcolor="#F4F4FF">　　　　　<font color="#000000">系統選單</font></td>
  </tr>
  <tr>
   <td height="5"></td>
  </tr>
 </table>

 <script language="javascript" src="js/tree_core.js" type=""></script>

 <script language="JavaScript" type="">
// TreeFile = "js/tree_script.txt";
 IconPath = "images/tree/";
 scriptText = "Payment Gateway,menuRoot,Merchant_Main.jsp,main\n";//系統選單-開頭
<%
String strScriptTextAction =(String) session.getAttribute(com.cybersoft.merchant.ctl.MerchantMenuCtl.MENU_SCRIPT_TEXT);
if(strScriptTextAction != null && strScriptTextAction.length() > 0){
  System.out.println(strScriptTextAction);
%>
  scriptText += '<%=strScriptTextAction%>';
<%}%>

if(scriptText.length > 0){
  onDownloadDone(scriptText);
}
 </script>

</body>

</html>

