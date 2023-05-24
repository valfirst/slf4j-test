package com.github.valfirst.slf4jtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.Test;

class TestMDCAdapterTests {

    private final TestMDCAdapter testMDCAdapter = new TestMDCAdapter();
    private final String key = "key";
    private final String value = "value";

    @Test
    void putGetRemoveLoop() {
        assertNull(testMDCAdapter.get(key));
        testMDCAdapter.put(key, value);
        assertEquals(value, testMDCAdapter.get(key));
        testMDCAdapter.remove(key);
        assertNull(testMDCAdapter.get(key));
    }

    @Test
    void getCopyOfContextMap() {
        testMDCAdapter.put(key, value);
        assertEquals(Collections.singletonMap(key, value), testMDCAdapter.getCopyOfContextMap());
    }

    @Test
    void getCopyOfContextMapIsCopy() {
        testMDCAdapter.put(key, value);
        Map<String, String> actual = testMDCAdapter.getCopyOfContextMap();
        testMDCAdapter.clear();
        assertEquals(Collections.singletonMap(key, value), actual);
    }

    @Test
    void clear() {
        testMDCAdapter.put(key, value);
        testMDCAdapter.clear();
        assertEquals(Collections.emptyMap(), testMDCAdapter.getCopyOfContextMap());
    }

    @Test
    void setContextMapSetsCopy() {
        Map<String, String> newValues = new HashMap<>();
        newValues.put(key, value);
        testMDCAdapter.setContextMap(newValues);
        Map<String, String> expected = new HashMap<>(newValues);
        newValues.clear();
        assertEquals(expected, testMDCAdapter.getCopyOfContextMap());
    }

    @Test
    void testMdcAdapterIsThreadLocal() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(2);
        final Map<String, String> results = new HashMap<>();
        Thread thread1 =
                new Thread(
                        () -> {
                            testMDCAdapter.put(key, "value1");
                            latch.countDown();
                            try {
                                latch.await();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            results.put("thread1", testMDCAdapter.get(key));
                        });
        Thread thread2 =
                new Thread(
                        () -> {
                            testMDCAdapter.put(key, "value2");
                            latch.countDown();
                            try {
                                latch.await();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            results.put("thread2", testMDCAdapter.get(key));
                        });
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        assertEquals("value1", results.get("thread1"));
        assertEquals("value2", results.get("thread2"));
    }
}
