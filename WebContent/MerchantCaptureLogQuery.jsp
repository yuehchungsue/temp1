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
  Hashtable merchantuserinfo=(Hashtable)userinfo.get("MERCHANT_USER");
  String merchantid=String.valueOf(merchantuserinfo.get("MERCHANT_ID"));
  ArrayList terminalinfo=(ArrayList)userinfo.get("TERMINAL");
  
//子特店清單
ArrayList subMidList = new ArrayList();
//是否為子特店
boolean isSubMid =false;
//是否為單一特店
boolean isSignMer = false;
String subMidName="";
String subMid="";
subMidName=merchantuserinfo.get("MERCHANTCALLNAME").toString();
 subMid=merchantuserinfo.get("SUBMID").toString();
 isSubMid =((String) merchantuserinfo.get("ISSUBMERCHANT")).equals("Y") ?  true : false;

 subMidList = (ArrayList) userinfo.get("SUBMID");
 isSignMer = subMidList != null && subMidList.size() > 1 ? false :  true;
 
 
//取得查詢日期限制
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

function submitform()
{
  if(checkdata()){

    document.form.submit();
  }
}
function checkdata(){
	 var nowdate  = new Date();
	 nowdate.setMonth(nowdate.getMonth()-<%=queryMonth%>);
	 var limitDate =cal_gen_date2(nowdate);
  if (document.all.form.Start_TransDate.value.length == 0 && document.all.form.End_TransDate.value.length == 0 ) {
    alert("請記得輸入交易查詢起始及結束日期");
    return;
  }
   if (document.all.form.Start_TransDate.value.length == 0 && document.all.form.End_TransDate.value.length > 0) {
     alert("請記得輸入交易起始日期");
     return;
   }
   if (document.all.form.End_TransDate.value.length == 0&& document.all.form.Start_TransDate.value.length > 0) {
     alert("請記得輸入交易結束日期");
     return;
   }
   if(document.all.form.End_TransDate.value.length > 0 && document.all.form.Start_TransDate.value.length > 0){
     if (!check_date_format(document.all.form.Start_TransDate.value) || !Check_Input_Date(document.all.form.Start_TransDate.value)) {
       alert("交易起始日期格式錯誤");
       return;
     }
     if (!check_date_format(document.all.form.End_TransDate.value) || !Check_Input_Date(document.all.form.End_TransDate.value) ) {
       alert("交易結束日期格式錯誤");
       return;
     }
   }
   if (!Check_Date( document.all.form.Start_TransDate.value, document.all.form.End_TransDate.value )) {
     alert("交易起啟日期不可大於結束日期");
     return;
   }
   if (Check_Date( document.all.form.Start_TransDate.value, limitDate )) {
      alert("查詢起日範圍不可超過當日起回算<%=queryMonth%>個月!!");
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
  document.all.form.End_TransDate.value="<%=Today%>";
  document.all.form.TransCode.value="ALL";
  document.all.form.AuthID.value="";
  document.all.form.TerminalID.value="ALL";
  document.all.form.CaptrueType.value="ALL";
  document.all.form.ExceptFlag.value="ALL";
  document.all.form.Type.value="TotalNet";
}
//-->
</script>
<body bgcolor="#ffffff">
<form method="post" name="form" id="form" action="./MerchantCaptureLogCtl">
 <%
String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);
 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
   <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
   <td align="left" valign="middle"><b><font color="#004E87" size="3">請款交易查詢條件</font></b></td>
  </tr>
 </table>

 <hr style="height:1px">

 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
  <tr align='center' height="25">
   <td width='25%' bgcolor='#F4F4FF' align='right'>特約商店代號(*)</td>
   <td width='75%' bgcolor='#ffffff' align='left' ><%=merchantid %></td>
  </tr>
   <% if(!isSignMer && !isSubMid){ %>
   <tr align='center' height="25">
      <td width="25%" bgcolor='#F4F4FF'   align='right'>子特約商店代號</td>
      <td width='75%' bgcolor='#ffffff' align='left' >
      <select id="subMid" name="subMid">
        	<option value="all">全部</option>
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
      <td width="25%" bgcolor='#F4F4FF'   align='right'>子特約商店代號</td>
      <td width='75%' bgcolor='#ffffff' align='left' >
      <%=subMid+"("+subMidName+")" %>
      <input type="hidden"   id="subMid" name="subMid"  value="<%=subMid%>">
      </td>
    </tr>
    <%} if(isSignMer ){%>   
    <input type="hidden"   id="subMid" name="subMid"  value="<%=subMid%>">
    <%} %> 
  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>請款日期(區間)(*)</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
    <input type="Text" name="Start_TransDate" id="Start_TransDate" value="<%=Today%>" size="20">
    <img src="images/calendar/cal.gif" style="cursor:hand" border="0" alt="日期輸入" onclick="javascript:datepopup(document.all.Start_TransDate)">
    ~
    <input type="Text" name="End_TransDate" id="End_TransDate" value="<%=Today%>" size="20">
    <img src="images/calendar/cal.gif" style="cursor:hand" border="0" alt="日期輸入" onclick="javascript:datepopup(document.all.End_TransDate)">
   </td>
  </tr>

  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>交易類別</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
     <select name='TransCode' id='TransCode'>
       <option value='ALL'>全部</option>
       <option value="00">購貨交易</option>
       <option value="01">退貨交易</option>
       <option value="CANCEL">請款取消</option>
     </select></td>
  </tr>
  
  <!-- 請款交易查詢 Jimmy Kang 20150727 查詢條件 新增  TransType 下拉選單  -- 新增開始 -- -->
  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>卡別</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
     <select name='TransType' id='TransType'>
       <option value='ALL'>全部</option>
       <option value='CUP'>CUP</option>
       <option value='VMJ'>VMJ</option>
       <option value='SSL'>信用卡</option>
     </select></td>
  </tr>
  <!-- 請款交易查詢 Jimmy Kang 20150727 查詢條件 新增  TransType 下拉選單  -- 新增結束 -- -->
  
  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>指定單號</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
    <select name='OrderType' id='OrderType'  onchange="selectchange();"><option value='M'>特店指定單號</option><option value='S'>系統指定單號</option></select>
    <input type='text' id='OrderID' name='OrderID' size='50' maxlength='25' ></td>
  </tr>

  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>授權碼</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><input type='text' id='AuthID' name='AuthID' size='10' maxlength='8'></td>
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
   <td width='25%' align='right' bgcolor='#F4F4FF'>請款結果</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><select name='CaptrueType' id='CaptrueType'><option value='ALL'>全部</option><option value='Capture'>核准</option><option value='NotCapture'>拒絕</option><option value='Process'>處理中</option></select></td>
  </tr>

  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>請款方式</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
     <select name='ExceptFlag' id='ExceptFlag'>
       <option value='ALL'>全部</option>
       <option value="MERCHANT">特店請款</option>
       <option value="ACQ">收單行補請款</option>
     </select></td>
  </tr>

  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>顯示類型</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><select name='Type' id='Type'><option value='TotalNet'>彙總表</option><option value='List'>明細表</option></select></td>
  </tr>

  <tr align='center' height="30">
   <td align='center' bgcolor='#F4F4FF' colspan="2">
    <input type='button' value='查詢' name='btnQuery' id='btnQuery' onclick='submitform()' >
    <input type='button' value='清除畫面' name='btnClear' id='btnClear' onclick='clearall()' >
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
