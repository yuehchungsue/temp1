/**異動說明
 * 202208190762-00 20220902 Jeffery Cheng 新增
 */
package com.fubon.tp.secure;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.regex.Pattern;

public class TPSecureUtilities {
	
	public static final String stripSQLInjection(String value) {
		if (value == null || value.length() == 0) return value;
		return value.replaceAll("'","’").replaceAll("\"", "”");
	}
	public static final String encodeXSS(String value) {
		if (value == null || value.length() == 0) return value;
		value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        
        return value;
	}
	public static final String stripXSS(String value) {
		if (value == null || value.length() == 0) return value;
		// Avoid anything between script tags
        Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid anything in a src='...' type of expression
        scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");
        scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        // Remove any lonesome </script> tag
        scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
        value = scriptPattern.matcher(value).replaceAll("");

        // Remove any lonesome <script ...> tag
        scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid eval(...) expressions
        scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid expression(...) expressions
        scriptPattern = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid javascript:... expressions
        scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid vbscript:... expressions
        scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid onload= expressions
        scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");
        
        //value = value.replaceAll("[<>\"'%;)(&#+{}]", "");
        value = value.replaceAll("[<>\"'%)(&#+{}]", "");
        
		return value;
	}
	
	/**
	 * Removes all unprintable characters from a string
	 * @param content
	 * @return
	 */
	public static final String getValue(String content) {
		if (content == null) return null;
		CharArrayReader reader = new CharArrayReader(content.toCharArray());
		StringWriter writer = new StringWriter();
		try {
			int c;
			while ((c = reader.read()) != -1) {
				if (isChinese((char)c)) {
					writer.write(c);
				} else {
					if ( (char)c > 0x20 && (char)c < 0x7f ) {
						writer.write(c);
					} else {
						writer.write(' ');
					}
				}
			}
		} catch (IOException e) {
			return null;
		} finally {
			reader.close();
		}
		return writer.toString();
	}
	
	private static final boolean isChinese(char c) {  
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);  
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS  
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS  
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A  
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION  
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION  
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {  
			return true;  
		}  
		return false;  
	}  


}
