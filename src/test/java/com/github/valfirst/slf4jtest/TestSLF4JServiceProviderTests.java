package com.github.valfirst.slf4jtest;

import java.util.ServiceLoader;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.MarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.SLF4JServiceProvider;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

/**
 * @author Valery Yatsynovich
 */
public class TestSLF4JServiceProviderTests {
    private SLF4JServiceProvider slf4JServiceProvider = ServiceLoader.load(SLF4JServiceProvider.class).iterator()
            .next();

    @Before
    public void setUp() {
        slf4JServiceProvider.initialize();
    }

    @Test
    public void getProviderClass() {
        assertEquals(TestSLF4JServiceProvider.class, slf4JServiceProvider.getClass());
    }

    @Test
    public void getLoggerFactory() {
        assertSame(TestLoggerFactory.class, slf4JServiceProvider.getLoggerFactory().getClass());
        assertSame(slf4JServiceProvider.getLoggerFactory(), slf4JServiceProvider.getLoggerFactory());
    }

    @Test
    public void getLoggerFactoryReturnsCorrectlyFromSlf4JLoggerFactory() {
        assertThat(LoggerFactory.getILoggerFactory(), is(slf4JServiceProvider.getLoggerFactory()));
    }

    @Test
    public void getRequestedApiVersion() {
        assertEquals("1.8.99", slf4JServiceProvider.getRequesteApiVersion());
    }

    @Test
    public void getMarkerFactory() {
        assertSame(BasicMarkerFactory.class, slf4JServiceProvider.getMarkerFactory().getClass());
        assertSame(slf4JServiceProvider.getMarkerFactory(), slf4JServiceProvider.getMarkerFactory());
    }

    @Test
    public void getMarkerFactoryReturnsCorrectlyFromSlf4JMarkerFactory() {
        assertThat(MarkerFactory.getIMarkerFactory(), instanceOf(BasicMarkerFactory.class));
    }

    @Test
    public void getMDCAdapter() {
        assertSame(TestMDCAdapter.class, slf4JServiceProvider.getMDCAdapter().getClass());
        assertSame(slf4JServiceProvider.getMDCAdapter(), slf4JServiceProvider.getMDCAdapter());
    }

    @Test
    public void getMDCAdapterIsReturnedCorrectlyFromSlf4JMDC() {
        assertThat(MDC.getMDCAdapter(), instanceOf(TestMDCAdapter.class));
    }
}
