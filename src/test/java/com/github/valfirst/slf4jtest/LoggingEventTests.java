package com.github.valfirst.slf4jtest;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.joda.time.DateTimeUtils;
import org.joda.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Marker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import uk.org.lidalia.slf4jext.Level;

import static com.github.valfirst.slf4jtest.LoggingEvent.error;
import static com.github.valfirst.slf4jtest.LoggingEvent.debug;
import static com.github.valfirst.slf4jtest.LoggingEvent.info;
import static com.github.valfirst.slf4jtest.LoggingEvent.trace;
import static com.github.valfirst.slf4jtest.LoggingEvent.warn;
import static java.lang.System.lineSeparator;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static uk.org.lidalia.slf4jext.Level.DEBUG;
import static uk.org.lidalia.slf4jext.Level.ERROR;
import static uk.org.lidalia.slf4jext.Level.INFO;
import static uk.org.lidalia.slf4jext.Level.TRACE;
import static uk.org.lidalia.slf4jext.Level.WARN;

class LoggingEventTests extends StdIoTests {

    private static final ImmutableMap<String, String> emptyMap = ImmutableMap.of();

    private final Level level = TRACE;
    private final Map<String, String> mdc = Collections.singletonMap("key", "value");
    private final Marker marker = mock(Marker.class);
    private final Throwable throwable = new Throwable();
    private final String message = "message";
    private final Object arg1 = "arg1";
    private final Object arg2 = "arg2";
    private final List<Object> args = asList(arg1, arg2);

    @AfterEach
    void afterEach()
    {
        super.after();
        DateTimeUtils.setCurrentMillisSystem();
        TestLoggerFactory.reset();
    }

    @Test
    void constructorMessageArgs() {
        LoggingEvent event = new LoggingEvent(level, message, arg1, arg2);
        assertThat(event.getLevel(), is(level));
        assertThat(event.getMdc(), is(emptyMap));
        assertThat(event.getMarker(), is(empty()));
        assertThat(event.getThrowable(), is(empty()));
        assertThat(event.getMessage(), is(message));
        assertThat(event.getArguments(), is(args));
    }

    @Test
    void constructorThrowableMessageArgs() {
        LoggingEvent event = new LoggingEvent(level, throwable, message, arg1, arg2);
        assertThat(event.getLevel(), is(level));
        assertThat(event.getMdc(), is(emptyMap));
        assertThat(event.getMarker(), is(empty()));
        assertThat(event.getThrowable(), is(of(throwable)));
        assertThat(event.getMessage(), is(message));
        assertThat(event.getArguments(), is(args));
    }

    @Test
    void constructorMarkerMessageArgs() {
        LoggingEvent event = new LoggingEvent(level, marker, message, arg1, arg2);
        assertThat(event.getLevel(), is(level));
        assertThat(event.getMdc(), is(emptyMap));
        assertThat(event.getMarker(), is(of(marker)));
        assertThat(event.getThrowable(), is(empty()));
        assertThat(event.getMessage(), is(message));
        assertThat(event.getArguments(), is(args));
    }

    @Test
    void constructorMarkerThrowableMessageArgs() {
        LoggingEvent event = new LoggingEvent(level, marker, throwable, message, arg1, arg2);
        assertThat(event.getLevel(), is(level));
        assertThat(event.getMdc(), is(emptyMap));
        assertThat(event.getMarker(), is(of(marker)));
        assertThat(event.getThrowable(), is(of(throwable)));
        assertThat(event.getMessage(), is(message));
        assertThat(event.getArguments(), is(args));
    }

    @Test
    void constructorMdcMessageArgs() {
        LoggingEvent event = new LoggingEvent(level, mdc, message, arg1, arg2);
        assertThat(event.getLevel(), is(level));
        assertThat(event.getMdc(), is(mdc));
        assertThat(event.getMarker(), is(empty()));
        assertThat(event.getThrowable(), is(empty()));
        assertThat(event.getMessage(), is(message));
        assertThat(event.getArguments(), is(args));
    }

    @Test
    void constructorMdcThrowableMessageArgs() {
        LoggingEvent event = new LoggingEvent(level, mdc, throwable, message, arg1, arg2);
        assertThat(event.getLevel(), is(level));
        assertThat(event.getMdc(), is(mdc));
        assertThat(event.getMarker(), is(empty()));
        assertThat(event.getThrowable(), is(of(throwable)));
        assertThat(event.getMessage(), is(message));
        assertThat(event.getArguments(), is(args));
    }

    @Test
    void constructorMdcMarkerMessageArgs() {
        LoggingEvent event = new LoggingEvent(level, mdc, marker, message, arg1, arg2);
        assertThat(event.getLevel(), is(level));
        assertThat(event.getMdc(), is(mdc));
        assertThat(event.getMarker(), is(of(marker)));
        assertThat(event.getThrowable(), is(empty()));
        assertThat(event.getMessage(), is(message));
        assertThat(event.getArguments(), is(args));
    }

    @Test
    void constructorMdcMarkerThrowableMessageArgs() {
        LoggingEvent event = new LoggingEvent(level, mdc, marker, throwable, message, arg1, arg2);
        assertThat(event.getLevel(), is(level));
        assertThat(event.getMdc(), is(mdc));
        assertThat(event.getMarker(), is(of(marker)));
        assertThat(event.getThrowable(), is(of(throwable)));
        assertThat(event.getMessage(), is(message));
        assertThat(event.getArguments(), is(args));
    }

    static Stream<Arguments> messageArgs() {
        return Stream.of(
            arguments((BiFunction<String, Object[], LoggingEvent>) LoggingEvent::trace, TRACE),
            arguments((BiFunction<String, Object[], LoggingEvent>) LoggingEvent::debug, DEBUG),
            arguments((BiFunction<String, Object[], LoggingEvent>) LoggingEvent::info,  INFO),
            arguments((BiFunction<String, Object[], LoggingEvent>) LoggingEvent::warn,  WARN),
            arguments((BiFunction<String, Object[], LoggingEvent>) LoggingEvent::error, ERROR)
        );
    }

    @ParameterizedTest
    @MethodSource("messageArgs")
    void messageArgs(BiFunction<String, Object[], LoggingEvent> producer, Level level) {
        LoggingEvent event = producer.apply(message, new Object[] {arg1, arg2});
        LoggingEvent expected = new LoggingEvent(level, message, arg1, arg2);
        assertThat(event, is(expected));
    }

    static Stream<Arguments> throwableMessageArgs() {
        return Stream.of(
            arguments((TriFunction<Throwable, String, Object[], LoggingEvent>) LoggingEvent::trace, TRACE),
            arguments((TriFunction<Throwable, String, Object[], LoggingEvent>) LoggingEvent::debug, DEBUG),
            arguments((TriFunction<Throwable, String, Object[], LoggingEvent>) LoggingEvent::info,  INFO),
            arguments((TriFunction<Throwable, String, Object[], LoggingEvent>) LoggingEvent::warn,  WARN),
            arguments((TriFunction<Throwable, String, Object[], LoggingEvent>) LoggingEvent::error, ERROR)
        );
    }

    @ParameterizedTest
    @MethodSource("throwableMessageArgs")
    void throwableMessageArgs(TriFunction<Throwable, String, Object[], LoggingEvent> producer, Level level) {
        LoggingEvent event = producer.apply(throwable, message, new Object[] {arg1, arg2});
        LoggingEvent expected = new LoggingEvent(level, throwable, message, arg1, arg2);
        assertThat(event, is(expected));
    }

    static Stream<Arguments> markerMessageArgs() {
        return Stream.of(
            arguments((TriFunction<Marker, String, Object[], LoggingEvent>) LoggingEvent::trace, TRACE),
            arguments((TriFunction<Marker, String, Object[], LoggingEvent>) LoggingEvent::debug, DEBUG),
            arguments((TriFunction<Marker, String, Object[], LoggingEvent>) LoggingEvent::info,  INFO),
            arguments((TriFunction<Marker, String, Object[], LoggingEvent>) LoggingEvent::warn,  WARN),
            arguments((TriFunction<Marker, String, Object[], LoggingEvent>) LoggingEvent::error, ERROR)
        );
    }

    @ParameterizedTest
    @MethodSource("markerMessageArgs")
    void markerMessageArgs(TriFunction<Marker, String, Object[], LoggingEvent> producer, Level level) {
        LoggingEvent event = producer.apply(marker, message, new Object[] {arg1, arg2});
        LoggingEvent expected = new LoggingEvent(level, marker, message, arg1, arg2);
        assertThat(event, is(expected));
    }

    static Stream<Arguments> mdcMessageArgs() {
        return Stream.of(
            arguments((TriFunction<Map<String, String>, String, Object[], LoggingEvent>) LoggingEvent::trace, TRACE),
            arguments((TriFunction<Map<String, String>, String, Object[], LoggingEvent>) LoggingEvent::debug, DEBUG),
            arguments((TriFunction<Map<String, String>, String, Object[], LoggingEvent>) LoggingEvent::info,  INFO),
            arguments((TriFunction<Map<String, String>, String, Object[], LoggingEvent>) LoggingEvent::warn,  WARN),
            arguments((TriFunction<Map<String, String>, String, Object[], LoggingEvent>) LoggingEvent::error, ERROR)
        );
    }

    @ParameterizedTest
    @MethodSource("mdcMessageArgs")
    void mdcMessageArgs(TriFunction<Map<String, String>, String, Object[], LoggingEvent> producer, Level level) {
        LoggingEvent event = producer.apply(mdc, message, new Object[] {arg1, arg2});
        LoggingEvent expected = new LoggingEvent(level, mdc, message, arg1, arg2);
        assertThat(event, is(expected));
    }

    @Test
    void traceMarkerThrowableMessageArgs() {
        LoggingEvent event = trace(marker, throwable, message, arg1, arg2);
        LoggingEvent expected = new LoggingEvent(TRACE, marker, throwable, message, arg1, arg2);
        assertThat(event, is(expected));
    }

    @Test
    void traceMdcThrowableMessageArgs() {
        LoggingEvent event = trace(mdc, throwable, message, arg1, arg2);
        LoggingEvent expected = new LoggingEvent(TRACE, mdc, throwable, message, arg1, arg2);
        assertThat(event, is(expected));
    }

    @Test
    void traceMdcMarkerMessageArgs() {
        LoggingEvent event = trace(mdc, marker, message, arg1, arg2);
        LoggingEvent expected = new LoggingEvent(TRACE, mdc, marker, message, arg1, arg2);
        assertThat(event, is(expected));
    }

    @Test
    void traceMdcMarkerThrowableMessageArgs() {
        LoggingEvent event = trace(mdc, marker, throwable, message, arg1, arg2);
        LoggingEvent expected = new LoggingEvent(TRACE, mdc, marker, throwable, message, arg1, arg2);
        assertThat(event, is(expected));
    }

    @Test
    void debugMarkerThrowableMessageArgs() {
        LoggingEvent event = debug(marker, throwable, message, arg1, arg2);
        LoggingEvent expected = new LoggingEvent(DEBUG, marker, throwable, message, arg1, arg2);
        assertThat(event, is(expected));
    }

    @Test
    void debugMdcThrowableMessageArgs() {
        LoggingEvent event = debug(mdc, throwable, message, arg1, arg2);
        LoggingEvent expected = new LoggingEvent(DEBUG, mdc, throwable, message, arg1, arg2);
        assertThat(event, is(expected));
    }

    @Test
    void debugMdcMarkerMessageArgs() {
        LoggingEvent event = debug(mdc, marker, message, arg1, arg2);
        LoggingEvent expected = new LoggingEvent(DEBUG, mdc, marker, message, arg1, arg2);
        assertThat(event, is(expected));
    }

    @Test
    void debugMdcMarkerThrowableMessageArgs() {
        LoggingEvent event = debug(mdc, marker, throwable, message, arg1, arg2);
        LoggingEvent expected = new LoggingEvent(DEBUG, mdc, marker, throwable, message, arg1, arg2);
        assertThat(event, is(expected));
    }

    @Test
    void infoMarkerThrowableMessageArgs() {
        LoggingEvent event = info(marker, throwable, message, arg1, arg2);
        LoggingEvent expected = new LoggingEvent(INFO, marker, throwable, message, arg1, arg2);
        assertThat(event, is(expected));
    }

    @Test
    void infoMdcThrowableMessageArgs() {
        LoggingEvent event = info(mdc, throwable, message, arg1, arg2);
        LoggingEvent expected = new LoggingEvent(INFO, mdc, throwable, message, arg1, arg2);
        assertThat(event, is(expected));
    }

    @Test
    void infoMdcMarkerMessageArgs() {
        LoggingEvent event = info(mdc, marker, message, arg1, arg2);
        LoggingEvent expected = new LoggingEvent(INFO, mdc, marker, message, arg1, arg2);
        assertThat(event, is(expected));
    }

    @Test
    void infoMdcMarkerThrowableMessageArgs() {
        LoggingEvent event = info(mdc, marker, throwable, message, arg1, arg2);
        LoggingEvent expected = new LoggingEvent(INFO, mdc, marker, throwable, message, arg1, arg2);
        assertThat(event, is(expected));
    }

    @Test
    void warnMarkerThrowableMessageArgs() {
        LoggingEvent event = warn(marker, throwable, message, arg1, arg2);
        LoggingEvent expected = new LoggingEvent(WARN, marker, throwable, message, arg1, arg2);
        assertThat(event, is(expected));
    }

    @Test
    void warnMdcThrowableMessageArgs() {
        LoggingEvent event = warn(mdc, throwable, message, arg1, arg2);
        LoggingEvent expected = new LoggingEvent(WARN, mdc, throwable, message, arg1, arg2);
        assertThat(event, is(expected));
    }

    @Test
    void warnMdcMarkerMessageArgs() {
        LoggingEvent event = warn(mdc, marker, message, arg1, arg2);
        LoggingEvent expected = new LoggingEvent(WARN, mdc, marker, message, arg1, arg2);
        assertThat(event, is(expected));
    }

    @Test
    void warnMdcMarkerThrowableMessageArgs() {
        LoggingEvent event = warn(mdc, marker, throwable, message, arg1, arg2);
        LoggingEvent expected = new LoggingEvent(WARN, mdc, marker, throwable, message, arg1, arg2);
        assertThat(event, is(expected));
    }

    @Test
    void errorMarkerThrowableMessageArgs() {
        LoggingEvent event = error(marker, throwable, message, arg1, arg2);
        LoggingEvent expected = new LoggingEvent(ERROR, marker, throwable, message, arg1, arg2);
        assertThat(event, is(expected));
    }

    @Test
    void errorMdcThrowableMessageArgs() {
        LoggingEvent event = error(mdc, throwable, message, arg1, arg2);
        LoggingEvent expected = new LoggingEvent(ERROR, mdc, throwable, message, arg1, arg2);
        assertThat(event, is(expected));
    }

    @Test
    void errorMdcMarkerMessageArgs() {
        LoggingEvent event = error(mdc, marker, message, arg1, arg2);
        LoggingEvent expected = new LoggingEvent(ERROR, mdc, marker, message, arg1, arg2);
        assertThat(event, is(expected));
    }

    @Test
    void errorMdcMarkerThrowableMessageArgs() {
        LoggingEvent event = error(mdc, marker, throwable, message, arg1, arg2);
        LoggingEvent expected = new LoggingEvent(ERROR, mdc, marker, throwable, message, arg1, arg2);
        assertThat(event, is(expected));
    }

    @Test
    void mdcIsSnapshotInTime() {
        Map<String, String> mdc = new HashMap<>();
        mdc.put("key", "value1");
        Map<String, String> mdcAtStart = new HashMap<>(mdc);
        LoggingEvent event = new LoggingEvent(level, mdc, message);
        mdc.put("key", "value2");
        assertThat(event.getMdc(), is(mdcAtStart));
    }

    @Test
    void mdcNotModifiable() {
        Map<String, String> mdc = Collections.singletonMap("key", "value1");
        assertThat(new LoggingEvent(level, mdc, message).getMdc(), is(instanceOf(ImmutableMap.class)) );
    }

    @Test
    void argsIsSnapshotInTime() {
        Object[] args = new Object[]{arg1, arg2};
        Object[] argsAtStart = Arrays.copyOf(args, args.length);
        LoggingEvent event = new LoggingEvent(level, message, args);
        args[0] = "differentArg";
        assertThat(event.getArguments(), is(asList(argsAtStart)));
    }

    @Test
    void argsNotModifiable() {
        assertThat(new LoggingEvent(level, message, arg1).getArguments(), is(instanceOf(ImmutableList.class)));
    }

    @Test
    void timestamp() {
        Instant now = Instant.now();
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());
        LoggingEvent event = info("Message");
        assertThat(event.getTimestamp(), is(now));
    }

    @Test
    void creatingLoggerNotPresent() {
        LoggingEvent message = info("message");
        assertThrows(NoSuchElementException.class, message::getCreatingLogger);
    }

    @Test
    void creatingLoggerPresent() {
        final TestLogger logger = TestLoggerFactory.getInstance().getLogger("logger");
        logger.info("message");
        final LoggingEvent event = logger.getLoggingEvents().get(0);
        assertThat(event.getCreatingLogger(), is(logger));
    }

    @Test
    void printToStandardOutNoThrowable() {
        DateTimeUtils.setCurrentMillisFixed(0);

        LoggingEvent event = new LoggingEvent(INFO, "message with {}", "argument");
        event.print();

        assertThat(getStdOut(),
                is("1970-01-01T00:00:00.000Z ["+Thread.currentThread().getName()+"] INFO - message with argument"+lineSeparator()));
    }

    @Test
    void printToStandardOutWithThrowable() {
        DateTimeUtils.setCurrentMillisFixed(0);

        LoggingEvent event = new LoggingEvent(INFO, new Exception(), "message");
        event.print();

        assertThat(getStdOut(),
                startsWith("1970-01-01T00:00:00.000Z ["+Thread.currentThread().getName()+"] INFO - message"+lineSeparator()
                        + "java.lang.Exception"+lineSeparator()
                        + "\tat"
                ));
    }

    @ParameterizedTest
    @EnumSource(names = {"TRACE", "DEBUG", "INFO"})
    void printInfoAndBelow(Level level) {
        LoggingEvent event = new LoggingEvent(level, "message with {}", "argument");
        event.print();
        assertThat(getStdOut(), is(not("")));
        assertThat(getStdErr(), is(""));
    }

    @ParameterizedTest
    @EnumSource(names = {"WARN", "ERROR"})
    void printWarnAndAbove(Level level) {
        LoggingEvent event = new LoggingEvent(level, "message with {}", "argument");
        event.print();
        assertThat(getStdErr(), is(not("")));
        assertThat(getStdOut(), is(""));
    }

    @Test
    void nullArgument() {
        LoggingEvent event = new LoggingEvent(level, "message with null arg", null, null);
        assertThat(event, is(new LoggingEvent(level, "message with null arg", empty(), empty())));
    }

    public interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }
}
