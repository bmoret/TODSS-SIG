package com.snafu.todss.sig.sessies.data;


import com.snafu.todss.sig.sessies.domain.Attendance;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.domain.session.types.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringAttendanceRepository extends JpaRepository<Attendance, UUID> {
    @Query(nativeQuery=true, value="SELECT * FROM attendance a WHERE a.person_id = ?1 AND a.session_id = ?2")
    Optional<Attendance> findAttendanceByIdContainingAndPersonAndSession(Person person, Session session);
}
