package com.snafu.todss.sig.security.presentation.controller;

import com.snafu.todss.sig.security.application.UserService;
import com.snafu.todss.sig.security.presentation.dto.request.Registration;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/registration")
public class RegistrationController {
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public void register(@Validated @RequestBody Registration registration) {
        this.userService.register(
                registration.username,
                registration.password
        );
    }
}
