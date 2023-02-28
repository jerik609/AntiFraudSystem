package antifraud.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.LuhnCheck;

@Data
@NoArgsConstructor(force = true)
public class StolenCardEntryRequest {

    @LuhnCheck
    private final String number;

}
