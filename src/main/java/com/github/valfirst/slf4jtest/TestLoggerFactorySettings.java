package com.github.valfirst.slf4jtest;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Annotation that can configure TestLoggerFactory as invoked by the {@link
 * TestLoggerFactoryExtension}.
 */
@ExtendWith(TestLoggerFactoryExtension.class)
@Inherited
@Retention(RUNTIME)
public @interface TestLoggerFactorySettings {

    /**
     * Configure when {@link TestLoggerFactory} is cleaned up
     *
     * @return The cleanup stage to configure, by default {@link CleanupStage#BEFORE_TEST_EXECUTION}
     */
    CleanupStage cleanupStage() default CleanupStage.BEFORE_TEST_EXECUTION;
}
