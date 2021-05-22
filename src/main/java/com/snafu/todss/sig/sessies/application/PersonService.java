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
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.math.NumberUtils.min;

@Service
@Transactional
public class PersonService {
    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final SpringPersonRepository PERSON_REPOSITORY;
    private LevenshteinDistance distance;

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

    public int calculateLevenshteinDistance(String val1, String val2) {
        if (val1.isBlank() || val2.isBlank()) {
            throw new RuntimeException("deze waarde mag niet leeg zijn");
        }
        char[] str1 = val1.toLowerCase(Locale.ROOT).toCharArray();
        char[] str2 = val2.toLowerCase(Locale.ROOT).toCharArray();
        int temp[][] = new int[str1.length+1][str2.length+1];

        for(int i=0; i < temp[0].length; i++) {
            temp[0][i] = i;
        }

        for(int i=0; i < temp.length; i++) {
            temp[i][0] = i;
        }

        for(int i=1;i <=str1.length; i++) {
            for(int j=1; j <= str2.length; j++) {
                if(str1[i-1] == str2[j-1]) {
                    temp[i][j] = temp[i-1][j-1];
                } else {
                    temp[i][j] = 1 + min(temp[i-1][j-1], temp[i-1][j], temp[i][j-1]);
                }
            }
        }

        return temp[str1.length][str2.length];
    }

    public List<Person> getBestlevenshteinDistanceValue(List<Person> allPersons, SearchRequest request) {
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
                    map.put(
                            person,
                            value
                    );
                }
        );

        return new ArrayList<>(map.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getValue()))
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

        return getBestlevenshteinDistanceValue(allPersons, request);
    }
}