//=================================================================================
// Trim field string
// 1. rid of space both in front and in back of the string
// 2. replace double space by single space
//=================================================================================
function trim( inputString ) {
   // only deal with string
   if( typeof inputString != "string" ) { return inputString; }
   
   var returnValue = inputString;

   var ch = returnValue.substring(0, 1);
   while (ch == " ") { // rid of space in front of the string
      returnValue = returnValue.substring(1, returnValue.length);
      ch = returnValue.substring(0, 1);
   }

   ch = returnValue.substring(returnValue.length-1, returnValue.length);
   while (ch == " ") { // rid of space in back of the string 
      returnValue = returnValue.substring(0, returnValue.length-1);
      ch = returnValue.substring(returnValue.length-1, returnValue.length);
   }

   while (returnValue.indexOf("  ") != -1) { // replace double space by single space
      returnValue = returnValue.substring(0, returnValue.indexOf("  ")) +
      	returnValue.substring(returnValue.indexOf("  ")+1, returnValue.length); 
   } 
   
//   while( returnValue.indexOf("\'") != -1 ) { // replace ' by  (holomorphic)
//      returnValue = returnValue.substring(0, returnValue.indexOf("\'")) + "" +
//      	returnValue.substring(returnValue.indexOf("\'")+1, returnValue.length); 
//   }
   
//   while( returnValue.indexOf("\"") != -1 ) { // replace " by  (holomorphic)
//      returnValue = returnValue.substring(0, returnValue.indexOf("\"")) + "" +
//      	returnValue.substring(returnValue.indexOf("\"")+1, returnValue.length); 
//   }
   return returnValue; // return the trimmed string
}
//============================
// check the field have value
//============================
function IsFill( strTitle, objField ) {
	var stringValue = trim( objField.value ); // 
	if ( stringValue.length == 0 ) {
		alert( strTitle + "欄位值未輸入!");
		objField.focus();
		return false;
	}
	else
	{
	return true;
	}
}
//==============
// 
//==============
function IsNumber( strTitle, objField ) {
	var Str = trim( objField.value );
	if ( Str.length == 0 ) {
		return true;
	}
	var i;
	var c;
	for( i = 0 ; i < Str.length; i ++ ) {
		c = Str.charCodeAt(i);
		if ( ( c < 48 ) || ( c > 57 ) ) {
			alert( strTitle + "應爲數字!" );
			objField.focus();
			return false;
		}
	}
	return true;
}
//==============
// 
//==============
function IsInteger( strTitle, objField ) {
	var Str = trim( objField.value );
	if ( Str.length == 0 ) {
		return;
	}
	var i;
	var c;
	for( i = 0 ; i < Str.length; i ++ ) {
		c = Str.charCodeAt(i);
		if ( ( c < 48 ) || ( c > 57 ) ) {
			alert( strTitle + "應爲數字!" );
			objField.focus();
			return;
		}
	}
	return;
}
//==============
// 
//==============
function IsMoney( strTitle, objField)
{
 var Str = trim( objField.value );
  if(Str.length == 0)
  {
   return;
  }
  else
  {
    vaild="0123456789";
    for(var i=0;i<Str.length;i++)
    { 
      temp =Str.substring(i, i+1);
      if(vaild.indexOf(temp)<0)
      {
       alert( strTitle + "金額應爲整數!");
		objField.focus();
		return false;
      }
    }
    if(Str.indexOf(0)==0)
    {
      alert( strTitle + "金額應爲整數!");
		objField.focus();
		return false;
    }
  }
}
//==============
// 
//==============
function IsEnglish(strTitle, objField)
{
  var Str = trim( objField.value );
  if(Str.length == 0)
  {
   return;
  }
  else
  {
   for(var i=0;i<Str.length;i++)
   {
     var code = parseInt(objField.value.charCodeAt(i));
     if ((code >= 65 && code <= 90) || (code >= 97 && code <= 122))
     {
     }
     else
     { 
       alert( strTitle + "只能爲英文字母!" );
       objField.focus();
       return;
     }
    }
  }
}
//==========================
// ^jg
//==========================
function IsUpper(strTitle, objField)
{
  var Str = trim( objField.value );
  if(Str.length == 0)
  {
   return true;
  }
  else
  {
	valid="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	for(var i=0;i<Str.length;i++)
	{
		temp = "" + Str.substring(i, i+1);
		if (valid.indexOf(temp)<0)
		{
		 alert( strTitle + "應為英文大寫!" );
         objField.focus();
         return false;
        }
    }
  }
}
//==========================
// ^pg
//==========================
function IsLower(strTitle, objField)
{
  var Str = trim( objField.value );
  if(Str.length == 0)
  {
   return true;
  }
  else
  {
	valid="abcdefghijklmnopqrstuvwxyz";
	for(var i=0;i<Str.length;i++)
	{
		temp = "" + Str.substring(i, i+1);
		if (valid.indexOf(temp)<0)
		{
		 alert( strTitle + "應為英文小寫!" );
         objField.focus();
         return false;
        }
    }
  }
}
//==========================
// 
//==========================
function IsEngOrNum(strTitle, objField)
{
  var Str = trim( objField.value );
  if(Str.length == 0)
  {
   return;
  }
  else
  {
    for(var i = 0; i < Str.length; i++)
	{
		var code = parseInt(objField.value.charCodeAt(i));

		if((code >= 48 && code <= 57) || (code >= 65 && code <= 90) || (code >= 97 && code <= 122))
		{
			
		}
		else
		{
			alert( strTitle + "只能為英文字母或數字!" );
            objField.focus();
            return;
		}
	}
  }
}
//==========================
// 
//==========================
function IsEMail(strTitle, objField)
{
  var Str = trim( objField.value );
  if(Str.length == 0)
  {
   return;
  }
  else
  {
   var filter=/^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
   if(filter.test(Str))
   {
   }
   else
   {
     alert( strTitle + "EMail格式不正確!" );
            objField.focus();
            return;
   }
  }
}
//==========================
// 
//==========================
function IsDigitalRange(strTitle,minPara,maxPara,objField)
{
  var Str = trim( objField.value );
  if(Str.length == 0)
  {
        return;
  }
  else
  {
		if(isNaN(Str))
		{
			alert( strTitle + "應爲數字!" );
			objField.focus();
			return false;
		}
		else
		{
			if(parseFloat(Str)<minPara)
			{
				alert( strTitle + "不可小於" +minPara);
				objField.focus();
				return false;
			}
			if(parseFloat(Str)>maxPara)
			{
				alert( strTitle + "不可大於"+maxPara );
				objField.focus();
				return false;
			}
		}
  }
}
//==========================
// 
//==========================
function IsChinese(strTitle, objField)
{
	var Str = trim( objField.value );
	if(Str.length == 0)
	{
			return true;
	}
	else
	{
		for(var i = 0; i < Str.length; i++)
		{
			if(Str.charCodeAt(i) >=32&&Str.charCodeAt(i)<=126)
			{
				alert( strTitle + "應爲中文!" );
				objField.focus();
				return false;	
			}
		}
	}
}
//==========================
// 
//==========================
function IsTaiwanID( strTitle, objID ) {

	var strTaiwanID = trim( objID.value ); // 
	
	if( strTaiwanID.length == 0 ) // 
		return;
	var codeList = [10,11,12,13,14,15,16,17,34,18,19,20,21,22,35,23,24,25,26,27,28,29,32,30,31,33];
	
	// 
	if( ! ( strTaiwanID.length == 10 || strTaiwanID.length == 11 ) ) {
		window.alert( strTitle + " Error" );
		objID.focus();
		return;
	}
	
	// 
	var c = strTaiwanID.toUpperCase().charCodeAt(0);
	if ( ( c < 65 ) || ( c > 92 ) ) {
		window.alert( strTitle + " Error" );
		objID.focus();
		return;
	}
	
	// 
	c = strTaiwanID.charCodeAt(1);
	if ( ( c < 49 ) || ( c > 50 ) ) {
		window.alert( strTitle + " Error" );
		objID.focus();
		return;
	}
	
	// 
	var i;
	for( i = 2 ; i < strTaiwanID.length; i ++ ) {
		c = strTaiwanID.charCodeAt(i);
		if ( ( c < 48 ) || ( c > 57 ) ) {
			window.alert( strTitle + " Error" );
			objID.focus();
			return;
		}
	}
	
	// 
	var codeOfFirstChar = codeList[strTaiwanID.toUpperCase().charCodeAt(0)-65];
	codeOfFirstChar = '' + codeOfFirstChar;
	// 
	codeOfFirstChar = parseInt( codeOfFirstChar.charAt(1) * 9 ) + parseInt( codeOfFirstChar.charAt(0) );
	// 
	codeOfFirstChar = codeOfFirstChar % 10;
	
	// 
	var sum = codeOfFirstChar ;
	for( i = 1 ;i <= 8 ; i++ ) {
		sum += parseInt( strTaiwanID.charAt(i) ) * ( 9 - i );
	}
	
	// 
	if( ( sum % 10 ) != ( 10 - parseInt( strTaiwanID.charAt(9) ) % 10 ) ) {
		window.alert( strTitle + " Error" );
		objID.focus();
		return;
	}
}
//====================
// 
//====================
function IsAlienID( strTitle, objID ) {
	
	var strForeignID = trim(objID.value); // 
	
	if( strForeignID.length==0 ) // 
		return ;

	var codeList = [10,11,12,13,14,15,16,17,34,18,19,20,21,22,35,23,24,25,26,27,28,29,32,30,31,33];

	// 
	if( ! ( strForeignID.length == 10 ) ) {
		window.alert( strTitle + " Error" );
		objID.focus();
		return;
	}
	
	// 
	var c = strForeignID.toUpperCase().charCodeAt(0);
	if ( ( c < 65 ) || ( c > 92 ) ) {
		window.alert( strTitle + " Error" );
		objID.focus();
		return;
	}
	
	// 
	c = strForeignID.toUpperCase().charCodeAt(1);
	if ( ( c < 65 ) || ( c > 68 ) ) {
		window.alert( strTitle + " Error" );
		objID.focus();
		return;
	}
	
	// 
	var i;
	for( i = 2 ; i < strForeignID.length; i ++ ) {
		c = strForeignID.charCodeAt(i);
		if ( ( c < 48 ) || ( c > 57 ) ){
			window.alert( strTitle + " Error" );
			objID.focus();
			return;
		}
	}
	
	// 
	var codeOfFirstChar = codeList[strForeignID.toUpperCase().charCodeAt(0)-65];
	codeOfFirstChar = '' + codeOfFirstChar;
	// 
	codeOfFirstChar = parseInt( codeOfFirstChar.charAt(1) * 9 ) + parseInt( codeOfFirstChar.charAt(0) );
	// 
	codeOfFirstChar = codeOfFirstChar % 10;
	
	// 
	var codeOfSecondChar = codeList[strForeignID.toUpperCase().charCodeAt(1)-65];
	codeOfSecondChar = ''+ codeOfSecondChar;
	// 
	codeOfSecondChar = parseInt( codeOfSecondChar.charAt(1) );
	
	// 
	var sum = codeOfFirstChar + codeOfSecondChar * 8;
	for( i = 2 ;i <= 8 ; i++ ){
		sum += parseInt( strForeignID.charAt(i) ) * ( 9 - i );
	}
	
	// 
	if( ( sum % 10 ) != ( 10 - parseInt( strForeignID.charAt(9) ) % 10 ) ) {
		window.alert( strTitle + " Error" );
		objID.focus();
		return;
	}
}
//============================
// 
//============================
function IsTSBForeignID(strTitle, objCompanyID){
	var Str = trim(objCompanyID.value); // 
	
	if( Str.length == 0 ) // 
		return;

	// 
	if( Str.length != 10 ) { 
		alert( strTitle + " Error" );
		objCompanyID.focus();
		return;
	}

	// 
	if( Str.charAt(0).toLowerCase()  >= 'a' && Str.charAt(0).toLowerCase() <= 'z' ) {
		// 
		if( Str.charAt(1).toLowerCase() >= 'a' && Str.charAt(1).toLowerCase() <= 'd') {
			// 
			for( var count = 2 ; count < 10 ; count++ ) {
				if( Str.charCodeAt( count ) < 48 || Str.charCodeAt( count ) > 57 ) {
					alert( strTitle + " Error" );
					objCompanyID.focus();
					return;
				}
			}
			// 
			IsAlienID( strTitle,objCompanyID ) ;
		}
		else {
			alert( strTitle + " Error" );
			objCompanyID.focus();
			return;
		}
	}
	else { // 
		for( var count = 0 ; count < 8 ; count++ ) { // 
			if( Str.charAt( count ) < '0' || Str.charAt( count ) > '9' ) {
				alert( strTitle + " Error" );
				objCompanyID.focus();
				return;
			}
		}
		// 
		if( Str.charAt(8).toLowerCase() < 'a' || Str.charAt(8).toLowerCase() > 'z' || 
			 Str.charAt(9).toLowerCase() < 'a' || Str.charAt(9).toLowerCase() > 'z' ) {
			alert( strTitle + " Error" );
			objCompanyID.focus();
			return;
		}
		Str = Str.substring(0,4) + '/' + Str.substring(4,6) + '/' + Str.substring(6,8);
		var lthdatestr = Str.length ;
		var tmpy = "";
		var tmpm = "";
		var tmpd = "";
		var status = 0;
		
		for( i = 0; i < lthdatestr; i++ ) {
			if( Str.charAt(i) == '/' ) {
				status++;
			}
			if( status > 2 ) {
				alert( strTitle + " Error" );
				objCompanyID.focus();
				return;
			}
			if( ( status == 0 ) && ( Str.charAt(i) != '/' ) ) {
				tmpy = tmpy + Str.charAt(i)
			}

			if( ( status == 1 ) && ( Str.charAt(i) != '/' ) ) {
				tmpm = tmpm + Str.charAt(i)
			}

			if((status==2) && (Str.charAt(i)!='/')){
				tmpd = tmpd + Str.charAt(i)
			}
		}
        year = new String(tmpy);
        month = new String(tmpm);
        day = new String(tmpd);

        if((tmpy.length!=4) || (tmpm.length>2) || (tmpd.length>2)){
            alert( strTitle + " Error" );
	        objCompanyID.focus();
            return;
        }

        if(!((1<=month) && (12>=month) && (31>=day) && (1<=day)) ){
            alert ( strTitle + " Error" );
	        objCompanyID.focus();
            return;
        }

       if(!((year % 4)==0 && !( ( year % 100 ) ==0 && ( year % 400 )!=0 ) ) && (month==2) && (day==29)){
           alert ( strTitle + " Error" );
	       objCompanyID.focus();
           return;
       }

       if((month<=7) && ((month % 2)==0) && (day>=31)){
           alert ( strTitle + " Error" );
	       objCompanyID.focus();
           return;
       }

       if((month>=8) && ((month % 2)==1) && (day>=31)){
           alert ( strTitle + " Error" );
	       objCompanyID.focus();
           return;
       }
       
       if((month==2) && (day==30)){
           alert ( strTitle + " Error" );
	       objCompanyID.focus();
           return;
       }
   	}
}
//==========================
//
//==========================
function IsChinaID( strTitle, objID ) {
	var Str = new String;
	Str = objID.value;
	if( Str.length == 0 )
		return;
	if( Str.length == 10 ) 
		IsForeignID( strTitle, objID );
	else {
		if( Str.charAt(0) != '9') {
			alert( strTitle + " Error" );
			objID.focus();
			return;
		} 
		else {
			if( Str.substring(1,3) <= '38' ) {
				Str = '20' + Str.substring(1,3) + '/' + Str.substring(3,5) + '/' + Str.substring(5,7);
			}
			else {
				Str = '19' + Str.substring(1,3) + '/' + Str.substring(3,5) + '/' + Str.substring(5,7);
			}
	  		
			var lthdatestr;
			lthdatestr= Str.length ;
			var tmpy="";
			var tmpm="";
			var tmpd="";
			var status;
			status=0;
			if( Str == "" ) {
				alert( strTitle + " Error" );
				objID.focus();
				return;
			}
	
			for( i=0; i<lthdatestr; i++ ) {
				if( Str.charAt(i)== '/' ) {
					status++;
				}
				if( status > 2 ) {
					alert( strTitle + " Error" );
					objID.focus();
					return;
				}
				if( ( status == 0 ) && ( Str.charAt(i)!='/' ) ) {
					tmpy = tmpy + Str.charAt(i)
				}
				if( ( status == 1 ) && ( Str.charAt(i)!='/' ) ) {
					tmpm = tmpm + Str.charAt(i)
				}
				if( ( status == 2 ) && ( Str.charAt(i)!='/' ) ) {
					tmpd = tmpd + Str.charAt(i)
				}
			}
	
			year = new String(tmpy);
			month = new String(tmpm);
			day = new String(tmpd);
	
			if( ( tmpy.length != 4 ) || ( tmpm.length > 2 ) || ( tmpd.length > 2 ) ) {
				alert( strTitle + " Error" );
				objID.focus();
				return;
			}

			if( ! ( ( 1 <= month ) && ( 12 >= month ) && ( 31 >= day ) && ( 1 <= day ) ) ) {
				alert( strTitle + " Error" );
				objID.focus();
				return;
			}

			if( ! ( ( year % 4 ) == 0 && ! ( ( year % 100 ) == 0 && ( year % 400 ) != 0 ) ) && ( month == 2 ) && ( day == 29 ) ) {
				alert( strTitle + " Error" );
				objID.focus();
				return;
			}

			if( ( month <= 7 ) && ( ( month % 2 ) == 0 ) && ( day >= 31 ) ) {
				alert( strTitle + " Error" );
				objID.focus();
				return;
			}

			if( ( month >= 8 ) && ( ( month % 2 ) == 1 ) && ( day >= 31 ) ) {
				alert( strTitle + " Error" );
				objID.focus();
				return;
			}

			if( ( month == 2 ) && ( day == 30 ) ) {
				alert( strTitle + " Error" );
				objID.focus();
				return;
			}
		}
	}
}
//======================
// 
//======================
function IsTaiwanCompanyID( strTitle, objCompanyID ){
   	var Str = new String;
   	var checkChar = new Array(8);
   	var checkSun = new Array(16)
   	var multipleNum = new Array(1,2,1,2,1,2,4,1);
   	var sun = 0;
   	Str = objCompanyID.value;
   	if(Str.length == 0)
			return true;
   	if( Str.length != 8 ){
			alert( strTitle + " Error" );
			objCompanyID.focus();
			return false;
		}
   	for( var count = 0 ; count < Str.length ; count++ ) {
       	if( Str.charAt(count) > '9' || Str.charAt(count) < '0' ) {
          	alert( strTitle + " Error" );
	      	objCompanyID.focus();
          	return false;
       	}
       	checkChar[count] = Str.charAt(count);
   	}
   	for( var count = 0 ; count < 8 ; count ++ ) {
      	var temp = new String;
      	temp = checkChar[count] * multipleNum[count];
      	if( temp.toString().length == 2 ) {
         	checkSun[count * 2] = temp.toString().charAt(0);
         	checkSun[count * 2 + 1] = temp.toString().charAt(1);
      	}
      	else {
         	checkSun[count * 2] = "0";
         	checkSun[count * 2 + 1] = temp.toString().charAt(0);
      	}
   	}
   	for( var count = 0 ; count < 16 ; count++ ) {
      	sun = sun + parseInt(checkSun[count]);
   	}
   	if( sun % 10 != 0 ) {
      	var temp = 4 * checkChar[6];
      	var sunNum = 0;
      	if( temp.toString().length == 2 ) {
         	sunNum = parseInt( temp.toString().charAt(0) );
         	sunNum = parseInt( sunNum ) + parseInt( temp.toString().charAt(1) );
      	}
      	else {
         	sunNum = sunNum + temp.toString().charAt(0);
      	}
      	if( sunNum.toString().length == 2 ) {
        	checkSun[12] = sunNum.toString().charAt(0);
        	checkSun[13] = sunNum.toString().charAt(1);
      	}
      	else {
        	checkSun[12] = '0';
        	checkSun[13] = sunNum.toString().charAt(0);
      	}
      	sun = 0;
      	for( var count = 0 ; count < 16 ; count++ ) {
         	sun = sun + parseInt( checkSun[count] );
      	}
      	if( sun % 10 != 0 ) {
          	alert( strTitle + " Error" );
	      	objCompanyID.focus();
          	return false;
      	}
   	}
   	return true;
}
//==========
// 
//==========
function IsDateValue(strDateValue ) {
	var blnDateOK = true;
	var intDateLength = strDateValue.length;
	
	if( intDateLength != 0 ) { // 
		var strYear = "";
		var strMonth = "";
		var strDay = "";
		var intStatus = 0;
		
		for( i = 0 ; i < intDateLength ; i++ ) {
			if( strDateValue.charAt( i ) == '/' ) {
				intStatus++;
				if( intStatus > 2 ) {
					blnDateOK = false;
					alert("blnDateOK == false");
					break;
				}
			}
			if( ( intStatus == 0 ) && ( strDateValue.charAt( i ) != '/' ) ) {
				strYear = strYear + strDateValue.charAt( i )
			}
			if( ( intStatus == 1 ) && ( strDateValue.charAt( i ) != '/' ) ) {
				strMonth = strMonth + strDateValue.charAt( i )
			}
			if( ( intStatus == 2 ) && ( strDateValue.charAt( i ) != '/' ) ) {
				strDay = strDay + strDateValue.charAt( i )
			}
		}
		if( blnDateOK != false ) {
			alert("blnDateOK != false");
			// 
    	  	if ( ( strYear.length != 4 ) || ( strMonth.length < 1 ) || ( strMonth.length > 2 ) ||
    	  		  ( strDay.length < 1 ) || ( strDay.length > 2 ) ) {
				blnDateOK = false;
	      }
  	      // 
    	  	if ( ( strYear.length != 4 ) || ( strMonth < 1 ) || ( strMonth > 12 ) || 
    	  		  ( strDay < 1 ) || ( strDay > 31 ) ) {
				blnDateOK = false;
	      }

    	  	if ( ! ( (strYear % 4) == 0 && !( (strYear % 100) != 0 && (strYear % 400) == 0 ) &&
						(strMonth == 2) && (strDay == 29) ) ) {
				blnDateOK = false;
	      }
		}
	}
	return blnDateOK;
	
}
//================
// 
//================
function IsDateField( strTitle, objDate )
{

	var blnDateOK = true;
	var strDateValue = trim( objDate.value );
	if ( strDateValue != "" ) {
		var intDateLength = strDateValue.length;
		var strYear = "";
		var strMonth = "";
		var strDay = "";
		var intStatus;
		intStatus = 0;
		for( i = 0 ; i < intDateLength ; i++ ) {
			if( strDateValue.charAt( i ) == '/' ) {
				intStatus++;
			}
			if ( intStatus > 2 ) {
				blnDateOK = false;
				break;
			}
			if ( ( intStatus==0 ) && ( strDateValue.charAt( i ) != '/' ) ) {
				strYear = strYear + strDateValue.charAt( i )
			}
			if ( ( intStatus==1 ) && ( strDateValue.charAt( i ) != '/' ) ) {
				strMonth = strMonth + strDateValue.charAt( i )
			}
			if ( ( intStatus==2 ) && ( strDateValue.charAt( i ) != '/' ) ) {
				strDay = strDay + strDateValue.charAt( i )
			}
		}
		if ( blnDateOK != false ) {			
			var strYear=new String( strYear );
      		var strMonth=new String( strMonth );
	      	var strDay=new String( strDay );
    	  	if ( ( strYear.length != 4 ) || ( strMonth.length > 2 ) || ( strDay.length > 2 ) ) {
				blnDateOK=false;
	      	}
    	  	if ( ! ( ( 1 <= strMonth ) && ( 12 >= strMonth ) && ( 31 >= strDay ) && ( 1 <= strDay ) ) ) {
				blnDateOK=false;
	      	}
    	  	if ( ! ( ( strYear % 4 ) == 0 && ! ( ( strYear % 100 ) == 0 && ( strYear % 400 ) != 0 ) ) && ( strMonth == 2 ) && ( strDay == 29 ) ) {
				blnDateOK=false;
	      	}
			if ( ( strMonth <= 7 ) && ( ( strMonth % 2 ) == 0 ) && ( strDay >= 31 ) ) {
				blnDateOK=false;
	      	}
    	  	if ( ( strMonth >= 8 ) && ( ( strMonth % 2 ) == 1 ) && ( strDay >= 31 ) ) {
				blnDateOK=false;
	      	}
    	  	if ( ( strMonth == 2 ) && ( strDay == 30 ) ) {
				blnDateOK=false;
	      	}
		}
	}
	else {
		//
		blnDateOK=true;
  	}
	
	//
	if ( blnDateOK == false ) {
		alert( strTitle + "日期錯誤!" );
		objDate.focus();
	}
	return blnDateOK;
}
//================
// 
//================
function IsDateFieldRequired( strTitle, objDate )
{

	IsDateField(strTitle, objDate);
}
//==============================================================
// 
// 
//==============================================================
function IsDateSection( strTitle, objStartDate, objEndDate )
{
	//
	if ( objStartDate.value == "" || objEndDate.value == "" ) {
		return true;
	}
	////////////////////
	if ( IsDateValue( strTitle + " BeginDate" , objStartDate ) == false ) {
		objStartDate.focus();
		return false;
	}
	else {
		if( IsDateValue( strTitle + " FinishDate" , objEndDate ) == false ) {
			objEndDate.focus();
			return false;
		}
	}
	//
	var strStartYear
	var strEndYear
	var strStartMonth
	var strEndMonth
	var strStartDay
	var strEndDay
	var strTmp
	var i;
	var strDate=new String();

	strDate=objStartDate.value;
	strStartYear=parseFloat( strDate.substring(0,4) );
	strTmp=strDate.substring(5);
	strStartMonth=parseFloat( strTmp.substring( 0 , strTmp.indexOf("/") ) );
	strStartDay=parseFloat( strTmp.substring( strTmp.indexOf("/") +1 ) );

	strDate=objEndDate.value;
	strEndYear=parseFloat( strDate.substring(0,4) );
	strTmp=strDate.substring(5);
	strEndMonth=parseFloat( strTmp.substring( 0,strTmp.indexOf("/") ) );
	strEndDay=parseFloat( strTmp.substring( strTmp.indexOf("/")+1 ) );
	//
	if ( ( strStartYear < strEndYear ) || ( ( strStartYear == strEndYear ) && ( strStartMonth < strEndMonth ) ) || ( ( strStartYear == strEndYear ) && ( strStartMonth == strEndMonth ) && ( strStartDay <= strEndDay ) ) ) {
		return true;
	}
	else {
		alert( strTitle + "BeginDate must early then finish date" );
		objEndDate.focus();
		return false;
	}
}

function IsDigital( strTitle, objValue ) {
	var Str = new String;
	Str = objValue.value;
	if ( Str.length==0 ) {
		return;
	}
	var i;
	var c;
	for( i = 0 ; i < Str.length; i ++ ) {
		c = Str.charCodeAt(i);
		if ( ( c < 48 ) || ( c > 57 ) ) {
			window.alert( strTitle + " Number only please" );
			objValue.focus();
			return;
		}
	}
	return;
}

function IsTSBNTBankAccount( strTitle, objValue ) {
	var Str = new String;
	Str = objValue.value;
	var temp=Str.length;
	if ( Str.length == 0 ) {
		return;
	}
	for( i = 0 ; i < Str.length; i ++ ) {
		c = Str.charCodeAt(i);
		if ( ( c < 48 ) || ( c > 57 ) ) {
			alert( strTitle + " Error" );
			objValue.focus();
			return;
		}
	}
	if ( temp == 14 ) {
		var sum;
		sum=Str.charAt(0) * 6 + Str.charAt(1) * 5 + Str.charAt(2) * 4 + Str.charAt(3) * 3 + Str.charAt(4) * 2 + Str.charAt(5) * 9 + Str.charAt(6) * 8 + Str.charAt(7) * 7 + Str.charAt(8) * 6 + Str.charAt(9) * 5 + Str.charAt(10) * 4 + Str.charAt(11) * 0 + Str.charAt(12) * 3 + Str.charAt(13) * 2;
		var IntMod=sum % 11;
		var StrRemainder = ( 11 - IntMod ).toString();
		var result = StrRemainder.charAt( StrRemainder.length - 1 );
		if( result == Str.charAt(11) ) {
			return;
		}
	}
	alert( strTitle + " Error" );
	objValue.focus();
	return;
}

function IsTSBForeignBankAccount( strTitle , objValue ) {
  	var tempStr = new String;
  	tempStr = objValue.value;
  	var lthstrValue;
  	lthstrValue=tempStr.length;
  	for( i = 0 ; i < lthstrValue ; i++ ) {
       	var c = tempStr.charAt(i);
		if( ! ( ( c >= "0" ) && ( c <= "9" ) ) ) {
			alert( strTitle + " Error" );
	    	objValue.focus();
        	return;
        }
  	}
  	if ( lthstrValue == 14 ) {
     	var sum;
     	//
     	sum = ( tempStr.charAt(0) * 3 ) % 10 + ( tempStr.charAt(1) * 7 ) % 10 + ( tempStr.charAt(2) * 1 ) % 10 + ( tempStr.charAt(3) * 3 ) % 10 + ( tempStr.charAt(4) * 7 ) % 10;
     	sum = sum + ( tempStr.charAt(5) * 1 ) % 10 + ( tempStr.charAt(6) * 3 ) % 10 + ( tempStr.charAt(7) * 7 ) % 10 + ( tempStr.charAt(8) * 1 ) % 10;
     	sum = sum + ( tempStr.charAt(9) * 3 ) % 10 + ( tempStr.charAt(10) * 7 ) % 10;
     	var check;
     	//
     	sum = sum % 10;
     	check = 10 - sum;
     	check = check % 10;
     	//
     	if( check != tempStr.charAt(11) ) {
			alert( strTitle + " Error" );
     		objValue.focus();
     		return;
     	}
  	}
  	else {
		alert( strTitle + " Error" );
     	objValue.focus();
     	return;
  	}
}

function IsTSBFNSBankAccount( strTitle, objValue ) {
  	//
  	a = new Array(10); 
  	a[0] = [0,0,0,0,0,0,0,0,0,0,0,0,0];
  	a[1] = [4,2,1,6,3,7,9,10,5,8,4,2,1];
  	a[2] = [8,4,2,1,6,3,7,9,10,5,8,4,2];
  	a[3] = [1,6,3,7,9,10,5,8,4,2,1,6,3];
  	a[4] = [5,8,4,2,1,6,3,7,9,10,5,8,4];
  	a[5] = [9,10,5,8,4,2,1,6,3,7,9,10,5];
  	a[6] = [2,1,6,3,7,9,10,5,8,4,2,1,6];
  	a[7] = [6,3,7,9,10,5,8,4,2,1,6,3,7];
  	a[8] = [10,5,8,4,2,1,6,3,7,9,10,5,8];
  	a[9] = [3,7,9,10,5,8,4,2,1,6,3,7,9];
  	var tempStr = new String;
  	tempStr = objValue.value;
  	var lthstrValue;
  	lthstrValue = tempStr.length;
  	for( i = 0 ; i < lthstrValue ; i ++ ) {
     	var c = tempStr.charAt(i);
		if ( ! ( ( c >= 0 ) && ( c <= 9 ) ) ) {
			alert( strTitle + " Error" );
	    	objValue.focus();
	    	return;
        }
  	}
  	for( i = 0 ; i < lthstrValue ; i ++ ) {
    	if( ( tempStr.charAt(i) != 0 ) || ( i >= lthstrValue - 14 ) ) {
    		tempStr = tempStr.substring( i,lthstrValue );
    		lthstrValue = tempStr.length;
    		break;
    	}
  	}
  	if ( lthstrValue == 14 ) {
    	var sum;
    	//
    	sum = a[tempStr.charAt(0)][0] + a[tempStr.charAt(1)][1] + a[tempStr.charAt(2)][2] + a[tempStr.charAt(3)][3] + a[tempStr.charAt(4)][4]
    	sum = sum + a[tempStr.charAt(5)][5] + a[tempStr.charAt(6)][6] + a[tempStr.charAt(7)][7] + a[tempStr.charAt(8)][8]
    	sum = sum + a[tempStr.charAt(9)][9] + a[tempStr.charAt(10)][10] + a[tempStr.charAt(11)][11] + a[tempStr.charAt(12)][12]
    	var check;
    	check = sum % 10;
    	//
    	if( ! ( check == tempStr.charAt(13) ) ) {
			alert( strTitle + " Error" );
    		objValue.focus();
    		return;
    	}
  	}
  	else {
		alert( strTitle + " Error" );
    	objValue.focus();
   		return;
  	}
}


//
function IsTSBTaiwanCompanyID( strTitle , objID  ){
	//
	if ( objID.value=="" ){
		return true;
	}
	
	if ( IsTaiwanCompanyID( strTitle,objID ) == true ){
		var intID = parseFloat( objID.value );
		if ( intID >= 206 ){
			return true;
		}
		else{
			alert( strTitle + " Error" );
			objID.focus(); 
			return false;
		}
	}
	else{
		return false;
	}
}

function None( strTitle , objID ) {
  // do nothing
}
