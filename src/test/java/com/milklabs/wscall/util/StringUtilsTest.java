package com.milklabs.wscall.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class StringUtilsTest {
	
    @Test
    void testToStringWithSeparatorIntArray() {
        assertEquals("1,2,3", StringUtils.toStringWithSeparator(new int[]{1, 2, 3}));
        assertEquals("", StringUtils.toStringWithSeparator(new int[]{}));
    }

    @Test
    void testToStringWithSeparatorIntArrayCustomSeparator() {
        assertEquals("1|2|3", StringUtils.toStringWithSeparator(new int[]{1, 2, 3}, "|"));
        assertEquals("", StringUtils.toStringWithSeparator(new int[]{}, "|"));
    }

    @Test
    void testToStringWithSeparatorLongArray() {
        assertEquals("1,2,3", StringUtils.toStringWithSeparator(new long[]{1L, 2L, 3L}));
        assertEquals("", StringUtils.toStringWithSeparator(new long[]{}));
    }

    @Test
    void testToStringWithSeparatorLongArrayCustomSeparator() {
        assertEquals("1|2|3", StringUtils.toStringWithSeparator(new long[]{1L, 2L, 3L}, "|"));
        assertEquals("", StringUtils.toStringWithSeparator(new long[]{}, "|"));
    }

    @Test
    void testSubstring() {
        assertEquals("Hel", StringUtils.substring("Hello", 0, 3));
        assertEquals("Hello", StringUtils.substring("Hello", 0, 10)); // endIndex maior que a string
        assertEquals("", StringUtils.substring(null, 0, 3)); // string nula
    }

    @Test
    void testRemoveLastCharacter() {
        StringBuilder sb = new StringBuilder("test,");
        assertEquals("test", StringUtils.removeLastCharacter(sb));

        sb = new StringBuilder("");
        assertEquals("", StringUtils.removeLastCharacter(sb)); // StringBuilder vazio
    }


    

    @Test
    void testIsEmptyOrNull() {
        assertTrue(StringUtils.isEmptyOrNull(null));
        assertTrue(StringUtils.isEmptyOrNull(""));
        assertFalse(StringUtils.isEmptyOrNull("test"));
    }

    @Test
    void testIsNotEmptyOrNull() {
        assertFalse(StringUtils.isNotEmptyOrNull(null));
        assertFalse(StringUtils.isNotEmptyOrNull(""));
        assertTrue(StringUtils.isNotEmptyOrNull("test"));
    }

    @Test
    void testIsEmptyTrimOrNull() {
        assertTrue(StringUtils.isEmptyTrimOrNull(null));
        assertTrue(StringUtils.isEmptyTrimOrNull("  "));
        assertFalse(StringUtils.isEmptyTrimOrNull("test"));
    }

    @Test
    void testIsNotEmptyTrimOrNull() {
        assertFalse(StringUtils.isNotEmptyTrimOrNull(null));
        assertFalse(StringUtils.isNotEmptyTrimOrNull("  "));
        assertTrue(StringUtils.isNotEmptyTrimOrNull("test"));
    }

    @Test
    void testRemoveBlankSpaces() {
        assertEquals("test", StringUtils.removeBlankSpaces(" t e s t "));
        assertEquals("", StringUtils.removeBlankSpaces(" "));
    }

    @Test
    void testToStringObject() {
        assertEquals("true", StringUtils.toString(true));
        assertEquals("false", StringUtils.toString(false));
        assertEquals("test", StringUtils.toString("test"));
    }

    @Test
    void testIsEqualsString() {
        assertTrue(StringUtils.isEquals("test", "test"));
        assertFalse(StringUtils.isEquals("test", "other"));
        assertFalse(StringUtils.isEquals(null, "test"));
    }

    @Test
    void testIsEqualsStringArray() {
        assertTrue(StringUtils.isEquals("test", new String[]{"test", "other"}));
        assertFalse(StringUtils.isEquals("test", new String[]{"other", "another"}));
    }

    @Test
    void testIsEqualsStringList() {
        assertTrue(StringUtils.isEquals("test", Arrays.asList("test", "other")));
        assertFalse(StringUtils.isEquals("test", Arrays.asList("other", "another")));
    }

    @Test
    void testTrimInsideWithOneSpace() {
        assertEquals("Hello World", StringUtils.trimInsideWithOneSpace("Hello   World"));
        assertEquals("Test", StringUtils.trimInsideWithOneSpace("   Test   "));
    }


    @Test
    void testSubstringFromFirst() {
        assertEquals("Hello", StringUtils.substringFromFirst("Hello World", " "));
        assertEquals("Hello", StringUtils.substringFromFirst("Hello", " "));
    }

    @Test
    void testSubstringFromLast() {
        assertEquals("World", StringUtils.substringFromLast("Hello World", " "));
        assertEquals("", StringUtils.substringFromLast("Hello ", " "));
        assertEquals("", StringUtils.substringFromLast(null, null));
        
    }

    @Test
    void testGetStringFromSplitPosition() {
        assertEquals("World", StringUtils.getStringFromSplitPosition("Hello World", " ", 1));
        assertEquals("", StringUtils.getStringFromSplitPosition("Hello", " ", 1));
    }

    @Test
    void testToStringWithSeparatorStringArray() {
        assertEquals("one,two,three", StringUtils.toStringWithSeparator(new String[]{"one", "two", "three"}));
        assertEquals("", StringUtils.toStringWithSeparator(new String[]{}));
    }

    @Test
    void testToStringWithSeparatorStringArrayCustomSeparator() {
        assertEquals("one|two|three", StringUtils.toStringWithSeparator(new String[]{"one", "two", "three"}, "|"));
        assertEquals("", StringUtils.toStringWithSeparator(new String[]{}, "|"));
    }


    @Test
    void testConcatsCommaOrReturnEmpty() {
        assertEquals(",test", StringUtils.concatsCommaOrReturnEmpty("test"));
        assertEquals("", StringUtils.concatsCommaOrReturnEmpty(""));
    }

    @Test
    void testDeNull() {
        assertEquals("test", StringUtils.deNull("test"));
        assertEquals("", StringUtils.deNull(null));
    }
}
