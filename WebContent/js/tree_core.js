document.write ("<div id=\"TreeScr\" style=\"behavior:url(#default#download)\"></div>")
document.write ("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td>")
document.write ("<div id=\"TreeRoot\"></div><div id=\"TreeTemp_0\"></div></td></tr></table>")

var IconPath, TreeFile, TreeLeft, TreeTop
var TmpStr_0, TmpStr_1, LastIndex
var RootItem, RootLink
var NodeItem, VertLine, NodeIcon, ItemIcon, NodeLink, NodeEvent, SwapEvent, ChildNode, TempHTML

function onDownloadDone(scriptText){
TmpStr_0 = scriptText.replace(/^[\s]+/g,"").replace(/[\s]+$/g,"").split("\n")
RootItem = TmpStr_0[0].replace(/^[\s]+/g,"").replace(/[\s]+$/g,"").split(",")
RootLink = (RootItem[2])?
" href=\""+RootItem[2]+"\" target=\""+RootItem[3]+"\" onMouseOver=\"Hilight('Over','Link_0');t\
op.status='"+RootItem[0]+"';return true\" onMouseOut=\"Hilight('Out','Link_0');top.status=''\"\
 onMouseUp=\"Hilight('Down','Link_0');top.status=''\"":
" href=\"javascript:void(0)\" onMouseOut=\"Hilight('Out','Link_0');top.status=''\" onMouseOver\
=\"Hilight('Over','Link_0');top.status='"+RootItem[0]+"';return true\" onMouseUp=\"Hilight('Do\
wn','Link_0');top.status=''\""
TreeRoot.innerHTML = "<table border=\"0\" cellpadding=\"0\" cellspacing=\"1\"><tr><td width=\"\
16\" height=\"16\"><a"+RootLink+" hidefocus=\"true\"><img height=\"16\" width=\"16\" align=\"t\
exttop\" src=\""+IconPath+RootItem[1].replace(/^[\s]+/g,"").replace(/[\s]+$/g,"")+".gif\" bord\
er=0></a></td><td height=\"16\"><div class=\"TreeLink\"><a"+RootLink+" id=\"Link_0\" class=\"T\
ree_Out\" hidefocus=\"true\">"+RootItem[0]+"</a></div></td></tr></table>"

//自動載入圖示
var IconTemper = new Array()
var IconReader = new Array()
var IconLoader = new Array()
for(p=0;p<TmpStr_0.length;p++){
IconTemper[p] = TmpStr_0[p].split(",")
IconReader[p] = IconTemper[p][1].replace(/^[\s]+/g,"").replace(/[\s]+$/g,"")
}
IconTemper = IconReader.join()
IconLoader[0] = "new_tree_node_e_0"
IconLoader[1] = "new_tree_node_e_1"
IconLoader[2] = "new_tree_node_e_2"
IconLoader[3] = "new_tree_node_n_0"
IconLoader[4] = "new_tree_node_n_1"
IconLoader[5] = "new_tree_node_n_2"
IconLoader[6] = "new_tree_vert"
IconLoader[7] = "folder_on"	//資料夾開啟圖示，若有必要請自行更改。
IconLoader[8] = "folder_off"	//資料夾關閉圖示，若有必要請自行更改。
Collection = IconLoader.join()
n = IconLoader.length
for(l=0;l<IconReader.length;l++){
if(!Collection.match(IconReader[l])){
if(IconTemper.match(IconReader[l])){
IconLoader[n] = IconReader[l]
Collection = IconLoader.join()
}
n++
}
}

var PreloadIcons = new Array()
for(c=0;c<IconLoader.length;c++){
PreloadIcons[c] = new Image()
PreloadIcons[c].src = eval('IconPath+IconLoader[c]+".gif"')
}

BuildTree(0,0,0,TmpStr_0.length,false)
}

//建立樹狀目錄
function BuildTree(Step,ToPos,Tree_S,Tree_E,Icon){
if(eval('TreeTemp_'+ToPos).innerHTML){
eval('TreeTemp_'+ToPos).style.display = (eval('TreeTemp_'+ToPos).style.display=="block")?
"none":
"block"
eval('TreeIcon_'+ToPos).src = (eval('TreeTemp_'+ToPos).style.display=="block")?
IconPath+Icon+"_on.gif":
IconPath+Icon+"_off.gif"
eval('TreeNode_'+ToPos).src = (eval('TreeTemp_'+ToPos).style.display=="block")?
eval('TreeNode_'+ToPos).src.replace("_1","_2"):
eval('TreeNode_'+ToPos).src.replace("_2","_1")
}else{
TmpStr_1 = new Array
var j = 0, k = 0
for(i=Tree_S;i<Tree_E;i++){
TmpStr_0[i] = TmpStr_0[i].replace(/^[\s]+/g,"").replace(/[\s]+$/g,"")
if(TmpStr_0[i].search("├")==Step||TmpStr_0[i].search("└")==Step){
TmpStr_1[j] = new Array
TmpStr_1[j][0] = TmpStr_0[i]
TmpStr_1[j][1] = k
j++
}else
if(TmpStr_0[i].search("├")>Step||TmpStr_0[i].search("└")>Step){
k++
TmpStr_1[j-1][1] = TmpStr_1[j-1][1]+1
}
k=0
}
var v = (Tree_S)?
Tree_S:
Tree_S+1
for(i=0;i<TmpStr_1.length;i++){
NodeItem = TmpStr_1[i][0].replace(/[├└│　]/g,"").split(",")
VertLine = (i==TmpStr_1.length-1)?
"":
"background=\""+IconPath+"new_tree_vert.gif\""
NodeIcon = (i==TmpStr_1.length-1)?
"e":
"n"
NodeIcon = (TmpStr_1[i][1])?
NodeIcon+"_1":
NodeIcon+"_0"
ItemIcon = (TmpStr_1[i][1])?
NodeItem[1]+"_off":
NodeItem[1].replace(/^[\s]+/g,"").replace(/[\s]+$/g,"")
NodeLink = (NodeItem[2])?
" href=\""+NodeItem[2]+"\" target=\""+NodeItem[3]+"\" onMouseOver=\"Hilight('Over','Link_"+v+"\
');top.status='"+NodeItem[0]+"';return true\" onMouseOut=\"Hilight('Out','Link_"+v+"');top.sta\
tus=''\" onMouseUp=\"Hilight('Down','Link_"+v+"');top.status=''\"":
" href=\"javascript:void(0)\" onMouseOut=\"Hilight('Out','Link_"+v+"');top.status=''\" onMouse\
Over=\"Hilight('Over','Link_"+v+"');top.status='"+NodeItem[0]+"';return true\" onMouseUp=\"Hil\
ight('Down','Link_"+v+"');top.status=''\""
NodeEvent = (TmpStr_1[i][1])?
" onClick=\"BuildTree("+(Step+1)+",'"+ToPos+"_"+(i+1)+"',"+(v+1)+","+(v+TmpStr_1[i][1]+1)+",'\
"+NodeItem[1]+"')\" style=\"cursor:hand\"":
""
SwapEvent = (TmpStr_1[i][1])?
" onClick=\"TreeNode_"+ToPos+"_"+(i+1)+".click()\"":
""
ChildNode = (TmpStr_1[i][1])?
"<div id=\"TreeTemp_"+ToPos+"_"+(i+1)+"\" style=\"display:none\"></div>":
""
TempHTML = "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td width=\"19\" heigh\
t=\"16\" valign=\"top\" "+VertLine+"><img id=\"TreeNode_"+ToPos+"_"+(i+1)+"\" width=\"19\" hei\
ght=\"16\" align=\"texttop\" src=\""+IconPath+"new_tree_node_"+NodeIcon+".gif\""+NodeEvent+"><\
/td><td height=\"16\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"1\"><tr><td width=\"\
16\" height=\"16\"><div "+SwapEvent+"><a"+NodeLink+" hidefocus=\"true\"><img border=\"0\" heig\
ht=\"16\" width=\"16\" id=\"TreeIcon_"+ToPos+"_"+(i+1)+"\" src=\""+IconPath+ItemIcon+".gif\"><\
/a></div></td><td height=\"16\"><div"+SwapEvent+" nowrap class=\"TreeLink\"><a"+NodeLink+" id=\
\"Link_"+v+"\" class=\"Tree_Out\" hidefocus=\"true\">"+NodeItem[0]+"</a></div></td></tr></tabl\
e>"+ChildNode+"</td></tr></table>"
eval('TreeTemp_'+ToPos).insertAdjacentHTML("beforeEnd",TempHTML)
if(ToPos){
eval('TreeNode_'+ToPos).src = eval('TreeNode_'+ToPos).src.replace("_1","_2")
eval('TreeIcon_'+ToPos).src = ""+IconPath+Icon+"_on.gif"
eval('TreeTemp_'+ToPos).style.display = "block"
}
v+=TmpStr_1[i][1]
v++
}
}
}

function Hilight(s,n)
{
 if(s=="Down")
 {
  if(LastIndex) eval(LastIndex).className = "Tree_Out"
  LastIndex = n
  eval(LastIndex).className = "Tree_Down"
 }
 else
 {
  eval(n).className=(eval(n).id==LastIndex)?"Tree_Down":eval("'Tree_'+s")
 }
}

function DrawTree()
{
 IconPath = (IconPath)?IconPath:"./"
 if(TreeTop>=0) document.body.style.marginTop = TreeTop
 if(TreeLeft>=0) document.body.style.marginLeft = TreeLeft
// TreeScr.startDownload(TreeFile, onDownloadDone)

}
document.ondragstart   = new Function("return false")	//關閉拖曳
document.onselectstart = new Function("return false")	//關閉選取
//document.oncontextmenu = new Function("return false")	//關閉右鍵
window.onload = DrawTree
