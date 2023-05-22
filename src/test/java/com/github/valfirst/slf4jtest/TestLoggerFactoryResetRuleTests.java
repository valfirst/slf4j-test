package com.github.valfirst.slf4jtest;

import static com.github.valfirst.slf4jtest.LoggingEvent.info;
import static com.github.valfirst.slf4jtest.TestLoggerFactory.getLoggingEvents;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.slf4j.LoggerFactory;

public class TestLoggerFactoryResetRuleTests {

    @Rule public TestRule resetLoggingEvents = new TestLoggerFactoryResetRule();

    @Test
    public void logOnce() {
        LoggerFactory.getLogger("logger").info("a message");
        assertThat(getLoggingEvents(), is(singletonList(info("a message"))));
    }

    @Test
    public void logAgain() {
        LoggerFactory.getLogger("logger").info("a message");
        assertThat(getLoggingEvents(), is(singletonList(info("a message"))));
    }
}
