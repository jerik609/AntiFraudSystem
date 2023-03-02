package antifraud.services;

import antifraud.enums.LimitOperation;
import antifraud.enums.TransactionValidationResult;
import antifraud.model.LimitsConfig;
import antifraud.model.Transaction;
import antifraud.repository.LimitsConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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

    // TODO: maybe synchronized would be too much of a bottleneck - this is on critical path
    //   maybe we can afford a certain level of "fuzziness" when validating
    //   or at least a read/write lock

    // https://stackoverflow.com/questions/1312259/what-is-the-re-entrant-lock-and-concept-in-general
    // https://www.google.cz/search?q=how+to+use+reentrantreadwritelock+java&rls=com.microsoft:cs&ie=UTF-8&oe=UTF-8&startIndex=&startPage=1

    ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    private final static double INITIAL_LIMIT_MAX_ALLOWED_AUTOMATED_AMOUNT = 200L;
    private final static double INITIAL_LIMIT_MAX_ALLOWED_MANUAL_AMOUNT = 1500L;

    private double limitMaxAllowedAutomatedAmount = INITIAL_LIMIT_MAX_ALLOWED_AUTOMATED_AMOUNT;
    private double limitMaxAllowedManualAmount = INITIAL_LIMIT_MAX_ALLOWED_MANUAL_AMOUNT;

    private final LimitsConfigRepository configRepository;

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

        limits.forEach(this::saveIfNotExists);
    }

    private void saveIfNotExists(LimitsConfig limitsConfig) {
        configRepository.findByValidityAndFeedback(limitsConfig.getValidity(), limitsConfig.getFeedback()).orElseGet(
                () -> configRepository.save(limitsConfig));
    }

    synchronized public TransactionValidationResult validate(long amount) {
        lock.readLock().lock();



        try {
            if (limitMaxAllowedAutomatedAmount >= amount) {
                return TransactionValidationResult.ALLOWED;
            } else if (limitMaxAllowedManualAmount >= amount) {
                return TransactionValidationResult.MANUAL_PROCESSING;
            } else {
                return TransactionValidationResult.PROHIBITED;
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    private static double modLimit(double limit, double amount, double sign) {
        return ceil(0.8 * limit + sign * 0.2 * amount);
    }

    synchronized void updateLimits(Transaction transaction) {
        lock.writeLock().lock();
        try {
            final var limitConfig = configRepository.findByValidityAndFeedback(transaction.getValidationResult(), transaction.getFeedback().getValidationResult())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Limits configuration missing for " + transaction.getValidationResult().getName() + ", " + transaction.getFeedback().getValidationResult().getName()));

            log.info("Updating limits for " +
                    "Validity:" + limitConfig.getValidity().getName() + ", " +
                    "Feedback: " + limitConfig.getFeedback().getName() + ", " +
                    "Operation: " + limitConfig.getOperation().name());

            log.info("Previous limits: Automated: " + limitMaxAllowedAutomatedAmount + ", Manual: " + limitMaxAllowedManualAmount);

            switch(limitConfig.getOperation()) {
                case EXCEPTION:
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cannot update limits");
                case INCREASE_ALLOWED:
                    limitMaxAllowedAutomatedAmount = modLimit(limitMaxAllowedAutomatedAmount, transaction.getAmount(), 1.0);
                    break;
                case INCREASE_MANUAL:
                    limitMaxAllowedManualAmount = modLimit(limitMaxAllowedManualAmount, transaction.getAmount(), 1.0);
                    break;
                case INCREASE_BOTH:
                    limitMaxAllowedAutomatedAmount = modLimit(limitMaxAllowedAutomatedAmount, transaction.getAmount(), 1.0);
                    limitMaxAllowedManualAmount = modLimit(limitMaxAllowedManualAmount, transaction.getAmount(), 1.0);
                    break;
                case DECREASE_ALLOWED:
                    limitMaxAllowedAutomatedAmount = modLimit(limitMaxAllowedAutomatedAmount, transaction.getAmount(), -1.0);
                    break;
                case DECREASE_MANUAL:
                    limitMaxAllowedManualAmount = modLimit(limitMaxAllowedManualAmount, transaction.getAmount(), -1.0);
                    break;
                case DECREASE_BOTH:
                    limitMaxAllowedAutomatedAmount = modLimit(limitMaxAllowedAutomatedAmount, transaction.getAmount(), -1.0);
                    limitMaxAllowedManualAmount = modLimit(limitMaxAllowedManualAmount, transaction.getAmount(), -1.0);
                    break;
            }

            log.info("Updated limits: Automated: " + limitMaxAllowedAutomatedAmount + ", Manual: " + limitMaxAllowedManualAmount);

        } finally {
            lock.writeLock().unlock();
        }
    }

}
