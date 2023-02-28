package antifraud.resources;

import antifraud.dto.request.StolenCardEntryRequest;
import antifraud.dto.request.SuspiciousIpEntryRequest;
import antifraud.dto.response.AntifraudActionResponse;
import antifraud.dto.request.TransactionEntryRequest;
import antifraud.dto.validation.TransactionAmountValidator;
import antifraud.model.StolenCard;
import antifraud.model.SuspiciousIp;
import antifraud.model.Transaction;
import antifraud.services.AntiFraudService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/antifraud")
@RequiredArgsConstructor
public class AntiFraudResource {

    private final AntiFraudService service;

    @PostMapping("/transaction")
    public ResponseEntity<AntifraudActionResponse> postTransaction(@RequestBody @Valid TransactionEntryRequest transactionEntryRequest) {

        final var amountValidationResult = TransactionAmountValidator.validate(transactionEntryRequest.getAmount());

        // validate transaction amount
        if (!amountValidationResult.equals(TransactionAmountValidator.VerificationResult.ALLOWED)) {
            return new ResponseEntity<>(AntifraudActionResponse.builder()
                    .amount(transactionEntryRequest.getAmount())
                    .ip(transactionEntryRequest.getIp())
                    .number(transactionEntryRequest.getNumber())
                    .result(amountValidationResult.getName())
                    .build(),
                    HttpStatus.OK);
        }

        final var transaction = Transaction.builder()
                .owner(SecurityContextHolder.getContext().getAuthentication().getName())
                .amount(transactionEntryRequest.getAmount())
                .build();

        final var enteredTransaction = service.enterTransaction(transaction);

        return new ResponseEntity<>(AntifraudActionResponse.builder()
                .id(enteredTransaction.getId())
                .result(amountValidationResult.getName())
                .build(),
                HttpStatus.OK);
    }

    @PostMapping("/suspicious-ip")
    public ResponseEntity<AntifraudActionResponse> postSuspiciousIp(@RequestBody @Valid SuspiciousIpEntryRequest suspiciousIpEntryRequest) {

        SuspiciousIp enteredSuspiciousIp;
        try {
            enteredSuspiciousIp = service.enterSuspiciousIp(SuspiciousIp.builder()
                    .ip(suspiciousIpEntryRequest.getIp())
                    .build());
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Suspicious IP " + suspiciousIpEntryRequest.getIp() + " already registered.");
        }

        return new ResponseEntity<>(AntifraudActionResponse.builder()
                .id(enteredSuspiciousIp.getId())
                .ip(enteredSuspiciousIp.getIp())
                .build(),
                HttpStatus.OK);
    }

    @PostMapping("/stolencard")
    public ResponseEntity<AntifraudActionResponse> postStolenCard(@RequestBody @Valid StolenCardEntryRequest stolenCardEntryRequest) {

        StolenCard enteredStolenCard;
        try {
            enteredStolenCard = service.enterStolenCard(StolenCard.builder()
                    .number(stolenCardEntryRequest.getNumber())
                    .build());
        } catch (DataIntegrityViolationException exception){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Stolen card " + stolenCardEntryRequest.getNumber() + " already registered.");
        }

        return new ResponseEntity<>(AntifraudActionResponse.builder()
                .id(enteredStolenCard.getId())
                .number(enteredStolenCard.getNumber())
                .build(),
                HttpStatus.OK);
    }



}
