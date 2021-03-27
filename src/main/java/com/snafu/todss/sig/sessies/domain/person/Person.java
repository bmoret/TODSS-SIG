package com.snafu.todss.sig.sessies.domain.person;

import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.enums.*;


import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;

    @Column(name = "first_name")
    private String firstname;

    @Column(name = "last_name")
    private String lastname;

    private String expertise;

    @Column(name = "employed_since")
    private LocalDate employedSince;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "manager_id")
    private Person supervisor;

    private Branch branch;

    private Role role;

    @OneToMany(mappedBy = "person")
    private List<Attendance> attendance;

    @OneToMany(mappedBy = "manager")
    private List<SpecialInterestGroup> manager;

    @OneToMany(mappedBy = "organizer")
    private List<SpecialInterestGroup> organizer;

    public Person() {

    }

    public Person(String email,
                  String firstname,
                  String lastname,
                  String expertise,
                  LocalDate employedSince,
                  Person supervisor,
                  Branch branch,
                  Role role) {

        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.expertise = expertise;
        this.employedSince = employedSince;
        this.supervisor = supervisor;
        this.branch = branch;
        this.role = role;

        this.attendance = new ArrayList<>();
        this.manager = new ArrayList<>();
        this.organizer = new ArrayList<>();
    }

    public Boolean addAttendance(Attendance attendance) {
        if (!this.attendance.contains(attendance)) {
            return this.attendance.add(attendance);
        }
        return false;
    }

    public Boolean removeAttendance(Attendance attendance) {
        return this.attendance.remove(attendance);
    }

    public Boolean addManager(SpecialInterestGroup manager) {
        if (!this.manager.contains(manager)) {
            return this.manager.add(manager);
        }
        return false;
    }

    public Boolean removeManager(SpecialInterestGroup manager) {
        return this.manager.remove(manager);
    }

    public Boolean addOrganizer(SpecialInterestGroup organizer) {
        if (!this.organizer.contains(organizer)) {
            return this.organizer.add(organizer);
        }
        return false;
    }

    public Boolean removeOrganizer(SpecialInterestGroup organizer) {
        return this.organizer.remove(organizer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return getEmail().equals(person.getEmail()) &&
                getFirstname().equals(person.getFirstname()) &&
                getLastname().equals(person.getLastname()) &&
                Objects.equals(getExpertise(), person.getExpertise()) &&
                Objects.equals(getEmployedSince(), person.getEmployedSince()) &&
                Objects.equals(getSupervisor(), person.getSupervisor()) &&
                getBranch() == person.getBranch()
                && getRole() == person.getRole();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail(),
                getFirstname(),
                getLastname(),
                getExpertise(),
                getEmployedSince(),
                getSupervisor(),
                getBranch(),
                getRole());
    }

    public Long getId() {
        return id;
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

    public List<Attendance> getAttendance() {
        return attendance;
    }

    public List<SpecialInterestGroup> getManager() {
        return manager;
    }

    public List<SpecialInterestGroup> getOrganizer() {
        return organizer;
    }
}
