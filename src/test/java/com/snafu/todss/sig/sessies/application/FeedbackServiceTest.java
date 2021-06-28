package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.FeedbackRepository;
import com.snafu.todss.sig.sessies.domain.Feedback;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.presentation.dto.request.FeedbackRequest;
import javassist.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FeedbackServiceTest {
    private static final FeedbackRepository FEEDBACK_REPOSITORY = mock(FeedbackRepository.class);
    private static final SessionService SESSION_SERVICE = mock(SessionService.class);
    private static final PersonService PERSON_SERVICE = mock(PersonService.class);
    private static FeedbackService feedbackService;
    private static Feedback testFeedback;
    private static Person person;
    private static Session session;


    @BeforeEach
    void setUp() {
        feedbackService = new FeedbackService(FEEDBACK_REPOSITORY, SESSION_SERVICE, PERSON_SERVICE);
        String example = "This is an example!";
        session = mock(Session.class);
        person = mock(Person.class);
        testFeedback = new Feedback(example, session, person);
    }

    @Test
    @DisplayName("Get feedback by id returns existing feedback")
    void getFeedbackById_ReturnsCorrectFeedback() throws NotFoundException {
        when(FEEDBACK_REPOSITORY.findById(any())).thenReturn(Optional.of(testFeedback));

        Feedback feedback = feedbackService.getFeedbackById(any());

        assertEquals(testFeedback, feedback);
    }

    @Test
    @DisplayName("Get feedback by id throws exception when no session with the id was found")
    void getFeedbackById_ThrowsWhenDoesNotExist() {
        when(FEEDBACK_REPOSITORY.findById(any())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> feedbackService.getFeedbackById(any(UUID.class)));
    }

    @Test
    @DisplayName("Create feedback, creates feedback")
    void createFeedback_CreatesInstance() throws NotFoundException {
        when(SESSION_SERVICE.getSessionById(any(UUID.class))).thenReturn(session);
        when(PERSON_SERVICE.getPersonById(any(UUID.class))).thenReturn(person);

        FeedbackRequest feedbackRequest = new FeedbackRequest();
        feedbackRequest.description = "This is an example of a description!";
        feedbackRequest.personId = UUID.randomUUID();
        feedbackRequest.sessionId = UUID.randomUUID();

        Feedback feedback = feedbackService.createFeedback(feedbackRequest);

        assertNotNull(feedback);
    }

    @Test
    @DisplayName("Delete feedback deletes feedback")
    void deleteFeedback_DeletesFeedback() throws NotFoundException {
        when(FEEDBACK_REPOSITORY.findById(testFeedback.getId())).thenReturn(Optional.of(testFeedback));
        when(FEEDBACK_REPOSITORY.existsById(testFeedback.getId())).thenReturn(true);

        feedbackService.deleteFeedback(testFeedback.getId());

        verify(FEEDBACK_REPOSITORY, times(1)).delete(testFeedback);
    }

    @ParameterizedTest
    @MethodSource("provideSessionAndFeedbackList")
    @DisplayName("Get feedback by session id, returns list of feedback")
    void getFeedbackBySession_ReturnsListFeedback(Session session, List<Feedback> expectedResult) throws NotFoundException {
        when(SESSION_SERVICE.getSessionById(any(UUID.class))).thenReturn(session);
        when(FEEDBACK_REPOSITORY.findBySession(any())).thenReturn(expectedResult);

        List<Feedback> feedback = feedbackService.getFeedbackBySession(any(UUID.class));

        assertEquals(expectedResult, feedback);
    }

     static Stream<Arguments> provideSessionAndFeedbackList() {
        return Stream.of(
                Arguments.of(mock(Session.class), List.of()),
                Arguments.of(mock(Session.class), List.of(testFeedback)),
                Arguments.of(mock(Session.class), List.of(testFeedback, testFeedback))
        );
    }

}