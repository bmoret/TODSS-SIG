package com.snafu.todss.sig.sessies.domain.person;

import com.snafu.todss.sig.sessies.presentation.dto.request.SearchRequest;
import javassist.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.snafu.todss.sig.sessies.domain.*;
import com.snafu.todss.sig.sessies.domain.person.enums.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    Person person;
    Attendance attendance;
    SpecialInterestGroup specialInterestGroup;

    @BeforeEach
    void setup() {
        PersonBuilder pb = new PersonBuilder();

        pb.setEmail("email@email.com");
        pb.setFirstname("first");
        pb.setLastname("last");
        pb.setExpertise("none");
        pb.setEmployedSince(LocalDate.of(2021,1,1));
        pb.setBranch(Branch.VIANEN);
        pb.setRole(Role.MANAGER);

        person = pb.build();
    }

    @Test
    void attendance() {
        assertEquals(0, person.getAttendance().size());

        assertTrue(person.addAttendance(attendance));

        assertEquals(attendance, person.getAttendance().get(0));
        assertEquals(1, person.getAttendance().size());

        assertTrue(person.removeAttendance(attendance));

        assertEquals(0, person.getAttendance().size());
    }

    @Test
    void attendanceDouble() {
        assertTrue(person.addAttendance(attendance));
        assertFalse(person.addAttendance(attendance));
    }

    @Test
    void manager() {
        assertEquals(0, person.getManagedSpecialInterestGroups().size());

        assertTrue(person.addManager(specialInterestGroup));

        assertEquals(specialInterestGroup, person.getManagedSpecialInterestGroups().get(0));
        assertEquals(1, person.getManagedSpecialInterestGroups().size());

        assertTrue(person.removeManager(specialInterestGroup));

        assertEquals(0, person.getManagedSpecialInterestGroups().size());
    }

   @Test
   void managerDouble() {
       assertTrue(person.addManager(specialInterestGroup));
       assertFalse(person.addManager(specialInterestGroup));

   }

    @Test
    void organizer() {
        assertEquals(0, person.getOrganisedSpecialInterestGroups().size());

        assertTrue(person.addOrganizer(specialInterestGroup));

        assertEquals(specialInterestGroup, person.getOrganisedSpecialInterestGroups().get(0));
        assertEquals(1, person.getOrganisedSpecialInterestGroups().size());

        assertTrue(person.removeOrganizer(specialInterestGroup));

        assertEquals(0, person.getOrganisedSpecialInterestGroups().size());
    }

    @Test
    void organizerDouble() {
        assertTrue(person.addOrganizer(specialInterestGroup));
        assertFalse(person.addOrganizer(specialInterestGroup));
    }

    @Test
    void testEquals() {
        assertEquals(person, person);
    }

    @Test
    void testEqualsNull() {
        assertNotNull(person);
    }

    @Test
    void testEqualsNotPerson() {
        assertNotEquals(new Attendance(), person);
    }

    @Test
    void testEqualsSameDetails() {
        PersonBuilder pb = new PersonBuilder();

        pb.setEmail("email@email.com");
        pb.setFirstname("first");
        pb.setLastname("last");
        pb.setExpertise("none");
        pb.setEmployedSince(LocalDate.of(2021,1,1));
        pb.setBranch(Branch.VIANEN);
        pb.setRole(Role.MANAGER);

        Person testPerson = pb.build();
        assertEquals(testPerson, person);
    }

    @Test
    void testEqualsDiffrent() {
        PersonBuilder pb = new PersonBuilder();

        pb.setEmail("email@email.com");
        pb.setFirstname("second");
        pb.setLastname("last");
        pb.setExpertise("none");
        pb.setEmployedSince(LocalDate.of(2021,1,1));
        pb.setBranch(Branch.VIANEN);
        pb.setRole(Role.MANAGER);

        Person testPerson = pb.build();
        assertNotEquals(testPerson, person);
    }
}