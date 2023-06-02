//Harry Mao 2006/07/23
//AIM:簡化頁面防呆部分的操作
//HOW TO USE :
//1:首先引用這個JS文件到頁面中
//<?script language=javascript src="http://image2.sina.com.cn/unipro/pub/getclientinfo_sinahome.js"></?script>
//③把連接地址改成實際地址。
//2:在畫面的最後加入:<?script language='javascript'>BindEachTextBoxOnBlurEventForCheck();</?script>
//3:在提交按鈕上增加attribute(first page load的時候):btnAdd.attribute.add("onclick","return checkAll();");
//4:畫面上的控件在命名ID的時候請按照如下規則:CHN_[XXX] ,
//"_"前面可以是以下[CHN,EML,NUM,DTN,PHO,MOB,URL,IDC,CUR,ZIP,DOU,ENG......]的任意組合(任意數量、任意順序),代表的涵義分別是
//[中文,EMAIL,數字,日期,電話,移動電話,URL,身份證號碼,貨幣,郵編,小數,英文].當是多個組合的時候,是說這格txtBox允許輸入的類型必須是幾種中的一種。
//5:所有的textBox,Select都需要放在table裏面,而且要保證結構是同一行的前一格中文，比如“姓名”,如果這個textbox是必須填寫的，則要寫成“姓名*”，只要包含“*”
//就可以，不限制"*"是怎樣修飾的。
//
//
//特殊變量以便確定到底是哪一個符號來確定當前字段是否必填。
var mustInputSymbol = "*";
//這個變量是要記載SpecialCheck中產生的多餘信息。
var strRhettJiangWantToSay = "";

function checkAll(obj)
{
    //ь清除所有的空格
    var txtSForClear = document.getElementsByTagName('input');
    for(var iForClear = 0;iForClear < txtSForClear.length;iForClear++)
    {
    	if(txtSForClear[iForClear].type.toLowerCase()=='text' )
    	{
    		txtSForClear[iForClear].value = ltrim(rtrim(txtSForClear[iForClear].value));
    	}
    	
    }
    
    
    if(obj == null)
    {
    	var i;
	//需要遍歷所有的textBOX 和 dropDownList,這次遍歷主要是查看哪些是不允許為空的字段.
	//首先遍歷所有的text
	var txtS = document.getElementsByTagName('input');
	
	for(i=0;i<txtS.length;i++)
	{	
		
		if(txtS[i].type.toLowerCase()=='text' && txtS[i].parentElement.parentElement.cells[txtS[i].parentElement.cellIndex-1]!=null)
		{
			//判斷這個textBox所有的單元格的前一個單元格是否包含mustInputSymbol,比如"*",如果包含，我們就認?這個textBox不允許為空.
			
			
			if(txtS[i].parentElement.parentElement.cells[txtS[i].parentElement.cellIndex-1].innerText.indexOf(mustInputSymbol)>=0)
			{
				//�蝳p果是必須有值的話
				if(trim(txtS[i].value)=='')
				{
					txtS[i].focus();
					alert(txtS[i].parentElement.parentElement.cells[txtS[i].parentElement.cellIndex-1].innerText.replace(mustInputSymbol,'').replace(":","").replace(":","").replace(" ","") + '不能夠為空!');
					
					changeOneCellCharacterColor(txtS[i].parentElement.parentElement.cells[txtS[i].parentElement.cellIndex-1]);
					return false;
				}
				else
				{
					
					
				}
				
			}
			
			//接下來判斷是否是指定的格式
			
			if(checkAll(txtS[i]))
			{
				//return true;	
			}
			else
			{
				txtS[i].focus();
				changeOneCellCharacterColor(txtS[i].parentElement.parentElement.cells[txtS[i].parentElement.cellIndex-1]);
				return false;	
			}
			
			
		
		}
		
		
	}
	
	
	//�輓M後遍歷所有的select
	var selS = document.getElementsByTagName('select');
	
	for(i=0;i<selS.length;i++)
	{
		//用於防止頁面上出現Table的第一個單元就放Select的情況
		if(selS[i].parentElement.parentElement.cells[selS[i].parentElement.cellIndex-1]!=null)
		{
			//判斷這個textbox所在單元格的前一個單元格是否包含MustInputSymbol,比如“*”，如果包含，我們就認定需要這個textbox不允許為空。
			if(selS[i].parentElement.parentElement.cells[selS[i].parentElement.cellIndex-1].innerText.indexOf(mustInputSymbol)>=0)
			{
				//如果是必填值的話
				if(trim(selS[i].value)=='')
				{
					selS[i].focus();
					alert(selS[i].parentElement.parentElement.cells[selS[i].parentElement.cellIndex-1].innerText.replace(mustInputSymbol,'').replace(":","").replace(":","").replace(" ","") + '不允許為空!');
					changeOneCellCharacterColor(selS[i].parentElement.parentElement.cells[selS[i].parentElement.cellIndex-1]);
					return false;
				}
				else
				{
					//return true;
				}
				
			}
		}
		
	}
	
	//在返回true之前，把紅色部分清理掉。（如果有紅色部分的話）
	changeBackOneCellCharacterColor();
	
	//待所有的都通過了，才return true;
	return true;
	
    }
    else
    {
    	//這裡判斷是否是用戶控件中的textbox，這種情況之下，在頁面上真正顯示出來的是NAME有："
	//所有以此作?判斷的依據.
	
	var realObjID;
	if(obj.name.indexOf(":")>0)
	{
		realObjID=obj.name.substring(obj.name.indexOf(":")+1);
	}
	else
	{
		//對於那些只有Name，沒有ID的也要做些處理
		if(obj.id=='')
		{
			realObjID=obj.name;
		}
		else
		{
			realObjID=obj.id;
		}
		
	}
    
	//只要Check指定的對象即可
	var strJudge = realObjID.split("_");
	
	
	
	if(checkOne(strJudge[0],obj.value,obj.parentElement.parentElement.cells[obj.parentElement.cellIndex-1].innerText.replace(mustInputSymbol,'').replace("ㄩ","").replace(":","").replace(" ",""),obj))
	{
	
		return true;
	}
	else
	{
		
		changeOneCellCharacterColor(obj.parentElement.parentElement.cells[obj.parentElement.cellIndex-1]);
		return false;
	}
	
    }
	
}

//這個function的功能是把指定的Table cell裏面的字改變色彩
function changeOneCellCharacterColor(objCell)
{
	/*
	changeBackOneCellCharacterColor();
	try
	{
		var objFont=document.createElement("<font id='changeColorFont' color='red'></font>");
		var objCharacter = document.createTextNode(objCell.innerText);
		objFont.appendChild(objCharacter);
		
		
		objCell.innerHTML="";
		objCell.appendChild(objFont);
	
	}
	catch(err)
	{
		alert(err.message);
	}
	*/
}


//把變成紅色的cell裏面字體變回來
function changeBackOneCellCharacterColor()
{	/*
	if(document.all('changeColorFont'))
	{
		var myCell = document.all('changeColorFont').parentElement;
		var myTxt = document.all('changeColorFont').innerText;
		
		myCell.innerHTML="";
		myCell.innerHTML=myTxt;
	}
	*/
}


function checkOne(txtType,txtValue,alertMeg,obj)
{
	//每一次新的檢查都需要把這個變量清除。
	strRhettJiangWantToSay = "";
	
	var i=0;
	var oneType='';
	var tmpTxtValue = txtValue;
	var strReg='';
	for(i=0;i<txtType.length;i=i+3)
	{
		oneType=txtType.substr(i,3);
		switch(oneType)
		{
			case "CHN":
				strReg=/^[\u0391-\uFFE5]+$/;
				break;
			case "EML":
				//strReg=/^[_a-z0-9]+@([_a-z0-9]+\.)+[a-z0-9]{2,3}$/;
				strReg=/^[A-Za-z0-9.]+@[A-Za-z0-9]+(.[A-Za-z0-9]+)*.[A-Za-z0-9]{2,3}$/ 
				break;
			case "NUM":
				strReg =  /\d+/g;
				break;	
			case "MUN": // MUN意思是NUM的反向，即居左排列，普通NUM是居右排列。
				strReg =  /\d+/g;
				break;	
			case "DTN":
				//date normal pattern 
				strReg = /^(\d{4})([\/,-])(\d{1,2})\2(\d{1,2})$/; 
				break;
			case "PHO":
				//phone
				strReg =/^((\(\d{3}\))|(\d{3}\-))?(\(0\d{2,3}\)|0\d{2,3}-)?[1-9]\d{6,7}$/;
				break;
			case "MOB":
				//mobile
				strReg =/^((\(\d{3}\))|(\d{3}\-))?13\d{9}$/;
				break;
			case "URL":
				strReg =/^http:\/\/[A-Za-z0-9]+\.[A-Za-z0-9]+[\/=\?%\-&_~`@[\]\':+!]*([^<>\"\"])*$/;
				break;
			case "IDC":
				//ID CARD
				strReg =  /^\d{15}(\d{2}[A-Za-z0-9])?$/;
				break;
			case "CUR":
				//金額有千分位
				strReg = /^(\d+\,?)+(\.\d+)?$/;
				break;
			case "ZIP":
				strReg = /^[1-9]\d{5}$/;
				break;
			case "DOU":
				//double
				strReg = /^[-\+]?\d+(\.\d+)?$/;
				break;
			case "ENG":
				strReg = /[A-Za-z]+/g;
				break;
			//case "AAA": //前四位英文，數字，或者中文，然後是“-”，接下來兩位是數字，再接下來兩位是數字或字母
				//strReg = /^[\u0391-\uFFE5\dA-Za-z]{4}-\d{2}[\dA-Za-z]{2}$/;
				//break;
			case "DE1": //加入Decimal的對於小數字數的判斷DE1意味小數位不能超過1位
				//Decimal(X,1)
				strReg = /^[-\+]?\d+(\.\d{1})?$/;
				break;
			case "DE2": //加入Decimal的對於小數字數的判斷DE2意味小數位不能超過2位
				//Decimal(X,2)
				strReg = /^[-\+]?\d+(\.\d{1,2})?$/;
				break;
			case "DE3": //加入Decimal的對於小數字數的判斷DE3意味小數位不能超過3位
				//Decimal(X,3)
				strReg = /^[-\+]?\d+(\.\d{1,3})?$/;
				break;
			case "DE4": //加入Decimal的對於小數字數的判斷DE4意味小數位不能超過4位
				//Decimal(X,4)
				strReg = /^[-\+]?\d+(\.\d{1,4})?$/;
				break;
			case "DE5": //加入Decimal的對於小數字數的判斷DE5意味小數位不能超過5位
				//Decimal(X,5)
				strReg = /^[-\+]?\d+(\.\d{1,5})?$/;
				break;
			case "DE6": //加入Decimal的對於小數字數的判斷DE6意味小數位不能超過6位
				//Decimal(X,6)
				strReg = /^[-\+]?\d+(\.\d{1,6})?$/;
				break;
			case "DE7": //加入Decimal的對於小數字數的判斷DE7意味小數位不能超過7位
				//Decimal(X,7)
				strReg = /^[-\+]?\d+(\.\d{1,7})?$/;
				break;
			case "DE8": //加入Decimal的對於小數字數的判斷DE8意味小數位不能超過8位
				//Decimal(X,8)
				strReg = /^[-\+]?\d+(\.\d{1,8})?$/;
				break;
			case "DE9": //加入Decimal的對於小數字數的判斷DE9意味小數位不能超過9位
				//Decimal(X,9)
				strReg = /^[-\+]?\d+(\.\d{1,9})?$/;
				break;
			case "CU0": //加入金額小數位數的判斷 
				//Currency 0小數位
				strReg = /^(\d+\,?)+$/;
				break;	
			case "CU1":  
				//Currency 1小數位
				strReg = /^(\d+\,?)+(\.\d{1})?$/;
				break;
			case "CU2":  
				//Currency 2小數位
				strReg = /^(\d+\,?)+(\.\d{1,2})?$/;
				break;
			case "CU3": 
				//Currency 3小數位
				strReg = /^(\d+\,?)+(\.\d{1,3})?$/;
				break;
			case "CU4":  
				//Currency 4小數位
				strReg = /^(\d+\,?)+(\.\d{1,4})?$/;
				break;
			case "CU5": 
				//Currency 5小數位
				strReg = /^(\d+\,?)+(\.\d{1,5})?$/;
				break;
			case "CU6":  
				//Currency 6小數位
				strReg = /^(\d+\,?)+(\.\d{1,6})?$/;
				break;
			case "CU7": //2005/7/3忳Stevenゐ楷,樓�踳蘤鍤﹋�弇杅腔瓚剿 
				//Currency 7小數位
				strReg = /^(\d+\,?)+(\.\d{1,7})?$/;
				break;
			case "CU8": 
				//Currency 8小數位
				strReg = /^(\d+\,?)+(\.\d{1,8})?$/;
				break;
			case "CU9":
				//Currency 9小數位
				strReg = /^(\d+\,?)+(\.\d{1,9})?$/;
				break;
				
				
			default:
			//出現任何不在列表?的標示，都認?當前textbox沒有違反約束
				tmpTxtValue='';
				break;
				

		}
		
		tmpTxtValue=tmpTxtValue.replace(strReg,"");
		
		//alert(tmpTxtValue);
		
				
	}
	if(tmpTxtValue=='')
	{
		//接下來對那些沒有辦法用正則表達式完全判斷的進行再次判斷
		
		if(specialCheck(txtType,txtValue,obj))
		{
			//還要判斷是否是終止日期DTT(means DateTo),如果是這種類型的話，需要寫成DTT001_UPDDATE這種形式，即後面跟三位數字
			//這樣的話，就可以找到其對應的DTF001_UPDDATE了(means DateFrom). 
			if(txtType.indexOf("DTT")>=0)//表明一個結束日期
			{
				var objDTF = document.all('DTF' + obj.id.substr(3));
				//alert(objDTF.value);
				//alert(obj.value);
				if(objDTF != null && trim(objDTF.value)!='' && trim(obj.value) != '' && (new Date(objDTF.value)>new Date(obj.value)))
				{
					var DTFText = objDTF.parentElement.parentElement.cells[objDTF.parentElement.cellIndex-1].innerText.replace(mustInputSymbol,'');
					var DTTText = obj.parentElement.parentElement.cells[obj.parentElement.cellIndex-1].innerText.replace(mustInputSymbol,'');
					//alert(DTTText.replace("：","").replace(":","").replace(" ","") + "截止日不能夠小於" + DTFText.replace("：","").replace(":","").replace(" ","")+"起始日");
					alert(DTFText.replace("：","").replace(":","").replace(" ","") + "(起)不能大於" + DTTText.replace("：","").replace(":","").replace(" ","")+"(迄)!");
					return false;
				}
				else
				{
					return true;
				}
			}
			else
			{
				return true;
			}
		}
		else
		{
			alert(alertMeg.replace("：","").replace(":","") + '必須為如下類型:' + getTypeCHNName(txtType));
		
			return false;
		}
		
	}
	else
	{
		alert(alertMeg.replace("：","").replace(":","") + '必須為如下類型:' + getTypeCHNName(txtType));
		
		return false;
	}

}

function getTypeCHNName(txtType)
{
	var i;
	var oneType;
	var strCHNName='';
	for(i=0;i<txtType.length;i=i+3)
	{
		oneType=txtType.substr(i,3);
		
		switch(oneType)
		{
			case "CHN":
				strCHNName+="中文 ";
				break;
			case "EML":
				strCHNName+="EMAIL ";
				break;
			case "NUM":
				strCHNName+="整數 " ;
				break;	
			case "MUN"://同NUM涵義相同，只是在排列上是居左排
				strCHNName+="整數 ";
				break;	
			case "DTN":
				strCHNName+="日期 ";
				break;	
			case "DTF":
				strCHNName+="日期 ";
				break;	
			case "DTT":
				strCHNName+="日期 ";
				break;	
			case "PHO":
				//phone
				strCHNName+="電話 ";
				break;
			case "MOB":
				//mobile
				strCHNName+="手機 ";
				break;
			case "URL":
				strCHNName+="URL ";
				break;
			case "IDC":
				//ID CARD
				strCHNName+="身份證號碼 ";
				break;
			case "CUR":
				//currency
				strCHNName+="貨幣 " ;
				break;
			case "ZIP":
				strCHNName+="郵編 ";
				break;
			case "DOU":
				//double
				strCHNName+="數字 ";
				break;
			case "ENG":
				strCHNName+="英文 ";
				break;
			case "DE1": //decimal(X,1)
				strCHNName+="小數且小數位不能超過1位 " ;
				break;	
			case "DE2": //decimal(X,2)
				strCHNName+="小數且小數位不能超過2位 ";
				break;	
			case "DE3": //decimal(X,3)
				strCHNName+="小數且小數位不能超過3位 ";
				break;	
			case "DE4": //decimal(X,4)
				strCHNName+="小數且小數位不能超過4位 ";
				break;	
			case "DE5": //decimal(X,5)
				strCHNName+="小數且小數位不能超過5位 ";
				break;	
			case "DE6": //decimal(X,6)
				strCHNName+="小數且小數位不能超過6位 ";
				break;	
			case "DE7": //decimal(X,7)
				strCHNName+="小數且小數位不能超過7位 ";
				break;	
			case "DE8": //decimal(X,8)
				strCHNName+="小數且小數位不能超過8位 ";
				break;	
			case "DE9": //decimal(X,9)
				strCHNName+="小數且小數位不能超過9位 ";
				break;	
			case "CU0": //currency 0小數位
				strCHNName+="金額且不帶小數位 ";
				break;
			case "CU1": //currency 1小數位
				strCHNName+="金額且小數位不能超過1位 ";
				break;
			case "CU2": //currency 2小數位
				strCHNName+="金額且小數位不能超過2位 ";
				break;
			case "CU3": //currency 3小數位
				strCHNName+="金額且小數位不能超過3位 ";
				break;
			case "CU4": //currency 4小數位
				strCHNName+="金額且小數位不能超過4位 ";
				break;
			case "CU5": //currency 5小數位
				strCHNName+="金額且小數位不能超過5位 ";
				break;
			case "CU6": //currency 6小數位
				strCHNName+="金額且小數位不能超過6位 ";
				break;
			case "CU7": //currency 7小數位
				strCHNName+="金額且小數位不能超過7位 ";
				break;
			case "CU8": //currency 8小數位
				strCHNName+="金額且小數位不能超過8位 ";
				break;
			case "CU9": //currency 9小數位
				strCHNName+="金額且小數位不能超過9位 ";
				break;
			
				
		}
	}
	
	
	var resultStr=strCHNName.substr(0,strCHNName.length-1);
	while(resultStr.indexOf(' ')>0)
	{
		resultStr=resultStr.replace(' ','或者');
	}
	
	resultStr += strRhettJiangWantToSay;
	return resultStr;

}

//供外部調用，幫定每個text的onblur事件
//每當失去焦點的時候觸發這個事件
//暫時不用這個方法，通過每次提交的時候，在調用checkAll()統一判斷
function BindTextBoxFormat()
{
	var allTxts = document.getElementsByTagName("input");
	var i;
	for(i=0;i<allTxts.length;i++)
	{
		
		
		if(allTxts[i].type.toLowerCase()=='text')
		{
			//對於NUM[數字],DOU[有小數字],CUR[貨幣],DE1~DE9,CU0~CU9這幾類textbox要把Align設置成right
			if(allTxts[i].name.indexOf("_")==3 && (allTxts[i].name.substr(0,3)=="NUM" || allTxts[i].name.substr(0,3)=="DOU" || allTxts[i].name.substr(0,3)=="CUR" || (allTxts[i].name.substr(0,3)>="CU0" && allTxts[i].name.substr(0,3)<="CU9") || (allTxts[i].name.substr(0,3)>="DE1" && allTxts[i].name.substr(0,3)<="DE9") ) )
			{
				allTxts[i].style.textAlign="right";	
			}
			
			//上面的邏輯結束后，需要加入新的判斷，判斷name的后兩位是否"_L"左 "_C"中 "_R"右
			if(allTxts[i].name.substr(allTxts[i].name.length-2)=="_L")
			{
				allTxts[i].style.textAlign="left";	
			}
			else if(allTxts[i].name.substr(allTxts[i].name.length-2)=="_C")
			{
				allTxts[i].style.textAlign="center";	
			}
			else if(allTxts[i].name.substr(allTxts[i].name.length-2)=="_R")
			{
				allTxts[i].style.textAlign="right";	
			}
			
			
			//如果是貨幣，需要加上千分號
			if(allTxts[i].name.indexOf("_")==3 && (allTxts[i].name.substr(0,3)=="CUR" || (allTxts[i].name.substr(0,3)>="CU0" && allTxts[i].name.substr(0,3)<="CU9") ))
			{
				
				allTxts[i].onfocus = new Function("removeMoneyComma(this);");
				allTxts[i].onblur= new Function("addDecimalCount(this);addMoneyComma(this);");
			}
			
			//如果是數字DE1--DE9，則要綁定自動補0的事件
			//特別注意的是NUM,DOU不需要補零動作發生.
			if(allTxts[i].name.indexOf("_")==3 && (allTxts[i].name.substr(0,3)>="DE1" && allTxts[i].name.substr(0,3)<="DE9"))
			{
				allTxts[i].onblur= new Function("addDecimalCount(this);");
			}
			
			
			
			//allTxts[i].onblur=new Function("if(checkAll(this)){return true}else{this.focus();return false;}");
		}
	}
	
	addAllCurrencyComma();
	addAllDecimalCount();

}


//在驗證畫面一切都正常的時候，Submit之前，需要把CUR_的textbox中的“，”.全部去掉
function clearAllCurrencyComma()
{
	var allTxts = document.getElementsByTagName("input");
	var i;
	for(i=0;i<allTxts.length;i++)
	{
		
		
		if(allTxts[i].type.toLowerCase()=='text')
		{
			
			//如果是貨幣，則需要把千分號在Submit之前全部去掉。
			if(allTxts[i].name.indexOf("_")==3 && (allTxts[i].name.substr(0,3)=="CUR" || (allTxts[i].name.substr(0,3)>="CU0" && allTxts[i].name.substr(0,3)<="CU9") ))
			{
				allTxts[i].value=trim(allTxts[i].value,",");
			}
			
			
			//allTxts[i].onblur=new Function("if(checkAll(this)){return true}else{this.focus();return false;}");
		}
	}
	
}

//在第一次load頁面的時候，需要帶出金額字段，所有金額字段需要加上",".
function addAllCurrencyComma()
{
	var allTxts = document.getElementsByTagName("input");
	var i;
	for(i=0;i<allTxts.length;i++)
	{
		
		
		if(allTxts[i].type.toLowerCase()=='text')
		{
			
			//如果是貨幣，需要把千分號在Submit之前全部去掉。
			if(allTxts[i].name.indexOf("_")==3 && (allTxts[i].name.substr(0,3)=="CUR" || (allTxts[i].name.substr(0,3)>="CU0" && allTxts[i].name.substr(0,3)<="CU9") ))
			{
				addMoneyComma(allTxts[i]);
			}
			
			
			//allTxts[i].onblur=new Function("if(checkAll(this)){return true}else{this.focus();return false;}");
		}
	}
	
}

//在第一次load頁面的時候，補齊小數位.
function addAllDecimalCount()
{
	var allTxts = document.getElementsByTagName("input");
	var i;
	for(i=0;i<allTxts.length;i++)
	{
		addDecimalCount(allTxts[i]);
	}
	
}


//給金額加千分位
function addMoneyComma(obj)
{	
	//這裡會補邏輯，以便在新增comma之前，把所有的違法字刪去
	obj.value=trim(obj.value,",");
	
	
	var frontStr="";
	var backStr="";
	if(obj.value.indexOf(".")!=-1)
	{
		frontStr = obj.value.substring(0,obj.value.indexOf("."));
		backStr = obj.value.substring(obj.value.indexOf(".")+1);
	}
	else
	{
		frontStr=obj.value;
		backStr="";	
	}
	
	var leftCharsCount = frontStr.length%3;
	//共被","分成幾段
	var totalFieldsCount = (frontStr.length-leftCharsCount)/3;
	
	
	var i=0;
	var newFrontStr="";
	
	newFrontStr += frontStr.substr(0,leftCharsCount);
	if(newFrontStr.length!=0)
	{
		newFrontStr += ",";	
	}
	
	for(i=0;i<totalFieldsCount;i++)
	{
		newFrontStr +=	frontStr.substr(i*3+leftCharsCount,3) + ",";
	}
	
	newFrontStr = newFrontStr.substr(0,newFrontStr.length-1);
	
	if(obj.value.indexOf(".")>=0)
	{
		obj.value = newFrontStr  + "." + backStr
	}
	else
	{
		obj.value = newFrontStr 
	}
	
}



//把金額的千分位去掉
function removeMoneyComma(obj)
{
	//因?要加千分位，所以需要把輸入限置在一定範圍
	//允許輸入大鍵盤，小鍵盤的0-9,backspace,"."
	//document.onkeydown=new Function("if(event.keyCode=8 || event.keyCode=110 || event.keyCode=190 || (event.keyCode>=96 && event.keyCode<=105) || (event.keyCode>=48 && event.keyCode<=57)){return true;} else {return false;}");
	obj.value=trim(obj.value,",");
	
	
}




function trim(str,notWantChar)
{
	if(notWantChar==null)
	{
		notWantChar=" ";	
	}
	var resultStr =str;
	while(resultStr.indexOf(notWantChar)>=0)
	{
		resultStr=resultStr.replace(notWantChar,'');	
	}
	return resultStr;
}

function ltrim(str)
{
	var tmpStr = str;
	while(tmpStr.charAt(0)==" ")
	{
		tmpStr = tmpStr.substr(1);
	}
	return tmpStr;
	
}
function rtrim(str)
{
	var tmpStr = str;
	while(tmpStr.charAt(tmpStr.length-1)==" ")
	{
		tmpStr = tmpStr.substr(0,tmpStr.length-1);
		
		
	}
	return tmpStr;
}

//要判斷是否是日期，還要把它變成正確形式，比如1982/13/1，javascript也認是正確日期，這時需要把它變成1983/1/1
function isDate(obj){
	var txtValue = obj.value;
	
	if(trim(txtValue)!='')
	{
		dDate = new Date(txtValue);
		
		if (dDate == "NaN") 
		{
			return false;
		}
		else
		{
			//obj.value=dDate.getFullYear()+"/"+(dDate.getMonth()+1)+"/"+dDate.getDate();
			formatDt(dDate,obj);
			return true;
		}
	}
	else
	{
		//如果是空字符串，則不作判斷，如果這個字段允許為空，不需要做校驗
		return true;	
	}
}
//這個方法是用來判斷一些無法用正則表達式完全確定是否正確的field Type:
function specialCheck(txtType,txtValue,obj)
{		
	//日期
	
	if(txtType.indexOf("DTN")>=0 || txtType.indexOf("DTF")>=0 || txtType.indexOf("DTT")>=0)
	{
		
		return isDate(obj);
	}
	
	//else if ......
	//�蝳p果有別的特殊判斷，加在後面
	if((txtType>="DE1" && txtType<="DE9")||(txtType>="CU0" && txtType<="CU9") || txtType == "CUR" || txtType == "NUM" || txtType == "DOU")
	{
		if(obj.MAXINTCOUNT!=null)//表明有定義最大整數數量
		{
			if(trim(txtValue," ")!="")
			{
				var s = new String(parseInt(trim(txtValue,",")));
				if(s.length>parseInt(obj.MAXINTCOUNT))
				{
					strRhettJiangWantToSay = "且整數位不能超過" + obj.MAXINTCOUNT + "位";
					return false;
				}
				else
				{
					strRhettJiangWantToSay = "且整數位不能超過" + obj.MAXINTCOUNT + "位";
					return true;
				}
			
			}
			
			
		}
		
	}
	
	//目的是增加功能，使全局防呆可以對那些指定位數的字段做正確的判斷。
	//這裡加的新標簽是:DEFINITELENGTH，如果有多個的話，用逗號格開，比如“10，12”意思是不是10就是12。逗號為英文逗號
	if(obj.DEFINITELENGTH!=null)
	{
		if(trim(txtValue," ")!="")
		{
			var lens = obj.DEFINITELENGTH.split(",");
			//newFrontStr = newFrontStr.substr(0,newFrontStr.length-1);
			var str = "";
			var i=0;
			for(i=0;i<lens.length;i++)
			{
				str += lens[i] + "位 ";	
			}
			
			if(str.length>0)
			{
				str = str.substr(0,str.length-1);	
			}
			
			while(str.indexOf(' ')>0)
			{
				str=str.replace(' ','或者');
			}
			
			
			var isValid = 0; //默認非有效
			for(i=0;i<lens.length;i++)
			{
				if(txtValue.length==parseInt(lens[i]))
				{
					isValid=1;
					break;
				}	
			}
			
			
			
			if(isValid==0)
			{
				strRhettJiangWantToSay = "且位數只能夠為" + str;
				return false;
			}
			else
			{
				strRhettJiangWantToSay = "且位數只能夠為" + str;
				return true;
			}
		
		}
		
	}
	
	
	
	
	
	return true;
	
}


function formatDt(dt,obj)
{
	
	var year=dt.getFullYear().toString();
	var month = (dt.getMonth()+1).toString();
	var dat = dt.getDate().toString();
	
	if(month.length==1)
	{
		month= "0" + month;
	}
	if(dat.length==1)
	{
		dat="0" + dat;
	}
	
	obj.value = year + "/" + month + "/" + dat;
}

//這個方法的作用是給DE1-DE9,CU1-CU9的相應小數補齊ょ
function addDecimalCount(obj)
{
    if(obj.name.indexOf("_")==3 && ((obj.name.substr(0,3)>="CU1" && obj.name.substr(0,3)<="CU9")  || (obj.name.substr(0,3)>="DE1" && obj.name.substr(0,3)<="DE9")))
    {
	if(trim(obj.value," ")!="")
	{
		//alert(obj.value);
		var cnt = parseInt(obj.name.substring(0,3).charAt(2));
			
		var firstPart = "";
		var secondPart = "";
			
		if(obj.value.indexOf(".")>=0)
		{
			firstPart = obj.value.substring(0,obj.value.indexOf("."));
			secondPart = obj.value.substring(obj.value.indexOf(".")+1,obj.value.length);
		}
		else
		{
			firstPart = obj.value;
			secondPart = "";
		}
			
		var newSecondPart = secondPart;
		while(newSecondPart.length<cnt)
		{
			newSecondPart += "0";
				
		}
		
		obj.value = firstPart + "." + newSecondPart;
			
	}
		
	
	
    }
	
}
