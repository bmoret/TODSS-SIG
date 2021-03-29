package com.snafu.todss.sig.sessies.domain.person;

import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Embedded
    private PersonDetails details;

    @OneToMany(mappedBy = "person")
    private List<Attendance> attendance;

    @OneToMany(mappedBy = "manager")
    private List<SpecialInterestGroup> manager;

    @OneToMany(mappedBy = "organizer")
    private List<SpecialInterestGroup> organizer;

    public Person() {

    }

    public Person(PersonDetails personDetails,
                  List<Attendance> attendances,
                  List<SpecialInterestGroup> managers,
                  List<SpecialInterestGroup> organizer
    ) {
        this.details = personDetails;
        this.attendance = attendances;
        this.manager = managers;
        this.organizer = organizer;
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
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id) &&
                Objects.equals(details, person.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, details);
    }

    public Long getId() {
        return id;
    }

    public PersonDetails getDetails() {
        return details;
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
