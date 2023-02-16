package antifraud.model;

import antifraud.repository.TransactionRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

// https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html#integration-testing-annotations

@Disabled("does not work with hyperskill test setup which runs on java 17")
@DataJpaTest
public class TransactionTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    public void saveTransaction() {
        Transaction transaction = Transaction.builder()
                .owner("Michal")
                .amount(100L)
                .build();
        transactionRepository.save(transaction);
    }

}