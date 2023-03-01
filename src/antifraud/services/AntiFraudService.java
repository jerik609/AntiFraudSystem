package antifraud.services;

import antifraud.dto.validation.TransactionAmountValidator;
import antifraud.enums.TransactionValidationResult;
import antifraud.model.StolenCard;
import antifraud.model.SuspiciousIp;
import antifraud.model.Transaction;
import antifraud.repository.StolenCardRepository;
import antifraud.repository.SuspiciousIpRepository;
import antifraud.repository.TransactionRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

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

    @Transactional
    public void enterTransaction(Transaction transaction, TreeMap<String, TransactionValidationResult> validationResult) {

        // amount validation
        final var amountValidation = TransactionAmountValidator.validate(transaction.getAmount());
        if (!amountValidation.equals(TransactionValidationResult.ALLOWED)) {
            validationResult.put("amount", amountValidation);
        }

        // IP validation
        if (suspiciousIpRepository.findByIp(transaction.getIp()).isPresent()) {
            validationResult.put("ip", TransactionValidationResult.PROHIBITED);
        }

        // card number
        if (stolenCardRepository.findByNumber(transaction.getNumber()).isPresent()) {
            validationResult.put("card-number", TransactionValidationResult.PROHIBITED);
        }

        if (validationResult.isEmpty()) {
            transactionRepository.save(transaction);
        }
    }

    public SuspiciousIp enterSuspiciousIp(SuspiciousIp suspiciousIp) {
        return suspiciousIpRepository.save(suspiciousIp);
    }

    @Transactional
    public void deleteSuspiciousIp(String ip) {
        final var suspiciousIp = suspiciousIpRepository.findByIp(ip).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "IP " + ip + " not found in the database."));

        suspiciousIpRepository.delete(suspiciousIp);
    }

    public List<SuspiciousIp> getAllSuspiciousIps() {
        return suspiciousIpRepository.findAll(Sort.by("id").ascending());
    }

    public StolenCard enterStolenCard(StolenCard stolenCard) {
        return stolenCardRepository.save(stolenCard);
    }

    @Transactional
    public void deleteStolenCard(String number) {
        final var stolenCard = stolenCardRepository.findByNumber(number).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Card number " + number + " not found in the database."));

        stolenCardRepository.delete(stolenCard);
    }

    public List<StolenCard> getAllStolenCards() {
        return stolenCardRepository.findAll(Sort.by("id").ascending());
    }

}
