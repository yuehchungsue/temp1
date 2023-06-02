<%@page contentType="text/html;charset=BIG5"%>
<%@page import="java.util.*" %>
<%@page import="java.text.*"%>
<%@page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%@page import="com.cybersoft.bean.UserBean" %>
<%
  try {
  response.addHeader("Pragma", "No-cache");
  response.addHeader("Cache-Control", "no-cache");
  response.setDateHeader("Expires", 0);


  Hashtable hashMsgBoardData =  new Hashtable();
  ArrayList arrayDocDownload = new ArrayList();
  ArrayList arrayMerchantDownload = new ArrayList();
  UserBean UserBean = new UserBean();
  if (request.getAttribute("DocDownload") != null) {
     arrayDocDownload = (ArrayList)request.getAttribute("DocDownload");
  }
  if (request.getAttribute("MerchantDownload") != null) {
     arrayMerchantDownload = (ArrayList)request.getAttribute("MerchantDownload");
  }
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html" >
  <link href="css/style.css" type="text/css" rel="stylesheet">
  <script type="text/javascript" language="javascript" src="./js/Vatrix.js"></script>
  <script type="text/javascript" language="JavaScript">
  <!--

//-->
</script>
</head>
<body>
<form id="form" name="form" method="post" action ="./MerchantMsgBoardCtl" >
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
      <td align="left" valign="middle"><font size="3" color="#004E87"><b>特約商店相關文件下載</b></font></td>
  </tr>
</table>
<hr style="height:1px">
<%if (arrayDocDownload.size()>0) { %>
<table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
  <tr align='center' bgcolor="#F4F4FF" height="25">
    <td align='left' colspan="10"><B> <img src="images/sub_title_icon.jpg"><img src="images/sub_title_icon.jpg"> 公開文件清單</B></td>
  </tr>
  <tr align='center' bgcolor="#F4F4FF" height="25">
    <td align='center' width="20%" >序號</td>
    <td align='center' width="80%">檔案名稱</td>
  </tr>
<% int count=0;
  for (int c=0; c<arrayDocDownload.size(); ++c) {
      count++;
      Hashtable hashData = (Hashtable)arrayDocDownload.get(c);
      String DisplayFileName =  hashData.get("DISPLAY_FILENAME").toString();
      String VirtualPath =  hashData.get("VIRTUAL_PATH").toString();
      String RealFileName =  hashData.get("REAL_FILENAME").toString();
%><tr align='center' bgcolor='#ffffff'  height="25">
    <td align='left' ><font color=""><%=count%></font></td>
    <td align='left'><font color=""><a href="<%=VirtualPath+RealFileName%>"><%=DisplayFileName%></a></font></td>
  </tr>
<%} %>
</table>
<%} else { %>
<table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
  <tr align='center' bgcolor="#F4F4FF" height="25">
    <td align='left' ><B> <img src="images/sub_title_icon.jpg"><img src="images/sub_title_icon.jpg"> 公開文件清單</B></td>
  </tr>
  <tr align='center' bgcolor='#ffffff'  height="25">
    <td align='left'><font color="#FF0000">無公開文件資料</font></td>
  </tr>
</table>
<%} %>
<br>
<%if (arrayMerchantDownload.size()>0) { %>
<table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
  <tr align='center' bgcolor="#F4F4FF" height="25">
    <td align='left' colspan="10"><B> <img src="images/sub_title_icon.jpg"><img src="images/sub_title_icon.jpg"> 私有文件清單</B></td>
  </tr>
  <tr align='center' bgcolor="#F4F4FF" height="25">
    <td align='center' width="20%">序號</td>
    <td align='center' width="80%" >檔案名稱</td>
  </tr>
<% int count=0;
  for (int c=0; c<arrayMerchantDownload.size(); ++c) {
      count++;
      Hashtable hashData = (Hashtable)arrayMerchantDownload.get(c);
      String DisplayFileName =  hashData.get("DISPLAY_FILENAME").toString();
      String VirtualPath =  hashData.get("VIRTUAL_PATH").toString();
      String RealFileName =  hashData.get("REAL_FILENAME").toString();
%><tr align='center' bgcolor='#ffffff'  height="25">
    <td align='left'><font color=""><%=count%></font></td>
    <td align='left'><font color=""><a href="<%=VirtualPath+RealFileName%>"><%=DisplayFileName%></a></font></td>
  </tr>
<%} %>
</table>
<%} else { %>
<table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
  <tr align='center' bgcolor="#F4F4FF" height="25">
    <td align='left' ><B> <img src="images/sub_title_icon.jpg"><img src="images/sub_title_icon.jpg"> 私有文件清單</B></td>
  </tr>
  <tr align='center' bgcolor='#ffffff'  height="25">
    <td align='left'><font color="#FF0000">無私有文件資料</font></td>
  </tr>
</table>
<%} %>
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
