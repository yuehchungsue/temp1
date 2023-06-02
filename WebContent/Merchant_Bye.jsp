<%@page contentType="text/html;charset=BIG5"%>
<%
//  response.addHeader("Pragma", "No-cache");
//  response.addHeader("Cache-Control", "no-cache");
//  response.addDateHeader("Expires", 1);
  try {
  String loginmsg = "";
  if (session.getAttribute("Message") != null) {
    loginmsg = (String) session.getAttribute("Message");
    session.removeAttribute("Message");
  }
  String logoutfororward = "";
  if (session.getAttribute("Forward") != null) {
    logoutfororward = (String) session.getAttribute("Forward");
//yrmw    session.removeAttribute("Forward");
  }
  if (loginmsg.length()==0) {
    loginmsg = "超過限制時間";
  }
  if (logoutfororward.length()==0) logoutfororward = "Merchant_Login.jsp";
  System.out.println("loginmsg="+loginmsg);

  String LogoutFlag = "";
  if (session.getAttribute("LogoutFlag")!=null) {
     LogoutFlag = (String)session.getAttribute("LogoutFlag");
     session.removeAttribute("LogoutFlag");
  }

System.out.println("LogoutFlag="+LogoutFlag);

%>
<html>
<head>
 <title><%=loginmsg%></title>
 <meta http-equiv="Content-Type" content="text/html" charset=big5">
 <link href="css/style.css" type="text/css" rel="stylesheet">
 <script language='javascript'>
 <!--
 <%if (logoutfororward.length()>0 && !LogoutFlag.equalsIgnoreCase("Close")) { %>
 function close_window(){
   document.form.action = "<%=logoutfororward%>";
   document.form.submit();
 }
<%} %>
 function loadWindow(){
   <%if (LogoutFlag.equalsIgnoreCase("End")) {%>
      window.close();
   <%} else {
     if (!LogoutFlag.equalsIgnoreCase("Close")) {
     %>
   setTimeout(close_window,1000);
   <% }
 }%>
 }

  // -->
</script>
</head>

<%if (logoutfororward.length()>0 || LogoutFlag.equalsIgnoreCase("End")) { %>
<body onload='loadWindow()'>
<%} else { %>
<body>
<%} %>
<form id="form" name="form" method='post' target="_top" action="<%=logoutfororward%>" >
<input type="hidden" name="LogoutFlag" id="LogoutFlag" value="Login">
 <table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
   <td align="center">

    <table width="790" border="0" cellspacing="0" cellpadding="0">
     <tr>
      <td>&nbsp;</td>
     </tr>
     <tr>
      <td bgcolor="#ffffff" height="250" align="center">

       <p>

       <table border="0" width="630" cellspacing="0" cellpadding="0">
        <tr><td>&nbsp;</td></tr>
       </table>

       <table border="0" width="630" bordercolor="#7B9CD9" cellspacing="0" cellpadding="0">
        <tr>
         <td width="23"><img src="images/login/bg1-1.gif"></td>
         <td background="images/login/bg1-2.gif"></td>
         <td width="24"><img src="images/login/bg1-3.gif"></td>
        </tr>
        <tr>
         <td width="23" background="images/login/bg2-1.gif"></td>
         <td>

          <table width="100%" border="0" cellspacing="0" cellpadding="0">
           <tr>
            <td align="right" height="30" width="10%"></td>
            <td align="left" height="30"></td>
           </tr>
           <tr>
            <td align="right" height="30" width="10%"></td>
            <td align="left" height="30"><font size="2"><b><%=loginmsg%></b></font></td>
           </tr>
           <tr>
            <td align="right" height="30" width="10%"></td>
            <td align="left" height="30"></td>
           </tr>
          </table>

         </td>
         <td width="24" background="images/login/bg2-2.gif"></td>
        </tr>
        <tr>
         <td width="23"><img src="images/login/bg3-1.gif"></td>
         <td background="images/login/bg3-2.gif"></td>
         <td width="24"><img src="images/login/bg3-3.gif"></td>
        </tr>
       </table>

       <br>

       <table width="630" border="0" cellspacing="0" cellpadding="0">
        <tr>
         <td align="right">
          &nbsp;</td>
        </tr>
       </table>

      </td>
     </tr>


    </table>


   </td>
  </tr>
 </table>

</form>

</body>

</html>
<%
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errMsg",e.toString());
            request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
        }
%>
