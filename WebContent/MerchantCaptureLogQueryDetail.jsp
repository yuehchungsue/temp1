<%@ page contentType="text/html; charset=Big5" %>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>
<%@ page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%@ page import="com.cybersoft.bean.UserBean" %>
<%
//  response.addHeader("Pragma", "No-cache");
//  response.addHeader("Cache-Control", "no-cache");
//  response.addDateHeader("Expires", 1);

try{
Hashtable detaillog=(Hashtable)session.getAttribute("DetailLog");
Hashtable userinfo=(Hashtable)session.getAttribute("SYSCONFDATA");
Hashtable merchantinfo=(Hashtable)userinfo.get("MERCHANT_USER");
String merchantid=String.valueOf(merchantinfo.get("MERCHANT_ID"));
Hashtable sysconfig=(Hashtable)userinfo.get("SYSCONF");
Hashtable merchant=(Hashtable)userinfo.get("MERCHANT");
String show_pan=String.valueOf(merchant.get("PERMIT_SHOW_PAN"));//�O�_����ܥd��
String page_cul=String.valueOf(sysconfig.get("MER_PAGE_CUL"));//�@������
System.out.println(detaillog);
NumberFormat nf = NumberFormat.getInstance();
nf.setMaximumFractionDigits(0);
nf.setMinimumFractionDigits(2);
  String transdate=String.valueOf(detaillog.get("TRANSDATE"));
  transdate=transdate.substring(0,4)+"/"+transdate.substring(4,6)+"/"+transdate.substring(6);
  String transtime=String.valueOf(detaillog.get("TRANSTIME"));
  transtime=transtime.substring(0,2)+":"+transtime.substring(2,4)+":"+transtime.substring(4);
  String transmode=String.valueOf(detaillog.get("TRANSMODE"));
  if(transmode.equals("0")){
    transmode=transmode+" - �@��";
  }else if(transmode.equals("1")){
    transmode=transmode+" - ����";
  }else if(transmode.equals("2")){
    transmode=transmode+" - ���Q";
  }
  String transcode=String.valueOf(detaillog.get("TRANSCODE"));
  String transstatus=String.valueOf(detaillog.get("TRANS_STATUS"));
  String balance_transstatus=String.valueOf(detaillog.get("BALANCE_TRANSCODE"));
  String balance_amt=String.valueOf(detaillog.get("CAPTUREAMT"));
  System.out.println("balance_amt{"+nf.format(Double.parseDouble(balance_amt))+"}");
  if(transcode.equals("00")){
    transcode=transcode+" - �ʳf���";
      balance_amt=nf.format(Double.parseDouble(balance_amt)) ;
  }else if(transcode.equals("01")){
    transcode=transcode+" - �h�f���";
    balance_amt=nf.format(Double.parseDouble(balance_amt)) ;
  }else if(transcode.equals("10")){
    transcode=transcode+" - �ʳf�������";
    balance_amt=nf.format(Double.parseDouble(balance_amt)) ;
  }else if(transcode.equals("21")||transcode.equals("20")){
    transcode=transcode+" - �h�f�������";
    balance_amt=nf.format(Double.parseDouble(balance_amt)) ;
  }else{
    balance_amt=nf.format(Double.parseDouble(balance_amt)) ;
  }
  String pan_old=String.valueOf(detaillog.get("PAN")).trim() ;//�d��
  String pan="";
  if(show_pan.equalsIgnoreCase("N")){
    UserBean UserBean = new UserBean();
    pan = UserBean.get_CardStar(pan_old, 7,4);
  }else{
    pan=pan_old;
  }
  
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

 <title>�дڥ���˵�</title>
 <meta http-equiv="Content-Type" content="text/html" charset="big5">
 <link href="css/style.css" type="text/css" rel="stylesheet">

</head>
<body bgcolor="#ffffff">
 <table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
   <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
   <td align="left" valign="middle"><b><font color="#004E87" size="3">�дڥ���˵�</font></b></td>
  </tr>
 </table>

 <hr style="height:1px">

 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8' >
  <tr align='center' bgcolor='#6666CC' height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>�S���ө��N��</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=merchantid %></td>
  </tr>
     <% if(!isSignMer){ %>
  <tr align='center' bgcolor='#6666CC' height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>�l�S���ө��N��</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(detaillog.get("SUBMID")) %></td>
  </tr>     
     <%} %>
  <tr height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>�ݥ����N�X</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(detaillog.get("TERMINALID")) %></td>
  </tr>
  <tr align='center' bgcolor='#6666CC' height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>�S�����w�渹</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(detaillog.get("ORDERID")) %></td>
  </tr>

  <tr height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>�t�Ϋ��w�渹</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(detaillog.get("SYS_ORDERID")) %></td>
  </tr>

  <tr height="25">
    <td width='35%' align='right' bgcolor='#F4F4FF'>�d��</td>
    <td width='65%' align='left'  bgcolor='#FFFFFF'><%=pan%></td>
  </tr>

  <tr height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>������O</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=transcode %></td>
  </tr>

  <tr height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>����Ҧ�</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=transmode%></td>
  </tr>

  <tr height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>������</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=transdate %> <%=transtime %></td>
  </tr>

  <tr height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>�^���X</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%
      String ResponseCode = String.valueOf(detaillog.get("RESPONSECODE"));
   String ResponseDesc = String.valueOf(detaillog.get("RESPONSEMSG")).trim();
   if (ResponseDesc.length()>0) {
      ResponseCode =  ResponseCode +"(" +ResponseDesc+")";
   }
   out.write(ResponseCode);
 %></td>
  </tr>

  <tr height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>���v�X</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(detaillog.get("APPROVECODE")) %></td>
  </tr>

  <tr height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>�妸���X</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(detaillog.get("BATCHNO")) %></td>
  </tr>

  <tr height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>������O</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'>�s�x��</td>
  </tr>

  <tr height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>�дڪ��B</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=detaillog.get("CAPTUREAMT")==null||String.valueOf(detaillog.get("CAPTUREAMT")).trim().length()==0?" ":balance_amt %></td>
  </tr>

  <tr height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>�дڤ��</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(detaillog.get("CAPTUREDATE")) %></td>
  </tr>

  <tr height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>�дڦ^���X</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(detaillog.get("FEEDBACKCODE")) %></td>
  </tr>

  <tr height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>�дڦ^���T��</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(detaillog.get("FEEDBACKMSG")) %></td>
  </tr>

  <tr bgcolor='#6666CC' height="30">
   <td width='100%' align='center' bgcolor='#F4F4FF' colspan="2"><input type='button' value='����' name='btnBack' id='btnBack' onclick='javascript:window.close()' ></td>
  </tr>

 </table>
</body>
</html>
<script language="JavaScript">
function EscCloseWindow(evt)
{
 evt = evt? evt : window.event;
 if (evt.keyCode == 27)
 {
  window.close();
 }
}

//���UESC��������
document.onkeydown = EscCloseWindow;
</script>
<%
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errMsg",e.toString());
            request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
        }
%>
