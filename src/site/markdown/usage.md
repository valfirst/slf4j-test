## Usage

### Making Assertions

TestLoggerFactory is the underlying implementation to which SLF4J's
LoggerFactory delegates when SLF4J Test is the bound SLF4J implementation. The
getLogger method on TestLoggerFactory returns the same Logger instance as that
on LoggerFactory, but with the additional type information to allow retrieval of
the LoggingEvents that have occurred against that Logger.

SLF4J Test provides a comprehensive set of static factory methods on
LoggingEvent to facilitate easy construction of expected events for comparison
against those that were actually received, and LoggingEvent has appropriate
equals, hashCode and toString implementations for this purpose. See the [front
](./index.html) page for an example of using the [info static factory method
](./xref/com/github/valfirst/slf4jtest/LoggingEvent.html#L161) to create an expected
LoggingEvent.

#### Custom Predicates
Users can provide custom predicates so that they can assert on `LoggingEvent`s in specific ways in addition to the regular supported ways.

_Example:_

    assertThat(logger)
        .hasLogged(event -> event.getFormattedMessage().startsWith("First section of long log message"));


#### MDC Comparison Strategy
The default behaviour requires the MDC contents to match exactly. But since the full MDC context isn't always relevant to the test, the MDC comparison strategy can be specified by users.

_Example:_

    assertThat(logger)
        .usingMdcComparator(MdcComparator.IGNORING)
        .hasLogged(warn("Some log message"));

### Setting the Log Level on a Logger

SLF4J Test only stores events for levels which are marked as enabled on the
Logger. By default, all levels are enabled; however, this can be programmatically
changed on a per logger basis using the following functions:

    Logger.setEnabledLevels(Level... levels)
    Logger.setEnabledLevelsForAllThreads(Level... levels)

It's important to note that SLF4J does *not* imply any relationship between the
levels - it is perfectly possible for a logger to be enabled for INFO but
disabled for ERROR as far as SLF4J is concerned, and hence as far as SLF4J Test
is concerned, notwithstanding the fact that implementations such as Logback and
Log4J do not permit this.

### Globally disabling a Log Level

Storing log events at all levels can slow down test executions. If needed a global
setting can be used to avoid capturing events at a given level or below respecting
the conventional level hierarchy (if the capture level is set to INFO, DEBUG and TRACE
events won't be captured). This can be set in any of the following ways:

#### Programmatically
    TestLoggerFactory.getInstance().setCaptureLevel(Level.INFO);

#### Via a System Property
Run the JVM with the following:

    -Dslf4jtest.capture.level=INFO

#### Via a properties file
Place a file called slf4jtest.properties on the classpath with the following
line in it:

    capture.level=INFO


### Resetting Stored State

In order to have robust tests the in memory state of SLF4J Test must be in a
known state for each test run, which in turn implies that a test should clean up
after itself. The simplest way to do so is via a call to

```java
TestLoggerFactory.clear()
```

in a tear down method of some kind.

More nuanced state resetting can be done on a per logger basis:

```java
TestLogger.clear()
```

or more aggressive clears will reset state across all threads:

```java
TestLoggerFactory.clearAll()
TestLogger.clearAll()
```
     
#### JUnit 4

If you are using JUnit 4 then SLF4J Test provides a `@Rule` that will do this
for you if you provide the following line in your test class:

```java
@Rule public TestRule resetLoggingEvents = new TestLoggerFactoryResetRule();
```

#### JUnit 5
SLF4J Test provides JUnit Platform extension which can be registered in any supported way.

##### Declarative Extension Registration
`TestLoggerFactoryExtension` can be registered declaratively via `@ExtendWith` annotation:

```java
import org.junit.jupiter.api.extension.ExtendWith;
import com.github.valfirst.slf4jtest.TestLoggerFactoryExtension;

@ExtendWith(TestLoggerFactoryExtension.class)
class BasicJUnit5Test {
    ...
}
```

It is possible to configure stage which cleanup should be performed at
using `@TestLoggerFactorySettings` annotation:

```java
import org.junit.jupiter.api.extension.ExtendWith;
import com.github.valfirst.slf4jtest.CleanupStage;
import com.github.valfirst.slf4jtest.TestLoggerFactoryExtension;
import com.github.valfirst.slf4jtest.TestLoggerFactorySettings;

@ExtendWith(TestLoggerFactoryExtension.class)
@TestLoggerFactorySettings(cleanupStage = CleanupStage.BEFORE_EACH)
class BasicJUnit5Test {
    ...
}
```

##### Programmatic Extension Registration
`TestLoggerFactoryExtension` can be registered programmatically by annotating field in test classes
with `@RegisterExtension`:

```java
import org.junit.jupiter.api.extension.RegisterExtension;
import com.github.valfirst.slf4jtest.TestLoggerFactoryExtension;

class BasicJUnit5Test {

    @RegisterExtension
    static TestLoggerFactoryExtension extension = new TestLoggerFactoryExtension();
    ...
}
```

Also, it is possible to configure stage which clean up should happen at:

```java
import org.junit.jupiter.api.extension.RegisterExtension;
import com.github.valfirst.slf4jtest.CleanupStage;
import com.github.valfirst.slf4jtest.TestLoggerFactoryExtension;

class BasicJUnit5Test {

    @RegisterExtension
    static TestLoggerFactoryExtension extension = new TestLoggerFactoryExtension(CleanupStage.BEFORE_EACH);
   ...
}
```


##### Automatic Extension Registration
SLF4J Test supports automatic extension registration via ServiceLoader mechanism.
This feature is considered advanced in JUnit Platform and
[it requires explicit enabling](https://junit.org/junit5/docs/current/user-guide/#extensions-registration-automatic-enabling).


### Parallel Testing

SLF4J Test is designed to facilitate tests run in parallel. Because SLF4J
Loggers are commonly shared across threads, the SLF4J Test implementation
maintains its state in ThreadLocals. The following functions:

    TestLogger.getLoggingEvents()
    TestLogger.clear()
    TestLogger.getEnabledLevels()
    TestLogger.setEnabledLevels(Level... levels)
    TestLoggerFactory.getLoggingEvents()
    TestLoggerFactory.clear()

all only affect state stored in a ThreadLocal, and thus tests which use them can
safely be parallelised without any danger of concurrent test runs affecting each
other.

### Testing Multiple Threads Logging

At times, however, it may be desirable to assert about the log messages produced
by concurrent code. To facilitate this SLF4J Test also maintains a record of
logging events from *all* threads. This state can be accessed and reset using
the following functions:

    TestLogger.getAllLoggingEvents()
    TestLogger.clearAll()
    TestLogger.setEnabledLevelsForAllThreads(Level... levels)
    TestLoggerFactory.getAllLoggingEvents()
    TestLoggerFactory.clearAll()


### Printing log statements to System out and err

It can still be useful to print log messages to System out/err as appropriate.
SLF4J Test will print messages using a standard (non-configurable) format based
on the value of the TestLoggerFactory's printLevel property. For convenience
this does respect the conventional level hierarchy where if the print level is
INFO logging events at levels WARN and ERROR will also be printed. A level that is
disabled [globally](#globally-disabling-a-log-level) will not be printed.

This can be set in any of the following ways:

#### Programmatically
    TestLoggerFactory.getInstance().setPrintLevel(Level.INFO);

#### Via a System Property
Run the JVM with the following:

    -Dslf4jtest.print.level=INFO

#### Via a properties file
Place a file called slf4jtest.properties on the classpath with the following
line in it:

    print.level=INFO

### Testing Code Using java.util.logging
Although this module is named SLF4J Test, it can be used to test code using
other test API's as well. In most cases, it is just a matter of placing the
proper bridge on the test classpath, see e.g.
[Bridging legacy APIs](https://www.slf4j.org/legacy.html).
But in case of java.util.logging, there is a little more work than adding
`jul-to-slf4j` to the classpath. Normally,
an application will create a `logging.properties` file containing

    handlers = org.slf4j.bridge.SLF4JBridgeHandler

and set the system property `java.util.logging.config.file` on the
command line to the full path of the file. But this is not
practical for running unit tests. Instead, the configuration is done
programmatically in the test code.

SLF4J Test has code to make this easier. Calling
[`JulConfig.setup()`](apidocs/com/github/valfirst/slf4jtest/JulConfig.html#setup--)
will do this for you. You should call this method before running any
test that requires java.util.logging to be configured. Calling this method more
than once has no effect, so you can safely call it before tests in all test
suites with this requirement, e.g. in a `@BeforeClass` method if using Junit 4.

If you use Junit 5, it is even easier. There is a Junit 5 extension
[`JulConfigExtension`](apidocs/apidocs/com/github/valfirst/slf4jtest/JulConfigExtension.html.html)
doing this declaratively. For example

    import com.github.valfirst.slf4jtest.JulConfigExtension;
    import org.junit.jupiter.api.extension.ExtendWith;
    
    @ExtendWith(JulConfigExtension.class)
    class JulLoggingTests {
        @Test
        void testJulLogging() {
            ...
        }
        ...
    }

### Customizing the MDC
The MDC (original meaning Mapped Diagnostic Context) is a place that can
be used to store additional information as key/value pairs. The
information is stored by thread. It can be set by the client code and used
by the logging backend.
The [specification](https://www.slf4j.org/apidocs/org/slf4j/MDC.html) of 
the MDC in SLF4J leaves much detail up to the logging backend. 

SLF4J Test lets the user customize the behavior of the MDC to more closely
match that of the backend expected to be used at run-time.

The configuration can be changed in the property file `slf4jtest.properties`,
using system properties specified as command line arguments to the JVM,
or programmatically. The property file and the system properties are read
at start-up time, with system properties overriding the property file.
Note that changing the values programmatically affects all threads.

#### Completely Disable the MDC
The backend does not even have to implement the MDC at all.
If the log4j2 adapter or logback is used, there is full support for the MDC.
Using the java.util.logging adapter, there is an MDC, but it cannot be used
by log appenders.
In the slf4j-simple, the MDC implementation is a no-op.

It is possible to disable the MDC completely by specifying

##### Programmatically

    TestMDCAdapter.getInstance().setEnable(false);

##### Via a System Property

    -Dslf4jtest.mdc.enable=false

##### In the slf4j.properties File

    mdc.enable=false

The default value is `true`. If disabled, the `get` and `getCopyOfContextMap`
methods return null, and all other methods are no-ops.

#### Inheritance from the Parent Thread
It is unspecified whether a child thread inherits the MDC from its parent.
In log4j2 and logback, the MDC is not inherited by default, but this
behavoir can be changed by the configuration. Using the java.util.logging
adapter, inheritance is always on.

##### Programmatically

    TestMDCAdapter.getInstance().setInherit(true);

##### Via a System Property

    -Dslf4jtest.mdc.inherit=true

##### In the slf4j.properties File

    mdc.inherit=true

The default value is `false`.

#### Allow null values
In the description of the
[`put`](https://www.slf4j.org/apidocs/org/slf4j/MDC.html#put-java.lang.String-java.lang.String-)
method, it says "The `val` parameter can be null only if the underlying
implementation supports it."
To emulate a backend that does not support null values, this can be configured.

##### Programmatically

    TestMDCAdapter.getInstance().setAllowNullValues(false);

##### Via a System Property

    -Dslf4jtest.mdc.allow.null.values=false

##### In the slf4j.properties File

    mdc.allow.null.values=false

The default value is `true`.
Note that if the MDC functionality has been completely disabled, null
values are accepted and ignored, regardless of this setting.

#### Null Returned for Empty Map
The description of the
[`getCopyOfContextMap`](https://www.slf4j.org/apidocs/org/slf4j/MDC.html#getCopyOfContextMap--)
method says "Returned value may be null.", but it is not specified under
which circumstances it may be null. Obviously, a backend that does not
support an MDC will return null. The implementation in the
java.util.logging adapter returns null, if `put` has not been called.
Other implementations may return an empty map in that case.
To change the behavior:

##### Programmatically

    TestMDCAdapter.getInstance().setReturnNullCopyWhenMdcNotSet(true);

##### Via a System Property

    -Dslf4jtest.mdc.return.null.copy.when.mdc.not.set=true

##### In the slf4j.properties File

    mdc.return.null.copy.when.mdc.not.set=true

The default value is `false`, in which case an empty map is returned
when the MDC is not set.
Note that if the MDC functionality has been completely disabled,
`null` is returned, regardless of this setting.

#### Restoring Options
To restore these options to the values defined by the initial
configuration, use

    TestMDCAdapter.getInstance().restoreOptions();

### Using the Fluent API
From SLF4J version 2.0, there is a new fluent logging API.
It allows writing code like

    logger.atInfo().setMessage("With an argument {}")
        .addArgument(() -> myObj.calculateValue())
        .addMarker("ForYourEyesOnly").log();

The `atInfo()` method returns a `LoggingEventBuilder` which is used to
build the event and finally log it.

If logging at INFO level is not enabled, a NOOP `LoggingEventBuilder` is 
returned by `atInfo`. This means the lambda on the second line will not be
executed, thus saving the overhead if the message is not logged.

Note that it is possible to add multiple markers to an event, 
which is not possible with the classic API.

SLF4J Test uses the `TestLoggingEventBuilder`, which is a modified version of
the `DefaultLoggingEventBuilder` of SLF4J.

Creating an SLF4J Test `LoggingEvent` to compare against can be done
in the classic way like

    LoggingEvent.info(
        MarkerFactory.getMarker("ForYourEyesOnly"),
        "With an argument {}",
        myObj.calculateValue());

Alternatively, you can create the `LoggingEvent` from an existing SLF4J
logging event, like

    
    LoggingEvent.fromSlf4jEvent(
        new TestLoggingEventBuilder(null, Level.INFO)
            .setMessage("With an argument {}")
            .addArgument(() -> myObj.calculateValue())
            .addMarker(MarkerFactory.getMarker("ForYourEyesOnly"))
            .toLoggingEvent());

This approach is necessary if you use the features available
in the fluent API only. This includes multiple markers and key/value pairs.

The `TestLoggingEventBuilder` adds the `toLoggingEvent` method
to access the created event. Please note that the returned value is an
`org.slf4j.event.LoggingEvent`,
which is different from `com.github.valfirst.slf4jtest.LoggingEvent`.
