package antifraud.service;

import antifraud.enums.RoleType;
import antifraud.model.Role;
import antifraud.model.User;
import antifraud.repository.RoleRepository;
import antifraud.repository.UserRepository;
import antifraud.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User enterUser(User user) {

        log.info("Processing user request: {}", user);

        final var adminRole = roleRepository
                .findByRoleType(RoleType.ADMINISTRATOR)
                .orElseGet(() -> {
                    // if admin role does not exist, create and save it
                    final var role = Role.builder().roleType(RoleType.ADMINISTRATOR).build();
                    roleRepository.save(role);
                    return role;
                });

        final var adminUsers = userRepository.findFirstByUserRolesContaining(adminRole);
        if (adminUsers.isEmpty()) {
            log.info("No administrator account exists, will create administrator from: {}", user);
            user.setUserRoles(Set.of(adminRole));
            user.setEnabled(true);
        } else {
            log.info("Administrator account already exists, will create merchant from: {}", user);
            final var role = roleRepository
                    .findByRoleType(RoleType.MERCHANT)
                    .orElse(Role.builder().roleType(RoleType.MERCHANT).build());
            user.setUserRoles(Set.of(role));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        log.info("Done processing user request: {}", user);

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

    @Transactional
    public UserDetails getUserDetails(String username) {

        final var user = getUserByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User with username " + username + " does not exist"));

        final var grantedAuthorities = user.getUserRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleType().name()))
                .collect(Collectors.toList());

        return UserDetailsImpl.builder()
                .authorities(grantedAuthorities)
                .username(user.getUsername())
                .password(user.getPassword())
                .enabled(user.isEnabled())
                .build();
    }
}
