<%@page contentType="text/html;charset=BIG5"%>
<%@page import="java.util.*" %>
<%@page import="java.text.*"%>
<%@page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%
//************************************************************
// * <p>#File Name:   Merchant_RefundVoid_List.jsp        </p>
// * <p>#Description:                  </p>
// * <p>#Create Date: 2020/6/24              </p>
// * <p>#Company:     cybersoft               </p>
// * <p>#Notice:                      </p>
// * @author      
// * @since       SPEC version
// * @version 
// * 2020/07/02 �ۥ� 202007070141-00 �ץ�chrome�s�����L�k�h�f���������p
// ************************************************************/
  try {
//    response.addHeader("Pragma", "No-cache");
//    response.addHeader("Cache-Control", "no-cache");
//    response.addDateHeader("Expires", 1);

    NumberFormat nf = NumberFormat.getInstance();
    nf.setMaximumFractionDigits(2);
    nf.setMinimumFractionDigits(2);

    String Message = "�L������";
    Hashtable hashVoidData = new Hashtable();
    ArrayList arrayList = new ArrayList();
    String VoidFlag = "false";
    String CancelAmt = "0";
    if (session.getAttribute("VoidData") != null) {
      hashVoidData = (Hashtable)session.getAttribute("VoidData");
      if (hashVoidData==null) hashVoidData = new Hashtable();
      if (hashVoidData.size()>0) {
        arrayList = (ArrayList)hashVoidData.get("DATALIST");
        if (arrayList==null) arrayList = new ArrayList();
        VoidFlag = hashVoidData.get("FLAG").toString();
        if (VoidFlag.equalsIgnoreCase("true")) {  // �i���
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
//   <!--
  var Sys_OrderID = "";
  var count = 0;
  function toSubmit() {
     //objs=document.all.tags("input");
     objs = document.getElementsByTagName("input");
     for(var i=0 ; i<objs.length ; ++i){
        if(objs[i].type=="checkbox"){
           if(objs[i].checked==true){
               if(count==0){
                  Sys_OrderID = Sys_OrderID + objs[i].value ;
               } else{
                  Sys_OrderID = Sys_OrderID + "," + objs[i].value ;
               }
               count++;
           }
        }
     }
     if (Sys_OrderID.length == 0) {
        alert("������h�f�������");
        return void(0);
     } else {
        document.form.InputSys_OrderID.value = Sys_OrderID;
        document.form.btnSubmit.disabled = true;
        document.form.submit();
     }
  }


//-->
</script>

</head>

<body>
<form id="form" name="form" method="post" action ="./MerchantVoidCtl" >
<input type="hidden" name="Action" id="Action" value="Void">
<input type="hidden" name="InputSys_OrderID" id="InputSys_OrderID" value="">
<input type="hidden" name="token" value="">
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
            <td height="30" align="left"><!--<%
            System.out.println("VoidFlag="+VoidFlag);
            if (VoidFlag.equalsIgnoreCase("true") ){
              %><input type='button' value='�T�w����' name='btnSubmit' id='btnSubmit' onclick="toSubmit();"><%
            }%>  -->
            </td>
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
      <td align='center' rowspan="2">�дڪ��B</td>
    </tr>

    <tr align='center' bgcolor='#F4F4FF' height="25">
      <td align='center'>�t�Ϋ��w�渹</td>
      <td align='center'>����Ҧ�</td>
      <td align='center'>���v�X</td>
    </tr>
    <%
    int count=0;
    for (int c=0; c<arrayList.size(); ++c) {
      count++;
      Hashtable hashData = (Hashtable)arrayList.get(c);
      String TransCode = hashData.get("TRANSCODE").toString();
      String FontColor = "";
      String BalanceAmt = hashData.get("BALANCEAMT").toString();  // Billing.balanceamt
      
      // Merchant Console �u�W�����@�~�Ҳ�  �ק�  by Jimmy Kang 20150511  -- �ק�}�l --
      String CardType = hashData.get("CARD_TYPE").toString();  // Billing.CARD_TYPE
      // Merchant Console �u�W�����@�~�Ҳ�  �ק�  by Jimmy Kang 20150511  -- �קﵲ�� --
      
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
  <% //�U�誺if�P�_���󤤥[�J�P�_�d�O(CARD_TYPE)�O�_�� C - CUP, �Y����C - CUP�~�i�H�q�Xcheckbox 
     //Merchant Console �u�W�����@�~�Ҳ�  �ק�  by Jimmy Kang 20150511 %>
  <td align='left'   rowspan="2"><%if (TransCode.equalsIgnoreCase("01") && Double.parseDouble(BalanceAmt) > 0 && !CardType.equalsIgnoreCase("C") ) { %><input type='checkbox' id='Checkbox"<%=String.valueOf(count)%>"' value='<%=hashData.get("SYS_ORDERID").toString()%>'> <%}%></td>
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

  <br>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
    <tr bgcolor='#ffffff' height="25">
      <td align='left' width='15%'>�p�p</td>
      <td align='left' width='85%'></td>
    </tr>

    <tr bgcolor='#ffffff' height="25">
      <td align='left' width='15%'>�i�����h�f���B</td>
      <td align='left' width='85%'><%
      if (CancelAmt.length()>0) {
        out.write(nf.format(Double.parseDouble(CancelAmt)));
      }%></td>
      </tr>
  </table>
  <%} %>
  <%if (Message.length()>0) { %>
  <table width="100%">
    <tr><td><b><font color="#FF0000" size="3">�`�N�G<%=Message%></font></b></td></tr>
  </table>
  <%}%>
<%if (VoidFlag.equalsIgnoreCase("true") ){%>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr>
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="30" align="left"><input type='button' value='�T�w����' name='btnSubmit' id='btnSubmit' onclick="toSubmit();"></td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
<%}%>
</form>
</body>

</html>
<%
        } catch (Exception e) {
            request.setAttribute("errMsg",e.toString());
            request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
        }
%>