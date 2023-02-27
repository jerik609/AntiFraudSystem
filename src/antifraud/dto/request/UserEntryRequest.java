package antifraud.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor(force = true)
public class UserEntryRequest {

    @NotEmpty
    private final String name;
    @NotEmpty
    private final String username;
    @NotEmpty
    private final String password;

}
