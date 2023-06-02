function buttonRhett()
{
	//以下代碼用以給每個按鈕添加按鈕特效
	var imgs = document.getElementsByTagName("input");
	var i;
	var newSrcS = new Array();
	var mySrcS = new Array();
	newSrcS.length=imgs.length;
	mySrcS.length = imgs.length;
	for (i=0;i < imgs.length;i++)
	{
		if(imgs(i).type=="image")
		{
			var src =  imgs(i).src;
			mySrcS[i] = src;
			
			if (src.charAt(src.indexOf(".")-1)=="1")
			{ 
				//newSrcS[i] = src.replace("1","2");
				newSrcS[i]=src.substring(0,src.indexOf(".")-1) + "2" + src.substr(src.indexOf("."));
				//alert(newSrcS[i]);
				//alert(mySrcS[i]);
				imgs(i).onmouseover=new Function("this.src=newSrcS[" + i + "]");
				imgs(i).onmouseout=new Function("this.src=mySrcS[" + i + "]");
			} 
			
		} 
	}
}				

function keyCheck(strQueryName)
{
	if(event.keyCode == 13)
	{
		document.all(strQueryName).focus();					
	}
}