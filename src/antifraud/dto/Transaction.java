package antifraud.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Positive;

// https://www.baeldung.com/jackson-deserialize-immutable-objects

// https://www.baeldung.com/javax-validation

@Data
@Builder // provides the builder :-)
@JsonDeserialize(builder = Transaction.TransactionBuilder.class) // tells JSON deserialization to use the generated builder
public class Transaction {
    @Positive
    private final long amount;
}
