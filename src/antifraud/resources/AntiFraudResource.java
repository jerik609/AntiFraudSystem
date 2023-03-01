package antifraud.resources;

import antifraud.dto.request.StolenCardEntryRequest;
import antifraud.dto.request.SuspiciousIpEntryRequest;
import antifraud.dto.response.AntifraudActionResponse;
import antifraud.dto.request.TransactionEntryRequest;
import antifraud.dto.validation.IpValidator;
import antifraud.enums.TransactionValidationResult;
import antifraud.model.StolenCard;
import antifraud.model.SuspiciousIp;
import antifraud.model.Transaction;
import antifraud.services.AntiFraudService;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.cfg.defs.CreditCardNumberDef;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.LuhnCheck;
import org.hibernate.validator.internal.constraintvalidators.hv.LuhnCheckValidator;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorFactoryImpl;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.Payload;
import javax.validation.Valid;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/antifraud")
@RequiredArgsConstructor
public class AntiFraudResource {

    private final AntiFraudService service;

    private final LuhnCheckValidator luhnCheckValidator;

    @PostMapping("/transaction")
    public ResponseEntity<AntifraudActionResponse> postTransaction(@RequestBody @Valid TransactionEntryRequest transactionEntryRequest) {

        final var validationResult = new TreeMap<String, TransactionValidationResult>();

        service.enterTransaction(Transaction.builder()
                .amount(transactionEntryRequest.getAmount())
                .ip(transactionEntryRequest.getIp())
                .number(transactionEntryRequest.getNumber())
                .owner(SecurityContextHolder.getContext().getAuthentication().getName())
                .build(),
                validationResult);

        if (validationResult.isEmpty()) {
            return new ResponseEntity<>(AntifraudActionResponse.builder()
                    .result(TransactionValidationResult.ALLOWED.getName())
                    .info("none")
                    .build(),
                    HttpStatus.OK);
        } else {
            final var lastResult = validationResult.lastEntry().getValue();
            return new ResponseEntity<>(AntifraudActionResponse.builder()
                    .result(lastResult.getName())
                    .info(validationResult.entrySet().stream()
                            .filter(entry -> entry.getValue().equals(lastResult))
                            .map(Map.Entry::getKey)
                            .collect(Collectors.joining(", ")))
                    .build(),
                    HttpStatus.OK);
        }
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

    @DeleteMapping("/suspicious-ip/{ip}")
    public ResponseEntity<AntifraudActionResponse> deleteSuspiciousIp(@PathVariable String ip) {

        if (!IpValidator.isValid(ip)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid IP address:" + ip);
        }

        service.deleteSuspiciousIp(ip);

        return new ResponseEntity<>(AntifraudActionResponse.builder()
                .status("IP " + ip + " successfully removed!")
                .build(),
                HttpStatus.OK);
    }

    @GetMapping("/suspicious-ip")
    public ResponseEntity<List<AntifraudActionResponse>> getSuspiciousIps() {

        List<SuspiciousIp> suspiciousIps = service.getAllSuspiciousIps();

        return new ResponseEntity<>(
                suspiciousIps.stream().map(
                        entry -> AntifraudActionResponse.builder()
                                .id(entry.getId())
                                .ip(entry.getIp())
                                .build()).collect(Collectors.toList()),
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

    @DeleteMapping("/stolencard/{number}")
    public ResponseEntity<AntifraudActionResponse> deleteStolenCard(@PathVariable String number) {

        if (!luhnCheckValidator.isValid(number, null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid credit card number");
        }

        service.deleteStolenCard(number);

        return new ResponseEntity<>(AntifraudActionResponse.builder()
                .status("Card " + number + " successfully removed!")
                .build(),
                HttpStatus.OK);
    }

    @GetMapping("/stolencard")
    public ResponseEntity<List<AntifraudActionResponse>> getStolenCards() {

        List<StolenCard> stolenCards = service.getAllStolenCards();

        return new ResponseEntity<>(
                stolenCards.stream().map(
                        entry -> AntifraudActionResponse.builder()
                                .id(entry.getId())
                                .number(entry.getNumber())
                                .build()).collect(Collectors.toList()),
                HttpStatus.OK);
    }

}
