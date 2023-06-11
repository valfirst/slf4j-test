package com.github.valfirst.slf4jtest;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * JUnit 5 extension that calls {@link JulConfig#setup()}.
 *
 * @author Karsten Spang
 */
public class JulConfigExtension implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        JulConfig.setup();
    }
}
