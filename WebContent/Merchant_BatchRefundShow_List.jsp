<%@page contentType="text/html;charset=BIG5"%>
<%@page import="java.util.*" %>
<%@page import="java.text.*"%>
<%@page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%
//  response.addHeader("Pragma", "No-cache");
//  response.addHeader("Cache-Control", "no-cache");
//  response.addDateHeader("Expires", 1);
  try {
    String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);

    NumberFormat nf = NumberFormat.getInstance();
    nf.setMaximumFractionDigits(2);
    nf.setMinimumFractionDigits(2);

    Hashtable hashRefundData = new Hashtable();
    ArrayList arraySuccessList = new ArrayList();
    ArrayList arrayFailList = new ArrayList();
    int SumFailCont = 0;
    int SumSuccessCont = 0;
    String SumSuccessAmt = "0";
    String SumFailAmt = "0";
    String BatchPmtID = "";
    boolean inputFlag =  false; // �O�_�i��J�h�f���B
    if (session.getAttribute("RefundData") != null) {
      hashRefundData = (Hashtable)session.getAttribute("RefundData");
      if (hashRefundData==null) hashRefundData = new Hashtable();
      if (hashRefundData.size()>0) {
        arraySuccessList = (ArrayList)hashRefundData.get("SUCCESSLIST");
        if (arraySuccessList==null) arraySuccessList = new ArrayList();
        arrayFailList = (ArrayList)hashRefundData.get("FAILLIST");
        if (arrayFailList==null) arrayFailList = new ArrayList();
        BatchPmtID = hashRefundData.get("BATCHPMTID").toString();
        for (int c=0; c<arraySuccessList.size(); ++c) {
           Hashtable hashData = (Hashtable)arraySuccessList.get(c);
           String BatchTxMsg = hashData.get("BATCHTXMSG").toString();
           String BatchTransAmt = hashData.get("BATCHTXAMT").toString();
           if (BatchTxMsg.equalsIgnoreCase("FAIL")) {  // ����
             SumFailCont++;
             SumFailAmt = String.valueOf(Double.parseDouble(SumFailAmt)+Double.parseDouble(BatchTransAmt));
           } else {
             SumSuccessCont++;
             SumSuccessAmt = String.valueOf(Double.parseDouble(SumSuccessAmt)+Double.parseDouble(BatchTransAmt));
           }
        }
        for (int c=0; c<arrayFailList.size(); ++c) {
           Hashtable hashData = (Hashtable)arrayFailList.get(c);
           String BatchTxMsg = hashData.get("BATCHTXMSG").toString();
           String BatchTransAmt = hashData.get("BATCHTXAMT").toString();
           if (BatchTxMsg.equalsIgnoreCase("FAIL")) {  // ����
             SumFailCont++;
             SumFailAmt = String.valueOf(Double.parseDouble(SumFailAmt)+Double.parseDouble(BatchTransAmt));
           } else {
             SumSuccessCont++;
             SumSuccessAmt = String.valueOf(Double.parseDouble(SumSuccessAmt)+Double.parseDouble(BatchTransAmt));
           }
        }
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
       var url = "<%=request.getContextPath()%>/MerchantBatchRefundCtl?Action=Print&<%=MerchantMenuCtl.MENU_FORWARD_KEY%>=<%=MenuKey%>&BatchPmtID="+document.all.form.BatchPmtID.value+"&PrintType="+PrintType;
       subWin=window.open(url,"main",strFeatures);
  }

  function closeSub(){
    if (subWin != null && subWin.open) subWin.close(); subWin=null;
  }

//-->
</script>

</head>

<body>
<form id="form" name="form" method="post" action ="./MerchantBatchRefundCtl" >
<input type="hidden" name="Action" id="Action" value="Refund">
<input type="hidden" name="BatchPmtID" id="BatchPmtID" value="<%=BatchPmtID%>">
 <%

 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
      <td align="left" valign="middle"><b><font color="#004E87" size="3">�ɮװh�f�@�~</font></b></td>
  </tr>
</table>

<hr style="height:1px">

  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr>
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="30" align="left"><input type='button' value='�HPDF�榡�ץX' name='btnPDF' id='btnPDF' onclick="toPrint('PDF');"><input type='button' value='�HCSV�榡�ץX' name='btnCSV' id='btnCSV' onclick="toPrint('CSV');"><input type='button' value='�HTXT�榡�ץX' name='btnTXT' id='btnTXT' onclick="toPrint('TXT');">
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
  <%if (arraySuccessList.size()>0) {%>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
    <tr align='center' bgcolor="#F4F4FF" height="25">
      <td align='center' rowspan="2">�Ǹ�</td>
      <td align='center' colspan="1">�S���ө��N�X</td>
      <td align='center' colspan="1">�S�����w�渹</td>
      <td align='center' colspan="1">������O</td>
      <td align='center' rowspan="2">������</td>
      <td align='center' colspan="1">�^���X</td>
      <td align='center' rowspan="2">�妸���X</td>
      <td align='center' rowspan="2">������O</td>
      <td align='center' rowspan="2">�h�f���B</td>
      <td align='center' rowspan="2">�h�f���p</td>
    </tr>

    <tr align='center' bgcolor='#F4F4FF' height="25">
      <td align='center'>�ݥ����N�X</td>
      <td align='center'>�t�Ϋ��w�渹</td>
      <td align='center'>����Ҧ�</td>
      <td align='center'>���v�X</td>
    </tr>
    <%
    int count=0;
    for (int c=0; c<arraySuccessList.size(); ++c) {
      count++;
      Hashtable hashData = (Hashtable)arraySuccessList.get(c);
      String BatchTxMsg = hashData.get("BATCHTXMSG").toString();
      String FontColor = "";
      String BatchTransAmt = hashData.get("BATCHTXAMT").toString();
      if (BatchTxMsg.equalsIgnoreCase("FAIL")) {  // ����
        FontColor = "#FF0000";
//        SumFailCont++;
//        SumFailAmt = String.valueOf(Double.parseDouble(SumFailAmt)+Double.parseDouble(BatchTransAmt));
//      } else {
//        SumSuccessCont++;
//        SumSuccessAmt = String.valueOf(Double.parseDouble(SumSuccessAmt)+Double.parseDouble(BatchTransAmt));
      }
%>
<tr align='center' bgcolor='#ffffff'  height="25">
  <td align='left'   rowspan="2"><font color="<%=FontColor%>"><%=count%></font></td>
  <td align='left'   colspan="1"><font color="<%=FontColor%>"><%=hashData.get("MERCHANTID").toString()%></font></td>
  <td align='left'   colspan="1"><font color="<%=FontColor%>"><%=hashData.get("BATCHSYSORDERID").toString()%></font></td>
  <td align='left'   colspan="1"><font color="<%=FontColor%>"><%
  String TransDesc = "";
  String TransCode =  hashData.get("BATCHTRANSCODE").toString();
  if (TransCode.equalsIgnoreCase("00")) TransDesc = "�ʳf";//���
  if (TransCode.equalsIgnoreCase("01")) TransDesc = "�h�f";//���
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
  <td align='center' rowspan="2"><font color="<%=FontColor%>">�s�x��</font></td>
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
    if (TransMode.equalsIgnoreCase("0")) TransModeDesc = "�@��";//���
    if (TransMode.equalsIgnoreCase("1")) TransModeDesc = "����";//���
    if (TransMode.equalsIgnoreCase("2")) TransModeDesc = "���Q";//���
    if (TransModeDesc.length()>0) TransMode = TransMode + "-" + TransModeDesc;
    out.write(TransMode);
    %></font></td>
    <td align='center'><font color="<%=FontColor%>"><%=hashData.get("BATCHTXAPPROVECODE").toString()%></font></td>
  </tr>
  <%} %>
  </table>
  <%} %>
  <br>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
    <tr bgcolor='#ffffff' height="25">
      <td align='left' width='10%'></td>
      <td align='center' width='10%'>����</td>
      <td align='center' width='15%'>���B</td>
      <td align='left' width='65%'></td>
    </tr>

    <tr bgcolor='#ffffff' height="25">
      <td align='left' width='10%'><font color="#FF0000">�h�f����</font></td>
      <td align='right' width='10%'><font color="#FF0000"><%=SumFailCont%>��</font></td>
      <td align='right' width='15%'><font color="#FF0000"><%=nf.format(Double.parseDouble(SumFailAmt))%></font></td>
      <td align='left' width='65%'></td>
    </tr>

    <tr bgcolor='#ffffff' height="25">
      <td align='left' width='10%'>�h�f���\</td>
      <td align='right' width='10%'><%=SumSuccessCont%>��</td>
      <td align='right' width='15%'><%=nf.format(Double.parseDouble(SumSuccessAmt))%></td>
      <td align='left' width='65%'></td>
    </tr>

    <tr bgcolor='#ffffff' height="25">
      <td align='left' width='10%'>�`�p</td>
      <td align='right' width='10%'><%=SumFailCont+SumSuccessCont%>��</td>
      <td align='right' width='15%'><%=nf.format(Double.parseDouble(SumSuccessAmt)+Double.parseDouble(SumFailAmt))%></td>
      <td align='left' width='65%'></td>
    </tr>
  </table>
  <%if (arrayFailList.size()>0) {%>
  <br>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
    <tr align='center' bgcolor="#F4F4FF" height="25">
      <td align='center' rowspan="2">�Ǹ�</td>
      <td align='center' colspan="1">�S���ө��N�X</td>
      <td align='center' colspan="1">�S�����w�渹</td>
      <td align='center' colspan="1">������O</td>
      <td align='center' rowspan="2">������</td>
      <td align='center' colspan="1">�^���X</td>
      <td align='center' rowspan="2">�妸���X</td>
      <td align='center' rowspan="2">������O</td>
      <td align='center' rowspan="2">�h�f���B</td>
      <td align='center' rowspan="2">���ѭ�]</td>
    </tr>

    <tr align='center' bgcolor='#F4F4FF' height="25">
      <td align='center'>�ݥ����N�X</td>
      <td align='center'>�t�Ϋ��w�渹</td>
      <td align='center'>����Ҧ�</td>
      <td align='center'>���v�X</td>
    </tr>
    <%
    int count=0;
    for (int c=0; c<arrayFailList.size(); ++c) {
      count++;
      Hashtable hashData = (Hashtable)arrayFailList.get(c);
      String BatchTxMsg = hashData.get("BATCHTXMSG").toString();
      String FontColor = "#FF0000";
      String BatchTransAmt = hashData.get("BATCHTXAMT").toString();
%>
<tr align='center' bgcolor='#ffffff'  height="25">
  <td align='left'   rowspan="2"><font color="<%=FontColor%>"><%=count%></font></td>
  <td align='left'   colspan="1"><font color="<%=FontColor%>"><%=hashData.get("MERCHANTID").toString()%></font></td>
  <td align='left'   colspan="1"><font color="<%=FontColor%>"><%=hashData.get("BATCHSYSORDERID").toString()%></font></td>
  <td align='left'   colspan="1"><font color="<%=FontColor%>"><%
  String TransDesc = "";
  String TransCode =  hashData.get("BATCHTRANSCODE").toString();
  if (TransCode.equalsIgnoreCase("00")) TransDesc = "�ʳf";//���
  if (TransCode.equalsIgnoreCase("01")) TransDesc = "�h�f";//���
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
  <td align='center' rowspan="2"><font color="<%=FontColor%>">�s�x��</font></td>
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
    if (TransMode.equalsIgnoreCase("0")) TransModeDesc = "�@��";//���
    if (TransMode.equalsIgnoreCase("1")) TransModeDesc = "����";//���
    if (TransMode.equalsIgnoreCase("2")) TransModeDesc = "���Q";//���
    if (TransModeDesc.length()>0) TransMode = TransMode + "-" + TransModeDesc;
    out.write(TransMode);
    %></font></td>
    <td align='center'><font color="<%=FontColor%>"><%=hashData.get("BATCHTXAPPROVECODE").toString()%></font></td>
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
