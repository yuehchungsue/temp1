//Harry Mao 2006/07/23
//AIM:²�ƭ������b�������ާ@
//HOW TO USE :
//1:�����ޥγo��JS���쭶����
//<?script language=javascript src="http://image2.sina.com.cn/unipro/pub/getclientinfo_sinahome.js"></?script>
//���s���a�}�令��ڦa�}�C
//2:�b�e�����̫�[�J:<?script language='javascript'>BindEachTextBoxOnBlurEventForCheck();</?script>
//3:�b������s�W�W�[attribute(first page load���ɭ�):btnAdd.attribute.add("onclick","return checkAll();");
//4:�e���W������b�R�WID���ɭԽЫ��Ӧp�U�W�h:CHN_[XXX] ,
//"_"�e���i�H�O�H�U[CHN,EML,NUM,DTN,PHO,MOB,URL,IDC,CUR,ZIP,DOU,ENG......]�����N�զX(���N�ƶq�B���N����),�N���[�q���O�O
//[����,EMAIL,�Ʀr,���,�q��,���ʹq��,URL,�����Ҹ��X,�f��,�l�s,�p��,�^��].��O�h�ӲզX���ɭ�,�O���o��txtBox���\��J�����������O�X�ؤ����@�ءC
//5:�Ҧ���textBox,Select���ݭn��btable�ح�,�ӥB�n�O�ҵ��c�O�P�@�檺�e�@�椤��A��p���m�W��,�p�G�o��textbox�O������g���A�h�n�g�����m�W*���A�u�n�]�t��*��
//�N�i�H�A������"*"�O��˭׹����C
//
//
//�S���ܶq�H�K�T�w�쩳�O���@�ӲŸ��ӽT�w��e�r�q�O�_����C
var mustInputSymbol = "*";
//�o���ܶq�O�n�O��SpecialCheck�����ͪ��h�l�H���C
var strRhettJiangWantToSay = "";

function checkAll(obj)
{
    //��M���Ҧ����Ů�
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
	//�ݭn�M���Ҧ���textBOX �M dropDownList,�o���M���D�n�O�d�ݭ��ǬO�����\���Ū��r�q.
	//�����M���Ҧ���text
	var txtS = document.getElementsByTagName('input');
	
	for(i=0;i<txtS.length;i++)
	{	
		
		if(txtS[i].type.toLowerCase()=='text' && txtS[i].parentElement.parentElement.cells[txtS[i].parentElement.cellIndex-1]!=null)
		{
			//�P�_�o��textBox�Ҧ����椸�檺�e�@�ӳ椸��O�_�]�tmustInputSymbol,��p"*",�p�G�]�t�A�ڭ̴N�{?�o��textBox�����\����.
			
			
			if(txtS[i].parentElement.parentElement.cells[txtS[i].parentElement.cellIndex-1].innerText.indexOf(mustInputSymbol)>=0)
			{
				//��p�G�O�������Ȫ���
				if(trim(txtS[i].value)=='')
				{
					txtS[i].focus();
					alert(txtS[i].parentElement.parentElement.cells[txtS[i].parentElement.cellIndex-1].innerText.replace(mustInputSymbol,'').replace(":","").replace(":","").replace(" ","") + '���������!');
					
					changeOneCellCharacterColor(txtS[i].parentElement.parentElement.cells[txtS[i].parentElement.cellIndex-1]);
					return false;
				}
				else
				{
					
					
				}
				
			}
			
			//���U�ӧP�_�O�_�O���w���榡
			
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
	
	
	//Ȼ�M��M���Ҧ���select
	var selS = document.getElementsByTagName('select');
	
	for(i=0;i<selS.length;i++)
	{
		//�Ω󨾤���W�X�{Table���Ĥ@�ӳ椸�N��Select�����p
		if(selS[i].parentElement.parentElement.cells[selS[i].parentElement.cellIndex-1]!=null)
		{
			//�P�_�o��textbox�Ҧb�椸�檺�e�@�ӳ椸��O�_�]�tMustInputSymbol,��p��*���A�p�G�]�t�A�ڭ̴N�{�w�ݭn�o��textbox�����\���šC
			if(selS[i].parentElement.parentElement.cells[selS[i].parentElement.cellIndex-1].innerText.indexOf(mustInputSymbol)>=0)
			{
				//�p�G�O����Ȫ���
				if(trim(selS[i].value)=='')
				{
					selS[i].focus();
					alert(selS[i].parentElement.parentElement.cells[selS[i].parentElement.cellIndex-1].innerText.replace(mustInputSymbol,'').replace(":","").replace(":","").replace(" ","") + '�����\����!');
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
	
	//�b��^true���e�A����ⳡ���M�z���C�]�p�G�����ⳡ�����ܡ^
	changeBackOneCellCharacterColor();
	
	//�ݩҦ������q�L�F�A�~return true;
	return true;
	
    }
    else
    {
    	//�o�̧P�_�O�_�O�Τᱱ�󤤪�textbox�A�o�ر��p���U�A�b�����W�u����ܥX�Ӫ��ONAME���G"
	//�Ҧ��H���@?�P�_���̾�.
	
	var realObjID;
	if(obj.name.indexOf(":")>0)
	{
		realObjID=obj.name.substring(obj.name.indexOf(":")+1);
	}
	else
	{
		//��󨺨ǥu��Name�A�S��ID���]�n���ǳB�z
		if(obj.id=='')
		{
			realObjID=obj.name;
		}
		else
		{
			realObjID=obj.id;
		}
		
	}
    
	//�u�nCheck���w����H�Y�i
	var strJudge = realObjID.split("_");
	
	
	
	if(checkOne(strJudge[0],obj.value,obj.parentElement.parentElement.cells[obj.parentElement.cellIndex-1].innerText.replace(mustInputSymbol,'').replace("��","").replace(":","").replace(" ",""),obj))
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

//�o��function���\��O����w��Table cell�ح����r���ܦ�m
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


//���ܦ����⪺cell�ح��r���ܦ^��
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
	//�C�@���s���ˬd���ݭn��o���ܶq�M���C
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
			case "MUN": // MUN�N��ONUM���ϦV�A�Y�~���ƦC�A���qNUM�O�~�k�ƦC�C
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
				//���B���d����
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
			//case "AAA": //�e�|��^��A�Ʀr�A�Ϊ̤���A�M��O��-���A���U�Ө��O�Ʀr�A�A���U�Ө��O�Ʀr�Φr��
				//strReg = /^[\u0391-\uFFE5\dA-Za-z]{4}-\d{2}[\dA-Za-z]{2}$/;
				//break;
			case "DE1": //�[�JDecimal�����p�Ʀr�ƪ��P�_DE1�N���p�Ʀ줣��W�L1��
				//Decimal(X,1)
				strReg = /^[-\+]?\d+(\.\d{1})?$/;
				break;
			case "DE2": //�[�JDecimal�����p�Ʀr�ƪ��P�_DE2�N���p�Ʀ줣��W�L2��
				//Decimal(X,2)
				strReg = /^[-\+]?\d+(\.\d{1,2})?$/;
				break;
			case "DE3": //�[�JDecimal�����p�Ʀr�ƪ��P�_DE3�N���p�Ʀ줣��W�L3��
				//Decimal(X,3)
				strReg = /^[-\+]?\d+(\.\d{1,3})?$/;
				break;
			case "DE4": //�[�JDecimal�����p�Ʀr�ƪ��P�_DE4�N���p�Ʀ줣��W�L4��
				//Decimal(X,4)
				strReg = /^[-\+]?\d+(\.\d{1,4})?$/;
				break;
			case "DE5": //�[�JDecimal�����p�Ʀr�ƪ��P�_DE5�N���p�Ʀ줣��W�L5��
				//Decimal(X,5)
				strReg = /^[-\+]?\d+(\.\d{1,5})?$/;
				break;
			case "DE6": //�[�JDecimal�����p�Ʀr�ƪ��P�_DE6�N���p�Ʀ줣��W�L6��
				//Decimal(X,6)
				strReg = /^[-\+]?\d+(\.\d{1,6})?$/;
				break;
			case "DE7": //�[�JDecimal�����p�Ʀr�ƪ��P�_DE7�N���p�Ʀ줣��W�L7��
				//Decimal(X,7)
				strReg = /^[-\+]?\d+(\.\d{1,7})?$/;
				break;
			case "DE8": //�[�JDecimal�����p�Ʀr�ƪ��P�_DE8�N���p�Ʀ줣��W�L8��
				//Decimal(X,8)
				strReg = /^[-\+]?\d+(\.\d{1,8})?$/;
				break;
			case "DE9": //�[�JDecimal�����p�Ʀr�ƪ��P�_DE9�N���p�Ʀ줣��W�L9��
				//Decimal(X,9)
				strReg = /^[-\+]?\d+(\.\d{1,9})?$/;
				break;
			case "CU0": //�[�J���B�p�Ʀ�ƪ��P�_ 
				//Currency 0�p�Ʀ�
				strReg = /^(\d+\,?)+$/;
				break;	
			case "CU1":  
				//Currency 1�p�Ʀ�
				strReg = /^(\d+\,?)+(\.\d{1})?$/;
				break;
			case "CU2":  
				//Currency 2�p�Ʀ�
				strReg = /^(\d+\,?)+(\.\d{1,2})?$/;
				break;
			case "CU3": 
				//Currency 3�p�Ʀ�
				strReg = /^(\d+\,?)+(\.\d{1,3})?$/;
				break;
			case "CU4":  
				//Currency 4�p�Ʀ�
				strReg = /^(\d+\,?)+(\.\d{1,4})?$/;
				break;
			case "CU5": 
				//Currency 5�p�Ʀ�
				strReg = /^(\d+\,?)+(\.\d{1,5})?$/;
				break;
			case "CU6":  
				//Currency 6�p�Ʀ�
				strReg = /^(\d+\,?)+(\.\d{1,6})?$/;
				break;
			case "CU7": //2005/7/3��Steven����,������С��λ�����ж� 
				//Currency 7�p�Ʀ�
				strReg = /^(\d+\,?)+(\.\d{1,7})?$/;
				break;
			case "CU8": 
				//Currency 8�p�Ʀ�
				strReg = /^(\d+\,?)+(\.\d{1,8})?$/;
				break;
			case "CU9":
				//Currency 9�p�Ʀ�
				strReg = /^(\d+\,?)+(\.\d{1,9})?$/;
				break;
				
				
			default:
			//�X�{���󤣦b�C��?���ХܡA���{?��etextbox�S���H�Ϭ���
				tmpTxtValue='';
				break;
				

		}
		
		tmpTxtValue=tmpTxtValue.replace(strReg,"");
		
		//alert(tmpTxtValue);
		
				
	}
	if(tmpTxtValue=='')
	{
		//���U�ӹ墨�ǨS����k�Υ��h��F�������P�_���i��A���P�_
		
		if(specialCheck(txtType,txtValue,obj))
		{
			//�٭n�P�_�O�_�O�פ���DTT(means DateTo),�p�G�O�o���������ܡA�ݭn�g��DTT001_UPDDATE�o�اΦ��A�Y�᭱��T��Ʀr
			//�o�˪��ܡA�N�i�H���������DTF001_UPDDATE�F(means DateFrom). 
			if(txtType.indexOf("DTT")>=0)//����@�ӵ������
			{
				var objDTF = document.all('DTF' + obj.id.substr(3));
				//alert(objDTF.value);
				//alert(obj.value);
				if(objDTF != null && trim(objDTF.value)!='' && trim(obj.value) != '' && (new Date(objDTF.value)>new Date(obj.value)))
				{
					var DTFText = objDTF.parentElement.parentElement.cells[objDTF.parentElement.cellIndex-1].innerText.replace(mustInputSymbol,'');
					var DTTText = obj.parentElement.parentElement.cells[obj.parentElement.cellIndex-1].innerText.replace(mustInputSymbol,'');
					//alert(DTTText.replace("�G","").replace(":","").replace(" ","") + "�I��餣����p��" + DTFText.replace("�G","").replace(":","").replace(" ","")+"�_�l��");
					alert(DTFText.replace("�G","").replace(":","").replace(" ","") + "(�_)����j��" + DTTText.replace("�G","").replace(":","").replace(" ","")+"(��)!");
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
			alert(alertMeg.replace("�G","").replace(":","") + '�������p�U����:' + getTypeCHNName(txtType));
		
			return false;
		}
		
	}
	else
	{
		alert(alertMeg.replace("�G","").replace(":","") + '�������p�U����:' + getTypeCHNName(txtType));
		
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
				strCHNName+="���� ";
				break;
			case "EML":
				strCHNName+="EMAIL ";
				break;
			case "NUM":
				strCHNName+="��� " ;
				break;	
			case "MUN"://�PNUM�[�q�ۦP�A�u�O�b�ƦC�W�O�~����
				strCHNName+="��� ";
				break;	
			case "DTN":
				strCHNName+="��� ";
				break;	
			case "DTF":
				strCHNName+="��� ";
				break;	
			case "DTT":
				strCHNName+="��� ";
				break;	
			case "PHO":
				//phone
				strCHNName+="�q�� ";
				break;
			case "MOB":
				//mobile
				strCHNName+="��� ";
				break;
			case "URL":
				strCHNName+="URL ";
				break;
			case "IDC":
				//ID CARD
				strCHNName+="�����Ҹ��X ";
				break;
			case "CUR":
				//currency
				strCHNName+="�f�� " ;
				break;
			case "ZIP":
				strCHNName+="�l�s ";
				break;
			case "DOU":
				//double
				strCHNName+="�Ʀr ";
				break;
			case "ENG":
				strCHNName+="�^�� ";
				break;
			case "DE1": //decimal(X,1)
				strCHNName+="�p�ƥB�p�Ʀ줣��W�L1�� " ;
				break;	
			case "DE2": //decimal(X,2)
				strCHNName+="�p�ƥB�p�Ʀ줣��W�L2�� ";
				break;	
			case "DE3": //decimal(X,3)
				strCHNName+="�p�ƥB�p�Ʀ줣��W�L3�� ";
				break;	
			case "DE4": //decimal(X,4)
				strCHNName+="�p�ƥB�p�Ʀ줣��W�L4�� ";
				break;	
			case "DE5": //decimal(X,5)
				strCHNName+="�p�ƥB�p�Ʀ줣��W�L5�� ";
				break;	
			case "DE6": //decimal(X,6)
				strCHNName+="�p�ƥB�p�Ʀ줣��W�L6�� ";
				break;	
			case "DE7": //decimal(X,7)
				strCHNName+="�p�ƥB�p�Ʀ줣��W�L7�� ";
				break;	
			case "DE8": //decimal(X,8)
				strCHNName+="�p�ƥB�p�Ʀ줣��W�L8�� ";
				break;	
			case "DE9": //decimal(X,9)
				strCHNName+="�p�ƥB�p�Ʀ줣��W�L9�� ";
				break;	
			case "CU0": //currency 0�p�Ʀ�
				strCHNName+="���B�B���a�p�Ʀ� ";
				break;
			case "CU1": //currency 1�p�Ʀ�
				strCHNName+="���B�B�p�Ʀ줣��W�L1�� ";
				break;
			case "CU2": //currency 2�p�Ʀ�
				strCHNName+="���B�B�p�Ʀ줣��W�L2�� ";
				break;
			case "CU3": //currency 3�p�Ʀ�
				strCHNName+="���B�B�p�Ʀ줣��W�L3�� ";
				break;
			case "CU4": //currency 4�p�Ʀ�
				strCHNName+="���B�B�p�Ʀ줣��W�L4�� ";
				break;
			case "CU5": //currency 5�p�Ʀ�
				strCHNName+="���B�B�p�Ʀ줣��W�L5�� ";
				break;
			case "CU6": //currency 6�p�Ʀ�
				strCHNName+="���B�B�p�Ʀ줣��W�L6�� ";
				break;
			case "CU7": //currency 7�p�Ʀ�
				strCHNName+="���B�B�p�Ʀ줣��W�L7�� ";
				break;
			case "CU8": //currency 8�p�Ʀ�
				strCHNName+="���B�B�p�Ʀ줣��W�L8�� ";
				break;
			case "CU9": //currency 9�p�Ʀ�
				strCHNName+="���B�B�p�Ʀ줣��W�L9�� ";
				break;
			
				
		}
	}
	
	
	var resultStr=strCHNName.substr(0,strCHNName.length-1);
	while(resultStr.indexOf(' ')>0)
	{
		resultStr=resultStr.replace(' ','�Ϊ�');
	}
	
	resultStr += strRhettJiangWantToSay;
	return resultStr;

}

//�ѥ~���եΡA���w�C��text��onblur�ƥ�
//�C���h�J�I���ɭ�Ĳ�o�o�Өƥ�
//�Ȯɤ��γo�Ӥ�k�A�q�L�C�����檺�ɭԡA�b�ե�checkAll()�Τ@�P�_
function BindTextBoxFormat()
{
	var allTxts = document.getElementsByTagName("input");
	var i;
	for(i=0;i<allTxts.length;i++)
	{
		
		
		if(allTxts[i].type.toLowerCase()=='text')
		{
			//���NUM[�Ʀr],DOU[���p�Ʀr],CUR[�f��],DE1~DE9,CU0~CU9�o�X��textbox�n��Align�]�m��right
			if(allTxts[i].name.indexOf("_")==3 && (allTxts[i].name.substr(0,3)=="NUM" || allTxts[i].name.substr(0,3)=="DOU" || allTxts[i].name.substr(0,3)=="CUR" || (allTxts[i].name.substr(0,3)>="CU0" && allTxts[i].name.substr(0,3)<="CU9") || (allTxts[i].name.substr(0,3)>="DE1" && allTxts[i].name.substr(0,3)<="DE9") ) )
			{
				allTxts[i].style.textAlign="right";	
			}
			
			//�W�����޿赲���Z�A�ݭn�[�J�s���P�_�A�P�_name���Z���O�_"_L"�� "_C"�� "_R"�k
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
			
			
			//�p�G�O�f���A�ݭn�[�W�d����
			if(allTxts[i].name.indexOf("_")==3 && (allTxts[i].name.substr(0,3)=="CUR" || (allTxts[i].name.substr(0,3)>="CU0" && allTxts[i].name.substr(0,3)<="CU9") ))
			{
				
				allTxts[i].onfocus = new Function("removeMoneyComma(this);");
				allTxts[i].onblur= new Function("addDecimalCount(this);addMoneyComma(this);");
			}
			
			//�p�G�O�ƦrDE1--DE9�A�h�n�j�w�۰ʸ�0���ƥ�
			//�S�O�`�N���ONUM,DOU���ݭn�ɹs�ʧ@�o��.
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


//�b���ҵe���@�������`���ɭԡASubmit���e�A�ݭn��CUR_��textbox�������A��.�����h��
function clearAllCurrencyComma()
{
	var allTxts = document.getElementsByTagName("input");
	var i;
	for(i=0;i<allTxts.length;i++)
	{
		
		
		if(allTxts[i].type.toLowerCase()=='text')
		{
			
			//�p�G�O�f���A�h�ݭn��d�����bSubmit���e�����h���C
			if(allTxts[i].name.indexOf("_")==3 && (allTxts[i].name.substr(0,3)=="CUR" || (allTxts[i].name.substr(0,3)>="CU0" && allTxts[i].name.substr(0,3)<="CU9") ))
			{
				allTxts[i].value=trim(allTxts[i].value,",");
			}
			
			
			//allTxts[i].onblur=new Function("if(checkAll(this)){return true}else{this.focus();return false;}");
		}
	}
	
}

//�b�Ĥ@��load�������ɭԡA�ݭn�a�X���B�r�q�A�Ҧ����B�r�q�ݭn�[�W",".
function addAllCurrencyComma()
{
	var allTxts = document.getElementsByTagName("input");
	var i;
	for(i=0;i<allTxts.length;i++)
	{
		
		
		if(allTxts[i].type.toLowerCase()=='text')
		{
			
			//�p�G�O�f���A�ݭn��d�����bSubmit���e�����h���C
			if(allTxts[i].name.indexOf("_")==3 && (allTxts[i].name.substr(0,3)=="CUR" || (allTxts[i].name.substr(0,3)>="CU0" && allTxts[i].name.substr(0,3)<="CU9") ))
			{
				addMoneyComma(allTxts[i]);
			}
			
			
			//allTxts[i].onblur=new Function("if(checkAll(this)){return true}else{this.focus();return false;}");
		}
	}
	
}

//�b�Ĥ@��load�������ɭԡA�ɻ��p�Ʀ�.
function addAllDecimalCount()
{
	var allTxts = document.getElementsByTagName("input");
	var i;
	for(i=0;i<allTxts.length;i++)
	{
		addDecimalCount(allTxts[i]);
	}
	
}


//�����B�[�d����
function addMoneyComma(obj)
{	
	//�o�̷|���޿�A�H�K�b�s�Wcomma���e�A��Ҧ����H�k�r�R�h
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
	//�@�Q","�����X�q
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



//����B���d����h��
function removeMoneyComma(obj)
{
	//�]?�n�[�d����A�ҥH�ݭn���J���m�b�@�w�d��
	//���\��J�j��L�A�p��L��0-9,backspace,"."
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

//�n�P�_�O�_�O����A�٭n�⥦�ܦ����T�Φ��A��p1982/13/1�Ajavascript�]�{�O���T����A�o�ɻݭn�⥦�ܦ�1983/1/1
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
		//�p�G�O�Ŧr�Ŧ�A�h���@�P�_�A�p�G�o�Ӧr�q���\���šA���ݭn������
		return true;	
	}
}
//�o�Ӥ�k�O�ΨӧP�_�@�ǵL�k�Υ��h��F�������T�w�O�_���T��field Type:
function specialCheck(txtType,txtValue,obj)
{		
	//���
	
	if(txtType.indexOf("DTN")>=0 || txtType.indexOf("DTF")>=0 || txtType.indexOf("DTT")>=0)
	{
		
		return isDate(obj);
	}
	
	//else if ......
	//��p�G���O���S��P�_�A�[�b�᭱
	if((txtType>="DE1" && txtType<="DE9")||(txtType>="CU0" && txtType<="CU9") || txtType == "CUR" || txtType == "NUM" || txtType == "DOU")
	{
		if(obj.MAXINTCOUNT!=null)//������w�q�̤j��Ƽƶq
		{
			if(trim(txtValue," ")!="")
			{
				var s = new String(parseInt(trim(txtValue,",")));
				if(s.length>parseInt(obj.MAXINTCOUNT))
				{
					strRhettJiangWantToSay = "�B��Ʀ줣��W�L" + obj.MAXINTCOUNT + "��";
					return false;
				}
				else
				{
					strRhettJiangWantToSay = "�B��Ʀ줣��W�L" + obj.MAXINTCOUNT + "��";
					return true;
				}
			
			}
			
			
		}
		
	}
	
	//�ت��O�W�[�\��A�ϥ������b�i�H�墨�ǫ��w��ƪ��r�q�����T���P�_�C
	//�o�̥[���s��ñ�O:DEFINITELENGTH�A�p�G���h�Ӫ��ܡA�γr����}�A��p��10�A12���N��O���O10�N�O12�C�r�����^��r��
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
				str += lens[i] + "�� ";	
			}
			
			if(str.length>0)
			{
				str = str.substr(0,str.length-1);	
			}
			
			while(str.indexOf(' ')>0)
			{
				str=str.replace(' ','�Ϊ�');
			}
			
			
			var isValid = 0; //�q�{�D����
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
				strRhettJiangWantToSay = "�B��ƥu�����" + str;
				return false;
			}
			else
			{
				strRhettJiangWantToSay = "�B��ƥu�����" + str;
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

//�o�Ӥ�k���@�άO��DE1-DE9,CU1-CU9�������p�Ƹɻ���
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
