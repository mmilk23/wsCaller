package com.milklabs.wscall;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;

public class ServiceClientWrapperStub extends ServiceClientWrapper {

    private boolean throwException;
    private OMElement mockResponse;

    public ServiceClientWrapperStub() throws AxisFault {
        super();
    }

    public void setThrowException(boolean throwException) {
        this.throwException = throwException;
    }

    public void setMockResponse(OMElement mockResponse) {
        this.mockResponse = mockResponse;
    }

    @Override
    public OMElement sendReceive(OMElement request) throws AxisFault {
        if (throwException) {
            throw new AxisFault("Erro simulado no sendReceive");
        }
        return mockResponse; // Retorna uma resposta simulada ou null
    }
}
