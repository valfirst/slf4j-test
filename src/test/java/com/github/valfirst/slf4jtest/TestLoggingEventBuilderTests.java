package com.github.valfirst.slf4jtest;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.slf4j.event.Level.INFO;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.event.LoggingEvent;

class TestLoggingEventBuilderTests {
    @Test
    void markers() {
        final Marker marker1 = MarkerFactory.getMarker("1");
        final Marker marker2 = MarkerFactory.getMarker("2");
        TestLoggingEventBuilder builder = new TestLoggingEventBuilder(null, INFO);
        builder.addMarker(marker1).addMarker(marker2);
        LoggingEvent event = builder.toLoggingEvent();
        assertThat(event.getMarkers(), is(asList(marker1, marker2)));
    }

    @Test
    void keyValuePairs() {
        LoggingEvent event =
                new TestLoggingEventBuilder(null, INFO)
                        .addKeyValue("KEY1", 1)
                        .addKeyValue("KEY1", 2L)
                        .toLoggingEvent();
        List<TestLoggingEventBuilder.TestKeyValuePair> expected =
                asList(
                        new TestLoggingEventBuilder.TestKeyValuePair("KEY1", 1),
                        new TestLoggingEventBuilder.TestKeyValuePair("KEY1", 2L));
        assertThat(event.getKeyValuePairs(), is(expected));
    }

    @Test
    void noKeyValuePairs() {
        LoggingEvent event = new TestLoggingEventBuilder(null, INFO).toLoggingEvent();
        assertThat(event.getKeyValuePairs(), is(nullValue()));
    }

    @Test
    void keyValuePairEqualsSame() {
        TestLoggingEventBuilder.TestKeyValuePair pair =
                new TestLoggingEventBuilder.TestKeyValuePair("x", "y");
        assertThat(pair.equals(pair), is(true));
    }

    @Test
    void keyValuePairNotEqualsNull() {
        TestLoggingEventBuilder.TestKeyValuePair pair =
                new TestLoggingEventBuilder.TestKeyValuePair("x", "y");
        assertThat(pair.equals(null), is(false));
    }

    @Test
    void keyValuePairHashCode() {
        TestLoggingEventBuilder.TestKeyValuePair pair =
                new TestLoggingEventBuilder.TestKeyValuePair(null, null);
        assertThat(pair.hashCode(), is(961));
    }
}
