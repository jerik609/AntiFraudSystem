package antifraud.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@RequiredArgsConstructor
public class UserEntryRequest {

    @NotEmpty
    private final String name;
    @NotEmpty
    private final String username;
    @NotEmpty
    private final String password;

}
