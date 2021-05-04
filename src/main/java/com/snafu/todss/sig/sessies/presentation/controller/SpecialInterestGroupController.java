package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.sessies.application.PersonService;
import com.snafu.todss.sig.sessies.application.SpecialInterestGroupService;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.presentation.dto.request.SpecialInterestGroupRequest;
import com.snafu.todss.sig.sessies.presentation.dto.response.PersonCompactResponse;
import com.snafu.todss.sig.sessies.presentation.dto.response.PersonResponse;
import com.snafu.todss.sig.sessies.presentation.dto.response.SpecialInterestGroupResponse;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sig")
public class SpecialInterestGroupController {
    private final SpecialInterestGroupService SERVICE;
    private final PersonService personService;

    public SpecialInterestGroupController(SpecialInterestGroupService specialInterestGroupService, PersonService personService) {
        this.SERVICE = specialInterestGroupService;
        this.personService = personService;
    }

    private SpecialInterestGroupResponse convertSpecialInterestGroupToResponse(SpecialInterestGroup sig) {
        PersonCompactResponse managerResponse = createPcr(sig.getManager());
        List<PersonCompactResponse> organizers = sig.getOrganizers().stream()
                .map(this::createPcr)
                .collect(Collectors.toList());

        return new SpecialInterestGroupResponse(
                sig.getId(),
                sig.getSubject(),
                managerResponse,
                organizers
        );
    }

    private PersonCompactResponse createPcr(Person person) {
        if (person != null) {
            return new PersonCompactResponse(person.getId(),
                    String.format("%s, %s", person.getDetails().getLastname(), person.getDetails().getFirstname())
            );
        }

        return null;
    }

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<SpecialInterestGroupResponse>> getAllSpecialInterestGroups() {
        List<SpecialInterestGroup> specialInterestGroups = this.SERVICE.getAllSpecialInterestGroups();
        List<SpecialInterestGroupResponse> responses = new ArrayList<>();
        for (SpecialInterestGroup specialInterestGroup : specialInterestGroups) {
            responses.add(convertSpecialInterestGroupToResponse(specialInterestGroup));
        }

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecialInterestGroupResponse> getSpecialInterestGroup(@PathVariable UUID id) throws NotFoundException {
        SpecialInterestGroup specialInterestGroup = this.SERVICE.getSpecialInterestGroupById(id);

        return new ResponseEntity<>(convertSpecialInterestGroupToResponse(specialInterestGroup), HttpStatus.OK);
    }

    @CrossOrigin("http://localhost:8081")
    @GetMapping("/{id}/people")
    public ResponseEntity<List<PersonResponse>> getAssociatedPeopleBySpecialInterestGroup(@PathVariable String id)
    throws NotFoundException {
        UUID id1 = UUID.fromString(id);
        List<Person> people = this.SERVICE.getAssociatedPeopleBySpecialInterestGroup(id1);
        List<PersonResponse> personResponses = new ArrayList<>();

        for (Person person : people) {
            personResponses.add(new PersonResponse(person.getId(), person.getSupervisor(), person.getDetails()));
        }

        return new ResponseEntity<>(personResponses, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SpecialInterestGroupResponse> createSpecialInterestGroup(
            @Valid @RequestBody SpecialInterestGroupRequest specialInterestGroupRequest
    ) throws NotFoundException {
        SpecialInterestGroup specialInterestGroup = this.SERVICE.createSpecialInterestGroup(specialInterestGroupRequest);

        return new ResponseEntity<>(convertSpecialInterestGroupToResponse(specialInterestGroup), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecialInterestGroupResponse> updateSpecialInterestGroup(
            @PathVariable UUID id,
            @Valid @RequestBody SpecialInterestGroupRequest specialInterestGroupRequest
    ) throws NotFoundException {
        SpecialInterestGroup specialInterestGroup = this.SERVICE.updateSpecialInterestGroup(
                id,
                specialInterestGroupRequest
        );

        return new ResponseEntity<>(convertSpecialInterestGroupToResponse(specialInterestGroup), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecialInterestGroup(@PathVariable UUID id) throws NotFoundException {
        this.SERVICE.deleteSpecialInterestGroup(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
