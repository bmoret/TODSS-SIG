package com.snafu.todss.sig.sessies.domain.person;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import com.snafu.todss.sig.sessies.domain.person.enums.*;

import static org.junit.jupiter.api.Assertions.*;

class PersonDetailsTest {

    PersonDetails pd;
    String testString;

    @BeforeEach
    void beforeEach() {
        pd = new PersonDetails(
                "woord",
                "woord",
                "woord",
                "woord",
                LocalDate.of(2020,1,1),
                Branch.BEST,
                Role.EMPLOYEE);
        testString = "test";
    }

    @Test
    void email() {
        assertNotNull(pd.getEmail());
        pd.setEmail(testString);
        assertEquals(testString, pd.getEmail());
    }

    @Test
    void firstname() {
        assertNotNull(pd.getFirstname());
        pd.setFirstname(testString);
        assertEquals(testString, pd.getFirstname());
    }

    @Test
    void lastname() {
        assertNotNull(pd.getLastname());
        pd.setLastname(testString);
        assertEquals(testString, pd.getLastname());
    }

    @Test
    void expertise() {
        assertNotNull(pd.getExpertise());
        pd.setExpertise(testString);
        assertEquals(testString, pd.getExpertise());
    }

    @Test
    void employedSince() {
        LocalDate date = LocalDate.of(2021,1,1);
        assertNotNull(pd.getEmployedSince());
        pd.setEmployedSince(date);
        assertEquals(date, pd.getEmployedSince());
    }

    @Test
    void branch() {
        assertNotNull(pd.getBranch());
        pd.setBranch(Branch.VIANEN);
        assertEquals(Branch.VIANEN, pd.getBranch());
    }

    @Test
    void role() {
        assertNotNull(pd.getRole());
        pd.setRole(Role.MANAGER);
        assertEquals(Role.MANAGER, pd.getRole());
    }


    @Test
    void testEquals() {
        assertEquals(pd, pd);
    }

    @Test
    void testEqualsTrue() {
        PersonDetails pdTest = new PersonDetails(
                "woord",
                "woord",
                "woord",
                "woord",
                LocalDate.of(2020,1,1),
                Branch.BEST,
                Role.EMPLOYEE);
        assertEquals(pdTest, pd);
    }

    @Test
    void testEqualsNull() {
        assertNotNull(pd);
    }

    @Test
    void testEqualsFalseEmail() {
        PersonDetails pdTest = new PersonDetails(
                "",
                "woord",
                "woord",
                "woord",
                LocalDate.of(2020,1,1),
                Branch.BEST,
                Role.EMPLOYEE);
        assertNotEquals(pdTest, pd);
    }

    @Test
    void testEqualsFalseFirstname() {
        PersonDetails pdTest = new PersonDetails(
                "woord",
                "",
                "woord",
                "woord",
                LocalDate.of(2020,1,1),
                Branch.BEST,
                Role.EMPLOYEE);
        assertNotEquals(pdTest, pd);
    }

    @Test
    void testEqualsFalseLastname() {
        PersonDetails pdTest = new PersonDetails(
                "woord",
                "woord",
                "",
                "woord",
                LocalDate.of(2020,1,1),
                Branch.BEST,
                Role.EMPLOYEE);
        assertNotEquals(pdTest, pd);
    }

    @Test
    void testEqualsFalseExpertise() {
        PersonDetails pdTest = new PersonDetails(
                "woord",
                "woord",
                "woord",
                "",
                LocalDate.of(2020,1,1),
                Branch.BEST,
                Role.EMPLOYEE);
        assertNotEquals(pdTest, pd);
    }

    @Test
    void testEqualsFalseEmployedSince() {
        PersonDetails pdTest = new PersonDetails(
                "woord",
                "woord",
                "woord",
                "woord",
                LocalDate.of(2021,1,1),
                Branch.BEST,
                Role.EMPLOYEE);
        assertNotEquals(pdTest, pd);
    }

    @Test
    void testEqualsFalseBranch() {
        PersonDetails pdTest = new PersonDetails(
                "woord",
                "woord",
                "woord",
                "woord",
                LocalDate.of(2020,1,1),
                Branch.VIANEN,
                Role.EMPLOYEE);
        assertNotEquals(pdTest, pd);
    }

    @Test
    void testEqualsFalseRole() {
        PersonDetails pdTest = new PersonDetails(
                "woord",
                "woord",
                "woord",
                "woord",
                LocalDate.of(2020,1,1),
                Branch.BEST,
                Role.MANAGER);
        assertNotEquals(pdTest, pd);
    }
}