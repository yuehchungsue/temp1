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
    loginmsg = loginmsg.replaceAll("使用者無此權限請洽系統管理者","");
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
<font size="3"><b><%=UserName%> (<%=UserID%>) 您好！歡迎使用本系統。</b></font>
<br><br>
<%if (loginmsg.length()>0) {%><font size="3" color="#FF0000"><b><%=loginmsg%>。</b></font><%}%>
</center>
<form method="POST" action="./MerchantMsgBoardCtl" id="form">
</form>
</body>
</html>
