package com.milklabs.wscall;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.ServiceClient;

public class ServiceClientWrapper {

    private final ServiceClient serviceClient;

    // Construtor padrão
    public ServiceClientWrapper() throws AxisFault {
        this.serviceClient = new ServiceClient();
    }

    // Método sendReceive encapsula a lógica de ServiceClient
    public OMElement sendReceive(OMElement request) throws AxisFault {
        return serviceClient.sendReceive(request);
    }

    // Métodos auxiliares para limpeza
    public void cleanup() throws AxisFault {
        serviceClient.cleanup();
    }

    public void cleanupTransport() throws AxisFault {
        serviceClient.cleanupTransport();
    }
}
