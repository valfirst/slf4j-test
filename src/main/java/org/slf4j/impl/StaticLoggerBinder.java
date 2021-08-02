package org.slf4j.impl;

import com.github.valfirst.slf4jtest.TestLoggerFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

public final class StaticLoggerBinder implements LoggerFactoryBinder {

    private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

    public static final String REQUESTED_API_VERSION = "1.6";

    public static StaticLoggerBinder getSingleton() {
        return SINGLETON;
    }

    private StaticLoggerBinder() {}

    public ILoggerFactory getLoggerFactory() {
        return TestLoggerFactory.getInstance();
    }

    public String getLoggerFactoryClassStr() {
        return TestLoggerFactory.class.getName();
    }
}
