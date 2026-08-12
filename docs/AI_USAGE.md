# wsCaller Guide For AI Agents

This file is written for AI coding agents and automation tools that need to use `wsCaller` correctly from Java code.

## Library Identity

- Name: `wsCaller`
- Maven coordinates: `io.github.mmilk23:wscaller:1.2.1`
- Purpose: generic synchronous SOAP calls through Apache Axis2
- Main package: `com.milklabs.wscall`
- Primary class: `SecureWebServiceCaller`

## Java Compatibility

- Current build target: JDK 25.
- Historical note: the original library was written for JDK 8.
- Do not claim Java 8 compatibility unless the Maven compiler target is changed and the full test suite passes.

## Correct Use Pattern

```java
import com.milklabs.wscall.SecureWebServiceCaller;
import com.milklabs.wscall.WebServiceException;
import org.apache.axis2.addressing.EndpointReference;

import java.util.HashMap;
import java.util.Map;

SecureWebServiceCaller caller = new SecureWebServiceCaller(
        new EndpointReference("https://example.com/soap/service"),
        "http://example.com/namespace",
        "ns"
);

Map<String, Object> params = new HashMap<>();
params.put("elementName", "elementValue");

String xml = caller.chamarWebService("OperationName", params, null, null);
```

With WS-Security UsernameToken:

```java
String xml = caller.chamarWebService(
        "OperationName",
        params,
        username,
        password
);
```

## API Contract

Constructor:

```java
SecureWebServiceCaller(EndpointReference urlServico, String namespace, String prefixo)
```

Call method:

```java
String chamarWebService(
        String metodo,
        Map<String, Object> params,
        String username,
        String password
) throws WebServiceException
```

Behavior:

- sends SOAP 1.2;
- performs one synchronous request;
- returns raw XML as `String`;
- wraps `AxisFault` and unexpected exceptions in `WebServiceException`;
- adds WS-Security only when `username != null && password != null`;
- skips request parameters whose value is `null`;
- cleans up Axis2 transport resources after each call.

## Parameter Serialization Rules

Important: preserve these rules when generating code.

| Java value in `params` | XML behavior |
| --- | --- |
| `null` | Parameter is omitted. |
| `String` | Parameter element receives text content. |
| `HashMap` | Parameter becomes nested map entries. |
| `Hashtable` | Parameter becomes nested map entries. |
| Other scalar types | Parameter element is created without text content by the current implementation. |

Therefore:

- Convert numbers, booleans, dates, enums, and IDs to `String` before putting them in `params`.
- Use parameter names exactly as required by the SOAP operation.
- Do not assume WSDL parsing or Java type generation exists.
- Do not send JSON.
- Do not expect the library to deserialize the response.

Good:

```java
params.put("customerId", String.valueOf(customerId));
params.put("active", Boolean.toString(active));
params.put("referenceDate", date.format(DateTimeFormatter.ISO_LOCAL_DATE));
```

Avoid:

```java
params.put("customerId", customerId);
params.put("active", active);
params.put("referenceDate", localDate);
```

## Response Handling

`chamarWebService` returns XML. Generated code should parse XML with a real XML parser.

Security recommendations:

- disable external DTD/entity access when supported by the parser;
- avoid regex for XML;
- handle namespaces explicitly.

## Error Handling Pattern

```java
try {
    String xml = caller.chamarWebService(operation, params, username, password);
    return parseResponse(xml);
} catch (WebServiceException e) {
    Throwable cause = e.getCause();
    throw new IllegalStateException(
            "SOAP call failed: " + (cause != null ? cause.getMessage() : e.getMessage()),
            e
    );
}
```

## Known Constraints

- Synchronous only.
- SOAP 1.2 only in the current implementation.
- Internal timeout is `600` milliseconds.
- No automatic WSDL introspection.
- No typed request/response model generation.
- No automatic response parsing.
- Map serialization creates a repeated wrapper element before `entry/key/value`.

## Files To Inspect

- Main implementation: `src/main/java/com/milklabs/wscall/SecureWebServiceCaller.java`
- Exception type: `src/main/java/com/milklabs/wscall/WebServiceException.java`
- Example: `src/main/java/com/milklabs/exemplo/ExemploChamadaWS.java`
- User documentation: `docs/API.md`
