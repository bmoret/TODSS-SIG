package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.SpringPersonRepository;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.person.PersonBuilder;
import com.snafu.todss.sig.sessies.domain.person.enums.Branch;
import com.snafu.todss.sig.sessies.domain.person.enums.Role;
import com.snafu.todss.sig.sessies.presentation.dto.request.PersonRequest;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Transactional
public class PersonService {
    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final SpringPersonRepository PERSON_REPOSITORY;

    public PersonService(SpringPersonRepository repository) {
        PERSON_REPOSITORY = repository;
    }

    public Person getPerson(Long id) throws NotFoundException {
        return PERSON_REPOSITORY.findById(id)
                .orElseThrow(() -> new NotFoundException("The given id is not related to a person"));
    }

    public Person getPersonByEmail(String email) throws NotFoundException {
        return PERSON_REPOSITORY.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("The given email is not related to a person"));
    }

    public Person createPerson(PersonRequest dto) throws NotFoundException {
        Branch branch = getBranchOfString(dto.branch);
        Role role = getRoleOfString(dto.role);
        LocalDate employedSince = LocalDate.parse(dto.employedSince, DATE_TIME_FORMATTER);
        Person supervisor = getSupervisorById(dto.supervisorId);
        Person person = new PersonBuilder()
                .setEmail(dto.email)
                .setFirstname(dto.firstname)
                .setLastname(dto.lastname)
                .setExpertise(dto.expertise)
                .setEmployedSince(employedSince)
                .setSupervisor(supervisor)
                .setBranch(branch)
                .setRole(role)
                .build();

        return PERSON_REPOSITORY.save(person);
    }

    public Person editPerson(Long id, PersonRequest request) throws NotFoundException {
        Person person = getPerson(id);
        person.setEmail(request.email);
        person.setFirstname(request.firstname);
        person.setLastname(request.lastname);
        person.setExpertise(request.expertise);
        LocalDate employedSince = LocalDate.parse(request.employedSince, DATE_TIME_FORMATTER);
        person.setEmployedSince(employedSince);
        Person supervisor = getPerson(request.supervisorId);
        person.setSupervisor(supervisor);
        person.setBranch(getBranchOfString(request.branch));
        person.setRole(getRoleOfString(request.role));
        return PERSON_REPOSITORY.save(person);
    }

    private Branch getBranchOfString(String branch) {
        try {
            return Branch.valueOf(branch);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("No branch with name '%s' exists", branch));
        }
    }

    private Role getRoleOfString(String role) {
        try {
            return Role.valueOf(role);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("No role with name '%s' exists", role));
        }
    }

    private Person getSupervisorById(Long id) throws NotFoundException {
        try {
            return getPerson(id);
        } catch (NotFoundException e) {
            throw new NotFoundException("The given supervisor id is not related to a person");
        }
    }

    public void removePerson(Long id) throws NotFoundException {
        PERSON_REPOSITORY.delete(getPerson(id));
    }

    //todo Ik ben niet zeker of deze functionaliteit nodig is -bas
    // Waarschijnlijk valt dit onder deels domain en deels meer deze andere classes -Jona
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

    public void addOrganiser(Long id, SpecialInterestGroup organizor) throws NotFoundException {
        Person person = getPerson(id);

        person.addOrganizer(organizor);
    }

    public void removeOrganiser(Long id, SpecialInterestGroup organizor) throws NotFoundException {
        Person person = getPerson(id);

        person.removeOrganizer(organizor);
    }
}
