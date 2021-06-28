package com.snafu.todss.sig.sessies.domain.session;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SessionDetailsTest {
    private static SessionDetails details;

    @BeforeEach
    void setup() {
        details = new SessionDetails(
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                "subject",
                "Description"
        );
    }

    @Test
    @DisplayName("creating SessionDetails does not throw")
    void sessionDetailsConstructor() {
        assertDoesNotThrow(
                () -> new SessionDetails(
                        LocalDateTime.now(),
                        LocalDateTime.now().plusHours(1),
                        "subject",
                        "Description"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideCorrectDateValues")
    @DisplayName("Set correct end dates")
    void setEndDate(LocalDateTime endDate) {
        assertDoesNotThrow(() -> details.setEndDate(endDate));
    }

    static  Stream<Arguments> provideCorrectDateValues() {
        return Stream.of(
                Arguments.of(LocalDateTime.now().plusSeconds(5)),
                Arguments.of(LocalDateTime.now().plusMinutes(1)),
                Arguments.of(LocalDateTime.now().plusHours(1)),
                Arguments.of(LocalDateTime.now().plusDays(1)),
                Arguments.of(LocalDateTime.now().plusDays(7).minusSeconds(5)),
                Arguments.of(LocalDateTime.now().plusWeeks(1).minusSeconds(5))
        );
    }

    @ParameterizedTest
    @MethodSource("provideCorrectDateValues")
    @DisplayName("Set end date and get the same back")
    void setAndGetSameEndDate(LocalDateTime endDate) {
        details.setEndDate(endDate);
        assertEquals(endDate, details.getEndDate());
    }

    @ParameterizedTest
    @MethodSource("provideCorrectDateValues")
    @DisplayName("Set correct end dates when the start date is null")
    void setEndDateWhenStartDateIsNull(LocalDateTime endDate) {
        details = new SessionDetails(
                null,
                LocalDateTime.now().plusHours(1),
                "subject",
                "Description"
        );
        assertDoesNotThrow(() -> details.setEndDate(endDate));
    }

    @ParameterizedTest
    @MethodSource("provideCorrectDateValues")
    @DisplayName("Set end date when the start date is null and get the same end date")
    void setEndDateWhenStartDateIsNullAndGetEndDate(LocalDateTime endDate) {
        details = new SessionDetails(
                null,
                LocalDateTime.now().plusHours(1),
                "subject",
                "Description"
        );
        details.setEndDate(endDate);
        assertEquals(endDate, details.getEndDate());
    }

    @Test
    @DisplayName("Throw when set end date with null")
    void throwWhenEndDateIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> details.setEndDate(null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidStartDateValues")
    @DisplayName("Set correct start dates")
    void sessionDetailsSetStartDate(LocalDateTime startDate) {
        assertDoesNotThrow(() -> details.setStartDate(startDate));
    }
    static Stream<Arguments> provideValidStartDateValues() {
        return Stream.of(
                Arguments.of(LocalDateTime.now().plusHours(1).minusSeconds(1)),
                Arguments.of(LocalDateTime.now()),
                Arguments.of(LocalDateTime.now().minusSeconds(1)),
                Arguments.of(LocalDateTime.now().minusDays(1)),
                Arguments.of(LocalDateTime.now().minusDays(7).plusHours(2))
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidStartDateValues")
    @DisplayName("Set start date and get same start date")
    void setAndGetStartDate(LocalDateTime startDate) {
        details.setStartDate(startDate);
        assertEquals(startDate, details.getStartDate());
    }

    @ParameterizedTest
    @MethodSource("provideValidStartDateValues")
    @DisplayName("Set correct start date when the end date is null")
    void setStartDateWhenEndDateIsNull(LocalDateTime startDate) {
        details = new SessionDetails(
                LocalDateTime.now(),
                null,
                "subject",
                "Description"
        );
        assertDoesNotThrow(() -> details.setStartDate(startDate));
    }

    @ParameterizedTest
    @MethodSource("provideValidStartDateValues")
    @DisplayName("Set start date when the end date is null and get same start date")
    void setStartDateWhenEndDateIsNullAndGetSameStartDate(LocalDateTime startDate) {
        details = new SessionDetails(
                LocalDateTime.now(),
                null,
                "subject",
                "Description"
        );

        details.setStartDate(startDate);

        assertEquals(startDate, details.getStartDate());
    }

    @Test
    @DisplayName("Throw when set start date with null")
    void throwWhenStartDateIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> details.setStartDate(null)
        );
    }
}