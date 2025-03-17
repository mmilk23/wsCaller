package com.milklabs.wscall;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.ServiceClient;

public class ServiceClientWrapper {

    private final ServiceClient serviceClient;

    // Default constructor (production use)
    public ServiceClientWrapper() throws AxisFault {
        this.serviceClient = new ServiceClient();
    }

    // Constructor for testing (dependency injection)
    public ServiceClientWrapper(ServiceClient serviceClient) {
        this.serviceClient = serviceClient;
    }

    public OMElement sendReceive(OMElement request) throws AxisFault {
        return serviceClient.sendReceive(request);
    }

    public void cleanup() throws AxisFault {
        serviceClient.cleanup();
    }

    public void cleanupTransport() throws AxisFault {
        serviceClient.cleanupTransport();
    }
}
