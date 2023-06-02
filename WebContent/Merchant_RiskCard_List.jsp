<%@page contentType="text/html; charset=Big5"%>
<%@page language="java" %>
<%@page import="java.util.*"%>
<%@page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%@page import="com.cybersoft.bean.UserBean" %>
<%
//************************************************************
// * <p>#File Name:   Merchant_RiskCard_List.jsp        </p>
// * <p>#Description:                  </p>
// * <p>#Create Date: 2020/6/24              </p>
// * <p>#Company:     cybersoft               </p>
// * <p>#Notice:                      </p>
// * @author      
// * @since       SPEC version
// * @version 
// * 2020/07/02 彥仲 202007070141-00 修正chrome瀏覽器無法風險卡異動的狀況
// ************************************************************/
  response.addHeader("Pragma", "No-cache");
  response.addHeader("Cache-Control", "no-cache");
  response.setDateHeader("Expires", 1);
  try {
    Hashtable userinfo=(Hashtable)session.getAttribute("SYSCONFDATA");
    Hashtable merchantinfo=(Hashtable)userinfo.get("MERCHANT_USER");
    String merchantid=String.valueOf(merchantinfo.get("MERCHANT_ID"));
    ArrayList arrayMsgcode = new ArrayList();
    if (session.getAttribute("RiskCardMsgCode") != null) {
      arrayMsgcode = (ArrayList)session.getAttribute("RiskCardMsgCode");
    }

    Hashtable sysinfo=(Hashtable)userinfo.get("SYSCONF");
    int PagePcs =  100;
    if (sysinfo.size()>0) {
      String strPagePcs = sysinfo.get("MER_PAGE_CUL").toString();
      if (strPagePcs.length()>0) {
        PagePcs = Integer.parseInt(strPagePcs);
      }
    }
    int nowPage = 0;
    ArrayList arrayList = new ArrayList();
    Hashtable hashRiskCardData = new Hashtable();
    if (session.getAttribute("RiskCardData") != null) {
      hashRiskCardData = (Hashtable)session.getAttribute("RiskCardData");
      if (hashRiskCardData==null) hashRiskCardData = new Hashtable();
      if (hashRiskCardData.size()>0) {
        arrayList = (ArrayList)hashRiskCardData.get("DATALIST");
        if (arrayList==null) arrayList = new ArrayList();
        String page_no = (String)hashRiskCardData.get("NOWPAGE");
        if (page_no.length()>0) nowPage = Integer.parseInt(page_no);
      }
    }
    int TotalPage = arrayList.size() / PagePcs ;
    if ((arrayList.size() % PagePcs) > 0) TotalPage++;
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html" >
  <link href="css/style.css" type="text/css" rel="stylesheet">
  <script type="text/javascript" language="javascript" src="./js/Vatrix.js"></script>
  <script type="text/javascript" language="JavaScript">
//   <!--
  function toSubmit() {
     var InputOrderID = "";
     var InputPan = "";
     var InputRisk_Degree = "";
     var InputMerInsUser = "";
     var InputMerUpdUser = "";
     var InputMerInsDate = "";
     var InputMerUpdDate = "";
     var count = 0;
     //objs=document.all.tags("input");
     objs = document.getElementsByTagName("input");
     for(var i=0 ; i<objs.length ; ++i){
        if(objs[i].type=="checkbox"){
           if(objs[i].checked==true){
             var CheckValue = objs[i].value;
             var arrayCheckValue = CheckValue.split(",");
             var SelectName = "document.form.Risk_Degree"+arrayCheckValue[0];
             var SelectValue = eval(SelectName).value;
             if (SelectValue.length>0) {
               if(count==0){
                 InputRisk_Degree = InputRisk_Degree + SelectValue;
                 InputOrderID = InputOrderID + arrayCheckValue[1];
                 InputPan = InputPan + arrayCheckValue[2];
                 InputMerInsUser = InputMerInsUser + arrayCheckValue[3];
                 InputMerInsDate = InputMerInsDate + arrayCheckValue[4];
                 InputMerUpdUser = InputMerUpdUser + arrayCheckValue[5];
                 InputMerUpdDate = InputMerUpdDate + arrayCheckValue[6];
               } else{
                 InputRisk_Degree = InputRisk_Degree + ","  + SelectValue;
                 InputOrderID = InputOrderID + "," + arrayCheckValue[1];
                 InputPan = InputPan + "," + arrayCheckValue[2];
                 InputMerInsUser = InputMerInsUser + "," + arrayCheckValue[3];
                 InputMerInsDate = InputMerInsDate + "," + arrayCheckValue[4];
                 InputMerUpdUser = InputMerUpdUser + "," + arrayCheckValue[5];

                 InputMerUpdDate = InputMerUpdDate + "," + arrayCheckValue[6];
               }
               count++;
             } else {
               alert("未選取風險等級");
               return void(0);
             }
           }
        }
     }
     if (InputOrderID.length == 0) {
        alert("未選取欲設定風險卡資料");
        return void(0);
     } else {
        document.form.InputRisk_Degree.value = InputRisk_Degree;
        document.form.InputOrderID.value = InputOrderID;
        document.form.InputPan.value = InputPan;
        document.form.InputMerInsUser.value = InputMerInsUser;
        document.form.InputMerUpdUser.value = InputMerUpdUser;
        document.form.InputMerInsDate.value = InputMerInsDate;
        document.form.InputMerUpdDate.value = InputMerUpdDate;
        document.form.btnSubmit.disabled = true;
        document.form.btnBack.disabled = true;
        document.form.submit();
     }
  }

  function toCheck(Count) {
     var CheckValue = false;
     var SelectName = "document.form.Risk_Degree"+Count;
     var SelectValue = eval(SelectName).value;
     if (SelectValue.length==0) {
        CheckValue = false;
     } else {
        CheckValue = true;
     }
     var CheckName = "document.form.Checkbox"+Count;
     eval(CheckName).checked = CheckValue;
  }

  function realsubmit(){
    document.form.Action.value = 'Query';
    document.form.submit();
  }

  function toBack(){
    document.form.Action.value = '';
    document.form.submit();
  }
//-->
</script>
</head>
<body>
<form id="form" name="form" method="post" action ="./MerchantRiskCardCtl" >
<input type="hidden" name="Action" id="Action" value="Check">
<input type="hidden" name="InputRisk_Degree" id="InputRisk_Degree" value="">
<input type="hidden" name="InputOrderID" id="InputOrderID" value="">
<input type="hidden" name="InputPan" id="InputPan" value="">
<input type="hidden" name="InputMerInsUser" id="InputMerInsUser" value="">
<input type="hidden" name="InputMerUpdUser" id="InputMerUpdUser" value="">
<input type="hidden" name="InputMerInsDate" id="InputMerInsDate" value="">
<input type="hidden" name="InputMerUpdDate" id="InputMerUpdDate" value="">
<%
String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);
out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
      <td align="left" valign="middle"><font size="3" color="#004E87"><b>特店風險卡維護</b></font></td>
  </tr>
</table>

<hr style="height:1px">
  <%if (arrayList.size()>0) { %>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr>
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="30" align="left"></td>
            <td align="right">總筆數：<%=arrayList.size()%>筆　
             <select name="page_no" id="page_no" onchange="realsubmit();">
             <%  for (int i=0; i<TotalPage; i++){%>
             <option value='<%=i%>' <%if (nowPage==i) out.write("selected");%> >第<%=(i+1)%>頁</option>
             <%  } %></select></td>
          </tr>
        </table>
     </td>
    </tr>
  </table>

  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
    <tr align='center' bgcolor="#F4F4FF" height="25">
      <td align='center' rowspan="2">序號</td>
      <td align='center' rowspan="2">選取</td>
      <td align='center' rowspan="2">特店指定單號</td>
      <td align='center' rowspan="2">信用卡卡號</td>
      <td align='center' rowspan="2">風險等級</td>
      <td align='center' colspan="1">特店新增人員</td>
      <td align='center' colspan="1">特店維護人員</td>
    </tr>
    <tr align='center' bgcolor='#F4F4FF' height="25">
      <td align='center'>特店新增時間</td>
      <td align='center'>特店維護時間</td>
    </tr>
    <%
    UserBean userbean = new UserBean();
    int count=0;
    int StartCnt = nowPage * PagePcs;
    for (int c=StartCnt; c<(StartCnt+PagePcs); ++c) {
      if (c >= arrayList.size()) break;
      count++;
      Hashtable hashData = (Hashtable)arrayList.get(c);
  %><tr align='center' bgcolor='#ffffff'  height="25">
       <td align='left' rowspan="2"><font color=""><%=count%></font></td>
       <td align='center' rowspan="2"><input type='checkbox' id='Checkbox<%=count%>' value='<%=count%>,<%=hashData.get("ORDERID").toString().trim()%>,<%=hashData.get("PAN").toString().trim()%>,<%=hashData.get("MER_INS_USER").toString()%>,<%=hashData.get("MER_INS_DATE").toString()%>,<%=hashData.get("MER_UPD_USER").toString()%>,<%=hashData.get("MER_UPD_DATE").toString()%>' ></td>
       <td align='left' rowspan="2"><font color=""><%=hashData.get("ORDERID").toString().trim()%></font></td>
       <td align='left' rowspan="2"><font color=""><%=userbean.get_CardStar(hashData.get("PAN").toString().trim(),9,2)%></font></td>
       <td align='center' rowspan="2"><font color=""><select name='Risk_Degree<%=count%>' id='Risk_Degree<%=count%>' onchange='toCheck("<%=count%>");'><%
       String Risk_Degree = hashData.get("RISK_DEGREE").toString().trim();
       if (Risk_Degree.length()==0) {
         %><option value=''>-----------------------------</option><%
       }
       for (int cc=0; cc<arrayMsgcode.size();++cc) {
         Hashtable hashMsgData = (Hashtable)arrayMsgcode.get(cc);
         String MsgCode = (String)hashMsgData.get("MSG_CODE");
         String MsgDesc = (String)hashMsgData.get("MSG_DESC");
         if (!MsgCode.equalsIgnoreCase("ALL")) {
         %><option value='<%=MsgCode%>' <% if (Risk_Degree.equalsIgnoreCase(MsgCode)) out.write("selected"); %> > <%=MsgCode+"-"+MsgDesc%></option><%
         }
       } %>
      </select></font></td>
       <td align='center' colspan="1"><font color=""><%=hashData.get("MER_INS_USER").toString()%></font></td>
       <td align='center' colspan="1"><font color=""><%=hashData.get("MER_UPD_USER").toString()%></font></td>
    </tr>
    <tr align='center' bgcolor='#ffffff' height="25">
      <td align='center'><font color=""><%=hashData.get("MER_INS_DATE").toString()%></font></td>
      <td align='center'><font color=""><%=hashData.get("MER_UPD_DATE").toString()%></font></td>
    </tr>
   <%} %>
  </table>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr>
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="30" align="left"><input type='button' value='風險卡異動' name='btnSubmit' id='btnSubmit' onclick="toSubmit();">　<input type='button' value='回查詢頁' name='btnBack' id='btnBack' onclick="toBack();"></td>
            <td align="right"></td>
          </tr>
        </table>
     </td>
    </tr>
  </table>
  <%} else {%>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="30" align="left"><input type='button' value='回查詢畫頁' name='btnSubmit' id='btnSubmit' onclick="toBack();"></td>
          </tr>
          <tr>
            <td height="30" align="left" bgcolor='#ffffff'><font color="#FF0000" size="3"><b>查無交易資料<b></font></td>
          </tr>

        </table>
    </tr>
  </table>
  <%} %>
</form>
</body>
</html>

<%
        } catch (Exception e) {
            request.setAttribute("errMsg",e.toString());
            request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
        }
%>
