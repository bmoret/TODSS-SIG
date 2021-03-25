package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.SpringPersonRepository;
import com.snafu.todss.sig.sessies.domain.Person;
import com.snafu.todss.sig.sessies.domain.enums.Branch;
import com.snafu.todss.sig.sessies.domain.enums.Role;
import javassist.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PersonService {
    private final SpringPersonRepository REPOSITORY;

    public PersonService(SpringPersonRepository repository) {
        REPOSITORY = repository;
    }

    public Person getPerson(Long id) throws NotFoundException {
        return REPOSITORY.findById(id)
                .orElseThrow(() -> new NotFoundException("The given id is not related to a person"));
    }

    public Person getPersonByEmail(String email) throws  NotFoundException {
        return REPOSITORY.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("The given email is not related to a person"));
    }

    public Person createPerson(String email,
                       String firstname,
                       String lastname,
                       String expertise,
                       String employedSinceString,
                       Long supervisorId,
                       String branchString,
                       String roleString) throws NotFoundException {
        Branch branch = null;
        if (branchString != null) {
            branch = Branch.valueOf(branchString);
        }
        Role role = null;
        if (roleString != null) {
            role = Role.valueOf(roleString);
        }
        LocalDate employedSince = null;
        if (employedSinceString != null) {
            employedSince = LocalDate.parse(employedSinceString, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }

        Person supervisor = null;
        if (supervisorId != null) {
            try {
                supervisor = getPerson(supervisorId);
            } catch (NotFoundException e) {
                throw new NotFoundException("The given supervisor id is not related to a person");
            }
        }
        Person person = new Person(email, firstname, lastname, expertise, employedSince, supervisor, branch, role);

        REPOSITORY.save(person);

        return person;
    }

    public Person editPerson(Long id,
                     String email,
                     String firstname,
                     String lastname,
                     String expertise,
                     String employedSinceString,
                     Long supervisorId,
                     String branchString,
                     String roleString) throws NotFoundException {
        Person person = getPerson(id);

        if (email != null) {
            person.setEmail(email);
        }
        if (firstname != null) {
            person.setFirstname(firstname);
        }
        if (lastname != null) {
            person.setLastname(lastname);
        }
        if (expertise != null) {
            person.setExpertise(expertise);
        }
        if (employedSinceString != null) {
            LocalDate employedSince = LocalDate.parse(employedSinceString, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            person.setEmployedSince(employedSince);
        }
        if (supervisorId != null) {
            try {
                Person supervisor = getPerson(supervisorId);
                person.setSupervisor(supervisor);
            } catch (NotFoundException e) {
                throw new NotFoundException("The given supervisor id is not related to a person");
            }
        }
        if (branchString != null) {
            Branch branch = Branch.valueOf(branchString);
            person.setBranch(branch);
        }
        if (roleString != null) {
            Role role = Role.valueOf(roleString);
            person.setRole(role);
        }

        REPOSITORY.save(person);

        return person;    }

    public void removePerson(Long id) throws NotFoundException {
        REPOSITORY.delete(getPerson(id));
    }


    // TODO Ik ben niet zeker of deze functionaliteit nodig is
    public void addAttendance(Long id, Attendance attendance) throws NotFoundException {
        Person person = getPerson(id);

        person.addAttendance(attendance);
    }

    public void removeAttendance(Long id, Attendance attendance) throws NotFoundException {
        Person person = getPerson(id);

        person.removeAttendance(attendance);
    }

    public void addManager(Long id, SpecialInterestGroup manager) throws NotFoundException {
        Person person = getPerson(id);

        person.addManager(manager);
    }

    public void removeManager(Long id, SpecialInterestGroup manager) throws NotFoundException {
        Person person = getPerson(id);

        person.removeManager(manager);
    }

    public void addManager(Long id, SpecialInterestGroup organizor) throws NotFoundException {
        Person person = getPerson(id);

        person.addOrganizer(organizor);
    }

    public void removeManager(Long id, SpecialInterestGroup organizor) throws NotFoundException {
        Person person = getPerson(id);

        person.removeOrganizer(organizor);
    }
}
