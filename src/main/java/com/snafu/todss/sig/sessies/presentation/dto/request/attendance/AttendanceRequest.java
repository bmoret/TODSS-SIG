package com.snafu.todss.sig.sessies.presentation.dto.request.attendance;


import javax.validation.constraints.NotNull;

public class AttendanceRequest {
    @NotNull
    public String state;
    @NotNull
    public boolean speaker;
}
