package com.github.valfirst.slf4jtest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

class TestMDCAdapterTests {

    private final TestMDCAdapter testMDCAdapter = new TestMDCAdapter();
    private final String key = "key";
    private final String value = "value";

    @AfterEach
    void afterEach() {
        testMDCAdapter.restoreOptions();
        testMDCAdapter.clear();
    }

    @Test
    void putGetRemoveLoop() {
        assertNull(testMDCAdapter.get(key));
        testMDCAdapter.remove(key);
        testMDCAdapter.put(key, value);
        assertEquals(value, testMDCAdapter.get(key));
        testMDCAdapter.remove(key);
        assertNull(testMDCAdapter.get(key));
    }

    @Test
    void putGetTwoKeys() {
        testMDCAdapter.put(key, value);
        testMDCAdapter.put("Another", "Value");
        assertEquals(value, testMDCAdapter.get(key));
        assertEquals("Value", testMDCAdapter.get("Another"));
    }

    @Test
    void testPutNullKeyThrowsIllegalArgumentException() {
        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> testMDCAdapter.put(null, value));
        assertEquals("key cannot be null", illegalArgumentException.getMessage());
    }

    @Test
    void testPutNullValueThrowsIllegalArgumentExceptionIfForbidden() {
        testMDCAdapter.setAllowNullValues(false);
        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> testMDCAdapter.put(key, null));
        assertEquals("val cannot be null", illegalArgumentException.getMessage());
    }

    @Test
    void testPutNullValueDoesNotThrowIfAllowed() {
        testMDCAdapter.setAllowNullValues(true);
        assertDoesNotThrow(() -> testMDCAdapter.put(key, null));
    }

    @Test
    void testPutNullKeyValueDoesNotThrowIfNotEnabled() {
        testMDCAdapter.setEnable(false);
        assertDoesNotThrow(() -> testMDCAdapter.put(key, null));
    }

    @Test
    void testGetNullKeyThrowsIllegalArgumentException() {
        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> testMDCAdapter.get(null));
        assertEquals("key cannot be null", illegalArgumentException.getMessage());
    }

    @Test
    void testGetReturnsNullIfNotEnabled() {
        testMDCAdapter.setEnable(false);
        assertNull(testMDCAdapter.get(null));
    }

    @Test
    void removeNullKeyThrowsIllegalArgumentException() {
        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> testMDCAdapter.remove(null));
        assertEquals("key cannot be null", illegalArgumentException.getMessage());
    }

    @Test
    void testRemoveNullKeyDoesNotThrowIfNotEnabled() {
        testMDCAdapter.setEnable(false);
        assertDoesNotThrow(() -> testMDCAdapter.remove(null));
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
    void getCopyOfContextMapReturnsNullWhenSet() {
        testMDCAdapter.setReturnNullCopyWhenMdcNotSet(true);
        assertNull(testMDCAdapter.getCopyOfContextMap());
    }

    @Test
    void getCopyOfContextMapReturnsNullWhenNotEnabled() {
        testMDCAdapter.setEnable(false);
        assertNull(testMDCAdapter.getCopyOfContextMap());
    }

    @Test
    void clear() {
        testMDCAdapter.put(key, value);
        testMDCAdapter.clear();
        assertEquals(Collections.emptyMap(), testMDCAdapter.getCopyOfContextMap());
    }

    @Test
    void clearWhenNotEnabled() {
        testMDCAdapter.setEnable(false);
        testMDCAdapter.clear();
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
    void setContextMapNotEnabled() {
        testMDCAdapter.setEnable(false);
        Map<String, String> newValues = new HashMap<>();
        newValues.put(key, value);
        testMDCAdapter.setContextMap(newValues);
    }

    @Test
    void testElementNullKeyThrowsIllegalArgumentException() {
        final Map<String, String> map = new HashMap<>();
        map.put(null, value);
        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> testMDCAdapter.setContextMap(map));
        assertEquals("key cannot be null", illegalArgumentException.getMessage());
    }

    @Test
    void testElementNullValueThrowsIllegalArgumentExceptionIfForbidden() {
        testMDCAdapter.setAllowNullValues(false);
        final Map<String, String> map = new HashMap<>();
        map.put(key, null);
        IllegalArgumentException illegalArgumentException =
                assertThrows(IllegalArgumentException.class, () -> testMDCAdapter.setContextMap(map));
        assertEquals("val cannot be null", illegalArgumentException.getMessage());
    }

    @Test
    void testElementNullValueDoesNotThrowIfAllowed() {
        testMDCAdapter.setAllowNullValues(true);
        final Map<String, String> map = new HashMap<>();
        map.put(key, null);
        assertDoesNotThrow(() -> testMDCAdapter.setContextMap(map));
    }

    @Test
    void testElementNonNullValueDoesNotThrowIfForbidden() {
        testMDCAdapter.setAllowNullValues(false);
        final Map<String, String> map = new HashMap<>();
        map.put(key, value);
        assertDoesNotThrow(() -> testMDCAdapter.setContextMap(map));
    }

    @Test
    void testSetNullMapClearsMdc() {
        testMDCAdapter.put(key, value);
        testMDCAdapter.setContextMap(null);
        assertEquals(Collections.emptyMap(), testMDCAdapter.getCopyOfContextMap());
    }

    @Test
    void testMdcAdapterIsThreadLocal() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(2);
        final Map<String, String> results = Collections.synchronizedMap(new HashMap<>());
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

    @Test
    void testMdcAdapterIsInheritedWhenSet() throws InterruptedException {
        testMDCAdapter.setInherit(true);
        testMDCAdapter.put(key, value);
        final AtomicReference<String> result = new AtomicReference<>();
        Thread thread =
                new Thread(
                        () -> {
                            result.set(testMDCAdapter.get(key));
                        });
        thread.start();
        thread.join();
        assertEquals(value, result.get());
    }

    @Test
    void testMdcAdapterIsNotInheritedWhenNotSet() throws InterruptedException {
        testMDCAdapter.setInherit(false);
        testMDCAdapter.put(key, value);
        final AtomicReference<String> result = new AtomicReference<>(value);
        Thread thread =
                new Thread(
                        () -> {
                            result.set(testMDCAdapter.get(key));
                        });
        thread.start();
        thread.join();
        assertNull(result.get());
    }

    @Test
    void testMdcAdapterNothingIsInheritedWhenSet() throws InterruptedException {
        testMDCAdapter.setInherit(true);
        final AtomicReference<String> result = new AtomicReference<>(value);
        Thread thread =
                new Thread(
                        () -> {
                            result.set(testMDCAdapter.get(key));
                        });
        thread.start();
        thread.join();
        assertNull(result.get());
    }

    @Test
    void testGetInstanceReturnsMDC() {
        assertSame(MDC.getMDCAdapter(), TestMDCAdapter.getInstance());
    }

    @Test
    void testDefaultOptions() {
        assertEquals(
                new TestMDCAdapterOptions(true, false, false, true),
                new TestMDCAdapterOptions(testMDCAdapter));
    }

    @Test
    void testOptionsCanBeSetToTrue() throws Exception {
        final OverridableProperties properties = mock(OverridableProperties.class);
        when(properties.getProperty(anyString(), anyString())).thenReturn("true");
        TestMDCAdapter localTestMDCAdapter = new TestMDCAdapter(properties);
        assertEquals(
                new TestMDCAdapterOptions(true, true, true, true),
                new TestMDCAdapterOptions(localTestMDCAdapter));
    }

    @Test
    void testOptionsCanBeSetToFalse() throws Exception {
        final OverridableProperties properties = mock(OverridableProperties.class);
        when(properties.getProperty(anyString(), anyString())).thenReturn("false");
        TestMDCAdapter localTestMDCAdapter = new TestMDCAdapter(properties);
        assertEquals(
                new TestMDCAdapterOptions(false, false, false, false),
                new TestMDCAdapterOptions(localTestMDCAdapter));
    }

    private static class TestMDCAdapterOptions {
        private final boolean enable;
        private final boolean inherit;
        private final boolean returnNullCopyWhenMdcNotSet;
        private final boolean allowNullValues;

        public TestMDCAdapterOptions(
                boolean enable,
                boolean inherit,
                boolean returnNullCopyWhenMdcNotSet,
                boolean allowNullValues) {
            this.enable = enable;
            this.inherit = inherit;
            this.allowNullValues = allowNullValues;
            this.returnNullCopyWhenMdcNotSet = returnNullCopyWhenMdcNotSet;
        }

        public TestMDCAdapterOptions(TestMDCAdapter testMDCAdapter) {
            this(
                    testMDCAdapter.getEnable(),
                    testMDCAdapter.getInherit(),
                    testMDCAdapter.getReturnNullCopyWhenMdcNotSet(),
                    testMDCAdapter.getAllowNullValues());
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof TestMDCAdapterOptions)) return false;
            TestMDCAdapterOptions that = (TestMDCAdapterOptions) other;
            Object[] ours = {enable, inherit, returnNullCopyWhenMdcNotSet, allowNullValues};
            Object[] theirs = {
                that.enable, that.inherit, that.returnNullCopyWhenMdcNotSet, that.allowNullValues
            };
            return Arrays.equals(ours, theirs);
        }

        @Override
        public int hashCode() {
            Object[] ours = {enable, inherit, returnNullCopyWhenMdcNotSet, allowNullValues};
            return Arrays.hashCode(ours);
        }

        @Override
        public String toString() {
            return "TestMDCAdapterOptions:{"
                    + "enable:"
                    + String.valueOf(enable)
                    + ",inherit:"
                    + String.valueOf(inherit)
                    + ",returnNullCopyWhenMdcNotSet:"
                    + String.valueOf(returnNullCopyWhenMdcNotSet)
                    + ",allowNullValues:"
                    + String.valueOf(allowNullValues)
                    + "}";
        }
    }
}
