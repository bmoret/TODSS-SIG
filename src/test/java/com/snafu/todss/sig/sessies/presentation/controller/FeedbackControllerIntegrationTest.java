package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.security.config.SecurityConfig;
import com.snafu.todss.sig.sessies.application.PersonService;
import com.snafu.todss.sig.sessies.data.FeedbackRepository;
import com.snafu.todss.sig.sessies.data.SessionRepository;
import com.snafu.todss.sig.sessies.data.SpringPersonRepository;
import com.snafu.todss.sig.sessies.domain.Feedback;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.presentation.dto.request.PersonRequest;
import javassist.NotFoundException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
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
    private SpringPersonRepository personRepository;

    @Autowired
    private PersonService personService;

    private Person person;

    private Session session;

    private Feedback testFeedback;


    @BeforeEach
    void setUp() throws NotFoundException {
        PersonRequest dtoSupervisor = new PersonRequest();
        dtoSupervisor.email = "test2@email.com";
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
        this.personRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Get feedback by id as manager returns the feedback")
    void getFeedbackByIdAsManager() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/feedback/" + testFeedback.getId())
                                                        .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testFeedback.getId().toString()))
                .andExpect(jsonPath("$.description").value(testFeedback.getDescription()))
                .andExpect(jsonPath("$.person").exists())
                .andExpect(jsonPath("$.session").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "SECRETARY")
    @DisplayName("Get feedback by id as secretary returns the feedback")
    void getFeedbackByIdAsSecretary() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/feedback/" + testFeedback.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testFeedback.getId().toString()))
                .andExpect(jsonPath("$.description").value(testFeedback.getDescription()))
                .andExpect(jsonPath("$.person").exists())
                .andExpect(jsonPath("$.session").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ORGANIZER")
    @DisplayName("Get feedback by id as organizer returns the feedback")
    void getFeedbackByIdAsOrganizer() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/feedback/" + testFeedback.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testFeedback.getId().toString()))
                .andExpect(jsonPath("$.description").value(testFeedback.getDescription()))
                .andExpect(jsonPath("$.person").exists())
                .andExpect(jsonPath("$.session").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ADMINISTRATOR")
    @DisplayName("Get feedback by id as administrator returns the feedback")
    void getFeedbackByIdAsAdministrator() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/feedback/" + testFeedback.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testFeedback.getId().toString()))
                .andExpect(jsonPath("$.description").value(testFeedback.getDescription()))
                .andExpect(jsonPath("$.person").exists())
                .andExpect(jsonPath("$.session").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("Get feedback by id as employee is not allowed")
    void getFeedbackByIdAsEmployee() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/feedback/" + testFeedback.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Get feedback by unknown id as manager throws exception")
    void getFeedbackByUnknownIdAsManager_ThrowsException() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/feedback/" + UUID.randomUUID());

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Get feedback by session id as manager returns the feedback")
    void getFeedbackBySessionAsManager_ReturnsFeedback() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/feedback/sessions/" + session.getId())
                                                        .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].description").value(testFeedback.getDescription()))
                .andExpect(jsonPath("$[0].person").exists())
                .andExpect(jsonPath("$[0].session").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "SECRETARY")
    @DisplayName("Get feedback by session id as secretary returns the feedback")
    void getFeedbackBySessionAsSecretary_ReturnsFeedback() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/feedback/sessions/" + session.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].description").value(testFeedback.getDescription()))
                .andExpect(jsonPath("$[0].person").exists())
                .andExpect(jsonPath("$[0].session").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ORGANIZER")
    @DisplayName("Get feedback by session id as organizer returns the feedback")
    void getFeedbackBySessionAsOrganizer_ReturnsFeedback() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/feedback/sessions/" + session.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].description").value(testFeedback.getDescription()))
                .andExpect(jsonPath("$[0].person").exists())
                .andExpect(jsonPath("$[0].session").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "ADMINISTRATOR")
    @DisplayName("Get feedback by session id as administrator returns the feedback")
    void getFeedbackBySessionAsAdministrator_ReturnsFeedback() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/feedback/sessions/" + session.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].description").value(testFeedback.getDescription()))
                .andExpect(jsonPath("$[0].person").exists())
                .andExpect(jsonPath("$[0].session").exists());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("Get feedback by session id as employee is not allowed")
    void getFeedbackBySessionAsEmployee() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/feedback/sessions/" + session.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "MANAGER")
    @DisplayName("Get feedback by unknown session id as manager throws exception")
    void getFeedbackByUnknownSession_ThrowsException() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/feedback/sessions/" + UUID.randomUUID());

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("Create feedback as employee returns newly made feedback")
    void createFeedback_ReturnsFeedback() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("description", "An example of feedback someone would give.");
        jsonObject.put("personId", person.getId().toString());
        jsonObject.put("sessionId", session.getId().toString());

        RequestBuilder request = MockMvcRequestBuilders
                .post("/feedback")
                .contentType(MediaType.APPLICATION_JSON)
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
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("Deleting a feedback as employee returns no content without a body")
    void deleteFeedback_ReturnsNoContentWithoutBody() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.delete("/feedback/" + testFeedback.getId())
                                                        .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @WithMockUser(username = "TestUser", roles = "EMPLOYEE")
    @DisplayName("Deleting a feedback that does not exist as employee throws not found")
    void deleteNonExistingFeedback_ThrowsNotFound() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.delete("/feedback/" + UUID.randomUUID())
                                                        .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

}