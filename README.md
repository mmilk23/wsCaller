
# wsCaller

[![Build Status](https://github.com/mmilk23/wsCaller/actions/workflows/maven.yml/badge.svg)](https://github.com/mmilk23/wsCaller/actions)
[![codecov](https://codecov.io/gh/mmilk23/wsCaller/branch/main/graph/badge.svg)](https://codecov.io/gh/mmilk23/wsCaller)
[![Coverage Status](https://coveralls.io/repos/github/mmilk23/wsCaller/badge.svg)](https://coveralls.io/github/mmilk23/wsCaller)
![Dependabot](https://img.shields.io/badge/Dependabot-enabled-brightgreen)
[![Known Vulnerabilities](https://snyk.io/test/github/mmilk23/wsCaller/badge.svg)](https://snyk.io/test/github/mmilk23/wsCaller)
[![Last Updated](https://img.shields.io/github/last-commit/mmilk23/wsCaller.svg)](https://github.com/mmilk23/wsCaller/commits/main)




**wsCaller** is a lightweight Java library for making secure and generic web service calls without the need to generate stubs or rely on complex frameworks. It is designed to streamline interactions with SOAP-based web services while providing a customizable and testable architecture.

---

## Features

- **Generic Web Service Calls**: Supports secure, synchronous SOAP-based service calls.
- **Customizable Authentication**: Allows secure headers with username and password.
- **Testability**: Includes extensible mechanisms to mock or stub service calls for unit testing.
- **Minimal Dependencies**: Built with simplicity and maintainability in mind.

---

## Getting Started

### Prerequisites

To use **wsCaller**, ensure you have the following installed:

- Java 8 or later
- Apache Maven
- Access to a SOAP-based web service

---

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/mmilk23/wsCaller.git
   cd wsCaller
   ```

2. Build the project using Maven:
   ```bash
   mvn clean install
   ```

---

## Usage

### Example: Making a Secure Web Service Call

Below is an example of how to use **wsCaller** to make a secure SOAP request:

```java
import com.milklabs.wscall.SecureWebServiceCaller;
import org.apache.axis2.addressing.EndpointReference;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            // Create a web service caller
            SecureWebServiceCaller caller = new SecureWebServiceCaller(
                    new EndpointReference("http://example.com/service"),
                    "http://example.com/namespace",
                    "prefix"
            );

            // Define parameters for the service call
            Map<String, Object> params = new HashMap<>();
            params.put("param1", "value1");

            // Make the web service call
            String response = caller.chamarWebService("methodName", params, "username", "password");

            // Process the response
            System.out.println("Response: " + response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

---

## Testing

The project includes unit tests for key components. Tests are written using **JUnit 5**.

To run the tests, execute:

```bash
mvn test
```

### Mocking and Stubs

The library includes a `ServiceClientWrapperStub` class to facilitate testing without connecting to a live web service. This enables testing various scenarios, such as exceptions or specific responses.

---

## Project Structure

```
wsCaller/
├── src/main/java/com/milklabs/wscall
│   ├── SecureWebServiceCaller.java       # Core class for making web service calls
│   ├── ServiceClientWrapper.java         # Wrapper for the Axis2 ServiceClient
│   └── util                              # Utility classes
├── src/test/java/com/milklabs/wscall
│   ├── SecureWebServiceCallerTest.java   # Unit tests for the core functionality
│   ├── ServiceClientWrapperStub.java     # Stub class for testing
```

---

## Dependencies

- **Apache Axis2**: Core dependency for SOAP-based web service calls.
- **JUnit 5**: Testing framework.
- **Log4j**: For logging.

Add the following dependencies to your Maven `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>org.apache.axis2</groupId>
        <artifactId>axis2</artifactId>
        <version>1.8.2</version>
    </dependency>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>log4j</groupId>
        <artifactId>log4j</artifactId>
        <version>1.2.17</version>
    </dependency>
</dependencies>
```

---

## Contributing

Contributions are welcome! Please fork the repository, make changes, and submit a pull request.

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## Contact

For any inquiries or support, feel free to contact the project maintainer:
- **GitHub**: [@mmilk23](https://github.com/mmilk23)

If you found this project helpful, please consider giving it a star ⭐️.
