package com.github.valfirst.slf4jtest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.helpers.BasicMDCAdapter;

public class TestMDCAdapter extends BasicMDCAdapter {

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
        value.remove();
    }

    public Map<String, String> getCopyOfContextMap() {
        return Collections.unmodifiableMap(new HashMap<>(value.get()));
    }

    public void setContextMap(final Map<String, String> contextMap) {
        value.set(new HashMap<>(contextMap));
    }
}
