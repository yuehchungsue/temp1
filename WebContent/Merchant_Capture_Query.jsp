<%@page contentType="text/html; charset=Big5"%>
<%@ page language="java" %>
<%@ page import="java.util.*"%>
<%@ page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%@ page import="com.cybersoft.bean.UserBean" %>
<!-- 20221028/jquery�ɯŦ�3.6.0/Jeffery.Cheng/202210210533-00 -->
<%
/*
 * 202007070145-00 20200707 HKP ���]��|�ݨDUI�W�[���дڥ\��
*/  
  response.addHeader("Pragma", "No-cache");
  response.addHeader("Cache-Control", "no-cache");
  response.setDateHeader("Expires", 1);

try {

  UserBean mlb=new UserBean();
  String StartDate = mlb.get_AppointDay_Date("yyyy/MM/dd",-7);
  String EndDate = mlb.get_TransDate("yyyy/MM/dd");

Hashtable userinfo=(Hashtable)session.getAttribute("SYSCONFDATA");
System.out.println(userinfo);
Hashtable merchantinfo=(Hashtable)userinfo.get("MERCHANT");
Hashtable merchantuserinfo=(Hashtable)userinfo.get("MERCHANT_USER");
String merchantid=String.valueOf(merchantuserinfo.get("MERCHANT_ID"));
ArrayList terminalinfo=(ArrayList)userinfo.get("TERMINAL");

//���o�d�ߤ������
Hashtable conf = (Hashtable)userinfo.get("SYSCONF");
int queryMonth = Integer.parseInt(conf.get("MER_LIMIT_QRY_MONTH").toString());

//�O�_���l�S��
boolean isSubMid =false;
String subMidName="";
String subMid="";
subMidName=merchantuserinfo.get("MERCHANTCALLNAME").toString();
 subMid=merchantuserinfo.get("SUBMID").toString();
 isSubMid =((String) merchantuserinfo.get("ISSUBMERCHANT")).equals("Y") ?  true : false;
%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html charset=big5">
    <link href="css/style.css" type="text/css" rel="stylesheet">
    <script language="JavaScript" src="js/Vatrix.js"></script>
    <script language="JavaScript" src="js/calendar.js" ></script>
    <!-- jquery -->
    <script type="text/javascript" src="./js/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" src="./js/popper.min.js"></script>
    <!-- jquery -->

<script language="JavaScript" type="text/JavaScript">
<!--
  $(document).ready(function () {
	  $("#divProcess").hide();
  });
  function blink(selector) {
      $(selector).fadeOut(1000, function () {
          $(this).fadeIn(1000, function () {
              blink(this);
          });
      });
  };

function submitform(){
  if(checkdata()){
    $("#btnQuery").attr('disabled', 'disabled');
    $("#divProcess").show().fadeIn(1000);;
    blink('.blink');//��r�{�{
    document.form.submit();
  }
}

function checkdata(){
 var nowdate  = new Date();
 nowdate.setMonth(nowdate.getMonth()-<%=queryMonth%>);
 var limitDate =cal_gen_date2(nowdate);
 
  if (document.all.form.StartTransDate.value.length == 0 && document.all.form.EndTransDate.value.length == 0 ) {
    alert("�аO�o��J����d�߰_�l�ε������");
    return;
  }
  if (document.all.form.StartTransDate.value.length == 0 && document.all.form.EndTransDate.value.length > 0) {
    alert("�аO�o��J����_�l���");
    return;
  }
  if (document.all.form.EndTransDate.value.length == 0&& document.all.form.StartTransDate.value.length > 0) {
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
  }if (Check_Date( document.all.form.StartTransDate.value, limitDate )) {
      alert("�d�߰_��d�򤣥i�W�L���_�^��<%=queryMonth%>�Ӥ�!!");
      return;
 }
  return true;
}//�d�߰_��d�򤣥i�W�L���_�^��T�Ӥ�!!

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
function CaptureTypeChange(captureType){
	if(captureType == "BATCH"){
		$("#trPage01").hide();
		$("#trPage02").hide();
		$("#trPage03").hide();
		$("#trPage04").hide();
		document.form.Action.value="BATCH";
		//$("#Action").text("BATCH");
		
	}else{
		$("#trPage01").show();
		$("#trPage02").show();
		$("#trPage03").show();
		$("#trPage04").show();
		document.form.Action.value="FirstQuery";
		//$("#Action").text("FirstQuery");
	}
}
//-->
</script>
</head>
<body bgcolor="#ffffff">
<form method="post" name="form" id="form" action="./MerchantCaptureCtl">
<input type="hidden" name="Action" id="Action" value="FirstQuery" />
<input type="hidden" name="token" id="token" value="" />
 <%
 String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);
 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
   <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
   <td align="left" valign="middle"><b><font color="#004E87" size="3">����дڬd�߱���</font></b></td>
  </tr>
 </table>

 <hr style="height:1px">
  <div id="divProcess">
        <div class="form-row">
            <div class="col-12 col-md-11 text-center">
                <font color="blue" size="3"><b class="blink">����B�z���еy��(Please wait)....</b></font>
            </div>
        </div>
  </div>

 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
  <tr align='center' height="25">
   <td width='25%' bgcolor='#F4F4FF' align='right'>�S���ө��N��(*)</td>
   <td width='75%' bgcolor='#ffffff' align='left' ><%=merchantid %></td>
  </tr>
   <% if(isSubMid){ %> 
  <tr align='center' height="25">
   <td width='25%' bgcolor='#F4F4FF' align='right'>�l�S���ө��N��</td>
   <td width='75%' bgcolor='#ffffff' align='left' ><%= subMid+"("+subMidName+")"%></td>
  </tr>
  <%} %>
  <tr align='center' height="30">
   <td width='25%' align='right' bgcolor='#F4F4FF'>�дڤ覡</td>
   <td align='left' bgcolor='#FFFFFF'>
     <input type="radio" id="Captue_page" name="CaptueTypeFlag" value="UI" checked="checked" onclick="javascript:CaptureTypeChange('UI');"/>�����Ŀ�
     <% if(isSubMid==false){ //����l�S�����i�妸�д�%>
     <input type="radio" id="Captue_batch" name="CaptueTypeFlag" value="BATCH"  onclick="javascript:CaptureTypeChange('BATCH');"/>���д�(�]�t�l�S��)
     <%} %>
   </td>
  </tr>
  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>������(*)</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
    <input type="Text" name="StartTransDate" id="StartTransDate" value="<%=StartDate%>" size="20">
    <img src="images/calendar/cal.gif" style="cursor:hand" border="0" alt="�����J" onclick="javascript:datepopup(document.all.StartTransDate)">&nbsp;

    ~
    <input type="Text" name="EndTransDate" id="EndTransDate" value="<%=EndDate%>" size="20">
    <img src="images/calendar/cal.gif" style="cursor:hand" border="0" alt="�����J" onclick="javascript:datepopup(document.all.EndTransDate)">&nbsp;

   </td>
  </tr>

  <tr id="trPage01" align='center' height="25">
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
   <td width='25%' align='right' bgcolor='#F4F4FF'>������O</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><select name='TransCode' id='TransCode'><option value='ALL'>����</option><option value='00'>�ʳf���</option><option value='01'>�h�f���</option></select></td>
  </tr>

<% //�s�W�d�O(TRANSTYPE)���U�Կ��, �w�]�� ���� 
   // Merchant Console �u�W�дڧ@�~�Ҳ�  �ק�  by Jimmy Kang 20150515 --�ק�}�l-- %>
  <tr id="trPage02" align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>�d�O</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><select name='TransType' id='TransType'><option value='ALL'>����</option><option value='CUP'>CUP</option><option value='VMJ'>VMJ</option><option value='SSL'>�H�Υd</option></select></td>
  </tr>
<% // Merchant Console �u�W�дڧ@�~�Ҳ�  �ק�  by Jimmy Kang 20150515 --�קﵲ��-- %>

  <tr id="trPage03" align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>���w�渹</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
    <select name='OrderType' id='OrderType' onchange="selectchange();" ><option value='Order'>�S�����w�渹</option><option value='Sys_Order'>�t�Ϋ��w�渹</option></select>
    <input type='text' id='OrderID' name='OrderID' size='50' maxlength='25' ></td>
  </tr>


  <tr id="trPage04" align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>���w���覡(*)</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><select name='CheckFlag' id='CheckFlag'><option value='Y' selected >���說</option><option value='N'>��說</option></select></td>
  </tr>
  <tr align='center' height="30">
   <td width='25%' align='right' bgcolor='#F4F4FF'>&nbsp;</td>
   <td align='left' bgcolor='#F4F4FF'>
     <input type='button' value='�d��' name='btnQuery' id='btnQuery' onclick='submitform()' >
     <input type='button' value='�M���e��' name='btnClear' id='btnClear' onclick='toClear();' >
   </td>
  </tr>
  <!-- tr align='center' height="30">
   <td align='center' bgcolor='#F4F4FF' colspan="2">
    <input type='button' value='�d��' name='btnQuery' id='btnQuery' onclick='submitform()' >
    <input type='button' value='�M���e��' name='btnClear' id='btnClear' onclick='toClear();' >
   </td>
  </tr-->

 </table>
</form>
</body>
</html>
<%
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errMsg",e.toString());
          //  request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
        }
%>
