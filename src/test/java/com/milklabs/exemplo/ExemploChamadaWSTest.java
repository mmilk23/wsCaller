package com.milklabs.exemplo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.milklabs.exemplo.vo.CepVO;
import com.milklabs.wscall.SecureWebServiceCaller;

class ExemploChamadaWSTest {

    @BeforeEach
    void setup() {
        // Configuração inicial
    }

    @Test
    void testMainSuccess() throws Exception {
        // Simulação manual do comportamento de SecureWebServiceCaller
        SecureWebServiceCaller mockWsCaller = mock(SecureWebServiceCaller.class);
        when(mockWsCaller.chamarWebService(eq("consultaCEP"), anyMap(), isNull(), isNull()))
                .thenReturn("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                        "<soap:Body>" +
                        "<ns2:consultaCEPResponse xmlns:ns2=\"http://cliente.bean.master.sigep.bsb.correios.com.br/\">" +
                        "<return>" +
                        "<bairro>Ipanema</bairro>" +
                        "<cep>22410030</cep>" +
                        "<cidade>Rio de Janeiro</cidade>" +
                        "<complemento2></complemento2>" +
                        "<end>Praça Nossa Senhora da Paz</end>" +
                        "<uf>RJ</uf>" +
                        "</return>" +
                        "</ns2:consultaCEPResponse>" +
                        "</soap:Body>" +
                        "</soap:Envelope>");

        // Chamando o método principal
        ExemploChamadaWS.main(new String[]{});
    }

    @Test
    void testParseResponseValidXML() throws Exception {
    	String xmlResponse = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<ns2:consultaCEPResponse xmlns:ns2=\"http://cliente.bean.master.sigep.bsb.correios.com.br/\">" +
                "<return>" +
                "<bairro>Ipanema</bairro>" +
                "<cep>22410030</cep>" +
                "<cidade>Rio de Janeiro</cidade>" +
                "<complemento2></complemento2>" +
                "<end>Praça Nossa Senhora da Paz</end>" +
                "<uf>RJ</uf>" +
                "</return>" +
                "</ns2:consultaCEPResponse>" +
                "</soap:Body>" +
                "</soap:Envelope>";

        Method method = ExemploChamadaWS.class.getDeclaredMethod("parseResponse", String.class);
        method.setAccessible(true);

        CepVO result = (CepVO) method.invoke(null, xmlResponse);

        assertNotNull(result);
        assertEquals("Ipanema", result.getBairro());
        assertEquals("22410030", result.getCep());
        assertEquals("Rio de Janeiro", result.getCidade());
        assertEquals("Praça Nossa Senhora da Paz", result.getEnd());
        assertEquals("RJ", result.getUf());
    }

    @Test
    void testLoadValidFile() throws Exception {
    	 String path = "src/test/resources/log4j.properties";
    	    Properties props = new Properties();

    	    try (InputStream inputStream = new FileInputStream(path)) {
    	        props.load(inputStream);
    	    }

    	    assertEquals("INFO, stdout", props.getProperty("log4j.rootLogger"));
    }

    @Test
    void testLoadInvalidFile() throws Exception {
        Method method = ExemploChamadaWS.class.getDeclaredMethod("load", String.class);
        method.setAccessible(true); // Tornar o método acessível

        assertThrows(Exception.class, () -> method.invoke(null, "invalid"));
    }
}
