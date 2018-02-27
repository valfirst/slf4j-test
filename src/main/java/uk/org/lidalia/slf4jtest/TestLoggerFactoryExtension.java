package uk.org.lidalia.slf4jtest;

import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * @author Valery Yatsynovich
 */
public class TestLoggerFactoryExtension implements BeforeTestExecutionCallback {

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) {
        TestLoggerFactory.clear();
    }
}
