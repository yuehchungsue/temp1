<%@ page contentType="text/html; charset=Big5" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="com.cybersoft.merchant.ctl.MerchantUserDataCtl" %>
<%@ page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%
try{
  //  ***************************************************************************
  //  * #File Name: 	 Merchant_UserDataUpdate.jsp
  //  * #Description:    the login interface
  //  * #Create Date:    2007-10-02
  //  * #Company:	 cybersoft
  //  * @author          Caspar Chen
  //  * @see
  //  * @since		 Java Standard V0.1
  //  * @version	 0.0.1    2007-10-02    Caspar Chen
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
      isSubMid =((String) hashMerUser.get("ISSUBMERCHANT")).equals("Y") ?  true : false;
      isSignMer = subMidList != null && subMidList.size() > 1 ? false :  true;
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
      <td align="left" valign="middle"><b><font color="#004E87" size="3">使用者管理-修改資料</font></b></td>
  </tr>
</table>

<hr style="height:1px">

    <%
    ArrayList arrayUserData = (ArrayList)session.getAttribute("arrayActionUserData");
    session.removeAttribute("arrayActionUserData");
    Hashtable hashUsetLoginStatus = (Hashtable)session.getAttribute("hashActionUserLogStatusData");
    session.removeAttribute("hashActionUserLogStatusData");
    %>
    <%
    String strUserId = "";
    String strUserName = "";
    String strDepName = "";
    String strUserStatus = "";
    String strUserStatusName = "";
    String strLstLoginData = "";
    String strUserUpdUser = "";
    String strUserUpdDate = "";
    String strSubMid="";
    if(arrayUserData != null && arrayUserData.size() > 0)
    {
      Hashtable hashData = (Hashtable)arrayUserData.get(0);
      strUserId = hashData.get("USER_ID") == null ? "&nbsp" :hashData.get("USER_ID").toString();
      strUserName = hashData.get("USER_NAME") == null ? "&nbsp" :hashData.get("USER_NAME").toString();
      strDepName = hashData.get("DEP_NAME") == null ? "&nbsp" :hashData.get("DEP_NAME").toString();
      strUserStatus = hashData.get("USER_STATUS") == null ? "&nbsp" :hashData.get("USER_STATUS").toString();
      strUserStatusName = strUserStatus;
      strLstLoginData = hashData.get("FST_LOGIN_DATE") == null ? "&nbsp" :hashData.get("FST_LOGIN_DATE").toString();
      strUserUpdUser = hashData.get("USER_UPD_ID") == null ? "&nbsp" :hashData.get("USER_UPD_ID").toString();
      strUserUpdDate = hashData.get("USER_UPDATE_DATE") == null ? "&nbsp" :hashData.get("USER_UPDATE_DATE").toString();
      strSubMid= hashData.get("SUBMID") == null ? "&nbsp" :hashData.get("SUBMID").toString();
      if(strUserUpdDate.length() >=19){
        strUserUpdDate = strUserUpdDate.substring(0,19);
      }
      if(strLstLoginData.length() >= 19){
        strLstLoginData = strLstLoginData.substring(0,19);
      }

      if("Y".equalsIgnoreCase(strUserStatusName)){
        if(hashUsetLoginStatus.containsKey(hashData.get("USER_ID"))){
          strUserStatusName = "已登入";
        }else{
          strUserStatusName = "未登入";
        }
      }else{
        strUserStatusName = MerchantUserDataCtl.getUserStatus(strUserStatusName);
      }
    }
      %>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
    <tr align='center'>
      <td bgcolor='#F4F4FF' width="20%">使用者代號(*)
      </td>
      <td bgcolor='#F4F4FF' width="30%">
      <%out.println(strUserId);%>
      </td>
      <td bgcolor='#F4F4FF' width="20%">使用者名稱(*)
      </td>
      <td bgcolor='#F4F4FF' width="30%" align='left'>
   <% if(boolck){
        out.print(strUserName);
      }else{%>
      <input type="text" name="userName" id="userName" maxlength="16" value="<%=strUserName%>">
    <%} %>
      </td>
    </tr>
    <tr align='center'>
      <td bgcolor='#F4F4FF'>部門名稱
      </td>
      <td bgcolor='#F4F4FF'  <% if(!isSignMer && !isSubMid){ %>    <%} else{%> colspan="3"    <%} %>  align='left'>
   <% if(boolck){
        out.print(strDepName);
      }else{%>
      <input type="text" name="depName" id="depName" size="60" maxlength="20" value="<%=strDepName%>">
     <%} %>
     
      </td>
            <% if(!isSignMer && !isSubMid){ %>  
		      <td bgcolor='#F4F4FF'>子特約商店
		      </td>
		      <td bgcolor='#F4F4FF'     align='left'>
		      <%
		      if(boolck){
		        out.print(request.getParameter("show") == null? "" : request.getParameter("show").equals("") ? strSessionMerchantId :  request.getParameter("show").replace(' ','(')  +")"  );
		      }else{%>
		        <select name="showSubMid" id="showSubMid"   onchange="selectSubMid(this)">
		    <% for(int i =0 ;  i< subMidList.size() ; i++) {
        				Hashtable content = (Hashtable) subMidList.get(i);
        				String name = content.get("MERCHANTCALLNAME").toString();
        				String value = content.get("SUBMID").toString();
        	%>
        		<option value="<%=value %>"  <%if(value.equals(strSubMid)){ %> selected  <%} %>><%=value+"  "+name %> </option>
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
      <td bgcolor='#F4F4FF'>
      <%out.println(strUserStatusName);%>
      </td>
      <td bgcolor='#F4F4FF'>最後一次登入時間
      </td>
      <td bgcolor='#F4F4FF'>
      <%out.println(strLstLoginData);%>
      </td>
    </tr>
    <tr align='center'>
      <td bgcolor='#F4F4FF'>維護日期
      </td>
      <td bgcolor='#F4F4FF'>
      <%out.println(strUserUpdDate);%>
      </td>
      <td bgcolor='#F4F4FF'>維護人員
      </td>
      <td bgcolor='#F4F4FF'>
      <%out.println(strUserUpdUser);%>
      </td>
    </tr>
 </table>
 <%
 String strUserDataRoleAction = (String)session.getAttribute("strUserDataRoleAction");
 ArrayList arrayUserAllRoleData = (ArrayList)session.getAttribute("arrayActionUserAllRoleData");
 session.removeAttribute("arrayActionUserAllRoleData");
 ArrayList arrayUserRoleData = (ArrayList)session.getAttribute("arrayActionUserRoleData");
 session.removeAttribute("arrayActionUserRoleData");
 %>
 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
   <tr align='center'>
     <td bgcolor='#F4F4FF'>群組設定
     </td>
   </tr>
 </table>
 <%
 ArrayList arrayViewRole = new ArrayList();//表格顯示資料-群組
 ArrayList arrayViewRoleData = new ArrayList();//表格顯示資料-群組功能

 Hashtable hashAllRoleId = new Hashtable();//全部-群組名稱(判斷用)
 Hashtable hashAllRoleData = new Hashtable();//全部-群組名稱資料
 ArrayList arrayAllRoleId = new ArrayList();//全部-群組名稱
 Hashtable hashAllAppRoll = new Hashtable();//全部-依群組名稱分類功能

 if(arrayUserAllRoleData != null && arrayUserAllRoleData.size() > 0)
 {
   for(int i=0;i<arrayUserAllRoleData.size();i++){
     Hashtable hashData = (Hashtable)arrayUserAllRoleData.get(i);
     String strRoleId = "";
     if(hashData.get("ROLEID") != null){
       strRoleId = String.valueOf(hashData.get("ROLEID"));
     }
     if(strRoleId.length() > 0 && hashAllRoleId.get(strRoleId) == null){
       arrayAllRoleId.add(hashData.get("ROLEID"));
       hashAllRoleId.put(hashData.get("ROLEID"),hashData.get("ROLEID"));
       hashAllRoleData.put(hashData.get("ROLEID"),hashData);
     }

     ArrayList arrayAppRoll = new ArrayList();
     if(hashAllAppRoll.get(strRoleId) == null){
       arrayAppRoll.add(hashData);
       hashAllAppRoll.put(strRoleId,arrayAppRoll);
     }else{
       arrayAppRoll = (ArrayList)hashAllAppRoll.get(strRoleId);
       arrayAppRoll.add(hashData);
       hashAllAppRoll.put(strRoleId,arrayAppRoll);
     }
   }
 }

 Hashtable hashAppRoll = new Hashtable();//個人-依群組名稱分類功能
 if(arrayUserRoleData != null && arrayUserRoleData.size() > 0)
 {
   for(int i=0;i<arrayUserRoleData.size();i++){
     Hashtable hashData = (Hashtable)arrayUserRoleData.get(i);
     if(hashData.get("ROLEID") != null){
       if(hashData.get("MENUID") != null){
         hashAppRoll.put(hashData.get("ROLEID").toString()+hashData.get("MENUID").toString(),"");
       }
     }
   }
 }

 //群組列表
 int intRolRun = 0;
 out.println("<table width='100%' border='0' cellspacing='1' cellpadding='1' bgcolor='#D8D8D8'>");
 for(int i=0;i<arrayAllRoleId.size();i++){
   String strRol = (String)arrayAllRoleId.get(i);
   Hashtable hasdData = (Hashtable)hashAllRoleData.get(strRol);
   String strRolName = hasdData.get("ROLENAME") == null ? "" : hasdData.get("ROLENAME").toString();
   String strViewData[] = new String[]{strRolName, strUserDataRoleAction, strRol};
   arrayViewRole.add(strViewData);
   ArrayList arrayData = (ArrayList)hashAllAppRoll.get(strRol);
   if(i == 0){
     intRolRun = arrayData.size();
   }else{
     if(intRolRun < arrayData.size()){
       intRolRun = arrayData.size();
     }
   }
 }

 Hashtable ad1 = new Hashtable(); //每一群組功能數
 Hashtable ad2 = new Hashtable(); //使用者設定的每一群組功能數

 for(int i=0;i<intRolRun;i++){
   String strData = "<tr align='center'>";
   for(int j=0;j<arrayAllRoleId.size();j++){
     String strRol = String.valueOf(arrayAllRoleId.get(j)).trim();
     ArrayList arrayAppRoll = (ArrayList)hashAllAppRoll.get(strRol);
     if(ad1.get(strRol) == null){
       ad1.put(strRol,"1");
     }else{
       ad1.put(strRol,ad1.get(strRol) + "1");
     }
     if(i < arrayAppRoll.size()){
       Hashtable hashData = (Hashtable)arrayAppRoll.get(i);
       String strParentId = String.valueOf(hashData.get("PARENTID")).trim();
       String strMenuName = (String)hashData.get("MENUNAME");
       String strMenuId = String.valueOf(hashData.get("MENUID")).trim();
       strData += "<td bgcolor='#F4F4FF' align='left'>";
       if(hashAppRoll.get(strRol+strMenuId) == null){
         strData += "<input type='checkbox' id='"+strRol+"' name='"+strUserDataRoleAction+","+strRol+","+strMenuId+"' "+strDis+" >"+strMenuName;
       }else{
         if(ad2.get(strRol) == null){
           ad2.put(strRol,"1");
         }else{
           ad2.put(strRol,ad2.get(strRol) + "1");
         }
         strData += "<input type='checkbox' id='"+strRol+"' name='"+strUserDataRoleAction+","+strRol+","+strMenuId+"' checked='checked' "+strDis+">"+strMenuName;
       }
       strData += "</td>";
     }else{
       strData += "<td bgcolor='#F4F4FF' align='left'>";
       strData += "&nbsp;";
       strData += "</td>";
     }
   }
   strData += "</tr>";
   arrayViewRoleData.add(strData);
 }

//顯示功能
 out.println("<tr align='center'>");
 for(int i=0;i<arrayViewRole.size();i++){
   out.println("<td bgcolor='#F4F4FF' align='left'>");
   String strViewName[] = (String[])arrayViewRole.get(i);

     String lk = String.valueOf(ad1.get(strViewName[2]));
     String tk = String.valueOf(ad2.get(strViewName[2]));
     if(lk.length() > 0 && tk.length() > 0 && lk.length() == tk.length()){
          out.println(strViewName[0]+"<input type='checkbox' name='"+strViewName[1]+","+strViewName[2]+",' checked='checked' onclick=\"chkall('form',this,'"+strViewName[2]+"' )\" "+strDis+">");
     }else{
          out.println(strViewName[0]+"<input type='checkbox' name='"+strViewName[1]+","+strViewName[2]+",' onclick=\"chkall('form',this,'"+strViewName[2]+"')\" "+strDis+">");
     }

   out.println("</td>");
 }
 out.println("</tr>");

 for(int i=0;i<arrayViewRoleData.size();i++){
   out.println(arrayViewRoleData.get(i));
 }

 out.println("</table>");
 %>
 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
   <tr align='center'>
     <td bgcolor='#F4F4FF'>
     <%if(!boolck){%>
     <input type='button' value='確定' name='btnQuery' id='btnQuery' onclick="Query('UPDATEDATA')" />&nbsp;
     <%if(!"E".equals(strUserStatus)){ %>
     <input type='button' value='密碼重置' name='btnQuery' id='btnQuery' onclick="Query('REUSERSTATUS')" />&nbsp;
     <input type='button' value='停用' name='btnQuery' id='btnQuery' onclick="Query('DEACTIVATE')" />&nbsp;
     <%}%>
     <%if("E".equals(strUserStatus)){ %>
     <input type='button' value='解除停用' name='btnQuery' id='btnQuery' onclick="Query('REDEACTIVATE')" />&nbsp;
     <%}%>
     <%}%>
        <input type='button' value='關閉' name='btnQuery' id='btnQuery' onclick="Close('LIST')" />
     </td>
   </tr>
 </table>
 <input type="hidden" name="userIdData" id="userIdData" value="<%=strUserId%>" />
 <input type="hidden" name="actionTypeCk" id="actionTypeCk" value="" />
 <input type="hidden" name="actionType" id="actionType" value="" />
 <%
 String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);
 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>
</form>

<script language="JavaScript" type="text/JavaScript">
<!--
<%if(!boolck){%>
document.form.userName.focus();
<%}%>
<%if (loginmsg.length()>0) { %>
toShowMessage();
function toShowMessage() {
  alert("<%=loginmsg%>");
  loginmsg = '';
  return void(0);
}
<%}%>

function Query(data)
{
  document.form.actionType.value = 'UPDATEDATA';
  document.form.actionTypeCk.value = data;
  if('UPDATEDATA' == data){
    var leUserName = document.form.userName.value.length;

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
  }else if('REUSERSTATUS' == data){
  }else if('REDEACTIVATE' == data){
  }else if('DEACTIVATE' == data){
  }else{
    return void(0);
  }

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
