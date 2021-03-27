package com.snafu.todss.sig.sessies.presentation.dto.request;

import com.snafu.todss.sig.sessies.domain.person.Person;
import com.sun.istack.NotNull;

import java.util.List;

public class SpecialInterestGroupRequest {
    @NotNull
    public String subject;

    @NotNull
    public Long managerId;

    @NotNull
    public List<Long> organizerIds;
}
