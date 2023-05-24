package com.github.valfirst.slf4jtest;

import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import uk.org.lidalia.lang.ThreadLocal;

/**
 * Implementation of {@link Logger} which stores {@link LoggingEvent}s in memory and provides
 * methods to access and remove them in order to facilitate writing tests that assert particular
 * logging calls were made.
 *
 * <p>{@link LoggingEvent}s are stored in both an {@link ThreadLocal} and a normal {@link List}. The
 * {@link #getLoggingEvents()} and {@link #clear()} methods reference the {@link ThreadLocal}
 * events. The {@link #getAllLoggingEvents()} and {@link #clearAll()} methods reference all events
 * logged on this Logger. This is in order to facilitate parallelising tests - tests that use the
 * thread local methods can be parallelised.
 *
 * <p>By default all Levels are enabled. It is important to note that the conventional hierarchical
 * notion of Levels, where info being enabled implies warn and error being enabled, is not a
 * requirement of the SLF4J API, so the {@link #setEnabledLevels(Collection)}, {@link
 * #setEnabledLevels(Level...)}, {@link #setEnabledLevelsForAllThreads(Collection)}, {@link
 * #setEnabledLevelsForAllThreads(Level...)} and the various isXxxxxEnabled() methods make no
 * assumptions about this hierarchy.
 */
@SuppressWarnings({"PMD.ExcessivePublicCount", "PMD.TooManyMethods"})
public class TestLogger implements Logger {

    private final String name;
    private final TestLoggerFactory testLoggerFactory;
    private final ThreadLocal<List<LoggingEvent>> loggingEvents = new ThreadLocal<>(ArrayList::new);

    private final List<LoggingEvent> allLoggingEvents = new CopyOnWriteArrayList<>();
    private volatile ThreadLocal<Set<Level>> enabledLevels =
            new ThreadLocal<>(Collections.unmodifiableSet(EnumSet.allOf(Level.class)));

    TestLogger(final String name, final TestLoggerFactory testLoggerFactory) {
        this.name = name;
        this.testLoggerFactory = testLoggerFactory;
    }

    public String getName() {
        return name;
    }

    /**
     * Removes all {@link LoggingEvent}s logged by this thread and resets the enabled levels of the
     * logger to all levels for this thread.
     */
    public void clear() {
        loggingEvents.get().clear();
        enabledLevels.remove();
    }

    /**
     * Removes ALL {@link LoggingEvent}s logged on this logger, regardless of thread, and resets the
     * enabled levels of the logger to all levels for ALL threads.
     */
    public void clearAll() {
        allLoggingEvents.clear();
        loggingEvents.reset();
        enabledLevels.reset();
    }

    /**
     * Get all {@link LoggingEvent}s logged on this logger by this thread.
     *
     * @return the {@link LoggingEvent}s as an unmodifiable {@link List}.
     */
    public List<LoggingEvent> getLoggingEvents() {
        return Collections.unmodifiableList(new ArrayList<>(loggingEvents.get()));
    }

    /**
     * Get all {@link LoggingEvent}s logged on this logger by ANY thread.
     *
     * @return the {@link LoggingEvent}s as an unmodifiable {@link List}.
     */
    public List<LoggingEvent> getAllLoggingEvents() {
        return Collections.unmodifiableList(new ArrayList<>(allLoggingEvents));
    }

    /**
     * @return whether this logger is trace enabled in this thread
     */
    @Override
    public boolean isTraceEnabled() {
        return enabledLevels.get().contains(Level.TRACE);
    }

    @Override
    public void trace(final String message) {
        log(Level.TRACE, message);
    }

    @Override
    public void trace(final String format, final Object arg) {
        log(Level.TRACE, format, arg);
    }

    @Override
    public void trace(final String format, final Object arg1, final Object arg2) {
        log(Level.TRACE, format, arg1, arg2);
    }

    @Override
    public void trace(final String format, final Object... args) {
        log(Level.TRACE, format, args);
    }

    @Override
    public void trace(final String msg, final Throwable throwable) {
        log(Level.TRACE, msg, throwable);
    }

    @Override
    public boolean isTraceEnabled(final Marker marker) {
        return enabledLevels.get().contains(Level.TRACE);
    }

    @Override
    public void trace(final Marker marker, final String msg) {
        log(Level.TRACE, marker, msg);
    }

    @Override
    public void trace(final Marker marker, final String format, final Object arg) {
        log(Level.TRACE, marker, format, arg);
    }

    @Override
    public void trace(
            final Marker marker, final String format, final Object arg1, final Object arg2) {
        log(Level.TRACE, marker, format, arg1, arg2);
    }

    @Override
    public void trace(final Marker marker, final String format, final Object... args) {
        log(Level.TRACE, marker, format, args);
    }

    @Override
    public void trace(final Marker marker, final String msg, final Throwable throwable) {
        log(Level.TRACE, marker, msg, throwable);
    }

    /**
     * @return whether this logger is debug enabled in this thread
     */
    @Override
    public boolean isDebugEnabled() {
        return enabledLevels.get().contains(Level.DEBUG);
    }

    @Override
    public void debug(final String message) {
        log(Level.DEBUG, message);
    }

    @Override
    public void debug(final String format, final Object arg) {
        log(Level.DEBUG, format, arg);
    }

    @Override
    public void debug(final String format, final Object arg1, final Object arg2) {
        log(Level.DEBUG, format, arg1, arg2);
    }

    @Override
    public void debug(final String format, final Object... args) {
        log(Level.DEBUG, format, args);
    }

    @Override
    public void debug(final String msg, final Throwable throwable) {
        log(Level.DEBUG, msg, throwable);
    }

    @Override
    public boolean isDebugEnabled(final Marker marker) {
        return enabledLevels.get().contains(Level.DEBUG);
    }

    @Override
    public void debug(final Marker marker, final String msg) {
        log(Level.DEBUG, marker, msg);
    }

    @Override
    public void debug(final Marker marker, final String format, final Object arg) {
        log(Level.DEBUG, marker, format, arg);
    }

    @Override
    public void debug(
            final Marker marker, final String format, final Object arg1, final Object arg2) {
        log(Level.DEBUG, marker, format, arg1, arg2);
    }

    @Override
    public void debug(final Marker marker, final String format, final Object... args) {
        log(Level.DEBUG, marker, format, args);
    }

    @Override
    public void debug(final Marker marker, final String msg, final Throwable throwable) {
        log(Level.DEBUG, marker, msg, throwable);
    }

    /**
     * @return whether this logger is info enabled in this thread
     */
    @Override
    public boolean isInfoEnabled() {
        return enabledLevels.get().contains(Level.INFO);
    }

    @Override
    public void info(final String message) {
        log(Level.INFO, message);
    }

    @Override
    public void info(final String format, final Object arg) {
        log(Level.INFO, format, arg);
    }

    @Override
    public void info(final String format, final Object arg1, final Object arg2) {
        log(Level.INFO, format, arg1, arg2);
    }

    @Override
    public void info(final String format, final Object... args) {
        log(Level.INFO, format, args);
    }

    @Override
    public void info(final String msg, final Throwable throwable) {
        log(Level.INFO, msg, throwable);
    }

    @Override
    public boolean isInfoEnabled(final Marker marker) {
        return enabledLevels.get().contains(Level.INFO);
    }

    @Override
    public void info(final Marker marker, final String msg) {
        log(Level.INFO, marker, msg);
    }

    @Override
    public void info(final Marker marker, final String format, final Object arg) {
        log(Level.INFO, marker, format, arg);
    }

    @Override
    public void info(final Marker marker, final String format, final Object arg1, final Object arg2) {
        log(Level.INFO, marker, format, arg1, arg2);
    }

    @Override
    public void info(final Marker marker, final String format, final Object... args) {
        log(Level.INFO, marker, format, args);
    }

    @Override
    public void info(final Marker marker, final String msg, final Throwable throwable) {
        log(Level.INFO, marker, msg, throwable);
    }

    /**
     * @return whether this logger is warn enabled in this thread
     */
    @Override
    public boolean isWarnEnabled() {
        return enabledLevels.get().contains(Level.WARN);
    }

    @Override
    public void warn(final String message) {
        log(Level.WARN, message);
    }

    @Override
    public void warn(final String format, final Object arg) {
        log(Level.WARN, format, arg);
    }

    @Override
    public void warn(final String format, final Object arg1, final Object arg2) {
        log(Level.WARN, format, arg1, arg2);
    }

    @Override
    public void warn(final String format, final Object... args) {
        log(Level.WARN, format, args);
    }

    @Override
    public void warn(final String msg, final Throwable throwable) {
        log(Level.WARN, msg, throwable);
    }

    @Override
    public boolean isWarnEnabled(final Marker marker) {
        return enabledLevels.get().contains(Level.WARN);
    }

    @Override
    public void warn(final Marker marker, final String msg) {
        log(Level.WARN, marker, msg);
    }

    @Override
    public void warn(final Marker marker, final String format, final Object arg) {
        log(Level.WARN, marker, format, arg);
    }

    @Override
    public void warn(final Marker marker, final String format, final Object arg1, final Object arg2) {
        log(Level.WARN, marker, format, arg1, arg2);
    }

    @Override
    public void warn(final Marker marker, final String format, final Object... args) {
        log(Level.WARN, marker, format, args);
    }

    @Override
    public void warn(final Marker marker, final String msg, final Throwable throwable) {
        log(Level.WARN, marker, msg, throwable);
    }

    /**
     * @return whether this logger is error enabled in this thread
     */
    @Override
    public boolean isErrorEnabled() {
        return enabledLevels.get().contains(Level.ERROR);
    }

    @Override
    public void error(final String message) {
        log(Level.ERROR, message);
    }

    @Override
    public void error(final String format, final Object arg) {
        log(Level.ERROR, format, arg);
    }

    @Override
    public void error(final String format, final Object arg1, final Object arg2) {
        log(Level.ERROR, format, arg1, arg2);
    }

    @Override
    public void error(final String format, final Object... args) {
        log(Level.ERROR, format, args);
    }

    @Override
    public void error(final String msg, final Throwable throwable) {
        log(Level.ERROR, msg, throwable);
    }

    @Override
    public boolean isErrorEnabled(final Marker marker) {
        return enabledLevels.get().contains(Level.ERROR);
    }

    @Override
    public void error(final Marker marker, final String msg) {
        log(Level.ERROR, marker, msg);
    }

    @Override
    public void error(final Marker marker, final String format, final Object arg) {
        log(Level.ERROR, marker, format, arg);
    }

    @Override
    public void error(
            final Marker marker, final String format, final Object arg1, final Object arg2) {
        log(Level.ERROR, marker, format, arg1, arg2);
    }

    @Override
    public void error(final Marker marker, final String format, final Object... args) {
        log(Level.ERROR, marker, format, args);
    }

    @Override
    public void error(final Marker marker, final String msg, final Throwable throwable) {
        log(Level.ERROR, marker, msg, throwable);
    }

    private void log(final Level level, final String format, final Object... args) {
        log(level, format, Optional.empty(), args);
    }

    private void log(final Level level, final String msg, final Throwable throwable) {
        addLoggingEvent(level, Optional.empty(), ofNullable(throwable), msg);
    }

    private void log(
            final Level level, final Marker marker, final String format, final Object... args) {
        log(level, format, ofNullable(marker), args);
    }

    private void log(
            final Level level, final Marker marker, final String msg, final Throwable throwable) {
        addLoggingEvent(level, ofNullable(marker), ofNullable(throwable), msg);
    }

    private void log(
            final Level level, final String format, final Optional<Marker> marker, final Object[] args) {
        final FormattingTuple formattedArgs = MessageFormatter.arrayFormat(format, args);
        addLoggingEvent(
                level,
                marker,
                ofNullable(formattedArgs.getThrowable()),
                format,
                formattedArgs.getArgArray());
    }

    private void addLoggingEvent(
            final Level level,
            final Optional<Marker> marker,
            final Optional<Throwable> throwable,
            final String format,
            final Object... args) {
        if (enabledLevels.get().contains(level) && enabledByGlobalCaptureLevel(level)) {
            final LoggingEvent event =
                    new LoggingEvent(Optional.of(this), level, mdc(), marker, throwable, format, args);
            allLoggingEvents.add(event);
            loggingEvents.get().add(event);
            testLoggerFactory.addLoggingEvent(event);
            optionallyPrint(event);
        }
    }

    private boolean enabledByGlobalCaptureLevel(Level level) {
        Level captureLevel = testLoggerFactory.getCaptureLevel();
        return captureLevel != null && captureLevel.compareTo(level) >= 0;
    }

    private Map<String, String> mdc() {
        return ofNullable(MDC.getCopyOfContextMap()).orElseGet(Collections::emptyMap);
    }

    private void optionallyPrint(final LoggingEvent event) {
        Level printLevel = testLoggerFactory.getPrintLevel();
        if (printLevel != null && printLevel.compareTo(event.getLevel()) >= 0) {
            event.print();
        }
    }

    /**
     * Get the enabled levels levels enabled for this logger on this thread.
     *
     * @return the set of levels as an unmodifiable {@link Set}.
     */
    public Set<Level> getEnabledLevels() {
        return enabledLevels.get();
    }

    /**
     * The conventional hierarchical notion of Levels, where info being enabled implies warn and error
     * being enabled, is not a requirement of the SLF4J API, so all levels you wish to enable must be
     * passed explicitly to this method.
     *
     * @param enabledLevels levels which will be considered enabled for this logger IN THIS THREAD;
     *     does not affect enabled levels for this logger in other threads
     */
    public void setEnabledLevels(final Collection<Level> enabledLevels) {
        this.enabledLevels.set(setOfLevels(enabledLevels));
    }

    /**
     * The conventional hierarchical notion of Levels, where info being enabled implies warn and error
     * being enabled, is not a requirement of the SLF4J API, so all levels you wish to enable must be
     * passed explicitly to this method.
     *
     * @param enabledLevels levels which will be considered enabled for this logger IN THIS THREAD;
     *     does not affect enabled levels for this logger in other threads
     */
    public void setEnabledLevels(final Level... enabledLevels) {
        setEnabledLevels(Arrays.asList(enabledLevels));
    }

    /**
     * The conventional hierarchical notion of Levels, where info being enabled implies warn and error
     * being enabled, is not a requirement of the SLF4J API, so all levels you wish to enable must be
     * passed explicitly to this method.
     *
     * <p>Note that this modifies the default enabled levels in all threads. After calling this
     * method, {@link #clear()} and {@link #clearAll()} will set the enabled levels to the value
     * passed to this method, not all levels. You will have to call this method with {@code
     * EnumSet.allOf(Level.class)} (or {@link #setEnabledLevelsForAllThreads(Level...)} with all the
     * levels) to return to standard behaviour.
     *
     * @param enabledLevelsForAllThreads levels which will be considered enabled for this logger IN
     *     ALL THREADS
     */
    public void setEnabledLevelsForAllThreads(final Collection<Level> enabledLevelsForAllThreads) {
        this.enabledLevels = new ThreadLocal<>(setOfLevels(enabledLevelsForAllThreads));
    }

    /**
     * The conventional hierarchical notion of Levels, where info being enabled implies warn and error
     * being enabled, is not a requirement of the SLF4J API, so all levels you wish to enable must be
     * passed explicitly to this method.
     *
     * <p>Note that this modifies the default enabled levels in all threads. After calling this
     * method, {@link #clear()} and {@link #clearAll()} will set the enabled levels to the value
     * passed to this method, not all levels. You will have to call this method with all levels (or
     * {@link #setEnabledLevelsForAllThreads(Collection)} with {@code EnumSet.allOf(Level.class)}) to
     * return to standard behaviour.
     *
     * @param enabledLevelsForAllThreads levels which will be considered enabled for this logger IN
     *     ALL THREADS
     */
    public void setEnabledLevelsForAllThreads(final Level... enabledLevelsForAllThreads) {
        setEnabledLevelsForAllThreads(Arrays.asList(enabledLevelsForAllThreads));
    }

    private Set<Level> setOfLevels(final Collection<Level> levels) {
        return levels.isEmpty()
                ? Collections.emptySet()
                : Collections.unmodifiableSet(EnumSet.copyOf(levels));
    }
}
