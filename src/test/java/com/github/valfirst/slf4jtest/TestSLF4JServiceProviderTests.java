package com.github.valfirst.slf4jtest;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ServiceLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.MarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.SLF4JServiceProvider;

/** @author Valery Yatsynovich */
class TestSLF4JServiceProviderTests {
    private SLF4JServiceProvider slf4JServiceProvider =
            ServiceLoader.load(SLF4JServiceProvider.class).iterator().next();

    @BeforeEach
    void setUp() {
        slf4JServiceProvider.initialize();
    }

    @Test
    void getProviderClass() {
        assertEquals(TestSLF4JServiceProvider.class, slf4JServiceProvider.getClass());
    }

    @Test
    void getLoggerFactory() {
        assertSame(TestLoggerFactory.class, slf4JServiceProvider.getLoggerFactory().getClass());
        assertSame(slf4JServiceProvider.getLoggerFactory(), slf4JServiceProvider.getLoggerFactory());
    }

    @Test
    void getLoggerFactoryReturnsCorrectlyFromSlf4JLoggerFactory() {
        assertThat(LoggerFactory.getILoggerFactory(), is(slf4JServiceProvider.getLoggerFactory()));
    }

    @Test
    void getRequestedApiVersion() {
        assertEquals("2.0.99", slf4JServiceProvider.getRequestedApiVersion());
    }

    @Test
    void getMarkerFactory() {
        assertSame(BasicMarkerFactory.class, slf4JServiceProvider.getMarkerFactory().getClass());
        assertSame(slf4JServiceProvider.getMarkerFactory(), slf4JServiceProvider.getMarkerFactory());
    }

    @Test
    void getMarkerFactoryReturnsCorrectlyFromSlf4JMarkerFactory() {
        assertThat(MarkerFactory.getIMarkerFactory(), instanceOf(BasicMarkerFactory.class));
    }

    @Test
    void getMDCAdapter() {
        assertSame(TestMDCAdapter.class, slf4JServiceProvider.getMDCAdapter().getClass());
        assertSame(slf4JServiceProvider.getMDCAdapter(), slf4JServiceProvider.getMDCAdapter());
    }

    @Test
    void getMDCAdapterIsReturnedCorrectlyFromSlf4JMDC() {
        assertThat(MDC.getMDCAdapter(), instanceOf(TestMDCAdapter.class));
    }
}
