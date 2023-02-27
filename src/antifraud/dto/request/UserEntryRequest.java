package antifraud.dto.request;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@RequiredArgsConstructor // because of builder
@NoArgsConstructor(force = true)
public class UserEntryRequest {

    @NotEmpty
    private final String name;
    @NotEmpty
    private final String username;
    @NotEmpty
    private final String password;

}
