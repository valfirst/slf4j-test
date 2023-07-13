## Changelog

### [slf4j-test-3.0.1](https://github.com/valfirst/slf4j-test/tree/slf4j-test-3.0.1) (2023-07-13)
[Full Changelog](https://github.com/valfirst/slf4j-test/compare/slf4j-test-3.0.0...slf4j-test-3.0.1)

**Fixes:**

- [#413](https://github.com/valfirst/slf4j-test/issues/413) via [#414](https://github.com/valfirst/slf4j-test/pull/414) Allow to use `TreeMap` as input parameter for `TestMDCAdapter::setContextMap` (by [@valfirst](https://github.com/valfirst))


### [slf4j-test-3.0.0](https://github.com/valfirst/slf4j-test/tree/slf4j-test-3.0.0) (2023-07-11)
[Full Changelog](https://github.com/valfirst/slf4j-test/compare/slf4j-test-2.9.0...slf4j-test-3.0.0)

**Breaking changes**

- [#380](https://github.com/valfirst/slf4j-test/issues/380) via [#383](https://github.com/valfirst/slf4j-test/pull/383) - Switch from Lidalia Level to SLF4J Level (by [@karstenspang](https://github.com/karstenspang))

  - `uk.org.lidalia.slf4jext.Level` is replaced with `org.slf4j.event.Level`

    The `org.slf4j.event.Level` class has been present in SLF4J since version `1.7.15` from 2016. There is little reason for a client to use a version older than that.

    Changing the `import` statement from one package to the other handles most of the changes. But some differences between the two `Level` classes remain.

    #### Ordinal Values

    In `uk.org.lidalia.slf4jext.Level`, `ERROR` has the lowest ordinal value, and `TRACE` has the highest. In `org.slf4j.event.Level`, it is the other way round. When comparing levels using `Enum.compareTo`, the equality sign will have to be reversed. In SLF4J Test, this is used to decide whether to print the log message.

    #### `OFF` Level

    `org.slf4j.event.Level` has no `OFF` level. This means that another way must be used to specify that printing is off. `null` is the only other value you can pass as an enum. The print level logic is changed to use `null` as "off".

    #### `enableableLevels`

    `uk.org.lidalia.slf4jext.Level` has the static method `enableableLevels` that returns the set of enum values corresponding to log levels that can be enabled. Since all values represent enableable levels in `org.slf4j.event.Level`, `EnumSet.allOf(Level.class)` provides the equivalent funtionality.

  - Dependency `uk.org.lidalia:lidalia-slf4j-ext` is dropped

- [#380](https://github.com/valfirst/slf4j-test/issues/380) via [#384](https://github.com/valfirst/slf4j-test/pull/384) - Remove Guava as dependency (by [@karstenspang](https://github.com/karstenspang))

  - Guava Collections are replaced with Java Collections

    Parameter and return types are changed to be simply `List`, `Map`, or `Set`, instead of the specific ones from Guava. The client can still pass the Guava ones as parameters if needed. In case the client assign a returned value to a specific type, code changes are needed.

    Returned vales are wrapped using `java.util.Collections.unmodifiableList`/`Map`/`Set` after copying.

    Internal values are implemented using `ArrayList`, `HashMap`, or `HashSet` wrapped to be made unmodifiable. As a special case, enabled log level sets are to be represented as `EnumSet<Level>`.

  - Dependency `com.google.guava:guava` is dropped

- [#312](https://github.com/valfirst/slf4j-test/issues/312), [#380](https://github.com/valfirst/slf4j-test/issues/380) via [#326](https://github.com/valfirst/slf4j-test/pull/326) - Use java.time's `Instant` rather than `joda-time` (by [@josephw](https://github.com/josephw))

  - `org.joda.time.Instant` is replaced with `java.time.Instant`
  - Dependency `joda-time:joda-time` is dropped

- [#390](https://github.com/valfirst/slf4j-test/issues/390) via [#394](https://github.com/valfirst/slf4j-test/pull/394) - Avoid substitution of `null` values (by [@karstenspang](https://github.com/karstenspang))

  `null`-s were replaced by other values:
    - When a `LoggingEvent` was constructed with arguments, `null`-s were replaced with `Option.empty()`. If logging events were just compared, such replacement had no impact. So restoration of `null`-s is considered as an internal change here.
    - If `null` value was inserted in the MDC, it was replaced with the string `"null"`. If MDC functionality were tested, such replacement could be annoying. In particular, the assertion failure message showed `null` in both the expected and the actual value. Now such substitution is eliminated, `null` value is always used.

- [#409](https://github.com/valfirst/slf4j-test/issues/409) via [#411](https://github.com/valfirst/slf4j-test/pull/411) Drop static binders (by [@valfirst](https://github.com/valfirst))

  slf4j-api 2.0.x relies on the `ServiceLoader` mechanism to find its logging backend. SLF4J 1.7.x and earlier versions relied on the static binder mechanism which is no loger honored by slf4j-api version 2.0.x. SLF4J Test doesn't support SLF4J 1.7.x and 1.8.x since version `2.4.0` (when breaking changes were introduced in SLf4J API). Thus static binders are removed:

    - `org.slf4j.impl.StaticLoggerBinder`
    - `org.slf4j.impl.StaticMDCBinder`
    - `org.slf4j.impl.StaticMarkerBinder`

**Implemented enhancements:**

- [#398](https://github.com/valfirst/slf4j-test/issues/398) via [#399](https://github.com/valfirst/slf4j-test/pull/399) Add utility classes to support `java.util.logging` testing (by [@karstenspang](https://github.com/karstenspang))

  - A helper class to do the `java.util.logging` setup is added, as well as a JUnit 5 extension that calls it.
  - `jul-to-slf4j` bridge is added as an optional dependency.

- [#392](https://github.com/valfirst/slf4j-test/issues/392) via [#401](https://github.com/valfirst/slf4j-test/pull/397) Implement MDC configuration management (by [@karstenspang](https://github.com/karstenspang))

  There is now configuration for four aspects:
    - Whether an MDC is supported at all.
    - Whether the MDC is inherited by child threads.
    - Whether `getCopyOfContextMap()` shall return an empty map or `null` if the MDC has not been set.
    - Whether `null` is a legal value in an MDC entry. `null` keys are never allowed.

- [#407](https://github.com/valfirst/slf4j-test/issues/407) via [#408](https://github.com/valfirst/slf4j-test/pull/408), [#412](https://github.com/valfirst/slf4j-test/pull/412) Add full support for the SLF4J fluent logging API (by [@karstenspang](https://github.com/karstenspang))

  - `LoggingEvent` class supports multiple markers. This is allowed by the fluent API.
  - `LoggingEvent` class stores the key/value pair list that can be generated fluent API.
  - The new `TestLoggingEventBuilder` class can be used for fluently creating `LoggingEvent` instances for use as expected values.

- [#183](https://github.com/valfirst/slf4j-test/issues/183) via [#410](https://github.com/valfirst/slf4j-test/pull/410) Add ability to configure cleanup stage (by [@valfirst](https://github.com/valfirst))

  Now it is possible to configure JUnit 5 stage which cleanup should be performed at using `@TestLoggerFactorySettings` annotation or while programmatic extension registration is performed.

**Fixes:**

- [#400](https://github.com/valfirst/slf4j-test/issues/400) via [#401](https://github.com/valfirst/slf4j-test/pull/401) Fix Javadoc links (by [@karstenspang](https://github.com/karstenspang))

- [#393](https://github.com/valfirst/slf4j-test/issues/393) via [#405](https://github.com/valfirst/slf4j-test/pull/405) Do not change the defaults for all threads at resetting enabled logging levels (by [@karstenspang](https://github.com/karstenspang))

### [slf4j-test-2.9.0](https://github.com/valfirst/slf4j-test/tree/slf4j-test-2.9.0) (2023-03-22)
[Full Changelog](https://github.com/valfirst/slf4j-test/compare/slf4j-test-2.8.1...slf4j-test-2.9.0)

**Implemented enhancements:**
- [#360](https://github.com/valfirst/slf4j-test/pull/360) - Add ability to make assertions using the `PredicateBuilder` more fluent (by [@topbadger](https://github.com/topbadger))

  Static helper method and method overloads are added to make assertions using the `PredicateBuilder` more fluent.

  This allows assertions to be declared as follows:

  ```java
  assertThat(logger).hasLogged(aLog().withLevel(Level.WARN).withMessage("Something"));
  ```

  as opposed to:

  ```java
  assertThat(logger).hasLogged(new PredicateBuilder().withLevel(Level.WARN).withMessage("Something").build());
  ```

**Updates:**
- Bump `org.slf4j:slf4j-api` from `2.0.6` to `2.0.7`
- Bump `org.assertj:assertj-core` from `3.23.1` to `3.24.2`

### [slf4j-test-2.8.1](https://github.com/valfirst/slf4j-test/tree/slf4j-test-2.8.1) (2023-01-05)
[Full Changelog](https://github.com/valfirst/slf4j-test/compare/slf4j-test-2.8.0...slf4j-test-2.8.1)

**Fixes:**
- [#323](https://github.com/valfirst/slf4j-test/issues/323) via [#334](https://github.com/valfirst/slf4j-test/pull/334) Allow logged message to be `null` (by [@valfirst](https://github.com/valfirst))

### [slf4j-test-2.8.0](https://github.com/valfirst/slf4j-test/tree/slf4j-test-2.8.0) (2022-12-28)
[Full Changelog](https://github.com/valfirst/slf4j-test/compare/slf4j-test-2.7.0...slf4j-test-2.8.0)

**Implemented enhancements:**
- [#188](https://github.com/valfirst/slf4j-test/issues/188) via [#328](https://github.com/valfirst/slf4j-test/pull/328) - Restructure assertions to be based on predicates (by [@topbadger](https://github.com/topbadger))

  By re-structuring the assertion logic to be based upon predicates, the following enhancements have been made:

  #### Custom Predicates
  Consumers can now provide custom predicates so that they can assert on `LoggingEvent`s in specific ways in addition to the ways previously supported by the library.

  _Example:_
  ```java
  assertThat(logger)
      .hasLogged(event -> event.getFormattedMessage().startsWith("First section of long log message"));
  ```

  #### MDC Comparison Strategy
  The MDC comparison strategy can now be set by consumers.
  - The full MDC context isn't always relevant to the test, yet the assertions previously enforced this check. The result was that people were unable to use the fluent assertions we provide in a number of scenarios.
  - The previous behaviour of requiring the MDC contents to match exactly has been retained as the default.

  _Example:_
  ```java
  assertThat(logger)
      .usingMdcComparator(MdcComparator.IGNORING)
      .hasLogged(warn("Some log message"));
  ```

  #### Enhanced Assertion Failure Messages
  Assertion failure messages have been enhanced to show the list of `LoggingEvent`s that were _actually_ captured to make debugging the cause of the failure easier.

  _Example:_
  ```
  Failed to find event:
    LoggingEvent{level=ERROR, mdc={}, marker=Optional.empty, throwable=Optional[throwable], message='There was a problem!', arguments=[]}

  The logger contained the following events:
    - LoggingEvent{level=WARN, mdc={}, marker=Optional.empty, throwable=Optional.empty, message='Some other problem', arguments=[]}
    - LoggingEvent{level=ERROR, mdc={}, marker=Optional.empty, throwable=Optional.empty, message='Yet another problem', arguments=[]}
  ```

### [slf4j-test-2.7.0](https://github.com/valfirst/slf4j-test/tree/slf4j-test-2.7.0) (2022-12-19)
[Full Changelog](https://github.com/valfirst/slf4j-test/compare/slf4j-test-2.6.1...slf4j-test-2.7.0)

**Implemented enhancements:**
- [#314](https://github.com/valfirst/slf4j-test/issues/314) via [#324](https://github.com/valfirst/slf4j-test/pull/324) - Introduce a new `capture.level` property and API to control captured events globally (by [@youribonnaffe]https://github.com/youribonnaffe)

  Introduce a new global setting `capture.level` to disable storing (and printing) logs at a given level (following the level hierarchy).

  The implementation is very similar to the `print.level` global setting.

  This is useful when tests are generating a lot of logging events that are not of interest and don't need to be captured. ArchUnit tests are known to be such tests and can be quite slow when `slf4j-test` is used.

**Updates:**
- Bump `org.slf4j:slf4j-api` from `2.0.0-alpha7` to `2.0.6`
- Bump `joda-time:joda-time` from `2.10.14` to `2.12.2`
- Bump `org.assertj:assertj-core` from `3.22.0` to `3.23.1`

### [slf4j-test-2.6.1](https://github.com/valfirst/slf4j-test/tree/slf4j-test-2.6.1) (2022-04-01)
[Full Changelog](https://github.com/valfirst/slf4j-test/compare/slf4j-test-2.6.0...slf4j-test-2.6.1)

**Fixes:**
- [#255](https://github.com/valfirst/slf4j-test/issues/255) via [#257](https://github.com/valfirst/slf4j-test/pull/257) Fix memory leak when clearing logs in all threads (by [@sofiamorseletto](https://github.com/sofiamorseletto))
  Going back to the custom `ThreadLocal` implementation (`uk.org.lidalia.lang.ThreadLocal`) in order to prevent memory leaks.

### [slf4j-test-2.6.0](https://github.com/valfirst/slf4j-test/tree/slf4j-test-2.6.0) (2022-03-21)
[Full Changelog](https://github.com/valfirst/slf4j-test/compare/slf4j-test-2.5.0...slf4j-test-2.6.0)

**Implemented enhancements:**
- [#247](https://github.com/valfirst/slf4j-test/pull/247) Log events found when AssertJ assertion fails (by [@jamietanna](https://github.com/jamietanna))

  This helps quite a bit with diagnosing why an assertion fails, as the alternative is to either attach a debugger or `System.out.println` the logging events found.
- [#250](https://github.com/valfirst/slf4j-test/issues/250) via [#251](https://github.com/valfirst/slf4j-test/pull/251) Add TCCL attribute to `LoggingEvent` (by [@sofiamorseletto](https://github.com/sofiamorseletto))

  The changes add an attribute on `LoggingEvent` which contains the Thread Context Classloader used when the logging event was created. This allows the assertion of the TCCL of a certain log to check that the log was handled by the correct classloader.

**Updates:**
- Bump `org.slf4j:slf4j-api` from `2.0.0-alpha6` to `2.0.0-alpha7`
- Bump `com.google.guava:guava` from `31.0.1-jre` to `31.1-jre`
- Bump `joda-time:joda-time` from `2.10.13` to `2.10.14`

### [slf4j-test-2.5.0](https://github.com/valfirst/slf4j-test/tree/slf4j-test-2.5.0) (2022-02-08)
[Full Changelog](https://github.com/valfirst/slf4j-test/compare/slf4j-test-2.4.1...slf4j-test-2.5.0)

**Updates:**
- [#228](https://github.com/valfirst/slf4j-test/pull/228) - Bump `org.slf4j:slf4j-api` from `2.0.0-alpha5` to `2.0.0-alpha6`

  _NOTE:_ `slf4j-test:2.5.0` is compatible with `slf4j-api:2.0.0-alpha6`and higher and it is not compatible with previous versions (see [compatibility matrix](https://github.com/valfirst/slf4j-test/blob/master/README.md#compatibility-matrix) for more details). The root cause is breaking changes in SLF4J API:
  - [breaking change 1](https://github.com/qos-ch/slf4j/commit/62a265d3c5a2bde82f2e025ee10f115564d951bb#diff-d9b428b7088f0b9ee7b67586509f3920e88f408e06258a0e2113c8cb8f4c8e37)
  - [breaking change 2](https://github.com/qos-ch/slf4j/commit/f09e33dd15f60b4480d9a60bfb9083ef739fea2f#diff-d9b428b7088f0b9ee7b67586509f3920e88f408e06258a0e2113c8cb8f4c8e37)
- Bump `org.assertj:assertj-core` from `3.21.0` to `3.22.0`

### [slf4j-test-2.4.1](https://github.com/valfirst/slf4j-test/tree/slf4j-test-2.4.1) (2021-12-23)
[Full Changelog](https://github.com/valfirst/slf4j-test/compare/slf4j-test-2.4.0...slf4j-test-2.4.1)

**Implemented enhancements:**
- [#207](https://github.com/valfirst/slf4j-test/pull/207) - Enable assertions to be made on LoggingEvents from any thread (by [@topbadger](https://github.com/topbadger))

  The current implementation only verifies against the `LoggingEvent`s captured by the thread that is running the test. `TestLogger` provides access to all events captured across any thread via `TestLogger::getAllLoggingEvents()` so a modifying method has been added to the assertion class to allow the assertion mode to be switched.

  Example usage:
  ```
  assertThat(logger).anyThread().hasLogged(...);
  ```
- [#220](https://github.com/valfirst/slf4j-test/issues/220) via [#221](https://github.com/valfirst/slf4j-test/pull/221) - Format `null`-s in `LoggingEvent#getFormattedMessage()` the same way as `SLF4J` (by [@josephw](https://github.com/josephw))

  `LoggingEvent#getFormattedMessage()` is public ([#23](https://github.com/valfirst/slf4j-test/issues/23)). However, it hasn't presented `null` values the same way as SLF4J, due to un-matched wrapping/unwrapping of `Optional`:
  ```
  LoggingEvent e = new LoggingEvent(Level.INFO, "Content: {}, {}", null, "value");
  System.out.println(e.getFormattedMessage());
  ```

  Produced previously:
  ```
  Content: Optional.empty, value
  ```
  Produces now:
  ```
  Content: null, value
  ```

**Updates:**
- Bump `org.slf4j:slf4j-api` from `2.0.0-alpha4` to `2.0.0-alpha5`
- Bump `joda-time:joda-time` from `2.10.10` to `2.10.13`
- Bump `com.google.guava:guava` from `30.1.1-jre` to `31.0.1-jre`
- Bump `org.assertj:assertj-core` from `3.20.2` to `3.21.0`

### [slf4j-test-2.4.0](https://github.com/valfirst/slf4j-test/tree/slf4j-test-2.4.0) (2021-08-25)
[Full Changelog](https://github.com/valfirst/slf4j-test/compare/slf4j-test-2.3.0...slf4j-test-2.4.0)

**Updates:**

- [#190](https://github.com/valfirst/slf4j-test/pull/190) - Bump `org.slf4j:slf4j-api` from `2.0.0-alpha2` to `2.0.0-alpha4`
- [#190](https://github.com/valfirst/slf4j-test/pull/190) - Update according to the changes in SLF4J API `2.0.0-alpha3`

  _NOTE:_ `slf4j-test:2.4.0` is compatible with `slf4j-api:2.0.0-alpha3`and higher and it is not compatible with previous versions (see [compatibility matrix](https://github.com/valfirst/slf4j-test/blob/master/README.md#compatiblity-matrix) for more details). The root cause is [breaking change in SLF4J API](https://github.com/qos-ch/slf4j/commit/3e2381ea694c23c897433d675facd9a799a52256#diff-2f5a94db3233a499f03f72a41ebd7fed35029b270f9a7578c202db4ab1845b48).

### [slf4j-test-2.3.0](https://github.com/valfirst/slf4j-test/tree/slf4j-test-2.3.0) (2021-08-05)
[Full Changelog](https://github.com/valfirst/slf4j-test/compare/slf4j-test-2.2.0...slf4j-test-2.3.0)

**Implemented enhancements:**

- [#184](https://github.com/valfirst/slf4j-test/pull/184) - Add assertion for substrings of log messages (by [@jamietanna](https://github.com/jamietanna))
- [#187](https://github.com/valfirst/slf4j-test/pull/187) - Add assertions based on `LoggingEvent` (by [@jamietanna](https://github.com/jamietanna))

**Internals:**
- [#185](https://github.com/valfirst/slf4j-test/issues/185) via [#186](https://github.com/valfirst/slf4j-test/pull/186) Enforce consistent code style (by [@jamietanna](https://github.com/jamietanna))

### [slf4j-test-2.2.0](https://github.com/valfirst/slf4j-test/tree/slf4j-test-2.2.0) (2021-07-29)
[Full Changelog](https://github.com/valfirst/slf4j-test/compare/slf4j-test-2.1.1...slf4j-test-2.2.0)

**Implemented enhancements:**

- [#181](https://github.com/valfirst/slf4j-test/issues/181) via [#182](https://github.com/valfirst/slf4j-test/pull/182) - Add initial AssertJ assertions (by [@jamietanna](https://github.com/jamietanna))

**Updates:**

- [#124](https://github.com/valfirst/slf4j-test/issues/124) via [#126](https://github.com/valfirst/slf4j-test/pull/126) - Add [an explicit license](https://github.com/valfirst/slf4j-test/blob/master/LICENSE) to the project
- Bump `org.slf4j:slf4j-api` from 1.8.0-beta2 to 2.0.0-alpha2
- Bump `joda-time:joda-time` from 2.10 to 2.10.10
- Bump `com.google.guava:guava` from 26.0-jre to 30.1.1-jre

### [slf4j-test-2.1.1](https://github.com/valfirst/slf4j-test/tree/slf4j-test-2.1.1) (2018-09-17)
[Full Changelog](https://github.com/valfirst/slf4j-test/compare/slf4j-test-2.1.0...slf4j-test-2.1.1)

**Implemented enhancements:**

- Make LoggingEvent self-descriptive: implement `toString` method (by [@valfirst](https://github.com/valfirst))

**Updates:**

- Bump `com.google.guava:guava` from 25.1-jre to 26.0-jre

### [slf4j-test-2.1.0](https://github.com/valfirst/slf4j-test/tree/slf4j-test-2.1.0) (2018-06-29)
[Full Changelog](https://github.com/valfirst/slf4j-test/compare/slf4j-test-2.0.0...slf4j-test-2.1.0)

**Implemented enhancements:**

- [#23](https://github.com/valfirst/slf4j-test/issues/23) - Make LoggingEvent\#getFormattedMessage public (by [@valfirst](https://github.com/valfirst))
- Drop `uk.org.lidalia:lidalia-lang` dependency (by [@valfirst](https://github.com/valfirst))

**Updates:**

- Bump `org.slf4j:slf4j-api` from 1.8.0-beta1 to 1.8.0-beta2
- Bump` joda-time:joda-time` from 2.9.9 to 2.10
- Bump `com.google.guava:guava` from 24.1-jre to 25.1-jre

### [slf4j-test-2.0.0](https://github.com/valfirst/slf4j-test/tree/slf4j-test-2.0.0) (2018-03-19)
[Full Changelog](https://github.com/valfirst/slf4j-test/compare/slf4j-test-1.3.0...slf4j-test-2.0.0)

**Breaking changes**

- Increase minimum required JDK version from 7 to 8
- Change base package to `com.github.valfirst` to reflect ownership

**Implemented enhancements:**

- [#6](https://github.com/valfirst/slf4j-test/pull/6) - Implement JUnit 5 support (by [@valfirst](https://github.com/valfirst))
- Optimize performance: replace CopyOnWriteArrayList with Collections.synchronizedList in `TestLoggerFactory` (by [@valfirst](https://github.com/valfirst))

**Updates:**

- Bump `org.slf4j:slf4j-api` from 1.8.0-beta0 to 1.8.0-beta2
- Bump `com.google.guava:guava` from 20.0 to 24.1-jre

### [slf4j-test-1.3.0](https://github.com/valfirst/slf4j-test/tree/slf4j-test-1.3.0) (2018-01-05)
[Full Changelog](https://github.com/valfirst/slf4j-test/compare/slf4j-test-1.2.0...slf4j-test-1.3.0)

**`groupId` was changed to `com.github.valfirst` to reflect ownership**

**Implemented enhancements:**

- Support new SLF4J binding mechanism introduced in [SLF4J 1.8](https://www.slf4j.org/faq.html#changesInVersion18)

**Updates:**

- Upgrade to Guava 20.0, Joda Time 2.9.9

### Version 1.2.0

Allows construction of standalone instances to facilitate logging in different
contexts.

### Version 1.1.0

Fixes https://github.com/Mahoney/slf4j-test/issues/4 - Detect throwable as last
varargs element if the format string has n-1 parameters.

With thanks to https://github.com/philipa

### Version 1.0.1

Fixed an issue where null arguments to a logging event and null values in the
MDC caused a NullPointerException due to Guava's strict attitude to nulls. Null
arguments now appear as Optional.absent() and null values in the MDC as the
String "null".
