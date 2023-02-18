package antifraud.security;

import antifraud.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserDetailsServiceConfiguration implements UserDetailsService {

    private final UserService userService;

    private final InMemoryUserDetailsManager inMemoryUserDetailsManager;

    UserDetailsServiceConfiguration(UserService userService, PasswordEncoder passwordEncoder) {

        // admin user (BAD BAD BACKDOOR!)
        final var admin = User.withUsername("admin")
                .password(passwordEncoder.encode("admin"))
                .roles("ADMIN")
                .build();

        this.inMemoryUserDetailsManager = new InMemoryUserDetailsManager(admin);
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (inMemoryUserDetailsManager.userExists(username)) {
            return inMemoryUserDetailsManager.loadUserByUsername(username);
        }

        final var user = userService.getUserByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User with username " + username + " does not exist"));

        return UserDetailsImpl.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }

}
