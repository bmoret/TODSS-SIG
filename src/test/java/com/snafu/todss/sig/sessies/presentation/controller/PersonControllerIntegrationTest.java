package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.sessies.application.PersonService;
import com.snafu.todss.sig.sessies.data.SpringPersonRepository;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.presentation.dto.request.PersonRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PersonControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpringPersonRepository personRepository;

    @Autowired
    private PersonService personService;

    private Person supervisor;

    @BeforeEach
    void setup() throws Exception {
        PersonRequest dtoSupervisor = new PersonRequest();
        dtoSupervisor.email = "test2@email.com";
        dtoSupervisor.firstname = "Test";
        dtoSupervisor.lastname = "Person";
        dtoSupervisor.expertise = "none";
        dtoSupervisor.branch = "VIANEN";
        dtoSupervisor.role = "EMPLOYEE";
        dtoSupervisor.employedSince = "2005-12-01";
        dtoSupervisor.supervisorId = null;
        supervisor = personService.createPerson(dtoSupervisor);
        personRepository.save(supervisor);
    }

    @AfterEach
    void tearDown() {
        personRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Get person by email as manager")
    void getPersonByEmailAsManager() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/person")
                .content("test2@email.com")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("test2@email.com")))
                .andExpect(jsonPath("$.role", is("EMPLOYEE")));
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "SECRETARY")
    @DisplayName("Get person by email as secretary")
    void getPersonByEmailAsSecretary() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/person")
                .content("test2@email.com")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("test2@email.com")))
                .andExpect(jsonPath("$.role", is("EMPLOYEE")));
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ADMINISTRATOR")
    @DisplayName("Get person by email as administrator")
    void getPersonByEmailAsAdministrator() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/person")
                .content("test2@email.com")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("test2@email.com")))
                .andExpect(jsonPath("$.role", is("EMPLOYEE")));
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("Get person by email as employee is not allowed")
    void getPersonByEmailAsEmployee() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/person")
                .content("email@email.com")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Create person as manager")
    void createPersonAsManager() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/person")
                .content("{\"email\":\"test.email@email.com\"," +
                        "\"firstname\":\"fourth\"," +
                        "\"lastname\":\"last\"," +
                        "\"expertise\":\"none\"," +
                        "\"branch\":\"VIANEN\"," +
                        "\"role\":\"EMPLOYEE\"," +
                        "\"employedSince\":\"2005-12-01\"," +
                        "\"supervisorId\":\""+ supervisor.getId() +"\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("test.email@email.com")))
                .andExpect(jsonPath("$.role", is("EMPLOYEE")));
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "SECRETARY")
    @DisplayName("Create person as secretary")
    void createPersonAsSecretary() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/person")
                .content("{\"email\":\"test1.email@email.com\"," +
                        "\"firstname\":\"fourth\"," +
                        "\"lastname\":\"last\"," +
                        "\"expertise\":\"none\"," +
                        "\"branch\":\"VIANEN\"," +
                        "\"role\":\"EMPLOYEE\"," +
                        "\"employedSince\":\"2005-12-01\"," +
                        "\"supervisorId\":\""+ supervisor.getId() +"\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("test1.email@email.com")))
                .andExpect(jsonPath("$.role", is("EMPLOYEE")));
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ADMINISTRATOR")
    @DisplayName("Create person as administrator")
    void createPersonAsAdministrator() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/person")
                .content("{\"email\":\"test3.email@email.com\"," +
                        "\"firstname\":\"fourth\"," +
                        "\"lastname\":\"last\"," +
                        "\"expertise\":\"none\"," +
                        "\"branch\":\"VIANEN\"," +
                        "\"role\":\"EMPLOYEE\"," +
                        "\"employedSince\":\"2005-12-01\"," +
                        "\"supervisorId\":\""+ supervisor.getId() +"\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("test3.email@email.com")))
                .andExpect(jsonPath("$.role", is("EMPLOYEE")));
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("Create person as employee is not allowed")
    void createPersonAsEmployee() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/person")
                .content("{\"email\":\"test.email@email.com\"," +
                        "\"firstname\":\"fourth\"," +
                        "\"lastname\":\"last\"," +
                        "\"expertise\":\"none\"," +
                        "\"branch\":\"VIANEN\"," +
                        "\"role\":\"EMPLOYEE\"," +
                        "\"employedSince\":\"2005-12-01\"," +
                        "\"supervisorId\":\""+ supervisor.getId().toString() +"\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Update person as manager")
    void updatePersonAsManager() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .put("/person/"+supervisor.getId())
                .content("{\"email\":\"email2@email.com\"," +
                        "\"firstname\":\"second\"," +
                        "\"lastname\":\"last\"," +
                        "\"expertise\":\"all\"," +
                        "\"branch\":\"VIANEN\"," +
                        "\"role\":\"EMPLOYEE\"," +
                        "\"employedSince\":\"2005-12-01\"," +
                        "\"supervisorId\":\""+ supervisor.getId().toString() +"\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("email2@email.com")))
                .andExpect(jsonPath("$.expertise", is("all")));
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "SECRETARY")
    @DisplayName("Update person as secretary")
    void updatePersonAsSecretary() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .put("/person/"+supervisor.getId())
                .content("{\"email\":\"email2@email.com\"," +
                        "\"firstname\":\"second\"," +
                        "\"lastname\":\"last\"," +
                        "\"expertise\":\"all\"," +
                        "\"branch\":\"VIANEN\"," +
                        "\"role\":\"EMPLOYEE\"," +
                        "\"employedSince\":\"2005-12-01\"," +
                        "\"supervisorId\":\""+ supervisor.getId().toString() +"\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("email2@email.com")))
                .andExpect(jsonPath("$.expertise", is("all")));
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ADMINISTRATOR")
    @DisplayName("Update person as administrator")
    void updatePersonAsAdministrator() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .put("/person/"+supervisor.getId())
                .content("{\"email\":\"email2@email.com\"," +
                        "\"firstname\":\"second\"," +
                        "\"lastname\":\"last\"," +
                        "\"expertise\":\"all\"," +
                        "\"branch\":\"VIANEN\"," +
                        "\"role\":\"EMPLOYEE\"," +
                        "\"employedSince\":\"2005-12-01\"," +
                        "\"supervisorId\":\""+ supervisor.getId().toString() +"\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("email2@email.com")))
                .andExpect(jsonPath("$.expertise", is("all")));
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("Update person as employee is not allowed")
    void updatePersonAsEmployee() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .put("/person/"+supervisor.getId())
                .content("{\"email\":\"email2@email.com\"," +
                        "\"firstname\":\"second\"," +
                        "\"lastname\":\"last\"," +
                        "\"expertise\":\"all\"," +
                        "\"branch\":\"VIANEN\"," +
                        "\"role\":\"EMPLOYEE\"," +
                        "\"employedSince\":\"2005-12-01\"," +
                        "\"supervisorId\":\""+ supervisor.getId().toString() +"\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Remove person as manager")
    void removePersonAsManager() throws Exception {
        PersonRequest dtoSupervisor = new PersonRequest();
        dtoSupervisor.email = "test3@email.com";
        dtoSupervisor.firstname = "Test";
        dtoSupervisor.lastname = "Person";
        dtoSupervisor.expertise = "none";
        dtoSupervisor.branch = "VIANEN";
        dtoSupervisor.role = "EMPLOYEE";
        dtoSupervisor.employedSince = "2005-12-01";
        dtoSupervisor.supervisorId = null;
        Person person = personService.createPerson(dtoSupervisor);
        personRepository.save(person);


        RequestBuilder request = MockMvcRequestBuilders
                .delete("/person/"+person.getId());

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "SECRETARY")
    @DisplayName("Remove person as secretary")
    void removePersonAsSecretary() throws Exception {
        PersonRequest dtoSupervisor = new PersonRequest();
        dtoSupervisor.email = "test3@email.com";
        dtoSupervisor.firstname = "Test";
        dtoSupervisor.lastname = "Person";
        dtoSupervisor.expertise = "none";
        dtoSupervisor.branch = "VIANEN";
        dtoSupervisor.role = "EMPLOYEE";
        dtoSupervisor.employedSince = "2005-12-01";
        dtoSupervisor.supervisorId = null;
        Person person = personService.createPerson(dtoSupervisor);
        personRepository.save(person);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/person/"+person.getId());

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ADMINISTRATOR")
    @DisplayName("Remove person as administrator")
    void removePersonAsAdministrator() throws Exception {
        PersonRequest dtoSupervisor = new PersonRequest();
        dtoSupervisor.email = "test3@email.com";
        dtoSupervisor.firstname = "Test";
        dtoSupervisor.lastname = "Person";
        dtoSupervisor.expertise = "none";
        dtoSupervisor.branch = "VIANEN";
        dtoSupervisor.role = "EMPLOYEE";
        dtoSupervisor.employedSince = "2005-12-01";
        dtoSupervisor.supervisorId = null;
        Person person = personService.createPerson(dtoSupervisor);
        personRepository.save(person);


        RequestBuilder request = MockMvcRequestBuilders
                .delete("/person/"+person.getId());

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("Remove person as employee is not allowed")
    void removePersonAsEmployee() throws Exception {
        PersonRequest dtoSupervisor = new PersonRequest();
        dtoSupervisor.email = "test3@email.com";
        dtoSupervisor.firstname = "Test";
        dtoSupervisor.lastname = "Person";
        dtoSupervisor.expertise = "none";
        dtoSupervisor.branch = "VIANEN";
        dtoSupervisor.role = "EMPLOYEE";
        dtoSupervisor.employedSince = "2005-12-01";
        dtoSupervisor.supervisorId = null;
        Person person = personService.createPerson(dtoSupervisor);
        personRepository.save(person);


        RequestBuilder request = MockMvcRequestBuilders
                .delete("/person/"+person.getId());

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Remove unknown person as manager")
    void removeUnknownPersonAsManager() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/person/" + UUID.randomUUID());

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Search person by name as manager")
    void searchPersonAsManager() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/person/search?name=Test")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").doesNotExist());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "SECRETARY")
    @DisplayName("Search person by name as secretary")
    void searchPersonAsSecretary() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/person/search?name=Test")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").doesNotExist());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ADMINISTRATOR")
    @DisplayName("Search person by name as administrator")
    void searchPersonAsAdministrator() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/person/search?name=Test")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").doesNotExist());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("Search person by name as employee is not allowed")
    void searchPersonAsEmployee() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.post("/person/search?name=second")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }
}