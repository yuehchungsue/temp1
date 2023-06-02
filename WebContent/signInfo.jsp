<%@ page language="java" contentType="text/html; charset=BIG5" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script src='<c:url value="js/bluebird.js"/>'></script>
<script src='<c:url value="js/html2canvas.js" />'></script>
<script src='<c:url value="js/jsPdf.debug.js" />'></script>

<%@ page isELIgnored="false" %>

<style>
.seco-logo {
    width: 70%;
    margin: 0px auto 0px;
}
</style>		

<script type="text/javascript">

	function signInfo(transId){
		var sampleHtml ="";
		var printHtml ="";
		var downloadHtml ="";
		
		var sampleHtmlF="";
		var downloadHtmlF ="";
		var printHtmlF ="";
		
		var browser = $('#browser').val();
		$('.posbox').empty();
		
		var ajax="ajaxReq_pdf";
		
		$.ajax({
			url : "<c:url value='${forwardStr}'/>",
			type : 'POST',
			async : false,
// 			async : true,
			data : {
				trans_id : transId,
				ajax:ajax,
			},
			error : function(jqXHR, thrownError){
			      alert(jqXHR.status);
			      alert(thrownError);
			},
			success : function(result){
				var data = result.data;
				var imageFile = result.imageFile;
				var signInfoType = data.signInfoType;
				var signImageFile = result.signImageFile;
				var maskNo = result.maskNo;
				var cardNo = result.cardNo;
				var callName = result.merchantName; 
						
				//console.log('signImageFile  : '+ signImageFile );
				//console.log('imageFile : '+ imageFile );
				//console.log('callName: '+ result.merchantName);
				//console.log('tip_amount_show:'+data.tip_amount_show);
				//console.log('tip_amount:'+data.tip_amount);
				//console.log('tx_amount_show:'+data.tx_amount_show);
				//console.log('tx_amount:'+data.tx_amount);
				if(data.data_source=='S'){
				//廣三SOGO、台茂 簽單版型
							
				sampleHtml +=	'<div class="resultout qa-out">';
				
				sampleHtml +=	'<div class="sign-logo">'+
								'	<img src="images/logo-sign-sign.png" clas="img-responsive" border="0" alt="">'+
								'</div>';
				
				if(imageFile != ''){
					sampleHtml +=	'<div class="seco-logo">'+
								//20220820 mask '	<img src="data:image/jpg;base64,'+imageFile+'" clas="img-responsive" border="0" alt="">'+
								'	<img src="images/'+imageFile+'" clas="img-responsive" border="0" alt="">'+
								'</div>';
				}	
				
									
				sampleHtml +=	'<div class="css_table">'+
								'	<div class="css_tr">'+
								'		<div class="css_td">商店代號</div>'+
								'		<div class="css_td">'+data.merchant_id+'</div>'+
								'	</div>'+
								'	<div class="css_tr">'+
								'		<div class="css_td">端末機代號</div>'+
								'		<div class="css_td">'+data.terminal_id+'</div>'+
								'	</div>'+
								'</div>'+
								'<hr>';
							
				sampleHtml +=	'<div class="pos-all">'+
								'	<div class="css_table pos-card">'+
								'		<div class="css_tr pos-content">'+
								'<div class="css_td">卡別(Card Type)</div>';
								
				
				//檢查碼 
				if(signInfoType != '21' && signInfoType != '22' && signInfoType != '23' &&
				   signInfoType != '24' && signInfoType != '25' && signInfoType != '26' &&
				   signInfoType != '27' ){
				   
					sampleHtml +=	'<div class="css_td">檢查碼(Check No.)</div>';
				}
				
				sampleHtml +=	'</div>'+
								'<div class="css_tr">'+
								'	<div class="css_td">'+data.card_type_show+'</div>';
				
				if(signInfoType != '21' && signInfoType != '22' && signInfoType != '23' &&
				   signInfoType != '24' && signInfoType != '25' && signInfoType != '26' &&
				   signInfoType != '27'){
				   
					sampleHtml +=	'<div class="css_td">'+data.check_no+'</div>';
				}
				sampleHtml +=	'</div></div>';
				
				sampleHtml +=	'<div class="css_table pos-card">'+
								'	<div class="css_tr pos-content">'+
								'		<div class="css_td">卡號(Card No.)</div>'+ 
								'	</div>'+
								'	<div class="css_tr">'+
// 								'		<div class="css_td" style="font-size: 16px;">'+data.card_no.replace("******", maskNo)+'('+data.trans_mode+')</div>'+
								'		<div class="css_td" style="font-size: 16px;">'+cardNo+'('+data.trans_mode+')</div>'+

								'	</div>'+
								'</div>'+
								'<div class="css_table pos-card">'+
								'	<div class="css_tr pos-content">'+
								'		<div class="css_td">交易類別(Trans. Type)</div>'+
								'	</div>';
								
				if(signInfoType == '8' || signInfoType == '12'|| signInfoType == '27'){
					sampleHtml += '	<div class="css_tr">'+
								'		<div class="css_td">'+data.trans_type_show_cht+'</div>'+
								'	</div>'+
								' <div class="css_tr">'+
								'		<div class="css_td">'+data.trans_type_show_eng+'</div>'+
								'	</div>';
				}else{
					sampleHtml += '	<div class="css_tr">'+
								'		<div class="css_td">'+data.trans_type_show_cht+' '+ data.trans_type_show_eng+'</div>'+
								'	</div>';
				}			
								
				sampleHtml +=	'</div>'+
								'<div class="css_table pos-card">'+
								'	<div class="css_tr pos-content">'+
								'		<div class="css_td">批次號碼(Batch No.)</div>'+
								'		<div class="css_td">授權碼(Auth Code)</div>'+
								'	</div>'+
								'	<div class="css_tr">'+
								'		<div class="css_td">'+data.batch_no+'</div>'+
								'		<div class="css_td">'+data.auth_code+'</div>'+
								'	</div>'+
								'</div>'+
								'<div class="css_table pos-card">'+
								'	<div class="css_tr pos-content">'+
								'		<div class="css_td">日期/時間(Date/Time)</div>'+
								'	</div>'+
								'	<div class="css_tr">'+
								'		<div class="css_td">'+data.tx_date_time_show+'</div>'+
								'	</div>'+
								'</div>'+
								'<div class="css_table pos-card">'+
								'	<div class="css_tr pos-content">'+
								'		<div class="css_td">調閱編號(Inv. No.)</div>'+
								'		<div class="css_td">序號(Ref. No.)</div>'+
								'	</div>'+
								'	<div class="css_tr">'+
								'		<div class="css_td">'+data.invoice_no+'</div>'+
								'		<div class="css_td">'+data.ref_no+'</div>'+
								'	</div>'+
								'</div>';
				//CUP交易序號	
				if(signInfoType == '21' || signInfoType == '22' || signInfoType == '23' ||
				   signInfoType == '24' || signInfoType == '25' || signInfoType == '26' ||
				   signInfoType == '27'){
				   
				   sampleHtml +=	'<div class="css_table pos-card">'+
									'	<div class="css_tr pos-content">'+
									'		<div class="css_td">CUP交易序號(CUP STAN)</div>'+
									'	</div>'+
									'	<div class="css_tr">'+
									'		<div class="css_td">'+data.cup_stan+'</div>'+
									'	</div>'+
									'</div>';
				}
				
				sampleHtml +=	'<div class="css_table pos-card">'+
								'	<div class="css_tr pos-content">'+
								'		<div class="css_td">櫃號(Store ID)</div>'+
								'	</div>'+
								'	<div class="css_tr">'+
								'		<div class="css_td">'+data.store_id+'</div>'+
								'	</div>'+
								'</div>';
								
				//晶片碼
				if(data.emv_tc != ''){
					if(signInfoType == '1' || signInfoType == '2' || signInfoType == '3' ||
					   signInfoType == '4' || signInfoType == '5' || signInfoType == '6' ||
					   signInfoType == '9' || signInfoType == '10' || signInfoType == '13' ||
					   signInfoType == '14' || signInfoType == '21' || signInfoType == '22' ||
					   signInfoType == '24' || signInfoType == '25'){
					   
					   sampleHtml +=	'<div class="css_table pos-card">'+
										'	<div class="css_tr pos-content">'+
										'		<div class="css_td">晶片碼(TC)</div>'+
										'	</div>'+
										'	<div class="css_tr">'+
										'		<div class="css_td">'+data.emv_tc+'</div>'+
										'	</div>'+
										'</div>';
					}
				}
				
				 sampleHtml +=	'<div class="total_box">';
		
				
				//金額
				if(signInfoType == '1' || signInfoType == '5' || signInfoType == '6' ||
				   signInfoType == '7' || signInfoType == '8' || signInfoType == '9' ||
				   signInfoType == '10' || signInfoType == '11' || signInfoType == '12' || 
				   signInfoType == '13' || signInfoType == '15' || signInfoType == '17' ||
				   signInfoType == '18' || signInfoType == '19' || signInfoType == '20' ){
				   
				   sampleHtml +=	'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">金額(Amount)</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">NT$</div>'+
									'		<div class="css_td">'+data.tx_amount_show+'</div>'+
									'	</div>'+
									'</div>';
				}
				
				
				//小費 & 總計(虛線)
				if(signInfoType == '1' || signInfoType == '15'){
					sampleHtml +=	'<div class="css_table signMontwo" >'+
									'	<div class="css_tr">'+
									'		<div class="css_td">小費(Tips)</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">'+data.tip_amount_show+'</div>'+
									'	</div>'+
									'</div>'+
									'<div class="css_table signMontwo">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">總計</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">'+data.tx_amount_show+'</div>'+
									'	</div>'+
									'</div>';
				}
				
				//分期期數 & 首期金額 & 每期金額 & 分期手續費
				if(signInfoType == '5' || signInfoType == '6' || signInfoType == '7' ||
				   signInfoType == '8' || signInfoType == '17' || signInfoType == '18'){
				   
				   sampleHtml +=	'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">分期期數</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">NT$</div>'+
									'		<div class="css_td">'+data.inst_period+'期</div>'+
									'	</div>'+
									'</div>'+
									'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">首期金額</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">NT$</div>'+
									'		<div class="css_td">'+data.inst_down_payment_show+'</div>'+
									'	</div>'+
									'</div>'+
									'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">每期金額</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">NT$</div>'+
									'		<div class="css_td">'+data.inst_payment_show+'</div>'+
									'	</div>'+
									'</div>'+
									'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">分期手續費</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">NT$</div>'+
									'		<div class="css_td">'+data.inst_fee_show+'</div>'+
									'	</div>'+
									'</div>';
				}
				
				//支付金額 & 紅利扣抵金額 & 紅利扣抵點數
				if(signInfoType == '9' || signInfoType == '10' || signInfoType == '11' ||
				   signInfoType == '12' || signInfoType == '19' || signInfoType == '20'){
				   
				   sampleHtml +=	'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">支付金額</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">NT$</div>'+
									'		<div class="css_td">'+data.redeem_paid_amount_show+'</div>'+
									'	</div>'+
									'</div>'+
									'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">紅利扣抵金額</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">NT$</div>'+
									'		<div class="css_td">'+data.redeem_amount_show+'</div>'+
									'	</div>'+
									'</div>'+
									'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">紅利扣抵點數</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td"></div>'+
									'		<div class="css_td">'+data.redeem_point_show+'點</div>'+
									'	</div>'+
									'</div>';
				}
				
				//紅利剩餘點數
				if(signInfoType == '9'){
					sampleHtml +=	'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">紅利剩餘點數</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td"></div>'+
									'		<div class="css_td">'+data.redeem_balance_point_show+'點</div>'+
									'	</div>'+
									'</div>';
				}
				
				//總計
				if(signInfoType == '2' || signInfoType == '3' || signInfoType == '4' ||
				   signInfoType == '13' || signInfoType == '14' || signInfoType == '16' ||
				   signInfoType == '21' || signInfoType == '22' || signInfoType == '23' || 
				   signInfoType == '24' || signInfoType == '25' || signInfoType == '26' ||
				   signInfoType == '27'){
				   
				   sampleHtml +=	'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">總計(Total)</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">NT$</div>'+
									'		<div class="css_td">'+data.tx_amount_show+'</div>'+
									'	</div>'+
									'</div>';
				}
				
				//分期訊息
				if(signInfoType == '5' || signInfoType == '6' || signInfoType == '7' ||
				   signInfoType == '8' || signInfoType == '17' || signInfoType == '18'){
				
// 					sampleHtml +=	'<div class="css_table pos-card" style="width:96%;margin-left: 6px;margin-top: 10px;color: #777777;">'+	
// 										'<div class="css_tr">'+
// 											'<div class="css_td" style="font-size: 10px;">於分期付款期間內，若持卡人持任一信用卡遭停卡或有</div>'+
// 										'</div>'+
// 										'<div class="css_tr">'+
// 											'<div class="css_td" style="font-size: 10px;">其他信用貶落情形，本行得不經催告請求一次支付未攤</div>'+
// 										'</div>'+
// 										'<div class="css_tr">'+
// 											'<div class="css_td" style="font-size: 10px;">還金額，未攤還金額視為全部到期。</div>'+
// 										'</div>'+
// 										'<br>'+
// 									'</div>';
									
									
					sampleHtml +=	'<div class="sign-logo">'+
									'	<img src="images/installment.png" clas="img-responsive" border="0" alt="">'+
									'</div>';
				}
				
				//本人指定以銀聯卡支付
				if(signInfoType == '21' || signInfoType == '22' || signInfoType == '23' ||
				   signInfoType == '24' || signInfoType == '25' || signInfoType == '26' ||
				   signInfoType == '27' ){
				   
				   sampleHtml +=	'<div class="css_table pos-total cup" style="margin: 5px 0px">'+
									'	<div class="css_tr pos-content sigCUP">'+
									'		本人指定以銀聯卡支付<br> I chose CUP card to pay'+
									'	</div>'+
									'</div>';
				}
				
				sampleHtml +=	'</div>';
				
				if(data.unsign_flag == '0'){
					sampleHtml +=	'<div><div class="belowleft">X：</div><div class="belowlogo"><img src="data:image/jpg;base64,'+signImageFile+'" clas="img-responsive" border="0" alt=""></div></div>';
				}else{
					sampleHtml +=	'<div><div class="belowleft">X：</div><div class="belowlogo"><img src="images/unsigned.png" clas="img-responsive" border="0" alt=""></div></div>';
				}
				
				sampleHtml +=	'<hr>';
				
				//英文姓名
				if(data.cardholder_name != ''){
					sampleHtml +=	'<div>'+data.cardholder_name+'</div>';
				}
				
				sampleHtml +=	'<div class="total-footer">';
				sampleHtml +=	'<h6>持卡人簽名</h6>';
				sampleHtml +=	'I AGREE TO PAY TOTAL AMOUNT<br> ACCORDING TO CARD ISSUER AGREEMENT';
				sampleHtml +=	'</div>';
				sampleHtml +=	'</div>';
				sampleHtml +=	'</div>';	
				
				
				
				

//===================================== printHtml Start ====================================

				
			
				printHtml +=	'<div style="width:97%;margin-left: 3px;">';
				printHtml +=	'<div class="sign-logo">'+
								'	<img src="images/logo-sign-sign.png" clas="img-responsive" border="0" alt="">'+
								'</div>';
				printHtml +=	'<div class="resultout">';
				
				if(imageFile != ''){
					printHtml +=	'<div class="seco-logo" style="width: 70%;">'+
											//20220820 mask '	<img src="data:image/jpg;base64,'+imageFile+'" clas="img-responsive" border="0" alt="">'+
											'	<img src="images/'+imageFile+'" clas="img-responsive" border="0" alt="">'+
											'</div>';
				}
				
				printHtml +=	'<div class="css_table">'+
								'	<div class="css_tr">'+
								'		<div class="css_td">商店代號</div>'+
								'		<div class="css_td">'+data.merchant_id+'</div>'+
								'	</div>'+
								'	<div class="css_tr">'+
								'		<div class="css_td">端末機代號</div>'+
								'		<div class="css_td">'+data.terminal_id+'</div>'+
								'	</div>'+
								'</div>'+
								'<hr>';		
							
				printHtml +=	'<div class="pos-all">'+
								'	<div class="css_table pos-card">'+
								'		<div class="css_tr pos-content">'+
								'<div class="css_td">卡別(Card Type)</div>';
								
				
				//檢查碼 
				if(signInfoType != '21' && signInfoType != '22' && signInfoType != '23' &&
				   signInfoType != '24' && signInfoType != '25' && signInfoType != '26' &&
				   signInfoType != '27' ){
				   
					printHtml +=	'<div class="css_td">檢查碼(Check No.)</div>';
				}
				
				printHtml +=	'</div>'+
								'<div class="css_tr">'+
								'	<div class="css_td">'+data.card_type_show+'</div>';
				
				if(signInfoType != '21' && signInfoType != '22' && signInfoType != '23' &&
				   signInfoType != '24' && signInfoType != '25' && signInfoType != '26' &&
				   signInfoType != '27'){
				   
					printHtml +=	'<div class="css_td">'+data.check_no+'</div>';
				}
				printHtml +=	'</div></div>';
				
				printHtml +=	'<div class="css_table pos-card">'+
								'	<div class="css_tr pos-content">'+
								'		<div class="css_td">卡號(Card No.)</div>'+ 
								'	</div>'+
								'	<div class="css_tr">'+
// 								'		<div class="css_td" style="font-size: 16px;">'+data.card_no.replace("******", maskNo)+'('+data.trans_mode+')</div>'+
								'		<div class="css_td" style="font-size: 16px;">'+cardNo+'('+data.trans_mode+')</div>'+
								'	</div>'+
								'</div>'+
								'<div class="css_table pos-card">'+
								'	<div class="css_tr pos-content">'+
								'		<div class="css_td">交易類別(Trans. Type)</div>'+
								'	</div>';
				if(signInfoType == '8' || signInfoType == '12'|| signInfoType == '27'){
					printHtml += '	<div class="css_tr">'+
								'		<div class="css_td">'+data.trans_type_show_cht+'</div>'+
								'	</div>'+
								' <div class="css_tr">'+
								'		<div class="css_td">'+data.trans_type_show_eng+'</div>'+
								'	</div>';
				}else{
					printHtml += '	<div class="css_tr">'+
								'		<div class="css_td">'+data.trans_type_show_cht+' '+ data.trans_type_show_eng+'</div>'+
								'	</div>';
				}							

				printHtml 		+='</div>'+
								'<div class="css_table pos-card">'+
								'	<div class="css_tr pos-content">'+
								'		<div class="css_td">批次號碼(Batch No.)</div>'+
								'		<div class="css_td">授權碼(Auth Code)</div>'+
								'	</div>'+
								'	<div class="css_tr">'+
								'		<div class="css_td">'+data.batch_no+'</div>'+
								'		<div class="css_td">'+data.auth_code+'</div>'+
								'	</div>'+
								'</div>'+
								'<div class="css_table pos-card">'+
								'	<div class="css_tr pos-content">'+
								'		<div class="css_td">日期/時間(Date/Time)</div>'+
								'	</div>'+
								'	<div class="css_tr">'+
								'		<div class="css_td">'+data.tx_date_time_show+'</div>'+
								'	</div>'+
								'</div>'+
								'<div class="css_table pos-card">'+
								'	<div class="css_tr pos-content">'+
								'		<div class="css_td">調閱編號(Inv. No.)</div>'+
								'		<div class="css_td">序號(Ref. No.)</div>'+
								'	</div>'+
								'	<div class="css_tr">'+
								'		<div class="css_td">'+data.invoice_no+'</div>'+
								'		<div class="css_td">'+data.ref_no+'</div>'+
								'	</div>'+
								'</div>';
				//CUP交易序號	
				if(signInfoType == '21' || signInfoType == '22' || signInfoType == '23' ||
				   signInfoType == '24' || signInfoType == '25' || signInfoType == '26' ||
				   signInfoType == '27'){
				   
				   printHtml +=	'<div class="css_table pos-card">'+
									'	<div class="css_tr pos-content">'+
									'		<div class="css_td">CUP交易序號(CUP STAN)</div>'+
									'	</div>'+
									'	<div class="css_tr">'+
									'		<div class="css_td">'+data.cup_stan+'</div>'+
									'	</div>'+
									'</div>';
				}
				
				printHtml +=	'<div class="css_table pos-card">'+
								'	<div class="css_tr pos-content">'+
								'		<div class="css_td">櫃號(Store ID)</div>'+
								'	</div>'+
								'	<div class="css_tr">'+
								'		<div class="css_td">'+data.store_id+'</div>'+
								'	</div>'+
								'</div>';
								
				//晶片碼
				if(data.emv_tc != ''){
					if(signInfoType == '1' || signInfoType == '2' || signInfoType == '3' ||
					   signInfoType == '4' || signInfoType == '5' || signInfoType == '6' ||
					   signInfoType == '9' || signInfoType == '10' || signInfoType == '13' ||
					   signInfoType == '14' || signInfoType == '21' || signInfoType == '22' ||
					   signInfoType == '24' || signInfoType == '25'){
					   
					   printHtml +=	'<div class="css_table pos-card">'+
										'	<div class="css_tr pos-content">'+
										'		<div class="css_td">晶片碼(TC)</div>'+
										'	</div>'+
										'	<div class="css_tr">'+
										'		<div class="css_td">'+data.emv_tc+'</div>'+
										'	</div>'+
										'</div>';
					}
				}
				
				 printHtml +=	'<div class="total_box">';
		
				
				//金額
				if(signInfoType == '1' || signInfoType == '5' || signInfoType == '6' ||
				   signInfoType == '7' || signInfoType == '8' || signInfoType == '9' ||
				   signInfoType == '10' || signInfoType == '11' || signInfoType == '12' || 
				   signInfoType == '13' || signInfoType == '15' || signInfoType == '17' ||
				   signInfoType == '18' || signInfoType == '19' || signInfoType == '20' ){
				   
				   printHtml +=	'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">金額(Amount)</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">NT$</div>'+
									'		<div class="css_td">'+data.tx_amount_show+'</div>'+
									'	</div>'+
									'</div>';
				}
				
				
				//小費 & 總計(虛線)
				if(signInfoType == '1' || signInfoType == '15'){
					printHtml +=	'<div class="css_table signMontwo" >'+
									'	<div class="css_tr">'+
									'		<div class="css_td">小費(Tips)</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">'+data.tip_amount_show+'</div>'+
									'	</div>'+
									'</div>'+
									'<div class="css_table signMontwo">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">總計</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">'+data.tx_amount_show+'</div>'+
									'	</div>'+
									'</div>';
				}
				
				//分期期數 & 首期金額 & 每期金額 & 分期手續費
				if(signInfoType == '5' || signInfoType == '6' || signInfoType == '7' ||
				   signInfoType == '8' || signInfoType == '17' || signInfoType == '18'){
				   
				   printHtml +=	'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">分期期數</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">NT$</div>'+
									'		<div class="css_td">'+data.inst_period+'期</div>'+
									'	</div>'+
									'</div>'+
									'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">首期金額</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">NT$</div>'+
									'		<div class="css_td">'+data.inst_down_payment_show+'</div>'+
									'	</div>'+
									'</div>'+
									'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">每期金額</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">NT$</div>'+
									'		<div class="css_td">'+data.inst_payment_show+'</div>'+
									'	</div>'+
									'</div>'+
									'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">分期手續費</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">NT$</div>'+
									'		<div class="css_td">'+data.inst_fee_show+'</div>'+
									'	</div>'+
									'</div>';
				}
				
				//支付金額 & 紅利扣抵金額 & 紅利扣抵點數
				if(signInfoType == '9' || signInfoType == '10' || signInfoType == '11' ||
				   signInfoType == '12' || signInfoType == '19' || signInfoType == '20'){
				   
				   printHtml +=	'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">支付金額</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">NT$</div>'+
									'		<div class="css_td">'+data.redeem_paid_amount_show+'</div>'+
									'	</div>'+
									'</div>'+
									'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">紅利扣抵金額</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">NT$</div>'+
									'		<div class="css_td">'+data.redeem_amount_show+'</div>'+
									'	</div>'+
									'</div>'+
									'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">紅利扣抵點數</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td"></div>'+
									'		<div class="css_td">'+data.redeem_point_show+'點</div>'+
									'	</div>'+
									'</div>';
				}
				
				//紅利剩餘點數
				if(signInfoType == '9'){
					printHtml +=	'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">紅利剩餘點數</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td"></div>'+
									'		<div class="css_td">'+data.redeem_balance_point_show+'點</div>'+
									'	</div>'+
									'</div>';
				}
				
				//總計
				if(signInfoType == '2' || signInfoType == '3' || signInfoType == '4' ||
				   signInfoType == '13' || signInfoType == '14' || signInfoType == '16' ||
				   signInfoType == '21' || signInfoType == '22' || signInfoType == '23' || 
				   signInfoType == '24' || signInfoType == '25' || signInfoType == '26' ||
				   signInfoType == '27'){
				   
				   printHtml +=	'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">總計(Total)</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">NT$</div>'+
									'		<div class="css_td">'+data.tx_amount_show+'</div>'+
									'	</div>'+
									'</div>';
				}
				
				//分期訊息
				if(signInfoType == '5' || signInfoType == '6' || signInfoType == '7' ||
				   signInfoType == '8' || signInfoType == '17' || signInfoType == '18'){
				
// 					printHtml +=	'<div class="css_table pos-card" style="width:96%;margin-left: 6px;margin-top: 10px;color: #777777;">'+	
// 										'<div class="css_tr">'+
// 											'<div class="css_td" style="font-size: 10px;">於分期付款期間內，若持卡人持任一信用卡遭停卡或有</div>'+
// 										'</div>'+
// 										'<div class="css_tr">'+
// 											'<div class="css_td" style="font-size: 10px;">其他信用貶落情形，本行得不經催告請求一次支付未攤</div>'+
// 										'</div>'+
// 										'<div class="css_tr">'+
// 											'<div class="css_td" style="font-size: 10px;">還金額，未攤還金額視為全部到期。</div>'+
// 										'</div>'+
// 										'<br>'+
// 									'</div>';
					printHtml +=	'<div class="sign-logo">'+
									'	<img src="images/installment.png" clas="img-responsive" border="0" alt="">'+
									'</div>';
				}
				
				//本人指定以銀聯卡支付
				if(signInfoType == '21' || signInfoType == '22' || signInfoType == '23' ||
				   signInfoType == '24' || signInfoType == '25' || signInfoType == '26' ||
				   signInfoType == '27' ){
				   
				   printHtml +=	'<div class="css_table pos-total cup" style="margin: 5px 0px">'+
									'	<div class="css_tr pos-content sigCUP">'+
									'		本人指定以銀聯卡支付<br> I chose CUP card to pay'+
									'	</div>'+
									'</div>';
				}
				
				printHtml +=	'</div>';
				if(data.unsign_flag == '0'){
					printHtml +=	'<div><div class="belowleft">X：</div><div class="belowlogo"><img src="data:image/jpg;base64,'+signImageFile+'" clas="img-responsive" border="0" alt=""></div></div>';
				}else{
					printHtml +=	'<div><div class="belowleft">X：</div><div class="belowlogo"><img src="images/unsigned.png" clas="img-responsive" border="0" alt=""></div></div>';
				}
				printHtml +=	'<hr>';
				
				//英文姓名
				if(data.cardholder_name != ''){
					printHtml +=	'<div>'+data.cardholder_name+'</div>';
				}
				
				printHtml +=	'<div class="total-footer">';
				printHtml +=	'<h6>持卡人簽名</h6>';
				printHtml +=	'I AGREE TO PAY TOTAL AMOUNT<br> ACCORDING TO CARD ISSUER AGREEMENT';
				printHtml +=	'</div>';
				printHtml +=	'</div>';
				printHtml +=	'</div>';
				printHtml +=	'</div>';
				
//===================================== printHtml End ====================================		

//===================================== downloadHtml Start ====================================

				
			
				downloadHtml +=	'<div style="width:97%;margin-left: 3px;">';
				downloadHtml +=	'<div class="sign-logo">'+
								'	<img src="images/logo-sign-sign.png" clas="img-responsive" border="0" alt="">'+
								'</div>';
				downloadHtml +=	'<div class="resultout">';
				
				if(imageFile != ''){
					downloadHtml +=	'<div class="seco-logo">'+
											//20220820 mask '	<img src="data:image/jpg;base64,'+imageFile+'" clas="img-responsive" border="0" alt="">'+
											'	<img src="images/'+imageFile+'" clas="img-responsive" border="0" alt="">'+
											'</div>';
				}
				
				downloadHtml +=	'<div class="css_table">'+
								'	<div class="css_tr">'+
								'		<div class="css_td">商店代號</div>'+
								'		<div class="css_td">'+data.merchant_id+'</div>'+
								'	</div>'+
								'	<div class="css_tr">'+
								'		<div class="css_td">端末機代號</div>'+
								'		<div class="css_td">'+data.terminal_id+'</div>'+
								'	</div>'+
								'</div>';
// 								'<hr>';		

				if(browser == '1'){
					downloadHtml +=	'<div style="height: 16px ;margin-top: -12px;">------------------------------------</div>';
				}else if(browser == '2'){
					downloadHtml +=	'<div style="height: 16px ;margin-top: -12px;">------------------------------------</div>';
				}else{
					downloadHtml +=	'<hr>';	
				}
							
				downloadHtml +=	'<div class="pos-all">'+
								'	<div class="css_table pos-card">'+
								'		<div class="css_tr pos-content">'+
								'<div class="css_td">卡別(Card Type)</div>';
								
				
				//檢查碼 
				if(signInfoType != '21' && signInfoType != '22' && signInfoType != '23' &&
				   signInfoType != '24' && signInfoType != '25' && signInfoType != '26' &&
				   signInfoType != '27' ){
				   
					downloadHtml +=	'<div class="css_td">檢查碼(Check No.)</div>';
				}
				
				downloadHtml +=	'</div>'+
								'<div class="css_tr">'+
								'	<div class="css_td">'+data.card_type_show+'</div>';
											
				
				if(signInfoType != '21' && signInfoType != '22' && signInfoType != '23' &&
				   signInfoType != '24' && signInfoType != '25' && signInfoType != '26' &&
				   signInfoType != '27'){
				   
					downloadHtml +=	'<div class="css_td">'+data.check_no+'</div>';
				}
				
				
				downloadHtml +=	'</div></div>';
				
				downloadHtml +=	'<div class="css_table pos-card">'+
								'	<div class="css_tr pos-content">'+
								'		<div class="css_td">卡號(Card No.)</div>'+ 
								'	</div>'+
								'	<div class="css_tr">'+
// 								'		<div class="css_td" style="font-size: 16px;">'+data.card_no.replace("******", maskNo)+'('+data.trans_mode+')</div>'+
								'		<div class="css_td" style="font-size: 16px;">'+cardNo+'('+data.trans_mode+')</div>'+
								'	</div>'+
								'</div>'+
								'<div class="css_table pos-card">'+
								'	<div class="css_tr pos-content">'+
								'		<div class="css_td">交易類別(Trans. Type)</div>'+
								'	</div>';
				if(signInfoType == '8' || signInfoType == '12'|| signInfoType == '27'){
					downloadHtml += '	<div class="css_tr">'+
								'		<div class="css_td">'+data.trans_type_show_cht+'</div>'+
								'	</div>'+
								' <div class="css_tr">'+
								'		<div class="css_td">'+data.trans_type_show_eng+'</div>'+
								'	</div>';
				}else{
					downloadHtml += '	<div class="css_tr">'+
								'		<div class="css_td">'+data.trans_type_show_cht+' '+ data.trans_type_show_eng+'</div>'+
								'	</div>';
				}						
				
				downloadHtml  +='</div>'+
								'<div class="css_table pos-card">'+
								'	<div class="css_tr pos-content">'+
								'		<div class="css_td">批次號碼(Batch No.)</div>'+
								'		<div class="css_td">授權碼(Auth Code)</div>'+
								'	</div>'+
								'	<div class="css_tr">'+
								'		<div class="css_td">'+data.batch_no+'</div>'+
								'		<div class="css_td">'+data.auth_code+'</div>'+
								'	</div>'+
								'</div>'+
								'<div class="css_table pos-card">'+
								'	<div class="css_tr pos-content">'+
								'		<div class="css_td">日期/時間(Date/Time)</div>'+
								'	</div>'+
								'	<div class="css_tr">'+
								'		<div class="css_td">'+data.tx_date_time_show+'</div>'+
								'	</div>'+
								'</div>'+
								'<div class="css_table pos-card">'+
								'	<div class="css_tr pos-content">'+
								'		<div class="css_td">調閱編號(Inv. No.)</div>'+
								'		<div class="css_td">序號(Ref. No.)</div>'+
								'	</div>'+
								'	<div class="css_tr">'+
								'		<div class="css_td">'+data.invoice_no+'</div>'+
								'		<div class="css_td">'+data.ref_no+'</div>'+
								'	</div>'+
								'</div>';
				//CUP交易序號	
				if(signInfoType == '21' || signInfoType == '22' || signInfoType == '23' ||
				   signInfoType == '24' || signInfoType == '25' || signInfoType == '26' ||
				   signInfoType == '27'){
				   
				   downloadHtml +=	'<div class="css_table pos-card">'+
									'	<div class="css_tr pos-content">'+
									'		<div class="css_td">CUP交易序號(CUP STAN)</div>'+
									'	</div>'+
									'	<div class="css_tr">'+
									'		<div class="css_td">'+data.cup_stan+'</div>'+
									'	</div>'+
									'</div>';
				}
				
				downloadHtml +=	'<div class="css_table pos-card">'+
								'	<div class="css_tr pos-content">'+
								'		<div class="css_td">櫃號(Store ID)</div>'+
								'	</div>'+
								'	<div class="css_tr">'+
								'		<div class="css_td">'+data.store_id+'</div>'+
								'	</div>'+
								'</div>';
								
				//晶片碼
				if(data.emv_tc != ''){
					if(signInfoType == '1' || signInfoType == '2' || signInfoType == '3' ||
					   signInfoType == '4' || signInfoType == '5' || signInfoType == '6' ||
					   signInfoType == '9' || signInfoType == '10' || signInfoType == '13' ||
					   signInfoType == '14' || signInfoType == '21' || signInfoType == '22' ||
					   signInfoType == '24' || signInfoType == '25'){
					   
					   downloadHtml +=	'<div class="css_table pos-card">'+
										'	<div class="css_tr pos-content">'+
										'		<div class="css_td">晶片碼(TC)</div>'+
										'	</div>'+
										'	<div class="css_tr">'+
										'		<div class="css_td">'+data.emv_tc+'</div>'+
										'	</div>'+
										'</div>';
					}
				}
				
				 downloadHtml +=	'<div class="total_box">';
		
				
				//金額
				if(signInfoType == '1' || signInfoType == '5' || signInfoType == '6' ||
				   signInfoType == '7' || signInfoType == '8' || signInfoType == '9' ||
				   signInfoType == '10' || signInfoType == '11' || signInfoType == '12' || 
				   signInfoType == '13' || signInfoType == '15' || signInfoType == '17' ||
				   signInfoType == '18' || signInfoType == '19' || signInfoType == '20' ){
				   
				   downloadHtml +=	'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">金額(Amount)</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">NT$</div>'+
									'		<div class="css_td">'+data.tx_amount_show+'</div>'+
									'	</div>'+
									'</div>';
				}
				
				
				//小費 & 總計(虛線)
				if(signInfoType == '1' || signInfoType == '15'){
					downloadHtml +=	'<div class="css_table signMontwo" >'+
									'	<div class="css_tr">'+
									'		<div class="css_td">小費(Tips)</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">'+data.tip_amount_show+'</div>'+
									'	</div>'+
									'</div>'+
									'<div class="css_table signMontwo">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">總計</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">'+data.tx_amount_show+'</div>'+
									'	</div>'+
									'</div>';
				}
				
				//分期期數 & 首期金額 & 每期金額 & 分期手續費
				if(signInfoType == '5' || signInfoType == '6' || signInfoType == '7' ||
				   signInfoType == '8' || signInfoType == '17' || signInfoType == '18'){
				   
				   downloadHtml +=	'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">分期期數</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">NT$</div>'+
									'		<div class="css_td">'+data.inst_period+'期</div>'+
									'	</div>'+
									'</div>'+
									'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">首期金額</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">NT$</div>'+
									'		<div class="css_td">'+data.inst_down_payment_show+'</div>'+
									'	</div>'+
									'</div>'+
									'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">每期金額</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">NT$</div>'+
									'		<div class="css_td">'+data.inst_payment_show+'</div>'+
									'	</div>'+
									'</div>'+
									'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">分期手續費</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">NT$</div>'+
									'		<div class="css_td">'+data.inst_fee_show+'</div>'+
									'	</div>'+
									'</div>';
				}
				
				//支付金額 & 紅利扣抵金額 & 紅利扣抵點數
				if(signInfoType == '9' || signInfoType == '10' || signInfoType == '11' ||
				   signInfoType == '12' || signInfoType == '19' || signInfoType == '20'){
				   
				   downloadHtml +=	'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">支付金額</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">NT$</div>'+
									'		<div class="css_td">'+data.redeem_paid_amount_show+'</div>'+
									'	</div>'+
									'</div>'+
									'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">紅利扣抵金額</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">NT$</div>'+
									'		<div class="css_td">'+data.redeem_amount_show+'</div>'+
									'	</div>'+
									'</div>'+
									'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">紅利扣抵點數</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td"></div>'+
									'		<div class="css_td">'+data.redeem_point_show+'點</div>'+
									'	</div>'+
									'</div>';
				}
				
				//紅利剩餘點數
				if(signInfoType == '9'){
					downloadHtml +=	'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">紅利剩餘點數</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td"></div>'+
									'		<div class="css_td">'+data.redeem_balance_point_show+'點</div>'+
									'	</div>'+
									'</div>';
				}
				
				//總計
				if(signInfoType == '2' || signInfoType == '3' || signInfoType == '4' ||
				   signInfoType == '13' || signInfoType == '14' || signInfoType == '16' ||
				   signInfoType == '21' || signInfoType == '22' || signInfoType == '23' || 
				   signInfoType == '24' || signInfoType == '25' || signInfoType == '26' ||
				   signInfoType == '27'){
				   
				   downloadHtml +=	'<div class="css_table signMonOne">'+
									'	<div class="css_tr">'+
									'		<div class="css_td">總計(Total)</div>'+
									'		<div class="css_td">:</div>'+
									'		<div class="css_td">NT$</div>'+
									'		<div class="css_td">'+data.tx_amount_show+'</div>'+
									'	</div>'+
									'</div>';
				}
				
				//分期訊息
				if(signInfoType == '5' || signInfoType == '6' || signInfoType == '7' ||
				   signInfoType == '8' || signInfoType == '17' || signInfoType == '18'){
				
// 					downloadHtml +=	'<div class="css_table pos-card" style="width:96%;margin-left: 6px;margin-top: 10px;color: #777777;">'+	
// 										'<div class="css_tr">'+
// 											'<div class="css_td" style="font-size: 10px;">於分期付款期間內，若持卡人持任一信用卡遭停卡或有</div>'+
// 										'</div>'+
// 										'<div class="css_tr">'+
// 											'<div class="css_td" style="font-size: 10px;">其他信用貶落情形，本行得不經催告請求一次支付未攤</div>'+
// 										'</div>'+
// 										'<div class="css_tr">'+
// 											'<div class="css_td" style="font-size: 10px;">還金額，未攤還金額視為全部到期。</div>'+
// 										'</div>'+
// 										'<br>'+
// 									'</div>';
									
					downloadHtml +=	'<div class="sign-logo">'+
									'	<img src="images/installment.png" clas="img-responsive" border="0" alt="">'+
									'</div>';
				}
				
				
				
				//本人指定以銀聯卡支付
				if(signInfoType == '21' || signInfoType == '22' || signInfoType == '23' ||
				   signInfoType == '24' || signInfoType == '25' || signInfoType == '26' ||
				   signInfoType == '27' ){
				   
				   downloadHtml +=	'<div class="css_table pos-total cup" style="margin: 5px 0px">'+
									'	<div class="css_tr pos-content sigCUP">'+
									'		本人指定以銀聯卡支付<br> I chose CUP card to pay'+
									'	</div>'+
									'</div>';
				}
				
				downloadHtml +=	'</div>';
				
				if(data.unsign_flag == '0'){
					downloadHtml +=	'<div><div class="belowleft">X：</div><div class="belowlogo"><img src="data:image/jpg;base64,'+signImageFile+'" clas="img-responsive" border="0" alt=""></div></div>';
				}else{
					downloadHtml +=	'<div><div class="belowleft">X：</div><div class="belowlogo"><img src="images/unsigned.png" clas="img-responsive" border="0" alt=""></div></div>';
				}
				
				if(browser == '1'){
					downloadHtml +=	'<div style="height: 16px ;margin-top: 0px;">------------------------------------</div>';
				}else if(browser == '2'){
					downloadHtml +=	'<div style="height: 16px ;margin-top: -12px;">------------------------------------</div>';
				}else{
					downloadHtml +=	'<hr>';	
				}
				
				
				//英文姓名
				if(data.cardholder_name != ''){
					downloadHtml +=	'<div>'+data.cardholder_name+'</div>';
				}
				
				downloadHtml +=	'<div class="total-footer">';
				downloadHtml +=	'<h6>持卡人簽名</h6>';
				downloadHtml +=	'I AGREE TO PAY TOTAL AMOUNT<br> ACCORDING TO CARD ISSUER AGREEMENT';
				downloadHtml +=	'</div>';
				downloadHtml +=	'</div>';
				downloadHtml +=	'</div>';
				downloadHtml +=	'</div>';
				
//===================================== downloadHtml End ====================================							
			}else if(data.data_source=='F'){  //全家便利商店套版
				
				
				
				
				
				sampleHtmlF +=	'<div class="resultout qa-out" style="overflow: hidden;">';
				
				
				sampleHtmlF +=	'<div class="sign-logo">'+
				'	<img src="images/logo-sign-sign.png" clas="img-responsive" border="0" alt="">'+
				'</div>';
				
				
				sampleHtmlF +=	'<div class="sign-logo">'+
 								'	<img src="images/logo_family.png" class="img-responsive" border="0" alt="">'+
 								'</div>';
				
//				if(imageFile != ''){
//					sampleHtmlF +=	'<div class="seco-logo" style="margin-top:5px;">'+
//								'	<img src="data:image/jpg;base64,'+imageFile+'" clas="img-responsive" border="0" alt="">'+
//							'</div>';
//				}	
				

				
				sampleHtmlF +='<div class="pos-all" style="margin-top:5px;>'+
							'<div class="css_table">'+
							  '<div class="css_tr">'+
							  	'<div class="css_td" style="text-indent :3em;">'+callName+'</div>	'+
							  '</div>';

								
				sampleHtmlF +='<div class="css_table pos-card css_m">'+
					
							'<div class="css_tr">'+
								'<span class="css_font">收單銀行: </span>'+'台北富邦銀行'+			  
							 '</div>'+
							 
							 '</div>';

							  
				sampleHtmlF +='<div class="css_table pos-card css_m">'+
								
								'<div class="css_tr">'+
								'<span class="css_font">特店代號:</span>'+data.merchant_id+			  
								'</div>'+
								'</div>';			 
				
				sampleHtmlF +='<div class="css_table pos-card css_m">'+
								'<div class="css_tr">'+
									'<span class="css_font">端末機代號:</span>'+data.terminal_id+			  
								'</div>'+
							 '</div>';
							 
				sampleHtmlF +='<div class="css_table pos-card css_m">'+
								
							    '<div class="css_tr">'+
									'<span class="css_font">信用卡卡別:</span>'+data.card_type_show+
								'</div>'+
							 '</div>';
			
							 
				sampleHtmlF +='<div class="css_table pos-card css_m">'+
								'<div class="css_tr">'+
									'<span class="css_font">信用卡號:</span>'+cardNo+'('+data.trans_mode+')'+			  
								'</div>'+
								'</div>';
							 
				sampleHtmlF +='<div class="css_table pos-card css_m">'+
								'<div class="css_tr">'+
									'<span class="css_font">交易類別:</span>'+data.trans_type_show_cht+' '+ data.trans_type_show_eng+			  
								'</div>'+
								'</div>';						 
 
				
							 
				sampleHtmlF +='<div class="css_table pos-card css_m">'+
							'<div class="css_tr">'+
								'<span class="css_font">日期時間:</span>'+	data.tx_date_time_show+	  
							'</div>'+
							'</div>';
				
							
				sampleHtmlF +='<div class="css_table pos-card css_m">'+
							'<div class="css_tr">'+
								'<span class="css_font">授權碼:</span>'+data.auth_code+			  
							'</div>'+
						  '</div>';
				
						  
						  

							 
				sampleHtmlF +='<div class="css_table pos-card css_m">'+
							'<div class="css_tr">'+
								'<span class="css_font">調閱編號:</span>'+data.invoice_no+'<span class="css_font">/批次號碼:</span>'+data.batch_no+			  
								'</div>'+
							'</div>';
							 
				sampleHtmlF +='<div class="css_table pos-card css_m">'+
							'<div class="css_tr">'+
								'<span class="css_font">櫃號/發票號碼:</span>'+data.store_id+			  
								'</div>'+
							'</div>';			 

				sampleHtmlF +='<div class="css_table pos-card css_m">'+
							'<div class="css_tr">'+
								'<span class="css_font">晶片號碼:</span>'+data.emv_tc+			  
								'</div>'+
							'</div>';			 
							 
				sampleHtmlF +='<div class="css_table pos-card css_m">'+
								'<div class="css_tr">'+
								'<span class="css_font">金額:</span>'+data.tx_amount_show+'元'+			  
								'</div>'+
							 '</div>';
							 
				sampleHtmlF +='<br><br>';
							 
				sampleHtmlF +='<div class="css_tr css_m" >';
// 								'<span>X：</span>';
								
								
								
				if(data.unsign_flag=='0'){
					sampleHtmlF +=	'<div><span>X：</span><div class="belowlogo"><img src="data:image/jpg;base64,'+signImageFile+'" clas="img-responsive" border="0" alt=""></div></div>';
					sampleHtmlF +=	'<div class="line" style=" width:190px;border-bottom: 1px solid black;transform:translate(23px,-18px);"></div>';
				}else{
					sampleHtmlF +=	'<div><span>X：</span><div class="belowlogo"><img src="images/unsigned.png" clas="img-responsive" border="0" alt=""></div></div>';
				}
								
			
								
								
								
// 				if(data.cardholder_name != ''){
// 					sampleHtmlF +='<span>'+data.cardholder_name+'</span>';
// 				}
								
				sampleHtmlF +='</div>';
							 
							 
				
				
				sampleHtmlF +='<div class="total-footer" style="transform:scale(0.88);">'+
								'<h6 style="transform: translate(-70px,5px);">持卡人簽名  商店收據存根聯</h6>'+
								'<div class="total-footer" style="transform: translate(-15px,5px); margin-top:4px;text-align: left;white-space: nowrap;">';
				sampleHtmlF +='I AGREE TO PAY TOTAL AMOUNT ACCORDING'
								'</div>';								
				sampleHtmlF +=	'<div class="total-footer" style="transform: translate(-1px,5px); text-align: left;">'+
								'TO CARD ISSUER AGREEMENT'+	
								'</div>'+
								'<br>'+
								'</div>';											  
				
			 	sampleHtmlF +=	'</div>'+
							  '</div>';					
						
			
				//===================================== printHtmlF Start ====================================

				
				
				printHtmlF +=	'<div style="width:97%;margin-left: 3px;">';
	
				printHtmlF +=	'<div class="resultout">';

								printHtmlF +=	'<div class="sign-logo">'+
 								'	<img src="images/logo-sign-sign.png" class="img-responsive" border="0" alt="">'+
 								'</div>';


								printHtmlF +=	'<div class="sign-logo">'+
 								'	<img src="images/logo_family.png" class="img-responsive" border="0" alt="">'+
 								'</div>';

				
//				if(imageFile != ''){
//					printHtmlF +=	'<div class="seco-logo" style="width: 70%;">'+
//											'	<img src="data:image/jpg;base64,'+imageFile+'" clas="img-responsive" //border="0" alt="">'+
//											'</div>';
//				}
				
				printHtmlF +=	'<div class="css_table">'+
								'	<div class="css_tr">'+
								'		<div class="css_td" style="text-indent :2em">'+callName+'</div>'+	
								'</div>'+
								
								'<div class="css_table pos-card css_m">'+								
									'<div class="css_tr">'+
										'<span class="css_font">收單銀行: </span>'+'台北富邦銀行'+				  
								 	'</div>'+								 
								'</div>';
							
							
				printHtmlF +=	'<div class="css_table pos-card css_m">'+
				
			    					'<div class="css_tr">'+
										'<span class="css_font">信用卡卡別:</span>'+data.card_type_show+
									'</div>'+
			 					'</div>';
								
				
			
				
			
				
			 	printHtmlF +='<div class="css_table pos-card css_m">'+
								
								'<div class="css_tr">'+
									'<span class="css_font">特店代號:</span>'+data.merchant_id+			  
								'</div>'+
								'</div>';			 
				
				printHtmlF +='<div class="css_table pos-card css_m">'+
								'<div class="css_tr">'+
									'<span class="css_font">端末機代號:</span>'+data.terminal_id+		  
								'</div>'+
							 '</div>';
							 
				printHtmlF +='<div class="css_table pos-card css_m">'+
							'<div class="css_tr">'+
								'<span class="css_font">日期時間:</span>'+data.tx_date_time_show+			  
							'</div>'+
							'</div>';
							 
				printHtmlF +='<div class="css_table pos-card css_m">'+
								'<div class="css_tr">'+
								'<span class="css_font">交易類別:</span>'+data.trans_type_show_cht+' '+ data.trans_type_show_eng+		  
								'</div>'+
								'</div>';
							 
				printHtmlF +='<div class="css_table pos-card css_m">'+
							'<div class="css_tr">'+
								'<span class="css_font">調閱編號:</span>'+data.invoice_no+'<span class="css_font">/批次號碼:</span>'+data.batch_no+			  
								'</div>'+
								'</div>';
							 
							 
				printHtmlF +='<div class="css_table pos-card css_m">'+
								'<div class="css_tr">'+
									'<span class="css_font">授權碼:'+data.auth_code+'</span>'+			  
								'</div>'+
							  '</div>';
							 
				printHtmlF +='<div class="css_table pos-card css_m">'+
								'<div class="css_tr">'+
									'<span class="css_font">信用卡號:</span>'+cardNo+'('+data.trans_mode+')'+			  
								'</div>'+
								'</div>';
				
							 
				printHtmlF +='<div class="css_table pos-card css_m">'+
								'<div class="css_tr">'+
								'<span class="css_font">金額:</span>'+data.tx_amount_show+'元'+			  
								'</div>'+
							 '</div>';
				
				printHtmlF +='<br><br>';
				
// 				printHtmlF +='<div class="css_tr css_m">'+
// 								'<div class="css_td">X：</div>'+			  
// 							 '</div>';
							 
				if(data.unsign_flag == '0'){
					printHtmlF +=	'<div><span>X：</span><div class="belowlogo"><img src="data:image/jpg;base64,'+signImageFile+'" clas="img-responsive" border="0" alt=""></div></div>';
					printHtmlF +=	'<div class="line" style=" width:190px;border-bottom: 1px solid black;transform:translate(23px,-18px);;"></div>';
				}else{
					printHtmlF +=	'<div><span>X：</span><div class="belowlogo"><img src="images/unsigned.png" clas="img-responsive" border="0" alt=""></div></div>';
				}
							 
							 
				
				
				printHtmlF +='<div class="total-footer" style="transform:scale(0.88);">'+
								'<h6 style="transform: translate(-77px,5px);">持卡人簽名  商店收據存根聯</h6>'+
								'<div class="total-footer" style="transform: translate(-15px,5px); margin-top:4px;text-align: left;white-space: nowrap;">';
				printHtmlF +='I AGREE TO PAY TOTAL AMOUNT ACCORDING'
								'</div>';								
				printHtmlF +=	'<div class="total-footer" style="transform: translate(-1px,5px);text-align: left;">'+
								'TO CARD ISSUER AGREEMENT'+	
								'</div>'+
								'<br>'+
								'</div>';		
								
				printHtmlF +=	'</div>';
				printHtmlF +=	'</div>';
				printHtmlF +=	'</div>';
				
//===================================== printHtmlF End ====================================	
			
			
//===================================== downloadHtml Start ====================================

				
				
				downloadHtmlF +=	'<div style="width:97%;margin-left: 3px;">';

				downloadHtmlF +=	'<div class="resultout">';


								downloadHtmlF +=	'<div class="sign-logo">'+
 								'	<img src="images/logo-sign-sign.png" class="img-responsive" border="0" alt="">'+
 								'</div>';


								downloadHtmlF +=	'<div class="sign-logo">'+
 								'	<img src="images/logo_family.png" class="img-responsive" border="0" alt="">'+
 								'</div>';

				
//				if(imageFile != ''){
//					downloadHtmlF +=	'<div class="seco-logo">'+
//											'	<img src="data:image/jpg;base64,'+imageFile+'" clas="img-responsive" //border="0" alt="">'+
//											'</div>';
//				}
				
			
	
			downloadHtmlF +=	'<div class="css_table">'+
								'	<div class="css_tr">'+
										'<div class="css_td" style="text-indent :2em">'+callName+'</div>'+	
								'</div>'+
				
								'<div class="css_table pos-card css_m">'+								
									'<div class="css_tr">'+
										'<span class="css_font">收單銀行:</span>'+'台北富邦銀行'+			  
				 					'</div>'+								 
								'</div>';
			
			
			downloadHtmlF +=	'<div class="css_table pos-card css_m">'+

									'<div class="css_tr">'+
										'<span class="css_font">信用卡卡別:</span>'+data.card_type_show+
									'</div>'+
								'</div>';
				
			downloadHtmlF +='<div class="css_table pos-card css_m">'+
				
								'<div class="css_tr">'+
									'<span class="css_font">特店代號:</span>'+data.merchant_id+			  
								'</div>'+
							 '</div>';			 

			downloadHtmlF +='<div class="css_table pos-card css_m">'+
								'<div class="css_tr">'+
									'<span class="css_font">端末機代號:</span>'+data.terminal_id+		  
								'</div>'+
			 				'</div>';
			 
			downloadHtmlF +='<div class="css_table pos-card css_m">'+
								'<div class="css_tr">'+
									'<span class="css_font">日期時間:</span>'+data.tx_date_time_show+			  
								'</div>'+
							'</div>';
			 
			downloadHtmlF +='<div class="css_table pos-card css_m">'+
								'<div class="css_tr">'+
									'<span class="css_font">交易類別:</span>'+data.trans_type_show_cht+' '+ data.trans_type_show_eng+			  
								'</div>'+
							'</div>';
			 
			downloadHtmlF +='<div class="css_table pos-card css_m">'+
								'<div class="css_tr">'+
									'<span class="css_font">調閱編號:</span>'+data.invoice_no+'<span class="css_font">/批次號碼:</span>'+data.batch_no+			  
								'</div>'+
							 '</div>';
			 
			 
			 downloadHtmlF +='<div class="css_table pos-card css_m">'+
								'<div class="css_tr">'+
									'<span class="css_font">授權碼:</span>'+data.auth_code+		  
								'</div>'+
			  				'</div>';
			 
			 downloadHtmlF +='<div class="css_table pos-card css_m">'+
									'<div class="css_tr">'+
										'<span class="css_font">信用卡號:</span>'+cardNo+'('+data.trans_mode+')'+			  
									'</div>'+
							'</div>';

			 
			 downloadHtmlF +='<div class="css_table pos-card css_m">'+
								'<div class="css_tr">'+
									'<span class="css_font">金額:</span>'+data.tx_amount_show+'元'+			  
								'</div>'+
			 				'</div>';


			 downloadHtmlF +='<br><br>';
			 				

			 				
			if(data.unsign_flag == '0'){
				downloadHtmlF +=	'<div><span>X：</span><div class="belowlogo"><img src="data:image/jpg;base64,'+signImageFile+'" clas="img-responsive" border="0" alt=""></div></div>';
				downloadHtmlF +=	'<div class="line" style=" width:190px;border-bottom: 1px solid black;transform:translate(23px,-18px);"></div>';
			}else{
				downloadHtmlF +=	'<div><span>X：</span><div class="belowlogo"><img src="images/unsigned.png" clas="img-responsive" border="0" alt=""></div></div>';
			}
			 
			 
			 

			downloadHtmlF +='<div class="total-footer" style="transform:scale(0.88);">'+
						'<h6 style="transform: translate(-77px,5px);">持卡人簽名  商店收據存根聯</h6>'+
						'<div class="total-footer" style="transform: translate(-15px,5px); margin-top:4px;text-align: left;white-space: nowrap;">';
			downloadHtmlF +='I AGREE TO PAY TOTAL AMOUNT ACCORDING'
						'</div>';								
			downloadHtmlF +=	'<div class="total-footer" style="transform: translate(-1px,5px);text-align: left;">'+
						'TO CARD ISSUER AGREEMENT'+	
						'</div>'+
						'<br>'+
						'</div>';	
			
				
			 downloadHtmlF +=	'</div>';
			 downloadHtmlF +=	'</div>';
				
//===================================== downloadHtml End ====================================			
	
			
			}
				
		}//success end
		
		
		
		});
    	
		$('#posbox1').html(sampleHtml);		
		$('#printPdf').html(printHtml);
		$('#downloadPdf').html(downloadHtml);
		cancel()
		
// 		sampleHtmlF="sampleF";
// 		alert("sampleHtml:"+sampleHtml);
// 		alert("printHtml:"+printHtml);
// 		alert("downloadHtml:"+downloadHtml);
// 		alert("sampleHtmlF:"+sampleHtmlF);
// 		alert("printHtmlF:"+printHtmlF);
// 		alert("downloadHtmlF:"+downloadHtmlF);
		
		if(sampleHtmlF!=''){
			$('#posboxF').html(sampleHtmlF);
		}
		
		if(printHtmlF!=''){
			$('#printPdfF').html(printHtmlF);
		}
		
		if(downloadHtmlF!=''){
			$('#downloadPdfF').html(downloadHtmlF);
		}
		
		
		$("#posbox1").css({
			"display" : "block"
		});
		
		$("#posboxF").css({
			"display" : "block",
//			"flex-direction":"column",
// 			"align-items": "flex-start"
		});
		
		$(".css_m").css({
			 "margin-top": "6px"
		});
		
		$(".box_inv").css({
			"display" : "block"		
		});
		
		$(".css_font").css({
			"color":"#777777",			
		});
		
				
		//ajax end
		
	}
	
	function downloadPdf(){
		$("#downloadPdf").css("display","block");
		$("#downloadPdfF").css("display","block");
		
		console.log("$('#downloadPdf'):"+$('#downloadPdf'));
		
		html2canvas($("#downloadPdf"), {
              onrendered:function(canvas) {

                  //返回图片dataURL，参数：图片格式和清晰度(0-1)
                  var pageData = canvas.toDataURL('image/jpeg', 1.0);

                  //方向默认竖直，尺寸ponits，格式a4[595.28,841.89]
                  var pdf = new jsPDF('', 'pt', 'a4');

                  //addImage后两个参数控制添加图片的尺寸，此处将页面高度按照a4纸宽高比列进行压缩
                  pdf.addImage(pageData, 'JPEG', 40, 60, canvas.width*0.6, canvas.height*0.6 );
				
                  pdf.save('signinfo.pdf');

              }
          })
          
          
        html2canvas($("#downloadPdfF"), {
              onrendered:function(canvas) {

                  //返回图片dataURL，参数：图片格式和清晰度(0-1)
                  var pageData = canvas.toDataURL('image/jpeg', 1.0);

                  //方向默认竖直，尺寸ponits，格式a4[595.28,841.89]
                  var pdf = new jsPDF('', 'pt', 'a4');

                  //addImage后两个参数控制添加图片的尺寸，此处将页面高度按照a4纸宽高比列进行压缩
                  pdf.addImage(pageData, 'JPEG', 40, 60, canvas.width*0.6, canvas.height*0.6 );
				
                  pdf.save('signinfo.pdf');

              }
          })
          
          
          
          
		$("#printDownload").css("display","none");
		
	}
	
	function printMe(){
		var startD = $('#txdate').text();
		cancel();
		
		setTimeout(function(){
			//給打印按鈕添加點擊事件
		
	     	//獲取所有要body中的內容，作為備份
			var oldhtml = $("body").prop("outerHTML");

			
			$("#printPdf").css("display","block");
			$("#printPdf").css("transform","scale(0.8, 0.8)");
			//獲取要打印部分的html
			var html = $("#printPdf").prop("outerHTML");
		    
	         
	 		$("#printPdfF").css("display","block");
			$("#printPdfF").css("transform","scale(0.8, 0.8)");
			//獲取要打印部分的html
			var htmlF = $("#printPdfF").prop("outerHTML");
	         
			//將打印的html賦值給body
 	       
 	        if (html.indexOf('img') !== -1) {
 	        	 $("body").html(html);  
 	       	}
	         
	 
 	        if(htmlF.indexOf('img') !== -1){
 	        	$("body").html(htmlF); 
	        }
				
	         //打印
	         window.print();
	
	         //同時把原來的html還原
	       	
	         $("body").html(oldhtml);
	         $('.pdf_link').each(function(){
	        	 $(this).html('<img src="images/pdf2.png" border="0" alt="" width="30px" height="30px">')
	         })
	         
	         $("#printPdf").css("display","none");
 	         $("#printPdfF").css("display","none");
	         
// 	         query(startD);
	                
		},500);

	
	}
	
	function cancel(){
		$(".posbox").css({
			"display" : "none"
		});
		$(".posPrint").css({
			"display" : "none"
		});
		$(".box_inv").css({
			"display" : "none"
		});
	}

</script> 
