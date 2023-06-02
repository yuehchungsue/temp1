<%@ page contentType="text/html; charset=Big5" %>
<%@ page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%
try{
  //  ***************************************************************************
  //  * #File Name: 	 Merchant_ChangePwdOld.jsp
  //  * #Description:    the login interface
  //  * #Create Date:    2007-10-17
  //  * #Company:	 cybersoft
  //  * @author          Caspar Chen
  //  * @see
  //  * @since		 Java Standard V0.1
  //  * @version	 0.0.1    2007-10-17    Caspar Chen
  //  ***************************************************************************
  //  * ���ʻ���
  //  *                 20221124 Frog Jump Co., YC White Scan/A04 Insecure Design
  //  *                 20221124 Frog Jump Co., YC White Scan/A07 Identification and Authentication Failures
  
  response.addHeader("Pragma", "No-cache");
  response.addHeader("Cache-Control", "no-cache");
  response.addDateHeader("Expires", 1);

  String loginmsg = "";
  if (session.getAttribute("Message") != null) {
    loginmsg = (String) session.getAttribute("Message");
    session.removeAttribute("Message");
  }

  //��session���
  String strSessionUserId = "";
  String strPwssMinLen = "";
  String strPwssMaxLen = "";

  java.util.Hashtable hashConfData = (java.util.Hashtable) session.getAttribute("SYSCONFDATA");

  if (hashConfData != null && hashConfData.size() > 0)
  {
    java.util.Hashtable hashMerUser = (java.util.Hashtable) hashConfData.get("MERCHANT_USER"); // �ϥΪ̸��
    // �ϥΪ̸��
    if (hashMerUser != null)
    {
      if (hashMerUser.get("USER_ID") != null)
      {
        strSessionUserId = hashMerUser.get("USER_ID").toString();
      }
    }

    java.util.Hashtable hashSys = (java.util.Hashtable) hashConfData.get("SYSCONF");
    // �t�ΰѼƸ��
    strPwssMinLen = hashSys.get("MER_PWD_MINLEN") == null || hashSys.get("MER_PWD_MINLEN").toString().length() == 0 ? "0":hashSys.get("MER_PWD_MINLEN").toString();
    strPwssMaxLen = hashSys.get("MER_PWD_MAXLEN") == null || hashSys.get("MER_PWD_MAXLEN").toString().length() == 0 ? "16":hashSys.get("MER_PWD_MAXLEN").toString();
  }
%>
<html>
<head>
 <meta http-equiv="Content-Type" content="text/html charset=big5" >
 <link href="css/style.css" type="text/css" rel="stylesheet">
 <script language='javascript'>
<!--
function loadWindow(){
  if (window.event.clientX < 0 && window.event.clientY < 0) {
    if (confirm("�z�T�w�n���}�ܡH")) {
      window.open("./MerchantLogoutCtl?LogoutFlag=End","main","");
    } else {
      window.open("./Merchant_ChangePwdOld.jsp","","");
    }
  }
}
-->
</script>
</head>

<body onunload='loadWindow()'>
<form name="form" id="form" method="post" action="./MerchantChangePwdCtl" >

<script language="JavaScript" src="js/calendar.js" type=""></script>

 <table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
   <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg" alt=""></td>
   <td align="left" valign="middle"><b><font color="#004E87" size="3">�K�X�ܧ�</font></b></td>
  </tr>
 </table>

 <hr style="height:1px">

 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
  <tr align='center' height="25">
   <td width='40%' bgcolor='#F4F4FF' align='right'>�ϥΪ̥N��(*)</td>
   <td width='60%' bgcolor='#ffffff' align='left' ><%=strSessionUserId %></td>
  </tr>

  <tr align='center' height="25">
   <td width='40%' align='right' bgcolor='#F4F4FF'>�±K�X(*)</td>
   <td width='60%' align='left'  bgcolor='#FFFFFF'>
    <input type="password" name="oldPwd" id="oldPwd" value="" size="20" maxlength="<%=strPwssMaxLen%>" autocomplete="off">
   </td>
  </tr>

  <tr align='center' height="25">
   <td width='40%' align='right' bgcolor='#F4F4FF'>�s�K�X(*)</td>
   <td width='60%' align='left'  bgcolor='#FFFFFF'>
     <input type="password" name="newPwd" id="newPwd" value="" size="20" maxlength="<%=strPwssMaxLen%>" autocomplete="off">
   </td>
  </tr>

  <tr align='center' height="25">
   <td width='40%' align='right' bgcolor='#F4F4FF'>�T�{�s�K�X(*)</td>
   <td width='60%' align='left'  bgcolor='#FFFFFF'>
     <input type="password" name="confirm" id="confirm" value="" size="20" maxlength="<%=strPwssMaxLen%>" autocomplete="off">
   </td>
  </tr>

  <tr align='center' height="30">
   <td align='center' bgcolor='#F4F4FF' colspan="2">
    <input type='button' value='�T�w' name='btnQuery' id='btnQuery' onclick='Query();' >
    <input type="reset" value='�M���e��' name='btnClear' id='btnClear' onclick=document.form.oldPwd.focus();>
    <input type='button' value='�n�X' name='btnQuery' id='btnQuery' onclick="Logout('Login');" >
   </td>
  </tr>

<%
String strUserStatus = (String)session.getAttribute(com.cybersoft.merchant.ctl.MerchantLoginCtl.LOGIN_OLD_USER_STATUS);
%>
<tr align='center' height="30">
  <td align='center' bgcolor='#F4F4FF' colspan="2">
  <%
  if("O".equalsIgnoreCase(strUserStatus)){
    out.println("<font color='FF0000' size='4'>�����n�J�ݧ@�K�X�ܧ�</font>");
  }else if("R".equalsIgnoreCase(strUserStatus)){
    out.println("<font color='FF0000' size='4'>�K�X���]�ݧ@�K�X�ܧ�</font>");
  }else if("T".equalsIgnoreCase(strUserStatus)){
    out.println("<font color='FF0000' size='4'>�K�X����ݧ@�K�X�ܧ�</font>");
  }else{
    out.println("<font color='FF0000' size='4'>�ݧ@�K�X�ܧ�</font>");
  }
  %>
  </td>
</tr>
 </table>
 <input type="hidden" name="LogoutFlag" id="LogoutFlag" value="">
 <input type="hidden" name="changePwdFlag" id="changePwdFlag" value="Y">
 <%
 String MenuKey = (String)session.getAttribute(com.cybersoft.merchant.ctl.MerchantLoginCtl.LOGIN_OLD_CHANGE);
 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>
</form>

<script language="JavaScript" type="text/JavaScript">
<!--
document.form.oldPwd.focus();
<%if (loginmsg.length()>0) { %>
toShowMessage();
function toShowMessage() {
  alert("<%=loginmsg%>");
  loginmsg = '';
  return void(0);
}
<%}%>

function Query()
{
  var leOldPwd = document.form.oldPwd.value.length;
  var leNewPwd = document.form.newPwd.value.length;
  var leConfirm = document.form.confirm.value.length;

  if(leOldPwd == 0){
    document.form.oldPwd.focus();
    alert("�±K�X��쥼��J");
    return void(0);
  }
  if(leNewPwd == 0){
    document.form.newPwd.focus();
    alert("�s�K�X��쥼��J");
    return void(0);
  }
  if(leConfirm == 0){
    document.form.confirm.focus();
    alert("�T�{�s�K�X��쥼��J");
    return void(0);
  }

  if(leNewPwd != leConfirm){
    document.form.newPwd.focus();
    alert("�z���s�K�X�P�T�{�s�K�X���פ��@�P");
    return void(0);
  }
  document.form.action = './MerchantChangePwdCtl';
  document.form.submit();
}

function Logout(flag){
  document.form.action = './MerchantLogoutCtl';
  document.form.LogoutFlag.value = flag;
  document.form.submit();
}
//-->
</script>
</body>

</html>
<%
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errMsg",e.toString());
            request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
        }
%>
