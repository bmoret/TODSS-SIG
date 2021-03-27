package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.sessies.application.PersonService;
import com.snafu.todss.sig.sessies.domain.Person;
import com.snafu.todss.sig.sessies.presentation.dto.request.PersonDTORequest;
import com.snafu.todss.sig.sessies.presentation.dto.response.PersonDTOResponse;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonService SERVICE;


    public PersonController(PersonService service) {
        SERVICE = service;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PersonDTOResponse> getPerson(@PathVariable("id") Long id) throws NotFoundException {
        Person person = SERVICE.getPerson(id);

        return new ResponseEntity<>(new PersonDTOResponse(person), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PersonDTOResponse> getPersonByEmail(@RequestBody String email) throws NotFoundException {
        Person person = SERVICE.getPersonByEmail(email);

        return new ResponseEntity<>(new PersonDTOResponse(person), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PersonDTOResponse> createPerson(@RequestBody PersonDTORequest DTO) throws NotFoundException {
        Person person = SERVICE.createPerson(DTO.email,
                DTO.firstname,
                DTO.lastname,
                DTO.expertise,
                DTO.employedSince,
                DTO.supervisorId,
                DTO.branch,
                DTO.role);

        return new ResponseEntity<>(new PersonDTOResponse(person), HttpStatus.OK);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<PersonDTOResponse> editPerson(@PathVariable Long id, @RequestBody PersonDTORequest DTO) throws NotFoundException {
        Person person = SERVICE.editPerson(id,
                DTO.email,
                DTO.firstname,
                DTO.lastname,
                DTO.expertise,
                DTO.employedSince,
                DTO.supervisorId,
                DTO.branch,
                DTO.role);

        return new ResponseEntity<>(new PersonDTOResponse(person), HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<PersonDTOResponse> removePerson(@PathVariable Long id) throws NotFoundException {
        SERVICE.removePerson(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
