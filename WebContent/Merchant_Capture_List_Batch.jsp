<%@page contentType="text/html;charset=BIG5"%>
<%@page import="java.util.*" %>
<%@page import="java.text.*"%>
<%@page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%@page import="com.cybersoft.common.Util" %>
<!-- 20221028/jquery�ɯŦ�3.6.0/Jeffery.Cheng/202210210533-00 -->
<%
  try {
/*
 * 202007070145-00 20200707 HKP ���]��|�ݨDUI�W�[���дڥ\��
*/  
    response.addHeader("Pragma", "No-cache");
    response.addHeader("Cache-Control", "no-cache");
    response.setDateHeader("Expires", 0);

    NumberFormat nf = NumberFormat.getInstance();
    nf.setMaximumFractionDigits(2);
    nf.setMinimumFractionDigits(2);
    NumberFormat nf0 = NumberFormat.getInstance();
    nf0.setMaximumFractionDigits(0);
    nf0.setMinimumFractionDigits(0);

    Hashtable userinfo=(Hashtable)session.getAttribute("SYSCONFDATA");
    Hashtable sysinfo=(Hashtable)userinfo.get("SYSCONF");
    /**
     * session key:[hashData]CaptureBatchData :[String]Message,[arrayData]DATALIST ,[hashQuery]QUERY       
     **/
     Hashtable hashCaptureData = new Hashtable();
     String sMessage = "",BatchContent = "",sSUBMITBATCH_DISABLE = "";
 	 String StartTransDate="",EndTransDate="",TransCode="";
     ArrayList arrayList = new ArrayList();
     Hashtable hashQuery = new Hashtable();
     ArrayList arrayFILESTATUS = new ArrayList();
     ArrayList arrayBatchFailGroup = new ArrayList();

     if (session.getAttribute("CaptureBatchData") != null) {
     	hashCaptureData = (Hashtable)session.getAttribute("CaptureBatchData");
     	if (hashCaptureData.size()>0) {
     		sMessage = Util.objToStrTrim(hashCaptureData.get("Message"));
     		sSUBMITBATCH_DISABLE = Util.objToStrTrim(hashCaptureData.get("SUBMITBATCH_DISABLE"));
     		arrayList = (ArrayList)hashCaptureData.get("DATALIST");
     		arrayFILESTATUS = (ArrayList)hashCaptureData.get("FILESTATUS"); //FILESTATUS
     		if(arrayFILESTATUS==null) arrayFILESTATUS = new ArrayList();    //FILESTATUS
     		arrayBatchFailGroup = (ArrayList)hashCaptureData.get("BatchFailGroup"); //BatchFailGroup
     		if(arrayBatchFailGroup == null) arrayBatchFailGroup = new ArrayList();    //BatchFailGroup
     		
     		hashQuery = (Hashtable)hashCaptureData.get("QUERY");
     		hashCaptureData.clear();hashCaptureData=null;
     	}
     }
  	StartTransDate = Util.objToStrTrim(hashQuery.get("StartTransDate"));
  	EndTransDate = Util.objToStrTrim(hashQuery.get("EndTransDate"));
  	TransCode = Util.objToStrTrim(hashQuery.get("TransCode"));
 	BatchContent = Util.objToStrTrim(hashQuery.get("BatchContent"));//�妸�дڥ�LIST:��ܸ��;RESULT:��ܵ��G
 	hashQuery.clear();hashQuery=null;
 	String Action;
 	if(BatchContent.equals("LIST")==true){
 		Action = "BATCHCAPTURE";
 	}else{
 		Action = "FirstQuery";
 	}
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html" >
  <link href="css/style.css" type="text/css" rel="stylesheet">
  <script type="text/javascript" language="javascript" src="./js/Vatrix.js"></script>
  <script type="text/javascript" src="./js/jquery-3.6.0.min.js"></script>
  <script type="text/javascript">
  $(document).ready(function () {
	  $("#divProcess").hide();
  });
  function blink(selector) {
      $(selector).fadeOut(1000, function () {
          $(this).fadeIn(1000, function () {
              blink(this);
          });
      });
  };
  </script>
  <script type="text/javascript" language="JavaScript">

  function toSummitBatch(){
	  if(confirm("�T�{�n������д�!!")==false) return void(0);
	  $("#btnSubmit").attr('disabled', 'disabled');
	  $("#divProcess").show().fadeIn(1000);
	  blink('.blink');//��r�{�{
      document.form.submit();
  }
  function realsubmit(){
    document.form.Action.value = 'Query';
    document.form.submit();
  }

  function toBack(){
    document.form.Action.value = '';
    document.form.submit();
  }

</script>

</head>

<body>
<form id="form" name="form" method="post" action ="./MerchantCaptureCtl" >
    <input type="hidden" name="Action" id="Action" value="<%=Action %>" />
    <input type="hidden" name="token" id="token" value="" />
    <input type="hidden" name="StartTransDate" id="StartTransDate" value="<%=StartTransDate %>" />
    <input type="hidden" name="EndTransDate" id="EndTransDate" value="<%=EndTransDate %>" />
    <input type="hidden" name="TransCode" id="TransCode" value="<%=TransCode %>" />
 <%
 String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);
 out.println("<input type='hidden' name='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' id='"+MerchantMenuCtl.MENU_FORWARD_KEY+"' value='"+MenuKey+"' />");
 %>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
      <td align="left" valign="middle"><b><font color="#004E87" size="3">�u�W����дڧ@�~</font></b></td>
  </tr>
</table>
<hr style="height:1px">
  <%if(sMessage.length() > 0){ %>
  <font color="RED" size="3">�T���G<%=sMessage %></font>
  <% }%>
  <div id="divProcess">
        <div class="form-row">
            <div class="col-12 col-md-11 text-center">
                <font color="blue" size="3"><b class="blink">����B�z���еy��(Please wait)....</b></font>
            </div>
        </div>
  </div>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr bgcolor="#F4F4FF" height="25">
      <td>
      	<font color="BLUE" size="3"><b>���д�</b></font>
      	<%if(BatchContent.equals("LIST")){ %>
      	(�i�дڪ����ơB���B)
      	<%} else{ %>
      	<font color="BLUE">(���G�q��)</font>
      	<%} %>
      </td>
    </tr>
  </table>
   
   <table border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
     <tr align='center' bgcolor="#F4F4FF" height="25">
       <td height="30" width="100" align="left">&nbsp;</td>
       <td width="100" align="right">�`����</td>
       <td width="100" align="right">�`���B</td>
     </tr>
     <%if(arrayList.size()==0){ %>
     <tr>
       <td height="30" colspan=3 align="left" bgcolor='#ffffff'><font color="#FF0000" size="3"><b>�d�L�i�дڸ��</b></font></td>
     </tr>
     <%}%>
     <%
     Hashtable hashData;
     String sMemo ;int iCNT;double dAMT;
     String fontColor="";
     int iTotalCNT=0;double dTotalAMT=0;
     for(int i=0 ;i < arrayList.size();i++){
   	  hashData =(Hashtable)arrayList.get(i);
   	  TransCode = Util.objToStr(hashData.get("TRANSCODE"));
   	  iCNT = Util.objToInt(hashData.get("CNT"));
   	  dAMT = Util.objToDouble(hashData.get("TOTAL_AMT"));
   	  
   	  iTotalCNT+=iCNT;
   	  
   	  if("00".equals(TransCode)){
   		  dTotalAMT += dAMT;
   		  sMemo = "�ʳf�д�";
   		  fontColor ="BLUE";
   	  }else if("01".equals(TransCode)){
   		  dTotalAMT -= dAMT;
   		  sMemo = "�h�f�д�";
   		  fontColor ="RED";
   	  }else if("00FAIL".equals(TransCode)){
   		  dTotalAMT += dAMT;
   		  sMemo = "�ʳf�д�(����)";
   		  fontColor ="BLUE";
   	  }else if("01FAIL".equals(TransCode)){
   		  dTotalAMT -= dAMT;
   		  sMemo = "�h�f�д�(����)";
   		  fontColor ="RED";
   	  }
   	  else{
   		  sMemo = TransCode;
   	  }
     %>
     <tr bgcolor='#ffffff'>
       <td align="left"><font color="<%=fontColor %>"><%=sMemo %></font></td>
       <td align="right"><font color="<%=fontColor %>"><%=nf0.format(iCNT) %></font></td>
       <td align="right"><font color="<%=fontColor %>"><%=nf.format(dAMT) %></font></td>
     </tr>
     <%}%>
     <tr bgcolor='#ffffff'>
       <td align="left">�X�p</td>
       <td align="right"><%=nf0.format(iTotalCNT) %></td>
       <td align="right"><%=nf.format(dTotalAMT) %></td>
     </tr>
   </table>

<%if(BatchContent.equals("LIST") && arrayList.size() > 0){ %>
   <table width="100%" border="0" cellspacing="0" cellpadding="0">
     <tr>
       <td height="30" align="left">
       <% if(sSUBMITBATCH_DISABLE.equals("Y")== false){ %>
           <input type='button' value='�T�w�д�' name='btnSubmit' id='btnSubmit' onclick="toSummitBatch();">
       <%} %>
       </td>
     </tr>
    <tr bgcolor="#F4F4FF" height="25">
      <td>
          <font color="BLUE">
          (1).�պ�i�дڵ��ƻP���B�A���]�t�w�W�L�i�дڴ��������<br/>
          (2).���p����϶��]�t���A�b����i�T�w�дڡj�\��ɦp���s�W���дڥ���N�|�C�J�����妸�дڤ��C
          </font>
      </td>
    </tr>
   </table>
<%} %>
<%if(arrayBatchFailGroup.size() > 0){ %>
<hr/>
  <table border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
    <tr bgcolor="#F4F4FF" height="25">
      <td align='left' colspan="22">
      	<B> >> <font color="BLUE">�妸�дڿ��~�T���J�`</font></B>
      </td>
    </tr>
    <tr align='center' bgcolor="#F4F4FF" height="25">
       <td align='center'>������</td>
       <td align='center'>���~�T��</td>
       <td align='center'>����</td>
       <td align='center'>���B</td>
    </tr>
     <%for (int i=0; i<arrayBatchFailGroup.size(); i++) { 
    	 Hashtable batchFail = (Hashtable)arrayBatchFailGroup.get(i);
     %>
    <tr align='center' bgcolor='#ffffff'  height="25">
       <td align="left" ><font color=""><%=Util.objToStrTrim(batchFail.get("TRANSDATE")) %> </font></td>
       <td align="left" ><font color=""><%=Util.objToStrTrim(batchFail.get("FEEDBACKMSG")) %></font></td>
       <td align="right"><font color=""><%=nf0.format(Util.objToInt(batchFail.get("CNT"))) %></font></td>
       <td align="right"><font color=""><%=nf.format(Util.objToDouble(batchFail.get("BALANCEAMT"))) %></font></td>
    </tr>
     <%} %>
  </table>
<%} %>
<hr/>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr>
    	<td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td height="30" align="left"><input type='button' value='�^�дڵe��' name='btnSubmit' id='btnSubmit' onclick="toBack();"></td>
          </tr>
        </table>
        </td>
    </tr>
  </table>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
    <tr bgcolor="#F4F4FF" height="25">
      <td align='left' colspan="22">
      	<B> >> <font color="BLUE">���дڬ���(�̪�@�P��)</font></B>
      </td>
    </tr>
    <tr align='center' bgcolor="#F4F4FF" height="25">
       <td align='center' rowspan="2">�дڤ��</td>
       <td align='center' rowspan="2">�妸���X</td>
       <td align='center' colspan="6">�妸�д�</td>
       <td align='center' colspan="4">�`���檬�A</td>
       <td align='center' colspan="4">�ʳf�дڰ��檬�A</td>
       <td align='center' colspan="4">�h�f�дڰ��檬�A</td>
       <td align='center' rowspan="2">�B�z���A</td>
       <td align='center' rowspan="2">�B�z�ɶ�</td>
     </tr>
     <tr align='center' bgcolor='#F4F4FF' height="25">
       <td align='center'>�`����</td>
       <td align='center'>�`���B</td>
       <td align='center'>�ʳf�`����</td>
       <td align='center'>�ʳf�`���B</td>
       <td align='center'>�h�f�`����</td>
       <td align='center'>�h�f�`���B</td>
       <td align='center'>�`���\����</td>
       <td align='center'>�`���\���B</td>
       <td align='center'>�`���ѵ���</td>
       <td align='center'>�`���Ѫ��B</td>
       <td align='center'>���\����<!-- �ʳf --></td>
       <td align='center'>���\���B<!-- �ʳf --></td>
       <td align='center'>���ѵ���<!-- �ʳf --></td>
       <td align='center'>���Ѫ��B<!-- �ʳf --></td>
       <td align='center'>���\����<!-- �h�f --></td>
       <td align='center'>���\���B<!-- �h�f --></td>
       <td align='center'>���ѵ���<!-- �h�f --></td>
       <td align='center'>���Ѫ��B<!-- �h�f --></td>
     </tr>
     <%for (int i=0; i<arrayFILESTATUS.size(); i++) { 
    	 Hashtable filestatus = (Hashtable)arrayFILESTATUS.get(i);
     %>
     <tr align='center' bgcolor='#ffffff'  height="25">
       <td align="left" ><font color=""><%=Util.objToStrTrim(filestatus.get("FILE_DATE")) %> <!-- �дڤ�� --></font></td>
       <td align="left" ><font color=""><%=Util.objToStrTrim(filestatus.get("FILE_BATCHNO")) %><!-- �妸���X--></font></td>
       <td align="right"><font color=""><%=nf0.format(Util.objToInt(filestatus.get("FILE_TOTAL_COUNT"))) %><!-- �妸�д��`����--></font></td>
       <td align="right"><font color=""><%=nf.format(Util.objToDouble(filestatus.get("FILE_TOTAL_AMOUNT"))) %><!-- �妸�д��`���B--></font></td>
       <td align="right"><font color=""><%=nf0.format(Util.objToInt(filestatus.get("FILE_PURCHASE_COUNT"))) %><!-- �ʳf�`����  --></font></td>
       <td align="right"><font color=""><%=nf.format(Util.objToDouble(filestatus.get("FILE_PURCHASE_AMOUNT"))) %><!-- �ʳf�`���B--></font></td>
       <td align="right"><font color=""><%=nf0.format(Util.objToInt(filestatus.get("FILE_REFUND_COUNT"))) %><!-- �h�f�`���� --></font></td>
       <td align="right"><font color=""><%=nf.format(Util.objToDouble(filestatus.get("FILE_REFUND_AMOUNT"))) %><!-- �h�f�`���B --></font></td>
       <td align="right"><font color=""><%=nf0.format(Util.objToInt(filestatus.get("SUCCESS_COUNT"))) %><!-- ���\���� --></font></td>
       <td align="right"><font color=""><%=nf.format(Util.objToDouble(filestatus.get("SUCCESS_AMOUNT"))) %><!-- ���\���B --></font></td>
       <td align="right"><font color=""><%=nf0.format(Util.objToInt(filestatus.get("REJECT_COUNT"))) %><!-- ���ѵ��� --></font></td>
       <td align="right"><font color=""><%=nf.format(Util.objToDouble(filestatus.get("REJECT_AMOUNT"))) %><!-- ���Ѫ��B --></font></td>
       <td align="right"><font color=""><%=nf0.format(Util.objToInt(filestatus.get("PURCHASE_SUCCESS_COUNT"))) %><!-- �ʳf���\���� --></font></td>
       <td align="right"><font color=""><%=nf.format(Util.objToDouble(filestatus.get("PURCHASE_SUCCESS_AMOUNT"))) %><!-- �ʳf���\���B --></font></td>
       <td align="right"><font color=""><%=nf0.format(Util.objToInt(filestatus.get("PURCHASE_REJECT_COUNT"))) %><!-- �ʳf���ѵ��� --></font></td>
       <td align="right"><font color=""><%=nf.format(Util.objToDouble(filestatus.get("PURCHASE_REJECT_AMOUNT"))) %><!-- �ʳf���Ѫ��B --></font></td>
       <td align="right"><font color=""><%=nf0.format(Util.objToInt(filestatus.get("REFUND_SUCCESS_COUNT"))) %><!-- �h�f���\���� --></font></td>
       <td align="right"><font color=""><%=nf.format(Util.objToDouble(filestatus.get("REFUND_SUCCESS_AMOUNT"))) %><!-- �h�f���\���B --></font></td>
       <td align="right"><font color=""><%=nf0.format(Util.objToInt(filestatus.get("REFUND_REJECT_COUNT"))) %><!-- �h�f���ѵ��� --></font></td>
       <td align="right"><font color=""><%=nf.format(Util.objToDouble(filestatus.get("REFUND_REJECT_AMOUNT"))) %><!-- �h�f���Ѫ��B --></font></td>
       <td align="center"><font color="">
       <%String sFILE_ATTRIBUTE = Util.objToStrTrim(filestatus.get("FILE_ATTRIBUTE")); 
         if(sFILE_ATTRIBUTE.equals("CF")==true){
        	 sFILE_ATTRIBUTE = "��ƳB�z��";
         }else if(sFILE_ATTRIBUTE.equals("SF")==true){
        	 sFILE_ATTRIBUTE = "�дڥ�����椤";
         }else if(sFILE_ATTRIBUTE.equals("RF")==true){
        	 sFILE_ATTRIBUTE = "���槹��";
         }
         out.write(sFILE_ATTRIBUTE);
       %>
       <!-- �B�z���A --></font></td>
       <td align="center"><font color=""><%=Util.objToStrTrim(filestatus.get("PROCESS_DATE")) %><!-- �B�z�ɶ� --></font></td>
     </tr>
         
     <%} %>
  </table>
</form>
</body>
</html>
<%
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errMsg",e.toString());
            request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
        }
%>
