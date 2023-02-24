package antifraud.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// this will be returned when user is trying to access the application and providing credentials (it should ask for them)
// typically this instructs the browser to display a dialogue to enter credentials - by setting the header "WWW-Authenticate" to "Basic"
// if this is left out, the dialogue should not be displayed
// https://stackoverflow.com/questions/57426668/what-is-the-purpose-of-authenticationentrypoint-in-spring-web-security

@Configuration
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.addHeader("WWW-Authenticate", "Basic"); // comment out to disable the login dialogue
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}