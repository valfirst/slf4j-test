package org.slf4j.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.github.valfirst.slf4jtest.TestLoggerFactory;
import org.junit.jupiter.api.Test;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

class StaticLoggerBinderTests {

    @Test
    void getLoggerFactory() {
        assertSame(
                TestLoggerFactory.getInstance(), StaticLoggerBinder.getSingleton().getLoggerFactory());
    }

    @Test
    void getLoggerFactoryClassStr() {
        assertEquals(
                "com.github.valfirst.slf4jtest.TestLoggerFactory",
                StaticLoggerBinder.getSingleton().getLoggerFactoryClassStr());
    }

    @Test
    void getLoggerFactoryReturnsCorrectlyFromSlf4JLoggerFactory() {
        ILoggerFactory expected = TestLoggerFactory.getInstance();
        assertThat(LoggerFactory.getILoggerFactory(), is(expected));
    }

    @Test
    void requestedApiVersion() {
        assertEquals("1.6", StaticLoggerBinder.REQUESTED_API_VERSION);
    }
}
