package com.github.valfirst.slf4jtest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssertionsTest {

    @Test
    void canCreateTestLoggerAssert(@Mock TestLogger testLogger) {
        TestLoggerAssert actual = Assertions.assertThat(testLogger);

        assertThat(actual).isNotNull();
    }
}
