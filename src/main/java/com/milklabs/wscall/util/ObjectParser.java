package com.milklabs.wscall.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Classe responsavel por realizar diversos tipos de parses.
 * 
 * @author mmilk23
 * 
 */
public abstract class ObjectParser {

	/**
	 * 
	 */
	public static final String PATTERN_NUMBER_DEFAULT = "##0.00";

	private static final String BOOLEAN_S = "S";

	private static final String BOOLEAN_SIM = "Sim";
	
	private ObjectParser(){
		
	}

	/**
	 * 
	 * @param value
	 * @param locale
	 * @return
	 * @throws java.text.ParseException
	 */
	public static Number parseNumber(String value, Locale locale) throws ParseException {
		return parseNumber(PATTERN_NUMBER_DEFAULT, value, locale);
	}

	/**
	 *
	 * @param pattern
	 * @param value
	 * @param locale
	 * @return
	 * @throws java.text.ParseException
	 */
	public static Number parseNumber(String pattern, String value, Locale locale) throws ParseException {
		DecimalFormat format = new DecimalFormat(pattern);
		format.setDecimalFormatSymbols(new DecimalFormatSymbols(locale));
		Number number = format.parse(value);
		return number;
	}

	/**
	 * Converte uma lista de objetos nao generica para uma lista de generica
	 * 
	 * @param <E>
	 *            Representacao do objeto contido na lista nao generica
	 * @param clazz
	 *            Classe do objeto nao generico
	 * @param list
	 *            Lista de objetos
	 * @return Lista generica dos objetos contidos na lista nao generica.
	 * @exception ClassCastException
	 *                Excecao gerada caso a classe informada nao for a mesma
	 *                classe dos objetos contidos na lista de objetos nao
	 *                generica.
	 */
	public static <E> List<E> parseList(Class<E> clazz, List<E> list) throws ClassCastException {
		ArrayList<E> genericList = new ArrayList<E>(list.size());
		for (Object obj : list) {
			E element = clazz.cast(obj);
			genericList.add(element);
		}
		return genericList;
	}

	/**
	 * 
	 * @param <E>
	 * @param set
	 * @return
	 */
	public static <E> List<E> parseList(Set<E> set) {
		Iterator<E> it = set.iterator();
		ArrayList<E> list = new ArrayList<E>(set.size());
		while (it.hasNext()) {
			list.add(it.next());
		}
		return list;
	}

	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static String toString(Object obj) {

		if (obj instanceof String) {
			return (String) obj;
		}

        if (obj instanceof Object[]){

           Object[] lista = (Object[]) obj;
           if (lista.length > 0) {
              String tmp = Arrays.toString(lista);
              return tmp.replace("[","").replace("]","");
           }
        }


		if (obj == null) {
			return StringUtils.EMPTY_STRING;
		}

		return obj.toString();
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static long parseLong(Object value) {
		Long parsedValue = parseLongWrapper(value);
		if (parsedValue == null) {
			return 0;
		}
		return parsedValue;
	}

	public static Long parseLongWrapper(Object value) {
		if (value == null) {
			return null;
		} else if (value instanceof Long) {
			return (Long) value;
		} else if (value instanceof Number) {
			return ((Number) value).longValue();
		}
		return parseLong(value.toString(), 0);
	}

	/**
	 * 
	 * @param value
	 * @param defaultValue
	 * @return
	 */
	public static long parseLong(String value, long defaultValue) {
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static int parseInt(Object value) {
		Integer parsedValue = parseInteger(value);
		if (parsedValue == null) {
			return 0;
		}
		return parsedValue;
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static Integer parseInteger(Object value) {
		if (value == null) {
			return null;
		} else if (value instanceof Integer) {
			return (Integer) value;
		} else if (value instanceof Number) {
			return ((Number) value).intValue();
		} else if (value.toString().trim().length() == 0) {
			return null;
		}
		return parseInt(value.toString(), 0);
	}

	/**
	 * 
	 * @param value
	 * @param defaultValue
	 * @return
	 */
	public static int parseInt(String value, int defaultValue) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static Double parseDoubleWrapper(Object value) {
		if (value == null) {
			return null;
		} else if (value instanceof Double) {
			return (Double) value;
		} else if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}
		return parseDouble(value.toString(), 0);
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static double parseDouble(Object value) {
		Double parsedValue = parseDoubleWrapper(value);
		if (parsedValue == null) {
			return 0;
		}
		return parsedValue;
	}

	/**
	 * 
	 * @param value
	 * @param defaultValue
	 * @return
	 */
	public static double parseDouble(String value, double defaultValue) {
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * Transforma o parametro informado em um valor booleano.
	 * 
	 * @param  value Objeto a ser transformado.
	 * @return <code>True</code> se for um <em><code>boolean</code></em> =
	 *         <code>true</code> ou se o valor for uma string: 'True', 'S' ou
	 *         'Sim'.
	 */
	public static boolean parseBoolean(Object value) {
		boolean result = false;
		if (value != null) {
			if (value instanceof Boolean) {
				result = ((Boolean) value).booleanValue();
			} else {
				String bool = value.toString();
				if (bool.equalsIgnoreCase(BOOLEAN_S) || bool.equalsIgnoreCase(BOOLEAN_SIM)) {
					result = true;
				} else {
					result = Boolean.parseBoolean(bool);
				}
			}
		}
		return result;
	}

	public static BigDecimal parseBigDecimal(double value) {
		return BigDecimal.valueOf(value);
	}

	public static Date parseDate(String pattern, String date) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		try {
			return format.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}
}
