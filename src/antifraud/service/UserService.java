package antifraud.service;

import antifraud.enums.RoleType;
import antifraud.model.Configuration;
import antifraud.model.Role;
import antifraud.model.User;
import antifraud.repository.ConfigurationRepository;
import antifraud.repository.RoleRepository;
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
    private final RoleRepository roleRepository;
    private final ConfigurationRepository configurationRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User enterUser(User user) {
        log.info("Processing user: {}", user);

        // check if there's and admin in the configuration
//        final var adminConfiguration = configurationRepository
//                .findByKey("admin_exists")
//                .orElse(Configuration.builder()
//                        .key(ADMIN_EXISTS)
//                        .value("false")
//                        .build()
//                );

        final var adminRole = roleRepository
                .findByRoleType(RoleType.ADMINISTRATOR)
                .orElseGet(() -> {
                    // if admin role does not exist, create and save it
                    final var role = Role.builder().roleType(RoleType.ADMINISTRATOR).build();
                    roleRepository.save(role);
                    return role;
                });

        //final var adminUsers = userRepository.findByUserRolesIn(Set.of(adminRole));

        System.out.println("HERE!!!");

        final var adminUsers = userRepository.findFirstByUserRoles(adminRole);


        if (adminUsers.isEmpty()) {
            log.info("No administrator account exists, will create administrator from: {}", user);
            //final var role = roleRepository.findByRoleType(RoleType.ADMINISTRATOR).orElse(Role.builder().roleType(RoleType.ADMINISTRATOR).build());
            user.setUserRoles(Set.of(adminRole));
            user.setActive(true);
        } else {
            log.info("Administrator account already exists, will create merchant from: {}", user);
            final var role = roleRepository
                    .findByRoleType(RoleType.MERCHANT)
                    .orElse(Role.builder().roleType(RoleType.MERCHANT).build());
            user.setUserRoles(Set.of(role));
            user.setActive(false);
        }

//        final var losers = userRepository.findByUserRoles()
//        if (adminConfiguration.getValue().equals("true")) {
//        } else {
//            adminConfiguration.setValue("true");
//        }
//        configurationRepository.save(adminConfiguration);

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
