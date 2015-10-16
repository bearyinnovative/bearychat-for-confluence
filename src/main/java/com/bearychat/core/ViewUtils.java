package com.bearychat.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class ViewUtils {

	private static final int DEFAULT_LENGTH = 256;
	private static final int DEFAULT_LINE   = 5;

	public static String limitLength(String txt){
		return ViewUtils.limitLength(txt, DEFAULT_LENGTH);
	}
	
	/**
	 * @param message
	 * @param length
	 * @return
	 */
	public static String limitLength(String txt, int length){
		if(StringUtils.isBlank(txt) || length <= 3){
			return "";
		}
		
		int originLength = txt.length();
		if(originLength <= length){
			return txt;
		}
		
		txt = txt.substring(0, length);
		return txt + "...";
	}

	/**
	 * Confluence text without markup, empty line will fill lines, so we have to trim empty lines.
	 * @param txt
	 * @param lineNumber
	 * @return
	 */
	public static String limitLines(String txt, int lineNumber) {
		String[] arrs = txt.replace("\r\n", "\n").split("\n");
		List<String> lines = new ArrayList<String>();
		for(String item : arrs){
			if(!StringUtils.isBlank(item)){
				lines.add(item);
			}
		}
		arrs = lines.toArray(new String[lines.size()]);

		boolean truncated = true;
		if (arrs.length <= lineNumber) {
			lineNumber = arrs.length;
			truncated = false;
		}

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < lineNumber - 1; i++) {
			String oneLine = arrs[i];
			sb.append(oneLine).append("\n");
		}
		sb.append(arrs[lineNumber - 1]);

		if(truncated){
			sb.append("...");
		}
		return sb.toString();
	}

	public static String limitLines(String txt) {
		return limitLines(txt, DEFAULT_LINE);
	}

	public static String formatMessage(String message){
		if(StringUtils.isBlank(message)){
			return "";
		}else{
			message = limitLines(message);
			message = limitLength(message);
			return message;
		}
	}
}
