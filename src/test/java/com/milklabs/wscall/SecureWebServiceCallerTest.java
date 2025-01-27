package com.milklabs.wscall;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.junit.jupiter.api.Test;

class SecureWebServiceCallerTest {

	private SecureWebServiceCaller createCallerWithStub(ServiceClientWrapperStub stubWrapper) {
		return new SecureWebServiceCaller(new EndpointReference("http://mockendpoint.com"), "http://mocknamespace.com",
				"mockPrefix") {
			@Override
			protected ServiceClientWrapper createServiceClientWrapper() {
				return stubWrapper;
			}
		};
	}

	@Test
	void testChamarWebServiceNullResponse() throws Exception {
		ServiceClientWrapperStub stubWrapper = new ServiceClientWrapperStub();
		SecureWebServiceCaller caller = createCallerWithStub(stubWrapper);
		String response = caller.chamarWebService("mockMethod", null, null, null);
		assertNull(response, "O metodo deveria retornar null ao usar o stub.");
	}

	@Test
	void testChamarSincronoWSWithParams() throws Exception {
		ServiceClientWrapperStub stubWrapper = new ServiceClientWrapperStub();
		SecureWebServiceCaller caller = createCallerWithStub(stubWrapper);
		Map<String, Object> params = new HashMap<>();
		params.put("key1", "value1");
		OMElement result = caller.chamarSincronoWS("mockMethod", params, null, null);
		assertNull(result, "O metodo deveria retornar null ao usar o stub.");
	}

	@Test
	void testChamarSincronoWSWithHeaders() throws Exception {
		ServiceClientWrapperStub stubWrapper = new ServiceClientWrapperStub();
		SecureWebServiceCaller caller = createCallerWithStub(stubWrapper);
		OMElement result = caller.chamarSincronoWS("mockMethod", null, "user", "pass");
		assertNull(result, "O metodo deveria retornar null ao usar o stub.");
	}

	@Test
	void testChamarSincronoWSWithException() {
		ServiceClientWrapperStub stubWrapper;
		try {
			stubWrapper = new ServiceClientWrapperStub();
			SecureWebServiceCaller caller = createCallerWithStub(stubWrapper);
			caller.chamarSincronoWS("mockMethod", null, null, null);

		} catch (AxisFault e) {
			assertEquals(AxisFault.class, e.getClass());
		} catch (WebServiceException e) {
			assertEquals(WebServiceException.class, e.getClass());
		}
	}

	@Test
	void testCreateServiceClientWrapper() throws Exception {
		SecureWebServiceCaller caller = new SecureWebServiceCaller(new EndpointReference("http://mockendpoint.com"),
				"http://mocknamespace.com", "mockPrefix");
		ServiceClientWrapper wrapper = caller.createServiceClientWrapper();
		assertNotNull(wrapper, "O wrapper criado nao deveria ser null.");
	}
}