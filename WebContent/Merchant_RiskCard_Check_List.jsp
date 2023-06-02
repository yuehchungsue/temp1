<%@page contentType="text/html; charset=Big5"%>
<%@page language="java" %>
<%@page import="java.util.*"%>
<%@page import="java.text.*"%>
<%@page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%@page import="com.cybersoft.bean.UserBean" %>
<%
  response.addHeader("Pragma", "No-cache");
  response.addHeader("Cache-Control", "no-cache");
  response.setDateHeader("Expires", 1);
  try {
    ArrayList arrayMsgcode = new ArrayList();
    if (session.getAttribute("RiskCardMsgCode") != null) {
      arrayMsgcode = (ArrayList)session.getAttribute("RiskCardMsgCode");
    }
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html" >
  <link href="css/style.css" type="text/css" rel="stylesheet">
  <script type="text/javascript" language="javascript" src="./js/Vatrix.js"></script>
  <script type="text/javascript" language="JavaScript">
  <!--
  function toSubmit() {
     document.form.btnSubmit.disabled = true;
     document.form.btnBack.disabled = true;
     document.form.submit();
  }

  function toBack(){
    document.form.Action.value = '';
    document.form.submit();
  }
//-->
</script>
</head>
<body>
<form id="form" name="form" method="post" action ="./MerchantRiskCardCtl" >
<input type="hidden" name="Action" id="Action" value="Risk">
<%
String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);
out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
      <td align="left" valign="middle"><font size="3" color="#004E87"><b>特店風險卡維護</b></font></td>
  </tr>
</table>

<hr style="height:1px">
  <%if (session.getAttribute("RiskCardPreCheckData") != null) {
      Hashtable hashRiskCardData = (Hashtable)session.getAttribute("RiskCardPreCheckData");
      String []InputRisk_Degree = hashRiskCardData.get("InputRisk_Degree").toString().split(",");
      String []InputOrderID = hashRiskCardData.get("InputOrderID").toString().toString().split(",");;
      String []InputPan = hashRiskCardData.get("InputPan").toString().toString().split(",");;
      String []InputMerInsUser = hashRiskCardData.get("InputMerInsUser").toString().toString().split(",");;
      String []InputMerUpdUser = hashRiskCardData.get("InputMerUpdUser").toString().toString().split(",");;
      String []InputMerInsDate = hashRiskCardData.get("InputMerInsDate").toString().toString().split(",");;
      String []InputMerUpdDate = hashRiskCardData.get("InputMerUpdDate").toString().toString().split(",");;
 %>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr>
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="30" align="left"></td>
            <td align="right">總筆數：<%=InputRisk_Degree.length%>筆</td>
          </tr>
        </table>
     </td>
    </tr>
  </table>

  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
    <tr align='center' bgcolor="#F4F4FF" height="25">
      <td align='center' rowspan="2">序號</td>
      <td align='center' rowspan="2">特店指定單號</td>
      <td align='center' rowspan="2">信用卡卡號</td>
      <td align='center' rowspan="2">風險等級</td>
      <td align='center' colspan="1">特店新增人員</td>
      <td align='center' colspan="1">特店維護人員</td>
    </tr>
    <tr align='center' bgcolor='#F4F4FF' height="25">
      <td align='center'>特店新增時間</td>
      <td align='center'>特店維護時間</td>
    </tr>
    <%
    UserBean userbean = new UserBean();
    int count=0;
    for (int c=0; c<InputRisk_Degree.length; ++c) {
      count++;
  %><tr align='center' bgcolor='#ffffff'  height="25">
       <td align='left' rowspan="2"><font color=""><%=count%></font></td>
       <td align='left' rowspan="2"><font color=""><%=InputOrderID[c]%></font></td>
       <td align='left' rowspan="2"><font color=""><%=userbean.get_CardStar(InputPan[c].trim(),9,2)%></font></td>
       <td align='left' rowspan="2"><font color=""><%
       for (int cc=0; cc<arrayMsgcode.size();++cc) {
         Hashtable hashMsgData = (Hashtable)arrayMsgcode.get(cc);
         String MsgCode = (String)hashMsgData.get("MSG_CODE");
         String MsgDesc = (String)hashMsgData.get("MSG_DESC");
         if (MsgCode.equalsIgnoreCase(InputRisk_Degree[c])) {
            out.write(MsgCode+"-"+MsgDesc);
            break;
         }
       } %></font></td>
       <td align='center' colspan="1"><font color=""><%=InputMerInsUser[c]%></font></td>
       <td align='center' colspan="1"><font color=""><%=InputMerUpdUser[c]%></font></td>
    </tr>
    <tr align='center' bgcolor='#ffffff' height="25">
      <td align='center'><font color=""><%=InputMerInsDate[c]%></font></td>
      <td align='center'><font color=""><%=InputMerUpdDate[c]%></font></td>
    </tr>
   <%} %>
  </table>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr>
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="30" align="left"><input type='button' value='確定送出' name='btnSubmit' id='btnSubmit' onclick="toSubmit();">　<input type='button' value='回查詢頁' name='btnBack' id='btnBack' onclick="toBack();"></td>
            <td align="right"></td>
          </tr>

        </table>
     </td>
    </tr>
  </table>


  <%} else {%>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="30" align="left"><input type='button' value='回查詢畫頁' name='btnSubmit' id='btnSubmit' onclick="toBack();"></td>
          </tr>
          <tr>
            <td height="30" align="left" bgcolor='#ffffff'><font color="#FF0000" size="3"><b>查無特約商店代碼資料<b></font></td>
          </tr>

        </table>
    </tr>
  </table>
  <%} %>
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
