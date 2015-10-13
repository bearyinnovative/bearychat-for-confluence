package com.bearychat.core;

import org.apache.commons.lang.StringUtils;

public class ViewUtils {
	
	private static final int DEFAULT_LENGTH = 1024;

	public static String formatMessage(String message){
		return ViewUtils.formatMessage(message, DEFAULT_LENGTH);
	}
	
	/**
	 * @param message
	 * @param length
	 * @return
	 */
	public static String formatMessage(String message, int length){
		if(StringUtils.isBlank(message) || length <= 3){
			return "";
		}
		
		int originLength = message.length();
		if(originLength <= length){
			return message;
		}
		
		message = message.substring(0, length);
		return message + "...";
	}
}
