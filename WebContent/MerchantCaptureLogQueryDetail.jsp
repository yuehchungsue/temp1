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
String show_pan=String.valueOf(merchant.get("PERMIT_SHOW_PAN"));//是否全顯示卡號
String page_cul=String.valueOf(sysconfig.get("MER_PAGE_CUL"));//一頁筆數
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
    transmode=transmode+" - 一般";
  }else if(transmode.equals("1")){
    transmode=transmode+" - 分期";
  }else if(transmode.equals("2")){
    transmode=transmode+" - 紅利";
  }
  String transcode=String.valueOf(detaillog.get("TRANSCODE"));
  String transstatus=String.valueOf(detaillog.get("TRANS_STATUS"));
  String balance_transstatus=String.valueOf(detaillog.get("BALANCE_TRANSCODE"));
  String balance_amt=String.valueOf(detaillog.get("CAPTUREAMT"));
  System.out.println("balance_amt{"+nf.format(Double.parseDouble(balance_amt))+"}");
  if(transcode.equals("00")){
    transcode=transcode+" - 購貨交易";
      balance_amt=nf.format(Double.parseDouble(balance_amt)) ;
  }else if(transcode.equals("01")){
    transcode=transcode+" - 退貨交易";
    balance_amt=nf.format(Double.parseDouble(balance_amt)) ;
  }else if(transcode.equals("10")){
    transcode=transcode+" - 購貨取消交易";
    balance_amt=nf.format(Double.parseDouble(balance_amt)) ;
  }else if(transcode.equals("21")||transcode.equals("20")){
    transcode=transcode+" - 退貨取消交易";
    balance_amt=nf.format(Double.parseDouble(balance_amt)) ;
  }else{
    balance_amt=nf.format(Double.parseDouble(balance_amt)) ;
  }
  String pan_old=String.valueOf(detaillog.get("PAN")).trim() ;//卡號
  String pan="";
  if(show_pan.equalsIgnoreCase("N")){
    UserBean UserBean = new UserBean();
    pan = UserBean.get_CardStar(pan_old, 7,4);
  }else{
    pan=pan_old;
  }
  
  Hashtable merchantuserinfo = (Hashtable)userinfo.get("MERCHANT_USER");
//子特店清單
ArrayList subMidList = new ArrayList();
//是否為子特店
boolean isSubMid =false;
//是否為單一特店
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

 <title>請款交易檢視</title>
 <meta http-equiv="Content-Type" content="text/html" charset="big5">
 <link href="css/style.css" type="text/css" rel="stylesheet">

</head>
<body bgcolor="#ffffff">
 <table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
   <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
   <td align="left" valign="middle"><b><font color="#004E87" size="3">請款交易檢視</font></b></td>
  </tr>
 </table>

 <hr style="height:1px">

 <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8' >
  <tr align='center' bgcolor='#6666CC' height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>特約商店代號</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=merchantid %></td>
  </tr>
     <% if(!isSignMer){ %>
  <tr align='center' bgcolor='#6666CC' height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>子特約商店代號</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(detaillog.get("SUBMID")) %></td>
  </tr>     
     <%} %>
  <tr height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>端末機代碼</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(detaillog.get("TERMINALID")) %></td>
  </tr>
  <tr align='center' bgcolor='#6666CC' height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>特店指定單號</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(detaillog.get("ORDERID")) %></td>
  </tr>

  <tr height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>系統指定單號</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(detaillog.get("SYS_ORDERID")) %></td>
  </tr>

  <tr height="25">
    <td width='35%' align='right' bgcolor='#F4F4FF'>卡號</td>
    <td width='65%' align='left'  bgcolor='#FFFFFF'><%=pan%></td>
  </tr>

  <tr height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>交易類別</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=transcode %></td>
  </tr>

  <tr height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>交易模式</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=transmode%></td>
  </tr>

  <tr height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>交易日期</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=transdate %> <%=transtime %></td>
  </tr>

  <tr height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>回應碼</td>
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
   <td width='35%' align='right' bgcolor='#F4F4FF'>授權碼</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(detaillog.get("APPROVECODE")) %></td>
  </tr>

  <tr height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>批次號碼</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(detaillog.get("BATCHNO")) %></td>
  </tr>

  <tr height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>交易幣別</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'>新台幣</td>
  </tr>

  <tr height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>請款金額</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=detaillog.get("CAPTUREAMT")==null||String.valueOf(detaillog.get("CAPTUREAMT")).trim().length()==0?" ":balance_amt %></td>
  </tr>

  <tr height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>請款日期</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(detaillog.get("CAPTUREDATE")) %></td>
  </tr>

  <tr height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>請款回應碼</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(detaillog.get("FEEDBACKCODE")) %></td>
  </tr>

  <tr height="25">
   <td width='35%' align='right' bgcolor='#F4F4FF'>請款回應訊息</td>
   <td width='65%' align='left'  bgcolor='#FFFFFF'><%=String.valueOf(detaillog.get("FEEDBACKMSG")) %></td>
  </tr>

  <tr bgcolor='#6666CC' height="30">
   <td width='100%' align='center' bgcolor='#F4F4FF' colspan="2"><input type='button' value='關閉' name='btnBack' id='btnBack' onclick='javascript:window.close()' ></td>
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

//按下ESC關閉視窗
document.onkeydown = EscCloseWindow;
</script>
<%
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errMsg",e.toString());
            request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
        }
%>
