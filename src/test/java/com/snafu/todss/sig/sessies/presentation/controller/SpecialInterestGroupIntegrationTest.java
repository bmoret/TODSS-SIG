package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.sessies.application.PersonService;
import com.snafu.todss.sig.sessies.application.SpecialInterestGroupService;
import com.snafu.todss.sig.sessies.data.SpecialInterestGroupRepository;
import com.snafu.todss.sig.sessies.data.SpringPersonRepository;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.person.PersonDetails;
import com.snafu.todss.sig.sessies.presentation.dto.request.PersonRequest;
import com.snafu.todss.sig.sessies.presentation.dto.response.PersonCompactResponse;
import javassist.NotFoundException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SpecialInterestGroupIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpecialInterestGroupService service;

    @Autowired
    private SpecialInterestGroupRepository repository;

    @Autowired
    private PersonService personService;

    private Person person;

    @BeforeEach
    void beforeEachTest() throws NotFoundException {
        PersonRequest dtoSupervisor = new PersonRequest();
            dtoSupervisor.email = "email@email.com";
            dtoSupervisor.firstname = "fourth";
            dtoSupervisor.lastname = "last";
            dtoSupervisor.expertise = "none";
            dtoSupervisor.branch = "VIANEN";
            dtoSupervisor.role = "EMPLOYEE";
            dtoSupervisor.employedSince = "01/01/2021";
            dtoSupervisor.supervisorId = null;
        Person supervisor = personService.createPerson(dtoSupervisor);

        PersonRequest dtoPerson = new PersonRequest();
            dtoPerson.email = "andereemail@email.com";
            dtoPerson.firstname = "fourth";
            dtoPerson.lastname = "last";
            dtoPerson.expertise = "none";
            dtoPerson.branch = "VIANEN";
            dtoPerson.role = "EMPLOYEE";
            dtoPerson.employedSince = "01/01/2021";
            dtoPerson.supervisorId = supervisor.getId();
        person = personService.createPerson(dtoPerson);
    }

    @AfterEach
    void tearDown() throws NotFoundException {
        this.repository.deleteAll();
        this.personService.removePerson(person.getId());
    }

    @Test
    @DisplayName("Get all special integration group gives list with special interest groups")
    void getAllSpecialInterestGroups() throws Exception {
        repository.save(new SpecialInterestGroup(
                        "any",
                        person,
                        new ArrayList<>(),
                        new ArrayList<>()
        ));
        RequestBuilder request = MockMvcRequestBuilders
                .get("/sig");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").doesNotExist());
    }

    @Test
    @DisplayName("Get all special interest groups gives empty list")
    void getAllSpecialInterestGroupsWithNoSessions() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/sig");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Get special interest group by id with not existing special interest group throws exception")
    void getSpecialInterestGroupByIdWithNotExistingSpecialInterestGroup_ThrowsNotFound() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/sig/" + UUID.randomUUID());

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("Get special interest group by id returns the special interest group")
    void getSpecialInterestGroupById_ReturnsSpecialInterestGroup() throws Exception {
        String subject = "subject";
        Person manager = personService.getPersonByEmail(person.getDetails().getEmail());
        SpecialInterestGroup specialInterestGroup = this.repository.save(
                new SpecialInterestGroup(
                        subject,
                        manager,
                        new ArrayList<>(),
                        new ArrayList<>()
                )
        );
        RequestBuilder request = MockMvcRequestBuilders
                .get("/sig/" + specialInterestGroup.getId());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(specialInterestGroup.getId().toString()))
                .andExpect(jsonPath("$.subject").value(specialInterestGroup.getSubject()))
                .andExpect(jsonPath("$.manager").exists())
                .andExpect(jsonPath("$.organizers").exists())
                .andExpect(jsonPath("$.sessions").doesNotExist());
    }


    @Test
    @DisplayName("Update special interest group updates the special interest group")
    void updateSpecialInterestGroup_ReturnsUpdatedSpecialInterestGroup() throws Exception {
        String subject = "subject";
        SpecialInterestGroup specialInterestGroup = this.repository.save(
                new SpecialInterestGroup(
                        subject,
                        person,
                        new ArrayList<>(),
                        new ArrayList<>()
                )
        );

        JSONObject json = new JSONObject();
        json.put("subject", "new");
        json.put("person", person);
        RequestBuilder request = MockMvcRequestBuilders
                .put("/sig/" + specialInterestGroup.getId())
                .contentType("application/json")
                .content(json.toString());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(specialInterestGroup.getId().toString()))
                .andExpect(jsonPath("$.subject").exists())
                .andExpect(jsonPath("$.manager").exists())
                .andExpect(jsonPath("$.organizers").exists())
                .andExpect(jsonPath("$.sessions").doesNotExist());
    }

    @Test
    @DisplayName("deleting a special interest group returns no content without body")
    void deleteSpecialInterestGroup_ReturnsNoContentWithoutBody() throws Exception {
        SpecialInterestGroup specialInterestGroup = this.repository.save(
                new SpecialInterestGroup(
                        "subject",
                        person,
                        new ArrayList<>(),
                        new ArrayList<>()
                )
        );
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/sig/" + specialInterestGroup.getId());

        mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @DisplayName("deleting a special interest group that does not exist throws not found")
    void deleteNotExistingSpecialInterestGroup_ThrowsNotFound() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/sig/" + UUID.randomUUID());

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }
}

