//RHETTJIANG 2005/03/30
//AIM:操作兩個listBox在客戶端
//要求:selValuesHidden的值為如下類型: ",1,2,3,4,5,"
//要求:allOptionsTextAndValueHidden的值為如下類型: "1:中文,2:馬來問,3:英文"

//把Id為from的listBox的選中的option,移動到Id為to的listBox中,同時操作存放當前選中option的value的hidden.
function addOneOptionToAnother(from,to,hiddenID,type)
{
	//type參數的涵義是要麼是"LR" 從左到右(LeftToRight) , "RL" 從右到左(RightToLeft)
	var fromList = document.all(from);
	var toList = document.all(to);
	var hidden = document.all(hiddenID);
	
	while (fromList.selectedIndex>=0)
	{
		var objOption = new Option(fromList[fromList.selectedIndex].text,fromList[fromList.selectedIndex].value)
		toList.options[toList.options.length]=objOption;
		
		if(type=="LR") //如果是從左到右
		{
			hidden.value+= (fromList[fromList.selectedIndex].value + ",");
		}
		else //如果是從右到左
		{
			hidden.value=hidden.value.replace("," + fromList[fromList.selectedIndex].value + ",",",");
			
		}
		fromList.remove(fromList.selectedIndex);
	}
}

//把Id為from的listBox的所有的option,移動到Id為to的listBox中,同時操作存放當前選中option的value的hidden.
function addAllOptionToAnother(from,to,hiddenID,type)
{
	var fromList = document.all(from);
	var toList = document.all(to);
	var hidden = document.all(hiddenID);
	
	while(fromList.length)
	{
		var objOption = new Option(fromList[0].text,fromList[0].value)
		toList.options[toList.options.length]=objOption;
		
		if(type=="LR") //如果是從左到右
		{
			hidden.value+= (fromList[0].value + ",");
		}
		else //如果是從右到左
		{
			hidden.value=hidden.value.replace("," + fromList[0].value + ",",",");
		}
		fromList.remove(0);
	
	}
	
	
}
//首先根據allOptionsTextAndValueHidden把所有option顯示在左面的ListBox中
//然後根據selValuesHidden把當前已經選中的option移動到右面的ListBox中去
//要求:selValuesHidden的值為如下類型: ",1,2,3,4,5,"
//要求:allOptionsTextAndValueHidden的值為如下類型: "1:中文,2:馬來問,3:英文"
//defaultSelectedValues是默認要被選中的項 比如如果值為",1,2," ,則這兩項默認就被選中,如果沒有值,則為空.
function initialTwoListBox(allOptionsTextAndValueHiddenID,selValuesHiddenID,leftListBoxID,rightListBoxID,defaultSelectedValues)
{
	var allHidden = document.all(allOptionsTextAndValueHiddenID);
	var selHidden = document.all(selValuesHiddenID);
	var LeftListBox = document.all(leftListBoxID);
	var RightListBox = document.all(rightListBoxID);
	
	
	if (allHidden.value!='')
	{
		//首先要把作面的listBox完整重現
		
		var strSAllHidden = allHidden.value.split(",");
		
		var i;
		for (i=0 ; i<strSAllHidden.length;i++)
		{
			
			var newOptText=strSAllHidden[i].split(":")[1];
			var newOptValue=strSAllHidden[i].split(":")[0];
			var newOpt=new Option(newOptText,newOptValue);
			//然後把該移動到右面的option移動到右面
			//如果selHidden裡面包括這個value,說明要把這個option放在右面的ListBox
			//否則,說明要放在左面的ListBox
			
			if(selHidden.value.indexOf(',' + newOptValue + ',')>=0) //說明是已經選中的option
			{
				RightListBox.options.add(newOpt);
			}
			else
			{
				
				LeftListBox.options.add(newOpt);
				//同時要判斷,這個option是否是要求被默認選中的.
				
				if((defaultSelectedValues==null) ||(defaultSelectedValues=='') || (defaultSelectedValues==','))//說明沒有默認被選中,要求顯示成藍色的項
				{
					//do nothing
				}
				else
				{
					//如果這項是要求默認被選中的話
					if(defaultSelectedValues.indexOf(','+ newOptValue +',')>=0)
					{
						
						newOpt.selected=true;
					}
				}
			}
			
			
		}
		
		
		
	}
}