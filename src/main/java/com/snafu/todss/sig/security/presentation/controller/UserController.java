package com.snafu.todss.sig.security.presentation.controller;

import com.snafu.todss.sig.security.application.UserService;
import com.snafu.todss.sig.security.domain.User;
import com.snafu.todss.sig.security.presentation.dto.response.UserDTOResponse;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService USER_SERVICE;

    public UserController(UserService USER_SERVICE) {
        this.USER_SERVICE = USER_SERVICE;
    }

    @GetMapping
    @RolesAllowed({"ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    public ResponseEntity<List<UserDTOResponse>> getUsers() {
        List<User> users = this.USER_SERVICE.getAllUsers();
        List<UserDTOResponse> userDTOResponses = new ArrayList<>();

        for(User user : users) {
            userDTOResponses.add(new UserDTOResponse(user));
        }

        return new ResponseEntity<>(userDTOResponses, HttpStatus.OK);
    }

    @GetMapping(path = "/{username}")
    @RolesAllowed({"ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    public ResponseEntity<UserDTOResponse> getUser(@PathVariable("username") String username) throws NotFoundException {
        User user = this.USER_SERVICE.getUserByUsername(username);

        return new ResponseEntity<>(new UserDTOResponse(user), HttpStatus.OK);
    }

    @DeleteMapping("/{username}")
    @RolesAllowed({"ROLE_MANAGER","ROLE_ADMINISTRATOR"})
    public ResponseEntity<HttpStatus> removeUser(@PathVariable String username) throws NotFoundException {
        USER_SERVICE.removeUser(username);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
