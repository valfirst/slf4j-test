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
import java.util.SortedMap;
import java.util.TreeMap;
import org.slf4j.Marker;
import org.slf4j.event.KeyValuePair;
import org.slf4j.event.Level;
import org.slf4j.helpers.MessageFormatter;

/**
 * Representation of a call to a logger for test assertion purposes. The contract of {@link
 * #equals(Object)} and {@link #hashCode} is that they compare the results of:
 *
 * <ul>
 *   <li>{@link #getLevel()}
 *   <li>{@link #getMdc()}
 *   <li>{@link #getMarkers()}
 *   <li>{@link #getKeyValuePairs()}
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
    private static final Object[] emptyObjectArray = {};

    private final Level level;
    private final SortedMap<String, String> mdc;
    private final List<Marker> markers;
    private final List<KeyValuePair> keyValuePairs;
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

    /**
     * Create a {@link LoggingEvent} from an SLF4J {@link org.slf4j.event.LoggingEvent}.
     *
     * @since 3.0.0
     */
    public static LoggingEvent fromSlf4jEvent(org.slf4j.event.LoggingEvent event) {
        return fromSlf4jEvent(event, Collections.emptyMap());
    }

    /**
     * Create a {@link LoggingEvent} with an MDC from an SLF4J {@link org.slf4j.event.LoggingEvent}.
     *
     * @since 3.0.0
     */
    public static LoggingEvent fromSlf4jEvent(
            org.slf4j.event.LoggingEvent event, Map<String, String> mdc) {
        List<Marker> markers = event.getMarkers();
        List<KeyValuePair> keyValuePairs = event.getKeyValuePairs();
        Object[] arguments = event.getArgumentArray();
        return new LoggingEvent(
                empty(),
                event.getLevel(),
                mdc,
                markers == null
                        ? Collections.emptyList()
                        : Collections.unmodifiableList(new ArrayList<>(markers)),
                keyValuePairs == null
                        ? Collections.emptyList()
                        : Collections.unmodifiableList(new ArrayList<>(keyValuePairs)),
                ofNullable(event.getThrowable()),
                event.getMessage(),
                arguments == null ? emptyObjectArray : arguments);
    }

    public LoggingEvent(final Level level, final String message, final Object... arguments) {
        this(level, Collections.emptySortedMap(), empty(), empty(), message, arguments);
    }

    public LoggingEvent(
            final Level level,
            final Throwable throwable,
            final String message,
            final Object... arguments) {
        this(level, Collections.emptySortedMap(), empty(), ofNullable(throwable), message, arguments);
    }

    public LoggingEvent(
            final Level level, final Marker marker, final String message, final Object... arguments) {
        this(level, Collections.emptySortedMap(), ofNullable(marker), empty(), message, arguments);
    }

    public LoggingEvent(
            final Level level,
            final Marker marker,
            final Throwable throwable,
            final String message,
            final Object... arguments) {
        this(
                level,
                Collections.emptySortedMap(),
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
        this(
                empty(),
                level,
                mdc,
                marker.map(Collections::singletonList).orElseGet(Collections::emptyList),
                Collections.emptyList(),
                throwable,
                message,
                arguments);
    }

    LoggingEvent(
            final Optional<TestLogger> creatingLogger,
            final Level level,
            final Map<String, String> mdc,
            final List<Marker> markers,
            final List<KeyValuePair> keyValuePairs,
            final Optional<Throwable> throwable,
            final String message,
            final Object... arguments) {
        super();
        this.creatingLogger = creatingLogger;
        this.level = requireNonNull(level);
        this.mdc =
                requireNonNull(mdc).isEmpty()
                        ? Collections.emptySortedMap()
                        : Collections.unmodifiableSortedMap(new TreeMap<>(mdc));
        this.markers = markers;
        this.keyValuePairs = keyValuePairs;
        this.throwable = requireNonNull(throwable);
        this.message = message;
        this.arguments =
                arguments.length == 0
                        ? Collections.emptyList()
                        : Collections.unmodifiableList(new ArrayList<>(Arrays.asList(arguments)));
    }

    public Level getLevel() {
        return level;
    }

    /**
     * Get the MDC of the event. For events created by {@link TestLogger}, this is an unmodifiable
     * copy of the MDC of the thread when the event was created. For events constructed directly, this
     * is unmodifiable copy of the MDC passed to the constructor, if any. If no MDC was used for
     * construction, the copy is an empty map. The copy is a {@link SortedMap}, in order to make it
     * easier to spot discrepancies in case an assertion fails. Natural ordering of the keys is used.
     */
    public SortedMap<String, String> getMdc() {
        return mdc;
    }

    /**
     * Get the marker of the event.
     *
     * @deprecated As events created using the SLF4J fluent API can contain multiple markers, this
     *     method is deprecated in favor of {@link #getMarkers}.
     * @throws IllegalStateException if the event has more than one marker.
     */
    @Deprecated
    public Optional<Marker> getMarker() {
        if (markers.isEmpty()) {
            return empty();
        }
        if (markers.size() == 1) {
            return Optional.of(markers.get(0));
        }
        throw new IllegalStateException("LoggingEvent has more than one marker");
    }

    /**
     * Get the markers of the event. If the event has no markers, an empty list is returned.
     *
     * @return an unmodifiable copy of the markers when the event was created.
     * @since 3.0.0
     */
    public List<Marker> getMarkers() {
        return markers;
    }

    /**
     * Get the key/value pairs of the event. If the event has no key/value pairs, an empty list is
     * returned.
     *
     * @return an unmodifiable copy of the key/value pairs when the event was created.
     * @since 3.0.0
     */
    public List<KeyValuePair> getKeyValuePairs() {
        return keyValuePairs;
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
                && Objects.equals(markers, that.markers)
                && Objects.equals(keyValuePairs, that.keyValuePairs)
                && Objects.equals(throwable, that.throwable)
                && Objects.equals(message, that.message)
                && Objects.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, mdc, markers, keyValuePairs, throwable, message, arguments);
    }

    @Override
    public String toString() {
        return "LoggingEvent{"
                + "level="
                + level
                + ", mdc="
                + mdc
                + ", markers="
                + markers
                + ", keyValuePairs="
                + keyValuePairs
                + ", throwable="
                + throwable
                + ", message="
                + (message == null ? "null" : '\'' + message + '\'')
                + ", arguments="
                + arguments
                + '}';
    }
}
