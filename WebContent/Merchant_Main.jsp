<%@page contentType="text/html;charset=BIG5"%>
<%@ page import="java.util.*"%>
<%
  response.addHeader("Pragma", "No-cache");
  response.addHeader("Cache-Control", "no-cache");
  response.setDateHeader("Expires", 0);

  Hashtable userinfo=(Hashtable)session.getAttribute("SYSCONFDATA");
  System.out.println(userinfo);
  Hashtable merchantinfo=(Hashtable)userinfo.get("MERCHANT_USER");
  String UserName = (String)merchantinfo.get("USER_NAME");
  String UserID = (String)merchantinfo.get("USER_ID");
  String loginmsg = "";
  if (session.getAttribute("Message") != null) {
    loginmsg = (String) session.getAttribute("Message");
    loginmsg = loginmsg.replaceAll("�ϥΪ̵L���v���Ь��t�κ޲z��","");
    session.removeAttribute("Message");
  }


%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=big5">
 <link href="css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" language="JavaScript">
 function toMsgBoard() {
   form.submit();
 }
 </script>

</head>
<body bgcolor=#ffffff  >

<center>
<br><br><br><br><br>
<font size="3"><b><%=UserName%> (<%=UserID%>) �z�n�I�w��ϥΥ��t�ΡC</b></font>
<br><br>
<%if (loginmsg.length()>0) {%><font size="3" color="#FF0000"><b><%=loginmsg%>�C</b></font><%}%>
</center>
<form method="POST" action="./MerchantMsgBoardCtl" id="form">
</form>
</body>
</html>
