package com.github.valfirst.slf4jtest;

import static com.github.valfirst.slf4jtest.LoggingEvent.debug;
import static com.github.valfirst.slf4jtest.LoggingEvent.error;
import static com.github.valfirst.slf4jtest.LoggingEvent.info;
import static com.github.valfirst.slf4jtest.LoggingEvent.trace;
import static com.github.valfirst.slf4jtest.LoggingEvent.warn;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.slf4j.event.Level.DEBUG;
import static org.slf4j.event.Level.ERROR;
import static org.slf4j.event.Level.INFO;
import static org.slf4j.event.Level.TRACE;
import static org.slf4j.event.Level.WARN;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.event.Level;

class TestLoggerTests {

    private static final String LOGGER_NAME = "com.github.valfirst";
    private static final String MESSAGE = "message {} {} {}";
    private final TestLogger testLogger =
            new TestLogger(LOGGER_NAME, TestLoggerFactory.getInstance());
    private final Marker marker = mock(Marker.class);
    private final Object arg1 = "arg1";
    private final Object arg2 = "arg2";
    private final Object[] args = new Object[] {arg1, arg2, "arg3"};
    private final Throwable throwable = new Throwable();
    private final Object[] argsWithThrowable = new Object[] {arg1, arg2, "arg3", throwable};

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
        TestLoggerFactory.getInstance().setPrintLevel(null);
        TestLoggerFactory.getInstance().setCaptureLevel(Level.TRACE);
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
        assertEquals(
                asList(debug(mdcValues, "message1"), debug(mdcValues, "message2")),
                testLogger.getLoggingEvents());
        testLogger.clear();
        assertEquals(emptyList(), testLogger.getLoggingEvents());
    }

    @Test
    void clearResetsLevel() {
        testLogger.setEnabledLevels();
        testLogger.clear();
        assertEquals(EnumSet.of(TRACE, DEBUG, INFO, WARN, ERROR), testLogger.getEnabledLevels());
    }

    @Test
    void traceEnabled() {
        assertEnabledReturnsCorrectly(TRACE);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {MESSAGE})
    void traceMessage(String message) {
        testLogger.trace(message);

        assertEquals(singletonList(trace(mdcValues, message)), testLogger.getLoggingEvents());
    }

    @Test
    void traceMessageOneArg() {
        testLogger.trace(MESSAGE, arg1);

        assertEquals(singletonList(trace(mdcValues, MESSAGE, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    void traceMessageTwoArgs() {
        testLogger.trace(MESSAGE, arg1, arg2);

        assertEquals(
                singletonList(trace(mdcValues, MESSAGE, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    void traceMessageManyArgs() {
        testLogger.trace(MESSAGE, args);

        assertEquals(singletonList(trace(mdcValues, MESSAGE, args)), testLogger.getLoggingEvents());
    }

    @Test
    void traceMessageManyArgsWithThrowable() {
        testLogger.trace(MESSAGE, argsWithThrowable);

        assertEquals(
                singletonList(trace(mdcValues, throwable, MESSAGE, args)), testLogger.getLoggingEvents());
    }

    @Test
    void traceMessageThrowable() {
        testLogger.trace(MESSAGE, throwable);

        assertEquals(
                singletonList(trace(mdcValues, throwable, MESSAGE)), testLogger.getLoggingEvents());
    }

    @Test
    void traceEnabledMarker() {
        assertEnabledReturnsCorrectly(TRACE, marker);
    }

    @Test
    void traceMarkerMessage() {
        testLogger.trace(marker, MESSAGE);

        assertEquals(singletonList(trace(mdcValues, marker, MESSAGE)), testLogger.getLoggingEvents());
    }

    @Test
    void traceMarkerMessageOneArg() {
        testLogger.trace(marker, MESSAGE, arg1);

        assertEquals(
                singletonList(trace(mdcValues, marker, MESSAGE, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    void traceMarkerMessageTwoArgs() {
        testLogger.trace(marker, MESSAGE, arg1, arg2);

        assertEquals(
                singletonList(trace(mdcValues, marker, MESSAGE, arg1, arg2)),
                testLogger.getLoggingEvents());
    }

    @Test
    void traceMarkerMessageManyArgs() {
        testLogger.trace(marker, MESSAGE, args);

        assertEquals(
                singletonList(trace(mdcValues, marker, MESSAGE, args)), testLogger.getLoggingEvents());
    }

    @Test
    void traceMarkerMessageManyArgsWithThrowable() {
        testLogger.trace(marker, MESSAGE, argsWithThrowable);

        assertEquals(
                singletonList(trace(mdcValues, marker, throwable, MESSAGE, args)),
                testLogger.getLoggingEvents());
    }

    @Test
    void traceMarkerMessageThrowable() {
        testLogger.trace(marker, MESSAGE, throwable);

        assertEquals(
                singletonList(trace(mdcValues, marker, throwable, MESSAGE)), testLogger.getLoggingEvents());
    }

    @Test
    void debugEnabled() {
        assertEnabledReturnsCorrectly(DEBUG);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {MESSAGE})
    void debugMessage(String message) {
        testLogger.debug(message);

        assertEquals(singletonList(debug(mdcValues, message)), testLogger.getLoggingEvents());
    }

    @Test
    void debugMessageOneArg() {
        testLogger.debug(MESSAGE, arg1);

        assertEquals(singletonList(debug(mdcValues, MESSAGE, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    void debugMessageTwoArgs() {
        testLogger.debug(MESSAGE, arg1, arg2);

        assertEquals(
                singletonList(debug(mdcValues, MESSAGE, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    void debugMessageManyArgs() {
        testLogger.debug(MESSAGE, args);

        assertEquals(singletonList(debug(mdcValues, MESSAGE, args)), testLogger.getLoggingEvents());
    }

    @Test
    void debugMessageManyArgsWithThrowable() {
        testLogger.debug(MESSAGE, argsWithThrowable);

        assertEquals(
                singletonList(debug(mdcValues, throwable, MESSAGE, args)), testLogger.getLoggingEvents());
    }

    @Test
    void debugMessageThrowable() {
        testLogger.debug(MESSAGE, throwable);

        assertEquals(
                singletonList(debug(mdcValues, throwable, MESSAGE)), testLogger.getLoggingEvents());
    }

    @Test
    void debugEnabledMarker() {
        assertEnabledReturnsCorrectly(DEBUG, marker);
    }

    @Test
    void debugMarkerMessage() {
        testLogger.debug(marker, MESSAGE);

        assertEquals(singletonList(debug(mdcValues, marker, MESSAGE)), testLogger.getLoggingEvents());
    }

    @Test
    void debugMarkerMessageOneArg() {
        testLogger.debug(marker, MESSAGE, arg1);

        assertEquals(
                singletonList(debug(mdcValues, marker, MESSAGE, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    void debugMarkerMessageTwoArgs() {
        testLogger.debug(marker, MESSAGE, arg1, arg2);

        assertEquals(
                singletonList(debug(mdcValues, marker, MESSAGE, arg1, arg2)),
                testLogger.getLoggingEvents());
    }

    @Test
    void debugMarkerMessageManyArgs() {
        testLogger.debug(marker, MESSAGE, args);

        assertEquals(
                singletonList(debug(mdcValues, marker, MESSAGE, args)), testLogger.getLoggingEvents());
    }

    @Test
    void debugMarkerMessageManyArgsWithThrowable() {
        testLogger.debug(marker, MESSAGE, argsWithThrowable);

        assertEquals(
                singletonList(debug(mdcValues, marker, throwable, MESSAGE, args)),
                testLogger.getLoggingEvents());
    }

    @Test
    void debugMarkerMessageThrowable() {
        testLogger.debug(marker, MESSAGE, throwable);

        assertEquals(
                singletonList(debug(mdcValues, marker, throwable, MESSAGE)), testLogger.getLoggingEvents());
    }

    @Test
    void infoEnabled() {
        assertEnabledReturnsCorrectly(INFO);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {MESSAGE})
    void infoMessage(String message) {
        testLogger.info(message);

        assertEquals(singletonList(info(mdcValues, message)), testLogger.getLoggingEvents());
    }

    @Test
    void infoMessageOneArg() {
        testLogger.info(MESSAGE, arg1);

        assertEquals(singletonList(info(mdcValues, MESSAGE, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    void infoMessageTwoArgs() {
        testLogger.info(MESSAGE, arg1, arg2);

        assertEquals(
                singletonList(info(mdcValues, MESSAGE, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    void infoMessageManyArgs() {
        testLogger.info(MESSAGE, args);

        assertEquals(singletonList(info(mdcValues, MESSAGE, args)), testLogger.getLoggingEvents());
    }

    @Test
    void infoMessageManyArgsWithThrowable() {
        testLogger.info(MESSAGE, argsWithThrowable);

        assertEquals(
                singletonList(info(mdcValues, throwable, MESSAGE, args)), testLogger.getLoggingEvents());
    }

    @Test
    void infoMessageThrowable() {
        testLogger.info(MESSAGE, throwable);

        assertEquals(singletonList(info(mdcValues, throwable, MESSAGE)), testLogger.getLoggingEvents());
    }

    @Test
    void infoEnabledMarker() {
        assertEnabledReturnsCorrectly(INFO, marker);
    }

    @Test
    void infoMarkerMessage() {
        testLogger.info(marker, MESSAGE);

        assertEquals(singletonList(info(mdcValues, marker, MESSAGE)), testLogger.getLoggingEvents());
    }

    @Test
    void infoMarkerMessageOneArg() {
        testLogger.info(marker, MESSAGE, arg1);

        assertEquals(
                singletonList(info(mdcValues, marker, MESSAGE, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    void infoMarkerMessageTwoArgs() {
        testLogger.info(marker, MESSAGE, arg1, arg2);

        assertEquals(
                singletonList(info(mdcValues, marker, MESSAGE, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    void infoMarkerMessageManyArgs() {
        testLogger.info(marker, MESSAGE, args);

        assertEquals(
                singletonList(info(mdcValues, marker, MESSAGE, args)), testLogger.getLoggingEvents());
    }

    @Test
    void infoMarkerMessageManyArgsWithThrowable() {
        testLogger.info(marker, MESSAGE, argsWithThrowable);

        assertEquals(
                singletonList(info(mdcValues, marker, throwable, MESSAGE, args)),
                testLogger.getLoggingEvents());
    }

    @Test
    void infoMarkerMessageThrowable() {
        testLogger.info(marker, MESSAGE, throwable);

        assertEquals(
                singletonList(info(mdcValues, marker, throwable, MESSAGE)), testLogger.getLoggingEvents());
    }

    @Test
    void warnEnabled() {
        assertEnabledReturnsCorrectly(WARN);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {MESSAGE})
    void warnMessage(String message) {
        testLogger.warn(message);

        assertEquals(singletonList(warn(mdcValues, message)), testLogger.getLoggingEvents());
    }

    @Test
    void warnMessageOneArg() {
        testLogger.warn(MESSAGE, arg1);

        assertEquals(singletonList(warn(mdcValues, MESSAGE, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    void warnMessageTwoArgs() {
        testLogger.warn(MESSAGE, arg1, arg2);

        assertEquals(
                singletonList(warn(mdcValues, MESSAGE, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    void warnMessageManyArgs() {
        testLogger.warn(MESSAGE, args);

        assertEquals(singletonList(warn(mdcValues, MESSAGE, args)), testLogger.getLoggingEvents());
    }

    @Test
    void warnMessageManyArgsWithThrowable() {
        testLogger.warn(MESSAGE, argsWithThrowable);

        assertEquals(
                singletonList(warn(mdcValues, throwable, MESSAGE, args)), testLogger.getLoggingEvents());
    }

    @Test
    void warnMessageThrowable() {
        testLogger.warn(MESSAGE, throwable);

        assertEquals(singletonList(warn(mdcValues, throwable, MESSAGE)), testLogger.getLoggingEvents());
    }

    @Test
    void warnEnabledMarker() {
        assertEnabledReturnsCorrectly(WARN, marker);
    }

    @Test
    void warnMarkerMessage() {
        testLogger.warn(marker, MESSAGE);

        assertEquals(singletonList(warn(mdcValues, marker, MESSAGE)), testLogger.getLoggingEvents());
    }

    @Test
    void warnMarkerMessageOneArg() {
        testLogger.warn(marker, MESSAGE, arg1);

        assertEquals(
                singletonList(warn(mdcValues, marker, MESSAGE, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    void warnMarkerMessageTwoArgs() {
        testLogger.warn(marker, MESSAGE, arg1, arg2);

        assertEquals(
                singletonList(warn(mdcValues, marker, MESSAGE, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    void warnMarkerMessageManyArgs() {
        testLogger.warn(marker, MESSAGE, args);

        assertEquals(
                singletonList(warn(mdcValues, marker, MESSAGE, args)), testLogger.getLoggingEvents());
    }

    @Test
    void warnMarkerMessageManyArgsWithThrowable() {
        testLogger.warn(marker, MESSAGE, argsWithThrowable);

        assertEquals(
                singletonList(warn(mdcValues, marker, throwable, MESSAGE, args)),
                testLogger.getLoggingEvents());
    }

    @Test
    void warnMarkerMessageThrowable() {
        testLogger.warn(marker, MESSAGE, throwable);

        assertEquals(
                singletonList(warn(mdcValues, marker, throwable, MESSAGE)), testLogger.getLoggingEvents());
    }

    @Test
    void errorEnabled() {
        assertEnabledReturnsCorrectly(ERROR);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {MESSAGE})
    void errorMessage(String message) {
        testLogger.error(message);

        assertEquals(singletonList(error(mdcValues, message)), testLogger.getLoggingEvents());
    }

    @Test
    void errorMessageOneArg() {
        testLogger.error(MESSAGE, arg1);

        assertEquals(singletonList(error(mdcValues, MESSAGE, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    void errorMessageTwoArgs() {
        testLogger.error(MESSAGE, arg1, arg2);

        assertEquals(
                singletonList(error(mdcValues, MESSAGE, arg1, arg2)), testLogger.getLoggingEvents());
    }

    @Test
    void errorMessageManyArgs() {
        testLogger.error(MESSAGE, args);

        assertEquals(singletonList(error(mdcValues, MESSAGE, args)), testLogger.getLoggingEvents());
    }

    @Test
    void errorMessageManyArgsWithThrowable() {
        testLogger.error(MESSAGE, argsWithThrowable);

        assertEquals(
                singletonList(error(mdcValues, throwable, MESSAGE, args)), testLogger.getLoggingEvents());
    }

    @Test
    void errorMessageThrowable() {
        testLogger.error(MESSAGE, throwable);

        assertEquals(
                singletonList(error(mdcValues, throwable, MESSAGE)), testLogger.getLoggingEvents());
    }

    @Test
    void errorEnabledMarker() {
        assertEnabledReturnsCorrectly(ERROR, marker);
    }

    @Test
    void errorMarkerMessage() {
        testLogger.error(marker, MESSAGE);

        assertEquals(singletonList(error(mdcValues, marker, MESSAGE)), testLogger.getLoggingEvents());
    }

    @Test
    void errorMarkerMessageOneArg() {
        testLogger.error(marker, MESSAGE, arg1);

        assertEquals(
                singletonList(error(mdcValues, marker, MESSAGE, arg1)), testLogger.getLoggingEvents());
    }

    @Test
    void errorMarkerMessageTwoArgs() {
        testLogger.error(marker, MESSAGE, arg1, arg2);

        assertEquals(
                singletonList(error(mdcValues, marker, MESSAGE, arg1, arg2)),
                testLogger.getLoggingEvents());
    }

    @Test
    void errorMarkerMessageManyArgs() {
        testLogger.error(marker, MESSAGE, args);

        assertEquals(
                singletonList(error(mdcValues, marker, MESSAGE, args)), testLogger.getLoggingEvents());
    }

    @Test
    void errorMarkerMessageManyArgsWithThrowable() {
        testLogger.error(marker, MESSAGE, argsWithThrowable);

        assertEquals(
                singletonList(error(mdcValues, marker, throwable, MESSAGE, args)),
                testLogger.getLoggingEvents());
    }

    @Test
    void errorMarkerMessageThrowable() {
        testLogger.error(marker, MESSAGE, throwable);

        assertEquals(
                singletonList(error(mdcValues, marker, throwable, MESSAGE)), testLogger.getLoggingEvents());
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
        testLogger.error(MESSAGE);
        testLogger.warn(MESSAGE);
        testLogger.info(MESSAGE);
        testLogger.debug(MESSAGE);
        testLogger.trace(MESSAGE);

        List<LoggingEvent> expectedEvents =
                Arrays.stream(shouldLog)
                        .map(level -> new LoggingEvent(level, mdcValues, MESSAGE))
                        .collect(Collectors.toList());

        assertEquals(expectedEvents, testLogger.getLoggingEvents());
        testLogger.clear();
    }

    @Test
    void getLoggingEventsReturnsCopyNotView() {
        testLogger.debug(MESSAGE);
        List<LoggingEvent> loggingEvents = testLogger.getLoggingEvents();
        testLogger.info(MESSAGE);

        assertEquals(singletonList(debug(mdcValues, MESSAGE)), loggingEvents);
    }

    @Test
    void getLoggingEventsReturnsUnmodifiableList() {
        List<LoggingEvent> loggingEvents = testLogger.getLoggingEvents();
        LoggingEvent debugEvent = debug("hello");
        assertThrows(UnsupportedOperationException.class, () -> loggingEvents.add(debugEvent));
    }

    @Test
    void getLoggingEventsOnlyReturnsEventsLoggedInThisThread() throws InterruptedException {
        Thread t = new Thread(() -> testLogger.info(MESSAGE));
        t.start();
        t.join();
        assertEquals(emptyList(), testLogger.getLoggingEvents());
    }

    @Test
    void getAllLoggingEventsReturnsEventsLoggedInAllThreads() throws InterruptedException {
        Thread t = new Thread(() -> testLogger.info(MESSAGE));
        t.start();
        t.join();
        testLogger.info(MESSAGE);
        assertEquals(asList(info(MESSAGE), info(mdcValues, MESSAGE)), testLogger.getAllLoggingEvents());
    }

    @Test
    void clearOnlyClearsEventsLoggedInThisThread() throws InterruptedException {
        Thread t = new Thread(() -> testLogger.info(MESSAGE));
        t.start();
        t.join();
        testLogger.clear();
        assertEquals(singletonList(info(MESSAGE)), testLogger.getAllLoggingEvents());
    }

    @Test
    void clearAllClearsEventsLoggedInAllThreads() throws InterruptedException {
        testLogger.info(MESSAGE);
        Thread t =
                new Thread(
                        () -> {
                            testLogger.info(MESSAGE);
                            testLogger.clearAll();
                        });
        t.start();
        t.join();
        assertEquals(emptyList(), testLogger.getAllLoggingEvents());
        assertEquals(emptyList(), testLogger.getLoggingEvents());
    }

    @Test
    void setEnabledLevelOnlyChangesLevelForCurrentThread() throws Exception {
        final AtomicReference<Set<Level>> inThreadEnabledLevels = new AtomicReference<>();
        Thread t =
                new Thread(
                        () -> {
                            testLogger.setEnabledLevels(WARN, ERROR);
                            inThreadEnabledLevels.set(testLogger.getEnabledLevels());
                        });
        t.start();
        t.join();
        assertEquals(EnumSet.of(WARN, ERROR), inThreadEnabledLevels.get());
        assertEquals(EnumSet.allOf(Level.class), testLogger.getEnabledLevels());
    }

    @Test
    void clearOnlyChangesLevelForCurrentThread() throws Exception {
        testLogger.setEnabledLevels(WARN, ERROR);
        Thread t = new Thread(testLogger::clear);
        t.start();
        t.join();
        assertEquals(EnumSet.of(WARN, ERROR), testLogger.getEnabledLevels());
    }

    @Test
    void setEnabledLevelsForAllThreads() throws Exception {
        final AtomicReference<Set<Level>> inThreadEnabledLevels = new AtomicReference<>();
        Thread t =
                new Thread(
                        () -> {
                            testLogger.setEnabledLevelsForAllThreads(WARN, ERROR);
                            inThreadEnabledLevels.set(testLogger.getEnabledLevels());
                        });
        t.start();
        t.join();
        assertEquals(EnumSet.of(WARN, ERROR), inThreadEnabledLevels.get());
        assertEquals(EnumSet.of(WARN, ERROR), testLogger.getEnabledLevels());
    }

    @Test
    void clearAllChangesAllLevels() throws Exception {
        testLogger.setEnabledLevels(WARN, ERROR);
        Thread t = new Thread(testLogger::clearAll);
        t.start();
        t.join();
        assertEquals(EnumSet.allOf(Level.class), testLogger.getEnabledLevels());
    }

    @ParameterizedTest
    @EnumSource(names = {"INFO", "DEBUG", "TRACE"})
    @StdIo
    void printsWhenPrintLevelIsEqualToOrLessThanEventLevel(Level printLevel, StdOut stdOut) {
        TestLoggerFactory.getInstance().setPrintLevel(printLevel);

        testLogger.info(MESSAGE);

        String[] stdOutLines = stdOut.capturedLines();
        assertThat(stdOutLines.length, is(equalTo(1)));
        assertThat(stdOutLines[0], is(endsWith(MESSAGE)));
    }

    @ParameterizedTest
    @EnumSource(names = {"WARN", "ERROR"})
    @StdIo
    void doesNotWhenPrintLevelGreaterThanEventLevel(Level printLevel, StdOut stdOut) {
        TestLoggerFactory.getInstance().setPrintLevel(printLevel);

        testLogger.info(MESSAGE);

        assertThat(stdOut.capturedLines(), is(arrayContaining("")));
    }

    @Test
    void doesNotCaptureWhenCaptureLevelGreaterThanEventLevel() {
        TestLoggerFactory.getInstance().setCaptureLevel(WARN);

        testLogger.setEnabledLevels(ERROR, WARN, INFO, DEBUG, TRACE);

        assertLogsAreCaptured(ERROR, WARN);
    }

    @Test
    void doesNotCaptureWhenCaptureLevelIsNull() {
        TestLoggerFactory.getInstance().setCaptureLevel(null);

        testLogger.setEnabledLevels(ERROR, WARN, INFO, DEBUG, TRACE);

        assertLogsAreCaptured();
    }

    @Test
    void captureLevelAndLoggerLevelAreChecked() {
        TestLoggerFactory.getInstance().setCaptureLevel(INFO);

        testLogger.setEnabledLevels(ERROR, WARN);

        assertLogsAreCaptured(ERROR, WARN);
    }

    @Test
    void captureAllLevelsByDefault() {
        assertLogsAreCaptured(ERROR, WARN, INFO, DEBUG, TRACE);
    }

    private void assertLogsAreCaptured(Level... shouldLog) {
        testLogger.error(MESSAGE);
        testLogger.warn(MESSAGE);
        testLogger.info(MESSAGE);
        testLogger.debug(MESSAGE);
        testLogger.trace(MESSAGE);

        List<LoggingEvent> expectedEvents =
                Arrays.stream(shouldLog)
                        .map(level -> new LoggingEvent(level, mdcValues, MESSAGE))
                        .collect(Collectors.toList());

        assertEquals(expectedEvents, testLogger.getLoggingEvents());
        testLogger.clear();
    }

    @Test
    void nullMdcValue() {
        MDC.clear();
        MDC.put("key", null);

        testLogger.info(MESSAGE);

        Map<String, String> expected = new HashMap<>();
        expected.put("key", null);

        assertEquals(singletonList(info(expected, MESSAGE)), testLogger.getLoggingEvents());
    }

    private static final Map<Level, Predicate<Logger>> levelEnabledMap;

    static {
        levelEnabledMap = new EnumMap<>(Level.class);
        levelEnabledMap.put(Level.TRACE, Logger::isTraceEnabled);
        levelEnabledMap.put(Level.DEBUG, Logger::isDebugEnabled);
        levelEnabledMap.put(Level.INFO, Logger::isInfoEnabled);
        levelEnabledMap.put(Level.WARN, Logger::isWarnEnabled);
        levelEnabledMap.put(Level.ERROR, Logger::isErrorEnabled);
    }

    private void assertEnabledReturnsCorrectly(Level levelToTest) {
        testLogger.setEnabledLevels(levelToTest);
        assertTrue(
                levelEnabledMap.get(levelToTest).test(testLogger),
                "Logger level set to " + levelToTest + " means " + levelToTest + " should be enabled");

        Set<Level> disabledLevels = EnumSet.complementOf(EnumSet.of(levelToTest));
        for (Level disabledLevel : disabledLevels) {
            assertFalse(
                    levelEnabledMap.get(disabledLevel).test(testLogger),
                    "Logger level set to " + levelToTest + " means " + levelToTest + " should be disabled");
        }
    }

    private static final Map<Level, BiPredicate<Logger, Marker>> levelMarkerEnabledMap;

    static {
        levelMarkerEnabledMap = new EnumMap<>(Level.class);
        levelMarkerEnabledMap.put(Level.TRACE, Logger::isTraceEnabled);
        levelMarkerEnabledMap.put(Level.DEBUG, Logger::isDebugEnabled);
        levelMarkerEnabledMap.put(Level.INFO, Logger::isInfoEnabled);
        levelMarkerEnabledMap.put(Level.WARN, Logger::isWarnEnabled);
        levelMarkerEnabledMap.put(Level.ERROR, Logger::isErrorEnabled);
    }

    private void assertEnabledReturnsCorrectly(Level levelToTest, Marker marker) {
        testLogger.setEnabledLevels(levelToTest);
        assertTrue(
                levelMarkerEnabledMap.get(levelToTest).test(testLogger, marker),
                "Logger level set to " + levelToTest + " means " + levelToTest + " should be enabled");

        Set<Level> disabledLevels = EnumSet.complementOf(EnumSet.of(levelToTest));
        for (Level disabledLevel : disabledLevels) {
            assertFalse(
                    levelMarkerEnabledMap.get(disabledLevel).test(testLogger, marker),
                    "Logger level set to " + levelToTest + " means " + levelToTest + " should be disabled");
        }
    }
}
