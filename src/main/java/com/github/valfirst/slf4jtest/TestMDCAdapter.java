package com.github.valfirst.slf4jtest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.helpers.BasicMDCAdapter;

public class TestMDCAdapter extends BasicMDCAdapter {

    private final ThreadLocal<Map<String, String>> value = ThreadLocal.withInitial(HashMap::new);

    @Override
    public void put(final String key, final String val) {
        value.get().put(key, String.valueOf(val));
    }

    @Override
    public String get(final String key) {
        return value.get().get(key);
    }

    @Override
    public void remove(final String key) {
        value.get().remove(key);
    }

    @Override
    public void clear() {
        value.get().clear();
        value.remove();
    }

    @Override
    public Map<String, String> getCopyOfContextMap() {
        return Collections.unmodifiableMap(new HashMap<>(value.get()));
    }

    @Override
    public void setContextMap(final Map<String, String> contextMap) {
        value.set(new HashMap<>(contextMap));
    }
}
