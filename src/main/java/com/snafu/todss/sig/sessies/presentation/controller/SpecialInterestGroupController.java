package com.snafu.todss.sig.sessies.presentation.controller;

import com.snafu.todss.sig.sessies.application.SpecialInterestGroupService;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.presentation.dto.request.SpecialInterestGroupRequest;
import com.snafu.todss.sig.sessies.presentation.dto.response.SpecialInterestGroupResponse;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sig")
public class SpecialInterestGroupController {
    private SpecialInterestGroupService specialInterestGroupService;

    public SpecialInterestGroupController(SpecialInterestGroupService specialInterestGroupService) {
        this.specialInterestGroupService = specialInterestGroupService;
    }

    private SpecialInterestGroupResponse convertSpecialInterestGroupToResponse(SpecialInterestGroup specialInterestGroup) {
        SpecialInterestGroupResponse response = new SpecialInterestGroupResponse();
        response.id = specialInterestGroup.getId();
        response.subject = specialInterestGroup.getSubject();
        return response;
    }

    @GetMapping
    public ResponseEntity<List<SpecialInterestGroupResponse>> getAllSpecialInterestGroups() {
        List<SpecialInterestGroup> specialInterestGroups = this.specialInterestGroupService.getAllSpecialInterestGroups();
        List<SpecialInterestGroupResponse> responses = new ArrayList<>();
        for (SpecialInterestGroup specialInterestGroup : specialInterestGroups) {
            responses.add(convertSpecialInterestGroupToResponse(specialInterestGroup));
        }
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecialInterestGroupResponse> getSpecialInterestGroup(@PathVariable UUID id) throws NotFoundException {
        SpecialInterestGroup specialInterestGroup = this.specialInterestGroupService.getSpecialInterestGroupById(id);
        return new ResponseEntity<>(convertSpecialInterestGroupToResponse(specialInterestGroup), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SpecialInterestGroupResponse> createSpecialInterestGroup
            (@Valid @RequestBody SpecialInterestGroupRequest specialInterestGroupRequest
            ) throws NotFoundException {
        SpecialInterestGroup specialInterestGroup = this.specialInterestGroupService.createSpecialInterestGroup(specialInterestGroupRequest);
        return new ResponseEntity<>(convertSpecialInterestGroupToResponse(specialInterestGroup), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecialInterestGroupResponse> updateSpecialInterestGroup(
            @PathVariable UUID id,
            @Valid @RequestBody SpecialInterestGroupRequest specialInterestGroupRequest
    ) throws NotFoundException {
        SpecialInterestGroup specialInterestGroup = this.specialInterestGroupService.updateSpecialInterestGroup(
                id,
                specialInterestGroupRequest
        );
        return new ResponseEntity<>(convertSpecialInterestGroupToResponse(specialInterestGroup), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecialInterestGroup(@PathVariable UUID id) {
        this.specialInterestGroupService.deleteSpecialInterestGroup(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
