package com.github.valfirst.slf4jtest;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

import java.io.PrintStream;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.MessageFormatter;

/**
 * Representation of a call to a logger for test assertion purposes. The contract of {@link
 * #equals(Object)} and {@link #hashCode} is that they compare the results of:
 *
 * <ul>
 *   <li>{@link #getLevel()}
 *   <li>{@link #getMdc()}
 *   <li>{@link #getMarker()}
 *   <li>{@link #getThrowable()}
 *   <li>{@link #getMessage()}
 *   <li>{@link #getArguments()}
 * </ul>
 *
 * <p>They do NOT compare the results of {@link #getTimestamp()}, {@link #getCreatingLogger()} or
 * {@link #getThreadContextClassLoader()} as this would render it impractical to create appropriate
 * expected {@link LoggingEvent}s to compare against.
 *
 * <p>Constructors and convenient static factory methods exist to create {@link LoggingEvent}s with
 * appropriate defaults. These are not documented further as they should be self-evident.
 */
@SuppressWarnings({"PMD.ExcessivePublicCount", "PMD.TooManyMethods"})
public class LoggingEvent {
    private static final DateTimeFormatter ISO_FORMAT =
            new DateTimeFormatterBuilder().appendInstant(3).toFormatter();

    private final Level level;
    private final Map<String, String> mdc;
    private final Optional<Marker> marker;
    private final Optional<Throwable> throwable;
    private final String message;
    private final List<Object> arguments;

    private final Optional<TestLogger> creatingLogger;
    private final Instant timestamp = Instant.now();
    private final String threadName = Thread.currentThread().getName();
    private final ClassLoader threadContextClassLoader =
            Thread.currentThread().getContextClassLoader();

    public static LoggingEvent trace(final String message, final Object... arguments) {
        return new LoggingEvent(Level.TRACE, message, arguments);
    }

    public static LoggingEvent trace(
            final Throwable throwable, final String message, final Object... arguments) {
        return new LoggingEvent(Level.TRACE, throwable, message, arguments);
    }

    public static LoggingEvent trace(
            final Marker marker, final String message, final Object... arguments) {
        return new LoggingEvent(Level.TRACE, marker, message, arguments);
    }

    public static LoggingEvent trace(
            final Marker marker,
            final Throwable throwable,
            final String message,
            final Object... arguments) {
        return new LoggingEvent(Level.TRACE, marker, throwable, message, arguments);
    }

    public static LoggingEvent trace(
            final Map<String, String> mdc, final String message, final Object... arguments) {
        return new LoggingEvent(Level.TRACE, mdc, message, arguments);
    }

    public static LoggingEvent trace(
            final Map<String, String> mdc,
            final Throwable throwable,
            final String message,
            final Object... arguments) {
        return new LoggingEvent(Level.TRACE, mdc, throwable, message, arguments);
    }

    public static LoggingEvent trace(
            final Map<String, String> mdc,
            final Marker marker,
            final String message,
            final Object... arguments) {
        return new LoggingEvent(Level.TRACE, mdc, marker, message, arguments);
    }

    public static LoggingEvent trace(
            final Map<String, String> mdc,
            final Marker marker,
            final Throwable throwable,
            final String message,
            final Object... arguments) {
        return new LoggingEvent(Level.TRACE, mdc, marker, throwable, message, arguments);
    }

    public static LoggingEvent debug(final String message, final Object... arguments) {
        return new LoggingEvent(Level.DEBUG, message, arguments);
    }

    public static LoggingEvent debug(
            final Throwable throwable, final String message, final Object... arguments) {
        return new LoggingEvent(Level.DEBUG, throwable, message, arguments);
    }

    public static LoggingEvent debug(
            final Marker marker, final String message, final Object... arguments) {
        return new LoggingEvent(Level.DEBUG, marker, message, arguments);
    }

    public static LoggingEvent debug(
            final Marker marker,
            final Throwable throwable,
            final String message,
            final Object... arguments) {
        return new LoggingEvent(Level.DEBUG, marker, throwable, message, arguments);
    }

    public static LoggingEvent debug(
            final Map<String, String> mdc, final String message, final Object... arguments) {
        return new LoggingEvent(Level.DEBUG, mdc, message, arguments);
    }

    public static LoggingEvent debug(
            final Map<String, String> mdc,
            final Throwable throwable,
            final String message,
            final Object... arguments) {
        return new LoggingEvent(Level.DEBUG, mdc, throwable, message, arguments);
    }

    public static LoggingEvent debug(
            final Map<String, String> mdc,
            final Marker marker,
            final String message,
            final Object... arguments) {
        return new LoggingEvent(Level.DEBUG, mdc, marker, message, arguments);
    }

    public static LoggingEvent debug(
            final Map<String, String> mdc,
            final Marker marker,
            final Throwable throwable,
            final String message,
            final Object... arguments) {
        return new LoggingEvent(Level.DEBUG, mdc, marker, throwable, message, arguments);
    }

    public static LoggingEvent info(final String message, final Object... arguments) {
        return new LoggingEvent(Level.INFO, message, arguments);
    }

    public static LoggingEvent info(
            final Throwable throwable, final String message, final Object... arguments) {
        return new LoggingEvent(Level.INFO, throwable, message, arguments);
    }

    public static LoggingEvent info(
            final Marker marker, final String message, final Object... arguments) {
        return new LoggingEvent(Level.INFO, marker, message, arguments);
    }

    public static LoggingEvent info(
            final Marker marker,
            final Throwable throwable,
            final String message,
            final Object... arguments) {
        return new LoggingEvent(Level.INFO, marker, throwable, message, arguments);
    }

    public static LoggingEvent info(
            final Map<String, String> mdc, final String message, final Object... arguments) {
        return new LoggingEvent(Level.INFO, mdc, message, arguments);
    }

    public static LoggingEvent info(
            final Map<String, String> mdc,
            final Throwable throwable,
            final String message,
            final Object... arguments) {
        return new LoggingEvent(Level.INFO, mdc, throwable, message, arguments);
    }

    public static LoggingEvent info(
            final Map<String, String> mdc,
            final Marker marker,
            final String message,
            final Object... arguments) {
        return new LoggingEvent(Level.INFO, mdc, marker, message, arguments);
    }

    public static LoggingEvent info(
            final Map<String, String> mdc,
            final Marker marker,
            final Throwable throwable,
            final String message,
            final Object... arguments) {
        return new LoggingEvent(Level.INFO, mdc, marker, throwable, message, arguments);
    }

    public static LoggingEvent warn(final String message, final Object... arguments) {
        return new LoggingEvent(Level.WARN, message, arguments);
    }

    public static LoggingEvent warn(
            final Throwable throwable, final String message, final Object... arguments) {
        return new LoggingEvent(Level.WARN, throwable, message, arguments);
    }

    public static LoggingEvent warn(
            final Marker marker, final String message, final Object... arguments) {
        return new LoggingEvent(Level.WARN, marker, message, arguments);
    }

    public static LoggingEvent warn(
            final Marker marker,
            final Throwable throwable,
            final String message,
            final Object... arguments) {
        return new LoggingEvent(Level.WARN, marker, throwable, message, arguments);
    }

    public static LoggingEvent warn(
            final Map<String, String> mdc, final String message, final Object... arguments) {
        return new LoggingEvent(Level.WARN, mdc, message, arguments);
    }

    public static LoggingEvent warn(
            final Map<String, String> mdc,
            final Throwable throwable,
            final String message,
            final Object... arguments) {
        return new LoggingEvent(Level.WARN, mdc, throwable, message, arguments);
    }

    public static LoggingEvent warn(
            final Map<String, String> mdc,
            final Marker marker,
            final String message,
            final Object... arguments) {
        return new LoggingEvent(Level.WARN, mdc, marker, message, arguments);
    }

    public static LoggingEvent warn(
            final Map<String, String> mdc,
            final Marker marker,
            final Throwable throwable,
            final String message,
            final Object... arguments) {
        return new LoggingEvent(Level.WARN, mdc, marker, throwable, message, arguments);
    }

    public static LoggingEvent error(final String message, final Object... arguments) {
        return new LoggingEvent(Level.ERROR, message, arguments);
    }

    public static LoggingEvent error(
            final Throwable throwable, final String message, final Object... arguments) {
        return new LoggingEvent(Level.ERROR, throwable, message, arguments);
    }

    public static LoggingEvent error(
            final Marker marker, final String message, final Object... arguments) {
        return new LoggingEvent(Level.ERROR, marker, message, arguments);
    }

    public static LoggingEvent error(
            final Marker marker,
            final Throwable throwable,
            final String message,
            final Object... arguments) {
        return new LoggingEvent(Level.ERROR, marker, throwable, message, arguments);
    }

    public static LoggingEvent error(
            final Map<String, String> mdc, final String message, final Object... arguments) {
        return new LoggingEvent(Level.ERROR, mdc, message, arguments);
    }

    public static LoggingEvent error(
            final Map<String, String> mdc,
            final Throwable throwable,
            final String message,
            final Object... arguments) {
        return new LoggingEvent(Level.ERROR, mdc, throwable, message, arguments);
    }

    public static LoggingEvent error(
            final Map<String, String> mdc,
            final Marker marker,
            final String message,
            final Object... arguments) {
        return new LoggingEvent(Level.ERROR, mdc, marker, message, arguments);
    }

    public static LoggingEvent error(
            final Map<String, String> mdc,
            final Marker marker,
            final Throwable throwable,
            final String message,
            final Object... arguments) {
        return new LoggingEvent(Level.ERROR, mdc, marker, throwable, message, arguments);
    }

    public LoggingEvent(final Level level, final String message, final Object... arguments) {
        this(level, Collections.emptyMap(), empty(), empty(), message, arguments);
    }

    public LoggingEvent(
            final Level level,
            final Throwable throwable,
            final String message,
            final Object... arguments) {
        this(level, Collections.emptyMap(), empty(), ofNullable(throwable), message, arguments);
    }

    public LoggingEvent(
            final Level level, final Marker marker, final String message, final Object... arguments) {
        this(level, Collections.emptyMap(), ofNullable(marker), empty(), message, arguments);
    }

    public LoggingEvent(
            final Level level,
            final Marker marker,
            final Throwable throwable,
            final String message,
            final Object... arguments) {
        this(
                level,
                Collections.emptyMap(),
                ofNullable(marker),
                ofNullable(throwable),
                message,
                arguments);
    }

    public LoggingEvent(
            final Level level,
            final Map<String, String> mdc,
            final String message,
            final Object... arguments) {
        this(level, mdc, empty(), empty(), message, arguments);
    }

    public LoggingEvent(
            final Level level,
            final Map<String, String> mdc,
            final Throwable throwable,
            final String message,
            final Object... arguments) {
        this(level, mdc, empty(), ofNullable(throwable), message, arguments);
    }

    public LoggingEvent(
            final Level level,
            final Map<String, String> mdc,
            final Marker marker,
            final String message,
            final Object... arguments) {
        this(level, mdc, ofNullable(marker), empty(), message, arguments);
    }

    public LoggingEvent(
            final Level level,
            final Map<String, String> mdc,
            final Marker marker,
            final Throwable throwable,
            final String message,
            final Object... arguments) {
        this(level, mdc, ofNullable(marker), ofNullable(throwable), message, arguments);
    }

    private LoggingEvent(
            final Level level,
            final Map<String, String> mdc,
            final Optional<Marker> marker,
            final Optional<Throwable> throwable,
            final String message,
            final Object... arguments) {
        this(empty(), level, mdc, marker, throwable, message, arguments);
    }

    LoggingEvent(
            final Optional<TestLogger> creatingLogger,
            final Level level,
            final Map<String, String> mdc,
            final Optional<Marker> marker,
            final Optional<Throwable> throwable,
            final String message,
            final Object... arguments) {
        super();
        this.creatingLogger = creatingLogger;
        this.level = requireNonNull(level);
        this.mdc =
                mdc == null ? Collections.emptyMap() : Collections.unmodifiableMap(new TreeMap<>(mdc));
        this.marker = requireNonNull(marker);
        this.throwable = requireNonNull(throwable);
        this.message = message;
        this.arguments = Collections.unmodifiableList(new ArrayList<>(Arrays.asList(arguments)));
    }

    public Level getLevel() {
        return level;
    }

    /**
     * Get the MDC of the event. This is a copy of the MDC when the event was created. The returned
     * value is an unmodifiable {@link java.util.SortedMap} with natural ordering of the keys.
     */
    public Map<String, String> getMdc() {
        return mdc;
    }

    public Optional<Marker> getMarker() {
        return marker;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Get the arguments to the event.
     *
     * @return an unmodifiable copy of the arguments when the event was created.
     */
    public List<Object> getArguments() {
        return arguments;
    }

    public Optional<Throwable> getThrowable() {
        return throwable;
    }

    /**
     * @return the logger that created this logging event.
     * @throws IllegalStateException if this logging event was not created by a logger
     */
    public TestLogger getCreatingLogger() {
        return creatingLogger.get();
    }

    /**
     * @return the time at which this logging event was created
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * @return the name of the thread that created this logging event
     */
    public String getThreadName() {
        return threadName;
    }

    /**
     * @return the Thread Context Classloader used when this logging event was created
     */
    public ClassLoader getThreadContextClassLoader() {
        return threadContextClassLoader;
    }

    void print() {
        final PrintStream output = printStreamForLevel();
        output.println(formatLogStatement());
        throwable.ifPresent(throwableToPrint -> throwableToPrint.printStackTrace(output));
    }

    private String formatLogStatement() {
        return ISO_FORMAT.format(getTimestamp())
                + " ["
                + getThreadName()
                + "] "
                + getLevel()
                + safeLoggerName()
                + " - "
                + getFormattedMessage();
    }

    private String safeLoggerName() {
        return creatingLogger.map(logger -> " " + logger.getName()).orElse("");
    }

    public String getFormattedMessage() {
        Object[] argumentsWithNulls = getArguments().toArray();
        return MessageFormatter.arrayFormat(getMessage(), argumentsWithNulls).getMessage();
    }

    private PrintStream printStreamForLevel() {
        switch (level) {
            case ERROR:
            case WARN:
                return System.err;
            default:
                return System.out;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LoggingEvent that = (LoggingEvent) o;
        return level == that.level
                && Objects.equals(mdc, that.mdc)
                && Objects.equals(marker, that.marker)
                && Objects.equals(throwable, that.throwable)
                && Objects.equals(message, that.message)
                && Objects.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, mdc, marker, throwable, message, arguments);
    }

    @Override
    public String toString() {
        return "LoggingEvent{"
                + "level="
                + level
                + ", mdc="
                + mdc
                + ", marker="
                + marker
                + ", throwable="
                + throwable
                + ", message='"
                + message
                + '\''
                + ", arguments="
                + arguments
                + '}';
    }
}
