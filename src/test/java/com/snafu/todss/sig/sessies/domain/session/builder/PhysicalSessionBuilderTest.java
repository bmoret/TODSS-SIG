package com.snafu.todss.sig.sessies.domain.session.builder;

import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.session.SessionDetails;
import com.snafu.todss.sig.sessies.domain.session.SessionState;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PhysicalSessionBuilderTest {
    private PhysicalSessionBuilder builder;

    @BeforeEach
    void setup() {
        builder = new PhysicalSessionBuilder();
    }

    @Test
    @DisplayName("Default, not set builder returns Session of type PhysicalSession")
    void defaultBuild_ReturnsPhysicalSession() {
        Session session = builder.build();

        assertTrue(session instanceof PhysicalSession);
    }

    @Test
    @DisplayName("Default, not set builder returns PhysicalSession with default values")
    void defaultBuild_ReturnsPhysicalSessionWithValues() {
        PhysicalSession session = builder.build();

        assertEquals(SessionState.DRAFT, session.getState());
        assertEquals("", session.getAddress());
    }

    @Test
    @DisplayName("All set builder returns PhysicalSession with all setted attributes")
    void AllSettedBuilder_ReturnsPhysicalSessionWithValues() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlusOneHour = LocalDateTime.now().plusHours(1);
        String subject = "Subject";
        String description = "Description";
        String address = "Address";
        SessionState state = SessionState.DRAFT;
        builder.setStartDate(now);
        builder.setEndDate(nowPlusOneHour);
        builder.setSubject(subject);
        builder.setDescription(description);
        builder.setSig(new SpecialInterestGroup());
        builder.setAddress(address);

        PhysicalSession session = builder.build();

        SessionDetails details = session.getDetails();
        assertEquals(now, details.getStartDate());
        assertEquals(nowPlusOneHour, details.getEndDate());
        assertEquals(subject, details.getSubject());
        assertEquals(description, details.getDescription());
        assertEquals(state, session.getState());
        assertEquals(address, session.getAddress());
    }
}