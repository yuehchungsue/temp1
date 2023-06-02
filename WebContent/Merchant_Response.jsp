<%
/**
 * 202208090854-01 20220801 HKP PCI-DSS更換密碼雜錯MD5為SHA256，MD5雜錯長度為32，SHA256雜錯長度為64，以此長度判斷是否為MD5，變更後的密碼一律使用SHA256與LOG遮蔽
 */
%>
<%@page contentType="text/html;charset=BIG5"%>
<%
  response.addHeader("Pragma", "No-cache");
  response.addHeader("Cache-Control", "no-cache");
  response.addDateHeader("Expires", 1);
  try {
  String logMsg = "";
  if (session.getAttribute("Message") != null) {
	  logMsg = (String) session.getAttribute("Message");
    session.removeAttribute("Message");
  }
  System.out.println("logMsg="+logMsg);

%>
<html>
<head>
 <title>處理結果顯示</title>
 <meta http-equiv="Content-Type" content="text/html" charset=big5">
<link href="css/style.css" type="text/css" rel="stylesheet">
</head>

<body>
<form id="form" name="form" method='post' >
 <table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
   <td align="center">

    <table width="790" border="0" cellspacing="0" cellpadding="0">
     <tr>
      <td>　</td>
     </tr>
     <tr>
      <td bgcolor="#ffffff" height="250" align="center">

       <p>

       <table border="0" width="630" cellspacing="0" cellpadding="0">
        <tr><td>　</td></tr>
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
            <td align="left" height="30"><font color="red" size="2"><b><%=logMsg%></b></font></td>
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
          　</td>
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
