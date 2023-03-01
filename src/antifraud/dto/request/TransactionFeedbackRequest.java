package antifraud.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor(force = true)
public class TransactionFeedbackRequest {

    private final long id;

    @NotEmpty
    private final String feedback;

}
