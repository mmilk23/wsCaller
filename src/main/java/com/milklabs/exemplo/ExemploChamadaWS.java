package com.milklabs.exemplo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;

import org.apache.axis2.addressing.EndpointReference;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.milklabs.exemplo.vo.CountryCurrencyVO;
import com.milklabs.wscall.SecureWebServiceCaller;
import com.milklabs.wscall.WebServiceException;

/**
 * @author mmilk23
 * 
 *         Example of use look at main method: just send a Map with parameters
 *         and get response In this example, we use a WS to get currency of a
 *         country
 * 
 *         Request example: WSDL:
 *         http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso?wsdl
 *         ENDPOINT:
 *         http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso
 * 
 *         REQUEST: 
 *         <?xml version="1.0" encoding="utf-8"?>
 *         <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
 *         	<soap:Body>
 *         	<CountryCurrency xmlns="http://www.oorsprong.org/websamples.countryinfo">
 *         		<sCountryISOCode>US</sCountryISOCode> 
 *         	</CountryCurrency> 
 *         	</soap:Body>
 *         </soap:Envelope>
 * 
 *         RESPONSE:
 *         <?xml version="1.0" encoding="utf-8"?> 
 *         <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
 *	          <soap:Body>
 *	          	<m:CountryCurrencyResponse xmlns:m="http://www.oorsprong.org/websamples.countryinfo">
 *	          	<m:CountryCurrencyResult> 
 *					<m:sISOCode>USD</m:sISOCode>
 *	          		<m:sName>Dollars</m:sName> 
 *	          	</m:CountryCurrencyResult>
 *	          </m:CountryCurrencyResponse> 
 *	          </soap:Body> 
 *         </soap:Envelope>
 */
public class ExemploChamadaWS {
	
    private static SecureWebServiceCaller wsCaller;

    public static void setWsCaller(SecureWebServiceCaller wsCaller) {
        ExemploChamadaWS.wsCaller = wsCaller;
    }

	
    
	public static void main(String[] args) {

		if (wsCaller == null) {
            wsCaller = new SecureWebServiceCaller(
                new EndpointReference("http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso"),
                "http://www.oorsprong.org/websamples.countryinfo",
                "xs"
            );
        }
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("sCountryISOCode", "BR");

		try {
			String xmlOutput = wsCaller.chamarWebService("CountryCurrency", params, null, null);
			System.out.println("xmlOutput: [" + xmlOutput + "]");
			System.out.println(parseResponse(xmlOutput).toString());
		} catch (WebServiceException e) {
			e.printStackTrace();
		}
	}

	
	private static CountryCurrencyVO parseResponse(String xmlOutput) {
	    if (xmlOutput == null || xmlOutput.trim().isEmpty()) {
	        throw new RuntimeException("Error: Empty XML response");
	    }

	    CountryCurrencyVO vo = new CountryCurrencyVO();
	    SAXBuilder builder = new SAXBuilder();
	    builder.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");

	    try {
	        Document doc = builder.build(new ByteArrayInputStream(xmlOutput.getBytes(StandardCharsets.UTF_8)));
	        Element rootElement = doc.getRootElement();

	        // If the response is wrapped in <soap:Envelope>, extract the <soap:Body>
	        Element body = rootElement.getChild("Body", rootElement.getNamespace());
	        Element contentElement = (body != null) ? body : rootElement;

	        boolean hasValidElement = extractCurrencyInfo(contentElement, vo);
	        if (!hasValidElement) {
	            throw new RuntimeException("Error: XML hasn't any content");
	        }

	    } catch (JDOMException | IOException e) {
	        throw new RuntimeException("Error parsing XML", e);
	    }

	    return vo;
	}

	/**
	 * Recursively searches for `sISOCode` and `sName` in any XML structure.
	 */
	private static boolean extractCurrencyInfo(Element element, CountryCurrencyVO vo) {
	    boolean found = false;
	    for (Element child : element.getChildren()) {
	        switch (child.getName()) {
	            case "sISOCode":
	                vo.setSISOCode(child.getValue());
	                found = true;
	                break;
	            case "sName":
	                vo.setSName(child.getValue());
	                found = true;
	                break;
	            default:
	                found |= extractCurrencyInfo(child, vo); // Recursively look inside
	        }
	    }
	    return found;
	}
}