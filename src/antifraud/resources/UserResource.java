package antifraud.resources;

import antifraud.dto.UserEntryRequest;
import antifraud.dto.UserEntryResponse;
import antifraud.dto.UserOperationResponse;
import antifraud.model.User;
import antifraud.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/auth")
@RequiredArgsConstructor
@Log4j2
public class UserResource {

    private final UserService userService;

    @PostMapping(value = "/user")
    public ResponseEntity<UserEntryResponse> enterUser(@RequestBody @Valid UserEntryRequest userEntryRequest) {

        log.info("Processing user entry request: " + userEntryRequest);

        final var user = userService.enterUser(
                User.builder()
                        .name(userEntryRequest.getName())
                        .username(userEntryRequest.getUsername().toLowerCase())
                        .password(userEntryRequest.getPassword())
                        .build());

        log.info("Created user: " + user);

        return new ResponseEntity<>(
                UserEntryResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .username(user.getUsername())
                        .build(),
                HttpStatus.CREATED);
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<UserEntryResponse>> getUserList() {

        final var users = userService.getUsers();

        return new ResponseEntity<>(users.stream().map(user ->
                UserEntryResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .username(user.getUsername())
                        .build()
        ).collect(Collectors.toList()), HttpStatus.OK);
    }

    @DeleteMapping(value = "/user/{username}")
    public ResponseEntity<UserOperationResponse> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return new ResponseEntity<>(new UserOperationResponse(username, "Deleted successfully!"), HttpStatus.OK);


    }

}
