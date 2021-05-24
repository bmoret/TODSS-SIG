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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

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
    @DisplayName("Get person by id")
    void getPerson() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/person/"+ supervisorId);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("email@email.com")))
                .andExpect(jsonPath("$.role", is("MANAGER")));
    }

    // {"id":"aa76906f-e3b2-4679-841f-3b767524847a","email":"email@email.com","firstname":"first","lastname":"last","expertise":"none","employedSince":"2000-01-01","supervisor":null,"branch":"VIANEN","role":"MANAGER"}
    @Test
    @DisplayName("Get person by email")
    void getPersonByEmail() throws Exception {
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
    void createPerson() throws Exception {
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
    void updatePerson() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/person/"+ id);

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
    void removePerson() throws Exception {
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

        // TODO Hoe kan ik testen of die niet gevonden wordt
//        RequestBuilder finalRequest =  MockMvcRequestBuilders
//                .get("/person/"+ id);
//
//        assertThrows(
//                NotFoundException.class,
//                () -> mockMvc.perform(finalRequest)
//        );
    }

    @Test
    @DisplayName("search provides closest results")
    void search() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/person/search")
                .content("{\"searchTerm\":\"ali\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("update state of attendance throws when attendance not found")
    void searchThrows() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/person/search")
                .content("{\"searchTerm\":\"\"}")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(content().contentType("application/json"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.Error").value("vul de zoekbalk"));
    }
}