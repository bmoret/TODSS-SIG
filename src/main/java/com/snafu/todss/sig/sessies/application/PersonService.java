package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.SpringPersonRepository;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.person.PersonBuilder;
import com.snafu.todss.sig.sessies.domain.person.enums.Branch;
import com.snafu.todss.sig.sessies.domain.person.enums.Role;
import com.snafu.todss.sig.sessies.presentation.dto.request.PersonRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.SearchRequest;
import com.sun.jdi.request.DuplicateRequestException;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.snafu.todss.sig.sessies.util.LevenshteinAlgorithm.calculateLevenshteinDistance;

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
        PERSON_REPOSITORY.findByDetails_Email(dto.email).ifPresent(error -> {
            throw new DuplicateRequestException(String.format("Person with email '%s' already exists", dto.email));
        });
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
        if (id != null) {
            try {
                return getPerson(id);
            } catch (NotFoundException e) {
                throw new NotFoundException("The given supervisor id is not related to a person");
            }
        }
      
        return null;
    }

    public void removePerson(UUID id) throws NotFoundException {
        PERSON_REPOSITORY.delete(getPerson(id));
    }

    public List<Person> getBestLevenshteinDistanceValue(List<Person> allPersons, SearchRequest request) {
        Map<Person, Integer> map = new HashMap<>();

        allPersons.forEach(
                person -> {
                    int value = calculateLevenshteinDistance(
                            request.searchTerm,
                            person.getDetails().getFirstname()+" "+ person.getDetails().getLastname()
                    );
                    int firstnameValue= calculateLevenshteinDistance(
                            request.searchTerm,
                            person.getDetails().getFirstname()
                    );
                    int lastnameValue= calculateLevenshteinDistance(
                            request.searchTerm,
                            person.getDetails().getLastname()
                    );
                    if(value > firstnameValue) {
                        value = firstnameValue;
                    }
                    if (value > lastnameValue) {
                        value= lastnameValue;
                    }
                    if (value <= 4) {
                        map.put(
                                person,
                                value
                        );
                    }
                }
        );

        return new ArrayList<>(map.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> { throw new AssertionError(); },
                        LinkedHashMap::new
                )).keySet());
    }

    public List<Person> searchPerson(SearchRequest request) {
        if (request.searchTerm.isBlank() || request.searchTerm.isBlank()) {
            throw new RuntimeException("vul de zoekbalk");
        }
        List<Person> allPersons = this.PERSON_REPOSITORY.findAll();

        return getBestLevenshteinDistanceValue(allPersons, request);
    }
}