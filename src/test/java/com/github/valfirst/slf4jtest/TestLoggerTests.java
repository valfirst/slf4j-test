package com.github.valfirst.slf4jtest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.slf4j.Marker;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jext.Logger;

import static com.github.valfirst.slf4jtest.LoggingEvent.debug;
import static com.github.valfirst.slf4jtest.LoggingEvent.error;
import static com.github.valfirst.slf4jtest.LoggingEvent.info;
import static com.github.valfirst.slf4jtest.LoggingEvent.trace;
import static com.github.valfirst.slf4jtest.LoggingEvent.warn;
import static com.google.common.collect.Sets.difference;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static uk.org.lidalia.slf4jext.Level.DEBUG;
import static uk.org.lidalia.slf4jext.Level.ERROR;
import static uk.org.lidalia.slf4jext.Level.INFO;
import static uk.org.lidalia.slf4jext.Level.TRACE;
import static uk.org.lidalia.slf4jext.Level.WARN;
import static uk.org.lidalia.slf4jext.Level.enablableValueSet;

class TestLoggerTests extends StdIoTests {

    private static final String LOGGER_NAME = "uk.org";
    private final TestLogger testLogger = new TestLogger(LOGGER_NAME, TestLoggerFactory.getInstance());
    private final Marker marker = mock(Marker.class);
    private final String message = "message {} {} {}";
    private final Object arg1 = "arg1";
    private final Object arg2 = "arg2";
    private final Object[] args = new Object[]{arg1, arg2, "arg3"};
    private final Throwable throwable = new Throwable();
    private final Object[] argsWithThrowable = new Object[]{arg1, arg2, "arg3", throwable};

    private final Map<String, String> mdcValues = new HashMap<>();

    @BeforeEach
    void beforeEach() {
        mdcValues.put("key1", "value1");
        mdcValues.put("key2", "value2");
        MDC.setContextMap(mdcValues);
    }

    @AfterEach
    void afterEach() {
        MDC.clear();
        TestLoggerFactory.reset();
        TestLoggerFactory.getInstance().setPrintLevel(Level.OFF);
    }

    @Test
    void name() {
        String name = UUID.randomUUID().toString();
        TestLogger logger = new TestLogger(name, TestLoggerFactory.getInstance());
        assertEquals(name, logger.getName());
    }

    @Test
    void clearRemovesEvents() {
        testLogger.debug("message1");
        testLogger.debug("message2");
        assertEquals(asList(debug(mdcValues, "message1"), debug(mdcValues, "message2")), testLogger.getLoggingEvents());
        testLogger.clear();
        assertEquals(emptyList(), testLogger.getLoggingEvents());
    }

    @Test
    void clearResetsLevel() {
        testLogger.setEnabledLevels();
        testLogger.clear();
        assertEquals(newHashSet(TRACE, DEBUG, INFO, WARN, ERROR), testLogger.getEnabledLevels());
    }

    @Test
    void traceEnabled() {
        assertEnabledReturnsCorrectly(TRACE);
    }

    @Test
    void traceMessage() {
        testLogger.trace(message);

        assertEquals(singletonList(trace(mdcValues, message)), testLogger.getLoggingEvents());
    }

    @Test
    void traceMessageOneArg() {
        testLogger.trace(message, arg1);

        assertEquals(singletonList(trace(mdcValues, message, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    void traceMessageTwoArgs() {
        testLogger.trace(message, arg1, arg2);

        assertEquals(singletonList(trace(mdcValues, message, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    void traceMessageManyArgs() {
        testLogger.trace(message, args);

        assertEquals(singletonList(trace(mdcValues, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    void traceMessageManyArgsWithThrowable() {
        testLogger.trace(message, argsWithThrowable);

        assertEquals(singletonList(trace(mdcValues, throwable, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    void traceMessageThrowable() {
        testLogger.trace(message, throwable);

        assertEquals(singletonList(trace(mdcValues, throwable, message)), testLogger.getLoggingEvents());
    }

    @Test
    void traceEnabledMarker() {
        assertEnabledReturnsCorrectly(TRACE, marker);
    }

    @Test
    void traceMarkerMessage() {
        testLogger.trace(marker, message);

        assertEquals(singletonList(trace(mdcValues, marker, message)), testLogger.getLoggingEvents());
    }

    @Test
    void traceMarkerMessageOneArg() {
        testLogger.trace(marker, message, arg1);

        assertEquals(singletonList(trace(mdcValues, marker, message, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    void traceMarkerMessageTwoArgs() {
        testLogger.trace(marker, message, arg1, arg2);

        assertEquals(singletonList(trace(mdcValues, marker, message, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    void traceMarkerMessageManyArgs() {
        testLogger.trace(marker, message, args);

        assertEquals(singletonList(trace(mdcValues, marker, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    void traceMarkerMessageManyArgsWithThrowable() {
        testLogger.trace(marker, message, argsWithThrowable);

        assertEquals(singletonList(trace(mdcValues, marker, throwable, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    void traceMarkerMessageThrowable() {
        testLogger.trace(marker, message, throwable);

        assertEquals(singletonList(trace(mdcValues, marker, throwable, message)), testLogger.getLoggingEvents());
    }

    @Test
    void debugEnabled() {
        assertEnabledReturnsCorrectly(DEBUG);
    }

    @Test
    void debugMessage() {
        testLogger.debug(message);

        assertEquals(singletonList(debug(mdcValues, message)), testLogger.getLoggingEvents());
    }

    @Test
    void debugMessageOneArg() {
        testLogger.debug(message, arg1);

        assertEquals(singletonList(debug(mdcValues, message, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    void debugMessageTwoArgs() {
        testLogger.debug(message, arg1, arg2);

        assertEquals(singletonList(debug(mdcValues, message, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    void debugMessageManyArgs() {
        testLogger.debug(message, args);

        assertEquals(singletonList(debug(mdcValues, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    void debugMessageManyArgsWithThrowable() {
        testLogger.debug(message, argsWithThrowable);

        assertEquals(singletonList(debug(mdcValues, throwable, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    void debugMessageThrowable() {
        testLogger.debug(message, throwable);

        assertEquals(singletonList(debug(mdcValues, throwable, message)), testLogger.getLoggingEvents());
    }

    @Test
    void debugEnabledMarker() {
        assertEnabledReturnsCorrectly(DEBUG, marker);
    }

    @Test
    void debugMarkerMessage() {
        testLogger.debug(marker, message);

        assertEquals(singletonList(debug(mdcValues, marker, message)), testLogger.getLoggingEvents());
    }

    @Test
    void debugMarkerMessageOneArg() {
        testLogger.debug(marker, message, arg1);

        assertEquals(singletonList(debug(mdcValues, marker, message, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    void debugMarkerMessageTwoArgs() {
        testLogger.debug(marker, message, arg1, arg2);

        assertEquals(singletonList(debug(mdcValues, marker, message, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    void debugMarkerMessageManyArgs() {
        testLogger.debug(marker, message, args);

        assertEquals(singletonList(debug(mdcValues, marker, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    void debugMarkerMessageManyArgsWithThrowable() {
        testLogger.debug(marker, message, argsWithThrowable);

        assertEquals(singletonList(debug(mdcValues, marker, throwable, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    void debugMarkerMessageThrowable() {
        testLogger.debug(marker, message, throwable);

        assertEquals(singletonList(debug(mdcValues, marker, throwable, message)), testLogger.getLoggingEvents());
    }

    @Test
    void infoEnabled() {
        assertEnabledReturnsCorrectly(INFO);
    }

    @Test
    void infoMessage() {
        testLogger.info(message);

        assertEquals(singletonList(info(mdcValues, message)), testLogger.getLoggingEvents());
    }

    @Test
    void infoMessageOneArg() {
        testLogger.info(message, arg1);

        assertEquals(singletonList(info(mdcValues, message, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    void infoMessageTwoArgs() {
        testLogger.info(message, arg1, arg2);

        assertEquals(singletonList(info(mdcValues, message, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    void infoMessageManyArgs() {
        testLogger.info(message, args);

        assertEquals(singletonList(info(mdcValues, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    void infoMessageManyArgsWithThrowable() {
        testLogger.info(message, argsWithThrowable);

        assertEquals(singletonList(info(mdcValues, throwable, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    void infoMessageThrowable() {
        testLogger.info(message, throwable);

        assertEquals(singletonList(info(mdcValues, throwable, message)), testLogger.getLoggingEvents());
    }

    @Test
    void infoEnabledMarker() {
        assertEnabledReturnsCorrectly(INFO, marker);
    }

    @Test
    void infoMarkerMessage() {
        testLogger.info(marker, message);

        assertEquals(singletonList(info(mdcValues, marker, message)), testLogger.getLoggingEvents());
    }

    @Test
    void infoMarkerMessageOneArg() {
        testLogger.info(marker, message, arg1);

        assertEquals(singletonList(info(mdcValues, marker, message, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    void infoMarkerMessageTwoArgs() {
        testLogger.info(marker, message, arg1, arg2);

        assertEquals(singletonList(info(mdcValues, marker, message, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    void infoMarkerMessageManyArgs() {
        testLogger.info(marker, message, args);

        assertEquals(singletonList(info(mdcValues, marker, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    void infoMarkerMessageManyArgsWithThrowable() {
        testLogger.info(marker, message, argsWithThrowable);

        assertEquals(singletonList(info(mdcValues, marker, throwable, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    void infoMarkerMessageThrowable() {
        testLogger.info(marker, message, throwable);

        assertEquals(singletonList(info(mdcValues, marker, throwable, message)), testLogger.getLoggingEvents());
    }

    @Test
    void warnEnabled() {
        assertEnabledReturnsCorrectly(WARN);
    }

    @Test
    void warnMessage() {
        testLogger.warn(message);

        assertEquals(singletonList(warn(mdcValues, message)), testLogger.getLoggingEvents());
    }

    @Test
    void warnMessageOneArg() {
        testLogger.warn(message, arg1);

        assertEquals(singletonList(warn(mdcValues, message, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    void warnMessageTwoArgs() {
        testLogger.warn(message, arg1, arg2);

        assertEquals(singletonList(warn(mdcValues, message, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    void warnMessageManyArgs() {
        testLogger.warn(message, args);

        assertEquals(singletonList(warn(mdcValues, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    void warnMessageManyArgsWithThrowable() {
        testLogger.warn(message, argsWithThrowable);

        assertEquals(singletonList(warn(mdcValues, throwable, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    void warnMessageThrowable() {
        testLogger.warn(message, throwable);

        assertEquals(singletonList(warn(mdcValues, throwable, message)), testLogger.getLoggingEvents());
    }

    @Test
    void warnEnabledMarker() {
        assertEnabledReturnsCorrectly(WARN, marker);
    }

    @Test
    void warnMarkerMessage() {
        testLogger.warn(marker, message);

        assertEquals(singletonList(warn(mdcValues, marker, message)), testLogger.getLoggingEvents());
    }

    @Test
    void warnMarkerMessageOneArg() {
        testLogger.warn(marker, message, arg1);

        assertEquals(singletonList(warn(mdcValues, marker, message, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    void warnMarkerMessageTwoArgs() {
        testLogger.warn(marker, message, arg1, arg2);

        assertEquals(singletonList(warn(mdcValues, marker, message, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    void warnMarkerMessageManyArgs() {
        testLogger.warn(marker, message, args);

        assertEquals(singletonList(warn(mdcValues, marker, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    void warnMarkerMessageManyArgsWithThrowable() {
        testLogger.warn(marker, message, argsWithThrowable);

        assertEquals(singletonList(warn(mdcValues, marker, throwable, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    void warnMarkerMessageThrowable() {
        testLogger.warn(marker, message, throwable);

        assertEquals(singletonList(warn(mdcValues, marker, throwable, message)), testLogger.getLoggingEvents());
    }

    @Test
    void errorEnabled() {
        assertEnabledReturnsCorrectly(ERROR);
    }

    @Test
    void errorMessage() {
        testLogger.error(message);

        assertEquals(singletonList(error(mdcValues, message)), testLogger.getLoggingEvents());
    }

    @Test
    void errorMessageOneArg() {
        testLogger.error(message, arg1);

        assertEquals(singletonList(error(mdcValues, message, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    void errorMessageTwoArgs() {
        testLogger.error(message, arg1, arg2);

        assertEquals(singletonList(error(mdcValues, message, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    void errorMessageManyArgs() {
        testLogger.error(message, args);

        assertEquals(singletonList(error(mdcValues, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    void errorMessageManyArgsWithThrowable() {
        testLogger.error(message, argsWithThrowable);

        assertEquals(singletonList(error(mdcValues, throwable, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    void errorMessageThrowable() {
        testLogger.error(message, throwable);

        assertEquals(singletonList(error(mdcValues, throwable, message)), testLogger.getLoggingEvents());
    }

    @Test
    void errorEnabledMarker() {
        assertEnabledReturnsCorrectly(ERROR, marker);
    }

    @Test
    void errorMarkerMessage() {
        testLogger.error(marker, message);

        assertEquals(singletonList(error(mdcValues, marker, message)), testLogger.getLoggingEvents());
    }

    @Test
    void errorMarkerMessageOneArg() {
        testLogger.error(marker, message, arg1);

        assertEquals(singletonList(error(mdcValues, marker, message, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    void errorMarkerMessageTwoArgs() {
        testLogger.error(marker, message, arg1, arg2);

        assertEquals(singletonList(error(mdcValues, marker, message, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    void errorMarkerMessageManyArgs() {
        testLogger.error(marker, message, args);

        assertEquals(singletonList(error(mdcValues, marker, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    void errorMarkerMessageManyArgsWithThrowable() {
        testLogger.error(marker, message, argsWithThrowable);

        assertEquals(singletonList(error(mdcValues, marker, throwable, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    void errorMarkerMessageThrowable() {
        testLogger.error(marker, message, throwable);

        assertEquals(singletonList(error(mdcValues, marker, throwable, message)), testLogger.getLoggingEvents());
    }

    @Test
    void loggerSetToOff() {
        logsIfEnabled();
    }

    @Test
    void loggerSetToError() {
        logsIfEnabled(ERROR);
    }

    @Test
    void loggerSetToWarn() {
        logsIfEnabled(ERROR, WARN);
    }

    @Test
    void loggerSetToInfo() {
        logsIfEnabled(ERROR, WARN, INFO);
    }

    @Test
    void loggerSetToDebug() {
        logsIfEnabled(ERROR, WARN, INFO, DEBUG);
    }

    @Test
    void loggerSetToTrace() {
        logsIfEnabled(ERROR, WARN, INFO, DEBUG, TRACE);
    }

    private void logsIfEnabled(Level... shouldLog) {
        testLogger.setEnabledLevels(shouldLog);
        testLogger.error(message);
        testLogger.warn(message);
        testLogger.info(message);
        testLogger.debug(message);
        testLogger.trace(message);

        List<LoggingEvent> expectedEvents = Arrays.stream(shouldLog).map(
                level -> new LoggingEvent(level, mdcValues, message)).collect(Collectors.toList());

        assertEquals(expectedEvents, testLogger.getLoggingEvents());
        testLogger.clear();
    }

    @Test
    void getLoggingEventsReturnsCopyNotView() {
        testLogger.debug(message);
        List<LoggingEvent> loggingEvents = testLogger.getLoggingEvents();
        testLogger.info(message);

        assertEquals(singletonList(debug(mdcValues, message)), loggingEvents);
    }

    @Test
    void getLoggingEventsReturnsUnmodifiableList() {
        List<LoggingEvent> loggingEvents = testLogger.getLoggingEvents();
        LoggingEvent debugEvent = debug("hello");
        assertThrows(UnsupportedOperationException.class, () -> loggingEvents.add(debugEvent));
    }

    @Test
    void getLoggingEventsOnlyReturnsEventsLoggedInThisThread() throws InterruptedException {
        Thread t = new Thread(() -> testLogger.info(message));
        t.start();
        t.join();
        assertEquals(emptyList(), testLogger.getLoggingEvents());
    }

    @Test
    void getAllLoggingEventsReturnsEventsLoggedInAllThreads() throws InterruptedException {
        Thread t = new Thread(() -> testLogger.info(message));
        t.start();
        t.join();
        testLogger.info(message);
        assertEquals(asList(info(message), info(mdcValues, message)), testLogger.getAllLoggingEvents());
    }

    @Test
    void clearOnlyClearsEventsLoggedInThisThread() throws InterruptedException {
        Thread t = new Thread(() -> testLogger.info(message));
        t.start();
        t.join();
        testLogger.clear();
        assertEquals(singletonList(info(message)), testLogger.getAllLoggingEvents());
    }

    @Test
    void clearAllClearsEventsLoggedInAllThreads() throws InterruptedException {
        testLogger.info(message);
        Thread t = new Thread(() -> {
            testLogger.info(message);
            testLogger.clearAll();
        });
        t.start();
        t.join();
        assertEquals(emptyList(), testLogger.getAllLoggingEvents());
        assertEquals(emptyList(), testLogger.getLoggingEvents());
    }

    @Test
    void setEnabledLevelOnlyChangesLevelForCurrentThread() throws Exception {
        final AtomicReference<ImmutableSet<Level>> inThreadEnabledLevels = new AtomicReference<>();
        Thread t = new Thread(() -> {
            testLogger.setEnabledLevels(WARN, ERROR);
            inThreadEnabledLevels.set(testLogger.getEnabledLevels());
        });
        t.start();
        t.join();
        assertEquals(ImmutableSet.of(WARN, ERROR), inThreadEnabledLevels.get());
        assertEquals(enablableValueSet(), testLogger.getEnabledLevels());
    }

    @Test
    void clearOnlyChangesLevelForCurrentThread() throws Exception {
        testLogger.setEnabledLevels(WARN, ERROR);
        Thread t = new Thread(testLogger::clear);
        t.start();
        t.join();
        assertEquals(ImmutableSet.of(WARN, ERROR), testLogger.getEnabledLevels());
    }

    @Test
    void setEnabledLevelsForAllThreads() throws Exception {
        final AtomicReference<ImmutableSet<Level>> inThreadEnabledLevels = new AtomicReference<>();
        Thread t = new Thread(() -> {
            testLogger.setEnabledLevelsForAllThreads(WARN, ERROR);
            inThreadEnabledLevels.set(testLogger.getEnabledLevels());
        });
        t.start();
        t.join();
        assertEquals(ImmutableSet.of(WARN, ERROR), inThreadEnabledLevels.get());
        assertEquals(ImmutableSet.of(WARN, ERROR), testLogger.getEnabledLevels());
    }

    @Test
    void clearAllChangesAllLevels() throws Exception {
        testLogger.setEnabledLevels(WARN, ERROR);
        Thread t = new Thread(testLogger::clearAll);
        t.start();
        t.join();
        assertEquals(enablableValueSet(), testLogger.getEnabledLevels());
    }

    @Test
    void printsWhenPrintLevelEqualToEventLevel() {
        TestLoggerFactory.getInstance().setPrintLevel(INFO);

        testLogger.info(message);

        assertThat(getStdOut(), containsString(message));
    }

    @Test
    void printsWhenPrintLevelLessThanEventLevel() {
        TestLoggerFactory.getInstance().setPrintLevel(DEBUG);

        testLogger.info(message);

        assertThat(getStdOut(), containsString(message));
    }

    @Test
    void doesNotWhenPrintLevelGreaterThanThanEventLevel() {
        TestLoggerFactory.getInstance().setPrintLevel(WARN);

        testLogger.info(message);

        assertThat(getStdOut(), emptyString());
    }

    @Test
    void nullMdcValue() {
        MDC.clear();
        MDC.put("key", null);

        testLogger.info(message);

        assertThat(testLogger.getLoggingEvents(), is(
                singletonList(info(ImmutableMap.of("key", "null"), message))));
    }

    private void assertEnabledReturnsCorrectly(Level levelToTest) {
        testLogger.setEnabledLevels(levelToTest);
        assertTrue(new Logger(testLogger).isEnabled(levelToTest),
                "Logger level set to " + levelToTest + " means " + levelToTest + " should be enabled");

        Set<Level> disabledLevels = difference(enablableValueSet(), newHashSet(levelToTest));
        for (Level disabledLevel: disabledLevels) {
            assertFalse(new Logger(testLogger).isEnabled(disabledLevel),
                    "Logger level set to " + levelToTest + " means " + levelToTest + " should be disabled");
        }
    }

    private void assertEnabledReturnsCorrectly(Level levelToTest, Marker marker) {
        testLogger.setEnabledLevels(levelToTest);
        assertTrue(new Logger(testLogger).isEnabled(levelToTest, marker),
                "Logger level set to " + levelToTest + " means " + levelToTest + " should be enabled");

        Set<Level> disabledLevels = difference(enablableValueSet(), newHashSet(levelToTest));
        for (Level disabledLevel: disabledLevels) {
            assertFalse(new Logger(testLogger).isEnabled(disabledLevel, marker),
                    "Logger level set to " + levelToTest + " means " + levelToTest + " should be disabled");
        }
    }
}
