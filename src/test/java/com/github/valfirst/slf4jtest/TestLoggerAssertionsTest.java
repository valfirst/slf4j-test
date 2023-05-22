package com.github.valfirst.slf4jtest;

import static com.github.valfirst.slf4jtest.LoggingEvent.warn;
import static com.github.valfirst.slf4jtest.TestLoggerAssert.MdcComparator.CONTAINING;
import static com.github.valfirst.slf4jtest.TestLoggerAssert.MdcComparator.IGNORING;
import static com.github.valfirst.slf4jtest.TestLoggerAssert.PredicateBuilder.aLog;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.google.common.collect.ImmutableList;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.slf4j.event.Level;

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

    @Nested
    class MdcComparison {
        private final Map<String, String> testMdc = new HashMap<>();
        private LoggingEvent loggingEvent;

        @BeforeEach
        void setUp() {
            testMdc.put("key", "value");
            testMdc.put("another", "different value");
            loggingEvent = warn(testMdc, "Something may be wrong");
            when(logger.getLoggingEvents()).thenReturn(ImmutableList.of(loggingEvent));
        }

        @Test
        void requiresExactMdcContentsByDefault() {
            Map<String, String> mdc = new HashMap<>();
            mdc.put("key", "value");
            mdc.put("another", "slightly different value");

            assertThatThrownBy(() -> assertions.hasLogged(warn(mdc, "Something may be wrong")))
                    .isInstanceOf(AssertionError.class)
                    .hasMessage(
                            "Failed to find event:\n  LoggingEvent{level=WARN, mdc={another=slightly different value, key=value}, marker=Optional.empty, throwable=Optional.empty, message='Something may be wrong', arguments=[]}"
                                    + loggerContainedMessage(loggingEvent));
        }

        @Test
        void ignoringComparatorAlwaysPasses() {
            assertThatNoException()
                    .isThrownBy(
                            () ->
                                    assertions
                                            .usingMdcComparator(IGNORING)
                                            .hasLogged(Level.WARN, "Something may be wrong"));
        }

        @Test
        void containingComparatorAllowsForAdditionalMdcEntries() {
            Map<String, String> subsetMdc = new HashMap<>();
            subsetMdc.put("key", "value");

            assertThatNoException()
                    .isThrownBy(
                            () ->
                                    assertions
                                            .usingMdcComparator(CONTAINING)
                                            .hasLogged(warn(subsetMdc, "Something may be wrong")));
        }
    }

    class HasLoggedTestCase {

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

        @Nested
        class CustomPredicates {

            private final Predicate<LoggingEvent> testPredicate =
                    event -> "A formatted message".equals(event.getFormattedMessage());

            @Test
            void passesWhenPredicateMatches() {
                eventsStubbing.thenReturn(ImmutableList.of(warn("A {} message", "formatted")));
                assertThatNoException().isThrownBy(() -> loggerAssert.hasLogged(testPredicate));
            }

            @Test
            void failsWhenPredicateDoesNotMatch() {
                LoggingEvent loggingEvent = warn("A different message");
                eventsStubbing.thenReturn(ImmutableList.of(loggingEvent));

                assertThatThrownBy(() -> loggerAssert.hasLogged(testPredicate))
                        .isInstanceOf(AssertionError.class)
                        .hasMessage(
                                "Failed to find log matching predicate" + loggerContainedMessage(loggingEvent));
            }
        }

        @Nested
        class UsingPredicateBuilder {

            @Test
            void passesWhenPredicateMatches() {
                eventsStubbing.thenReturn(ImmutableList.of(warn("A message")));
                assertThatNoException()
                        .isThrownBy(() -> loggerAssert.hasLogged(aLog().withMessage("A message")));
            }

            @Test
            void failsWhenPredicateDoesNotMatch() {
                LoggingEvent loggingEvent = warn("A different message");
                eventsStubbing.thenReturn(ImmutableList.of(loggingEvent));

                assertThatThrownBy(() -> loggerAssert.hasLogged(aLog().withMessage("A message")))
                        .isInstanceOf(AssertionError.class)
                        .hasMessage(
                                "Failed to find log matching predicate" + loggerContainedMessage(loggingEvent));
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
                                    "Failed to find event:\n  LoggingEvent{level=WARN, mdc={}, marker=Optional.empty, throwable=Optional.empty, message='Something may be wrong', arguments=[]}"
                                            + loggerContainedMessage());
                }

                @Test
                void failsWhenExpectingMoreArgumentsThanExists() {
                    LoggingEvent loggingEvent = warn("Something may be wrong");
                    eventsStubbing.thenReturn(ImmutableList.of(loggingEvent));

                    assertThatThrownBy(
                                    () ->
                                            performAssert(
                                                    loggerAssert, Level.WARN, "Something may be wrong", "Extra context"))
                            .isInstanceOf(AssertionError.class)
                            .hasMessage(
                                    "Failed to find event:\n  LoggingEvent{level=WARN, mdc={}, marker=Optional.empty, throwable=Optional.empty, message='Something may be wrong', arguments=[Extra context]}"
                                            + loggerContainedMessage(loggingEvent));
                }

                @Test
                void failsWhenActuallyMoreArgumentsThanExpected() {
                    LoggingEvent firstEvent = warn("Something may be wrong", "Extra context", "Another");
                    LoggingEvent secondEvent =
                            LoggingEvent.error("Something may be wrong", "Extra context", "Another");
                    eventsStubbing.thenReturn(ImmutableList.of(firstEvent, secondEvent));

                    assertThatThrownBy(
                                    () ->
                                            performAssert(
                                                    loggerAssert, Level.WARN, "Something may be wrong", "Extra context"))
                            .isInstanceOf(AssertionError.class)
                            .hasMessage(
                                    "Failed to find event:\n  LoggingEvent{level=WARN, mdc={}, marker=Optional.empty, throwable=Optional.empty, message='Something may be wrong', arguments=[Extra context]}"
                                            + loggerContainedMessage(firstEvent, secondEvent));
                }

                @Test
                void failsWhenArgumentsInDifferentOrder() {
                    LoggingEvent loggingEvent = warn("Something may be wrong", "Another", "Extra context");
                    eventsStubbing.thenReturn(ImmutableList.of(loggingEvent));

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
                                    "Failed to find event:\n  LoggingEvent{level=WARN, mdc={}, marker=Optional.empty, throwable=Optional.empty, message='Something may be wrong', arguments=[Extra context, Another]}"
                                            + loggerContainedMessage(loggingEvent));
                }

                @Test
                void passesWhenLogMessageIsFound() {
                    eventsStubbing.thenReturn(ImmutableList.of(warn("Something may be wrong")));

                    assertThatCode(() -> performAssert(loggerAssert, Level.WARN, "Something may be wrong"))
                            .doesNotThrowAnyException();
                }

                @Test
                void returnsSelfWhenPasses() {
                    eventsStubbing.thenReturn(ImmutableList.of(warn("Something may be wrong")));

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
                    LoggingEvent loggingEvent = LoggingEvent.error("There was a problem!", "context");
                    eventsStubbing.thenReturn(ImmutableList.of(loggingEvent));

                    assertThatThrownBy(
                                    () ->
                                            performAssert(
                                                    loggerAssert, throwable, Level.ERROR, "There was a problem!", "context"))
                            .isInstanceOf(AssertionError.class)
                            .hasMessage(
                                    "Failed to find event:\n  LoggingEvent{level=ERROR, mdc={}, marker=Optional.empty, throwable=Optional[throwable], message='There was a problem!', arguments=[context]}"
                                            + loggerContainedMessage(loggingEvent));
                }

                @Test
                void canBeUsedToAssertWithThrowablesWhenNotFound() {
                    LoggingEvent loggingEvent = LoggingEvent.error("There was a problem!");
                    eventsStubbing.thenReturn(ImmutableList.of(loggingEvent));

                    assertThatThrownBy(
                                    () -> performAssert(loggerAssert, throwable, Level.ERROR, "There was a problem!"))
                            .isInstanceOf(AssertionError.class)
                            .hasMessage(
                                    "Failed to find event:\n  LoggingEvent{level=ERROR, mdc={}, marker=Optional.empty, throwable=Optional[throwable], message='There was a problem!', arguments=[]}"
                                            + loggerContainedMessage(loggingEvent));
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

    static class HasNotLoggedTestCase {

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

        @Nested
        class CustomPredicates {

            private final Predicate<LoggingEvent> testPredicate =
                    event -> "A formatted message".equals(event.getFormattedMessage());

            @Test
            void passesWhenPredicateDoesNotMatch() {
                eventsStubbing.thenReturn(ImmutableList.of(warn("A different message")));

                assertThatNoException().isThrownBy(() -> loggerAssert.hasNotLogged(testPredicate));
            }

            @Test
            void failsWhenPredicateMatches() {
                LoggingEvent loggingEvent = warn("A {} message", "formatted");
                eventsStubbing.thenReturn(ImmutableList.of(loggingEvent));

                assertThatThrownBy(() -> loggerAssert.hasNotLogged(testPredicate))
                        .isInstanceOf(AssertionError.class)
                        .hasMessage("Found " + loggingEvent + ", even though we expected not to");
            }
        }

        @Nested
        class UsingPredicateBuilder {
            @Test
            void passesWhenPredicateDoesNotMatch() {
                eventsStubbing.thenReturn(ImmutableList.of(warn("A message")));

                assertThatNoException()
                        .isThrownBy(() -> loggerAssert.hasNotLogged(aLog().withMessage("Unexpected")));
            }

            @Test
            void failsWhenPredicateMatches() {
                LoggingEvent loggingEvent = warn("A message");
                eventsStubbing.thenReturn(ImmutableList.of(loggingEvent));

                assertThatThrownBy(() -> loggerAssert.hasNotLogged(aLog().withMessage("A message")))
                        .isInstanceOf(AssertionError.class)
                        .hasMessage("Found " + loggingEvent + ", even though we expected not to");
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
                eventsStubbing.thenReturn(ImmutableList.of(warn("Something may be wrong")));

                assertThatThrownBy(() -> performAssert(loggerAssert, Level.WARN, "Something may be wrong"))
                        .isInstanceOf(AssertionError.class)
                        .hasMessage(
                                "Found LoggingEvent{level=WARN, mdc={}, marker=Optional.empty, throwable=Optional.empty, message='Something may be wrong', arguments=[]}, even though we expected not to");
            }

            @Test
            void failsWhenExpectingMoreArgumentsThanExists() {
                eventsStubbing.thenReturn(ImmutableList.of(warn("Something may be wrong")));

                assertThatCode(
                                () ->
                                        performAssert(
                                                loggerAssert, Level.WARN, "Something may be wrong", "Extra context"))
                        .doesNotThrowAnyException();
            }

            @Test
            void passesWhenActuallyMoreArgumentsThanExpected() {
                eventsStubbing.thenReturn(
                        ImmutableList.of(warn("Something may be wrong", "Extra context", "Another")));

                assertThatCode(
                                () ->
                                        performAssert(
                                                loggerAssert, Level.WARN, "Something may be wrong", "Extra context"))
                        .doesNotThrowAnyException();
            }

            @Test
            void failsWhenArgumentsInDifferentOrder() {
                eventsStubbing.thenReturn(
                        ImmutableList.of(warn("Something may be wrong", "Another", "Extra context")));

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
                eventsStubbing.thenReturn(ImmutableList.of(warn("Something else")));

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
                return warn(message, arguments);
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
                return warn(throwable, message, arguments);
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

    private String loggerContainedMessage(LoggingEvent... events) {
        String eventDetails =
                events.length == 0
                        ? "<none>"
                        : "- "
                                + Arrays.stream(events)
                                        .map(Objects::toString)
                                        .collect(Collectors.joining("\n  - "));
        return "\n\nThe logger contained the following events:\n  " + eventDetails;
    }
}
