package com.objectpool.util;

/**
 * 工具方法集合
 * @author Lee
 */
public class Utils {

	public static final String lineSeparator = System.getProperty("line.separator", "\n");
	
	public static boolean equals(String s1, String s2) {
		if (s1 == null && s2 == null) {
			return true;
		} else if (s1 != null && s2 != null) {
			return s1.equals(s2);
		} else {
			return false;
		}
	}
}
