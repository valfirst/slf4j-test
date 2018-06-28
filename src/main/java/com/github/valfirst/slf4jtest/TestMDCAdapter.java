package com.github.valfirst.slf4jtest;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.spi.MDCAdapter;

import com.google.common.collect.ImmutableMap;

public class TestMDCAdapter implements MDCAdapter {

    private final ThreadLocal<Map<String, String>> value = ThreadLocal.withInitial(HashMap::new);

    public void put(final String key, final String val) {
        value.get().put(key, String.valueOf(val));
    }

    public String get(final String key) {
        return value.get().get(key);
    }

    public void remove(final String key) {
        value.get().remove(key);
    }

    public void clear() {
        value.get().clear();
    }

    public ImmutableMap<String, String> getCopyOfContextMap() {
        return ImmutableMap.copyOf(value.get());
    }

    @SuppressWarnings("unchecked")
    public void setContextMap(final Map contextMap) {
        value.set(new HashMap<String, String>(contextMap));
    }
}
