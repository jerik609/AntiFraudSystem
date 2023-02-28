package antifraud.dto.request;

import antifraud.dto.validation.IpConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
public class SuspiciousIpEntryRequest {

    @IpConstraint
    private final String ip;

}
