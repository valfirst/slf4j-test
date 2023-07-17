package com.github.valfirst.slf4jtest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockConstruction;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

class OverridablePropertiesTests {

    private static final String PROPERTY_SOURCE_NAME = "test";
    private static final String PROPERTY_IN_BOTH = "bothprop";
    private static final String PROPERTY_IN_SYSTEM_PROPS = "sysprop";

    @AfterEach
    void resetLoggerFactory() {
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

    @Test
    void ioExceptionAtPropertiesLoading() {
        IOException ioException = new IOException();
        try (MockedConstruction<Properties> ignored =
                mockConstruction(
                        Properties.class,
                        (mock, context) -> doThrow(ioException).when(mock).load(any(InputStream.class)))) {
            UncheckedIOException uncheckedIoException =
                    assertThrows(
                            UncheckedIOException.class, () -> OverridableProperties.createUnchecked("test"));
            assertThat(uncheckedIoException.getCause(), is(ioException));
        }
    }
}
