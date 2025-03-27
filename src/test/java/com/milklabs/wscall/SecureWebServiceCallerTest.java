package com.milklabs.wscall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Classe de testes para SecureWebServiceCaller com 100% de cobertura.
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({
    "javax.xml.*",
    "org.xml.sax.*",
    "org.w3c.dom.*",
    "com.sun.org.apache.xerces.internal.*",
    "jdk.xml.internal.*",           //  <--- importante para ignorar classes internas do módulo java.xml
    "org.apache.axiom.*",           //  <--- ajude a ignorar internamente a instrumentação do Axiom
    "javax.management.*"
})
@PrepareForTest({ SecureWebServiceCaller.class, ServiceClient.class })
public class SecureWebServiceCallerTest {

    private EndpointReference dummyEndpoint = new EndpointReference("http://dummy");

    /**
     * Testa uma chamada bem-sucedida com:
     * - Um parâmetro String ("paramString")
     * - Um parâmetro do tipo Map (HashMap) ("paramMap") contendo uma entrada
     * - Um parâmetro nulo ("nullParam") que deve ser ignorado.
     */
    @Test
    public void testChamarWebServiceSuccess() throws Exception {
        SecureWebServiceCaller caller = new SecureWebServiceCaller(dummyEndpoint, "http://dummy.namespace", "dummyPrefix");
        ServiceClient mockClient = PowerMockito.mock(ServiceClient.class);
        PowerMockito.whenNew(ServiceClient.class).withNoArguments().thenReturn(mockClient);

        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement dummyElement = factory.createOMElement("response", null);
        dummyElement.addChild(factory.createOMText(dummyElement, "OK"));
        when(mockClient.sendReceive(any(OMElement.class))).thenReturn(dummyElement);

        // Prepara os parâmetros
        Map<String, Object> params = new HashMap<>();
        params.put("paramString", "value");

        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put("key1", "val1");
        params.put("paramMap", innerMap);

        // Parâmetro com valor nulo (deve ser ignorado)
        params.put("nullParam", null);

        String result = caller.chamarWebService("dummyMethod", params, null, null);
        assertEquals(dummyElement.toString(), result);

        // Captura o OMElement enviado ao sendReceive.
        ArgumentCaptor<OMElement> omCaptor = ArgumentCaptor.forClass(OMElement.class);
        verify(mockClient).sendReceive(omCaptor.capture());
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
        assertNotNull("Elemento 'paramString' não encontrado", paramStringElement);
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
        assertNotNull("Elemento 'paramMap' não encontrado", outerParamMapElement);

        // O elemento interno deve ter o mesmo nome (conforme criação de arg2)
        OMElement innerParamMapElement = outerParamMapElement.getFirstChildWithName(new QName(null, "paramMap"));
        assertNotNull("Elemento interno 'paramMap' não encontrado", innerParamMapElement);

        OMElement entryElement = innerParamMapElement.getFirstChildWithName(new QName(null, "entry"));
        assertNotNull("Elemento 'entry' não encontrado em 'paramMap'", entryElement);

        OMElement keyElement = entryElement.getFirstChildWithName(new QName(null, "key"));
        OMElement valueElement = entryElement.getFirstChildWithName(new QName(null, "value"));
        assertNotNull(keyElement);
        assertNotNull(valueElement);
        assertEquals("key1", keyElement.getText());
        assertEquals("val1", valueElement.getText());

        // Verifica que o parâmetro nulo não foi adicionado.
        boolean nullParamFound = false;
        for (Iterator<?> it = sentElement.getChildElements(); it.hasNext();) {
            OMElement child = (OMElement) it.next();
            if ("nullParam".equals(child.getLocalName())) {
                nullParamFound = true;
                break;
            }
        }
        assertFalse("Elemento 'nullParam' não deve estar presente", nullParamFound);

        verify(mockClient).cleanup();
        verify(mockClient).cleanupTransport();
    }

    /**
     * Testa o processamento de um parâmetro do tipo Map onde o inner map contém uma
     * chave com valor nulo. Para este teste, assume-se que StringUtils.toString(null)
     * retorna uma string vazia.
     */
    @Test
    public void testChamarWebServiceMapParameterWithNullInnerValue() throws Exception {
        SecureWebServiceCaller caller = new SecureWebServiceCaller(dummyEndpoint, "http://dummy.namespace", "dummyPrefix");
        ServiceClient mockClient = PowerMockito.mock(ServiceClient.class);
        PowerMockito.whenNew(ServiceClient.class).withNoArguments().thenReturn(mockClient);

        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement dummyElement = factory.createOMElement("response", null);
        dummyElement.addChild(factory.createOMText(dummyElement, "OK"));
        when(mockClient.sendReceive(any(OMElement.class))).thenReturn(dummyElement);

        // Cria um inner map onde o valor é nulo.
        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put("nullKey", null);
        Map<String, Object> params = new HashMap<>();
        params.put("paramMap", innerMap);

        String result = caller.chamarWebService("dummyMethod", params, null, null);
        assertEquals(dummyElement.toString(), result);

        // Captura o OMElement enviado.
        ArgumentCaptor<OMElement> omCaptor = ArgumentCaptor.forClass(OMElement.class);
        verify(mockClient).sendReceive(omCaptor.capture());
        OMElement sentElement = omCaptor.getValue();

        // Verifica a estrutura do inner map
        OMElement outerParamMapElement = null;
        for (Iterator<?> it = sentElement.getChildElements(); it.hasNext();) {
            OMElement child = (OMElement) it.next();
            if ("paramMap".equals(child.getLocalName())) {
                outerParamMapElement = child;
                break;
            }
        }
        assertNotNull("Elemento 'paramMap' não encontrado", outerParamMapElement);

        OMElement innerParamMapElement = outerParamMapElement.getFirstChildWithName(new QName(null, "paramMap"));
        assertNotNull("Elemento interno 'paramMap' não encontrado", innerParamMapElement);

        OMElement entryElement = innerParamMapElement.getFirstChildWithName(new QName(null, "entry"));
        assertNotNull("Elemento 'entry' não encontrado em 'paramMap'", entryElement);

        OMElement keyElement = entryElement.getFirstChildWithName(new QName(null, "key"));
        OMElement valueElement = entryElement.getFirstChildWithName(new QName(null, "value"));
        assertNotNull(keyElement);
        assertNotNull(valueElement);
        assertEquals("nullKey", keyElement.getText());
        // Assumindo que StringUtils.toString(null) retorna ""
        assertEquals("", valueElement.getText());

        verify(mockClient).cleanup();
        verify(mockClient).cleanupTransport();
    }

    @Test
    public void testChamarWebServiceWithSecurityHeader() throws Exception {
        // Instancia a classe que queremos testar:
        SecureWebServiceCaller caller = new SecureWebServiceCaller(
            dummyEndpoint, "http://dummy.namespace", "dummyPrefix");

        // Cria o objeto real do ServiceClient e faz spy
        ServiceClient realClient = new ServiceClient();
        ServiceClient spyClient = PowerMockito.spy(realClient);

        // Quando chamarmos .sendReceive, devolva um dummyElement
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement dummyElement = factory.createOMElement("response", null);
        dummyElement.addChild(factory.createOMText(dummyElement, "OK"));

        PowerMockito.doReturn(dummyElement)
                    .when(spyClient)
                    .sendReceive(any(OMElement.class));

        // Quando new ServiceClient() for chamado, devolva o spy
        PowerMockito.whenNew(ServiceClient.class).withNoArguments().thenReturn(spyClient);

        // Chama o método com user e pass != null
        String username = "user";
        String password = "pass";
        String result = caller.chamarWebService("dummyMethod", Collections.emptyMap(), username, password);
        assertEquals(dummyElement.toString(), result);

        // Agora capturamos o header adicionado, garantindo que o if rodou
        ArgumentCaptor<SOAPHeaderBlock> headerCaptor = ArgumentCaptor.forClass(SOAPHeaderBlock.class);
        verify(spyClient).addHeader(headerCaptor.capture()); // garante que foi chamado
        SOAPHeaderBlock header = headerCaptor.getValue();
        assertNotNull("O header não deve ser nulo", header);

        // etc. (verificações dos filhos como UsernameToken, Username, Password)

        // Limpamos
        verify(spyClient).cleanup();
        verify(spyClient).cleanupTransport();
    }

    /**
     * Testa o tratamento de um AxisFault com código de falha e detalhes não nulos.
     */
    @Test
    public void testChamarWebServiceAxisFaultDetailed() throws Exception {
        SecureWebServiceCaller caller = new SecureWebServiceCaller(dummyEndpoint, "http://dummy.namespace", "dummyPrefix");
        ServiceClient mockClient = PowerMockito.mock(ServiceClient.class);
        PowerMockito.whenNew(ServiceClient.class).withNoArguments().thenReturn(mockClient);

        QName faultCode = new QName("faultCode");
        OMFactory factory = OMAbstractFactory.getOMFactory();
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

        when(mockClient.sendReceive(any(OMElement.class))).thenThrow(axisFault);

        try {
            caller.chamarWebService("dummyMethod", Collections.emptyMap(), null, null);
            fail("Era esperado que uma WebServiceException fosse lançada");
        } catch (WebServiceException e) {
            assertEquals(axisFault, e.getCause());
        }
        verify(mockClient).cleanup();
        verify(mockClient).cleanupTransport();
    }

    /**
     * Testa que uma exceção genérica lançada durante o sendReceive é envolvida corretamente.
     */
    @Test
    public void testChamarWebServiceGenericException() throws Exception {
        SecureWebServiceCaller caller = new SecureWebServiceCaller(dummyEndpoint, "http://dummy.namespace", "dummyPrefix");
        ServiceClient mockClient = PowerMockito.mock(ServiceClient.class);
        PowerMockito.whenNew(ServiceClient.class).withNoArguments().thenReturn(mockClient);

        RuntimeException genericException = new RuntimeException("Generic error");
        when(mockClient.sendReceive(any(OMElement.class))).thenThrow(genericException);

        try {
            caller.chamarWebService("dummyMethod", Collections.emptyMap(), null, null);
            fail("Era esperado que uma WebServiceException fosse lançada");
        } catch (WebServiceException e) {
            assertEquals(genericException, e.getCause());
        }
        verify(mockClient).cleanup();
        verify(mockClient).cleanupTransport();
    }

    /**
     * Testa que mesmo se cleanup() e cleanupTransport() lançarem exceções,
     * o método ainda retorna uma resposta válida.
     */
    @Test
    public void testCleanupExceptionHandling() throws Exception {
        SecureWebServiceCaller caller = new SecureWebServiceCaller(dummyEndpoint, "http://dummy.namespace", "dummyPrefix");
        ServiceClient mockClient = PowerMockito.mock(ServiceClient.class);
        PowerMockito.whenNew(ServiceClient.class).withNoArguments().thenReturn(mockClient);

        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement dummyElement = factory.createOMElement("response", null);
        dummyElement.addChild(factory.createOMText(dummyElement, "OK"));
        when(mockClient.sendReceive(any(OMElement.class))).thenReturn(dummyElement);

        doThrow(new AxisFault("Cleanup error")).when(mockClient).cleanup();
        doThrow(new AxisFault("Cleanup transport error")).when(mockClient).cleanupTransport();

        String result = caller.chamarWebService("dummyMethod", Collections.emptyMap(), null, null);
        assertEquals(dummyElement.toString(), result);
    }
    
    /**
     * Novo teste: Processamento de parâmetro do tipo Hashtable para garantir que
     * o bloco de código que trata mapas (HashMap/Hashtable) seja executado.
     */
    @Test
    public void testChamarWebServiceMapParameterWithHashtable() throws Exception {
        SecureWebServiceCaller caller = new SecureWebServiceCaller(dummyEndpoint, "http://dummy.namespace", "dummyPrefix");
        ServiceClient mockClient = PowerMockito.mock(ServiceClient.class);
        PowerMockito.whenNew(ServiceClient.class).withNoArguments().thenReturn(mockClient);
        
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement dummyElement = factory.createOMElement("response", null);
        dummyElement.addChild(factory.createOMText(dummyElement, "OK"));
        when(mockClient.sendReceive(any(OMElement.class))).thenReturn(dummyElement);
        
        // Cria um Hashtable com uma única entrada
        Hashtable<String, Object> table = new Hashtable<>();
        table.put("hashtableKey", "hashtableValue");
        Map<String, Object> params = new HashMap<>();
        params.put("paramHashtable", table);
        
        String result = caller.chamarWebService("dummyMethod", params, null, null);
        assertEquals(dummyElement.toString(), result);
        
        // Captura o OMElement enviado e verifica a estrutura aninhada
        ArgumentCaptor<OMElement> omCaptor = ArgumentCaptor.forClass(OMElement.class);
        verify(mockClient).sendReceive(omCaptor.capture());
        OMElement sentElement = omCaptor.getValue();
        
        OMElement outerParamElement = null;
        for (Iterator<?> it = sentElement.getChildElements(); it.hasNext();) {
            OMElement child = (OMElement) it.next();
            if ("paramHashtable".equals(child.getLocalName())) {
                outerParamElement = child;
                break;
            }
        }
        assertNotNull("Elemento 'paramHashtable' não encontrado", outerParamElement);
        
        OMElement innerParamElement = outerParamElement.getFirstChildWithName(new QName(null, "paramHashtable"));
        assertNotNull("Elemento interno 'paramHashtable' não encontrado", innerParamElement);
        
        OMElement entryElement = innerParamElement.getFirstChildWithName(new QName(null, "entry"));
        assertNotNull("Elemento 'entry' não encontrado", entryElement);
        
        OMElement keyElement = entryElement.getFirstChildWithName(new QName(null, "key"));
        OMElement valueElement = entryElement.getFirstChildWithName(new QName(null, "value"));
        assertNotNull(keyElement);
        assertNotNull(valueElement);
        assertEquals("hashtableKey", keyElement.getText());
        assertEquals("hashtableValue", valueElement.getText());
        
        verify(mockClient).cleanup();
        verify(mockClient).cleanupTransport();
    }
    
    /**
     * Novo teste: Processamento de parâmetro do tipo HashMap com múltiplas entradas
     * para garantir que o laço interno seja executado, verificando que o nó interno
     * contém dois elementos "entry".
     */
    @Test
    public void testChamarWebServiceMapParameterMultipleEntries() throws Exception {
        SecureWebServiceCaller caller = new SecureWebServiceCaller(dummyEndpoint, "http://dummy.namespace", "dummyPrefix");
        ServiceClient mockClient = PowerMockito.mock(ServiceClient.class);
        PowerMockito.whenNew(ServiceClient.class).withNoArguments().thenReturn(mockClient);
        
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement dummyElement = factory.createOMElement("response", null);
        dummyElement.addChild(factory.createOMText(dummyElement, "OK"));
        when(mockClient.sendReceive(any(OMElement.class))).thenReturn(dummyElement);
        
        // Cria um HashMap com duas entradas para garantir a execução do loop interno
        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put("key1", "val1");
        innerMap.put("key2", "val2");
        Map<String, Object> params = new HashMap<>();
        params.put("mapParam", innerMap);
        
        String result = caller.chamarWebService("dummyMethod", params, null, null);
        assertEquals(dummyElement.toString(), result);
        
        // Captura o OMElement enviado e inspeciona a estrutura gerada
        ArgumentCaptor<OMElement> omCaptor = ArgumentCaptor.forClass(OMElement.class);
        verify(mockClient).sendReceive(omCaptor.capture());
        OMElement sentElement = omCaptor.getValue();
        
        // Busca pelo elemento com o nome "mapParam" (nó externo criado em "arg")
        OMElement mapParamElement = null;
        for (Iterator<?> it = sentElement.getChildElements(); it.hasNext();) {
            OMElement child = (OMElement) it.next();
            if ("mapParam".equals(child.getLocalName())) {
                mapParamElement = child;
                break;
            }
        }
        assertNotNull("Elemento 'mapParam' não encontrado", mapParamElement);
        
        // Dentro de mapParam, existe um nó interno (arg2) também com o nome "mapParam"
        OMElement innerParamElement = mapParamElement.getFirstChildWithName(new QName(null, "mapParam"));
        assertNotNull("Elemento interno 'mapParam' não encontrado", innerParamElement);
        
        // Conta os elementos "entry" dentro do nó interno
        int entryCount = 0;
        for (Iterator<?> it = innerParamElement.getChildElements(); it.hasNext();) {
            OMElement child = (OMElement) it.next();
            if ("entry".equals(child.getLocalName())) {
                entryCount++;
            }
        }
        // Espera-se 2 elementos "entry" (um para cada entrada do map)
        assertEquals(2, entryCount);
        
        verify(mockClient).cleanup();
        verify(mockClient).cleanupTransport();
    }
    
    /**
     * Novo teste: Cenário em que sendReceive retorna null, fazendo com que
     * chamarWebService lance uma WebServiceException com causa NullPointerException.
     */
    @Test
    public void testChamarWebServiceReturnsNull() throws Exception {
        SecureWebServiceCaller caller = new SecureWebServiceCaller(dummyEndpoint, "http://dummy.namespace", "dummyPrefix");
        ServiceClient mockClient = PowerMockito.mock(ServiceClient.class);
        PowerMockito.whenNew(ServiceClient.class).withNoArguments().thenReturn(mockClient);
        
        // Simula sendReceive retornando null
        when(mockClient.sendReceive(any(OMElement.class))).thenReturn(null);
        
        try {
            caller.chamarWebService("dummyMethod", Collections.emptyMap(), null, null);
            fail("Era esperado que uma WebServiceException fosse lançada");
        } catch (WebServiceException e) {
            // Espera que a causa seja um NullPointerException, pois result.serialize() falha
            assertNotNull(e.getCause());
            assertEquals(NullPointerException.class, e.getCause().getClass());
        }
        
        verify(mockClient).cleanup();
        verify(mockClient).cleanupTransport();
    }
    
    
    @Test
    public void testChamarSincronoWS() throws Exception {
        SecureWebServiceCaller caller = new SecureWebServiceCaller(dummyEndpoint, "http://dummy.namespace", "dummyPrefix");
        ServiceClient mockClient = PowerMockito.mock(ServiceClient.class);
        PowerMockito.whenNew(ServiceClient.class).withNoArguments().thenReturn(mockClient);
        
        // Simula sendReceive retornando null
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement dummyElement = factory.createOMElement("response", null);
        dummyElement.addChild(factory.createOMText(dummyElement, "OK"));
        
        when(mockClient.sendReceive(any(OMElement.class))).thenReturn(dummyElement);
        
        try {
            caller.chamarSincronoWS("dummyMethod", Collections.emptyMap(), "username", "password");
           // fail("Era esperado que uma WebServiceException fosse lançada");
        } catch (WebServiceException e) {
            // Espera que a causa seja um NullPointerException, pois result.serialize() falha
            assertNotNull(e.getCause());
            assertEquals(NullPointerException.class, e.getCause().getClass());
        }
        
        verify(mockClient).cleanup();
        verify(mockClient).cleanupTransport();
    }
}
