package com.snafu.todss.sig.sessies.data;


import com.snafu.todss.sig.sessies.domain.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringAttendanceRepository extends JpaRepository<Attendance, UUID> {
//    Optional<List<Attendance>> findAllByPerson(Person person);
//    Optional<List<Attendance>> findAllBySession(Session session);
}
