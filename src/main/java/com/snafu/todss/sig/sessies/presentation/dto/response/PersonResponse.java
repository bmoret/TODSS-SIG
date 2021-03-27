package com.snafu.todss.sig.sessies.presentation.dto.response;

import com.snafu.todss.sig.sessies.domain.person.PersonDetails;
import com.snafu.todss.sig.sessies.domain.person.enums.Branch;
import com.snafu.todss.sig.sessies.domain.person.enums.Role;

import java.time.LocalDate;
class SupervisorCompactResponse {
    private final Long supervisorId;
    private final String supervisorName;

    public SupervisorCompactResponse(Long supervisorId, String supervisorName) {
        this.supervisorId = supervisorId;
        this.supervisorName = supervisorName;
    }

    public Long getSupervisorId() {
        return supervisorId;
    }

    public String getSupervisorName() {
        return supervisorName;
    }
}
public class PersonResponse {
    private final Long id;
    private final String email;
    private final String firstname;
    private final String lastname;
    private final String expertise;
    private final LocalDate employedSince;
    private final SupervisorCompactResponse supervisor;
    private final Branch branch;
    private final Role role;

    public PersonResponse(Long id, PersonDetails details) {
        this.id = id;
        this.email = details.getEmail();
        this.firstname = details.getFirstname();
        this.lastname = details.getLastname();
        this.expertise = details.getExpertise();
        this.employedSince = details.getEmployedSince();
        PersonDetails supervisorDetail = details.getSupervisor().getDetails();
        this.supervisor = new SupervisorCompactResponse(
                details.getSupervisor().getId(),
                String.format("%s, %s",supervisorDetail.getLastname(), supervisorDetail.getFirstname() )

        );
        this.branch = details.getBranch();
        this.role = details.getRole();
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

    public SupervisorCompactResponse getSupervisor() {
        return supervisor;
    }

    public Branch getBranch() {
        return branch;
    }

    public Role getRole() {
        return role;
    }
}
