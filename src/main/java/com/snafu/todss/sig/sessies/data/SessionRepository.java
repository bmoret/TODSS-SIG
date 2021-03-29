package com.snafu.todss.sig.sessies.data;

import com.snafu.todss.sig.sessies.domain.session.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
}
