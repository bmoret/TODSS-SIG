package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.sessies.application.PersonService;
import com.snafu.todss.sig.sessies.application.SpecialInterestGroupService;
import com.snafu.todss.sig.sessies.data.SpecialInterestGroupRepository;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.presentation.dto.request.PersonRequest;
import javassist.NotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    @WithMockUser(username = "TestUser", roles = "{MANAGER, SECRETARY, EMPLOYEE, ADMINISTRATOR}")
    @DisplayName("Get all special integration group gives list with special interest groups")
    void getAllSpecialInterestGroups() throws Exception {
        repository.save(new SpecialInterestGroup(
                        "any",
                        person,
                        new ArrayList<>(),
                        new ArrayList<>()
        ));
        RequestBuilder request = MockMvcRequestBuilders
                .get("/sig")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").doesNotExist());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "{MANAGER, SECRETARY, EMPLOYEE, ADMINISTRATOR}")
    @DisplayName("Get all special interest groups gives empty list")
    void getAllSpecialInterestGroupsWithNoSessions() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/sig")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "{MANAGER, SECRETARY, EMPLOYEE, ADMINISTRATOR}")
    @DisplayName("Get special interest group by id with not existing special interest group throws exception")
    void getSpecialInterestGroupByIdWithNotExistingSpecialInterestGroup_ThrowsNotFound() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/sig/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "{MANAGER, SECRETARY, EMPLOYEE, ADMINISTRATOR}")
    @DisplayName("Get associated employees by SIG id returns list of people")
    void getAssociatedEmployeesById_ReturnsListOfPeople() throws Exception {
        SpecialInterestGroup sig = repository.save(new SpecialInterestGroup(
                "any",
                person,
                new ArrayList<>(),
                new ArrayList<>()
        ));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/sig/" + sig.getId() + "/people")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "{MANAGER, SECRETARY, EMPLOYEE, ADMINISTRATOR}")
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
                .get("/sig/" + specialInterestGroup.getId())
                .contentType(MediaType.APPLICATION_JSON);

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
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Create special interest group by id as manager returns the special interest group")
    void createSpecialInterestGroupAsManager_ReturnsSpecialInterestGroup() throws Exception {
        JSONObject jsonObject = new JSONObject();
        JSONArray ids = new JSONArray();
        jsonObject.put("subject", "Test subject");
        jsonObject.put("managerId", person.getId().toString());
        jsonObject.put("organizerIds", ids);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/sig")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.subject").value("Test subject"))
                .andExpect(jsonPath("$.manager").exists())
                .andExpect(jsonPath("$.organizers").exists())
                .andExpect(jsonPath("$.sessions").doesNotExist());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ADMINISTRATOR")
    @DisplayName("Create special interest group by id as manager returns the special interest group")
    void createSpecialInterestGroupAAdministrator_ReturnsSpecialInterestGroup() throws Exception {
        JSONObject jsonObject = new JSONObject();
        JSONArray ids = new JSONArray();
        jsonObject.put("subject", "Test subject");
        jsonObject.put("managerId", person.getId().toString());
        jsonObject.put("organizerIds", ids);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/sig")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.subject").value("Test subject"))
                .andExpect(jsonPath("$.manager").exists())
                .andExpect(jsonPath("$.organizers").exists())
                .andExpect(jsonPath("$.sessions").doesNotExist());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "SECRETARY")
    @DisplayName("Create special interest group by id as secretary returns the special interest group")
    void createSpecialInterestGroupAsSecretary_ReturnsSpecialInterestGroup() throws Exception {
        JSONObject jsonObject = new JSONObject();
       JSONArray ids = new JSONArray();
        jsonObject.put("subject", "Test1 subject");
        jsonObject.put("managerId", person.getId().toString());
        jsonObject.put("organizerIds", ids);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/sig")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.subject").value("Test1 subject"))
                .andExpect(jsonPath("$.manager").exists())
                .andExpect(jsonPath("$.organizers").exists())
                .andExpect(jsonPath("$.sessions").doesNotExist());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("Create special interest group by id as manager is not allowed")
    void createSpecialInterestGroupAsEmployee() throws Exception {
        JSONObject jsonObject = new JSONObject();
        JSONArray ids = new JSONArray();
        jsonObject.put("subject", "Test subject");
        jsonObject.put("managerId", person.getId().toString());
        jsonObject.put("organizerIds", ids);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/sig")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Update special interest group as manager updates the special interest group")
    void updateSpecialInterestGroupAsManager_ReturnsUpdatedSpecialInterestGroup() throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON)
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
    @WithMockUser(username = "TestUser", roles = "SECRETARY")
    @DisplayName("Update special interest group as secretary updates the special interest group")
    void updateSpecialInterestGroupAsSecretary_ReturnsUpdatedSpecialInterestGroup() throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON)
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
    @WithMockUser(username = "TestUser", roles = "ADMINISTRATOR")
    @DisplayName("Update special interest group as administrator updates the special interest group")
    void updateSpecialInterestGroupAsAdministrator_ReturnsUpdatedSpecialInterestGroup() throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON)
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
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("Update special interest group as employee is not allowed")
    void updateSpecialInterestGroupAsEmployee() throws Exception {
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
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString());

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Deleting a special interest group as manager returns no content without body")
    void deleteSpecialInterestGroupAsManager_ReturnsNoContentWithoutBody() throws Exception {
        SpecialInterestGroup specialInterestGroup = this.repository.save(
                new SpecialInterestGroup(
                        "subject",
                        person,
                        new ArrayList<>(),
                        new ArrayList<>()
                )
        );
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/sig/" + specialInterestGroup.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "SECRETARY")
    @DisplayName("Deleting a special interest group as secretary returns no content without body")
    void deleteSpecialInterestGroupAsSecretary_ReturnsNoContentWithoutBody() throws Exception {
        SpecialInterestGroup specialInterestGroup = this.repository.save(
                new SpecialInterestGroup(
                        "subject",
                        person,
                        new ArrayList<>(),
                        new ArrayList<>()
                )
        );
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/sig/" + specialInterestGroup.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ADMINISTRATOR")
    @DisplayName("Deleting a special interest group as administrator returns no content without body")
    void deleteSpecialInterestGroupAsAdministrator_ReturnsNoContentWithoutBody() throws Exception {
        SpecialInterestGroup specialInterestGroup = this.repository.save(
                new SpecialInterestGroup(
                        "subject",
                        person,
                        new ArrayList<>(),
                        new ArrayList<>()
                )
        );
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/sig/" + specialInterestGroup.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("Deleting a special interest group as employee is not allowed")
    void deleteSpecialInterestGroupAsEmployee() throws Exception {
        SpecialInterestGroup specialInterestGroup = this.repository.save(
                new SpecialInterestGroup(
                        "subject",
                        person,
                        new ArrayList<>(),
                        new ArrayList<>()
                )
        );
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/sig/" + specialInterestGroup.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Deleting a non existing special interest group as manager throws not found")
    void deleteNotExistingSpecialInterestGroupAsManager_ThrowsNotFound() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/sig/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }
}

