package com.snafu.todss.sig.sessies.domain;

import com.snafu.todss.sig.sessies.domain.enums.Branch;
import com.snafu.todss.sig.sessies.domain.enums.Role;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Person {
    private String email, firstname, lastname, expertise;
    private LocalDateTime employedSince;
    private Person supervisor;
    private Branch branch;
    private Role role;

    private List<Attendance> attendances;
    private List<SpecialInterestGroup> manager;
    private List<SpecialInterestGroup> organizer;

    public Person(String email,
                  String firstname,
                  String lastname,
                  String expertise,
                  LocalDateTime employedSince,
                  Person supervisor,
                  Branch branch,
                  Role role) throws NullPointerException {

        if (email.isBlank() || email.isEmpty() ||
                firstname.isBlank() || firstname.isEmpty() ||
                lastname.isBlank() || lastname.isEmpty()) {
            throw new NullPointerException();
        }

        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.expertise = expertise;
        this.employedSince = employedSince;
        this.supervisor = supervisor;
        this.branch = branch;
        this.role = role;

        this.attendances = new ArrayList<>();
        this.manager = new ArrayList<>();
        this.organizer = new ArrayList<>();
    }

    public Boolean addAttendance(Attendance attendance) {
        if (!this.attendances.contains(attendance)) {
            this.attendances.add(attendance);
            return true;
        }
        return false;
    }

    public Boolean removeAttendance(Attendance attendance) {
        if (this.attendances.contains(attendance)) {
            this.attendances.remove(attendance);
            return true;
        }
        return false;
    }

    public Boolean addManager(SpecialInterestGroup manager) {
        if (!this.manager.contains(manager)) {
            this.manager.add(manager);
            return true;
        }
        return false;
    }

    public Boolean removeManager(SpecialInterestGroup manager) {
        if (this.manager.contains(manager)) {
            this.manager.remove(manager);
            return true;
        }
        return false;
    }

    public Boolean addOrganizer(SpecialInterestGroup organizer) {
        if (!this.organizer.contains(organizer)) {
            this.organizer.add(organizer);
            return true;
        }
        return false;
    }

    public Boolean removeOrganizer(SpecialInterestGroup organizer) {
        if (this.organizer.contains(organizer)) {
            this.organizer.remove(organizer);
            return true;
        }
        return false;
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
                && getRole() == person.getRole()
                && getAttendances().equals(person.getAttendances())
                && getManager().equals(person.getManager())
                && getOrganizer().equals(person.getOrganizer());
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
                getRole(),
                getAttendances(),
                getManager(),
                getOrganizer());
    }

    @Override
    public String toString() {
        return "Person{" +
                "email='" + email + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", expertise='" + expertise + '\'' +
                ", employedSince=" + employedSince +
                ", supervisor=" + supervisor +
                ", branch=" + branch +
                ", role=" + role +
                ", attendances=" + attendances +
                ", manager=" + manager +
                ", organizer=" + organizer +
                '}';
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

    public LocalDateTime getEmployedSince() {
        return employedSince;
    }

    public void setEmployedSince(LocalDateTime employedSince) {
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

    public List<Attendance> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<Attendance> attendances) {
        this.attendances = attendances;
    }

    public List<SpecialInterestGroup> getManager() {
        return manager;
    }

    public void setManager(List<SpecialInterestGroup> manager) {
        this.manager = manager;
    }

    public List<SpecialInterestGroup> getOrganizer() {
        return organizer;
    }

    public void setOrganizer(List<SpecialInterestGroup> organizer) {
        this.organizer = organizer;
    }
}
