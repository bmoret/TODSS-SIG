package com.snafu.todss.sig.security.application;

import com.snafu.todss.sig.security.application.util.JwtGenerator;
import com.snafu.todss.sig.security.application.util.JwtValidator;
import com.snafu.todss.sig.security.data.SpringUserRepository;
import com.snafu.todss.sig.security.domain.User;
import com.snafu.todss.sig.security.domain.UserRole;
import com.snafu.todss.sig.security.presentation.dto.request.RefreshTokenRequest;
import com.snafu.todss.sig.sessies.domain.person.Person;
import com.sun.jdi.request.DuplicateRequestException;
import javassist.NotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UserService implements UserDetailsService {
    private final SpringUserRepository USER_REPOSITORY;
    private final PasswordEncoder PASSSWORD_ENCODER;
    private final JwtGenerator JWT_GENERATOR;
    private final JwtValidator JWT_VALIDATOR;

    public UserService(SpringUserRepository repository, PasswordEncoder passwordEncoder, JwtGenerator jwtGenerator, JwtValidator jwtValidator) {
        this.USER_REPOSITORY = repository;
        this.PASSSWORD_ENCODER = passwordEncoder;
        JWT_GENERATOR = jwtGenerator;
        JWT_VALIDATOR = jwtValidator;
    }

    private void checkIfUserAlreadyExists(String username) {
        if (username == null || USER_REPOSITORY.findByUsername(username).isPresent()) {
            throw new DuplicateRequestException(String.format("User with username '%s' already exists", username));
        }
    }

    public void register(String username, String password, Person person) {
        checkIfUserAlreadyExists(username);
        String encodedPassword = this.PASSSWORD_ENCODER.encode(password);
        User user = new User(username, encodedPassword, person);
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

    public Map<String, String> refreshUserToken(RefreshTokenRequest refreshTokenRequest) {
        this.JWT_VALIDATOR.validateAccessJwt(refreshTokenRequest.accessToken);
        this.JWT_VALIDATOR.validateRefreshJwt(refreshTokenRequest.refreshToken);
        String accessToken = this.JWT_GENERATOR.refreshAccessTokenFromAccessToken(refreshTokenRequest.accessToken);
        Map<String, String> authTokenMap = new HashMap<>();
        authTokenMap.put("Access-Token", accessToken);
        authTokenMap.put("Access-Control-Expose-Headers", "*");

        return authTokenMap;
    }
}
