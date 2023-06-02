
function query(conditionTableID)
{
	var chk= document.all("chkIsLike");
	
	var tbl = document.all(conditionTableID);
	
	var txts = tbl.getElementsByTagName("input");
	var sels = tbl.getElementsByTagName("select");
	
	var sqlResult = " where 1=1 ";//放拼接好的sql語句
	var scriptResult = "";//放拼好的javascript
	
	
	if(chk.checked==true) //模糊查
	{
	
		scriptResult += " document.all('" + chk.id + "').checked=true; ";
		//遍歷所有的有infoforquery屬性的textbox
		var i=0;
		for(i=0;i<txts.length;i++)
		{
			if(txts[i].type=="text" && txts[i].infoforquery!=null && txts[i].value!="")
			{
				var strInfoForQuery = txts[i].infoforquery;
				/*
				var fieldType = txts[i].infoforquery.split("_")[0];
				var fieldName = txts[i].infoforquery.split("_")[1];
				*/
				
				var fieldType = txts[i].infoforquery.substring(0,txts[i].infoforquery.indexOf("_"));
				var fieldName = txts[i].infoforquery.substr(txts[i].infoforquery.indexOf("_")+1);
				//alert("fieldType:" + fieldType);
				//alert("fieldName:" + fieldName);
				
				if(fieldType=="STRING")
				{
					sqlResult+= " and " + fieldName + " like '%" + txts[i].value + "%' " ;
					scriptResult += " document.all('" + txts[i].id + "').value ='" + txts[i].value + "'; ";
				}
				else if(fieldType=="INTEGER")
				{
					sqlResult+= " and " + fieldName + " = " + txts[i].value ;
					scriptResult += " document.all('" + txts[i].id + "').value ='" + txts[i].value + "'; ";
				}
				else if(fieldType=="DATEFROM")
				{
					//sqlResult+= " and to_char(" + fieldName + ",'yyyy/mm/dd') >= to_date('" + txts[i].value + "','yyyy/mm/dd') ";
					sqlResult+= " and to_char(" + fieldName + ",'yyyy/mm/dd') >= '" + txts[i].value + "' ";
					scriptResult += " document.all('" + txts[i].id + "').value ='" + txts[i].value + "'; ";
				
				}
				else if(fieldType=="DATETO")
				{
					sqlResult+= " and to_char(" + fieldName + ",'yyyy/mm/dd') <= '" + txts[i].value  + "' ";
					scriptResult += " document.all('" + txts[i].id + "').value ='" + txts[i].value + "'; ";
				
				}
				else if(fieldType=="DATEFROMS")
				{
					sqlResult+= " and to_date(" + fieldName + ",'yyyy/mm/dd') >= to_date('" + txts[i].value + "','yyyy/mm/dd') ";
					scriptResult += " document.all('" + txts[i].id + "').value ='" + txts[i].value + "'; ";
				
				}
				else if(fieldType=="DATETOS")
				{
					sqlResult+= " and to_date(" + fieldName + ",'yyyy/mm/dd') <= to_date('" + txts[i].value  + "','yyyy/mm/dd') ";
					scriptResult += " document.all('" + txts[i].id + "').value ='" + txts[i].value + "'; ";
				
				}
				else if(fieldType=="DATEEND")
				{
					sqlResult+= " and to_char(" + fieldName + ",'yyyy/mm/dd') = '" + txts[i].value + "' ";
					scriptResult += " document.all('" + txts[i].id + "').value ='" + txts[i].value + "'; ";
				
				}
				else if(fieldType=="DATEENDS")
				{
					sqlResult+= " and to_date(" + fieldName + ",'yyyy/mm/dd') = to_date('" + txts[i].value  + "','yyyy/mm/dd') ";
					scriptResult += " document.all('" + txts[i].id + "').value ='" + txts[i].value + "'; ";
				
				}
			
			}
		
		}
	}
	else //精確查
	{
		scriptResult += " document.all('" + chk.id + "').checked=false; ";
		
		//遍歷所有的有infoforquery屬性的textbox
		var i=0;
		for(i=0;i<txts.length;i++)
		{
			if(txts[i].type=="text" && txts[i].infoforquery!=null && txts[i].value!="")
			{
				var strInfoForQuery = txts[i].infoforquery;
				/*
				var fieldType = txts[i].infoforquery.split("_")[0];
				var fieldName = txts[i].infoforquery.split("_")[1];
				*/
				var fieldType = txts[i].infoforquery.substring(0,txts[i].infoforquery.indexOf("_"));
				var fieldName = txts[i].infoforquery.substr(txts[i].infoforquery.indexOf("_")+1);
				
				
				if(fieldType=="STRING")
				{
					sqlResult+= " and " + fieldName + " = '" + txts[i].value + "' " ;
					scriptResult += " document.all('" + txts[i].id + "').value ='" + txts[i].value + "'; ";
				}
				else if(fieldType=="INTEGER")
				{
					sqlResult+= " and " + fieldName + " = " + txts[i].value ;
					scriptResult += " document.all('" + txts[i].id + "').value ='" + txts[i].value + "'; ";
				}
				else if(fieldType=="DATEFROM")
				{
					sqlResult+= " and to_char(" + fieldName + ",'yyyy/mm/dd') >= '" + txts[i].value + "' ";
					scriptResult += " document.all('" + txts[i].id + "').value ='" + txts[i].value + "'; ";
				
				}
				else if(fieldType=="DATETO")
				{
					sqlResult+= " and to_char(" + fieldName + ",'yyyy/mm/dd') <= '" + txts[i].value  + "' ";
					scriptResult += " document.all('" + txts[i].id + "').value ='" + txts[i].value + "'; ";
				
				}
				else if(fieldType=="DATEFROMS")
				{
					sqlResult+= " and to_date(" + fieldName + ",'yyyy/mm/dd') >= to_date('" + txts[i].value + "','yyyy/mm/dd') ";
					scriptResult += " document.all('" + txts[i].id + "').value ='" + txts[i].value + "'; ";
				
				}
				else if(fieldType=="DATETOS")
				{
					sqlResult+= " and to_date(" + fieldName + ",'yyyy/mm/dd') <= to_date('" + txts[i].value  + "','yyyy/mm/dd') ";
					scriptResult += " document.all('" + txts[i].id + "').value ='" + txts[i].value + "'; ";
				
				}
				else if(fieldType=="DATEEND")
				{
					sqlResult+= " and to_char(" + fieldName + ",'yyyy/mm/dd') = '" + txts[i].value + "' ";
					scriptResult += " document.all('" + txts[i].id + "').value ='" + txts[i].value + "'; ";
				
				}
				else if(fieldType=="DATEENDS")
				{
					sqlResult+= " and to_date(" + fieldName + ",'yyyy/mm/dd') = to_date('" + txts[i].value  + "','yyyy/mm/dd') ";
					scriptResult += " document.all('" + txts[i].id + "').value ='" + txts[i].value + "'; ";
				
				}
			
			}
		
		}
	
	
	}
	
	////遍歷所有的有infoforquery屬性的checkbox,不論是精確查還是模糊查,對於checkbox而言,都是"="
	var j =0;
	for(j=0;j<sels.length;j++)
	{
		if(sels[j].infoforquery!=null && sels[j].value!="")
		{
			var strInfoForQuery = sels[j].infoforquery;
			/*
			var fieldType = sels[j].infoforquery.split("_")[0];
			var fieldName = sels[j].infoforquery.split("_")[1];
			*/
			var fieldType = sels[j].infoforquery.substring(0,sels[j].infoforquery.indexOf("_"));
			var fieldName = sels[j].infoforquery.substr(sels[j].infoforquery.indexOf("_")+1);
			//alert("fieldType:" + fieldType);
			//alert("fieldName:" + fieldName);
			
			if(fieldType=="STRING")
			{
				sqlResult+= " and " + fieldName + " = '" + sels[j].value + "' " ;
				scriptResult += " document.all('" + sels[j].id + "').selectedIndex =getSelIndexByValue('" + sels[j].id + "','" + sels[j].value + "'); ";
			}
			else if(fieldType=="INTEGER")
			{
				sqlResult+= " and " + fieldName + " = " + sels[j].value ;
				scriptResult += " document.all('" + sels[j].id + "').selectedIndex =getSelIndexByValue('" + sels[j].id + "','" + sels[j].value + "'); ";
			}
		
		}
	
	}
	
	document.all("hiddenScriptResult").value=scriptResult;
	document.all("hiddenSqlResult").value=sqlResult;



}
/***************************************************************************************************************/

function querynochk(conditionTableID)
{
	//var chk= document.all("chkIsLike");
	var tbl = document.all(conditionTableID);
	var txts = tbl.getElementsByTagName("input");
	var sels = tbl.getElementsByTagName("select");
	var sqlResult = " where 1=1 ";//放拼接好的sql語句
	var scriptResult = "";//放拼好的javascript
	//精確查
	//scriptResult += " document.all('" + chk.id + "').checked=false; ";
	//遍歷所有的有infoforquery屬性的textbox
	var i=0;
	for(i=0;i<txts.length;i++)
	{
		if(txts[i].type=="text" && txts[i].infoforquery!=null && txts[i].value!="")
		{
			var strInfoForQuery = txts[i].infoforquery;
			/*
			var fieldType = txts[i].infoforquery.split("_")[0];
			var fieldName = txts[i].infoforquery.split("_")[1];
			*/
			var fieldType = txts[i].infoforquery.substring(0,txts[i].infoforquery.indexOf("_"));
			var fieldName = txts[i].infoforquery.substr(txts[i].infoforquery.indexOf("_")+1);
				
			if(fieldType=="STRING")
			{
				sqlResult+= " and " + fieldName + " = '" + txts[i].value + "' " ;
				scriptResult += " document.all('" + txts[i].id + "').value ='" + txts[i].value + "'; ";
			}
			else if(fieldType=="INTEGER")
			{
				sqlResult+= " and " + fieldName + " = " + txts[i].value ;
				scriptResult += " document.all('" + txts[i].id + "').value ='" + txts[i].value + "'; ";
			}
			else if(fieldType=="DATEFROM")
			{
				sqlResult+= " and to_char(" + fieldName + ",'yyyy/mm/dd') >= '" + txts[i].value + "' ";
				scriptResult += " document.all('" + txts[i].id + "').value ='" + txts[i].value + "'; ";
			}
			else if(fieldType=="DATETO")
			{
				sqlResult+= " and to_char(" + fieldName + ",'yyyy/mm/dd') <= '" + txts[i].value  + "' ";
				scriptResult += " document.all('" + txts[i].id + "').value ='" + txts[i].value + "'; ";
			}
			else if(fieldType=="DATEFROMS")
			{
				sqlResult+= " and to_date(" + fieldName + ",'yyyy/mm/dd') >= to_date('" + txts[i].value + "','yyyy/mm/dd')";
				scriptResult += " document.all('" + txts[i].id + "').value ='" + txts[i].value + "'; ";
			}
			else if(fieldType=="DATETOS")
			{
				sqlResult+= " and to_date(" + fieldName + ",'yyyy/mm/dd') <= to_date('" + txts[i].value  + "','yyyy/mm/dd')";
				scriptResult += " document.all('" + txts[i].id + "').value ='" + txts[i].value + "'; ";
			}
			else if(fieldType=="DATEEND")
			{
				sqlResult+= " and to_char(" + fieldName + ",'yyyy/mm/dd') = '" + txts[i].value + "' ";
				scriptResult += " document.all('" + txts[i].id + "').value ='" + txts[i].value + "'; ";
			
			}
			else if(fieldType=="DATEENDS")
			{
				sqlResult+= " and to_date(" + fieldName + ",'yyyy/mm/dd') = to_date('" + txts[i].value  + "','yyyy/mm/dd') ";
				scriptResult += " document.all('" + txts[i].id + "').value ='" + txts[i].value + "'; ";
			
			}
		}
		
	}
	////遍歷所有的有infoforquery屬性的checkbox,不論是精確查還是模糊查,對於checkbox而言,都是"="
	var j =0;
	for(j=0;j<sels.length;j++)
	{
		if(sels[j].infoforquery!=null && sels[j].value!="")
		{
			var strInfoForQuery = sels[j].infoforquery;
			/*
			var fieldType = sels[j].infoforquery.split("_")[0];
			var fieldName = sels[j].infoforquery.split("_")[1];
			*/
			var fieldType = sels[j].infoforquery.substring(0,sels[j].infoforquery.indexOf("_"));
			var fieldName = sels[j].infoforquery.substr(sels[j].infoforquery.indexOf("_")+1);
			//alert("fieldType:" + fieldType);
			//alert("fieldName:" + fieldName);
			
			if(fieldType=="STRING")
			{
				sqlResult+= " and " + fieldName + " = '" + sels[j].value + "' " ;
				scriptResult += " document.all('" + sels[j].id + "').selectedIndex =getSelIndexByValue('" + sels[j].id + "','" + sels[j].value + "'); ";
			}
			else if(fieldType=="INTEGER")
			{
				sqlResult+= " and " + fieldName + " = " + sels[j].value ;
				scriptResult += " document.all('" + sels[j].id + "').selectedIndex =getSelIndexByValue('" + sels[j].id + "','" + sels[j].value + "'); ";
			}
		
		}
	
	}
	document.all("hiddenScriptResult").value=scriptResult;
	document.all("hiddenSqlResult").value=sqlResult;
}


/***************************************************************************************************************/
//根據value找到是第幾項被選中,而不實單純的按照上次是第幾項被選中就說這次是第幾項被選中.
function getSelIndexByValue(selectId,selectValue)
{
	var sel = document.all(selectId);
	var i ;
	for(i=0;i<sel.options.length;i++)
	{
		var opt = sel.options[i];
		if(opt.value == selectValue)
		{
			return i;
		}
	
	}
}




//第一位:add,第二位:delete,第三位:modify
//比如110意味著有add,delete權限,沒有modify權限
function getRightCodeByFuncID(funcID)
{
	var hid=document.all("hiddenOptRight").value;
	var hidVals = hid.split(",");
	var i =0 ;
	for(i=0;i<hidVals.length;i++)
	{
		var oneFuncID=hidVals[i].split(":")[0];
		var oneRightCode=hidVals[i].split(":")[1];
		if(oneFuncID==funcID)
		{
			return oneRightCode;
		}
	
	}

}

function canAdd(funcID)
{
	var rightCode = getRightCodeByFuncID(funcID);
	if(rightCode.charAt(0)=="1")
	{
		return true;
	}
	else
	{
		return false;
	}

}
function canDelete(funcID)
{
	var rightCode = getRightCodeByFuncID(funcID);
	if(rightCode.charAt(1)=="1")
	{
		return true;
	}
	else
	{
		return false;
	}
}

function canModify(funcID)
{
	var rightCode = getRightCodeByFuncID(funcID);
	if(rightCode.charAt(2)=="1")
	{
		return true;
	}
	else
	{
		return false;
	}
}

function setBtnHiddenByRight(funcID)
{
	var btn_Add=document.all("btnAdd");
	if(btn_Add!=null)
	{
		if(!canAdd(funcID))
		{
			btn_Add.style.visibility="hidden";
		}
	}
	
	var btn_Delete=document.all("btnDelete");
	if(btn_Delete!=null)
	{
		if(!canDelete(funcID))
		{
			btn_Delete.style.visibility="hidden";
		}
	}
	
	var btn_Recover=document.all("btnRecover");
	if(btn_Recover!=null)
	{
		//如果有刪除權限,則有恢復權限
		if(!canDelete(funcID))
		{
			btn_Recover.style.visibility="hidden";
		}
	}
	
	
	var btn_Modify=document.all("btnModify");
	if(btn_Modify!=null)
	{
		if(!canModify(funcID))
		{
			btn_Modify.style.visibility="hidden";
		}
	}

}

//有五種狀態:查看未刪除,查看已刪除,新增,刪除,修改
//這裡會用到一個hidden:hiddenOptStatus
//這個函數的功能是把該顯示的按鈕顯示出來,(默認是全都不顯示)
function setBtnNotHiddenByStatus()
{
	//可以為如下五類:VIEWNORMAL,VIEWDELETE,ADD,DELETE,MODIFY(必須為大寫)
	var optStatus = document.all("hiddenOptStatus").value;
	
	var btn_OK=document.all("btnOK");
	var btn_Delete = document.all("btnDelete");
	var btn_Recover = document.all("btnRecover");
	var btn_Modify = document.all("btnModify");
	var btn_Return = document.all("btnReturn");
	
	
	//任何狀態下,btn_Return都是可見的.
	btn_Return.style.visibility="";
	
	if(optStatus=="VIEWNORMAL") //可見按鈕有刪除,修改,返回
	{
		btn_Delete.style.visibility="";
		btn_Modify.style.visibility="";
	}
	else if(optStatus=="VIEWDELETE") //可見按鈕有恢復,返回
	{
		btn_Recover.style.visibility="";
		
	}
	else if(optStatus=="ADD") //可見按鈕由確定 ,返回
	{
		btn_OK.style.visibility="";
	}
	else if(optStatus=="DELETE") //可見按鈕刪除,返回
	{
		btn_Delete.style.visibility="";
	}
	else if(optStatus=="MODIFY")//可見按鈕確定,返回
	{
		btn_OK.style.visibility="";
	}

}

// set Disable of Detail頁面上除了 確定 ,刪除, 恢復, 修改, 返回 這五個按鈕之外的所有Elements
function setDisableAllElementsInDetailPage(trueOrFalse)
{
	var i=0;
	var inputs = document.getElementsByTagName("input");
	for(i=0;i<inputs.length;i++)
	{
		if(inputs[i].id!="btnOK" && inputs[i].id!="btnDelete" && inputs[i].id!="btnRecover" && inputs[i].id!="btnModify" && inputs[i].id!="btnReturn" )
		{
			if(inputs[i].type!="hidden")
			{
				inputs[i].disabled=trueOrFalse;
			}
		}
	
	}
	
	var selects =document.getElementsByTagName("select");
	for(i=0;i<selects.length;i++)
	{
		selects[i].disabled=trueOrFalse;
	}
	
	var textareas = document.getElementsByTagName("textarea");
	for(i=0;i<textareas.length;i++)
	{
		textareas[i].disabled=trueOrFalse;
	}

}


//以下方法供外部調用,以確定頁面上按鈕和輸入框的狀態

function setDetailPageElementsStatus(funcID)
{
	//RhettJiang,先把所有5個的button的style.visibility設成false;
	document.all("btnOK").style.visibility="hidden";
	document.all("btnDelete").style.visibility="hidden";
	document.all("btnRecover").style.visibility="hidden";
	document.all("btnModify").style.visibility="hidden";
	document.all("btnReturn").style.visibility="hidden";
	
	
	setBtnNotHiddenByStatus();
	setBtnHiddenByRight(funcID);
	
	//可以為如下五類:VIEWNORMAL,VIEWDELETE,ADD,DELETE,MODIFY(必須為大寫)
	var optStatus = document.all("hiddenOptStatus").value;
	if (optStatus=="VIEWNORMAL" || optStatus=="VIEWDELETE" || optStatus=="DELETE" )
	{
		setDisableAllElementsInDetailPage(true);
	}
	else
	{
		setDisableAllElementsInDetailPage(false);
	}
	//setHiddenBtnWidthZero();
}

//一下這個方法把所有的為hidden的button的width置成0
function setHiddenBtnWidthZero()
{
	var i=0;
	var btns = document.getElementsByTagName("input");
	for(i=0;i<btns.length;i++)
	{
		if(btns[i].type.toLowerCase()=="button" && btns[i].style.visibility=="hidden")
		{
			btns[i].style.width=0;
			btns[i].style.height=0;
		}	
	}	
	
}
