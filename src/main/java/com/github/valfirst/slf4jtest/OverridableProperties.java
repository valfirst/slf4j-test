package com.github.valfirst.slf4jtest;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.Properties;

class OverridableProperties {
    private static final Properties EMPTY_PROPERTIES = new Properties();
    private final String propertySourceName;
    private final Properties properties;

    OverridableProperties(final String propertySourceName) {
        this.propertySourceName = propertySourceName;
        this.properties = getProperties();
    }

    private Properties getProperties() {
        return Optional.ofNullable(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(propertySourceName + ".properties"))
                .map(OverridableProperties::loadProperties)
                .orElse(EMPTY_PROPERTIES);
    }

    private static Properties loadProperties(InputStream propertyResource) {
        try (InputStream closablePropertyResource = propertyResource) {
            final Properties loadedProperties = new Properties();
            loadedProperties.load(closablePropertyResource);
            return loadedProperties;
        } catch (IOException ioException) {
            throw new UncheckedIOException(ioException);
        }
    }

    String getProperty(final String propertyKey, final String defaultValue) {
        final String propertyFileProperty = properties.getProperty(propertyKey, defaultValue);
        return System.getProperty(propertySourceName + "." + propertyKey, propertyFileProperty);
    }
}
