package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.SpringPersonRepository;
import com.snafu.todss.sig.sessies.domain.Attendance;
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
import java.util.UUID;

@Service
@Transactional
public class PersonService {
    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final SpringPersonRepository PERSON_REPOSITORY;

    public PersonService(SpringPersonRepository repository) {
        PERSON_REPOSITORY = repository;
    }

    public Person getPerson(UUID id) throws NotFoundException {
        return PERSON_REPOSITORY.findById(id)
                .orElseThrow(() -> new NotFoundException("The given id is not related to a person"));
    }

    public Person getPersonByEmail(String email) throws NotFoundException {
        return PERSON_REPOSITORY.findByDetails_Email(email)
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

    public Person editPerson(UUID id, PersonRequest request) throws NotFoundException {
        Person person = getPerson(id);
        person.getDetails().setEmail(request.email);
        person.getDetails().setFirstname(request.firstname);
        person.getDetails().setLastname(request.lastname);
        person.getDetails().setExpertise(request.expertise);
        person.getDetails().setBranch(getBranchOfString(request.branch));
        person.getDetails().setRole(getRoleOfString(request.role));
        LocalDate employedSince = LocalDate.parse(request.employedSince, DATE_TIME_FORMATTER);
        person.getDetails().setEmployedSince(employedSince);
        Person supervisor = getPerson(request.supervisorId);
        person.setSupervisor(supervisor);
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

    private Person getSupervisorById(UUID id) throws NotFoundException {
        try {
            return getPerson(id);
        } catch (NotFoundException e) {
            throw new NotFoundException("The given supervisor id is not related to a person");
        }
    }

    public void removePerson(UUID id) throws NotFoundException {
        PERSON_REPOSITORY.delete(getPerson(id));
    }

    //todo Ik ben niet zeker of deze functionaliteit nodig is -bas
    // Waarschijnlijk valt dit onder deels domain en deels meer deze andere functies (andere classes) -Jona
    public boolean addAttendance(UUID id, Attendance attendance) throws NotFoundException {
        Person person = getPerson(id);
        return person.addAttendance(attendance);
    }

    public boolean removeAttendance(UUID id, Attendance attendance) throws NotFoundException {
        Person person = getPerson(id);
        return person.removeAttendance(attendance);
    }

    public boolean addManager(UUID id, SpecialInterestGroup manager) throws NotFoundException {
        Person person = getPerson(id);
        return person.addManager(manager);
    }

    public boolean removeManager(UUID id, SpecialInterestGroup manager) throws NotFoundException {
        Person person = getPerson(id);
        return person.removeManager(manager);
    }

    public boolean addOrganiser(UUID id, SpecialInterestGroup organizor) throws NotFoundException {
        Person person = getPerson(id);
        return person.addOrganizer(organizor);
    }

    public boolean removeOrganiser(UUID id, SpecialInterestGroup organizor) throws NotFoundException {
        Person person = getPerson(id);
        return person.removeOrganizer(organizor);
    }
}
