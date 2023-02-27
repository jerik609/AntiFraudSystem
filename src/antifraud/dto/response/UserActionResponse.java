package antifraud.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserActionResponse {

    private final String id;
    private final String name;
    private final String username;
    private final String status;
    private final String role;

}
