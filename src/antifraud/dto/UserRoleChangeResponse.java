package antifraud.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class UserRoleChangeResponse {

    private final String username;
    private final String role;

}
