package com.snafu.todss.sig.sessies.presentation.dto.response;

import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.person.PersonDetails;
import com.snafu.todss.sig.sessies.domain.person.enums.Branch;
import com.snafu.todss.sig.sessies.domain.person.enums.Role;

import java.time.LocalDate;
import java.util.UUID;

public class PersonResponse {
    private final UUID id;
    private final String email;
    private final String firstname;
    private final String lastname;
    private final String expertise;
    private final LocalDate employedSince;
    private final PersonCompactResponse supervisor;
    private final Branch branch;
    private final Role role;

    public PersonResponse(UUID id, Person supervisor, PersonDetails details) {
        this.id = id;
        this.email = details.getEmail();
        this.firstname = details.getFirstname();
        this.lastname = details.getLastname();
        this.expertise = details.getExpertise();
        this.employedSince = details.getEmployedSince();
        this.branch = details.getBranch();
        this.role = details.getRole();

        if (supervisor != null) {
            PersonDetails supervisorDetails = supervisor.getDetails();
            this.supervisor = new PersonCompactResponse(
                    supervisor.getId(),
                    String.format("%s, %s",supervisorDetails.getLastname(), supervisorDetails.getFirstname() )
            );
        } else {
            this.supervisor = null;
        }
    }

    public UUID getId() {
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

    public PersonCompactResponse getSupervisor() {
        return supervisor;
    }

    public Branch getBranch() {
        return branch;
    }

    public Role getRole() {
        return role;
    }
}
