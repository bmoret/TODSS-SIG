package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.SpecialInterestGroupRepository;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.presentation.dto.request.SpecialInterestGroupRequest;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SpecialInterestGroupService {
    private final SpecialInterestGroupRepository specialInterestGroupRepository;

    public SpecialInterestGroupService(SpecialInterestGroupRepository specialInterestGroupRepository) {
        this.specialInterestGroupRepository = specialInterestGroupRepository;
    }

    public List<SpecialInterestGroup> getAllSpecialInterestGroups() {
        return this.specialInterestGroupRepository.findAll();
    }

    public SpecialInterestGroup getSpecialInterestGroupById(UUID id) throws NotFoundException {
        return this.specialInterestGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No special interest group with given id"));
    }

    public SpecialInterestGroup createSpecialInterestGroup(SpecialInterestGroupRequest specialInterestGroupRequest) {
        SpecialInterestGroup specialInterestGroup = new SpecialInterestGroup(specialInterestGroupRequest.subject);
        return specialInterestGroupRepository.save(specialInterestGroup);
    }

    public SpecialInterestGroup updateSpecialInterestGroup(UUID id, SpecialInterestGroupRequest specialInterestGroupRequest) throws NotFoundException {
        SpecialInterestGroup specialInterestGroup = getSpecialInterestGroupById(id);
        specialInterestGroup.setSubject(specialInterestGroupRequest.subject);
        return this.specialInterestGroupRepository.save(specialInterestGroup);
    }

    public void deleteSpecialInterestGroup(UUID id) {
        this.specialInterestGroupRepository.deleteById(id);
    }}
