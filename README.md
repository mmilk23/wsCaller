# wsCaller

[![Java CI with Maven](https://github.com/mmilk23/wsCaller/actions/workflows/maven.yml/badge.svg)](https://github.com/mmilk23/wsCaller/actions/workflows/maven.yml)
[![CodeQL](https://github.com/mmilk23/wsCaller/actions/workflows/codeql.yml/badge.svg)](https://github.com/mmilk23/wsCaller/actions/workflows/codeql.yml)
[![Dependency Review](https://github.com/mmilk23/wsCaller/actions/workflows/dependency-review.yml/badge.svg)](https://github.com/mmilk23/wsCaller/actions/workflows/dependency-review.yml)
[![OWASP Dependency Check](https://github.com/mmilk23/wsCaller/actions/workflows/dependency-check.yml/badge.svg)](https://github.com/mmilk23/wsCaller/actions/workflows/dependency-check.yml)
[![Snyk](https://github.com/mmilk23/wsCaller/actions/workflows/snyk.yml/badge.svg)](https://github.com/mmilk23/wsCaller/actions/workflows/snyk.yml)
[![codecov](https://codecov.io/gh/mmilk23/wsCaller/branch/main/graph/badge.svg)](https://codecov.io/gh/mmilk23/wsCaller)
[![Coverage Status](https://coveralls.io/repos/github/mmilk23/wsCaller/badge.svg)](https://coveralls.io/github/mmilk23/wsCaller)
[![Last Updated](https://img.shields.io/github/last-commit/mmilk23/wsCaller.svg)](https://github.com/mmilk23/wsCaller/commits/main)

**wsCaller** is a lightweight Java library for making secure and generic SOAP web service calls without generating stubs for every service contract. It wraps Apache Axis2 with a small API, supports optional username/password SOAP headers, and keeps the call path easy to test with mocks and stubs.

## Features

- Generic synchronous SOAP calls through `SecureWebServiceCaller`.
- Optional username/password security headers.
- Request parameter mapping from `Map<String, Object>` to SOAP payload elements.
- Axis2 isolation through `ServiceClientWrapper`, which keeps unit tests independent from live services.
- Utility classes for common string and object parsing operations.
- Maven build configured for Java 25.

## Requirements

- JDK 25.
- Apache Maven 3.9 or newer.
- Access to the target SOAP endpoint for integration/manual calls.

On this workstation, the JDK 25 path used for validation is:

```powershell
$env:JAVA_HOME = "C:\dev\aws-jdk25.0.4_7"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
```

## Build

Compile, test, package, and generate the JaCoCo report:

```bash
mvn -B clean verify
```

Install the artifact in the local Maven repository:

```bash
mvn -B clean install
```

The generated JAR is written to:

```text
target/wscaller-1.2.0.jar
```

## Maven Dependency

The Maven coordinates for this project are:

```xml
<dependency>
    <groupId>io.github.mmilk23</groupId>
    <artifactId>wscaller</artifactId>
    <version>1.2.0</version>
</dependency>
```

Before the first Maven Central publication is available, another Maven project on the same machine can still use `wsCaller` after a local install:

```bash
mvn -B clean install
```

The release workflow publishes to Maven Central and attaches the generated JARs to GitHub Releases for tags matching `v*.*.*`, such as `v1.2.0`.

## Usage

```java
import com.milklabs.wscall.SecureWebServiceCaller;
import com.milklabs.wscall.WebServiceException;
import org.apache.axis2.addressing.EndpointReference;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws WebServiceException {
        SecureWebServiceCaller caller = new SecureWebServiceCaller(
                new EndpointReference("http://example.com/service"),
                "http://example.com/namespace",
                "ns"
        );

        Map<String, Object> params = new HashMap<>();
        params.put("param1", "value1");

        String response = caller.chamarWebService(
                "methodName",
                params,
                "username",
                "password"
        );

        System.out.println(response);
    }
}
```

For a complete runnable example, see `src/main/java/com/milklabs/exemplo/ExemploChamadaWS.java`.

## Tests And Coverage

Run only the unit tests:

```bash
mvn -B test
```

Run the full verification lifecycle and open the local JaCoCo report:

```bash
mvn -B clean verify
```

```text
target/site/jacoco/index.html
```

The current test suite covers the main call flow, error wrapping, service-client wrapper behavior, the example parser, and utility classes. At the time of this update, JaCoCo reports 100% instruction, branch, line, complexity, method, and class coverage.

## Security Checks

The repository has three dependency-security layers in GitHub Actions:

- **Dependency Review**: runs on pull requests and blocks newly introduced dependencies with high-or-critical known vulnerabilities.
- **OWASP Dependency-Check**: runs on push, pull request, weekly schedule, and manual dispatch. It fails the build for dependencies with CVSS `>= 7.0` and uploads HTML/JSON reports.
- **Snyk**: runs on push, pull request, weekly schedule, and manual dispatch. It scans Maven dependencies with `severity-threshold=high`, uploads SARIF to GitHub Code Scanning, and monitors the project in Snyk on pushes.

Required GitHub repository secret:

```text
SNYK_TOKEN
```

Required GitHub repository secrets for Maven Central releases:

```text
CENTRAL_USERNAME
CENTRAL_PASSWORD
GPG_PRIVATE_KEY
GPG_PASSPHRASE
```

`CENTRAL_USERNAME` and `CENTRAL_PASSWORD` must come from a Sonatype Central Portal user token. `GPG_PRIVATE_KEY` must be the ASCII-armored private key used to sign Maven artifacts.

Optional GitHub repository secret for faster and more reliable OWASP/NVD updates:

```text
NVD_API_KEY
```

Run OWASP Dependency-Check locally:

```bash
mvn -B -Pdependency-check verify -DskipTests
```

Run Snyk locally after authenticating with the Snyk CLI:

```bash
snyk test --file=pom.xml --package-manager=maven --severity-threshold=high
```

## GitHub Actions

- `maven.yml`: builds and tests on Ubuntu and Windows with JDK 25, then uploads coverage reports.
- `codeql.yml`: performs Java static analysis with CodeQL.
- `dependency-review.yml`: reviews dependency changes on pull requests.
- `dependency-check.yml`: scans dependencies with OWASP Dependency-Check.
- `snyk.yml`: scans and monitors dependencies with Snyk.
- `release.yml`: publishes signed artifacts to Maven Central and creates GitHub Releases for tags matching `v*.*.*`.

## Project Structure

```text
wsCaller/
|-- pom.xml
|-- src/main/java/com/milklabs/wscall
|   |-- SecureWebServiceCaller.java
|   |-- ServiceClientWrapper.java
|   |-- ServiceClientWrapperStub.java
|   |-- WebServiceException.java
|   `-- util/
|-- src/main/java/com/milklabs/exemplo
|   |-- ExemploChamadaWS.java
|   `-- vo/
`-- src/test/java/com/milklabs
    |-- exemplo/
    `-- wscall/
```

## Main Dependencies

- Apache Axis2 2.0.0.
- JDOM 2.0.6.1.
- SLF4J 2.0.17 and Logback 1.5.26.
- Lombok 1.18.42.
- JUnit Jupiter 5.14.2 and Mockito 5.21.0 for tests.
- JaCoCo 0.8.14 for coverage.
- OWASP Dependency-Check Maven Plugin 12.2.2 for dependency vulnerability scanning.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
