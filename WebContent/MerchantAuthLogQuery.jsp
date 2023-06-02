<%@page contentType="text/html; charset=Big5"%>
<%@ page language="java" %>
<%@ page import="java.util.*"%>
<%@ page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%@ page import="com.cybersoft.bean.UserBean" %>
<%
//  response.addHeader("Pragma", "No-cache");
//  response.addHeader("Cache-Control", "no-cache");
//  response.addDateHeader("Expires", 1);

  try {
  UserBean mlb=new UserBean();
  String Today = mlb.get_TransDate("yyyy/MM/dd");

Hashtable userinfo=(Hashtable)session.getAttribute("SYSCONFDATA");
//System.out.println(userinfo);
Hashtable merchantuserinfo = (Hashtable)userinfo.get("MERCHANT_USER");
String merchantid=String.valueOf(merchantuserinfo.get("MERCHANT_ID"));
ArrayList terminalinfo=(ArrayList)userinfo.get("TERMINAL");
//�l�S���M��
ArrayList subMidList = new ArrayList();
//�O�_���l�S��
boolean isSubMid =false;
//�O�_����@�S��
boolean isSignMer = false;
String subMidName="";
String subMid="";
subMidName=merchantuserinfo.get("MERCHANTCALLNAME").toString();
 subMid=merchantuserinfo.get("SUBMID").toString();
 isSubMid =((String) merchantuserinfo.get("ISSUBMERCHANT")).equals("Y") ?  true : false;

 subMidList = (ArrayList) userinfo.get("SUBMID");
 isSignMer = subMidList != null && subMidList.size() > 1 ? false :  true;

//���o�d�ߤ������
Hashtable conf = (Hashtable)userinfo.get("SYSCONF");
int queryMonth = Integer.parseInt(conf.get("MER_LIMIT_QRY_MONTH").toString());
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

function submitform() {
  if(checkdata()){
     document.form.submit();
  }
}
function checkdata(){
	 var nowdate  = new Date();
	 nowdate.setMonth(nowdate.getMonth()-<%=queryMonth%>);
	 var limitDate =cal_gen_date2(nowdate);
  if (document.all.form.Start_TransDate.value.length == 0 && document.all.form.End_TransDate.value.length == 0 ) {
    alert("�аO�o��J����d�߰_�l�ε������");
    return;
  }
   if (document.all.form.Start_TransDate.value.length == 0 && document.all.form.End_TransDate.value.length > 0) {
     alert("�аO�o��J����_�l���");
     return;
   }
   if (document.all.form.End_TransDate.value.length == 0&& document.all.form.Start_TransDate.value.length > 0) {
     alert("�аO�o��J����������");
     return;
   }
   if(document.all.form.End_TransDate.value.length > 0 && document.all.form.Start_TransDate.value.length > 0){
     if (!check_date_format(document.all.form.Start_TransDate.value) || !Check_Input_Date(document.all.form.Start_TransDate.value)) {
       alert("����_�l����榡���~");
       return;
     }
     if (!check_date_format(document.all.form.End_TransDate.value) || !Check_Input_Date(document.all.form.End_TransDate.value) ) {
       alert("�����������榡���~");
       return;
     }
   }
   if (!Check_Date( document.all.form.Start_TransDate.value, document.all.form.End_TransDate.value )) {
     alert("����_�Ҥ�����i�j�󵲧����");
     return;
   }
   if (Check_Date( document.all.form.Start_TransDate.value, limitDate )) {
	      alert("�d�߰_��d�򤣥i�W�L���_�^��<%=queryMonth%>�Ӥ�!!");
	      return;
	 }
   return true;
}

function selectchange(){
  var selectvalue=document.all.form.OrderType.value;
  if(selectvalue=='M'){
    document.all.form.OrderID.maxLength = 25;
    document.all.form.OrderID.value="";
  } else {
    document.all.form.OrderID.maxLength = 40;
    document.all.form.OrderID.value="";
  }
}

function clearall(){
  document.all.form.OrderType.value='M';
  document.all.form.OrderID.maxLength = 25;
  document.all.form.OrderID.value="";
  document.all.form.Start_TransDate.value="<%=Today%>";
  document.all.form.Start_TransHour.value="00";
  document.all.form.End_TransDate.value="<%=Today%>";
  document.all.form.End_TransHour.value="23";
  document.all.form.TransCode.value="ALL";
  document.all.form.TransStatus.value="ALL";
  document.all.form.AuthID.value="";
  document.all.form.TerminalID.value="ALL";
  document.all.form.CaptrueType.value="ALL";
}

//-->
</script>
<body bgcolor="#ffffff">
<form method="post" name="form" id="form" action="./MerchantAuthLogCtl">
 <%
String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);
 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
   <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
   <td align="left" valign="middle"><b><font color="#004E87" size="3">���v����d�߱���</font></b></td>
  </tr>
 </table>

 <hr style="height:1px">

 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
  <tr align='center' height="25">
   <td width='25%' bgcolor='#F4F4FF' align='right'>�S���ө��N��(*)</td>
   <td width='75%' bgcolor='#ffffff' align='left' ><%=merchantid %></td>
  </tr>
   <% if(!isSignMer && !isSubMid){ %>
   <tr align='center' height="25">
      <td width="25%" bgcolor='#F4F4FF'   align='right'>�l�S���ө��N��</td>
      <td width='75%' bgcolor='#ffffff' align='left' >
      <select id="subMid" name="subMid">
        	<option value="all">����</option>
        	<% for(int i =0 ;  i< subMidList.size() ; i++) {
        				Hashtable content = (Hashtable) subMidList.get(i);
        				String name = content.get("MERCHANTCALLNAME").toString();
        				String value = content.get("SUBMID").toString();
        	%>
        		<option value="<%=value %>"><%=value+"  "+name %></option>
        	<%} %>
        </select>

      </td>
    </tr>
    <%} %>
   <% if(isSubMid){ %>
   <tr align='center' height="25">
      <td width="25%" bgcolor='#F4F4FF'   align='right'>�l�S���ө��N��</td>
      <td width='75%' bgcolor='#ffffff' align='left' >
      <%=subMid+"("+subMidName+")" %>
      <input type="hidden"   id="subMid" name="subMid"  value="<%=subMid%>">
      </td>
    </tr>
    <%}  if(isSignMer ){%>   
    <input type="hidden"   id="subMid" name="subMid"  value="<%=subMid%>">
    <%} %>
  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>������(*)</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
    <input type="Text" name="Start_TransDate" id="Start_TransDate" value="<%=Today%>" size="20">
    <img src="images/calendar/cal.gif" style="cursor:hand" border="0" alt="�����J" onclick="javascript:datepopup(document.all.Start_TransDate)">

    <select name='Start_TransHour' id='Start_TransHour'>
     <option value='00'>00</option>
     <option value='01'>01</option>
     <option value='02'>02</option>
     <option value='03'>03</option>
     <option value='04'>04</option>
     <option value='05'>05</option>
     <option value='06'>06</option>
     <option value='07'>07</option>
     <option value='08'>08</option>
     <option value='09'>09</option>
     <option value='10'>10</option>
     <option value='11'>11</option>
     <option value='12'>12</option>
     <option value='13'>13</option>
     <option value='14'>14</option>
     <option value='15'>15</option>
     <option value='16'>16</option>
     <option value='17'>17</option>
     <option value='18'>18</option>
     <option value='19'>19</option>
     <option value='20'>20</option>
     <option value='21'>21</option>
     <option value='22'>22</option>
     <option value='23'>23</option>
    </select> ��

    ~
    <input type="Text" name="End_TransDate" id="End_TransDate" value="<%=Today%>" size="20">
    <img src="images/calendar/cal.gif" style="cursor:hand" border="0" alt="�����J" onclick="javascript:datepopup(document.all.End_TransDate)">

    <select name='End_TransHour' id='End_TransHour'>
     <option value='00'>00</option>
     <option value='01'>01</option>
     <option value='02'>02</option>
     <option value='03'>03</option>
     <option value='04'>04</option>
     <option value='05'>05</option>
     <option value='06'>06</option>
     <option value='07'>07</option>
     <option value='08'>08</option>
     <option value='09'>09</option>
     <option value='10'>10</option>
     <option value='11'>11</option>
     <option value='12'>12</option>
     <option value='13'>13</option>
     <option value='14'>14</option>
     <option value='15'>15</option>
     <option value='16'>16</option>
     <option value='17'>17</option>
     <option value='18'>18</option>
     <option value='19'>19</option>
     <option value='20'>20</option>
     <option value='21'>21</option>
     <option value='22'>22</option>
     <option value='23' selected>23</option>
    </select> ��

   </td>
  </tr>

  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>������O</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><select name='TransCode' id='TransCode'><option value='ALL'>����</option><option value='00'>00-�ʳf���</option><option value='01'>01-�h�f���</option><option value='10'>10-�ʳf�������</option><option value='11'>11-�h�f�������</option><option value='81'>81-�d�����ҥ��(M�BV Only)</option></select></td>
  </tr>
  
  <!-- Jimmy Kang 20150721 �d�߱��� �s�W  TransType �U�Կ��  -- �s�W�}�l -- -->
  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>�d�O</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><select name='TransType' id='TransType'><option value='ALL'>����</option><option value='CUP'>CUP</option><option value='VMJ'>VMJ</option><option value='SSL'>�H�Υd</option></select></td>
  </tr>
  <!-- Jimmy Kang 20150721 �d�߱��� �s�W  TransType �U�Կ��  -- �s�W���� -- -->

  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>���v���G</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><select name='TransStatus' id='TransStatus'><option value='ALL'>����</option><!--<option value='R'>R-Request</option>--><option value='A'>A-Approved</option><option value='D'>D-Declined</option><option value='C'>C-Call Bank</option>
   <!-- Jimmy Kang 20150721 �d�߱���ﶵ �s�W "P-Pending" -- �s�W�}�l -- -->
   <option value='P'>P-Pending</option>
   <!-- Jimmy Kang 20150721 �d�߱���ﶵ �s�W "P-Pending" -- �s�W���� -- -->
   </select></td>
  </tr>

  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>���w�渹</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
    <select name='OrderType' id='OrderType'  onchange="selectchange();"><option value='M'>�S�����w�渹</option><option value='S'>�t�Ϋ��w�渹</option></select>
    <input type='text' id='OrderID' name='OrderID' size='50' maxlength='25' ></td>
  </tr>

  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>���v�X</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><input type='text' id='AuthID' name='AuthID' size='10' maxlength='8'></td>
  </tr>

  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>�ݥ����N�X</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><select name='TerminalID' id='TerminalID'>
     <option value='ALL'>����</option>
     <%for(int i=0;terminalinfo!=null&&i<terminalinfo.size();i++){
       String terminalid=String.valueOf(((Hashtable)terminalinfo.get(i)).get("TERMINALID"));
       %>
     <option value='<%=terminalid%>'><%=terminalid%></option>
     <%} %></select>
   </td>
  </tr>

  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>�дڪ��p</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><select name='CaptrueType' id='CaptrueType'><option value='ALL'>����</option><option value='Capture'>�w�д�</option><option value='NotCapture'>���д�</option></select></td>
  </tr>

  <tr align='center' height="30">
   <td align='center' bgcolor='#F4F4FF' colspan="2">
    <input type='button' value='�d��' name='btnQuery' id='btnQuery' onclick='submitform()' >
    <input type='button' value='�M���e��' name='btnClear' id='btnClear' onclick='clearall()' >
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
