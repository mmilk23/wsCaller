package com.milklabs.exemplo;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jdom2.JDOMException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.milklabs.exemplo.vo.CountryCurrencyVO;
import com.milklabs.wscall.SecureWebServiceCaller;

class ExemploChamadaWSTest {

    @BeforeEach
    void setup() {
        
    }

    @Test
    void testMainSuccess() throws Exception {
        SecureWebServiceCaller mockWsCaller = mock(SecureWebServiceCaller.class);
        when(mockWsCaller.chamarWebService(eq("CountryCurrency"), anyMap(), isNull(), isNull()))
                .thenReturn("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
	                		+ "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n"
	                		+ "  <soap:Body>\r\n"
	                		+ "    <m:CountryCurrencyResponse xmlns:m=\"http://www.oorsprong.org/websamples.countryinfo\">\r\n"
	                		+ "      <m:CountryCurrencyResult>\r\n"
	                		+ "        <m:sISOCode>USD</m:sISOCode>\r\n"
	                		+ "        <m:sName>Dollars</m:sName>\r\n"
	                		+ "      </m:CountryCurrencyResult>\r\n"
	                		+ "    </m:CountryCurrencyResponse>\r\n"
	                		+ "  </soap:Body>\r\n"
	                		+ "</soap:Envelope>");

        ExemploChamadaWS.main(new String[]{});
    }

    @Test
    void testParseResponseValidXML() throws Exception {
    	String xmlResponse = ("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
			        		+ "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n"
			        		+ "  <soap:Body>\r\n"
			        		+ "    <m:CountryCurrencyResponse xmlns:m=\"http://www.oorsprong.org/websamples.countryinfo\">\r\n"
			        		+ "      <m:CountryCurrencyResult>\r\n"
			        		+ "        <m:sISOCode>USD</m:sISOCode>\r\n"
			        		+ "        <m:sName>Dollars</m:sName>\r\n"
			        		+ "      </m:CountryCurrencyResult>\r\n"
			        		+ "    </m:CountryCurrencyResponse>\r\n"
			        		+ "  </soap:Body>\r\n"
			        		+ "</soap:Envelope>");

        Method method = ExemploChamadaWS.class.getDeclaredMethod("parseResponse", String.class);
        method.setAccessible(true);

        CountryCurrencyVO result = (CountryCurrencyVO) method.invoke(null, xmlResponse);

        assertNotNull(result);
        assertEquals("USD", result.getSISOCode());
        assertEquals("Dollars", result.getSName());
     
    }
    
    @Test
    void testParseResponseWithEmptyXML() throws Exception {
        String emptyXml = "";
        Method method = ExemploChamadaWS.class.getDeclaredMethod("parseResponse", String.class);
        method.setAccessible(true);
        Exception exception = assertThrows(InvocationTargetException.class, () -> method.invoke(null, emptyXml));

        Throwable actualCause = exception.getCause();
        assertNotNull(actualCause);
        assertTrue(actualCause instanceof RuntimeException);
        assertEquals("Error: Empty XML response", actualCause.getMessage());
    }
    
    @Test
    void testParseResponseWithMalformedXML() throws Exception {
        String malformedXml = "<soap:Envelope><soap:Body><m:CountryCurrencyResult>"; // Invalid XML

        Method method = ExemploChamadaWS.class.getDeclaredMethod("parseResponse", String.class);
        method.setAccessible(true);

        Exception exception = assertThrows(InvocationTargetException.class, () -> method.invoke(null, malformedXml));

        Throwable actualCause = exception.getCause();
        assertNotNull(actualCause);
        assertTrue(actualCause instanceof RuntimeException);
        assertTrue(actualCause.getMessage().contains("Error parsing XML"));
    }

    @Test
    void testParseResponseWithIOException() throws Exception {
        String malformedXml = "INVALID_XML"; // Simulating an I/O error

        Method method = ExemploChamadaWS.class.getDeclaredMethod("parseResponse", String.class);
        method.setAccessible(true);

        Exception exception = assertThrows(InvocationTargetException.class, () -> method.invoke(null, malformedXml));

        Throwable actualCause = exception.getCause();
        System.out.println("Actual exception type: " + (actualCause != null ? actualCause.getClass().getName() : "null"));
        assertNotNull(actualCause);
        assertTrue(
            actualCause instanceof RuntimeException ||
            actualCause instanceof IOException ||
            actualCause instanceof JDOMException,
            "Unexpected exception type: " + actualCause.getClass().getName()
        );

        assertTrue(actualCause.getMessage().contains("Error parsing XML"));
    }
    
    @Test
    void testParseResponseWithEmptyContentXML() throws Exception {
        String emptyContentXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "  <soap:Body>\n"
                + "  </soap:Body>\n"
                + "</soap:Envelope>";

        Method method = ExemploChamadaWS.class.getDeclaredMethod("parseResponse", String.class);
        method.setAccessible(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            method.invoke(null, emptyContentXml);
        });

        assertEquals("Error: XML hasn't any content", exception.getMessage());
    }



}
