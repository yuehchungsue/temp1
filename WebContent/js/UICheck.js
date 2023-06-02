//ˬd
function IsFill(field,fieldname)
{
if (field==undefined){return true;}
else 
{
if (field.value.length==0)
{
	alert(fieldname + "沒有找到指定的MsgID相對應的Message");
	field.focus();
	return false;
}
}
}

//O_Ʀr
function IsNumber(field,fieldname)
{
  if (field==undefined){return true;}
  else
  {
    if(field.value.length>0)
    {
		//var "0123456789.";
		var ok = "no";
      
        var temp;
		for (var i=0; i<field.value.length; i++) 
		{
			temp = "" + field.value.substring(i, i+1);
			if (valid.indexOf(temp) != "-1")
			{ 
			  ok = "yes";
			}
			else
			{
			ok = "no";
			alert(fieldname + "沒有找到指定的MsgID相對應的Message");
            field.focus();
            return false;
			}
		}
    }
    else
   {
		return true;
	}
  }
}

//O_
function IsInteger(field,fieldname)
{
   if (field==undefined){return true;}
   else
   {
     if(field.value.length>0)
      {
		valid="0123456789-";
		var ok = "yes";
		if ((field.value.indexOf("-")>0) || field.value.split("-").length>2)
			{
			ok="no";
			}
		var temp;
		for (var i=0; i<field.value.length; i++) 
			{
				temp = "" + field.value.substring(i, i+1);
				if (valid.indexOf(temp) == "-1") 
				{
					ok = "no";
				}
			}		
		if (ok == "no") 
			{
				alert(fieldname + "沒有找到指定的MsgID相對應的Message");
				field.focus();
				field.value="";
				return false;
			}
	  }
	}
}

//O_B
function IsDecimal(field,fieldname)
{
  if (field==undefined){return true;}
  else 
   {
     if(field.value.length>0)
     {
        valid="0123456789.-";
		var ok = "yes";
		if ((field.value.indexOf("-")>0) || field.value.split("-").length>2)
		{
		 ok="no";
		}
		if((field.value.indexOf(".")==0)||((field.value.indexOf(".")==field.value.length)))
		{
		 ok="no";
		}
		for (var i=0; i<field.value.length; i++) 
		{
		 temp = "" + field.value.substring(i, i+1);
		 if (valid.indexOf(temp) == "-1") 
		 {
			ok = "no";
		 }
		}
		if (ok == "no") 
		{
		 alert(fieldname + "沒有找到指定的MsgID相對應的Message");
		 field.focus();
		 field.value="";
		 return false;
		}
     }
     else
     {
      return true;
     }
    } 
}

//O_
function IsDateField()
{
 if (field==undefined){return true;}
 else 
 {
  if(field.value.length>0)
  {
   var bolDateValid;
   bolDateValid=true;
   var aryTmp=new Array(3);
   if (field.value.indexOf("-")!=-1)
   {
   aryTmp=field.value.split("-");
   }
   else
   {aryTmp=field.value.split("/");}  
   if(aryTmp.length!=3)
   {
      alert(fieldName+"沒有找到指定的MsgID相對應的Message");
      bolDateValid=false;
	  field.focus();
	  field.value="";	
	  return false;
   }

   if(isNaN(aryTmp[0]) || isNaN(aryTmp[1]) || isNaN(aryTmp[2]))
   {
     alert("沒有找到指定的MsgID相對應的Message");
     bolDateValid=false;
	 field.focus();
	 field.value="";	
	 return false;
   }

   if(aryTmp[0]<1 || aryTmp[0]>2100 || aryTmp[0]<1911 )
   {
     alert("沒有找到指定的MsgID相對應的Message");
     bolDateValid=false;
	 field.focus();
	 field.value="";	
	 return false;
   }

   if(aryTmp[1]<1 || aryTmp[1]>12)
   {
     alert("沒有找到指定的MsgID相對應的Message");
     bolDateValid=false;
	 field.focus();
	 field.value="";
	 return false;
   }


   if(aryTmp[0]%100!=0 && aryTmp[0]%4==0)
   {
      if(aryTmp[1]==1 || aryTmp[1]==3 || aryTmp[1]==5 || aryTmp[1]==7 || aryTmp[1]==8 || aryTmp[1]==10 || aryTmp[1]==12)
      {
         if(aryTmp[2]<1 || aryTmp[2]>31)
         {
           alert("沒有找到指定的MsgID相對應的Message");
           bolDateValid=false;
		   field.focus();
		   field.value="";	
		   return false;
         }
      }
      else if(aryTmp[1]==4 || aryTmp[1]==6 || aryTmp[1]==9 || aryTmp[1]==11)
      {
           if(aryTmp[2]<1 || aryTmp[2]>30)
           {
              alert("沒有找到指定的MsgID相對應的Message");
            bolDateValid=false;
			field.focus();
			field.value="";	
			return false;
            }
       }
      else{
           if(aryTmp[2]<1 || aryTmp[2]>29)
           {
               alert("沒有找到指定的MsgID相對應的Message");
             bolDateValid=false;
			 field.focus();
			 field.value="";	
			 return false;
            }
          }
   }
   else{
     if(aryTmp[1]==1 || aryTmp[1]==3 || aryTmp[1]==5 || aryTmp[1]==7 || aryTmp[1]==8 || aryTmp[1]==10 || aryTmp[1]==12){
         if(aryTmp[2]<1 || aryTmp[2]>31)
         {
           alert("沒有找到指定的MsgID相對應的Message");
           bolDateValid=false;
		   field.focus();
		   field.value="";	
		   return false;
         }
      }
      else if(aryTmp[1]==4 || aryTmp[1]==6 || aryTmp[1]==9 || aryTmp[1]==11){
           if(aryTmp[2]<1 || aryTmp[2]>30)
           {
              alert("沒有找到指定的MsgID相對應的Message");
             bolDateValid=false;
			 field.focus();
			 field.value="";	
			 return false;
            }
         }
      else{
           if(aryTmp[2]<1 || aryTmp[2]>28){
              alert("沒有找到指定的MsgID相對應的Message");
             bolDateValid=false;
			field.focus();
			field.value="";	
			return false;
            }
      }
   }

   return bolDateValid;
  }
  else
  {
   return true;
  }
 }
}



