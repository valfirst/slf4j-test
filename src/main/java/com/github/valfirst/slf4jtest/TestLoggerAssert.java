package com.github.valfirst.slf4jtest;

import uk.org.lidalia.slf4jext.Level;

import java.util.Optional;

/**
 * A set of assertions to validate that logs have been logged to a {@link TestLogger}.
 *
 * <p>Should be thread safe, as this uses <code>testLogger.getLoggingEvents()</code>.
 */
public class TestLoggerAssert extends AbstractTestLoggerAssert<TestLoggerAssert> {

    protected TestLoggerAssert(TestLogger testLogger) {
        super(testLogger, TestLoggerAssert.class);
    }

    /**
     * Verify that a log message, at a specific level, has been logged by the test logger.
     *
     * @param level     the level of the log message to look for
     * @param message   the expected message
     * @param arguments any optional arguments that may be provided to the log message
     * @return a {@link TestLoggerAssert} for chaining
     */
    public TestLoggerAssert hasLogged(Level level, String message, Object... arguments) {
        long count = getLogCount(level, logWithMessage(message, arguments));
        if (count == 0) {
            if (arguments.length == 0) {
                failWithMessage("Failed to find %s log with message `%s`", level, message);
            } else {
                failWithMessage("Failed to find %s log with message `%s` (with arguments)", level, message);
            }
        }
        return this;
    }

    /**
     * Verify that a log message, at a specific level, has been logged by the test logger in the presence of a {@link Throwable}.
     *
     * @param throwable the throwable that is attached to the log message
     * @param level     the level of the log message to look for
     * @param message   the expected message
     * @param arguments any optional arguments that may be provided to the log message
     * @return a {@link TestLoggerAssert} for chaining
     */
    public TestLoggerAssert hasLogged(Throwable throwable, Level level, String message, Object... arguments) {
        long count = getLogCount(level, logWithMessage(message, Optional.of(throwable), arguments));
        if (count == 0) {
            if (arguments.length == 0) {
                failWithMessage("Failed to find %s log message with message `%s` (with throwable)", level, message);
            } else {
                failWithMessage("Failed to find %s log message with message `%s` (with throwable and arguments)", level, message);
            }
        }
        return this;
    }

    /**
     * Verify that a log message, at a specific level, has not been logged by the test logger.
     *
     * @param level     the level of the log message to look for
     * @param message   the expected message
     * @param arguments any optional arguments that may be provided to the log message
     * @return a {@link TestLoggerAssert} for chaining
     */

    public TestLoggerAssert hasNotLogged(Level level, String message, Object... arguments) {
        long count = getLogCount(level, logWithMessage(message, arguments));
        if (count != 0) {
            if (arguments.length == 0) {
                failWithMessage("Found %s log with message `%s`, even though we expected not to", level, message);
            } else {
                failWithMessage("Found %s log with message `%s` (with arguments), even though we expected not to", level, message);
            }
        }
        return this;
    }

    /**
     * Verify that a log message, at a specific level, has not been logged by the test logger in the presence of a {@link Throwable}.
     *
     * @param throwable the throwable that is attached to the log message
     * @param level     the level of the log message to look for
     * @param message   the expected message
     * @param arguments any optional arguments that may be provided to the log message
     * @return a {@link TestLoggerAssert} for chaining
     */
    public TestLoggerAssert hasNotLogged(Throwable throwable, Level level, String message, Object... arguments) {
        long count = getLogCount(level, logWithMessage(message, Optional.of(throwable), arguments));
        if (count != 0) {
            if (arguments.length == 0) {
                failWithMessage("Found %s log with message `%s` (with throwable), even though we expected not to", level, message);
            } else {
                failWithMessage("Found %s log with message `%s` (with throwable and arguments), even though we expected not to", level, message);
            }
        }
        return this;
    }

    public LevelAssert hasLevel(Level level) {
        return new LevelAssert(actual, level);
    }
}
