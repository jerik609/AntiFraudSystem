package antifraud.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
public class UserRoleChangeRequest {

    private final String username;
    private final String role;

}
