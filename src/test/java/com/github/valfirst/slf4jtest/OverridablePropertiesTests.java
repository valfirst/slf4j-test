package com.github.valfirst.slf4jtest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.IOException;
import java.io.UncheckedIOException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class OverridablePropertiesTests {

    private static final String PROPERTY_SOURCE_NAME = "test";
    private static final String PROPERTY_IN_BOTH = "bothprop";
    private static final String PROPERTY_IN_SYSTEM_PROPS = "sysprop";

    @After
    public void resetLoggerFactory() {
        System.getProperties().remove(PROPERTY_SOURCE_NAME + "." + PROPERTY_IN_BOTH);
        System.getProperties().remove(PROPERTY_SOURCE_NAME + "." + PROPERTY_IN_SYSTEM_PROPS);
    }

    @Test
    public void propertyNotInEither() throws IOException {
        final String defaultValue = "sensible_default";
        OverridableProperties properties = new OverridableProperties(PROPERTY_SOURCE_NAME);
        assertEquals(defaultValue, properties.getProperty("notpresent", defaultValue));
    }

    @Test
    public void propertyInFileNotInSystemProperties() throws IOException {
        OverridableProperties properties = new OverridableProperties(PROPERTY_SOURCE_NAME);
        assertEquals("file value", properties.getProperty("infile", "default"));
    }

    @Test
    public void propertyNotInFileInSystemProperties() throws IOException {
        final String expectedValue = "system value";
        System.setProperty(PROPERTY_SOURCE_NAME + "." + PROPERTY_IN_SYSTEM_PROPS, expectedValue);

        OverridableProperties properties = new OverridableProperties(PROPERTY_SOURCE_NAME);
        assertEquals(expectedValue, properties.getProperty(PROPERTY_IN_SYSTEM_PROPS, "default"));
    }

    @Test
    public void propertyInBothFileAndSystemProperties() throws IOException {
        final String expectedValue = "system value";
        System.setProperty(PROPERTY_SOURCE_NAME + "." + PROPERTY_IN_BOTH, expectedValue);

        OverridableProperties properties = new OverridableProperties(PROPERTY_SOURCE_NAME);
        assertEquals(expectedValue, properties.getProperty(PROPERTY_IN_BOTH, "default"));
    }

    @Test
    public void noPropertyFile() throws IOException {
        OverridableProperties properties = new OverridableProperties("no-property-file");

        final String defaultValue = "sensible_default";
        assertEquals(defaultValue, properties.getProperty("blah", defaultValue));
    }

    @Test
    @PrepareForTest(OverridableProperties.class)
    public void ioExceptionAtPropertiesLoading() throws Exception {
        IOException ioException = new IOException();
        whenNew(OverridableProperties.class).withArguments(anyString()).thenThrow(ioException);

        try {
            OverridableProperties.createUnchecked("somesource");
            fail("UncheckedIOException was not thrown");
        } catch (UncheckedIOException uncheckedIOException) {
            assertThat(uncheckedIOException.getCause(), is(ioException));
        }
    }
}
