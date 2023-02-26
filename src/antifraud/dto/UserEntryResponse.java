package antifraud.dto;

import lombok.*;

@Data
@Builder
@RequiredArgsConstructor // because of builder
@NoArgsConstructor(force = true)
public class UserEntryResponse {

    private final Long id;
    private final String name;
    private final String username;
    private final String role;

}
