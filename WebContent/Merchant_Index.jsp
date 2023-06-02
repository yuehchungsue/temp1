<%@page contentType="text/html;charset=BIG5"%>
<%
  response.addHeader("Pragma", "No-cache");
  response.addHeader("Cache-Control", "no-cache");
  response.setDateHeader("Expires", 0);
  System.out.println("--------------------------Start Merchant_Index.jsp-----"+session.getId());
  if (session.getAttribute("SYSCONFDATA") == null) {  //  系統檔
     session.setAttribute("Message", "抱歉您無使用權限");
     %><jsp:forward page="Merchant_Bye.jsp" /><%
  }
  String MsgBoardFlag = "N";
  if (session.getAttribute("MsgBoardFlag") !=null) {
      MsgBoardFlag = (String)session.getAttribute("MsgBoardFlag");
  }

%>
<html>

<head>
<title>台北富邦銀行 - 電子商務交易付款閘道平台系統</title>
<meta http-equiv="Content-Type" content="text/html" >
<script language='javascript'>

function loadWindow(){
  if (window.event.clientX < 0 && window.event.clientY < 0) {
    if (confirm("您確定要離開嗎？")) {
      window.open("./MerchantLogoutCtl?LogoutFlag=End","main","");
    } else {
      window.open("./Merchant_Index.jsp","","");
    }
  }
}

</script>
</head>

<!--<frameset framespacing="0" border="0" frameborder="0" rows="140,*" onunload='loadWindow()'> -->
<frameset framespacing="0" border="0" frameborder="0" rows="150,*" onunload='loadWindow()' >
 <frame name="banner" scrolling="no" noresize src="Merchant_Banner.jsp">

 <frameset name="BodyFrameset" cols="180,*" >
  <frame name="menu" scrolling="yes" noresize src="./MerchantMenuCtl">
  <frame name="main" scrolling="yes" noresize src=" <%if (MsgBoardFlag.equalsIgnoreCase("Y")) { out.write("./MerchantMsgBoardCtl"); } else { out.write("Merchant_Main.jsp"); } %>" >
 </frameset>

 <noframes>
  <body>
  <span onclick="Exit()">關閉視窗</span>
   <p>此網頁使用框架，但是您的瀏覽器並不支援。</p>
  </body>
 </noframes>

</frameset>

</html>
