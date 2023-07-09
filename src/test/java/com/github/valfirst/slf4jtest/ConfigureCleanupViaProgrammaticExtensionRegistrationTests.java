package com.github.valfirst.slf4jtest;

import static com.github.valfirst.slf4jtest.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junitpioneer.jupiter.Issue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Issue("183")
class ConfigureCleanupViaProgrammaticExtensionRegistrationTests {

    private static final TestLogger TEST_LOGGER =
            TestLoggerFactory.getTestLogger(ClassUnderTest.class);

    @RegisterExtension
    static TestLoggerFactoryExtension extension =
            new TestLoggerFactoryExtension(CleanupStage.BEFORE_EACH);

    @Nested
    class Something {
        @BeforeEach
        void setup() {
            ClassUnderTest.doTheThing();
        }

        @Test
        void info() {
            assertThat(TEST_LOGGER).hasLogged(LoggingEvent.info("The message"));
        }

        @Test
        void error() {
            assertThat(TEST_LOGGER).hasLogged(LoggingEvent.error("Oh no!"));
        }
    }

    public static class ClassUnderTest {
        private static final Logger LOGGER = LoggerFactory.getLogger(ClassUnderTest.class);

        public static void doTheThing() {
            LOGGER.info("The message");
            LOGGER.error("Oh no!");
        }
    }
}
