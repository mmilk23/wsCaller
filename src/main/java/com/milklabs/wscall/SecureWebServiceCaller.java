package com.milklabs.wscall;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMText;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.log4j.Logger;

import com.milklabs.wscall.util.StringUtils;

/**
 * Implements generic calls to any webservice, whitout generate stubs o any
 * other bullshit
 * 
 * @author mmilk23
 * 
 */
public class SecureWebServiceCaller {

	private static final Logger log = Logger.getLogger(SecureWebServiceCaller.class);
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
		log.debug("[SecureWebServiceCaller] constructor called, urlServico: [" + urlServico + "] namespace: ["
				+ namespace + "] prefixo: [" + prefixo + "]");
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

	/**
	 * Chamada sincrona a um Web Service
	 * 
	 * @param metodo
	 * @param params
	 * @return OMElement
	 * @throws WebServiceException
	 */
	@SuppressWarnings("rawtypes")
	protected OMElement chamarSincronoWS(String metodo, Map<String, ?> params, String wsUsername, String wsPassword)
			throws WebServiceException {
		log.debug("[chamarSincronoWS] chamado, EndpointReference: [" + urlServico + "] namespace: [" + namespace
				+ "] prefixo: [" + prefixo + "] metodo: [" + metodo + "] params: [" + params + "] username: ["
				+ wsUsername + "] password: [" + wsPassword + "]");

		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(namespace, prefixo);

		// composicao da requisicao: nome do metodo do WS a ser chamado
		OMElement method = fac.createOMElement(metodo, omNs);

		// criacao dos parametros
		if (params != null) {
			for (Map.Entry<String, ?> entry : params.entrySet()) {
				log.debug("[chamarSincronoWS]  entry: " + entry.getKey() + " value: " + entry.getValue() + " type: "
						+ entry.getValue() != null ? "null" : entry.getValue().getClass());

				// nothing to do with this parameter
				if (entry.getValue() == null)
					continue;

				OMElement arg = fac.createOMElement(entry.getKey(), null);
				if (entry.getValue().getClass().isInstance("")) {
					String value = (String) entry.getValue();
					arg.addChild(fac.createOMText(arg, value));
				}

				if (entry.getValue().getClass().isInstance(new java.util.HashMap())
						|| entry.getValue().getClass().isInstance(new java.util.Hashtable())) {
					OMElement arg2 = fac.createOMElement(entry.getKey(), null);

					@SuppressWarnings("unchecked")
					Map<String, Object> listaParams = (Map<String, Object>) entry.getValue();
					for (Map.Entry<String, Object> param : listaParams.entrySet()) {

						Object valor = param.getValue();
						String value = StringUtils.toString(valor);

						OMElement entryEntry = fac.createOMElement("entry", null);

						OMElement entryKey = fac.createOMElement("key", null);
						entryKey.addChild(fac.createOMText(entryKey, param.getKey()));

						OMElement entryValue = fac.createOMElement("value", null);
						entryValue.addChild(fac.createOMText(entryValue, value));

						entryEntry.addChild(entryKey);
						entryEntry.addChild(entryValue);

						arg2.addChild(entryEntry);
						arg.addChild(arg2);

					}
				}
				method.addChild(arg);
			}
		}

		// especifica opcoes de envio
		Options options = new Options();
		options.setSoapVersionURI(Constants.URI_SOAP12_ENV);
		options.setTo(urlServico);
		options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
		options.setProperty(HTTPConstants.MC_ACCEPT_GZIP, Boolean.FALSE);
		options.setProperty(HTTPConstants.MC_GZIP_REQUEST, Boolean.FALSE);
		options.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, Boolean.FALSE);
		options.setTimeOutInMilliSeconds(timeoutHttp);

		/*
		 * adicionando headers http if (username != null) { List<Header> headers = new
		 * ArrayList<Header>(); headers.add(new Header("chaveDelegada", username));
		 * options.setProperty(HTTPConstants.HTTP_HEADERS, headers); }
		 */

		ServiceClient sender = null;

		// enviando!!!
		OMElement result = null;
		try {
			sender = new ServiceClient();
			// criando headers de seguranca
			OMNamespace omNsWsse = fac.createOMNamespace(
					"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "wsse");
			SOAPFactory soap12Factory = OMAbstractFactory.getSOAP12Factory();

			if (null != wsUsername && null != wsPassword) {
				log.debug("[chamarSincronoWS] adicionando Username [" + wsUsername + "] password [" + wsPassword
						+ "] ao header");

				SOAPHeaderBlock soapHeaderBlock = soap12Factory.createSOAPHeaderBlock("Security", omNsWsse);
				soapHeaderBlock.setMustUnderstand(true);
				OMElement usernameTokenElement = fac.createOMElement("UsernameToken", omNsWsse);

				// create the username element
				OMElement usernameElement = fac.createOMElement("Username", omNsWsse);
				OMText omTextUsername = fac.createOMText(usernameElement, wsUsername);
				usernameElement.addChild(omTextUsername);

				// create the password element
				OMElement passwordElement = fac.createOMElement("Password", omNsWsse);
				OMText omTextPassword = fac.createOMText(passwordElement, wsPassword);
				passwordElement.addChild(omTextPassword);

				usernameTokenElement.addChild(usernameElement);
				usernameTokenElement.addChild(passwordElement);
				soapHeaderBlock.addChild(usernameTokenElement);
				sender.addHeader(soapHeaderBlock);
			}

			sender.setOptions(options);

			result = sender.sendReceive(method);
			ByteArrayOutputStream xmlOut = new ByteArrayOutputStream();
			result.serialize(xmlOut, true);
			log.info("[chamarSincronoWS] resposta XML: \n" + xmlOut);
			xmlOut.close();
		} catch (AxisFault e) {
			if (((!"activityAbort".equals(metodo)) && (e.getMessage().contains("is not running")
					|| e.getMessage().contains("nao esta em execucao")))) {
				log.fatal("[chamarSincronoWS] AxisFault: ", e);
				throw new WebServiceException(e);
			}
		} catch (FactoryConfigurationError e) {
			log.fatal("[chamarSincronoWS] FactoryConfigurationError : ", e);
			throw new WebServiceException(e);
		} catch (IOException e) {
			log.fatal("[chamarSincronoWS] IOException: ", e);
			throw new WebServiceException(e);
		} finally {
			try {
				sender.cleanup();
				sender.cleanupTransport();
			} catch (AxisFault e) {
				log.fatal("[chamarSincronoWS] AxisFault: ", e);
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
