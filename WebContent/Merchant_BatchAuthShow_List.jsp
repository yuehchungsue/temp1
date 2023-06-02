<%@page contentType="text/html;charset=BIG5"%>
<%@page import="java.util.*" %>
<%@page import="java.text.*"%>
<%@page import="com.cybersoft.merchant.ctl.MerchantMenuCtl" %>
<%@ page import="com.cybersoft.bean.UserBean" %>
<%
//  response.addHeader("Pragma", "No-cache");
//  response.addHeader("Cache-Control", "no-cache");
//  response.addDateHeader("Expires", 1);
  try {

    String MenuKey = (String)request.getAttribute(MerchantMenuCtl.MENU_FORWARD_KEY);
    System.out.println("MerchantMenuCtl.MENU_FORWARD_KEY="+MerchantMenuCtl.MENU_FORWARD_KEY);
    NumberFormat nf = NumberFormat.getInstance();
    nf.setMaximumFractionDigits(2);
    nf.setMinimumFractionDigits(2);
    ArrayList content=(ArrayList)session.getAttribute("Content");
    System.out.println("content{"+content+"}");
    int total_count=0;
    int success_count=0;
    int fail_count=0;
    for(int i=0;content!=null&&i<content.size();i++){
      total_count++;
      Hashtable temp=(Hashtable)content.get(i);
      if(temp.get("Check").toString().equalsIgnoreCase("false")){
        fail_count++ ;
      }else{
        success_count++;
      }
    }
    boolean check_pass=false;
    System.out.println("Warn Message{"+session.getAttribute("Message")+"}");
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html" charset=big5">
  <link href="css/style.css" type="text/css" rel="stylesheet">
  <script type="text/javascript" language="javascript" src="./js/Vatrix.js"></script>
  <script type="text/javascript" language="JavaScript">
  <!--
  function toSubmit() {
     var Url = "./MerchantBatchAuthCtl?Action=Confirm&<%=MerchantMenuCtl.MENU_FORWARD_KEY%>=<%=MenuKey%>";
     document.form.action = Url;
     document.form.submit();
  }


  function toCancel(){
 var Url = "./MerchantBatchAuthCtl?Action=&<%=MerchantMenuCtl.MENU_FORWARD_KEY%>=<%=MenuKey%>";
     document.form.action = Url;
     document.form.submit();
  }

//-->
</script>

</head>

<body>
<form id="form" name="form" method="post" action ="./MerchantBatchAuthCtl" >
<input type="hidden" name="Action" id="Action" value="Confirm">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td align="left" valign="top" width="12"><img src="images/sub_title_icon.jpg"></td>
      <td align="left" valign="middle"><b><font color="#004E87" size="3">�ɮױ��v�@�~-�T�{</font></b></td>
  </tr>
</table>
<hr style="height:1px">

  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#F4F4FF">
    <tr>
      <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
          <td>�ɮ��`����:<%=total_count %> ���\����:<%=success_count %> ���ѵ���:<%=fail_count %></td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
  <%if (content!=null&&content.size()>0) {%>
  <table width="100%" border="0" cellspacing="1" cellpadding="1" bgcolor="#D8D8D8">
    <tr align='center' bgcolor="#F4F4FF" height="25">
      <td align='center' rowspan="2">�Ǹ�</td>
      <td align='center' rowspan="1">�ݥ����N�X</td>
      <td align='center' rowspan="1">����Ҧ�</td>
      <td align='center' rowspan="2">�H�Υd�d��</td>
      <td align='center' rowspan="1">�H�Υd���Ħ~��</td>
      <td align='center' rowspan="2">��������</td>
      <td align='center' rowspan="1">���d�HEmail</td>
      <td align='center' rowspan="2">�ˮֵ��G</td>
    </tr>

    <tr align='center' bgcolor='#F4F4FF' height="25">
      <td align='center'>�S�����w�渹</td>
      <td align='center'>������B</td>
      <td align='center'>CVV2/CVC2</td>
      <td align='center'>���d�H�����Ҧr��</td>
    </tr>
    <%
    int count=1;
    for (int c=0; c<content.size(); ++c,count++) {

      Hashtable hashData = (Hashtable)content.get(c);
      System.out.println("hashData="+hashData);
      String BatchTxMsg = hashData.get("Check").toString();
      String FontColor = "";
      String result_message="�ˮ֦��\";
      // ����
      if (BatchTxMsg.equalsIgnoreCase("false")) {
        FontColor = "#FF0000";
        result_message="�ˮ֥���"+hashData.get("CheckMsg").toString();
        // �ɮױ��v�@�~ ��ܻ��p����L�k�ɮױ��v  �ק�  by Jimmy Kang 20150806 -- �ק�}�l --
        if (hashData.get("CheckMsg").toString().equalsIgnoreCase("(M15)")) {
        	result_message += "���p����L�k�ɮױ��v";
        }
        // �ɮױ��v�@�~ ��ܻ��p����L�k�ɮױ��v  �ק�  by Jimmy Kang 20150806 -- �קﵲ�� --
      }else{
        check_pass=check_pass||true;
      }
      String modeshow="";
      if(hashData.get("Tranmode").toString().equalsIgnoreCase("0")){
        modeshow="0-�@����";
      }else if(hashData.get("Tranmode").toString().equalsIgnoreCase("1")){
        modeshow="1-�������";
      }else if(hashData.get("Tranmode").toString().equalsIgnoreCase("2")){
        modeshow="2-���Q���";
      }else{
        modeshow=hashData.get("Tranmode").toString();
      }
      
      UserBean UserBean = new UserBean();    
      String encodePAN = "";
      encodePAN = UserBean.get_CardStar(null == hashData.get("Pan")?"":hashData.get("Pan").toString().trim(),7,4);
%>
<tr align='center' bgcolor='#ffffff'  height="25">
  <td align='left'   rowspan="2"><font color="<%=FontColor%>"><%=count%></font></td>
  <td align='left'   rowspan="1"><font color="<%=FontColor%>"><%=hashData.get("Terminal").toString()%></font></td>
  <td align='left'   rowspan="1"><font color="<%=FontColor%>"><%=modeshow%></font></td>
  <td align='left'   rowspan="2"><font color="<%=FontColor%>"><%=encodePAN%></font></td>
  <td align='center' rowspan="1"><font color="<%=FontColor%>"><%=hashData.get("Effectdate").toString()%></font></td>
  <td align='center' rowspan="2"><font color="<%=FontColor%>"><%=hashData.get("Time").toString()%></font></td>
  <td align='center' rowspan="1"><font color="<%=FontColor%>"><%=hashData.get("Email").toString()%></font></td>
  <td align='left'   rowspan="2"><font color="<%=FontColor%>"><%=result_message%></font></td>
 </tr>
  <tr align='center' bgcolor='#ffffff' height="25">
    <td align='left'  ><font color="<%=FontColor%>"><%=hashData.get("Orderid").toString()%></font></td>
    <td align='right'  ><font color="<%=FontColor%>"><%=nf.format(Double.parseDouble(hashData.get("Tranamt").toString()))%></font></td>
    <td align='center'  ><font color="<%=FontColor%>"><%=hashData.get("CVV2/CVC2").toString()%></font></td>
    <td align='center'><font color="<%=FontColor%>"><%=hashData.get("Id").toString()%></font></td>
  </tr>
  <% } %>
  <tr align='center' height="30">
   <td align="left" bgcolor='#F4F4FF' colspan="9">
    <%if(check_pass){ %><input type='button' value='�T�w' name='btnQuery' id='btnQuery' onclick="toSubmit();" ><%} %><input type='button' value='����' name='btnQuery' id='btnQuery' onclick='toCancel();' >
   </td>
  </tr>
  </table>
  <font color="red">����:�w���ˮ֦��\��ưe�X</font>
  <%} %>
  <br>
</body>
</html>
<%
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errMsg",e.toString());
            request.getRequestDispatcher("./Merchant_Error.jsp").forward(request, response);
        }
%>
