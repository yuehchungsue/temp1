<%@page contentType="text/html;charset=BIG5"%>
<%@page import="java.util.*" %>
<%@page import="java.text.*"%>
<%@page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%
  try {

//    response.addHeader("Pragma", "No-cache");
//    response.addHeader("Cache-Control", "no-cache");
//    response.addDateHeader("Expires", 1);

    String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);

    NumberFormat nf = NumberFormat.getInstance();
    nf.setMaximumFractionDigits(2);
    nf.setMinimumFractionDigits(2);

    Hashtable hashCaptureData = new Hashtable();
    ArrayList arraySuccessList = new ArrayList();
    ArrayList arrayFailList = new ArrayList();
    int SumSaleFailCount = 0;
    int SumRefundFailCount = 0;
    int SumSaleSuccessCount = 0;
    int SumRefundSuccessCount = 0;
    String SumSaleSuccessAmt = "0";
    String SumRefundSuccessAmt = "0";
    String SumSaleFailAmt = "0";
    String SumRefundFailAmt = "0";
    String BatchPmtID = "";
    boolean inputFlag =  false; // 是否可輸入退貨金額
    if (session.getAttribute("CaptureUpdateData") != null) {
      hashCaptureData = (Hashtable)session.getAttribute("CaptureUpdateData");
      if (hashCaptureData==null) hashCaptureData = new Hashtable();
      if (hashCaptureData.size()>0) {
        arraySuccessList = (ArrayList)hashCaptureData.get("SUCCESS");
        if (arraySuccessList==null) arraySuccessList = new ArrayList();
        arrayFailList = (ArrayList)hashCaptureData.get("FAIL");
        if (arrayFailList==null) arrayFailList = new ArrayList();
        BatchPmtID = hashCaptureData.get("BATCHPMTID").toString();
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

  function toPrint(PrintType) {

       strFeatures = "top=" + (screen.height-670)/2 + ",left=150,width=1,height=1,toolbar=0,menubar=0,location=0,directories=0,status=0,scrollbars=yes";
       var url = "<%=request.getContextPath()%>/MerchantBatchCaptureCtl?Action=Print&<%=MerchantMenuCtl.MENU_FORWARD_KEY%>=<%=MenuKey%>&BatchPmtID="+document.all.form.BatchPmtID.value+"&PrintType="+PrintType;
       subWin=window.open(url,"main",strFeatures);
  }

  function closeSub(){
    if (subWin != null && subWin.open) subWin.close(); subWin=null;
  }

//-->
</script>

</head>

<body>
<form id="form" name="form" method="post" action ="./MerchantBatchCaptureCtl" >
<input type="hidden" name="Action" id="Action" value="Capture">
<input type="hidden" name="BatchPmtID" id="BatchPmtID" value="<%=BatchPmtID%>">
 <%
 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
      <td align="left" valign="middle"><b><font color="#004E87" size="3">檔案請款作業</font></b></td>
  </tr>
</table>

<hr style="height:1px">

  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr>
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="30" align="left"><input type='button' value='以PDF格式匯出' name='btnPDF' id='btnPDF' onclick="toPrint('PDF');"><input type='button' value='以CSV格式匯出' name='btnCSV' id='btnCSV' onclick="toPrint('CSV');"><input type='button' value='以TXT格式匯出' name='btnTXT' id='btnTXT' onclick="toPrint('TXT');">
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
  <%if (arraySuccessList.size()>0) {%>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
    <tr align='center' bgcolor="#F4F4FF" height="25">
      <td align='center' rowspan="2">序號</td>
      <td align='center' colspan="1">特約商店代碼</td>
      <td align='center' colspan="1">特店指定單號</td>
      <td align='center' colspan="1">交易類別</td>
      <td align='center' rowspan="2">交易日期</td>
      <td align='center' colspan="1">回應碼</td>
      <td align='center' rowspan="2">批次號碼</td>
      <td align='center' colspan="1">交易幣別</td>
      <td align='center' rowspan="2">請款金額</td>
      <td align='center' colspan="1">請款日期</td>
      <td align='center' rowspan="2">請款狀態</td>
    </tr>

    <tr align='center' bgcolor='#F4F4FF' height="25">
      <td align='center'>端末機代碼</td>
      <td align='center'>系統指定單號</td>
      <td align='center'>交易模式</td>
      <td align='center'>授權碼</td>
      <td align='center'>交易金額</td>
      <td align='center'>請款期限</td>
    </tr>
    <%
    int count=0;
    for (int c=0; c<arraySuccessList.size(); ++c) {
      count++;
      Hashtable hashData = (Hashtable)arraySuccessList.get(c);
      String BatchTxMsg = hashData.get("BATCHTXMSG").toString();
      String FontColor = "";
      String BatchTransAmt = hashData.get("BATCHTXAMT").toString();
      String TransCode =  hashData.get("BATCHTRANSCODE").toString();
      if (TransCode.equalsIgnoreCase("00")) {
        SumSaleSuccessCount++;
        SumSaleSuccessAmt = String.valueOf(Double.parseDouble(SumSaleSuccessAmt)+Double.parseDouble(BatchTransAmt));
      }
      if (TransCode.equalsIgnoreCase("01")) {
        SumRefundSuccessCount++;
        SumRefundSuccessAmt = String.valueOf(Double.parseDouble(SumRefundSuccessAmt)+Double.parseDouble(BatchTransAmt));
      }


      %>
      <tr align='center' bgcolor='#ffffff'  height="25">
        <td align='left'   rowspan="2"><font color="<%=FontColor%>"><%=count%></font></td>
        <td align='left'   colspan="1"><font color="<%=FontColor%>"><%=hashData.get("MERCHANTID").toString()%></font></td>
        <td align='left'   colspan="1"><font color="<%=FontColor%>"><%=hashData.get("BATCHSYSORDERID").toString()%></font></td>
        <td align='left'   colspan="1"><font color="<%=FontColor%>"><%
        String TransDesc = "";

        if (TransCode.equalsIgnoreCase("00")) TransDesc = "購貨"; //交易
        if (TransCode.equalsIgnoreCase("01")) TransDesc = "退貨";  //交易
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
          TransTime = TransTime.substring(0,2)+":"+TransTime.substring(2,4)+"/"+TransTime.substring(4,6);
        }
        out.write(TransDate+"<br>"+TransTime);
        %></font></td>
        <td align='center' colspan="1"><font color="<%=FontColor%>"><%=hashData.get("RESPONSECODE").toString()%></font></td>
        <td align='center' rowspan="2"><font color="<%=FontColor%>"><%=hashData.get("BATCHNO").toString()%></font></td>
        <td align='center' colspan="1"><font color="<%=FontColor%>">新台幣</font></td>
        <td align='right'  rowspan="2"><font color="<%=FontColor%>"><%
        if (BatchTransAmt.length()>0) {
          out.write(nf.format(Double.parseDouble(BatchTransAmt)));
        }%></font></td>
        <td align='center' colspan="1"><font color="<%=FontColor%>"><%
          String BatchDate = hashData.get("BATCHDATE").toString().replaceAll(" ","<br>");
          out.write(BatchDate);
          %></font></td>
          <td align='center' rowspan="2"><font color="<%=FontColor%>">待處理</font></td>
      </tr>
      <tr align='center' bgcolor='#ffffff' height="25">
        <td align='left'  ><font color="<%=FontColor%>"><%=hashData.get("BATCHTERMINALID").toString()%></font></td>
        <td align='left'  ><font color="<%=FontColor%>"><%=hashData.get("SYS_ORDERID").toString()%></font></td>
        <td align='left'  ><font color="<%=FontColor%>"><%
        String TransMode = hashData.get("TRANSMODE").toString();
        String TransModeDesc = "";
        if (TransMode.equalsIgnoreCase("0")) TransModeDesc = "一般";  //交易
        if (TransMode.equalsIgnoreCase("1")) TransModeDesc = "分期"; //交易
        if (TransMode.equalsIgnoreCase("2")) TransModeDesc = "紅利";//交易
        if (TransModeDesc.length()>0) TransMode = TransMode + "-" + TransModeDesc;
        out.write(TransMode);
        %></font></td>
        <td align='center'><font color="<%=FontColor%>"><%=hashData.get("BATCHTXAPPROVECODE").toString()%></font></td>
        <td align='right' ><font color="<%=FontColor%>"><%
        String TransAmt = hashData.get("TRANSAMT").toString();
        if (TransAmt.length()>0) {
          out.write(nf.format(Double.parseDouble(TransAmt)));
        }
        %></font></td>
        <td align='center'><font color="<%=FontColor%>"><%=hashData.get("DUE_DATE").toString()%></font></td>

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
      <td align='left' width='10%'>預請款</td>
      <td align='right' width='10%'><%=SumSaleSuccessCount%>筆</td>
      <td align='right' width='15%'><%=nf.format(Double.parseDouble(SumSaleSuccessAmt))%></td>
      <td align='left' width='65%'></td>
    </tr>

    <tr bgcolor='#ffffff' height="25">
      <td align='left' width='10%'>預退款</td>
      <td align='right' width='10%'><%=SumRefundSuccessCount%>筆</td>
      <td align='right' width='15%'><%=nf.format(Double.parseDouble(SumRefundSuccessAmt))%></td>
      <td align='left' width='65%'></td>
    </tr>

    <tr bgcolor='#ffffff' height="25">
      <td align='left' width='10%'>請款淨額</td>
      <td align='right' width='10%'><%=SumSaleSuccessCount+SumRefundSuccessCount%>筆</td>
      <td align='right' width='15%'><%=nf.format(Double.parseDouble(SumSaleSuccessAmt)-Double.parseDouble(SumRefundSuccessAmt))%></td>
      <td align='left' width='65%'></td>
    </tr>
  </table>
  <%} %>
  <%if (arrayFailList.size()>0) {%>
  <br>
    <br>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
    <tr align='center' bgcolor="#F4F4FF" height="25">
      <td align='center' rowspan="2">序號</td>
      <td align='center' colspan="1">特約商店代碼</td>
      <td align='center' colspan="1">特店指定單號</td>
      <td align='center' colspan="1">交易類別</td>
      <td align='center' rowspan="2">交易日期</td>
      <td align='center' colspan="1">回應碼</td>
      <td align='center' rowspan="2">批次號碼</td>
      <td align='center' colspan="1">交易幣別</td>
      <td align='center' rowspan="2">請款金額</td>
      <td align='center' colspan="1">請款日期</td>
      <td align='center' rowspan="2">請款狀態</td>
    </tr>

    <tr align='center' bgcolor='#F4F4FF' height="25">
      <td align='center'>端末機代碼</td>
      <td align='center'>系統指定單號</td>
      <td align='center'>交易模式</td>
      <td align='center'>授權碼</td>
      <td align='center'>交易金額</td>
      <td align='center'>請款期限</td>
    </tr>
    <%
    int count=0;
    for (int c=0; c<arrayFailList.size(); ++c) {
      count++;
      Hashtable hashData = (Hashtable)arrayFailList.get(c);
      String BatchTxMsg = hashData.get("BATCHTXMSG").toString();
      String FontColor = "#FF0000";
      String TransCode =  hashData.get("BATCHTRANSCODE").toString();
      String BatchTransAmt = hashData.get("BATCHTXAMT").toString().trim();
      if (TransCode.equalsIgnoreCase("00")) {
         SumSaleFailCount++;
         SumSaleFailAmt = String.valueOf(Double.parseDouble(SumSaleFailAmt)+Double.parseDouble(BatchTransAmt));
      }
      if (TransCode.equalsIgnoreCase("01")) {
         SumRefundFailCount++;
         SumRefundFailAmt = String.valueOf(Double.parseDouble(SumRefundFailAmt)+Double.parseDouble(BatchTransAmt));
      }
%>
<tr align='center' bgcolor='#ffffff'  height="25">
  <td align='left'   rowspan="2"><font color="<%=FontColor%>"><%=count%></font></td>
  <td align='left'   colspan="1"><font color="<%=FontColor%>"><%=hashData.get("MERCHANTID").toString()%></font></td>
  <td align='left'   colspan="1"><font color="<%=FontColor%>"><%=hashData.get("BATCHSYSORDERID").toString()%></font></td>
  <td align='left'   colspan="1"><font color="<%=FontColor%>"><%
  String TransDesc = "";

  if (TransCode.equalsIgnoreCase("00")) TransDesc = "購貨"; //交易
  if (TransCode.equalsIgnoreCase("01")) TransDesc = "退貨"; //交易
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
    TransTime = TransTime.substring(0,2)+":"+TransTime.substring(2,4)+"/"+TransTime.substring(4,6);
  }
  out.write(TransDate+"<br>"+TransTime);
  %></font></td>
  <td align='center' colspan="1"><font color="<%=FontColor%>"><%=hashData.get("RESPONSECODE").toString()%></font></td>
  <td align='center' rowspan="2"><font color="<%=FontColor%>"><%=hashData.get("BATCHNO").toString()%></font></td>
  <td align='center' colspan="1"><font color="<%=FontColor%>">新台幣</font></td>
  <td align='right'  rowspan="2"><font color="<%=FontColor%>"><%
  if (BatchTransAmt.length()>0) {
    out.write(nf.format(Double.parseDouble(BatchTransAmt)));
  }%></font></td>
    <td align='center' colspan="1"><font color="<%=FontColor%>"><%
     String BatchDate = hashData.get("BATCHDATE").toString().replaceAll(" ","<br>");
     out.write(BatchDate);
    %></font></td>
  <td align='center' rowspan="2"><font color="<%=FontColor%>"><%=hashData.get("BATCHRESPONSE").toString()%></font></td>
  </tr>
  <tr align='center' bgcolor='#ffffff' height="25">
    <td align='left'  ><font color="<%=FontColor%>"><%=hashData.get("BATCHTERMINALID").toString()%></font></td>
    <td align='left'  ><font color="<%=FontColor%>"><%=hashData.get("SYS_ORDERID").toString()%></font></td>
    <td align='left'  ><font color="<%=FontColor%>"><%
    String TransMode = hashData.get("TRANSMODE").toString();
    String TransModeDesc = "";
    if (TransMode.equalsIgnoreCase("0")) TransModeDesc = "一般";  //交易
    if (TransMode.equalsIgnoreCase("1")) TransModeDesc = "分期";  //交易
    if (TransMode.equalsIgnoreCase("2")) TransModeDesc = "紅利";  //交易
    if (TransModeDesc.length()>0) TransMode = TransMode + "-" + TransModeDesc;
    out.write(TransMode);
    %></font></td>
    <td align='center'><font color="<%=FontColor%>"><%=hashData.get("BATCHTXAPPROVECODE").toString()%></font></td>

  <td align='right' ><font color="<%=FontColor%>"><%
  String TransAmt = hashData.get("TRANSAMT").toString().trim();
  if (TransAmt.length()>0) {
    out.write(nf.format(Double.parseDouble(TransAmt)));
  }
%></font></td>
    <td align='center'><font color="<%=FontColor%>"><%=hashData.get("DUE_DATE").toString()%></font></td>
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
      <td align='left' width='10%'>請款剔退</td>
      <td align='right' width='10%'><%=SumSaleFailCount%>筆</td>
      <td align='right' width='15%'><%=nf.format(Double.parseDouble(SumSaleFailAmt))%></td>
      <td align='left' width='65%'></td>
    </tr>
    <tr bgcolor='#ffffff' height="25">
      <td align='left' width='10%'>退款剔退</td>
      <td align='right' width='10%'><%=SumRefundFailCount%>筆</td>
      <td align='right' width='15%'><%=nf.format(Double.parseDouble(SumRefundFailAmt))%></td>
      <td align='left' width='65%'></td>
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
