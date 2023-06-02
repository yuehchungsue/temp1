<%@page contentType="text/html;charset=BIG5"%>
<%@page import="java.util.*" %>
<%@page import="java.text.*"%>
<%@page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%
  try {
//    response.addHeader("Pragma", "No-cache");
//    response.addHeader("Cache-Control", "no-cache");
//    response.addDateHeader("Expires", 1);

    NumberFormat nf = NumberFormat.getInstance();
    nf.setMaximumFractionDigits(2);
    nf.setMinimumFractionDigits(2);
    String Message = "無交易資料";
    Hashtable hashVoidData = new Hashtable();
    ArrayList arrayList = new ArrayList();
    String VoidFlag = "false";
    String CancelAmt = "0";
    Hashtable HashBalance = new Hashtable();
    if (session.getAttribute("VoidData") != null) {
      hashVoidData = (Hashtable)session.getAttribute("VoidData");
      if (hashVoidData==null) hashVoidData = new Hashtable();
      if (hashVoidData.size()>0) {
        arrayList = (ArrayList)hashVoidData.get("DATALIST");
        if (arrayList==null) arrayList = new ArrayList();
        VoidFlag = hashVoidData.get("FLAG").toString();
        if (VoidFlag.equalsIgnoreCase("true")) {  // 可交易
          Message = "";
        } else {
          Message = hashVoidData.get("MESSAGE").toString();
        }
      }
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
    document.form.submit();
  }

//-->
</script>

</head>

<body>
<form id="form" name="form" method="post" action ="./MerchantVoidCtl" >
<input type="hidden" name="Action" id="Action" value="Void">
 <%
 String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);
 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
      <td align="left" valign="middle"><b><font color="#004E87" size="3">線上購貨取消作業</font></b></td>
  </tr>
</table>

<hr style="height:1px">

  <%if (arrayList.size()>0) {
  System.out.println("arrayList.size()="+arrayList.size());
  %>

  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr>
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="30" align="left"><!--<%
            System.out.println("VoidFlag="+VoidFlag);
            if (VoidFlag.equalsIgnoreCase("true") ){
              %><input type='button' value='確定取消' name='btnSubmit' id='btnSubmit' onclick="toSubmit();"><%
            }%>-->
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>

  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
    <tr align='center' bgcolor="#F4F4FF" height="25">
      <td align='center' rowspan="2">序號</td>
      <td align='center' rowspan="2">端末機代碼</td>
      <td align='center' colspan="1">特店指定單號</td>
      <td align='center' colspan="1">交易類別</td>
      <td align='center' rowspan="2">交易日期</td>
      <td align='center' colspan="1">回應碼</td>
      <td align='center' rowspan="2">批次號碼</td>
      <td align='center' rowspan="2">交易幣別</td>
      <td align='center' rowspan="2">交易金額</td>
      <td align='center' rowspan="2">可請款金額</td>
    </tr>

    <tr align='center' bgcolor='#F4F4FF' height="25">
      <td align='center'>系統指定單號</td>
      <td align='center'>交易模式</td>
      <td align='center'>授權碼</td>
    </tr>
    <%
    int count=0;
    for (int c=0; c<arrayList.size(); ++c) {
      count++;
      Hashtable hashData = (Hashtable)arrayList.get(c);
      String TransCode = hashData.get("TRANSCODE").toString();
      String FontColor = "";
      String BalanceAmt = hashData.get("BALANCEAMT").toString();
      if (TransCode.equalsIgnoreCase("00")) {  // 購貨
         if (BalanceAmt.length()>0) {
           CancelAmt = String.valueOf(Double.parseDouble(CancelAmt)+Double.parseDouble(BalanceAmt));
         }
      }
      if (TransCode.equalsIgnoreCase("01")) {  // 退貨
        FontColor = "#FF0000";
      }
      if (TransCode.equalsIgnoreCase("10") || TransCode.equalsIgnoreCase("11")) {
        FontColor = "#0000FF";
      }
      if (TransCode.equalsIgnoreCase("20") || TransCode.equalsIgnoreCase("21")) {
        FontColor = "#CCCC00";
      }

%>
<tr align='center' bgcolor='#ffffff'  height="25">
  <td align='left'   rowspan="2"><font color="<%=FontColor%>"><%=count%></font></td>
  <td align='left'   rowspan="2"><font color="<%=FontColor%>"><%=hashData.get("TERMINALID").toString()%></font></td>
  <td align='left'   colspan="1"><font color="<%=FontColor%>"><%=hashData.get("ORDERID").toString()%></font></td>
  <td align='left'   colspan="1"><font color="<%=FontColor%>"><%
  String TransDesc = "";
  if (TransCode.equalsIgnoreCase("00")) TransDesc = "購貨";//交易
  if (TransCode.equalsIgnoreCase("01")) TransDesc = "退貨";//交易
  if (TransCode.equalsIgnoreCase("10")) TransDesc = "購貨取消";//交易
  if (TransCode.equalsIgnoreCase("11")) TransDesc = "退貨取消";//交易
  if (TransCode.equalsIgnoreCase("20")) TransDesc = "購貨請款";//交易
  if (TransCode.equalsIgnoreCase("21")) TransDesc = "購貨請款取消";//交易

  if (TransDesc.length()>0) TransCode = TransCode + "-" + TransDesc;
  out.write(TransCode);
  %></font></td>
  <td align='center' rowspan="2"><font color="<%=FontColor%>"><%
  String TransDate = hashData.get("TRANSDATE").toString();
  String TransTime = hashData.get("TRANSTIME").toString();
  if (TransDate.length()==8) {
    TransDate = TransDate.substring(0,4)+"/"+TransDate.substring(4,6)+"/"+TransDate.substring(6,8);
  }
  if (TransTime.length()==6) {
    TransTime = TransTime.substring(0,2)+":"+TransTime.substring(2,4)+":"+TransTime.substring(4,6);
  }
  out.write(TransDate+"<br>"+TransTime);
  %></font></td>
  <td align='center' colspan="1"><font color="<%=FontColor%>"><%=hashData.get("RESPONSECODE").toString()%></font></td>
  <td align='center' rowspan="2"><font color="<%=FontColor%>"><%=hashData.get("BATCHNO").toString()%></font></td>
  <td align='center' rowspan="2"><font color="<%=FontColor%>">新台幣</font></td>
  <td align='right'  rowspan="2"><font color="<%=FontColor%>"><%
  String TransAmt = hashData.get("TRANSAMT").toString();
  if (TransAmt.length()>0) {
    out.write(nf.format(Double.parseDouble(TransAmt)));
  }%></font></td>
  <td align='right'  rowspan="2"><font color="<%=FontColor%>"><%
  if (BalanceAmt.length()>0) {
    if (Double.parseDouble(BalanceAmt) < 0) {
      BalanceAmt = "0";
    }
    out.write(nf.format(Double.parseDouble(BalanceAmt)));
  }%></font></td>
  </tr>

  <tr align='center' bgcolor='#ffffff' height="25">
    <td align='left'  ><font color="<%=FontColor%>"><%=hashData.get("SYS_ORDERID").toString()%></font></td>
    <td align='left'  ><font color="<%=FontColor%>"><%
    String TransMode = hashData.get("TRANSMODE").toString();
    String TransModeDesc = "";
    if (TransMode.equalsIgnoreCase("0")) TransModeDesc = "一般";//交易
    if (TransMode.equalsIgnoreCase("1")) TransModeDesc = "分期";//交易
    if (TransMode.equalsIgnoreCase("2")) TransModeDesc = "紅利";//交易
    if (TransModeDesc.length()>0) TransMode = TransMode + "-" + TransModeDesc;
    out.write(TransMode);
    %></font></td>
    <td align='center'><font color="<%=FontColor%>"><%=hashData.get("APPROVECODE").toString()%></font></td>
  </tr>
  <%} %>
  </table>

  <br>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
    <tr bgcolor='#ffffff' height="25">
      <td align='left' width='15%'>小計</td>
      <td align='left' width='85%'></td>
    </tr>

    <tr bgcolor='#ffffff' height="25">
      <td align='left' width='15%'>可取消金額</td>
      <td align='left' width='85%'><%
      if (CancelAmt.length()>0) {

        if (VoidFlag.equalsIgnoreCase("false") || Double.parseDouble(CancelAmt) < 0) {
          CancelAmt = "0";
        }
        out.write(nf.format(Double.parseDouble(CancelAmt)));
      }%></td>
      </tr>
  </table>
  <%} %>
  <%if (Message.length()>0) { %>
  <table width="100%">
    <tr><td><b><font color="#FF0000" size="3">注意：<%=Message%></font></b></td></tr>
  </table>
  <%}%>
  <% if (VoidFlag.equalsIgnoreCase("true") ){ %>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
     <tr>
       <td>
         <table width="100%" border="0" cellspacing="0" cellpadding="0">
           <tr>
            <td height="30" align="left"><input type='button' value='確定取消' name='btnSubmit' id='btnSubmit' onclick="toSubmit();"></td>
           </tr>
         </table>
       </td>
     </tr>
  </table>
<%}%>
</body>

</html>
<%
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errMsg",e.toString());
            request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
        }
%>
