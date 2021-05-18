package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.sessies.application.FeedbackService;
import com.snafu.todss.sig.sessies.domain.Feedback;
import com.snafu.todss.sig.sessies.presentation.dto.request.FeedbackRequest;
import com.snafu.todss.sig.sessies.presentation.dto.response.FeedbackResponse;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RolesAllowed("ROLE_ADMINISTRATOR")
@RestController
@RequestMapping("/feedback")
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

    @RolesAllowed({"ROLE_MANAGER", "ROLE_SECRETARY", "ROLE_ORGANIZER"})
    @GetMapping("/{uuid}")
    public ResponseEntity<FeedbackResponse> getFeedbackById(@PathVariable UUID uuid) throws NotFoundException {
        Feedback feedback = this.SERVICE.getFeedbackById(uuid);

        return new ResponseEntity<>(convertFeedbackToResponse(feedback), HttpStatus.OK);
    }

    @RolesAllowed({"ROLE_MANAGER", "ROLE_SECRETARY", "ROLE_ORGANIZER"})
    @GetMapping("/sessions/{uuid}")
    public ResponseEntity<List<FeedbackResponse>> getFeedbackBySessionId(@PathVariable UUID uuid) throws NotFoundException {
        List<FeedbackResponse> responses = new ArrayList<>();
        List<Feedback> feedbacks = this.SERVICE.getFeedbackBySession(uuid);

        for (Feedback feedback : feedbacks) {
            responses.add(convertFeedbackToResponse(feedback));
        }

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PermitAll
    @PostMapping
    public ResponseEntity<FeedbackResponse> createFeedback(@Valid @RequestBody FeedbackRequest feedbackRequest)
            throws NotFoundException {
        Feedback feedback = this.SERVICE.createFeedback(feedbackRequest);

        return new ResponseEntity<>(convertFeedbackToResponse(feedback), HttpStatus.CREATED);
    }

    @PermitAll
    @DeleteMapping("/{uuid}")
    public ResponseEntity<FeedbackResponse> deleteFeedbackById(@PathVariable UUID uuid) throws NotFoundException {
        this.SERVICE.deleteFeedback(uuid);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
