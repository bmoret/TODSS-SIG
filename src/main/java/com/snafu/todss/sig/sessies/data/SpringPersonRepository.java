package com.snafu.todss.sig.sessies.data;


import com.snafu.todss.sig.sessies.domain.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringPersonRepository extends JpaRepository<Person, UUID> {
    Optional<Person> findByDetails_Email(String email);
    List<Person> findByDetails_FirstnameAndDetails_Lastname(String firstname, String lastname);
    List<Person> findByDetails_Firstname(String firstname);
    List<Person> findByDetails_Lastname(String lastname);
    @Query(nativeQuery=true, value="SELECT * FROM person WHERE first_name LIKE CONCAT(?1, '%') AND LENGTH(last_name) BETWEEN ?2 AND ?3")
    List<Person> findPersonByFirstPartialFirstname(String firstname, Integer min, Integer max);
    @Query(nativeQuery=true, value="SELECT * FROM person WHERE first_name LIKE CONCAT('%', ?1, '%') AND LENGTH(last_name) BETWEEN ?2 AND ?3")
    List<Person> findPersonByMiddlePartialFirstname(String firstname, Integer min, Integer max);
    @Query(nativeQuery=true, value="SELECT * FROM person WHERE first_name LIKE CONCAT('%', ?1) AND LENGTH(last_name) BETWEEN ?2 AND ?3")
    List<Person> findPersonByLastPartialFirstname(String firstname, Integer min, Integer max);
    @Query(nativeQuery=true, value="SELECT * FROM person WHERE last_name LIKE CONCAT(?1, '%') AND LENGTH(last_name) BETWEEN ?2 AND ?3")
    List<Person> findPersonByFirstPartialLastname(String lastname, Integer min, Integer max);
    @Query(nativeQuery=true, value="SELECT * FROM person WHERE last_name LIKE CONCAT('%', ?1, '%') AND LENGTH(last_name) BETWEEN ?2 AND ?3")
    List<Person> findPersonByMiddlePartialLastname(String lastname, Integer min, Integer max);
    @Query(nativeQuery=true, value="SELECT * FROM person WHERE last_name LIKE CONCAT('%', ?1) AND LENGTH(last_name) BETWEEN ?2 AND ?3")
    List<Person> findPersonByLastPartialLastname(String lastname, Integer min, Integer max);
}
