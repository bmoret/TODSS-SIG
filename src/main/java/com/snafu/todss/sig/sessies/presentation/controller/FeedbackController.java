package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.sessies.application.FeedbackService;
import com.snafu.todss.sig.sessies.domain.Feedback;
import com.snafu.todss.sig.sessies.presentation.dto.request.FeedbackRequest;
import com.snafu.todss.sig.sessies.presentation.dto.response.FeedbackResponse;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

public class FeedbackController {
    private FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    private FeedbackResponse convertFeedbackToResponse(Feedback feedback) {
        FeedbackResponse feedbackResponse = new FeedbackResponse();
        feedbackResponse.id = feedback.getId();
        feedbackResponse.description = feedback.getDescription();
        // add the rest too

        return feedbackResponse;
    }

    @GetMapping("/id")
    public ResponseEntity<FeedbackResponse> getFeedbackById(@PathVariable UUID uuid) throws NotFoundException {
        Feedback feedback = this.feedbackService.getFeedbackById(uuid);

        return new ResponseEntity<>(convertFeedbackToResponse(feedback), HttpStatus.OK);
    }

    //getfeedbackbysession?

    @PostMapping
    public ResponseEntity<FeedbackResponse> createFeedback(@Valid @RequestBody FeedbackRequest feedbackRequest) {
        Feedback feedback = this.feedbackService.createFeedback(feedbackRequest);

        return new ResponseEntity<>(convertFeedbackToResponse(feedback), HttpStatus.CREATED);
    }

    @DeleteMapping("/id")
    public ResponseEntity<FeedbackResponse> deleteFeedbackById(@PathVariable UUID uuid) {
        this.feedbackService.deleteFeedback(uuid);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
