package com.github.valfirst.slf4jtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.bridge.SLF4JBridgeHandler;

class JulConfigTests {

    private static String message = "message";

    @BeforeEach
    void beforeEach() {
        SLF4JBridgeHandler.uninstall();
    }

    @AfterAll
    static void afterAll() {
        SLF4JBridgeHandler.uninstall();
    }

    @Test
    void testExtension() {
        new JulConfigExtension().beforeAll(null);
        assertTrue(SLF4JBridgeHandler.isInstalled());
    }

    @Test
    void testSetupTwice() {
        JulConfig.setup();
        JulConfig.setup();
        assertTrue(SLF4JBridgeHandler.isInstalled());
    }

    @Test
    void testLogging() {
        JulConfig.setup();
        Logger logger = Logger.getLogger(JulConfigTests.class.getName());
        TestLogger testLogger = TestLoggerFactory.getTestLogger(JulConfigTests.class);
        testLogger.clear();
        logger.fine(message);
        List<LoggingEvent> events = testLogger.getLoggingEvents();
        List<LoggingEvent> expected = Arrays.asList(LoggingEvent.debug(message));
        assertEquals(expected, events);
    }
}
