
function query(conditionTableID)
{
	var chk= document.all("chkIsLike");
	
	var tbl = document.all(conditionTableID);
	
	var txts = tbl.getElementsByTagName("input");
	var sels = tbl.getElementsByTagName("select");
	
	var sqlResult = " where 1=1 ";//������n��sql�y�y
	var scriptResult = "";//����n��javascript
	
	
	if(chk.checked==true) //�ҽk�d
	{
	
		scriptResult += " document.all('" + chk.id + "').checked=true; ";
		//�M���Ҧ�����infoforquery�ݩʪ�textbox
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
	else //��T�d
	{
		scriptResult += " document.all('" + chk.id + "').checked=false; ";
		
		//�M���Ҧ�����infoforquery�ݩʪ�textbox
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
	
	////�M���Ҧ�����infoforquery�ݩʪ�checkbox,���׬O��T�d�٬O�ҽk�d,���checkbox�Ө�,���O"="
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
	var sqlResult = " where 1=1 ";//������n��sql�y�y
	var scriptResult = "";//����n��javascript
	//��T�d
	//scriptResult += " document.all('" + chk.id + "').checked=false; ";
	//�M���Ҧ�����infoforquery�ݩʪ�textbox
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
	////�M���Ҧ�����infoforquery�ݩʪ�checkbox,���׬O��T�d�٬O�ҽk�d,���checkbox�Ө�,���O"="
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
//�ھ�value���O�ĴX���Q�襤,�Ӥ����ª����ӤW���O�ĴX���Q�襤�N���o���O�ĴX���Q�襤.
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




//�Ĥ@��:add,�ĤG��:delete,�ĤT��:modify
//��p110�N���ۦ�add,delete�v��,�S��modify�v��
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
		//�p�G���R���v��,�h����_�v��
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

//�����ت��A:�d�ݥ��R��,�d�ݤw�R��,�s�W,�R��,�ק�
//�o�̷|�Ψ�@��hidden:hiddenOptStatus
//�o�Ө�ƪ��\��O�����ܪ����s��ܥX��,(�q�{�O���������)
function setBtnNotHiddenByStatus()
{
	//�i�H���p�U����:VIEWNORMAL,VIEWDELETE,ADD,DELETE,MODIFY(�������j�g)
	var optStatus = document.all("hiddenOptStatus").value;
	
	var btn_OK=document.all("btnOK");
	var btn_Delete = document.all("btnDelete");
	var btn_Recover = document.all("btnRecover");
	var btn_Modify = document.all("btnModify");
	var btn_Return = document.all("btnReturn");
	
	
	//���󪬺A�U,btn_Return���O�i����.
	btn_Return.style.visibility="";
	
	if(optStatus=="VIEWNORMAL") //�i�����s���R��,�ק�,��^
	{
		btn_Delete.style.visibility="";
		btn_Modify.style.visibility="";
	}
	else if(optStatus=="VIEWDELETE") //�i�����s����_,��^
	{
		btn_Recover.style.visibility="";
		
	}
	else if(optStatus=="ADD") //�i�����s�ѽT�w ,��^
	{
		btn_OK.style.visibility="";
	}
	else if(optStatus=="DELETE") //�i�����s�R��,��^
	{
		btn_Delete.style.visibility="";
	}
	else if(optStatus=="MODIFY")//�i�����s�T�w,��^
	{
		btn_OK.style.visibility="";
	}

}

// set Disable of Detail�����W���F �T�w ,�R��, ��_, �ק�, ��^ �o���ӫ��s���~���Ҧ�Elements
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


//�H�U��k�ѥ~���ե�,�H�T�w�����W���s�M��J�ت����A

function setDetailPageElementsStatus(funcID)
{
	//RhettJiang,����Ҧ�5�Ӫ�button��style.visibility�]��false;
	document.all("btnOK").style.visibility="hidden";
	document.all("btnDelete").style.visibility="hidden";
	document.all("btnRecover").style.visibility="hidden";
	document.all("btnModify").style.visibility="hidden";
	document.all("btnReturn").style.visibility="hidden";
	
	
	setBtnNotHiddenByStatus();
	setBtnHiddenByRight(funcID);
	
	//�i�H���p�U����:VIEWNORMAL,VIEWDELETE,ADD,DELETE,MODIFY(�������j�g)
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

//�@�U�o�Ӥ�k��Ҧ�����hidden��button��width�m��0
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
