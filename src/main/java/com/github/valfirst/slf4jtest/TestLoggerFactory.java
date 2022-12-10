package com.github.valfirst.slf4jtest;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import org.slf4j.ILoggerFactory;
import uk.org.lidalia.lang.ThreadLocal;
import uk.org.lidalia.slf4jext.Level;

public final class TestLoggerFactory implements ILoggerFactory {

    private static final Supplier<TestLoggerFactory> INSTANCE =
            Suppliers.memoize(
                    () -> {
                        try {
                            OverridableProperties properties = new OverridableProperties("slf4jtest");
                            Level printLevel = getLevelProperty(properties, "print.level", "OFF");
                            Level captureLevel = getLevelProperty(properties, "capture.level", "TRACE");
                            return new TestLoggerFactory(printLevel, captureLevel);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });

    private static Level getLevelProperty(
            OverridableProperties properties, String propertyKey, String defaultValue)
            throws IOException {
        try {
            final String printLevelProperty = properties.getProperty(propertyKey, defaultValue);
            return Level.valueOf(printLevelProperty);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                    "Invalid level name in property "
                            + propertyKey
                            + " of file slf4jtest.properties "
                            + "or System property slf4jtest."
                            + propertyKey,
                    e);
        }
    }

    private final ConcurrentMap<String, TestLogger> loggers = new ConcurrentHashMap<>();
    private final List<LoggingEvent> allLoggingEvents =
            Collections.synchronizedList(new ArrayList<>());
    private final ThreadLocal<List<LoggingEvent>> loggingEvents = new ThreadLocal<>(ArrayList::new);
    private volatile Level printLevel;
    private volatile Level captureLevel;

    public static TestLoggerFactory getInstance() {
        return INSTANCE.get();
    }

    public static TestLogger getTestLogger(final Class<?> aClass) {
        return getInstance().getLogger(aClass);
    }

    public static TestLogger getTestLogger(final String name) {
        return getInstance().getLogger(name);
    }

    public static Map<String, TestLogger> getAllTestLoggers() {
        return getInstance().getAllLoggers();
    }

    public static void clear() {
        getInstance().clearLoggers();
    }

    public static void clearAll() {
        getInstance().clearAllLoggers();
    }

    static void reset() {
        getInstance().doReset();
    }

    public static List<LoggingEvent> getLoggingEvents() {
        return getInstance().getLoggingEventsFromLoggers();
    }

    public static List<LoggingEvent> getAllLoggingEvents() {
        return getInstance().getAllLoggingEventsFromLoggers();
    }

    public TestLoggerFactory() {
        this(Level.OFF, Level.TRACE);
    }

    public TestLoggerFactory(final Level printLevel) {
        this(printLevel, Level.TRACE);
    }

    public TestLoggerFactory(final Level printLevel, final Level captureLevel) {
        this.printLevel = checkNotNull(printLevel);
        this.captureLevel = checkNotNull(captureLevel);
    }

    public Level getPrintLevel() {
        return printLevel;
    }

    public Level getCaptureLevel() {
        return captureLevel;
    }

    public ImmutableMap<String, TestLogger> getAllLoggers() {
        return ImmutableMap.copyOf(loggers);
    }

    public TestLogger getLogger(final Class<?> aClass) {
        return getLogger(aClass.getName());
    }

    public TestLogger getLogger(final String name) {
        final TestLogger newLogger = new TestLogger(name, this);
        return Optional.ofNullable(loggers.putIfAbsent(name, newLogger)).orElse(newLogger);
    }

    public void clearLoggers() {
        for (final TestLogger testLogger : loggers.values()) {
            testLogger.clear();
        }
        loggingEvents.get().clear();
    }

    public void clearAllLoggers() {
        for (final TestLogger testLogger : loggers.values()) {
            testLogger.clearAll();
        }
        loggingEvents.reset();
        allLoggingEvents.clear();
    }

    void doReset() {
        clearAllLoggers();
        loggers.clear();
    }

    public ImmutableList<LoggingEvent> getLoggingEventsFromLoggers() {
        return ImmutableList.copyOf(loggingEvents.get());
    }

    public List<LoggingEvent> getAllLoggingEventsFromLoggers() {
        return allLoggingEvents;
    }

    void addLoggingEvent(final LoggingEvent event) {
        loggingEvents.get().add(event);
        allLoggingEvents.add(event);
    }

    public void setPrintLevel(final Level printLevel) {
        this.printLevel = checkNotNull(printLevel);
    }

    public void setCaptureLevel(Level captureLevel) {
        this.captureLevel = checkNotNull(captureLevel);
    }
}
