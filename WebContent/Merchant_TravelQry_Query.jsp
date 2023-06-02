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
    alert("請記得輸入交易查詢起始及結束日期");
    return;
  }
  if (document.all.form.StartTransDate.value.length == 0 && document.all.form.EndTransDate.value.length > 0) {
    alert("請記得輸入交易起始日期");
    return;
  }
  if (document.all.form.EndTransDate.value.length == 0 && document.all.form.StartTransDate.value.length > 0) {
    alert("請記得輸入交易結束日期");
    return;
  }
  if(document.all.form.EndTransDate.value.length > 0 && document.all.form.StartTransDate.value.length > 0){
     if (!check_date_format(document.all.form.StartTransDate.value) || !Check_Input_Date(document.all.form.StartTransDate.value)) {
       alert("交易起始日期格式錯誤");
       return;
     }
     if (!check_date_format(document.all.form.EndTransDate.value) || !Check_Input_Date(document.all.form.EndTransDate.value) ) {
       alert("交易結束日期格式錯誤");
       return;
     }
  }
  if (!Check_Date( document.all.form.StartTransDate.value, document.all.form.EndTransDate.value )) {
       alert("交易起啟日期不可大於結束日期");
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
   <td align="left" valign="middle"><b><font color="#004E87" size="3">國旅卡預購型交易查詢條件</font></b></td>
  </tr>
 </table>

 <hr style="height:1px">

 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
  <tr align='center' height="25">
   <td width='25%' bgcolor='#F4F4FF' align='right'>特約商店代號(*)</td>
   <td width='75%' bgcolor='#ffffff' align='left' ><%=merchantid %></td>
  </tr>

  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>交易日期(*)</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
    <input type="Text" name="StartTransDate" id="StartTransDate" value="<%=Today%>" size="20">
    <img src="images/calendar/cal.gif" style="cursor:hand" border="0" alt="日期輸入" onclick="javascript:datepopup(document.all.StartTransDate)">&nbsp;

    ~
    <input type="Text" name="EndTransDate" id="EndTransDate" value="<%=Today%>" size="20">
    <img src="images/calendar/cal.gif" style="cursor:hand" border="0" alt="日期輸入" onclick="javascript:datepopup(document.all.EndTransDate)">&nbsp;

   </td>
  </tr>
  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>指定單號</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
    <select name='OrderType' id='OrderType' onchange="selectchange();" ><option value='Order'>特店指定單號</option><option value='Sys_Order'>系統指定單號</option></select>
    <input type='text' id='OrderID' name='OrderID' size='50' maxlength='25' ></td>
  </tr>
  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>授權碼</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><input type='text' id='ApproveCode' name='ApproveCode' size='10' maxlength='8'></td>
  </tr>
  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>端末機代碼</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><select name='TerminalID' id='TerminalID'>
     <option value='ALL'>全部</option>
     <%for(int i=0;terminalinfo!=null&&i<terminalinfo.size();i++){
       String terminalid=String.valueOf(((Hashtable)terminalinfo.get(i)).get("TERMINALID"));
       %>
     <option value='<%=terminalid%>'><%=terminalid%></option>
     <%} %>
    </select>
   </td>
  </tr>

  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>處理狀態</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><select name='Status' id='Status'>
     <option value='ALL'>全部</option>
     <option value='00'>00-待送NCCC</option>
     <option value='01'>01-已送NCCC，待回應</option>
     <option value='10'>10-NCCC檢核成功</option>
     <option value='11'>11-NCCC剔退</option>
     <option value='20'>20-已刪除，不上傳NCCC</option>
    </select>
   </td>
  </tr>

  <tr align='center' height="30">
   <td align='center' bgcolor='#F4F4FF' colspan="2">
    <input type='button' value='查詢' name='btnQuery' id='btnQuery' onclick='submitform()' >
    <input type='button' value='清除畫面' name='btnClear' id='btnClear' onclick='toClear();' >
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