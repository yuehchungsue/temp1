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

    Hashtable userinfo=(Hashtable)session.getAttribute("SYSCONFDATA");
    Hashtable sysinfo=(Hashtable)userinfo.get("SYSCONF");
    int PagePcs =  100;
    if (sysinfo.size()>0) {
      String strPagePcs = sysinfo.get("MER_PAGE_CUL").toString();
      if (strPagePcs.length()>0) {
        PagePcs = Integer.parseInt(strPagePcs);
      }
    }

    ArrayList arrayCaptureSucessData = new ArrayList();
    ArrayList arrayCaptureFailData = new ArrayList();
    String Success_SysOrderID = "";
    String Fail_SysOrderID = "";
    ArrayList arrayList = new ArrayList();
    String PartialFlag = "N";
    String CheckFlag = "Y";
    String BatchPmtID = "";
    if (session.getAttribute("CaptureUpdateData") != null) {
      Hashtable hashCaptureData = (Hashtable)session.getAttribute("CaptureUpdateData");
      if (hashCaptureData==null) hashCaptureData = new Hashtable();
      if (hashCaptureData.size()>0) {
         arrayCaptureSucessData = (ArrayList)hashCaptureData.get("SUCCESS");
         arrayCaptureFailData = (ArrayList)hashCaptureData.get("FAIL");
          BatchPmtID = hashCaptureData.get("BATCHPMTID").toString();
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
  function toPrint(PrintType) {
       strFeatures = "top=" + (screen.height-670)/2 + ",left=150,width=1,height=1,toolbar=0,menubar=0,location=0,directories=0,status=0,scrollbars=yes";
       var url = "<%=request.getContextPath()%>/MerchantCaptureCtl?Action=Print&<%=MerchantMenuCtl.MENU_FORWARD_KEY%>=<%=MenuKey%>&BatchPmtID="+document.all.form.BatchPmtID.value+"&PrintType="+PrintType;
       subWin=window.open(url,"main",strFeatures);
  }

  function closeSub(){
    if (subWin != null && subWin.open) subWin.close(); subWin=null;
  }

  function toCapture() { // �^�дڭ�
     document.form.btnCapture.disabled = true;
     document.form.submit();
  }

  function toBack() { // �^����
     document.form.action = "Merchant_Main.jsp";
     document.form.btnCapture.disabled = true;
     document.form.submit();
  }

//-->
</script>

</head>

<body>
<form id="form" name="form" method="post" action ="./MerchantCaptureCtl" >
<input type="hidden" name="Action" id="Action" value="">
<input type="hidden" name="BatchPmtID" id="BatchPmtID" value="<%=BatchPmtID%>">
 <%
 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
      <td align="left" valign="middle"><b><font color="#004E87" size="3">�u�W����дڧ@�~</font></b></td>
  </tr>
</table>
<hr style="height:1px">
  <%if (arrayCaptureSucessData.size()>0) { %>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr>
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="30" align="left"><%if (arrayCaptureSucessData.size()>0) {%><input type='button' value='�HPDF�榡�ץX' name='btnPDF' id='btnPDF' onclick="toPrint('PDF');"><input type='button' value='�HCSV�榡�ץX' name='btnCSV' id='btnCSV' onclick="toPrint('CSV');"><input type='button' value='�HTXT�榡�ץX' name='btnTXT' id='btnTXT' onclick="toPrint('TXT');"><input type='button' value='�^�дڭ�' name='btnCapture' id='btnCapture' onclick="toCapture();"><input type='button' value='�^����' name='btnBack' id='btnBack' onclick="toBack();"><%}%></td>
            <td align="right">�дڦ��\�`���ơG<%=arrayCaptureSucessData.size()%>��</td>
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
      <td align='center' colspan="1">�дڤ��</td>
      <td align='center' colspan="1">�дڪ��B</td>
    </tr>

    <tr align='center' bgcolor='#F4F4FF' height="25">
      <td align='center'>�t�Ϋ��w�渹</td>
      <td align='center'>����Ҧ�</td>
      <td align='center'>���v�X</td>
      <td align='center'>�дڴ���</td>
      <td align='center'>�дڪ��A</td>
    </tr>

    <%
    String SumCaptureAmt ="0";
    String SumRefundAmt ="0";
    String NetCaptureAmt ="0";
    int count=0;
    for (int c=0; c<arrayCaptureSucessData.size(); ++c) {
      count++;
      Hashtable hashData = (Hashtable)arrayCaptureSucessData.get(c);
      String TransCode = hashData.get("TRANSCODE").toString();
      String FontColor = "";
      if (TransCode.equalsIgnoreCase("01")) {  // �h�f
        FontColor = "#FF0000";
      } %>

      <tr align='center' bgcolor='#ffffff'  height="25">
       <td align='left'   rowspan="2"><font color="<%=FontColor%>"><%=count%></font></td>
       <td align='left'   rowspan="2"><font color="<%=FontColor%>"><%=hashData.get("TERMINALID").toString()%></font></td>
       <td align='left'   colspan="1"><font color="<%=FontColor%>"><%=hashData.get("ORDERID").toString()%></font></td>
       <td align='left'   colspan="1"><font color="<%=FontColor%>"><%
         String TransDesc = "";
         if (TransCode.equalsIgnoreCase("00")) TransDesc = "�ʳf";//���
         if (TransCode.equalsIgnoreCase("01")) TransDesc = "�h�f";//���
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
      <td align='center' colspan="1"><font color="<%=FontColor%>"><%
      String Date = hashData.get("CAPTUREDATE").toString().replaceAll("-","/").substring(0,10) +"<br>"+ hashData.get("CAPTUREDATE").toString().replaceAll("-","/").substring(11,19);
      out.write(Date);
      %></font></td>
      <td align='right' colspan="1"><font color="<%=FontColor%>"><%
        String CaptureAmt = hashData.get("CAPTUREAMT").toString();
        if (CaptureAmt.length()>0) {
          String TempTransCode = hashData.get("TRANSCODE").toString();
          if (TempTransCode.equalsIgnoreCase("00")) {
             SumCaptureAmt = String.valueOf(Double.parseDouble(SumCaptureAmt)+Double.parseDouble(CaptureAmt));
          }
          if (TempTransCode.equalsIgnoreCase("01")) {
             SumRefundAmt = String.valueOf(Double.parseDouble(SumRefundAmt)+Double.parseDouble(CaptureAmt));
          }
          out.write(nf.format(Double.parseDouble(CaptureAmt)));
        }
      %></font></td>

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
      <td align='center' ><font color="<%=FontColor%>"><%=hashData.get("DUE_DATE").toString()%></font></td>
      <td align='center' ><font color="<%=FontColor%>">�ݳB�z</font></td>
      </tr>
   <%} %>
   <tr align='center' bgcolor='#ffffff'  height="25">
       <td align='left'   colspan="2"><font color="">�����p�p</font></td>
       <td align='left'   colspan="9"><font color=""></font></td>
   </tr>
   <tr align='center' bgcolor='#ffffff'  height="25">
       <td align='left'   colspan="2"><font color="">�w�дڪ��B</font></td>
       <td align='left'   colspan="9"><font color=""><%=nf.format(Double.parseDouble(SumCaptureAmt))%></font></td>
   </tr>
   <tr align='center' bgcolor='#ffffff'  height="25">
       <td align='left'   colspan="2"><font color="">�w�h�f���B</font></td>
       <td align='left'   colspan="9"><font color=""><%=nf.format(Double.parseDouble(SumRefundAmt))%></font></td>
   </tr>
   <tr align='center' bgcolor='#ffffff'  height="25">
       <td align='left'   colspan="2"><font color="">�дڲb�B</font></td>
       <td align='left'   colspan="9"><font color=""><%=nf.format(Double.parseDouble(SumCaptureAmt)-Double.parseDouble(SumRefundAmt))%></font></td>
   </tr>
  </table>

<%}%>
  <%if (arrayCaptureFailData.size()>0) { %>
  <br>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr>
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="30" align="left"><%if (arrayCaptureSucessData.size()==0) { %><input type='button' value='�HPDF�榡�ץX' name='btnPDF' id='btnPDF' onclick="toPrint('PDF');"><input type='button' value='�HCSV�榡�ץX' name='btnCSV' id='btnCSV' onclick="toPrint('CSV');"><input type='button' value='�HTXT�榡�ץX' name='btnTXT' id='btnTXT' onclick="toPrint('TXT');"><input type='button' value='�^�дڭ�' name='btnCapture' id='btnCapture' onclick="toCapture();"><input type='button' value='�^����' name='btnBack' id='btnBack' onclick="toBack();"><%}%></td>
            <td align="right">�дڥ����`���ơG<%=arrayCaptureFailData.size()%>��</td>
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
      <td align='center' colspan="1">�дڤ��</td>
      <td align='center' colspan="1">�дڪ��B</td>

    </tr>

    <tr align='center' bgcolor='#F4F4FF' height="25">
      <td align='center'>�t�Ϋ��w�渹</td>
      <td align='center'>����Ҧ�</td>
      <td align='center'>���v�X</td>
      <td align='center'>�дڴ���</td>
      <td align='center'>�дڪ��A</td>
    </tr>
    <%
    int count=0;
    for (int c=0; c<arrayCaptureFailData.size(); ++c) {
      count++;
      Hashtable hashData = (Hashtable)arrayCaptureFailData.get(c);
      String TransCode = hashData.get("TRANSCODE").toString();
      String FontColor = "";
      if (TransCode.equalsIgnoreCase("01")) {  // �h�f
        FontColor = "#FF0000";
      } %>

      <tr align='center' bgcolor='#ffffff'  height="25">
       <td align='left'   rowspan="2"><font color="<%=FontColor%>"><%=count%></font></td>
       <td align='left'   rowspan="2"><font color="<%=FontColor%>"><%=hashData.get("TERMINALID").toString()%></font></td>
       <td align='left'   colspan="1"><font color="<%=FontColor%>"><%=hashData.get("ORDERID").toString()%></font></td>
       <td align='left'   colspan="1"><font color="<%=FontColor%>"><%
         String TransDesc = "";
         if (TransCode.equalsIgnoreCase("00")) TransDesc = "�ʳf";//���
         if (TransCode.equalsIgnoreCase("01")) TransDesc = "�h�f";//���
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
      <td align='center' colspan="1"><font color="<%=FontColor%>"><%=hashData.get("CAPTUREDATE").toString()%></font></td>
      <td align='right' colspan="1"><font color="<%=FontColor%>"><%
        String CaptureAmt = hashData.get("CAPTUREAMT").toString();
        if (CaptureAmt.length()>0) {
          out.write(nf.format(Double.parseDouble(CaptureAmt)));
        }
      %></font></td>
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
      <td align='center' ><font color="<%=FontColor%>"><%=hashData.get("DUE_DATE").toString()%></font></td>
      <td align='center' ><font color="<%=FontColor%>"><%=hashData.get("BATCHRESPONSE").toString()%></font></td>
      </tr>
   <%} %>
  </table>
<%}%>
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
