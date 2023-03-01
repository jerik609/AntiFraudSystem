package antifraud.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AntifraudActionResponse {

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private final Long id;
    private final String ip;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private final Long amount;
    private final String number;
    private final String result;
    private final String status;
    private final String info;

}
