package com.snafu.todss.sig.security.presentation.controller;

import com.snafu.todss.sig.security.application.UserService;
import com.snafu.todss.sig.security.presentation.dto.request.Registration;
import com.snafu.todss.sig.sessies.application.PersonService;
import com.snafu.todss.sig.sessies.domain.person.Person;
import javassist.NotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
@RequestMapping("/registration")
@Transactional
public class RegistrationController {
    private final UserService userService;
    private final PersonService personService;

    public RegistrationController(UserService userService, PersonService personService) {
        this.userService = userService;
        this.personService = personService;
    }

    @PostMapping
    public void register(@Valid @RequestBody Registration registration) throws NotFoundException {
        Person person = this.personService.createPerson(registration);
        this.userService.register(
                registration.username,
                registration.password,
                person
        );
    }
}
