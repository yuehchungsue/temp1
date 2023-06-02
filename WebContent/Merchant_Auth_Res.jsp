<%@ page contentType="text/html; charset=Big5" %>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>
<%@ page import="com.cybersoft4u.prj.tfbpg.bean.ParamBean"%>
<%@ page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%
//    response.addHeader("Pragma", "No-cache");
//    response.addHeader("Cache-Control", "no-cache");
//    response.addDateHeader("Expires", 1);
    try {
    Hashtable userinfo=(Hashtable)session.getAttribute("SYSCONFDATA");
    Hashtable merchantuserinfo=(Hashtable)userinfo.get("MERCHANT_USER");
    Hashtable merchantinfo=(Hashtable)userinfo.get("MERCHANT");
    Hashtable merinfo=(Hashtable)userinfo.get("MERCHANT_USER");
    ParamBean pbean = (ParamBean)session.getAttribute("ParamBean");
    
    String MerchantName = (String)merinfo.get("MERCHANTCALLNAME");
    
    NumberFormat nf = NumberFormat.getInstance();
    nf.setMaximumFractionDigits(0);
    nf.setMinimumFractionDigits(2);
    String chkmsg = (String)request.getAttribute("checkmsg");
    String redirect = "";
    if(chkmsg!=null) {
       System.out.println("chkmsg = "+chkmsg);
  	redirect="Y";
    } else {
        chkmsg = "";
    }
   boolean  isSubMid =((String) merchantuserinfo.get("ISSUBMERCHANT")).equals("Y") ?  true : false;
%>
<html>
<head>

 <title>授權交易檢視</title>
 <meta http-equiv="Content-Type" content="text/html" charset="big5">
 <link href="css/style.css" type="text/css" rel="stylesheet">
<script language="JavaScript" src="./js/Vatrix.js"></script>
<script language="JavaScript" src="./js/calendar.js" ></script>
<script type="text/javascript" language="JavaScript">
<!--
function onload() {
	if("<%=redirect%>"=="Y")
	{
		    alert("<%=chkmsg%>");
	}
}

//-->
</script>
</head>
<body bgcolor="#ffffff" onload="onload()">
 <table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
   <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
   <td align="left" valign="middle"><b><font color="#004E87" size="3">交易授權</font></b><font size="3" color="#004E87"><b>結果</b></font></td>
  </tr>
 </table>

 <hr style="height:1px">

 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8' >
  <tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>特約商店代號</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getMerchantID()).replaceAll("null","")%></td>
  </tr>
    <tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>子特約商店代號</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getSubMID()).replaceAll("null","")+"("+MerchantName+")"%></td>
  </tr>
	<tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>端末機代號</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getTerminalID()).replaceAll("null","")%></td>
  </tr>
	<tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>授權日期</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getTransDate()).replaceAll("null","")%></td>
  </tr>
	<tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>授權時間</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getTransTime()).replaceAll("null","")%></td>
  </tr>
	<tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>回應碼</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getResponseCode()).replaceAll("null","")%></td>
  </tr>
	<tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>回應訊息</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getResponseMsg()).replaceAll("null","")%></td>
  </tr>
	<tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>授權碼</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getApproveCode()).replaceAll("null","")%></td>
  </tr>
  <tr align='center' bgcolor='#6666CC' >
   <td width='35%' align='right' bgcolor='#F4F4FF'>特店指定單號</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getOrderID()).replaceAll("null","")%></td>
  </tr>

  <tr >
   <td width='35%' align='right' bgcolor='#F4F4FF'>系統指定單號</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getSysOrderID()).replaceAll("null","")%></td>
  </tr>
  <tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>批次號碼</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getBatchNo()).replaceAll("null","")%></td>
  </tr>
	<tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>交易類別</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'>00_授權交易</td>
  </tr>
	<tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>交易幣別</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'>新台幣</td>
  </tr>
	<tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>交易金額</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getTransAmt()).trim().length()==0?" ":nf.format(pbean.getTransAmt())%></td>
  </tr>
  <tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>交易模式</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%
      String TransMode = String.valueOf(pbean.getTransMode()).replaceAll("null","");
      if (TransMode.equalsIgnoreCase("0")) out.write("0_一般");
      if (TransMode.equalsIgnoreCase("1")) out.write("1_分期");
      if (TransMode.equalsIgnoreCase("2")) out.write("2_紅利折抵");
   %></td>
  </tr>
  <% if (TransMode.equalsIgnoreCase("1")){ %>
  	<tr>
   		<td width='35%' align='right' bgcolor='#F4F4FF'>分期期數</td>
   		<td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getInstallCount()).replaceAll("null","")%></td>
  	</tr>
	<tr>
   		<td width='35%' align='right' bgcolor='#F4F4FF'>首期金額</td>
   		<td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getFirstAmt()).trim().replaceAll("null","").length()==0?" ":nf.format(pbean.getFirstAmt())%></td>
  	</tr>
	<tr>
   		<td width='35%' align='right' bgcolor='#F4F4FF'>每期金額</td>
   		<td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getEachAmt()).trim().replaceAll("null","").length()==0?" ":nf.format(pbean.getEachAmt())%></td>
  	</tr>
	<tr>
   		<td width='35%' align='right' bgcolor='#F4F4FF'>手續費</td>
   		<td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getFee()).trim().replaceAll("null","").length()==0?" ":nf.format(pbean.getFee())%> </td>
  	</tr>
	<tr>
   		<td width='35%' align='right' bgcolor='#F4F4FF'>分期手續費註記</td>
   		<td width='65%' align='left'  bgcolor='#FFFFFF'><%
                    String InstallType = String.valueOf(pbean.getInstallType()).replaceAll("null","");
                    if (InstallType.equalsIgnoreCase("I")) out.write("I_內含");
                    if (InstallType.equalsIgnoreCase("E")) out.write("E_外加");
                %>
   	</td>
  	</tr>
  <% } %>
  <% if (TransMode.equalsIgnoreCase("2")){ %>
  	<tr>
   		<td width='35%' align='right' bgcolor='#F4F4FF'>紅利折抵註記</td>
   		<td width='65%' align='left'  bgcolor='#FFFFFF'><%
                String RedemType = String.valueOf(pbean.getRedemType()).replaceAll("null","");
                String RedemTypeShow="非紅利折抵交易";
                if (RedemType.equalsIgnoreCase("1")) RedemTypeShow = "1_全額紅利折抵交易";
                if (RedemType.equalsIgnoreCase("2")) RedemTypeShow = "2_部份紅利折抵交易";
                out.write(RedemTypeShow);
 %>            </td>
        </tr>
    	<tr>
   		<td width='35%' align='right' bgcolor='#F4F4FF'>紅利折抵點數</td>
   		<td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getRedemUsed()).trim().replaceAll("null","").length()==0?" ":nf.format(Double.parseDouble(String.valueOf(pbean.getRedemUsed())))%></td>
  	</tr>

        <tr>
   		<td width='35%' align='right' bgcolor='#F4F4FF'>紅利餘額</td>
   		<td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getRedemBalance()).trim().replaceAll("null","").length()==0?" ":nf.format(Double.parseDouble(String.valueOf(pbean.getRedemBalance())))%></td>
  	</tr>

  	<tr>
   		<td width='35%' align='right' bgcolor='#F4F4FF'>卡人自付額</td>
   		<td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getCreditAmt()).trim().replaceAll("null","").length()==0?" ":nf.format(Double.parseDouble(String.valueOf(pbean.getCreditAmt())))%></td>
  	</tr>
  <% } %>

 </table>
</body>
</html>
<%
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errMsg",e.toString());
            request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
        }
%>
