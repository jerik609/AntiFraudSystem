package antifraud.resources;

import antifraud.dto.request.UserEntryRequest;
import antifraud.dto.request.UserRoleChangeRequest;
import antifraud.dto.request.UserToggleRequest;
import antifraud.dto.response.UserActionResponse;
import antifraud.enums.RoleType;
import antifraud.enums.UserStatus;
import antifraud.model.User;
import antifraud.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public ResponseEntity<UserActionResponse> enterUser(@RequestBody @Valid UserEntryRequest userEntryRequest) {

        log.info("Processing user entry request: " + userEntryRequest);

        // trying to enter a user
        // if no admin exists - this user will be the admin
        // if admin exists, then this user will be a merchant

        User user;
        try {
            user = userService.enterUser(
                    User.builder()
                            .name(userEntryRequest.getName())
                            .username(userEntryRequest.getUsername().toLowerCase())
                            .password(userEntryRequest.getPassword())
                            .build());
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with name " + userEntryRequest.getName() + " already exists.");
        }

        log.info("Created user: " + user);

        return new ResponseEntity<>(
                UserActionResponse.builder()
                        .id(user.getId().toString())
                        .name(user.getName())
                        .username(user.getUsername())
                        .role(user.getUserRoles().stream()
                                .findFirst().orElseThrow(() -> new RuntimeException("No role for user: " + user))
                                .getRoleType()
                                .name())
                        .build(),
                HttpStatus.CREATED);
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<UserActionResponse>> getUserList() {

        final var users = userService.getUsers();

        return new ResponseEntity<>(
                users.stream().map(user -> UserActionResponse.builder()
                                .name(user.getName())
                                .username(user.getUsername())
                                .role(user.getUserRoles().stream()
                                        .findFirst().orElseThrow(() -> new RuntimeException("No role for user: " + user))
                                        .getRoleType()
                                        .name())
                                .build())
                        .collect(Collectors.toList()),
                HttpStatus.OK);
    }

    @DeleteMapping(value = "/user/{username}")
    public ResponseEntity<UserActionResponse> deleteUser(@PathVariable String username) {

        log.info("Deleting user: " + username);

        userService.deleteUser(username);

        log.info("Deleted user: " + username);

        return new ResponseEntity<>(
                UserActionResponse.builder()
                        .username(username)
                        .status("Deleted successfully!")
                        .build(),
                HttpStatus.OK);
    }

    @PutMapping(value = "/role")
    public ResponseEntity<UserActionResponse> changeUserRole(@RequestBody @Valid UserRoleChangeRequest request) {

        log.info("Processing role change request: " + request);

        final var roleType = RoleType.valueOf(request.getRole());

        if (!roleType.equals(RoleType.MERCHANT) && !roleType.equals(RoleType.SUPPORT)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        userService.setUserRole(request.getUsername(), roleType);

        log.info("Changed user " + request.getUsername() + " role to: " + roleType.name());

        return new ResponseEntity<>(
                UserActionResponse.builder()
                        .username(request.getUsername())
                        .role(roleType.name())
                        .build(),
                HttpStatus.OK);
    }

    @PutMapping(value = "/access")
    public ResponseEntity<UserActionResponse> changeActivationStatus(@RequestBody @Valid UserToggleRequest request) {

        log.info("Processing user activation status: " + request);

        final var userStatus = UserStatus.valueOf(request.getOperation());

        userService.setUserActivationStatus(request.getUsername(), userStatus);

        log.info("User " + request.getUsername() + " set to " + userStatus.name());

        return new ResponseEntity<>(
                UserActionResponse.builder()
                        .name(request.getUsername())
                        .status("User " + request.getUsername() + " " + (userStatus.equals(UserStatus.LOCK) ? "locked" : "unlocked") + "!")
                        .build(),
                HttpStatus.OK);
    }

}
