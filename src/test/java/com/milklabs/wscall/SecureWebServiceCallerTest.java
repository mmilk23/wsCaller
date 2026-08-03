package com.milklabs.wscall;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.ServiceClient;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

/**
 * Classe de testes para SecureWebServiceCaller. Migrada para JUnit 5 + Mockito 5 (sem PowerMock).
 */
class SecureWebServiceCallerTest {

	private final EndpointReference dummyEndpoint = new EndpointReference("http://dummy");

	@Test
	void testChamarWebServiceSuccess() throws Exception {
		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMElement dummyElement = factory.createOMElement("response", null);
		dummyElement.addChild(factory.createOMText(dummyElement, "OK"));

		try (MockedConstruction<ServiceClient> mocked = Mockito.mockConstruction(ServiceClient.class,
				(mock, context) -> when(mock.sendReceive(any(OMElement.class))).thenReturn(dummyElement))) {

			SecureWebServiceCaller caller = new SecureWebServiceCaller(dummyEndpoint, "http://dummy.namespace",
					"dummyPrefix");

			Map<String, Object> params = new HashMap<>();
			params.put("paramString", "value");

			Map<String, Object> innerMap = new HashMap<>();
			innerMap.put("key1", "val1");
			params.put("paramMap", innerMap);

			params.put("nullParam", null);

			String result = caller.chamarWebService("dummyMethod", params, null, null);
			assertEquals(dummyElement.toString(), result);

			ServiceClient created = mocked.constructed().get(0);

			ArgumentCaptor<OMElement> omCaptor = ArgumentCaptor.forClass(OMElement.class);
			verify(created).sendReceive(omCaptor.capture());
			OMElement sentElement = omCaptor.getValue();

			// Verifica o parâmetro string
			OMElement paramStringElement = null;
			for (Iterator<?> it = sentElement.getChildElements(); it.hasNext();) {
				OMElement child = (OMElement) it.next();
				if ("paramString".equals(child.getLocalName())) {
					paramStringElement = child;
					break;
				}
			}
			assertNotNull(paramStringElement, "Elemento 'paramString' não encontrado");
			assertEquals("value", paramStringElement.getText());

			// Verifica a estrutura aninhada do parâmetro Map (HashMap)
			OMElement outerParamMapElement = null;
			for (Iterator<?> it = sentElement.getChildElements(); it.hasNext();) {
				OMElement child = (OMElement) it.next();
				if ("paramMap".equals(child.getLocalName())) {
					outerParamMapElement = child;
					break;
				}
			}
			assertNotNull(outerParamMapElement, "Elemento 'paramMap' não encontrado");

			OMElement innerParamMapElement = outerParamMapElement.getFirstChildWithName(new QName(null, "paramMap"));
			assertNotNull(innerParamMapElement, "Elemento interno 'paramMap' não encontrado");

			OMElement entryElement = innerParamMapElement.getFirstChildWithName(new QName(null, "entry"));
			assertNotNull(entryElement, "Elemento 'entry' não encontrado em 'paramMap'");

			OMElement keyElement = entryElement.getFirstChildWithName(new QName(null, "key"));
			OMElement valueElement = entryElement.getFirstChildWithName(new QName(null, "value"));
			assertNotNull(keyElement);
			assertNotNull(valueElement);
			assertEquals("key1", keyElement.getText());
			assertEquals("val1", valueElement.getText());

			// Verifica que o parâmetro nulo não foi adicionado
			boolean nullParamFound = false;
			for (Iterator<?> it = sentElement.getChildElements(); it.hasNext();) {
				OMElement child = (OMElement) it.next();
				if ("nullParam".equals(child.getLocalName())) {
					nullParamFound = true;
					break;
				}
			}
			assertFalse(nullParamFound, "Elemento 'nullParam' não deve estar presente");

			verify(created).cleanup();
			verify(created).cleanupTransport();
		}
	}

	@Test
	void testChamarWebServiceMapParameterWithNullInnerValue() throws Exception {
		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMElement dummyElement = factory.createOMElement("response", null);
		dummyElement.addChild(factory.createOMText(dummyElement, "OK"));

		try (MockedConstruction<ServiceClient> mocked = Mockito.mockConstruction(ServiceClient.class,
				(mock, context) -> when(mock.sendReceive(any(OMElement.class))).thenReturn(dummyElement))) {

			SecureWebServiceCaller caller = new SecureWebServiceCaller(dummyEndpoint, "http://dummy.namespace",
					"dummyPrefix");

			Map<String, Object> innerMap = new HashMap<>();
			innerMap.put("nullKey", null);
			Map<String, Object> params = new HashMap<>();
			params.put("paramMap", innerMap);

			String result = caller.chamarWebService("dummyMethod", params, null, null);
			assertEquals(dummyElement.toString(), result);

			ServiceClient created = mocked.constructed().get(0);

			ArgumentCaptor<OMElement> omCaptor = ArgumentCaptor.forClass(OMElement.class);
			verify(created).sendReceive(omCaptor.capture());
			OMElement sentElement = omCaptor.getValue();

			OMElement outerParamMapElement = null;
			for (Iterator<?> it = sentElement.getChildElements(); it.hasNext();) {
				OMElement child = (OMElement) it.next();
				if ("paramMap".equals(child.getLocalName())) {
					outerParamMapElement = child;
					break;
				}
			}
			assertNotNull(outerParamMapElement, "Elemento 'paramMap' não encontrado");

			OMElement innerParamMapElement = outerParamMapElement.getFirstChildWithName(new QName(null, "paramMap"));
			assertNotNull(innerParamMapElement, "Elemento interno 'paramMap' não encontrado");

			OMElement entryElement = innerParamMapElement.getFirstChildWithName(new QName(null, "entry"));
			assertNotNull(entryElement, "Elemento 'entry' não encontrado em 'paramMap'");

			OMElement keyElement = entryElement.getFirstChildWithName(new QName(null, "key"));
			OMElement valueElement = entryElement.getFirstChildWithName(new QName(null, "value"));
			assertNotNull(keyElement);
			assertNotNull(valueElement);
			assertEquals("nullKey", keyElement.getText());

			// Aqui depende do comportamento real do StringUtils.toString(null).
			// Se ele retornar "" (como você assumiu), mantém assim:
			assertEquals("", valueElement.getText());

			ServiceClient created2 = mocked.constructed().get(0);
			verify(created2).cleanup();
			verify(created2).cleanupTransport();
		}
	}

	@Test
	void testChamarWebServiceWithSecurityHeader() throws Exception {
		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMElement dummyElement = factory.createOMElement("response", null);
		dummyElement.addChild(factory.createOMText(dummyElement, "OK"));

		try (MockedConstruction<ServiceClient> mocked = Mockito.mockConstruction(ServiceClient.class,
				(mock, context) -> when(mock.sendReceive(any(OMElement.class))).thenReturn(dummyElement))) {

			SecureWebServiceCaller caller = new SecureWebServiceCaller(dummyEndpoint, "http://dummy.namespace",
					"dummyPrefix");

			String result = caller.chamarWebService("dummyMethod", Collections.emptyMap(), "user", "pass");
			assertEquals(dummyElement.toString(), result);

			ServiceClient created = mocked.constructed().get(0);

			ArgumentCaptor<SOAPHeaderBlock> headerCaptor = ArgumentCaptor.forClass(SOAPHeaderBlock.class);
			verify(created).addHeader(headerCaptor.capture());
			SOAPHeaderBlock header = headerCaptor.getValue();
			assertNotNull(header, "O header não deve ser nulo");

			verify(created).cleanup();
			verify(created).cleanupTransport();
		}
	}

	@Test
	void testChamarWebServiceWithPartialSecurityCredentialsDoesNotAddHeader() throws Exception {
		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMElement dummyElement = factory.createOMElement("response", null);
		dummyElement.addChild(factory.createOMText(dummyElement, "OK"));

		try (MockedConstruction<ServiceClient> mocked = Mockito.mockConstruction(ServiceClient.class,
				(mock, context) -> when(mock.sendReceive(any(OMElement.class))).thenReturn(dummyElement))) {

			SecureWebServiceCaller caller = new SecureWebServiceCaller(dummyEndpoint, "http://dummy.namespace",
					"dummyPrefix");

			String result = caller.chamarWebService("dummyMethod", Collections.emptyMap(), "user", null);
			assertEquals(dummyElement.toString(), result);

			ServiceClient created = mocked.constructed().get(0);
			verify(created).cleanup();
			verify(created).cleanupTransport();
		}
	}

	@Test
	void testChamarWebServiceWithNullParams() throws Exception {
		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMElement dummyElement = factory.createOMElement("response", null);
		dummyElement.addChild(factory.createOMText(dummyElement, "OK"));

		try (MockedConstruction<ServiceClient> mocked = Mockito.mockConstruction(ServiceClient.class,
				(mock, context) -> when(mock.sendReceive(any(OMElement.class))).thenReturn(dummyElement))) {

			SecureWebServiceCaller caller = new SecureWebServiceCaller(dummyEndpoint, "http://dummy.namespace",
					"dummyPrefix");

			String result = caller.chamarWebService("dummyMethod", null, null, null);
			assertEquals(dummyElement.toString(), result);

			ServiceClient created = mocked.constructed().get(0);
			verify(created).cleanup();
			verify(created).cleanupTransport();
		}
	}

	@Test
	void testChamarWebServiceAxisFaultDetailed() throws Exception {
		OMFactory factory = OMAbstractFactory.getOMFactory();
		QName faultCode = new QName("faultCode");

		AxisFault axisFault = new AxisFault("AxisFault occurred") {
			@Override
			public QName getFaultCode() {
				return faultCode;
			}

			@Override
			public OMElement getDetail() {
				OMElement detailElement = factory.createOMElement("detail", null);
				detailElement.addChild(factory.createOMText(detailElement, "detail text"));
				return detailElement;
			}
		};

		try (MockedConstruction<ServiceClient> mocked = Mockito.mockConstruction(ServiceClient.class,
				(mock, context) -> when(mock.sendReceive(any(OMElement.class))).thenThrow(axisFault))) {

			SecureWebServiceCaller caller = new SecureWebServiceCaller(dummyEndpoint, "http://dummy.namespace",
					"dummyPrefix");

			WebServiceException ex = assertThrows(WebServiceException.class,
					() -> caller.chamarWebService("dummyMethod", Collections.emptyMap(), null, null));

			assertEquals(axisFault, ex.getCause());

			ServiceClient created = mocked.constructed().get(0);
			verify(created).cleanup();
			verify(created).cleanupTransport();
		}
	}

	@Test
	void testChamarWebServiceAxisFaultWithoutDetails() throws Exception {
		AxisFault axisFault = new AxisFault("AxisFault occurred");

		try (MockedConstruction<ServiceClient> mocked = Mockito.mockConstruction(ServiceClient.class,
				(mock, context) -> when(mock.sendReceive(any(OMElement.class))).thenThrow(axisFault))) {

			SecureWebServiceCaller caller = new SecureWebServiceCaller(dummyEndpoint, "http://dummy.namespace",
					"dummyPrefix");

			WebServiceException ex = assertThrows(WebServiceException.class,
					() -> caller.chamarWebService("dummyMethod", Collections.emptyMap(), null, null));

			assertEquals(axisFault, ex.getCause());

			ServiceClient created = mocked.constructed().get(0);
			verify(created).cleanup();
			verify(created).cleanupTransport();
		}
	}

	@Test
	void testChamarWebServiceGenericException() throws Exception {
		RuntimeException genericException = new RuntimeException("Generic error");

		try (MockedConstruction<ServiceClient> mocked = Mockito.mockConstruction(ServiceClient.class,
				(mock, context) -> when(mock.sendReceive(any(OMElement.class))).thenThrow(genericException))) {

			SecureWebServiceCaller caller = new SecureWebServiceCaller(dummyEndpoint, "http://dummy.namespace",
					"dummyPrefix");

			WebServiceException ex = assertThrows(WebServiceException.class,
					() -> caller.chamarWebService("dummyMethod", Collections.emptyMap(), null, null));

			assertEquals(genericException, ex.getCause());

			ServiceClient created = mocked.constructed().get(0);
			verify(created).cleanup();
			verify(created).cleanupTransport();
		}
	}

	@Test
	void testServiceClientConstructionFailureLeavesSenderNull() {
		RuntimeException constructionFailure = new RuntimeException("Construction failed");

		try (MockedConstruction<ServiceClient> mocked = Mockito.mockConstruction(ServiceClient.class,
				(mock, context) -> {
					throw constructionFailure;
				})) {

			SecureWebServiceCaller caller = new SecureWebServiceCaller(dummyEndpoint, "http://dummy.namespace",
					"dummyPrefix");

			WebServiceException ex = assertThrows(WebServiceException.class,
					() -> caller.chamarWebService("dummyMethod", Collections.emptyMap(), null, null));

			assertEquals("Could not initialize mocked construction", ex.getCause().getMessage());
			assertEquals(0, mocked.constructed().size());
		}
	}

	@Test
	void testCleanupExceptionHandling() throws Exception {
		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMElement dummyElement = factory.createOMElement("response", null);
		dummyElement.addChild(factory.createOMText(dummyElement, "OK"));

		try (MockedConstruction<ServiceClient> mocked = Mockito.mockConstruction(ServiceClient.class,
				(mock, context) -> {
					when(mock.sendReceive(any(OMElement.class))).thenReturn(dummyElement);
					doThrow(new AxisFault("Cleanup error")).when(mock).cleanup();
					doThrow(new AxisFault("Cleanup transport error")).when(mock).cleanupTransport();
				})) {

			SecureWebServiceCaller caller = new SecureWebServiceCaller(dummyEndpoint, "http://dummy.namespace",
					"dummyPrefix");

			String result = caller.chamarWebService("dummyMethod", Collections.emptyMap(), null, null);
			assertEquals(dummyElement.toString(), result);
		}
	}

	@Test
	void testChamarWebServiceMapParameterWithHashtable() throws Exception {
		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMElement dummyElement = factory.createOMElement("response", null);
		dummyElement.addChild(factory.createOMText(dummyElement, "OK"));

		try (MockedConstruction<ServiceClient> mocked = Mockito.mockConstruction(ServiceClient.class,
				(mock, context) -> when(mock.sendReceive(any(OMElement.class))).thenReturn(dummyElement))) {

			SecureWebServiceCaller caller = new SecureWebServiceCaller(dummyEndpoint, "http://dummy.namespace",
					"dummyPrefix");

			Hashtable<String, Object> table = new Hashtable<>();
			table.put("hashtableKey", "hashtableValue");

			Map<String, Object> params = new HashMap<>();
			params.put("paramHashtable", table);

			String result = caller.chamarWebService("dummyMethod", params, null, null);
			assertEquals(dummyElement.toString(), result);

			ServiceClient created = mocked.constructed().get(0);

			ArgumentCaptor<OMElement> omCaptor = ArgumentCaptor.forClass(OMElement.class);
			verify(created).sendReceive(omCaptor.capture());
			OMElement sentElement = omCaptor.getValue();

			OMElement outerParamElement = null;
			for (Iterator<?> it = sentElement.getChildElements(); it.hasNext();) {
				OMElement child = (OMElement) it.next();
				if ("paramHashtable".equals(child.getLocalName())) {
					outerParamElement = child;
					break;
				}
			}
			assertNotNull(outerParamElement, "Elemento 'paramHashtable' não encontrado");

			OMElement innerParamElement = outerParamElement.getFirstChildWithName(new QName(null, "paramHashtable"));
			assertNotNull(innerParamElement, "Elemento interno 'paramHashtable' não encontrado");

			OMElement entryElement = innerParamElement.getFirstChildWithName(new QName(null, "entry"));
			assertNotNull(entryElement, "Elemento 'entry' não encontrado");

			OMElement keyElement = entryElement.getFirstChildWithName(new QName(null, "key"));
			OMElement valueElement = entryElement.getFirstChildWithName(new QName(null, "value"));
			assertNotNull(keyElement);
			assertNotNull(valueElement);
			assertEquals("hashtableKey", keyElement.getText());
			assertEquals("hashtableValue", valueElement.getText());

			verify(created).cleanup();
			verify(created).cleanupTransport();
		}
	}

	@Test
	void testChamarWebServiceMapParameterMultipleEntries() throws Exception {
		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMElement dummyElement = factory.createOMElement("response", null);
		dummyElement.addChild(factory.createOMText(dummyElement, "OK"));

		try (MockedConstruction<ServiceClient> mocked = Mockito.mockConstruction(ServiceClient.class,
				(mock, context) -> when(mock.sendReceive(any(OMElement.class))).thenReturn(dummyElement))) {

			SecureWebServiceCaller caller = new SecureWebServiceCaller(dummyEndpoint, "http://dummy.namespace",
					"dummyPrefix");

			Map<String, Object> innerMap = new HashMap<>();
			innerMap.put("key1", "val1");
			innerMap.put("key2", "val2");

			Map<String, Object> params = new HashMap<>();
			params.put("mapParam", innerMap);

			String result = caller.chamarWebService("dummyMethod", params, null, null);
			assertEquals(dummyElement.toString(), result);

			ServiceClient created = mocked.constructed().get(0);

			ArgumentCaptor<OMElement> omCaptor = ArgumentCaptor.forClass(OMElement.class);
			verify(created).sendReceive(omCaptor.capture());
			OMElement sentElement = omCaptor.getValue();

			OMElement mapParamElement = null;
			for (Iterator<?> it = sentElement.getChildElements(); it.hasNext();) {
				OMElement child = (OMElement) it.next();
				if ("mapParam".equals(child.getLocalName())) {
					mapParamElement = child;
					break;
				}
			}
			assertNotNull(mapParamElement, "Elemento 'mapParam' não encontrado");

			OMElement innerParamElement = mapParamElement.getFirstChildWithName(new QName(null, "mapParam"));
			assertNotNull(innerParamElement, "Elemento interno 'mapParam' não encontrado");

			int entryCount = 0;
			for (Iterator<?> it = innerParamElement.getChildElements(); it.hasNext();) {
				OMElement child = (OMElement) it.next();
				if ("entry".equals(child.getLocalName())) {
					entryCount++;
				}
			}
			assertEquals(2, entryCount);

			verify(created).cleanup();
			verify(created).cleanupTransport();
		}
	}

	@Test
	void testChamarWebServiceReturnsNull() throws Exception {
		try (MockedConstruction<ServiceClient> mocked = Mockito.mockConstruction(ServiceClient.class,
				(mock, context) -> when(mock.sendReceive(any(OMElement.class))).thenReturn(null))) {

			SecureWebServiceCaller caller = new SecureWebServiceCaller(dummyEndpoint, "http://dummy.namespace",
					"dummyPrefix");

			WebServiceException ex = assertThrows(WebServiceException.class,
					() -> caller.chamarWebService("dummyMethod", Collections.emptyMap(), null, null));

			assertNotNull(ex.getCause());
			assertEquals(NullPointerException.class, ex.getCause().getClass());

			ServiceClient created = mocked.constructed().get(0);
			verify(created).cleanup();
			verify(created).cleanupTransport();
		}
	}

	@Test
	void testChamarWebServiceReturnsNullWhenSynchronousCallReturnsNull() throws Exception {
		SecureWebServiceCaller caller = new SecureWebServiceCaller(dummyEndpoint, "http://dummy.namespace",
				"dummyPrefix") {
			@Override
			protected OMElement chamarSincronoWS(String metodo, Map<String, ?> params, String wsUsername,
					String wsPassword) throws WebServiceException {
				return null;
			}
		};

		assertNull(caller.chamarWebService("dummyMethod", Collections.emptyMap(), null, null));
	}

	@Test
	void testChamarSincronoWS() throws Exception {
		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMElement dummyElement = factory.createOMElement("response", null);
		dummyElement.addChild(factory.createOMText(dummyElement, "OK"));

		try (MockedConstruction<ServiceClient> mocked = Mockito.mockConstruction(ServiceClient.class,
				(mock, context) -> when(mock.sendReceive(any(OMElement.class))).thenReturn(dummyElement))) {

			SecureWebServiceCaller caller = new SecureWebServiceCaller(dummyEndpoint, "http://dummy.namespace",
					"dummyPrefix");

			OMElement out = caller.chamarSincronoWS("dummyMethod", Collections.emptyMap(), "username", "password");
			assertNotNull(out);

			ServiceClient created = mocked.constructed().get(0);
			verify(created).cleanup();
			verify(created).cleanupTransport();
		}
	}
}
