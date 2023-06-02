<%@page contentType="text/html;charset=BIG5"%>
<%@page import="java.util.*" %>
<%@page import="java.text.*"%>
<%@page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%@page import="com.cybersoft.common.Util" %>
<!-- 20221028/jquery升級成3.6.0/Jeffery.Cheng/202210210533-00 -->
<%
  try {
/*
 * 202007070145-00 20200707 HKP 路跑協會需求UI增加整批請款功能
*/  
    response.addHeader("Pragma", "No-cache");
    response.addHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", 0);

    NumberFormat nf = NumberFormat.getInstance();
    nf.setMaximumFractionDigits(2);
    nf.setMinimumFractionDigits(2);
    NumberFormat nf0 = NumberFormat.getInstance();
    nf0.setMaximumFractionDigits(0);
    nf0.setMinimumFractionDigits(0);

    Hashtable userinfo=(Hashtable)session.getAttribute("SYSCONFDATA");
    Hashtable sysinfo=(Hashtable)userinfo.get("SYSCONF");
    /**
     * session key:[hashData]CaptureBatchData :[String]Message,[arrayData]DATALIST ,[hashQuery]QUERY       
     **/
     Hashtable hashCaptureData = new Hashtable();
     String sMessage = "",BatchContent = "",sSUBMITBATCH_DISABLE = "";
 	 String StartTransDate="",EndTransDate="",TransCode="";
     ArrayList arrayList = new ArrayList();
     Hashtable hashQuery = new Hashtable();
     ArrayList arrayFILESTATUS = new ArrayList();
     ArrayList arrayBatchFailGroup = new ArrayList();

     if (session.getAttribute("CaptureBatchData") != null) {
     	hashCaptureData = (Hashtable)session.getAttribute("CaptureBatchData");
     	if (hashCaptureData.size()>0) {
     		sMessage = Util.objToStrTrim(hashCaptureData.get("Message"));
     		sSUBMITBATCH_DISABLE = Util.objToStrTrim(hashCaptureData.get("SUBMITBATCH_DISABLE"));
     		arrayList = (ArrayList)hashCaptureData.get("DATALIST");
     		arrayFILESTATUS = (ArrayList)hashCaptureData.get("FILESTATUS"); //FILESTATUS
     		if(arrayFILESTATUS==null) arrayFILESTATUS = new ArrayList();    //FILESTATUS
     		arrayBatchFailGroup = (ArrayList)hashCaptureData.get("BatchFailGroup"); //BatchFailGroup
     		if(arrayBatchFailGroup == null) arrayBatchFailGroup = new ArrayList();    //BatchFailGroup
     		
     		hashQuery = (Hashtable)hashCaptureData.get("QUERY");
     		hashCaptureData.clear();hashCaptureData=null;
     	}
     }
  	StartTransDate = Util.objToStrTrim(hashQuery.get("StartTransDate"));
  	EndTransDate = Util.objToStrTrim(hashQuery.get("EndTransDate"));
  	TransCode = Util.objToStrTrim(hashQuery.get("TransCode"));
 	BatchContent = Util.objToStrTrim(hashQuery.get("BatchContent"));//批次請款用LIST:顯示資料;RESULT:顯示結果
 	hashQuery.clear();hashQuery=null;
 	String Action;
 	if(BatchContent.equals("LIST")==true){
 		Action = "BATCHCAPTURE";
 	}else{
 		Action = "FirstQuery";
 	}
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html" >
  <link href="css/style.css" type="text/css" rel="stylesheet">
  <script type="text/javascript" language="javascript" src="./js/Vatrix.js"></script>
  <script type="text/javascript" src="./js/jquery-3.6.0.min.js"></script>
  <script type="text/javascript">
  $(document).ready(function () {
	  $("#divProcess").hide();
  });
  function blink(selector) {
      $(selector).fadeOut(1000, function () {
          $(this).fadeIn(1000, function () {
              blink(this);
          });
      });
  };
  </script>
  <script type="text/javascript" language="JavaScript">

  function toSummitBatch(){
	  if(confirm("確認要執行整批請款!!")==false) return void(0);
	  $("#btnSubmit").attr('disabled', 'disabled');
	  $("#divProcess").show().fadeIn(1000);
	  blink('.blink');//文字閃爍
      document.form.submit();
  }
  function realsubmit(){
    document.form.Action.value = 'Query';
    document.form.submit();
  }

  function toBack(){
    document.form.Action.value = '';
    document.form.submit();
  }

</script>

</head>

<body>
<form id="form" name="form" method="post" action ="./MerchantCaptureCtl" >
    <input type="hidden" name="Action" id="Action" value="<%=Action %>" />
    <input type="hidden" name="token" id="token" value="" />
    <input type="hidden" name="StartTransDate" id="StartTransDate" value="<%=StartTransDate %>" />
    <input type="hidden" name="EndTransDate" id="EndTransDate" value="<%=EndTransDate %>" />
    <input type="hidden" name="TransCode" id="TransCode" value="<%=TransCode %>" />
 <%
 String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);
 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
      <td align="left" valign="middle"><b><font color="#004E87" size="3">線上交易請款作業</font></b></td>
  </tr>
</table>
<hr style="height:1px">
  <%if(sMessage.length() > 0){ %>
  <font color="RED" size="3">訊息：<%=sMessage %></font>
  <% }%>
  <div id="divProcess">
        <div class="form-row">
            <div class="col-12 col-md-11 text-center">
                <font color="blue" size="3"><b class="blink">交易處理中請稍後(Please wait)....</b></font>
            </div>
        </div>
  </div>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr bgcolor="#F4F4FF" height="25">
      <td>
      	<font color="BLUE" size="3"><b>整批請款</b></font>
      	<%if(BatchContent.equals("LIST")){ %>
      	(可請款的筆數、金額)
      	<%} else{ %>
      	<font color="BLUE">(結果通知)</font>
      	<%} %>
      </td>
    </tr>
  </table>
   
   <table border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
     <tr align='center' bgcolor="#F4F4FF" height="25">
       <td height="30" width="100" align="left">&nbsp;</td>
       <td width="100" align="right">總筆數</td>
       <td width="100" align="right">總金額</td>
     </tr>
     <%if(arrayList.size()==0){ %>
     <tr>
       <td height="30" colspan=3 align="left" bgcolor='#ffffff'><font color="#FF0000" size="3"><b>查無可請款資料</b></font></td>
     </tr>
     <%}%>
     <%
     Hashtable hashData;
     String sMemo ;int iCNT;double dAMT;
     String fontColor="";
     int iTotalCNT=0;double dTotalAMT=0;
     for(int i=0 ;i < arrayList.size();i++){
   	  hashData =(Hashtable)arrayList.get(i);
   	  TransCode = Util.objToStr(hashData.get("TRANSCODE"));
   	  iCNT = Util.objToInt(hashData.get("CNT"));
   	  dAMT = Util.objToDouble(hashData.get("TOTAL_AMT"));
   	  
   	  iTotalCNT+=iCNT;
   	  
   	  if("00".equals(TransCode)){
   		  dTotalAMT += dAMT;
   		  sMemo = "購貨請款";
   		  fontColor ="BLUE";
   	  }else if("01".equals(TransCode)){
   		  dTotalAMT -= dAMT;
   		  sMemo = "退貨請款";
   		  fontColor ="RED";
   	  }else if("00FAIL".equals(TransCode)){
   		  dTotalAMT += dAMT;
   		  sMemo = "購貨請款(失敗)";
   		  fontColor ="BLUE";
   	  }else if("01FAIL".equals(TransCode)){
   		  dTotalAMT -= dAMT;
   		  sMemo = "退貨請款(失敗)";
   		  fontColor ="RED";
   	  }
   	  else{
   		  sMemo = TransCode;
   	  }
     %>
     <tr bgcolor='#ffffff'>
       <td align="left"><font color="<%=fontColor %>"><%=sMemo %></font></td>
       <td align="right"><font color="<%=fontColor %>"><%=nf0.format(iCNT) %></font></td>
       <td align="right"><font color="<%=fontColor %>"><%=nf.format(dAMT) %></font></td>
     </tr>
     <%}%>
     <tr bgcolor='#ffffff'>
       <td align="left">合計</td>
       <td align="right"><%=nf0.format(iTotalCNT) %></td>
       <td align="right"><%=nf.format(dTotalAMT) %></td>
     </tr>
   </table>

<%if(BatchContent.equals("LIST") && arrayList.size() > 0){ %>
   <table width="100%" border="0" cellspacing="0" cellpadding="0">
     <tr>
       <td height="30" align="left">
       <% if(sSUBMITBATCH_DISABLE.equals("Y")== false){ %>
           <input type='button' value='確定請款' name='btnSubmit' id='btnSubmit' onclick="toSummitBatch();">
       <%} %>
       </td>
     </tr>
    <tr bgcolor="#F4F4FF" height="25">
      <td>
          <font color="BLUE">
          (1).試算可請款筆數與金額，不包含已超過可請款期限之資料<br/>
          (2).假如日期區間包含當日，在執行【確定請款】功能時如有新增的請款交易將會列入此次批次請款中。
          </font>
      </td>
    </tr>
   </table>
<%} %>
<%if(arrayBatchFailGroup.size() > 0){ %>
<hr/>
  <table border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
    <tr bgcolor="#F4F4FF" height="25">
      <td align='left' colspan="22">
      	<B> >> <font color="BLUE">批次請款錯誤訊息彙總</font></B>
      </td>
    </tr>
    <tr align='center' bgcolor="#F4F4FF" height="25">
       <td align='center'>交易日期</td>
       <td align='center'>錯誤訊息</td>
       <td align='center'>筆數</td>
       <td align='center'>金額</td>
    </tr>
     <%for (int i=0; i<arrayBatchFailGroup.size(); i++) { 
    	 Hashtable batchFail = (Hashtable)arrayBatchFailGroup.get(i);
     %>
    <tr align='center' bgcolor='#ffffff'  height="25">
       <td align="left" ><font color=""><%=Util.objToStrTrim(batchFail.get("TRANSDATE")) %> </font></td>
       <td align="left" ><font color=""><%=Util.objToStrTrim(batchFail.get("FEEDBACKMSG")) %></font></td>
       <td align="right"><font color=""><%=nf0.format(Util.objToInt(batchFail.get("CNT"))) %></font></td>
       <td align="right"><font color=""><%=nf.format(Util.objToDouble(batchFail.get("BALANCEAMT"))) %></font></td>
    </tr>
     <%} %>
  </table>
<%} %>
<hr/>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr>
    	<td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="30" align="left"><input type='button' value='回請款畫頁' name='btnSubmit' id='btnSubmit' onclick="toBack();"></td>
          </tr>
        </table>
        </td>
    </tr>
  </table>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
    <tr bgcolor="#F4F4FF" height="25">
      <td align='left' colspan="22">
      	<B> >> <font color="BLUE">整批請款紀錄(最近一星期)</font></B>
      </td>
    </tr>
    <tr align='center' bgcolor="#F4F4FF" height="25">
       <td align='center' rowspan="2">請款日期</td>
       <td align='center' rowspan="2">批次號碼</td>
       <td align='center' colspan="6">批次請款</td>
       <td align='center' colspan="4">總執行狀態</td>
       <td align='center' colspan="4">購貨請款執行狀態</td>
       <td align='center' colspan="4">退貨請款執行狀態</td>
       <td align='center' rowspan="2">處理狀態</td>
       <td align='center' rowspan="2">處理時間</td>
     </tr>
     <tr align='center' bgcolor='#F4F4FF' height="25">
       <td align='center'>總筆數</td>
       <td align='center'>總金額</td>
       <td align='center'>購貨總筆數</td>
       <td align='center'>購貨總金額</td>
       <td align='center'>退貨總筆數</td>
       <td align='center'>退貨總金額</td>
       <td align='center'>總成功筆數</td>
       <td align='center'>總成功金額</td>
       <td align='center'>總失敗筆數</td>
       <td align='center'>總失敗金額</td>
       <td align='center'>成功筆數<!-- 購貨 --></td>
       <td align='center'>成功金額<!-- 購貨 --></td>
       <td align='center'>失敗筆數<!-- 購貨 --></td>
       <td align='center'>失敗金額<!-- 購貨 --></td>
       <td align='center'>成功筆數<!-- 退貨 --></td>
       <td align='center'>成功金額<!-- 退貨 --></td>
       <td align='center'>失敗筆數<!-- 退貨 --></td>
       <td align='center'>失敗金額<!-- 退貨 --></td>
     </tr>
     <%for (int i=0; i<arrayFILESTATUS.size(); i++) { 
    	 Hashtable filestatus = (Hashtable)arrayFILESTATUS.get(i);
     %>
     <tr align='center' bgcolor='#ffffff'  height="25">
       <td align="left" ><font color=""><%=Util.objToStrTrim(filestatus.get("FILE_DATE")) %> <!-- 請款日期 --></font></td>
       <td align="left" ><font color=""><%=Util.objToStrTrim(filestatus.get("FILE_BATCHNO")) %><!-- 批次號碼--></font></td>
       <td align="right"><font color=""><%=nf0.format(Util.objToInt(filestatus.get("FILE_TOTAL_COUNT"))) %><!-- 批次請款總筆數--></font></td>
       <td align="right"><font color=""><%=nf.format(Util.objToDouble(filestatus.get("FILE_TOTAL_AMOUNT"))) %><!-- 批次請款總金額--></font></td>
       <td align="right"><font color=""><%=nf0.format(Util.objToInt(filestatus.get("FILE_PURCHASE_COUNT"))) %><!-- 購貨總筆數  --></font></td>
       <td align="right"><font color=""><%=nf.format(Util.objToDouble(filestatus.get("FILE_PURCHASE_AMOUNT"))) %><!-- 購貨總金額--></font></td>
       <td align="right"><font color=""><%=nf0.format(Util.objToInt(filestatus.get("FILE_REFUND_COUNT"))) %><!-- 退貨總筆數 --></font></td>
       <td align="right"><font color=""><%=nf.format(Util.objToDouble(filestatus.get("FILE_REFUND_AMOUNT"))) %><!-- 退貨總金額 --></font></td>
       <td align="right"><font color=""><%=nf0.format(Util.objToInt(filestatus.get("SUCCESS_COUNT"))) %><!-- 成功筆數 --></font></td>
       <td align="right"><font color=""><%=nf.format(Util.objToDouble(filestatus.get("SUCCESS_AMOUNT"))) %><!-- 成功金額 --></font></td>
       <td align="right"><font color=""><%=nf0.format(Util.objToInt(filestatus.get("REJECT_COUNT"))) %><!-- 失敗筆數 --></font></td>
       <td align="right"><font color=""><%=nf.format(Util.objToDouble(filestatus.get("REJECT_AMOUNT"))) %><!-- 失敗金額 --></font></td>
       <td align="right"><font color=""><%=nf0.format(Util.objToInt(filestatus.get("PURCHASE_SUCCESS_COUNT"))) %><!-- 購貨成功筆數 --></font></td>
       <td align="right"><font color=""><%=nf.format(Util.objToDouble(filestatus.get("PURCHASE_SUCCESS_AMOUNT"))) %><!-- 購貨成功金額 --></font></td>
       <td align="right"><font color=""><%=nf0.format(Util.objToInt(filestatus.get("PURCHASE_REJECT_COUNT"))) %><!-- 購貨失敗筆數 --></font></td>
       <td align="right"><font color=""><%=nf.format(Util.objToDouble(filestatus.get("PURCHASE_REJECT_AMOUNT"))) %><!-- 購貨失敗金額 --></font></td>
       <td align="right"><font color=""><%=nf0.format(Util.objToInt(filestatus.get("REFUND_SUCCESS_COUNT"))) %><!-- 退貨成功筆數 --></font></td>
       <td align="right"><font color=""><%=nf.format(Util.objToDouble(filestatus.get("REFUND_SUCCESS_AMOUNT"))) %><!-- 退貨成功金額 --></font></td>
       <td align="right"><font color=""><%=nf0.format(Util.objToInt(filestatus.get("REFUND_REJECT_COUNT"))) %><!-- 退貨失敗筆數 --></font></td>
       <td align="right"><font color=""><%=nf.format(Util.objToDouble(filestatus.get("REFUND_REJECT_AMOUNT"))) %><!-- 退貨失敗金額 --></font></td>
       <td align="center"><font color="">
       <%String sFILE_ATTRIBUTE = Util.objToStrTrim(filestatus.get("FILE_ATTRIBUTE")); 
         if(sFILE_ATTRIBUTE.equals("CF")==true){
        	 sFILE_ATTRIBUTE = "資料處理中";
         }else if(sFILE_ATTRIBUTE.equals("SF")==true){
        	 sFILE_ATTRIBUTE = "請款交易執行中";
         }else if(sFILE_ATTRIBUTE.equals("RF")==true){
        	 sFILE_ATTRIBUTE = "執行完畢";
         }
         out.write(sFILE_ATTRIBUTE);
       %>
       <!-- 處理狀態 --></font></td>
       <td align="center"><font color=""><%=Util.objToStrTrim(filestatus.get("PROCESS_DATE")) %><!-- 處理時間 --></font></td>
     </tr>
         
     <%} %>
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
