package com.github.valfirst.slf4jtest;

import static com.github.valfirst.slf4jtest.LoggingEvent.info;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.slf4j.event.Level.DEBUG;
import static org.slf4j.event.Level.INFO;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.event.Level;

class TestLoggerFactoryExtensionUnitTests {

    private final TestLoggerFactoryExtension extension = new TestLoggerFactoryExtension();

    @Test
    void resetsThreadLocalData() {

        final TestLogger logger = TestLoggerFactory.getTestLogger("logger_name");
        logger.setEnabledLevels(INFO, DEBUG);
        logger.info("a message");

        ExtensionContext extensionContext = mock(ExtensionContext.class);
        when(extensionContext.getElement()).thenReturn(Optional.empty());
        extension.beforeTestExecution(extensionContext);

        assertThat(TestLoggerFactory.getLoggingEvents(), is(Collections.emptyList()));
        assertThat(logger.getLoggingEvents(), is(Collections.emptyList()));
        assertThat(logger.getEnabledLevels(), is(EnumSet.allOf(Level.class)));
    }

    @Test
    void doesNotResetNonThreadLocalData() {

        final TestLogger logger = TestLoggerFactory.getTestLogger("logger_name");
        logger.info("a message");

        ExtensionContext extensionContext = mock(ExtensionContext.class);
        when(extensionContext.getElement()).thenReturn(Optional.empty());
        extension.beforeTestExecution(extensionContext);

        final List<LoggingEvent> loggedEvents = singletonList(info("a message"));

        assertThat(TestLoggerFactory.getAllLoggingEvents(), is(loggedEvents));
        assertThat(logger.getAllLoggingEvents(), is(loggedEvents));
    }

    @BeforeEach
    @AfterEach
    void resetTestLoggerFactory() {
        TestLoggerFactory.reset();
    }
}
