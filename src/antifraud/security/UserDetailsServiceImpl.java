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
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    UserDetailsServiceImpl(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        final var user = userService.getUserByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User with username " + username + " does not exist"));

        return UserDetailsImpl.builder()
                .username(user.getUsername())
                .password(user.getPassword())
//                .active(user.isActive())
//                .roles(user.getRoles())
                .build();
    }

}
