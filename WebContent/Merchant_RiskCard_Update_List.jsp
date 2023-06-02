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
    Hashtable userinfo=(Hashtable)session.getAttribute("SYSCONFDATA");
    Hashtable merchantinfo=(Hashtable)userinfo.get("MERCHANT_USER");
    String merchantid=String.valueOf(merchantinfo.get("MERCHANT_ID"));
    ArrayList arrayMsgcode = new ArrayList();
    if (session.getAttribute("RiskCardMsgCode") != null) {
      arrayMsgcode = (ArrayList)session.getAttribute("RiskCardMsgCode");
    }
    Hashtable sysinfo=(Hashtable)userinfo.get("SYSCONF");
    int PagePcs =  100;
    if (sysinfo.size()>0) {
      String strPagePcs = sysinfo.get("MER_PAGE_CUL").toString();
      if (strPagePcs.length()>0) {
        PagePcs = Integer.parseInt(strPagePcs);
      }
    }
    int nowPage = 0;
    ArrayList arrayList = new ArrayList();
    if (session.getAttribute("RiskCardUpdCheckData") != null) {
      arrayList = (ArrayList)session.getAttribute("RiskCardUpdCheckData");
      if (arrayList==null) arrayList = new ArrayList();
    }
    int TotalPage = arrayList.size() / PagePcs ;
    if ((arrayList.size() % PagePcs) > 0) TotalPage++;
    System.out.println("arrayList.size()="+arrayList.size());

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html" >
  <link href="css/style.css" type="text/css" rel="stylesheet">
  <script type="text/javascript" language="javascript" src="./js/Vatrix.js"></script>
  <script type="text/javascript" language="JavaScript">
  <!--
  function toBack(){
    document.form.Action.value = '';
    document.form.submit();
  }
//-->
</script>
</head>
<body>
<form id="form" name="form" method="post" action ="./MerchantRiskCardCtl" >
<input type="hidden" name="Action" id="Action" value="">
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
  <%if (arrayList.size()>0) { %>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr>
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="30" align="left"></td>
            <td align="right">總筆數：<%=arrayList.size()%>筆</td>
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
    int StartCnt = nowPage * PagePcs;
    for (int c=StartCnt; c<(StartCnt+PagePcs); ++c) {
      if (c >= arrayList.size()) break;
      count++;
      Hashtable hashData = (Hashtable)arrayList.get(c);
  %><tr align='center' bgcolor='#ffffff'  height="25">
       <td align='left' rowspan="2"><font color=""><%=count%></font></td>
       <td align='left' rowspan="2"><font color=""><%=hashData.get("ORDERID").toString().trim()%></font></td>
       <td align='left' rowspan="2"><font color=""><%=userbean.get_CardStar(hashData.get("PAN").toString().trim(),9,2)%></font></td>
       <td align='left' rowspan="2"><font color=""><%
       String Risk_Degree = hashData.get("RISK_DEGREE").toString().trim();
       for (int cc=0; cc<arrayMsgcode.size();++cc) {
         Hashtable hashMsgData = (Hashtable)arrayMsgcode.get(cc);
         String MsgCode = (String)hashMsgData.get("MSG_CODE");
         String MsgDesc = (String)hashMsgData.get("MSG_DESC");
         if (MsgCode.equalsIgnoreCase(Risk_Degree)) {
            out.write(MsgCode+"-"+MsgDesc);
            break;
         }
       }

       %></font></td>
       <td align='center' colspan="1"><font color=""><%=hashData.get("MER_INS_USER").toString()%></font></td>
       <td align='center' colspan="1"><font color=""><%=hashData.get("MER_UPD_USER").toString()%></font></td>
    </tr>
    <tr align='center' bgcolor='#ffffff' height="25">
      <td align='center'><font color=""><%=hashData.get("MER_INS_DATE").toString()%></font></td>
      <td align='center'><font color=""><%=hashData.get("MER_UPD_DATE").toString()%></font></td>
    </tr>
   <%} %>
  </table>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr>
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="30" align="left"><input type='button' value='回查詢頁' name='btnBack' id='btnBack' onclick="toBack();"></td>
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
