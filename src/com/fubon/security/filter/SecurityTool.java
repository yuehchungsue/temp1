package com.fubon.security.filter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public final class SecurityTool {

	ResultSet xrs;
	public static final String PATTERNXSSSCRIPT = "<script>(.*?)</script>";
	public static final String REPLACEXSS = "-invalid xss-";

	public static final String out(Object obj) {
		if (obj == null) {
			return "";
		} else {
			String tmp = decodeForHtml(encodeForHtml(obj.toString()));
			tmp = tmp.replaceAll(PATTERNXSSSCRIPT, REPLACEXSS);
			return tmp;
		}
	}

	public static final String encodeForHtml(String source) {
		if (source == null) {
			return "";
		}
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < source.length(); i++) {
			char c = source.charAt(i);
			switch (c) {
			case '&':
				buffer.append("&amp;");
				break;
			case '<':
				buffer.append("&lt;");
				break;
			case '>':
				buffer.append("&gt;");
				break;
			case '"':
				buffer.append("&#34;");
				break;
			case '\'':
				buffer.append("&#39;");
				break;
			default:
				buffer.append(c);
			}
		}
		return buffer.toString();
	}

	public static final String decodeForHtml(String source) {
		if (source == null) {
			return "";
		}
		return source.replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">").replace("&#34;", "\"").replace("&#39;", "'");
	}

	public SecurityTool(ResultSet rs) {
		this.xrs = rs;
	}

	public int findColumn(String str) throws SQLException {
		return output(xrs.findColumn(str));
	}
	
	public boolean getBoolean(String str) throws SQLException {
		return output(xrs.getBoolean(str));
	}
	
	public String getString(int index) throws SQLException {
		return output(xrs.getString(index));
	}

	public String getString(String str) throws SQLException {
		return output(xrs.getString(str));
	}
	
	public String getLong(String str) throws SQLException {
		return output(xrs.getLong(str));
	}
	
	public Date getDate(String str) throws SQLException {
		return output(xrs.getDate(str));
	}

	public Date getTimestamp(String str) throws SQLException {
		return output(xrs.getTimestamp(str));
	}
	
	public int getInt(int index) throws SQLException {
		return output(xrs.getInt(index));
	}
	
	public int getInt(String index) throws SQLException {
		return output(xrs.getInt(index));
	}
	
	public boolean isBeforeFirst() throws SQLException {
		return xrs.isBeforeFirst();
	}

	public void beforeFirst() throws SQLException {
		xrs.beforeFirst();
	}

	public boolean last() throws SQLException {
		return xrs.last();
	}

	public int getRow() throws SQLException {
		return xrs.getRow();
	}

	public boolean absolute(int row) throws SQLException {
		return xrs.absolute(row);
	}

	public boolean next() throws SQLException {
		return xrs.next();
	}

	public void close() throws SQLException {
		xrs.close();
	}

	public double getDouble(int i) throws SQLException {
		return xrs.getDouble(i);
	}

	public boolean isAfterLast() throws SQLException {
		return xrs.isAfterLast();
	}

	public void previous() throws SQLException {
		xrs.previous();
	}

	public static String input(final String requestContent) {
		if (requestContent == null) {
			return null;
		}
		String tmp = requestContent.replace("'", "`");
		tmp = escapeHtml(tmp);
		return tmp;
	}

	public static  int input(final int requestContent) {
		return requestContent;
	}

	public static  String[] input(final String[] requestContent) {
		if (requestContent == null) {
			return new String[0];
		}
		String[] tmp = new String[requestContent.length];
		for (int i = 0; i < requestContent.length; i++) {
			tmp[i] = requestContent[i].replace("'", "`");
			tmp[i] = escapeHtml(tmp[i]);
		}
		return tmp;
	}

	/**
	 * filter rs.getString("column"),
	 */
	public static  String safeOut(final String requestContent) {
		if (requestContent == null) {
			return null;
		}
		String tmp = requestContent;
		tmp = escapeSql(tmp);
		tmp = escapeHtml(tmp);
		return tmp;
	}

	public static  String output(final String requestContent) {
		if (requestContent == null) {
			return null;
		}
		String tmp = requestContent;
		// Actually, request.getParameter("input") has done XSS escape.
		// just filter only.
		tmp = escapeHtml2(tmp);
		tmp = unescapeHtml2(tmp);

		return tmp;
	}

	public static  int output(final int requestContent) {
		return requestContent;
	}

	public static  Date output(final Date requestContent) {
		return requestContent;
	}

	public static  boolean output(final boolean requestContent) {
		return requestContent;
	}

	public static  String output(final Object requestContent) {
		return output(requestContent.toString());
	}

	private static final String escapeSql(final String requestContent) {
		if (requestContent == null) {
			return null;
		}
		return requestContent.replace("'", "`");
	}

	private static final String escapeHtml(final String source) {
		if (source == null) {
			return "";
		}
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < source.length(); i++) {
			char c = source.charAt(i);
			switch (c) {
			case '<':
				buffer.append("嚗?");
				break;
			case '>':
				buffer.append("嚗?");
				break;
			default:
				buffer.append(c);
			}
		}
		return buffer.toString();
	}

	@SuppressWarnings("unused")
	private static final String unescapeHtml(final String source) {
		if (source == null) {
			return "";
		}
		return source.replace("嚗?", "<").replace("嚗?", ">");
	}

	private static final String escapeHtml2(final String source) {
		if (source == null) {
			return "";
		}
		String html = "";
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < source.length(); i++) {
			char c = source.charAt(i);
			switch (c) {
			case '<':
				buffer.append("&lt;");
				break;
			case '>':
				buffer.append("&gt;");
				break;
			case '&':
				buffer.append("&amp;");
				break;
			case '"':
				buffer.append("&quot;");
				break;
			default:
				buffer.append(c);
			}
		}
		html = buffer.toString();
		return html;
	}

	private static final String unescapeHtml2(final String source) {
		if (source == null) {
			return "";
		}
		return source.replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&").replace("&quot;", "\"");
	}

	public ResultSet getXrs() {
		return xrs;
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
	public final static String stripXSS(String value) {
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
	
	/**
	 * Check if : has XSS Content</br>
	 * </br>
	 * @param request
	 * @return boolean
	 */
	public final static boolean hasXSSContent(String value) {
		try {
			if(!StringUtils.isEmpty(value)){
				
				/** log_XSSUtils.debug("value : " + value); */
				
				if (value.equals(stripXSS(value))) {
					return false;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		/** log.error("XSS defense, parameter: " + originJson); */
		return true;
	}
}
