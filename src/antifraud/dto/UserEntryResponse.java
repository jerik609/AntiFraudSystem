package antifraud.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class UserEntryResponse {

    private final Long id;
    private final String name;
    private final String username;

}
