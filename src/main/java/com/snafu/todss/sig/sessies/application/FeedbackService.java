package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.FeedbackRepository;
import com.snafu.todss.sig.sessies.domain.Feedback;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import com.snafu.todss.sig.sessies.presentation.dto.request.FeedbackRequest;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
@Transactional
public class FeedbackService {
    private final FeedbackRepository FEEDBACK_REPOSITORY;
    private final SessionService SESSION_SERVICE;
    private final PersonService PERSON_SERVICE;

    public FeedbackService(FeedbackRepository feedbackRepository,
                           SessionService sessionService,
                           PersonService personService
    ) {
        this.FEEDBACK_REPOSITORY = feedbackRepository;
        this.SESSION_SERVICE = sessionService;
        this.PERSON_SERVICE = personService;
    }

    public Feedback getFeedbackById(UUID uuid) throws NotFoundException {
        return this.FEEDBACK_REPOSITORY.findById(uuid)
                .orElseThrow( () -> new NotFoundException("No feedback found with given id."));
    }

    public Feedback createFeedback(FeedbackRequest feedbackRequest) throws NotFoundException {
        Session session = this.SESSION_SERVICE.getSessionById(feedbackRequest.sessionId);
        Person person = this.PERSON_SERVICE.getPerson(feedbackRequest.personId);
        Feedback feedback = new Feedback(feedbackRequest.description, session, person);

        return FEEDBACK_REPOSITORY.save(feedback);
    }

    public void deleteFeedback(UUID uuid) {
        this.FEEDBACK_REPOSITORY.deleteById(uuid);
    }

}
