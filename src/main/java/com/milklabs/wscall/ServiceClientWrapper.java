package com.milklabs.wscall;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.ServiceClient;

public class ServiceClientWrapper {

    private final ServiceClient serviceClient;

    // Construtor padr�o
    public ServiceClientWrapper() throws AxisFault {
        this.serviceClient = new ServiceClient();
    }

    // M�todo sendReceive encapsula a l�gica de ServiceClient
    public OMElement sendReceive(OMElement request) throws AxisFault {
        return serviceClient.sendReceive(request);
    }

    // M�todos auxiliares para limpeza
    public void cleanup() throws AxisFault {
        serviceClient.cleanup();
    }

    public void cleanupTransport() throws AxisFault {
        serviceClient.cleanupTransport();
    }
}
