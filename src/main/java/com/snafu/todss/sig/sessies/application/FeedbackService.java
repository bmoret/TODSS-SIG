package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.FeedbackRepository;
import com.snafu.todss.sig.sessies.domain.Feedback;
import com.snafu.todss.sig.sessies.presentation.dto.request.FeedbackRequest;
import javassist.NotFoundException;

import java.util.UUID;

public class FeedbackService {
    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public Feedback getFeedbackById(UUID uuid) throws NotFoundException {
        return this.feedbackRepository.findById(uuid)
                .orElseThrow( () -> new NotFoundException("No feedback found with given id."));
    }

    //getfeedbackbysession?

    public Feedback createFeedback(FeedbackRequest feedbackRequest) {
        Feedback feedback = new Feedback(feedbackRequest.description); // needs to add person & session too

        return feedbackRepository.save(feedback);
    }

    public void deleteFeedback(UUID uuid) {
        this.feedbackRepository.deleteById(uuid);
    }

}
