package com.github.valfirst.slf4jtest;

import uk.org.lidalia.slf4jext.Level;

import java.util.Objects;

/**
 * A set of assertions to validate that logs have been logged to a {@link TestLogger}, for a specific log level.
 */
public class LevelAssert extends AbstractTestLoggerAssert<LevelAssert> {
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
            failWithMessage("Expected level %s to have %d log messages available, but %d were found", level, expected, count);
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
}
