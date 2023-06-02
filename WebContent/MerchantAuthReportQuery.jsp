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
System.out.println(userinfo);
Hashtable merchantuserinfo=(Hashtable)userinfo.get("MERCHANT_USER");
String merchantid=String.valueOf(merchantuserinfo.get("MERCHANT_ID"));
ArrayList terminalinfo=(ArrayList)userinfo.get("TERMINAL");

String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);


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
   if (document.all.form.End_TransDate.value.length == 0 && document.all.form.Start_TransDate.value.length > 0) {
     alert("�аO�o��J����������");
     return;
   }
   if(document.all.form.End_TransDate.value.length > 0 && document.all.form.Start_TransDate.value.length > 0 ){
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

function cleardata(){
    document.form.Action.value='';
    document.form.submit();

}

function toPrint(PrintType) {
 if(checkdata()) {
   strFeatures = "top=" + (screen.height-670)/2 + ",left=150,width=1,height=1,toolbar=0,menubar=0,location=0,directories=0,status=0,scrollbars=yes";
   
   // ���v����έp���� �}�ҦC�L�����}�[�J�ǰe TransType���� by Jimmy Kang 20150730
   var url = "<%=request.getContextPath()%>/MerchantAuthReportCtl?Action=Query&<%=MerchantMenuCtl.MENU_FORWARD_KEY%>=<%=MenuKey%>&Start_TransDate="+document.all.form.Start_TransDate.value+"&Start_TransHour="+document.all.form.Start_TransHour.value+"&End_TransDate="+document.all.form.End_TransDate.value+"&End_TransHour="+document.all.form.End_TransHour.value+"&TransCode="+document.all.form.TransCode.value+"&RrportItem="+document.all.form.RrportItem.value +"&PrintType="+PrintType+"&subMid="+document.all.form.subMid.value+"&TransType="+document.all.form.TransType.value ;
   subWin=window.open(url,"main",strFeatures);
 }
}

function closeSub(){
  if (subWin != null && subWin.open) subWin.close(); subWin=null;
}

//-->
</script>
<body bgcolor="#ffffff">
<form method="post" name="form" id="form" action="./MerchantAuthReportCtl">
<input type="hidden" name="Action" id="Action" value="">
 <%
 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
   <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
   <td align="left" valign="middle"><b><font color="#004E87" size="3">���v����έp����</font></b></td>
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
    <%}  else if(isSubMid){%>
   
   <tr align='center' height="25">
      <td width="25%" bgcolor='#F4F4FF'   align='right'>�l�S���ө��N��</td>
      <td width='75%' bgcolor='#ffffff' align='left' >
      <%=subMid+"("+subMidName+")" %>
      <input type="hidden"   id="subMid" name="subMid"  value="<%=subMid%>">
      </td>
    </tr>
    <%} else {%>   
    <input type="hidden"   id="subMid" name="subMid"  value="<%=subMid%>">
    <%} %>
  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>������(�϶�)(*)</td>
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
   <td width='75%' align='left'  bgcolor='#FFFFFF'><select name='TransCode' id='TransCode'><option value='ALL'>����</option><option value='CANCEL'>�������</option></select></td>
  </tr>
  
  <!-- ���v����έp���� Jimmy Kang 20150730 �d�߱��� �s�W  TransType �U�Կ��  -- �s�W�}�l -- -->
  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>�d�O</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><select name='TransType' id='TransType'><option value='ALL'>����</option><option value='CUP'>CUP</option><option value='VMJ'>VMJ</option><option value='SSL'>�H�Υd</option></select></td>
  </tr>
  <!-- ���v����έp���� Jimmy Kang 20150730 �d�߱��� �s�W  TransType �U�Կ��  -- �s�W���� -- -->
  
  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>��������</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><select name='RrportItem' id='RrportItem'><option value='ByDate'>By���</option><option value='ByTerminal'>By�ݥ���</option></select></td>
  </tr>

  <tr align='center' height="30">
   <td align='center' bgcolor='#F4F4FF' colspan="2">
    <input type='button' value='�HPDF�榡�ץX' name='btnPDF' id='btnPDF' onclick="toPrint('PDF')" >
<!--    <input type='button' value='�HTXT�榡�ץX' name='btnTXT' id='btnTXT' onclick="toPrint('TXT')" >  -->
    <input type='button' value='�M���e��' name='btnClear' id='btnClear' onclick='cleardata()' >
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
