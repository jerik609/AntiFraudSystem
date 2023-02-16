package antifraud.model;

import antifraud.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html#integration-testing-annotations

@SpringBootTest
//@DataJpaTest
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