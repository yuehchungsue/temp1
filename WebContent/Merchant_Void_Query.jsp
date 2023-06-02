<%@page contentType="text/html;charset=BIG5"%>
<%@page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%@page import="com.cybersoft.bean.UserBean" %>
<%@page import="java.util.*" %>
<%
//  response.addHeader("Pragma", "No-cache");
//  response.addHeader("Cache-Control", "no-cache");
//  response.addDateHeader("Expires", 1);

try {
   Hashtable hashConfData = (Hashtable) session.getAttribute("SYSCONFDATA");
   Hashtable hashMerchant = (Hashtable) hashConfData.get("MERCHANT"); // �S���D��
   Hashtable merchantuserinfo=(Hashtable)hashConfData.get("MERCHANT_USER");
   UserBean UserBean = new UserBean();
   boolean Merchant_SaleVoid_Flag = UserBean.check_Merchant_Column(hashMerchant,"PERMIT_SALE_VOID", "Y");
   boolean Merchant_RefundVoid_Flag = UserBean.check_Merchant_Column(hashMerchant,"PERMIT_REFUND_VOID", "Y");
   boolean Merchant_CaptureVoid_Flag = UserBean.check_Merchant_Column(hashMerchant,"PERMIT_CAPTURE_VOID", "Y");
   
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
 <meta http-equiv="Content-Type" content="text/html" charset=big5">
 <link href="css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" language="javascript" src="./js/ScreenProtectPage.js"></script>
<script type="text/javascript" language="JavaScript">
<!--
  function toSubmit() {
     if (document.form.OrderID.value.length == 0 ) {
        alert("�п�J���w�渹");
        return void(0);
     }
     if(document.all.form.OrderType.value == 'Order'){
       if(document.all.form.OrderID.value.length > 25){
          alert("�S�����w�渹���פ��i�j��25");
          return void(0);
       }
     } else {
       if(document.all.form.OrderID.value.length > 40){
          alert("�t�Ϋ��w�渹���פ��i�j��40");
          return void(0);
       }
     }
     document.form.submit();
  }

  function toClear() {
    document.form.Action.value='';
    document.form.submit();
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

//-->
</script>
</head>

<body>
<form id="form" name="form" method="post" action ="./MerchantVoidCtl" >
<input type="hidden" name="Action" id="Action" value="Query">
<%
 String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);
 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>

 <table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
   <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
   <td align="left" valign="middle"><b><font color="#004E87" size="3">��������d�߱���</font></b></td>
  </tr>
 </table>

 <hr style="height:1px">
<br>
<br>
 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
    <% if(isSubMid){ %> 
  <tr align='center' height="30">
   <td width='30%' bgcolor='#F4F4FF' align='right'>�l�S���ө��N��</td>
   <td width='70%' bgcolor='#ffffff' align='left' ><%= subMid+"("+subMidName+")"%></td>
  </tr>
  <%} %>
  <tr align='center' height="30">
   <td width='30%' align='right' bgcolor='#F4F4FF'>�п����������ʽ�(*)</td>
   <td width='70%' align='left'  bgcolor='#FFFFFF'>
    <select name='ServiceType' id='ServiceType'><%
    if (Merchant_SaleVoid_Flag) { %><option value='Sale'>�ʳf����</option><%
    }
    if (Merchant_RefundVoid_Flag) {%><option value='Refund'>�h�f����</option><%
    }
    if (Merchant_CaptureVoid_Flag) {%><option value='Capture'>�дڨ���</option><%
    }
    %></select></td>
  </tr>

  <tr align='center' height="30">
   <td width='30%' align='right' bgcolor='#F4F4FF'>���w�渹</td>
   <td width='70%' align='left'  bgcolor='#FFFFFF'>
    <select name='OrderType' id='OrderType' onchange="selectchange();" ><option value='Order'>�S�����w�渹</option><option value='Sys_Order'>�t�Ϋ��w�渹</option></select>
    <input type='text' id='OrderID' name='OrderID' size='40' maxlength='25'></td>
  </tr>


  <tr align='center' height="30">
   <td align='center' bgcolor='#F4F4FF' colspan="2">
    <input type='button' value='�d��' name='btnQuery' id='btnQuery' onclick='toSubmit();' >
    <input type='button' value='�M���e��' name='btnClear' id='btnClear' onclick='toClear();' >
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
