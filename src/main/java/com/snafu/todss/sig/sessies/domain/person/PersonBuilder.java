package com.snafu.todss.sig.sessies.domain.person;

import com.snafu.todss.sig.sessies.domain.person.enums.*;

import java.time.LocalDate;
import java.util.ArrayList;

public class PersonBuilder {
    private PersonDetails details;

    public PersonBuilder() {
        this.details = new PersonDetails();
    }

    public PersonBuilder setEmail(String email) {
        this.details.setEmail(email);
        return this;
    }

    public PersonBuilder setFirstname(String firstname) {
        this.details.setFirstname(firstname);
        return this;
    }

    public PersonBuilder setLastname(String lastname) {
        this.details.setLastname(lastname);
        return this;
    }

    public PersonBuilder setExpertise(String expertise) {
        this.details.setExpertise(expertise);
        return this;
    }

    public PersonBuilder setEmployedSince(LocalDate employedSince) {
        this.details.setEmployedSince(employedSince);
        return this;
    }

    public PersonBuilder setSupervisor(Person supervisor) {
        this.details.setSupervisor(supervisor);
        return this;
    }

    public PersonBuilder setBranch(Branch branch) {
        this.details.setBranch(branch);
        return this;
    }

    public PersonBuilder setRole(Role role) {
        this.details.setRole(role);
        return this;
    }

    public Person build() {
        return new Person(
                this.details,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }
}
