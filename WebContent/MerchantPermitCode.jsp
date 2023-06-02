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
// * 2020/06/17 �ۥ� 202006110363-00 �S���޲z�����s�WPermitCode�]�w
// ************************************************************/
	try{
		// ���~�T���O�_������
		String Message = (String)session.getAttribute("Message");
		
		Hashtable userinfo = (Hashtable)session.getAttribute("SYSCONFDATA");
		System.out.println("userinfo: " + userinfo);
		// �����n�J�S���ϥΪ� & ��T
		Hashtable merchantuserinfo = (Hashtable)userinfo.get("MERCHANT_USER");
		Hashtable merchantinfo = (Hashtable)userinfo.get("MERCHANT");
		String merchantid = String.valueOf(merchantinfo.get("MERCHANTID"));
		String userId = String.valueOf(merchantuserinfo.get("USER_NAME")); // FIXME: �S�����U�ϥΪ̬O���}����?
		System.out.println("merchantid: " + merchantid + "userid: " + userId);
		
		// �����ӯS��permit���
		List<MerchantPermitListEntity> merchantPermitCodeDataList = (List<MerchantPermitListEntity>)session.getAttribute("GETPERMITDATA");
		// �ä[���Ī�permitCode���
		String permitId = "";
		// �@���P�_�̾�
		int ListLength = merchantPermitCodeDataList.size();
				
		if(ListLength == 0){ // ��ܧ䤣��merchantid���
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
	// ��permit�Nshow�X�̷s��
	// �P�w�ӯS�����Lpermit
	var permitCodeContainer = "<%=permitId%>" ; 
	var userid = "<%=userId%>" ;
	var showInfoElement = $("#showInfoDiv");
	var hasOriginPermitCodeElement = $("#hasOriginPermitCodeDiv"); // ��permitcode��div
	var noOriginPermitCodeElement = $("#noOriginPermitCodeDiv"); // �Spermitcode��div
	var merchantPermitCodeField = $("#merchantPermitCode"); // �S��permitCode���
	function init(){
		if(msg == "null" || msg == ""){
			
		} else {
			alert(msg);
		}
		

		// init ���éҦ���div
		$("#showInfoDiv").hide();
		$("#hasOriginPermitCodeDiv").hide();
		$("#noOriginPermitCodeDiv").hide();
		
		// init �Ҧ�
		$("#merchantPermitCode").css("color", "#000000");
		
		
		// �ޤJ�ӯS����merchatid=> �@�w�|��
		$("#merchantId").val(originMerchantId);
		// �L�J�S����permitCode => ���@�w��
		// �P�_���LpermitCode => return true �N��permitCode
// 							  => return false �N��S��permitCode
		
		// user
		$("#userName").val(userid);
		
		if(permitCodeContainer != ""){
			// ���}
			$("#showInfoDiv").show();
			$("#hasOriginPermitCodeDiv").show();
		} else {
			$("#merchantPermitCode").val("�|���]�w");
			$("#merchantPermitCode").css('color', '#ff0000');
			
			// ���}
			$("#showInfoDiv").show();
			$("#noOriginPermitCodeDiv").show();
			
		}
	}
	
	// 
	function submitform(){
		if(checkdata()){
			var yesToDo = confirm("�O�_�T�w�n�i��]�w�@�~");
			if(yesToDo){
				document.form.btnSubmit.disabled = true;
			    document.form.btnClear.disabled = true;
			    document.form.submit();
			} else {
				// ����
			}
			
		}
	}
	
	function checkdata(){
		// �T�w��ܪ�div
		if($("#hasOriginPermitCodeDiv").is(":visible")) {
			
			if($("#UPD_OLDPERMITCODE").val() != permitCodeContainer ||
					!existUperLowerAndNumber($("#UPD_OLDPERMITCODE").val())) {
				alert('��PermitCode��J���~');
				$("#UPD_OLDPERMITCODE").val("");
				$("#UPD_OLDPERMITCODE").focus();
				return false;
			}
			
			if(!existUperLowerAndNumber($("#UPD_PERMITCODE").val())) {
				alert('���ܤ֥]�t�@�ӭ^��j�p�g�P�Ʀr�A�B���פ��b8~40�춡');
				$("#UPD_PERMITCODE").val("");
				$("#UPD_PERMITCODE").focus();
				return false;
			} else if($("#UPD_PERMITCODE").val() == $("#UPD_OLDPERMITCODE").val()){
				alert('�±K�X�P�s�K�X���i�ۦP');
				$("#UPD_PERMITCODE").val("");
				$("#UPD_PERMITCODETWO").val("");
				$("#UPD_PERMITCODE").focus();
				return false;
			}
			
			if($("#UPD_PERMITCODETWO").val() != $("#UPD_PERMITCODE").val()){
				alert('�ݻP�ҳ]�w��PermitCode�@�P');
				$("#UPD_PERMITCODE").val("");
				$("#UPD_PERMITCODETWO").val("");
				$("#UPD_PERMITCODE").focus();
				return false;
			}
		}
		else if($("#noOriginPermitCodeDiv").is(":visible")){
			
			if(!existUperLowerAndNumber($("#INS_PERMITCODE").val())) {
				alert('���ܤ֥]�t�@�ӭ^��j�p�g�P�Ʀr�A�B���פ��b8~40�춡');
				$("#INS_PERMITCODE").val("");
				$("#INS_PERMITCODE").focus();
				return false;
			}
			if($("#INS_PERMITCODETWO").val() != $("#INS_PERMITCODE").val()){
				alert('�ݻP�ҳ]�w��PermitCode�@�P');
				$("#INS_PERMITCODE").val("");
				$("#INS_PERMITCODETWO").val("");
				$("#INS_PERMITCODE").focus();
				return false;
			}
		}
		return true;
	}
	
	// �^��true: �T�̳����]�t=> �j�p�g�^��A�Ʀr
	//     false: ���Y�өάY�ǨS���]�t
	function existUperLowerAndNumber(param){
		var reg = /^[A-Za-z0-9]{8,40}$/;
		var regNumber = /[0-9]/;
		var regUper = /[A-Z]/;
		var regLow = /[a-z]/;
		var regOther = /[^A-Za-z0-9]/;
		
		if (regOther.test(param)) {
			return false; // �ˬd��A���Ÿ�
		}
		
		if(!regNumber.test(param)){
			return false; // �ˬd��A�S���Ʀr
		}
		
		if(!regUper.test(param)){
			return false; // �ˬd��A�S���j�g
		}
		
		if(!regLow.test(param)){
			return false; // �ˬd��A�S���p�g
		}
		
		if (!reg.test(param)) {
			return false; // �ˬd��A���פ��ŦX
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
			// �ˬd��A���Ÿ�
			obj.value = input.replace(/[^a-zA-Z0-9]/g,'');
		}
		
		if(!regNumber.test(input)){
			number = true; // �ˬd��A�S���Ʀr
		}
		
		if(!regUper.test(input)){
			upper = true; // �ˬd��A�S���j�g
		}
		
		if(!regLow.test(input)){
			lower = true; // �ˬd��A�S���p�g
		}
		
		if (!reg.test(input)) {
			length = true; // �ˬd��A���פ��ŦX
		}

		if (length)
			msgHint = "(���ץ�������8~40��)";
		else if (number && upper && lower)
			msgHint = "(�ܤ֥]�t�@�Ӥj�g�^��B�p�g�^��P�Ʀr)";
		else if (number && upper)
			msgHint = "(�ܤ֥]�t�@�Ӥj�g�^��P�Ʀr)";
		else if (number && lower)
			msgHint = "(�ܤ֥]�t�@�Ӥp�g�^��P�Ʀr)";
		else if (upper && lower)
			msgHint = "(�ܤ֥]�t�@�Ӥj�g�^��B�p�g�^��)";
		else if (number)
			msgHint = "(�ܤ֥]�t�@�ӼƦr)";
		else if (upper)
			msgHint = "(�ܤ֥]�t�@�Ӥj�g�^��)";
		else if (lower)
			msgHint = "(�ܤ֥]�t�@�Ӥp�g�^��)";
		
		document.getElementById(hintid).innerHTML = msgHint;
	}
	
	function check2ndPermitCode(obj, firstPermitCode, hintid) {
		var msgHint = "";
		if (obj.value != document.getElementById(firstPermitCode).value)
			msgHint = "���۲šI";
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
				<td align="left" valign="middle"><b><font color="#004E87" size="3">�]�w</font></b><font size="3" color="#004E87"><b>PermitCode</b></font>
			</tr>
		</table>

		<hr style="height: 1px">
		
		<div id="showInfoDiv">
			<table width="100%" border="0" bgcolor='#D8D8D8' cellspacing="1" cellpadding="1">
				<tr align='center' bgcolor="#F4F4FF"  height="30">
					<th>�S���ө��N��</th>
					<th>PermitCode</th>
					<th>�����</th>
					<th>�]�w�H��</th>
				</tr>
				
	<%
		if(merchantPermitCodeDataList.size() == 0){
	%>
				<tr align="center" bgcolor='#ffffff' height="25">
					<td width='25%'><%=merchantid%></td> 
					<td width='25%'><font color='#ff0000'>�|���]�w</font></td>
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
				
				// �YEXPIRE=20991231�A�hEXPIRE=�ä[����
				if(expireDate.equals("20991231")){
					expireDate = "�ä[����";
				}
				
				String permitPre = permitCode.substring(0, 2);
				String permitBack = permitCode.substring(permitCode.length()-2);
				permitCode = permitPre + "*******" + permitBack;
	%>
				<tr align="center" bgcolor='#ffffff' height="25">
					<td width='25%'><%=merchantId%></td> 
					<td width='25%'><%=permitCode%></td>
					<td width='25%'>
						<%  if (expireDate.equals("�ä[����")) {
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
		

<!-- 		�w��permitCode -->
		<div id="hasOriginPermitCodeDiv">
		<div><b>�`�N�ƶ��G</b></div>
		<div>1. ��J��PermitCode�ɡA�п�J������<font color='#ff0000'>�ä[����</font>�������A�B����P�������ơC</div>
		<div>2. PermitCode���ܤ֥]�t�@�Ӥj�g�^��B�p�g�^��P�Ʀr�A���ץ�������8��40�줧���C</div>
		<div>3. PermitCode�ק粒����<font color='#ff0000'>�кɳt</font>�^�ۨ��t�ΤW���PermitCode�C</div>
			<table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
				<tr align='center'  height="35">
					<td width='25%' colspan='2' bgcolor='#F4F4FF'><font size='3' color='#ff0000'><b>�w�s�bPermitCode�A�i�b�����s���]�w</b></font></td>
				</tr>
				<tr align='center' height="25">
					<td width='50%' bgcolor='#F4F4FF' align='right'><font size='1'>��J�ª�PermitCode</font></td>
					<td width='50%' bgcolor='#ffffff' align='left'>
						<input type='text' id='UPD_OLDPERMITCODE' name='UPD_OLDPERMITCODE' size='40' maxlength='40' 
							onkeyup="inputHint(this, 'UPD_OLDPERMITCODE_HINT')">
						<font color='#ff0000' size='1'><span id="UPD_OLDPERMITCODE_HINT"></span></font>
					</td>
				</tr>
				<tr align='center' height="25">
					<td width='50%' bgcolor='#F4F4FF' align='right'><font size='1'>�]�w�s��PermitCode</font></td>
					<td width='50%' bgcolor='#ffffff' align='left'>
						<input type='text' id='UPD_PERMITCODE' name='UPD_PERMITCODE' size='40' maxlength='40'
							onkeyup="inputHint(this, 'UPD_PERMITCODE_HINT')">
						<font color='#ff0000' size='1'><span id="UPD_PERMITCODE_HINT"></span></font>
					</td>
				</tr>
				<tr align='center' height="25">
					<td width='50%' bgcolor='#F4F4FF' align='right'><font size='1'>�A����J�s��PermitCode</font></td>
					<td width='50%' bgcolor='#ffffff' align='left'>
						<input type='text' id='UPD_PERMITCODETWO' name='UPD_PERMITCODETWO' size='40' maxlength='40'
							onkeyup="check2ndPermitCode(this, 'UPD_PERMITCODE', 'UPD_PERMITCODETWO_HINT')">
						<font color='#ff0000' size='1'><span id="UPD_PERMITCODETWO_HINT"></span></font>
					</td>
				</tr>
				  <tr align='center' height="30">
				   <td align='center' bgcolor='#F4F4FF' colspan="2">
				    <input type='button' value='�T�{�]�w' name='btnSubmit' id='btnSubmit' onclick='submitform()' >
				    <input type='reset' value='�M���e��' name='btnClear' id='btnClear'  >
				   </td>
				  </tr>
			</table>
		</div>
		
		
<!-- 		�|����PermitCode -->
		<div id="noOriginPermitCodeDiv">
		<div><b>�`�N�ƶ��G</b></div>
		<div>�]�wPermitCode���ܤ֥]�t�@�Ӥj�g�^��B�p�g�^��P�Ʀr�A���ץ�������8��40�줧���C</div>
			<table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
				<tr align='center'  height="35">
					<td width='25%' colspan='2' bgcolor='#F4F4FF'><font size='3' color='#ff0000'><b>�S�����]�wPermitCode</b></font></td>
				</tr>
				<tr align='center' height="25">
					<td width='50%' bgcolor='#F4F4FF' align='right'><font size='1'>�]�w�s��PermitCode</font></td>
					<td width='50%' bgcolor='#ffffff' align='left'>
						<input type='text' id='INS_PERMITCODE' name='INS_PERMITCODE' size='40' maxlength='40'
							onkeyup="inputHint(this, 'INS_PERMITCODE_HINT')">
						<font color='#ff0000' size='1'><span id="INS_PERMITCODE_HINT"></span></font>
					</td>
				</tr>
				<tr align='center' height="25">
					<td width='50%' bgcolor='#F4F4FF' align='right'><font size='1'>�A����JPermitCode</font></td>
					<td width='50%' bgcolor='#ffffff' align='left'>
						<input type='text' id='INS_PERMITCODETWO' name='INS_PERMITCODETWO' size='40' maxlength='40'
							onkeyup="check2ndPermitCode(this, 'INS_PERMITCODE', 'INS_PERMITCODETWO_HINT')">
						<font color='#ff0000' size='1'><span id="INS_PERMITCODETWO_HINT"></span></font>
					</td>
				</tr>
				  <tr align='center' height="30">
				   <td align='center' bgcolor='#F4F4FF' colspan="2">
				    <input type='button' value='�T�{�]�w' name='btnSubmit' id='btnSubmit' onclick='submitform()' >
				    <input type='reset' value='�M���e��' name='btnClear' id='btnClear'  >
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
