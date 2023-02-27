package antifraud.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
public class UserToggleRequest {

    private final String username;
    private final String operation;

}
