<%@ page contentType="text/html; charset=Big5" %>
<%@ page import="java.util.*" %>
<%@ page import="com.cybersoft.merchant.ctl.MerchantUserDataCtl" %>
<%@ page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%
try{
  //  ***************************************************************************
  //  * #File Name: 	 Merchant_UserDataAdd.jsp
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

  //頁面是否可修改
  String strActionUserView = "";
  if (session.getAttribute("strActionUserView") != null) {
    strActionUserView = (String) session.getAttribute("strActionUserView");
    session.removeAttribute("strActionUserView");
  }
  boolean boolck = false;
  String strDis = "";
  if("1".equals(strActionUserView)){
    boolck = true;
    strDis = "disabled";
  }

  //取session資料
  String strSessionMerchantId = "";
  String strSessionUserId = "";
  String strSessionMerchantName="";
  //子特店清單
  ArrayList subMidList = new ArrayList();
 //是否為子特店
 boolean isSubMid =false;
 //是否為單一特店
 boolean isSignMer = false;
 
  java.util.Hashtable hashConfData = (java.util.Hashtable) session.getAttribute("SYSCONFDATA");

  if (hashConfData != null && hashConfData.size() > 0)
  {
	subMidList = (ArrayList) hashConfData.get("SUBMID");
    java.util.Hashtable hashMerUser = (java.util.Hashtable) hashConfData.get("MERCHANT_USER"); // 使用者資料
    // 使用者資料
    if (hashMerUser != null)
    {
      if (hashMerUser.get("MERCHANT_ID") != null)
      {
        strSessionMerchantId = hashMerUser.get("MERCHANT_ID").toString();
        strSessionMerchantName=hashMerUser.get("MERCHANTCALLNAME").toString();
      }
      if (hashMerUser.get("USER_ID") != null)
      {
        strSessionUserId = hashMerUser.get("USER_ID").toString();
      }
    }
    isSubMid =((String) hashMerUser.get("ISSUBMERCHANT")).equals("Y") ?  true : false;
    isSignMer = subMidList != null && subMidList.size() > 0 ? false :  true;
  }

  //action 送出的資料
  String userId = "";
  Hashtable hashActionUserData = new Hashtable();
  if (session.getAttribute("hashActionUserData") != null) {
    hashActionUserData = (Hashtable) session.getAttribute("hashActionUserData");
    session.removeAttribute("hashActionUserData");
    if(hashActionUserData.get("userId") != null){
      userId = hashActionUserData.get("userId").toString();
    }
  }

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
      <td align="left" valign="middle"><b><font color="#004E87" size="3">使用者管理-新增資料</font></b></td>
  </tr>
</table>

<hr style="height:1px">

  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
    <tr align='center'>
      <td bgcolor='#F4F4FF' width="20%">使用者代號(*)
      </td>
      <td bgcolor='#F4F4FF' width="30%" align='left'>
      <%
      if(boolck){
        out.print(request.getParameter("userIdData") == null?"":request.getParameter("userIdData"));
      }else{%>
        <input type="text" name="userIdData" id="userIdData" maxlength="8" value="<%=request.getParameter("userIdData") == null?"":request.getParameter("userIdData")%>">
      <%}%>
      </td>
      <td bgcolor='#F4F4FF' width="20%">使用者名稱(*)
      </td>
      <td bgcolor='#F4F4FF' width="30%" align='left'>
      <%
      if(boolck){
        out.print(request.getParameter("userName") == null?"":request.getParameter("userName"));
      }else{%>
        <input type="text" name="userName" id="userName" maxlength="16" value="<%=request.getParameter("userName") == null?"":request.getParameter("userName")%>">
      <%}%>
      </td>
    </tr>
    <tr align='center'>
      <td bgcolor='#F4F4FF'>部門名稱
      </td>
      <td bgcolor='#F4F4FF'    <% if(!isSignMer && !isSubMid){ %>    <%} else{%> colspan="3"    <%} %> align='left'>
      <%
      if(boolck){
        out.print(request.getParameter("depName") == null?"":request.getParameter("depName"));
      }else{%>
        <input type="text" name="depName" id="depName" size="60" maxlength="20" value="<%=request.getParameter("depName") == null?"":request.getParameter("depName")%>">
      <%}%>

      </td>
      <% if(!isSignMer && !isSubMid){ %>  
		      <td bgcolor='#F4F4FF'>子特約商店
		      </td>
		      <td bgcolor='#F4F4FF'     align='left'>
		      <%
		      if(boolck){
		        out.print(request.getParameter("show") == null? "" : request.getParameter("show").equals("") ? strSessionMerchantId :  request.getParameter("show").replace(' ','(')  +")" );
		      }else{%>
		        <select name="showSubMid" id="showSubMid"   onchange="selectSubMid(this)">
		    <% for(int i =0 ;  i< subMidList.size() ; i++) {
        				Hashtable content = (Hashtable) subMidList.get(i);
        				String name = content.get("MERCHANTCALLNAME").toString();
        				String value = content.get("SUBMID").toString();
        	%>
        		<option value="<%=value %>"><%=value+"  "+name %></option>
        	<%} %>
		        </select>
		        <input type="hidden" name="subMid" id="subMid"   value="<%=strSessionMerchantId.substring(strSessionMerchantId.length()-13) %>"/>
		        <input type="hidden" name="show" id="show"   value="<%=strSessionMerchantId.substring(strSessionMerchantId.length()-13)+" "+strSessionMerchantName %>"/>
     		 <%}%>

      </td>
        <%} %>
    </tr>
    <tr align='center'>
      <td bgcolor='#F4F4FF'>狀態
      </td>
      <td bgcolor='#F4F4FF'><%=MerchantUserDataCtl.getUserStatus("O")%>
      </td>
      <td bgcolor='#F4F4FF'>最後一次登入時間
      </td>
      <td bgcolor='#F4F4FF'>
      </td>
    </tr>
    <tr align='center'>
      <td bgcolor='#F4F4FF'>異動日期
      </td>
      <td bgcolor='#F4F4FF'>
        <%out.println(new java.text.SimpleDateFormat("yyyy/MM/dd").format(new java.util.Date()));%>
        <%//out.println(new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date()));%>
      </td>
      <td bgcolor='#F4F4FF'>異動人員
      </td>
      <td bgcolor='#F4F4FF'>
      <%out.println(strSessionUserId);%>
      </td>
    </tr>
 </table>
 <%
 String strUserDataRoleAction = (String)session.getAttribute("strUserDataRoleAction");
 ArrayList arrayUserAllRoleData = (ArrayList)session.getAttribute("arrayActionUserAllRoleData");
 session.removeAttribute("arrayActionUserAllRoleData");
 %>
 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
   <tr align='center'>
     <td bgcolor='#F4F4FF'>群組設定
     </td>
   </tr>
 </table>
 <%
 Hashtable hashRoleId = new Hashtable();//群組名稱(判斷用)
 Hashtable hashRoleData = new Hashtable();//群組名稱資料
 ArrayList arrayRoleId = new ArrayList();//群組名稱
 Hashtable hashAppRoll = new Hashtable();//依群組名稱分類功能
 if(arrayUserAllRoleData != null && arrayUserAllRoleData.size() > 0)
 {
   for(int i=0;i<arrayUserAllRoleData.size();i++){
     Hashtable hashData = (Hashtable)arrayUserAllRoleData.get(i);
     String strRoleId = "";
     if(hashData.get("ROLEID") != null){
       strRoleId = String.valueOf(hashData.get("ROLEID"));
     }
     if(strRoleId.length() > 0 && hashRoleId.get(strRoleId) == null){
       arrayRoleId.add(hashData.get("ROLEID"));
       hashRoleId.put(hashData.get("ROLEID"),hashData.get("ROLEID"));
       hashRoleData.put(hashData.get("ROLEID"),hashData);
     }

     ArrayList arrayAppRoll = new ArrayList();
     if(hashAppRoll.get(strRoleId) == null){
       arrayAppRoll.add(hashData);
       hashAppRoll.put(strRoleId,arrayAppRoll);
     }else{
       arrayAppRoll = (ArrayList)hashAppRoll.get(strRoleId);
       arrayAppRoll.add(hashData);
       hashAppRoll.put(strRoleId,arrayAppRoll);
     }
   }
 }


 //群組列表
 int intRolRun = 0;
 out.println("<table width='100%' border='0' cellspacing='1' cellpadding='1' bgcolor='#D8D8D8'>");
 out.println("<tr align='center'>");
 for(int i=0;i<arrayRoleId.size();i++){
   String strRol = (String)arrayRoleId.get(i);
   Hashtable hasdData = (Hashtable)hashRoleData.get(strRol);
   out.println("<td bgcolor='#F4F4FF' align='left'>");
   if(request.getParameter(strUserDataRoleAction+","+strRol+",") != null){
     out.println(hasdData.get("ROLENAME")+"<input type='checkbox' name='"+strUserDataRoleAction+","+strRol+",' onclick=\"chkall('form',this,'"+strRol+"')\" checked "+strDis+">");
   }else{
     out.println(hasdData.get("ROLENAME")+"<input type='checkbox' name='"+strUserDataRoleAction+","+strRol+",' onclick=\"chkall('form',this,'"+strRol+"')\" "+strDis+">");
   }
   out.println("</td>");
   ArrayList arrayData = (ArrayList)hashAppRoll.get(strRol);
   if(i == 0){
     intRolRun = arrayData.size();
   }else{
     if(intRolRun < arrayData.size()){
       intRolRun = arrayData.size();
     }
   }
 }
 out.println("</tr>");
 for(int i=0;i<intRolRun;i++){
   out.println("<tr align='center'>");
   for(int j=0;j<arrayRoleId.size();j++){
     String strRol = (String)arrayRoleId.get(j);
     ArrayList arrayAppRoll = (ArrayList)hashAppRoll.get(strRol);
     if(i < arrayAppRoll.size()){
       Hashtable hashData = (Hashtable)arrayAppRoll.get(i);
       String strParentId = String.valueOf(hashData.get("PARENTID"));
       String strMenuName = (String)hashData.get("MENUNAME");
       String strMenuId = (String)hashData.get("MENUID");
       out.println("<td bgcolor='#F4F4FF' align='left'>");
       if(request.getParameter(strUserDataRoleAction+","+strRol+","+strMenuId) != null){
         out.println("<input type='checkbox' id='"+strRol+"' name='"+strUserDataRoleAction+","+strRol+","+strMenuId+"' checked "+strDis+">"+strMenuName);
       }else{
         out.println("<input type='checkbox' id='"+strRol+"' name='"+strUserDataRoleAction+","+strRol+","+strMenuId+"' "+strDis+">"+strMenuName);
       }
       out.println("</td>");
     }else{
       out.println("<td bgcolor='#F4F4FF' align='left'>");
       out.println("&nbsp;");
       out.println("</td>");
     }
   }
   out.println("</tr>");
 }
 out.println("</table>");

 %>
 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
   <tr align='center'>
     <td bgcolor='#F4F4FF'>
     <%if(!boolck){%>
     <input type='button' value='確定' name='btnQuery' id='btnQuery' onclick="Query()" />
     <%}%>
     <input type='button' value='關閉' name='btnQuery' id='btnQuery' onclick="Close('LIST')" />
     </td>
   </tr>
 </table>
 <input type="hidden" name="actionType" id="actionType" value="" />
 <%
 String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);
 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>
</form>

<script language="JavaScript" type="text/JavaScript">
<!--
<%if(!boolck){%>
document.form.userIdData.focus();
<%}%>
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
  var leUserId = document.form.userIdData.value.length;
  var leUserName = document.form.userName.value.length;

  if(leUserId == 0){
    document.form.userIdData.focus();
    alert("使用者代號欄位未輸入");
    return void(0);
  }
  if(leUserName == 0){
    document.form.userName.focus();
    alert("使用者名稱欄位未輸入");
    return void(0);
  }

  if(true){
    var boolChecked = false;
    var boolNameConCk = false;
    var boolNameCon;
    var objForm = document.form;
    var objLen = objForm.length;
    for (var iCount = 0; iCount < objLen; iCount++)
    {
      if (objForm.elements[iCount].type == "checkbox")
      {
        if(!boolNameConCk){
          boolNameCon = iCount;
          boolNameConCk = true;
        }
        if(objForm.elements[iCount].checked == true)
        {
          boolChecked = true;
          break;
        }
      }
    }
    if(!boolChecked){
      objForm.elements[boolNameCon].focus();
      alert("您未賦予使用者權限，請確認!");
      return void(0);
    }
  }

  document.form.actionType.value = 'ADDDATA';
  document.form.submit();
}

function Close(data)
{
  if(data == 'LIST'){
    document.form.actionType.value=data;
    document.form.submit();
  }
  return void(0);
}

function chkall(input1,input2, key)
{
  var objForm = document.forms[input1];
  var objLen = objForm.length;
  for (var iCount = 0; iCount < objLen; iCount++)
  {
    if(key == objForm.elements[iCount].id){
      if (input2.checked == true)
      {
        if (objForm.elements[iCount].type == "checkbox")
        {
          objForm.elements[iCount].checked = true;
        }
      }
      else
      {
        if (objForm.elements[iCount].type == "checkbox")
        {
          objForm.elements[iCount].checked = false;
        }
      }
    }
  }
}

function selectSubMid(obj){
	document.form.subMid.value = obj.value;
	document.form.show.value = obj.options[obj.selectedIndex].text;
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
