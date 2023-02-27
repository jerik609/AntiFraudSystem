package antifraud.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class UserToggleRequest {

    private final String username;
    private final String operation;

}
