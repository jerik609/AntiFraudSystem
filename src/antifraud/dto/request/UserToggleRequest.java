package antifraud.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor(force = true)
public class UserToggleRequest {

    private final String username;
    private final String operation;

}
