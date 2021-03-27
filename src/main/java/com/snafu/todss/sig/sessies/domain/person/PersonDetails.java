package com.snafu.todss.sig.sessies.domain.person;

import com.snafu.todss.sig.sessies.domain.person.enums.Branch;
import com.snafu.todss.sig.sessies.domain.person.enums.Role;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Embeddable
public class PersonDetails {
    private String email;

    @Column(name = "first_name")
    private String firstname;

    @Column(name = "last_name")
    private String lastname;

    private String expertise;

    @Column(name = "employed_since")
    private LocalDate employedSince;

    @OneToOne(cascade = {CascadeType.ALL})
    @Column(name = "supervisor_id")
    private Person supervisor;

    private Branch branch;

    private Role role;

    public PersonDetails() { }

    public PersonDetails(String email,
                         String firstname,
                         String lastname,
                         String expertise,
                         LocalDate employedSince,
                         Person supervisor,
                         Branch branch,
                         Role role
    ) {
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.expertise = expertise;
        this.employedSince = employedSince;
        this.supervisor = supervisor;
        this.branch = branch;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public LocalDate getEmployedSince() {
        return employedSince;
    }

    public void setEmployedSince(LocalDate employedSince) {
        this.employedSince = employedSince;
    }

    public Person getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Person supervisor) {
        this.supervisor = supervisor;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
