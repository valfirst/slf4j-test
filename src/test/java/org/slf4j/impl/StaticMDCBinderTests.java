package org.slf4j.impl;

import org.junit.Test;
import org.slf4j.MDC;

import com.github.valfirst.slf4jtest.TestMDCAdapter;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

public class StaticMDCBinderTests {

    @Test
    public void getMDCA() {
        assertSame(TestMDCAdapter.class, StaticMDCBinder.SINGLETON.getMDCA().getClass());
        assertSame(StaticMDCBinder.SINGLETON.getMDCA(), StaticMDCBinder.SINGLETON.getMDCA());
    }

    @Test
    public void getMDCAdapterClassStr() {
        assertEquals("com.github.valfirst.slf4jtest.TestMDCAdapter", StaticMDCBinder.SINGLETON.getMDCAdapterClassStr());
    }

    @Test
    public void getMarkerFactoryReturnsCorrectlyFromSlf4JLoggerFactory() {
        assertThat(MDC.getMDCAdapter(), instanceOf(TestMDCAdapter.class));
    }
}
