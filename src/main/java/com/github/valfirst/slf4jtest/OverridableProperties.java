package com.github.valfirst.slf4jtest;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

class OverridableProperties {
    private static final Properties EMPTY_PROPERTIES = new Properties();
    private final String propertySourceName;
    private final Properties properties;

    OverridableProperties(final String propertySourceName) throws IOException {
        this.propertySourceName = propertySourceName;
        this.properties = getProperties();
    }

    private Properties getProperties() throws IOException {
        InputStream resourceAsStream =
                Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream(propertySourceName + ".properties");
        if (resourceAsStream != null) {
            return loadProperties(resourceAsStream);
        }
        return EMPTY_PROPERTIES;
    }

    private static Properties loadProperties(InputStream propertyResource) throws IOException {
        try (InputStream closablePropertyResource = propertyResource) {
            final Properties loadedProperties = new Properties();
            loadedProperties.load(closablePropertyResource);
            return loadedProperties;
        }
    }

    String getProperty(final String propertyKey, final String defaultValue) {
        final String propertyFileProperty = properties.getProperty(propertyKey, defaultValue);
        return System.getProperty(propertySourceName + "." + propertyKey, propertyFileProperty);
    }

    public static OverridableProperties createUnchecked(final String propertySourceName) {
        try {
            return new OverridableProperties(propertySourceName);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
