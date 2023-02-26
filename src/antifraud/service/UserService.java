package antifraud.service;

import antifraud.enums.RoleType;
import antifraud.model.Configuration;
import antifraud.model.Role;
import antifraud.model.User;
import antifraud.repository.ConfigurationRepository;
import antifraud.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final static String ADMIN_EXISTS = "admin_exists";

    private final UserRepository userRepository;
    private final ConfigurationRepository configurationRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User enterUser(User user) {
        log.info("Processing user: {}", user);

        // check if there's and admin in the configuration
        final var adminConfiguration = configurationRepository
                .findByKey("admin_exists")
                .orElse(Configuration.builder()
                        .key(ADMIN_EXISTS)
                        .value("false")
                        .build()
                );

        // set user role - depends on the fact that an admin already exists
        if (adminConfiguration.getValue().equals("true")) {
            log.info("Administrator account already exists, will create merchant from: {}", user);
            // admin already exists
            user.setRoles(Set.of(Role.builder().role(RoleType.MERCHANT).build()));
            user.setActive(false);
        } else {
            log.info("No administrator account exists, will create administrator from: {}", user);
            // no admin - create a new one
            user.setRoles(Set.of(Role.builder().role(RoleType.ADMINISTRATOR).build()));
            user.setActive(true);
            adminConfiguration.setValue("true");
        }

        configurationRepository.save(adminConfiguration);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        log.info("Done processing user: {}", user);

        return user;
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getUsers() {
        return userRepository.findAll(Sort.by("id").ascending());
    }

    @Transactional
    public void deleteUser(String username) {
        userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with username " + username + " not found"));
        userRepository.deleteByUsername(username);
    }

}
