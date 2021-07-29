package com.github.valfirst.slf4jtest;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

/** @author Valery Yatsynovich */
public class TestSLF4JServiceProvider implements SLF4JServiceProvider {
    public static final String REQUESTED_API_VERSION = "1.8.99";

    private ILoggerFactory loggerFactory;
    private IMarkerFactory markerFactory;
    private MDCAdapter mdcAdapter;

    @Override
    public void initialize() {
        loggerFactory = TestLoggerFactory.getInstance();
        markerFactory = new BasicMarkerFactory();
        mdcAdapter = new TestMDCAdapter();
    }

    @Override
    public ILoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    @Override
    public IMarkerFactory getMarkerFactory() {
        return markerFactory;
    }

    @Override
    public MDCAdapter getMDCAdapter() {
        return mdcAdapter;
    }

    @Override
    public String getRequesteApiVersion() {
        return REQUESTED_API_VERSION;
    }
}
