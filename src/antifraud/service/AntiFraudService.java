package antifraud.service;

import antifraud.dto.TransactionEntryRequest;
import antifraud.enumeration.VerificationResult;
import antifraud.model.Transaction;
import antifraud.repository.TransactionRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class AntiFraudService {

    private final static long MAX_ALLOWED_AUTOMATED_AMOUNT = 200L;
    private final static long MAX_ALLOWED_MANUAL_AMOUNT = 1500L;

    private final TransactionRepository transactionRepository;

    @Autowired
    AntiFraudService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public VerificationResult verifyTransaction(TransactionEntryRequest transactionEntryRequest) {
        if (MAX_ALLOWED_AUTOMATED_AMOUNT >= transactionEntryRequest.getAmount()) {
            final var transaction = Transaction.builder()
                    .amount(transactionEntryRequest.getAmount())
                    .owner(SecurityContextHolder.getContext().getAuthentication().getName())
                    .build();
            log.info("transaction = {}", transaction);
            transactionRepository.save(transaction);
            return VerificationResult.ALLOWED;
        } else if (MAX_ALLOWED_MANUAL_AMOUNT >= transactionEntryRequest.getAmount()) {
            return VerificationResult.MANUAL_PROCESSING;
        } else {
            return VerificationResult.PROHIBITED;
        }
    }
}
