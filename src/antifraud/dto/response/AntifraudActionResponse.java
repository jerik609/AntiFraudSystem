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
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private final Long transactionId;
    private final String ip;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private final Long amount;
    private final String number;
    private final String result;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String feedback;
    private final String status;
    private final String info;
    private final String region;
    private final String date;

}
