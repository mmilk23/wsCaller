package com.milklabs.wscall.util;

import java.util.List;

/**
 * 
 * @author mmilk23
 * 
 */
public abstract class StringUtils {

	public static final String EMPTY_STRING = "";

	public static final String SPACE_STRING = " ";

	public static final String COMMA_STRING = ",";

	public static final String OPEN_PARENTHESES_STRING = "(";

	public static final String CLOSE_PARENTHESES_STRING = ")";

	public static final String COMMA_SPACE_STRING = ", ";

	public static final String SPACE_COMMA_SPACE_STRING = " , ";

	public static final String DOT_STRING = ".";

	public static final String AND_SPACE_STRING = "e ";

	public static final String SPACE_AND_SPACE_STRING = " e ";

	public static final String SIMPLE_QUOTATION = "'";

	public static final String SLASH_STRING = "/";

	public static final String TRUE_STRING = "true";

	public static final String FALSE_STRING = "false";

	private StringUtils() {

	}

	/**
	 *
	 * @param value
	 * @return
	 */
	public static boolean isEmptyOrNull(Object value) {
		return value == null || ObjectParser.toString(value).equals(EMPTY_STRING);
	}

	/**
	 *
	 * @param value
	 * @return
	 */
	public static boolean isNotEmptyOrNull(Object value) {
		return !isEmptyOrNull(value);
	}

	/**
	 *
	 * @param value
	 * @return
	 */
	public static boolean isEmptyTrimOrNull(Object value) {
		return value == null || ObjectParser.toString(value).trim().equals(EMPTY_STRING);
	}

	/**
	 *
	 * @param value
	 * @return
	 */
	public static boolean isNotEmptyTrimOrNull(Object value) {
		return !isEmptyTrimOrNull(value);
	}

	/**
	 *
	 * @param string
	 * @return
	 */
	public static String removeBlankSpaces(String string) {
		return string.replaceAll(" ", EMPTY_STRING);
	}

	/**
	 *
	 * @param value
	 * @return
	 */
	public static String toString(Object value) {
		if (value instanceof Boolean) {
			return ((Boolean) value) ? "true" : "false";
		}

		return ObjectParser.toString(value);
	}

	public static boolean isEquals(String nome1, String nome2) {
		return nome1 != null && nome1.equals(nome2);
	}

	public static boolean isEquals(String nome, String[] nomes) {
		for (String s : nomes) {
			if (s.equals(nome)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isEquals(String nome, List<String> nomes) {
		for (String s : nomes) {
			if (s.equals(nome)) {
				return true;
			}
		}
		return false;
	}

	public static String trimInsideWithOneSpace(String string) {
		int amountBlanks = 0;
		StringBuilder newString = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if (c != ' ') {
				amountBlanks = 0;
			} else {
				amountBlanks += 1;
			}
			if (amountBlanks < 2) {
				newString.append(c);
			}
		}
		return newString.toString().trim();
	}

	/**
	 *
	 * @param value
	 * @param beginIndex
	 * @param endIndex
	 * @return
	 */
	public static String substring(String value, int beginIndex, int endIndex) {
		if (value != null) {
			if (value.length() < endIndex) {
				endIndex = value.length();
			}
			return value.substring(beginIndex, endIndex);
		}
		return StringUtils.EMPTY_STRING;
	}

	/**
	 *
	 */
	public static String substringFromFirst(String value, String first) {
		if (StringUtils.isNotEmptyOrNull(value)) {
			int index = value.indexOf(first);
			if (index != -1) {
				return value.substring(0, index);
			}
		}
		return value;
	}

	/**
	 *
	 */
	public static String substringFromLast(String value, String last) {
		if (StringUtils.isNotEmptyOrNull(value)) {
			int index = value.lastIndexOf(last);
			if (index != -1) {
				if (index + 1 != value.length()) {
					return value.substring(index + 1);
				}
				return StringUtils.EMPTY_STRING;
			}
		}
		return StringUtils.EMPTY_STRING;
	}

	/**
	 *
	 * @param value
	 * @param token
	 * @param position
	 * @return
	 */
	public static String getStringFromSplitPosition(String value, String token, int position) {
		String[] array = toString(value).split(token);
		String string = StringUtils.EMPTY_STRING;
		if (array.length > position) {
			string = array[position];
		}
		return string;
	}

	/**
	 * Returns a string with a comma as separator between items.
	 * 
	 * @param items
	 * @return
	 */
	public static String toStringWithSeparator(String[] items) {
		if (items == null || items.length == 0) {
			return EMPTY_STRING;
		}
		StringBuilder sb = new StringBuilder();
		for (String item : items) {
			if (isNotEmptyTrimOrNull(item)) {
				sb.append(item).append(COMMA_STRING);
			}
		}
		return removeLastCharacter(sb);
	}

	/**
	 * Returns a string with a separator between items.
	 * 
	 * @param items
	 * @param separator
	 * @return
	 */
	public static String toStringWithSeparator(String[] items, String separator) {
		if (items == null || items.length == 0) {
			return EMPTY_STRING;
		}
		StringBuilder sb = new StringBuilder();
		for (String item : items) {
			if (isNotEmptyTrimOrNull(item)) {
				sb.append(item).append(separator);
			}
		}
		return removeLastCharacter(sb);
	}

	/**
	 * Returns a string with a comma as separator between items.
	 * 
	 * @param items
	 * @return
	 */
	public static String toStringWithSeparator(int[] items) {
		if (items == null || items.length == 0) {
			return EMPTY_STRING;
		}
		StringBuilder sb = new StringBuilder();
		for (int item : items) {
			sb.append(item).append(COMMA_STRING);
		}
		return removeLastCharacter(sb);
	}

	/**
	 * Returns a string with a separator between items.
	 * 
	 * @param items
	 * @param separator
	 * @return
	 */
	public static String toStringWithSeparator(int[] items, String separator) {
		if (items == null || items.length == 0) {
			return EMPTY_STRING;
		}
		StringBuilder sb = new StringBuilder();
		for (int item : items) {
			sb.append(item).append(separator);
		}
		return removeLastCharacter(sb);
	}

	/**
	 * Returns a string with a comma as separator between items.
	 * 
	 * @param items
	 * @return
	 */
	public static String toStringWithSeparator(long[] items) {
		if (items == null || items.length == 0) {
			return EMPTY_STRING;
		}
		StringBuilder sb = new StringBuilder();
		for (long item : items) {
			sb.append(item).append(COMMA_STRING);
		}
		return removeLastCharacter(sb);
	}

	/**
	 * Returns a string with a separator between items.
	 * 
	 * @param items
	 * @param separator
	 * @return
	 */
	public static String toStringWithSeparator(long[] items, String separator) {
		if (items == null || items.length == 0) {
			return EMPTY_STRING;
		}
		StringBuilder sb = new StringBuilder();
		for (long item : items) {
			sb.append(item).append(separator);
		}
		return removeLastCharacter(sb);
	}

	/**
	 * Removes the last char of a given StringBuffer.<br>
	 * Returns the corresponding string.
	 * 
	 * @param sb
	 * @return
	 */
	public static String removeLastCharacter(StringBuilder sb) {
		return isEmptyOrNull(sb.toString()) ? StringUtils.EMPTY_STRING
				: sb.toString().substring(0, sb.toString().length() - 1);
	}

	/**
	 * 
	 * @return
	 */
	public static String concatsCommaOrReturnEmpty(String s) {
		return s != null && s.length() > 0 ? COMMA_STRING + s : EMPTY_STRING;
	}

	public static String deNull(Object str) {
		if (null != str) {
			return str.toString();
		} else {
			return "";
		}
	}
}