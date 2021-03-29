package com.snafu.todss.sig.sessies.presentation.dto.request;

import javax.validation.constraints.NotNull;

public class AttendanceUpdateRequest {
    @NotNull
    public boolean confirmed;
    @NotNull
    public boolean absence;
    @NotNull
    public boolean speaker;
}
