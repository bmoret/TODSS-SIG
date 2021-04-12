package com.snafu.todss.sig;

import com.snafu.todss.sig.sessies.PersonTestDataFixtures;
import com.snafu.todss.sig.sessies.data.SpringPersonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CiTestConfiguration {
    @Bean
    CommandLineRunner importWords(SpringPersonRepository repository) {
        return new PersonTestDataFixtures(repository);
    }
}
