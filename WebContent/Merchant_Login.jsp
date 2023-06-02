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
  //  * 201710050565-EC網站資安檢測中風險 SherryAnn
  //  * 201803200651 20180320 修補滲透測試弱點,修改EC UI特店登入畫面 SherryAnn，增加圖型驗證
  //  * 2019.12.27 HKP 201912250729-00 HPP頁面與Fubon特店網站增加TLS1.2提示
  //  * 2021.09.16 KAL 202109130773-00 特店TLS宣告刪除
  //  * 202208090854-01 20220801 HKP PCI-DSS更換密碼雜錯MD5為SHA256，MD5雜錯長度為32，SHA256雜錯長度為64，以此長度判斷是否為MD5，變更後的密碼一律使用SHA256與LOG遮蔽
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
    
    //20180320 四位數字的圖形驗證碼
    String fileName="A01";
    ImageVerifyItem item = new ImageVerifyItem();
    if(item.creatImageVerify(90, 25, 6) == false){
    	System.out.println("數字的驗證碼 false："+item.Message);
    }else{
    	//記住圖形驗證碼
    	session.setAttribute("zNO", item.randNum);
        fileName = "No"+session.getId().substring(0,10)+".jpg";
        String ContextPath;
        ContextPath = session.getServletContext().getRealPath("/");
        String fileNamePath = ContextPath + "/images/zNO/" + fileName;
        System.out.println("zNO fileNamePath："+fileNamePath);
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
 <title>台北富邦銀行 - 電子商務交易付款閘道平台系統</title>
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
        alert("請輸入特約商店代號/使用者代號/使用者密碼/圖型驗證碼");
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
            <td align="right">請輸入圖型驗證碼</td>
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

       <!-- 圖片不可斷行,不然圖會分開 -->
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
    <!-- 20210916 刪除特店登入畫面下方之TLS安全宣告
    <table width="790" border="0" cellspacing="1" cellpadding="1" >
     <tr>
      <td align="left">
            <div class="container">
                <div style="width:630px; font-family: 微軟正黑體; background-color: #F2F2F0;">
                    <p style="font-size: small;color:blue">親愛的客戶 您好：<br/>
                        請您於109/01/16前升級系統或瀏覽器以提供您更安全的網路交易環境</p>
                        <div style="font-size: small">
                        為提供您更具安全保障的網路交易環境，我們將於近期全面升級通訊加密協定至TLS1.2以上版本，並停用舊版TLS1.0及TLS1.1通訊加密協定，請檢視您的瀏覽器是否為以下瀏覽器版本，以避免無法使用本行網站。
                        <br /><br />
                        請您於109年1月16日前升級電腦作業系統至Windows7以上，並更新瀏覽器至最新版本；安卓系統請更新至Android 5.0以上。<br />
                        參考瀏覽器版本如下說明：<br />
                        Chrome   49或以上<br />
                        Firefox  31.3或以上<br />
                        IE       11或以上(僅支援Win7以上作業系統)<br />
                        Safari   7 或以上<br />
                            <br />
                        屆時若您尚未更新，將可能出現無法顯示此網頁之錯誤訊息；如有任何疑問，請洽<a href="javascript:openWinTLS12();">本行數位客服</a>。<br />
                        </div>
                    <div style="font-size: small; text-align: right;">台北富邦銀行  敬上</div>
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
