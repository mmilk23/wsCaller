package com.milklabs.wscall;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.kernel.http.HTTPConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.milklabs.wscall.util.StringUtils;

/**
 * Implements generic calls to any webservice, whitout generate stubs o any
 * other bullshit
 * 
 * @author mmilk23
 * 
 */
public class SecureWebServiceCaller {

	private static final Logger log = LoggerFactory.getLogger(SecureWebServiceCaller.class);
	protected EndpointReference urlServico;
	protected String namespace;
	protected String prefixo;

	protected static long timeoutHttp = 600;

	/**
	 * 
	 * @param urlServico Endpoint
	 * @param namespace
	 * @param prefixo
	 */
	public SecureWebServiceCaller(EndpointReference urlServico, String namespace, String prefixo) {
		log.debug("[SecureWebServiceCaller] constructor called, urlServico: [{}] namespace: [{}] prefixo: [{}]", urlServico, namespace, prefixo);
		this.urlServico = urlServico;
		this.namespace = namespace;
		this.prefixo = prefixo;
	}

	/**
	 * Método protegido que pode ser sobrescrito em testes para substituir a criação
	 * do ServiceClientWrapper.
	 */
	protected ServiceClientWrapper createServiceClientWrapper() {
		try {
			return new ServiceClientWrapper();
		} catch (Exception e) {
			throw new RuntimeException("Erro ao criar ServiceClientWrapper", e);
		}
	}

	/*
	 * adicionando headers http if (username != null) { List<Header> headers = new
	 * ArrayList<Header>(); headers.add(new Header("chaveDelegada", username));
	 * options.setProperty(HTTPConstants.HTTP_HEADERS, headers); }
	 */

	/**
	 * Chamada sincrona a um Web Service
	 * 
	 * @param metodo
	 * @param params
	 * @return OMElement
	 * @throws WebServiceException
	 */
	@SuppressWarnings("rawtypes")

	protected OMElement chamarSincronoWS(String metodo, Map<String, ?> params, String wsUsername, String wsPassword) throws WebServiceException {
	    log.info("[chamarSincronoWS] chamado, EndpointReference: [{}] namespace: [{}] prefixo: [{}] metodo: [{}] params: {}] username: [{}] password: {}",  urlServico, namespace, prefixo, metodo, params, wsUsername, wsPassword);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = factory.createOMNamespace(namespace, prefixo);

		OMElement methodElement = factory.createOMElement(metodo, omNs);

		if (params != null) {
			for (Map.Entry<String, ?> entry : params.entrySet()) {
				log.info("[callSynchronousWS] entry: [{}] value: [{}] type: [{}]", entry.getKey(), entry.getValue(),
						(entry.getValue() != null ? entry.getValue().getClass() : "null"));

				if (entry.getValue() == null)
					continue;

				OMElement arg = factory.createOMElement(entry.getKey(), null);
				if (entry.getValue().getClass().isInstance("")) {
					String value = (String) entry.getValue();
					arg.addChild(factory.createOMText(arg, value));
				}

				if (entry.getValue().getClass().isInstance(new java.util.HashMap())
						|| entry.getValue().getClass().isInstance(new java.util.Hashtable())) {
					OMElement arg2 = factory.createOMElement(entry.getKey(), null);
					@SuppressWarnings("unchecked")
					Map<String, Object> paramList = (Map<String, Object>) entry.getValue();
					for (Map.Entry<String, Object> param : paramList.entrySet()) {
						Object valueObject = param.getValue();
						String value = StringUtils.toString(valueObject);

						OMElement entryElement = factory.createOMElement("entry", null);
						OMElement keyElement = factory.createOMElement("key", null);
						keyElement.addChild(factory.createOMText(keyElement, param.getKey()));

						OMElement valueElement = factory.createOMElement("value", null);
						valueElement.addChild(factory.createOMText(valueElement, value));

						entryElement.addChild(keyElement);
						entryElement.addChild(valueElement);
						arg2.addChild(entryElement);
						arg.addChild(arg2);
					}
				}
				methodElement.addChild(arg);
			}
		}

		Options options = new Options();
		options.setSoapVersionURI(Constants.URI_SOAP12_ENV);
		options.setTo(urlServico);
		options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
		options.setProperty(HTTPConstants.MC_ACCEPT_GZIP, Boolean.FALSE);
		options.setProperty(HTTPConstants.MC_GZIP_REQUEST, Boolean.FALSE);
		options.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, Boolean.FALSE);
		options.setTimeOutInMilliSeconds(timeoutHttp);

		ServiceClient sender = null;
		OMElement result = null;

		try {
			sender = new ServiceClient();

			OMNamespace omNsWsse = factory.createOMNamespace("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "wsse");
			SOAPFactory soap12Factory = OMAbstractFactory.getSOAP12Factory();

			if (wsUsername != null && wsPassword != null) {
				log.debug("[callSynchronousWS] adding Username [{}] password [{}] to header", wsUsername, wsPassword);

				SOAPHeaderBlock soapHeaderBlock = soap12Factory.createSOAPHeaderBlock("Security", omNsWsse);
				soapHeaderBlock.setMustUnderstand(true);
				OMElement usernameTokenElement = factory.createOMElement("UsernameToken", omNsWsse);

				OMElement usernameElement = factory.createOMElement("Username", omNsWsse);
				usernameElement.addChild(factory.createOMText(usernameElement, wsUsername));

				OMElement passwordElement = factory.createOMElement("Password", omNsWsse);
				passwordElement.addChild(factory.createOMText(passwordElement, wsPassword));

				usernameTokenElement.addChild(usernameElement);
				usernameTokenElement.addChild(passwordElement);
				soapHeaderBlock.addChild(usernameTokenElement);
				sender.addHeader(soapHeaderBlock);
			}

			sender.setOptions(options);
			result = sender.sendReceive(methodElement);

			ByteArrayOutputStream xmlOut = new ByteArrayOutputStream();
			result.serialize(xmlOut, true);
			String responseXml = xmlOut.toString(StandardCharsets.UTF_8);
			log.info("[callSynchronousWS] response XML: \n{}", responseXml);
			xmlOut.close();

		} catch (AxisFault e) {
	        log.error("[callSynchronousWS] AxisFault occurred: {}", e.getMessage(), e);
	        if (e.getFaultCode() != null) {
	            log.error("[callSynchronousWS] SOAP error code: {}", e.getFaultCode().getLocalPart());
	        }
	        if (e.getDetail() != null) {
	            log.error("[callSynchronousWS] SOAP error details: {}", e.getDetail().toString());
	        }
	        throw new WebServiceException(e);
	    } catch (Exception e) {
	        log.error("[callSynchronousWS] Unexpected exception: {}", e.getMessage(), e);
	        throw new WebServiceException(e);
	    } finally {
	        if (sender != null) {
	            try {
	                sender.cleanup();
	                sender.cleanupTransport();
	            } catch (AxisFault e) {
	                log.error("[callSynchronousWS] AxisFault while closing connection: {}", e.getMessage(), e);
	            }
	        }
	    }
	    return result;
	}

	/**
	 * Chamada sincrona a um Web Service
	 * 
	 * 
	 * @param metodo
	 * @param params
	 * @param username
	 * @return representacao XML com a resposta do servico
	 * @throws SecureWebServiceException
	 */
	public String chamarWebService(String metodo, Map<String, Object> params, String username, String password)
			throws WebServiceException {
		OMElement result = this.chamarSincronoWS(metodo, params, username, password);
		if (null == result) {
			return null;
		} else {
			return result.toString();
		}
	}

}
