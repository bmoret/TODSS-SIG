package com.snafu.todss.sig.security.presentation.controller;

import com.snafu.todss.sig.security.application.UserService;
import com.snafu.todss.sig.security.presentation.dto.request.Registration;
import com.snafu.todss.sig.sessies.application.PersonService;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.snafu.todss.sig.sessies.presentation.dto.request.PersonRequest;
import javassist.NotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/registration")
public class RegistrationController {
    private final UserService userService;
    private final PersonService personService;

    public RegistrationController(UserService userService, PersonService personService) {
        this.userService = userService;
        this.personService = personService;
    }

    @PostMapping
    public void register(@Validated @RequestBody Registration registration, @Validated @RequestBody PersonRequest personRequest) throws NotFoundException {
        Person person = this.personService.createPerson(personRequest);
        this.userService.register(
                registration.username,
                registration.password,
                person
        );


    }
}
