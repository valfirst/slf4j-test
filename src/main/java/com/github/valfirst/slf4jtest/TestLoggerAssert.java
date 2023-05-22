package com.github.valfirst.slf4jtest;

import com.google.common.collect.ObjectArrays;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import org.slf4j.Marker;
import org.slf4j.event.Level;

/**
 * A set of assertions to validate that logs have been logged to a {@link TestLogger}.
 *
 * <p>Should be thread safe, as this uses <code>testLogger.getLoggingEvents()</code> by default. The
 * assertion mode can be switched to use <code>testLogger.getAllLoggingEvents()</code> by calling
 * {@link #anyThread()}.
 */
public class TestLoggerAssert extends AbstractTestLoggerAssert<TestLoggerAssert> {

    private MdcComparator mdcComparator = MdcComparator.EXACT;

    protected TestLoggerAssert(TestLogger testLogger) {
        super(testLogger, TestLoggerAssert.class);
    }

    /**
     * Changes the assertion mode to verify that log messages have been logged regardless of which
     * thread actually logged the message.
     *
     * @return a {@link TestLoggerAssert} for chaining
     */
    public TestLoggerAssert anyThread() {
        loggingEventsSupplier = actual::getAllLoggingEvents;
        return this;
    }

    /**
     * Allows the comparison strategy for verifying the MDC contents to be configured.
     *
     * <p>The default behaviour is to verify the contents of the MDC are exactly equal to those on the
     * logging event.
     *
     * @param mdcComparator the comparator to use when verifying the contents of the MDC captured by
     *     the logging event
     * @return a {@link TestLoggerAssert} for chaining
     * @see MdcComparator
     */
    public TestLoggerAssert usingMdcComparator(MdcComparator mdcComparator) {
        this.mdcComparator = mdcComparator;
        return this;
    }

    /**
     * Verify that a log message, at a specific level, has been logged by the test logger.
     *
     * @param level the level of the log message to look for
     * @param message the expected message
     * @param arguments any optional arguments that may be provided to the log message
     * @return a {@link TestLoggerAssert} for chaining
     */
    public TestLoggerAssert hasLogged(Level level, String message, Object... arguments) {
        return hasLogged(event(level, message, arguments));
    }

    /**
     * Verify that a log message, at a specific level, has been logged by the test logger in the
     * presence of a {@link Throwable}.
     *
     * @param throwable the throwable that is attached to the log message
     * @param level the level of the log message to look for
     * @param message the expected message
     * @param arguments any optional arguments that may be provided to the log message
     * @return a {@link TestLoggerAssert} for chaining
     */
    public TestLoggerAssert hasLogged(
            Throwable throwable, Level level, String message, Object... arguments) {
        return hasLogged(event(throwable, level, message, arguments));
    }

    /**
     * Verify that a {@link LoggingEvent} has been logged by the test logger.
     *
     * @param event the event to verify presence of
     * @return a {@link TestLoggerAssert} for chaining
     */
    public TestLoggerAssert hasLogged(LoggingEvent event) {
        return hasLogged(buildPredicate(event), "Failed to find event:%n  %s", event);
    }

    /**
     * Verify that a {@link LoggingEvent} satisfying the provided predicate has been logged by the
     * test logger.
     *
     * @param predicate the predicate to test against
     * @return a {@link TestLoggerAssert} for chaining
     */
    public TestLoggerAssert hasLogged(Predicate<LoggingEvent> predicate) {
        return hasLogged(predicate, "Failed to find log matching predicate");
    }

    /**
     * Uses the supplied {@link PredicateBuilder} to construct the predicate with which to Verify that
     * a matching {@link LoggingEvent} has been logged by the test logger.
     *
     * @param predicate the {@link PredicateBuilder} to use to construct the test predicate
     * @return a {@link TestLoggerAssert} for chaining
     */
    public TestLoggerAssert hasLogged(PredicateBuilder predicate) {
        return hasLogged(predicate.build());
    }

    /**
     * Verify that a log message, at a specific level, has not been logged by the test logger.
     *
     * @param level the level of the log message to look for
     * @param message the expected message
     * @param arguments any optional arguments that may be provided to the log message
     * @return a {@link TestLoggerAssert} for chaining
     */
    public TestLoggerAssert hasNotLogged(Level level, String message, Object... arguments) {
        return hasNotLogged(event(level, message, arguments));
    }

    /**
     * Verify that a log message, at a specific level, has not been logged by the test logger in the
     * presence of a {@link Throwable}.
     *
     * @param throwable the throwable that is attached to the log message
     * @param level the level of the log message to look for
     * @param message the expected message
     * @param arguments any optional arguments that may be provided to the log message
     * @return a {@link TestLoggerAssert} for chaining
     */
    public TestLoggerAssert hasNotLogged(
            Throwable throwable, Level level, String message, Object... arguments) {
        return hasNotLogged(event(throwable, level, message, arguments));
    }

    /**
     * Verify that a {@link LoggingEvent} has not been logged by the test logger.
     *
     * @param event the event to verify absence of
     * @return a {@link TestLoggerAssert} for chaining
     */
    public TestLoggerAssert hasNotLogged(LoggingEvent event) {
        return hasNotLogged(buildPredicate(event));
    }

    /**
     * Verify that a {@link LoggingEvent} satisfying the provided predicate has _not_ been logged by
     * the test logger.
     *
     * @param predicate the predicate to test against
     * @return a {@link TestLoggerAssert} for chaining
     */
    public TestLoggerAssert hasNotLogged(Predicate<LoggingEvent> predicate) {
        findEvent(predicate)
                .ifPresent(
                        loggingEvent ->
                                failWithMessage("Found %s, even though we expected not to", loggingEvent));

        return this;
    }

    /**
     * Uses the supplied {@link PredicateBuilder} to construct the predicate with which to Verify that
     * a matching {@link LoggingEvent} has _not_ been logged by the test logger.
     *
     * @param predicate the {@link PredicateBuilder} to use to construct the test predicate
     * @return a {@link TestLoggerAssert} for chaining
     */
    public TestLoggerAssert hasNotLogged(PredicateBuilder predicate) {
        findEvent(predicate.build())
                .ifPresent(
                        loggingEvent ->
                                failWithMessage("Found %s, even though we expected not to", loggingEvent));

        return this;
    }

    /**
     * Convenience method for a {@link LevelAssert} from a provided test logger.
     *
     * @param level the {@link Level} to assert against
     * @return the {@link LevelAssert} bound to the given {@link Level}
     */
    public LevelAssert hasLevel(Level level) {
        LevelAssert levelAssert = new LevelAssert(actual, level);
        levelAssert.loggingEventsSupplier = loggingEventsSupplier;
        return levelAssert;
    }

    private TestLoggerAssert hasLogged(
            Predicate<LoggingEvent> predicate, String failureMessage, Object... arguments) {
        if (!findEvent(predicate).isPresent()) {
            String allEvents =
                    loggingEventsSupplier.get().stream()
                            .map(Objects::toString)
                            .reduce((first, second) -> first + "\n  - " + second)
                            .map(output -> "  - " + output)
                            .orElse("  <none>");
            Object[] newArguments = ObjectArrays.concat(arguments, allEvents);
            failWithMessage(
                    failureMessage + "%n%nThe logger contained the following events:%n%s", newArguments);
        }
        return this;
    }

    private Predicate<LoggingEvent> buildPredicate(LoggingEvent event) {
        return new PredicateBuilder()
                .withMarker(event.getMarker().orElse(null))
                .withThrowable(event.getThrowable().orElse(null))
                .withLevel(event.getLevel())
                .withMessage(event.getMessage())
                .withArguments(event.getArguments().toArray())
                .withMdc(event.getMdc(), mdcComparator)
                .build();
    }

    private Optional<LoggingEvent> findEvent(Predicate<LoggingEvent> predicate) {
        return loggingEventsSupplier.get().stream().filter(predicate).findFirst();
    }

    public static class PredicateBuilder {

        private static final Predicate<LoggingEvent> IGNORE_PREDICATE = event -> true;

        private Predicate<LoggingEvent> messagePredicate = IGNORE_PREDICATE;
        private Predicate<LoggingEvent> argumentsPredicate = IGNORE_PREDICATE;
        private Predicate<LoggingEvent> markerPredicate = IGNORE_PREDICATE;
        private Predicate<LoggingEvent> mdcPredicate = IGNORE_PREDICATE;
        private Predicate<LoggingEvent> throwablePredicate = IGNORE_PREDICATE;
        private Predicate<LoggingEvent> levelPredicate = IGNORE_PREDICATE;

        public static PredicateBuilder aLog() {
            return new PredicateBuilder();
        }

        public PredicateBuilder withLevel(Level level) {
            levelPredicate = event -> event.getLevel().equals(level);
            return this;
        }

        public PredicateBuilder withMarker(Marker marker) {
            markerPredicate = event -> event.getMarker().equals(Optional.ofNullable(marker));
            return this;
        }

        public PredicateBuilder withMessage(String message) {
            return withMessage(message::equals);
        }

        public PredicateBuilder withMessage(Predicate<String> predicate) {
            this.messagePredicate = event -> predicate.test(event.getMessage());
            return this;
        }

        public PredicateBuilder withArguments(Object... arguments) {
            return withArguments(actualArgs -> actualArgs.equals(Arrays.asList(arguments)));
        }

        public PredicateBuilder withArguments(Predicate<Collection<Object>> predicate) {
            this.argumentsPredicate = event -> predicate.test(event.getArguments());
            return this;
        }

        public PredicateBuilder withThrowable(Throwable throwable) {
            return withThrowable(t -> t.equals(Optional.ofNullable(throwable)));
        }

        public PredicateBuilder withThrowable(Predicate<Optional<Throwable>> predicate) {
            this.throwablePredicate = event -> predicate.test(event.getThrowable());
            return this;
        }

        public PredicateBuilder withMdc(Map<String, String> mdc, MdcComparator comparator) {
            this.mdcPredicate = event -> comparator.compare(event.getMdc(), mdc);
            return this;
        }

        public Predicate<LoggingEvent> build() {
            return levelPredicate
                    .and(markerPredicate)
                    .and(messagePredicate)
                    .and(argumentsPredicate)
                    .and(throwablePredicate)
                    .and(mdcPredicate);
        }
    }

    public static final class MdcComparator {

        /** Disables verification of the MDC contents. */
        public static final MdcComparator IGNORING = new MdcComparator((a, b) -> true);

        /**
         * Validates the contents of the MDC on the logging event exactly match the specified values.
         */
        public static final MdcComparator EXACT =
                new MdcComparator((a, b) -> a.size() == b.size() && a.entrySet().containsAll(b.entrySet()));

        /**
         * Validates the MDC contains all specified entries, but will not fail if additional entries
         * exist.
         */
        public static final MdcComparator CONTAINING =
                new MdcComparator((a, b) -> a.entrySet().containsAll(b.entrySet()));

        private final BiFunction<Map<String, String>, Map<String, String>, Boolean> compareFunction;

        private MdcComparator(
                BiFunction<Map<String, String>, Map<String, String>, Boolean> compareFunction) {
            this.compareFunction = compareFunction;
        }

        public boolean compare(Map<String, String> actual, Map<String, String> expected) {
            return compareFunction.apply(actual, expected);
        }
    }
}
