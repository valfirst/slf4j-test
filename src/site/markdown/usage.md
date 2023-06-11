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

    TestLoggerFactory.clear()

in a tear down method of some kind. If you are using JUnit then SLF4J Test
provides a Rule that will do this for you if you provide the following line in
your test class:

    @Rule public TestRule resetLoggingEvents = new TestLoggerFactoryResetRule();

More nuanced state resetting can be done on a per logger basis:

    TestLogger.clear()

or more aggressive clears will reset state across all threads:

    TestLoggerFactory.clearAll()
    TestLogger.clearAll()

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
programatically in the test code.

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
