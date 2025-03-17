package com.milklabs.wscall;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebServiceExceptionTest {

    @Test
    void testDefaultConstructor() {
        WebServiceException exception = new WebServiceException();
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMessageConstructor() {
        String message = "Test WebServiceException";
        WebServiceException exception = new WebServiceException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testCauseConstructor() {
        Throwable cause = new RuntimeException("Underlying cause");
        WebServiceException exception = new WebServiceException(cause);
        
        assertEquals(cause, exception.getCause());
        assertEquals("java.lang.RuntimeException: Underlying cause", exception.getMessage());
    }

    @Test
    void testMessageAndCauseConstructor() {
        String message = "Test error message";
        Throwable cause = new RuntimeException("Underlying cause");
        
        WebServiceException exception = new WebServiceException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testExceptionConstructor() {
        Exception originalException = new Exception("Original exception");
        
        WebServiceException exception = new WebServiceException(originalException);
        
        assertEquals(originalException, exception.getCause());
        assertEquals("java.lang.Exception: Original exception", exception.getMessage());
    }
}
