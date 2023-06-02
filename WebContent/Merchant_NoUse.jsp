<%@page contentType="text/html;charset=BIG5"%>
<%
  response.addHeader("Pragma", "No-cache");
  response.addHeader("Cache-Control", "no-cache");
  response.addDateHeader("Expires", 1);
%>
<html>
<head>
 <title>無使用權限</title>
 <meta http-equiv="Content-Type" content="text/html" >
 <link href="css/style.css" type="text/css" rel="stylesheet">
 <script language='javascript' type="">
 <!--
 function close_window(){
   document.form.submit();
 }
  // -->
</script>
</head>
<body onload='close_window()'>
<form id="form" name="form" method='post' target="_top" action="./MerchantLogoutCtl" >
<!--<input type="hidden" name="LogoutFlag" id="LogoutFlag" value="<%//LogoutFlag%>">  -->
</form>

</body>

</html>
