<%@page contentType="text/html; charset=Big5"%>
<%@ page language="java" %>
<%@ page import="java.util.*"%>
<%@ page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%@ page import="com.cybersoft.bean.UserBean" %>
<%
  response.addHeader("Pragma", "No-cache");
  response.addHeader("Cache-Control", "no-cache");
  response.setDateHeader("Expires", 1);

try {

  UserBean mlb=new UserBean();
  String Today = mlb.get_TransDate("yyyy/MM/dd");

Hashtable userinfo=(Hashtable)session.getAttribute("SYSCONFDATA");
System.out.println(userinfo);
Hashtable merchantinfo=(Hashtable)userinfo.get("MERCHANT_USER");
String merchantid=String.valueOf(merchantinfo.get("MERCHANT_ID"));
ArrayList terminalinfo=(ArrayList)userinfo.get("TERMINAL");
%>
<html>
<head>
 <meta http-equiv="Content-Type" content="text/html charset=big5">
<link href="css/style.css" type="text/css" rel="stylesheet">
</head>
<script language="JavaScript" src="js/Vatrix.js"></script>
<script language="JavaScript" src="js/calendar.js" ></script>
<script language="JavaScript" type="text/JavaScript">
<!--

function submitform(){
  if(checkdata()){
    document.form.submit();
  }
}

function checkdata(){

  if (document.all.form.StartTransDate.value.length == 0 && document.all.form.EndTransDate.value.length == 0 ) {
    alert("�аO�o��J����d�߰_�l�ε������");
    return;
  }
  if (document.all.form.StartTransDate.value.length == 0 && document.all.form.EndTransDate.value.length > 0) {
    alert("�аO�o��J����_�l���");
    return;
  }
  if (document.all.form.EndTransDate.value.length == 0 && document.all.form.StartTransDate.value.length > 0) {
    alert("�аO�o��J����������");
    return;
  }
  if(document.all.form.EndTransDate.value.length > 0 && document.all.form.StartTransDate.value.length > 0){
     if (!check_date_format(document.all.form.StartTransDate.value) || !Check_Input_Date(document.all.form.StartTransDate.value)) {
       alert("����_�l����榡���~");
       return;
     }
     if (!check_date_format(document.all.form.EndTransDate.value) || !Check_Input_Date(document.all.form.EndTransDate.value) ) {
       alert("�����������榡���~");
       return;
     }
  }
  if (!Check_Date( document.all.form.StartTransDate.value, document.all.form.EndTransDate.value )) {
       alert("����_�Ҥ�����i�j�󵲧����");
       return;
  }
  return true;
}

function selectchange(){
  var selectvalue=document.all.form.OrderType.value;
  if(selectvalue=='Order'){
    document.all.form.OrderID.maxLength = 25;
    document.all.form.OrderID.value="";
  } else {
    document.all.form.OrderID.maxLength = 40;
    document.all.form.OrderID.value="";
  }
}

function toClear() {
    document.form.Action.value='';
    document.form.submit();
}

//-->
</script>
<body bgcolor="#ffffff">
<form method="post" name="form" id="form" action="./MerchantTravelQryCtl">
<input type="hidden" name="Action" id="Action" value="FirstQuery">
 <%
 String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);
 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
   <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
   <td align="left" valign="middle"><b><font color="#004E87" size="3">��ȥd�w�ʫ�����d�߱���</font></b></td>
  </tr>
 </table>

 <hr style="height:1px">

 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
  <tr align='center' height="25">
   <td width='25%' bgcolor='#F4F4FF' align='right'>�S���ө��N��(*)</td>
   <td width='75%' bgcolor='#ffffff' align='left' ><%=merchantid %></td>
  </tr>

  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>������(*)</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
    <input type="Text" name="StartTransDate" id="StartTransDate" value="<%=Today%>" size="20">
    <img src="images/calendar/cal.gif" style="cursor:hand" border="0" alt="�����J" onclick="javascript:datepopup(document.all.StartTransDate)">&nbsp;

    ~
    <input type="Text" name="EndTransDate" id="EndTransDate" value="<%=Today%>" size="20">
    <img src="images/calendar/cal.gif" style="cursor:hand" border="0" alt="�����J" onclick="javascript:datepopup(document.all.EndTransDate)">&nbsp;

   </td>
  </tr>
  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>���w�渹</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
    <select name='OrderType' id='OrderType' onchange="selectchange();" ><option value='Order'>�S�����w�渹</option><option value='Sys_Order'>�t�Ϋ��w�渹</option></select>
    <input type='text' id='OrderID' name='OrderID' size='50' maxlength='25' ></td>
  </tr>
  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>���v�X</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><input type='text' id='ApproveCode' name='ApproveCode' size='10' maxlength='8'></td>
  </tr>
  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>�ݥ����N�X</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><select name='TerminalID' id='TerminalID'>
     <option value='ALL'>����</option>
     <%for(int i=0;terminalinfo!=null&&i<terminalinfo.size();i++){
       String terminalid=String.valueOf(((Hashtable)terminalinfo.get(i)).get("TERMINALID"));
       %>
     <option value='<%=terminalid%>'><%=terminalid%></option>
     <%} %>
    </select>
   </td>
  </tr>

  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>�B�z���A</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><select name='Status' id='Status'>
     <option value='ALL'>����</option>
     <option value='00'>00-�ݰeNCCC</option>
     <option value='01'>01-�w�eNCCC�A�ݦ^��</option>
     <option value='10'>10-NCCC�ˮ֦��\</option>
     <option value='11'>11-NCCC��h</option>
     <option value='20'>20-�w�R���A���W��NCCC</option>
    </select>
   </td>
  </tr>

  <tr align='center' height="30">
   <td align='center' bgcolor='#F4F4FF' colspan="2">
    <input type='button' value='�d��' name='btnQuery' id='btnQuery' onclick='submitform()' >
    <input type='button' value='�M���e��' name='btnClear' id='btnClear' onclick='toClear();' >
   </td>
  </tr>

 </table>
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