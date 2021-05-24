package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.sessies.application.PersonService;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.presentation.dto.request.PersonRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.SearchRequest;
import com.snafu.todss.sig.sessies.presentation.dto.response.PersonResponse;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonService SERVICE;

    public PersonController(PersonService service) {
        SERVICE = service;
    }

    private PersonResponse convertPersonToResponse(Person person) {
        return new PersonResponse(
                person.getId(),
                person.getSupervisor(),
                person.getDetails()
        );
    }

    @CrossOrigin("http://localhost:8081")
    @GetMapping(path = "/{id}")
    public ResponseEntity<PersonResponse> getPerson(@PathVariable("id") UUID id) throws NotFoundException {
        Person person = SERVICE.getPerson(id);

        return new ResponseEntity<>(convertPersonToResponse(person), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PersonResponse> getPersonByEmail(@RequestBody String email) throws NotFoundException {
        Person person = SERVICE.getPersonByEmail(email);

        return new ResponseEntity<>(convertPersonToResponse(person), HttpStatus.OK);
    }

    @CrossOrigin("http://localhost:8081")
    @PostMapping
    public ResponseEntity<PersonResponse> createPerson(
            @Valid @RequestBody PersonRequest dto
    ) throws NotFoundException {
        Person person = SERVICE.createPerson(dto);

        return new ResponseEntity<>(convertPersonToResponse(person), HttpStatus.OK);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<PersonResponse> updatePerson(
            @PathVariable UUID id,
            @Valid @RequestBody PersonRequest dto
    ) throws NotFoundException {
        Person person = SERVICE.editPerson(id, dto);

        return new ResponseEntity<>(convertPersonToResponse(person), HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<PersonResponse> removePerson(@PathVariable UUID id) throws NotFoundException {
        SERVICE.removePerson(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private List<PersonResponse> convertPersonToResponseList(List<Person> persons) {
        return persons.stream().map(this::convertPersonToResponse).collect(Collectors.toList());
    }

    @CrossOrigin("http://localhost:8081")
    @GetMapping(path = "/search")
    public ResponseEntity<List<PersonResponse>> searchPerson(@Valid @RequestBody SearchRequest request) throws RuntimeException {
        List<Person> personList = SERVICE.searchPerson(request);
        return new ResponseEntity<>(convertPersonToResponseList(personList), HttpStatus.OK);
    }
}
