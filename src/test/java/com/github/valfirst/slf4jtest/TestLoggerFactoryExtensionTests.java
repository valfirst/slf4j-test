package com.github.valfirst.slf4jtest;

import static com.github.valfirst.slf4jtest.LoggingEvent.info;
import static com.github.valfirst.slf4jtest.TestLoggerFactory.getLoggingEvents;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.lidalia.slf4jext.LoggerFactory;

@ExtendWith(TestLoggerFactoryExtension.class)
class TestLoggerFactoryExtensionTests {

    @Test
    void logOnce() {
        LoggerFactory.getLogger("logger").info("a message");
        assertThat(getLoggingEvents(), is(singletonList(info("a message"))));
    }

    @Test
    void logAgain() {
        LoggerFactory.getLogger("logger").info("a message");
        assertThat(getLoggingEvents(), is(singletonList(info("a message"))));
    }
}
