<%@ page language="java" contentType="text/html; charset=BIG5" pageEncoding="BIG5"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>
<%@ page import="com.cybersoft.entity.MerchantPermitListEntity" %>
<%
//************************************************************
// * <p>#File Name:   MerchantPermitCode.jsp        </p>
// * <p>#Description:                  </p>
// * <p>#Create Date: 2020/4/6              </p>
// * <p>#Company:     cybersoft               </p>
// * <p>#Notice:                      </p>
// * @author      dan
// * @since       SPEC version
// * @version 0.1 2020/4/6  dan
// * 2020/06/17 彥仲 202006110363-00 特店管理介面新增PermitCode設定
// ************************************************************/
	try{
		// 錯誤訊息是否有接到
		String Message = (String)session.getAttribute("Message");
		
		Hashtable userinfo = (Hashtable)session.getAttribute("SYSCONFDATA");
		System.out.println("userinfo: " + userinfo);
		// 拿取登入特店使用者 & 資訊
		Hashtable merchantuserinfo = (Hashtable)userinfo.get("MERCHANT_USER");
		Hashtable merchantinfo = (Hashtable)userinfo.get("MERCHANT");
		String merchantid = String.valueOf(merchantinfo.get("MERCHANTID"));
		String userId = String.valueOf(merchantuserinfo.get("USER_NAME")); // FIXME: 特店底下使用者是分開的嗎?
		System.out.println("merchantid: " + merchantid + "userid: " + userId);
		
		// 拿取該特店permit資料
		List<MerchantPermitListEntity> merchantPermitCodeDataList = (List<MerchantPermitListEntity>)session.getAttribute("GETPERMITDATA");
		// 永久有效的permitCode資料
		String permitId = "";
		// 作為判斷依據
		int ListLength = merchantPermitCodeDataList.size();
				
		if(ListLength == 0){ // 表示找不到merchantid資料
			permitId = "";
		} else {
			permitId = merchantPermitCodeDataList.get(ListLength-1).getPERMIT_CODE();
		}
		 
		
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=BIG5">
<link type="text/css" rel="stylesheet" href="css/style.css">
<!-- <link type="text/css" rel="stylesheet" href="./css/bootstrap.min.css" > -->
<title>MerSetPermitCode.jsp</title>
</head>
<script type="text/javascript" src="./js/jquery-3.3.1.min.js"></script>
<!-- <script type="text/javascript" src="./js/popper.min.js"></script> -->
<script language="JavaScript" src="js/Vatrix.js"></script>
<script language="JavaScript" src="js/calendar.js"></script>
<script language="JavaScript" type="text/JavaScript">
	
	var msg = "<%=Message%>";
	var originMerchantId = "<%=merchantid%>";
	// 有permit就show出最新的
	// 判定該特店有無permit
	var permitCodeContainer = "<%=permitId%>" ; 
	var userid = "<%=userId%>" ;
	var showInfoElement = $("#showInfoDiv");
	var hasOriginPermitCodeElement = $("#hasOriginPermitCodeDiv"); // 有permitcode的div
	var noOriginPermitCodeElement = $("#noOriginPermitCodeDiv"); // 沒permitcode的div
	var merchantPermitCodeField = $("#merchantPermitCode"); // 特店permitCode欄位
	function init(){
		if(msg == "null" || msg == ""){
			
		} else {
			alert(msg);
		}
		

		// init 隱藏所有的div
		$("#showInfoDiv").hide();
		$("#hasOriginPermitCodeDiv").hide();
		$("#noOriginPermitCodeDiv").hide();
		
		// init 所有
		$("#merchantPermitCode").css("color", "#000000");
		
		
		// 引入該特店之merchatid=> 一定會有
		$("#merchantId").val(originMerchantId);
		// 印入特店之permitCode => 不一定有
		// 判斷有無permitCode => return true 代表有permitCode
// 							  => return false 代表沒有permitCode
		
		// user
		$("#userName").val(userid);
		
		if(permitCodeContainer != ""){
			// 打開
			$("#showInfoDiv").show();
			$("#hasOriginPermitCodeDiv").show();
		} else {
			$("#merchantPermitCode").val("尚未設定");
			$("#merchantPermitCode").css('color', '#ff0000');
			
			// 打開
			$("#showInfoDiv").show();
			$("#noOriginPermitCodeDiv").show();
			
		}
	}
	
	// 
	function submitform(){
		if(checkdata()){
			var yesToDo = confirm("是否確定要進行設定作業");
			if(yesToDo){
				document.form.btnSubmit.disabled = true;
			    document.form.btnClear.disabled = true;
			    document.form.submit();
			} else {
				// 不做
			}
			
		}
	}
	
	function checkdata(){
		// 確定顯示的div
		if($("#hasOriginPermitCodeDiv").is(":visible")) {
			
			if($("#UPD_OLDPERMITCODE").val() != permitCodeContainer ||
					!existUperLowerAndNumber($("#UPD_OLDPERMITCODE").val())) {
				alert('舊PermitCode輸入錯誤');
				$("#UPD_OLDPERMITCODE").val("");
				$("#UPD_OLDPERMITCODE").focus();
				return false;
			}
			
			if(!existUperLowerAndNumber($("#UPD_PERMITCODE").val())) {
				alert('須至少包含一個英文大小寫與數字，且長度介在8~40位間');
				$("#UPD_PERMITCODE").val("");
				$("#UPD_PERMITCODE").focus();
				return false;
			} else if($("#UPD_PERMITCODE").val() == $("#UPD_OLDPERMITCODE").val()){
				alert('舊密碼與新密碼不可相同');
				$("#UPD_PERMITCODE").val("");
				$("#UPD_PERMITCODETWO").val("");
				$("#UPD_PERMITCODE").focus();
				return false;
			}
			
			if($("#UPD_PERMITCODETWO").val() != $("#UPD_PERMITCODE").val()){
				alert('需與所設定的PermitCode一致');
				$("#UPD_PERMITCODE").val("");
				$("#UPD_PERMITCODETWO").val("");
				$("#UPD_PERMITCODE").focus();
				return false;
			}
		}
		else if($("#noOriginPermitCodeDiv").is(":visible")){
			
			if(!existUperLowerAndNumber($("#INS_PERMITCODE").val())) {
				alert('須至少包含一個英文大小寫與數字，且長度介在8~40位間');
				$("#INS_PERMITCODE").val("");
				$("#INS_PERMITCODE").focus();
				return false;
			}
			if($("#INS_PERMITCODETWO").val() != $("#INS_PERMITCODE").val()){
				alert('需與所設定的PermitCode一致');
				$("#INS_PERMITCODE").val("");
				$("#INS_PERMITCODETWO").val("");
				$("#INS_PERMITCODE").focus();
				return false;
			}
		}
		return true;
	}
	
	// 回傳true: 三者都有包含=> 大小寫英文，數字
	//     false: 有某個或某些沒有包含
	function existUperLowerAndNumber(param){
		var reg = /^[A-Za-z0-9]{8,40}$/;
		var regNumber = /[0-9]/;
		var regUper = /[A-Z]/;
		var regLow = /[a-z]/;
		var regOther = /[^A-Za-z0-9]/;
		
		if (regOther.test(param)) {
			return false; // 檢查後，有符號
		}
		
		if(!regNumber.test(param)){
			return false; // 檢查後，沒有數字
		}
		
		if(!regUper.test(param)){
			return false; // 檢查後，沒有大寫
		}
		
		if(!regLow.test(param)){
			return false; // 檢查後，沒有小寫
		}
		
		if (!reg.test(param)) {
			return false; // 檢查後，長度不符合
		}
		
		return true;
	}
</script>
<script>
	function inputHint(obj, hintid) {
		
	    var input = obj.value;
	    var msgHint = "";
	    var number = false, upper = false, lower = false, length = false;
	    
		var reg = /^[A-Za-z0-9]{8,40}$/;
		var regNumber = /[0-9]/;
		var regUper = /[A-Z]/;
		var regLow = /[a-z]/;
		var regOther = /[^A-Za-z0-9]/;
		
		if (regOther.test(input)) {
			// 檢查後，有符號
			obj.value = input.replace(/[^a-zA-Z0-9]/g,'');
		}
		
		if(!regNumber.test(input)){
			number = true; // 檢查後，沒有數字
		}
		
		if(!regUper.test(input)){
			upper = true; // 檢查後，沒有大寫
		}
		
		if(!regLow.test(input)){
			lower = true; // 檢查後，沒有小寫
		}
		
		if (!reg.test(input)) {
			length = true; // 檢查後，長度不符合
		}

		if (length)
			msgHint = "(長度必須介於8~40位)";
		else if (number && upper && lower)
			msgHint = "(至少包含一個大寫英文、小寫英文與數字)";
		else if (number && upper)
			msgHint = "(至少包含一個大寫英文與數字)";
		else if (number && lower)
			msgHint = "(至少包含一個小寫英文與數字)";
		else if (upper && lower)
			msgHint = "(至少包含一個大寫英文、小寫英文)";
		else if (number)
			msgHint = "(至少包含一個數字)";
		else if (upper)
			msgHint = "(至少包含一個大寫英文)";
		else if (lower)
			msgHint = "(至少包含一個小寫英文)";
		
		document.getElementById(hintid).innerHTML = msgHint;
	}
	
	function check2ndPermitCode(obj, firstPermitCode, hintid) {
		var msgHint = "";
		if (obj.value != document.getElementById(firstPermitCode).value)
			msgHint = "不相符！";
		document.getElementById(hintid).innerHTML = msgHint;
	}
</script>
<body bgcolor="#ffffff" onload='init()'>
	<form name="form" id="form" method="post" action="MerchantPermitCodeCtl">
	<input type='hidden' id='userName' name='userName'>
	<input type='hidden' id='merchantId' name='merchantId'>
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
				<td align="left" valign="middle"><b><font color="#004E87" size="3">設定</font></b><font size="3" color="#004E87"><b>PermitCode</b></font>
			</tr>
		</table>

		<hr style="height: 1px">
		
		<div id="showInfoDiv">
			<table width="100%" border="0" bgcolor='#D8D8D8' cellspacing="1" cellpadding="1">
				<tr align='center' bgcolor="#F4F4FF"  height="30">
					<th>特約商店代號</th>
					<th>PermitCode</th>
					<th>到期日</th>
					<th>設定人員</th>
				</tr>
				
	<%
		if(merchantPermitCodeDataList.size() == 0){
	%>
				<tr align="center" bgcolor='#ffffff' height="25">
					<td width='25%'><%=merchantid%></td> 
					<td width='25%'><font color='#ff0000'>尚未設定</font></td>
					<td width='25%'></td>
					<td width='25%'></td>
				</tr>
	<%
		} else {
			for(int index=0 ; index<merchantPermitCodeDataList.size() ; index++){
				
				MerchantPermitListEntity value = merchantPermitCodeDataList.get(index);
				String merchantId = value.getMERCHANTID();
				String permitCode = value.getPERMIT_CODE();
				String expireDate = value.getEXPIRE_DATE();
				String setUser = value.getUPDATE_USER();
				
				// 若EXPIRE=20991231，則EXPIRE=永久有效
				if(expireDate.equals("20991231")){
					expireDate = "永久有效";
				}
				
				String permitPre = permitCode.substring(0, 2);
				String permitBack = permitCode.substring(permitCode.length()-2);
				permitCode = permitPre + "*******" + permitBack;
	%>
				<tr align="center" bgcolor='#ffffff' height="25">
					<td width='25%'><%=merchantId%></td> 
					<td width='25%'><%=permitCode%></td>
					<td width='25%'>
						<%  if (expireDate.equals("永久有效")) {
						%>
							<font color='#ff0000'size='3'><%=expireDate%></font>
						<%  } else {
						%>
							<%=expireDate%> 
						<%  }
						%>
					</td> 
					<td width='25%'><%=setUser%></td>
				</tr>
	<%		
		
			}
		}
				
	%>
			</table>
		</div>
		

<!-- 		已有permitCode -->
		<div id="hasOriginPermitCodeDiv">
		<div><b>注意事項：</b></div>
		<div>1. 輸入舊PermitCode時，請輸入期限為<font color='#ff0000'>永久有效</font>的那筆，且不能與此筆重複。</div>
		<div>2. PermitCode須至少包含一個大寫英文、小寫英文與數字，長度必須介於8到40位之間。</div>
		<div>3. PermitCode修改完畢後<font color='#ff0000'>請盡速</font>回自身系統上更改PermitCode。</div>
			<table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
				<tr align='center'  height="35">
					<td width='25%' colspan='2' bgcolor='#F4F4FF'><font size='3' color='#ff0000'><b>已存在PermitCode，可在此重新做設定</b></font></td>
				</tr>
				<tr align='center' height="25">
					<td width='50%' bgcolor='#F4F4FF' align='right'><font size='1'>輸入舊的PermitCode</font></td>
					<td width='50%' bgcolor='#ffffff' align='left'>
						<input type='text' id='UPD_OLDPERMITCODE' name='UPD_OLDPERMITCODE' size='40' maxlength='40' 
							onkeyup="inputHint(this, 'UPD_OLDPERMITCODE_HINT')">
						<font color='#ff0000' size='1'><span id="UPD_OLDPERMITCODE_HINT"></span></font>
					</td>
				</tr>
				<tr align='center' height="25">
					<td width='50%' bgcolor='#F4F4FF' align='right'><font size='1'>設定新的PermitCode</font></td>
					<td width='50%' bgcolor='#ffffff' align='left'>
						<input type='text' id='UPD_PERMITCODE' name='UPD_PERMITCODE' size='40' maxlength='40'
							onkeyup="inputHint(this, 'UPD_PERMITCODE_HINT')">
						<font color='#ff0000' size='1'><span id="UPD_PERMITCODE_HINT"></span></font>
					</td>
				</tr>
				<tr align='center' height="25">
					<td width='50%' bgcolor='#F4F4FF' align='right'><font size='1'>再次輸入新的PermitCode</font></td>
					<td width='50%' bgcolor='#ffffff' align='left'>
						<input type='text' id='UPD_PERMITCODETWO' name='UPD_PERMITCODETWO' size='40' maxlength='40'
							onkeyup="check2ndPermitCode(this, 'UPD_PERMITCODE', 'UPD_PERMITCODETWO_HINT')">
						<font color='#ff0000' size='1'><span id="UPD_PERMITCODETWO_HINT"></span></font>
					</td>
				</tr>
				  <tr align='center' height="30">
				   <td align='center' bgcolor='#F4F4FF' colspan="2">
				    <input type='button' value='確認設定' name='btnSubmit' id='btnSubmit' onclick='submitform()' >
				    <input type='reset' value='清除畫面' name='btnClear' id='btnClear'  >
				   </td>
				  </tr>
			</table>
		</div>
		
		
<!-- 		尚未有PermitCode -->
		<div id="noOriginPermitCodeDiv">
		<div><b>注意事項：</b></div>
		<div>設定PermitCode須至少包含一個大寫英文、小寫英文與數字，長度必須介於8到40位之間。</div>
			<table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
				<tr align='center'  height="35">
					<td width='25%' colspan='2' bgcolor='#F4F4FF'><font size='3' color='#ff0000'><b>特店未設定PermitCode</b></font></td>
				</tr>
				<tr align='center' height="25">
					<td width='50%' bgcolor='#F4F4FF' align='right'><font size='1'>設定新的PermitCode</font></td>
					<td width='50%' bgcolor='#ffffff' align='left'>
						<input type='text' id='INS_PERMITCODE' name='INS_PERMITCODE' size='40' maxlength='40'
							onkeyup="inputHint(this, 'INS_PERMITCODE_HINT')">
						<font color='#ff0000' size='1'><span id="INS_PERMITCODE_HINT"></span></font>
					</td>
				</tr>
				<tr align='center' height="25">
					<td width='50%' bgcolor='#F4F4FF' align='right'><font size='1'>再次輸入PermitCode</font></td>
					<td width='50%' bgcolor='#ffffff' align='left'>
						<input type='text' id='INS_PERMITCODETWO' name='INS_PERMITCODETWO' size='40' maxlength='40'
							onkeyup="check2ndPermitCode(this, 'INS_PERMITCODE', 'INS_PERMITCODETWO_HINT')">
						<font color='#ff0000' size='1'><span id="INS_PERMITCODETWO_HINT"></span></font>
					</td>
				</tr>
				  <tr align='center' height="30">
				   <td align='center' bgcolor='#F4F4FF' colspan="2">
				    <input type='button' value='確認設定' name='btnSubmit' id='btnSubmit' onclick='submitform()' >
				    <input type='reset' value='清除畫面' name='btnClear' id='btnClear'  >
				   </td>
				  </tr>
			</table>
		</div>		
	</form>
<style>
	.inputClass{
		border-top:0px;
		border-left:0px;
		border-right:0px;
		border-bottom:0px;
		background-color: #F4F4FF;
		text-align: center;
		font-size: 15px;
	}
</style>
</body>
</html>
<%
	session.removeAttribute("Message");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errMsg",e.toString());
            request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
        }
%>
