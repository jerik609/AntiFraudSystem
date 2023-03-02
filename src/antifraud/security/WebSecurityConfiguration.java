package antifraud.security;

import antifraud.enums.RoleType;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final AuthenticationEntryPoint authenticationEntryPoint;

    public WebSecurityConfiguration(AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    // can I change spring security configuration (namely HttpSecurity) during runtime?
    // https://stackoverflow.com/questions/39089494/modify-spring-security-config-at-runtime

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
                // actuator
                .mvcMatchers("/actuator/**").permitAll()

                // database console
                .mvcMatchers("/h2/**").permitAll()

                // user management
                .mvcMatchers(HttpMethod.POST, "/api/auth/user").permitAll()

                .mvcMatchers(HttpMethod.DELETE, "/api/auth/user/*").hasAnyRole(RoleType.ADMINISTRATOR.name())
                .mvcMatchers(HttpMethod.GET, "/api/auth/list").hasAnyRole(RoleType.ADMINISTRATOR.name(), RoleType.SUPPORT.name())

                .mvcMatchers(HttpMethod.PUT, "/api/auth/access").hasAnyRole(RoleType.ADMINISTRATOR.name())
                .mvcMatchers(HttpMethod.PUT, "/api/auth/role").hasAnyRole(RoleType.ADMINISTRATOR.name())

                // transaction API
                .mvcMatchers(HttpMethod.POST, "/api/antifraud/transaction").hasAnyRole(RoleType.MERCHANT.name())
                .mvcMatchers(HttpMethod.PUT, "/api/antifraud/transaction").hasAnyRole(RoleType.SUPPORT.name())

                .mvcMatchers(HttpMethod.GET, "/api/antifraud/history/**").hasAnyRole(RoleType.SUPPORT.name())

                // master data API
                .mvcMatchers(HttpMethod.POST, "/api/antifraud/suspicious-ip").hasAnyRole(RoleType.SUPPORT.name())
                .mvcMatchers(HttpMethod.DELETE, "/api/antifraud/suspicious-ip/*").hasAnyRole(RoleType.SUPPORT.name())
                .mvcMatchers(HttpMethod.GET, "/api/antifraud/suspicious-ip").hasAnyRole(RoleType.SUPPORT.name())

                .mvcMatchers(HttpMethod.POST, "/api/antifraud/stolencard").hasAnyRole(RoleType.SUPPORT.name())
                .mvcMatchers(HttpMethod.DELETE, "/api/antifraud/stolencard/*").hasAnyRole(RoleType.SUPPORT.name())
                .mvcMatchers(HttpMethod.GET, "/api/antifraud/stolencard").hasAnyRole(RoleType.SUPPORT.name())

                // deny all the rest
                .mvcMatchers("/**").denyAll();
        // https://www.baeldung.com/spring-security-basic-authentication
        httpSecurity.httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint);
        httpSecurity.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // what does this button do?
        httpSecurity.csrf()
                .disable();
        httpSecurity.headers()
                .frameOptions().disable();
    }

}
