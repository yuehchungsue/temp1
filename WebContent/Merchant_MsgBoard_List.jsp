<%@page contentType="text/html;charset=BIG5"%>
<%@page import="java.util.*" %>
<%@page import="java.text.*"%>
<%@page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%@page import="com.cybersoft.bean.UserBean" %>
<%
  try {
  response.addHeader("Pragma", "No-cache");
  response.addHeader("Cache-Control", "no-cache");
  response.setDateHeader("Expires", 0);

  NumberFormat nf = NumberFormat.getInstance();
  nf.setMaximumFractionDigits(2);
  nf.setMinimumFractionDigits(2);

  Hashtable hashMsgBoardData =  new Hashtable();
  ArrayList arrayCapture = new ArrayList();
  ArrayList arrayBilling = new ArrayList();
  UserBean UserBean = new UserBean();
  String Today = UserBean.get_AppointDay_Date("yyyy/MM/dd", 0);
  if (session.getAttribute("MsgBoardData") != null) {
    hashMsgBoardData = (Hashtable)session.getAttribute("MsgBoardData");
    session.removeAttribute("MsgBoardData");
    if (hashMsgBoardData.size()>0) {
       arrayCapture = (ArrayList)hashMsgBoardData.get("CaputreData");
       arrayBilling = (ArrayList)hashMsgBoardData.get("BillingData");
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

//-->
</script>
</head>
<body>
<form id="form" name="form" method="post" action ="./MerchantMsgBoardCtl" >
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
      <td align="left" valign="middle"><font size="3" color="#004E87"><b>公告訊息</b></font></td>
  </tr>
</table>
<hr style="height:1px">
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr>
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="30" align="left"><img src="images/sub_title_icon.jpg"><img src="images/sub_title_icon.jpg"><font size="2" ><b>目前系統日期：<%=Today%></b></font></td>
            <td align="right"></td>
          </tr>
       </table>
     </td>
    </tr>
  </table>
<%if (arrayCapture.size()>0) { %>
<table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
  <tr align='center' bgcolor="#F4F4FF" height="25">
    <td align='left' colspan="10"><B> >> 請款交易狀態</B></td>
  </tr>
  <tr align='center' bgcolor="#F4F4FF" height="25">
    <td align='center' rowspan="2">序號</td>
    <td align='center' rowspan="2">請款日</td>
    <td align='center' colspan="2">待處理</td>
    <td align='center' colspan="2">已請款(處理中)</td>
    <td align='center' colspan="2">請款核准</td>
    <td align='center' colspan="2"><font color="#FF0000">請款剔退</font></td>
  </tr>
  <tr align='center' bgcolor='#F4F4FF' height="25">
    <td align='center'>筆數</td>
    <td align='center'>淨額</td>
    <td align='center'>筆數</td>
    <td align='center'>淨額</td>
    <td align='center'>筆數</td>
    <td align='center'>淨額</td>
    <td align='center'><font color="#FF0000">筆數</font></td>
    <td align='center'><font color="#FF0000">淨額</font></td>
  </tr>
<%int TotalCaptureCount=0;
  int TotalCaptureProcessCount=0;
  int TotalCaptureApproveCount=0;
  int TotalCaptureRejectCount=0;
  String TotalCaptureAmt="0";
  String TotalCaptureProcessAmt="0";
  String TotalCaptureApproveAmt="0";
  String TotalCaptureRejectAmt="0";
  int count=0;
  for (int c=0; c<arrayCapture.size(); ++c) {
      count++;
      Hashtable hashData = (Hashtable)arrayCapture.get(c);
%><tr align='center' bgcolor='#ffffff'  height="25">
    <td align='left'><font color=""><%=count%></font></td>
    <td align='left'><font color=""><%=hashData.get("CAPTUREDATE").toString()%></font></td>
    <td align='right'><font color=""><%
    String CaptureCount = hashData.get("CAPTURECOUNT").toString();
    if (CaptureCount.length()>0) {
      TotalCaptureCount = TotalCaptureCount + Integer.parseInt(CaptureCount);
      out.write(String.valueOf(CaptureCount));
    }
    %></font></td>
    <td align='right'><font color=""><%
  String CaptureAmt = hashData.get("CAPTUREAMT").toString();
  if (CaptureAmt.length()>0) {
    TotalCaptureAmt = String.valueOf(Double.parseDouble(TotalCaptureAmt) + Double.parseDouble(CaptureAmt));
    out.write(nf.format(Double.parseDouble(CaptureAmt)));
  }%></font></td>
    <td align='right'><font color=""><%
    String CaptureProcessCount = hashData.get("CAPTUREPROCESSCOUNT").toString();
    if (CaptureProcessCount.length()>0) {
      TotalCaptureProcessCount = TotalCaptureProcessCount + Integer.parseInt(CaptureProcessCount);
      out.write(String.valueOf(CaptureProcessCount));
    }%></font></td>
    <td align='right'><font color=""><%
  String CaptureProcessAmt = hashData.get("CAPTUREPROCESSAMT").toString();
  if (CaptureProcessAmt.length()>0) {
    TotalCaptureProcessAmt = String.valueOf(Double.parseDouble(TotalCaptureProcessAmt) + Double.parseDouble(CaptureProcessAmt));
    out.write(nf.format(Double.parseDouble(CaptureProcessAmt)));
  }%></font></td>
    <td align='right'><font color=""><%
    String CaptureApproveCount = hashData.get("CAPTUREAPPROVECOUNT").toString();
    if (CaptureApproveCount.length()>0) {
      TotalCaptureApproveCount = TotalCaptureApproveCount + Integer.parseInt(CaptureApproveCount);
      out.write(String.valueOf(CaptureApproveCount));
    }%></font></td>
    <td align='right'><font color=""><%
  String CaptureApproveAmt = hashData.get("CAPTUREAPPROVEAMT").toString();
  if (CaptureApproveAmt.length()>0) {
    TotalCaptureApproveAmt = String.valueOf(Double.parseDouble(TotalCaptureApproveAmt) + Double.parseDouble(CaptureApproveAmt));
    out.write(nf.format(Double.parseDouble(CaptureApproveAmt)));
  }%></font></td>
    <td align='right'><font color="#FF0000"><%
    String CaptureRejectCount = hashData.get("CAPTUREREJECTCOUNT").toString();
    if (CaptureRejectCount.length()>0) {
      TotalCaptureRejectCount = TotalCaptureRejectCount + Integer.parseInt(CaptureRejectCount);
      out.write(String.valueOf(CaptureRejectCount));
    }    %></font></td>
    <td align='right'><font color="#FF0000"><%
  String CaptureRejectAmt = hashData.get("CAPTUREREJECTAMT").toString();
  if (CaptureRejectAmt.length()>0) {
    TotalCaptureRejectAmt = String.valueOf(Double.parseDouble(TotalCaptureRejectAmt) + Double.parseDouble(CaptureRejectAmt));
    out.write(nf.format(Double.parseDouble(CaptureRejectAmt)));
  }%></font></td>
  </tr>
  <%} %>
  <tr align='center' bgcolor='#ffffff'  height="25">
    <td align='right' colspan='2'><font color="">總　　計：</font></td>
    <td align='right'><font color=""><%=TotalCaptureCount%></font></td>
    <td align='right'><font color=""><%=nf.format(Double.parseDouble(TotalCaptureAmt))%></font></td>
    <td align='right'><font color=""><%=TotalCaptureProcessCount%></font></td>
    <td align='right'><font color=""><%=nf.format(Double.parseDouble(TotalCaptureProcessAmt))%></font></td>
    <td align='right'><font color=""><%=TotalCaptureApproveCount%></font></td>
    <td align='right'><font color=""><%=nf.format(Double.parseDouble(TotalCaptureApproveAmt))%></font></td>
    <td align='right'><font color="#FF0000"><%=TotalCaptureRejectCount%></font></td>
    <td align='right'><font color="#FF0000"><%=nf.format(Double.parseDouble(TotalCaptureRejectAmt))%></font></td>
  </tr>
</table>
<%} else { %>
<table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
  <tr align='center' bgcolor="#F4F4FF" height="25">
    <td align='left' ><B> >> 請款交易狀態</B></td>
  </tr>
  <tr align='center' bgcolor='#ffffff'  height="25">
    <td align='left'><font color="#FF0000">無請款交易狀態公告訊息資料</font></td>
  </tr>
</table>
<%} %>
<br>
<%if (arrayBilling.size()>0) { %>
<table>
<tr><td>
<table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8" >
  <tr align='center' bgcolor="#F4F4FF" height="25">
    <td align='left' colspan="10"><B><font color="#FF0000"> >> 將逾期交易</font></B></td>
  </tr>
  <tr align='center' bgcolor="#F4F4FF" height="25">
    <td align='center' rowspan="2"><font color="#FF0000">序號</font></td>
    <td align='center' rowspan="2"><font color="#FF0000">逾期日</font></td>
    <td align='center' rowspan="2"><font color="#FF0000">交易日</font></td>
    <td align='center' colspan="2"><font color="#FF0000">購貨</font></td>
    <td align='center' colspan="2"><font color="#FF0000">退貨</font></td>
  </tr>
  <tr align='center' bgcolor='#F4F4FF' height="25">
    <td align='center'><font color="#FF0000">筆數</font></td>
    <td align='center'><font color="#FF0000">金額</font></td>
    <td align='center'><font color="#FF0000">筆數</font></td>
    <td align='center'><font color="#FF0000">金額</font></td>
  </tr>
<%
  int TotalSaleCaptureBehindCount=0;
  String TotalSaleCaptureBehindAmt="0";
  int TotalRefundCaptureBehindCount=0;
  String TotalRefundCaptureBehindAmt="0";
  int count=0;
  for (int c=0; c<arrayBilling.size(); ++c) {
      count++;
      Hashtable hashData = (Hashtable)arrayBilling.get(c);
%><tr align='center' bgcolor='#ffffff'  height="25">
    <td align='left'><font color="#FF0000"><%=count%></font></td>
    <td align='left'><font color="#FF0000"><%=hashData.get("CAPTUREDEALDATE").toString()%></font></td>
    <td align='left'><font color="#FF0000"><%=hashData.get("TRANSDATE").toString()%></font></td>
    <td align='right'><font color="#FF0000"><%
    String SaleCaptureBehindCount = hashData.get("SALECAPTUREBEHINDCOUNT").toString();
    if (SaleCaptureBehindCount.length()>0) {
      TotalSaleCaptureBehindCount = TotalSaleCaptureBehindCount+ Integer.parseInt(SaleCaptureBehindCount);
      out.write(String.valueOf(SaleCaptureBehindCount));
    }
    %></font></td>
    <td align='right'><font color="#FF0000"><%
  String SaleCaptureBehindAmt = hashData.get("SALECAPTUREBEHINDAMT").toString();
  if (SaleCaptureBehindAmt.length()>0) {
    TotalSaleCaptureBehindAmt = String.valueOf(Double.parseDouble(TotalSaleCaptureBehindAmt) + Double.parseDouble(SaleCaptureBehindAmt));
    out.write(nf.format(Double.parseDouble(SaleCaptureBehindAmt)));
  }%></font></td>
    <td align='right'><font color="#FF0000"><%
    String RefundCaptureBehindCount = hashData.get("REFUNDCAPTUREBEHINDCOUNT").toString();
    if (RefundCaptureBehindCount.length()>0) {
      TotalRefundCaptureBehindCount = TotalRefundCaptureBehindCount+ Integer.parseInt(RefundCaptureBehindCount);
      out.write(String.valueOf(RefundCaptureBehindCount));
    }
    %></font></td>
    <td align='right'><font color="#FF0000"><%
  String RefundCaptureBehindAmt = hashData.get("REFUNDCAPTUREBEHINDAMT").toString();
  if (RefundCaptureBehindAmt.length()>0) {
    TotalRefundCaptureBehindAmt = String.valueOf(Double.parseDouble(TotalRefundCaptureBehindAmt) + Double.parseDouble(RefundCaptureBehindAmt));
    out.write(nf.format(Double.parseDouble(RefundCaptureBehindAmt)));
  }%></font></td>
  </tr>
<%} %>
  <tr align='center' bgcolor='#ffffff'  height="25">
    <td align='right' colspan='3'><font color="#FF0000">總　　計：</font></td>
    <td align='right'><font color="#FF0000"><%=TotalSaleCaptureBehindCount%></font></td>
    <td align='right'><font color="#FF0000"><%=nf.format(Double.parseDouble(TotalSaleCaptureBehindAmt))%></font></td>
    <td align='right'><font color="#FF0000"><%=TotalRefundCaptureBehindCount%></font></td>
    <td align='right'><font color="#FF0000"><%=nf.format(Double.parseDouble(TotalRefundCaptureBehindAmt))%></font></td>

  </tr>
</table>
</td></tr>
<tr><td></td></tr>
</table>
<%} else { %>
<table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
  <tr align='center' bgcolor="#F4F4FF" height="25">
    <td align='left' colspan="6"><B> >> 將逾期交易</B></td>
  </tr>

  <tr align='center' bgcolor='#ffffff'  height="25">
    <td align='left'><font color="#FF0000">無將逾期交易公告訊息資料</font></td>
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
