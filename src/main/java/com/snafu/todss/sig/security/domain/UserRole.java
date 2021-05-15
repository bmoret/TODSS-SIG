package com.snafu.todss.sig.security.domain;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public enum UserRole {
    @Enumerated(EnumType.STRING)
    MANAGER,
    @Enumerated(EnumType.STRING)
    SECRETARY,
    @Enumerated(EnumType.STRING)
    EMPLOYEE,
    @Enumerated(EnumType.STRING)
    ADMINISTRATOR
}
