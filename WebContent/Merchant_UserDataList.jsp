<%@ page contentType="text/html; charset=Big5" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="com.cybersoft.merchant.ctl.MerchantUserDataCtl" %>
<%@ page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%
try{
  //  ***************************************************************************
  //  * #File Name: 	 Merchant_UserDataList.jsp
  //  * #Description:    the login interface
  //  * #Create Date:    2007-10-01
  //  * #Company:	 cybersoft
  //  * @author          Caspar Chen
  //  * @see
  //  * @since		 Java Standard V0.1
  //  * @version	 0.0.1    2007-10-01    Caspar Chen
  //  ***************************************************************************
//  response.addHeader("Pragma", "No-cache");
//  response.addHeader("Cache-Control", "no-cache");
//  response.addDateHeader("Expires", 1);

  String loginmsg = "";
  if (session.getAttribute("Message") != null) {
    loginmsg = (String) session.getAttribute("Message");
    session.removeAttribute("Message");
  }

  //action 送出的資料
  String userIdList = "";
  if (session.getAttribute("strActionUserIdList") != null) {
    userIdList = session.getAttribute("strActionUserIdList").toString();
  }

  //登入使用者ID
  String loginUserId = "";
  //子特店清單
   ArrayList subMidList = new ArrayList();
  //是否為子特店
  boolean isSubMid =false;
  //是否為單一特店
  boolean isSignMer = false;
  Hashtable hashConfData = (Hashtable) session.getAttribute("SYSCONFDATA");
  if(hashConfData != null){
    Hashtable hashMerUser = (Hashtable) hashConfData.get("MERCHANT_USER");
    subMidList = (ArrayList) hashConfData.get("SUBMID");
    if (hashMerUser != null && hashMerUser.get("USER_ID") != null)
    {
      loginUserId = hashMerUser.get("USER_ID").toString().trim();
    }
    isSubMid =((String) hashMerUser.get("ISSUBMERCHANT")).equals("Y") ?  true : false;
    isSignMer = subMidList != null && subMidList.size() > 1 ? false :  true;
  }
  String strSubMid = request.getParameter("subMid")==null?"" : request.getParameter("subMid").toString();
%>
<html>
<head>
 <meta http-equiv="Content-Type" content="text/html charset=big5" >
 <link href="css/style.css" type="text/css" rel="stylesheet">
</head>

<body>
<form name="form" id="form" method="post" action="./MerchantUserDataCtl">

<script language="JavaScript" src="js/calendar.js" type=""></script>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg" alt=""></td>
      <td align="left" valign="middle"><b><font color="#004E87" size="3">使用者管理</font></b></td>
  </tr>
</table>

<hr style="height:1px">

  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
    <tr align='center'>
      <td width="40%" bgcolor='#F4F4FF' align="right">使用者代號
        <input type="text" name="userIdList" id="userIdList" value="<%=userIdList%>" size="20" maxlength="8">

      </td>
      <td width="60%" align='left' bgcolor='#F4F4FF'>
      <% if(isSignMer || isSubMid){ %>
       		 <input type='button' value='查詢' name='btnQuery' id='btnQuery' onclick="Query('SELECT')" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <input type='button' value='新增' name='btnQuery' id='btnQuery' onclick="Query('ADD')" />
        <input type='button' value='關閉' name='btnQuery' id='btnQuery' onclick="Close('CLS')" />
        <%} %>
      </td>
    </tr>
    <% if(!isSignMer && !isSubMid){ %>
     <tr align='right'>
      <td width="40%" bgcolor='#F4F4FF'   >子特約商店
        <select id="subMid" name="subMid">
        	<option value="all">全部</option>
        	<% for(int i =0 ;  i< subMidList.size() ; i++) {
        				Hashtable content = (Hashtable) subMidList.get(i);
        				String name = content.get("MERCHANTCALLNAME").toString();
        				String value = content.get("SUBMID").toString();
        	%>
        		<option value="<%=value %>" <%if(value.equals(strSubMid)){ %> selected  <%} %>><%=value+"  "+name %></option>
        	<%} %>
        </select>
        
        
      </td>
      <td width="60%" align='left' bgcolor='#F4F4FF'>
        <input type='button' value='查詢' name='btnQuery' id='btnQuery' onclick="Query('SELECT')" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <input type='button' value='新增' name='btnQuery' id='btnQuery' onclick="Query('ADD')" />
        <input type='button' value='關閉' name='btnQuery' id='btnQuery' onclick="Close('CLS')" />
      </td>
    </tr>
    <%} %>
  </table>

  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
    <%
    ArrayList arrayUserData = (ArrayList)session.getAttribute("arrayActionUserData");
    session.removeAttribute("arrayActionUserData");
    Hashtable hashUsetLoginStatus = (Hashtable)session.getAttribute("hashActionUserLogStatusData");
    session.removeAttribute("hashActionUserLogStatusData");
    %>
    <%
    if(arrayUserData != null && arrayUserData.size() > 0)
    {
      %>
      <tr align='center'>
        <td align='center' bgcolor='#F4F4FF'>編輯</td>
        <td align='center' bgcolor='#F4F4FF'>使用者代號</td>
        <td align='center' bgcolor='#F4F4FF'>使用者名稱</td>
        <% if(!isSignMer && !isSubMid){ %>
        <td align='center' bgcolor='#F4F4FF'>子特約商店</td>
        <%} %>
        <td align='center' bgcolor='#F4F4FF'>最後一次登入時間</td>
        <td align='center' bgcolor='#F4F4FF'>部門名稱</td>
        <td align='center' bgcolor='#F4F4FF'>維謢人員</td>
        <td align='center' bgcolor='#F4F4FF'>狀態</td>
      </tr>
      <%
      boolean boolUid = false;
      String uId = "";      //使用者代號
      String uName = "";    //使用者名稱
      String llDate = "";   //最後一次登入時間
      String dName = "";    //部門名稱
      String updId = "";    //維謢人員
      String uStatus = "";
      String ac_add = "";   //管理者權限
      String subMid = "";//子特店帳號
      String subMidName = "";//子特店名稱
      String tempSubMid = "";
      for(int i=0;i<arrayUserData.size();i++){
        Hashtable hashData = (Hashtable)arrayUserData.get(i);
        if(hashData != null){
          if(hashData.get("USER_ID") != null){
            uId = hashData.get("USER_ID").toString();
            if(uId.length() > 0){
              boolUid = true;
            }
          }
          if(hashData.get("USER_NAME") != null){
            uName = hashData.get("USER_NAME").toString();
            if(uName.length() == 0){
              uName = "&nbsp;";
            }
          }
          if(hashData.get("DEP_NAME") != null){
            dName = hashData.get("DEP_NAME").toString();
            if(dName.length() == 0){
              dName = "&nbsp;";
            }
          }
          if(hashData.get("FST_LOGIN_DATE") != null){
            llDate = hashData.get("FST_LOGIN_DATE").toString();
            if(llDate.length() == 0){
              llDate = "&nbsp;";
            }else{
              if(llDate.length() >= 19){
                llDate = llDate.substring(0,19);
              }
            }
          }
          if(hashData.get("USER_UPD_ID") != null){
            updId = hashData.get("USER_UPD_ID").toString();
            if(updId.length() == 0){
              updId = "&nbsp;";
            }
          }
          if(hashData.get("USER_STATUS") != null){
            uStatus = hashData.get("USER_STATUS").toString();
            if(uStatus.length() == 0){
              uStatus = "&nbsp;";
            }else{
              if("Y".equalsIgnoreCase(uStatus)){
                if(hashUsetLoginStatus.containsKey(hashData.get("USER_ID"))){
                  uStatus = "已登入";
                }else{
                  uStatus = "未登入";
                }
              }else{
                uStatus = MerchantUserDataCtl.getUserStatus(uStatus);
              }
            }
          }
          if(hashData.get("AC_ADD") != null){
            ac_add = hashData.get("AC_ADD").toString();
            if(ac_add.length() == 0){
              ac_add = "&nbsp;";
            }
          }
          if(hashData.get("SUBMID") != null ){
        	  subMid = hashData.get("SUBMID").toString()+"("+ hashData.get("MERCHANTCALLNAME").toString()+")";
            }
          if(hashData.get("MERCHANT_ID") != null ){
        	  tempSubMid = hashData.get("MERCHANT_ID").toString().substring(hashData.get("MERCHANT_ID").toString().length()-13);
            }
          if(boolUid){
          %>
          <tr align='center'>
            <td align='center' bgcolor='#F4F4FF'>
              <%
              boolean boolViewBtn = true;
              if("Y".equalsIgnoreCase(ac_add)){
                boolViewBtn = false;
              }
              if(loginUserId.equals(uId)){
                boolViewBtn = false;
              }
              if(boolViewBtn){%>
                <input type='button' value='修改' name='btnQuery' id='btnQuery' onclick="UpdateUser('UPDATE','<%=uId%>')" />
                <input type='button' value='刪除' name='btnQuery' id='btnQuery' onclick="UpdateUser('DELETEDATA','<%=uId%>')" />
              <%}%>
              </td>
              <td align='left' bgcolor='#F4F4FF'><%=uId%></td>
              <td align='left' bgcolor='#F4F4FF'><%=uName%></td>
              <% if(!isSignMer && !isSubMid){ %>
              <td align='left' bgcolor='#F4F4FF'><%= subMid%></td>
              <%} %>
              <td align='left' bgcolor='#F4F4FF'><%=llDate%></td>
              <td align='left' bgcolor='#F4F4FF'><%=dName%></td>
              <td align='left' bgcolor='#F4F4FF'><%=updId%></td>
              <td align='left' bgcolor='#F4F4FF'><%=uStatus%></td>
            </tr>
            <%
            }
          }
        }
      } else {
        if (userIdList.length()>0 ) {
        %>
        <table width="100%">
           <tr><td><b><font color="#FF0000" size="3">查無使用者資料!</font></b></td></tr>
        </table>
    <%  }
  } %>
    <input type="hidden" name="userIdData" id="userIdData" value="" />
    <input type="hidden" name="actionTypeCk" id="actionTypeCk" value="" />
    <input type="hidden" name="actionType" id="actionType" value="" />
    <%
    String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);
    out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
    %>
 </table>
</form>

<script language="JavaScript" type="text/JavaScript">
<!--
document.form.userIdList.focus();
<%if (loginmsg.length()>0) { %>
toShowMessage();
function toShowMessage() {
  alert("<%=loginmsg%>");
  loginmsg = '';
  return void(0);
}
<%}%>

function UpdateUser(data,ui)
{
  if('DELETEDATA' == data){
    if(!window.confirm('是否確定刪除資料')){
      return void(0);
    }
  }
  document.form.userIdData.value=ui;
  Query(data);
}

function Query(data)
{

  if(data == 'SELECT'){
    if(document.form.userIdList.value.length == 0){
      document.form.actionTypeCk.value='ALL';
    }else{
      document.form.actionTypeCk.value='USER';
    }
  }else if(data == 'ADD'){
  }else if(data == 'UPDATE'){
  }else if(data == 'DELETEDATA'){
  }else{
    return void(0);
  }
  document.form.actionType.value=data;
  document.form.submit();
}

function Close(data)
{
  if(data == 'CLS'){
    document.form.actionType.value=data;
    document.form.submit();
  }
  return void(0);
}
//-->
</script>
</body>

</html>
<%
        } catch (Exception e) {
            e.printStackTrace();
         //   request.setAttribute("errMsg",e.toString());
           // request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
        }
%>

