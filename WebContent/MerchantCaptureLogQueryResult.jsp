<!DOCTYPE HTML PUBLIC "-//w3c//dtd html 4.0 transitional//en">
<%@page import="com.cybersoft.merchant.ctl.MerchantCaptureLogCtl"%>
<%@ page contentType="text/html; charset=Big5" %>
<%@ page language="java" %>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>
<%@ page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%@ page import="com.cybersoft.bean.UserBean" %>
<%
//  response.addHeader("Pragma", "No-cache");
//  response.addHeader("Cache-Control", "no-cache");
//  response.addDateHeader("Expires", 1);

try{
    Enumeration key= request.getAttributeNames();
    while(key.hasMoreElements()){
    String keyname=String.valueOf(key.nextElement());
    //System.out.println(keyname+"="+request.getAttribute(keyname));
    }
    String type=request.getParameter("Type");
    ArrayList capturelog=(ArrayList)session.getAttribute("Capturelog");
    String error_message="./Merchant_Response.jsp";
    if(capturelog==null||capturelog.size()==0){
      session.setAttribute("Message", "查無資料");
      request.getRequestDispatcher(error_message).forward(request, response);
    }else{
    NumberFormat nf = NumberFormat.getInstance();
    nf.setMaximumFractionDigits(0);
    nf.setMinimumFractionDigits(2);
    NumberFormat nf_int = NumberFormat.getInstance();
    nf_int.setMaximumFractionDigits(0);
    nf_int.setMinimumFractionDigits(0);
    Hashtable userinfo=(Hashtable)session.getAttribute("SYSCONFDATA");
    Hashtable sysconfig=(Hashtable)userinfo.get("SYSCONF");
    String page_cul=String.valueOf(sysconfig.get("MER_PAGE_CUL"));//一頁筆數
    String checkpoint=request.getAttribute("checkpoint")!=null ? request.getAttribute("checkpoint").toString() : "";
    int queryMax=3000;
    String errMessage ="";
    queryMax = Integer.parseInt( sysconfig.get("MER_CAPTURE_QRY_QUANTITY").toString());//授權查詢最高筆數
    String tempcon=((Hashtable)capturelog.get(0)).get("COUNT")!=null ? ((Hashtable)capturelog.get(0)).get("COUNT").toString() :"0";
    boolean showflag=false;//false:超過 ture:不超過
	 if(capturelog != null&&capturelog.size() != 0 && checkpoint.equals("") && Integer.parseInt(tempcon)> queryMax && type.equalsIgnoreCase("List")){
		 showflag=true;
		 capturelog.remove(capturelog.size()-1);
		session.setAttribute("Message", "查詢資料筆數"+tempcon+"筆，超過系統查詢限制"+String.valueOf(queryMax)+"筆!");
	    errMessage ="查詢資料筆數"+tempcon+"筆，超過系統查詢限制"+String.valueOf(queryMax)+"筆，是否查詢上限筆數"+String.valueOf(queryMax)+"筆?";
    }

//#########################顯示業數處理 #########################
    int result_count=0;  //結果總筆數
    try{
      result_count=capturelog.size();
    }catch(Exception e){
      result_count=0;
    }
    String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);
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
int sale_appro_amt=0;//購貨已核准
int sale_appro_cnt=0;
int sale_failer_amt=0;//購貨剔退
int sale_failer_cnt=0;
int sale_wait_amt=0;//購貨待處理
int sale_wait_cnt=0;
int sale_void_amt=0;//購貨取消
int sale_void_cnt=0;
int sale_process_amt=0;//購貨處理中
int sale_process_cnt=0;

int refund_appro_amt=0;//退貨已核准
int refund_appro_cnt=0;
int refund_failer_amt=0;//退貨剔退
int refund_failer_cnt=0;
int refund_wait_amt=0;//退貨待處理
int refund_wait_cnt=0;
int refund_void_amt=0;//退貨取消
int refund_void_cnt=0;
int refund_process_amt=0;//退貨處理中
int refund_process_cnt=0;

int net_appro_amt=0;//已核准
int net_appro_cnt=0;
int net_failer_amt=0;//剔退
int net_failer_cnt=0;
int net_wait_amt=0;//待處理
int net_wait_cnt=0;
int net_void_amt=0;//取消
int net_void_cnt=0;
int net_process_amt=0;//處理中
int net_process_cnt=0;

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
<title>
</title>

 <meta http-equiv="Content-Type" content="text/html charset=big5">
 <link href="css/style.css" type="text/css" rel="stylesheet">
<script language="JavaScript">
function checkXXX(forma){
	forma.style.display='none';

	if(<%=showflag%> && document.form.checkpoint.value!="XXX" && document.form.Type.value!="TotalNet"){

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
{rowid=encodeURIComponent(rowid);
 strFeatures = "top=" + (screen.height-670)/2 + ",left=150,width=500,height=670,toolbar=0,menubar=0,location=0,directories=0,status=0,scrollbars=yes,resizable=yes";
// window.open("<%=request.getContextPath()%>/MerchantCaptureLogCtl?Detail=true&MerchantId="+merchantid+"&SubMid="+submid+"&SysOrderId="+sysorderid+"&RowId="+rowid+"&<%=MerchantMenuCtl.MENU_FORWARD_KEY%>=<%=MenuKey%>&Action=OPEN","_bank",strFeatures);
 window.open("<%=request.getContextPath()%>/MerchantCaptureLogCtl?Detail=true&MerchantId="+merchantid+"&SubMid="+submid+"&SysOrderId="+sysorderid+"&RowId="+rowid+"&<%=MerchantMenuCtl.MENU_FORWARD_KEY%>=<%=MenuKey%>&Action=OPEN","",strFeatures);
}

//請款交易查詢  printReport method 引數加入 查詢條件 TransType by Jimmy Kang 20150727
function printReport(Start_TransDate,End_TransDate,TransCode,OrderType,OrderId,AuthID,TerminalID,CaptrueType,Type,printtype, ExceptFlag,subMid, TransType)
{
 strFeatures = "top=" + (screen.height-670)/2 + ",left=150,width=500,height=670,toolbar=0,menubar=0,location=0,directories=0,status=0,scrollbars=yes";
 
 //授權交易查詢 開啟列印的網址加入傳送 TransType的值 by Jimmy Kang 20150727
 window.open("<%=request.getContextPath()%>/MerchantCaptureLogCtl?ReportFlag=ture&Start_TransDate="+Start_TransDate+"&End_TransDate="+End_TransDate+"&TransCode="+TransCode+"&OrderType="+OrderType+"&OrderID="+OrderId+"&AuthID="+AuthID+"&TerminalID="+TerminalID+"&CaptrueType="+CaptrueType+"&Type="+Type+"&<%=MerchantMenuCtl.MENU_FORWARD_KEY%>=<%=MenuKey%>"+"&PrintType="+printtype+"&ExceptFlag="+ExceptFlag+"&subMid="+subMid+"&TransType="+TransType,"main",strFeatures);
}
function realsubmit(){
  document.form.submit();
}
</script>
</head>
<body bgcolor="#ffffff"  onload="checkXXX(this.form);">
<form name="form" id="form"  method="post" action="./MerchantCaptureLogCtl">
<input type="hidden" id="Start_TransDate" name="Start_TransDate" value='<%=String.valueOf(request.getAttribute("Start_TransDate"))%>' />
<input type="hidden" id="End_TransDate" name="End_TransDate" value='<%=String.valueOf(request.getAttribute("End_TransDate"))%>' />
<input type="hidden" id="TransCode" name="TransCode" value='<%=String.valueOf(request.getAttribute("TransCode"))%>' />
<input type="hidden" id="OrderType" name="OrderType" value='<%=String.valueOf(request.getAttribute("OrderType"))%>' />
<input type="hidden" id="OrderId" name="OrderId" value='<%=String.valueOf(request.getAttribute("OrderId"))%>' />
<input type="hidden" id="AuthID" name="AuthID" value='<%=String.valueOf(request.getAttribute("AuthID"))%>' />
<input type="hidden" id="TerminalID" name="TerminalID" value='<%=String.valueOf(request.getAttribute("TerminalID"))%>' />
<input type="hidden" id="CaptrueType" name="CaptrueType" value='<%=String.valueOf(request.getAttribute("CaptrueType"))%>' />
<input type="hidden" id="ExceptFlag" name="ExceptFlag" value='<%=String.valueOf(request.getAttribute("ExceptFlag"))%>' />
<input type="hidden" id="Type" name="Type" value='<%=String.valueOf(request.getAttribute("Type"))%>' />
<input type="hidden" id="subMid" name="subMid" value='<%=String.valueOf(request.getAttribute("subMid"))%>' />
<input type="hidden" id="checkpoint" name="checkpoint" value='<%=request.getAttribute("checkpoint")==null ? "" : String.valueOf(request.getAttribute("checkpoint"))%>' />

<!-- 請款交易查詢 新增隱藏的html標籤儲存查詢條件 TransType by Jimmy Kang 20150727  -- 修改開始 -- -->
<input type="hidden" id="TransType" name="TransType" value='<%=String.valueOf(request.getAttribute("TransType"))%>' />
<!-- 請款交易查詢 新增隱藏的html標籤儲存查詢條件 TransType by Jimmy Kang 20150727  -- 修改結束 -- -->

 <%
 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>

 <table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
   <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
   <td align="left" valign="middle"><b><font color="#004E87" size="3">請款交易查詢</font></b></td>
  </tr>
 </table>

 <hr style="height:1px">

 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
  <tr>
   <td>
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
     <tr>
      <td height="30" align="left">
      <!-- 請款交易查詢  printReport method 引數加入 查詢條件 TransType by Jimmy Kang 20150727  -- 修改開始 -- -->
       <input type='button' value='以PDF格式匯出' name='btnPDF' id='btnPDF' onclick="printReport('<%=String.valueOf(request.getAttribute("Start_TransDate"))%>','<%=String.valueOf(request.getAttribute("End_TransDate"))%>','<%=String.valueOf(request.getAttribute("TransCode"))%>','<%=String.valueOf(request.getAttribute("OrderType"))%>','<%=String.valueOf(request.getAttribute("OrderId"))%>','<%=String.valueOf(request.getAttribute("AuthID"))%>','<%=String.valueOf(request.getAttribute("TerminalID"))%>','<%=String.valueOf(request.getAttribute("CaptrueType"))%>','<%=String.valueOf(request.getAttribute("Type"))%>','PDF','<%=String.valueOf(request.getAttribute("ExceptFlag"))%>','<%=String.valueOf(request.getAttribute("subMid"))%>','<%=String.valueOf(request.getAttribute("TransType"))%>')">
       <%if(type!=null&&type.equalsIgnoreCase("List")){%>
       <input type='button' value='以CSV格式匯出' name='btnCSV' id='btnCSV' onclick="printReport('<%=String.valueOf(request.getAttribute("Start_TransDate"))%>','<%=String.valueOf(request.getAttribute("End_TransDate"))%>','<%=String.valueOf(request.getAttribute("TransCode"))%>','<%=String.valueOf(request.getAttribute("OrderType"))%>','<%=String.valueOf(request.getAttribute("OrderId"))%>','<%=String.valueOf(request.getAttribute("AuthID"))%>','<%=String.valueOf(request.getAttribute("TerminalID"))%>','<%=String.valueOf(request.getAttribute("CaptrueType"))%>','<%=String.valueOf(request.getAttribute("Type"))%>','CSV','<%=String.valueOf(request.getAttribute("ExceptFlag"))%>','<%=String.valueOf(request.getAttribute("subMid"))%>','<%=String.valueOf(request.getAttribute("TransType"))%>')">
       <input type='button' value='以TXT格式匯出' name='btnTXT' id='btnTXT' onclick="printReport('<%=String.valueOf(request.getAttribute("Start_TransDate"))%>','<%=String.valueOf(request.getAttribute("End_TransDate"))%>','<%=String.valueOf(request.getAttribute("TransCode"))%>','<%=String.valueOf(request.getAttribute("OrderType"))%>','<%=String.valueOf(request.getAttribute("OrderId"))%>','<%=String.valueOf(request.getAttribute("AuthID"))%>','<%=String.valueOf(request.getAttribute("TerminalID"))%>','<%=String.valueOf(request.getAttribute("CaptrueType"))%>','<%=String.valueOf(request.getAttribute("Type"))%>','TXT','<%=String.valueOf(request.getAttribute("ExceptFlag"))%>','<%=String.valueOf(request.getAttribute("subMid"))%>','<%=String.valueOf(request.getAttribute("TransType"))%>')">
       <!-- 請款交易查詢  printReport method 引數加入 查詢條件 TransType by Jimmy Kang 20150727  -- 修改結束 -- -->
       <%} %>
      </td>
       <%if(type!=null&&type.equalsIgnoreCase("List")){%>
       <td align="right">
         <select name="page_no" id="page_no" onchange="realsubmit();">
         <%
         for (int i=0; i<TotalPage  && checkpoint.equals("XXX");++i) {
         %>
         <option <%if (i==NowPage) out.write("selected");%> value="<%=i%>">第<%=i+1%>   頁               </option>
         <%}%>
         </select>
       </td>
       <%}else if(type!=null&&type.equalsIgnoreCase("TotalNet")){

for(int i=0; i<capturelog.size()&& checkpoint.equals("XXX");++i){
  if (i >= capturelog.size())break;
  Hashtable content=(Hashtable)capturelog.get(i);
  String transcode=String.valueOf(content.get("TRANSCODE"));//交易代碼
  String feedbackcode=String.valueOf(content.get("FEEDBACKCODE"));//請款回覆碼
  String captrueflag=String.valueOf(content.get("CAPTUREFLAG"));//請款控制旗標
  //Jason 201303
  //String pan = String.valueOf(content.get("PAN"));//PAN
 // System.out.println(content);
  String balance_amt=String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT"));
  balance_amt=String.valueOf(Integer.parseInt(balance_amt)<=0?"0":balance_amt);
  //System.out.println("balance_amt="+balance_amt);
 // System.out.println("transcode="+transcode);
  //System.out.println("captrueflag=<"+captrueflag+">");
  int count=Integer.parseInt(String.valueOf(content.get("COUNT")).trim().length()==0?"0":String.valueOf(content.get("COUNT")));
  if(transcode.equals("00")){//購貨交易
    if(captrueflag.equalsIgnoreCase("3")){//以處理
      if(feedbackcode.equalsIgnoreCase("000")){
        sale_appro_amt=sale_appro_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
        //sale_appro_cnt++;
        sale_appro_cnt = sale_appro_cnt + count;
      }else{//踢退
        sale_failer_amt=sale_failer_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
        //sale_failer_cnt++;
        sale_failer_cnt = sale_failer_cnt + count;
      }
    }else if(captrueflag.equalsIgnoreCase("0")){//未處理
      sale_wait_amt=sale_wait_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //sale_wait_cnt++;
      sale_wait_cnt = sale_wait_cnt + count;
    }else if(captrueflag.equalsIgnoreCase("2")){//已取消
      sale_void_amt=sale_void_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //sale_void_cnt++;
      sale_void_cnt = sale_void_cnt + count;
    }else if(captrueflag.equalsIgnoreCase("1")){//處理中
      sale_process_amt=sale_process_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //sale_process_cnt++;
      sale_process_cnt = sale_process_cnt + count;
    }
  }else if(transcode.equals("01")){
    if(captrueflag.equalsIgnoreCase("3")){//以處理
      if(feedbackcode.equalsIgnoreCase("000")){
        refund_appro_amt=refund_appro_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
        //refund_appro_cnt++;
        refund_appro_cnt = refund_appro_cnt + count;
      }else{//踢退
        refund_failer_amt=refund_failer_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
        //refund_failer_cnt++;
        refund_failer_cnt = refund_failer_cnt + count;
      }
    }else if(captrueflag.equalsIgnoreCase("0")){//未處理
      refund_wait_amt=refund_wait_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
     // refund_wait_cnt++;
      refund_wait_cnt = refund_wait_cnt + count;
    }else if(captrueflag.equalsIgnoreCase("2")){//已取消
      refund_void_amt=refund_void_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //refund_void_cnt++;
      refund_void_cnt = refund_void_cnt + count;
    }else if(captrueflag.equalsIgnoreCase("1")){//處理中
      refund_process_amt=refund_process_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //refund_process_cnt++;
      refund_process_cnt = refund_process_cnt + count;
    }
  }else if(transcode.equals("20")){
    if(captrueflag.equalsIgnoreCase("3")){//以處理
    if(feedbackcode.equalsIgnoreCase("000")){
      sale_appro_amt=sale_appro_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //sale_appro_cnt++;
      sale_appro_cnt = sale_appro_cnt + count;
    }else{//踢退
      sale_failer_amt=sale_failer_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //sale_failer_cnt++;
      sale_failer_cnt = sale_failer_cnt + count;
    }
    }else if(captrueflag.equalsIgnoreCase("0")){//未處理
      sale_wait_amt=sale_wait_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //sale_wait_cnt++;
      sale_wait_cnt = sale_wait_cnt + count;
    }else if(captrueflag.equalsIgnoreCase("2")){//已取消
      sale_void_amt=sale_void_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //sale_void_cnt++;
      sale_void_cnt = sale_void_cnt + count;
    }else if(captrueflag.equalsIgnoreCase("1")){//處理中
      sale_process_amt=sale_process_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //sale_process_cnt++;
      sale_process_cnt = sale_process_cnt + count;
    }
  }else if(transcode.equals("21")){
    if(captrueflag.equalsIgnoreCase("3")){//以處理
    if(feedbackcode.equalsIgnoreCase("000")){
      refund_appro_amt=refund_appro_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //refund_appro_cnt++;
      refund_appro_cnt = refund_appro_cnt + count;
    }else{//踢退
      refund_failer_amt=refund_failer_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //refund_failer_cnt++;
      refund_failer_cnt = refund_failer_cnt + count;
    }
    }else if(captrueflag.equalsIgnoreCase("0")){//未處理
      refund_wait_amt=refund_wait_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //refund_wait_cnt++;
      refund_wait_cnt = refund_wait_cnt + count;
    }else if(captrueflag.equalsIgnoreCase("2")){//已取消
      refund_void_amt=refund_void_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
     //refund_void_cnt++;
      refund_void_cnt = refund_void_cnt + count;
    }else if(captrueflag.equalsIgnoreCase("1")){//已取消
      refund_process_amt=refund_void_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //refund_process_cnt++;
      refund_process_cnt = refund_process_cnt + count;
    }
  }
}
} %>
     </tr>
     <tr>
       <td align="left">查詢日期:<%=String.valueOf(request.getAttribute("Start_TransDate"))%>~<%=String.valueOf(request.getAttribute("End_TransDate"))%></td>
       <td align="right">查詢端末機代碼:<%
       String TerminalID = String.valueOf(request.getAttribute("TerminalID"));
       if (TerminalID.equalsIgnoreCase("ALL")) TerminalID = "全部";
       out.write(TerminalID);
       %></td>
     </tr>
       <tr>
       <td align="left">請款方式:<%
       String ExceptFlag = String.valueOf(request.getAttribute("ExceptFlag"));
       if (ExceptFlag.equalsIgnoreCase("ALL")) out.write("全部");
       if (ExceptFlag.equalsIgnoreCase("MERCHANT")) out.write("特店請款");
       if (ExceptFlag.equalsIgnoreCase("ACQ")) out.write("收單行補請款");
       %></td>
       <td align="right"></td>
     </tr>

    </table>
   </td>
  </tr>
 </table>
<%if(type!=null&&type.equalsIgnoreCase("List")){%>
 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
    <tr align='center' bgcolor="#F4F4FF" height="25">
      <td align='center' rowspan="2">序號</td>
      <td align='center' rowspan="2">檢視</td>
      <td align='center' colspan="1">特約商店代號</td>
      <% if(!isSignMer){ %>
   	  <td align='center' rowspan="2">子特約商店代號</td>
      <%} %>
      <td align='center' colspan="1">特店指定單號</td>
      <td align='center' rowspan="2">卡號</td>
      <td align='center' colspan="1">交易類別</td>
      <td align='center' rowspan="2">交易日期</td>
      <td align='center' colspan="1">回應碼</td>
      <td align='center' rowspan="2">批次號碼</td>
      <td align='center' rowspan="2">交易幣別</td>
      <td align='center' rowspan="2">請款金額</td>
      <td align='center' rowspan="2">請款日期</td>
      <td align='center' colspan="1">請款回應碼</td>
      <td align='center' rowspan="2">請款方式</td>
    </tr>

    <tr align='center' bgcolor='#F4F4FF' height="25">
      <td align='center'>端末機代碼</td>
      <td align='center'>系統指定單號</td>
      <td align='center'>交易模式</td>
      <td align='center'>授權碼</td>
      <td align='center'>請款回應訊息</td>
    </tr>
<%
int page_00_success_amt=0;//購貨成功
int page_00_failer_amt=0;
int page_01_amt=0;//退貨
int page_2x_amt=0;
int page_11_amt=0;
int page_balance_amt=0;
int page_refund_amt=0;

String color="";
  int StartPoint = NowPage * MaxPcs;
  int EndPoint = NowPage * MaxPcs + MaxPcs;
  System.out.println("StartPoint="+StartPoint+" EndPoint="+EndPoint);
for(int i=StartPoint; i<EndPoint&& checkpoint.equals("XXX");++i){
  if (i >= capturelog.size())break;
  Hashtable content=(Hashtable)capturelog.get(i);
  String transcode=String.valueOf(content.get("TRANSCODE"));//交易代碼
  String feedbackcode=String.valueOf(content.get("FEEDBACKCODE"));//請款回覆碼
  String captrueflag=String.valueOf(content.get("CAPTUREFLAG"));//請款控制旗標
  //System.out.println(content);
  
    //Jason 201303
  String pan = String.valueOf(content.get("PAN"));//PAN
  //MerchantCaptureLogCtl mcl = new MerchantCaptureLogCtl();
  //pan = mcl.panenCode(pan);
  UserBean UserBean = new UserBean();
  pan = UserBean.get_CardStar(pan, 7,4);
  content.remove("PAN");
  content.put("PAN", pan);
  System.out.println("PAN:"+pan);
  
  
  
//  System.out.println("@@@@@@@@@@@@2");
  String balance_amt=String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT"));
  balance_amt=String.valueOf(Integer.parseInt(balance_amt)<=0?"0":balance_amt);
  //System.out.println("balance_amt="+balance_amt);
 // System.out.println("transcode="+transcode);
 // System.out.println("captrueflag=<"+captrueflag+">");
  if(transcode.equals("00")){//購貨交易
    color="black";
    transcode=transcode+" - 購貨";  //交易
    balance_amt=nf.format(Double.parseDouble(String.valueOf(balance_amt).trim().length()==0?"0":String.valueOf(balance_amt))) ;
    if(captrueflag.equalsIgnoreCase("3")){//以處理
    if(feedbackcode.equalsIgnoreCase("000")){
      //System.out.println("transcode="+transcode+" Amt="+content.get("CAPTUREAMT"));
      sale_appro_amt=sale_appro_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      sale_appro_cnt++;
    }else{//踢退
   // System.out.println("踢退transcode="+transcode+" Amt="+content.get("CAPTUREAMT"));
      sale_failer_amt=sale_failer_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      sale_failer_cnt++;
    }
    }else if(captrueflag.equalsIgnoreCase("0")){//未處理
  //  System.out.println("未處理transcode="+transcode+" Amt="+content.get("CAPTUREAMT"));
      sale_wait_amt=sale_wait_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      sale_wait_cnt++;
    }else if(captrueflag.equalsIgnoreCase("2")){//已取消
      sale_void_amt=sale_void_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      sale_void_cnt++;
    }else if(captrueflag.equalsIgnoreCase("1")){//處理中
      sale_process_amt=sale_process_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      sale_process_cnt++;
    }
  }else if(transcode.equals("01")){
    color="red";
    transcode=transcode+" - 退貨";  //交易
    balance_amt=nf.format(Double.parseDouble(String.valueOf(balance_amt).trim().length()==0?"0":String.valueOf(balance_amt))) ;
    if(captrueflag.equalsIgnoreCase("3")){//以處理
    if(feedbackcode.equalsIgnoreCase("000")){
      refund_appro_amt=refund_appro_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      refund_appro_cnt++;
    }else{//踢退
      refund_failer_amt=refund_failer_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      refund_failer_cnt++;
    }
    }else if(captrueflag.equalsIgnoreCase("0")){//未處理
      refund_wait_amt=refund_wait_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      refund_wait_cnt++;
    }else if(captrueflag.equalsIgnoreCase("2")){//已取消
      refund_void_amt=refund_void_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      refund_void_cnt++;
    }else if(captrueflag.equalsIgnoreCase("1")){//處理中
      refund_process_amt=refund_process_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      refund_process_cnt++;
    }
  }else if(transcode.equals("20")){
    color="blue";
    transcode=transcode+" - 購貨取消"; //交易
    balance_amt=nf.format(Double.parseDouble(String.valueOf(balance_amt).trim().length()==0?"0":String.valueOf(balance_amt))) ;
    if(captrueflag.equalsIgnoreCase("3")){//以處理
    if(feedbackcode.equalsIgnoreCase("000")){
      sale_appro_amt=sale_appro_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      sale_appro_cnt++;
    }else{//踢退
      sale_failer_amt=sale_failer_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      sale_failer_cnt++;
    }
    }else if(captrueflag.equalsIgnoreCase("0")){//未處理
      sale_wait_amt=sale_wait_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      sale_wait_cnt++;
    }else if(captrueflag.equalsIgnoreCase("2")){//已取消
      sale_void_amt=sale_void_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      sale_void_cnt++;
    }else if(captrueflag.equalsIgnoreCase("1")){//處理中
      sale_process_amt=sale_process_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      sale_process_cnt++;
    }
  }else if(transcode.equals("21")){
    color="blue";
    transcode=transcode+" - 退貨取消";//交易
    balance_amt=nf.format(Double.parseDouble(String.valueOf(balance_amt).trim().length()==0?"0":String.valueOf(balance_amt))) ;
    if(captrueflag.equalsIgnoreCase("3")){//以處理
    if(feedbackcode.equalsIgnoreCase("000")){
      refund_appro_amt=sale_appro_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      refund_appro_cnt++;
    }else{//踢退
      refund_failer_amt=sale_failer_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      refund_failer_cnt++;
    }
    }else if(captrueflag.equalsIgnoreCase("0")){//未處理
      refund_wait_amt=refund_wait_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      refund_wait_cnt++;
    }else if(captrueflag.equalsIgnoreCase("2")){//已取消
      refund_void_amt=refund_void_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      refund_void_cnt++;
    }else if(captrueflag.equalsIgnoreCase("1")){//處理中
      refund_process_amt=refund_process_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      refund_process_cnt++;
   }
  }else{
//   balance_amt=nf.format(Double.parseDouble(balance_amt)) ;
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
 String merchantid=String.valueOf(content.get("MERCHANTID"));
 String submid=String.valueOf(content.get("SUBMID"));
 String sysorderid=String.valueOf(content.get("SYS_ORDERID"));
 String rowid=String.valueOf(content.get("ROWID1"));
  %>

  <tr align='center' bgcolor='#ffffff'  height="25">
    <td align='left'   rowspan="2"><font color="<%=color%>"><%=i+1 %></font></td>
    <td align='left' rowspan="2">
      <a href="javascript:View_Data(0,'<%=merchantid %>','<%=submid %>','<%= sysorderid%>','<%= rowid%>');"><img src="images/button/view.jpg" alt="檢視" border="0"></a>
    </td>
    <td align='left'   colspan="1">
      <font color="<%=color%>"><%=String.valueOf(content.get("MERCHANTID"))%></font>
    </td>
    <% if(!isSignMer){ %>
      <td align='center' rowspan="2"><font color="<%=color%>"> <%=String.valueOf(content.get("SUBMID"))  %>  </font></td>
      <%} %>
    <td align='left'   colspan="1">
      <font color="<%=color%>"><%=String.valueOf(content.get("ORDERID"))  %></font>
    <td align='left'   rowspan="2">
      <font color="<%=color%>"><%=String.valueOf(content.get("PAN"))  %></font>
    </td>
    <td align='left'   colspan="1">
      <font color="<%=color%>"><%=String.valueOf(transcode)  %></font>
    </td>
    <td align='center' rowspan="2">
      <font color="<%=color%>"><%=String.valueOf(transdate)  %><br><%=String.valueOf(transtime)  %></font>
    </td>
    <td align='center' colspan="1">
      <font color="<%=color%>"><%=String.valueOf(content.get("RESPONSECODE"))  %></font>
    </td>
    <td align='center'   rowspan="2"><font color="<%=color%>"><%=String.valueOf(content.get("BATCHNO"))  %></font></td>
    <td align='center'   rowspan="2"><font color="<%=color%>">新台幣</font></td>
    <td align='right'  rowspan="2"><font color="<%=color%>"><%=balance_amt%></font></td>
    <td align='center'  rowspan="2"><font color="<%=color%>"><%=String.valueOf(content.get("CAPTUREDATE"))  %></font></td>
    <td align='left'  rowspan="1"><font color="<%=color%>"><%=String.valueOf(content.get("FEEDBACKCODE"))  %></font></td>
    <td align='left'  rowspan="2"><font color="<%=color%>"><%
    String strExceptFlag = String.valueOf(content.get("EXCEPT_FLAG"));
    if (strExceptFlag.equalsIgnoreCase("Y")) {
      out.write("收單行補請款");
    } else {
      out.write("特店請款");
    }

    %></font></td>
  </tr>

  <tr align='center' bgcolor='#ffffff' height="25">
    <td align='left'><font color="<%=color%>"><%=String.valueOf(content.get("TERMINALID"))  %></font></td>
    <td align='left'><font color="<%=color%>"><%=String.valueOf(content.get("SYS_ORDERID"))  %></font></td>
    <td align='left'><font color="<%=color%>"><%=String.valueOf(transmode)  %></font></td>
    <td align='center'><font color="<%=color%>"><%=String.valueOf(content.get("APPROVECODE"))  %></font></td>
    <td align='left'><font color="<%=color%>"><%=String.valueOf(content.get("FEEDBACKMSG"))  %></font></td>
  </tr><%//System.out.println("@@@@@@@@@@@@6"); %>
 <%  }if(checkpoint.equals("")){%>
  <tr align='center' bgcolor='#ffffff'  height="25">
  <td align='left'  colspan="15"  rowspan="2">查詢中...........</td>
  </tr>
 <%} %>
 </table>
 <br>
 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
 <tr align='center' bgcolor='#ffffff'  height="25">
   <td align='left'   rowspan="2"><font color="">本頁小計</font></td>
 </tr>
 </table>

 <br>
<%}
net_appro_amt=sale_appro_amt-refund_appro_amt;
net_appro_cnt=sale_appro_cnt+refund_appro_cnt;
net_failer_amt=sale_failer_amt-refund_failer_amt;
net_failer_cnt=sale_failer_cnt+refund_failer_cnt;
net_wait_amt=sale_wait_amt-refund_wait_amt;
net_wait_cnt=sale_wait_cnt+refund_wait_cnt;
net_void_amt=sale_void_amt-refund_void_amt;
net_void_cnt=sale_void_cnt+refund_void_cnt;
net_process_amt=sale_process_amt-refund_process_amt;
net_process_cnt=sale_process_cnt+refund_process_cnt;

%>
 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
  <tr bgcolor='#ffffff' height="25">
  <td align="center" width='10%'></td>
  <td align='center' width='30%' colspan="2">購貨請款</td>
  <td align='center' width='30%' colspan="2">退貨交易請款</td>
  <td align='center' width='30%' colspan="2">淨額</td>
  </tr>
  <tr bgcolor='#ffffff' height="25">
  <td align='center' width='10%'></td>
  <td align='center' width='15%'>筆數</td>
  <td align='center' width='15%'>金額</td>
  <td align='center' width='15%'>筆數</td>
  <td align='center' width='15%'>金額</td>
  <td align='center' width='15%'>筆數</td>
  <td align='center' width='15%'>金額</td>
  </tr>
  <tr bgcolor='#ffffff' height="25">
  <td align='center' width='10%'>已核准</td>
  <td align='right' width='15%'><%=nf_int.format(Integer.parseInt(String.valueOf(sale_appro_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(sale_appro_amt)))  %></td>
  <td align='right' width='15%'><%=nf_int.format(Double.parseDouble(String.valueOf(refund_appro_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(refund_appro_amt)))  %></td>
  <td align='right' width='15%'><%=nf_int.format(Double.parseDouble(String.valueOf(net_appro_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(net_appro_amt)))  %></td>
  </tr>

  <tr bgcolor='#ffffff' height="25">
  <td align='center' width='10%'>剔退</td>
  <td align='right' width='15%'><%=nf_int.format(Double.parseDouble(String.valueOf(sale_failer_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(sale_failer_amt)))  %></td>
  <td align='right' width='15%'><%=nf_int.format(Double.parseDouble(String.valueOf(refund_failer_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(refund_failer_amt)))  %></td>
  <td align='right' width='15%'><%=nf_int.format(Double.parseDouble(String.valueOf(net_failer_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(net_failer_amt)))  %></td>
  </tr>

  <tr bgcolor='#ffffff' height="25">
  <td align='center' width='10%'>待處理</td>
  <td align='right' width='15%' ><%=nf_int.format(Integer.parseInt(String.valueOf(sale_wait_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(sale_wait_amt)))  %></td>
  <td align='right' width='15%'><%=nf_int.format(Double.parseDouble(String.valueOf(refund_wait_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(refund_wait_amt)))  %></td>
  <td align='right' width='15%'><%=nf_int.format(Double.parseDouble(String.valueOf(net_wait_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(net_wait_amt)))  %></td>
  </tr>

  <tr bgcolor='#ffffff' height="25">
  <td align='center' width='10%'>取消</td>
  <td align='right' width='15%'><%=nf_int.format(Integer.parseInt(String.valueOf(sale_void_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(sale_void_amt)))  %></td>
  <td align='right' width='15%'><%=nf_int.format(Double.parseDouble(String.valueOf(refund_void_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(refund_void_amt)))  %></td>
  <td align='right' width='15%'><%=nf_int.format(Double.parseDouble(String.valueOf(net_void_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(net_void_amt)))  %></td>
  </tr>
  <tr bgcolor='#ffffff' height="25">
  <td align='center' width='10%'>處理中</td>
  <td align='right' width='15%'><%=nf_int.format(Integer.parseInt(String.valueOf(sale_process_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(sale_process_amt)))  %></td>
  <td align='right' width='15%'><%=nf_int.format(Double.parseDouble(String.valueOf(refund_process_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(refund_process_amt)))  %></td>
  <td align='right' width='15%'><%=nf_int.format(Double.parseDouble(String.valueOf(net_process_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(net_process_amt)))  %></td>
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
