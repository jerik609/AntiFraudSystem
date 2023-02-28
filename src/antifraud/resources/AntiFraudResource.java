package antifraud.resources;

import antifraud.dto.request.StolenCardEntryRequest;
import antifraud.dto.request.SuspiciousIpEntryRequest;
import antifraud.dto.response.AntifraudActionResponse;
import antifraud.dto.request.TransactionEntryRequest;
import antifraud.dto.validation.IpConstraint;
import antifraud.dto.validation.IpValidator;
import antifraud.dto.validation.TransactionAmountValidator;
import antifraud.model.StolenCard;
import antifraud.model.SuspiciousIp;
import antifraud.model.Transaction;
import antifraud.services.AntiFraudService;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.LuhnCheck;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<AntifraudActionResponse> deleteStolenCard(@PathVariable @LuhnCheck String number) {

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
