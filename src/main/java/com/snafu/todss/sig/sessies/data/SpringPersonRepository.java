package com.snafu.todss.sig.sessies.data;


import com.snafu.todss.sig.sessies.domain.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringPersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByEmail(String email);
}
