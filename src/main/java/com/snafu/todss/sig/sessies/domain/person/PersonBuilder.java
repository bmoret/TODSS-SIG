package com.snafu.todss.sig.sessies.domain.person;

import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;

import com.snafu.todss.sig.sessies.domain.person.enums.*;

import java.time.LocalDate;
import java.util.List;

public class PersonBuilder {
    private String email;
    private String firstname;
    private String lastname;
    private String expertise;
    private LocalDate employedSince;
    private Person supervisor;
    private Branch branch;
    private Role role;

    public PersonBuilder() {
    }

    public PersonBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public PersonBuilder setFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public PersonBuilder setLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public PersonBuilder setExpertise(String expertise) {
        this.expertise = expertise;
        return this;
    }

    public PersonBuilder setEmployedSince(LocalDate employedSince) {
        this.employedSince = employedSince;
        return this;
    }

    public PersonBuilder setSupervisor(Person supervisor) {
        this.supervisor = supervisor;
        return this;
    }

    public PersonBuilder setBranch(Branch branch) {
        this.branch = branch;
        return this;
    }

    public PersonBuilder setRole(Role role) {
        this.role = role;
        return this;
    }

    public Person build() {
        return new Person(
                this.email,
                this.firstname,
                this.lastname,
                this.expertise,
                this.employedSince,
                this.supervisor,
                this.branch,
                this.role
        );
    }
}
