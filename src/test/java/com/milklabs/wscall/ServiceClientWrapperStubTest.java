package com.milklabs.wscall;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axis2.AxisFault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServiceClientWrapperStubTest {
    private ServiceClientWrapperStub stub;

    @BeforeEach
    void setUp() throws AxisFault {
        stub = new ServiceClientWrapperStub();
    }

    @Test
    void testSendReceiveReturnsMockResponse() throws Exception {
        // Prepare mock XML response
        String xmlResponse = "<Response><Message>Success</Message></Response>";
        XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(xmlResponse));

        // Correct Axiom 2.0.0 way to build OMElement
        OMElement mockResponse = OMXMLBuilderFactory.createStAXOMBuilder(reader).getDocumentElement();

        stub.setMockResponse(mockResponse);

        OMElement result = stub.sendReceive(null);
        assertNotNull(result);
        assertEquals("Response", result.getLocalName());
    }

    @Test
    void testSendReceiveThrowsException() {
        stub.setThrowException(true);
        AxisFault thrown = assertThrows(AxisFault.class, () -> stub.sendReceive(null));
        assertEquals("Erro simulado no sendReceive", thrown.getMessage());
    }
}
