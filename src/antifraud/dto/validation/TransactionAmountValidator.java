package antifraud.dto.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class TransactionAmountValidator {

    private final static long MAX_ALLOWED_AUTOMATED_AMOUNT = 200L;

    private final static long MAX_ALLOWED_MANUAL_AMOUNT = 1500L;

    public static VerificationResult validate(long amount) {
        if (MAX_ALLOWED_AUTOMATED_AMOUNT >= amount) {
            return VerificationResult.ALLOWED;
        } else if (MAX_ALLOWED_MANUAL_AMOUNT >= amount) {
            return VerificationResult.MANUAL_PROCESSING;
        } else {
            return VerificationResult.PROHIBITED;
        }
    }

    @RequiredArgsConstructor
    @Getter
    public enum VerificationResult {
        ALLOWED("ALLOWED"),
        MANUAL_PROCESSING("MANUAL_PROCESSING"),
        PROHIBITED("PROHIBITED");

        private final String name;
    }

}
