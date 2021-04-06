package com.snafu.todss.sig.sessies.presentation.dto.request;

import com.snafu.todss.sig.sessies.domain.StateAttendance;

import javax.validation.constraints.NotNull;

public class AttendanceRequest {
    @NotNull
    public StateAttendance state;

    @NotNull
    public boolean isSpeaker;
}
