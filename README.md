# SLF4J Test
[![slf4j-test CI](https://github.com/valfirst/slf4j-test/workflows/slf4j-test%20CI/badge.svg)](https://github.com/valfirst/slf4j-test/actions?query=workflow%3A%22slf4j-test+CI%22)
[![Coverage Status](https://coveralls.io/repos/github/valfirst/slf4j-test/badge.svg?branch=master)](https://coveralls.io/github/valfirst/slf4j-test?branch=master)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=com.github.valfirst%3Aslf4j-test&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=com.github.valfirst%3Aslf4j-test)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/ac96a0c1a5614c3b93491d10e70b3a36)](https://www.codacy.com/gh/valfirst/slf4j-test/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=valfirst/slf4j-test&amp;utm_campaign=Badge_Grade)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.valfirst/slf4j-test.svg)](https://central.sonatype.com/search?q=slf4j-test&namespace=com.github.valfirst)
[![Javadocs](http://www.javadoc.io/badge/com.github.valfirst/slf4j-test.svg)](http://www.javadoc.io/doc/com.github.valfirst/slf4j-test)
[![Known Vulnerabilities](https://snyk.io/test/github/valfirst/slf4j-test/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/valfirst/slf4j-test?targetFile=pom.xml)
[![MIT licensed](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/valfirst/jbehave-junit-runner/master/LICENSE)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fvalfirst%2Fslf4j-test.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Fvalfirst%2Fslf4j-test?ref=badge_shield)

A test implementation of [SLF4J](https://www.slf4j.org/) that stores log messages in memory and provides methods for retrieving them. This implementation supports all versions of SLF4J including 1.8.X which have [a new binding mechanism](https://www.slf4j.org/faq.html#changesInVersion18).

## Installation

The easiest way is to include it in your project(s) by ways of a Maven dependency. Binary, Sources and Javadocs are
all available from Maven Central.

```xml
<dependency>
    <groupId>com.github.valfirst</groupId>
    <artifactId>slf4j-test</artifactId>
    <version>2.9.0</version>
</dependency>
```

## Compatibility Matrix
| `org.slf4j:slf4j-api`           | `com.github.valfirst:slf4j-test` |
|---------------------------------|----------------------------------|
| `1.8.0-beta0` - `2.0.0-alpha2`  | `1.3.0` - `2.3.0`                |
| `2.0.0-alpha3` - `2.0.0-alpha5` | `2.4.0` - `2.4.1`                |
| `2.0.0-alpha6` - `2.0.7`        | `2.5.0` - `2.9.0`                |


## JUnit 5 Support
### Declarative Extension Registration
SLF4J Test provides JUnit Platform extension which can registered via annotation:

```java
import org.junit.jupiter.api.extension.ExtendWith;
import com.github.valfirst.slf4jtest.TestLoggerFactoryExtension;

@ExtendWith(TestLoggerFactoryExtension.class)
class BasicJUnit5Test {
   ...
}
```

### Automatic Extension Registration
SLF4J Test supports automatic extension registration via ServiceLoader mechanism. This feature is considered advanced in JUnit Platform and [it requires explicit enabling](https://junit.org/junit5/docs/current/user-guide/#extensions-registration-automatic-enabling).

## Documentation
See https://valfirst.github.io/slf4j-test/ for details.

## Credits
This project is based on the original implementation by [Robert Elliot](https://github.com/Mahoney), located at https://github.com/Mahoney/slf4j-test which worked with SLF4J prior to version 1.8.X.

## License Scan
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fvalfirst%2Fslf4j-test.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Fvalfirst%2Fslf4j-test?ref=badge_large)
