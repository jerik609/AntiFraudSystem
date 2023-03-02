package antifraud.services;

import antifraud.dto.request.TransactionEntryRequest;
import antifraud.dto.request.TransactionFeedbackRequest;
import antifraud.dto.response.AntifraudActionResponse;
import antifraud.enums.RegionType;
import antifraud.enums.TransactionValidationResult;
import antifraud.model.*;
import antifraud.repository.RegionRepository;
import antifraud.repository.StolenCardRepository;
import antifraud.repository.SuspiciousIpRepository;
import antifraud.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class AntiFraudService {

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private final TransactionRepository transactionRepository;
    private final SuspiciousIpRepository suspiciousIpRepository;
    private final StolenCardRepository stolenCardRepository;
    private final RegionRepository regionRepository;
    private final LimitService limitService;

    @Transactional
    public void enterTransaction(TransactionEntryRequest transactionEntryRequest, TreeMap<String, TransactionValidationResult> validationResult) {

        RegionType regionType;
        try {
            regionType = RegionType.valueOf(transactionEntryRequest.getRegion());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown region: " + transactionEntryRequest.getRegion());
        }

        final var localDateTime = LocalDateTime.parse(transactionEntryRequest.getDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        final var timestamp = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        final var region = regionRepository.findByRegionType(regionType)
                .orElseGet(() -> regionRepository.save(Region.builder().regionType(regionType).build()));

        // amount validation
        final var cardLimits = limitService.getCardLimits(transactionEntryRequest.getNumber());
        final var amountValidation = limitService.validateLimits(transactionEntryRequest.getAmount(), cardLimits);
        if (!amountValidation.equals(TransactionValidationResult.ALLOWED)) {
            validationResult.put("amount", amountValidation);
        }

        // blocked IP validation
        if (suspiciousIpRepository.findByIp(transactionEntryRequest.getIp()).isPresent()) {
            validationResult.put("ip", TransactionValidationResult.PROHIBITED);
        }

        // stolen card number validation
        if (stolenCardRepository.findByNumber(transactionEntryRequest.getNumber()).isPresent()) {
            validationResult.put("card-number", TransactionValidationResult.PROHIBITED);
        }

        // regions validation
        final var numberOfDistinctRegions = transactionRepository.countDistinctRegionsForCreditCardAndNotRegionAndWithinPeriod(
                transactionEntryRequest.getNumber(),
                region,
                Date.from(localDateTime.minusHours(1).atZone(ZoneId.systemDefault()).toInstant()),
                timestamp
        );

        if (numberOfDistinctRegions == 2) {
            validationResult.put("region-correlation", TransactionValidationResult.MANUAL_PROCESSING);
        } else if (numberOfDistinctRegions > 2) {
            validationResult.put("region-correlation", TransactionValidationResult.PROHIBITED);
        }

        // IP-correlation validation
        final var numberOfDistinctIps = transactionRepository.countDistinctIpsForCreditCardAndNotIpAndWithinPeriod(
                transactionEntryRequest.getNumber(),
                transactionEntryRequest.getIp(),
                Date.from(localDateTime.minusHours(1).atZone(ZoneId.systemDefault()).toInstant()),
                timestamp
        );

        if (numberOfDistinctIps == 2) {
            validationResult.put("ip-correlation", TransactionValidationResult.MANUAL_PROCESSING);
        } else if (numberOfDistinctIps > 2) {
            validationResult.put("ip-correlation", TransactionValidationResult.PROHIBITED);
        }

        // print validation result
        log.info("validationResult = {}", validationResult);

        // create transaction and save it
        final var transaction = Transaction.builder()
                .amount(transactionEntryRequest.getAmount())
                .ip(transactionEntryRequest.getIp())
                .number(transactionEntryRequest.getNumber())
                .limits(cardLimits)
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

    @Transactional
    public AntifraudActionResponse applyTransactionFeedback(TransactionFeedbackRequest transactionFeedbackRequest) {

        TransactionValidationResult validationFeedback;
        try {
            validationFeedback = TransactionValidationResult.valueOf(transactionFeedbackRequest.getFeedback());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown feedback type: " + transactionFeedbackRequest.getFeedback());
        }

        // transaction does not exists
        Transaction transaction = transactionRepository.findById(transactionFeedbackRequest.getTransactionId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction with id " + transactionFeedbackRequest.getTransactionId() + " not found."));

        // transaction already has feedback
        if (transaction.getFeedback() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Feedback for transaction id " + transactionFeedbackRequest.getTransactionId() + " already exists.");
        }

        // create feedback
        transaction.addFeedback(Feedback.builder().validationResult(validationFeedback).build());

        // update limits
        limitService.updateLimits(transaction);

        // persist transaction updates
        final var savedTransaction = transactionRepository.save(transaction);

        return AntifraudActionResponse.builder()
                .transactionId(savedTransaction.getId())
                .amount(savedTransaction.getAmount())
                .ip(savedTransaction.getIp())
                .number(savedTransaction.getNumber())
                .region(savedTransaction.getRegion().getRegionType().name())
                .date(DATE_FORMAT.format(savedTransaction.getDate()))
                .result(savedTransaction.getValidationResult().getName())
                .feedback(savedTransaction.getFeedback().getValidationResult().getName())
                .build();
    }

    @Transactional
    public List<AntifraudActionResponse> getTransactionHistory() {

        return transactionRepository.findAll(Sort.by("id").ascending()).stream()
                .map(transaction -> AntifraudActionResponse.builder()
                        .transactionId(transaction.getId())
                        .amount(transaction.getAmount())
                        .ip(transaction.getIp())
                        .number(transaction.getNumber())
                        .region(transaction.getRegion().getRegionType().name())
                        .date(DATE_FORMAT.format(transaction.getDate()))
                        .result(transaction.getValidationResult().getName())
                        .feedback(transaction.getFeedback() == null ? "" : transaction.getFeedback().getValidationResult().getName())
                        .build())
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional
    public List<AntifraudActionResponse> getTransactionHistoryByCardNumber(String number) {

        final var dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        return transactionRepository.findByNumberOrderByIdAsc(number).stream()
                .map(transaction -> AntifraudActionResponse.builder()
                        .transactionId(transaction.getId())
                        .amount(transaction.getAmount())
                        .ip(transaction.getIp())
                        .number(transaction.getNumber())
                        .region(transaction.getRegion().getRegionType().name())
                        .date(dateFormat.format(transaction.getDate()))
                        .result(transaction.getValidationResult().getName())
                        .feedback(transaction.getFeedback() == null ? "" : transaction.getFeedback().getValidationResult().getName())
                        .build())
                .collect(Collectors.toUnmodifiableList());
    }
}
