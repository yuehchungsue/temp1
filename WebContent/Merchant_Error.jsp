<%@ page contentType="text/html;charset=BIG5" %>
<%
  response.addHeader("Pragma", "No-cache");
  response.addHeader("Cache-Control", "no-cache");
  response.addDateHeader("Expires", 1);
//  ***************************************************************************
//  * #File Name: 	 Merchant_Error.jsp
//  * #Description:    catch all the exception and display the cause
//  * #Create Date:    2007-10-17
//  * #Company:	 cybersoft
//  * @author          Caspar Chen
//  * @see
//  * @since		 Java Standard V0.1
//  * @version	 0.0.1    2007-10-17    Caspar Chen
//  ***************************************************************************
// Comic Sans MS
%>
<html>
	<head>
          <title>�t�ο��~</title>
	</head>
	<body>
          <font face="Microsoft JhengHei" size=4>
            <blockquote>
              <h1><font color=red>�t�ΰT��</font></h1>
              <p>���~: <%=request.getAttribute("errMsg")%>
            </blockquote>
          </font>
	</body>
</html>
