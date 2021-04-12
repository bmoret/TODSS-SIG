package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.sessies.application.FeedbackService;
import com.snafu.todss.sig.sessies.application.PersonService;
import com.snafu.todss.sig.sessies.data.FeedbackRepository;
import com.snafu.todss.sig.sessies.domain.Feedback;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.presentation.dto.request.FeedbackRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.PersonRequest;
import javassist.NotFoundException;
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
    private FeedbackService feedbackService;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private PersonService personService;

    private Person person;

    private Session session;

    @BeforeEach
    void setup() throws NotFoundException {
        PersonRequest dtoSupervisor = new PersonRequest();
        dtoSupervisor.email = "email@email.com";
        dtoSupervisor.firstname = "fourth";
        dtoSupervisor.lastname = "last";
        dtoSupervisor.expertise = "none";
        dtoSupervisor.branch = "VIANEN";
        dtoSupervisor.role = "EMPLOYEE";
        dtoSupervisor.employedSince = "01/01/2021";
        dtoSupervisor.supervisorId = null; //todo does not work
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

        session = new PhysicalSession();
    }

    @AfterEach
    void tearDown() throws NotFoundException {
        this.feedbackRepository.deleteAll();
        this.personService.removePerson(person.getId());
    }

    @Test
    @DisplayName("Get feedback by id returns the feedback")
    void getFeedbackById() throws Exception {
        String example = "This is an example!";
        Feedback feedback = new Feedback(example, session, person);

        RequestBuilder request = MockMvcRequestBuilders.get("/feedback/" + feedback.getId());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
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
        RequestBuilder request = MockMvcRequestBuilders.get("/feedback/session" + session.getId());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("Create feedback returns newly made feedback")
    void createFeedback_ReturnsFeedback() throws Exception {
        FeedbackRequest feedbackRequest = new FeedbackRequest();
        feedbackRequest.description = "An example of feedback someone would give.";
        feedbackRequest.personId = person.getId();
        feedbackRequest.sessionId = session.getId();

        RequestBuilder request = MockMvcRequestBuilders.post("/feedback");
        //todo send request to backend

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("Deleting a feedback returns no content without a body")
    void deleteFeedback_ReturnsNoContentWithoutBody() throws Exception {
        String example = "An example of feedback someone would give.";
        Feedback feedback = new Feedback(example, session, person);
        this.feedbackRepository.save(feedback);

        RequestBuilder request= MockMvcRequestBuilders.delete("/feedback/" + feedback.getId());

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