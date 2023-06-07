package com.github.valfirst.slf4jtest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.ILoggerFactory;
import org.slf4j.event.Level;
import uk.org.lidalia.lang.ThreadLocal;

public final class TestLoggerFactory implements ILoggerFactory {

    private static volatile TestLoggerFactory INSTANCE = null;

    private static Level getLevelProperty(
            OverridableProperties properties, String propertyKey, String defaultValue) {
        try {
            final String printLevelProperty = properties.getProperty(propertyKey, defaultValue);
            if ("OFF".equals(printLevelProperty)) return null;
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
        if (INSTANCE == null) {
            synchronized (TestLoggerFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = createInstance(OverridableProperties.createUnchecked("slf4jtest"));
                }
            }
        }
        return INSTANCE;
    }

    static TestLoggerFactory createInstance(OverridableProperties properties) {
        Level printLevel = getLevelProperty(properties, "print.level", "OFF");
        Level captureLevel = getLevelProperty(properties, "capture.level", "TRACE");
        return new TestLoggerFactory(printLevel, captureLevel);
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
        this(null, Level.TRACE);
    }

    public TestLoggerFactory(final Level printLevel) {
        this(printLevel, Level.TRACE);
    }

    public TestLoggerFactory(final Level printLevel, final Level captureLevel) {
        this.printLevel = printLevel;
        this.captureLevel = captureLevel;
    }

    public Level getPrintLevel() {
        return printLevel;
    }

    public Level getCaptureLevel() {
        return captureLevel;
    }

    public Map<String, TestLogger> getAllLoggers() {
        return Collections.unmodifiableMap(new HashMap<>(loggers));
    }

    public TestLogger getLogger(final Class<?> aClass) {
        return getLogger(aClass.getName());
    }

    public TestLogger getLogger(final String name) {
        return loggers.computeIfAbsent(name, nm -> new TestLogger(nm, this));
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

    public List<LoggingEvent> getLoggingEventsFromLoggers() {
        return Collections.unmodifiableList(new ArrayList<>(loggingEvents.get()));
    }

    public List<LoggingEvent> getAllLoggingEventsFromLoggers() {
        return allLoggingEvents;
    }

    void addLoggingEvent(final LoggingEvent event) {
        loggingEvents.get().add(event);
        allLoggingEvents.add(event);
    }

    public void setPrintLevel(final Level printLevel) {
        this.printLevel = printLevel;
    }

    public void setCaptureLevel(Level captureLevel) {
        this.captureLevel = captureLevel;
    }
}
