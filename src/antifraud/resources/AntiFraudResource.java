package antifraud.resources;

import antifraud.dto.TransactionEntryResponse;
import antifraud.dto.TransactionEntryRequest;
import antifraud.service.AntiFraudService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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

    @ControllerAdvice
    private static class AntiFraudResourceExceptionHandler extends ResponseEntityExceptionHandler {

        // own exception handler, will need this later, so leaving commented code here
//        @ExceptionHandler(InvalidTransactionAmountException.class)
//        public ResponseEntity<Result> handleRuntimeException(InvalidTransactionAmountException e, WebRequest request) {
//            return new ResponseEntity<>(new Result("Error: " + e + " while processing request: " + request), HttpStatus.BAD_REQUEST);
//        }

        @Override
        public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
            return new ResponseEntity<>(new TransactionEntryResponse("Exception: " + ex), HttpStatus.BAD_REQUEST);
        }



    }

}
