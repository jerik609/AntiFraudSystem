package antifraud.services;

import antifraud.model.StolenCard;
import antifraud.model.SuspiciousIp;
import antifraud.model.Transaction;
import antifraud.repository.StolenCardRepository;
import antifraud.repository.SuspiciousIpRepository;
import antifraud.repository.TransactionRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class AntiFraudService {

    private final TransactionRepository transactionRepository;

    private final SuspiciousIpRepository suspiciousIpRepository;

    private final StolenCardRepository stolenCardRepository;

    @Autowired
    AntiFraudService(TransactionRepository transactionRepository, SuspiciousIpRepository suspiciousIpRepository, StolenCardRepository stolenCardRepository) {
        this.transactionRepository = transactionRepository;
        this.suspiciousIpRepository = suspiciousIpRepository;
        this.stolenCardRepository = stolenCardRepository;
    }

    public Transaction enterTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public SuspiciousIp enterSuspiciousIp(SuspiciousIp suspiciousIp) {
        return suspiciousIpRepository.save(suspiciousIp);
    }

    public StolenCard enterStolenCard(StolenCard stolenCard) {
        return stolenCardRepository.save(stolenCard);
    }
}
