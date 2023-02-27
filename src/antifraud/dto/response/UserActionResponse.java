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

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private final Long id;
    private final String name;
    private final String username;
    private final String status;
    private final String role;

}
