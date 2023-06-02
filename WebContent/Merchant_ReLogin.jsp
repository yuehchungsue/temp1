<%@page contentType="text/html;charset=BIG5"%>
<%@page import="java.text.*"%>
<%@page import="java.util.*" %>
<%
  try {
    String LoginDate = "";
    if (session.getAttribute("SESSIONDATA") != null) {
       Hashtable hashSession = new Hashtable();
       hashSession = (Hashtable) session.getAttribute("SESSIONDATA");
       if (hashSession.size()>0) LoginDate = (String)hashSession.get("LOGIN_DATE");
    }
    System.out.println("Start Merchant_ReLogin.jsp");
%>
<html>
<head>
 <title>重覆簽入訊息</title>
 <meta http-equiv="Content-Type" content="text/html" charset=big5">
 <link href="css/style.css" type="text/css" rel="stylesheet">
 <script language='javascript'>
 <!--
  function toSign(flag) {
  document.form.Flag.value = flag;
  document.form.btnSign.disabled = true;
  document.form.btnCancel.disabled = true;
  document.form.submit();

  }
  // -->
</script>
</head>
<body>
<form id="form" name="form" method='post' target="_top" action="./MerchantReLoginCtl" >
<input type="hidden" name="Flag" id="Flag" value="Cancel">
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
            <td align="left" height="30"><font size="2"><b>您已於<%=LoginDate%>簽入，並且尚未簽出。<br>請選擇要繼續簽入（捨棄上次簽入），或取消本次簽入（保留上次簽入）？</b></font></td>
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
         <td align="right"><input type='button' value='繼續簽入' name='btnSign' id='btnSign' onclick="toSign('Sign');">　<input type='button' value='取消簽入' name='btnCancel' id='btnCancel' onclick="toSign('Cancel');">
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
