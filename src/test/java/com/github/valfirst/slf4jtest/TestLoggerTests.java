package com.github.valfirst.slf4jtest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.MDC;
import org.slf4j.Marker;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jext.Logger;
import uk.org.lidalia.test.SystemOutputRule;

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
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static uk.org.lidalia.slf4jext.Level.DEBUG;
import static uk.org.lidalia.slf4jext.Level.ERROR;
import static uk.org.lidalia.slf4jext.Level.INFO;
import static uk.org.lidalia.slf4jext.Level.TRACE;
import static uk.org.lidalia.slf4jext.Level.WARN;
import static uk.org.lidalia.slf4jext.Level.enablableValueSet;

public class TestLoggerTests {

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

    @Rule public SystemOutputRule systemOutputRule = new SystemOutputRule();

    @Before
    public void setUp() {
        mdcValues.put("key1", "value1");
        mdcValues.put("key2", "value2");
        MDC.setContextMap(mdcValues);
    }

    @After
    public void tearDown() {
        MDC.clear();
        TestLoggerFactory.reset();
        TestLoggerFactory.getInstance().setPrintLevel(Level.OFF);
    }

    @Test
    public void name() {
        String name = RandomStringUtils.random(10);
        TestLogger logger = new TestLogger(name, TestLoggerFactory.getInstance());
        assertEquals(name, logger.getName());
    }

    @Test
    public void clearRemovesEvents() {
        testLogger.debug("message1");
        testLogger.debug("message2");
        assertEquals(asList(debug(mdcValues, "message1"), debug(mdcValues, "message2")), testLogger.getLoggingEvents());
        testLogger.clear();
        assertEquals(emptyList(), testLogger.getLoggingEvents());
    }

    @Test
    public void clearResetsLevel() {
        testLogger.setEnabledLevels();
        testLogger.clear();
        assertEquals(newHashSet(TRACE, DEBUG, INFO, WARN, ERROR), testLogger.getEnabledLevels());
    }

    @Test
    public void traceEnabled() {
        assertEnabledReturnsCorrectly(TRACE);
    }

    @Test
    public void traceMessage() {
        testLogger.trace(message);

        assertEquals(singletonList(trace(mdcValues, message)), testLogger.getLoggingEvents());
    }

    @Test
    public void traceMessageOneArg() {
        testLogger.trace(message, arg1);

        assertEquals(singletonList(trace(mdcValues, message, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    public void traceMessageTwoArgs() {
        testLogger.trace(message, arg1, arg2);

        assertEquals(singletonList(trace(mdcValues, message, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    public void traceMessageManyArgs() {
        testLogger.trace(message, args);

        assertEquals(singletonList(trace(mdcValues, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    public void traceMessageManyArgsWithThrowable() {
        testLogger.trace(message, argsWithThrowable);

        assertEquals(singletonList(trace(mdcValues, throwable, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    public void traceMessageThrowable() {
        testLogger.trace(message, throwable);

        assertEquals(singletonList(trace(mdcValues, throwable, message)), testLogger.getLoggingEvents());
    }

    @Test
    public void traceEnabledMarker() {
        assertEnabledReturnsCorrectly(TRACE, marker);
    }

    @Test
    public void traceMarkerMessage() {
        testLogger.trace(marker, message);

        assertEquals(singletonList(trace(mdcValues, marker, message)), testLogger.getLoggingEvents());
    }

    @Test
    public void traceMarkerMessageOneArg() {
        testLogger.trace(marker, message, arg1);

        assertEquals(singletonList(trace(mdcValues, marker, message, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    public void traceMarkerMessageTwoArgs() {
        testLogger.trace(marker, message, arg1, arg2);

        assertEquals(singletonList(trace(mdcValues, marker, message, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    public void traceMarkerMessageManyArgs() {
        testLogger.trace(marker, message, args);

        assertEquals(singletonList(trace(mdcValues, marker, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    public void traceMarkerMessageManyArgsWithThrowable() {
        testLogger.trace(marker, message, argsWithThrowable);

        assertEquals(singletonList(trace(mdcValues, marker, throwable, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    public void traceMarkerMessageThrowable() {
        testLogger.trace(marker, message, throwable);

        assertEquals(singletonList(trace(mdcValues, marker, throwable, message)), testLogger.getLoggingEvents());
    }

    @Test
    public void debugEnabled() {
        assertEnabledReturnsCorrectly(DEBUG);
    }

    @Test
    public void debugMessage() {
        testLogger.debug(message);

        assertEquals(singletonList(debug(mdcValues, message)), testLogger.getLoggingEvents());
    }

    @Test
    public void debugMessageOneArg() {
        testLogger.debug(message, arg1);

        assertEquals(singletonList(debug(mdcValues, message, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    public void debugMessageTwoArgs() {
        testLogger.debug(message, arg1, arg2);

        assertEquals(singletonList(debug(mdcValues, message, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    public void debugMessageManyArgs() {
        testLogger.debug(message, args);

        assertEquals(singletonList(debug(mdcValues, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    public void debugMessageManyArgsWithThrowable() {
        testLogger.debug(message, argsWithThrowable);

        assertEquals(singletonList(debug(mdcValues, throwable, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    public void debugMessageThrowable() {
        testLogger.debug(message, throwable);

        assertEquals(singletonList(debug(mdcValues, throwable, message)), testLogger.getLoggingEvents());
    }

    @Test
    public void debugEnabledMarker() {
        assertEnabledReturnsCorrectly(DEBUG, marker);
    }

    @Test
    public void debugMarkerMessage() {
        testLogger.debug(marker, message);

        assertEquals(singletonList(debug(mdcValues, marker, message)), testLogger.getLoggingEvents());
    }

    @Test
    public void debugMarkerMessageOneArg() {
        testLogger.debug(marker, message, arg1);

        assertEquals(singletonList(debug(mdcValues, marker, message, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    public void debugMarkerMessageTwoArgs() {
        testLogger.debug(marker, message, arg1, arg2);

        assertEquals(singletonList(debug(mdcValues, marker, message, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    public void debugMarkerMessageManyArgs() {
        testLogger.debug(marker, message, args);

        assertEquals(singletonList(debug(mdcValues, marker, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    public void debugMarkerMessageManyArgsWithThrowable() {
        testLogger.debug(marker, message, argsWithThrowable);

        assertEquals(singletonList(debug(mdcValues, marker, throwable, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    public void debugMarkerMessageThrowable() {
        testLogger.debug(marker, message, throwable);

        assertEquals(singletonList(debug(mdcValues, marker, throwable, message)), testLogger.getLoggingEvents());
    }

    @Test
    public void infoEnabled() {
        assertEnabledReturnsCorrectly(INFO);
    }

    @Test
    public void infoMessage() {
        testLogger.info(message);

        assertEquals(singletonList(info(mdcValues, message)), testLogger.getLoggingEvents());
    }

    @Test
    public void infoMessageOneArg() {
        testLogger.info(message, arg1);

        assertEquals(singletonList(info(mdcValues, message, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    public void infoMessageTwoArgs() {
        testLogger.info(message, arg1, arg2);

        assertEquals(singletonList(info(mdcValues, message, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    public void infoMessageManyArgs() {
        testLogger.info(message, args);

        assertEquals(singletonList(info(mdcValues, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    public void infoMessageManyArgsWithThrowable() {
        testLogger.info(message, argsWithThrowable);

        assertEquals(singletonList(info(mdcValues, throwable, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    public void infoMessageThrowable() {
        testLogger.info(message, throwable);

        assertEquals(singletonList(info(mdcValues, throwable, message)), testLogger.getLoggingEvents());
    }

    @Test
    public void infoEnabledMarker() {
        assertEnabledReturnsCorrectly(INFO, marker);
    }

    @Test
    public void infoMarkerMessage() {
        testLogger.info(marker, message);

        assertEquals(singletonList(info(mdcValues, marker, message)), testLogger.getLoggingEvents());
    }

    @Test
    public void infoMarkerMessageOneArg() {
        testLogger.info(marker, message, arg1);

        assertEquals(singletonList(info(mdcValues, marker, message, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    public void infoMarkerMessageTwoArgs() {
        testLogger.info(marker, message, arg1, arg2);

        assertEquals(singletonList(info(mdcValues, marker, message, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    public void infoMarkerMessageManyArgs() {
        testLogger.info(marker, message, args);

        assertEquals(singletonList(info(mdcValues, marker, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    public void infoMarkerMessageManyArgsWithThrowable() {
        testLogger.info(marker, message, argsWithThrowable);

        assertEquals(singletonList(info(mdcValues, marker, throwable, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    public void infoMarkerMessageThrowable() {
        testLogger.info(marker, message, throwable);

        assertEquals(singletonList(info(mdcValues, marker, throwable, message)), testLogger.getLoggingEvents());
    }

    @Test
    public void warnEnabled() {
        assertEnabledReturnsCorrectly(WARN);
    }

    @Test
    public void warnMessage() {
        testLogger.warn(message);

        assertEquals(singletonList(warn(mdcValues, message)), testLogger.getLoggingEvents());
    }

    @Test
    public void warnMessageOneArg() {
        testLogger.warn(message, arg1);

        assertEquals(singletonList(warn(mdcValues, message, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    public void warnMessageTwoArgs() {
        testLogger.warn(message, arg1, arg2);

        assertEquals(singletonList(warn(mdcValues, message, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    public void warnMessageManyArgs() {
        testLogger.warn(message, args);

        assertEquals(singletonList(warn(mdcValues, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    public void warnMessageManyArgsWithThrowable() {
        testLogger.warn(message, argsWithThrowable);

        assertEquals(singletonList(warn(mdcValues, throwable, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    public void warnMessageThrowable() {
        testLogger.warn(message, throwable);

        assertEquals(singletonList(warn(mdcValues, throwable, message)), testLogger.getLoggingEvents());
    }

    @Test
    public void warnEnabledMarker() {
        assertEnabledReturnsCorrectly(WARN, marker);
    }

    @Test
    public void warnMarkerMessage() {
        testLogger.warn(marker, message);

        assertEquals(singletonList(warn(mdcValues, marker, message)), testLogger.getLoggingEvents());
    }

    @Test
    public void warnMarkerMessageOneArg() {
        testLogger.warn(marker, message, arg1);

        assertEquals(singletonList(warn(mdcValues, marker, message, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    public void warnMarkerMessageTwoArgs() {
        testLogger.warn(marker, message, arg1, arg2);

        assertEquals(singletonList(warn(mdcValues, marker, message, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    public void warnMarkerMessageManyArgs() {
        testLogger.warn(marker, message, args);

        assertEquals(singletonList(warn(mdcValues, marker, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    public void warnMarkerMessageManyArgsWithThrowable() {
        testLogger.warn(marker, message, argsWithThrowable);

        assertEquals(singletonList(warn(mdcValues, marker, throwable, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    public void warnMarkerMessageThrowable() {
        testLogger.warn(marker, message, throwable);

        assertEquals(singletonList(warn(mdcValues, marker, throwable, message)), testLogger.getLoggingEvents());
    }

    @Test
    public void errorEnabled() {
        assertEnabledReturnsCorrectly(ERROR);
    }

    @Test
    public void errorMessage() {
        testLogger.error(message);

        assertEquals(singletonList(error(mdcValues, message)), testLogger.getLoggingEvents());
    }

    @Test
    public void errorMessageOneArg() {
        testLogger.error(message, arg1);

        assertEquals(singletonList(error(mdcValues, message, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    public void errorMessageTwoArgs() {
        testLogger.error(message, arg1, arg2);

        assertEquals(singletonList(error(mdcValues, message, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    public void errorMessageManyArgs() {
        testLogger.error(message, args);

        assertEquals(singletonList(error(mdcValues, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    public void errorMessageManyArgsWithThrowable() {
        testLogger.error(message, argsWithThrowable);

        assertEquals(singletonList(error(mdcValues, throwable, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    public void errorMessageThrowable() {
        testLogger.error(message, throwable);

        assertEquals(singletonList(error(mdcValues, throwable, message)), testLogger.getLoggingEvents());
    }

    @Test
    public void errorEnabledMarker() {
        assertEnabledReturnsCorrectly(ERROR, marker);
    }

    @Test
    public void errorMarkerMessage() {
        testLogger.error(marker, message);

        assertEquals(singletonList(error(mdcValues, marker, message)), testLogger.getLoggingEvents());
    }

    @Test
    public void errorMarkerMessageOneArg() {
        testLogger.error(marker, message, arg1);

        assertEquals(singletonList(error(mdcValues, marker, message, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    public void errorMarkerMessageTwoArgs() {
        testLogger.error(marker, message, arg1, arg2);

        assertEquals(singletonList(error(mdcValues, marker, message, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    public void errorMarkerMessageManyArgs() {
        testLogger.error(marker, message, args);

        assertEquals(singletonList(error(mdcValues, marker, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    public void errorMarkerMessageManyArgsWithThrowable() {
        testLogger.error(marker, message, argsWithThrowable);

        assertEquals(singletonList(error(mdcValues, marker, throwable, message, args)), testLogger.getLoggingEvents());
    }

    @Test
    public void errorMarkerMessageThrowable() {
        testLogger.error(marker, message, throwable);

        assertEquals(singletonList(error(mdcValues, marker, throwable, message)), testLogger.getLoggingEvents());
    }

    @Test
    public void loggerSetToOff() {
        logsIfEnabled();
    }

    @Test
    public void loggerSetToError() {
        logsIfEnabled(ERROR);
    }

    @Test
    public void loggerSetToWarn() {
        logsIfEnabled(ERROR, WARN);
    }

    @Test
    public void loggerSetToInfo() {
        logsIfEnabled(ERROR, WARN, INFO);
    }

    @Test
    public void loggerSetToDebug() {
        logsIfEnabled(ERROR, WARN, INFO, DEBUG);
    }

    @Test
    public void loggerSetToTrace() {
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
    public void getLoggingEventsReturnsCopyNotView() {
        testLogger.debug(message);
        List<LoggingEvent> loggingEvents = testLogger.getLoggingEvents();
        testLogger.info(message);

        assertEquals(singletonList(debug(mdcValues, message)), loggingEvents);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getLoggingEventsReturnsUnmodifiableList() {
        List<LoggingEvent> loggingEvents = testLogger.getLoggingEvents();
        loggingEvents.add(debug("hello"));
    }

    @Test
    public void getLoggingEventsOnlyReturnsEventsLoggedInThisThread() throws InterruptedException {
        Thread t = new Thread(() -> testLogger.info(message));
        t.start();
        t.join();
        assertEquals(emptyList(), testLogger.getLoggingEvents());
    }

    @Test
    public void getAllLoggingEventsReturnsEventsLoggedInAllThreads() throws InterruptedException {
        Thread t = new Thread(() -> testLogger.info(message));
        t.start();
        t.join();
        testLogger.info(message);
        assertEquals(asList(info(message), info(mdcValues, message)), testLogger.getAllLoggingEvents());
    }

    @Test
    public void clearOnlyClearsEventsLoggedInThisThread() throws InterruptedException {
        Thread t = new Thread(() -> testLogger.info(message));
        t.start();
        t.join();
        testLogger.clear();
        assertEquals(singletonList(info(message)), testLogger.getAllLoggingEvents());
    }

    @Test
    public void clearAllClearsEventsLoggedInAllThreads() throws InterruptedException {
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
    public void setEnabledLevelOnlyChangesLevelForCurrentThread() throws Exception {
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
    public void clearOnlyChangesLevelForCurrentThread() throws Exception {
        testLogger.setEnabledLevels(WARN, ERROR);
        Thread t = new Thread(testLogger::clear);
        t.start();
        t.join();
        assertEquals(ImmutableSet.of(WARN, ERROR), testLogger.getEnabledLevels());
    }

    @Test
    public void setEnabledLevelsForAllThreads() throws Exception {
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
    public void clearAllChangesAllLevels() throws Exception {
        testLogger.setEnabledLevels(WARN, ERROR);
        Thread t = new Thread(testLogger::clearAll);
        t.start();
        t.join();
        assertEquals(enablableValueSet(), testLogger.getEnabledLevels());
    }

    @Test
    public void printsWhenPrintLevelEqualToEventLevel() {
        TestLoggerFactory.getInstance().setPrintLevel(INFO);

        testLogger.info(message);

        assertThat(systemOutputRule.getSystemOut(), containsString(message));
    }

    @Test
    public void printsWhenPrintLevelLessThanEventLevel() {
        TestLoggerFactory.getInstance().setPrintLevel(DEBUG);

        testLogger.info(message);

        assertThat(systemOutputRule.getSystemOut(), containsString(message));
    }

    @Test
    public void doesNotWhenPrintLevelGreaterThanThanEventLevel() {
        TestLoggerFactory.getInstance().setPrintLevel(WARN);

        testLogger.info(message);

        assertThat(systemOutputRule.getSystemOut(), isEmptyString());
    }

    @Test
    public void nullMdcValue() {
        MDC.clear();
        MDC.put("key", null);

        testLogger.info(message);

        assertThat(testLogger.getLoggingEvents(), is(
                singletonList(info(ImmutableMap.of("key", "null"), message))));
    }

    private void assertEnabledReturnsCorrectly(Level levelToTest) {
        testLogger.setEnabledLevels(levelToTest);
        assertTrue("Logger level set to " + levelToTest + " means " + levelToTest + " should be enabled",
                new Logger(testLogger).isEnabled(levelToTest));

        Set<Level> disabledLevels = difference(enablableValueSet(), newHashSet(levelToTest));
        for (Level disabledLevel: disabledLevels) {
            assertFalse("Logger level set to " + levelToTest + " means " + levelToTest + " should be disabled",
                    new Logger(testLogger).isEnabled(disabledLevel));
        }
    }

    private void assertEnabledReturnsCorrectly(Level levelToTest, Marker marker) {
        testLogger.setEnabledLevels(levelToTest);
        assertTrue("Logger level set to " + levelToTest + " means " + levelToTest + " should be enabled",
                new Logger(testLogger).isEnabled(levelToTest, marker));

        Set<Level> disabledLevels = difference(enablableValueSet(), newHashSet(levelToTest));
        for (Level disabledLevel: disabledLevels) {
            assertFalse("Logger level set to " + levelToTest + " means " + levelToTest + " should be disabled",
                    new Logger(testLogger).isEnabled(disabledLevel, marker));
        }
    }
}
