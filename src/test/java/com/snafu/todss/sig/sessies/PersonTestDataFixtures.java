package com.snafu.todss.sig.sessies;

import com.snafu.todss.sig.sessies.domain.person.PersonBuilder;
import com.snafu.todss.sig.sessies.domain.person.enums.Role;
import org.springframework.boot.CommandLineRunner;
import com.snafu.todss.sig.sessies.data.*;

import java.time.LocalDate;

public class PersonTestDataFixtures implements CommandLineRunner {
    private final SpringPersonRepository repository;

    public PersonTestDataFixtures(SpringPersonRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        PersonBuilder pb = new PersonBuilder();

        pb.setEmail("email@email.com");
        pb.setFirstname("first");
        pb.setLastname("last");
        pb.setExpertise("none");
        pb.setEmployedSince(LocalDate.of(2000,1,1));
        pb.setBranch(com.snafu.todss.sig.sessies.domain.person.enums.Branch.VIANEN);
        pb.setRole(Role.MANAGER);

        this.repository.save(pb.build());

        pb.setEmail("email2@email.com");
        pb.setFirstname("second");
        pb.setLastname("last");
        pb.setExpertise("none");
        pb.setEmployedSince(LocalDate.of(2000,1,1));
        pb.setBranch(com.snafu.todss.sig.sessies.domain.person.enums.Branch.VIANEN);
        pb.setRole(Role.EMPLOYEE);

        this.repository.save(pb.build());

        pb.setEmail("email3@email.com");
        pb.setFirstname("third");
        pb.setLastname("last");
        pb.setExpertise("none");
        pb.setEmployedSince(LocalDate.of(2000,1,1));
        pb.setBranch(com.snafu.todss.sig.sessies.domain.person.enums.Branch.VIANEN);
        pb.setRole(Role.EMPLOYEE);

        this.repository.save(pb.build());

        pb.setEmail("tom@email.com");
        pb.setFirstname("tom");
        pb.setLastname("albert");
        pb.setExpertise("all");
        pb.setEmployedSince(LocalDate.of(2000,1,1));
        pb.setBranch(com.snafu.todss.sig.sessies.domain.person.enums.Branch.VIANEN);
        pb.setRole(Role.EMPLOYEE);

        this.repository.save(pb.build());

        pb.setEmail("tom1@email.com");
        pb.setFirstname("tom");
        pb.setLastname("alberto");
        pb.setExpertise("all");
        pb.setEmployedSince(LocalDate.of(2000,1,1));
        pb.setBranch(com.snafu.todss.sig.sessies.domain.person.enums.Branch.VIANEN);
        pb.setRole(Role.EMPLOYEE);

        this.repository.save(pb.build());

        pb.setEmail("tom2@email.com");
        pb.setFirstname("tom");
        pb.setLastname("alberto");
        pb.setExpertise("all");
        pb.setEmployedSince(LocalDate.of(2000,1,1));
        pb.setBranch(com.snafu.todss.sig.sessies.domain.person.enums.Branch.VIANEN);
        pb.setRole(Role.EMPLOYEE);

        this.repository.save(pb.build());

        pb.setEmail("toms@email.com");
        pb.setFirstname("toms");
        pb.setLastname("alberto");
        pb.setExpertise("all");
        pb.setEmployedSince(LocalDate.of(2000,1,1));
        pb.setBranch(com.snafu.todss.sig.sessies.domain.person.enums.Branch.VIANEN);
        pb.setRole(Role.EMPLOYEE);

        this.repository.save(pb.build());

        pb.setEmail("to@email.com");
        pb.setFirstname("to");
        pb.setLastname("alber");
        pb.setExpertise("all");
        pb.setEmployedSince(LocalDate.of(2000,1,1));
        pb.setBranch(com.snafu.todss.sig.sessies.domain.person.enums.Branch.VIANEN);
        pb.setRole(Role.EMPLOYEE);

        this.repository.save(pb.build());

        pb.setEmail("thomaz@email.com");
        pb.setFirstname("thomaz");
        pb.setLastname("albertorinie");
        pb.setExpertise("all");
        pb.setEmployedSince(LocalDate.of(2000,1,1));
        pb.setBranch(com.snafu.todss.sig.sessies.domain.person.enums.Branch.VIANEN);
        pb.setRole(Role.EMPLOYEE);

        this.repository.save(pb.build());

    }

}
