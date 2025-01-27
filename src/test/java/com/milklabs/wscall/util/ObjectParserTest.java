package com.milklabs.wscall.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.jupiter.api.Test;

class ObjectParserTest {

	@Test
	void testParseNumberDefaultPattern() throws ParseException {
		Number number = ObjectParser.parseNumber("1234.56", Locale.US);
		assertEquals(1234.56, number.doubleValue());
	}

	@Test
	void testParseNumberCustomPattern() throws ParseException {
		Number number = ObjectParser.parseNumber("#,##0.00", "1.234,56", Locale.GERMANY);
		assertEquals(1234.56, number.doubleValue());
	}

	@Test
	void testParseListGeneric() {
		List<String> input = Arrays.asList("one", "two", "three");
		List<String> result = ObjectParser.parseList(String.class, input);
		assertEquals(input, result);
	}

	@Test
	void testParseListFromSet() {
		Set<String> input = new HashSet<>(Arrays.asList("one", "two", "three"));
		List<String> result = ObjectParser.parseList(input);
		assertTrue(result.containsAll(input));
	}

	@Test
	void testToString() {
		assertEquals("test", ObjectParser.toString("test"));
		assertEquals("1,2,3", ObjectParser.toString(new Object[] { 1, 2, 3 }));
		assertEquals("", ObjectParser.toString(null));
		Object customObject = new Object() {
	        @Override
	        public String toString() {
	            return "CustomToString";
	        }
	    };
	    assertEquals("CustomToString", ObjectParser.toString(customObject));
	}

	@Test
	void testParseLong() {
		assertEquals(123L, ObjectParser.parseLong(123L));
		assertEquals(0L, ObjectParser.parseLong(null));
		assertEquals(123L, ObjectParser.parseLong("123", 0L));
		assertEquals(0L, ObjectParser.parseLong("1A2", 0L));
	}

	@Test
	void testParseLongWrapper() {
		assertEquals(Long.valueOf(123), ObjectParser.parseLongWrapper(123));
		assertNull(ObjectParser.parseLongWrapper(null));
		assertEquals(Long.valueOf(123), ObjectParser.parseLongWrapper("123"));
	}

	@Test
	void testParseInt() {
		assertEquals(123, ObjectParser.parseInt(123));
		assertEquals(0, ObjectParser.parseInt(null));
		assertEquals(123, ObjectParser.parseInt("123", 0));
		assertEquals(0, ObjectParser.parseInt("1A2", 0));
	}

	@Test
	void testParseInteger() {
		assertEquals(Integer.valueOf(123), ObjectParser.parseInteger(123));
		assertNull(ObjectParser.parseInteger(null));
		assertEquals(Integer.valueOf(123), ObjectParser.parseInteger("123"));
		assertEquals(123, ObjectParser.parseInteger(123L));
		assertEquals(123, ObjectParser.parseInteger(123.45));
		assertEquals(null, ObjectParser.parseInteger(""));

	}

	@Test
	void testParseDouble() {
		assertEquals(123.45, ObjectParser.parseDouble(123.45));
		assertEquals(0.0, ObjectParser.parseDouble(null));
		assertEquals(123.45, ObjectParser.parseDouble("123.45", 0.0));
		assertEquals(0.0, ObjectParser.parseDouble("12A.45", 0.0));
	}

	@Test
	void testParseDoubleWrapper() {
		assertEquals(123.0, ObjectParser.parseDoubleWrapper(123), 0.0001);
		assertEquals(123456789.0, ObjectParser.parseDoubleWrapper(123456789L), 0.0001);
		assertEquals(123.45, ObjectParser.parseDoubleWrapper(123.45f), 0.0001);
		assertEquals(12345.67, ObjectParser.parseDoubleWrapper(new BigDecimal("12345.67")), 0.0001);
		assertEquals(123.456, ObjectParser.parseDoubleWrapper(123.456), 0.0001);
		assertNull(ObjectParser.parseDoubleWrapper(null));
		Object value = new Object() {
			@Override
			public String toString() {
				return "123.45";
			}
		};
		assertEquals(123.45, ObjectParser.parseDoubleWrapper(value), 0.0001);
	}

	@Test
	void testParseBoolean() {
		assertTrue(ObjectParser.parseBoolean(true));
		assertTrue(ObjectParser.parseBoolean("true"));
		assertTrue(ObjectParser.parseBoolean("S"));
		assertTrue(ObjectParser.parseBoolean("Sim"));
		assertFalse(ObjectParser.parseBoolean(null));
		assertFalse(ObjectParser.parseBoolean("false"));
	}

	@Test
	void testParseBigDecimal() {
		BigDecimal result = ObjectParser.parseBigDecimal(1234.56);
		assertEquals(new BigDecimal("1234.56"), result);
	}

	@Test
	void testParseDate() {
		Date result = ObjectParser.parseDate("yyyy-MM-dd", "2023-01-01");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(result);
		assertEquals(2023, calendar.get(Calendar.YEAR));
		assertEquals(Calendar.JANUARY, calendar.get(Calendar.MONTH));
		assertEquals(1, calendar.get(Calendar.DAY_OF_MONTH));
	}

	@Test
	void testParseDateInvalid() {
		Date result = ObjectParser.parseDate("yyyy-MM-dd", "invalid-date");
		assertNull(result);
	}
}
