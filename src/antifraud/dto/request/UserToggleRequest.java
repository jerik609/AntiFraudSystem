package antifraud.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

// https://www.baeldung.com/spring-boot-bean-validation

@Data
@NoArgsConstructor(force = true)
public class UserToggleRequest {

    @NotEmpty(message = "username must not be empty")
    private final String username;
    @NotEmpty(message = "operation must no be empty")
    private final String operation;

}
