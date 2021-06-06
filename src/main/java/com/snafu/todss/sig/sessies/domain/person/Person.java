package com.snafu.todss.sig.sessies.domain.person;

import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Embedded
    private PersonDetails details;

    @OneToOne(cascade = {CascadeType.ALL})
    private Person supervisor;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    private List<Attendance> attendance;

    @OneToMany(mappedBy = "manager")
    private List<SpecialInterestGroup> managedSpecialInterestGroups;

    @ManyToMany(mappedBy = "organizers")
    private List<SpecialInterestGroup> organisedSpecialInterestGroups;

    public Person() {
    }

    public Person(PersonDetails personDetails,
                  Person supervisor,
                  List<Attendance> attendances,
                  List<SpecialInterestGroup> managedSpecialInterestGroups,
                  List<SpecialInterestGroup> organisedSpecialInterestGroups
    ) {
        this.details = personDetails;
        this.supervisor = supervisor;
        this.attendance = attendances;
        this.managedSpecialInterestGroups = managedSpecialInterestGroups;
        this.organisedSpecialInterestGroups = organisedSpecialInterestGroups;
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
        if (!this.managedSpecialInterestGroups.contains(manager)) {
            return this.managedSpecialInterestGroups.add(manager);
        }
        return false;
    }

    public Boolean removeManager(SpecialInterestGroup manager) {
        return this.managedSpecialInterestGroups.remove(manager);
    }

    public Boolean addOrganizer(SpecialInterestGroup organizer) {
        if (!this.organisedSpecialInterestGroups.contains(organizer)) {
            return this.organisedSpecialInterestGroups.add(organizer);
        }
        return false;
    }

    public Boolean removeOrganizer(SpecialInterestGroup organizer) {
        return this.organisedSpecialInterestGroups.remove(organizer);
    }

    public UUID getId() {
        return id;
    }

    public PersonDetails getDetails() {
        return details;
    }

    public List<Attendance> getAttendance() {
        return attendance;
    }

    public List<SpecialInterestGroup> getManagedSpecialInterestGroups() {
        return managedSpecialInterestGroups;
    }

    public List<SpecialInterestGroup> getOrganisedSpecialInterestGroups() {
        return organisedSpecialInterestGroups;
    }

    public Person getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Person supervisor) {
        this.supervisor = supervisor;
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
}
