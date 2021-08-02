package com.github.valfirst.slf4jtest;

import static com.github.valfirst.slf4jtest.LoggingEvent.debug;
import static com.github.valfirst.slf4jtest.LoggingEvent.info;
import static com.github.valfirst.slf4jtest.LoggingEvent.trace;
import static com.github.valfirst.slf4jtest.TestLoggerFactory.getInstance;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static uk.org.lidalia.slf4jext.Level.WARN;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.org.lidalia.slf4jext.Level;

@RunWith(PowerMockRunner.class)
public class TestLoggerFactoryTests {

    @Test
    public void getLoggerDifferentNames() {
        TestLogger logger1 = getInstance().getLogger("name1");
        TestLogger logger2 = getInstance().getLogger("name2");

        assertNotSame(logger1, logger2);
    }

    @Test
    public void getLoggerSameNames() {
        TestLogger logger1 = getInstance().getLogger("name1");
        TestLogger logger2 = getInstance().getLogger("name1");

        assertSame(logger1, logger2);
    }

    @Test
    public void staticGetTestLoggerStringReturnsSame() {
        TestLogger logger1 = TestLoggerFactory.getTestLogger("name1");
        TestLogger logger2 = getInstance().getLogger("name1");

        assertSame(logger1, logger2);
    }

    @Test
    public void staticGetTestLoggerClassReturnsSame() {
        TestLogger logger1 = TestLoggerFactory.getTestLogger(String.class);
        TestLogger logger2 = getInstance().getLogger("java.lang.String");

        assertSame(logger1, logger2);
    }

    @Test
    public void clear() {
        TestLogger logger1 = getInstance().getLogger("name1");
        logger1.trace("hello");
        assertThat(logger1.getLoggingEvents().size(), is(1));
        TestLogger logger2 = getInstance().getLogger("name2");
        logger2.trace("world");
        assertThat(logger2.getLoggingEvents().size(), is(1));

        TestLoggerFactory.clear();

        assertThat(logger1.getLoggingEvents(), is(empty()));
        assertThat(logger2.getLoggingEvents(), is(empty()));
        assertThat(TestLoggerFactory.getLoggingEvents(), is(empty()));
    }

    @Test
    public void getAllLoggingEvents() {
        TestLogger logger1 = getInstance().getLogger("name1");
        TestLogger logger2 = getInstance().getLogger("name2");
        logger1.trace("hello");
        logger2.trace("world");
        logger1.trace("here");
        logger2.trace("I am");

        assertThat(
                TestLoggerFactory.getLoggingEvents(),
                is(asList(trace("hello"), trace("world"), trace("here"), trace("I am"))));
    }

    @Test
    public void getAllLoggingEventsDoesNotAddToMultipleLoggers() {
        TestLogger logger1 = getInstance().getLogger("name1");
        TestLogger logger2 = getInstance().getLogger("name2");
        logger1.trace("hello");
        logger2.trace("world");

        assertThat(logger1.getLoggingEvents(), is(singletonList(trace("hello"))));
        assertThat(logger2.getLoggingEvents(), is(singletonList(trace("world"))));
    }

    @Test
    public void getAllLoggingEventsDoesNotGetEventsForLoggersNotEnabled() {
        TestLogger logger = getInstance().getLogger("name1");
        logger.setEnabledLevels(WARN);
        logger.info("hello");

        assertThat(TestLoggerFactory.getLoggingEvents(), is(empty()));
    }

    @Test
    public void getAllTestLoggers() {
        TestLogger logger1 = getInstance().getLogger("name1");
        TestLogger logger2 = getInstance().getLogger("name2");
        Map<String, TestLogger> expected = new HashMap<>();
        expected.put("name1", logger1);
        expected.put("name2", logger2);
        assertThat(TestLoggerFactory.getAllTestLoggers(), is(expected));
    }

    @Test
    public void clearDoesNotRemoveLoggers() {
        TestLogger logger1 = getInstance().getLogger("name1");
        TestLoggerFactory.clear();

        assertThat(
                TestLoggerFactory.getAllTestLoggers(), is(Collections.singletonMap("name1", logger1)));
    }

    @Test
    public void resetRemovesAllLoggers() {
        getInstance().getLogger("name1");

        TestLoggerFactory.reset();

        final Map<String, TestLogger> emptyMap = Collections.emptyMap();
        assertThat(TestLoggerFactory.getAllTestLoggers(), is(emptyMap));
    }

    @Test
    public void resetRemovesAllLoggingEvents() {
        getInstance().getLogger("name1").info("hello");

        TestLoggerFactory.reset();

        assertThat(TestLoggerFactory.getLoggingEvents(), is(empty()));
    }

    @Test
    public void getLoggingEventsReturnsCopyNotView() {
        getInstance().getLogger("name1").debug("hello");
        List<LoggingEvent> loggingEvents = TestLoggerFactory.getLoggingEvents();
        getInstance().getLogger("name1").info("world");
        assertThat(loggingEvents, is(singletonList(debug("hello"))));
    }

    @Test
    public void getLoggingEventsReturnsUnmodifiableList() {
        List<LoggingEvent> loggingEvents = TestLoggerFactory.getLoggingEvents();
        LoggingEvent loggingEvent = debug("hello");
        assertThrows(UnsupportedOperationException.class, () -> loggingEvents.add(loggingEvent));
    }

    @Test
    public void getAllLoggersReturnsCopyNotView() {
        TestLogger logger1 = getInstance().getLogger("name1");
        Map<String, TestLogger> allTestLoggers = TestLoggerFactory.getAllTestLoggers();
        getInstance().getLogger("name2");

        assertThat(allTestLoggers, is(Collections.singletonMap("name1", logger1)));
    }

    @Test
    public void getAllLoggersReturnsUnmodifiableList() {
        Map<String, TestLogger> allTestLoggers = TestLoggerFactory.getAllTestLoggers();
        TestLogger newLogger = new TestLogger("newlogger", getInstance());
        assertThrows(
                UnsupportedOperationException.class, () -> allTestLoggers.put("newlogger", newLogger));
    }

    @Test
    public void getLoggingEventsOnlyReturnsEventsLoggedInThisThread() throws InterruptedException {
        Thread t = new Thread(() -> TestLoggerFactory.getTestLogger("name1").info("hello"));
        t.start();
        t.join();
        assertThat(TestLoggerFactory.getLoggingEvents(), is(empty()));
    }

    @Test
    public void getAllLoggingEventsReturnsEventsLoggedInAllThreads() throws InterruptedException {
        Thread t = new Thread(() -> TestLoggerFactory.getTestLogger("name1").info("message1"));
        t.start();
        t.join();
        TestLoggerFactory.getTestLogger("name1").info("message2");
        assertThat(
                TestLoggerFactory.getAllLoggingEvents(), is(asList(info("message1"), info("message2"))));
    }

    @Test
    public void clearOnlyClearsEventsLoggedInThisThread() throws InterruptedException {
        final TestLogger logger = TestLoggerFactory.getTestLogger("name");
        Thread t = new Thread(() -> logger.info("hello"));
        t.start();
        t.join();
        TestLoggerFactory.clear();
        assertThat(TestLoggerFactory.getAllLoggingEvents(), is(singletonList(info("hello"))));
    }

    @Test
    public void clearAllClearsEventsLoggedInAllThreads() throws InterruptedException {
        final TestLogger logger1 = TestLoggerFactory.getTestLogger("name1");
        final TestLogger logger2 = TestLoggerFactory.getTestLogger("name2");
        logger1.info("hello11");
        logger2.info("hello21");
        Thread t =
                new Thread(
                        () -> {
                            logger1.info("hello12");
                            logger2.info("hello22");
                            TestLoggerFactory.clearAll();
                        });
        t.start();
        t.join();
        assertThat(TestLoggerFactory.getLoggingEvents(), is(empty()));
        assertThat(TestLoggerFactory.getAllLoggingEvents(), is(empty()));
        assertThat(logger1.getLoggingEvents(), is(empty()));
        assertThat(logger1.getAllLoggingEvents(), is(empty()));
        assertThat(logger2.getLoggingEvents(), is(empty()));
        assertThat(logger2.getAllLoggingEvents(), is(empty()));
    }

    @Test
    public void defaultPrintLevelIsOff() {
        assertThat(getInstance().getPrintLevel(), is(Level.OFF));
    }

    @Test
    @PrepareForTest(TestLoggerFactory.class)
    public void printLevelTakenFromOverridableProperties() throws Exception {
        final OverridableProperties properties = mock(OverridableProperties.class);
        whenNew(OverridableProperties.class).withArguments("slf4jtest").thenReturn(properties);
        when(properties.getProperty("print.level", "OFF")).thenReturn("INFO");

        assertThat(getInstance().getPrintLevel(), is(Level.INFO));
    }

    @Test
    @PrepareForTest(TestLoggerFactory.class)
    public void printLevelInvalidInOverridableProperties() throws Exception {
        final OverridableProperties properties = mock(OverridableProperties.class);
        whenNew(OverridableProperties.class).withArguments("slf4jtest").thenReturn(properties);
        final String invalidLevelName = "nonsense";
        when(properties.getProperty("print.level", "OFF")).thenReturn(invalidLevelName);

        final IllegalStateException illegalStateException =
                assertThrows(IllegalStateException.class, TestLoggerFactory::getInstance);
        assertThat(
                illegalStateException.getMessage(),
                is(
                        "Invalid level name in property print.level of file slf4jtest.properties "
                                + "or System property slf4jtest.print.level"));
        assertThat(illegalStateException.getCause(), instanceOf(IllegalArgumentException.class));
        assertThat(
                illegalStateException.getCause().getMessage(),
                is("No enum constant " + Level.class.getName() + "." + invalidLevelName));
    }

    @Test
    @PrepareForTest(TestLoggerFactory.class)
    public void ioExceptionAtPropertiesLoading() throws Exception {
        IOException ioException = new IOException();
        whenNew(OverridableProperties.class).withArguments("slf4jtest").thenThrow(ioException);

        final UncheckedIOException uncheckedIOException =
                assertThrows(UncheckedIOException.class, TestLoggerFactory::getInstance);
        assertThat(uncheckedIOException.getCause(), is(ioException));
    }

    @Test
    public void setLevel() {
        for (Level printLevel : Level.values()) {
            getInstance().setPrintLevel(printLevel);
            assertThat(getInstance().getPrintLevel(), is(printLevel));
        }
    }

    @After
    public void resetLoggerFactory() {
        try {
            TestLoggerFactory.reset();
            getInstance().setPrintLevel(Level.OFF);
        } catch (IllegalStateException | UncheckedIOException e) {
            // ignore
        }
    }
}
