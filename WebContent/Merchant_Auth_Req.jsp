<%@page contentType="text/html; charset=Big5"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>
<%@ page import="com.cybersoft4u.prj.tfbpg.api.SSLServer"%>
<%@ page import="com.cybersoft4u.prj.tfbpg.bean.ParamBean"%>
<%@ page import="com.cybersoft.merchant.bean.MerchantAuthParam"%>
<%@ page import="com.cybersoft4u.util.CheckPan"%>
<%@ page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%
//***************************************************************************
//  * #File Name: 	 Merchant_Auth_Req.jsp
//  * #Description:    the login interface
//  * #Create Date:    2007-10-11
//  * #Company:	 cybersoft
//  * @author          Tina YOU
//  * @see
//  * @since		 Java Standard V0.1
//  * @version	 0.0.1    2007-10-11    Tina YOU
//  ***************************************************************************

//response.addHeader("Pragma", "No-cache");
//response.addHeader("Cache-Control", "no-cache");
//response.addDateHeader("Expires", 1);
try {
Hashtable userinfo=(Hashtable)session.getAttribute("SYSCONFDATA");
System.out.println(userinfo);
Hashtable merchantuserinfo=(Hashtable)userinfo.get("MERCHANT_USER");
Hashtable merchantinfo=(Hashtable)userinfo.get("MERCHANT");
String merchantid=String.valueOf(merchantinfo.get("MERCHANTID"));
//String FORCE_SOCIALID=String.valueOf(merchantinfo.get("FORCE_SOCIALID"));
String FORCE_SOCIALID="N";
String FORCE_CVV2=String.valueOf(merchantinfo.get("FORCE_CVV2"));
String PERMIT_SALE=String.valueOf(merchantinfo.get("PERMIT_SALE"));
String PERMIT_REDEM_SALE=String.valueOf(merchantinfo.get("PERMIT_REDEM_SALE"));
String PERMIT_INSTALL_SALE=String.valueOf(merchantinfo.get("PERMIT_INSTALL_SALE"));
ArrayList terminalinfo=(ArrayList)userinfo.get("TERMINAL");
ArrayList AuthExpireYear = (ArrayList)session.getAttribute("AuthExpireYear");
if (AuthExpireYear==null) AuthExpireYear = new ArrayList();

boolean fcvv2 = false;
if( FORCE_CVV2.equals("Y")) {
  fcvv2 = true;
}
MerchantAuthParam hppparam = new MerchantAuthParam();
hppparam.setMerchantID(String.valueOf(merchantinfo.get("MERCHANTID"))==null?"":String.valueOf(merchantinfo.get("MERCHANTID")));
hppparam.setForceCVV2(fcvv2);

request.getSession().setAttribute("AuthParam",hppparam);

Date date = new Date();
SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
String Today = sdf.format(new Date());

//是否為子特店
boolean isSubMid =false;
//是否為單一特店
boolean isSignMer = false;
ArrayList subMidList = (ArrayList) userinfo.get("SUBMID");
String subMidName="";
subMidName=merchantuserinfo.get("MERCHANTCALLNAME").toString();
String subMid=merchantuserinfo.get("SUBMID").toString();
isSubMid =((String) merchantuserinfo.get("ISSUBMERCHANT")).equals("Y") ?  true : false;
isSignMer = subMidList != null && subMidList.size() > 1 ? false :  true;
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
  if(checkdata())
  {
     document.form.btnSubmit.disabled = true;
     document.form.btnClear.disabled = true;
     document.form.submit();
  }
}

function checkdata(){
  if (document.all.form.TerminalID.value.length == 0) {
     alert("請輸入端末機代號");
     return;
  }
  if (document.all.form.ORDERID.value.length == 0) {
     alert("請輸入訂單編號");
     return;
  }
  if (document.all.form.pan.value.length == 0) {
     alert("請輸入卡號");
     return;
  }
  if(!check_numerical(document.all.form.pan.value)){
      alert("信用卡卡號輸入應為數字!!");
      return;
  }
  if(document.all.form.pan.value.length < 11 || document.all.form.pan.value.length > 19){
      alert("信用卡卡號長度不符(11~19碼)!!");
      return;
  }
  if (document.all.form.expireM.value.length == 0) {
     alert("請輸入有效期限");
     return;
  }
  if (document.all.form.expireY.value.length == 0) {
     alert("請輸入有效期限");
     return;
  }
  //有效期限大於等於系統日
  var today = "<%=Today%>";
  var inputdate = document.all.form.expireY.value + document.all.form.expireM.value;
  if (parseInt(inputdate) < parseInt(today)) {
        alert("有效日期應大於等於系統日期");
        return;
  }

  if (!check_date_format(document.all.form.expireY.value + "/" + document.all.form.expireM.value + "/01")
   || !Check_Input_Date(document.all.form.expireY.value + "/" + document.all.form.expireM.value + "/01")) {
  	alert("有效期限格式錯誤");
  	return;
  }
<%if (FORCE_CVV2.equalsIgnoreCase("Y")) {  // 強制輸入 cvv2%>
  if (document.all.form.extenNo.value.length == 0) {
     alert("請輸入CVV2/CVC2");
     return;
  }
<%}%>
  if (document.all.form.extenNo.value.length > 0) {
    if (document.all.form.extenNo.value.length != 3) {
      alert("CVV2/CVC2 資料長度必須為3位");
      return;
    }
  }
  if (document.all.form.TRANSMODE.value.length == 0) {
     alert("請輸入交易模式");
     return;
  }
  if (document.all.form.TRANSAMT.value.length == 0) {
     alert("請輸入請款金額");
     return;
  }
  if(!check_numerical(document.all.form.TRANSAMT.value)){
     alert("請款金額必須為數字");
     return;
  }

  if (document.all.form.TRANSMODE.value == "1") {
  	if (document.all.form.INSTALL.value.length == 0) {
          alert("請輸入分期期數");
          return;
  	}
  } else {
 	if (document.all.form.INSTALL.value.length != 0) {
          alert("非分期交易但分期期數有值");
          return;
  	}
  }
<% if (FORCE_SOCIALID.equalsIgnoreCase("Y")) {   %>
  if (document.all.form.SOCIALID.value.length == 0) {
    alert("請輸入持卡人身份證字號");
    return;
  }
<% } %>
  return true;
}

//-->
</script>
<body bgcolor="#ffffff" >
<form method="post" name="form" id="form" action="MerchantAuthCtl">
<input type="hidden" name="Action" id="Action" value="Query">
 <%
 String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);
 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
   <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
   <td align="left" valign="middle"><b><font color="#004E87" size="3">交易</font></b><font size="3" color="#004E87"><b>授權輸入</b></font></td>
  </tr>
 </table>

 <hr style="height:1px">

 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
  <tr align='center' height="25">
   <td width='25%' bgcolor='#F4F4FF' align='right'>特約商店代號</td>
   <td width='75%' bgcolor='#ffffff' align='left' ><%= merchantid%></td>
  </tr>
   <% if(isSubMid){ %> 
  <tr align='center' height="25">
   <td width='25%' bgcolor='#F4F4FF' align='right'>子特約商店代號</td>
   <td width='75%' bgcolor='#ffffff' align='left' ><%= subMid+"("+subMidName+")"%></td>
  </tr>
  <%} %>
     <input type="hidden" id="subMid"name="subMid" value="<%= subMid+"("+subMidName+")"%>"/>
  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>端末機代號(*)</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
    <select size="1" name="TerminalID" id="TerminalID">
    <%for(int i=0;terminalinfo!=null&&i<terminalinfo.size();i++){
       String terminalid=String.valueOf(((Hashtable)terminalinfo.get(i)).get("TERMINALID"));
    %>
    <option value='<%=terminalid%>'><%=terminalid%></option>
    <%} %>
    </select>
   </td>
  </tr>

  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>特店指定單號(*)</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
    <input type='text' id='ORDERID' name='ORDERID' size='50' maxlength='25' >
   </td>
  </tr>

  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>交易類別</td>
   <td width='75%' bgcolor='#ffffff' align='left' >00_授權交易</td>
  </tr>

  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>卡號(*)</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
    <input type='text' id='pan' name='pan' size='50' maxlength='19'></td>
  </tr>


  <tr>
   <td width='25%' align='right' bgcolor='#F4F4FF'>有效期限(*)</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><select size="1" name="expireM" id="expireM">
                <option value="01" selected>01</option>
                <option value="02">02</option>
                <option value="03">03</option>
                <option value="04">04</option>
                <option value="05">05</option>
                <option value="06">06</option>
                <option value="07">07</option>
                <option value="08">08</option>
                <option value="09">09</option>
                <option value="10">10</option>
                <option value="11">11</option>
                <option value="12">12</option>
            </select>
            /
            <select size="1" name="expireY" id="expireY"><%
            for (int j=0; j<AuthExpireYear.size();++j) {
                String StrExpireYear = (String)AuthExpireYear.get(j);
                %><option value="<%=StrExpireYear%>"><%=StrExpireYear%></option><%
            }
            %>
   </td>
  </tr>
  <tr>
   <td width='25%' align='right' bgcolor='#F4F4FF'>
    <%   if (fcvv2) {%>CVV2/CVC2(*)
    <%   }else{     %>CVV2/CVC2
    <%   }     %></td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
    <input type='text' id='extenNo' name='extenNo' size='50' maxlength='3' ></td>
  </tr>
	<tr>
   <td width='25%' align='right' bgcolor='#F4F4FF'>交易模式(*)</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
	<select size="1" name="TRANSMODE"  id="TRANSMODE">
		<% if (PERMIT_SALE.equalsIgnoreCase("Y")){ %>
    	<option value='0'>0_一般</option>
    	<% } %>
    	<% if (PERMIT_INSTALL_SALE.equalsIgnoreCase("Y")){ %>
    	<option value='1'>1_分期</option>
    	<% } %>
    	<% if (PERMIT_REDEM_SALE.equalsIgnoreCase("Y")){ %>
    	<option value='2'>2_紅利折抵</option>
    	<% } %>
	</select>
   </td>
  </tr>
	<tr>
   <td width='25%' align='right' bgcolor='#F4F4FF'>交易幣別</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>新台幣</td>
  </tr>
	<tr>
   <td width='25%' align='right' bgcolor='#F4F4FF'>交易金額(*)</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
    <input type='text' id='TRANSAMT' name='TRANSAMT' size='50' maxlength='11'></td>
  </tr>
	<tr>
   <td width='25%' align='right' bgcolor='#F4F4FF'>分期期數</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
    <input type='text'  name="INSTALL" id="INSTALL"  size='50' maxlength='2'></td>
  </tr>
	<tr>
   <td width='25%' align='right' bgcolor='#F4F4FF'>持卡人E-Mail</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
    <input type='text' id='OrderID8' name='OrderID8' size='50' maxlength='40'></td>
  </tr>


  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>持卡人身份證字號<% if (FORCE_SOCIALID.equalsIgnoreCase("Y")) out.write("(*)");%></td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
    <input type='text' id='SOCIALID' name='SOCIALID' size='50' maxlength='12'></td>
  </tr>

  <tr align='center' height="30">
   <td align='center' bgcolor='#F4F4FF' colspan="2">
    <input type='button' value='確認' name='btnSubmit' id='btnSubmit' onclick='submitform()' >
    <input type='reset' value='清除畫面' name='btnClear' id='btnClear'  >
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
