package uk.org.lidalia.slf4jtest;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.org.lidalia.slf4jtest.LoggingEvent.info;
import static uk.org.lidalia.slf4jtest.TestLoggerFactory.getLoggingEvents;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import uk.org.lidalia.slf4jext.LoggerFactory;

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
