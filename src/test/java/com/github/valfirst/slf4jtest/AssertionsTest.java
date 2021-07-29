package com.github.valfirst.slf4jtest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class AssertionsTest {

    @Test
    void canCreateTestLoggerAssert(@Mock TestLogger testLogger) {
        TestLoggerAssert actual = Assertions.assertThat(testLogger);

        assertThat(actual).isNotNull();
    }
}
