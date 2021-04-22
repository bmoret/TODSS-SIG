package com.snafu.todss.sig.sessies.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FeedbackTest {


    @Test
    @DisplayName("Valid constructor creates instance")
    void constructorCorrect() {
        assertDoesNotThrow(() ->new Feedback("description", null, null));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidDescriptions")
    @DisplayName("Invalid constructor throwsIllegalArgumentException")
    void constructorThrows() {
        assertThrows(
                IllegalArgumentException.class,
                () ->new Feedback("", null, null)
        );
    }
    private static Stream<Arguments> provideInvalidDescriptions() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of(" "),
                Arguments.of((Object) null)
        );
    }
}