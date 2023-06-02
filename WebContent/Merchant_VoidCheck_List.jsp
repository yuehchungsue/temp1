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

    Hashtable userinfo=(Hashtable)session.getAttribute("SYSCONFDATA");
    Hashtable sysinfo=(Hashtable)userinfo.get("SYSCONF");
    int PagePcs =  100;
    if (sysinfo.size()>0) {
      String strPagePcs = sysinfo.get("MER_PAGE_CUL").toString();
      if (strPagePcs.length()>0) {
        PagePcs = Integer.parseInt(strPagePcs);
      }
    }

    Hashtable hashCaptureSucessData = new Hashtable();
    Hashtable hashCaptureFailData = new Hashtable();
    ArrayList arrayList = new ArrayList();
    String PartialFlag = "N";
    String CheckFlag = "Y";
    if (session.getAttribute("CaptureCheckData") != null) {
      Hashtable hashCaptureData = (Hashtable)session.getAttribute("CaptureCheckData");
      if (hashCaptureData==null) hashCaptureData = new Hashtable();
      if (hashCaptureData.size()>0) {
         hashCaptureSucessData = (Hashtable)hashCaptureData.get("CaptureSuccessData");
         hashCaptureFailData = (Hashtable)hashCaptureData.get("CaptureFailData");
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
<form id="form" name="form" method="post" action ="./MerchantCaptureCtl" >
<input type="hidden" name="Action" id="Action" value="Capture">
 <%
 String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);
 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
      <td align="left" valign="middle"><b><font color="#004E87" size="3">線上交易請款作業</font></b></td>
  </tr>
</table>

<hr style="height:1px">
  <%if (hashCaptureSucessData.size()>0) { %>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr>
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="30" align="left"><input type='button' value='確定請款' name='btnSubmit' id='btnSubmit' onclick="toSubmit();"></td>
            <td align="right">可請款總筆數：<%=hashCaptureSucessData.size()%>筆</td>
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
      <td align='center' colspan="1">請款期限</td>
      <td align='center' rowspan="2">請款狀態</td>
    </tr>

    <tr align='center' bgcolor='#F4F4FF' height="25">
      <td align='center'>系統指定單號</td>
      <td align='center'>交易模式</td>
      <td align='center'>授權碼</td>
      <td align='center'>請款金額</td>
    </tr>

    <%
    String SumCaptureAmt ="0";
    String SumRefundAmt ="0";
    String NetCaptureAmt ="0";
    int count=0;
    for (int c=0; c<hashCaptureSucessData.size(); ++c) {
      count++;
      Hashtable hashData = (Hashtable)hashCaptureSucessData.get(String.valueOf(c));
      String TransCode = hashData.get("TRANSCODE").toString();
      String FontColor = "";
      if (TransCode.equalsIgnoreCase("01")) {  // 退貨
        FontColor = "#FF0000";
      } %>

      <tr align='center' bgcolor='#ffffff'  height="25">
       <td align='left'   rowspan="2"><font color="<%=FontColor%>"><%=count%></font></td>
       <td align='left'   rowspan="2"><font color="<%=FontColor%>"><%=hashData.get("TERMINALID").toString()%></font></td>
       <td align='left'   colspan="1"><font color="<%=FontColor%>"><%=hashData.get("ORDERID").toString()%></font></td>
       <td align='left'   colspan="1"><font color="<%=FontColor%>"><%
         String TransDesc = "";
         if (TransCode.equalsIgnoreCase("00")) TransDesc = "購貨";//交易
         if (TransCode.equalsIgnoreCase("01")) TransDesc = "退貨";//交易
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
       <td align='center' colspan="1"><font color="<%=FontColor%>"><%=hashData.get("CAPTUREDDEALLINE").toString()%></font></td>
      <td align='center' rowspan="2"><font color="<%=FontColor%>"><%=hashData.get("CHECKRESPONSE").toString()%></font></td>
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
      <td align='right' ><font color="<%=FontColor%>"><%
        String ToCaptureAmt = hashData.get("TOCAPTUREAMT").toString();
        if (ToCaptureAmt.length()>0) {
          if (Double.parseDouble(ToCaptureAmt) >0) {
            String TempTransCode = hashData.get("TRANSCODE").toString();
            if (TempTransCode.equalsIgnoreCase("00")) {
               SumCaptureAmt = String.valueOf(Double.parseDouble(SumCaptureAmt)+Double.parseDouble(ToCaptureAmt));
            }
            if (TempTransCode.equalsIgnoreCase("01")) {
               SumRefundAmt = String.valueOf(Double.parseDouble(SumRefundAmt)+Double.parseDouble(ToCaptureAmt));
            }
            out.write(nf.format(Double.parseDouble(ToCaptureAmt)));
          } else {
            out.write(nf.format(Double.parseDouble("0")));
          }
        }%></font></td>
      </tr>
   <%} %>
   <tr align='center' bgcolor='#ffffff'  height="25">
       <td align='left'   colspan="2"><font color="">本頁小計</font></td>
       <td align='left'   colspan="9"><font color=""></font></td>
   </tr>
   <tr align='center' bgcolor='#ffffff'  height="25">
       <td align='left'   colspan="2"><font color="">預請款金額</font></td>
       <td align='left'   colspan="9"><font color=""><%=nf.format(Double.parseDouble(SumCaptureAmt))%></font></td>
   </tr>
   <tr align='center' bgcolor='#ffffff'  height="25">
       <td align='left'   colspan="2"><font color="">預退貨金額</font></td>
       <td align='left'   colspan="9"><font color=""><%=nf.format(Double.parseDouble(SumRefundAmt))%></font></td>
   </tr>
   <tr align='center' bgcolor='#ffffff'  height="25">
       <td align='left'   colspan="2"><font color="">請款淨額</font></td>
       <td align='left'   colspan="9"><font color=""><%=nf.format(Double.parseDouble(SumCaptureAmt)-Double.parseDouble(SumRefundAmt))%></font></td>
   </tr>
  </table>

<%}%>
  <%if (hashCaptureFailData.size()>0) { %>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr>
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="30" align="left"><%if (hashCaptureSucessData.size()==0) { %><input type='button' value='確定請款' name='btnSubmit' id='btnSubmit' onclick="toSubmit();"><%}%></td>
            <td align="right">無法請款總筆數：<%=hashCaptureFailData.size()%>筆</td>
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
      <td align='center' colspan="1">請款期限</td>
      <td align='center' rowspan="2">請款狀態</td>
    </tr>

    <tr align='center' bgcolor='#F4F4FF' height="25">
      <td align='center'>系統指定單號</td>
      <td align='center'>交易模式</td>
      <td align='center'>授權碼</td>
      <td align='center'>請款金額</td>
    </tr>

    <%
    int count=0;
    for (int c=0; c<hashCaptureFailData.size(); ++c) {
      count++;
      Hashtable hashData = (Hashtable)hashCaptureFailData.get(String.valueOf(c));
      String TransCode = hashData.get("TRANSCODE").toString();
      String FontColor = "";
      if (TransCode.equalsIgnoreCase("01")) {  // 退貨
        FontColor = "#FF0000";
      } %>

      <tr align='center' bgcolor='#ffffff'  height="25">
       <td align='left'   rowspan="2"><font color="<%=FontColor%>"><%=count%></font></td>
       <td align='left'   rowspan="2"><font color="<%=FontColor%>"><%=hashData.get("TERMINALID").toString()%></font></td>
       <td align='left'   colspan="1"><font color="<%=FontColor%>"><%=hashData.get("ORDERID").toString()%></font></td>
       <td align='left'   colspan="1"><font color="<%=FontColor%>"><%
         String TransDesc = "";
         if (TransCode.equalsIgnoreCase("00")) TransDesc = "購貨";//交易
         if (TransCode.equalsIgnoreCase("01")) TransDesc = "退貨";//交易
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
       <td align='center' colspan="1"><font color="<%=FontColor%>"><%=hashData.get("CAPTUREDDEALLINE").toString()%></font></td>
      <td align='center' rowspan="2"><font color="<%=FontColor%>"><%=hashData.get("CHECKRESPONSE").toString()%></font></td>
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
      <td align='right' ><font color="<%=FontColor%>"><%
        String ToCaptureAmt = hashData.get("TOCAPTUREAMT").toString();
        if (ToCaptureAmt.length()>0) {
          if (Double.parseDouble(ToCaptureAmt) >0) {
            out.write(nf.format(Double.parseDouble(ToCaptureAmt)));
          } else {
            out.write(nf.format(Double.parseDouble("0")));
          }
        }%></font></td>
      </tr>
   <%} %>
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
