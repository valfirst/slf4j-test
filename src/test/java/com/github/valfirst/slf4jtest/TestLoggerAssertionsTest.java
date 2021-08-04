package com.github.valfirst.slf4jtest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.org.lidalia.slf4jext.Level;

@ExtendWith(MockitoExtension.class)
class TestLoggerAssertionsTest {

    @Mock private TestLogger logger;

    private TestLoggerAssert assertions;

    @BeforeEach
    void setup() {
        assertions = new TestLoggerAssert(logger);
    }

    @Nested
    class HasLogged {

        @Nested
        class LogMessage extends TestCase {

            @Override
            TestLoggerAssert performAssert(Level level, String message, Object... arguments) {
                return assertions.hasLogged(level, message, arguments);
            }

            @Override
            TestLoggerAssert performAssert(
                    Throwable throwable, Level level, String message, Object... arguments) {
                return assertions.hasLogged(throwable, level, message, arguments);
            }
        }

        abstract class TestCase {
            abstract TestLoggerAssert performAssert(Level level, String message, Object... arguments);

            abstract TestLoggerAssert performAssert(
                    Throwable throwable, Level level, String message, Object... arguments);

            @Nested
            class WithoutThrowable {
                @Test
                void failsWhenLogMessageIsNotFound() {
                    when(logger.getLoggingEvents()).thenReturn(ImmutableList.of());

                    assertThatThrownBy(() -> performAssert(Level.WARN, "Something may be wrong"))
                            .isInstanceOf(AssertionError.class)
                            .hasMessage("Failed to find WARN log with message `Something may be wrong`");
                }

                @Test
                void failsWhenExpectingMoreArgumentsThanExists() {
                    when(logger.getLoggingEvents())
                            .thenReturn(ImmutableList.of(LoggingEvent.warn("Something may be wrong")));

                    assertThatThrownBy(
                                    () -> performAssert(Level.WARN, "Something may be wrong", "Extra context"))
                            .isInstanceOf(AssertionError.class)
                            .hasMessage(
                                    "Failed to find WARN log with message `Something may be wrong` (with arguments)");
                }

                @Test
                void failsWhenActuallyMoreArgumentsThanExpected() {
                    when(logger.getLoggingEvents())
                            .thenReturn(
                                    ImmutableList.of(
                                            LoggingEvent.warn("Something may be wrong", "Extra context", "Another")));

                    assertThatThrownBy(
                                    () -> performAssert(Level.WARN, "Something may be wrong", "Extra context"))
                            .isInstanceOf(AssertionError.class)
                            .hasMessage(
                                    "Failed to find WARN log with message `Something may be wrong` (with arguments)");
                }

                @Test
                void failsWhenArgumentsInDifferentOrder() {
                    when(logger.getLoggingEvents())
                            .thenReturn(
                                    ImmutableList.of(
                                            LoggingEvent.warn("Something may be wrong", "Another", "Extra context")));

                    assertThatThrownBy(
                                    () ->
                                            performAssert(
                                                    Level.WARN, "Something may be wrong", "Extra context", "Another"))
                            .isInstanceOf(AssertionError.class)
                            .hasMessage(
                                    "Failed to find WARN log with message `Something may be wrong` (with arguments)");
                }

                @Test
                void passesWhenLogMessageIsFound() {
                    when(logger.getLoggingEvents())
                            .thenReturn(ImmutableList.of(LoggingEvent.warn("Something may be wrong")));

                    assertThatCode(() -> performAssert(Level.WARN, "Something may be wrong"))
                            .doesNotThrowAnyException();
                }

                @Test
                void returnsSelfWhenPasses() {
                    when(logger.getLoggingEvents())
                            .thenReturn(ImmutableList.of(LoggingEvent.warn("Something may be wrong")));

                    TestLoggerAssert actual = performAssert(Level.WARN, "Something may be wrong");

                    assertThat(actual).isNotNull();
                }
            }

            @Nested
            class Throwables {
                @Mock private Throwable throwable;

                @Test
                void canBeUsedToAssertWithThrowablesWhenFound() {
                    when(logger.getLoggingEvents())
                            .thenReturn(ImmutableList.of(LoggingEvent.error(throwable, "There was a problem!")));

                    assertThatCode(() -> performAssert(throwable, Level.ERROR, "There was a problem!"))
                            .doesNotThrowAnyException();
                }

                @Test
                void canBeUsedToAssertWithThrowablesWhenNotFoundWithArguments() {
                    when(logger.getLoggingEvents())
                            .thenReturn(ImmutableList.of(LoggingEvent.error("There was a problem!", "context")));

                    assertThatThrownBy(
                                    () -> performAssert(throwable, Level.ERROR, "There was a problem!", "context"))
                            .isInstanceOf(AssertionError.class)
                            .hasMessage(
                                    "Failed to find ERROR log message with message `There was a problem!` (with throwable and arguments)");
                }

                @Test
                void canBeUsedToAssertWithThrowablesWhenNotFound() {
                    when(logger.getLoggingEvents())
                            .thenReturn(ImmutableList.of(LoggingEvent.error("There was a problem!")));

                    assertThatThrownBy(() -> performAssert(throwable, Level.ERROR, "There was a problem!"))
                            .isInstanceOf(AssertionError.class)
                            .hasMessage(
                                    "Failed to find ERROR log message with message `There was a problem!` (with throwable)");
                }

                @Test
                void returnsSelfWhenPasses() {
                    when(logger.getLoggingEvents())
                            .thenReturn(ImmutableList.of(LoggingEvent.error(throwable, "There was a problem!")));

                    TestLoggerAssert actual = performAssert(throwable, Level.ERROR, "There was a problem!");

                    assertThat(actual).isNotNull();
                }
            }
        }
    }

    @Nested
    class HasNotLogged {

        @Nested
        class LogMessage extends TestCase {

            @Override
            TestLoggerAssert performAssert(Level level, String message, Object... arguments) {
                return assertions.hasNotLogged(level, message, arguments);
            }

            @Override
            TestLoggerAssert performAssert(
                    Throwable throwable, Level level, String message, Object... arguments) {
                return assertions.hasNotLogged(throwable, level, message, arguments);
            }
        }

        abstract class TestCase {
            abstract TestLoggerAssert performAssert(Level level, String message, Object... arguments);

            abstract TestLoggerAssert performAssert(
                    Throwable throwable, Level level, String message, Object... arguments);

            @Test
            void failsWhenLogMessageIsFound() {
                when(logger.getLoggingEvents())
                        .thenReturn(ImmutableList.of(LoggingEvent.warn("Something may be wrong")));

                assertThatThrownBy(() -> performAssert(Level.WARN, "Something may be wrong"))
                        .isInstanceOf(AssertionError.class)
                        .hasMessage(
                                "Found WARN log with message `Something may be wrong`, even though we expected not to");
            }

            @Test
            void failsWhenExpectingMoreArgumentsThanExists() {
                when(logger.getLoggingEvents())
                        .thenReturn(ImmutableList.of(LoggingEvent.warn("Something may be wrong")));

                assertThatCode(() -> performAssert(Level.WARN, "Something may be wrong", "Extra context"))
                        .doesNotThrowAnyException();
            }

            @Test
            void passesWhenActuallyMoreArgumentsThanExpected() {
                when(logger.getLoggingEvents())
                        .thenReturn(
                                ImmutableList.of(
                                        LoggingEvent.warn("Something may be wrong", "Extra context", "Another")));

                assertThatCode(() -> performAssert(Level.WARN, "Something may be wrong", "Extra context"))
                        .doesNotThrowAnyException();
            }

            @Test
            void failsWhenArgumentsInDifferentOrder() {
                when(logger.getLoggingEvents())
                        .thenReturn(
                                ImmutableList.of(
                                        LoggingEvent.warn("Something may be wrong", "Another", "Extra context")));

                assertThatCode(
                                () ->
                                        performAssert(Level.WARN, "Something may be wrong", "Extra context", "Another"))
                        .doesNotThrowAnyException();
            }

            @Test
            void passesWhenLogMessageIsNotFound() {
                when(logger.getLoggingEvents())
                        .thenReturn(ImmutableList.of(LoggingEvent.info("Nothing to see here")));

                assertThatCode(() -> performAssert(Level.WARN, "Something may be wrong"))
                        .doesNotThrowAnyException();
            }

            @Test
            void passesWhenLogMessageIsNotFoundWithArguments() {
                when(logger.getLoggingEvents())
                        .thenReturn(
                                ImmutableList.of(LoggingEvent.info("Nothing to see here", "Context setting")));

                assertThatCode(() -> performAssert(Level.WARN, "Something may be wrong"))
                        .doesNotThrowAnyException();
            }

            @Test
            void returnsSelfWhenPasses() {
                when(logger.getLoggingEvents())
                        .thenReturn(ImmutableList.of(LoggingEvent.warn("Something else")));

                TestLoggerAssert actual = performAssert(Level.WARN, "Something may be wrong");

                assertThat(actual).isNotNull();
            }

            @Nested
            class Throwables {
                @Mock private Throwable throwable;

                @Test
                void canBeUsedToAssertWithThrowablesWhenNotFound() {
                    when(logger.getLoggingEvents())
                            .thenReturn(
                                    ImmutableList.of(
                                            LoggingEvent.error(
                                                    throwable,
                                                    "There was a problem, but this isn't what you're looking for!")));

                    assertThatCode(() -> performAssert(throwable, Level.ERROR, "There was a problem!"))
                            .doesNotThrowAnyException();
                }

                @Test
                void canBeUsedToAssertWithThrowablesWhenFoundWithArguments() {
                    when(logger.getLoggingEvents())
                            .thenReturn(
                                    ImmutableList.of(
                                            LoggingEvent.error(throwable, "There was a problem!", "context")));

                    assertThatThrownBy(
                                    () -> performAssert(throwable, Level.ERROR, "There was a problem!", "context"))
                            .isInstanceOf(AssertionError.class)
                            .hasMessage(
                                    "Found ERROR log with message `There was a problem!` (with throwable and arguments), even though we expected not to");
                }

                @Test
                void canBeUsedToAssertWithThrowablesWhenFound() {
                    when(logger.getLoggingEvents())
                            .thenReturn(ImmutableList.of(LoggingEvent.error(throwable, "There was a problem!")));

                    assertThatThrownBy(() -> performAssert(throwable, Level.ERROR, "There was a problem!"))
                            .isInstanceOf(AssertionError.class)
                            .hasMessage(
                                    "Found ERROR log with message `There was a problem!` (with throwable), even though we expected not to");
                }

                @Test
                void returnsSelfWhenPasses() {
                    when(logger.getLoggingEvents()).thenReturn(ImmutableList.of());

                    TestLoggerAssert actual = performAssert(throwable, Level.ERROR, "There was a problem!");

                    assertThat(actual).isNotNull();
                }
            }
        }
    }

    @Nested
    class HasLevel {
        @Test
        void returnsLevelAssertRegardlessOfWhetherLogsAreAvailableOrNot() {
            LevelAssert actual = assertions.hasLevel(Level.ERROR);

            assertThat(actual).isNotNull();
        }
    }
}
