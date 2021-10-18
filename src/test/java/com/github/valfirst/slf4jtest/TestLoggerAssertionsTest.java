package com.github.valfirst.slf4jtest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import uk.org.lidalia.slf4jext.Level;

@ExtendWith(MockitoExtension.class)
class TestLoggerAssertionsTest {

    private final TestLogger logger = mock(TestLogger.class);
    private final TestLoggerAssert assertions = new TestLoggerAssert(logger);

    @Nested
    class CurrentThread {

        @Nested
        class HasLogged extends HasLoggedTestCase {
            HasLogged() {
                super(when(logger.getLoggingEvents()), assertions);
            }
        }

        @Nested
        class HasNotLogged extends HasNotLoggedTestCase {
            HasNotLogged() {
                super(when(logger.getLoggingEvents()), assertions);
            }
        }
    }

    @Nested
    class AnyThread {

        @Nested
        class HasLogged extends HasLoggedTestCase {
            HasLogged() {
                super(when(logger.getAllLoggingEvents()), assertions.anyThread());
            }
        }

        @Nested
        class HasNotLogged extends HasNotLoggedTestCase {
            HasNotLogged() {
                super(when(logger.getAllLoggingEvents()), assertions.anyThread());
            }
        }
    }

    static class HasLoggedTestCase {

        private final OngoingStubbing<ImmutableList<LoggingEvent>> eventsStubbing;
        private final TestLoggerAssert loggerAssert;

        protected HasLoggedTestCase(
                OngoingStubbing<ImmutableList<LoggingEvent>> eventsStubbing,
                TestLoggerAssert loggerAssert) {
            this.eventsStubbing = eventsStubbing;
            this.loggerAssert = loggerAssert;
        }

        @Nested
        class LogMessage extends TestCase {

            @Override
            TestLoggerAssert performAssert(
                    TestLoggerAssert loggerAssert, Level level, String message, Object... arguments) {
                return loggerAssert.hasLogged(level, message, arguments);
            }

            @Override
            TestLoggerAssert performAssert(
                    TestLoggerAssert loggerAssert,
                    Throwable throwable,
                    Level level,
                    String message,
                    Object... arguments) {
                return loggerAssert.hasLogged(throwable, level, message, arguments);
            }
        }

        @Nested
        class AsLoggingEvent extends TestCase {

            @Override
            TestLoggerAssert performAssert(
                    TestLoggerAssert loggerAssert, Level level, String message, Object... arguments) {
                return loggerAssert.hasLogged(event(level, message, arguments));
            }

            @Override
            TestLoggerAssert performAssert(
                    TestLoggerAssert loggerAssert,
                    Throwable throwable,
                    Level level,
                    String message,
                    Object... arguments) {
                return loggerAssert.hasLogged(event(throwable, level, message, arguments));
            }
        }

        abstract class TestCase {

            abstract TestLoggerAssert performAssert(
                    TestLoggerAssert loggerAssert, Level level, String message, Object... arguments);

            abstract TestLoggerAssert performAssert(
                    TestLoggerAssert loggerAssert,
                    Throwable throwable,
                    Level level,
                    String message,
                    Object... arguments);

            @Nested
            class WithoutThrowable {
                @Test
                void failsWhenLogMessageIsNotFound() {
                    eventsStubbing.thenReturn(ImmutableList.of());

                    assertThatThrownBy(
                                    () -> performAssert(loggerAssert, Level.WARN, "Something may be wrong"))
                            .isInstanceOf(AssertionError.class)
                            .hasMessage(
                                    "Failed to find LoggingEvent{level=WARN, mdc={}, marker=Optional.empty, throwable=Optional.empty, message='Something may be wrong', arguments=[]}");
                }

                @Test
                void failsWhenExpectingMoreArgumentsThanExists() {
                    eventsStubbing.thenReturn(ImmutableList.of(LoggingEvent.warn("Something may be wrong")));

                    assertThatThrownBy(
                                    () ->
                                            performAssert(
                                                    loggerAssert, Level.WARN, "Something may be wrong", "Extra context"))
                            .isInstanceOf(AssertionError.class)
                            .hasMessage(
                                    "Failed to find LoggingEvent{level=WARN, mdc={}, marker=Optional.empty, throwable=Optional.empty, message='Something may be wrong', arguments=[Extra context]}");
                }

                @Test
                void failsWhenActuallyMoreArgumentsThanExpected() {
                    eventsStubbing.thenReturn(
                            ImmutableList.of(
                                    LoggingEvent.warn("Something may be wrong", "Extra context", "Another")));

                    assertThatThrownBy(
                                    () ->
                                            performAssert(
                                                    loggerAssert, Level.WARN, "Something may be wrong", "Extra context"))
                            .isInstanceOf(AssertionError.class)
                            .hasMessage(
                                    "Failed to find LoggingEvent{level=WARN, mdc={}, marker=Optional.empty, throwable=Optional.empty, message='Something may be wrong', arguments=[Extra context]}");
                }

                @Test
                void failsWhenArgumentsInDifferentOrder() {
                    eventsStubbing.thenReturn(
                            ImmutableList.of(
                                    LoggingEvent.warn("Something may be wrong", "Another", "Extra context")));

                    assertThatThrownBy(
                                    () ->
                                            performAssert(
                                                    loggerAssert,
                                                    Level.WARN,
                                                    "Something may be wrong",
                                                    "Extra context",
                                                    "Another"))
                            .isInstanceOf(AssertionError.class)
                            .hasMessage(
                                    "Failed to find LoggingEvent{level=WARN, mdc={}, marker=Optional.empty, throwable=Optional.empty, message='Something may be wrong', arguments=[Extra context, Another]}");
                }

                @Test
                void passesWhenLogMessageIsFound() {
                    eventsStubbing.thenReturn(ImmutableList.of(LoggingEvent.warn("Something may be wrong")));

                    assertThatCode(() -> performAssert(loggerAssert, Level.WARN, "Something may be wrong"))
                            .doesNotThrowAnyException();
                }

                @Test
                void returnsSelfWhenPasses() {
                    eventsStubbing.thenReturn(ImmutableList.of(LoggingEvent.warn("Something may be wrong")));

                    TestLoggerAssert actual =
                            performAssert(loggerAssert, Level.WARN, "Something may be wrong");

                    assertThat(actual).isNotNull();
                }
            }

            @Nested
            class Throwables {
                @Mock private Throwable throwable;

                @Test
                void canBeUsedToAssertWithThrowablesWhenFound() {
                    eventsStubbing.thenReturn(
                            ImmutableList.of(LoggingEvent.error(throwable, "There was a problem!")));

                    assertThatCode(
                                    () -> performAssert(loggerAssert, throwable, Level.ERROR, "There was a problem!"))
                            .doesNotThrowAnyException();
                }

                @Test
                void canBeUsedToAssertWithThrowablesWhenNotFoundWithArguments() {
                    eventsStubbing.thenReturn(
                            ImmutableList.of(LoggingEvent.error("There was a problem!", "context")));

                    assertThatThrownBy(
                                    () ->
                                            performAssert(
                                                    loggerAssert, throwable, Level.ERROR, "There was a problem!", "context"))
                            .isInstanceOf(AssertionError.class)
                            .hasMessage(
                                    "Failed to find LoggingEvent{level=ERROR, mdc={}, marker=Optional.empty, throwable=Optional[throwable], message='There was a problem!', arguments=[context]}");
                }

                @Test
                void canBeUsedToAssertWithThrowablesWhenNotFound() {
                    eventsStubbing.thenReturn(ImmutableList.of(LoggingEvent.error("There was a problem!")));

                    assertThatThrownBy(
                                    () -> performAssert(loggerAssert, throwable, Level.ERROR, "There was a problem!"))
                            .isInstanceOf(AssertionError.class)
                            .hasMessage(
                                    "Failed to find LoggingEvent{level=ERROR, mdc={}, marker=Optional.empty, throwable=Optional[throwable], message='There was a problem!', arguments=[]}");
                }

                @Test
                void returnsSelfWhenPasses() {
                    eventsStubbing.thenReturn(
                            ImmutableList.of(LoggingEvent.error(throwable, "There was a problem!")));

                    TestLoggerAssert actual =
                            performAssert(loggerAssert, throwable, Level.ERROR, "There was a problem!");

                    assertThat(actual).isNotNull();
                }
            }
        }
    }

    class HasNotLoggedTestCase {

        private final OngoingStubbing<ImmutableList<LoggingEvent>> eventsStubbing;
        private final TestLoggerAssert loggerAssert;

        protected HasNotLoggedTestCase(
                OngoingStubbing<ImmutableList<LoggingEvent>> eventsStubbing,
                TestLoggerAssert loggerAssert) {
            this.eventsStubbing = eventsStubbing;
            this.loggerAssert = loggerAssert;
        }

        @Nested
        class LogMessage extends TestCase {

            @Override
            TestLoggerAssert performAssert(
                    TestLoggerAssert loggerAssert, Level level, String message, Object... arguments) {
                return loggerAssert.hasNotLogged(level, message, arguments);
            }

            @Override
            TestLoggerAssert performAssert(
                    TestLoggerAssert loggerAssert,
                    Throwable throwable,
                    Level level,
                    String message,
                    Object... arguments) {
                return loggerAssert.hasNotLogged(throwable, level, message, arguments);
            }
        }

        @Nested
        class AsLoggingEvent extends TestCase {

            @Override
            TestLoggerAssert performAssert(
                    TestLoggerAssert loggerAssert, Level level, String message, Object... arguments) {
                return loggerAssert.hasNotLogged(event(level, message, arguments));
            }

            @Override
            TestLoggerAssert performAssert(
                    TestLoggerAssert loggerAssert,
                    Throwable throwable,
                    Level level,
                    String message,
                    Object... arguments) {
                return loggerAssert.hasNotLogged(event(throwable, level, message, arguments));
            }
        }

        abstract class TestCase {
            abstract TestLoggerAssert performAssert(
                    TestLoggerAssert loggerAssert, Level level, String message, Object... arguments);

            abstract TestLoggerAssert performAssert(
                    TestLoggerAssert loggerAssert,
                    Throwable throwable,
                    Level level,
                    String message,
                    Object... arguments);

            @Test
            void failsWhenLogMessageIsFound() {
                eventsStubbing.thenReturn(ImmutableList.of(LoggingEvent.warn("Something may be wrong")));

                assertThatThrownBy(() -> performAssert(loggerAssert, Level.WARN, "Something may be wrong"))
                        .isInstanceOf(AssertionError.class)
                        .hasMessage(
                                "Found LoggingEvent{level=WARN, mdc={}, marker=Optional.empty, throwable=Optional.empty, message='Something may be wrong', arguments=[]}, even though we expected not to");
            }

            @Test
            void failsWhenExpectingMoreArgumentsThanExists() {
                eventsStubbing.thenReturn(ImmutableList.of(LoggingEvent.warn("Something may be wrong")));

                assertThatCode(
                                () ->
                                        performAssert(
                                                loggerAssert, Level.WARN, "Something may be wrong", "Extra context"))
                        .doesNotThrowAnyException();
            }

            @Test
            void passesWhenActuallyMoreArgumentsThanExpected() {
                eventsStubbing.thenReturn(
                        ImmutableList.of(
                                LoggingEvent.warn("Something may be wrong", "Extra context", "Another")));

                assertThatCode(
                                () ->
                                        performAssert(
                                                loggerAssert, Level.WARN, "Something may be wrong", "Extra context"))
                        .doesNotThrowAnyException();
            }

            @Test
            void failsWhenArgumentsInDifferentOrder() {
                eventsStubbing.thenReturn(
                        ImmutableList.of(
                                LoggingEvent.warn("Something may be wrong", "Another", "Extra context")));

                assertThatCode(
                                () ->
                                        performAssert(
                                                loggerAssert,
                                                Level.WARN,
                                                "Something may be wrong",
                                                "Extra context",
                                                "Another"))
                        .doesNotThrowAnyException();
            }

            @Test
            void passesWhenLogMessageIsNotFound() {
                eventsStubbing.thenReturn(ImmutableList.of(LoggingEvent.info("Nothing to see here")));

                assertThatCode(() -> performAssert(loggerAssert, Level.WARN, "Something may be wrong"))
                        .doesNotThrowAnyException();
            }

            @Test
            void passesWhenLogMessageIsNotFoundWithArguments() {
                eventsStubbing.thenReturn(
                        ImmutableList.of(LoggingEvent.info("Nothing to see here", "Context setting")));

                assertThatCode(() -> performAssert(loggerAssert, Level.WARN, "Something may be wrong"))
                        .doesNotThrowAnyException();
            }

            @Test
            void returnsSelfWhenPasses() {
                eventsStubbing.thenReturn(ImmutableList.of(LoggingEvent.warn("Something else")));

                TestLoggerAssert actual = performAssert(loggerAssert, Level.WARN, "Something may be wrong");

                assertThat(actual).isNotNull();
            }

            @Nested
            class Throwables {
                @Mock private Throwable throwable;

                @Test
                void canBeUsedToAssertWithThrowablesWhenNotFound() {
                    eventsStubbing.thenReturn(
                            ImmutableList.of(
                                    LoggingEvent.error(
                                            throwable, "There was a problem, but this isn't what you're looking for!")));

                    assertThatCode(
                                    () -> performAssert(loggerAssert, throwable, Level.ERROR, "There was a problem!"))
                            .doesNotThrowAnyException();
                }

                @Test
                void canBeUsedToAssertWithThrowablesWhenFoundWithArguments() {
                    eventsStubbing.thenReturn(
                            ImmutableList.of(LoggingEvent.error(throwable, "There was a problem!", "context")));

                    assertThatThrownBy(
                                    () ->
                                            performAssert(
                                                    loggerAssert, throwable, Level.ERROR, "There was a problem!", "context"))
                            .isInstanceOf(AssertionError.class)
                            .hasMessage(
                                    "Found LoggingEvent{level=ERROR, mdc={}, marker=Optional.empty, throwable=Optional[throwable], message='There was a problem!', arguments=[context]}, even though we expected not to");
                }

                @Test
                void canBeUsedToAssertWithThrowablesWhenFound() {
                    eventsStubbing.thenReturn(
                            ImmutableList.of(LoggingEvent.error(throwable, "There was a problem!")));

                    assertThatThrownBy(
                                    () -> performAssert(loggerAssert, throwable, Level.ERROR, "There was a problem!"))
                            .isInstanceOf(AssertionError.class)
                            .hasMessage(
                                    "Found LoggingEvent{level=ERROR, mdc={}, marker=Optional.empty, throwable=Optional[throwable], message='There was a problem!', arguments=[]}, even though we expected not to");
                }

                @Test
                void returnsSelfWhenPasses() {
                    eventsStubbing.thenReturn(ImmutableList.of());

                    TestLoggerAssert actual =
                            performAssert(loggerAssert, throwable, Level.ERROR, "There was a problem!");

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

        @Test
        void propagatesAnyThreadToLevelAssert() {
            when(logger.getAllLoggingEvents()).thenReturn(ImmutableList.of());

            assertions.anyThread().hasLevel(Level.ERROR).hasNumberOfLogs(0);

            verify(logger).getAllLoggingEvents();
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
