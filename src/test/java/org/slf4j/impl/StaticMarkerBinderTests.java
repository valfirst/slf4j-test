package org.slf4j.impl;

import org.junit.jupiter.api.Test;
import org.slf4j.MarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class StaticMarkerBinderTests {

    @Test
    void getMarkerFactory() {
        assertSame(BasicMarkerFactory.class, StaticMarkerBinder.SINGLETON.getMarkerFactory().getClass());
        assertSame(StaticMarkerBinder.SINGLETON.getMarkerFactory(), StaticMarkerBinder.SINGLETON.getMarkerFactory());
    }

    @Test
    void getMarkerFactoryClassStr() {
        assertEquals("org.slf4j.helpers.BasicMarkerFactory", StaticMarkerBinder.SINGLETON.getMarkerFactoryClassStr());
    }

    @Test
    void getMarkerFactoryReturnsCorrectlyFromSlf4JLoggerFactory() {
        assertThat(MarkerFactory.getIMarkerFactory(), instanceOf(BasicMarkerFactory.class));
    }
}
