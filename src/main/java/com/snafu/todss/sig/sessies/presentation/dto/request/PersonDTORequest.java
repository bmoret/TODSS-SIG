package com.snafu.todss.sig.sessies.presentation.dto.request;

import com.snafu.todss.sig.sessies.domain.Person;
import com.snafu.todss.sig.sessies.domain.enums.Branch;
import com.snafu.todss.sig.sessies.domain.enums.Role;

import java.time.LocalDateTime;

public class PersonDTORequest {
    public String email,
            firstname,
            lastname,
            expertise,
            branch,
            role,
            employedSince;
    public Long supervisorId;
}
