package com.snafu.todss.sig.sessies.domain;

import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.person.PersonBuilder;
import com.snafu.todss.sig.sessies.domain.session.builder.PhysicalSessionBuilder;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpecialInterestGroupTest {
    private SpecialInterestGroup sig;

    @BeforeEach
    void setup() {
        sig = new SpecialInterestGroup("subject", null, new ArrayList<>(), new ArrayList<>());
    }

    @Test
    @DisplayName("Add organiser adds organiser")
    void addOrganizer_AddsOrganizer() {
        Person organizer = new PersonBuilder()
                .setFirstname("firstName")
                .build();
        sig.addOrganizer(organizer);

        assertTrue(sig.getOrganizers().contains(organizer));
    }

    @Test
    @DisplayName("Add organiser when already in sig return false")
    void addAlreadyAddedOrganizer_ReturnsFalse() {
        Person organizer = new PersonBuilder()
                .setFirstname("firstName")
                .build();
        sig.addOrganizer(organizer);

        assertFalse(sig.addOrganizer(organizer));
    }

    @Test
    @DisplayName("Remove organiser removes organiser")
    void removeOrganizer_RemovesOrganizer() {
        Person organizer = new PersonBuilder()
                .setFirstname("firstName")
                .build();
        sig.addOrganizer(organizer);

        sig.removeOrganizer(organizer);

        assertTrue(sig.getOrganizers().isEmpty());
    }

    @Test
    @DisplayName("Add session adds session")
    void addSession_AddsSession() {
        Session session = new PhysicalSessionBuilder()
                .setAddress("address")
                .build();
        sig.addSession(session);

        assertTrue(sig.getSessions().contains(session));
    }

    @Test
    @DisplayName("Add session when already in sig return false")
    void addAlreadyAddedSession_ReturnsFalse() {
        Session session = new PhysicalSessionBuilder()
                .setAddress("address")
                .build();
        sig.addSession(session);

        assertFalse(sig.addSession(session));
    }

    @Test
    @DisplayName("Remove session removes session")
    void removeSession_RemovesSession() {
        Session session = new PhysicalSessionBuilder()
                .setAddress("address")
                .build();
        sig.addSession(session);

        sig.removeSession(session);

        assertTrue(sig.getSessions().isEmpty());
    }

}