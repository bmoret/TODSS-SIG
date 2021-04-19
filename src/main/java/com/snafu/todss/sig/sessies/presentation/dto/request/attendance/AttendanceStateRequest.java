package com.snafu.todss.sig.sessies.presentation.dto.request.attendance;

import com.snafu.todss.sig.sessies.domain.StateAttendance;
import javax.validation.constraints.NotNull;

public class AttendanceStateRequest {
    @NotNull
    public StateAttendance state;
}
