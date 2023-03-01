package antifraud.dto.validation;

import antifraud.enums.TransactionValidationResult;

public class TransactionAmountValidator {

    private final static long MAX_ALLOWED_AUTOMATED_AMOUNT = 200L;
    private final static long MAX_ALLOWED_MANUAL_AMOUNT = 1500L;

    public static TransactionValidationResult validate(long amount) {
        if (MAX_ALLOWED_AUTOMATED_AMOUNT >= amount) {
            return TransactionValidationResult.ALLOWED;
        } else if (MAX_ALLOWED_MANUAL_AMOUNT >= amount) {
            return TransactionValidationResult.MANUAL_PROCESSING;
        } else {
            return TransactionValidationResult.PROHIBITED;
        }
    }

}
