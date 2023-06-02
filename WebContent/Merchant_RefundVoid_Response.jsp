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

    String Message = "�`�N�G�L������";
    Hashtable hashVoidData = new Hashtable();
    ArrayList arrayList = new ArrayList();
    String VoidFlag = "false";
    String CancelAmt = "0";
    if (session.getAttribute("VoidDataResponse") != null) {
      arrayList = (ArrayList)session.getAttribute("VoidDataResponse");
      if (arrayList==null) arrayList = new ArrayList();
      if (session.getAttribute("Message") != null) {
        Message = (String)session.getAttribute("Message");
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
<form id="form" name="form" method="post" action ="./MerchantVoidCtl" >
<input type="hidden" name="Action" id="Action" value="Void">
<input type="hidden" name="InputSys_OrderID" id="InputSys_OrderID" value="">
 <%
 String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);
 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
      <td align="left" valign="middle"><b><font color="#004E87" size="3">�u�W�h�f�����@�~</font></b></td>
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
          </tr>
        </table>
      </td>
    </tr>
  </table>

  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
    <tr align='center' bgcolor="#F4F4FF" height="25">
      <td align='center' rowspan="2">�Ǹ�</td>
      <td align='center' rowspan="2">�ݥ����N�X</td>
      <td align='center' colspan="1">�S�����w�渹</td>
      <td align='center' colspan="1">������O</td>
      <td align='center' rowspan="2">������</td>
      <td align='center' colspan="1">�^���X</td>
      <td align='center' rowspan="2">�妸���X</td>
      <td align='center' rowspan="2">������O</td>
      <td align='center' rowspan="2">������B</td>
      <td align='center' rowspan="2">�дڪ��B</td>
    </tr>

    <tr align='center' bgcolor='#F4F4FF' height="25">
      <td align='center'>�t�Ϋ��w�渹</td>
      <td align='center'>����Ҧ�</td>
      <td align='center'>���v�X</td>
    </tr>
    <%
    int count=0;


    System.out.println("------------arrayList.size()="+arrayList.size());

    for (int c=0; c<arrayList.size(); ++c) {
      count++;
      Hashtable hashData = (Hashtable)arrayList.get(c);
      String TransCode = hashData.get("TRANSCODE").toString();
      String FontColor = "";
      String BalanceAmt = hashData.get("BALANCEAMT").toString();  // Billing.balanceamt
      if (TransCode.equalsIgnoreCase("01")) {  // �h�f
        if (BalanceAmt.length()>0 && Double.parseDouble(BalanceAmt)>0) {
          CancelAmt = String.valueOf(Double.parseDouble(CancelAmt)+Double.parseDouble(BalanceAmt));
        }
        FontColor = "#FF0000";
      }
      if (TransCode.equalsIgnoreCase("10")) {  // �ʳf����
        if (BalanceAmt.length()>0 && Double.parseDouble(BalanceAmt)>0) {
          CancelAmt = String.valueOf(Double.parseDouble(CancelAmt)-Double.parseDouble(BalanceAmt));
        }

        FontColor = "#0000FF";
      }
      if (TransCode.equalsIgnoreCase("11")) {  // �h�f����
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
  if (TransCode.equalsIgnoreCase("00")) TransDesc = "�ʳf";//���
  if (TransCode.equalsIgnoreCase("01")) TransDesc = "�h�f";//���
  if (TransCode.equalsIgnoreCase("10")) TransDesc = "�ʳf����";//���
  if (TransCode.equalsIgnoreCase("11")) TransDesc = "�h�f����";//���
  if (TransCode.equalsIgnoreCase("20")) TransDesc = "�ʳf�д�";//���
  if (TransCode.equalsIgnoreCase("21")) TransDesc = "�ʳf�дڨ���";//���

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
  <td align='center' rowspan="2"><font color="<%=FontColor%>">�s�x��</font></td>
  <td align='right'  rowspan="2"><font color="<%=FontColor%>"><%
  String TransAmt = hashData.get("TRANSAMT").toString();
  if (TransAmt.length()>0) {
    out.write(nf.format(Double.parseDouble(TransAmt)));
  }%></font></td>
  <td align='right'  rowspan="2"><font color="<%=FontColor%>"><%
  if (BalanceAmt.length()>0) {
    if (Double.parseDouble(BalanceAmt) >0) {
       out.write(nf.format(Double.parseDouble(BalanceAmt)));
    } else {
       out.write(nf.format(Double.parseDouble("0")));
    }

  }%></font></td>
  </tr>

  <tr align='center' bgcolor='#ffffff' height="25">
    <td align='left'  ><font color="<%=FontColor%>"><%=hashData.get("SYS_ORDERID").toString()%></font></td>
    <td align='left'  ><font color="<%=FontColor%>"><%
    String TransMode = hashData.get("TRANSMODE").toString();
    String TransModeDesc = "";
    if (TransMode.equalsIgnoreCase("0")) TransModeDesc = "�@��";//���
    if (TransMode.equalsIgnoreCase("1")) TransModeDesc = "����";//���
    if (TransMode.equalsIgnoreCase("2")) TransModeDesc = "���Q";//���
    if (TransModeDesc.length()>0) TransMode = TransMode + "-" + TransModeDesc;
    out.write(TransMode);
    %></font></td>
    <td align='center'><font color="<%=FontColor%>"><%=hashData.get("APPROVECODE").toString()%></font></td>
  </tr>
  <%} %>
  </table>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr>
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="30" align="left"></td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
  <%} %>
  <%if (Message.length()>0) { %>
  <table width="100%">
    <tr><td><b><font color="#FF0000" size="3"><%=Message%></font></b></td></tr>
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
