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
}
