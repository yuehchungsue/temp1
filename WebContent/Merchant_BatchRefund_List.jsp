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

    String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);

    Hashtable hashRefundData = new Hashtable();
    ArrayList arrayList = new ArrayList();
    int SumFailCont = 0;
    int SumSuccessCont = 0;
    String SumSuccessAmt = "0";
    String SumFailAmt = "0";
    boolean inputFlag =  false; // 是否可輸入退貨金額
    if (session.getAttribute("RefundData") != null) {
      hashRefundData = (Hashtable)session.getAttribute("RefundData");
      if (hashRefundData==null) hashRefundData = new Hashtable();
      if (hashRefundData.size()>0) {
        arrayList = (ArrayList)hashRefundData.get("DATALIST");
        if (arrayList==null) arrayList = new ArrayList();
      }
    }
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html" charset=big5">
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
<form id="form" name="form" method="post" action ="./MerchantBatchRefundCtl?Action=Refund&<%=MerchantMenuCtl.MENU_FORWARD_KEY%>=<%=MenuKey%>" >
<input type="hidden" name="Action" id="Action" value="Refund">
 <%
 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
      <td align="left" valign="middle"><b><font color="#004E87" size="3">檔案退貨作業</font></b></td>
  </tr>
</table>
<hr style="height:1px">
 <%if (arrayList.size()>0) {%>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr>
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="30" align="left"><!--<%
            if (arrayList.size()>0 ){
              %><input type='button' value='確定退貨' name='btnSubmit' id='btnSubmit' onclick="toSubmit();"><%
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
      <td align='center' colspan="1">特約商店代碼</td>
      <td align='center' colspan="1">特店指定單號</td>
      <td align='center' colspan="1">交易類別</td>
      <td align='center' rowspan="2">交易日期</td>
      <td align='center' colspan="1">回應碼</td>
      <td align='center' rowspan="2">批次號碼</td>
      <td align='center' rowspan="2">交易幣別</td>
      <td align='center' rowspan="2">退貨金額</td>
      <td align='center' rowspan="2">檢核結果</td>
    </tr>

    <tr align='center' bgcolor='#F4F4FF' height="25">
      <td align='center'>端末機代碼</td>
      <td align='center'>系統指定單號</td>
      <td align='center'>交易模式</td>
      <td align='center'>授權碼</td>
    </tr>
    <%
    int count=0;
    for (int c=0; c<arrayList.size(); ++c) {
      count++;
      Hashtable hashData = (Hashtable)arrayList.get(c);
      String BatchTxMsg = hashData.get("BATCHTXMSG").toString();
      String FontColor = "";
      String BatchTransAmt = hashData.get("BATCHTXAMT").toString();
      if (BatchTxMsg.equalsIgnoreCase("FAIL")) {  // 失敗
        FontColor = "#FF0000";
        SumFailCont++;
        SumFailAmt = String.valueOf(Double.parseDouble(SumFailAmt)+Double.parseDouble(BatchTransAmt));
      } else {
        SumSuccessCont++;
        SumSuccessAmt = String.valueOf(Double.parseDouble(SumSuccessAmt)+Double.parseDouble(BatchTransAmt));
      }
%>
<tr align='center' bgcolor='#ffffff'  height="25">
  <td align='left'   rowspan="2"><font color="<%=FontColor%>"><%=count%></font></td>
  <td align='left'   colspan="1"><font color="<%=FontColor%>"><%=hashData.get("MERCHANTID").toString()%></font></td>
  <td align='left'   colspan="1"><font color="<%=FontColor%>"><%=hashData.get("BATCHSYSORDERID").toString()%></font></td>
  <td align='left'   colspan="1"><font color="<%=FontColor%>"><%
  String TransDesc = "";
  String TransCode =  hashData.get("BATCHTRANSCODE").toString();
  if (TransCode.equalsIgnoreCase("00")) TransDesc = "購貨";  //交易
  if (TransCode.equalsIgnoreCase("01")) TransDesc = "退貨";//交易
  if (TransDesc.length()>0) TransCode = TransCode + "-" + TransDesc;
  out.write(TransCode);
  %></font></td>
  <td align='center' rowspan="2"><font color="<%=FontColor%>"><%
  String TransDate = hashData.get("BATCHTXDATE").toString();
  String TransTime = hashData.get("BATCHTXTIME").toString();
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
  if (BatchTransAmt.length()>0) {
    out.write(nf.format(Double.parseDouble(BatchTransAmt)));
  }%></font></td>
    <td align='left' rowspan="2"  ><font color="<%=FontColor%>"><%=hashData.get("BATCHRESPONSE").toString()%></font></td>
  </tr>
  <tr align='center' bgcolor='#ffffff' height="25">
    <td align='left'  ><font color="<%=FontColor%>"><%=hashData.get("BATCHTERMINALID").toString()%></font></td>
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
    <td align='center'><font color="<%=FontColor%>"><%=hashData.get("BATCHTXAPPROVECODE").toString()%></font></td>
  </tr>
  <%} %>
  </table>

  <br>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
    <tr bgcolor='#ffffff' height="25">
      <td align='left' width='10%'></td>
      <td align='center' width='10%'>筆數</td>
      <td align='center' width='15%'>金額</td>
      <td align='left' width='65%'></td>
    </tr>

    <tr bgcolor='#ffffff' height="25">
      <td align='left' width='10%'><font color="#FF0000">檢核失敗</font></td>
      <td align='right' width='10%'><font color="#FF0000"><%=SumFailCont%>筆</font></td>
      <td align='right' width='15%'><font color="#FF0000"><%=nf.format(Double.parseDouble(SumFailAmt))%></font></td>
      <td align='left' width='65%'></td>
    </tr>

    <tr bgcolor='#ffffff' height="25">
      <td align='left' width='10%'>檢核成功</td>
      <td align='right' width='10%'><%=SumSuccessCont%>筆</td>
      <td align='right' width='15%'><%=nf.format(Double.parseDouble(SumSuccessAmt))%></td>
      <td align='left' width='65%'></td>
    </tr>

    <tr bgcolor='#ffffff' height="25">
      <td align='left' width='10%'>總計</td>
      <td align='right' width='10%'><%=SumFailCont+SumSuccessCont%>筆</td>
      <td align='right' width='15%'><%=nf.format(Double.parseDouble(SumSuccessAmt)+Double.parseDouble(SumFailAmt))%></td>
      <td align='left' width='65%'></td>
    </tr>

  </table>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr>
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="30" align="left"><%
            if (arrayList.size()>0 ){
              %><input type='button' value='確定退貨' name='btnSubmit' id='btnSubmit' onclick="toSubmit();"><%
            }%>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>

  <%} %>
</body>
</html>
<%
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errMsg",e.toString());
            request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
        }
%>
