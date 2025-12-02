package com.github.valfirst.slf4jtest;

import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

import java.util.Optional;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * @author Valery Yatsynovich
 */
public class TestLoggerFactoryExtension implements BeforeEachCallback, BeforeTestExecutionCallback {

    private final CleanupStage cleanupStage;

    public TestLoggerFactoryExtension() {
        this(CleanupStage.BEFORE_TEST_EXECUTION);
    }

    public TestLoggerFactoryExtension(CleanupStage cleanupStage) {
        this.cleanupStage = cleanupStage;
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        if (calculateCleanupStage(context) == CleanupStage.BEFORE_EACH) {
            TestLoggerFactory.clearAll();
        }
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        if (calculateCleanupStage(context) == CleanupStage.BEFORE_TEST_EXECUTION) {
            TestLoggerFactory.clearAll();
        }
    }

    private CleanupStage calculateCleanupStage(ExtensionContext context) {
        // Inspired by Mockito
        return this.retrieveAnnotationFromTestClasses(context)
                .map(TestLoggerFactorySettings::cleanupStage)
                .orElse(cleanupStage);
    }

    private Optional<TestLoggerFactorySettings> retrieveAnnotationFromTestClasses(
            final ExtensionContext context) {
        ExtensionContext currentContext = context;
        Optional<TestLoggerFactorySettings> annotation;

        do {
            annotation = findAnnotation(currentContext.getElement(), TestLoggerFactorySettings.class);

            if (!currentContext.getParent().isPresent()) {
                break;
            }

            currentContext = currentContext.getParent().get();
        } while (!annotation.isPresent() && currentContext != context.getRoot());

        return annotation;
    }
}
