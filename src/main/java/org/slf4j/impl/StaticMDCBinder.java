package org.slf4j.impl;

import org.slf4j.spi.MDCAdapter;

import uk.org.lidalia.slf4jtest.TestMDCAdapter;

public final class StaticMDCBinder {

    public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();

    private final TestMDCAdapter testMDCAdapter = new TestMDCAdapter();

    private StaticMDCBinder() {
    }

    public MDCAdapter getMDCA() {
        return testMDCAdapter;
    }

    public String getMDCAdapterClassStr() {
        return TestMDCAdapter.class.getName();
    }
}
