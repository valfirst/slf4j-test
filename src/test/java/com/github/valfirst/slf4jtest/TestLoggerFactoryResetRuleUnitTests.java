package com.github.valfirst.slf4jtest;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import uk.org.lidalia.slf4jext.Level;

import static com.github.valfirst.slf4jtest.LoggingEvent.info;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.org.lidalia.slf4jext.Level.DEBUG;
import static uk.org.lidalia.slf4jext.Level.INFO;

public class TestLoggerFactoryResetRuleUnitTests {

    private TestLoggerFactoryResetRule resetRule = new TestLoggerFactoryResetRule();

    @Test
    public void resetsThreadLocalDataBeforeTest() throws Throwable {

        final TestLogger logger = TestLoggerFactory.getTestLogger("logger_name");
        logger.setEnabledLevels(INFO, DEBUG);
        logger.info("a message");

        resetRule.apply(new Statement() {
            @Override
            public void evaluate() {
                assertThat(TestLoggerFactory.getLoggingEvents(), is(Collections.<LoggingEvent>emptyList()));
                assertThat(logger.getLoggingEvents(), is(Collections.<LoggingEvent>emptyList()));
                assertThat(logger.getEnabledLevels(), is(Level.enablableValueSet()));
            }
        }, Description.EMPTY).evaluate();
    }
    
    @Test
    public void resetsThreadLocalDataAfterTest() throws Throwable {

        final TestLogger logger = TestLoggerFactory.getTestLogger("logger_name");
        logger.setEnabledLevels(INFO, DEBUG);
        logger.info("a message");

        resetRule.apply(new Statement() {
            @Override
            public void evaluate() {
            }
        }, Description.EMPTY).evaluate();


        assertThat(TestLoggerFactory.getLoggingEvents(), is(Collections.<LoggingEvent>emptyList()));
        assertThat(logger.getLoggingEvents(), is(Collections.<LoggingEvent>emptyList()));
        assertThat(logger.getEnabledLevels(), is(Level.enablableValueSet()));
    }

    @Test
    public void resetsThreadLocalDataOnException() {

        final TestLogger logger = TestLoggerFactory.getTestLogger("logger_name");
        logger.setEnabledLevels(INFO, DEBUG);
        logger.info("a message");

        final RuntimeException toThrow = new RuntimeException();
        Exception thrown = assertThrows(Exception.class, () -> resetRule.apply(new Statement() {
            @Override
            public void evaluate() {
                throw toThrow;
            }
        }, Description.EMPTY).evaluate());

        assertThat(thrown, is(toThrow));
        assertThat(TestLoggerFactory.getLoggingEvents(), is(Collections.<LoggingEvent>emptyList()));
        assertThat(logger.getLoggingEvents(), is(Collections.<LoggingEvent>emptyList()));
        assertThat(logger.getEnabledLevels(), is(Level.enablableValueSet()));
    }

    @Test
    public void doesNotResetNonThreadLocalData() throws Throwable {

        final TestLogger logger = TestLoggerFactory.getTestLogger("logger_name");
        logger.info("a message");

        resetRule.apply(new Statement() {
            @Override
            public void evaluate() {
            }
        }, Description.EMPTY).evaluate();

        final List<LoggingEvent> loggedEvents = singletonList(info("a message"));

        assertThat(TestLoggerFactory.getAllLoggingEvents(), is(loggedEvents));
        assertThat(logger.getAllLoggingEvents(), is(loggedEvents));
    }

    @Before @After
    public void resetTestLoggerFactory() {
        TestLoggerFactory.reset();
    }
}
