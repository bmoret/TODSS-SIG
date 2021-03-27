package com.snafu.todss.sig.sessies.presentation.dto.response;

import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.person.enums.Branch;
import com.snafu.todss.sig.sessies.domain.person.enums.Role;

import java.time.LocalDate;

public class PersonResponse {
    private final Long id;
    private final String email, firstname, lastname, expertise;
    private final LocalDate employedSince;
    private final Long supervisorId;
    private final String supervisorName;
    private final Branch branch;
    private final Role role;

    public PersonResponse(Person person) {
        this.id = person.getId();
        this.email = person.getEmail();
        this.firstname = person.getFirstname();
        this.lastname = person.getLastname();
        this.expertise = person.getExpertise();
        this.employedSince = person.getEmployedSince();
        this.supervisorId = person.getSupervisor().getId();
        this.supervisorName = person.getSupervisor().getLastname()+", "+person.getSupervisor().getFirstname();
        this.branch = person.getBranch();
        this.role = person.getRole();
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getExpertise() {
        return expertise;
    }

    public LocalDate getEmployedSince() {
        return employedSince;
    }

    public Long getSupervisorId() {
        return supervisorId;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public Branch getBranch() {
        return branch;
    }

    public Role getRole() {
        return role;
    }
}
