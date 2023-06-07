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
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.slf4j.event.Level.WARN;

import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;

class TestLoggerFactoryTests {

    @Test
    void getLoggerDifferentNames() {
        TestLogger logger1 = getInstance().getLogger("name1");
        TestLogger logger2 = getInstance().getLogger("name2");

        assertNotSame(logger1, logger2);
    }

    @Test
    void getLoggerSameNames() {
        TestLogger logger1 = getInstance().getLogger("name1");
        TestLogger logger2 = getInstance().getLogger("name1");

        assertSame(logger1, logger2);
    }

    @Test
    void staticGetTestLoggerStringReturnsSame() {
        TestLogger logger1 = TestLoggerFactory.getTestLogger("name1");
        TestLogger logger2 = getInstance().getLogger("name1");

        assertSame(logger1, logger2);
    }

    @Test
    void staticGetTestLoggerClassReturnsSame() {
        TestLogger logger1 = TestLoggerFactory.getTestLogger(String.class);
        TestLogger logger2 = getInstance().getLogger("java.lang.String");

        assertSame(logger1, logger2);
    }

    @Test
    void clear() {
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
    void getAllLoggingEvents() {
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
    void getAllLoggingEventsDoesNotAddToMultipleLoggers() {
        TestLogger logger1 = getInstance().getLogger("name1");
        TestLogger logger2 = getInstance().getLogger("name2");
        logger1.trace("hello");
        logger2.trace("world");

        assertThat(logger1.getLoggingEvents(), is(singletonList(trace("hello"))));
        assertThat(logger2.getLoggingEvents(), is(singletonList(trace("world"))));
    }

    @Test
    void getAllLoggingEventsDoesNotGetEventsForLoggersNotEnabled() {
        TestLogger logger = getInstance().getLogger("name1");
        logger.setEnabledLevels(WARN);
        logger.info("hello");

        assertThat(TestLoggerFactory.getLoggingEvents(), is(empty()));
    }

    @Test
    void getAllTestLoggers() {
        TestLogger logger1 = getInstance().getLogger("name1");
        TestLogger logger2 = getInstance().getLogger("name2");
        Map<String, TestLogger> expected = new HashMap<>();
        expected.put("name1", logger1);
        expected.put("name2", logger2);
        assertThat(TestLoggerFactory.getAllTestLoggers(), is(expected));
    }

    @Test
    void clearDoesNotRemoveLoggers() {
        TestLogger logger1 = getInstance().getLogger("name1");
        TestLoggerFactory.clear();

        assertThat(
                TestLoggerFactory.getAllTestLoggers(), is(Collections.singletonMap("name1", logger1)));
    }

    @Test
    void resetRemovesAllLoggers() {
        getInstance().getLogger("name1");

        TestLoggerFactory.reset();

        final Map<String, TestLogger> emptyMap = Collections.emptyMap();
        assertThat(TestLoggerFactory.getAllTestLoggers(), is(emptyMap));
    }

    @Test
    void resetRemovesAllLoggingEvents() {
        getInstance().getLogger("name1").info("hello");

        TestLoggerFactory.reset();

        assertThat(TestLoggerFactory.getLoggingEvents(), is(empty()));
    }

    @Test
    void getLoggingEventsReturnsCopyNotView() {
        getInstance().getLogger("name1").debug("hello");
        List<LoggingEvent> loggingEvents = TestLoggerFactory.getLoggingEvents();
        getInstance().getLogger("name1").info("world");
        assertThat(loggingEvents, is(singletonList(debug("hello"))));
    }

    @Test
    void getLoggingEventsReturnsUnmodifiableList() {
        List<LoggingEvent> loggingEvents = TestLoggerFactory.getLoggingEvents();
        LoggingEvent loggingEvent = debug("hello");
        assertThrows(UnsupportedOperationException.class, () -> loggingEvents.add(loggingEvent));
    }

    @Test
    void getAllLoggersReturnsCopyNotView() {
        TestLogger logger1 = getInstance().getLogger("name1");
        Map<String, TestLogger> allTestLoggers = TestLoggerFactory.getAllTestLoggers();
        getInstance().getLogger("name2");

        assertThat(allTestLoggers, is(Collections.singletonMap("name1", logger1)));
    }

    @Test
    void getAllLoggersReturnsUnmodifiableList() {
        Map<String, TestLogger> allTestLoggers = TestLoggerFactory.getAllTestLoggers();
        TestLogger newLogger = new TestLogger("newlogger", getInstance());
        assertThrows(
                UnsupportedOperationException.class, () -> allTestLoggers.put("newlogger", newLogger));
    }

    @Test
    void getLoggingEventsOnlyReturnsEventsLoggedInThisThread() throws InterruptedException {
        Thread t = new Thread(() -> TestLoggerFactory.getTestLogger("name1").info("hello"));
        t.start();
        t.join();
        assertThat(TestLoggerFactory.getLoggingEvents(), is(empty()));
    }

    @Test
    void getAllLoggingEventsReturnsEventsLoggedInAllThreads() throws InterruptedException {
        Thread t = new Thread(() -> TestLoggerFactory.getTestLogger("name1").info("message1"));
        t.start();
        t.join();
        TestLoggerFactory.getTestLogger("name1").info("message2");
        assertThat(
                TestLoggerFactory.getAllLoggingEvents(), is(asList(info("message1"), info("message2"))));
    }

    @Test
    void clearOnlyClearsEventsLoggedInThisThread() throws InterruptedException {
        final TestLogger logger = TestLoggerFactory.getTestLogger("name");
        Thread t = new Thread(() -> logger.info("hello"));
        t.start();
        t.join();
        TestLoggerFactory.clear();
        assertThat(TestLoggerFactory.getAllLoggingEvents(), is(singletonList(info("hello"))));
    }

    @Test
    void clearAllClearsEventsLoggedInAllThreads() throws InterruptedException {
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
    void defaultPrintLevelIsOff() {
        assertThat(getInstance().getPrintLevel(), is(nullValue()));
    }

    @Test
    void printLevelTakenFromOverridableProperties() throws Exception {
        final OverridableProperties properties = mock(OverridableProperties.class);
        when(properties.getProperty("print.level", "OFF")).thenReturn("INFO");
        when(properties.getProperty("capture.level", "TRACE")).thenReturn("INFO");

        assertThat(TestLoggerFactory.createInstance(properties).getPrintLevel(), is(Level.INFO));
    }

    @Test
    void printLevelInvalidInOverridableProperties() throws Exception {
        final OverridableProperties properties = mock(OverridableProperties.class);
        final String invalidLevelName = "nonsense";
        when(properties.getProperty("print.level", "OFF")).thenReturn(invalidLevelName);

        final IllegalStateException illegalStateException =
                assertThrows(
                        IllegalStateException.class, () -> TestLoggerFactory.createInstance(properties));
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
    void defaultCaptureLevelIsTrace() {
        assertThat(getInstance().getCaptureLevel(), is(Level.TRACE));
    }

    @Test
    void captureLevelTakenFromOverridableProperties() throws Exception {
        final OverridableProperties properties = mock(OverridableProperties.class);
        when(properties.getProperty("print.level", "OFF")).thenReturn("INFO");
        when(properties.getProperty("capture.level", "TRACE")).thenReturn("INFO");

        assertThat(TestLoggerFactory.createInstance(properties).getCaptureLevel(), is(Level.INFO));
    }

    @Test
    void captureLevelInvalidInOverridableProperties() throws Exception {
        final OverridableProperties properties = mock(OverridableProperties.class);
        when(properties.getProperty("print.level", "OFF")).thenReturn("INFO");
        final String invalidLevelName = "nonsense";
        when(properties.getProperty("capture.level", "TRACE")).thenReturn(invalidLevelName);

        final IllegalStateException illegalStateException =
                assertThrows(
                        IllegalStateException.class, () -> TestLoggerFactory.createInstance(properties));
        assertThat(
                illegalStateException.getMessage(),
                is(
                        "Invalid level name in property capture.level of file slf4jtest.properties "
                                + "or System property slf4jtest.capture.level"));
        assertThat(illegalStateException.getCause(), instanceOf(IllegalArgumentException.class));
        assertThat(
                illegalStateException.getCause().getMessage(),
                is("No enum constant " + Level.class.getName() + "." + invalidLevelName));
    }

    @Test
    void setLevel() {
        for (Level printLevel : Level.values()) {
            getInstance().setPrintLevel(printLevel);
            assertThat(getInstance().getPrintLevel(), is(printLevel));
        }
    }

    @Test
    void defaultConstructor() {
        TestLoggerFactory testLoggerFactory = new TestLoggerFactory();
        assertEquals(Level.TRACE, testLoggerFactory.getCaptureLevel(), "capture level");
        assertNull(testLoggerFactory.getPrintLevel(), "print level");
    }

    @Test
    void oneArgConstructor() {
        TestLoggerFactory testLoggerFactory = new TestLoggerFactory(Level.INFO);
        assertEquals(Level.TRACE, testLoggerFactory.getCaptureLevel(), "capture level");
        assertEquals(Level.INFO, testLoggerFactory.getPrintLevel(), "print level");
    }

    @AfterEach
    void resetLoggerFactory() {
        try {
            TestLoggerFactory.reset();
            getInstance().setPrintLevel(null);
        } catch (IllegalStateException | UncheckedIOException e) {
            // ignore
        }
    }
}
