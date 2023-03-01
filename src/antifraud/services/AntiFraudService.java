package antifraud.services;

import antifraud.dto.request.TransactionEntryRequest;
import antifraud.dto.validation.TransactionAmountValidator;
import antifraud.enums.RegionType;
import antifraud.enums.TransactionValidationResult;
import antifraud.model.Region;
import antifraud.model.StolenCard;
import antifraud.model.SuspiciousIp;
import antifraud.model.Transaction;
import antifraud.repository.RegionRepository;
import antifraud.repository.StolenCardRepository;
import antifraud.repository.SuspiciousIpRepository;
import antifraud.repository.TransactionRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

@Service
@Log4j2
public class AntiFraudService {

    private final TransactionRepository transactionRepository;
    private final SuspiciousIpRepository suspiciousIpRepository;
    private final StolenCardRepository stolenCardRepository;
    private final RegionRepository regionRepository;

    @Autowired
    AntiFraudService(
            TransactionRepository transactionRepository,
            SuspiciousIpRepository suspiciousIpRepository,
            StolenCardRepository stolenCardRepository,
            RegionRepository regionRepository) {
        this.transactionRepository = transactionRepository;
        this.suspiciousIpRepository = suspiciousIpRepository;
        this.stolenCardRepository = stolenCardRepository;
        this.regionRepository = regionRepository;
    }

    @Transactional
    public void enterTransaction(TransactionEntryRequest transactionEntryRequest, TreeMap<String, TransactionValidationResult> validationResult) {

        RegionType regionType;
        try {
            regionType = RegionType.valueOf(transactionEntryRequest.getRegion());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown region: " + transactionEntryRequest.getRegion());
        }

        final var localDateTime = LocalDateTime.parse(transactionEntryRequest.getDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        final var timestamp = Date.from(localDateTime.toInstant(ZoneOffset.UTC));

        final var region = regionRepository.findByRegionType(regionType).orElse(Region.builder().regionType(regionType).build());

        // amount validation
        final var amountValidation = TransactionAmountValidator.validate(transactionEntryRequest.getAmount());
        if (!amountValidation.equals(TransactionValidationResult.ALLOWED)) {
            validationResult.put("amount", amountValidation);
        }

        // IP validation
        if (suspiciousIpRepository.findByIp(transactionEntryRequest.getIp()).isPresent()) {
            validationResult.put("ip", TransactionValidationResult.PROHIBITED);
        }

        // card number
        if (stolenCardRepository.findByNumber(transactionEntryRequest.getNumber()).isPresent()) {
            validationResult.put("card-number", TransactionValidationResult.PROHIBITED);
        }

        final var transaction = Transaction.builder()
                .amount(transactionEntryRequest.getAmount())
                .ip(transactionEntryRequest.getIp())
                .number(transactionEntryRequest.getNumber())
                .region(region)
                .date(timestamp)
                .owner(SecurityContextHolder.getContext().getAuthentication().getName())
                .validationResult(validationResult.isEmpty() ? TransactionValidationResult.ALLOWED : validationResult.lastEntry().getValue())
                .build();

        transactionRepository.save(transaction);
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
