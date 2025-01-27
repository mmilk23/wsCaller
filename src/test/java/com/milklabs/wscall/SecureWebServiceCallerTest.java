package com.milklabs.wscall;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.junit.jupiter.api.Test;

class SecureWebServiceCallerTest {

	// Método auxiliar para criar uma instância do caller com um stub
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
		// Usar o stub no lugar do mock
		ServiceClientWrapperStub stubWrapper = new ServiceClientWrapperStub();

		// Criar o objeto SecureWebServiceCaller usando o stub
		SecureWebServiceCaller caller = createCallerWithStub(stubWrapper);

		// Executar o método a ser testado
		String response = caller.chamarWebService("mockMethod", null, null, null);

		// Validar que o método retorna null conforme esperado
		assertNull(response, "O método deveria retornar null ao usar o stub.");
	}

	@Test
	void testChamarSincronoWSWithParams() throws Exception {
		// Configuração
		ServiceClientWrapperStub stubWrapper = new ServiceClientWrapperStub();
		SecureWebServiceCaller caller = createCallerWithStub(stubWrapper);

		// Criar params simulados
		Map<String, Object> params = new HashMap<>();
		params.put("key1", "value1");

		// Executar o método
		OMElement result = caller.chamarSincronoWS("mockMethod", params, null, null);

		// Verificar o resultado
		assertNull(result, "O método deveria retornar null ao usar o stub.");
	}

	@Test
	void testChamarSincronoWSWithHeaders() throws Exception {
		// Configuração
		ServiceClientWrapperStub stubWrapper = new ServiceClientWrapperStub();
		SecureWebServiceCaller caller = createCallerWithStub(stubWrapper);

		// Executar o método com headers de segurança
		OMElement result = caller.chamarSincronoWS("mockMethod", null, "user", "pass");

		// Verificar o resultado
		assertNull(result, "O método deveria retornar null ao usar o stub.");
	}

	@Test
	void testChamarSincronoWSWithException() {
		// Configuração
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

		// Verificar que o método retorna uma instância válida
		ServiceClientWrapper wrapper = caller.createServiceClientWrapper();
		assertNotNull(wrapper, "O wrapper criado não deveria ser null.");
	}
}
