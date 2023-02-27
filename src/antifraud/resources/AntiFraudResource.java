package antifraud.resources;

import antifraud.dto.response.TransactionActionResponse;
import antifraud.dto.request.TransactionEntryRequest;
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
    public ResponseEntity<TransactionActionResponse> postTransaction(@RequestBody @Valid TransactionEntryRequest transactionEntryRequest) {
        final var result = service.verifyTransaction(transactionEntryRequest);
        return new ResponseEntity<>(TransactionActionResponse.builder().result(result.getName()).build(), HttpStatus.OK);
    }

}
