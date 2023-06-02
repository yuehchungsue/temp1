<%@page contentType="text/html;charset=BIG5"%>
<%@ page import="java.util.*"%>
<%@ page import="com.cybersoft.merchant.bean.MerchantLoginBean"%>
<%
  // 201906 201906180256 調整特店網站名稱顯示 SHERRY ANN
  response.addHeader("Pragma", "No-cache");
  response.addHeader("Cache-Control", "no-cache");
  response.addDateHeader("Expires", 1);
  MerchantLoginBean mlb=new MerchantLoginBean();
  ArrayList arrayShow = mlb.get_MsgBoard("M");
  Hashtable userinfo=(Hashtable)session.getAttribute("SYSCONFDATA");
  Hashtable merchantinfo=(Hashtable)userinfo.get("MERCHANT_USER");
  String MerchantID = (String)merchantinfo.get("MERCHANT_ID");
  String UserID = (String)merchantinfo.get("USER_ID");
  String UserName = (String)merchantinfo.get("USER_NAME");

  Hashtable merinfo=(Hashtable)userinfo.get("MERCHANT_USER");
  String MerchantName = (String)merinfo.get("MERCHANTCALLNAME");
  //allpay 新加subMid
  String submid= (String)merchantinfo.get("SUBMID");
  String subMerchantName = (String)merchantinfo.get("MERCHANTCALLNAME");
  String issubmerchant= (String)merchantinfo.get("ISSUBMERCHANT");
  String tempmerchant = MerchantID.substring(2,MerchantID.length()-1);
  boolean showsubmid=false;
  if(issubmerchant.equals("N")){
	  //單一貨母特店
  }else{
	  showsubmid=true;
  }

%>
<html>

<head>
 <meta http-equiv="Content-Type" content="text/html" charset=big5">
 <link href="css/style.css" type="text/css" rel="stylesheet">
 <script language="javascript">
 function showFrame() {
   if (document.all.showFrame.value == "1"){
     parent.BodyFrameset.cols = "0,*";
     document.all.showFrame.value = "0"
   } else {
     parent.BodyFrameset.cols = "180,*";
     document.all.showFrame.value = "1"
   }
 }

 function Logout(flag){
   document.form.LogoutFlag.value = flag;
   document.form.submit();
 }

 </script>
</head>

<body topmargin="0" leftmargin="0">
 <input type="hidden" id="showFrame" name="showFrame1" value="1">

 <table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>

   <td width="320"><img src="images/logo.jpg"></td>

   <td>
    <marquee onmouseover="stop()" onmouseout="start()" scrollAmount="1" hspace="0" vspace="0" scrollDelay="120" direction="up" behavior="scroll" loop="INFINITE" width="400" bgColor="#ffffff" height="33">
     <div><%
       for (int i =0; i<arrayShow.size(); ++i) {
         Hashtable hashdata = (Hashtable)arrayShow.get(i);
         String Msg_Content = hashdata.get("MSG_CONTENT").toString();
         if (i==0) {
           %><font color="#ff0000">[重大訊息] 　</font><%
         } else {
           %><font color="#ff0000">　　　　　　</font><%
         }
         if (Msg_Content.indexOf("\n") == -1) {
           int len = 20;
           int Count = Msg_Content.length() / len;
           if ((Msg_Content.length() % len) > 0) Count++;
           for (int j=0; j<Count; ++j) {
             int Start = j * len;
             int End = (j * len)+ len;
             if (End > Msg_Content.length()) End = Msg_Content.length();
               String ShowData = Msg_Content.substring(Start, End);
               if (j>0) {
                 out.write("　　　　　　");
               }
               out.write(ShowData+"<br>");
           }
         } else {
           Msg_Content = Msg_Content.replaceAll("\n", "<br>　　　　　　");
           out.write(Msg_Content+"<br>");
         }
       }

       %>
     </div>
    </marquee>
   </td>

   <td align="right" valign="top">
    <img style="cursor:hand" src="images/tree_vis_1.jpg" onmouseover="this.src='images/tree_vis_2.jpg'" onmouseout="this.src='images/tree_vis_1.jpg'" onclick="showFrame();"><img style="cursor:hand" src="images/logout_1.jpg" onmouseover="this.src='images/logout_2.jpg'" onmouseout="this.src='images/logout_1.jpg'" onclick="Logout('Login');" ><img style="cursor:hand" src="images/sys_1.jpg" onmouseover="this.src='images/sys_2.jpg'" onmouseout="this.src='images/sys_1.jpg'" onclick="Logout('Close');">
   </td>

  </tr>
 </table>

 <table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
   <td background="images/banner_line1.jpg"><img src="images/banner_bg1.jpg"></td>
  </tr>
<!--  <tr>
   <td height="10" background="images/banner_underline.gif"></td>
  </tr>  -->
 </table>
 <table width="100%" cellpadding="0" cellspacing="0" border="0" bgcolor='#F0EFEC'>
  <tr>
    <td align="left" height="20">　特店代號：<%=MerchantID+"("+MerchantName+")"%></td>
    <td align="center" height="20"><%if(showsubmid){ %>　子特店代號：<%=submid+"("+subMerchantName+")"%><%} %></td>
    <td align="right" height="20">　使用者代號：<%=UserID%>(<%=UserName%>)</td>
  </tr>
</table>

<form id='form' name='form' method='post' target='_top' action="./MerchantLogoutCtl" >
<input type="hidden" name="LogoutFlag" id="LogoutFlag" value="">
</form>
</body>

</html>
