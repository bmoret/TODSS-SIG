package com.snafu.todss.sig.sessies.application;

import com.snafu.todss.sig.sessies.data.SpecialInterestGroupRepository;
import com.snafu.todss.sig.sessies.domain.SpecialInterestGroup;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.presentation.dto.request.SpecialInterestGroupRequest;
import javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SpecialInterestGroupService {
    private final SpecialInterestGroupRepository SIG_REPOSITORY;
    private final PersonService PERSON_SERVICE;

    public SpecialInterestGroupService(SpecialInterestGroupRepository sigRepository, PersonService personService) {
        this.SIG_REPOSITORY = sigRepository;
        this.PERSON_SERVICE = personService;
    }

    public List<SpecialInterestGroup> getAllSpecialInterestGroups() {
        return this.SIG_REPOSITORY.findAll();
    }

    public SpecialInterestGroup getSpecialInterestGroupById(UUID id) throws NotFoundException {
        return this.SIG_REPOSITORY.findById(id)
                .orElseThrow(() -> new NotFoundException("No special interest group with given id"));
    }

    public SpecialInterestGroup createSpecialInterestGroup(SpecialInterestGroupRequest sigRequest) throws NotFoundException {
        Person manager = this.PERSON_SERVICE.getPerson(sigRequest.managerId);
        List<Person> organizers = getOrganizersWithIds(sigRequest.organizerIds);
        SpecialInterestGroup specialInterestGroup = new SpecialInterestGroup(sigRequest.subject, manager, organizers, new ArrayList<>());
        return SIG_REPOSITORY.save(specialInterestGroup);
    }

    private List<Person> getOrganizersWithIds (List<Long> organizerIds) throws NotFoundException {
        List<Person> organizers = new ArrayList<>();
        for (Long id : organizerIds) {
            organizers.add(PERSON_SERVICE.getPerson(id));
        }
        return organizers;
    }

    public SpecialInterestGroup updateSpecialInterestGroup(UUID id, SpecialInterestGroupRequest specialInterestGroupRequest) throws NotFoundException {
        SpecialInterestGroup specialInterestGroup = getSpecialInterestGroupById(id);
        specialInterestGroup.setSubject(specialInterestGroupRequest.subject);
        return this.SIG_REPOSITORY.save(specialInterestGroup);
    }

    public void deleteSpecialInterestGroup(UUID id) {
        this.SIG_REPOSITORY.deleteById(id);
    }}
