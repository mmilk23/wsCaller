package com.milklabs.exemplo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.milklabs.exemplo.vo.CepVO;
import com.milklabs.wscall.SecureWebServiceCaller;
import com.milklabs.wscall.WebServiceException;

/**
 * @author mmilk23
 * 
 *         Example of use look at main method: just send a Map with parameters
 *         and get response In this example, we use a WS to get a adress based
 *         on brazilian ZIP codes
 * 
 * 
 *         Request example: WSDL:
 *         https://apps.correios.com.br/SigepMasterJPA/AtendeClienteService/AtendeCliente?wsdl
 *         ENDPOINT:
 *         https://apps.correios.com.br/SigepMasterJPA/AtendeClienteService/AtendeCliente
 * 
 *         REQUEST: <soapenv:Envelope xmlns:soapenv=
 *         "http://schemas.xmlsoap.org/soap/envelope/" xmlns:cli=
 *         "http://cliente.bean.master.sigep.bsb.correios.com.br/">
 *         <soapenv:Header/> <soapenv:Body> <cli:consultaCEP> <!--Optional:-->
 *         <cep>22410030</cep> </cli:consultaCEP> </soapenv:Body>
 *         </soapenv:Envelope>
 * 
 *         RESPONSE: <soap:Envelope xmlns:soap=
 *         "http://schemas.xmlsoap.org/soap/envelope/"> <soap:Body>
 *         <ns2:consultaCEPResponse xmlns:ns2=
 *         "http://cliente.bean.master.sigep.bsb.correios.com.br/"> <return>
 *         <bairro>Ipanema</bairro> <cep>22410030</cep> <cidade>Rio de
 *         Janeiro</cidade> <complemento2/> <end>Praça Nossa Senhora da
 *         Paz</end> <uf>RJ</uf> </return> </ns2:consultaCEPResponse>
 *         </soap:Body> </soap:Envelope> }
 */
public class ExemploChamadaWS {

	public static void main(String[] args) {

		startLog();

		SecureWebServiceCaller wsCaller = new SecureWebServiceCaller(
				new EndpointReference("https://apps.correios.com.br/SigepMasterJPA/AtendeClienteService/AtendeCliente"),
				"http://cliente.bean.master.sigep.bsb.correios.com.br/", "cli");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cep", "22410-030");

		try {
			String xmlOutput = wsCaller.chamarWebService("consultaCEP", params, null, null);
					
			//Unfortunately, the service has been showing constant errors. As a proof of concept, I mocked up the response.
		    xmlOutput = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
						 + "<soap:Body>"
						 + "   <ns2:consultaCEPResponse xmlns:ns2=\"http://cliente.bean.master.sigep.bsb.correios.com.br/\">"
						 + "      <return>"
						 + "         <bairro>Ipanema</bairro>"
						 + "         <cep>22410030</cep>"
						 + "         <cidade>Rio de Janeiro</cidade>"
						 + "         <complemento2/>"
						 + "         <end>Praça Nossa Senhora da Paz</end>"
						 + "         <uf>RJ</uf>"
						 + "      </return>"
						 + "   </ns2:consultaCEPResponse>"
						 + "</soap:Body>"
						 + "</soap:Envelope>";	
			
			
			System.out.println(parseResponse(xmlOutput).toString());
		} catch (WebServiceException e) {
			e.printStackTrace();
		}

	}

	private static CepVO parseResponse(String xmlOutput) {
		CepVO vo = new CepVO();
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		try {
			doc = builder.build(new ByteArrayInputStream(xmlOutput.getBytes("UTF-8")));

			Iterator<Content> obj = (Iterator<Content>) doc.getDescendants();
			while (obj.hasNext()) {
				Object xmlElement = obj.next();
				if (xmlElement instanceof org.jdom2.Element) {
					Element el = (Element) xmlElement;
					switch (el.getName()) {
					case "bairro":
						vo.setBairro(el.getValue());
					case "cep":
						vo.setCep(el.getValue());
					case "cidade":
						vo.setCidade(el.getValue());
					case "complemento2":
						vo.setComplemento2(el.getValue());
					case "end":
						vo.setEnd(el.getValue());
					case "uf":
						vo.setUf(el.getValue());
					default:
						break;
					}
				}
			}
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
		return vo;
	}

	private static void startLog() {

		Properties myProps;
		try {
			myProps = load("log4j");
			if (myProps.keys() != null) {
				PropertyConfigurator.configure(myProps);
			} else {
				System.out.println("WARNING: log4j.properties empty, loading BasicConfigurator...");
				BasicConfigurator.configure();
			}
		} catch (Exception e) {
			BasicConfigurator.configure();
		}
	}

	private static Properties load(String propsName) throws Exception {
		Properties props = new Properties();
		URL url = ClassLoader.getSystemResource(propsName);
		props.load(url.openStream());
		return props;
	}

}
