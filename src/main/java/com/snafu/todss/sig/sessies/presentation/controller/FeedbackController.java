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
    private final FeedbackService SERVICE;

    public FeedbackController(FeedbackService service) {
        this.SERVICE = service;
    }

    private FeedbackResponse convertFeedbackToResponse(Feedback feedback) {
        return new FeedbackResponse(
                feedback.getId(),
                feedback.getDescription(),
                feedback.getPerson(),
                feedback.getSession()
        );
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<FeedbackResponse> getFeedbackById(@PathVariable UUID uuid) throws NotFoundException {
        Feedback feedback = this.SERVICE.getFeedbackById(uuid);

        return new ResponseEntity<>(convertFeedbackToResponse(feedback), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<FeedbackResponse> createFeedback(@Valid @RequestBody FeedbackRequest feedbackRequest) throws NotFoundException {
        Feedback feedback = this.SERVICE.createFeedback(feedbackRequest);

        return new ResponseEntity<>(convertFeedbackToResponse(feedback), HttpStatus.CREATED);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<FeedbackResponse> deleteFeedbackById(@PathVariable UUID uuid) {
        this.SERVICE.deleteFeedback(uuid);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
