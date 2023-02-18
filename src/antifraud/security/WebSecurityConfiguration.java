package antifraud.security;

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

    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
                // actuator
                .mvcMatchers("/actuator/**").permitAll()
                // database console
                .mvcMatchers("/h2/**").permitAll()
                // user authentication/authorization settings
                .mvcMatchers(HttpMethod.POST, "/api/auth/user").permitAll()
                .mvcMatchers(HttpMethod.GET, "api/auth/list").authenticated()
                .mvcMatchers(HttpMethod.DELETE, "api/auth/delete").authenticated()
                // transaction entry api
                .mvcMatchers(HttpMethod.POST, "api/antifraud/transaction").authenticated()
                // all the rest
                .mvcMatchers("/**").authenticated();
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
