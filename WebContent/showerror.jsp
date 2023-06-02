<%@page isErrorPage="true" %>
<%--/**
//  * Transaction description
//  *                 20221124 Frog Jump Co., YC White Scan/A03 Injection/Cross-Site Scripting: DOM
    */ --%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <title>error</title>
    <!--link rel="stylesheet" type="text/css" media="all" href="./base.css"-->
    <style type="text/css">
        html{background-color: white;}
        #29617f
        html, body, ul, li, dl, dt, dd, form ,img{margin:0;border:0;padding:0;}
        .page {
            width: 950px;
            margin: 0 auto;
            padding-bottom: 8px;
        }

        dl.top_page {
            padding: 14px 0 10px 0;
            display: block;
        }

        .top_page .path {
            color: #3e7db2;
            font-size: 16px;
            font-weight: 100;
            padding-left: 18px;
        }
        /*con_white 交易頁面底圖*/
        #con_nobg{margin:15px;}
        #con_white{  /* 20160520 by christine.tsai */
	        position: relative;
	        background-color: #fff;
	        margin: 0px 0 8px 0;
	        padding: 5px;
	        -webkit-border-radius: 0px 7px 7px 7px;
	        -moz-border-radius: 0px 7px 7px 7px;
	        border-radius: 0px 7px 7px 7px;}
        #con_white .area_bg{
	        padding:10px;
	        background-color:#ededed;
	        /*圓角*/
	        -webkit-border-radius: 5px;
	        -moz-border-radius: 5px;
	        border-radius: 5px;}
        .titleBlock{
            clear:both; 
            margin:8px 0 3px 0;
            padding:5px 0 5px 0;}
        .tit1{display:inline-block;*zoom: 1;*display: inline;   
	        border-left:6px solid #3e7db2;text-align:left;font-size:15px;color:#034c74;padding:0 5px 0 5px;}
        .tb2{
	        clear:both;
	        border-collapse:collapse;
	        border:3px #f8f8f8 solid;}
        .tb2 td{
	        border:1px #f8f8f8 solid;font-size:13px;
	        color:#343434;
	        padding:5px 5px;+padding:2px 5px;
	        background-color:#efefef;}
        .tb2 .hd{
	        padding:13px 5px;
	        background:url(../img/layout/con_bg.jpg);
	        font-weight:bold; color:#1b3f53;
	        text-align:right;}
        .txt-red,td.txt-red /*, .plus  */ {color:red;font-family:Microsoft JhengHei;}
        .txt-green/*, .minus*/ {color:#009f3c}
        .txt-gray {color:#666;}
        .txt-blue {color:#1b5271;}
        .txt-org {color:#d98b1a;}
        .txt-Balance {color:#366580;}
        .txt-Calculation{color:#333;font-weight:bold;}
        .btnBlock {
            line-height: normal;
            text-align: center;
            padding: 10px 0 10px;
        }

    </style>
	<script language="JavaScript">
	<%-- /** About : Hhtml Frame -- TODO : (1). Need to test*/ --%>
	if (top.location != self.location) {top.location.replace(document.location.href);}
	</script>
</head>
<body styleclass="main">
<%
int code = (pageContext.getErrorData() != null) ?pageContext.getErrorData().getStatusCode():000;

String message = ""+code+"";
if(code == 400) message = "HTTP_BAD_REQUEST";
else if(code == 401) message = "HTTP_UNAUTHORIZED";
else if(code == 403) message = "HTTP_FORBIDDEN";
else if(code == 404) message = "HTTP_NOT_FOUND";
else if(code == 405) message = "HTTP_METHOD_NOT_ALLOWED";
else if(code == 408) message = "HTTP_REQUEST_TIME_OUT";
else if(code == 410) message = "HTTP_GONE";
else if(code == 411) message = "HTTP_LENGTH_REQUIRED";
else if(code == 412) message = "HTTP_PRECONDITION_FAILED";
else if(code == 413) message = "HTTP_REQUEST_ENTITY_TOO_LARGE";
else if(code == 414) message = "HTTP_REQUEST_URI_TOO_LARGE";
else if(code == 415) message = "HTTP_SERVICE_UNAVAILABLE";
else if(code == 500) message = "HTTP_INTERNAL_SERVER_ERROR";
else if(code == 501) message = "HTTP_NOT_IMPLEMENTED";
else if(code == 502) message = "HTTP_BAD_GATEWAY";
else if(code == 503) message = "HTTP_SERVICE_UNAVAILABLE";
else if(code == 506) message = "HTTP_VARIANT_ALSO_VARIES";
%>
    <div class="page" style="width:60%;">
        <dl class="top_page">
            <dt class="path"></dt>
        </dl>
        <div id="con_white">
            <div class="area_bg">
                <table width="100%" border="0" cellpadding="0" cellspacing="0" class="titleBlock">
                    <tbody>
                        <tr>
                            <td>
                                <div class="tit1">Error!!</div>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <table width="100%" border="0" cellspacing="0" cellpadding="0" class="tb2">
                    <tbody>
                        <tr>
                            <td width="20%" class="hd"><%=code%></td>
                            <td style="background-color : white;">
                                <span class="txt-red"><%=message%></span>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <div class="btnBlock"></div>
            </div>
        </div>
    </div>
</body>
</html>