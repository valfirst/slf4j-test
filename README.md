# SLF4J Test
[![Build Status](https://travis-ci.org/valfirst/slf4j-test.svg?branch=master)](https://travis-ci.org/valfirst/slf4j-test)
[![Coverage Status](https://coveralls.io/repos/github/valfirst/slf4j-test/badge.svg?branch=master)](https://coveralls.io/github/valfirst/slf4j-test?branch=master)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=com.github.valfirst:slf4j-test&metric=alert_status)](https://sonarcloud.io/dashboard/index/com.github.valfirst:slf4j-test)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/ac96a0c1a5614c3b93491d10e70b3a36)](https://www.codacy.com/app/valfirst/slf4j-test?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=valfirst/slf4j-test&amp;utm_campaign=Badge_Grade)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.valfirst/slf4j-test.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3Acom.github.valfirst%20a%3Aslf4j-test)
[![Javadocs](http://www.javadoc.io/badge/com.github.valfirst/slf4j-test.svg)](http://www.javadoc.io/doc/com.github.valfirst/slf4j-test)
[![Known Vulnerabilities](https://snyk.io/test/github/valfirst/slf4j-test/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/valfirst/slf4j-test?targetFile=pom.xml)
[![MIT licensed](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/valfirst/jbehave-junit-runner/master/LICENSE.txt)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fvalfirst%2Fslf4j-test.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Fvalfirst%2Fslf4j-test?ref=badge_shield)

A test implementation of [SLF4J](https://www.slf4j.org/) that stores log messages in memory and provides methods for retrieving them. This implementation supports all versions of SLF4J including 1.8.X which have [a new binding mechanism](https://www.slf4j.org/faq.html#changesInVersion18).

## Installation

The easiest way is to include it in your project(s) by ways of a Maven dependency. Binary, Sources and Javadocs are
all available from Maven Central.

```xml
<dependency>
    <groupId>com.github.valfirst</groupId>
    <artifactId>slf4j-test</artifactId>
    <version>2.1.1</version>
</dependency>
```

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

## Credits
This project is based on the original implementation by [Robert Elliot](https://github.com/Mahoney), located at https://github.com/Mahoney/slf4j-test which worked with SLF4J prior to version 1.8.X.

See http://projects.lidalia.org.uk/slf4j-test for details.


## License Scan
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fvalfirst%2Fslf4j-test.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Fvalfirst%2Fslf4j-test?ref=badge_large)
