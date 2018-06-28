package com.github.valfirst.slf4jtest;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

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
    public void propertyNotInEither() {
        final String defaultValue = "sensible_default";
        OverridableProperties properties = new OverridableProperties(PROPERTY_SOURCE_NAME);
        assertEquals(defaultValue, properties.getProperty("notpresent", defaultValue));
    }

    @Test
    public void propertyInFileNotInSystemProperties() {
        OverridableProperties properties = new OverridableProperties(PROPERTY_SOURCE_NAME);
        assertEquals("file value", properties.getProperty("infile", "default"));
    }

    @Test
    public void propertyNotInFileInSystemProperties() {
        final String expectedValue = "system value";
        System.setProperty(PROPERTY_SOURCE_NAME + "." + PROPERTY_IN_SYSTEM_PROPS, expectedValue);

        OverridableProperties properties = new OverridableProperties(PROPERTY_SOURCE_NAME);
        assertEquals(expectedValue, properties.getProperty(PROPERTY_IN_SYSTEM_PROPS, "default"));
    }

    @Test
    public void propertyInBothFileAndSystemProperties() {
        final String expectedValue = "system value";
        System.setProperty(PROPERTY_SOURCE_NAME + "." + PROPERTY_IN_BOTH, expectedValue);

        OverridableProperties properties = new OverridableProperties(PROPERTY_SOURCE_NAME);
        assertEquals(expectedValue, properties.getProperty(PROPERTY_IN_BOTH, "default"));
    }

    @Test
    public void noPropertyFile() {
        OverridableProperties properties = new OverridableProperties("no-property-file");

        final String defaultValue = "sensible_default";
        assertEquals(defaultValue, properties.getProperty("blah", defaultValue));
    }

    @Test
    @PrepareForTest(OverridableProperties.class)
    public void ioExceptionLoadingProperties() throws IOException {
        final IOException ioException = new IOException();
        final InputStream inputStreamMock = mock(InputStream.class);
        mockPropertyFileInputStreamToBe(inputStreamMock);
        when(inputStreamMock.read(any(byte[].class))).thenThrow(ioException);

        final UncheckedIOException actual = assertThrows(UncheckedIOException.class,
                () -> new OverridableProperties(PROPERTY_SOURCE_NAME));
        assertEquals(ioException, actual.getCause());
    }

    @Test
    @PrepareForTest(OverridableProperties.class)
    public void ioExceptionClosingPropertyStream() throws IOException {
        final IOException ioException = new IOException();
        final InputStream inputStreamMock = mock(InputStream.class);
        mockPropertyFileInputStreamToBe(inputStreamMock);
        doThrow(ioException).when(inputStreamMock).close();

        final UncheckedIOException actual = assertThrows(UncheckedIOException.class,
                () -> new OverridableProperties(PROPERTY_SOURCE_NAME));
        assertEquals(ioException, actual.getCause());
    }

    @Test
    @PrepareForTest(OverridableProperties.class)
    public void ioExceptionLoadingAndClosingPropertyStream() throws IOException {
        final IOException loadException = new IOException("exception on load");
        final IOException closeException = new IOException("exception on close");
        final InputStream inputStreamMock = mock(InputStream.class);
        mockPropertyFileInputStreamToBe(inputStreamMock);
        when(inputStreamMock.read(any(byte[].class))).thenThrow(loadException);
        doThrow(closeException).when(inputStreamMock).close();

        final UncheckedIOException finalException = assertThrows(UncheckedIOException.class,
                () -> new OverridableProperties(PROPERTY_SOURCE_NAME));
        assertThat(finalException.getCause(), sameInstance(loadException));
        assertArrayEquals(new Throwable[]{closeException}, finalException.getCause().getSuppressed());
    }

    private void mockPropertyFileInputStreamToBe(InputStream inputStream) {
        mockStatic(Thread.class);
        Thread threadMock = mock(Thread.class);
        when(Thread.currentThread()).thenReturn(threadMock);
        ClassLoader classLoaderMock = mock(ClassLoader.class);
        when(threadMock.getContextClassLoader()).thenReturn(classLoaderMock);

        when(classLoaderMock.getResourceAsStream(PROPERTY_SOURCE_NAME + ".properties"))
                .thenReturn(inputStream);
    }
}
