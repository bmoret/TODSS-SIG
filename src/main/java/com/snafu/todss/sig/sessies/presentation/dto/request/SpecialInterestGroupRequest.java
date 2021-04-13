package com.snafu.todss.sig.sessies.presentation.dto.request;

import com.sun.istack.NotNull;

import java.util.List;
import java.util.UUID;

public class SpecialInterestGroupRequest {
    @NotNull
    public String subject;

    @NotNull
    public UUID managerId;

    @NotNull
    public List<UUID> organizerIds;
}
