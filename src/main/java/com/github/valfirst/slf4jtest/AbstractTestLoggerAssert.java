package com.github.valfirst.slf4jtest;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;
import org.assertj.core.api.AbstractAssert;
import uk.org.lidalia.slf4jext.Level;

abstract class AbstractTestLoggerAssert<C extends AbstractAssert<C, TestLogger>>
        extends AbstractAssert<C, TestLogger> {
    protected AbstractTestLoggerAssert(TestLogger testLogger, Class clazz) {
        super(testLogger, clazz);
    }

    protected long getLogCount(Level level, Predicate<LoggingEvent> predicate) {
        return actual.getLoggingEvents().stream()
                .filter(event -> level.equals(event.getLevel()) && predicate.test(event))
                .count();
    }

    protected static Predicate<LoggingEvent> logWithMessage(String message, Object... arguments) {
        return logWithMessage(message, Optional.empty(), arguments);
    }

    protected static Predicate<LoggingEvent> logWithMessage(
            String message,
            @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<Throwable> maybeThrowable,
            Object... arguments) {
        return event ->
                message.equals(event.getMessage())
                        && event.getArguments().equals(Arrays.asList(arguments))
                        && event.getThrowable().equals(maybeThrowable);
    }

    protected static LoggingEvent event(Level level, String message, Object... arguments) {
        switch (level) {
            case WARN:
                return LoggingEvent.warn(message, arguments);
            case ERROR:
                return LoggingEvent.error(message, arguments);
            case INFO:
                return LoggingEvent.info(message, arguments);
            case DEBUG:
                return LoggingEvent.debug(message, arguments);
            case TRACE:
                return LoggingEvent.trace(message, arguments);
            default:
                throw new IllegalStateException("Unmatched level " + level + " provided");
        }
    }

    protected static LoggingEvent event(
            Throwable throwable, Level level, String message, Object... arguments) {
        switch (level) {
            case WARN:
                return LoggingEvent.warn(throwable, message, arguments);
            case ERROR:
                return LoggingEvent.error(throwable, message, arguments);
            case INFO:
                return LoggingEvent.info(throwable, message, arguments);
            case DEBUG:
                return LoggingEvent.debug(throwable, message, arguments);
            case TRACE:
                return LoggingEvent.trace(throwable, message, arguments);
            default:
                throw new IllegalStateException("Unmatched level " + level + " provided");
        }
    }
}
