package com.snafu.todss.sig.sessies.data;


import com.snafu.todss.sig.sessies.domain.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringPersonRepository extends JpaRepository<Person, UUID> {
    Optional<Person> findByDetails_Email(String email);
}
