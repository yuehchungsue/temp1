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

 <title>���v����˵�</title>
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
   <td align="left" valign="middle"><b><font color="#004E87" size="3">������v</font></b><font size="3" color="#004E87"><b>���G</b></font></td>
  </tr>
 </table>

 <hr style="height:1px">

 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8' >
  <tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>�S���ө��N��</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getMerchantID()).replaceAll("null","")%></td>
  </tr>
    <tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>�l�S���ө��N��</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getSubMID()).replaceAll("null","")+"("+MerchantName+")"%></td>
  </tr>
	<tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>�ݥ����N��</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getTerminalID()).replaceAll("null","")%></td>
  </tr>
	<tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>���v���</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getTransDate()).replaceAll("null","")%></td>
  </tr>
	<tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>���v�ɶ�</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getTransTime()).replaceAll("null","")%></td>
  </tr>
	<tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>�^���X</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getResponseCode()).replaceAll("null","")%></td>
  </tr>
	<tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>�^���T��</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getResponseMsg()).replaceAll("null","")%></td>
  </tr>
	<tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>���v�X</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getApproveCode()).replaceAll("null","")%></td>
  </tr>
  <tr align='center' bgcolor='#6666CC' >
   <td width='35%' align='right' bgcolor='#F4F4FF'>�S�����w�渹</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getOrderID()).replaceAll("null","")%></td>
  </tr>

  <tr >
   <td width='35%' align='right' bgcolor='#F4F4FF'>�t�Ϋ��w�渹</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getSysOrderID()).replaceAll("null","")%></td>
  </tr>
  <tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>�妸���X</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getBatchNo()).replaceAll("null","")%></td>
  </tr>
	<tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>������O</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'>00_���v���</td>
  </tr>
	<tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>������O</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'>�s�x��</td>
  </tr>
	<tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>������B</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getTransAmt()).trim().length()==0?" ":nf.format(pbean.getTransAmt())%></td>
  </tr>
  <tr>
   <td width='35%' align='right' bgcolor='#F4F4FF'>����Ҧ�</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%
      String TransMode = String.valueOf(pbean.getTransMode()).replaceAll("null","");
      if (TransMode.equalsIgnoreCase("0")) out.write("0_�@��");
      if (TransMode.equalsIgnoreCase("1")) out.write("1_����");
      if (TransMode.equalsIgnoreCase("2")) out.write("2_���Q���");
   %></td>
  </tr>
  <% if (TransMode.equalsIgnoreCase("1")){ %>
  	<tr>
   		<td width='35%' align='right' bgcolor='#F4F4FF'>��������</td>
   		<td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getInstallCount()).replaceAll("null","")%></td>
  	</tr>
	<tr>
   		<td width='35%' align='right' bgcolor='#F4F4FF'>�������B</td>
   		<td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getFirstAmt()).trim().replaceAll("null","").length()==0?" ":nf.format(pbean.getFirstAmt())%></td>
  	</tr>
	<tr>
   		<td width='35%' align='right' bgcolor='#F4F4FF'>�C�����B</td>
   		<td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getEachAmt()).trim().replaceAll("null","").length()==0?" ":nf.format(pbean.getEachAmt())%></td>
  	</tr>
	<tr>
   		<td width='35%' align='right' bgcolor='#F4F4FF'>����O</td>
   		<td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getFee()).trim().replaceAll("null","").length()==0?" ":nf.format(pbean.getFee())%> </td>
  	</tr>
	<tr>
   		<td width='35%' align='right' bgcolor='#F4F4FF'>��������O���O</td>
   		<td width='65%' align='left'  bgcolor='#FFFFFF'><%
                    String InstallType = String.valueOf(pbean.getInstallType()).replaceAll("null","");
                    if (InstallType.equalsIgnoreCase("I")) out.write("I_���t");
                    if (InstallType.equalsIgnoreCase("E")) out.write("E_�~�[");
                %>
   	</td>
  	</tr>
  <% } %>
  <% if (TransMode.equalsIgnoreCase("2")){ %>
  	<tr>
   		<td width='35%' align='right' bgcolor='#F4F4FF'>���Q�����O</td>
   		<td width='65%' align='left'  bgcolor='#FFFFFF'><%
                String RedemType = String.valueOf(pbean.getRedemType()).replaceAll("null","");
                String RedemTypeShow="�D���Q�����";
                if (RedemType.equalsIgnoreCase("1")) RedemTypeShow = "1_���B���Q�����";
                if (RedemType.equalsIgnoreCase("2")) RedemTypeShow = "2_�������Q�����";
                out.write(RedemTypeShow);
 %>            </td>
        </tr>
    	<tr>
   		<td width='35%' align='right' bgcolor='#F4F4FF'>���Q����I��</td>
   		<td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getRedemUsed()).trim().replaceAll("null","").length()==0?" ":nf.format(Double.parseDouble(String.valueOf(pbean.getRedemUsed())))%></td>
  	</tr>

        <tr>
   		<td width='35%' align='right' bgcolor='#F4F4FF'>���Q�l�B</td>
   		<td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(pbean.getRedemBalance()).trim().replaceAll("null","").length()==0?" ":nf.format(Double.parseDouble(String.valueOf(pbean.getRedemBalance())))%></td>
  	</tr>

  	<tr>
   		<td width='35%' align='right' bgcolor='#F4F4FF'>�d�H�ۥI�B</td>
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
