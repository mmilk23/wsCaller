# wsCaller API Guide

`wsCaller` is a small Java library for calling SOAP web services without generating a Java stub for each WSDL. The library builds a SOAP 1.2 request from a method name and a `Map<String, Object>`, sends it with Apache Axis2, and returns the SOAP response as XML text.

Use this guide when you want to call an existing SOAP endpoint from an application.

## When To Use

Use `wsCaller` when:

- the target service exposes a SOAP endpoint;
- you know the endpoint URL, namespace, operation name, and input element names;
- you want a simple synchronous call;
- the request can be represented as strings and simple map entries.

Do not use `wsCaller` as a REST/JSON client. It does not parse WSDL files, generate typed clients, or convert responses into Java objects automatically.

## Installation

Add the Maven dependency:

```xml
<dependency>
    <groupId>io.github.mmilk23</groupId>
    <artifactId>wscaller</artifactId>
    <version>1.2.1</version>
</dependency>
```

If the artifact is not available from Maven Central yet, install this project locally first:

```bash
mvn -B clean install
```

## Requirements

- JDK 25.
- Apache Maven 3.9 or newer.
- Network access to the SOAP endpoint.
- SOAP contract details from the service provider or WSDL.

## Quick Start

This example calls the public CountryInfo SOAP service and asks for Brazil's currency.

```java
import com.milklabs.wscall.SecureWebServiceCaller;
import com.milklabs.wscall.WebServiceException;
import org.apache.axis2.addressing.EndpointReference;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws WebServiceException {
        SecureWebServiceCaller caller = new SecureWebServiceCaller(
                new EndpointReference("http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso"),
                "http://www.oorsprong.org/websamples.countryinfo",
                "xs"
        );

        Map<String, Object> params = new HashMap<>();
        params.put("sCountryISOCode", "BR");

        String xml = caller.chamarWebService("CountryCurrency", params, null, null);
        System.out.println(xml);
    }
}
```

## Main Class

### `SecureWebServiceCaller`

Package:

```java
com.milklabs.wscall.SecureWebServiceCaller
```

Constructor:

```java
public SecureWebServiceCaller(
        EndpointReference urlServico,
        String namespace,
        String prefixo
)
```

Parameters:

| Parameter | Type | Description |
| --- | --- | --- |
| `urlServico` | `org.apache.axis2.addressing.EndpointReference` | SOAP endpoint URL. This is usually the endpoint address, not the WSDL URL. |
| `namespace` | `String` | XML namespace used by the SOAP operation. |
| `prefixo` | `String` | Namespace prefix used when creating the operation element. |

Example:

```java
SecureWebServiceCaller caller = new SecureWebServiceCaller(
        new EndpointReference("https://example.com/soap/service"),
        "http://example.com/contract/v1",
        "v1"
);
```

## Calling A Service

Method:

```java
public String chamarWebService(
        String metodo,
        Map<String, Object> params,
        String username,
        String password
) throws WebServiceException
```

Parameters:

| Parameter | Type | Required | Description |
| --- | --- | --- | --- |
| `metodo` | `String` | Yes | SOAP operation element name. |
| `params` | `Map<String, Object>` | No | Request parameters. Use `null` or an empty map for operations without parameters. |
| `username` | `String` | No | WS-Security username. A security header is added only when both `username` and `password` are non-null. |
| `password` | `String` | No | WS-Security password. |

Returns:

- `String` containing the XML returned by Axis2.
- `null` only if an override of the protected synchronous method returns `null`.

Throws:

- `WebServiceException` when Axis2 raises an `AxisFault` or when an unexpected error occurs during the call.

## Parameter Mapping

`wsCaller` creates the request body from `params`.

For each `params` entry:

- the map key becomes an XML element name;
- `null` values are ignored and are not sent;
- `String` values become text content;
- `HashMap` and `Hashtable` values become nested `entry/key/value` elements;
- other scalar types are not converted into text by the current implementation, so pass numbers, booleans, dates, and enums as strings.

Recommended scalar usage:

```java
Map<String, Object> params = new HashMap<>();
params.put("customerId", "12345");
params.put("active", "true");
params.put("amount", "99.90");
```

Map parameter usage:

```java
Map<String, Object> filters = new HashMap<>();
filters.put("status", "ACTIVE");
filters.put("region", "BR");

Map<String, Object> params = new HashMap<>();
params.put("filters", filters);
```

The map parameter is serialized with entries similar to:

```xml
<filters>
    <filters>
        <entry>
            <key>status</key>
            <value>ACTIVE</value>
        </entry>
        <entry>
            <key>region</key>
            <value>BR</value>
        </entry>
    </filters>
</filters>
```

## WS-Security Username And Password

When both `username` and `password` are provided, `wsCaller` adds a SOAP 1.2 WS-Security `Security` header with `UsernameToken`, `Username`, and `Password`.

```java
String xml = caller.chamarWebService(
        "ConsultarCliente",
        params,
        System.getenv("SOAP_USERNAME"),
        System.getenv("SOAP_PASSWORD")
);
```

When either value is `null`, no security header is added.

## Reading The Response

The library returns raw XML. Parse the XML according to the service contract.

For XML parsing, prefer a namespace-aware parser and disable external entity access when possible. The sample project uses JDOM2 with external DTD access disabled.

```java
String xml = caller.chamarWebService("CountryCurrency", params, null, null);
// Parse xml into your own DTO/VO here.
```

See [ExemploChamadaWS.java](../src/main/java/com/milklabs/exemplo/ExemploChamadaWS.java) for a complete example.

## Error Handling

Wrap calls in `try/catch` and inspect the cause when needed.

```java
try {
    String xml = caller.chamarWebService("ConsultarCliente", params, username, password);
    System.out.println(xml);
} catch (WebServiceException e) {
    Throwable cause = e.getCause();
    System.err.println("SOAP call failed: " + (cause != null ? cause.getMessage() : e.getMessage()));
}
```

`AxisFault` errors are wrapped as `WebServiceException`. The implementation logs SOAP fault code and detail when Axis2 provides them.

## Public Support Classes

### `WebServiceException`

Package:

```java
com.milklabs.wscall.WebServiceException
```

Checked exception used by `SecureWebServiceCaller`.

Constructors:

```java
public WebServiceException()
public WebServiceException(Exception e)
public WebServiceException(String message)
public WebServiceException(String message, Throwable cause)
public WebServiceException(Throwable cause)
```

### `ServiceClientWrapper`

Package:

```java
com.milklabs.wscall.ServiceClientWrapper
```

Thin wrapper around Axis2 `ServiceClient`. It is mainly useful for tests and extension points.

Methods:

```java
public OMElement sendReceive(OMElement request) throws AxisFault
public void cleanup() throws AxisFault
public void cleanupTransport() throws AxisFault
```

### `ServiceClientWrapperStub`

Package:

```java
com.milklabs.wscall.ServiceClientWrapperStub
```

Simple test stub that can return a mock `OMElement` response or simulate an `AxisFault`.

## Utilities

The `com.milklabs.wscall.util` package contains small parsing and string helpers used internally by the library. They are public, but they are not required for normal SOAP calls.

Notable methods:

```java
StringUtils.isEmptyOrNull(Object value)
StringUtils.isEmptyTrimOrNull(Object value)
StringUtils.toString(Object value)
ObjectParser.parseInt(Object value)
ObjectParser.parseLong(Object value)
ObjectParser.parseDouble(Object value)
ObjectParser.parseBoolean(Object value)
ObjectParser.parseDate(String pattern, String date)
```

## Operational Notes

- Calls are synchronous.
- SOAP version is set to SOAP 1.2.
- HTTP gzip is disabled.
- HTTP client reuse is disabled.
- The internal timeout is currently `600` milliseconds.
- `ServiceClient.cleanup()` and `cleanupTransport()` are called after each request.

## Troubleshooting

| Symptom | Likely cause | What to check |
| --- | --- | --- |
| SOAP fault from server | Wrong method name, namespace, endpoint, or payload shape | Compare generated request names with the WSDL. |
| Empty values in request | Non-string scalar value passed in `params` | Convert scalar values to `String` before calling. |
| Authentication failure | Missing or incompatible WS-Security header | Pass both username and password, and confirm the service expects UsernameToken. |
| Connection timeout | Endpoint unavailable or timeout too low | Confirm endpoint reachability and service latency. |
| XML parsing error | Response namespace or shape differs from expectation | Parse by XML structure and namespace instead of fixed string matching. |
