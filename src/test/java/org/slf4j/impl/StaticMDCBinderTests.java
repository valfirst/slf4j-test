package org.slf4j.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.github.valfirst.slf4jtest.TestMDCAdapter;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

class StaticMDCBinderTests {

    @Test
    void getMDCA() {
        assertSame(TestMDCAdapter.class, StaticMDCBinder.SINGLETON.getMDCA().getClass());
        assertSame(StaticMDCBinder.SINGLETON.getMDCA(), StaticMDCBinder.SINGLETON.getMDCA());
    }

    @Test
    void getMDCAdapterClassStr() {
        assertEquals(
                "com.github.valfirst.slf4jtest.TestMDCAdapter",
                StaticMDCBinder.SINGLETON.getMDCAdapterClassStr());
    }

    @Test
    void getMarkerFactoryReturnsCorrectlyFromSlf4JLoggerFactory() {
        assertThat(MDC.getMDCAdapter(), instanceOf(TestMDCAdapter.class));
    }
}
