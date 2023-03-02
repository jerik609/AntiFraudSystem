package antifraud.services;

import antifraud.enums.LimitOperation;
import antifraud.enums.TransactionValidationResult;
import antifraud.model.CardLimits;
import antifraud.model.LimitsConfig;
import antifraud.model.Transaction;
import antifraud.repository.CardLimitsRepository;
import antifraud.repository.LimitsConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.List;

import static antifraud.enums.TransactionValidationResult.ALLOWED;
import static antifraud.enums.TransactionValidationResult.PROHIBITED;
import static antifraud.enums.TransactionValidationResult.MANUAL_PROCESSING;

import static antifraud.enums.LimitOperation.EXCEPTION;
import static antifraud.enums.LimitOperation.DECREASE_ALLOWED;
import static antifraud.enums.LimitOperation.DECREASE_MANUAL;
import static antifraud.enums.LimitOperation.DECREASE_BOTH;
import static antifraud.enums.LimitOperation.INCREASE_ALLOWED;
import static antifraud.enums.LimitOperation.INCREASE_MANUAL;
import static antifraud.enums.LimitOperation.INCREASE_BOTH;
import static java.lang.Math.ceil;

@Service
@RequiredArgsConstructor
@Log4j2
public class LimitService {

    private final static double INITIAL_MAX_ALLOWED_AUTOMATED_AMOUNT = 200L;
    private final static double INITIAL_MAX_ALLOWED_MANUAL_AMOUNT = 1500L;

    private final LimitsConfigRepository configRepository;
    private final CardLimitsRepository cardLimitsRepository;

    @PostConstruct
    @Transactional
    public void init() {

        final var limits = List.of(
                LimitsConfig.builder().validity(ALLOWED).feedback(ALLOWED).operation(LimitOperation.EXCEPTION).build(),
                LimitsConfig.builder().validity(ALLOWED).feedback(MANUAL_PROCESSING).operation(DECREASE_ALLOWED).build(),
                LimitsConfig.builder().validity(ALLOWED).feedback(PROHIBITED).operation(DECREASE_BOTH).build(),

                LimitsConfig.builder().validity(MANUAL_PROCESSING).feedback(ALLOWED).operation(INCREASE_ALLOWED).build(),
                LimitsConfig.builder().validity(MANUAL_PROCESSING).feedback(MANUAL_PROCESSING).operation(EXCEPTION).build(),
                LimitsConfig.builder().validity(MANUAL_PROCESSING).feedback(PROHIBITED).operation(DECREASE_MANUAL).build(),

                LimitsConfig.builder().validity(PROHIBITED).feedback(ALLOWED).operation(INCREASE_BOTH).build(),
                LimitsConfig.builder().validity(PROHIBITED).feedback(MANUAL_PROCESSING).operation(INCREASE_MANUAL).build(),
                LimitsConfig.builder().validity(PROHIBITED).feedback(PROHIBITED).operation(EXCEPTION).build()
        );

        limits.forEach(this::saveConfigIfNotExists);
    }

    private void saveConfigIfNotExists(LimitsConfig limitsConfig) {
        configRepository.findByValidityAndFeedback(limitsConfig.getValidity(), limitsConfig.getFeedback()).orElseGet(
                () -> configRepository.save(limitsConfig));
    }

    @Transactional
    public CardLimits getCardLimits(String number) {
        return cardLimitsRepository.getByNumber(number).orElseGet(() ->
                cardLimitsRepository.save(CardLimits.builder()
                        .limitAllowed(INITIAL_MAX_ALLOWED_AUTOMATED_AMOUNT)
                        .limitManual(INITIAL_MAX_ALLOWED_MANUAL_AMOUNT)
                        .number(number)
                        .build()));
    }

    @Transactional
    public TransactionValidationResult validateLimits(long amount, CardLimits cardLimits) {
        if (cardLimits.getLimitAllowed() >= amount) {
            return TransactionValidationResult.ALLOWED;
        } else if (cardLimits.getLimitManual() >= amount) {
            return TransactionValidationResult.MANUAL_PROCESSING;
        } else {
            return TransactionValidationResult.PROHIBITED;
        }
    }

    void updateLimits(Transaction transaction) {

        final var limitConfig = configRepository.findByValidityAndFeedback(transaction.getValidationResult(), transaction.getFeedback().getValidationResult())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Limits configuration missing for " + transaction.getValidationResult().getName() + ", " + transaction.getFeedback().getValidationResult().getName()));

        log.info("Updating limits for " +
                "Validity:" + limitConfig.getValidity().getName() + ", " +
                "Feedback: " + limitConfig.getFeedback().getName() + ", " +
                "Operation: " + limitConfig.getOperation().name());

        log.info("Previous limits: Automated: " + transaction.getLimits().getLimitAllowed() + ", Manual: " + transaction.getLimits().getLimitManual());

        switch (limitConfig.getOperation()) {
            case EXCEPTION:
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cannot update limits");
            case INCREASE_ALLOWED:
                transaction.getLimits().setLimitAllowed(modLimit(transaction.getLimits().getLimitAllowed(), transaction.getAmount(), 1.0));
                break;
            case INCREASE_MANUAL:
                transaction.getLimits().setLimitManual(modLimit(transaction.getLimits().getLimitManual(), transaction.getAmount(), 1.0));
                break;
            case INCREASE_BOTH:
                transaction.getLimits().setLimitAllowed(modLimit(transaction.getLimits().getLimitAllowed(), transaction.getAmount(), 1.0));
                transaction.getLimits().setLimitManual(modLimit(transaction.getLimits().getLimitManual(), transaction.getAmount(), 1.0));
                break;
            case DECREASE_ALLOWED:
                transaction.getLimits().setLimitAllowed(modLimit(transaction.getLimits().getLimitAllowed(), transaction.getAmount(), -1.0));
                break;
            case DECREASE_MANUAL:
                transaction.getLimits().setLimitManual(modLimit(transaction.getLimits().getLimitManual(), transaction.getAmount(), -1.0));
                break;
            case DECREASE_BOTH:
                transaction.getLimits().setLimitAllowed(modLimit(transaction.getLimits().getLimitAllowed(), transaction.getAmount(), -1.0));
                transaction.getLimits().setLimitManual(modLimit(transaction.getLimits().getLimitManual(), transaction.getAmount(), -1.0));
                break;
        }
        log.info("Updated limits: Automated: " + transaction.getLimits().getLimitAllowed() + ", Manual: " + transaction.getLimits().getLimitManual());
    }

    private static double modLimit(double limit, double amount, double sign) {
        return ceil(0.8 * limit + sign * 0.2 * amount);
    }

}
