package antifraud.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor(force = true)
public class UserEntryRequest {

    @NotEmpty(message = "name must not be empty")
    private final String name;
    @NotEmpty(message = "username must not be empty")
    private final String username;
    @NotEmpty(message = "password must not be empty")
    private final String password;

}
