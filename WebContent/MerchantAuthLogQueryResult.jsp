<%@ page contentType="text/html; charset=Big5" %>
<%@ page language="java" %>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>
<%@ page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%
//  response.addHeader("Pragma", "No-cache");
//  response.addHeader("Cache-Control", "no-cache");
//  response.addDateHeader("Expires", 1);
try{
	boolean showflag=false;//false:超過 ture:不超過
    Enumeration key= request.getAttributeNames();
    while(key.hasMoreElements()){
    String keyname=String.valueOf(key.nextElement());
   // System.out.println(keyname+"="+request.getAttribute(keyname));
    }

    ArrayList authlog=(ArrayList)session.getAttribute("AuthLog");
    String error_message="./Merchant_Response.jsp";
    if(authlog==null||authlog.size()==0){
      session.setAttribute("Message", "查無資料");
      request.getRequestDispatcher(error_message).forward(request, response);
    }else{
    String checkpoint=request.getAttribute("checkpoint")!=null ? request.getAttribute("checkpoint").toString() : "";
    String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);
	int queryMax=3000;
    NumberFormat nf = NumberFormat.getInstance();
    nf.setMaximumFractionDigits(0);
    nf.setMinimumFractionDigits(2);
    Hashtable userinfo=(Hashtable)session.getAttribute("SYSCONFDATA");
//    session.removeAttribute("SYSCONFDATA");
    Hashtable sysconfig=(Hashtable)userinfo.get("SYSCONF");
    Hashtable merchant=(Hashtable)userinfo.get("MERCHANT");
    String merchantid=String.valueOf(merchant.get("MERCHANTID"));
    String submid=String.valueOf(merchant.get("SUBMID"));
    String page_cul=String.valueOf(sysconfig.get("MER_PAGE_CUL"));//一頁筆數
    String errMessage ="";
    queryMax = Integer.parseInt( sysconfig.get("MER_AUTH_QRY_QUANTITY").toString());//授權查詢最高筆數
    String tempcon=((Hashtable)authlog.get(0)).get("TOTAL")!=null ? ((Hashtable)authlog.get(0)).get("TOTAL").toString() :"0";
	 if(checkpoint.equals("")&& Integer.parseInt(tempcon)> queryMax){
		 showflag=true;
		
    	session.setAttribute("Message", "查詢資料筆數"+tempcon+"筆，超過系統查詢限制"+String.valueOf(queryMax)+"筆!");
    	errMessage ="查詢資料筆數"+tempcon+"筆，超過系統查詢限制"+String.valueOf(queryMax)+"筆，是否查詢上限筆數"+String.valueOf(queryMax)+"筆?";
    }


    
//#########################顯示業數處理 #########################
    int result_count=0;  //結果總筆數
    try{
      result_count=authlog.size();
    }catch(Exception e){
      result_count=0;
    }

    int TotalPcs = result_count;
    int TotalPage = 0;
    int MaxPcs = 10;
    int NowPage = 0;
    try {
      String strNowPage = (String)request.getParameter("page_no");
      if (strNowPage != null && strNowPage.length()>0) {
        NowPage = Integer.parseInt(strNowPage);
      }
    }
    catch (Exception e) {
      NowPage = 0;
    }
    try {
      String strPagePcs = page_cul;
      if (strPagePcs != null && strPagePcs.length()>0) {
        MaxPcs = Integer.parseInt(strPagePcs);
      }
    }
    catch (Exception e) {
      MaxPcs = 10;
    }

    boolean boolHome = false;
    boolean boolUp = false;
    boolean boolDown = false;
    boolean boolEnd = false;
    TotalPage = TotalPcs / MaxPcs;  // 算總頁數
    System.out.println("TotalPage="+TotalPage+" TotalPcs="+TotalPcs+" MaxPcs="+MaxPcs);
    if ((TotalPcs % MaxPcs) >0) TotalPage++;

    if (NowPage>=TotalPage) NowPage = 0;  // 目前指定頁數大於最大頁數需歸0

    if (TotalPage>1) {
      if ((NowPage+1) < TotalPage){
        boolDown = true;
        boolEnd = true;
      }
      if (NowPage>0){
        boolHome = true;
        boolUp = true;
      }
    }
//#########################顯示業數處理 #########################
Hashtable merchantuserinfo = (Hashtable)userinfo.get("MERCHANT_USER");
//子特店清單
ArrayList subMidList = new ArrayList();
//是否為子特店
boolean isSubMid =false;
//是否為單一特店
boolean isSignMer = false;
String subMidName="";
String subMid="";
subMidName=merchantuserinfo.get("MERCHANTCALLNAME").toString();
 subMid=merchantuserinfo.get("SUBMID").toString();
 isSubMid =((String) merchantuserinfo.get("ISSUBMERCHANT")).equals("Y") ?  true : false;

 subMidList = (ArrayList) userinfo.get("SUBMID");
 isSignMer = subMidList != null && subMidList.size() > 1 ? false :  true;
%>
<html>
<head>
<title></title>
 <meta http-equiv="Content-Type" content="text/html" charset="big5">
 <link href="css/style.css" type="text/css" rel="stylesheet">
<script language="JavaScript">

function checkXXX(forma){
	forma.style.display='none';

	if(<%=showflag%> && document.form.checkpoint.value!="XXX"){

		if(confirm('<%=errMessage%>')){
			forma.style.display='';
			document.form.checkpoint.value="XXX";
			forma.submit();
		}else{
			var XXX= "<%=error_message %>";
			//document.form.action.value=XXX;
			forma.action=XXX;
			forma.submit();
		}
	}else{
		forma.style.display='';
	}
}

function View_Data(x,merchantid,submid,sysorderid,rowid)
{
   rowid=encodeURIComponent(rowid);
   merchantid=encodeURIComponent(merchantid);
   submid=encodeURIComponent(submid);
   sysorderid=encodeURIComponent(sysorderid);
   strFeatures = "top=" + (screen.height-670)/2 + ",left=150,width=500,height=670,toolbar=0,menubar=0,location=0,directories=0,status=0,scrollbars=yes,resizable=yes";
//   window.open("<%=request.getContextPath()%>/MerchantAuthLogCtl?MerchantId="+merchantid+"&SubMid="+submid+"&SysOrderId="+sysorderid+"&RowId="+rowid+"&Detail=true&<%=MerchantMenuCtl.MENU_FORWARD_KEY%>=<%=MenuKey%>&Action=OPEN","_bank",strFeatures);
   window.open("<%=request.getContextPath()%>/MerchantAuthLogCtl?MerchantId="+merchantid+"&SubMid="+submid+"&SysOrderId="+sysorderid+"&RowId="+rowid+"&Detail=true&<%=MerchantMenuCtl.MENU_FORWARD_KEY%>=<%=MenuKey%>&Action=OPEN","",strFeatures);
}

// 授權交易查詢  printReport method 引數加入 查詢條件 TransType by Jimmy Kang 20150721
function printReport(merchantid,submid,Start_TransDate,Start_TransHour,End_TransDate,End_TransHour,TransCode,TransStatus,OrderType,OrderId,AuthID,TerminalID,CaptrueType,subMid,TransType,printtype)
{
  merchantid=encodeURIComponent(merchantid);
  TransStatus=encodeURIComponent(TransStatus);
  submid=encodeURIComponent(submid);
  OrderType=encodeURIComponent(OrderType);
  Start_TransDate=encodeURIComponent(Start_TransDate);
  AuthID=encodeURIComponent(AuthID);
  Start_TransHour=encodeURIComponent(Start_TransHour);
  TerminalID=encodeURIComponent(TerminalID);
  End_TransDate=encodeURIComponent(End_TransDate);
  CaptrueType=encodeURIComponent(CaptrueType);
  TransCode=encodeURIComponent(TransCode);
  printtype=encodeURIComponent(printtype);
  subMid=encodeURIComponent(subMid);
  
  // 授權交易查詢 新增查詢條件 TransType by Jimmy Kang 20150721 -- 修改開始 --
  TransType=encodeURIComponent(TransType);
  // 授權交易查詢 新增查詢條件 TransType by Jimmy Kang 20150721 -- 修改結束 --
  
  strFeatures = "top=" + (screen.height-670)/2 + ",left=150,width=1,height=1,toolbar=0,menubar=0,location=0,directories=0,status=0,scrollbars=yes";
  
  // 授權交易查詢 開啟列印的網址加入傳送 TransType的值 by Jimmy Kang 20150721
  subWin=window.open("<%=request.getContextPath()%>/MerchantAuthLogCtl?ReportFlag=ture&<%=MerchantMenuCtl.MENU_FORWARD_KEY%>=<%=MenuKey%>&RptName=MerchantAuthLogReport.rpt&MerchantId="+merchantid+"&SubMid="+submid+"&Start_TransDate="+Start_TransDate+"&Start_TransHour="+Start_TransHour+"&End_TransDate="+End_TransDate+"&End_TransHour="+End_TransHour+"&TransCode="+TransCode+"&TransStatus="+TransStatus+"&OrderType="+OrderType+"&OrderID="+OrderId+"&AuthID="+AuthID+"&TerminalID="+TerminalID+"&CaptrueType="+CaptrueType+"&subMid="+subMid+"&PrintType="+printtype+"&TransType="+TransType,"main",strFeatures);
}

function realsubmit(){
	  document.form.submit();
}

function closeSub(){
  if (subWin != null && subWin.open) subWin.close(); subWin=null;
}

</script>
</head>
<body bgcolor="#ffffff"   onload="checkXXX(this.form);" >
<form name="form" id="form"  method="post" action="./MerchantAuthLogCtl">
<%//System.out.println(String.valueOf(request.getAttribute("Start_TransDate"))+"@@"+String.valueOf(request.getAttribute("Start_TransHour"))); %>
<input type="hidden" id="Start_TransDate" name="Start_TransDate" value='<%=String.valueOf(request.getAttribute("Start_TransDate"))%>' />
<input type="hidden" id="Start_TransHour" name="Start_TransHour" value='<%=String.valueOf(request.getAttribute("Start_TransHour"))%>' />
<input type="hidden" id="End_TransDate" name="End_TransDate" value='<%=String.valueOf(request.getAttribute("End_TransDate"))%>' />
<input type="hidden" id="End_TransHour" name="End_TransHour" value='<%=String.valueOf(request.getAttribute("End_TransHour"))%>' />
<input type="hidden" id="TransCode" name="TransCode" value='<%=String.valueOf(request.getAttribute("TransCode"))%>' />
<input type="hidden" id="TransStatus" name="TransStatus" value='<%=String.valueOf(request.getAttribute("TransStatus"))%>' />
<input type="hidden" id="OrderType" name="OrderType" value='<%=String.valueOf(request.getAttribute("OrderType"))%>' />
<input type="hidden" id="OrderId" name="OrderId" value='<%=String.valueOf(request.getAttribute("OrderId"))%>' />
<input type="hidden" id="AuthID" name="AuthID" value='<%=String.valueOf(request.getAttribute("AuthID"))%>' />
<input type="hidden" id="TerminalID" name="TerminalID" value='<%=String.valueOf(request.getAttribute("TerminalID"))%>' />
<input type="hidden" id="CaptrueType" name="CaptrueType" value='<%=String.valueOf(request.getAttribute("CaptrueType"))%>' />
<input type="hidden" id="subMid" name="subMid" value='<%=String.valueOf(request.getAttribute("subMid"))%>' />
<input type="hidden" id="checkpoint" name="checkpoint" value='<%=request.getAttribute("checkpoint")==null ? "" : String.valueOf(request.getAttribute("checkpoint"))%>' />

<!-- 授權交易查詢 新增隱藏的html標籤儲存查詢條件 TransType by Jimmy Kang 20150721  -- 修改開始 -- -->
<input type="hidden" id="TransType" name="TransType" value='<%=String.valueOf(request.getAttribute("TransType"))%>' />
<!-- 授權交易查詢 新增隱藏的html標籤儲存查詢條件 TransType by Jimmy Kang 20150721  -- 修改結束 -- -->

 <%
out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>

 <table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
   <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
   <td align="left" valign="middle"><b><font color="#004E87" size="3">授權交易查詢</font></b></td>
  </tr>
 </table>

 <hr style="height:1px">

 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
  <tr>
   <td>
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
     <tr>
      <td height="30" align="left">
       <!-- 授權交易查詢 新增 printReport method傳入的引數 TransType by Jimmy Kang 20150721  -- 修改開始 -- -->
       <input type='button' value='以PDF格式匯出' name='btnPDF' id='btnPDF' onclick="printReport('<%=merchantid %>','<%=submid %>','<%=String.valueOf(request.getAttribute("Start_TransDate"))%>','<%=String.valueOf(request.getAttribute("Start_TransHour"))%>','<%=String.valueOf(request.getAttribute("End_TransDate"))%>','<%=String.valueOf(request.getAttribute("End_TransHour"))%>','<%=String.valueOf(request.getAttribute("TransCode"))%>','<%=String.valueOf(request.getAttribute("TransStatus"))%>','<%=String.valueOf(request.getAttribute("OrderType"))%>','<%=String.valueOf(request.getAttribute("OrderId"))%>','<%=String.valueOf(request.getAttribute("AuthID"))%>','<%=String.valueOf(request.getAttribute("TerminalID"))%>','<%=String.valueOf(request.getAttribute("CaptrueType"))%>','<%=String.valueOf(request.getAttribute("subMid"))%>','<%=String.valueOf(request.getAttribute("TransType"))%>','PDF')">
       <input type='button' value='以CSV格式匯出' name='btnCSV' id='btnCSV' onclick="printReport('<%=merchantid %>','<%=submid %>','<%=String.valueOf(request.getAttribute("Start_TransDate"))%>','<%=String.valueOf(request.getAttribute("Start_TransHour"))%>','<%=String.valueOf(request.getAttribute("End_TransDate"))%>','<%=String.valueOf(request.getAttribute("End_TransHour"))%>','<%=String.valueOf(request.getAttribute("TransCode"))%>','<%=String.valueOf(request.getAttribute("TransStatus"))%>','<%=String.valueOf(request.getAttribute("OrderType"))%>','<%=String.valueOf(request.getAttribute("OrderId"))%>','<%=String.valueOf(request.getAttribute("AuthID"))%>','<%=String.valueOf(request.getAttribute("TerminalID"))%>','<%=String.valueOf(request.getAttribute("CaptrueType"))%>','<%=String.valueOf(request.getAttribute("subMid"))%>','<%=String.valueOf(request.getAttribute("TransType"))%>','CSV')">
       <input type='button' value='以TXT格式匯出' name='btnTXT' id='btnTXT' onclick="printReport('<%=merchantid %>','<%=submid %>','<%=String.valueOf(request.getAttribute("Start_TransDate"))%>','<%=String.valueOf(request.getAttribute("Start_TransHour"))%>','<%=String.valueOf(request.getAttribute("End_TransDate"))%>','<%=String.valueOf(request.getAttribute("End_TransHour"))%>','<%=String.valueOf(request.getAttribute("TransCode"))%>','<%=String.valueOf(request.getAttribute("TransStatus"))%>','<%=String.valueOf(request.getAttribute("OrderType"))%>','<%=String.valueOf(request.getAttribute("OrderId"))%>','<%=String.valueOf(request.getAttribute("AuthID"))%>','<%=String.valueOf(request.getAttribute("TerminalID"))%>','<%=String.valueOf(request.getAttribute("CaptrueType"))%>','<%=String.valueOf(request.getAttribute("subMid"))%>','<%=String.valueOf(request.getAttribute("TransType"))%>','TXT')">
       <!-- 授權交易查詢 新增 printReport method傳入的引數 TransType by Jimmy Kang 20150721  -- 修改結束 -- -->
      </td>
      <td align="right">
        <select name="page_no" id="page_no" onchange="realsubmit();">
 <%
 for (int i=0; i<TotalPage && checkpoint.equals("XXX");++i) {
 %>
 <option <%if (i==NowPage) out.write("selected");%> value="<%=i%>">第<%=i+1%>   頁               </option>
 <%}%>

        </select></td>
     </tr>
    </table>
   </td>
  </tr>
 </table>

 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
  <tr align='center' bgcolor="#F4F4FF" height="25">
   <td align='center' rowspan="2">序號</td>
   <td align='center' rowspan="2">檢視</td>
   <td align='center' colspan="1">特約商店代號</td>
   <% if(!isSignMer){ %>
   <td align='center' rowspan="2">子特約商店代號</td>
   <%} %>
   <td align='center' colspan="1">特店指定單號</td>
   <td align='center' colspan="1">交易類別</td>
   <td align='center' rowspan="2">交易日期</td>
   <td align='center' colspan="1">交易幣別</td>
   <td align='center' colspan="1">回應碼</td>
   <td align='center' colspan="1">批次號碼</td>
   <td align='center' colspan="1">首期金額</td>
   <td align='center' colspan="1">紅利折抵點數</td>
   <td align='center' rowspan="2">可請款金額</td>
  </tr>

  <tr align='center' bgcolor='#F4F4FF' height="25">
   <td align='center'>端末機代碼</td>
   <td align='center'>系統指定單號</td>
   <td align='center'>交易模式</td>
   <td align='center'>交易金額</td>
   <td align='center'>授權碼</td>
   <td align='center'>分期期數</td>
   <td align='center'>每期金額</td>
   <td align='center'>卡人自付額</td>
  </tr>
<%
  double page_00_success_amt=0;
  double page_00_failer_amt=0;
  double page_01_amt=0;
  double page_10_amt=0; 
  double page_11_amt=0;
  double page_balance_amt=0;
  double page_refund_amt=0;
  
  // 授權交易查詢 新增 處理中交易的金額/筆數統計 by Jimmy Kang 20150721  -- 修改開始 --
  double page_00_pending_amt=0;  // 購貨處理中交易金額
  double page_01_pending_amt=0;  // 退貨處理中交易金額
  double page_10_pending_amt=0;  // 購貨取消處理中交易金額
  // 授權交易查詢 新增 處理中交易的金額/筆數統計 by Jimmy Kang 20150721  -- 修改結束 --

  int page_00_success_cnt=0;
  int page_00_failer_cnt=0;
  int page_01_cnt=0;
  int page_10_cnt=0;
  int page_11_cnt=0;
  int page_balance_cnt=0;
  int page_refund_cnt=0;
  
  // 授權交易查詢 新增 處理中交易的金額/筆數統計 by Jimmy Kang 20150721  -- 修改開始 --
  int page_00_pending_cnt=0;  // 購貨處理中交易金額
  int page_01_pending_cnt=0;  // 退貨處理中交易金額
  int page_10_pending_cnt=0;  // 購貨取消處理中交易金額
  // 授權交易查詢 新增 處理中交易的金額/筆數統計 by Jimmy Kang 20150721  -- 修改結束 --
  
  String color="";
  int StartPoint = NowPage * MaxPcs;
  int EndPoint = NowPage * MaxPcs + MaxPcs;
  System.out.println("StartPoint="+StartPoint+" EndPoint="+EndPoint);
  for(int i=StartPoint; i<EndPoint && checkpoint.equals("XXX");++i){
    if (i >= authlog.size())break;
    Hashtable content=(Hashtable)authlog.get(i);
    String transcode=String.valueOf(content.get("TRANSCODE"));
    String transstatus=String.valueOf(content.get("TRANS_STATUS"));
    String balance_transstatus=String.valueOf(content.get("BALANCE_TRANSCODE"));
    
    String show_balance_amt = "0";
    String balance_amt=String.valueOf(content.get("BALANCEAMT")).trim().length()==0?"0":String.valueOf(content.get("BALANCEAMT"));
    
    if(transstatus.equalsIgnoreCase("A")){//Approved
       show_balance_amt=nf.format(Double.parseDouble(balance_amt)) ;
    } else {
       show_balance_amt="不可請款";
    }
    String transamt = String.valueOf(content.get("TRANSAMT")).trim().length()==0?"0.0":String.valueOf(content.get("TRANSAMT"));
    if(transcode.equals("00")){
      color="black";
      transcode=transcode+" - 購貨";//交易
      if(transstatus.equalsIgnoreCase("A")){//Approved
        page_00_success_amt=page_00_success_amt+Double.parseDouble(transamt);
        page_00_success_cnt++;
        //System.out.println("page_00_success_amt="+page_00_success_amt+",transamt"+transamt);
        if (Double.parseDouble(balance_amt)>0) {
          page_balance_amt=page_balance_amt+Double.parseDouble(balance_amt);
          page_balance_cnt++;
        }
        //System.out.println("page_balance_amt="+page_balance_amt+",balance_amt="+balance_amt);
      }
      // 授權交易查詢 新增 處理中交易的金額/筆數統計 by Jimmy Kang 20150721  -- 修改開始 --
      else if(transstatus.equalsIgnoreCase("P")){//Pending
    	page_00_pending_amt = page_00_pending_amt + Double.parseDouble(transamt);
    	page_00_pending_cnt++;
      }
      // 授權交易查詢 新增 處理中交易的金額/筆數統計 by Jimmy Kang 20150721  -- 修改結束 --
      else{//Request,Declined,Call Bank
        page_00_failer_amt=page_00_failer_amt+Double.parseDouble(transamt);
        page_00_failer_cnt++;
      }
    }else if(transcode.equals("01")){
      color="red";
      transcode=transcode+" - 退貨";//交易
      // 授權交易查詢 新增 處理中交易的金額/筆數統計 by Jimmy Kang 20150721  -- 修改開始 --
      if(transstatus.equalsIgnoreCase("P")){//Pending
        page_01_pending_amt = page_01_pending_amt + Double.parseDouble(transamt);
        page_01_pending_cnt++;
      }
      // 授權交易查詢 新增 處理中交易的金額/筆數統計 by Jimmy Kang 20150721  -- 修改結束 --
      else{
        page_01_amt=page_01_amt+Double.parseDouble(transamt);
        page_01_cnt++;
        if (Double.parseDouble(balance_amt) > 0) {
          page_refund_amt=page_refund_amt+Double.parseDouble(balance_amt);
          page_refund_cnt++;
        }
      }
    }else if(transcode.equals("10")){
      color="blue";
      transcode=transcode+" - 購貨取消";//交易
      // 授權交易查詢 新增 處理中交易的金額/筆數統計 by Jimmy Kang 20150721  -- 修改開始 --
      if(transstatus.equalsIgnoreCase("P")){//Pending
        page_10_pending_amt = page_10_pending_amt + Double.parseDouble(transamt);
        page_10_pending_cnt++;
      }
      // 授權交易查詢 新增 處理中交易的金額/筆數統計 by Jimmy Kang 20150721  -- 修改結束 --
      else{
        page_10_amt=page_10_amt+Double.parseDouble(transamt);
        page_10_cnt++;
      }
    }else if(transcode.equals("11")){
      color="blue";
      transcode=transcode+" - 退貨取消";//交易
      page_11_amt=page_11_amt+Double.parseDouble(transamt);
      page_11_cnt++;
    }
    else if(transcode.equals("81")){
      color="blue";
      transcode=transcode+" - 卡片驗證交易";//20160525 Flora Add 81交易
    }
    else{
    }
    String transdate=String.valueOf(content.get("TRANSDATE"));
    transdate=transdate.substring(0,4)+"/"+transdate.substring(4,6)+"/"+transdate.substring(6);
    String transtime=String.valueOf(content.get("TRANSTIME"));
    transtime=transtime.substring(0,2)+":"+transtime.substring(2,4)+":"+transtime.substring(4);
    String transmode=String.valueOf(content.get("TRANSMODE"));
    if(transmode.equals("0")){
      transmode=transmode+" - 一般";
    }else if(transmode.equals("1")){
      transmode=transmode+" - 分期";
    }else if(transmode.equals("2")){
      transmode=transmode+" - 紅利";
    }
   merchantid=String.valueOf(content.get("MERCHANTID"));
   submid=String.valueOf(content.get("SUBMID"));
   String sysorderid=String.valueOf(content.get("SYS_ORDERID"));
   String rowid=String.valueOf(content.get("ROWID1"));
  %>
  <tr align='center' bgcolor='#ffffff'  height="25">
    <td align='left'   rowspan="2"><font color="<%=color%>"><%=i+1 %></font></td>
    <td align='center' rowspan="2"><a href="javascript:View_Data(0,'<%=merchantid %>','<%=String.valueOf(content.get("SUBMID"))  %>','<%= sysorderid%>','<%= rowid%>');"><img src="images/button/view.jpg" alt="檢視" border="0"></a></td>
      <td align='left'   colspan="1"><font color="<%=color%>"><%=String.valueOf(content.get("MERCHANTID"))  %></font></td>
      <% if(!isSignMer){ %>
      <td align='center' rowspan="2"><font color="<%=color%>"> <%=String.valueOf(content.get("SUBMID"))  %>  </font></td>
      <%} %>
      <td align='left'   colspan="1"><font color="<%=color%>"><%=String.valueOf(content.get("ORDERID"))  %></font></td>
      <td align='left'   colspan="1"><font color="<%=color%>"><%=String.valueOf(transcode)  %></font></td>
      <td align='center' rowspan="2"><font color="<%=color%>"><%=String.valueOf(transdate)  %><br><%=String.valueOf(transtime)  %></font></td>
        <td align='center' colspan="1"><font color="<%=color%>">新台幣</font></td>
        <td align='left'   colspan="1"><font color="<%=color%>"><%=String.valueOf(content.get("RESPONSECODE"))  %></font></td>
        <td align='left'   colspan="1"><font color="<%=color%>"><%=String.valueOf(content.get("BATCHNO"))  %></font></td>
        <td align='right'  colspan="1"><font color="<%=color%>"><%=nf.format(Double.parseDouble(String.valueOf(content.get("FIRSTAMT"))))  %></font></td>
        <td align='right'  colspan="1"><font color="<%=color%>"><%=String.valueOf(content.get("REDEMUSED"))  %></font></td>
        <td align='right'  rowspan="2"><font color="<%=color%>"><%=show_balance_amt%></font></td>
  </tr>

  <tr align='center' bgcolor='#ffffff' height="25">
    <td align='left'><font color="<%=color%>"><%=String.valueOf(content.get("TERMINALID"))%></font></td>
    <td align='left'><font color="<%=color%>"><%=String.valueOf(content.get("SYS_ORDERID"))%></font></td>
    <td align='left'><font color="<%=color%>"><%=String.valueOf(transmode)  %></font></td>
    <td align='right'><font color="<%=color%>"><%=nf.format(Double.parseDouble(String.valueOf(content.get("TRANSAMT")).trim().length()==0?"0":String.valueOf(content.get("TRANSAMT"))))  %></font></td>
    <td align='left'><font color="<%=color%>"><%=String.valueOf(content.get("APPROVECODE"))  %></font></td>
    <td align='left'><font color="<%=color%>"><%=String.valueOf(content.get("INSTALL"))  %></font></td>
    <td align='right'><font color="<%=color%>"><%=nf.format(Double.parseDouble(String.valueOf(content.get("EACHAMT")).trim().length()==0?"0":String.valueOf(content.get("EACHAMT"))))  %></font></td>
    <td align='right'><font color="<%=color%>"><%=nf.format(Double.parseDouble(String.valueOf(content.get("CREDITAMT")).trim().length()==0?"0":String.valueOf(content.get("CREDITAMT")) )) %></font></td>
  </tr>
 <%  }if(checkpoint.equals("")){%>
  <tr align='center' bgcolor='#ffffff'  height="25">
  <td align='left'  colspan="13"  rowspan="2">查詢中...........</td>
  </tr>
 <%} %>
 </table>

 <br>

 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
  <tr bgcolor='#ffffff' height="25">
   <td align='left' width='20%'>本頁授權小計 </td>
   <td align='center' width='15%'>筆數</td>
   <td align='center' width='25%'>金額</td>
   <td align='center' width='40%'></td>
  </tr>

  <tr bgcolor='#ffffff' height="25">
   <td align='right' width='20%'>購貨失敗授權交易</td>
   <td align='right' width='15%'><%=nf.format(page_00_failer_cnt).replaceAll(".00","")%></td>
   <td align='right' width='25%'><%=nf.format(page_00_failer_amt)%></td>
   <td align='right' width='40%'></td>
  </tr>

  <tr bgcolor='#ffffff' height="25">
   <td align='right' width='20%'>購貨成功授權交易</td>
   <td align='right' width='15%'><%=nf.format(page_00_success_cnt).replaceAll(".00","")%></td>
   <td align='right' width='25%'><%=nf.format(page_00_success_amt)%></td>
   <td align='right' width='40%'></td>
  </tr>
  
  <!-- 授權交易查詢 新增 處理中交易的金額/筆數統計 by Jimmy Kang 20150721  -- 修改開始 -- -->
  <tr bgcolor='#ffffff' height="25">
   <td align='right' width='20%'>購貨處理中交易</td>
   <td align='right' width='15%'><%=nf.format(page_00_pending_cnt).replaceAll(".00","")%></td>
   <td align='right' width='25%'><%=nf.format(page_00_pending_amt)%></td>
   <td align='right' width='40%'></td>
  </tr>
  <!-- 授權交易查詢 新增 處理中交易的金額/筆數統計 by Jimmy Kang 20150721  -- 修改結束 -- -->

  <tr bgcolor='#ffffff' height="25">
   <td align='right' width='20%'><font color="red">退貨成功交易</font></td>
   <td align='right' width='15%'><font color="red"><%=nf.format(page_01_cnt).replaceAll(".00","")%></font></td>
   <td align='right' width='25%'><font color="red"><%=nf.format(page_01_amt)%></font></td>
   <td align='right' width='40%'></td>
  </tr>
  
  <!-- 授權交易查詢 新增 處理中交易的金額/筆數統計 by Jimmy Kang 20150721  -- 修改開始 -- -->
  <tr bgcolor='#ffffff' height="25">
   <td align='right' width='20%'><font color="red">退貨處理中交易</font></td>
   <td align='right' width='15%'><font color="red"><%=nf.format(page_01_pending_cnt).replaceAll(".00","")%></font></td>
   <td align='right' width='25%'><font color="red"><%=nf.format(page_01_pending_amt)%></font></td>
   <td align='right' width='40%'></td>
  </tr>
  <!-- 授權交易查詢 新增 處理中交易的金額/筆數統計 by Jimmy Kang 20150721  -- 修改結束 -- -->

  <tr bgcolor='#ffffff' height="25">
   <td align='right' width='20%'><font color="blue">購貨取消成功交易</font></td>
   <td align='right' width='15%'><font color="blue"><%=nf.format(page_10_cnt).replaceAll(".00","")%></font></td>
   <td align='right' width='25%'><font color="blue"><%=nf.format(page_10_amt)%></font></td>
   <td align='right' width='40%'></td>
  </tr>
  
  <!-- 授權交易查詢 新增 處理中交易的金額/筆數統計 by Jimmy Kang 20150721  -- 修改開始 -- -->
  <tr bgcolor='#ffffff' height="25">
   <td align='right' width='20%'><font color="blue">購貨取消處理中交易</font></td>
   <td align='right' width='15%'><font color="blue"><%=nf.format(page_10_pending_cnt).replaceAll(".00","")%></font></td>
   <td align='right' width='25%'><font color="blue"><%=nf.format(page_10_pending_amt)%></font></td>
   <td align='right' width='40%'></td>
  </tr>
  <!-- 授權交易查詢 新增 處理中交易的金額/筆數統計 by Jimmy Kang 20150721  -- 修改結束 -- -->
  

  <tr bgcolor='#ffffff' height="25">
   <td align='right' width='20%'><font color="blue">退貨取消成功交易</font></td>
   <td align='right' width='15%'><font color="blue"><%=nf.format(page_11_cnt).replaceAll(".00","")%></font></td>
   <td align='right' width='25%'><font color="blue"><%=nf.format(page_11_amt)%></font></td>
   <td align='right' width='40%'></td>
  </tr>

  <tr bgcolor='#ffffff' height="25">
   <td align='left' width='20%'>本頁可請款小計 </td>
   <td align='center' width='15%'>筆數</td>
   <td align='center' width='25%'>金額</td>
   <td align='center' width='40%'></td>
  </tr>

  <tr bgcolor='#ffffff' height="25">
   <td align='right' width='20%'>購貨成功授權交易</td>
   <td align='right' width='15%'><%=nf.format(page_balance_cnt).replaceAll(".00","")%></td>
   <td align='right' width='25%'><%=nf.format(page_balance_amt)%></td>
   <td align='right' width='40%'></td>
  </tr>

  <tr bgcolor='#ffffff' height="25">
   <td align='right' width='20%'><font color="red">退貨成功交易</font></td>
   <td align='right' width='15%'><font color="red"><%=nf.format(page_refund_cnt).replaceAll(".00","")%></font></td>
   <td align='right' width='25%'><font color="red"><%=nf.format(page_refund_amt)  %></font></td>
   <td align='right' width='40%'></td>
  </tr>
 </table>
</form>
</body>
</html>
<%
    }  } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errMsg",e.toString());
           request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
        }
%>
