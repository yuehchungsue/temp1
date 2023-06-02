<%@page contentType="text/html;charset=BIG5"%>
<%@page import="java.util.*" %>
<%@page import="java.text.*"%>
<%@page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%
  try {
  response.addHeader("Pragma", "No-cache");
  response.addHeader("Cache-Control", "no-cache");
  response.setDateHeader("Expires", 0);


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

    ArrayList arrayTerminal = (ArrayList)userinfo.get("TERMINAL");
    if (arrayTerminal == null) arrayTerminal = new ArrayList();

    Hashtable hashCaptureData = new Hashtable();
    ArrayList arrayList = new ArrayList();
    String PartialFlag = "N";
    String CheckFlag = "Y";
    int nowPage = 0;
    if (session.getAttribute("CaptureData") != null) {
      hashCaptureData = (Hashtable)session.getAttribute("CaptureData");
      if (hashCaptureData==null) hashCaptureData = new Hashtable();
      if (hashCaptureData.size()>0) {
        arrayList = (ArrayList)hashCaptureData.get("DATALIST");
        if (arrayList==null) arrayList = new ArrayList();
        PartialFlag = (String)hashCaptureData.get("PARTIALFLAG");
        CheckFlag = (String)hashCaptureData.get("CHECKFLAG");
        String page_no = (String)hashCaptureData.get("NOWPAGE");
        if (page_no.length()>0) nowPage = Integer.parseInt(page_no);
      }
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
  var Sys_OrderID = "";
  var InputCaptureAmt = "";
  var count = 0;
  function toSubmit() {
     var InputTextName = "";
     objs=document.getElementsByTagName("input");
     //objs=document.all.tags("input");
     for(var i=0 ; i<objs.length ; ++i){
        if(objs[i].type=="checkbox"){
           if(objs[i].checked==true){
               InputTextName = objs[i].id;
               InputTextName = InputTextName.substring(8);
               var TextName = "document.form.InputCapture"+InputTextName;
               var TextNameValue = eval(TextName).value;
               if (TextNameValue.length==0) {
                  alert("�п�J�дڪ��B");
                  return void(0);
               }
               if(!check_numerical(TextNameValue)){
                  alert("�дڪ��B�������Ʀr");
                  return void(0);
              }
              if(count==0){
                  Sys_OrderID = Sys_OrderID + objs[i].value ;
                  InputCaptureAmt = InputCaptureAmt + TextNameValue;
              } else{
                  Sys_OrderID = Sys_OrderID + "," + objs[i].value ;
                  InputCaptureAmt = InputCaptureAmt + "," + TextNameValue;
               }
               count++;
           }
        }
     }
     if (Sys_OrderID.length == 0) {
        alert("������дڸ��");
        return void(0);
     } else {
        document.form.InputSysOrderID.value = Sys_OrderID;
        document.form.InputCaptureAmt.value = InputCaptureAmt;
        document.form.btnSubmit.disabled = true;
        document.form.submit();
     }
  }

  function isChecked(objEle, TransCode){
    if(objEle.checked==false) {
      if (TransCode == '01') {  // �h�f
          objEle.checked = true;
          alert("�h�f����j��д�");
          return void(0);
      }
    }
  }
  function realsubmit(){
    document.form.Action.value = 'Query';
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
<form id="form" name="form" method="post" action ="./MerchantCaptureCtl" >
<input type="hidden" name="Action" id="Action" value="Check">
<input type="hidden" name="InputSysOrderID" id="InputSysOrderID" value="">
<input type="hidden" name="InputCaptureAmt" id="InputCaptureAmt" value="">
 <%
 String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);
 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
      <td align="left" valign="middle"><b><font color="#004E87" size="3">�u�W����дڧ@�~</font></b></td>
  </tr>
</table>

<hr style="height:1px">
  <%if (arrayList.size()>0) { %>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr>
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="30" align="left"><!--<input type='button' value='�T�w' name='btnSubmit' id='btnSubmit' onclick="toSubmit();">--></td>
            <td align="right">�`���ơG<%=arrayList.size()%>���@�@<select name="page_no" id="page_no" onchange="realsubmit();">
             <%  for (int i=0; i<TotalPage; i++){%>
             <option value='<%=i%>' <%if (nowPage==i) out.write("selected");%> >��<%=(i+1)%>��</option>
             <%  } %></select></td>
          </tr>

        </table>
     </td>
    </tr>
  </table>

  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
    <tr align='center' bgcolor="#F4F4FF" height="25">
      <td align='center' rowspan="2">�Ǹ�</td>
      <td align='center' rowspan="2">���</td>
      <td align='center' rowspan="2">�ݥ����N�X</td>
      <td align='center' colspan="1">�S�����w�渹</td>
      <td align='center' colspan="1">������O</td>
      <td align='center' rowspan="2">������</td>
      <td align='center' colspan="1">�^���X</td>
      <td align='center' rowspan="2">�妸���X</td>
      <td align='center' rowspan="2">������O</td>
      <td align='center' rowspan="2">������B</td>
      <td align='center' colspan="1">�дڴ���</td>
      <td align='center' rowspan="2">���дڪ��B</td>
    </tr>

    <tr align='center' bgcolor='#F4F4FF' height="25">
      <td align='center'>�t�Ϋ��w�渹</td>
      <td align='center'>����Ҧ�</td>
      <td align='center'>���v�X</td>
      <td align='center'>�i�дڪ��B</td>
    </tr>

    <%
    int count=0;
    int StartCnt = nowPage * PagePcs;
    for (int c=StartCnt; c<(StartCnt+PagePcs); ++c) {
      if (c >= arrayList.size()) break;
      count++;
      Hashtable hashData = (Hashtable)arrayList.get(c);
      String TransCode = hashData.get("TRANSCODE").toString();
      String FontColor = "";
      String TerminalPartialFlag = "N";
      String TransMode = hashData.get("TRANSMODE").toString();
      String BalanceAmt = hashData.get("BALANCEAMT").toString();  // Billing.balanceamt
      if (TransCode.equalsIgnoreCase("01")) {  // �h�f
        FontColor = "#FF0000";
      } %>

      <tr align='center' bgcolor='#ffffff'  height="25">
       <td align='left'   rowspan="2"><font color="<%=FontColor%>"><%=count%></font></td>
       <td align='left'   rowspan="2"><input type='checkbox' id='Checkbox<%=String.valueOf(count)%>' value='<%=hashData.get("SYS_ORDERID").toString()%>' <%if (CheckFlag.equalsIgnoreCase("Y") || TransCode.equalsIgnoreCase("01")) out.write("checked");%> onclick='isChecked( this, "<%=TransCode%>" );' ></td>
       <td align='left'   rowspan="2"><font color="<%=FontColor%>"><%
       String Terminal = hashData.get("TERMINALID").toString();
       for (int j=0; j<arrayTerminal.size();++j) {
         Hashtable hasTerminal = (Hashtable)arrayTerminal.get(j);
         String GetTerminal = hasTerminal.get("TERMINALID").toString();
         if (GetTerminal.equalsIgnoreCase(Terminal)) {
           TerminalPartialFlag = hasTerminal.get("PERMIT_PARTIAL_CAPTURE").toString();
           break;
         }
       }
       out.write(Terminal);
       %></font></td>
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
       <td align='center' colspan="1"><font color="<%=FontColor%>"><%=hashData.get("CAPTUREDDEALLINE").toString()%></font></td>
      <td align='center' rowspan="2"><font color="<%=FontColor%>"><input type='text' id='InputCapture<%=String.valueOf(count)%>' name='InputCapture<%=String.valueOf(count)%>' size='8' maxlength='8' value='<%=BalanceAmt%>' <%
      String CheckTrans = hashData.get("TRANSCODE").toString();

      if (CheckTrans.equalsIgnoreCase("01") || PartialFlag.equalsIgnoreCase("N") || TerminalPartialFlag.equalsIgnoreCase("N") || !TransMode.equalsIgnoreCase("0")) out.write("readonly"); %>  ></font></td>
  </tr>
      <tr align='center' bgcolor='#ffffff' height="25">
      <td align='left'  ><font color="<%=FontColor%>"><%=hashData.get("SYS_ORDERID").toString()%></font></td>
      <td align='left'  ><font color="<%=FontColor%>"><%
         String TransModeDesc = "";
         if (TransMode.equalsIgnoreCase("0")) TransModeDesc = "�@��";//���
         if (TransMode.equalsIgnoreCase("1")) TransModeDesc = "����";//���
         if (TransMode.equalsIgnoreCase("2")) TransModeDesc = "���Q";//���
         if (TransModeDesc.length()>0) TransMode = TransMode + "-" + TransModeDesc;
         out.write(TransMode);
      %></font></td>
      <td align='center'><font color="<%=FontColor%>"><%=hashData.get("APPROVECODE").toString()%></font></td>
      <td align='right' ><font color="<%=FontColor%>"><%
        if (BalanceAmt.length()>0) {
          if (Double.parseDouble(BalanceAmt) >0) {
            out.write(nf.format(Double.parseDouble(BalanceAmt)));
          } else {
            out.write(nf.format(Double.parseDouble("0")));
          }
        }%></font></td>
      </tr>
   <%} %>
  </table>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr>
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="30" align="left"><input type='button' value='�T�w' name='btnSubmit' id='btnSubmit' onclick="toSubmit();"></td>
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
            <td height="30" align="left"><input type='button' value='�^�дڵe��' name='btnSubmit' id='btnSubmit' onclick="toBack();"></td>
          </tr>
          <tr>
            <td height="30" align="left" bgcolor='#ffffff'><font color="#FF0000" size="3"><b>�d�L�i�дڸ��<b></font></td>
          </tr>

        </table>
<!--     </td>  -->
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
