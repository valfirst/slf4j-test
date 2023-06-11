package com.github.valfirst.slf4jtest;

import java.util.logging.Level;
import java.util.logging.LogManager;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Configuration to redirect log messages from {@link java.util.logging} to SLF4J.
 *
 * @author Karsten Spang
 */
public class JulConfig {

    /**
     * Redirect all logging from {@link java.util.logging} to SLF4J. If called more than once, it will
     * do nothing on subsequent calls.
     */
    public static void setup() {
        if (!SLF4JBridgeHandler.isInstalled()) {
            synchronized (JulConfig.class) {
                if (!SLF4JBridgeHandler.isInstalled()) {
                    SLF4JBridgeHandler.removeHandlersForRootLogger();
                    SLF4JBridgeHandler.install();
                    LogManager.getLogManager().getLogger("").setLevel(Level.ALL);
                }
            }
        }
    }

    private JulConfig() {}
}
