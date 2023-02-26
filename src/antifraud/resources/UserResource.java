package antifraud.resources;

import antifraud.dto.*;
import antifraud.enums.RoleType;
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
    public ResponseEntity<UserEntryResponse> enterUser(@RequestBody @Valid UserEntryRequest userEntryRequest) {

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
                UserEntryResponse.builder()
                        .id(user.getId())
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
    public ResponseEntity<List<UserEntryResponse>> getUserList() {

        final var users = userService.getUsers();

        return new ResponseEntity<>(users.stream().map(user ->
                UserEntryResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .username(user.getUsername())
                        .role(user.getUserRoles().stream()
                                .findFirst().orElseThrow(() -> new RuntimeException("No role for user: " + user))
                                .getRoleType()
                                .name())
                        .build()
        ).collect(Collectors.toList()), HttpStatus.OK);
    }

    @PutMapping(value = "/role")
    public ResponseEntity<UserRoleChangeResponse> changeUserRole(@RequestBody @Valid UserRoleChangeRequest request) {

        final var roleType = RoleType.valueOf(request.getUsername());

        if (!roleType.equals(RoleType.MERCHANT) && !roleType.equals(RoleType.SUPPORT)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        log.info("Changing user role: " + request.getUsername() + ", " + roleType.name());
        userService.setUserRole(request.getUsername(), roleType);

        return new ResponseEntity<>(new UserRoleChangeResponse(request.getUsername(), roleType.name()), HttpStatus.OK);
    }

    @DeleteMapping(value = "/user/{username}")
    public ResponseEntity<UserDeleteResponse> deleteUser(@PathVariable String username) {
        log.info("Deleting user: " + username);
        userService.deleteUser(username);
        return new ResponseEntity<>(new UserDeleteResponse(username, "Deleted successfully!"), HttpStatus.OK);
    }

}
