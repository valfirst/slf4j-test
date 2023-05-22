package com.github.valfirst.slf4jtest;

public class Assertions {
    public static TestLoggerAssert assertThat(TestLogger testLogger) {
        return new TestLoggerAssert(testLogger);
    }

    private Assertions() {}
}
