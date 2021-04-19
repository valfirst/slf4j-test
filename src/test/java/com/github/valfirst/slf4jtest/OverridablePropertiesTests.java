package com.github.valfirst.slf4jtest;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OverridablePropertiesTests {

    private static final String PROPERTY_SOURCE_NAME = "test";
    private static final String PROPERTY_IN_BOTH = "bothprop";
    private static final String PROPERTY_IN_SYSTEM_PROPS = "sysprop";

    @AfterEach
    public void resetLoggerFactory() {
        System.getProperties().remove(PROPERTY_SOURCE_NAME + "." + PROPERTY_IN_BOTH);
        System.getProperties().remove(PROPERTY_SOURCE_NAME + "." + PROPERTY_IN_SYSTEM_PROPS);
    }

    @Test
    void propertyNotInEither() throws IOException {
        final String defaultValue = "sensible_default";
        OverridableProperties properties = new OverridableProperties(PROPERTY_SOURCE_NAME);
        assertEquals(defaultValue, properties.getProperty("notpresent", defaultValue));
    }

    @Test
    void propertyInFileNotInSystemProperties() throws IOException {
        OverridableProperties properties = new OverridableProperties(PROPERTY_SOURCE_NAME);
        assertEquals("file value", properties.getProperty("infile", "default"));
    }

    @Test
    void propertyNotInFileInSystemProperties() throws IOException {
        final String expectedValue = "system value";
        System.setProperty(PROPERTY_SOURCE_NAME + "." + PROPERTY_IN_SYSTEM_PROPS, expectedValue);

        OverridableProperties properties = new OverridableProperties(PROPERTY_SOURCE_NAME);
        assertEquals(expectedValue, properties.getProperty(PROPERTY_IN_SYSTEM_PROPS, "default"));
    }

    @Test
    void propertyInBothFileAndSystemProperties() throws IOException {
        final String expectedValue = "system value";
        System.setProperty(PROPERTY_SOURCE_NAME + "." + PROPERTY_IN_BOTH, expectedValue);

        OverridableProperties properties = new OverridableProperties(PROPERTY_SOURCE_NAME);
        assertEquals(expectedValue, properties.getProperty(PROPERTY_IN_BOTH, "default"));
    }

    @Test
    void noPropertyFile() throws IOException {
        OverridableProperties properties = new OverridableProperties("no-property-file");

        final String defaultValue = "sensible_default";
        assertEquals(defaultValue, properties.getProperty("blah", defaultValue));
    }
}
