package com.snafu.todss.sig.sessies.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.snafu.todss.sig.sessies.util.LevenshteinAlgorithm.calculateLevenshteinDistance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LevenshteinAlgorithmTest {
    private static Stream<Arguments> stringExamples() {
        return Stream.of(
                Arguments.of(
                        "hoi",
                        "h",
                        2
                ),
                Arguments.of(
                        "hoi",
                        "hoiiii",
                        3
                ),
                Arguments.of(
                        "solucim",
                        "cimsolu",
                        6
                ),
                Arguments.of(
                        "cimSolu",
                        "cimsolu",
                        0
                ),
                Arguments.of(
                        "cimSolu",
                        "Cimsolu",
                        0
                ),
                Arguments.of(
                        "CimSolu",
                        "cImsOlu",
                        0
                ),
                Arguments.of(
                        "Cim Solu",
                        "cImsOlu",
                        1
                ),
                Arguments.of(
                        "Plank Van Der Plan",
                        "van der plan",
                        6
                )
        );
    }

    @ParameterizedTest
    @MethodSource("stringExamples")
    @DisplayName("calulateLevenshteinDistance returns correct value")
    void calculateLevenshteinDistanceCheck(String s1,
                                      String s2,
                                      int expectedValue) {
        assertEquals(expectedValue, calculateLevenshteinDistance(s1, s2));
    }

    private static Stream<Arguments> stringNullExamples() {
        return Stream.of(
                Arguments.of(
                        "hoi",
                        null
                ),
                Arguments.of(
                        null,
                        "hoi"
                ),
                Arguments.of(
                        null,
                        null
                ),
                Arguments.of(
                        "hoi",
                        ""
                ),
                Arguments.of(
                        "",
                        "hoi"
                ),
                Arguments.of(
                        "",
                        ""
                )
        );
    }

    @ParameterizedTest
    @MethodSource("stringNullExamples")
    @DisplayName("calculateLevenshteinDistance throws when provided with null")
    void calculateLevenshteinDistanceThrows(String s1,
                                            String s2) {
        assertThrows(
                RuntimeException.class,
                () -> calculateLevenshteinDistance(s1, s2));
    }
}
