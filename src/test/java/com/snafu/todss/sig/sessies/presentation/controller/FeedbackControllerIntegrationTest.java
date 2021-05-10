package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.sessies.application.PersonService;
import com.snafu.todss.sig.sessies.data.FeedbackRepository;
import com.snafu.todss.sig.sessies.data.SessionRepository;
import com.snafu.todss.sig.sessies.domain.Feedback;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.presentation.dto.request.PersonRequest;
import javassist.NotFoundException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FeedbackControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private PersonService personService;

    private Person person;

    private Session session;

    private Feedback testFeedback;


    @BeforeEach
    void setup() throws NotFoundException {
        PersonRequest dtoSupervisor = new PersonRequest();
        dtoSupervisor.email = "eenemail@email.com";
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

        String example = "This is an example!";

        session = new PhysicalSession();
        sessionRepository.save(session);

        testFeedback = new Feedback(example, session, person);
        this.feedbackRepository.save(testFeedback);
    }

    @AfterEach
    void tearDown() throws NotFoundException {
        this.feedbackRepository.deleteAll();
        this.personService.removePerson(person.getId());
        this.sessionRepository.deleteAll();
    }

    @Test
    @DisplayName("Get feedback by id returns the feedback")
    void getFeedbackById() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/feedback/" + testFeedback.getId());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(testFeedback.getId().toString()))
                .andExpect(jsonPath("$.description").value(testFeedback.getDescription()))
                .andExpect(jsonPath("$.person").exists())
                .andExpect(jsonPath("$.session").exists());
    }

    @Test
    @DisplayName("Get feedback by unknown id throws exception")
    void getFeedbackByUnknownId_ThrowsException() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/feedback/" + UUID.randomUUID());

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("Get feedback by session id returns the feedback")
    void getFeedbackBySession_ReturnsFeedback() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/feedback/sessions/" + session.getId());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].description").value(testFeedback.getDescription()))
                .andExpect(jsonPath("$[0].person").exists())
                .andExpect(jsonPath("$[0].session").exists());
    }

    @Test
    @DisplayName("Get feedback by unknown session id returns the feedback")
    void getFeedbackByUnknownSession_ThrowsException() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/feedback/sessions/" + UUID.randomUUID());

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("Create feedback returns newly made feedback")
    void createFeedback_ReturnsFeedback() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("description", "An example of feedback someone would give.");
        jsonObject.put("personId", person.getId().toString());
        jsonObject.put("sessionId", session.getId().toString());

        RequestBuilder request = MockMvcRequestBuilders
                .post("/feedback")
                .contentType("application/json")
                .content(jsonObject.toString());

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.description")
                        .value("An example of feedback someone would give."))
                .andExpect(jsonPath("$.person").exists())
                .andExpect(jsonPath("$.session").exists());
    }

    @Test
    @DisplayName("Deleting a feedback returns no content without a body")
    void deleteFeedback_ReturnsNoContentWithoutBody() throws Exception {
        RequestBuilder request= MockMvcRequestBuilders.delete("/feedback/" + testFeedback.getId());

        mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @DisplayName("Deleting a feedback that does not exist throws not found")
    void deleteNonExistingFeedback_ThrowsNotFound() throws Exception {
        RequestBuilder request= MockMvcRequestBuilders.delete("/feedback/" + UUID.randomUUID());

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

}