// check if user has the privilege to copy webpage (including printscreen), default is false.
//canCopy = isOpenPrintScreenFunction('<%= channel %>', '<%= authCode %>','<%= ROLE %>');
//alert(canCopy);

// check if user has the privilege to select text (including copy, paste), default is false.
//canSelect = isOpenSelectTextFunction('<%= channel %>', '<%= authCode %>','<%= ROLE %>');
//alert(canSelect);

hp_ok=true;
var sDisableCopyFunction_Text = '[SVC:權限限制使用複製、貼上功能]';
/*
	iLevel : 
		0	=> 全無
		1	=> can copy
		2	=> can select
		4	=> can mouse right key
		8	=> Mouse Drag
*/
var bEnableCopyFunction 	= false; //是否開放複製、貼上
var bEnableSelectFunction 	= false;
var bEnableMouseRightKey 	= false;
var bEnableMouseDrag		= false;
var bEnableFunction_KK		= false; //保留用

function ScreenProtectPage(iLevel) {

	/*Production Environment 
	bEnableCopyFunction 	= iLevel!=0 && !(iLevel%2 <1);
	bEnableSelectFunction 	= iLevel!=0 && !(iLevel%4 <2);
	bEnableMouseRightKey 	= iLevel!=0 && !(iLevel%8 <4);
	bEnableMouseDrag		= iLevel!=0 && !(iLevel%16<8);
	bEnableFunction_KK		= iLevel!=0 && !(iLevel%32<16);
	*/
	//Testing Environment
	bEnableCopyFunction     = iLevel!=1
	bEnableSelectFunction   = iLevel!=1
	bEnableMouseRightKey    = iLevel!=1
	bEnableMouseDrag        = iLevel!=1
	bEnableFunction_KK      = iLevel!=1
	

	onerror=hp_ne;
	
	if(!bEnableMouseRightKey){
		ScreenProtectPage_LockMouseRightKey();
	}	
	if(!bEnableSelectFunction){
		ScreenProtectPage_LockSelectFunction();
	}
	if(!bEnableMouseDrag){
		document.ondragstart=hp_ndd;
	}	
    if (!bEnableCopyFunction) {
		ScreenProtectPage_LockCopyFunction();
    }
    
	/*
	if (navigator.appName.indexOf('InternetExploter') != -1 && navigator.userAgent.indexOf('MSIE') == -1) {
		hp_ok = false;
	}
	if (navigator.userAgent.indexOf('Opera') != -1) {
		window.location='about:blank';
	}
	if(frames) {
		if(top.frames.length > 0) {
			top.location.href=self.location;
		}
	}
	*/
}

function ScreenProtectPage_LockCopyFunction(){
	if(navigator.appName.indexOf('InternetExplorer') == -1 || navigator.userAgent.indexOf('MSIE') != -1) {
		if (document.all && navigator.userAgent.indexOf('Opera') == -1) {
			//document.write('<div id="ID_ScreenProtectPage_LockCopyFunction" onBlur="bScreenProtectPage_BodyOnFocus=false;" onFocus="bScreenProtectPage_BodyOnFocus = true;" style="position:absolute; left:0px; top:0px; width:1000; height: 1000;"></div>');
			document.write('<div style=position:absolute;left:-1000px;top:-1000px><input type="textarea" name="hp_ta_0" value="' + sDisableCopyFunction_Text + '" style="visibility:hidden"></div>');
			document.write('<div style=position:absolute;left:-1000px;top:-1000px><input type="textarea" name="hp_ta_1" value="" style="visibility:hidden"></div>');
			//run interval			
			hp_dc();
		}			
	}
}		

function ScreenProtectPage_LockSelectFunction(){
    if (navigator.appName.indexOf('InternetExplorer') == -1 || navigor.userAgent.indexOf('MSIE') != -1) {
        if (document.all) {
            document.onselectstart = hp_dn;
        }
	}
}		
function ScreenProtectPage_LockMouseRightKey(){
    if (navigator.appName.indexOf('InternetExplorer') == -1 || navigor.userAgent.indexOf('MSIE') != -1) {
        if (document.all) {
           	document.oncontextmenu = hp_cm;
        }
        if (document.layers) {
            window.captureEvents(Event.MOUSEUP | Event.MOUSEDOWN);
            window.onmousedown = hp_md;
            window.onmouseup = hp_mu;
        }
        if (document.getElementById && !document.all) {
            document.oncontextmenu = hp_md;
            window.onmouseup = hp_mu;
            
            if (true) {
                window.onmousedown = hp_dn;
                document.oncontextmenu = hp_cm;
            }
        }
    }
    
    if (window.location.href.substring(0,4) == 'file') {
        window.location = 'about:blank';
    }
    
    //hp_nls();
    window.onbeforeprint=hp_dp1;
    window.onafterprint=hp_dp2;
}


function hp_d00(s) {
    if(!hp_ok) return;
    document.write(s);
}

hp_d00(unescape(""));

function hp_ne() {
    return true;
}

function hp_dn(a) {
    return false;
}

function hp_cm() {
    alert('本頁已被保護!');
    return false;
}

function hp_md(e) {
    if (e.which == 2 || e.which == 3) {
        alert('本頁已被保護!');
        return false;
    }
    if (e.which == 1) {
        window.captureEvents(Event.MOUSEMOVE);
        window.onmousemove=hp_dn;
    }
}

function hp_mu(e) {
    if (e.which == 1) {
        window.releaseEvents(Event.MOUSEMOVE);
        window.onmousemove=hp_dn;
    }
}

function hp_nls() {
    window.stats='';
    setTimeout('hp_nls()', 100);
}

function hp_dp1() {
    for (i = 0; i < document.all.length; i++) {
        if (document.all[i].style.visibility != 'hidden') {
            document.all[i].style.visibility = 'hidden';
            document.all[i].id = 'hp_id'
        }
    }
}

function hp_dp2() {
    for (i = 0; i < document.all.length; i++) {
        if (document.all[i].id == 'hp_id') {
            document.all[i].style.visibility = '';
        }
    }
}

var bScreenProtectPage_BodyOnFocus = true;
try{
	window.TABLE_TEMPLATE.onmouseover = function(){ bScreenProtectPage_BodyOnFocus=true; };
	window.TABLE_TEMPLATE.onmouseout = function(){ bScreenProtectPage_BodyOnFocus=false; };
	window.TABLE_TEMPLATE.onfocus = function(){ alert(123); };
}catch(e){
}	
function hp_dc() {
	var sClipboardData = window.clipboardData.getData( 'Text' );
	if(bScreenProtectPage_BodyOnFocus){
		if(sClipboardData != sDisableCopyFunction_Text){
			try{ 
				hp_ta_1.innerText = sClipboardData;
			}catch(e){ 
				hp_ta_1[0].innerText = sClipboardData; 
			}
		}

		try{ 
			hp_ta_0.createTextRange().execCommand('Copy');
		}catch(e){ 
			hp_ta_0[0].createTextRange().execCommand('Copy'); 
		}
		
		iScreenProtectPage_ReleaseClipboardData = 0;
	}else{
		ScreenProtectPage_ReleaseClipboardData();
	}

	//window.status = bScreenProtectPage_BodyOnFocus;// + event.clientX;// + ', event.clientY : ' + String.valueOf(event.clientY);
    setTimeout('hp_dc()', 100);
}

var iScreenProtectPage_ReleaseClipboardData = 0;
function ScreenProtectPage_ReleaseClipboardData(){
	if(!bEnableCopyFunction 
		&& iScreenProtectPage_ReleaseClipboardData<10){
		
		//var sClipboardData = window.clipboardData.getData( 'Text' );
		var sKeepText = '';
		try{ 
			sKeepText = hp_ta_1.innerText;
		}catch(e){ 
			sKeepText = hp_ta_1[0].innerText; 
		}

		if(sKeepText != sDisableCopyFunction_Text){
			try{ 
				hp_ta_1.createTextRange().execCommand('Copy');
			}catch(e){ 
				hp_ta_1[0].createTextRange().execCommand('Copy'); 
			}	
		}
		
		iScreenProtectPage_ReleaseClipboardData++;
	}
}

function hp_ndd() {
    return false;
}
