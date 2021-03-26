package com.snafu.todss.sig.sessies.data;

import com.snafu.todss.sig.sessies.domain.session.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {
}
