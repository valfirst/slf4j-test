package com.github.valfirst.slf4jtest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import uk.org.lidalia.slf4jext.LoggerFactory;

import static com.github.valfirst.slf4jtest.LoggingEvent.info;
import static com.github.valfirst.slf4jtest.TestLoggerFactory.getLoggingEvents;
import static java.util.Collections.singletonList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@ExtendWith(TestLoggerFactoryExtension.class)
public class TestLoggerFactoryExtensionTests {

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
