package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.FeedbackRepository;
import com.snafu.todss.sig.sessies.data.SessionRepository;
import com.snafu.todss.sig.sessies.domain.Feedback;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.types.PhysicalSession;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.presentation.dto.request.FeedbackRequest;
import com.snafu.todss.sig.sessies.presentation.dto.request.PersonRequest;
import javassist.NotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class FeedbackServiceIntegrationTest {
    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private PersonService personService;

    private Person person;

    private Feedback testFeedback;

    private Session session;

    @BeforeEach
    void setup() throws NotFoundException {
        PersonRequest dtoSupervisor = new PersonRequest();
            dtoSupervisor.email = "TestEmail@email.com";
            dtoSupervisor.firstname = "fourth";
            dtoSupervisor.lastname = "last";
            dtoSupervisor.expertise = "none";
            dtoSupervisor.branch = "VIANEN";
            dtoSupervisor.role = "EMPLOYEE";
            dtoSupervisor.employedSince = "2005-12-01";
            dtoSupervisor.supervisorId = null;
        Person supervisor = personService.createPerson(dtoSupervisor);

        PersonRequest dtoPerson = new PersonRequest();
            dtoPerson.email = "NogEenTestEmail@email.com";
            dtoPerson.firstname = "fourth";
            dtoPerson.lastname = "last";
            dtoPerson.expertise = "none";
            dtoPerson.branch = "VIANEN";
            dtoPerson.role = "EMPLOYEE";
            dtoPerson.employedSince = "2000-12-01";
            dtoPerson.supervisorId = supervisor.getId();
        person = personService.createPerson(dtoPerson);

        session = new PhysicalSession();
        sessionRepository.save(session);

        String example = "This is an example!";
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
    @DisplayName("Get feedback by id")
    void getFeedbackById_ReturnsFeedback() throws NotFoundException {
        Feedback feedback = feedbackService.getFeedbackById(testFeedback.getId());

        assertEquals(testFeedback, feedback);
    }

    @Test
    @DisplayName("Get feedback by unknown id throws exception")
    void getFeedbackByUnknownId_ThrowsError() {
        assertThrows(
                NotFoundException.class,
                () -> feedbackService.getFeedbackById(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Get feedback by known session")
    void getFeedbackBySession_ReturnsFeedback() throws NotFoundException {
        List<Feedback> feedback = feedbackService.getFeedbackBySession(session.getId());

        assertEquals(List.of(testFeedback), feedback);
    }

    @Test
    @DisplayName("Get feedback by unknown session id throws exception")
    void getFeedbackByUnknownSessionId_ThrowsError() {
        assertThrows(
                NotFoundException.class,
                () -> feedbackService.getFeedbackBySession(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Creating feedback returns a newly made feedback")
    void createFeedback_CreatesInstance() throws NotFoundException {
        FeedbackRequest feedbackRequest = new FeedbackRequest();
        feedbackRequest.description = "An example of feedback someone would give.";
        feedbackRequest.personId = person.getId();
        feedbackRequest.sessionId = session.getId();

        Feedback feedback = feedbackService.createFeedback(feedbackRequest);

        assertEquals(feedback.getClass(), Feedback.class);
        assertEquals(feedback.getSession(), session);
        assertEquals(feedback.getPerson(), person);
    }

    @Test
    @DisplayName("Deleting feedback deletes feedback")
    void deleteFeedback_DeletesFeedback() throws NotFoundException {
        feedbackService.deleteFeedback(testFeedback.getId());

        assertEquals(Collections.emptyList(), feedbackRepository.findAll());
    }

    @Test
    @DisplayName("Deleting feedback does not throw")
    void deleteFeedback_DoesNotThrow() {
        assertDoesNotThrow(() -> feedbackService.deleteFeedback(testFeedback.getId()));
    }

    @Test
    @DisplayName("Deleting feedback that does not exist throws eception")
    void deleteUnknownFeedback_ThrowsException() {
        assertThrows(
                NotFoundException.class,
                () -> feedbackService.deleteFeedback(UUID.randomUUID()));
    }
}