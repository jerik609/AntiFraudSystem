package antifraud.services;

import antifraud.enums.TransactionValidationResult;
import antifraud.model.Transaction;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class LimitService {

    // TODO: maybe synchronized would be too much of a bottleneck - this is on critical path
    //   maybe we can afford a certain level of "fuzziness" when validating
    //   or at least a read/write lock

    // https://stackoverflow.com/questions/1312259/what-is-the-re-entrant-lock-and-concept-in-general
    // https://www.google.cz/search?q=how+to+use+reentrantreadwritelock+java&rls=com.microsoft:cs&ie=UTF-8&oe=UTF-8&startIndex=&startPage=1

    ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    private final static long INITIAL_LIMIT_MAX_ALLOWED_AUTOMATED_AMOUNT = 200L;
    private final static long INITIAL_LIMIT_MAX_ALLOWED_MANUAL_AMOUNT = 1500L;

    private long limitMaxAllowedAutomatedAmount = INITIAL_LIMIT_MAX_ALLOWED_AUTOMATED_AMOUNT;
    private long limitMaxAllowedManualAmount = INITIAL_LIMIT_MAX_ALLOWED_MANUAL_AMOUNT;

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

    synchronized void updateLimits(Transaction transaction) {
        lock.writeLock().lock();
        try {
            System.out.println("calling limits for transaction ID: " + transaction.getId());
        } finally {
            lock.writeLock().unlock();
        }
    }

}
