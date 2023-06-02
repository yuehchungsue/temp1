<%@page contentType="text/html; charset=BIG5"%>
<%@ page language="java" %>
<%@ page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page isELIgnored="false" %>

<link rel="stylesheet" href='<c:url value="css/bootstrap_sign.min.css" />' media="screen"/>
<%
  response.addHeader("Pragma", "No-cache");
  response.addHeader("Cache-Control", "no-cache");
  response.addDateHeader("Expires", 1);
%>
<html>
<head>
 <meta http-equiv="Content-Type" content="text/html charset=big5">
 <link rel="stylesheet" href='css/style_sign.css' />
<!-- <link rel="stylesheet" href="css/style.css" type="text/css"> -->
<link rel="stylesheet" href='css/responsive.css' />
<link rel="stylesheet" href='css/alert.css' />
<script language="JavaScript" src="js/Vatrix.js"></script>
<script language="JavaScript" src="js/calendar.js" ></script>
<script language="JavaScript" src="js/jquery-3.6.0.min.js"></script>


<jsp:include page="signInfo.jsp" />
</head>

<body bgcolor="#ffffff">
	<div class="box"></div>
		<div class="box_inv">
			<div class="posbox_button">
				<ul>
					<li><img src="images/print_pdf.png" class="img-responsive" border="0" alt="" onclick="downloadPdf()"></li>
					<li><img src="images/print_pr.png" class="img-responsive" border="0" alt=""  onclick="printMe()"></li>
					<li class="btn-cancel"><img src="images/print_cancel.png" class="img-responsive" border="0" alt="" onclick="cancel();"></li>
				</ul>
			</div>
		</div>
		
		<div class="posbox" id="posbox1" style="transform: scale(1.2, 1.2);margin-top: 0;transform-origin:center 10px"></div>
		<div class="posbox" id="posboxF" style="transform: scale(1.2, 1.2); margin-top: 0;transform-origin:center 10px"></div>
		<div class="posPrint" id="printPdf"></div>
		<div class="posPrint" id="printPdfF"></div>
		<div class="posPrint" id="downloadPdf"></div>
		<div class="posPrint" id="downloadPdfF"></div>
	
<form method="post" name="form" id="form" action="./MerchantSignBillCtl">
	<input type='hidden' name='Action' id='Action' value='QUERY'/>
 <%
String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);
 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr id="head">
   <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
   <td align="left" valign="middle"><b><font color="#004E87" size="3">電子簽單查詢條件</font></b></td>
  </tr>
 </table>

 <!-- hr style="height:1px" -->
 
 
 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
	<tr align='center' height="25"  class="dateQry" >
		<td width='25%'  bgcolor='#F4F4FF' align='right'>特約商店代號(*)</td>
		<td bgcolor='#ffffff' align='left' >
		<!--行員查詢顯示輸入框，特店端查詢顯示子特店下拉選單-->
		   <c:choose>
			<c:when test="${SIGN_BILL_Flag=='B'}">
				<input id="merchantId" name="merchantId" value="${merchantId_type_B}" type="text" maxlength="15" size="20" onkeyup="value=value.replace(/[^\d]/g,'')">
			</c:when>
			<c:otherwise>
				<select id="merchantId" name="merchantId">
				<c:forEach var="merchantStore" items="${allMerchantStore}">
					<c:choose>		
						<c:when test="${merchantStore.mid==MERCHANT_ID_choosen}">
							<option selected="selected" value="${merchantStore.mid}">${merchantStore.mid}_${merchantStore.callName}</option>
						</c:when>		
						<c:otherwise>
							<option value="${merchantStore.mid}">${merchantStore.mid}_${merchantStore.callName}</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
				</select>
			</c:otherwise>
			</c:choose>
			<input type='button' value='查詢' name='btnQuery' id='btnQuery' />
		    <input type='button' value='清除畫面' name='btnClear' id='btnClear' onclick='clearall()' >
		</td>
		<td nowrap="nowrap" align='right' width="220px">
			<input type="button" value="交易日查詢" id="btn_QryByDate" class="btnQryCondition btn btn-warning btn-sm" style="width:100px;height:30px;float:left"/>	
			<input type="button" value="進階查詢" id="btn_DetailQry" class="btnQryCondition btn btn-warning btn-sm" style="width:100px;height:30px;"/>
		</td>
	</tr>
	<tr align='center' height="25" class="dateQry">
		<td align='right' bgcolor='#F4F4FF' >交易日期(*)</td>
		<td align='left'  bgcolor='#FFFFFF' id='TransDate_td'>
		    <input type="Text" name="Start_TransDate" id="Start_TransDate" value="${Start_TransDate}" class="calendar" size="20" maxlength="10"/>
		    <img src="images/calendar/cal.gif" style="cursor:hand" border="0" alt="日期輸入"  class="calendar" onclick="javascript:datepopup(document.all.Start_TransDate)">
		    <span class='calendar'>~</span>
		    <input type="Text" name="End_TransDate" id="End_TransDate" value="${End_TransDate}" class="calendar" size="20">
		    <img src="images/calendar/cal.gif" style="cursor:hand" border="0" alt="日期輸入"  class="calendar" onclick="javascript:datepopup(document.all.End_TransDate)">
		    <input type="Text" name="TransDate" id="TransDate" value="${TransDate}" class="calendar qryByDate" size="20" hidden="hidden" maxlength="10"/>
    		<img src="images/calendar/cal.gif" style="cursor:hand" border="0" alt="日期輸入"  class="calendar qryByDate" hidden="hidden" onclick="javascript:datepopup(document.all.TransDate)">
		</td>
		<td/>
	</tr>
  <!-- ************************************************** -->
  <!-- 卡號、POS代號、批次序號、調閱編號、金額、授權碼，標籤內onkeyup屬性之值=> onkeyup="value=value.replace(/[^\d]/g,'')" ： 將非數字字元改成空字串 -->
  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>卡號</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
   	<input type='text' id='CARD_NO' name='CARD_NO' size='20' maxlength='19' value='${CARD_NO}' onkeyup="value=value.replace(/[^\d]/g,'')"/>
   </td>
		<td/>
  </tr>
  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>POS代號</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
   	<input type='text' id='TERMINAL_ID' name='TERMINAL_ID' size='10' maxlength='8' value="${TERMINAL_ID}" onkeyup="value=value.replace(/[^\d]/g,'')"/>
   </td>
		<td/>
  </tr>
  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>批次序號</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
   	<input type='text' id='BATCH_NO' name='BATCH_NO' size='8' maxlength='6' value="${BATCH_NO}" onkeyup="value=value.replace(/[^\d]/g,'')"/>
   </td>
		<td/>
  </tr>
  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>調閱編號</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
   	<input type='text' id='REF_NO' name='INVOICE_NO' size='8' maxlength='6' value="${INVOICE_NO}" onkeyup="value=value.replace(/[^\d]/g,'')"/>
   </td>
		<td/>
  </tr>
  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>金額</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
   	<input type='text' id='TX_AMOUNT' name='TX_AMOUNT' size='16' maxlength='14' value="${TX_AMOUNT}" onkeyup="value=value.replace(/[^\d]/g,'')"/>
   </td>
		<td/>
  </tr>
  <tr align='center' height="25">
   <td width='25%' align='right' bgcolor='#F4F4FF'>授權碼</td>
   <td width='75%' align='left'  bgcolor='#FFFFFF'>
   	<input type='text' id='AUTH_CODE' name='AUTH_CODE' size='8' maxlength='6' value="${AUTH_CODE}" onkeyup="value=value.replace(/[^\da-zA-Z]/g,'')"/>
   </td>
		<td/>
  </tr>
 </table>
 
 <hr/>
 </form>
	<!--  pos代號顯示 -->
	<c:if test="${tErmlogGroup_DistinctPos!=null}">
	<div id="PosIDlist" class="in-right-second">
		<div class="col-md-12">
			<div class="data-table">
				<div class="row">
					<div class="data-table-in col-md-12 col-sm-12">
						<div class="in-right-second dataPos">
							<p>請選擇下方POS代號</p>
							<ul>
								<c:forEach var="obj" items="${tErmlogGroup_DistinctPos}">
									<li><a href="#">
											<div class="signPos">
												<div id='PosID_btn' class="signPosin" value='${obj.terminal_id}'
													onclick="txDateResultDetail('${obj.terminal_id}');">${obj.terminal_id}</div>
											</div>
									</a></li>
								</c:forEach>
							</ul>
						</div>
						
						<c:if test="${empty tErmlogGroup_DistinctPos}">
<!-- 							<div class="result-in"> -->
<!-- 								<div class="resultout-search"> -->
									<span><font color="blue"> **查無資料**</font></span>
<!-- 								</div> -->
<!-- 							</div> -->
						</c:if>
	
					</div>
				</div>
			</div>
		</div>
	</div>
	</c:if>
	<!--  pos代號顯示  end-->
	<!-- 	pos代號表單 -->
	<form id="singForm" method="post">
		<%
				out.println("<input type='hidden' name='" + MerchantMenuCtl.MENU_FORWARD_KEY + "' id='"
						+ MerchantMenuCtl.MENU_FORWARD_KEY + "' value='" + MenuKey + "' />");
		%>
		<input type="text" style="display: none" id="pos_Action" name="Action" value="POS_ID" />
		<input type="text" style="display: none" id="merchant_id" name="merchant_id" value="${merchant_id}" />
		<input type="text" style="display: none" id="process_date" name="process_date" value="${process_date}" />
		<input type="text" style="display: none" id="merchant_name" name="merchant_name" value="${merchant_name}" />
		<input type="text" style="display: none" id="terminal_id" name="terminal_id" value="${terminal_id}" />
	</form>
	<!-- 	pos代號表單 end-->

		     	<!-- 分頁筆數form -->
		     	<c:if test="${tErmlogGroup!=null && !(empty tErmlogGroup)}">
				<form class="perlog" id="perPageNumForm" method="post">
					<%
						out.println("<input type='hidden' name='" + MerchantMenuCtl.MENU_FORWARD_KEY + "' id='"
								+ MerchantMenuCtl.MENU_FORWARD_KEY + "' value='" + MenuKey + "' />");
					%>
					<input type="text" style="display: none" id="perPageNum_Action" name="Action" value="PerPageNum" /> 
					
					<input type="text" style="display: none" id="merchant__id" name="merchant__id" value="${merchant__id}" />
					
					<input type="text" style="display: none" id="process__date" name="process__date" value="${process__date}" />
					<input type="text" style="display: none" id="start__date" name="start__date" value="${start__date}" />
					<input type="text" style="display: none" id="end__date" name="end__date" value="${end__date}" />
					
					<input type="text" style="display: none" id="card__no" name="card__no" value="${card__no}" />
					<input type="text" style="display: none" id="terminal__id" name="terminal__id" value="${terminalId}" />
					<input type="text" style="display: none" id="batch__no" name="batch__no" value="${batch__no}" />
					<input type="text" style="display: none" id="ref__no" name="ref__no" value="${ref__no}" />
					<input type="text" style="display: none" id="tx__amount" name="tx__amount" value="${tx__amount}" />
					<input type="text" style="display: none" id="auth__code" name="auth__code" value="${auth__code}" />
					
					<input type="text" style="display: none" id="perPage__num" name="perPage__num" value="${perPage__num}" />
					<input type="text" style="display: none" id="totalPage__num" name="totalPageNum" value="${totalPageNum}" />
					
						
					每頁筆數 
					<select id="perPageNumSelecter" name="perPageNum" >
<%-- 						在<c:forTokens>的items屬性內控制每頁筆數，可輸入"all"對應後端程式在1頁上顯示所有結果 --%>
						<c:forTokens items="10,25,50,100,500" delims="," var="num">
							<c:choose>
							<c:when test="${num==perPageNum_choosen}">
								<option selected="selected" value="${num}">${num}</option>
							</c:when>
							<c:otherwise>
								<option  value="${num}">${num}</option>
							</c:otherwise>
							</c:choose>
						</c:forTokens>
					</select>
				</form>
				</c:if>
				<!-- 分頁筆數form end-->

	<!-- 顯示明細資料 -->
	<c:if test="${Action!=null && !(empty Action)}">
	 <table
	  class="perlog"
	 width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
		
	     <thead>
	        <tr id="logListHead" align='center' bgcolor="#F4F4FF" height="25">
			   <th align='center' colspan="1">卡號</th>
			   <th align='center' colspan="1">POS代號</th>
			   <th align='center' colspan="1">批次序號</th>
			   <th align='center' rowspan="1">調閱編號</th>
			   <th align='center' colspan="1">交易日期</th>
			   <th align='center' colspan="1">交易金額</th>
			   <th align='center' colspan="1">授權碼</th>
			   <th align='center' colspan="1">交易類別</th>
			   <th align='center' colspan="1">電子簽單</th>
	        </tr>
	    </thead>
	<!--   <tr id="logListHead" align='center' bgcolor="#F4F4FF" height="25"> -->
	<!--    <td align='center' colspan="1">卡號</td> -->
	<!--    <td align='center' colspan="1">POS代號</td> -->
	<!--    <td align='center' colspan="1">批次序號</td> -->
	<!--    <td align='center' rowspan="1">調閱編號</td> -->
	<!--    <td align='center' colspan="1">交易日期</td> -->
	<!--    <td align='center' colspan="1">交易金額</td> -->
	<!--    <td align='center' colspan="1">授權碼</td> -->
	<!--    <td align='center' colspan="1">交易類別</td> -->
	<!--    <td align='center' colspan="1">電子簽單</td> -->
	<!--   </tr> -->

		<c:if test="${tErmlogGroup!=null && empty tErmlogGroup}">
			<tr class='perlog'><td><span><font color="blue"> **查無資料**</font></span></td></tr>
		</c:if>
		
	<c:if test="${tErmlogGroup!=null && !(empty tErmlogGroup)}">
		<tbody>
			<c:forEach var="tErmlog" items="${tErmlogGroup}">
			<tr class='perlog' align='center' bgcolor='#ffffff' height="25">
				<td align='left'>${tErmlog.card_no}</td>
			    <td align='left'>${tErmlog.terminal_id}</td>
			    <td align='left'>${tErmlog.batch_no}</td>
			    <td align='left'>${tErmlog.invoice_no}</td>
			    <td align='left'>${tErmlog.tx_date}</td>
			    <td align='left'><div style="opacity: 0.0; float: left;">*</div> <fmt:formatNumber value="${tErmlog.tx_amount}" pattern="###,###,###,###.##" /></td>
			    <td align='left'>${tErmlog.auth_code}</td>
			    <td align='left'>${tErmlog.trans_type_show_cht} ${tErmlog.trans_type_show_eng}</td>
			    <td align='left'>
				    <a class='pdf_link' href="#" onclick="signInfo('${tErmlog.trans_id}');">
				    	<img src="images/pdf2.png" border="0" alt="" width="30%" height="30%">
				    </a>
			    </td>
			</tr>
			</c:forEach>
		</tbody>
	</c:if>
	 </table>
	<c:if test="${tErmlogGroup!=null && !(empty tErmlogGroup)}">
	<div class="perlog" style="text-align: center;">
|
	<c:forEach var="page" begin="1" end="${totalPageNum}">
<!-- 		當前頁面頁數無超連結 -->
		<c:choose>
		<c:when test="${page==page_choosen}">
			${page} |
		</c:when>
		<c:otherwise>
<!-- 			第一次查詢時，每頁筆數預設=10 -->
			<c:choose>
				<c:when test="${perPageNum_choosen==null}">
					<a onclick="showChoosenPage('10','${page}')">${page} </a>|
				</c:when>
				<c:otherwise>
					<a onclick="showChoosenPage('${perPageNum_choosen}','${page}')">${page} </a>|
				</c:otherwise>
			</c:choose>
			
		</c:otherwise>
		</c:choose>	
	</c:forEach>
<!-- 		選擇分頁form -->
		<form class="perlog" id="ChoosenPageForm" method="post">
		 	<%
				out.println("<input type='hidden' name='" + MerchantMenuCtl.MENU_FORWARD_KEY + "' id='"
						+ MerchantMenuCtl.MENU_FORWARD_KEY + "' value='" + MenuKey + "' />");
			%>
			<input type="text" style="display: none" id="choosenPage_Action" name="Action" value="ChoosenPage" /> 
			
			<input type="text" style="display: none" id="merchant___id" name="merchant___id" value="" />
			
			<input type="text" style="display: none" id="process___date" name="process___date" value="" />
			<input type="text" style="display: none" id="start___date" name="start___date" value="" />
			<input type="text" style="display: none" id="end___date" name="end___date" value="" />
			
			<input type="text" style="display: none" id="card___no" name="card___no" value="" />
			<input type="text" style="display: none" id="terminal___id" name="terminal___id" value="${terminalId}" />
			<input type="text" style="display: none" id="batch___no" name="batch___no" value="" />
			<input type="text" style="display: none" id="ref___no" name="ref___no" value="" />
			<input type="text" style="display: none" id="tx___amount" name="tx___amount" value="" />
			<input type="text" style="display: none" id="auth___code" name="auth___code" value="" />
			
			<input type="text" style="display: none" id="choosenPage" name="choosenPage" value="" />
			<input type="text" style="display: none" id="perPage___num" name="perPage___num" value="" />
			<input type="text" style="display: none" id="totalPage___num" name="totalPageNum" value="${totalPageNum}" />
		</form>
	</div>
	</c:if>
	</c:if>
 
</body>
<head>
<script language="JavaScript" type="text/JavaScript">
$("#btnQuery").click(function() {
	if('${SIGN_BILL_Flag}'=='B'){
		if($('#merchantId').val().length!=15){
			alert('特店代號須為15碼')
			return;
		}
	}
	
	if($('#Action').val()=='QUERY'){
		if(checkdate() && checkInput()){
			$("#form").submit();
		}
	}else if($('#Action').val()=='QryByDate'){
		if(checkdate()){
			$("#form").submit();
		}
	}
});

function checkInput() {
	var CARD_NO=$('#CARD_NO').val();
	var TERMINAL_ID=$('#TERMINAL_ID').val();
	var BATCH_NO=$('#BATCH_NO').val();
	var REF_NO=$('#REF_NO').val();
	var TX_AMOUNT=$('#TX_AMOUNT').val();
	var AUTH_CODE=$('#AUTH_CODE').val();
	
	if($('#Action').val()=='QUERY'){
		if (CARD_NO.length == 0
			&& TERMINAL_ID.length == 0
			&& BATCH_NO.length == 0
			&& REF_NO.length == 0
			&& TX_AMOUNT.length == 0
			&& AUTH_CODE.length == 0 ){
			alert("卡號、POS代號、批次序號、調閱編號、金額、授權碼，請至少輸入其一");
			return;
		}
		
		var alertStr=''
		if(CARD_NO.length!=0 && CARD_NO.length!=15 && CARD_NO.length!=16 && CARD_NO.length!=19 ) alertStr+='卡號為15、16、19碼\n'
		if(TERMINAL_ID.length!=0 && TERMINAL_ID.length!=8) alertStr+='POS代號為8碼\n'
		if(BATCH_NO.length!=0 && BATCH_NO.length!=6) alertStr+='批次序號為6碼\n'
		if(REF_NO.length!=0 && REF_NO.length!=6) alertStr+='調閱編號為6碼\n'
		if(AUTH_CODE.length!=0 && AUTH_CODE.length!=6) alertStr+='授權碼為6碼\n'
		
		if(alertStr!=''){
			alert(alertStr)
			return;
		}
		return true;
	}
}


function checkdate(){
	var nowdate  = new Date();
	nowdate.setMonth(nowdate.getMonth()-'${queryMonth}');
	var limitDate =cal_gen_date2(nowdate);
	
	if($('#Action').val()=='QUERY'){
		if (document.all.form.Start_TransDate.value.length == 0
				&& document.all.form.End_TransDate.value.length == 0) {
			alert("請記得輸入交易查詢起始及結束日期");
			return;
		}
		if (document.all.form.Start_TransDate.value.length == 0
				&& document.all.form.End_TransDate.value.length > 0) {
			alert("請記得輸入交易起始日期");
			return;
		}
		if (document.all.form.End_TransDate.value.length == 0
				&& document.all.form.Start_TransDate.value.length > 0) {
			alert("請記得輸入交易結束日期");
			return;
		}
		if (document.all.form.End_TransDate.value.length > 0
				&& document.all.form.Start_TransDate.value.length > 0) {
			if (!check_date_format(document.all.form.Start_TransDate.value)
					|| !Check_Input_Date(document.all.form.Start_TransDate.value)) {
				alert("交易起始日期格式錯誤");
				return;
			}
			if (!check_date_format(document.all.form.End_TransDate.value)
					|| !Check_Input_Date(document.all.form.End_TransDate.value)) {
				alert("交易結束日期格式錯誤");
				return;
			}
		}
		if (!Check_Date(document.all.form.Start_TransDate.value,
				document.all.form.End_TransDate.value)) {
			alert("交易起啟日期不可大於結束日期");
			return;
		}
		if (Check_Date(document.all.form.Start_TransDate.value, limitDate)) {
			
			alert("查詢起日範圍不可超過當日起回算 '${queryMonth}'個月!! ");
	    	return;
		}
	return true;
	}else if($('#Action').val()=='QryByDate'){
		if (document.all.form.TransDate.value.length == 0
				&& document.all.form.TransDate.value.length == 0) {
			alert("請記得輸入交易查詢日期");
			return;
		}
		if (document.all.form.TransDate.value.length > 0) {
			if (!check_date_format(document.all.form.TransDate.value)){
				alert("交易起始日期格式錯誤");
				return;
			}
		}
	}
	return true;
}



function clearall(){
	document.all.form.Start_TransDate.value='${Start_TransDate}';
	document.all.form.End_TransDate.value='${End_TransDate}';
}

function txDateResultDetail(obj) {
	var merchantId = $('#merchantId').val();
	var merchantNameStr = $('#merchantId').find('option:selected').text();
	var merchantName = merchantNameStr.substring(merchantId.length + 1,
			merchantNameStr.length);
	var startD = $('#TransDate').val()

	$('#merchant_id').val(merchantId);
	$('#process_date').val(startD);
	$('#terminal_id').val(obj)
	$('#merchant_name').val(merchantName);

	if (startD == "") {
		$('#errorMessage').html('日期查詢不可有空值');
		if (errorMessage != "") {
			$('#errorMessage').css('display', 'block');
		}
	} else {
		$("#singForm").attr("action","./MerchantSignBillCtl")
				.submit();
	}
}

function btn_QryByDate() {
	$('#Action').val('QryByDate')
	$('#TransDate_td:eq(1)').text('')
	$('tr:not(#head,.perlog,#logListHead)').each(function(){
		
		if($(this).attr('class')!='dateQry' && $(this).attr('class')!='search'){
			$(this).hide()
		}
	})
	$('#PosIDlist').show()
	$('.calendar[class*="qryByDate"]').show()
	$('.calendar:not(.qryByDate)').hide()
	$('#process__date').val('')
	
}

function btn_DetailQry() {
	$('#Action').val('QUERY')
	$('tr').each(function(){
		$(this).show()
	})
	$('#PosIDlist').hide()
	$('.calendar[class*="qryByDate"]').hide()
	$('.calendar:not(.qryByDate)').show()
	$('#process__date').val('')
	
}
// 	<input type="button" value="交易日查詢" id="btn_QryByDate" class="btnQryCondition" style="width:100px;height:30px;"> | 
// 	<input type="button" value="進階查詢" id="btn_DetailQry" class="btnQryCondition" style="width:100px;height:30px;">
var clickTime=0
$('.btnQryCondition').click(function () {
	clickTime+=1
	
	if($(this).attr('id')=='btn_QryByDate'){
		btn_QryByDate()
	}else if($(this).attr('id')=='btn_DetailQry'){
		btn_DetailQry()
	}
	if(clickTime>0){
		$('.perlog').each(function(){
			$(this).html('')
		})
		$('#PosIDlist').hide()
	}
})


// function perPageNum() {
// 	if($('#PosID_btn').val()!=null){
// 		alert('asdasd')
// 	}else{
// 		alert('awwww')
// 	}
// }

$("#perPageNumSelecter").on('change', function () {
	var merchantId = $('#merchantId').val();
	var perPageNum=$(this).val()
	$('#perPage__num').val(perPageNum)
	$('#merchant__id').val(merchantId);
	
	//判斷是posid查詢的表or進階查詢的表
	if($('#PosID_btn').val()!=null){
		
		var merchantNameStr = $('#merchantId').find('option:selected').text();
		var startD = $('#TransDate').val()

		$('#process__date').val(startD);
		
	}else{
// 		<input type="text" style="display: none" id="card___no" name="card___no" value="" />
// 			<input type="text" style="display: none" id="terminal___id" name="terminal___id" value="" />
// 			<input type="text" style="display: none" id="batch___no" name="batch___no" value="" />
// 			<input type="text" style="display: none" id="ref___no" name="ref___no" value="" />
// 			<input type="text" style="display: none" id="tx___amount" name="tx___amount" value="" />
// 			<input type="text" style="display: none" id="auth___code" name="auth___code" value="" />
			
// 			<input type="text" style="display: none" id="choosenPage" name="choosenPage" value="" />
// 			<input type="text" style="display: none" id="perPage___num" name="perPage___num" value="" />
// 			<input type="text" style="display: none" id="totalPage___num" name="totalPageNum" value="${totalPageNum}" />
		var startD=$('#Start_TransDate').val()
		var endD=$('#End_TransDate').val()
		$('#start__date').val(startD)
		$('#end__date').val(endD)
		
		
		var CARD_NO=$('#CARD_NO').val()
		var TERMINAL_ID=$('#TERMINAL_ID').val()
		var BATCH_NO=$('#BATCH_NO').val()
		var REF_NO=$('#REF_NO').val()
		var TX_AMOUNT=$('#TX_AMOUNT').val()
		var CARD_NO=$('#CARD_NO').val()
		
		$('#card__no').val(CARD_NO)
		$('#terminal__id').val(TERMINAL_ID)
		$('#batch__no').val(BATCH_NO)
		$('#ref__no').val(REF_NO)
		$('#tx__amount').val(TX_AMOUNT)
		$('#auth__code').val(CARD_NO)
	}
	
	$("#perPageNumForm").attr("action","./MerchantSignBillCtl")
	.submit();
})

function showChoosenPage(perPageNum,choosenPage) {
// 	<input type="text" style="display: none" id="choosenPage" name="choosenPage" value="" />
// 	<input type="text" style="display: none" id="perPage___num" name="perPage___num" value="" />
	var merchantId = $('#merchantId').val();
	$('#choosenPage').val(choosenPage)
	$('#perPage___num').val(perPageNum)
	$('#merchant___id').val(merchantId);
	
	//判斷是posid查詢的表or進階查詢的表
	if($('#PosID_btn').val()!=null){
		
		var merchantNameStr = $('#merchantId').find('option:selected').text();
		var startD = $('#TransDate').val()

		$('#process___date').val(startD);
		
	}else{
		var startD=$('#Start_TransDate').val()
		var endD=$('#End_TransDate').val()
		$('#start___date').val(startD)
		$('#end___date').val(endD)
		
		
		var CARD_NO=$('#CARD_NO').val()
		var TERMINAL_ID=$('#TERMINAL_ID').val()
		var BATCH_NO=$('#BATCH_NO').val()
		var REF_NO=$('#REF_NO').val()
		var TX_AMOUNT=$('#TX_AMOUNT').val()
		var CARD_NO=$('#CARD_NO').val()
		
		$('#card___no').val(CARD_NO)
		$('#terminal___id').val(TERMINAL_ID)
		$('#batch___no').val(BATCH_NO)
		$('#ref___no').val(REF_NO)
		$('#tx___amount').val(TX_AMOUNT)
		$('#auth___code').val(CARD_NO)
	}
	$("#ChoosenPageForm").attr("action","./MerchantSignBillCtl")
	.submit();
} 

//初始化頁面
init()

function init() {
	//因原畫面之jsp為進階查詢畫面，初始載入時先自動呼叫btn_QryByDate()
	if('${Action}'==''){
		clickTime=0
		btn_QryByDate()
	}

	// 若PosID按鈕值存在表示為QryByDate表單，網頁載入時先切換成QryByDate表單畫面，再顯示查表
	if('${Action}'!='QUERY'){
		if($('#PosID_btn').val()!=null){
			btn_QryByDate()
		}else if($('#PosID_btn').val()==null && '${tErmlogGroup}'.length==0){//QryByDate表單查詢結果無PosID
			btn_QryByDate()
		}
	}
}

</script>
</head>
</html>