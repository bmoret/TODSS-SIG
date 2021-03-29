package com.snafu.todss.sig.sessies.presentation.dto.request;

import javax.validation.constraints.NotNull;

public class AttendanceRequest {
    @NotNull
    public boolean isConfirmed;

    @NotNull
    public boolean isAbsence;

    @NotNull
    public boolean isSpeaker;
}
