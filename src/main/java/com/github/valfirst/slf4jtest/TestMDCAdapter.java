package com.github.valfirst.slf4jtest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.MDC;
import org.slf4j.helpers.BasicMDCAdapter;

public class TestMDCAdapter extends BasicMDCAdapter {

    private final ThreadLocal<Map<String, String>> value;
    private final boolean initialEnable;
    private final boolean initialInherit;
    private final boolean initialReturnNullCopyWhenMdcNotSet;
    private final boolean initialAllowNullValues;
    private volatile boolean enable;
    private volatile boolean inherit;
    private volatile boolean returnNullCopyWhenMdcNotSet;
    private volatile boolean allowNullValues;

    public TestMDCAdapter() {
        this(OverridableProperties.createUnchecked("slf4jtest"));
    }

    TestMDCAdapter(OverridableProperties properties) {
        enable = initialEnable = getBooleanProperty(properties, "mdc.enable", true);
        inherit = initialInherit = getBooleanProperty(properties, "mdc.inherit", false);
        returnNullCopyWhenMdcNotSet =
                initialReturnNullCopyWhenMdcNotSet =
                        getBooleanProperty(properties, "mdc.return.null.copy.when.mdc.not.set", false);
        allowNullValues =
                initialAllowNullValues = getBooleanProperty(properties, "mdc.allow.null.values", true);

        value =
                new InheritableThreadLocal<Map<String, String>>() {
                    @Override
                    protected Map<String, String> childValue(Map<String, String> parentValue) {
                        if (enable && inherit && parentValue != null) {
                            return new HashMap<>(parentValue);
                        } else {
                            return null;
                        }
                    }
                };
    }

    static boolean getBooleanProperty(
            OverridableProperties properties, String propertyKey, boolean defaultValue) {
        return Boolean.valueOf(properties.getProperty(propertyKey, String.valueOf(defaultValue)));
    }

    @Override
    public void put(final String key, final String val) {
        if (!enable) return;
        if (key == null) throw new IllegalArgumentException("key cannot be null");
        if (val == null && !allowNullValues) throw new IllegalArgumentException("val cannot be null");
        Map<String, String> map = value.get();
        if (map == null) {
            map = new HashMap<>();
            value.set(map);
        }
        map.put(key, val);
    }

    @Override
    public String get(final String key) {
        if (!enable) return null;
        if (key == null) throw new IllegalArgumentException("key cannot be null");
        Map<String, String> map = value.get();
        if (map != null) {
            return map.get(key);
        } else {
            return null;
        }
    }

    @Override
    public void remove(final String key) {
        if (!enable) return;
        if (key == null) throw new IllegalArgumentException("key cannot be null");
        Map<String, String> map = value.get();
        if (map != null) map.remove(key);
    }

    @Override
    public void clear() {
        if (!enable) return;
        Map<String, String> map = value.get();
        if (map == null) return;
        map.clear();
        value.remove();
    }

    /**
     * Return a copy of the current thread's context map. {@code null} is returned if
     *
     * <ul>
     *   <li>The MDC functionality is disabled, c.f. <code>setEnable</code>, or
     *   <li>"return null when empty" is enabled, c.f. <code>setReturnNullCopyWhenMdcNotSet</code>,
     *       and <code>put</code> has not been called.
     * </ul>
     *
     * @return A copy of the current thread's context map.
     */
    @Override
    public Map<String, String> getCopyOfContextMap() {
        if (!enable) return null;
        Map<String, String> map = value.get();
        if (map == null) {
            if (returnNullCopyWhenMdcNotSet) {
                return null;
            } else {
                return new HashMap<>();
            }
        } else {
            return new HashMap<>(map);
        }
    }

    // Internal access
    Map<String, String> getContextMap() {
        Map<String, String> map = value.get();
        return map == null ? Collections.emptySortedMap() : map;
    }

    @Override
    public void setContextMap(final Map<String, String> contextMap) {
        if (!enable) return;
        clear();
        if (contextMap == null) return;
        if (contextMap.keySet().contains(null))
            throw new IllegalArgumentException("key cannot be null");
        if (!allowNullValues && contextMap.values().contains(null))
            throw new IllegalArgumentException("val cannot be null");
        value.set(new HashMap<>(contextMap));
    }

    /**
     * Enable the MDC functionality for all threads.
     *
     * @param enable Whether to enable the MDC functionality. The default value is {@code true}.
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    /**
     * Define whether child threads inherit a copy of the MDC from its parent thread. Note that the
     * copy is taken when the {@link Thread} is constructed. This affects all threads.
     *
     * @param inherit Whether to enable inheritance. The default value is {@code false}.
     */
    public void setInherit(boolean inherit) {
        this.inherit = inherit;
    }

    /**
     * Define whether null values are allowed in the MDC. This affects all threads.
     *
     * @param allowNullValues Whether to allow nulls. The default value is {@code true}.
     */
    public void setAllowNullValues(boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }

    /**
     * Define whether {@link #getCopyOfContextMap} returns {@code null} when no values have been set.
     * This affects all threads.
     *
     * @param returnNullCopyWhenMdcNotSet Whether to return null. The default value is {@code false}.
     *     If {@code false}, an empty map is returned instead.
     */
    public void setReturnNullCopyWhenMdcNotSet(boolean returnNullCopyWhenMdcNotSet) {
        this.returnNullCopyWhenMdcNotSet = returnNullCopyWhenMdcNotSet;
    }

    /**
     * Whether the MDC functionality is enabled.
     *
     * @return Whether the MDC functionality is enabled.
     */
    public boolean getEnable() {
        return enable;
    }

    /**
     * Whether child threads inherit a copy of the MDC from its parent thread.
     *
     * @return Whether inheritance is enabled.
     */
    public boolean getInherit() {
        return inherit;
    }

    /**
     * Whether null values are allowed in the MDC.
     *
     * @return Whether nulls are allowed.
     */
    public boolean getAllowNullValues() {
        return allowNullValues;
    }

    /**
     * Whether {@link #getCopyOfContextMap} returns {@code null} when no values have been set.
     *
     * @return Whether to return null.
     */
    public boolean getReturnNullCopyWhenMdcNotSet() {
        return returnNullCopyWhenMdcNotSet;
    }

    /**
     * Reset the options to values defined by the static configuration. This undoes to changes made
     * using {@link #setEnable}, {@link #setInherit}, {@link #setAllowNullValues}, and {@link
     * #setReturnNullCopyWhenMdcNotSet}.
     */
    public void restoreOptions() {
        enable = initialEnable;
        inherit = initialInherit;
        returnNullCopyWhenMdcNotSet = initialReturnNullCopyWhenMdcNotSet;
        allowNullValues = initialAllowNullValues;
    }

    /** Access the current MDC adapter. Used to call the option setting methods. */
    @SuppressWarnings("unchecked")
    public static TestMDCAdapter getInstance() {
        return (TestMDCAdapter) MDC.getMDCAdapter();
    }
}
