package antifraud.service;

import antifraud.dto.Transaction;
import antifraud.enumeration.VerificationResult;
import antifraud.exception.InvalidTransactionAmountException;
import org.springframework.stereotype.Service;

@Service
public class AntiFraudService {

    private final static long MAX_ALLOWED_AUTOMATED_AMOUNT = 200L;
    private final static long MAX_ALLOWED_MANUAL_AMOUNT = 1500L;

    public VerificationResult verifyTransaction(Transaction transaction) {
        if (MAX_ALLOWED_AUTOMATED_AMOUNT >= transaction.getAmount()) {
            return VerificationResult.ALLOWED;
        } else if (MAX_ALLOWED_MANUAL_AMOUNT >= transaction.getAmount()) {
            return VerificationResult.MANUAL_PROCESSING;

        } else {
            return VerificationResult.PROHIBITED;
        }
    }
}
