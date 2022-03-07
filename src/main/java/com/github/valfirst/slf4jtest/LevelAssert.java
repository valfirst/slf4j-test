package com.github.valfirst.slf4jtest;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import uk.org.lidalia.slf4jext.Level;

/**
 * A set of assertions to validate that logs have been logged to a {@link TestLogger}, for a
 * specific log level.
 */
public class LevelAssert extends AbstractTestLoggerAssert<LevelAssert> {

    private static Predicate<LoggingEvent> messageWithSubstring(String substring) {
        return event -> event.getMessage().contains(substring);
    }

    private static Predicate<LoggingEvent> messageForPattern(String regex) {
        return event -> event.getMessage().matches(regex);
    }

    private final Level level;

    public LevelAssert(TestLogger logger, Level level) {
        super(logger, LevelAssert.class);

        this.level = level;
    }

    /**
     * Assert that the given log level has the expected number of logs, regardless of content.
     *
     * @param expected the number of logs expected at this level
     * @return a {@link LevelAssert} for chaining
     */
    public LevelAssert hasNumberOfLogs(int expected) {
        long count = getLogCount(level, ignored -> true);
        if (count != expected) {
            failWithMessage(
                    "Expected level %s to have %d log messages available, but %d were found",
                    level, expected, count);
        }

        return this;
    }

    /**
     * Assert that the given log level includes a log message that contains a substring.
     *
     * @param substring a substring of a log message that should be present
     * @return a {@link LevelAssert} for chaining
     */
    public LevelAssert hasMessageContaining(String substring) {
        long count = getLogCount(level, messageWithSubstring(substring));
        if (count == 0) {
            failWithMessage(
                    "Expected level %s to contain a log message containing `%s`, but it did not.\n\nLog messages found:%n%s",
                    level, substring, eventsToLogMessage(level));
        }

        return this;
    }

    /**
     * Assert that the given log level includes a log message that matches a regex.
     *
     * @param regex the regular expression to which this string is to be matched
     * @return a {@link LevelAssert} for chaining
     */
    public LevelAssert hasMessageMatching(String regex) {
        long count = getLogCount(level, messageForPattern(regex));
        if (count == 0) {
            failWithMessage(
                    "Expected level %s to contain a log message matching regex `%s`, but it did not.\n\nLog messages found:%n%s",
                    level, regex, eventsToLogMessage());
        }

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LevelAssert that = (LevelAssert) o;
        return level == that.level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), level);
    }

    private String eventsToLogMessage() {
        return loggingEventsSupplier.get().stream()
                .map(e -> "- " + e)
                .collect(Collectors.joining("\n"));
    }

    private String eventsToLogMessage(Level level) {
        return loggingEventsSupplier.get().stream()
                .filter(event -> level.equals(event.getLevel()))
                .map(e -> "- " + e)
                .collect(Collectors.joining("\n"));
    }
}
