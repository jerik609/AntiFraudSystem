package antifraud.resources;

import antifraud.dto.TransactionEntryResponse;
import antifraud.dto.TransactionEntryRequest;
import antifraud.service.AntiFraudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/antifraud")
@RequiredArgsConstructor
public class AntiFraudResource {

    private final AntiFraudService service;

    @PostMapping("/transaction")
    public ResponseEntity<TransactionEntryResponse> postTransaction(@RequestBody @Valid TransactionEntryRequest transactionEntryRequest) {
        final var result = service.verifyTransaction(transactionEntryRequest);
        return new ResponseEntity<>(new TransactionEntryResponse(result.getName()), HttpStatus.OK);
    }

}
