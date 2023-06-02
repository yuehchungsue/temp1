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
Hashtable merchantinfo=(Hashtable)userinfo.get("MERCHANT_USER");
String merchantid=String.valueOf(merchantinfo.get("MERCHANT_ID"));
ArrayList terminalinfo=(ArrayList)userinfo.get("TERMINAL");

String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);

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
  if (document.all.form.Start_TransDate.value.length == 0 && document.all.form.End_TransDate.value.length == 0 ) {
     alert("請記得輸入送檔查詢起始及結束日期");
     return;
   }
   if (document.all.form.Start_TransDate.value.length == 0 && document.all.form.End_TransDate.value.length > 0) {
     alert("請記得輸入送檔起始日期");
     return;
   }
   if (document.all.form.End_TransDate.value.length == 0 && document.all.form.Start_TransDate.value.length > 0) {
     alert("請記得輸入送檔結束日期");
     return;
   }
   if(document.all.form.End_TransDate.value.length > 0 && document.all.form.Start_TransDate.value.length > 0 ){
     if (!check_date_format(document.all.form.Start_TransDate.value) || !Check_Input_Date(document.all.form.Start_TransDate.value)) {
       alert("送檔起始日期格式錯誤");
       return;
     }
     if (!check_date_format(document.all.form.End_TransDate.value) || !Check_Input_Date(document.all.form.End_TransDate.value) ) {
       alert("送檔結束日期格式錯誤");
       return;
     }
   }
   if (!Check_Date( document.all.form.Start_TransDate.value, document.all.form.End_TransDate.value )) {
     alert("送檔起啟日期不可大於結束日期");
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
   var url = "<%=request.getContextPath()%>/MerchantReAuthReportCtl?Action=Query&<%=MerchantMenuCtl.MENU_FORWARD_KEY%>=<%=MenuKey%>&Start_TransDate="+document.all.form.Start_TransDate.value+"&End_TransDate="+document.all.form.End_TransDate.value+"&ReauthFlag="+document.all.form.ReauthFlag.value+"&PrintType="+PrintType;
   subWin=window.open(url,"main",strFeatures);
 }
}

function closeSub(){
  if (subWin != null && subWin.open) subWin.close(); subWin=null;
}

//-->
</script>
<body bgcolor="#ffffff">
<form method="post" name="form" id="form" action="./MerchantReAuthReportCtl">
<input type="hidden" name="Action" id="Action" value="">
 <%
 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
   <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
   <td align="left" valign="middle"><b><font color="#004E87" size="3">再授權交易統計報表</font></b></td>
  </tr>
 </table>

 <hr style="height:1px">

 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
  <tr align='center' height="25">
   <td width='25%' bgcolor='#F4F4FF' align='right'>特約商店代號(*)</td>
   <td width='75%' bgcolor='#ffffff' align='left' ><%=merchantid %></td>
  </tr>

  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>送檔日期(區間)(*)</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
    <input type="Text" name="Start_TransDate" id="Start_TransDate" value="<%=Today%>" size="20">
    <img src="images/calendar/cal.gif" style="cursor:hand" border="0" alt="日期輸入" onclick="javascript:datepopup(document.all.Start_TransDate)">
    ~
    <input type="Text" name="End_TransDate" id="End_TransDate" value="<%=Today%>" size="20">
    <img src="images/calendar/cal.gif" style="cursor:hand" border="0" alt="日期輸入" onclick="javascript:datepopup(document.all.End_TransDate)">
   </td>
  </tr>

  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>再授權結果</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'><select name='ReauthFlag' id='ReauthFlag'><option value='ALL'>全部</option><option value='Y'>核准</option><option value='N'>拒絕</option><option value='R'>再授權</option></select></td>
  </tr>

  <tr align='center' height="30">
   <td align='center' bgcolor='#F4F4FF' colspan="2">
    <input type='button' value='以PDF格式匯出' name='btnPDF' id='btnPDF' onclick="toPrint('PDF')" >
    <input type='button' value='清除畫面' name='btnClear' id='btnClear' onclick='cleardata()' >
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
