package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.CiTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(CiTestConfiguration.class)
@AutoConfigureMockMvc
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    String supervisorId;
    String id;

    @BeforeEach
    void setup() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/person")
                .content("email@email.com")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        MvcResult ra = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String dubbelepunt = Arrays.asList(ra.getResponse().getContentAsString().split(":")).get(1);
        supervisorId = Arrays.asList(dubbelepunt.split("\"")).get(1);

        RequestBuilder request2 = MockMvcRequestBuilders
                .get("/person")
                .content("email2@email.com")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        MvcResult ra2 = mockMvc.perform(request2)
                .andExpect(status().isOk())
                .andReturn();
        String dubbelepunt2 = Arrays.asList(ra2.getResponse().getContentAsString().split(":")).get(1);
        id = Arrays.asList(dubbelepunt2.split("\"")).get(1);
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Get person by id as manager")
    void getPersonAsManager() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/person/"+ supervisorId)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("email@email.com")))
                .andExpect(jsonPath("$.role", is("MANAGER")));
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "SECRETARY")
    @DisplayName("Get person by id as secretary")
    void getPersonAsSecretary() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/person/"+ supervisorId)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("email@email.com")))
                .andExpect(jsonPath("$.role", is("MANAGER")));
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("Get person by id as employee is not allowed")
    void getPersonAsEmployee() throws Exception { //todo expects 200?
        RequestBuilder request = MockMvcRequestBuilders
                .get("/person/"+ supervisorId)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Get person by email as manager")
    void getPersonByEmailAsManager() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/person")
                .content("email@email.com")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("email@email.com")))
                .andExpect(jsonPath("$.role", is("MANAGER")));
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "SECRETARY")
    @DisplayName("Get person by email as secretary")
    void getPersonByEmailAsSecretary() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/person")
                .content("email@email.com")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("email@email.com")))
                .andExpect(jsonPath("$.role", is("MANAGER")));
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("Get person by email as employee is not allowed") //todo expects 200?
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
                        "\"employedSince\":\"01/01/2021\"," +
                        "\"supervisorId\":\""+ supervisorId +"\"}")
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
                        "\"employedSince\":\"01/01/2021\"," +
                        "\"supervisorId\":\""+ supervisorId +"\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("test1.email@email.com")))
                .andExpect(jsonPath("$.role", is("EMPLOYEE")));
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("Create person as employee is not allowed")
    void createPersonAsEmployee() throws Exception { //todo expects 200?
        RequestBuilder request = MockMvcRequestBuilders
                .post("/person")
                .content("{\"email\":\"test.email@email.com\"," +
                        "\"firstname\":\"fourth\"," +
                        "\"lastname\":\"last\"," +
                        "\"expertise\":\"none\"," +
                        "\"branch\":\"VIANEN\"," +
                        "\"role\":\"EMPLOYEE\"," +
                        "\"employedSince\":\"01/01/2021\"," +
                        "\"supervisorId\":\""+ supervisorId +"\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Update person as manager")
    void updatePersonAsManager() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/person/"+ id)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("email2@email.com")))
                .andExpect(jsonPath("$.expertise", is("none")));


        request = MockMvcRequestBuilders
                .put("/person/"+id)
                .content("{\"email\":\"email2@email.com\"," +
                        "\"firstname\":\"second\"," +
                        "\"lastname\":\"last\"," +
                        "\"expertise\":\"all\"," +
                        "\"branch\":\"VIANEN\"," +
                        "\"role\":\"EMPLOYEE\"," +
                        "\"employedSince\":\"01/01/2000\"," +
                        "\"supervisorId\":\""+ supervisorId +"\"}")
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
                .get("/person/"+ id)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("email2@email.com")))
                .andExpect(jsonPath("$.expertise", is("none")));


        request = MockMvcRequestBuilders
                .put("/person/"+id)
                .content("{\"email\":\"email2@email.com\"," +
                        "\"firstname\":\"second\"," +
                        "\"lastname\":\"last\"," +
                        "\"expertise\":\"all\"," +
                        "\"branch\":\"VIANEN\"," +
                        "\"role\":\"EMPLOYEE\"," +
                        "\"employedSince\":\"01/01/2000\"," +
                        "\"supervisorId\":\""+ supervisorId +"\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("email2@email.com")))
                .andExpect(jsonPath("$.expertise", is("all")));
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("Update person as employee is not allowed")
    void updatePersonAsEmployee() throws Exception { //todo expects 200?
        RequestBuilder request = MockMvcRequestBuilders
                .put("/person/"+id)
                .content("{\"email\":\"email2@email.com\"," +
                        "\"firstname\":\"second\"," +
                        "\"lastname\":\"last\"," +
                        "\"expertise\":\"all\"," +
                        "\"branch\":\"VIANEN\"," +
                        "\"role\":\"EMPLOYEE\"," +
                        "\"employedSince\":\"01/01/2000\"," +
                        "\"supervisorId\":\""+ supervisorId +"\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Remove person as manager")
    void removePersonAsManager() throws Exception {
        RequestBuilder request2 = MockMvcRequestBuilders
                .get("/person")
                .content("test.email@email.com")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        MvcResult ra2 = mockMvc.perform(request2)
                .andExpect(status().isOk())
                .andReturn();
        String dubbelepunt2 = Arrays.asList(ra2.getResponse().getContentAsString().split(":")).get(1);
        String id = Arrays.asList(dubbelepunt2.split("\"")).get(1);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/person/"+id);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "SECRETARY")
    @DisplayName("Remove person as secretary")
    void removePersonAsSecretary() throws Exception {
        RequestBuilder request2 = MockMvcRequestBuilders
                .get("/person")
                .content("email3@email.com")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        MvcResult ra2 = mockMvc.perform(request2)
                .andExpect(status().isOk())
                .andReturn();
        String dubbelepunt2 = Arrays.asList(ra2.getResponse().getContentAsString().split(":")).get(1);
        String id = Arrays.asList(dubbelepunt2.split("\"")).get(1);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/person/"+id);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("Remove person as employee is not allowed")
    void removePersonAsEmployee() throws Exception { //todo expects 200?
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/person/" + UUID.randomUUID());

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
        RequestBuilder request = MockMvcRequestBuilders.post("/person/search")
                .content("{\"firstname\":\"second\"}")
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
        RequestBuilder request = MockMvcRequestBuilders.post("/person/search")
                .content("{\"firstname\":\"second\"}")
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
    void searchPersonAsEmployee() throws Exception { //todo expected 200?
        RequestBuilder request = MockMvcRequestBuilders.post("/person/search")
                .content("{\"firstname\":\"second\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }
}