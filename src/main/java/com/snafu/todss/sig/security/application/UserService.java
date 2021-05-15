package com.snafu.todss.sig.security.application;

import com.snafu.todss.sig.security.data.SpringUserRepository;
import com.snafu.todss.sig.security.domain.User;
import com.snafu.todss.sig.security.domain.UserRole;
import com.sun.jdi.request.DuplicateRequestException;
import javassist.NotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class UserService implements UserDetailsService {
    private final SpringUserRepository USER_REPOSITORY;
    private final PasswordEncoder PASSSWORD_ENCODER;

    public UserService(SpringUserRepository repository, PasswordEncoder passwordEncoder) {
        this.USER_REPOSITORY = repository;
        this.PASSSWORD_ENCODER = passwordEncoder;
    }

    private void checkIfUserAlreadyExists(String username) {
        if (username != null) {
            USER_REPOSITORY.findByUsername(username).ifPresent(error -> {
                throw new DuplicateRequestException(String.format("User with username '%s' already exists", username));
            });
        }
    }

    public void register(String username, String password) {
        checkIfUserAlreadyExists(username);

        String encodedPassword = this.PASSSWORD_ENCODER.encode(password);

        User user = new User(username, encodedPassword);

        this.USER_REPOSITORY.save(user);
    }

    @Override
    public User loadUserByUsername(String username) {
        return this.USER_REPOSITORY.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public void setUserRole(String username, String role) {
        User user = this.USER_REPOSITORY.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("There is no account registered with the given " +
                        "username: " + username));

        UserRole eRole = UserRole.valueOf(role);
        user.setRole(eRole);
    }

    public List<User> getAllUsers() {
        return USER_REPOSITORY.findAll();
    }

    public User getUserByUsername(String username) throws NotFoundException {
        return USER_REPOSITORY.findByUsername(username).orElseThrow(
                () -> new NotFoundException("An employee with that ID doesn't exist."));
    }

    public void removeUser(String username) throws NotFoundException {
        USER_REPOSITORY.delete(getUserByUsername(username));
    }
}
