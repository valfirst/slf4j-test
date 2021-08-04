package com.github.valfirst.slf4jtest;

import uk.org.lidalia.slf4jext.Level;

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
        if (!actual.getLoggingEvents().contains(event)) {
            failWithMessage("Failed to find %s", event);
        }
        return this;
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
        if (actual.getLoggingEvents().contains(event)) {
            failWithMessage("Found %s, even though we expected not to", event);
        }
        return this;
    }

    /**
    * Convenience method for a {@link LevelAssert} from a provided test logger.
    *
    * @param level the {@link Level} to assert against
    * @return the {@link LevelAssert} bound to the given {@link Level}
    */
    public LevelAssert hasLevel(Level level) {
        return new LevelAssert(actual, level);
    }
}
