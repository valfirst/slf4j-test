package com.github.valfirst.slf4jtest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.DefaultLoggingEvent;
import org.slf4j.event.KeyValuePair;
import org.slf4j.event.Level;
import org.slf4j.event.LoggingEvent;
import org.slf4j.helpers.CheckReturnValue;
import org.slf4j.spi.DefaultLoggingEventBuilder;
import org.slf4j.spi.LoggingEventBuilder;

/**
 * A {@link LoggingEventBuilder} which changes the following compared to {@link
 * DefaultLoggingEventBuilder}:
 *
 * <ul>
 *   <li>The {@link #toLoggingEvent} method is added to build the event without logging it.
 *   <li>The return type of the fluent methods is {@link TestLoggingEventBuilder}. This allows
 *       {@link #toLoggingEvent} to be used in a fluent manner.
 *   <li>The {@link KeyValuePair} implementation overrides the {@link Object#equals} and {@link
 *       Object#hashCode} methods. This allows tests for equality in test assertions.
 * </ul>
 */
public class TestLoggingEventBuilder extends DefaultLoggingEventBuilder {

    public TestLoggingEventBuilder(Logger logger, Level level) {
        super(logger, level);
        loggingEvent = new TestLoggingEvent(level, logger);
    }

    /** Build the event, without logging it. */
    public LoggingEvent toLoggingEvent() {
        return loggingEvent;
    }

    static class TestLoggingEvent extends DefaultLoggingEvent {

        private List<KeyValuePair> keyValuePairs = null;

        public TestLoggingEvent(Level level, Logger logger) {
            super(level, logger);
        }

        @Override
        public void addKeyValue(String key, Object value) {
            if (keyValuePairs == null) keyValuePairs = new ArrayList<>();
            keyValuePairs.add(new TestKeyValuePair(key, value));
        }

        @Override
        public List<KeyValuePair> getKeyValuePairs() {
            return keyValuePairs;
        }
    }

    /**
     * Extension of {@link KeyValuePair} with overridden {@link Object#equals} and {@link
     * Object#hashCode} methods. This class must be used on the left hand side of {@link
     * Object#equals} instead of {@link KeyValuePair}. {@link KeyValuePair} can be used on the right
     * hand side.
     */
    public static class TestKeyValuePair extends KeyValuePair {
        public TestKeyValuePair(String key, Object value) {
            super(key, value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof KeyValuePair)) return false;
            KeyValuePair that = (KeyValuePair) o;
            return Arrays.deepEquals(new Object[] {key, value}, new Object[] {that.key, that.value});
        }

        @Override
        public int hashCode() {
            return Arrays.deepHashCode(new Object[] {key, value});
        }
    }

    @CheckReturnValue
    @Override
    @SuppressWarnings("unchecked")
    public TestLoggingEventBuilder setCause(Throwable cause) {
        return (TestLoggingEventBuilder) super.setCause(cause);
    }

    @CheckReturnValue
    @Override
    public TestLoggingEventBuilder addMarker(Marker marker) {
        return (TestLoggingEventBuilder) super.addMarker(marker);
    }

    @CheckReturnValue
    @Override
    @SuppressWarnings("unchecked")
    public TestLoggingEventBuilder addArgument(Object p) {
        return (TestLoggingEventBuilder) super.addArgument(p);
    }

    @CheckReturnValue
    @Override
    @SuppressWarnings("unchecked")
    public TestLoggingEventBuilder addArgument(Supplier<?> objectSupplier) {
        return (TestLoggingEventBuilder) super.addArgument(objectSupplier);
    }

    @CheckReturnValue
    @Override
    @SuppressWarnings("unchecked")
    public TestLoggingEventBuilder addKeyValue(String key, Object value) {
        return (TestLoggingEventBuilder) super.addKeyValue(key, value);
    }

    @CheckReturnValue
    @Override
    @SuppressWarnings("unchecked")
    public TestLoggingEventBuilder addKeyValue(String key, Supplier<Object> valueSupplier) {
        return (TestLoggingEventBuilder) super.addKeyValue(key, valueSupplier);
    }

    @CheckReturnValue
    @Override
    @SuppressWarnings("unchecked")
    public TestLoggingEventBuilder setMessage(String message) {
        return (TestLoggingEventBuilder) super.setMessage(message);
    }

    @CheckReturnValue
    @Override
    @SuppressWarnings("unchecked")
    public TestLoggingEventBuilder setMessage(Supplier<String> messageSupplier) {
        return (TestLoggingEventBuilder) super.setMessage(messageSupplier);
    }
}
