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
  if (arrayRiskCard == null) arrayRiskCard = new ArrayList();
  String Message = "";
  if (session.getAttribute("Message") != null) {
      Message = (String)session.getAttribute("Message");
      session.removeAttribute("Message");
  }

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

function submitform() {
  document.form.submit();
}



function toAdd() {
  if (document.all.form.OrderID.value.length == 0) {
    alert("請記得輸入特店指定單號");
    return void(0);
  }
  if (document.all.form.Risk_Degree.value == "ALL") {
    alert("請選擇風險等級");
    return void(0);
  }
  document.all.form.Action.value = "Add";
  document.form.submit();
}

function toClear(){
  document.all.form.OrderID.maxLength = 25;
  document.all.form.OrderID.value="";
  document.all.form.Risk_Degree.value='ALL';
}

  function toPrint(PrintType) {
       strFeatures = "top=" + (screen.height-670)/2 + ",left=150,width=1,height=1,toolbar=0,menubar=0,location=0,directories=0,status=0,scrollbars=yes";
       var url = "<%=request.getContextPath()%>/MerchantRiskCardCtl?Action=Print&<%=MerchantMenuCtl.MENU_FORWARD_KEY%>=<%=MenuKey%>&OrderID="+document.all.form.OrderID.value+"&Risk_Degree="+document.all.form.Risk_Degree.value+"&PrintType="+PrintType;
       subWin=window.open(url,"main",strFeatures);
  }


<%if (Message.length() > 0 ) {%>
  function alertMsg() {
     alert("<%=Message%>");
  }
<%}%>
//-->
</script>
<body bgcolor="#ffffff" <%if (Message.length() > 0 ){%> onload='alertMsg()' <%}%> >
<form method="post" name="form" id="form" action="./MerchantRiskCardCtl"   >
<input type="hidden" name="Action" id="Action" value="FirstQuery">
<%

 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
   <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
   <td align="left" valign="middle"><b><font color="#004E87" size="3">特店風險卡維護</font></b></td>
  </tr>
 </table>

 <hr style="height:1px">

 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
  <tr align='center' height="25">
   <td width='25%' bgcolor='#F4F4FF' align='right'>特約商店代號(*)</td>
   <td width='75%' bgcolor='#ffffff' align='left' ><%=merchantid%></td>
  </tr>
  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>特店指定單號</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><input type='text' id='OrderID' name='OrderID' size='50' maxlength='25' ></td>
  </tr>

  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>風險等級</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><select name='Risk_Degree' id='Risk_Degree'><%
   for (int i=0; i<arrayRiskCard.size();++i) {
     Hashtable hashData = (Hashtable)arrayRiskCard.get(i);
     String MsgCode = (String)hashData.get("MSG_CODE");
     String MsgDesc = (String)hashData.get("MSG_DESC");
     %><option value='<%=MsgCode%>' > <%=MsgCode+"-"+MsgDesc%></option><%
   } %>
     </select><font color="red"><b>(選擇"全部"按【查詢】按鈕,有查詢筆數上限)</b></font>
   </td>
  </tr>
  <tr align='center' height="30">
   <td align='center' bgcolor='#F4F4FF' colspan="2">
    <input type='button' value='新增' name='btnAdd' id='btnAdd' onclick='toAdd()' >
    <input type='button' value='查詢' name='btnQuery' id='btnQuery' onclick='submitform()' >
    <input type='button' value='以TXT格式匯出' name='btnTXT' id='btnTXT' onclick="toPrint('TXT');">
    <input type='button' value='清除畫面' name='btnClear' id='btnClear' onclick='toClear();' >
   </td>
  </tr>
 </table>
<table width="100%" border="0">
 <tr align='center' height="30">
   <td align='left'><font color="red"><b>(說明：下載功能無查詢筆數上限)</b></font></td>
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
