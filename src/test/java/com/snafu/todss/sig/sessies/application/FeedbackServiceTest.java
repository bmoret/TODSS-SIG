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
    private static FeedbackService FEEDBACK_SERVICE;
    private static Feedback FEEDBACK;


    @BeforeEach
    void setUp() {
        FEEDBACK_SERVICE = new FeedbackService(FEEDBACK_REPOSITORY, SESSION_SERVICE, PERSON_SERVICE);
        String example = "This is an example!";
        Session session = mock(Session.class);
        Person person = mock(Person.class);
        FEEDBACK = new Feedback(example, session, person);
    }

    @Test
    @DisplayName("Get feedback by id returns existing feedback")
    void getFeedbackById_ReturnsCorrectFeedback() throws NotFoundException {
        when(FEEDBACK_REPOSITORY.findById(any())).thenReturn(Optional.of(FEEDBACK));

        Feedback feedback = FEEDBACK_SERVICE.getFeedbackById(any());

        assertEquals(FEEDBACK, feedback);
    }

    @Test
    @DisplayName("Get feedback by id throws exception when no session with the id was found")
    void getFeedbackById_ThrowsWhenDoesNotExist() throws NotFoundException {
        when(FEEDBACK_REPOSITORY.findById(any())).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> FEEDBACK_SERVICE.getFeedbackById(any(UUID.class)));
    }

    @Test
    @DisplayName("Create feedback, creates feedback")
    void createFeedback_CreatesInstance() throws NotFoundException {
        when(SESSION_SERVICE.getSessionById(any(UUID.class))).thenReturn(mock(Session.class));
        when(PERSON_SERVICE.getPerson(any(UUID.class))).thenReturn(mock(Person.class));

        String example = "This is an example of a description!";
        UUID personUUID = UUID.randomUUID();
        UUID sessionUUID = UUID.randomUUID();
        FeedbackRequest feedbackRequest = new FeedbackRequest();
        feedbackRequest.description = example;
        feedbackRequest.personId = personUUID;
        feedbackRequest.sessionId = sessionUUID;

        Feedback feedback = FEEDBACK_SERVICE.createFeedback(feedbackRequest);

        assertNotNull(feedback);
    }

    @Test
    @DisplayName("Delete feedback deletes feedback")
    void deleteFeedback_DeletesFeedback() throws NotFoundException {
        when(FEEDBACK_REPOSITORY.existsById(FEEDBACK.getId())).thenReturn(true);

        FEEDBACK_SERVICE.deleteFeedback(FEEDBACK.getId());

        verify(FEEDBACK_REPOSITORY, times(1)).deleteById(FEEDBACK.getId());
    }

    @ParameterizedTest
    @MethodSource("provideSessionAndFeedbackList")
    @DisplayName("Get feedbacks by sessions, returns list of feedback")
    void getFeedbackBySession_ReturnsListFeedback(Session session, List<Feedback> expectedResult) throws NotFoundException {
        when(SESSION_SERVICE.getSessionById(any(UUID.class))).thenReturn(session);
        when(FEEDBACK_REPOSITORY.findBySession(any())).thenReturn(expectedResult);

        List<Feedback> feedbacks = FEEDBACK_SERVICE.getFeedbackBySession(any(UUID.class));

        assertEquals(expectedResult, feedbacks);
    }

     static Stream<Arguments> provideSessionAndFeedbackList() {
        return Stream.of(
                Arguments.of(mock(Session.class), List.of()),
                Arguments.of(mock(Session.class), List.of(FEEDBACK)),
                Arguments.of(mock(Session.class), List.of(FEEDBACK, FEEDBACK))
        );
    }

}