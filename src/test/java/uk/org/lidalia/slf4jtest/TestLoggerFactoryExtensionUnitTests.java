package uk.org.lidalia.slf4jtest;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.org.lidalia.slf4jext.Level.DEBUG;
import static uk.org.lidalia.slf4jext.Level.INFO;
import static uk.org.lidalia.slf4jtest.LoggingEvent.info;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.org.lidalia.slf4jext.Level;

public class TestLoggerFactoryExtensionUnitTests {

    private TestLoggerFactoryExtension extension = new TestLoggerFactoryExtension();

    @Test
    public void resetsThreadLocalData() {

        final TestLogger logger = TestLoggerFactory.getTestLogger("logger_name");
        logger.setEnabledLevels(INFO, DEBUG);
        logger.info("a message");

        extension.beforeTestExecution(null);

        assertThat(TestLoggerFactory.getLoggingEvents(), is(Collections.emptyList()));
        assertThat(logger.getLoggingEvents(), is(Collections.emptyList()));
        assertThat(logger.getEnabledLevels(), is(Level.enablableValueSet()));
    }

    @Test
    public void doesNotResetNonThreadLocalData() {

        final TestLogger logger = TestLoggerFactory.getTestLogger("logger_name");
        logger.info("a message");

        extension.beforeTestExecution(null);

        final List<LoggingEvent> loggedEvents = singletonList(info("a message"));

        assertThat(TestLoggerFactory.getAllLoggingEvents(), is(loggedEvents));
        assertThat(logger.getAllLoggingEvents(), is(loggedEvents));
    }

    @BeforeEach
    @AfterEach
    public void resetTestLoggerFactory() {
        TestLoggerFactory.reset();
    }
}
