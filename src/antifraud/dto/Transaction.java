package antifraud.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Positive;

// https://www.baeldung.com/jackson-deserialize-immutable-objects

// https://www.baeldung.com/javax-validation

// http://www.devnips.com/2021/05/adding-custom-validation-in-lombok.html

// but @Valid and @Builder do not seem to play together nicely ...

// ??? https://www.linkedin.com/pulse/spring-projects-best-practices-episode-i-ekramali-kazi/

// ??? https://www.baeldung.com/exception-handling-for-rest-with-spring

@Data
//@Builder // provides the builder :-)
//@JsonDeserialize(builder = Transaction.TransactionBuilder.class) // tells JSON deserialization to use the generated builder
public class Transaction {
    @Positive
    private long amount;

//    @Positive
//    private long amount;



}

