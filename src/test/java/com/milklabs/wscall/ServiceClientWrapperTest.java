package com.milklabs.wscall;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.ServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServiceClientWrapperTest {
    private ServiceClient mockServiceClient;
    private ServiceClientWrapper serviceClientWrapper;

    @BeforeEach
    void setUp() {
        mockServiceClient = mock(ServiceClient.class);
        serviceClientWrapper = new ServiceClientWrapper(mockServiceClient);
    }

    @Test
    void testSendReceiveSuccess() throws AxisFault {
        OMElement mockRequest = mock(OMElement.class);
        OMElement mockResponse = mock(OMElement.class);
        
        when(mockServiceClient.sendReceive(mockRequest)).thenReturn(mockResponse);

        OMElement result = serviceClientWrapper.sendReceive(mockRequest);
        assertNotNull(result);
        assertEquals(mockResponse, result);
        
        verify(mockServiceClient, times(1)).sendReceive(mockRequest);
    }

    @Test
    void testSendReceiveThrowsAxisFault() throws AxisFault {
        OMElement mockRequest = mock(OMElement.class);
        
        when(mockServiceClient.sendReceive(mockRequest)).thenThrow(new AxisFault("Simulated AxisFault"));

        AxisFault thrown = assertThrows(AxisFault.class, () -> serviceClientWrapper.sendReceive(mockRequest));
        assertEquals("Simulated AxisFault", thrown.getMessage());
    }

    @Test
    void testCleanupSuccess() throws AxisFault {
        serviceClientWrapper.cleanup();
        verify(mockServiceClient, times(1)).cleanup();
    }

    @Test
    void testCleanupThrowsAxisFault() throws AxisFault {
        doThrow(new AxisFault("Simulated Cleanup Failure")).when(mockServiceClient).cleanup();

        AxisFault thrown = assertThrows(AxisFault.class, () -> serviceClientWrapper.cleanup());
        assertEquals("Simulated Cleanup Failure", thrown.getMessage());
    }

    @Test
    void testCleanupTransportSuccess() throws AxisFault {
        serviceClientWrapper.cleanupTransport();
        verify(mockServiceClient, times(1)).cleanupTransport();
    }

    @Test
    void testCleanupTransportThrowsAxisFault() throws AxisFault {
        doThrow(new AxisFault("Simulated Transport Cleanup Failure")).when(mockServiceClient).cleanupTransport();

        AxisFault thrown = assertThrows(AxisFault.class, () -> serviceClientWrapper.cleanupTransport());
        assertEquals("Simulated Transport Cleanup Failure", thrown.getMessage());
    }
}
