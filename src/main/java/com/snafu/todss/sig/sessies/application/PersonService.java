package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.SpringPersonRepository;
import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public List<Person> searchPerson(SearchRequest request) throws NotFoundException {
        List<Person> results = new ArrayList<>();
        String firstname = request.firstname;
        String lastname = request.lastname;
        if (firstname != null && lastname != null && !firstname.isEmpty() && !lastname.isEmpty()) {
            System.out.println("schuif aan!");
            searchCorrectByFirstnameLastname(firstname, lastname).forEach(person -> results.add(person));
            if (results.isEmpty()) {
                compareLists(searchByLastname(lastname), searchByFirstname(firstname))
                        .forEach(person -> results.add(person));
                if (results.isEmpty()) {
                    searchPersonByPartial(firstname, lastname)
                            .forEach(person -> results.add(person));
                }
                if (results.isEmpty()) {
                    throw new NotFoundException(
                            String.format(
                                "Er zijn geen medewerkers met voornaam \"%s\" en achternaam \"%s\" gevonden.",
                                firstname,
                                lastname)
                    );
                }
            }
        }
        else if (firstname != null && !firstname.isEmpty()) {
            System.out.println("alleen voornaam");
            searchByFirstname(firstname).forEach(person -> results.add(person));
            if (results.isEmpty()) {
                searchPersonByPartial(firstname, lastname).forEach(person -> results.add(person));
            }
            if (results.isEmpty()) {
                throw new NotFoundException(
                        String.format(
                                "Er zijn geen medewerkers met voornaam \"%s\" gevonden.",
                                firstname)
                );
            }
        }
        else if (lastname != null && !lastname.isEmpty()) {
            System.out.println("alleen achternaam");
            searchByLastname(lastname).forEach(person -> results.add(person));
            if (results.isEmpty()) {
                searchPersonByPartial(firstname, lastname).forEach(person -> results.add(person));
            }
            if (results.isEmpty()) {
                throw new NotFoundException(
                        String.format(
                                "Er zijn geen medewerkers met achternaam \"%s\"",
                                lastname)
                );
            }
        }
        else {
            throw new NotFoundException("fillout form");
        }
        return results;
    }

    public List<Person> searchCorrectByFirstnameLastname(String firstname, String lastname) {
        List<Person> results = new ArrayList<>();
        PERSON_REPOSITORY.findByDetails_FirstnameAndDetails_Lastname(firstname, lastname)
                .forEach(person -> results.add(person));
        return results;
    }

    public List<Person> compareLists(List<Person> results, List<Person> toAdd) {
        toAdd.stream().filter(person -> !results.contains(person)).forEach(person -> results.add(person));
        return results;
    }

    public List<Person> searchByFirstname(String firstname) {
        List<Person> results = new ArrayList<>();
        PERSON_REPOSITORY.findByDetails_Firstname(firstname).forEach(person -> results.add(person));
        return results;
    }

    public List<Person> searchByLastname(String lastname) {
        List<Person> results = new ArrayList<>();
        PERSON_REPOSITORY.findByDetails_Lastname(lastname).forEach(person -> results.add(person));
        return results;
    }

    public List<Person> searchPersonByPartial(String firstname, String lastname) {
        List<Person> results = new ArrayList<>();
        String first; String middle; String last;
        int aThird; int half; int twoThirds; int min = 0; int max;
        if (firstname != null && !firstname.isEmpty()) {
            List<Person> firstnameResults = new ArrayList<>();
            aThird = firstname.length()/3;
            half = firstname.length()/2;
            twoThirds = firstname.length()/3*2;
            if (firstname.length() > 2) {
                min = firstname.length()-2;
                System.out.println("min: "+min);
            }
            max = firstname.length()+2;
            System.out.println("max: "+max);

            if (firstname.length() < 5) {
                if (firstname.length() == 1) {
                    PERSON_REPOSITORY.findPersonByFirstPartialFirstname(firstname, min, max)
                            .forEach(person -> firstnameResults.add(person));
                } else {
                    first = firstname.substring(0, half);
                    last = firstname.substring(half);
                    compareLists(
                            PERSON_REPOSITORY.findPersonByFirstPartialFirstname(first, min, max),
                            PERSON_REPOSITORY.findPersonByLastPartialFirstname(last, min, max)
                    ).forEach(person -> firstnameResults.add(person));
                }
            } else {
                first = firstname.substring(0, aThird);
                middle = firstname.substring(aThird, twoThirds);
                last = firstname.substring(twoThirds);
                System.out.println(first +" "+ middle +" "+ last);
                compareLists(
                        compareLists(
                                PERSON_REPOSITORY.findPersonByFirstPartialFirstname(first, min, max),
                                PERSON_REPOSITORY.findPersonByMiddlePartialFirstname(middle, min, max)
                        ),
                        PERSON_REPOSITORY.findPersonByLastPartialFirstname(last, min, max)
                ).forEach(person -> firstnameResults.add(person));
            }
            System.out.println("results in: "+ results);
            compareLists(results, firstnameResults);
        }
        if (lastname != null && !lastname.isEmpty())  {
            List<Person> lastnameResults = new ArrayList<>();
            aThird = lastname.length()/3;
            half = lastname.length()/2;
            twoThirds = lastname.length()/3*2;
            if (lastname.length() > 2) {
                min = lastname.length()-2;
            }
            max = lastname.length()+2;

            if (lastname.length() < 5) {
                if (lastname.length() == 1) {
                    PERSON_REPOSITORY.findPersonByFirstPartialLastname(lastname, min, max).forEach(person -> lastnameResults.add(person));
                } else {
                    first = lastname.substring(0, half);
                    last = lastname.substring(half);
                    compareLists(
                            PERSON_REPOSITORY.findPersonByFirstPartialLastname(first, min, max),
                            PERSON_REPOSITORY.findPersonByLastPartialLastname(last, min, max)
                    ).forEach(person -> lastnameResults.add(person));
                }
            } else {
                first = lastname.substring(0, aThird);
                middle = lastname.substring(aThird, twoThirds);
                last = lastname.substring(twoThirds);

                System.out.println(first +" "+ middle +" "+ last);
                compareLists(
                        compareLists(
                                PERSON_REPOSITORY.findPersonByFirstPartialLastname(first, min, max),
                                PERSON_REPOSITORY.findPersonByMiddlePartialLastname(middle, min, max)
                        ),
                        PERSON_REPOSITORY.findPersonByLastPartialLastname(last, min, max)
                ).forEach(person -> lastnameResults.add(person));
            }
            compareLists(results, lastnameResults);
        }
        System.out.println("results eind: "+ results);
        return results;
    }

}
