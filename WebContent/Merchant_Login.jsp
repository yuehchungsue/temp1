<%@page contentType="text/html;charset=BIG5"%>
<%@page import="com.cybersoft.bean.*" %>
<%@page import="com.cybersoft.common.*" %>
<%@page import="java.text.*" %>
<%@page import="java.io.ByteArrayOutputStream" %>
<%@page import="java.io.FileOutputStream" %>
<%@page import="java.io.IOException" %>
<%
  //  ***************************************************************************
  //  * #File Name: 	 Merchant_Login.jsp
  //  * #Description:    the login interface
  //  * #Create Date:    2009-09-13
  //  * #Company:	 cybersoft
  //  * @author          Shirley Lin
  //  * @see
  //  * @since		 Java Standard V0.1
  //  * @version	 0.0.1    2009-09-13    Shirley Lin
  //  * 201710050565-EC������w�˴������I SherryAnn
  //  * 201803200651 20180320 �׸ɺ��z���ծz�I,�ק�EC UI�S���n�J�e�� SherryAnn�A�W�[�ϫ�����
  //  * 2019.12.27 HKP 201912250729-00 HPP�����PFubon�S�������W�[TLS1.2����
  //  * 2021.09.16 KAL 202109130773-00 �S��TLS�ŧi�R��
  //  * 202208090854-01 20220801 HKP PCI-DSS�󴫱K�X����MD5��SHA256�AMD5�������׬�32�ASHA256�������׬�64�A�H�����קP�_�O�_��MD5�A�ܧ�᪺�K�X�@�ߨϥ�SHA256�PLOG�B��
  //  ***************************************************************************
  response.addHeader("Pragma", "no-cache");
  response.addHeader("Cache-Control", "no-store");
  response.setDateHeader("Expires", 0);
  //20180320
  response.addHeader("X-Frame-Options", "DENY");

  try {
  String loginmsg = "";
  if (session.getAttribute("Message") != null) {
    loginmsg = (String) session.getAttribute("Message");
    session.removeAttribute("Message");
  }
  System.out.println("loginmsg="+loginmsg);

    System.out.println("session id="+session.getId());
    session.invalidate();
    session = request.getSession(true);
    System.out.println("session id1="+session.getId());
    
    //20180320 �|��Ʀr���ϧ����ҽX
    String fileName="A01";
    ImageVerifyItem item = new ImageVerifyItem();
    if(item.creatImageVerify(90, 25, 6) == false){
    	System.out.println("�Ʀr�����ҽX false�G"+item.Message);
    }else{
    	//�O��ϧ����ҽX
    	session.setAttribute("zNO", item.randNum);
        fileName = "No"+session.getId().substring(0,10)+".jpg";
        String ContextPath;
        ContextPath = session.getServletContext().getRealPath("/");
        String fileNamePath = ContextPath + "/images/zNO/" + fileName;
        System.out.println("zNO fileNamePath�G"+fileNamePath);
        FileOutputStream imageFile = null;
        try {
            imageFile = new FileOutputStream(fileNamePath);
            imageFile.write(item.image);
        } catch (IOException e ) {
            e.printStackTrace();
        } finally{
            if (imageFile != null)
            	imageFile.close();
        }
    }
    item= null;
%>
<html>
<head>
 <title>�x�_�I���Ȧ� - �q�l�Ӱȥ���I�ڹh�D���x�t��</title>
 <meta http-equiv="Content-Type" content="text/html">
 <link href="css/style.css" type="text/css" rel="stylesheet">
<script type="text/javascript" language="javascript" src="./js/ScreenProtectPage.js"></script>
<script type="text/javascript" src="./js/chk.js"></script>
<script type="text/javascript" src="./js/listanddetail.js"></script>
<script type="text/javascript" language="JavaScript">
<!--
  function openWinTLS12() {
      strFeatures = "top=" + (screen.height-670)/2 + ",left=150,width=600,height=600,toolbar=0,menubar=0,location=0,directories=0,status=0,scrollbars=yes";
      var url = "https://esrv.taipeifubon.com.tw/WebChatFubon/QAiframe/qqa.html?fromDevice=TLS_shutdown";
      window.open(url, "notice", strFeatures);
  }
  function toShowMessage() {
    document.form.MerchantID.focus();
    <%if (loginmsg.length()>0) { %>
      alert("<%=loginmsg%>");
      return void(0);
    <%}%>
  }
  function toSubmit() {
     if (document.form.MerchantID.value.length == 0 || document.form.UserID.value.length == 0 
    		 || document.form.UserPwss.value.length == 0 || document.form.zNO.value.length == 0) {
        alert("�п�J�S���ө��N��/�ϥΪ̥N��/�ϥΪ̱K�X/�ϫ����ҽX");
        return void(0);
     }
     document.form.submit();
  }
  function toClear() {
      document.form.MerchantID.value = '';
      document.form.UserID.value = '';
      document.form.UserPwss.value = '';
      document.form.MerchantID.focus();
  }
  function toClearPwss() {
      document.form.UserPwss.value = '';
      document.form.UserPwss.focus();
  }


  function checklocation() {
     if(top != self) {
         top.location.href = self.location.href;
     }
  }

  window.setTimeout("checklocation()",100);

//-->
</script>
</head>

<body onload="toShowMessage();"  >
<form method="post" id="form" name="form" action ="./MerchantLoginCtl" >
 <table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
   <td align="center">

    <table width="790" border="0" cellspacing="0" cellpadding="0">
     <tr>
      <td><img src="images/logo.jpg"></td>
     </tr>
     <tr>
      <td background="images/login/login_bg.jpg" height="90"></td>
     </tr>
    </table>

    <table width="790" border="0" cellspacing="1" cellpadding="1" bgcolor='#D8D8D8'>
     <tr>
      <td background="images/login/login_line.jpg" height="34"></td>
     </tr>
     <tr>
      <td bgcolor="#ffffff" height="250" align="center">

       <p>

       <table border="0" width="630" cellspacing="0" cellpadding="0">
        <tr><td><img src="images/login/login_title.jpg"></td></tr>
       </table>

       <table border="0" width="630" bordercolor="#7B9CD9" cellspacing="0" cellpadding="0">
        <tr>
         <td width="23"><img src="images/login/bg1-1.gif"></td>
         <td background="images/login/bg1-2.gif"></td>
         <td width="24"><img src="images/login/bg1-3.gif"></td>
        </tr>
        <tr>
         <td width="23" background="images/login/bg2-1.gif"></td>
         <td>

          <table width="100%" border="0" cellspacing="0" cellpadding="0">
           <tr>
            <td align="right"><img src="images/login/login_txt1.jpg"></td>
            <td align="left">&nbsp;<input type='text' id='MerchantID' name='MerchantID' size='23' maxlength='15' autocomplete="off" ></td>
            <td>&nbsp;</td>
           </tr>
           <tr>
            <td align="right"><img src="images/login/login_txt2.jpg"></td>
            <td align="left">&nbsp;<input type='text' id='UserID' name='UserID' size='23' maxlength='10' autocomplete="off" ></td>
            <td>&nbsp;</td>
           </tr>
           <tr>
            <td align="right"><img src="images/login/login_txt3.jpg"></td>
            <td align="left">&nbsp;<input type='password' id='UserPwss' name='UserPwss' size='23' maxlength='16' autocomplete="off" ></td>
            <td>&nbsp;</td>
           </tr>
           <tr>
            <td align="right">�п�J�ϫ����ҽX</td>
            <td align="left" valign="middle">&nbsp;<input type='text' id='zNO' name='zNO' size='23' maxlength='6' autocomplete="off" onkeyup="value=value.replace(/[^\d]/g,'')" />
            &nbsp;<img src="images/zNO/<%=fileName %>"/></td>
            <td align="left"></td>
           </tr>
          </table>

         </td>
         <td width="24" background="images/login/bg2-2.gif"></td>
        </tr>
        <tr>
         <td width="23"><img src="images/login/bg3-1.gif"></td>
         <td background="images/login/bg3-2.gif"></td>
         <td width="24"><img src="images/login/bg3-3.gif"></td>
        </tr>
       </table>

       <br>

       <!-- �Ϥ����i�_��,���M�Ϸ|���} -->
       <table width="630" border="0" cellspacing="0" cellpadding="0">
        <tr>
         <td align="right">
          <img style="cursor:hand" src="images/login/login_ok1.jpg" onmouseover="this.src='images/login/login_ok2.jpg'" onmouseout="this.src='images/login/login_ok1.jpg'" onclick="toSubmit();"><img style="cursor:hand" src="images/login/login_cancel1.jpg" onmouseover="this.src='images/login/login_cancel2.jpg'" onmouseout="this.src='images/login/login_cancel1.jpg'" onclick="toClear();"><!--<img style="cursor:hand" src="images/login/login_reset1.jpg" onmouseover="this.src='images/login/login_reset2.jpg'" onmouseout="this.src='images/login/login_reset1.jpg'" onclick="toClearPwss();">-->
         </td>
        </tr>
       </table>
      </td>
     </tr>
     <tr>
      <td height="105" align="right" background="images/login/login_corp.jpg">
       <font style="FONT-FAMILY:Verdana;FONT-SIZE:7pt" color="#ffffff">Copyright &copy; 2007 Taipei Fubon Bank.&nbsp;&nbsp;&nbsp;</font>
      </td>
     </tr>
    </table>
    <!-- 20210916 �R���S���n�J�e���U�褧TLS�w���ŧi
    <table width="790" border="0" cellspacing="1" cellpadding="1" >
     <tr>
      <td align="left">
            <div class="container">
                <div style="width:630px; font-family: �L�n������; background-color: #F2F2F0;">
                    <p style="font-size: small;color:blue">�˷R���Ȥ� �z�n�G<br/>
                        �бz��109/01/16�e�ɯŨt�Ω��s�����H���ѱz��w���������������</p>
                        <div style="font-size: small">
                        �����ѱz���w���O�٪�����������ҡA�ڭ̱N���������ɯųq�T�[�K��w��TLS1.2�H�W�����A�ð����ª�TLS1.0��TLS1.1�q�T�[�K��w�A���˵��z���s�����O�_���H�U�s���������A�H�קK�L�k�ϥΥ�������C
                        <br /><br />
                        �бz��109�~1��16��e�ɯŹq���@�~�t�Φ�Windows7�H�W�A�ç�s�s�����̷ܳs�����F�w���t�νЧ�s��Android 5.0�H�W�C<br />
                        �Ѧ��s���������p�U�����G<br />
                        Chrome   49�ΥH�W<br />
                        Firefox  31.3�ΥH�W<br />
                        IE       11�ΥH�W(�Ȥ䴩Win7�H�W�@�~�t��)<br />
                        Safari   7 �ΥH�W<br />
                            <br />
                        ���ɭY�z�|����s�A�N�i��X�{�L�k��ܦ����������~�T���F�p������ðݡA�Ь�<a href="javascript:openWinTLS12();">����Ʀ�ȪA</a>�C<br />
                        </div>
                    <div style="font-size: small; text-align: right;">�x�_�I���Ȧ�  �q�W</div>
                </div>
                <br /><br />
            </div>
      </td>
     </tr>
    </table> -->
  </td>
  </tr>
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
