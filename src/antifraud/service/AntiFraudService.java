package antifraud.service;

import antifraud.dto.TransactionEntryRequest;
import antifraud.enumeration.VerificationResult;
import org.springframework.stereotype.Service;

@Service
public class AntiFraudService {

    private final static long MAX_ALLOWED_AUTOMATED_AMOUNT = 200L;
    private final static long MAX_ALLOWED_MANUAL_AMOUNT = 1500L;

    public VerificationResult verifyTransaction(TransactionEntryRequest transactionEntryRequest) {
        if (MAX_ALLOWED_AUTOMATED_AMOUNT >= transactionEntryRequest.getAmount()) {
            return VerificationResult.ALLOWED;
        } else if (MAX_ALLOWED_MANUAL_AMOUNT >= transactionEntryRequest.getAmount()) {
            return VerificationResult.MANUAL_PROCESSING;

        } else {
            return VerificationResult.PROHIBITED;
        }
    }
}
