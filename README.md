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

## Advantages

- **No generated stubs required**: call SOAP services directly when you know the endpoint, namespace, operation, and parameter names.
- **Small public API**: most integrations only need `SecureWebServiceCaller`, a `Map<String, Object>`, and basic error handling.
- **Lower integration overhead**: useful for legacy SOAP services where generating and maintaining client code for every WSDL is inconvenient.
- **Optional security header**: send WS-Security username/password credentials only when the target service requires them.
- **Easy to test**: the Axis2 call path can be isolated with wrappers, stubs, Mockito, or custom subclasses.
- **AI-friendly usage contract**: the repository includes `docs/AI_USAGE.md` and `llms.txt` so coding agents can use the library with fewer assumptions.

## Requirements

- JDK 25.
- Apache Maven 3.9 or newer.
- Access to the target SOAP endpoint for integration/manual calls.

### Java Compatibility Note

This project is currently built and validated with JDK 25. The original version of the library was written for JDK 8, so the source code may still be adaptable to earlier Java versions by changing the Maven compiler configuration and rebuilding from source. If you need Java 8 compatibility, run the full test suite after changing the target release.

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
target/wscaller-1.2.1.jar
```

## Maven Dependency

The Maven coordinates for this project are:

```xml
<dependency>
    <groupId>io.github.mmilk23</groupId>
    <artifactId>wscaller</artifactId>
    <version>1.2.1</version>
</dependency>
```

Before the first Maven Central publication is available, another Maven project on the same machine can still use `wsCaller` after a local install:

```bash
mvn -B clean install
```

The release workflow publishes to Maven Central and attaches the generated JARs to GitHub Releases for tags matching `v*.*.*`, such as `v1.2.1`.

`mvnrepository.com` is not the publishing target; it is a third-party index. Publishing happens through Sonatype Central Portal, then Maven Repository and other indexes pick up the artifact after Maven Central syncs.

## Branches

- `main`: stable branch for released or release-ready code.
- `development`: integration branch for ongoing work before it is promoted to `main`.

Open feature and dependency-update pull requests against `development`. Promote `development` to `main` with a pull request after the build, dependency review, OWASP Dependency-Check, Snyk, and CodeQL checks are green.

## Usage

For detailed end-user API documentation, see [docs/API.md](docs/API.md).

For AI agents and automation tools that need a compact implementation contract, see [docs/AI_USAGE.md](docs/AI_USAGE.md) and [llms.txt](llms.txt).

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

`CENTRAL_USERNAME` and `CENTRAL_PASSWORD` must come from a Sonatype Central Portal user token, not from the account login password. `GPG_PRIVATE_KEY` must be the ASCII-armored private key used to sign Maven artifacts.

Before the first publication, confirm in Sonatype Central Portal that the namespace `io.github.mmilk23` is verified. GitHub-linked Central Portal accounts can usually publish under `io.github.<username>`.

Release flow:

```bash
git switch main
git pull --ff-only origin main
git tag v1.2.1
git push origin v1.2.1
```

The release workflow only publishes tags whose version matches `pom.xml`, and the tagged commit must already be part of `main`.

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

- `maven.yml`: builds and tests pushes and pull requests for `main` and `development` on Ubuntu and Windows with JDK 25, then uploads coverage reports.
- `codeql.yml`: performs Java static analysis for `main` and `development`.
- `dependency-review.yml`: reviews dependency changes on pull requests targeting `main` or `development`.
- `dependency-check.yml`: scans dependencies with OWASP Dependency-Check for `main` and `development`.
- `snyk.yml`: scans and monitors dependencies for `main` and `development`.
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

- Apache Axis2 2.0.1.
- JDOM 2.0.6.1.
- SLF4J 2.0.17 and Logback 1.6.1.
- Lombok 1.18.46.
- JUnit Jupiter 6.1.2 and Mockito 5.23.0 for tests.
- JaCoCo 0.8.14 for coverage.
- OWASP Dependency-Check Maven Plugin 12.2.2 for dependency vulnerability scanning.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
