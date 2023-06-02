<%@page contentType="text/html;charset=BIG5"%>
<%@page import="java.util.*" %>
<%@page import="java.text.*"%>
<%@page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%
  try {
//      response.addHeader("Pragma", "No-cache");
//      response.addHeader("Cache-Control", "no-cache");
//      response.addDateHeader("Expires", 1);

      Hashtable userinfo = (Hashtable)session.getAttribute("SYSCONFDATA");
      Hashtable merchantinfo = (Hashtable)userinfo.get("MERCHANT_USER");
      String merchantid = String.valueOf(merchantinfo.get("MERCHANT_ID"));
      String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);

%>
<html>
<head>
 <meta http-equiv="Content-Type" content="text/html" charset=big5">
 <link href="css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" language="javascript" src="./js/ScreenProtectPage.js"></script>
<script type="text/javascript" language="JavaScript">
<!--
  function toSubmit() {
     if (document.form.UploadName.value.length == 0 ) {
        alert("�п�ܶפJ�ɮ�");
        return void(0);
     }
     var Url = "./MerchantBatchVoidCtl?Action=Batch&<%=MerchantMenuCtl.MENU_FORWARD_KEY%>=<%=MenuKey%>";
     document.form.action = Url;
     document.form.submit();
  }
//-->
</script>
</head>

<body>
<form id="form" name="form" method="post" action ="./MerchantBatchVoidCtl" enctype="multipart/form-data">
<input type="hidden" name="Action" id="Action" value="Batch">
 <%
 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>

 <table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
   <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
   <td align="left" valign="middle"><b><font color="#004E87" size="3">�妸����</font></b></td>
  </tr>
 </table>

 <hr style="height:1px">
<br>
<br>
 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
  <tr align='center' height="30">
   <td width='30%' align='right' bgcolor='#F4F4FF'>�S���ө��N��(*)</td>
   <td width='70%' align='left'  bgcolor='#FFFFFF'><%=merchantid %></td>
  </tr>

  <tr align='center' height="30">
   <td width='30%' align='right' bgcolor='#F4F4FF'>��ܶפJ�ɮ�</td>
   <td width='70%' align='left'  bgcolor='#FFFFFF'><input type="file" size="30" name="UploadName" id="UploadName" ></td>
  </tr>


  <tr align='center' height="30">
   <td align='center' bgcolor='#F4F4FF' colspan="2">
    <input type='button' value='�T�w' name='btnQuery' id='btnQuery' onclick='toSubmit();' >
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