package com.fubon.tp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
/** import org.apache.commons.lang3.StringEscapeUtils; */
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.errors.EncodingException;

import com.cybersoft.bean.LogUtils;

/**
 * @author YC
 *
 */
public class XSSUtils extends HttpServletRequestWrapper{

	private static final LogUtils log_XSSUtils = new LogUtils("systeminfo");
	private boolean isValidateSuccess = false;
	private String returnValue = "";
	private int returnValueInt = 0;
	private String test = "test";
	private String safeStringPname = "safeStringPname";
	
	/**
	 * Implicit super constructor HttpServletRequestWrapper() is undefined. Must explicitly invoke another constructor
	 */
	/**public XSSUtils() {*/  
		/** super(null); */
	/*}*/

	/**
	 * Throws: java.lang.IllegalArgumentException - if the request is null
	 * @param request
	 */
	public XSSUtils(HttpServletRequest request) {
		super(request);
	}
	
	/**
	 * Validate Parameter
	 * @param String parameter
	 * @return boolean isValidateSuccess
	 */
	public boolean hasValidateParameter(String parameter) {
		
		if  (!parameter.isEmpty() && !parameter.contains("\n") ){
			/** parameter = ESAPI.encoder().decodeForHTML(ESAPI.encoder().encodeForHTML(parameter)); **/
			
			isValidateSuccess = true;
		}
		
		return isValidateSuccess;
	}
	
	/**
	 * Validate Parameter
	 * @param String parameter
	 * @return boolean isValidateSuccess
	 */
	public String filterParameter(String parameter) {
		
		if  (!parameter.isEmpty() && !parameter.contains("\n") ){
			/** parameter = ESAPI.encoder().decodeFromURL(ESAPI.encoder().encodeForURL(parameter)); */
			parameter = ESAPI.encoder().decodeForHTML(ESAPI.encoder().encodeForHTML(parameter));
			
			returnValue = parameter;
		}
		
		return returnValue;
	}
	
	/**
	 * getParameterValues
	 * 
	 * @param String parameter
	 * @return String[]
	 */
	@Override
	public String[] getParameterValues(String parameter) {
		String[] values = super.getParameterValues(parameter);

		if (values == null) {
			return new String[0];
		}

		int count = values.length;
		String[] encodedValues = new String[count];
		for (int i = 0; i < count; i++) {
			encodedValues[i] = stripXSS(values[i]);
		}

		return encodedValues;
	}
	
	/**
	 * Validate Parameters Return Value : String[] arrayReturn
	 * @param String parameters
	 * @return String[] arrayReturn
	 */
	public String[] validateParametersReturnValue(String parameter) {
		
		return this.getParameterValues(parameter);
	}
	
	/**
	 * Validate RowPerPage of Form Value</br>
	 * ESAPI.validator().isValidInteger</br>
	 * @param String formValue
	 * @return boolean isValidateSuccess
	 */
	public boolean hasValidateRowPerPage(String formValue) {
		
		if(ESAPI.validator().isValidInteger(
				test,
                formValue,
                0,
                formValue.length(), 
                true)){
			
			isValidateSuccess = true;
		}
		
		return isValidateSuccess;
	}
	
	/**
	 * Validate RowPerPage of Form Value</br>
	 * ESAPI.validator().isValidInteger</br>
	 * @param int formValue
	 * @return boolean isValidateSuccess
	 */
	public boolean hasValidateInteger(String formValue) {
		
		if(ESAPI.validator().isValidInteger(
				test,
                formValue,
                0,
                3, 
                true)){
			
			isValidateSuccess = true;
		}
		
		return isValidateSuccess;
	}
	
	/**
	 * Validate business of Form Value</br>
	 * Validator.PositiveDigitUnlimited=[0-9]* --       ��</br>
	 * i.e. : SendData.business</br>
	 * @param String formValue
	 * @return boolean isValidateSuccess
	 */
	public boolean hasValidateBusiness(String formValue) {
		
		if(ESAPI.validator().isValidInput(
				"test",
                formValue, 
                "SafeStringCode",
                formValue.length(), 
                false)){
			
			isValidateSuccess = true;
		}
		
		return isValidateSuccess;
	}
	
	/**
	 * Validate UserID of Form Value</br>
	 * Validator.safeStringPname=.* --   �颱  �� </br>
	 * @param String formValue
	 * @return boolean isValidateSuccess
	 */
	public boolean hasValidateUserID(String formValue) {
		
		if(ESAPI.validator().isValidInput(
				test,
                formValue, 
                safeStringPname,
                formValue.length(), 
                false)){
			
			isValidateSuccess = true;
		}
		
		return isValidateSuccess;
	}
	
	/**
	 * Validate ID of Form Value</br>
	 * Validator.SafeStringCode=[0-9a-zA-Z]* --   �� �� �� �� �    �  �� �� </br>
	 * i.e. : SendData.storeid, SendData.shopid, SendData.detail_num, SendData.status_code</br>
	 * @param String formValue
	 * @return boolean isValidateSuccess
	 */
	/** public boolean hasValidateID(String formValue) {

		boolean isValidateSuccess = false;
		
		if(ESAPI.validator().isValidInput(
				"test",
                formValue, 
                "safeStringPname",
                formValue.length()+1, 
                false)){
			
			isValidateSuccess = true;
		}
		
		return isValidateSuccess;
	}*/
	
	/**
	 * Validate URL of Form Value</br>
	 * Validator.SafeStringCode=[0-9a-zA-Z]* -- 不含空白  �英 ��� </br>
	 * i.e. : SendData.img_URL, SendData.flowcontrol_url</br>
	 * @param String formValue
	 * @return boolean isValidateSuccess
	 */
	public boolean hasValidateURL(String formValue) {
		
		if(ESAPI.validator().isValidInput(
				"test",
                formValue, 
                "URL",
                formValue.length(), 
                false)){
			
			isValidateSuccess = true;
		}
		
		return isValidateSuccess;
	}
	
	/**
	 * Validate Email of Form Value</br>
	 * Validator.Email=^[A-Za-z0-9._%'-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,4}$ -- Email</br>
	 * i.e. : </br> 
	 * @param String formValue
	 * @return boolean isValidateSuccess
	 */
	public boolean hasValidateEmail(String formValue) {
		
		if(ESAPI.validator().isValidInput(
				"test",
                formValue, 
                "Email",
                formValue.length(), 
                false)){
			
			isValidateSuccess = true;
		}
		
		return isValidateSuccess;
	}	
	
	/**
	 * Validate DateTime of Form Value</br>
	 * Validator.PositiveDigitUnlimited=[0-9]*</br>
	 * i.e. : this.getServletContext().getInitParameter("stopServiceStartDateTime")</br> 
	 * value : 20201117000000</br>
	 * @param String formValue
	 * @return boolean isValidateSuccess
	 */
	public boolean hasValidateDateTime(String formValue) {
		
		if(ESAPI.validator().isValidInput(
				"test",
                formValue, 
                "PositiveDigitUnlimited",
                formValue.length(), 
                false)){
			
			isValidateSuccess = true;
		}
		
		return isValidateSuccess;
	}
	
	/**
	 * Validate Desc of Form Value</br>
	 * Validator.safeStringPname=.* -- 任� � </br>
	 * i.e. : SendData.status_desc</br>
	 * @param String formValue
	 * @return boolean isValidateSuccess
	 */
	public boolean hasValidateDesc(String formValue) {
		if(ESAPI.validator().isValidInput(
				test,
                formValue, 
                safeStringPname,
                formValue.length(), 
                false)){
			
			isValidateSuccess = true;
		}
		
		return isValidateSuccess;
	}
	
	/**
	 * Validate ParameterNamePassword of Form Value</br>
	 * Validator.Validator.parameter.name.password=(password)* -- 字串中含  �password</br>
	 * @param String formValue
	 * @return boolean isValidateSuccess
	 */
	public String validateRowPerPage(String formValue) {
		
		if(hasValidateRowPerPage(formValue)){
			
			returnValue = formValue;
		}
		
		return returnValue;
	}
	
	/**
	 * Validate Integer of Form Value</br>
	 * ESAPI.validator().isValidInteger</br>
	 * i.e. : Order_ChoiceBackDate.jsp -- xssrequest.getParameter("currentRowPage")</br>
	 * @param int formValue
	 * @return int returnValue
	 */
	public int validateInteger(String formValue) {
		
		if(hasValidateInteger(formValue)){
			
			returnValueInt = Integer.parseInt(formValue);
		}
		
		return returnValueInt;
	}
	
	/**
	 * Validate business of Form Value</br>
	 * Validator.PositiveDigitUnlimited=[0-9]* -- �  ��</br>
	 * i.e. : SendData.business</br>
	 * @param String formValue
	 * @return String returnValue
	 */
	public String validateBusiness(String formValue) {
		
		if(hasValidateBusiness(formValue)){
			
			returnValue = formValue;
		}
		
		return returnValue;
	}
	
	/**
	 * Validate UserID of Form Value</br>
	 * Validator.safeStringPname=.* -- 任� � </br>
	 * @param String formValue
	 * @return String returnValue
	 */
	public String validateUserID(String formValue) {
		
		if(hasValidateUserID(formValue)){
			
			returnValue = formValue;
		}
		
		return returnValue;
	}
	
	/**
	 * Validate ID of Form Value</br>
	 * Validator.SafeStringCode=[0-9a-zA-Z]* --   �� �� �� �� �    �  �� �� </br>
	 * i.e. : SendData.storeid, SendData.shopid, SendData.detail_num, SendData.status_code</br>
	 * @param String formValue
	 * @return String returnValue
	 */
	public String validateID(String formValue) {
		
		if(hasValidateUserID(formValue)){
			
			returnValue = formValue;
		}
		
		return returnValue;
	}
	
	/**
	 * Validate URL of Form Value</br>
	 * usage : ESAPI.encoder().encodeForURL(), ESAPI.encoder().decodeFromURL(), java.net.URLDecoder.decode()</br>
	 * i.e. : SendData.img_URL, SendData.flowcontrol_url</br>
	 * @param String formValue
	 * @return String returnValue
	 */
	public String validateURL(String formValue) {

		
		if(hasValidateURL(formValue)){
			
			returnValue = formValue;
		}
		
		return returnValue;
	}
	
	/**
	 * Validate URL of Form Value</br>
	 * usage : ESAPI.encoder().encodeForURL(), ESAPI.encoder().decodeFromURL(), java.net.URLDecoder.decode()</br>
	 * i.e. : SendData.img_URL, SendData.flowcontrol_url</br>
	 * @param String formValue
	 * @return String returnValue
	 */
	public String validateURL2(String formValue) {
		
		if  (!formValue.isEmpty() && !formValue.contains("\n") ){
                try {
					formValue = ESAPI.encoder().decodeFromURL(ESAPI.encoder().encodeForURL(formValue));
				} catch (EncodingException e) {
					e.printStackTrace();
				}
                try {
					formValue = java.net.URLDecoder.decode(formValue,"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
        }

		return formValue;
	}
	
	/**
	 * Validate Email of Form Value</br>
	 * @param String formValue
	 * @return String returnValue
	 */
	public String validateEmail(String formValue) {
		
		if(hasValidateEmail(formValue)){
			
			returnValue = formValue;
		}
		
		return returnValue;
	}
	
	/**
	 * Validate DateTime of Form Value</br>
	 * @param String formValue
	 * @return String returnValue
	 */
	public String validateDateTime(String formValue) {
		
		if(hasValidateDateTime(formValue)){
			
			returnValue = formValue;
		}
		
		return returnValue;
	}
	
	/**
	 * Validate Desc of Form Value</br>
	 * Validator.SafeStringPname=.* -- 任一字</br>
	 * i.e. : SendData.status_desc</br>
	 * @param String formValue
	 * @return isValidateSuccess
	 * @throws EncodingException
	 * @throws UnsupportedEncodingException
	 */
	public String validateDesc(String formValue) {
		
		if(hasValidateDesc(formValue)){
			
			returnValue = formValue;
		}
		
		return returnValue;
	}
	
	/**
	 * Filter the value of innerHTML of the jsp</br>
	 * @param String formValue
	 * @return String validateValue
	 */
	public String filterInnerHTML(String innerHTMLValue){

		String validateString = "";
		
		/** encoder */
		Encoder encoder = ESAPI.encoder();
		
		/** validateString */
		validateString = encoder.decodeForHTML(encoder.encodeForJavaScript(innerHTMLValue));
		
		return validateString;
	}

	/**
	 * Filter the Attribute of Form of/for the jsp</br>
	 * @param String formValue
	 * @return String validateValue
	 */
	public String filterFormAttribute(String attributeValue){

		String validateValue = "";
		
		/** encoder */
		Encoder encoder = ESAPI.encoder();
		
		/** validateString */
		if(StringUtils.isEmpty(attributeValue)) {
			validateValue = "";
		}else {
			validateValue = encoder.decodeForHTML(encoder.encodeForHTML(attributeValue));
		}		
		
		return validateValue;
	}

	/**
	 * Filter the Attribute of Form of/for the jsp</br>
	 * @param String formValue
	 * @return String validateValue
	 */
	public String escapeFormAttribute(String attributeValue){

		String validateValue = "";
		
		/** validateString */
		if(StringUtils.isEmpty(attributeValue)) {
			validateValue = "";
		}else {
			/** validateValue = (String) StringEscapeUtils.unescapeHtml4(StringEscapeUtils.escapeHtml4(attributeValue)); */
		}		
		
		return validateValue;
	}	

	/**
	 * Check if : has XSS Content</br>
	 * </br>
	 * @param request
	 * @return String returnValue
	 */
	public String validateXSSContent(String value) {
		
		if(!hasXSSContent(value)){
			returnValue = value;
		}else{
			returnValue = dealwithXSSContent(value);
			String msg = "Check if has XSS Content : " + value + ". After dealed with XSSContent : " + returnValue;
			log_XSSUtils.debug(msg);
		}
		
		return returnValue;
	}
	
	/**
	 * For fixing the item : "Cross Site History Manipulation" of CheckMarX</br>
	 * </br>
	 * Replace the name of the redirect method, "redirect"</br> 
	 * with the name of the SendPage method, "sendPage".</br>
	 * 
	 * @param HttpServletResponse response
	 * @param String url
	 */
	public void sendPage(HttpServletResponse response, String url) {

        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            String msg = "Failed to redirect to the URL " + url + ". \nCaused by " +
                    e.getMessage();
            log_XSSUtils.debug(msg, e);
        }
    }
		
	/**
	 * For fixing the item : "Cross Site History Manipulation" of CheckMarX</br>
	 * </br> 
	 * Replace the name of the redirect method, "getRequestDispatcher and forward"</br> 
	 * with the name of the SendPage method, "dispatchPage".</br>
	 * 
	 * @param HttpServletRequest request
	 * @param String url
	 */
	public void dispatchPage(
			HttpServletRequest request, 
			HttpServletResponse response, 
			String url) {

        try {
        	request.getRequestDispatcher(url).forward(request, response);
        } catch (ServletException | IOException e) {
            String msg = "Failed to dispatcher to the URL " + url + ". \nCaused by " +
                    e.getMessage();
            log_XSSUtils.error(msg, e);
        }
	}
	
	/**
	 * setAttribute
	 *
	 * @param HttpSession session
	 * @param String parameter
	 * @param Object o
	 * @return void
	 */
	public void setAttribute(HttpSession session, String name, Object theobject) {
		if(theobject instanceof String) {
			
			String validateValue = "";
			String string1 = (String) theobject;

			
			/** encoder */
			Encoder encoder = ESAPI.encoder();
			
			/** validateString */
			if(StringUtils.isEmpty(string1)) {
				validateValue = "";
			}else {
				validateValue = encoder.decodeForHTML(encoder.encodeForHTML(string1));
			}
			
			/** stripXSS */
			session.setAttribute(name, stripXSS(validateValue));
		}else {
			session.setAttribute(name, theobject);
		}
	}
	
	/**
	 * setAttribute
	 *
	 * @param HttpSession session
	 * @param String parameter
	 * @param Object o
	 * @return void
	 */
	public void setAttribute(ServletContext sc, String name, Object object) {
		if(object instanceof String) {
			
			String validateValue = "";
			String string1 = (String) object;
			
			/** encoder */
			Encoder encoder = ESAPI.encoder();
			
			/** validateString */
			if(StringUtils.isEmpty(string1)) {
				validateValue = "";
			}else {
				validateValue = encoder.decodeForHTML(encoder.encodeForHTML(string1));
			}
			
			/** stripXSS */
			sc.setAttribute(name, stripXSS(validateValue));
		}else {
			sc.setAttribute(name, object);
		}
	}	
	
	
	/**
	 * Check if : has XSS Content</br>
	 * </br>
	 * @param request
	 * @return boolean
	 */
	public boolean hasXSSContent(String value) {
		try {
			if(!StringUtils.isEmpty(value)){
				
				/** log_XSSUtils.debug("value : " + value); */
				
				if (value.equals(this.stripXSS(value))) {
					return false;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		/** log.error("XSS defense, parameter: " + originJson); */
		return true;
	}
	
	/**
	 * Deal with if : has XSS Content</br>
	 * </br>
	 * @param request
	 * @return String returnValue
	 */
	public String dealwithXSSContent(String value) {
		
		try {
			if(!StringUtils.isEmpty(value)){
				
				/** log_XSSUtils.debug("value : " + value); */
				
				returnValue = this.stripXSS(value);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		/** log.error("XSS defense, parameter: " + originJson); */
		return returnValue;
	}
	
	/**
	 * Check if : has XSS Content</br>
	 * </br>
	 * @param String value
	 * @return boolean
	 */
	public boolean hasXSSContent(ServletRequest request) {
		BufferedReader reader;
		StringBuilder buffer = new StringBuilder ();
		String line = null;
		String originJson = "";

		try {
			reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}

			originJson = buffer.toString();
			if(!StringUtils.isEmpty(originJson)){
				
				/** log_XSSUtils.debug("originJson.toString() : " + originJson); */
				
				if (originJson.equals(this.stripXSS(originJson))) {
					return false;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		/** log.error("XSS defense, parameter: " + originJson); */
		return true;
	}
	
	/**
	 * strip XSS
	 * 
	 * 1. Pattern.CASE_INSENSITIVE : 啟用不區分大小寫的匹配
	 * 2. Pattern.MULTILINE : 啟用多行模式
	 * 3. Pattern.DOTALL : 啟用dotall模式
	 * @param String value
	 * @return String
	 */
	public String stripXSS(String value) {
		if (value != null) {
			// NOTE: It's highly recommended to use the ESAPI library and uncomment the
			// following line to
			// Avoid : encoded attacks.
			/** value = ESAPI.encoder().canonicalize(value); */

			// Avoid : null characters
			value = value.replace("", "");

			// ########## HTML - start #############################################
			// Avoid : anything between script tags
			Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");

			// Avoid : anything in a src='...' type of expression
			scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");

			scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");

			// Remove any lonesome </script> tag
			scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");

			// Remove any lonesome <script ...> tag
			scriptPattern = Pattern.compile("<script(.*?)>",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");

			// Avoid : eval(...) expressions
			scriptPattern = Pattern.compile("eval\\((.*?)\\)",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");

			// Avoid : expression(...) expressions
			scriptPattern = Pattern.compile("expression\\((.*?)\\)",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");

			// Avoid : javascript:... expressions
			scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");

			// Avoid : vbscript:... expressions
			scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
			value = scriptPattern.matcher(value).replaceAll("");

			// Avoid : onload= expressions
			scriptPattern = Pattern.compile("onload(.*?)=",
					Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
			value = scriptPattern.matcher(value).replaceAll("");
			// ########## HTML - end #############################################

			// ########## SQL - start #############################################
			// SQL replace string
			final String replaceSql = "-Invalid SQL-";

			// Avoid : insert into 
			scriptPattern = Pattern.compile("(?i)insert\\s*(?i)into.*\\s*\\(.*\\)");
			value = scriptPattern.matcher(value).replaceAll(replaceSql);

			// Avoid : delete from
			scriptPattern = Pattern.compile("(?i)delete\\s*(?i)from\\s*");
			value = scriptPattern.matcher(value).replaceAll(replaceSql);

			// Avoid : drop table(a,b,c)
			scriptPattern = Pattern.compile("(?i)drop\\s*(?i)table\\s*");
			value = scriptPattern.matcher(value).replaceAll(replaceSql);

			// Avoid : truncate table(a,b,c)
			scriptPattern = Pattern.compile("(?i)truncate\\s*(?i)table\\s*");
			value = scriptPattern.matcher(value).replaceAll(replaceSql);

			// Avoid : update set
			/** scriptPattern = Pattern.compile("(?i)update.*(?i)set.*=");
			value = scriptPattern.matcher(value).replaceAll(replaceSql); */

			// Avoid : select from
			/** scriptPattern = Pattern.compile("(?i)select.*(?i)from\\s*");
			value = scriptPattern.matcher(value).replaceAll(replaceSql); */

			// Avoid : alert table(a,b,c)
			scriptPattern = Pattern.compile("(?i)alert\\s*(?i)table\\s*");
			value = scriptPattern.matcher(value).replaceAll(replaceSql);

			// Avoid : 
			/** scriptPattern = Pattern.compile("'\\s*(?i)and.*=");
			value = scriptPattern.matcher(value).replaceAll(replaceSql);*/

			// Avoid : 
			/** scriptPattern = Pattern.compile("'\\s*(?i)or.*=");
			value = scriptPattern.matcher(value).replaceAll(replaceSql); */

			// ########## SQL - end #############################################
		}
		return value;
	}
	
}
