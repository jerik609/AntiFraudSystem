package antifraud.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class UserDeleteResponse {

    private final String username;
    private final String Status;

}
