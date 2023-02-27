package antifraud.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor // because of builder
@NoArgsConstructor(force = true)
public class UserRoleChangeRequest {

    private final String username;
    private final String role;

}
