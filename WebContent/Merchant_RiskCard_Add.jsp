<%@page contentType="text/html; charset=Big5"%>
<%@ page language="java" %>
<%@ page import="java.util.*"%>
<%@ page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%@ page import="com.cybersoft.bean.UserBean" %>
<%
  response.addHeader("Pragma", "No-cache");
  response.addHeader("Cache-Control", "no-cache");
  response.setDateHeader("Expires", 1);
try {
  Hashtable userinfo=(Hashtable)session.getAttribute("SYSCONFDATA");
  Hashtable merchantinfo=(Hashtable)userinfo.get("MERCHANT_USER");
  String merchantid=String.valueOf(merchantinfo.get("MERCHANT_ID"));
  ArrayList arrayRiskCard = (ArrayList)session.getAttribute("RiskCardMsgCode");
  Hashtable hashRiskCard = new Hashtable();
  if (session.getAttribute("AddRiskData") != null) {
     hashRiskCard = (Hashtable)session.getAttribute("AddRiskData");
  }
  UserBean userbean = new UserBean();
  String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);

%>
<html>
<head>
 <meta http-equiv="Content-Type" content="text/html charset=big5">
<link href="css/style.css" type="text/css" rel="stylesheet">
</head>
<script language="JavaScript" src="js/Vatrix.js"></script>
<script language="JavaScript" src="js/calendar.js" ></script>
<script language="JavaScript" type="text/JavaScript">
<!--

  function toAdd() {
     document.form.submit();
  }
  function toBack(){
    document.form.Action.value = '';
    document.form.submit();
  }

//-->
</script>
<body bgcolor="#ffffff" >
<form method="post" name="form" id="form" action="./MerchantRiskCardCtl" >
<input type="hidden" name="Action" id="Action" value="toAdd">
<%
 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
   <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg" ></td>
   <td align="left" valign="middle"><b><font color="#004E87" size="3">特店風險卡新增</font></b></td>
  </tr>
 </table>

 <hr style="height:1px">



 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
 <%if (hashRiskCard.size()>0) { %>
 <tr align='center' height="25">
   <td width='25%' bgcolor='#F4F4FF' align='right'>特約商店代號</td>
   <td width='75%' bgcolor='#ffffff' align='left' ><%=merchantid%></td>
 </tr>
 <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>特店指定單號</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><%=hashRiskCard.get("ORDERID").toString()%></td>
 </tr>
 <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>卡號</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><%=userbean.get_CardStar(hashRiskCard.get("PAN").toString().trim(),9,2)%></td>
 </tr>
 <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>風險等級</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><%
     String Risk_Degree = hashRiskCard.get("Risk_Degree").toString();
     for (int i=0; i<arrayRiskCard.size();++i) {
       Hashtable hashData = (Hashtable)arrayRiskCard.get(i);
       String MsgCode = (String)hashData.get("MSG_CODE");
       String MsgDesc = (String)hashData.get("MSG_DESC");
       if (Risk_Degree.equalsIgnoreCase(MsgCode)) {
         out.write(MsgCode+" - "+ MsgDesc);
         break;
       }
     } %></td>
 </tr>
 <%} %>
  <tr align='center' height="30">
   <td align='center' bgcolor='#F4F4FF' colspan="2">
    <input type='button' value='確定' name='btnSubmit' id='btnSubmit' onclick="toAdd();" >
    <input type='button' value='取消' name='btnCancel' id='btnCancel' onclick="toBack();">
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
