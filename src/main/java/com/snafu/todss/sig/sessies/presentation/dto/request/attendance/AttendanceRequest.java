package com.snafu.todss.sig.sessies.presentation.dto.request.attendance;

import com.snafu.todss.sig.sessies.domain.AttendanceState;

import javax.validation.constraints.NotNull;

public class AttendanceRequest {
    @NotNull
    public AttendanceState state;
    @NotNull
    public boolean speaker;
}
