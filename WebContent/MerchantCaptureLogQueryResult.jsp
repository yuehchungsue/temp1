<!DOCTYPE HTML PUBLIC "-//w3c//dtd html 4.0 transitional//en">
<%@page import="com.cybersoft.merchant.ctl.MerchantCaptureLogCtl"%>
<%@ page contentType="text/html; charset=Big5" %>
<%@ page language="java" %>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>
<%@ page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%@ page import="com.cybersoft.bean.UserBean" %>
<%
//  response.addHeader("Pragma", "No-cache");
//  response.addHeader("Cache-Control", "no-cache");
//  response.addDateHeader("Expires", 1);

try{
    Enumeration key= request.getAttributeNames();
    while(key.hasMoreElements()){
    String keyname=String.valueOf(key.nextElement());
    //System.out.println(keyname+"="+request.getAttribute(keyname));
    }
    String type=request.getParameter("Type");
    ArrayList capturelog=(ArrayList)session.getAttribute("Capturelog");
    String error_message="./Merchant_Response.jsp";
    if(capturelog==null||capturelog.size()==0){
      session.setAttribute("Message", "�d�L���");
      request.getRequestDispatcher(error_message).forward(request, response);
    }else{
    NumberFormat nf = NumberFormat.getInstance();
    nf.setMaximumFractionDigits(0);
    nf.setMinimumFractionDigits(2);
    NumberFormat nf_int = NumberFormat.getInstance();
    nf_int.setMaximumFractionDigits(0);
    nf_int.setMinimumFractionDigits(0);
    Hashtable userinfo=(Hashtable)session.getAttribute("SYSCONFDATA");
    Hashtable sysconfig=(Hashtable)userinfo.get("SYSCONF");
    String page_cul=String.valueOf(sysconfig.get("MER_PAGE_CUL"));//�@������
    String checkpoint=request.getAttribute("checkpoint")!=null ? request.getAttribute("checkpoint").toString() : "";
    int queryMax=3000;
    String errMessage ="";
    queryMax = Integer.parseInt( sysconfig.get("MER_CAPTURE_QRY_QUANTITY").toString());//���v�d�̰߳�����
    String tempcon=((Hashtable)capturelog.get(0)).get("COUNT")!=null ? ((Hashtable)capturelog.get(0)).get("COUNT").toString() :"0";
    boolean showflag=false;//false:�W�L ture:���W�L
	 if(capturelog != null&&capturelog.size() != 0 && checkpoint.equals("") && Integer.parseInt(tempcon)> queryMax && type.equalsIgnoreCase("List")){
		 showflag=true;
		 capturelog.remove(capturelog.size()-1);
		session.setAttribute("Message", "�d�߸�Ƶ���"+tempcon+"���A�W�L�t�άd�߭���"+String.valueOf(queryMax)+"��!");
	    errMessage ="�d�߸�Ƶ���"+tempcon+"���A�W�L�t�άd�߭���"+String.valueOf(queryMax)+"���A�O�_�d�ߤW������"+String.valueOf(queryMax)+"��?";
    }

//#########################��ܷ~�ƳB�z #########################
    int result_count=0;  //���G�`����
    try{
      result_count=capturelog.size();
    }catch(Exception e){
      result_count=0;
    }
    String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);
    int TotalPcs = result_count;
    int TotalPage = 0;
    int MaxPcs = 10;
    int NowPage = 0;
    try {
      String strNowPage = (String)request.getParameter("page_no");
      if (strNowPage != null && strNowPage.length()>0) {
        NowPage = Integer.parseInt(strNowPage);
      }
    }
    catch (Exception e) {
      NowPage = 0;
    }
    try {
      String strPagePcs = page_cul;
      if (strPagePcs != null && strPagePcs.length()>0) {
        MaxPcs = Integer.parseInt(strPagePcs);
      }
    }
    catch (Exception e) {
      MaxPcs = 10;
    }

    boolean boolHome = false;
    boolean boolUp = false;
    boolean boolDown = false;
    boolean boolEnd = false;
    TotalPage = TotalPcs / MaxPcs;  // ���`����
    System.out.println("TotalPage="+TotalPage+" TotalPcs="+TotalPcs+" MaxPcs="+MaxPcs);
    if ((TotalPcs % MaxPcs) >0) TotalPage++;

    if (NowPage>=TotalPage) NowPage = 0;  // �ثe���w���Ƥj��̤j���ƻ��k0

    if (TotalPage>1) {
      if ((NowPage+1) < TotalPage){
        boolDown = true;
        boolEnd = true;
      }
      if (NowPage>0){
        boolHome = true;
        boolUp = true;
      }
    }
//#########################��ܷ~�ƳB�z #########################
int sale_appro_amt=0;//�ʳf�w�֭�
int sale_appro_cnt=0;
int sale_failer_amt=0;//�ʳf��h
int sale_failer_cnt=0;
int sale_wait_amt=0;//�ʳf�ݳB�z
int sale_wait_cnt=0;
int sale_void_amt=0;//�ʳf����
int sale_void_cnt=0;
int sale_process_amt=0;//�ʳf�B�z��
int sale_process_cnt=0;

int refund_appro_amt=0;//�h�f�w�֭�
int refund_appro_cnt=0;
int refund_failer_amt=0;//�h�f��h
int refund_failer_cnt=0;
int refund_wait_amt=0;//�h�f�ݳB�z
int refund_wait_cnt=0;
int refund_void_amt=0;//�h�f����
int refund_void_cnt=0;
int refund_process_amt=0;//�h�f�B�z��
int refund_process_cnt=0;

int net_appro_amt=0;//�w�֭�
int net_appro_cnt=0;
int net_failer_amt=0;//��h
int net_failer_cnt=0;
int net_wait_amt=0;//�ݳB�z
int net_wait_cnt=0;
int net_void_amt=0;//����
int net_void_cnt=0;
int net_process_amt=0;//�B�z��
int net_process_cnt=0;

Hashtable merchantuserinfo = (Hashtable)userinfo.get("MERCHANT_USER");
//�l�S���M��
ArrayList subMidList = new ArrayList();
//�O�_���l�S��
boolean isSubMid =false;
//�O�_����@�S��
boolean isSignMer = false;
String subMidName="";
String subMid="";
subMidName=merchantuserinfo.get("MERCHANTCALLNAME").toString();
subMid=merchantuserinfo.get("SUBMID").toString();
isSubMid =((String) merchantuserinfo.get("ISSUBMERCHANT")).equals("Y") ?  true : false;

subMidList = (ArrayList) userinfo.get("SUBMID");
isSignMer = subMidList != null && subMidList.size() > 1 ? false :  true;
%>
<html>
<head>
<title>
</title>

 <meta http-equiv="Content-Type" content="text/html charset=big5">
 <link href="css/style.css" type="text/css" rel="stylesheet">
<script language="JavaScript">
function checkXXX(forma){
	forma.style.display='none';

	if(<%=showflag%> && document.form.checkpoint.value!="XXX" && document.form.Type.value!="TotalNet"){

		if(confirm('<%=errMessage%>')){
			forma.style.display='';
			document.form.checkpoint.value="XXX";
			forma.submit();
		}else{
			var XXX= "<%=error_message %>";
			//document.form.action.value=XXX;
			forma.action=XXX;
			forma.submit();
		}
	}else{
		forma.style.display='';
	}
}


function View_Data(x,merchantid,submid,sysorderid,rowid)
{rowid=encodeURIComponent(rowid);
 strFeatures = "top=" + (screen.height-670)/2 + ",left=150,width=500,height=670,toolbar=0,menubar=0,location=0,directories=0,status=0,scrollbars=yes,resizable=yes";
// window.open("<%=request.getContextPath()%>/MerchantCaptureLogCtl?Detail=true&MerchantId="+merchantid+"&SubMid="+submid+"&SysOrderId="+sysorderid+"&RowId="+rowid+"&<%=MerchantMenuCtl.MENU_FORWARD_KEY%>=<%=MenuKey%>&Action=OPEN","_bank",strFeatures);
 window.open("<%=request.getContextPath()%>/MerchantCaptureLogCtl?Detail=true&MerchantId="+merchantid+"&SubMid="+submid+"&SysOrderId="+sysorderid+"&RowId="+rowid+"&<%=MerchantMenuCtl.MENU_FORWARD_KEY%>=<%=MenuKey%>&Action=OPEN","",strFeatures);
}

//�дڥ���d��  printReport method �޼ƥ[�J �d�߱��� TransType by Jimmy Kang 20150727
function printReport(Start_TransDate,End_TransDate,TransCode,OrderType,OrderId,AuthID,TerminalID,CaptrueType,Type,printtype, ExceptFlag,subMid, TransType)
{
 strFeatures = "top=" + (screen.height-670)/2 + ",left=150,width=500,height=670,toolbar=0,menubar=0,location=0,directories=0,status=0,scrollbars=yes";
 
 //���v����d�� �}�ҦC�L�����}�[�J�ǰe TransType���� by Jimmy Kang 20150727
 window.open("<%=request.getContextPath()%>/MerchantCaptureLogCtl?ReportFlag=ture&Start_TransDate="+Start_TransDate+"&End_TransDate="+End_TransDate+"&TransCode="+TransCode+"&OrderType="+OrderType+"&OrderID="+OrderId+"&AuthID="+AuthID+"&TerminalID="+TerminalID+"&CaptrueType="+CaptrueType+"&Type="+Type+"&<%=MerchantMenuCtl.MENU_FORWARD_KEY%>=<%=MenuKey%>"+"&PrintType="+printtype+"&ExceptFlag="+ExceptFlag+"&subMid="+subMid+"&TransType="+TransType,"main",strFeatures);
}
function realsubmit(){
  document.form.submit();
}
</script>
</head>
<body bgcolor="#ffffff"  onload="checkXXX(this.form);">
<form name="form" id="form"  method="post" action="./MerchantCaptureLogCtl">
<input type="hidden" id="Start_TransDate" name="Start_TransDate" value='<%=String.valueOf(request.getAttribute("Start_TransDate"))%>' />
<input type="hidden" id="End_TransDate" name="End_TransDate" value='<%=String.valueOf(request.getAttribute("End_TransDate"))%>' />
<input type="hidden" id="TransCode" name="TransCode" value='<%=String.valueOf(request.getAttribute("TransCode"))%>' />
<input type="hidden" id="OrderType" name="OrderType" value='<%=String.valueOf(request.getAttribute("OrderType"))%>' />
<input type="hidden" id="OrderId" name="OrderId" value='<%=String.valueOf(request.getAttribute("OrderId"))%>' />
<input type="hidden" id="AuthID" name="AuthID" value='<%=String.valueOf(request.getAttribute("AuthID"))%>' />
<input type="hidden" id="TerminalID" name="TerminalID" value='<%=String.valueOf(request.getAttribute("TerminalID"))%>' />
<input type="hidden" id="CaptrueType" name="CaptrueType" value='<%=String.valueOf(request.getAttribute("CaptrueType"))%>' />
<input type="hidden" id="ExceptFlag" name="ExceptFlag" value='<%=String.valueOf(request.getAttribute("ExceptFlag"))%>' />
<input type="hidden" id="Type" name="Type" value='<%=String.valueOf(request.getAttribute("Type"))%>' />
<input type="hidden" id="subMid" name="subMid" value='<%=String.valueOf(request.getAttribute("subMid"))%>' />
<input type="hidden" id="checkpoint" name="checkpoint" value='<%=request.getAttribute("checkpoint")==null ? "" : String.valueOf(request.getAttribute("checkpoint"))%>' />

<!-- �дڥ���d�� �s�W���ê�html�����x�s�d�߱��� TransType by Jimmy Kang 20150727  -- �ק�}�l -- -->
<input type="hidden" id="TransType" name="TransType" value='<%=String.valueOf(request.getAttribute("TransType"))%>' />
<!-- �дڥ���d�� �s�W���ê�html�����x�s�d�߱��� TransType by Jimmy Kang 20150727  -- �קﵲ�� -- -->

 <%
 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>

 <table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
   <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
   <td align="left" valign="middle"><b><font color="#004E87" size="3">�дڥ���d��</font></b></td>
  </tr>
 </table>

 <hr style="height:1px">

 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
  <tr>
   <td>
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
     <tr>
      <td height="30" align="left">
      <!-- �дڥ���d��  printReport method �޼ƥ[�J �d�߱��� TransType by Jimmy Kang 20150727  -- �ק�}�l -- -->
       <input type='button' value='�HPDF�榡�ץX' name='btnPDF' id='btnPDF' onclick="printReport('<%=String.valueOf(request.getAttribute("Start_TransDate"))%>','<%=String.valueOf(request.getAttribute("End_TransDate"))%>','<%=String.valueOf(request.getAttribute("TransCode"))%>','<%=String.valueOf(request.getAttribute("OrderType"))%>','<%=String.valueOf(request.getAttribute("OrderId"))%>','<%=String.valueOf(request.getAttribute("AuthID"))%>','<%=String.valueOf(request.getAttribute("TerminalID"))%>','<%=String.valueOf(request.getAttribute("CaptrueType"))%>','<%=String.valueOf(request.getAttribute("Type"))%>','PDF','<%=String.valueOf(request.getAttribute("ExceptFlag"))%>','<%=String.valueOf(request.getAttribute("subMid"))%>','<%=String.valueOf(request.getAttribute("TransType"))%>')">
       <%if(type!=null&&type.equalsIgnoreCase("List")){%>
       <input type='button' value='�HCSV�榡�ץX' name='btnCSV' id='btnCSV' onclick="printReport('<%=String.valueOf(request.getAttribute("Start_TransDate"))%>','<%=String.valueOf(request.getAttribute("End_TransDate"))%>','<%=String.valueOf(request.getAttribute("TransCode"))%>','<%=String.valueOf(request.getAttribute("OrderType"))%>','<%=String.valueOf(request.getAttribute("OrderId"))%>','<%=String.valueOf(request.getAttribute("AuthID"))%>','<%=String.valueOf(request.getAttribute("TerminalID"))%>','<%=String.valueOf(request.getAttribute("CaptrueType"))%>','<%=String.valueOf(request.getAttribute("Type"))%>','CSV','<%=String.valueOf(request.getAttribute("ExceptFlag"))%>','<%=String.valueOf(request.getAttribute("subMid"))%>','<%=String.valueOf(request.getAttribute("TransType"))%>')">
       <input type='button' value='�HTXT�榡�ץX' name='btnTXT' id='btnTXT' onclick="printReport('<%=String.valueOf(request.getAttribute("Start_TransDate"))%>','<%=String.valueOf(request.getAttribute("End_TransDate"))%>','<%=String.valueOf(request.getAttribute("TransCode"))%>','<%=String.valueOf(request.getAttribute("OrderType"))%>','<%=String.valueOf(request.getAttribute("OrderId"))%>','<%=String.valueOf(request.getAttribute("AuthID"))%>','<%=String.valueOf(request.getAttribute("TerminalID"))%>','<%=String.valueOf(request.getAttribute("CaptrueType"))%>','<%=String.valueOf(request.getAttribute("Type"))%>','TXT','<%=String.valueOf(request.getAttribute("ExceptFlag"))%>','<%=String.valueOf(request.getAttribute("subMid"))%>','<%=String.valueOf(request.getAttribute("TransType"))%>')">
       <!-- �дڥ���d��  printReport method �޼ƥ[�J �d�߱��� TransType by Jimmy Kang 20150727  -- �קﵲ�� -- -->
       <%} %>
      </td>
       <%if(type!=null&&type.equalsIgnoreCase("List")){%>
       <td align="right">
         <select name="page_no" id="page_no" onchange="realsubmit();">
         <%
         for (int i=0; i<TotalPage  && checkpoint.equals("XXX");++i) {
         %>
         <option <%if (i==NowPage) out.write("selected");%> value="<%=i%>">��<%=i+1%>   ��               </option>
         <%}%>
         </select>
       </td>
       <%}else if(type!=null&&type.equalsIgnoreCase("TotalNet")){

for(int i=0; i<capturelog.size()&& checkpoint.equals("XXX");++i){
  if (i >= capturelog.size())break;
  Hashtable content=(Hashtable)capturelog.get(i);
  String transcode=String.valueOf(content.get("TRANSCODE"));//����N�X
  String feedbackcode=String.valueOf(content.get("FEEDBACKCODE"));//�дڦ^�нX
  String captrueflag=String.valueOf(content.get("CAPTUREFLAG"));//�дڱ���X��
  //Jason 201303
  //String pan = String.valueOf(content.get("PAN"));//PAN
 // System.out.println(content);
  String balance_amt=String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT"));
  balance_amt=String.valueOf(Integer.parseInt(balance_amt)<=0?"0":balance_amt);
  //System.out.println("balance_amt="+balance_amt);
 // System.out.println("transcode="+transcode);
  //System.out.println("captrueflag=<"+captrueflag+">");
  int count=Integer.parseInt(String.valueOf(content.get("COUNT")).trim().length()==0?"0":String.valueOf(content.get("COUNT")));
  if(transcode.equals("00")){//�ʳf���
    if(captrueflag.equalsIgnoreCase("3")){//�H�B�z
      if(feedbackcode.equalsIgnoreCase("000")){
        sale_appro_amt=sale_appro_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
        //sale_appro_cnt++;
        sale_appro_cnt = sale_appro_cnt + count;
      }else{//��h
        sale_failer_amt=sale_failer_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
        //sale_failer_cnt++;
        sale_failer_cnt = sale_failer_cnt + count;
      }
    }else if(captrueflag.equalsIgnoreCase("0")){//���B�z
      sale_wait_amt=sale_wait_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //sale_wait_cnt++;
      sale_wait_cnt = sale_wait_cnt + count;
    }else if(captrueflag.equalsIgnoreCase("2")){//�w����
      sale_void_amt=sale_void_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //sale_void_cnt++;
      sale_void_cnt = sale_void_cnt + count;
    }else if(captrueflag.equalsIgnoreCase("1")){//�B�z��
      sale_process_amt=sale_process_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //sale_process_cnt++;
      sale_process_cnt = sale_process_cnt + count;
    }
  }else if(transcode.equals("01")){
    if(captrueflag.equalsIgnoreCase("3")){//�H�B�z
      if(feedbackcode.equalsIgnoreCase("000")){
        refund_appro_amt=refund_appro_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
        //refund_appro_cnt++;
        refund_appro_cnt = refund_appro_cnt + count;
      }else{//��h
        refund_failer_amt=refund_failer_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
        //refund_failer_cnt++;
        refund_failer_cnt = refund_failer_cnt + count;
      }
    }else if(captrueflag.equalsIgnoreCase("0")){//���B�z
      refund_wait_amt=refund_wait_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
     // refund_wait_cnt++;
      refund_wait_cnt = refund_wait_cnt + count;
    }else if(captrueflag.equalsIgnoreCase("2")){//�w����
      refund_void_amt=refund_void_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //refund_void_cnt++;
      refund_void_cnt = refund_void_cnt + count;
    }else if(captrueflag.equalsIgnoreCase("1")){//�B�z��
      refund_process_amt=refund_process_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //refund_process_cnt++;
      refund_process_cnt = refund_process_cnt + count;
    }
  }else if(transcode.equals("20")){
    if(captrueflag.equalsIgnoreCase("3")){//�H�B�z
    if(feedbackcode.equalsIgnoreCase("000")){
      sale_appro_amt=sale_appro_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //sale_appro_cnt++;
      sale_appro_cnt = sale_appro_cnt + count;
    }else{//��h
      sale_failer_amt=sale_failer_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //sale_failer_cnt++;
      sale_failer_cnt = sale_failer_cnt + count;
    }
    }else if(captrueflag.equalsIgnoreCase("0")){//���B�z
      sale_wait_amt=sale_wait_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //sale_wait_cnt++;
      sale_wait_cnt = sale_wait_cnt + count;
    }else if(captrueflag.equalsIgnoreCase("2")){//�w����
      sale_void_amt=sale_void_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //sale_void_cnt++;
      sale_void_cnt = sale_void_cnt + count;
    }else if(captrueflag.equalsIgnoreCase("1")){//�B�z��
      sale_process_amt=sale_process_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //sale_process_cnt++;
      sale_process_cnt = sale_process_cnt + count;
    }
  }else if(transcode.equals("21")){
    if(captrueflag.equalsIgnoreCase("3")){//�H�B�z
    if(feedbackcode.equalsIgnoreCase("000")){
      refund_appro_amt=refund_appro_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //refund_appro_cnt++;
      refund_appro_cnt = refund_appro_cnt + count;
    }else{//��h
      refund_failer_amt=refund_failer_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //refund_failer_cnt++;
      refund_failer_cnt = refund_failer_cnt + count;
    }
    }else if(captrueflag.equalsIgnoreCase("0")){//���B�z
      refund_wait_amt=refund_wait_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //refund_wait_cnt++;
      refund_wait_cnt = refund_wait_cnt + count;
    }else if(captrueflag.equalsIgnoreCase("2")){//�w����
      refund_void_amt=refund_void_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
     //refund_void_cnt++;
      refund_void_cnt = refund_void_cnt + count;
    }else if(captrueflag.equalsIgnoreCase("1")){//�w����
      refund_process_amt=refund_void_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      //refund_process_cnt++;
      refund_process_cnt = refund_process_cnt + count;
    }
  }
}
} %>
     </tr>
     <tr>
       <td align="left">�d�ߤ��:<%=String.valueOf(request.getAttribute("Start_TransDate"))%>~<%=String.valueOf(request.getAttribute("End_TransDate"))%></td>
       <td align="right">�d�ߺݥ����N�X:<%
       String TerminalID = String.valueOf(request.getAttribute("TerminalID"));
       if (TerminalID.equalsIgnoreCase("ALL")) TerminalID = "����";
       out.write(TerminalID);
       %></td>
     </tr>
       <tr>
       <td align="left">�дڤ覡:<%
       String ExceptFlag = String.valueOf(request.getAttribute("ExceptFlag"));
       if (ExceptFlag.equalsIgnoreCase("ALL")) out.write("����");
       if (ExceptFlag.equalsIgnoreCase("MERCHANT")) out.write("�S���д�");
       if (ExceptFlag.equalsIgnoreCase("ACQ")) out.write("�����ɽд�");
       %></td>
       <td align="right"></td>
     </tr>

    </table>
   </td>
  </tr>
 </table>
<%if(type!=null&&type.equalsIgnoreCase("List")){%>
 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
    <tr align='center' bgcolor="#F4F4FF" height="25">
      <td align='center' rowspan="2">�Ǹ�</td>
      <td align='center' rowspan="2">�˵�</td>
      <td align='center' colspan="1">�S���ө��N��</td>
      <% if(!isSignMer){ %>
   	  <td align='center' rowspan="2">�l�S���ө��N��</td>
      <%} %>
      <td align='center' colspan="1">�S�����w�渹</td>
      <td align='center' rowspan="2">�d��</td>
      <td align='center' colspan="1">������O</td>
      <td align='center' rowspan="2">������</td>
      <td align='center' colspan="1">�^���X</td>
      <td align='center' rowspan="2">�妸���X</td>
      <td align='center' rowspan="2">������O</td>
      <td align='center' rowspan="2">�дڪ��B</td>
      <td align='center' rowspan="2">�дڤ��</td>
      <td align='center' colspan="1">�дڦ^���X</td>
      <td align='center' rowspan="2">�дڤ覡</td>
    </tr>

    <tr align='center' bgcolor='#F4F4FF' height="25">
      <td align='center'>�ݥ����N�X</td>
      <td align='center'>�t�Ϋ��w�渹</td>
      <td align='center'>����Ҧ�</td>
      <td align='center'>���v�X</td>
      <td align='center'>�дڦ^���T��</td>
    </tr>
<%
int page_00_success_amt=0;//�ʳf���\
int page_00_failer_amt=0;
int page_01_amt=0;//�h�f
int page_2x_amt=0;
int page_11_amt=0;
int page_balance_amt=0;
int page_refund_amt=0;

String color="";
  int StartPoint = NowPage * MaxPcs;
  int EndPoint = NowPage * MaxPcs + MaxPcs;
  System.out.println("StartPoint="+StartPoint+" EndPoint="+EndPoint);
for(int i=StartPoint; i<EndPoint&& checkpoint.equals("XXX");++i){
  if (i >= capturelog.size())break;
  Hashtable content=(Hashtable)capturelog.get(i);
  String transcode=String.valueOf(content.get("TRANSCODE"));//����N�X
  String feedbackcode=String.valueOf(content.get("FEEDBACKCODE"));//�дڦ^�нX
  String captrueflag=String.valueOf(content.get("CAPTUREFLAG"));//�дڱ���X��
  //System.out.println(content);
  
    //Jason 201303
  String pan = String.valueOf(content.get("PAN"));//PAN
  //MerchantCaptureLogCtl mcl = new MerchantCaptureLogCtl();
  //pan = mcl.panenCode(pan);
  UserBean UserBean = new UserBean();
  pan = UserBean.get_CardStar(pan, 7,4);
  content.remove("PAN");
  content.put("PAN", pan);
  System.out.println("PAN:"+pan);
  
  
  
//  System.out.println("@@@@@@@@@@@@2");
  String balance_amt=String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT"));
  balance_amt=String.valueOf(Integer.parseInt(balance_amt)<=0?"0":balance_amt);
  //System.out.println("balance_amt="+balance_amt);
 // System.out.println("transcode="+transcode);
 // System.out.println("captrueflag=<"+captrueflag+">");
  if(transcode.equals("00")){//�ʳf���
    color="black";
    transcode=transcode+" - �ʳf";  //���
    balance_amt=nf.format(Double.parseDouble(String.valueOf(balance_amt).trim().length()==0?"0":String.valueOf(balance_amt))) ;
    if(captrueflag.equalsIgnoreCase("3")){//�H�B�z
    if(feedbackcode.equalsIgnoreCase("000")){
      //System.out.println("transcode="+transcode+" Amt="+content.get("CAPTUREAMT"));
      sale_appro_amt=sale_appro_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      sale_appro_cnt++;
    }else{//��h
   // System.out.println("��htranscode="+transcode+" Amt="+content.get("CAPTUREAMT"));
      sale_failer_amt=sale_failer_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      sale_failer_cnt++;
    }
    }else if(captrueflag.equalsIgnoreCase("0")){//���B�z
  //  System.out.println("���B�ztranscode="+transcode+" Amt="+content.get("CAPTUREAMT"));
      sale_wait_amt=sale_wait_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      sale_wait_cnt++;
    }else if(captrueflag.equalsIgnoreCase("2")){//�w����
      sale_void_amt=sale_void_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      sale_void_cnt++;
    }else if(captrueflag.equalsIgnoreCase("1")){//�B�z��
      sale_process_amt=sale_process_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      sale_process_cnt++;
    }
  }else if(transcode.equals("01")){
    color="red";
    transcode=transcode+" - �h�f";  //���
    balance_amt=nf.format(Double.parseDouble(String.valueOf(balance_amt).trim().length()==0?"0":String.valueOf(balance_amt))) ;
    if(captrueflag.equalsIgnoreCase("3")){//�H�B�z
    if(feedbackcode.equalsIgnoreCase("000")){
      refund_appro_amt=refund_appro_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      refund_appro_cnt++;
    }else{//��h
      refund_failer_amt=refund_failer_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      refund_failer_cnt++;
    }
    }else if(captrueflag.equalsIgnoreCase("0")){//���B�z
      refund_wait_amt=refund_wait_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      refund_wait_cnt++;
    }else if(captrueflag.equalsIgnoreCase("2")){//�w����
      refund_void_amt=refund_void_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      refund_void_cnt++;
    }else if(captrueflag.equalsIgnoreCase("1")){//�B�z��
      refund_process_amt=refund_process_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      refund_process_cnt++;
    }
  }else if(transcode.equals("20")){
    color="blue";
    transcode=transcode+" - �ʳf����"; //���
    balance_amt=nf.format(Double.parseDouble(String.valueOf(balance_amt).trim().length()==0?"0":String.valueOf(balance_amt))) ;
    if(captrueflag.equalsIgnoreCase("3")){//�H�B�z
    if(feedbackcode.equalsIgnoreCase("000")){
      sale_appro_amt=sale_appro_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      sale_appro_cnt++;
    }else{//��h
      sale_failer_amt=sale_failer_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      sale_failer_cnt++;
    }
    }else if(captrueflag.equalsIgnoreCase("0")){//���B�z
      sale_wait_amt=sale_wait_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      sale_wait_cnt++;
    }else if(captrueflag.equalsIgnoreCase("2")){//�w����
      sale_void_amt=sale_void_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      sale_void_cnt++;
    }else if(captrueflag.equalsIgnoreCase("1")){//�B�z��
      sale_process_amt=sale_process_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      sale_process_cnt++;
    }
  }else if(transcode.equals("21")){
    color="blue";
    transcode=transcode+" - �h�f����";//���
    balance_amt=nf.format(Double.parseDouble(String.valueOf(balance_amt).trim().length()==0?"0":String.valueOf(balance_amt))) ;
    if(captrueflag.equalsIgnoreCase("3")){//�H�B�z
    if(feedbackcode.equalsIgnoreCase("000")){
      refund_appro_amt=sale_appro_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      refund_appro_cnt++;
    }else{//��h
      refund_failer_amt=sale_failer_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      refund_failer_cnt++;
    }
    }else if(captrueflag.equalsIgnoreCase("0")){//���B�z
      refund_wait_amt=refund_wait_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      refund_wait_cnt++;
    }else if(captrueflag.equalsIgnoreCase("2")){//�w����
      refund_void_amt=refund_void_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      refund_void_cnt++;
    }else if(captrueflag.equalsIgnoreCase("1")){//�B�z��
      refund_process_amt=refund_process_amt+Integer.parseInt(String.valueOf(content.get("CAPTUREAMT")).trim().length()==0?"0":String.valueOf(content.get("CAPTUREAMT")));
      refund_process_cnt++;
   }
  }else{
//   balance_amt=nf.format(Double.parseDouble(balance_amt)) ;
  }
  String transdate=String.valueOf(content.get("TRANSDATE"));
  transdate=transdate.substring(0,4)+"/"+transdate.substring(4,6)+"/"+transdate.substring(6);
  String transtime=String.valueOf(content.get("TRANSTIME"));
  transtime=transtime.substring(0,2)+":"+transtime.substring(2,4)+":"+transtime.substring(4);
  String transmode=String.valueOf(content.get("TRANSMODE"));
  if(transmode.equals("0")){
    transmode=transmode+" - �@��";
  }else if(transmode.equals("1")){
    transmode=transmode+" - ����";
  }else if(transmode.equals("2")){
    transmode=transmode+" - ���Q";
  }
 String merchantid=String.valueOf(content.get("MERCHANTID"));
 String submid=String.valueOf(content.get("SUBMID"));
 String sysorderid=String.valueOf(content.get("SYS_ORDERID"));
 String rowid=String.valueOf(content.get("ROWID1"));
  %>

  <tr align='center' bgcolor='#ffffff'  height="25">
    <td align='left'   rowspan="2"><font color="<%=color%>"><%=i+1 %></font></td>
    <td align='left' rowspan="2">
      <a href="javascript:View_Data(0,'<%=merchantid %>','<%=submid %>','<%= sysorderid%>','<%= rowid%>');"><img src="images/button/view.jpg" alt="�˵�" border="0"></a>
    </td>
    <td align='left'   colspan="1">
      <font color="<%=color%>"><%=String.valueOf(content.get("MERCHANTID"))%></font>
    </td>
    <% if(!isSignMer){ %>
      <td align='center' rowspan="2"><font color="<%=color%>"> <%=String.valueOf(content.get("SUBMID"))  %>  </font></td>
      <%} %>
    <td align='left'   colspan="1">
      <font color="<%=color%>"><%=String.valueOf(content.get("ORDERID"))  %></font>
    <td align='left'   rowspan="2">
      <font color="<%=color%>"><%=String.valueOf(content.get("PAN"))  %></font>
    </td>
    <td align='left'   colspan="1">
      <font color="<%=color%>"><%=String.valueOf(transcode)  %></font>
    </td>
    <td align='center' rowspan="2">
      <font color="<%=color%>"><%=String.valueOf(transdate)  %><br><%=String.valueOf(transtime)  %></font>
    </td>
    <td align='center' colspan="1">
      <font color="<%=color%>"><%=String.valueOf(content.get("RESPONSECODE"))  %></font>
    </td>
    <td align='center'   rowspan="2"><font color="<%=color%>"><%=String.valueOf(content.get("BATCHNO"))  %></font></td>
    <td align='center'   rowspan="2"><font color="<%=color%>">�s�x��</font></td>
    <td align='right'  rowspan="2"><font color="<%=color%>"><%=balance_amt%></font></td>
    <td align='center'  rowspan="2"><font color="<%=color%>"><%=String.valueOf(content.get("CAPTUREDATE"))  %></font></td>
    <td align='left'  rowspan="1"><font color="<%=color%>"><%=String.valueOf(content.get("FEEDBACKCODE"))  %></font></td>
    <td align='left'  rowspan="2"><font color="<%=color%>"><%
    String strExceptFlag = String.valueOf(content.get("EXCEPT_FLAG"));
    if (strExceptFlag.equalsIgnoreCase("Y")) {
      out.write("�����ɽд�");
    } else {
      out.write("�S���д�");
    }

    %></font></td>
  </tr>

  <tr align='center' bgcolor='#ffffff' height="25">
    <td align='left'><font color="<%=color%>"><%=String.valueOf(content.get("TERMINALID"))  %></font></td>
    <td align='left'><font color="<%=color%>"><%=String.valueOf(content.get("SYS_ORDERID"))  %></font></td>
    <td align='left'><font color="<%=color%>"><%=String.valueOf(transmode)  %></font></td>
    <td align='center'><font color="<%=color%>"><%=String.valueOf(content.get("APPROVECODE"))  %></font></td>
    <td align='left'><font color="<%=color%>"><%=String.valueOf(content.get("FEEDBACKMSG"))  %></font></td>
  </tr><%//System.out.println("@@@@@@@@@@@@6"); %>
 <%  }if(checkpoint.equals("")){%>
  <tr align='center' bgcolor='#ffffff'  height="25">
  <td align='left'  colspan="15"  rowspan="2">�d�ߤ�...........</td>
  </tr>
 <%} %>
 </table>
 <br>
 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
 <tr align='center' bgcolor='#ffffff'  height="25">
   <td align='left'   rowspan="2"><font color="">�����p�p</font></td>
 </tr>
 </table>

 <br>
<%}
net_appro_amt=sale_appro_amt-refund_appro_amt;
net_appro_cnt=sale_appro_cnt+refund_appro_cnt;
net_failer_amt=sale_failer_amt-refund_failer_amt;
net_failer_cnt=sale_failer_cnt+refund_failer_cnt;
net_wait_amt=sale_wait_amt-refund_wait_amt;
net_wait_cnt=sale_wait_cnt+refund_wait_cnt;
net_void_amt=sale_void_amt-refund_void_amt;
net_void_cnt=sale_void_cnt+refund_void_cnt;
net_process_amt=sale_process_amt-refund_process_amt;
net_process_cnt=sale_process_cnt+refund_process_cnt;

%>
 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
  <tr bgcolor='#ffffff' height="25">
  <td align="center" width='10%'></td>
  <td align='center' width='30%' colspan="2">�ʳf�д�</td>
  <td align='center' width='30%' colspan="2">�h�f����д�</td>
  <td align='center' width='30%' colspan="2">�b�B</td>
  </tr>
  <tr bgcolor='#ffffff' height="25">
  <td align='center' width='10%'></td>
  <td align='center' width='15%'>����</td>
  <td align='center' width='15%'>���B</td>
  <td align='center' width='15%'>����</td>
  <td align='center' width='15%'>���B</td>
  <td align='center' width='15%'>����</td>
  <td align='center' width='15%'>���B</td>
  </tr>
  <tr bgcolor='#ffffff' height="25">
  <td align='center' width='10%'>�w�֭�</td>
  <td align='right' width='15%'><%=nf_int.format(Integer.parseInt(String.valueOf(sale_appro_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(sale_appro_amt)))  %></td>
  <td align='right' width='15%'><%=nf_int.format(Double.parseDouble(String.valueOf(refund_appro_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(refund_appro_amt)))  %></td>
  <td align='right' width='15%'><%=nf_int.format(Double.parseDouble(String.valueOf(net_appro_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(net_appro_amt)))  %></td>
  </tr>

  <tr bgcolor='#ffffff' height="25">
  <td align='center' width='10%'>��h</td>
  <td align='right' width='15%'><%=nf_int.format(Double.parseDouble(String.valueOf(sale_failer_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(sale_failer_amt)))  %></td>
  <td align='right' width='15%'><%=nf_int.format(Double.parseDouble(String.valueOf(refund_failer_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(refund_failer_amt)))  %></td>
  <td align='right' width='15%'><%=nf_int.format(Double.parseDouble(String.valueOf(net_failer_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(net_failer_amt)))  %></td>
  </tr>

  <tr bgcolor='#ffffff' height="25">
  <td align='center' width='10%'>�ݳB�z</td>
  <td align='right' width='15%' ><%=nf_int.format(Integer.parseInt(String.valueOf(sale_wait_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(sale_wait_amt)))  %></td>
  <td align='right' width='15%'><%=nf_int.format(Double.parseDouble(String.valueOf(refund_wait_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(refund_wait_amt)))  %></td>
  <td align='right' width='15%'><%=nf_int.format(Double.parseDouble(String.valueOf(net_wait_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(net_wait_amt)))  %></td>
  </tr>

  <tr bgcolor='#ffffff' height="25">
  <td align='center' width='10%'>����</td>
  <td align='right' width='15%'><%=nf_int.format(Integer.parseInt(String.valueOf(sale_void_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(sale_void_amt)))  %></td>
  <td align='right' width='15%'><%=nf_int.format(Double.parseDouble(String.valueOf(refund_void_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(refund_void_amt)))  %></td>
  <td align='right' width='15%'><%=nf_int.format(Double.parseDouble(String.valueOf(net_void_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(net_void_amt)))  %></td>
  </tr>
  <tr bgcolor='#ffffff' height="25">
  <td align='center' width='10%'>�B�z��</td>
  <td align='right' width='15%'><%=nf_int.format(Integer.parseInt(String.valueOf(sale_process_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(sale_process_amt)))  %></td>
  <td align='right' width='15%'><%=nf_int.format(Double.parseDouble(String.valueOf(refund_process_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(refund_process_amt)))  %></td>
  <td align='right' width='15%'><%=nf_int.format(Double.parseDouble(String.valueOf(net_process_cnt)))  %></td>
  <td align='right' width='15%'><%=nf.format(Double.parseDouble(String.valueOf(net_process_amt)))  %></td>
  </tr>

 </table>
</form>
</body>
</html>
<%
    }  } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errMsg",e.toString());
            request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
        }
%>
