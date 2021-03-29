package com.snafu.todss.sig.sessies.data;


import com.snafu.todss.sig.sessies.domain.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringPersonRepository extends JpaRepository<Person, UUID> {
    Optional<Person> findByEmail(String email);
}
