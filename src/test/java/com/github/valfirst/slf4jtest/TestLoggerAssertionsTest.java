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

        @Nested
        class AsLoggingEvent extends TestCase {

            @Override
            TestLoggerAssert performAssert(Level level, String message, Object... arguments) {
                return assertions.hasLogged(event(level, message, arguments));
            }

            @Override
            TestLoggerAssert performAssert(
                    Throwable throwable, Level level, String message, Object... arguments) {
                return assertions.hasLogged(event(throwable, level, message, arguments));
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
                            .hasMessage(
                                    "Failed to find LoggingEvent{level=WARN, mdc={}, marker=Optional.empty, throwable=Optional.empty, message='Something may be wrong', arguments=[]}");
                }

                @Test
                void failsWhenExpectingMoreArgumentsThanExists() {
                    when(logger.getLoggingEvents())
                            .thenReturn(ImmutableList.of(LoggingEvent.warn("Something may be wrong")));

                    assertThatThrownBy(
                                    () -> performAssert(Level.WARN, "Something may be wrong", "Extra context"))
                            .isInstanceOf(AssertionError.class)
                            .hasMessage(
                                    "Failed to find LoggingEvent{level=WARN, mdc={}, marker=Optional.empty, throwable=Optional.empty, message='Something may be wrong', arguments=[Extra context]}");
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
                                    "Failed to find LoggingEvent{level=WARN, mdc={}, marker=Optional.empty, throwable=Optional.empty, message='Something may be wrong', arguments=[Extra context]}");
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
                                    "Failed to find LoggingEvent{level=WARN, mdc={}, marker=Optional.empty, throwable=Optional.empty, message='Something may be wrong', arguments=[Extra context, Another]}");
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
                                    "Failed to find LoggingEvent{level=ERROR, mdc={}, marker=Optional.empty, throwable=Optional[throwable], message='There was a problem!', arguments=[context]}");
                }

                @Test
                void canBeUsedToAssertWithThrowablesWhenNotFound() {
                    when(logger.getLoggingEvents())
                            .thenReturn(ImmutableList.of(LoggingEvent.error("There was a problem!")));

                    assertThatThrownBy(() -> performAssert(throwable, Level.ERROR, "There was a problem!"))
                            .isInstanceOf(AssertionError.class)
                            .hasMessage(
                                    "Failed to find LoggingEvent{level=ERROR, mdc={}, marker=Optional.empty, throwable=Optional[throwable], message='There was a problem!', arguments=[]}");
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

        @Nested
        class AsLoggingEvent extends TestCase {

            @Override
            TestLoggerAssert performAssert(Level level, String message, Object... arguments) {
                return assertions.hasNotLogged(event(level, message, arguments));
            }

            @Override
            TestLoggerAssert performAssert(
                    Throwable throwable, Level level, String message, Object... arguments) {
                return assertions.hasNotLogged(event(throwable, level, message, arguments));
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
                                "Found LoggingEvent{level=WARN, mdc={}, marker=Optional.empty, throwable=Optional.empty, message='Something may be wrong', arguments=[]}, even though we expected not to");
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
                                    "Found LoggingEvent{level=ERROR, mdc={}, marker=Optional.empty, throwable=Optional[throwable], message='There was a problem!', arguments=[context]}, even though we expected not to");
                }

                @Test
                void canBeUsedToAssertWithThrowablesWhenFound() {
                    when(logger.getLoggingEvents())
                            .thenReturn(ImmutableList.of(LoggingEvent.error(throwable, "There was a problem!")));

                    assertThatThrownBy(() -> performAssert(throwable, Level.ERROR, "There was a problem!"))
                            .isInstanceOf(AssertionError.class)
                            .hasMessage(
                                    "Found LoggingEvent{level=ERROR, mdc={}, marker=Optional.empty, throwable=Optional[throwable], message='There was a problem!', arguments=[]}, even though we expected not to");
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

    private static LoggingEvent event(Level level, String message, Object... arguments) {
        switch (level) {
            case WARN:
                return LoggingEvent.warn(message, arguments);
            case ERROR:
                return LoggingEvent.error(message, arguments);
            case INFO:
                return LoggingEvent.info(message, arguments);
            case DEBUG:
                return LoggingEvent.debug(message, arguments);
            case TRACE:
                return LoggingEvent.trace(message, arguments);
            default:
                throw new IllegalStateException("Unmatched level " + level + " provided");
        }
    }

    private static LoggingEvent event(
            Throwable throwable, Level level, String message, Object... arguments) {
        switch (level) {
            case WARN:
                return LoggingEvent.warn(throwable, message, arguments);
            case ERROR:
                return LoggingEvent.error(throwable, message, arguments);
            case INFO:
                return LoggingEvent.info(throwable, message, arguments);
            case DEBUG:
                return LoggingEvent.debug(throwable, message, arguments);
            case TRACE:
                return LoggingEvent.trace(throwable, message, arguments);
            default:
                throw new IllegalStateException("Unmatched level " + level + " provided");
        }
    }
}
