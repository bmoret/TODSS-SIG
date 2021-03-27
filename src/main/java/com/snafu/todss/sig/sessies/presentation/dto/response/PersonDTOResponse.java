package com.snafu.todss.sig.sessies.presentation.dto.response;

import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.person.enums.Branch;
import com.snafu.todss.sig.sessies.domain.person.enums.Role;

import java.time.LocalDate;

public class PersonDTOResponse {
    private Long id;
    private String email, firstname, lastname, expertise;
    private LocalDate employedSince;
    private Long supervisorId;
    private String supervisorName;
    private Branch branch;
    private Role role;

    public PersonDTOResponse(Person person) {
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
}
