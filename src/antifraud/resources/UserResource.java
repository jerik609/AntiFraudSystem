package antifraud.resources;

import antifraud.dto.UserEntryRequest;
import antifraud.dto.UserEntryResponse;
import antifraud.model.User;
import antifraud.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/auth")
@RequiredArgsConstructor
@Log4j2
public class UserResource {

    private final UserService userService;

    @GetMapping(name = "/user")
    public String getUser() {
        return "hello, user!";
    }

    @PostMapping(value = "/user")
    public ResponseEntity<UserEntryResponse> enterUser(@RequestBody @Valid UserEntryRequest userEntryRequest) {
        final var user = User.builder()
                .name(userEntryRequest.getName())
                .username(userEntryRequest.getUsername().toLowerCase())
                .password(userEntryRequest.getPassword())
                .build();

        log.info("Creating user: " + user);

        final var id = userService.enterUser(user);

        return new ResponseEntity<>(
                UserEntryResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .username(user.getUsername())
                        .build(),
                HttpStatus.OK);
    }

}
