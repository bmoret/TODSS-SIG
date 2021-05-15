package com.snafu.todss.sig.security.application;

import com.snafu.todss.sig.security.data.SpringUserRepository;
import com.snafu.todss.sig.security.domain.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService implements UserDetailsService {
    private final SpringUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserService(SpringUserRepository repository, PasswordEncoder passwordEncoder) {
        this.userRepository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(String username, String password) {
        String encodedPassword = this.passwordEncoder.encode(password);

        User user = new User(username, encodedPassword);

        this.userRepository.save(user);
    }

    @Override
    public User loadUserByUsername(String username) {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
