package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.sessies.application.PersonService;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.presentation.dto.request.PersonRequest;
import com.snafu.todss.sig.sessies.presentation.dto.response.PersonResponse;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonService SERVICE;

    public PersonController(PersonService service) {
        SERVICE = service;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PersonResponse> getPerson(@PathVariable("id") Long id) throws NotFoundException {
        Person person = SERVICE.getPerson(id);

        return new ResponseEntity<>(new PersonResponse(person), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PersonResponse> getPersonByEmail(@RequestBody String email) throws NotFoundException {
        Person person = SERVICE.getPersonByEmail(email);

        return new ResponseEntity<>(new PersonResponse(person), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PersonResponse> createPerson(
            @Valid @RequestBody PersonRequest dto
    ) throws NotFoundException {
        Person person = SERVICE.createPerson(dto);

        return new ResponseEntity<>(new PersonResponse(person), HttpStatus.OK);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<PersonResponse> updatePerson(
            @PathVariable Long id,
            @Valid @RequestBody PersonRequest dto
    ) throws NotFoundException {
        Person person = SERVICE.editPerson(id, dto);

        return new ResponseEntity<>(new PersonResponse(person), HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<PersonResponse> removePerson(@PathVariable Long id) throws NotFoundException {
        SERVICE.removePerson(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}