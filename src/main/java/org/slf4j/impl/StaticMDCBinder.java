package org.slf4j.impl;

import com.github.valfirst.slf4jtest.TestMDCAdapter;
import org.slf4j.spi.MDCAdapter;

public final class StaticMDCBinder {

    public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();

    private final TestMDCAdapter testMDCAdapter = new TestMDCAdapter();

    private StaticMDCBinder() {}

    public MDCAdapter getMDCA() {
        return testMDCAdapter;
    }

    public String getMDCAdapterClassStr() {
        return TestMDCAdapter.class.getName();
    }
}
